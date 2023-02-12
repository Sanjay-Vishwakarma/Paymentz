<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
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
      document.f1.action="/icici/riskRuleDoughnutChart.jsp?ctoken=<%=ctoken%>";
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
    String dailyJsonStr=(String)request.getAttribute("dailyJsonStr");
    String weeklyJsonStr=(String)request.getAttribute("weeklyJsonStr");
    String monthlyJsonStr=(String)request.getAttribute("monthlyJsonStr");

    //System.out.println("dailyJsonStr======"+dailyJsonStr);
    //System.out.println("weeklyJsonStr======"+weeklyJsonStr);
    //System.out.println("monthlyJsonStr======"+monthlyJsonStr);

    String dailyTableData=(String)request.getAttribute("dailyTableData");
    String weeklyTableData=(String)request.getAttribute("weeklyTableData");
    String monthlyTableData=(String)request.getAttribute("monthlyTableData");

    //System.out.println("dailyTableData======"+dailyTableData);
    //System.out.println("weeklyTableData======"+weeklyTableData);
    //System.out.println("monthlyTableData======"+monthlyTableData);

    String displayRuleName=(String)request.getAttribute("displayRuleName");
    //System.out.println("displayRuleName======"+displayRuleName);

    if(functions.isValueNull(dailyJsonStr) || functions.isValueNull(weeklyJsonStr) || functions.isValueNull(monthlyJsonStr))
    {
  %>
  <div style="width:100%;" >
    <%
      if(functions.isValueNull(dailyJsonStr) && ("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <tr>
        <td width="50%" align="center">
          <%=dailyTableData%>
        </td>
        <td width="50%">
          <canvas id="chart-area-daily" />
        </td>
      </tr>
    </table>
    <%
      }
      if(functions.isValueNull(weeklyJsonStr) && ("all".equals(request.getParameter("frequency")) || "weekly".equals(request.getParameter("frequency"))))
      {
    %>
    <table border="1" style="width:100%; height:70px;" >
      <tr>
        <td width="50%" align="center">
          <%=weeklyTableData%>
        </td>
        <td width="50%">
          <canvas id="chart-area-weekly" />
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
          <canvas id="chart-area-monthly" />
        </td>
      </tr>
    </table>
    <br>
    <br>
    <%
      }
    %>
    <%
      String dailyText;
      String weeklyText;
      String monthlyText;

      dailyText = displayRuleName+" [today]";
      dailyText="'"+dailyText+"'";
      weeklyText = displayRuleName+" [in current week]";
      weeklyText="'"+weeklyText+"'";
      monthlyText = displayRuleName+" [in current month]";
      monthlyText="'"+monthlyText+"'";

    %>
    <script>
      window.onload = function()
      {
        <%
         if(functions.isValueNull(dailyJsonStr) && ("all".equals(request.getParameter("frequency")) || "daily".equals(request.getParameter("frequency"))))
         {
         %>
        var dailyctx = document.getElementById("chart-area-daily").getContext("2d");
        window.myDoughnut = new Chart(dailyctx,
                {
                  type: 'doughnut',
                  data: {<%=dailyJsonStr%>},
                  options:
                  {
                    responsive: true,
                    legend:
                    {
                      position: 'top'
                    },
                    title:
                    {
                      display:true,
                      text:<%=dailyText%>
                    },
                    animation: {
                      animateScale: true,
                      animateRotate: true
                    }
                  }
                });
        <%
         }
         if(functions.isValueNull(weeklyJsonStr) && ("all".equals(request.getParameter("frequency"))  || "weekly".equals(request.getParameter("frequency"))))
         {
         %>
        var weeklyctx = document.getElementById("chart-area-weekly").getContext("2d");
        window.myDoughnut = new Chart(weeklyctx,
                {
                  type: 'doughnut',
                  data: {<%=weeklyJsonStr%>},
                  options:
                  {
                    responsive: true,
                    legend:
                    {
                      position: 'top'
                    },
                    title:
                    {
                      display:true,
                      text:<%=weeklyText%>
                    },
                    animation: {
                      animateScale: true,
                      animateRotate: true
                    }
                  }
                });
        <%
            }
            if(functions.isValueNull(monthlyJsonStr) && ("all".equals(request.getParameter("frequency")) || "monthly".equals(request.getParameter("frequency"))))
            {
        %>
        var monthlyctx = document.getElementById("chart-area-monthly").getContext("2d");
        window.myDoughnut = new Chart(monthlyctx,
                {
                  type: 'doughnut',
                  data: {<%=monthlyJsonStr%>},
                  options:
                  {
                    responsive: true,
                    legend:
                    {
                      position: 'top'
                    },
                    title:
                    {
                      display:true,
                      text:<%=monthlyText%>
                    },
                    animation: {
                      animateScale: true,
                      animateRotate: true
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