<%@ page import="com.directi.pg.Functions" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 11/3/15
  Time: 03:50 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>
    <script language="javascript">
        $(document).ready(function() {

            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
                document.getElementById('passwd').value =  encryptedString1;

                var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
                document.getElementById('conpasswd').value =  encryptedString2;

                document.getElementById('isEncrypted').value =  true;
            });

        });
    </script>
    <title>Admin SignUp </title>
</head>
<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Add New Admin
                <div style="float: right;">
                    <form action="/icici/adminlist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="List Admin" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;List Admin
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="reporttable">
    <div align="center" class="textb"><h5><b><u>Admin Information</u></b></h5></div>
    <%
        String passwd = "";
        String conpasswd = "";
        Functions functions=new Functions();
        String username =request.getParameter("username");
        if(functions.isValueNull(username))
        {
            username="";
        }
        String contact_emails =request.getParameter("contact_emails");
        if(functions.isValueNull(contact_emails))
        {
            contact_emails="";
        }

        String errormsg=(String)request.getAttribute("message");
        if(errormsg!=null)
        {
            out.println("<center><font class=\"textb\" face=\"arial\">"+errormsg+"</font></center>");
        }

    %>
    <form action="/icici/servlet/NewAdminSignUp?ctoken=<%=ctoken%>" method="post" name="form1">
        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

        <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor=white align="center">
            <tr>
                <td>
                    <table border="0" cellpadding="5" cellspacing="0" width="90%"  align="center" style="margin-left:100px">
                        <tr><td colspan="4">&nbsp;</td></tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"><span class="textb">Username*</span><br>
                                (Username Should Not Contain Special Characters like !@#$%)</td>
                            <td width="20%" class="textb">:</td>
                            <td width="20%"><input class="txtbox" type="Text" maxlength="100"  maxlength = 100 value="" name="username" size="35"></td>
                        </tr>

                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>

                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"><span class="textb">Password*</span><br>
                                (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)</td>
                            <td class="textb">:</td>
                            <td><input id="passwd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                       value="" name="passwd" size="35"></td>

                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>

                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"><span class="textb">Confirm Password*</span><br>
                                (Should be same as PASSWORD)</td>
                            <td class="textb">:</td>
                            <td><input id="conpasswd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                       value="" name="conpasswd" size="35"></td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <hr class="hrnew" style="margin-left: 61;margin-right: 135;">
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb">Contact email address*</td>
                            <td class="textb">:</td>
                            <td><input class="txtbox" type="Text" maxlength="100" value="" name="contact_emails" size="35"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td>
                                <input type="hidden" value="1" name="step">
                                <button type="submit" name="submit" class="buttonform" id="submit" disabled="disabled">
                                    <i class="fa fa-save"></i>
                                    Submit
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td></td>
                        </tr>

                    </table>
                </td>
            </tr>
        </table>
    </form>
    <p class="textb" align="center" style="font-weight: bold;">KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES</p>

    <%
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
    %>
</div>
</body>
</html>