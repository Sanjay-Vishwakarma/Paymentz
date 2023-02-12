package com.directi.pg.core.valueObjects;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 11/14/12
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreAddressDetailsVO extends GenericAddressDetailsVO
{
    private String birthdate;
    private String ssn;

    private String md5info;

    public String getMd5info()
    {
        return md5info;
    }

    public void setMd5info(String md5info)
    {
        this.md5info = md5info;
    }

    private String remark;

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }


    public String getBirthdate()
    {
        return birthdate;
    }

    public void setBirthdate(String birthdate)
    {
        this.birthdate = birthdate;
    }

    public String getSsn()
    {
        return ssn;
    }

    public void setSsn(String ssn)
    {
        this.ssn = ssn;
    }

    public String getMd5key()
    {
        return md5key;
    }

    public void setMd5key(String md5key)
    {
        this.md5key = md5key;
    }

    private String md5key;

}
