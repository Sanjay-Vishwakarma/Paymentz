<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.CardholderProfileVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/13/15
  Time: 5:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<%--<link href="/merchant/NewCss/libs/fontello/css/fontello.css" rel="stylesheet" />--%>

<script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});

        /*************************************Cardholder Profile JS****************************************/


        $( "input[name='compliance_swapp']").on( "click", function() {
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


        $( "input[name='compliance_companiesorgateways']").on( "click", function() {
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


        $( "input[name='compliance_electronically']").on( "click", function() {
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


        $( "input[name='compliance_cispcompliant']").on( "click", function() {
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

        if($('input:radio[name=compliance_pcidsscompliant]:checked').val()=="N")
        {
            document.myformname.compliance_pcidsscompliant_yes.disabled = true;
            document.myformname.compliance_pcidsscompliant_yes.value = "";
        }
        else
        {
            document.myformname.compliance_pcidsscompliant_yes.disabled = false;
        }


        $( "input[name='compliance_pcidsscompliant']").on( "click", function() {
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


        $( "input[name='compliance_datacompromise']").on( "click", function() {
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


        $( "input[name='siteinspection_merchant']").on( "click", function() {
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
            <div class="row" style="background-color: #ffffff">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <%--<h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Cardholder Profile</strong></h2>--%>
                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7;text-align: center"> Cardholder Profile</h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <div class="form foreground bodypanelfont_color panelbody_color">
                                    <center><h4 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Cardholder Profile after entering the data provided"%></h4></center>
                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"> CARDHOLDER DATA STORAGE COMPLIANCE</h2>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.compliance_swapp)||cardholderProfileInputList.contains(BankInputName.compliance_thirdpartyappform)||cardholderProfileInputList.contains(BankInputName.compliance_thirdpartysoft)||cardholderProfileInputList.contains(BankInputName.compliance_version)||cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways)||cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways_yes)|| view)
                                        {
                                    %>
                                    <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">&nbsp; Are you using software or gateway application?*</label>
                                        &nbsp;&nbsp;<input type="radio"  name="compliance_swapp"  <%=globaldisabled%>  value="Y" <%=checkedY%>/><%if(validationErrorList.getError("compliance_swapp")!=null){%><span style="width:13%" class="apperrormsg">Invalid software or gateway application</span><%}%> Yes
                                        &nbsp;&nbsp;<input type="radio" name="compliance_swapp"  <%=globaldisabled%>  value="N" <%=checkedN%> />  No
                                    </div>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="compliance_thirdpartyappform" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">&nbsp; What third party software company/vendor did you purchase your Application from?*</label>
                                        <input type="text" class="form-control" id="compliance_thirdpartyappform" name="compliance_thirdpartyappform"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartyappform())==true?cardholderProfileVO.getCompliance_thirdpartyappform():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_thirdpartyappform")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                                    </div>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="compliance_thirdpartysoft" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">What is the name of the third party software?*  <font style="color:transparent; user-select: none;">purchase your Application from</font></label>
                                        <input type="text" class="form-control"  id="compliance_thirdpartysoft" name="compliance_thirdpartysoft"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartysoft())==true?cardholderProfileVO.getCompliance_thirdpartysoft():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_thirdpartysoft")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                                    </div>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="compliance_thirdpartysoft" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Version #?</label>
                                        <input type="text" class="form-control" id="compliance_version" name="compliance_version"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>   value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_version())==true?cardholderProfileVO.getCompliance_version():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_version")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                                    </div>


                                    <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <div class="col-md-6" style="padding-left: 0;">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Do you use any third parties,web hosting companies or gateways as service provider and is their name and email address provided on your website?</label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio" name="compliance_companiesorgateways" id="compliance_companiesorgatewaysY" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_companiesorgateways())?"checked":""%> <%=disabled%>/><%if(validationErrorList.getError("compliance_companiesorgateways")!=null){%><span style="width:13%" class="apperrormsg">Invalid companiesorgateways</span><%}%>
                                            Yes&nbsp;&nbsp;
                                            <input type="radio" name="compliance_companiesorgateways" id="compliance_companiesorgatewaysN" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getCompliance_companiesorgateways()) || !functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways()) ?"checked":""%> <%=disabled%>/>         <%--Condition for Radio--%>
                                            No<%--&nbsp;&nbsp;&nbsp;&nbsp;If yes, who is it?--%>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-6 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="text" class="form-control" name="compliance_companiesorgateways_yes"<%-- style="margin-left: 5%;width: 96%"--%> <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways_yes())==true?cardholderProfileVO.getCompliance_companiesorgateways_yes():""%>" name="compliance_companiesorgateways_yes" <%=!functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways())||"N".equals(cardholderProfileVO.getCompliance_companiesorgateways())?"disabled":""%> <%=disabled%>/><%if(validationErrorList.getError("compliance_companiesorgateways_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Do you or your vendor receive, pass, transmit or store the full cardholder number, electronically?</label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio"  name="compliance_electronically" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_electronically())?"checked":""%> /><%if(validationErrorList.getError("compliance_electronically")!=null){%><span style="width:13%" class="apperrormsg">Invalid electronically</span><%}%>
                                            Yes&nbsp;&nbsp;
                                            <input type="radio" name="compliance_electronically"<%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_electronically()) || !functions.isValueNull(cardholderProfileVO.getCompliance_electronically()) ?"checked":""%> />            <%--Condition for Radio--%>
                                            No
                                        </div>
                                    </div>


                                    <div class="form-group col-md-10" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">If yes, where is card data stored?</label>

                                        <div class="col-md-10">
                                            <input type="radio" id="cd1" name="compliance_carddatastored" <%=globaldisabled%>  value="Merchant"   <%=electroDisabled%><%%> <%="Merchant".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                                            Merchant
                                            &nbsp;&nbsp; <input type="radio"  id="cd2" name="compliance_carddatastored" <%=globaldisabled%>  value="ThirdParty" <%=electroDisabled%> <%="ThirdParty".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                                            Third Party Only
                                            &nbsp;&nbsp; <input type="radio"  id="cd3" name="compliance_carddatastored" <%=globaldisabled%>  value="Both" <%=electroDisabled%> <%=!functions.isValueNull(cardholderProfileVO.getCompliance_carddatastored())||"Both".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                                            Both Merchant & Third Party
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Company is CISP (Cardholder Information Security Program) compliant?</label>
                                        </div>
                                        <div class="col-md-6">
                                            &nbsp;&nbsp;<input type="radio"  name="compliance_cispcompliant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_cispcompliant())?"checked":""%> />
                                            Yes&nbsp;&nbsp;
                                            <input type="radio" name="compliance_cispcompliant" <%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_cispcompliant()) || !functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant()) ?"checked":""%> />
                                            No
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <div class="col-md-6" style="padding-left: 0;">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">If yes please provide more details</label>
                                        </div>

                                        <div class="col-md-6 has-feedback">
                                            <input type="text" class="form-control"  id="compliance_cispcompliant_yes" name="compliance_cispcompliant_yes"style="border: 1px solid #b2b2b2;font-weight:bold;margin-left:0%;" <%=cisCompliantDisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant_yes())==true?cardholderProfileVO.getCompliance_cispcompliant_yes():""%>" />
                                            <%if(validationErrorList.getError("compliance_cispcompliant_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Are you or your vendor PCI/DSS (Payment Card Industry/Data Security Standard) compliant?</label>
                                        </div>
                                        <div class="col-md-6">
                                            &nbsp;&nbsp;<input type="radio"  name="compliance_pcidsscompliant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_pcidsscompliant())?"checked":""%> />
                                            Yes&nbsp;&nbsp;
                                            <input type="radio" name="compliance_pcidsscompliant" <%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_pcidsscompliant()) || !functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant()) ?"checked":""%> />
                                            No&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <div class="col-md-6" style="padding-left: 0;">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">If yes please provide more details</label>
                                        </div>

                                        <div class="col-md-6 has-feedback">
                                            <input type="text" class="form-control"  id="compliance_pcidsscompliant_yes" name="compliance_pcidsscompliant_yes"style="border: 1px solid #b2b2b2;font-weight:bold;margin-left:0%;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant_yes())==true?cardholderProfileVO.getCompliance_pcidsscompliant_yes():""%>"<%=!functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant_yes())||"N".equals(cardholderProfileVO.getCompliance_pcidsscompliant_yes())?"disabled":""%> />
                                            <%if(validationErrorList.getError("compliance_pcidsscompliant_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
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
                                        <label for="compliance_qualifiedsecurityassessor" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">What is the name of your Qualified Security Assessor?</label>
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
                                        <label for="compliance_dateofcompliance" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Date of compliance</label>
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
                                        <label for="compliance_dateoflastscan" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Last scan</label>
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Have you ever experienced an account data compromise?*</label>
                                        </div>
                                        <div class="col-md-6">
                                            &nbsp;&nbsp;<input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" name="compliance_datacompromise" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_datacompromise())?"checked":""%>/>
                                            Yes&nbsp;&nbsp;
                                            <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" name="compliance_datacompromise" <%=globaldisabled%>  value="N"  <%=!functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise())||"N".equals(cardholderProfileVO.getCompliance_datacompromise())?"checked":""%>/> <%--this is radio button  we can use condition--%>
                                            No
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <div class="col-md-6" style="padding-left: 0;">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">If yes, when</label>
                                        </div>

                                        <div class="col-md-6">
                                            <input type="text" name="compliance_datacompromise_yes" class="form-control datepicker" style="width: 100%!important;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise_yes())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getCompliance_datacompromise_yes()):""%>"<%=!functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise())||"N".equals(cardholderProfileVO.getCompliance_datacompromise())?"disabled":""%>>
                                            <%if(validationErrorList.getError("compliance_datacompromise_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="border-top-right-radius: 5px!important; border-bottom-right-radius: 5px!important; margin-right: 0!important;"></i><%}%>
                                        </div>

                                    </div>


                                    <label class="newborder" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">    ***** Card Association requirements dictate it is prohibited to store track data in any circumstance. Further, it is recommended that no merchant or a merchant's third
                                        party vendor store cardholder data. If you or your vendor store data, you or your vendor are required to be PCI DSS compliant. Failure to adhere to these requirements
                                        may result in fines or loss of card acceptance. *****</label>
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
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Merchant:</label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_merchant" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getSiteinspection_merchant()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_merchant()) ?"checked":""%>/>&nbsp;Owns
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_merchant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getSiteinspection_merchant())?"checked":""%>/>&nbsp;Rents
                                    </div>

                                    <div class="form-group col-md-8" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <label class="col-sm-2 control-label" style="font-weight: inherit;font-size: inherit;">Landlord:</label>
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
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Building Type:</label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="ShoppingCtr" <%="ShoppingCtr".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%>/>&nbsp;
                                        Shopping Ctr
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="OfficeBldg" <%="OfficeBldg".equals(cardholderProfileVO.getSiteinspection_buildingtype()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_buildingtype()) ?"checked":""%> />&nbsp;
                                        Office Bldg
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="IndustrialBldg" <%="IndustrialBldg".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%> />&nbsp;
                                        Industrial Bldg
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="Residence" <%="Residence".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%>/>&nbsp;
                                        Residence
                                    </div>

                                    <%
                                        }
                                    %>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.siteinspection_areazoned)||view)
                                        {

                                    %>
                                    <div class="form-group col-md-12">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Area Zoned:</label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Commercial" <%="Commercial".equals(cardholderProfileVO.getSiteinspection_areazoned()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()) ?"checked":""%> />&nbsp;
                                        Commercial
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Industrial" <%="Industrial".equals(cardholderProfileVO.getSiteinspection_areazoned())?"checked":""%>/>&nbsp;
                                        Industrial
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Residential" <%="Residential".equals(cardholderProfileVO.getSiteinspection_areazoned())?"checked":""%>/>&nbsp;
                                        Residential
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.siteinspection_squarefootage)||view)
                                        {

                                    %>
                                    <div class="form-group col-md-12">
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Square Footage/m2:</label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="0-500" <%="0-500".equals(cardholderProfileVO.getSiteinspection_squarefootage()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()) ?"checked":""%> />&nbsp;
                                        0-500
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="501-2500" <%="501-2500".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%> />&nbsp;
                                        501-2500
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="2501-5000" <%="2501-5000".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%>/>&nbsp;
                                        2501-5000
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="5001-10000+" <%="5001-10000+".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%> />&nbsp;
                                        5001-10000+
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Does Merchant have the appropriate facilities, equipment, inventory, personnel and license/permit to operate their business?</label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio" name="siteinspection_operatebusiness" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getSiteinspection_operatebusiness())?"checked":""%> />
                                            Yes&nbsp;&nbsp;
                                            <input type="radio" name="siteinspection_operatebusiness" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getSiteinspection_operatebusiness()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_operatebusiness()) ?"checked":""%> />
                                            No
                                        </div>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <label class="newborder bg-info"  style="font-family:Open Sans;font-size: 13px;font-weight: 600;text-align: justify;padding: 15px;">     <b>Declarations:</b><br />
                                        I hereby confirm to be the owner of the listed website(s). I further declare to have full control and authorization of the website content. I acknowledge and agree that I will not use the Processing System for transactions relating to; 1) Sales made under a
                                        different trade name or business affiliation than indicated on this Agreement or otherwise approved by the acquirer in writing; 2) Fines or Penalties of any kind, losses, damages or any other costs that are beyond the Total Sale Price; 3) Any transaction that
                                        violates any law, ordinance, or regulation applicable to my business; 4) Goods which I / we know will be resold by a customer whom I / we reasonably should know is not ordinarily in the business of selling such goods; 5) Sales by third parties; 6) Any other
                                        amounts for which a customer has not specifically authorized payment through the acquirer; 7) Cash, traveler's checks, Cash equivalents, or other negotiable instruments; or 8) Amounts which do not represent a bona fide sale of goods or services by me /
                                        us. I also declare on behalf of the company and on behalf of myself that, to the best of our knowledge, neither the company nor the website nor myself (or any of us) have ever been involved in excessive chargeback’s, fraud or content violation nor have any
                                        of the above ever terminated by an acquirer or asked by an acquirer to terminate an agreement within a set period of time.<br /><br/>
                                        <b>Investigate Consumer Report</b><br />
                                        An investigative or consumer report may be made in connection with application. Merchant authorizes any party to the agreement or any of their agents to investigate the reference provided or any other statements or data obtained from merchant and
                                        from any of the undersigned personal guarantor(s), or from any person or entity with any financial obligations under this agreement. You have a right, upon written request, to a complete and accurate disclosure of the nature of and scope of the
                                        investigation requested.</label>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.siteinspection_principal1)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-3">
                                        <label for="siteinspection_principal1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Principal 1</label>
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
                                        <label for="siteinspection_principal1_date" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Date</label>
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
                                        <label for="siteinspection_principal2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Principal 2</label>
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
                                        <label for="siteinspection_principal2_date" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Date</label>
                                        <input type="text" class="form-control datepicker"  id="siteinspection_principal2_date" name="siteinspection_principal2_date" style="border: 1px solid #b2b2b2;font-weight:bold;width: 100%;" <%=globaldisabled%>   value="<%=functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2_date())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getSiteinspection_principal2_date()):""%>" /><%if(validationErrorList.getError("siteinspection_principal2_date")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    </div>

                                </div>

                                <%
                                        }
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
                                                "                                <h2 class=\"col-md-12 background panelheading_color headpanelfont_color\" style=\"background-color: #9fabb7;text-align: center\"> Cardholder Profile</h2>\n" +
                                                "                                <div class=\"additional-btn\">\n" +
                                                "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                                                "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                                                "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                                                "                                </div>\n" +
                                                "                            </div>");
                                        out.println("<div class=\"widget-content padding\">");
          /*out.println("<div class=\"table-responsive\">");*/
                                        out.println(Functions.NewShowConfirmation1("Profile","No details need to be provided"));         /* out.println("</div>");*/
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
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%--</div>--%>



