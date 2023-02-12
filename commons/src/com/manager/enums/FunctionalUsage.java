package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/10/15
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FunctionalUsage
{
    APPLICATIONMANAGER("Application Manager"),
    MERCHANTLOGONAME("Merchant Logo");

    private String functionalUsage;

    FunctionalUsage(String functionalUsage)
    {
        this.functionalUsage=functionalUsage;
    }

    public String toString()
    {
        return functionalUsage;
    }
}
