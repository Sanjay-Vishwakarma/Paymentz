<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.directi.pg.Merchants" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.manager.vo.MerchantDetailsVO"%>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.CardTypeAmountVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MerchantRiskParameterVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.TerminalProcessingDetailsVO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%!
  private static Logger log=new Logger("merchantriskmanagement.jsp");
%>
<html>
<head>
</head>
<title> Merchant Monitoring</title>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
         Merchant Monitoring
      </div>
      <%
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        Merchants merchants = new Merchants();
        if (merchants.isLoggedIn(session))
        {
      %>
      <form action="/icici/servlet/MerchantRiskManagement?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <br>
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <%
                String errormsg1 = (String) request.getAttribute("error");
                if (errormsg1 != null)
                {
                  out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
                }

                String errormsg = (String)request.getAttribute("cbmessage");
                if (errormsg == null)
                  errormsg = "";

                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                out.println(errormsg);
                out.println("</b></font></td></tr></table>");
              %>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" >Member Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input name="memberid" size="10" class="txtbox">
                  </td>
                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" ></td>
                  <td width="5%" class="textb"></td>
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
<form action="/icici/servlet/SetReserves?ctoken=<%=ctoken%>" method=post>
  <div class="reporttable" style="margin-bottom: 9px">
    <%
      HashMap<String,TerminalProcessingDetailsVO> processingDetailsVOHashMap=(HashMap)request.getAttribute("processingDetailsVOHashMap");
      MerchantDetailsVO merchantDetailsVO=(MerchantDetailsVO)request.getAttribute("merchantDetailsVO");
      HashMap<String,CardTypeAmountVO> cardTypeAmountVOHashMap=(HashMap)request.getAttribute("currencyAmount");
      HashMap<String,CardTypeAmountVO> cardTypeAmountVOHashMap1=(HashMap)request.getAttribute("currentmonthcurrencyamount");
      SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Functions functions=new Functions();
      if (merchantDetailsVO!=null && functions.isValueNull(merchantDetailsVO.getMemberId()))
      {
    %>
    <table align=center class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <%
        String style = "tr0";
      %>
      <thead>
      <tr class="th0">
        <td colspan="1" align="center"  style="border-left-style: hidden"><b style="align:center"></b></td>
        <td colspan="1" align="center;" style="border-left-style: hidden"><b></b></td>
        <td colspan="1" align="center;" style="border-left-style: hidden">Merchant Details</td>
        <td colspan="1" align="center;" style="border-left-style: hidden"><b></b></td>
        <td colspan="7" align="center;" style="border-left-style: hidden"><b></b></td>
        <%--<td colspan="7" align="left;" style="border-left-style: hidden">Current Month End Date-</td>--%>
        <%--<td colspan="1" align="center;" style="border-left-style: hidden"></td>--%>
      </tr>
     </thead>
      <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Merchant ID</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Company Name</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Contact Name</td>
        <td  style="height: 30px"valign="middle"  align="center"  class="tr1">Contact No</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Partner Name</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getMemberId()%></td>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getCompany_name()%></td>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getContact_persons()%></td>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getContactNumber()%></td>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getPartnerName()%></td>
      </tr>
      <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Country</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Registration Date</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Activation Status</td>
        <td  style="height: 30px"valign="middle"  align="center"  class="tr1">BackOffice Access</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >IsPharma</td>
      </tr>
      </thead>
      <tr>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getCountry()%></td>
        <td align="center" class="<%=style%>"><%=simpleDateFormat.format(simpleDateFormat.parse(merchantDetailsVO.getRegistrationDate()))%></td>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getActivation()%></td>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getIsBackOfficeAccess()%></td>
        <td align="center" class="<%=style%>"><%=merchantDetailsVO.getIsPharma()%></td>
      </tr>
      <thead>
      <tr class="th0">
        <td colspan="1" align="center"  style="border-left-style: hidden"><b style="align:center"></b></td>
        <td colspan="1" align="center;" style="border-left-style: hidden"><b></b></td>
        <td colspan="1" align="center;" style="border-left-style: hidden">Total Sales Amount</td>
        <td colspan="1" align="center;" style="border-left-style: hidden"><b></b></td>
        <td colspan="7" align="center;" style="border-left-style: hidden"><b></b></td>
        <%--<td colspan="7" align="left;" style="border-left-style: hidden">Current Month End Date-</td>--%>
        <%--<td colspan="1" align="center;" style="border-left-style: hidden"></td>--%>
      </tr>
      <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>Currency\CardType</b></td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>Master Card</b></td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>VISA</b></td>
        <td  style="height: 30px"valign="middle"  align="center"  class="tr1"><b>AMEX</b></td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>DINERS</b></td>
      </tr>
      <%
        if(cardTypeAmountVOHashMap.size()>0)
        {
          Set set=cardTypeAmountVOHashMap.keySet();
          Iterator iterator=set.iterator();
          while (iterator.hasNext())
          {
            String currency=(String)iterator.next();
            CardTypeAmountVO cardTypeAmountVO=cardTypeAmountVOHashMap.get(currency);
      %>
      <tr>
        <td align="center" class="<%=style%>"><b><%=currency%></b></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getMCAmount(), 2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getVISAAmount(), 2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getAMEXAmount(), 2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getDINRESAmount(), 2)%></td>
      </tr>
      <%
          }
        }
        else
        {

        }
      %>
      </thead>
      <thead>
      <tr class="th0">
        <td colspan="1" align="center"  style="border-left-style: hidden"><b style="align:center"></b></td>
        <td colspan="1" align="center;" style="border-left-style: hidden"><b></b></td>
        <td colspan="1" align="center;" style="border-left-style: hidden">Current Month Sales Amount</td>
        <td colspan="1" align="center;" style="border-left-style: hidden"><b></b></td>
        <td colspan="7" align="center;" style="border-left-style: hidden"><b></b></td>
        <%--<td colspan="7" align="left;" style="border-left-style: hidden">Current Month End Date-</td>--%>
        <%--<td colspan="1" align="center;" style="border-left-style: hidden"></td>--%>
      </tr>
      <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>Currency\CardType</b></td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>Master Card</b></td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>VISA</b></td>
        <td  style="height: 30px"valign="middle"  align="center"  class="tr1"><b>AMEX</b></td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" ><b>DINERS</b></td>
      </tr>
      <%
        if(cardTypeAmountVOHashMap1.size()>0)
        {
          Set set=cardTypeAmountVOHashMap1.keySet();
          Iterator iterator=set.iterator();
          while (iterator.hasNext())
          {
            String currency=(String)iterator.next();
            CardTypeAmountVO cardTypeAmountVO=cardTypeAmountVOHashMap1.get(currency);
      %>
      <tr>
        <td align="center" class="<%=style%>"><b><%=currency%></b></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getMCAmount(), 2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getVISAAmount(), 2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getAMEXAmount(), 2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(cardTypeAmountVO.getDINRESAmount(), 2)%></td>
      </tr>
      <%
          }
        }
        else
        {

        }
      %>
      </thead>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <%
      if(processingDetailsVOHashMap!=null && processingDetailsVOHashMap.size()>0)
      {
        Set set=processingDetailsVOHashMap.keySet();
        Iterator iterator=set.iterator();
        while (iterator.hasNext())
        {
          String terminalId=(String)iterator.next();
          TerminalProcessingDetailsVO processingDetailsVO=processingDetailsVOHashMap.get(terminalId);
          TerminalVO terminalVO=processingDetailsVO.getTerminalVO();
          String bankName=processingDetailsVO.getBankName();
          String currency =processingDetailsVO.getCurrency();
          String currentMonthStartDate=processingDetailsVO.getCurrentMonthStartDate();
          String currentMonthEndDate=processingDetailsVO.getCurrentMonthEndDate();
          MerchantRiskParameterVO merchantRiskParameterVO=processingDetailsVO.getRiskParameterVO();
    %>
    <table border="0" width="100%" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">
      <thead>
      <tr class="th0">
        <td colspan="1" align="center"  style="border-left-style: hidden"><b style="align:center"></b></td>
        <td colspan="1" align="center;" style="border-left-style: hidden">Bank Name-<%=bankName%></td>
        <td colspan="1" align="center;" style="border-left-style: hidden">Currency-<%=currency%></td>
        <td colspan="1" align="center;" style="border-left-style: hidden"><%=terminalVO.toString()%></td>
        <td colspan="7" align="center;" style="border-left-style: hidden">Current Month:[<%=currentMonthStartDate%>-<%=currentMonthEndDate%>]</td>
        <%--<td colspan="7" align="left;" style="border-left-style: hidden">Current Month End Date-</td>--%>
        <%--<td colspan="1" align="center;" style="border-left-style: hidden"></td>--%>
      </tr>
      </thead>
      <thead>
      <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Activation Date</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >First Transaction Date</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Last Transaction Date</td>
        <td  style="height: 30px"valign="middle"  align="center"  class="tr1">Current Month Sales Amount</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Current Month CB Amount</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Current Month RF Amount</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Current Month Decline Amount</td>
      </tr>
      </thead>
      <tr>
        <%
          if(functions.isValueNull(processingDetailsVO.getActivationDate()))
          {%>
        <td align="center" class="<%=style%>"><%=simpleDateFormat.format(simpleDateFormat.parse(processingDetailsVO.getActivationDate()))%></td>
        <%}
        else
        {%>
        <td align="center" class="<%=style%>"><%="-"%></td>
        <%}
          if(functions.isValueNull(processingDetailsVO.getFirstTransactionDate()))
          {%>
        <td align="center" class="<%=style%>"><%=simpleDateFormat.format(simpleDateFormat.parse(processingDetailsVO.getFirstTransactionDate()))%></td>
        <%}
        else
        {%>
        <td align="center" class="<%=style%>"><%="-"%></td>
        <%}
          if(functions.isValueNull(processingDetailsVO.getLastTransactionDate()))
          {%>
        <td align="center" class="<%=style%>"><%=simpleDateFormat.format(simpleDateFormat.parse(processingDetailsVO.getLastTransactionDate()))%></td>
        <%}
        else
        {%>
        <td align="center" class="<%=style%>"><%="-"%></td>
        <%}
        %>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getSalesAmount(),2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getChargebackAmount(),2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getRefundAmount(), 2)%>
        </td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getDeclinedAmount(), 2)%></td>
      </tr>
      <thead>
      <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >First Submission(Month:Days)</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >InActivity Period(Month:Days)</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Total Sales Amount</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Current Month Approval Ratio%</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Current Month CB Ratio%</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Current Month RF Ratio%</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >Current Month Decline Ratio%</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><%=merchantRiskParameterVO.getFirstSubmissionInMonthDaysFormat()%></td>
        <td align="center" class="<%=style%>"><%=merchantRiskParameterVO.getLastSubmissionInMonthDaysFormat()%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getTotalProcessingAmount(),2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getSalesPercentage(),2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getChargebackPercentage(),2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getRefundPercentage(),2)%></td>
        <td align="center" class="<%=style%>"><%=Functions.round(processingDetailsVO.getDeclinedPercentage(),2)%></td>
      </tr>
      </thead>
    </table>
    <%
      }
    %>
  </div>
  <%
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No terminal found."));
      out.println("</div>");//end for
    }
  %>

  <%
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
      out.println("</div>");//end for
    }
  %>
</form>
<br>
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