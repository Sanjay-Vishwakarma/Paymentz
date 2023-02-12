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
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/15
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<script src="/merchant/javascript/ownershipdetails.js"></script>
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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownwershipprofile_shareholder%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <center><h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;">
                                    <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownwershipprofile_please%>
                                </h5></center>

                                <div class="col-sm-8 portlets ui-sortable">
                                    <label for="numOfShareholders" style="font-family:Open Sans;font-size: 13px;font-weight: 600;margin-left: 10%;"><%=ownwershipprofile_natural%></label>
                                </div>
                                <div class="col-sm-4 portlets ui-sortable">
                                    <%--
                                                                        <input type="text" class="form-control"  id="numOfShareholders" name="numOfShareholders" onkeypress="return isNumber(event)" autocomplete="off" style="border: 1px solid #b2b2b2;font-weight:bold;width:79%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfShareholders())?ownershipProfileVO.getNumOfShareholders():"1"%>"/><%if(validationErrorList.getError("numOfShareholders")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                    --%>
                                    <select name="numOfShareholders" id="numOfShareholders"  value="">
                                        <option value=""><%=ownwershipprofile_select_count%></option>
                                        <option value="1"><%=ownwershipprofile_1%></option>
                                        <option value="2"><%=ownwershipprofile_2%></option>
                                        <option value="3"><%=ownwershipprofile_3%></option>
                                        <option value="4"><%=ownwershipprofile_4%></option>

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
                                                <center><h5><strong><i class="fa fa-file-text"></i>&nbsp;&nbsp;<%=ownwershipprofile_shareholder%></strong></h5></center>
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
                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_shareholder_profile1%></h2>

                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Title%></label>
                                                                        <select class="form-control" id="shareholderprofile1_title" name="shareholderprofile1_title" <%=globaldisabled%>>
                                                                            <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                                            <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownwershipprofile_MR%></option>
                                                                            <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownwershipprofile_MRS%></option>
                                                                            <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownwershipprofile_MS%></option>
                                                                            <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownwershipprofile_MISS%></option>
                                                                            <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownwershipprofile_MASTER%></option>
                                                                            <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder1.getTitle()) ? "selected":""%>><%=ownwershipprofile_DR%></option>

                                                                        </select> <%if(validationErrorList.getError("shareholderprofile1_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1" name="shareholderprofile1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getFirstname()) ? ownershipProfileDetailsVO_shareholder1.getFirstname():""%>" /><%if(validationErrorList.getError("shareholderprofile1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile1_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_lastname" name="shareholderprofile1_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getLastname()) ? ownershipProfileDetailsVO_shareholder1.getLastname():""%>" /><%if(validationErrorList.getError("shareholderprofile1_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                    <label for="shareholderprofile1_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_DateBirth%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile1_dateofbirth" name="shareholderprofile1_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile1_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_telnocc1" name="shareholderprofile1_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_telnocc1")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile1_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_PhoneNo%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_telephonenumber" name="shareholderprofile1_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder1.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Email%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_emailaddress" name="shareholderprofile1_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder1.getEmailaddress():""%>"/><%if(validationErrorList.getError("shareholderprofile1_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage%></label>
                                                                        <div class="input-group">
                                                                            <input type="text" class="form-control"  id="shareholderprofile1_owned" name="shareholderprofile1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getOwned()) ? ownershipProfileDetailsVO_shareholder1.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile2_owned','shareholderprofile3_owned','shareholderprofile4_owned',1)"/>
                                                                            <span class="input-group-addon" style="font-weight: 800;">%</span>

                                                                            <%if(validationErrorList.getError("shareholderprofile1_owned")!=null){%> <span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownwershipprofile_invalid_percentage%></span><%}%>
                                                                            <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile1_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile1_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile1_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                                        </div>
                                                                    </div>

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_Proof%></label>
                                                                        <select class="form-control" name="shareholderprofile1_addressproof" id="shareholderprofile1_addressproof"class="form-control" <%=globaldisabled%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddressProof())  ? ownershipProfileDetailsVO_shareholder1.getAddressProof() : ""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_addressId" name="shareholderprofile1_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddressId()) ? ownershipProfileDetailsVO_shareholder1.getAddressId():""%>"/><%if(validationErrorList.getError("shareholderprofile1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address1%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_address" name="shareholderprofile1_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getAddress()) ? ownershipProfileDetailsVO_shareholder1.getAddress():""%>"/><%if(validationErrorList.getError("shareholderprofile1_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Street%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_street" name="shareholderprofile1_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getStreet()) ? ownershipProfileDetailsVO_shareholder1.getStreet():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_street")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_city" name="shareholderprofile1_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCity()) ? ownershipProfileDetailsVO_shareholder1.getCity():""%>"/><%if(validationErrorList.getError("shareholderprofile1_city")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_State" name="shareholderprofile1_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getState()) ? ownershipProfileDetailsVO_shareholder1.getState():""%>"/><%if(validationErrorList.getError("shareholderprofile1_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country1%></label>
                                                                        <select id="shareholderprofile1_country" name="shareholderprofile1_country" onchange="myjunk1('shareholderprofile1_country','shareholderprofile1_telnocc1');"  class="form-control" <%=globaldisabled%> >
                                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCountry()) ? ownershipProfileDetailsVO_shareholder1.getCountry():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile1_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon" style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_zip" name="shareholderprofile1_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getZipcode()) ? ownershipProfileDetailsVO_shareholder1.getZipcode():""%>"/><%if(validationErrorList.getError("shareholderprofile1_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_type%></label>
                                                                        <select class="form-control" id="shareholderprofile1_identificationtypeselect" name="shareholderprofile1_identificationtypeselect" id="shareholderprofile1_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder1.getIdentificationtypeselect():""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile1_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_IdentificationID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile1_identificationtype" name="shareholderprofile1_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("shareholderprofile1_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                                        <select  class="form-control" id="shareholderprofile1_nationality" name="shareholderprofile1_nationality" style="border: 1px solid #b2b2b2;"   <%=globaldisabled%>>
                                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getNationality()) ? ownershipProfileDetailsVO_shareholder1.getNationality():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile1_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile1_passportissuedate" name="shareholderprofile1_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile1_Passportexpirydate" name="shareholderprofile1_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("shareholderprofile1_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-12 has-feedback"></div>

                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                                        <label for="shareholderprofile1_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                                                                        <font class="radio_align"> <input type="radio" id="shareholderprofile1_politicallyexposed" name="shareholderprofile1_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile1_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                                        <label for="shareholderprofile1_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                                        <font class="radio_align"><input type="radio" id="shareholderprofile1_criminalrecord" name="shareholderprofile1_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder1.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile1_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder1.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                                                                    </div>
                                                                </div>
                                                                <div class="share2">

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_shareholder_profile2%></h2>

                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Title%></label>
                                                                        <select class="form-control" id="shareholderprofile2_title" name="shareholderprofile2_title" <%=disableShareHolder2%> <%=globaldisabled%>>
                                                                            <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                                            <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownwershipprofile_MR%></option>
                                                                            <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownwershipprofile_MRS%></option>
                                                                            <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownwershipprofile_MS%></option>
                                                                            <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownwershipprofile_MISS%></option>
                                                                            <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownwershipprofile_MASTER%></option>
                                                                            <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder2.getTitle()) ? "selected":""%>><%=ownwershipprofile_DR%></option>

                                                                        </select> <%if(validationErrorList.getError("shareholderprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2" name="shareholderprofile2" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getFirstname()) ? ownershipProfileDetailsVO_shareholder2.getFirstname():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_lastname" name="shareholderprofile2_lastname" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getLastname()) ? ownershipProfileDetailsVO_shareholder2.getLastname():""%>" <%=disableShareHolder2%> /><%if(validationErrorList.getError("shareholderprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_DateBirth%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile2_dateofbirth" name="shareholderprofile2_dateofbirth" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getDateofbirth()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile2_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_telnocc2" name="shareholderprofile2_telnocc2" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder2.getTelnocc1():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_PhoneNo%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_telephonenumber" name="shareholderprofile2_telephonenumber" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%> <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder2.getTelephonenumber():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Email%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_emailaddress" name="shareholderprofile2_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder2.getEmailaddress():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage%></label>
                                                                        <div class="input-group">
                                                                            <input type="text" class="form-control"  id="shareholderprofile2_owned" name="shareholderprofile2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getOwned()) ? ownershipProfileDetailsVO_shareholder2.getOwned():""%>" <%=disableShareHolder2%> onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile1_owned','shareholderprofile3_owned','shareholderprofile4_owned',2)"/>
                                                                            <span class="input-group-addon" style="font-weight: 800;">%</span>

                                                                            <%if(validationErrorList.getError("shareholderprofile2_owned")!=null){%> <span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownwershipprofile_invalid_percentage%></span><%}%>
                                                                            <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile2_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile2_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile2_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                                        </div>
                                                                    </div>


                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_Proof%></label>
                                                                        <select class="form-control" name="shareholderprofile2_addressproof" id="shareholderprofile2_addressproof"class="form-control" <%=globaldisabled%> <%=disableShareHolder2%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddressProof()) ? ownershipProfileDetailsVO_shareholder2.getAddressProof() : ""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_addressId" name="shareholderprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddressId()) ? ownershipProfileDetailsVO_shareholder2.getAddressId():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address1%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_address" name="shareholderprofile2_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getAddress()) ? ownershipProfileDetailsVO_shareholder2.getAddress():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Street%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_street" name="shareholderprofile2_street" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getStreet()) ? ownershipProfileDetailsVO_shareholder2.getStreet():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_city" name="shareholderprofile2_city" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCity()) ? ownershipProfileDetailsVO_shareholder2.getCity():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_State" name="shareholderprofile2_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getState()) ? ownershipProfileDetailsVO_shareholder2.getState():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country1%></label>
                                                                        <select onchange="myjunk1('shareholderprofile2_country','shareholderprofile2_telnocc2');"  class="form-control" style="<%=disablecolor%>" id="shareholderprofile2_country" name="shareholderprofile2_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCountry()) ? ownershipProfileDetailsVO_shareholder2.getCountry():""%>" <%=disableShareHolder2%> /> <%if(validationErrorList.getError("shareholderprofile2_country")!=null){%><%}%>
                                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCountry()) ? ownershipProfileDetailsVO_shareholder2.getCountry():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_zip" name="shareholderprofile2_zip" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getZipcode()) ? ownershipProfileDetailsVO_shareholder2.getZipcode():""%>" <%=disableShareHolder2%>/> <%if(validationErrorList.getError("shareholderprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_type%></label>
                                                                        <select class="form-control" name="shareholderprofile2_identificationtypeselect" id="shareholderprofile2_identificationtypeselect" class="form-control" <%=globaldisabled%> style="<%=disablecolor%>" <%=disableShareHolder2%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder2.getIdentificationtypeselect():""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_IdentificationID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile2_identificationtype" name="shareholderprofile2_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder2.getIdentificationtype():""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                                        <select  class="form-control" id="shareholderprofile2_nationality" name="shareholderprofile2_nationality" style="border: 1px solid #b2b2b2;"<%=disableShareHolder2%>   <%=globaldisabled%>>
                                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getNationality()) ? ownershipProfileDetailsVO_shareholder2.getNationality():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile2_passportissuedate" name="shareholderprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getPassportissuedate()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile2_Passportexpirydate" name="shareholderprofile2_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder2.getPassportexpirydate()):""%>" <%=disableShareHolder2%>/><%if(validationErrorList.getError("shareholderprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-12 has-feedback"></div>

                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal2%>">
                                                                        <label for="shareholderprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%>?</label>
                                                                        <font class="radio_align"><input type="radio" id="shareholderprofile2_politicallyexposed" name="shareholderprofile2_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile2_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal2%>">
                                                                        <label for="shareholderprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                                        <font class="radio_align"><input type="radio" id="shareholderprofile2_criminalrecord" name="shareholderprofile2_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder2.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile2_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder2.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                                                                    </div>
                                                                </div>
                                                                <div class="share3">

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_shareholder_profile3%></h2>

                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Title%></label>
                                                                        <select class="form-control" id="shareholderprofile3_title" name="shareholderprofile3_title" <%=globaldisabled%> <%=disableShareHolder3%>>
                                                                            <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                                            <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ? "selected":""%>><%=ownwershipprofile_MR%></option>
                                                                            <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ? "selected":""%>><%=ownwershipprofile_MRS%></option>
                                                                            <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownwershipprofile_MS%></option>
                                                                            <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownwershipprofile_MISS%></option>
                                                                            <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownwershipprofile_MISS%></option>
                                                                            <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder3.getTitle()) ?"selected":""%>><%=ownwershipprofile_DR%></option>

                                                                        </select> <%if(validationErrorList.getError("shareholderprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3" name="shareholderprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getFirstname()) ? ownershipProfileDetailsVO_shareholder3.getFirstname():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_lastname" name="shareholderprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getLastname()) ? ownershipProfileDetailsVO_shareholder3.getLastname():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_DateBirth%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile3_dateofbirth" name="shareholderprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getDateofbirth()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile3_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_telnocc2" name="shareholderprofile3_telnocc2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder3.getTelnocc1():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_PhoneNo%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_telephonenumber" name="shareholderprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder3.getTelephonenumber():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Email%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_emailaddress" name="shareholderprofile3_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder3.getEmailaddress():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage%></label>
                                                                        <div class="input-group">
                                                                            <input type="text" align="center" class="form-control" id="shareholderprofile3_owned" name="shareholderprofile3_owned" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getOwned()) ? ownershipProfileDetailsVO_shareholder3.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile4_owned',3)" <%=disableShareHolder3%>/>
                                                                            <span class="input-group-addon" style="font-weight: 800;">%</span>

                                                                            <%if(validationErrorList.getError("shareholderprofile3_owned")!=null){%><span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownwershipprofile_invalid_percentage%></span><%}%>
                                                                            <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile3_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile3_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile3_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                                        </div>
                                                                    </div>

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_Proof%></label>
                                                                        <select class="form-control" name="shareholderprofile3_addressproof" id="shareholderprofile3_addressproof"class="form-control" <%=globaldisabled%> <%=disableShareHolder3%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddressProof()) ? ownershipProfileDetailsVO_shareholder3.getAddressProof() : ""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_addressId" name="shareholderprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddressId()) ? ownershipProfileDetailsVO_shareholder3.getAddressId():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address1%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_address" name="shareholderprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getAddress()) ? ownershipProfileDetailsVO_shareholder3.getAddress():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Street%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_street" name="shareholderprofile3_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getStreet()) ? ownershipProfileDetailsVO_shareholder3.getStreet():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_city" name="shareholderprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCity()) ? ownershipProfileDetailsVO_shareholder3.getCity():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_State" name="shareholderprofile3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getState()) ? ownershipProfileDetailsVO_shareholder3.getState():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country1%></label>
                                                                        <select onchange="myjunk1('shareholderprofile3_country','shareholderprofile3_telnocc2');"  class="form-control" style="<%=disablecolor%>" id="shareholderprofile3_country" name="shareholderprofile3_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCountry()) ? ownershipProfileDetailsVO_shareholder3.getCountry():""%>" <%=disableShareHolder3%> /> <%if(validationErrorList.getError("shareholderprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCountry()) ? ownershipProfileDetailsVO_shareholder3.getCountry():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_zip" name="shareholderprofile3_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getZipcode()) ? ownershipProfileDetailsVO_shareholder3.getZipcode():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_type%></label>
                                                                        <select class="form-control" name="shareholderprofile3_identificationtypeselect" id="shareholderprofile3_identificationtypeselect" class="form-control" <%=globaldisabled%>
                                                                                style="<%=disablecolor%>" <%=disableShareHolder3%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder3.getIdentificationtypeselect():""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_IdentificationID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile3_identificationtype" name="shareholderprofile3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder3.getIdentificationtype():""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                                        <select  class="form-control" id="shareholderprofile3_nationality" name="shareholderprofile3_nationality" style="border: 1px solid #b2b2b2;"<%=disableShareHolder3%>   <%=globaldisabled%>>
                                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getNationality()) ? ownershipProfileDetailsVO_shareholder3.getNationality():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile3_passportissuedate" name="shareholderprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getPassportissuedate()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile3_Passportexpirydate" name="shareholderprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder3.getPassportexpirydate()):""%>" <%=disableShareHolder3%>/><%if(validationErrorList.getError("shareholderprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-12 has-feedback"></div>

                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal3%>">
                                                                        <label for="shareholderprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                                                                        <font class="radio_align"><input type="radio" id="shareholderprofile3_politicallyexposed" name="shareholderprofile3_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile3_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal3%>">
                                                                        <label for="shareholderprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                                        <font class="radio_align"><input type="radio" id="shareholderprofile3_criminalrecord" name="shareholderprofile3_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile3_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                                                                    </div>
                                                                </div>


                                                                <!--share 4-->

                                                                <div class="share4">

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_shareholder_profile4%></h2>

                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Title%></label>
                                                                        <select class="form-control" id="shareholderprofile4_title" name="shareholderprofile4_title" <%=globaldisabled%> <%=disableShareHolder4%>>
                                                                            <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                                            <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ? "selected":""%>><%=ownwershipprofile_MR%></option>
                                                                            <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ? "selected":""%>><%=ownwershipprofile_MRS%></option>
                                                                            <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownwershipprofile_MS%></option>
                                                                            <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownwershipprofile_MISS%></option>
                                                                            <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                                                                            <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_shareholder4.getTitle()) ?"selected":""%>><%=ownwershipprofile_DR%></option>

                                                                        </select> <%if(validationErrorList.getError("shareholderprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4" name="shareholderprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getFirstname()) ? ownershipProfileDetailsVO_shareholder4.getFirstname():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-3 has-feedback">
                                                                        <label for="shareholderprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_lastname" name="shareholderprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getLastname()) ? ownershipProfileDetailsVO_shareholder4.getLastname():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_DateBirth%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile4_dateofbirth" name="shareholderprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getDateofbirth()):""%>"  <%=disableShareHolder4%> /><%if(validationErrorList.getError("shareholderprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile4_telnocc2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_telnocc2" name="shareholderprofile4_telnocc2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getTelnocc1()) ? ownershipProfileDetailsVO_shareholder4.getTelnocc1():""%>" <%=disableShareHolder3%>/> <%if(validationErrorList.getError("shareholderprofile4_telnocc2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-2 has-feedback">
                                                                        <label for="shareholderprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_PhoneNo%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_telephonenumber" name="shareholderprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getTelephonenumber()) ? ownershipProfileDetailsVO_shareholder4.getTelephonenumber():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Email%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_emailaddress" name="shareholderprofile4_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getEmailaddress()) ? ownershipProfileDetailsVO_shareholder4.getEmailaddress():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage%></label>
                                                                        <div class="input-group">
                                                                            <input type="text" align="center" class="form-control" id="shareholderprofile4_owned" name="shareholderprofile4_owned" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getOwned()) ? ownershipProfileDetailsVO_shareholder4.getOwned():""%>" onkeypress="return isNumberKey(event)" onchange="return myShareholder(this.value,'shareholderprofile1_owned','shareholderprofile2_owned','shareholderprofile3_owned',4)" <%=disableShareHolder4%>/>
                                                                            <span class="input-group-addon" style="font-weight: 800;">%</span>

                                                                            <%if(validationErrorList.getError("shareholderprofile4_owned")!=null){%><span style="display: table-row;text-align: center;font-weight:600;" class="apperrormsg"><%=ownwershipprofile_invalid_percentage%></span><%}%>
                                                                            <span style="<%=validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile4_owned"))?"background-color: #f2dede;":""%>"class="errormesage" style="display: table-row;text-align: center;font-weight:600;" id="shareholderprofile4_owned"><%if(validationErrorList.getError("shareholderplus_owned")!=null && functions.isValueNull(request.getParameter("shareholderprofile4_owned"))){%><%=validationErrorList.getError("shareholderplus_owned").getLogMessage()%><%}%></span>
                                                                        </div>
                                                                    </div>

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_Proof%></label>
                                                                        <select class="form-control" name="shareholderprofile4_addressproof" id="shareholderprofile4_addressproof"class="form-control" <%=globaldisabled%> <%=disableShareHolder4%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddressProof()) ? ownershipProfileDetailsVO_shareholder4.getAddressProof() : ""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_addressId" name="shareholderprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddressId()) ? ownershipProfileDetailsVO_shareholder4.getAddressId():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address1%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_address" name="shareholderprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getAddress()) ? ownershipProfileDetailsVO_shareholder4.getAddress():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Street%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_street" name="shareholderprofile4_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getStreet()) ? ownershipProfileDetailsVO_shareholder4.getStreet():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_city" name="shareholderprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCity()) ? ownershipProfileDetailsVO_shareholder4.getCity():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_State" name="shareholderprofile4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getState()) ? ownershipProfileDetailsVO_shareholder4.getState():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country1%></label>
                                                                        <select onchange="myjunk1('shareholderprofile4_country','shareholderprofile4_telnocc2');"  class="form-control" style="<%=disablecolor%>" id="shareholderprofile4_country" name="shareholderprofile4_country" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCountry()) ? ownershipProfileDetailsVO_shareholder4.getCountry():""%>" <%=disableShareHolder3%> /> <%if(validationErrorList.getError("shareholderprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCountry()) ? ownershipProfileDetailsVO_shareholder4.getCountry():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_zip" name="shareholderprofile4_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getZipcode()) ? ownershipProfileDetailsVO_shareholder4.getZipcode():""%>" <%=disableShareHolder4%>/> <%if(validationErrorList.getError("shareholderprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_type%></label>
                                                                        <select class="form-control" name="shareholderprofile4_identificationtypeselect" id="shareholderprofile4_identificationtypeselect" class="form-control" <%=globaldisabled%>
                                                                                style="<%=disablecolor%>" <%=disableShareHolder4%>>
                                                                            <%
                                                                                out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_shareholder4.getIdentificationtypeselect():""));
                                                                            %>
                                                                        </select>
                                                                        <%if(validationErrorList.getError("shareholderprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_IdentificationID%></label>
                                                                        <input type="text" class="form-control"  id="shareholderprofile4_identificationtype" name="shareholderprofile4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getIdentificationtype()) ? ownershipProfileDetailsVO_shareholder4.getIdentificationtype():""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                                        <select  class="form-control" id="shareholderprofile4_nationality" name="shareholderprofile4_nationality" style="border: 1px solid #b2b2b2;"<%=disableShareHolder4%>   <%=globaldisabled%>>
                                                                            <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_shareholder3.getNationality()) ? ownershipProfileDetailsVO_shareholder4.getNationality():"")%>
                                                                        </select><%if(validationErrorList.getError("shareholderprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile4_passportissuedate" name="shareholderprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getPassportissuedate()):""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-4 has-feedback">
                                                                        <label for="shareholderprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                                        <input type="text" class="form-control datepicker"  id="shareholderprofile4_Passportexpirydate" name="shareholderprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_shareholder4.getPassportexpirydate()):""%>" <%=disableShareHolder4%>/><%if(validationErrorList.getError("shareholderprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                                    </div>

                                                                    <div class="form-group col-md-12 has-feedback"></div>

                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal4%>">
                                                                        <label for="shareholderprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                                                                        <font class="radio_align"><input type="radio" id="shareholderprofile4_politicallyexposed" name="shareholderprofile4_politicallyexposed"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile4_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                                    </div>
                                                                    <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;<%=disablePoliticalCriminal4%>">
                                                                        <label for="shareholderprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                                        <font class="radio_align"><input type="radio" id="shareholderprofile4_criminalrecord" name="shareholderprofile4_criminalrecord"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_shareholder4.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                                            <input type="radio" name="shareholderprofile4_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_shareholder4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_shareholder4.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownwershipprofile_shareholder3%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <center><h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;">
                            <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownwershipprofile_please2%></h5></center>

                        <div class="col-sm-8 portlets ui-sortable">
                            <label for="numOfCorporateShareholders" style="font-family:Open Sans;font-size: 13px;font-weight: 600;margin-left: 10%;"><%=ownwershipprofile_corporate%></label>
                        </div>
                        <div class="col-sm-4 portlets ui-sortable">
                            <%--
                                                            <input type="text" class="form-control"  id="numOfCorporateShareholders" name="numOfCorporateShareholders" onkeypress="return isNumber(event)" style="border: 1px solid #b2b2b2;font-weight:bold;width: 92%;" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfCorporateShareholders())?ownershipProfileVO.getNumOfCorporateShareholders():"0"%>"/><%if(validationErrorList.getError("numOfCorporateShareholders")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                            --%>
                            <select name="numOfCorporateShareholders" id="numOfCorporateShareholders"  value="">
                                <option value=""><%=ownwershipprofile_select_count%></option>
                                <option value="0"><%=ownwershipprofile_count0%></option>
                                <option value="1"><%=ownwershipprofile_1%></option>
                                <option value="2"><%=ownwershipprofile_2%></option>
                                <option value="3"><%=ownwershipprofile_3%></option>
                                <option value="4"><%=ownwershipprofile_4%></option>

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
                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_profile1%></h2>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Corporate1%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_Name" name="corporateshareholder1_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getName()) ? ownershipProfileDetailsVO_corporateShareholder1.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder1_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Registration%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_RegNumber" name="corporateshareholder1_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder1.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder1_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage_holding%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_owned" name="corporateshareholder1_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder1.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder1_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                                                                <select class="form-control" name="corporateshareholder1_addressproof" id="corporateshareholder1_addressproof"class="form-control" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddressProof() : ""));
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder1_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_addressId" name="corporateshareholder1_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder1_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_Address" name="corporateshareholder1_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder1.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder1_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_Street" name="corporateshareholder1_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder1.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder1_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_City" name="corporateshareholder1_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getCity()) ? ownershipProfileDetailsVO_corporateShareholder1.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder1_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_State" name="corporateshareholder1_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getState()) ? ownershipProfileDetailsVO_corporateShareholder1.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder1_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label>
                                                                <select  name="corporateshareholder1_Country" class="form-control" <%=globaldisabled%> >
                                                                    <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder1.getCountry():"")%>
                                                                </select><%if(validationErrorList.getError("corporateshareholder1_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_ZipCode" name="corporateshareholder1_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder1.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder1_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                                                                <select class="form-control" id="corporateshareholder1_identificationtypeselect" name="corporateshareholder1_identificationtypeselect" id="corporateshareholder1_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtypeselect():""));
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder1_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder1_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder1_identificationtype" name="corporateshareholder1_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder1_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                        </div>
                                                        <div class="corporate2">
                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_profile2%></h2>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Corporate2%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_Name" name="corporateshareholder2_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getName()) ? ownershipProfileDetailsVO_corporateShareholder2.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder2_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Registration%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_RegNumber" name="corporateshareholder2_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder2.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder2_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage_holding%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_owned" name="corporateshareholder2_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder2.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder2_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                                                                <select class="form-control" name="corporateshareholder2_addressproof" id="corporateshareholder2_addressproof"class="form-control" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddressProof() : ""));
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_addressId" name="corporateshareholder2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_Address" name="corporateshareholder2_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder2.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder2_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_Street" name="corporateshareholder2_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder2.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder2_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_City" name="corporateshareholder2_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getCity()) ? ownershipProfileDetailsVO_corporateShareholder2.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder2_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_State" name="corporateshareholder2_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getState()) ? ownershipProfileDetailsVO_corporateShareholder2.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label>
                                                                <select  name="corporateshareholder2_Country" class="form-control" <%=globaldisabled%> >
                                                                    <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder2.getCountry():"")%>
                                                                </select><%if(validationErrorList.getError("corporateshareholder2_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_ZipCode" name="corporateshareholder2_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder2.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder2_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>


                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                                                                <select class="form-control" id="corporateshareholder2_identificationtypeselect" name="corporateshareholder2_identificationtypeselect" id="corporateshareholder2_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtypeselect():""));
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder2_identificationtype" name="corporateshareholder2_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                        </div>
                                                        <div class="corporate3">
                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_profile3%></h2>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Corporate3%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_Name" name="corporateshareholder3_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getName()) ? ownershipProfileDetailsVO_corporateShareholder3.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder3_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Registration%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_RegNumber" name="corporateshareholder3_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder3.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder3_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage_holding%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_owned" name="corporateshareholder3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder3.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                                                                <select class="form-control" name="corporateshareholder3_addressproof" id="corporateshareholder3_addressproof"class="form-control" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddressProof() : ""));
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_addressId" name="corporateshareholder3_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_Address" name="corporateshareholder3_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder3.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder3_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_Street" name="corporateshareholder3_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder3.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder3_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_City" name="corporateshareholder3_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getCity()) ? ownershipProfileDetailsVO_corporateShareholder3.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder3_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_State" name="corporateshareholder3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getState()) ? ownershipProfileDetailsVO_corporateShareholder3.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label >
                                                                <select  name="corporateshareholder3_Country" class="form-control" <%=globaldisabled%> >
                                                                    <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder3.getCountry():"")%>
                                                                </select><%if(validationErrorList.getError("corporateshareholder3_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_ZipCode" name="corporateshareholder3_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder3.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder3_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                                                                <select class="form-control" id="corporateshareholder3_identificationtypeselect" name="corporateshareholder3_identificationtypeselect" id="corporateshareholder3_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtypeselect():""));
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder3_identificationtype" name="corporateshareholder3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtype()) ? ownershipProfileDetailsVO_corporateShareholder3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("corporateshareholder3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                        </div>
                                                        <!--- share holder 4---->
                                                        <div class="corporate4">
                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_profile4%></h2>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_Name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Corporate4%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_Name" name="corporateshareholder4_Name"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getName()) ? ownershipProfileDetailsVO_corporateShareholder4.getName():""%>" /><%if(validationErrorList.getError("corporateshareholder4_Name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_RegNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Registration%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_RegNumber" name="corporateshareholder4_RegNumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getRegistrationNumber()) ? ownershipProfileDetailsVO_corporateShareholder4.getRegistrationNumber():""%>"/><%if(validationErrorList.getError("corporateshareholder4_RegNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage_holding%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_owned" name="corporateshareholder4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getOwned()) ? ownershipProfileDetailsVO_corporateShareholder4.getOwned():""%>"/><%if(validationErrorList.getError("corporateshareholder_owned")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                                                                <select class="form-control" name="corporateshareholder4_addressproof" id="corporateshareholder4_addressproof"class="form-control" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddressProof()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddressProof() : ""));
                                                                        //System.out.println("addressproof :"+ownershipProfileDetailsVO_corporateShareholder4.getAddressProof());
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_addressId" name="corporateshareholder4_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddressId()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddressId():""%>"/><%if(validationErrorList.getError("corporateshareholder4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_Address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateAddress%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_Address" name="corporateshareholder4_Address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getAddress()) ? ownershipProfileDetailsVO_corporateShareholder4.getAddress():""%>"/><%if(validationErrorList.getError("corporateshareholder4_Address")!=null){%> <i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_Street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporateStreet%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_Street" name="corporateshareholder4_Street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getStreet()) ? ownershipProfileDetailsVO_corporateShareholder4.getStreet():""%>"/><%if(validationErrorList.getError("corporateshareholder4_Street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_City" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecity%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_City" name="corporateshareholder4_City"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getCity()) ? ownershipProfileDetailsVO_corporateShareholder4.getCity():""%>"/><%if(validationErrorList.getError("corporateshareholder4_City")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatestate%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_State" name="corporateshareholder4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getState()) ? ownershipProfileDetailsVO_corporateShareholder4.getState():""%>"/><%if(validationErrorList.getError("corporateshareholder4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporatecountry%></label >
                                                                <select  name="corporateshareholder4_Country" class="form-control" <%=globaldisabled%> >
                                                                    <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getCountry()) ? ownershipProfileDetailsVO_corporateShareholder4.getCountry():"")%>
                                                                </select><%if(validationErrorList.getError("corporateshareholder4_Country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_ZipCode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_zip%></label>
                                                                <input type="text" class="form-control"  id="corporateshareholder4_ZipCode" name="corporateshareholder4_ZipCode"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getZipcode()) ? ownershipProfileDetailsVO_corporateShareholder4.getZipcode():""%>"/><%if(validationErrorList.getError("corporateshareholder4_ZipCode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>

                                                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_corporate_identification%></h2>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_type%></label>
                                                                <select class="form-control" id="corporateshareholder4_identificationtypeselect" name="corporateshareholder4_identificationtypeselect" id="corporateshareholder4_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                                    <%
                                                                        out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_corporateShareholder4.getIdentificationtypeselect():""));
                                                                    %>
                                                                </select>
                                                                <%if(validationErrorList.getError("corporateshareholder4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                            </div>
                                                            <div class="form-group col-md-4 has-feedback">
                                                                <label for="corporateshareholder4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_corporate_Id%></label>
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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ownwershipprofile_director%></strong></h2>
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
                                                <%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():ownwershipprofile_corporate_please3%></h5></center>

                                            <div class="col-sm-4 portlets ui-sortable">
                                                <label for="numOfDirectors" style="font-family:Open Sans;font-size: 13px;font-weight: 600;margin-left: 10%;"><%=ownwershipprofile_directors%></label>
                                            </div>
                                            <div class="col-sm-3 portlets ui-sortable">
                                                <%--<input type="text" class="form-control"  id="numOfDirectors" name="numOfDirectors" onkeypress="return isNumber(event)" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileVO.getNumOfDirectors())?ownershipProfileVO.getNumOfDirectors():"1"%>"/><%if(validationErrorList.getError("numOfDirectors")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>--%>
                                                <select name="numOfDirectors" id="numOfDirectors"  value="">
                                                    <option value=""><%=ownwershipprofile_select_count%></option>
                                                    <option value="1"><%=ownwershipprofile_1%></option>
                                                    <option value="2"><%=ownwershipprofile_2%></option>
                                                    <option value="3"><%=ownwershipprofile_3%></option>
                                                    <option value="4"><%=ownwershipprofile_4%></option>

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
                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color"  style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_director_profile1%></h2>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Title%></label>
                                                    <select class="form-control" id="directorsprofile_title" name="directorsprofile_title" <%=globaldisabled%>>
                                                        <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                        <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownwershipprofile_MR%></option>
                                                        <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownwershipprofile_MRS%></option>
                                                        <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownwershipprofile_MS%></option>
                                                        <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownwershipprofile_MISS%></option>
                                                        <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                                                        <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director1.getTitle()) ?"selected":""%>><%=ownwershipprofile_DR%></option>

                                                    </select> <%if(validationErrorList.getError("directorsprofile_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile" name="directorsprofile"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getFirstname()) ? ownershipProfileDetailsVO_director1.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_Name%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_lastname" name="directorsprofile_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getLastname()) ? ownershipProfileDetailsVO_director1.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_DateBirth%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile_dateofbirth" name="directorsprofile_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_telnocc1" name="directorsprofile_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getTelnocc1()) ? ownershipProfileDetailsVO_director1.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_PhoneNo%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_telephonenumber" name="directorsprofile_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getTelephonenumber()) ? ownershipProfileDetailsVO_director1.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Email%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_emailaddress" name="directorsprofile_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getEmailaddress()) ? ownershipProfileDetailsVO_director1.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Percentage%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_owned" name="directorsprofile_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getOwned()) ? ownershipProfileDetailsVO_director1.getOwned():""%>" onchange="return myDirector(this.value,'directorsprofile2_owned','directorsprofile3_owned','directorsprofile4_owned',1)"/><%if(validationErrorList.getError("directorsprofile_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_Proof%></label>
                                                    <select class="form-control" name="directorsprofile_addressproof" id="directorsprofile_addressproof"class="form-control" <%=globaldisabled%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director1.getAddressProof()) ? ownershipProfileDetailsVO_director1.getAddressProof() : ""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_addressId" name="directorsprofile_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getAddressId()) ? ownershipProfileDetailsVO_director1.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_address" name="directorsprofile_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getAddress()) ? ownershipProfileDetailsVO_director1.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Street%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_street" name="directorsprofile_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getStreet()) ? ownershipProfileDetailsVO_director1.getStreet():""%>"/><%if(validationErrorList.getError("directorsprofile_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_city" name="directorsprofile_city" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getCity()) ? ownershipProfileDetailsVO_director1.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_State" name="directorsprofile_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getState()) ? ownershipProfileDetailsVO_director1.getState():""%>"/><%if(validationErrorList.getError("directorsprofile_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Country1%></label>
                                                    <select  name="directorsprofile_country" id="directorsprofile_country" <%=globaldisabled%> onchange="myjunk1('directorsprofile_country','directorsprofile_telnocc1');"  class="form-control">
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director1.getCountry()) ? ownershipProfileDetailsVO_director1.getCountry():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_zip" name="directorsprofile_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getZipcode()) ? ownershipProfileDetailsVO_director1.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_type%></label>
                                                    <select class="form-control" name="directorsprofile_identificationtypeselect" id="directorsprofile_identificationtypeselect"class="txtbox" <%=globaldisabled%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director1.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director1.getIdentificationtypeselect():""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_IdentificationID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile_identificationtype" name="directorsprofile_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getIdentificationtype()) ? ownershipProfileDetailsVO_director1.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>


                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                    <select  class="form-control" id="directorsprofile_nationality" name="directorsprofile_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%>>
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director1.getNationality()) ? ownershipProfileDetailsVO_director1.getNationality():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile_passportissuedate" name="directorsprofile_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile_Passportexpirydate" name="directorsprofile_Passportexpirydate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%>  value="<%=functions.isValueNull(ownershipProfileDetailsVO_director1.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director1.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-12 has-feedback"></div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile_politicallyexposed" name="directorsprofile_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile_politicallyexposed" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile_criminalrecord" name="directorsprofile_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile_criminalrecord" <%=globaldisabled%> value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getCriminalrecord()) ?"checked":""%> />&nbsp;No</font>
                                                </div>
                                            </div>
                                            <div class="dir2">

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_director_profile2%></h2>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_title2%></label>
                                                    <select class="form-control" id="directorsprofile2_title" name="directorsprofile2_title" <%=globaldisabled%> <%=disableDirector2%>>
                                                        <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                        <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownwershipprofile_MR%></option>
                                                        <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownwershipprofile_MRS%></option>
                                                        <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownwershipprofile_MS%></option>
                                                        <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownwershipprofile_MISS%></option>
                                                        <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                                                        <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director2.getTitle()) ?"selected":""%>><%=ownwershipprofile_DR%></option>

                                                    </select> <%if(validationErrorList.getError("directorsprofile2_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2" name="directorsprofile2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getFirstname()) ? ownershipProfileDetailsVO_director2.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile2_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_lastname" name="directorsprofile2_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getLastname()) ? ownershipProfileDetailsVO_director2.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile2_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Date1%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile2_dateofbirth" name="directorsprofile2_dateofbirth" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile2_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile2_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_code1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_telnocc1" name="directorsprofile2_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getTelnocc1()) ? ownershipProfileDetailsVO_director2.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile2_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile2_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_phoneno1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_telephonenumber" name="directorsprofile2_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getTelephonenumber()) ? ownershipProfileDetailsVO_director2.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile2_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_email1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_emailaddress" name="directorsprofile2_emailaddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getEmailaddress()) ? ownershipProfileDetailsVO_director2.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile2_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_owned" name="directorsprofile2_owned" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getOwned()) ? ownershipProfileDetailsVO_director2.getOwned():""%>" onchange="return myDirector(this.value,'directorsprofile_owned','directorsprofile3_owned','directorsprofile4_owned',2)"/><%if(validationErrorList.getError("directorsprofile2_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address_Details1%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressProof1%></label>
                                                    <select class="form-control" name="directorsprofile2_addressproof" id="directorsprofile2_addressproof"class="form-control" <%=globaldisabled%> <%=disableDirector2%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director2.getAddressProof()) ? ownershipProfileDetailsVO_director2.getAddressProof() : ""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile2_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_ID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_addressId" name="directorsprofile2_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getAddressId()) ? ownershipProfileDetailsVO_director2.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile2_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Address1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_address" name="directorsprofile2_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getAddress()) ? ownershipProfileDetailsVO_director2.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile2_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Street1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_street" name="directorsprofile2_street"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getStreet()) ? ownershipProfileDetailsVO_director2.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile2_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_city" name="directorsprofile2_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getCity()) ? ownershipProfileDetailsVO_director2.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile2_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_State" name="directorsprofile2_State"style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getState()) ? ownershipProfileDetailsVO_director2.getState():""%>"/><%if(validationErrorList.getError("directorsprofile2_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_country1%></label>
                                                    <select id="directorsprofile2_country" name="directorsprofile2_country" onchange="myjunk1('directorsprofile2_country','directorsprofile2_telnocc1');" class="form-control" <%=globaldisabled%> <%=disableDirector2%>>
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director2.getCountry()) ? ownershipProfileDetailsVO_director2.getCountry():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile2_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_zip" name="directorsprofile2_zip"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getZipcode()) ? ownershipProfileDetailsVO_director2.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile2_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_type1%></label>
                                                    <select class="form-control" name="directorsprofile2_identificationtypeselect" id="directorsprofile2_identificationtypeselect" <%=globaldisabled%> <%=disableDirector2%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director2.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director2.getIdentificationtypeselect():""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile2_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_identify1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile2_identificationtype" name="directorsprofile2_identificationtype" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getIdentificationtype()) ? ownershipProfileDetailsVO_director2.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile2_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                    <select  class="form-control" id="directorsprofile2_nationality" name="directorsprofile2_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableDirector2%>>
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director2.getNationality()) ? ownershipProfileDetailsVO_director2.getNationality():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile2_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile2_passportissuedate" name="directorsprofile2_passportissuedate"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile2_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile2_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile2_Passportexpirydate" name="directorsprofile2_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector2%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director2.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director2.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile2_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-12 has-feedback"></div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile2_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile2_politicallyexposed" name="directorsprofile2_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile2_politicallyexposed" <%=globaldisabled%> <%=disableDirector2%> value="N" <%="N".equals(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director1.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile2_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile2_criminalrecord" name="directorsprofile2_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector2%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director2.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile2_criminalrecord" <%=globaldisabled%> <%=disableDirector2%> value="N" <%="N".equals(ownershipProfileDetailsVO_director2.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director2.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                                                </div>
                                            </div>
                                            <div class="dir3">

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_director_profile3%></h2>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_title2%></label>
                                                    <select class="form-control" id="directorsprofile3_title" name="directorsprofile3_title" <%=globaldisabled%> <%=disableDirector3%>>
                                                        <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                        <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownwershipprofile_MR%></option>
                                                        <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownwershipprofile_MRS%></option>
                                                        <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownwershipprofile_MS%></option>
                                                        <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownwershipprofile_MISS%></option>
                                                        <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                                                        <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director3.getTitle())?"selected":""%>><%=ownwershipprofile_DR%></option>

                                                    </select> <%if(validationErrorList.getError("directorsprofile3_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile3" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3" name="directorsprofile3"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getFirstname()) ? ownershipProfileDetailsVO_director3.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile3")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile3_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_lastname" name="directorsprofile3_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getLastname()) ? ownershipProfileDetailsVO_director3.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile3_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Date1%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile3_dateofbirth" name="directorsprofile3_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile3_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile3_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_code1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_telnocc1" name="directorsprofile3_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getTelnocc1()) ? ownershipProfileDetailsVO_director3.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile3_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile3_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_phoneno1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_telephonenumber" name="directorsprofile3_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getTelephonenumber()) ? ownershipProfileDetailsVO_director3.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile3_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_email1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_emailaddress" name="directorsprofile3_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getEmailaddress()) ? ownershipProfileDetailsVO_director3.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile3_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_owned" name="directorsprofile3_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getOwned()) ? ownershipProfileDetailsVO_director3.getOwned():""%>" onchange="return myDirector(this.value,'directorsprofile_owned','directorsprofile2_owned','directorsprofile4_owned',3)"/><%if(validationErrorList.getError("directorsprofile3_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_Proof%></label>
                                                    <select class="form-control" name="directorsprofile3_addressproof" id="directorsprofile3_addressproof"class="form-control" <%=globaldisabled%> <%=disableDirector3%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director3.getAddressProof()) ? ownershipProfileDetailsVO_director3.getAddressProof() : ""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile3_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_addressId" name="directorsprofile3_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getAddressId()) ? ownershipProfileDetailsVO_director3.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile3_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_address" name="directorsprofile3_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getAddress()) ? ownershipProfileDetailsVO_director3.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile3_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Street1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_street" name="directorsprofile3_street" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getStreet()) ? ownershipProfileDetailsVO_director3.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile3_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_city" name="directorsprofile3_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getCity()) ? ownershipProfileDetailsVO_director3.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile3_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#4984a9"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_State" name="directorsprofile3_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getState()) ? ownershipProfileDetailsVO_director3.getState():""%>"/><%if(validationErrorList.getError("directorsprofile3_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_country1%></label>
                                                    <select id="directorsprofile3_country" <%=globaldisabled%> <%=disableDirector3%> name="directorsprofile3_country" onchange="myjunk1('directorsprofile3_country','directorsprofile3_telnocc1');"  class="form-control">
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director3.getCountry()) ? ownershipProfileDetailsVO_director3.getCountry():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile3_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_zip" name="directorsprofile3_zip" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getZipcode()) ? ownershipProfileDetailsVO_director3.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile3_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_type1%></label>
                                                    <select class="form-control" name="directorsprofile3_identificationtypeselect" id="directorsprofile3_identificationtypeselect"class="txtbox" <%=globaldisabled%> <%=disableDirector3%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director3.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director3.getIdentificationtypeselect():""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile3_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_identify1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile3_identificationtype" name="directorsprofile3_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getIdentificationtype()) ? ownershipProfileDetailsVO_director3.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile3_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>



                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                    <select  class="form-control" id="directorsprofile3_nationality" name="directorsprofile3_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableDirector3%>>
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director3.getNationality()) ? ownershipProfileDetailsVO_director3.getNationality():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile3_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile3_passportissuedate" name="directorsprofile3_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile3_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile3_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile3_Passportexpirydate" name="directorsprofile3_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector3%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director3.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director3.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile3_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-12 has-feedback"></div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile3_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile3_politicallyexposed" name="directorsprofile3_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile3_politicallyexposed" <%=globaldisabled%> <%=disableDirector3%> value="N" <%="N".equals(ownershipProfileDetailsVO_director3.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director3.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile3_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile3_criminalrecord" name="directorsprofile3_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile3_criminalrecord" <%=globaldisabled%> <%=disableDirector3%> value="N" <%="N".equals(ownershipProfileDetailsVO_director3.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director3.getCriminalrecord()) ?"checked":""%> />&nbsp;<%=ownwershipprofile_No%></font>
                                                </div>
                                            </div>

                                            <!--director 4-->

                                            <div class="dir4">

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:#ffffff;"><%=ownwershipprofile_director_profile4%></h2>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_title2%></label>
                                                    <select class="form-control" id="directorsprofile4_title" name="directorsprofile4_title" <%=globaldisabled%> <%=disableDirector4%>>
                                                        <option value=""><%=ownwershipprofile_Select_Title%></option>
                                                        <option value="MR" <%="MR".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownwershipprofile_MR%></option>
                                                        <option value="MRS" <%="MRS".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownwershipprofile_MRS%></option>
                                                        <option value="MS" <%="MS".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownwershipprofile_MS%></option>
                                                        <option value="MISS" <%="MISS".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownwershipprofile_MISS%></option>
                                                        <option value="MASTER" <%="MASTER".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownwershipprofile_MASTER%></option>
                                                        <option value="DR" <%="DR".equals(ownershipProfileDetailsVO_director4.getTitle())?"selected":""%>><%=ownwershipprofile_DR%></option>

                                                    </select> <%if(validationErrorList.getError("directorsprofile4_title")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_First%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4" name="directorsprofile4"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getFirstname()) ? ownershipProfileDetailsVO_director4.getFirstname():""%>" /><%if(validationErrorList.getError("directorsprofile4")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label for="directorsprofile4_lastname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_last%>&nbsp;<%=ownwershipprofile_director_Name1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_lastname" name="directorsprofile4_lastname"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getLastname()) ? ownershipProfileDetailsVO_director4.getLastname():""%>" /><%if(validationErrorList.getError("directorsprofile4_lastname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_dateofbirth" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Date1%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile4_dateofbirth" name="directorsprofile4_dateofbirth"style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getDateofbirth()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getDateofbirth()):""%>"/><%if(validationErrorList.getError("directorsprofile4_dateofbirth")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile4_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_code1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_telnocc1" name="directorsprofile4_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getTelnocc1()) ? ownershipProfileDetailsVO_director4.getTelnocc1():""%>"/> <%if(validationErrorList.getError("directorsprofile4_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-2 has-feedback">
                                                    <label for="directorsprofile4_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_phoneno1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_telephonenumber" name="directorsprofile4_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getTelephonenumber()) ? ownershipProfileDetailsVO_director4.getTelephonenumber():""%>"/> <%if(validationErrorList.getError("directorsprofile4_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px;margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_emailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_email1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_emailaddress" name="directorsprofile4_emailaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getEmailaddress()) ? ownershipProfileDetailsVO_director4.getEmailaddress():""%>"/><%if(validationErrorList.getError("directorsprofile4_emailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_owned" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_percentage1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_owned" name="directorsprofile4_owned"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getOwned()) ? ownershipProfileDetailsVO_director4.getOwned():""%>" onchange="return myDirector(this.value,'directorsprofile_owned','directorsprofile2_owned','directorsprofile3_owned',4)"/><%if(validationErrorList.getError("directorsprofile4_owned")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Address%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Address_Proof%></label>
                                                    <select class="form-control" name="directorsprofile4_addressproof" id="directorsprofile4_addressproof"class="form-control" <%=globaldisabled%> <%=disableDirector4%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getAddressProofTypes(functions.isValueNull(ownershipProfileDetailsVO_director4.getAddressProof()) ? ownershipProfileDetailsVO_director4.getAddressProof() : ""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile4_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_AddressID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_addressId" name="directorsprofile4_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getAddressId()) ? ownershipProfileDetailsVO_director4.getAddressId():""%>"/><%if(validationErrorList.getError("directorsprofile4_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Address1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_address" name="directorsprofile4_address"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getAddress()) ? ownershipProfileDetailsVO_director4.getAddress():""%>"/><%if(validationErrorList.getError("directorsprofile4_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_Street1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_street" name="directorsprofile4_street" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getStreet()) ? ownershipProfileDetailsVO_director4.getStreet():""%>"/> <%if(validationErrorList.getError("directorsprofile4_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_City%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_city" name="directorsprofile4_city"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getCity()) ? ownershipProfileDetailsVO_director4.getCity():""%>"/><%if(validationErrorList.getError("directorsprofile4_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#4984a9"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_ID%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_State" name="directorsprofile4_State"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getState()) ? ownershipProfileDetailsVO_director4.getState():""%>"/><%if(validationErrorList.getError("directorsprofile4_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_country1%></label>
                                                    <select id="directorsprofile4_country" <%=globaldisabled%> <%=disableDirector4%> name="directorsprofile4_country" onchange="myjunk1('directorsprofile4_country','directorsprofile4_telnocc1');"  class="form-control">
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director4.getCountry()) ? ownershipProfileDetailsVO_director4.getCountry():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile4_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_zip" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_zip%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_zip" name="directorsprofile4_zip" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getZipcode()) ? ownershipProfileDetailsVO_director4.getZipcode():""%>"/><%if(validationErrorList.getError("directorsprofile4_zip")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;"><%=ownwershipprofile_Identification%></h2>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_identificationtypeselect" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_type1%></label>
                                                    <select class="form-control" name="directorsprofile4_identificationtypeselect" id="directorsprofile4_identificationtypeselect"class="txtbox" <%=globaldisabled%> <%=disableDirector4%>>
                                                        <%
                                                            out.println(commonFunctionUtil.getIdentificationType(functions.isValueNull(ownershipProfileDetailsVO_director4.getIdentificationtypeselect()) ? ownershipProfileDetailsVO_director4.getIdentificationtypeselect():""));
                                                        %>
                                                    </select>
                                                    <%if(validationErrorList.getError("directorsprofile4_identificationtypeselect")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_identificationtype" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_director_identify1%></label>
                                                    <input type="text" class="form-control"  id="directorsprofile4_identificationtype" name="directorsprofile4_identificationtype"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getIdentificationtype()) ? ownershipProfileDetailsVO_director4.getIdentificationtype():""%>"/> <%if(validationErrorList.getError("directorsprofile4_identificationtype")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>



                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_nationality" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_Nationality%></label>
                                                    <select  class="form-control" id="directorsprofile4_nationality" name="directorsprofile4_nationality" style="border: 1px solid #b2b2b2;"<%=globaldisabled%> <%=disableDirector4%>>
                                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(ownershipProfileDetailsVO_director4.getNationality()) ? ownershipProfileDetailsVO_director4.getNationality():"")%>
                                                    </select><%if(validationErrorList.getError("directorsprofile4_nationality")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_passportissuedate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_issue%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile4_passportissuedate" name="directorsprofile4_passportissuedate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getPassportissuedate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getPassportissuedate()):""%>"/><%if(validationErrorList.getError("directorsprofile4_passportissuedate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label for="directorsprofile4_Passportexpirydate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=ownwershipprofile_expiry%></label>
                                                    <input type="text" class="form-control datepicker"  id="directorsprofile4_Passportexpirydate" name="directorsprofile4_Passportexpirydate" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  <%=globaldisabled%> <%=disableDirector4%> value="<%=functions.isValueNull(ownershipProfileDetailsVO_director4.getPassportexpirydate()) ? commonFunctionUtil.convertTimestampToDatepicker(ownershipProfileDetailsVO_director4.getPassportexpirydate()):""%>"/><%if(validationErrorList.getError("directorsprofile4_Passportexpirydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                                </div>

                                                <div class="form-group col-md-12 has-feedback"></div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile4_politicallyexposed" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_politically%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile4_politicallyexposed" name="directorsprofile4_politicallyexposed" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="Y" <%="Y".equals(ownershipProfileDetailsVO_director4.getPoliticallyexposed())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile4_politicallyexposed" <%=globaldisabled%> <%=disableDirector4%> value="N" <%="N".equals(ownershipProfileDetailsVO_director4.getPoliticallyexposed()) || !functions.isValueNull(ownershipProfileDetailsVO_director4.getPoliticallyexposed()) ?"checked":""%>/>&nbsp;<%=ownwershipprofile_No%></font>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                                    <label for="directorsprofile4_criminalrecord" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=ownwershipprofile_existence%></label>
                                                    <font class="radio_align"><input type="radio" id="directorsprofile4_criminalrecord" name="directorsprofile4_criminalrecord" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="Y" <%="Y".equals(ownershipProfileDetailsVO_director3.getCriminalrecord())?"checked":""%> />&nbsp;<%=ownwershipprofile_Yes%>&nbsp;
                                                        <input type="radio" name="directorsprofile4_criminalrecord" <%=globaldisabled%> <%=disableDirector4%> value="N" <%="N".equals(ownershipProfileDetailsVO_director4.getCriminalrecord()) || !functions.isValueNull(ownershipProfileDetailsVO_director4.getCriminalrecord()) ?"checked":""%> />&nbsp;No</font>
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
                "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;Ownership Profile</strong></h2>\n" +
                "                                <div class=\"additional-btn\">\n" +
                "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                "                                </div>\n" +
                "                            </div>");
        out.println("<div class=\"widget-content padding\">");
        out.println(Functions.NewShowConfirmation1("Profile","There is no details that has to be provided for this profile"));
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
    }
%>