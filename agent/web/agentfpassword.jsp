<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.User,
                 com.directi.pg.Functions,
                 com.directi.pg.DefaultUser" %>
<%String company = (String)session.getAttribute("company");%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="ietest.jsp" %>


<%


    String ctoken= request.getParameter("ctoken");
    User user =  (User)session.getAttribute("Anonymous");
    if(ctoken !=null)
    {
        if(!com.directi.pg.Functions.validateCSRFAnonymos(ctoken,user))
        {
            response.sendRedirect("/agent/sessionout.jsp");
            return;
        }

    }
    else if(user!=null)
    {

        ctoken = user.getCSRFToken();

    }
    else
    {
        user =   new com.directi.pg.DefaultUser(User.ANONYMOUS.getName());
        ctoken = (user).resetCSRFToken();

    }

    session.setAttribute("Anonymous", user);

    String partnerIcon = (String) session.getAttribute("icon");
    String logo= (String) session.getAttribute("logo");
    Functions functions = new Functions();

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Agent Forgot Password</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <%--<link rel="stylesheet" type="text/css" href="/agent/style1/styles123.css">--%>
    <link href="/agent/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

    <!-- META SECTION -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <%--<link rel="icon" href="/agent/NewCss/favicon.ico" type="image/x-icon" />--%>
    <!-- END META SECTION -->

    <!-- CSS INCLUDE -->
    <link href="/agent/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" id="theme" href="/agent/NewCss/theme-default.css"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fontawesome/font-awesome.min.css"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/fontawesome-webfont.eot"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/fontawesome-webfont.svg"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/fontawesome-webfont.woff"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular.eot"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular.svg"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular"/>
    <link rel="stylesheet" type="text/css"  href="/agent/NewCss/fonts/glyphicons-halflings-regular.woff"/>
    <!-- EOF CSS INCLUDE -->


    <style type="text/css">
        @-moz-document url-prefix() {
            .login-container.lightmode {
                position: fixed;
            }
        }

    </style>

</head>

<div class="login-container lightmode">
    <body>
    <div class="login-box animated fadeInDown" style="padding-top: 55px;">

        <%
            if(functions.isValueNull(partnerIcon))
            {
        %>
        <div class="login-logo" style="
                background: url(/images/agent/<%=partnerIcon%>) top center no-repeat;
                width: 100%;
                height: 70px;
                float: left;
                background-size: contain;
                margin-bottom: 10px">
        </div>
        <div class="login-logo" style="
                background: url(/images/agent/<%=logo%>) top center no-repeat;
                width: 100%;
                height: 50px;
                float: left;
                background-size: contain;
                margin-bottom: 20px;">
        </div>
        <%
        }
        else
        {
        %>
        <div class="login-logo" style="
                background: url(/images/agent/<%=logo%>) top center no-repeat;
                width: 100%;
                height: 50px;
                float: left;
                background-size: contain;
                margin-bottom: 20px;">
        </div>
        <%
            }
        %>

        <%--<div class="login-logo" style="background: url(/agent/images/pay2.png) top center no-repeat;width: 100%;height: 50px;float: left;background-size: contain;margin-bottom: 20px;">&lt;%&ndash;<img src="/agent/images/pay2.png">&ndash;%&gt;</div>--%>


        <div class="login-body">
            <div class="login-title"><p align="center" class="subtitle1" style="font-weight: bold; color: #FFFFFF;font-family: 'Open Sans', sans-serif;font-size: 18px;">Forgot Password</p></div>


            <p>
            <table border="0" width="100%" align="center">
                <br><br>
                <tr>
                    <td class="textb" align="center" style="color: #FFFFFF;font-family: 'Open Sans', sans-serif;font-size: 14px;">Please fill in your username. An email will be sent to you containing a new password.</td>
                    </td>
                </tr>
                <tr>
                    <td class="textb" align="center" style="color: #FFFFFF;font-family: 'Open Sans', sans-serif;font-size: 14px;">Note:Please login using the new password within one hour of receiving the email.
                    </td>
                </tr>

            </table>
            <hr class="hrnew"><br>

            <%
                String action = request.getParameter("action");
                String error=(String)request.getAttribute("error");
                if(error!=null)
                {
                    out.println("<p><center><font class=\"textb\">"+error+"</font></center></p>");
                }
                else if (action != null)
                {
                    if (action.equalsIgnoreCase("F"))
                    {
                        out.println("<p><center><font class=\"textb\">Invalid User Name.</font></center></p>");
                    }
                    else if (action.equalsIgnoreCase("E"))
                    {
                        out.println("<p><center><font class=\"textb\">Invalid Input.</font></center></p>");
                    }
                }

            %>
            <form action="/agent/net/AgentForgotPassword?ctoken=<%=ctoken%>" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <div class="form-group col-md-12 has-feedback">
                    <label  style="font-family:Open Sans;font-size: 15px;font-weight: 600; color: #FFFFFF;">Username:<br><br></label>
                    <input class="form-control" type="text"   value="" name="username">
                </div>
                <div class="form-group col-md-12 has-feedback">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"></label>
                    <button type="submit" name="submit" class="btnblue">
                        <span><i class="fa fa-mail-forward"></i></span>
                        Send Mail
                    </button>
                </div>

            </form>

            </p>
        </div>
    </div>
    </body>
</div>

</html>
