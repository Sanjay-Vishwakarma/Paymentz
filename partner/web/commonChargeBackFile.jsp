<%--
  Created by IntelliJ IDEA.
  User: NAMRATA BARI
  Date: 16/09/19
  Time: 3:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp" %>
<html>
<head>
    <title>Result ChargeBack Upload</title>
</head>
<style>

    .tablestyle{
        font-family: Open Sans;
        font-size: 13px;
        font-weight: 600;
        width: 100%;
    }

    .table.table {
        clear: both;
        margin-top: 6px !important;
        margin-bottom: 6px !important;
        max-width: none !important;
        background-color: transparent;
    }

    .table-bordered {
        border: 1px solid #ddd;
    }

    .tdstyle{
        background-color: #7eccad !important;;
    }
</style>
<body>
<%
    user = (User) session.getAttribute("ESAPIUserSessionKey");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
%>
<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/uploadMerchantChargeback.jsp?ctoken=<%=ctoken%>" method="post" name="form">
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <br>
            <br>
            <div class="widget-header transparent">
                <%
                    String Msg = (String) request.getAttribute("msg");
                    String Result = (String) request.getAttribute("Result");
                %>
                <table align="center" width="50%" cellpadding="2" cellspacing="2">
                    <tr>
                        <td>
                            <table bgcolor="#ecf0f1" align="center" cellpadding="0" cellspacing="0">
                                <tr height=30>
                                    <td colspan="3" bgcolor="#7eccad" class="texthead" align="center"><font
                                            color="#FFFFFF"
                                            size="2"
                                            face="Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif">
                                        <%=Msg%>
                                    </font></td>

                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td align="center" class="textb"><%=Result%>
                                    </td>
                                </tr>
                                <tr>
                                    <td>&nbsp;</td>
                                </tr>
                            </table>
                    </tr>
                    </td> </table>

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
