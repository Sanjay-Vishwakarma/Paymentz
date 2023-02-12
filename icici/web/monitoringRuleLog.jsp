<%@ page import="com.directi.pg.Database,com.directi.pg.Functions" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringRuleLogVO" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.List" %>
<%!
  private static Logger logger = new Logger("monitoringRuleLog.jsp");
%>
<html>
<head>

  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>

  <style>
    .multiselect {
      width: 100px;
    }
    .selectBox {
      position: relative;
    }
    .selectBox select {
      width: 100%;
      font-weight: bold;
    }
    .overSelect {
      position: absolute;
      left: 0; right: 0; top: 0; bottom: 0;
    }
    .checkboxes {
      display: none;
      border: 1px #dadada solid;
    }
    .checkboxes label {
      display: block;
    }
    .checkboxes label:hover {
      background-color: #1e90ff;
    }
  </style>
  <%--<script>
    function selectTerminals(data)
    {
      document.f1.submit();
    }
  </script>--%>
</head>
<title> Merchants Management > Monitoring Rule Change Log</title>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Risk Rule Change History
      </div>
      <%
        ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
          String memberId = nullToStr(request.getParameter("memberid"));
          String terminalid = nullToStr(request.getParameter("terminalid"));
      %>
      <form action="/icici/servlet/MonitoringRuleLog?ctoken=<%=ctoken%>" method="get" name="f1">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="monitoringRuleLog" name="requestedfilename">
        <br>
        <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <%
                String errormsg1 = (String) request.getAttribute("error");
                if (errormsg1 != null)
                {
                  out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                }
              %>
              <%
                String errormsg = (String) request.getAttribute("cbmessage");
                if (errormsg == null)
                  errormsg = "";
                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                out.println(errormsg);
                out.println("</b></font></td></tr></table>");
              %>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" for="midy">Member Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input name="memberid" id="midy" value="<%=memberId%>" class="txtbox" autocomplete="on">
                   <%-- <select name="memberid" class="txtbox" style="width: 162px" onchange="selectTerminals(this)">
                      <option value="" selected>--Select--</option>
                      <%
                        Connection conn=null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          //conn= Database.getConnection();
                          conn= Database.getRDBConnection();
                          String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selection="";
                            if(rs.getString("memberid").equals(memberId))
                            {
                              selection="selected";
                            }
                      %>
                      <option value="<%=rs.getInt("memberid")%>" <%=selection%>><%=rs.getInt("memberid")+"-"+rs.getString("company_name")%></option>;
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
                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" for="tid">TerminalId</td>
                  <td width="5%" class="textb">
                    <input name="terminalid" id="tid" value="<%=terminalid%>"  class="txtbox" autocomplete="on">
                   <%-- <select name="terminalid" class="txtbox" style="width: 162px">
                      <option value="" selected>--Select--</option>
                      <%
                        try
                        {
                          //conn = Database.getConnection();
                          conn = Database.getRDBConnection();
                          String query = "SELECT terminalid,paymodeid,cardtypeid,memberid FROM member_account_mapping where memberid='" + memberId + "'";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getString("terminalid") + "\">" + rs.getString("memberid") + "-" + rs.getString("terminalid") + "-" + GatewayAccountService.getPaymentMode(rs.getString("paymodeid")) + "-" + GatewayAccountService.getCardType(rs.getString("cardtypeid")) + "</option>");
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
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable" style="margin-bottom: 9px;">
  <%
    List<MonitoringRuleLogVO> monitoringRuleLogVOs=(List)request.getAttribute("monitoringRuleLogVOs");
    if (monitoringRuleLogVOs != null && monitoringRuleLogVOs.size() > 0)
    {
      TerminalManager terminalManager=new TerminalManager();
      TerminalVO terminalVO = terminalManager.getActiveInActiveTerminalInfo(request.getParameter("terminalid"));
      GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
      String currency = gatewayAccount.getCurrency();
      String bank = gatewayAccount.getGateway();
      String cardType = terminalVO.getCardType();
  %>
  <table border="0" width="100%" class="table table-striped table-bordered table-green dataTable"
         style="margin-bottom: 0px">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0" colspan='4'><font size=2><%=bank%> - <%=currency%>
        - <%=cardType%>
      </font></td>
      <td valign="middle" align="center" class="th0"
          style="border-left-style: hidden"><%=terminalVO.toString()%>
      </td>
    </tr>
    </thead>
    <thead>
    <tr>
      <td class="textb" valign="middle" align="center" rowspan="2"><b>Risk Rule ID</b></td>
      <td class="textb" valign="middle" align="center" rowspan="2"><b>Risk Rule Name</b></td>
      <td class="textb" valign="middle" align="center" rowspan="2"><b>Action Executor</b></td>
      <td class="textb" valign="middle" align="center" rowspan="2"><b>Modified On</b></td>
      <td class="textb" valign="middle" align="center" rowspan="2"><b>See Changes</b></td>
    </tr>
    </thead>
    <%
      for(MonitoringRuleLogVO monitoringRuleLogVO:monitoringRuleLogVOs)
      {
        MonitoringParameterMappingVO monitoringParameterMappingVO=monitoringRuleLogVO.getMonitoringParameterMappingVO();
        MonitoringParameterVO monitoringParameterVO=monitoringParameterMappingVO.getMonitoringParameterVO();
    %>
    <tr>
      <td align="center" class="textb">
        <%=monitoringParameterVO.getMonitoringParameterId()%></td>
      <td align="center" class="textb"><%=monitoringParameterVO.getMonitoringParameterName()%></td>
      <td align="center" class="textb"><%=monitoringRuleLogVO.getActionExecutor()%></td>
      <td align="center" class="textb"><%=monitoringRuleLogVO.getModifiedOn()%></td>
      <td align="center" class="textb"><button type="submit" class="goto" onclick="window.open('GetRuleChangeHistoryDetails?terminalid=<%=terminalVO.getTerminalId()%>&historyid=<%=monitoringRuleLogVO.getHistoryId()%>&ruleid=<%=monitoringParameterVO.getMonitoringParameterId()%>&ctoken=<%=user.getCSRFToken()%>', 'newwindow', 'width=500, height=600'); return false;">See Changes</button></td>
    </tr>
    <%
      }
    %>
  </table>
  </form>
  <%
    }
    else if (request.getAttribute("updatemsg") != null)
    {
      out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
      out.println((String) request.getAttribute("updatemsg"));
      out.println("</b></font>");
      out.println("</td></tr></table>");
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
    }
  %>
</div>
<%
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