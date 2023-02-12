package com.vo.responseVOs;

import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.sun.jersey.api.core.InjectParam;
import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by NIKET on 2/13/2016.
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationManagerResponse

{
    @XmlElement(name="paymentId")
    String paymentId;

    @XmlElement(name="partnerId")
    String partnerId;

    @XmlElement(name="memberId")
    String memberId;

    @XmlElement(name = "customerId")
    String customerId;

    @XmlElement(name="result")
    Result result;

    @XmlElement (name="timestamp")
    String timestamp;

    @XmlElement (name="secureKey")
    String secureKey;

    @XmlElement (name="email")
    String email;

    @XmlElement (name="telno")
    String telno;

    @XmlElement (name="login")
    String login;

    @XmlElement (name="contactPerson")
    String contactPerson;

    @XmlElement (name="country")
    String country;

    @XmlElement (name="LoginName")
    String LoginName;

    @XmlElement (name="AuthToken")
    String authToken;

    @XmlElement (name="partnerKey")
    String partnerKey;

    @XmlElement (name="mobilenumber")
    String mobilenumber;

    @FormParam("status")
    private String status;

    @FormParam("statusDescription")
    private String statusDescription;

    @InjectParam("MerchantApplicationDetails")
    private ApplicationManagerVO applicationManagerVO;

    @XmlElement(name="checksum")
    String checksum;

    @XmlElement(name="random")
    String random;

    @XmlElement(name="mode")
    String mode;

    @XmlElement(name = "result")
    private List<ValidationError> validationError;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusDescription()
    {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription)
    {
        this.statusDescription = statusDescription;
    }

    public ApplicationManagerVO getApplicationManagerVO()
    {
        return applicationManagerVO;
    }

    public void setApplicationManagerVO(ApplicationManagerVO applicationManagerVO)
    {
        this.applicationManagerVO = applicationManagerVO;
    }

    public List<ValidationError> getValidationError()
    {
        return validationError;
    }

    public void setValidationError(List<ValidationError> validationError)
    {
        this.validationError = validationError;
    }


    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getMobilenumber()
    {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber)
    {
        this.mobilenumber = mobilenumber;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public Result getResult()
    {
        return result;
    }

    public void setResult(Result result)
    {
        this.result = result;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getSecureKey()
    {
        return secureKey;
    }

    public void setSecureKey(String secureKey)
    {
        this.secureKey = secureKey;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
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
    }


    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public String getRandom()
    {
        return random;
    }

    public void setRandom(String random)
    {
        this.random = random;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getPartnerKey()
    {
        return partnerKey;
    }

    public void setPartnerKey(String partnerKey)
    {
        this.partnerKey = partnerKey;
    }
}
