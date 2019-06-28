package com.android.pharmware;

/**
 * Created by corei3 on 19-07-2017.
 */

public class ListModel {

    private String med;
    private String pr;
    private String qty;
    private String amount;
    private String batch;
    private String expdate;
    private String key;
    private String orgqty;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String unit;

    public ListModel(String med, String pr, String qty, String amount, String batch, String expdate, String key,
            String orgqty, String unit) {

        this.med = med;
        this.amount = amount;
        this.qty = qty;
        this.pr = pr;
        this.batch = batch;
        this.expdate = expdate;
        this.key = key;
        this.orgqty = orgqty;
        this.unit = unit;
    }

    public String getOrgqty() {
        return orgqty;
    }

    public void setOrgqty(String orgqty) {
        this.orgqty = orgqty;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getMed() {
        return med;
    }

    public void setMed(String med) {
        this.med = med;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }
}
