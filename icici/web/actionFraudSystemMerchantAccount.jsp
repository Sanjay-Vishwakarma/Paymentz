<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO" %>
<%@ page import="servlets.ChargesUtils" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 22/7/15
  Time: 3:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
    <title>Merchant Fraud Accounts</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<%
  String memberid=nullToStr(request.getParameter("mid"));
  Logger logger = new Logger("actionFraudSystemMerchantAccount.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
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
        Merchant Fraud Accounts
        <div style="float: right;">
          <form action="/icici/manageMarchantFraudAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add Merchant Fraud Account" name="submit" class="addnewmember" style="width: 300px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add Merchant Fraud Account
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemMerchantSubAccountList?ctoken=<%=ctoken%>" method="post" name="forms">
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
                  <td width="10%" class="textb" >Member ID</td>
                  <td width="12%" class="textb">
                    <input name="mid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                    <%--<select name="mid" class="txtbox" style="width: 200px;"><option value="" selected>ALL</option>
                      <%
                        Hashtable<String, String> members = ChargesUtils.getMembers();
                        Iterator it1 = members.entrySet().iterator();
                        while (it1.hasNext())
                        {
                          Map.Entry pair = (Map.Entry)it1.next();
                          out.println("<option value=\""+pair.getKey()+"\">"+pair.getKey()+" - "+pair.getValue()+"</option>");
                        }
                      %>
                    </select--%>
                  </td>
                  <td width="7%" class="textb">&nbsp;</td>
                  <td width="9%" class="textb" >Fraud Sub Account</td>
                  <td width="12%" class="textb">
                    <select name="subaccount" class="txtbox" style="width: 350px;"><option value="" selected>ALL</option>
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
                  <td width="9%" class="textb"></td>
                  <td width="9%" class="textb">
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
        Update Merchant Fraud Account Configuration
      </div>
      <%
        MerchantFraudAccountVO fraudAccountVO = (MerchantFraudAccountVO) request.getAttribute("merchantAccountVO");
        if(fraudAccountVO!=null)
        {
      %>
      <form action="/icici/servlet/ActionFraudSystemMerchantAccount?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" name="mappingid" value="<%=fraudAccountVO.getMerchantFraudAccountId() %>">
        <input type="hidden" name="merchantfraudid" value="<%=fraudAccountVO.getMerchantFraudAccountId()%>">
        <input type="hidden" name="fssubaccountid" value="<%=fraudAccountVO.getFsSubAccountId()%>">
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
                  <td style="padding: 3px" width="43%" class="textb">Member ID</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="mid1" class="txtbox" value="<%=fraudAccountVO.getMemberId()%>" disabled>
                    <input type="hidden" name="mid" value="<%=fraudAccountVO.getMemberId()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb" >Fraud System SubMerchant UserName</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="submerchantUsername" class="txtbox" maxlength="20" value="<%=fraudAccountVO.getSubmerchantUsername()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"> Fraud System SubMerchant Password</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input type="text" size="30" name="submerchantPassword" class="txtbox" maxlength="20" value="<%=fraudAccountVO.getSubmerchantPassword()%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">isActive</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="isActive">
                      <% if(fraudAccountVO.getIsActive().equals("Y")){
                      %>
                      <option value="Y" selected>Y</option>
                      <option value="N">N</option>
                      <%}else{%>
                      <option value="Y" >Y</option>
                      <option value="N" selected>N</option>
                      <%}%>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">isVisible</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="isVisible">
                      <% if(fraudAccountVO.getIsVisible().equals("Y")){
                      %>
                      <option value="Y" selected>Y</option>
                      <option value="N">N</option>
                      <%}else{%>
                      <option value="Y" >Y</option>
                      <option value="N" selected>N</option>
                      <%}%>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Online Fraud Check</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="isOnlineFraudCheck">
                      <% if(fraudAccountVO.getIsOnlineFraudCheck().equals("Y")){
                      %>
                      <option value="Y" selected>Y</option>
                      <option value="N">N</option>
                      <%}else{%>
                      <option value="Y" >Y</option>
                      <option value="N" selected>N</option>
                      <%}%>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Is API Call Supported</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <select name="isAPIUser">
                      <% if(fraudAccountVO.getIsAPIUser().equals("Y")){
                      %>
                      <option value="Y" selected>Y</option>
                      <option value="N">N</option>
                      <%}else{%>
                      <option value="Y" >Y</option>
                      <option value="N" selected>N</option>
                      <%}%>
                    </select>
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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>