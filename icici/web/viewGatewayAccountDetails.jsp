<%@ page import="com.directi.pg.Database,
                 com.directi.pg.Functions"%>
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
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css">
<%--
  Created by IntelliJ IDEA.
  User: mukesh
  Date: 2/03/14
  Time: 11:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title></title>
    <style>
        .txtbox1{
            width: 300px;
        }
    </style>
    <script>
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
</head>
<body>
<%!
    private static Logger logger=new Logger("viewGatewayAccountDetails.jsp");
%>

<%!
    String selected;
    HashMap<String,String> dropdown= new HashMap<String, String>();
    HashMap<String,String> dropdown1 = new HashMap<String, String>();
    HashMap<String,String> dropdown2 = new HashMap<String, String>();
%><%
    Functions functions = new Functions();
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
    ResourceBundle gatewayLabelRB= LoadProperties.getProperty("com.directi.pg.GatewayLabel");
    dropdown.put("Y","Y");
    dropdown.put("N","N");

    dropdown1.put("1","Y");
    dropdown1.put("0","N");

    dropdown2.put("N","N");
    dropdown2.put("account_Level","Account Level");
    dropdown2.put("mid_Level","MID Level");
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Gateway Account Details
                <div style="float: right;">
                    <form action="/icici/gatewayAccountInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember"  name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;GateWay Account Master
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <form action="/icici/servlet/editGatewayAccountsDetails?ctoken=<%=ctoken%>" method="POST"> <input type="hidden" name="ctoken" value="<%=ctoken%>">
                <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>
                <table border="1" align="center" style="width:60%" class="table table-striped table-bordered table-green dataTable">
                    <%
                        String message=(String)request.getAttribute("message");
                        String accountid = (String) request.getAttribute("accountid");
                        String action = (String) request.getAttribute("action");
                        if(message!=null)
                        {
                            out.println("<center><font class=\"textb\"><b>"+message+"</b></font></center>");
                        }

                        Hashtable hash = (Hashtable) request.getAttribute("gatewayaccountdetails");
                        String isreadonly =(String) request.getAttribute("isreadonly");
                        String table_data =(String) request.getAttribute("table_data");
                        String columnName = (String) request.getAttribute("table_column");
                        String conf = " ";
                        String gateway_table_name = (String)request.getAttribute("gateway_table_name");
                        String role="Admin";
                        String username=(String)session.getAttribute("username");
                        String actionExecutorId=(String)session.getAttribute("merchantid");
                        String actionExecutorName=role+"-"+username;
                        String gateway = (String)request.getAttribute("gateway");
                        String currency = (String)request.getAttribute("currency");
                        JSONObject gatewayLabelJSON=new JSONObject();
                        try{
                            String json=gatewayLabelRB.getString(gateway);
                            if(functions.isValueNull(json) && json.contains("{"))
                                gatewayLabelJSON=new JSONObject(json);
                        }catch (Exception e){
                            logger.error("Property not found-->",e);
                        }
                        Hashtable innerhash = new Hashtable();
                        if(isreadonly.equalsIgnoreCase("view"))
                        {
                            conf = "disabled";
                        }
                        if (hash != null && hash.size() > 0)
                        {
                            String style="class=tr0";
                            innerhash = (Hashtable) hash.get(1 + "");
                    %>

                    <input type="hidden" name="columnnames" value=<%=columnName%>>
                    <input type="hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("accountid"))%>">
                    <input type="hidden" name="merchantid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("merchantid"))%>">
                    <input type="hidden" name="pgtypeid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("pgtypeid"))%>">
                    <input type="hidden" name="gateway_table_name" value="<%=ESAPI.encoder().encodeForHTML(gateway_table_name)%>">

                    <tr <%=style%>>
                        <td class="th0" colspan="2">GatewayAccount Details</td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">AccountType Id: </td>
                        <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("accountid"))%>" disabled ></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Pgtypeid: </td>
                        <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("pgtypeid"))%>-<%=gateway%>-<%=currency%>" disabled ></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">
                            <%if(gatewayLabelJSON.has("merchantId")){
                                out.println(gatewayLabelJSON.getString("merchantId")+":");
                            %>
                            <%}else{%>Merchant Id: <%}%></td>
                        <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("merchantid"))%>" disabled ></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Alias Name*: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="aliasname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("aliasname"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Alias Name')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Display Name*: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="displayname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("displayname"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Display Name')"> </td>
                        <input type="hidden" name="0ld_displayname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("displayname"))%>" <%=conf%>>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Is MasterCardSupported: </td>
                        <td class="tr1">
                            <select type="text" name="ismastercardsupported" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("ismastercardsupported"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is MasterCardSupported')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown1.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("ismastercardsupported")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">
                            <%if(gatewayLabelJSON.has("shortName")){
                                out.println(gatewayLabelJSON.getString("shortName")+":");
                            %>
                            <%}else{%>Short Name:<%}%> </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="shortname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("shortname"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Short Name')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">
                            <%if(gatewayLabelJSON.has("site")){
                                out.println(gatewayLabelJSON.getString("site")+":");
                            %>
                            <%}else{%><%--Site--%>Back Office URL:<%}%> </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="site" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("site"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Site')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">
                            <%if(gatewayLabelJSON.has("path")){
                                out.println(gatewayLabelJSON.getString("path")+":");
                            %>
                            <%}else{%>Path:<%}%> </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="path" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("path"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Path')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">
                            <%if(gatewayLabelJSON.has("username")){
                                out.println(gatewayLabelJSON.getString("username")+":");
                            %>
                            <%}else{%><%--Username--%>API Name:<%}%> </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("username"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Username')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">
                            <%if(gatewayLabelJSON.has("password")){
                            out.println(gatewayLabelJSON.getString("password")+":");
                            %>
                            <%}else{%>API KEY:<%}%> </td>
                        <td class="tr1"><input type="text" size="100" class="txtbox1" name="passwd" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("passwd"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Password')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">
                            <%if(gatewayLabelJSON.has("chargeBackPath")){
                                out.println(gatewayLabelJSON.getString("chargeBackPath")+":");
                            %>
                            <%}else{%>ChargeBack Path:<%}%> </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="chargeback_path" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("chargeback_path"))%>" <%=conf%> onchange="ChangeFunction(this.value,'ChargeBack Path')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Is CVV Required: </td>
                        <td class="tr1">
                            <select type="text" name="isCVVrequired" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isCVVrequired"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is CVV Required')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("isCVVrequired")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Card Limit Check: </td>
                        <td class="tr1">
                            <select type="text" name="cardLimitCheck" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("cardLimitCheck"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Card Limit Check')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown2.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("cardLimitCheck")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Card Amount Limit Check: </td>
                        <td class="tr1">
                            <select type="text" name="cardAmountLimitCheck" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("cardAmountLimitCheck"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Card Amount Limit Check')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown2.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("cardAmountLimitCheckAcc")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Amount Limit Check: </td>
                        <td class="tr1">
                            <select type="text" name="amountLimitCheck" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("amountLimitCheck"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Amount Limit Check')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown2.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("amountLimitCheckAcc")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Daily Amount Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="daily_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_amount_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Daily Amount Limit')">
                                <select style="margin-left: 10px" type="text" name="Daily_Account_Amount_Limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("Daily_Account_Amount_Limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Daily Account Amount Limit')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("Daily_Account_Amount_Limit")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                                </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Daily Amount Range: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="daily_amount_range" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("daily_amount_range"))%>" <%=conf%> onchange="ChangeFunction(this.value, 'Daily Amount Range')">
                            <select style="margin-left: 10px" type="text" name="daily_amount_range_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_amount_range_check"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Daily Amount Range Check')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair: dropdown.entrySet())
                                    {
                                        selected= "";
                                        if (yesNoPair.getKey().equals(innerhash.get("daily_amount_range_check")))
                                            selected="selected";
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Weekly Amount Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="weekly_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_amount_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Weekly Amount Limit')">
                                <select style="margin-left: 10px" type="text" name="Weekly_Account_Amount_Limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("Weekly_Account_Amount_Limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Weekly Account Amount Limit')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("Weekly_Account_Amount_Limit")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                                </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Monthly Amount Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="monthly_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_amount_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Monthly Amount Limit')">
                                <select style="margin-left: 10px" type="text" name="Monthly_Account_Amount_Limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("Monthly_Account_Amount_Limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Monthly Account Amount Limit')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("Monthly_Account_Amount_Limit")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Daily Card Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="daily_card_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Daily Card Limit')">
                                <select style="margin-left: 10px" type="text" name="daily_card_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_limit_check"))%>" <%=conf%> onchange="ChangeFunction(this.value,'daily card limit check')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("daily_card_limit_check")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                                </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Weekly Card Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="weekly_card_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Weekly Card Limit')">
                                <select style="margin-left: 10px" type="text" name="weekly_card_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_limit_check"))%>" <%=conf%> onchange="ChangeFunction(this.value,'weekly card limit check')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("weekly_card_limit_check")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Monthly Card Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="monthly_card_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Monthly Card Limit')">
                                <select style="margin-left: 10px" type="text" name="monthly_card_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_limit_check"))%>" <%=conf%> onchange="ChangeFunction(this.value,'monthly card limit check')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("monthly_card_limit_check")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                                </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Min Transaction Amount: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="min_transaction_amount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("min_transaction_amount"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Min Transaction Amount')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Max Transaction Amount: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="max_transaction_amount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("max_transaction_amount"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Max Transaction Amount')"> </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Daily Card Amount Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="daily_card_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_amount_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Daily Card Amount Limit')">
                                <select style="margin-left: 10px" type="text" name="daily_card_amount_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_amount_limit_check"))%>" <%=conf%> onchange="ChangeFunction(this.value,'daily card amount limit check')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("daily_card_amount_limit_check")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                                </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Weekly Card Amount Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="weekly_card_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_amount_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Weekly Card Amount Limit')">
                                <select style="margin-left: 10px" type="text" name="weekly_card_amount_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_amount_limit_check"))%>" <%=conf%> onchange="ChangeFunction(this.value,'weekly card amount limit check')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("weekly_card_amount_limit_check")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                                </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Monthly Card Amount Limit: </td>
                        <td class="tr1">
                            <input type="text" class="txtbox1" name="monthly_card_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_amount_limit"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Monthly Card Amount Limit')">
                                <select style="margin-left: 10px" type="text" name="monthly_card_amount_limit_check" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_amount_limit_check"))%>" <%=conf%> onchange="ChangeFunction(this.value,'monthly card amount limit check')">
                                    <%
                                        for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                        {
                                            selected="";
                                            if(yesNoPair.getKey().equals(innerhash.get("monthly_card_amount_limit_check")))
                                                selected="selected";
                                            out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                        }
                                    %>
                                </select>
                        </td>
                    </tr>
                    <%--<tr <%=style%>>
                        <td class="tr1">Is Test Account: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="istest" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("istest"))%>" <%=conf%>> </td>
                    </tr>--%>
                    <tr <%=style%>>
                    <td class="tr1">Is Test Account: </td>
                    <td class="tr1">
                        <select type="text" name="istest" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("istest"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is Test Account')">
                            <%
                                for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                {
                                    selected="";
                                    if(yesNoPair.getKey().equals((String) innerhash.get("istest")))
                                    {
                                        selected="selected";
                                    }
                                    out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                }
                            %>
                        </select>
                    </td>
                    </tr>
                    <%--<tr <%=style%>>
                        <td class="tr1">Is Active Account: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="isactive" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isactive"))%>" <%=conf%>> </td>
                    </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Is Active Account: </td>
                        <td class="tr1">
                            <select type="text" name="isactive" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isactive"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is Active Account')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("isactive")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1"> Emi Support: </td>
                        <td class="tr1">
                            <select type="text" name="emiSupport" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("emiSupport"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Emi Suppor')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("emiSupport")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Is Multiple Refund: </td>
                        <%--<td class="tr1"><input type="text" class="txtbox1" name="isMultipleRefund" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isMultipleRefund"))%>" <%=conf%>> </td>--%>
                        <td class="tr1">
                            <select type="text" name="isMultipleRefund" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isMultipleRefund"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is Multiple Refund')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("isMultipleRefund")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Partial Refund: </td>
                        <%--<td class="tr1"><input type="text" class="txtbox1" name="isMultipleRefund" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isMultipleRefund"))%>" <%=conf%>> </td>--%>
                        <td class="tr1">
                            <select type="text" name="PartialRefund" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("PartialRefund"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Partial Refund')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("PartialRefund")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">Is Recurring Allowed: </td>
                        <td class="tr1">
                            <select type="text" name="isrecurring" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("is_recurring"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is Recurring Allowed')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("is_recurring")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                     <%--<tr <%=style%>>
                        <td class="tr1">Address Validation: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="addressValidation" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("addressValidation"))%>" <%=conf%>> </td>
                    </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Address Validation: </td>
                        <td class="tr1">
                            <select type="text" name="addressValidation" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("addressValidation"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Address Validation')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("addressValidation")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>


                    <tr <%=style%>>
                        <td class="tr1">Partner : </td>
                        <td class="tr1">
                            <select name="partnerid" class="txtbox" disabled >
                                <option value=""></option>
                                <%
                                    Connection conn=null;
                                    try
                                    {
                                        conn = Database.getConnection();
                                        String query = "SELECT partnerid,partnerName FROM partners WHERE activation='T' ORDER BY partnerid ASC";
                                        PreparedStatement pstmt = conn.prepareStatement( query );
                                        ResultSet rs = pstmt.executeQuery();
                                        while (rs.next())
                                        {
                                            selected="";
                                            if(rs.getString("partnerid").equalsIgnoreCase((String) innerhash.get("partnerid")))
                                            {
                                                selected="selected";
                                            }
                                            out.println("<option value=\""+rs.getInt("partnerid")+"\" "+selected+">"+rs.getInt("partnerid")+" - "+rs.getString("partnerName")+"</option>");
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        logger.error("Exception::::"+e);
                                    }
                                    finally
                                    {
                                        Database.closeConnection(conn);
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Agent : </td>
                        <td class="tr1">
                            <select name="agentid" class="txtbox" disabled >
                                <option value=""></option>
                                <%
                                    try
                                    {
                                        conn = Database.getConnection();
                                        String query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
                                        PreparedStatement pstmt = conn.prepareStatement(query);
                                        ResultSet rs = pstmt.executeQuery();
                                        while (rs.next())
                                        {
                                            selected = "";
                                            if (rs.getString("agentid").equalsIgnoreCase((String) innerhash.get("agentid")))
                                            {
                                                selected = "selected";
                                            }
                                            out.println("<option value=\"" + rs.getInt("agentid") + "\" " + selected + ">" + rs.getInt("agentid") + " - " + rs.getString("agentName") + "</option>");
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        logger.error("Exception::::"+e);
                                    }
                                    finally
                                    {
                                        Database.closeConnection(conn);
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Gateway Table Name: </td>
                        <td class="tr1"><input type="text" class="txtbox1" value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML(gateway_table_name))?ESAPI.encoder().encodeForHTML(gateway_table_name):""%>" disabled ></td>
                    </tr>

                    <%--<tr <%=style%>>
                        <td class="tr1">is Dynamic Descriptor: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="isDynamicDescriptor" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isDynamicDescriptor"))%>" <%=conf%>> </td>
                    </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Is Dynamic Descriptor: </td>
                        <td class="tr1">
                            <select type="text" name="isDynamicDescriptor" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isDynamicDescriptor"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is Dynamic Descriptor')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("isDynamicDescriptor")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>

                    <tr <%=style%>>
                        <td class="tr1">is 3D Supported: </td>
                        <td class="tr1"><select class="txtbox1" name='3dSupportAccount' class="txtboxsmall" <%=conf%> onchange="ChangeFunction(this.value,'is 3D Supporte')"><%out.println(Functions.comboval1(ESAPI.encoder().encodeForHTML((String) innerhash.get("3dSupportAccount"))));%></select></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">3Ds Version: </td>
                        <td class="tr1"><select class="txtbox1" name='threeDsVersion' class="txtboxsmall" <%=conf%> onchange="ChangeFunction(this.value,'3Ds Version')"><%out.println(Functions.comboval3DsVersion(ESAPI.encoder().encodeForHTML((String) innerhash.get("threeDsVersion"))));%></select></td>
                    </tr>
                    <%--<tr <%=style%>>
                        <td class="tr1">is FX Mid: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="isForexMid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isForexMid"))%>" <%=conf%>> </td>
                    </tr>--%>
                    <tr <%=style%>>
                        <td class="tr1">Is FX Mid: </td>
                        <td class="tr1">
                            <select type="text" name="isForexMid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isForexMid"))%>" <%=conf%> onchange="ChangeFunction(this.value,'Is FX Mid')">
                                <%
                                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                                    {
                                        selected="";
                                        if(yesNoPair.getKey().equals((String) innerhash.get("isForexMid")))
                                        {
                                            selected="selected";
                                        }
                                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>
                    <%
                        String fromAccountid="";
                        if(functions.isValueNull((String) innerhash.get("fromAccountId")))
                        {
                            fromAccountid=((String)innerhash.get("fromAccountId"));
                        }
                        else
                        {
                            fromAccountid="";
                        }
                        String fromMid="";
                        if(functions.isValueNull((String) innerhash.get("fromMid")))
                        {
                            fromMid=((String)innerhash.get("fromMid"));
                        }
                        else
                        {
                            fromMid="";
                        }
                    %>
                    <tr <%=style%>>
                        <td class="tr1"><%--From Account ID--%> Back Office User Name: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="fromAccountId" value="<%=ESAPI.encoder().encodeForHTML(fromAccountid)%>" <%=conf%> onchange="ChangeFunction(this.value,'From Account ID')"> </td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1"><%--From MID--%>  Back Office Password: </td>
                        <td class="tr1"><input type="text" class="txtbox1" name="fromMid" value="<%=ESAPI.encoder().encodeForHTML(fromMid)%>" <%=conf%> onchange="ChangeFunction(this.value,'From MID')"> </td>
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
                    <tr <%=style%>>
                        <td class="tr1">Action Executor Id : <br>
                        <td class="tr1"><input  type="Text"  class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorId)%>" name="actionExecutorId" id="actionExecutorId" size="50" <%=conf%> disabled></td>
                    </tr>
                    <tr <%=style%>>
                        <td class="tr1">Action Executor Name: <br>
                        <td class="tr1"><input  type="Text"  class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML(actionExecutorName)%>" name="actionExecutorName" id="actionExecutorName" size="50" <%=conf%> disabled></td>
                    </tr>

                    <%
                        String pgtypeid = (String) request.getAttribute("pgtypeid");
                        GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeid);
                        if(table_data.length() > 0)
                        {
                    %>
                    <tr <%=style%>>
                        <td class="th0" colspan="2"> <%=gatewayType.getGateway() %> GateWay Data Details</td>
                    </tr>
                    <tr <%=style%>>
                        <%=table_data%>
                    </tr>
                    <%
                            }
                            out.println("<tr>");
                            if(!conf.equalsIgnoreCase("disabled"))
                                out.println("<td colspan='2' style='text-align:center'><input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\"><input type=\"hidden\" name=\"action\" value=\""+action+"\"><input type=\"submit\" class=\"gotoauto\" name=\"modify\" value=\"Save\" "+conf+"></td>");
                            out.println("</tr>");
                        }
                    %>
                </table>
            </form>
                <%--<%
                    out.println(Functions.ShowMessage("Message","No record found"));
                %>--%>
</body>
</html>