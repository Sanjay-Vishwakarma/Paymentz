<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.payment.MultiplePartnerUtill" %>
<%--
  Created by IntelliJ IDEA.
  User: Sanjeet
  Date: 15/4/2019
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
  <script src="/icici/javascript/autocomplete1.js"></script>


  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>

  <script language="javascript">

    $(document).ready(function() {

      document.getElementById('submit').disabled =  false;
      $("#submit").click(function() {
        console.log($("#ctoken").val());
        var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
        console.log(encryptedString1)
        document.getElementById('passwd').value =  encryptedString1;

        var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
        document.getElementById('conpasswd').value =  encryptedString2;

        document.getElementById('isEncrypted').value =  true;
      });

    });
  </script>

  <title>Partner Child Signup</title>
</head>
<body>
<%
  MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String partnerid = nullToStr(request.getParameter("partnerid"));
%>
<br><br><br><br><br><br>

<div class="reporttable">

  <div align="center" class="textb"><h5><b><u>Partner's User Signup</u></b></h5></div>


  <%
    String errormsg=(String)request.getAttribute("error");
    String userName = "";
    if(errormsg!=null)
    {
      out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");
    }
    if (request.getParameter("error") != null)
    {
      String mes = (String) request.getParameter("MES");

      if ((String) request.getAttribute("username") != null) userName = (String) request.getAttribute("username");
    }

  %>

  <%--<%
    String success=(String)request.getAttribute("success");
    String reg = "";
    if(success!=null)
    {
      out.println("<center><font class=\"text\" face=\"arial\">"+success+"</font></center>");
    }
    if (request.getParameter("error") != null)
    {
      String mes = (String) request.getParameter("MES");

      if ((String) request.getAttribute("reg") != null) reg = (String) request.getAttribute("reg");
    }

  %>--%>

  <form action="/icici/servlet/NewChildPartnerSignUp?ctoken=<%=ctoken%>" method="post" name="form1">
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false">

    <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor=white align="center">
      <tr>
        <td>

          <table border="0" cellpadding="5" cellspacing="0" width="90%"  align="center" style="margin-left:100px">
            <tr><td colspan="4">&nbsp;</td></tr>

            <tr>
              <td width="2%" class="textb">&nbsp;</td>
              <td width="43%" class="textb">Partner Id*</td>
              <td width="20%" class="textb"></td>
              <td width="20%"><input class="txtbox" name="partnerid" id="mid1" value="<%=partnerid%>"  autocomplete="on"></td>
              </td>

              <td class="textb">&nbsp;</td>
              <td class="textb" ></td>
              <td class="textb"></td>

            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>
            <tr>
              <td width="2%" class="textb">&nbsp;</td>
              <td width="43%" class="textb"><span class="textb">Username*</span><br>
                (Username Should Not Contain Special Characters like !@#$%)</td>
              <td width="20%" class="textb">:</td>
              <td width="20%"><input class="txtbox" type="Text" maxlength="100"  maxlength = 100 value="<%=ESAPI.encoder().encodeForHTMLAttribute(userName)%>" name="username" size="35"></td>
            </tr>

            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>

            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"><span class="textb">Password*</span><br>
                (Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)</td>
              <td class="textb">:</td>
              <td><input id="passwd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                         value="" name="passwd" size="35"></td>

            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>

            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"><span class="textb">Confirm Password*</span><br>
                (Should be same as PASSWORD)</td>
              <td class="textb">:</td>
              <td><input id="conpasswd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                         value="" name="conpasswd" size="35"></td>
            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"><span class="textb">Email Address*</span><br>
              </td>
              <td class="textb">:</td>
              <td><input id="email" class="txtbox" type="text" maxlength="125" value="" name="email" size="35"></td>
            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td>
                <input type="hidden" value="1" name="step">

                <button type="submit" name="submit" class="buttonform" id="submit" >
                  <i class="fa fa-save"></i>
                  Submit
                </button> </td>
            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>

            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </form>
</div>

<%
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>

<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>