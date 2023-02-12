<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 4/26/13
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="index.jsp" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<html>
<head>
    <title>Common Integration> Common Settlement Cron</title>
    <script type="text/javascript" language="JavaScript">
        function check() {
            var  retpath = document.FIRCForm.File.value;
            var pos = retpath.lastIndexOf(".");
            var filename="";
            if (pos != -1)
                filename = retpath.substring(pos + 1);
            else
                filename = retpath;

            if (filename==('xls')) {

                return true;
            }
            alert('Please select a .xls file instead!');
            return false;
        }
    </script>
</head>
<body>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        //Hashtable accountHash = GatewayAccountService.getCommonAccountDetail();
        String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
%>
<form name = "FIRCForm" action="/icici/commonsettlementfile.jsp?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Settlement File
                </div>
                <br>
                <table  border="0" cellpadding="5" cellspacing="0" align="center">
                    <tr>
                        <td>
                            <table  border="0" cellpadding="5" cellspacing="0" align="center">
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">
                                        Account ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
                                    </td>
                                    <td>
                                        <input name="accountid" id="accountid" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                        <%-- <select size="1" name="accountid" class="txtbox" style="width: 480px">
                                                <option value="">All</option>
                                                <%
                                                    Enumeration enu3 = accountHash.keys();
                                                    while (enu3.hasMoreElements())
                                                    {
                                                        Integer accountId = (Integer) enu3.nextElement();
                                                        GatewayAccount account = (GatewayAccount) accountHash.get(accountId);
                                                        String currency = account.getCurrency();
                                                        String merchantId = account.getMerchantId();
                                                        String displayName = account.getDisplayName();
                                                        String gateway = account.getGateway().toUpperCase();
                                                        String value = accountId + " - " + currency + " - " + merchantId + " ( " + gateway + " - " + displayName + " ) ";
                                                %>
                                                <option value="<%=accountId%>"><%=value%>
                                                </option>
                                                <%
                                                    }
                                                %>
                                            </select>--%>
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="textb">
                                        Settlement File :&nbsp;&nbsp;&nbsp;
                                    </td>
                                    <td>
                                        <input name="File" type="file" value="choose File">
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="2" align="right">
                                        <button name="mybutton" type="submit" value="Upload" class="buttonform"
                                                onclick="return check()">Upload
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                    <td>

                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</form>
<%
    String message=(String)request.getAttribute("res");
    if(message!=null)
    {
        out.println(Functions.ShowMessage("Message",message));
    }
%>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>