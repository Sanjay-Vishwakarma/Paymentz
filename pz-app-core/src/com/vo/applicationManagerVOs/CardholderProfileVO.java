package com.vo.applicationManagerVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/20/15
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="CardholderProfileVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardholderProfileVO
{
    @XmlElement(name="application_id")
    String application_id;

    @XmlElement(name="compliance_swapp")
    String compliance_swapp;

    @XmlElement(name="compliance_thirdpartyappform")
    String compliance_thirdpartyappform;

    @XmlElement(name="compliance_thirdpartysoft")
    String compliance_thirdpartysoft;

    @XmlElement(name="compliance_version")
    String compliance_version;

    @XmlElement(name="compliance_companiesorgateways")
    String compliance_companiesorgateways;

    @XmlElement(name="compliance_companiesorgateways_yes")
    String compliance_companiesorgateways_yes;

    @XmlElement(name="compliance_electronically")
    String compliance_electronically;

    @XmlElement(name="compliance_carddatastored")
    String compliance_carddatastored;

    @XmlElement(name="compliance_pcidsscompliant")
    String compliance_pcidsscompliant;

    @XmlElement(name="compliance_pcidsscompliant_yes")
    String compliance_pcidsscompliant_yes;

    @XmlElement(name="compliance_qualifiedsecurityassessor")
    String compliance_qualifiedsecurityassessor;

    @XmlElement(name="compliance_dateofcompliance")
    String compliance_dateofcompliance;

    @XmlElement(name="compliance_dateoflastscan")
    String compliance_dateoflastscan;

    @XmlElement(name="compliance_datacompromise")
    String compliance_datacompromise;

    @XmlElement(name="compliance_datacompromise_yes")
    String compliance_datacompromise_yes;

    @XmlElement(name="siteinspection_merchant")
    String siteinspection_merchant;

    @XmlElement(name="siteinspection_landlord")
    String siteinspection_landlord;

    @XmlElement(name="siteinspection_buildingtype")
    String siteinspection_buildingtype;

    @XmlElement(name="siteinspection_areazoned")
    String siteinspection_areazoned;

    @XmlElement(name="siteinspection_squarefootage")
    String siteinspection_squarefootage;

    @XmlElement(name="siteinspection_operatebusiness")
    String siteinspection_operatebusiness;

    @XmlElement(name="siteinspection_principal1")
    String siteinspection_principal1;

    @XmlElement(name="siteinspection_principal1_date")
    String siteinspection_principal1_date;

    @XmlElement(name="siteinspection_principal2")
    String siteinspection_principal2;

    @XmlElement(name="siteinspection_principal2_date")
    String siteinspection_principal2_date;

    @XmlElement(name="compliance_cispcompliant")
    String compliance_cispcompliant;

    @XmlElement(name="compliance_cispcompliant_yes")
    String compliance_cispcompliant_yes;

    @XmlElement(name="cardHolderProfileSaved")
    String cardHolderProfileSaved;


    public String getCompliance_cispcompliant()
    {
        return compliance_cispcompliant;
    }

    public void setCompliance_cispcompliant(String compliance_cispcompliant)
    {
        this.compliance_cispcompliant = compliance_cispcompliant;
    }

    public String getCompliance_cispcompliant_yes()
    {
        return compliance_cispcompliant_yes;
    }

    public void setCompliance_cispcompliant_yes(String compliance_cispcompliant_yes)
    {
        this.compliance_cispcompliant_yes = compliance_cispcompliant_yes;
    }

    public String getCompliance_pcidsscompliant_yes()
    {
        return compliance_pcidsscompliant_yes;
    }

    public void setCompliance_pcidsscompliant_yes(String compliance_pcidsscompliant_yes)
    {
        this.compliance_pcidsscompliant_yes = compliance_pcidsscompliant_yes;
    }

    public String getApplication_id()
    {
        return application_id;
    }

    public void setApplication_id(String application_id)
    {
        this.application_id = application_id;
    }

    public String getCompliance_swapp()
    {
        return compliance_swapp;
    }

    public void setCompliance_swapp(String compliance_swapp)
    {
        this.compliance_swapp = compliance_swapp;
    }

    public String getCompliance_thirdpartyappform()
    {
        return compliance_thirdpartyappform;
    }

    public void setCompliance_thirdpartyappform(String compliance_thirdpartyappform)
    {
        this.compliance_thirdpartyappform = compliance_thirdpartyappform;
    }

    public String getCompliance_thirdpartysoft()
    {
        return compliance_thirdpartysoft;
    }

    public void setCompliance_thirdpartysoft(String compliance_thirdpartysoft)
    {
        this.compliance_thirdpartysoft = compliance_thirdpartysoft;
    }

    public String getCompliance_version()
    {
        return compliance_version;
    }

    public void setCompliance_version(String compliance_version)
    {
        this.compliance_version = compliance_version;
    }

    public String getCompliance_companiesorgateways()
    {
        return compliance_companiesorgateways;
    }

    public void setCompliance_companiesorgateways(String compliance_companiesorgateways)
    {
        this.compliance_companiesorgateways = compliance_companiesorgateways;
    }

    public String getCompliance_companiesorgateways_yes()
    {
        return compliance_companiesorgateways_yes;
    }

    public void setCompliance_companiesorgateways_yes(String compliance_companiesorgateways_yes)
    {
        this.compliance_companiesorgateways_yes = compliance_companiesorgateways_yes;
    }

    public String getCompliance_electronically()
    {
        return compliance_electronically;
    }

    public void setCompliance_electronically(String compliance_electronically)
    {
        this.compliance_electronically = compliance_electronically;
    }

    public String getCompliance_carddatastored()
    {
        return compliance_carddatastored;
    }

    public void setCompliance_carddatastored(String compliance_carddatastored)
    {
        this.compliance_carddatastored = compliance_carddatastored;
    }

    public String getCompliance_pcidsscompliant()
    {
        return compliance_pcidsscompliant;
    }

    public void setCompliance_pcidsscompliant(String compliance_pcidsscompliant)
    {
        this.compliance_pcidsscompliant = compliance_pcidsscompliant;
    }

    public String getCompliance_qualifiedsecurityassessor()
    {
        return compliance_qualifiedsecurityassessor;
    }

    public void setCompliance_qualifiedsecurityassessor(String compliance_qualifiedsecurityassessor)
    {
        this.compliance_qualifiedsecurityassessor = compliance_qualifiedsecurityassessor;
    }

    public String getCompliance_dateofcompliance()
    {
        return compliance_dateofcompliance;
    }

    public void setCompliance_dateofcompliance(String compliance_dateofcompliance)
    {
        this.compliance_dateofcompliance = compliance_dateofcompliance;
    }

    public String getCompliance_dateoflastscan()
    {
        return compliance_dateoflastscan;
    }

    public void setCompliance_dateoflastscan(String compliance_dateoflastscan)
    {
        this.compliance_dateoflastscan = compliance_dateoflastscan;
    }

    public String getCompliance_datacompromise()
    {
        return compliance_datacompromise;
    }

    public void setCompliance_datacompromise(String compliance_datacompromise)
    {
        this.compliance_datacompromise = compliance_datacompromise;
    }

    public String getCompliance_datacompromise_yes()
    {
        return compliance_datacompromise_yes;
    }

    public void setCompliance_datacompromise_yes(String compliance_datacompromise_yes)
    {
        this.compliance_datacompromise_yes = compliance_datacompromise_yes;
    }

    public String getSiteinspection_merchant()
    {
        return siteinspection_merchant;
    }

    public void setSiteinspection_merchant(String siteinspection_merchant)
    {
        this.siteinspection_merchant = siteinspection_merchant;
    }

    public String getSiteinspection_landlord()
    {
        return siteinspection_landlord;
    }

    public void setSiteinspection_landlord(String siteinspection_landlord)
    {
        this.siteinspection_landlord = siteinspection_landlord;
    }

    public String getSiteinspection_buildingtype()
    {
        return siteinspection_buildingtype;
    }

    public void setSiteinspection_buildingtype(String siteinspection_buildingtype)
    {
        this.siteinspection_buildingtype = siteinspection_buildingtype;
    }

    public String getSiteinspection_areazoned()
    {
        return siteinspection_areazoned;
    }

    public void setSiteinspection_areazoned(String siteinspection_areazoned)
    {
        this.siteinspection_areazoned = siteinspection_areazoned;
    }

    public String getSiteinspection_squarefootage()
    {
        return siteinspection_squarefootage;
    }

    public void setSiteinspection_squarefootage(String siteinspection_squarefootage)
    {
        this.siteinspection_squarefootage = siteinspection_squarefootage;
    }

    public String getSiteinspection_operatebusiness()
    {
        return siteinspection_operatebusiness;
    }

    public void setSiteinspection_operatebusiness(String siteinspection_operatebusiness)
    {
        this.siteinspection_operatebusiness = siteinspection_operatebusiness;
    }

    public String getSiteinspection_principal1()
    {
        return siteinspection_principal1;
    }

    public void setSiteinspection_principal1(String siteinspection_principal1)
    {
        this.siteinspection_principal1 = siteinspection_principal1;
    }

    public String getSiteinspection_principal1_date()
    {
        return siteinspection_principal1_date;
    }

    public void setSiteinspection_principal1_date(String siteinspection_principal1_date)
    {
        this.siteinspection_principal1_date = siteinspection_principal1_date;
    }

    public String getSiteinspection_principal2()
    {
        return siteinspection_principal2;
    }

    public void setSiteinspection_principal2(String siteinspection_principal2)
    {
        this.siteinspection_principal2 = siteinspection_principal2;
    }

    public String getSiteinspection_principal2_date()
    {
        return siteinspection_principal2_date;
    }

    public void setSiteinspection_principal2_date(String siteinspection_principal2_date)
    {
        this.siteinspection_principal2_date = siteinspection_principal2_date;
    }

    public String getCardHolderProfileSaved()
    {
        return cardHolderProfileSaved;
    }

    public void setCardHolderProfileSaved(String cardHolderProfileSaved)
    {
        this.cardHolderProfileSaved = cardHolderProfileSaved;
    }

}
