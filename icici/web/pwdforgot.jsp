<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company = ApplicationProperties.getProperty("COMPANY");%>
<%@ page import="org.owasp.esapi.ESAPI" %>

<%

    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
       if(!Functions.validateCSRFAnonymos(ctoken,user))
       {
           response.sendRedirect("/icici/sessionout.jsp");
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
    <title><%=company%> Merchant Settings</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>

<body>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">

            <img src="<%=(String)application.getAttribute("merchantpath")%>/images/logo.jpg" border="0">

        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
</table>

<p class="title"><%=company%> Admin Settings</p>

<p>
<table border="0" width="750" align="center">

    <tr>
        <td class="subtitle1" align="center" >Forgot Password?</td>
    </tr>
    <tr>
        <td class="text" align="center" >Please fill in your username.A mail will be sent to you containing a new password</td>
    </td>
</tr>
<tr>
    <td class="textb" align="center" >Note:Please login using this password within one hour of receiving the mail.
    </td>
</tr>

</table>
<hr color="#007ACC" width="600" align="center" size="1"><br><br>

<form action="/icici/servlet/PwdForgot?ctoken=<%=ctoken%>" method="post">
    <table bgcolor="#007ACC" align="center" width="400" cellpadding="2" cellspacing="2">
        <tr>
            <td>
                <table border="0" bgcolor="#CCE0FF" width="100%" align="center" cellpadding="2" cellspacing="2">
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td class="subtitle" colspan="4">&nbsp;&nbsp;Forgot Password > Email<br><br></td>
                    </tr>
                    <tr>
                        <td width="10" class="textb">&nbsp;</td>
                        <td width="220" class="textb">Username</td>
                        <td width="10" class="textb">:</td>
                        <td width="260"><input class="textBoxes" type="text" maxlength="100"  value="" name="username" size="30"></td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td colspan="4" class="label" align="center">
                            <input type="Submit" value="Send Mail" name="submit" class="submit">
                        </td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                </table>

            </td>
        </tr>
    </table>
</form>
<br>

</p>

<p>&nbsp;&nbsp; </p>
</body>

</html>
