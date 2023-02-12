<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileDetailsVO" %>
<%@ page import="com.vo.applicationManagerVOs.OwnershipProfileVO" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/15
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<script src="/partner/javascript/ownershipdetails.js"></script>
<script type="text/javascript">
  $('#sandbox-container input').datepicker({
  });
</script>
<script>
  $(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
  });
</script>

<style type="text/css">
  .glyphicon{
    display: block!important;
    color:#a94442!important;
    background-color: #ebccd1!important;
    width: 40px!important;
    margin-right: 16px!important;
    height: 32px!important;
    top: inherit!important;
    margin-top: -33px!important;
  }
  .errormesage{
    background-color: transparent!important;
    color: red;
    display: table-footer-group;
  }

  @media(max-width: 540px){
    .radio_align{
      display: inherit;
    }
  }

  .panelheading_color{
    background-color: #7eccad;
  }

</style>

<script>



</script>
<script>
  function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }

</script>
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


<div class="content-page" id="ownershipid">

  <%--Shareholders information--%>
  <%
    if(ownershipProfileInputList.contains(BankInputName.shareholderprofile1_title) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_lastname) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_owned) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_address) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile1_city) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_dateofbirth) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_zip) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_country) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_street) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile1_telnocc1) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_telephonenumber) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_emailaddress) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_identificationtype) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile1_State) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_nationality) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_Passportexpirydate) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_passportissuedate) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile1_politicallyexposed) || ownershipProfileInputList.contains(BankInputName.shareholderprofile1_criminalrecord) ||

            ownershipProfileInputList.contains(BankInputName.shareholderprofile2_title) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_lastname) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_owned) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile2_address) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_city) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_dateofbirth) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_zip) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile2_country) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_street) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_telnocc2) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_telephonenumber) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile2_emailaddress) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_identificationtype) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile2_State) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_nationality) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_Passportexpirydate) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_passportissuedate) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile2_politicallyexposed) || ownershipProfileInputList.contains(BankInputName.shareholderprofile2_criminalrecord) ||

            ownershipProfileInputList.contains(BankInputName.shareholderprofile3_title) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_lastname) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_owned) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile3_address) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_city) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_dateofbirth) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_zip) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile3_country) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_street) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_telnocc2) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_telephonenumber) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile3_emailaddress) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_identificationtype) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile3_State) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_nationality) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_Passportexpirydate) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_passportissuedate) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile3_politicallyexposed) || ownershipProfileInputList.contains(BankInputName.shareholderprofile3_criminalrecord) ||

            ownershipProfileInputList.contains(BankInputName.shareholderprofile4_title) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_lastname) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_owned) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile4_address) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_city) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_dateofbirth) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_zip) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile4_country) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_street) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_telnocc2) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_telephonenumber) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile4_emailaddress) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_identificationtype) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile4_State) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_nationality) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_Passportexpirydate) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_passportissuedate) ||
            ownershipProfileInputList.contains(BankInputName.shareholderprofile4_politicallyexposed) || ownershipProfileInputList.contains(BankInputName.shareholderprofile4_criminalrecord) ||

         /* ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Name) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_RegNumber) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Address) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_City) ||
          ownershipProfileInputList.contains(BankInputName.corporateshareholder1_State) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_ZipCode) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Country) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Street) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_identificationtype) ||

          ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Name) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_RegNumber) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Address) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_City) ||
          ownershipProfileInputList.contains(BankInputName.corporateshareholder2_State) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_ZipCode) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Country) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Street) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_identificationtype) ||

          ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Name) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_RegNumber) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Address) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_City) ||
          ownershipProfileInputList.contains(BankInputName.corporateshareholder3_State) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_ZipCode) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Country) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_identificationtype) ||
          ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Street) ||*/ view)
    {
  %>

  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget" style="margin-bottom: 0;">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownershipprofile_shareholder%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">
                <center><h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;">
                  <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownershipprofile_please%>
                </h5></center>

                <div class="col-sm-8 portlets ui-sortable">
                  <label for="numOfShareholders" style="font-family:Open Sans;font-size: 13px;font-weight: 600;margin-left: 10%;"><%=ownershipprofile_natural%></label>
                </div>
                <div class="col-sm-4 portlets ui-sortable">
                  <%--
                                                      <input type="text" class="form-control"  id="numOfShareholders" name="numOfShareholders" onkeypress="return isNumber(event)" autocomplete="off" style="border: 1px solid #b2b2b2;font-weight:bold;width:79%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfShareholders())?ownershipProfileVO.getNumOfShareholders():"1"%>"/><%if(validationErrorList.getError("numOfShareholders")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  --%>
                  <select name="numOfShareholders" id="numOfShareholders"  value="">
                    <option value=""><%=ownershipprofile_Select_Count%></option>
                    <option value="1"><%=ownershipprofile_1%></option>
                    <option value="2"><%=ownershipprofile_2%></option>
                    <option value="3"><%=ownershipprofile_3%></option>
                    <option value="4"><%=ownershipprofile_4%></option>

                  </select>
                  <input value='<%=functions.isValueNull(ownershipProfileVO.getNumOfShareholders())?ownershipProfileVO.getNumOfShareholders():"1"%>' id="getcountvalShare" type="hidden">
                  <script>

                    var countryval = document.getElementById('getcountvalShare').value;
                    $('[name=numOfShareholders] option').filter(function ()
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
                  <%--<label style="font-family:Open Sans;font-size: 13px;font-weight: 600;" class="labelshare1">(Shareholders Count Should be 1 to 3)</label>--%>
                </div>
                <br>
                <div class="row">
                  <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                      <div class="widget-header transparent">
                        <center><h5><strong><i class="fa fa-file-text"></i>&nbsp;&nbsp;<%=ownershipprofile_shareholder%></strong></h5></center>
                        <div class="additional-btn">
                          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                      </div>

                      <div class="widget-content padding">
                        <div id="horizontal-form">

                          <div class="container-fluid " style="padding-left: 0px;adding-right: 0px;">
                            <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%;text-align: left;">
                              <div class="form foreground bodypanelfont_color panelbody_color">
                                <div class="share1">
                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;">Individual Shareholder Profile 1</h2>

                                  <div class="form-group col-md-2 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title%></label>
                                    <select class="form-control" id="shareholderprofile1_title" name="shareholderprofile1_title" <%=globaldisabled%>>
                                      <option value=""><%=ownershipprofile_Select_Title%></option>
                                      <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownershipprofile_MR%></option>
                                      <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownershipprofile_MRS%></option>
                                      <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownershipprofile_MS%></option>
                                      <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownershipprofile_MISS%></option>
                                      <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownershipprofile_MASTER%></option>
                                      <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownershipprofile_DR%></option>

                                    </select> <%if(validationErrorList.getError("shareholderprofile1_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1" name="shareholderprofile1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getFirstname()) ? ownershipProfileDetailsVO_shareholder1.getFirstname():""%>" /><%if(validationErrorList.getError("shareholderprofile1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile1_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_lastname" name="shareholderprofile1_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getLastname()) ? ownershipProfileDetailsVO_shareholder1.getLastname():""%>" /><%if(validationErrorList.getError("shareholderprofile1_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile1_dateofbirth" name="shareholderprofile1_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile1_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_telnocc1" name="shareholderprofile1_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_telnocc1")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile1_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_telephonenumber" name="shareholderprofile1_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder1.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_emailaddress" name="shareholderprofile1_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder1.getEmailaddress():""%>"/><%if(validationErrorList.getError("shareholderprofile1_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_percentage%></label>
                                    <div class="input-group">
                                      <input type="text" class="form-control"  id="shareholderprofile1_owned" name="shareholderprofile1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) ? ownershipProfileDetailsVO_shareholder1.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile2_owned','shareholderprofile3_owned','shareholderprofile4_owned',1)"/>
                                      <span class="input-group-addon" style="font-weight: 800;">%</span>

                                      <%if(validationErrorList.getError("shareholderprofile1_owned")!=null){%> <span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownershipprofile_Invalid_Percentage%></span><%}%>
                                      <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile1_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile1_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile1_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                    </div>
                                  </div>

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof%></label>
                                    <select class="form-control" name="shareholderprofile1_addressproof" id="shareholderprofile1_addressproof"class="form-control" <%=globaldisabled%>>
                                      <%
                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddressProof())  ? ownershipProfileDetailsVO_shareholder1.getAddressProof() : ""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_addressId" name="shareholderprofile1_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddressId()) ? ownershipProfileDetailsVO_shareholder1.getAddressId():""%>"/><%if(validationErrorList.getError("shareholderprofile1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_address" name="shareholderprofile1_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddress()) ? ownershipProfileDetailsVO_shareholder1.getAddress():""%>"/><%if(validationErrorList.getError("shareholderprofile1_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_street" name="shareholderprofile1_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getStreet()) ? ownershipProfileDetailsVO_shareholder1.getStreet():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_street")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_city" name="shareholderprofile1_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCity()) ? ownershipProfileDetailsVO_shareholder1.getCity():""%>"/><%if(validationErrorList.getError("shareholderprofile1_city")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_State" name="shareholderprofile1_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getState()) ? ownershipProfileDetailsVO_shareholder1.getState():""%>"/><%if(validationErrorList.getError("shareholderprofile1_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country%></label>
                                    <select id="shareholderprofile1_country" name="shareholderprofile1_country" onchange="myjunk1('shareholderprofile1_country','shareholderprofile1_telnocc1');"  class="form-control" <%=globaldisabled%> >
                                      <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCountry()) ? ownershipProfileDetailsVO_shareholder1.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile1_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_zip" name="shareholderprofile1_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getZipcode()) ? ownershipProfileDetailsVO_shareholder1.getZipcode():""%>"/><%if(validationErrorList.getError("shareholderprofile1_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type%></label>
                                    <select class="form-control" id="shareholderprofile1_identificationtypeselect" name="shareholderprofile1_identificationtypeselect" id="shareholderprofile1_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                      <%
                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder1.getIdentificationtypeselect():""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile1_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile1_identificationtype" name="shareholderprofile1_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality%></label>
                                    <select  class="form-control" id="shareholderprofile1_nationality" name="shareholderprofile1_nationality" style="border: 1px solid #b2b2b2;"   <%=globaldisabled%>>
                                      <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getNationality()) ? ownershipProfileDetailsVO_shareholder1.getNationality():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile1_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_isssuing%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile1_passportissuedate" name="shareholderprofile1_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Expiry_date%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile1_Passportexpirydate" name="shareholderprofile1_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-12 has-feedback"></div>

                                  <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <label for="shareholderprofile1_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                                    <font class="radio_align"> <input type="radio" id="shareholderprofile1_politicallyexposed" name="shareholderprofile1_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed())?"checked":""%> />&nbsp;Yes&nbsp;
                                      <input type="radio" name="shareholderprofile1_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;No</font>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <label for="shareholderprofile1_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                                    <font class="radio_align"><input type="radio" id="shareholderprofile1_criminalrecord" name="shareholderprofile1_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder1.getCriminalrecord())?"checked":""%> />&nbsp;Yes&nbsp;
                                      <input type="radio" name="shareholderprofile1_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCriminalrecord()) ?"checked":""%> />&nbsp;No</font>
                                  </div>
                                </div>
                                <div class="share2">

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Individual_Shareholder2%></h2>

                                  <div class="form-group col-md-2 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title%></label>
                                    <select class="form-control" id="shareholderprofile2_title" name="shareholderprofile2_title" <%=disableShareHolder2%> <%=globaldisabled%>>
                                      <option value=""><%=ownershipprofile_Select_Title%></option>
                                      <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownershipprofile_MR%></option>
                                      <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownershipprofile_MRS%></option>
                                      <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownershipprofile_MS%></option>
                                      <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownershipprofile_MISS%></option>
                                      <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownershipprofile_MASTER%></option>
                                      <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownershipprofile_DR%></option>

                                    </select> <%if(validationErrorList.getError("shareholderprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2" name="shareholderprofile2" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getFirstname()) ? ownershipProfileDetailsVO_shareholder2.getFirstname():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_lastname" name="shareholderprofile2_lastname" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getLastname()) ? ownershipProfileDetailsVO_shareholder2.getLastname():""%>" <%=disableShareHolder2%> /><%if(validationErrorList.getError("shareholderprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile2_dateofbirth" name="shareholderprofile2_dateofbirth" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getDateofbirth()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile2_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_telnocc2" name="shareholderprofile2_telnocc2" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder2.getTelnocc1():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_telephonenumber" name="shareholderprofile2_telephonenumber" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%> <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder2.getTelephonenumber():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_emailaddress" name="shareholderprofile2_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder2.getEmailaddress():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_percentage%></label>
                                    <div class="input-group">
                                      <input type="text" class="form-control"  id="shareholderprofile2_owned" name="shareholderprofile2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned()) ? ownershipProfileDetailsVO_shareholder2.getOwned():""%>" <%=disableShareHolder2%> onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile1_owned','shareholderprofile3_owned','shareholderprofile4_owned',2)"/>
                                      <span class="input-group-addon" style="font-weight: 800;">%</span>

                                      <%if(validationErrorList.getError("shareholderprofile2_owned")!=null){%> <span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownershipprofile_Invalid_Percentage%></span><%}%>
                                      <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile2_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile2_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile2_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                    </div>
                                  </div>


                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof%></label>
                                    <select class="form-control" name="shareholderprofile2_addressproof" id="shareholderprofile2_addressproof"class="form-control" <%=globaldisabled%> <%=disableShareHolder2%>>
                                      <%
                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddressProof()) ? ownershipProfileDetailsVO_shareholder2.getAddressProof() : ""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_addressId" name="shareholderprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddressId()) ? ownershipProfileDetailsVO_shareholder2.getAddressId():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_address" name="shareholderprofile2_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddress()) ? ownershipProfileDetailsVO_shareholder2.getAddress():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_street" name="shareholderprofile2_street" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getStreet()) ? ownershipProfileDetailsVO_shareholder2.getStreet():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_city" name="shareholderprofile2_city" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCity()) ? ownershipProfileDetailsVO_shareholder2.getCity():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_State" name="shareholderprofile2_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getState()) ? ownershipProfileDetailsVO_shareholder2.getState():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country%></label>
                                    <select onchange="myjunk1('shareholderprofile2_country','shareholderprofile2_telnocc2');"  class="form-control" style="<%=disablecolor%>" id="shareholderprofile2_country" name="shareholderprofile2_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCountry()) ? ownershipProfileDetailsVO_shareholder2.getCountry():""%>" <%=disableShareHolder2%> /> <%if(validationErrorList.getError("shareholderprofile2_country")!=null){%><%}%>
                                    <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCountry()) ? ownershipProfileDetailsVO_shareholder2.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_zip" name="shareholderprofile2_zip" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getZipcode()) ? ownershipProfileDetailsVO_shareholder2.getZipcode():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type%></label>
                                    <select class="form-control" name="shareholderprofile2_identificationtypeselect" id="shareholderprofile2_identificationtypeselect" class="form-control" <%=globaldisabled%> style="<%=disablecolor%>" <%=disableShareHolder2%>>
                                      <%
                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder2.getIdentificationtypeselect():""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile2_identificationtype" name="shareholderprofile2_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder2.getIdentificationtype():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality%></label>
                                    <select  class="form-control" id="shareholderprofile2_nationality" name="shareholderprofile2_nationality" style="border: 1px solid #b2b2b2;"<%=disableShareHolder2%>   <%=globaldisabled%>>
                                      <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getNationality()) ? ownershipProfileDetailsVO_shareholder2.getNationality():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_isssuing%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile2_passportissuedate" name="shareholderprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getPassportissuedate()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Expiry_date%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile2_Passportexpirydate" name="shareholderprofile2_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getPassportexpirydate()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-12 has-feedback"></div>

                                                                <div class="form-group col-md-4 has-feedback" id="sh2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal2%>" >
                                    <label for="shareholderprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                                    <font class="radio_align"><input type="radio" id="shareholderprofile2_politicallyexposed" name="shareholderprofile2_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                                      <input type="radio" name="shareholderprofile2_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                                  </div>
                                                                <div class="form-group col-md-4 has-feedback" id="sh2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal2%>" >
                                    <label for="shareholderprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                                    <font class="radio_align"><input type="radio" id="shareholderprofile2_criminalrecord" name="shareholderprofile2_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder2.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                                      <input type="radio" name="shareholderprofile2_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                                  </div>
                                </div>
                                <div class="share3">

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Individual_Shareholder3%></h2>

                                  <div class="form-group col-md-2 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title%></label>
                                    <select class="form-control" id="shareholderprofile3_title" name="shareholderprofile3_title" <%=globaldisabled%> <%=disableShareHolder3%>>
                                      <option value=""><%=ownershipprofile_Select_Title%></option>
                                      <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ? "selected":""%>><%=ownershipprofile_MR%></option>
                                      <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ? "selected":""%>><%=ownershipprofile_MRS%></option>
                                      <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownershipprofile_MS%></option>
                                      <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownershipprofile_MISS%></option>
                                      <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownershipprofile_MASTER%></option>
                                      <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownershipprofile_DR%></option>

                                    </select> <%if(validationErrorList.getError("shareholderprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3" name="shareholderprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getFirstname()) ? ownershipProfileDetailsVO_shareholder3.getFirstname():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_lastname" name="shareholderprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getLastname()) ? ownershipProfileDetailsVO_shareholder3.getLastname():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile3_dateofbirth" name="shareholderprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getDateofbirth()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile3_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_telnocc2" name="shareholderprofile3_telnocc2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder3.getTelnocc1():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_telephonenumber" name="shareholderprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder3.getTelephonenumber():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_emailaddress" name="shareholderprofile3_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder3.getEmailaddress():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_percentage%></label>
                                    <div class="input-group">
                                      <input type="text" align="center" class="form-control" id="shareholderprofile3_owned" name="shareholderprofile3_owned" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getOwned()) ? ownershipProfileDetailsVO_shareholder3.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile4_owned',3)" <%=disableShareHolder3%>/>
                                      <span class="input-group-addon" style="font-weight: 800;">%</span>

                                      <%if(validationErrorList.getError("shareholderprofile3_owned")!=null){%><span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownershipprofile_Invalid_Percentage%></span><%}%>
                                      <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile3_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile3_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile3_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                    </div>
                                  </div>

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof%></label>
                                    <select class="form-control" name="shareholderprofile3_addressproof" id="shareholderprofile3_addressproof"class="form-control" <%=globaldisabled%> <%=disableShareHolder3%>>
                                      <%
                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddressProof()) ? ownershipProfileDetailsVO_shareholder3.getAddressProof() : ""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_addressId" name="shareholderprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddressId()) ? ownershipProfileDetailsVO_shareholder3.getAddressId():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_address" name="shareholderprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddress()) ? ownershipProfileDetailsVO_shareholder3.getAddress():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_street" name="shareholderprofile3_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getStreet()) ? ownershipProfileDetailsVO_shareholder3.getStreet():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_city" name="shareholderprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCity()) ? ownershipProfileDetailsVO_shareholder3.getCity():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_State" name="shareholderprofile3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getState()) ? ownershipProfileDetailsVO_shareholder3.getState():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country%></label>
                                    <select onchange="myjunk1('shareholderprofile3_country','shareholderprofile3_telnocc2');"  class="form-control" style="<%=disablecolor%>" id="shareholderprofile3_country" name="shareholderprofile3_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCountry()) ? ownershipProfileDetailsVO_shareholder3.getCountry():""%>" <%=disableShareHolder3%> /> <%if(validationErrorList.getError("shareholderprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCountry()) ? ownershipProfileDetailsVO_shareholder3.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_zip" name="shareholderprofile3_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getZipcode()) ? ownershipProfileDetailsVO_shareholder3.getZipcode():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type%></label>
                                    <select class="form-control" name="shareholderprofile3_identificationtypeselect" id="shareholderprofile3_identificationtypeselect" class="form-control" <%=globaldisabled%>
                                            style="<%=disablecolor%>" <%=disableShareHolder3%>>
                                      <%
                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder3.getIdentificationtypeselect():""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile3_identificationtype" name="shareholderprofile3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder3.getIdentificationtype():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality%></label>
                                    <select  class="form-control" id="shareholderprofile3_nationality" name="shareholderprofile3_nationality" style="border: 1px solid #b2b2b2;"<%=disableShareHolder3%>   <%=globaldisabled%>>
                                      <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getNationality()) ? ownershipProfileDetailsVO_shareholder3.getNationality():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_isssuing%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile3_passportissuedate" name="shareholderprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getPassportissuedate()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Expiry_date%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile3_Passportexpirydate" name="shareholderprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getPassportexpirydate()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-12 has-feedback"></div>

                                                                <div class="form-group col-md-4 has-feedback" id="sh3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal3%>">
                                    <label for="shareholderprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                                    <font class="radio_align"><input type="radio" id="shareholderprofile3_politicallyexposed" name="shareholderprofile3_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                                      <input type="radio" name="shareholderprofile3_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                                  </div>
                                                                <div class="form-group col-md-4 has-feedback" id="sh3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal3%>">
                                    <label for="shareholderprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                                    <font class="radio_align"><input type="radio" id="shareholderprofile3_criminalrecord" name="shareholderprofile3_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                                      <input type="radio" name="shareholderprofile3_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
                                  </div>
                                </div>


                                <!--share 4-->

                                <div class="share4">

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Individual_Shareholder4%></h2>

                                  <div class="form-group col-md-2 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Title%></label>
                                    <select class="form-control" id="shareholderprofile4_title" name="shareholderprofile4_title" <%=globaldisabled%> <%=disableShareHolder4%>>
                                      <option value=""><%=ownershipprofile_Select_Title%></option>
                                      <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ? "selected":""%>><%=ownershipprofile_MR%></option>
                                      <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ? "selected":""%>><%=ownershipprofile_MRS%></option>
                                      <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownershipprofile_MS%></option>
                                      <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownershipprofile_MISS%></option>
                                      <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownershipprofile_MASTER%></option>
                                      <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownershipprofile_DR%></option>

                                    </select> <%if(validationErrorList.getError("shareholderprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_First%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4" name="shareholderprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getFirstname()) ? ownershipProfileDetailsVO_shareholder4.getFirstname():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-3 has-feedback">
                                    <label for="shareholderprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Last%>&nbsp;<%=ownershipprofile_Name%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_lastname" name="shareholderprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getLastname()) ? ownershipProfileDetailsVO_shareholder4.getLastname():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Date_Birth%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile4_dateofbirth" name="shareholderprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getDateofbirth()):""%>"<%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile4_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country_Code%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_telnocc2" name="shareholderprofile4_telnocc2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder4.getTelnocc1():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile4_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-2 has-feedback">
                                    <label for="shareholderprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Phone_No%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_telephonenumber" name="shareholderprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder4.getTelephonenumber():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Email_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_emailaddress" name="shareholderprofile4_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder4.getEmailaddress():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_percentage%></label>
                                    <div class="input-group">
                                      <input type="text" align="center" class="form-control" id="shareholderprofile4_owned" name="shareholderprofile4_owned" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getOwned()) ? ownershipProfileDetailsVO_shareholder4.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile3_owned',4)" <%=disableShareHolder4%>/>
                                      <span class="input-group-addon" style="font-weight: 800;">%</span>

                                      <%if(validationErrorList.getError("shareholderprofile4_owned")!=null){%><span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownershipprofile_Invalid_Percentage%></span><%}%>
                                      <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile4_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile4_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile4_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                    </div>
                                  </div>

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Address_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_Proof%></label>
                                    <select class="form-control" name="shareholderprofile4_addressproof" id="shareholderprofile4_addressproof"class="form-control" <%=globaldisabled%> <%=disableShareHolder4%>>
                                      <%
                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddressProof()) ? ownershipProfileDetailsVO_shareholder3.getAddressProof() : ""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_addressId" name="shareholderprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddressId()) ? ownershipProfileDetailsVO_shareholder4.getAddressId():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_address" name="shareholderprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddress()) ? ownershipProfileDetailsVO_shareholder4.getAddress():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_street" name="shareholderprofile4_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getStreet()) ? ownershipProfileDetailsVO_shareholder4.getStreet():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_city" name="shareholderprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCity()) ? ownershipProfileDetailsVO_shareholder4.getCity():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_state%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_State" name="shareholderprofile4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getState()) ? ownershipProfileDetailsVO_shareholder4.getState():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country%></label>
                                    <select onchange="myjunk1('shareholderprofile4_country','shareholderprofile4_telnocc2');"  class="form-control" style="<%=disablecolor%>" id="shareholderprofile4_country" name="shareholderprofile4_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCountry()) ? ownershipProfileDetailsVO_shareholder4.getCountry():""%>" <%=disableShareHolder3%> /> <%if(validationErrorList.getError("shareholderprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCountry()) ? ownershipProfileDetailsVO_shareholder4.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_zip" name="shareholderprofile4_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getZipcode()) ? ownershipProfileDetailsVO_shareholder4.getZipcode():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details%></h2>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type%></label>
                                    <select class="form-control" name="shareholderprofile4_identificationtypeselect" id="shareholderprofile4_identificationtypeselect" class="form-control" <%=globaldisabled%>
                                            style="<%=disablecolor%>" <%=disableShareHolder4%>>
                                      <%
                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder4.getIdentificationtypeselect():""));
                                      %>
                                    </select>
                                    <%if(validationErrorList.getError("shareholderprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_ID%></label>
                                    <input type="text" class="form-control"  id="shareholderprofile4_identificationtype" name="shareholderprofile4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder4.getIdentificationtype():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Nationality%></label>
                                    <select  class="form-control" id="shareholderprofile4_nationality" name="shareholderprofile4_nationality" style="border: 1px solid #b2b2b2;"<%=disableShareHolder4%>   <%=globaldisabled%>>
                                      <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getNationality()) ? ownershipProfileDetailsVO_shareholder4.getNationality():"")%>
                                    </select><%if(validationErrorList.getError("shareholderprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_isssuing%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile4_passportissuedate" name="shareholderprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getPassportissuedate()):""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-4 has-feedback">
                                    <label for="shareholderprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Expiry_date%></label>
                                    <input type="text" class="form-control datepicker"  id="shareholderprofile4_Passportexpirydate" name="shareholderprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getPassportexpirydate()):""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                  </div>

                                  <div class="form-group col-md-12 has-feedback"></div>

                                  <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal4%>">
                                    <label for="shareholderprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_Politically%></label>
                                    <font class="radio_align"><input type="radio" id="shareholderprofile4_politicallyexposed" name="shareholderprofile4_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                                      <input type="radio" name="shareholderprofile4_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownershipprofile_No%></font>
                                  </div>
                                  <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal4%>">
                                    <label for="shareholderprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownershipprofile_existence%></label>
                                    <font class="radio_align"><input type="radio" id="shareholderprofile4_criminalrecord" name="shareholderprofile4_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder4.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownershipprofile_Yes%>&nbsp;
                                      <input type="radio" name="shareholderprofile4_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownershipprofile_No%></font>
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
          </div>
        </div>
      </div>
    </div>
  </div>

  <%
    }

    if(ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Name) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_RegNumber) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Address) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_City) ||
            ownershipProfileInputList.contains(BankInputName.corporateshareholder1_State) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_ZipCode) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Country) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_Street) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.corporateshareholder1_identificationtype) ||

            ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Name) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_RegNumber) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Address) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_City) ||
            ownershipProfileInputList.contains(BankInputName.corporateshareholder2_State) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_ZipCode) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Country) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_Street) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.corporateshareholder2_identificationtype) ||

            ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Name) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_RegNumber) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Address) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_City) ||
            ownershipProfileInputList.contains(BankInputName.corporateshareholder3_State) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_ZipCode) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Country) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.corporateshareholder3_identificationtype) ||
            ownershipProfileInputList.contains(BankInputName.corporateshareholder3_Street) ||

            ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Name) || ownershipProfileInputList.contains(BankInputName.corporateshareholder4_RegNumber) || ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Address) || ownershipProfileInputList.contains(BankInputName.corporateshareholder4_City) ||
            ownershipProfileInputList.contains(BankInputName.corporateshareholder4_State) || ownershipProfileInputList.contains(BankInputName.corporateshareholder4_ZipCode) || ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Country) || ownershipProfileInputList.contains(BankInputName.corporateshareholder4_identificationtypeselect) || ownershipProfileInputList.contains(BankInputName.corporateshareholder4_identificationtype) ||
            ownershipProfileInputList.contains(BankInputName.corporateshareholder4_Street) || view)
    {
  %>
  <div class="content" style="margin-top: 0;">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget" style="margin-bottom: 0;">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownershipprofile_Corporate_Shareholder%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <center><h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;">
              <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownershipprofile_Corporate_please%></h5></center>

            <div class="col-sm-8 portlets ui-sortable">
              <label for="numOfCorporateShareholders" style="font-family:Open Sans;font-size: 13px;font-weight: 600;margin-left: 10%;">How many Corporate Shareholders do you have that more than 25% in your business?</label>
            </div>
            <div class="col-sm-4 portlets ui-sortable">
              <%--
                                              <input type="text" class="form-control"  id="numOfCorporateShareholders" name="numOfCorporateShareholders" onkeypress="return isNumber(event)" style="border: 1px solid #b2b2b2;font-weight:bold;width: 92%;" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfCorporateShareholders())?ownershipProfileVO.getNumOfCorporateShareholders():"0"%>"/><%if(validationErrorList.getError("numOfCorporateShareholders")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
              --%>
              <select name="numOfCorporateShareholders" id="numOfCorporateShareholders"  value="">
                <option value=""><%=ownershipprofile_Select_Count%></option>
                <option value="0"><%=ownershipprofile_0%></option>
                <option value="1"><%=ownershipprofile_1%></option>
                <option value="2"><%=ownershipprofile_2%></option>
                <option value="3"><%=ownershipprofile_3%></option>
                <option value="4"><%=ownershipprofile_4%></option>

              </select>
              <input value='<%=functions.isValueNull(ownershipProfileVO.getNumOfCorporateShareholders())?ownershipProfileVO.getNumOfCorporateShareholders():"0"%>' id="getcountvalCorp" type="hidden">
              <script>

                var countryval = document.getElementById('getcountvalCorp').value;
                $('[name=numOfCorporateShareholders] option').filter(function ()
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
            <div class="row">
              <div class="col-sm-12 portlets ui-sortable">
                <div class="widget">

                  <div class="widget-header transparent">
                    <%--<center><h5><strong><i class="fa fa-file-text"></i>&nbsp;&nbsp;Corporate Shareholder Profile (Shareholder must equal 50% or more)</strong></h5></center>--%>
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
                            <div class="corporate1">
                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;">Corporate Shareholder Profile 1</h2>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_Shareholder1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_Name" name="corporateshareholder1_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getName()) ? ownershipProfileDetailsVO_corporateShareholder1.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder1_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Registration_Number%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_RegNumber" name="corporateshareholder1_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder1.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder1_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_percentage%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_owned" name="corporateshareholder1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder1.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder1_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_AddressDetails%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressProof%></label>
                                <select class="form-control" name="corporateshareholder1_addressproof" id="corporateshareholder1_addressproof"class="form-control" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddressProof() : ""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressID%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_addressId" name="corporateshareholder1_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_Address" name="corporateshareholder1_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder1_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_Street" name="corporateshareholder1_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder1.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder1_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_City" name="corporateshareholder1_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getCity()) ? ownershipProfileDetailsVO_corporateShareholder1.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder1_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_State%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_State" name="corporateshareholder1_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getState()) ? ownershipProfileDetailsVO_corporateShareholder1.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder1_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country1%></label>
                                <select  name="corporateshareholder1_Country" class="form-control" <%=globaldisabled%> >
                                  <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder1.getCountry():"")%>
                                </select><%if(validationErrorList.getError("corporateshareholder1_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_ZipCode" name="corporateshareholder1_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder1.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder1_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details1%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type1%></label>
                                <select class="form-control" id="corporateshareholder1_identificationtypeselect" name="corporateshareholder1_identificationtypeselect" id="corporateshareholder1_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtypeselect():""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder1_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder1_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_id1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder1_identificationtype" name="corporateshareholder1_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder1_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                            </div>
                            <div class="corporate2">
                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Corporate_profile2%></h2>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_Shareholder2%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_Name" name="corporateshareholder2_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getName()) ? ownershipProfileDetailsVO_corporateShareholder2.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder2_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Registration_Number%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_RegNumber" name="corporateshareholder2_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder2.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder2_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_percentage%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_owned" name="corporateshareholder2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder2.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder2_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_AddressDetails%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressProof%></label>
                                <select class="form-control" name="corporateshareholder2_addressproof" id="corporateshareholder2_addressproof"class="form-control" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddressProof() : ""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressID%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_addressId" name="corporateshareholder2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_Address" name="corporateshareholder2_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder2_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_Street" name="corporateshareholder2_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder2.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder2_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_City" name="corporateshareholder2_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getCity()) ? ownershipProfileDetailsVO_corporateShareholder2.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder2_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_State%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_State" name="corporateshareholder2_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getState()) ? ownershipProfileDetailsVO_corporateShareholder2.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country1%></label>
                                <select  name="corporateshareholder2_Country" class="form-control" <%=globaldisabled%> >
                                  <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder2.getCountry():"")%>
                                </select><%if(validationErrorList.getError("corporateshareholder2_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_ZipCode" name="corporateshareholder2_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder2.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder2_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>


                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details1%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type1%></label>
                                <select class="form-control" id="corporateshareholder2_identificationtypeselect" name="corporateshareholder2_identificationtypeselect" id="corporateshareholder2_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtypeselect():""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_id1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder2_identificationtype" name="corporateshareholder2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                            </div>
                            <div class="corporate3">
                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Corporate_profile3%></h2>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_Shareholder3%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_Name" name="corporateshareholder3_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getName()) ? ownershipProfileDetailsVO_corporateShareholder3.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder3_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Registration_Number%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_RegNumber" name="corporateshareholder3_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder3.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder3_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_percentage%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_owned" name="corporateshareholder3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder3.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_AddressDetails%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressProof%></label>
                                <select class="form-control" name="corporateshareholder3_addressproof" id="corporateshareholder3_addressproof"class="form-control" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddressProof() : ""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressID%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_addressId" name="corporateshareholder3_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_Address" name="corporateshareholder3_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder3_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_Street" name="corporateshareholder3_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder3.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder3_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_City" name="corporateshareholder3_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getCity()) ? ownershipProfileDetailsVO_corporateShareholder3.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder3_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_State%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_State" name="corporateshareholder3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getState()) ? ownershipProfileDetailsVO_corporateShareholder3.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country1%></label >
                                <select  name="corporateshareholder3_Country" class="form-control" <%=globaldisabled%> >
                                  <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder3.getCountry():"")%>
                                </select><%if(validationErrorList.getError("corporateshareholder3_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_ZipCode" name="corporateshareholder3_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder3.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder3_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details1%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type1%></label>
                                <select class="form-control" id="corporateshareholder3_identificationtypeselect" name="corporateshareholder3_identificationtypeselect" id="corporateshareholder3_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtypeselect():""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_id1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder3_identificationtype" name="corporateshareholder3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                            </div>
                            <!--- share holder 4---->
                            <div class="corporate4">
                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownershipprofile_Corporate_profile4%></h2>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_Shareholder4%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_Name" name="corporateshareholder4_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getName()) ? ownershipProfileDetailsVO_corporateShareholder4.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder4_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Registration_Number%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_RegNumber" name="corporateshareholder4_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder4.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder4_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Corporate_percentage%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_owned" name="corporateshareholder4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder4.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_AddressDetails%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressProof%></label>
                                <select class="form-control" name="corporateshareholder4_addressproof" id="corporateshareholder4_addressproof"class="form-control" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddressProof() : ""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_AddressID%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_addressId" name="corporateshareholder4_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Address1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_Address" name="corporateshareholder4_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder4_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Street1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_Street" name="corporateshareholder4_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder4.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder4_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_city1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_City" name="corporateshareholder4_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getCity()) ? ownershipProfileDetailsVO_corporateShareholder4.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder4_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_State%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_State" name="corporateshareholder4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getState()) ? ownershipProfileDetailsVO_corporateShareholder4.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Country1%></label >
                                <select  name="corporateshareholder3_Country" class="form-control" <%=globaldisabled%> >
                                  <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder4.getCountry():"")%>
                                </select><%if(validationErrorList.getError("corporateshareholder4_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_zip1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_ZipCode" name="corporateshareholder4_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder4.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder4_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>

                              <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownershipprofile_Identification_Details1%></h2>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_Type1%></label>
                                <select class="form-control" id="corporateshareholder4_identificationtypeselect" name="corporateshareholder4_identificationtypeselect" id="corporateshareholder4_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                  <%
                                    out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtypeselect():""));
                                  %>
                                </select>
                                <%if(validationErrorList.getError("corporateshareholder4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                              </div>
                              <div class="form-group col-md-4 has-feedback">
                                <label for="corporateshareholder4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownershipprofile_Identification_id1%></label>
                                <input type="text" class="form-control"  id="corporateshareholder4_identificationtype" name="corporateshareholder4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
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
      </div>
    </div>
  </div>
  <%
    }
    %>
    <jsp:include page="ownershipprofile_1.jsp"></jsp:include>
</div>

<%
  if(ownershipProfileInputList.size()==0 && !view)
  {

    out.println("<div class=\"content-page\">");
    out.println("<div class=\"content\">");
    out.println("<div class=\"page-heading\">");
    out.println("<div class=\"row\">");
    out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
    out.println("<div class=\"widget\">");
    out.println("<div class=\"widget-header transparent\">\n" +
            "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;"+ownershipprofile_Ownership_Profile+"</strong></h2>\n" +
            "                                <div class=\"additional-btn\">\n" +
            "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
            "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
            "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
            "                                </div>\n" +
            "                            </div>");
    out.println("<div class=\"widget-content padding\">");
    out.println(Functions.NewShowConfirmation1(ownershipprofile_Profile,ownershipprofile_details));
    out.println("</div>");
    out.println("</div>");
    out.println("</div>");
    out.println("</div>");
    out.println("</div>");
    out.println("</div>");
    out.println("</div>");
  }
%>