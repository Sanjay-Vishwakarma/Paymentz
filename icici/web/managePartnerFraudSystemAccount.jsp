<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page import="static com.directi.pg.Database.closeConnection" %>
<%!
  Logger logger=new Logger("addBankPartnerMapping.jsp");
  Functions functions= new Functions();
%>
<%--
  Created by IntelliJ IDEA.
 User: Supriya
 Date: 10/2/16
 Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Partner FraudSystem Account Mapping</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
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
        FraudSystem Accounts
        <div style="float: right;">
          <form action="/icici/manageFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="New FraudSystem Account" name="submit" class="addnewmember" style="width: 200px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Account
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="FraudSystem Account List" name="submit" class="addnewmember" style="width: 200px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Account List
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/ManagePartnerFraudSystemAccount?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Partner*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px" class="textb">
                    <input name="partnerid" class="txtbox" id="allpid">
                    <%--<select name="partnerid" class="txtbox">
                      <option value=""></option>
                      <%
                        Connection conn=null;
                        try
                        {
                          conn= Database.getConnection();
                          String  query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                          PreparedStatement pstmt = conn.prepareStatement( query );
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("partnerId")+"\" >"+rs.getInt("partnerId")+" - "+rs.getString("partnerName")+"</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception::::::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">FraudSystem Account*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="fsAccount" class="txtbox" style="width: 200px;"><option value="" selected>All</option>
                      <%
                        Connection conn=null;
                        try
                        {
                          conn= Database.getConnection();
                          ResultSet rs = Database.executeQuery("select fsaccountid,accountname,fsid from fraudsystem_account_mapping ",conn);
                          while (rs.next())
                          {
                            out.println("<option value="+rs.getString("fsaccountid")+">"+rs.getString("fsaccountid")+" - "+rs.getString("accountname")+"("+FraudSystemService.getFSGateway(rs.getString("fsid"))+")"+"</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception"+e);
                        }
                        finally
                        {
                          closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">isActive*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="isActive" size="1" class="txtbox" style="width: 70px;">
                      <option value="N">N</option>
                      <option value="Y">Y</option>
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
                      &nbsp;&nbsp;Add
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
    String message = (String)request.getAttribute("statusMsg");
    Functions functions=new Functions();
    if(functions.isValueNull(message))
    {
      out.println("<div class=\"reporttable\">");
      out.println(Functions.NewShowConfirmation("Result",message));
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