package com.vo.applicationManagerVOs;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 08-09-2017.
 */
@XmlRootElement(name = "CompanyProfileVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class CompanyProfileRequestVO
{
    @FormParam("MerchantApplicationForm.CompanyProfileVO.applicationId")
    String applicationId;

   @FormParam("MerchantApplicationForm.CompanyProfileVO.merchantName")
    String merchantName;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateName")
    String corporateName;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.locationAddress")
    String locationAddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateAddress")
    String corporateAddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.merchantCity")
    String merchantCity;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.merchantState")
    String merchantState;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.merchantZipCode")
    String merchantZipCode;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.merchantCountry")
    String merchantCountry;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.merchantStreet")
    String merchantStreet;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateCity")
    String corporateCity;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateState")
    String corporateState;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateZipCode")
    String corporateZipCode;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateCountry")
    String corporateCountry;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateStreet")
    String corporateStreet;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.contactName")
    String contactName;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.contactEmailAddress")
    String contactEmailAddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.contact_designation")
    String contact_designation;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.technicalContactName")
    String technicalContactName;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.technicalEmailAddress")
    String technicalEmailAddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.technical_designation")
    String technical_designation;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.contactnamePhoneNumber")
    String contactnamePhoneNumber;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.billingContactName")
    String billingContactName;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.billingEmailAddress")
    String billingEmailAddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.billing_designation")
    String billing_designation;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyRegistrationNumber")
    String companyRegistrationNumber;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.vatIdentification")
    String vatIdentification;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyRegisteredEU")
    String companyRegisteredEU;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyBankruptcy")
    String companyBankruptcy;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyBankruptcydate")
    String companyBankruptcydate;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyTypeOfBusiness")
    String companyTypeOfBusiness;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredCorporateName")
    String registeredCorporateName;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredDirectors")
    String registeredDirectors;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredDirectorsAddress")
    String registeredDirectorsAddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredDirectorsCity")
    String registeredDirectorsCity;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredDirectorsState")
    String registeredDirectorsState;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredDirectorsPostalcode")
    String registeredDirectorsPostalcode;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredDirectorsCountry")
    String registeredDirectorsCountry;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.registeredDirectorsStreet")
    String registeredDirectorsStreet;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.countryOfRegistration")
    String countryOfRegistration;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyLengthOfTimeInBusiness")
    String companyLengthOfTimeInBusiness;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyCapitalResources")
    String companyCapitalResources;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyTurnoverLastYear")
    String companyTurnoverLastYear;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyNumberOfEmployees")
    String companyNumberOfEmployees;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.isCompanyProfileSaved")
    String isCompanyProfileSaved;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.contactname_telnocc1")
    String contactname_telnocc1;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.company_currencylastyear")
    String company_currencylastyear;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.company_turnoverlastyear_unit")
    String company_turnoverlastyear_unit;

    //Add extra Field
    @FormParam("MerchantApplicationForm.CompanyProfileVO.technical_telephonenumber")
    String Technical_telephonenumber;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.financial_telephonenumber")
    String Financial_telephonenumber;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.company_Date_Registration")
    String Company_Date_Registration;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.EUCompany_details")
    String EUCompany_details;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporate_Date_Registration")
    String corporate_Date_Registration;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.license_required")
    String License_required;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.license_Permission")
    String License_Permission;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.clients_country")
    String clients_country;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporateVat")
    String corporateVat;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.CorporateCountryRegistr")
    String CorporateCountryRegistr;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.CorporateCompanyRegistrNumber")
    String CorporateCompanyRegistrNumber;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.technicalphonecc1")
    String technicalphonecc1;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.financialphonecc1")
    String financialphonecc1;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyphonecc1")
    String Companyphonecc1;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyTelephoneNO")
    String CompanyTelephoneNO;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyFax")
    String CompanyFax;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.companyEmailAddress")
    String CompanyEmailAddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.time_business")
    String time_business;

    //ADD NEW
    @FormParam("MerchantApplicationForm.CompanyProfileVO.skypeIMaddress")
    String skypeIMaddress;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.legalProceeding")
    String legalProceeding;

    //ADD new
    @FormParam("MerchantApplicationForm.CompanyProfileVO.fedraltaxid")
    String fedraltaxid;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.EURegistrationNumber")
    String EURegistrationNumber;

    // Wirecard requirement added in Company Profile
    @FormParam("MerchantApplicationForm.CompanyProfileVO.startup_business")
    String startup_business;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.cbk_contactperson")
    String cbk_contactperson;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.cbk_email")
    String cbk_email;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.cbk_telephonenumber")
    String cbk_telephonenumber;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.cbk_phonecc")
    String cbk_phonecc;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.cbk_designation")
    String cbk_designation;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.pci_contactperson")
    String pci_contactperson;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.pci_email")
    String pci_email;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.pci_telephonenumber")
    String pci_telephonenumber;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.pci_phonecc")
    String pci_phonecc;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.pci_designation")
    String pci_designation;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.iscompany_insured")
    String iscompany_insured;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.insured_companyname")
    String insured_companyname;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.insured_amount")
    String insured_amount;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.insured_currency")
    String insured_currency;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.main_business_partner")
    String main_business_partner;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.loans")
    String loans;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.income_economic_activity")
    String income_economic_activity;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.interest_income")
    String interest_income;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.investments")
    String investments;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.income_sources_other")
    String income_sources_other;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.income_sources_other_yes")
    String income_sources_other_yes;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.merchant_addressproof")
    String merchant_addressproof;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.merchant_addressId")
    String merchant_addressId;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporate_addressproof")
    String corporate_addressproof;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporate_addressId")
    String corporate_addressId;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporate_addressproof")
    String registered_directors_addressproof;

    @FormParam("MerchantApplicationForm.CompanyProfileVO.corporate_addressId")
    String registered_directors_addressId;


   @FormParam("MerchantApplicationForm.CompanyProfileVO.passportissuedate")
   String passportissuedate;

   /* Map<String, AddressIdentificationRequestVO> companyProfile_addressVOMap;

    Map<String, ContactDetailsRequestVO> companyProfile_contactInfoVOMap;
*/
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

 public String getMerchantName()
 {
  return merchantName;
 }

 public void setMerchantName(String merchantName)
 {
  this.merchantName = merchantName;
 }

 public String getCorporateName()
 {
  return corporateName;
 }

 public void setCorporateName(String corporateName)
 {
  this.corporateName = corporateName;
 }

 public String getLocationAddress()
 {
  return locationAddress;
 }

 public void setLocationAddress(String locationAddress)
 {
  this.locationAddress = locationAddress;
 }

 public String getCorporateAddress()
 {
  return corporateAddress;
 }

 public void setCorporateAddress(String corporateAddress)
 {
  this.corporateAddress = corporateAddress;
 }

 public String getMerchantCity()
 {
  return merchantCity;
 }

 public void setMerchantCity(String merchantCity)
 {
  this.merchantCity = merchantCity;
 }

 public String getMerchantState()
 {
  return merchantState;
 }

 public void setMerchantState(String merchantState)
 {
  this.merchantState = merchantState;
 }

 public String getMerchantZipCode()
 {
  return merchantZipCode;
 }

 public void setMerchantZipCode(String merchantZipCode)
 {
  this.merchantZipCode = merchantZipCode;
 }

 public String getMerchantCountry()
 {
  return merchantCountry;
 }

 public void setMerchantCountry(String merchantCountry)
 {
  this.merchantCountry = merchantCountry;
 }

 public String getMerchantStreet()
 {
  return merchantStreet;
 }

 public void setMerchantStreet(String merchantStreet)
 {
  this.merchantStreet = merchantStreet;
 }

 public String getCorporateCity()
 {
  return corporateCity;
 }

 public void setCorporateCity(String corporateCity)
 {
  this.corporateCity = corporateCity;
 }

 public String getCorporateState()
 {
  return corporateState;
 }

 public void setCorporateState(String corporateState)
 {
  this.corporateState = corporateState;
 }

 public String getCorporateZipCode()
 {
  return corporateZipCode;
 }

 public void setCorporateZipCode(String corporateZipCode)
 {
  this.corporateZipCode = corporateZipCode;
 }

 public String getCorporateCountry()
 {
  return corporateCountry;
 }

 public void setCorporateCountry(String corporateCountry)
 {
  this.corporateCountry = corporateCountry;
 }

 public String getCorporateStreet()
 {
  return corporateStreet;
 }

 public void setCorporateStreet(String corporateStreet)
 {
  this.corporateStreet = corporateStreet;
 }

 public String getContactName()
 {
  return contactName;
 }

 public void setContactName(String contactName)
 {
  this.contactName = contactName;
 }

 public String getContactEmailAddress()
 {
  return contactEmailAddress;
 }

 public void setContactEmailAddress(String contactEmailAddress)
 {
  this.contactEmailAddress = contactEmailAddress;
 }

 public String getContact_designation()
 {
  return contact_designation;
 }

 public void setContact_designation(String contact_designation)
 {
  this.contact_designation = contact_designation;
 }

 public String getTechnicalContactName()
 {
  return technicalContactName;
 }

 public void setTechnicalContactName(String technicalContactName)
 {
  this.technicalContactName = technicalContactName;
 }

 public String getTechnicalEmailAddress()
 {
  return technicalEmailAddress;
 }

 public void setTechnicalEmailAddress(String technicalEmailAddress)
 {
  this.technicalEmailAddress = technicalEmailAddress;
 }

 public String getTechnical_designation()
 {
  return technical_designation;
 }

 public void setTechnical_designation(String technical_designation)
 {
  this.technical_designation = technical_designation;
 }

 public String getContactnamePhoneNumber()
 {
  return contactnamePhoneNumber;
 }

 public void setContactnamePhoneNumber(String contactnamePhoneNumber)
 {
  this.contactnamePhoneNumber = contactnamePhoneNumber;
 }

 public String getBillingContactName()
 {
  return billingContactName;
 }

 public void setBillingContactName(String billingContactName)
 {
  this.billingContactName = billingContactName;
 }

 public String getBillingEmailAddress()
 {
  return billingEmailAddress;
 }

 public void setBillingEmailAddress(String billingEmailAddress)
 {
  this.billingEmailAddress = billingEmailAddress;
 }

 public String getBilling_designation()
 {
  return billing_designation;
 }

 public void setBilling_designation(String billing_designation)
 {
  this.billing_designation = billing_designation;
 }

 public String getCompanyRegistrationNumber()
 {
  return companyRegistrationNumber;
 }

 public void setCompanyRegistrationNumber(String companyRegistrationNumber)
 {
  this.companyRegistrationNumber = companyRegistrationNumber;
 }

 public String getVatIdentification()
 {
  return vatIdentification;
 }

 public void setVatIdentification(String vatIdentification)
 {
  this.vatIdentification = vatIdentification;
 }

 public String getRegisteredCorporateName()
 {
  return registeredCorporateName;
 }

 public void setRegisteredCorporateName(String registeredCorporateName)
 {
  this.registeredCorporateName = registeredCorporateName;
 }

 public String getRegisteredDirectors()
 {
  return registeredDirectors;
 }

 public void setRegisteredDirectors(String registeredDirectors)
 {
  this.registeredDirectors = registeredDirectors;
 }

 public String getRegisteredDirectorsAddress()
 {
  return registeredDirectorsAddress;
 }

 public void setRegisteredDirectorsAddress(String registeredDirectorsAddress)
 {
  this.registeredDirectorsAddress = registeredDirectorsAddress;
 }

 public String getRegisteredDirectorsCity()
 {
  return registeredDirectorsCity;
 }

 public void setRegisteredDirectorsCity(String registeredDirectorsCity)
 {
  this.registeredDirectorsCity = registeredDirectorsCity;
 }

 public String getRegisteredDirectorsState()
 {
  return registeredDirectorsState;
 }

 public void setRegisteredDirectorsState(String registeredDirectorsState)
 {
  this.registeredDirectorsState = registeredDirectorsState;
 }

 public String getRegisteredDirectorsPostalcode()
 {
  return registeredDirectorsPostalcode;
 }

 public void setRegisteredDirectorsPostalcode(String registeredDirectorsPostalcode)
 {
  this.registeredDirectorsPostalcode = registeredDirectorsPostalcode;
 }

 public String getRegisteredDirectorsCountry()
 {
  return registeredDirectorsCountry;
 }

 public void setRegisteredDirectorsCountry(String registeredDirectorsCountry)
 {
  this.registeredDirectorsCountry = registeredDirectorsCountry;
 }

 public String getRegisteredDirectorsStreet()
 {
  return registeredDirectorsStreet;
 }

 public void setRegisteredDirectorsStreet(String registeredDirectorsStreet)
 {
  this.registeredDirectorsStreet = registeredDirectorsStreet;
 }

 public String getIsCompanyProfileSaved()
 {
  return isCompanyProfileSaved;
 }

 public void setIsCompanyProfileSaved(String isCompanyProfileSaved)
 {
  this.isCompanyProfileSaved = isCompanyProfileSaved;
 }

 public String getContactname_telnocc1()
 {
  return contactname_telnocc1;
 }

 public void setContactname_telnocc1(String contactname_telnocc1)
 {
  this.contactname_telnocc1 = contactname_telnocc1;
 }

 public String getTechnical_telephonenumber()
 {
  return Technical_telephonenumber;
 }

 public void setTechnical_telephonenumber(String technical_telephonenumber)
 {
  Technical_telephonenumber = technical_telephonenumber;
 }

 public String getFinancial_telephonenumber()
 {
  return Financial_telephonenumber;
 }

 public void setFinancial_telephonenumber(String financial_telephonenumber)
 {
  Financial_telephonenumber = financial_telephonenumber;
 }

 public String getCompany_Date_Registration()
 {
  return Company_Date_Registration;
 }

 public void setCompany_Date_Registration(String company_Date_Registration)
 {
  Company_Date_Registration = company_Date_Registration;
 }

 public String getEUCompany_details()
 {
  return EUCompany_details;
 }

 public void setEUCompany_details(String EUCompany_details)
 {
  this.EUCompany_details = EUCompany_details;
 }

 public String getCorporate_Date_Registration()
 {
  return corporate_Date_Registration;
 }

 public void setCorporate_Date_Registration(String corporate_Date_Registration)
 {
  this.corporate_Date_Registration = corporate_Date_Registration;
 }

 public String getClients_country()
 {
  return clients_country;
 }

 public void setClients_country(String clients_country)
 {
  this.clients_country = clients_country;
 }

 public String getCorporateVat()
 {
  return corporateVat;
 }

 public void setCorporateVat(String corporateVat)
 {
  this.corporateVat = corporateVat;
 }

 public String getCorporateCountryRegistr()
 {
  return CorporateCountryRegistr;
 }

 public void setCorporateCountryRegistr(String corporateCountryRegistr)
 {
  CorporateCountryRegistr = corporateCountryRegistr;
 }

 public String getCorporateCompanyRegistrNumber()
 {
  return CorporateCompanyRegistrNumber;
 }

 public void setCorporateCompanyRegistrNumber(String corporateCompanyRegistrNumber)
 {
  CorporateCompanyRegistrNumber = corporateCompanyRegistrNumber;
 }

 public String getTechnicalphonecc1()
 {
  return technicalphonecc1;
 }

 public void setTechnicalphonecc1(String technicalphonecc1)
 {
  this.technicalphonecc1 = technicalphonecc1;
 }

 public String getFinancialphonecc1()
 {
  return financialphonecc1;
 }

 public void setFinancialphonecc1(String financialphonecc1)
 {
  this.financialphonecc1 = financialphonecc1;
 }

 public String getCompanyphonecc1()
 {
  return Companyphonecc1;
 }

 public void setCompanyphonecc1(String companyphonecc1)
 {
  Companyphonecc1 = companyphonecc1;
 }

 public String getCompanyTelephoneNO()
 {
  return CompanyTelephoneNO;
 }

 public void setCompanyTelephoneNO(String companyTelephoneNO)
 {
  CompanyTelephoneNO = companyTelephoneNO;
 }

 public String getCompanyFax()
 {
  return CompanyFax;
 }

 public void setCompanyFax(String companyFax)
 {
  CompanyFax = companyFax;
 }

 public String getCompanyEmailAddress()
 {
  return CompanyEmailAddress;
 }

 public void setCompanyEmailAddress(String companyEmailAddress)
 {
  CompanyEmailAddress = companyEmailAddress;
 }

 public String getSkypeIMaddress()
 {
  return skypeIMaddress;
 }

 public void setSkypeIMaddress(String skypeIMaddress)
 {
  this.skypeIMaddress = skypeIMaddress;
 }

 public String getFedraltaxid()
 {
  return fedraltaxid;
 }

 public void setFedraltaxid(String fedraltaxid)
 {
  this.fedraltaxid = fedraltaxid;
 }

 public String getEURegistrationNumber()
 {
  return EURegistrationNumber;
 }

 public void setEURegistrationNumber(String EURegistrationNumber)
 {
  this.EURegistrationNumber = EURegistrationNumber;
 }

 public String getCbk_contactperson()
 {
  return cbk_contactperson;
 }

 public void setCbk_contactperson(String cbk_contactperson)
 {
  this.cbk_contactperson = cbk_contactperson;
 }

 public String getCbk_email()
 {
  return cbk_email;
 }

 public void setCbk_email(String cbk_email)
 {
  this.cbk_email = cbk_email;
 }

 public String getCbk_telephonenumber()
 {
  return cbk_telephonenumber;
 }

 public void setCbk_telephonenumber(String cbk_telephonenumber)
 {
  this.cbk_telephonenumber = cbk_telephonenumber;
 }

 public String getCbk_phonecc()
 {
  return cbk_phonecc;
 }

 public void setCbk_phonecc(String cbk_phonecc)
 {
  this.cbk_phonecc = cbk_phonecc;
 }

 public String getCbk_designation()
 {
  return cbk_designation;
 }

 public void setCbk_designation(String cbk_designation)
 {
  this.cbk_designation = cbk_designation;
 }

 public String getPci_contactperson()
 {
  return pci_contactperson;
 }

 public void setPci_contactperson(String pci_contactperson)
 {
  this.pci_contactperson = pci_contactperson;
 }

 public String getPci_email()
 {
  return pci_email;
 }

 public void setPci_email(String pci_email)
 {
  this.pci_email = pci_email;
 }

 public String getPci_telephonenumber()
 {
  return pci_telephonenumber;
 }

 public void setPci_telephonenumber(String pci_telephonenumber)
 {
  this.pci_telephonenumber = pci_telephonenumber;
 }

 public String getPci_phonecc()
 {
  return pci_phonecc;
 }

 public void setPci_phonecc(String pci_phonecc)
 {
  this.pci_phonecc = pci_phonecc;
 }

 public String getPci_designation()
 {
  return pci_designation;
 }

 public void setPci_designation(String pci_designation)
 {
  this.pci_designation = pci_designation;
 }

 public String getMerchant_addressproof()
 {
  return merchant_addressproof;
 }

 public void setMerchant_addressproof(String merchant_addressproof)
 {
  this.merchant_addressproof = merchant_addressproof;
 }

 public String getMerchant_addressId()
 {
  return merchant_addressId;
 }

 public void setMerchant_addressId(String merchant_addressId)
 {
  this.merchant_addressId = merchant_addressId;
 }

 public String getCorporate_addressproof()
 {
  return corporate_addressproof;
 }

 public void setCorporate_addressproof(String corporate_addressproof)
 {
  this.corporate_addressproof = corporate_addressproof;
 }

 public String getCorporate_addressId()
 {
  return corporate_addressId;
 }

 public void setCorporate_addressId(String corporate_addressId)
 {
  this.corporate_addressId = corporate_addressId;
 }

 public String getRegistered_directors_addressproof()
 {
  return registered_directors_addressproof;
 }

 public void setRegistered_directors_addressproof(String registered_directors_addressproof)
 {
  this.registered_directors_addressproof = registered_directors_addressproof;
 }

 public String getRegistered_directors_addressId()
 {
  return registered_directors_addressId;
 }

 public void setRegistered_directors_addressId(String registered_directors_addressId)
 {
  this.registered_directors_addressId = registered_directors_addressId;
 }

 public String getPassportissuedate()
 {
  return passportissuedate;
 }

 public void setPassportissuedate(String passportissuedate)
 {
  this.passportissuedate = passportissuedate;
 }


 /* public Map<String, AddressIdentificationRequestVO> getCompanyProfile_addressVOMap()
    {
        return companyProfile_addressVOMap;
    }

    public void setCompanyProfile_addressVOMap(Map<String, AddressIdentificationRequestVO> companyProfile_addressVOMap)
    {
        this.companyProfile_addressVOMap = companyProfile_addressVOMap;
    }

    public Map<String, ContactDetailsRequestVO> getCompanyProfile_contactInfoVOMap()
    {
        return companyProfile_contactInfoVOMap;
    }

    public void setCompanyProfile_contactInfoVOMap(Map<String, ContactDetailsRequestVO> companyProfile_contactInfoVOMap)
    {
        this.companyProfile_contactInfoVOMap = companyProfile_contactInfoVOMap;
    }*/
}
