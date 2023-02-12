<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<%@ include file="ietest.jsp" %>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Settings > Change Withdrawal Password</title>
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))

                return false;
            else
                return true;
        }
        function check()
        {
            var msg = "Following information are missing:-\n";
            var flag = "false";

            if (document.form1.oldpwd.value.length == 0 || document.form1.newpwd.value.length == 0 || document.form1.confirmpwd.value.length == 0)
            {
                msg = msg + "\nPassword field can not be empty.";
                document.form1.oldpwd.select();
                document.form1.oldpwd.focus();
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

    <link rel="stylesheet" type="text/css" href="style.css">
</head>

<body >

<%@ include file="Top.jsp" %>

<br>

<p align="center" class="title"><%=company%> Merchant Change Withdrawal Password</p>


<br>
<%
    String errormsg= ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
    if (errormsg == null)
    {
        errormsg = "";
    }
    if (!errormsg.equals(""))
        {
            out.println("<table align=\"center\" width=\"500\" ><tr><td><font class=\"text\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. ");
            out.println("<br><font face=\"arial\" color=\"red\" align size=\"2\">");
            errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
            out.println(errormsg);
            out.println("<br></font>");
            out.println("</font></td></tr></table>");
        }
%>

<form action="/merchant/servlet/ChangeTransPassword?ctoken=<%=ctoken%>" method="post" name="form1" onsubmit="return check();">
      <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
  <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
    <table  align="center" width="500" cellpadding="2" cellspacing="2">
        <tr>
            <td>
                <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#CCE0FF" align="center">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <tr bgcolor="#007ACC">
                        <td width="10" class="textb">&nbsp;</td>
                        <td colspan="3" class="label">
                            Change Withdrawal Password
                        </td>
                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <tr>
                        <td width="10" class="textb">&nbsp;</td>
                        <td width="220" class="textb">Old Password</td>
                        <td width="10" class="textb">:</td>
                        <td width="260"><input id="oldpwd" class="textBoxes" type="password" maxlength="125"  value="" name="oldpwd" size="30"></td>
                    </tr>
                    <tr>
                        <td width="10" class="textb">&nbsp;</td>
                        <td width="220" class="textb">New Password</td>
                        <td width="10" class="textb">:</td>
                        <td width="260"><input id="newpwd" class="textBoxes" type="password" maxlength="125" value="" name="newpwd" size="30"></td>
                         </tr>
                     <tr>
                        <td width="10" class="textb">&nbsp;</td>
                        <td width="100%" colspan="4" class="textb" >(Password length should be at least 8 & must contain alphabet, numeric, and special characters like !@#$%)</td>
                    </tr>
                    <tr>
                        <td width="10" class="textb">&nbsp;</td>
                        <td width="220" class="textb">Confirm New Password</td>
                        <td width="10" class="textb">:</td>
                        <td width="260"><input id="confirmpwd" class="textBoxes" type="password" maxlength="125" value="" name="confirmpwd" size="30">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="label" align="center">
                            <input  id="submit"  type="Submit" value="Change" name="submit" class="submit" disabled="disabled">
                        </td>
                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                </table>
            </td>
        </tr>
    </table>
</form>
<table align=center valign="center">

    <tr><td>&nbsp;</td></tr>
    <tr><td class="textb"> KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES </td></tr>
    </table>

<p>&nbsp;&nbsp; </p>
</body>

</html>
