<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.CardholderProfileVO" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/13/15
  Time: 5:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<script type="text/javascript">
  $('#sandbox-container input').datepicker({
  });
</script>
<script>
  $(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});

    /*************************************Cardholder Profile JS****************************************/


    $( "input[name='compliance_swapp']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=compliance_swapp]:checked').val()=="N")
      {
        document.myformname.compliance_thirdpartyappform.disabled = true;
        document.myformname.compliance_thirdpartyappform.value = "";

        document.myformname.compliance_thirdpartysoft.disabled= true;
        document.myformname.compliance_thirdpartysoft.value = "";

        document.myformname.compliance_version.disabled= true;
        document.myformname.compliance_version.value = "";

        document.getElementById('compliance_companiesorgatewaysY').disabled = true;
        document.getElementById('compliance_companiesorgatewaysN').disabled = true;

        document.myformname.compliance_companiesorgateways_yes.disabled= true;
        document.myformname.compliance_companiesorgateways_yes.value = "";

        $('input:radio[name=compliance_companiesorgateways]').each(function(){
          $(this).attr('disabled','disabled');
          $(this).parent('.iradio_square-aero').addClass('disabled');
        });
      }
      else
      {
        document.myformname.compliance_thirdpartyappform.disabled = false;
        document.myformname.compliance_thirdpartysoft.disabled= false;
        document.myformname.compliance_version.disabled= false;
        document.getElementById('compliance_companiesorgatewaysY').disabled = false;
        document.getElementById('compliance_companiesorgatewaysN').disabled = false;
        $('input:radio[name=compliance_companiesorgateways]').each(function(){
          $(this).removeAttr('disabled');
          $(this).parent('.iradio_square-aero').removeClass('disabled');
        });
      }
    });


    $( "input[name='compliance_companiesorgateways']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=compliance_companiesorgateways]:checked').val()=="N")
      {
        document.myformname.compliance_companiesorgateways_yes.disabled = true;
        document.myformname.compliance_companiesorgateways_yes.value = "";

      }
      else
      {
        document.myformname.compliance_companiesorgateways_yes.disabled = false;
      }
    });


    $( "input[name='compliance_electronically']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=compliance_electronically]:checked').val()=="N")
      {
        $('input:radio[name=compliance_carddatastored]').each(function(){
          $(this).attr('disabled','disabled');
          $(this).parent('.iradio_square-aero').addClass('disabled');
        });

      }
      else
      {
        $('input:radio[name=compliance_carddatastored]').each(function(){
          $(this).removeAttr('disabled');
          $(this).parent('.iradio_square-aero').removeClass('disabled');
        });
      }
    });


    $( "input[name='compliance_cispcompliant']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=compliance_cispcompliant]:checked').val()=="N")
      {
        document.myformname.compliance_cispcompliant_yes.disabled = true;
        document.myformname.compliance_cispcompliant_yes.value = "";

      }
      else
      {
        document.myformname.compliance_cispcompliant_yes.disabled = false;
      }
    });


    $( "input[name='compliance_pcidsscompliant']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=compliance_pcidsscompliant]:checked').val()=="N")
      {
        document.myformname.compliance_pcidsscompliant_yes.disabled = true;
        document.myformname.compliance_pcidsscompliant_yes.value = "";
      }
      else
      {
        document.myformname.compliance_pcidsscompliant_yes.disabled = false;
      }
    });


    $( "input[name='compliance_datacompromise']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=compliance_datacompromise]:checked').val()=="N")
      {
        document.myformname.compliance_datacompromise_yes.disabled = true;
        document.myformname.compliance_datacompromise_yes.value = "";

      }
      else
      {
        document.myformname.compliance_datacompromise_yes.disabled = false;
      }
    });


    $( "input[name='siteinspection_merchant']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=siteinspection_merchant]:checked').val()=="N")
      {
        document.myformname.siteinspection_landlord.disabled = true;
        document.myformname.siteinspection_landlord.value = "";

      }
      else
      {
        document.myformname.siteinspection_landlord.disabled = false;
      }
    });

  });
</script>

<style type="text/css">
  /*    .input-group-addon{
          font-weight: 800;
          background-color: #ebccd1;
          color: #a94442;
          font-size: 18px;
      }*/

  .glyphicon{
    display: block!important;
    color:#a94442!important;
    background-color: #ebccd1!important;
    width: 40px!important;
    margin-right: 16px!important;
    height: 32px!important;
    /*margin-top: -1px!important;*/
    top: inherit!important;
    margin-top: -33px!important;
  }

  /*@media (min-width: 404px) and (max-width: 642px){
      #glipmedia {
          top: inherit!important;
          margin-top: -32px!important;
      }
  }


  @media (max-width: 403px){
      #glipmedia {
          top: inherit!important;
          margin-top: -32px!important;
      }
  }*/

</style>

<%!
  private Functions functions = new Functions();
  private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
%>
<%
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String cardholderprofile_Cardholder_Profile = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Cardholder_Profile")) ? rb1.getString("cardholderprofile_Cardholder_Profile") : "Cardholder Profile";
  String cardholderprofile_please = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_please")) ? rb1.getString("cardholderprofile_please") : "Please save Cardholder Profile after entering the data provided";
  String cardholderprofile_data_storage = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_data_storage")) ? rb1.getString("cardholderprofile_data_storage") : "CARDHOLDER DATA STORAGE COMPLIANCE";
  String cardholderprofile_software = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_software")) ? rb1.getString("cardholderprofile_software") : "Are you using software or gateway application?*";
  String cardholderprofile_invalid_software = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_invalid_software")) ? rb1.getString("cardholderprofile_invalid_software") : "Invalid software or gateway application";
  String cardholderprofile_Yes = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Yes")) ? rb1.getString("cardholderprofile_Yes") : "Yes";
  String cardholderprofile_No = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_No")) ? rb1.getString("cardholderprofile_No") : "No";
  String cardholderprofile_third = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_third")) ? rb1.getString("cardholderprofile_third") : "What third party software company/vendor did you purchase your Application from?*";
  String cardholderprofile_third_party = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_third_party")) ? rb1.getString("cardholderprofile_third_party") : "What is the name of the third party software?* ";
  String cardholderprofile_Version = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Version")) ? rb1.getString("cardholderprofile_Version") : "Version #?";
  String cardholderprofile_companies = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_companies")) ? rb1.getString("cardholderprofile_companies") : "Do you use any third parties,web hosting companies or gateways as service provider and is their name and email address provided on your website?";
  String cardholderprofile_vendor = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_vendor")) ? rb1.getString("cardholderprofile_vendor") : "Do you or your vendor receive, pass, transmit or store the full cardholder number, electronically?";
  String cardholderprofile_if = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_if")) ? rb1.getString("cardholderprofile_if") : "If yes, where is card data stored?";
  String cardholderprofile_Merchant = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Merchant")) ? rb1.getString("cardholderprofile_Merchant") : "Merchant";
  String cardholderprofile_Third_Party = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Third_Party")) ? rb1.getString("cardholderprofile_Third_Party") : " Third Party Only";
  String cardholderprofile_both = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_both")) ? rb1.getString("cardholderprofile_both") : "Both Merchant & Third Party";
  String cardholderprofile_security = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_security")) ? rb1.getString("cardholderprofile_security") : "Company is CISP (Cardholder Information Security Program) compliant?";
  String cardholderprofile_details = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_details")) ? rb1.getString("cardholderprofile_details") : "If yes please provide more details";
  String cardholderprofile_industry = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_industry")) ? rb1.getString("cardholderprofile_industry") : "Are you or your vendor PCI/DSS (Payment Card Industry/Data Security Standard) compliant?";
  String cardholderprofile_qualified = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_qualified")) ? rb1.getString("cardholderprofile_qualified") : "What is the name of your Qualified Security Assessor?";
  String cardholderprofile_Date_compliance = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Date_compliance")) ? rb1.getString("cardholderprofile_Date_compliance") : "Date of compliance";
  String cardholderprofile_late_compliance = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_late_compliance")) ? rb1.getString("cardholderprofile_late_compliance") : "Date of Last scan";
  String cardholderprofile_experienced = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_experienced")) ? rb1.getString("cardholderprofile_experienced") : "Have you ever experienced an account data compromise?*";
  String cardholderprofile_when = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_when")) ? rb1.getString("cardholderprofile_when") : "If yes, when";
  String cardholderprofile_card_association = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_card_association")) ? rb1.getString("cardholderprofile_card_association") : "***** Card Association requirements dictate it is prohibited to store track data in any circumstance. Further, it is recommended that no merchant or a merchant's thirdparty vendor store cardholder data. If you or your vendor store data, you or your vendor are required to be PCI DSS compliant. Failure to adhere to these requirement may result in fines or loss of card acceptance. *****";
  String cardholderprofile_Merchant1 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Merchant1")) ? rb1.getString("cardholderprofile_Merchant1") : "Merchant:";
  String cardholderprofile_Landlord = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Landlord")) ? rb1.getString("cardholderprofile_Landlord") : "Landlord:";
  String cardholderprofile_Building_Type = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Building_Type")) ? rb1.getString("cardholderprofile_Building_Type") : "Building Type:";
  String cardholderprofile_Shopping_Ctr = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Shopping_Ctr")) ? rb1.getString("cardholderprofile_Shopping_Ctr") : "Shopping Ctr";
  String cardholderprofile_Office_Bldg = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Office_Bldg")) ? rb1.getString("cardholderprofile_Office_Bldg") : "Office Bldg";
  String cardholderprofile_Industrial_Bldg = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Industrial_Bldg")) ? rb1.getString("cardholderprofile_Industrial_Bldg") : "Industrial Bldg";
  String cardholderprofile_Residence = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Residence")) ? rb1.getString("cardholderprofile_Residence") : " Residence";
  String cardholderprofile_Area_Zoned = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Area_Zoned")) ? rb1.getString("cardholderprofile_Area_Zoned") : "Area Zoned:";
  String cardholderprofile_Commercial = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Commercial")) ? rb1.getString("cardholderprofile_Commercial") : "Commercial";
  String cardholderprofile_Industrial = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Industrial")) ? rb1.getString("cardholderprofile_Industrial") : "Industrial";
  String cardholderprofile_Residential = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Residential")) ? rb1.getString("cardholderprofile_Residential") : "Residential";
  String cardholderprofile_square = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_square")) ? rb1.getString("cardholderprofile_square") : "Square Footage/m2:";
  String cardholderprofile_0_500 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_0_500")) ? rb1.getString("cardholderprofile_0_500") : " 0-500";
  String cardholderprofile_501_2500 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_501_2500")) ? rb1.getString("cardholderprofile_501_2500") : "501-2500";
  String cardholderprofile_2501_5000 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_2501_5000")) ? rb1.getString("cardholderprofile_2501_5000") : "2501-5000";
  String cardholderprofile_5001_10000 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_5001_10000")) ? rb1.getString("cardholderprofile_5001_10000") : "5001-10000+";
  String cardholderprofile_appropriate = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_appropriate")) ? rb1.getString("cardholderprofile_appropriate") : "Does Merchant have the appropriate facilities, equipment, inventory, personnel and license/permit to operate their business?";
  String cardholderprofile_Declarations = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Declarations")) ? rb1.getString("cardholderprofile_Declarations") : "Declarations:";
  String cardholderprofile_hereby = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_hereby")) ? rb1.getString("cardholderprofile_hereby") : "I hereby confirm to be the owner of the listed website(s). I further declare to have full control and authorization of the website content. I acknowledge and agree that I will not use the Processing System for transactions relating to; 1) Sales made under a different trade name or business affiliation than indicated on this Agreement or otherwise approved by the acquirer in writing; 2) Fines or Penalties of any kind, losses, damages or any other costs that are beyond the Total Sale Price; 3) Any transaction that violates any law, ordinance, or regulation applicable to my business; 4) Goods which I / we know will be resold by a customer whom I / we reasonably should know is not ordinarily in the business of selling such goods; 5) Sales by third parties; 6) Any other amounts for which a customer has not specifically authorized payment through the acquirer; 7) Cash, traveler's checks, Cash equivalents, or other negotiable instruments; or 8) Amounts which do not represent a bona fide sale of goods or services by me / us. I also declare on behalf of the company and on behalf of myself that, to the best of our knowledge, neither the company nor the website nor myself (or any of us) have ever been involved in excessive chargeback’s, fraud or content violation nor have any of the above ever terminated by an acquirer or asked by an acquirer to terminate an agreement within a set period of time.";
  String cardholderprofile_Investigate_Consumer = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Investigate_Consumer")) ? rb1.getString("cardholderprofile_Investigate_Consumer") : "Investigate Consumer Report";
  String cardholderprofile_investigative = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_investigative")) ? rb1.getString("cardholderprofile_investigative") : "An investigative or consumer report may be made in connection with application. Merchant authorizes any party to the agreement or any of their agents to investigate the reference provided or any other statements or data obtained from merchant and from any of the undersigned personal guarantor(s), or from any person or entity with any financial obligations under this agreement. You have a right, upon written request, to a complete and accurate disclosure of the nature of and scope of the investigation requested.";
  String cardholderprofile_Principal = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Principal")) ? rb1.getString("cardholderprofile_Principal") : "Principal 1";
  String cardholderprofile_Date = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Date")) ? rb1.getString("cardholderprofile_Date") : "Date";
  String cardholderprofile_Principal2 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Principal2")) ? rb1.getString("cardholderprofile_Principal2") : "Principal 2";
  String cardholderprofile_CardHolder_Profile = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_CardHolder_Profile")) ? rb1.getString("cardholderprofile_CardHolder_Profile") : "CardHolder Profile";
  String cardholderprofile_Profile = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Profile")) ? rb1.getString("cardholderprofile_Profile") : "Profile";
  String cardholderprofile_no_details = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_no_details")) ? rb1.getString("cardholderprofile_no_details") : "There is no details that has to be provided for this profile";

  ApplicationManagerVO applicationManagerVO=null;
  CardholderProfileVO cardholderProfileVO=null;
  ValidationErrorList validationErrorList=null;
  if(session.getAttribute("applicationManagerVO")!=null)
  {
    applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
  }

  if(applicationManagerVO.getCardholderProfileVO()!=null)
  {
    cardholderProfileVO=applicationManagerVO.getCardholderProfileVO();
  }
  if(cardholderProfileVO==null)
  {
    cardholderProfileVO=new CardholderProfileVO();
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

  String disabled="";
  String checkedN="";
  String checkedY="";


  String electroDisabled="";

  if(!functions.isValueNull(cardholderProfileVO.getCompliance_swapp()) || "N".equals(cardholderProfileVO.getCompliance_swapp()))
  {
    disabled="disabled";
    checkedN="checked";
  }
  else
  {
    checkedY="checked";
  }
  if(!functions.isValueNull(cardholderProfileVO.getCompliance_electronically()) || "N".equals(cardholderProfileVO.getCompliance_electronically()))
  {
    electroDisabled="disabled";
  }
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

  //for specific condition

  Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
  Map<Boolean, Set<BankInputName>> fullPageViseValidationForStep=new HashMap<Boolean, Set<BankInputName>>();

  Set<BankInputName> cardholderProfileInputList=new HashSet<BankInputName>();

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
        cardholderProfileInputList.addAll(fullPageViseValidationForStep.get(false));
      if(fullPageViseValidationForStep.containsKey(true))
        cardholderProfileInputList.addAll(fullPageViseValidationForStep.get(true));
    }
  }
  //end
%>
<%
  if(cardholderProfileInputList.contains(BankInputName.compliance_swapp)||
          cardholderProfileInputList.contains(BankInputName.compliance_thirdpartyappform)||
          cardholderProfileInputList.contains(BankInputName.compliance_thirdpartysoft)||
          cardholderProfileInputList.contains(BankInputName.compliance_version)||
          cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways)||
          cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways_yes)||
          cardholderProfileInputList.contains(BankInputName.compliance_electronically)||
          cardholderProfileInputList.contains(BankInputName.compliance_cispcompliant)||
          cardholderProfileInputList.contains(BankInputName.compliance_cispcompliant_yes)||
          cardholderProfileInputList.contains(BankInputName.compliance_pcidsscompliant)||
          cardholderProfileInputList.contains(BankInputName.compliance_qualifiedsecurityassessor)||
          cardholderProfileInputList.contains(BankInputName.compliance_dateofcompliance)||
          cardholderProfileInputList.contains(BankInputName.compliance_datacompromise)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_merchant)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_landlord)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_buildingtype)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_areazoned)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_squarefootage)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_operatebusiness)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_merchant)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_landlord)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_buildingtype)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_areazoned)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_squarefootage)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_operatebusiness)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_principal1)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_principal1_date)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_principal2)||
          cardholderProfileInputList.contains(BankInputName.siteinspection_principal2_date)||view)
  {
%>

<div class="content-page" id="cardid">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">

        <div class="col-sm-12 portlets ui-sortable">

          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=cardholderprofile_Cardholder_Profile%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">
                <div class="form foreground bodypanelfont_color panelbody_color">
                  <center><h4 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():cardholderprofile_please%></h4></center>
                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"> <%=cardholderprofile_data_storage%></h2>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_swapp)||cardholderProfileInputList.contains(BankInputName.compliance_thirdpartyappform)||cardholderProfileInputList.contains(BankInputName.compliance_thirdpartysoft)||cardholderProfileInputList.contains(BankInputName.compliance_version)||cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways)||cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways_yes)|| view)
                    {
                  %>
                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">&nbsp; <%=cardholderprofile_software%></label>
                    &nbsp;&nbsp;<input type="radio"  name="compliance_swapp"  <%=globaldisabled%>  value="Y" <%=checkedY%>/><%if(validationErrorList.getError("compliance_swapp")!=null){%><span style="width:13%" class="apperrormsg"><%=cardholderprofile_invalid_software%></span><%}%> <%=cardholderprofile_Yes%>
                    &nbsp;&nbsp;<input type="radio" name="compliance_swapp"  <%=globaldisabled%>  value="N" <%=checkedN%> />  <%=cardholderprofile_No%>
                  </div>
                  <div class="form-group col-md-6 has-feedback">
                    <label for="compliance_thirdpartyappform" style="font-family:Open Sans;font-size: 13.4px;font-weight: 600;">&nbsp; <%=cardholderprofile_third%></label>
                    <input type="text" class="form-control" id="compliance_thirdpartyappform" name="compliance_thirdpartyappform"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartyappform())==true?cardholderProfileVO.getCompliance_thirdpartyappform():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_thirdpartyappform")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                  </div>
                  <div class="form-group col-md-6 has-feedback">
                    <label for="compliance_thirdpartysoft" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_third_party%> <font style="color:transparent; user-select: none;">purchase your Application from</font></label>
                    <input type="text" class="form-control"  id="compliance_thirdpartysoft" name="compliance_thirdpartysoft"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartysoft())==true?cardholderProfileVO.getCompliance_thirdpartysoft():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_thirdpartysoft")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                  </div>
                  <div class="col-md-12"></div>
                  <div class="form-group col-md-6 has-feedback">
                    <label for="compliance_thirdpartysoft" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_Version%></label>
                    <input type="text" class="form-control" id="compliance_version" name="compliance_version"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>   value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_version())==true?cardholderProfileVO.getCompliance_version():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_version")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                  </div>


                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_companies%></label>
                    </div>
                    <div class="col-md-6">
                      <input type="radio" name="compliance_companiesorgateways" id="compliance_companiesorgatewaysY" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_companiesorgateways())?"checked":""%> <%=disabled%>/><%if(validationErrorList.getError("compliance_companiesorgateways")!=null){%><span style="width:13%" class="apperrormsg">Invalid companiesorgateways</span><%}%>
                      <%=cardholderprofile_Yes%>&nbsp;&nbsp;
                      <input type="radio" name="compliance_companiesorgateways" id="compliance_companiesorgatewaysN" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getCompliance_companiesorgateways()) || !functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways()) ?"checked":""%> <%=disabled%>/>         <%--Condition for Radio--%>
                      <%=cardholderprofile_No%><%--&nbsp;&nbsp;&nbsp;&nbsp;If yes, who is it?--%>
                    </div>
                  </div>

                  <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <input type="text" class="form-control" name="compliance_companiesorgateways_yes"<%-- style="margin-left: 5%;width: 96%"--%> <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways_yes())==true?cardholderProfileVO.getCompliance_companiesorgateways_yes():""%>" name="compliance_companiesorgateways_yes" <%=!functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways())||"N".equals(cardholderProfileVO.getCompliance_companiesorgateways())?"disabled":""%> <%=disabled%>/><%if(validationErrorList.getError("compliance_companiesorgateways_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <%
                    }
                  %>

                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_electronically) || cardholderProfileInputList.contains(BankInputName.compliance_carddatastored)|| view)
                    {
                  %>

                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_vendor%></label>
                    </div>
                    <div class="col-md-6">
                      <input type="radio"  name="compliance_electronically" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_electronically())?"checked":""%> /><%if(validationErrorList.getError("compliance_electronically")!=null){%><span style="width:13%" class="apperrormsg">Invalid electronically</span><%}%>
                      <%=cardholderprofile_Yes%>&nbsp;&nbsp;
                      <input type="radio" name="compliance_electronically"<%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_electronically()) || !functions.isValueNull(cardholderProfileVO.getCompliance_electronically()) ?"checked":""%> />            <%--Condition for Radio--%>
                      <%=cardholderprofile_No%>
                    </div>
                  </div>


                  <div class="form-group col-md-10" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_if%></label>

                    <div class="col-md-10">
                      <input type="radio" id="cd1" name="compliance_carddatastored" <%=globaldisabled%>  value="Merchant"   <%=electroDisabled%><%%> <%="Merchant".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                      <%=cardholderprofile_Merchant%>
                      &nbsp;&nbsp; <input type="radio"  id="cd2" name="compliance_carddatastored" <%=globaldisabled%>  value="ThirdParty" <%=electroDisabled%> <%="ThirdParty".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                     <%=cardholderprofile_Third_Party%>
                      &nbsp;&nbsp; <input type="radio"  id="cd3" name="compliance_carddatastored" <%=globaldisabled%>  value="Both" <%=electroDisabled%> <%=!functions.isValueNull(cardholderProfileVO.getCompliance_carddatastored())||"Both".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                      <%=cardholderprofile_both%>
                    </div>
                  </div>
                  <%
                    }
                  %>

                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_cispcompliant)||cardholderProfileInputList.contains(BankInputName.compliance_cispcompliant_yes)||view)
                    {
                      String cisCompliantDisabled = "";
                      if("N".equals(cardholderProfileVO.getCompliance_cispcompliant()) || !functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant()))
                      {
                        cisCompliantDisabled = "disabled";
                      }
                      if("disabled".equals(globaldisabled)){
                        cisCompliantDisabled = "disabled";
                      }
                  %>

                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_security%></label>
                    </div>
                    <div class="col-md-6">
                      &nbsp;&nbsp;<input type="radio"  name="compliance_cispcompliant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_cispcompliant())?"checked":""%> />
                      <%=cardholderprofile_Yes%>&nbsp;&nbsp;
                      <input type="radio" name="compliance_cispcompliant" <%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_cispcompliant()) || !functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant()) ?"checked":""%> />
                      <%=cardholderprofile_No%>
                    </div>
                  </div>

                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_details%></label>
                    </div>

                    <div class="col-md-6">
                      <input type="text" class="form-control"  id="compliance_cispcompliant_yes" name="compliance_cispcompliant_yes"style="border: 1px solid #b2b2b2;font-weight:bold;margin-left:0%;" <%=cisCompliantDisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant_yes())==true?cardholderProfileVO.getCompliance_cispcompliant_yes():""%>" />
                      <%if(validationErrorList.getError("compliance_cispcompliant_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>
                  </div>
                  <%
                    }
                  %>

                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_pcidsscompliant)||cardholderProfileInputList.contains(BankInputName.compliance_pcidsscompliant_yes)||view)
                    {
                  %>

                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_industry%></label>
                    </div>
                    <div class="col-md-6">
                      &nbsp;&nbsp;<input type="radio"  name="compliance_pcidsscompliant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_pcidsscompliant())?"checked":""%> />
                      <%=cardholderprofile_Yes%>&nbsp;&nbsp;
                      <input type="radio" name="compliance_pcidsscompliant" <%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_pcidsscompliant()) || !functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant()) ?"checked":""%> />
                      <%=cardholderprofile_No%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </div>
                  </div>

                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_details%></label>
                    </div>

                    <div class="col-md-6">
                      <input type="text" class="form-control"  id="compliance_pcidsscompliant_yes" name="compliance_pcidsscompliant_yes"style="border: 1px solid #b2b2b2;font-weight:bold;margin-left:0%;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant_yes())==true?cardholderProfileVO.getCompliance_pcidsscompliant_yes():""%>"<%=!functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant_yes())||"N".equals(cardholderProfileVO.getCompliance_pcidsscompliant_yes())?"disabled":""%> />
                      <%if(validationErrorList.getError("compliance_pcidsscompliant_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                    </div>

                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_qualifiedsecurityassessor)||view)
                    {
                  %>

                  <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label for="compliance_qualifiedsecurityassessor" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_qualified%></label>
                    <input type="text" class="form-control"  id="compliance_qualifiedsecurityassessor" name="compliance_qualifiedsecurityassessor"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_qualifiedsecurityassessor())==true?cardholderProfileVO.getCompliance_qualifiedsecurityassessor():""%>" /><%if(validationErrorList.getError("compliance_qualifiedsecurityassessor")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 37px;height: 32px; margin-top: -1px;margin-top: 25px"></i><%}%>
                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_dateofcompliance)||view)
                    {
                  %>

                  <div class="form-group col-md-3">
                    <label for="compliance_dateofcompliance" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_Date_compliance%></label>
                    <input type="text" class="form-control datepicker"  id="compliance_dateofcompliance" name="compliance_dateofcompliance" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%;"  <%=globaldisabled%>   value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_dateofcompliance())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getCompliance_dateofcompliance()):""%>"/><%if(validationErrorList.getError("compliance_dateofcompliance")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_dateoflastscan)||view)
                    {
                  %>
                  <div class="form-group col-md-3">
                    <label for="compliance_dateoflastscan" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_late_compliance%></label>
                    <input type="text" class="form-control datepicker"  id="compliance_dateoflastscan" name="compliance_dateoflastscan" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%;" <%=globaldisabled%>  <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_dateoflastscan())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getCompliance_dateoflastscan()):""%>" /><%if(validationErrorList.getError("compliance_dateoflastscan")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.compliance_datacompromise)||view)
                    {
                  %>
                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_experienced%></label>
                    </div>
                    <div class="col-md-6">
                      &nbsp;&nbsp;<input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" name="compliance_datacompromise" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_datacompromise())?"checked":""%>/>
                      <%=cardholderprofile_Yes%>&nbsp;&nbsp;
                      <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" name="compliance_datacompromise" <%=globaldisabled%>  value="N"  <%=!functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise())||"N".equals(cardholderProfileVO.getCompliance_datacompromise())?"checked":""%>/> <%--this is radio button  we can use condition--%>
                      <%=cardholderprofile_No%>
                    </div>
                  </div>

                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding-left: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_when%></label>
                    </div>

                    <div class="col-md-6">
                      <input type="text" name="compliance_datacompromise_yes" class="form-control datepicker" style="width: 100%!important;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise_yes())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getCompliance_datacompromise_yes()):""%>"<%=!functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise())||"N".equals(cardholderProfileVO.getCompliance_datacompromise())?"disabled":""%>>
                      <%if(validationErrorList.getError("compliance_datacompromise_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="border-top-right-radius: 5px!important; border-bottom-right-radius: 5px!important; margin-right: 0!important;"></i><%}%>
                    </div>

                  </div>


                  <label class="newborder">    <%=cardholderprofile_card_association%></label>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_merchant)||cardholderProfileInputList.contains(BankInputName.siteinspection_landlord)||cardholderProfileInputList.contains(BankInputName.siteinspection_buildingtype)||cardholderProfileInputList.contains(BankInputName.siteinspection_areazoned)||cardholderProfileInputList.contains(BankInputName.siteinspection_squarefootage)||cardholderProfileInputList.contains(BankInputName.siteinspection_operatebusiness)||view)
                    {

                  %>


                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">SITE INSPECTION</h2>

                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_merchant)||cardholderProfileInputList.contains(BankInputName.siteinspection_landlord)||view)
                    {

                  %>

                  <div class="form-group col-md-12">
                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_Merchant1%></label>
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_merchant" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getSiteinspection_merchant()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_merchant()) ?"checked":""%>/>&nbsp;Owns
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_merchant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getSiteinspection_merchant())?"checked":""%>/>&nbsp;Rents
                  </div>

                  <div class="form-group col-md-8">
                    <label class="col-sm-2 control-label" style="font-weight: inherit;font-size: inherit;"><%=cardholderprofile_Landlord%></label>
                    <div class="col-sm-6">
                      <input type="text"  class="form-control" name="siteinspection_landlord" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getSiteinspection_landlord())==true?cardholderProfileVO.getSiteinspection_landlord():""%>" name="siteinspection_landlord" <%=!functions.isValueNull(cardholderProfileVO.getSiteinspection_merchant())||"N".equals(cardholderProfileVO.getSiteinspection_merchant())?"disabled":""%> /><%if(validationErrorList.getError("siteinspection_landlord")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                    </div>
                  </div>

                  <%

                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_buildingtype)||view)
                    {

                  %>

                  <div class="form-group col-md-12">
                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_Building_Type%></label>
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="ShoppingCtr" <%="ShoppingCtr".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%>/>&nbsp;
                    <%=cardholderprofile_Shopping_Ctr%>
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="OfficeBldg" <%="OfficeBldg".equals(cardholderProfileVO.getSiteinspection_buildingtype()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_buildingtype()) ?"checked":""%> />&nbsp;
                    <%=cardholderprofile_Office_Bldg%>
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="IndustrialBldg" <%="IndustrialBldg".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%> />&nbsp;
                    <%=cardholderprofile_Industrial_Bldg%>
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="Residence" <%="Residence".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%>/>&nbsp;
                   <%=cardholderprofile_Residence%>
                  </div>

                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_areazoned)||view)
                    {

                  %>
                  <div class="form-group col-md-12">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_Area_Zoned%></label>
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Commercial" <%="Commercial".equals(cardholderProfileVO.getSiteinspection_areazoned()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()) ?"checked":""%> />&nbsp;
                    <%=cardholderprofile_Commercial%>
                  </div>
                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Industrial" <%="Industrial".equals(cardholderProfileVO.getSiteinspection_areazoned())?"checked":""%>/>&nbsp;
                    <%=cardholderprofile_Industrial%>
                  </div>
                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Residential" <%="Residential".equals(cardholderProfileVO.getSiteinspection_areazoned())?"checked":""%>/>&nbsp;
                    <%=cardholderprofile_Residential%>
                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_squarefootage)||view)
                    {

                  %>
                  <div class="form-group col-md-12">
                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_square%></label>
                  </div>

                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="0-500" <%="0-500".equals(cardholderProfileVO.getSiteinspection_squarefootage()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()) ?"checked":""%> />&nbsp;
                   <%=cardholderprofile_0_500%>
                  </div>
                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="501-2500" <%="501-2500".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%> />&nbsp;
                    <%=cardholderprofile_501_2500%>
                  </div>
                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="2501-5000" <%="2501-5000".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%>/>&nbsp;
                    <%=cardholderprofile_2501_5000%>
                  </div>
                  <div class="form-group col-lg-2 col-md-4">
                    <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="5001-10000+" <%="5001-10000+".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%> />&nbsp;
                    <%=cardholderprofile_5001_10000%>
                  </div>

                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_operatebusiness)||view)
                    {

                  %>
                  <div class="form-group col-md-12"></div>
                  <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <div class="col-md-6" style="padding: 0;">
                      <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_appropriate%></label>
                    </div>
                    <div class="col-md-6">
                      <input type="radio" name="siteinspection_operatebusiness" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getSiteinspection_operatebusiness())?"checked":""%> />
                      <%=cardholderprofile_Yes%>&nbsp;&nbsp;
                      <input type="radio" name="siteinspection_operatebusiness" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getSiteinspection_operatebusiness()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_operatebusiness()) ?"checked":""%> />
                      <%=cardholderprofile_No%>
                    </div>
                  </div>
                  <%
                    }
                  %>
                  <label class="newborder bg-info" style="text-align: justify;">     <b><%=cardholderprofile_Declarations%></b><br />
                    <%=cardholderprofile_hereby%><br /><br/>
                    <b><%=cardholderprofile_Investigate_Consumer%></b><br />
                    <%=cardholderprofile_investigative%></label>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_principal1)||view)
                    {
                  %>
                  <div class="form-group col-md-3">
                    <label for="siteinspection_principal1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_Principal%></label>
                    <input type="text" class="form-control"  id="siteinspection_principal1" name="siteinspection_principal1" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1())==true?cardholderProfileVO.getSiteinspection_principal1():""%>" /><%if(validationErrorList.getError("siteinspection_principal1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_principal1_date)||view)
                    {
                  %>

                  <div class="form-group col-md-3">
                    <label for="siteinspection_principal1_date" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_Date%></label>
                    <input type="text"  class="form-control datepicker" id="siteinspection_principal1_date" name="siteinspection_principal1_date"style="border: 1px solid #b2b2b2;font-weight:bold;width: 100%;" <%=globaldisabled%>    value="<%=functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1_date())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getSiteinspection_principal1_date()):""%>" /><%if(validationErrorList.getError("siteinspection_principal1_date")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_principal2)||view)
                    {
                  %>

                  <div class="form-group col-md-3">
                    <label for="siteinspection_principal2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_Principal2%></label>
                    <input type="text" class="form-control"  id="siteinspection_principal2" name="siteinspection_principal2"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2())==true?cardholderProfileVO.getSiteinspection_principal2():""%>" /><%if(validationErrorList.getError("siteinspection_principal2")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                  </div>
                  <%
                    }
                  %>
                  <%
                    if(cardholderProfileInputList.contains(BankInputName.siteinspection_principal2_date)||view)
                    {
                  %>

                  <div class="form-group col-md-3">
                    <label for="siteinspection_principal2_date" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_Date%></label>
                    <input type="text" class="form-control datepicker"  id="siteinspection_principal2_date" name="siteinspection_principal2_date" style="border: 1px solid #b2b2b2;font-weight:bold;width: 100%;" <%=globaldisabled%>   value="<%=functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2_date())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getSiteinspection_principal2_date()):""%>" /><%if(validationErrorList.getError("siteinspection_principal2_date")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                  </div>

                </div>

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
                %>
                <%

                  if(cardholderProfileInputList.size()==0 && !view)
                  {
                    out.println("<div class=\"content-page\">");
                    out.println("<div class=\"content\">");
                    out.println("<div class=\"page-heading\">");
                    out.println("<div class=\"row\">");
                    out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
                    out.println("<div class=\"widget\">");
                    out.println("<div class=\"widget-header transparent\">\n" +
                            "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;"+cardholderprofile_CardHolder_Profile+"</strong></h2>\n" +
                            "                                <div class=\"additional-btn\">\n" +
                            "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                            "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                            "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                            "                                </div>\n" +
                            "                            </div>");
                    out.println("<div class=\"widget-content padding\">");
          /*out.println("<div class=\"table-responsive\">");*/
                    out.println(Functions.NewShowConfirmation1(cardholderprofile_Profile,cardholderprofile_no_details));         /* out.println("</div>");*/
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                  }
                %>