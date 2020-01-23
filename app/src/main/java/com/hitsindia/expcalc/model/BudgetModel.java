package com.hitsindia.expcalc.model;

public class BudgetModel {

    private int intKey;
    private long strBudegetAmount;
    private String strBudegetCategory;
    private String strBudgetMonth;

    public BudgetModel() {
    }

    public BudgetModel(int intKey, long strBudegetAmount, String strBudegetCategory, String strBudgetMonth) {
        this.intKey = intKey;
        this.strBudegetAmount = strBudegetAmount;
        this.strBudegetCategory = strBudegetCategory;
        this.strBudgetMonth = strBudgetMonth;
    }

    public String getStrBudegetCategory() {
        return strBudegetCategory;
    }

    public void setStrBudegetCategory(String strBudegetCategory) {
        this.strBudegetCategory = strBudegetCategory;
    }

    public long getStrBudegetAmount() {
        return strBudegetAmount;
    }

    public void setStrBudegetAmount(long strBudegetAmount) {
        this.strBudegetAmount = strBudegetAmount;
    }

    public String getStrBudgetMonth() {
        return strBudgetMonth;
    }

    public void setStrBudgetMonth(String strBudgetMonth) {
        this.strBudgetMonth = strBudgetMonth;
    }

    public int getIntKey() {
        return intKey;
    }

    public void setIntKey(int intKey) {
        this.intKey = intKey;
    }
}
