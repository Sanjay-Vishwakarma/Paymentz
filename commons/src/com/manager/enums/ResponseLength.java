package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/18/15
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ResponseLength
{
    FULL("Full"),
    DEFAULT("Default");

    private String responseLength;
    ResponseLength(String responseLength)
    {
        this.responseLength = responseLength;
    }
    @Override
    public String toString()
    {
        return responseLength;
    }
}
