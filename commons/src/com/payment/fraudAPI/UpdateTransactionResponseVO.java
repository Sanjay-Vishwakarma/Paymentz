package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/16/14
 * Time: 10:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateTransactionResponseVO extends ATResponseVO
{
    String internal_trans_id;

    public String getInternal_trans_id() {
        return internal_trans_id;
    }

    public void setInternal_trans_id(String internal_trans_id) {
        this.internal_trans_id = internal_trans_id;
    }
    public HashMap getHashMap()
    {
        HashMap responseMap = new HashMap();

        responseMap.put("internal_trans_id", this.internal_trans_id);
        responseMap.put("status", this.status);
        responseMap.put("description", this.description );

        return responseMap;

    }
    public void setHashMap(HashMap responseHash)
    {
        this.internal_trans_id = (String)responseHash.get("internal_trans_id");
        this.status = (Integer) responseHash.get("status");
        this.description = (String) responseHash.get("description");

    }
}
