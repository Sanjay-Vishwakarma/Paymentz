<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company = ApplicationProperties.getProperty("COMPANY");%>

<%
    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
        if(!Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/partner/sessionout.jsp");
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
    <title>Partner Administration </title>
    <link rel="stylesheet" type="text/css" href="/partner/style1/styles123.css">
</head>

<body class="bodybackground">
<br><br><br><br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">
            <img src="/partner/images/pay2.png" border="0">
        </td>
    </tr>
</table>
<br><br>
<table  align="center" width="65%" cellpadding="2" cellspacing="2">
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tr>
                    <td class="texthead" align="left">&nbsp;&nbsp;Partner Administration Module</td>
                </tr>
                <tr align="center" style="background-color: #ffffff;">
                    <td class="textb" style="background-color: #ffffff;">
                        <%
                            String action = ESAPI.encoder().encodeForHTML(request.getParameter("action"));
                            if (action.equals("F"))
                            {
                                out.println("Your Login or Password was incorrect.");
                                out.println("</p><p ALIGN=\"CENTER\"><a href=\"/partner/login.jsp\"><b>Please Try Again.</b></a></p>");
                                out.println("<p ALIGN=\"center\">If you have forgotten the password please click on forgot password link on the login page and you will get mail from " + company + " support team with a temporary password. <br><br>If you have forgotten your username, please contact support team.");
                                out.println("</p>");
                            }
                            else if (action.equals("T"))
                            {
                                out.println("Your account is not yet activated. So , first pay the initial payment , only afterwards you can excess the account.");
                            }
                            else if (action.equals("E"))
                            {
                                out.println("Your account does not exist in system.");
                            }
                            else
                            {
                                out.println("Your Login has been Disabled.<br><br><b>Please contact support team.</b>");
                            }
                        %>
                    </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
            </table>
        </td>
    </tr>
</table>


</body>
</html>
