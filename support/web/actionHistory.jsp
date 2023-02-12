<%@ page import="java.util.HashMap" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="custSuppDash.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 4/26/14
  Time: 2:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!static final String classname="actionHistory.jsp";%>
<% HashMap actionhist= (HashMap)  request.getAttribute("transactionDetails");
    Hashtable callList=(Hashtable)request.getAttribute("callerList");
    /*log.debug(classname+" actionhist::"+actionhist);*/
    Hashtable inn;

    String Track  =session.getAttribute("trackingid").toString();
    HashMap action;
    String b = (String)request.getAttribute("update");
    String nopodbatch= (String) request.getAttribute("nopodbatch");
    /*log.debug("action history details::"+actionhist);*/

    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
    String str="",trackingid,desc="",orderdesc="",Shippingid="",Shippingbno="",emailaddr="",amount="",name1="",status="",firstfourofccnum="",lastfourofccnum="";
    trackingid=Functions.checkStringNull(request.getParameter("STrakingid"))==null?"":request.getParameter("STrakingid");

    str="STrakingid="+trackingid+"&table=all_details&desc="+desc+"&orderdesc="+orderdesc+"&Shippingid="+Shippingid+"&Shippingbno="+Shippingbno+"&emailaddr="+emailaddr+"&amount="+amount+"&name="+name1+"&status="+status+"&firstfourofccnum="+firstfourofccnum+"&lastfourofccnum="+lastfourofccnum+"&ctoken="+ctoken;
    String currentblock=request.getParameter("currentblock");
    int records = 0,totalrecords = 0;

    if(currentblock==null)
        currentblock="1";

    if(actionhist==null)
    {
        actionhist=(HashMap)session.getAttribute("actionhistory");
    }
    else
    {
        session.setAttribute("actionhistory",actionhist);
    }

    if(callList==null)
    {
        Long time= new Long(Calendar.getInstance().get(Calendar.YEAR));
        Long time2= new Long(Calendar.getInstance().getTime().getTime());
        String ftime=String.valueOf(time);
        String ttime=String.valueOf(time2);
        callList =CustomerSupport.callerList(trackingid,null,ftime,ttime,1,30);
    }
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
    <meta charset="utf-8">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Expires" CONTENT="-1">
    <title>ACTION HISTORY</title>
</head>
<body onload="form1.reset();"  class="bodybackground">

<%if(actionhist!=null) {%>

<div class="row">
    <div class="col-lg-12">
        <div style="float:right; margin-top: 70px">
            <form action="/support/servlet/TransactionDetail?ctoken=<%=ctoken%>" method="post">
                <input name="fdate" type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("fdate"))==null?"":session.getAttribute("fdate").toString()%>" >
                <input  name="fmonth" type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("fmonth"))==null?"":session.getAttribute("fmonth").toString()%>">
                <input   name="fyear"type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("fyear"))==null?"":session.getAttribute("fyear").toString()%>">
                <input   name="tdate" type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("tdate"))==null?"":session.getAttribute("tdate").toString()%>">
                <input   name="tmonth" type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("tmonth"))==null?"":session.getAttribute("tmonth").toString()%>">
                <input   name="tyear" type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("tyear"))==null?"":session.getAttribute("tyear").toString()%>">
                <input name="status" type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("status"))==null?"":session.getAttribute("status").toString()%>">
                <input name="table" type="hidden" value="<%=Functions.checkStringNull((String)session.getAttribute("table"))==null?"":session.getAttribute("table").toString()%>">
                <input type="hidden" name="STrakingid" value="<%=Functions.checkStringNull((String)session.getAttribute("STrakingid"))==null?"":session.getAttribute("STrakingid").toString()%>">
                <input type="hidden" name="name" value="<%=Functions.checkStringNull((String)session.getAttribute("name"))==null?"":session.getAttribute("name").toString()%>">
                <input type="hidden"  name="firstfourofccnum" value="<%=Functions.checkStringNull((String)session.getAttribute("firstfourofccnum"))==null?"":session.getAttribute("firstfourofccnum").toString()%>">
                <input type="hidden" name="lastfourofccnum" value="<%=Functions.checkStringNull((String)session.getAttribute("lastfourofccnum"))==null?"":session.getAttribute("lastfourofccnum").toString()%>">
                <input type="hidden" name="amount" value="<%=Functions.checkStringNull((String)session.getAttribute("amount"))==null?"":session.getAttribute("amount").toString()%>">
                <input type="hidden" name="emailaddr" value="<%=Functions.checkStringNull((String)session.getAttribute("emailaddr"))==null?"":session.getAttribute("emailaddr").toString()%>">
                <input type="hidden"  name="Shippingid" value="<%=Functions.checkStringNull((String)session.getAttribute("Shippingid"))==null?"":session.getAttribute("Shippingid").toString()%>">
                <input type="hidden" name="Shippingbno" value="<%=Functions.checkStringNull((String)session.getAttribute("Shippingbno"))==null?"":session.getAttribute("Shippingbno").toString()%>">
                <input type="hidden"  name="perfectmatch" value="<%=Functions.checkStringNull((String)session.getAttribute("perfectmatch"))==null?"":session.getAttribute("perfectmatch").toString()%>">
                <input type="hidden"  name="desc" value="<%=Functions.checkStringNull((String)session.getAttribute("descr"))==null?"":session.getAttribute("descr").toString()%>">
                <input type="hidden"  name="orderdesc" value="<%=Functions.checkStringNull((String)session.getAttribute("orderdescr"))==null?"":session.getAttribute("orderdescr").toString()%>">
                <button type="submit" class="buttonform" name="B1">
                    <span>
                        <i class="fa fa-backward"></i>
                    </span>
                    &nbsp;Back
                </button>
            </form>
        </div>
        <div class="panel panel-default" style="padding-bottom: 30px">
            <center><h4 class="textb"><b> Transaction Detail</b></h4></center>
            <%

                log.debug(classname+"--Transaction DISPLAY--"+(actionhist.size()-1));
                action = (HashMap)actionhist.get(actionhist.size()-1);
                log.debug(" transaction hashMap"+action);
                String Date  =action.get("Date").toString();
                String  Trackingid=action.get("Trackingid").toString();
                String name2=action.get("Name").toString();
                String Firstsixccno=action.get("Firstsixccno").toString();
                String Lastfourccno=action.get("Lastfourccno").toString();
                String Merchant=action.get("MerchantName").toString();
                String Amount=action.get("Amount").toString();
                String Refund=action.get("RefundAmount").toString();
                String Capture=action.get("CaptureAmount").toString();

            %>

            <table border="0" cellpadding="5" cellspacing="2" style="width:95%;" color="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
                <thead>
                <tr>
                    <td width="5%" class="th1">Date Of Transaction</td>


                    <td width="10%" class="th0">Tracking Id</td>


                    <td width="5%" class="th1">Name</td>


                    <td width="10%" class="th0">First Six ccno</td>


                    <td width="5%" class="th1">Last Four ccno</td>


                    <td width="5%" class="th1">Merchant</td>


                    <td width="5%" class="th1">Amount</td>

                    <td width="5%" class="th1">Capture Amount</td>

                    <td width="5%" class="th1">Refund Amount</td>
                </tr>
                <td  height="35px" width="9%" class="textb"><center><%=Date%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=Trackingid%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=name2%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=Firstsixccno%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=Lastfourccno%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=Merchant%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=Amount%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=Capture%></center></td>
                <td  height="35px" width="9%" class="textb"><center><%=Refund%></center></td>
                <tr>
                </tr>
                </thead>
                <tbody>
                <% }%>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="reporttable">
    <center><h4 class="textb"><b> Action History</b></h4></center>
    <table border="0" cellpadding="5" cellspacing="2" style="width:95%" color="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td width="10%" class="th0">Tracking Id:<%=Track%></td>
            <td width="5%" class="th1">Action</td>
            <td width="10%" class="th0">Status</td>
            <td width="5%" class="th1">Date</td>
        </tr>
        </thead>
        <tbody>
        <%for(int i=0;i<actionhist.size()-1;i++)
        {
            action = (HashMap)actionhist.get("A"+i);
            String  act =action.get("Action").toString();
            String  stat=action.get("AStatus").toString();
            String date=action.get("TimeStamp").toString();
            log.debug(classname+"--ACTION HISTORY DISPLAY--"+i);
            log.debug(classname+" ACTION::"+act+" Status::"+stat+" Date::"+date);
        %>

        <tr style="height: 35px;font-size: 12"><td  height="35px" width="7%"><center><%=i+1%></center></td>
            <td  height="35px" width="9%" class="textb"><center><%=act%></center></td>
            <td  height="35px" width="7%" class="textb"><center><%=stat%></center></td>
            <td  height="35px" width="10%" class="textb"><center><%=date%></center></td> </tr>
        <% } %>
        </tbody>
    </table>
</div>




<%
    if(records>0)
    {
%>
<div class="reporttable " >
    <center><h4 class="textb"><b> Caller Detail</b></h4></center>
    <table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="95%" >
        <tr>
            <td>

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
                                tracking= ESAPI.encoder().encodeForHTML(inn.get("trackingid").toString());
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
                            String descr,remark,statusr ;
                            if(inn.containsKey("descr"))
                            {
                                descr=ESAPI.encoder().encodeForHTML(inn.get("descr").toString());
                            }
                            else
                            {
                                descr=" N.A";
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
                                statusr=ESAPI.encoder().encodeForHTML(inn.get("statuss").toString());
                            }
                            else
                            {
                                statusr=" N.A";
                            }

                        %>
                        <td   height="40px" width="95px" class="tr1"><center><%=descr%></center></td>
                        <%--<%System.out.println("AMOUNT"+Capture);%>--%>
                        <td   height="40px" width="95px" class="tr1"><center><%=remark%></center></td>
                        <%--<%System.out.println("AMOUNT"+Refund);%>--%>
                        <td   height="40px" width="102px" class="tr1"><center><%=statusr%></center></td></tr>
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
            <jsp:param name="page" value="TransactionDetail"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </div>

</div>
<%}
else
{ %>
<div class="reporttable" style="padding-bottom: 30px">
    <center><h4 class="textb"><b> Caller Detail</b></h4></center>
    <%
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    %>
</div>
<%
    }
%>



<center>


<div class="row" >
<div class="col-lg-12">
<div class="panel panel-default" style="margin-top: 10px">
<div class="panel-heading" style="background-color:#2c3e50;color:#ffffff;font-size:13px;  ">
    <p> Caller Entry</p>
</div><br>
<%  String mes=request.getParameter("MES");
    Hashtable errorM=null;
    Hashtable errorO= null;
    if(mes!=null)
    {
        errorM=(Hashtable) request.getAttribute("errorM");
        errorO= (Hashtable) request.getAttribute("errorO");
        if(errorM!=null && !errorM.isEmpty() )
        {

            Enumeration enuM = errorM.keys();
            String keyM = "";
            String valueM = "";
            while (enuM.hasMoreElements())
            {
                keyM = (String) enuM.nextElement();
                valueM = (String) errorM.get(keyM);
                out.println("<center><li><font class=\"textb\"><b>"+valueM+"</b></font></li></center>");
            }
        }
        if(errorO!=null && !errorO.isEmpty())
        {

            Enumeration enuO = errorO.keys();
            String keyO = "";
            String valueO = "";
            while (enuO.hasMoreElements())
            {
                keyO = (String) enuO.nextElement();
                valueO = (String) errorO.get(keyO);
                out.println("<center><li><font class=\"textb\"><b>"+valueO+"</b></font></li></center>");
            }
        }
    }


    if("yes".equals(nopodbatch))
    {
        out.println("<font class=\"textb\"><b>PLEASE ENTER PODBATCH WITH CORRESPONDING POD</b></font>");
    }
    if("no".equals(nopodbatch))
    {
        out.println("<font class=\"textb\"><b>PLEASE ENTER POD WITH CORRESPONDING PODBATCH</b></font>");
    }
    if("true".equals(b))
    {
        out.println("<center><font class=\"textb\"><b>New caller added successfully</b></font></center>");
    }
%>

<form id="form1" class="form-horizontal"  action="/support/servlet/Caller?ctoken=<%=ctoken%>" method="post" autocomplete="off">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:5%; ">

        <tr>
            <td>

                <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>
                    </tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Tracking Id</td>
                        <td width="14px" class="textb"></td>
                        <td width="155px" class="textb">
                            <input type="text" class="txtbox" readonly="true" size="20" name="STrakingid" value="<%=Track%>" style="font-size: 12px">
                        </td>
                    </tr>

                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>
                    </tr>

                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >First Name</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <input type="text" class="txtbox"  size="20" required="true" name="firstname"  style="font-size: 12px">
                        </td>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Last Name</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <input type="text" class="txtbox"  size="20" required="true" name="lastname"  style="font-size: 12px">
                        </td>
                    </tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>
                    </tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Email Address</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <input type="text" class="txtbox"  size="20" required="true" name="email"  style="font-size: 12px">
                        </td>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Phone No</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <input type="text" class="txtbox"  size="20" name="phoneno"  style="font-size: 12px" maxlength="15">
                        </td>
                    </tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>
                    </tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Shipping Id</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <input type="text" class="txtbox"  size="20"  name="Shippingid"  style="font-size: 12px">
                        </td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Shipping Site</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <input type="text" class="txtbox"  size="20"  name="Shippingbno"  style="font-size: 12px">
                        </td>
                    </tr>
                    <tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>
                    </tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <table  align="center" width="950px" cellpadding="2" cellspacing="2" style="margin-left:25px; ">
        <tr>
            <td>
                <table border="0" cellpadding="5" cellspacing="0" width="950px"  align="center">
                    <tr style="height: 51px">
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Description</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <textarea class="txtbox"  name="desc" value="" maxlength="255"  style="font-size: 12px;max-height: 50px;max-width: 150px"></textarea>
                        </td>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Remark</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <textarea type="textarea" class="txtbox"  name="remark" value="" maxlength="255"  style="font-size: 12px;max-height: 50px;max-width: 150px"></textarea>
                        </td>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >Status</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="155px" class="textb">
                            <textarea  type="textarea" class="txtbox" maxlength="255"  name="status" value=""  style="font-size: 12px;max-height: 50px;max-width: 150px"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>

                        <td width="6px" class="textb">&nbsp;</td>
                        <td width="66px" class="textb" >&nbsp;</td>
                        <td width="14px" class="textb">&nbsp;</td>
                        <td width="141px" class="textb">&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <table>

        <tr>
            <td width="6px" class="textb">&nbsp;</td>
            <td width="66px" class="textb" >&nbsp;</td>
            <td width="14px" class="textb">&nbsp;</td>
            <td width="141px" class="textb">&nbsp;</td>

            <td width="6px" class="textb">&nbsp;</td>
            <td width="66px" class="textb" >&nbsp;</td>
            <td width="14px" class="textb">&nbsp;</td>
            <td width="141px" class="textb">&nbsp;</td>

            <td width="6px" class="textb">&nbsp;</td>
            <td width="66px" class="textb" >&nbsp;</td>
            <td width="14px" class="textb">&nbsp;</td>
            <td width="141px" class="textb">
                <button type="submit" class="buttonform" name="B1">
                    <span>
                        <i class="fa fa-clock-o"></i>
                    </span>
                    &nbsp;Save
                </button>
            </td>
        </tr>    </table>
</form> </div> </div></div></center>
</body>
</html>