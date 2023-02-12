package com.vo.requestVOs;

import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.sun.jersey.api.core.InjectParam;
import com.vo.applicationManagerVOs.CompanyProfileVO;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by NIKET on 2/12/2016.
 */
@XmlRootElement(name="Authentication")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationManagerAuthentication
{
    @XmlElement(name="memberId")
     String memberId;

    @XmlElement(name="checksum")
    String checksum;

    @XmlElement(name="random")
    String random;

    @XmlElement(name="mode")
    String mode;

    @XmlElement(name="applicationId")
    String applicationId;


    @XmlElement(name="MerchantApplicationForm")
    private ApplicationManagerVO applicationManagerVO;


    @XmlElement(name="merchant")
    Merchant merchant;

    @XmlElement(name="authentication")
    Authentication authentication;


    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId(String applicationId)
    {
        this.applicationId = applicationId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public String getRandom()
    {
        return random;
    }

    public void setRandom(String random)
    {
        this.random = random;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public ApplicationManagerVO getApplicationManagerVO()
    {
        return applicationManagerVO;
    }

    public void setApplicationManagerVO(ApplicationManagerVO applicationManagerVO)
    {
        this.applicationManagerVO = applicationManagerVO;
    }


    public Merchant getMerchantVO()
    {
        return merchant;
    }

    public void setMerchantVO(Merchant merchant)
    {
        this.merchant = merchant;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }
}
