package com.vo.applicationManagerVOs;

/**
 * Created by SurajT on 7/30/2018.
 */
public class AppKycTemplateVO
{
    String labelId;
    String labelName;
    String alternateName;
    String functionalUsage;
    String supportedFileType;
    String partnerid;
    String criteria;


    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getSupportedFileType() {
        return supportedFileType;
    }

    public void setSupportedFileType(String supportedFileType) {
        this.supportedFileType = supportedFileType;
    }

    public String getFunctionalUsage() {
        return functionalUsage;
    }

    public void setFunctionalUsage(String functionalUsage) {
        this.functionalUsage = functionalUsage;
    }

    public String getAlternateName() {
        return alternateName;
    }

    public void setAlternateName(String alternateName) {
        this.alternateName = alternateName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}

