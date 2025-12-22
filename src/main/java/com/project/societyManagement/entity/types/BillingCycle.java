package com.project.societyManagement.entity.types;

public enum BillingCycle {
    MONTHLY(1),
    QUARTERLY(3),
    SEMI_ANNUAL(6),
    ANNUAL(12),
    BIENNIAL(24);

    private final int months;

    BillingCycle(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }

}