import controller.ClubController;
import model.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Automated Verification Suite for Case Study 47.
 * Validates the core project workflows without touching the user's saved data.
 */
public class TestRunner {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  RUNNING CASE STUDY 47 AUTOMATED VERIFICATION SUITE (46 TESTS)  ");
        System.out.println("=================================================================\n");

        File testDataFile;
        try {
            testDataFile = File.createTempFile("sports-club-test-data-", ".dat");
            testDataFile.deleteOnExit();
        } catch (IOException e) {
            throw new IllegalStateException("Could not create isolated test data file.", e);
        }

        ClubController controller = new ClubController(testDataFile);
        controller.resetToDefaultData();

        // 1. Add Member
        try {
            Membership ms = new Membership("MS-T1", Membership.PLAN_MONTHLY, LocalDate.now());
            Member m = new Member("M9999", "Test Member", 25, "Male", "9998887776", "test@club.com", "Test City", ms);
            controller.addMember(m);
            assertTest("1. Add Member", controller.getMemberById("M9999") != null);
        } catch (Exception e) {
            failTest("1. Add Member", e);
        }

        // 2. View Member (get and check details)
        try {
            Member m = controller.getMemberById("M9999");
            assertTest("2. View Member", m != null && m.getName().equals("Test Member"));
        } catch (Exception e) {
            failTest("2. View Member", e);
        }

        // 3. Update Member
        try {
            Member m = controller.getMemberById("M9999");
            m.setName("Updated Test Member");
            controller.updateMember(m);
            Member updated = controller.getMemberById("M9999");
            assertTest("3. Update Member", updated.getName().equals("Updated Test Member"));
        } catch (Exception e) {
            failTest("3. Update Member", e);
        }

        // 4. Delete Member
        try {
            boolean deleted = controller.deleteMember("M9999");
            assertTest("4. Delete Member", deleted && controller.getMemberById("M9999") == null);
        } catch (Exception e) {
            failTest("4. Delete Member", e);
        }

        // 5. Search Member
        try {
            ArrayList<Member> res = controller.searchMembers("Aarav", "Name");
            assertTest("5. Search Member", !res.isEmpty() && res.get(0).getName().contains("Aarav"));
        } catch (Exception e) {
            failTest("5. Search Member", e);
        }

        // 6. Sort Members by Name
        try {
            ArrayList<Member> sorted = controller.sortMembersByName(true);
            boolean isSorted = true;
            for (int i = 0; i < sorted.size() - 1; i++) {
                if (sorted.get(i).getName().compareToIgnoreCase(sorted.get(i + 1).getName()) > 0) {
                    isSorted = false;
                    break;
                }
            }
            assertTest("6. Sort Members by Name", isSorted);
        } catch (Exception e) {
            failTest("6. Sort Members by Name", e);
        }

        // 7. Sort Members by Expiry
        try {
            ArrayList<Member> sorted = controller.sortMembersByExpiry(true);
            boolean isSorted = true;
            for (int i = 0; i < sorted.size() - 1; i++) {
                LocalDate d1 = sorted.get(i).getMembershipExpiryDate();
                LocalDate d2 = sorted.get(i + 1).getMembershipExpiryDate();
                if (d1.isAfter(d2)) {
                    isSorted = false;
                    break;
                }
            }
            assertTest("7. Sort Members by Expiry", isSorted);
        } catch (Exception e) {
            failTest("7. Sort Members by Expiry", e);
        }

        // 8. Add Sport
        try {
            Sport s = new Sport("SP999", "Squash", "Racquet", "Squash court", "Unassigned");
            controller.addSport(s);
            assertTest("8. Add Sport", controller.getSportById("SP999") != null);
        } catch (Exception e) {
            failTest("8. Add Sport", e);
        }

        // 9. Update Sport
        try {
            Sport s = controller.getSportById("SP999");
            s.setDescription("Updated court description");
            controller.updateSport(s);
            assertTest("9. Update Sport", controller.getSportById("SP999").getDescription().equals("Updated court description"));
        } catch (Exception e) {
            failTest("9. Update Sport", e);
        }

        // 10. Delete Sport
        try {
            boolean del = controller.deleteSport("SP999");
            assertTest("10. Delete Sport", del && controller.getSportById("SP999") == null);
        } catch (Exception e) {
            failTest("10. Delete Sport", e);
        }

        // 11. Search Sport
        try {
            ArrayList<Sport> res = controller.searchSports("Badminton");
            assertTest("11. Search Sport", !res.isEmpty() && res.get(0).getSportName().equals("Badminton"));
        } catch (Exception e) {
            failTest("11. Search Sport", e);
        }

        // 12. Add Coach
        try {
            Coach c = new Coach("CH99", "Test Coach", "9112233445", "coach@test.com", "Fitness", "Tennis", 5);
            controller.addCoach(c);
            assertTest("12. Add Coach", controller.getCoachById("CH99") != null);
        } catch (Exception e) {
            failTest("12. Add Coach", e);
        }

        // 13. Update Coach
        try {
            Coach c = controller.getCoachById("CH99");
            c.setSpecialization("Elite Tennis Pro");
            controller.updateCoach(c);
            assertTest("13. Update Coach", controller.getCoachById("CH99").getSpecialization().equals("Elite Tennis Pro"));
        } catch (Exception e) {
            failTest("13. Update Coach", e);
        }

        // 14. Delete Coach
        try {
            boolean del = controller.deleteCoach("CH99");
            assertTest("14. Delete Coach", del && controller.getCoachById("CH99") == null);
        } catch (Exception e) {
            failTest("14. Delete Coach", e);
        }

        // 15. Search Coach
        try {
            ArrayList<Coach> res = controller.searchCoaches("Phelps");
            assertTest("15. Search Coach", !res.isEmpty() && res.get(0).getName().contains("Phelps"));
        } catch (Exception e) {
            failTest("15. Search Coach", e);
        }

        // 16. Create Membership
        try {
            Membership ms = new Membership("MS-NEW", Membership.PLAN_QUARTERLY, LocalDate.of(2026, 1, 1));
            assertTest("16. Create Membership", ms.getDurationMonths() == 3 && ms.getPrice() == 135.0);
        } catch (Exception e) {
            failTest("16. Create Membership", e);
        }

        // 17. Calculate Expiry
        try {
            LocalDate start = LocalDate.of(2026, 1, 1);
            Membership ms = new Membership("MS-CALC", Membership.PLAN_HALF_YEARLY, start);
            assertTest("17. Calculate Expiry", ms.getExpiryDate().equals(LocalDate.of(2026, 7, 1)));
        } catch (Exception e) {
            failTest("17. Calculate Expiry", e);
        }

        // 18. Display Expiry Alert
        try {
            ArrayList<Member> expired = controller.getExpiredMembers();
            ArrayList<Member> expiring = controller.getExpiringSoonMembers();
            assertTest("18. Display Expiry Alert", !expired.isEmpty() || !expiring.isEmpty());
        } catch (Exception e) {
            failTest("18. Display Expiry Alert", e);
        }

        // 19. Enroll Member in Sport (HashSet)
        try {
            Member m = controller.getMemberById("M1001");
            controller.enrollMemberInSport(m.getId(), "Basketball");
            assertTest("19. Enroll Member in Sport", m.isEnrolledInSport("Basketball"));
        } catch (Exception e) {
            failTest("19. Enroll Member in Sport", e);
        }

        // 20. Prevent Duplicate Sport Enrollment (HashSet)
        try {
            boolean duplicateCaught = false;
            try {
                controller.enrollMemberInSport("M1001", "Basketball");
            } catch (InvalidMemberException ime) {
                duplicateCaught = true;
            }
            assertTest("20. Prevent Duplicate Sport Enrollment", duplicateCaught);
        } catch (Exception e) {
            failTest("20. Prevent Duplicate Sport Enrollment", e);
        }

        // 21. Create Training Session
        try {
            Training t = new Training("TR-TEST", "Tennis", "John McEnroe", "M1001", "Aarav Sharma", 
                    LocalDate.now().plusDays(5), Training.FIXED_SLOTS[4], "Forehand work");
            controller.scheduleTraining(t);
            assertTest("21. Create Training Session", controller.getTrainingSchedule().stream().anyMatch(tr -> tr.getTrainingId().equals("TR-TEST")));
        } catch (Exception e) {
            failTest("21. Create Training Session", e);
        }

        // 22. View Training Schedule
        try {
            LinkedList<Training> sched = controller.getTrainingSchedule();
            assertTest("22. View Training Schedule", !sched.isEmpty());
        } catch (Exception e) {
            failTest("22. View Training Schedule", e);
        }

        // 23. Add Training History (LinkedList)
        try {
            controller.completeTraining("TR-TEST");
            LinkedList<Training> hist = controller.getTrainingHistory();
            assertTest("23. Add Training History", hist.getFirst().getTrainingId().equals("TR-TEST"));
        } catch (Exception e) {
            failTest("23. Add Training History", e);
        }

        // 24. Add Payment
        try {
            Payment p = new Payment("PAY-TEST", "M1001", "Aarav Sharma", 100.0, LocalDate.now(), "Monthly", "Cash", "Paid");
            controller.recordPayment(p);
            assertTest("24. Add Payment", controller.getPaymentHistory().getFirst().getPaymentId().equals("PAY-TEST"));
        } catch (Exception e) {
            failTest("24. Add Payment", e);
        }

        // 25. View Payment History (LinkedList)
        try {
            LinkedList<Payment> history = controller.getPaymentHistory();
            assertTest("25. View Payment History", !history.isEmpty() && history.getFirst().getPaymentId().equals("PAY-TEST"));
        } catch (Exception e) {
            failTest("25. View Payment History", e);
        }

        // 26. Save Data
        File tempFile = null;
        try {
            tempFile = File.createTempFile("sports-club-persistence-test-", ".dat");
            tempFile.deleteOnExit();
            boolean saved = controller.saveToFile(tempFile);
            assertTest("26. Save Data", saved && tempFile.exists() && tempFile.length() > 0);
        } catch (Exception e) {
            failTest("26. Save Data", e);
        }

        // 27. Close / Create Fresh Controller
        ClubController newController = null;
        try {
            controller.saveDefaultData();
            newController = new ClubController(testDataFile);
            boolean restored = newController.getPaymentHistory().stream()
                    .anyMatch(payment -> payment.getPaymentId().equals("PAY-TEST"));
            assertTest("27. Restore persisted data in a fresh controller", restored);
        } catch (Exception e) {
            failTest("27. Close / Reset Controller Instance", e);
        }

        // 28. Reopen Application
        assertTest("28. Reopen Application Instance", newController != null);

        // 29. Load Saved Data
        try {
            boolean loaded = tempFile != null && newController != null && newController.loadFromFile(tempFile);
            assertTest("29. Load Saved Data", loaded && newController.getMemberById("M1001") != null);
        } catch (Exception e) {
            failTest("29. Load Saved Data", e);
        }

        // 30. Generate Club Report
        try {
            String report = controller.generateClubReport();
            assertTest("30. Generate Club Report", report != null && report.contains("SPORTS CLUB MEMBERSHIP MANAGEMENT SYSTEM"));
        } catch (Exception e) {
            failTest("30. Generate Club Report", e);
        }

        // 31. Test Invalid Input (Empty Name)
        try {
            boolean caught = false;
            try {
                Membership ms = new Membership("MS-ERR", Membership.PLAN_MONTHLY, LocalDate.now());
                Member invalidMember = new Member("M8888", "", 25, "Male", "9998887776", "test@club.com", "City", ms);
                controller.addMember(invalidMember);
            } catch (InvalidMemberException ime) {
                caught = true;
            }
            assertTest("31. Test Invalid Input (Empty Name)", caught);
        } catch (Exception e) {
            failTest("31. Test Invalid Input (Empty Name)", e);
        }

        // 32. Test Invalid Dates (Expiry before Start)
        try {
            boolean caught = false;
            try {
                Membership badMs = new Membership("MS-BAD", Membership.PLAN_MONTHLY, 50.0, LocalDate.now(), LocalDate.now().minusDays(5));
                Member invalidMember = new Member("M8887", "Bad Date Member", 25, "Male", "9998887776", "test@club.com", "City", badMs);
                controller.addMember(invalidMember);
            } catch (InvalidMemberException ime) {
                caught = true;
            }
            assertTest("32. Test Invalid Dates (Expiry before Start)", caught);
        } catch (Exception e) {
            failTest("32. Test Invalid Dates (Expiry before Start)", e);
        }

        // 33. Test Invalid Membership (Null Membership)
        try {
            boolean caught = false;
            try {
                Member invalidMember = new Member("M8886", "No Membership Member", 25, "Male", "9998887776", "test@club.com", "City", null);
                invalidMember.setMembership(null);
                controller.addMember(invalidMember);
            } catch (InvalidMemberException ime) {
                caught = true;
            }
            assertTest("33. Test Invalid Membership (Null Plan)", caught);
        } catch (Exception e) {
            failTest("33. Test Invalid Membership (Null Plan)", e);
        }

        // 34. Test Invalid Training (Invalid Slot not in Array)
        try {
            boolean caught = false;
            try {
                Training invalidTraining = new Training("TR-BAD", "Tennis", "John McEnroe", "M1001", "Aarav Sharma", 
                        LocalDate.now().plusDays(1), "03:00 AM - 04:00 AM (Invalid Slot)", "Invalid");
                controller.scheduleTraining(invalidTraining);
            } catch (InvalidTrainingException ite) {
                caught = true;
            }
            assertTest("34. Test Invalid Training (Slot Array Validation)", caught);
        } catch (Exception e) {
            failTest("34. Test Invalid Training (Slot Array Validation)", e);
        }

        // 35. Test case-insensitive duplicate member IDs
        try {
            boolean caught = false;
            try {
                Membership ms = new Membership("MS-DUP", Membership.PLAN_MONTHLY, LocalDate.now());
                Member dupMember = new Member("m1001", "Duplicate Aarav", 28, "Male", "9998887776", "dup@club.com", "City", ms);
                controller.addMember(dupMember);
            } catch (InvalidMemberException ime) {
                caught = true;
            }
            assertTest("35. Test case-insensitive duplicate member ID", caught && controller.getMemberById("m1001") != null);
        } catch (Exception e) {
            failTest("35. Test Duplicate Member ID", e);
        }

        // 36. Reject unsupported membership plans
        try {
            boolean caught = false;
            try {
                new Membership("MS-INVALID", "Lifetime", LocalDate.now());
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTest("36. Reject unsupported membership plan", caught);
        } catch (Exception e) {
            failTest("36. Reject unsupported membership plan", e);
        }

        // 37. Require member enrollment before scheduling a sport's training
        try {
            boolean caught = false;
            try {
                Training invalidEnrollment = new Training("TR-ENROLLMENT", "Badminton", "P. Gopichand",
                        "M1001", "Aarav Sharma", LocalDate.now().plusDays(5), Training.FIXED_SLOTS[3], "");
                controller.scheduleTraining(invalidEnrollment);
            } catch (InvalidTrainingException e) {
                caught = true;
            }
            assertTest("37. Require enrollment before training", caught);
        } catch (Exception e) {
            failTest("37. Require enrollment before training", e);
        }

        // 38. Keep scheduled member names consistent after an update
        try {
            Member original = controller.getMemberById("M1001");
            Member renamed = new Member(original.getId(), "Aarav Updated", original.getAge(), original.getGender(),
                    original.getContact(), original.getEmail(), original.getAddress(), original.getMembership());
            renamed.setEnrolledSports(new HashSet<>(original.getEnrolledSports()));
            controller.updateMember(renamed);
            boolean updatedSchedule = controller.getTrainingSchedule().stream()
                    .anyMatch(training -> training.getTrainingId().equals("TR-01")
                            && training.getMemberName().equals("Aarav Updated"));
            assertTest("38. Update member name in scheduled training", updatedSchedule);
        } catch (Exception e) {
            failTest("38. Update member name in scheduled training", e);
        }

        // 39. Keep sport references consistent after a rename
        try {
            Sport existingSport = controller.getSportById("SP101");
            Sport renamedSport = new Sport(existingSport.getSportId(), "Lawn Tennis", existingSport.getCategory(),
                    existingSport.getDescription(), existingSport.getAssignedCoachName());
            controller.updateSport(renamedSport);
            boolean updatedReferences = controller.getMemberById("M1001").isEnrolledInSport("Lawn Tennis")
                    && controller.getTrainingSchedule().stream()
                    .anyMatch(training -> training.getTrainingId().equals("TR-01")
                            && training.getSportName().equals("Lawn Tennis"));
            assertTest("39. Update sport name in enrollment and schedule", updatedReferences);
        } catch (Exception e) {
            failTest("39. Update sport name in enrollment and schedule", e);
        }

        // 40. Preserve records and clean active references after sport deletion
        try {
            boolean deleted = controller.deleteSport("SP101");
            boolean cancelled = controller.getTrainingSchedule().stream()
                    .anyMatch(training -> training.getTrainingId().equals("TR-01")
                            && Training.STATUS_CANCELLED.equals(training.getStatus()));
            boolean unenrolled = !controller.getMemberById("M1001").isEnrolledInSport("Lawn Tennis");
            assertTest("40. Cancel sessions and remove enrollment after sport deletion",
                    deleted && cancelled && unenrolled
                            && "Unassigned".equals(controller.getCoachById("CH01").getAssignedSport()));
        } catch (Exception e) {
            failTest("40. Cancel sessions and remove enrollment after sport deletion", e);
        }

        // 41. Keep active references consistent after coach deletion
        try {
            boolean deleted = controller.deleteCoach("CH03");
            boolean cancelled = controller.getTrainingSchedule().stream()
                    .anyMatch(training -> training.getTrainingId().equals("TR-02")
                            && Training.STATUS_CANCELLED.equals(training.getStatus()));
            assertTest("41. Unassign coach and cancel sessions after coach deletion",
                    deleted && cancelled
                            && "Unassigned".equals(controller.getSportById("SP103").getAssignedCoachName()));
        } catch (Exception e) {
            failTest("41. Unassign coach and cancel sessions after coach deletion", e);
        }

        // 42. Preserve payment history while cancelling a deleted member's sessions
        try {
            boolean deleted = controller.deleteMember("M1002");
            boolean cancelled = controller.getTrainingSchedule().stream()
                    .anyMatch(training -> training.getTrainingId().equals("TR-03")
                            && Training.STATUS_CANCELLED.equals(training.getStatus()));
            boolean paymentKept = controller.getPaymentHistory().stream()
                    .anyMatch(payment -> payment.getPaymentId().equals("PAY-1002"));
            assertTest("42. Preserve history and cancel sessions after member deletion",
                    deleted && controller.getMemberById("M1002") == null && cancelled && paymentKept);
        } catch (Exception e) {
            failTest("42. Preserve history and cancel sessions after member deletion", e);
        }

        // 43. Renew a membership and record its payment atomically
        try {
            int paymentCount = controller.getPaymentHistory().size();
            controller.renewMembership("M1001", Membership.PLAN_QUARTERLY, LocalDate.now());
            Member renewedMember = controller.getMemberById("M1001");
            Payment renewalPayment = controller.getPaymentHistory().getFirst();
            boolean indexed = controller.getMembersByExpiryTreeMap()
                    .get(renewedMember.getMembershipExpiryDate()).contains(renewedMember);
            assertTest("43. Renew membership, payment, and expiry index",
                    renewedMember.getMembership().getPlanName().equals(Membership.PLAN_QUARTERLY)
                            && controller.getPaymentHistory().size() == paymentCount + 1
                            && renewalPayment.getMemberId().equals("M1001")
                            && renewalPayment.getMembershipPlan().equals(Membership.PLAN_QUARTERLY)
                            && indexed);
        } catch (Exception e) {
            failTest("43. Renew membership, payment, and expiry index", e);
        }

        // 44. Reject an invalid renewal without changing membership or payment history
        try {
            Member member = controller.getMemberById("M1001");
            LocalDate previousExpiry = member.getMembershipExpiryDate();
            int paymentCount = controller.getPaymentHistory().size();
            boolean caught = false;
            try {
                controller.renewMembership("M1001", "Lifetime", LocalDate.now());
            } catch (InvalidMemberException e) {
                caught = true;
            }
            assertTest("44. Reject invalid renewal without partial changes",
                    caught && member.getMembershipExpiryDate().equals(previousExpiry)
                            && controller.getPaymentHistory().size() == paymentCount);
        } catch (Exception e) {
            failTest("44. Reject invalid renewal without partial changes", e);
        }

        // 45. Reject non-finite payment amounts
        try {
            boolean caught = false;
            try {
                controller.recordPayment(new Payment("PAY-NAN", "M1001", "Aarav Updated", Double.NaN,
                        LocalDate.now(), Membership.PLAN_MONTHLY, "Cash", "Paid"));
            } catch (Exception e) {
                caught = true;
            }
            assertTest("45. Reject non-finite payment amount", caught);
        } catch (Exception e) {
            failTest("45. Reject non-finite payment amount", e);
        }

        // 46. Reject payments for unknown members
        try {
            boolean caught = false;
            try {
                controller.recordPayment(new Payment("PAY-ORPHAN", "M-NOT-FOUND", "Unknown Member", 50.0,
                        LocalDate.now(), Membership.PLAN_MONTHLY, "Cash", "Paid"));
            } catch (Exception e) {
                caught = true;
            }
            assertTest("46. Reject payment for unknown member", caught);
        } catch (Exception e) {
            failTest("46. Reject payment for unknown member", e);
        }

        System.out.println("\n=================================================================");
        System.out.println(String.format("VERIFICATION RESULT: %d / %d TESTS PASSED", testsPassed, (testsPassed + testsFailed)));
        System.out.println("=================================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTest(String testName, boolean condition) {
        if (condition) {
            System.out.println("  ✓ PASS: " + testName);
            testsPassed++;
        } else {
            System.err.println("  ✗ FAIL: " + testName);
            testsFailed++;
        }
    }

    private static void failTest(String testName, Exception e) {
        System.err.println("  ✗ FAIL: " + testName + " with Exception: " + e.getMessage());
        testsFailed++;
    }
}
