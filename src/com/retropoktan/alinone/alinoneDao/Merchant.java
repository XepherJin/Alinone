package com.retropoktan.alinone.alinoneDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MERCHANT.
 */
public class Merchant {

    /** Not-null value. */
    private String merchantID;
    /** Not-null value. */
    private String merchantName;
    private int orderNum;

    public Merchant() {
    }

    public Merchant(String merchantID) {
        this.merchantID = merchantID;
    }

    public Merchant(String merchantID, String merchantName, int orderNum) {
        this.merchantID = merchantID;
        this.merchantName = merchantName;
        this.orderNum = orderNum;
    }

    /** Not-null value. */
    public String getMerchantID() {
        return merchantID;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    /** Not-null value. */
    public String getMerchantName() {
        return merchantName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

}
