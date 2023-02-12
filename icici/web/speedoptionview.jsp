<%@ page import = "com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.enums.ApplicationManagerTypes" %>
<%@ page import="com.enums.ApplicationStatus" %>
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
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="index.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","speedoption");

%>
<script src='/merchant/stylenew01/BeforeAppManager.js'></script>
<%!
  private Functions functions = new Functions();
  private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
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

  /*ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  System.out.println("language_property1 -----"+language_property1);
  rb1 = LoadProperties.getProperty(language_property1);*/

  /*String speedoptionview_CompanyInformation   = !functions.isEmptyOrNull(rb1.getString("speedoptionview_CompanyInformation"))?rb1.getString("speedoptionview_CompanyInformation"): "Company Information";
  String speedoptionview_legal                = !functions.isEmptyOrNull(rb1.getString("speedoptionview_legal"))?rb1.getString("speedoptionview_legal"): "Legal Name & Form*";
  String speedoptionview_trading              = !functions.isEmptyOrNull(rb1.getString("speedoptionview_trading"))?rb1.getString("speedoptionview_trading"): "Trading As(DBA)*";
  String speedoptionview_RegistrationNumber   = !functions.isEmptyOrNull(rb1.getString("speedoptionview_RegistrationNumber"))?rb1.getString("speedoptionview_RegistrationNumber"): "Registration Number*";
  String speedoptionview_date                 = !functions.isEmptyOrNull(rb1.getString("speedoptionview_date"))?rb1.getString("speedoptionview_date"): "Date of Registration*";
  String speedoptionview_Legal                = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Legal"))?rb1.getString("speedoptionview_Legal"): "Legal Representative";
  String speedoptionview_Registration_Address = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Registration_Address"))?rb1.getString("speedoptionview_Registration_Address"): "Registration Address(no p/o)*";
  String speedoptionview_VAT_Number           = !functions.isEmptyOrNull(rb1.getString("speedoptionview_VAT_Number"))?rb1.getString("speedoptionview_VAT_Number"): "VAT Number";
  String speedoptionview_Zip_Code             = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Zip_Code"))?rb1.getString("speedoptionview_Zip_Code"): "Zip Code";
  String speedoptionview_Country              = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Country"))?rb1.getString("speedoptionview_Country"): "Country*";
  String speedoptionview_Phonecc              = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Phonecc"))?rb1.getString("speedoptionview_Phonecc"): "Phone CC*";
  String speedoptionview_PhoneNo              = !functions.isEmptyOrNull(rb1.getString("speedoptionview_PhoneNo"))?rb1.getString("speedoptionview_PhoneNo"): "Phone No*";
  String speedoptionview_Email_Address        = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Email_Address"))?rb1.getString("speedoptionview_Email_Address"): "Email Address*";
  String speedoptionview_Website              = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Website"))?rb1.getString("speedoptionview_Website"): "Website URL(S)*";
  String speedoptionview_tic                  = !functions.isEmptyOrNull(rb1.getString("speedoptionview_tic"))?rb1.getString("speedoptionview_tic"): "TIC / TIN Number / Federal Tax ID / PAN";
  String speedoptionview_Type_of_Business     = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Type_of_Business"))?rb1.getString("speedoptionview_Type_of_Business"): "Type of Business :-";
  String speedoptionview_Corporation          = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Corporation"))?rb1.getString("speedoptionview_Corporation"): "Corporation";
  String speedoptionview_limited              = !functions.isEmptyOrNull(rb1.getString("speedoptionview_limited"))?rb1.getString("speedoptionview_limited"): "Limited Liability Company";
  String speedoptionview_Sole                 = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Sole"))?rb1.getString("speedoptionview_Sole"): "Sole";
  String speedoptionview_Proprietor           = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Proprietor"))?rb1.getString("speedoptionview_Proprietor"): "Proprietor";
  String speedoptionview_Partnership          = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Partnership"))?rb1.getString("speedoptionview_Partnership"): "Partnership";
  String speedoptionview_Nonprofit            = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Nonprofit"))?rb1.getString("speedoptionview_Nonprofit"): "Nonprofit Organization";
  String speedoptionview_Bank_Information     = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Bank_Information"))?rb1.getString("speedoptionview_Bank_Information"): "Bank Information";
  String speedoptionview_Bank_Name            = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Bank_Name"))?rb1.getString("speedoptionview_Bank_Name"): "Bank Name";
  String speedoptionview_Bank_Account_Number  = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Bank_Account_Number"))?rb1.getString("speedoptionview_Bank_Account_Number"): "Bank Account Number";
  String speedoptionview_Bank_IBAN            = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Bank_IBAN"))?rb1.getString("speedoptionview_Bank_IBAN"): " Bank IBAN";
  String speedoptionview_swift                = !functions.isEmptyOrNull(rb1.getString("speedoptionview_swift"))?rb1.getString("speedoptionview_swift"): "SWIFT / IFSC / BIC (Bank Identifier Code)";
  String speedoptionview_Account_Holder       = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Account_Holder"))?rb1.getString("speedoptionview_Account_Holder"): "Account Holder";
  String speedoptionview_currency             = !functions.isEmptyOrNull(rb1.getString("speedoptionview_currency"))?rb1.getString("speedoptionview_currency"): "In which currency the products are sold?";
  String speedoptionview_Save                 = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Save"))?rb1.getString("speedoptionview_Save"): "Save";
  String speedoptionview_Submit               = !functions.isEmptyOrNull(rb1.getString("speedoptionview_Submit"))?rb1.getString("speedoptionview_Submit"): "Submit";*/

  String speedoptionview_CompanyInformation   = "Company Information";
  String speedoptionview_legal                = "Legal Name & Form*";
  String speedoptionview_trading              = "Trading As(DBA)*";
  String speedoptionview_RegistrationNumber   = "Registration Number*";
  String speedoptionview_date                 = "Date of Registration*";
  String speedoptionview_Legal                = "Legal Representative";
  String speedoptionview_Registration_Address = "Registration Address(no p/o)*";
  String speedoptionview_VAT_Number           = "VAT Number";
  String speedoptionview_Zip_Code             = "Zip Code";
  String speedoptionview_Country              = "Country*";
  String speedoptionview_Phonecc              = "Phone CC*";
  String speedoptionview_PhoneNo              = "Phone No*";
  String speedoptionview_Email_Address        = "Email Address*";
  String speedoptionview_Website              = "Website URL(S)*";
  String speedoptionview_tic                  = "TIC / TIN Number / Federal Tax ID / PAN";
  String speedoptionview_Type_of_Business     = "Type of Business :-";
  String speedoptionview_Corporation          = "Corporation";
  String speedoptionview_limited              = "Limited Liability Company";
  String speedoptionview_Sole                 = "Sole";
  String speedoptionview_Proprietor           = "Proprietor";
  String speedoptionview_Partnership          = "Partnership";
  String speedoptionview_Nonprofit            = "Nonprofit Organization";
  String speedoptionview_Bank_Information     = "Bank Information";
  String speedoptionview_Bank_Name            = "Bank Name";
  String speedoptionview_Bank_Account_Number  = "Bank Account Number";
  String speedoptionview_Bank_IBAN            = "Bank IBAN";
  String speedoptionview_swift                = "SWIFT / IFSC / BIC (Bank Identifier Code)";
  String speedoptionview_Account_Holder       = "Account Holder";
  String speedoptionview_currency             = "In which currency the products are sold?";
  String speedoptionview_Save                 = "Save";
  String speedoptionview_Submit               = "Submit";
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

  <title>Pre-vet Form</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <%--<link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap.min.css">--%>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
  <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
  <script src="/merchant/NewCss/am_multipleselection/jquery-multipleselection.js"></script>
  <script src="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
  <link href="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
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
+      display: block;
    }

  </style>


  <%--New  css--%>
  <%--<link rel="icon" type="image/x-icon" href="/asset/img/favicon.ico">--%>
  <%--<link rel="stylesheet" href="//cdn.jsdelivr.net/bootstrap/3.3.2/css/bootstrap.min.css">--%>
  <%--<link rel="stylesheet" href="//cdn.jsdelivr.net/fontawesome/4.2.0/css/font-awesome.min.css">
  <link rel="stylesheet" href="/vendor/formvalidation/css/formValidation.min.css">
  <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:300,400,700">
  <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Lato:300,400,700">
  <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Cardo:400,400italic,700">
  <link rel="stylesheet" href="/vendor/pygments/github.css">
--%>
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
<form action="/icici/servlet/SpeedOption?ctoken=<%=ctoken%>" method="post" name="myformname" id="myformname">
  <div class="container-fluid ">
    <div class="row" style="margin-top: 100px;background-color: #ffffff">
      <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">


        <div class="form foreground bodypanelfont_color panelbody_color">

          <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%>"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Pre-vet form after entering the data provided"%></h2></center>
          <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7"><%=speedoptionview_CompanyInformation%></h2>

          <div class="form-group col-md-4 has-feedback">
            <label for="merchantname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_legal%></label>
            <input type="text"  class="form-control"  id="merchantname" name="merchantname" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name():""%>" <%=actionVO.isView()?"disabled":""%> <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("merchantname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_trading%></label>
            <input type="text" class="form-control"   id="corporatename" name="corporatename"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name():""%>" <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="companyregistrationnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_RegistrationNumber%></label>
            <input type="text" class="form-control"  id="companyregistrationnumber" name="companyregistrationnumber"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number():""%>" <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("companyregistrationnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_date%></label>
            <input type="text" name="Company_Date_Registration" class="form-control datepicker" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration())==true?commonFunctionUtil.convertTimestampToDatepicker(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration()):""%>"  <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("Company_Date_Registration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="contactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Legal%></label>
            <input type="text" class="form-control"  id="contactname" name="contactname"style="border: 1px solid #b2b2b2;font-weight:bold"value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName())==true?companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName():""%>" <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("contactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="locationaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Registration_Address%></label>
            <input type="text" class="form-control"  id="locationaddress" name="locationaddress"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress())==true?StringEscapeUtils.escapeHtml(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress()):""%>" <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("locationaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4">
            <label for="vatidentification" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_VAT_Number%></label>
            <input type="text" class="form-control"  id="vatidentification" name="vatidentification" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification():""%>" <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("vatidentification")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="merchantzipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Zip_Code%></label>
            <input type="text" class="form-control"  id="merchantzipcode" name="merchantzipcode" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode():""%>" <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("merchantzipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="merchantcountry" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Country%></label>
            <select  class="form-control" id="merchantcountry" name="merchantcountry"  <%=actionVO.isView()?"disabled":""%> style="border: 1px solid #b2b2b2;font-weight:bold"  onchange="mycountrycode('merchantcountry','Companyphonecc1');">
              <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry():"")%>
            </select><%if(validationErrorList.getError("merchantcountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>

          </div>
          <div class="form-group col-md-2 has-feedback">
            <label for="Companyphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Phonecc%></label>
            <input type="text" class="form-control"  id="Companyphonecc1" name="Companyphonecc1" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc():""%>" <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("Companyphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-2 has-feedback">
            <label for="CompanyTelephoneNO" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_PhoneNo%></label>
            <input type="text" class="form-control"   id="CompanyTelephoneNO" name="CompanyTelephoneNO" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number():""%>"  <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("CompanyTelephoneNO")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="CompanyEmailAddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Email_Address%></label>
            <input type="text" class="form-control"   id="CompanyEmailAddress" name="CompanyEmailAddress" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id():""%>"  <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("CompanyEmailAddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="urls" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Website%></label>
            <input type="text" class="form-control"  id="urls" name="urls" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(businessProfileVO.getUrls())==true?businessProfileVO.getUrls():""%>" <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("urls")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>

          <div class="form-group col-md-4 has-feedback">
            <label for="FederalTaxID" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_tic%></label>
            <input type="text" class="form-control"  id="FederalTaxID" name="FederalTaxID" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId())==true?companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId():""%>"  <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("FederalTaxID")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>

          <div class="form-group col-md-12">
            <label for="FederalTaxID" style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=speedoptionview_Type_of_Business%></u></label>
          </div>
          <div class="form-group col-md-2 has-feedback">
            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
              <input type="radio"   name="company_typeofbusiness" style="width:30%;"  value="Corporation" <%="Corporation".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
              <%=speedoptionview_Corporation%></label>
          </div>
          <div class="form-group col-md-3">
            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
              <input type="radio"  name="company_typeofbusiness" style="width:20%;"  value="LimitedLiabilityCompany" <%="LimitedLiabilityCompany".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
              <%=speedoptionview_limited%></label>
          </div>
          <div class="form-group col-md-2 has-feedback">
            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;">
              <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="SoleProprietor"<%="SoleProprietor".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
              <%=speedoptionview_Sole%>&nbsp<%=speedoptionview_Proprietor%></label>
          </div>
          <div class="form-group col-md-2 has-feedback">
            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
              <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="Partnership" <%="Partnership".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
              <%=speedoptionview_Partnership%></label>
          </div>
          <div class="form-group col-md-3">
            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
              <input type="radio" name="company_typeofbusiness" style="width:30%;"  value="NotforProfit" <%="NotforProfit".equals(companyProfileVO.getCompanyTypeOfBusiness()) || !functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ?"checked":""%>  />
              <%=speedoptionview_Nonprofit%></label>
          </div>

          <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7"><%=speedoptionview_Bank_Information%></h2>

          <div class="form-group col-md-4 has-feedback">
            <label for="bankinfo_bank_name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Bank_Name%></label>
            <input type="text"  class="form-control"  id="bankinfo_bank_name" name="bankinfo_bank_name" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bank_name())==true?bankProfileVO.getBankinfo_bank_name():""%>"  <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("bankinfo_bank_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>

          <div class="form-group col-md-4 has-feedback">
            <label for="bankinfo_accountnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Bank_Account_Number%></label>
            <input type="text"  class="form-control"  id="bankinfo_accountnumber" name="bankinfo_accountnumber" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountnumber())==true?bankProfileVO.getBankinfo_accountnumber():""%>"  <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("bankinfo_accountnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>

          <div class="form-group col-md-4 has-feedback">
            <label for="bankinfo_IBAN" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Bank_IBAN%></label>
            <input type="text"  class="form-control"  id="bankinfo_IBAN" name="bankinfo_IBAN" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(bankProfileVO.getBankinfo_IBAN())==true?bankProfileVO.getBankinfo_IBAN():""%>"  <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("bankinfo_IBAN")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>

          <div class="form-group col-md-4 has-feedback">
            <label for="bankinfo_bic" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_swift%></label>
            <input type="text" class="form-control"   id="bankinfo_bic" name="bankinfo_bic" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bic())==true?bankProfileVO.getBankinfo_bic():""%>" <%=actionVO.isView()?"disabled":""%> /><%if(validationErrorList.getError("bankinfo_bic")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>
          <div class="form-group col-md-4 has-feedback">
            <label for="bankinfo_accountholder" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_Account_Holder%></label>
            <input type="text" class="form-control" id="bankinfo_accountholder" name="bankinfo_accountholder" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountholder())==true?bankProfileVO.getBankinfo_accountholder():""%>" <%=actionVO.isView()?"disabled":""%>/><%if(validationErrorList.getError("bankinfo_accountholder")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
          </div>


          <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">CURRENCY REQUESTED</h2>


          <div class="form-group col-md-6 has-feedback">

            <label for="product_sold_currencies" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=speedoptionview_currency%></label>
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
            <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;" >In which currency are your products sold?</label>
          </div>

          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">USD</label>
            <input type="checkbox" class="form-control" name="currency_products_USD" style="width: 50%;" value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_USD())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">GBP</label>
            <input type="checkbox" class="form-control" id="currency_products_GBP" name="currency_products_GBP" style="width: 50%;" value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_GBP())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">EUR</label>
            <input type="checkbox" class="form-control"   id="currency_products_EUR" name="currency_products_EUR" style="width: 50%;" value="Y" <%=actionVO.isView() ? "disabled" : ""%>  <%="Y".equals(bankProfileVO.getCurrency_products_EUR())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">JPY</label>
            <input type="checkbox" class="form-control"   id="currency_products_JPY" name="currency_products_JPY" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_JPY())?"checked":""%> />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">PEN</label>
            <input type="checkbox" class="form-control"   id="currency_products_PEN" name="currency_products_PEN" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_PEN())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">HKD</label>
            <input type="checkbox" class="form-control"   id="currency_products_HKD" name="currency_products_HKD" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_HKD())?"checked":""%> />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">AUD</label>
            <input type="checkbox" class="form-control"   id="currency_products_AUD" name="currency_products_AUD" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_AUD())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">CAD</label>
            <input type="checkbox" class="form-control"   id="currency_products_CAD" name="currency_products_CAD" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_CAD())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">DKK</label>
            <input type="checkbox" class="form-control"   id="currency_products_DKK" name="currency_products_DKK" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_DKK())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">SEK</label>
            <input type="checkbox" class="form-control"   id="currency_products_SEK" name="currency_products_SEK" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_SEK())?"checked":""%>  />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">NOK</label>
            <input type="checkbox" class="form-control"   id="currency_products_NOK" name="currency_products_NOK" style="width: 50%;"  value="Y" <%=actionVO.isView()?"disabled":""%> <%="Y".equals(bankProfileVO.getCurrency_products_NOK())?"checked":""%> />
          </div>
          <div class="form-group col-md-1">
            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">INR</label>
            <input type="checkbox" class="form-control"   id="currency_products_INR" name="currency_products_INR" style="width: 50%;" value="Y" <%=actionVO.isView()?"disabled":""%>  <%="Y".equals(bankProfileVO.getCurrency_products_INR())?"checked":""%> />
          </div>--%>

        </div>
      </div>

      <div align="center" class="textb">
        <%
          if(!actionVO.isView())
          {
        %>
        <button class="btn btn-default" type="submit" name="action" value="Save"><%=speedoptionview_Save%></button>
        <button class="btn btn-default" type="submit" name="action" value="Submit"><%=speedoptionview_Submit%></button>
        <%
          }
        %>
      </div>

      <br>
      <br>
    </div>
  </div>


</form>



<div class="container-fluid ">
  <div class="row" style="background-color: #ffffff">
    <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">

      <div class="form foreground bodypanelfont_color panelbody_color">

        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7">Modified Status</h2>

        <form action="/icici/servlet/AppManagerStatus?ctoken=<%=ctoken%>" method="post">
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
            <button type="submit" class="buttonform" >
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
</body>
</html>
<script src='/merchant/stylenew01/BeforeAppManager.js'></script>
