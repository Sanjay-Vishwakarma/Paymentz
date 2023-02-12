<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemAccountService" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 14/7/15
  Time: 12:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Merchant Fraud Account</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
  <script type="application/javascript">
    function selectMember(ctoken)
    {
      document.forms.action="/icici/manageMarchantFraudAccount.jsp?ctoken=<%=ctoken%>";
      document.forms.submit();
    }
  </script>
</head>
<body>
<%
  String memberid=nullToStr(request.getParameter("memberid"));
  Logger logger = new Logger("manageFraudSystemAccountSubAccount.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String partnerId = request.getParameter("partnerId");
    String memberId = request.getParameter("memberid");
    String fsAccount=request.getParameter("fsAccount");
    String submerchantUsername=request.getParameter("submerchantUsername");
    String submerchantPassword=request.getParameter("submerchantPassword");
    String subAccountName=request.getParameter("subaccountName");
    boolean isAPICallSupported=false;
    FraudSystemAccountVO fraudSystemAccountVO=null;
    Functions functions=new Functions();
    if(functions.isValueNull(fsAccount))
    {
      fraudSystemAccountVO=FraudSystemAccountService.getFraudSystemAccount(fsAccount);
      String fsId=fraudSystemAccountVO.getFraudSystemId();
      isAPICallSupported=FraudSystemService.isAPICallSupport(fsId);
    }
    if((subAccountName == null) && (submerchantUsername == null) && (submerchantPassword == null))
    {
      subAccountName="";
      submerchantUsername="";
      submerchantPassword="";
    }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Fraud Accounts
        <div style="float: right;">
          <form action="/icici/fraudSystemMerchantSubAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Merchant Fraud Account List" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Merchant Fraud Account Master
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/ManageMarchantFraudAccount?ctoken=<%=ctoken%>" method="Post" name="forms" id="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <input type="hidden" value="manageMerchantFraudAccount" name="requestedfilename">
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
                    <select name="fsAccount" class="txtbox" style="width: 200px; "onChange="selectMember('<%=ctoken%>')">
                      <option value="" selected>Select Fraud System Account</option>
                      <%
                        Connection conn = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          conn= Database.getConnection();
                          rs = Database.executeQuery("select fsaccountid,accountname,fsid from fraudsystem_account_mapping", conn);
                          while (rs.next())
                          {
                            String selected="";
                            if(rs.getString("fsaccountid").equals(fsAccount))
                            {
                              selected="selected";
                            }
                      %>
                      <option value="<%=rs.getString("fsaccountid")%>" <%=selected%>><%=rs.getString("fsaccountid")+"-"+rs.getString("fsid")+" - "+rs.getString("accountname")%></option>;
                      <%
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
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Fraud Account/Website*</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input class="txtbox" maxlength="25" type="text" name="subaccountName" value="<%= ESAPI.encoder().encodeForHTMLAttribute(subAccountName)%>"></td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Partner ID</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="partnerId" class="txtbox" id="allpid">
                    <%--<select name="partnerId" class="txtbox" style="width: 172px; "onChange="selectMember('<%=ctoken%>')">
                      <option value="" selected ></option>
                      <%
                        try
                        {
                          conn = Database.getConnection();
                          String query = "select partnerId, partnerName from partners where activation='T' ORDER BY partnerId ASC";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selection="";
                            if (rs.getString("partnerId").equals(partnerId))
                            {
                              selection="selected";
                            }
                      %>
                      <option value="<%=rs.getInt("partnerId")%>" <%=selection%>><%=rs.getInt("partnerId") + "-" + rs.getString("partnerName")%>
                      </option>
                      ;
                      <%
                          }
                        }
                        catch (SystemError se)
                        {
                          logger.error("Exception:::::"+se);
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
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Fraud System SubMerchant UserName</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input class="txtbox" maxlength="25" type="text" name="submerchantUsername" value="<%= ESAPI.encoder().encodeForHTMLAttribute(submerchantUsername)%>"></td>
                </tr>
                <tr><td>&nbsp;</td></tr>

                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Fraud System SubMerchant Password</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input class="txtbox" maxlength="25" type="text" name="submerchantPassword" value="<%= ESAPI.encoder().encodeForHTMLAttribute(submerchantPassword)%>"></td>
                </tr>
                <tr><td>&nbsp;</td></tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Merchant ID*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                    <%--<select name="memberid" id="memberid" class="txtbox" style="width: 172px;">
                      <option value="" selected></option>
                      <%
                        try
                        {
                          conn = Database.getConnection();
                          String query = "select memberid, company_name from members where partnerId='" + partnerId + "'";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selection = "";
                            if (rs.getString("memberid").equals(memberId))
                            {
                              selection = "selected";
                            }
                      %>
                      <option value=<%=rs.getInt("memberid")%> <%=selection%>><%=rs.getInt("memberid")+" - "+rs.getString("company_name")%></option>;
                      <%
                          }
                        }
                        catch (SystemError se)
                        {
                          logger.error("Exception:::::" + se);
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
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">isActive</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="isActive" size="1" class="txtbox" style="width: 70px;">
                      <option value="Y">Y</option>
                      <option value="N">N</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">isVisible</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="isVisible" size="1" class="txtbox" style="width: 70px;">
                      <option value="Y">Y</option>
                      <option value="N">N</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Online Fraud Check</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="isonlinefraudcheck" size="1" class="txtbox" style="width: 70px;" >
                      <option value="Y">Y</option>
                      <option value="N">N</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <%
                  if(isAPICallSupported)
                  {
                %>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Is API Call Supported</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="isapiuser" size="1" class="txtbox" style="width: 70px;">
                      <option value="Y" >Y</option>
                      <option value="N">N</option>
                    </select>
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
                      &nbsp;&nbsp;Save
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>
