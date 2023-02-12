<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountVO" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 21/7/15
  Time: 6:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Sub Account</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Logger logger = new Logger("actionFraudSystemSubAccount.jsp");
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
  if(request.getAttribute("message")!=null)
  {
    out.println(request.getAttribute("message"));
  }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Fraud System Sub Accounts
        <div style="float: right;">
          <form action="/icici/manageFraudSystemAccountSubAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Fraud Sub Account" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Fraud Sub Account
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemSubAccountList?ctoken=<%=ctoken%>" method="post" name="forms" >
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
                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="15%" class="textb" >Fraud System Account</td>
                  <td width="2%" class="textb"></td>
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
                          logger.error("Exception"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                  <td width="5%" class="textb">&nbsp;</td>
                  <td width="13%" class="textb" >Fraud Sub Account Id</td>
                  <td width="2%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="50" type="text" name="fssubaccountid"  value="" class="txtbox" style="width: 170px;">
                  </td>
                  <td width="7%" class="textb"></td>
                  <td width="11%" class="textb" align="center">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
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
        Update Fraud System Sub Account Configuration
      </div>
      <%
        FraudSystemSubAccountVO subAccountVO = (FraudSystemSubAccountVO) request.getAttribute("subaccountDetails");
        if(subAccountVO != null)
        {
      %>
      <form action="/icici/servlet/ActionFraudSystemSubAccount?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="mappingid" value="<%=subAccountVO.getFraudSystemSubAccountId() %>">
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
                  <td style="padding: 3px" width="43%" class="textb">Fraud Sub Account Id</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="fssubaccountid1" class="txtbox" value="<%=subAccountVO.getFraudSystemSubAccountId()%>" disabled>
                    <input type="hidden" name="fssubaccountid" value="<%=subAccountVO.getFraudSystemSubAccountId()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud System Account Id</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="fsaccountid1" class="txtbox" value="<%=subAccountVO.getFraudSystemAccountId()%>" disabled>
                    <input type="hidden" name="fsaccountid" value="<%=subAccountVO.getFraudSystemAccountId()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Fraud Account/Website</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="subaccountname1" class="txtbox" value="<%=subAccountVO.getSubAccountName()%>" disabled>
                    <input type="hidden" name="subaccountname" value="<%=subAccountVO.getSubAccountName()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">User Name</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="subusername" class="txtbox" maxlength="25" value="<%=subAccountVO.getUserName()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>

                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">User Number</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="subpwd" maxlength="25" class="txtbox" value="<%=subAccountVO.getPassword()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform" name="action" value="update">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Update
                    </button>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
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
      String msg = (String)request.getAttribute("msg");
      Functions functions=new Functions();
      if(functions.isValueNull(msg)){
        out.println("<div class=\"reporttable\">");
        out.println(Functions.NewShowConfirmation("Result",msg));
        out.println("</div>");
      }
    }
    else if (request.getAttribute("updateMsg") != null)
    {
      out.println(Functions.NewShowConfirmation("Result", (String)request.getAttribute("updateMsg")));
      out.println("</div>");
    }
    else
    {
      out.println("<div class=\"reportable\">");
      out.println(Functions.NewShowConfirmation("Result", "No records found."));
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
