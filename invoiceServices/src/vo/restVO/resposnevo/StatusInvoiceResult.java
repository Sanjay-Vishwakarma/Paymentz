package vo.restVO.resposnevo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/6/2017.
 */
@XmlRootElement(name="statusInvoiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusInvoiceResult
{
    @XmlElement(name="code")
    String code;

    @XmlElement(name="description")
    String description;

    public String getResultCode()
    {
        return code;
    }

    public void setResultCode(String resultCode)
    {
        this.code = resultCode;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
