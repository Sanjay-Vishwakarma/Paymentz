<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 8/8/16
  Time: 3:22 PM
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
  <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

  <script language="javascript">
    function getPdfFile(settleid)
    {
      if (confirm("Do you really want to download selected file."))
      {
        document.getElementById("pdfform"+settleid).submit();
      }
    }
  </script>
  <title>Bank Description Manager> ISO Commission Report</title>
</head>
<body>
<%!
  private static Logger logger=new Logger("isocommwiremanagerlist.jsp");
  Functions functions=new Functions();
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    Connection conn=null;
    try
    {
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
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
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
              <input name="accountid" id="accountid" value="<%=accountId==null?"":accountId%>" class="txtbox" autocomplete="on">
            <%--<select name="accountid" class="txtbox" style="width: 530px">
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
              </select>--%>
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
                <option value="" selected> -- select -- </option>
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
    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
      out.println("<b>");
      out.println(error);
      out.println("</b>");
    }

    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    Hashtable temphash=null;
    if (hash != null && hash.size() > 0)
    {
      int records=0;
      int totalrecords=0;
      int currentblock=1;

      try
      {
        records=Functions.convertStringtoInt((String)hash.get("records"),15);
        totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
        currentblock = Functions.convertStringtoInt((request.getParameter("currentblock")),1);
      }
      catch(Exception ex)
      {
        logger.error("Records & TotalRecords is found null",ex);
      }
      if(records>0)
      {
  %>

  <table align=center width="90%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0">Sr No</td>
      <td valign="middle" align="center" class="th0">Account ID</td>
      <td valign="middle" align="center" class="th0">Wire Creation On</td>
      <td valign="middle" align="center" class="th0">Start Date</td>
      <td valign="middle" align="center" class="th0">End Date</td>
      <td valign="middle" align="center" class="th0">Settle Date</td>
      <td valign="middle" align="center" class="th0">Net Final Amount</td>
      <td valign="middle" align="center" class="th0">Unpaid Amount</td>
      <td valign="middle" align="center" class="th0">Currency</td>
      <td valign="middle" align="center" class="th0">Status</td>
      <td valign="middle" align="center" class="th0">PDF Report</td>
      <td valign="middle" align="center" class="th0" colspan="3">Action</td>
      </td>
    </tr>
    </thead>
    <%
      String style="class=td1";
      String ext="light";
      Hashtable inner=null;
      for(int pos=1;pos<=records;pos++)
      {
        String id=Integer.toString(pos);

        int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

        if(pos%2==0)
        {
          style="class=tr0";
          ext="dark";
        }
        else
        {
          style="class=tr1";
          ext="light";
        }

        temphash=(Hashtable)hash.get(id);
        String isoCommId=ESAPI.encoder().encodeForHTML((String)temphash.get("iso_comm_id"));
        String accountId1=ESAPI.encoder().encodeForHTML((String)temphash.get("accountid"));
        String wireCreationTime=(String)temphash.get("wirecreationtime");
        String firstDate=(String)temphash.get("startdate");
        String lastDate=(String)temphash.get("enddate");
        String settleDate=functions.isEmptyOrNull((String)temphash.get("settleddate"))?"":(String)temphash.get("settleddate");
        String netFinalAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("netfinalamount"));
        String unpaidAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("unpaidamount"));
        String currency=ESAPI.encoder().encodeForHTML((String)temphash.get("currency"));
        String status=ESAPI.encoder().encodeForHTML((String)temphash.get("status"));

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(functions.isValueNull(wireCreationTime))
        {
          wireCreationTime=simpleDateFormat.format(simpleDateFormat.parse(wireCreationTime));
        }
        if(functions.isValueNull(settleDate))
        {
          settleDate=simpleDateFormat.format(simpleDateFormat.parse(settleDate));
        }
        if(functions.isValueNull(firstDate))
        {
          firstDate=simpleDateFormat.format(simpleDateFormat.parse(firstDate));
        }
        if(functions.isValueNull(lastDate))
        {
          lastDate=simpleDateFormat.format(simpleDateFormat.parse(lastDate));
        }
    %>
    <tr>
      <td align="center" <%=style%>><%=srno%></td>
      <td align="center" <%=style%>><%=accountId1%></td>
      <td align="center"<%=style%>><%=wireCreationTime%></td>
      <td align="center"<%=style%>><%=firstDate%></td>
      <td align="center"<%=style%>><%=lastDate%></td>
      <td align="center"<%=style%>><%=settleDate%></td>
      <td align="center"<%=style%>><%=netFinalAmount%></td>
      <td align="center"<%=style%>><%=unpaidAmount%></td>
      <td align="center"<%=style%>><%=currency%></td>
      <td align="center"<%=style%>><%=status%></td>
      <td align="center"<%=style%>><form id="pdfform<%=isoCommId%>" action="/icici/servlet/ActionISOCommissionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="isocommid" value=<%=isoCommId%>><input type="hidden" name="action" value="sendPdfFile">  </form><a href="javascript: getPdfFile(<%=isoCommId%>)"><img width="20" height="28" border="0" src="/icici/images/pdflogo.jpg"></a></td>
      <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionISOCommissionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="isocommid" value="<%=isoCommId%>"><input type="hidden" name="action" value="view"><input type="submit" class="gotoauto"  value="View"></form></td>
      <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionISOCommissionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="isocommid" value="<%=isoCommId%>"><input type="hidden" name="action" value="update"><input type="submit" class="gotoauto"  value="Update"></form></td>
    </tr>
    <%
      }
    %>
  </table>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="ISOCommissionWireList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation("Sorry","No Record Found."));
          }
        }
        else
        {
          out.println(Functions.NewShowConfirmation("Sorry","No Record Found."));
        }

      }
      catch(Exception e)
      {
        logger.error("Exception ::"+e);
      }
      finally
      {
        Database.closeConnection(conn);
      }
    }
    else
    {
      response.sendRedirect("/icici/logout.jsp");
      return;
    }
  %>
</div>
</body>
</html>