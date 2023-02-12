<%@ page import="com.directi.pg.Database"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.codehaus.jettison.json.JSONObject" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 8/9/15
  Time: 3:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  //private LoadProperties LoadProperties;
%><%
  Logger logger = new Logger("actionGatewayAccounts.jsp");
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","actionGatewayAccounts");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> | Bank Account Details</title>
  <style type="text/css">
    @media(max-width: 767px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
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
  <script>
    function disbaleBank(){
      document.getElementById("pgtypeid").disabled = false;
    }
    var lablevalues = new Array();
    function ChangeFunction(Value , lable){
      console.log("Value" + Value + "lable" + lable);
      var finalvalue=lable+"="+Value;
      console.log("finalvalue" + finalvalue );
      lablevalues.push(finalvalue);
      console.log(lablevalues);
      document.getElementById("onchangedvalue").value = lablevalues;
    }
  </script>

  <script src="/merchant/javascript/hidde.js"></script>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    ResourceBundle gatewayLabelRB= LoadProperties.getProperty("com.directi.pg.GatewayLabel");
    JSONObject gatewayLabelJSON=new JSONObject();
    String memberid = (String) session.getAttribute("merchantid");

    String str=null;
    String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String merchantid = Functions.checkStringNull(request.getParameter("merchantid"))==null?"":request.getParameter("merchantid");
    String role=(String)session.getAttribute("role");
    String username=(String)session.getAttribute("username");
    String actionExecutorId=(String)session.getAttribute("merchantid");
    String actionExecutorName=role+"-"+username;

    String setSelected;
    HashMap<String,String> dropdown = new HashMap<String, String>();
    dropdown.put("Y","Y");
    dropdown.put("N","N");

    if (accountid != null) str = str + "&accountid=" + accountid;
    if (merchantid != null) str = str + "&merchantid=" + merchantid;
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <form action="/partner/net/listGatewayAccountDetails?ctoken=<%=ctoken%>" method="post" name="form">

        <div class="pull-right">
          <div class="btn-group">
            <form action="/partner/net/ActionGatewayAccounts?ctoken=<%=ctoken%>" method="post" name="form">
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
      </form>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <%
                StringBuffer confg = new StringBuffer(" ");
                String action = (String) request.getAttribute("action");
                if(action.equalsIgnoreCase("View"))
                {
                  confg.append("disabled");
              %>
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>View Bank Accounts</strong></h2>
              <%
              }
              else if(action.equalsIgnoreCase("Edit"))
              {%>
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Edit Bank Accounts</strong></h2>
              <%
                }
                Hashtable innerhash = new Hashtable();
                Functions functions=new Functions();
                Hashtable hash = (Hashtable) request.getAttribute("hash");
                accountid = (String) request.getAttribute("accountid");

                String table_data =(String) request.getAttribute("table_data");
                String columnName = (String) request.getAttribute("table_column");
                String conf = " ";
                String gateway_table_name = (String)request.getAttribute("gateway_table_name");
                String pgTypeId="";
                String isCvvOptional="";
                String gateway="";

                if(hash != null && hash.size() > 0)
                {
                  innerhash = (Hashtable) hash.get(1 + "");
                  pgTypeId=(String)innerhash.get("pgtypeid");
                  GatewayType gatewayType= GatewayTypeService.getGatewayType(pgTypeId);
                  isCvvOptional=gatewayType.getIsCvvOptional();
                  gateway=gatewayType.getGateway();
                  try{
                    String json=gatewayLabelRB.getString(gateway);
                    if(functions.isValueNull(json) && json.contains("{"))
                      gatewayLabelJSON=new JSONObject(json);
                  }catch (Exception e){
                    logger.error("Property not found");
                  }
              %>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">

              <table class="table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <form action="/partner/net/ActionGatewayAccounts?ctoken=<%=ctoken%>" method="post" name="forms" >
                  <input type="hidden" value="<%=accountid%>" name="accountid">
                  <input type="hidden" value="<%=gateway_table_name%>" name="gateway_table_name">
                  <input type="hidden" value="<%=columnName%>" name="columnnames">
                  <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Bank Name*</td>
                    <td align="center">
                      <select id="pgtypeid" name="pgtypeid" class="form-control"  disabled >
                        <%
                          System.out.println((String) innerhash.get("pgtypeid"));
                          Connection con            = null;
                          PreparedStatement pstmt   = null;
                          ResultSet rs              = null;

                          try
                          {
                            con               = Database.getConnection();
                            StringBuffer qry  = new StringBuffer("SELECT gtype.pgtypeid,gtype.gateway,gtype.name,gtype.currency,gtpm.partnerid FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid=?");
                            pstmt             = con.prepareStatement(qry.toString());
                            pstmt.setString(1,memberid);
                            rs        = pstmt.executeQuery();
                            String id =(String) innerhash.get("pgtypeid");
                            while(rs.next())
                            {
                              String selected="";
                              if(innerhash.get("pgtypeid").equals(rs.getString("pgtypeid")))
                              {
                                selected="selected";
                              }

                              if(!id.equals(rs.getString("pgtypeid")))
                              {
                                out.println("<option value=" + rs.getString("pgtypeid") + " "+selected+" >" + rs.getString("name") + " : " + rs.getString("currency") + "</option>");
                              }
                            }

                              out.println("<option value=" + (String) innerhash.get("pgtypeid") + " selected>" + (String) innerhash.get("name") + " : " + (String) innerhash.get("currency") + "</option>");
                          }
                          catch (Exception e)
                          {
                            logger.error("Exception::::"+e);
                          }
                          finally
                          {
                            Database.closeResultSet(rs);
                            Database.closePreparedStatement(pstmt);
                            Database.closeConnection(con);
                          }
                        %>
                      </select>
                      <input type="hidden" name="pgtypeid" value="<%=(String) innerhash.get("pgtypeid")%>">
                    </td>
                      <td>
                      <button type="button" class="btn btn-default"  <%=confg%> name="update_bank" value="Update" onclick="disbaleBank()">
                         Bank Name Update
                      </button>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">
                      <%if(gatewayLabelJSON.has("merchantId")){
                        out.println(gatewayLabelJSON.getString("merchantId"));
                      %>
                      <%}else{%>Merchant Id<%}%>*</td>
                    <td colspan="2" align="center">
                      <input type="text" name="mid"  class="form-control" value="<%=(String) innerhash.get("merchantid")%>" <%=confg%> onchange="ChangeFunction(this.value,'Merchant Id')">
                    </td>

                  </tr>

                  <%
                    if("Y".equals(isCvvOptional))
                    {
                  %>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Is CVV Required*</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="iscvvrequired" id="iscvvrequired" <%=confg%>>
                        <%
                          String isCVVRequired=(String)innerhash.get("isCVVrequired");
                          if("Y".equals(isCVVRequired))
                          {
                        %>
                        <option value='Y' selected >Enable</option>
                        <option value='N'>Disable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N' selected >Enable</option>
                        <option value='Y'>Disable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>

                  </tr>
                  <%
                    }
                  %>

                  <tr >
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">MCC/ Alias Name*</td>
                    <td align="center" colspan="2">
                      <input  maxlength="100" type="text" name="mcc"  class="form-control" value="<%= ESAPI.encoder().encodeForHTML((String) innerhash.get("aliasname"))%>" <%=confg%> onchange="ChangeFunction(this.value,'MCC/Alias Name')">
                    </td>

                    </td>
                  </tr>

                  <tr >
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Billing Descriptor/ Display Name*</td>
                    <td align="center" colspan="2">
                      <input  maxlength="255" type="text" name="displayname"  class="form-control" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("displayname"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Billing Descriptor/Display Name')">
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Is MasterCardSupported</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="ismastercardsupported" id="ismastercardsupported" <%=confg%> onchange="ChangeFunction(this.value,'Is MasterCardSupported')">
                        <%
                          String ismastercardsupported=(String)innerhash.get("ismastercardsupported");
                          //System.out.println("isActiveAccount:::::"+isActiveAccount);
                          if("1".equals(ismastercardsupported))
                          {
                        %>
                        <option value='0'>Disable</option>
                        <option value='1'  selected>Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='0'  selected>Disable</option>
                        <option value='1'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>


                  <tr >
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">
                      <%if(gatewayLabelJSON.has("username")){
                        out.println(gatewayLabelJSON.getString("username"));
                      %>
                      <%}else{%>Username/ Key<%}%></td>
                    <td align="center" colspan="2">
                      <input  maxlength="50" type="text" name="username"  class="form-control" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("username"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Username/Key')">
                    </td>

                  </tr>


                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">
                      <%if(gatewayLabelJSON.has("password")){
                        out.println(gatewayLabelJSON.getString("password"));
                      %>
                      <%}else{%>Password<%}%></td>
                    <td align="center" colspan="2">
                      <input  maxlength="255" id="passwd" type="text" name="passwd"  class="form-control" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("passwd"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Password')">
                    </td>

                  </tr>


                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">
                      <%if(gatewayLabelJSON.has("shortName")){
                        out.println(gatewayLabelJSON.getString("shortName"));
                      %>
                      <%}else{%>Short Name<%}%></td>
                    <td align="center" colspan="2">
                      <input  maxlength="255" type="text" name="shortname"  class="form-control" value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("shortname"))) ? ESAPI.encoder().encodeForHTML((String) innerhash.get("shortname")) : ""%>" <%=confg%> onchange="ChangeFunction(this.value,'Short Name')">
                    </td>

                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">
                      <%if(gatewayLabelJSON.has("site")){
                        out.println(gatewayLabelJSON.getString("site"));
                      %>
                      <%}else{%>Site<%}%></td>
                    <td align="center" colspan="2">
                      <input  maxlength="255" type="text" name="site"  class="form-control" value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("site"))) ? ESAPI.encoder().encodeForHTML((String) innerhash.get("site")) : ""%>" <%=confg%> onchange="ChangeFunction(this.value,'Site')">
                    </td>

                  <tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">
                      <%if(gatewayLabelJSON.has("path")){
                        out.println(gatewayLabelJSON.getString("path"));
                      %>
                      <%}else{%>Path<%}%></td>
                    <td align="center" colspan="2">
                      <input  maxlength="255" type="text" name="path"  class="form-control" value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("path"))) ? ESAPI.encoder().encodeForHTML((String) innerhash.get("path")) : ""%>" <%=confg%> onchange="ChangeFunction(this.value,'Path')">
                    </td>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">
                      <%if(gatewayLabelJSON.has("chargeBackPath")){
                        out.println(gatewayLabelJSON.getString("chargeBackPath"));
                      %>
                      <%}else{%>ChargeBack Path<%}%></td>
                    <td align="center" colspan="2">
                      <input  maxlength="255" type="text" name="chargeBackPath"  class="form-control" value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String) innerhash.get("chargeback_path"))) ? ESAPI.encoder().encodeForHTML((String) innerhash.get("chargeback_path")) : ""%>" <%=confg%> <%=confg%> onchange="ChangeFunction(this.value,'ChargeBack Path')">
                    </td>

                  <tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Card Limit check</td>
                    <td align="center" colspan="2">
                      <select name="cardLimitCheck" id="cardLimitCheck" class="form-control" <%=confg%> onchange="ChangeFunction(this.value,'Card Limit check')">
                        <%String cardLimitCheck=(String)innerhash.get("cardLimitCheck");
                        if("account_Level".equalsIgnoreCase(cardLimitCheck)){
                        %>
                        <option value='N'>Disable</option>
                        <option value='account_Level' selected>Account Level</option>
                        <option value='mid_Level'>MID Level</option>
                        <%}else if("mid_Level".equalsIgnoreCase(cardLimitCheck)){%>
                        <option value='N'>Disable</option>
                        <option value='account_Level'>Account Level</option>
                        <option value='mid_Level' selected>MID Level</option>
                        <%}else{%>
                        <option value='N' selected >Disable</option>
                        <option value='account_Level'>Account Level</option>
                        <option value='mid_Level'>MID Level</option>
                        <%}%>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Card Amount Limit check</td>
                    <td align="center" colspan="2">
                      <select name="cardAmountLimitCheck" id="cardAmountLimitCheck" class="form-control" <%=confg%> onchange="ChangeFunction(this.value,'Card Amount Limit check')">
                        <%String cardAmountLimitCheck=(String)innerhash.get("cardAmountLimitCheckAcc");
                          if("account_Level".equalsIgnoreCase(cardAmountLimitCheck)){
                        %>
                        <option value='N'>Disable</option>
                        <option value='account_Level' selected>Account Level</option>
                        <option value='mid_Level'>MID Level</option>
                        <%}else if("mid_Level".equalsIgnoreCase(cardAmountLimitCheck)){%>
                        <option value='N'>Disable</option>
                        <option value='account_Level'>Account Level</option>
                        <option value='mid_Level' selected>MID Level</option>
                        <%}else{%>
                        <option value='N' selected >Disable</option>
                        <option value='account_Level'>Account Level</option>
                        <option value='mid_Level'>MID Level</option>
                        <%}%>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Amount Limit check</td>
                    <td align="center" colspan="2">
                      <select name="amountLimitCheck" id="amountLimitCheck" class="form-control" <%=confg%> onchange="ChangeFunction(this.value,'Amount Limit check')">
                        <%String amountLimitCheck=(String)innerhash.get("amountLimitCheckAcc");
                          if("account_Level".equalsIgnoreCase(amountLimitCheck)){
                        %>
                        <option value='N'>Disable</option>
                        <option value='account_Level' selected>Account Level</option>
                        <option value='mid_Level'>MID Level</option>
                        <%}else if("mid_Level".equalsIgnoreCase(amountLimitCheck)){%>
                        <option value='N'>Disable</option>
                        <option value='account_Level'>Account Level</option>
                        <option value='mid_Level' selected>MID Level</option>
                        <%}else{%>
                        <option value='N' selected >Disable</option>
                        <option value='account_Level'>Account Level</option>
                        <option value='mid_Level'>MID Level</option>
                        <%}%>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Daily Card Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="dailycardlimit" id="dailycardlimit" size="10" value="<%= innerhash.get("daily_card_limit") != null?innerhash.get("daily_card_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Daily Card Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="daily_card_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_limit_check"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Daily card limit check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("daily_card_limit_check")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Weekly Card Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="weeklycardlimit" id="weeklycardlimit" size="10" value="<%= innerhash.get("weekly_card_limit") != null?innerhash.get("weekly_card_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Weekly Card Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="weekly_card_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_limit_check"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Weekly Card Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("weekly_card_limit_check")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Monthly Card Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10"  name="monthlycardlimit" id="monthlycardlimit" size="10" value="<%= innerhash.get("monthly_card_limit") != null?innerhash.get("monthly_card_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Monthly Card Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="monthly_card_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_limit_check"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Monthly Card Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("monthly_card_limit_check")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Daily Amount Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="dailyamountlimit" id="dailyamountlimit" size="10" value="<%= innerhash.get("daily_amount_limit") != null?innerhash.get("daily_amount_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Daily Amount Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="Daily_Account_Amount_Limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("Daily_Account_Amount_Limit"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Daily Amount Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("Daily_Account_Amount_Limit")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Daily Amount Range</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="text" maxlength="20" name="daily_amount_range" id="daily_amount_range" size="10" value="<%=innerhash.get("daily_amount_range")!= null?innerhash.get("daily_amount_range"):""%>" <%=confg%> onchange="ChangeFunction(this.value,'Daily Amount Range')">
                    </td>
                    <td>
                      <select class="form-control" name="daily_amount_range_check" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("daily_amount_range_check"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Daily Amount Range Check')">
                        <%
                          for (Map.Entry<String,String> yesNoPair:dropdown.entrySet() )
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("daily_amount_range_check")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Weekly amount Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="weeklyamountlimit" id="weeklyamountlimit" size="10" value="<%= innerhash.get("weekly_amount_limit") != null?innerhash.get("weekly_amount_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Weekly amount Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="Weekly_Account_Amount_Limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("Weekly_Account_Amount_Limit"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Weekly Account Amount Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("Weekly_Account_Amount_Limit")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Monthly amount Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="monthlyamountlimit" id="monthlyamountlimit" size="10" value="<%= innerhash.get("monthly_amount_limit") != null?innerhash.get("monthly_amount_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Monthly amount Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="Monthly_Account_Amount_Limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("Monthly_Account_Amount_Limit"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Monthly Account Amount Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("Monthly_Account_Amount_Limit")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Daily Card Amount Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="dailycardamountlimit" id="dailycardamountlimit" size="10" value="<%= innerhash.get("daily_card_amount_limit") != null?innerhash.get("daily_card_amount_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Daily Card Amount Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="daily_card_amount_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_amount_limit_check"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Daily Card Amount Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("daily_card_amount_limit_check")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Weekly Card Amount Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="weeklycardamountlimit" id="weeklycardamountlimit" size="10" value="<%= innerhash.get("weekly_card_amount_limit") != null?innerhash.get("weekly_card_amount_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Weekly Card Amount Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="weekly_card_amount_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_amount_limit_check"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Weekly Card Amount Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("weekly_card_amount_limit_check")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Monthly Card Amount Limit</td>
                    <td align="center" colspan="1">
                      <input class="form-control" type="Text" maxlength="10" name="monthlycardamountlimit" id="monthlycardamountlimit" size="10" value="<%= innerhash.get("monthly_card_amount_limit") != null?innerhash.get("monthly_card_amount_limit"):"" %>" <%=confg%> onchange="ChangeFunction(this.value,'Monthly Card Amount Limit')">
                    </td>
                    <td>
                      <select class="form-control" type="text" name="monthly_card_amount_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_amount_limit_check"))%>" <%=confg%> onchange="ChangeFunction(this.value,'Monthly Card Amount Limit Check')">
                        <%
                          for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                          {
                            setSelected="";
                            if(yesNoPair.getKey().equals(innerhash.get("monthly_card_amount_limit_check")))
                              setSelected="selected";
                            out.println("<option value="+yesNoPair.getKey()+" "+setSelected+">"+yesNoPair.getValue()+"</option>");
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">is 3D Supported </td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="is3dSupportAccount" id="is3dSupportAccount" <%=confg%> onchange="ChangeFunction(this.value,'is 3D Supported')">
                        <%
                          String is3dSupportAccount=(String)innerhash.get("3dSupportAccount");
                          if("Y".equals(is3dSupportAccount))
                          {
                        %>
                        <option value='Y' selected >Enable</option>
                        <option value='N'>Disable</option>
                        <option value='O'>Only 3D</option>
                        <option value='R'>Routing</option>
                        <%
                        }
                        else if ("N".equals(is3dSupportAccount))
                        {
                        %>
                        <option value='Y'>Enable</option>
                        <option value='N' selected >Disable</option>
                        <option value='O'>Only 3D</option>
                        <option value='R'>Routing</option>
                        <%
                        }
                        else if ("O".equals(is3dSupportAccount))
                        {
                        %>
                        <option value='Y'>Enable</option>
                        <option value='N'>Disable</option>
                        <option value='O' selected>Only 3D</option>
                        <option value='R'>Routing</option>
                        <%
                          }
                        else
                        {
                        %>
                        <option value='Y'>Enable</option>
                        <option value='N'>Disable</option>
                        <option value='O'>Only 3D</option>
                        <option value='R' selected >Routing</option>
                        <%
                          }
                        %>
                      </select>
                    </td>

                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">3Ds Version </td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="threeDsVersion" id="threeDsVersion" <%=confg%> onchange="ChangeFunction(this.value,'3Ds Version')">
                        <%
                          String threeDsVersion=(String)innerhash.get("threeDsVersion");
                          if("3Dsv1".equals(threeDsVersion))
                          {
                        %>
                        <option value='3Dsv1' selected >3Ds v1</option>
                        <option value='3Dsv2'>3Ds v2</option>
                        <%
                        }
                        else if ("3Dsv2".equals(threeDsVersion))
                        {
                        %>
                        <option value='3Dsv1'>3Ds v1</option>
                        <option value='3Dsv2' selected >3Ds v2</option>
                        <%
                          }
                        else
                        {
                        %>
                        <option value='3Dsv1'>3Ds v1</option>
                        <option value='3Dsv2'>3Ds v2</option>
                        <%
                          }
                        %>
                      </select>
                    </td>

                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">is Test Account</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="isTestAccount" id="isTestAccount" <%=confg%> onchange="ChangeFunction(this.value,'is Test Account')">
                        <%
                          String isTestAccount=(String)innerhash.get("istest");
                          if("Y".equals(isTestAccount))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y' selected >Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N' selected >Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">is Active Account</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="isActiveAccount" id="isActiveAccount" <%=confg%> onchange="ChangeFunction(this.value,'is Active Account')">
                        <%
                          String isActiveAccount=(String)innerhash.get("isactive");
                          //System.out.println("isActiveAccount:::::"+isActiveAccount);
                          if("Y".equals(isActiveAccount))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y' selected >Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N' selected >Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">is Multiple Refund</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="isMultipleRefund" id="isMultipleRefund" <%=confg%> onchange="ChangeFunction(this.value,'is Multiple Refund')">
                        <%
                          String isMultipleRefund=(String)innerhash.get("isMultipleRefund");
                          //System.out.println("isActiveAccount:::::"+isActiveAccount);
                          if("Y".equals(isMultipleRefund))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y' selected >Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N' selected >Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">is Partial Refund</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="PartialRefund" id="PartialRefund" <%=confg%> onchange="ChangeFunction(this.value,'is Partial Refund')">
                        <%
                          String PartialRefund=(String)innerhash.get("PartialRefund");
                          //System.out.println("isActiveAccount:::::"+isActiveAccount);
                          if("Y".equals(PartialRefund))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y' selected >Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N'  selected>Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Is Recurring Allowed</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="isrecurring" id="isrecurring" <%=confg%> onchange="ChangeFunction(this.value,'Is Recurring Allowed')">
                        <%
                          String isrecurring=(String)innerhash.get("is_recurring");
                          //System.out.println("isActiveAccount:::::"+isActiveAccount);
                          if("Y".equals(isrecurring))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y' selected >Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N'  selected>Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Address Validation</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="addressValidation" id="addressValidation" <%=confg%> onchange="ChangeFunction(this.value,'Address Validation')">
                        <%
                          String addressValidation=(String)innerhash.get("addressValidation");
                          //System.out.println("isActiveAccount:::::"+isActiveAccount);
                          if("Y".equals(addressValidation))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y'  selected>Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N'  selected>Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Emi Support</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="emiSupport" id="emiSupport" <%=confg%> onchange="ChangeFunction(this.value,'Emi Support')">
                        <%
                          String emiSupport=(String)innerhash.get("emiSupport");
                          if("Y".equals(emiSupport))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y'selected>Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N'  selected>Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>

                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">MOTO</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="isForexMid" id="isForexMid" <%=confg%> onchange="ChangeFunction(this.value,'MOTO')">
                        <%
                          String isForexMid=(String)innerhash.get("isForexMid");
                         // System.out.println("isForexMid:::::"+isForexMid);
                          if("Y".equals(isForexMid))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y'selected>Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N'selected>Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Is Dynamic Descriptor</td>
                    <td align="center" colspan="2">
                      <select class="form-control" name="isDynamicDescriptor" id="isDynamicDescriptor" <%=confg%> onchange="ChangeFunction(this.value,'Is Dynamic Descriptor')">
                        <%
                          String isDynamicDescriptor=(String)innerhash.get("isDynamicDescriptor");
                          if("Y".equals(isDynamicDescriptor))
                          {
                        %>
                        <option value='N'>Disable</option>
                        <option value='Y'selected>Enable</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value='N'  selected>Disable</option>
                        <option value='Y'>Enable</option>
                        <%
                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">From Account ID</td>
                    <td align="center" colspan="2">
                      <%
                        String fromAccountId="" ;
                        if(functions.isValueNull(fromAccountId))
                        {
                          fromAccountId=((String)innerhash.get("fromAccountId"));
                        }
                        else{
                          fromAccountId="";
                        }
                      %>
                      <input class="form-control" type="Text" maxlength="50" name="fromAccountId" id="fromAccountId" size="50" value="<%=ESAPI.encoder().encodeForHTML(fromAccountId)%>" <%=confg%> onchange="ChangeFunction(this.value,'From Account ID')">
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">From MID</td>
                    <td align="center" colspan="2">
                      <%
                        String fromMid= "";
                        if(functions.isValueNull(fromAccountId))
                        {
                          fromMid=((String)innerhash.get("fromMid"));
                        }
                        else{
                          fromMid="";
                        }
                      %>
                      <input class="form-control" type="Text" maxlength="50" name="fromMid" id="fromMid" size="50" value="<%=ESAPI.encoder().encodeForHTML(fromMid)%>" <%=confg%> onchange="ChangeFunction(this.value,'From MID')" >
                    </td>
                  </tr>
<%

  if(functions.isValueNull((String) innerhash.get("actionExecutorId")))
  {
    actionExecutorId=((String)innerhash.get("actionExecutorId"));
  }
  else
  {
    actionExecutorId="-";
  }
  if(functions.isValueNull((String)innerhash.get("actionExecutorName")))
  {
    actionExecutorName=(String)innerhash.get("actionExecutorName");
  }
  else{
    actionExecutorName="-";
  }
%>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Action Executor Id</td>
                    <td align="center" colspan="2">
                      <input class="form-control" type="Text" maxlength="10" name="actionExecutorId" id="actionExecutorId" size="10" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorId)%>"disabled>
                    </td>
                  </tr>
                  <tr>
                    <td align="right" valign="middle" style="color: #555; vertical-align: middle; border-bottom: 1px solid white; font-weight: 700;">Action Executor Name</td>
                    <td align="center" colspan="2">
                      <input class="form-control" type="Text" maxlength="10" name="actionExecutorName" id="actionExecutorName" size="10" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorName)%>"disabled>
                    </td>
                  </tr>

                    </td>
                  </tr>

                  <%
                    String pgtypeid = (String) request.getAttribute("pgtypeid");
                    if(table_data.length() > 0)
                    {
                  %>
                  <tr>
                    <td class="th0" > <b><%=gatewayType.getGateway() %> GateWay Data Details</b></td>
                    <%

                      logger.debug("gatewayType.getGateway()::::"+gatewayType.getGateway());
                      logger.debug("table_data:::::"+table_data);
                    %>
                  </tr>
                  <tr>
                    <%=table_data%>
                  </tr>

                  <%
                    }
                  %>

                  <tr>
                    <td></td>
                    <td align="center">
                      <button type="submit" class="btn btn-default" style="margin-left: 26px" <%=confg%> name="submit" value="Update">
                        <i class="fa fa-sign-in"></i>
                        Update
                      </button>
                    </td>

                  </tr>

                </form>
              </table>
            </div>
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
      out.println("Record Not Found");
    }
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
  }
%>
</body>
</html>

