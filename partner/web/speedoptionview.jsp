<%@ page import = "org.owasp.esapi.ESAPI" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.CompanyProfileVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.vo.applicationManagerVOs.BusinessProfileVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankProfileVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.enums.ApplicationStatus" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="COM.rsa.jsafe.ac" %>
<%@ page import="com.enums.ApplicationManagerTypes" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ include file="index.jsp" %>
<%
    //company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","speedoption");

%>

<%!
    private Functions functions = new Functions();
    private CommonFunctionUtil commonFunctionUtil= new CommonFunctionUtil();
    List<ApplicationStatus> applicationStatusNotNeeded= new ArrayList<ApplicationStatus>();
    ApplicationManager applicationManager=new ApplicationManager();

    private static Logger logger=new Logger("speedoptionview.jsp");

%>
<%
    ActionVO actionVO=new ActionVO();
    if(session.getAttribute("actionVO")!=null)
    {
        actionVO= (ActionVO) session.getAttribute("actionVO");
    }
%>
<%
    ApplicationManagerVO applicationManagerVO=null;
    CompanyProfileVO companyProfileVO=null;
    BankProfileVO bankProfileVO=null;
    BusinessProfileVO businessProfileVO=null;
    ValidationErrorList validationErrorList=null;
  /*ActionVO actionVO = new ActionVO();*/

    if(session.getAttribute("actionVO")!=null)
    {
        actionVO= (ActionVO) session.getAttribute("actionVO");
    }

    if(session.getAttribute("applicationManagerVO")!=null)
    {

        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");

    }

    if(applicationManagerVO.getCompanyProfileVO()!=null)
    {
        companyProfileVO = applicationManagerVO.getCompanyProfileVO();


        if (companyProfileVO == null)
        {
            companyProfileVO = new CompanyProfileVO();
        }
        if (applicationManagerVO.getBusinessProfileVO() != null)
        {
            businessProfileVO = applicationManagerVO.getBusinessProfileVO();
        }
        if (businessProfileVO == null)
        {
            businessProfileVO = new BusinessProfileVO();
        }
        if (applicationManagerVO.getBankProfileVO() != null)
        {
            bankProfileVO = applicationManagerVO.getBankProfileVO();
        }
        if (bankProfileVO == null)
        {
            bankProfileVO = new BankProfileVO();
        }
        if (session.getAttribute("validationErrorList") != null)
        {
            validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");
        }
        else if (request.getAttribute("validationErrorList") != null)
        {
            validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");
        }
        else
        {
            validationErrorList = new ValidationErrorList();
        }
    }
    else
    {
        if (companyProfileVO == null)
        {
            companyProfileVO = new CompanyProfileVO();
        }
        if (businessProfileVO == null)
        {
            businessProfileVO = new BusinessProfileVO();
        }
        if (bankProfileVO == null)
        {
            bankProfileVO = new BankProfileVO();
        }
        if (session.getAttribute("validationErrorList") != null)
        {
            validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");
        }
        else if (request.getAttribute("validationErrorList") != null)
        {
            validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");
        }
        else
        {
            validationErrorList = new ValidationErrorList();
        }

    }
%>
<html lang="en">
<head>

    <title>Pre-vet Form</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <script src='/partner/stylenew/BeforeAppManager.js'></script>
    <script src="/merchant/NewCss/am_multipleselection/jquery-multipleselection.js"></script>
    <script src="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
    <link href="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
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

    <style type="text/css">

        .form-control-feedback {
            position: absolute;
            top: 23!important;
            right: 0;
            z-index: 2;
            display: block;
            width: 30px;
            height: 34px;
            line-height: 34px;
            text-align: center;
            pointer-events: none;
            background-color: #ebccd1!important;
            right: 15px!important;
        }

    </style>

    <script>
        $(function () {
            $('#productsoldcurrency').multiselect({
                buttonText: function(options, select) {
                    if (options.length === 0) {
                        return 'Select Currency';
                    }
                    /* if (options.length === select[0].length) {
                     return 'All selected ('+select[0].length+')';
                     }
                     else if (options.length >= 4) {
                     return options.length + ' selected';
                     }*/
                    else {
                        var labels = [];
                        console.log(options);
                        options.each(function() {
                            labels.push($(this).val());
                        });
                        document.getElementById('product_sold_currencies').value = labels;
                        return labels.join(', ') + '';
                    }
                }
            });
        });
    </script>
    <style>
        .multiselect-container>li {
            padding: 0;
            margin-left: 31px;
        }
        .open>#multiselect-id.dropdown-menu {
            display: block;
            overflow-y: scroll;
            height: 500%;
        }
        .multiselect-container>li>a>label {
            margin: 0;
            height: 24px;
            font-weight: 400;
            padding: 3px 20px 3px 40px;
        }
        span.multiselect-native-select {
            position: relative;
        }

        span.multiselect-native-select select {
            border: 0!important;
            clip: rect(0 0 0 0)!important;
            height: 1px!important;
            margin: -1px -1px -1px -3px!important;
            overflow: hidden!important;
            padding: 0!important;
            position: absolute!important;
            width: 1px!important;
            left: 50%;
            top: 30px;
        }
        select[multiple], select[size] {
            height: auto;
        }
        .widget .btn-group {
            z-index: 1;
        }

        .btn-group, .btn-group-vertical {
            position: relative;
            display: flex;
            vertical-align: middle;

        }
        .btn {

            display: inline-block;
            padding: 6px 12px;
            margin-bottom: 0;
            font-size: 14px;
            font-weight: normal;
            line-height: 1.428571429;
            text-align: center;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            background-image: none;
            border: 1px solid transparent;
            /*border-radius: 4px;*/
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            -o-user-select: none;
            user-select: none;
        }
        #mainbtn-id.btn-default {
            color: #333;
            background-color: #fff;
            border-color: #ccc;
        }
        .btn-group>.btn:first-child {
            margin-left: 0;
        }

        .btn-group>.btn:first-child {
            margin-left: 0;
        }

        .btn-group>.btn, .btn-group-vertical>.btn {
            position: relative;
            float: left;
        }
        .multiselect-container {
            position: absolute;
            list-style-type: none;
            margin: 0;
            padding: 0;
        }
        #multiselect-id.dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            z-index: 1000;
            display: none;
            float: left;
            min-width: 160px;
            padding: 5px 0;
            margin: 2px 0 0;
            font-size: 14px;
            list-style: none;
            background-color: #fff;
            border: 1px solid #ccc;
            border: 1px solid rgba(0,0,0,0.15);
            /*border-radius: 4px;*/
            -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
            box-shadow: 0 6px 12px rgba(0,0,0,0.175);
            background-clip: padding-box;
        }
        #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open>.dropdown-toggle#mainbtn-id.btn-default {
            color: #333;
            background-color: white;
            border-color: #b2b2b2;
            text-align: left;
            width: 100%;
        }
        .caret {
            display: inline-block;
            width: 0;
            height: 0;
            margin-left: 2px;
            vertical-align: middle;
            border-top: 4px solid;
            border-right: 4px solid transparent;
            border-left: 4px solid transparent;
            float: right;
            margin-top: 8px;
        }


        input[type=radio], input[type=checkbox] {
            -ms-transform: scale(1.6); /* IE */
            -moz-transform: scale(1.6); /* FF */
            -webkit-transform: scale(1.6); /* Safari and Chrome */
            -o-transform: scale(1.6); /* Opera */
            padding: 10px;
        }

        .multiselect-selected-text {
            overflow: hidden;
            display: block;
        }

    </style>


</head>
<%
    List<String> ProductSoldCurrencyList= new ArrayList();
    if(session.getAttribute("applicationManagerVO")!=null)
    {
        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
    }

    if(applicationManagerVO.getBusinessProfileVO()!=null)
    {
        businessProfileVO=applicationManagerVO.getBusinessProfileVO();
        if(functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
        {
            String arrcurrency[] = businessProfileVO.getProduct_sold_currencies().split(",");
            for(int i=0; i<arrcurrency.length; i++)
            {
                ProductSoldCurrencyList.add(arrcurrency[i]);
            }
        }

    }
%>

<body class="bodybackground">

<jsp:include page="appManagerAction.jsp"></jsp:include>

<div class="content-page">
    <div class="content" style="margin-top: 0;">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <form action="/partner/net/SpeedOption?ctoken=<%=ctoken%>" method="post" name="myformname" id="myformname">

                <div class="row">

                    <div class="col-sm-12 portlets ui-sortable">

                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Pre-vet Form</strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():" bg-alert "%>"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;<%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save the Pre-vet form after entering the data provided"%></h5>

                                    <%--<h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #7eccad !important;color: white;font-size: 14px;font-family: 'Open Sans';font-weight: 700;">Company Profile</h2>--%>

                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">Company Information</h2>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="merchantname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Legal Name & Form*</label>
                                        <input type="text"  class="form-control"  id="merchantname" name="merchantname" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name())?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name():""%>" <%=actionVO.isView()?"disabled":""%> <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("merchantname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Trading As(DBA)*</label>
                                        <input type="text" class="form-control"   id="corporatename" name="corporatename"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name():""%>" <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="companyregistrationnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number*</label>
                                        <input type="text" class="form-control"  id="companyregistrationnumber" name="companyregistrationnumber"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number():""%>" <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("companyregistrationnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Registration*</label>
                                        <input type="text" name="Company_Date_Registration" class="form-control datepicker" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration())==true?commonFunctionUtil.convertTimestampToDatepicker(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration()):""%>"  <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("Company_Date_Registration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="contactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Legal Representative</label>
                                        <input type="text" class="form-control"  id="contactname" name="contactname"style="border: 1px solid #b2b2b2;font-weight:bold"value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName())==true?companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName():""%>" <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("contactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="locationaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Address(no p/o)*</label>
                                        <input type="text" class="form-control"  id="locationaddress" name="locationaddress"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress())==true?StringEscapeUtils.escapeHtml(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress()):""%>" <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("locationaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4">
                                        <label for="vatidentification" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">VAT Number</label>
                                        <input type="text" class="form-control"  id="vatidentification" name="vatidentification" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification():""%>" <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("vatidentification")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="merchantzipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip Code</label>
                                        <input type="text" class="form-control"  id="merchantzipcode" name="merchantzipcode" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode():""%>" <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("merchantzipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="merchantcountry" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                        <select  class="form-control" id="merchantcountry" name="merchantcountry"  <%=actionVO.isView()?"disabled":""%> style="border: 1px solid #b2b2b2;font-weight:bold"  onchange="mycountrycode('merchantcountry','Companyphonecc1');">
                                            <%=CommonFunctionUtil.getCountryDetails(functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry():"")%>
                                        </select>
                                        <%if(validationErrorList.getError("merchantcountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>

                                    </div>

                                    <div class="form-group col-md-2 has-feedback">
                                        <label for="Companyphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">PhoneCC*</label>
                                        <input type="text" class="form-control"  id="Companyphonecc1" name="Companyphonecc1" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc():""%>" <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("Companyphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-2 has-feedback">
                                        <label for="CompanyTelephoneNO" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No*</label>
                                        <input type="text" class="form-control"   id="CompanyTelephoneNO" name="CompanyTelephoneNO" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number():""%>"  <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("CompanyTelephoneNO")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="CompanyEmailAddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                        <input type="text" class="form-control"   id="CompanyEmailAddress" name="CompanyEmailAddress" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id():""%>"  <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("CompanyEmailAddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="urls" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website URL(S)*</label>
                                        <input type="text" class="form-control"  id="urls" name="urls" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(businessProfileVO.getUrls())==true?businessProfileVO.getUrls():""%>" <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("urls")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="FederalTaxID" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">TIC / TIN Number / Federal Tax ID / PAN</label>
                                        <input type="text" class="form-control"  id="FederalTaxID" name="FederalTaxID" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId():""%>"  <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("FederalTaxID")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>

                                    <div class="form-group col-md-12">
                                        <label for="FederalTaxID" style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Type of Business :-</u></label>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="radio"   name="company_typeofbusiness" style="width:30%;"  value="Corporation" <%="Corporation".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/> &nbsp;Corporation
                                        </label>
                                    </div>
                                    <div class="form-group col-lg-3 col-md-4">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="radio"  name="company_typeofbusiness" style="width:20%;"  value="LimitedLiabilityCompany" <%="LimitedLiabilityCompany".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> /> &nbsp;Limited Liability Company</label>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="SoleProprietor"<%="SoleProprietor".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> /> &nbsp;Sole Proprietor</label>
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="Partnership" <%="Partnership".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/> &nbsp;Partnership
                                        </label>
                                    </div>
                                    <div class="form-group col-lg-3 col-md-4">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="NotforProfit" <%="NotforProfit".equals(companyProfileVO.getCompanyTypeOfBusiness()) || !functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ?"checked":""%>  /> &nbsp;Nonprofit Organization</label>
                                    </div>




                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">Bank Information</h2>



                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_bank_name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Bank Name</label>
                                        <input type="text"  class="form-control"  id="bankinfo_bank_name" name="bankinfo_bank_name" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bank_name())==true?bankProfileVO.getBankinfo_bank_name():""%>"  <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("bankinfo_bank_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_accountnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Bank Account Number</label>
                                        <input type="text"  class="form-control"  id="bankinfo_accountnumber" name="bankinfo_accountnumber" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountnumber())==true?bankProfileVO.getBankinfo_accountnumber():""%>"  <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("bankinfo_accountnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <%--@declare id="bankinfo_iban"--%><label for="bankinfo_IBAN" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Bank IBAN</label>
                                        <input type="text"  class="form-control"  id="bankinfo_IBAN" name="bankinfo_IBAN" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_IBAN())==true?bankProfileVO.getBankinfo_IBAN():""%>"  <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("bankinfo_IBAN")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_bic" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">SWIFT / IFSC / BIC (Bank Identifier Code)</label>
                                        <input type="text" class="form-control"   id="bankinfo_bic" name="bankinfo_bic" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bic())==true?bankProfileVO.getBankinfo_bic():""%>" <%=actionVO.isView()?"disabled":""%> />
                                        <%if(validationErrorList.getError("bankinfo_bic")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_accountholder" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Account Holder</label>
                                        <input type="text" class="form-control" id="bankinfo_accountholder" name="bankinfo_accountholder" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountholder())==true?bankProfileVO.getBankinfo_accountholder():""%>" <%=actionVO.isView()?"disabled":""%>/>
                                        <%if(validationErrorList.getError("bankinfo_accountholder")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " ></i>
                                        <%}%>
                                    </div>


                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">CURRENCY REQUESTED</h2>


                                    <div class="form-group col-md-6 has-feedback">

                                        <label for="product_sold_currencies" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">In which currency the products are sold?</label>
                                    </div>

                                    <div class="form-group col-md-6 has-feedback">
                                        <div class="col-md-10" style="padding: 0;">
                                            <select size="5" multiple="multiple" id="productsoldcurrency" class="form-group" >
                                                <%
                                                    out.println(AppFunctionUtil.getCurrencyProductSold(functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) == true ? businessProfileVO.getProduct_sold_currencies() : "", ProductSoldCurrencyList));
                                                %>
                                            </select>

                                            <input type="hidden" id="product_sold_currencies" name="product_sold_currencies" value="">
                                        </div>

                                    </div>


                                    <%--<div class="form-group col-md-12">
                                        <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;">In which currency are your products sold?</label>
                                    </div>

                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">USD</label>
                                        <input type="checkbox" id="currency_products_USD" name="currency_products_USD" value="Y" value="Y" <%=actionVO.isView()? "disabled": ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_USD())?"checked":""%>>
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">GBP</label>
                                        <input type="checkbox" id="currency_products_GBP" name="currency_products_GBP" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_GBP())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">EUR</label>
                                        <input type="checkbox"  id="currency_products_EUR" name="currency_products_EUR" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_EUR())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">JPY</label>
                                        <input type="checkbox"  id="currency_products_JPY" name="currency_products_JPY" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_JPY())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">PEN</label>
                                        <input type="checkbox"  id="currency_products_PEN" name="currency_products_PEN" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_PEN())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">HKD</label>
                                        <input type="checkbox"  id="currency_products_HKD" name="currency_products_HKD" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_HKD())?"checked":""%> >

                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">AUD</label>
                                        <input type="checkbox"  id="currency_products_AUD" name="currency_products_AUD" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_AUD())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">CAD</label>
                                        <input type="checkbox"  id="currency_products_CAD" name="currency_products_CAD" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_CAD())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">DKK</label>
                                        <input type="checkbox"  id="currency_products_DKK" name="currency_products_DKK" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_DKK())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">SEK</label>
                                        <input type="checkbox"  id="currency_products_SEK" name="currency_products_SEK" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_SEK())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">NOK</label>
                                        <input type="checkbox"  id="currency_products_NOK" name="currency_products_NOK" value="Y" <%=actionVO.isView() ? "disabled" : ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_NOK())?"checked":""%> >
                                    </div>
                                    <div class="form-group col-md-1">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">INR</label>
                                        <input type="checkbox"  id="currency_products_INR" name="currency_products_INR" value="Y" <%=actionVO.isView()? "disabled": ""%>
                                            <%="Y".equals(bankProfileVO.getCurrency_products_INR())?"checked":""%> >
                                    </div>--%>



                                    <center>
                                        <div class="form-group col-md-12">
                                            <%-- style="margin-left: 40%;"--%>
                                            <%
                                                if(!actionVO.isView())
                                                {
                                            %>
                                            <button class="btn btn-default" type="submit" name="action" value="Save">Save</button>&nbsp;&nbsp;&nbsp;
                                            <button class="btn btn-default" type="submit" name="action" value="Submit">Submit</button>
                                            <%
                                                }
                                            %>
                                        </div>
                                    </center>


                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </form>

            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Modified Status</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <%--<h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;color: white;font-size: 14px;font-family: 'Open Sans';font-weight: 700;">Bank Information</h2>--%>

                                <form action="/partner/net/AppManagerStatus?ctoken=<%=ctoken%>" method="post">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <input type="hidden" name="SPEED" value="Y">
                                    <%
                                        applicationStatusNotNeeded.add(ApplicationStatus.SAVED);
                                        applicationStatusNotNeeded.add(ApplicationStatus.SUBMIT);
                                        //applicationStatusNotNeeded.add(ApplicationStatus.STEP1_SUBMIT);
                                        //applicationStatusNotNeeded.add(ApplicationStatus.STEP1_SAVED);
                                        applicationStatusNotNeeded.add(ApplicationStatus.MODIFIED);
                                        //applicationStatusNotNeeded.add(ApplicationStatus.VERIFIED);

                                    %>

                                    <div class="form-group col-md-12 has-feedback">
                                        <label for="merchantname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Status</label>
                                        <select  name="status"  class="form-control" style="width: 50%;">
                                            <%=applicationManager.getApplicationStatus(functions.isValueNull(applicationManagerVO.getSpeed_status()) == true ? applicationManagerVO.getSpeed_status() : "",applicationStatusNotNeeded)%>

                                        </select>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"></label>
                                        <button type="submit" class="buttonform btn btn-default" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<script src='/partner/stylenew/BeforeAppManager.js'></script>
