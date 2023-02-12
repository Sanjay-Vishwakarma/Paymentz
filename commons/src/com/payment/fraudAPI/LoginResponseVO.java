package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/16/14
 * Time: 7:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginResponseVO extends ATResponseVO
{
    String session_id;

    public String getSession_id()
    {
        return session_id;
    }

    public void setSession_id(String session_id)
    {
        this.session_id = session_id;
    }

    public HashMap getHashMap()
    {
        HashMap responseMap = new HashMap();

        responseMap.put("session_id", this.session_id);
        responseMap.put("status", this.status);
        responseMap.put("description", this.description );

        return responseMap;

    }

    public void setHashMap(HashMap responseHash)
    {
        this.session_id = (String)responseHash.get("session_id");
        this.status = (Integer) responseHash.get("status");
        this.description = (String) responseHash.get("description");

    }

}
