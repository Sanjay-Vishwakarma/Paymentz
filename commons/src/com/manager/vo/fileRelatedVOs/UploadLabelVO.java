package com.manager.vo.fileRelatedVOs;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 3/10/15
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadLabelVO
{
    String labelId;
    String labelName;
    String alternateName;
    String functionalUsage;
    String supportedFileType;

    public String getLabelId()
    {
        return labelId;
    }

    public void setLabelId(String labelId)
    {
        this.labelId = labelId;
    }

    public String getLabelName()
    {
        return labelName;
    }

    public void setLabelName(String labelName)
    {
        this.labelName = labelName;
    }

    public String getAlternateName()
    {
        return alternateName;
    }

    public void setAlternateName(String alternateName)
    {
        this.alternateName = alternateName;
    }

    public String getFunctionalUsage()
    {
        return functionalUsage;
    }

    public void setFunctionalUsage(String functionalUsage)
    {
        this.functionalUsage = functionalUsage;
    }

    public String getSupportedFileType()
    {
        return supportedFileType;
    }

    public void setSupportedFileType(String supportedFileType)
    {
        this.supportedFileType = supportedFileType;
    }
}
