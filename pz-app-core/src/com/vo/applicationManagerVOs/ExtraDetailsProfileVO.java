package com.vo.applicationManagerVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by kajal on 12/21/2015.
 */
@XmlRootElement(name="ExtraDetailsProfileVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtraDetailsProfileVO
{
    @XmlElement(name="application_id")
    String application_id;

    @XmlElement(name="company_financialReport")
    String company_financialReport;

    @XmlElement(name="company_financialReportYes")
    String company_financialReportYes;

    @XmlElement(name="financialReport_institution")
    String financialReport_institution;

    @XmlElement(name="financialReport_available")
    String financialReport_available;

    @XmlElement(name="financialReport_availableYes")
    String financialReport_availableYes;

    @XmlElement(name="ownerSince")
    String ownerSince;

    @XmlElement(name="socialSecurity")
    String socialSecurity;

    @XmlElement(name="company_formParticipation")
    String company_formParticipation;

    @XmlElement(name="financialObligation")
    String financialObligation;

    @XmlElement(name="compliance_punitiveSanction")
    String compliance_punitiveSanction;

    @XmlElement(name="compliance_punitiveSanctionYes")
    String compliance_punitiveSanctionYes;

    @XmlElement(name="workingExperience")
    String workingExperience;

    @XmlElement(name="goodsInsuranceOffered")
    String goodsInsuranceOffered;

    @XmlElement(name="fulfillment_productEmail")
    String fulfillment_productEmail;

    @XmlElement(name="fulfillment_productEmailYes")
    String fulfillment_productEmailYes;

    @XmlElement(name="blacklistedAccountClosed")
    String blacklistedAccountClosed;

    @XmlElement(name="blacklistedAccountClosedYes")
    String blacklistedAccountClosedYes;

    @XmlElement(name="shiping_deliveryMethod")
    String shiping_deliveryMethod;

    @XmlElement(name="transactionMonitoringProcess")
    String transactionMonitoringProcess;

    @XmlElement(name="operationalLicense")
    String operationalLicense;

    @XmlElement(name="supervisorregularcontrole")
    String supervisorregularcontrole;

    @XmlElement(name="deedOfAgreement")
    String deedOfAgreement;

    @XmlElement(name="deedOfAgreementYes")
    String deedOfAgreementYes;

    @XmlElement(name="extraDetailsProfileSaved")
    String extraDetailsProfileSaved;

    public String getApplication_id()
    {
        return application_id;
    }

    public void setApplication_id(String application_id)
    {
        this.application_id = application_id;
    }

    public String getCompany_financialReport()
    {
        return company_financialReport;
    }

    public void setCompany_financialReport(String company_financialReport)
    {
        this.company_financialReport = company_financialReport;
    }

    public String getCompany_financialReportYes()
    {
        return company_financialReportYes;
    }

    public void setCompany_financialReportYes(String company_financialReportYes)
    {
        this.company_financialReportYes = company_financialReportYes;
    }

    public String getFinancialReport_institution()
    {
        return financialReport_institution;
    }

    public void setFinancialReport_institution(String financialReport_institution)
    {
        this.financialReport_institution = financialReport_institution;
    }

    public String getFinancialReport_available()
    {
        return financialReport_available;
    }

    public void setFinancialReport_available(String financialReport_available)
    {
        this.financialReport_available = financialReport_available;
    }

    public String getFinancialReport_availableYes()
    {
        return financialReport_availableYes;
    }

    public void setFinancialReport_availableYes(String financialReport_availableYes)
    {
        this.financialReport_availableYes = financialReport_availableYes;
    }

    public String getOwnerSince()
    {
        return ownerSince;
    }

    public void setOwnerSince(String ownerSince)
    {
        this.ownerSince = ownerSince;
    }

    public String getSocialSecurity()
    {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity)
    {
        this.socialSecurity = socialSecurity;
    }

    public String getCompany_formParticipation()
    {
        return company_formParticipation;
    }

    public void setCompany_formParticipation(String company_formParticipation)
    {
        this.company_formParticipation = company_formParticipation;
    }

    public String getFinancialObligation()
    {
        return financialObligation;
    }

    public void setFinancialObligation(String financialObligation)
    {
        this.financialObligation = financialObligation;
    }

    public String getCompliance_punitiveSanction()
    {
        return compliance_punitiveSanction;
    }

    public void setCompliance_punitiveSanction(String compliance_punitiveSanction)
    {
        this.compliance_punitiveSanction = compliance_punitiveSanction;
    }

    public String getCompliance_punitiveSanctionYes()
    {
        return compliance_punitiveSanctionYes;
    }

    public void setCompliance_punitiveSanctionYes(String compliance_punitiveSanctionYes)
    {
        this.compliance_punitiveSanctionYes = compliance_punitiveSanctionYes;
    }

    public String getWorkingExperience()
    {
        return workingExperience;
    }

    public void setWorkingExperience(String workingExperience)
    {
        this.workingExperience = workingExperience;
    }

    public String getGoodsInsuranceOffered()
    {
        return goodsInsuranceOffered;
    }

    public void setGoodsInsuranceOffered(String goodsInsuranceOffered)
    {
        this.goodsInsuranceOffered = goodsInsuranceOffered;
    }

    public String getFulfillment_productEmail()
    {
        return fulfillment_productEmail;
    }

    public void setFulfillment_productEmail(String fulfillment_productEmail)
    {
        this.fulfillment_productEmail = fulfillment_productEmail;
    }

    public String getFulfillment_productEmailYes()
    {
        return fulfillment_productEmailYes;
    }

    public void setFulfillment_productEmailYes(String fulfillment_productEmailYes)
    {
        this.fulfillment_productEmailYes = fulfillment_productEmailYes;
    }

    public String getBlacklistedAccountClosed()
    {
        return blacklistedAccountClosed;
    }

    public void setBlacklistedAccountClosed(String blacklistedAccountClosed)
    {
        this.blacklistedAccountClosed = blacklistedAccountClosed;
    }

    public String getBlacklistedAccountClosedYes()
    {
        return blacklistedAccountClosedYes;
    }

    public void setBlacklistedAccountClosedYes(String blacklistedAccountClosedYes)
    {
        this.blacklistedAccountClosedYes = blacklistedAccountClosedYes;
    }

    public String getShiping_deliveryMethod()
    {
        return shiping_deliveryMethod;
    }

    public void setShiping_deliveryMethod(String shiping_deliveryMethod)
    {
        this.shiping_deliveryMethod = shiping_deliveryMethod;
    }

    public String getTransactionMonitoringProcess()
    {
        return transactionMonitoringProcess;
    }

    public void setTransactionMonitoringProcess(String transactionMonitoringProcess)
    {
        this.transactionMonitoringProcess = transactionMonitoringProcess;
    }

    public String getOperationalLicense()
    {
        return operationalLicense;
    }

    public void setOperationalLicense(String operationalLicense)
    {
        this.operationalLicense = operationalLicense;
    }

    public String getSupervisorregularcontrole()
    {
        return supervisorregularcontrole;
    }

    public void setSupervisorregularcontrole(String supervisorregularcontrole)
    {
        this.supervisorregularcontrole = supervisorregularcontrole;
    }

    public String getDeedOfAgreement()
    {
        return deedOfAgreement;
    }

    public void setDeedOfAgreement(String deedOfAgreement)
    {
        this.deedOfAgreement = deedOfAgreement;
    }

    public String getDeedOfAgreementYes()
    {
        return deedOfAgreementYes;
    }

    public void setDeedOfAgreementYes(String deedOfAgreementYes)
    {
        this.deedOfAgreementYes = deedOfAgreementYes;
    }

    public String getExtraDetailsProfileSaved()
    {
        return extraDetailsProfileSaved;
    }

    public void setExtraDetailsProfileSaved(String extraDetailsProfileSaved)
    {
        this.extraDetailsProfileSaved = extraDetailsProfileSaved;
    }
}
