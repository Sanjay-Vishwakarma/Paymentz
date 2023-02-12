package com.vo.requestVOs;

import com.payment.payon.core.message.Authentication;
import com.sun.jersey.api.core.InjectParam;
import com.vo.applicationManagerVOs.ApplicationManagerRequestVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.CompanyProfileRequestVO;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 08-09-2017.
 */
@XmlRootElement(name="Authentication")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationManagerAuthenticationRequest
{
    @FormParam("memberId")
    private String memberId;

    @FormParam("checksum")
    private String checksum;

    @FormParam("random")
    private String random;

    @FormParam("mode")
    private String mode;

    @FormParam("applicationId")
    private String applicationId;

    @InjectParam("MerchantApplicationForm")
    private ApplicationManagerRequestVO applicationManagerRequestVO;


    @InjectParam("merchant")
    MerchantVO merchant;

    @InjectParam("authentication")
    AuthenticationVO authentication;

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


    public ApplicationManagerRequestVO getApplicationManagerRequestVO()
    {
        return applicationManagerRequestVO;
    }

    public void setApplicationManagerRequestVO(ApplicationManagerRequestVO applicationManagerRequestVO)
    {
        this.applicationManagerRequestVO = applicationManagerRequestVO;
    }


    public MerchantVO getMerchant()
    {
        return merchant;
    }

    public void setMerchant(MerchantVO merchant)
    {
        this.merchant = merchant;
    }

    public AuthenticationVO getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(AuthenticationVO authentication)
    {
        this.authentication = authentication;
    }
}
