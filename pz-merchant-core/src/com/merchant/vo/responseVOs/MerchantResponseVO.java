package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 18-01-2022.
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantResponseVO
{

    @XmlElement(name="memberId")
    String memberId;

    @XmlElement(name="result")
    Result result;

    @XmlElement (name="timestamp")
    String timestamp;

    @XmlElement (name="email")
    String email;

    @XmlElement (name="telno")
    String telno;

    @XmlElement (name="otp")
    String otp;

    @XmlElement (name="mobilenumber")
    String mobilenumber;

//    @XmlElement(name="paymentId")
//    String paymentId;
//
//    @XmlElement(name="partnerId")
//    String partnerId;


//    @XmlElement(name = "customerId")
//    String customerId;



//    @XmlElement (name="secureKey")
//    String secureKey;



//    @XmlElement (name="login")
//    String login;

//    @XmlElement (name="role")
//    String role;
//
//    @XmlElement (name="contactPerson")
//    String contactPerson;
//
//    @XmlElement (name="country")
//    String country;
//
//    @XmlElement (name="LoginName")
//    String LoginName;

//    @XmlElement (name="AuthToken")
//    String authToken;



//    @XmlElement (name="isemailverified")
//    String isemailverified;
//
//    @XmlElement (name="isMobileVerified")
//    String isMobileVerified;

//    @XmlElement (name="isActive")
//    String isActive;
//
//    @XmlElement (name="virtualCheckout")
//    String virtualCheckout;
//
//    @XmlElement (name="isMobileRequiredForVC")
//    String isMobileRequiredForVC;

//    @XmlElement (name="isEmailRequiredForVC")
//    String isEmailRequiredForVC;
//
//    @XmlElement (name="isRefund")
//    String isRefund;

//    @XmlElement (name="isPartialRefund")
//    String isPartialRefund;
//
//    @XmlElement (name="isMultipleRefund")
//    String isMultipleRefund;
//
//    @XmlElement (name="isShareAllowed")
//    String isShareAllowed;
//
//    @XmlElement (name="isSignatureAllowed")
//    String isSignatureAllowed;
//
//    @XmlElement (name="isSaveReceiptAllowed")
//    String isSaveReceiptAllowed;
//
//    @XmlElement (name="defaultLanguage")
//    String defaultLanguage;

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

/*    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }*/

/*    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }*/

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

   /* public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }*/

    public Result getResult()
    {
        return result;
    }

//    public void setResult(Result result)
//    {
//        this.result = result;
//    }
//
//    public String getSecureKey()
//    {
//        return secureKey;
//    }
//
//    public void setSecureKey(String secureKey)
//    {
//        this.secureKey = secureKey;
//    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
    }

 /*   public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getLoginName()
    {
        return LoginName;
    }

    public void setLoginName(String loginName)
    {
        LoginName = loginName;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }*/

    public String getMobilenumber()
    {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber)
    {
        this.mobilenumber = mobilenumber;
    }


    /*public String getIsemailverified()
    {
        return isemailverified;
    }

    public void setIsemailverified(String isemailverified)
    {
        this.isemailverified = isemailverified;
    }

    public String getIsMobileVerified()
    {
        return isMobileVerified;
    }

    public void setIsMobileVerified(String isMobileVerified)
    {
        this.isMobileVerified = isMobileVerified;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getVirtualCheckout()
    {
        return virtualCheckout;
    }

    public void setVirtualCheckout(String virtualCheckout)
    {
        this.virtualCheckout = virtualCheckout;
    }

    public String getIsMobileRequiredForVC()
    {
        return isMobileRequiredForVC;
    }

    public void setIsMobileRequiredForVC(String isMobileRequiredForVC)
    {
        this.isMobileRequiredForVC = isMobileRequiredForVC;
    }

    public String getIsEmailRequiredForVC()
    {
        return isEmailRequiredForVC;
    }

    public void setIsEmailRequiredForVC(String isEmailRequiredForVC)
    {
        this.isEmailRequiredForVC = isEmailRequiredForVC;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getIsPartialRefund()
    {
        return isPartialRefund;
    }

    public void setIsPartialRefund(String isPartialRefund)
    {
        this.isPartialRefund = isPartialRefund;
    }

    public String getIsMultipleRefund()
    {
        return isMultipleRefund;
    }

    public void setIsMultipleRefund(String isMultipleRefund)
    {
        this.isMultipleRefund = isMultipleRefund;
    }

    public String getIsRefund()
    {
        return isRefund;
    }

    public void setIsRefund(String isRefund)
    {
        this.isRefund = isRefund;
    }

    public String getIsShareAllowed() { return isShareAllowed; }

    public void setIsShareAllowed(String isShareAllowed) { this.isShareAllowed = isShareAllowed; }

    public String getIsSignatureAllowed() { return isSignatureAllowed; }

    public void setIsSignatureAllowed(String isSignatureAllowed) { this.isSignatureAllowed = isSignatureAllowed; }

    public String getIsSaveReceiptAllowed() { return isSaveReceiptAllowed;}

    public void setIsSaveReceiptAllowed(String isSaveReceiptAllowed) { this.isSaveReceiptAllowed = isSaveReceiptAllowed;}

    public String getDefaultLanguage() { return defaultLanguage; }

    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage;}*/

    public void setResult(Result result)
    {
        this.result = result;
    }

    public String getOtp()
    {
        return otp;
    }

    public void setOtp(String otp)
    {
        this.otp = otp;
    }

    @Override
    public String toString()
    {
        return "MerchantResponseVO{" +
                "memberId='" + memberId + '\'' +
                ", result=" + result +
                ", timestamp='" + timestamp + '\'' +
                ", email='" + email + '\'' +
                ", telno='" + telno + '\'' +
                ", otp='" + otp + '\'' +
                ", mobilenumber='" + mobilenumber + '\'' +
                '}';
    }
}