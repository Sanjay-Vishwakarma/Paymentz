<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="com.vo.applicationManagerVOs.AddressIdentificationVO" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.CompanyProfileVO" %>
<%@ page import="com.vo.applicationManagerVOs.ContactDetailsVO" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>

<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<script type="text/javascript">
    $('#sandbox-container input').datepicker({});
</script>


<style type="text/css">
    .form-control {
        /*height: 35px;*/
    }

    /*.input-group{
        display: flex;
    }*/
    .input-group-addon {
        font-weight: 800;
        color: #a94442;
        background-color: #ebccd1;
        text-shadow: 1px 0px;
        border-radius: inherit;
        /*padding: 10px 25px 10px 15px;*/
    }

    .glyphicon {
        display: block !important;
        color: #a94442 !important;
        background-color: #ebccd1 !important;
        width: 40px !important;
        margin-right: 16px !important;
        height: 32px !important;
        /*margin-top: -1px !important;*/
    }

    #align_check > .icheckbox_square-aero {
        display: table;
    }

    @media (max-width: 364px) {
        #income_sources_other_yes {
            margin-top: 10px;
            margin-left: 20px;
            width: 90% !important;
        }

        #income_row {
            margin-left: -15px !important;
        }
    }

    #income_row {
        margin-left: 0 !important;
    }

    @media (max-width: 991px) {
        #margintop10 {
            margin-top: 10px;
        }

        #top10 {
            top: 10px;
        }
    }
</style>

<%!
    private Functions functions = new Functions();
    private AppFunctionUtil appFunctionUtil= new AppFunctionUtil();
%>
<%

    boolean view=false;
    String globaldisabled="";
    if(functions.isValueNull(request.getParameter("view")))
    {
        globaldisabled="disabled";
        view=true;
    }
    else
    {
        globaldisabled="";
    }

    String selectedY="";
    String selectedN="";
    String disableEU="";
    String bgcolor="";
    ApplicationManagerVO applicationManagerVO=null;
    CompanyProfileVO companyProfileVO = null;

    //Start : Added to ensure OwnershipProfileVO is not null
   /* OwnershipProfileVO ownershipProfileVO=null;
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory3 = new OwnershipProfileDetailsVO();
    Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap = new HashMap();*/
    //End : Added to ensure OwnershipProfileVO is not null
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String companyprofile_Welcome = StringUtils.isNotEmpty(rb1.getString("companyprofile_Welcome")) ? rb1.getString("companyprofile_Welcome") : "Welcome";
    String companyprofile_thank = StringUtils.isNotEmpty(rb1.getString("companyprofile_thank")) ? rb1.getString("companyprofile_thank") : " Thank you for choosing us for processing your transactions, we would be happy to be your Payment Processor.";
    String companyprofile_short = StringUtils.isNotEmpty(rb1.getString("companyprofile_short")) ? rb1.getString("companyprofile_short") : "Short description for each step:";
    String companyprofile_step_company = StringUtils.isNotEmpty(rb1.getString("companyprofile_step_company")) ? rb1.getString("companyprofile_step_company") : "Step 1 : Company Profile";
    String companyprofile_step_ownership = StringUtils.isNotEmpty(rb1.getString("companyprofile_step_ownership")) ? rb1.getString("companyprofile_step_ownership") : "Step 2 : Ownership Profile";
    String companyprofile_step_business = StringUtils.isNotEmpty(rb1.getString("companyprofile_step_business")) ? rb1.getString("companyprofile_step_business") : "Step 3 : Business/Website Info";
    String companyprofile_step_bank = StringUtils.isNotEmpty(rb1.getString("companyprofile_step_bank")) ? rb1.getString("companyprofile_step_bank") : "Step 4 : Bank Profile";
    String companyprofile_step_cardholder = StringUtils.isNotEmpty(rb1.getString("companyprofile_step_cardholder")) ? rb1.getString("companyprofile_step_cardholder") : " Step 5 : Cardholder Profile";
    String companyprofile_step_upload = StringUtils.isNotEmpty(rb1.getString("companyprofile_step_upload")) ? rb1.getString("companyprofile_step_upload") : "Step 6 : KYC Doc Upload";
    String companyprofile_Company_Profile = StringUtils.isNotEmpty(rb1.getString("companyprofile_Company_Profile")) ? rb1.getString("companyprofile_Company_Profile") : "Company Profile";
    String companyprofile_please = StringUtils.isNotEmpty(rb1.getString("companyprofile_please")) ? rb1.getString("companyprofile_please") : "Please save Company Profile after entering the data provided";
    String companyprofile_registered = StringUtils.isNotEmpty(rb1.getString("companyprofile_registered")) ? rb1.getString("companyprofile_registered") : "Registered Company Information";
    String companyprofile_Name = StringUtils.isNotEmpty(rb1.getString("companyprofile_Name")) ? rb1.getString("companyprofile_Name") : "Name*";
    String companyprofile_Registration_Number = StringUtils.isNotEmpty(rb1.getString("companyprofile_Registration_Number")) ? rb1.getString("companyprofile_Registration_Number") : "Registration Number*";
    String companyprofile_Date_Registration = StringUtils.isNotEmpty(rb1.getString("companyprofile_Date_Registration")) ? rb1.getString("companyprofile_Date_Registration") : "Date of Registration*";
    String companyprofile_Country_Code = StringUtils.isNotEmpty(rb1.getString("companyprofile_Country_Code")) ? rb1.getString("companyprofile_Country_Code") : "Country Code*";
    String companyprofile_Phone_No = StringUtils.isNotEmpty(rb1.getString("companyprofile_Phone_No")) ? rb1.getString("companyprofile_Phone_No") : "Phone No*";
    String companyprofile_Fax = StringUtils.isNotEmpty(rb1.getString("companyprofile_Fax")) ? rb1.getString("companyprofile_Fax") : "Fax";
    String companyprofile_Email_Address = StringUtils.isNotEmpty(rb1.getString("companyprofile_Email_Address")) ? rb1.getString("companyprofile_Email_Address") : "Email Address*";
    String companyprofile_Identification_Details = StringUtils.isNotEmpty(rb1.getString("companyprofile_Identification_Details")) ? rb1.getString("companyprofile_Identification_Details") : "Identification Details";
    String companyprofile_VAT_Identification = StringUtils.isNotEmpty(rb1.getString("companyprofile_VAT_Identification")) ? rb1.getString("companyprofile_VAT_Identification") : "VAT Identification Number";
    String companyprofile_tic = StringUtils.isNotEmpty(rb1.getString("companyprofile_tic")) ? rb1.getString("companyprofile_tic") : "TIC/TIN Number/Federal Tax ID/PAN";
    String companyprofile_Address_Details = StringUtils.isNotEmpty(rb1.getString("companyprofile_Address_Details")) ? rb1.getString("companyprofile_Address_Details") : "Address Details";
    String companyprofile_Address_Proof = StringUtils.isNotEmpty(rb1.getString("companyprofile_Address_Proof")) ? rb1.getString("companyprofile_Address_Proof") : "Address Proof*";
    String companyprofile_Address_ID = StringUtils.isNotEmpty(rb1.getString("companyprofile_Address_ID")) ? rb1.getString("companyprofile_Address_ID") : "Address ID*";
    String companyprofile_Registration = StringUtils.isNotEmpty(rb1.getString("companyprofile_Registration")) ? rb1.getString("companyprofile_Registration") : "Registration Address(no p/o)*";
    String companyprofile_Street = StringUtils.isNotEmpty(rb1.getString("companyprofile_Street")) ? rb1.getString("companyprofile_Street") : "Street*";
    String companyprofile_city = StringUtils.isNotEmpty(rb1.getString("companyprofile_city")) ? rb1.getString("companyprofile_city") : "City/Town";
    String companyprofile_state = StringUtils.isNotEmpty(rb1.getString("companyprofile_state")) ? rb1.getString("companyprofile_state") : "State/County/Province";
    String companyprofile_Country = StringUtils.isNotEmpty(rb1.getString("companyprofile_Country")) ? rb1.getString("companyprofile_Country") : "Country*";
    String companyprofile_zip = StringUtils.isNotEmpty(rb1.getString("companyprofile_zip")) ? rb1.getString("companyprofile_zip") : "Zip/Postal Code";
    String companyprofile_type_business = StringUtils.isNotEmpty(rb1.getString("companyprofile_type_business")) ? rb1.getString("companyprofile_type_business") : "Type of Business :-";
    String companyprofile_Corporation = StringUtils.isNotEmpty(rb1.getString("companyprofile_Corporation")) ? rb1.getString("companyprofile_Corporation") : "Corporation";
    String companyprofile_Limited_Liability = StringUtils.isNotEmpty(rb1.getString("companyprofile_Limited_Liability")) ? rb1.getString("companyprofile_Limited_Liability") : "Limited Liability Company";
    String companyprofile_Sole = StringUtils.isNotEmpty(rb1.getString("companyprofile_Sole")) ? rb1.getString("companyprofile_Sole") : "Sole";
    String companyprofile_Proprietor = StringUtils.isNotEmpty(rb1.getString("companyprofile_Proprietor")) ? rb1.getString("companyprofile_Proprietor") : "Proprietor";
    String companyprofile_Partnership = StringUtils.isNotEmpty(rb1.getString("companyprofile_Partnership")) ? rb1.getString("companyprofile_Partnership") : "Partnership";
    String companyprofile_Nonprofit_Organization = StringUtils.isNotEmpty(rb1.getString("companyprofile_Nonprofit_Organization")) ? rb1.getString("companyprofile_Nonprofit_Organization") : "Nonprofit Organization";
    String companyprofile_Startup = StringUtils.isNotEmpty(rb1.getString("companyprofile_Startup")) ? rb1.getString("companyprofile_Startup") : "Is this a start-up business?";
    String companyprofile_Yes = StringUtils.isNotEmpty(rb1.getString("companyprofile_Yes")) ? rb1.getString("companyprofile_Yes") : " Yes";
    String companyprofile_No = StringUtils.isNotEmpty(rb1.getString("companyprofile_No")) ? rb1.getString("companyprofile_No") : "No";
    String companyprofile_long = StringUtils.isNotEmpty(rb1.getString("companyprofile_long")) ? rb1.getString("companyprofile_long") : "If no, how long have you been in business?";
    String companyprofile_want = StringUtils.isNotEmpty(rb1.getString("companyprofile_want")) ? rb1.getString("companyprofile_want") : "If you want same data in";
    String companyprofile_Business_Information = StringUtils.isNotEmpty(rb1.getString("companyprofile_Business_Information")) ? rb1.getString("companyprofile_Business_Information") : "Business Information";
    String companyprofile_Select = StringUtils.isNotEmpty(rb1.getString("companyprofile_Select")) ? rb1.getString("companyprofile_Select") : "Select";
    String companyprofile_Checkbox = StringUtils.isNotEmpty(rb1.getString("companyprofile_Checkbox")) ? rb1.getString("companyprofile_Checkbox") : "Checkbox";
    String companyprofile_EU_Company = StringUtils.isNotEmpty(rb1.getString("companyprofile_EU_Company")) ? rb1.getString("companyprofile_EU_Company") : "EU Company Details";
    String companyprofile_business = StringUtils.isNotEmpty(rb1.getString("companyprofile_business")) ? rb1.getString("companyprofile_business") : "Business Name/DBA(Doing Business As)*";
    String companyprofile_AddressProof = StringUtils.isNotEmpty(rb1.getString("companyprofile_AddressProof")) ? rb1.getString("companyprofile_AddressProof") : "Address Proof";
    String companyprofile_AddressID = StringUtils.isNotEmpty(rb1.getString("companyprofile_AddressID")) ? rb1.getString("companyprofile_AddressID") : "Address ID";
    String companyprofile_Billing_Address = StringUtils.isNotEmpty(rb1.getString("companyprofile_Billing_Address")) ? rb1.getString("companyprofile_Billing_Address") : "Billing Address*";
    String companyprofile_Zip_Code = StringUtils.isNotEmpty(rb1.getString("companyprofile_Zip_Code")) ? rb1.getString("companyprofile_Zip_Code") : "Zip Code";
    String companyprofile_EU_company = StringUtils.isNotEmpty(rb1.getString("companyprofile_EU_company")) ? rb1.getString("companyprofile_EU_company") : "EU company details";
    String companyprofile_Registered_Corporate = StringUtils.isNotEmpty(rb1.getString("companyprofile_Registered_Corporate")) ? rb1.getString("companyprofile_Registered_Corporate") : "Registered Corporate Name";
    String companyprofile_Registered_Directors = StringUtils.isNotEmpty(rb1.getString("companyprofile_Registered_Directors")) ? rb1.getString("companyprofile_Registered_Directors") : "Registered Directors";
    String companyprofile_Registration_Number1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Registration_Number1")) ? rb1.getString("companyprofile_Registration_Number1") : "Registration Number";
    String companyprofile_Country1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Country1")) ? rb1.getString("companyprofile_Country1") : "Country";
    String companyprofile_Street1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Street1")) ? rb1.getString("companyprofile_Street1") : "Street";
    String companyprofile_Registered_Office_Address = StringUtils.isNotEmpty(rb1.getString("companyprofile_Registered_Office_Address")) ? rb1.getString("companyprofile_Registered_Office_Address") : "Registered Office Address";
    String companyprofile_Other_Information = StringUtils.isNotEmpty(rb1.getString("companyprofile_Other_Information")) ? rb1.getString("companyprofile_Other_Information") : "Other Information";
    String companyprofile_Turnover_last = StringUtils.isNotEmpty(rb1.getString("companyprofile_Turnover_last")) ? rb1.getString("companyprofile_Turnover_last") : "Turnover last year";
    String companyprofile_Amount = StringUtils.isNotEmpty(rb1.getString("companyprofile_Amount")) ? rb1.getString("companyprofile_Amount") : "Amount";
    String companyprofile_Capital_Resources = StringUtils.isNotEmpty(rb1.getString("companyprofile_Capital_Resources")) ? rb1.getString("companyprofile_Capital_Resources") : "Capital Resources*";
    String companyprofile_Amount1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Amount1")) ? rb1.getString("companyprofile_Amount1") : "Amount*";
    String companyprofile_No_Employees = StringUtils.isNotEmpty(rb1.getString("companyprofile_No_Employees")) ? rb1.getString("companyprofile_No_Employees") : "No. of Employees*";
    String companyprofile_contact = StringUtils.isNotEmpty(rb1.getString("companyprofile_contact")) ? rb1.getString("companyprofile_contact") : "Contact Information :(All fields are non Mandatory)";
    String companyprofile_relationship = StringUtils.isNotEmpty(rb1.getString("companyprofile_relationship")) ? rb1.getString("companyprofile_relationship") : "Name/Relationship";
    String companyprofile_EmailAddress = StringUtils.isNotEmpty(rb1.getString("companyprofile_EmailAddress")) ? rb1.getString("companyprofile_EmailAddress") : "Email Address";
    String companyprofile_Country_Code1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Country_Code1")) ? rb1.getString("companyprofile_Country_Code1") : "Country Code";
    String companyprofile_Phone_No1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Phone_No1")) ? rb1.getString("companyprofile_Phone_No1") : "Phone No";
    String companyprofile_Designation = StringUtils.isNotEmpty(rb1.getString("companyprofile_Designation")) ? rb1.getString("companyprofile_Designation") : "Designation";
    String companyprofile_Skpe = StringUtils.isNotEmpty(rb1.getString("companyprofile_Skpe")) ? rb1.getString("companyprofile_Skpe") : "Skype/IM Address";
    String companyprofile_Technical_Contact = StringUtils.isNotEmpty(rb1.getString("companyprofile_Technical_Contact")) ? rb1.getString("companyprofile_Technical_Contact") : "Technical Contact :";
    String companyprofile_Financial = StringUtils.isNotEmpty(rb1.getString("companyprofile_Financial")) ? rb1.getString("companyprofile_Financial") : "Financial/billing Contact :";
    String companyprofile_chargeback = StringUtils.isNotEmpty(rb1.getString("companyprofile_chargeback")) ? rb1.getString("companyprofile_chargeback") : "Chargeback/Refund Contacts:";
    String companyprofile_PCI_Contacts = StringUtils.isNotEmpty(rb1.getString("companyprofile_PCI_Contacts")) ? rb1.getString("companyprofile_PCI_Contacts") : "PCI Contacts :";
    String companyprofile_have = StringUtils.isNotEmpty(rb1.getString("companyprofile_have")) ? rb1.getString("companyprofile_have") : "Have you ever filed for bankruptcy ?*";
    String companyprofile_when = StringUtils.isNotEmpty(rb1.getString("companyprofile_when")) ? rb1.getString("companyprofile_when") : "If yes when";
    String companyprofile_license = StringUtils.isNotEmpty(rb1.getString("companyprofile_license")) ? rb1.getString("companyprofile_license") : "Does your business require a license?*";
    String companyprofile_permission = StringUtils.isNotEmpty(rb1.getString("companyprofile_permission")) ? rb1.getString("companyprofile_permission") : "Does the Company have a license/ permission?";
    String companyprofile_legal = StringUtils.isNotEmpty(rb1.getString("companyprofile_legal")) ? rb1.getString("companyprofile_legal") : "Was the Company involved in any legal proceeding?*";
    String companyprofile_insured = StringUtils.isNotEmpty(rb1.getString("companyprofile_insured")) ? rb1.getString("companyprofile_insured") : "Is the Company insured?";
    String companyprofile_details = StringUtils.isNotEmpty(rb1.getString("companyprofile_details")) ? rb1.getString("companyprofile_details") : "If yes please provide the details below";
    String companyprofile_Insured_company = StringUtils.isNotEmpty(rb1.getString("companyprofile_Insured_company")) ? rb1.getString("companyprofile_Insured_company") : "Insured company name";
    String companyprofile_Insured_Currency = StringUtils.isNotEmpty(rb1.getString("companyprofile_Insured_Currency")) ? rb1.getString("companyprofile_Insured_Currency") : "Insured Currency";
    String companyprofile_Insured_Amount = StringUtils.isNotEmpty(rb1.getString("companyprofile_Insured_Amount")) ? rb1.getString("companyprofile_Insured_Amount") : "Insured Amount";
    String companyprofile_main = StringUtils.isNotEmpty(rb1.getString("companyprofile_main")) ? rb1.getString("companyprofile_main") : "Main business partners of the Company?";
    String companyprofile_income = StringUtils.isNotEmpty(rb1.getString("companyprofile_income")) ? rb1.getString("companyprofile_income") : "Income sources of the Company:";
    String companyprofile_Loans = StringUtils.isNotEmpty(rb1.getString("companyprofile_Loans")) ? rb1.getString("companyprofile_Loans") : "Loans";
    String companyprofile_economics = StringUtils.isNotEmpty(rb1.getString("companyprofile_economics")) ? rb1.getString("companyprofile_economics") : "Income from the economic activity";
    String companyprofile_Dividends = StringUtils.isNotEmpty(rb1.getString("companyprofile_Dividends")) ? rb1.getString("companyprofile_Dividends") : "Dividends / interest income";
    String companyprofile_Investments = StringUtils.isNotEmpty(rb1.getString("companyprofile_Investments")) ? rb1.getString("companyprofile_Investments") : "Investments";
    String companyprofile_Other = StringUtils.isNotEmpty(rb1.getString("companyprofile_Other")) ? rb1.getString("companyprofile_Other") : "Other";
    String companyprofile_Company_Profile1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Company_Profile1")) ? rb1.getString("companyprofile_Company_Profile1") : "Company Profile";
    String companyprofile_Profile1 = StringUtils.isNotEmpty(rb1.getString("companyprofile_Profile1")) ? rb1.getString("companyprofile_Profile1") : "Profile";
    String companyprofile_Company_details = StringUtils.isNotEmpty(rb1.getString("companyprofile_Company_details")) ? rb1.getString("companyprofile_Company_details") : "There is no details that has to be provided for this profile";

    AddressIdentificationVO identificationVO_company = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_business = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_euCompany = new AddressIdentificationVO();
    ContactDetailsVO contactDetailsVO_main = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_technical = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_billing = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_cbk = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_pci = new ContactDetailsVO();
    ValidationErrorList validationErrorList = null;

    Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
    Map<Boolean, Set<BankInputName>> fullPageViseValidationForStep = new HashMap<Boolean, Set<BankInputName>>();
    Set<BankInputName> companyProfileInputList=new HashSet<BankInputName>();
    Map<String, AddressIdentificationVO> addressIdentificationVOMap = new HashMap();
    Map<String, ContactDetailsVO> contactDetailsVOMap = new HashMap();

    if(request.getAttribute("fullValidationForStep")!=null)
    {

        fullValidationForStep= (Map<Integer, Map<Boolean, Set<BankInputName>>>) request.getAttribute("fullValidationForStep");
        if(functions.isValueNull(request.getParameter("currentPageNO")) && fullValidationForStep.containsKey(Integer.valueOf(request.getParameter("currentPageNO"))))
        {

            fullPageViseValidationForStep=fullValidationForStep.get(Integer.valueOf(request.getParameter("currentPageNO")));
            if(fullPageViseValidationForStep.containsKey(false))
                companyProfileInputList.addAll(fullPageViseValidationForStep.get(false));
            if(fullPageViseValidationForStep.containsKey(true))
                companyProfileInputList.addAll(fullPageViseValidationForStep.get(true));
        }
    }


    if(session.getAttribute("applicationManagerVO")!=null)
    {
        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
    }

    if(applicationManagerVO.getCompanyProfileVO() != null)
    {
        companyProfileVO = applicationManagerVO.getCompanyProfileVO();
        if(companyProfileVO.getCompanyProfile_addressVOMap()!=null && companyProfileVO.getCompanyProfile_addressVOMap().size() > 0)
        {
            identificationVO_company = companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY");
            identificationVO_business = companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS");
            identificationVO_euCompany = companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY");
            contactDetailsVO_main = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN");
            contactDetailsVO_billing = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING");
            contactDetailsVO_technical = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL");
            contactDetailsVO_cbk = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK");
            contactDetailsVO_pci = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI");
        }
    }

   /* if(companyProfileVO == null)
    {
        companyProfileVO = new CompanyProfileVO();
        addressIdentificationVOMap.put("COMPANY",identificationVO_company);
        addressIdentificationVOMap.put("BUSINESS", identificationVO_business);
        addressIdentificationVOMap.put("EU_COMPANY", identificationVO_euCompany);
        contactDetailsVOMap.put("MAIN", contactDetailsVO_main);
        contactDetailsVOMap.put("BILLING", contactDetailsVO_billing);
        contactDetailsVOMap.put("TECHNICAL", contactDetailsVO_technical);
        contactDetailsVOMap.put("CBK", contactDetailsVO_cbk);
        contactDetailsVOMap.put("PCI", contactDetailsVO_pci);
    }


    if(applicationManagerVO.getOwnershipProfileVO()==null)
    {
        ownershipProfileVO = new OwnershipProfileVO();
        ownershipProfileDetailsVOMap.put("SHAREHOLDER1",ownershipProfileDetailsVO_shareholder1);
        ownershipProfileDetailsVOMap.put("SHAREHOLDER2",ownershipProfileDetailsVO_shareholder2);
        ownershipProfileDetailsVOMap.put("SHAREHOLDER3",ownershipProfileDetailsVO_shareholder3);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER1",ownershipProfileDetailsVO_corporateShareholder1);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER2",ownershipProfileDetailsVO_corporateShareholder2);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER3",ownershipProfileDetailsVO_corporateShareholder3);
        ownershipProfileDetailsVOMap.put("DIRECTOR1",ownershipProfileDetailsVO_director1);
        ownershipProfileDetailsVOMap.put("DIRECTOR2",ownershipProfileDetailsVO_director2);
        ownershipProfileDetailsVOMap.put("DIRECTOR3",ownershipProfileDetailsVO_director3);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY1",ownershipProfileDetailsVO_authorizeSignatory1);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY2",ownershipProfileDetailsVO_authorizeSignatory2);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY3",ownershipProfileDetailsVO_authorizeSignatory3);
        ownershipProfileVO.setOwnershipProfileDetailsVOMap(ownershipProfileDetailsVOMap);
        applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
    }*/

    if(session.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");
    }
    else if(request.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");
    }
    else
    {
        validationErrorList= new ValidationErrorList();
    }
    if(functions.isValueNull(companyProfileVO.getCompanyRegisteredEU()) && "Y".equals(companyProfileVO.getCompanyRegisteredEU()))
    {
        selectedY="checked";
        selectedN="disabled";
    }
    else
    {
        selectedN="checked";
        disableEU="disabled";
        selectedY="disabled";
        bgcolor="background-color:#EBEBE4";
    }

%>
<script>
    $( document ).ready(function() {
        $('#billingtoo').next('.iCheck-helper').click(function(){
            if($('#billingtoo').prop("checked") == true){
                document.getElementById('corporatename').value = document.getElementById('merchantname').value;
                document.getElementById('corporatestreet').value = document.getElementById('locationaddress').value;
                document.getElementById('corporatecity').value = document.getElementById('merchantcity').value;
                document.getElementById('corporatestate').value = document.getElementById('merchantstate').value;
                document.getElementById('corporatecountry').value = document.getElementById('merchantcountry').value;
                document.getElementById('corporatezipcode').value = document.getElementById('merchantzipcode').value;

            }

        });
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        $("input[name='startup_business']").next('.iCheck-helper').on("click", function () {
            if ($('input:radio[name=startup_business]:checked').val()=="N")
            {
                document.myformname.company_lengthoftime_business.disabled = false;
            }
            else
            {
                document.myformname.company_lengthoftime_business.disabled = true;
                document.myformname.company_lengthoftime_business.value = "";
            }
        });


        $( "input[name='company_bankruptcy']").next('.iCheck-helper').on( "click", function() {
            if($('input:radio[name=company_bankruptcy]:checked').val()=="N")
            {
                document.myformname.company_bankruptcydate.disabled = true;
                document.myformname.company_bankruptcydate.value = "";
            }
            else
            {
                document.myformname.company_bankruptcydate.disabled = false;
            }
        });


        $( "input[name='iscompany_insured']").next('.iCheck-helper').on( "click", function() {
            if($('input:radio[name=iscompany_insured]:checked').val()=="Y")
            {
                document.myformname.insured_companyname.disabled = false;
                document.myformname.insured_currency.disabled = false;
                document.myformname.insured_amount.disabled = false;
            }
            else
            {
                document.myformname.insured_companyname.disabled = true;
                document.myformname.insured_companyname.value = "";
                document.myformname.insured_currency.disabled = true;
                document.myformname.insured_currency.value = "";
                document.myformname.insured_amount.disabled = true;
                document.myformname.insured_amount.value = "";
            }
        });
        $( "input[name='income_sources_other']").next('.iCheck-helper').on( "click", function() {
            if($('input:checkbox[name=income_sources_other]:checked').val()=="Y")
            {
                document.myformname.income_sources_other_yes.disabled = false;
            }
            else
            {
                document.myformname.income_sources_other_yes.disabled = true;
                document.myformname.income_sources_other_yes.value = "";
            }
        });

        //By default when user loads for 1st time
        if($('input:radio[name=iscompany_insured]:checked').val()=="Y")
        {
            document.myformname.insured_companyname.disabled = false;
            document.myformname.insured_currency.disabled = false;
            document.myformname.insured_amount.disabled = false;
        }
        else
        {
            document.myformname.insured_companyname.disabled = true;
            document.myformname.insured_companyname.value = "";
            document.myformname.insured_currency.disabled = true;
            document.myformname.insured_currency.value = "";
            document.myformname.insured_amount.disabled = true;
            document.myformname.insured_amount.value = "";
        }

        $( "input[name='iscompany_insured']").next('.iCheck-helper').on( "click", function() {
            if($('input:radio[name=iscompany_insured]:checked').val()=="Y")
            {
                document.myformname.insured_companyname.disabled = false;
                document.myformname.insured_currency.disabled = false;
                document.myformname.insured_amount.disabled = false;
            }
            else
            {
                document.myformname.insured_companyname.disabled = true;
                document.myformname.insured_companyname.value = "";
                document.myformname.insured_currency.disabled = true;
                document.myformname.insured_currency.value = "";
                document.myformname.insured_amount.disabled = true;
                document.myformname.insured_amount.value = "";
            }
        });
        $( "input[name='income_sources_other']").next('.iCheck-helper').on( "click", function() {
            if($('input:checkbox[name=income_sources_other]:checked').val()=="Y")
            {
                document.myformname.income_sources_other_yes.disabled = false;
            }
            else
            {
                document.myformname.income_sources_other_yes.disabled = true;
                document.myformname.income_sources_other_yes.value = "";
            }
        });

        $('#billingtoo').next('.iCheck-helper').click(function(){
            if($('#billingtoo').prop("checked") == true){

                document.getElementById('corporatename').value = document.getElementById('merchantname').value;
                document.getElementById('corporate_addressId').value = document.getElementById('merchant_addressId').value;
                document.getElementById('corporatecity').value = document.getElementById('merchantcity').value;
                document.getElementById('corporatestate').value = document.getElementById('merchantstate').value;
                document.getElementById('corporatecountry').value = document.getElementById('merchantcountry').value;
                document.getElementById('corporatezipcode').value = document.getElementById('merchantzipcode').value;
                document.getElementById('corporatestreet').value = document.getElementById('merchantstreet').value;
                document.getElementById('corporateaddress').value = document.getElementById('locationaddress').value;
                document.getElementById('corporate_addressproof').value = document.getElementById('merchant_addressproof').value;
            }

        });

        //On LOAD

        if($('input:radio[name=License_required]:checked').val()=="N")
        {
            document.getElementById('License_Permission').disabled = true;
            document.getElementById('License_Permission').disabled = true;
            $('input:radio[name=License_Permission]').each(function(){
                $(this).attr('disabled','disabled');
                $(this).parent('.iradio_square-aero').addClass('disabled');
            });
        }
        else
        {
            document.getElementById('License_Permission').disabled = false;
            document.getElementById('License_Permission').disabled = false;
            $('input:radio[name=License_Permission]').each(function(){
                $(this).removeAttr('disabled');
                $(this).parent('.iradio_square-aero').removeClass('disabled');
            });
        }


        //On CLICK

        $( "input[name='License_required']").next('.iCheck-helper').on( "click", function() {
            if($('input:radio[name=License_required]:checked').val()=="N")
            {
                document.getElementById('License_Permission').disabled = true;
                document.getElementById('License_Permission').disabled = true;
                $('input:radio[name=License_Permission]').each(function(){
                    $(this).attr('disabled','disabled');
                    $(this).parent('.iradio_square-aero').addClass('disabled');
                });
            }
            else
            {
                document.getElementById('License_Permission').disabled = false;
                document.getElementById('License_Permission').disabled = false;
                $('input:radio[name=License_Permission]').each(function(){
                    $(this).removeAttr('disabled');
                    $(this).parent('.iradio_square-aero').removeClass('disabled');
                });
            }
        });

        $('#eucompany').next('.iCheck-helper').click(function(){
            if($('#eucompany').prop("checked") == true){
                document.getElementById('registered_corporatename').value = document.getElementById('merchantname').value;
                document.getElementById('registered_directors_address').value = document.getElementById('locationaddress').value;
                document.getElementById('registered_directors_addressproof').value = document.getElementById('merchant_addressproof').value;
                document.getElementById('registered_directors_addressId').value = document.getElementById('merchant_addressId').value;
                document.getElementById('EURegistrationNumber').value = document.getElementById('companyregistrationnumber').value;
                document.getElementById('registered_directors_city').value = document.getElementById('merchantcity').value;
                document.getElementById('registered_directors_State').value = document.getElementById('merchantstate').value;
                document.getElementById('registered_directors_country').value = document.getElementById('merchantcountry').value;
                document.getElementById('registered_directors_street').value = document.getElementById('merchantstreet').value;
                document.getElementById('registered_directors_postalcode').value = document.getElementById('merchantzipcode').value;
            }

        });
    });

    function myjunk1(fromaction)
    {/*alert("change");*/

        if(document.getElementById([fromaction]).value!=null)
        {
            /*alert("inside func" +this.document.forms["myformname"][fromaction].value);*/
            var hat1 = document.getElementById([fromaction]).selectedIndex ;

            var hatto1 = document.getElementById([fromaction]).options[hat1].value;
            if (hatto1 != '')
            {
                //document.forms["myformname"][toa.value = hatto1.split("|")[0];
                //this.document.forms["myformname"][toaction].value = hatto1.split("|")[1];
                if( hatto1.split("|")[2]=="Y")
                {
                    document.getElementById("eucompany").checked = false;
                    document.getElementById("eucompany").disabled = true;
                    document.getElementById("registered_corporatename").disabled = true;
                    document.getElementById("registered_directors").disabled = true;
                    document.getElementById("EURegistrationNumber").disabled = true;
                    document.getElementById("registered_directors_country").disabled = true;
                    document.getElementById("registered_directors_address").disabled = true;
                    document.getElementById("registered_directors_city").disabled = true;
                    document.getElementById("registered_directors_State").disabled = true;
                    document.getElementById("registered_directors_postalcode").disabled = true;
                    document.getElementById("registered_directors_addressproof").disabled = true;
                    document.getElementById("registered_directors_addressId").disabled = true;
                    document.getElementById("registered_directors_street").disabled = true;
                    $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#EBEBE4');
                }
                else
                {
                    document.getElementById("eucompany").checked = true;
                    document.getElementById("eucompany").disabled = false;
                    document.getElementById("registered_corporatename").disabled = false;
                    document.getElementById("registered_directors").disabled = false;
                    document.getElementById("EURegistrationNumber").disabled = false;
                    document.getElementById("registered_directors_country").disabled = false;
                    document.getElementById("registered_directors_address").disabled = false;
                    document.getElementById("registered_directors_city").disabled = false;
                    document.getElementById("registered_directors_State").disabled = false;
                    document.getElementById("registered_directors_postalcode").disabled = false;
                    document.getElementById("registered_directors_addressproof").disabled = false;
                    document.getElementById("registered_directors_addressId").disabled = false;
                    document.getElementById("registered_directors_street").disabled = false;
                    $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#ffffff');
                }

                document.getElementById([fromaction]).options[hat1].selected=true
            }
            else
            {
                /*document.forms["myformname"][toaction].value = "";
                this.document.forms["myformname"][toaction].value = "";*/
            }
        }

    }
</script>

<body onload="myjunk1('merchantcountry');">
<div class="container-fluid " id="companyid_container">
    <%
        if(companyProfileInputList.contains(BankInputName.merchantname) || companyProfileInputList.contains(BankInputName.locationaddress) || companyProfileInputList.contains(BankInputName.merchantcity)||
                companyProfileInputList.contains(BankInputName.merchantstate)|| companyProfileInputList.contains(BankInputName.merchantcountry)|| companyProfileInputList.contains(BankInputName.merchantstreet)||
                companyProfileInputList.contains(BankInputName.merchantzipcode)|| companyProfileInputList.contains(BankInputName.Companyphonecc1)|| companyProfileInputList.contains(BankInputName.CompanyTelephoneNO)||
                companyProfileInputList.contains(BankInputName.CompanyFax) || companyProfileInputList.contains(BankInputName.CompanyEmailAddress)  || companyProfileInputList.contains(BankInputName.countryofregistration)  ||
                companyProfileInputList.contains(BankInputName.companyregistrationnumber)  || companyProfileInputList.contains(BankInputName.Company_Date_Registration)  || companyProfileInputList.contains(BankInputName.vatidentification)||
                companyProfileInputList.contains(BankInputName.FederalTaxID) || companyProfileInputList.contains(BankInputName.company_typeofbusiness)||
                companyProfileInputList.contains(BankInputName.corporatename) || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry) || companyProfileInputList.contains(BankInputName.corporatestreet) || companyProfileInputList.contains(BankInputName.corporatezipcode)  ||
                companyProfileInputList.contains(BankInputName.registered_corporatename)  || companyProfileInputList.contains(BankInputName.registered_directors)  || companyProfileInputList.contains(BankInputName.EURegistrationNumber) || companyProfileInputList.contains(BankInputName.registered_directors_country) || companyProfileInputList.contains(BankInputName.registered_directors_street) || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  ||
                companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode)||
                companyProfileInputList.contains(BankInputName.company_turnoverlastyear_unit)  ||companyProfileInputList.contains(BankInputName.company_currencylastyear)  || companyProfileInputList.contains(BankInputName.startup_business) ||companyProfileInputList.contains(BankInputName.company_lengthoftime_business) || companyProfileInputList.contains(BankInputName.company_numberofemployees) ||
                companyProfileInputList.contains(BankInputName.contactname)   || companyProfileInputList.contains(BankInputName.contactemailaddress)   || companyProfileInputList.contains(BankInputName.contactname_telnocc1)  || companyProfileInputList.contains(BankInputName.contactname_telephonenumber)  ||
                companyProfileInputList.contains(BankInputName.SkypeIMaddress)  || companyProfileInputList.contains(BankInputName.contact_designation)  ||
                companyProfileInputList.contains(BankInputName.technicalcontactname)  || companyProfileInputList.contains(BankInputName.technicalemailaddress)  || companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||  companyProfileInputList.contains(BankInputName.Technical_telephonenumber) || companyProfileInputList.contains(BankInputName.technical_designation)  ||
                companyProfileInputList.contains(BankInputName.billingcontactname)   || companyProfileInputList.contains(BankInputName.billingemailaddress)  || companyProfileInputList.contains(BankInputName.financialphonecc1) || companyProfileInputList.contains(BankInputName.Financial_telephonenumber)|| companyProfileInputList.contains(BankInputName.billing_designation)||
                companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) ||companyProfileInputList.contains(BankInputName.legal_proceeding)|| companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||
                companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) ||companyProfileInputList.contains(BankInputName.legal_proceeding)||
                companyProfileInputList.contains(BankInputName.cbk_contactperson) || companyProfileInputList.contains(BankInputName.cbk_email) ||companyProfileInputList.contains(BankInputName.cbk_telephonenumber)  ||companyProfileInputList.contains(BankInputName.cbk_phonecc) || companyProfileInputList.contains(BankInputName.cbk_designation) ||
                companyProfileInputList.contains(BankInputName.pci_contactperson) || companyProfileInputList.contains(BankInputName.pci_email) ||companyProfileInputList.contains(BankInputName.pci_telephonenumber)  ||companyProfileInputList.contains(BankInputName.pci_phonecc) || companyProfileInputList.contains(BankInputName.pci_designation) ||
                companyProfileInputList.contains(BankInputName.iscompany_insured)|| companyProfileInputList.contains(BankInputName.insured_companyname) ||
                companyProfileInputList.contains(BankInputName.main_business_partner)||
                companyProfileInputList.contains(BankInputName.loans) || companyProfileInputList.contains(BankInputName.income_economic_activity) || companyProfileInputList.contains(BankInputName.interest_income) || companyProfileInputList.contains(BankInputName.investments) || companyProfileInputList.contains(BankInputName.income_sources_other) || companyProfileInputList.contains(BankInputName.income_sources_other_yes) || view)
        {
    %>


<div class="content-page" id="companyid">
    <div class="content" id="companycontent">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=companyprofile_Welcome%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <div class="bg-info "style="text-align: center;font-size:16px;/*color: #31708f;*/">
                                   <%=companyprofile_thank%>
                                    <br> <%=companyprofile_short%><br>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        <%=companyprofile_step_company%>
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        <%=companyprofile_step_ownership%>
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        <%=companyprofile_step_business%>
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        <%=companyprofile_step_bank%>
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                       <%=companyprofile_step_cardholder%>
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        <%=companyprofile_step_upload%>
                                    </div>
                                    <br>
                                  <br>
                                    <br>
                                    <br>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=companyprofile_Company_Profile%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <center><h4 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():companyprofile_please%></h4></center>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantname) || companyProfileInputList.contains(BankInputName.locationaddress) || companyProfileInputList.contains(BankInputName.merchantcity)||companyProfileInputList.contains(BankInputName.merchantstate)|| companyProfileInputList.contains(BankInputName.merchantcountry)||companyProfileInputList.contains(BankInputName.merchantzipcode)|| companyProfileInputList.contains(BankInputName.Companyphonecc1)|| companyProfileInputList.contains(BankInputName.CompanyTelephoneNO)|| companyProfileInputList.contains(BankInputName.CompanyFax) || companyProfileInputList.contains(BankInputName.CompanyEmailAddress)  || companyProfileInputList.contains(BankInputName.countryofregistration)  || companyProfileInputList.contains(BankInputName.companyregistrationnumber)  || companyProfileInputList.contains(BankInputName.Company_Date_Registration)  || companyProfileInputList.contains(BankInputName.vatidentification)|| companyProfileInputList.contains(BankInputName.FederalTaxID)  || companyProfileInputList.contains(BankInputName.company_typeofbusiness) || companyProfileInputList.contains(BankInputName.merchantstreet) || companyProfileInputList.contains(BankInputName.merchant_addressproof) || companyProfileInputList.contains(BankInputName.merchant_addressId)|| view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=companyprofile_registered%></h2>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantname) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Name%></label>
                                    <input  type="text" class="form-control" id="merchantname" name="merchantname" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getCompany_name())) ? identificationVO_company.getCompany_name():""%>" /><%if(validationErrorList.getError("merchantname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.companyregistrationnumber)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="companyregistrationnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Registration_Number%></label>
                                    <input type="text" class="form-control"  id="companyregistrationnumber" name="companyregistrationnumber" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_company.getRegistration_number())) ? identificationVO_company.getRegistration_number():""%>" /><%if(validationErrorList.getError("companyregistrationnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.Company_Date_Registration)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Date_Registration%></label>
                                    <input type="text" name="Company_Date_Registration" class="form-control datepicker" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%= (functions.isValueNull(identificationVO_company.getDate_of_registration())) ? appFunctionUtil.convertTimestampToDatepicker(identificationVO_company.getDate_of_registration()):""%>"/><%if(validationErrorList.getError("Company_Date_Registration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.Companyphonecc1)  || view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="Companyphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country_Code%></label>
                                    <input type="text" class="form-control"  id="Companyphonecc1" name="Companyphonecc1" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getPhone_cc())) ? identificationVO_company.getPhone_cc():""%>"/><%if(validationErrorList.getError("Companyphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.CompanyTelephoneNO)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="CompanyTelephoneNO" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=companyprofile_Phone_No%></label>
                                    <input type="text" class="form-control"   id="CompanyTelephoneNO" name="CompanyTelephoneNO" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=(functions.isValueNull(identificationVO_company.getPhone_number())) ? identificationVO_company.getPhone_number():""%>" /><%if(validationErrorList.getError("CompanyTelephoneNO")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.CompanyFax)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="CompanyFax" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Fax%></label>
                                    <input type="text" class="form-control"  id="CompanyFax" name="CompanyFax" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getFax())) ? identificationVO_company.getFax():""%>" /><%if(validationErrorList.getError("CompanyFax")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.CompanyEmailAddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="CompanyEmailAddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Email_Address%></label>
                                    <input type="text" class="form-control"   id="CompanyEmailAddress" name="CompanyEmailAddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getEmail_id())) ? identificationVO_company.getEmail_id():""%>" /><%if(validationErrorList.getError("CompanyEmailAddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.vatidentification)||companyProfileInputList.contains(BankInputName.FederalTaxID)  || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=companyprofile_Identification_Details%></h2>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.vatidentification) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="vatidentification" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_VAT_Identification%></label>
                                    <input type="text" class="form-control"  id="vatidentification" name="vatidentification" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getVatidentification()))?identificationVO_company.getVatidentification():""%>" /><%if(validationErrorList.getError("vatidentification")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.FederalTaxID)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="FederalTaxID" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_tic%></label>
                                    <input type="text" class="form-control"  id="FederalTaxID" name="FederalTaxID" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getFederalTaxId()) )?identificationVO_company.getFederalTaxId():""%>" /><%if(validationErrorList.getError("FederalTaxID")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=companyprofile_Address_Details%></h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchant_addressproof)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchant_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Address_Proof%></label>
                                    <select class="form-control" name="merchant_addressproof" id="merchant_addressproof" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getAddressProofTypes((functions.isValueNull(identificationVO_company.getAddressProof())) ? identificationVO_company.getAddressProof() : ""));
                                        %>
                                    </select>
                                    <%--<%if(validationErrorList.getError("merchant_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>--%>
                                    <%if(validationErrorList.getError("merchant_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchant_addressId)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchant_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Address_ID%></label>
                                    <input type="text" class="form-control"  id="merchant_addressId" name="merchant_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getAddressId())) ? identificationVO_company.getAddressId():""%>"/><%if(validationErrorList.getError("merchant_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.locationaddress) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="locationaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Registration%></label>
                                    <input type="text" class="form-control"  id="locationaddress" name="locationaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getAddress())) ? StringEscapeUtils.escapeHtml(identificationVO_company.getAddress()):""%>"/><%if(validationErrorList.getError("locationaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantstreet)  || view )
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantstreet" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Street%></label>
                                    <input type="text" class="form-control"  id="merchantstreet" name="merchantstreet" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getStreet())) ? identificationVO_company.getStreet():""%>" /><%if(validationErrorList.getError("merchantstreet")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantcity)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantcity" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_city%></label>
                                    <input type="text" class="form-control"   id="merchantcity" name="merchantcity"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getCity())) ? StringEscapeUtils.escapeHtml(identificationVO_company.getCity()):""%>"/><%if(validationErrorList.getError("merchantcity")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantstate)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantstate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_state%></label>
                                    <input type="text" class="form-control"  id="merchantstate" name="merchantstate"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getState())) ? StringEscapeUtils.escapeHtml(identificationVO_company.getState()):""%>" /><%if(validationErrorList.getError("merchantstate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantcountry) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback ">
                                    <label for="merchantcountry" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country%></label>
                                    <select  class="form-control" id="merchantcountry" name="merchantcountry" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  onchange="mycountrycode('merchantcountry','Companyphonecc1');">
                                        <%=AppFunctionUtil.getCountryDetails((functions.isValueNull(identificationVO_company.getCountry())) ? identificationVO_company.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("merchantcountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>

                                </div>
                                <%
                                    }
                                %>
                                <%--<%
                                    if(companyProfileInputList.contains(BankInputName.countryofregistration)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="countryofregistration" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country of Registration*</label>
                                    <select  class="form-control" id="countryofregistration" name="countryofregistration" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>>
                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(companyProfileVO.getCountryOfRegistration())?companyProfileVO.getCountryOfRegistration():"")%>
                                    </select><%if(validationErrorList.getError("countryofregistration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>--%>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantzipcode)  || view )
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantzipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_zip%></label>
                                    <input type="text" class="form-control"  id="merchantzipcode" name="merchantzipcode" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getZipcode())) ? identificationVO_company.getZipcode():""%>" /><%if(validationErrorList.getError("merchantzipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_typeofbusiness)  || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label for="FederalTaxID" style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=companyprofile_type_business%></u></label>
                                </div>
                                <div class="col-md-12" style="padding: 0;">
                                    <div class="form-group col-md-4 col-lg-3 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio"   name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="Corporation" <%="Corporation".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                                            <%=companyprofile_Corporation%></label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio"  name="company_typeofbusiness" style="width:20%;" <%=globaldisabled%> value="LimitedLiabilityCompany" <%="LimitedLiabilityCompany".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                                            <%=companyprofile_Limited_Liability%></label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;padding-left:0;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="SoleProprietor"<%="SoleProprietor".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                                            <%=companyprofile_Sole%>&nbsp;<%=companyprofile_Proprietor%></label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="Partnership" <%="Partnership".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                                            <%=companyprofile_Partnership%></label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="NotforProfit" <%="NotforProfit".equals(companyProfileVO.getCompanyTypeOfBusiness()) || !functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ?"checked":""%>  />
                                            <%=companyprofile_Nonprofit_Organization%></label>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <div class="form-group col-md-12"></div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.startup_business) || companyProfileInputList.contains(BankInputName.company_lengthoftime_business) || view)
                                    {
                                        String startupBusinessDisabled = "";
                                        if("Y".equals(companyProfileVO.getStartup_business()) || !functions.isValueNull(companyProfileVO.getStartup_business()))
                                        {
                                            startupBusinessDisabled = "disabled";
                                        }
                                        if("disabled".equals(globaldisabled)){
                                            startupBusinessDisabled = "disabled";
                                        }
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-3 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Startup%></label>
                                    <div class="col-md-2">
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%>  id="startup_business" name="startup_business"  value="Y" <%="Y".equals(companyProfileVO.getStartup_business())  || !functions.isValueNull(companyProfileVO.getStartup_business()) ?"checked":""%>/><%--<%if(validationErrorList.getError("startup_business")!=null){%><span class="apperrormsg">Invalid Startup business</span><%}%>--%>
                                       <%=companyprofile_Yes%>&nbsp;
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="startup_business" value="N"  <%="N".equals(companyProfileVO.getStartup_business())?"checked":""%> />
                                        <%=companyprofile_No%>
                                    </div>
                                    <div class="form-group col-md-7" style="margin-bottom: 0;">
                                        <label class="col-md-6 control-label" style="padding-left: initial;padding-right:0;"><%=companyprofile_long%></label>
                                        <div class="col-md-6" style="padding-left: 0;">
                                            <input type="text" class="form-control" placeholder="Example: 2 Years, 9 Months" id="company_lengthoftime_business" name="company_lengthoftime_business" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=startupBusinessDisabled%> value="<%=functions.isValueNull(companyProfileVO.getCompanyLengthOfTimeInBusiness())==true?companyProfileVO.getCompanyLengthOfTimeInBusiness():""%>" name="company_lengthoftime_business" <%=!functions.isValueNull(companyProfileVO.getStartup_business()) && "Y".equals(companyProfileVO.getStartup_business())?"disabled":""%> /><%if(validationErrorList.getError("company_lengthoftime_business")!=null){%><%--<span class="apperrormsg">Invalid Company length of time Business</span>--%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </div>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatename)    || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry)  || companyProfileInputList.contains(BankInputName.corporatezipcode) || companyProfileInputList.contains(BankInputName.corporatestreet) || view)
                                    {
                                %>
                                <%--<div class="col-md-12" style="padding: 0;">--%>
                                <div class="form-group col-md-12">
                                    <label class="checkbox-inline" id="view_css" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                        <input type="checkbox" id="billingtoo" name="billingtoo" style="float: none;margin-left:0;" <%=globaldisabled%> <%--onclick="FillBilling(this.form)"--%>>
                                        <%=companyprofile_want%> <b><%=companyprofile_Business_Information%></b> <%=companyprofile_Select%> <b><%=companyprofile_Checkbox%></b></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_corporatename)  || companyProfileInputList.contains(BankInputName.registered_directors)  || companyProfileInputList.contains(BankInputName.EURegistrationNumber) || companyProfileInputList.contains(BankInputName.registered_directors_country)  || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  || companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode) || companyProfileInputList.contains(BankInputName.registered_directors_street) || view)
                                    {
                                %>
                                <div class="form-group col-md-6 col-lg-6">
                                    <label class="checkbox-inline" id="view_css" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                        <input type="checkbox" id="eucompany" name="eucompany" style="float: none;margin-left:0;" <%=globaldisabled%> <%--onclick="eucompanydetails(this.form)"--%>>
                                        <%=companyprofile_want%> <b><%=companyprofile_EU_Company%></b>, <%=companyprofile_Select%> <b><%=companyprofile_Checkbox%></b></label>
                                </div>
                                <%
                                        }
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatename)||companyProfileInputList.contains(BankInputName.corporate_addressproof) ||companyProfileInputList.contains(BankInputName.corporate_addressId) || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry)  || companyProfileInputList.contains(BankInputName.corporatezipcode) || companyProfileInputList.contains(BankInputName.corporatestreet) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=companyprofile_Business_Information%></h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatename)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_business%></label>
                                    <input type="text" class="form-control"   id="corporatename" name="corporatename" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_business.getCompany_name())) ? identificationVO_business.getCompany_name():""%>"/><%if(validationErrorList.getError("corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporate_addressproof) ||companyProfileInputList.contains(BankInputName.corporate_addressId) || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry)  || companyProfileInputList.contains(BankInputName.corporatezipcode) || companyProfileInputList.contains(BankInputName.corporatestreet) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=companyprofile_Address_Details%></h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporate_addressproof)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporate_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_AddressProof%></label>
                                    <select class="form-control" name="corporate_addressproof" id="corporate_addressproof" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getAddressProofTypes(( functions.isValueNull(identificationVO_business.getAddressProof())) ? identificationVO_business.getAddressProof() : ""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("corporate_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporate_addressId) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporate_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_AddressID%></label>
                                    <input type="text" class="form-control"  id="corporate_addressId" name="corporate_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%= ( functions.isValueNull(identificationVO_business.getAddressId())) ? identificationVO_business.getAddressId():""%>"/><%if(validationErrorList.getError("corporate_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporateaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporateaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Billing_Address%></label>
                                    <input type="text" class="form-control"   id="corporateaddress" name="corporateaddress" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getAddress()))?StringEscapeUtils.escapeHtml(identificationVO_business.getAddress()):""%>" /><%if(validationErrorList.getError("corporateaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatestreet)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatestreet" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Street%></label>
                                    <input type="text" class="form-control"   id="corporatestreet" <%=globaldisabled%> name="corporatestreet" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getStreet()))?identificationVO_business.getStreet():""%>" /><%if(validationErrorList.getError("corporatestreet")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatecity)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatecity" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_city%></label>
                                    <input type="text" class="form-control" id="corporatecity" name="corporatecity" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getCity()))?StringEscapeUtils.escapeHtml(identificationVO_business.getCity()):""%>"/><%if(validationErrorList.getError("corporatecity")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatestate)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatestate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_state%></label>
                                    <input type="text" class="form-control"   id="corporatestate" name="corporatestate" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getState()))?StringEscapeUtils.escapeHtml(identificationVO_business.getState()):""%>" /><%if(validationErrorList.getError("corporatestate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatecountry)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country%></label>
                                    <select  class="form-control" id="corporatecountry" name="corporatecountry" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  >
                                        <%=AppFunctionUtil.getCountryDetails((functions.isValueNull(identificationVO_business.getCountry()))?identificationVO_business.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("corporatecountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>

                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatezipcode)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatezipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Zip_Code%></label>
                                    <input type="text" class="form-control"   id="corporatezipcode" <%=globaldisabled%> name="corporatezipcode" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getZipcode())) ? identificationVO_business.getZipcode():""%>" /><%if(validationErrorList.getError("corporatezipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_corporatename)  || companyProfileInputList.contains(BankInputName.registered_directors)  || companyProfileInputList.contains(BankInputName.EURegistrationNumber) || companyProfileInputList.contains(BankInputName.registered_directors_country)  || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  || companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode) || companyProfileInputList.contains(BankInputName.registered_directors_street) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=companyprofile_EU_company%></h2>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_corporatename)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Registered_Corporate%></label>
                                    <input type="text" class="form-control"  <%=globaldisabled%> id="registered_corporatename" name="registered_corporatename" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getCompany_name())) ? identificationVO_euCompany.getCompany_name():""%>" /><%if(validationErrorList.getError("registered_corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_addressproof)  || companyProfileInputList.contains(BankInputName.registered_directors_addressId)  || companyProfileInputList.contains(BankInputName.registered_directors_country)  || companyProfileInputList.contains(BankInputName.registered_directors_street) || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  || companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode)  || view)
                                    {
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Registered_Directors%></label>
                                    <input type="text" class="form-control"  <%=globaldisabled%> id="registered_directors" name="registered_directors" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getRegistred_directors())) ? identificationVO_euCompany.getRegistred_directors():""%>"/><%if(validationErrorList.getError("registered_directors")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.EURegistrationNumber) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="EURegistrationNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Registration_Number1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="EURegistrationNumber" name="EURegistrationNumber" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getRegistration_number())) ? identificationVO_euCompany.getRegistration_number():""%>" /><%if(validationErrorList.getError("EURegistrationNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=companyprofile_Address_Details%></h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_addressproof)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_AddressProof%></label>
                                    <select class="form-control" name="registered_directors_addressproof" <%=disableEU%> id="registered_directors_addressproof" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getAddressProofTypes((functions.isValueNull(identificationVO_euCompany.getAddressProof())) ? identificationVO_euCompany.getAddressProof() : ""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("registered_directors_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_addressId)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_AddressID%></label>
                                    <input type="text" class="form-control" <%=disableEU%> id="registered_directors_addressId" name="registered_directors_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_euCompany.getAddressId())) ? identificationVO_euCompany.getAddressId():""%>"/><%if(validationErrorList.getError("registered_directors_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_country)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country1%></label>
                                    <select  name="registered_directors_country" id="registered_directors_country" class="form-control" <%=globaldisabled%> <%=bgcolor%> <%=disableEU%> <%=globaldisabled%>>
                                        <%=AppFunctionUtil.getCountryDetailsEU((functions.isValueNull(identificationVO_euCompany.getCountry())) ? identificationVO_euCompany.getCountry() : "")%>
                                    </select>  <%if(validationErrorList.getError("registered_directors_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>

                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_street) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Street1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="registered_directors_street" name="registered_directors_street" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getStreet()))?identificationVO_euCompany.getStreet():""%>" /><%if(validationErrorList.getError("registered_directors_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_address) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Registered_Office_Address%></label>
                                    <input type="text" class="form-control"   id="registered_directors_address" name="registered_directors_address" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold"  <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getAddress()))?StringEscapeUtils.escapeHtml(identificationVO_euCompany.getAddress()):""%>" /><%if(validationErrorList.getError("registered_directors_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_city)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_city%></label>
                                    <input type="text" class="form-control"   id="registered_directors_city" <%=globaldisabled%> name="registered_directors_city" style="border: 1px solid #b2b2b2;font-weight:bold" <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getCity()))?StringEscapeUtils.escapeHtml(identificationVO_euCompany.getCity()):""%>"/><%if(validationErrorList.getError("registered_directors_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_State )  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_state%></label>
                                    <input type="text" class="form-control"   id="registered_directors_State" <%=globaldisabled%> name="registered_directors_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getState()))?StringEscapeUtils.escapeHtml(identificationVO_euCompany.getState()):""%>" /><%if(validationErrorList.getError("registered_directors_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_postalcode)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_postalcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_zip%></label>
                                    <input type="text" class="form-control"   id="registered_directors_postalcode" <%=globaldisabled%> name="registered_directors_postalcode" style="border: 1px solid #b2b2b2;font-weight:bold" <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getZipcode()))?identificationVO_euCompany.getZipcode():""%>"/><%if(validationErrorList.getError("registered_directors_postalcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                        }
                                    }
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=companyprofile_Other_Information%></h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_turnoverlastyear_unit)  ||companyProfileInputList.contains(BankInputName.company_currencylastyear)  || companyProfileInputList.contains(BankInputName.company_numberofemployees)   || companyProfileInputList.contains(BankInputName.contactname)   || companyProfileInputList.contains(BankInputName.contactemailaddress)   || companyProfileInputList.contains(BankInputName.contactname_telnocc1)  || companyProfileInputList.contains(BankInputName.contactname_telephonenumber)  ||  companyProfileInputList.contains(BankInputName.SkypeIMaddress)  || companyProfileInputList.contains(BankInputName.technicalcontactname)  || companyProfileInputList.contains(BankInputName.technicalemailaddress)  || companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||  companyProfileInputList.contains(BankInputName.Technical_telephonenumber)  || companyProfileInputList.contains(BankInputName.billingcontactname)   || companyProfileInputList.contains(BankInputName.billingemailaddress)  || companyProfileInputList.contains(BankInputName.financialphonecc1) || companyProfileInputList.contains(BankInputName.Financial_telephonenumber)|| companyProfileInputList.contains(BankInputName.contact_designation) ||
                                            companyProfileInputList.contains(BankInputName.technical_designation) || companyProfileInputList.contains(BankInputName.billing_designation) || companyProfileInputList.contains(BankInputName.cbk_designation) || companyProfileInputList.contains(BankInputName.pci_designation) || view)
                                    {
                                %>


                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_turnoverlastyear_unit)  || view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label for="company_turnoverlastyear_unit" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Turnover_last%></label>
                                    <select name="company_turnoverlastyear_unit" id="company_turnoverlastyear_unit" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getCurrencyUnit(functions.isValueNull(companyProfileVO.getCompany_turnoverlastyear_unit())?companyProfileVO.getCompany_turnoverlastyear_unit():""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("company_turnoverlastyear_unit")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="company_turnoverlastyear" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Amount%></label>
                                    <input type="text" class="form-control" id="company_turnoverlastyear" name="company_turnoverlastyear" <%=globaldisabled%> value="<%=functions.isValueNull(companyProfileVO.getCompanyTurnoverLastYear())==true?companyProfileVO.getCompanyTurnoverLastYear():""%>"/><%if(validationErrorList.getError("company_turnoverlastyear")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_currencylastyear)  || view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label for="company_turnoverlastyear_unit" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Capital_Resources%></label>
                                    <select name="company_currencylastyear"  class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getCurrencyUnit(functions.isValueNull(companyProfileVO.getCompany_currencylastyear())==true?companyProfileVO.getCompany_currencylastyear():""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("company_currencylastyear")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="company_capitalresources" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Amount1%></label>
                                    <input type="text" class="form-control" id="company_capitalresources" <%=globaldisabled%> name="company_capitalresources"  value="<%=functions.isValueNull(companyProfileVO.getCompanyCapitalResources())==true?companyProfileVO.getCompanyCapitalResources():""%>"/><%if(validationErrorList.getError("company_capitalresources")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_numberofemployees)   || view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="company_numberofemployees" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_No_Employees%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="company_numberofemployees" name="company_numberofemployees"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyNumberOfEmployees())==true?companyProfileVO.getCompanyNumberOfEmployees():""%>"/><%if(validationErrorList.getError("company_numberofemployees")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactname)   ||companyProfileInputList.contains(BankInputName.contactemailaddress)   ||companyProfileInputList.contains(BankInputName.contactname_telnocc1)   ||companyProfileInputList.contains(BankInputName.contactname_telephonenumber)   ||companyProfileInputList.contains(BankInputName.SkypeIMaddress) ||companyProfileInputList.contains(BankInputName.contact_designation) || view)
                                    {
                                %>

                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=companyprofile_contact%> </u></label>
                                </div>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactname)   || view)
                                    {
                                %>

                                <div class="form-group col-md-4 has-feedback">
                                    <label for="contactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_relationship%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contactname" name="contactname" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_main.getName())) ? contactDetailsVO_main.getName():""%>"/><%if(validationErrorList.getError("contactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactemailaddress)   || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="contactemailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_EmailAddress%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contactemailaddress" name="contactemailaddress" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_main.getEmailaddress())) ? contactDetailsVO_main.getEmailaddress():""%>" /><%if(validationErrorList.getError("contactemailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactname_telnocc1)  || companyProfileInputList.contains(BankInputName.contactname_telephonenumber)  ||  view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="contactname_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country_Code1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="contactname_telnocc1" name="contactname_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_main.getPhonecc1())) ? contactDetailsVO_main.getPhonecc1():""%>"/> <%if(validationErrorList.getError("contactname_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="contactname_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Phone_No1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contactname_telephonenumber" name="contactname_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_main.getTelephonenumber())) ? contactDetailsVO_main.getTelephonenumber():""%>" /><%if(validationErrorList.getError("contactname_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.contact_designation)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="contact_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Designation%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contact_designation" name="contact_designation" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_main.getDesignation())) ? contactDetailsVO_main.getDesignation():""%>" /><%if(validationErrorList.getError("contact_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.SkypeIMaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="SkypeIMaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Skpe%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="SkypeIMaddress" name="SkypeIMaddress" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_main.getSkypeIMaddress())) ? contactDetailsVO_main.getSkypeIMaddress():""%>" /><%if(validationErrorList.getError("SkypeIMaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalcontactname)  ||companyProfileInputList.contains(BankInputName.technicalemailaddress)  ||companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||companyProfileInputList.contains(BankInputName.Technical_telephonenumber) || companyProfileInputList.contains(BankInputName.technical_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=companyprofile_Technical_Contact%> </u></label>
                                </div>
                                <%-- <%
                                     }
                                 %>--%>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalcontactname)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="technicalcontactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_relationship%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technicalcontactname" name="technicalcontactname" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getName())) ? contactDetailsVO_technical.getName():""%>" /><%if(validationErrorList.getError("technicalcontactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalemailaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="technicalemailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_EmailAddress%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technicalemailaddress" name="technicalemailaddress" style="border: 1px solid #b2b2b2;font-weight:bold"   value="<%=(functions.isValueNull(contactDetailsVO_technical.getEmailaddress())) ? contactDetailsVO_technical.getEmailaddress():""%>" /><%if(validationErrorList.getError("technicalemailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||  companyProfileInputList.contains(BankInputName.Technical_telephonenumber)  || view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="technicalphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country_Code1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technicalphonecc1" name="technicalphonecc1"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getPhonecc1())) ? contactDetailsVO_technical.getPhonecc1():""%>"/> <%if(validationErrorList.getError("technicalphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="Technical_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Phone_No1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="Technical_telephonenumber" name="Technical_telephonenumber" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getTelephonenumber())) ? contactDetailsVO_technical.getTelephonenumber():""%>" /><%if(validationErrorList.getError("Technical_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.technical_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="technical_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Designation%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technical_designation" name="technical_designation"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getDesignation())) ? contactDetailsVO_technical.getDesignation():""%>"/> <%if(validationErrorList.getError("technical_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.billingcontactname)  ||companyProfileInputList.contains(BankInputName.billingemailaddress)  ||companyProfileInputList.contains(BankInputName.financialphonecc1)  ||companyProfileInputList.contains(BankInputName.Financial_telephonenumber) || companyProfileInputList.contains(BankInputName.billing_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=companyprofile_Financial%> </u></label>
                                </div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.billingcontactname) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="billingcontactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_relationship%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="billingcontactname" name="billingcontactname"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_billing.getName())) ? contactDetailsVO_billing.getName():""%>" /><%if(validationErrorList.getError("billingcontactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.billingemailaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="billingemailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_EmailAddress%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="billingemailaddress" name="billingemailaddress"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_billing.getEmailaddress())) ? contactDetailsVO_billing.getEmailaddress():""%>" /><%if(validationErrorList.getError("billingemailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.financialphonecc1) || companyProfileInputList.contains(BankInputName.Financial_telephonenumber)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="financialphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country_Code1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="financialphonecc1" name="financialphonecc1"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_billing.getPhonecc1())) ? contactDetailsVO_billing.getPhonecc1():""%>"/> <%if(validationErrorList.getError("financialphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="Financial_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Phone_No1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="Financial_telephonenumber" name="Financial_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_billing.getTelephonenumber())) ? contactDetailsVO_billing.getTelephonenumber():""%>" /><%if(validationErrorList.getError("Financial_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.billing_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="billing_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Designation%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="billing_designation" name="billing_designation"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_billing.getDesignation())) ? contactDetailsVO_billing.getDesignation():""%>"/> <%if(validationErrorList.getError("billing_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.cbk_contactperson) || companyProfileInputList.contains(BankInputName.cbk_email) ||companyProfileInputList.contains(BankInputName.cbk_telephonenumber) ||companyProfileInputList.contains(BankInputName.cbk_phonecc) ||companyProfileInputList.contains(BankInputName.cbk_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=companyprofile_chargeback%> </u></label>
                                </div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.cbk_contactperson) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="cbk_contactperson" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_relationship%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_contactperson" name="cbk_contactperson"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getName())) ? contactDetailsVO_cbk.getName():""%>" /><%if(validationErrorList.getError("cbk_contactperson")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.cbk_email) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="cbk_email" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_EmailAddress%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_email" name="cbk_email"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getEmailaddress())) ? contactDetailsVO_cbk.getEmailaddress():""%>" /><%if(validationErrorList.getError("cbk_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.cbk_telephonenumber) ||companyProfileInputList.contains(BankInputName.cbk_phonecc)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="cbk_phonecc" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country_Code1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_phonecc" name="cbk_phonecc"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getPhonecc1())) ? contactDetailsVO_cbk.getPhonecc1():""%>"/> <%if(validationErrorList.getError("cbk_phonecc")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="cbk_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Phone_No1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_telephonenumber" name="cbk_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_cbk.getTelephonenumber())) ? contactDetailsVO_cbk.getTelephonenumber():""%>" /><%if(validationErrorList.getError("cbk_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.cbk_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="cbk_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Designation%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_designation" name="cbk_designation"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getDesignation())) ? contactDetailsVO_cbk.getDesignation():""%>"/> <%if(validationErrorList.getError("cbk_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                        }
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.pci_contactperson) || companyProfileInputList.contains(BankInputName.pci_email) ||companyProfileInputList.contains(BankInputName.pci_telephonenumber) ||companyProfileInputList.contains(BankInputName.pci_phonecc) ||companyProfileInputList.contains(BankInputName.pci_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=companyprofile_PCI_Contacts%> </u></label>
                                </div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.pci_contactperson) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="pci_contactperson" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_relationship%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_contactperson" name="pci_contactperson"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getName())) ? contactDetailsVO_pci.getName():""%>" /><%if(validationErrorList.getError("pci_contactperson")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.pci_email) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="pci_email" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_EmailAddress%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_email" name="pci_email"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getEmailaddress())) ? contactDetailsVO_pci.getEmailaddress():""%>" /><%if(validationErrorList.getError("pci_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.pci_telephonenumber) ||companyProfileInputList.contains(BankInputName.pci_phonecc)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="pci_phonecc" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Country_Code1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_phonecc" name="pci_phonecc"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getPhonecc1())) ? contactDetailsVO_pci.getPhonecc1():""%>"/> <%if(validationErrorList.getError("pci_phonecc")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="pci_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Phone_No1%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_telephonenumber" name="pci_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_pci.getTelephonenumber())) ? contactDetailsVO_pci.getTelephonenumber():""%>" /><%if(validationErrorList.getError("pci_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.pci_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="pci_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Designation%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_designation" name="pci_designation"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getDesignation())) ? contactDetailsVO_pci.getDesignation():""%>"/> <%if(validationErrorList.getError("pci_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                            }
                                        }
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) ||companyProfileInputList.contains(BankInputName.legal_proceeding)|| companyProfileInputList.contains(BankInputName.iscompany_insured)|| companyProfileInputList.contains(BankInputName.insured_companyname)|| companyProfileInputList.contains(BankInputName.main_business_partner)||
                                            companyProfileInputList.contains(BankInputName.loans)|| companyProfileInputList.contains(BankInputName.income_economic_activity)|| companyProfileInputList.contains(BankInputName.interest_income)|| companyProfileInputList.contains(BankInputName.investments)|| companyProfileInputList.contains(BankInputName.income_sources_other)|| companyProfileInputList.contains(BankInputName.income_sources_other_yes) || view)
                                    {
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||   view )
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=companyprofile_have%></label>
                                    <div class="col-md-2">
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="company_bankruptcy" value="Y" <%="Y".equals(companyProfileVO.getCompanyBankruptcy())?"checked":""%>/>
                                        <%=companyprofile_Yes%>&nbsp;
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="company_bankruptcy" value="N"  <%=!functions.isValueNull(companyProfileVO.getCompanyBankruptcy())||"N".equals(companyProfileVO.getCompanyBankruptcy())?"checked":""%>/>
                                        <%=companyprofile_No%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback" style="margin-bottom: 0;">
                                        <label class="col-md-4 control-label" style="padding-left: initial;"><%=companyprofile_when%></label>
                                        <div class="col-md-8" style="padding-left:0;">
                                            <input style="width: 100%;" type="text" <%=globaldisabled%> name="company_bankruptcydate" style="display:initial;width:100%;" class="form-control datepicker" value="<%=functions.isValueNull(companyProfileVO.getCompanyBankruptcydate())==true?appFunctionUtil.convertTimestampToDatepicker(companyProfileVO.getCompanyBankruptcydate()):""%>"<%=!functions.isValueNull(companyProfileVO.getCompanyBankruptcy())||"N".equals(companyProfileVO.getCompanyBankruptcy())?"disabled":""%>>
                                            <%if(validationErrorList.getError("company_bankruptcydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="margin-top: inherit!important;height: inherit!important;"></i><%}%>
                                        </div>
                                    </div>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) || view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">

                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=companyprofile_license%></label>

                                    <div class="col-md-4">
                                        <input type="radio" <%=globaldisabled%> name="License_required" value="Y" <%="Y".equals(companyProfileVO.getLicense_required())?"checked":""%> />
                                        <%=companyprofile_Yes%>&nbsp;
                                        <input type="radio" <%=globaldisabled%> name="License_required"  value="N" <%="N".equals(companyProfileVO.getLicense_required()) || !functions.isValueNull(companyProfileVO.getLicense_required()) ?"checked":""%> />
                                        <%=companyprofile_No%></div>
                                </div>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=companyprofile_permission%></label>

                                    <div class="col-md-4">
                                        <input type="radio" <%=globaldisabled%>  id="License_Permission" name="License_Permission"  value="Y" <%="Y".equals(companyProfileVO.getLicense_Permission())?"checked":""%> />
                                        <%=companyprofile_Yes%>&nbsp;
                                        <input type="radio" <%=globaldisabled%>  id="License_Permission" name="License_Permission" value="N" <%="N".equals(companyProfileVO.getLicense_Permission()) || !functions.isValueNull(companyProfileVO.getLicense_Permission()) ?"checked":""%> />
                                        <%=companyprofile_No%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.legal_proceeding)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=companyprofile_legal%></label>

                                    <div class="col-md-4">
                                        <input type="radio" name="legal_proceeding" <%=globaldisabled%> value="Y" <%="Y".equals(companyProfileVO.getLegalProceeding())?"checked":""%> />
                                        <%=companyprofile_Yes%>&nbsp;
                                        <input type="radio"  name="legal_proceeding" <%=globaldisabled%> value="N" <%="N".equals(companyProfileVO.getLegalProceeding()) || !functions.isValueNull(companyProfileVO.getLegalProceeding()) ?"checked":""%> />
                                        <%=companyprofile_No%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.iscompany_insured) || companyProfileInputList.contains(BankInputName.insured_companyname) || companyProfileInputList.contains(BankInputName.insured_currency) ||companyProfileInputList.contains(BankInputName.insured_amount) || view)
                                    {
                                        String insuredDisable = "";
                                        if(functions.isValueNull(companyProfileVO.getIscompany_insured()) && "N".equals(companyProfileVO.getIscompany_insured()))
                                            insuredDisable="disabled";
                                        if("disabled".equals(globaldisabled))
                                            insuredDisable="disabled";
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=companyprofile_insured%></label>
                                    <div class="col-md-2">
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="iscompany_insured"  value="Y" <%="Y".equals(companyProfileVO.getIscompany_insured())?"checked":""%> />
                                        <%=companyprofile_Yes%>&nbsp;
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="iscompany_insured"  value="N" <%="N".equals(companyProfileVO.getIscompany_insured()) || !functions.isValueNull(companyProfileVO.getIscompany_insured()) ?"checked":""%> />
                                        <%=companyprofile_No%>
                                    </div>
                                    <div class="form-group col-md-6 has-feedback" style="margin-bottom: 0;">
                                        <label class="col-md-12 control-label" style="padding-left: initial;"><%=companyprofile_details%></label>
                                    </div>
                                </div>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <div class="form-group col-md-6" style="margin-bottom: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Insured_company%></label>
                                        <input type="text" class="form-control" <%=insuredDisable%> id="insured_companyname" name="insured_companyname"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getInsured_companyname())==true?companyProfileVO.getInsured_companyname():""%>" /><%if(validationErrorList.getError("insured_companyname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;margin-top: 24px;"></i><%}%>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Insured_Currency%></label>
                                        <select name="insured_currency"  class="form-control" <%=insuredDisable%>>
                                            <%
                                                out.println(appFunctionUtil.getCurrencyUnit(functions.isValueNull(companyProfileVO.getInsured_currency())? companyProfileVO.getInsured_currency():""));
                                            %>
                                        </select>
                                        <%if(validationErrorList.getError("insured_currency")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label for="insured_amount" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_Insured_Amount%></label>
                                        <input type="text" class="form-control" id="insured_amount" <%=insuredDisable%> name="insured_amount"  value="<%=functions.isValueNull(companyProfileVO.getInsured_amount())?companyProfileVO.getInsured_amount():""%>"/><%if(validationErrorList.getError("insured_amount")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.main_business_partner) || view)
                                    {
                                %>
                                <div class="form-group col-md-6 has-feedback">
                                    <label for="main_business_partner" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_main%></label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="main_business_partner" name="main_business_partner"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getMain_business_partner())==true?companyProfileVO.getMain_business_partner():""%>" /><%if(validationErrorList.getError("main_business_partner")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.loans)|| companyProfileInputList.contains(BankInputName.income_economic_activity) || companyProfileInputList.contains(BankInputName.interest_income)|| companyProfileInputList.contains(BankInputName.investments) || companyProfileInputList.contains(BankInputName.income_sources_other)||companyProfileInputList.contains(BankInputName.income_sources_other_yes) ||view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=companyprofile_income%></label>
                                </div>

                                <div class="row" id="income_row">
                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="checkbox"   name="loans" style="width:30%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(companyProfileVO.getLoans())?"checked":""%> />
                                            <%=companyprofile_Loans%></label>
                                    </div>
                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;">
                                            <input type="checkbox" name="income_economic_activity" style="width:30%;display: table;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(companyProfileVO.getIncome_economic_activity())?"checked":""%> />
                                            <%=companyprofile_economics%></label>
                                    </div>

                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;">
                                            <input type="checkbox"   name="interest_income" style="width:30%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(companyProfileVO.getInterest_income())?"checked":""%> />
                                            <%=companyprofile_Dividends%></label>
                                    </div>

                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="checkbox"   name="investments" style="width:30%;"  <%=globaldisabled%>
                                                   value="Y"  <%="Y".equals(companyProfileVO.getInvestments())?"checked":""%> />
                                            <%=companyprofile_Investments%></label>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label class="col-md-5 control-label checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"><input type="checkbox" id="income_sources_other"  name="income_sources_other" style="width:30%;"  <%=globaldisabled%> value="Y" <%="Y".equals(companyProfileVO.getIncome_sources_other())?"checked":""%> onchange="return myIncomeCompanyCheckBox('income_sources_other','income_sources_other')"/>
                                            <%=companyprofile_Other%>&nbsp;&nbsp;</label>
                                        <div class="col-md-7" id="top10">
                                            <input type="text" <%=globaldisabled%> name="income_sources_other_yes" style="display:initial;width:100%;" class="form-control" value="<%=functions.isValueNull(companyProfileVO.getIncome_sources_other_yes())==true?companyProfileVO.getIncome_sources_other_yes():""%>"<%=!functions.isValueNull(companyProfileVO.getIncome_sources_other())||"N".equals(companyProfileVO.getIncome_sources_other())?"disabled":""%>>
                                            <%if(validationErrorList.getError("income_sources_other_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="margin-top: inherit!important;height: inherit!important;"></i><%}%>
                                        </div>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                        }
                                %>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

    <%
        }
        addressIdentificationVOMap.put("COMPANY",identificationVO_company);
        addressIdentificationVOMap.put("BUSINESS",identificationVO_business);
        addressIdentificationVOMap.put("EU_COMPANY",identificationVO_euCompany);
        companyProfileVO.setCompanyProfile_addressVOMap(addressIdentificationVOMap);

        contactDetailsVOMap.put("MAIN", contactDetailsVO_main);
        contactDetailsVOMap.put("BILLING", contactDetailsVO_billing);
        contactDetailsVOMap.put("TECHNICAL", contactDetailsVO_technical);
        contactDetailsVOMap.put("CBK", contactDetailsVO_cbk);
        contactDetailsVOMap.put("PCI", contactDetailsVO_pci);
        companyProfileVO.setCompanyProfile_contactInfoVOMap(contactDetailsVOMap);
        applicationManagerVO.setCompanyProfileVO(companyProfileVO);
    %>

    <%
        if(companyProfileInputList.size()==0 && !view)
        {
            out.println("<div class=\"content-page\">");
            out.println("<div class=\"content\">");
            out.println("<div class=\"page-heading\">");
            out.println("<div class=\"row\">");
            out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
            out.println("<div class=\"widget\">");
            out.println("<div class=\"widget-header transparent\">\n" +
                    "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;"+companyprofile_Company_Profile1+"</strong></h2>\n" +
                    "                                <div class=\"additional-btn\">\n" +
                    "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                    "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                    "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                    "                                </div>\n" +
                    "                            </div>");
            out.println("<div class=\"widget-content padding\">");
          /*out.println("<div class=\"table-responsive\">");*/
            out.println(Functions.NewShowConfirmation1(companyprofile_Profile1,companyprofile_Company_details));
         /* out.println("</div>");*/
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");


        }
    %>


</div>
