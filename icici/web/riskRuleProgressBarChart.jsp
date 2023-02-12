<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringSubKeyword" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringKeyword" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 10/27/2016
  Time: 3:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%!
  private static Logger log = new Logger("merchantTerminalThreshold.jsp");
%>
<html>
<head>
  <script src="/icici/javascript/Chart.bundle.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
  <script type="text/javascript">
    function selectTerminals()
    {
      document.f1.action="/icici/riskRuleProgressBarChart.jsp?ctoken=<%=ctoken%>";
      document.f1.submit();
    }
  </script>
</head>
<title> Merchant Threshold </title>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Risk Rule Graph
      </div>
      <%
        Logger logger=new Logger("riskrulemapping.jsp");
        ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
          Functions functions=new Functions();
          String memberId=request.getParameter("memberid");
          String frequency=request.getParameter("frequency");
          String terminalId=request.getParameter("terminalid");
          String ruleid=request.getParameter("ruleid");
          if(!functions.isValueNull(memberId))
          {
            memberId="";
          }
          if(!functions.isValueNull(frequency))
          {
            frequency="";
          }
          if(!functions.isValueNull(terminalId))
          {
            terminalId="";
          }
      %>
      <form action="/icici/servlet/MonitoringGraphController?ctoken=<%=ctoken%>" method="get" name="f1"
            onsubmit="">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="riskrulemapping" name="requestedfilename">
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
                  <td width="20%" class="textb">Member Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <select name="memberid" class="txtbox" style="width: 220px" onchange="selectTerminals()">
                      <option value="" selected></option>
                      <%
                        Connection conn=null;
                        PreparedStatement pstmt=null;
                        ResultSet rs=null;
                        try
                        {
                          //conn= Database.getConnection();
                          conn= Database.getRDBConnection();
                          String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                          pstmt = conn.prepareStatement( query );
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
                    </select>
                  </td>
                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb">Terminal Id</td>
                  <td width="5%" class="textb">
                    <select name="terminalid" class="txtbox" onchange="selectTerminals()">
                      <option value="" selected></option>
                      <%
                        try
                        {
                          //conn = Database.getConnection();
                          conn = Database.getRDBConnection();
                          String query = "SELECT terminalid,paymodeid,cardtypeid,memberid FROM member_account_mapping where memberid='"+request.getParameter("memberid")+"' ";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selection="";
                            if(rs.getString("terminalid").equals(terminalId))
                            {
                              selection="selected";
                            }
                      %>
                      <option value="<%=rs.getInt("terminalid")%>" <%=selection%>><%=rs.getInt("memberid")+"-"+rs.getString("terminalid")+"-"+ GatewayAccountService.getPaymentMode(rs.getString("paymodeid")) + "-" + GatewayAccountService.getCardType(rs.getString("cardtypeid"))%></option>;
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
                    </select>
                  </td>
                  <td width="50%" class="textb">
                  </td>
                </tr>
                </td>
                </tr>
                <tr><td colspan="8">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb">Rule Name</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <select name="ruleid" class="txtbox" style="width: 220px">
                      <option value="" selected></option>
                      <%
                        try
                        {
                          //conn=Database.getConnection();
                          conn=Database.getRDBConnection();
                          String query = "SELECT mampm.monitoing_para_id,mpm.monitoing_para_name,terminalid FROM member_account_monitoringpara_mapping AS mampm JOIN monitoring_parameter_master AS mpm ON mampm.monitoing_para_id=mpm.monitoing_para_id WHERE memberid='"+request.getParameter("memberid")+"' AND terminalid='"+request.getParameter("terminalid")+"'";
                          pstmt = conn.prepareStatement( query );
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selection="";
                            if(rs.getString("monitoing_para_id").equals(ruleid))
                            {
                              selection="selected";
                            }
                      %>
                      <option value="<%=rs.getString("monitoing_para_id")%>" <%=selection%>><%=rs.getString("monitoing_para_name")+" - "+rs.getString("monitoing_para_id")%></option>;
                      <%
                          }
                        }
                        catch(Exception e){
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb">Frequency</td>
                  <td width="5%" class="textb">
                    <select name="frequency" class="txtbox">
                      <%
                        if("weekly".equals(frequency))
                        {
                      %>
                      <option value="weekly">Weekly</option>
                      <option value="hoursly">Hoursly</option>
                      <option value="daily">Daily</option>
                      <option value="monthly">Monthly</option>
                      <option value="all">All</option>
                      <%
                      }
                      else if("daily".equals(frequency))
                      {
                      %>
                      <option value="daily">Daily</option>
                      <option value="hoursly">Hoursly</option>
                      <option value="weekly">Weekly</option>
                      <option value="monthly">Monthly</option>
                      <option value="all">All</option>
                      <%
                      }
                      else if("monthly".equals(frequency))
                      {
                      %>
                      <option value="monthly">Monthly</option>
                      <option value="hoursly">Hoursly</option>
                      <option value="daily">Daily</option>
                      <option value="weekly">Weekly</option>
                      <option value="all">All</option>
                      <%
                      }
                      else if("hoursly".equals(frequency))
                      {
                      %>
                      <option value="hoursly">Hoursly</option>
                      <option value="daily">Daily</option>
                      <option value="weekly">Weekly</option>
                      <option value="monthly">Monthly</option>
                      <option value="all">All</option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="all">All</option>
                      <option value="hoursly">Hoursly</option>
                      <option value="daily">Daily</option>
                      <option value="weekly">Weekly</option>
                      <option value="monthly">Monthly</option>
                      <%
                        }
                      %>
                    </select>
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
    int i = 0;
    //System.out.println("actualInactiveDays::::"+request.getAttribute("actualinactivedays"));
    //System.out.println("weeklyActualInactiveDays::::"+request.getAttribute("weeklyActualInactiveDays"));
    //System.out.println("monthlyActualInactiveDays::::"+request.getAttribute("monthlyActualInactiveDays"));
    int actualInactiveDays=0;
    int weeklyActualInactiveDays=0;
    int monthlyActualInactiveDays=0;
    if(request.getAttribute("actualinactivedays")!=null)
    {
      actualInactiveDays = (int) request.getAttribute("actualinactivedays");
    }
    if(request.getAttribute("weeklyActualInactiveDays")!=null)
    {
      weeklyActualInactiveDays = (int) request.getAttribute("weeklyActualInactiveDays");
    }
    if(request.getAttribute("monthlyActualInactiveDays")!=null)
    {
      monthlyActualInactiveDays = (int) request.getAttribute("monthlyActualInactiveDays");
    }
    if(actualInactiveDays > 0 || weeklyActualInactiveDays > 0 || monthlyActualInactiveDays > 0)
    {
      String unit = "";
      MonitoringParameterVO monitoringParameterVO = (MonitoringParameterVO) request.getAttribute("monitoringParameterVO");
      if(MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
      {
        unit = " D";
      }

      int alertThreshold = (int) request.getAttribute("alertThresholdOrange");
      int suspensionThreshold = (int) request.getAttribute("suspensionThresholdRed");

      int actualInactiveGreen = 0;
      int alertThresholdOrange = 0;
      int suspensionThresholdRed = 0;
      if(actualInactiveDays > alertThreshold && actualInactiveDays > suspensionThreshold)
      {
        actualInactiveGreen = alertThreshold;
        alertThresholdOrange = (suspensionThreshold - actualInactiveGreen);
        suspensionThresholdRed = actualInactiveDays - (actualInactiveGreen + alertThresholdOrange);
      }
      else if(actualInactiveDays > alertThreshold)
      {
        actualInactiveGreen = alertThreshold;
        alertThresholdOrange = actualInactiveDays - actualInactiveGreen;
      }
      else
      {
        actualInactiveGreen = actualInactiveDays;
      }
      int actualInactive = actualInactiveGreen + alertThresholdOrange + suspensionThresholdRed;
      String actualInactivePercentage = "";
      if(actualInactive > 100)
      {
        actualInactivePercentage = "90%";
      }
      else
      {
        if(actualInactive < 50)
        {
          actualInactivePercentage = actualInactive * 1.5 + "%";
        }
        else
        {
          actualInactivePercentage = actualInactive + "%";
        }
      }

      String actualInactiveLabel = "'" + actualInactive + unit + "'";
      int weeklyAlertThreshold = (int) request.getAttribute("weeklyAlertThresholdOrange");
      int weeklySuspensionThreshold = (int) request.getAttribute("weeklySuspensionThresholdRed");

      int weeklyActualInactiveGreen = 0;
      int weeklyAlertThresholdOrange = 0;
      int weeklySuspensionThresholdRed = 0;
      if(weeklyActualInactiveDays > weeklyAlertThreshold && weeklyActualInactiveDays > weeklySuspensionThreshold)
      {
        weeklyActualInactiveGreen = weeklyAlertThreshold;
        weeklyAlertThresholdOrange = (weeklySuspensionThreshold - weeklyActualInactiveGreen);
        weeklySuspensionThresholdRed = weeklyActualInactiveDays - (weeklyActualInactiveGreen + weeklyAlertThresholdOrange);
      }
      else if(weeklyActualInactiveDays > weeklyAlertThreshold)
      {
        weeklyActualInactiveGreen = weeklyAlertThreshold;
        weeklyAlertThresholdOrange = weeklyActualInactiveDays - weeklyActualInactiveGreen;
      }
      else
      {
        weeklyActualInactiveGreen = weeklyActualInactiveDays;
      }
      int weeklyActualInactive = weeklyActualInactiveGreen + weeklyAlertThresholdOrange + weeklySuspensionThresholdRed;
      String weeklyActualInactivePercentage = "";
      if(weeklyActualInactive > 100)
      {
        weeklyActualInactivePercentage = "90%";
      }
      else
      {
        if(weeklyActualInactive < 50)
        {
          weeklyActualInactivePercentage = weeklyActualInactive * 1.5 + "%";
        }
        else
        {
          weeklyActualInactivePercentage = weeklyActualInactive + "%";
        }
      }
      String weeklyActualInactiveLabel = "'" + weeklyActualInactive + unit + "'";
      int monthlyAlertThreshold = (int) request.getAttribute("monthlyAlertThresholdOrange");
      int monthlySuspensionThreshold = (int) request.getAttribute("monthlySuspensionThresholdRed");

      int monthlyActualInactiveGreen = 0;
      int monthlyAlertThresholdOrange = 0;
      int monthlySuspensionThresholdRed = 0;
      if(monthlyActualInactiveDays > monthlyAlertThreshold && monthlyActualInactiveDays > monthlySuspensionThreshold)
      {
        monthlyActualInactiveGreen = monthlyAlertThreshold;
        monthlyAlertThresholdOrange = (monthlySuspensionThresholdRed - monthlyActualInactiveGreen);
        monthlySuspensionThresholdRed = monthlyActualInactiveDays - (monthlyActualInactiveGreen + monthlyAlertThresholdOrange);
      }
      else if(monthlyActualInactiveDays > monthlyAlertThreshold)
      {
        monthlyActualInactiveGreen = monthlyAlertThreshold;
        monthlyAlertThresholdOrange = monthlyActualInactiveDays - monthlyActualInactiveGreen;
      }
      else
      {
        monthlyActualInactiveGreen = monthlyActualInactiveDays;
      }
      int monthlyActualInactive = monthlyActualInactiveGreen + monthlyAlertThresholdOrange + monthlySuspensionThresholdRed;
      String monthlyActualInactivePercentage = "";
      if(monthlyActualInactive > 100)
      {
        monthlyActualInactivePercentage = "90%";
      }
      else
      {
        if(monthlyActualInactive < 50)
        {
          monthlyActualInactivePercentage = monthlyActualInactive * 1.5 + "%";
        }
        else
        {
          monthlyActualInactivePercentage = monthlyActualInactive + "%";
        }
      }

      String monthlyActualInactiveLabel = "'" + monthlyActualInactive + unit + "'";
      String message = (String) request.getAttribute("message");
      String weeklyMessage = (String) request.getAttribute("weeklyMessage");
      String monthlyMessage = (String) request.getAttribute("monthlyMessage");

      String dailyTableData = (String) request.getAttribute("dailyTableData");
      String weeklyTableData = (String) request.getAttribute("weeklyTableData");
      String monthlyTableData = (String) request.getAttribute("monthlyTableData");
  %>
  <%
    if(("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))) && functions.isValueNull(dailyTableData))
    {
  %>
  <script type="text/javascript">
    $(document).ready(function ()
    {
      fdaily();
    });

    function fdaily()
    {
      $(".progress").css("width", "100%")
      <%
        if(actualInactiveDays > 0 && actualInactiveDays <= alertThreshold)
        {
      %>
      $('#progress-bar-success-daily').animate(
              {
                width: "<%=actualInactivePercentage%>"
              },1);
      $('#progress-bar-success-daily').text(<%=actualInactiveLabel%>);
      <%
       }
       if(actualInactiveDays > alertThreshold && actualInactiveDays <= suspensionThreshold)
       {
      %>
      $('#progress-bar-warning-daily').animate(
              {
                width: "<%=actualInactivePercentage%>"
              },1);
      $('#progress-bar-warning-daily').text(<%=actualInactiveLabel%>);
      <%
        }
        if(actualInactiveDays > suspensionThreshold)
        {
      %>
      $('#progress-bar-danger-daily').animate(
              {
                width: "<%=actualInactivePercentage%>"
              },1);
      $('#progress-bar-danger-daily').text(<%=actualInactiveLabel%>);
      <%
        }
      %>
    }
  </script>
  <%
    }
    if(("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))) && ((functions.isValueNull(weeklyTableData) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))))
    {
  %>
  <script type="text/javascript">
    $(document).ready(function ()
    {
      fweekly();
    });

    function fweekly()
    {
      $(".progress").css("width", "100%")
      <%
        if(weeklyActualInactiveDays > 0 && weeklyActualInactiveDays <= weeklyAlertThreshold)
        {
      %>
      $('#progress-bar-success-weekly').animate(
              {
                width: "<%=weeklyActualInactivePercentage%>"
              },1);
      $('#progress-bar-success-weekly').text(<%=weeklyActualInactiveLabel%>);
      <%
       }
       if(weeklyActualInactiveDays > weeklyAlertThreshold && weeklyActualInactiveDays <= weeklySuspensionThreshold)
       {
      %>
      $('#progress-bar-warning-weekly').animate(
              {
                width: "<%=weeklyActualInactivePercentage%>"
              },1);
      $('#progress-bar-warning-weekly').text(<%=weeklyActualInactiveLabel%>);
      <%
        }
        if(weeklyActualInactiveDays > weeklySuspensionThreshold)
        {
      %>
      $('#progress-bar-danger-weekly').animate(
              {
                width: "<%=weeklyActualInactivePercentage%>"
              },1);
      $('#progress-bar-danger-weekly').text(<%=weeklyActualInactiveLabel%>);
      <%
        }
      %>
    }
  </script>
  <%
    }
    if(("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))) && (functions.isValueNull(monthlyTableData) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())))
    {
  %>
  <script type="text/javascript">
    $(document).ready(function ()
    {
      fmonthly();
    });

    function fmonthly()
    {
      $(".progress").css("width", "100%")
      <%
        if(monthlyActualInactiveDays > 0 && monthlyActualInactiveDays <= monthlyAlertThreshold)
        {
      %>
      $('#progress-bar-success-monthly').animate(
              {
                width: "<%=monthlyActualInactivePercentage%>"
              },1);
      $('#progress-bar-success-monthly').text(<%=monthlyActualInactiveLabel%>);
      <%
       }
       if(monthlyActualInactiveDays > monthlyAlertThreshold && monthlyActualInactiveDays <= monthlySuspensionThreshold)
       {
      %>
      $('#progress-bar-warning-monthly').animate(
              {
                width: "<%=monthlyActualInactivePercentage%>"
              },1);
      $('#progress-bar-warning-monthly').text(<%=monthlyActualInactiveLabel%>);
      <%
        }
        if(monthlyActualInactiveDays > monthlySuspensionThreshold)
        {
      %>
      $('#progress-bar-danger-monthly').animate(
              {
                width: "<%=monthlyActualInactivePercentage%>"
              },1);
      $('#progress-bar-danger-monthly').text(<%=monthlyActualInactiveLabel%>);
      <%
        }
      %>
    }
  </script>
  <%
    }
  %>

  <%
    if(("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))) && functions.isValueNull(dailyTableData))
    {
  %>
  <div style="width:100%;">
    <table border="1" style="width:100%; height:200px;" >
      <tr>
        <td width="40%" align="center" rowspan="3">
          <%=dailyTableData%>
        </td>
        <td align="center" class="textb">
          <b><%=message%></b>
        </td>
      </tr>
      <tr>
        <td width="60%">
          <div style="width:100%; margin-top:5px; height:25px;" class="progress">
            <span style="width:25%;background-color: #ff0000;border:2px">
              <div class="progress-bar progress-bar-success" id="progress-bar-success-daily" role="progressbar"></div>
            </span>
            <span style="width:25%;background-color: #ff0000;border:2px">
                <div class="progress-bar progress-bar-warning" id="progress-bar-warning-daily" role="progressbar"></div>
            </span>
            <span style="width:40%;border-color: #ff0000;border:2px">
               <div class="progress-bar progress-bar-danger" id="progress-bar-danger-daily" role="progressbar"></div>
            </span>
          </div>
        </td>
      </tr>
      <tr>
        <td width="50%" align="center">
          <table border="0" align="left" style="margin-left:8px;">
            <tr>
              <td bgcolor="#50C46E" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Below Threshold (<= <%=alertThreshold%>)</td>

              <td bgcolor="#F2B835" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Above Threshold (> <%=alertThreshold%> & <=<%=suspensionThreshold%>)&nbsp;&nbsp;</td>

              <td bgcolor="#E74C3C" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Suspended (> <%=suspensionThreshold%>)</td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </div>
  <%
    }
    if(("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))) && (functions.isValueNull(weeklyTableData) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())))
    {
  %>
  <div style="width:100%;">
    <table border="1" style="width:100%; height:200px;" >
      <tr>
        <td width="40%" align="center" rowspan="3">
          <%=weeklyTableData%>
        </td>
        <td align="center" class="textb">
          <b><%=weeklyMessage%></b>
        </td>
      </tr>
      <tr>
        <td width="60%">
          <div style="width:100%; margin-top:5px; height:25px;" class="progress">
            <span style="width:25%;background-color: #ff0000;border:2px">
              <div class="progress-bar progress-bar-success" id="progress-bar-success-weekly" role="progressbar"></div>
            </span>
            <span style="width:25%;background-color: #ff0000;border:2px">
                <div class="progress-bar progress-bar-warning" id="progress-bar-warning-weekly" role="progressbar"></div>
            </span>
            <span style="width:40%;border-color: #ff0000;border:2px">
               <div class="progress-bar progress-bar-danger" id="progress-bar-danger-weekly" role="progressbar"></div>
            </span>
          </div>
        </td>
      </tr>
      <tr>
        <td width="50%" align="center">
          <table border="0" align="left" style="margin-left:8px;">
            <tr>
              <td bgcolor="#50C46E" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Below Threshold (<= <%=weeklyAlertThreshold%>)</td>

              <td bgcolor="#F2B835" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Above Threshold (> <%=weeklyAlertThreshold%> & <=<%=weeklySuspensionThreshold%>)&nbsp;&nbsp;</td>

              <td bgcolor="#E74C3C" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Suspended (> <%=weeklySuspensionThreshold%>)</td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </div>
  <%
    }
    if(("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))) && (functions.isValueNull(monthlyTableData) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())))
    {
  %>
  <div style="width:100%;">
    <table border="1" style="width:100%; height:200px;" >
      <tr>
        <td width="40%" align="center" rowspan="3">
          <%=monthlyTableData%>
        </td>
        <td align="center" class="textb">
          <b><%=monthlyMessage%></b>
        </td>
      </tr>
      <tr>
        <td width="60%">
          <div style="width:100%; margin-top:5px; height:25px;" class="progress">
            <span style="width:25%;background-color: #ff0000;border:2px">
              <div class="progress-bar progress-bar-success" id="progress-bar-success-monthly" role="progressbar"></div>
            </span>
            <span style="width:25%;background-color: #ff0000;border:2px">
                <div class="progress-bar progress-bar-warning" id="progress-bar-warning-monthly" role="progressbar"></div>
            </span>
            <span style="width:40%;border-color: #ff0000;border:2px">
               <div class="progress-bar progress-bar-danger" id="progress-bar-danger-monthly" role="progressbar"></div>
            </span>
          </div>
        </td>
      </tr>
      <tr>
        <td width="50%" align="center">
          <table border="0" align="left" style="margin-left:8px;">
            <tr>
              <td bgcolor="#50C46E" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Below Threshold (<= <%=monthlyAlertThreshold%>)</td>

              <td bgcolor="#F2B835" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Above Threshold (> <%=monthlyAlertThreshold%> & <=<%=monthlySuspensionThreshold%>)&nbsp;&nbsp;</td>

              <td bgcolor="#E74C3C" width="6%"></td>
              <td width="30%" class="textb">&nbsp; Suspended (> <%=monthlySuspensionThreshold%>)</td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </div>
  <%
    }
  }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No Data Found."));
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