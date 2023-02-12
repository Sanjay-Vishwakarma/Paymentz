
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileDetailsVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%!
  private Functions functions = new Functions();
  private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
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


  // for director profile --suraj

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

  // for Authorized signatory --suraj

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
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String ownershipprofile_shareholder = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_shareholder")) ? rb1.getString("ownershipprofile_shareholder") : "Shareholder Profile and Corporate Shareholder Profile(% owned must be equal to 25% or more)";
  String ownershipprofile_please = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_please")) ? rb1.getString("ownershipprofile_please") : "Please save the Shareholder Profile after entering the data provided";
  String ownershipprofile_natural = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_natural")) ? rb1.getString("ownershipprofile_natural") : "How many natural person (not corporate) own more than 25% of the shares in your business?";
  String ownershipprofile_Select_Count = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Select_Count")) ? rb1.getString("ownershipprofile_Select_Count") : "Select Count";
  String ownershipprofile_1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_1")) ? rb1.getString("ownershipprofile_1") : "1";
  String ownershipprofile_2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_2")) ? rb1.getString("ownershipprofile_2") : "2";
  String ownershipprofile_3 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_3")) ? rb1.getString("ownershipprofile_3") : "3";
  String ownershipprofile_4 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_4")) ? rb1.getString("ownershipprofile_4") : "4";
  String ownershipprofile_Title = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Title")) ? rb1.getString("ownershipprofile_Title") : "Title*";
  String ownershipprofile_Select_Title = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Select_Title")) ? rb1.getString("ownershipprofile_Select_Title") : "Select Title";
  String ownershipprofile_MR = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_MR")) ? rb1.getString("ownershipprofile_MR") : "MR";
  String ownershipprofile_MRS = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_MRS")) ? rb1.getString("ownershipprofile_MRS") : "MRS";
  String ownershipprofile_MS = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_MS")) ? rb1.getString("ownershipprofile_MS") : "MS";
  String ownershipprofile_MISS = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_MISS")) ? rb1.getString("ownershipprofile_MISS") : "MISS";
  String ownershipprofile_MASTER = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_MASTER")) ? rb1.getString("ownershipprofile_MASTER") : "MASTER";
  String ownershipprofile_DR = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_DR")) ? rb1.getString("ownershipprofile_DR") : "DR";
  String ownershipprofile_First = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_First")) ? rb1.getString("ownershipprofile_First") : "First";
  String ownershipprofile_Name = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Name")) ? rb1.getString("ownershipprofile_Name") : "Name*";
  String ownershipprofile_Last = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Last")) ? rb1.getString("ownershipprofile_Last") : "Last";
  String ownershipprofile_Date_Birth = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Date_Birth")) ? rb1.getString("ownershipprofile_Date_Birth") : "Date of Birth*";
  String ownershipprofile_Country_Code = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Country_Code")) ? rb1.getString("ownershipprofile_Country_Code") : "Country Code*";
  String ownershipprofile_Phone_No = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Phone_No")) ? rb1.getString("ownershipprofile_Phone_No") : "Phone No*";
  String ownershipprofile_Email_Address = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Email_Address")) ? rb1.getString("ownershipprofile_Email_Address") : "Email Address*";
  String ownershipprofile_percentage = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_percentage")) ? rb1.getString("ownershipprofile_percentage") : "Percentage(%)holding*";
  String ownershipprofile_Invalid_Percentage = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Invalid_Percentage")) ? rb1.getString("ownershipprofile_Invalid_Percentage") : "Invalid Percentage Holding";
  String ownershipprofile_Address_Details = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address_Details")) ? rb1.getString("ownershipprofile_Address_Details") : "Address Details";
  String ownershipprofile_Address_Proof = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address_Proof")) ? rb1.getString("ownershipprofile_Address_Proof") : "Address Proof";
  String ownershipprofile_Address_ID = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address_ID")) ? rb1.getString("ownershipprofile_Address_ID") : "Address ID";
  String ownershipprofile_Address = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address")) ? rb1.getString("ownershipprofile_Address") : "Address*";
  String ownershipprofile_Street = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Street")) ? rb1.getString("ownershipprofile_Street") : "Street*";
  String ownershipprofile_city = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_city")) ? rb1.getString("ownershipprofile_city") : "City/Town";
  String ownershipprofile_state = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_state")) ? rb1.getString("ownershipprofile_state") : "State/County of ID";
  String ownershipprofile_Country = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Country")) ? rb1.getString("ownershipprofile_Country") : "Country*";
  String ownershipprofile_zip = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_zip")) ? rb1.getString("ownershipprofile_zip") : "Zip/Postal Code";
  String ownershipprofile_Identification_Details = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_Details")) ? rb1.getString("ownershipprofile_Identification_Details") : "Identification Details";
  String ownershipprofile_Identification_Type = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_Type")) ? rb1.getString("ownershipprofile_Identification_Type") : "Identification Type*";
  String ownershipprofile_Identification_ID = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_ID")) ? rb1.getString("ownershipprofile_Identification_ID") : "Identification ID*";
  String ownershipprofile_Nationality = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Nationality")) ? rb1.getString("ownershipprofile_Nationality") : "Nationality";
  String ownershipprofile_isssuing = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_isssuing")) ? rb1.getString("ownershipprofile_isssuing") : "Issuing date of ID";
  String ownershipprofile_Expiry_date = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Expiry_date")) ? rb1.getString("ownershipprofile_Expiry_date") : "Expiry date of ID";
  String ownershipprofile_Politically = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Politically")) ? rb1.getString("ownershipprofile_Politically") : "Politically exposed person?";
  String ownershipprofile_existence = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_existence")) ? rb1.getString("ownershipprofile_existence") : "Existence of criminal record?";
  String ownershipprofile_Individual_Shareholder2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Individual_Shareholder2")) ? rb1.getString("ownershipprofile_Individual_Shareholder2") : "Individual Shareholder Profile 2";
  String ownershipprofile_Yes = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Yes")) ? rb1.getString("ownershipprofile_Yes") : "Yes";
  String ownershipprofile_No = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_No")) ? rb1.getString("ownershipprofile_No") : "No";
  String ownershipprofile_Individual_Shareholder3 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Individual_Shareholder3")) ? rb1.getString("ownershipprofile_Individual_Shareholder3") : "Individual Shareholder Profile 3";
  String ownershipprofile_Individual_Shareholder4 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Individual_Shareholder4")) ? rb1.getString("ownershipprofile_Individual_Shareholder4") : "Individual Shareholder Profile 4";
  String ownershipprofile_Corporate_Shareholder = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_Shareholder")) ? rb1.getString("ownershipprofile_Corporate_Shareholder") : "Corporate Shareholder Profile(Corporate Shareholder must equal 50% or more)\n";
  String ownershipprofile_Corporate_please = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_please")) ? rb1.getString("ownershipprofile_Corporate_please") : "Please save the Directors Profile after entering the data provided";
  String ownershipprofile_0 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_0")) ? rb1.getString("ownershipprofile_0") : "0";
  String ownershipprofile_Corporate_Shareholder1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_Shareholder1")) ? rb1.getString("ownershipprofile_Corporate_Shareholder1") : "Corporate Shareholder 1";
  String ownershipprofile_Registration_Number = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Registration_Number")) ? rb1.getString("ownershipprofile_Registration_Number") : "Registration Number";
  String ownershipprofile_Corporate_percentage = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_percentage")) ? rb1.getString("ownershipprofile_Corporate_percentage") : "Percentage(%)holding";
  String ownershipprofile_AddressDetails = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_AddressDetails")) ? rb1.getString("ownershipprofile_AddressDetails") : "Address Details";
  String ownershipprofile_AddressProof = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_AddressProof")) ? rb1.getString("ownershipprofile_AddressProof") : "Address Proof";
  String ownershipprofile_AddressID = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_AddressID")) ? rb1.getString("ownershipprofile_AddressID") : "Address ID";
  String ownershipprofile_Address1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address1")) ? rb1.getString("ownershipprofile_Address1") : "Address";
  String ownershipprofile_Street1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Street1")) ? rb1.getString("ownershipprofile_Street1") : "Street";
  String ownershipprofile_city1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_city1")) ? rb1.getString("ownershipprofile_city1") : "City/Town";
  String ownershipprofile_State = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_State")) ? rb1.getString("ownershipprofile_State") : "State/County/Province";
  String ownershipprofile_Country1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Country1")) ? rb1.getString("ownershipprofile_Country1") : "Country";
  String ownershipprofile_zip1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_zip1")) ? rb1.getString("ownershipprofile_zip1") : "Zip/Postal Code";
  String ownershipprofile_Identification_Details1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_Details1")) ? rb1.getString("ownershipprofile_Identification_Details1") : "Identification Details";
  String ownershipprofile_Identification_Type1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_Type1")) ? rb1.getString("ownershipprofile_Identification_Type1") : "Identification Type";
  String ownershipprofile_Identification_id1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_id1")) ? rb1.getString("ownershipprofile_Identification_id1") : "Identification ID";
  String ownershipprofile_Corporate_Shareholder2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_Shareholder2")) ? rb1.getString("ownershipprofile_Corporate_Shareholder2") : "Corporate Shareholder 2";
  String ownershipprofile_Corporate_Shareholder3 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_Shareholder3")) ? rb1.getString("ownershipprofile_Corporate_Shareholder3") : "Corporate Shareholder 3";
  String ownershipprofile_Corporate_Shareholder4 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_Shareholder4")) ? rb1.getString("ownershipprofile_Corporate_Shareholder4") : "Corporate Shareholder 4";
  String ownershipprofile_Corporate_profile2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_profile2")) ? rb1.getString("ownershipprofile_Corporate_profile2") : "Corporate Shareholder Profile 2";
  String ownershipprofile_Corporate_profile3 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_profile3")) ? rb1.getString("ownershipprofile_Corporate_profile3") : "Corporate Shareholder Profile 3";
  String ownershipprofile_Corporate_profile4 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Corporate_profile4")) ? rb1.getString("ownershipprofile_Corporate_profile4") : "Corporate Shareholder Profile 4";
  String ownershipprofile_director = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_director")) ? rb1.getString("ownershipprofile_director") : "Director's Profile";
  String ownershipprofile_director_please = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_director_please")) ? rb1.getString("ownershipprofile_director_please") : "Please save the Directors Profile after entering the data provided";
  String ownershipprofile_director_how = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_director_how")) ? rb1.getString("ownershipprofile_director_how") : "How many Directors do you have?";
  String ownershipprofile_director_profile1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_director_profile1")) ? rb1.getString("ownershipprofile_director_profile1") : "Directors Profile 1";
  String ownershipprofile_director_profile2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_director_profile2")) ? rb1.getString("ownershipprofile_director_profile2") : "Directors Profile 2";
  String ownershipprofile_Title1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Title1")) ? rb1.getString("ownershipprofile_Title1") : "Title";
  String ownershipprofile_First1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_First1")) ? rb1.getString("ownershipprofile_First1") : "First";
  String ownershipprofile_Name1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Name1")) ? rb1.getString("ownershipprofile_Name1") : "Name";
  String ownershipprofile_Last1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Last1")) ? rb1.getString("ownershipprofile_Last1") : "Last";
  String ownershipprofile_Date_Birth1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Date_Birth1")) ? rb1.getString("ownershipprofile_Date_Birth1") : "Date of Birth";
  String ownershipprofile_Country_Code1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Country_Code1")) ? rb1.getString("ownershipprofile_Country_Code1") : "Country Code";
  String ownershipprofile_Phone_No1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Phone_No1")) ? rb1.getString("ownershipprofile_Phone_No1") : "Phone No";
  String ownershipprofile_Email = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Email")) ? rb1.getString("ownershipprofile_Email") : "Email Address";
  String ownershipprofile_Percentage1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Percentage1")) ? rb1.getString("ownershipprofile_Percentage1") : "Percentage(%)holding";
  String ownershipprofile_Address_Details1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address_Details1")) ? rb1.getString("ownershipprofile_Address_Details1") : "Address Details";
  String ownershipprofile_Address_Proof1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address_Proof1")) ? rb1.getString("ownershipprofile_Address_Proof1") : "Address Proof";
  String ownershipprofile_Address_ID1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address_ID1")) ? rb1.getString("ownershipprofile_Address_ID1") : "Address ID";
  String ownershipprofile_Address2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Address2")) ? rb1.getString("ownershipprofile_Address2") : "Address";
  String ownershipprofile_Street2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Street2")) ? rb1.getString("ownershipprofile_Street2") : "Street";
  String ownershipprofile_City1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_City1")) ? rb1.getString("ownershipprofile_City1") : "City/Town";
  String ownershipprofile_state1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_state1")) ? rb1.getString("ownershipprofile_state1") : "State/County of ID";
  String ownershipprofile_Country2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Country2")) ? rb1.getString("ownershipprofile_Country2") : "Country";
  String ownershipprofile_Zip1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Zip1")) ? rb1.getString("ownershipprofile_Zip1") : "Zip/Postal Code";
  String ownershipprofile_Identification_Details2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_Details2")) ? rb1.getString("ownershipprofile_Identification_Details2") : "Identification Details";
  String ownershipprofile_Identification_Type2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_Type2")) ? rb1.getString("ownershipprofile_Identification_Type2") : "Identification Type";
  String ownershipprofile_Identification_ID2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Identification_ID2")) ? rb1.getString("ownershipprofile_Identification_ID2") : "Identification ID";
  String ownershipprofile_Nationality1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Nationality1")) ? rb1.getString("ownershipprofile_Nationality1") : "Nationality";
  String ownershipprofile_issuing1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_issuing1")) ? rb1.getString("ownershipprofile_issuing1") : "Issuing date of ID";
  String ownershipprofile_expiry1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_expiry1")) ? rb1.getString("ownershipprofile_expiry1") : "Expiry date of ID";
  String ownershipprofile_director_profile3 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_director_profile3")) ? rb1.getString("ownershipprofile_director_profile3") : "Directors Profile 3";
  String ownershipprofile_director_profile4 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_director_profile4")) ? rb1.getString("ownershipprofile_director_profile4") : "Directors Profile 4";
  String ownershipprofile_Authorize_Signatory = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Authorize_Signatory")) ? rb1.getString("ownershipprofile_Authorize_Signatory") : "Authorize Signatory Profile";
  String ownershipprofile_Authorize_How = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Authorize_How")) ? rb1.getString("ownershipprofile_Authorize_How") : "How many Authorised Signatories do you have?";
  String ownershipprofile_Authorize_please = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Authorize_please")) ? rb1.getString("ownershipprofile_Authorize_please") : "Please save Authorized Signatory after entering the data provided";
  String ownershipprofile_Authorize_Signatory1 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Authorize_Signatory1")) ? rb1.getString("ownershipprofile_Authorize_Signatory1") : "Authorize Signatory Profile 1";
  String ownershipprofile_Designation = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Designation")) ? rb1.getString("ownershipprofile_Designation") : "Designation";
  String ownershipprofile_Authorize_Signatory2 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Authorize_Signatory2")) ? rb1.getString("ownershipprofile_Authorize_Signatory2") : "Authorize Signatory Profile 2";
  String ownershipprofile_Authorize_Signatory3 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Authorize_Signatory3")) ? rb1.getString("ownershipprofile_Authorize_Signatory3") : "Authorize Signatory Profile 3";
  String ownershipprofile_Authorize_Signatory4 = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Authorize_Signatory4")) ? rb1.getString("ownershipprofile_Authorize_Signatory4") : "Authorize Signatory Profile 4";
  String ownershipprofile_Ownership_Profile = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Ownership_Profile")) ? rb1.getString("ownershipprofile_Ownership_Profile") : "Ownership Profile";
  String ownershipprofile_Profile = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_Profile")) ? rb1.getString("ownershipprofile_Profile") : "Profile";
  String ownershipprofile_details = StringUtils.isNotEmpty(rb1.getString("ownershipprofile_details")) ? rb1.getString("ownershipprofile_details") : "There is no details that has to be provided for this profile";

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
  if(ownershipProfileInputList.contains(BankInputName.directorsprofile_title)|| ownershipProfileInputList.contains(BankInputName.directorsprofile) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_lastname) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_address) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_city) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_zip) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_country) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_street) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_telephonenumber) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_identificationtypeselect)||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_State) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_nationality) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_Passportexpirydate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile_criminalrecord) ||

          ownershipProfileInputList.contains(BankInputName.directorsprofile2_title) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_lastname) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_address) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_city) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_zip) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_country) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_street) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_telephonenumber) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_identificationtypeselect) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_State) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_nationality) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_Passportexpirydate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile2_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_criminalrecord) ||

          ownershipProfileInputList.contains(BankInputName.directorsprofile3_title)||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_lastname) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_address) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_city) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_zip) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_country) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_street) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_telephonenumber)||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_identificationtypeselect) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_State) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_nationality) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_Passportexpirydate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile3_criminalrecord)||

          ownershipProfileInputList.contains(BankInputName.directorsprofile4_title)||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_lastname) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_address) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_city) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_dateofbirth) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_zip) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_country) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_street) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_telnocc1) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_telephonenumber)||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_emailaddress) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_identificationtypeselect) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_State) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_nationality) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_passportissuedate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_Passportexpirydate) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_politicallyexposed) ||
          ownershipProfileInputList.contains(BankInputName.directorsprofile4_criminalrecord) ||view)
  {
%>

<div class="content" style="margin-top: 0;">
  <!-- Page Heading Start -->
  <div class="page-heading">

    <div class="row">
      <div class="col-sm-12 portlets ui-sortable">
        <div class="widget" style="margin-bottom: 0;">
          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownershipprofile_director%></strong></h2>
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
                    <center><h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;">
                      <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownershipprofile_director_please%></h5></center>

                    <div class="col-sm-4 portlets ui-sortable">
                      <label for="numOfDirectors" style="font-family:Open Sans;font-size: 13px;font-weight: 600;margin-left: 10%;"><%=ownershipprofile_director_how%></label>
                    </div>
                    <div class="col-sm-3 portlets ui-sortable">
                      <%--<input type="text" class="form-control"  id="numOfDirectors" name="numOfDirectors" onkeypress="return isNumber(event)" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfDirectors())?ownershipProfileVO.getNumOfDirectors():"1"%>"/><%if(validationErrorList.getError("numOfDirectors")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>--%>
                      <select name="numOfDirectors" id="numOfDirectors"  value="">
                        <option value=""><%=ownershipprofile_Select_Count%></option>
                        <option value="1"><%=ownershipprofile_1%></option>
                        <option value="2"><%=ownershipprofile_2%></option>
                        <option value="3"><%=ownershipprofile_3%></option>
                        <option value="4"><%=ownershipprofile_4%></option>

                      </select>
                      <input value='<%=functions.isValueNull(ownershipProfileVO.getNumOfDirectors())?ownershipProfileVO.getNumOfDirectors():"1"%>' id="getcountvalDir" type="hidden">
                      <script>

                        var countryval = document.getElementById('getcountvalDir').value;
                        $('[name=numOfDirectors] option').filter(function ()
                        {
                          if (countryval == '1')
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
                    <div class="dir1">
                      <h2 class="col-md-12 background panelheading_color headpanelfont_color"  style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_director_profile1%></h2>

                      <div class="form-group col-md-2 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title%></label>
                        <select class="form-control" id="directorsprofile_title" name="directorsprofile_title" <%=globaldisabled%>>
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("directorsprofile_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First%>&nbsp;<%=ownershipprofile_Name%></label>
                        <input type="text" class="form-control"  id="directorsprofile" name="directorsprofile"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getFirstname()) ? ownershipProfileDetailsVO_director1.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last%>&nbsp;<%=ownershipprofile_Name%></label>
                        <input type="text" class="form-control"  id="directorsprofile_lastname" name="directorsprofile_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getLastname()) ? ownershipProfileDetailsVO_director1.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile_dateofbirth" name="directorsprofile_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code%></label>
                        <input type="text" class="form-control"  id="directorsprofile_telnocc1" name="directorsprofile_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getTelnocc1()) ? ownershipProfileDetailsVO_director1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No%></label>
                        <input type="text" class="form-control"  id="directorsprofile_telephonenumber" name="directorsprofile_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getTelephonenumber()) ? ownershipProfileDetailsVO_director1.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email_Address%></label>
                        <input type="text" class="form-control"  id="directorsprofile_emailaddress" name="directorsprofile_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getEmailaddress()) ? ownershipProfileDetailsVO_director1.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_percentage%></label>
                        <input type="text" class="form-control"  id="directorsprofile_owned" name="directorsprofile_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getOwned()) ? ownershipProfileDetailsVO_director1.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myDirector(this.value,'directorsprofile2_owned','directorsprofile3_owned','directorsprofile4_owned',1)"/><%if(validationErrorList.getError("directorsprofile_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof%></label>
                        <select class="form-control" name="directorsprofile_addressproof" id="directorsprofile_addressproof"class="form-control" <%=globaldisabled%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director1.getAddressProof()) ? ownershipProfileDetailsVO_director1.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID%></label>
                        <input type="text" class="form-control"  id="directorsprofile_addressId" name="directorsprofile_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getAddressId()) ? ownershipProfileDetailsVO_director1.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address%></label>
                        <input type="text" class="form-control"  id="directorsprofile_address" name="directorsprofile_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getAddress()) ? ownershipProfileDetailsVO_director1.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street%></label>
                        <input type="text" class="form-control"  id="directorsprofile_street" name="directorsprofile_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getStreet()) ? ownershipProfileDetailsVO_director1.getStreet():""%>"/><%if(validationErrorList.getError("directorsprofile_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city%></label>
                        <input type="text" class="form-control"  id="directorsprofile_city" name="directorsprofile_city" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getCity()) ? ownershipProfileDetailsVO_director1.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state%></label>
                        <input type="text" class="form-control"  id="directorsprofile_State" name="directorsprofile_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getState()) ? ownershipProfileDetailsVO_director1.getState():""%>"/><%if(validationErrorList.getError("directorsprofile_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country%></label>
                        <select  name="directorsprofile_country" id="directorsprofile_country" <%=globaldisabled%> onchange="myjunk1('directorsprofile_country','directorsprofile_telnocc1');"  class="form-control">
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director1.getCountry()) ? ownershipProfileDetailsVO_director1.getCountry():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip%></label>
                        <input type="text" class="form-control"  id="directorsprofile_zip" name="directorsprofile_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getZipcode()) ? ownershipProfileDetailsVO_director1.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type%></label>
                        <select class="form-control" name="directorsprofile_identificationtypeselect" id="directorsprofile_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director1.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID%></label>
                        <input type="text" class="form-control"  id="directorsprofile_identificationtype" name="directorsprofile_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getIdentificationtype()) ? ownershipProfileDetailsVO_director1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>


                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality%></label>
                        <select  class="form-control" id="directorsprofile_nationality" name="directorsprofile_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director1.getNationality()) ? ownershipProfileDetailsVO_director1.getNationality():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_isssuing%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile_passportissuedate" name="directorsprofile_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Expiry_date%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile_Passportexpirydate" name="directorsprofile_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile_politicallyexposed" name="directorsprofile_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile_criminalrecord" name="directorsprofile_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                    </div>
                    <div class="dir2">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_director_profile2%></h2>

                      <div class="form-group col-md-2 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title1%></label>
                        <select class="form-control" id="directorsprofile2_title" name="directorsprofile2_title" <%=globaldisabled%> <%=disableDirector2%>>
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("directorsprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2" name="directorsprofile2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getFirstname()) ? ownershipProfileDetailsVO_director2.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_lastname" name="directorsprofile2_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getLastname()) ? ownershipProfileDetailsVO_director2.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile2_dateofbirth" name="directorsprofile2_dateofbirth" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile2_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_telnocc1" name="directorsprofile2_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getTelnocc1()) ? ownershipProfileDetailsVO_director2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile2_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_telephonenumber" name="directorsprofile2_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getTelephonenumber()) ? ownershipProfileDetailsVO_director2.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_emailaddress" name="directorsprofile2_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getEmailaddress()) ? ownershipProfileDetailsVO_director2.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Percentage1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_owned" name="directorsprofile2_owned" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getOwned()) ? ownershipProfileDetailsVO_director2.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myDirector(this.value,'directorsprofile_owned','directorsprofile3_owned','directorsprofile4_owned',2)"/><%if(validationErrorList.getError("directorsprofile2_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof1%></label>
                        <select class="form-control" name="directorsprofile2_addressproof" id="directorsprofile2_addressproof"class="form-control" <%=globaldisabled%> <%=disableDirector2%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director2.getAddressProof()) ? ownershipProfileDetailsVO_director2.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_addressId" name="directorsprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getAddressId()) ? ownershipProfileDetailsVO_director2.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address2%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_address" name="directorsprofile2_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getAddress()) ? ownershipProfileDetailsVO_director2.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street2%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_street" name="directorsprofile2_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getStreet()) ? ownershipProfileDetailsVO_director2.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_City1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_city" name="directorsprofile2_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getCity()) ? ownershipProfileDetailsVO_director2.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_State" name="directorsprofile2_State"style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%> <%=disableDirector2%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getState()) ? ownershipProfileDetailsVO_director2.getState():""%>"/><%if(validationErrorList.getError("directorsprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country2%></label>
                        <select id="directorsprofile2_country" name="directorsprofile2_country" onchange="myjunk1('directorsprofile2_country','directorsprofile2_telnocc1');" class="form-control" <%=globaldisabled%> <%=disableDirector2%> >
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director2.getCountry()) ? ownershipProfileDetailsVO_director2.getCountry():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Zip1%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_zip" name="directorsprofile2_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getZipcode()) ? ownershipProfileDetailsVO_director2.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details2%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type2%></label>
                        <select class="form-control" name="directorsprofile2_identificationtypeselect" id="directorsprofile2_identificationtypeselect" <%=globaldisabled%> <%=disableDirector2%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director2.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID2%></label>
                        <input type="text" class="form-control"  id="directorsprofile2_identificationtype" name="directorsprofile2_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getIdentificationtype()) ? ownershipProfileDetailsVO_director2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality1%></label>
                        <select  class="form-control" id="directorsprofile2_nationality" name="directorsprofile2_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableDirector2%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director2.getNationality()) ? ownershipProfileDetailsVO_director2.getNationality():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_issuing1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile2_passportissuedate" name="directorsprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector2%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_expiry1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile2_Passportexpirydate" name="directorsprofile2_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector2%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile2_politicallyexposed" name="directorsprofile2_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile2_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile2_criminalrecord" name="directorsprofile2_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director2.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile2_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director2.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                    </div>
                    <div class="dir3">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_director_profile3%></h2>

                      <div class="form-group col-md-2 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title1%></label>
                        <select class="form-control" id="directorsprofile3_title" name="directorsprofile3_title" <%=globaldisabled%> <%=disableDirector3%>>
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("directorsprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3" name="directorsprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getFirstname()) ? ownershipProfileDetailsVO_director3.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_lastname" name="directorsprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getLastname()) ? ownershipProfileDetailsVO_director3.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile3_dateofbirth" name="directorsprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile3_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_telnocc1" name="directorsprofile3_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getTelnocc1()) ? ownershipProfileDetailsVO_director3.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile3_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_telephonenumber" name="directorsprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getTelephonenumber()) ? ownershipProfileDetailsVO_director3.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_emailaddress" name="directorsprofile3_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getEmailaddress()) ? ownershipProfileDetailsVO_director3.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Percentage1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_owned" name="directorsprofile3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getOwned()) ? ownershipProfileDetailsVO_director3.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myDirector(this.value,'directorsprofile_owned','directorsprofile2_owned','directorsprofile4_owned',3)"/><%if(validationErrorList.getError("directorsprofile3_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof1%></label>
                        <select class="form-control" name="directorsprofile3_addressproof" id="directorsprofile3_addressproof"class="form-control" <%=globaldisabled%> <%=disableDirector3%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director3.getAddressProof()) ? ownershipProfileDetailsVO_director3.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_addressId" name="directorsprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getAddressId()) ? ownershipProfileDetailsVO_director3.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address2%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_address" name="directorsprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getAddress()) ? ownershipProfileDetailsVO_director3.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street2%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_street" name="directorsprofile3_street" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getStreet()) ? ownershipProfileDetailsVO_director3.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_City1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_city" name="directorsprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getCity()) ? ownershipProfileDetailsVO_director3.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#4984a9"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_State" name="directorsprofile3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getState()) ? ownershipProfileDetailsVO_director3.getState():""%>"/><%if(validationErrorList.getError("directorsprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country2%></label>
                        <select id="directorsprofile3_country" <%=globaldisabled%> <%=disableDirector3%> name="directorsprofile3_country" onchange="myjunk1('directorsprofile3_country','directorsprofile3_telnocc1');"  class="form-control">
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director3.getCountry()) ? ownershipProfileDetailsVO_director3.getCountry():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Zip1%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_zip" name="directorsprofile3_zip" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getZipcode()) ? ownershipProfileDetailsVO_director3.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details2%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type2%></label>
                        <select class="form-control" name="directorsprofile3_identificationtypeselect" id="directorsprofile3_identificationtypeselect"class="txtbox" <%=globaldisabled%> <%=disableDirector3%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director3.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID2%></label>
                        <input type="text" class="form-control"  id="directorsprofile3_identificationtype" name="directorsprofile3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getIdentificationtype()) ? ownershipProfileDetailsVO_director3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>



                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality1%></label>
                        <select  class="form-control" id="directorsprofile3_nationality" name="directorsprofile3_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableDirector3%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director3.getNationality()) ? ownershipProfileDetailsVO_director3.getNationality():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_issuing1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile3_passportissuedate" name="directorsprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_expiry1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile3_Passportexpirydate" name="directorsprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile3_politicallyexposed" name="directorsprofile3_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile3_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile3_criminalrecord" name="directorsprofile3_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile3_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director3.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                    </div>

                    <!--director 4-->

                    <div class="dir4">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_director_profile4%></h2>

                      <div class="form-group col-md-2 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title1%></label>
                        <select class="form-control" id="directorsprofile4_title" name="directorsprofile4_title" <%=globaldisabled%> <%=disableDirector4%>>
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("directorsprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4" name="directorsprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getFirstname()) ? ownershipProfileDetailsVO_director4.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-3 has-feedback">
                        <label for="directorsprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_lastname" name="directorsprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getLastname()) ? ownershipProfileDetailsVO_director4.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile4_dateofbirth" name="directorsprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile4_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_telnocc1" name="directorsprofile4_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getTelnocc1()) ? ownershipProfileDetailsVO_director4.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile4_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="directorsprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_telephonenumber" name="directorsprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getTelephonenumber()) ? ownershipProfileDetailsVO_director4.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_emailaddress" name="directorsprofile4_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getEmailaddress()) ? ownershipProfileDetailsVO_director4.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Percentage1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_owned" name="directorsprofile4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getOwned()) ? ownershipProfileDetailsVO_director4.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myDirector(this.value,'directorsprofile_owned','directorsprofile2_owned','directorsprofile3_owned',4)"/><%if(validationErrorList.getError("directorsprofile4_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof1%></label>
                        <select class="form-control" name="directorsprofile4_addressproof" id="directorsprofile4_addressproof"class="form-control" <%=globaldisabled%> <%=disableDirector4%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director4.getAddressProof()) ? ownershipProfileDetailsVO_director4.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_addressId" name="directorsprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getAddressId()) ? ownershipProfileDetailsVO_director4.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address2%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_address" name="directorsprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getAddress()) ? ownershipProfileDetailsVO_director4.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street2%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_street" name="directorsprofile4_street" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getStreet()) ? ownershipProfileDetailsVO_director4.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_City1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_city" name="directorsprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getCity()) ? ownershipProfileDetailsVO_director4.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#4984a9"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_State" name="directorsprofile4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getState()) ? ownershipProfileDetailsVO_director4.getState():""%>"/><%if(validationErrorList.getError("directorsprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country2%></label>
                        <select id="directorsprofile4_country" <%=globaldisabled%> <%=disableDirector4%> name="directorsprofile4_country" onchange="myjunk1('directorsprofile4_country','directorsprofile4_telnocc1');"  class="form-control">
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director4.getCountry()) ? ownershipProfileDetailsVO_director4.getCountry():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Zip1%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_zip" name="directorsprofile4_zip" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getZipcode()) ? ownershipProfileDetailsVO_director4.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details2%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type2%></label>
                        <select class="form-control" name="directorsprofile4_identificationtypeselect" id="directorsprofile4_identificationtypeselect"class="txtbox" <%=globaldisabled%> <%=disableDirector4%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director4.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("directorsprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID2%></label>
                        <input type="text" class="form-control"  id="directorsprofile4_identificationtype" name="directorsprofile4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getIdentificationtype()) ? ownershipProfileDetailsVO_director4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>



                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality1%></label>
                        <select  class="form-control" id="directorsprofile4_nationality" name="directorsprofile4_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director4.getNationality()) ? ownershipProfileDetailsVO_director4.getNationality():"")%>
                        </select><%if(validationErrorList.getError("directorsprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_issuing1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile4_passportissuedate" name="directorsprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="directorsprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_expiry1%></label>
                        <input type="text" class="form-control datepicker"  id="directorsprofile4_Passportexpirydate" name="directorsprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile4_politicallyexposed" name="directorsprofile4_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director4.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile4_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="directorsprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="directorsprofile4_criminalrecord" name="directorsprofile4_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="directorsprofile4_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director4.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
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
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownershipprofile_Authorize_Signatory%></strong></h2>
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
                    <center><h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"> <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownershipprofile_Authorize_please%></h5></center>

                    <div class="col-sm-5 portlets ui-sortable">
                      <label for="numOfAuthrisedSignatory" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Authorize_How%></label>
                    </div>
                    <div class="col-sm-4 portlets ui-sortable">
                      <%--<input type="text" class="form-control"  id="numOfAuthrisedSignatory" name="numOfAuthrisedSignatory" onkeypress="return isNumber(event)" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfAuthrisedSignatory())?ownershipProfileVO.getNumOfAuthrisedSignatory():"1"%>"/><%if(validationErrorList.getError("numOfAuthrisedSignatory")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>--%>
                      <select name="numOfAuthrisedSignatory" id="numOfAuthrisedSignatory"  value="">
                        <option value=""><%=ownershipprofile_Select_Count%></option>
                        <option value="0"><%=ownershipprofile_0%></option>
                        <option value="1"><%=ownershipprofile_1%></option>
                        <option value="2"><%=ownershipprofile_2%></option>
                        <option value="3"><%=ownershipprofile_3%></option>
                        <option value="4"><%=ownershipprofile_4%></option>

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
                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Authorize_Signatory1%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title1%></label>
                        <select class="form-control" id="authorizedsignatoryprofile_title" name="authorizedsignatoryprofile_title" <%=globaldisabled%> >
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory1.getTitle())?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile" name="authorizedsignatoryprofile"style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory1.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_lastname" name="authorizedsignatoryprofile_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory1.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_dateofbirth" name="authorizedsignatoryprofile_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile1_designation" name="authorizedsignatoryprofile1_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory1.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile1_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_telnocc1" name="authorizedsignatoryprofile_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_telephonenumber" name="authorizedsignatoryprofile_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory1.getTelephonenumber():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_emailaddress" name="authorizedsignatoryprofile_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory1.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile1_owned" name="authorizedsignatoryprofile1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory1.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile2_owned','authorizedsignatoryprofile3_owned','authorizedsignatoryprofile4_owned',1)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile1_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile1_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile1_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile1_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile1_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>



                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile1_addressproof" id="authorizedsignatoryprofile1_addressproof"class="form-control" <%=globaldisabled%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile1_addressId" name="authorizedsignatoryprofile1_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_address" name="authorizedsignatoryprofile_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory1.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_street" name="authorizedsignatoryprofile_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory1.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_City1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_city" name="authorizedsignatoryprofile_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory1.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_State" name="authorizedsignatoryprofile_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getState()) ? ownershipProfileDetailsVO_authorizeSignatory1.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country2%></label>
                        <select id="authorizedsignatoryprofile_country" <%=globaldisabled%> name="authorizedsignatoryprofile_country" onchange="myjunk1('authorizedsignatoryprofile_country','authorizedsignatoryprofile_telnocc1');" class="form-control" >
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory1.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Zip1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_zip" name="authorizedsignatoryprofile_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory1.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details2%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type2%></label>
                        <select class="form-control" id="authorizedsignatoryprofile_identificationtypeselect" name="authorizedsignatoryprofile_identificationtypeselect" id="authorizedsignatoryprofile_identificationtypeselect" <%=globaldisabled%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile_identificationtype" name="authorizedsignatoryprofile_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality1%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile_nationality" name="authorizedsignatoryprofile_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory1.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_issuing1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_passportissuedate" name="authorizedsignatoryprofile_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_expiry1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile_Passportexpirydate" name="authorizedsignatoryprofile_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile1_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile1_politicallyexposed" name="authorizedsignatoryprofile1_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile1_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile1_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile1_criminalrecord" name="authorizedsignatoryprofile1_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile1_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory1.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                    </div>
                    <div class="authorized2">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Authorize_Signatory2%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title1%></label>
                        <select class="form-control" id="authorizedsignatoryprofile2_title" name="authorizedsignatoryprofile2_title" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory2.getTitle())?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2" name="authorizedsignatoryprofile2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull((ownershipProfileDetailsVO_authorizeSignatory2.getFirstname())) ? ownershipProfileDetailsVO_authorizeSignatory2.getFirstname():""%>"  /><%if(validationErrorList.getError("authorizedsignatoryprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_lastname" name="authorizedsignatoryprofile2_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory2.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_dateofbirth" name="authorizedsignatoryprofile2_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_designation" name="authorizedsignatoryprofile2_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory2.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile2_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_telnocc1" name="authorizedsignatoryprofile2_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_telephonenumber" name="authorizedsignatoryprofile2_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_emailaddress" name="authorizedsignatoryprofile2_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory2.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile2_owned" name="authorizedsignatoryprofile2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory2.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile1_owned','authorizedsignatoryprofile3_owned','authorizedsignatoryprofile4_owned',2)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile2_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile2_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile2_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile2_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile2_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile2_addressproof" id="authorizedsignatoryprofile2_addressproof"class="form-control" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_addressId" name="authorizedsignatoryprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_address" name="authorizedsignatoryprofile2_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory2.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_street" name="authorizedsignatoryprofile2_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory2.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_City1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_city" name="authorizedsignatoryprofile2_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory2.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_State" name="authorizedsignatoryprofile2_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getState()) ? ownershipProfileDetailsVO_authorizeSignatory2.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country2%></label>
                        <select id="authorizedsignatoryprofile2_country" name="authorizedsignatoryprofile2_country" onchange="myjunk1('authorizedsignatoryprofile2_country','authorizedsignatoryprofile2_telnocc1');" class="form-control" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory2.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Zip1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_zip" name="authorizedsignatoryprofile2_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory2.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details2%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type2%></label>
                        <select class="form-control" name="authorizedsignatoryprofile2_identificationtypeselect" id="authorizedsignatoryprofile2_identificationtypeselect" <%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>

                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile2_identificationtype" name="authorizedsignatoryprofile2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality1%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile2_nationality" name="authorizedsignatoryprofile2_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableAuthSignatory2%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory2.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_issuing1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_passportissuedate" name="authorizedsignatoryprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_expiry1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile2_Passportexpirydate" name="authorizedsignatoryprofile2_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory2.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile2_politicallyexposed" name="authorizedsignatoryprofile2_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile2_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile2_criminalrecord" name="authorizedsignatoryprofile2_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory2%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile2_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory2.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                    </div>
                    <div class="authorized3">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Authorize_Signatory3%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title1%></label>
                        <select class="form-control" id="authorizedsignatoryprofile3_title" name="authorizedsignatoryprofile3_title" <%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3" name="authorizedsignatoryprofile3" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory3.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_lastname" name="authorizedsignatoryprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory3.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_dateofbirth" name="authorizedsignatoryprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_designation" name="authorizedsignatoryprofile3_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory3.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile3_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_telnocc1" name="authorizedsignatoryprofile3_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory3.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_telephonenumber" name="authorizedsignatoryprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory3.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_emailaddress" name="authorizedsignatoryprofile3_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory3.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile3_owned" name="authorizedsignatoryprofile3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory3.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile1_owned','authorizedsignatoryprofile2_owned','authorizedsignatoryprofile4_owned',3)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile3_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile3_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile3_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile3_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile3_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile3_addressproof" id="authorizedsignatoryprofile3_addressproof"class="form-control" <%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_addressId" name="authorizedsignatoryprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_address" name="authorizedsignatoryprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory3.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_street" name="authorizedsignatoryprofile3_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory3.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_City1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_city" name="authorizedsignatoryprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory3.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_State" name="authorizedsignatoryprofile3_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getState()) ? ownershipProfileDetailsVO_authorizeSignatory3.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country2%></label>
                        <select id="authorizedsignatoryprofile3_country" name="authorizedsignatoryprofile3_country" onchange="myjunk1('authorizedsignatoryprofile3_country','authorizedsignatoryprofile3_telnocc1');"  class="form-control" <%=globaldisabled%> >
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory3.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Zip1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_zip" name="authorizedsignatoryprofile3_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory3.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details2%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type2%></label>
                        <select class="form-control" name="authorizedsignatoryprofile3_identificationtypeselect" id="authorizedsignatoryprofile3_identificationtypeselect" <%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile3_identificationtype" name="authorizedsignatoryprofile3_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality1%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile3_nationality" name="authorizedsignatoryprofile3_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableAuthSignatory3%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory3.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_issuing1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_passportissuedate" name="authorizedsignatoryprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_expiry1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile3_Passportexpirydate" name="authorizedsignatoryprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory3.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile3_politicallyexposed" name="authorizedsignatoryprofile3_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile3_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile3_criminalrecord" name="authorizedsignatoryprofile3_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory3%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile3_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory3.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                    </div>

                    <!--Authirise signatory 4 -->

                    <div class="authorized4">

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Authorize_Signatory4%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title1%></label>
                        <select class="form-control" id="authorizedsignatoryprofile4_title" name="authorizedsignatoryprofile4_title" <%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <option value=""><%=ownershipprofile_Select_Title%></option>
                          <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownershipprofile_MR%></option>
                          <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownershipprofile_MRS%></option>
                          <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownershipprofile_MS%></option>
                          <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownershipprofile_MISS%></option>
                          <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_authorizeSignatory4.getTitle())?"selected":""%>><%=ownershipprofile_MASTER%></option>
                          <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_authorizeSignatory3.getTitle())?"selected":""%>><%=ownershipprofile_DR%></option>

                        </select> <%if(validationErrorList.getError("authorizedsignatoryprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4" name="authorizedsignatoryprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getFirstname()) ? ownershipProfileDetailsVO_authorizeSignatory4.getFirstname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last1%>&nbsp;<%=ownershipprofile_Name1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_lastname" name="authorizedsignatoryprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getLastname()) ? ownershipProfileDetailsVO_authorizeSignatory4.getLastname():""%>" /><%if(validationErrorList.getError("authorizedsignatoryprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_dateofbirth" name="authorizedsignatoryprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getDateofbirth()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Designation%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_designation" name="authorizedsignatoryprofile4_designation"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getDesignation()) ? ownershipProfileDetailsVO_authorizeSignatory4.getDesignation():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile4_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_telnocc1" name="authorizedsignatoryprofile4_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getTelnocc1()) ? ownershipProfileDetailsVO_authorizeSignatory4.getTelnocc1():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-2 has-feedback">
                        <label for="authorizedsignatoryprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_telephonenumber" name="authorizedsignatoryprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getTelephonenumber()) ? ownershipProfileDetailsVO_authorizeSignatory4.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_emailaddress" name="authorizedsignatoryprofile4_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getEmailaddress()) ? ownershipProfileDetailsVO_authorizeSignatory4.getEmailaddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Percentage1%></label>
                        <div class="input-group">
                          <input type="text" class="form-control"  id="authorizedsignatoryprofile4_owned" name="authorizedsignatoryprofile4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getOwned()) ? ownershipProfileDetailsVO_authorizeSignatory4.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myAuthSignatory(this.value,'authorizedsignatoryprofile1_owned','authorizedsignatoryprofile2_owned','authorizedsignatoryprofile3_owned',4)"/>
                          <span class="input-group-addon" style="font-weight: 800;">%</span>
                          <%if(validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                          <span style="<%=validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile4_owned"))?"background-color: #f2dede;":""%>"class="errormesage" id="authorizedsignatoryprofile4_owned"><%if(validationErrorList.getError("authorizedsignatoryprofile4_owned")!=null && functions.isValueNull(request.getParameter("authorizedsignatoryprofile4_owned"))){%><%=validationErrorList.getError("authorizedsignatoryprofile4_owned").getLogMessage()%><%}%></span>
                        </div>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details1%></h2>
                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof1%></label>
                        <select class="form-control" name="authorizedsignatoryprofile4_addressproof" id="authorizedsignatoryprofile4_addressproof"class="form-control" <%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <%
                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddressProof()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddressProof() : ""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_addressId" name="authorizedsignatoryprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddressId()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddressId():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_address" name="authorizedsignatoryprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getAddress()) ? ownershipProfileDetailsVO_authorizeSignatory4.getAddress():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_street" name="authorizedsignatoryprofile4_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getStreet()) ? ownershipProfileDetailsVO_authorizeSignatory4.getStreet():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_City1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_city" name="authorizedsignatoryprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCity()) ? ownershipProfileDetailsVO_authorizeSignatory4.getCity():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_State" name="authorizedsignatoryprofile4_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getState()) ? ownershipProfileDetailsVO_authorizeSignatory4.getState():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>
                      <div class="form-group col-md-4 has-feedback">
                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country2%></label>
                        <select id="authorizedsignatoryprofile4_country" name="authorizedsignatoryprofile4_country" onchange="myjunk1('authorizedsignatoryprofile4_country','authorizedsignatoryprofile4_telnocc1');"  class="form-control" <%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCountry()) ? ownershipProfileDetailsVO_authorizeSignatory4.getCountry():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Zip1%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_zip" name="authorizedsignatoryprofile4_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getZipcode()) ? ownershipProfileDetailsVO_authorizeSignatory4.getZipcode():""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details2%></h2>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type2%></label>
                        <select class="form-control" name="authorizedsignatoryprofile4_identificationtypeselect" id="authorizedsignatoryprofile4_identificationtypeselect" <%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <%
                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtypeselect():""));
                          %>
                        </select>
                        <%if(validationErrorList.getError("authorizedsignatoryprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID2%></label>
                        <input type="text" class="form-control"  id="authorizedsignatoryprofile4_identificationtype" name="authorizedsignatoryprofile4_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtype()) ? ownershipProfileDetailsVO_authorizeSignatory4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("authorizedsignatoryprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality1%></label>
                        <select  class="form-control" id="authorizedsignatoryprofile4_nationality" name="authorizedsignatoryprofile4_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableAuthSignatory4%>>
                          <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getNationality()) ? ownershipProfileDetailsVO_authorizeSignatory4.getNationality():"")%>
                        </select><%if(validationErrorList.getError("authorizedsignatoryprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_issuing1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_passportissuedate" name="authorizedsignatoryprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                      </div>

                      <div class="form-group col-md-4 has-feedback">
                        <label for="authorizedsignatoryprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_expiry1%></label>
                        <input type="text" class="form-control datepicker"  id="authorizedsignatoryprofile4_Passportexpirydate" name="authorizedsignatoryprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableAuthSignatory4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_authorizeSignatory4.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("authorizedsignatoryprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                      </div>

                      <div class="form-group col-md-12 has-feedback"></div>

                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile4_politicallyexposed" name="authorizedsignatoryprofile4_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile4_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                      </div>
                      <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                        <label for="authorizedsignatoryprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                        <font class="radio_align"><input type="radio" id="authorizedsignatoryprofile4_criminalrecord" name="authorizedsignatoryprofile4_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableAuthSignatory4%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                          <input type="radio" name="authorizedsignatoryprofile4_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_authorizeSignatory4.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
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