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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Member Child Signup</title>
</head>
<body>
<%
  MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant's Merchant Master
        <div style="float: right;">
          <form action="/icici/memberChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" class="addnewmember" value="Add New Child Merchant" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New User
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/MemberUserList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <br>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" >Member Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">

                    <select size="1" name="merchantid" class="txtboxsignup" id="dropdown1">

                      <option value="">--Select MerchantId--</option>
                      <%
                        String mId = "";
                        Hashtable merchantHash = multipleMemberUtill.selectMemberIdForDropDown();
                        Enumeration merEnum = merchantHash.keys();
                        String login = "";
                        String selected3 = "";
                        while (merEnum.hasMoreElements())
                        {
                          mId = (String) merEnum.nextElement();
                          login = (String) merchantHash.get(mId);

                          if(mId.equals(request.getAttribute("memberid")))
                            selected3 = "selected";
                          else
                            selected3 = "";

                      %>
                      <option value="<%=mId%>"<%=selected3%>><%=mId%>--<%=login%></option>

                      <%
                        }
                      %>

                    </select>

                  </td>

                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" ></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
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

<div class="reporttable">
  <br>
  <table  align="center" width="50%" cellpadding="2" cellspacing="2">

    <form action="/icici/servlet/NewChildMemberSignUp?ctoken=<%=ctoken%>" method="post" name="form1">

      <tr>
        <td>
          <%
            String errormsg=(String)request.getAttribute("error");
            if(errormsg!=null)
            {
              out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");
            }
          %>
          <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">

            <tr>
              <td width="2%" class="textb">&nbsp;</td>
              <td colspan="3" class="label">
                Merchant Information (New Login)
              </td>

            </tr>
            <tr><td colspan="4">&nbsp;</td></tr>
            <tr>
              <td width="2%" class="textb">&nbsp;</td>
              <td width="43%" class="textb" valign="top">New Login *</td>
              <td width="5%" class="textb" valign="top">:</td>
              <td width="50%" valign="top">
                <input type="text" maxlength="100" class="txtbox" value="" name="username">
              </td>
            </tr>
            <input type="hidden" value="3" name="step">
            <br>
            <tr>

              <td width="2%" class="textb">&nbsp;</td>
              <td width="43%" class="textb" valign="top"></td>
              <td width="5%" class="textb" valign="top"></td>
              <td width="50%" valign="top">
                <input type="Submit" value="Submit" name="submit" class="buttonform">
              </td>

            </tr>
            <tr><td colspan="4">&nbsp;</td></tr>
          </table>
        </td>
      </tr>
    </form>
  </table>
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
