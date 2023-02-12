<%@ page import="java.util.HashMap" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="custSuppDash.jsp"%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>CHANGE PASSWORD</title>
    <script type="text/javascript" src="/support/javascript/jquery-1.7.1.js?ver=1"></script>
    <script type="text/javascript" src="/support/javascript/jquery.jcryption.js?ver=1"></script>
    <script type="text/javascript">
        $(document).ready(function() {

            document.getElementById('button').disabled =  false;
            $("#button").click(function() {
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
</head>
<body style="background-color: #ecf0f1">

<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    log.debug("inside changePassword.jsp");
%>
<br><br><br><br><br>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <p> Customer Support Change Password</p>
            </div><br><br>
            <%

                String update= request.getParameter("MES");
                Hashtable error = null;

                if(update!=null)
                {  if(update.equals("X"))
                {
                    if(request.getAttribute("error")!=null)
                        error= (Hashtable) request.getAttribute("error");

                    if(!error.isEmpty() && error!=null)
                    {

                        Enumeration enu = error.keys();
                        String key = "";
                        String value = "";
                        while (enu.hasMoreElements())
                        {
                            key = (String) enu.nextElement();
                            value = (String) error.get(key);
                            out.println("<center><font class=\"textb\" size=\"3\"><b>"+value+"</b></font></center>");
                        }
                    }
                }
                    if ("OLD".equals(update))
                    {
                        out.println("<center><font class=\"textb\" size=\"2\"><b>Old password invalid</b></font></center>");
                    }
                    if ("Y".equals(update))
                    {
                        out.println("<center><font class=\"textb\" size=\"2\"><b>Change password successfull</b></font></center>");
                    }
                    if("C".equals(update))
                    {
                        out.println("<center><font class=\"textb\" size=\"2\"><b>Re-enter new password & confirm password </b></font></center>");
                    }
                    if("N".equals(update))
                    {
                        out.println("<center><font class=\"textb\" size=\"2\"><b>New password cannot be the old 5 passwords</b></font></center>");
                    }
                }%>
            <form action="/support/servlet/changePassword?ctoken=<%=ctoken%>" method="post">
                <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
                <table  align="center" width="85%" cellpadding="2" cellspacing="2" style="margin-left:55px; ">

                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb" >Old Password</td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="30%%"><input id="oldpwd"  type="password" maxlength="125"  value="" name="oldPass" size="40" class="txtbox"  style="font-size: 12px;width: 250px"></td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%%"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%%"></td>
                                </tr>

                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">New Password</td>
                                    <td class="textb">:</td>
                                    <td width="30% "><input id="newpwd"  type="password" value="" maxlength="125" name="newPass" size="40" class="txtbox" style="font-size: 12px;width: 250px"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>

                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td width="50%" colspan="3" class="textb" style="font-size: 10px">(Password length should be at least 8 & must contain alphabet, numeric,<br> and special characters like !@#$%)</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Confirm New Password</td>
                                    <td class="textb">:</td>
                                    <td width="30%"><input id="confirmpwd"  type="password" value="" maxlength="125" name="confPass" size="40" class="txtbox"  style="font-size: 12px;width: 250px"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%%"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" >
                                        <button type="submit" class="buttonform" name="B1" style="width: 140px" id="button" disabled="disabled">
                                            <span><i class="fa fa-clock-o"></i></span>
                                            Change Password</button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%%"></td>
                                </tr>
                            </table></td></tr>
                </table>
            </form>
        </div>
    </div>
</div>
</div>
</body>
</html>