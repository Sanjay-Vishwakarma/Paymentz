package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/17/14
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardNumberCheckResponseVO extends ATResponseVO
{
    String check_id;
    String das;
    String dae;
    String cbn;
    String ren;
    String bla;

    public String getCheck_id() {
        return check_id;
    }

    public void setCheck_id(String check_id) {
        this.check_id = check_id;
    }

    public String getDas() {
        return das;
    }

    public void setDas(String das) {
        this.das = das;
    }

    public String getDae() {
        return dae;
    }

    public void setDae(String dae) {
        this.dae = dae;
    }

    public String getCbn() {
        return cbn;
    }

    public void setCbn(String cbn) {
        this.cbn = cbn;
    }

    public String getRen() {
        return ren;
    }

    public void setRen(String ren) {
        this.ren = ren;
    }

    public String getBla() {
        return bla;
    }

    public void setBla(String bla) {
        this.bla = bla;
    }

    public HashMap getHashMap()
    {
        HashMap responseMap = new HashMap();

        responseMap.put("check_id", this.check_id);
        responseMap.put("status", this.status);
        responseMap.put("description", this.description );
        responseMap.put("das", this.das);
        responseMap.put("dae", this.dae);
        responseMap.put("cbn", this.cbn);
        responseMap.put("ren", this.ren);
        responseMap.put("bla", this.bla);

        return responseMap;

    }

    public void setHashMap(HashMap responseHash)
    {
        this.check_id = (String)responseHash.get("check_id");
        this.status = (Integer) responseHash.get("status");
        this.description = (String) responseHash.get("description");
        this.das = (String)responseHash.get("das");
        this.dae = (String)responseHash.get("dae");
        this.cbn = (String)responseHash.get("cbn");
        this.ren = (String)responseHash.get("ren");
        this.bla = (String)responseHash.get("bla");

    }



}
