package vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 6/29/2017.
 */
@XmlRootElement(name="authRequestVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthRequestVO
{
    @XmlElement(name="authentication")
    Authentication authentication;

    @XmlElement(name="merchant")
    Merchant merchant;

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public Merchant getMerchant()
    {
        return merchant;
    }

    public void setMerchant(Merchant merchant)
    {
        this.merchant = merchant;
    }
}
