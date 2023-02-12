<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Agent" %>
<%@ include file="top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));%>
<%--<%
    session.setAttribute("submit","Change Password");
%>--%>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <title><%=company%> Change Password</title>
    <script src="/agent/javascript/jquery.min.js"></script>
    <script type="text/javascript" src="/agent/javascript/jquery.jcryption.js?ver=1"></script>
    <script type="text/javascript">
        $(document).ready(function()
        {

            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var encryptedString1 =  $.jCryption.encrypt($("#oldpwd").val(), $("#ctoken").val());
                document.getElementById('oldpwd').value =  encryptedString1;

                var encryptedString2 =  $.jCryption.encrypt($("#newpwd").val(), $("#ctoken").val());
                document.getElementById('newpwd').value =  encryptedString2;

                var encryptedString3 =  $.jCryption.encrypt($("#confirmpwd").val(), $("#ctoken").val());
                document.getElementById('confirmpwd').value =  encryptedString3;
                document.getElementById('isEncrypted').value =  true;
            });

        });
    </script>
    <script src="/merchant/javascript/hidde.js"></script>
    <style type="text/css">
        .field-icon
        {
            float:  right;
            margin-top: -25px;
            position: relative;
            z-index: 2;
        }
    </style>
    <style type="text/css">
        .panel-default{border-color: transparent;}
    </style>

</head>

<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><%=company%>'s Change Password</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <%--<div class="form-group">
                                    <label for="inputEmail3" class="col-sm-2 control-label">Email</label>
                                    <div class="col-sm-10">
                                        <input type="email" class="form-control" id="inputEmail3" placeholder="Email">
                                        <p class="help-block">Example block-level help text here.</p>
                                    </div>
                                </div>--%>
                                <div class="form-group ">
                                    <label <%--for="oldpwd"--%> class="col-sm-2 control-label">&nbsp;</label>
                                    <div class="col-sm-12">


                                        <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                            if(agent.isLoggedInAgent(session))
                                            {
                                        %>

                                        <%
                                            if (request.getParameter("MES") != null)
                                            {
                                                String mes = ESAPI.encoder().encodeForHTML((String) request.getParameter("MES"));
                                                if (mes.equals("FP"))
                                                {
                                        %>

                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;Please change your password as your have logged on using a temporary password.</h5>
                                        <%
                                            }

                                            if (mes.equals("UP"))
                                            {
                                        %>
                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;Please change your password within 90 Days.</h5>
                                        <%
                                            }

                                            if (mes.equals("ERR"))
                                            {
                                        %>
                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;
                                            <%
                                                String error=(String) request.getAttribute("error");
                                                out.println(error);

                                            %>
                                        </h5>
                                        <%
                                                }

                                            }
                                        %>
                                    </div>
                                    <form action="/agent/net/AgentChngPassword?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">
                                        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                                        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <div class="form-group">
                                            <label <%--for="oldpwd"--%> class="col-sm-2 control-label">Old Password</label>
                                            <div class="col-sm-6">
                                                <input class="form-control" id="oldpwd" type="password" value="" name="oldpwd" maxlength="125" size="30"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="oldshowHidepass" onclick="hideshowpass('oldshowHidepass','oldpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="newpwd"--%> class="col-sm-2 control-label">New Password</label>
                                            <div class="col-sm-6">
                                                <input id="newpwd" class="form-control" type="password" value="" name="newpwd" maxlength="125" size="30"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="newshowHidepass" onclick="hideshowpass('newshowHidepass','newpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="newpwd"--%> class="col-sm-2 control-label"></label>
                                            <div class="col-sm-10">
                                                <h5 class="bg-info" style="width: 75%;"><i class="fa fa-envelope"></i>&nbsp;&nbsp;(Password length should be at least 8 & must contain alphabet, numeric,and special characters like !@#$%)</h5>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="confirmpwd"--%> class="col-sm-2 control-label">Confirm New Password</label>
                                            <div class="col-sm-6">
                                                <input id="confirmpwd" class="form-control" type="password" value="" name="confirmpwd" maxlength="125" size="30"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','confirmpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="confirmpwd"--%> class="col-sm-2 control-label"></label>
                                            <div class="col-sm-10">
                                                <button id="submit" type="Submit" name="submit" class="btn btn-default" disabled="disabled" style="margin-top: 19px;">
                                                    <span><i class="fa fa-lock"></i></span>
                                                    &nbsp;&nbsp;Change
                                                </button>
                                            </div>
                                        </div>

                                        <h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES.</h5>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
<%
    }
    else
    {
        response.sendRedirect("/agent/logout.jsp");
        return;
    }
%>
</body>
</html>