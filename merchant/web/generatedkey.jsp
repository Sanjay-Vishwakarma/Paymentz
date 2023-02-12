<%String company = (String)session.getAttribute("company");
    session.setAttribute("submit","Generate Key");

%>

<%@ page language="java" import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import ="org.owasp.esapi.ESAPI" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <%--<link rel="stylesheet" href="http://www.jacklmoore.com/colorbox/example1/colorbox.css" />
    <script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="http://www.jacklmoore.com/colorbox/jquery.colorbox.js"></script>
    <script type="text/javascript" src='/merchant/css/new/html5.js'></script>--%>

    <title><%=company%> Merchant Settings > Generate Key</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <script>
        function openColorBox(){
            $.colorbox({iframe:true, width:"80%", height:"80%", href: "/merchant/generatedkey.jsp"});
        }

        setTimeout(openColorBox, 100);
    </script>

    <style type="text/css">

        /*        #textareaid{
                    width: 31%;
                }
                */
        @media (min-width: 441px) {
            #textareaid{
                width: inherit;
            }
        }

        @media (max-width: 440px) {
            #textareaid{
                padding: 0;
            }
        }


        #generatekeyid{
            border: 1px solid #b2b2b2;
            font-weight: bold;
            word-break: break-all;
            color: #1e8b92;
            background-color: rgb(255, 255, 255);
            border-color: transparent;
            font-size: 15px;
            cursor: text;
            width: 50%;
            height: inherit;
        }

        @media (max-width: 640px) {
            #generatekeyid{
                width: inherit;
            }
        }

    </style>


</head>

<body class="pace-done widescreen fixed-left-void">

<%--<div class="rowcontainer-fluid " onload="openColorBox">
    <div class="row" style="margin-top: 100px;margin-left:29px;margin-right: 14px;margin-bottom: 12px;background-color: #ffffff">
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #ffffff; color:#34495e" >Merchant Key Generation</h2>
                <p class="hrform"></p>
            </div>--%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">
                    <%--<div class="widget green-1" style="background-color:  #68c39f;color: #fff;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;font-size: 14px;line-height: 1.42857143;">--%>
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Key Generated</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">


                            <div class="form-group col-md-12 has-feedback">
                                <label class="bg-info" style="font-family:Open Sans;font-size: 13px;font-weight: 600; margin-left: 1%;">
                                    The key below is now your new key. Please use this key and make the changes in your integration code.
                                    Without these changes your web site will not connect with the <%=company%> Payment Gateway Service.
                                    Please replace this key with the old key in the <b><i>checkout</i></b> and <b><i>redirect</i></b> pages.
                                    <br><br>
                                    The generated key is highly confidential information and must not be shared to anyone including our system and support team.
                                    In case by any chance it has been shared to anyone, we suggest you to report to us  and change it immediately.
                                    <br><br>
                                    You are the owner of this secret key and to maintain its confidentiality is your responsibility and only you will be held responsible if it has been leaked and misused.
                                    We can't see this key or never ask to share this information to us or to anyone.
                                    <br><br><br><br>

                                    <div class="form-group col-md-12 has-feedback">
                                        <center>
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><h4><b>Key Generated</b></h4></label>
                                            <%--<input type="text" size="32" class="form-control" value=<%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("key"))%> disabled style="border: 1px solid #b2b2b2;font-weight:bold; width: 32%">--%>
                                            <%--<textarea type="text" size="32" id="textareaid" class="form-control" placeholder=<%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("key"))%> disabled style="border: 1px solid #b2b2b2;font-weight:bold;"></textarea>--%>
                                            <label class="form-control" id="generatekeyid" style="border: 1px solid #b2b2b2;font-weight:bold;word-break: break-all;"><%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("key"))%></label>
                                        </center>
                                    </div>
                            </div>
                            <%--<br><br><br><br><br><br><br>

                            <div style="box-sizing:border-box;padding:5px; background:#ffffff;height:auto;margin-left:240px;margin-right:30px;border:solid 2px #2c3e50; " >

                                <p>
                                <table border="0" width="100%" align="center">
                                    <tr><td>&nbsp;</td></tr>

                                    <tr>
                                        <td class="subtitle">&nbsp;&nbsp;&nbsp;Note:-</td>
                                    </tr>
                                    <tr>
                                        <td class="subtitle">&nbsp;</td>
                                    </tr><tr>
                                    <td class="subtitle">&nbsp;</td>
                                </tr>
                                    <tr>
                                        <td class="textb" style="padding-left:50px;padding-right: 50px" >
                                            <font color="red" >
                                            The key below is now your new key. Please use this key and make the changes in your integration code.
                                            Without these changes your web site will not connect with the <%=company%> Payment Gateway Service.
                                            Please replace this key with the old key in the <b><i>checkout</i></b> and <b><i>redirect</i></b> pages.
                                            </font>
                                        </td>
                                    </tr>
                                    <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td class="textb" style="padding-left:50px;padding-right: 50px">
                                        <font color="red" >
                                        The generated key is highly confidential information and must not be shared to anyone including our system and support team.
                                        In case by any chance it has been shared to anyone, we suggest you to report to us  and change it immediately.
                                        </font>
                                    </td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td class="textb" style="padding-left:50px;padding-right: 50px">
                                        <font color="red" >
                                        You are the owner of this secret key and to maintain its confidentiality is your responsibility and only you will be held responsible if it has been leaked and misused.
                                        We can't see this key or never ask to share this information to us or to anyone.
                                        </font>
                                    </td>
                                </tr>
                                </table>
                                <hr class="hrnew">
                                &lt;%&ndash; <hr bgcolor="#2c3e50" width="750" align="center"   size="1">&ndash;%&gt;<br><br>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <table   align="center" width="750" cellpadding="2" cellspacing="2">
                                    <tr>
                                        <td>
                                            <table border="0" width="100%" align="center" cellpadding="2" cellspacing="2">
                                                <tr><td></td></tr>
                                                <tr>
                                                    <td class="textb" style="font-weight: bold;">&nbsp;&nbsp;New Key Generated<br><br></td>
                                                </tr>
                                                <tr>
                                                    <td class="textb" align="center"   >
                                                        <p style="background: #34495e;color:#ffffff;height:20px;border-radius:4px;width:400px;">&nbsp;&nbsp;<%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("key"))%></p>
                                                    </td>
                                                </tr>

                                                <tr><td>&nbsp;</td></tr>
                                            </table>

                                        </td>
                                    </tr>
                                </table>

                                </p>
                            </div>--%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>