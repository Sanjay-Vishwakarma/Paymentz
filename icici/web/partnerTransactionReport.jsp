<%@ page import="com.directi.pg.Database,
                 com.directi.pg.Functions "%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 5/3/2017
  Time: 11:57 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <%-- <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>

  <script type="text/javascript">
    $('#sandbox-container input').datepicker({

    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker();

    });
  </script>
  <title> Reports> Partner Transaction Report</title>
</head>
<body>
<%
  Logger logger = new Logger("partnerTransactionReport.jsp");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    TerminalManager terminalManager = new TerminalManager();
    Functions functions = new Functions();
    Hashtable statushash = new Hashtable();
    String pNameLower = "";
    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
    String style="class=\"tr0\"";

    String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String merchantid = Functions.checkStringNull(request.getParameter("merchantid"));
    String memberid = request.getParameter("merchantid");
    String pgtypeid = Functions.checkStringNull(request.getParameter("gateway"))==null?"":request.getParameter("gateway");
    String startDate = request.getParameter("startdate");
    String startTime = request.getParameter("starttime");
    String endDate = request.getParameter("enddate");
    String endTime = request.getParameter("endtime");
    String partnerName = (String) request.getAttribute("partnerName");

    statushash.put("begun", "Begun Processing");
    statushash.put("authstarted", "Auth Started");
    statushash.put("payoutstarted", "Payout Started");
    statushash.put("proofrequired", "Proof Required");
    statushash.put("authsuccessful", "Auth Successful");
    statushash.put("payoutsuccessful", "Payout Successful");
    statushash.put("authfailed", "Auth Failed");
    statushash.put("payoutfailed", "Payout Failed");
    statushash.put("capturestarted", "Capture Started");
    statushash.put("capturesuccess", "Capture Successful");
    statushash.put("capturefailed", "Capture Failed");
    statushash.put("podsent", "POD Sent ");
    statushash.put("settled", "Settled");
    statushash.put("markedforreversal", "Reversal Request Sent");
    statushash.put("reversed", "Reversed");
    statushash.put("chargeback", "Chargeback");
    statushash.put("failed", "Cancelled by Customer");
    statushash.put("cancelstarted", "Cancel Strarted");
    statushash.put("cancelled", "Cancelled Transaction");
    statushash.put("authcancelled", "Authorisation Cancelled");
    statushash.put("cancelstarted", "Cancel Started");
    statushash.put("authstarted_3D", "Auth Started_3D");
    statushash.put("partialrefund", "Partial Refund");
    statushash.put("payoutcancelfailed", "Payout Cancel Failed");
    statushash.put("payoutcancelsuccessful", "Payout Cancel Successful");


    String errorMsg =(String) request.getAttribute("errormsg");
    if (gateway == null)
    {
      gateway = "";
    }
    if (accountid== null)
    {
      accountid = "";
    }
    if(merchantid==null)
    {
      merchantid="";
    }

    TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
    TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();

    MerchantDAO merchantDAO = new MerchantDAO();
    LinkedHashMap<Integer,String> memberMap = merchantDAO.listAllMember();
%>

<form action="/icici/servlet/PartnerTransactionReport?ctoken=<%=ctoken%>" method="post" name="forms" >
  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">

  <div class="row" style="margin-left: 210px;">
    <div class="col-lg-12">
      <div class="panel panel-default" style="margin-left:0px">
        <div class="panel-heading" >
          Partner Transaction Report
        </div>
        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
        <table align=center>
          <%
            if(errorMsg != null) {
          %>
          <tr>
            <td colspan="10" align="center" class="textb"><%=errorMsg%></td>
          </tr>
          <%}%>
        </table>

        <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:0%;">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >From *</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input type="text" readonly class="datepicker" name="startdate" style="width:142px;height: 25px"  value="<%=request.getParameter("startdate")==null?"":request.getParameter("startdate")%>">

                  </td>
                  <td width="4%" class="textb">&nbsp;&nbsp;&nbsp;</td>
                  <td width="8%" class="textb" >Start Time(HH:MM:SS)</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input type="text" name="starttime" value="<%=request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime")%>" class="txtbox">

                  </td>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">To *</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input type="text" readonly class="datepicker" name="enddate"  style="width:142px;height: 25px"value="<%=request.getParameter("enddate")==null?"":request.getParameter("enddate")%>">

                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >End Time(HH:MM:SS)</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input type="text"   name="endtime" value="<%=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime")%>" class="txtbox">
                  </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>
                <tr>
                  <td width="6%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Partner Id *</td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <select name="partnerName" class="txtbox">
                      <option value="" selected>---Select Partner ID---</option>
                      <%
                        Connection conn=null;
                        try
                        {
                          conn = Database.getConnection();
                          String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                          PreparedStatement pstmt = conn.prepareStatement(query);
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selected = "";
                            if((rs.getString("partnerName")).equals(partnerName))
                            {
                              selected = "selected";
                            }
                            else
                            {
                              selected = "";
                            }
                      %>
                      <option value="<%=rs.getInt("partnerId") +"-"+ rs.getString("partnerName")%>" <%=selected%>><%=rs.getInt("partnerId")+"-"+rs.getString("partnerName")%></option>;
                      <%
                          }
                        }
                        catch(SQLException e)
                        {

                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Gateway *</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input name="gateway" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                    <%--<select size="1" id="bank" name="gateway" class="txtbox">
                        <option value="0" default>--All--</option>
                        <%
                          for(String gatewayType : gatewayTypeTreeMap.keySet())
                          {
                            String isSelected = "";
                            if(gatewayType.equalsIgnoreCase(pgtypeid))
                            {
                              isSelected = "selected";
                            }
                        %>
                        <option value="<%=gatewayType%>" <%=isSelected%>><%=gatewayType%></option>
                        <%
                          }
                        %>
                      </select>--%>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Account ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb" >
                    <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                  <%--<select size="1" id="accountid" name="accountid" class="txtbox">
                      <option data-bank="all" value="0">---Select AccountID---</option>
                      <%
                        for(Integer sAccid : accountDetails.keySet())
                        {
                          GatewayAccount g = accountDetails.get(sAccid);
                          String isSelected = "";
                          String gateway2 = g.getGateway().toUpperCase();
                          String currency2 = g.getCurrency();
                          String pgtype = g.getPgTypeId();
                          if (String.valueOf(sAccid).equals(accountid))
                            isSelected = "selected";
                          else
                            isSelected = "";
                      %>
                      <option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                      <%
                        }
                      %>
                    </select>--%>
                  </td>

                  <%--<td width="4%" class="textb">&nbsp;</td>
                  <td colspan="2" class="textb">Member ID</td>
                  <td colspan="2" class="textb" >
                    <select name="merchantid" id="memberid" class="txtbox">
                      <option data-bank="all"  data-accid="all" value="" selected>--Select MemberID--</option>
                      <%
                        for (Integer mId : memberMap.keySet())
                        {
                          String companyName = memberMap.get(mId);
                          String isSelected = "";
                          if (String.valueOf(mId).equalsIgnoreCase(memberid))
                          {
                            isSelected = "selected";
                          }
                      %>
                      <option value="<%=mId%>"<%=isSelected%>><%=mId+"-"+companyName%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>--%>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb"></td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <%--<td width="12%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>--%>

                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
                <tr>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb"></td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb"></td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                  </td>

                </tr>
              </table>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</form>
<div class="reporttable">
  <%
    Map<String, List<TransactionVO>> transactionVOMap = (Map<String, List<TransactionVO>>) request.getAttribute("transactionVOMap");
    Map<String, List<TransactionVO>> transactionVORefundMap = (Map<String, List<TransactionVO>>) request.getAttribute("transactionVORefundMap");
    Set<String> gatewaySet = (Set) request.getAttribute("gatewaySet");

    logger.debug("gatewaySet===="+gatewaySet);
    logger.debug("transactionVOMap===="+transactionVOMap);
    logger.debug("transactionVORefundMap===="+transactionVORefundMap);

    if((transactionVOMap != null && transactionVOMap.size() > 0) || (transactionVORefundMap != null && transactionVORefundMap.size()>0))
    {
  %>
  <form name="exportform" method="post" action="/icici/servlet/ExportExcelForPartnerTransactions?ctoken=<%=ctoken%>" >
    <input type="hidden" value="<%=startDate%>" name="startDate">
    <input type="hidden" value="<%=endDate%>" name="endDate">
    <input type="hidden" value="<%=startTime%>" name="startTime">
    <input type="hidden" value="<%=endTime%>" name="endTime">
    <input type="hidden" value="<%=partnerName%>" name="partnerName">
    <input type="hidden" value="<%=gateway%>" name="gateway">
    <input type="hidden" value="<%=accountid%>" name="accountId">
    <input type="hidden" value="<%=memberid%>" name="memberId">
    <%
      session.setAttribute("gatewaySet", gatewaySet);
      session.setAttribute("statushash", statushash);
    %>
    <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
  </form>

  <%
    for (String gatewayName : gatewaySet)
    {
      logger.debug("gatewayName===="+gatewayName);
      int pos = 1;
      if (transactionVOMap != null && transactionVOMap.size()>0)
      {
        List<TransactionVO> transactionVOList = transactionVOMap.get(gatewayName);
        logger.debug("transactionVOList===="+transactionVOList);
        if (transactionVOList != null)
        {
  %>
  <table align="center" border="2" class="table table-striped table-bordered table-green dataTable">
    <tr>
      <td align="center" class="textb" style="border: 4px solid #000;";>
        <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center" class="table table-striped table-bordered table-hover table-green dataTable"  >
          <tr>
            <td  class="textb" colspan="5"><center><b> <%=gatewayName%> </b></center></td>
          </tr>
          <tr>
            <td  class="textb" colspan="5"><center><b><%=partnerName%> Transactions Received (<%=startDate%> - <%=endDate%>)</center></td>
          </tr>
          <tr <%=style%>>
            <td  class="th0" >Status</td>
            <td  class="th0" >No. Of Transaction</td>
            <td  class="th0" >Amount</td>
            <td  class="th0" >Capture Amount</td>
            <td  class="th0" >Percentage(%)</td>
          </tr>
          <%
                for (TransactionVO transactionVO : transactionVOList)
                {
                  if (pos % 2 == 0)
                  {
                    style = "class=tr0";
                  }
                  else
                  {
                    style = "class=tr1";
                  }

                  out.println("<tr  " + style + ">");
                  out.println("<td align=\"center\" >" + statushash.get(transactionVO.getStatus()) + " </td>");
                  out.println("<td align=\"center\" >" + transactionVO.getCount() + " </td>");
                  out.println("<td align=\"center\" >" + transactionVO.getAmount() + " </td>");
                  out.println("<td align=\"center\" >" + transactionVO.getCaptureAmount() + " </td>");
                  out.println("<td align=\"center\" >" + Functions.round(transactionVO.getCount() / Double.parseDouble(transactionVO.getTotalTransCount()) * 100, 2) + " </td>");
                  out.println("</tr>");
                  pos++;
                }
              }
            }
          %>
        </table>
        <%
          int pos1 = 1;
          if (transactionVORefundMap != null && transactionVORefundMap.size()>0)
          {
            List<TransactionVO> transactionVORefundList = transactionVORefundMap.get(gatewayName);
            logger.debug("transactionVORefundList===="+transactionVORefundList);
            if (transactionVORefundList != null)
            {
        %>
        <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center" class="table table-striped table-bordered table-hover table-green dataTable" >
          <tr>
            <td  class="textb" colspan="6"><center><b><%=partnerName%> Refund and Chargeback Reeceived (<%=startDate%> - <%=endDate%>)</b></center></td>
          </tr>
          <tr <%=style%>>
            <td  class="th0" >Status</td>
            <td  class="th0" >No. Of Transaction</td>
            <td  class="th0" >Amount</td>
            <td  class="th0" >Refund Amount</td>
            <td  class="th0" >Chargeback Amount</td>
            <td  class="th0" >Percentage(%)</td>
          </tr>
          <%
                for (TransactionVO transactionVO : transactionVORefundList)
                {
                  if (pos1 % 2 == 0)
                  {
                    style = "class=tr0";
                  }
                  else
                  {
                    style = "class=tr1";
                  }

                  out.println("<tr  " + style + ">");
                  out.println("<td align=\"center\" >" + statushash.get(transactionVO.getStatus()) + " </td>");
                  out.println("<td align=\"center\" >" + transactionVO.getCount() + " </td>");
                  out.println("<td align=\"center\" >" + transactionVO.getAmount() + " </td>");
                  out.println("<td align=\"center\" >" + transactionVO.getRefundAmount() + " </td>");
                  out.println("<td align=\"center\" >" + transactionVO.getChargebackAmount() + " </td>");
                  out.println("<td align=\"center\" >" + Functions.round(transactionVO.getCount()/ Double.parseDouble(transactionVO.getTotalTransCount())*100,2) + " </td>");
                  out.println("</tr>");
                  pos1++;
                }
              }
            }
          %>
        </table>
      </td>
    </tr>
  </table>
  <%
      }
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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
