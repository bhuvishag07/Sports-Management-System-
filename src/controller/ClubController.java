package controller;

import model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Controller connecting Models and Views (MVC Architecture).
 * Holds and manages the required Java Collections:
 * - Array: Fixed training slots and sport categories
 * - ArrayList: Members, Coaches, Sports
 * - LinkedList: Training History, Payment History, Training Schedule
 * - HashMap: Member ID -> Member
 * - TreeMap: Members organized by Membership Expiry Date
 * - HashSet: Unique sports enrolled per member (inside Member class)
 */
public class ClubController {

    private static final String DEFAULT_DATA_FILE = "data/sports_club_data.dat";
    private final File dataFile;

    // Required Collections:
    // 1. ArrayLists
    private ArrayList<Member> members;
    private ArrayList<Coach> coaches;
    private ArrayList<Sport> sports;

    // 2. LinkedLists
    private LinkedList<Training> trainingSchedule;
    private LinkedList<Training> trainingHistory;
    private LinkedList<Payment> paymentHistory;

    // 3. HashMap for O(1) Member lookup
    private HashMap<String, Member> memberMap;

    // 4. TreeMap for organizing members by membership expiry date
    private TreeMap<LocalDate, ArrayList<Member>> membersByExpiryMap;

    public ClubController() {
        this(new File(DEFAULT_DATA_FILE));
    }

    public ClubController(File dataFile) {
        if (dataFile == null) {
            throw new IllegalArgumentException("Data file cannot be null.");
        }
        this.dataFile = dataFile;
        this.members = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.sports = new ArrayList<>();
        this.trainingSchedule = new LinkedList<>();
        this.trainingHistory = new LinkedList<>();
        this.paymentHistory = new LinkedList<>();
        this.memberMap = new HashMap<>();
        this.membersByExpiryMap = new TreeMap<>();

        // Try loading existing data file, or populate sample data if none exists
        if (!loadFromFile(this.dataFile)) {
            loadSampleData();
            saveToFile(this.dataFile);
        }
    }

    private String normalizeMemberId(String memberId) {
        return memberId.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isScheduled(Training training) {
        return Training.STATUS_SCHEDULED.equalsIgnoreCase(training.getStatus());
    }

    // ==========================================
    // REBUILD INDEXES (HashMap & TreeMap)
    // ==========================================
    private void rebuildIndexes() {
        memberMap.clear();
        membersByExpiryMap.clear();

        for (Member m : members) {
            memberMap.put(normalizeMemberId(m.getId()), m);

            LocalDate expiry = m.getMembershipExpiryDate();
            if (expiry != null) {
                membersByExpiryMap.computeIfAbsent(expiry, k -> new ArrayList<>()).add(m);
            }
        }
    }

    // ==========================================
    // VALIDATION HELPERS
    // ==========================================
    public void validateMember(Member member, boolean isNew) throws InvalidMemberException {
        if (member == null) {
            throw new InvalidMemberException("Member object cannot be null.");
        }
        if (member.getId() == null || member.getId().trim().isEmpty()) {
            throw new InvalidMemberException("Member ID is required.");
        }
        if (isNew && memberMap.containsKey(normalizeMemberId(member.getId()))) {
            throw new InvalidMemberException("Member ID '" + member.getId().trim() + "' already exists!");
        }
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new InvalidMemberException("Member Name cannot be empty.");
        }
        if (member.getAge() < 5 || member.getAge() > 100) {
            throw new InvalidMemberException("Member Age must be between 5 and 100.");
        }
        if (member.getContact() == null || !member.getContact().matches("^[0-9+()\\-\\s]{7,15}$")) {
            throw new InvalidMemberException("Valid contact number (7-15 digits) is required.");
        }
        if (member.getEmail() == null || !member.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidMemberException("Valid email address is required (e.g. user@example.com).");
        }
        if (member.getMembership() == null) {
            throw new InvalidMemberException("Member must be assigned a membership plan.");
        }
        if (!Membership.isValidPlan(member.getMembership().getPlanName())) {
            throw new InvalidMemberException("Member must have a valid membership plan.");
        }
        if (member.getMembership().getStartDate() == null || member.getMembership().getExpiryDate() == null) {
            throw new InvalidMemberException("Membership start date and expiry date are required.");
        }
        if (member.getMembership().getExpiryDate().isBefore(member.getMembership().getStartDate())) {
            throw new InvalidMemberException("Membership expiry date cannot be before the start date.");
        }
        if (!Double.isFinite(member.getMembership().getPrice()) || member.getMembership().getPrice() <= 0
                || member.getMembership().getDurationMonths() <= 0) {
            throw new InvalidMemberException("Membership price and duration must be greater than zero.");
        }
    }

    public void validateTraining(Training training) throws InvalidTrainingException {
        if (training == null) {
            throw new InvalidTrainingException("Training session cannot be null.");
        }
        if (training.getTrainingId() == null || training.getTrainingId().trim().isEmpty()) {
            throw new InvalidTrainingException("Training ID is required.");
        }
        if (training.getSportName() == null || training.getSportName().trim().isEmpty()) {
            throw new InvalidTrainingException("Sport must be selected.");
        }
        if (training.getCoachName() == null || training.getCoachName().trim().isEmpty()) {
            throw new InvalidTrainingException("Coach must be selected.");
        }
        if (training.getDate() == null) {
            throw new InvalidTrainingException("Training Date is required.");
        }
        if (training.getDate().isBefore(LocalDate.now())) {
            throw new InvalidTrainingException("Training date cannot be in the past.");
        }
        // Validating slot against Array requirement
        if (!Training.isValidSlot(training.getTrainingSlot())) {
            throw new InvalidTrainingException("Invalid training slot! Must select a valid slot from the schedule array.");
        }
        Member member = getMemberById(training.getMemberId());
        if (member == null) {
            throw new InvalidTrainingException("Training member does not exist.");
        }
        if (sports.stream().noneMatch(s -> s.getSportName().equalsIgnoreCase(training.getSportName().trim()))) {
            throw new InvalidTrainingException("Training sport does not exist.");
        }
        if (coaches.stream().noneMatch(c -> c.getName().equalsIgnoreCase(training.getCoachName().trim()))) {
            throw new InvalidTrainingException("Training coach does not exist.");
        }
        if (!member.isEnrolledInSport(training.getSportName())) {
            throw new InvalidTrainingException("Member must be enrolled in the selected sport before training.");
        }
    }

    // ==========================================
    // MEMBER CRUD (ArrayList + HashMap + TreeMap)
    // ==========================================
    public synchronized void addMember(Member member) throws InvalidMemberException {
        validateMember(member, true);
        members.add(member);
        rebuildIndexes();
    }

    public synchronized void updateMember(Member updatedMember) throws InvalidMemberException {
        validateMember(updatedMember, false);
        for (int i = 0; i < members.size(); i++) {
            Member existingMember = members.get(i);
            if (normalizeMemberId(existingMember.getId()).equals(normalizeMemberId(updatedMember.getId()))) {
                members.set(i, updatedMember);
                for (Training training : trainingSchedule) {
                    if (isScheduled(training)
                            && normalizeMemberId(training.getMemberId()).equals(normalizeMemberId(updatedMember.getId()))) {
                        training.setMemberName(updatedMember.getName());
                    }
                }
                rebuildIndexes();
                return;
            }
        }
        throw new InvalidMemberException("Member with ID '" + updatedMember.getId() + "' not found.");
    }

    public synchronized boolean deleteMember(String memberId) {
        if (memberId == null) return false;
        Member toRemove = memberMap.get(normalizeMemberId(memberId));
        if (toRemove != null) {
            for (Training training : trainingSchedule) {
                if (isScheduled(training)
                        && normalizeMemberId(training.getMemberId()).equals(normalizeMemberId(toRemove.getId()))) {
                    training.setStatus(Training.STATUS_CANCELLED);
                }
            }
            members.remove(toRemove);
            rebuildIndexes();
            return true;
        }
        return false;
    }

    public Member getMemberById(String memberId) {
        if (memberId == null) return null;
        return memberMap.get(normalizeMemberId(memberId));
    }

    public ArrayList<Member> getAllMembers() {
        return new ArrayList<>(members);
    }

    // ==========================================
    // SPORTS CRUD (ArrayList)
    // ==========================================
    public synchronized void addSport(Sport sport) throws Exception {
        if (sport == null || sport.getSportId() == null || sport.getSportId().trim().isEmpty()) {
            throw new Exception("Sport ID is required.");
        }
        if (sport.getSportName() == null || sport.getSportName().trim().isEmpty()) {
            throw new Exception("Sport Name cannot be empty.");
        }
        for (Sport s : sports) {
            if (s.getSportId().equalsIgnoreCase(sport.getSportId().trim())) {
                throw new Exception("Sport ID '" + sport.getSportId() + "' already exists!");
            }
        }
        sports.add(sport);
    }

    public synchronized void updateSport(Sport updatedSport) throws Exception {
        if (updatedSport == null || updatedSport.getSportId() == null
                || updatedSport.getSportName() == null || updatedSport.getSportName().trim().isEmpty()) {
            throw new Exception("Invalid sport data.");
        }
        for (int i = 0; i < sports.size(); i++) {
            Sport existingSport = sports.get(i);
            if (existingSport.getSportId().equalsIgnoreCase(updatedSport.getSportId())) {
                String oldName = existingSport.getSportName();
                String newName = updatedSport.getSportName().trim();
                if (!oldName.equalsIgnoreCase(newName)) {
                    for (Member member : members) {
                        if (member.unenrollSport(oldName)) {
                            member.enrollSport(newName);
                        }
                    }
                    for (Training training : trainingSchedule) {
                        if (isScheduled(training) && training.getSportName().equalsIgnoreCase(oldName)) {
                            training.setSportName(newName);
                        }
                    }
                    for (Coach coach : coaches) {
                        if (coach.getAssignedSport().equalsIgnoreCase(oldName)) {
                            coach.setAssignedSport(newName);
                        }
                    }
                }
                sports.set(i, updatedSport);
                return;
            }
        }
        throw new Exception("Sport with ID '" + updatedSport.getSportId() + "' not found.");
    }

    public synchronized boolean deleteSport(String sportId) {
        if (sportId == null) return false;
        for (int i = 0; i < sports.size(); i++) {
            if (sports.get(i).getSportId().equalsIgnoreCase(sportId)) {
                Sport removedSport = sports.remove(i);
                for (Member member : members) {
                    member.unenrollSport(removedSport.getSportName());
                }
                for (Training training : trainingSchedule) {
                    if (isScheduled(training) && training.getSportName().equalsIgnoreCase(removedSport.getSportName())) {
                        training.setStatus(Training.STATUS_CANCELLED);
                    }
                }
                for (Coach coach : coaches) {
                    if (coach.getAssignedSport().equalsIgnoreCase(removedSport.getSportName())) {
                        coach.setAssignedSport("Unassigned");
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Sport getSportById(String sportId) {
        for (Sport s : sports) {
            if (s.getSportId().equalsIgnoreCase(sportId)) {
                return s;
            }
        }
        return null;
    }

    public ArrayList<Sport> getAllSports() {
        return new ArrayList<>(sports);
    }

    // ==========================================
    // COACH CRUD (ArrayList)
    // ==========================================
    public synchronized void addCoach(Coach coach) throws Exception {
        if (coach == null || coach.getId() == null || coach.getId().trim().isEmpty()) {
            throw new Exception("Coach ID is required.");
        }
        if (coach.getName() == null || coach.getName().trim().isEmpty()) {
            throw new Exception("Coach Name cannot be empty.");
        }
        if (coach.getContact() == null || coach.getContact().trim().isEmpty()) {
            throw new Exception("Coach Contact cannot be empty.");
        }
        for (Coach c : coaches) {
            if (c.getId().equalsIgnoreCase(coach.getId().trim())) {
                throw new Exception("Coach ID '" + coach.getId() + "' already exists!");
            }
        }
        coaches.add(coach);
    }

    public synchronized void updateCoach(Coach updatedCoach) throws Exception {
        if (updatedCoach == null || updatedCoach.getId() == null
                || updatedCoach.getName() == null || updatedCoach.getName().trim().isEmpty()) {
            throw new Exception("Invalid coach data.");
        }
        for (int i = 0; i < coaches.size(); i++) {
            Coach existingCoach = coaches.get(i);
            if (existingCoach.getId().equalsIgnoreCase(updatedCoach.getId())) {
                String oldName = existingCoach.getName();
                String newName = updatedCoach.getName().trim();
                if (!oldName.equalsIgnoreCase(newName)) {
                    for (Sport sport : sports) {
                        if (sport.getAssignedCoachName().equalsIgnoreCase(oldName)) {
                            sport.setAssignedCoachName(newName);
                        }
                    }
                    for (Training training : trainingSchedule) {
                        if (isScheduled(training) && training.getCoachName().equalsIgnoreCase(oldName)) {
                            training.setCoachName(newName);
                        }
                    }
                }
                coaches.set(i, updatedCoach);
                return;
            }
        }
        throw new Exception("Coach with ID '" + updatedCoach.getId() + "' not found.");
    }

    public synchronized boolean deleteCoach(String coachId) {
        if (coachId == null) return false;
        for (int i = 0; i < coaches.size(); i++) {
            if (coaches.get(i).getId().equalsIgnoreCase(coachId)) {
                Coach removedCoach = coaches.remove(i);
                for (Sport sport : sports) {
                    if (sport.getAssignedCoachName().equalsIgnoreCase(removedCoach.getName())) {
                        sport.setAssignedCoachName("Unassigned");
                    }
                }
                for (Training training : trainingSchedule) {
                    if (isScheduled(training) && training.getCoachName().equalsIgnoreCase(removedCoach.getName())) {
                        training.setStatus(Training.STATUS_CANCELLED);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Coach getCoachById(String coachId) {
        for (Coach c : coaches) {
            if (c.getId().equalsIgnoreCase(coachId)) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Coach> getAllCoaches() {
        return new ArrayList<>(coaches);
    }

    // ==========================================
    // MEMBER ENROLLMENT (HashSet Requirement)
    // ==========================================
    public synchronized void enrollMemberInSport(String memberId, String sportName) throws InvalidMemberException {
        Member member = getMemberById(memberId);
        if (member == null) {
            throw new InvalidMemberException("Member with ID '" + memberId + "' does not exist.");
        }
        if (sportName == null || sportName.trim().isEmpty()) {
            throw new InvalidMemberException("Please select a sport to enroll.");
        }
        // HashSet prevents duplicate enrollment
        boolean added = member.enrollSport(sportName.trim());
        if (!added) {
            throw new InvalidMemberException("Member '" + member.getName() + "' is already enrolled in " + sportName + "!");
        }
    }

    public synchronized boolean unenrollMemberFromSport(String memberId, String sportName) {
        Member member = getMemberById(memberId);
        if (member != null) {
            return member.unenrollSport(sportName);
        }
        return false;
    }

    // ==========================================
    // TRAINING SCHEDULE & HISTORY (LinkedList)
    // ==========================================
    public synchronized void scheduleTraining(Training training) throws InvalidTrainingException {
        validateTraining(training);
        for (Training t : trainingSchedule) {
            if (t.getTrainingId().equalsIgnoreCase(training.getTrainingId())) {
                throw new InvalidTrainingException("Training ID '" + training.getTrainingId() + "' already exists.");
            }
            // Check coach availability on same date and slot
            if (t.getCoachName().equalsIgnoreCase(training.getCoachName()) &&
                t.getDate().equals(training.getDate()) &&
                t.getTrainingSlot().equalsIgnoreCase(training.getTrainingSlot()) &&
                !Training.STATUS_CANCELLED.equals(t.getStatus())) {
                throw new InvalidTrainingException("Coach " + training.getCoachName() + 
                    " already has a training scheduled at " + training.getTrainingSlot() + " on " + training.getDate() + "!");
            }
        }
        trainingSchedule.add(training);
    }

    public synchronized void completeTraining(String trainingId) throws InvalidTrainingException {
        for (Training t : trainingSchedule) {
            if (t.getTrainingId().equalsIgnoreCase(trainingId)) {
                t.setStatus(Training.STATUS_COMPLETED);
                // Add to LinkedList training history
                trainingHistory.addFirst(t);
                return;
            }
        }
        throw new InvalidTrainingException("Training session not found.");
    }

    public synchronized void cancelTraining(String trainingId) throws InvalidTrainingException {
        for (Training t : trainingSchedule) {
            if (t.getTrainingId().equalsIgnoreCase(trainingId)) {
                t.setStatus(Training.STATUS_CANCELLED);
                return;
            }
        }
        throw new InvalidTrainingException("Training session not found.");
    }

    public synchronized boolean deleteTraining(String trainingId) {
        for (Iterator<Training> it = trainingSchedule.iterator(); it.hasNext(); ) {
            Training t = it.next();
            if (t.getTrainingId().equalsIgnoreCase(trainingId)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public LinkedList<Training> getTrainingSchedule() {
        return new LinkedList<>(trainingSchedule);
    }

    public LinkedList<Training> getTrainingHistory() {
        return new LinkedList<>(trainingHistory);
    }

    // ==========================================
    // PAYMENT MANAGEMENT (LinkedList)
    // ==========================================
    public synchronized void recordPayment(Payment payment) throws Exception {
        if (payment == null) {
            throw new Exception("Payment object cannot be null.");
        }
        if (payment.getPaymentId() == null || payment.getPaymentId().trim().isEmpty()) {
            throw new Exception("Payment ID is required.");
        }
        if (payment.getMemberId() == null || payment.getMemberId().trim().isEmpty()) {
            throw new Exception("Member must be selected for payment.");
        }
        if (getMemberById(payment.getMemberId()) == null) {
            throw new Exception("Payment member does not exist.");
        }
        if (payment.getPaymentDate() == null) {
            throw new Exception("Payment date is required.");
        }
        if (!Double.isFinite(payment.getAmount()) || payment.getAmount() <= 0) {
            throw new Exception("Payment amount must be greater than zero.");
        }
        for (Payment existingPayment : paymentHistory) {
            String existingPaymentId = existingPayment.getPaymentId();
            if (existingPaymentId != null
                    && existingPaymentId.equalsIgnoreCase(payment.getPaymentId().trim())) {
                throw new Exception("Payment ID '" + payment.getPaymentId().trim() + "' already exists.");
            }
        }
        // LinkedList requirement: Add to payment history
        paymentHistory.addFirst(payment);
    }

    public LinkedList<Payment> getPaymentHistory() {
        return new LinkedList<>(paymentHistory);
    }

    public double getTotalPaymentsAmount() {
        double total = 0.0;
        for (Payment p : paymentHistory) {
            if ("Paid".equalsIgnoreCase(p.getPaymentStatus())) {
                total += p.getAmount();
            }
        }
        return total;
    }

    // ==========================================
    // MEMBERSHIP RENEWAL & EXPIRY
    // ==========================================
    public synchronized void renewMembership(String memberId, String planName, LocalDate startDate) throws InvalidMemberException {
        Member member = getMemberById(memberId);
        if (member == null) {
            throw new InvalidMemberException("Member with ID '" + memberId + "' does not exist.");
        }
        if (!Membership.isValidPlan(planName)) {
            throw new InvalidMemberException("Unsupported membership plan: " + planName);
        }
        Membership newMembership = new Membership(
            "MS-" + memberId + "-" + (System.currentTimeMillis() % 10000),
            planName,
            startDate != null ? startDate : LocalDate.now()
        );
        Payment renewalPayment = new Payment(
            "PAY-" + UUID.randomUUID(),
            member.getId(),
            member.getName(),
            newMembership.getPrice(),
            LocalDate.now(),
            newMembership.getPlanName(),
            "Cash",
            "Paid"
        );
        try {
            recordPayment(renewalPayment);
        } catch (Exception e) {
            throw new InvalidMemberException("Membership renewal was not completed: " + e.getMessage());
        }
        member.setMembership(newMembership);
        rebuildIndexes();
    }

    public ArrayList<Member> getExpiredMembers() {
        ArrayList<Member> result = new ArrayList<>();
        for (Member m : members) {
            if (m.getMembership() != null && m.getMembership().isExpired()) {
                result.add(m);
            }
        }
        return result;
    }

    public ArrayList<Member> getExpiringSoonMembers() {
        ArrayList<Member> result = new ArrayList<>();
        for (Member m : members) {
            if (m.getMembership() != null && m.getMembership().isExpiringSoon()) {
                result.add(m);
            }
        }
        return result;
    }

    public ArrayList<Member> getActiveMembers() {
        ArrayList<Member> result = new ArrayList<>();
        for (Member m : members) {
            if (m.getMembership() != null && !m.getMembership().isExpired()) {
                result.add(m);
            }
        }
        return result;
    }

    // ==========================================
    // SEARCHING IMPLEMENTATION
    // ==========================================
    public ArrayList<Member> searchMembers(String query, String field) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMembers();
        }
        String q = query.trim().toLowerCase();
        ArrayList<Member> results = new ArrayList<>();

        for (Member m : members) {
            boolean match = false;
            switch (field) {
                case "ID":
                    match = m.getId().toLowerCase().contains(q);
                    break;
                case "Name":
                    match = m.getName().toLowerCase().contains(q);
                    break;
                case "Sport":
                    match = m.getEnrolledSportsString().toLowerCase().contains(q);
                    break;
                case "Coach":
                    // Check if member is scheduled with coach
                    for (Training t : trainingSchedule) {
                        if (t.getMemberId().equalsIgnoreCase(m.getId()) &&
                            t.getCoachName().toLowerCase().contains(q)) {
                            match = true;
                            break;
                        }
                    }
                    break;
                case "All":
                default:
                    match = m.getId().toLowerCase().contains(q) ||
                            m.getName().toLowerCase().contains(q) ||
                            m.getContact().toLowerCase().contains(q) ||
                            m.getEmail().toLowerCase().contains(q) ||
                            m.getEnrolledSportsString().toLowerCase().contains(q);
                    break;
            }
            if (match) {
                results.add(m);
            }
        }
        return results;
    }

    public ArrayList<Sport> searchSports(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllSports();
        }
        String q = query.trim().toLowerCase();
        ArrayList<Sport> results = new ArrayList<>();
        for (Sport s : sports) {
            if (s.getSportId().toLowerCase().contains(q) ||
                s.getSportName().toLowerCase().contains(q) ||
                s.getCategory().toLowerCase().contains(q) ||
                s.getAssignedCoachName().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    public ArrayList<Coach> searchCoaches(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCoaches();
        }
        String q = query.trim().toLowerCase();
        ArrayList<Coach> results = new ArrayList<>();
        for (Coach c : coaches) {
            if (c.getId().toLowerCase().contains(q) ||
                c.getName().toLowerCase().contains(q) ||
                c.getSpecialization().toLowerCase().contains(q) ||
                c.getAssignedSport().toLowerCase().contains(q)) {
                results.add(c);
            }
        }
        return results;
    }

    // ==========================================
    // SORTING IMPLEMENTATION (Comparator & TreeMap)
    // ==========================================
    public ArrayList<Member> sortMembersByName(boolean ascending) {
        ArrayList<Member> sortedList = new ArrayList<>(members);
        sortedList.sort((m1, m2) -> {
            int comp = m1.getName().compareToIgnoreCase(m2.getName());
            return ascending ? comp : -comp;
        });
        return sortedList;
    }

    public ArrayList<Member> sortMembersByExpiry(boolean ascending) {
        ArrayList<Member> sortedList = new ArrayList<>(members);
        sortedList.sort((m1, m2) -> {
            LocalDate d1 = m1.getMembershipExpiryDate();
            LocalDate d2 = m2.getMembershipExpiryDate();
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            int comp = d1.compareTo(d2);
            return ascending ? comp : -comp;
        });
        return sortedList;
    }

    // TreeMap requirement: returns TreeMap organized by expiry
    public TreeMap<LocalDate, ArrayList<Member>> getMembersByExpiryTreeMap() {
        return new TreeMap<>(membersByExpiryMap);
    }

    // ==========================================
    // FILE PERSISTENCE (Save / Load)
    // ==========================================
    public synchronized boolean saveToFile(File file) {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                ClubData data = new ClubData(members, sports, coaches, trainingSchedule, trainingHistory, paymentHistory);
                oos.writeObject(data);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saving club data: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean loadFromFile(File file) {
        if (file == null || !file.exists() || file.length() == 0) {
            return false;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ClubData data = (ClubData) ois.readObject();
            if (data != null) {
                this.members = data.getMembers() != null ? data.getMembers() : new ArrayList<>();
                this.sports = data.getSports() != null ? data.getSports() : new ArrayList<>();
                this.coaches = data.getCoaches() != null ? data.getCoaches() : new ArrayList<>();
                this.trainingSchedule = data.getTrainingSchedule() != null ? data.getTrainingSchedule() : new LinkedList<>();
                this.trainingHistory = data.getTrainingHistory() != null ? data.getTrainingHistory() : new LinkedList<>();
                this.paymentHistory = data.getPaymentHistory() != null ? data.getPaymentHistory() : new LinkedList<>();
                rebuildIndexes();
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error loading club data: " + e.getMessage());
        }
        return false;
    }

    public synchronized void resetToDefaultData() {
        members.clear();
        sports.clear();
        coaches.clear();
        trainingSchedule.clear();
        trainingHistory.clear();
        paymentHistory.clear();
        loadSampleData();
        saveToFile(dataFile);
    }

    public void saveDefaultData() {
        saveToFile(dataFile);
    }

    // ==========================================
    // SAMPLE DATA INITIALIZATION
    // ==========================================
    private void loadSampleData() {
        // Sports
        Sport s1 = new Sport("SP101", "Tennis", "Racquet", "Lawn Tennis Court with Synthetic Turf", "John McEnroe");
        Sport s2 = new Sport("SP102", "Badminton", "Racquet", "Indoor Wooden Flooring Courts", "P. Gopichand");
        Sport s3 = new Sport("SP103", "Swimming", "Aquatics", "Olympic-size 50m Heated Pool", "Michael Phelps");
        Sport s4 = new Sport("SP104", "Basketball", "Indoor", "Hardwood Court with Shot Clocks", "Steve Kerr");
        Sport s5 = new Sport("SP105", "Fitness & Gym", "Fitness & Gym", "Weights, Cardio & Calisthenics Zone", "Arnold S.");
        sports.add(s1);
        sports.add(s2);
        sports.add(s3);
        sports.add(s4);
        sports.add(s5);

        // Coaches
        Coach c1 = new Coach("CH01", "John McEnroe", "9876543210", "john@club.com", "Grand Slam Veteran", "Tennis", 15);
        Coach c2 = new Coach("CH02", "P. Gopichand", "9876543211", "gopichand@club.com", "National Badminton Coach", "Badminton", 18);
        Coach c3 = new Coach("CH03", "Michael Phelps", "9876543212", "michael@club.com", "Olympic Gold Specialist", "Swimming", 12);
        Coach c4 = new Coach("CH04", "Steve Kerr", "9876543213", "kerr@club.com", "Tactical Team Play", "Basketball", 14);
        Coach c5 = new Coach("CH05", "Arnold S.", "9876543214", "arnold@club.com", "Bodybuilding & Conditioning", "Fitness & Gym", 25);
        coaches.add(c1);
        coaches.add(c2);
        coaches.add(c3);
        coaches.add(c4);
        coaches.add(c5);

        // Members with varying expiry dates (Active, Expiring Soon, Expired)
        LocalDate today = LocalDate.now();

        // Member 1: Active Yearly
        Membership mship1 = new Membership("MS101", Membership.PLAN_YEARLY, 450.0, today.minusMonths(2), today.plusMonths(10));
        Member m1 = new Member("M1001", "Aarav Sharma", 24, "Male", "9812345678", "aarav@gmail.com", "12 Park Street", mship1);
        m1.enrollSport("Tennis");
        m1.enrollSport("Swimming");

        // Member 2: Expiring Soon (15 days remaining)
        Membership mship2 = new Membership("MS102", Membership.PLAN_MONTHLY, 50.0, today.minusDays(15), today.plusDays(15));
        Member m2 = new Member("M1002", "Riya Patel", 22, "Female", "9823456789", "riya@gmail.com", "45 Lake View Road", mship2);
        m2.enrollSport("Badminton");

        // Member 3: Already Expired (10 days ago)
        Membership mship3 = new Membership("MS103", Membership.PLAN_QUARTERLY, 135.0, today.minusMonths(3).minusDays(10), today.minusDays(10));
        Member m3 = new Member("M1003", "Vikram Singh", 29, "Male", "9834567890", "vikram@gmail.com", "88 Ring Road", mship3);
        m3.enrollSport("Fitness & Gym");

        // Member 4: Active Half-Yearly
        Membership mship4 = new Membership("MS104", Membership.PLAN_HALF_YEARLY, 250.0, today.minusMonths(1), today.plusMonths(5));
        Member m4 = new Member("M1004", "Ananya Verma", 20, "Female", "9845678901", "ananya@gmail.com", "9 Ocean Avenue", mship4);
        m4.enrollSport("Swimming");
        m4.enrollSport("Badminton");

        // Member 5: Expiring Soon (5 days remaining)
        Membership mship5 = new Membership("MS105", Membership.PLAN_MONTHLY, 50.0, today.minusDays(25), today.plusDays(5));
        Member m5 = new Member("M1005", "Rohan Mehta", 27, "Male", "9856789012", "rohan@gmail.com", "17 Hill Top", mship5);
        m5.enrollSport("Basketball");

        members.add(m1);
        members.add(m2);
        members.add(m3);
        members.add(m4);
        members.add(m5);

        // Training Sessions
        Training t1 = new Training("TR-01", "Tennis", "John McEnroe", "M1001", "Aarav Sharma", today.plusDays(1), Training.FIXED_SLOTS[0], "Backhand drills");
        Training t2 = new Training("TR-02", "Swimming", "Michael Phelps", "M1004", "Ananya Verma", today.plusDays(2), Training.FIXED_SLOTS[1], "Freestyle breathing");
        Training t3 = new Training("TR-03", "Badminton", "P. Gopichand", "M1002", "Riya Patel", today.plusDays(3), Training.FIXED_SLOTS[2], "Smash technique");
        trainingSchedule.add(t1);
        trainingSchedule.add(t2);
        trainingSchedule.add(t3);

        // Completed Training in History (LinkedList)
        Training th1 = new Training("TR-00", "Fitness & Gym", "Arnold S.", "M1003", "Vikram Singh", today.minusDays(5), Training.FIXED_SLOTS[3], "Cardio orientation");
        th1.setStatus(Training.STATUS_COMPLETED);
        trainingHistory.add(th1);

        // Payment Records (LinkedList)
        Payment p1 = new Payment("PAY-1001", "M1001", "Aarav Sharma", 450.0, today.minusMonths(2), Membership.PLAN_YEARLY, "Credit Card", "Paid");
        Payment p2 = new Payment("PAY-1002", "M1002", "Riya Patel", 50.0, today.minusDays(15), Membership.PLAN_MONTHLY, "UPI / GPay", "Paid");
        Payment p3 = new Payment("PAY-1003", "M1003", "Vikram Singh", 135.0, today.minusMonths(3).minusDays(10), Membership.PLAN_QUARTERLY, "Debit Card", "Paid");
        Payment p4 = new Payment("PAY-1004", "M1004", "Ananya Verma", 250.0, today.minusMonths(1), Membership.PLAN_HALF_YEARLY, "Net Banking", "Paid");
        Payment p5 = new Payment("PAY-1005", "M1005", "Rohan Mehta", 50.0, today.minusDays(25), Membership.PLAN_MONTHLY, "Cash", "Paid");
        paymentHistory.add(p1);
        paymentHistory.add(p2);
        paymentHistory.add(p3);
        paymentHistory.add(p4);
        paymentHistory.add(p5);

        rebuildIndexes();
    }

    // ==========================================
    // CLUB REPORT SUMMARY TEXT
    // ==========================================
    public String generateClubReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("          SPORTS CLUB MEMBERSHIP MANAGEMENT SYSTEM               \n");
        sb.append("              ACADEMIC CASE STUDY 47 REPORT                      \n");
        sb.append("=================================================================\n\n");
        sb.append("Generated On: ").append(LocalDate.now()).append("\n\n");

        sb.append("1. EXECUTIVE METRICS SUMMARY\n");
        sb.append("-----------------------------------------------------------------\n");
        sb.append(String.format(" * Total Members Registered:       %d\n", members.size()));
        sb.append(String.format(" * Total Sports Disciplines:       %d\n", sports.size()));
        sb.append(String.format(" * Total Appointed Coaches:        %d\n", coaches.size()));
        sb.append(String.format(" * Active Subscriptions:           %d\n", getActiveMembers().size()));
        sb.append(String.format(" * Subscriptions Expiring Soon:    %d\n", getExpiringSoonMembers().size()));
        sb.append(String.format(" * Expired Subscriptions:          %d\n", getExpiredMembers().size()));
        sb.append(String.format(" * Scheduled Training Sessions:    %d\n", trainingSchedule.size()));
        sb.append(String.format(" * Training History Completed:     %d\n", trainingHistory.size()));
        sb.append(String.format(" * Total Revenue Collected:        $%.2f\n\n", getTotalPaymentsAmount()));

        sb.append("2. MEMBERSHIP EXPIRY DISTRIBUTION (TreeMap View)\n");
        sb.append("-----------------------------------------------------------------\n");
        TreeMap<LocalDate, ArrayList<Member>> expiryMap = getMembersByExpiryTreeMap();
        if (expiryMap.isEmpty()) {
            sb.append(" No member records found.\n");
        } else {
            for (Map.Entry<LocalDate, ArrayList<Member>> entry : expiryMap.entrySet()) {
                sb.append(" Expiry Date: ").append(entry.getKey()).append(" -> ");
                for (Member m : entry.getValue()) {
                    sb.append(m.getName()).append(" (").append(m.getId()).append(") ");
                }
                sb.append("\n");
            }
        }
        sb.append("\n");

        sb.append("3. SPORTS & ASSIGNED COACHES (ArrayList View)\n");
        sb.append("-----------------------------------------------------------------\n");
        for (Sport s : sports) {
            sb.append(String.format(" [%s] %-15s | Category: %-15s | Coach: %s\n",
                    s.getSportId(), s.getSportName(), s.getCategory(), s.getAssignedCoachName()));
        }
        sb.append("\n");

        sb.append("4. RECENT PAYMENTS (LinkedList View)\n");
        sb.append("-----------------------------------------------------------------\n");
        int count = 0;
        for (Payment p : paymentHistory) {
            sb.append(String.format(" [%s] Member: %-12s | Plan: %-12s | Amount: $%-7.2f | Date: %s | Via: %s\n",
                    p.getPaymentId(), p.getMemberName(), p.getMembershipPlan(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod()));
            if (++count >= 10) break;
        }

        sb.append("\n=================================================================\n");
        sb.append("                     END OF CLUB REPORT                          \n");
        sb.append("=================================================================\n");
        return sb.toString();
    }
}
