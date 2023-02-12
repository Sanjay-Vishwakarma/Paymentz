<%@ page import="com.directi.pg.Database,com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.codehaus.jettison.json.JSONObject" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 2/9/15
  Time: 1:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("manageGatewayAccount.jsp");
  Functions functions= new Functions();
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","gatewayAccountInterface");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String manageGatewayAccount_Add_New_Bank = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_Add_New_Bank")) ? rb1.getString("manageGatewayAccount_Add_New_Bank") : "Add New Bank Account";
  String manageGatewayAccount_bank_name = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_bank_name")) ? rb1.getString("manageGatewayAccount_bank_name") : "Bank Name* :";
  String manageGatewayAccount_select_bank = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_select_bank")) ? rb1.getString("manageGatewayAccount_select_bank") : "Select Bank";
  String manageGatewayAccount_mid = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_mid")) ? rb1.getString("manageGatewayAccount_mid") : "(MID)* :";
  String manageGatewayAccount_MerchantId = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_MerchantId")) ? rb1.getString("manageGatewayAccount_MerchantId") : "Merchant Id";
  String manageGatewayAccount_mcc = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_mcc")) ? rb1.getString("manageGatewayAccount_mcc") : "MCC/ Alias Name* :";
  String manageGatewayAccount_billing = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_billing")) ? rb1.getString("manageGatewayAccount_billing") : "Billing Descriptor/ Display Name* :";
  String manageGatewayAccount_master = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_master")) ? rb1.getString("manageGatewayAccount_master") : "Is MasterCardSupported :";
  String manageGatewayAccount_Disable = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_Disable")) ? rb1.getString("manageGatewayAccount_Disable") : "Disable";
  String manageGatewayAccount_Enable = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_Enable")) ? rb1.getString("manageGatewayAccount_Enable") : "Enable";
  String manageGatewayAccount_username = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_username")) ? rb1.getString("manageGatewayAccount_username") : "Username/ Key";
  String manageGatewayAccount_username1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_username1")) ? rb1.getString("manageGatewayAccount_username1") : ":";
  String manageGatewayAccount_password = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_password")) ? rb1.getString("manageGatewayAccount_password") : "Password";
  String manageGatewayAccount_password1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_password1")) ? rb1.getString("manageGatewayAccount_password1") : ":";
  String manageGatewayAccount_shortname = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_shortname")) ? rb1.getString("manageGatewayAccount_shortname") : "Short Name";
  String manageGatewayAccount_shortname1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_shortname1")) ? rb1.getString("manageGatewayAccount_shortname1") : ":";
  String manageGatewayAccount_Site = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_Site")) ? rb1.getString("manageGatewayAccount_Site") : "Site";
  String manageGatewayAccount_Site1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_Site1")) ? rb1.getString("manageGatewayAccount_Site1") : ":";
  String manageGatewayAccount_path = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_path")) ? rb1.getString("manageGatewayAccount_path") : "Path";
  String manageGatewayAccount_path1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_path1")) ? rb1.getString("manageGatewayAccount_path1") : ":";
  String manageGatewayAccount_chargeback = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_chargeback")) ? rb1.getString("manageGatewayAccount_chargeback") : "ChargeBack Path";
  String manageGatewayAccount_chargeback1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_chargeback1")) ? rb1.getString("manageGatewayAccount_chargeback1") : ":";
  String manageGatewayAccount_card = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_card")) ? rb1.getString("manageGatewayAccount_card") : "Card Limit check :";
  String manageGatewayAccount_account = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_account")) ? rb1.getString("manageGatewayAccount_account") : "Account Level";
  String manageGatewayAccount_mid_level = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_mid_level")) ? rb1.getString("manageGatewayAccount_mid_level") : "MID Level";
  String manageGatewayAccount_limit = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_limit")) ? rb1.getString("manageGatewayAccount_limit") : "Card Amount Limit check :";
  String manageGatewayAccount_limit_check = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_limit_check")) ? rb1.getString("manageGatewayAccount_limit_check") : "Amount Limit check :";
  String manageGatewayAccount_limit_daily = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_limit_daily")) ? rb1.getString("manageGatewayAccount_limit_daily") : "Daily Card Limit :";
  String manageGatewayAccount_limit_weekly = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_limit_weekly")) ? rb1.getString("manageGatewayAccount_limit_weekly") : "Weekly Card Limit :";
  String manageGatewayAccount_limit_monthly = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_limit_monthly")) ? rb1.getString("manageGatewayAccount_limit_monthly") : "Monthly Card Limit :";
  String manageGatewayAccount_limit_amount = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_limit_amount")) ? rb1.getString("manageGatewayAccount_limit_amount") : "Daily Amount Limit :";
  String manageGatewayAccount_amount_weekly = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_amount_weekly")) ? rb1.getString("manageGatewayAccount_amount_weekly") : "Weekly amount Limit :";
  String manageGatewayAccount_amount_monthly = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_amount_monthly")) ? rb1.getString("manageGatewayAccount_amount_monthly") : "Monthly amount Limit :";
  String manageGatewayAccount_amount_daily = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_amount_daily")) ? rb1.getString("manageGatewayAccount_amount_daily") : "Daily Card Amount Limit :";
  String manageGatewayAccount_amount_weekly1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_amount_weekly1")) ? rb1.getString("manageGatewayAccount_amount_weekly1") : "Daily Card Amount Limit :";
  String manageGatewayAccount_amount_monthly1 = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_amount_monthly1")) ? rb1.getString("manageGatewayAccount_amount_monthly1") : "Monthly Card Amount Limit :";
  String manageGatewayAccount_supported = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_supported")) ? rb1.getString("manageGatewayAccount_supported") : "is 3D Supported :";
  String manageGatewayAccount_only = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_only")) ? rb1.getString("manageGatewayAccount_only") : "Only 3D";
  String manageGatewayAccount_test = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_test")) ? rb1.getString("manageGatewayAccount_test") : "is Test Account :";
  String manageGatewayAccount_active = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_active")) ? rb1.getString("manageGatewayAccount_active") : "is Active Account :";
  String manageGatewayAccount_multiple = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_multiple")) ? rb1.getString("manageGatewayAccount_multiple") : "is Multiple Refund :";
  String manageGatewayAccount_partial = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_partial")) ? rb1.getString("manageGatewayAccount_partial") : "is Partial Refund :";
  String manageGatewayAccount_recuring = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_recuring")) ? rb1.getString("manageGatewayAccount_recuring") : "Is Recurring Allowed :";
  String manageGatewayAccount_address = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_address")) ? rb1.getString("manageGatewayAccount_address") : "Address Validation :";
  String manageGatewayAccount_emi = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_emi")) ? rb1.getString("manageGatewayAccount_emi") : "Emi Support :";
  String manageGatewayAccount_fx = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_fx")) ? rb1.getString("manageGatewayAccount_fx") : "MOTO :";
  String manageGatewayAccount_dynamic = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_dynamic")) ? rb1.getString("manageGatewayAccount_dynamic") : "Is Dynamic Descriptor :";
  String manageGatewayAccount_add_bank = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_add_bank")) ? rb1.getString("manageGatewayAccount_add_bank") : "Add Bank Account";
  String manageGatewayAccount_3Ds_version = StringUtils.isNotEmpty(rb1.getString("manageGatewayAccount_3Ds_version")) ? rb1.getString("manageGatewayAccount_3Ds_version") : "3Ds Version";
%>
<html>
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<head>
  <title><%=company%> | Add Bank Account</title>

  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
  <link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css">
  <style type="text/css">
    #maintitle{
      text-align: center;
      background: #7eccad;
      color: #fff;
      font-size: 14px;
    }

    @media(min-width: 640px){
      #saveid{
        position: absolute;
        background: #F9F9F9!important;
      }

      #savetable{
        padding-bottom: 25px;
      }

      table.table{
        margin-bottom: 6px !important;
      }

      table#savetable td:before{
        font-size: inherit;
      }
    }

    table#savetable td:before{
      font-size: 13px;
      font-family: Open Sans;
    }

    table.table{
      margin-bottom: 0px !important;
    }

    #saveid input{
      font-size: 16px;
      padding-right: 30px;
      padding-left: 30px;
    }


    .multiselect {
      width: 100%;

    }

    .selectBox {
      position: relative;
    }

    .selectBox select {
      width: 100%;
      font-weight: bold;
    }

    .overSelect {
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
    }

    #checkboxes {
      display: none;
      border: 1px #dadada solid;
      position: absolute;
      width: 100%;
      background-color: #ffffff;
      z-index: 1;
      height: 130px;
      overflow-x: auto;
    }

    #checkboxes label {
      display: block;
    }

    #checkboxes label:hover {
      background-color: #1e90ff;
    }


    #checkboxes_1 label {
      display: block;
    }

    #checkboxes_1 label:hover {
      background-color: #1e90ff;
    }


    #checkboxes_2 label {
      display: block;
    }

    #checkboxes_2 label:hover {
      background-color: #1e90ff;
    }

    input[type="checkbox"]{
      width: 18px; /*Desired width*/
      height: 18px; /*Desired height*/
    }

    /********************************************************************************/

    .multiselect-container>li {
      padding: 0;
      margin-left: 31px;
    }
    .open>#multiselect-id.dropdown-menu {
      display: block;
      padding-top: 5px;
      padding-bottom: 5px;
    }
    .multiselect-container>li>a>label {
      margin: 0;
      height: 28px;
      padding-left:1px; !important;
      text-align: left;
    }
    span.multiselect-native-select {
      position: relative;
    }

    @supports (-ms-ime-align:auto) {
      span.multiselect-native-select {
        position: static!important;
      }
    }

    select[multiple], select[size] {
      height: auto;
      border-color: rgb(169, 169, 169);
    }
    .widget .btn-group {
      z-index: 1;
    }
    .btn-group, .btn-group-vertical {
      position: relative;
      vertical-align: middle;
      border-radius: 4px;
      width:100%;
      height: 30px;
      background-color: #fff;
    }
    #mainbtn-id.btn-default {
      color: #333;
      background-color: #fff;
      padding: 6px;
      border: 1px solid #b2b2b2;
      height: 33px;
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
      height: 188px;
      overflow-y: scroll;
    }
    #multiselect-id.dropdown-menu {
      position: absolute;
      top: 100%;
      left: 0;
      z-index: 1000;
      display: none;
      float: left;
      min-width: 100%;
      font-size: 14px;
      font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
      list-style: none;
      background-color: #fff;
      border: 1px solid #ccc;
      border: 1px solid rgba(0,0,0,0.15);
      border-radius: 4px;
      -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      background-clip: padding-box;
    }
    #mainbtn-id .multiselect-selected-text{
      font-size: 12px;
      font: inherit;
      padding-right: 18px;
    }
    #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open>.dropdown-toggle#mainbtn-id.btn-default {
      color: #333;
      /*color: #fff;*/
      background-color: white!important;
      text-align: left;
      width: 100%;
    }
    .tr1 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text , .tr0 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text
    {
      font-size: 13px;
      font: inherit;
    }
    .tr1 .multiselect-native-select .btn-group #mainbtn-id , .tr0 .multiselect-native-select .btn-group #mainbtn-id
    {
      border: 1px solid #ddd; !important;
    }
    .btn .caret { /*fa fa-chevron-down*/
      position: absolute;
      display: inline-block;!important;
      width: 0px;!important;
      height: 1px;!important;
      margin-left: 2px;!important;
      vertical-align: middle;!important;
      border-top: 7px solid;!important;
      border-right: 4px solid transparent;!important;
      border-left: 4px solid transparent;!important;
      float: right;!important;
      margin-top: 5px;!important;
      box-sizing: inherit;
      right: 5px;
      top: 15px;
      margin-top: -2px;
    }
    .fa-chevron-down{
      position: absolute;
      right:0px;
      top: 0px;
      margin-top: -2px;
      vertical-align: middle;
      float: right;
      font-size: 9px;
    }
    #mainbtn-id
    {
      overflow: hidden;
      display: block;
    }
    .chkbox{
      border: 2px solid #bcc8c6;
      font-weight:bold;
      margin-right: -15px;
      margin-left: -15px;
      height: 34px;
    }
  </style>

  <script type="text/javascript">
    function gettabledata()
    {

      var select = document.getElementById("pgtypeid");
      var answer = select.options[select.selectedIndex].value;
      var bankid = document.getElementById("bankid").value;
      var temp = new Array();
      if(bankid != "")
      {
        temp = bankid.split(",");
      }
      temp.push(answer);
      if( temp.length <= 1 || answer == "")
      {
        document.addbankaccount.action = "/partner/manageGatewayAccount.jsp?pgtypeid="+ answer;
        document.addbankaccount.submit();
      }
    }

    function check()
    {
      var msg = "";
      var flag = false;
      if (document.getElementById("pgtypeid").value.length == 0)
      {
        msg = msg + "\nPlease Select Gateway Type.";
        flag = true;
        //document.getElementById("pgtypeid").focus();
      }

      if (flag == true)
      {
        alert(msg);
        return false;
      }
      else
      {
        document.addtype.submit();
        return true;
      }
    }

    $(function ()
    {
      $(document).ready(function ()
      {
        $(".caret").addClass('icon2');
        $('.multiselect-selected-text').addClass("filter-option pull-left");
        firefox = navigator.userAgent.search("Firefox");
        if (firefox > -1)
        {
          $('.icon2').removeClass("caret");
          $('.icon2').addClass("fa fa-chevron-down");
          $('.icon2').css({
            "height": "30px",
            "width": "17px",
            "text-align": "center",
            "background-color": "#E6E2E2",
            "padding-top": "6px",
            "margin-top": "0px",
            "border": "1px solid #C7BFBF"
          });
          $('.dropdown-toggle').css({"padding": "0px", "vertical-align": "middle", "height": "25px"});
          $('.tr0 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
          $('.tr1 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
          $('.multiselect-selected-text').css({
            "padding-top": "4px",
            "padding-bottom": "10px",
            "padding-left": "10px",
            "vertical-align": "middle"
          });
        }
      });

    });

  </script>
  <script src="/merchant/javascript/hidde.js"></script>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }

    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }


    /*    .textb{color: red!important;}*/

  </style>
  <style type="text/css">
    .field-icon
    {
      float:  right;
      margin-top: -25px;
      position: relative;
      z-index: 2;
    }


  </style>

</head>
<body>
<%
  Functions functions = new Functions();
  PartnerDAO partnerDAO = new PartnerDAO();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  ResourceBundle gatewayLabelRB= LoadProperties.getProperty("com.directi.pg.GatewayLabel");
  JSONObject gatewayLabelJSON=new JSONObject();
  if (partner.isLoggedInPartner(session))
  {
    String partnerId = (String) session.getAttribute("merchantid");
    String flag="false";
    String bankid=request.getParameter("bankid");
    String isCvvOptional = "";
    String merchantId = "";
    String pgtypeId="";
    String mcc="";
    String displayName="";
    String username="";
    String password="";
    String shortName="";
    String path="";
    String site="";
    String chargeBackPath="";
    String is3dSupportAccount = "";
    String threeDsVersion = "";
    String isTestAccount="";
    String isActiveAccount="";
    String gateway_table_name = "";
    String gateway = "";
    String tableColumn = "";
    String daily_amount_range= "500.00-1000.00";
    LinkedHashMap<Integer, String> gatewayTypes =new LinkedHashMap<Integer, String>();

    String role=(String)session.getAttribute("role");

    StringBuilder dataStringBuilder = new StringBuilder();
    Connection conn=null;
    //ArrayList<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();

    try
    {
      if(functions.isValueNull(request.getParameter("mid")))
        merchantId = request.getParameter("mid");

      if (request.getParameter("pgtypeid") != null && request.getParameter("pgtypeid").length() > 0)
      {
        pgtypeId = request.getParameter("pgtypeid");
        try
        {
          conn = Database.getConnection();
          ResultSet rs = Database.executeQuery("SELECT gtype.pgTypeId as pgTypeId,gtype.name as name FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid='"+partnerId+"' and gtype.gateway=(select gateway from gateway_type where pgtypeid='"+pgtypeId+"') ORDER BY gtype.name,gtype.currency ASC", conn);
          while (rs.next())
          {
            gatewayTypes.put(rs.getInt("pgTypeId"), rs.getString("name"));
          }
        }catch(Exception e){
          logger.error("Exception---" + e);
        }
        finally
        {
          Database.closeConnection(conn);
        }
      }
      else{
        try
        {
          conn = Database.getConnection();
          ResultSet rs = Database.executeQuery("SELECT gtype.pgTypeId as pgTypeId,gtype.name as name FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid='"+partnerId+"' ORDER BY gtype.name,gtype.currency ASC", conn);
          while (rs.next())
          {
            gatewayTypes.put(rs.getInt("pgTypeId"), rs.getString("name"));
          }
        }catch(Exception e){
          logger.error("Exception---" + e);
        }
        finally
        {
          Database.closeConnection(conn);
        }
      }


      if (functions.isValueNull(request.getParameter("mcc")))
        mcc = request.getParameter("mcc");

      if(functions.isValueNull(request.getParameter("displayName")))
        displayName = request.getParameter("displayName");

      if(functions.isValueNull(request.getParameter("username")))
        username = request.getParameter("username");

      if(functions.isValueNull(request.getParameter("pwd")))
        password = request.getParameter("pwd");

      if(functions.isValueNull(request.getParameter("shortname")))
        shortName = request.getParameter("shortname");

      if(functions.isValueNull(request.getParameter("path")))
        path = request.getParameter("path");
      if(functions.isValueNull(request.getParameter("site")))
        site = request.getParameter("site");
      if(functions.isValueNull(request.getParameter("chargebackpath")))
        chargeBackPath=request.getParameter("chargebackpath");
      if(functions.isValueNull(request.getParameter("is3dSupportAccount")))
        is3dSupportAccount = request.getParameter("is3dSupportAccount");
      if(functions.isValueNull(request.getParameter("threeDsVersion")))
        threeDsVersion = request.getParameter("threeDsVersion");

      if(functions.isValueNull(request.getParameter("isTestAccount")))
        isTestAccount = request.getParameter("isTestAccount");
      if(functions.isValueNull(request.getParameter("isActiveAccount")))
        isTestAccount = request.getParameter("isActiveAccount");

      if (pgtypeId.length() > 0)
      {
        conn = Database.getConnection();
        String tableNameQuery = "SELECT isCvvOptional, gateway_table_name,gateway FROM gateway_type WHERE pgtypeid=?";
        PreparedStatement preparedStatement = conn.prepareStatement(tableNameQuery);
        preparedStatement.setInt(1,Integer.parseInt(pgtypeId));
        ResultSet rs = preparedStatement.executeQuery();
        if(rs.next())
        {
          isCvvOptional = rs.getString("isCvvOptional");
          gateway_table_name = rs.getString("gateway_table_name");
          gateway = rs.getString("gateway");
        }
        try{
          String json=gatewayLabelRB.getString(gateway);
          if(functions.isValueNull(json) && json.contains("{"))
            gatewayLabelJSON=new JSONObject(json);
        }catch (Exception e){
          logger.error("Property not found-->",e);
        }
        if(functions.isValueNull(gateway_table_name) && gateway_table_name.length() > 0)
        {
          String tableDataQuery = "select * from " + gateway_table_name + " where 1=1 limit 1";
          PreparedStatement preparedStatementData = conn.prepareStatement(tableDataQuery);
          ResultSet tableMetaDataRs = preparedStatementData.executeQuery();
          Integer count = tableMetaDataRs.getMetaData().getColumnCount();
          while(tableMetaDataRs.next())
          {
            for(int i=1;i<=count;i++)
            {
              String columnName = tableMetaDataRs.getMetaData().getColumnName(i);
              String columnNameLabel = tableMetaDataRs.getMetaData().getColumnName(i);
              int columnNameSize = tableMetaDataRs.getMetaData().getColumnDisplaySize(i);
              columnNameLabel=columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1);
              String isOptional="true";
              if(tableMetaDataRs.getMetaData().isNullable(i)==0)
              {
                columnNameLabel=columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1)+"*";
                isOptional="false";
              }
              columnNameLabel = partnerDAO.toTitleCase(columnNameLabel.replace("_"," "));
              String columnType = tableMetaDataRs.getMetaData().getColumnTypeName(i);
              if(!columnName.equalsIgnoreCase("id") && !columnName.equalsIgnoreCase("accountid"))
              {
                dataStringBuilder.append("<div class='form-group'>");
                dataStringBuilder.append("<div class='col-md-3'></div>");
                if ("IsThreeDSec*".equals(columnNameLabel))
                {
                  dataStringBuilder.append("<label class='col-md-3 control-label'>"+columnNameLabel+" (Y/N)</label>");
                }
                else
                {
                  dataStringBuilder.append("<label class='col-md-3 control-label'>"+columnNameLabel+"</label>");
                }
                dataStringBuilder.append("<div class='col-md-4'>");
                dataStringBuilder.append("<input type=\"hidden\" name=\""+columnName+"_isOptional\" value=\""+isOptional+"\"><input type=\"hidden\" name=\""+columnName+"_size\" value=\""+columnNameSize+"\"><input  maxlength='255' type='text' name='"+columnName+"'  class='form-control value=''>("+columnType+" - "+columnNameSize+")");
                dataStringBuilder.append("</div>");
                dataStringBuilder.append("<div class='col-md-6'></div>");
                dataStringBuilder.append("</div>");
              }
              tableColumn += columnName + ",";
            }
          }
          if(tableColumn.length() > 0)
            tableColumn = tableColumn.substring(0,tableColumn.length()-1);
        }
      }
      else
      {
        logger.error("pgtypenot selected");
      }
    }
    catch(Exception e)
    {
      logger.error("Sql Exception in manageGatewayAccount:",e);
    }
    finally
    {
      Database.closeConnection(conn);
    }
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/gatewayAccountInterface.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if("accountid".equals(name))
                {
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                }
                else
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
              }
            %>

            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=manageGatewayAccount_Add_New_Bank%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <form action="/partner/net/ManageGatewayAccounts?ctoken=<%=ctoken%>" method="post" name="addbankaccount" onsubmit="return check()" class="form-horizontal">
              <input type="hidden" value="<%=partnerId%>" name="partnerid">
              <input type="hidden" value="<%=ctoken%>" name="ctoken">
              <input type="hidden" value="true" name="isSubmitted">
              <input type="hidden" name="gatewaytablename" value="<%=gateway_table_name%>">
              <input type="hidden" name="columnnames" value="<%=tableColumn%>">
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <div class="widget-content padding">

                <%
                  String msg = (String) request.getAttribute("msg");
                  if(functions.isValueNull(msg))
                  {
                    //out.println("<center><font class=\"textb\"><b>"+msg+"</b></font></center><br>");
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                    flag="true";
                  }
                %>
                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_bank_name%></label>
                  <div class="col-md-4">
                    <select id="pgtypeid" name="pgtypeid" class="form-control"
                            multiple="multiple" size="1" onchange ='gettabledata()'>
                      <option value="" default>Display all Banks</option>
                      <%
                        if(gatewayTypes != null ){
                        for (Integer id : gatewayTypes.keySet())
                        {
                          String st = "";
                          String name = gatewayTypes.get(id);%>
                        <option value="<%=id%>"><%=gatewayTypes.get(id)%>
                      </option>
                      <%
                        }
                        }
                      %>
                      <%--<%=sb.toString()%>--%>
                    </select>
                    <input type="hidden" id="bankid" name="bankid" value="<%=pgtypeId%>">
                    <script type="application/javascript">
                      <%
                      if(flag.equals("true")){
                       if(functions.isValueNull(bankid)){%>
                      var bankid = "<%=bankid%>";
                      var temp = new Array();
                      if(bankid != "")
                      {
                        temp = bankid.split(",");
                      }
                      for(var i=0; i <= temp.length ; i++)
                      {
                        console.log(temp[i]);
                        $("#pgtypeid option[value='"+temp[i]+"']").prop('selected', true);
                      }
                      <%
                        }
                            }else{
                                    if(functions.isValueNull(pgtypeId)){%>
                      $("#pgtypeid option[value='<%=pgtypeId%>']").prop('selected', true);
                      <%
                      }
                      }
                      %>
                      $('#pgtypeid').multiselect({
                        buttonText: function (options, select)
                        {
                          var labels = [];
                          if (options.length === 0)
                          {
                            labels.pop();
                            document.getElementById('bankid').value = labels;
                            return 'Select PgType ID';
                          }
                          else
                          {
                            options.each(function ()
                            {
                              if($(this).val() != "")
                              {
                                labels.push($(this).val());
                              }
                            });
                            document.getElementById('bankid').value = labels;
                            //return labels.join(', ') + '';
                            return 'Select PgType ID';
                          }
                        }
                      });
                    </script>
                   <%-- <select id="pgtypeid" name="pgtypeid" class="form-control" onchange="gettabledata();"> <option value="" default><%=manageGatewayAccount_select_bank%></option>
                      <%
                        Connection con = null;
                        try
                        {
                          con = Database.getConnection();
                          StringBuffer qry = new StringBuffer("SELECT gtype.pgtypeid,gtype.gateway,gtype.name,gtype.currency,gtpm.partnerid FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid=? ORDER BY name,currency ASC");
                          PreparedStatement pstmt = con.prepareStatement(qry.toString());
                          pstmt.setString(1, partnerId);
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selected = "";
                            if (rs.getString("pgtypeid").equals(pgtypeId))
                            {
                              selected = "selected";
                            }
                            out.println("<option value=" + rs.getString("pgtypeid") + " " + selected + ">" + rs.getString("name") + " : " + rs.getString("currency") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(con);
                        }
                      %>
                    </select>--%>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">
                    <%if(gatewayLabelJSON.has("merchantId")){
                      out.println(gatewayLabelJSON.getString("merchantId"));
                    %>
                    <%}else{%><%=manageGatewayAccount_MerchantId%><%}%><%=manageGatewayAccount_mid%></label>
                  <div class="col-md-4">
                    <input  size="10" type="text" name="mid"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(merchantId)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <%
                  if("Y".equals(isCvvOptional))
                  {
                %>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Is CVV Required* :</label>
                  <div class="col-md-4">
                    <select name="iscvvrequired" id="iscvvrequired" class="form-control">
                      <option value='Y'>Y</option>
                      <option value='N'>N</option>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <%
                  }
                %>


                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_mcc%></label>
                  <div class="col-md-4">
                    <input maxlength="100" type="text" name="mcc" class="form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute(mcc)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_billing%></label>
                  <div class="col-md-4">
                    <input  maxlength="255" type="text" name="displayName"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(displayName)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_master%></label>
                  <div class="col-md-4">
                    <select name="ismastercardsupported" id="ismastercardsupported" class="form-control">
                      <%
                        String ismastercardsupported=request.getParameter("ismastercardsupported");
                        if("1".equals(ismastercardsupported))
                        {
                      %>
                      <option value='0'><%=manageGatewayAccount_Disable%></option>
                      <option value='1'  selected><%=manageGatewayAccount_Enable%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value='0'  selected><%=manageGatewayAccount_Disable%></option>
                      <option value='1'><%=manageGatewayAccount_Enable%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">
                    <%if(gatewayLabelJSON.has("username")){
                    out.println(gatewayLabelJSON.getString("username"));
                    %>
                    <%}else{%><%=manageGatewayAccount_username%><%}%> <%=manageGatewayAccount_username1%></label>
                  <div class="col-md-4">
                    <input  maxlength="50" type="text" name="username"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">
                    <%if(gatewayLabelJSON.has("password")){
                      out.println(gatewayLabelJSON.getString("password"));
                    %>
                    <%}else{%><%=manageGatewayAccount_password%><%}%> <%=manageGatewayAccount_password1%></label>
                  <div class="col-md-4">
                    <input  maxlength="255" id="passwd" type="text" name="pwd"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(password)%>" autocomplete="off"
                            readonly onfocus="this.removeAttribute('readonly');">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">
                    <%if(gatewayLabelJSON.has("shortName")){
                      out.println(gatewayLabelJSON.getString("shortName"));
                    %>
                    <%}else{%><%=manageGatewayAccount_shortname%><%}%> <%=manageGatewayAccount_shortname1%></label>
                  <div class="col-md-4">
                    <input  maxlength="255" type="text" name="shortname"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(shortName)%>" >
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">
                    <%if(gatewayLabelJSON.has("site")){
                      out.println(gatewayLabelJSON.getString("site"));
                    %>
                    <%}else{%><%=manageGatewayAccount_Site%><%}%> <%=manageGatewayAccount_Site1%></label>
                  <div class="col-md-4">
                    <input  maxlength="255" type="text" name="site"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(site)%>" >
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">
                    <%if(gatewayLabelJSON.has("path")){
                      out.println(gatewayLabelJSON.getString("path"));
                    %>
                    <%}else{%><%=manageGatewayAccount_path%><%}%> <%=manageGatewayAccount_path1%></label>
                  <div class="col-md-4">
                    <input  maxlength="255" type="text" name="path"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(path)%>" >
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">
                    <%if(gatewayLabelJSON.has("chargeBackPath")){
                      out.println(gatewayLabelJSON.getString("chargeBackPath"));
                    %>
                    <%}else{%><%=manageGatewayAccount_chargeback%><%}%> <%=manageGatewayAccount_chargeback1%></label>
                  <div class="col-md-4">
                    <input  maxlength="255" type="text" name="chargeBackPath"  class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeBackPath)%>" >
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_card%></label>
                  <div class="col-md-4">
                    <select name="cardLimitCheck" id="cardLimitCheck" class="form-control">
                      <%String cardLimitCheck=request.getParameter("cardLimitCheck");
                        if("account_Level".equalsIgnoreCase(cardLimitCheck)){
                      %>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level' selected><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level'><%=manageGatewayAccount_mid_level%></option>
                      <%}else if("mid_Level".equalsIgnoreCase(cardLimitCheck)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level'><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level' selected><%=manageGatewayAccount_mid_level%></option>
                      <%}else{%>
                      <option value='N' selected ><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level'><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level'><%=manageGatewayAccount_mid_level%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_limit%></label>
                  <div class="col-md-4">
                    <select name="cardAmountLimitCheck" id="cardAmountLimitCheck" class="form-control">
                      <%String cardAmountLimitCheck=request.getParameter("cardAmountLimitCheck");
                        if("account_Level".equalsIgnoreCase(cardAmountLimitCheck)){
                      %>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level' selected><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level'><%=manageGatewayAccount_mid_level%></option>
                      <%}else if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level'><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level' selected><%=manageGatewayAccount_mid_level%></option>
                      <%}else{%>
                      <option value='N' selected ><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level'><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level'><%=manageGatewayAccount_mid_level%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_limit_check%></label>
                  <div class="col-md-4">
                    <select name="amountLimitCheck" id="amountLimitCheck" class="form-control">
                      <%String amountLimitCheck=request.getParameter("amountLimitCheck");
                        if("account_Level".equalsIgnoreCase(amountLimitCheck)){
                      %>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level' selected><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level'><%=manageGatewayAccount_mid_level%></option>
                      <%}else if("mid_Level".equalsIgnoreCase(amountLimitCheck)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level'><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level' selected><%=manageGatewayAccount_mid_level%></option>
                      <%}else{%>
                      <option value='N' selected ><%=manageGatewayAccount_Disable%></option>
                      <option value='account_Level'><%=manageGatewayAccount_account%></option>
                      <option value='mid_Level'><%=manageGatewayAccount_mid_level%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_limit_daily%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="dailycardlimit" id="dailycardlimit" size="10" value="<%= request.getParameter("dailycardlimit") != null?request.getParameter("dailycardlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='daily_card_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_limit_weekly%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="weeklycardlimit" id="weeklycardlimit" size="10" value="<%= request.getParameter("weeklycardlimit") != null?request.getParameter("weeklycardlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='weekly_card_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_limit_monthly%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10"  name="monthlycardlimit" id="monthlycardlimit" size="10" value="<%= request.getParameter("monthlycardlimit") != null?request.getParameter("monthlycardlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='monthly_card_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_limit_amount%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="dailyamountlimit" id="dailyamountlimit" size="10" value="<%= request.getParameter("dailyamountlimit") != null?request.getParameter("dailyamountlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='daily_amount_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Daily Amount Range</label>
                  <div class="col-md-4">
                    <input class="form-control" type="text" maxlength="20" name="daily_amount_range" id="daily_amount_range" size="10" value="<%=daily_amount_range%>">
                  </div>
                  <select class="chkbox" name="daily_amount_range_check">
                    <%
                      out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));
                    %></select>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_amount_weekly%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="weeklyamountlimit" id="weeklyamountlimit" size="10" value="<%= request.getParameter("weeklyamountlimit") != null?request.getParameter("weeklyamountlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='weekly_amount_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_amount_monthly%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="monthlyamountlimit" id="monthlyamountlimit" size="10" value="<%= request.getParameter("monthlyamountlimit") != null?request.getParameter("monthlyamountlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='monthly_amount_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_amount_daily%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="dailycardamountlimit" id="dailycardamountlimit" size="10" value="<%= request.getParameter("dailycardamountlimit") != null?request.getParameter("dailycardamountlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='daily_card_amount_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Weekly Card Amount Limit:</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="weeklycardamountlimit" id="weeklycardamountlimit" size="10" value="<%= request.getParameter("weeklycardamountlimit") != null?request.getParameter("weeklycardamountlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='weekly_card_amount_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_amount_monthly1%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="10" name="monthlycardamountlimit" id="monthlycardamountlimit" size="10" value="<%= request.getParameter("monthlycardamountlimit") != null?request.getParameter("monthlycardamountlimit"):"" %>">
                  </div>
                  <select class ="chkbox " name='monthly_card_amount_limit_check' ><%out.println(functions.comboval(ESAPI.encoder().encodeForHTMLAttribute("N")));%></select>

                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_supported%></label>
                  <div class="col-md-4">
                    <select name="is3dSupportAccount" id="is3dSupportAccount" class="form-control">
                      <%if("Y".equalsIgnoreCase(is3dSupportAccount)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y' selected><%=manageGatewayAccount_Enable%></option>
                      <option value='O'><%=manageGatewayAccount_only%></option>
                      <option value='R'>Routing</option>
                      <%}else if("O".equalsIgnoreCase(is3dSupportAccount)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <option value='O' selected><%=manageGatewayAccount_only%></option>
                      <option value='R'>Routing</option>
                      <%}else if("R".equalsIgnoreCase(is3dSupportAccount)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <option value='O'><%=manageGatewayAccount_only%></option>
                      <option value='R' selected>Routing</option>
                      <%}else{%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <option value='O'><%=manageGatewayAccount_only%></option>
                      <option value='R'>Routing</option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_3Ds_version%></label>
                  <div class="col-md-4">
                    <select name="threeDsVersion" id="threeDsVersion" class="form-control">
                      <%if("3Dsv2".equalsIgnoreCase(threeDsVersion)){%>
                      <option value='3Dsv1'>3Ds v1</option>
                      <option value='3Dsv2' selected>3Ds v2</option>
                      <%}else{%>
                      <option value='3Dsv1'>3Ds v1</option>
                      <option value='3Dsv2'>3Ds v2</option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_test%></label>
                  <div class="col-md-4">
                    <select name="isTestAccount" id="isTestAccount" class="form-control">
                      <%if("Y".equalsIgnoreCase(isTestAccount)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y' selected><%=manageGatewayAccount_Enable%></option>
                      <%}else{%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_active%></label>
                  <div class="col-md-4">
                    <select name="isActiveAccount" id="isActiveAccount" class="form-control">
                      <%if("Y".equalsIgnoreCase(isActiveAccount)){%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y' selected><%=manageGatewayAccount_Enable%></option>
                      <%}else{%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_multiple%></label>
                  <div class="col-md-4">
                    <select name="isMultipleRefund" id="isMultipleRefund" class="form-control">
                      <%String isMultipleRefund=request.getParameter("isMultipleRefund");
                        if("Y".equalsIgnoreCase(isMultipleRefund)){%>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <%}else{%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_partial%></label>
                  <div class="col-md-4">
                    <select name="PartialRefund" id="PartialRefund" class="form-control">
                      <%String isPartialRefund=request.getParameter("PartialRefund");
                        if("Y".equalsIgnoreCase(isPartialRefund)){%>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <%}else{%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_recuring%></label>
                  <div class="col-md-4">
                    <select name="isrecurring" id="isrecurring" class="form-control">
                      <%String isrecurring=request.getParameter("isrecurring");
                        if("Y".equalsIgnoreCase(isrecurring)){%>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <%}else{%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=manageGatewayAccount_address%></label>
                  <div class="col-md-4">
                    <select name="addressValidation" id="addressValidation" class="form-control">
                      <%String addressValidation=request.getParameter("addressValidation");
                        if("Y".equalsIgnoreCase(addressValidation)){%>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <%}else{%>
                      <option value='N'><%=manageGatewayAccount_Disable%></option>
                      <option value='Y'><%=manageGatewayAccount_Enable%></option>
                      <%}%>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                <div class="col-md-3"></div>
                <label class="col-md-3 control-label"><%=manageGatewayAccount_emi%></label>
                <div class="col-md-4">
                  <select name="emiSupport" id="emiSupport" class="form-control">
                    <%String emiSupport=request.getParameter("emiSupport");
                      if("Y".equalsIgnoreCase(emiSupport)){%>
                    <option value='Y'><%=manageGatewayAccount_Enable%></option>
                    <option value='N'><%=manageGatewayAccount_Disable%></option>
                    <%}else{%>
                    <option value='N'><%=manageGatewayAccount_Disable%></option>
                    <option value='Y'><%=manageGatewayAccount_Enable%></option>
                    <%}%>
                  </select>
                </div>
                <div class="col-md-6"></div>
              </div>

              <div class="form-group">
                <div class="col-md-3"></div>
                <label class="col-md-3 control-label"><%=manageGatewayAccount_fx%></label>
                <div class="col-md-4">
                  <select name="isForexMid" id="isForexMid" class="form-control">
                    <%String isForexMid=request.getParameter("isForexMid");
                      if("Y".equalsIgnoreCase(isForexMid)){%>
                    <option value='Y'><%=manageGatewayAccount_Enable%></option>
                    <option value='N'><%=manageGatewayAccount_Disable%></option>
                    <%}else{%>
                    <option value='N'><%=manageGatewayAccount_Disable%></option>
                    <option value='Y'><%=manageGatewayAccount_Enable%></option>
                    <%}%>
                  </select>
                </div>
                <div class="col-md-6"></div>
              </div>
              <div class="form-group">
                <div class="col-md-3"></div>
                <label class="col-md-3 control-label"><%=manageGatewayAccount_dynamic%></label>
                <div class="col-md-4">
                  <select name="isDynamicDescriptor" id="isDynamicDescriptor" class="form-control">
                    <%String isDynamicDescriptor=request.getParameter("isDynamicDescriptor");
                      if("Y".equalsIgnoreCase(isDynamicDescriptor)){%>
                    <option value='Y'><%=manageGatewayAccount_Enable%></option>
                    <option value='N'><%=manageGatewayAccount_Disable%></option>
                    <%}else{%>
                    <option value='N'><%=manageGatewayAccount_Disable%></option>
                    <option value='Y'><%=manageGatewayAccount_Enable%></option>
                    <%}%>
                  </select>
                </div>
                <div class="col-md-6"></div>
              </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">From Account ID :</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="50" name="fromAccountId" id="fromAccountId" size="50" value="<%= request.getParameter("fromAccountId") != null?request.getParameter("fromAccountId"):"" %>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">From MID :</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="50" name="fromMid" id="fromMid" size="50" value="<%= request.getParameter("fromMid") != null?request.getParameter("fromMid"):"" %>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <%
                  if(dataStringBuilder.length() > 0)
                  {
                    GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeId);
                %>
                <h4><i class="fa fa-table"></i>&nbsp;&nbsp;<strong> <%=gatewayType.getGateway() %> Gateway Account Details</strong></h4>

                <%=dataStringBuilder.toString()%>

                <%
                    logger.debug("dataStringBuilder:::::"+dataStringBuilder);
                  }%>


                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility:hidden;">Button</label>
                  <div class="col-md-4">
                    <button type="submit" class="buttonform btn btn-default" name="add">
                      <i class="fa fa-save"></i>&nbsp;
                      <%=manageGatewayAccount_add_bank%>
                    </button>
                  </div>
                  <div class="col-md-6"></div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<%
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
  }
%>
</body>
</html>