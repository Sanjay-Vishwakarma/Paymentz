<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<%@ include file="functions.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.payment.MultipleMemberUtill" %>
<%@ page import="java.util.*" %>

<%--
Created by IntelliJ IDEA.
User: Sanjeet
Date: 15/4/2019
Time: 12:41 PM
To change this template use File | Settings | File Templates.
--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Member Child Signup</title>
</head>
<body>
<%
    Hashtable detailHash = (Hashtable) request.getAttribute("detailHash");
    if (detailHash != null)
    {
        detailHash = (Hashtable) request.getAttribute("detailHash");
    }
    String action = (String) request.getAttribute("action");
    String isreadonly = (String) request.getAttribute("action");
    String conf = "";

    if (isreadonly.equalsIgnoreCase("view"))
    {
        conf = "disabled";
    }
    else if (isreadonly.equalsIgnoreCase("modify"))
    {
        conf = "";
    }
    if (detailHash != null && detailHash.size() > 0)
    {
        String style = "class=tr0";

%>
<%
    MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Partner's Partner Master
                <div style="float: right;">
                    <form action="/icici/partnerChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" class="addnewmember" value="Add New Child Partner" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New User
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="forms">
                <br>
                <p align="right"><button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>Go Back</button>
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>

                            <%
                                if (request.getAttribute("error") != null)
                                {
                                    String message = (String) request.getAttribute("error");
                                    if (message != null)
                                        out.println("<center><font class=\"textb\">" + message + "</font></center><br/><br/>");
                                }
                            %>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" for="mid">Partner Id</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb">
                                        <input name="partnerid" id="pid1"
                                               value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("partnerid"))%>"
                                               class="txtbox" autocomplete="on">

                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
</div>

<div class="reporttable">

    <br>

    <form action="/icici/servlet/EditPartnerUserList?ctoken=<%=ctoken%>" method="POST"><input type="hidden" name="ctoken" value="<%=ctoken%>">
        <table border="1" align="center" style="width:50%"
               class="table table-striped table-bordered table-green dataTable">


            <tr <%=style%>>
                <td class="th0" colspan="2">Partner User Details</td>
            </tr>

            <tr <%=style%>>
                <td class="tr1">Parent PartnerId:</td>
                <td class="tr1"><input type="text" class="txtbox1"
                                       value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("partnerid"))%>"
                                       disabled></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Login:</td>
                <td class="tr1"><input type="text" class="txtbox1"
                                       value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("login"))%>"
                                       disabled></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Email Address</td>
                <td class="tr1"><input type="text" class="txtbox1" name="contact_emails"
                                       value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("emailaddress"))%>" <%=conf%> >
                </td>
            </tr>
</div>

<%
    out.println("<tr>");
    if (!conf.equalsIgnoreCase("disabled"))
        out.println("<td colspan='2' style='text-align:center'><input type=\"submit\" class=\"gotoauto\" name=\"modify\" value=\"Save\" " + conf + "><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"hidden\" name=\"memberid\" value=\"" + (String) detailHash.get("memberid") + "\"><input type=\"hidden\" name=\"login\" value=\"" + request.getAttribute("login") + "\"></td>");
    out.println("</tr>");
%>

<%

    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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
</table>
</form>
</body>
</html>

<%!
    public static String nullToStr(String str)
    {
        if (str == null)
            return "";
        return str;
    }
%>