package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Training class representing a coaching or practice session.
 * Demonstrates meaningful use of Java Array for fixed training time slots.
 */
public class Training implements Serializable {
    private static final long serialVersionUID = 1L;

    // Array requirement: Fixed training time slots
    public static final String[] FIXED_SLOTS = {
        "06:00 AM - 07:30 AM (Slot 1)",
        "07:30 AM - 09:00 AM (Slot 2)",
        "04:00 PM - 05:30 PM (Slot 3)",
        "05:30 PM - 07:00 PM (Slot 4)",
        "07:00 PM - 08:30 PM (Slot 5)"
    };

    public static final String STATUS_SCHEDULED = "Scheduled";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String trainingId;
    private String sportName;
    private String coachName;
    private String memberId;
    private String memberName;
    private LocalDate date;
    private String trainingSlot;
    private String status;
    private String notes;

    // Default Constructor
    public Training() {
        this.status = STATUS_SCHEDULED;
    }

    // Parameterized Constructor
    public Training(String trainingId, String sportName, String coachName, 
                    String memberId, String memberName, LocalDate date, 
                    String trainingSlot, String notes) {
        this.trainingId = trainingId;
        this.sportName = sportName;
        this.coachName = coachName;
        this.memberId = memberId;
        this.memberName = memberName;
        this.date = date;
        this.trainingSlot = trainingSlot;
        this.notes = notes != null ? notes : "";
        this.status = STATUS_SCHEDULED;
    }

    // Method to validate slot against Array of fixed slots
    public static boolean isValidSlot(String slot) {
        if (slot == null) return false;
        for (String s : FIXED_SLOTS) {
            if (s.equalsIgnoreCase(slot.trim())) {
                return true;
            }
        }
        return false;
    }

    // Getters and Setters
    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTrainingSlot() {
        return trainingSlot;
    }

    public void setTrainingSlot(String trainingSlot) {
        this.trainingSlot = trainingSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return trainingId + ": " + sportName + " on " + date + " [" + trainingSlot + "] (" + status + ")";
    }
}
