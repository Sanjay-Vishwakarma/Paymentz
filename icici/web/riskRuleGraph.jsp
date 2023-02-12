<%@ page import="com.directi.pg.Database" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%!
  private static Logger log = new Logger("merchantTerminalThreshold.jsp");
%>
<script src="/icici/javascript/Chart.bundle.js"></script>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<html>
<head>
  <style>
    canvas {
      -moz-user-select: none;
      -webkit-user-select: none;
      -ms-user-select: none;
    }
  </style>
  <%--<script type="text/javascript">
    function selectTerminals()
    {
      document.f1.action="/icici/riskRuleGraph.jsp?ctoken=<%=ctoken%>";
      document.f1.submit();
    }
  </script>--%>

</head>
<title> Merchants Management > Risk Rule Graph </title>
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
          String memberId = nullToStr(request.getParameter("memberid"));
          String frequency=request.getParameter("frequency");
          String terminalId = nullToStr(request.getParameter("terminalid"));
          String ruleid= nullToStr(request.getParameter("ruleid"));

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
                  <td width="20%" class="textb" for="midy">Member Id*</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input name="memberid" id="midy" value="<%=memberId%>" class="txtbox" autocomplete="on">
                   <%-- <select name="memberid" class="txtbox" style="width: 220px" onchange="selectTerminals()">
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
                    </select>--%>
                  </td>
                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" for="tid">Terminal Id*</td>
                  <td width="5%" class="textb">
                    <input name="terminalid" id="tid" value="<%=terminalId%>" class="txtbox" autocomplete="on">
                   <%-- <select name="terminalid" class="txtbox" onchange="selectTerminals()">
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
                      <option value="<%=rs.getInt("terminalid")%>" <%=selection%>><%=rs.getInt("memberid")+"-"+rs.getString("terminalid")+"-"+GatewayAccountService.getPaymentMode(rs.getString("paymodeid")) + "-" + GatewayAccountService.getCardType(rs.getString("cardtypeid"))%></option>;
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
                  <td width="50%" class="textb">
                  </td>
                </tr>
                </td>
                </tr>
                <tr><td colspan="8">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb">Rule Name*</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input name="ruleid" id="rn" value="<%=ruleid%>" class="txtbox" autocomplete="on">
                    <%--<select name="ruleid" class="txtbox" style="width: 220px">
                      <option value="" selected></option>
                      <%
                        Connection conn=null;
                        PreparedStatement pstmt=null;
                        ResultSet rs=null;
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
                    </select>--%>
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
    double maxRatioCompositeHoursly = 0.00;
    double maxRatioCompositeDaily = 0.00;
    double maxRatioCompositeWeekly = 0.00;
    double maxRatioCompositeMonthly = 0.00;

    String dailyJsonStr = (String)request.getAttribute("dailyJsonStr");
    String weeklyJsonStr = (String)request.getAttribute("weeklyJsonStr");
    String weeklyNewJsonStr = (String)request.getAttribute("weeklyNewJsonStr");
    String monthlyJsonStr = (String)request.getAttribute("monthlyJsonStr");
    String dailyTableData = (String)request.getAttribute("dailyTableData");
    String weeklyTableData = (String)request.getAttribute("weeklyTableData");
    String weeklyNewTableData = (String)request.getAttribute("weeklyNewTableData");
    String monthlyTableData = (String)request.getAttribute("monthlyTableData");
    String displayRuleName = (String)request.getAttribute("displayRuleName");
    String monitoringUnit = (String)request.getAttribute("monitoringUnit");

    if(request.getAttribute("maxRatioCompositeHoursly")!=null)
    {
      maxRatioCompositeHoursly = (Double) request.getAttribute("maxRatioCompositeHoursly");
    }
    if(request.getAttribute("maxRatioCompositeDaily")!=null)
    {
      maxRatioCompositeDaily = (Double) request.getAttribute("maxRatioCompositeDaily");
    }
    if(request.getAttribute("maxRatioCompositeWeekly")!=null)
    {
      maxRatioCompositeWeekly = (Double) request.getAttribute("maxRatioCompositeWeekly");
    }
    if(request.getAttribute("maxRatioCompositeMonthly")!=null)
    {
      maxRatioCompositeMonthly = (Double) request.getAttribute("maxRatioCompositeMonthly");
    }

    String ticksHoursly = "";
    String ticksDaily = "";
    String ticksWeekly = "";
    String ticksMonthly = "";
    if(maxRatioCompositeHoursly > 5)
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
    if(maxRatioCompositeDaily > 5)
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
    if(maxRatioCompositeWeekly > 5)
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
    if(maxRatioCompositeMonthly > 5)
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
    }

    if(functions.isValueNull(dailyJsonStr) || functions.isValueNull(weeklyJsonStr) || functions.isValueNull(monthlyJsonStr) || functions.isValueNull(weeklyNewJsonStr))
    {
  %>
  <div style="width:100%;" >
    <%
      if(functions.isValueNull(dailyJsonStr) && ("all".equals(request.getParameter("frequency")) || "hoursly".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <tr>
        <td width="50%" align="center">
          <%=dailyTableData%>
        </td>
        <td width="50%">
          <canvas id="canvas1"></canvas>
        </td>
      </tr>
    </table>
    <%
      }
      if(functions.isValueNull(weeklyJsonStr) && ("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <tr>
        <td width="50%" align="center">
          <%=weeklyTableData%>
        </td>
        <td width="50%">
          <canvas id="canvas2"></canvas>
        </td>
      </tr>
    </table>
    <%
      }
      if(functions.isValueNull(weeklyNewJsonStr) && ("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <tr>
        <td width="50%" align="center">
          <%=weeklyNewTableData%>
        </td>
        <td width="50%">
          <canvas id="canvas3"></canvas>
        </td>
      </tr>
    </table>
    <%
      }
      if(functions.isValueNull(monthlyJsonStr) && ("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <tr>
        <td width="50%" align="center">
          <%=monthlyTableData%>
        </td>
        <td width="50%">
          <canvas id="canvas4"></canvas>
        </td>
      </tr>
    </table>
    <br>
    <br>
    <%
      }
    %>
    <%
      String hourslyText;
      String dailyText;
      String weeklyText;
      String monthlyText;

      hourslyText = displayRuleName+" [today]";
      dailyText = displayRuleName+" [in current week]";
      weeklyText = displayRuleName+" [in current month]";
      monthlyText = displayRuleName+" [last six months]";
    %>
    <script>
      window.onload = function()
      {<%
        if(functions.isValueNull(dailyJsonStr) && ("all".equals(request.getParameter("frequency")) ||  "hoursly".equals(request.getParameter("frequency"))))
        {
        %>
        var ctx1 = document.getElementById("canvas1").getContext("2d");
        window.myLine = new Chart(ctx1,
                {
                  type:'line',
                  data:<%=dailyJsonStr%>,
                  options:
                  {
                    responsive: true,
                    legend: {
                      position: 'bottom'
                    },
                    hover: {
                      mode: 'label'
                    },
                    scales: {
                      xAxes: [{
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'Time (Hrs)'
                        }
                      }],
                      yAxes: [{
                        display: true,
                        <%=ticksHoursly%>
                        scaleLabel: {
                          display: true,
                          labelString: 'Value (<%=monitoringUnit%>)'
                        }
                      }]
                    },
                    title:
                    {
                      display: true,
                      text: '<%=hourslyText%>'
                    }
                  }
                });
        <%
        }
        if(functions.isValueNull(weeklyJsonStr) && ("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))))
        {
        %>
        var ctx2 = document.getElementById("canvas2").getContext("2d");
        window.myLine = new Chart(ctx2,
                {
                  type:'line',
                  data:<%=weeklyJsonStr%>,
                  options:
                  {
                    responsive: true,
                    legend: {
                      position: 'bottom'
                    },
                    hover: {
                      mode: 'label'
                    },
                    scales: {
                      xAxes: [{
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'Week Days'
                        }
                      }],
                      yAxes: [{
                        display: true,
                        <%=ticksDaily%>
                        scaleLabel: {
                          display: true,
                          labelString: 'Value (<%=monitoringUnit%>)'
                        }
                      }]
                    },
                    title:
                    {
                      display: true,
                      text: '<%=dailyText%>'
                    }
                  }
                });
        <%
       }
       if(functions.isValueNull(weeklyNewJsonStr) && ("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))))
       {
        %>
        var ctx3 = document.getElementById("canvas3").getContext("2d");
        window.myLine = new Chart(ctx3,
                {
                  type:'line',
                  data:<%=weeklyNewJsonStr%>,
                  options:
                  {
                    responsive: true,
                    legend: {
                      position: 'bottom'
                    },
                    hover: {
                      mode: 'label'
                    },
                    scales: {
                      xAxes: [{
                        display: true,
                        <%=ticksWeekly%>
                        scaleLabel: {
                          display: true,
                          labelString: 'Month Weeks'
                        }
                      }],
                      yAxes: [{
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'Value (<%=monitoringUnit%>)'
                        }
                      }]
                    },
                    title:
                    {
                      display: true,
                      text: '<%=weeklyText%>'
                    }
                  }
                });
        <%
       }
       if(functions.isValueNull(monthlyJsonStr) && ("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))))
       {
        %>
        var ctx4 = document.getElementById("canvas4").getContext("2d");
        window.myLine = new Chart(ctx4,
                {
                  type:'line',
                  data:<%=monthlyJsonStr%>,
                  options:
                  {
                    responsive: true,
                    legend: {
                      position: 'bottom'
                    },
                    hover: {
                      mode: 'label'
                    },
                    scales: {
                      xAxes: [{
                        display: true,
                        scaleLabel: {
                          display: true,
                          labelString: 'Last Six Months'
                        }
                      }],
                      yAxes: [{
                        display: true,
                        <%=ticksMonthly%>
                        scaleLabel: {
                          display: true,
                          labelString: 'Value (<%=monitoringUnit%>)'
                        }
                      }]
                    },
                    title:
                    {
                      display: true,
                      text: '<%=monthlyText%>'
                    }
                  }
                });
        <%
       }
       %>
      };
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

<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>