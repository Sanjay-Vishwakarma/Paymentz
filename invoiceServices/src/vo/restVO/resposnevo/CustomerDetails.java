package vo.restVO.resposnevo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Pranav on 15-05-2017.
 */
    @XmlRootElement(name="customerDetails")
    @XmlAccessorType(XmlAccessType.FIELD)
    public class CustomerDetails
    {
        @XmlElement(name="customername")
        String customername;

        @XmlElement(name="customeraddress")

        String customeraddress;

        @XmlElement(name="customermobileno")
        String customermobileno;

        @XmlElement(name="customeremail")
        String customeremail;

        public String getCustomername()
        {
            return customername;
        }

        public void setCustomername(String customername)
        {
            this.customername = customername;
        }

        public String getCustomeraddress()
        {
            return customeraddress;
        }

        public void setCustomeraddress(String customeraddress)
        {
            this.customeraddress = customeraddress;
        }

        public String getCustomermobileno()
        {
            return customermobileno;
        }

        public void setCustomermobileno(String customermobileno)
        {
            this.customermobileno = customermobileno;
        }

        public String getCustomeremail()
        {
            return customeremail;
        }

        public void setCustomeremail(String customeremail)
        {
            this.customeremail = customeremail;
        }
    }
