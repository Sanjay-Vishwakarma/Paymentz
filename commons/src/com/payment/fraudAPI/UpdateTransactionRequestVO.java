package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/16/14
 * Time: 9:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateTransactionRequestVO extends ATRequestVO
{

    String trans_id;
    String internal_trans_id;
    String processor;
    String status;
    String reason;

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getInternal_trans_id() {
        return internal_trans_id;
    }

    public void setInternal_trans_id(String internal_trans_id) {
        this.internal_trans_id = internal_trans_id;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getUndefined() {
        return status;
    }

    public void setUndefined(String undefined) {
        status = undefined;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public HashMap  getHashMap()
    {
        HashMap requestMap = new HashMap();
        requestMap.put("session_id",this.session_id);
        requestMap.put("merchant_id", this.merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("trans_id", this.trans_id);
        requestMap.put("internal_trans_id", this.internal_trans_id);
        requestMap.put("processor", this.processor );
        requestMap.put("status", this.status);
        requestMap.put("reason", this.reason);
        return requestMap;

    }
    public void setHashMap(HashMap requestHash)
    {
        this.session_id = (String) requestHash.get("session_id");
        this.merchant_id = (String) requestHash.get("merchant_id");
        this.password =  (String) requestHash.get("password");
        this.trans_id = (String) requestHash.get("trans_id");
        this.internal_trans_id =  (String) requestHash.get("internal_trans_id");
        this.processor = (String) requestHash.get("processor");
        this.status = (String) requestHash.get("status");
        this.reason =  (String) requestHash.get("reason");

    }


}
