<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 9/9/14
  Time: 2:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Add New Fraud System
                <div style="float: right;">
                    <form action="/icici/listFraudSystem.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Fraud System Master
                        </button>
                    </form>
                </div>
            </div>
            <%
                List errorList=(List)request.getAttribute("errorList");
                if(errorList!=null)
                {
                    out.println("<table align=\"center\" font class=\"textb\"><tr><td><b>");
                    out.println(errorList);
                    out.println("</b></font></td></tr></table>");
                }
            %>
            <form action="/icici/servlet/AddNewFraudSystem?ctoken=<%=ctoken%>" method="post" name="forms" >

                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <table align="center" width="65%" cellpadding="2" cellspacing="2">

                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">Fraud System Name*</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <input class="txtbox" maxlength="25" type="text" name="fsname">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Contact Person(Name)*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="25" type="text" name="contactperson"></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Contact Email*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="25" type="text" name="contactemail" ></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Website Url*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="100" type="text" name="weburl"></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Offline Fraud Check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="offline" class="txtbox">
                                            <option value="Y">Y</option>
                                            <option value="N" selected="">N</option>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Online Fraud Check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="online">
                                            <option value="Y">Y</option>
                                            <option value="N" selected="">N</option>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is API Call Supported</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="apicall">
                                            <option value="Y">Y</option>
                                            <option value="N" selected="">N</option>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb"></td>
                                    <td style="padding: 3px" width="5%" class="textb"></td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <button type="submit" class="buttonform">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
<%
        if(request.getAttribute("message")!=null)
        {
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Result", (String) request.getAttribute("message")));
            out.println("<div>");
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