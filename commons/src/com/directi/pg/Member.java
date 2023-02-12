package com.directi.pg;

import java.io.Serializable;

//import PaymentClient.*;

public class Member implements Serializable
{
    public int memberid = -9999;
    public String activation = null;
    public String authenticate = null;
    public String address = null;
    public String telno = null;
    public String contactemails = null;
    public boolean isservice;
    public String partnerid = null;
    public boolean isMerchantInterfaceAccess;
    public String isApplicationManagerAccess;
    public String isCardRegistrationAllowed;
    public String isRecurringModuleAllowed;
    public String isSpeedOptionAccess;
    public String is_rest_whitelisted;
    public String secureKey;
    public String login;
    public String contactpersons;
    public String country;
    public String etoken;
    public String isemailverified;
    public String isMobileVerified;
    public String currentTheme;
    public String defaultTheme;
    public String multiCurrencySupport;
    public String upiSupportInvoice;
    public String upiQRSupportIinvoice;
    public String paybylinkSpportInvoice;
    public String AEPSSupportInvoice;
    MemberUser memberUser;
    MerchantModuleAccessVO merchantModuleAccessVO;
    private String accountId = null;

    public MemberUser getMemberUser()
    {
        return memberUser;
    }

    public void setMemberUser(MemberUser memberUser)
    {
        this.memberUser = memberUser;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accId)
    {
        accountId = accId;
    }

    public String getSecureKey()
    {
        return secureKey;
    }

    public void setSecureKey(String secureKey)
    {
        this.secureKey = secureKey;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getContactpersons()
    {
        return contactpersons;
    }

    public void setContactpersons(String contactpersons)
    {
        this.contactpersons = contactpersons;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }


    public String getEtoken()
    {
        return etoken;
    }

    public void setEtoken(String etoken)
    {
        this.etoken = etoken;
    }

    public String getDefaultTheme()
    {
        return defaultTheme;
    }

    public void setDefaultTheme(String defaultTheme)
    {
        this.defaultTheme = defaultTheme;
    }

    public String getCurrentTheme()
    {
        return currentTheme;
    }

    public void setCurrentTheme(String currentTheme)
    {
        this.currentTheme = currentTheme;
    }

    public String getIsMobileVerified()
    {
        return isMobileVerified;
    }

    public void setIsMobileVerified(String isMobileVerified)
    {
        this.isMobileVerified = isMobileVerified;
    }

    public MerchantModuleAccessVO getMerchantModuleAccessVO()
    {
        return merchantModuleAccessVO;
    }

    public void setMerchantModuleAccessVO(MerchantModuleAccessVO merchantModuleAccessVO)
    {
        this.merchantModuleAccessVO = merchantModuleAccessVO;
    }

    public String getUpiSupportInvoice()
    {
        return upiSupportInvoice;
    }

    public void setUpiSupportInvoice(String upiSupportInvoice)
    {
        this.upiSupportInvoice = upiSupportInvoice;
    }

    public String getUpiQRSupportIinvoice()
    {
        return upiQRSupportIinvoice;
    }

    public void setUpiQRSupportIinvoice(String upiQRSupportIinvoice)
    {
        this.upiQRSupportIinvoice = upiQRSupportIinvoice;
    }

    public String getPaybylinkSpportInvoice()
    {
        return paybylinkSpportInvoice;
    }

    public void setPaybylinkSpportInvoice(String paybylinkSpportInvoice)
    {
        this.paybylinkSpportInvoice = paybylinkSpportInvoice;
    }

    public String getAEPSSupportInvoice()
    {
        return AEPSSupportInvoice;
    }

    public void setAEPSSupportInvoice(String AEPSSupportInvoice)
    {
        this.AEPSSupportInvoice = AEPSSupportInvoice;
    }
}