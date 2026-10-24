package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Payment class representing a fee payment for club membership.
 * Stored in LinkedList for maintaining payment history.
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String[] PAYMENT_METHODS = {"Cash", "Credit Card", "Debit Card", "UPI / GPay", "Net Banking"};

    private String paymentId;
    private String memberId;
    private String memberName;
    private double amount;
    private LocalDate paymentDate;
    private String membershipPlan;
    private String paymentMethod;
    private String paymentStatus;

    // Default Constructor
    public Payment() {
        this.paymentDate = LocalDate.now();
        this.paymentStatus = "Paid";
        this.paymentMethod = "Cash";
    }

    // Parameterized Constructor
    public Payment(String paymentId, String memberId, String memberName, 
                   double amount, LocalDate paymentDate, String membershipPlan, 
                   String paymentMethod, String paymentStatus) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.amount = amount;
        this.paymentDate = paymentDate != null ? paymentDate : LocalDate.now();
        this.membershipPlan = membershipPlan;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "Cash";
        this.paymentStatus = paymentStatus != null ? paymentStatus : "Paid";
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(String membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return paymentId + " - " + memberName + " ($" + amount + " via " + paymentMethod + " on " + paymentDate + ")";
    }
}
