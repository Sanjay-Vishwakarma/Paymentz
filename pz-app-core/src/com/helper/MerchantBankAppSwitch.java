package com.helper;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.manager.ApplicationManager;
import com.enums.DefinedAcroFields;
import com.enums.IdentificationType;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.vo.applicationManagerVOs.UploadfileLabelVO;
import com.vo.applicationManagerVOs.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Kajal
 * Date: 5/7/15
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantBankAppSwitch
{
    private static Logger logger = new Logger(MerchantBankAppSwitch.class.getName());
    private static Functions functions = new Functions();

    public void fillAcroFieldsForBankApplication(String enumValue, AcroFields acroFields, ApplicationManagerVO applicationManagerVO, BankTypeVO bankTypeVO) throws IOException, DocumentException
    {
        CompanyProfileVO companyProfileVO = new CompanyProfileVO();
        OwnershipProfileVO ownershipProfileVO = new OwnershipProfileVO();
        BusinessProfileVO businessProfileVO = new BusinessProfileVO();
        BankProfileVO bankProfileVO = new BankProfileVO();
        CardholderProfileVO cardholderProfileVO = new CardholderProfileVO();
        ExtraDetailsProfileVO extraDetailsProfileVO=new ExtraDetailsProfileVO();
        ContractualPartnerVO contractualPartnerVO = null;
        ApplicationManager applicationManager = new ApplicationManager();
        Map<String,FileDetailsListVO> fileDetailsVOHashMap = new HashMap<String,FileDetailsListVO>();
        UploadfileLabelVO uploadfileLabelVO = null;

        companyProfileVO = applicationManagerVO.getCompanyProfileVO();
        ownershipProfileVO = applicationManagerVO.getOwnershipProfileVO();
        businessProfileVO = applicationManagerVO.getBusinessProfileVO();
        bankProfileVO = applicationManagerVO.getBankProfileVO();
        cardholderProfileVO = applicationManagerVO.getCardholderProfileVO();
        extraDetailsProfileVO = applicationManagerVO.getExtradetailsprofileVO();
        Double avgSaleVolume = 0.0;
        Double avgExpectedSaleVolume = 0.0;
        Double expectedTurnOverLastYear = 0.0;
        Double cbkRatio = 0.0;
        Double avgOfNumberOfCbk3MothsAgo = 0.0;
        Double avgOfNumberOfCbk = 0.0;
        Double avgCbkVolume = 0.0;
        Double avgOfNumberOfRefund3MothsAgo = 0.0;
        Double avgRefundVolume = 0.0;
        Double avgNumberOfTrans = 0.0;
        int totalNumTransMonth = 0;
        Double avgExpectedNumberOfTrans = 0.0;
        int expectedNumberOfTrans = 0;
        double tenPerOfAvgTicket = 0.0;
        double transactionAvgVolume = 0.0;
        double monthlyCreditVolume = 0.0;
        double monthlyDebitVolume = 0.0;
        double tenPerOfTransactionAvgVolume = 0.0;
        int numberOfTransThroughInternetByMC = 0;
        double numberOfTransThroughInternetByVISA = 0;
        double numberOfTransThroughMotoByMC = 0;
        double numberOfTransThroughMotoByVISA = 0;
        String processingFromDate = "";
        String processingToDate = "";

        if(uploadfileLabelVO == null)
            fileDetailsVOHashMap = applicationManager.getApplicationUploadedDetail(applicationManagerVO.getMemberId());

        if(fileDetailsVOHashMap != null && fileDetailsVOHashMap.size()>0)
        {
            uploadfileLabelVO = new UploadfileLabelVO();
            for (String alternateName : fileDetailsVOHashMap.keySet())
            {
                if ("memorandomArticle".contains(alternateName))
                    uploadfileLabelVO.setMemorandomArticle("ON");
                else if ("Incorporation Certificate".equals(alternateName))
                    uploadfileLabelVO.setIncorporation_Certificate("ON");
                else if ("Share Certificate".equals(alternateName))
                    uploadfileLabelVO.setShare_Certificate("ON");
                else if ("Processing History".equals(alternateName))
                    uploadfileLabelVO.setProcessing_History("ON");
                else if ("License".equals(alternateName))
                    uploadfileLabelVO.setLicense("ON");
                else if ("Bank Statement".equals(alternateName))
                    uploadfileLabelVO.setBank_Statement("ON");
                else if ("Bank Reference Letter".equals(alternateName))
                    uploadfileLabelVO.setBank_Reference_Letter("ON");
                else if ("Proof Of Identity".equals(alternateName))
                    uploadfileLabelVO.setProof_Of_Identityt("ON");
                else if ("Address Proof".equals(alternateName))
                    uploadfileLabelVO.setAddress_Proof("ON");
                else if ("Cross Corporate".equals(alternateName))
                    uploadfileLabelVO.setCross_Corporate("ON");
            }
        }

        if (DefinedAcroFields.isInEnum(enumValue))
        {
            DefinedAcroFields definedAcroFields = DefinedAcroFields.valueOf(enumValue);
            if ((acroFields.getFieldType(definedAcroFields.toString()) == AcroFields.FIELD_TYPE_TEXT))
            {
                if(contractualPartnerVO == null)
                    contractualPartnerVO = applicationManager.getContractualPartnerDetails(applicationManagerVO.getMemberId(), bankTypeVO.getBankName());

                switch (definedAcroFields)
                {
                    //Company profile
                    case CompanyName:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getCompany_name()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getCompany_name() : "");
                        break;
                    case CompanyAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getAddress()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getAddress() : "");
                        break;
                    case CompanyCity:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getCity()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getCity() : "");
                        break;
                    case CompanyState:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getState()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getState() : "");
                        break;
                    case CompanyCountry:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getCountry()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getCountry() : "");
                        break;
                    case CompanyStreet:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getStreet()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getStreet() : "");
                        break;
                    case CompanyZip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getZipcode()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getZipcode() : "");
                        break;
                    case Companyphonecc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getPhone_cc()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getPhone_cc() : "");
                        break;
                    case CompanyTelephoneNO:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getPhone_number()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getPhone_number() : "");
                        break;
                    case CompanyFax:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getFax()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getFax() : "");
                        break;
                    case CompanyEmailAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getEmail_id()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getEmail_id() : "");
                        break;
                    case CompanyCountryofRegistration:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCountryOfRegistration()) ? companyProfileVO.getCountryOfRegistration() : "");
                        break;
                    case CompanyRegistrationNumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getRegistration_number()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getRegistration_number() : "");
                        break;
                    case CompanyDateOfRegistration:
                        String companyDateOfRegistration = getCompanyDateOfRegistration(bankProfileVO,companyProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyDateOfRegistration) ? companyDateOfRegistration : "");
                        break;
                    case CompanyVat:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getVatidentification()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getVatidentification() : "");
                        break;
                    case skypeIMaddress:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getSkypeIMaddress()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getSkypeIMaddress(): "");
                        break;
                    case FederalTaxID:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getFederalTaxId()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getFederalTaxId(): "");
                        break;
                    case TypeofBusiness:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ? companyProfileVO.getCompanyTypeOfBusiness() : "");
                        break;

                    //Business profile
                    case BusinessName://corporate to business
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCompany_name()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCompany_name() : "");
                        break;
                    case BusinessAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getAddress()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getAddress() : "");
                        break;
                    case BusinessCity:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCity()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCity() : "");
                        break;
                    case BusinessState:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getState()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getState() : "");
                        break;
                    case BusinessCountry:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCountry()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCountry() : "");
                        break;
                    case BusinessStreet:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getStreet()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getStreet() : "");
                        break;
                    case BusinessZip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getZipcode()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getZipcode() : "");
                        break;
                    case RegisteredCorporateNameEU:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCompany_name()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS").getCompany_name() : "");
                        break;
                    case RegisteredDirectorsAddressEU:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getAddress()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getAddress() : "");
                        break;
                    case RegisteredDirectorsEU:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getRegistred_directors()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getRegistred_directors() : "");
                        break;
                    case RegisteredDirectorsCity:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getCity()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getCity() : "");
                        break;
                    case RegisteredDirectorsState:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getState()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getState() : "");
                        break;
                    case RegisteredDirectorsCountry:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getCountry()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getCountry() : "");
                        break;
                    case RegisteredDirectorsStreet:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getStreet()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getStreet() : "");
                        break;
                    case RegisteredDirectorsZip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getZipcode()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getZipcode() : "");
                        break;
                    case EURegistrationNumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getRegistration_number()) ? companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY").getRegistration_number() : "");
                        break;

                    //Other Information
                    case companylengthoftimebusiness:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyLengthOfTimeInBusiness()) ? companyProfileVO.getCompanyLengthOfTimeInBusiness() : "");
                        break;
                    case NumberofEmployees:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyNumberOfEmployees()) ? companyProfileVO.getCompanyNumberOfEmployees() : "");
                        break;
                    case companyturnoverlastyearunit:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompany_turnoverlastyear_unit()) ? companyProfileVO.getCompany_turnoverlastyear_unit() : "");
                        break;
                    case TurnoverLastYear:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyTurnoverLastYear()) ? companyProfileVO.getCompanyTurnoverLastYear() : "");
                        break;
                    case companycurrencylastyear:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompany_currencylastyear()) ? companyProfileVO.getCompany_currencylastyear() : "");
                        break;
                    case CapitalResources:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyCapitalResources()) ? companyProfileVO.getCompanyCapitalResources() : "");
                        break;
                    case cbkContactPerson:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getName()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getName() : "");
                        break;
                    case cbkEmail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getEmailaddress()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getEmailaddress() : "");
                        break;
                    case cbkTelNumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getTelephonenumber()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getTelephonenumber() : "");
                        break;
                    case cbkPhonecc:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getPhonecc1()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getPhonecc1() : "");
                        break;
                    case pciContactPerson:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getName()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getName() : "");
                        break;
                    case pciEmail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getEmailaddress()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getEmailaddress() : "");
                        break;
                    case pciTelNumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getTelephonenumber()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getTelephonenumber() : "");
                        break;
                    case pciPhonecc:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getPhonecc1()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getPhonecc1() : "");
                        break;

                    //Contact Information
                    case contactname:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getName()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getName() : "");
                        break;
                    case contactEmailAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getEmailaddress()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getEmailaddress() : "");
                        break;
                    case Contactnametelnocc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getPhonecc1()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getPhonecc1() : "");
                        break;
                    case ContactphoneNumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getTelephonenumber()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getTelephonenumber() : "");
                        break;
                    case TechnicalContactName:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getName()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getName() : "");
                        break;
                    case TechnicalEmailAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getEmailaddress()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getEmailaddress() : "");
                        break;
                    case technicalphonecc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getPhonecc1()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getPhonecc1() : "");
                        break;
                    case TechnicalTelephoneNumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getTelephonenumber()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getTelephonenumber() : "");
                        break;
                    case FinancialContactName:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getName()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getName() : "");
                        break;
                    case FinancialEmailAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getEmailaddress()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getEmailaddress() : "");
                        break;
                    case financialphonecc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getPhonecc1()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getPhonecc1() : "");
                        break;
                    case FinancialTelephoneNumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getTelephonenumber()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getTelephonenumber() : "");
                        break;
                    case companybankruptcydate:
                        String companybankdate = getCompanyBankruptcyDate(bankProfileVO,companyProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companybankdate) ? companybankdate : "");
                        break;
                    case insuredCompanyName:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getInsured_companyname()) ? companyProfileVO.getInsured_companyname() : "");
                        break;
                    case insured_currency:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getInsured_currency()) ? companyProfileVO.getInsured_currency() : "");
                        break;
                    case insured_amount:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getInsured_amount()) ? companyProfileVO.getInsured_amount() : "");
                        break;
                    case legalProceedingTxt:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getLegalProceeding()) ? "Legal Proceeding" : "");
                        break;
                    case main_business_partner:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getMain_business_partner()) ? companyProfileVO.getMain_business_partner() : "");
                        break;
                    case contactDesignation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getDesignation()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN").getDesignation() : "");
                        break;
                    case TechnicalDesignation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getDesignation()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL").getDesignation() : "");
                        break;
                    case FinanacialDesignation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getDesignation()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING").getDesignation() : "");
                        break;
                    case cbkDesignation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getDesignation()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK").getDesignation() : "");
                        break;
                    case pciDesignation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getDesignation()) ? companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI").getDesignation() : "");
                        break;

                    //Ownership profile

                    //Nameprincipal 1
                    //Individual Shareholder 1
                    case shareholderprofile1:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getFirstname() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1lastname:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getLastname():"");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1title:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getTitle() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1owned:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getOwned()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getOwned() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1State:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getState() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1address:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getAddress() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1city:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getCity() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1zip:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getZipcode() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1country:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getCountry() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1street:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getStreet() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1telnocc1:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getTelnocc1() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1telephonenumber:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getTelephonenumber() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1emailaddress:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getEmailaddress() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1dateofbirth:
                        String shareholderProfile1DateOfBirth = getShareholderProfile1DateOfBirth(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(shareholderProfile1DateOfBirth) ? shareholderProfile1DateOfBirth : "");
                        break;
                    case shareholderprofile1identificationtypeselect:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtypeselect() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1identificationtype:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtype() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1nationality:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getNationality() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile1Passportexpirydate:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getPassportexpirydate() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;

                    //Individual Shareholder 2
                    case shareholderprofile2:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getFirstname() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2lastname:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getLastname():"");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2title:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getTitle() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2owned:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getOwned()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getOwned() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2State:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getState() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2address:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getAddress() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2city:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getCity() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2zip:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getZipcode() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2country:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getCountry() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2street:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                             acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getStreet() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2telnocc2:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getTelnocc1() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2telephonenumber:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getTelephonenumber() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2emailaddress:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getEmailaddress() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2dateofbirth:
                        String shareholderProfile2DateOfBirth = getShareholderProfile2DateOfBirth(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(shareholderProfile2DateOfBirth) ? shareholderProfile2DateOfBirth : "");
                        break;
                    case shareholderprofile2identificationtypeselect:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtypeselect() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2identificationtype:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtype() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2nationality:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getNationality() : "");
                        else
                        acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile2Passportexpirydate:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!= null)
                            acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getPassportexpirydate() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;

                    //Individual Shareholder 3
                    case shareholderprofile3:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getFirstname() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3lastname:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getLastname():"");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3title:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getTitle() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3owned:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getOwned()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getOwned() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3State:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getState() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3address:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getAddress() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3city:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getCity() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3zip:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getZipcode() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3country:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getCountry() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3street:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getStreet() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3telnocc2:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getTelnocc1() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3telephonenumber:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getTelephonenumber() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3emailaddress:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getEmailaddress() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3dateofbirth:
                        String shareholderProfile3DateOfBirth = getShareholderProfile3DateOfBirth(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(shareholderProfile3DateOfBirth) ? shareholderProfile3DateOfBirth : "");
                        break;
                    case shareholderprofile3identificationtypeselect:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getIdentificationtypeselect() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3identificationtype:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getIdentificationtype() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3nationality:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getNationality() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;
                    case shareholderprofile3Passportexpirydate:
                        if(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!= null)
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getPassportexpirydate() : "");
                        else
                            acroFields.setField(definedAcroFields.name().toString(),"");
                        break;

                    // Corporate Shareholder 1
                    case corporateshareholder1Name:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getName()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getName() : "");
                        break;
                    case corporateshareholder1Address:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getAddress():"");
                        break;
                    case corporateshareholder1RegNumber:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getRegistrationNumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getRegistrationNumber():"");
                        break;
                    case corporateshareholder1City:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getCity(): "");
                        break;
                    case corporateshareholder1Country:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getCountry(): "");
                        break;
                    case corporateshareholder1Street:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getStreet(): "");
                        break;
                    case corporateshareholder1State:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getState(): "");
                        break;
                    case corporateshareholder1ZipCode:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1").getZipcode():"");
                        break;

                    // Corporate Shareholder 2
                    case corporateshareholder2Name:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getName()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getName() : "");
                        break;
                    case corporateshareholder2Address:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getAddress():"");
                        break;
                    case corporateshareholder2RegNumber:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getRegistrationNumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getRegistrationNumber() :"");
                        break;
                    case corporateshareholder2City:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getCity(): "");
                        break;
                    case corporateshareholder2Country:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getCountry(): "");
                        break;
                    case corporateshareholder2Street:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getStreet(): "");
                        break;
                    case corporateshareholder2State:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getState(): "");
                        break;
                    case corporateshareholder2ZipCode:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2").getZipcode():"");
                        break;

                    // Corporate Shareholder 3
                    case corporateshareholder3Name:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getName()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getName() : "");
                        break;
                    case corporateshareholder3Address:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getAddress():"");
                        break;
                    case corporateshareholder3RegNumber:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getRegistrationNumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getRegistrationNumber():"");
                        break;
                    case corporateshareholder3City:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getCity(): "");
                        break;
                    case corporateshareholder3Country:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getCountry(): "");
                        break;
                    case corporateshareholder3Street:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getStreet(): "");
                        break;
                    case corporateshareholder3State:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getState(): "");
                        break;
                    case corporateshareholder3ZipCode:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3").getZipcode():"");
                        break;

                    //Director profile

                    //Director 1
                    case directorsprofile:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getFirstname() : "");
                        break;
                    case directorsprofilelastname:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getLastname():"");
                        break;
                    case directorsprofiletitle:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getTitle() : "");
                        break;
                    case directorsprofileState:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getState() : "");
                        break;
                    case directorsprofileaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getAddress() : "");
                        break;
                    case directorsprofilecity:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getCity() : "");
                        break;
                    case directorsprofilezip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getZipcode() : "");
                        break;
                    case directorsprofilecountry:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getCountry() : "");
                        break;
                    case directorsprofilestreet:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getStreet() : "");
                        break;
                    case directorsprofiletelnocc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getTelnocc1() : "");
                        break;
                    case directorsprofiletelephonenumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getTelephonenumber() : "");
                        break;
                    case directorsprofileemailaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getEmailaddress() : "");
                        break;
                    case directorsprofiledateofbirth:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getDateofbirth()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getDateofbirth() : "");
                        break;
                    case directorsprofileidentificationtypeselect:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtypeselect() : "");
                        break;
                    case directorsprofileidentificationtype:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtype() : "");
                        break;
                    case directorsprofilenationality:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getNationality() : "");
                        break;
                    case directorsprofilePassportexpirydate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getPassportexpirydate() : "");
                        break;

                    //Director 2
                    case directorsprofile2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getFirstname() : "");
                        break;
                    case directorsprofile2lastname:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getLastname():"");
                        break;
                    case directorsprofile2title:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getTitle() : "");
                        break;
                    case directorsprofile2State:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getState() : "");
                        break;
                    case directorsprofile2address:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getAddress() : "");
                        break;
                    case directorsprofile2city:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getCity() : "");
                        break;
                    case directorsprofile2zip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getZipcode() : "");
                        break;
                    case directorsprofile2country:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getCountry() : "");
                        break;
                    case directorsprofile2street:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getStreet() : "");
                        break;
                    case directorsprofile2telnocc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getTelnocc1() : "");
                        break;
                    case directorsprofile2telephonenumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getTelephonenumber() : "");
                        break;
                    case directorsprofile2emailaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getEmailaddress() : "");
                        break;
                    case directorsprofile2dateofbirth:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getDateofbirth()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getDateofbirth() : "");
                        break;
                    case directorsprofile2identificationtypeselect:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtypeselect() : "");
                        break;
                    case directorsprofile2identificationtype:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtype() : "");
                        break;
                    case directorsprofile2nationality:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getNationality() : "");
                        break;
                    case directorsprofile2Passportexpirydate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getPassportexpirydate() : "");
                        break;

                    //Director 3
                    case directorsprofile3:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getFirstname() : "");
                        break;
                    case directorsprofile3lastname:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getLastname():"");
                        break;
                    case directorsprofile3title:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getTitle() : "");
                        break;
                    case directorsprofile3State:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getState() : "");
                        break;
                    case directorsprofile3address:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getAddress() : "");
                        break;
                    case directorsprofile3city:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getCity() : "");
                        break;
                    case directorsprofile3zip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getZipcode() : "");
                        break;
                    case directorsprofile3country:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getCountry() : "");
                        break;
                    case directorsprofile3street:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getStreet() : "");
                        break;
                    case directorsprofile3telnocc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getTelnocc1() : "");
                        break;
                    case directorsprofile3telephonenumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getTelephonenumber() : "");
                        break;
                    case directorsprofile3emailaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getEmailaddress() : "");
                        break;
                    case directorsprofile3dateofbirth:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getDateofbirth()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getDateofbirth() : "");
                        break;
                    case directorsprofile3identificationtypeselect:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getIdentificationtypeselect() : "");
                        break;
                    case directorsprofile3identificationtype:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getIdentificationtype() : "");
                        break;
                    case directorsprofile3nationality:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getNationality() : "");
                        break;
                    case directorsprofile3Passportexpirydate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getPassportexpirydate() : "");
                        break;


                    //Authorizedsignatory Profile

                    //Authorized signatory 1
                    case authorizedsignatoryprofile:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getFirstname() : "");
                        break;
                    case authorizedsignatoryprofilelastname:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getLastname():"");
                        break;
                    case authorizedsignatoryprofiletitle:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getTitle() : "");
                        break;
                    case authorizedsignatoryprofileState:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getState() : "");
                        break;
                    case authorizedsignatoryprofileaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getAddress() : "");
                        break;
                    case authorizedsignatoryprofilecity:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getCity() : "");
                        break;
                    case authorizedsignatoryprofilezip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getZipcode() : "");
                        break;
                    case authorizedsignatoryprofilecountry:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getCountry() : "");
                        break;
                    case authorizedsignatoryprofilestreet:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getStreet() : "");
                        break;
                    case authorizedsignatoryprofiletelnocc1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getTelnocc1() : "");
                        break;
                    case authorizedsignatoryprofiletelephonenumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getTelephonenumber() : "");
                        break;
                    case authorizedsignatoryprofileemailaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getEmailaddress() : "");
                        break;
                    case authorizedsignatoryprofiledateofbirth:
                        String authorizedSignatoryProfileDateOfBirth = getAuthorizedSignatoryProfileDateOfBirth(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(authorizedSignatoryProfileDateOfBirth) ? authorizedSignatoryProfileDateOfBirth : "");
                        break;
                    case authorizedsignatoryprofileidentificationtypeselect:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getIdentificationtypeselect() : "");
                        break;
                    case authorizedsignatoryprofileidentificationtype:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getIdentificationtype() : "");
                        break;
                    case authorizedsignatoryprofilenationality:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getNationality() : "");
                        break;
                    case authorizedsignatoryprofilePassportexpirydate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getPassportexpirydate() : "");
                        break;

                    //Authorized signatory 2
                    case authorizedsignatoryprofile2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getFirstname() : "");
                        break;
                    case authorizedsignatoryprofile2lastname:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getLastname():"");
                        break;
                    case authorizedsignatoryprofile2title:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getTitle() : "");
                        break;
                    case authorizedsignatoryprofile2State:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getState() : "");
                        break;
                    case authorizedsignatoryprofile2address:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getAddress() : "");
                        break;
                    case authorizedsignatoryprofile2city:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getCity() : "");
                        break;
                    case authorizedsignatoryprofile2zip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getZipcode() : "");
                        break;
                    case authorizedsignatoryprofile2country:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getCountry() : "");
                        break;
                    case authorizedsignatoryprofile2street:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getStreet() : "");
                        break;
                    case authorizedsignatoryprofile2telnocc2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getTelnocc1() : "");
                        break;
                    case authorizedsignatoryprofile2telephonenumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getTelephonenumber() : "");
                        break;
                    case authorizedsignatoryprofile2emailaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getEmailaddress() : "");
                        break;
                    case authorizedsignatoryprofile2dateofbirth:
                        String authorizedSignatoryProfile2DateOfBirth = getAuthorizedSignatoryProfile2DateOfBirth(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(authorizedSignatoryProfile2DateOfBirth) ? authorizedSignatoryProfile2DateOfBirth : "");
                        break;
                    case authorizedsignatoryprofile2identificationtypeselect:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getIdentificationtypeselect() : "");
                        break;
                    case authorizedsignatoryprofile2identificationtype:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getIdentificationtype() : "");
                        break;
                    case authorizedsignatoryprofile2nationality:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getNationality() : "");
                        break;
                    case authorizedsignatoryprofile2Passportexpirydate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getPassportexpirydate() : "");
                        break;

                    //Authorized signatory 3
                    case authorizedsignatoryprofile3:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getFirstname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getFirstname() : "");
                        break;
                    case authorizedsignatoryprofile3lastname:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getLastname()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getLastname():"");
                        break;
                    case authorizedsignatoryprofile3title:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getTitle()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getTitle() : "");
                        break;
                    case authorizedsignatoryprofile3State:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getState()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getState() : "");
                        break;
                    case authorizedsignatoryprofile3address:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getAddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getAddress() : "");
                        break;
                    case authorizedsignatoryprofile3city:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getCity()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getCity() : "");
                        break;
                    case authorizedsignatoryprofile3zip:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getZipcode()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getZipcode() : "");
                        break;
                    case authorizedsignatoryprofile3country:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getCountry()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getCountry() : "");
                        break;
                    case authorizedsignatoryprofile3street:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getStreet()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getStreet() : "");
                        break;
                    case authorizedsignatoryprofile3telnocc2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getTelnocc1()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getTelnocc1() : "");
                        break;
                    case authorizedsignatoryprofile3telephonenumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getTelephonenumber()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getTelephonenumber() : "");
                        break;
                    case authorizedsignatoryprofile3emailaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getEmailaddress()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getEmailaddress() : "");
                        break;
                    case authorizedsignatoryprofile3dateofbirth:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getDateofbirth()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getDateofbirth() : "");
                        break;
                    case authorizedsignatoryprofile3identificationtypeselect:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getIdentificationtypeselect() : "");
                        break;
                    case authorizedsignatoryprofile3identificationtype:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getIdentificationtype()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getIdentificationtype() : "");
                        break;
                    case authorizedsignatoryprofile3nationality:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getNationality()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getNationality() : "");
                        break;
                    case authorizedsignatoryprofile3Passportexpirydate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPassportexpirydate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPassportexpirydate() : "");
                        break;
                    case authorizedsignatoryprofile1Designation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getDesignation()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getDesignation() : "");
                        break;
                    case authorizedsignatoryprofile2Designation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getDesignation()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getDesignation() : "");
                        break;
                    case authorizedsignatoryprofile3Designation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getDesignation()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getDesignation() : "");
                        break;
                    case shareholderprofile1Passportissuedate:
                        String shareholderProfile1PassportIssueDate = getShareholderProfile1PassportIssueDate(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(shareholderProfile1PassportIssueDate.toString()) ? shareholderProfile1PassportIssueDate.toString() : "");
                        break;
                    case shareholderprofile2Passportissuedate:
                        String shareholderProfile2PassportIssueDate = getShareholderProfile2PassportIssueDate(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(shareholderProfile2PassportIssueDate.toString()) ? shareholderProfile2PassportIssueDate.toString() : "");
                        break;
                    case shareholderprofile3Passportissuedate:
                        String shareholderProfile3PassportIssueDate = getShareholderProfile3PassportIssueDate(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(shareholderProfile3PassportIssueDate.toString()) ? shareholderProfile3PassportIssueDate.toString() : "");
                        break;
                    case directorsprofilePassportissuedate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getPassportissuedate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getPassportissuedate() : "");
                        break;
                    case directorsprofile2Passportissuedate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getPassportissuedate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getPassportissuedate() : "");
                        break;
                    case directorsprofile3Passportissuedate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getPassportissuedate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getPassportissuedate() : "");
                        break;
                    case authorizedsignatoryprofilePassportissuedate:
                        String authorizedSignatoryProfilePassportIssueDate = getAuthorizedSignatoryProfilePassportIssueDate(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(authorizedSignatoryProfilePassportIssueDate.toString()) ? authorizedSignatoryProfilePassportIssueDate.toString() : "");
                        break;
                    case authorizedsignatoryprofile2Passportissuedate:
                        String authorizedSignatoryProfile2PassportIssueDate = getAuthorizedSignatoryProfile2PassportIssueDate(bankProfileVO,ownershipProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(authorizedSignatoryProfile2PassportIssueDate.toString()) ? authorizedSignatoryProfile2PassportIssueDate.toString() : "");
                        break;
                    case authorizedsignatoryprofile3Passportissuedate:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPassportissuedate()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPassportissuedate() : "");
                        break;

                    //Business Profile
                    case urls:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getUrls()) ? businessProfileVO.getUrls() : "");
                        break;
                    case descriptorcreditcardstmt:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getDescriptor()) ? businessProfileVO.getDescriptor() : "");
                        break;
                    case ipaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getIpaddress()) ? businessProfileVO.getIpaddress() : "");
                        break;
                    case loginId:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getLoginId()) ? businessProfileVO.getLoginId() : "");
                        break;
                    case passWord:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPassWord()) ? businessProfileVO.getPassWord(): "");
                        break;
                    case averageticket:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getAverageticket()) ? businessProfileVO.getAverageticket() : "");
                        break;
                    case highestticket:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getHighestticket()) ? businessProfileVO.getHighestticket() : "");
                        break;
                    case lowestticket:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getLowestticket()) ? businessProfileVO.getLowestticket() : "");
                        break;

                    case foreigntransactionsus:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getForeigntransactions_us()) ? businessProfileVO.getForeigntransactions_us() : "");
                        break;
                    case foreigntransactionsEurope:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()) ? businessProfileVO.getForeigntransactions_Europe() : "");
                        break;
                    case foreigntransactionsAsia:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()) ? businessProfileVO.getForeigntransactions_Asia() : "");
                        break;
                    case foreigntransactionscis:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getForeigntransactions_cis()) ? businessProfileVO.getForeigntransactions_cis() : "");
                        break;
                    case foreigntransactionscanada:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getForeigntransactions_canada()) ? businessProfileVO.getForeigntransactions_canada() : "");
                        break;
                    case foreigntransactionsuk:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getForeigntransactions_uk()) ? businessProfileVO.getForeigntransactions_uk() : "");
                        break;
                    case foreigntransactionsRestoftheWorld:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()) ? businessProfileVO.getForeigntransactions_RestoftheWorld() : "");
                        break;
                    case cardtypesacceptedotheryes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_other()) ? businessProfileVO.getCardvolume_other() : "");
                        break;
                    case descriptionofproducts:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getDescriptionofproducts()) ? businessProfileVO.getDescriptionofproducts() : "");
                        break;
                    case productSoldCurrencies:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) ? businessProfileVO.getProduct_sold_currencies() : "");
                        break;
                    case recurringservicesyes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getRecurringservicesyes()) ? businessProfileVO.getRecurringservicesyes() : "");
                        break;
                    case isacallcenterusedyes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getIsacallcenterusedyes()) ? businessProfileVO.getIsacallcenterusedyes() : "");
                        break;
                    case isafulfillmenthouseusedyes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused_yes()) ? businessProfileVO.getIsafulfillmenthouseused_yes() : "");
                        break;
                    case shoppingcartdetails:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getShopping_cart_details()) ? businessProfileVO.getShopping_cart_details() : "");
                        break;

                    case countriesblockeddetails:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCountries_blocked_details()) ? businessProfileVO.getCountries_blocked_details() : "");
                        break;
                    case inHouseLocation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getInHouseLocation()) ? businessProfileVO.getInHouseLocation() : "");
                        break;
                    case contactPerson:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getContactPerson()) ? businessProfileVO.getContactPerson() : "");
                        break;
                    case shippingContactEmail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getShippingContactemail()) ? businessProfileVO.getShippingContactemail() : "");
                        break;
                    case otherLocation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getOtherLocation()) ? businessProfileVO.getOtherLocation() : "");
                        if(functions.isValueNull(businessProfileVO.getOtherLocation()))
                        {
                            businessProfileVO.setWarehouseLocation("Warehouse Location");
                        }
                        break;
                    case warehouseLocation:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getWarehouseLocation()) ? businessProfileVO.getWarehouseLocation() : "");
                        break;
                    case mainSuppliers:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getMainSuppliers()) ? businessProfileVO.getMainSuppliers(): "");
                        break;
                    case shipmentAssured:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getShipmentAssured())?"Y":"N");
                        break;
                    case sourceContent:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getSourceContent()) ? businessProfileVO.getSourceContent(): "");
                        break;
                    case affiliateprogramsdetails:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getAffiliate_programs_details()) ? businessProfileVO.getAffiliate_programs_details() : "");
                        break;
                    case listfraudtools_yes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getListfraudtools_yes()) ? businessProfileVO.getListfraudtools_yes() : "");
                        break;
                    case listfraudtools_yes2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getListfraudtools_yes()) ? businessProfileVO.getListfraudtools_yes() : "");
                        break;
                    case applicationservices:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getApp_Services())?"Y":"N");
                        break;

                    case customersupport:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getCustomer_support())?"Y":"N");
                        break;
                    case customersupportemail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCustomersupport_email()) ? businessProfileVO.getCustomersupport_email() : "");
                        break;
                    case custsupportworkhours:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCustsupportwork_hours()) ? businessProfileVO.getCustsupportwork_hours() : "");
                        break;
                    case technicalcontact:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getTechnical_contact()) ? businessProfileVO.getTechnical_contact() : "");
                        break;
                    case timeframe:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getTimeframe()) ? businessProfileVO.getTimeframe() : "");
                        break;
                    case merchantCode:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getMerchantCode()) ? businessProfileVO.getMerchantCode() : "");
                        break;
                    case customersidentification_yes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCustomers_identification_yes()) ? businessProfileVO.getCustomers_identification_yes() : "");
                        break;
                    case coolingoffperiod:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCoolingoffperiod()) ? businessProfileVO.getCoolingoffperiod() : "");
                        break;
                    case webhostCompanyName:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getWebhost_company_name()) ? businessProfileVO.getWebhost_company_name() : "");
                        break;
                    case webhostPhone:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getWebhost_phone()) ? businessProfileVO.getWebhost_phone() : "");
                        break;
                    case webhostEmail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getWebhost_email()) ? businessProfileVO.getWebhost_email() : "");
                        break;
                    case webhostWebsite:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getWebhost_website()) ? businessProfileVO.getWebhost_website() : "");
                        break;
                    case webhostAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getWebhost_address()) ? businessProfileVO.getWebhost_address() : "");
                        break;
                    case paymentCompanyName:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getPayment_company_name()) ? businessProfileVO.getPayment_company_name() : "");
                        break;
                    case paymentPhone:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getPayment_phone()) ? businessProfileVO.getPayment_phone() : "");
                        break;
                    case paymentEmail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getPayment_email()) ? businessProfileVO.getPayment_email() : "");
                        break;
                    case paymentWebsite:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getPayment_website()) ? businessProfileVO.getPayment_website() : "");
                        break;
                    case paymentAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getPayment_address()) ? businessProfileVO.getPayment_address() : "");
                        break;
                    case callcenterPhone:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCallcenter_phone()) ? businessProfileVO.getCallcenter_phone() : "");
                        break;
                    case callcenterEmail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCallcenter_email()) ? businessProfileVO.getCallcenter_email() : "");
                        break;
                    case callcenterWebsite:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCallcenter_website()) ? businessProfileVO.getCallcenter_website() : "");
                        break;
                    case callcenterAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCallcenter_address()) ? businessProfileVO.getCallcenter_address() : "");
                        break;
                    case shoppingcartCompanyName:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getShoppingcart_company_name()) ? businessProfileVO.getShoppingcart_company_name() : "");
                        break;
                    case shoppingcartPhone:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getShoppingcart_phone()) ? businessProfileVO.getShoppingcart_phone() : "");
                        break;
                    case shoppingcartEmail:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getShoppingcart_email()) ? businessProfileVO.getShoppingcart_email() : "");
                        break;
                    case shoppingcartWebsite:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getShoppingcart_website()) ? businessProfileVO.getShoppingcart_website() : "");
                        break;
                    case shoppingcartAddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getShoppingcart_address()) ? businessProfileVO.getShoppingcart_address() : "");
                        break;

                    case cardvolumeVisa:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_visa()) ? businessProfileVO.getCardvolume_visa() : "");
                        break;
                    case cardvolumeMasterCard:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_mastercard()) ? businessProfileVO.getCardvolume_mastercard() : "");
                        break;
                    case expectedCardvolumeVisa:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_visa()) ? String.valueOf(Double.parseDouble(businessProfileVO.getCardvolume_visa()) + 10.00) : "");
                        break;
                    case expectedCardvolumeMasterCard:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_mastercard()) ? String.valueOf(Double.parseDouble(businessProfileVO.getCardvolume_mastercard()) + 10.00) : "");
                        break;
                    case cardvolumeAmericanExpress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_americanexpress()) ? businessProfileVO.getCardvolume_americanexpress() : "");
                        break;
                    case cardvolumeDinner:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_dinner()) ? businessProfileVO.getCardvolume_dinner() : "");
                        break;
                    case cardvolumeDiscover:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_discover()) ? businessProfileVO.getCardvolume_discover() : "");
                        break;
                    case cardvolumeRupay:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_rupay()) ? businessProfileVO.getCardvolume_rupay() : "");
                        break;
                    case cardvolumeJCB:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_jcb()) ? businessProfileVO.getCardvolume_jcb() : "");
                        break;
                    case cardvolumeOther:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_other()) ? businessProfileVO.getCardvolume_other() : "");
                        break;
                    case billingModel:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getBillingModel())? businessProfileVO.getBillingModel() : "");
                        break;
                    case billingTimeFrame:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getBillingTimeFrame()) ? businessProfileVO.getBillingTimeFrame() : "");
                        break;
                    case recurringAmount:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getRecurringAmount()) ? businessProfileVO.getRecurringAmount() : "");
                        break;
                    case billingModelText:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getBillingModel()) ? businessProfileVO.getBillingModel() : "");
                        break;
                    case paymentDelivery:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getPayment_delivery()) ? businessProfileVO.getPayment_delivery() : "");
                        break;
                    case orderconfirmationOtherYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getOrderconfirmation_other_yes()) ? businessProfileVO.getOrderconfirmation_other_yes() :"");
                        break;
                    case paymentDelivery_otheryes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPayment_delivery_otheryes()) ? businessProfileVO.getPayment_delivery_otheryes() :"");
                        break;
                    case riskManagement:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getRisk_management()) ? businessProfileVO.getRisk_management() :"");
                        break;
                    case shopsystemPlugin:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getShopsystem_plugin()) ? businessProfileVO.getShopsystem_plugin() :"");
                        break;
                    case directDebitSepa:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getDirect_debit_sepa()) ? businessProfileVO.getDirect_debit_sepa() :"");
                        break;
                    case creditorId:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getCreditor_id()) ? businessProfileVO.getCreditor_id() :"");
                        break;
                    case alternativePayments:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getAlternative_payments()) ? businessProfileVO.getAlternative_payments() :"");
                        break;
                    case paymentEngine:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPayment_engine()) ? businessProfileVO.getPayment_engine() :"");
                        break;
                    case domainsOwned_no:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getDomainsOwned_no()) ? businessProfileVO.getDomainsOwned_no() :"");
                        break;
                    case agency_employed_yes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getAgency_employed_yes()) ? businessProfileVO.getAgency_employed_yes() :"");
                        break;
                    case test_link:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getTest_link()) ? businessProfileVO.getTest_link() :"");
                        break;
                    case pricingpolicieswebsite_yes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPricing_policies_website_yes()) ? businessProfileVO.getPricing_policies_website_yes() :"");
                        break;
                    case seasonalFluctuatingyes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getSeasonal_fluctuating_yes()) ? businessProfileVO.getSeasonal_fluctuating_yes() :"");
                        break;
                    case goodsDelivery:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getGoods_delivery()) ? businessProfileVO.getGoods_delivery() :"");
                        break;

                    case oneTimePercentage:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getOne_time_percentage()) ? businessProfileVO.getOne_time_percentage() :"");
                        break;
                    case motoPercentage:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getMoto_percentage()) ? businessProfileVO.getMoto_percentage() :"");
                        break;
                    case internetPercentage:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getInternet_percentage()) ? businessProfileVO.getInternet_percentage() :"");
                        break;
                    case swipePercentage:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getSwipe_percentage()) ? businessProfileVO.getSwipe_percentage() :"");
                        break;
                    case recurringPercentage:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getRecurring_percentage()) ? businessProfileVO.getRecurring_percentage() :"");
                        break;
                    case threedsecurePercentage:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getThreedsecure_percentage()) ? businessProfileVO.getThreedsecure_percentage() :"");
                        break;
                    case terminalTypeOtherYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()) ? businessProfileVO.getTerminal_type_otheryes() :"");
                        break;
                    case terminalTypeOther:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getTerminal_type_other()) ? businessProfileVO.getTerminal_type_other() :"");
                        break;

                    case paymentTypeCredit:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPaymenttype_credit()) ? businessProfileVO.getPaymenttype_credit() :"");
                        break;
                    case paymentTypeDebit:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPaymenttype_debit()) ? businessProfileVO.getPaymenttype_debit() :"");
                        break;
                    case paymentTypeNetbanking:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPaymenttype_netbanking()) ? businessProfileVO.getPaymenttype_netbanking() :"");
                        break;
                    case paymentTypeWallet:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPaymenttype_wallet()) ? businessProfileVO.getPaymenttype_wallet() :"");
                        break;
                    case paymentTypeAlternate:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPaymenttype_alternate()) ? businessProfileVO.getPaymenttype_alternate() :"");
                        break;
                    case methodofacceptanceinternet:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getInternet_percentage()) ? businessProfileVO.getInternet_percentage() :"");
                        break;
                    case methodofacceptancemoto:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getMoto_percentage()) ? businessProfileVO.getMoto_percentage() :"");
                        break;
                    case methodofacceptanceswipe:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getSwipe_percentage()) ? businessProfileVO.getSwipe_percentage() :"");
                        break;

                    case foreigntransactionsCountryName:
                        StringBuffer countryName = new StringBuffer();
                        if(businessProfileVO != null && functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
                            countryName.append("US, ");
                        if(businessProfileVO != null && functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
                            countryName.append("Europe, ");
                        if(businessProfileVO != null && functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
                            countryName.append("Asia, ");
                        if(businessProfileVO != null && functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                            countryName.append("CIS, ");
                        if(businessProfileVO != null && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                            countryName.append("Canada, ");
                        if(businessProfileVO != null && functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                            countryName.append("UK, ");
                        if(businessProfileVO != null && functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                            countryName.append("RestoftheWorld ");

                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(countryName.toString()) ? countryName.toString() :"");
                        break;

                    //TWUK Cardtype Condition
                    case cardtypeaccepted_besidesVisaMasterCardDinners://TWUK
                        StringBuffer s3Buffer = new StringBuffer("");
                        boolean commas=false;

                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_americanexpress()))
                        {
                            s3Buffer.append(" American Express ");
                            commas=true;
                        }

                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getCardtypesaccepted_discover()))
                        {
                            if(commas)
                                s3Buffer.append(",");
                            s3Buffer.append(" Discover ");
                            commas=true;
                        }
                        if (businessProfileVO!=null  &&functions.isValueNull(businessProfileVO.getCardtypesaccepted_jcb()))
                        {
                            if(commas)
                                s3Buffer.append(",");
                            s3Buffer.append(" JCB ");
                            commas=true;
                        }
                        if (businessProfileVO!=null  &&functions.isValueNull(businessProfileVO.getCardtypesaccepted_rupay()))
                        {
                            if(commas)
                                s3Buffer.append(",");
                            s3Buffer.append(" RUPAY ");
                            commas=true;
                        }
                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_other_yes()))
                        {
                            if(commas)
                                s3Buffer.append(" Other   ");
                            commas=true;

                        }

                        acroFields.setField(definedAcroFields.name().toString(),s3Buffer.toString());

                        break;

                    //twuk productSold currency



                    case currencyProductSoldJPY_PEN_HKD_AUD_DKK_SEK_NOK_INR://TWUK
                        StringBuffer s4Buffer = new StringBuffer("");
                        boolean commas4=false;

                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("JPY"))
                        {
                            s4Buffer.append(" JPY ");
                            commas4=true;
                        }

                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("PEN"))
                        {
                            if(commas4)
                                s4Buffer.append(",");
                            s4Buffer.append(" PEN ");
                            commas4=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("HKD"))
                        {
                            if(commas4)
                                s4Buffer.append(",");
                            s4Buffer.append(" HKD ");
                            commas4=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("AUD"))
                        {
                            if(commas4)
                                s4Buffer.append(",");
                            s4Buffer.append(" AUD ");
                            commas4=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("DKK"))
                        {
                            if(commas4)
                                s4Buffer.append(",");
                            s4Buffer.append(" DKK ");
                            commas4=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("SEK"))
                        {
                            if(commas4)
                                s4Buffer.append(",");
                            s4Buffer.append(" SEK ");
                            commas4=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("NOK"))
                        {
                            if(commas4)
                                s4Buffer.append(",");
                            s4Buffer.append(" NOK ");
                            commas4=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("INR"))
                        {
                            if(commas4)
                                s4Buffer.append(",");
                            s4Buffer.append(" INR ");
                            commas4=true;
                        }

                        acroFields.setField(definedAcroFields.name().toString(),s4Buffer.toString());

                        break;

                    //BANK PDF Condition


                    case ForeignTransaction://

                        int totalPercent=0;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
                        {
                            totalPercent += Integer.valueOf(businessProfileVO.getForeigntransactions_us());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
                        {
                            totalPercent += Integer.valueOf(businessProfileVO.getForeigntransactions_Asia());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                        {
                            totalPercent += Integer.valueOf(businessProfileVO.getForeigntransactions_cis());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            totalPercent += Integer.valueOf(businessProfileVO.getForeigntransactions_canada());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
                        {
                            totalPercent += Integer.valueOf(businessProfileVO.getForeigntransactions_Europe());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                        {
                            totalPercent += Integer.valueOf(businessProfileVO.getForeigntransactions_uk());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                        {
                            totalPercent += Integer.valueOf(businessProfileVO.getForeigntransactions_RestoftheWorld());
                        }


                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercent));
                        break;

                    case ForeignTransaction2:

                        int totalPercent2=0;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
                        {
                            totalPercent2 += Integer.valueOf(businessProfileVO.getForeigntransactions_Asia());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                        {
                            totalPercent2 += Integer.valueOf(businessProfileVO.getForeigntransactions_cis());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            totalPercent2 += Integer.valueOf(businessProfileVO.getForeigntransactions_canada());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                        {
                            totalPercent2 += Integer.valueOf(businessProfileVO.getForeigntransactions_uk());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                        {
                            totalPercent2 += Integer.valueOf(businessProfileVO.getForeigntransactions_RestoftheWorld());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercent2));
                        break;


                    case ForeignTransaction_All:

                        int totalPercent3=0;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
                        {
                            totalPercent3 += Integer.valueOf(businessProfileVO.getForeigntransactions_Asia());
                        }
                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                        {
                            totalPercent3 += Integer.valueOf(businessProfileVO.getForeigntransactions_RestoftheWorld());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                        {
                            totalPercent3 += Integer.valueOf(businessProfileVO.getForeigntransactions_uk());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            totalPercent3 += Integer.valueOf(businessProfileVO.getForeigntransactions_canada());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                        {
                            totalPercent3 += Integer.valueOf(businessProfileVO.getForeigntransactions_cis());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
                        {
                            totalPercent3 += Integer.valueOf(businessProfileVO.getForeigntransactions_Europe());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
                        {
                            totalPercent3 += Integer.valueOf(businessProfileVO.getForeigntransactions_us());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercent3));
                        break;

                    //case use for only payvision

                    case ForeignTransactionCIS_CANADA_UK_ROW:

                        int totalPercentCIS_CANADA_UK_ROW=0;
                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                        {
                            totalPercentCIS_CANADA_UK_ROW += Integer.valueOf(businessProfileVO.getForeigntransactions_RestoftheWorld());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                        {
                            totalPercentCIS_CANADA_UK_ROW += Integer.valueOf(businessProfileVO.getForeigntransactions_uk());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            totalPercentCIS_CANADA_UK_ROW += Integer.valueOf(businessProfileVO.getForeigntransactions_canada());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                        {
                            totalPercentCIS_CANADA_UK_ROW += Integer.valueOf(businessProfileVO.getForeigntransactions_cis());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercentCIS_CANADA_UK_ROW));
                        break;

                    case TerminalTypeAll:
                        int totalPercentForAll=0;
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getOne_time_percentage()))
                        {
                            totalPercentForAll += Integer.valueOf(businessProfileVO.getOne_time_percentage());
                        }
                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getMoto_percentage()))
                        {
                            totalPercentForAll += Integer.valueOf(businessProfileVO.getMoto_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getInternet_percentage()))
                        {
                            totalPercentForAll += Integer.valueOf(businessProfileVO.getInternet_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getSwipe_percentage()))
                        {
                            totalPercentForAll += Integer.valueOf(businessProfileVO.getSwipe_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getRecurring_percentage()))
                        {
                            totalPercentForAll += Integer.valueOf(businessProfileVO.getRecurring_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getThreedsecure_percentage()))
                        {
                            totalPercentForAll += Integer.valueOf(businessProfileVO.getThreedsecure_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()))
                        {
                            totalPercentForAll += Integer.valueOf(businessProfileVO.getTerminal_type_otheryes());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercentForAll));
                        break;

                    case PaymentTypeAll:
                        int totalPaymentTypePerForAll=0;
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPaymenttype_credit()))
                        {
                            totalPaymentTypePerForAll += Integer.valueOf(businessProfileVO.getPaymenttype_credit());
                        }
                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPaymenttype_debit()))
                        {
                            totalPaymentTypePerForAll += Integer.valueOf(businessProfileVO.getPaymenttype_debit());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPaymenttype_netbanking()))
                        {
                            totalPaymentTypePerForAll += Integer.valueOf(businessProfileVO.getPaymenttype_netbanking());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPaymenttype_alternate()))
                        {
                            totalPaymentTypePerForAll += Integer.valueOf(businessProfileVO.getPaymenttype_alternate());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPaymenttype_wallet()))
                        {
                            totalPaymentTypePerForAll += Integer.valueOf(businessProfileVO.getPaymenttype_wallet());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPaymentTypePerForAll));
                        break;

                    case ForeignTransaction_besidesAsia_Europe_US_other:

                        int totalPercent4=0;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                        {
                            totalPercent4 += Integer.valueOf(businessProfileVO.getForeigntransactions_cis());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            totalPercent4 += Integer.valueOf(businessProfileVO.getForeigntransactions_canada());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercent4));
                        break;

                    case ForeignTransaction_US_Canada:

                        int totalPercent5=0;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
                        {
                            totalPercent5 += Integer.valueOf(businessProfileVO.getForeigntransactions_us());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            totalPercent5 += Integer.valueOf(businessProfileVO.getForeigntransactions_canada());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercent5));
                        break;

                    case foreigntransactionsUKRestoftheWord:

                        int totalPercent6=0;
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                        {
                            totalPercent6 += Integer.valueOf(businessProfileVO.getForeigntransactions_uk());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                        {
                            totalPercent6 += Integer.valueOf(businessProfileVO.getForeigntransactions_RestoftheWorld());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(totalPercent6));
                        break;

                    case cardtypesaccepted_all:
                        StringBuffer s5Buffer = new StringBuffer("");
                        commas = false;
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_mastercard()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            s5Buffer.append(" Master Card ");
                            commas=true;
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_jcb()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            s5Buffer.append(" JCB ");
                            commas=true;
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_visa()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            s5Buffer.append("VISA");
                            commas=true;
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_discover()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            s5Buffer.append("DISCOVER");
                            commas=true;
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_americanexpress()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            commas=true;
                            s5Buffer.append("AMERICAN EXPRESS");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_diners()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            commas=true;
                            s5Buffer.append("DINNER");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_rupay()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            commas=true;
                            s5Buffer.append("RUPAY");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_other()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            commas=true;
                            s5Buffer.append("OTHER");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_other_yes()))
                        {
                            if(commas)
                                s5Buffer.append(",");
                            commas=true;
                            s5Buffer.append("OTHER YES");
                        }

                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s5Buffer.toString()) ? s5Buffer.toString() : "");
                        break;

                    case TerminalTypeAccepted:
                        StringBuffer sBuffer = new StringBuffer("");
                        commas = false;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getSwipe_percentage()))
                        {
                            if(commas)
                                sBuffer.append(",");
                            commas=true;
                            sBuffer.append("Swipe");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getInternet_percentage()))
                        {
                            if(commas)
                                sBuffer.append(",");
                            commas=true;
                            sBuffer.append("Internet");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()))
                        {
                            if(commas)
                                sBuffer.append(",");
                            commas=true;
                            sBuffer.append("OTHER");
                        }

                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(sBuffer.toString()) ? sBuffer.toString() : "");
                        break;


                    case CardTypeAcceptedBesides_Visa_MC:
                        StringBuffer s13Buffer = new StringBuffer("");
                        commas = false;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_jcb()))
                        {
                            if(commas)
                                s13Buffer.append(",");
                            commas=true;
                            s13Buffer.append("JCB");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_discover()))
                        {
                            if(commas)
                                s13Buffer.append(",");
                            commas=true;
                            s13Buffer.append("DISCOVER");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_americanexpress()))
                        { if(commas)
                            s13Buffer.append(",");
                            commas=true;
                            s13Buffer.append("AMERICAN EXPRESS");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_diners()))
                        {
                            if(commas)
                                s13Buffer.append(",");
                            commas=true;
                            s13Buffer.append("DINNER");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_rupay()))
                        {
                            if(commas)
                                s13Buffer.append(",");
                            commas=true;
                            s13Buffer.append("RUPAY");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_other()))
                        {
                            if(commas)
                                s13Buffer.append(",");
                            commas=true;
                            s13Buffer.append("OTHER");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_other_yes()))
                        {
                            if(commas)
                                s13Buffer.append(",");
                            commas=true;
                            s13Buffer.append("OTHER YES");
                        }

                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s13Buffer.toString()) ? s13Buffer.toString() : "");
                        break;

                    case MarketStrategy_All:
                        StringBuffer s6Buffer = new StringBuffer("");
                        commas = false;
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getYellowPages()))
                        {
                            if(commas)
                                s6Buffer.append(",");
                            commas=true;
                            s6Buffer.append("YELLOW PAGES");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getRadioTv()))
                        {
                            if(commas)
                                s6Buffer.append(",");
                            commas=true;
                            s6Buffer.append("RADIO TV");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getInternet()))
                        {
                            if(commas)
                                s6Buffer.append(",");
                            commas=true;
                            s6Buffer.append("INTERNET");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getNetworking()))
                        {
                            if(commas)
                                s6Buffer.append(",");
                            commas=true;
                            s6Buffer.append("NETWORKING");
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getOutboundTelemarketing()))
                        {
                            if(commas)
                                s6Buffer.append(",");
                            commas=true;
                            s6Buffer.append("OUT BOUND TELEMARKETING");
                        }

                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s6Buffer.toString()) ? s6Buffer.toString() : "");
                        break;

                    case CardTypeAcceptedVisaText:
                        StringBuffer s16Buffer = new StringBuffer("");
                        commas = false;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_visa()))
                        {
                            if(commas)
                                s16Buffer.append(",");
                            commas=true;
                            s16Buffer.append("VISA");
                        }
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s16Buffer.toString()) ? s16Buffer.toString() : "");
                        break;

                    case CardTypeAcceptedMasterCardText:
                        StringBuffer s17Buffer = new StringBuffer("");
                        commas = false;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_mastercard()))
                        {
                            if(commas)
                                s17Buffer.append(",");
                            commas=true;
                            s17Buffer.append("MASTER CARD");
                        }
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s17Buffer.toString()) ? s17Buffer.toString() : "");
                        break;

                    case cardvolume_AmericanExpress_Dinner_Discover_Other:
                        int cal=0;
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_americanexpress()))
                        {
                            cal += Integer.valueOf(businessProfileVO.getCardvolume_americanexpress());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_dinner()))
                        {
                            cal += Integer.valueOf(businessProfileVO.getCardvolume_dinner());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_discover()))
                        {
                            cal += Integer.valueOf(businessProfileVO.getCardvolume_discover());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_rupay()))
                        {
                            cal += Integer.valueOf(businessProfileVO.getCardvolume_rupay());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_jcb()))
                        {
                            cal += Integer.valueOf(businessProfileVO.getCardvolume_jcb());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(cal));
                        break;

                    case totalChannelType:

                        int cal2=0;

                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getOne_time_percentage()))
                        {
                            cal2 += Integer.valueOf(businessProfileVO.getOne_time_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getSwipe_percentage()))
                        {
                            cal2 += Integer.valueOf(businessProfileVO.getSwipe_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getRecurring_percentage()))
                        {
                            cal2 += Integer.valueOf(businessProfileVO.getRecurring_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getThreedsecure_percentage()))
                        {
                            cal2 += Integer.valueOf(businessProfileVO.getThreedsecure_percentage());
                        }
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()))
                        {
                            cal2 += Integer.valueOf(businessProfileVO.getTerminal_type_otheryes());
                        }
                        acroFields.setField(definedAcroFields.name().toString(), String.valueOf(cal2));
                        break;

                    case totalMarketCoverage:
                        StringBuffer marketCoverage = new StringBuffer();
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
                        {
                            marketCoverage.append("US-"+businessProfileVO.getForeigntransactions_us()+",");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
                        {
                            marketCoverage.append(" EUROP-"+businessProfileVO.getForeigntransactions_Europe()+",");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
                        {
                            marketCoverage.append(" ASIA-"+businessProfileVO.getForeigntransactions_Asia()+",");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                        {
                            marketCoverage.append(" CIS-"+businessProfileVO.getForeigntransactions_cis()+",");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            marketCoverage.append(" CANADA-"+businessProfileVO.getForeigntransactions_canada()+",");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                        {
                            marketCoverage.append(" UK-"+businessProfileVO.getForeigntransactions_uk()+",");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                        {
                            marketCoverage.append(" REST-"+businessProfileVO.getForeigntransactions_RestoftheWorld());
                        }
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(marketCoverage.toString()) ? marketCoverage.toString() : "");
                        break;

                    case shippingBreaking:
                        StringBuffer shippingBreaking = new StringBuffer();
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
                        {
                            shippingBreaking.append("US,");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
                        {
                            shippingBreaking.append(" EUROP,");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
                        {
                            shippingBreaking.append(" ASIA,");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
                        {
                            shippingBreaking.append(" CIS,");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
                        {
                            shippingBreaking.append(" CANADA,");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
                        {
                            shippingBreaking.append(" UK,");
                        }
                        if(functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
                        {
                            shippingBreaking.append(" REST Of World");
                        }
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(shippingBreaking.toString()) ? shippingBreaking.toString() : "");
                        break;

                    case orderconfirmationSMSOtherYes:
                        StringBuffer concadinateOtherWithSMS = new StringBuffer();
                        if(functions.isValueNull(businessProfileVO.getOrderconfirmation_sms()))
                        {
                            concadinateOtherWithSMS.append("SMS");
                        }
                        concadinateOtherWithSMS.append(businessProfileVO.getOrderconfirmation_other_yes());
                        break;

                    //BANK PROFILE
                    case bankinfobic:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_bic()) ? bankProfileVO.getBankinfo_bic() : "");
                        break;
                    case bankinfoaccountnumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_accountnumber()) ? bankProfileVO.getBankinfo_accountnumber() : "");
                        break;
                    case bankinfoIBAN:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_IBAN()) ? bankProfileVO.getBankinfo_IBAN() : "");
                        break;
                    case bankinfobankname:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_bank_name()) ? bankProfileVO.getBankinfo_bank_name() : "");
                        break;
                    case bankinfobankaddress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_bankaddress()) ? bankProfileVO.getBankinfo_bankaddress() : "");
                        break;
                    case bankinfobankphonenumber:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_bankphonenumber()) ? bankProfileVO.getBankinfo_bankphonenumber() : "");
                        break;
                    case bankinfocontactperson:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_contactperson()) ? bankProfileVO.getBankinfo_contactperson() : "");
                        break;

                    case bankinfoabaroutingcode:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_aba_routingcode()) ? bankProfileVO.getBankinfo_aba_routingcode() : "");
                        break;
                    case bankinfoaccountholder:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_accountholder()) ? bankProfileVO.getBankinfo_accountholder() : "");
                        break;

                    case aquirer:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getAquirer()) ? bankProfileVO.getAquirer() : "");
                        break;
                    case reasonaquirer:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getReasonaquirer()) ? bankProfileVO.getReasonaquirer() : "");
                        break;
                    case bankinfocurrency:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getBankinfo_currency()) ? bankProfileVO.getBankinfo_currency() : "");
                        break;

                    case salesvolumelastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()) ? bankProfileVO.getSalesvolume_lastmonth() : "");
                        break;
                    case salesvolume2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()) ? bankProfileVO.getSalesvolume_2monthsago() : "");
                        break;
                    case salesvolume3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()) ? bankProfileVO.getSalesvolume_3monthsago() : "");
                        break;
                    case salesvolume4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()) ? bankProfileVO.getSalesvolume_4monthsago() : "");
                        break;
                    case salesvolume5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()) ? bankProfileVO.getSalesvolume_5monthsago() : "");
                        break;
                    case salesvolume6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()) ? bankProfileVO.getSalesvolume_6monthsago() : "");
                        break;
                    case salesvolume12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()) ? bankProfileVO.getSalesvolume_12monthsago() : "");
                        break;
                    case salesvolumeyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_year2()) ? bankProfileVO.getSalesvolume_year2() : "");
                        break;
                    case salesvolumeyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getSalesvolume_year3()) ? bankProfileVO.getSalesvolume_year3() : "");
                        break;
                    case processingToDate:
                        processingToDate = getProcessingToDate(bankProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(processingToDate) ? processingToDate : "");
                        break;
                    case processingFromDate:
                        processingFromDate = getProcessingFromDate(bankProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(processingFromDate) ? processingFromDate : "");
                        break;

                    case numberoftransactionslastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth()) ? bankProfileVO.getNumberoftransactions_lastmonth() : "");
                        break;
                    case numberoftransactions2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago()) ? bankProfileVO.getNumberoftransactions_2monthsago() : "");
                        break;
                    case numberoftransactions3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago()) ? bankProfileVO.getNumberoftransactions_3monthsago() : "");
                        break;
                    case numberoftransactions4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago()) ? bankProfileVO.getNumberoftransactions_4monthsago() : "");
                        break;
                    case numberoftransactions5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago()) ? bankProfileVO.getNumberoftransactions_5monthsago() : "");
                        break;
                    case numberoftransactions6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago()) ? bankProfileVO.getNumberoftransactions_6monthsago() : "");
                        break;
                    case numberoftransactions12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago()) ? bankProfileVO.getNumberoftransactions_12monthsago() : "");
                        break;
                    case numberoftransactionsyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_year2()) ? bankProfileVO.getNumberoftransactions_year2() : "");
                        break;
                    case numberoftransactionsyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_year3()) ? bankProfileVO.getNumberoftransactions_year3() : "");
                        break;

                    case chargebackratiolastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_lastmonth()) ? bankProfileVO.getChargebackratio_lastmonth() : "");
                        break;
                    case chargebackratio2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_2monthsago()) ? bankProfileVO.getChargebackratio_2monthsago() : "");
                        break;
                    case chargebackratio3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_3monthsago()) ? bankProfileVO.getChargebackratio_3monthsago() : "");
                        break;
                    case chargebackratio4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_4monthsago()) ? bankProfileVO.getChargebackratio_4monthsago() : "");
                        break;
                    case chargebackratio5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_5monthsago()) ? bankProfileVO.getChargebackratio_5monthsago() : "");
                        break;
                    case chargebackratio6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_6monthsago()) ? bankProfileVO.getChargebackratio_6monthsago() : "");
                        break;
                    case chargebackratio12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_12monthsago()) ? bankProfileVO.getChargebackratio_12monthsago() : "");
                        break;
                    case chargebackratioyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_year2()) ? bankProfileVO.getChargebackratio_year2() : "");
                        break;
                    case chargebackratioyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackratio_year3()) ? bankProfileVO.getChargebackratio_year3() : "");
                        break;

                    case chargebackvolumelastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_lastmonth()) ? bankProfileVO.getChargebackvolume_lastmonth() : "");
                        break;
                    case chargebackvolume2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_2monthsago()) ? bankProfileVO.getChargebackvolume_2monthsago() : "");
                        break;
                    case chargebackvolume3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_3monthsago()) ? bankProfileVO.getChargebackvolume_3monthsago() : "");
                        break;
                    case chargebackvolume4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_4monthsago()) ? bankProfileVO.getChargebackvolume_4monthsago() : "");
                        break;
                    case chargebackvolume5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_5monthsago()) ? bankProfileVO.getChargebackvolume_5monthsago() : "");
                        break;
                    case chargebackvolume6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_6monthsago()) ? bankProfileVO.getChargebackvolume_6monthsago() : "");
                        break;
                    case chargebackvolume12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_12monthsago()) ? bankProfileVO.getChargebackvolume_12monthsago() : "");
                        break;
                    case chargebackvolumeyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_year2()) ? bankProfileVO.getChargebackvolume_year2() : "");
                        break;
                    case chargebackvolumeyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getChargebackvolume_year3()) ? bankProfileVO.getChargebackvolume_year3() : "");
                        break;

                    case numberofchargebackslastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth()) ? bankProfileVO.getNumberofchargebacks_lastmonth() : "");
                        break;
                    case numberofchargebacks2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago()) ? bankProfileVO.getNumberofchargebacks_2monthsago() : "");
                        break;
                    case numberofchargebacks3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago()) ? bankProfileVO.getNumberofchargebacks_3monthsago() : "");
                        break;
                    case numberofchargebacks4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago()) ? bankProfileVO.getNumberofchargebacks_4monthsago() : "");
                        break;
                    case numberofchargebacks5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago()) ? bankProfileVO.getNumberofchargebacks_5monthsago() : "");
                        break;
                    case numberofchargebacks6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago()) ? bankProfileVO.getNumberofchargebacks_6monthsago() : "");
                        break;
                    case numberofchargebacks12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_12monthsago()) ? bankProfileVO.getNumberofchargebacks_12monthsago() : "");
                        break;
                    case numberofchargebacksyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_year2()) ? bankProfileVO.getNumberofchargebacks_year2() : "");
                        break;
                    case numberofchargebacksyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofchargebacks_year3()) ? bankProfileVO.getNumberofchargebacks_year3() : "");
                        break;

                    case refundsvolumelastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_lastmonth()) ? bankProfileVO.getRefundsvolume_lastmonth() : "");
                        break;
                    case refundsvolume2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_2monthsago()) ? bankProfileVO.getRefundsvolume_2monthsago() : "");
                        break;
                    case refundsvolume3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_3monthsago()) ? bankProfileVO.getRefundsvolume_3monthsago() : "");
                        break;
                    case refundsvolume4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_4monthsago()) ? bankProfileVO.getRefundsvolume_4monthsago() : "");
                        break;
                    case refundsvolume5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_5monthsago()) ? bankProfileVO.getRefundsvolume_5monthsago() : "");
                        break;
                    case refundsvolume6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_6monthsago()) ? bankProfileVO.getRefundsvolume_6monthsago() : "");
                        break;
                    case refundsvolume12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_12monthsago()) ? bankProfileVO.getRefundsvolume_12monthsago() : "");
                        break;
                    case refundsvolumeyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_year2()) ? bankProfileVO.getRefundsvolume_year2() : "");
                        break;
                    case refundsvolumeyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundsvolume_year3()) ? bankProfileVO.getRefundsvolume_year3() : "");
                        break;

                    case numberofrefundslastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_lastmonth()) ? bankProfileVO.getNumberofrefunds_lastmonth() : "");
                        break;
                    case numberofrefunds2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_2monthsago()) ? bankProfileVO.getNumberofrefunds_2monthsago() : "");
                        break;
                    case numberofrefunds3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_3monthsago()) ? bankProfileVO.getNumberofrefunds_3monthsago() : "");
                        break;
                    case numberofrefunds4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_4monthsago()) ? bankProfileVO.getNumberofrefunds_4monthsago() : "");
                        break;
                    case numberofrefunds5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_5monthsago()) ? bankProfileVO.getNumberofrefunds_5monthsago() : "");
                        break;
                    case numberofrefunds6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_6monthsago()) ? bankProfileVO.getNumberofrefunds_6monthsago() : "");
                        break;
                    case numberofrefunds12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_12monthsago()) ? bankProfileVO.getNumberofrefunds_12monthsago() : "");
                        break;
                    case numberofrefundsyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_year2()) ? bankProfileVO.getNumberofrefunds_year2() : "");
                        break;
                    case numberofrefundsyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getNumberofrefunds_year3()) ? bankProfileVO.getNumberofrefunds_year3() : "");
                        break;

                    case refundratiolastmonth:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_lastmonth()) ? bankProfileVO.getRefundratio_lastmonth() : "");
                        break;
                    case refundratio2monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_2monthsago()) ? bankProfileVO.getRefundratio_2monthsago() : "");
                        break;
                    case refundratio3monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_3monthsago()) ? bankProfileVO.getRefundratio_3monthsago() : "");
                        break;
                    case refundratio4monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_4monthsago()) ? bankProfileVO.getRefundratio_4monthsago() : "");
                        break;
                    case refundratio5monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_5monthsago()) ? bankProfileVO.getRefundratio_5monthsago() : "");
                        break;
                    case refundratio6monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_6monthsago()) ? bankProfileVO.getRefundratio_6monthsago() : "");
                        break;
                    case refundratio12monthsago:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_12monthsago()) ? bankProfileVO.getRefundratio_12monthsago() : "");
                        break;
                    case refundratioyear2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_year2()) ? bankProfileVO.getRefundratio_year2() : "");
                        break;
                    case refundratioyear3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getRefundratio_year3()) ? bankProfileVO.getRefundratio_year3() : "");
                        break;
                    case customertransdata:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getCustomer_trans_data()) ? bankProfileVO.getCustomer_trans_data() : "");
                        break;

                    //BANK PDF FIELDS For Reitumu
                    case currencyProductSoldbesides_INR_JPY_PEN_CAD_NOK:
                        StringBuffer sBuffer1 = new StringBuffer("");
                        commas = false;
                        if (bankProfileVO!=null && functions.isValueNull(bankProfileVO.getCurrency_products_INR()))
                        {
                            if(commas)
                                sBuffer1.append(",");
                            commas=true;
                            sBuffer1.append(" INR ");

                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getCurrency_products_JPY()))
                        {
                            if(commas)
                                sBuffer1.append(",");
                            commas=true;
                            sBuffer1.append(" JPY ");
                        }

                        if (bankProfileVO!=null  &&functions.isValueNull(bankProfileVO.getCurrency_products_PEN()))
                        {
                            if(commas)
                                sBuffer1.append(",");
                            commas=true;
                            sBuffer1.append("PEN");
                        }
                        if (bankProfileVO!=null && functions.isValueNull(bankProfileVO.getCurrency_products_HKD()))
                        {
                            if(commas)
                                sBuffer1.append(",");
                            commas=true;
                            sBuffer1.append("HKD");

                        }
                        if (bankProfileVO!=null &&functions.isValueNull(bankProfileVO.getCurrency_products_CAD()))
                        {
                            if(commas)
                                sBuffer1.append(",");
                            commas=true;
                            sBuffer1.append("CAD");
                        }
                        if (bankProfileVO!=null &&functions.isValueNull(bankProfileVO.getCurrency_products_NOK()))
                        {
                            if(commas)
                                sBuffer1.append(",");
                            commas=true;
                            sBuffer1.append("NOK");
                        }
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(sBuffer1.toString()) ? sBuffer1.toString() : "");
                        break;

                    //BANK PDF FIELDS For Wirecard
                    case MultipleTransactioncurrencies:
                        StringBuffer s9Buffer2 = new StringBuffer("");
                        boolean commas9 = false;
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("SEK"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" SEK ");
                            commas9=true;
                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("USD"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" USD ");
                            commas9=true;

                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("JPY"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" JPY ");
                            commas9=true;
                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("INR"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" INR ");
                            commas9=true;
                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("PEN"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" PEN ");
                            commas9=true;
                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("GBP"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" GBP ");
                            commas9=true;
                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("EUR"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" EUR ");
                            commas9=true;
                        }

                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("HKD"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" HKD ");
                            commas9=true;

                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("AUD"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" AUD ");
                            commas9=true;

                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("CAD"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" CAD ");
                            commas9=true;

                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("DKK"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" DKK ");
                            commas9=true;
                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("SEK"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" SEK ");
                            commas9=true;
                        }
                        if (bankProfileVO!=null  && functions.isValueNull(bankProfileVO.getBank_account_currencies()) && bankProfileVO.getBank_account_currencies().contains("NOK"))
                        {
                            if(commas9)
                                s9Buffer2.append(",");
                            s9Buffer2.append(" NOK ");
                            commas9=true;
                        }

                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s9Buffer2.toString()) ? s9Buffer2.toString() : "");
                        break;

                    case ProductSoldCurrency_All:
                        StringBuffer s8Buffer = new StringBuffer("");
                        boolean commas6 = false;
                        //
                        if (businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("USD"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" USD ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("INR"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" INR ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("AUD"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" AUD ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("CAD"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" CAD ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("DKK"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" DKK ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("EUR"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" EUR ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("GBP"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" GBP ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("HKD"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" HKD ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("JPY"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" JPY ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("NOK"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" NOK ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("PEN"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" PEN ");
                            commas6=true;
                        }
                        if (businessProfileVO!=null  && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) && businessProfileVO.getProduct_sold_currencies().contains("SEK"))
                        {
                            if(commas6)
                                s8Buffer.append(",");
                            s8Buffer.append(" SEK ");
                            commas6=true;
                        }

                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s8Buffer.toString()) ? s8Buffer.toString() : "");
                        break;

                    //processing currency for agnipay

                    case currency_ProcessingHistory:
                        StringBuffer s15Buffer = new StringBuffer("");
                        boolean commas9c = false;
                        if (bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBankinfo_currency()) && bankProfileVO.getBankinfo_currency().contains("INR"))
                        {
                            if(commas9c)
                                s15Buffer.append(",");
                            commas9c=true;
                            s15Buffer.append(" INR ");

                        }
                        if (bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBankinfo_currency()) && bankProfileVO.getBankinfo_currency().contains("USD"))
                        {
                            if(commas9c)
                                s15Buffer.append(",");
                            commas9c=true;
                            s15Buffer.append(" USD ");

                        }
                        if (bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBankinfo_currency()) && bankProfileVO.getBankinfo_currency().contains("EUR"))
                        {
                            if(commas9c)
                                s15Buffer.append(",");
                            commas9c=true;
                            s15Buffer.append(" EUR ");

                        }
                        if (bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBankinfo_currency()) && bankProfileVO.getBankinfo_currency().contains("GBP"))
                        {
                            if(commas9c)
                                s15Buffer.append(",");
                            commas9c=true;
                            s15Buffer.append(" GBP ");

                        }
                        if (bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBankinfo_currency()) && bankProfileVO.getBankinfo_currency().contains("JPY"))
                        {
                            if(commas9c)
                                s15Buffer.append(",");
                            commas9c=true;
                            s15Buffer.append(" JPY ");

                        }
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(s15Buffer.toString()) ? s15Buffer.toString() : "");
                        break;

                    case AverageSalesVolume:

                        int salesvolumeaverage=0,salesvolume=0;

                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
                        {
                            salesvolume += Integer.valueOf(bankProfileVO.getSalesvolume_lastmonth());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
                        {
                            salesvolume += Integer.valueOf(bankProfileVO.getSalesvolume_2monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
                        {
                            salesvolume += Integer.valueOf(bankProfileVO.getSalesvolume_3monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
                        {
                            salesvolume += Integer.valueOf(bankProfileVO.getSalesvolume_4monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
                        {
                            salesvolume += Integer.valueOf(bankProfileVO.getSalesvolume_5monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
                        {
                            salesvolume += Integer.valueOf(bankProfileVO.getSalesvolume_6monthsago());
                        }

                        salesvolumeaverage=salesvolume/6;

                        acroFields.setField(definedAcroFields.name().toString(),String.valueOf(salesvolumeaverage));

                        break;

                    case AverageNumberofTransaction:

                        int numberoftransactionaverage=0,numberoftransaction=0;

                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth()))
                        {
                            numberoftransaction += Integer.valueOf(bankProfileVO.getNumberoftransactions_lastmonth());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago()))
                        {
                            numberoftransaction += Integer.valueOf(bankProfileVO.getNumberoftransactions_2monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago()))
                        {
                            numberoftransaction += Integer.valueOf(bankProfileVO.getNumberoftransactions_3monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago()))
                        {
                            numberoftransaction += Integer.valueOf(bankProfileVO.getNumberoftransactions_4monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago()))
                        {
                            numberoftransaction += Integer.valueOf(bankProfileVO.getNumberoftransactions_5monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago()))
                        {
                            numberoftransaction += Integer.valueOf(bankProfileVO.getNumberoftransactions_6monthsago());
                        }

                        numberoftransactionaverage=numberoftransaction/6;

                        acroFields.setField(definedAcroFields.name().toString(),String.valueOf(numberoftransactionaverage));

                        break;

                    case Averagenumberofchargebacks:

                        int numberofchargebacksaverage=0,numberofchargebacks=0;

                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth()))
                        {
                            numberofchargebacks += Integer.valueOf(bankProfileVO.getNumberofchargebacks_lastmonth());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago()))
                        {
                            numberofchargebacks += Integer.valueOf(bankProfileVO.getNumberofchargebacks_2monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago()))
                        {
                            numberofchargebacks += Integer.valueOf(bankProfileVO.getNumberofchargebacks_3monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago()))
                        {
                            numberofchargebacks += Integer.valueOf(bankProfileVO.getNumberofchargebacks_4monthsago());
                        }
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago()))
                        {
                            numberofchargebacks += Integer.valueOf(bankProfileVO.getNumberofchargebacks_5monthsago());
                        }
                        if(bankProfileVO!=null &&functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago()))
                        {
                            numberofchargebacks += Integer.valueOf(bankProfileVO.getNumberofchargebacks_6monthsago());
                        }

                        numberofchargebacksaverage=numberofchargebacks/6;

                        acroFields.setField(definedAcroFields.name().toString(),String.valueOf(numberofchargebacksaverage));

                        break;



                    // Cardholder profile
                    /*case complianceswapp:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(cardholderProfileVO.getCompliance_swapp())?"Y":"N");
                        break;*/
                    case compliancethirdpartyappform:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartyappform()) ? cardholderProfileVO.getCompliance_thirdpartyappform() : "");
                        break;
                    case compliancethirdpartysoft:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartysoft()) ? cardholderProfileVO.getCompliance_thirdpartysoft() : "");
                        break;
                    case complianceversion:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_version()) ? cardholderProfileVO.getCompliance_version() : "");
                        break;
                    case compliancecompaniesorgateways:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(cardholderProfileVO.getCompliance_companiesorgateways())?"Y":"N");
                        break;
                    case compliancecompaniesorgatewaysyes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways_yes()) ? cardholderProfileVO.getCompliance_companiesorgateways_yes() : "");
                        break;
                    case complianceelectronically:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(cardholderProfileVO.getCompliance_electronically())?"Y":"N");
                        break;
                    case compliancecarddatastored:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_carddatastored()) ? cardholderProfileVO.getCompliance_carddatastored() : "");
                        break;
                    case compliancequalifiedsecurityassessor:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_qualifiedsecurityassessor()) ? cardholderProfileVO.getCompliance_qualifiedsecurityassessor() : "");
                        break;
                    case compliancedateofcompliance:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_dateofcompliance()) ? cardholderProfileVO.getCompliance_dateofcompliance() : "");
                        break;
                    case compliancedateoflastscan:
                        String compliancedateoflastscan = getComplianceDateOfLastScan(bankProfileVO, cardholderProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(compliancedateoflastscan) ? compliancedateoflastscan : "");
                        break;
                    case compliancedatacompromise:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(cardholderProfileVO.getCompliance_datacompromise())?"Y":"N");
                        break;
                    case compliancedatacompromiseyes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise_yes()) ? cardholderProfileVO.getCompliance_datacompromise_yes() : "");
                        break;

                    //Site Inspection
                    case siteinspectionmerchant:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(cardholderProfileVO.getCompliance_datacompromise())?"RENTS":"OWNS");
                        break;
                    case siteinspectionlandlord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_landlord()) ? cardholderProfileVO.getSiteinspection_landlord() : "");
                        break;
                    case siteinspectionbuildingtype:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_buildingtype()) ? cardholderProfileVO.getSiteinspection_buildingtype() : "");
                        break;
                    case siteinspectionareazoned:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()) ? cardholderProfileVO.getSiteinspection_areazoned() : "");
                        break;
                    case siteinspectionsquarefootage:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_squarefootage()) ? cardholderProfileVO.getSiteinspection_squarefootage() : "");
                        break;
                    case siteinspectionoperatebusiness:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(cardholderProfileVO.getSiteinspection_operatebusiness())?"Y":"N");
                        break;
                    case siteinspectionprincipal1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1()) ? cardholderProfileVO.getSiteinspection_principal1() : "");
                        break;
                    case siteinspectionprincipal1date:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1_date()) ? cardholderProfileVO.getSiteinspection_principal1_date() : "");
                        break;
                    case siteinspectionprincipal2date:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2_date()) ? cardholderProfileVO.getSiteinspection_principal2_date() : "");
                        break;
                    case siteinspectionprincipal2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2()) ? cardholderProfileVO.getSiteinspection_principal2() : "");
                        break;
                    case compliancepcidsscompliantYes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant_yes()) ? cardholderProfileVO.getCompliance_pcidsscompliant_yes() : "");
                        break;

                    // Extra Details Profile
                    case ownerSince:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getOwnerSince()) ? extraDetailsProfileVO.getOwnerSince() :"");
                        break;
                    case socialSecurity:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getSocialSecurity()) ? extraDetailsProfileVO.getSocialSecurity() :"");
                        break;
                    case companyformParticipation:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getCompany_formParticipation()) ? extraDetailsProfileVO.getCompany_formParticipation() :"");
                        break;
                    case financialObligation:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getFinancialObligation()) ? extraDetailsProfileVO.getFinancialObligation() :"");
                        break;
                    case workingExperience:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getWorkingExperience()) ? extraDetailsProfileVO.getWorkingExperience() :"");
                        break;
                    case companyfinancialReportYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getCompany_financialReportYes()) ? extraDetailsProfileVO.getCompany_financialReportYes() :"");
                        break;
                    case financialReportinstitution:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getFinancialReport_institution()) ? extraDetailsProfileVO.getFinancialReport_institution() :"");
                        break;
                    case financialReportavailableYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getFinancialReport_availableYes()) ? extraDetailsProfileVO.getFinancialReport_availableYes() :"");
                        break;
                    case compliancepunitiveSanctionYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanctionYes()) ? extraDetailsProfileVO.getCompliance_punitiveSanctionYes() :"");
                        break;
                    case deedOfAgreementYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreementYes()) ? extraDetailsProfileVO.getDeedOfAgreementYes() :"");
                        break;
                    case fulfillmentproductEmailYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmailYes()) ? extraDetailsProfileVO.getFulfillment_productEmailYes() :"");
                        break;
                    case shipingdeliveryMethod:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getShiping_deliveryMethod()) ? extraDetailsProfileVO.getShiping_deliveryMethod() :"");
                        break;
                    case transactionMonitoringProcess:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getTransactionMonitoringProcess()) ? extraDetailsProfileVO.getTransactionMonitoringProcess() :"");
                        break;
                    case blacklistedAccountClosedYes:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosedYes()) ? extraDetailsProfileVO.getBlacklistedAccountClosedYes() :"");
                        break;
                    case livechat:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getLivechat())?"Chat":"");
                        break;

                    /*case recurringservices:
                        acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getRecurringservices());
                        break;*/

                    /*case cardtypesacceptedvisa:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_visa()) ? businessProfileVO.getCardtypesaccepted_visa() : "");
                        break;
                    case cardtypesacceptedmastercard:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_mastercard()) ? businessProfileVO.getCardtypesaccepted_mastercard() : "");
                        break;
                    case cardtypesacceptedamericanexpress:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_americanexpress()) ? businessProfileVO.getCardtypesaccepted_americanexpress() : "");
                        break;
                    case cardtypesaccepteddiscover:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_discover()) ? businessProfileVO.getCardtypesaccepted_discover() : "");
                        break;
                    case cardtypesaccepteddiners:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_diners()) ? businessProfileVO.getCardtypesaccepted_diners() : "");
                        break;
                    case cardtypesacceptedjcb:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_jcb()) ? businessProfileVO.getCardtypesaccepted_jcb() : "");
                        break;
                    case cardtypesacceptedother:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_other()) ? businessProfileVO.getCardtypesaccepted_other() : "");
                        break;
                    case cardtypesacceptedotheryes:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardtypesaccepted_other_yes()) ? businessProfileVO.getCardtypesaccepted_other_yes() : "");
                        break;*/
                    //bank profile

                    /*case currencyrequestedproductssold:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getCurrencyrequested_productssold()) ? bankProfileVO.getCurrencyrequested_productssold() : "");
                        break;
                    case currencyrequestedbankaccount:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(bankProfileVO.getCurrencyrequested_bankaccount()) ? bankProfileVO.getCurrencyrequested_bankaccount() : "");
                        break;*/

                    case calAvgCbkRatio:
                        cbkRatio = (double) Math.round(calAvgChkRatio(bankProfileVO));
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cbkRatio.toString()) ? cbkRatio.toString() : "");
                        break;

                    case Its_recommend_by_PSP:
                        acroFields.setField(definedAcroFields.name().toString(), "Recommended by PSP");
                        logger.debug("Inside Its_recommend_by_PSP----"+acroFields.getField("Recommended by PSP"));
                        break;

                    case Its_done_by_PSP:
                        acroFields.setField(definedAcroFields.name().toString(), "Its done by PSP");
                        break;

                    case avgSalesVolume:
                        avgSaleVolume = getAvgSalesVolume(bankProfileVO);
                        logger.debug("avgSaleVolume-->"+avgSaleVolume);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(avgSaleVolume.toString()) ? avgSaleVolume.toString() :"");
                        break;

                    case avgExpectedSalesVolume:
                        avgSaleVolume = getAvgSalesVolume(bankProfileVO);
                        avgExpectedSaleVolume = ((10*avgSaleVolume)/100) + avgSaleVolume;
                        logger.debug("avgExpectedSaleVolume--->"+avgExpectedSaleVolume);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(avgExpectedSaleVolume.toString()) ? avgExpectedSaleVolume.toString() :"");
                        break;

                    case expectedCompanyTurnOverLastYear:
                        if(!functions.isValueNull(companyProfileVO.getCompanyTurnoverLastYear()))
                            expectedTurnOverLastYear = 0.0;
                        else
                            expectedTurnOverLastYear = ((10 * Double.parseDouble(companyProfileVO.getCompanyTurnoverLastYear())) / 100) + Double.parseDouble(companyProfileVO.getCompanyTurnoverLastYear());

                        logger.debug("expectedTurnOverLastYear--->"+expectedTurnOverLastYear);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(expectedTurnOverLastYear)) ? String.valueOf(expectedTurnOverLastYear) :"");
                        break;

                    case avgNumberOfTransaction:
                        avgNumberOfTrans = getAvgNumberOfTransaction(bankProfileVO);
                        logger.debug("avgSaleVolume-->"+avgNumberOfTrans);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(avgNumberOfTrans.toString()) ? avgNumberOfTrans.toString() :"");
                        break;

                    case avgExpectedNumberOfTrans:
                        avgNumberOfTrans = getAvgNumberOfTransaction(bankProfileVO);
                        avgExpectedNumberOfTrans = ((10*avgNumberOfTrans)/100) + avgNumberOfTrans;
                        logger.debug("avgExpectedSaleVolume--->"+avgExpectedNumberOfTrans);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(avgExpectedNumberOfTrans.toString()) ? avgExpectedNumberOfTrans.toString() :"");
                        break;

                    case tenPerOfAvgTicket:
                        if(!functions.isValueNull(businessProfileVO.getAverageticket()))
                            tenPerOfAvgTicket = 0.0;
                        else
                            tenPerOfAvgTicket = ((10* Integer.parseInt(businessProfileVO.getAverageticket())) /100) + Integer.parseInt(businessProfileVO.getAverageticket());
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(tenPerOfAvgTicket)) ? String.valueOf(tenPerOfAvgTicket) :"");
                        break;

                    case transactionVolume:
                        if(functions.isValueNull(businessProfileVO.getHighestticket()) || functions.isValueNull(businessProfileVO.getLowestticket()))
                            transactionAvgVolume = Double.parseDouble(businessProfileVO.getHighestticket()) - Double.parseDouble(businessProfileVO.getLowestticket());
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(transactionAvgVolume)) ? String.valueOf(transactionAvgVolume) : "");
                        break;

                    case tenPerOfTransactionVolume:
                        if(functions.isValueNull(businessProfileVO.getHighestticket()) || functions.isValueNull(businessProfileVO.getLowestticket()))
                        {
                            transactionAvgVolume = Double.parseDouble(businessProfileVO.getHighestticket()) - Double.parseDouble(businessProfileVO.getLowestticket());
                            tenPerOfTransactionAvgVolume = (10 * transactionAvgVolume) / 100 + transactionAvgVolume;
                        }
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(tenPerOfTransactionAvgVolume)) ? String.valueOf(tenPerOfTransactionAvgVolume) :"");
                        break;

                    case avgOfCbk3MothsAgo:
                        avgOfNumberOfCbk3MothsAgo =  getAvgOfNumberOfCbk3MothsAgo(bankProfileVO);
                        logger.debug("avgOfNumberOfCbk3MothsAgo--->"+avgOfNumberOfCbk3MothsAgo);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(avgOfNumberOfCbk3MothsAgo)) ? String.valueOf(avgOfNumberOfCbk3MothsAgo) :"");
                        break;

                    case avgOfNumberOfCbk:
                        avgOfNumberOfCbk =  getAvgOfNumberOfCbk(bankProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(avgOfNumberOfCbk)) ? String.valueOf(avgOfNumberOfCbk) :"");
                        break;

                    case avgCbkVolume:
                        avgCbkVolume = getAvgCbkVolume(bankProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(avgCbkVolume)) ? String.valueOf(avgCbkVolume) :"");
                        break;

                    case avgOfNumberOfRefund3MothsAgo:
                        avgOfNumberOfRefund3MothsAgo = getAvgOfNumberOfRefund3MothsAgo(bankProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(avgOfNumberOfRefund3MothsAgo)) ? String.valueOf(avgOfNumberOfRefund3MothsAgo) :"");
                        break;

                    case avgRefundVolume:
                        avgRefundVolume = getAvgRefundVolume(bankProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(avgRefundVolume)) ? String.valueOf(avgRefundVolume) :"");
                        break;

                    case numberOfTransThroughInternetByMC:
                        numberOfTransThroughInternetByMC = getNumberOfTransThroughInternetByMC(bankProfileVO, businessProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(numberOfTransThroughInternetByMC)) ? String.valueOf(numberOfTransThroughInternetByMC) :"");
                        break;

                    case numberOfTransThroughInternetByVISA:
                        numberOfTransThroughInternetByVISA = getNumberOfTransThroughInternetByVISA(bankProfileVO, businessProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(numberOfTransThroughInternetByVISA)) ? String.valueOf(numberOfTransThroughInternetByVISA) :"");
                        break;

                    case numberOfTransThroughMotoByMC:
                        numberOfTransThroughMotoByMC = getNumberOfTransThroughMotoByMC(bankProfileVO, businessProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(numberOfTransThroughMotoByMC)) ? String.valueOf(numberOfTransThroughMotoByMC) :"");
                        break;

                    case numberOfTransThroughMotoByVISA:
                        numberOfTransThroughMotoByVISA = getNumberOfTransThroughMotoByVISA(bankProfileVO, businessProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(numberOfTransThroughMotoByVISA)) ? String.valueOf(numberOfTransThroughMotoByVISA) : "");
                        break;

                    case contractualPartnerId:
                        acroFields.setField(definedAcroFields.name().toString(), (contractualPartnerVO != null && functions.isValueNull(contractualPartnerVO.getContractual_partnerid())) ? contractualPartnerVO.getContractual_partnerid() :"");
                        break;
                    case contractualPartnerName:
                        acroFields.setField(definedAcroFields.name().toString(), (contractualPartnerVO != null && functions.isValueNull(contractualPartnerVO.getContractual_partnername())) ? contractualPartnerVO.getContractual_partnername() :"");
                        break;

                    case monthlyCreditVolume:
                        monthlyCreditVolume = getMonthlyCreditVolume(bankProfileVO, businessProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(monthlyCreditVolume)) ? String.valueOf(monthlyCreditVolume) :"");
                        break;

                    case monthlyDebitVolume:
                        monthlyDebitVolume = getMonthlyDebitVolume(bankProfileVO, businessProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(monthlyDebitVolume)) ? String.valueOf(monthlyDebitVolume) :"");
                        break;

                    case monthlyCardVolumeForTW:
                        double avgMonthlCardVolumeForTW = 0;
                        avgMonthlCardVolumeForTW = getMonthlyCardVolumeForTW(bankProfileVO, businessProfileVO);
                        //System.out.println("monthlyCardVolumeForTW total...."+avgMonthlCardVolumeForTW);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(avgMonthlCardVolumeForTW)) ? String.valueOf(avgMonthlCardVolumeForTW) :"");
                        break;

                    case foreigntransactionsRestoftheWorldForTW:
                        double totalOfRestOfWorld = 0;
                        totalOfRestOfWorld = getTotalOfRestOfWorld(businessProfileVO);
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(String.valueOf(totalOfRestOfWorld)) ? String.valueOf(totalOfRestOfWorld) :"");
                        break;

                    case shareholderprofile1IdentificationTypeForPassport:
                        if(functions.isValueNull(String.valueOf(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtype().equals(IdentificationType.Passport))) && !functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtypeselect()))
                            acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getIdentificationtypeselect() :"");
                        break;

                    case shareholderprofile2IdentificationTypeForPassport:
                        if(functions.isValueNull(String.valueOf(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtype().equals(IdentificationType.Passport))) && !functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtypeselect()))
                            acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getIdentificationtypeselect() :"");
                        break;

                    case directorsprofile1IdentificationTypeForPassport:
                        if(functions.isValueNull(String.valueOf(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtypeselect().equals(IdentificationType.Passport))) && !functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtypeselect()))
                            acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getIdentificationtypeselect() :"");
                        break;

                    case directorsprofile2IdentificationTypeForPassport:
                        if(functions.isValueNull(String.valueOf(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtype().equals(IdentificationType.Passport))) && !functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtypeselect()))
                            acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtypeselect()) ? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getIdentificationtypeselect() :"");
                        break;
                }
            }

            //Start Checkbox Condition
            else if (acroFields.getFieldType(definedAcroFields.toString()) == AcroFields.FIELD_TYPE_CHECKBOX)
            {
                switch (definedAcroFields)
                {
                    case loans:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(companyProfileVO.getLoans())?"ON":"");
                        break;
                    case income_economic_activity:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(companyProfileVO.getIncome_economic_activity())?"ON":"");
                        break;
                    case interest_income:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(companyProfileVO.getInterest_income())?"ON":"");
                        break;
                    case investments:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(companyProfileVO.getInvestments())?"ON":"");
                        break;
                    case income_sources_other:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(companyProfileVO.getIncome_sources_other())?"ON":"");
                        break;
                    case income_sources_other_yes:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(companyProfileVO.getIncome_sources_other_yes())?"ON":"");
                        break;

                    /*Start for cardtype*/
                    case cardtypesacceptedvisa:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_visa()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;

                    case cardtypesacceptedmastercard:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_mastercard()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;

                    case cardtypesacceptedamericanexpress:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_americanexpress()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardtypesaccepteddiscover:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_discover()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardtypesaccepteddiners:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_diners()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardtypesacceptedjcb:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_jcb()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardtypesacceptedrupay:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_rupay()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardtypesacceptedother:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardtypesaccepted_other()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;

                    /*END for cardtype*/
                    case oneTimePercentage:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getOne_time_percentage()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;

                    case motoPercentage:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getMoto_percentage()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case internetPercentage:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getInternet_percentage()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case swipePercentage:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getSwipe_percentage()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case threedsecurePercentage:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getThreedsecure_percentage()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case recurringPercentage:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getRecurring_percentage()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case terminalTypeOtherYes:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case directMail:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getDirectMail())?"ON":"");
                        break;
                    case yellowPages:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getYellowPages())?"ON":"");
                        break;
                    case radioTv:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getRadioTv())?"ON":"");
                        break;
                    case internet:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getInternet())?"ON":"");
                        break;
                    case networking:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getNetworking())? "ON" : "" );
                        break;
                    case outboundTelemarketing:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getOutboundTelemarketing())? "ON" : "");
                        break;

                    case isafulfillmenthouseused:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getIsafulfillmenthouseused())? "ON" :"");
                        break;
                    case ifInHouseLocation:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getInHouseLocation())? "ON" :"");
                        break;

                    case orderconfirmationPost:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getOrderconfirmation_post())? "ON" :"");
                        break;
                    case orderconfirmationEmail:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getOrderconfirmation_email())? "ON" :"");
                        break;
                    case orderconfirmationSms:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getOrderconfirmation_sms())? "ON" :"");
                        break;
                    case orderconfirmationOther:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getOrderconfirmation_other())? "ON" :"");
                        break;
                    case physicalGoodsDelivered:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getPhysicalgoods_delivered())? "ON" :"");
                        break;
                    case viaInternetGoodsDelivered:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getViainternetgoods_delivered())? "ON" :"");
                        break;
                    case activeBusiness:
                        acroFields.setField(definedAcroFields.name().toString(), "ON");
                        break;
                   /* case cardvolumeVisaChk:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getCardvolume_visa())? "ON" :"");
                        break;
                    case cardvolumeMasterCardChk:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getCardvolume_mastercard())? "ON" :"");
                        break;*/
                    case cardvolumeVisa:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_visa()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardvolumeMasterCard:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_mastercard()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardvolumeDinner:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_dinner()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardvolumeAmericanExpress:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_americanexpress()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardvolumeDiscover:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_discover()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardvolumeRupay:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_rupay()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;
                    case cardvolumeJCB:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getCardvolume_jcb()))
                            acroFields.setField(definedAcroFields.name().toString(),"ON");
                        break;

                    case methodofacceptancemotordbtn:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getMoto_percentage())? "ON" :"");
                        break;
                    case methodofacceptanceinternetrdbtn:
                        acroFields.setField(definedAcroFields.name().toString(),"Y".equals(businessProfileVO.getInternet_percentage())? "ON" :"");
                        break;

                    //KYC set up
                    case memorandomArticle:
                        acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getMemorandomArticle())) ? uploadfileLabelVO.getMemorandomArticle() : "");
                        break;

                    case incorporationCertificate:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getIncorporation_Certificate())))
                            acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getIncorporation_Certificate())) ? uploadfileLabelVO.getIncorporation_Certificate() : "");
                        break;

                    case shareCertificate:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getShare_Certificate())))
                            acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getShare_Certificate())) ? uploadfileLabelVO.getShare_Certificate() : "");
                        break;

                    case processingHistory:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getProcessing_History())))
                            acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getProcessing_History())) ? uploadfileLabelVO.getProcessing_History() : "");
                        break;

                    case license:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getLicense())))
                            acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getLicense())) ? uploadfileLabelVO.getLicense() : "");
                        break;

                    case bankStatement:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getBank_Statement())))
                            logger.debug("uploadfileLabelVO.getBank_Statement()--->"+uploadfileLabelVO.getBank_Statement());
                        acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getBank_Statement())) ? uploadfileLabelVO.getBank_Statement() : "");
                        break;

                    case bankReferenceLetter:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getBank_Reference_Letter())))
                            logger.debug("uploadfileLabelVO.getBank_Reference_Letter()--->"+uploadfileLabelVO.getBank_Reference_Letter());
                        acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getBank_Reference_Letter())) ? uploadfileLabelVO.getBank_Reference_Letter() : "");
                        break;

                    case proofOfIdentity:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getProof_Of_Identityt())))
                            logger.debug("uploadfileLabelVO.getProof_Of_Identityt()--->"+uploadfileLabelVO.getProof_Of_Identityt());
                        acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getProof_Of_Identityt())) ? uploadfileLabelVO.getProof_Of_Identityt() : "");
                        break;

                    case addressProof:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getAddress_Proof())))
                            acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO != null && functions.isValueNull(uploadfileLabelVO.getAddress_Proof())) ? uploadfileLabelVO.getAddress_Proof() : "");
                        break;

                    case crossCorporate:
                        if((uploadfileLabelVO!=null && functions.isValueNull(uploadfileLabelVO.getCross_Corporate())))
                            acroFields.setField(definedAcroFields.name().toString(), (uploadfileLabelVO != null && functions.isValueNull(uploadfileLabelVO.getCross_Corporate())) ? uploadfileLabelVO.getCross_Corporate() : "");
                        break;

                    //Bank PDF AcroFields (Transferred) For LPB
                    case currencyTransferredINR:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(), bankProfileVO.getBank_account_currencies().contains("INR") ? "ON" : "");
                        break;
                    case currencyTransferredUSD:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()));
                    {
                        acroFields.setField(definedAcroFields.name().toString(), bankProfileVO.getBank_account_currencies().contains("USD") ? "ON" : "");
                    }
                    break;
                    case currencyTransferredEUR:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                        {
                            acroFields.setField(definedAcroFields.name().toString(), bankProfileVO.getBank_account_currencies().contains("EUR") ? "ON" : "");
                        }
                        break;
                    case currencyTransferredGBP:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                        {
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("GBP") ? "ON" : "");
                        }
                        break;
                    case currencyTransferredJPY:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("JPY")? "ON" :"");
                        break;
                    case currencyTransferredPEN:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("PEN")? "ON" :"");
                        break;
                    case currencyTransferredHKD:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("HKD")? "ON" :"");
                        break;
                    case currencyTransferredCAD:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("CAD")? "ON" :"");
                        break;
                    case currencyTransferredAUD:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("AUD")? "ON" :"");
                        break;
                    case currencyTransferredDKK:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("DKK")? "ON" :"");
                        break;
                    case currencyTransferredSEK:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("SEK")? "ON" :"");
                        break;
                    case currencyTransferredNOK:
                        if(bankProfileVO!=null && functions.isValueNull(bankProfileVO.getBank_account_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),bankProfileVO.getBank_account_currencies().contains("NOK")? "ON" :"");
                        break;

                    //Bank PDF AcroFields (ProductSold) For LPB/ Reitumu
                    case currencyProductSoldINR:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("INR")? "ON" :"");
                        break;
                    case currencyProductSoldUSD:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("USD")? "ON" :"");
                        break;
                    case currencyProductSoldEUR:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("EUR")? "ON" :"");
                        break;
                    case currencyProductSoldGBP:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("GBP")? "ON" :"");
                        break;
                    case currencyProductSoldJPY:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("JPY")? "ON" :"");
                        break;
                    case currencyProductSoldPEN:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("PEN")? "ON" :"");
                        break;
                    case currencyProductSoldHKD:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("HKD")? "ON" :"");
                        break;
                    case currencyProductSoldCAD:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("CAD")? "ON" :"");
                        break;
                    case currencyProductSoldAUD:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("AUD")? "ON" :"");
                        break;
                    case currencyProductSoldDKK:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("DKK")? "ON" :"");
                        break;
                    case currencyProductSoldSEK:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("SEK")? "ON" :"");
                        break;
                    case currencyProductSoldNOK:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getProduct_sold_currencies().contains("NOK")? "ON" :"");
                        break;

                    case paymentDelivery_UponPurchase:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPayment_delivery()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getPayment_delivery().contains("upon_purchase")? "ON" :"");
                        break;
                    case paymentDelivery_OnDelivery:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPayment_delivery()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getPayment_delivery().contains("on_delivery")? "ON" :"");
                        break;
                    case paymentDelivery_Download:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPayment_delivery()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getPayment_delivery().contains("with_download")? "ON" :"");
                        break;
                    case paymentDelivery_other:
                        if(businessProfileVO!=null && functions.isValueNull(businessProfileVO.getPayment_delivery()))
                            acroFields.setField(definedAcroFields.name().toString(),businessProfileVO.getPayment_delivery().contains("payment_delivery_other")? "ON" :"");
                        break;
                }
            }

            //Start RadioButton Condition
            else if (acroFields.getFieldType(definedAcroFields.toString()) == AcroFields.FIELD_TYPE_RADIOBUTTON)
            {
                switch (definedAcroFields)
                {
                    case startupBusiness:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(companyProfileVO.getStartup_business())? companyProfileVO.getStartup_business() : "");
                        break;
                    case iscompanyInsured:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(companyProfileVO.getIscompany_insured())? companyProfileVO.getIscompany_insured() :"");
                        break;
                    case TypeofBusiness:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ? companyProfileVO.getCompanyTypeOfBusiness() : "");
                        break;
                    case Bankruptcy:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(companyProfileVO.getCompanyBankruptcy())? companyProfileVO.getCompanyBankruptcy() : "");
                        break;
                    case Licenserequired:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(companyProfileVO.getLicense_required())? companyProfileVO.getLicense_required() :"");
                        break;
                    case LicensePermission:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(companyProfileVO.getLicense_Permission())? companyProfileVO.getLicense_Permission() : "");
                        break;
                    case legalProceeding:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(companyProfileVO.getLegalProceeding())? companyProfileVO.getLegalProceeding() : "");
                        break;
                    case isDirectorsProfileNationalityUS:
                        acroFields.setField(definedAcroFields.name().toString(), (functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getNationality()) && "US".contains(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getNationality())) ? "Y" : "N");
                        break;

                    case isShareholderProfileNationalityUS:
                        acroFields.setField(definedAcroFields.name().toString(), (functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getNationality()) && "US".contains(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getNationality())) ? "Y" : "N");
                        break;

                    case terminalType:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getTerminal_type())? businessProfileVO.getTerminal_type() :"");
                        break;
                    case goodsDelivery:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getGoods_delivery()) ? businessProfileVO.getGoods_delivery() : "");
                        break;
                    case multipleMembership:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getMultipleMembership())? businessProfileVO.getMultipleMembership() :"");
                        break;
                    case shoppingcart:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getShopping_cart())? businessProfileVO.getShopping_cart() :"");
                        break;
                    case recurringservices:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getRecurringservices())? businessProfileVO.getRecurringservices() : "");
                        break;
                    case isacallcenterused:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getIsacallcenterused()) ? businessProfileVO.getIsacallcenterused() :"");
                        break;
                    case isafulfillmenthouseused:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused())? businessProfileVO.getIsafulfillmenthouseused() :"");
                        break;
                    case visacardlogos:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getVisa_cardlogos())?businessProfileVO.getVisa_cardlogos() : "");
                        break;
                    case mastercardlogos:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getMaster_cardlogos())?businessProfileVO.getMaster_cardlogos() : "");
                        break;
                    case pricedisplayed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPrice_displayed())? businessProfileVO.getPrice_displayed() :"");
                        break;
                    case companyIdentifiable:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getCompanyIdentifiable())? businessProfileVO.getCompanyIdentifiable() :"");
                        break;
                    case clearlyPresented:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getClearlyPresented())? businessProfileVO.getClearlyPresented() : "");
                        break;
                    case clearlyPresented1:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getClearlyPresented())? businessProfileVO.getClearlyPresented() : "");
                        break;
                    case clearlyPresented2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getClearlyPresented())? businessProfileVO.getClearlyPresented() : "");
                        break;
                    case domainsOwned:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getDomainsOwned())? businessProfileVO.getDomainsOwned() :"");
                        break;
                    case trackingNumber:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getTrackingNumber())? businessProfileVO.getTrackingNumber() :"");
                        break;
                    case cardholderasked:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getCardholder_asked()) ? businessProfileVO.getCardholder_asked() :"");
                        break;
                    case threeDsecurecompulsory:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getThreeD_secure_compulsory()) ? businessProfileVO.getThreeD_secure_compulsory() :"");
                        break;
                    case transactioncurrency:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getTransaction_currency())? businessProfileVO.getTransaction_currency() :"");
                        break;
                    case dynamicdescriptors:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getDynamic_descriptors())? businessProfileVO.getDynamic_descriptors() :"");
                        break;
                    case pricingpolicieswebsite:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPricing_policies_website())? businessProfileVO.getPricing_policies_website() : "");
                        break;
                    case pricingpolicieswebsite2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPricing_policies_website())? businessProfileVO.getPricing_policies_website() : "");
                        break;
                    case fulfillmenttimeframe:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getFulfillment_timeframe())? businessProfileVO.getFulfillment_timeframe() : "");
                        break;
                    case goodspolicy:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getGoods_policy())? businessProfileVO.getGoods_policy() :"");
                        break;
                    case goodspolicynew:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getGoods_policy())? businessProfileVO.getGoods_policy() :"");
                        break;
                    case countriesblocked:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCountries_blocked()) ? businessProfileVO.getCountries_blocked() : "");
                        break;
                    case copyright:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getCopyright()) ? businessProfileVO.getCopyright() :"");
                        break;
                    case kycprocesses:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getKyc_processes())? businessProfileVO.getKyc_processes() :"");
                        break;
                    case securitypolicy:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getSecuritypolicy())? businessProfileVO.getSecuritypolicy() :"");
                        break;
                    case confidentialitypolicy:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getConfidentialitypolicy())? businessProfileVO.getConfidentialitypolicy() :"");
                        break;
                    case applicablejurisdictions:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getApplicablejurisdictions())? businessProfileVO.getApplicablejurisdictions() : "");
                        break;
                    case privacyanonymitydataprotection:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPrivacy_anonymity_dataprotection())? businessProfileVO.getPrivacy_anonymity_dataprotection() :"");
                        break;
                    case sslSecured:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getSslSecured())? businessProfileVO.getSslSecured() :"");
                        break;
                    case productrequireage:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getProduct_requires())? businessProfileVO.getProduct_requires() :"");
                        break;
                    case affiliateprograms:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getAffiliate_programs())? businessProfileVO.getAffiliate_programs() :"");
                        break;
                    /*case cardtypesacceptedvisa:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getCardvolume_visa())? businessProfileVO.getCardvolume_visa() :"");
                        break;
                    case cardtypesacceptedmastercard:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getCardvolume_mastercard())? businessProfileVO.getCardvolume_mastercard() :"");
                        break;*/
                    case customersupportemailrdbtn:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCustomersupport_email()) ? "Y" : "N");
                        break;
                    case technicalcontactrdbtn:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getTechnical_contact()) ? "Y" : "N");
                        break;
                    case methodofacceptancemotordbtn:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getMoto_percentage()) ? "Y" : "N");
                        break;
                    case methodofacceptanceinternetrdbtn:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getInternet_percentage()) ? "Y" : "N");
                        break;
                    case isRecurringBilling:
                        acroFields.setField(definedAcroFields.name().toString(), (functions.isValueNull(businessProfileVO.getBillingModel()) && "recurring".equalsIgnoreCase(businessProfileVO.getBillingModel())) ? "Y" : "N");
                        break;
                    case isRiskManagement:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getRisk_management()) ? "Y" :"N");
                        break;
                    case isCustomersidentification:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCustomers_identification()) ? "Y" : "N");
                        break;
                    case isCardTypeAcceptedMC:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_mastercard()) ? "Y" : "N");
                        break;
                    case isCardTypeAcceptedVISA:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_visa()) ? "Y" : "N");
                        break;
                    case isCardTypeAcceptedMC2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_mastercard()) ? "Y" : "N");
                        break;
                    case isCardTypeAcceptedVISA2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCardvolume_visa()) ? "Y" : "N");
                        break;

                    case isAquirer:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(bankProfileVO.getAquirer()) ? "Y" :"N");
                        break;

                    case complianceswapp:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(cardholderProfileVO.getCompliance_swapp()) ? "Y" :"N");
                        break;

                    case compliancecispcompliant:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant())? cardholderProfileVO.getCompliance_cispcompliant() :"");
                        break;

                    case compliancepcidsscompliant:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant())? cardholderProfileVO.getCompliance_pcidsscompliant() :"");
                        break;

                    case goodsInsuranceOffered:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getGoodsInsuranceOffered())? extraDetailsProfileVO.getGoodsInsuranceOffered() :"");
                        break;
                    case companyfinancialReport:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(extraDetailsProfileVO.getCompany_financialReport())? extraDetailsProfileVO.getCompany_financialReport() : "");
                        break;
                    case financialReportavailable:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getFinancialReport_available())? extraDetailsProfileVO.getFinancialReport_available() :"");
                        break;
                    case compliancepunitiveSanction:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanction())? extraDetailsProfileVO.getCompliance_punitiveSanction() :"");
                        break;
                    case deedOfAgreement:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreement())? extraDetailsProfileVO.getDeedOfAgreement() :"");
                        break;
                    case fulfillmentproductEmail:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmail())? extraDetailsProfileVO.getFulfillment_productEmail() :"");
                        break;
                    case operationalLicense:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getOperationalLicense())? extraDetailsProfileVO.getOperationalLicense() :"");
                        break;
                    case supervisorregularcontrole:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getSupervisorregularcontrole())? extraDetailsProfileVO.getSupervisorregularcontrole() :"");
                        break;
                    case blacklistedAccountClosed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosed())? extraDetailsProfileVO.getBlacklistedAccountClosed() : "");
                        break;
                    /*case politicallyExposed1:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getPoliticallyExposed1())? ownershipProfileVO.getPoliticallyExposed1() :"");
                        break;
                    case criminalRecord1:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getCriminalRecord1())? ownershipProfileVO.getCriminalRecord1() : "");
                        break;
                    case politicallyExposed2:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getPoliticallyExposed2())? ownershipProfileVO.getPoliticallyExposed2() :"");
                        break;
                    case criminalRecord2:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getCriminalRecord2())? ownershipProfileVO.getCriminalRecord2() :"");
                        break;
                    case politicallyExposed3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getPoliticallyExposed3())? ownershipProfileVO.getPoliticallyExposed3() :"");
                        break;
                    case criminalRecord3:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getCriminalRecord3())? ownershipProfileVO.getCriminalRecord3() :"");
                        break;*/

                    case shareholderprofile1Politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getPoliticallyexposed() :"");
                        break;
                    case shareholderprofile1Criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getCriminalrecord() : "");
                        break;
                    case shareholderprofile2Politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getPoliticallyexposed() :"");
                        break;
                    case shareholderprofile2Criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getCriminalrecord() : "");
                        break;
                    case shareholderprofile3Politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getPoliticallyexposed() :"");
                        break;
                    case shareholderprofile3Criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getCriminalrecord() : "");
                        break;
                    case authorizedsignatoryprofile1Politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getPoliticallyexposed() :"");
                        break;
                    case authorizedsignatoryprofile1Criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getCriminalrecord() : "");
                        break;
                    case authorizedsignatoryprofile2Politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getPoliticallyexposed() :"");
                        break;
                    case authorizedsignatoryprofile2Criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getCriminalrecord() : "");
                        break;
                    case authorizedsignatoryprofile3Politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPoliticallyexposed() :"");
                        break;
                    case authorizedsignatoryprofile3Criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getCriminalrecord() : "");
                        break;

                    case directorsprofile_politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getPoliticallyexposed() : "");
                        break;
                    case directorsprofile_criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1").getCriminalrecord() : "");
                        break;
                    case directorsprofile2_politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getPoliticallyexposed() : "");
                        break;
                    case directorsprofile2_criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2").getCriminalrecord() : "");
                        break;
                    case directorsprofile3_politicallyexposed:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getPoliticallyexposed())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getPoliticallyexposed() : "");
                        break;
                    case directorsprofile3_criminalrecord:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getCriminalrecord())? ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3").getCriminalrecord() : "");
                        break;
                    case automaticRecurring:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getAutomaticRecurring())? businessProfileVO.getAutomaticRecurring() :"");
                        break;
                    case freeMembership:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getFreeMembership())? businessProfileVO.getFreeMembership() :"");
                        break;
                    case creditCardRequired:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getCreditCardRequired())? businessProfileVO.getCreditCardRequired() :"");
                        break;
                    case automaticallyBilled:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getAutomaticallyBilled())? businessProfileVO.getAutomaticallyBilled() :"");
                        break;
                    case preAuthorization:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getPreAuthorization())? businessProfileVO.getPreAuthorization() :"");
                        break;
                    case livechat:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getLivechat())?businessProfileVO.getLivechat():"");
                        break;
                   /* case MCCCtegory:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getMCC_Ctegory())?businessProfileVO.getMCC_Ctegory():"");
                        break;*/
                    case customersidentification:
                        acroFields.setField(definedAcroFields.name().toString(), functions.isValueNull(businessProfileVO.getCustomers_identification())?businessProfileVO.getCustomers_identification():"");
                        break;
                    case seasonalFluctuating:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getSeasonal_fluctuating()) ? businessProfileVO.getSeasonal_fluctuating() : "");
                        break;
                    case agency_employed:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getAgency_employed()) ? businessProfileVO.getAgency_employed() : "");
                        break;
                    case listfraudtools:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getListfraudtools()) ? businessProfileVO.getListfraudtools() : "");
                        break;
                    case is_website_live:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getIs_website_live()) ? businessProfileVO.getIs_website_live() : "");
                        break;
                    case paymentDelivery_UponPurchase:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPayment_delivery()) ? businessProfileVO.getPayment_delivery() : "");
                        break;
                    case paymentDelivery_OnDelivery:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPayment_delivery()) ? businessProfileVO.getPayment_delivery() : "");
                        break;
                    case paymentDelivery_Download:
                        acroFields.setField(definedAcroFields.name().toString(),functions.isValueNull(businessProfileVO.getPayment_delivery()) ? businessProfileVO.getPayment_delivery() : "");
                        break;
                }
            }
        }
    }

    private String getFromProcessingDate(BankProfileVO bankProfileVO)
    {
        String date = "";
        Functions functions = new Functions();
        SimpleDateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3())){

        }

        return date;
    }

    private Double calAvgChkRatio(BankProfileVO bankProfileVO)
    {
        Functions functions = new Functions();
        Double cbkRatio = 0.0;
        int months = 0;

        if(!functions.isValueNull(bankProfileVO.getChargebackratio_year3())) {
            bankProfileVO.setChargebackratio_year3("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_year2())) {
            bankProfileVO.setChargebackratio_year2("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_12monthsago())) {
            bankProfileVO.setChargebackratio_12monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_6monthsago())) {
            bankProfileVO.setChargebackratio_6monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_5monthsago())) {
            bankProfileVO.setChargebackratio_5monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_4monthsago())) {
            bankProfileVO.setChargebackratio_4monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_3monthsago())) {
            bankProfileVO.setChargebackratio_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_2monthsago())) {
            bankProfileVO.setChargebackratio_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackratio_lastmonth())) {
            bankProfileVO.setChargebackratio_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getChargebackratio_year3())) {
            months = 36;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_year2())) {
            months = 24;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_12monthsago())) {
            months = 12;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_6monthsago())) {
            months = 6;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_5monthsago())) {
            months = 5;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_4monthsago())) {
            months = 4;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_3monthsago())) {
            months = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_2monthsago())) {
            months = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackratio_lastmonth())) {
            months = 1;
        }

        cbkRatio = (Double.parseDouble(bankProfileVO.getChargebackratio_year3()) + Double.parseDouble(bankProfileVO.getChargebackratio_year2()) + Double.parseDouble(bankProfileVO.getChargebackratio_12monthsago())
                + Double.parseDouble(bankProfileVO.getChargebackratio_6monthsago()) + Double.parseDouble(bankProfileVO.getChargebackratio_5monthsago()) + Double.parseDouble(bankProfileVO.getChargebackratio_4monthsago())
                + Double.parseDouble(bankProfileVO.getChargebackratio_3monthsago()) + Double.parseDouble(bankProfileVO.getChargebackratio_2monthsago()) + Double.parseDouble(bankProfileVO.getChargebackratio_lastmonth())) / months;

        return cbkRatio;
    }

    private int getNumberOfTransLastYear(BankProfileVO bankProfileVO)
    {
        int numberOfTransLastYear = 0;

        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago())) {
            bankProfileVO.setNumberoftransactions_12monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago())) {
            bankProfileVO.setNumberoftransactions_6monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago())) {
            bankProfileVO.setNumberoftransactions_5monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago())) {
            bankProfileVO.setNumberoftransactions_4monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago())) {
            bankProfileVO.setNumberoftransactions_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago())) {
            bankProfileVO.setNumberoftransactions_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth())) {
            bankProfileVO.setNumberoftransactions_lastmonth("0");
        }

        numberOfTransLastYear = (Integer.parseInt(bankProfileVO.getNumberoftransactions_lastmonth()) + Integer.parseInt(bankProfileVO.getNumberoftransactions_2monthsago()) + Integer.parseInt(bankProfileVO.getNumberoftransactions_3monthsago())
                + Integer.parseInt(bankProfileVO.getNumberoftransactions_4monthsago()) + Integer.parseInt(bankProfileVO.getNumberoftransactions_5monthsago()) + Integer.parseInt(bankProfileVO.getNumberoftransactions_6monthsago())
                + Integer.parseInt(bankProfileVO.getNumberoftransactions_12monthsago()));
        logger.debug("numberOfTransLastYear----"+numberOfTransLastYear);

        return numberOfTransLastYear;
    }

    private Double getNumberOfTransThroughInternet(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        Double numberOfTransThroughInternet = 0.0;
        if(functions.isValueNull(businessProfileVO.getInternet_percentage()))
            numberOfTransThroughInternet = getNumberOfTransLastYear(bankProfileVO) * (Double.parseDouble(businessProfileVO.getInternet_percentage()) / 100);
        return numberOfTransThroughInternet;
    }

    private int getNumberOfTransThroughInternetByMC(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        int numberOfTransThroughInternetByMC = 0;
        if(functions.isValueNull(businessProfileVO.getCardvolume_mastercard()))
            numberOfTransThroughInternetByMC = getNumberOfTransThroughInternet(bankProfileVO, businessProfileVO).intValue() * (Integer.parseInt(businessProfileVO.getCardvolume_mastercard()) / 100);
        return numberOfTransThroughInternetByMC;
    }

    private Double getNumberOfTransThroughInternetByVISA(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        double numberOfTransThroughInternetByVISA = 0;
        if(functions.isValueNull(businessProfileVO.getCardvolume_visa()))
            numberOfTransThroughInternetByVISA = getNumberOfTransThroughInternet(bankProfileVO, businessProfileVO).intValue() * (Integer.parseInt(businessProfileVO.getCardvolume_visa()) / 100);
        logger.debug("numberOfTransThroughInternetByVISA----"+numberOfTransThroughInternetByVISA);
        return numberOfTransThroughInternetByVISA;
    }

    private Double getNumberOfTransThroughMoto(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        Double numberOfTransThroughMoto = 0.0;
        if(functions.isValueNull(businessProfileVO.getMoto_percentage()))
            numberOfTransThroughMoto = getNumberOfTransLastYear(bankProfileVO) * (Double.parseDouble(businessProfileVO.getMoto_percentage()) / 100);
        return numberOfTransThroughMoto;
    }

    private Double getNumberOfTransThroughMotoByMC(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        double numberOfTransThroughMotoByMC = 0;
        if(functions.isValueNull(businessProfileVO.getCardvolume_mastercard()))
            numberOfTransThroughMotoByMC = getNumberOfTransThroughMoto(bankProfileVO, businessProfileVO).intValue() * (Integer.parseInt(businessProfileVO.getCardvolume_mastercard()) / 100);
        return numberOfTransThroughMotoByMC;
    }

    private Double getNumberOfTransThroughMotoByVISA(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        double numberOfTransThroughMotoByVISA = 0;
        if(functions.isValueNull(businessProfileVO.getCardvolume_visa()))
            numberOfTransThroughMotoByVISA = getNumberOfTransThroughMoto(bankProfileVO, businessProfileVO).intValue() * (Integer.parseInt(businessProfileVO.getCardvolume_visa()) / 100);
        return numberOfTransThroughMotoByVISA;
    }

    private Double getMonthlyCreditVolume(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        double monthlyCreditVolume = 0.0;
        if(functions.isValueNull(businessProfileVO.getPaymenttype_credit()) && functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            monthlyCreditVolume = (Double.parseDouble(businessProfileVO.getPaymenttype_credit()) * Double.parseDouble(bankProfileVO.getSalesvolume_lastmonth())) / 100;
        return monthlyCreditVolume;
    }

    private Double getMonthlyDebitVolume(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        double monthlyDebitVolume = 0.0;
        if(functions.isValueNull(businessProfileVO.getPaymenttype_credit()) && functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            monthlyDebitVolume = (Double.parseDouble(businessProfileVO.getPaymenttype_debit()) * Double.parseDouble(bankProfileVO.getSalesvolume_lastmonth())) / 100;
        return monthlyDebitVolume;
    }

    private Double getMonthlyCardVolumeForTW(BankProfileVO bankProfileVO, BusinessProfileVO businessProfileVO)
    {
        double avgMonthlCardVolumeForTW = 0;
        //System.out.println("debit details....."+businessProfileVO.getPaymenttype_debit());
        //System.out.println("credit details....."+businessProfileVO.getPaymenttype_credit());
        //System.out.println("Salesvolume lastmonth....."+bankProfileVO.getSalesvolume_lastmonth());

        if((functions.isValueNull(businessProfileVO.getPaymenttype_credit()) && functions.isValueNull(businessProfileVO.getPaymenttype_debit())) && functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            avgMonthlCardVolumeForTW = ((Integer.parseInt(businessProfileVO.getPaymenttype_credit()) + Integer.parseInt(businessProfileVO.getPaymenttype_debit())) * Integer.parseInt(bankProfileVO.getSalesvolume_lastmonth())) / 100;
        logger.debug("avgMonthlCardVolumeForTW........"+avgMonthlCardVolumeForTW);
        return avgMonthlCardVolumeForTW;
    }

    private Double getTotalOfRestOfWorld(BusinessProfileVO businessProfileVO)
    {
        double totalOfRestOfWorld = 0;
        if(functions.isValueNull(businessProfileVO.getForeigntransactions_cis()) && functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
            totalOfRestOfWorld = Double.parseDouble(businessProfileVO.getForeigntransactions_cis()) + Double.parseDouble(businessProfileVO.getForeigntransactions_canada()) ;
        return totalOfRestOfWorld;
    }

    private Double getAvgNumberOfTransaction(BankProfileVO bankProfileVO)
    {
        Double avgNumberOfTrans = 0.0;
        int totalNumTransMonth = 0;

        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_year3())) {
            bankProfileVO.setNumberoftransactions_year3("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_year2())) {
            bankProfileVO.setNumberoftransactions_year2("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago())) {
            bankProfileVO.setNumberoftransactions_12monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago())) {
            bankProfileVO.setNumberoftransactions_6monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago())) {
            bankProfileVO.setNumberoftransactions_5monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago())) {
            bankProfileVO.setNumberoftransactions_4monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago())) {
            bankProfileVO.setNumberoftransactions_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago())) {
            bankProfileVO.setNumberoftransactions_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth())) {
            bankProfileVO.setNumberoftransactions_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getNumberoftransactions_year3())) {
            totalNumTransMonth = 36;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_year2())) {
            totalNumTransMonth = 24;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago())) {
            totalNumTransMonth = 12;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago())) {
            totalNumTransMonth = 6;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago())) {
            totalNumTransMonth = 5;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago())) {
            totalNumTransMonth = 4;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago())) {
            totalNumTransMonth = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago())) {
            totalNumTransMonth = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth())) {
            totalNumTransMonth = 1;
        }
        avgNumberOfTrans = ( Double.parseDouble(bankProfileVO.getNumberoftransactions_lastmonth()) + Double.parseDouble(bankProfileVO.getNumberoftransactions_2monthsago()) + Double.parseDouble(bankProfileVO.getNumberoftransactions_3monthsago())
                + Double.parseDouble(bankProfileVO.getNumberoftransactions_4monthsago()) + Double.parseDouble(bankProfileVO.getNumberoftransactions_5monthsago()) + Double.parseDouble(bankProfileVO.getNumberoftransactions_6monthsago())
                + Double.parseDouble(bankProfileVO.getNumberoftransactions_12monthsago()) + Double.parseDouble(bankProfileVO.getNumberoftransactions_year2()) + Double.parseDouble(bankProfileVO.getNumberoftransactions_year3()) ) / totalNumTransMonth;

        return avgNumberOfTrans;
    }

    private Double getAvgSalesVolume(BankProfileVO bankProfileVO)
    {
        Double avgSaleVolume = 0.0;
        int totalSaleMonths = 0;

        if(!functions.isValueNull(bankProfileVO.getSalesvolume_year3())) {
            bankProfileVO.setSalesvolume_year3("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_year2())) {
            bankProfileVO.setSalesvolume_year2("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago())) {
            bankProfileVO.setSalesvolume_12monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago())) {
            bankProfileVO.setSalesvolume_6monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago())) {
            bankProfileVO.setSalesvolume_5monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago())) {
            bankProfileVO.setSalesvolume_4monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago())) {
            bankProfileVO.setSalesvolume_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago())) {
            bankProfileVO.setSalesvolume_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth())) {
            bankProfileVO.setSalesvolume_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3())) {
            totalSaleMonths = 36;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_year2())) {
            totalSaleMonths = 24;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago())) {
            totalSaleMonths = 12;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago())) {
            totalSaleMonths = 6;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago())) {
            totalSaleMonths = 5;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago())) {
            totalSaleMonths = 4;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago())) {
            totalSaleMonths = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago())) {
            totalSaleMonths = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth())) {
            totalSaleMonths = 1;
        }
        avgSaleVolume = ( Double.parseDouble(bankProfileVO.getSalesvolume_lastmonth()) + Double.parseDouble(bankProfileVO.getSalesvolume_2monthsago()) + Double.parseDouble(bankProfileVO.getSalesvolume_3monthsago())
                + Double.parseDouble(bankProfileVO.getSalesvolume_4monthsago()) + Double.parseDouble(bankProfileVO.getSalesvolume_5monthsago()) + Double.parseDouble(bankProfileVO.getSalesvolume_6monthsago())
                + Double.parseDouble(bankProfileVO.getSalesvolume_12monthsago()) + Double.parseDouble(bankProfileVO.getSalesvolume_year2()) + Double.parseDouble(bankProfileVO.getSalesvolume_year3()) ) / totalSaleMonths;

        return avgSaleVolume;
    }

    private Double getAvgOfNumberOfCbk3MothsAgo(BankProfileVO bankProfileVO)
    {
        Double avgNumberOfCbk = 0.0;
        int months = 0;

        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago())) {
            bankProfileVO.setNumberofchargebacks_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago())) {
            bankProfileVO.setNumberofchargebacks_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth())) {
            bankProfileVO.setNumberofchargebacks_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago())) {
            months = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago())) {
            months = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth())) {
            months = 1;
        }

        avgNumberOfCbk = (Double.parseDouble(bankProfileVO.getNumberofchargebacks_3monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_2monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_lastmonth())) / months;

        return avgNumberOfCbk;
    }

    private Double getAvgOfNumberOfCbk(BankProfileVO bankProfileVO)
    {
        Double avgNumberOfCbk = 0.0;
        int months = 0;

        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_year3())) {
            bankProfileVO.setNumberofchargebacks_year3("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_year2())) {
            bankProfileVO.setNumberofchargebacks_year2("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_12monthsago())) {
            bankProfileVO.setNumberofchargebacks_12monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago())) {
            bankProfileVO.setNumberofchargebacks_6monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago())) {
            bankProfileVO.setNumberofchargebacks_5monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago())) {
            bankProfileVO.setNumberofchargebacks_4monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago())) {
            bankProfileVO.setNumberofchargebacks_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago())) {
            bankProfileVO.setNumberofchargebacks_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth())) {
            bankProfileVO.setNumberofchargebacks_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_year3())) {
            months = 36;
        }
        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_year2())) {
            months = 24;
        }
        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_12monthsago())) {
            months = 12;
        }
        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago())) {
            months = 6;
        }
        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago())) {
            months = 5;
        }
        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago())) {
            months = 4;
        }
        if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago())) {
            months = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago())) {
            months = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth())) {
            months = 1;
        }

        avgNumberOfCbk =( Double.parseDouble(bankProfileVO.getNumberofchargebacks_year3()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_year2()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_12monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_6monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_5monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_4monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_3monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_2monthsago()) + Double.parseDouble(bankProfileVO.getNumberofchargebacks_lastmonth())) / months;
        return avgNumberOfCbk;
    }

    private Double getAvgCbkVolume(BankProfileVO bankProfileVO)
    {
        Double avgCbkVolume =0.0;
        int months = 0;

        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_year3())) {
            bankProfileVO.setChargebackvolume_year3("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_year2())) {
            bankProfileVO.setChargebackvolume_year2("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_12monthsago())) {
            bankProfileVO.setChargebackvolume_12monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_6monthsago())) {
            bankProfileVO.setChargebackvolume_6monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_5monthsago())) {
            bankProfileVO.setChargebackvolume_5monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_4monthsago())) {
            bankProfileVO.setChargebackvolume_4monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_3monthsago())) {
            bankProfileVO.setChargebackvolume_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_2monthsago())) {
            bankProfileVO.setChargebackvolume_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getChargebackvolume_lastmonth())) {
            bankProfileVO.setChargebackvolume_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getChargebackvolume_year3())) {
            months = 36;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_year2())) {
            months = 24;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_12monthsago())) {
            months = 12;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_6monthsago())) {
            months = 6;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_5monthsago())) {
            months = 5;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_4monthsago())) {
            months = 4;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_3monthsago())) {
            months = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_2monthsago())) {
            months = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getChargebackvolume_lastmonth())) {
            months = 1;
        }
        avgCbkVolume = ( Double.parseDouble(bankProfileVO.getChargebackvolume_lastmonth()) + Double.parseDouble(bankProfileVO.getChargebackvolume_2monthsago()) + Double.parseDouble(bankProfileVO.getChargebackvolume_3monthsago())
                + Double.parseDouble(bankProfileVO.getChargebackvolume_4monthsago()) + Double.parseDouble(bankProfileVO.getChargebackvolume_5monthsago()) + Double.parseDouble(bankProfileVO.getChargebackvolume_6monthsago())
                + Double.parseDouble(bankProfileVO.getChargebackvolume_12monthsago()) + Double.parseDouble(bankProfileVO.getChargebackvolume_year2()) + Double.parseDouble(bankProfileVO.getChargebackvolume_year3()) ) / months;

        return avgCbkVolume;
    }

    private Double getAvgOfNumberOfRefund3MothsAgo(BankProfileVO bankProfileVO)
    {
        Double avgNumberOfRefund = 0.0;
        int months = 0;

        if(!functions.isValueNull(bankProfileVO.getNumberofrefunds_3monthsago())) {
            bankProfileVO.setNumberofrefunds_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofrefunds_2monthsago())) {
            bankProfileVO.setNumberofrefunds_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getNumberofrefunds_lastmonth())) {
            bankProfileVO.setNumberofrefunds_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getNumberofrefunds_3monthsago())) {
            months = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberofrefunds_2monthsago())) {
            months = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getNumberofrefunds_lastmonth())) {
            months = 1;
        }

        avgNumberOfRefund = (Double.parseDouble(bankProfileVO.getNumberofrefunds_3monthsago()) + Double.parseDouble(bankProfileVO.getNumberofrefunds_2monthsago()) + Double.parseDouble(bankProfileVO.getNumberofrefunds_lastmonth())) / months;

        return avgNumberOfRefund;
    }

    private Double getAvgRefundVolume(BankProfileVO bankProfileVO)
    {
        Double avgRefundVolume =0.0;
        int months = 0;

        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_year3())) {
            bankProfileVO.setRefundsvolume_year3("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_year2())) {
            bankProfileVO.setRefundsvolume_year2("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_12monthsago())) {
            bankProfileVO.setRefundsvolume_12monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_6monthsago())) {
            bankProfileVO.setRefundsvolume_6monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_5monthsago())) {
            bankProfileVO.setRefundsvolume_5monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_4monthsago())) {
            bankProfileVO.setRefundsvolume_4monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_3monthsago())) {
            bankProfileVO.setRefundsvolume_3monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_2monthsago())) {
            bankProfileVO.setRefundsvolume_2monthsago("0");
        }
        if(!functions.isValueNull(bankProfileVO.getRefundsvolume_lastmonth())) {
            bankProfileVO.setRefundsvolume_lastmonth("0");
        }

        if(functions.isValueNull(bankProfileVO.getRefundsvolume_year3())) {
            months = 36;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_year2())) {
            months = 24;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_12monthsago())) {
            months = 12;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_6monthsago())) {
            months = 6;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_5monthsago())) {
            months = 5;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_4monthsago())) {
            months = 4;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_3monthsago())) {
            months = 3;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_2monthsago())) {
            months = 2;
        }
        else if(functions.isValueNull(bankProfileVO.getRefundsvolume_lastmonth())) {
            months = 1;
        }
        avgRefundVolume = (Double.parseDouble(bankProfileVO.getRefundsvolume_lastmonth()) + Double.parseDouble(bankProfileVO.getRefundsvolume_2monthsago()) + Double.parseDouble(bankProfileVO.getRefundsvolume_3monthsago())
                + Double.parseDouble(bankProfileVO.getRefundsvolume_4monthsago()) + Double.parseDouble(bankProfileVO.getRefundsvolume_5monthsago()) + Double.parseDouble(bankProfileVO.getRefundsvolume_6monthsago())
                + Double.parseDouble(bankProfileVO.getRefundsvolume_12monthsago()) + Double.parseDouble(bankProfileVO.getRefundsvolume_year2()) + Double.parseDouble(bankProfileVO.getRefundsvolume_year3())) / months;

        return avgRefundVolume;
    }

    private String getProcessingFromDate(BankProfileVO bankProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(bankProfileVO !=null && functions.isValueNull(bankProfileVO.getProcessinghistory_creation_time()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(bankProfileVO.getProcessinghistory_creation_time());
                cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(cal.getTime());
        else
            return "";
    }

    private String getProcessingToDate(BankProfileVO bankProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(bankProfileVO !=null && functions.isValueNull(bankProfileVO.getProcessinghistory_updation_time()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(bankProfileVO.getProcessinghistory_updation_time());
                cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(cal.getTime());
        else
            return "";
    }

    private String getCompanyBankruptcyDate(BankProfileVO bankProfileVO,CompanyProfileVO companyProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(companyProfileVO !=null && functions.isValueNull(companyProfileVO.getCompanyBankruptcydate()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(companyProfileVO.getCompanyBankruptcydate());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }

    private String getComplianceDateOfLastScan(BankProfileVO bankProfileVO,CardholderProfileVO cardholderProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(cardholderProfileVO !=null && functions.isValueNull(cardholderProfileVO.getCompliance_dateoflastscan()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(cardholderProfileVO.getCompliance_dateoflastscan());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getCompanyDateOfRegistration(BankProfileVO bankProfileVO,CompanyProfileVO companyProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(companyProfileVO.getCompanyProfile_addressVOMap()!=null && companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY")!=null &&
                    functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getDate_of_registration()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY").getDate_of_registration());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }

    private String getShareholderProfile1DateOfBirth(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getDateofbirth()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getDateofbirth());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getShareholderProfile2DateOfBirth(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getDateofbirth()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getDateofbirth());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getShareholderProfile3DateOfBirth(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getDateofbirth()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getDateofbirth());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getAuthorizedSignatoryProfileDateOfBirth(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getDateofbirth()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getDateofbirth());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->",e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getAuthorizedSignatoryProfile2DateOfBirth(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getDateofbirth()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getDateofbirth());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->",e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getShareholderProfile1PassportIssueDate(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;

        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getPassportissuedate()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1").getPassportissuedate());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getShareholderProfile2PassportIssueDate(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;

        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getPassportissuedate()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2").getPassportissuedate());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getShareholderProfile3PassportIssueDate(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getPassportissuedate()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3").getPassportissuedate());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getAuthorizedSignatoryProfilePassportIssueDate(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;

        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getPassportissuedate()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1").getPassportissuedate());
                //cal = Calendar.getInstance();
                //cal.setTime(date);
                //cal.add(Calendar.MONTH, -month);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }

    private String getAuthorizedSignatoryProfile2PassportIssueDate(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getPassportissuedate()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2").getPassportissuedate());
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->", e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
    private String getAuthorizedSignatoryProfile3PassportIssueDate(BankProfileVO bankProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        int month = 0;
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
            month = 36;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
            month = 24;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
            month = 12;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
            month = 6;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
            month = 5;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
            month = 4;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
            month = 3;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
            month = 2;
        if(functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
            month = 1;

        Calendar cal = null;
        try
        {
            if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3")!=null &&
                    functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPassportissuedate()))
            {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3").getPassportissuedate());
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException---->",e);
        }
        if(date!=null)
            return dateFormat.format(date.getTime());
        else
            return "";
    }
}