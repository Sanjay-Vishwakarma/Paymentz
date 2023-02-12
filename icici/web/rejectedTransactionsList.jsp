<%@ page import="com.directi.pg.Functions,
                 com.directi.pg.Logger,
                 com.manager.TerminalManager,
                 com.manager.dao.MerchantDAO,
                 com.manager.enums.TransReqRejectCheck,
                 com.manager.vo.TerminalVO,
                 org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TreeMap" %>
<%!
    private static Logger log = new Logger("rejectedTransactionsList.jsp");
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<%
    try
    {
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
    statushash.put("cancelstarted", "Cancel Initiated");

    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
    if (gateway == null)
    {
        gateway = "";
    }


    String desc = Functions.checkStringNull(request.getParameter("desc"));
    if (desc == null)
        desc = "";

    String amt = Functions.checkStringNull(request.getParameter("amount"));
    if (amt == null)
        amt = "";

    String emailaddr = Functions.checkStringNull(request.getParameter("emailaddr"));
    if (emailaddr == null)
        emailaddr = "";

    String orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"));
    if (orderdesc == null)
        orderdesc = "";

    String name = Functions.checkStringNull(request.getParameter("name"));
    if (name == null)
        name = "";

    String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
    if (trackingid == null)
        trackingid = "";

    String partnerid = Functions.checkStringNull(request.getParameter("partnerid")) == null ? "" : request.getParameter("partnerid");
    String toid = Functions.checkStringNull(request.getParameter("toid"));
    if (toid == null)
    {
        toid = "";
    }
    String customerId = Functions.checkStringNull(request.getParameter("customerId"));
    if (customerId == null)
    {
        customerId = "";
    }
    String remark = Functions.checkStringNull(request.getParameter("remark"));
    if (remark == null)
        remark = "";


    String rejectReasonPara = Functions.checkStringNull(request.getParameter("rejectreason"));
    if (rejectReasonPara == null)
        rejectReasonPara = "";

    String firstsix = Functions.checkStringNull(request.getParameter("firstfourofccnum"));
    if (firstsix == null)
        firstsix = "";

    String lastfour = Functions.checkStringNull(request.getParameter("lastfourofccnum"));
    //String ccnum=Functions.checkStringNull(request.getParameter("ccnum"));
    if (lastfour == null)
        lastfour = "";

        String phone= Functions.checkStringNull(request.getParameter("phone"));
        if (phone == null)
            phone= "";

    String bankAccountNumber= Functions.checkStringNull(request.getParameter("bankAccountNumber"));
    if (bankAccountNumber == null)
        bankAccountNumber= "";

    String fdate = null;
    String tdate = null;
    String fmonth = null;
    String tmonth = null;
    String fyear = null;
    String tyear = null;
    try
    {
        fdate = ESAPI.validator().getValidInput("fdate", request.getParameter("fdate"), "Days", 2, true);
        tdate = ESAPI.validator().getValidInput("tdate", request.getParameter("tdate"), "Days", 2, true);
        fmonth = ESAPI.validator().getValidInput("fmonth", request.getParameter("fmonth"), "Months", 2, true);
        tmonth = ESAPI.validator().getValidInput("tmonth", request.getParameter("tmonth"), "Months", 2, true);
        fyear = ESAPI.validator().getValidInput("fyear", request.getParameter("fyear"), "Years", 4, true);
        tyear = ESAPI.validator().getValidInput("tyear", request.getParameter("tyear"), "Years", 4, true);
    }
    catch (ValidationException e)
    {
        log.error("Date Format Exception while select");
    }

       String startTime        = Functions.checkStringNull(request.getParameter("starttime"));
       String endTime          = Functions.checkStringNull(request.getParameter("endtime"));

        System.out.println("jsp startTime::: "+startTime+ " endTime:::: "+endTime);

    Calendar rightNow = Calendar.getInstance();
    String str = "";
    //rightNow.setTime(new Date());
    String currentyear = "" + rightNow.get(Calendar.YEAR);
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);
    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";
    String status = Functions.checkStringNull(request.getParameter("status"));

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
    if (name != null) str = str + "&name=" + name;
    if (trackingid != null) str = str + "&STrackingid=" + trackingid;
    if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
    if (toid != null) str = str + "&toid=" + toid;
    if (status != null) str = str + "&status=" + status;
    if (firstsix != null) str = str + "&firstfourofccnum=" + firstsix;
    if (lastfour != null) str = str + "&lastfourofccnum=" + lastfour;
    if (gateway != null) str = str + "&gateway=" + gateway;
    if (remark != null) str = str + "&remark=" + remark;
    if (rejectReasonPara != null) str = str + "&rejectreason=" + rejectReasonPara;
    if (customerId != null) str = str + "&customerId=" + customerId;
    if (phone!= null)str = str+ "&phone=" + phone;
    if (bankAccountNumber!= null)str = str+ "&bankAccountNumber=" + bankAccountNumber;

    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;
    String archivalString = "Archived";
    String currentString = "Current";
    String selectedArchived, selectedCurrent;
    if (archive)
    {
        selectedArchived = "selected";
        selectedCurrent = "";
    }
    else
    {
        selectedArchived = "";
        selectedCurrent = "selected";
    }

    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

    TerminalManager terminalManager = new TerminalManager();
    List<TerminalVO> terminalList = terminalManager.getAllMappedTerminalsForCommon();

    MerchantDAO merchantDAO = new MerchantDAO();
    TreeMap<Integer, String> memberTreeMap = merchantDAO.getMemberDetailsForRejectedTransaction();


    TreeMap<String, TerminalVO> memberMap = new TreeMap<String, TerminalVO>();

    for (TerminalVO terminalVO : terminalList)
    {
        String memberKey = terminalVO.getMemberId() + "-" + terminalVO.getAccountId() + "-" + terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
        memberMap.put(memberKey, terminalVO);
    }


%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Rejected Transactions List</title>
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

            var firstfourofccnum = document.form.firstfourofccnum.value;
            var lastfourofccnum = document.form.lastfourofccnum.value;
            var phone= document.form.phone.value;
            if (firstfourofccnum.length == 0 && lastfourofccnum.length == 0 && phone.length==0)
                return true;
            if(firstfourofccnum.length > 0 && firstfourofccnum.length < 6)
            {
                alert("Enter first six of ccnum");
                return false;
            }
            if( lastfourofccnum.length > 0 && lastfourofccnum.length < 4 )
            {
                alert("Enter last four of ccnum");
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
            if(phone!= ""){
                if (!(/^\S{3,}$/.test(phone))) {
                    alert( 'Phone number cannot contain whitespace');
                    return false;
                }
            }
        }

        function getExcelFile()
        {
            if (document.getElementById("containrecord"))
            {
                document.exportform.submit();
            }
        }

    </script>
    <%--<link rel="stylesheet" type="text/css" href="/merchant/style.css"/>--%>
</head>

<body>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        try
        {

%>
<form name="form" method="post" action="/icici/servlet/RejectedTransactionsList?ctoken=<%=ctoken%>"
      onsubmit="return validateccnum()">
    <div class="row">
        <div class="col-lg-12" style="position:static;">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div style="float:right">

                        <form action="/icici/servlet/RejectedTransactionsList?ctoken=<%=ctoken%>" method="post">
                        </form>
                    </div>
                    <p>Rejected Transactions List</p>
                </div>
                <br>
                <table align="center" width="95%" cellpadding="2" cellspacing="2"
                       style="margin-left:2.5%;margin-right: 2.5% ">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="7%" class="textb">
                                        <select size="1" name="fdate" value="">
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" value="">
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" value="">
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
                                    <%--<td width="4%" class="textb">&nbsp;</td>--%>
                                    <td width="5%" class="textb">To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="7%" class="textb">
                                        <select size="1" name="tdate">
                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>

                                        <select size="1" name="tmonth">
                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>

                                        <select size="1" name="tyear">
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
                                    <%--<td width="4%" class="textb">&nbsp;</td>--%>
                                    <td width="4%" class="textb">Reject Reason</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="7%" class="textb">
                                        <select size="1" name="rejectreason" class="textb">
                                            <option value="">All</option>
                                            <%
                                                for (TransReqRejectCheck transReqRejectCheck : TransReqRejectCheck.values())
                                                {
                                                    String isSelected = "";
                                                    if (transReqRejectCheck.name().trim().equals(rejectReasonPara.trim()))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                            %>
                                            <option value="<%=transReqRejectCheck.name()%>" <%=isSelected%>><%=transReqRejectCheck.name()%>
                                            </option>
                                            ;
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" colspan="2">Card Number</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="firstfourofccnum" maxlength="6"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>" size="5"
                                               class="txtbox" style="width:60px">
                                        <input type=text name="lastfourofccnum" maxlength="4"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>" size="4"
                                               class="txtbox" style="width:60px">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Amount</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="amount" maxlength="10"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" class="txtbox"
                                               size="10">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Email Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="emailaddr" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>"
                                               class="txtbox" size="20">
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" align="right" style="font-size: 10px; color:#1D7F2C">
                                        <b>(Enter First six</b></td>
                                    <td width="3%" class="textb" align="center" style="font-size: 10px; color:#1D7F2C">
                                        <b>&</b></td>
                                    <td width="12%" class="textb" align="left" style="font-size: 10px; color: #1D7F2C">
                                        <b>Last Four Credit Card No)</b></td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Merchant ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="toid" id="mid" value="<%=toid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Name</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="name" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="txtbox"
                                               size="10">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Description</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="desc" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" class="txtbox"
                                               size="10">
                                    </td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Partner ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="partnerid" id="pid1" value="<%=partnerid%>" class="txtbox"
                                               autocomplete="on">
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

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>


                                </tr>


                                <tr>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="5%" class="textb" >Tracking Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="7%" class="textb">
                                        <input type="text" maxlength="500" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"
                                               name="STrackingid" size="10" class="txtbox">
                                    </td>



                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >
                                        <input type="checkbox" value="yes" name="perfectmatch"><font color="#1D7F2C">
                                        &nbsp;&nbsp;<b>Show Perfect Match</b></font>
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
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">&nbsp;</td>
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
<div class="reporttable">

    <%

        Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
        Hashtable temphash = null;
        //out.println(hash);
        String error = (String) request.getAttribute("errormessage");
        if (error != null)
        {
            out.println("<b>");
            out.println(error);
            out.println("</b>");

        }
        String message=(String) request.getAttribute("message");
        if (message != null)
        {
            out.println("<center><font class=\"textb\"><b>"+message+"</b></font></center>");
        }
        if (hash != null && hash.size() > 0)
        {
            int records = 0;
            int totalrecords = 0;
            int currentblock = 1;

            try
            {
                records = Integer.parseInt((String) hash.get("records"));
                totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                if (request.getParameter("currentblock") != null)
                {
                    currentblock = Integer.parseInt(request.getParameter("currentblock"));
                }
            }
            catch (NumberFormatException ex)
            {
                log.error("Records & TotalRecords is found null", ex);
            }

            str = str + "SRecords=" + pagerecords;
            str = str + "&ctoken=" + ctoken;
            String style = "class=td0";
            String ext = "light";


            if (records > 0)
            {

    %>
    <form name="exportform" method="post" action="ExportRejectedTransactionsList?ctoken=<%=ctoken%>">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rejectReasonPara)%>" name="rejectreason">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>" name="firstfourofccnum">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>" name="lastfourofccnum">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" name="amount">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" name="emailaddr">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" name="toid">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" name="name">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerid)%>" name="partnerid">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(customerId)%>" name="customerId">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(phone)%>" name="phone">

        <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
    </form>
    <br>

    <div id="containrecord"></div>
    <div style="width:100%; overflow: auto">
    <table border="1" cellpadding="5" cellspacing="0" width="750" align="center"
           class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td width="7%" class="th0">Date</td>
            <td width="7%" class="th0">Tracking Id</td>
            <td width="7%" class="th1">Merchant Id</td>
            <td width="7%" class="th0">ToType</td>
            <td width="10%" class="th1">Description</td>
            <td width="7%" class="th1">Amount</td>
            <td width="7%" class="th0">First Name</td>
            <td width="7%" class="th0">Last Name</td>
            <td width="7%" class="th0">Currency</td>
            <td width="7%" class="th0">Email</td>
            <td width="10%" class="th1">IP Address</td>
            <td width="12%" class="th0">Reject Reason</td>
            <td width="10%" class="th1">Terminal Id</td>
            <td width="10%" class="th1">customer Id</td>
            <td width="10%" class="th1">Phone Number</td>
            <%--<td width="12%" class="th0">Remark</td>--%>
        </tr>
        </thead>
        <%
            Functions functions = new Functions();
            for (int pos = 1; pos <= records; pos++)
            {
                String id = Integer.toString(pos);

                int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                style = "class=\"tr" + pos % 2 + "\"";

                temphash = (Hashtable) hash.get(id);
                String date = Functions.convertDtstampToDBFormat((String) temphash.get("dtstamp"));
                String searchId = (String) temphash.get("id");
                String merchantId = (String) temphash.get("toid");
                String toType = (String) temphash.get("totype");
                String description = (String) temphash.get("description");
                String amount = (String) temphash.get("amount");
                String terminalId = (String) temphash.get("terminalid");
                String firstName = (String) temphash.get("firstname");
                String lastName = (String) temphash.get("lastname");
                String Currency = (String) temphash.get("currency");
                String email = (String) temphash.get("email");
                String requestedIp = (String) temphash.get("requestedip");
                String rejectReason = (String) temphash.get("rejectreason");
                String vpa = (String) temphash.get("customerId");
                String Phone= (String) temphash.get("phone");
                //String remark1 =(String) temphash.get("remark");

                if (functions.isValueNull((String) temphash.get("rejectreason")) && temphash.get("rejectreason").toString().contains("<BR>"))
                {
                    rejectReason = ((String) temphash.get("rejectreason")).replaceAll("<BR>", "\n");
                }
                else if (!functions.isValueNull(rejectReason))
                {
                    rejectReason = "-";
                }

                if (!functions.isValueNull(remark))
                {
                    remark = "-";
                }
                if (!functions.isValueNull(requestedIp))
                {
                    requestedIp = "-";
                }
                if (!functions.isValueNull(amount))
                {
                    amount = "-";
                }
                if (!functions.isValueNull(terminalId))
                {
                    terminalId = "-";
                }
                if (!functions.isValueNull(description))
                {
                    description = "-";
                }
                if (!functions.isValueNull(firstName))
                {
                    firstName = "-";
                }
                if (!functions.isValueNull(lastName))
                {
                    lastName = "-";
                }
                if (!functions.isValueNull(Currency))
                {
                    Currency = "-";
                }
                if (!functions.isValueNull(email))
                {
                    email = "-";
                }
                if (!functions.isValueNull(toType))
                {
                    toType = "-";
                }
                if (!functions.isValueNull(merchantId))
                {
                    merchantId = "-";
                }

                if (!functions.isValueNull(vpa))
                {
                    vpa = "-";
                }
                if (!functions.isValueNull(Phone))
                {
                    Phone ="-";
                }
                out.println("<tr " + style + ">");
                out.println("<td align=center >" + date + "</td>");
                out.println("<td align=center><form action=\"RejectedTransactionsList?ctoken=" + ctoken + "\" method=\"post\">" +
                        "<input type=\"hidden\" name=\"action\" value=\"RejectedTransactionsList\"><input type=\"hidden\" name=\"STrackingid\" value=\"" + searchId + "\">" +
                        "<input type=\"hidden\" name=\"fdate\" value=\"" + fdate + "\">" +
                        "<input type=\"hidden\" name=\"fmonth\" value=\"" + fmonth + "\">" +
                        "<input type=\"hidden\" name=\"fyear\" value=\"" + fyear + "\">" +
                        "<input type=\"hidden\" name=\"startTime\" value=\"" + startTime + "\">" +
                        "<input type=\"hidden\" name=\"tdate\" value=\"" + tdate +"\">" +
                        "<input type=\"hidden\" name=\"tmonth\" value=\"" + tmonth + "\">" +
                        "<input type=\"hidden\" name=\"tyear\" value=\"" + tyear + "\">" +
                        "<input type=\"hidden\" name=\"endTime\" value=\"" + endTime + "\">" +
                        "<input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\"><input type=\"hidden\" name=\"archive\" value=\"false\"><input type=\"hidden\" name=\"accountid\" value=\"" + searchId + "\">" +
                        "<input type=\"submit\" class=\"goto\" name=\"submit\" value=\"" + searchId + "\"></form></td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(merchantId) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(toType) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(description) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(firstName) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(lastName) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(Currency) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(email) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(requestedIp) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(rejectReason) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(terminalId) + "</td>");
                 out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(vpa) + "</td>");
                out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(Phone) + "</td>");
                out.println("</tr>");
            }
        %>
        <thead>
        <tr>
            <td class="th0" align="center" class=textb>Page No:- <%=pageno%></td>
            <td class="th0" align="center" class=textb>Total Records: <%=totalrecords%></td>
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

        </tr>
        </thead>
    </table>
   </div>
    <table align=center valign=top>
        <tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                    <jsp:param name="numrows" value="<%=pagerecords%>"/>
                    <jsp:param name="pageno" value="<%=pageno%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="RejectedTransactionsList"/>
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
                out.println(Functions.NewShowConfirmation("Sorry", "No records found"));
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting the transactions", e);
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