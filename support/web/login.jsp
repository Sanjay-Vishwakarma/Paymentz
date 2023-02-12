<%--
<!DOCTYPE html>
--%>
<%@ page language="java" %>
<%@ page import="com.directi.pg.DefaultUser,
                 com.directi.pg.Functions,
                 com.directi.pg.Logger,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User" %>
<%@ page import="java.util.Hashtable" %>


<%!static Logger log=new Logger("logger1");%>
<%

    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    log.debug("Cust Supp Login  user::"+user+" session:: "+session.getAttribute("Anonymous"));
    session.setAttribute("valid", "true");
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
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <title></title>



    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>

    <link href="/support/css/styles123.css" rel="stylesheet">

    <link href="/support/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

    <script type="text/javascript" src="/support/javascript/jquery-1.7.1.js?ver=1"></script>
    <script type="text/javascript" src="/support/javascript/jquery.jcryption.js?ver=1"></script>
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

<body class="login" <%--style="background-color:#cce0ff; "--%>>

<div class="container1">
    <div class="row1">
        <div class="col1-md-4 col1-md-offset-4"> <br>
            <%--<div class="login-banner text-center" style="height:50px; ">


            </div>--%>
            <%
                String status= ESAPI.encoder().encodeForHTML((String) request.getAttribute("isValid"));
                String refresh=ESAPI.encoder().encodeForHTML(request.getParameter("isValid")) ;
                String inValid=ESAPI.encoder().encodeForHTML(request.getParameter("MES"));
                Hashtable error=(Hashtable) request.getAttribute("error");
            %>
            <table class=search border="0" cellpadding="2" cellspacing="0" width="40%" align=center valign="center">
                <tr>
                    <td align="center">

                                                <img src="/support/images/logo.jpg">

                        <%--<img src="/support/images/<%=session.getAttribute("logo")%>" border="0">--%>
                </tr>
            </table> <br><br>
            <div class="portlet1 portlet-green1" style="border-color:#2c3e50; ">
                <div class="portlet-heading1 login-heading1" style="background-color:#2c3e50;color: #ffffff;border-color:#2c3e50; ">
                    <div class="portlet-title">
                        Customer Support Login

                    </div>

                    <div class="clearfix"></div>
                </div>
                <div class="portlet-body1">

                    <form autocomplete="off" action="/support/servlet/Login?ctoken=<%=ctoken%>" method="post">

                        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >

                        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
                        <table width="100%">
                            <tr>
                                <td class="text" align="center">
                                    <%if(inValid!=null)
                                    {
                                        out.println(" * Invalid username and password");
                                    }
                                    else{
                                        if("false".equals(status))
                                        {
                                            out.println(" * Username or Password incorrect");
                                        }else{if("refresh".equals(refresh))
                                        {
                                            out.println("* Username or Password incorrect");
                                        }
                                        else
                                        {
                                            if("false".equals(refresh))
                                            { out.println("* Username or Password incorrect");}
                                            else{
                                                if("D".equals(refresh))
                                                {
                                                    out.println("* USER HAS BEEN DISABLED");
                                                }else{
                                                    if("L".equals(refresh))
                                                    {
                                                        out.println("* USER HAS BEEN LOCKED");
                                                    }else
                                                    {
                                                        if("AD".equals(refresh))
                                                        {
                                                            out.println("* ACCESS DENIED");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        }
                                    }%>
                                </td></tr></table>
                        <br>
                        <%

                        %>


                        <%--<form accept-charset="UTF-8" role="form">--%>
                            <fieldset>
                                <div class="form-group">
                                    <input name="username" type="Text" maxlength="100"  value="" class="form-control" placeholder="Username">
                                </div>
                                <div class="form-group">
                                    <input id="password" name="password" type="Password" maxlength="125" value="" class="form-control" placeholder="password">
                                </div>
                                <%--<div class="checkbox">
                                    <label>
                                        <input name="remember" type="checkbox" value="Remember Me">Remember Me
                                    </label>
                                </div>--%>
                                <br>
                                <INPUT id="submit" type=submit  value=Login  class="btn btn-lg btn-green btn-block" disabled="disabled" style="background-color:#2c3e50;color: #ffffff;border-color:#2c3e50; ">

                            </fieldset>
                            <br>
                            <p class="small">
                            <div class="textb">
                                <a href="/support/forgotPassword.jsp?ctoken=<%=ctoken%>"><input type="hidden" value="<%=ctoken%>" name="ctoken">Forgot Password?</a>
                            </div>
                            </p>
                        </form>


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
                <a target="_blank" href="https://www.sisainfosec.com/certificate.php?number=75087015223241354987&type=pcidss"><IMG border=0
                                                                                                             height=50
                                                                                                            src="/support/images/pci_dss_logo.png"
                                                                                                            width=50></a>
            </td></tr>

        <tr><td class="textb"> KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES </td></tr>
    </table>
</div>
</body>

</html>
