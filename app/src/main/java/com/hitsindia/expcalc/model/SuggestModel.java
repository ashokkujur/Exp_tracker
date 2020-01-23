package com.hitsindia.expcalc.model;

public class SuggestModel {
    private int intPrimaryKey;
    private String strSuggest;
    private int intNoOfHit = 0;

    public SuggestModel() {
    }

    public SuggestModel(int intPrimaryKey, String strSuggest, int intNoOfHit) {
        this.intPrimaryKey = intPrimaryKey;
        this.strSuggest = strSuggest;
        this.intNoOfHit = intNoOfHit;
    }

    public int getIntPrimaryKey() {
        return intPrimaryKey;
    }

    public void setIntPrimaryKey(int intPrimaryKey) {
        this.intPrimaryKey = intPrimaryKey;
    }

    public String getStrSuggest() {
        return strSuggest;
    }

    public void setStrSuggest(String strSuggest) {
        this.strSuggest = strSuggest;
    }

    public int getIntNoOfHit() {
        return intNoOfHit;
    }

    public void setIntNoOfHit(int intNoOfHit) {
        this.intNoOfHit = intNoOfHit;
    }
}
