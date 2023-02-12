package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 3/24/15
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public enum UploadFileType
{
    PDF("pdf"),
    EXCEL("xlsx"),
    PNG("png"),
    JPG("jpg");


    private String fileType;

    UploadFileType(String fileType)
    {
        this.fileType=fileType;
    }

    public String toString()
    {
        return fileType;
    }
}
