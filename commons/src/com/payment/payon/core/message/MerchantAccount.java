package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantAccount
{
    @XStreamAsAttribute
    private String type;
    @XStreamAlias("MerchantID")
    private String merchantID;
    @XStreamAlias("MerchantName")
    private String merchantName;
    @XStreamAlias("Key")
    private String key;
    @XStreamAlias("Username")
    private String username;

    @XStreamAlias("Password")
    private String password;
    @XStreamAlias("Country")
    private String country;
    @XStreamAlias("TerminalID")
    private String terminalID;
    @XStreamAlias("Psp")
    private String psp;

    private String IBAN;
    @XStreamAlias("InternationalBankID")
    private String internationalBankID;

    @XStreamAlias("NationalBankID")
    private String nationalBankID;

    @XStreamAlias("UploadUser")
    private String uploadUser;
    @XStreamAlias("UploadPassword")
    private String uploadPassword;

    @XStreamAlias("TransactionCategory")
    private String transactionCategory;
    @XStreamAlias("RecurringCode")
    private String recurringCode;
    @XStreamAlias("MerchantCategory")
    private String merchantCategory;
    @XStreamAlias("AVS")
    private Validation avs;
    @XStreamAlias("CVVValidation")
    private Validation cvv;
    @XStreamAlias("Secret")
    private String secret;

    public String getInternationalBankID()
    {
        return internationalBankID;
    }

    public void setInternationalBankID(String internationalBankID)
    {
        this.internationalBankID = internationalBankID;
    }

    public String getNationalBankID()
    {
        return nationalBankID;
    }

    public void setNationalBankID(String nationalBankID)
    {
        this.nationalBankID = nationalBankID;
    }

    public String getType()
    {
        return type;
    }

    public String getMerchantID()
    {
        return merchantID;
    }

    public String getMerchantName()
    {
        return merchantName;
    }

    public String getKey()
    {
        return key;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getCountry()
    {
        return country;
    }

    public String getTerminalID()
    {
        return terminalID;
    }

    public String getPsp()
    {
        return psp;
    }

    public void setPsp(String psp)
    {
        this.psp = psp;
    }

    public String getIBAN()
    {
        return IBAN;
    }


    public String getUploadUser()
    {
        return uploadUser;
    }

    public String getUploadPassword()
    {
        return uploadPassword;
    }

    public String getTransactionCategory()
    {
        return transactionCategory;
    }

    public String getRecurringCode()
    {
        return recurringCode;
    }

    public Validation getAvs()
    {
        return avs;
    }

    public Validation getCvv()
    {
        return cvv;
    }

    public String getSecret()
    {
        return secret;
    }

    public String getMerchantCategory()
    {
        return merchantCategory;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setTerminalID(String terminalID)
    {
        this.terminalID = terminalID;
    }

    public void setAvs(Validation avs)
    {
        this.avs = avs;
    }

    public void setCvv(Validation cvv)
    {
        this.cvv = cvv;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public void setIBAN(String IBAN)

    {
        this.IBAN = IBAN;
    }


    public void setUploadUser(String uploadUser)
    {
        this.uploadUser = uploadUser;
    }

    public void setUploadPassword(String uploadPassword)
    {
        this.uploadPassword = uploadPassword;
    }

    public void setTransactionCategory(String transactionCategory)
    {
        this.transactionCategory = transactionCategory;
    }

    public void setRecurringCode(String recurringCode)
    {
        this.recurringCode = recurringCode;
    }

    public void setMerchantCategory(String merchantCategory)
    {
        this.merchantCategory = merchantCategory;
    }


    public void setType(String type)
    {
        this.type = type;
    }

    public void setMerchantID(String merchantID)
    {
        this.merchantID = merchantID;
    }

    public void setMerchantName(String merchantName)
    {
        this.merchantName = merchantName;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }
}
