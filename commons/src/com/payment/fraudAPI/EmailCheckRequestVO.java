package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/17/14
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmailCheckRequestVO extends ATRequestVO
{
    String emm_user_partial;
    String emm_domain;
    String emh;

    public String getEmm_user_partial()
    {
        return emm_user_partial;
    }

    public void setEmm_user_partial(String emm_user_partial)
    {
        this.emm_user_partial = emm_user_partial;
    }

    public String getEmm_domain()
    {
        return emm_domain;
    }

    public void setEmm_domain(String emm_domain)
    {
        this.emm_domain = emm_domain;
    }

    public String getEmh()
    {
        return emh;
    }

    public void setEmh(String emh)
    {
        this.emh = emh;
    }

    public HashMap getHashMap()
    {
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id", this.merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("emm_user_partial", this.emm_user_partial);
        requestMap.put("emm_domain", this.emm_domain);
        requestMap.put("emh", this.emh );

        return requestMap;

    }

    public void setHashMap(HashMap requestHash)
    {
        this.merchant_id = (String) requestHash.get("merchant_id");
        this.password =  (String) requestHash.get("password");
        this.emm_user_partial = (String) requestHash.get("emm_user_partial");
        this.emm_domain =  (String) requestHash.get("emm_domain");
        this.emh = (String) requestHash.get("emh");

    }


}
