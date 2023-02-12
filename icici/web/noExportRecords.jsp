<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Admin" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ include file="functions.jsp"%>
<%@ include file="rollingReserveManager.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: WAHEED
  Date: 2/11/14
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <%--Datepicker css format--%>
    <link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
    <script src="/icici/olddatepicker1/jquery-1.9.1.js"></script>
    <script src="/icici/olddatepicker1/jquery-ui.js"></script>
    <link rel="stylesheet" href="/resources/demos/style.css">
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();
//            $("#yourinput").datepicker( "setDate" , "7/11/2005" );

        });
    </script>
    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String str="ctoken=" + ctoken;
    if (Admin.isLoggedIn(session))
    {
        //Hashtable accountidhash= GatewayAccountService.getAllAccountDetails();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        String accountid = Functions.checkStringNull(request.getParameter("accountid"));
     %>   <form name = "FIRCForm" action="/icici/servlet/ExportReserveFile?ctoken=<%=ctoken%>" method="post"  >
<center>
    <table  border="1" cellpadding="5" cellspacing="0" bgcolor="#CCE0FF" align="center">
        <tr><td>
            <table  border="0" cellpadding="5" cellspacing="0" bgcolor="#CCE0FF" align="center">
                <tr>
                    <td>
                        Account ID * :
                    </td>
                    <td>
                        <select size="1" name="accountid" class="textBoxes">
                            <%

                                for(Integer sAccid : accountDetails.keySet())
                                {
                                    GatewayAccount g = accountDetails.get(sAccid);
                                    String selected3 = "";
                                    //String aGateway = accountMap.get(sAccountID).getGateway().toUpperCase()+"-"+accountMap.get(sAccountID).getCurrency()+"-"+accountMap.get(sAccountID).getGateway_id();


                                    if (String.valueOf(sAccid).equals(accountid))
                                        selected3 = "selected";
                                    else
                                        selected3 = "";

                                /*Enumeration enu3 = accountidhash.keys();
                                String selected3 = "";
                                String key3 = "";
                                String value3 = "";
                                while (enu3.hasMoreElements())
                                {
                                    key3 = (String) enu3.nextElement();
                                    value3 = (String) accountidhash.get(key3);
                                    if (key3.equals(accountid))
                                        selected3 = "selected";
                                    else
                                        selected3 = "";*/
                            %>
                            <option value="<%=sAccid%>" <%=selected3%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                            <%
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>From : &nbsp;&nbsp;&nbsp;<input name="fromDate" type="text"  readonly class="datepicker" value="<%= request.getAttribute("fromDate").toString() %>"></td>
                    <td>&nbsp;&nbsp;&nbsp;To : &nbsp;&nbsp;&nbsp;&nbsp;<input name="toDate" type="text" readonly class="datepicker" value="<%= request.getAttribute("toDate").toString() %>"></td>
                </tr>
                <tr>
                    <td valign="left" >Day Light Saving :
                        <select size="1" name="time_difference_daylight" class="textBoxes">
                            <option value="N">No</option>
                            <option value="Y" <%if("Y".equals(request.getAttribute("isDaylight"))){%> selected <%}%>>Yes</option>
                        </select>
                    </td>
                    <td align="right">Time Difference Normal : &nbsp;&nbsp;&nbsp;<input name="time_difference_normal" type="text" value="<%= request.getAttribute("time_difference_normal").toString() %>" disabled=""></td>
                </tr>
                    <td colspan="2" align="center">
                        <input name="mybutton" type="submit" value="View" >
                    </td>
                    <td colspan="2" align="center">
                        <input name="mybutton" type="submit" value="Generate Excel File" >
                    </td>
                </tr>
            </table>
        </td></tr>
    </table>
</center>
</form>
<br>
<%
        out.println(ShowConfirmation("Sorry","No records found."));
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>