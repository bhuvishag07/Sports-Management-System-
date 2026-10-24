package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Membership class representing a member's club subscription plan.
 */
public class Membership implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PLAN_MONTHLY = "Monthly";
    public static final String PLAN_QUARTERLY = "Quarterly";
    public static final String PLAN_HALF_YEARLY = "Half-Yearly";
    public static final String PLAN_YEARLY = "Yearly";

    public static final String[] AVAILABLE_PLANS = {
        PLAN_MONTHLY,
        PLAN_QUARTERLY,
        PLAN_HALF_YEARLY,
        PLAN_YEARLY
    };

    private String membershipId;
    private String planName;
    private int durationMonths;
    private double price;
    private LocalDate startDate;
    private LocalDate expiryDate;

    // Default Constructor
    public Membership() {
        this.startDate = LocalDate.now();
        this.planName = PLAN_MONTHLY;
        this.durationMonths = 1;
        this.price = 50.0;
        this.expiryDate = startDate.plusMonths(1);
    }

    // Parameterized Constructor
    public Membership(String membershipId, String planName, LocalDate startDate) {
        this.membershipId = membershipId;
        this.startDate = startDate != null ? startDate : LocalDate.now();
        applyPlanDetails(planName);
    }

    // Constructor with custom expiry and price
    public Membership(String membershipId, String planName, double price, LocalDate startDate, LocalDate expiryDate) {
        this.membershipId = membershipId;
        this.planName = normalizePlanName(planName);
        this.price = price;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.durationMonths = calculateDurationMonths(this.planName);
    }

    private void applyPlanDetails(String plan) {
        this.planName = normalizePlanName(plan);
        switch (this.planName) {
            case PLAN_QUARTERLY:
                this.durationMonths = 3;
                this.price = 135.0;
                this.expiryDate = startDate.plusMonths(3);
                break;
            case PLAN_HALF_YEARLY:
                this.durationMonths = 6;
                this.price = 250.0;
                this.expiryDate = startDate.plusMonths(6);
                break;
            case PLAN_YEARLY:
                this.durationMonths = 12;
                this.price = 450.0;
                this.expiryDate = startDate.plusYears(1);
                break;
            case PLAN_MONTHLY:
                this.durationMonths = 1;
                this.price = 50.0;
                this.expiryDate = startDate.plusMonths(1);
                break;
            default:
                throw new IllegalArgumentException("Unsupported membership plan: " + plan);
        }
    }

    public static boolean isValidPlan(String plan) {
        if (plan == null) {
            return false;
        }
        for (String availablePlan : AVAILABLE_PLANS) {
            if (availablePlan.equalsIgnoreCase(plan.trim())) {
                return true;
            }
        }
        return false;
    }

    private static String normalizePlanName(String plan) {
        if (plan != null) {
            for (String availablePlan : AVAILABLE_PLANS) {
                if (availablePlan.equalsIgnoreCase(plan.trim())) {
                    return availablePlan;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported membership plan: " + plan);
    }

    private int calculateDurationMonths(String plan) {
        switch (normalizePlanName(plan)) {
            case PLAN_QUARTERLY:
                return 3;
            case PLAN_HALF_YEARLY:
                return 6;
            case PLAN_YEARLY:
                return 12;
            case PLAN_MONTHLY:
                return 1;
            default:
                throw new IllegalArgumentException("Unsupported membership plan: " + plan);
        }
    }

    public static double getDefaultPriceForPlan(String plan) {
        switch (normalizePlanName(plan)) {
            case PLAN_QUARTERLY:
                return 135.0;
            case PLAN_HALF_YEARLY:
                return 250.0;
            case PLAN_YEARLY:
                return 450.0;
            case PLAN_MONTHLY:
                return 50.0;
            default:
                throw new IllegalArgumentException("Unsupported membership plan: " + plan);
        }
    }

    // Get status: Active, Expiring Soon (within 30 days), Expired
    public String getStatus() {
        if (expiryDate == null) {
            return "Inactive";
        }
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return "Expired";
        }
        long daysRemaining = ChronoUnit.DAYS.between(today, expiryDate);
        if (daysRemaining <= 30) {
            return "Expiring Soon (" + daysRemaining + "d)";
        }
        return "Active";
    }

    public boolean isExpired() {
        if (expiryDate == null) return true;
        return expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        LocalDate today = LocalDate.now();
        return !expiryDate.isBefore(today) && ChronoUnit.DAYS.between(today, expiryDate) <= 30;
    }

    public long getDaysRemaining() {
        if (expiryDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    // Getters and Setters
    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
        applyPlanDetails(planName);
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        if (startDate != null) {
            this.expiryDate = startDate.plusMonths(this.durationMonths > 0 ? this.durationMonths : 1);
        }
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return planName + " ($" + price + ") [Expires: " + expiryDate + " - " + getStatus() + "]";
    }
}
