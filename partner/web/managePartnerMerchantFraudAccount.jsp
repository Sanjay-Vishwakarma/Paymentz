<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 9/9/15
  Time: 6:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Logger logger = new Logger("managePartnerMerchantFraudAccount.jsp");
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","managePartnerMerchantFraudAccount");
%>
<html>
<head>
  <title> Add Merchant FraudAccount</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String memberid = (String) session.getAttribute("merchantid");
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Fraud Account Interface
        <div style="float: right;">
          <form action="/partner/partnerMerchantFraudAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Merchant Fraud Sub-Account List" name="submit" class="button" style="width:190px; font-size:13px">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Merchant Fraud Accounts
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/partner/addNewPartnerMerchantFraudAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Fraud Account" name="submit" class="button" style="width:190px; font-size:13px">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Fraud Account
            </button>
          </form>
        </div>
      </div>

      <form action="/partner/net/ManagePartnerMerchantFraudAccount?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">MemberId*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="mid" class="txtbox" style="width: 200px;"><option value="" selected></option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn= Database.getConnection();
                          String query = "select memberid, company_name from members where activation='Y' and partnerId =? ORDER BY memberid ASC";
                          PreparedStatement pstmt = conn.prepareStatement( query );
                          pstmt.setString(1,memberid);
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
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
                  <td style="padding: 3px" width="43%" class="textb">FraudAccount / website*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="fsaccountid" class="txtbox" style="width: 450px;"><option value="" selected></option>
                      <%
                        try
                        {
                          conn= Database.getConnection();
                          StringBuffer qry = new StringBuffer("select subacc.fssubaccountid,acc.accountname,subaccountname from fsaccount_subaccount_mapping as subacc,fraudsystem_account_mapping as acc,fraudsystem_master as fs where subacc.fsaccountid=acc.fsaccountid and acc.fsid=fs.fsid");
                          PreparedStatement ps = conn.prepareStatement(qry.toString());
                          ResultSet rs1 = ps.executeQuery();
                          while(rs1.next())
                          {
                            out.println("<option value=\""+rs1.getInt("subacc.fssubaccountid")+"\">"+rs1.getInt("subacc.fssubaccountid")+" : "+rs1.getString("subaccountname")+" ( "+rs1.getString("acc.accountname")+ " ) "+"</option>");
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
                  <td style="padding: 3px" width="43%" class="textb">isActive*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="isActive" size="1" class="txtbox" style="width: 100px;">
                      <option value="" selected=""></option>
                      <option value="Y">Y</option>
                      <option value="N">N</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">isVisible*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="isVisible" size="1" class="txtbox" style="width: 100px;">
                      <option value="" selected=""></option>
                      <option value="Y">Y</option>
                      <option value="N">N</option>
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
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
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
    String statusMsg = (String) request.getAttribute("statusMsg");
    Functions functions = new Functions();
    if(statusMsg != null)
    {
      out.println("<div class=\"reporttable\">");
      out.println(functions.NewShowConfirmation1("Result",statusMsg));
      out.println("</div>");
    }
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
  }
%>
</body>
</html>
