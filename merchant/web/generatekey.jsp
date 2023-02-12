<%@ page import="com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.User" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>

<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Generate Key");

%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.directi.pg.LoadProperties" %>

<html lang="en">
<head>

    <title><%=company%> Merchant Settings > Generate Key</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }

        function DoConfirm()
        {
            if (confirm("Do you really want to generate NEW key ?"))
            {
                document.form.submit();
            }
            else
            {
                return false;
            }
        }
    </script>
    <%--<script>
        black_overlay{
            display: none;
            position: absolute;
            top: 0%;
            left: 0%;
            width: 100%;
            height: 100%;
            background-color: black;
            z-index:1001;
            -moz-opacity: 0.8;
            opacity:.80;
            filter: alpha(opacity=80);
        }
        white_content {
            display: none;
            position: absolute;
            top: 5%;
            left: auto;
            width: 50%;
            height: auto;
            padding: 16px;
            border: 4px solid #7eccad;
            background-color: white;
            z-index: 1002;
            overflow: auto;
        }
    </script>--%>
    <%--<link rel="stylesheet" type="text/css" href="style.css">

    <script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>

    <%--<script type="text/javascript" src='/merchant/css/new/html5.js'></script>--%>
</head>
<body class="pace-done widescreen fixed-left-void">

<%--<div class="container-fluid " >
    <div class="row rowadd">
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-size:18px ;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;&lt;%&ndash;<%=company%>&ndash;%&gt; Merchant Key Generation</h2>
                <hr class="hrform">
            </div>--%>

<%
    String memberKey = merchantDAO.getMerchantKey((String) session.getAttribute("merchantid"));
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String generatekey_key_generation = rb1.getString("generatekey_key_generation");
    String generatekey_successful = rb1.getString("generatekey_successful");
    String generatekey_generatedkey = rb1.getString("generatekey_generatedkey");
    String generatekey_currentkey = rb1.getString("generatekey_currentkey");
    String generatekey_generate = rb1.getString("generatekey_generate");

    String generatekeys_success_str[]=generatekey_successful.split("paymentz");
    String generate_value1[]= generatekeys_success_str[1].split("123");
    String generate_str[]= generatekey_generatedkey.split("old");
    String companyname=  merchantDAO.getMerchantCompanyName((String) session.getAttribute("merchantid"));

%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">
                    <%--<div class="widget green-1" style="background-color:  #68c39f;color: #fff;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;font-size: 14px;line-height: 1.42857143;">--%>
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=generatekey_key_generation%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">

                            <div class="form-group bg-info col-md-12 has-feedback">
                                <label style="font-size: 14px;">
                                    <%--The following task that you wish to perform will require you to make adjustments to your scripts in your
                                    web pages.<br><br>--%>

                                        <%=companyname.concat(", ")%><%=generatekeys_success_str[0].concat(company)+generate_value1[0] %><br><br>

                                    <%--Without these changes your web site will not connect with the <%=company%> Payment Gateway Service.<br><br>--%>

                                      <%=generatekey_generatedkey%> <i></i>.<i></i><br><br>

                                        <%=generatekey_currentkey%><%=memberKey%><br><br><br>

                                </label>

                                <form action="/merchant/servlet/GenerateKey?ctoken<%=ctoken%>" method="post" name="form1">
                                    <div class="form-group col-md-5">
                                    </div>
                                    <div class="form-group col-md-3">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="Submit"  value="Generate New Key" name="key" class="btn btn-default"  onclick="return DoConfirm();"<%--style="width:135px;margin-left: 39%; margin-top: 22px;"--%>><%=generatekey_generate%>
                                            <%--<p><a href = "javascript:void(0)" style="colo" onclick = "document.getElementById('light').style.display='block';document.getElementById('fade').style.display='block'">

                                            </a></p>--%>
                                        </button>
                                    </div>
                                </form>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%--<%
                String pwdData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                int len = pwdData.length();
                Date date = new Date();
                Random rand = new Random(date.getTime());
                StringBuffer key = new StringBuffer();
                int index = -1;
                for (int i = 0; i < 32; i++)
                {
                    index = rand.nextInt(len);
                    key.append(pwdData.substring(index, index + 1));
                }
                request.setAttribute("key", key.toString());
                String merchantid = (String) session.getAttribute("merchantid");

                StringBuffer updQuery = new StringBuffer("update members");
                updQuery.append(" set clkey = ?  where memberid= ? " );
                Connection conn = null;

                try
                {
                    conn = Database.getConnection();
                    PreparedStatement pstmt= conn.prepareStatement(updQuery.toString());
                    pstmt.setString(1,key.toString());
                    pstmt.setString(2,merchantid);
                    pstmt.executeUpdate();
                }
                catch (SystemError se)
                {
                    //Log.error("System  Error:::::", se);
                    //System.out.println(se.toString());
                }
                catch (Exception e)
                {
                    //Log.error("Error!", e);
                }
                finally
                {
                    Database.closeConnection(conn);
                }
            %>--%>

            <%--<div id="light" class="white_content"><a href = "javascript:void(0)" onclick = "document.getElementById('light').style.display='none';document.getElementById('fade').style.display='none'">Close</a>
                &lt;%&ndash;<div class="reporttable">&ndash;%&gt;
                &lt;%&ndash;<div class="form foreground bodypanelfont_color panelbody_color">

                    <div class="form foreground bodypanelfont_color panelbody_color">
                        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-size:18px ;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> Merchant Key Generation</h2>
                        <hr class="hrform">
                    </div>

                    <div class="form-group col-md-12 has-feedback">&ndash;%&gt;

                <div class="row">

                    <div class="col-sm-12 portlets ui-sortable">

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
                                            <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><h4>Key Generated</h4></label>
                                            <input type="text" size="32" class="form-control" value=<%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("key"))%> disabled style="border: 1px solid #b2b2b2;font-weight:bold; width: 300px">
                                        </center>
                                    </div>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="fade" class="black_overlay"></div>
            </div>--%>
        </div>
    </div>
</div>
</body>
</html>
