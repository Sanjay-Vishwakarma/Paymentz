<%@ page import="com.directi.pg.Database,com.directi.pg.Functions"%>
<%@ include file="functions.jsp"%>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.FraudRuleManager" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudSystemAccountRuleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 14/7/15
  Time: 3:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Logger logger = new Logger("actionFraudSystemAccountRule.jsp");
  String accountname = Functions.checkStringNull(request.getParameter("accountname"));
  String status = Functions.checkStringNull(request.getParameter("status"));
  String str ="";

  str="ctoken="+ctoken;
  if(accountname == null) { accountname = ""; }
  if(status == null) { status = ""; }

  if(accountname != null) { str = str + "&accountname" +accountname; }
  if(status != null)      {    str = str + "&status" +status; }

%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Account Rule List</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Fraud Account Rule Configuration
        <div style="float: right;">
          <form action="/icici/manageFraudSystemAccountRule.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add Fraud Account Rule Mapping" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add Fraud Account Rule Mapping
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemAccountRuleList?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="15%" class="textb" >Fraud System Account</td>
                  <td width="12%" class="textb">
                    <select name="fsaccountid" class="txtbox" style="width: 200px;">
                      <option value="" selected>ALL</option>
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
                          logger.error("Exception:::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb" align="center">Status</td>
                  <td width="12%" class="textb">
                    <select name="status" size="1" class="txtbox" style="width: 130px;">
                      <option value="">ALL</option>
                      <option value="Enable">Enable</option>
                      <option value="Disable">Disable</option>
                    </select>
                  </td>
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
<div class="row" style="margin-top: 0px">
  <div class="col-lg-12">
    <div class="panel panel-default" style="margin-top: 0px">
      <div class="panel-heading" >
        Update Fraud Account rule Mapping
      </div>
      <%
        FraudSystemAccountRuleVO  fraudSystemAccountRuleVO=(FraudSystemAccountRuleVO)request.getAttribute("fraudSystemAccountRuleVO");
        if(fraudSystemAccountRuleVO!=null)
        {
          String style="class=tr0";
      %>
      <form action="/icici/servlet/ActionFraudSystemAccountRule?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden"  name="action" value="update">
        <input type="hidden"  name="fsaccountid" value="<%=fraudSystemAccountRuleVO.getFsAccountId()%>">
        <input type="hidden"  name="ruleid" value="<%=fraudSystemAccountRuleVO.getRuleId()%>">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb"&nbsp;></td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud System Account*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb" size="30" class="txtbox">
                    <input type="text" name="accountName" value="<%=fraudSystemAccountRuleVO.getAccountName()%>" class="txtbox" disabled align="right">
                  </td>
                </tr>
                <%--</tr>--%>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud Rule*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" name="fraudrulename" value="<%=fraudSystemAccountRuleVO.getRuleName()%>>" class="txtbox"  align="right" disabled>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Score*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <input class="txtbox" maxlength="2" type="text" name="score" align="right" value="<%=fraudSystemAccountRuleVO.getScore()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Status*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="status" size="1" class="txtbox" style="width: 100px;">
                      <%
                        String enable="";
                        String disable="";
                        if("Enable".equals(fraudSystemAccountRuleVO.getStatus())){enable="selected";}
                        else {disable="selected";}
                      %>
                      <option value="Enable" <%=enable%>>Enable</option>
                      <option value="Disable" <%=disable%>>Disable</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Limit </td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <%
                      if(fraudSystemAccountRuleVO.getRuleName().equalsIgnoreCase("Block_Card_By_Type"))
                      {
                    %>
                    <select id="value1" name="value" size="1" class="txtbox" style="width: 150px;">
                      <%
                        FraudRuleManager fraudRuleManager = new FraudRuleManager();
                        String isSelected = "";

                        for(Object cardType : fraudRuleManager.getCardType())
                        {
                          if(cardType.toString().equalsIgnoreCase(fraudSystemAccountRuleVO.getValue()))
                            isSelected = "selected";
                          else
                            isSelected = "";
                          String card=cardType.toString().replace(" ","");
                      %>
                      <option value=<%=card%> <%=isSelected%>><%=card%></option>
                      <%
                        }
                      %>
                    </select>
                    <%
                    }
                    else if(fraudSystemAccountRuleVO.getRuleName().equalsIgnoreCase("Block_Card_By_Usage"))
                    {
                    %>
                    <select id="value1" name="value" size="1" class="txtbox" style="width: 150px;">
                      <%
                        if("Personal".equals(fraudSystemAccountRuleVO.getValue()))
                        {
                      %>
                      <option value="Personal" selected>Personal</option>
                      <option value="Commercial">Commercial</option>
                      <%
                      }
                      else if("Commercial".equals(fraudSystemAccountRuleVO.getValue()))
                      {
                      %>
                      <option value="Personal">Personal</option>
                      <option value="Commercial" selected>Commercial</option>
                      <%
                        }
                        else
                        {
                      %>
                      <option value="Personal">Personal</option>
                      <option value="Commercial">Commercial</option>
                      <%
                        }
                      %>
                      %>
                    </select>
                    <%
                    }
                    else
                    {
                    %>
                    <input class="txtbox" maxlength="2" type="text" id="value" name="value"  value="<%=fraudSystemAccountRuleVO.getValue()==null?"":fraudSystemAccountRuleVO.getValue()%>">
                    <%
                      }
                    %>
                    </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr><td>&nbsp;</td></tr>
                <tr><td>&nbsp;</td></tr>
                <tr><td>&nbsp;</td></tr>
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
      out.println("</div>");
    }
    else
    {
      out.println("<div class=\"reportable\">");
      out.println(Functions.NewShowConfirmation("Result", "No Records Found"));
      out.println("</div>");
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

