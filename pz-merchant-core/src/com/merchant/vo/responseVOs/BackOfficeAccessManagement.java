package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="backOfficeAccessManagement")
@XmlAccessorType(XmlAccessType.FIELD)
public class BackOfficeAccessManagement
{
    @XmlElement(name="dashBoard")
    private String dashBoard;

    @XmlElement(name="accountDetails")
    private String accountDetail;

    @XmlElement(name="settings")
    private String setting;

    @XmlElement(name="transactionManagement")
    private String transactionMgmt;

    @XmlElement(name="invoicing")
    private String invoice;

    @XmlElement(name="virtualTerminal")
    private String virtualTerminal;

    @XmlElement(name="merchantManagement")
    private String merchantMgmt;

    @XmlElement(name="applicationManager")
    private String applicationManager;

    @XmlElement(name="recurring")
    private String recurring;

    @XmlElement(name="tokenManagement")
    private String tokenMgmt;

    @XmlElement(name="rejectedTransaction")
    private String rejectedTransaction;

    @XmlElement(name="virtualCheckout")
    private String virtualCheckout;

    @XmlElement(name="payByLink")
    private String payByLink;

    @XmlElement(name="accountDetails")
    private AccountDetails accountDetails;

    @XmlElement(name="settings")
    private Settings settings;

    @XmlElement(name="transactionManagement")
    private TransactionManagement transactionManagement;

    @XmlElement(name="invoicing")
    private Invoicing invoicing;

    @XmlElement(name="tokenManagement")
    private TokenManagement tokenManagement;

    @XmlElement(name="merchantManagement")
    private MerchantManagement merchantManagement;

    public String getDashBoard()
    {
        return dashBoard;
    }

    public void setDashBoard(String dashBoard)
    {
        this.dashBoard = dashBoard;
    }

    public String getAccountDetail()
    {
        return accountDetail;
    }

    public void setAccountDetail(String accountDetail)
    {
        this.accountDetail = accountDetail;
    }

    public String getSetting()
    {
        return setting;
    }

    public void setSetting(String setting)
    {
        this.setting = setting;
    }

    public String getTransactionMgmt()
    {
        return transactionMgmt;
    }

    public void setTransactionMgmt(String transactionMgmt)
    {
        this.transactionMgmt = transactionMgmt;
    }

    public String getInvoice()
    {
        return invoice;
    }

    public void setInvoice(String invoice)
    {
        this.invoice = invoice;
    }

    public String getVirtualTerminal()
    {
        return virtualTerminal;
    }

    public void setVirtualTerminal(String virtualTerminal)
    {
        this.virtualTerminal = virtualTerminal;
    }

    public String getMerchantMgmt()
    {
        return merchantMgmt;
    }

    public void setMerchantMgmt(String merchantMgmt)
    {
        this.merchantMgmt = merchantMgmt;
    }

    public String getApplicationManager()
    {
        return applicationManager;
    }

    public void setApplicationManager(String applicationManager)
    {
        this.applicationManager = applicationManager;
    }

    public String getRecurring()
    {
        return recurring;
    }

    public void setRecurring(String recurring)
    {
        this.recurring = recurring;
    }

    public String getTokenMgmt()
    {
        return tokenMgmt;
    }

    public void setTokenMgmt(String tokenMgmt)
    {
        this.tokenMgmt = tokenMgmt;
    }

    public String getRejectedTransaction()
    {
        return rejectedTransaction;
    }

    public void setRejectedTransaction(String rejectedTransaction)
    {
        this.rejectedTransaction = rejectedTransaction;
    }

    public String getVirtualCheckout()
    {
        return virtualCheckout;
    }

    public void setVirtualCheckout(String virtualCheckout)
    {
        this.virtualCheckout = virtualCheckout;
    }

    public String getPayByLink()
    {
        return payByLink;
    }

    public void setPayByLink(String payByLink)
    {
        this.payByLink = payByLink;
    }

    public AccountDetails getAccountDetails()
    {
        return accountDetails;
    }

    public void setAccountDetails(AccountDetails accountDetails)
    {
        this.accountDetails = accountDetails;
    }

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    public TransactionManagement getTransactionManagement()
    {
        return transactionManagement;
    }

    public void setTransactionManagement(TransactionManagement transactionManagement)
    {
        this.transactionManagement = transactionManagement;
    }

    public Invoicing getInvoicing()
    {
        return invoicing;
    }

    public void setInvoicing(Invoicing invoicing)
    {
        this.invoicing = invoicing;
    }

    public TokenManagement getTokenManagement()
    {
        return tokenManagement;
    }

    public void setTokenManagement(TokenManagement tokenManagement)
    {
        this.tokenManagement = tokenManagement;
    }

    public MerchantManagement getMerchantManagement()
    {
        return merchantManagement;
    }

    public void setMerchantManagement(MerchantManagement merchantManagement)
    {
        this.merchantManagement = merchantManagement;
    }

    @Override
    public String toString()
    {
        return "BackOfficeAccessManagement{" +
                "dashBoard='" + dashBoard + '\'' +
                ", accountDetail='" + accountDetail + '\'' +
                ", setting='" + setting + '\'' +
                ", transactionMgmt='" + transactionMgmt + '\'' +
                ", invoice='" + invoice + '\'' +
                ", virtualTerminal='" + virtualTerminal + '\'' +
                ", merchantMgmt='" + merchantMgmt + '\'' +
                ", applicationManager='" + applicationManager + '\'' +
                ", recurring='" + recurring + '\'' +
                ", tokenMgmt='" + tokenMgmt + '\'' +
                ", rejectedTransaction='" + rejectedTransaction + '\'' +
                ", virtualCheckout='" + virtualCheckout + '\'' +
                ", payByLink='" + payByLink + '\'' +
                ", accountDetails=" + accountDetails +
                ", settings=" + settings +
                ", transactionManagement=" + transactionManagement +
                ", invoicing=" + invoicing +
                ", tokenManagement=" + tokenManagement +
                ", merchantManagement=" + merchantManagement +
                '}';
    }
}
