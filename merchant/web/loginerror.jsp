<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company = (String)session.getAttribute("company");%>

<%
    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    String partnerid=(String) session.getAttribute("partnerid");
    if(ctoken !=null)
    {
        if(!Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/merchant/sessionout.jsp");
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
    <title>Merchant Administration </title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body style="background-color:#ecf0f1; ">

<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">
            <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">
        </td>
    </tr>
</table>
<br><br>
<table  align="center" width="65%" cellpadding="2" cellspacing="2">
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#ffffff" align="center">
                <tr bgcolor="#2c3e50">
                    <td class="text" align="left" style="color: #ffffff;">&nbsp;&nbsp;Merchant Administration Module</td>
                </tr>
                <tr align="center">
                    <td class="text">
                        <%
                            String action = ESAPI.encoder().encodeForHTML((String) request.getAttribute("action"));
                            if (action.equals("F"))
                            {
                                out.println("The username does not exist in system.");
                        %>
                        &nbsp;&nbsp;Click <form action="/merchant/index.jsp" method="post"><input type="hidden" value="<%=partnerid%>" name="partnerid"><input type="hidden" value="<%=session.getAttribute("company")%>" name="fromtype"><input type="submit" value="LOGIN" name="submit" ></form> to go back to the Merchant login page
                        <%
                            }
                            else if (action.equals("T"))
                            {
                                out.println("Your account does not exist in system.");
                            }
                            else if(action.equals("D"))
                            {
                                out.println("Your Login has been Disabled.");
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
