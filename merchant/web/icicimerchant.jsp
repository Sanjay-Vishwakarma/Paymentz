<%@ page language="java" import="com.logicboxes.util.ApplicationProperties,java.util.Hashtable" %>
<%String company = (String)session.getAttribute("company");%>
<%@ include file="ietest.jsp" %>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Settings > Merchant Profile</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
    <script language="javascript">
        function check()
        {

            if (document.form1.directors.value.length == 0)
            {
                alert("Please enter names of directors.");
                document.form1.directors.select();
                return false;
            }
            if (document.form1.employees.value.length == 0)
            {
                alert("Please enter names of employees.");
                document.form1.employees.select();
                return false;
            }
            if (document.form1.potentialbusiness.value.length == 0 || isNaN(document.form1.potentialbusiness.value))
            {
                alert("Please enter Potential Business Volumes per month in Rupees.");
                document.form1.potentialbusiness.select();
                return false;
            }
            if (document.form1.registeredaddress.value.length == 0)
            {
                alert("Please enter Registered Address.");
                document.form1.registeredaddress.select();
                return false;
            }
            if (document.form1.bussinessaddress.value.length == 0)
            {
                alert("Please enter Business Address.");
                document.form1.bussinessaddress.select();
                return false;
            }
            if (document.form1.bankname.value.length == 0)
            {
                alert("Please enter Bankname.");
                document.form1.bankname.select();
                return false;
            }
            if (document.form1.branch.value.length == 0)
            {
                alert("Please enter branch name.");
                document.form1.branch.select();
                return false;
            }
            if (document.form1.accno.value.length == 0)
            {
                alert("Please enter Account Number.");
                document.form1.accno.select();
                return false;
            }

        }
    </script>
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
    String directors = "";
    String employees = "";
    String potentialbusiness = "";
    String registeredaddress = "";
    String bussinessaddress = "";
    String bankname = "";
    String branch = "";
    String acctype = "CD";
    String accno = "";


    if (request.getParameter("MES") != null)
    {
        String mes = (String) request.getParameter("MES");
        if (mes.equals("F"))
        {
            Hashtable details = (Hashtable) request.getAttribute("details");
            if ((String) details.get("directors") != null) directors = (String) details.get("directors");
            if ((String) details.get("employees") != null) employees = (String) details.get("employees");
            if ((String) details.get("potentialbusiness") != null)
                potentialbusiness = (String) details.get("potentialbusiness");
            if ((String) details.get("registeredaddress") != null)
                registeredaddress = (String) details.get("registeredaddress");
            if ((String) details.get("bussinessaddress") != null)
                bussinessaddress = (String) details.get("bussinessaddress");
            if ((String) details.get("bankname") != null) bankname = (String) details.get("bankname");
            if ((String) details.get("branch") != null) branch = (String) details.get("branch");
            if ((String) details.get("acctype") != null) acctype = (String) details.get("acctype");
            if ((String) details.get("accno") != null) accno = (String) details.get("accno");

        }
    }
%>


<br>
<table bgcolor="#E4D6C9" align="center" width="80%" cellpadding="2" cellspacing="2">
<form action="/merchant/servlet/NewMerchant" method="post" name="form1"
      onsubmit="return check();">
<tr>
<td>

<table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#F1EDE0" align="center">

<tr bgcolor="#7D0000">
    <td width="2%" class="textb">&nbsp;</td>
    <td colspan="3" class="label">
        Merchant Information (Step 2)
    </td>

</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="2%" class="textb">&nbsp;</td>
    <td width="43%" class="textb" valign="top">Name of all Directors *</td>
    <td width="5%" class="textb" valign="top">:</td>
    <td width="50%" valign="top">
        <textarea class="textBoxes" name="directors" rows="5" cols="30"><%=directors%></textarea>
    </td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb" valign="top">Name of Key Employees *</td>
    <td class="textb" valign="top">:</td>
    <td valign="top">
        <textarea class="textBoxes" name="employees" rows="5" cols="30"><%=employees%></textarea>
    </td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="text" valign="top"><span class="textb">Potential Business Volumes per month*</span><br>(Amount of
        transactions per month in INR)</td>
    <td class="textb" valign="top">:</td>
    <td valign="top">
        <input type="text" class="textBoxes" name="potentialbusiness" value="<%=potentialbusiness%>" size="30">

    </td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb" valign="top">Registered Address & Pincode *</td>
    <td class="textb" valign="top">:</td>
    <td valign="top">
        <textarea class="textBoxes" name="registeredaddress" rows="5" cols="30"><%=registeredaddress%></textarea>
    </td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb" valign="top">Business Address & Pincode *</td>
    <td class="textb" valign="top">:</td>
    <td valign="top">
        <textarea class="textBoxes" name="bussinessaddress" rows="5" cols="30"><%=bussinessaddress%></textarea>
    </td>
</tr>
<tr><td colspan="4"><hr color="#7D0000"></td></tr>

<tr><td class="textb" colspan="4">Bank Account Details</td></tr>

<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Bank Name *</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Text" value="<%=bankname%>" name="bankname" size="30"></td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Branch *</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Text" value="<%=branch%>" name="branch" size="30"></td>
</tr>
<td class="textb">&nbsp;</td>
<td class="textb">Account Type *</td>
<td class="textb">:</td>
<td>
    <select name="acctype" class="textBoxes">
        <%
            if (acctype.equals("CD"))
            {
        %>
        <option value="CD" selected>Current</option>
        <%
        }
        else
        {
        %>
        <option value="CD">Current</option>
        <%
            }
            if (acctype.equals("SB"))
            {
        %>
        <option value="SB" selected>Savings</option>
        <%
        }
        else
        {
        %>
        <option value="SB">Savings</option>
        <%
            }
            if (acctype.equals("OD"))
            {
        %>
        <option value="OD" selected>Over Draft</option>
        <%
        }
        else
        {
        %>
        <option value="OD">Over Draft</option>
        <%
            }
        %>

    </select>

</td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Account Number *</td>
    <td class="textb">:</td>
    <td><input class="textBoxes" type="Text" value="<%=accno%>" name="accno" size="30"></td>
</tr>
<tr>

<tr>
    <td colspan="4" class="label" align="center">
        <input type="hidden" value="2" name="step">
        <input type="Submit" value="Submit" name="submit" class="submit">
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
