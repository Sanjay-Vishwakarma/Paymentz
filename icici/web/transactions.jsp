<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.directi.pg.core.GatewayAccount,com.directi.pg.core.GatewayAccountService,
                com.directi.pg.core.GatewayType,com.directi.pg.core.GatewayTypeService,org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%!
    private static Logger log=new Logger("transactions.jsp");
%>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/cardtype-issuing-bank.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<%--FontAwesome CDN--%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css" integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g==" crossorigin="anonymous" referrerpolicy="no-referrer" />

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
<% System.out.println("inside transactions.jsp--->");
    try{
    HashMap<String, String> statusFlagHash  = new LinkedHashMap();
    HashMap<String, String> statusHash      = new LinkedHashMap();
    HashMap<String, String> parameter       = new LinkedHashMap();
    HashMap<String, String> transactionModeHM = new LinkedHashMap();

    parameter.put("rrn", "RRN");
    parameter.put("arn", "ARN");
    parameter.put("authorization_code", "AuthCode");

    statusFlagHash.put("isChargeback", "Chargeback");
    statusFlagHash.put("isFraud", "Fraud");
    statusFlagHash.put("isRefund", "Refund");
    statusFlagHash.put("isRollingReserveKept", "RollingReserveKept");
    statusFlagHash.put("isRollingReserveReleased", "RollingReserveReleased");
    statusFlagHash.put("isSettled", "Settled");
    statusFlagHash.put("isSuccessful", "Successful");

    statusHash.put("authfailed", "Auth Failed");
    statusHash.put("authstarted", "Auth Started");
    statusHash.put("authstarted_3D", "Auth Started 3D");
    statusHash.put("authsuccessful", "Auth Successful");
    statusHash.put("authcancelled", "Authorisation Cancelled");
    statusHash.put("begun", "Begun Processing");
    statusHash.put("cancelstarted", "Cancel Initiated");
    statusHash.put("cancelled", "Cancelled Transaction");
    statusHash.put("capturefailed", "Capture Failed");
    statusHash.put("capturestarted", "Capture Started");
    statusHash.put("capturesuccess", "Capture Successful");
    statusHash.put("chargeback", "Chargeback");
    statusHash.put("chargebackreversed", "Chargeback Reversed");
    statusHash.put("partialrefund", "Partial Refund");
    statusHash.put("payoutfailed", "Payout Failed");
    statusHash.put("payoutstarted", "Payout Started");
    statusHash.put("payoutsuccessful", "Payout Successful");
    statusHash.put("payoutuploaded", "Payout Uploaded");
    statusHash.put("podsent", "POD Sent ");
    statusHash.put("proofrequired", "Proof Required");
    statusHash.put("markedforreversal", "Reversal Request Sent");
    statusHash.put("reversed", "Reversed");
    statusHash.put("settled", "Settled");
    statusHash.put("failed", "Validation Failed");
    statusHash.put("payoutcancelstarted", "Payout Cancel Started");
    statusHash.put("payoutcancelsuccessful", "Payout Cancel Successful");
    statusHash.put("payoutcancelfailed", "Payout Cancel Failed");
    statusHash.put("smsstarted", "SMS Started");
    statusHash.put("enrollmentstarted", "Enrollment Started");

    transactionModeHM.put("3Dv1","3Dv1");
    transactionModeHM.put("3Dv2","3Dv2");
    transactionModeHM.put("Non-3D","Non-3D");

    String gateway      = Functions.checkStringNull(request.getParameter("gateway"));
    String accountid    = request.getParameter("accountid")==null?"":request.getParameter("accountid");
    String memberid     = request.getParameter("toid")==null?"":request.getParameter("toid");
    String pgtypeid     = request.getParameter("pgtypeid")==null?"":request.getParameter("pgtypeid");
    String partnerid     = request.getParameter("partnerid")==null?"":request.getParameter("partnerid");
    String desc         = Functions.checkStringNull(request.getParameter("desc"));
    String amt          = Functions.checkStringNull(request.getParameter("amount"));
    String emailaddr    = Functions.checkStringNull(request.getParameter("emailaddr"));
    String orderdesc    = Functions.checkStringNull(request.getParameter("orderdesc"));
    String name         = Functions.checkStringNull(request.getParameter("name"));
    String trackingid   = Functions.checkStringNull(request.getParameter("STrackingid"));
    String toid         = Functions.checkStringNull(request.getParameter("toid"))==null?"":request.getParameter("toid");
    String refundamount = Functions.checkStringNull(request.getParameter("refundamount"));
    String remark       = Functions.checkStringNull(request.getParameter("remark"));
    String cardtype     = Functions.checkStringNull(request.getParameter("cardtype"));
    String cardpresent  = Functions.checkStringNull(request.getParameter("cardpresent"));
    String issuing_bank = Functions.checkStringNull(request.getParameter("issuing_bank"));
    String paymentid    = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
    String dateType     = Functions.checkStringNull(request.getParameter("datetype"));
    String firstfourofccnum = Functions.checkStringNull(request.getParameter("firstfourofccnum"));
    String lastfourofccnum  = Functions.checkStringNull(request.getParameter("lastfourofccnum"));
    String startTime        = Functions.checkStringNull(request.getParameter("starttime"));
    String endTime          = Functions.checkStringNull(request.getParameter("endtime"));
    String perfectmatch     = Functions.checkStringNull(request.getParameter("perfectmatch"));
    String flaghash         = Functions.checkStringNull(request.getParameter("statusflag"));
    String status           = Functions.checkStringNull(request.getParameter("status"));
    String statusType       = Functions.checkStringNull(request.getParameter("statusType"));
    String arn              = request.getParameter("ARN");
    String rrn              = request.getParameter("RRN");
    String auth             = request.getParameter("AUTHORIZATION_CODE");
    String customerid       = Functions.checkStringNull(request.getParameter("customerid"))== null? "" : request.getParameter("customerid");
    String telno            = Functions.checkStringNull(request.getParameter("telno"));
    String telnocc          = Functions.checkStringNull(request.getParameter("telnocc"));
    String transactionMode  = (String) request.getAttribute("transactionMode");
    String bankacc          = Functions.checkStringNull(request.getParameter("bankaccount"));

    Functions functions     = new Functions();

//    if(!functions.isValueNull(request.getParameter("bankaccount"))){
//        bankacc = "";
//    }else
//    {
//        bankacc =   request.getParameter("bankaccount").trim();
//        System.out.println("JSP Bank Account Number ==================> "+bankacc);
//    }

    if(!functions.isValueNull(request.getParameter("ARN")))
    {
        arn = "";
    }
    if(!functions.isValueNull(request.getParameter("RRN")))
    {
        rrn = "";
    }
    if(!functions.isValueNull(request.getParameter("AUTHORIZATION_CODE")))
    {
        auth = "";
    }
    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();

    String str              = "";
    String selectedArchived = "", selectedCurrent = "";
    String archivalString   = "Archived";
    String currentString    = "Current";

    if (gateway == null) gateway = "";
    if (desc == null) desc = "";
    if (amt == null) amt = "";
    if (emailaddr == null) emailaddr = "";
    if (telno == null) telno= "";
    if (telnocc == null)telnocc ="";
    if (orderdesc == null) orderdesc = "";
    if (name == null) name = "";
    if (trackingid == null) trackingid = "";
    if (toid == null) toid = "";
    if (refundamount == null) refundamount = "";
    if (remark == null) remark = "";
    if (cardtype == null) cardtype = "";
    if (issuing_bank == null) issuing_bank = "";
    if (dateType == null) dateType = "";
    if (firstfourofccnum == null)firstfourofccnum = "";
    if (lastfourofccnum == null)lastfourofccnum = "";
    if (partnerid == null)partnerid = "";
    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";
    if (flaghash == null)
    {
        flaghash = "";
    }
    if (status == null)
    {
        status = "";
    }

    if (archive)
    {
        selectedArchived="selected";
        selectedCurrent = "";
    }
    else
    {
        selectedCurrent = "selected";
        selectedArchived="";
    }

    String fdate    = null;
    String tdate    = null;
    String fmonth   = null;
    String tmonth   = null;
    String fyear    = null;
    String tyear    = null;



    try
    {
        fdate   = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
        tdate   = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
        fmonth  = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
        tmonth  =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
        fyear   = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
        tyear   = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
    }
    catch (ValidationException e)
    {
        log.error("Date Format Exception while select");
    }

    Calendar rightNow = Calendar.getInstance();
    String currentyear = "" + rightNow.get(Calendar.YEAR);
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

    if (fdate != null) str = str + "fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;
    if (startTime != null) str = str + "&starttime=" + startTime;
    if (endTime != null) str = str + "&endtime=" + endTime;
    if (desc != null) str = str + "&desc=" + desc;
    if (amt != null) str = str + "&amount=" + amt;
    if (emailaddr != null) str = str + "&emailaddr=" + emailaddr;
    if(telno != null)str= str + "&telno=" + telno;
    if (telnocc!= null)str= str + "&telnocc=" + telnocc;
    if (name != null) str = str + "&name=" + name;
    if (trackingid != null) str = str + "&STrackingid=" + trackingid;
    if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
    if (toid != null) str = str + "&toid=" + toid;
    if (status != null) str = str + "&status=" + status;
    if (firstfourofccnum != null) str = str + "&firstfourofccnum=" + firstfourofccnum;
    if (lastfourofccnum != null) str = str + "&lastfourofccnum=" + lastfourofccnum;
    if (gateway !=null) str = str + "&gateway=" + gateway;
    if (remark !=null) str = str + "&remark=" + remark;
    if (cardtype !=null) str = str + "&cardtype=" + cardtype;
    if (issuing_bank !=null) str = str + "&issuing_bank=" + issuing_bank;
    if (flaghash !=null) str = str + "&statusflag=" + flaghash;
    if (pgtypeid !=null) str = str + "&pgtypeid=" + pgtypeid;
    if (accountid !=null) str = str + "&accountid=" + accountid;
    if (dateType != null) str = str + "&datetype=" + dateType;
    if (arn != null) str = str + "&ARN=" + arn;
    if (rrn != null) str = str + "&RRN=" + rrn;
    if (auth != null) str = str + "&AUTHORIZATION_CODE=" + auth;
    if (cardpresent != null) str = str + "&cardpresent=" + cardpresent;
    if (customerid != null) str = str + "&customerid=" + customerid;
    if (transactionMode != null) str = str + "&transactionMode=" + transactionMode;
    if (partnerid != null) str = str + "&partnerid=" + partnerid;
  //  if (bankacc != null) str = str + "&bankaccount=" + bankacc;
    str = str + "&archive=" + archive;

    int pageno      = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

    TreeMap<String,GatewayType> gatewayTypeTreeMap  = GatewayTypeService.getAllGatewayTypesMap();
    TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Transaction Management> Transactions List</title>
    <script language="javascript" type="text/javascript">
        var expanded2 = false;
        function showaccountcheckbox(event)
        {
            event.stopPropagation();
            var checkboxes = document.getElementById("checkboxes_2");
            if (!expanded2) {
                checkboxes.style.display = "block";
                expanded2 = true;
            } else {
                checkboxes.style.display = "none";
                expanded2 = false;
            }
        }

        if (expanded2)
        {
            var checkboxes = document.getElementById("checkboxes_2");
            checkboxes.style.display = "none";
            expanded2 = false;
        }


        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function validateccnum()
        {

            var firstfourofccnum    = document.form.firstfourofccnum.value;
            var lastfourofccnum     = document.form.lastfourofccnum.value;
            var telcccode           = document.form.telnocc.value;
            var phone               = document.form.telno.value;
            if(firstfourofccnum.length == 0 && lastfourofccnum.length == 0  && telcccode.length == 0  && phone.length == 0)
                return true;
            if(firstfourofccnum.length > 0 && firstfourofccnum.length < 6)
            {
                alert("Enter first six of ccnum");
                return false;
            }
            if(firstfourofccnum.length > 0 && lastfourofccnum.length < 4)
            {
                alert("Enter last four of ccnum");
                return false;
            }
            if(lastfourofccnum.length > 0 && firstfourofccnum.length < 6)
            {
                alert("Enter first six of ccnum");
                return false;
            }
            if(lastfourofccnum.length > 0 && lastfourofccnum.length < 4)
            {
                alert("Enter last four of ccnum");
                return false;
            }
            if(telcccode!= "")
            {
                if(!(/^\S{0,}$/.test(telcccode)))
                {
                    alert("Phone CC cannot contain space");
                    return false;
                }
            }
            if(phone!= "")
            {
                if (!(/^\S{3,}$/.test(phone))) {
                    alert( "Phone number cannot contain space");
                    return false;
                }
            }
        }

        function getExcelFile()
        {
            if(document.getElementById("containrecord"))
            {
                document.exportform.submit();
            }
        }

        function check(){
            if(document.getElementById("statusid").value == "detail")
                document.getElementById("dateid").disabled = true;
            else
                document.getElementById("dateid").disabled = false;
        }
        function copyToClipboard(element) {
            var $temp = $("<input>");
            $("body").append($temp);
            $temp.val(element).select();
            document.execCommand("copy");
            $temp.remove();
        }

    </script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>
</head>
<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        try
        {
%>
<form name="form" method="post" action="/icici/servlet/TransactionDetails?ctoken=<%=ctoken%>" onsubmit="return validateccnum()">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <div class="row" >
        <div class="col-lg-12" style="position:static;">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <p><%=archive ? archivalString : currentString%> Transactions Details</p>
                </div>
                <br>
        <div style="width: 100%;overflow: auto;">
            <%--<br>--%>
            <%--<table align="center" width="65%" cellpadding="2" cellspacing="2">--%>
                <%--<tbody>--%>
                <%--<tr>--%>
                    <%--<td>--%>
                        <%--<%--%>
                            <%--String errormsg1 = (String) request.getAttribute("error");--%>
                            <%--if (errormsg1 != null)--%>
                            <%--{--%>
                                <%--out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");--%>
                            <%--}--%>
                        <%--%>--%>
                    <%--</td>--%>
                <%--</tr>--%>
                <%--</tbody>--%>
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="12">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="7%" class="textb">
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
                                        <input type="text" size="6" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>"/>
                                    </td>
                                    <td width="5%" class="textb">To</td>
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
                                        <input type="text" size="6" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>"/>
                                    </td>
                                    <td width="4%" class="textb" >Status</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select size="1" name="status" class="txtbox">
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
                                    <td width="8%" class="textb">DataSource</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="7%" class="textb">
                                        <select size="1" name="archive" class="txtbox">
                                            <option value="true" <%=ESAPI.encoder().encodeForHTML(selectedArchived)%>>Archives</option>
                                            <option value="false" <%=ESAPI.encoder().encodeForHTML(selectedCurrent)%>>Current</option>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="5%" class="textb" >Tracking Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <input type="text" maxlength="500" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"
                                               name="STrackingid" size="10" class="txtbox">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="4%" class="textb" >Name</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="txtbox" size="10">
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
                                        <input type=text  name="firstfourofccnum" maxlength="6" size="5"  class="txtbox" style="width:60px" value="<%=firstfourofccnum%>">
                                        &nbsp;&nbsp;&nbsp;&nbsp;
                                        <input type=text name="lastfourofccnum" maxlength="4" size="4" class="txtbox" style="width:60px" value="<%=lastfourofccnum%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="5%" class="textb" >Amount</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <input type=text name="amount" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" class="txtbox" size="10">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="4%" class="textb" >Email Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="emailaddr" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" class="txtbox" size="20">
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" align="right"></td>
                                    <td width="3%" class="textb" align="center" ></td>
                                    <td width="7%" class="textb" align="left" style="font-size: 10px; color: #1D7F2C"><b>&nbsp;(Enter First six &nbsp;&&nbsp;  Last Four</b></td>

                                    <td width="4%" class="textb" style="font-size: 10px; color: #1D7F2C"><b>Credit Card No)</b></td>
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
                                    <td width="8%" class="textb">Gateway</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                        <%-- <select size="1" id="bank" name="pgtypeid" class="txtbox" style="width:200px;">
                                                <option value="0" default></option>
                                                <%
                                                    for(String gatewayType : gatewayTypeTreeMap.keySet()){
                                                        String isSelected = "";
                                                        if (gatewayType.equalsIgnoreCase(pgtypeid))
                                                        {
                                                            isSelected = "selected";
                                                        }
                                                %>
                                                <option value="<%=gatewayType%>" <%=isSelected%>><%=gatewayType%>
                                                </option>
                                                <%
                                                    }
                                                %>
                                            </select>--%>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Account Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
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
                                    <td width="4%" class="textb">Remark</td>
                                    <td width="3%" class="textb" > </td>
                                    <td width="12%" class="textb">
                                        <input type=text name="remark" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(remark)%>" class="txtbox" size="50">
                                    </td>


                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Description</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <input type=text name="desc" maxlength="300"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" class="txtbox" size="10">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="5%" class="textb">Order Despription</td>
                                    <td width="3%" class="textb" > </td>
                                    <td width="7%" class="textb">
                                        <input type=text name="orderdesc" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderdesc)%>" class="txtbox" size="20">
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
                                    <td width="4%" class="textb">Card Type</td>
                                    <td width="3%" class="textb" > </td>
                                    <td width="12%" class="textb">
                                        <input name="cardtype" id="ctype" value="<%=cardtype%>" class="txtbox" autocomplete="on">
                                    </td>


                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Issuing Bank Name</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <input name="issuing_bank" id="ibank" value="<%=issuing_bank%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Status Flag</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="statusflag" class="txtbox">
                                            <option value="all">All</option>
                                            <%
                                                Set statusflagSet   = statusFlagHash.keySet();
                                                iterator            = statusflagSet.iterator();
                                                String selected3    = "";
                                                String key3         = "";
                                                String value3       = "";

                                                while(iterator.hasNext())
                                                {
                                                    key3    = (String)iterator.next();
                                                    value3  = statusFlagHash.get(key3);

                                                    if (key3.equals(flaghash))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key3)%>" <%=selected3%>><%=ESAPI.encoder().encodeForHTML(value3)%></option>
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
                                    <td width="4%" class="textb">Payment ID</td>
                                    <td width="3%" class="textb" > </td>
                                    <td width="12%" class="textb">
                                        <input type=text name="paymentid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>" class="txtbox" size="50">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Date Type</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <%
                                            String Config="";
                                            if(functions.isValueNull(statusType) && statusType.equals("detail")){
                                                Config="disabled";
                                            }
                                        %>
                                        <select size="1" name="datetype" class="txtbox" id="dateid" <%=Config%>>
                                            <%
                                                if("TIMESTAMP".equals(dateType))
                                                {%>
                                            <option value="DTSTAMP">DTSTAMP</option>
                                            <option value="TIMESTAMP" SELECTED>TIMESTAMP</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="DTSTAMP" SELECTED>DTSTAMP</option>
                                            <option value="TIMESTAMP">TIMESTAMP</option>
                                            <%}
                                            %>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Card Present</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <select size="1" name="cardpresent" class="txtbox">

                                            <%
                                                if("CP".equals(cardpresent))
                                                {%>
                                            <option value="CP" SELECTED>Yes</option>
                                            <option value="CNP">No</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="CNP" SELECTED>No</option>
                                            <option value="CP">Yes</option>
                                            <%}
                                            %>
                                        </select>
                                    </td>
                                <tr>


                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Parameters</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <select class="txtbox" name="param" onclick="showaccountcheckbox(event)">
                                            <option>Select Parameters</option>
                                        </select>
                                        <div id="checkboxes_2">
                                            <div id="chkbox2" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px">
                                                <%-- <%
                                                 for(String param:parameter.keySet())
                                                 {
                                                     String paramval="";
                                                     Functions functions = new Functions();
                                                     if(functions.isValueNull(request.getParameter("param_"+param+"")))
                                                     {
                                                         paramval = Functions.checkStringNull(request.getParameter("param_" + param + ""));
                                                     }
                                             %>
                                             &lt;%&ndash;<option value="<%=paymap.get(paymode)%>"> <%=paymode%></option>&ndash;%&gt;
--%>
                                                ARN&nbsp;&nbsp;&nbsp; <input type="text" class="textb" name="ARN" style="font-size: 15px;" value="<%=arn%>"><br><br>
                                                RRN&nbsp;&nbsp;&nbsp; <input type="text" class="textb" name="RRN" style="font-size: 15px;" value="<%=rrn%>"><br><br>
                                                AuthCode&nbsp;&nbsp; <input type="text" class="textb" name="AUTHORIZATION_CODE" style="font-size: 15px;" value="<%=auth%>"><br><br>


                                            </div>
                                        </div>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="4%" class="textb" >Status Type</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select size="1" name="statusType" class="txtbox" id="statusid" onchange="check()">
                                            <%
                                                String select="";
                                                if(functions.isValueNull(statusType) && statusType.equals("detail")){
                                                    select="selected";
                                                }
                                            %>
                                            <option value="main">Main</option>
                                            <option value="detail" <%=select%> >Detail</option>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="5%" class="textb">CustomerId</td>
                                    <td width="3%" class="textb" > </td>
                                    <td width="7%" class="textb">
                                        <input type=text name="customerid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(customerid)%>" class="txtbox" size="20">
                                    </td>
                                </tr>
                                <tr>


                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Transaction Mode</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <select size="1" name="transactionMode" class="txtbox">
                                            <option value="">All</option>
                                            <%
                                                for(String trnsModeKey : transactionModeHM.keySet()){

                                               String modStr = transactionModeHM.get(trnsModeKey);
                                               if(modStr.equalsIgnoreCase(transactionMode)){
                                            %>
                                                <option value="<%=modStr%>" SELECTED  ><%=modStr%></option>
                                            <% }else{ %>
                                                <option value="<%=modStr%>" ><%=modStr%></option>
                                           <% }}
                                            %>
                                        </select>
                                    </td>
                                    <%--<td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">&nbsp;</td>--%>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="4%" class="textb">Phone Number</td>
                                    <td width="3%" class="textb" > </td>
                                    <td width="12%" class="textb">
                                        <input type=text  name="telnocc" id="telnocc" maxlength="5" size="5"  class="txtbox" style="width:40px" value="<%=telnocc%>">
                                        <input type=text name="telno" id="telno" maxlength="20" size="20" class="txtbox" style="width:90px" value="<%=telno%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="5%" class="textb" >Partner Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <input name="partnerid" id="pid1" value="<%=partnerid%>" class="txtbox" autocomplete="on">
                                    </td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <%--<td width="8%" class="textb"> Bank Account Number</td>--%>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <%--<input name="bankaccount" value="<%= ESAPI.encoder().encodeForHTMLAttribute(bankacc) %>" class="txtbox" />--%>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >
                                        <input type="checkbox" value="yes" name="perfectmatch"><font color="#1D7F2C">&nbsp;&nbsp;<b>Show Perfect Match</b></font>
                                    </td>
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
                                    <td width="7%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">&nbsp;</td>

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
    </div>
</form>
<div class="reporttable">
    <%
        Hashtable hash              = (Hashtable) request.getAttribute("transactionsdetails");
        HashMap trackinghash        = (HashMap) request.getAttribute("TrackingIDList1");
        List<String> trackingList   = new ArrayList<String>();
        Hashtable transpayout_details   = (Hashtable)request.getAttribute("transpayoutdetails");
        Hashtable innerhash_pay         = null;
        String bankname                 = "";
        String bankaccount              = "";
        String ifsc                     = "";

        if (transpayout_details!= null && transpayout_details.size()>0)
        {
            innerhash_pay   = (Hashtable)transpayout_details.get(1 + "");
        }
        if (innerhash_pay != null)
        {
            bankname        = (String)innerhash_pay.get("fullname");
            bankaccount     = (String)innerhash_pay.get("bankaccount");
            ifsc            = (String)innerhash_pay.get("ifsc");
        }
        Hashtable temphash  = null;
        Hashtable temphash2  = null;
        HashMap temphash1   = null;
        String error        = (String ) request.getAttribute("errormessage");
        String dateRangeIsValid        = (String ) request.getAttribute("dateRangeIsValid");
        if(error != null || dateRangeIsValid != null)
        {
            out.println("<b>");
            if(functions.isValueNull(error))
            {
                out.println(error+"&nbsp");
            }
            if(functions.isValueNull(dateRangeIsValid))
            {
                out.println(dateRangeIsValid);
            }
            out.println("</b>");

        }
        if (hash != null && hash.size() > 0)
        {
            int records         = 0;
            int totalrecords    = 0;
            int trackingRecords = 0;
            int currentblock    = 1;
//            String bankaccn     = "";
//
            try
            {
                records         = Integer.parseInt((String) hash.get("records"));
               //trackingRecords = Integer.parseInt((String) trackinghash.get("records"));
                totalrecords    = Integer.parseInt((String) hash.get("totalrecords"));
//                if(functions.isValueNull((String)hash.get("bankaccount")))
//                {
//                 bankaccn=(String)hash.get("bankaccount");
//                }
                if(request.getParameter("currentblock")!=null)
                {
                    currentblock = Integer.parseInt(request.getParameter("currentblock"));
                }
            }
            catch (NumberFormatException ex)
            {
                log.error("Records & TotalRecords is found null",ex);
            }
            if(totalrecords>0)
            {
                for (int pos = 1; pos <= totalrecords; pos++)
                {
                    String id = Integer.toString(pos);

                    temphash1           = (HashMap) trackinghash.get(id);
                    String icicitransid =(String)temphash1.get("trackingid");
                    trackingList.add(icicitransid);
                }
            }

            session.setAttribute("TrackingIDList",trackingList);
            str             = str + "&SRecords=" + pagerecords;
            str             = str + "&ctoken=" + ctoken;
            String style    = "class=td0";
            String ext      = "light";
            if (records > 0)
            {

    %>
    <br>
    <div class="pull-right">
        <form  action="ExportActionHistory?ctoken=<%=ctoken%>" method="post" >
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" name="name">
            <%
                if (!request.getParameter("toid").equalsIgnoreCase("0"))
                {
            %>
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" name="toid">
            <%
                }
            %>
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" name="amt">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderdesc)%>" name="orderdesc">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" name="emailaddr">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstfourofccnum)%>" name="firstfourofccnum">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfourofccnum)%>" name="lastfourofccnum">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(remark)%>" name="remark">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fromdate">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="todate">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundamount)%>" name="refundamount">
            <input type="hidden" value="<%=archive%>" name="archive">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(perfectmatch)%>" name="perfectmatch">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgtypeid)%>" name="pgtypeid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>" name="accountid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardtype)%>" name="cardtype">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(issuing_bank)%>" name="issuing_bank">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>" name="paymentid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardpresent)%>" name="cardpresent">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(arn)%>" name="arn">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rrn)%>" name="rrn">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(auth)%>" name="authorization_code">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardpresent)%>" name="cardpresent">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(customerid)%>" name="customerId">
            <button class="button3"  style="background: transparent; border-radius: 25px;">
                <b>Export History</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png">
            </button>
        </form>
    </div>
    &nbsp;&nbsp;
    <div class="pull-right">
        <form name="exportform" method="post" action="ExportTransactions?ctoken=<%=ctoken%>" >
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" name="name">
            <%
                if (!request.getParameter("toid").equalsIgnoreCase("0"))
                {
            %>
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" name="toid">
            <%
                }
            %>
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" name="amt">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderdesc)%>" name="orderdesc">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" name="emailaddr">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstfourofccnum)%>" name="firstfourofccnum">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfourofccnum)%>" name="lastfourofccnum">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(remark)%>" name="remark">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundamount)%>" name="refundamount">
            <input type="hidden" value="<%=archive%>" name="archive">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(perfectmatch)%>" name="perfectmatch">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgtypeid)%>" name="pgtypeid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>" name="accountid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardtype)%>" name="cardtype">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(issuing_bank)%>" name="issuing_bank">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>" name="paymentid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardpresent)%>" name="cardpresent">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(arn)%>" name="arn">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rrn)%>" name="rrn">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(auth)%>" name="authorization_code">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(statusType)%>" name="statusType">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(customerid)%>" name="customerid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(transactionMode)%>" name="transactionMode">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" name="telno">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(telnocc)%>" name="telnocc">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(flaghash)%>" name="statusflag">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerid)%>" name="partnerid">
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankname)%>" name="bankname">
            <%--<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankaccn)%>" name="bankaccount">--%>
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(ifsc)%>" name="ifsc">

            <%
                if (request.getParameter("pgtypeid")!=null)
                {
            %>
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("pgtypeid"))%>" name="gateway">
            <%

                }
//            if (!request.getParameter("accountid").equalsIgnoreCase("0"))
//            {
            %>
            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("accountid"))%>" name="accountid">
            <%--<%
                }
            %>--%>
            <button type="submit" class="button3" style="background: transparent; border-radius: 25px;"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
        </form>
    </div>
    <br>
    <div id="containrecord"></div>
        <div style="width:100%; overflow: auto">
    <table border="1" cellpadding="5" cellspacing="0" width="750" align="center" class="table table-striped table-bordered table-hover table-green dataTable" style="width: 100%;overflow: auto;">
        <thead>
        <tr>
            <td width="7%" class="th0">Transaction Date</td>
            <%if("CP".equals(cardpresent)){
            %>
            <td width="7%" class="th0">Transaction Fetched Date</td>
            <%
                }
            %>
            <%if("detail".equals(statusType)){
            %>
            <td width="7%" class="th0">Detail Transaction Time</td>
            <%
                }
            %>

            <td width="10%" class="th1">Tracking ID</td>
            <td  class="th1" style="border-left-style: hidden;"></td>
            <td width="4%" class="th1">Partner Name</td>
            <td width="7%" class="th0">Description</td>
            <td width="7%" class="th0">Account ID</td>
            <td width="7%" class="th0">Member ID</td>
            <%--<td width="10%" class="th1">Order Description</td>--%>
            <td width="7%" class="th0">Card Holder's Name</td>
            <td width="7%" class="th0">Brand</td>
            <td width="7%" class="th0">Transaction Mode</td>
            <td width="7%" class="th1">Amount</td>
            <td width="7%" class="th0">Refund Amount</td>
            <td width="7%" class="th0">Status</td>
            <%if("detail".equals(statusType)){
            %>
            <td width="7%" class="th0">Detail Status</td>
            <td width="7%" class="th0">Detail Amount</td>
            <%
                }
            %>
            <td width="12%" class="th0">Remark</td>
            <td width="7%" class="th0">CustomerId</td>
            <td width="5%" class="th0">PhoneCC</td>
            <td width="7%" class="th0">Phone Number</td>
            <td width="7%" class="th0">Response Code</td>
        </tr>
        </thead>
        <%
            for (int pos = 1; pos <= records; pos++)
            {
                String id   = Integer.toString(pos);
                int srno    = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                style       = "class=\"tr" + pos % 2 + "\"";

                temphash = (Hashtable) hash.get(id);

                //String date = Functions.convertDtstampToDBFormat((String) temphash.get("dtstamp"));
                String date             = (String) temphash.get("dtstamp");
                String transactionTime  = (String) temphash.get("transactionTime");
                String description      = (String) temphash.get("description");
                String orderdescription = (String) temphash.get("orderdescription");
                String icicitransid     = (String) temphash.get("trackingid");
                String partnername="";

                if (functions.isValueNull((String)temphash.get("totype")))
                {
                    partnername = (String)temphash.get("totype");
                }
                else
                {
                    partnername = "-";
                }
                String custname     = (String) temphash.get("name");
                String bin_brand    = "";
               /* if (functions.isValueNull((String) temphash.get("bin_brand")))
                {
                    bin_brand = (String) temphash.get("bin_brand");
                }
                else
                {*/
                if(functions.isValueNull((String) temphash.get("cardtype")))
                {
                    bin_brand=(String) temphash.get("cardtype");
                }
                else {
                    bin_brand = "-";
                }
                //}
                //String hrcode = (String) temphash.get("hrcode");
                String remark1      = (String) temphash.get("remark");
                refundamount        = (String) temphash.get("refundamount");
                String currency     = (String) temphash.get("currency");
                String accountId    = (String) temphash.get("accountid");
                String arn1         = (String) temphash.get("arn");
                String rrn1         = (String) temphash.get("rrn");
                String auth1        = (String) temphash.get("authorization_code");
                String customerid1  = (String) temphash.get("customerId");
                String telno1       = (String)temphash.get("telno");
                String telnocc1             = (String)temphash.get("telnocc");
                String memberid1            = (String) temphash.get("toid");
                String transactionGateway   = (String) temphash.get("fromtype");
                String responseCode         = (String) temphash.get("machineid");
                String accountId_gateWay    = "";

                if(!functions.isValueNull(arn1))
                {
                    arn1 = "";
                }
                if(!functions.isValueNull(rrn1))
                {
                    rrn1 = "";
                }
                if(!functions.isValueNull(auth1))
                {
                    auth1 = "";
                }
                if(!functions.isValueNull(accountId))
                {
                    accountId = "";
                }
                if(!functions.isValueNull(remark1))
                {
                    remark1 = "-";
                }
                if(!functions.isValueNull(custname))
                {
                    custname = "-";
                }
                if (!functions.isValueNull(telnocc1))
                {
                    telnocc1 = "-";
                }
                if (!functions.isValueNull(telno1))
                {
                    telno1= "-";
                }
                if (!functions.isValueNull(responseCode))
                {
                    responseCode= "-";
                }

                if(!functions.isValueNull(orderdescription))
                {
                    orderdescription = "-";
                }
                if(!functions.isValueNull(memberid1))
                {
                    memberid1 = "-";
                }
                if(!functions.isValueNull(transactionGateway)){
                    transactionGateway = "";
                }

                String amount       = currency + " " + temphash.get("amount");
                refundamount        = currency + " " + temphash.get("refundamount");
                String tempstatus   = (String) statusHash.get(temphash.get("status"));

                if(functions.isEmptyOrNull(accountId)){
                    accountId_gateWay   = "-";
                }else{
                    accountId_gateWay   = accountId +"-"+ transactionGateway;
                }

                if(!functions.isValueNull(memberid1))
                {
                    memberid1 = "-";
                }
                if(!functions.isValueNull(customerid1))
                {
                    customerid1 = "-";
                }

                String transaction_mode = (String)temphash.get("transaction_mode");

                if(!functions.isValueNull(transaction_mode))
                {
                    transaction_mode = "-";
                }

                out.println("<tr " + style + ">");

                if("CP".equals(cardpresent))
                {
                    out.println("<td align=center >" + transactionTime + "</td>");
                }else{
                    out.println("<td align=center >" + date + "</td>");
                }
                if("CP".equals(cardpresent))
                {
                    out.println("<td align=center >" + date + "</td>");
                }
                if("detail".equals(statusType))
                {
                    out.println("<td align=center >" + temphash.get("detailTimestamp") + "</td>");
                }
                //out.println("<td align=center><form action=\"TransactionDetails?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"action\" value=\"TransactionDetails\"><input type=\"hidden\" name=\"STrackingid\" value=\""+icicitransid+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\"><input type=\"hidden\" name=\"archive\" value=\"false\"><input type=\"hidden\" name=\"accountid\" value=\""+accountId+"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+icicitransid+"\"></form></td>");
                out.println("<td align=center>" +
                        "<form action=\"TransactionDetails?ctoken="+ctoken+"\" method=\"post\">" +
                        "<input type=\"hidden\" name=\"action\" value=\"TransactionDetails\">" +
                        "<input type=\"hidden\" name=\"STrackingid\" value=\""+icicitransid+"\">" +
                        "<input type=\"hidden\" name=\"partnername\" value=\""+partnername+"\">" +
                        "<input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\">" +
                        "<input type=\"hidden\" name=\"archive\" value=\""+archive+"\">" +
                        "<input type=\"hidden\" name=\"accountid\" value=\""+accountId+"\">" +
                        "<input type=\"hidden\" name=\"fdate\" value=\""+fdate+"\">" +
                        "<input type=\"hidden\" name=\"tdate\" value=\""+tdate+"\">" +
                        "<input type=\"hidden\" name=\"fmonth\" value=\""+fmonth+"\">" +
                        "<input type=\"hidden\" name=\"tmonth\" value=\""+tmonth+"\">" +
                        "<input type=\"hidden\" name=\"fyear\" value=\""+fyear+"\">" +
                        "<input type=\"hidden\" name=\"tyear\" value=\""+tyear+"\">" +
                        "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">" +
                        "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">" +
                        "<input type=\"hidden\" name=\"cardpresent\" value=\""+cardpresent+"\">" +
                        "<input type=\"hidden\" name=\"arn\" value=\""+arn1+"\">" +
                        "<input type=\"hidden\" name=\"rrn\" value=\""+rrn1+"\">" +
                        "<input type=\"hidden\" name=\"auth\" value=\""+auth1+"\">" +
                        "<input type=\"hidden\" name=\"status\" value=\""+status+"\">" +
                        "<input type=\"hidden\" name=\"statusType\" value=\""+statusType+"\">" +
                        "<input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+icicitransid+"\">" +
                        "</form>" +
                        "</td>");
                out.println("<td align=center style=\"border-left-style: hidden;\"><span title=\"copy\"  onclick=\"copyToClipboard("+icicitransid+")\" ><i style=\"position: relative;left: -3px;top: -1px;\" class=\"fa-solid fa-copy\"></i></span></td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(partnername) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(description) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(accountId_gateWay) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(memberid1) + "</td>");
                /*out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(orderdescription) + "</td>");*/
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(custname) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(bin_brand) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(transaction_mode) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(refundamount) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");
                if("detail".equals(statusType)){
                    out.println("<td align=center>" + ESAPI.encoder().encodeForHTML((String)temphash.get("detailStatus")) + "</td>");
                    out.println("<td align=center>" + ESAPI.encoder().encodeForHTML((String)temphash.get("detailAmount")) + "</td>");
                }
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(remark1) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(customerid1) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(telnocc1) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(telno1) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(responseCode) + "</td>");
                out.println("</tr>");
            }
        %>
        <thead>
        <tr>
            <td class="th0" align="center" class=textb>Page No:- <%=pageno%></td>
            <td class="th0"align="center" class=textb>Total Records: <%=totalrecords%></td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <%
                if(functions.isValueNull(statusType) && "detail".equals(statusType)){
            %>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <%
                }
            %>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
            <td class="th0">&nbsp;</td>
        </tr>
        </thead>
    </table>
    </div>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="TransactionDetails"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </table>
    <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Sorry", "No records found.<br><br>Date :<br>From " + fdate + "/" + (Integer.parseInt(fmonth) + 1) + "/" + fyear + "<br>To " + tdate + "/" + (Integer.parseInt(tmonth) + 1) + "/" + tyear));
                }
            }
            else
            {

                if(functions.isValueNull((String) request.getAttribute("error")))
                {

                    out.println(Functions.NewShowConfirmation("Sorry","Invalid Bank Account Number. It accepts Only Numeric value"));
                }else
                {
                    out.println(Functions.NewShowConfirmation("Sorry", "No records found"));
                }
            }

        }
        catch (Exception e)
        {
            log.error("Exception while getting the transactions",e);
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
    catch (Exception e){
        e.printStackTrace();
    }
%>
</body>
</html>