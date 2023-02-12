<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="java.util.List" %>
<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Wallet
  Date: 10/03/2021
  Time: 19:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/cardtype-issuing-bank.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<style type="text/css">
  #maintitle{
    text-align: center;
    background: #7eccad;
    color: #fff;
    font-size: 14px;
  }

  @media(min-width: 640px){
    #saveid{
      position: absolute;
      background: #F9F9F9!important;
    }

    #savetable{
      padding-bottom: 25px;
    }

    table.table{
      margin-bottom: 6px !important;
    }

    table#savetable td:before{
      font-size: inherit;
    }
  }

  table#savetable td:before{
    font-size: 13px;
    font-family: Open Sans;
  }

  table.table{
    margin-bottom: 0px !important;
  }

  #saveid input{
    font-size: 16px;
    padding-right: 30px;
    padding-left: 30px;
  }


  .multiselect {
    width: 100%;

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
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
  }

  #checkboxes {
    display: none;
    border: 1px #dadada solid;
    width: 80%;
    background-color: #ffffff;
    z-index: 1;
    height: 82px;
    overflow-x: auto;
  }

  #checkboxes label {
    display: block;
  }

  #checkboxes label:hover {
    background-color: #1e90ff;
  }

  #checkboxes_1 {
    display: none;
    border: 1px #dadada solid;
    position: absolute;
    width: 100%;
    background-color: #ffffff;
    z-index: 1;
    height: 130px;
    overflow-x: auto;
  }

  #checkboxes_1 label {
    display: block;
  }

  #checkboxes_1 label:hover {
    background-color: #1e90ff;
  }

  #checkboxes_2 {
    display: none;
    border: 1px #dadada solid;
    position: absolute;
    width: 17%;
    background-color: #ffffff;
    z-index: 1;
    height: 110px;
    overflow-x: auto;
  }

  #checkboxes_2 label {
    display: block;
  }

  #checkboxes_2 label:hover {
    background-color: #1e90ff;
  }


</style>
<%
  Functions functions = new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String pid = Functions.checkStringNull(request.getParameter("pid"));
  String rbid = Functions.checkStringNull(request.getParameter("rbid"));
  String memberid=Functions.checkStringNull(request.getParameter("memberid"));
  String terminalid = Functions.checkStringNull(request.getParameter("terminalid"));
  String recurring_subscription_id = Functions.checkStringNull(request.getParameter("recurring_subscription_id"));
  String firstsix = Functions.checkStringNull(request.getParameter("firstsix"));
  String lastfour=Functions.checkStringNull(request.getParameter("lastfour"));
  String name = Functions.checkStringNull(request.getParameter("name"));
  String pTerminalBuffer = Functions.checkStringNull(request.getParameter("terminalBuffer"));
  String cardHolderName = Functions.checkStringNull(request.getParameter("name"));
  String trackingid = request.getParameter("trackingid")==null?"":request.getParameter("trackingid");

  String str = "";
  if (pid == null) pid = "";
  if (memberid == null)memberid = "";
  if (terminalid == null)terminalid = "";
  if (recurring_subscription_id == null) recurring_subscription_id = "";
  if (firstsix == null) firstsix = "";
  if (lastfour == null) lastfour = "";
  if (trackingid == null) trackingid = "";
  if (name == null) name = "";


  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Recurring Module
        <%--<div style="float: right;">
          <form action="/icici/bulkFraudUpload.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Upload Card Details" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Upload Bulk Fraud
            </button>
          </form>
        </div>--%>
      </div>
      <form action="/icici/servlet/RecurringModule?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
          <%
            String errorMsg=(String) request.getAttribute("errorMsg");
              if(errorMsg !=null)
              {
                  out.println("<center><font class=\"textb\"><b>"+errorMsg+"</b></font></center>");
              }
          %>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5%">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Partner ID*</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>"
                           name="pid" id="allpid" class="txtbox">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Merchant Id</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>"
                           name="memberid" id="midy" class="txtbox">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Terminal Id</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>"
                           name="terminalid" id="tid" class="txtbox">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Recurring Billing ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text"value="<%=ESAPI.encoder().encodeForHTMLAttribute(recurring_subscription_id)%>"
                           name="recurring_subscription_id" class="txtbox">
                  </td>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">First Six</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="firstsix" maxlength="50" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Last Four</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">
                    <input type=text name="lastfour" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>" class="txtbox" size="10">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>

                <tr>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb" >Tracking ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="trackingid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" class="txtbox" size="10">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb" >Card Holder Name</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="txtbox" size="20">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>

                <td width="3%" class="textb">&nbsp;</td>
                <td width="12%" class="textb">
                  <button type="submit" class="buttonform">
                    Search
                  </button>
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
  <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center"
         class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="5%" class="th0" colspan="8">Recurring Subscriptions Details</td>
    </tr>
    <tr>
      <td width="5%" class="th0">Recurring Billing ID</td>
      <td width="3%" class="th0">Interval</td>
      <td width="3%" class="th0">Frequency</td>
      <td width="3%" class="th0">Run Date</td>
      <td width="5%" class="th0">Subscribe Amount</td>
      <td width="5%" class="th0">Card Number</td>
      <td width="5%" class="th0">Card Holder</td>
      <td width="5%" class="th0">status</td>
    </thead>
    <tr>
      <%
        RecurringBillingVO recurringBillingVO = null;

        recurringBillingVO = (RecurringBillingVO) request.getAttribute("recurringsub");

      %>
      <%
      out.println("<tr>");
      String style=null;
     if (recurringBillingVO.getRecurring_subscrition_id()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurring_subscrition_id()) + "</td>");
    }
    else if(recurringBillingVO.getRecurring_subscrition_id()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
    if (recurringBillingVO.getInterval()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getInterval()) + "</td>");
    }
    else if(recurringBillingVO.getInterval()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
    if (recurringBillingVO.getFrequency()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getFrequency()) + "</td>");
    }
    else if(recurringBillingVO.getFrequency()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
     if (recurringBillingVO.getRunDate()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getRunDate()) + "</td>");
    }
    else if(recurringBillingVO.getRunDate()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
    if (recurringBillingVO.getAmount()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getAmount()) + "</td>");
    }
    else if(recurringBillingVO.getAmount()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
     if (recurringBillingVO.getFirstSix()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getFirstSix()+"****"+recurringBillingVO.getLastFour()) + "</td>");
    }
    else if(recurringBillingVO.getFirstSix()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
    if (recurringBillingVO.getCardHolderName()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getCardHolderName()) + "</td>");
    }
    else if(recurringBillingVO.getCardHolderName()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
    if (recurringBillingVO.getRecurringStatus()!=null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(recurringBillingVO.getRecurringStatus()) + "</td>");
    }
    else if(recurringBillingVO.getRecurringStatus()==null)
    {
    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
    }
    out.println("</tr>");
   %>
      </tr>
    <%
      if(request.getAttribute("transactionList")!=null)
      {
        List<RecurringBillingVO> rbList = (List<RecurringBillingVO>) request.getAttribute("transactionList");
        if(rbList.size()>0)
        {
    %>
      <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center"
             class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
          <td width="5%" class="th0" colspan="8">Recurring Transaction Details</td>
        </tr>
        <tr>
          <td width="5%" class="th0">Sr.No</td>
          <td width="3%" class="th0">TrackingId</td>
          <td width="3%" class="th0">Amount</td>
          <td width="3%" class="th0">Transaction Date(yyyy/MM/dd)</td>
          <td width="5%" class="th0">Transaction Status</td>
        </thead>
        <tr>
          <%
            String ext="light";
            int pos = 1;
            for(RecurringBillingVO recVO : rbList)
            {
              {
                if (pos % 2 == 0)
                {
                  style = "class=tr0";
                  ext = "dark";
                }
                else
                {
                  style = "class=tr1";
                  ext = "light";
                }

                out.println("<tr>");
                out.println("<td align=center " + style + ">&nbsp;" + pos + "</td>");
                out.println("<td align=center " + style + ">&nbsp;" + recVO.getNewPzTransactionID() + "</td>");
                out.println("<td align=center " + style + ">&nbsp;" + recVO.getAmount() + "</td>");
                if ("null".equals(recVO.getRecurringRunDate()))
                {
                  out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                }
                else if (!"null".equals(recVO.getRecurringRunDate()))
                {
                  out.println("<td align=center " + style + ">&nbsp;" + recVO.getRecurringRunDate() + "</td>");
                }
                out.println("<td align=center " + style + ">&nbsp;" + recVO.getTransactionStatus() + "</td>");
                out.println("</tr>");
                pos++;
              }
            }
            }
          %>
        </tr>
        <%
          }
          }
          }
        %>
  </table>
  </div>
</body>
</html>
