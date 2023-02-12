<%@ page errorPage="error.jsp"%>
<%@ page import="com.directi.pg.Functions,com.logicboxes.util.ApplicationProperties,java.math.BigDecimal,java.util.Calendar,
                 org.owasp.esapi.errors.ValidationException,
                 org.owasp.esapi.User" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI"  %>

<%@ include file="ietest.jsp" %>


<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Accounts");

%>
<html lang="en">
<head>

    <script type="text/javascript"src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript"src='/merchant/css/new/respond.min.js'></script>
    <title><%=company%> Merchant Accounts</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>


    <script language="JavaScript" type="text/javascript">
        /*<![CDATA[*/
        var Lst;

        function CngClass(obj){
            if (Lst) Lst.className='';
            obj.className='selected';
            Lst=obj;
            alert("click");


        }

        /*]]>*/
    </script>
    <link href="/merchant/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

</head>

<body>

<%@ include file="Top.jsp" %>

<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String currency = (String) session.getAttribute("currency");
    String desc = Functions.checkStringNull(request.getParameter("desc"));
    String str = "";


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
        tmonth = ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);


    }
    catch(ValidationException e)
    {


    }
    Calendar rightNow = Calendar.getInstance();
    String currentyear= ""+rightNow.get(rightNow.YEAR);

    //rightNow.setTime(new Date());

    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

    if (fdate != null) str = str + "fdate=" + ESAPI.encoder().encodeForHTML(fdate);
    if (tdate != null) str = str + "&tdate=" + ESAPI.encoder().encodeForHTML(tdate);
    if (fmonth != null) str = str + "&fmonth=" + ESAPI.encoder().encodeForHTML(fmonth);
    if (tmonth != null) str = str + "&tmonth=" + ESAPI.encoder().encodeForHTML(tmonth);
    if (fyear != null) str = str + "&fyear=" + ESAPI.encoder().encodeForHTML(fyear);
    if (tyear != null) str = str + "&tyear=" + ESAPI.encoder().encodeForHTML(tyear);
    if (desc != null) str = str + "&desc=" + ESAPI.encoder().encodeForHTML(desc);
    str = str + "&ctoken=" + ctoken;
    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;


%>

<form  name="form" method="post" action="Accounts?ctoken=<%=ctoken%>">

    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default" >
                <div class="panel-heading">
                    <%=company%> Merchant Account
                </div>

                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <table  align="center" width="82%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="30%" class="textb" >From</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="25%" class="textb">
                                        <select size="1" name="fdate" class="textb">
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="textb" >
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

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" >To</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="50%" class="textb">

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
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>

                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td  class="textb" >Description</td>
                                    <td  class="textb"></td>
                                    <td  class="textb">

                                        <input type="text"  placeholder="Description" name="desc" maxlength="100" class="txtbox" >

                                    </td>

                                    <td  class="textb">&nbsp;</td>
                                    <td  class="textb" >Rows/Page</td>
                                    <td  class="textb"></td>
                                    <td  class="textb">
                                        <input type="text"  maxlength="5" value="<%=pagerecords%>"  name="SRecords" class="txtbox">

                                    </td>

                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="22%" class="textb" align="center">


                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" align="right">
                                        <button type="submit" name="B1" class="buttonform">
                                            <i class="fa fa-clock-o" class="iconbuttonform"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="22%" class="textb" align="center">
                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" align="right">
                                    </td>
                                </tr>
                                <tr>
                            </table>
                        </tD>
                    </tr>
                </table>

            </div>
        </div>
    </div>
</form>
<div class="reporttable">
<div class="textb">Closing Balance: <%=ESAPI.encoder().encodeForHTML(currency)%> <%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("balance"))%>


<table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%">
<tr><td>
<table border="0" cellpadding="5" cellspacing="2" width="100%" color="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">

<thead>
<tr style="height:30px; ">
    <td width="14%" class="th0">Date</td>
    <td width="15%" class="th1">Transaction ID</td>
    <td width="39%" class="th0">Description</td>
    <td width="10%" class="th1">Debit (<%=currency%>)</td>
    <td width="10%" class="th0">Credit (<%=currency%>)</td>
    <td width="12%" class="th1">Balance (<%=currency%>)</td>
</tr>
</thead>
<%

    BigDecimal balance = new BigDecimal((String) request.getAttribute("cfbalance"));
    BigDecimal debitAmt = new BigDecimal("0");
    BigDecimal creditAmt = new BigDecimal("0");


%>
<tr >
    <td align="center" class="tr0"><%=Functions.convertDtstamptoDate(ESAPI.encoder().encodeForHTML((String) request.getAttribute("fdtstamp")))%></td>
    <td align="center" class="tr0">&nbsp;</td>
    <td align="center" class="tr0">Balance Carry Forwarded</td>
    <td align="center" class="tr0">
        <%
            if (balance.signum() == -1)
            {
                debitAmt = debitAmt.add(balance);
                debitAmt = debitAmt.abs();
                out.println(debitAmt);
            }
            else
            {
                out.println("&nbsp;");
            }
        %>
    </td>
    <td align="center" class="tr0">
        <%
            if (balance.signum() != -1)
            {
                out.println(balance);
                creditAmt = creditAmt.add(balance);
            }
            else
            {
                out.println("&nbsp;");
            }
        %>
    </td>

    <%
        if (desc == null)
        {
            if (balance.signum() == -1)
            {
                out.println("<td align=\"center\" bgcolor=\"#fffeec\" class=\"negbal0\">" + balance + "</td>");
            }
            else
            {
                out.println("<td align=\"center\" class=\"tr0\">" + balance + "</td>");
            }
        }
        else
        {
    %>
    <td align="center" class="tr0">&nbsp;</td>
    <%
        }
    %>
</tr>
<%

    Hashtable hash = (Hashtable) request.getAttribute("Accountsdetails");
    Hashtable temphash = null;
//	out.println(hash);

    int records = 0;
    int totalrecords = 0;
    int currentblock = 1;

    try
    {
        records = Integer.parseInt((String) hash.get("records"));
        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        currentblock = Integer.parseInt(request.getParameter("currentblock"));
    }
    catch (Exception ex)
    {
    }

    String style = "class=td0";
    String style1 = "";
    String ext = "light";

    if (records > 0)
    {

        for (int pos = 1; pos <= records; pos++)
        {
            String id = Integer.toString(pos);

            //int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

            style = "class=\"tr" + pos % 2 + "\"";
            style1 = "class=\"negbal" + pos % 2 + "\"";

            temphash = (Hashtable) hash.get(id);

            String date = Functions.convertDtstamptoDate((String) temphash.get("dtstamp"));


            String description = (String) temphash.get("description");


            String debit = "&nbsp;";
            String credit = "&nbsp;";

            String toid = (String) temphash.get("toid");
            String totype = (String) temphash.get("totype");
            String fromid = (String) temphash.get("fromid");
            String fromtype = (String) temphash.get("fromtype");

            BigDecimal amount = new BigDecimal((String) temphash.get("amount"));

            if (toid.equals(session.getAttribute("merchantid")))
            {
                String name;
                if (("payment").equals(fromtype))
                {
                    name = merchants.getCompany(fromid);
                }
                else
                {
                    name = (String) session.getAttribute("displayname");
                }

                description = "TRF from " + name + " for " + description;

                credit = ESAPI.encoder().encodeForHTML(amount.toString());
                creditAmt = creditAmt.add(amount);
                balance = balance.add(amount);
            }
            if (fromid.equals(session.getAttribute("merchantid")))
            {
                String name;
                if (("payment").equals(totype))
                {
                    name = merchants.getCompany(toid);
                }
                else
                {
                    name = (String) session.getAttribute("displayname");
                }

                if (totype.equals("withdrawal"))
                    description = "Withdrawal (" + description + ")";
                else
                    description = "TRF to " + name + " for " + description;

                debit = ESAPI.encoder().encodeForHTML(amount.toString());
                debitAmt = debitAmt.add(amount);
                balance = balance.subtract(amount);
            }
            //out.println("check this:" + toid+":"+ totype +":" + fromid + ":" + fromtype);
            out.println("<tr >");
            out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date )+ "</td>");
            out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML((String) temphash.get("transid")) + "</td>");
            out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(description) + "</td>");
            out.println("<td align=\"center\"" + style + ">" + debit + "</td>");
            out.println("<td align=\"center\"" + style + ">" + credit + "</td>");
            if (balance.signum() == -1)
                out.println("<td align=\"center\"" + style1 + " >" + balance + "</td>");
            else
                out.println("<td align=\"center\"" + style + " >" + balance + "</td>");
            out.println("</tr>");

        }

%>
<thead>
<tr class="th0">
    <td align="center" class="th1">Total Records : <%=totalrecords%></td>
    <td align="center" class="th0">Page No : <%=pageno%></td>
    <td align="center" class="th1">&nbsp;</td>
    <td align="center" class="th0" >
        <%=debitAmt%>
    </td>
    <td align="center" class="th1" >
        <%=creditAmt%>
    </td>
    <%
        if (desc == null)
        {
            if (balance.signum() == -1)
            {
                out.println("<td align=\"center\"  class=\"th0bal\">" + balance + "</td>");
            }
            else
            {
                out.println("<td align=\"center\"  class=\"th0bal\">" + balance + "</td>");
            }
        }
        else
        {
    %>
    <td align="center">&nbsp;</td>
    <%
        }
    %>
</tr>

</thead>
</table>
</td></tr>
</table>
<jsp:include page="page.jsp" flush="true">
    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
    <jsp:param name="numrows" value="<%=pagerecords%>"/>
    <jsp:param name="pageno" value="<%=pageno%>"/>
    <jsp:param name="str" value="<%=str%>"/>
    <jsp:param name="page" value="Accounts"/>
    <jsp:param name="currentblock" value="<%=currentblock%>"/>
    <jsp:param name="orderby" value=""/>
</jsp:include>

<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation1("Sorry", "No records found for given search criteria.<br><br>Date :<br>From " + fdate + "/" + (Integer.parseInt(fmonth) + 1) + "/" + fyear + "<br>To " + tdate + "/" + (Integer.parseInt(tmonth) + 1) + "/" + tyear));


    }
%>
</div>
</div>
</body>
</html>