<%@ page  import="com.directi.pg.Functions,com.directi.pg.core.GatewayAccount,com.directi.pg.core.GatewayAccountService,org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.manager.enums.TransReqRejectCheck" %>
<%@ page import="com.directi.pg.PzEncryptor" %>
<%!
  private static Logger log=new Logger("transactionDetails.jsp");
%>
<%
  Hashtable statushash = new Hashtable();

  statushash.put("begun", "Begun Processing");
  statushash.put("authstarted", "Auth Started");
  statushash.put("proofrequired", "Proof Required");
  statushash.put("authsuccessful", "Auth Successful");
  statushash.put("authfailed", "Auth Failed");
  statushash.put("capturestarted", "Capture Started");
  statushash.put("capturesuccess", "Capture Successful");
  statushash.put("capturefailed", "Capture Failed");
  statushash.put("podsent", "POD Sent ");
  statushash.put("settled", "Settled");
  statushash.put("markedforreversal", "Reversal Request Sent");
  statushash.put("reversed", "Reversed");
  statushash.put("chargeback", "Chargeback");
  statushash.put("failed", "Validation Failed");
  statushash.put("cancelled", "Cancelled Transaction");
  statushash.put("authcancelled", "Authorisation Cancelled");
  statushash.put("cancelstarted","Cancel Initiated");


  //Hashtable gatewayHash = GatewayTypeService.getGatewayTypes();

  String gateway = Functions.checkStringNull(request.getParameter("gateway"));
  if (gateway == null)
  {
    gateway = "";}

  String desc = Functions.checkStringNull(request.getParameter("desc"));
  if (desc == null)
    desc = "";

  String amt = Functions.checkStringNull(request.getParameter("amount"));
  if (amt == null)
    amt = "";

  String templateamount = Functions.checkStringNull(request.getParameter("templateamount"));
  if (templateamount == null)
    templateamount = "";

  String emailaddr = Functions.checkStringNull(request.getParameter("emailaddr"));
  if (emailaddr == null)
    emailaddr = "";

  String partnerid = Functions.checkStringNull(request.getParameter("partnerid"));
  if (partnerid == null)
    partnerid = "";

  String orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"));
  if (orderdesc == null)
    orderdesc = "";

  String name = Functions.checkStringNull(request.getParameter("name"));
  if (name == null)
    name = "";

  String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
  if (trackingid == null)
    trackingid = "";

  String toid = Functions.checkStringNull(request.getParameter("toid"));
  if (toid == null)
    toid = "";

  String remark = Functions.checkStringNull(request.getParameter("remark"));
  if (remark == null)
    remark = "";

  String rejectreason = Functions.checkStringNull(request.getParameter("rejectreason"));
  if (rejectreason == null)
    rejectreason = "";

  String cardholderip = Functions.checkStringNull(request.getParameter("cardholderip"));
  if (cardholderip == null)
    cardholderip = "";

  String firstsix = Functions.checkStringNull(request.getParameter("firstsix"));
  String lastfour = Functions.checkStringNull(request.getParameter("lastfour"));
  if (firstsix == null)
    firstsix = "";
  if (lastfour == null)
    lastfour = "";

  String phone= Functions.checkStringNull(request.getParameter("phone"));
  if (phone == null)
    phone= "";

  String bankAccountNumber= Functions.checkStringNull(request.getParameter("bankAccountNumber"));
  if (bankAccountNumber == null)
    bankAccountNumber= "";

  String fdate=null;
  String tdate=null;
  String fmonth=null;
  String tmonth=null;
  String fyear=null;
  String tyear=null;

 /* if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";*/
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
    log.error("Date Format Exception while select");
  }

  String startTime        = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime          = Functions.checkStringNull(request.getParameter("endtime"));

  Functions functions=new Functions();

  Calendar rightNow = Calendar.getInstance();
  String str = "";
  //rightNow.setTime(new Date());
  String currentyear=""+rightNow.get(Calendar.YEAR);
  if (fdate == null) fdate = "" + 1;
  if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
  if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
  if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
  if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
  if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);
  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";

  if (fdate != null) str = str + "fdate=" + fdate;
  if (tdate != null) str = str + "&tdate=" + tdate;
  if (fmonth != null) str = str + "&fmonth=" + fmonth;
  if (tmonth != null) str = str + "&tmonth=" + tmonth;
  if (fyear != null) str = str + "&fyear=" + fyear;
  if (tyear != null) str = str + "&tyear=" + tyear;
  if (startTime != null) str = str + "&starttime=" + startTime;
  if (endTime != null) str = str + "&endtime=" + endTime;
  if (desc != null) str = str + "&description=" + desc;
  if (amt != null) str = str + "&amount=" + amt;
  if (emailaddr != null) str = str + "&emailaddr=" + emailaddr;
  if (name != null) str = str + "&name=" + name;
  if (trackingid != null) str = str + "&STrackingid=" + trackingid;
  if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
  if (rejectreason != null) str = str + "&rejectreason=" + rejectreason;
  if (toid != null) str = str + "&toid=" + toid;
  if (firstsix != null) str = str + "&firstsix=" + firstsix;
  if (lastfour != null) str = str + "&lastfour=" + lastfour;
  if (remark !=null) str = str + "&remark=" + remark;
  if (cardholderip !=null) str = str + "&cardholderip=" + cardholderip;
  if (phone!= null)str = str+ "&phone=" + phone;
  if (bankAccountNumber!= null)str = str+ "&bankAccountNumber=" + bankAccountNumber;

%>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<html>
<head>


  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <title>Transactions</title>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }
    function validateccnum()
    {
      var firstsix = document.form.firstsix.value;
      var lastfour= document.form.lastfour.value;
      if(firstsix.length==0 && lastfour.length==0 )
        return true;
      if(firstsix.length<4)
      {
        alert("Enter first Six card Number");
        return false;
      }

      if( lastfour.length<4)
      {
        alert("Enter last four Card Number");
        return false;
      }
    }
  </script>
</head>

<body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    try
    {
      Logger log = new Logger("transactiondetails.jsp");
%>
<form name="form" method="post" action="/icici/servlet/RejectedTransactionsList?ctoken=<%=ctoken%>" onsubmit="return validateccnum()">
  <div class="row" >
    <div class="col-lg-12" style="position:static;">
      <div class="panel panel-default">
        <div class="panel-heading">
          <p>Rejected Transactions List</p>
          <div style="float: right;">

            <form action="/icici/servlet/RejectedTransactionsList?ctoken=<%=ctoken%>" method="post">
              <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;  margin-top: -23px; background-color: white; color: black;"><i
                      class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;Go Back
              </button>
            </form>
          </div>

        </div>
        <br>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >From</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select size="1" name="fdate" value="" >
                      <%
                        if (fdate != null)
                          out.println(Functions.dayoptions(1, 31, fdate));
                        else
                          out.println(Functions.printoptions(1, 31));
                      %>
                    </select>
                    <select size="1" name="fmonth"  value="" >
                      <%
                        if (fmonth != null)
                          out.println(Functions.newmonthoptions(1, 12, fmonth));
                        else
                          out.println(Functions.printoptions(1, 12));
                      %>
                    </select>
                    <select size="1" name="fyear" value="" >
                      <%
                        if (fyear != null)
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                        else
                          out.println(Functions.printoptions(2005, 2020));
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb">
                    <input type="text" size="6" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>">
                  </td>
                  <%--<td width="4%" class="textb">&nbsp;</td>--%>
                  <td width="5%" class="textb" >To</td>
                  <td width="3%" class="textb"></td>
                  <td width="7%" class="textb">
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
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                        else
                          out.println(Functions.printoptions(2005, 2020));
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb">
                    <input type="text" size="6" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>">
                  </td>
                  <%--<td width="4%" class="textb">&nbsp;</td>--%>
                  <td width="4%" class="textb" >Reject Reason</td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <select size="1" name="rejectreason" class="textb">
                      <option value="">All</option>
                      <%
                        for (TransReqRejectCheck transReqRejectCheck: TransReqRejectCheck.values()){
                          out.println("<option value=\""+transReqRejectCheck.name()+"\">"+transReqRejectCheck.name()+"</option>");
                        }
                      %>
                    </select>
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
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" colspan="2">Card Number</td>
                  <td width="12%" class="textb">
                    <input type=text  name="firstfourofccnum" maxlength="6" size="5"  class="txtbox" style="width:60px">
                    <input type=text name="lastfourofccnum" maxlength="4" size="4" class="txtbox" style="width:60px">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Amount</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="amount" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" class="txtbox" size="10">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Email Id</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="emailaddr" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" class="txtbox" size="20">
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" align="right" style="font-size: 10px; color:#1D7F2C"><b>(Enter First six</b></td>
                  <td width="3%" class="textb" align="center" style="font-size: 10px; color:#1D7F2C"><b>&</b></td>
                  <td width="12%" class="textb" align="left" style="font-size: 10px; color: #1D7F2C"><b>Last Four Credid Card No)</b></td>

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
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Merchant ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="toid" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" class="txtbox" size="10">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Name</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="txtbox" size="10">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Description</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="description" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" class="txtbox" size="10">
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
                  <td width="8%" class="textb" >Partner ID</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input name="partnerid" id="pid1" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerid)%>" class="txtbox" autocomplete="on" >
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Phone Number</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text" name="phone" maxlength="20" class="txtbox"
                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(phone)%>" size="20">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Bank Account Number</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type="text" name="bankAccountNumber" maxlength="20" class="txtbox"
                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankAccountNumber)%>" size="20">
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
                  <td width="12%" class="textb">
                    <input type="checkbox" value="yes" name="perfectmatch"><font color="#1D7F2C">&nbsp;&nbsp;<b>Show Perfect Match</b></font>
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
                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform">
                      Search
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
                  <td width="12%" class="textb"></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</form>
<%
  Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
  Hashtable innerhash = null;
  if (hash != null && hash.size() > 0)
  {
    String style = "class=tr0";

    innerhash = (Hashtable) hash.get(1 + "");
    int pos = 0;
    String value = (String) innerhash.get("id");

%>
<div style="margin-left: -3px;margin-right: -50px;">
  <div class="col-lg-2 col-sm-6" style="width: 25%;margin-left: 224px">
    <table border="1" cellpadding="5" cellspacing="0" style="" align="center" class="table table-striped table-bordered table-green dataTable">
      <tr <%=style%>  >
        <td class="th0" colspan="2" align="center">Transaction Details</td>
      </tr>
      <tr <%=style%>  >
        <td class="td0" width="10%">Tracking ID: </td>
        <td class="td0" width="10%"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("amount");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Transaction Amount: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("transactiontime");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Date of transaction: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("description");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Description: </td>
        <td class="td0"><%=value%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("orderdescription");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Order Description: </td>
        <td class="td0"><%=value%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        if(innerhash.get("remark").toString().contains("<BR>"))
        {
          value = ((String)innerhash.get("remark")).replaceAll("<BR>","\n");
        }
        else
          value = (String)innerhash.get("remark");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Remark </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("rejectreason");
        if(innerhash.get("rejectreason").toString().contains("<BR>"))
        {
          value = ((String)innerhash.get("rejectreason")).replaceAll("<BR>","\n");
        }
        if (value == null || value =="" || value =="null")
          value = "-";

      %>
      <tr <%=style%> >
        <td class="td0">Rejected Reason </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("httpheader");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Http Header </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("requestedhost");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Request Host </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("requestedip");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Requested IP </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("requestedReferer");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Requested Referer </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("transactionType");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Transaction Type </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("redirecturl");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Redirect URL </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr> <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("notificationUrl");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Notification URL </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("totype");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">ToType</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("attemptThreeD");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%>>
        <td class="td0">Attempt 3D </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("paymentProvider");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Payment Provider </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("language");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Language </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("bic");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">BIC </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("iban");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">IBAN </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("mandateId");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Mandate ID </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("bankName");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Bank Name </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("accountType");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Account Type </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("routingNumber");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Routing Number </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("accountNumber");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Account Number </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("checkNumber");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Check Number </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("bankIfsc");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Bank IFSC </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("bankAccountNumber");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Bank Account Number </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("transferType");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Transfer Type </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("bankAccountName");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Bank Account Name </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("recurringType");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Recurring Type </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("createRegistration");
        if (value == null || value =="" || value =="null")
        {}
        else
        {
      %>
      <tr <%=style%>>
        <td class="td0">Create Registration </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%}%>
    </table>
  </div>

  <div class="col-lg-2 col-sm-6" style="width: 27%;">
    <table border="1" cellpadding="5" cellspacing="0" align="center" class="table table-striped table-bordered table-green dataTable">
      <tr <%=style%>  >
        <td class="th0" align="center" colspan="2">Customer's Details</td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String)innerhash.get("firstname");
        String value1 = (String) (innerhash.get("lastname"));
        if (value == null ||  value =="" || value =="null" || value1 == null ||  value1 =="" || value1 =="null")
        {
          value = "-";
          value1="-";
        }
        else
          value=value+" " +value1;
      %>
      <tr <%=style%> >
        <td class="td0" width="10%">Cardholder's Name: </td>
        <td class="td0" width="10%"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        /*String card=String.valueOf(innerhash.get("cardnumber"));
        if(card == null || card=="" || card=="null")
        {
          value = "-";
        }
        else
        {
          value= PzEncryptor.decryptPAN(card);
          value = value.substring(0,6)+"******"+value.substring(value.length()-4,value.length());
        }*/
        //Functions functions = new Functions();
        String first_six=(String) innerhash.get("firstsix");
        String last_four=(String) innerhash.get("lastfour");
        if(!functions.isValueNull(first_six)){
          first_six="-";
        }
        if(!functions.isValueNull(last_four)){
          last_four="-";
        }

      %>
      <%--<tr <%=style%> >
        <td class="td0">Card Number: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>--%>
      <tr <%=style%> >
        <td class="td0">First Six: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(first_six)%></td>
      </tr>
      <tr <%=style%> >
        <td class="td0">Last Four: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(last_four)%></td>
      </tr>
<%--      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("cvv");
        if (value == null || value =="" || value =="null")
          value = "-";
        else if (ESAPI.validator().isValidInput("cvv", value, "CVV", 4, false))
        {
          value="***";
        }else {
          value = value;
        }
      %>
      <tr <%=style%> >
        <td class="td0">Cvv: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>--%>

<%--      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = String.valueOf(innerhash.get("expirydate"));
        if(value == null || value =="" || value =="null")
        {
          value = "-";
        }
        else
        {
          value =  PzEncryptor.decryptExpiryDate(value);
        }
      %>
      <tr <%=style%> >
        <td class="td0">Expiry Date: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>--%>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("customerId");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Customer ID: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("email");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Customer's Email: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("cardholderip");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Cardholder's IP: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("cardtypeid")+"-"+(String) innerhash.get("paymentBrand");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">CardType ID: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("paymenttypeid")+"-"+(String) innerhash.get("paymentMode");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">PaymentType ID: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("amount");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Amount: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("templateamount");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Template Amount: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("currency");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Currency: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("templatecurrency");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Template Currency: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("country");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Country: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("city");
        if (value == null || value =="" || value == "null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">City: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("state");
        if (value == null || value =="" || value == "null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">State: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("zipcode");
        if (value == null || value =="" || value == "null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Zip Code: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("street");
        if (value == null || value =="" || value == "null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Street: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("phone");
        if (value == null || value =="" || value == "null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Phone: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("birthdate");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Birth Date: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

    </table>
  </div>
  <div class="col-lg-2 col-sm-6" style="width: 27%;">
    <table border="1" cellpadding="5" cellspacing="0" align="center" class="table table-striped table-bordered table-green dataTable">
      <tr <%=style%>  >
        <td class="th0" align="center" colspan="2">Merchant's Details</td>
      </tr>
      <tr <%=style%> >
        <td class="td0" width="7%">Member ID: </td>
        <td class="td0" width="5%"><%=getValue((String)innerhash.get("toid"))%></td>
      </tr>
      <tr <%=style%> >
        <td class="td0" width="7%">sKey: </td>
        <td class="td0" width="5%"><%=getValue((String)innerhash.get("clkey"))%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("login");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Name of Merchant: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("contact_persons");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Contact person: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("contact_emails");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Merchant's Email: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("sitename");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Site URL: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("telno");
        if (value == null || value =="" || value =="null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Merchant's Telephone Number: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("terminalid");
        if (value == null || value =="" || value == "null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Terminal Id: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>

      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("accountId");
        if (value == null || value =="" || value == "null")
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Account Id: </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%></td>
      </tr>
    </table>
  </div>
</div>
<%
      }
      else
      {
        out.println("<div class=\"reporttable\">");
        out.println(Functions.NewShowConfirmation("Sorry", "No Records Found For Tracking Id :" + trackingid));
        out.println("</div>");
      }
    }
    catch (Exception e)
    {
      log.error("Exception while getting transaction details",e);
    }
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
  public String getValue(String data)
  {
    String data1="-";
    Functions functions=new Functions();
    if(functions.isValueNull(data))
    {
      data1=data;
    }
    return data1;
  }
%>