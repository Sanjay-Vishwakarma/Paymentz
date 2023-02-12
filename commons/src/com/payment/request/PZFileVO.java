package com.payment.request;

/**
 * Created by ThinkPadT410 on 7/5/2016.
 */
public class PZFileVO extends PZRequest
{
    private String filepath;
    private String fileName;

    public String getFilepath()
    {
        return filepath;
    }

    public void setFilepath(String filepath)
    {
        this.filepath = filepath;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}
