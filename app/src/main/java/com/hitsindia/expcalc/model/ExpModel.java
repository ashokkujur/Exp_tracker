package com.hitsindia.expcalc.model;

public class ExpModel {

    private int intPrimaryKey;
    private String strDesc;
    private long lngAmount;
    private String dtInvest;
    private String strCategory;
    private String strAttachUrl;

    public ExpModel() {
    }

    public ExpModel(int intPrimaryKey, String strDesc, long lngAmount, String dtInvest, String strCategory, String strAttachUrl) {
        this.intPrimaryKey = intPrimaryKey;
        this.strDesc = strDesc;
        this.lngAmount = lngAmount;
        this.dtInvest = dtInvest;
        this.strCategory = strCategory;
        this.strAttachUrl = strAttachUrl;
    }

    public ExpModel(String strDesc, long lngAmount, String dtInvest, String strCategory, String strAttachUrl) {
        this.strDesc = strDesc;
        this.lngAmount = lngAmount;
        this.dtInvest = dtInvest;
        this.strCategory = strCategory;
        this.strAttachUrl = strAttachUrl;
    }

    public int getIntPrimaryKey() {
        return intPrimaryKey;
    }

    public void setIntPrimaryKey(int intPrimaryKey) {
        this.intPrimaryKey = intPrimaryKey;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public long getLngAmount() {
        return lngAmount;
    }

    public void setLngAmount(long lngAmount) {
        this.lngAmount = lngAmount;
    }

    public String getDtInvest() {
        return dtInvest;
    }

    public void setDtInvest(String dtInvest) {
        this.dtInvest = dtInvest;
    }

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrAttachUrl() {
        return strAttachUrl;
    }

    public void setStrAttachUrl(String strAttachUrl) {
        this.strAttachUrl = strAttachUrl;
    }
}
