package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/18/15
 * Time: 4:21 PM
 */
public enum ResponseType
{
    STRING("String"),
    XML("XML"),
    JSON("JSON");

    private String responseType;
    ResponseType(String responseType)
    {
        this.responseType = responseType;
    }
    @Override
    public String toString()
    {
        return responseType;
    }
}
