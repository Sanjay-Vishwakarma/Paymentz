<%@ page import = "com.directi.pg.Functions" %>
<%@ page import="com.enums.ApplicationManagerTypes" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankProfileVO" %>
<%@ page import="com.vo.applicationManagerVOs.BusinessProfileVO" %>
<%@ page import="com.vo.applicationManagerVOs.CompanyProfileVO" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ include file="top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","speedoption");

%>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
<script src='/partner/stylenew/BeforeAppManager.js'></script>
<script src="/partner/NewCss/am_multipleselection/jquery-multipleselection.js"></script>
<script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
<link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
<%!
  private Functions functions = new Functions();
  private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
%>
<%
  ApplicationManagerVO applicationManagerVO=null;
  CompanyProfileVO companyProfileVO=null;
  BankProfileVO bankProfileVO=null;
  BusinessProfileVO businessProfileVO=null;
  ValidationErrorList validationErrorList=null;
  ActionVO actionVO = new ActionVO();

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
    companyProfileVO=applicationManagerVO.getCompanyProfileVO();
  }

  if(companyProfileVO==null)
  {
    companyProfileVO=new CompanyProfileVO();
  }
  if(applicationManagerVO.getBusinessProfileVO()!=null)
  {
    businessProfileVO=applicationManagerVO.getBusinessProfileVO();
  }
  if(businessProfileVO==null)
  {
    businessProfileVO=new BusinessProfileVO();
  }
  if(applicationManagerVO.getBankProfileVO()!=null)
  {
    bankProfileVO=applicationManagerVO.getBankProfileVO();
  }
  if(bankProfileVO==null)
  {
    bankProfileVO=new BankProfileVO();
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
<html lang="en">
<head>

  <title><%=company%> Privet Form</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <%--<link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap.min.css">--%>
  <link rel="stylesheet" href="/partner/transactionCSS/css/main.css">
  <script type="text/javascript" src="/partner/transactionCSS/js/jquery.js"></script>
  <script type="text/javascript" src="/partner/transactionCSS/js/creditly.js"></script>
  <link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>
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
    .open>.dropdown-menu {
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
      border-radius: 4px;
      -webkit-user-select: none;
      -moz-user-select: none;
      -ms-user-select: none;
      -o-user-select: none;
      user-select: none;
    }
    .btn-default {
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
    .dropdown-menu {
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
      border-radius: 4px;
      -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      background-clip: padding-box;
    }
    .btn-default, .btn-default:focus, .btn-default.focus, .btn-default:active, .btn-default.active, .open>.dropdown-toggle.btn-default {
      color: #333;
      background-color: white;
      border-color: gray;
      text-align: left;
      width: 70%;
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

  </style>

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

    .bg-infoorange {
      padding: 15px;
      margin-bottom: 20px;
      border: 1px solid transparent;
      border-radius: 4px;
      color: #474a54;
      background-color: #fdf7f7;
      border-left: 4px solid #f00;
      font-family: "Open Sans";
      font-size: 13px;
      font-weight: 600;
      text-align: center;
    }


    /***********************Dropdown For Currency****************************/

    .dropdown dd,
    .dropdown dt, .dropdown2 dd,
    .dropdown2 dt,  {
      margin: 0px;
      padding: 0px;
    }

    .dropdown ul, .dropdown2 ul {
      margin: -1px 0 0 0;
    }

    .dropdown li, .dropdown2 li {
      margin: 10px;
    }

    .dropdown dd, .dropdown2 dd {
      position: relative;
    }

    .dropdown a,
    .dropdown a:visited {
      color: #fff;
      text-decoration: none;
      outline: none;
      font-size: 12px;
    }

    .dropdown2 a,
    .dropdown2 a:visited {
      color: #fff;
      text-decoration: none;
      outline: none;
      font-size: 12px;
    }

    .dropdown dt a, .dropdown2 dt a {
      background-color: #ffffff;
      display: block;
    }

    .dropdown dt a span,
    .multiSel span, .dropdown2 dt a span,
    .multiSel2 span {
      cursor: pointer;
      display: inline-block;
      color:#555;
    }

    .dropdown dd ul, .dropdown2 dd ul {
      background-color: #ffffff;
      border: 0;
      color: #555;
      display: none;
      left: 0px;
      padding: 2px 15px 2px 5px;
      position: absolute;
      top: 2px;
      width: 100%;
      list-style: none;
      height: 100px;
      overflow: auto;
      font-size: 13px;
    }

    .dropdown span.value {
      display: none;
    }

    .dropdown2 span.value {
      display: none;
    }

    .dropdown dd ul li a, .dropdown2 dd ul li a {
      padding: 5px;
      display: block;
    }

    .dropdown dd ul li a:hover {
      background-color: #fff;
    }

    .dropdown2 dd ul li a:hover {
      background-color: #fff;
    }


    @media(max-width: 991px){
      #margintop10{
        margin-top: 10px;
      }
    }

    #align_check > .icheckbox_square-aero{
      display: table;
    }


    input[type=radio], input[type=checkbox] {
      -ms-transform: scale(1.6); /* IE */
      -moz-transform: scale(1.6); /* FF */
      -webkit-transform: scale(1.6); /* Safari and Chrome */
      -o-transform: scale(1.6); /* Opera */
      padding: 10px;
    }


    #businessid .checkbox-inline input[type=checkbox] {
      position: inherit!important;
      margin-left: inherit!important;
    }

  </style>

</head>
<%
  if(session.getAttribute("applicationManagerVO")!=null)
  {
    applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
  }
  List<String> ProductSoldCurrencyList= new ArrayList();
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
<body>

<form action="/merchant/servlet/SpeedOption?ctoken=<%=ctoken%>" method="post" name="myformname" id="myformname">
  <div class="content-page">
    <div class="content">
      <!-- Page Heading Start -->
      <div class="page-heading">
        <div class="row">

          <div class="col-sm-12 portlets ui-sortable">

            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Privet Form</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>

              <div class="widget-content padding">
                <div id="horizontal-form">

                  <h5 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"bg-alert"%>"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;<%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Organisation Profile after entering the data provided"%></h5>

                  <%--<h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #7eccad !important;color: white;font-size: 14px;font-family: 'Open Sans';font-weight: 700;">Company Profile</h2>--%>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="merchantname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Legal Name & Form*</label>
                    <input type="text"  class="form-control"  id="merchantname" name="merchantname" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("merchantname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Trading As(DBA)*</label>
                    <input type="text" class="form-control"   id="corporatename" name="corporatename"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="companyregistrationnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number*</label>
                    <input type="text" class="form-control"  id="companyregistrationnumber" name="companyregistrationnumber"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("companyregistrationnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Registration*</label>
                    <input type="text" name="Company_Date_Registration" class="form-control datepicker" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration())==true?commonFunctionUtil.convertTimestampToDatepicker(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration()):""%>"/><%if(validationErrorList.getError("Company_Date_Registration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="contactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Legal Representative</label>
                    <input type="text" class="form-control"  id="contactname" name="contactname"style="border: 1px solid #b2b2b2;font-weight:bold"value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName())==true?companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("contactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="locationaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Address(no p/o)*</label>
                    <input type="text" class="form-control"  id="locationaddress" name="locationaddress"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress())==true?StringEscapeUtils.escapeHtml(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress()):""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("locationaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4">
                    <label for="vatidentification" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">VAT Number</label>
                    <input type="text" class="form-control"  id="vatidentification" name="vatidentification" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("vatidentification")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="merchantzipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip Code</label>
                    <input type="text" class="form-control"  id="merchantzipcode" name="merchantzipcode" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("merchantzipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="merchantcountry" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                    <select  class="form-control" id="merchantcountry" name="merchantcountry" style="border: 1px solid #b2b2b2;font-weight:bold" <%=actionVO.isView()?"disabled":""%>  onchange="mycountrycode('merchantcountry','Companyphonecc1');">
                      <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry():"")%>
                    </select><%if(validationErrorList.getError("merchantcountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>

                  </div>

                  <div class="form-group col-md-2 has-feedback">
                    <label for="Companyphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">PhoneCC*</label>
                    <input type="text" class="form-control"  id="Companyphonecc1" name="Companyphonecc1" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("Companyphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-2 has-feedback">
                    <label for="CompanyTelephoneNO" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No*</label>
                    <input type="text" class="form-control"   id="CompanyTelephoneNO" name="CompanyTelephoneNO" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("CompanyTelephoneNO")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="CompanyEmailAddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                    <input type="text" class="form-control"   id="CompanyEmailAddress" name="CompanyEmailAddress" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id():""%>"<%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("CompanyEmailAddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="urls" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website URL(S)*</label>
                    <input type="text" class="form-control"  id="urls" name="urls" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(businessProfileVO.getUrls())==true?businessProfileVO.getUrls():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("urls")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="FederalTaxID" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">TIC / TIN Number / Federal Tax ID / PAN</label>
                    <input type="text" class="form-control"  id="FederalTaxID" name="FederalTaxID" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId():""%>" /><%if(validationErrorList.getError("FederalTaxID")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>

                  <div class="form-group col-md-12">
                    <label for="FederalTaxID" style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Type of Business :-</u></label>
                  </div>
                  <div class="form-group col-lg-2 col-md-4 has-feedback">
                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                      <input type="radio"   name="company_typeofbusiness" style="width:30%;"  value="Corporation" <%="Corporation".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                      &nbsp;Corporation</label>
                  </div>
                  <div class="form-group col-lg-3 col-md-4">
                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                      <input type="radio"  name="company_typeofbusiness" style="width:20%;"  value="LimitedLiabilityCompany" <%="LimitedLiabilityCompany".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                      &nbsp;Limited Liability Company</label>
                  </div>
                  <div class="form-group col-lg-2 col-md-4 has-feedback">
                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;">
                      <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="SoleProprietor"<%="SoleProprietor".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                      &nbsp;Sole Proprietor</label>
                  </div>
                  <div class="form-group col-lg-2 col-md-4 has-feedback">
                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                      <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="Partnership" <%="Partnership".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                      &nbsp;Partnership</label>
                  </div>
                  <div class="form-group col-lg-3 col-md-4">
                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                      <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="NotforProfit" <%="NotforProfit".equals(companyProfileVO.getCompanyTypeOfBusiness()) || !functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ?"checked":""%>  />
                      &nbsp;Nonprofit Organization</label>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>


        <div class="row">

          <div class="col-sm-12 portlets ui-sortable">

            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Bank Information</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>

              <div class="widget-content padding">
                <div id="horizontal-form">
                  <%--<h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;color: white;font-size: 14px;font-family: 'Open Sans';font-weight: 700;">Bank Information</h2>--%>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="bankinfo_bank_name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Select Currency</label>
                    <%
                      ApplicationManager applicationManager = new ApplicationManager();
                      List<String> currencyList = applicationManager.getcurrencyCode();
                      String currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

                      /*String disabledProcessingHistory = "";
                      if(functions.isValueNull(bankProfileVO.getIsProcessingHistory()) && "Y".equals(bankProfileVO.getIsProcessingHistory()))
                      {
                        disabledProcessingHistory = "disabled";
                      }*/
                    %>
                    <select size="1" id="currency" name="currency" class="form-control">
                      <%
                        if(currencyList.size()>0)
                        {
                          Iterator currencyIterator = currencyList.iterator();
                          String singleCurrency = "";
                          while(currencyIterator.hasNext())
                          {
                            singleCurrency = (String) currencyIterator.next();
                            String select = "";
                            if(currency.equalsIgnoreCase(singleCurrency))
                            {
                              select = "selected";
                            }
                      %>
                      <option value="<%=singleCurrency%>" <%=select%> ><%=singleCurrency%></option>
                      <%
                          }
                        }
                      %>
                    </select>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="bankinfo_bank_name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Bank Name</label>
                    <input type="text"  class="form-control"  id="bankinfo_bank_name" name="bankinfo_bank_name" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bank_name())==true?bankProfileVO.getBankinfo_bank_name():""%>" /><%if(validationErrorList.getError("bankinfo_bank_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="bankinfo_accountnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Account Number</label>
                    <input type="text"  class="form-control"  id="bankinfo_accountnumber" name="bankinfo_accountnumber" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountnumber())==true?bankProfileVO.getBankinfo_accountnumber():""%>"/><%if(validationErrorList.getError("bankinfo_accountnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="bankinfo_IBAN" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">IBAN</label>
                    <input type="text"  class="form-control"  id="bankinfo_IBAN" name="bankinfo_IBAN" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_IBAN())==true?bankProfileVO.getBankinfo_IBAN():""%>"/><%if(validationErrorList.getError("bankinfo_IBAN")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="bankinfo_bic" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">SWIFT / IFSC / BIC (Bank Identifier Code)</label>
                    <input type="text" class="form-control"   id="bankinfo_bic" name="bankinfo_bic" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bic())==true?bankProfileVO.getBankinfo_bic():""%>" <%=actionVO.isView()?"disabled":""%>  /><%if(validationErrorList.getError("bankinfo_bic")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label for="bankinfo_accountholder" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Account Holder</label>
                    <input type="text" class="form-control" id="bankinfo_accountholder" name="bankinfo_accountholder" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountholder())==true?bankProfileVO.getBankinfo_accountholder():""%>"<%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("bankinfo_accountholder")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                  </div>

                  <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">CURRENCY REQUESTED</h2>


                  <div class="form-group col-md-12">
                    <div class="form-group col-md-4">
                      <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;" >In which currency are your products sold?</label>
                    </div>
                    <div class="form-group col-md-4 has-feedback">
                      <input type="text"  class="form-control"  id="product_sold_currencies" name="product_sold_currencies" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getProduct_sold_currencies()) ? bankProfileVO.getProduct_sold_currencies():""%>" /><%if(validationErrorList.getError("product_sold_currencies")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                    </div>
                  </div>

                  <div class="form-group col-md-12">&nbsp;&nbsp;
                    <%--<label class="checkbox-inline icheckbox" style="margin-top: 0px;">
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox" style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_USD" name="currency_products_USD"  value="Y" value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_USD())?"checked":""%>>

                      </div>&nbsp;USD
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_GBP" name="currency_products_GBP"   value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_GBP())?"checked":""%> >

                      </div>&nbsp;GBP
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_EUR" name="currency_products_EUR"  value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_EUR())?"checked":""%> >

                      </div>&nbsp;EUR
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_JPY" name="currency_products_JPY"   value="Y"  <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_JPY())?"checked":""%> >

                      </div>&nbsp;JPY
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_PEN" name="currency_products_PEN"   value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_PEN())?"checked":""%> >

                      </div>&nbsp;PEN
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_HKD" name="currency_products_HKD"   value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_HKD())?"checked":""%> >

                      </div>&nbsp;HKD
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_AUD" name="currency_products_AUD"   value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_AUD())?"checked":""%> >

                      </div>&nbsp;AUD
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_CAD" name="currency_products_CAD"   value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_CAD())?"checked":""%> >

                      </div>&nbsp;CAD
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_DKK" name="currency_products_DKK"  value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_DKK())?"checked":""%> >

                      </div>&nbsp;DKK
                    </label>
                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_SEK" name="currency_products_SEK"   value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_SEK())?"checked":""%> >

                      </div>&nbsp;SEK
                    </label>

                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_NOK" name="currency_products_NOK"  value="Y" <%=actionVO.isView() ? "disabled" : ""%> <%="Y".equals(bankProfileVO.getCurrency_products_NOK())?"checked":""%>  >

                      </div>&nbsp;NOK
                    </label>
                    <label class="checkbox-inline icheckbox" &lt;%&ndash;style="margin-top: 0px;padding-left: 25px;"&ndash;%&gt;>
                      <div class="icheckbox_square-aero" style="position: relative;">
                        <input type="checkbox"  style="position: absolute; top: -20%; left: -20%; display: block; width: 140%; height: 140%; margin: 0px; padding: 0px; border: 0px; opacity: 1; background: rgb(255, 255, 255);" id="currency_products_INR" name="currency_products_INR"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_INR())?"checked":""%> >

                      </div>&nbsp;INR
                    </label>--%>
                  </div>

                  <center>
                    <div class="form-group col-md-12"><%-- style="margin-left: 40%;"--%>
                      <%
                        if(!actionVO.isView())
                        {
                      %>
                      <button class="btn" type="submit" name="action" value="Save">Save</button>&nbsp;&nbsp;&nbsp;
                      <button class="btn" type="submit" name="action" value="Submit">Submit</button>
                      <%
                        }
                      %>
                    </div>
                  </center>


                </div></div></div></div></div></div></div></div>


</form>


</body>

</html>
<script src='/partner/stylenew/BeforeAppManager.js'></script>