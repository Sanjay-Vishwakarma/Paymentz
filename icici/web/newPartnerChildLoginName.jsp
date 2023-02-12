<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.payment.MultiplePartnerUtill" %>
<%@ page import="com.directi.pg.Admin" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 15/4/2019
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Partner Child Signup</title>
</head>
<body>
<%
    MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    session.setAttribute("submit", "User Management");

    /*if (partner.isLoggedInPartner(session))*/
    if (Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Partner's Partner Master
                <div style="float: right;">
                    <form action="/icici/partnerChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Child Merchant" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New User
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="forms">
                <br>
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" for="mid">Partner Id</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb">
                                        <input name="partnerid" id="mid" value="<%=session.getAttribute("merchantid")%>"
                                               class="form-control" autocomplete="on">
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


<div class="row reporttable">
    <div class="col-md-8">
        <div class="widget">
            <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;Partner Information (New Login)</h2>
                <div class="additional-btn">
                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
            </div>
            <div class="widget-content padding">
                <div id="horizontal-form">
                    <br>
                    <%
                        String errormsg = (String) request.getAttribute("error");
                        if (errormsg != null)
                        {
                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                        }
                    %>
                    <form action="/icici/servlet/NewChildPartnerSignUp?ctoken=<%=ctoken%>" method="post"
                          name="form1" class="form-horizontal">
                        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
                        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false">
                        <div class="form-group">
                            <label class="col-sm-4 control-label">New Login*</label>
                            <div class="col-sm-6">
                                <input type="text" maxlength="100" class="form-control" value=""
                                       name="username">
                            </div>
                        </div>
                        <input type="hidden" value="3" name="step">
                        <div class="form-group col-md-4"></div>
                        <div class="form-group col-md-4">
                            <button type="submit" name="submit" class="btn btn-default" id="submit"
                            <i class="fa fa-save"></i> Submit </button>
                        </div>
                    </form>
                    <%

                    %>
                </div>
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
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>
