package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="templateConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateConfiguration
{
    @XmlElement(name="isPharma")
    private String isPharma;

    @XmlElement(name="isPoweredByLogo")
    private String isPoweredByLogo;

    @XmlElement(name="template")
    private String template;

    @XmlElement(name="pciLogo")
    private String pciLogo;

    @XmlElement(name="securityLogo")
    private String securityLogo;

    @XmlElement(name="isMerchantLogo")
    private String isMerchantLogo;

    @XmlElement(name="visaSecureLogo")
    private String visaSecureLogo;

    @XmlElement(name="isMerchantLogoBO")
    private String isMerchantLogoBO	;

    @XmlElement(name="mcSecureLogo")
    private String mcSecureLogo;

    @XmlElement(name="checkoutTimer")
    private String checkoutTimer;

    @XmlElement(name="partnerLogo")
    private String partnerLogo;

    @XmlElement(name="consent")
    private String consent;

    public String getIsPharma()
    {
        return isPharma;
    }

    public void setIsPharma(String isPharma)
    {
        this.isPharma = isPharma;
    }

    public String getIsPoweredByLogo()
    {
        return isPoweredByLogo;
    }

    public void setIsPoweredByLogo(String isPoweredByLogo)
    {
        this.isPoweredByLogo = isPoweredByLogo;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String getPciLogo()
    {
        return pciLogo;
    }

    public void setPciLogo(String pciLogo)
    {
        this.pciLogo = pciLogo;
    }

    public String getSecurityLogo()
    {
        return securityLogo;
    }

    public void setSecurityLogo(String securityLogo)
    {
        this.securityLogo = securityLogo;
    }

    public String getIsMerchantLogo()
    {
        return isMerchantLogo;
    }

    public void setIsMerchantLogo(String isMerchantLogo)
    {
        this.isMerchantLogo = isMerchantLogo;
    }

    public String getVisaSecureLogo()
    {
        return visaSecureLogo;
    }

    public void setVisaSecureLogo(String visaSecureLogo)
    {
        this.visaSecureLogo = visaSecureLogo;
    }

    public String getIsMerchantLogoBO()
    {
        return isMerchantLogoBO;
    }

    public void setIsMerchantLogoBO(String isMerchantLogoBO)
    {
        this.isMerchantLogoBO = isMerchantLogoBO;
    }

    public String getMcSecureLogo()
    {
        return mcSecureLogo;
    }

    public void setMcSecureLogo(String mcSecureLogo)
    {
        this.mcSecureLogo = mcSecureLogo;
    }

    public String getCheckoutTimer()
    {
        return checkoutTimer;
    }

    public void setCheckoutTimer(String checkoutTimer)
    {
        this.checkoutTimer = checkoutTimer;
    }

    public String getPartnerLogo()
    {
        return partnerLogo;
    }

    public void setPartnerLogo(String partnerLogo)
    {
        this.partnerLogo = partnerLogo;
    }

    public String getConsent()
    {
        return consent;
    }

    public void setConsent(String consent)
    {
        this.consent = consent;
    }
}
