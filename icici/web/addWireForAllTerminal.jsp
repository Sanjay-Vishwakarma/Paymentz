<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TreeMap" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 6/3/14
  Time: 3:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

  <script type="text/javascript">
    $('#sandbox-container input').datepicker({

    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker();
    });
  </script>
  <script type="text/javascript" language="JavaScript">
    function validate(){
      checkPDF();
      checkXLS();
      var xls=checkXLS();
      var pdf=checkPDF();
      if(xls && pdf){
        return true;
      }
      if(!xls){
        alert('Please select a .xls file! ');
      }
      if(!pdf){
        alert('Please select a .pdf file! ');
      }
      return false;
    }

    function checkPDF() {
      var retpath = document.FIRCForm.settledReportFileName.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;
      var settledReportFile= retpath.split("\\").pop();
      document.getElementById('settledReportFile').value=settledReportFile;

      if (filename==('pdf'))
      {
        return true;
      }
      else
      {
        document.FIRCForm.settledReportFile.value="";
        return false;
      }
    }

    function checkXLS() {
      var retpath = document.FIRCForm.settledTransFileName.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;
      var settledTransFile=retpath.split("\\").pop();
      document.getElementById('settledTransFile').value=settledTransFile;

      if (filename==('xls')) {
        return true;
      }
      else
      {
        document.FIRCForm.settledTransFile.value="";
        return false;
      }
    }
  </script>

  <title></title>
</head>
<body>
<%!
  private static Logger logger=new Logger("addwire.jsp");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {

%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Wire Manager
        <div style="float: right;">
          <form action="/icici/addWireForAllTerminal.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New wire For ALL Terminal
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/addwire.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New wire
            </button>
          </form>
        </div>
      </div><br>
      <form action="/icici/servlet/WireList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <%
          Hashtable<String,String> details= (Hashtable) request.getAttribute("detailHash");
          String str = "";
          String fdate = null;
          String tdate = null;
          String fmonth = null;
          String tmonth = null;
          String fyear = null;
          String tyear = null;
          String toid = null;
          String accountid = null;
          String terminalid = null;
          String ispaid=null;
            String memberid="";
          String amount= "";
          String balanceamount= "";
          String unpaidamount= "";
          String famount= "";
          String status= "";
          String rrwire= "";
          String firstdate= "";
          String settledstarttime= "";
          String lastdate= "";
          String settledendtime= "";
          String declinedcoverdateupto="";
          String declinedcovertimeupto= "";
          String reversedcoverdateupto= "";
          String reversedcovertimeupto= "";
          String chargebackcoverdateupto= "";
          String chargebackcovertimeupto="";
          String rrdate= "";
          String rrtime= "";
          String cycleno= "";

          String pgtypeid = "";
            pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
            accountid = Functions.checkStringNull(request.getParameter("accountid"));
            memberid = Functions.checkStringNull(request.getParameter("toid")) == null ? "" : request.getParameter("toid");
            terminalid = Functions.checkStringNull(request.getParameter("terminalid"));

          //Get List of TerminalID
          PayoutManager payoutManager = new PayoutManager();
          TerminalManager terminalManager = new TerminalManager();
          TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
          TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
          TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();

          List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
          for(TerminalVO terminalVO : terminalList)
          {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
          }

          try
          {
            fdate = ESAPI.validator().getValidInput("fdate", request.getParameter("fdate"), "Days", 2, true);
            tdate = ESAPI.validator().getValidInput("tdate", request.getParameter("tdate"), "Days", 2, true);
            fmonth = ESAPI.validator().getValidInput("fmonth", request.getParameter("fmonth"), "Months", 2, true);
            tmonth = ESAPI.validator().getValidInput("tmonth", request.getParameter("tmonth"), "Months", 2, true);
            fyear = ESAPI.validator().getValidInput("fyear", request.getParameter("fyear"), "Years", 4, true);
            tyear = ESAPI.validator().getValidInput("tyear", request.getParameter("tyear"), "Years", 4, true);
            toid = ESAPI.validator().getValidInput("toid", request.getParameter("toid"), "Numbers", 10, true);
            accountid = ESAPI.validator().getValidInput("accountid", request.getParameter("accountid"), "Numbers", 10, true);
            terminalid = ESAPI.validator().getValidInput("accountid", request.getParameter("tyear"), "Numbers", 10, true);
          }
          catch (ValidationException e)
          {
          }
          Calendar rightNow = Calendar.getInstance();
          if (fdate == null) fdate = "" + 1;
          if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
          if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
          if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
          if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
          if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);
          str = str + "ctoken=" + ctoken;
          if (fdate != null) str = str + "&fdate=" + fdate;
          if (tdate != null) str = str + "&tdate=" + tdate;
          if (fmonth != null) str = str + "&fmonth=" + fmonth;
          if (tmonth != null) str = str + "&tmonth=" + tmonth;
          if (fyear != null) str = str + "&fyear=" + fyear;
          if (tyear != null) str = str + "&tyear=" + tyear;
          if (toid != null) str = str + "&toid=" + toid;
          if (accountid != null) str = str + "&accountid=" + accountid;
          if (terminalid != null) str = str + "&terminalid=" + terminalid;
          if (pgtypeid != null) str = str + "&pgtypeid=" + pgtypeid;
          int year = Calendar.getInstance().get(Calendar.YEAR);
        %>
        <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
          <tr><td colspan="12">&nbsp;</td></tr>
          <tr>
            <td colspan="2" class="textb" >From</td>
            <td colspan="2" class="textb">

              <select size="1" name="fdate"  value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                <%
                  if (fdate != null)
                    out.println(Functions.dayoptions(1, 31, fdate));
                  else
                    out.println(Functions.printoptions(1, 31));
                %>
              </select>
              <select size="1" name="fmonth"  value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                <%
                  if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                  else
                    out.println(Functions.printoptions(1, 12));
                %>
              </select>
              <select size="1" name="fyear"  value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                <%
                  if (fyear != null)
                    out.println(Functions.yearoptions(2005, year, fyear));
                  else
                    out.println(Functions.printoptions(2005, year));
                %>
              </select>
            </td>
            <td colspan="2" class="textb" >To</td>
            <td colspan="2" class="textb">
              <select size="1" name="tdate" >
                <%
                  if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                  else
                    out.println(Functions.printoptions(1, 31));
                %>
              </select>

              <select size="1" name="tmonth" >
                <%
                  if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                  else
                    out.println(Functions.printoptions(1, 12));
                %>
              </select>

              <select size="1" name="tyear" >
                <%
                  if (tyear != null)
                    out.println(Functions.yearoptions(2005, year, tyear));
                  else
                    out.println(Functions.printoptions(2005, year));
                %>
              </select>
            </td>

            <td class="textb" colspan="2">Is Paid</td>
            <td class="textb" colspan="2">
              <select name="isrr" >
                <%
                  if("Y".equals(ispaid))
                  {
                %>
                <option value="">--ALL--</option>
                <option value="Y" selected>Paid</option>
                <option value="N">UnPaid</option>
                <%
                }
                else if("N".equals(ispaid))
                {
                %>
                <option value="">--ALL--</option>
                <option value="N" selected>Unpaid</option>
                <option value="Y">Paid</option>
                <%
                }
                else
                {
                %>
                <option value="" selected>--ALL--</option>
                <option value="N" >Unpaid</option>
                <option value="Y">Paid</option>
                <% }
                %>
              </select>
            </td>
          </tr>

          <tr><td colspan="12">&nbsp;</td></tr>
          <tr><td colspan="12">&nbsp;</td></tr>

          <tr>
            <td colspan="1" class="textb" for="gateway">Gateway</td>
            <td colspan="2" class="textb">
              <input name="pgtypeid" id="pgtypeid" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
            </td>

            <td colspan="2" class="textb">AccountID</td>
            <td colspan="2" class="textb" >
              <input name="accountid" id="accountid"
                     value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>"
                     class="txtbox" autocomplete="on">
            </td>

            <td colspan="2" class="textb">Member ID</td>
            <td colspan="2" class="textb" >
              <input name="toid" id="memberid" value="<%=memberid%>" class="txtbox" autocomplete="on">
            </td>

            <td colspan="1" class="textb">Terminal ID</td>
            <td colspan="2" class="textb">
              <input name="terminalid" id="tid3"
                     value="<%=Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid")%>"
                     class="txtbox" autocomplete="on">
            </td>

          </tr>

          <tr><td colspan="12">&nbsp;</td></tr>

          <tr>
            <td colspan="12" class="textb">
              <button type="submit" class="buttonform" style="margin-left: 95%">
                <i class="fa fa-clock-o"></i>
                &nbsp;&nbsp;Search
              </button>
            </td>
          </tr>
          <tr><td colspan="12">&nbsp;</td></tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="row" style="margin-top:0px ">
  <div class="col-lg-12">
    <div class="panel panel-default" style="margin-top:5px">
      <div class="panel-heading" >
        Add New Wire For All Terminal
      </div>

      <form name="FIRCForm" action="/icici/servlet/AddMerchantWireAllTerminal?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="true" name="isSubmitted">
        <input type="hidden" id="settledTransFile" value="" name="settledTransFile">
        <input type="hidden" id="settledReportFile" value="" name="settledReportFile">
        <%
          String sberror = (String) request.getAttribute("statusmsg");
          String success=(String)request.getAttribute("success");
          String failure=(String)request.getAttribute("failure");
          if (sberror != null)
          {
            out.println("<center><font class=\"textb\"><b>"+sberror+"<br></b></font></center>");
          }
          else if("success".equalsIgnoreCase(success)){
            out.println("<center><font class=\"textb\"><b>"+"Records uploaded Successfully."+"<br></b></font></center>");
          }
          else if("failure".equalsIgnoreCase(failure)){
            out.println("<center><font class=\"textb\"><b>"+"Records uploaded Failed."+"<br></b></font></center>");
          }
          if(details!=null)
          {
            pgtypeid = Functions.checkStringNull(details.get("pgtypeid")) == null ? "" : details.get("pgtypeid");
            accountid = Functions.checkStringNull(details.get("accountid")) == null ? "" : details.get("accountid");
            memberid = Functions.checkStringNull(details.get("toid")) == null ? "" : details.get("toid");
            amount = Functions.checkStringNull(details.get("amount")) == null ? "" : details.get("amount");
            balanceamount = Functions.checkStringNull(details.get("balanceamount")) == null ? "" : details.get("balanceamount");
            unpaidamount = Functions.checkStringNull(details.get("unpaidamount")) == null ? "" : details.get("unpaidamount");
            famount = Functions.checkStringNull(details.get("famount")) == null ? "" : details.get("famount");
            status = Functions.checkStringNull(details.get("status")) == null ? "" : details.get("status");
            rrwire = Functions.checkStringNull(details.get("rrwire")) == null ? "" : details.get("rrwire");
            firstdate = Functions.checkStringNull(details.get("firstdate")) == null ? "" : details.get("firstdate");
            settledstarttime = Functions.checkStringNull(details.get("settledstarttime")) == null ? "" : details.get("settledstarttime");
            lastdate = Functions.checkStringNull(details.get("lastdate")) == null ? "" : details.get("lastdate");
            settledendtime = Functions.checkStringNull(details.get("settledendtime")) == null ? "" : details.get("settledendtime");
            declinedcoverdateupto = Functions.checkStringNull(details.get("declinedcoverdateupto")) == null ? "" : details.get("declinedcoverdateupto");
            declinedcovertimeupto = Functions.checkStringNull(details.get("declinedcovertimeupto")) == null ? "" : details.get("declinedcovertimeupto");
            reversedcoverdateupto = Functions.checkStringNull(details.get("reversedcoverdateupto")) == null ? "" : details.get("reversedcoverdateupto");
            reversedcovertimeupto = Functions.checkStringNull(details.get("reversedcovertimeupto")) == null ? "" : details.get("reversedcovertimeupto");
            chargebackcoverdateupto = Functions.checkStringNull(details.get("chargebackcoverdateupto")) == null ? "" : details.get("chargebackcoverdateupto");
            chargebackcovertimeupto = Functions.checkStringNull(details.get("chargebackcovertimeupto")) == null ? "" : details.get("chargebackcovertimeupto");
            rrdate = Functions.checkStringNull(details.get("rrdate")) == null ? "" : details.get("rrdate");
            rrtime = Functions.checkStringNull(details.get("rrtime")) == null ? "" : details.get("rrtime");
            cycleno = Functions.checkStringNull(details.get("cycleno")) == null ? "" : details.get("cycleno");
          }

        %>
        <table border="0"  width="70%" align="center">
          <tbody>
          <tr><td colspan="7" align="center"><h4>Add New Wire Merchant For ALL Terminal</h4></td>
          </tr>
          <tr><td colspan="7" ><font color="black">Note:- *All field are mandatory</font></td></tr>
          <tr>
            <td colspan="7" class="textb">&nbsp;</td>
          </tr>
          <tr>
            <td  colspan="3" class="textb">Gateway:</td>
            <td colspan="4" class="textb">
              <select size="1" id="bank1" name="pgtypeid" class="txtbox" >
                <option value="0" default>--All--</option>
                <%
                  for(String gatewayType : gatewayTypeTreeMap.keySet())
                  {
                    String isSelected = "";
                    String value = gatewayType.toUpperCase()+"-"+gatewayTypeTreeMap.get(gatewayType).getPgTypeId();
                    if(gatewayType.equalsIgnoreCase(pgtypeid))
                    {
                      isSelected = "selected";
                    }
                %>
                <option value="<%=gatewayType%>" <%=isSelected%>><%=gatewayType%></option>
                <%
                  }
                %>

              </select>
            </td>
          </tr>

          <tr>
            <td  colspan="3" class="textb">Account ID:</td>
            <td colspan="4" class="textb">
              <select size="1" id="accountid1" name="accountid" class="txtbox" >
                <option data-bank="all" value="0">--All--</option>
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
              </select>
            </td>
          </tr>

          <tr>
            <td  colspan="3" class="textb">Member ID:</td>
            <td colspan="4" class="textb">
              <select name="toid" id="memberid1" class="txtbox" >
                <option data-bank="all"  data-accid="all" value="0" selected>--All--</option>
                <%
                  for(String sMemberId: memberMap.keySet())
                  {
                    TerminalVO t = memberMap.get(sMemberId);
                    String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                    String accId = t.getAccountId();
                    String aContactPerson = t.getContactPerson();
                    String aCompanyName = t.getCompany_name();
                    String gateway2 = t.getGateway().toUpperCase();
                    String currency2 = t.getCurrency();
                    String pgtype = t.getGateway_id();
                    String value = gateway2+"-"+currency2+"-"+pgtype;
                    String isSelected = "";
                    if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid+"-"+accId))
                    {
                      isSelected = "selected";
                    }
                    else
                    {
                      isSelected = "";
                    }
                %>
                <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aCompanyName%></option>
                <%
                  }
                %>
              </select>
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Amount:</td>
            <td colspan="4">
              <input maxlength="15" class="txtbox" type="text" name="amount"  value="<%=amount%>">
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Balance Amount:</td>
            <td colspan="4">
              <input maxlength="15" class="txtbox" type="text" name="balanceamount"  value="<%=balanceamount%>">
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Unpaid Amount</td>
            <td colspan="4">
              <input maxlength="15" class="txtbox" type="text" name="unpaidamount"  value="<%=unpaidamount%>">
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Final Amount:</td>
            <td colspan="4" valign="top">
              <input maxlength="15" class="txtbox" type="text" name="famount"  value="<%=famount%>">
            </td>
          </tr>

          <%--<tr>
            <td colspan="3" class="textb">Currency:</td>
            <td colspan="4">
              <select name="currency" class="txtbox">
                <option value="" selected>Select Currency</option>
                <option value="USD" >USD</option>
                <option value="INR" >INR</option>
                <option value="EUR" >EUR</option>
                <option value="GBP" >GBP</option>
                <option value="JPY" >JPY</option>
              </select>
            </td>
          </tr>--%>

          <tr>
            <td colspan="3" class="textb">Status:</td>
            <td colspan="4">
              <select name="status" class="txtbox"><option value="" selected>Select Status</option>
                <option value="unpaid" <%if("unpaid".equals(status)) {%>selected<%}%>>UNPAID FOR ALL</option>
                <option value="paid" <%if("paid".equals(status)) {%>selected<%}%>>PAID FOR ALL</option>
                <>
              </select>
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Is RollingReserve Wire:</td>
            <td colspan="4">
              <select name="rrwire" class="txtbox"><option value="" selected></option>
                <option value="Y" <%if("Y".equals(rrwire)) {%>selected<%}%>>YES</option>
                <option value="N" <%if("N".equals(rrwire)) {%>selected<%}%>>NO</option>
              </select>
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Settelment Start Date:</td>
            <td colspan="2" class="textb">
              <input type="text" readonly class="datepicker" name="firstdate" value="<%=firstdate%>">
            </td>
            <td colspan="2" align="left" class="textb">
              <%--Time--%>Time: <input maxlength="10" type="text" class="txtbox" name="settledstarttime"  value="<%=settledstarttime%>" placeholder="(HH:MM:SS)">
            </td>
          </tr>
          <tr>
            <td colspan="3" class="textb">Settelment End Date:</td>
            <td colspan="2">
              <input type="text" readonly class="datepicker" name="lastdate" value="<%=lastdate%>">
            </td>
            <td colspan="2" class="textb">
              <%--Time--%>Time: <input maxlength="10" type="text" class="txtbox" name="settledendtime"  value="<%=settledendtime%>" placeholder="(HH:MM:SS)">
            </td>
          </tr>
          <tr>
            <td colspan="3" class="textb">Decline Covered Upto:</td>
            <td colspan="2">
              <input type="text" readonly class="datepicker" name="declinedcoverdateupto" value="<%=declinedcoverdateupto%>">
            </td>
            <td colspan="2" class="textb">
              <%--Time--%>Time: <input maxlength="10" type="text" class="txtbox" name="declinedcovertimeupto"  value="<%=declinedcovertimeupto%>" placeholder="(HH:MM:SS)">
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Reversal Covered Upto:</td>
            <td colspan="2">
              <input type="text" readonly class="datepicker" name="reversedcoverdateupto" value="<%=reversedcoverdateupto%>">
            </td>
            <td colspan="2" class="textb">
              <%--Time--%>Time: <input maxlength="10" type="text" class="txtbox" name="reversedcovertimeupto"  value="<%=reversedcovertimeupto%>" placeholder="(HH:MM:SS)">
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Chargeback Covered Upto:</td>
            <td colspan="2">
              <input type="text" readonly class="datepicker" name="chargebackcoverdateupto" value="<%=chargebackcoverdateupto%>">
            </td>
            <td colspan="2" class="textb">
              <%--Time--%>Time: <input maxlength="10" type="text" class="txtbox" name="chargebackcovertimeupto"  value="<%=chargebackcovertimeupto%>" placeholder="(HH:MM:SS)">
            </td>
          </tr>
          <tr>
            <td colspan="3" class="textb">Reserve Release Date:</td>
            <td colspan="2">
              <input type="text" readonly class="datepicker" name="rrdate" value="<%=rrdate%>">
            </td>
            <td colspan="2" class="textb">
              <%--Time--%>Time: <input maxlength="10" type="text" class="txtbox" name="rrtime"  value="<%=rrtime%>" placeholder="(HH:MM:SS)">
            </td>
          </tr>
          <tr>
            <td colspan="3" class="textb">Settlement Cycle No:</td>
            <td colspan="4">
              <input maxlength="35" type="text" class="txtboxsignup" name="cycleno"  value="<%=cycleno%>">
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Settled Transaction File Name(.xls):</td>
            <td colspan="4">
                <input type="file" name="settledTransFileName" oninput="checkXLS()">
            </td>
          </tr>

          <tr>
            <td colspan="3" class="textb">Settlement Report File Name(.pdf):</td>
            <td colspan="4">
              <input type="file" name="settledReportFileName" oninput="checkPDF()">
            </td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>
          <tr>
            <td align="center" colspan="6" class="textb">
              <button type="submit" class="buttonform" value="Save" onclick="return validate()">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Save
              </button>
            </td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>

          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
<%
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