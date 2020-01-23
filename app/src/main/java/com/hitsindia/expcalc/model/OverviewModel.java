package com.hitsindia.expcalc.model;

public class OverviewModel {
    private String strToday;
    private String strThisWekk;
    private String strThisMonth;

    public OverviewModel() {
    }

    public OverviewModel(String strToday, String strThisWekk, String strThisMonth) {
        this.strToday = strToday;
        this.strThisWekk = strThisWekk;
        this.strThisMonth = strThisMonth;
    }

    public String getStrToday() {
        return strToday;
    }

    public void setStrToday(String strToday) {
        this.strToday = strToday;
    }

    public String getStrThisWekk() {
        return strThisWekk;
    }

    public void setStrThisWekk(String strThisWekk) {
        this.strThisWekk = strThisWekk;
    }

    public String getStrThisMonth() {
        return strThisMonth;
    }

    public void setStrThisMonth(String strThisMonth) {
        this.strThisMonth = strThisMonth;
    }
}
