<%@ page import="com.directi.pg.Merchants,com.directi.pg.Template" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%String company = (String)session.getAttribute("company");%>
<%
    Merchants merchants = new Merchants();
    if (!merchants.isLoggedIn(session))
    {
%>
<html>
<head>


    <title>Merchant Administration Logout</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <script src="/merchant/NewCss/libs/bootstrap/js/bootstrap.min.js"></script>
    <script src="/merchant/NewCss/libs/jqueryui/jquery-ui-1.10.4.custom.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-ui-touch/jquery.ui.touch-punch.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-detectmobile/detect.js"></script>
    <script src="/merchant/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
    <script src="/merchant/NewCss/libs/ios7-switch/ios7.switch.js"></script>
    <script src="/merchant/NewCss/libs/fastclick/fastclick.js"></script>
    <script src="/merchant/NewCss/libs/jquery-blockui/jquery.blockUI.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-bootbox/bootbox.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-slimscroll/jquery.slimscroll.js"></script>
    <script src="/merchant/NewCss/libs/jquery-sparkline/jquery-sparkline.js"></script>
    <script src="/merchant/NewCss/libs/nifty-modal/js/classie.js"></script>
    <script src="/merchant/NewCss/libs/nifty-modal/js/modalEffects.js"></script>
    <script src="/merchant/NewCss/libs/sortable/sortable.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-fileinput/bootstrap.file-input.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-select/bootstrap-select.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-select2/select2.min.js"></script>
    <script src="/merchant/NewCss/libs/magnific-popup/jquery.magnific-popup.min.js"></script>
    <script src="/merchant/NewCss/libs/pace/pace.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/NewCss/js/init.js"></script>

    <!-- Base Css Files -->
    <link href="/merchant/NewCss/libs/jqueryui/ui-lightness/jquery-ui-1.10.4.custom.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/fontello/css/fontello.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/animate-css/animate.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/nifty-modal/css/component.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/magnific-popup/magnific-popup.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/pace/pace.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/bootstrap-datepicker/css/datepicker.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/jquery-icheck/skins/all.css" rel="stylesheet" />
    <!-- Code Highlighter for Demo -->
    <link href="/merchant/NewCss/libs/prettify/github.css" rel="stylesheet" />

    <!-- Extra CSS Libraries Start -->
    <link href="/merchant/NewCss/libs/rickshaw/rickshaw.min.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/morrischart/morris.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-jvectormap/css/jquery-jvectormap-1.2.2.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-clock/clock.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/bootstrap-calendar/css/bic_calendar.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-weather/simpleweather.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/bootstrap-xeditable/css/bootstrap-editable.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <!-- Extra CSS Libraries End -->
    <link href="/merchant/NewCss/css/style-responsive.css" rel="stylesheet" />
    <!-- EOF CSS INCLUDE -->

    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>

</head>


<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0 marginwidth="0" marginheight="0">
<form action="index.jsp" method=post>
    <br><br>

    <br>

    <div>
        <div class="full-content-center animated flipInX">
            <h1><%--<img src="/merchant/images/<%=logo%>" border="0">--%></h1>
            <h1 style="font-size: 28px;">Session Expired.</h1>
            <h3>Suggestion *For security reasons, we have disabled double clicks and Back, Forward and Refresh tabs of the browser. Also,&nbsp;the&nbsp;session will expire automatically, if the browser window is idle for a long time. <br><br>*If the problem persists, please try again after clearing the <strong>Temporary Files </strong>from your web browser.</h3><br>


        </div>
    </div>
    <%--<table class=search border="0" cellpadding="2" cellspacing="0" width="80%" align=center valign="center">
        <tr>
            <td align="center">
                &lt;%&ndash;<img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">&ndash;%&gt;
                <img src="/merchant/images/lg.jpg">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="80%"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;Merchant Administration Module</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Your session has expired </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="index.jsp" class="link">here</a> to go to the Merchant login
                page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>--%>


</form>
</body>
</html>
<%
        return;
    }
%>
<%
    String helptitle = (String) request.getParameter("helptitle");
    String helpstr = "";
    String helplabel = (String) Template.defaultlabelhash.get(helptitle);
    if (helptitle.equals("TEXTCOLOR"))
        helpstr = "This is a colour of text in the page. You can put value like #ff0000 for red colour or you can directly write name of colour e.g. RED/red.";
    else if (helptitle.equals("HEADER"))
        helpstr = "This is a header of the page. This header may contain table and/or any html tag except &lt;html&gt;,&lt;title&gt; and &lt;body&gt; tag. The header should not contain any script or form.<br><br>e.g. To display your company name in center you can write following code.<br><br>&lt;center&gt;&lt;table&gt;&lt;tr&gt;&lt;td&gt; My Company &lt;/td&gt;&lt;/tr&gt;&lt;/table&gt;&lt;/center&gt;<br><br>You can also insert graphic image in the header. Click <a href=\"/merchant/help.jsp?helptitle=IMAGE\" >here </a> for more information.";
    else if (helptitle.equals("BGCOLOR"))
        helpstr = "This is a background colour of text in the page. You can put value like #ff0000 for red colour or you can directly write name of colour e.g. RED/red.";
    else if (helptitle.equals("FONTFACE"))
        helpstr = "This is font of text in the page. You can write font like arial,verdana,helvetica etc. This font will be reflected in each" + company + "page on your site.";
    else if (helptitle.equals("BACKGROUND"))
        helpstr = "This is background image in the page. This image will appear as background in each page on your site.To know more about how to put image click <a href=\"/merchant/help.jsp?helptitle=IMAGE\" >here</a>.";
    else if (helptitle.equals("HIGHLIGHT_TEXT_COLOR"))
        helpstr = "Colour of message text which is displayed on completion of transaction.<br>The message  is  : 'Please Click on Continue below in order to complete the Transaction'.<br><br>This message will be displayed on last page of " + company + ".To get exactly idea about this fill in any colour in text box and see preview.";
    else if (helptitle.equals("IMAGE"))
    {
        helplabel = "Images";
        helpstr = "If you want to put image you have to upload it. You can do it by clicking Manage Images button. You can upload maximum three images. Once you upload require image , appropriate tag will be displayed in text box opposite to Images label. You should use this tag where ever you want to put that image.<br><br>e.g. Suppose your uploaded image has tag &lt;#IMAGE1#&gt; To display image below your company name you can write following code in header.<br><br>&lt;center&gt;&lt;table&gt;&lt;tr&gt;&lt;td&gt; My Company &lt;/td&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;&lt;#IMAGE1#&gt;&lt;/td&gt;&lt;/tr&gt;&lt;/table&gt;&lt;/center&gt;<br><br>The size of image will be same as image size.";
    }
    else if (helptitle.equals("HIGHRISKALERT"))
    {
        helplabel = "HIGH RISK FRAUD ALERT";
        helpstr = "If you select 'Yes' option then all transaction which are defined as HIGH RISK FRAUD ALERT by " + company + " credit card processor will be considered as proof require transaction. The customer has submit proof document in order to process this transaction ahead. Check your daily mails on HIGH RISK FRAUD ALERT to know more about it.";
    }
    else if (helptitle.equals("DATAMISMATCHALERT"))
    {
        helplabel = "Billing Address data mismatch FRAUD ALERT";
        helpstr = "If you select 'Yes' option then whenever somebody do transaction using incorrect billing address data, that transaction will be considered as proof require transaction. The customer has submit proof document in order to process this transaction ahead. If you select 'No' option then in such cases we will send you alert mail stating that the customer has entered wrong billing address data.";
    }
    else if (helptitle.equals("MITIGATIONMAIL"))
    {
        helplabel = "Risk Mitigation and Fraud Report Mail";
        helpstr = "If you select 'Yes' option then we will daily send you Risk Mitigation and Fraud Report that will help you minimise fraud on your website.";
    }
    else if (helptitle.equals("AUTOREDIRECT"))
    {
        helplabel = "Auto Redirect";
        helpstr = "This flag determines whether you wish to display a transaction confirmation page to the customer. By default after the Transaction is processed, the result is displayed to the Customer with a button to continue back to your website. If you do not want us to display the result of the transaction to the Customer and directly redirect him to your website, you can set this flag ON.";
    }
    else if (helptitle.equals("CHECKSUMALGO"))
    {
        helplabel = "Checksum Algorithm";
        helpstr = "A checksum is a form of redundancy check for protecting the integrity of data by detecting errors in data, when data is either transmitted over a network [such as the Internet] or when data is stored [such as in a computer's Hard Drive]. The " + company + " Payment Gateway allows  you to secure all communication exchanged between your website and our server, by generating a checksum. There are many algorithms for generating checksum.Previously we used Adler32 as Checksum Algorithm .\n" +
                "<br><br>" +
                "Now we recommend using the MD5 Checksum Algorithm, since it is more secure than Adler32. You may select the Adler32 Checksum Algorithm if you have already integrated your website with " + company + " previously, using the Adler32 Algorithm and can not immediately upgrade your integration to the MD5 Algorithm.";
    }
    else if (helptitle.equals("TEMPLATE"))
    {
        helplabel = "Enable Template";
        helpstr = "The checkbox Enable Template represents whether or not the template designed is applied. <BR> Keep the checkbox enabled in order to apply the template to your Transaction Pages";
    }
    else if(helptitle.equals("PHONE1"))
    {
        helplabel = "Phone Number 1";
        helpstr="The Phone number that will be present in the Emails Signature of the Email sent while sending the customer an Invoice";
    }
    else if(helptitle.equals("PHONE2"))
    {
        helplabel = "Phone Number 2";
        helpstr="The Second Phone number that will be present in the Emails Signature of the Email sent while sending the customer an Invoice";
    }
    else if(helptitle.equals("EMAILS"))
    {
        helplabel = "Email Id present in Invoice Emails";
        helpstr="The contact Email Id's that will be present in the Emails Signature of the Email sent while sending the customer an Invoice";
    }
%>
<html>
<head>

    <title>Merchant Help</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script src="/merchant/NewCss/libs/bootstrap/js/bootstrap.min.js"></script>
    <script src="/merchant/NewCss/libs/jqueryui/jquery-ui-1.10.4.custom.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-ui-touch/jquery.ui.touch-punch.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-detectmobile/detect.js"></script>
    <script src="/merchant/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
    <script src="/merchant/NewCss/libs/ios7-switch/ios7.switch.js"></script>
    <script src="/merchant/NewCss/libs/fastclick/fastclick.js"></script>
    <script src="/merchant/NewCss/libs/jquery-blockui/jquery.blockUI.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-bootbox/bootbox.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-slimscroll/jquery.slimscroll.js"></script>
    <script src="/merchant/NewCss/libs/jquery-sparkline/jquery-sparkline.js"></script>
    <script src="/merchant/NewCss/libs/nifty-modal/js/classie.js"></script>
    <script src="/merchant/NewCss/libs/nifty-modal/js/modalEffects.js"></script>
    <script src="/merchant/NewCss/libs/sortable/sortable.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-fileinput/bootstrap.file-input.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-select/bootstrap-select.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-select2/select2.min.js"></script>
    <script src="/merchant/NewCss/libs/magnific-popup/jquery.magnific-popup.min.js"></script>
    <script src="/merchant/NewCss/libs/pace/pace.min.js"></script>
    <script src="/merchant/NewCss/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/NewCss/js/init.js"></script>

    <!-- Base Css Files -->
    <link href="/merchant/NewCss/libs/jqueryui/ui-lightness/jquery-ui-1.10.4.custom.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/fontello/css/fontello.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/animate-css/animate.min.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/nifty-modal/css/component.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/magnific-popup/magnific-popup.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/pace/pace.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/bootstrap-datepicker/css/datepicker.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/jquery-icheck/skins/all.css" rel="stylesheet" />
    <!-- Code Highlighter for Demo -->
    <link href="/merchant/NewCss/libs/prettify/github.css" rel="stylesheet" />

    <!-- Extra CSS Libraries Start -->
    <link href="/merchant/NewCss/libs/rickshaw/rickshaw.min.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/morrischart/morris.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-jvectormap/css/jquery-jvectormap-1.2.2.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-clock/clock.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/bootstrap-calendar/css/bic_calendar.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/sortable/sortable-theme-bootstrap.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/jquery-weather/simpleweather.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/libs/bootstrap-xeditable/css/bootstrap-editable.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <!-- Extra CSS Libraries End -->
    <link href="/merchant/NewCss/css/style-responsive.css" rel="stylesheet" />
    <!-- EOF CSS INCLUDE -->

    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>

</head>

<body style="margin: 0px" leftmargin=0 topmargin=0 marginwidth="0" marginheight="0">

<form action="/merchant/index.jsp" method=post>
    <div <%--class="container"--%>>
        <div class="full-content-center animated flipInX">

            <h2><b>
                What is <%=helplabel%>?

            </b>
            </h2>

            <%--<table class=search border="0" cellpadding="2" cellspacing="0" width="80%"
                    align=center valign="center">

                <tr><td>&nbsp;</td></tr>
                <tr>
                    <td class="textb"><%=helpstr%></td>
                </tr>
                <tr><td>&nbsp;</td></tr>

            </table>--%>
            <h4><%=helpstr%></h4>
        </div>
    </div>
</form>
</body>
</html>