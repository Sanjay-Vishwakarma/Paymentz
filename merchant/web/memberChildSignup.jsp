<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="Top.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page
        import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,java.util.Calendar,java.util.Enumeration,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.payment.MultipleMemberUtill" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
    <script language="javascript">

        $(document).ready(function() {

            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var str =$("#passwd").val();
                if(str.indexOf('<') >= 0 && str.indexOf('/') >= 0)
                {

                }else
                {
                    var encryptedString1 = $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
                    document.getElementById('passwd').value = encryptedString1;

                    var encryptedString2 = $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
                    document.getElementById('conpasswd').value = encryptedString2;

                    document.getElementById('isEncrypted').value = true;
                }
            });

        });

    </script>
    <script src="/merchant/javascript/hidde.js"></script>
    <title>Merchant Child Signup</title>

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

<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","User Management");
    MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
%>


<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">


            <form action="/merchant/memberChildList.jsp?ctoken=<%=ctoken%>" name="form" method="post">
                <div class="pull-right">
                <div class="btn-group">


                        <%

                            Enumeration<String> aName=request.getParameterNames();
                            while(aName.hasMoreElements())
                            {
                                String name=aName.nextElement();
                                String value = request.getParameter(name);
                                if(value==null || value.equals("null"))
                                {
                                    value = "";
                                }
                                else if(value.contains("<")){
                                    value = "";
                                }
                        %>
                        <input type=hidden name=<%=name%> value=<%=value%>>
                        <%
                            }

                        %>
                        <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/merchant/images/goBack.png">
                        </button>--%>
                    <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;Go Back</button>


                </div>
            </div>
            </form>
            <br><br>

            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Add New User</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <br>
                                <%--<div class="table-responsive datatable">
                                <div class="form foreground bodypanelfont_color panelbody_color">

                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/color:#34495e;padding-top: 5px;font-size: 18px;align: initial;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant's User Signup</h2>
                                    <hr class="hrform">
                                </div>--%>
                                <%
                                    String errormsg=(String)request.getAttribute("error");
                                    String userName = "";
                                    String phonecc = "";
                                    String memberId = "";
                                    memberId= (String) session.getAttribute("merchantid");

                                    if(errormsg!=null)
                                    {
                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\" face=\"arial\"><b>"+errormsg+"<b></li></center>");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                                    }
                                    if (request.getParameter("error") != null)
                                    {
                                        String mes = (String) request.getParameter("MES");

                                        //Hashtable details = (Hashtable) request.getAttribute("details");
                                        if ((String) request.getAttribute("username") != null) userName = (String) request.getAttribute("username");
                                        if ((String) request.getAttribute("phonecc") != null) phonecc = (String) request.getAttribute("phonecc");



                                    }
                                %>
                                <form action="/merchant/servlet/NewChildMemberSignUp?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">

                                    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                                    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >

                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Merchant Id*</label>
                                        <div class="col-sm-6">
                                            <input type="text" class="form-control" name="merchantid"  value="<%=(String) session.getAttribute("merchantid")%>" disabled >
                                            <input type="hidden" name="merchantid" value="<%=(String) session.getAttribute("merchantid")%>">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Username*</label>
                                        <div class="col-sm-6">
                                            <input class="form-control" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(userName)%>" name="username" size="35">
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">(Username Should Not Contain Special Characters like !@#$%)</label>
                                        </div>
                                    </div>

                                    <div class="form-group ">
                                        <label class="col-sm-4 control-label">Password*</label>
                                        <div class="col-sm-6 has-feedback">
                                            <input id="passwd" class="form-control" type="password" maxlength="125"  value="" name="passwd"size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                                            <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')"></span>
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">(Passwords length should be at least 6 and should contain alphabet, numeric, and special characters like !@#$)</label>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Confirm Password*</label>
                                        <div class="col-sm-6 has-feedback">
                                            <input id="conpasswd" class="form-control" type="Password" maxlength="125" value="" name="conpasswd" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                                            <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')"></span>
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">(Should be same as PASSWORD)</label>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-sm-4 control-label"> Email Address*</label>
                                        <div class="col-sm-6 has-feedback">
                                            <input id="email" class="form-control" type="text" maxlength="125" value="" name="email" size="35">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Phone CC*</label>
                                        <div class="col-sm-6 has-feedback">
                                            <input id="telnocc" class="form-control" type="text" maxlength="125" value="" name="telnocc" size="35">

                                        <%-- <input type="text" class="form-control" name="telcc"  value="<%=Functions.getTelcc(memberId).substring(0,3)%>" disabled>
                                         <input type="hidden" name="telcc" value="<%=Functions.getTelcc(memberId).substring(0,3)%>">--%>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-sm-4 control-label">Phone No*</label>
                                        <div class="col-sm-6 has-feedback">
                                            <input id="telno" class="form-control" type="text" maxlength="125" value="" name="telno" size="35">
                                        </div>
                                    </div>
                                    <input type="hidden" value="1" name="step">

                                    <div class="form-group col-md-4"></div>
                                    <div class="form-group col-md-4">
                                        <button type="submit" name="submit" class="btn btn-default" id="submit" <%--style="background: rgb(126, 204, 173);"--%>>
                                            <i class="fa fa-save"></i>
                                            Submit
                                        </button>
                                    </div>
                                </form>
                                <%--</div>
                                <div id="fade" class="black_overlay"></div>
                                <div id="wrapper" class="forced">--%>
                                <%

                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>