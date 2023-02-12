<%@ page language="java" %>
<%@ page import="org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User,
                 org.owasp.esapi.reference.DefaultEncoder,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser"  %>


<%

    session.setAttribute("valid", "true");

    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
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
    <title>Virtual Login</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>


    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {

            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var encryptedString =  $.jCryption.encrypt($("#password").val(), $("#ctoken").val());
                document.getElementById('password').value =  encryptedString;
                document.getElementById('isEncrypted').value =  true;
            });

        });
    </script>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0" onLoad='document.forms[0].username.focus();'>
<br><br>
<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">

            <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">

        </td>
    </tr>
</table>
<br><br>

<form action="/merchant/servlet/Login?ctoken=<%=ctoken%>" method="post">

    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >

    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >


    <%
        String action = ESAPI.encoder().encodeForHTML(request.getParameter("action"));
        if (action != null)
        {
    %>
    <table width="100%">
        <tr>
            <td class="text" align="center">
                <%
                    if (action.equals("F"))
                    {
                        out.println("*Your Login or Password was incorrect.");
                    }
                    if (action.equals("L"))
                    {
                        out.println("*Your Merchant Account has been Locked. Kindly Contact the Support Desk.");
                    }
                    if (action.equals("D"))
                    {
                        out.println("*Your Merchant Account has been Disabled. Kindly Contact the  Support Desk.");
                    }
                    if (action.equals("IP"))
                    {
                        out.println("*Your IP is not white listed with us. Kindly Contact the Support Desk.");
                    }
                    if (action.equals("A"))
                    {
                        out.println("*Access Denied.Kindly Contact the Support Desk.");
                    }
                %>
            </td></tr></table>
    <br>
    <%
        }
    %>
    <table <%--border="1" --%> align="center" width="30%" cellpadding="1" cellspacing="1">
        <tr>
            <td>
                <table bgcolor="#CCE0FF"<%--"#F1EDE0"--%> border="1"  cellpadding="2" cellspacing="0" width="100%"
                       bordercolorlight="#E4D6C9" bordercolordark="#E4D6C9" align=center valign="center">
                    <tr>
                        <td bgcolor="#007ACC" <%-- "#9A1305"--%> colspan="2" class="label">Merchant Login</td>
                    </tr>
                    <tr>
                        <td class="textb">Username: </td>
                        <td><input name="username" type="Text" maxlength="100"  value="" class="textBoxes"></td>
                    </tr>
                    <tr>
                        <td class="textb">Password: </td>
                        <td><input id="password" name="password" type="Password" maxlength="125"   value="" class="textBoxes"></td>

                    </tr>
                    <tr>
                        <td><input id="isVirtual" name="isVirtual" type="hidden" value="T" ></td>

                    </tr>

                    <tr><td colspan="2" align="center"><INPUT id="submit" type=submit  value=Login  class="submit" disabled="disabled"></td></tr>

                </table>
            </td></tr>
    </table>
</form>
<table align=center valign="center">
    <%--<tr>
        <td align="center">

            &lt;%&ndash;<a target="_blank" href="http://www.abc.com/site/certificate/05975497347639315258"><IMG border=0 height=40 src="http://www.<companyname>.com/sites/all/themes/UrbanSolice/images/Pci.png" width=105></a>&ndash;%&gt;
            <a target="_blank" href="http://abc.com/site/certificate/13835412899812182686"><IMG border=0 height=40 src="http://www.<companyname>.com/sites/all/themes/UrbanSolice/images/Pci.png" width=105></a>



        </td></tr>--%>    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td class="textb"> KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES </td></tr>
</table>
</body>
</html>


