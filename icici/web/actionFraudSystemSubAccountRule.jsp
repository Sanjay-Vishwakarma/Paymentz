<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.Connection" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountRuleVO" %>
<%@ page import="com.manager.FraudRuleManager" %>

<%--
  Created by IntelliJ IDEA.
  User: kiran
  Date: 14/7/15
  Time: 5:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private static Logger logger=new Logger("actionFraudSystemSubAccountRule.jsp");

%>
<%
  String fssubaccountid =Functions.checkStringNull(request.getParameter("fssubaccountid"));
  if (fssubaccountid == null){fssubaccountid = "";}

  String status = Functions.checkStringNull(request.getParameter("status"));
  if (status == null){status = "";}
  String str1= "";
  str1="ctoken="+ctoken;
  if (fssubaccountid != null){str1 = str1 + "&fssubaccountid=" + fssubaccountid;}
  if (status != null){str1 = str1 + "&status=" + status;}

%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud SubAccount Rule List</title>

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
        Fraud Sub Account Rule
        <div style="float: right;">
          <form action="/icici/manageFraudSystemSubAccountRule.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add Fraud Account Rule Mapping" name="submit" class="addnewmember" style="width: 270px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;New Sub Account Rule Mapping
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemSubAccountRuleList?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="ctoken=" + ctoken;
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
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="15%" class="textb" >Fraud Sub Account</td>
                  <td width="12%" class="textb">
                    <select name="fssubaccountid" class="txtbox" style="width: 200px;">
                      <option value="" selected>ALL</option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn = Database.getConnection();
                          StringBuffer qry = new StringBuffer("select subacc.fssubaccountid,fs.fsname,acc.accountname,subaccountname from fsaccount_subaccount_mapping as subacc,fraudsystem_account_mapping as acc,fraudsystem_master as fs where subacc.fsaccountid=acc.fsaccountid and acc.fsid=fs.fsid");
                          PreparedStatement ps = conn.prepareStatement(qry.toString());
                          ResultSet rs = ps.executeQuery();
                          while(rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("subacc.fssubaccountid")+"\">"+rs.getInt("subacc.fssubaccountid")+" : "+rs.getString("subaccountname")+" ( "+rs.getString("acc.accountname")+" : "+rs.getString("fs.fsname")+" ) "+"</option>");
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
                  <td width="3%" class="textb" >&nbsp;</td>
                  <td width="5%" class="textb" >Status</td>
                  <td width="12%" class="textb">
                    <select name="status" size="1" class="txtbox" style="width: 130px;">
                      <option value="" selected="">ALL</option>
                      <option value="Enable">Enable</option>
                      <option value="Disable">Disable</option>
                    </select>
                  </td>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="10%" class="textb" align="center">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
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
        Update Fraud Sub Account Rule Configuration
      </div>
      <%
        FraudSystemSubAccountRuleVO  fraudSystemSubAccountRuleVO=(FraudSystemSubAccountRuleVO)request.getAttribute("fraudSystemSubAccountRuleVO");
        if(fraudSystemSubAccountRuleVO!=null)
        {

          String style="class=tr0";
      %>
      <form action="/icici/servlet/ActionFraudSystemSubAccountRule?ctoken=<%=ctoken%>" method="post" name="actionfrm">
        <input type="hidden"  name="action" value="update">
        <input type="hidden"  name="fssubaccountid" value="<%=fraudSystemSubAccountRuleVO.getFsSubAccountId()%>">
        <input type="hidden" id="ruleid"  name="ruleid" value="<%=fraudSystemSubAccountRuleVO.getRuleId()%>">
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
                  <td style="padding: 3px" width="43%" class="textb">Fraud Sub Account*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb" size="70" class="txtbox">
                    <input type="text" name="accountName" value="<%=fraudSystemSubAccountRuleVO.getSubAccountName()%>" class="txtbox" disabled align="right">
                  </td>
                </tr>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud Rule*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input id="fraudrulename" type="text" name="fraudrulename" value="<%=fraudSystemSubAccountRuleVO.getRuleName()%>" class="txtbox"  align="right" disabled>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Score*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <input class="txtbox" maxlength="2" type="text" name="score" align="right" value="<%=fraudSystemSubAccountRuleVO.getScore()%>">
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
                        if("Enable".equals(fraudSystemSubAccountRuleVO.getStatus())){enable="selected";}
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
                      if(fraudSystemSubAccountRuleVO.getRuleName().equalsIgnoreCase("Block_Card_By_Type"))
                      {
                    %>
                    <select id="value1" name="value" size="1" class="txtbox" style="width: 150px;">
                      <%
                        FraudRuleManager fraudRuleManager = new FraudRuleManager();
                        String isSelected = "";

                        for(Object cardType : fraudRuleManager.getCardType())
                        {
                          if(cardType.toString().equalsIgnoreCase(fraudSystemSubAccountRuleVO.getValue()))
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
                    else if(fraudSystemSubAccountRuleVO.getRuleName().equalsIgnoreCase("Block_Card_By_Usage"))
                    {
                    %>
                    <select id="value2" name="value" size="1" class="txtbox" style="width: 150px;">
                      <%
                        if("Personal".equals(fraudSystemSubAccountRuleVO.getValue()))
                        {
                      %>
                      <option value="Personal" selected>Personal</option>
                      <option value="Commercial">Commercial</option>
                      <%
                      }
                      else if("Commercial".equals(fraudSystemSubAccountRuleVO.getValue()))
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
                    </select>
                    <%
                    }
                    else
                    {
                    %>
                    <input class="txtbox" maxlength="2" type="text" id="value" name="value"  value="<%=fraudSystemSubAccountRuleVO.getValue()==null?"":fraudSystemSubAccountRuleVO.getValue()%>">
                    <%
                      }
                    %>
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
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>

