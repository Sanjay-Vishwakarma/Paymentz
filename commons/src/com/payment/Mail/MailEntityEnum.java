package com.payment.Mail;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/13/14
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public enum MailEntityEnum
{
    AdminEmail("AdminEmail"),
    PartnerEmail("PartnerEmail"),
    MerchantEmail("MerchantEmail"),
    CustomerEmail("CustomerEmail"),
    OtpEmail("OtpEmail"),
    CustomerSupportEmail("CustomerSupportEmail"),
    AgentEmail("AgentEmail"),
    FraudSystemEmail("FraudSystemEmail"),
    BankEmail("BankEmail"),
    MerchantUserEmail("MerchantUserEmail"),
    PartnerUserEmail("PartnerUserEmail");

    private String mailEntity;

    MailEntityEnum(String mailEntity)
    {
        this.mailEntity = mailEntity;
    }


    @Override
    public String toString()
    {
        return mailEntity;
    }
}