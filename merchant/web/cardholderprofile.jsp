<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.CardholderProfileVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
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
<script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

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

        if($('input:radio[name=compliance_pcidsscompliant]:checked').val()=="N")
        {
            document.myformname.compliance_pcidsscompliant_yes.disabled = true;
            document.myformname.compliance_pcidsscompliant_yes.value = "";
        }
        else
        {
            document.myformname.compliance_pcidsscompliant_yes.disabled = false;
        }


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
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String cardholderprofile_Cardholder_Profile = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Cardholder_Profile"))?rb1.getString("cardholderprofile_Cardholder_Profile"): "Cardholder Profile";
    String cardholderprofile_please = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_please"))?rb1.getString("cardholderprofile_please"): "Please save Cardholder Profile after entering the data provided";
    String cardholderprofile_storage = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_storage"))?rb1.getString("cardholderprofile_storage"): "CARDHOLDER DATA STORAGE COMPLIANCE";
    String cardholderprofile_gateway = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_gateway"))?rb1.getString("cardholderprofile_gateway"): "Are you using software or gateway application?*";
    String cardholderprofile_yes = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_yes"))?rb1.getString("cardholderprofile_yes"): "Yes";
    String cardholderprofile_no = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_no"))?rb1.getString("cardholderprofile_no"): "No";
    String cardholderprofile_third = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_third"))?rb1.getString("cardholderprofile_third"): "What third party software company/vendor did you purchase your Application from?*";
    String cardholderprofile_name = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_name"))?rb1.getString("cardholderprofile_name"): "What is the name of the third party software?**";
    String cardholderprofile_version = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_version"))?rb1.getString("cardholderprofile_version"): "Version #?";
    String cardholderprofile_webhosting = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_webhosting"))?rb1.getString("cardholderprofile_webhosting"): "Do you use any third parties,web hosting companies or gateways as service provider and is their name and email address provided on your website?";
    String cardholderprofile_receive = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_receive"))?rb1.getString("cardholderprofile_receive"): "Do you or your vendor receive, pass, transmit or store the full cardholder number, electronically?";
    String cardholderprofile_carddata = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_carddata"))?rb1.getString("cardholderprofile_carddata"): "If yes, where is card data stored?";
    String cardholderprofile_Merchant = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Merchant"))?rb1.getString("cardholderprofile_Merchant"): "Merchant";
    String cardholderprofile_thirdparty = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_thirdparty"))?rb1.getString("cardholderprofile_thirdparty"): "Third Party Only";
    String cardholderprofile_both = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_both"))?rb1.getString("cardholderprofile_both"): "Both Merchant & Third Party";
    String cardholderprofile_vendor = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_vendor"))?rb1.getString("cardholderprofile_vendor"): "Are you or your vendor PCI/DSS (Payment Card Industry/Data Security Standard) compliant?";
    String cardholderprofile_details = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_details"))?rb1.getString("cardholderprofile_details"): "If yes please provide more details";
    String cardholderprofile_assessor = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_assessor"))?rb1.getString("cardholderprofile_assessor"): "What is the name of your Qualified Security Assessor?";
    String cardholderprofile_date = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_date"))?rb1.getString("cardholderprofile_date"): "Date of compliance";
    String cardholderprofile_last = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_last"))?rb1.getString("cardholderprofile_last"): "Date of Last scan";
    String cardholderprofile_experience = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_experience"))?rb1.getString("cardholderprofile_experience"): "Have you ever experienced an account data compromise?*";
    String cardholderprofile_when = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_when"))?rb1.getString("cardholderprofile_when"): "If yes, when";
    String cardholderprofile_association = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_association"))?rb1.getString("cardholderprofile_association"): "***** Card Association requirements dictate it is prohibited to store track data in any circumstance. Further, it is recommended that no merchant or a merchant's third party vendor store cardholder data. If you or your vendor store data, you or your vendor are required to be PCI DSS compliant. Failure to adhere to these requirements may result in fines or loss of card acceptance. *****";
    String cardholderprofile_site = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_site"))?rb1.getString("cardholderprofile_site"): "SITE INSPECTION";
    String cardholderprofile_Merchant1 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Merchant1"))?rb1.getString("cardholderprofile_Merchant1"): "Merchant:";
    String cardholderprofile_owns = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_owns"))?rb1.getString("cardholderprofile_owns"): "Owns";
    String cardholderprofile_rents = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_rents"))?rb1.getString("cardholderprofile_rents"): "Rents";
    String cardholderprofile_Landlord = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_Landlord"))?rb1.getString("cardholderprofile_Landlord"): "Landlord:";
    String cardholderprofile_building = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_building"))?rb1.getString("cardholderprofile_building"): "Building Type:";
    String cardholderprofile_shop = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_shop"))?rb1.getString("cardholderprofile_shop"): "Shopping Ctr";
    String cardholderprofile_office = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_office"))?rb1.getString("cardholderprofile_office"): "Office Bldg";
    String cardholderprofile_industrial = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_industrial"))?rb1.getString("cardholderprofile_industrial"): "Industrial Bldg";
    String cardholderprofile_residence = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_residence"))?rb1.getString("cardholderprofile_residence"): "Residence";
    String cardholderprofile_areazone = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_areazone"))?rb1.getString("cardholderprofile_areazone"): "Area Zoned:";
    String cardholderprofile_commercial = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_commercial"))?rb1.getString("cardholderprofile_commercial"): "Commercial";
    String cardholderprofile_industrial1 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_industrial1"))?rb1.getString("cardholderprofile_industrial1"): "Industrial";
    String cardholderprofile_residential = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_residential"))?rb1.getString("cardholderprofile_residential"): "Residential";
    String cardholderprofile_square = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_square"))?rb1.getString("cardholderprofile_square"): "Square Footage/m2:";
    String cardholderprofile_500 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_500"))?rb1.getString("cardholderprofile_500"): "0-500";
    String cardholderprofile_501 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_501"))?rb1.getString("cardholderprofile_501"): "501-2500";
    String cardholderprofile_2501 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_2501"))?rb1.getString("cardholderprofile_2501"): "2501-5000";
    String cardholderprofile_5001 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_5001"))?rb1.getString("cardholderprofile_5001"): "5001-10000+";
    String cardholderprofile_inventory = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_inventory"))?rb1.getString("cardholderprofile_inventory"): "Does Merchant have the appropriate facilities, equipment, inventory, personnel and license/permit to operate their business?";
    String cardholderprofile_declarations = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_declarations"))?rb1.getString("cardholderprofile_declarations"): "Declarations:";
    String cardholderprofile_confirm = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_confirm"))?rb1.getString("cardholderprofile_confirm"): "I hereby confirm to be the owner of the listed website(s). I further declare to have full control and authorization of the website content. I acknowledge and agree that I will not use the Processing System for transactions relating to; 1) Sales made under a different trade name or business affiliation than indicated on this Agreement or otherwise approved by the acquirer in writing; 2) Fines or Penalties of any kind, losses, damages or any other costs that are beyond the Total Sale Price; 3) Any transaction that violates any law, ordinance, or regulation applicable to my business; 4) Goods which I / we know will be resold by a customer whom I / we reasonably should know is not ordinarily in the business of selling such goods; 5) Sales by third parties; 6) Any other amounts for which a customer has not specifically authorized payment through the acquirer; 7) Cash, traveler's checks, Cash equivalents, or other negotiable instruments; or 8) Amounts which do not represent a bona fide sale of goods or services by me / us. I also declare on behalf of the company and on behalf of myself that, to the best of our knowledge, neither the company nor the website nor myself (or any of us) have ever been involved in excessive chargeback’s, fraud or content violation nor have any of the above ever terminated by an acquirer or asked by an acquirer to terminate an agreement within a set period of time.";
    String cardholderprofile_cusumer = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_cusumer"))?rb1.getString("cardholderprofile_cusumer"): "Investigate Consumer Report";
    String cardholderprofile_investigative = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_investigative"))?rb1.getString("cardholderprofile_investigative"): "An investigative or consumer report may be made in connection with application. Merchant authorizes any party to the agreement or any of their agents to investigate the reference provided or any other statements or data obtained from merchant and from any of the undersigned personal guarantor(s), or from any person or entity with any financial obligations under this agreement. You have a right, upon written request, to a complete and accurate disclosure of the nature of and scope of the investigation requested.";
    String cardholderprofile_principal = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_principal"))?rb1.getString("cardholderprofile_principal"): "Principal 1";
    String cardholderprofile_date1 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_date1"))?rb1.getString("cardholderprofile_date1"): "Date";
    String cardholderprofile_principal2 = StringUtils.isNotEmpty(rb1.getString("cardholderprofile_principal2"))?rb1.getString("cardholderprofile_principal2"): "Principal 2";

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
                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"> <%=cardholderprofile_storage%></h2>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.compliance_swapp)||cardholderProfileInputList.contains(BankInputName.compliance_thirdpartyappform)||cardholderProfileInputList.contains(BankInputName.compliance_thirdpartysoft)||cardholderProfileInputList.contains(BankInputName.compliance_version)||cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways)||cardholderProfileInputList.contains(BankInputName.compliance_companiesorgateways_yes)|| view)
                                        {
                                    %>
                                    <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">&nbsp; <%=cardholderprofile_gateway%></label>
                                        &nbsp;&nbsp;<input type="radio"  name="compliance_swapp"  <%=globaldisabled%>  value="Y" <%=checkedY%>/><%if(validationErrorList.getError("compliance_swapp")!=null){%><span style="width:13%" class="apperrormsg">Invalid software or gateway application</span><%}%><%=cardholderprofile_yes%>
                                        &nbsp;&nbsp;<input type="radio" name="compliance_swapp"  <%=globaldisabled%>  value="N" <%=checkedN%> />  <%=cardholderprofile_no%>
                                    </div>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="compliance_thirdpartyappform" style="font-family:Open Sans;font-size: 13.4px;font-weight: 600;">&nbsp; <%=cardholderprofile_third%></label>
                                        <input type="text" class="form-control" id="compliance_thirdpartyappform" name="compliance_thirdpartyappform"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartyappform())==true?cardholderProfileVO.getCompliance_thirdpartyappform():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_thirdpartyappform")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                                    </div>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="compliance_thirdpartysoft" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_name%>  <font style="color:transparent; user-select: none;">purchase your Application from</font></label>
                                        <input type="text" class="form-control"  id="compliance_thirdpartysoft" name="compliance_thirdpartysoft"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>  value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartysoft())==true?cardholderProfileVO.getCompliance_thirdpartysoft():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_thirdpartysoft")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                                    </div>
                                    <div class="col-md-12"></div>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="compliance_thirdpartysoft" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_version%></label>
                                        <input type="text" class="form-control" id="compliance_version" name="compliance_version"style="border: 1px solid #b2b2b2;font-weight:bold;" <%=globaldisabled%>   value="<%=functions.isValueNull(cardholderProfileVO.getCompliance_version())==true?cardholderProfileVO.getCompliance_version():""%>" <%=disabled%>/><%if(validationErrorList.getError("compliance_version")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                                    </div>


                                    <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <div class="col-md-6" style="padding-left: 0;">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_webhosting%></label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio" name="compliance_companiesorgateways" id="compliance_companiesorgatewaysY" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_companiesorgateways())?"checked":""%> <%=disabled%>/><%if(validationErrorList.getError("compliance_companiesorgateways")!=null){%><span style="width:13%" class="apperrormsg">Invalid companiesorgateways</span><%}%>
                                            <%=cardholderprofile_yes%>&nbsp;&nbsp;
                                            <input type="radio" name="compliance_companiesorgateways" id="compliance_companiesorgatewaysN" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getCompliance_companiesorgateways()) || !functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways()) ?"checked":""%> <%=disabled%>/>         <%--Condition for Radio--%>
                                            <%=cardholderprofile_no%><%--&nbsp;&nbsp;&nbsp;&nbsp;If yes, who is it?--%>
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_receive%></label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio"  name="compliance_electronically" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_electronically())?"checked":""%> /><%if(validationErrorList.getError("compliance_electronically")!=null){%><span style="width:13%" class="apperrormsg">Invalid electronically</span><%}%>
                                            <%=cardholderprofile_yes%>&nbsp;&nbsp;
                                            <input type="radio" name="compliance_electronically"<%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_electronically()) || !functions.isValueNull(cardholderProfileVO.getCompliance_electronically()) ?"checked":""%> />            <%--Condition for Radio--%>
                                            <%=cardholderprofile_no%>
                                        </div>
                                    </div>


                                    <div class="form-group col-md-10" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_carddata%></label>

                                        <div class="col-md-10">
                                            <input type="radio" id="cd1" name="compliance_carddatastored" <%=globaldisabled%>  value="Merchant"   <%=electroDisabled%><%%> <%="Merchant".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                                            <%=cardholderprofile_Merchant%>
                                            &nbsp;&nbsp; <input type="radio"  id="cd2" name="compliance_carddatastored" <%=globaldisabled%>  value="ThirdParty" <%=electroDisabled%> <%="ThirdParty".equals(cardholderProfileVO.getCompliance_carddatastored())?"checked":""%>/>
                                            <%=cardholderprofile_thirdparty%>
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_vendor%></label>
                                        </div>
                                        <div class="col-md-6">
                                            &nbsp;&nbsp;<input type="radio"  name="compliance_pcidsscompliant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_pcidsscompliant())?"checked":""%> />
                                           <%=cardholderprofile_yes%>&nbsp;&nbsp;
                                            <input type="radio" name="compliance_pcidsscompliant" <%=globaldisabled%>   value="N" <%="N".equals(cardholderProfileVO.getCompliance_pcidsscompliant()) || !functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant()) ?"checked":""%> />
                                            <%=cardholderprofile_no%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
                                        <label for="compliance_qualifiedsecurityassessor" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_assessor%></label>
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
                                        <label for="compliance_dateofcompliance" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_date%></label>
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
                                        <label for="compliance_dateoflastscan" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_last%></label>
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_experience%></label>
                                        </div>
                                        <div class="col-md-6">
                                            &nbsp;&nbsp;<input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" name="compliance_datacompromise" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getCompliance_datacompromise())?"checked":""%>/>
                                            <%=cardholderprofile_yes%>&nbsp;&nbsp;
                                            <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" name="compliance_datacompromise" <%=globaldisabled%>  value="N"  <%=!functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise())||"N".equals(cardholderProfileVO.getCompliance_datacompromise())?"checked":""%>/> <%--this is radio button  we can use condition--%>
                                            <%=cardholderprofile_no%>
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


                                    <label class="newborder">    <%=cardholderprofile_association%></label>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.siteinspection_merchant)||cardholderProfileInputList.contains(BankInputName.siteinspection_landlord)||cardholderProfileInputList.contains(BankInputName.siteinspection_buildingtype)||cardholderProfileInputList.contains(BankInputName.siteinspection_areazoned)||cardholderProfileInputList.contains(BankInputName.siteinspection_squarefootage)||cardholderProfileInputList.contains(BankInputName.siteinspection_operatebusiness)||view)
                                        {

                                    %>


                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=cardholderprofile_site%></h2>

                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.siteinspection_merchant)||cardholderProfileInputList.contains(BankInputName.siteinspection_landlord)||view)
                                        {

                                    %>

                                    <div class="form-group col-md-12">
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_Merchant1%></label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_merchant" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getSiteinspection_merchant()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_merchant()) ?"checked":""%>/>&nbsp;<%=cardholderprofile_owns%>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_merchant" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getSiteinspection_merchant())?"checked":""%>/>&nbsp;<%=cardholderprofile_rents%>
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
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_building%></label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="ShoppingCtr" <%="ShoppingCtr".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%>/>&nbsp;
                                        <%=cardholderprofile_shop%>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="OfficeBldg" <%="OfficeBldg".equals(cardholderProfileVO.getSiteinspection_buildingtype()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_buildingtype()) ?"checked":""%> />&nbsp;
                                        <%=cardholderprofile_office%>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="IndustrialBldg" <%="IndustrialBldg".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%> />&nbsp;
                                        <%=cardholderprofile_industrial%>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_buildingtype" <%=globaldisabled%>  value="Residence" <%="Residence".equals(cardholderProfileVO.getSiteinspection_buildingtype())?"checked":""%>/>&nbsp;
                                       <%=cardholderprofile_residence%>
                                    </div>

                                    <%
                                        }
                                    %>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.siteinspection_areazoned)||view)
                                        {

                                    %>
                                    <div class="form-group col-md-12">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_areazone%></label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Commercial" <%="Commercial".equals(cardholderProfileVO.getSiteinspection_areazoned()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()) ?"checked":""%> />&nbsp;
                                        <%=cardholderprofile_commercial%>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Industrial" <%="Industrial".equals(cardholderProfileVO.getSiteinspection_areazoned())?"checked":""%>/>&nbsp;
                                        <%=cardholderprofile_industrial1%>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_areazoned" <%=globaldisabled%>  value="Residential" <%="Residential".equals(cardholderProfileVO.getSiteinspection_areazoned())?"checked":""%>/>&nbsp;
                                       <%=cardholderprofile_residential%>
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
                                        <%=cardholderprofile_500%>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="501-2500" <%="501-2500".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%> />&nbsp;
                                        <%=cardholderprofile_501%>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="2501-5000" <%="2501-5000".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%>/>&nbsp;
                                        <%=cardholderprofile_2501%>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <input type="radio" name="siteinspection_squarefootage" <%=globaldisabled%>  value="5001-10000+" <%="5001-10000+".equals(cardholderProfileVO.getSiteinspection_squarefootage())?"checked":""%> />&nbsp;
                                        <%=cardholderprofile_5001%>
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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_inventory%></label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio" name="siteinspection_operatebusiness" <%=globaldisabled%>  value="Y" <%="Y".equals(cardholderProfileVO.getSiteinspection_operatebusiness())?"checked":""%> />
                                           <%=cardholderprofile_yes%>&nbsp;&nbsp;
                                            <input type="radio" name="siteinspection_operatebusiness" <%=globaldisabled%>  value="N" <%="N".equals(cardholderProfileVO.getSiteinspection_operatebusiness()) || !functions.isValueNull(cardholderProfileVO.getSiteinspection_operatebusiness()) ?"checked":""%> />
                                            <%=cardholderprofile_no%>
                                        </div>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <label class="newborder bg-info" style="text-align: justify;">     <b><%=cardholderprofile_declarations%></b><br />
                                        <%=cardholderprofile_confirm%><br /><br/>
                                        <b><%=cardholderprofile_cusumer%></b><br />
                                        <%=cardholderprofile_investigative%></label>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(cardholderProfileInputList.contains(BankInputName.siteinspection_principal1)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-3">
                                        <label for="siteinspection_principal1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_principal%></label>
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
                                        <label for="siteinspection_principal1_date" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_date1%></label>
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
                                        <label for="siteinspection_principal2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=cardholderprofile_principal2%></label>
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
                                        <label for="siteinspection_principal2_date" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=cardholderprofile_date1%></label>
                                        <input type="text" class="form-control datepicker"  id="siteinspection_principal2_date" name="siteinspection_principal2_date" style="border: 1px solid #b2b2b2;font-weight:bold;width: 100%;" <%=globaldisabled%>   value="<%=functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2_date())==true?commonFunctionUtil.convertTimestampToDatepicker(cardholderProfileVO.getSiteinspection_principal2_date()):""%>" /><%if(validationErrorList.getError("siteinspection_principal2_date")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
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
                "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;CardHolder Profile</strong></h2>\n" +
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

