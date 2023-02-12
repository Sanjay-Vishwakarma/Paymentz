package com.enums;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/24/15
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public enum IdentificationType
{
    Passport("Passport"),
    DriverLicense("DriverLicense"),
    NationalID("NationalID");


    private String identificationtype;

    IdentificationType(String idtype)
    {
        this.identificationtype=idtype;
    }

    public String toString()
    {
        return identificationtype;
    }
}




