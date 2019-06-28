package com.android.pharmware;

/**
 * Created by corei3 on 04-07-2017.
 */

public class Model {

    private String einame;
    private String ebatchNo;
    private String eqty;
    private String eexpdate;
    private String emrp;
    private String esp;
    private String ecName;
    private String ecode;
    private String ecp;
    private String edesc;
    private String itemId;
    private String eunit;

    public String getEunit() {
        return eunit;
    }

    public void setEunit(String eunit) {
        this.eunit = eunit;
    }

    public Model() {

    }

    public Model(String einame, String ebatchNo, String eqty, String eexpdate, String emrp, String esp, String ecName,
            String ecode, String ecp, String edesc, String itemId, String eunit) {
        this.ebatchNo = ebatchNo;
        this.eexpdate = eexpdate;
        this.einame = einame;
        this.emrp = emrp;
        this.eqty = eqty;
        this.esp = esp;
        this.ecName = ecName;
        this.ecode = ecode;
        this.ecp = ecp;
        this.itemId = itemId;
        this.edesc = edesc;
        this.eunit = eunit;
    }

    public Model(String einame, String ebatchNo, String eqty, String eexpdate, String emrp, String esp, String ecName,
            String ecode, String ecp, String edesc) {
        this.ebatchNo = ebatchNo;
        this.eexpdate = eexpdate;
        this.einame = einame;
        this.emrp = emrp;
        this.eqty = eqty;
        this.esp = esp;
        this.ecName = ecName;
        this.edesc = edesc;
    }

    public String getEdesc() {
        return edesc;
    }

    public void setEdesc(String edesc) {
        this.edesc = edesc;
    }

    public String getEcode() {
        return ecode;
    }

    public void setEcode(String ecode) {
        this.ecode = ecode;
    }

    public String getEcp() {
        return ecp;
    }

    public void setEcp(String ecp) {
        this.ecp = ecp;
    }

    public String getEcName() {
        return ecName;
    }

    public void setEcName(String ecName) {
        this.ecName = ecName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getEiname() {
        return einame;
    }

    public void setEiname(String einame) {
        this.einame = einame;
    }

    public String getEsp() {
        return esp;
    }

    public void setEsp(String esp) {
        this.esp = esp;
    }

    public String getEmrp() {
        return emrp;
    }

    public void setEmrp(String emrp) {
        this.emrp = emrp;
    }

    public String getEexpdate() {
        return eexpdate;
    }

    public void setEexpdate(String eexpdate) {
        this.eexpdate = eexpdate;
    }

    public String getEqty() {
        return eqty;
    }

    public void setEqty(String eqty) {
        this.eqty = eqty;
    }

    public String getEbatchNo() {
        return ebatchNo;
    }

    public void setEbatchNo(String ebatchNo) {
        this.ebatchNo = ebatchNo;
    }
}
