<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeMasterVO" %>
<%@ page import="com.manager.vo.AgentCommissionVO" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/26/14
  Time: 3:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Agent Commission</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd',endDate:'+10y'});
    });
  </script>
</head>
<body>
<%!
  private static Logger logger=new Logger("actionAgentCommission.jsp");
  private static CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
%>
<%
  String memberid=nullToStr(request.getParameter("memberid"));
  String agentid=nullToStr(request.getParameter("agentid"));
  String terminalid=nullToStr(request.getParameter("terminalid"));
  String chargeName = request.getParameter("chargeid") == null ? "" : request.getParameter("chargeid");
  List<AgentCommissionVO> chargeNameList = PayoutManager.loadchargenameAgent();

  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (!com.directi.pg.Admin.isLoggedIn(session))
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Agent Commission
        <div style="float: right;">
          <form action="/icici/manageAgentCommission.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Agent Commission" name="submit" style="width:350px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Agent Commission
            </button>
          </form>
        </div>
      </div><br>
      <form action="/icici/servlet/ListAgentCommission?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" >
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Agent Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="agentid" id="agnt" value="<%=agentid%>" class="txtbox" autocomplete="on">
                   <%-- <select name="agentid" class="txtbox"><option value="" selected></option>
                      <%
                        Connection conn = Database.getConnection();
                        String query = "SELECT agentId,agentName FROM agents WHERE activation='T' ORDER BY agentId ASC";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                          out.println("<option value=\""+rs.getInt("agentId")+"\">"+rs.getInt("agentId")+" - "+rs.getString("agentName")+"</option>");
                        }
                      %>
                    </select>--%>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" for="mid">Member Id</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="memberid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                    <%--<select name="memberid" class="txtbox"><option value="" selected></option>
                      <%
                        conn= Database.getConnection();
                        query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                        pstmt = conn.prepareStatement( query );
                        rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                          out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" for="tid">Terminal Id:</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="terminalid" id="tid3" value="<%=terminalid%>" class="txtbox" autocomplete="on">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Commission Name:</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select size="1" class="txtbox" name="chargeid">
                      <option value="" selected>Select Commission Name</option>
                      <%
                        for (AgentCommissionVO commissionVO : chargeNameList)
                        {
                          String isSelected = "";
                          if (commissionVO.getChargeId().equalsIgnoreCase(chargeName))
                            isSelected = "selected";
                          else
                            isSelected = "";
                      %>
                      <option value="<%=commissionVO.getChargeId()%>" <%=isSelected%>><%=commissionVO.getChargeName()%>
                      </option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td colspan="6">&nbsp;</td>
                </tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                  </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%
    String error = (String) request.getAttribute("errormessage");
    AgentCommissionVO agentCommissionVO=(AgentCommissionVO)request.getAttribute("agentCommissionVO");
    AgentCommissionVO agentCommissionVO1=(AgentCommissionVO)request.getAttribute("agentCommissionVO1");

    String action = (String) request.getAttribute("action");
    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (agentCommissionVO1!=null)
    {
    if ("history".equalsIgnoreCase(action))
    {
     %>
        <table align=center class="table table-striped table-bordered table-green dataTable">
        <tr>
        <td valign="middle" align="center" class="th0" >Mapping ID</td>
        <td valign="middle" align="center" class="th0" >Agent ID</td>
        <td valign="middle" align="center" class="th0" >Member ID</td>
        <td valign="middle" align="center" class="th0" >Terminal ID</td>
        <td valign="middle" align="center" class="th0" >Charge ID</td>
        <td valign="middle" align="center" class="th0" >Commmission Value</td>
        <td valign="middle" align="center" class="th0" >Start Date</td>
        <td valign="middle" align="center" class="th0" >End Date</td>
          <td valign="middle" align="center" class="th0" >Sequence Number</td>

        </tr>
  <%

      String style = "class=td1";
      out.println("<tr>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getCommissionId()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getAgentId()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getMemberId()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getTerminalId()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getChargeId()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getChargeValue()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getStartDate()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getEndDate()) + "</td>");
      out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML(agentCommissionVO1.getSequenceNo()) + "</td>");
      out.println("</tr>");
    }

  %>
  </table>
<%
  }

    if (agentCommissionVO != null)
    {
      if ("modify".equalsIgnoreCase(action))
      {
      ChargeMasterVO chargeMasterVO=agentCommissionVO.getChargeMasterVO();
      String style = "class=tr0";
  %> <form action="/icici/servlet/UpdateAgentCommission?ctoken=<%=ctoken%>" method="post" name="forms" >
  <input type="hidden" name="commissionid" value="<%=agentCommissionVO.getCommissionId()%>">
  <table align=center class="table table-striped table-bordered table-green dataTable" style="width:50% ">
    <tr <%=style%>>
      <td class="th0" colspan="2"><b>Update Commission</b></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Agent Id : </td>
      <td class="tr1"><input type="text" class="txtbox1"  size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(agentCommissionVO.getAgentId())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Member Id : </td>
      <td class="tr1"><input type="text" class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(agentCommissionVO.getMemberId())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Terminal Id : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(agentCommissionVO.getTerminalId())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Commission Name : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(chargeMasterVO.getChargeName())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Commission Value : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="commissionvalue" value="<%=Functions.round(agentCommissionVO.getCommissionValue(),2)%>"></td>
    </tr>
  <%--  <tr <%=style%>>
      <td class="tr1">Start Date : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="50" name="startdate" value="<%=targetFormat.format(targetFormat.parse(agentCommissionVO.getStartDate()))%>"></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">End Date : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="50" name="enddate" value="<%=targetFormat.format(targetFormat.parse(agentCommissionVO.getEndDate()))%>"></td>
    </tr>--%>
    <tr <%=style%>>
      <td class="tr1">Start Date : </td>
      <td class="tr1">
        <input type="text"  size="30" readonly class="datepicker" name="startdate" value="<%=commonFunctionUtil.convertTimestampToDatepicker(/*getPreviousDate(*/agentCommissionVO.getStartDate())/*)*/%>">
      </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">End Date : </td>
      <td class="tr1">
        <input type="text"  size="30" readonly class="datepicker" name="enddate" value="<%=commonFunctionUtil.convertTimestampToDatepicker(agentCommissionVO.getEndDate())%>">
      </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Sequence No : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(agentCommissionVO.getSequenceNo())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="td1"></td>
      <td class="td1"><input type="submit" align="center" class="button" value="Update"></td>
    </tr>
  </table>
</form>
  <%
      }
      }
      else
      {
        out.println(Functions.NewShowConfirmation(error!=null?"Result":"Sorry",error!=null?error:"No Records Found."));
      }

  %>
</div>
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
<%!
  public String getPreviousDate(String source)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date2 = null;
    try
    {
      date2 = sdf.parse(source);
    }
    catch (ParseException e)
    {
      logger.error("Parse Exception while  getting  PreviousDate",e);
    }
    Date dateBefore = new Date(date2.getTime() + 1 * 24 * 3600 * 1000 );
    String sDate2=sdf.format(dateBefore);
    return sDate2;
  }
%>
