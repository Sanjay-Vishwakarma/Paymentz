<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ include file="top.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 4/17/14
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
    session.setAttribute("submit","FraudAlertTransactions");
%>
<style type="text/css">
    #ui-id-2
    {
        overflow: auto;
        max-height: 350px;
    }
</style>
<html>
<head>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>

    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title><%=company%> Transaction Management> Fraud Alert Transactions</title>
    <style type="text/css">
        @media(max-width: 991px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit!important;
            }
        }
    </style>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        });
    </script>
    <script>

        $(document).ready(function(){

            var w = $(window).width();

            //alert(w);

            if(w > 990){
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else{
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });
    </script>
</head>
<body>

<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (partner.isLoggedInPartner(session))
    {
        String Config =" ";

        String str = "";

        String trackingid = Functions.checkStringNull(request.getParameter("trackingid"));
        String paymentid = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
        String accountid = request.getParameter("accountid")==null?"":request.getParameter("accountid");
        String memberid = request.getParameter("toid")==null?"":request.getParameter("toid");
        String amt = Functions.checkStringNull(request.getParameter("amount"));
        String refundamount = Functions.checkStringNull(request.getParameter("refundamount"));
        String mid = Functions.checkStringNull(request.getParameter("mid"));
        String authCode = Functions.checkStringNull(request.getParameter("authCode"));
        String rrn = Functions.checkStringNull(request.getParameter("rrn"));
        String isRefund = Functions.checkStringNull(request.getParameter("isRefund"));
        String transactionType = Functions.checkStringNull(request.getParameter("transactionType"));
        String terminalid = nullToStr(request.getParameter("terminalid"));
        String cardnumber = nullToStr(request.getParameter("cardnumber"));
        String firstSix = Functions.checkStringNull(request.getParameter("firstsix"));
       String lastFour = Functions.checkStringNull(request.getParameter("lastfour"));



        String pid = null;
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(Roles.contains("superpartner")){
            pid=Functions.checkStringNull(request.getParameter("partnerlist"))==null?"":request.getParameter("partnerlist");
        }else{
            Config = "disabled";
            pid = String.valueOf(session.getAttribute("merchantid"));
        }
        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);
        String fdate=null;
        String tdate=null;
        fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
        String startTime = Functions.checkStringNull(request.getParameter("starttime"));
        String endTime = Functions.checkStringNull(request.getParameter("endtime"));
        str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (startTime == null) startTime = "00:00:00";
        if (endTime == null) endTime = "23:59:59";
        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        String currentyear = "" + rightNow.get(rightNow.YEAR);

        if (startTime != null) str = str + "&starttime=" + startTime;
        if (endTime != null) str = str + "&endTime=" + endTime;
        if (trackingid == null) trackingid = "";
        if (memberid == null) memberid = "";
        if (amt == null) amt = "";
        if (refundamount == null) refundamount = "";
        if (mid == null) mid = "";
        if (authCode == null) authCode = "";
        if (rrn == null) rrn = "";
        if (firstSix == null)firstSix = "";
        if (lastFour == null)lastFour = "";



        if(pid!=null)str = str + "&partnerlist=" + pid;
        else
            pid="";
        if(memberid!=null)str = str + "&toid=" + memberid;
        else
            memberid="";

        if(startTime!=null)str = str + "&starttime=" + startTime;
        else
            startTime="";
        if(endTime!=null)str = str + "&endtime=" + endTime;
        else
            endTime="";
        if(accountid!=null)str = str + "&accountid=" + accountid;
        else
            accountid="";

        if(amt!=null)str = str + "&amount=" + amt;
        else
            amt="";
        if(authCode!=null)str = str + "&authCode=" + authCode;
        else
            authCode="";
        if(mid!=null)str = str + "&mid=" + mid;
        else
            mid="";
        if(rrn!=null)str = str + "&rrn=" + rrn;
        else
            rrn="";
        if(rrn!=null)str = str + "&rrn=" + rrn;
        else
            rrn="";
        if(isRefund!=null)str = str + "&isRefund=" + isRefund;
        else
            isRefund="";

        int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

        str = str + "&SRecords=" + pagerecords;
        //String date = Functions.convertDtstamptoDate((String)temphash.get("dtstamp"));



        if (startTime == null) startTime = "00:00:00";
        if (endTime == null) endTime = "23:59:59";

        if (fdate == null) fdate = "" + 1;
        if (fdate != null) str = str + "&fromdate=" + fdate;
        if (tdate != null) str = str + "&todate=" + tdate;
        if (trackingid != null) str = str + "&Strackingid=" + trackingid;



%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <%-- <div class="page-heading">
             <div class="pull-right">
                 <div class="btn-group">
                     <form action="/partner/partnermerchantsignup.jsp?ctoken=<%=ctoken%>" method="POST">
                         <button class="btn-xs" type="submit" value="memberlist" name="submit" name="B1" style="background: transparent;border: 0;">
                             <img style="height: 45px;width: 200px;" src="/partner/images/addnewmember.png">
                         </button>
                     </form>
                 </div>
             </div>
             <br>
             <br>
             <br>--%>
        <div class="row">
            <div class="col-sm-12 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Fraud Alert Transactions Details</strong></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="horizontal-form">
                            <form action="/partner/net/FraudAlertTransactionList?ctoken=<%=ctoken%>" method="post" name="forms">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">

                                <%
                                    Functions funct = new Functions();
                                    String message = (String) request.getAttribute("error");
                                    if(funct.isValueNull(message))
                                    {
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                                    }
                                    String success = (String) request.getAttribute("success");
                                    if(funct.isValueNull(success))
                                    {
                                        out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-check-circle\" style=\" font-size:initial !important;\" ></i>&nbsp;&nbsp;" + success + "</h5>");
                                    }

                                    //  pid=Functions.checkStringNull(request.getParameter("partnerlist"))==null?"":request.getParameter("partnerlist");;

                                %>

                                <div class="widget-content padding">
                                    <div id="horizontal-form">

                                        <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                            <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                                <label>From</label>
                                                <input type="text" name="fromdate" id="From_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                            </div>


                                            <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                                <div class="form-group">
                                                    <label class="hide_label">From</label>
                                                    <div class='input-group date' >
                                                        <input type='text' id='datetimepicker12' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                        <%--<div id="datetimepicker12"></div>--%>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>


                                        <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                            <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                                <label>To</label>
                                                <input type="text" name="todate" id="To_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">

                                            </div>


                                            <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                                <div class="form-group">
                                                    <label class="hide_label">To</label>
                                                    <div class='input-group date' >
                                                        <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                    </div>
                                                </div>
                                            </div>

                                        </div>


                                        <div class="ui-widget form-group col-md-4 has-feedback">
                                            <label for="pid">Partner ID</label>
                                            <input name="partnerlist" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                            <input type="hidden" name="partnerlist" value="<%=pid%>">
                                        </div>

                                        <div class="form-group col-xs-12 col-md-12 has-feedback" style="margin:0; padding: 0;"></div>
                                        <div class="ui-widget form-group col-md-4 has-feedback">
                                            <label >Tracking Id</label>
                                            <input  type="text" name="trackingid" maxlength="100" class="form-control" size="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label>Payment ID</label>
                                            <input  type="text" name="paymentid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label>Account Id</label>
                                            <input  type="text" name="accountid" id="accountid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>" autocomplete="on">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >Member Id</label>
                                            <input type="text" name="toid" id="member" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" autocomplete="on">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >Terminal Id</label>
                                            <input id="terminal" name="terminalid" value="<%=terminalid%>" class="form-control" autocomplete="on">
                                        </div>

                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-4 control-label">Card Number</label>
                                            <div class="col-md-2">
                                                <input class="form-control" type="text" maxlength="6"  name="firstsix"  size="20" value="<%=firstSix%>">
                                            </div>
                                            <div class="col-md-2">
                                                <input class="form-control" type="text" maxlength="4"  name="lastfour" size="4" value="<%=lastFour%>" size="4">
                                            </div>
                                            <div class="col-md-1"></div>
                                        </div>

                                        <div class="form-group col-xs-12 col-md-12 has-feedback" style="margin:0; padding: 0;"></div>
                                        <div class="form-group col-md-4 has-feedback">
                                            <label >Amount</label>
                                            <input  type="text" name="amount" size="50"  maxlength="50" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >Refund Amount</label>
                                            <input  type="text" name="refundamount" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundamount)%>">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >MID</label>
                                            <input  type="text" name="mid" size="50"  maxlength="50" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mid)%>">
                                        </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label >Authorization Code</label>
                                            <input  type="text" name="authCode" size="50"  maxlength="50" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(authCode)%>">
                                        </div>


                                        <div class="form-group col-md-4 has-feedback">
                                            <label >RRN</label>
                                            <input  type="text" name="rrn" size="50"  maxlength="50" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rrn)%>">
                                        </div>

                                        <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                            <label >Is Refund</label>
                                            <select size="1" name="isRefund" class="form-control">
                                                <%
                                                    if("Y".equals(isRefund))
                                                    {%>
                                                <option value="Y" SELECTED>Yes</option>
                                                <option value="N">No</option>
                                                <%}
                                                else
                                                {%>
                                                <option value="N" SELECTED>No</option>
                                                <option value="Y">Yes</option>
                                                <%}
                                                %>
                                            </select>
                                        </div>

                                        <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                            <label >Transaction Type</label>
                                            <select size="1" name="transactionType" class="form-control">
                                                <%
                                                    if("REFUND".equals(transactionType))
                                                    {%>
                                                <option value="REFUND" SELECTED>REFUND</option>
                                                <option value="SALE">SALE</option>
                                                <%}
                                                else
                                                {%>
                                                <option value="SALE" SELECTED>SALE</option>
                                                <option value="REFUND">REFUND</option>
                                                <%}
                                                %>
                                            </select>
                                        </div>

                                        <div class="form-group col-md-4">
                                            <label style="color: transparent;">Path</label>
                                            <button type="submit" class="btn btn-default" style="display:block;">
                                                <i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;Search
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-y: auto;">
                        <%
                            StringBuffer requestParameter = new StringBuffer();
                            Enumeration<String> stringEnumeration = request.getParameterNames();
                            while(stringEnumeration.hasMoreElements())
                            {
                                String name=stringEnumeration.nextElement();
                                if("SPageno".equals(name) || "SRecords".equals(name))
                                {

                                }
                                else
                                    requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                            }
                            requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                            requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");
                            Hashtable hash = (Hashtable)request.getAttribute("transactionsdetails");
                            Hashtable temphash=null;
                            int records=0;
                            int totalrecords=0;
                            String currentblock=request.getParameter("currentblock");
                            if(currentblock==null)
                                currentblock="1";
                            try
                            {
                                records=Integer.parseInt((String)hash.get("records"));
                                totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
                            }
                            catch(Exception ex)
                            {

                            }
                            if(hash!=null)
                            {
                                hash = (Hashtable)request.getAttribute("transactionsdetails");
                            }
                            if(records>0)
                            {
                        %>
                        <div class="pull-right">
                            <div class="btn-group">
                                <form name="exportform" method="post" action="/partner/net/ExportFraudAlertTransactionList?ctoken=<%=ctoken%>" >
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="toid">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" name="spid">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" name="amt">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fromdate">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="todate">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingId">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>" name="paymentid">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>" name="accountid">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>" name="terminalId">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cardnumber)%>" name="personalAccountNumber">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundamount)%>" name="refundamount">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(authCode)%>" name="authCode">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rrn)%>" name="rrn">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(isRefund)%>" name="isRefund">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(transactionType)%>" name="transactionType">
                                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("accountid"))%>" name="accountid">
                                    <input type="hidden" value="<%=partnerid%>" name="partnerId">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">


                                    <button class="btn-xs" type="submit" style="background: white;border: 0;">

                                        <img style="height: 40px;" src="/merchant/images/excel.png">

                                    </button>
                                </form>
                            </div>
                        </div>
                        <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                            <thead>
                            <tr>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>ID</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Date</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction ID</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Payment ID</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Account ID</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Member ID</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Terminal ID</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Amount</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Refund Amount</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Type</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Card Number</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>MID</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Authorization Code</b></td>
                                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>RRN</b></td>
                                <td  colspan="3" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Is Refunded</b></td>
                            </tr>
                            </thead>
                            <%
                                Functions functions = new Functions();
                                String style="class=td1";
                                String ext="light";

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
                                    String transId = (String) temphash.get("id");
                                    String transactionTime = (String) temphash.get("transactionDate");
                                    String icicitransid = (String) temphash.get("trackingId");
                                    String paymentId = (String) temphash.get("paymentid");
                                    String currency = (String) temphash.get("currency");
                                    String accountId = (String) temphash.get("accountId");
                                    String memberId = (String) temphash.get("toid");
                                    String terminalid1 = (String) temphash.get("terminalId");
                                    String IsRefund = (String)temphash.get("isRefunded");
                                    String amount = currency + " " + temphash.get("amount");
                                    refundamount = currency + " " + temphash.get("refund_amount");
                                    String transactiontype=(String)temphash.get("transactionType");
                                    String cardnumber1=(String)temphash.get("personalAccountNumber");
                                    String MID=(String)temphash.get("merchant_id");
                                    String autherizationCode=(String)temphash.get("authorization_code");
                                    String RRN=(String)temphash.get("rrn");
                                    if(!functions.isValueNull(accountId))
                                        accountId = "-";

                                    if(!functions.isValueNull(memberId))
                                        memberId = "-";

                                    if(!functions.isValueNull(terminalid1))
                                        terminalid1 = "-";

                                    if(!functions.isValueNull(paymentId))
                                        paymentId = "-";

                                    if(!functions.isValueNull((String) temphash.get("refund_amount")))
                                        refundamount = "-";

                                    if(!functions.isValueNull(transactiontype))
                                        transactiontype = "-";
                                    if(!functions.isValueNull(cardnumber1))
                                        cardnumber1 = "-";
                                    if(!functions.isValueNull(MID))
                                        MID = "";
                                    if(!functions.isValueNull(autherizationCode))
                                        autherizationCode = "-";
                                    if(!functions.isValueNull(RRN))
                                        RRN = "-";


                                    out.println("<tr id=\"maindata\">");
                                    out.println("<td valign=\"middle\" data-label=\"ID\" align=\"center\" "+style+">&nbsp;"+id+ "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"Transaction Date\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("transactionDate"))+"<input type=\"hidden\" name=\"transactionTime\"value=\""+temphash.get("transactionDate")+"\"></td>");
/*
                                        out.println("<td valign=\"middle\" data-label=\"Partner Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"<input type=\"hidden\" name=\"partnerlist\"value=\""+temphash.get("partnerid")+"\"></td>");
*/
                                    out.println("<td valign=\"middle\" data-label=\"Tracking Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(icicitransid)+"<input type=\"hidden\" name=\"STrackingid\"value=\""+(icicitransid)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Payment ID\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(paymentId)+"<input type=\"hidden\" name=\"paymentid\" value=\""+(paymentId)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Account ID\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(accountId)+"<input type=\"hidden\" name=\"toid\" value=\""+(accountId)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Member ID\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(memberId)+"<input type=\"hidden\" name=\"toid\" value=\""+(memberId)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Terminal ID\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalid1)+"<input type=\"hidden\" name=\"terminalid\" value=\""+(terminalid1)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Amount\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"amount\" value=\""+temphash.get("amount")+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Refund Amount\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(refundamount)+"<input type=\"hidden\" name=\"refundamount\" value=\""+(refundamount)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Transaction Type\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactiontype)+"<input type=\"hidden\" name=\"transactiontype\" value=\""+(transactiontype)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Card Number\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(cardnumber1)+"<input type=\"hidden\" name=\"cardnumber\" value=\""+(cardnumber1)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"MID\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(MID)+"<input type=\"hidden\" name=\"mid\" value=\""+(MID)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Authorization Code\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(autherizationCode)+"<input type=\"hidden\" name=\"autherizationCode\" value=\""+(autherizationCode)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"RRN\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(RRN)+"<input type=\"hidden\" name=\"rrn\" value=\""+(RRN)+"\"></td>");
                                    out.println("<td valign=\"middle\" data-label=\"Is Refunded\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(IsRefund)+"<input type=\"hidden\" name=\"IsRefund\" value=\""+(IsRefund)+"\"></td>");
                                       /* out.println("<td valign=\"middle\" data-label=\"Terminal Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(functions.getEmailMasking((String)temphash.get("contact_emails")))+"<input type=\"hidden\" name=\"contact_emails\" value=\""+temphash.get("contact_emails")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Contact Email\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("activation"))+"<input type=\"hidden\" name=\"activation\" value=\""+temphash.get("activation")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Country\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("country"))+"<input type=\"hidden\" name=\"country\" value=\""+temphash.get("country")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"action\" value=\"View\" class=\"gotoauto btn btn-default\" ><input type=\"hidden\" name=\"action\" value=\"View\">");*/
                                        /*out.println(requestParameter.toString());
                                        out.println("</form></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"chk\" value=\"Edit\" class=\"gotoauto btn btn-default\"><input type=\"hidden\" name=\"action\" value=\"modify\">");
                                        out.println(requestParameter.toString());
                                        out.println("</form></td>");

                                        out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+">");
                                        out.println("<form action=\"/partner/net/VerifyMemberRegistration?ctoken="+ctoken+"\" method=\"POST\">");
                                        out.println("<input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"partnerId\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerId"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"emailtoken\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"pidName\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerId"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"contact_persons\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"\">");
                                        out.println("<input type=\"submit\" name=\"Verify\" value=\"Verify\" class=\"gotoauto btn btn-default\">");*/
                                    out.println("</form>");
                                    out.println("</td>");

                                    out.println("</tr>");
                                }
                            %>
                        </table>

                        <%--<table align=center valign=top style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                            <thead>
                            <tr style="background-color: #7eccad !important;color: white;">
                                <td  align="left" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Total Records : <%=totalrecords%></td>
                                <td  align="right" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Page No : <%=pageno%></td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                            </tr>
                            </thead>

                            <tbody>
                            <tr>
                                <td align=center>
                                    <jsp:include page="page.jsp" flush="true">
                                        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                        <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                        <jsp:param name="pageno" value="<%=pageno%>"/>
                                        <jsp:param name="str" value="<%=str%>"/>
                                        <jsp:param name="page" value="MemberDetailList"/>
                                        <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                        <jsp:param name="orderby" value=""/>
                                    </jsp:include>
                                </td>
                            </tr>
                            </tbody>
                        </table>--%>



                    </div>
                </div>
            </div>
        </div>
        <div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>

        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="MemberDetailList"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
        <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                }
            }
            else
            {
                response.sendRedirect("/partner/logout.jsp");
                return;
            }
        %>

    </div>
</div>
</div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
    public static String getStatus(String str)
    {
        if(str.equals("Y"))
            return "Active";
        else if(str.equals("T"))
            return "Test";
        else if(str.equals("N"))
            return "Inactive";

        return str;
    }
%>