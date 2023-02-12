<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.ExtraDetailsProfileVO" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/15
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<%--<link rel="stylesheet" href="/partner/transactionCSS/css/main.css">
<script type="text/javascript" src="/partner/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/partner/transactionCSS/js/creditly.js"></script>--%>
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
    /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
  });
</script>
<script>
  $(function() {
    $( "input[name='company_financialreport']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=company_financialreport]:checked').val()=="N")
      {
        document.myformname.company_financialreportyes.disabled = true;
        document.myformname.company_financialreportyes.value = "";
      }
      else
      {
        document.myformname.company_financialreportyes.disabled = false;
      }
    });

    $( "input[name='financialreport_available']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=financialreport_available]:checked').val()=="N")
      {
        document.myformname.financialreport_availableyes.disabled = true;
        document.myformname.financialreport_availableyes.value = "";
      }
      else
      {
        document.myformname.financialreport_availableyes.disabled = false;
      }
    });

    $( "input[name='compliance_punitivesanction']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=compliance_punitivesanction]:checked').val()=="N")
      {
        document.myformname.compliance_punitivesanctionyes.disabled = true;
        document.myformname.compliance_punitivesanctionyes.value = "";
      }
      else
      {
        document.myformname.compliance_punitivesanctionyes.disabled = false;
      }
    });

    $( "input[name='deedofagreement']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=deedofagreement]:checked').val()=="N")
      {
        document.myformname.deedofagreementyes.disabled = true;
        document.myformname.deedofagreementyes.value = "";
      }
      else
      {
        document.myformname.deedofagreementyes.disabled = false;
      }
    });

    $( "input[name='fulfillment_productemail']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=fulfillment_productemail]:checked').val()=="N")
      {
        document.myformname.fulfillment_productemailyes.disabled = true;
        document.myformname.fulfillment_productemailyes.value = "";
      }
      else
      {
        document.myformname.fulfillment_productemailyes.disabled = false;
      }
    });

    $( "input[name='blacklistedaccountclosed']").next('.iCheck-helper').on( "click", function() {
      if($('input:radio[name=blacklistedaccountclosed]:checked').val()=="N")
      {
        document.myformname.blacklistedaccountclosedyes.disabled = true;
        document.myformname.blacklistedaccountclosedyes.value = "";
      }
      else
      {
        document.myformname.blacklistedaccountclosedyes.disabled = false;
      }
    });

    $( "input[name='compliance_cispcompliant']").next('.iCheck-helper').on( "click", function() {
      if($ ('input:radio[name=compliance_cispcompliant]:checked').val()=="N")
      {
        document.myformname.compliance_cispcompliant_yes.disabled = true;
        document.myformname.compliance_cispcompliant_yes.value = "";
      }
      else
      {
        document.myformname.compliance_cispcompliant_yes.disabled = false;
      }
    })

    $( "input[name='compliance_pcidsscompliant']").next('.iCheck-helper').on( "click", function() {
      if($ ('input:radio[name=compliance_pcidsscompliant]:checked').val()=="N")
      {
        document.myformname.compliance_pcidsscompliant_yes.disabled = true;
        document.myformname.compliance_pcidsscompliant_yes.value = "";
      }
      else
      {
        document.myformname.compliance_pcidsscompliant_yes.disabled = false;
      }
    });

    $( "input[name='seasonal_fluctuating']").next('.iCheck-helper').on( "click", function() {
      if($ ('input:radio[name=seasonal_fluctuating]:checked').val()=="N")
      {
        document.myformname.seasonal_fluctuating_yes.disabled = true;
        document.myformname.seasonal_fluctuating_yes.value = "";
      }
      else
      {
        document.myformname.seasonal_fluctuating_yes.disabled = false;
      }
    });

    $( "input[name='is_website_live']").next('.iCheck-helper').on( "click", function() {
      if($ ('input:radio[name=is_website_live]:checked').val()=="N")
      {
        document.myformname.test_link.disabled = true;
        document.myformname.test_link.value = "";
      }
      else
      {
        document.myformname.test_link.disabled = false;
      }
    });
    $( "input[name='listfraudtools']").next('.iCheck-helper').on( "click", function() {
      if($ ('input:radio[name=listfraudtools]:checked').val()=="N")
      {
        document.myformname.listfraudtools_yes.disabled = true;
        document.myformname.listfraudtools_yes.value = "";
      }
      else
      {
        document.myformname.listfraudtools_yes.disabled = false;
      }
    });

    $( "input[name='agency_employed']").next('.iCheck-helper').on( "click", function()
    {
      if ($('input:radio[name=agency_employed]:checked').val() == "N")
      {
        document.myformname.agency_employed_yes.disabled = true;
        document.myformname.agency_employed_yes.value = "";
      }
      else
      {
        document.myformname.agency_employed_yes.disabled = false;
      }
    });
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
    /*margin-top: -1px!important;*/
    top: inherit!important;
    margin-top: -33px!important;
  }
</style>

<%!
  private Functions functions = new Functions();
  private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
%>
<%
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String extradetailsprofile_Extra_Details_Profile = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Extra_Details_Profile")) ? rb1.getString("extradetailsprofile_Extra_Details_Profile") : "Extra Details Profile";
  String extradetailsprofile_please = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_please")) ? rb1.getString("extradetailsprofile_please") : "Please save Extra Details Profile after entering the data provided";
  String extradetailsprofile_Additional_Details = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Additional_Details")) ? rb1.getString("extradetailsprofile_Additional_Details") : "Additional Company Details-";
  String extradetailsprofile_Owner_Since = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Owner_Since")) ? rb1.getString("extradetailsprofile_Owner_Since") : "Owner Since?";
  String extradetailsprofile_1 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_1")) ? rb1.getString("extradetailsprofile_1") : "1.";
  String extradetailsprofile_2 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_2")) ? rb1.getString("extradetailsprofile_2") : "2.";
  String extradetailsprofile_3 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_3")) ? rb1.getString("extradetailsprofile_3") : "3.";
  String extradetailsprofile_4 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_4")) ? rb1.getString("extradetailsprofile_4") : "4.";
  String extradetailsprofile_5 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_5")) ? rb1.getString("extradetailsprofile_5") : "5.";
  String extradetailsprofile_Socialsecurity = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Socialsecurity")) ? rb1.getString("extradetailsprofile_Socialsecurity") : "Socialsecurity?";
  String extradetailsprofile_form = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_form")) ? rb1.getString("extradetailsprofile_form") : "Form of participation in company?";
  String extradetailsprofile_Financial_Obligation = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Financial_Obligation")) ? rb1.getString("extradetailsprofile_Financial_Obligation") : "Financial Obligation?";
  String extradetailsprofile_working = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_working")) ? rb1.getString("extradetailsprofile_working") : "Working experience in this sphere?";
  String extradetailsprofile_Financial_Reporting = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Financial_Reporting")) ? rb1.getString("extradetailsprofile_Financial_Reporting") : "Financial Reporting-";
  String extradetailsprofile_does = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_does")) ? rb1.getString("extradetailsprofile_does") : "6.Does company should submit financial report in its registration on business activities ?";
  String extradetailsprofile_Yes = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Yes")) ? rb1.getString("extradetailsprofile_Yes") : "Yes";
  String extradetailsprofile_No = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_No")) ? rb1.getString("extradetailsprofile_No") : "No";
  String extradetailsprofile_if = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_if")) ? rb1.getString("extradetailsprofile_if") : "If yes, please provide more details";
  String extradetailsprofile_7 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_7")) ? rb1.getString("extradetailsprofile_7") : "7.";
  String extradetailsprofile_8 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_8")) ? rb1.getString("extradetailsprofile_8") : "8.";
  String extradetailsprofile_9 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_9")) ? rb1.getString("extradetailsprofile_9") : "9.";
  String extradetailsprofile_specify = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_specify")) ? rb1.getString("extradetailsprofile_specify") : "Specify institution for submitting financial reports?";
  String extradetailsprofile_financial = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_financial")) ? rb1.getString("extradetailsprofile_financial") : "Financial report are publically available?";
  String extradetailsprofile_Legal_Matters = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Legal_Matters")) ? rb1.getString("extradetailsprofile_Legal_Matters") : "Legal Matters-";
  String extradetailsprofile_non_compliance = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_non_compliance")) ? rb1.getString("extradetailsprofile_non_compliance") : "Is the company has been under punitive sanction/restriction for non compliance of requirement that regulate mention activity of company?";
  String extradetailsprofile_10 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_10")) ? rb1.getString("extradetailsprofile_10") : "10.";
  String extradetailsprofile_11 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_11")) ? rb1.getString("extradetailsprofile_11") : "11.";
  String extradetailsprofile_12 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_12")) ? rb1.getString("extradetailsprofile_12") : "12.";
  String extradetailsprofile_13 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_13")) ? rb1.getString("extradetailsprofile_13") : "13.";
  String extradetailsprofile_14 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_14")) ? rb1.getString("extradetailsprofile_14") : "14.";
  String extradetailsprofile_15 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_15")) ? rb1.getString("extradetailsprofile_15") : "15.";
  String extradetailsprofile_16 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_16")) ? rb1.getString("extradetailsprofile_16") : "16.";
  String extradetailsprofile_17 = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_17")) ? rb1.getString("extradetailsprofile_17") : "17.";
  String extradetailsprofile_party = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_party")) ? rb1.getString("extradetailsprofile_party") : "Have you ever been adjudiscated bankrupt or party to a deed of arrangement?";
  String extradetailsprofile_describe = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_describe")) ? rb1.getString("extradetailsprofile_describe") : "If yes, describe describe?";
  String extradetailsprofile_Additional_Shipping = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_Additional_Shipping")) ? rb1.getString("extradetailsprofile_Additional_Shipping") : "Additional Shipping Information-";
  String extradetailsprofile_goods = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_goods")) ? rb1.getString("extradetailsprofile_goods") : "Do you offer insurance for goods being sent?";
  String extradetailsprofile_email = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_email")) ? rb1.getString("extradetailsprofile_email") : "Do you send an email recipt to card holder? When the product has been fulfilled?";
  String extradetailsprofile_shipping = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_shipping")) ? rb1.getString("extradetailsprofile_shipping") : "Shipping and delivery methods?";
  String extradetailsprofile_information = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_information")) ? rb1.getString("extradetailsprofile_information") : "Information about transaction monitoring process?";
  String extradetailsprofile_owner = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_owner")) ? rb1.getString("extradetailsprofile_owner") : "Verification of bank account owner?If applicable,a certified copy of an operational license?";
  String extradetailsprofile_blacklisted = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_blacklisted")) ? rb1.getString("extradetailsprofile_blacklisted") : "Have you ever been blacklisted or had an account closed by master card/visa or ACH proceesor ?";
  String extradetailsprofile_whether = StringUtils.isNotEmpty(rb1.getString("extradetailsprofile_whether")) ? rb1.getString("extradetailsprofile_whether") : "Whether the company is a subject of its supervisor regular control?";

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

  ApplicationManagerVO applicationManagerVO=null;
  ExtraDetailsProfileVO extraDetailsProfileVO=null;
  ValidationErrorList validationErrorList=null;
  if(session.getAttribute("applicationManagerVO")!=null)
  {
    applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
  }

  if(applicationManagerVO.getExtradetailsprofileVO()!=null)
  {
    extraDetailsProfileVO=applicationManagerVO.getExtradetailsprofileVO();
  }

  if(extraDetailsProfileVO==null)
  {
    extraDetailsProfileVO=new ExtraDetailsProfileVO();
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


%>
<div class="content-page" id="extraid">
  <div class="content" id="companycontent">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">

        <div class="col-sm-12 portlets ui-sortable">

          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=extradetailsprofile_Extra_Details_Profile%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">

                <center><h4 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():extradetailsprofile_please%></h4></center>


                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=extradetailsprofile_Extra_Details_Profile%></h2>


                <div class="form-group col-md-12  has-feedback">
                  <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=extradetailsprofile_Additional_Details%></u></label>
                </div>


                <div class="form-group col-md-6  has-feedback">
                  <label for="ownersince" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_1%>&nbsp;<%=extradetailsprofile_Owner_Since%></label>
                  <input type="text"  class="form-control datepicker"   id="ownersince" <%=globaldisabled%> name="ownersince" style="border: 1px solid #b2b2b2;font-weight:bold;width: 100%" value="<%=functions.isValueNull(extraDetailsProfileVO.getOwnerSince())==true?commonFunctionUtil.convertTimestampToDatepicker(extraDetailsProfileVO.getOwnerSince()):""%>"/><%if(validationErrorList.getError("ownersince")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-6  has-feedback">
                  <label for="socialsecurity" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_2%>&nbsp;<%=extradetailsprofile_Socialsecurity%></label>
                  <input type="text" class="form-control"   id="socialsecurity" <%=globaldisabled%> name="socialsecurity" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(extraDetailsProfileVO.getSocialSecurity())==true?extraDetailsProfileVO.getSocialSecurity():""%>"/><%if(validationErrorList.getError("socialsecurity")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-6  has-feedback">
                  <label for="company_formparticipation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_3%>&nbsp;<%=extradetailsprofile_form%></label>
                  <input type="text" class="form-control"   id="company_formparticipation" <%=globaldisabled%> name="company_formparticipation" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(extraDetailsProfileVO.getCompany_formParticipation())==true?extraDetailsProfileVO.getCompany_formParticipation():""%>"/><%if(validationErrorList.getError("company_formparticipation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>
                <div class="form-group col-md-6  has-feedback">
                  <label for="financialobligation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_4%>&nbsp;<%=extradetailsprofile_Financial_Obligation%></label>
                  <input type="text" class="form-control"   id="financialobligation" <%=globaldisabled%> name="financialobligation" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(extraDetailsProfileVO.getFinancialObligation())==true?extraDetailsProfileVO.getFinancialObligation():""%>"/><%if(validationErrorList.getError("financialobligation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-6  has-feedback">
                  <label for="workingexperience" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_5%>&nbsp;<%=extradetailsprofile_working%></label>
                  <input type="text" class="form-control"   id="workingexperience" <%=globaldisabled%> name="workingexperience" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(extraDetailsProfileVO.getWorkingExperience())==true?extraDetailsProfileVO.getWorkingExperience():""%>"/><%if(validationErrorList.getError("workingexperience")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>


                <div class="form-group col-md-12">
                  <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=extradetailsprofile_Financial_Reporting%></u></label>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">&nbsp;<%=extradetailsprofile_does%></label>
                  &nbsp;&nbsp;<input type="radio" name="company_financialreport" <%=globaldisabled%> value="Y" <%="Y".equals(extraDetailsProfileVO.getCompany_financialReport())?"checked":""%>/><%--<%if(validationErrorList.getError("company_financialreport")!=null){%><span style="width:13%" class="apperrormsg">Invalid countries_blocked</span><%}%>--%>
                  <%=extradetailsprofile_Yes%>&nbsp;
                  &nbsp;&nbsp;<input type="radio" name="company_financialreport" <%=globaldisabled%> value="N" <%="N".equals(extraDetailsProfileVO.getCompany_financialReport()) || !functions.isValueNull(extraDetailsProfileVO.getCompany_financialReport()) ?"checked":""%> />           <%--Condition for Radio--%>
                  &nbsp;<%=extradetailsprofile_No%> &nbsp;&nbsp;&nbsp;&nbsp;<%=extradetailsprofile_if%>
                  </p><p><input type="text" class="form-control has-feedback" style="width:47%;" <%=globaldisabled%> name="company_financialreportyes" value="<%=functions.isValueNull(extraDetailsProfileVO.getCompany_financialReportYes())==true?extraDetailsProfileVO.getCompany_financialReportYes():""%>" name="company_financialreportyes" <%=!functions.isValueNull(extraDetailsProfileVO.getCompany_financialReport())||"N".equals(extraDetailsProfileVO.getCompany_financialReport())?"disabled":""%> /><%if(validationErrorList.getError("company_financialreportyes")!=null){%><span style="width:18%" class="apperrormsg">Invalid company financial report details</span><%}%></p></td>
                </div>

                <div class="form-group col-md-12  has-feedback">
                  <label for="financialreport_institution" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_7%>&nbsp; <%=extradetailsprofile_specify%></label>
                  <input type="text" class="form-control"   id="financialreport_institution" <%=globaldisabled%> name="financialreport_institution" style="border: 1px solid #b2b2b2;font-weight:bold;width: 49%"  value="<%=functions.isValueNull(extraDetailsProfileVO.getFinancialReport_institution())==true?extraDetailsProfileVO.getFinancialReport_institution():""%>"/><%if(validationErrorList.getError("financialreport_institution")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_8%>&nbsp;<%=extradetailsprofile_financial%></label>
                  &nbsp;&nbsp;<input type="radio" name="financialreport_available" <%=globaldisabled%>  value="Y" <%="Y".equals(extraDetailsProfileVO.getFinancialReport_available())?"checked":""%>/><%--<%if(validationErrorList.getError("financialreport_available")!=null){%><span style="width:13%" class="apperrormsg">Invalid financialreport_available</span><%}%>--%>
                  <%=extradetailsprofile_Yes%>&nbsp;
                  &nbsp;&nbsp;<input type="radio" name="financialreport_available"  <%=globaldisabled%>  value="N" <%="N".equals(extraDetailsProfileVO.getFinancialReport_available()) || !functions.isValueNull(extraDetailsProfileVO.getFinancialReport_available()) ?"checked":""%> />           <%--Condition for Radio--%>
                  &nbsp;<%=extradetailsprofile_No%> &nbsp;&nbsp;&nbsp;&nbsp;<%=extradetailsprofile_if%>
                  </p><p><input type="text" class="form-control has-feedback" style="width:47%;" <%=globaldisabled%> name="financialreport_availableyes" value="<%=functions.isValueNull(extraDetailsProfileVO.getFinancialReport_availableYes())==true?extraDetailsProfileVO.getFinancialReport_availableYes():""%>" name="financialreport_availableyes" <%=!functions.isValueNull(extraDetailsProfileVO.getFinancialReport_available())||"N".equals(extraDetailsProfileVO.getFinancialReport_available())?"disabled":""%> /><%if(validationErrorList.getError("financialreport_availableyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12">
                  <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=extradetailsprofile_Legal_Matters%></u></label>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_9%>&nbsp;<%=extradetailsprofile_non_compliance%></label>
                  &nbsp;&nbsp;<input type="radio" name="compliance_punitivesanction" <%=globaldisabled%>  value="Y" <%="Y".equals(extraDetailsProfileVO.getCompliance_punitiveSanction())?"checked":""%>/><%--<%if(validationErrorList.getError("compliance_punitivesanction")!=null){%><span style="width:13%" class="apperrormsg">Invalid Comliance Punitive Sanction</span><%}%>--%>
                  <%=extradetailsprofile_Yes%>&nbsp;
                  &nbsp;&nbsp;<input type="radio" name="compliance_punitivesanction"  <%=globaldisabled%>  value="N" <%="N".equals(extraDetailsProfileVO.getCompliance_punitiveSanction()) || !functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanction()) ?"checked":""%> />           <%--Condition for Radio--%>
                  &nbsp;<%=extradetailsprofile_No%> &nbsp;&nbsp;&nbsp;&nbsp;<%=extradetailsprofile_if%>
                  </p><p><input type="text" class="form-control has-feedback" style="width:47%;" <%=globaldisabled%> name="compliance_punitivesanctionyes" value="<%=functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanctionYes())==true?extraDetailsProfileVO.getCompliance_punitiveSanctionYes():""%>" name="compliance_punitivesanctionyes" <%=!functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanction())||"N".equals(extraDetailsProfileVO.getCompliance_punitiveSanction())?"disabled":""%> /><%if(validationErrorList.getError("compliance_punitivesanctionyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_10%>&nbsp;<%=extradetailsprofile_party%></label>
                  &nbsp;&nbsp;<input type="radio" name="deedofagreement" <%=globaldisabled%>  value="Y" <%="Y".equals(extraDetailsProfileVO.getDeedOfAgreement())?"checked":""%>/><%--<%if(validationErrorList.getError("deedofagreement")!=null){%><span style="width:13%" class="apperrormsg">Invalid Deed Of Agreement</span><%}%>--%>
                  <%=extradetailsprofile_Yes%>&nbsp;
                  &nbsp;&nbsp;<input type="radio" name="deedofagreement" <%=globaldisabled%>   value="N" <%="N".equals(extraDetailsProfileVO.getDeedOfAgreement()) || !functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreement()) ?"checked":""%> />           <%--Condition for Radio--%>
                  &nbsp;<%=extradetailsprofile_No%> &nbsp;&nbsp;&nbsp;&nbsp;<%=extradetailsprofile_describe%>
                  </p><p><input type="text" class="form-control has-feedback" style="width:47%;" name="deedofagreementyes" value="<%=functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreementYes())==true?extraDetailsProfileVO.getDeedOfAgreementYes():""%>" name="deedofagreementyes" <%=!functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreement())||"N".equals(extraDetailsProfileVO.getDeedOfAgreement())?"disabled":""%> /><%if(validationErrorList.getError("deedofagreementyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12">
                  <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=extradetailsprofile_Additional_Shipping%></u></label>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_11%>&nbsp; <%=extradetailsprofile_goods%></label>
                  &nbsp;&nbsp;<input type="radio" <%=globaldisabled%> name="goodsinsuranceoffered" id="goodsinsuranceofferedY" value="Y" <%="Y".equals(extraDetailsProfileVO.getGoodsInsuranceOffered())?"checked":""%>/><%if(validationErrorList.getError("goodsinsuranceoffered")!=null){%><span style="width:13%" class="apperrormsg">Invalid Goods Insurance Offered</span><%}%>
                  <%=extradetailsprofile_Yes%>
                  &nbsp;&nbsp;<input type="radio" <%=globaldisabled%> name="goodsinsuranceoffered" id="goodsinsuranceofferedN" value="N" <%="N".equals(extraDetailsProfileVO.getGoodsInsuranceOffered()) || !functions.isValueNull(extraDetailsProfileVO.getGoodsInsuranceOffered()) ?"checked":""%>/>         <%--Condition for Radio--%>
                  <%=extradetailsprofile_No%>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_12%>&nbsp;<%=extradetailsprofile_email%></label>
                  &nbsp;&nbsp;<input type="radio" <%=globaldisabled%> name="fulfillment_productemail"  value="Y" <%="Y".equals(extraDetailsProfileVO.getFulfillment_productEmail())?"checked":""%>/><%--<%if(validationErrorList.getError("fulfillment_productemail")!=null){%><span style="width:13%" class="apperrormsg">Invalid Fulfillment Product Email</span><%}%>--%>
                  <%=extradetailsprofile_Yes%>&nbsp;
                  &nbsp;&nbsp;<input type="radio" <%=globaldisabled%> name="fulfillment_productemail"   value="N" <%="N".equals(extraDetailsProfileVO.getFulfillment_productEmail()) || !functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmail()) ?"checked":""%> />           <%--Condition for Radio--%>
                  &nbsp;<%=extradetailsprofile_No%> &nbsp;&nbsp;&nbsp;&nbsp;<%=extradetailsprofile_describe%>
                  </p><p><input type="text" class="form-control has-feedback" style="width:47%;" <%=globaldisabled%> name="fulfillment_productemailyes" value="<%=functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmailYes())==true?extraDetailsProfileVO.getFulfillment_productEmailYes():""%>" name="fulfillment_productemailyes" <%=!functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmail())||"N".equals(extraDetailsProfileVO.getFulfillment_productEmail())?"disabled":""%> /><%if(validationErrorList.getError("fulfillment_productemailyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12  has-feedback">
                  <label for="shiping_deliverymethod" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_13%>&nbsp;<%=extradetailsprofile_shipping%></label>
                  <input type="text" class="form-control"   id="shiping_deliverymethod" <%=globaldisabled%> name="shiping_deliverymethod" style="border: 1px solid #b2b2b2;font-weight:bold;width: 49%"  value="<%=functions.isValueNull(extraDetailsProfileVO.getShiping_deliveryMethod())==true?extraDetailsProfileVO.getShiping_deliveryMethod():""%>"/><%if(validationErrorList.getError("shiping_deliverymethod")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12">
                  <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Account/Transaction Monitoring-</u></label>
                </div>

                <div class="form-group col-md-12  has-feedback">
                  <label for="transactionmonitoringprocess" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_14%>&nbsp;<%=extradetailsprofile_information%></label>
                  <input type="text" class="form-control"   id="transactionmonitoringprocess" <%=globaldisabled%> name="transactionmonitoringprocess" style="border: 1px solid #b2b2b2;font-weight:bold;width: 49%"  value="<%=functions.isValueNull(extraDetailsProfileVO.getTransactionMonitoringProcess())==true?extraDetailsProfileVO.getTransactionMonitoringProcess():""%>"/><%if(validationErrorList.getError("transactionmonitoringprocess")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_15%>&nbsp; <%=extradetailsprofile_owner%></label>
                  &nbsp;&nbsp;<input type="radio" name="operationallicense" <%=globaldisabled%> id="operationallicenseY" value="Y" <%="Y".equals(extraDetailsProfileVO.getOperationalLicense())?"checked":""%>/><%if(validationErrorList.getError("operationallicense")!=null){%><span style="width:13%" class="apperrormsg">Invalid Operational License</span><%}%>
                  <%=extradetailsprofile_Yes%>
                  &nbsp;&nbsp;<input type="radio" name="operationallicense" <%=globaldisabled%> id="operationallicenseN" value="N" <%="N".equals(extraDetailsProfileVO.getOperationalLicense()) || !functions.isValueNull(extraDetailsProfileVO.getOperationalLicense()) ?"checked":""%>/>         <%--Condition for Radio--%>
                  <%=extradetailsprofile_No%>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_16%>&nbsp;<%=extradetailsprofile_blacklisted%></label>
                  &nbsp;&nbsp;<input type="radio" name="blacklistedaccountclosed" <%=globaldisabled%>  value="Y" <%="Y".equals(extraDetailsProfileVO.getBlacklistedAccountClosed())?"checked":""%>/><%--<%if(validationErrorList.getError("blacklistedaccountclosed")!=null){%><span style="width:13%" class="apperrormsg">Invalid Blacklisted Account Closed</span><%}%>--%>
                  <%=extradetailsprofile_Yes%>&nbsp;
                  &nbsp;&nbsp;<input type="radio" name="blacklistedaccountclosed" <%=globaldisabled%>   value="N" <%="N".equals(extraDetailsProfileVO.getBlacklistedAccountClosed()) || !functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosed()) ?"checked":""%> />           <%--Condition for Radio--%>
                  &nbsp;<%=extradetailsprofile_No%> &nbsp;&nbsp;&nbsp;&nbsp;<%=extradetailsprofile_describe%>
                  </p><p><input type="text" class="form-control has-feedback" style="width:47%;" <%=globaldisabled%> name="blacklistedaccountclosedyes" value="<%=functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosedYes())==true?extraDetailsProfileVO.getBlacklistedAccountClosedYes():""%>" name="blacklistedaccountclosedyes" <%=!functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosed())||"N".equals(extraDetailsProfileVO.getBlacklistedAccountClosed())?"disabled":""%> /><%if(validationErrorList.getError("blacklistedaccountclosedyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                  <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=extradetailsprofile_17%>&nbsp;<%=extradetailsprofile_whether%></label>
                  &nbsp;&nbsp;<input type="radio" name="supervisorregularcontrole" id="supervisorregularcontroleY" <%=globaldisabled%> value="Y" <%="Y".equals(extraDetailsProfileVO.getSupervisorregularcontrole())?"checked":""%>/><%if(validationErrorList.getError("supervisorregularcontrole")!=null){%><span style="width:13%" class="apperrormsg">Invalid Supervisor Regular Controle</span><%}%>
                  <%=extradetailsprofile_Yes%>
                  &nbsp;&nbsp;<input type="radio" name="supervisorregularcontrole" id="supervisorregularcontroleN" <%=globaldisabled%> value="N" <%="N".equals(extraDetailsProfileVO.getSupervisorregularcontrole()) || !functions.isValueNull(extraDetailsProfileVO.getSupervisorregularcontrole()) ?"checked":""%>/>         <%--Condition for Radio--%>
                  <%=extradetailsprofile_No%>
                </div>

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>