<%@ page import="com.directi.pg.Functions,java.util.Calendar,
                org.owasp.esapi.ESAPI,
                org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.function.Function" %>
<%@ include file="index.jsp" %>
<%!
    private static Logger logger=new Logger("fraudTransactionList.jsp");
%>
<%
    Hashtable statushash = new Hashtable();
    statushash.put("Pending", "Pending");
    statushash.put("Process Failed", "Process Failed");
    statushash.put("Process Successfully", "Process Successfully");

    Hashtable gatewayHash = FraudSystemService.getFraudSystem();
    String fsid =Functions.checkStringNull(request.getParameter("fsid"));
    if (fsid == null)
    {fsid = "";}


    String membertrandid = Functions.checkStringNull(request.getParameter("toid"));
    if (membertrandid == null)
        membertrandid = "";

    String fraudtransid = Functions.checkStringNull(request.getParameter("fraudtransid"));
    if (fraudtransid == null)
        fraudtransid = "";



    String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
    if (trackingid == null)
        trackingid = "";

    String status = Functions.checkStringNull(request.getParameter("fstransstatus"));
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
        logger.error("Date FOrmat Exception",e);
    }
    Calendar rightNow = Calendar.getInstance();
    String str = "";
    //rightNow.setTime(new Date());
    String currentyear=""+rightNow.get(rightNow.YEAR);
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);


    if (fdate != null) str = str + "fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;
    if (trackingid != null) str = str + "&STrackingid=" + trackingid;
    if (membertrandid != null) str = str + "&toid=" + membertrandid;
    if (status != null) str = str + "&fstransstatus=" + status;
    if (fsid !=null) str = str + "&fsid=" + fsid;
    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;

    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Fraud Transaction Details> Fraud transaction list</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>

</head>
<body>

<%
    String memberid=nullToStr(request.getParameter("toid"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        try
        {

%>
<form name="form" method="post" action="/icici/servlet/FraudTransactionList?ctoken=<%=ctoken%>" onsubmit="return validateccnum()">
<div class="row">
    <div class="col-lg-12">
<div class="panel panel-default" >
<div class="panel-heading" >
    Fraud Transactions List
</div>
<input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken" id="ctoken">
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
            <select size="1" name="tyear"  class="textb">
                <%
                    if (tyear != null)
                        out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                    else
                        out.println(Functions.printoptions(2005, 2013));
                %>
            </select>
        </td>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >Fraud ID</td>
        <td width="3%" class="textb"></td>
        <td width="10%" class="textb">
            <input type=text name="fraudtransid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudtransid)%>" class="txtbox" size="20">
        </td>
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
        <td width="8%" class="textb" >Member ID</td>
        <td width="3%" class="textb"></td>
        <td width="12%" class="textb">
            <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
        </td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb">Status</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">
            <select size="1" name="fstransstatus" class="txtbox">
                <option value="">All</option>
                <%
                    Enumeration enu = statushash.keys();
                    String selected = "";
                    String key = "";
                    String value = "";


                    while (enu.hasMoreElements())
                    {
                        key = (String) enu.nextElement();
                        value = (String) statushash.get(key);

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

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb">Fraud System</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">
            <select size="1" name="fsid" class="txtbox">
                <option value="">All</option>
                <%
                    Enumeration enu2 = gatewayHash.keys();
                    String selected2 = "";
                    String key2 = "";
                    String value2 = "";


                    while (enu2.hasMoreElements())
                    {
                        key2 = (String) enu2.nextElement();
                        value2 = (String) gatewayHash.get(key2);

                        if (key2.equals(fsid))
                            selected2 = "selected";
                        else
                            selected2 = "";

                %>
                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key2)%>" <%=selected2%>><%=ESAPI.encoder().encodeForHTML(value2)%></option>
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
        <td width="8%" class="textb" colspan="2">Tracking ID</td>
        <td width="12%" class="textb">
            <input type="text" maxlength="10" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"  name="STrackingid" size="10" class="txtbox">
        </td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" ></td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">

        </td>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" ></td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">
            <button type="submit" class="buttonform">
                <i class="fa fa-clock-o"></i>
                &nbsp;&nbsp;Search
            </button>
        </td>

    </tr>
    <tr>
        <td width="4%" class="textb">&nbsp;</td>
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
        String error=(String ) request.getAttribute("errormessage");
        if(error !=null)
        {
            out.println(error);

        }
        if (hash != null && hash.size() > 0)
        {
            int records = 0;
            int totalrecords = 0;
            int currentblock = 1;

            try
            {
                records=Functions.convertStringtoInt((String)hash.get("records"),15);
                totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
                currentblock = Functions.convertStringtoInt((request.getParameter("currentblock")),1);
            }
            catch (NumberFormatException ex)
            {
                logger.error("Records & TotalRecords is found null",ex);
            }

            str = str + "SRecords=" + pagerecords;
            str = str + "&ctoken=" + ctoken;
            String style = "class=td0";
            String ext = "light";


            if (records > 0)
            {
    %>
    <div id="containrecord"></div>
    <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td width="14%" class="th0">Date</td>
            <td width="7%" class="th1">Fraud ID</td>
            <td width="10%" class="th0">Fraud System</td>
            <td width="10%" class="th1">Fraud Status</td>
            <td width="8%" class="th0">Tracking Id</td>
            <td width="8%" class="th1">MemberId</td>
            <td width="8%" class="th1">AccountId</td>
            <td width="10%" class="th1">Fraud Alert Score</td>
            <td width="10%" class="th1">Auto Reversal Score</td>
            <td width="8%" class="th0">Score</td>
            <td width="17%" class="th0">Amount</td>
            <td width="12%" class="th0">IsAlertSent</td>
            <td width="12%" class="th0">Is Reversed <%--Action Taken--%></td>
            <td width="12%" class="th0">Is Fraud Reverse</td>
        </tr>
        </thead>
        <%

            for (int pos = 1; pos <= records; pos++)
            {
                String id = Integer.toString(pos);

                int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                style = "class=\"tr" + pos % 2 + "\"";

                temphash = (Hashtable) hash.get(id);

                String date =(String)temphash.get("dtstamp");

                Functions functions = new Functions();
                String fraudtransid1 = (String) temphash.get("fraudtransid");
                String fsGateway =FraudSystemService.getFSGateway((String)temphash.get("fsid"));
                String fraudtransstatus = (String) temphash.get("fraudtransstatus");
                String icicitransid = (String) temphash.get("trackingid")==null?"-":(String) temphash.get("trackingid");
                memberid = (String) temphash.get("memberid");
                String accountid = (String) temphash.get("accountid")==null?"-":(String)temphash.get("accountid");
                String maxScore = (String) temphash.get("maxScore");
                if(!functions.isValueNull(maxScore)) {maxScore="";}
                String autoReversalScore = (String) temphash.get("autoReversalScore");
                if(!functions.isValueNull(autoReversalScore)) {autoReversalScore="";}
                String score = (String) temphash.get("score");
                String amount = (String) temphash.get("amount");
                String isAlertsent = (String) temphash.get("isAlertSent");
                String isReversalAction = (String) temphash.get("isRefund")==null?"-":(String) temphash.get("isRefund");
                String isFraud = (String) temphash.get("isFraud")==null?"-":(String) temphash.get("isFraud");

                out.println("<tr " + style + ">");
                out.println("<td >" + date + "</td>");
                out.println("<td align=\"center\"><form action=\"FraudTransactionList?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"fstransid\" value=\""+fraudtransid1+"\"> <input type=\"hidden\" name=\"score\" value=\""+score+"\"> <input type=\"hidden\" name=\"fsid\" value=\""+temphash.get("fsid")+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\")+\"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+fraudtransid1+"\"></form></td>");
                /*out.println("<td align=\"Center\"><a href=\"FraudTransactionList?fstransid=" + ESAPI.encoder().encodeForHTMLAttribute(fstransid)+ "&fsid=" +  ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("fsid")) +"&ctoken="+ctoken+"\">" +fstransid+ "</a></td>");*/
                out.println("<td align=\"Center\">" + ESAPI.encoder().encodeForHTML(fsGateway) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(fraudtransstatus) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(icicitransid) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(memberid) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(accountid) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(maxScore) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(autoReversalScore) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(score) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(isAlertsent) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(isReversalAction) + "</td>");
                out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(isFraud) + "</td>");
                out.println("</tr>");

            }
        %>
        <thead>
        <tr class="th0" style="font-color: #ffffff;">
            <td align="left">Total Records: <%=totalrecords%></td>
            <td align="right">PageNo <%=pageno%></td>
            <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>

        </tr>
        </thead>
    </table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="FraudTransactionList"/>
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
            logger.error("Exception while getting fraud transaction list",e);

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
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>