<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","Change Password");
%>
<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);

    String partnerChngpassword_Partner_Change_Password = rb1.getString("partnerChngpassword_Partner_Change_Password");
    String partnerChngpassword_Note1 = rb1.getString("partnerChngpassword_Note1");
    String partnerChngpassword_Note2 = rb1.getString("partnerChngpassword_Note2");
    String partnerChngpassword_Old_Password = rb1.getString("partnerChngpassword_Old_Password");
    String partnerChngpassword_New_Password = rb1.getString("partnerChngpassword_New_Password");
    String partnerChngpassword_Confirm_New_Password = rb1.getString("partnerChngpassword_Confirm_New_Password");
    String partnerChngpassword_Change= rb1.getString("partnerChngpassword_Change");
    String partnerChngpassword_Note3= rb1.getString("partnerChngpassword_Note3");
    String partnerChngpassword_Note4= rb1.getString("partnerChngpassword_Note4");

%>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>--%>

    <title><%=company%> Partner Change Password</title>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <%--   <script type="text/javascript" src="/partner/javascript/jquery-1.7.1.js?ver=1"></script>--%>
    <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
    <script type="text/javascript">
        $(document).ready(function() {
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
        .panel-default{border-color: transparent;}
    </style>
    <style type="text/css">
        .field-icon
        {
            float:  right;
            margin-top: -25px;
            position: relative;
            z-index: 2;
        }
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
                            <h2><strong><%=partnerChngpassword_Partner_Change_Password%></strong></h2>
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

                                            if (partner.isLoggedInPartner(session))
                                            {
                                        %>
                                        <%
                                            if (request.getParameter("MES") != null)
                                            {
                                                String mes = ESAPI.encoder().encodeForHTML((String) request.getParameter("MES"));
                                                if (mes.equals("FP"))
                                                {
                                        %>
                                        <%--<table width="100%" align="right">
                                            <tr class="textb">
                                                <td align="center">--%>
                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;<%=partnerChngpassword_Note1%></h5><%--</td>--%>
                                        <%--</tr>
                                    </table>--%>
                                        <%
                                            }

                                            if (mes.equals("UP"))
                                            {
                                        %>
                                        <%-- <table width="100%">
                                             <tr class="textb">
                                                 <td style="padding-left:167px">--%>
                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;<%=partnerChngpassword_Note2%></h5><%--</td>--%>
                                        <%--</tr>
                                    </table>--%>
                                        <%
                                            }

                                            if (mes.equals("ERR"))
                                            {
                                        %>
                                        <%--<table>
                                            <tr align="center" >
                                                <td> --%>
                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;
                                            <%
                                                String error=(String) request.getAttribute("error");
                                                out.println(error);

                                            %>
                                        </h5><%--</td>
                                    </tr>
                                </table>--%>
                                        <%
                                                }

                                            }
                                        %>
                                    </div>
                                    <form action="/partner/net/PartnerChngPassword?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">
                                        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                                        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <div class="form-group">
                                            <label <%--for="oldpwd"--%> class="col-sm-2 control-label"><%=partnerChngpassword_Old_Password%></label>
                                            <div class="col-sm-6">
                                                <input class="form-control" id="oldpwd" type="password" value="" name="oldpwd" maxlength="125" size="30" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="oldshowHidepass" onclick="hideshowpass('oldshowHidepass','oldpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="newpwd"--%> class="col-sm-2 control-label"><%=partnerChngpassword_New_Password%></label>
                                            <div class="col-sm-6">
                                                <input id="newpwd" class="form-control" type="password" value="" name="newpwd" maxlength="125" size="30"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="newshowHidepass" onclick="hideshowpass('newshowHidepass','newpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="newpwd"--%> class="col-sm-2 control-label"></label>
                                            <div class="col-sm-10">
                                                <h5 class="bg-info" style="width: 75%;"><i class="fa fa-envelope"></i>&nbsp;&nbsp;<%=partnerChngpassword_Note3%></h5>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="confirmpwd"--%> class="col-sm-2 control-label"><%=partnerChngpassword_Confirm_New_Password%></label>
                                            <div class="col-sm-6">
                                                <input id="confirmpwd" class="form-control" type="password" value="" name="confirmpwd" maxlength="125" size="30"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','confirmpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="confirmpwd"--%> class="col-sm-2 control-label"></label>
                                            <div class="col-sm-10">
                                                <button id="submit" type="Submit" name="submit" class="btn btn-default" disabled="disabled" style="margin-top: 19px;">
                                                    <span><i class="fa fa-lock"></i></span>
                                                    &nbsp;&nbsp;<%=partnerChngpassword_Change%>
                                                </button>
                                            </div>
                                        </div>

                                        <h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;<%=partnerChngpassword_Note4%></h5>
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
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
</body>
</html>