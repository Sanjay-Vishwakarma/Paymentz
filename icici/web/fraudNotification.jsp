<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 23/6/18
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Common Integration> Common Fraud> Fraud intimation & Action </title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script language="javascript">
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
    function fraudAlerts()
    {
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          break;
        }
      }
      if(!flag)
      {
        alert("Select at least one transaction");
        return flag;
      }
      var con=confirm("Do you really want to update all selected transaction.");

      if(con==true){
        return true;
      }
      else{
        return false;
      }
    }
    function fraudAlertsNotification()
    {
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          break;
        }
      }
      if(!flag)
      {
        alert("Select at least one transaction");
        return flag;
      }
      var con=confirm("Do you really want to update all selected transaction.");

      if(con==true){
        return true;
      }
      else{
        return false;
      }
    }
  </script>
</head>
<%
  HashMap<String, String> statusHash = new LinkedHashMap();
  statusHash.put("capturesuccess", "Capture Successful");
  statusHash.put("partialrefund", "Partial Refund");
  statusHash.put("settled", "Settled");

  Functions functions = new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String trackingId = Functions.checkStringNull(request.getParameter("STrackingid"));
  String paymentID=Functions.checkStringNull(request.getParameter("paymentid"));
  String firstSix = Functions.checkStringNull(request.getParameter("firstsix"));
  String lastFour = Functions.checkStringNull(request.getParameter("lastfour"));
  String emailAddr = Functions.checkStringNull(request.getParameter("emailaddr"));
  String name = Functions.checkStringNull(request.getParameter("name"));
  String memberId = request.getParameter("toid")==null?"":request.getParameter("toid");
  String accountId = request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String status = Functions.checkStringNull(request.getParameter("status"));
  String description=Functions.checkStringNull(request.getParameter("description"));
  String amount=Functions.checkStringNull(request.getParameter("amount"));
  String pgtypeid = request.getParameter("pgtypeid")==null?"":request.getParameter("pgtypeid");
  int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
  int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

  String str = "";
  if (trackingId == null) trackingId = "";
  if (paymentID==null)paymentID="";
  if (firstSix == null)firstSix = "";
  if (lastFour == null)lastFour = "";
  if (emailAddr == null) emailAddr = "";
  if (name == null) name = "";
  if (memberId == null) memberId = "";
  if (accountId==null)accountId="";
  if (status == null)
  {
    status = "";
  }
  if (description==null)description="";
  if (amount==null)amount="";
  if (pgtypeid == null) pgtypeid = "";

  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String fdate=null;
    String tdate=null;
    String fmonth=null;
    String tmonth=null;
    String fyear=null;
    String tyear=null;

    try
    {
      fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
      tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
      fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
      tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
      fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
      tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
    }
    catch(ValidationException e)
    {

    }
    Calendar rightNow = Calendar.getInstance();
    String currentyear= "" + rightNow.get(rightNow.YEAR);
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

%>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  ResourceBundle rb= LoadProperties.getProperty("com.directi.pg.chargebackFraud");
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Fraud Reversal & Intimation
      </div>
      <form action="/icici/servlet/FraudNotification?ctoken=<%=ctoken%>" method="post" name="forms">
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
                  <td width="8%" class="textb" >From</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select size="1" name="fdate" class="textb">
                      <%
                        if (fdate != null)
                          out.println(Functions.dayoptions(1, 31, fdate));
                        else
                          out.println(Functions.printoptions(1, 31));
                      %>
                    </select>
                    <select size="1" name="fmonth" class="textb">
                      <%
                        if (fmonth != null)
                          out.println(Functions.newmonthoptions(1, 12, fmonth));
                        else
                          out.println(Functions.printoptions(1, 12));
                      %>
                    </select>
                    <select size="1" name="fyear" class="textb">
                      <%
                        if (fyear != null)
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                        else
                          out.println(Functions.printoptions(2005, 2013));
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >To</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select size="1" name="tdate" class="textb">
                      <%
                        if (tdate != null)
                          out.println(Functions.dayoptions(1, 31, tdate));
                        else
                          out.println(Functions.printoptions(1, 31));
                      %>
                    </select>

                    <select size="1" name="tmonth" class="textb">
                      <%
                        if (tmonth != null)
                          out.println(Functions.newmonthoptions(1, 12, tmonth));
                        else
                          out.println(Functions.printoptions(1, 12));
                      %>
                    </select>

                    <select size="1" name="tyear" class="textb" style="width:54px;">

                      <%
                        if (tyear != null)
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                        else
                          out.println(Functions.printoptions(2005, 2013));
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Staus</td>
                  <td width="3%" class="textb"></td>
                  <td><select size="1" name="status" class="txtbox">
                    <option value="">All</option>
                    <%
                      Set statusSet = statusHash.keySet();
                      Iterator iterator=statusSet.iterator();
                      String selected = "";
                      String key = "";
                      String value = "";

                      while (iterator.hasNext())
                      {
                        key = (String)iterator.next();
                        value = (String) statusHash.get(key);

                        if (key.equals(status))
                          selected = "selected";
                        else
                          selected = "";

                    %>
                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>
                    <%
                      }
                    %>
                  </select>
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
                  <td width="8%" class="textb">Tracking ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text" maxlength="500" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingId)%>"
                           name="STrackingid" size="10" class="txtbox">
                  </td>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Payment ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="paymentid" maxlength="50" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("paymentid")==null?"":request.getParameter("paymentid"))%>">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Description</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">
                    <input type=text name="description" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(description)%>" class="txtbox" size="10">
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
                  <td width="8%" class="textb" colspan="2">Card Number</td>
                  <td width="7%" class="textb">
                    <input type=text  name="firstsix" maxlength="6" size="6"  class="txtbox" style="width:60px" value="<%=firstSix%>">
                    <input type=text name="lastfour" maxlength="4" size="4" class="txtbox" style="width:60px" value="<%=lastFour%>">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb" >Customer Name</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="txtbox" size="10">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb" >Email ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="emailaddr" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddr)%>" class="txtbox" size="20">
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
                  <td width="8%" class="textb">Gateway*</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Account ID*</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="accountid" id="accountid1" value="<%=accountId%>" class="txtbox" autocomplete="on">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Member ID*</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="toid" id="memberid1" value="<%=memberId%>" class="txtbox" autocomplete="on">
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
  <%
    String error=(String) request.getAttribute("error");
    if(error !=null)
    {
      out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
    }
    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
      currentblock="1";

    if(currentblock==null)
      currentblock="1";
    if(request.getAttribute("cbVO")!=null)
    {
      List<TransactionVO> cbVOList= (List<TransactionVO>) request.getAttribute("cbVO");
      PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
      if(cbVOList.size()>0)
      {
  %>
  <form name="markfraud" action="/icici/servlet/FraudNotificationAction?ctoken=<%=ctoken%>" method="post" name="forms">
    <input type="hidden" name="toid" value="<%=memberId%>">
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
        <td valign="middle" align="center" class="th0">Sr.No</td>
        <td valign="middle" align="center" class="th0">Date</td>
        <td valign="middle" align="center" class="th0">TrackingID</td>
        <td valign="middle" align="center" class="th0">MemberID</td>
        <td valign="middle" align="center" class="th0">Card Holder's Name</td>
        <td valign="middle" align="center" class="th0">Email</td>
        <td valign="middle" align="center" class="th0">Amount</td>
        <td valign="middle" align="center" class="th0">Card Num</td>
        <td valign="middle" align="center" class="th0">IsFraud</td>
        <td valign="middle" align="center" class="th0">Status</td>
        <td valign="middle" align="center" class="th0">Reason</td>
      </tr>
      </thead>
      <%
        int sno=1;
        String style="class=td1";
        String ext="light";
        String reason = rb.getString("reason");
        String reason1[] = reason.split("\\|");

        for(TransactionVO transactionVO : cbVOList)
        {
          String country="";
          String cardNO="";
          String nameStr="";
          String amountStr="";

          if(functions.isValueNull(transactionVO.getCountry()))
            country=transactionVO.getCountry();

          if(functions.isValueNull(transactionVO.getFirstSix())&& functions.isValueNull(transactionVO.getLastFour()))
          {
             cardNO=transactionVO.getFirstSix()+"******"+transactionVO.getLastFour();
          }
          else {
            cardNO="";
          }

          if(functions.isValueNull(transactionVO.getCustFirstName())&& functions.isValueNull(transactionVO.getCustLastName()))
          {
             nameStr=transactionVO.getCustFirstName()+" "+transactionVO.getCustLastName();
          }
          else {
            nameStr="-";
          }

          if(functions.isValueNull(transactionVO.getCurrency())&& functions.isValueNull(transactionVO.getAmount()))
          {
            amountStr=transactionVO.getCurrency()+" "+transactionVO.getAmount();
          }
          else {
            amountStr="";
          }

          int pos=1;
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
          int srno=sno+((pageno-1)*pagerecords);
          out.println("<tr>");
          out.println("<td align=center "+ style + ">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+transactionVO.getTrackingId()+"\"></td>");
          out.println("<td align=center "+style+">&nbsp;" + srno +"</td>");
          out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(transactionVO.getTimestamp())+"</td>");
          out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId())+"</td>");
          out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(transactionVO.getToid())+"</td>");
          out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(nameStr)+"</td>");
          out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(transactionVO.getEmailAddr())+"<input type=\"hidden\" name=\"emailaddr_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getEmailAddr()+"\"></td>");
          out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(amountStr)+"</td>");
          out.println("<td align=center "+style+">&nbsp;" +cardNO+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getFraudRequest())+"</td>");
          out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(transactionVO.getStatus())+"<input type=\"hidden\" name=\"status_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getStatus() +"\"></td>");
          out.println("<td align=center "+style+">&nbsp; <select class=\"txtbox\" name=\"reason_"+ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId())+"\">");
          out.println("<option value=\"0\"  default>Select Reason</option>");
          for(String remark:reason1)
          {
            out.println("<option value=\""+remark+"\">\""+remark+"\"</option>");
          }
          out.println("</select></td>");
          out.println("</tr>");
          sno++;
        }
      %>
      <thead>
      <tr>
        <td class="th0" colspan="12">
          <input type="submit" name="submitbutton" class="addnewmember" value="Fraud Intimation">
          <input type="submit" name="submitbutton" class="addnewmember" value="Fraud Reversal & Intimation" onclick="return fraudAlertsNotification();">
        </td>
      </tr>
      </thead>
    </table>
  </form>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
      }
      else
      {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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
  }
%>
</body>
</html>