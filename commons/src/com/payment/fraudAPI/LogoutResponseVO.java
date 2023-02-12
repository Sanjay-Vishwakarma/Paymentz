package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/17/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogoutResponseVO extends ATResponseVO
{
    public HashMap getHashMap()
    {
        HashMap responseMap = new HashMap();

        responseMap.put("status", this.status);
        responseMap.put("description", this.description );

        return responseMap;

    }

    public void setHashMap(HashMap responseHash)
    {
        this.status = (Integer) responseHash.get("status");
        this.description = (String) responseHash.get("description");

    }



}
