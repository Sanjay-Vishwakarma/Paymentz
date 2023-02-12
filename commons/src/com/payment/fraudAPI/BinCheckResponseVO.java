package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/17/14
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinCheckResponseVO extends ATResponseVO
{
    String check_id;
    //Bank Name
    String ban;
    String iso;
    String cou;
    String cat;
    String typ;

    public String getCheck_id() {
        return check_id;
    }

    public void setCheck_id(String check_id) {
        this.check_id = check_id;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getCou() {
        return cou;
    }

    public void setCou(String cou) {
        this.cou = cou;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }


    public HashMap getHashMap()
    {
        HashMap responseMap = new HashMap();

        responseMap.put("check_id", this.check_id);
        responseMap.put("status", this.status);
        responseMap.put("description", this.description );
        responseMap.put("ban", this.ban);
        responseMap.put("iso", this.iso);
        responseMap.put("cou", this.cou);
        responseMap.put("cat", this.cat);
        responseMap.put("typ", this.typ);


        return responseMap;

    }

    public void setHashMap(HashMap responseHash)
    {
        this.check_id = (String) responseHash.get("check_id");
        this.status = (Integer) responseHash.get("status");
        this.description = (String) responseHash.get("description");
        this.ban = (String) responseHash.get("ban");
        this.iso = (String) responseHash.get("iso");
        this.cou = (String) responseHash.get("cou");
        this.cat = (String) responseHash.get("cat");
        this.typ = (String) responseHash.get("typ");

    }


}
