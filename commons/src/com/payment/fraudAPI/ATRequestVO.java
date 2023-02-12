package com.payment.fraudAPI;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/16/14
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATRequestVO {

    public String merchant_id;
    String password;
    String session_id;

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }
}
