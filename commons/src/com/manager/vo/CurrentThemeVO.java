package com.manager.vo;

/**
 * Created by admin on 11-12-2017.
 */
public class CurrentThemeVO
{
    String themeId;
    String CurrentThemeName;
    String themeType;


    public String getThemeId()
    {
        return themeId;
    }

    public void setThemeId(String themeId)
    {
        this.themeId = themeId;
    }

    public String getCurrentThemeName()
    {
        return CurrentThemeName;
    }

    public void setCurrentThemeName(String currentThemeName)
    {
        CurrentThemeName = currentThemeName;
    }

    public String getThemeType()
    {
        return themeType;
    }

    public void setThemeType(String themeType)
    {
        this.themeType = themeType;
    }
}
