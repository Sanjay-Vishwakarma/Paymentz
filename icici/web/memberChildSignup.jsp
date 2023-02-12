<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page
        import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,java.util.Calendar,java.util.Enumeration,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.payment.MultipleMemberUtill" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
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
  <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js?ver=1"></script>
  <script language="javascript">

    $(document).ready(function() {

      document.getElementById('submit').disabled =  false;
      $("#submit").click(function() {
        var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
        document.getElementById('passwd').value =  encryptedString1;

        var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
        document.getElementById('conpasswd').value =  encryptedString2;

        document.getElementById('isEncrypted').value =  true;
      });

    });
  </script>
  <title>Member Child Signup</title>
</head>
<body>
<%
  MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String merchantid = nullToStr(request.getParameter("merchantid"));
%>
<div class="row">
  <div class="col-lg-12">

      </div>
      <form action="/icici/servlet/MemberUserList?ctoken=<%=ctoken%>" method="post" name="forms">
        <br>
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                    <input name="merchantid" id="mid" value="<%=merchantid%>" class="form-control" autocomplete="on">


                  </td>



                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<br><br><br><br><br><br>
<div class="reporttable">

  <div align="center" class="textb"><h5><b><u>Merchant's User Signup</u></b></h5></div>

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

    //Hashtable details = (Hashtable) request.getAttribute("details");
    if ((String) request.getAttribute("username") != null) userName = (String) request.getAttribute("username");
  }

%>

  <form action="/icici/servlet/NewChildMemberSignUp?ctoken=<%=ctoken%>" method="post" name="form1">

    <input id="ctoken1" name="ctoken" type="hidden" value="<%=ctoken%>">
    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false">

    <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor=white align="center">
      <tr>
        <td>

          <table border="0" cellpadding="5" cellspacing="0" width="90%"  align="center" style="margin-left:100px">
            <tr><td colspan="4">&nbsp;</td></tr>
            <tr>
              <td width="2%" class="textb">&nbsp;</td>
              <td width="43%" class="textb"><span class="textb">Member Id*</span></td>
              <td width="20%" class="textb">:</td>
              <td width="20%"><input name="merchantid" id="mid1" value="<%=merchantid%>" class="txtbox" autocomplete="on">

                <%--<select size="1" name="merchantid" class="txtboxsignup" id="dropdown2">

                  <option value="">--Select MerchantId--</option>
                  <%
                    String mId1 = "";
                    Hashtable merchantHash1 = multipleMemberUtill.selectMemberIdForDropDown();
                    Enumeration merEnum1 = merchantHash1.keys();
                    String login1 = "";
                    String selected31 = "";
                    while (merEnum1.hasMoreElements())
                    {
                      mId1 = (String) merEnum1.nextElement();
                      login1 = (String) merchantHash1.get(mId1);

                      if(mId1.equals(request.getAttribute("memberid")))
                        selected31 = "selected";
                      else
                        selected31 = "";

                  %>
                  <option value="<%=mId1%>"<%=selected31%>><%=mId1%>--<%=login1%></option>

                  <%
                    }
                  %>

                </select>--%>
              </td>

              <td width="10%" class="textb">&nbsp;</td>
              <td width="40%" class="textb" ></td>
              <td width="5%" class="textb"></td>

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
              <td><input id="passwd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');" value="" name="passwd" size="35"></td>

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
              <td><input id="conpasswd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');" value="" name="conpasswd" size="35"></td>
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
              <td class="textb"><span class="textb">Phone CC*</span><br>
              </td>
              <td class="textb">:</td>
              <td><input id="telnocc" class="txtbox" type="text" maxlength="125" value="" name="telnocc" size="35"></td>
            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"></td>
              <td class="textb"></td>
              <td></td>
            </tr>
            <tr>
              <td class="textb">&nbsp;</td>
              <td class="textb"><span class="textb">Phone Number*</span><br>
              </td>
              <td class="textb">:</td>
              <td><input id="telno" class="txtbox" type="text" maxlength="125" value="" name="telno" size="35"></td>
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
                <%--
                            <input id="submit" type="Submit" value="submit" name="submit" class="submit" ></td>
                --%>

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