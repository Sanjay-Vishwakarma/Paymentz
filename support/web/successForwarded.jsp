<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>

<%
    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    String partnerid=(String) session.getAttribute("partnerid");
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
    <title>Customer Support mail sent</title>
    <link rel="stylesheet" type="text/css" href="/support/style.css">
</head>
<body style="background-color:#ecf0f1; " >
<br>
<br>
<br>
<br>
<br>
  <center>
<table bgcolor="#2c3e50" align="center" width="50%" cellpadding="2" cellspacing="0" >
    <tr>
        <td class="texthead">Customer Support Forgot Password
            <table bgcolor="#ffffff"  width="100%" align="center" cellpadding="2" cellspacing="0" style="height:260px; border:1px solid #2c3e50 ">
                <tr>
                    <td align="center" style="padding-top:5px; ">
                        <img src="/support/images/pay1.png">
                    </td>
                </tr>

                <tr style="padding-top: 10px">
                    <td align="center" class="textb" >Thank you for visiting Customer Support Module <br><br>Your temporary password has been sent to you by email to your mailing address set on our server. Please use the login and password to logon to the System within one hour.</td>

                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr >
                    <td align="center" class="textb" ><form action="/support/login.jsp" method="post">
                        <button type="submit" value="LOGIN"  style="background:#34495e;color:#edeef4;border:0;width: 60px;height: 30px">Login</button>
                    </form> </td>

                </tr>
                <tr >
                    <td align="center" class="textb" >&nbsp;</td>

                </tr>
                <tr >
                    <td align="center" class="textb" >&nbsp;</td>

                </tr>
                <tr >
                    <td align="center" class="textb" >&nbsp;</td>

                </tr>
            </table>
        </td>
    </tr>
</table>
  </center>
</body>
</html>







