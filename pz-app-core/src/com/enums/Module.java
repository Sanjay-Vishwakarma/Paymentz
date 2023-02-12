package com.enums;

/**
 * Created by Pradeep on 16/06/2015.
 */
public enum  Module
{

    ADMIN("ADMIN"),
    MERCHANT("MERCHANT"),
    PARTNER("PARTNER");


    private String module;

    Module(String Module)
    {
        this.module=Module;
    }

    public String toString()
    {
        return module;
    }
}
