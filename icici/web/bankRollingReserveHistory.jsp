<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.BankRollingReserveVO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 12/08/14
  Time: 1:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="/icici/styyle.css" >
    <link rel="stylesheet" href="/resources/demos/style.css">
    <link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
    <title>Bank Rolling Reserve History</title>

    <script src="/icici/olddatepicker1/jquery-1.9.1.js"></script>
    <script src="/icici/olddatepicker1/jquery-ui.js"></script>
    <link rel="stylesheet" href="/resources/demos/style.css">
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'dd/mm/yy'});
        });
    </script>
</head>
<body>
<%!
    private static Logger logger=new Logger("actionRollingReserve.jsp");

%>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Bank Rolling Reserve Manager
                <div style="float: right;">
                    <form  action="/icici/addNewBankRollingReserve.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Add Rolling Reserve">
                    </form>
                </div>
                <div style="float: right;">
                    <form  action="/icici/bankRollingReserveList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Rolling Reserve List " >
                    </form>
                </div>
            </div>
            <%
                String action = (String) request.getAttribute("action");
                if (action != null)
                {
                    List<BankRollingReserveVO> bankRollingReserveVOList = (List<BankRollingReserveVO>)request.getAttribute("data");

                    if (bankRollingReserveVOList.size()>0)
                    {
                        int i=1;
            %>
            <center><h4 class="textb"><b>Bank Rolling Reserve Release History</b></h4></center>
            <table align=center class="table table-striped table-bordered table-green dataTable">
                <tr>
                    <td valign="middle" align="center" class="th0" >Sr. No</td>
                    <td valign="middle" align="center" class="th0" >Account ID</td>
                    <td valign="middle" align="center" class="th0" >MID</td>
                    <td valign="middle" align="center" class="th0" >Rolling Release Date Upto</td>
                </tr>
                <%

                    for(BankRollingReserveVO bankRollingReserveVO:bankRollingReserveVOList)
                    {
                        String style = "class=td1";
                        out.println("<tr>");
                        out.println("<td " + style + " >&nbsp;" + i + "</td>");
                        out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getAccountId()) + "</td>");
                        out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getMerchantId()) + "</td>");
                        out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getBankRollingReserveDateTime()) + "</td>");
                        out.println("</tr>");
                        i = i + 1;
                    }

                %>
            </table>
            <%
                        }
                        else
                        {
                            out.println(Functions.NewShowConfirmation("Sorry","No records found."));
                        }
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation("Sorry","Invalid Action"));
                    }
                }
                else
                {
                    response.sendRedirect("/icici/logout.jsp");
                    return;
                }
            %>

        </div>
    </div>
</div>
</body>
</html>

