package com.manager.vo;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 4/3/15
 * Time: 03:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenRequestVO extends CommonValidatorVO
{
    String memberId;
    String trackingId;
    String description;
    String cardholderEmail;
    String cardholderId;
    String terminalId;
    String partnerId;
    String generatedBy;
    String registrationToken;
    String bankAccountId;
    String tokenId;
    String registrationGeneratedBy;
    String cardNum;
    String expMonth;
    String cvv;
    String firstName;
    String street;
    String city;
    String country;
    String zipCode;
    String state;
    String phone;
    String telnocc;
    String isMerchantCardRegistration;
    String redirectUrl;
    String notificationUrl;
    String customerId;
    String isActive;

    public String getTokenId()
    {
        return tokenId;
    }

    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }

    public String getBankAccountId()
    {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId)
    {
        this.bankAccountId = bankAccountId;
    }

    public String getGeneratedBy()
    {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy)
    {
        this.generatedBy = generatedBy;
    }

    public String getRaisedBy()
    {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy)
    {
        this.raisedBy = raisedBy;
    }

    String raisedBy;

    CommCardDetailsVO commCardDetailsVO;
    GenericAddressDetailsVO addressDetailsVO;
    BankAccountVO bankAccountVO;

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getCardholderEmail()
    {
        return cardholderEmail;
    }
    public void setCardholderEmail(String cardholderEmail)
    {
        this.cardholderEmail = cardholderEmail;
    }
    public CommCardDetailsVO getCommCardDetailsVO()
    {
        return commCardDetailsVO;
    }
    public void setCommCardDetailsVO(CommCardDetailsVO commCardDetailsVO)
    {
        this.commCardDetailsVO = commCardDetailsVO;
    }
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public GenericAddressDetailsVO getAddressDetailsVO()
    {
        return addressDetailsVO;
    }

    public void setAddressDetailsVO(GenericAddressDetailsVO addressDetailsVO)
    {
        this.addressDetailsVO = addressDetailsVO;
    }

    public String getCardholderId()
    {
        return cardholderId;
    }

    public void setCardholderId(String cardholderId)
    {
        this.cardholderId = cardholderId;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    String accnum;

    public String getAccnum()
    {
        return accnum;
    }

    public void setAccnum(String accnum)
    {
        this.accnum = accnum;
    }

    String tokenAccountId;

    public String getTokenAccountId()
    {
        return tokenAccountId;
    }

    public void setTokenAccountId(String tokenAccountId)
    {
        this.tokenAccountId = tokenAccountId;
    }

    public BankAccountVO getBankAccountVO()
    {
        return bankAccountVO;
    }

    public void setBankAccountVO(BankAccountVO bankAccountVO)
    {
        this.bankAccountVO = bankAccountVO;
    }

    public String getRegistrationToken()
    {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken)
    {
        this.registrationToken = registrationToken;
    }

    public String getRegistrationGeneratedBy()
    {
        return registrationGeneratedBy;
    }

    public void setRegistrationGeneratedBy(String registrationGeneratedBy)
    {
        this.registrationGeneratedBy = registrationGeneratedBy;
    }

    public String getCardNum()
    {
        return cardNum;
    }

    public void setCardNum(String cardNum)
    {
        this.cardNum = cardNum;
    }

    public String getExpMonth()
    {
        return expMonth;
    }

    public void setExpMonth(String expMonth)
    {
        this.expMonth = expMonth;
    }

    public String getCvv()
    {
        return cvv;
    }

    public void setCvv(String cvv)
    {
        this.cvv = cvv;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getTelnocc()
    {
        return telnocc;
    }

    public void setTelnocc(String telnocc)
    {
        this.telnocc = telnocc;
    }

    public String getIsMerchantCardRegistration()
    {
        return isMerchantCardRegistration;
    }

    public void setIsMerchantCardRegistration(String isMerchantCardRegistration)
    {
        this.isMerchantCardRegistration = isMerchantCardRegistration;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    @Override
    public String getCustomerId()
    {
        return customerId;
    }

    @Override
    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }
}
