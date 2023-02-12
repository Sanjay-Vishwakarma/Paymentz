<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.vo.applicationManagerVOs.ExtraDetailsProfileVO" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/15
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

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
        $( "input[name='company_financialreport']").on( "click", function() {
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

        $( "input[name='financialreport_available']").on( "click", function() {
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

        $( "input[name='compliance_punitivesanction']").on( "click", function() {
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

        $( "input[name='deedofagreement']").on( "click", function() {
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

        $( "input[name='fulfillment_productemail']").on( "click", function() {
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

        $( "input[name='blacklistedaccountclosed']").on( "click", function() {
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

        $( "input[name='compliance_cispcompliant']").on( "click", function() {
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

        $( "input[name='compliance_pcidsscompliant']").on( "click", function() {
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

        $( "input[name='seasonal_fluctuating']").on( "click", function() {
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

        $( "input[name='is_website_live']").on( "click", function() {
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

        $( "input[name='agency_employed']").on( "click", function()
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

    //System.out.println("globaldisabled--->"+globaldisabled);
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
<div class="container-fluid ">
    <div class="row" style="margin-top: 100px;margin-left: 226px;margin-bottom: 12px;background-color: #ffffff">
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">

            <div class="form foreground bodypanelfont_color panelbody_color">

                <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%>"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Extra Details Profile after entering the data provided"%></h2></center>
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">  Extra Details Profile</h2>


                <div class="form-group col-md-12  has-feedback">
                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Additional Company Details-</u></label>
                </div>


                <div class="form-group col-md-6  has-feedback">
                    <label for="ownersince" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">1.&nbsp;Owner Since?</label>
                    <input type="text"  class="form-control datepicker"   id="ownersince" <%=globaldisabled%> name="ownersince" style="border: 1px solid #b2b2b2;font-weight:bold;width: 100%" value="<%=functions.isValueNull(extraDetailsProfileVO.getOwnerSince())==true?commonFunctionUtil.convertTimestampToDatepicker(extraDetailsProfileVO.getOwnerSince()):""%>"/><%if(validationErrorList.getError("ownersince")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-6  has-feedback">
                    <label for="socialsecurity" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">2.&nbsp;Socialsecurity?</label>
                    <input type="text" class="form-control"   id="socialsecurity" <%=globaldisabled%> name="socialsecurity" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(extraDetailsProfileVO.getSocialSecurity())==true?extraDetailsProfileVO.getSocialSecurity():""%>"/><%if(validationErrorList.getError("socialsecurity")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-6  has-feedback">
                    <label for="company_formparticipation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">3.&nbsp;Form of participation in company?</label>
                    <input type="text" class="form-control"   id="company_formparticipation" <%=globaldisabled%> name="company_formparticipation" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(extraDetailsProfileVO.getCompany_formParticipation())==true?extraDetailsProfileVO.getCompany_formParticipation():""%>"/><%if(validationErrorList.getError("company_formparticipation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>
                <div class="form-group col-md-6  has-feedback">
                    <label for="financialobligation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">4.&nbsp;Financial Obligation?</label>
                    <input type="text" class="form-control"   id="financialobligation" <%=globaldisabled%> name="financialobligation" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(extraDetailsProfileVO.getFinancialObligation())==true?extraDetailsProfileVO.getFinancialObligation():""%>"/><%if(validationErrorList.getError("financialobligation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-6  has-feedback">
                    <label for="workingexperience" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">5.&nbsp;Working experience in this sphere?</label>
                    <input type="text" class="form-control"   id="workingexperience" <%=globaldisabled%> name="workingexperience" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(extraDetailsProfileVO.getWorkingExperience())==true?extraDetailsProfileVO.getWorkingExperience():""%>"/><%if(validationErrorList.getError("workingexperience")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>


                <div class="form-group col-md-12">
                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Financial Reporting-</u></label>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">6.Does company should submit financial report in its registration on business activities ?</label>
                    &nbsp;&nbsp;<input type="radio" name="company_financialreport" <%=globaldisabled%> value="Y" <%="Y".equals(extraDetailsProfileVO.getCompany_financialReport())?"checked":""%>/><%if(validationErrorList.getError("company_financialreport")!=null){%><span style="width:13%" class="apperrormsg">Invalid countries_blocked</span><%}%>
                    Yes&nbsp;
                    &nbsp;&nbsp;<input type="radio" name="company_financialreport" <%=globaldisabled%> value="N" <%="N".equals(extraDetailsProfileVO.getCompany_financialReport()) || !functions.isValueNull(extraDetailsProfileVO.getCompany_financialReport()) ?"checked":""%> />           <%--Condition for Radio--%>
                    &nbsp;No &nbsp;&nbsp;&nbsp;&nbsp;If yes, Please provide details?
                    </p><p><input type="text" class="form-control" style="width: 49%" <%=globaldisabled%> name="company_financialreportyes"  value="<%=functions.isValueNull(extraDetailsProfileVO.getCompany_financialReportYes())==true?extraDetailsProfileVO.getCompany_financialReportYes():""%>"  <%=!functions.isValueNull(extraDetailsProfileVO.getCompany_financialReport()) ||"N".equals(extraDetailsProfileVO.getCompany_financialReport())?"disabled" : ""%>/><%if(validationErrorList.getError("company_financialreportyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12  has-feedback">
                    <label for="financialreport_institution" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">7.&nbsp; Specify institution for submitting financial reports?</label>
                    <input type="text" class="form-control"   id="financialreport_institution" <%=globaldisabled%> name="financialreport_institution" style="border: 1px solid #b2b2b2;font-weight:bold;width: 49%"  value="<%=functions.isValueNull(extraDetailsProfileVO.getFinancialReport_institution())==true?extraDetailsProfileVO.getFinancialReport_institution():""%>"/><%if(validationErrorList.getError("financialreport_institution")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">8.&nbsp; Financial report are publically available?</label>
                    &nbsp;&nbsp;<input type="radio" name="financialreport_available" <%=globaldisabled%> value="Y" <%="Y".equals(extraDetailsProfileVO.getFinancialReport_available())?"checked":""%>/><%if(validationErrorList.getError("financialreport_available")!=null){%><span style="width:13%" class="apperrormsg">Invalid financialreport_available</span><%}%>
                    Yes&nbsp;
                    &nbsp;&nbsp;<input type="radio" name="financialreport_available" <%=globaldisabled%> value="N" <%="N".equals(extraDetailsProfileVO.getFinancialReport_available()) || !functions.isValueNull(extraDetailsProfileVO.getFinancialReport_available()) ?"checked":""%> />           <%--Condition for Radio--%>
                    &nbsp;No &nbsp;&nbsp;&nbsp;&nbsp;If yes, Please provide details?
                    </p><p><input type="text" class="form-control" style="width: 49%" <%=globaldisabled%> name="financialreport_availableyes"  value="<%=functions.isValueNull(extraDetailsProfileVO.getFinancialReport_availableYes())==true?extraDetailsProfileVO.getFinancialReport_availableYes():""%>" name="financialreport_availableyes" <%=!functions.isValueNull(extraDetailsProfileVO.getFinancialReport_available()) ||"N".equals(extraDetailsProfileVO.getFinancialReport_available())?"disabled" : ""%>/><%if(validationErrorList.getError("financialreport_availablyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>


                <div class="form-group col-md-12">
                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Legal Matters-</u></label>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">9.&nbsp;Is the company has been under punitive sanction/restriction for non compliance of requirement that regulate mention activity of company?</label>
                    &nbsp;&nbsp;<input type="radio" name="compliance_punitivesanction" <%=globaldisabled%> id="compliance_punitivesanctionY" value="Y" <%="Y".equals(extraDetailsProfileVO.getCompliance_punitiveSanction())?"checked":""%>/><%if(validationErrorList.getError("compliance_punitivesanction")!=null){%><span style="width:13%" class="apperrormsg">Invalid Comliance Punitive Sanction</span><%}%>
                    Yes
                    &nbsp;&nbsp;<input type="radio" name="compliance_punitivesanction" <%=globaldisabled%> id="compliance_punitivesanctionN" value="N" <%="N".equals(extraDetailsProfileVO.getCompliance_punitiveSanction()) || !functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanction()) ?"checked":""%>/>         <%--Condition for Radio--%>
                    &nbsp;No &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If yes, describe?
                    </p><p><input type="text" class="form-control" style="width: 49%" <%=globaldisabled%> name="compliance_punitivesanctionyes"  value="<%=functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanctionYes())==true?extraDetailsProfileVO.getCompliance_punitiveSanctionYes():""%>"  <%=!functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanction()) ||"N".equals(extraDetailsProfileVO.getCompliance_punitiveSanction())?"disabled" : ""%>/><%if(validationErrorList.getError("compliance_punitivesanctionyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">10.&nbsp;Have you ever been adjudiscated bankrupt or party to a deed of arrangement?</label>
                    &nbsp;&nbsp;<input type="radio" name="deedofagreement" id="deedofagreementY" <%=globaldisabled%> value="Y" <%="Y".equals(extraDetailsProfileVO.getDeedOfAgreement())?"checked":""%>/><%if(validationErrorList.getError("deedofagreement")!=null){%><span style="width:13%" class="apperrormsg">Invalid Deed Of Agreement</span><%}%>
                    Yes
                    &nbsp;&nbsp;<input type="radio" name="deedofagreement" id="deedofagreementN" <%=globaldisabled%> value="N" <%="N".equals(extraDetailsProfileVO.getDeedOfAgreement()) || !functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreement()) ?"checked":""%>/>         <%--Condition for Radio--%>
                    &nbsp;No&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If yes, describe describe?
                    </p><p><input type="text" class="form-control" style="width: 49%" <%=globaldisabled%> name="deedofagreementyes"   value="<%=functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreementYes())==true?extraDetailsProfileVO.getDeedOfAgreementYes():""%>" name="deedofagreementyes" <%=!functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreement()) ||"N".equals(extraDetailsProfileVO.getDeedOfAgreement())?"disabled" : ""%>/><%if(validationErrorList.getError("deedofagreementyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>


                <div class="form-group col-md-12">
                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Additional Shipping Information-</u></label>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">11.&nbsp; Do you offer insurance for goods being sent?</label>
                    &nbsp;&nbsp;<input type="radio" name="goodsinsuranceoffered" <%=globaldisabled%> id="goodsinsuranceofferedY" value="Y" <%="Y".equals(extraDetailsProfileVO.getGoodsInsuranceOffered())?"checked":""%>/><%if(validationErrorList.getError("goodsinsuranceoffered")!=null){%><span style="width:13%" class="apperrormsg">Invalid Goods Insurance Offered</span><%}%>
                    Yes
                    &nbsp;&nbsp;<input type="radio" name="goodsinsuranceoffered" <%=globaldisabled%> id="goodsinsuranceofferedN" value="N" <%="N".equals(extraDetailsProfileVO.getGoodsInsuranceOffered()) || !functions.isValueNull(extraDetailsProfileVO.getGoodsInsuranceOffered()) ?"checked":""%>/>         <%--Condition for Radio--%>
                    No
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">12.&nbsp;Do you send an email recipt to card holder? When the product has been fulfilled?</label>
                    &nbsp;&nbsp;<input type="radio" name="fulfillment_productemail" <%=globaldisabled%> id="fulfillment_productemailY" value="Y" <%="Y".equals(extraDetailsProfileVO.getFulfillment_productEmail())?"checked":""%>/><%if(validationErrorList.getError("fulfillment_productemail")!=null){%><span style="width:13%" class="apperrormsg">Invalid Fulfillment Product Email</span><%}%>
                    Yes
                    &nbsp;&nbsp;<input type="radio" name="fulfillment_productemail" <%=globaldisabled%> id="fulfillment_productemailN" value="N" <%="N".equals(extraDetailsProfileVO.getFulfillment_productEmail()) || !functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmail()) ?"checked":""%>/>         <%--Condition for Radio--%>
                    No	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If yes, describe?
                    </p><p><input type="text" class="form-control" style="width: 49%" <%=globaldisabled%> name="fulfillment_productemailyes"   value="<%=functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmailYes())==true?extraDetailsProfileVO.getFulfillment_productEmailYes():""%>" name="fulfillment_productemailyes" <%=!functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmail()) || "N".equals(extraDetailsProfileVO.getFulfillment_productEmail())? "disabled" : ""%>/><%if(validationErrorList.getError("fulfillment_productemailyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12  has-feedback">
                    <label for="shiping_deliverymethod" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">13.&nbsp;Shipping and delivery methods?</label>
                    <input type="text" class="form-control"   id="shiping_deliverymethod" <%=globaldisabled%> name="shiping_deliverymethod" style="border: 1px solid #b2b2b2;font-weight:bold;width: 49%"  value="<%=functions.isValueNull(extraDetailsProfileVO.getShiping_deliveryMethod())==true?extraDetailsProfileVO.getShiping_deliveryMethod():""%>"/><%if(validationErrorList.getError("shiping_deliverymethod")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12">
                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Account/Transaction Monitoring-</u></label>
                </div>

                <div class="form-group col-md-12  has-feedback">
                    <label for="transactionmonitoringprocess" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">14.&nbsp;Information about transaction monitoring process?</label>
                    <input type="text" class="form-control"   id="transactionmonitoringprocess" <%=globaldisabled%> name="transactionmonitoringprocess" style="border: 1px solid #b2b2b2;font-weight:bold;width: 49%"  value="<%=functions.isValueNull(extraDetailsProfileVO.getTransactionMonitoringProcess())==true?extraDetailsProfileVO.getTransactionMonitoringProcess():""%>"/><%if(validationErrorList.getError("transactionmonitoringprocess")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">15.&nbsp; Verification of bank account owner?If applicable,a certified copy of an operational license?</label>
                    &nbsp;&nbsp;<input type="radio" name="operationallicense" id="operationallicenseY" <%=globaldisabled%> value="Y" <%="Y".equals(extraDetailsProfileVO.getOperationalLicense())?"checked":""%>/><%if(validationErrorList.getError("operationallicense")!=null){%><span style="width:13%" class="apperrormsg">Invalid Operational License</span><%}%>
                    Yes
                    &nbsp;&nbsp;<input type="radio" name="operationallicense" id="operationallicenseN" <%=globaldisabled%> value="N" <%="N".equals(extraDetailsProfileVO.getOperationalLicense()) || !functions.isValueNull(extraDetailsProfileVO.getOperationalLicense()) ?"checked":""%>/>         <%--Condition for Radio--%>
                    No
                </div>


                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">16.&nbsp;Have you ever been blacklisted or had an account closed by master card/visa or ACH proceesor ?</label>
                    &nbsp;&nbsp;<input type="radio" name="blacklistedaccountclosed" <%=globaldisabled%> id="blacklistedaccountclosedY" value="Y" <%="Y".equals(extraDetailsProfileVO.getBlacklistedAccountClosed())?"checked":""%>/><%if(validationErrorList.getError("blacklistedaccountclosed")!=null){%><span style="width:13%" class="apperrormsg">Invalid Blacklisted Account Closed</span><%}%>
                    Yes
                    &nbsp;&nbsp;<input type="radio" name="blacklistedaccountclosed" <%=globaldisabled%> id="blacklistedaccountclosedN" value="N" <%="N".equals(extraDetailsProfileVO.getBlacklistedAccountClosed()) || !functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosed()) ?"checked":""%>/>         <%--Condition for Radio--%>
                    No	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If yes, describe
                    </p><p><input type="text" class="form-control" style="width: 49%" <%=globaldisabled%> name="blacklistedaccountclosedyes" value="<%=functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosedYes())==true?extraDetailsProfileVO.getBlacklistedAccountClosedYes():""%>" name="blacklistedaccountclosedyes" <%=!functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosed()) ||"N".equals(extraDetailsProfileVO.getBlacklistedAccountClosed())? "disabled" : ""%>/><%if(validationErrorList.getError("blacklistedaccountclosedyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                </div>

                <div class="form-group col-md-12  has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">17.&nbsp;Whether the company is a subject of its supervisor regular control?</label>
                    &nbsp;&nbsp;<input type="radio" name="supervisorregularcontrole" <%=globaldisabled%> id="supervisorregularcontroleY" value="Y" <%="Y".equals(extraDetailsProfileVO.getSupervisorregularcontrole())?"checked":""%>/><%if(validationErrorList.getError("supervisorregularcontrole")!=null){%><span style="width:13%" class="apperrormsg">Invalid Supervisor Regular Controle</span><%}%>
                    Yes
                    &nbsp;&nbsp;<input type="radio" name="supervisorregularcontrole" <%=globaldisabled%> id="supervisorregularcontroleN" value="N" <%="N".equals(extraDetailsProfileVO.getSupervisorregularcontrole()) || !functions.isValueNull(extraDetailsProfileVO.getSupervisorregularcontrole()) ?"checked":""%>/>         <%--Condition for Radio--%>
                    No
                </div>

            </div>

        </div>
    </div>

</div>