<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ include file="custSuppDash.jsp" %>

<%!static final String classname="transactionTrack.jsp";%>
<%

    String mes=request.getParameter("MES");
    String tableName = (String) session.getAttribute("tableName");
    session.setAttribute("submit","TRANSACTION DETAILS");
    String str ;
    String notint1 = (String) request.getAttribute("notint1");
    String notint2 = (String) request.getAttribute("notint2");
    String notint3 = (String) request.getAttribute("notint3");
    String notint4 = (String) request.getAttribute("notint4");
    String isString=(String)request.getAttribute("isString");
    String data = (String) request.getAttribute("data");
    HashMap tranDetail = null;
    tranDetail = (HashMap) request.getAttribute("transactionDetails");

    String STrackingid = Functions.checkStringNull(request.getParameter("STrakingid"))==null?"":request.getParameter("STrakingid");
    String status=Functions.checkStringNull(request.getParameter("status"))==null?"":request.getParameter("status");
    String table=Functions.checkStringNull(request.getParameter("table"))==null?"all":request.getParameter("table");
    String firstfourofccnum=Functions.checkStringNull(request.getParameter("firstfourofccnum"))==null?"":request.getParameter("firstfourofccnum");
    String lastfourofccnum=Functions.checkStringNull(request.getParameter("lastfourofccnum"))==null?"":request.getParameter("lastfourofccnum");
    String custname=Functions.checkStringNull(request.getParameter("name"))==null?"":request.getParameter("name");
    String amount=Functions.checkStringNull(request.getParameter("amount"))==null?"":request.getParameter("amount");
    String emailaddr=Functions.checkStringNull(request.getParameter("emailaddr"))==null?"":request.getParameter("emailaddr");
    String Shippingid=Functions.checkStringNull(request.getParameter("Shippingid"))==null?"":request.getParameter("Shippingid");
    String Shippingbno=Functions.checkStringNull(request.getParameter("Shippingbno"))==null?"":request.getParameter("Shippingbno");
    String perfectmatch=Functions.checkStringNull(request.getParameter("perfectmatch"))==null?"":request.getParameter("perfectmatch");
    String descr=Functions.checkStringNull(request.getParameter("desc"))==null?"":request.getParameter("desc");
    String orderdescr=Functions.checkStringNull(request.getParameter("orderdesc"))==null?"":request.getParameter("orderdesc");
    log.debug(classname+"STrackingid="+STrackingid+"&status="+status+"&table="+table+"& firstfourofccnum="+ firstfourofccnum+"&lastfourofccnum="+lastfourofccnum+"&name"+custname+"&amount::"+amount+"&emailaddr::"+emailaddr+"&Shippingid::"+Shippingid+"&Shippingbno::"+Shippingbno+"&perfectmatch::"+perfectmatch+"&desc::"+descr+"&orderdesc::"+orderdescr);
    str="&STrakingid="+STrackingid+"&status="+status+"&table="+table+"&firstfourofccnum="+ firstfourofccnum+"&lastfourofccnum="+lastfourofccnum+"&name="+custname+"&amount="+amount+"&emailaddr="+emailaddr+"&Shippingid="+Shippingid+"&Shippingbno="+Shippingbno+"&perfectmatch="+perfectmatch+"&desc="+descr+"&orderdesc="+orderdescr;

    int records = 0;
    int totalrecords = 0;
    int currentblock = 1;
//     log.debug("total records"+tranDetail.get("totalrecords")+" records"+tranDetail.get("records"));
    try
    {
//        records = Integer.parseInt((String) tranDetail.get("records"));
        totalrecords = Integer.parseInt((request.getAttribute("totalrecords").toString()));
        currentblock = Integer.parseInt(request.getParameter("currentblock").toString());
    }
    catch (Exception ex)
    {
    }
    if(tranDetail!=null)
    {
        tranDetail.remove("records");
        tranDetail.remove("totalrecords");
    }
    int pageno =1;
    int pagerecords=30;
    try
    {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",request.getParameter("SPageno"),"Numbers",3,true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    }
    catch(ValidationException e)
    {
        pageno = 1;
        pagerecords = 30;
    }



    session.setAttribute("pagination",tranDetail);


    int pagecontent=7;
    HashMap inn;
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
    statushash.put("failed", "Cancelled by Customer");
    statushash.put("cancelled", "Cancelled Transaction");
    statushash.put("authcancelled", "Authorisation Cancelled");


    String fdate = null, tdate = null, fmonth = null, tmonth = null, fyear = null, tyear = null;
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

    if("all".equals(table) || "common".equals(table) ||  "qwipi".equals(table) || "ecore".equals(table))
    {
        session.setAttribute("STrakingid",STrackingid);
        session.setAttribute("status",status);
        session.setAttribute("table",table);
        session.setAttribute("firstfourofccnum",firstfourofccnum);
        session.setAttribute("lastfourofccnum",lastfourofccnum);
        session.setAttribute("custname",custname);
        session.setAttribute("amount",amount);
        session.setAttribute("emailaddr",emailaddr);
        session.setAttribute("Shippingid",Shippingid);
        session.setAttribute("Shippingbno",Shippingbno);
        session.setAttribute("perfectmatch",perfectmatch);
        session.setAttribute("descr",descr);
        session.setAttribute("orderdescr",orderdescr);
        session.setAttribute("fdate",fdate);
        session.setAttribute("tdate",tdate);
        session.setAttribute("fmonth",fmonth);
        session.setAttribute("tmonth",tmonth);
        session.setAttribute("fyear",fyear);
        session.setAttribute("tyear",tyear);
    }
    log.debug(classname+" Inside TRANSACTIONTRACK");
    int Srno=((pageno-1)*pagerecords)+1;
%>
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
            var firstsixofccnum = document.form.firstfourofccnum.value;
            var lastfourofccnum= document.form.lastfourofccnum.value;
            var trackingid=document.form.STrakingid.value;
            var name=document.form.name.value;
            var email=document.form.emailaddr.value;
            var shippingid=document.form.Shippingid.value;
            var amount=document.form.amount.value;
            var desc=document.form.desc.value;
            var flag=false;
//            var msg="One of the following field has to be filled";

            if(firstsixofccnum.length>0 || lastfourofccnum.length>0)
            {
            if(firstsixofccnum.length==6 && lastfourofccnum.length==4 )
                return true;
            if(firstsixofccnum.length<6)
            {

                alert("Enter first 6 of ccnum");
                document.form.firstfourofccnum.select();
                document.form.firstfourofccnum.focus();
                return false;
            }

            if( lastfourofccnum.length<4)
            {
                alert("Enter last 4 of ccnum");
                document.form.lastfourofccnum.select();
                document.form.lastfourofccnum.focus();
                return false;
            }
            }
            if (document.form.emailaddr.value.length > 0 && (document.form.emailaddr.value.indexOf(".") <= 0 || document.form1.contact_emails.value.indexOf("@") <= 0))
            {
                //alert("Please enter valid contact emailaddress.");
                msg = msg + "\nPlease enter valid  emailaddress.";
                document.form.emailaddr.select();
                document.form.emailaddr.focus();
                alert("Please Enter Valid Email Id @ & .");
                return false;
            }
        }

    </script>
</head>
<body class="bodybackground">
<form  class="form-horizontal" name="form" method="post" action="/support/servlet/TransactionDetail?ctoken=<%=ctoken%>"   autocomplete="off">
<input type="hidden" name="ctoken" value="<%=ctoken%>">
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">
<div class="panel-heading">
    <p> Transaction</p>
</div><br>
<%
    String MES=request.getParameter("MES");
    Hashtable  errorM=null;
    Hashtable errorO=null;
    log.info("transaction Detail::"+tranDetail);
    if(request.getAttribute("errorM")!=null)
    {
        errorM= (Hashtable) request.getAttribute("errorM");
    }
    if(request.getAttribute("errorO")!=null)
        errorO=(Hashtable) request.getAttribute("errorO");

    if(MES!=null)
    {
        if("X".equals(MES))
        {
            if(errorM!=null && !errorM.isEmpty() )
            {

                Enumeration enuM = errorM.keys();
                String keyM = "";
                String valueM = "";
                while (enuM.hasMoreElements())
                {
                    keyM = (String) enuM.nextElement();
                    valueM = (String) errorM.get(keyM);
                    out.println("<center><li class=\"textb\"><b>"+valueM+"</b></font></li></center>");
                }
            }  if(errorO!=null && !errorO.isEmpty())
        {

            Enumeration enuO = errorO.keys();
            String keyO = "";
            String valueO = "";
            while (enuO.hasMoreElements())
            {
                keyO = (String) enuO.nextElement();
                valueO = (String) errorO.get(keyO);
                out.println("<center><b class=\"textb\">"+valueO+"</b></center>");
            }
        }
        }
        else if("C".equals(MES))
        {
            out.println("<center><b class=\"textb\"> One of the below Value Has to be provided <br> Trackingid <br> Amount <br> Name <br> Emailid <br> Shippingid <br> Description <br> First Six CC No <br> Last Four CC No</b></font></center>");
        }
    }
    if("no".equals(data))
    {
        out.println("<center><b class=\"textb\"> No Data Found</b></center>");
    }
%>
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
                    out.println(Functions.yearoptions(2010, currentYear, fyear));
                else
                    out.println(Functions.printoptions(2010, currentYear));
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
                    out.println(Functions.yearoptions(2010, currentYear, tyear));
                else
                    out.println(Functions.printoptions(2010, currentYear));
            %>
        </select>
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Status</td>
    <td width="3%" class="textb"></td>
    <td width="10%" class="textb">
        <select name="status" class="txtbox" style="font-size:12px; ">
            <option value="">All</option>
            <%
                Enumeration enu = statushash.keys();
                String key = "";
                String value = "";


                while (enu.hasMoreElements())
                {
                    key = (String) enu.nextElement();
                    value = (String) statushash.get(key);

            %>
            <option value=<%=key%>><%=value%></option>
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
    <td width="8%" class="textb" >Datasource</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <select name="table" class="txtbox" style="font-size:12px; ">
            <option value="all">ALL</option>
            <option value="common">COMMON</option>
            <option value="qwipi">QWIPI</option>
            <option value="ecore">ECORE</option>
        </select>
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Tracking Id</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="text" value="" name="STrakingid"  class="txtbox" >
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Name</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="name" value=""  size="18" class="txtbox">
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
        <input type=text  name="firstfourofccnum"  maxlength="6" size="6"  class="txtbox" style="width:60px">
        <input type=text name="lastfourofccnum"  maxlength="4" size="4" class="txtbox" style="width:60px">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Amount</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="amount" value=""  size="10" class="txtbox">
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Email Id</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="emailaddr" value=""  size="18" class="txtbox">
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
    <td width="8%" class="textb" >Shipping Id</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="text" value="" name="Shippingid" size="18" class="txtbox">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" colspan="2" >Shipping Site</td>
    <td width="12%" class="textb">
        <input type="text" value="" name="Shippingbno" size="20" class="txtbox">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="checkbox" value="yes" name="perfectmatch"><font color="#1D7F2C"> <b>Perfect Match</b></font>
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
    <td width="8%" class="textb" >Description</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="text"  name="desc" class="txtbox">
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="14%" class="textb">Order Despription</td>
    <td width="3%" class="textb" > </td>
    <td width="12%" class="textb">
        <input type="text"  name="orderdesc" class="txtbox">
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
        <button type="submit" class="buttonform" name="B1">
            <span>
                <i class="fa fa-clock-o" style="float: left"></i>
            </span>
            Search  </button>
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

</tr><tr>
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

</table>
</td>
</tr>
</table>
</div>
</div>
</div>
</form>

<%

    if(tranDetail!=null)
    {

%>
</center>
<div class="reporttable " >
<table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%" >
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="2" width="100%" color="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
                <thead>
                <tr>
                    <td width="5%" class="th0">Sr</td>
                    <td width="8%" class="th0">Tracking ID</td>
                    <td width="7%" class="th1">Merchant Name</td>
                    <%--<td width="3%" class="th0">Site</td>--%>
                    <td width="5%" class="th1">Date</td>

                    <td width="9%" class="th1">Description</td>
                    <%--<td width="11%" class="th0">Order Description</td>--%>
                    <%--<td width="7%" class="th0">Pod Batch</td>--%>
                    <%--<td width="5%" class="th0">Pod</td>--%>
                    <td width="10%" class="th1">CardHolder's&nbsp;Name</td>
                    <td width="6%" class="th1">FirstSixccno</td>
                    <td width="4%" class="th0">LastFourccno</td>
                    <td width="4%" class="th0">Amount</td>
                    <td width="10%" class="th0">Capture Amount</td>
                    <td width="10%" class="th1">Refund Amount</td>
                    <td width="10%" class="th1">Current Status</td>
                </tr>
                </thead>
                <tbody>
                <%     for(int i=0;i<tranDetail.size();i++)
                {
                    inn =(HashMap)tranDetail.get(i);
                    String podno = inn.get("Podbatch").toString();
                    String pod  = inn.get("Pod").toString();
                    String Date = inn.get("Date").toString();
                    String TrackingId = inn.get("Trackingid").toString();
                    String name2 = inn.get("Name").toString();
                    String Firstsix = inn.get("Firstsixccno").toString();
                    String Lastfour = inn.get("Lastfourccno").toString();
                    String Amount = inn.get("Amount").toString();
                    String Capture =inn.get("CaptureAmount").toString();
                    String Refund = inn.get("RefundAmount").toString();
                    String Status = inn.get("Status").toString();
                    String MerName = inn.get("MerchantName").toString();
                    String Site = inn.get("SiteName").toString();
                    String desc=inn.get("desc").toString();
                    String orderdesc=inn.get("orderdesc").toString();
                    log.debug(classname+"--Transaction DISPLAY DATA-- "+i);
                    log.debug(classname+" Date::"+Date+" TrackingId::"+TrackingId+" CUSTOMER NAME::"+name2+" MerchantName::"+MerName+" MERCHANT Site::"+Site+" DESCRIPTION::"+desc+" POD::"+pod+" PODBATCH::"+podno+" FIRST SIX ccno"+Firstsix+" Last four ccn::"+Lastfour+" Amount::"+Amount+" CaptureAmount::"+Capture+" REFUND AMOUNT::"+Refund+" Current Status::"+Status);
                %>
                <tr style="font-size: 12px">
                    <td   height="40px" width="2px" class="tr1"><center><%=Srno%></center></td>
                    <td   height="40px" width="73px" class="tr1"><center><form action="/support/servlet/TransactionDetail?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="ctoken" value="<%=ctoken%>"><input type="hidden"  name="orderdesc" value=""><input type="hidden" name="STrakingid" value="<%=TrackingId%>"><input type="hidden" name="amount" value=""><input type="hidden" name="firstfourofccnum" value="<%=Firstsix%>"><input type="hidden" name="lastfourofccnum" value="<%=Lastfour%>"><input type="hidden" name="name" value=""><input type="hidden" name="table" value="<%=tableName%>_details"><input type="hidden"  name="desc" value=""><input type="hidden" value="" name="Shippingid" ><input type="hidden" value="" name="Shippingbno" > <input type=hidden name="emailaddr" value=""><input type="hidden" value="" name="status"><input type="submit" value="<%=TrackingId%>" style="border: 1px solid grey"><input type="hidden" name="fdate" value="<%=fdate%>"><input type="hidden" name="tdate" value="<%=tdate%>"><input type="hidden" name="fmonth" value="<%=fmonth%>"><input type="hidden" name="tmonth" value="<%=tmonth%>"><input type="hidden" name="fyear" value="<%=fyear%>"><input type="hidden" name="tyear" value="<%=tyear%>"></form></center></td>
                    <td   height="40px" width="70px" class="tr1"><center><%=MerName%></center></td>
                    <%--<td   height="40px" width="145px" class="tr1"><center><%=Site%></center></td>--%>
                    <td   height="40px" width="75px" class="tr1"><center><%=Date%></center></td>
                    <%--<% out.println("<td bgcolor=\"#fff8dc\" style=\"border:1px solid black  \" height=\"10px\" width=\"9%\"><a onclick=\"\"><font class=\"textb\">"+TrackingId+"</font></a></td>"); %>--%>

                    <td   height="40px" width="66px" class="tr1"><center><%=desc%></center></td>
                    <%--<td   height="40px" width="108px" class="tr1"><center><%=orderdesc%></center></td>--%>
                    <%--<td   height="40px" width="59px" class="tr1"><center><%=podno%></center></td>--%>
                    <%--<td   height="40px" width="59px" class="tr1"><center><%=pod%></center></td>--%>
                    <td   height="40px" width="96px" class="tr1"><center><%=name2%></center></td>
                    <td   height="40px" width="77px" class="tr1"><center><%=Firstsix%></center></td>
                    <td   height="40px" width="79px" class="tr1"><center><%=Lastfour%></center></td>
                    <td   height="40px" width="45px" class="tr1"><center><%=Amount%></center></td>
                    <%--<%System.out.println("AMOUNT"+Amount);%>--%>
                    <td   height="40px" width="95px" class="tr1"><center><%=Capture%></center></td>
                    <%--<%System.out.println("AMOUNT"+Capture);%>--%>
                    <td   height="40px" width="95px" class="tr1"><center><%=Refund%></center></td>
                    <%--<%System.out.println("AMOUNT"+Refund);%>--%>
                    <td   height="40px" width="102px" class="tr1"><center><%=Status%></center></td></tr>
                <% Srno++;
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
                <%--<td class="th0">&nbsp;</td>--%>
                <%--<td class="th0">&nbsp;</td>--%>
                <%--<td class="th0">&nbsp;</td>--%>
                <%--<td class="th0">&nbsp;</td>--%>
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
        <jsp:param name="page" value="TransactionDetail"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
    </jsp:include>
</div></td></tr></table>
<%}%>                   </div>

</body>

</html>