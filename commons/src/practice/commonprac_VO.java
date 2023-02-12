package practice;

import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 6/21/2020.
 */
public class commonprac_VO
{
    String firstname;
    String lastname;
    String education;
    int number;
    String city;
    String birthdate;
    String state;
    long mobile;
    String pincode;
    public String getFirstname()
    {
        return firstname;
    }
    public void setFirstname(String firstname)
    {
        this.firstname=firstname;
    }

    public String getLastname()
    {
        return lastname;
    }
public void setLastname(String lastname)
{
    this.lastname=lastname;
}

    public String getEducation()
    {
        return education;
    }
    public void setEducation(String education)
    {
        this.education=education;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getBirthdate()
    {
        return birthdate;
    }

    public void setBirthdate(String birthdate)
    {
        this.birthdate = birthdate;
    }

    public long getMobile()
    {
        return mobile;
    }

    public void setMobile(long mobile)
    {
        this.mobile=mobile;
    }

    public String getPincode()
    {
        return pincode;
    }

    public void setPincode(String pincode)
    {
        this.pincode = pincode;
    }
}
