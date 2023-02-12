package com.transaction.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 5/28/15
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
/*@XmlType(name = "DirectTransactionRequest", propOrder = {"toId","toType","amount","description","redirectURL","checksum","cardnumber","cvv","expiry_month","expiry_year","birthDate","language","firstName","lastName"
,"terminalId","paymentType","cardType","orderDescription","ipAddress","countryCode","city","state","street","zip","telNo","telNoCC","emailAddress","cardHolderIpAddress","ssn","reservedField1","reservedField2"
,"createRegistration"
})
@XmlAccessorType(XmlAccessType.FIELD)*/
@XmlRootElement(name="DirectTransactionRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectTransactionRequest
{
    @XmlElement(name = "toId",required = true)
    private int toId;

    @XmlElement(name = "toType",required = true)
    private String toType;

    @XmlElement(name = "amount",required = true)
    private BigDecimal amount;

    @XmlElement(name = "description",required = true)
    private String description;

    @XmlElement(name = "redirectURL",required = true)
    private String redirectURL;

    @XmlElement(name = "checksum",required = true)
    private String checksum;

    @XmlElement(name = "cardnumber",required = true)
    private String cardnumber;

    @XmlElement(name = "cvv",required = true)
    private String cvv;

    @XmlElement(name = "expiry_month",required = true)
    private String expiry_month1;

    @XmlElement(name = "expiry_month",required = true)
    private int expiry_month;



    public String getExpiry_year1()
    {
        return expiry_year1;
    }

    public void setExpiry_year1(String expiry_year1)
    {
        this.expiry_year1 = expiry_year1;
    }

    public String getExpiry_month1()
    {
        return expiry_month1;
    }

    public void setExpiry_month1(String expiry_month1)
    {
        this.expiry_month1 = expiry_month1;
    }

    @XmlElement(name = "expiry_year",required = true)
    private String expiry_year1;

    @XmlElement(name = "expiry_year",required = true)
    private int expiry_year;

    @XmlElement(name = "birthDate",required = true)
    private String birthDate;

    @XmlElement(name = "language",required = true)
    private String language;

    @XmlElement(name = "firstName",required = true)
    private String firstName;

    @XmlElement(name = "lastName",required = true)
    private String lastName;

    @XmlElement(name = "terminalId",required = false)
    private String terminalId;

    @XmlElement(name = "paymentType",required = false)
    private String paymentType;

    @XmlElement(name = "cardType",required = false)
    private String cardType;

    @XmlElement(name = "orderDescription",required = false)
    private String orderDescription;

    @XmlElement(name = "ipAddress",required = false)
    private String ipAddress;

    @XmlElement(name = "countryCode",required = false)
    private String countryCode;

    @XmlElement(name = "city",required = false)
    private String city;

    @XmlElement(name = "state",required = false)
    private String state;

    @XmlElement(name = "street",required = false)
    private String street;

    @XmlElement(name = "zip",required = false)
    private String zip;

    @XmlElement(name = "telNo",required = false)
    private String telNo;

    @XmlElement(name = "telNoCC",required = false)
    private String telNoCC;

    @XmlElement(name = "emailAddress",required = false)
    private String emailAddress;

    @XmlElement(name = "cardHolderIpAddress",required = false)
    private String cardHolderIpAddress;

    @XmlElement(name = "ssn",required = false)
    private String ssn;

    @XmlElement(name = "reservedField1",required = false)
    private String reservedField1;

    @XmlElement(name = "reservedField2",required = false)
    private String reservedField2;

    @XmlElement(name = "createRegistration",required = false)
    private String createRegistration;

    @XmlElement(name = "isEncrypted",required = false)
    private String isEncrypted;

    @XmlElement(name = "recurringDetail",required = false)
    private String recurringDetail;

    @XmlElement(name = "splitPaymentDetail",required = false)
    private String splitPaymentDetail;

    @XmlElement(name = "partnerId",required = false)
    private String partnerId;

    @XmlElement(name = "currency",required = false)
    private String currency;

    public int getToId()
    {
        return toId;
    }

    public void setToId(int toId)
    {
        this.toId = toId;
    }

    public String getToType()
    {
        return toType;
    }

    public void setToType(String toType)
    {
        this.toType = toType;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getRedirectURL()
    {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public String getCardnumber()
    {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber)
    {
        this.cardnumber = cardnumber;
    }

    public String getCvv()
    {
        return cvv;
    }

    public void setCvv(String cvv)
    {
        this.cvv = cvv;
    }

    public int getExpiry_month()
    {
        return expiry_month;
    }

    public void setExpiry_month(int expiry_month)
    {
        this.expiry_month = expiry_month;
    }

    public int getExpiry_year()
    {
        return expiry_year;
    }

    public void setExpiry_year(int expiry_year)
    {
        this.expiry_year = expiry_year;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getOrderDescription()
    {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription)
    {
        this.orderDescription = orderDescription;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getTelNo()
    {
        return telNo;
    }

    public void setTelNo(String telNo)
    {
        this.telNo = telNo;
    }

    public String getTelNoCC()
    {
        return telNoCC;
    }

    public void setTelNoCC(String telNoCC)
    {
        this.telNoCC = telNoCC;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getCardHolderIpAddress()
    {
        return cardHolderIpAddress;
    }

    public void setCardHolderIpAddress(String cardHolderIpAddress)
    {
        this.cardHolderIpAddress = cardHolderIpAddress;
    }

    public String getSsn()
    {
        return ssn;
    }

    public void setSsn(String ssn)
    {
        this.ssn = ssn;
    }

    public String getReservedField1()
    {
        return reservedField1;
    }

    public void setReservedField1(String reservedField1)
    {
        this.reservedField1 = reservedField1;
    }

    public String getReservedField2()
    {
        return reservedField2;
    }

    public void setReservedField2(String reservedField2)
    {
        this.reservedField2 = reservedField2;
    }

    public String getCreateRegistration()
    {
        return createRegistration;
    }

    public void setCreateRegistration(String createRegistration)
    {
        this.createRegistration = createRegistration;
    }

    public String getIsEncrypted()
    {
        return isEncrypted;
    }

    public void setIsEncrypted(String isEncrypted)
    {
        this.isEncrypted = isEncrypted;
    }

    public String getRecurringDetail()
    {
        return recurringDetail;
    }

    public void setRecurringDetail(String recurringDetail)
    {
        this.recurringDetail = recurringDetail;
    }

    public String getSplitPaymentDetail()
    {
        return splitPaymentDetail;
    }

    public void setSplitPaymentDetail(String splitPaymentDetail)
    {
        this.splitPaymentDetail = splitPaymentDetail;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }
}
