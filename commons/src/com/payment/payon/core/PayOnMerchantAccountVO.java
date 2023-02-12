package com.payment.payon.core;

import com.payment.common.core.CommMerchantVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/28/12
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnMerchantAccountVO extends CommMerchantVO
{
    private String type;

    public String getConnector()
    {
        return connector;
    }

    public void setConnector(String connector)
    {
        this.connector = connector;
    }

    private String connector;
    private String payOnUsername;
    private String payOnPassword;
    private String payOnUrl;
    private String payOnTransactionMode;


    private String country;
    private String terminalID;
    private String IBAN;
    private String internationalBankID;
    private String nationalBankID;

    private String uploadUser;
    private String uploadPassword;
    private String transactionCategory;
    private String recurringCode;
    private String merchantCategory;
    private String avsInstruction;
    private String avsRejectionPolicy;
    private String cvvInstruction;
    private String cvvRejectionPolicy;
    private String secret;

    //Additional field for payon worldline
    private String merchantname;
    private String username;
    private String key;

    private String shortID;


    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPayOnUsername()
    {
        return payOnUsername;
    }

    public void setPayOnUsername(String payOnUsername)
    {
        this.payOnUsername = payOnUsername;
    }

    public String getPayOnPassword()
    {
        return payOnPassword;
    }

    public void setPayOnPassword(String payOnPassword)
    {
        this.payOnPassword = payOnPassword;
    }

    public String getPayOnUrl()
    {
        return payOnUrl;
    }

    public void setPayOnUrl(String payOnUrl)
    {
        this.payOnUrl = payOnUrl;
    }

    public String getPayOnTransactionMode()
    {
        return payOnTransactionMode;
    }

    public void setPayOnTransactionMode(String payOnTransactionMode)
    {
        this.payOnTransactionMode = payOnTransactionMode;
    }

    //    public String getMerchantID()
//    {
//        return merchantID;
//    }
//
//    public void setMerchantID(String merchantID)
//    {
//        this.merchantID = merchantID;
//    }
//
//    public String getMerchantName()
//    {
//        return merchantName;
//    }
//
//    public void setMerchantName(String merchantName)
//    {
//        this.merchantName = merchantName;
//    }
//
//    public String getKey()
//    {
//        return key;
//    }
//
//    public void setKey(String key)
//    {
//        this.key = key;
//    }
//
//    public String getUsername()
//    {
//        return username;
//    }
//
//    public void setUsername(String username)
//    {
//        this.username = username;
//    }
//
//    public String getPassword()
//    {
//        return password;
//    }
//
//    public void setPassword(String password)
//    {
//        this.password = password;
//    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getTerminalID()
    {
        return terminalID;
    }

    public void setTerminalID(String terminalID)
    {
        this.terminalID = terminalID;
    }

    public String getIBAN()
    {
        return IBAN;
    }

    public void setIBAN(String IBAN)
    {
        this.IBAN = IBAN;
    }

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

    public String getUploadUser()
    {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser)
    {
        this.uploadUser = uploadUser;
    }

    public String getUploadPassword()
    {
        return uploadPassword;
    }

    public void setUploadPassword(String uploadPassword)
    {
        this.uploadPassword = uploadPassword;
    }

    public String getTransactionCategory()
    {
        return transactionCategory;
    }

    public void setTransactionCategory(String transactionCategory)
    {
        this.transactionCategory = transactionCategory;
    }

    public String getRecurringCode()
    {
        return recurringCode;
    }

    public void setRecurringCode(String recurringCode)
    {
        this.recurringCode = recurringCode;
    }

    public String getMerchantCategory()
    {
        return merchantCategory;
    }

    public void setMerchantCategory(String merchantCategory)
    {
        this.merchantCategory = merchantCategory;
    }

    public String getAvsInstruction()
    {
        return avsInstruction;
    }

    public void setAvsInstruction(String avsInstruction)
    {
        this.avsInstruction = avsInstruction;
    }

    public String getAvsRejectionPolicy()
    {
        return avsRejectionPolicy;
    }

    public void setAvsRejectionPolicy(String avsRejectionPolicy)
    {
        this.avsRejectionPolicy = avsRejectionPolicy;
    }

    public String getCvvInstruction()
    {
        return cvvInstruction;
    }

    public void setCvvInstruction(String cvvInstruction)
    {
        this.cvvInstruction = cvvInstruction;
    }

    public String getCvvRejectionPolicy()
    {
        return cvvRejectionPolicy;
    }

    public void setCvvRejectionPolicy(String cvvRejectionPolicy)
    {
        this.cvvRejectionPolicy = cvvRejectionPolicy;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getMerchantname()
    {
        return merchantname;
    }

    public void setMerchantname(String merchantname)
    {
        this.merchantname = merchantname;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getShortID()
    {
        return shortID;
    }

    public void setShortID(String shortID)
    {
        this.shortID = shortID;
    }
}
