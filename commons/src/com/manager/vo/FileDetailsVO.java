package com.manager.vo;

import com.manager.enums.FileActionType;

import java.security.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/17/15
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileDetailsVO
{

    String mappingId;
    String filename;
    String fileType;
    String filePath;
    String   fileSize;
    String   dtstamp;
    String timestamp;
    String memberid;

    FileActionType fileActionType ;
    boolean success;
    String reasonOfFailure;
    String fieldName;
    String labelId;

    String movedFileName;

    public String getMappingId()
    {
        return mappingId;
    }

    public void setMappingId(String mappingId)
    {
        this.mappingId = mappingId;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(String fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getDtstamp()
    {
        return dtstamp;
    }

    public void setDtstamp(String dtstamp)
    {
        this.dtstamp = dtstamp;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getMemberid()
    {
        return memberid;
    }

    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }
    /*--------------*/
    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getReasonOfFailure()
    {
        return reasonOfFailure;
    }

    public void setReasonOfFailure(String reasonOfFailure)
    {
        this.reasonOfFailure = reasonOfFailure;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getLabelId()
    {
        return labelId;
    }

    public void setLabelId(String labelId)
    {
        this.labelId = labelId;
    }

    public FileActionType getFileActionType()
    {
        return fileActionType;
    }

    public void setFileActionType(FileActionType fileActionType)
    {
        this.fileActionType = fileActionType;
    }

    public String getMovedFileName()
    {
        return movedFileName;
    }

    public void setMovedFileName(String movedFileName)
    {
        this.movedFileName = movedFileName;
    }
}