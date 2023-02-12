package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/17/14
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogoutRequestVO extends ATRequestVO
{

    public HashMap getHashMap()
    {
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id", this.merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("session_id", this.session_id);

        return requestMap;

    }

    public void setHashMap(HashMap requestHash)
    {
        this.merchant_id = (String) requestHash.get("merchant_id");
        this.password =  (String) requestHash.get("password");
        this.session_id = (String) requestHash.get("session_id");

    }

}
