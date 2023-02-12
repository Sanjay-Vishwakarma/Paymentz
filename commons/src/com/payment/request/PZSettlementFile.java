package com.payment.request;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/26/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZSettlementFile extends PZRequest
{
    public String getFilepath()
    {
        return filepath;
    }

    public void setFilepath(String filepath)
    {
        this.filepath = filepath;
    }

    private String filepath;

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}
