package com.manager.enums;

/**
 * Created by admin on 11/20/2015.
 */
public enum PartnerTemplatePreference
{
    AHEADPANELFONT_COLOR("AHEADPANELFONT_COLOR"),
    ABODYPANELFONT_COLOR("ABODYPANELFONT_COLOR"),
    APANELHEADING_COLOR("APANELHEADING_COLOR"),
    APANELBODY_COLOR("APANELBODY_COLOR"),
    AMAINBACKGROUNDCOLOR("AMAINBACKGROUNDCOLOR"),
    AMERCHANTLOGONAME("AMERCHANTLOGONAME"),
    ATRANSACTIONPAGEMERCHANTLOGO("ATRANSACTIONPAGEMERCHANTLOGO"),
    ABODY_BACKGROUND_COLOR("ABODY_BACKGROUND_COLOR"),
    ABODY_FOREGROUND_COLOR("ABODY_FOREGROUND_COLOR"),
    ANAVIGATION_FONT_COLOR("ANAVIGATION_FONT_COLOR"),
    ATEXTBOX_COLOR("ATEXTBOX_COLOR"),
    AMAILBACKGROUNDCOLOR("AMAILBACKGROUNDCOLOR"),
    AMAIL_PANELHEADING_COLOR("AMAIL_PANELHEADING_COLOR"),
    AMAIL_HEADPANELFONT_COLOR("AMAIL_HEADPANELFONT_COLOR"),
    AMAIL_BODYPANELFONT_COLOR("AMAIL_BODYPANELFONT_COLOR"),
    AICON_VECTOR_COLOR("AICON_VECTOR_COLOR"),


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
    NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR")
     ;

    private String partnerTemplatePreference;

    PartnerTemplatePreference(String templatePreference)
    {
        this.partnerTemplatePreference = templatePreference;
    }

    public static  PartnerTemplatePreference getEnum(String value)
    {
        try
        {
            return PartnerTemplatePreference.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }

    public String toString()
    {
        return partnerTemplatePreference;
    }
}
