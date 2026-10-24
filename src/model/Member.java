package model;

import java.time.LocalDate;
import java.util.HashSet;

/**
 * Member class representing a sports club member.
 * Inherits from Person.
 * Uses HashSet for managing unique sports enrollment.
 */
public class Member extends Person {
    private static final long serialVersionUID = 1L;

    private int age;
    private String gender;
    private String address;
    private Membership membership;
    
    // HashSet requirement: prevents duplicate sport enrollments
    private HashSet<String> enrolledSports;

    // Default Constructor
    public Member() {
        super();
        this.enrolledSports = new HashSet<>();
        this.membership = new Membership();
    }

    // Parameterized Constructor
    public Member(String id, String name, int age, String gender, String contact, 
                  String email, String address, Membership membership) {
        super(id, name, contact, email);
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.membership = membership != null ? membership : new Membership();
        this.enrolledSports = new HashSet<>();
    }

    // Sport enrollment using HashSet
    public boolean enrollSport(String sportName) {
        if (sportName == null || sportName.trim().isEmpty()) {
            return false;
        }
        return enrolledSports.add(sportName.trim());
    }

    public boolean unenrollSport(String sportName) {
        if (sportName == null) return false;
        return enrolledSports.remove(sportName.trim());
    }

    public boolean isEnrolledInSport(String sportName) {
        if (sportName == null) return false;
        return enrolledSports.contains(sportName.trim());
    }

    public HashSet<String> getEnrolledSports() {
        return enrolledSports;
    }

    public void setEnrolledSports(HashSet<String> enrolledSports) {
        this.enrolledSports = enrolledSports != null ? enrolledSports : new HashSet<>();
    }

    public String getEnrolledSportsString() {
        if (enrolledSports == null || enrolledSports.isEmpty()) {
            return "None";
        }
        return String.join(", ", enrolledSports);
    }

    public LocalDate getMembershipExpiryDate() {
        return membership != null ? membership.getExpiryDate() : LocalDate.MIN;
    }

    public String getMembershipStatus() {
        return membership != null ? membership.getStatus() : "No Plan";
    }

    // Overriding abstract methods from Person (Polymorphism)
    @Override
    public String getRole() {
        return "Member";
    }

    @Override
    public String getDetails() {
        return "Member [ID: " + getId() + ", Name: " + getName() + ", Age: " + age + 
               ", Gender: " + gender + ", Contact: " + getContact() + ", Email: " + getEmail() +
               ", Plan: " + (membership != null ? membership.getPlanName() : "None") +
               ", Status: " + getMembershipStatus() +
               ", Sports: " + getEnrolledSportsString() + "]";
    }

    // Getters and Setters
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    @Override
    public String toString() {
        return getId() + " - " + getName() + " (" + getMembershipStatus() + ")";
    }
}
