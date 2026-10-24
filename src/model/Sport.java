package model;

import java.io.Serializable;

/**
 * Sport class representing an athletic discipline offered by the club.
 */
public class Sport implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String[] CATEGORIES = {"Indoor", "Outdoor", "Aquatics", "Racquet", "Fitness & Gym"};

    private String sportId;
    private String sportName;
    private String category;
    private String description;
    private String assignedCoachName;

    // Default Constructor
    public Sport() {
    }

    // Parameterized Constructor
    public Sport(String sportId, String sportName, String category, String description, String assignedCoachName) {
        this.sportId = sportId;
        this.sportName = sportName;
        this.category = category;
        this.description = description;
        this.assignedCoachName = assignedCoachName != null ? assignedCoachName : "Unassigned";
    }

    // Getters and Setters
    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignedCoachName() {
        return assignedCoachName;
    }

    public void setAssignedCoachName(String assignedCoachName) {
        this.assignedCoachName = assignedCoachName;
    }

    @Override
    public String toString() {
        return sportName + " (" + category + ")";
    }
}
