<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringKeyword" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%@ page import="com.manager.MerchantMonitoringManager" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 12/9/2016
  Time: 5:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%!
  private static Logger log = new Logger("merchantTerminalThreshold.jsp");
%>
<html>
<head>
  <script src="/icici/javascript/Chart.bundle.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
  <style>
    canvas {
      -moz-user-select: none;
      -webkit-user-select: none;
      -ms-user-select: none;
    }
  </style>
  <script type="text/javascript">
    function selectTerminals()
    {
      document.f1.action="/icici/riskRuleToggleBarChart.jsp?ctoken=<%=ctoken%>";
      document.f1.submit();
    }
    function dailyChange(data)
    {
      if('Detail View' == data.innerText)
      {
        document.getElementById("dailytogglebutton").innerText = "Composite View";
      }
      else
      {
        document.getElementById("dailytogglebutton").innerText = "Detail View";
      }
    }
    function weeklyChange(data)
    {
      if('Detail View' == data.innerText)
      {
        document.getElementById("weeklytogglebutton").innerText = "Composite View";
      }
      else
      {
        document.getElementById("weeklytogglebutton").innerText = "Detail View";
      }
    }
    function weeklyNewChange(data)
    {
      if('Detail View' == data.innerText)
      {
        document.getElementById("weeklytogglebuttonnew").innerText = "Composite View";
      }
      else
      {
        document.getElementById("weeklytogglebuttonnew").innerText = "Detail View";
      }
    }
    function monthlyChange(data)
    {
      if('Detail View' == data.innerText)
      {
        document.getElementById("monthlytogglebutton").innerText = "Composite View";
      }
      else
      {
        document.getElementById("monthlytogglebutton").innerText = "Detail View";
      }
    }

    function showHideView(data)
    {
      if('dailytoggledetail' == data.value)
      {
        showDailyToggleComposite(data);
        $('#showDetail').hide();
        //$('#showComposite').show();
      }
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
    String ruleId = request.getParameter("ruleid");
    MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
    MonitoringParameterVO monitoringParameterVO = merchantMonitoringManager.getMonitoringParameterDetails(ruleId);

    String dailyJsonStr = (String)request.getAttribute("dailyJsonStr");
    String weeklyJsonStr = (String)request.getAttribute("weeklyJsonStr");
    String weeklyNewJsonStr = (String)request.getAttribute("weeklyNewJsonStr");
    String monthlyJsonStr = (String)request.getAttribute("monthlyJsonStr");

 /*   System.out.println("dailyJsonStr======"+dailyJsonStr);
    System.out.println("weeklyJsonStr======"+weeklyJsonStr);
    System.out.println("monthlyJsonStr======"+monthlyJsonStr);*/

    String dailyTableData = (String)request.getAttribute("dailyTableData");
    String weeklyTableData = (String)request.getAttribute("weeklyTableData");
    String weeklyNewTableData = (String)request.getAttribute("weeklyNewTableData");
    String monthlyTableData = (String)request.getAttribute("monthlyTableData");

   /* System.out.println("dailyTableData======"+dailyTableData);
    System.out.println("weeklyTableData======"+weeklyTableData);
    System.out.println("monthlyTableData======"+monthlyTableData);*/

    String displayRuleName=(String)request.getAttribute("displayRuleName");
    //System.out.println("displayRuleName======"+displayRuleName);

    String dailyJsonStr1 = (String)request.getAttribute("dailyJsonStr1");
    String weeklyJsonStr1 = (String)request.getAttribute("weeklyJsonStr1");
    String weeklyNewJsonStr1 = (String)request.getAttribute("weeklyNewJsonStr1");
    String monthlyJsonStr1 = (String)request.getAttribute("monthlyJsonStr1");

    long maxRatioCompositeHoursly = (long)request.getAttribute("maxRatioCompositeHoursly");
    long maxRatioCompositeDaily = (long)request.getAttribute("maxRatioCompositeDaily");
    long maxRatioCompositeWeekly = (long)request.getAttribute("maxRatioCompositeWeekly");
    long maxRatioCompositeMonthly = (long)request.getAttribute("maxRatioCompositeMonthly");

    long maxRatioDetailHoursly = (long)request.getAttribute("maxRatioDetailHoursly");
    long maxRatioDetailDaily = (long)request.getAttribute("maxRatioDetailDaily");
    long maxRatioDetailWeekly = (long)request.getAttribute("maxRatioDetailWeekly");
    long maxRatioDetailMonthly = (long)request.getAttribute("maxRatioDetailMonthly");

    /*System.out.println("dailyJsonStr1:::"+dailyJsonStr1);
    System.out.println("weeklyJsonStr1:::"+weeklyJsonStr1);*/
    String ticksHoursly = "";
    String ticksDaily = "";
    String ticksWeekly = "";
    String ticksMonthly = "";
    /*if(maxRatioCompositeHoursly > 5 || maxRatioDetailHoursly > 5)
    {
      ticksHoursly = "";
    }
    else
    {
      ticksHoursly = "ticks: {\n" +
              "                          beginAtZero: true,\n" +
              "                          steps: 1,\n" +
              "                          max: 5\n" +
              "                        },";
    }
    if(maxRatioCompositeDaily > 5 || maxRatioDetailDaily > 5)
    {
      ticksDaily = "";
    }
    else
    {
      ticksDaily = "ticks: {\n" +
              "                          beginAtZero: true,\n" +
              "                          steps: 1,\n" +
              "                          max: 5\n" +
              "                        },";
    }
    if(maxRatioCompositeWeekly > 5 || maxRatioDetailWeekly > 5)
    {
      ticksWeekly = "";
    }
    else
    {
      ticksWeekly = "ticks: {\n" +
              "                          beginAtZero: true,\n" +
              "                          steps: 1,\n" +
              "                          max: 5\n" +
              "                        },";
    }
    if(maxRatioCompositeMonthly > 5 || maxRatioDetailMonthly > 5)
    {
      ticksMonthly = "";
    }
    else
    {
      ticksMonthly = "ticks: {\n" +
              "                          beginAtZero: true,\n" +
              "                          steps: 1,\n" +
              "                          max: 5\n" +
              "                        },";
    }*/

    /*System.out.println("maxRatioCompositeWeekly===="+maxRatioCompositeWeekly);
    System.out.println("maxRatioDetailWeekly===="+maxRatioDetailWeekly);
    System.out.println("ticksWeekly===="+ticksWeekly);*/
    String value = "";
    if(MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
    {
      value = "Count";
    }
    if(MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
    {
      value = "Amount";
    }
    if(MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
    {
      value = "Day";
    }

    if(functions.isValueNull(dailyJsonStr) || functions.isValueNull(weeklyJsonStr) || functions.isValueNull(monthlyJsonStr) || functions.isValueNull(weeklyNewJsonStr))
    {
  %>

  <div style="width:100%;">
    <%
      if(functions.isValueNull(dailyJsonStr) && ("all".equals(request.getParameter("frequency")) || "hoursly".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <div id="dailyDefault">
        <tr>
          <td width="50%" align="center">
            <div id="dailyDefaultTable">
              <%=dailyTableData%>
            </div>
          </td>
          <td width="50%">
            <button type="submit" class="buttonform" style="margin-left:10px;margin-top:10px;" value="dailytoggle" onclick="showDailyToggle(this); dailyChange(this)" id="dailytogglebutton">
              &nbsp;&nbsp;Composite View
            </button>
            <canvas id="canvas-daily"></canvas>
          </td>
        </tr>
      </div>
    </table>
    <%
      }
      if(functions.isValueNull(weeklyJsonStr) && ("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <div id="weeklyDefault">
        <tr>
          <td width="50%" align="center">
            <div id="weeklyDefaultTable">
              <%=weeklyTableData%>
            </div>
          </td>
          <td width="50%">
            <button type="submit" class="buttonform" style="margin-left:10px; margin-top:10px;" value="weeklytoggle" onclick="showWeeklyToggle(this); weeklyChange(this)" id="weeklytogglebutton">
              &nbsp;&nbsp;Composite View
            </button>
            <canvas id="canvas-weekly"></canvas>
          </td>
        </tr>
      </div>
    </table>
    <%
      }
      if(functions.isValueNull(weeklyNewJsonStr) && ("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <div id="weeklyDefaultNew">
        <tr>
          <td width="50%" align="center">
            <div id="weeklyDefaultTableNew">
              <%=weeklyNewTableData%>
            </div>
          </td>
          <td width="50%">
            <button type="submit" class="buttonform" style="margin-left:10px; margin-top:10px;" value="weeklytogglenew" onclick="showWeeklyToggleNew(this); weeklyNewChange(this)" id="weeklytogglebuttonnew">
              &nbsp;&nbsp;Composite View
            </button>
            <canvas id="canvas-weekly-new"></canvas>
          </td>
        </tr>
      </div>
    </table>
    <%
      }
      if(functions.isValueNull(monthlyJsonStr) && ("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <div id="monthlyDefault">
        <tr>
          <td width="50%" align="center">
            <div id="monthlyDefaultTable">
              <%=monthlyTableData%>
            </div>
          </td>
          <td width="50%">
            <button type="submit" class="buttonform" style="margin-left:10px; margin-top:10px;" value="monthlytoggle" onclick="showMonthlyToggle(this); monthlyChange(this)" id="monthlytogglebutton">
              &nbsp;&nbsp;Composite View
            </button>
            <canvas id="canvas-monthly"></canvas>
          </td>
        </tr>
      </div>
    </table>
    <br>
    <br>
    <%
      }
    %>
    <%
      String dailyText;
      String weeklyText;
      String newWeeklyText;
      String monthlyText;

      dailyText = displayRuleName+" [today]";
      dailyText="'"+dailyText+"'";
      weeklyText = displayRuleName+" [in current week]";
      weeklyText="'"+weeklyText+"'";
      newWeeklyText = displayRuleName+" [in current month]";
      newWeeklyText="'"+newWeeklyText+"'";
      monthlyText = displayRuleName+" [in last six months]";
      monthlyText="'"+monthlyText+"'";
    %>
    <script>
      window.onload = function()
      {
        <%
         if(functions.isValueNull(dailyJsonStr) && ("all".equals(request.getParameter("frequency")) || "hoursly".equals(request.getParameter("frequency"))))
         {
        %>
        var hourslyCtx = document.getElementById("canvas-daily").getContext("2d");
        window.myBar = new Chart(hourslyCtx,
                {
                  type: 'bar',
                  data: <%=dailyJsonStr%>,
                  options:
                  {
                    title:
                    {
                      display:true,
                      text:<%=dailyText%>
                    },
                    tooltips:
                    {
                      mode: 'label'
                    },
                    responsive: true,
                    scales:
                    {
                      xAxes: [{
                        stacked: true,
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'Time (In Hrs)'
                        }
                      }],
                      yAxes: [{
                        stacked: true,
                        display: true,
                        <%=ticksHoursly%>
                        scaleLabel: {
                          display: true,
                          labelString: '<%=value%>'
                        }
                      }]
                    }
                  }
                });
        <%
         }
         if(functions.isValueNull(weeklyJsonStr) && ("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))))
         {
         %>
        var dailyCtx = document.getElementById("canvas-weekly").getContext("2d");
        window.myBar = new Chart(dailyCtx,
                {
                  type: 'bar',
                  data: <%=weeklyJsonStr%>,
                  options:
                  {
                    title:
                    {
                      display:true,
                      text:<%=weeklyText%>
                    },
                    tooltips:
                    {
                      mode: 'label'
                    },
                    responsive: true,
                    scales:
                    {
                      xAxes: [{
                        stacked: true,
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'Week Days'
                        }
                      }],
                      yAxes: [{
                        stacked: true,
                        display: true,
                        <%=ticksDaily%>
                        scaleLabel: {
                          display: true,
                          labelString: '<%=value%>'
                        }
                      }]
                    }
                  }
                });
        <%
         }
         if(functions.isValueNull(weeklyNewJsonStr) && ("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))))
         {
        %>
        var weeklyCtx = document.getElementById("canvas-weekly-new").getContext("2d");
        window.myBar = new Chart(weeklyCtx,
                {
                  type: 'bar',
                  data: <%=weeklyNewJsonStr%>,
                  options:
                  {
                    title:
                    {
                      display:true,
                      text:<%=newWeeklyText%>
                    },
                    tooltips:
                    {
                      mode: 'label'
                    },
                    responsive: true,
                    scales:
                    {
                      xAxes: [{
                        stacked: true,
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'One Month'
                        }
                      }],
                      yAxes: [{
                        stacked: true,
                        display: true,
                        <%=ticksWeekly%>
                        scaleLabel: {
                          display: true,
                          labelString: '<%=value%>'
                        }
                      }]
                    }
                  }
                });
        <%
         }
         if(functions.isValueNull(monthlyJsonStr) && ("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))))
         {
        %>
        var monthlyCtx = document.getElementById("canvas-monthly").getContext("2d");
        window.myBar = new Chart(monthlyCtx,
                {
                  type: 'bar',
                  data: <%=monthlyJsonStr%>,
                  options:
                  {
                    title:
                    {
                      display:true,
                      text:<%=monthlyText%>
                    },
                    tooltips:
                    {
                      mode: 'label'
                    },
                    responsive: true,
                    scales:
                    {
                      xAxes: [{
                        stacked: true,
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'Last Six Months'
                        }
                      }],
                      yAxes: [{
                        stacked: true,
                        display: true,
                        <%=ticksMonthly%>
                        scaleLabel: {
                          display: true,
                          labelString: '<%=value%>'
                        }
                      }]
                    }
                  }
                });
        <%
       }
       %>
      };
      function showDailyToggle(data)
      {
        if('dailytoggle'==data.value)
        {
          document.getElementById('dailyDefaultTable').innerHTML ='<%=dailyTableData%>';
          document.getElementById('dailytogglebutton').value = 'dailyDefault';
          <%
          if(functions.isValueNull(dailyJsonStr1) && ("all".equals(request.getParameter("frequency")) || "hoursly".equals(request.getParameter("frequency"))))
          {
          %>
          var hourslyCtx = document.getElementById("canvas-daily").getContext("2d");
          window.myBar = new Chart(hourslyCtx,
                  {
                    type: 'bar',
                    data: <%=dailyJsonStr1%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=dailyText%>
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'Time (In Hrs)'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksHoursly%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
           }
          %>
        }
        else
        {
          document.getElementById('dailyDefaultTable').innerHTML ='<%=dailyTableData%>';
          document.getElementById('dailytogglebutton').value = 'dailytoggle';
          <%
          if(functions.isValueNull(dailyJsonStr) && ("all".equals(request.getParameter("frequency")) || "hoursly".equals(request.getParameter("frequency"))))
          {
          %>
          var hourslyCtx = document.getElementById("canvas-daily").getContext("2d");
          window.myBar = new Chart(hourslyCtx,
                  {
                    type: 'bar',
                    data: <%=dailyJsonStr%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=dailyText%>
                      },
                      tooltips:
                      {
                        mode: 'label'
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'Time (In Hrs)'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksHoursly%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
           }
          %>
        }
      }
      function showWeeklyToggle(data)
      {
        if('weeklytoggle'==data.value)
        {
          document.getElementById('weeklyDefaultTable').innerHTML='<%=weeklyTableData%>';
          document.getElementById('weeklytogglebutton').value = 'weeklyDefault';
          <%
           if(functions.isValueNull(weeklyJsonStr1) && ("all".equals(request.getParameter("frequency"))  || "daily".equals(request.getParameter("frequency"))))
           {
           %>
          var dailyCtx = document.getElementById("canvas-weekly").getContext("2d");
          window.myBar = new Chart(dailyCtx,
                  {
                    type: 'bar',
                    data: <%=weeklyJsonStr1%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=weeklyText%>
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'Week Days'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksDaily%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
           }
          %>
        }
        else
        {
          document.getElementById('weeklyDefaultTable').innerHTML='<%=weeklyTableData%>';
          document.getElementById('weeklytogglebutton').value='weeklytoggle';
          <%
          if(functions.isValueNull(weeklyJsonStr) && ("all".equals(request.getParameter("frequency"))  || "daily".equals(request.getParameter("frequency"))))
          {
          %>
          var dailyCtx = document.getElementById("canvas-weekly").getContext("2d");
          window.myBar = new Chart(dailyCtx,
                  {
                    type: 'bar',
                    data: <%=weeklyJsonStr%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=weeklyText%>
                      },
                      tooltips:
                      {
                        mode: 'label'
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'Week Days'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksDaily%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
          }
          %>
        }
      }
      function showWeeklyToggleNew(data)
      {
        if('weeklytogglenew'==data.value)
        {
          document.getElementById('weeklyDefaultTableNew').innerHTML='<%=weeklyNewTableData%>';
          document.getElementById('weeklytogglebuttonnew').value = 'weeklyDefaultNew';
          <%
           if(functions.isValueNull(weeklyNewJsonStr1) && ("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))))
           {
          %>
          var weeklyCtx = document.getElementById("canvas-weekly-new").getContext("2d");
          window.myBar = new Chart(weeklyCtx,
                  {
                    type: 'bar',
                    data: <%=weeklyNewJsonStr1%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=monthlyText%>
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'One Month'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksWeekly%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
         }
         %>
        }
        else
        {
          document.getElementById('weeklyDefaultTableNew').innerHTML='<%=weeklyNewTableData%>';
          document.getElementById('weeklytogglebuttonnew').value='weeklytogglenew';
          <%
              if(functions.isValueNull(weeklyNewJsonStr) && ("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))))
              {
          %>
          var weeklyCtx = document.getElementById("canvas-weekly-new").getContext("2d");
          window.myBar = new Chart(weeklyCtx,
                  {
                    type: 'bar',
                    data: <%=weeklyNewJsonStr%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=monthlyText%>
                      },
                      tooltips:
                      {
                        mode: 'label'
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'One Month'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksWeekly%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
          }
          %>
        }
      }
      function showMonthlyToggle(data)
      {
        if('monthlytoggle'==data.value)
        {
          document.getElementById('monthlyDefaultTable').innerHTML='<%=monthlyTableData%>';
          document.getElementById('monthlytogglebutton').value = 'monthlyDefault';
          <%
           if(functions.isValueNull(monthlyJsonStr1) && ("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))))
           {
          %>
          var monthlyCtx = document.getElementById("canvas-monthly").getContext("2d");
          window.myBar = new Chart(monthlyCtx,
                  {
                    type: 'bar',
                    data: <%=monthlyJsonStr1%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=monthlyText%>
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'Last Six Months'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksMonthly%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
         }
         %>
        }
        else
        {
          document.getElementById('monthlyDefaultTable').innerHTML='<%=monthlyTableData%>';
          document.getElementById('monthlytogglebutton').value='monthlytoggle';
          <%
              if(functions.isValueNull(monthlyJsonStr) && ("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))))
              {
          %>
          var monthlyCtx = document.getElementById("canvas-monthly").getContext("2d");
          window.myBar = new Chart(monthlyCtx,
                  {
                    type: 'bar',
                    data: <%=monthlyJsonStr%>,
                    options:
                    {
                      title:
                      {
                        display:true,
                        text:<%=monthlyText%>
                      },
                      tooltips:
                      {
                        mode: 'label'
                      },
                      responsive: true,
                      scales:
                      {
                        xAxes: [{
                          stacked: true,
                          display: true,
                          scaleLabel: {
                            display: true,
                            labelString: 'Last Six Months'
                          }
                        }],
                        yAxes: [{
                          stacked: true,
                          display: true,
                          <%=ticksMonthly%>
                          scaleLabel: {
                            display: true,
                            labelString: '<%=value%>'
                          }
                        }]
                      }
                    }
                  });
          <%
          }
          %>
        }
      }
    </script>
    <%
      }
      else
      {
        out.println(Functions.NewShowConfirmation("Sorry","No Data Found."));
      }
    %>
  </div>
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
