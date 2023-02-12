<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="Top.jsp" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Change Password");
%>
<%
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String chngpwd_Change_Password = rb1.getString("chngpwd_Change_Password");
    String chngpwd_Change_change_your_pwd_msg1 = rb1.getString("chngpwd_Change_change_your_pwd_msg1");
    String chngpwd_Change_change_your_pwd_msg2 = rb1.getString("chngpwd_Change_change_your_pwd_msg2");
    String chngpwd_Change_Old_Password = rb1.getString("chngpwd_Change_Old_Password");
    String chngpwd_Change_New_Password = rb1.getString("chngpwd_Change_New_Password");
    String chngpwd_Change_Password_length = rb1.getString("chngpwd_Change_Password_length");
    String chngpwd_Change_Confirm_New_Password = rb1.getString("chngpwd_Change_Confirm_New_Password");
    String chngpwd_Change_Change_submit = rb1.getString("chngpwd_Change_Change_submit");
    String chngpwd_JAVASCRIPT_ENABLED_msg = rb1.getString("chngpwd_JAVASCRIPT_ENABLED_msg");

%>
<%
    response.setHeader("X-Frame-Options", "ALLOWALL");
    session.setAttribute("X-Frame-Options", "ALLOWALL");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>--%>

    <title><%=company%> Merchant Change Password</title>

<%--
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
--%>
    <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
    <script src="/merchant/javascript/hidde.js"></script>
    <script>

        $(document).ready(function(){

            var w = $(window).width();

            //alert(w);

            if(w > 990){
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else{
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>
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
                            <h2><strong><%=chngpwd_Change_Password%></strong></h2>
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
                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;<%=chngpwd_Change_change_your_pwd_msg1%></h5><%--</td>--%>
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
                                        <h5 class="bg-alert" style="text-align: center"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;<%=chngpwd_Change_change_your_pwd_msg2%></h5><%--</td>--%>
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
                                            /*out.println("<div class=\"alert alert-info\" style=\"text-align: center;font-size: 12px;font-weight:300;float: none;\">");*/
                                                out.println(error);
                                            /*out.println("</div>");*/


                                            %>
                                        </h5><%--</td>
                                    </tr>
                                </table>--%>
                                        <%
                                                }

                                            }
                                        %>
                                    </div>
                                    <form action="/merchant/servlet/ChangePassword?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">
                                        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                                        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <div class="form-group">
                                            <label <%--for="oldpwd"--%> class="col-sm-2 control-label"><%=chngpwd_Change_Old_Password%></label>
                                            <div class="col-sm-6">
                                                <input class="form-control" id="oldpwd" type="password" value="" name="oldpwd" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="oldshowHidepass" onclick="hideshowpass('oldshowHidepass','oldpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="newpwd"--%> class="col-sm-2 control-label"><%=chngpwd_Change_New_Password%></label>
                                            <div class="col-sm-6">
                                                <input id="newpwd" class="form-control" type="password" value="" name="newpwd" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="newshowHidepass" onclick="hideshowpass('newshowHidepass','newpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="newpwd"--%> class="col-sm-2 control-label"></label>
                                            <div class="col-sm-10">
                                                <h5 class="bg-info" style="width: 75%;"><i class="fa fa-envelope"></i>&nbsp;&nbsp;<%=chngpwd_Change_Password_length%></h5>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="confirmpwd"--%> class="col-sm-2 control-label"><%=chngpwd_Change_Confirm_New_Password%></label>
                                            <div class="col-sm-6">
                                                <input id="confirmpwd" class="form-control" type="password" value="" name="confirmpwd" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                                                 <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','confirmpwd')"></span>
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label <%--for="confirmpwd"--%> class="col-sm-2 control-label"></label>
                                            <div class="col-sm-10">
                                                <button id="submit" type="Submit" name="submit" class="btn btn-default"  style="margin-top: 19px;">
                                                    <span><i class="fa fa-lock"></i></span>
                                                    &nbsp;&nbsp;<%=chngpwd_Change_Change_submit%>
                                                </button>
                                            </div>
                                        </div>

                                        <h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;<%=chngpwd_JAVASCRIPT_ENABLED_msg%></h5>
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
<script type="text/javascript" src="/merchant/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/merchant/cookies/cookies_popup.js"></script>
<link href="/merchant/cookies/quicksand_font.css" rel="stylesheet">
<link href="/merchant/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>