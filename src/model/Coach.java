package model;

/**
 * Coach class representing a sports club trainer/coach.
 * Inherits from Person.
 */
public class Coach extends Person {
    private static final long serialVersionUID = 1L;

    private String specialization;
    private String assignedSport;
    private int experienceYears;

    // Default Constructor
    public Coach() {
        super();
    }

    // Parameterized Constructor
    public Coach(String id, String name, String contact, String email, 
                 String specialization, String assignedSport, int experienceYears) {
        super(id, name, contact, email);
        this.specialization = specialization;
        this.assignedSport = assignedSport;
        this.experienceYears = experienceYears;
    }

    // Overriding abstract methods from Person (Polymorphism)
    @Override
    public String getRole() {
        return "Coach";
    }

    @Override
    public String getDetails() {
        return "Coach [ID: " + getId() + ", Name: " + getName() + 
               ", Sport: " + assignedSport + ", Specialization: " + specialization + 
               ", Experience: " + experienceYears + " yrs, Contact: " + getContact() + "]";
    }

    // Getters and Setters
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAssignedSport() {
        return assignedSport;
    }

    public void setAssignedSport(String assignedSport) {
        this.assignedSport = assignedSport;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    @Override
    public String toString() {
        return getId() + " - " + getName() + " (" + assignedSport + ")";
    }
}
