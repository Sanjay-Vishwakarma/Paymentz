<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 18/7/15
  Time: 6:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Rule Setting</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Fraud Rule Master
        <div style="float: right;">
          <form action="/icici/addNewFraudRule.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Fraud Rule" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Fraud Rule
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudRuleList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="15" type="text" name="ruleid"  value=""  class="txtbox">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule Name</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="50" type="text" name="rulename"  value="" class="txtbox">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule Group *</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="rulegroup" size="1" class="txtbox">
                      <option value="" selected="">ALL</option>
                      <option value="HardCoded">HardCoded</option>
                      <option value="Dynamic">Dynamic</option>
                      <option value="Internal">Internal</option>
                      <option value="Other">Other</option>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Score *</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input class="txtbox" maxlength="2" type="text" name="score">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <td width="4%" class="textb">&nbsp;</td>
                <td width="8%" class="textb" ></td>
                <td width="3%" class="textb"></td>
                <td width="10%" class="textb">
                <td width="4%" class="textb">&nbsp;</td>
                <td width="8%" class="textb" ></td>
                <td width="3%" class="textb"></td>
                <td width="10%" class="textb">
                  <button type="submit" class="buttonform">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button>
                </td>
                </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="row" style="margin-top:0px">
  <div class="col-lg-12">
    <div class="panel panel-default" style="margin-top:0px">
      <div class="panel-heading" >
        Update Fraud Rule Configuration
      </div>
      <%
        HashMap hash = (HashMap)request.getAttribute("chargedetails");
        if(hash!=null)
        {
          String style="class=tr0";
      %>
      <form action="/icici/servlet/ActionFraudRuleList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" name="action" value="update">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <%
                if(request.getAttribute("errormsg")!=null)
                {
                  out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("errormsg")));
                }
              %>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud Rule Id</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="ruleid1" class="txtbox" disabled="true" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("ruleid"))%>" >
                    <input type="hidden" name="ruleid" value="<%=hash.get("ruleid")%>">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Fraud Rule Name*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input type="text" class="txtbox" size="30" class="txtbox" name="rulename" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("rulename"))%>" ></td>
                </tr>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Rule Description</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input type="text" maxlength="255" size="30" class="txtbox" name="ruledescription" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("ruledescription"))%>"></td>
                </tr>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Rule Group*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <input type="hidden" maxlength="255" size="30" class="txtbox" name="rulegroup" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("rulegroup"))%>">
                    <select name="rulegroup1" class="txtbox" disabled="true">
                      <%if("HardCoded".equals(ESAPI.encoder().encodeForHTML((String)hash.get("rulegroup"))))
                      {
                      %>
                      <option value="hardCoded" selected>Hard Coded</option>
                      <option value="dynamic">Dynamic</option>
                      <option value="internal">Internal</option>
                      <option value="other">Other</option>
                      <%
                      }
                      else if("Dynamic".equals(ESAPI.encoder().encodeForHTML((String)hash.get("rulegroup"))))
                      {
                      %>
                      <option value="hardCoded">Hard Coded</option>
                      <option value="dynamic" selected>Dynamic</option>
                      <option value="internal">Internal</option>
                      <option value="other">Other</option>
                      <%
                      }
                      else if("Internal".equals(ESAPI.encoder().encodeForHTML((String)hash.get("rulegroup"))))
                      {
                      %>
                      <option value="hardCoded">Hard Coded</option>
                      <option value="dynamic">Dynamic</option>
                      <option value="internal" selected>Internal</option>
                      <option value="other">Other</option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="hardCoded">Hard Coded</option>
                      <option value="dynamic">Dynamic</option>
                      <option value="internal">Internal</option>
                      <option value="other" selected>Other</option>
                      <%}%>
                    </select>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Score*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <input type="text" size="30" maxlength="2" name="score" class="txtbox" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("score"))%>">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Status*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="status" class="txtbox">
                      <%if("Enable".equals(ESAPI.encoder().encodeForHTML((String)hash.get("status"))))
                      {
                      %>
                      <option value="Enable" selected>Enable</option>
                      <option value="Disable">Disable</option>
                      <%
                      }
                      else if("Disable".equals(ESAPI.encoder().encodeForHTML((String)hash.get("status"))))
                      {
                      %>
                      <option value="Enable">Enable</option>
                      <option value="Disable" selected >Disable</option>
                      <% } %>
                    </select>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Update
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
              <br>
              <br>
              <br>
            </td>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
<%
    }
    else if(request.getAttribute("statusMsg")!=null)
    {
      out.println("<div class=\"reportable\">");
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")) );
      out.println("<br><br></div>");

    }else
    {
      out.println("<div class=\"reportable\">");
      out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
      out.println("<br><br>" +
              "</div>");
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

