<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.manager.enums.MerchantModuleEnum" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="java.io.File" %>
<%@ include file="ietest.jsp" %>
<%!
  private Functions functions = new Functions();
  public static String getStatus(String str)
  {
    if("Y".equals(str))
      return "Active";
    else if("N".equals(str))
      return "Inactive";
    else if("T".equals(str))
      return "Test";
    return str;
  }
%>
<%
  String uri = request.getRequestURI();

  String pageName = uri.substring(uri.lastIndexOf("/")+1);

  String buttonvalue=request.getParameter("submit");
  if(buttonvalue==null)
  {
    buttonvalue=(String)session.getAttribute("submit");
  }
  Enumeration<String> attributes = request.getSession().getAttributeNames();
  String attribute="";
  String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
  User user =  (User)session.getAttribute("ESAPIUserSessionKey");

  String partnerIcon = (String)session.getAttribute("icon");
  String DefaultTheme=(String)session.getAttribute("defaulttheme");
  String CurrentTheme=(String)session.getAttribute("currenttheme");

  MerchantModuleAccessVO merchantModuleAccessVO=(MerchantModuleAccessVO)session.getAttribute("MerchantModuleAccessVO");

  ResourceBundle rb = null;
  String themevalue="";
  String logoHeight="38";
  String logoWidth="140";

  if(functions.isEmptyOrNull(DefaultTheme) && functions.isEmptyOrNull(CurrentTheme))
  {
    rb = LoadProperties.getProperty("com.directi.pg.ColorTheme", "pz");
    themevalue=rb.getString("pz");
  }
  else if(functions.isEmptyOrNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
  {
    rb = LoadProperties.getProperty("com.directi.pg.ColorTheme",DefaultTheme);
    themevalue=rb.getString(DefaultTheme);
  }
  else if(functions.isValueNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
  {
    rb = LoadProperties.getProperty("com.directi.pg.ColorTheme",CurrentTheme);
    themevalue=rb.getString(CurrentTheme);
  }
  else if(functions.isValueNull(CurrentTheme) && functions.isEmptyOrNull(DefaultTheme))
  {
    rb = LoadProperties.getProperty("com.directi.pg.ColorTheme",CurrentTheme);
    themevalue=rb.getString(CurrentTheme);
  }
  session.setAttribute("colorPallet",themevalue);
  PartnerDAO partnerDAO = new PartnerDAO();
  String partnerid="";
  if(session.getAttribute("partnerid")!=null && !session.getAttribute("partnerid").equals(""))
  {
    partnerid = (String)session.getAttribute("partnerid");
  }
  PartnerDetailsVO partnerDetailsVOMain = null;
  if(functions.isValueNull(partnerid))
  {
    partnerDetailsVOMain = partnerDAO.geturl(partnerid);
  }

  if(partnerDetailsVOMain != null)
  {
    if(functions.isValueNull(partnerDetailsVOMain.getLogoHeight()))
    {
      logoHeight = partnerDetailsVOMain.getLogoHeight();
    }
    if(functions.isValueNull(partnerDetailsVOMain.getLogoWidth()))
    {
      logoWidth = partnerDetailsVOMain.getLogoWidth();
    }
  }

  ResourceBundle resource_Bundle = null;
  String language_property = (String)session.getAttribute("language_property");
  session.setAttribute("language_property",language_property);
  resource_Bundle = LoadProperties.getProperty(language_property);

  String Top_Merchant_Administration_Module = StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Administration_Module"))?resource_Bundle.getString("Top_Merchant_Administration_Module"): "Merchant Administration Module";
  String Top_Your_session_has_expired =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Your_session_has_expired"))?resource_Bundle.getString("Top_Your_session_has_expired"): "Your session has expired";
  String Top_Click=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Click"))?resource_Bundle.getString("Top_Click"): "Click";
  String Top_here =StringUtils.isNotEmpty(resource_Bundle.getString("Top_here"))?resource_Bundle.getString("Top_here"): "here";
  String Top_merchant_login_page=StringUtils.isNotEmpty(resource_Bundle.getString("Top_merchant_login_page"))?resource_Bundle.getString("Top_merchant_login_page"): "to go to the Merchant login page";
  String Top_English =StringUtils.isNotEmpty(resource_Bundle.getString("Top_English"))?resource_Bundle.getString("Top_English"): "English (US)";
  String Top_Merchant_Profile=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Profile"))?resource_Bundle.getString("Top_Merchant_Profile"): "Merchant Profile";
  String Top_Change_Password=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Change_Password"))?resource_Bundle.getString("Top_Change_Password"): "Change Password";
  String Top_Organisation_Profile =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Organisation_Profile"))?resource_Bundle.getString("Top_Organisation_Profile"): "Organisation Profile";
  String Top_Logout=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Logout"))?resource_Bundle.getString("Top_Logout"): "Logout";
  String Top_Merchant_ID=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_ID"))?resource_Bundle.getString("Top_Merchant_ID"): "Merchant ID";
  String Top_User =StringUtils.isNotEmpty(resource_Bundle.getString("Top_User"))?resource_Bundle.getString("Top_User"): "User";
  String Top_Status =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Status"))?resource_Bundle.getString("Top_Status"): "Status";
  String Top_DashBoard=StringUtils.isNotEmpty(resource_Bundle.getString("Top_DashBoard"))?resource_Bundle.getString("Top_DashBoard"): "DashBoard";
  String Top_Account_Details=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Account_Details"))?resource_Bundle.getString("Top_Account_Details"): "Account Details";
  String Top_Account_Summary=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Account_Summary"))?resource_Bundle.getString("Top_Account_Summary"): "Account Summary";
  String Top_Charges_Summary=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Charges_Summary"))?resource_Bundle.getString("Top_Charges_Summary"): "Charges Summary";
  String Top_Transaction_Summary=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Transaction_Summary"))?resource_Bundle.getString("Top_Transaction_Summary"): "Transaction Summary";
  String Top_Reports=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Reports"))?resource_Bundle.getString("Top_Reports"): "Reports";
  String Top_Settings=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Settings"))?resource_Bundle.getString("Top_Settings"): "Settings";
  String Top_Generate_Key=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Generate_Key"))?resource_Bundle.getString("Top_Generate_Key"): "Generate Key";
  String Top_Merchant_Configuration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Configuration"))?resource_Bundle.getString("Top_Merchant_Configuration"): "Merchant Configuration";
  String Top_Fraud_Rule_Configuration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Fraud_Rule_Configuration"))?resource_Bundle.getString("Top_Fraud_Rule_Configuration"): "Fraud Rule Configuration";
  String Top_Whitelist_Details=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Whitelist_Details"))?resource_Bundle.getString("Top_Whitelist_Details"): "Whitelist Details";
  String Top_Blacklist_Details=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Blacklist_Details"))?resource_Bundle.getString("Top_Blacklist_Details"): "Blacklist Details";
  String Top_Transaction_Management =StringUtils.isNotEmpty(resource_Bundle.getString("Top_Transaction_Management"))?resource_Bundle.getString("Top_Transaction_Management"): "Transaction Management";
  String Top_Transactions=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Transactions"))?resource_Bundle.getString("Top_Transactions"): "Transactions";
  String Top_Capture=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Capture"))?resource_Bundle.getString("Top_Capture"): "Capture";
  String Top_Reversal=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Reversal"))?resource_Bundle.getString("Top_Reversal"): "Reversal";
  String Top_Payout=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Payout"))?resource_Bundle.getString("Top_Payout"): "Payout";
  String Top_Rejected_Transaction=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Rejected_Transaction"))?resource_Bundle.getString("Top_Rejected_Transaction"): "Rejected Transaction";
  String Top_Invoice=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Invoice"))?resource_Bundle.getString("Top_Invoice"): "Invoice";
  String Top_Generate_Invoice=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Generate_Invoice"))?resource_Bundle.getString("Top_Generate_Invoice"): "Generate Invoice";
  String Top_Invoice_History=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Invoice_History"))?resource_Bundle.getString("Top_Invoice_History"): "Invoice History";
  String Top_Invoice_Configuration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Invoice_Configuration"))?resource_Bundle.getString("Top_Invoice_Configuration"): "Invoice Configuration";
  String Top_Token_Management=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Token_Management"))?resource_Bundle.getString("Top_Token_Management"): "Token Management";
  String Top_Registration_History=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Registration_History"))?resource_Bundle.getString("Top_Registration_History"): "Registration History";
  String Top_Card_Registration=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Card_Registration"))?resource_Bundle.getString("Top_Card_Registration"): "Card Registration";
  String Top_Register_Card=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Register_Card"))?resource_Bundle.getString("Top_Register_Card"): "Register Card";
  String Top_Virtual_Terminal=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Virtual_Terminal"))?resource_Bundle.getString("Top_Virtual_Terminal"): "Virtual Terminal";
  String Top_Terminal=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Terminal"))?resource_Bundle.getString("Top_Terminal"): "Terminal";
  String Top_Virtual_Checkout=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Virtual_Checkout"))?resource_Bundle.getString("Top_Virtual_Checkout"): "Virtual Checkout";
  String Top_Merchant_Management=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Management"))?resource_Bundle.getString("Top_Merchant_Management"): "Merchant Management";
  String Top_User_Management=StringUtils.isNotEmpty(resource_Bundle.getString("Top_User_Management"))?resource_Bundle.getString("Top_User_Management"): "User Management";
  String Top_Merchant_Application=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Merchant_Application"))?resource_Bundle.getString("Top_Merchant_Application"): "Merchant Application";
  String Top_Recurring_Module=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Recurring_Module"))?resource_Bundle.getString("Top_Recurring_Module"): "Recurring Module";
  String Top_Active=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Active"))?resource_Bundle.getString("Top_Active"): "Active";
  String Top_Inactive=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Inactive"))?resource_Bundle.getString("Top_Inactive"): "Inactive";
  String Top_Test=StringUtils.isNotEmpty(resource_Bundle.getString("Top_Test"))?resource_Bundle.getString("Top_Test"): "Test";
  String Top_PayoutTransaction=StringUtils.isNotEmpty(resource_Bundle.getString("Top_PayoutTransaction"))?resource_Bundle.getString("Top_PayoutTransaction"):"Payout Transactions";
  String Checkout_Config=StringUtils.isNotEmpty(resource_Bundle.getString("Checkout_Config"))?resource_Bundle.getString("Checkout_Config"):"Checkout Config";

  String ctoken= null;
  if(user!=null)
  {
    ctoken = user.getCSRFToken();
  }
  String copyiframe=(String)session.getAttribute("fileName");
  Merchants merchants = new Merchants();
  String logo=(String)session.getAttribute("logo");
  MerchantDAO merchantDAO=new MerchantDAO();
  MerchantDetailsVO merchantDetailsVO= merchantDAO.getMemberDetails((String)session.getAttribute("merchantid"));
  String isMerchantLogo=merchantDetailsVO.getIsMerchantLogoBO();
  Hashtable hashtable =merchantDAO.getMemberTemplateDetails((String) session.getAttribute("merchantid"));
  String isMerchantLogoName=(String)hashtable.get((String) session.getAttribute("merchantid"));
  if(functions.isValueNull(isMerchantLogo) && isMerchantLogo.equals("Y") && functions.isValueNull(isMerchantLogoName))
  {
    logo = isMerchantLogoName;
  }
  String otpMessage = "";
  if(request.getAttribute("OtpMessage") != null)
  {
    otpMessage = (String) request.getAttribute("OtpMessage");
  }
%>
<html lang="en" class="body-full-height">
<head>
  <title><%=session.getAttribute("company")%> Merchant Login</title>

  <!-- META SECTION -->
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />

  <link rel="icon" href="/images/merchant/<%=partnerIcon%>" type="image/x-icon" />
  <!-- END META SECTION -->
  <!-- CSS INCLUDE -->
  <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap-4.4.1/js/bootstrap.min.js" ></script>
  <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
  <script src="/merchant/javascript/hidde.js"></script>
  <link rel="stylesheet" type="text/css" id="theme" href="/merchant/NewCss/theme-default.css"/>
  <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/bootstrap.min.css"/>
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
  <%-- <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fontawesome/font-awesome.min.css"/>
   <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.eot"/>
   <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.svg"/>
   <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/fontawesome-webfont.woff"<%--<link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.eot"/>
   <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.svg"/>
   <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular"/>
   <link rel="stylesheet" type="text/css"  href="/merchant/NewCss/fonts/glyphicons-halflings-regular.woff"/>--%>
  <!-- EOF CSS INCLUDE -->

  <%--<script type="text/javascript" src="/merchant/javascript/jquery-1.7.1.js?ver=1"></script>--%>

  <style type="text/css">
    .roundedOtp
    {
      width: 50px;
      border-radius: 10px !important;
      display: inline-block;
    }
    }
    .field-icon
    {
      float: right;
      margin-top: -23px;
      position: relative;
      z-index: 2;
      margin-right: 3px;
    }
    /*Header Styling*/
    .main-topheader {
      position: fixed;
      top: 0;
      padding-top: 15px;
      width: 100%;
      z-index: 9;
      transition: all 0.3s ease;
      color: #fff;
      height:70px;
    }

    .main-topheader.colorize {
      color: #8e8e8e;
      background-color: transparent;
    }


    .main-topheader.fixed .top-nav a {
      color: inherit
    }

    .main-topheader.fixed .top-nav li:last-child .right-arr #right-arr {
      fill: #8e8e8e
    }

    .main-topheader.fixed .main-logo {
      color: #43c6ac
    }

    .main-topheader .main-logo {
      color: inherit
    }

    @-webkit-keyframes shine {
      from {
        -webkit-mask-position: 150%
      }
      to {
        -webkit-mask-position: -50%
      }
    }

    .main-topheader .top-nav {
      float: right;
      margin-bottom: 0
    }

    .main-topheader .top-nav li {
      padding-left: 11px;
      padding-right: 11px;
      vertical-align: middle
    }

    .NewPlatformLabel
    {
      display: inline;
    }

    @media (max-width: 991px) {
      .main-topheader .top-nav li {
        padding-right: 8px;
        padding-left: 8px
      }
    }

    @media (max-width: 767px) {
      .main-topheader {
        padding-top: 15px
      }

      .main-topheader.colorize .top-nav .ghost-btn {
        -webkit-box-shadow: none;
        box-shadow: none;
        background-color: transparent
      }

      .main-topheader.colorize .top-nav .ghost-btn.filled {
        background-color: transparent;
        border-color: transparent
      }
      .main-topheader.fixed .top-nav li:nth-last-child(2) {
        display: none
      }
      .main-topheader.fixed .top-nav li:nth-last-child(3) {
        display: block
      }
      .main-logo {
        position: absolute;
        left: 50%;
        top: 50%;
        margin-top: -14px;
        margin-left: -60px
      }
      .main-logo img {
        width: 140px !important;
      }
      .NewPlatformLabel
      {
        display: none;
      }
    }
    .login-body {

      margin-top: 55px;
    }


    .header-scrolled {
      background-color: #fff;
    }




  </style>
  <%--<script type="text/javascript">
      $(document).ready(function() {

          document.getElementById('submit').disabled =  false;
          $("#submit").click(function() {
              var encryptedString =  $.jCryption.encrypt($("#password").val(), $("#ctoken").val());
              document.getElementById('password').value =  encryptedString;
              document.getElementById('isEncrypted').value =  true;
          });

      });
  </script>--%>



    <link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
  <%
    if(functions.isEmptyOrNull(DefaultTheme) && functions.isEmptyOrNull(CurrentTheme))
    {
  %>
  <link href="/merchant/NewCss/css/pz.css" rel="stylesheet" type="text/css" />
  <%
    }
    else if(functions.isEmptyOrNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
    {
  %>
  <link href="/merchant/NewCss/css/<%=DefaultTheme%>.css" rel="stylesheet" type="text/css" />
  <%
    }
    else if(functions.isValueNull(CurrentTheme) && functions.isValueNull(DefaultTheme))
    {
  %>
  <link href="/merchant/NewCss/css/<%=CurrentTheme%>.css" rel="stylesheet" type="text/css" />
  <%
    }
    else if(functions.isValueNull(CurrentTheme) && functions.isEmptyOrNull(DefaultTheme))
    {
  %>
  <link href="/merchant/NewCss/css/<%=CurrentTheme%>.css" rel="stylesheet" type="text/css" />
  <%
    }
  %>


</head>
<%
  String redirectPage = "/servlet/DashBoard";
  Calendar cal = Calendar.getInstance();
  cal.add(Calendar.DAY_OF_MONTH, -91);
  if (user.getLastPasswordChangeTime().compareTo(cal.getTime()) == -1)
  {
    redirectPage = "/chngpwd.jsp";
  }

%>

<body>
<div class="login-container lightmode" id="login_new">
  <div class="widget-header">
    <div class="main-topheader colorize">
      <div class="container">
        <div class="row">
          <div class="col-md-12 clearfix">
            <%--<a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/<%=logo%>" style="width: 140px;height: 38px;object-fit: contain;"></a>--%>
            <%
              if((functions.isEmptyOrNull(CurrentTheme) && functions.isValueNull(DefaultTheme)) && (CurrentTheme.equalsIgnoreCase("indiaProcessing") || DefaultTheme.equalsIgnoreCase("indiaProcessing")))
              {
            %>
            <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img class="custom" src="/merchant/images/pay1.png" style="width: 140px;height: 38px;object-fit: contain;"></a>
            <%}else{
            %>
            <a href="#" title="<%=session.getAttribute("company")%>" class="main-logo" ><img src="/images/merchant/<%=logo%>" style="width: <%=logoWidth%>px;height: <%=logoHeight%>px;object-fit: contain;"></a>
            <%}%>


          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="login-box animated fadeInDown">

    <form action="/merchant<%=redirectPage%>?MES=UP&ctoken=<%=ctoken%>" method="post"  name="otpVerify" id="otpVerify" class="form-horizontal">
                    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
                    <input id="verifyAction" name="verifyAction" type="hidden" value="" >


                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <div class="login-body"  id="CancelModalCreditcard">
                      <input id="partId"  type="hidden" value="<%=request.getAttribute("partnerid")%>" >
                      <input id="userName"  type="hidden" value="<%=request.getAttribute("membername")%>" >
                      <input id="phonenophonecc"  type="hidden" value="<%=request.getAttribute("mobileno")%>" >
                      <input id="clkey"  type="hidden" value="<%=request.getAttribute("securekey")%>" >
                      <input id="memberId"  type="hidden" value="<%=request.getAttribute("memberid")%>" >
                      <input id="memberTransactionId"  type="hidden" value="<%=request.getAttribute("memberTransactionId")%>" >
                      <input id="email"  type="hidden" value="<%=request.getAttribute("memberid")%>" >

                      <div class="header p-3 w-100 h-auto">
                        <h5 class="login-title" style="font-weight: 600;text-align: center;">OTP VERIFICATION</h5>
                      </div>

                      <div  style="max-width: 385px !important; top:2px;">
                        <div >
                          <div <%--class="container d-flex justify-content-center align-items-center"--%> >

                            <div class="card text-center" style="width: 370px;">

                              <p id="mainTimer"  style="margin-top: 10px;margin-left: 200px;white-space: nowrap;"></p>
                              <div>
                                <span  id="messageOTP"  style="color:darkblue;font-size: 15px;"><%=otpMessage%></span>
                                <input type="hidden" id="SuccessOTP" value="">
                              </div>
                              <br>
                              <label  class="mt-2 font-weight-bold" style="font-family: system-ui;font-size: 16px;text-align: center;">Verify Mobile OTP</label>

                              <!-- OTP Number -->
                              <div class="form-group" id="mobileIdVerify">
                                <div id="otpM" class="col-md-12 input-container d-flex flex-row justify-content-center">
                                  <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)" id="fisrtM" class="m-2 text-center form-control roundedOtp" maxlength="1">
                                  <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="secondM"  class="m-2 text-center form-control roundedOtp" maxlength="1">
                                  <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"   id="thirdM" class="m-2 text-center form-control roundedOtp" maxlength="1">
                                  <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fourthM"  class="m-2 text-center form-control roundedOtp" maxlength="1">
                                  <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fifthM"  class="m-2 text-center form-control roundedOtp" maxlength="1">
                                  <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"   id="sixthM" class="m-2 text-center form-control roundedOtp" maxlength="1">
                                </div>
                                <br>
                                <br>
                                <br>
                                <p   class="mt-2" style="font-size:17px;font-family: sans-serif;">Please enter the OTP received via Mobile at registered Mobile number. <b><span style="font-weight: 600;" id="copyOTPPhoneNumber"></span></b></p>
                              </div>


                              <div>
                                <big>
                                  didn't get the otp?
                                  <p id="regenerateOTPText" style="color: blue;font-size: 19px;cursor: pointer;" onclick="regenerateOTP1();" class="text-decoration-none">Resend</p>
                                </big>
                              </div>

                              <!-- <h6 class="m-4 font-weight-bold" style="font-family: sans-serif;">Please enter the OTP received via Email at registered email id.</h6> -->

                              <div class="mt-3 mb-5">
                                <button type="button" id="submit1" onclick="verifyOTP1()" style="width: 151px;font-size: 23px;" class="btn btn-default">Validate</button>
<%--
--%>
                              </div>

                            </div>

                          </div>
                        </div>
                      </div>

                      <div id="response"></div>
                    </div>

                  </form>
                </div>
                </div>


<script type="text/javascript" src="/merchant/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/merchant/cookies/cookies_popup.js"></script>
<link href="/merchant/cookies/quicksand_font.css" rel="stylesheet">
<link href="/merchant/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<script>
  //Copy OTP
  document.addEventListener("DOMContentLoaded", function(event) {
    timer1(60);
    function pasteOTP(event){
      event.preventDefault();
      var elm = event.target;
      var pasteVal = event.clipboardData.getData('text').split("");
      if(pasteVal.length > 0){
        while(elm){
          elm.value = pasteVal.shift();
          elm = elm.nextSibling.nextSibling;
        }
      }
    }
    function OTPInput() {
      const editor = document.getElementById('fisrtM');
      editor.onpaste = pasteOTP;

      const inputs = document.querySelectorAll('#otpM > *[id]');
      for (var i = 0; i < inputs.length; i++) {
        inputs[i].addEventListener('input', function(event) {
          if(!event.target.value || event.target.value == '' ){
            if(event.target.previousSibling.previousSibling){
              event.target.previousSibling.previousSibling.focus();
            }

          }else{
            if(event.target.nextSibling.nextSibling){
              event.target.nextSibling.nextSibling.focus();
            }
          }
        });
      }
    }
    function OTPInput2() {
      const editor = document.getElementById('fisrtE');
      editor.onpaste = pasteOTP;

      const inputs = document.querySelectorAll('#otpE > *[id]');
      for (var i = 0; i < inputs.length; i++) {
        inputs[i].addEventListener('input', function(event) {
          if(!event.target.value || event.target.value == '' ){
            if(event.target.previousSibling.previousSibling){
              event.target.previousSibling.previousSibling.focus();
            }

          }else{
            if(event.target.nextSibling.nextSibling){
              event.target.nextSibling.nextSibling.focus();
            }
          }
        });
      }
    }
    OTPInput();
    OTPInput2();
  });
</script>