package com.manager.enums;

/**
 * Created by admin on 11/20/2015.
 */
public enum TemplatePreference
{
    HEADPANELFONT_COLOR("HEADPANELFONT_COLOR"),
    BODYPANELFONT_COLOR("BODYPANELFONT_COLOR"),
    PANELHEADING_COLOR("PANELHEADING_COLOR"),
    PANELBODY_COLOR("PANELBODY_COLOR"),
    MAINBACKGROUNDCOLOR("MAINBACKGROUNDCOLOR"),
    MERCHANTLOGONAME("MERCHANTLOGONAME"),
    TRANSACTIONPAGEMERCHANTLOGO("TRANSACTIONPAGEMERCHANTLOGO"),
    BODY_BACKGROUND_COLOR("BODY_BACKGROUND_COLOR"),
    BODY_FOREGROUND_COLOR("BODY_FOREGROUND_COLOR"),
    NAVIGATION_FONT_COLOR("NAVIGATION_FONT_COLOR"),
    TEXTBOX_COLOR("TEXTBOX_COLOR"),
    MAILBACKGROUNDCOLOR("MAILBACKGROUNDCOLOR"),
    MAIL_PANELHEADING_COLOR("MAIL_PANELHEADING_COLOR"),
    MAIL_HEADPANELFONT_COLOR("MAIL_HEADPANELFONT_COLOR"),
    MAIL_BODYPANELFONT_COLOR("MAIL_BODYPANELFONT_COLOR"),

    CHECKOUT_BODYNFOOTER_COLOR("CHECKOUT_BODYNFOOTER_COLOR"),
    CHECKOUT_HEADERBACKGROUND_COLOR("CHECKOUT_HEADERBACKGROUND_COLOR"),
    CHECKOUT_NAVIGATIONBAR_COLOR("CHECKOUT_NAVIGATIONBAR_COLOR"),
    CHECKOUT_BUTTON_FONT_COLOR("CHECKOUT_BUTTON_FONT_COLOR"),
    CHECKOUT_HEADER_FONT_COLOR("CHECKOUT_HEADER_FONT_COLOR"),
    CHECKOUT_FULLBACKGROUND_COLOR("CHECKOUT_FULLBACKGROUND_COLOR"),
    CHECKOUT_LABEL_FONT_COLOR("CHECKOUT_LABEL_FONT_COLOR"),
    CHECKOUT_NAVIGATIONBAR_FONT_COLOR("CHECKOUT_NAVIGATIONBAR_FONT_COLOR"),
    CHECKOUT_BUTTON_COLOR("CHECKOUT_BUTTON_COLOR"),
    CHECKOUT_ICON_COLOR("CHECKOUT_ICON_COLOR"),
    CHECKOUT_TIMER_COLOR("CHECKOUT_TIMER_COLOR"),
    CHECKOUT_BOX_SHADOW("CHECKOUT_BOX_SHADOW"),

    NEW_CHECKOUT_BODYNFOOTER_COLOR("NEW_CHECKOUT_BODYNFOOTER_COLOR"),
    NEW_CHECKOUT_HEADERBACKGROUND_COLOR("NEW_CHECKOUT_HEADERBACKGROUND_COLOR"),
    NEW_CHECKOUT_NAVIGATIONBAR_COLOR("NEW_CHECKOUT_NAVIGATIONBAR_COLOR"),
    NEW_CHECKOUT_BUTTON_FONT_COLOR("NEW_CHECKOUT_BUTTON_FONT_COLOR"),
    NEW_CHECKOUT_HEADER_FONT_COLOR("NEW_CHECKOUT_HEADER_FONT_COLOR"),
    NEW_CHECKOUT_FULLBACKGROUND_COLOR("NEW_CHECKOUT_FULLBACKGROUND_COLOR"),
    NEW_CHECKOUT_LABEL_FONT_COLOR("NEW_CHECKOUT_LABEL_FONT_COLOR"),
    NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR"),
    NEW_CHECKOUT_BUTTON_COLOR("NEW_CHECKOUT_BUTTON_COLOR"),
    NEW_CHECKOUT_ICON_COLOR("NEW_CHECKOUT_ICON_COLOR"),
    NEW_CHECKOUT_TIMER_COLOR("NEW_CHECKOUT_TIMER_COLOR"),
    NEW_CHECKOUT_BOX_SHADOW("NEW_CHECKOUT_BOX_SHADOW"),
    NEW_CHECKOUT_FOOTER_FONT_COLOR("NEW_CHECKOUT_FOOTER_FONT_COLOR"),
    NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR"),


    Transactions_Date("Transactions_Date"),
    Transactions_TimeZone("Transactions_TimeZone"),
    Transactions_TrackingID("Transactions_TrackingID"),
    Transactions_OrderId("Transactions_OrderId"),
    Transactions_OrderDescription("Transactions_OrderDescription"),
    Transactions_CardHoldername("Transactions_CardHoldername"),
    Transactions_CustomerEmail("Transactions_CustomerEmail"),
    Transactions_CustomerID("Transactions_CustomerID"),
    Transactions_PayMode("Transactions_PayMode"),
    Transactions_CardType("Transactions_CardType"),
    Transactions_IssuingBank("Transactions_IssuingBank"),
    Transactions_Amount("Transactions_Amount"),
    Transactions_RefundedAmt("Transactions_RefundedAmt"),
    Transactions_Currency("Transactions_Currency"),
    Transactions_Status("Transactions_Status"),
    Transactions_Remark("Transactions_Remark"),
    Transactions_Terminal("Transactions_Terminal"),
    Transactions_LastUpdateDate("Transactions_LastUpdateDate"),
    Transactions_Mode("Transactions_Mode"),

    partnerTransactions_Transaction_Date1("partnerTransactions_Transaction_Date1"),
    partnerTransactions_TimeZone("partnerTransactions_TimeZone"),
    partnerTransactions_TrackingID("partnerTransactions_TrackingID"),
    partnerTransactions_PaymentID("partnerTransactions_PaymentID"),
    partnerTransactions_PartnerID("partnerTransactions_PartnerID"),
    partnerTransactions_MerchantID("partnerTransactions_MerchantID"),
    partnerTransactions_OrderID("partnerTransactions_OrderID"),
    partnerTransactions_OrderDescription("partnerTransactions_OrderDescription"),
    partnerTransactions_Card_Holder_Name("partnerTransactions_Card_Holder_Name"),
    partnerTransactions_Customer_Email("partnerTransactions_Customer_Email"),
    partnerTransactions_CustomerID("partnerTransactions_CustomerID"),
    partnerTransactions_PaymentMode("partnerTransactions_PaymentMode"),
    partnerTransactions_PaymentBrand("partnerTransactions_PaymentBrand"),
    transactions_mode("transactions_mode"),
    partnerTransactions_Amount("partnerTransactions_Amount"),
    partnerTransactions_RefundAmount("partnerTransactions_RefundAmount"),
    partnerTransactions_Currency("partnerTransactions_Currency"),
    partnerTransactions_Status1("partnerTransactions_Status1"),
    partnerTransactions_Remark("partnerTransactions_Remark"),
    partnerTransactions_TerminalID("partnerTransactions_TerminalID"),
    partnerTransactions_LastUpdateDate("partnerTransactions_LastUpdateDate"),

    ICON_VECTOR_COLOR("ICON_VECTOR_COLOR"),
    ;



    private String templatePreference;

    TemplatePreference(String templatePreference)
    {
        this.templatePreference = templatePreference;
    }

    public String toString()
    {
        return templatePreference;
    }

    public static  TemplatePreference getEnum(String value)
    {
        try
        {
           return TemplatePreference.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}
