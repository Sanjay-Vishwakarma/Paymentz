<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>

<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title> Settings> Change Password</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
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

        function hideshowpass(spanid,inputid)
        {
            var x = document.getElementById(inputid);
            if (x.type === "password")
            {
                $("#"+spanid).removeClass('fa-eye-slash').addClass('fa-eye')
                x.type = "text";
            }
            else
            {
                $("#"+spanid).removeClass('fa-eye').addClass('fa-eye-slash')
                x.type = "password";
            }
        }
    </script>



</head>

<body class="bodybackground">

<div class="row" >
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Admin Change Password
            </div><br><br>


    <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<%
    if (request.getParameter("MES") != null)
    {
        String mes = ESAPI.encoder().encodeForHTML((String) request.getParameter("MES"));
        if (mes.equals("FP"))
        {
%>
<table width="100%" align="center">
    <tr class="textb">
        <td>Please change your password as your have logged on using a temporary password.</td>
    </tr>
</table>
<%
    }
    if (mes.equals("UP"))
    {
%>
<table width="100%" align="center" style="margin-left:250px ">
    <tr class="textb">
        <td>Please change your password as you have not updated your password since 90 days</td>
    </tr>
</table>
<%
    }
    if (mes.equals("ERR"))
    {
%>
<table  align="center"  width="60%" >
    <tr class="text" align="center" >
        <td> <%

            String error=(String) request.getAttribute("error"); //invalid password
            out.println("<font class=\"textb\"><b>");
            out.println(error);
            out.println("</b></font>");

         %> </td>
    </tr>
</table>
<%
        }

    }

%>
<form action="/icici/servlet/ChngPassword?ctoken=<%=ctoken%>" method="post" name="form1">
    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
    <div>
        <table  align="center" width="60%" cellpadding="2" cellspacing="2">
            <tr>
                <td>
                    <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                        <tr><td colspan="4">&nbsp;</td></tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb">Old Password</td>
                            <td width="5%" class="textb">:</td>
                            <td width="50%%"><input id="oldpwd" class="txtbox" type="password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');" value="" name="oldpwd" size="30">
                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','oldpwd')" style="position: relative;right: 27;margin-top: 3;"></span>

                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb">New Password</td>
                            <td class="textb">:</td>
                            <td><input id="newpwd" class="txtbox" type="password" value="" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                       name="newpwd" size="30">
                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass1" onclick="hideshowpass('showHidepass1','newpwd')" style="position: relative;right: 27;margin-top: 3;"></span>

                            </td>
                        </tr>

                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td width="50%" colspan="3" class="textb"  >(Password length should be at least 8 & must contain alphabet, numeric,<br> and special characters like !@#$%)</td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb">Confirm New Password</td>
                            <td class="textb">:</td>
                            <td><input id="confirmpwd" class="txtbox" type="password" value="" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                       name="confirmpwd" size="30">
                                <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','confirmpwd')" style="position: relative;right: 27;margin-top: 3;"></span>

                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb"></td>
                            <td class="textb"></td>
                            <td>

                                <button id="submit" type="Submit" name="submit" class="buttonform" disabled="disabled" style="margin-left:50px; ">
                                    <span><i class="fa fa-lock"></i></span>
                                    &nbsp;&nbsp;Change
                                </button>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</form>
<p>&nbsp;&nbsp; </p>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</div>
</div>
<table align=center valign="center">

    <tr><td>&nbsp;</td></tr>
    <tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
    <tr><td class="textb" style="color:#001962">  KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES </td></tr>
</table>

<p>&nbsp;&nbsp; </p>
</div>
</body>

</html>
