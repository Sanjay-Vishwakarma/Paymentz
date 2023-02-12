package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/16/14
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginRequestVO extends ATRequestVO {

    String user_name;
    String user_number;
    String player_status;
    String reg_date;
    String reg_ip_address;
    String reg_device_id;
    String ip_address;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getPlayer_status() {
        return player_status;
    }

    public void setPlayer_status(String player_status) {
        this.player_status = player_status;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getReg_ip_address() {
        return reg_ip_address;
    }

    public void setReg_ip_address(String reg_ip_address) {
        this.reg_ip_address = reg_ip_address;
    }

    public String getReg_device_id() {
        return reg_device_id;
    }

    public void setReg_device_id(String reg_device_id) {
        this.reg_device_id = reg_device_id;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }




    public HashMap  getHashMap()
    {
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id", this.merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("user_name", this.user_name);
        requestMap.put("user_number", this.user_number);
        requestMap.put("player_status", this.player_status );
        requestMap.put("reg_date", this.reg_date);
        requestMap.put("reg_ip_address", this.reg_ip_address);
        requestMap.put("reg_device_id", this.reg_device_id);
        requestMap.put("ip_address", this.ip_address);
        return requestMap;

    }

    public void setHashMap(HashMap requestHash)
    {
        this.merchant_id = (String) requestHash.get("merchant_id");
        this.password =  (String) requestHash.get("password");
        this.user_name = (String) requestHash.get("user_name");
        this.user_number =  (String) requestHash.get("user_number");
        this.player_status = (String) requestHash.get("player_status");
        this.reg_date = (String) requestHash.get("reg_date");
        this.reg_ip_address =  (String) requestHash.get("reg_ip_address");
        this.reg_device_id = (String) requestHash.get("reg_device_id");
        this.reg_device_id = (String) requestHash.get("reg_device_id");
        this.ip_address = (String) requestHash.get("ip_address");
    }



}
