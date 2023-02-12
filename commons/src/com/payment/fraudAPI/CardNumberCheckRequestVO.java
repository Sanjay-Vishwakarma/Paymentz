package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/17/14
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardNumberCheckRequestVO extends ATRequestVO
{
    String bin;
    String last_4;
    String reason;

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getLast_4() {
        return last_4;
    }

    public void setLast_4(String last_4) {
        this.last_4 = last_4;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public HashMap getHashMap()
    {
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id", this.merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("bin", this.bin);
        requestMap.put("last_4", this.last_4);
        requestMap.put("reason", this.reason );

        return requestMap;

    }

    public void setHashMap(HashMap requestHash)
    {
        this.merchant_id = (String) requestHash.get("merchant_id");
        this.password =  (String) requestHash.get("password");
        this.bin = (String) requestHash.get("bin");
        this.last_4 =  (String) requestHash.get("last_4");
        this.reason = (String) requestHash.get("reason");

    }



}
