<%@ page import="com.directi.pg.Merchants,
                 org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 java.util.Enumeration,
                 java.security.NoSuchAlgorithmException" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>




<%

    String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
    User user =  (User)session.getAttribute("ESAPIUserSessionKey");
    Hashtable hiddenvariables= new Hashtable();


    String ctoken= null;
     if(user!=null)
     {
         ctoken = user.getCSRFToken();

     }
    Merchants merchants = new Merchants();
    if (!merchants.isLoggedIn(session))
    {


%>

<html>
<head>
    <title>Virtual Merchant  Logout</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>

</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<form action="index.jsp" method=post>
    <br><br>

    <br>
    <table class=search border="0" cellpadding="0" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;Merchant Virtual Terminal</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Your session has expired </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="virtualLogin.jsp" class="link">here</a> to go to the Virtual login
                page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>
</form>
</body>
</html>
<%
        return;
    }



        String memberid = (String)session.getAttribute("merchantid");
        String currency =  (String)session.getAttribute("currency");


%>
<html>
<head>
<title>Merchant Payments Confirmation</title>

</head>

<br><br>
<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">

            <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">

        </td>
    </tr>
</table>
<br><br>

<body bgcolor="white">
<font size=4>
   
    <%

        String desc = request.getParameter("desc");
        String amount = request.getParameter("amount");
        String status = request.getParameter("status");
        String checksum = request.getParameter("newchecksum");



        if ("Y".equals(status))
        {
            out.println("Thank you for shopping with us. Your credit card has been charged and your transaction is successful. We will be shipping your order to you soon.");

            //Here you need to put in, the routines for a successful
            //transaction such as sending an email to customer,
            //setting database status, informing logistics etc etc
        }
        else if ("N".equals(status))
        {
            out.println("Thank you for shopping with us. However it seems your credit card transaction failed.");

            //Here you need to put in, the routines for a failed
            //transaction such as sending an email to customer
            //setting database status etc etc
        }
        else if ("P".equals(status))
        {
            out.println("Your Transaction has been classified as a HIGH RISK Transaction by our Credit Card Processor.This requires you to Fax us an Authorisation for this transaction in order to complete the processing. This process is required by our Credit Card Processor to ensure that this transaction is being done by a genuine card-holder. The transaction will NOT be completed (and your card will NOT be charged) if you do not fax required documents");

            //Here you need to put in, the routines for a HIGH RISK
            //transaction such as sending an email to customer and explaining him a procedure,
            //setting database status etc etc

        }
        else
        {
            out.println("Security Error. Illegal access detected");

            //Here you need to simply ignore this and dont need
            //to perform any operation in this condition
        }


    %>
</font>
</body>
</html>
