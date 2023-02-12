<%@ page import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","Refund");
    Logger logger = new Logger("partnerMerchantRefundList.jsp");
%>
<style type="text/css">
    #ui-id-2
    {
        overflow: auto;
        max-height: 350px;
    }
</style>
<html lang="en">
<head>
    <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
    <script type="text/javascript" src='/partner/css/new/html5.js'></script>
    <title><%=company%> Transaction Management> Merchants Refund</title>

    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }

        function DoReverse(accountid,id,ctoken,memberid,description)
        {
            if (confirm("Do you really want to reverse this transaction."))
            {
                document.location.href = "GetRefundDetails?ctoken="+ctoken+"&icicitransid="+id+"&accountid="+accountid+"&memberid="+memberid+"&description="+description ;
            }
        }

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
    //TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");

    String str = "";
    str ="&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String memberid=nullToStr(request.getParameter("memberid"));
    String pid=nullToStr(request.getParameter("pid"));
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
        pid = String.valueOf(session.getAttribute("merchantid"));
        Config = "disabled";
    }
    String desc = Functions.checkStringNull(request.getParameter("description"));
    String trackingid = Functions.checkStringNull(request.getParameter("trakingid"))==null?"":request.getParameter("trakingid");
    String partnerid = String.valueOf(session.getAttribute("merchantid"));

    if(memberid!=null)str = str + "&memberid=" + memberid;
    else
        memberid="";
    if(pid!=null)str = str + "&pid=" + pid;
    else
        pid="";
    if(desc!=null)str = str + "&description=" + desc;
    else
        desc="";
    if(trackingid!=null)str = str + "&trackingid=" + trackingid;
    else
        trackingid="";

    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

    str = str + "&SRecords=" + pagerecords;

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);


    String partnerMerchantRefundList_Merchants_Refund = rb1.getString("partnerMerchantRefundList_Merchants_Refund");
    String partnerMerchantRefundList_Tracking_ID = rb1.getString("partnerMerchantRefundList_Tracking_ID");
    String partnerMerchantRefundList_Partner_ID = rb1.getString("partnerMerchantRefundList_Partner_ID");
    String partnerMerchantRefundList_Merchant_ID = rb1.getString("partnerMerchantRefundList_Merchant_ID");
    String partnerMerchantRefundList_Order_ID = rb1.getString("partnerMerchantRefundList_Order_ID");
    String partnerMerchantRefundList_Search = rb1.getString("partnerMerchantRefundList_Search");
    String partnerMerchantRefundList_Report_Table = rb1.getString("partnerMerchantRefundList_Report_Table");
    String partnerMerchantRefundList_TransactionDate = rb1.getString("partnerMerchantRefundList_TransactionDate");
    String partnerMerchantRefundList_PartnerID = rb1.getString("partnerMerchantRefundList_PartnerID");
    String partnerMerchantRefundList_MerchantID = rb1.getString("partnerMerchantRefundList_MerchantID");
    String partnerMerchantRefundList_TrackingID = rb1.getString("partnerMerchantRefundList_TrackingID");
    String partnerMerchantRefundList_OrderID = rb1.getString("partnerMerchantRefundList_OrderID");
    String partnerMerchantRefundList_CapturedAmount = rb1.getString("partnerMerchantRefundList_CapturedAmount");
    String partnerMerchantRefundList_RefundedAmount = rb1.getString("partnerMerchantRefundList_RefundedAmount");
    String partnerMerchantRefundList_Currency = rb1.getString("partnerMerchantRefundList_Currency");
    String partnerMerchantRefundList_Status = rb1.getString("partnerMerchantRefundList_Status");
    String partnerMerchantRefundList_Action = rb1.getString("partnerMerchantRefundList_Action");
    String partnerMerchantRefundList_ShowingPage = rb1.getString("partnerMerchantRefundList_ShowingPage");
    String partnerMerchantRefundList_of = rb1.getString("partnerMerchantRefundList_of");
    String partnerMerchantRefundList_records = rb1.getString("partnerMerchantRefundList_records");
    String partnerMerchantRefundList_Sorry = rb1.getString("partnerMerchantRefundList_Sorry");
    String partnerMerchantRefundList_No_records_found = rb1.getString("partnerMerchantRefundList_No_records_found");
    String partnerMerchantRefundList_Filter = rb1.getString("partnerMerchantRefundList_Filter");
    String partnerMerchantRefundList_Please = rb1.getString("partnerMerchantRefundList_Please");
    String partnerMerchantRefundList_page_no = rb1.getString("partnerMerchantRefundList_page_no");
    String partnerMerchantRefundList_total_no_of_records = rb1.getString("partnerMerchantRefundList_total_no_of_records");

    //LinkedHashMap memberidDetails= partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=partnerMerchantRefundList_Merchants_Refund%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%
                            Functions function = new Functions();
                            String res=(String)request.getAttribute("message");
                            if(function.isValueNull(res))
                            {
                                //out.println("<center><font face=\"arial\" color=\"#2C3E50\" size=\"2\"><b>"+res+"</b></font></center>");
                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+res+"</h5>");
                            }

                            String errorMsg=(String)request.getAttribute("error");
                            if(function.isValueNull(errorMsg))
                            {
                                //out.println("<center><font face=\"arial\" color=\"#2C3E50\" size=\"2\"><b>"+errorMsg+"</b></font></center>");
                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errorMsg+"</h5>");
                            }

                            String success=(String)request.getAttribute("success");
                            if(function.isValueNull(success))
                            {
                                //out.println("<center><font face=\"arial\" color=\"#2C3E50\" size=\"2\"><b>"+errorMsg+"</b></font></center>");
                                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+success+"</h5>");
                            }
                        %>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/partner/net/PartnerMerchantRefundList" method="post" name="forms" >
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">

                                    <div class="form-group col-md-3">
                                        <label><%=partnerMerchantRefundList_Tracking_ID%></label>
                                        <input type="text" name="trakingid" maxlength="20" value="<%=trackingid%>" class="form-control">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <div class="ui-widget">
                                            <label for="pid"><%=partnerMerchantRefundList_Partner_ID%></label>
                                            <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                            <input name="pid" type="hidden" value="<%=pid%>" >
                                        </div>
                                    </div>


                                        <div class="form-group col-md-3">
                                        <div class="ui-widget">
                                            <label for="member"><%=partnerMerchantRefundList_Merchant_ID%></label>
                                            <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                        </div>

                                      <%--  <label>Merchant ID</label>
                                        <select size="1" name="memberid" class="form-control">
                                            <option value="">All</option>
                                            <%
                                                String isSelected="";
                                                for(Object mid : memberidDetails.keySet())
                                                {
                                                    if(String.valueOf(mid).equals(memberid))
                                                    {
                                                        isSelected="selected";
                                                    }
                                                    else
                                                    {
                                                        isSelected="";
                                                    }
                                            %>
                                            <option value="<%=mid%>" <%=isSelected%>><%=mid+"-"+memberidDetails.get(mid)%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=partnerMerchantRefundList_Order_ID%></label>
                                        <input type="text" name="description" maxlength="100" value="<%=desc%>" class="form-control">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>&nbsp;</label>
                                        <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnerMerchantRefundList_Search%></button>

                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <br>

            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerMerchantRefundList_Report_Table%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <%
                                HashMap hash = (HashMap) request.getAttribute("transdetails");
                                HashMap temphash = null;

                                if(!"Refund".equals(request.getParameter("submit")))
                                {
                                    if(hash!=null && hash.size() > 0)
                                    {
                                        int records=0;
                                        int totalrecords=0;
                                        int currentblock=1;

                                        try
                                        {
                                            records=Functions.convertStringtoInt((String)hash.get("records"),15);
                                            totalrecords=Functions.convertStringtoInt((String) hash.get("totalrecords"), 0);
                                            currentblock = Functions.convertStringtoInt((request.getParameter("currentblock")), 1);
                                        }
                                        catch(Exception ex)
                                        {
                                            logger.error("Records & TotalRecords is found null",ex);
                                        }

                                        if (records > 0)
                                        {
                            %>
                            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_TransactionDate%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_PartnerID%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_MerchantID%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_TrackingID%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_OrderID%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_CapturedAmount%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_RefundedAmount%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_Currency%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_Status%></b></td>
                                    <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantRefundList_Action%></b></td>
                                </tr>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                </thead>

                                <%
                                    Functions functions = new Functions();
                                    String style = "class=td0";
                                    for (int pos = 1; pos <= records; pos++)
                                    {
                                        String id = Integer.toString(pos);

                                        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                        style = "class=\"tr" + (pos + 1) % 2 + "\"";
                                        temphash = (HashMap) hash.get(id);
                                        String toid=(String) temphash.get("toid");
                                        String captureamount=(String) temphash.get("captureamount");
                                        String refundamount=(String) temphash.get("refundamount");
                                        String currency=(String) temphash.get("currency");
                                        String date = Functions.convertDtstamptoDate((String)temphash.get("dtstamp"));
                                        String productname = ESAPI.encoder().encodeForHTML((String) temphash.get("productkey"));
                                        String path = productname + "/";
                                        String icicitransid = ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"));
                                        String accountid = ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"));
                                        if ("JPY".equalsIgnoreCase(currency))
                                        {
                                            captureamount = functions.printNumber(Locale.JAPAN, captureamount);
                                            refundamount = functions.printNumber(Locale.JAPAN, refundamount);
                                        }
                                        else if ("EUR".equalsIgnoreCase(currency))
                                        {
                                            captureamount = functions.printNumber(Locale.FRANCE, captureamount);
                                            refundamount = functions.printNumber(Locale.FRANCE, refundamount);
                                        }
                                        else if ("GBP".equalsIgnoreCase(currency))
                                        {
                                            captureamount = functions.printNumber(Locale.UK, captureamount);
                                            refundamount = functions.printNumber(Locale.UK, refundamount);
                                        }
                                        else if ("USD".equalsIgnoreCase(currency))
                                        {
                                            captureamount = functions.printNumber(Locale.US, captureamount);
                                            refundamount = functions.printNumber(Locale.US, refundamount);
                                        }
                                        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                        out.println("<tr id=\"maindata\">");
                                        out.println("<td valign=\"middle\" data-label=\"Transaction Date\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(date) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Partner ID\" align=\"center\">&nbsp;" + partner.getPartnerId(toid) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Merchant ID\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(toid) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Tracking ID\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(icicitransid) + "</a></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Description\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("description")) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Captured Amount\" align=\"center\">&nbsp;"+ ESAPI.encoder().encodeForHTML(captureamount) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Refunded Amount\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(refundamount)  + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"currency\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Status\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("status")) + "</td>");
                                        out.print("<td valign=\"middle\" data-label=\"Action\" align=\"center\">");
                                        out.print("<input class=\"button btn btn-default\" type=\"button\" value=\"Reverse\"  onClick=\"return DoReverse("+accountid+"," + ESAPI.encoder().encodeForHTMLAttribute(icicitransid) + ",'"+ctoken+"',"+ ESAPI.encoder().encodeForHTML(toid) + ",'" + ESAPI.encoder().encodeForHTML((String) temphash.get("description"))+"')\" >");
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
            <% int TotalPageNo;
                if(totalrecords%pagerecords!=0)
                {
                    TotalPageNo=totalrecords/pagerecords+1;
                }
                else
                {
                    TotalPageNo=totalrecords/pagerecords;
                }
            %>
            <div id="showingid"><strong><%=partnerMerchantRefundList_page_no%> <%=pageno%> <%=partnerMerchantRefundList_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
            <div id="showingid"><strong><%=partnerMerchantRefundList_total_no_of_records%>   <%=totalrecords%> </strong></div>

            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="PartnerMerchantRefundList"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>

            <%
                        }
                        else
                        {
                            out.println(Functions.NewShowConfirmation1(partnerMerchantRefundList_Sorry, partnerMerchantRefundList_No_records_found));
                        }
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation1(partnerMerchantRefundList_Sorry, partnerMerchantRefundList_No_records_found));
                    }
                }
                else if("Refund".equals(request.getParameter("submit")))
                {
                    out.println(Functions.NewShowConfirmation1(partnerMerchantRefundList_Filter, partnerMerchantRefundList_Please));
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1(partnerMerchantRefundList_Sorry, partnerMerchantRefundList_No_records_found));
                }
            %>


            <%!
                public static String nullToStr(String str)
                {
                    if(str == null)
                        return "";
                    return str;
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
