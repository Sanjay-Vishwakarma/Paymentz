package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantResponseFlagsVO
{
    @XmlElement(name="result")
    private Result result;

    @XmlElement(name="memberLimits")
    private MemberLimits memberLimits;

    @XmlElement(name="transactionLimits")
    private TransactionLimits transactionLimits;

    @XmlElement(name="generalConfiguration")
    private GeneralConfiguration generalConfiguration;

    @XmlElement(name="transactionConfiguration")
    private TransactionConfiguration transactionConfiguration;

    @XmlElement(name="payoutLimits")
    private PayoutLimits payoutLimits;

    @XmlElement(name="backOfficeAccessManagement")
    private BackOfficeAccessManagement backOfficeAccessManagement;

    @XmlElement(name="templateConfiguration")
    private TemplateConfiguration templateConfiguration;

    @XmlElement(name="tokenConfiguration")
    private TokenConfiguration tokenConfiguration;

    @XmlElement(name="whitelistingConfiguration")
    private WhitelistingConfiguration whitelistingConfiguration;

    @XmlElement(name="fraudDefenderConfiguration")
    private FraudDefenderConfiguration fraudDefenderConfiguration;

    @XmlElement(name="hrTransactionConfiguration")
    private HRTransactionConfiguration hrTransactionConfiguration;

    @XmlElement(name="refundConfiguration")
    private RefundConfiguration refundConfiguration;

    @XmlElement(name="emailConfiguration")
    private EmailConfiguration emailConfiguration;

    @XmlElement(name="smsConfiguration")
    private SMSConfiguration smsConfiguration;

    @XmlElement(name="invoiceConfiguration")
    private InvoiceConfiguration invoiceConfiguration;

    @XmlElement(name="fraudConfiguration")
    private FraudConfiguration fraudConfiguration;

    @XmlElement(name="splitTransactionConfiguration")
    private SplitTransactionConfiguration splitTransactionConfiguration;

    @XmlElement(name="invoizerConfiguration")
    private InvoizerConfiguration invoizerConfiguration;

    @XmlElement(name="merchantNotificationCallback")
    private MerchantNotificationCallback merchantNotificationCallback;

    @XmlElement (name="timestamp")
    String timestamp;

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public Result getResult()
    {
        return result;
    }

    public void setResult(Result result)
    {
        this.result = result;
    }

    public MemberLimits getMemberLimits()
    {
        return memberLimits;
    }

    public void setMemberLimits(MemberLimits memberLimits)
    {
        this.memberLimits = memberLimits;
    }

    public TransactionLimits getTransactionLimits()
    {
        return transactionLimits;
    }

    public void setTransactionLimits(TransactionLimits transactionLimits)
    {
        this.transactionLimits = transactionLimits;
    }

    public PayoutLimits getPayoutLimits()
    {
        return payoutLimits;
    }

    public void setPayoutLimits(PayoutLimits payoutLimits)
    {
        this.payoutLimits = payoutLimits;
    }

    public GeneralConfiguration getGeneralConfiguration()
    {
        return generalConfiguration;
    }

    public void setGeneralConfiguration(GeneralConfiguration generalConfiguration)
    {
        this.generalConfiguration = generalConfiguration;
    }

    public TransactionConfiguration getTransactionConfiguration()
    {
        return transactionConfiguration;
    }

    public void setTransactionConfiguration(TransactionConfiguration transactionConfiguration)
    {
        this.transactionConfiguration = transactionConfiguration;
    }

    public BackOfficeAccessManagement getBackOfficeAccessManagement()
    {
        return backOfficeAccessManagement;
    }

    public void setBackOfficeAccessManagement(BackOfficeAccessManagement backOfficeAccessManagement)
    {
        this.backOfficeAccessManagement = backOfficeAccessManagement;
    }

    public TemplateConfiguration getTemplateConfiguration()
    {
        return templateConfiguration;
    }

    public void setTemplateConfiguration(TemplateConfiguration templateConfiguration)
    {
        this.templateConfiguration = templateConfiguration;
    }

    public TokenConfiguration getTokenConfiguration()
    {
        return tokenConfiguration;
    }

    public void setTokenConfiguration(TokenConfiguration tokenConfiguration)
    {
        this.tokenConfiguration = tokenConfiguration;
    }

    public FraudDefenderConfiguration getFraudDefenderConfiguration()
    {
        return fraudDefenderConfiguration;
    }

    public void setFraudDefenderConfiguration(FraudDefenderConfiguration fraudDefenderConfiguration)
    {
        this.fraudDefenderConfiguration = fraudDefenderConfiguration;
    }

    public WhitelistingConfiguration getWhitelistingConfiguration()
    {
        return whitelistingConfiguration;
    }

    public void setWhitelistingConfiguration(WhitelistingConfiguration whitelistingConfiguration)
    {
        this.whitelistingConfiguration = whitelistingConfiguration;
    }

    public HRTransactionConfiguration getHrTransactionConfiguration()
    {
        return hrTransactionConfiguration;
    }

    public void setHrTransactionConfiguration(HRTransactionConfiguration hrTransactionConfiguration)
    {
        this.hrTransactionConfiguration = hrTransactionConfiguration;
    }

    public RefundConfiguration getRefundConfiguration()
    {
        return refundConfiguration;
    }

    public void setRefundConfiguration(RefundConfiguration refundConfiguration)
    {
        this.refundConfiguration = refundConfiguration;
    }

    public EmailConfiguration getEmailConfiguration()
    {
        return emailConfiguration;
    }

    public void setEmailConfiguration(EmailConfiguration emailConfiguration)
    {
        this.emailConfiguration = emailConfiguration;
    }

    public SMSConfiguration getSmsConfiguration()
    {
        return smsConfiguration;
    }

    public void setSmsConfiguration(SMSConfiguration smsConfiguration)
    {
        this.smsConfiguration = smsConfiguration;
    }

    public InvoiceConfiguration getInvoiceConfiguration()
    {
        return invoiceConfiguration;
    }

    public void setInvoiceConfiguration(InvoiceConfiguration invoiceConfiguration)
    {
        this.invoiceConfiguration = invoiceConfiguration;
    }

    public FraudConfiguration getFraudConfiguration()
    {
        return fraudConfiguration;
    }

    public void setFraudConfiguration(FraudConfiguration fraudConfiguration)
    {
        this.fraudConfiguration = fraudConfiguration;
    }

    public SplitTransactionConfiguration getSplitTransactionConfiguration()
    {
        return splitTransactionConfiguration;
    }

    public void setSplitTransactionConfiguration(SplitTransactionConfiguration splitTransactionConfiguration)
    {
        this.splitTransactionConfiguration = splitTransactionConfiguration;
    }

    public InvoizerConfiguration getInvoizerConfiguration()
    {
        return invoizerConfiguration;
    }

    public void setInvoizerConfiguration(InvoizerConfiguration invoizerConfiguration)
    {
        this.invoizerConfiguration = invoizerConfiguration;
    }

    public MerchantNotificationCallback getMerchantNotificationCallback()
    {
        return merchantNotificationCallback;
    }

    public void setMerchantNotificationCallback(MerchantNotificationCallback merchantNotificationCallback)
    {
        this.merchantNotificationCallback = merchantNotificationCallback;
    }

    @Override
    public String toString()
    {
        return "MerchantResponseFlagsVO{" +
                "result=" + result +
                ", memberLimits=" + memberLimits +
                ", transactionLimits=" + transactionLimits +
                ", generalConfiguration=" + generalConfiguration +
                ", transactionConfiguration=" + transactionConfiguration +
                ", payoutLimits=" + payoutLimits +
                ", backOfficeAccessManagement=" + backOfficeAccessManagement +
                ", templateConfiguration=" + templateConfiguration +
                ", tokenConfiguration=" + tokenConfiguration +
                ", whitelistingConfiguration=" + whitelistingConfiguration +
                ", fraudDefenderConfiguration=" + fraudDefenderConfiguration +
                ", hrTransactionConfiguration=" + hrTransactionConfiguration +
                ", refundConfiguration=" + refundConfiguration +
                ", emailConfiguration=" + emailConfiguration +
                ", smsConfiguration=" + smsConfiguration +
                ", invoiceConfiguration=" + invoiceConfiguration +
                ", fraudConfiguration=" + fraudConfiguration +
                ", splitTransactionConfiguration=" + splitTransactionConfiguration +
                ", invoizerConfiguration=" + invoizerConfiguration +
                ", merchantNotificationCallback=" + merchantNotificationCallback +
                '}';
    }
}
