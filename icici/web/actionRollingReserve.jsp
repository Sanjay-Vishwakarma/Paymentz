<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.BankRollingReserveVO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 11/26/14
  Time: 3:53 PM
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
    <title></title>
    <link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
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
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

        String action = (String) request.getAttribute("action");
        String conf = " ";
        if (action != null)
        {
            if (action.equalsIgnoreCase("view"))
            {
                conf = "disabled";
            }

            BankRollingReserveVO bankRollingReserveVO = (BankRollingReserveVO)request.getAttribute("data");

            if (bankRollingReserveVO != null)
            {
                String style = "class=tr0";

%>
<div class="row" >
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Bank Rolling Reserve Action Manager
                <div style="float: right;">
                    <form  action="/icici/addNewBankRollingReserve.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Add Rolling Reserve">
                    </form>
                </div>
                <div style="float: right;">
                    <form  action="/icici/bankRollingReserveList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Rolling Reserve List">
                    </form>
                </div>
            </div>
            <%
                String errormsg1 = (String)request.getAttribute("errormessage");
                if (errormsg1 == null)
                {
                    errormsg1 = "";
                }
                else
                {
                    out.println("<table align=\"center\" class=\"textb\" ><tr><td valign=\"middle\"><font class=\"text\" >");

                    out.println(errormsg1);

                    out.println("</font></td></tr></table>");
                }
            %>
            <form action="/icici/servlet/ModifyBankRollingReserve?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" name="id" value="<%=ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getBankRollingReserveId())%>">


                <table border="0" cellpadding="5" cellspacing="0" width="90%" align="center">


                    <tbody>
                    <tr>
                        <td colspan="6">&nbsp;
                        </td>
                    </tr>
                    <tr>

                        <td colspan="3" class="textb" align="right">Account Id:</td>
                        <td colspan="3" class="textb" align="left">
                            <input type="text" size="30" name="" disabled value="<%=ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getAccountId())%>" >
                            <input type="hidden" size="30" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getAccountId())%>" >
                        </td>

                    </tr>
                    <tr><td colspan="6">&nbsp;</td>
                    <tr>
                        <td colspan="3" class="textb" align="right">Reserve Release Date:</td>
                        <td colspan="3" class="textb" align="left">
                            <input  class="datepicker textb" type="text"  name="rollingreservedateupto" <%=conf%> value="<%=ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getRollingReserveDateUpTo())%>">
                            <input  class="textb" type="text"  name="rollingRelease_Time" <%=conf%>  value="<%=ESAPI.encoder().encodeForHTML(bankRollingReserveVO.getRollingRelease_time())%>" >

                        </td>

                    </tr>


                    <tr><td colspan="6">&nbsp;</td>
                    <tr>



                        <td  colspan="6" class="textb" align="center">
                            <button type="submit" class="buttonform" <%=conf%>  >
                                <i class="fa fa-sign-in"></i>
                                &nbsp;&nbsp;Update
                            </button>
                        </td>
                    </tr>


                    </tbody>
                </table>


            </form>

        </div>
    </div>
</div>
<%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry","No records found."));
            }
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry","Invlid Action"));
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>

