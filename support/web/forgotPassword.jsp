<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company = (String)session.getAttribute("company");%>
<%@ page import="org.owasp.esapi.ESAPI" %>



<%


    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
        if(!Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/support/sessionout.jsp");
            return;
        }

    }
    else if(user!=null)
    {

        ctoken = user.getCSRFToken();

    }
    else
    {
        user =   new DefaultUser(User.ANONYMOUS.getName());
        ctoken = (user).resetCSRFToken();

    }

    session.setAttribute("Anonymous", user);

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title> Customer Support forgot password</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <link rel="stylesheet" type="text/css" href="/support/css/styles123.css"/>
    <link href="/support/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

</head>

<body <%--style="background-color:#ecf0f1; "--%>>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td align="center">

            <%--
                        <img src="/support/images/<%=session.getAttribute("logo")%>" border="0">
            --%>
            <img src="/support/images/pay1.png">

        </td>
    </tr>
</table>

<div class="row" style="margin-right:250px;MARGIN-TOP: -80PX; ">
    <div class="col-lg-12">
        <div class="panel panel-default" style="border:1px solid;border-radius:4px;">
            <div class="panel-heading" >
                Forgot Password?
            </div>
            <p>
            <table border="0" width="100%" align="center">
                <br><br><br>
                <tr>
                    <td class="textb" align="center" >Please fill in your username.A mail will be sent to you containing a new password</td>
                    </td>
                </tr>
                <tr>
                    <td class="textb" align="center" >Note:Please login using this password within one hour of receiving the mail.
                    </td>
                </tr>

            </table>
            <hr class="hrnew"><br><br>

            <form action="/support/servlet/ForgotPassword?ctoken=<%=ctoken%>" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table bgcolor="#2c3e50" align="center" width="400" cellpadding="2" cellspacing="2">
                    <tr>
                        <td>
                            <table border="0" bgcolor="#FFFFFF" width="100%" align="center" cellpadding="2" cellspacing="2">
                                <%
                                 if(request.getParameter("MES")!=null)
                                 {
                                     String mes=request.getParameter("MES");
                                     if("X".equals(mes))
                                     {
                                        out.println("<tr><td class\"textb\" colspan=\"4\"><center> * Username does not exist</center></td></tr><tr><td>&nbsp;</td></tr>");
                                     }
                                     if("F".equals(mes))
                                     {
                                         out.println("<tr><td class\"textb\" colspan=\"4\"><center> * Invalid username</center></td></tr><tr><td>&nbsp;</td></tr>");
                                     }
                                 }
                                %>
                                <tr>
                                    <td class="textb" colspan="4" >&nbsp;&nbsp;Forgot Password > Email<br><br></td>
                                </tr>
                                <tr>
                                    <td width="10" class="textb">&nbsp;</td>
                                    <td width="220" class="textb">Username</td>
                                    <td width="10" class="textb">:</td>
                                    <td width="260"><input class="txtbox" type="text" maxlength="100"  value="" name="username" size="30"></td>
                                </tr>

                                <tr><td>&nbsp;</td></tr>
                                <tr><td>&nbsp;</td>
                                <tr>
                                    <td colspan="4" align="center">
                                        <button type="submit" name="submit" class="buttonform">
                                            <span><i class="fa fa-mail-forward"></i></span>
                                            Send Mail
                                        </button>
                                    </td>
                                </tr>

                            </table>

                        </td>
                    </tr>
                </table>
            </form>
            <br>

            </p>
        </div>
    </div>
</div>
<p>&nbsp;&nbsp; </p>
</body>

</html>
