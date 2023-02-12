package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/31/13
 * Time: 12:13 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("PERSONINFO")
public class PERSONINFO
{
    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    @XStreamAlias("BirthDate")
    String birthDate;


}
