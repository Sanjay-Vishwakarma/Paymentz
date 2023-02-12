package com.manager.enums;

/**
 * Created by Pradeep on 22/09/2015.
 */
public enum ContentTypeEnum
{
    PDF("application/pdf"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),//application/vnd.ms-excel
    PNG("image/png"),
    JPG("image/jpeg");

    private String contentType;

    ContentTypeEnum(String contentType)
    {
        this.contentType=contentType;
    }

    public String toString()
    {
        return contentType;
    }

    public static ContentTypeEnum getEnum(String value)
    {
        try
        {
            return ContentTypeEnum.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}
