package com.directi.pg;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LoadProperties
{
    static Logger cat = new Logger(LoadProperties.class.getName());
    static Functions functions = new Functions();
    public static ResourceBundle getProperty(String propertiesClass)
    {
        cat.debug("begin of getProperty");
        ResourceBundle property = null;
        try
        {
            property = ResourceBundle.getBundle(propertiesClass, Locale.getDefault());
        }
        catch (MissingResourceException mre)
        {
            cat.debug("Properties not found:" + mre.toString());
        }
        cat.debug("end of getProperty");
        return property;
    }

    public static ResourceBundle getProperty(String propertiesClass,String language)
    {
        cat.debug("begin of getProperty");
        ResourceBundle property = null;
        try
        {
            if(functions.isValueNull(language))
            {
                Locale locale = new Locale(language);
                property = ResourceBundle.getBundle(propertiesClass, locale);
            }
            else
            {
                property = ResourceBundle.getBundle(propertiesClass, Locale.getDefault());
            }
        }
        catch (MissingResourceException mre)
        {
            cat.debug("Properties not found:" + mre.toString());
        }
        cat.debug("end of getProperty");
        return property;
    }

    public static ResourceBundle getProperty(String propertiesClass,String language,String country)
    {
        cat.debug("begin of getProperty");
        ResourceBundle property = null;
        try
        {
            if(functions.isValueNull(language) && functions.isValueNull(country))
            {
                Locale locale = new Locale(language,country);
                property = ResourceBundle.getBundle(propertiesClass, locale);
            }
            else
            {
                property = ResourceBundle.getBundle(propertiesClass, Locale.getDefault());
            }
        }
        catch (MissingResourceException mre)
        {
            cat.debug("Properties not found:" + mre.toString());
        }
        cat.debug("end of getProperty");
        return property;
    }

    /*public static ResourceBundle getProperty(String propertiesClass)
    {
        cat.debug("begin of getProperty");
        ResourceBundle property = null;
        try
        {
            property = ResourceBundle.getBundle(propertiesClass, Locale.getDefault());
        }
        catch (MissingResourceException mre)
        {
            cat.debug("Properties not found:" + mre.toString());
        }
        cat.debug("end of getProperty");
        return property;
    }*/
}
