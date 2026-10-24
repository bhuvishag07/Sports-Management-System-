package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Serializable data container used for file persistence.
 * Holds all club records without requiring external database dependencies.
 */
public class ClubData implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Member> members;
    private ArrayList<Sport> sports;
    private ArrayList<Coach> coaches;
    private LinkedList<Training> trainingSchedule;
    private LinkedList<Training> trainingHistory;
    private LinkedList<Payment> paymentHistory;

    public ClubData() {
        this.members = new ArrayList<>();
        this.sports = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.trainingSchedule = new LinkedList<>();
        this.trainingHistory = new LinkedList<>();
        this.paymentHistory = new LinkedList<>();
    }

    public ClubData(ArrayList<Member> members, ArrayList<Sport> sports, ArrayList<Coach> coaches,
                    LinkedList<Training> trainingSchedule, LinkedList<Training> trainingHistory,
                    LinkedList<Payment> paymentHistory) {
        this.members = members;
        this.sports = sports;
        this.coaches = coaches;
        this.trainingSchedule = trainingSchedule;
        this.trainingHistory = trainingHistory;
        this.paymentHistory = paymentHistory;
    }

    // Getters and Setters
    public ArrayList<Member> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public ArrayList<Sport> getSports() {
        return sports;
    }

    public void setSports(ArrayList<Sport> sports) {
        this.sports = sports;
    }

    public ArrayList<Coach> getCoaches() {
        return coaches;
    }

    public void setCoaches(ArrayList<Coach> coaches) {
        this.coaches = coaches;
    }

    public LinkedList<Training> getTrainingSchedule() {
        return trainingSchedule;
    }

    public void setTrainingSchedule(LinkedList<Training> trainingSchedule) {
        this.trainingSchedule = trainingSchedule;
    }

    public LinkedList<Training> getTrainingHistory() {
        return trainingHistory;
    }

    public void setTrainingHistory(LinkedList<Training> trainingHistory) {
        this.trainingHistory = trainingHistory;
    }

    public LinkedList<Payment> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(LinkedList<Payment> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }
}
