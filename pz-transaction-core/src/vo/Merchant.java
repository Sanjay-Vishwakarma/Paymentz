package vo;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 9/1/2016.
 */
@XmlRootElement(name="merchant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Merchant
{
    @FormParam("merchant.username")
    String loginName;

    @FormParam("merchant.password")
    String newPassword;


    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }
}

