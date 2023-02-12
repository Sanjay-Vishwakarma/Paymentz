<%@ page language="java" %>
<%@ page import="com.directi.pg.DefaultUser,com.directi.pg.Functions,org.owasp.esapi.ESAPI,org.owasp.esapi.User" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>

<%
    session.setAttribute("valid", "true");

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
    final ResourceBundle RBundel1 = LoadProperties.getProperty("com.directi.pg.CertificateLink");
    String pciurl= RBundel1.getString("PCILINK");


%>
<%@ include file="/ietest.jsp" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Admin Login</title>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type='text/javascript' src='/icici/css/menu_jquery.js'></script>

    <link rel="stylesheet" type="text/css" href="/icici/stylenew/styles123.css">
    <script src='/icici/css/umd/popper.js'></script>
    <script src='/icici/css/bootstrap.min.js'></script>

    <link href="/icici/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">


		<script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>
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

<body bgcolor="#ffffff" class="login" onLoad='document.forms[0].username.focus();'>

<div class="container1">
    <div class="row1">
        <div class="col1-md-4 col1-md-offset-4"> <br>

            <table class=search border="0" cellpadding="2" cellspacing="0" width="40%" align=center valign="center">
                <tr>
                    <td align="center">

                        <img src="/icici/images/paylogo_icon.png">

                </tr>
            </table> <br><br>
            <div class="portlet1 portlet-green1" style="border-color:#2c3e50; ">
                <div class="portlet-heading1 login-heading1" style="background-color:#2c3e50;color: #ffffff;border-color:#2c3e50; ">
                    <div class="portlet-title">
                        Admin Login

                    </div>

                    <div class="clearfix"></div>
                </div>
                <div class="portlet-body1">

                    <form autocomplete="off" action="/icici/servlet/Login" method="POST">

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
                                            out.println("*Invalid Credentials.");
                                        }
                                        if (action.equals("L"))
                                        {
                                            out.println("*Your Account has been Locked.");
                                        }
                                        if (action.equals("D"))
                                        {
                                            out.println("*Your Account has been Disabled.");
                                        }
                                        if (action.equals("E"))
                                        {
                                            out.println("*Invalid Credentials.");
                                        }
                                        if (action.equals("IP"))
                                        {
                                            out.println("*Your IP is not white listed with us. Kindly Contact Support Desk.");
                                        }
                                        if (action.equals("N"))
                                        {
                                            out.println("*Your Account Deactivated");
                                        }
                                    %>
                                </td></tr></table>
                        <br>
                        <%
                            }
                        %>


                            <fieldset>
                                <div class="form-group">
                                    <div class="input-group">
                                        <span class="input-group-addon" style="background-color: #2c3e50;color:#ffffff;"> <i class="fa fa-user"></i></span>
                                        <input name="username" type="Text" maxlength="100"  value="" class="form-control" placeholder="Username" autocomplete="on">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="input-group">
                                        <span class="input-group-addon" style="background-color: #2c3e50;color:#ffffff;"><i class="fa fa-key"></i></span>
                                        <input id="password" name="password" type="Password" maxlength="125" value="" class="form-control" placeholder="password" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');">
                                    </div>
                                </div>

                                <br>
                                <INPUT id="submit" type=submit  value=Login  class="btn btn-lg btn-green btn-block" disabled="disabled" style="background-color:#2c3e50;color: #ffffff;border-color:#2c3e50; ">

                            </fieldset>
                            <br>

                    </form>

                </div>
            </div>
        </div>
    </div>
</div>
<table align=center valign="center">
    <tr>
        <td class="textb">
            <font color="#2c3e50"><b>Supported Browsers :</b> </font>
            <img src="/merchant/images/browsericon-firefox.png" alt="Firefox" title="Firefox">&nbsp;
            <img src="/merchant/images/browsericon-chrome.png" alt="Chrome" title="Chrome">&nbsp;
            <img src="/merchant/images/browsericon-safari.png" alt="Safari" title="Safari">&nbsp;
            <img src="/merchant/images/browsericon-opera.png" alt="Opera" title="Opera">&nbsp;
            <img src="/merchant/images/browsericon-explorer.png" alt="Internet Explorer 8+" title="Internet Explorer 8+">

        </td>
    </tr>
</table>
<table align=center valign="center" >
    <tr>
        <td align="center">
            <a target="_blank" href="<%=pciurl%>"><IMG border=0 height=100 src="/merchant/images/pci_dss_logo.png" width=152></a>
        </td>
    </tr>
    <tr><td class="textb"> KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES </td></tr>
</table>
</body>
</html>

