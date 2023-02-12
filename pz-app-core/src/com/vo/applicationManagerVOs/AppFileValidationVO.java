package com.vo.applicationManagerVOs;

import com.enums.AppUploadFileType;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 3/24/15
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppFileValidationVO
{

    StringBuffer totalFileExtensionCheckOn=new StringBuffer();
    //after this the validation
    AppUploadFileType fileType;
    boolean fileExtensionValidation;
    boolean fileContentValidation;

    public StringBuffer getTotalFileExtensionCheckOn()
    {
        return totalFileExtensionCheckOn;
    }

    public void setTotalFileExtensionCheckOn(String totalFileExtensionCheckOn)
    {
        if(this.totalFileExtensionCheckOn.length()>0)
        {
            this.totalFileExtensionCheckOn .append(" OR ");
        }
        this.totalFileExtensionCheckOn .append(totalFileExtensionCheckOn);
    }

    public AppUploadFileType getFileType()
    {
        return fileType;
    }

    public void setFileType(AppUploadFileType fileType)
    {
        if(this.fileExtensionValidation && this.fileType==null)
            this.fileType = fileType;

    }

    public boolean isFileExtensionValidation()
    {
        return fileExtensionValidation;
    }

    public void setFileExtensionValidation(boolean fileExtensionValidation)
    {
        if(!this.fileExtensionValidation)
            this.fileExtensionValidation = fileExtensionValidation;
    }

    public boolean isFileContentValidation()
    {
        return fileContentValidation;
    }

    public void setFileContentValidation(boolean fileContentValidation)
    {
        if(fileExtensionValidation && !this.fileContentValidation)
            this.fileContentValidation = fileContentValidation;
    }
}
