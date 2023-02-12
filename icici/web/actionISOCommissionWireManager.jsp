<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.manager.vo.ISOCommReportVO" %>
<%--
  Created by IntelliJ IDEA.
  User: supriya
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
  <title>ISO Commission Report</title>
  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker();
    });
  </script>
</head>
<body>
<%!
  private static Logger logger=new Logger("actionISOCommissionWireManager.jsp");
%>
<%
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
        ISO Commission Report
        <div style="float: right;">
          <form action="/icici/addnewisocommwire.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Wire" name="submit" class="addnewmember" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Wire
            </button>
          </form>
        </div>
      </div><br>
      <form action="/icici/servlet/ISOCommissionWireList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="";
          String fdate=null;
          String tdate=null;
          String fmonth=null;
          String tmonth=null;
          String fyear=null;
          String tyear=null;
          String toid=null;
          String accountId=null;
          String ispaid=null;
          try
          {
            fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
            toid = ESAPI.validator().getValidInput("toid",request.getParameter("toid"),"Numbers",10,true);
            accountId = ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",10,true);
            ispaid = ESAPI.validator().getValidInput("ispaid",request.getParameter("ispaid"),"SafeString",2,true);


          }
          catch(ValidationException e)
          {
            logger.error("Date Format Exception while select",e);
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
          if (accountId != null) str = str + "&accountid=" + accountId;
          if (ispaid != null) str = str + "&ispaid=" + ispaid;

          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
          int year = Calendar.getInstance().get(Calendar.YEAR);

        %>
        <table  border="0" align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
          <tr>
            <td  class="textb" colspan="12">&nbsp;</td>
          </tr>
          <tr>
            <td class="textb" colspan="2">Account ID</td>
            <td class="textb" colspan="6">
              <select name="accountid" class="txtbox" style="width: 530px">
                <option value=""></option>
                <%
                  Hashtable<String, String> map = GatewayAccountService.getMerchantDetails();
                  Iterator it = map.entrySet().iterator();
                  while (it.hasNext())
                  {
                    String selected="";
                    Map.Entry pair = (Map.Entry)it.next();
                    if(pair.getKey().toString().equalsIgnoreCase(accountId))
                    {
                      selected="selected";
                    }
                %>
                <option value="<%=pair.getKey()%>" <%=selected%>><%=pair.getValue()%></option>
                <%
                  }
                %>
              </select>
            </td>
            <td class="textb" colspan="2">Is Paid</td>
            <td class="textb" colspan="2">
              <select name="ispaid" style="width:120px">
                <%
                  if("Y".equals(ispaid))
                  {
                %>
                <option value=""></option>
                <option value="Y" selected>Paid</option>
                <option value="N">UnPaid</option>
                <%
                }
                else if("N".equals(ispaid))
                {
                %>
                <option value=""></option>
                <option value="N" selected>Unpaid</option>
                <option value="Y">Paid</option>
                <%
                }
                else
                {
                %>
                <option value="" selected></option>
                <option value="N" >Unpaid</option>
                <option value="Y">Paid</option>
                <% }
                %>
              </select>
            </td>
          </tr>
          <tr>
            <td class="textb" colspan="12">&nbsp;</td>
          </tr>
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
            <td colspan="4" class="textb">
              <button type="submit" class="buttonform">
                <i class="fa fa-clock-o"></i>
                &nbsp;&nbsp;Search
              </button>
            </td>
          </tr>
          <tr><td colspan="12">&nbsp;</td></tr>
          <tr><td colspan="12">&nbsp;</td></tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%
    Functions functions=new Functions();
    String errorMessage=(String)request.getAttribute("message");
    ISOCommReportVO isoCommReportVO=(ISOCommReportVO)request.getAttribute("isoCommReportVO");
    String action = (String) request.getAttribute("action");
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String conf = " ";

    if ("view".equalsIgnoreCase(action))
    {
      conf = "disabled";
    }
    if (isoCommReportVO != null)
    {
      String setteledDate="";
      String startDate="";
      String endDate="";
      String status=isoCommReportVO.getStatus();
      if(functions.isValueNull(isoCommReportVO.getSettledDate()) && "disabled".equalsIgnoreCase(conf))
      {
        setteledDate=simpleDateFormat.format(simpleDateFormat.parse(isoCommReportVO.getSettledDate()));
      }
      if(functions.isValueNull(isoCommReportVO.getStartDate()))
      {
        startDate = simpleDateFormat.format(simpleDateFormat.parse(isoCommReportVO.getStartDate()));
      }
      if(functions.isValueNull(isoCommReportVO.getEndDate()))
      {
        endDate=simpleDateFormat.format(simpleDateFormat.parse(isoCommReportVO.getEndDate()));
      }
      String style="class=tr1";

  %>
  <form action="/icici/servlet/UpdateISOCommWire?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" name="isocommid" value="<%=isoCommReportVO.getIsoWireId()%>">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <input type="hidden" value="true" name="isSubmitted">
    <table border="1" bordercolor="#ffffff" align="center" style="width:80%" class="table table-striped table-bordered table-green dataTable">
      <tr <%=style%>>
        <td class="th0" colspan="2">ISO Commission Report:</td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Account Id : </td>
        <td class="tr1"><input type="text" class="txtbox1" size="30" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(isoCommReportVO.getAccountId())%>" disabled> </td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Start Date : </td>
        <td class="tr1"><input type="text"  class="txtbox1" size="50" name="startdate" value="<%=ESAPI.encoder().encodeForHTML(startDate)%>"disabled></td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">End Date : </td>
        <td class="tr1"><input type="text"  class="txtbox1" size="50" name="enddate" value="<%=ESAPI.encoder().encodeForHTML(endDate)%>" disabled></td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Settle Date: </td>
        <td class="tr1">
          <input type="text" size="50" readonly class="datepicker" name="settledate" value="<%=setteledDate%>" <%=conf%>>
        </td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Amount: </td>
        <td class="tr1"><input type="text" class="txtbox1" size="50" name="amount" value="<%=Functions.round(isoCommReportVO.getAmount(),2)%>" <%=conf%>></td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">NetFinalAmount: </td>
        <td class="tr1"><input type="text" class="txtbox1" size="=50" name="netfinalamount" value="<%=Functions.round(isoCommReportVO.getNetfinalamount(),2)%>" <%=conf%>></td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Unpaid Amount: </td>
        <td class="tr1"><input type="text" class="txtbox1" size="50" name="unpaidamount" value="<%=Functions.round(isoCommReportVO.getUnpaidAmount(),2)%>" <%=conf%>></td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Status: </td>
        <td class="tr1">
          <select name="status" class="txtbox"  <%=conf%>>
            <%
              if("paid".equalsIgnoreCase(status))
              {
            %>
            <option value="paid" selected>Paid</option>
            <option value="unpaid">Unpaid</option>
            <%
            }
            else
            {
            %>
            <option value="unpaid" selected>Unpaid</option>
            <option value="paid">Paid</option>
            <%
              }
            %>
          </select>
        </td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Report File Path: </td>
        <td class="tr1"><input type="text" class="txtbox1" size="50" name="reportfilepath" value="<%=ESAPI.encoder().encodeForHTML(isoCommReportVO.getReportFilePath())%>" disabled></td>
      </tr>
      <tr <%=style%>>
        <td class="tr1">Transaction File Path: </td>
        <td class="tr1"><input type="text" class="txtbox1" size="50" name="transactionfilepath" value="<%=functions.isEmptyOrNull(ESAPI.encoder().encodeForHTML(isoCommReportVO.getTransactionFilePath()))?"":ESAPI.encoder().encodeForHTML(isoCommReportVO.getTransactionFilePath())%>" disabled></td>
      </tr>
      <tr <%=style%>>
        <td class="tr1" colspan="2" align="center"><input type="submit" value="Update" class="buttonform" <%=conf%>></td>
      </tr>
    </table>
  </form>
  <%
    }
    else if(functions.isValueNull(errorMessage))
    {
      out.println(Functions.NewShowConfirmation("Result",errorMessage));
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Error","No Records Found"));
    }
  %>
</div>
</body>
</html>

