<%@ page import="com.directi.pg.Functions"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: kiran
  Date: 13/7/15
  Time: 1:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Add New Fraud Rule</title>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Add New Fraud Rule
        <div style="float: right;">
          <form action="/icici/fraudRuleList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Fraud Rule List" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Fraud Rule List
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/AddNewFraudRule?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Rule Name*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input class="txtbox" maxlength="255" type="text" name="rulename">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Rule Description</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input class="txtbox" maxlength="255" type="text" name="ruledescription"></td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Rule Group *</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="rulegroup" size="1" class="txtbox">
                      <option value="" selected="">Select Rule Group</option>
                      <option value="hardCoded">Hard Coded</option>
                      <option value="dynamic">Dynamic</option>
                      <option value="internal">Internal</option>
                      <option value="other">Other</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Score *</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input class="txtbox" maxlength="2" type="text" name="score"></td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Status *</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="status" size="1" class="txtbox">
                      <option value="" selected="">Select Status</option>
                      <option value="Enable">Enable</option>
                      <option value="Disable">Disable</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Save
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
<%
  String message=(String)request.getAttribute("statusMsg");
  Functions functions=new Functions();
  if(functions.isValueNull(message))
  {
%>
<div class="reporttable">
  <% if(!(message.equals("Fraud Rule is Already Exist in the System")))
    out.println(Functions.NewShowConfirmation("Result",message));
      else
     out.println(Functions.NewShowConfirmation("Error",message));
  %>
</div>
<%
    }

  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>
