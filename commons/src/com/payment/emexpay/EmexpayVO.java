package com.payment.emexpay;

import com.payment.common.core.Comm3DResponseVO;

/**
 * Created by Admin on 12/15/2017.
 */
public class EmexpayVO extends Comm3DResponseVO
{
    private String holder_name;
    private String token;
    private String stamp;
    private String brand;
    private String bank_code;
    private String rrn;
    private String ref_id;
    private String gateway_id;
    private String message;
    private String address_details;
    private String ve_status;

    public String getHolder_name()
    {
        return holder_name;
    }

    public void setHolder_name(String holder_name)
    {
        this.holder_name = holder_name;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getStamp()
    {
        return stamp;
    }

    public void setStamp(String stamp)
    {
        this.stamp = stamp;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getBank_code()
    {
        return bank_code;
    }

    public void setBank_code(String bank_code)
    {
        this.bank_code = bank_code;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getRef_id()
    {
        return ref_id;
    }

    public void setRef_id(String ref_id)
    {
        this.ref_id = ref_id;
    }

    public String getGateway_id()
    {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id)
    {
        this.gateway_id = gateway_id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getAddress_details()
    {
        return address_details;
    }

    public void setAddress_details(String address_details)
    {
        this.address_details = address_details;
    }

    public String getVe_status()
    {
        return ve_status;
    }

    public void setVe_status(String ve_status)
    {
        this.ve_status = ve_status;
    }
}
