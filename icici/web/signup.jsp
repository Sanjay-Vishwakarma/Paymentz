<%@ page language="java" import="com.logicboxes.util.ApplicationProperties,java.util.Hashtable
                                 " %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%String company = ApplicationProperties.getProperty("COMPANY");%>
<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title><%=company%> Merchant Settings > Member Profile</title>
<script language="javascript">
function isint(form)
{
    if (isNaN((form.numrows.value)))
        return false;
    else
        return true;
}
function check()
{
    var msg = "Following information are missing:-\n";
    var flag = "false";
    if (document.form1.username.value.length == 0)
    {
        msg = msg + "\nPlease enter username.";
        document.form1.username.select();
        document.form1.username.focus();
        flag = "true";
    }
    if (document.form1.passwd.value.length == 0 || document.form1.conpasswd.value.length == 0)
    {
        //alert("Password field can not be empty.");
        msg = msg + "\nPassword field can not be empty.";
        document.form1.passwd.select();
        document.form1.passwd.focus();
        flag = "true";
    }
    if (document.form1.contact_emails.value.length == 0 || document.form1.contact_emails.value.indexOf(".") <= 0 || document.form1.contact_emails.value.indexOf("@") <= 0)
    {
        //alert("Please enter valid contact emailaddress.");
        msg = msg + "\nPlease enter valid contact emailaddress.";
        document.form1.contact_emails.select();
        document.form1.contact_emails.focus();
        flag = "true";
    }
    if (flag == "true")
    {
        alert(msg);
        return false;
    }
    else
        return true;
}
function test()
{
    document.form1.username.focus();
    return true;
}
</script>

</head>

<body onload="return test();">
<p align="center" class="title">New Admin Signup Form</p>

<%
    String username = "";
    String passwd = "";
    String conpasswd = "";
    String contact_emails = "";
    
%>

<table  align="center" width="60%" cellpadding="2" cellspacing="2">
<form action="" method="post" name="form1"
      onsubmit="return check();">
<tr>
<td>

<table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#CCE0FF" align="center">

<tr bgcolor="#007ACC">
    <td width="2%" class="textb">&nbsp;</td>
    <td colspan="3" class="label">
        Admin Information
    </td>

</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="2%" class="textb">&nbsp;</td>
    <td width="43%" class="textb">Username*</td>
    <td width="5%" class="textb">:</td>
    <td width="50%"><input class="textBoxes" type="Text" maxlength="100"  maxlength = 100 value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35"></td>
</tr>

<tr>
    <td class="textb">&nbsp;</td>
    <td class="text"><span class="textb">Password*</span><br>
        (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$%^&)</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Password" maxlength="20"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="35"></td>

</tr>
<tr>

<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Confirm Password*</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Password" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="35"></td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Contact email address*</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" size="35"></td>
</tr>
<tr>
<td colspan="4" class="label" align="center">
    <input type="hidden" value="1" name="step">
    <input type="Submit" value="submit" name="submit" class="submit">
</td>
</tr>
<tr height="15"><td colspan="4"></td></tr>
</table>

</td>
</tr>
</form>
</table>
</body>

</html>