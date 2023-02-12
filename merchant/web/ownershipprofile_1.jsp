<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileDetailsVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%!
  private Functions functions = new Functions();
  private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
%>
<%
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String ownwershipprofile_shareholder = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_shareholder"))?rb1.getString("ownwershipprofile_shareholder"): "Shareholder Profile and Corporate Shareholder Profile(% owned must be equal to 25% or more)";
  String ownwershipprofile_please = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_please"))?rb1.getString("ownwershipprofile_please"): "Please save the Shareholder Profile after entering the data provided";
  String ownwershipprofile_natural = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_natural"))?rb1.getString("ownwershipprofile_natural"): "How many natural person (not corporate) own more than 25% of the shares in your business?";
  String ownwershipprofile_select_count = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_select_count"))?rb1.getString("ownwershipprofile_select_count"): "Select Count";
  String ownwershipprofile_1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_1"))?rb1.getString("ownwershipprofile_1"): "1";
  String ownwershipprofile_2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_2"))?rb1.getString("ownwershipprofile_2"): "2";
  String ownwershipprofile_3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_3"))?rb1.getString("ownwershipprofile_3"): "3";
  String ownwershipprofile_4 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_4"))?rb1.getString("ownwershipprofile_4"): "4";
  String ownwershipprofile_shareholder_profile1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_shareholder_profile1"))?rb1.getString("ownwershipprofile_shareholder_profile1"): "Individual Shareholder Profile 1";
  String ownwershipprofile_Title = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Title"))?rb1.getString("ownwershipprofile_Title"): "Title*";
  String ownwershipprofile_Select_Title = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Select_Title"))?rb1.getString("ownwershipprofile_Select_Title"): "Select Title";
  String ownwershipprofile_MR = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_MR"))?rb1.getString("ownwershipprofile_MR"): "MR";
  String ownwershipprofile_MRS = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_MRS"))?rb1.getString("ownwershipprofile_MRS"): "MRS";
  String ownwershipprofile_MS = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_MS"))?rb1.getString("ownwershipprofile_MS"): "MS";
  String ownwershipprofile_MISS = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_MISS"))?rb1.getString("ownwershipprofile_MISS"): "MISS";
  String ownwershipprofile_MASTER = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_MASTER"))?rb1.getString("ownwershipprofile_MASTER"): "MASTER";
  String ownwershipprofile_DR = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_DR"))?rb1.getString("ownwershipprofile_DR"): "DR";
  String ownwershipprofile_First = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_First"))?rb1.getString("ownwershipprofile_First"): "First";
  String ownwershipprofile_Name = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Name"))?rb1.getString("ownwershipprofile_Name"): "Name*";
  String ownwershipprofile_last = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_last"))?rb1.getString("ownwershipprofile_last"): "Last";
  String ownwershipprofile_DateBirth = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_DateBirth"))?rb1.getString("ownwershipprofile_DateBirth"): "Date of Birth*";
  String ownwershipprofile_Country = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Country"))?rb1.getString("ownwershipprofile_Country"): "Country Code*";
  String ownwershipprofile_PhoneNo = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_PhoneNo"))?rb1.getString("ownwershipprofile_PhoneNo"): "Phone No*";
  String ownwershipprofile_Email = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Email"))?rb1.getString("ownwershipprofile_Email"): "Email Address*";
  String ownwershipprofile_Percentage = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Percentage"))?rb1.getString("ownwershipprofile_Percentage"): "Percentage(%)holding*";
  String ownwershipprofile_invalid_percentage = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_invalid_percentage"))?rb1.getString("ownwershipprofile_invalid_percentage"): "Invalid Percentage Holding";
  String ownwershipprofile_Address = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Address"))?rb1.getString("ownwershipprofile_Address"): "Address Details";
  String ownwershipprofile_Address_Proof = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Address_Proof"))?rb1.getString("ownwershipprofile_Address_Proof"): "Address Proof";
  String ownwershipprofile_AddressID = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_AddressID"))?rb1.getString("ownwershipprofile_AddressID"): "Address ID";
  String ownwershipprofile_Address1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Address1"))?rb1.getString("ownwershipprofile_Address1"): "Address*";
  String ownwershipprofile_Street = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Street"))?rb1.getString("ownwershipprofile_Street"): "Street*";
  String ownwershipprofile_City = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_City"))?rb1.getString("ownwershipprofile_City"): "City/Town";
  String ownwershipprofile_ID = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_ID"))?rb1.getString("ownwershipprofile_ID"): "State/County of ID";
  String ownwershipprofile_Country1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Country1"))?rb1.getString("ownwershipprofile_Country1"): "Country*";
  String ownwershipprofile_zip = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_zip"))?rb1.getString("ownwershipprofile_zip"): "Zip/Postal Code";
  String ownwershipprofile_Identification = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Identification"))?rb1.getString("ownwershipprofile_Identification"): "Identification Details";
  String ownwershipprofile_type = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_type"))?rb1.getString("ownwershipprofile_type"): "Identification Type*";
  String ownwershipprofile_IdentificationID = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_IdentificationID"))?rb1.getString("ownwershipprofile_IdentificationID"): "Identification ID*";
  String ownwershipprofile_Nationality = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Nationality"))?rb1.getString("ownwershipprofile_Nationality"): "Nationality";
  String ownwershipprofile_issue = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_issue"))?rb1.getString("ownwershipprofile_issue"): "Issuing date of ID";
  String ownwershipprofile_expiry = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_expiry"))?rb1.getString("ownwershipprofile_expiry"): "Expiry date of ID";
  String ownwershipprofile_politically = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_politically"))?rb1.getString("ownwershipprofile_politically"): "Politically exposed person?";
  String ownwershipprofile_Yes = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Yes"))?rb1.getString("ownwershipprofile_Yes"): "Yes";
  String ownwershipprofile_No = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_No"))?rb1.getString("ownwershipprofile_No"): "No";
  String ownwershipprofile_existence = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_existence"))?rb1.getString("ownwershipprofile_existence"): "Existence of criminal record?";
  String ownwershipprofile_shareholder_profile2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_shareholder_profile2"))?rb1.getString("ownwershipprofile_shareholder_profile2"): "Individual Shareholder Profile 2";
  String ownwershipprofile_shareholder_profile3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_shareholder_profile3"))?rb1.getString("ownwershipprofile_shareholder_profile3"): "Individual Shareholder Profile 3";
  String ownwershipprofile_shareholder_profile4 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_shareholder_profile4"))?rb1.getString("ownwershipprofile_shareholder_profile4"): "Individual Shareholder Profile 4";
  String ownwershipprofile_shareholder3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_shareholder3"))?rb1.getString("ownwershipprofile_shareholder3"): "Corporate Shareholder Profile(Corporate Shareholder must equal 50% or more)";
  String ownwershipprofile_please2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_please2"))?rb1.getString("ownwershipprofile_please2"): "Please save the Directors Profile after entering the data provided";
  String ownwershipprofile_corporate = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporate"))?rb1.getString("ownwershipprofile_corporate"): "How many Corporate Shareholders do you have that more than 25% in your business?";
  String ownwershipprofile_count0 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_count0"))?rb1.getString("ownwershipprofile_count0"): "0";
  String ownwershipprofile_profile1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_profile1"))?rb1.getString("ownwershipprofile_profile1"): "Corporate Shareholder Profile 1";
  String ownwershipprofile_Corporate1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Corporate1"))?rb1.getString("ownwershipprofile_Corporate1"): "Corporate Shareholder 1";
  String ownwershipprofile_Registration = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Registration"))?rb1.getString("ownwershipprofile_Registration"): "Registration Number";
  String ownwershipprofile_Percentage_holding = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Percentage_holding"))?rb1.getString("ownwershipprofile_Percentage_holding"): "Percentage(%)holding";
  String ownwershipprofile_Address_Details1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Address_Details1"))?rb1.getString("ownwershipprofile_Address_Details1"): "Address Details";
  String ownwershipprofile_AddressProof1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_AddressProof1"))?rb1.getString("ownwershipprofile_AddressProof1"): "Address Proof";
  String ownwershipprofile_Address_ID = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Address_ID"))?rb1.getString("ownwershipprofile_Address_ID"): "Address ID";
  String ownwershipprofile_corporateAddress = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporateAddress"))?rb1.getString("ownwershipprofile_corporateAddress"): "Address";
  String ownwershipprofile_corporateStreet = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporateStreet"))?rb1.getString("ownwershipprofile_corporateStreet"): "Street";
  String ownwershipprofile_corporatecity = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporatecity"))?rb1.getString("ownwershipprofile_corporatecity"): "City/Town";
  String ownwershipprofile_corporatestate = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporatestate"))?rb1.getString("ownwershipprofile_corporatestate"): "State/County/Province";
  String ownwershipprofile_corporatecountry = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporatecountry"))?rb1.getString("ownwershipprofile_corporatecountry"): "Country";
  String ownwershipprofile_corporate_zip = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporate_zip"))?rb1.getString("ownwershipprofile_corporate_zip"): "Zip/Postal Code";
  String ownwershipprofile_corporate_identification = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporate_identification"))?rb1.getString("ownwershipprofile_corporate_identification"): "Identification Details";
  String ownwershipprofile_corporate_type = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporate_type"))?rb1.getString("ownwershipprofile_corporate_type"): "Identification Type";
  String ownwershipprofile_corporate_Id = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporate_Id"))?rb1.getString("ownwershipprofile_corporate_Id"): "Identification ID";
  String ownwershipprofile_profile2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_profile2"))?rb1.getString("ownwershipprofile_profile2"): "Corporate Shareholder Profile 2";
  String ownwershipprofile_Corporate2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Corporate2"))?rb1.getString("ownwershipprofile_Corporate2"): "Corporate Shareholder 2";
  String ownwershipprofile_profile3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_profile3"))?rb1.getString("ownwershipprofile_profile3"): "Corporate Shareholder Profile 3";
  String ownwershipprofile_Corporate3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Corporate3"))?rb1.getString("ownwershipprofile_Corporate3"): "Corporate Shareholder 3";
  String ownwershipprofile_profile4 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_profile4"))?rb1.getString("ownwershipprofile_profile4"): "Corporate Shareholder Profile 4";
  String ownwershipprofile_Corporate4 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Corporate4"))?rb1.getString("ownwershipprofile_Corporate4"): "Corporate Shareholder 4";
  String ownwershipprofile_director = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director"))?rb1.getString("ownwershipprofile_director"): "Director's Profile";
  String ownwershipprofile_corporate_please3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_corporate_please3"))?rb1.getString("ownwershipprofile_corporate_please3"): "Please save the Directors Profile after entering the data provided";
  String ownwershipprofile_directors = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_directors"))?rb1.getString("ownwershipprofile_directors"): "How many Directors do you have?";
  String ownwershipprofile_director_profile1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_profile1"))?rb1.getString("ownwershipprofile_director_profile1"): "Directors Profile 1";
  String ownwershipprofile_director_profile2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_profile2"))?rb1.getString("ownwershipprofile_director_profile2"): "Directors Profile 2";
  String ownwershipprofile_director_title2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_title2"))?rb1.getString("ownwershipprofile_director_title2"): "Title";
  String ownwershipprofile_director_Name1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_Name1"))?rb1.getString("ownwershipprofile_director_Name1"): "Name";
  String ownwershipprofile_director_Date1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_Date1"))?rb1.getString("ownwershipprofile_director_Date1"): "Date of Birth";
  String ownwershipprofile_director_code1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_code1"))?rb1.getString("ownwershipprofile_director_code1"): "Country Code";
  String ownwershipprofile_director_phoneno1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_phoneno1"))?rb1.getString("ownwershipprofile_director_phoneno1"): "Phone No";
  String ownwershipprofile_director_email1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_email1"))?rb1.getString("ownwershipprofile_director_email1"): "Email Address";
  String ownwershipprofile_director_percentage1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_percentage1"))?rb1.getString("ownwershipprofile_director_percentage1"): "Percentage(%)holding";
  String ownwershipprofile_director_Address1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_Address1"))?rb1.getString("ownwershipprofile_director_Address1"): "Address";
  String ownwershipprofile_director_Street1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_Street1"))?rb1.getString("ownwershipprofile_director_Street1"): "Street";
  String ownwershipprofile_director_country1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_country1"))?rb1.getString("ownwershipprofile_director_country1"): "Country";
  String ownwershipprofile_director_type1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_type1"))?rb1.getString("ownwershipprofile_director_type1"): "Identification Type";
  String ownwershipprofile_director_identify1 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_identify1"))?rb1.getString("ownwershipprofile_director_identify1"): "Identification ID";
  String ownwershipprofile_director_profile3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_profile3"))?rb1.getString("ownwershipprofile_director_profile3"): "Directors Profile 3";
  String ownwershipprofile_director_profile4 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_director_profile4"))?rb1.getString("ownwershipprofile_director_profile4"): "Directors Profile 4";
  String ownwershipprofile_authorize = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_authorize"))?rb1.getString("ownwershipprofile_authorize"): "Authorize Signatory Profile";
  String ownwershipprofile_please4 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_please4"))?rb1.getString("ownwershipprofile_please4"): "Please save Authorized Signatory after entering the data provided";
  String ownwershipprofile_how = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_how"))?rb1.getString("ownwershipprofile_how"): "How many Authorised Signatories do you have?";
  String ownwershipprofile_signatory = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_signatory"))?rb1.getString("ownwershipprofile_signatory"): "Authorize Signatory Profile 1";
  String ownwershipprofile_Designation = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_Designation"))?rb1.getString("ownwershipprofile_Designation"): "Designation";
  String ownwershipprofile_signatory2 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_signatory2"))?rb1.getString("ownwershipprofile_signatory2"): "Authorize Signatory Profile 2";
  String ownwershipprofile_signatory3 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_signatory3"))?rb1.getString("ownwershipprofile_signatory3"): "Authorize Signatory Profile 3";
  String ownwershipprofile_signatory4 = StringUtils.isNotEmpty(rb1.getString("ownwershipprofile_signatory4"))?rb1.getString("ownwershipprofile_signatory4"): "Authorize Signatory Profile 4";

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
  String disableNamePrinciple2="";
  String disableNamePrinciple3="";
  String disablecolor="";

  String disableShareHolder2="";
  String disableShareHolder3="";
  String disableShareHolder4="";

  String disableDirector2="";
  String disableDirector3="";
  String disableDirector4="";

  String disableAuthSignatory2="";
  String disableAuthSignatory3="";
  String disableAuthSignatory4="";

  String disablePoliticalCriminal2="";
  String disablePoliticalCriminal3="";
  String disablePoliticalCriminal4="";

  ApplicationManagerVO applicationManagerVO=null;
  OwnershipProfileVO ownershipProfileVO=null;
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder1 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder2 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder3 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder4 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder1 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder2 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder3 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder4 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_director1 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_director2 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_director3 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_director4 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory1 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory2 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory3 = new OwnershipProfileDetailsVO();
  OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory4 = new OwnershipProfileDetailsVO();
  ValidationErrorList validationErrorList=null;
  Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap = new HashMap();
  if(session.getAttribute("applicationManagerVO")!=null)
  {
    applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
  }

  if(applicationManagerVO.getOwnershipProfileVO()!=null)
  {
    ownershipProfileVO=applicationManagerVO.getOwnershipProfileVO();
    if(ownershipProfileVO.getOwnershipProfileDetailsVOMap()!=null && ownershipProfileVO.getOwnershipProfileDetailsVOMap().size() > 0)
    {
      ownershipProfileDetailsVO_shareholder1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER1");
      ownershipProfileDetailsVO_shareholder2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER2");
      ownershipProfileDetailsVO_shareholder3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER3");
      ownershipProfileDetailsVO_shareholder4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("SHAREHOLDER4");
      ownershipProfileDetailsVO_corporateShareholder1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER1");
      ownershipProfileDetailsVO_corporateShareholder2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER2");
      ownershipProfileDetailsVO_corporateShareholder3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER3");
      ownershipProfileDetailsVO_corporateShareholder4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("CORPORATESHAREHOLDER4");
      ownershipProfileDetailsVO_director1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR1");
      ownershipProfileDetailsVO_director2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR2");
      ownershipProfileDetailsVO_director3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR3");
      ownershipProfileDetailsVO_director4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("DIRECTOR4");
      ownershipProfileDetailsVO_authorizeSignatory1 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY1");
      ownershipProfileDetailsVO_authorizeSignatory2 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY2");
      ownershipProfileDetailsVO_authorizeSignatory3 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY3");
      ownershipProfileDetailsVO_authorizeSignatory4 = ownershipProfileVO.getOwnershipProfileDetailsVOMap().get("AUTHORIZESIGNATORY4");
    }
  }
  if(ownershipProfileVO==null)
  {
    ownershipProfileVO = new OwnershipProfileVO();
    ownershipProfileDetailsVOMap.put("SHAREHOLDER1",ownershipProfileDetailsVO_shareholder1);
    ownershipProfileDetailsVOMap.put("SHAREHOLDER2",ownershipProfileDetailsVO_shareholder2);
    ownershipProfileDetailsVOMap.put("SHAREHOLDER3",ownershipProfileDetailsVO_shareholder3);
    ownershipProfileDetailsVOMap.put("SHAREHOLDER4",ownershipProfileDetailsVO_shareholder4);
    ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER1",ownershipProfileDetailsVO_corporateShareholder1);
    ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER2",ownershipProfileDetailsVO_corporateShareholder2);
    ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER3",ownershipProfileDetailsVO_corporateShareholder3);
    ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER4",ownershipProfileDetailsVO_corporateShareholder4);
    ownershipProfileDetailsVOMap.put("DIRECTOR1",ownershipProfileDetailsVO_director1);
    ownershipProfileDetailsVOMap.put("DIRECTOR2",ownershipProfileDetailsVO_director2);
    ownershipProfileDetailsVOMap.put("DIRECTOR3",ownershipProfileDetailsVO_director3);
    ownershipProfileDetailsVOMap.put("DIRECTOR4",ownershipProfileDetailsVO_director4);
    ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY1",ownershipProfileDetailsVO_authorizeSignatory1);
    ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY2",ownershipProfileDetailsVO_authorizeSignatory2);
    ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY3",ownershipProfileDetailsVO_authorizeSignatory3);
    ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY4",ownershipProfileDetailsVO_authorizeSignatory4);
  }
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
    /*if(!functions.isValueNull(ownershipProfileVO.getNameprincipal1_owned()) || 100<=(Integer.valueOf(ownershipProfileVO.getNameprincipal1_owned())))
    {
        disableNamePrinciple2="disabled";
        disableNamePrinciple3="disabled";
        disablecolor="background-color:#EBEBE4";
    }
    if((!functions.isValueNull(ownershipProfileVO.getNameprincipal1_owned()) || !functions.isValueNull(ownershipProfileVO.getNameprincipal2_owned())) || ((functions.isValueNull(ownershipProfileVO.getNameprincipal1_owned()) || functions.isValueNull(ownershipProfileVO.getNameprincipal2_owned())) && 100<=(Integer.valueOf(ownershipProfileVO.getNameprincipal1_owned())+Integer.valueOf(ownershipProfileVO.getNameprincipal2_owned()))))
    {
        disableNamePrinciple3="disabled";
        disablecolor="background-color:#EBEBE4";
    }*/
  if(ownershipProfileDetailsVO_shareholder1!=null)
  {
    if (!functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || 100 <= (Integer.valueOf(ownershipProfileDetailsVO_shareholder1.getOwned())))
    {
      disableShareHolder2 = "";
      disableShareHolder3 = "";
      disableShareHolder4 = "";
      disablePoliticalCriminal2 = "opacity:0.5;pointer-events: none;";
      disablePoliticalCriminal3 = "opacity:0.5;pointer-events: none;";
      disablePoliticalCriminal4 = "opacity:0.5;pointer-events: none;";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  if(ownershipProfileDetailsVO_shareholder1!=null && ownershipProfileDetailsVO_shareholder2!=null)
  {
    if ((!functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_shareholder1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_shareholder2.getOwned()))))
    {
      disableShareHolder3 = "";
      disableShareHolder4 = "";
      disablePoliticalCriminal3 = "opacity:0.5;pointer-events: none;";
      disablePoliticalCriminal4 = "opacity:0.5;pointer-events: none;";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  if(ownershipProfileDetailsVO_shareholder1!=null && ownershipProfileDetailsVO_shareholder2!=null && ownershipProfileDetailsVO_shareholder3!=null)
  {
    if ((!functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())|| !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned())|| functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_shareholder1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_shareholder2.getOwned())+ Integer.valueOf(ownershipProfileDetailsVO_shareholder3.getOwned()))))
    {
      disableShareHolder4 = "";
      disablePoliticalCriminal4 = "opacity:0.5;pointer-events: none;";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  //for Director

  if(ownershipProfileDetailsVO_director1!=null)
  {
    if (!functions.isValueNull(ownershipProfileDetailsVO_director1.getOwned()) || 100 <= (Integer.valueOf(ownershipProfileDetailsVO_director1.getOwned())))
    {
      disableDirector2 = "";
      disableDirector3 = "";
      disableDirector4 = "";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  if(ownershipProfileDetailsVO_director1!=null && ownershipProfileDetailsVO_director2!=null)
  {
    if ((!functions.isValueNull(ownershipProfileDetailsVO_director1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_director2.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_director1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_director2.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_director1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_director2.getOwned()))))
    {
      disableDirector3 = "";
      disableDirector4 = "";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  if(ownershipProfileDetailsVO_director1!=null && ownershipProfileDetailsVO_director2!=null && ownershipProfileDetailsVO_director3!=null)
  {
    if ((!functions.isValueNull(ownershipProfileDetailsVO_director1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_director2.getOwned())|| !functions.isValueNull(ownershipProfileDetailsVO_director3.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_director1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_director2.getOwned())|| functions.isValueNull(ownershipProfileDetailsVO_director3.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_director1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_director2.getOwned())+ Integer.valueOf(ownershipProfileDetailsVO_director3.getOwned()))))
    {
      disableDirector4 = "";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  //for AuthorizedSignatory

  if(ownershipProfileDetailsVO_authorizeSignatory1!=null)
  {
    if (!functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) || 100 <= (Integer.valueOf(ownershipProfileDetailsVO_authorizeSignatory1.getOwned())))
    {
      disableAuthSignatory2 = "disabled";
      disableAuthSignatory3 = "disabled";
      disableAuthSignatory4 = "disabled";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  if(ownershipProfileDetailsVO_authorizeSignatory1!=null && ownershipProfileDetailsVO_authorizeSignatory2!=null)
  {
    if ((!functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_authorizeSignatory2.getOwned()))))
    {
      disableAuthSignatory3 = "disabled";
      disableAuthSignatory4 = "disabled";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  if(ownershipProfileDetailsVO_authorizeSignatory1!=null && ownershipProfileDetailsVO_authorizeSignatory2!=null && ownershipProfileDetailsVO_authorizeSignatory3!=null)
  {
    if ((!functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getOwned())|| !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getOwned())) || ((functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) || functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getOwned())|| functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getOwned())) && 100 <= (Integer.valueOf(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) + Integer.valueOf(ownershipProfileDetailsVO_authorizeSignatory2.getOwned())+ Integer.valueOf(ownershipProfileDetailsVO_authorizeSignatory3.getOwned()))))
    {
      disableAuthSignatory4 = "disabled";
      disablecolor = "background-color:#EBEBE4";
    }
  }

  //for specific condition
  Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
  Map<Boolean, Set<BankInputName>> fullPageViseValidationForStep=new HashMap<Boolean, Set<BankInputName>>();

  Set<BankInputName> ownershipProfileInputList=new HashSet<BankInputName>();

  //System.out.println("Current PageNO:::"+request.getParameter("currentPageNO"));

  if(request.getAttribute("fullValidationForStep")!=null)
  {
    //System.out.println("Inside FullValidationForStep:::");
    fullValidationForStep= (Map<Integer, Map<Boolean, Set<BankInputName>>>) request.getAttribute("fullValidationForStep");
    if(functions.isValueNull(request.getParameter("currentPageNO")) && fullValidationForStep.containsKey(Integer.valueOf(request.getParameter("currentPageNO"))))
    {
      //System.out.println("Inside PageViseFullValidationForStep:::");
      fullPageViseValidationForStep=fullValidationForStep.get(Integer.valueOf(request.getParameter("currentPageNO")));
      //System.out.println("PageViseFullValidationForStep::::"+fullPageViseValidationForStep);
      if(fullPageViseValidationForStep.containsKey(false))
        ownershipProfileInputList.addAll(fullPageViseValidationForStep.get(false));
      if(fullPageViseValidationForStep.containsKey(true))
        ownershipProfileInputList.addAll(fullPageViseValidationForStep.get(true));
    }
  }
%>
<%
  if(ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_title) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_lastname) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_address) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_city) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_zip) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_country) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_street) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_telephonenumber) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_designation) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_identificationtypeselect)||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_State) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_nationality) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_Passportexpirydate) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile1_criminalrecord) ||

          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_title) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_lastname) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_address) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_city) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_zip) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_country) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_street) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_telephonenumber)  ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_designation) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_identificationtypeselect) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_State) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_nationality) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_Passportexpirydate) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile2_criminalrecord) ||

          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_title) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_lastname) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_address) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_city) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_zip) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_country) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_street) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_telephonenumber) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_designation) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_identificationtypeselect) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_State) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_nationality) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_criminalrecord) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile3_Passportexpirydate)||

          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_title) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_lastname) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_address) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_city) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_zip) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_country) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_street) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_telephonenumber) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_designation) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_identificationtypeselect) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_State) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_nationality) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_criminalrecord) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.authorizedsignatoryprofile4_Passportexpirydate) || view)

  {
%>

<div class="content" style="margin-top: 0;">
  <!-- Page Heading Start -->
  <div class="page-heading">


    <div class="row">
      <div class="col-sm-12 portlets ui-sortable">
        <div class="widget" style="margin-bottom: 0;">
          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownwershipprofile_authorize%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>

          <div class="widget-content padding">
            <div id="horizontal-form">
              <div class="container-fluid ">
                <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                  <div class="form foreground bodypanelfont_color panelbody_color">
                    <center><h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"> <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownwershipprofile_please4%></h5></center>

                    <div class="col-sm-5 portlets ui-sortable">
                      <label for="numOfAuthrisedSignatory" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_how%></label>
                    </div>
                    <div class="col-sm-4 portlets ui-sortable">
                      <%--<input type="text" class="form-control"  id="numOfAuthrisedSignatory" name="numOfAuthrisedSignatory" onkeypress="return isNumber(event)" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfAuthrisedSignatory())?ownershipProfileVO.getNumOfAuthrisedSignatory():"1"%>"/><%if(validationErrorList.getError("numOfAuthrisedSignatory")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>--%>
                      <select name="numOfAuthrisedSignatory" id="numOfAuthrisedSignatory"  value="">
                        <option value=""><%=ownwershipprofile_select_count%></option>
                        <option value="0"><%=ownwershipprofile_count0%></option>
                        <option value="1"><%=ownwershipprofile_1%></option>
                        <option value="2"><%=ownwershipprofile_2%></option>
                        <option value="3"><%=ownwershipprofile_3%></option>
                        <option value="4"><%=ownwershipprofile_4%></option>

                      </select>
                      <input value='<%=functions.isValueNull(ownershipProfileVO.getNumOfAuthrisedSignatory())?ownershipProfileVO.getNumOfAuthrisedSignatory():"1"%>' id="getcountvalAuth" type="hidden">
                      <script>

                        var countryval = document.getElementById('getcountvalAuth').value;
                        $('[name=numOfAuthrisedSignatory] option').filter(function ()
                        {
                          if (countryval == '0')
                          {
                            return ($(this).text() == '0');
                          }
                          else if (countryval == '1')
                          {
                            return ($(this).text() == '1');
                          }
                          else if (countryval == '2')
                          {
                            return ($(this).text() == '2');
                          }
                          else if (countryval == '3')
                          {
                            return ($(this).text() == '3');
                          }
                          else if (countryval == '4')
                          {
                            return ($(this).text() == '4');
                          }
                          else
                          {
                            return ($(this).text() == 'Select Count');
                          }

                        })
                                .prop('selected', true);
                      </script>

                    </div>
                    <div class="authorized1">
                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_signatory%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_title2%></label>
                        <select class="form-control" id="authorizedsignatoryprofile_title" name="authorizedsignatoryprofile_title" <%=globaldisabled%>>
                          <option value=""><%=ownwershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownwershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownwershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownwershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownwershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownwershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile" name="authorizedsignatoryprofile"style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory1.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_lastname" name="authorizedsignatoryprofile_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory1.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Date1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_dateofbirth" name="authorizedsignatoryprofile_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile1_designation" name="authorizedsignatoryprofile1_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory1.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile1_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_telnocc1" name="authorizedsignatoryprofile_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_phoneno1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_telephonenumber" name="authorizedsignatoryprofile_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory1.getTelephonenumber():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_email1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_emailaddress" name="authorizedsignatoryprofile_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory1.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile1_owned" name="authorizedsignatoryprofile1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory1.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile2_owned','authorizedsignatoryprofile3_owned','authorizedsignatoryprofile4_owned',1)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile1_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile1_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile1_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile1_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>



                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile1_addressproof" id="authorizedsignatoryprofile1_addressproof"class="form-control" <%=globaldisabled%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile1_addressId" name="authorizedsignatoryprofile1_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_address" name="authorizedsignatoryprofile_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_street" name="authorizedsignatoryprofile_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory1.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_city" name="authorizedsignatoryprofile_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory1.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_State" name="authorizedsignatoryprofile_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getState()) ? ownershipProfileDetailsVO_authorizeSignatory1.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label>
                        <select id="authorizedsignatoryprofile_country" <%=globaldisabled%> name="authorizedsignatoryprofile_country" onchange="myjunk1('authorizedsignatoryprofile_country','authorizedsignatoryprofile_telnocc1');" class="form-control" >
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory1.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_zip" name="authorizedsignatoryprofile_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory1.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                        <select class="form-control" id="authorizedsignatoryprofile_identificationtypeselect" name="authorizedsignatoryprofile_identificationtypeselect" id="authorizedsignatoryprofile_identificationtypeselect" <%=globaldisabled%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_identificationtype" name="authorizedsignatoryprofile_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile_nationality" name="authorizedsignatoryprofile_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory1.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_passportissuedate" name="authorizedsignatoryprofile_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_Passportexpirydate" name="authorizedsignatoryprofile_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile1_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile1_politicallyexposed" name="authorizedsignatoryprofile1_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile1_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile1_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile1_criminalrecord" name="authorizedsignatoryprofile1_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile1_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                    </div>
                    <div class="authorized2">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_signatory2%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_title2%></label>
                        <select class="form-control" id="authorizedsignatoryprofile2_title" name="authorizedsignatoryprofile2_title" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <option value=""><%=ownwershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownwershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownwershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownwershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownwershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownwershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2" name="authorizedsignatoryprofile2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull((ownershipProfileDetailsVO_authorizeSignatory2.getFirstname())) ? ownershipProfileDetailsVO_authorizeSignatory2.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_lastname" name="authorizedsignatoryprofile2_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory2.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_dateofbirth" name="authorizedsignatoryprofile2_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_designation" name="authorizedsignatoryprofile2_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory2.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile2_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_telnocc1" name="authorizedsignatoryprofile2_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_phoneno1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_telephonenumber" name="authorizedsignatoryprofile2_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_email1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_emailaddress" name="authorizedsignatoryprofile2_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory2.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile2_owned" name="authorizedsignatoryprofile2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory2.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile1_owned','authorizedsignatoryprofile3_owned','authorizedsignatoryprofile4_owned',2)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile2_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile2_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile2_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile2_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile2_addressproof" id="authorizedsignatoryprofile2_addressproof"class="form-control" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_addressId" name="authorizedsignatoryprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_address" name="authorizedsignatoryprofile2_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_street" name="authorizedsignatoryprofile2_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory2.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_city" name="authorizedsignatoryprofile2_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory2.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_State" name="authorizedsignatoryprofile2_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getState()) ? ownershipProfileDetailsVO_authorizeSignatory2.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label>
                        <select id="authorizedsignatoryprofile2_country" name="authorizedsignatoryprofile2_country" onchange="myjunk1('authorizedsignatoryprofile2_country','authorizedsignatoryprofile2_telnocc1');" class="form-control" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory2.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_zip" name="authorizedsignatoryprofile2_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory2.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                        <select class="form-control" name="authorizedsignatoryprofile2_identificationtypeselect" id="authorizedsignatoryprofile2_identificationtypeselect" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>

                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_identificationtype" name="authorizedsignatoryprofile2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile2_nationality" name="authorizedsignatoryprofile2_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory2.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_passportissuedate" name="authorizedsignatoryprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_Passportexpirydate" name="authorizedsignatoryprofile2_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile2_politicallyexposed" name="authorizedsignatoryprofile2_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile2_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile2_criminalrecord" name="authorizedsignatoryprofile2_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile2_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                    </div>
                    <div class="authorized3">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_signatory3%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_title2%></label>
                        <select class="form-control" id="authorizedsignatoryprofile3_title" name="authorizedsignatoryprofile3_title" <%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <option value=""><%=ownwershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownwershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownwershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownwershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownwershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownwershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3" name="authorizedsignatoryprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory3.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_lastname" name="authorizedsignatoryprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory3.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Date1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_dateofbirth" name="authorizedsignatoryprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_designation" name="authorizedsignatoryprofile3_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory3.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile3_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_telnocc1" name="authorizedsignatoryprofile3_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory3.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_phoneno1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_telephonenumber" name="authorizedsignatoryprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory3.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_email1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_emailaddress" name="authorizedsignatoryprofile3_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory3.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile3_owned" name="authorizedsignatoryprofile3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory3.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile1_owned','authorizedsignatoryprofile2_owned','authorizedsignatoryprofile4_owned',3)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile3_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile3_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile3_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile3_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile3_addressproof" id="authorizedsignatoryprofile3_addressproof"class="form-control" <%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_addressId" name="authorizedsignatoryprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_address" name="authorizedsignatoryprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_street" name="authorizedsignatoryprofile3_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory3.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_city" name="authorizedsignatoryprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory3.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_State" name="authorizedsignatoryprofile3_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getState()) ? ownershipProfileDetailsVO_authorizeSignatory3.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label>
                        <select id="authorizedsignatoryprofile3_country" name="authorizedsignatoryprofile3_country" onchange="myjunk1('authorizedsignatoryprofile3_country','authorizedsignatoryprofile3_telnocc1');"  class="form-control" <%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory3.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_zip" name="authorizedsignatoryprofile3_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory3.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                        <select class="form-control" name="authorizedsignatoryprofile3_identificationtypeselect" id="authorizedsignatoryprofile3_identificationtypeselect" <%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_identificationtype" name="authorizedsignatoryprofile3_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile3_nationality" name="authorizedsignatoryprofile3_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory3.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_passportissuedate" name="authorizedsignatoryprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_Passportexpirydate" name="authorizedsignatoryprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile3_politicallyexposed" name="authorizedsignatoryprofile3_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile3_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile3_criminalrecord" name="authorizedsignatoryprofile3_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile3_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                    </div>

                    <!--Authirise signatory 4 -->

                    <div class="authorized4">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_signatory4%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_title2%></label>
                        <select class="form-control" id="authorizedsignatoryprofile4_title" name="authorizedsignatoryprofile4_title" <%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <option value=""><%=ownwershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownwershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownwershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownwershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownwershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownwershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4" name="authorizedsignatoryprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory4.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_lastname" name="authorizedsignatoryprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory4.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Date1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_dateofbirth" name="authorizedsignatoryprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_designation" name="authorizedsignatoryprofile4_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory4.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile4_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_telnocc1" name="authorizedsignatoryprofile4_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory4.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_phoneno1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_telephonenumber" name="authorizedsignatoryprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory4.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_email1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_emailaddress" name="authorizedsignatoryprofile4_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory4.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile4_owned" name="authorizedsignatoryprofile4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory4.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile1_owned','authorizedsignatoryprofile2_owned','authorizedsignatoryprofile3_owned',4)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile4_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile4_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile4_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile4_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile4_addressproof" id="authorizedsignatoryprofile4_addressproof"class="form-control" <%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_addressId" name="authorizedsignatoryprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_address" name="authorizedsignatoryprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_street" name="authorizedsignatoryprofile4_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory4.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_city" name="authorizedsignatoryprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory4.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_State" name="authorizedsignatoryprofile4_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getState()) ? ownershipProfileDetailsVO_authorizeSignatory4.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label>
                        <select id="authorizedsignatoryprofile4_country" name="authorizedsignatoryprofile4_country" onchange="myjunk1('authorizedsignatoryprofile4_country','authorizedsignatoryprofile4_telnocc1');"  class="form-control" <%=globaldisabled%> <%=disableAuthSignatory4%> >
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory4.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_zip" name="authorizedsignatoryprofile4_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory4.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                        <select class="form-control" name="authorizedsignatoryprofile4_identificationtypeselect" id="authorizedsignatoryprofile4_identificationtypeselect" <%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_identificationtype" name="authorizedsignatoryprofile4_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile4_nationality" name="authorizedsignatoryprofile4_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory4.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_passportissuedate" name="authorizedsignatoryprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_Passportexpirydate" name="authorizedsignatoryprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile4_politicallyexposed" name="authorizedsignatoryprofile4_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile4_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile4_criminalrecord" name="authorizedsignatoryprofile4_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile4_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                      </div>
                    </div>
                  </div>
                </div>
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
  ownershipProfileDetailsVOMap.put("SHAREHOLDER1",ownershipProfileDetailsVO_shareholder1);
  ownershipProfileDetailsVOMap.put("SHAREHOLDER2",ownershipProfileDetailsVO_shareholder2);
  ownershipProfileDetailsVOMap.put("SHAREHOLDER3",ownershipProfileDetailsVO_shareholder3);
  ownershipProfileDetailsVOMap.put("SHAREHOLDER4",ownershipProfileDetailsVO_shareholder4);
  ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER1",ownershipProfileDetailsVO_corporateShareholder1);
  ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER2",ownershipProfileDetailsVO_corporateShareholder2);
  ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER3",ownershipProfileDetailsVO_corporateShareholder3);
  ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER4",ownershipProfileDetailsVO_corporateShareholder4);
  ownershipProfileDetailsVOMap.put("DIRECTOR1",ownershipProfileDetailsVO_director1);
  ownershipProfileDetailsVOMap.put("DIRECTOR2",ownershipProfileDetailsVO_director2);
  ownershipProfileDetailsVOMap.put("DIRECTOR3",ownershipProfileDetailsVO_director3);
  ownershipProfileDetailsVOMap.put("DIRECTOR4",ownershipProfileDetailsVO_director4);
  ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY1",ownershipProfileDetailsVO_authorizeSignatory1);
  ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY2",ownershipProfileDetailsVO_authorizeSignatory2);
  ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY3",ownershipProfileDetailsVO_authorizeSignatory3);
  ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY4",ownershipProfileDetailsVO_authorizeSignatory4);
  ownershipProfileVO.setOwnershipProfileDetailsVOMap(ownershipProfileDetailsVOMap);
  applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
%>