<%@ page language="java" import="com.logicboxes.util.ApplicationProperties,java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Settings > Merchant Profile</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css">
</head>

<body>

<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">
            <img src="/merchant/images/logo.gif" border="0">
        </td>
    </tr>
</table>
<br>

<br>

<p align="center" class="title">New Merchant Signup Form</p>

<%
   // String username = "";
    String passwd = "";
    String conpasswd = "";
    String tpasswd = "";
    String contpasswd = "";
    String company_name = "";
    String brandname = "";
    String sitename = "";
    String contact_emails = "";
    String contact_persons = "";
    String telno = "";
    String faxno = "";
    String address = "";
    String city = "";
    String state = "";
    String country = "";
    String zip = "";
    String notifyemail = "";
    String role="Merchant";
    String username=(String)session.getAttribute("username");
    String actionExecutorId=(String)session.getAttribute("merchantid");
    String actionExecutorName=role+"-"+username;



    if (request.getParameter("MES") != null)
    {
        String mes = (String) request.getParameter("MES");
        if (mes.equals("F"))
        {
            Hashtable details = (Hashtable) request.getAttribute("details");
            if ((String) details.get("login") != null) username = (String) details.get("login");
            if ((String) details.get("company_name") != null) company_name = (String) details.get("company_name");
            if ((String) details.get("brandname") != null) brandname = (String) details.get("brandname");
            if ((String) details.get("sitename") != null) sitename = (String) details.get("sitename");
            if ((String) details.get("contact_emails") != null) contact_emails = (String) details.get("contact_emails");
            if ((String) details.get("contact_persons") != null)
                contact_persons =(String) details.get("contact_persons");
            if ((String) details.get("telno") != null) telno = (String) details.get("telno");
            if ((String) details.get("faxno") != null) faxno =(String) details.get("faxno");
            if ((String) details.get("address") != null) address = (String) details.get("address");
            if ((String) details.get("city") != null) city = (String) details.get("city");
            if ((String) details.get("state") != null) state = (String) details.get("state");
            if ((String) details.get("country") != null) country = (String) details.get("country");
            if ((String) details.get("zip") != null) zip = (String) details.get("zip");
            if ((String) details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
            if ((String) details.get("actionExecutorId") != null) actionExecutorId = (String) details.get("actionExecutorId");
            if ((String) details.get("actionExecutorName") != null) actionExecutorName = (String) details.get("actionExecutorName");
        }
    }

%>


<br>
<table bgcolor="#E4D6C9" align="center" width="80%" cellpadding="2" cellspacing="2">
<form action="/merchant/servlet/NewMerchant" method="post" name="form1">
<tr>
    <td>

    <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#F1EDE0" align="center">

        <tr bgcolor="#7D0000">
            <td width="2%" class="textb">&nbsp;</td>
            <td colspan="3" class="label">
                Merchant Information (Step 1)
            </td>

        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td width="43%" class="textb">Username*</td>
            <td width="5%" class="textb">:</td>
            <td width="50%"><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Password*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Password" value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Confirm Password*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Password" value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Withdrawal Password*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Password" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tpasswd)%>" name="tpasswd" size="30"></td>
        </tr>
        <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td width="220" class="textb">Confirm Withdrawal Password*</td>
            <td width="10" class="textb">:</td>
            <td width="260"><input class="textBoxes" type="Password" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contpasswd)%>" name="contpasswd"
                                   size="30"></td>
        </tr>
        <tr><td colspan="4"><hr color="#7D0000"></td></tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Company Name*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="company_name" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Brand Name*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(brandname)%>" name="brandname" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Site URL</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Contact email address*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Contact Person's Name*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Contact Telephone Number*</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" name="telno" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb">Contact Fax Number</td>
            <td class="textb">:</td>
            <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(faxno)%>" name="faxno" size="30"></td>
        </tr>
        <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb" valign="top">Address*</td>
            <td class="textb" valign="top">:</td>
            <td valign="top">
                <textarea class="textBoxes" name="address" rows="5" cols="30"><%=ESAPI.encoder().encodeForHTMLAttribute(address)%></textarea>
            </td>
        </tr>
        <td class="textb">&nbsp;</td>
        <td class="textb">City*</td>
        <td class="textb">:</td>
        <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(city)%>" name="city" size="30"></td>
</tr>
<tr>
    <td width="2%" class="textb">&nbsp;</td>
    <td class="textb">State</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(state)%>" name="state" size="30"></td>
</tr>
<td class="textb">&nbsp;</td>
<td class="textb">Country*</td>
<td class="textb">:</td>
<td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" name="country" size="30"></td>
</tr>
<td class="textb">&nbsp;</td>
<td class="textb">Pin Code*</td>
<td class="textb">:</td>
<td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(zip)%>" name="zip" size="30"></td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Notification email address*</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>" name="notifyemail" size="30"></td>
</tr>
<tr>
    <td colspan="4" class="label" align="center">
        <input type="hidden" value="1" name="step">
        <input type="Submit" value="Next >" name="submit" class="submit">
    </td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
</table>

</td>
</tr>
</form>
</table>
</body>

</html>
