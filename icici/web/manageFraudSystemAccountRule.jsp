<%@ page import="com.directi.pg.Database"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.FraudRuleManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: kiran
  Date: 14/7/15
  Time: 4:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Fraud Account Rule Mapping</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Logger logger = new Logger("manageFraudSystemAccountRule.jsp");
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Add Fraud Account Rule Mapping
        <div style="float: right;">
          <form action="/icici/fraudSystemAccountRuleList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Fraud Sub-Account List" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Fraud Account Rule List
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/ManageFraudSystemAccountRule?ctoken=<%=ctoken%>" method="post" name="forms">
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
                  <td style="padding: 3px" width="43%" class="textb">Fraud System Account*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="fsaccountid" class="txtbox" style="width: 200px;"><option value="" selected>Select Fraud System Account</option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn= Database.getConnection();
                          ResultSet rs = Database.executeQuery("select fsaccountid,accountname from fraudsystem_account_mapping",conn);
                          while (rs.next())
                          {
                            out.println("<option value="+rs.getString("fsaccountid")+">"+rs.getString("fsaccountid")+" - "+rs.getString("accountname")+"</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud Rule*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select id="ruleid" name="ruleid" class="txtbox" style="width: 200px;">
                      <option value="" selected>Select Fraud Rule</option>
                      <%
                        try
                        {
                          conn = Database.getConnection();
                          StringBuffer qry = new StringBuffer("select rulegroup,ruleid,rulename from rule_master where rulegroup in('HardCoded','Other','Internal')");
                          PreparedStatement ps = conn.prepareStatement(qry.toString());
                          ResultSet rs = ps.executeQuery();
                          while(rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("ruleid")+"\">"+rs.getString("rulegroup")+"-" + rs.getInt("ruleid") + "-" + rs.getString("rulename") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                    <script>
                      $( "select" ) .change(function () {
                        var e = document.getElementById("ruleid");
                        var ruleName = e.options[e.selectedIndex].text.split("-");
                        if(ruleName[2]=="Block_Card_By_Type")
                        {
                          var value1= document.getElementById("value1");
                          var value= document.getElementById("value");
                          var value2= document.getElementById("value2");
                          value1.style.display="block";
                          value.style.display="none";
                          value2.style.display="none";
                        }
                        else if(ruleName[2]=="Block_Card_By_Usage")
                        {
                          var value1= document.getElementById("value1");
                          var value= document.getElementById("value");
                          var value2= document.getElementById("value2");
                          value2.style.display="block";
                          value1.style.display="none";
                          value.style.display="none";
                        }
                        else
                        {
                          var value= document.getElementById("value");
                          var value1= document.getElementById("value1");
                          var value2= document.getElementById("value2");
                          value.style.display="block";
                          value1.style.display="none";
                          value2.style.display="none";
                        }
                      });
                    </script>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Score*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input class="txtbox" maxlength="2" type="text" name="score" align="right"></td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Status*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="status" size="1" class="txtbox" style="width: 100px;">
                      <option value="" selected="">Select Status</option>
                      <option value="Enable">Enable</option>
                      <option value="Disable">Disable</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Limit </td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input class="txtbox" maxlength="2" type="text" id="value" name="value">
                    <select id="value1" name="value1" size="1" class="txtbox" style="width: 150px; display: none">
                      <option value="" selected=""></option>
                      <%
                        FraudRuleManager fraudRuleManager = new FraudRuleManager();
                        for(Object cardType : fraudRuleManager.getCardType())
                        {
                          String card=cardType.toString().replace(" ","");
                      %>
                      <option value=<%=card%>><%=card%></option>
                      <%
                        }
                      %>
                    </select>
                    <select id="value2" name="value2" size="1" class="txtbox" style="width: 150px; display: none">
                      <option value="" selected=""></option>
                      <option value="Personal">Personal</option>
                      <option value="Commercial">Commercial</option>
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
      out.println("<div class=\"reporttable\">");
      out.println(Functions.NewShowConfirmation("Result",message));
      out.println("<div>");
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
