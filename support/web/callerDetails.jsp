<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.ShowConfirmation" %>
<%@include file="custSuppDash.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    session.setAttribute("submit","CALLER DETAILS");
    Hashtable statushash = new Hashtable();
    Hashtable callList=(Hashtable)request.getAttribute("callList");
    Hashtable inn;
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
    statushash.put("failed", "Cancelled by Customer");
    statushash.put("cancelled", "Cancelled Transaction");
    statushash.put("authcancelled", "Authorisation Cancelled");

    String str="";

    String fdate = null, tdate = null, fmonth = null, tmonth = null, fyear = null, tyear = null,trackingid=null,toid=null;
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
    trackingid=Functions.checkStringNull(request.getParameter("STrakingid"))==null?"":request.getParameter("STrakingid");
    toid=Functions.checkStringNull(request.getParameter("toid"))==null?"":request.getParameter("toid");
    str=str+"STrakingid="+trackingid+"&toid="+toid;
    Calendar rightNow = Calendar.getInstance();
    if(fdate==null)fdate = "" + 1;
    if(tmonth==null) tdate = "" + rightNow.get(rightNow.DATE);
    if(fmonth==null)fmonth = "" + rightNow.get(rightNow.MONTH);
    if(tmonth==null)tmonth = "" + rightNow.get(rightNow.MONTH);
    if(fyear==null)fyear = "" + rightNow.get(rightNow.YEAR);
    if(tyear==null)tyear = "" + rightNow.get(rightNow.YEAR);

    if (fdate != null) str = str + "&fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear+"&ctoken="+ctoken;



    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

    String currentblock=request.getParameter("currentblock");

    int records = 0,totalrecords = 0;

    if(currentblock==null)
        currentblock="1";

    try
    {
        records=Integer.parseInt(callList.get("records").toString());
        totalrecords=Integer.parseInt((callList.get("totalrecords").toString()));
    }
    catch(Exception ex)
    {

    }

%>
<html>
<head>
    <title>Caller Detail</title>
</head>
<body  class="bodybackground">
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">

<div class="panel-heading">
    Call Detail
    <div style="float: right;">
    <form action="/support/newCaller.jsp?ctoken=<%=ctoken%>" method="POST">

        <button type="submit" class="addnewmember" value="Add Caller" name="submit">
            <i class="fa fa-sign-in"></i>
            &nbsp;&nbsp;Add Caller
        </button>
    </form>
</div>




</div><br><br>
<table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
<form  class="form-horizontal" name="form" method="post" action="/support/servlet/CallerDetail?ctoken=<%=ctoken%>" onsubmit="return validateccnum()"  autocomplete="off">
<input type="hidden" name="ctoken" value="<%=ctoken%>">
<%
    String mes=request.getParameter("MES");
    Hashtable  error=null;
    if(request.getAttribute("error")!=null)
    {
        error= (Hashtable) request.getAttribute("error");
    }

    if(mes!=null)
    {
        if(error!=null && !error.isEmpty() )
        {

            Enumeration enuM = error.keys();
            String key = "";
            String value = "";
            while (enuM.hasMoreElements())
            {
                key = (String) enuM.nextElement();
                value = (String) error.get(key);
                out.println("<center><li class=\"textb\"><b>"+value+"</b></li></center>");
            }
        }
    }
%>
<tr>
<td>

<table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >From</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <select name="fdate" class="textBoxes"  style="font-size:12px; ">
            <%
                if (fdate != null)
                    out.println(Functions.dayoptions(1, 31, fdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select >
        <select  name="fmonth" class="textBoxes" style="font-size:12px; ">
            <%
                if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select   name="fyear" class="textboxes" style="font-size:12px; ">
            <%
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (fyear != null)
                    out.println(Functions.yearoptions(2000, currentYear, fyear));
                else
                    out.println(Functions.printoptions(2000, currentYear));
            %>
        </select>
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >To</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <select name="tdate" class="textboxes" style="font-size:12px; ">

            <%
                if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>
        <select name="tmonth"  class="textboxes" style="font-size:12px; ">

            <%
                if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>

        <select name="tyear" class="textboxes" style="font-size:12px; ">

            <%
                if (tyear != null)
                    out.println(Functions.yearoptions(2000, currentYear, tyear));
                else
                    out.println(Functions.printoptions(2000, currentYear));
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
</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Tracking Id</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="text" value="" name="STrakingid"  class="txtbox" >
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Member Id</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <select size="1" name="toid" class="txtbox" id="dropdown1">

            <option value="">--Select MerchantId--</option>
            <%
                String mId = "";
                Hashtable merchantHash = CustomerSupport.selectMemberIdForDropDown();
                Enumeration merEnum = merchantHash.keys();
                String login = "";
                String selected3 = "";
                while (merEnum.hasMoreElements())
                {
                    mId = (String) merEnum.nextElement();
                    login = (String) merchantHash.get(mId);

                    if(mId.equals(request.getAttribute("merchantid")))
                        selected3 = "selected";
                    else
                        selected3 = "";

            %>
            <option value="<%=mId%>"<%=selected3%>><%=mId%>--<%=login%></option>

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

</tr>


<tr>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <button type="submit" class="buttonform" name="B1" value="">
            <span>
                <i class="fa fa-clock-o"></i>
            </span>
            &nbsp;&nbsp;Search</button>
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



</tr>
</table>
</td>
</tr>
</form>
</table>
</div>
</div>
</div>

<div class="reporttable " >
    <table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%" >
        <tr>
            <td>
               <%
                   if(records>0)
                   {
               %>
                <table border="0" cellpadding="5" cellspacing="2" width="100%" color="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
                    <thead>
                    <tr>
                        <td width="5%" class="th0">Sr No</td>
                        <td width="8%" class="th0">Call Date</td>
                        <td width="7%" class="th1">Tracking Id</td>
                        <td width="5%" class="th1">Merchant Id</td>
                        <td width="9%" class="th1">Merchant Name</td>
                        <td width="10%" class="th1">CardHolder's&nbsp;Name</td>
                        <td width="6%" class="th1">Caller Name</td>
                        <td width="4%" class="th0">Caller&nbsp;email&nbsp;Id</td>
                        <td width="4%" class="th0">Caller&nbsp;phone&nbsp;No</td>
                        <td width="10%" class="th0">Description</td>
                        <td width="10%" class="th1">Remark</td>
                        <td width="10%" class="th1">Status</td>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                            log.debug(" callList::"+callList);
                            for(int i=1;i<=records;i++)
                            {
                                int srno=i+ ((pageno-1)*pagerecords);
                                inn =(Hashtable)callList.get(""+i);
                                String tracking,memberid,merchant,rname,phoneno;
                               if(inn.containsKey("trackingid"))
                               {
                                tracking=ESAPI.encoder().encodeForHTML(inn.get("trackingid").toString());
                               }
                                else
                               {
                                   tracking="";
                               }
                                if(inn.containsKey("memberid"))
                                {
                                memberid=ESAPI.encoder().encodeForHTML(inn.get("memberid").toString());
                                }
                                else
                                {
                                    memberid="";
                                }
                                if(inn.containsKey("merchant"))
                                {
                                merchant=ESAPI.encoder().encodeForHTML(inn.get("merchant").toString());
                                }
                                else
                                {
                                    merchant="";
                                }
                                if(inn.containsKey("rname"))
                                {
                               rname=ESAPI.encoder().encodeForHTML(inn.get("rname").toString());
                                }
                                else{
                                    rname="";
                                }
                                if(inn.containsKey("phoneno"))
                                {
                                phoneno=ESAPI.encoder().encodeForHTML(inn.get("phoneno").toString());
                                }
                                else
                                {
                                    phoneno="";
                                }
                    %>
                    <tr style="font-size: 12px">
                        <td   height="40px" width="2px" class="tr1"><center><%=srno%></center></td>
                        <td   height="40px" width="70px" class="tr1"><center><%=ESAPI.encoder().encodeForHTML(inn.get("dates").toString())%></center></td>
                        <td   height="40px" width="75px" class="tr1"><center><%=tracking%></center></td>
                        <td   height="40px" width="66px" class="tr1"><center><%=memberid%></center></td>
                        <td   height="40px" width="96px" class="tr1"><center><%=merchant%></center></td>
                        <td   height="40px" width="77px" class="tr1"><center><%=rname%></center></td>
                        <td   height="40px" width="79px" class="tr1"><center><%=inn.get("cname").toString()%></center></td>
                        <td   height="40px" width="45px" class="tr1"><center><%=ESAPI.encoder().encodeForHTML(inn.get("email").toString())%></center></td>
                        <td   height="40px" width="45px" class="tr1"><center><%=phoneno%></center></td>
                        <%
                            String desc,remark,status ;
                            if(inn.containsKey("descr"))
                            {
                                 desc=ESAPI.encoder().encodeForHTML(inn.get("descr").toString());
                            }
                            else
                            {
                            desc=" N.A";
                            }
                            if(inn.containsKey("remarks"))
                            {
                                remark=ESAPI.encoder().encodeForHTML(inn.get("remarks").toString());
                            }
                            else
                            {
                                remark=" N.A";
                            }
                            if(inn.containsKey("statuss"))
                            {
                               status=ESAPI.encoder().encodeForHTML(inn.get("statuss").toString());
                            }
                            else
                            {
                                status=" N.A";
                            }

                        %>
                        <td   height="40px" width="95px" class="tr1"><center><%=desc%></center></td>
                        <%--<%System.out.println("AMOUNT"+Capture);%>--%>
                        <td   height="40px" width="95px" class="tr1"><center><%=remark%></center></td>
                        <%--<%System.out.println("AMOUNT"+Refund);%>--%>
                        <td   height="40px" width="102px" class="tr1"><center><%=status%></center></td></tr>
                    <%
                        } %>
                    </tbody>
                    <thead >
                    <td  align="left" class="th0">Total Records : <%=totalrecords%></td>
                    <td  align="right" class="th0">Page No : <%=pageno%></td>
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

                    </thead>


                </table>
            </td>
        </tr>
    </table>
    <br>
    <div>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="CallerDetail"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </div>
    <%}else
    {
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
    }%>
</div>
</body>
</html>