<%@ page language="java" import="com.directi.pg.Functions,org.owasp.esapi.ESAPI
                                 ,java.util.Hashtable" %>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 5/21/14
  Time: 12:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<html>
<head>
    <title>ADD new Customer Support </title>
    <%--<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <script type="text/javascript" src="/support/javascript/jquery-1.7.1.js?ver=1"></script>
    <script type="text/javascript" src="/support/javascript/jquery.jcryption.js?ver=1"></script>--%>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>
    <script language="javascript">


        $(document).ready(function() {

            document.getElementById('submit').disabled =  false;
            $("#submit").click(function() {
                var encryptedString1 =  $.jCryption.encrypt($("#password").val(), $("#ctoken").val());
                document.getElementById('password').value =  encryptedString1;

                var encryptedString2=  $.jCryption.encrypt($("#confirmpassword").val(), $("#ctoken").val());
                document.getElementById('confirmpassword').value =  encryptedString2;
                document.getElementById('isEncrypted').value =  true;
            });

        });

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


            if (document.form1.emailid.value.length == 0 || document.form1.emailid.value.indexOf(".") <= 0 || document.form1.emailid.value.indexOf("@") <= 0)
            {
                //alert("Please enter valid contact emailaddress.");
                msg = msg + "\nPlease enter valid contact emailaddress.";
                document.form1.emailid.select();
                document.form1.emailid.focus();
                flag = "true";
            }

            if (document.form1.phoneno.value.length == 0)
            {
                //alert("Please enter numeric value Telephone number.");
                msg = msg + "\nPlease enter numeric value Telephone number.";
                document.form1.phoneno.select();
                document.form1.phoneno.focus();
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
            //    alert("hi");
            document.form1.username.focus();
            return true;
        }

    </script>
</head>
<body <%if(request.getAttribute("username")==null){%>onload="form1.username.focus()"<%}%>>
<form action="/icici/servlet/CustSuppSignup?ctoken=<%=ctoken%>" method="post" name="form1" >
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default" >
                <div class="panel-heading" >
                    Customer Support Information

                </div>

    <%
        String username = "";
        String csPassword = "";
        String csconpasswd = "";
        String csename = "";
        //String company_type = "";
        String brandname = "";
        String sitename = "";
        String emails = "";
        String contact_persons = "";
        String csContactNumber= "";
        String faxno = "";
        String address = "";
        String city = "";
        String state = "";
        String country = "";
        String zip = "";
        String notifyemail = "";
        String potentialbusiness="";
        String currency="";
        String errormsg = (String)request.getAttribute("error");
        Functions functions=new Functions();
        if (errormsg == null)
        {
            errormsg = "";
        }
        if (request.getParameter("MES") != null)
        {
            String mes = (String) request.getParameter("MES");
            if (mes.equals("success"))
            {
                out.println("<center><table align=\"center\" width=\"60%\" style=\"bgcolor: white\" ><tr><td><font class=\"text\" >New customer support executive created successfully.</font>");
                out.println("</font>");
                out.println("</td></tr></table></center>");

            }
            else
            {
                Hashtable details = (Hashtable)request.getAttribute("details");
                if(details!=null)
                {
                    if (functions.isValueNull((String)details.get("csLogin")))
                    {
                        username = (String)details.get("csLogin");
                    }
                    if (functions.isValueNull((String)details.get("csName")))
                    {
                        csename = (String) details.get("csName");
                    }
                    if (functions.isValueNull((String)details.get("csEmail")))
                    {
                        emails = (String) details.get("csEmail");
                    }
                    if (functions.isValueNull((String)details.get("csContactNumber")))
                    {
                        csContactNumber = (String) details.get("csContactNumber");
                    }
                    //if(details.get("csPassword")!=null)csPassword=(String)details.get("csPassword");
                    //if(details.get("csConfPassword")!=null)csconpasswd=(String)details.get("csConfPassword");

                }
                if (mes.equals("F"))
                {
                    out.println("<center><table align=\"center\" width=\"60%\" style=\"bgcolor: white\" ><tr><td><font class=\"textb\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.</font>");
                    out.println("</td></tr><tr><td algin=\"center\" ><font face=\"arial\" class=\"textb\"  size=\"2\">");
                    errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                    out.println(errormsg);
                    out.println("</font>");
                    out.println("</td></tr></table></center>");

                }
                else if (mes.equals("X"))
                {
                    out.println("<center><table align=\"center\" width=\"60%\" style=\"bgcolor: white\" ><tr><td><font class=\"textb\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.<br>");
                    //out.println("<br><font face=\"arial\" color=\"red\" size=\"2\"> align=\"center\"");
                    errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                    out.println(errormsg);
                    out.println("</font>");
                    out.println("</td></tr></table></center>");
                }
                else if (mes.equals("username"))
                {
                    out.println("<center><table align=\"center\" width=\"60%\"style=\" bgcolor: white\" ><tr><td><font class=\"textb\" >You have <b>NOT FILLED</b> or <b>Duplicate</b> UserName <br>");
                    //out.println("<br><font face=\"arial\" color=\"red\" size=\"2\"> align=\"center\"");
                    username="";
                    errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                    out.println(errormsg);
                    out.println("</font>");
                    out.println("</td></tr></table></center>");
                }
                else if(mes.equals("I"))
                {
                    out.println("<center><table align=\"center\" width=\"60%\" style=\"bgcolor: white\" ><tr><td><font class=\"textb\" >Internal Failure</font>");
                }
            }

        }
    %>
                <table  align="center" width="70%" cellpadding="2" cellspacing="2">

                        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
                        <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
                        <tr>
                            <td>

                                <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center" >

                                    <tr><td colspan="4">&nbsp;</td></tr>
                                    <tr>
                                        <td width="2%" class="textb">&nbsp;</td>
                                        <td width="43%" class="textb"><span class="textb">Customer Support Executive Name*</span></td>
                                        <td width="5%" class="textb">:</td>
                                        <td width="50%">
                                            <input id="name" class="txtbox" type="Text" maxlength="100" value="<%=csename%>" name="name" size="35">
                                        </td>
                                    </tr>
                                    <tr><td colspan="4">&nbsp;</td></tr>
                                    <tr>
                                        <td class="textb">&nbsp;</td>
                                        <td class="textb"><span class="textb">Username*</span><br>
                                            (Username Should Not Contain Special Characters like !@#$%)</td>
                                        <td class="textb">:</td>
                                        <td><input id="username" class="txtbox" type="Text" maxlength="100"  maxlength = 100 value="<%=username%>" name="username" size="35"></td>

                                    </tr>
                                    <tr><td colspan="4">&nbsp;</td></tr>

                                    <tr>
                                        <td class="textb">&nbsp;</td>
                                        <td class="textb"><span class="textb">Password*</span><br>
                                            (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)</td>
                                        <td class="textb">:</td>
                                        <td>
                                            <input id="password" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(csPassword)%>" name="password" size="35">
                                        </td>
                                    </tr>
                                    <tr><td colspan="4">&nbsp;</td></tr>

                                    <tr>
                                        <td class="textb">&nbsp;</td>
                                        <td class="textb"><span>Confirm Password*</span><br>(Should be same as PASSWORD)</td>
                                        <td class="textb">:</td>
                                        <td><input id="confirmpassword" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(csconpasswd)%>" name="confirmpassword" size="35"></td>
                                    </tr>
                                    <tr><td colspan="4">&nbsp;</td></tr>
                                    <tr>
                                        <td class="textb">&nbsp;</td>
                                        <td class="textb"><span class="textb">Email Id*</span></td>
                                        <td class="textb">:</td>
                                        <td><input id="emailid" class="txtbox" type="Text" maxlength="100"  value="<%=emails%>" name="emailid" size="35"></td>
                                    </tr>
                                    <tr><td colspan="4">&nbsp;</td></tr>
                                    <tr>
                                        <td class="textb">&nbsp;</td>
                                        <td class="textb">Phone no*</td>
                                        <td class="textb">:</td>
                                        <td><input id="phoneno" class="txtbox" type="Text" maxlength="100" value="<%=csContactNumber%>" name="phoneno" size="35"></td>
                                    </tr>
                                    <tr><td colspan="4">&nbsp;</td></tr>
                                    <tr>
                                        <td class="textb">&nbsp;</td>
                                        <td class="textb"></td>
                                        <td class="textb"></td>
                                        <td>
                                            <input type="hidden" value="-" name="bussinessdevexecutive"><input type="hidden" value="1" name="step">
                                            <button id="submit" type="Submit" value="submit" name="submit" class="buttonform">
                                                submit
                                            </button>
                                        </td>
                                    </tr>

                                    <tr><td colspan="4">&nbsp;</td></tr>

                                </table>

                            </td>
                        </tr>
                </table>
              </div>
        </div>
    </div>




</form>
</body>
</html>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>