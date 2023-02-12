<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company =ApplicationProperties.getProperty("COMPANY");%>
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

</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<br><br>


<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">
            <img src="/icici/images/logo.jpg" border="0">
        </td>
    </tr>
</table>
<br><br>
<table  align="center" width="50%" cellpadding="2" cellspacing="2">
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#F1EDE0" align="center">
                <tr bgcolor="#007ACC">

                    <td class="label" align="left">Admin Forgot Password</td>
                    <td>&nbsp;&nbsp;</td>
                </tr>


                <tr><td>&nbsp;</td></tr>
                <tr>

                    <td class="text" align="center" >Your temporary password has been sent to you by email to your mailing address set
                        on our server.</td>
                        </tr>
                <tr align="center">

                    <td class="text">Click <a href="/icici/admin/login.jsp"
                                                          class="link">here</a> to go back to the Admin login page
                    </td>

                </tr>
                <tr><td>&nbsp;</td><td>&nbsp;&nbsp;</td></tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>


