package com.vo.applicationManagerVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/16/15
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "CompanyProfileVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompanyProfileVO
{
    @XmlElement(name="applicationId")
    String applicationId;

   /* @FormParam("merchantName")
    String merchantName;

    @FormParam("corporateName")
    String corporateName;

    @FormParam("locationAddress")
    String locationAddress;

    @FormParam("corporateAddress")
    String corporateAddress;

    @FormParam("merchantCity")
    String merchantCity;

    @FormParam("merchantState")
    String merchantState;

    @FormParam("merchantZipCode")
    String merchantZipCode;

    @FormParam("merchantCountry")
    String merchantCountry;

    @FormParam("merchantStreet")
    String merchantStreet;

    @FormParam("corporateCity")
    String corporateCity;

    @FormParam("corporateState")
    String corporateState;

    @FormParam("corporateZipCode")
    String corporateZipCode;

    @FormParam("corporateCountry")
    String corporateCountry;

    @FormParam("corporateStreet")
    String corporateStreet;

    @FormParam("contactName")
    String contactName;

    @FormParam("contactEmailAddress")
    String contactEmailAddress;

    @FormParam("contact_designation")
    String contact_designation;

    @FormParam("technicalContactName")
    String technicalContactName;

    @FormParam("technicalEmailAddress")
    String technicalEmailAddress;

    @FormParam("technical_designation")
    String technical_designation;

    @FormParam("contactnamePhoneNumber")
    String contactnamePhoneNumber;

    @FormParam("billingContactName")
    String billingContactName;

    @FormParam("billingEmailAddress")
    String billingEmailAddress;

    @FormParam("billing_designation")
    String billing_designation;*/

    /*@FormParam("companyRegistrationNumber")
    String companyRegistrationNumber;

    @FormParam("vatIdentification")
    String vatIdentification;*/

    @XmlElement(name="companyRegisteredEU")
    String companyRegisteredEU;

    @XmlElement(name="companyBankruptcy")
    String companyBankruptcy;

    @XmlElement(name="companyBankruptcydate")
    String companyBankruptcydate;

    @XmlElement(name="companyTypeOfBusiness")
    String companyTypeOfBusiness;

    /*@FormParam("registeredCorporateName")
    String registeredCorporateName;

    @FormParam("registeredDirectors")
    String registeredDirectors;

    @FormParam("registeredDirectorsAddress")
    String registeredDirectorsAddress;

    @FormParam("registeredDirectorsCity")
    String registeredDirectorsCity;

    @FormParam("registeredDirectorsState")
    String registeredDirectorsState;

    @FormParam("registeredDirectorsPostalcode")
    String registeredDirectorsPostalcode;

    @FormParam("registeredDirectorsCountry")
    String registeredDirectorsCountry;

    @FormParam("registeredDirectorsStreet")
    String registeredDirectorsStreet;*/

    @XmlElement(name="countryOfRegistration")
    String countryOfRegistration;

    @XmlElement(name="companyLengthOfTimeInBusiness")
    String companyLengthOfTimeInBusiness;

    @XmlElement(name="companyCapitalResources")
    String companyCapitalResources;

    @XmlElement(name="companyTurnoverLastYear")
    String companyTurnoverLastYear;

    @XmlElement(name="companyNumberOfEmployees")
    String companyNumberOfEmployees;

    @XmlElement(name="isCompanyProfileSaved")
    String isCompanyProfileSaved;

    /*@FormParam("contactname_telnocc1")
    String contactname_telnocc1;*/

    @XmlElement(name="company_currencylastyear")
    String company_currencylastyear;

    @XmlElement(name="company_turnoverlastyear_unit")
    String company_turnoverlastyear_unit;

    //Add extra Field
    /*@FormParam("technical_telephonenumber")
    String Technical_telephonenumber;

    @FormParam("financial_telephonenumber")
    String Financial_telephonenumber;

    @FormParam("company_Date_Registration")
    String Company_Date_Registration;

    @FormParam("EUCompany_details")
    String EUCompany_details;

    @FormParam("corporate_Date_Registration")
    String corporate_Date_Registration;*/

    @XmlElement(name="license_required")
    String License_required;

    @XmlElement(name="license_Permission")
    String License_Permission;

    /*@FormParam("clients_country")
    String clients_country;

    @FormParam("corporateVat")
    String corporateVat;

    @FormParam("CorporateCountryRegistr")
    String CorporateCountryRegistr;

    @FormParam("CorporateCompanyRegistrNumber")
    String CorporateCompanyRegistrNumber;

    @FormParam("technicalphonecc1")
    String technicalphonecc1;

    @FormParam("financialphonecc1")
    String financialphonecc1;

    @FormParam("companyphonecc1")
    String Companyphonecc1;

    @FormParam("companyTelephoneNO")
    String CompanyTelephoneNO;

    @FormParam("companyFax")
    String CompanyFax;

    @FormParam("companyEmailAddress")
    String CompanyEmailAddress;*/

    @XmlElement(name="time_business")
    String time_business;

    //ADD NEW
    /*@FormParam("skypeIMaddress")
    String skypeIMaddress;*/

    @XmlElement(name="legalProceeding")
    String legalProceeding;

    /*//ADD new
    @FormParam("fedraltaxid")
    String fedraltaxid;

    @FormParam("EURegistrationNumber")
    String EURegistrationNumber;*/

    // Wirecard requirement added in Company Profile
    @XmlElement(name="startup_business")
    String startup_business;

    /*@FormParam("cbk_contactperson")
    String cbk_contactperson;

    @FormParam("cbk_email")
    String cbk_email;

    @FormParam("cbk_telephonenumber")
    String cbk_telephonenumber;

    @FormParam("cbk_phonecc")
    String cbk_phonecc;

    @FormParam("cbk_designation")
    String cbk_designation;

    @FormParam("pci_contactperson")
    String pci_contactperson;

    @FormParam("pci_email")
    String pci_email;

    @FormParam("pci_telephonenumber")
    String pci_telephonenumber;

    @FormParam("pci_phonecc")
    String pci_phonecc;

    @FormParam("pci_designation")
    String pci_designation;*/

    @XmlElement(name="iscompany_insured")
    String iscompany_insured;

    @XmlElement(name="insured_companyname")
    String insured_companyname;

    @XmlElement(name="insured_amount")
    String insured_amount;

    @XmlElement(name="insured_currency")
    String insured_currency;

    @XmlElement(name="main_business_partner")
    String main_business_partner;

    @XmlElement(name="loans")
    String loans;

    @XmlElement(name="income_economic_activity")
    String income_economic_activity;

    @XmlElement(name="interest_income")
    String interest_income;

    @XmlElement(name="investments")
    String investments;

    @XmlElement(name="income_sources_other")
    String income_sources_other;

    @XmlElement(name="income_sources_other_yes")
    String income_sources_other_yes;



    /*@FormParam("merchant_addressproof")
    String merchant_addressproof;

    @FormParam("merchant_addressId")
    String merchant_addressId;

    @FormParam("corporate_addressproof")
    String corporate_addressproof;

    @FormParam("corporate_addressId")
    String corporate_addressId;

    @FormParam("corporate_addressproof")
    String registered_directors_addressproof;

    @FormParam("corporate_addressId")
    String registered_directors_addressId;*/

    Map<String, AddressIdentificationVO> companyProfile_addressVOMap;

    Map<String, ContactDetailsVO> companyProfile_contactInfoVOMap;

    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId(String applicationId)
    {
        this.applicationId = applicationId;
    }

    public String getCountryOfRegistration()
    {
        return countryOfRegistration;
    }

    public String getCompanyRegisteredEU()
    {
        return companyRegisteredEU;
    }

    public void setCompanyRegisteredEU(String companyRegisteredEU)
    {
        this.companyRegisteredEU = companyRegisteredEU;
    }

    public String getCompanyBankruptcy()
    {
        return companyBankruptcy;
    }

    public void setCompanyBankruptcy(String companyBankruptcy)
    {
        this.companyBankruptcy = companyBankruptcy;
    }

    public String getCompanyBankruptcydate()
    {
        return companyBankruptcydate;
    }

    public void setCompanyBankruptcydate(String companyBankruptcydate)
    {
        this.companyBankruptcydate = companyBankruptcydate;
    }

    public String getCompanyTypeOfBusiness()
    {
        return companyTypeOfBusiness;
    }

    public void setCompanyTypeOfBusiness(String companyTypeOfBusiness)
    {
        this.companyTypeOfBusiness = companyTypeOfBusiness;
    }

    public void setCountryOfRegistration(String countryOfRegistration)
    {
        this.countryOfRegistration = countryOfRegistration;
    }

    public String getCompanyLengthOfTimeInBusiness()
    {
        return companyLengthOfTimeInBusiness;
    }

    public void setCompanyLengthOfTimeInBusiness(String companyLengthOfTimeInBusiness)
    {
        this.companyLengthOfTimeInBusiness = companyLengthOfTimeInBusiness;
    }

    public String getCompanyCapitalResources()
    {
        return companyCapitalResources;
    }

    public void setCompanyCapitalResources(String companyCapitalResources)
    {
        this.companyCapitalResources = companyCapitalResources;
    }

    public String getCompanyTurnoverLastYear()
    {
        return companyTurnoverLastYear;
    }

    public void setCompanyTurnoverLastYear(String companyTurnoverLastYear)
    {
        this.companyTurnoverLastYear = companyTurnoverLastYear;
    }

    public String getCompanyNumberOfEmployees()
    {
        return companyNumberOfEmployees;
    }

    public void setCompanyNumberOfEmployees(String companyNumberOfEmployees)
    {
        this.companyNumberOfEmployees = companyNumberOfEmployees;
    }

    public String getCompanyProfileSaved()
    {
        return isCompanyProfileSaved;
    }

    public void setCompanyProfileSaved(String isCompanyProfileSaved)
    {
        this.isCompanyProfileSaved = isCompanyProfileSaved;
    }

    public String getCompany_currencylastyear()
    {
        return company_currencylastyear;
    }

    public void setCompany_currencylastyear(String company_currencylastyear)
    {
        this.company_currencylastyear = company_currencylastyear;
    }

    public String getCompany_turnoverlastyear_unit()
    {
        return company_turnoverlastyear_unit;
    }

    public void setCompany_turnoverlastyear_unit(String company_turnoverlastyear_unit)
    {
        this.company_turnoverlastyear_unit = company_turnoverlastyear_unit;
    }

    public String getLicense_required()
    {
        return License_required;
    }

    public void setLicense_required(String license_required)
    {
        License_required = license_required;
    }

    public String getLicense_Permission()
    {
        return License_Permission;
    }

    public void setLicense_Permission(String license_Permission)
    {
        License_Permission = license_Permission;
    }

    public String getTime_business()
    {
        return time_business;
    }

    public void setTime_business(String time_business)
    {
        this.time_business = time_business;
    }

    public String getLegalProceeding()
    {
        return legalProceeding;
    }

    public void setLegalProceeding(String legalProceeding)
    {
        this.legalProceeding = legalProceeding;
    }

    public String getStartup_business()
    {
        return startup_business;
    }

    public void setStartup_business(String startup_business)
    {
        this.startup_business = startup_business;
    }

    public String getIscompany_insured()
    {
        return iscompany_insured;
    }

    public void setIscompany_insured(String iscompany_insured)
    {
        this.iscompany_insured = iscompany_insured;
    }

    public String getInsured_companyname()
    {
        return insured_companyname;
    }

    public void setInsured_companyname(String insured_companyname)
    {
        this.insured_companyname = insured_companyname;
    }

    public String getInsured_amount()
    {
        return insured_amount;
    }

    public void setInsured_amount(String insured_amount)
    {
        this.insured_amount = insured_amount;
    }

    public String getInsured_currency()
    {
        return insured_currency;
    }

    public void setInsured_currency(String insured_currency)
    {
        this.insured_currency = insured_currency;
    }

    public String getMain_business_partner()
    {
        return main_business_partner;
    }

    public void setMain_business_partner(String main_business_partner)
    {
        this.main_business_partner = main_business_partner;
    }

    public String getLoans()
    {
        return loans;
    }

    public void setLoans(String loans)
    {
        this.loans = loans;
    }

    public String getIncome_economic_activity()
    {
        return income_economic_activity;
    }

    public void setIncome_economic_activity(String income_economic_activity)
    {
        this.income_economic_activity = income_economic_activity;
    }

    public String getInterest_income()
    {
        return interest_income;
    }

    public void setInterest_income(String interest_income)
    {
        this.interest_income = interest_income;
    }

    public String getInvestments()
    {
        return investments;
    }

    public void setInvestments(String investments)
    {
        this.investments = investments;
    }

    public String getIncome_sources_other()
    {
        return income_sources_other;
    }

    public void setIncome_sources_other(String income_sources_other)
    {
        this.income_sources_other = income_sources_other;
    }

    public String getIncome_sources_other_yes()
    {
        return income_sources_other_yes;
    }

    public void setIncome_sources_other_yes(String income_sources_other_yes)
    {
        this.income_sources_other_yes = income_sources_other_yes;
    }

    public Map<String, AddressIdentificationVO> getCompanyProfile_addressVOMap()
    {
        return companyProfile_addressVOMap;
    }

    public void setCompanyProfile_addressVOMap(Map<String, AddressIdentificationVO> companyProfile_addressVOMap)
    {
        this.companyProfile_addressVOMap = companyProfile_addressVOMap;
    }

    public Map<String, ContactDetailsVO> getCompanyProfile_contactInfoVOMap()
    {
        return companyProfile_contactInfoVOMap;
    }

    public void setCompanyProfile_contactInfoVOMap(Map<String, ContactDetailsVO> companyProfile_contactInfoVOMap)
    {
        this.companyProfile_contactInfoVOMap = companyProfile_contactInfoVOMap;
    }
}
