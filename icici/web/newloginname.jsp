<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 4/19/14
  Time: 12:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

<center><h2>Member Master</h2></center>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Member Master
                <div style="float: right;">
                    <form action="/icici/membersignup.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" class="addnewmember" value="Add New Member" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Member
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/MemberDetailList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:-2%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Merchant Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="memberid"  value="<%=request.getParameter("memberid")==null?"":request.getParameter("memberid")%>">

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Username</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="username"  value="<%=request.getParameter("username")==null?"":request.getParameter("username")%>">

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="15%" class="textb" >Contact&nbsp;Person&nbsp;Name</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input  type="text" name="contact_persons" value="<%=request.getParameter("contact_persons")==null?"":request.getParameter("contact_persons")%>">

                                    </td>
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb" >Contact&nbsp;Person&nbsp;Email</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="contact_emails"  value="<%=request.getParameter("contact_emails")==null?"":request.getParameter("contact_emails")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Company Name</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="company_name"  value="<%=request.getParameter("company_name")==null?"":request.getParameter("company_name")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" value="Search" class="buttonform" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>


                                </tr>


                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>

                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>

        </div>
    </div>
</div>
<div class="reporttable">
<table  align="center" width="50%" cellpadding="2" cellspacing="2">

    <form action="/icici/servlet/NewMemberSignUp?ctoken=<%=ctoken%>" method="post" name="form1">

        <tr>
            <td>

                <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">

                  <%--  <tr>
                        <td width="2%" class="textb">&nbsp;</td>
                        <td colspan="3" class="label">
                            Merchant Information (New Login)
                        </td>

                    </tr>--%>
                       <%
                           String errormsg=(String)request.getAttribute("error");
                           if(errormsg!=null)
                           {
                               out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");
                           }
                       %>

                    <tr><td colspan="4">&nbsp;</td></tr>
                    <tr>
                        <td width="2%" class="textb">&nbsp;</td>
                        <td width="43%" class="textb" valign="top">New Login *</td>
                        <td width="5%" class="textb" valign="top">:</td>
                        <td width="50%" valign="top">
                            <input type="text" maxlength="100" class="txtbox" value="" name="username">
                        </td>
                    </tr>
                    <input type="hidden" value="3" name="step">
                    <tr>

                        <td width="2%" class="textb">&nbsp;</td>
                        <td width="43%" class="textb" valign="top"></td>
                        <td width="5%" class="textb" valign="top"></td>
                        <td width="50%" valign="top">
                            <br>
                            <input type="Submit" value="Submit" name="submit" class="buttonform">
                        </td>

                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                </table>
            </td>
        </tr>
    </form>
</table>
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