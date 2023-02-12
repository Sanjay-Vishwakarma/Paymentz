package com.manager.enums;

/**
 * Created by admin on 16-Nov-17.
 */
public enum DefaultThemeColor
{
    Pztheme("Pztheme"),
    flyingmerchant("flyingmerchant"),
    alea("alea"),
    condorgaming("condorgaming"),
    igamingcloud("igamingcloud");



    private String defaultThemeColor;
    DefaultThemeColor(String defaultThemeColor)
    {
        this.defaultThemeColor = defaultThemeColor;
    }
    @Override
    public String toString()
    {
        return defaultThemeColor;
    }


}
