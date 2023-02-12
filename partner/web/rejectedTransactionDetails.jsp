<%@ page import="com.directi.pg.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ include file="top.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: march 9, 2017
  Time: 4:31:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","rejectedTransactionsList");
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    TransactionLogger transactionLogger = new TransactionLogger("rejectedTransactionDetails.jsp");

    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");

    String str = "";

    String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
    if (trackingid == null)
        trackingid = "";

    String fdate=null;
    String tdate=null;

    fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"fromDate",10,true);
    tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"fromDate",10,true);

    Functions functions = new Functions();
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    tdate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");

    Calendar rightNow = Calendar.getInstance();
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

    String currentyear = "" + rightNow.get(rightNow.YEAR);

    if (fdate != null) str = str + "fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (trackingid != null) str = str + "&STrackingid=" + trackingid;

    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

    str = str + "&SRecords=" + pagerecords;
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String rejectedTransactionDetails_Transaction_Details = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Transaction_Details")) ? rb1.getString("rejectedTransactionDetails_Transaction_Details") : "Transaction Details";
    String rejectedTransactionDetails_TrackingID = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_TrackingID")) ? rb1.getString("rejectedTransactionDetails_TrackingID") : "Tracking ID";
    String rejectedTransactionDetails_Transaction_Amount = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Transaction_Amount")) ? rb1.getString("rejectedTransactionDetails_Transaction_Amount") : "Transaction Amount";
    String rejectedTransactionDetails_Date_Transaction = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Date_Transaction")) ? rb1.getString("rejectedTransactionDetails_Date_Transaction") : "Date Of Transaction";
    String rejectedTransactionDetails_Description = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Description")) ? rb1.getString("rejectedTransactionDetails_Description") : "Description";
    String rejectedTransactionDetails_Order_Description = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Order_Description")) ? rb1.getString("rejectedTransactionDetails_Order_Description") : "Order Description";
    String rejectedTransactionDetails_Remark = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Remark")) ? rb1.getString("rejectedTransactionDetails_Remark") : "Remark";
    String rejectedTransactionDetails_Rejected_Reason = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Rejected_Reason")) ? rb1.getString("rejectedTransactionDetails_Rejected_Reason") : "Rejected Reason";
    String rejectedTransactionDetails_Http_Header = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Http_Header")) ? rb1.getString("rejectedTransactionDetails_Http_Header") : "Http Header";
    String rejectedTransactionDetails_Request_Host = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Request_Host")) ? rb1.getString("rejectedTransactionDetails_Request_Host") : "Request Host";
    String rejectedTransactionDetails_Request_IP = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Request_IP")) ? rb1.getString("rejectedTransactionDetails_Request_IP") : "Request IP";
    String rejectedTransactionDetails_Customer_Details = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Customer_Details")) ? rb1.getString("rejectedTransactionDetails_Customer_Details") : "Customer Details";
    String rejectedTransactionDetails_Cardholder_Name = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Cardholder_Name")) ? rb1.getString("rejectedTransactionDetails_Cardholder_Name") : "Cardholder's Name :";
    String rejectedTransactionDetails_Card_Number = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Card_Number")) ? rb1.getString("rejectedTransactionDetails_Card_Number") : "Card Number :";
  //  String rejectedTransactionDetails_Expiry_Date = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Expiry_Date")) ? rb1.getString("rejectedTransactionDetails_Expiry_Date") : "Expiry Date :";
    String rejectedTransactionDetails_Card_TypeID = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Card_TypeID")) ? rb1.getString("rejectedTransactionDetails_Card_TypeID") : "Card TypeID :";
    String rejectedTransactionDetails_Payment_TypeID = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Payment_TypeID")) ? rb1.getString("rejectedTransactionDetails_Payment_TypeID") : "Payment TypeID :";
    String rejectedTransactionDetails_Currency = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Currency")) ? rb1.getString("rejectedTransactionDetails_Currency") : "Currency :";
    String rejectedTransactionDetails_Customer_Email = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Customer_Email")) ? rb1.getString("rejectedTransactionDetails_Customer_Email") : "Customer's Email :";
    String rejectedTransactionDetails_Country = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Country")) ? rb1.getString("rejectedTransactionDetails_Country") : "Country :";
    String rejectedTransactionDetails_Cardholder_IP = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Cardholder_IP")) ? rb1.getString("rejectedTransactionDetails_Cardholder_IP") : "Cardholder's IP :";
    String rejectedTransactionDetails_Merchant_Details = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Merchant_Details")) ? rb1.getString("rejectedTransactionDetails_Merchant_Details") : "Merchant Details";
    String rejectedTransactionDetails_MerchantID = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_MerchantID")) ? rb1.getString("rejectedTransactionDetails_MerchantID") : "Merchant ID :";
    String rejectedTransactionDetails_Name_Merchant = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Name_Merchant")) ? rb1.getString("rejectedTransactionDetails_Name_Merchant") : "Name Of Merchant :";
    String rejectedTransactionDetails_Contact_Person = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Contact_Person")) ? rb1.getString("rejectedTransactionDetails_Contact_Person") : "Contact Person :";
    String rejectedTransactionDetails_Merchant_Email = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Merchant_Email")) ? rb1.getString("rejectedTransactionDetails_Merchant_Email") : "Merchant's Email :";
    String rejectedTransactionDetails_SiteURL = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_SiteURL")) ? rb1.getString("rejectedTransactionDetails_SiteURL") : "Site URL :";
    String rejectedTransactionDetails_Merchant_Telephone = StringUtils.isNotEmpty(rb1.getString("rejectedTransactionDetails_Merchant_Telephone")) ? rb1.getString("rejectedTransactionDetails_Merchant_Telephone") : "Merchant Telephone Number :";

%>
<html>
<head>
    <title><%=company%> | Rejected Transactions</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
            /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
        });
    </script>
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
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
</head>
<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/net/RejectedTransactionsList?ctoken=<%=ctoken%>" method="post" name="form">
                        <%
                            Hashtable temphash1 = null;
                            temphash1 = (Hashtable) hash.get("1");
                            String trackingId1 = (String)temphash1.get("id");

                            out.println("<input type='hidden' name='"+"STrackingid"+"' value='"+trackingId1+"'/>");
                            //out.println("<input type='hidden' name='"+"amount"+"' value='"+amount1+"'/>");

                            Enumeration<String> stringEnumeration=request.getParameterNames();
                            while(stringEnumeration.hasMoreElements())
                            {
                                String name=stringEnumeration.nextElement();
                                if(!"memberid".equals(name))
                                {
                                    if ("trackingid".equals(name))
                                    {
                                        out.println("<input type='hidden' name='" + name + "' value='" + request.getParameterValues(name)[1] + "'/>");
                                    }
                                    else
                                        out.println("<input type='hidden' name='" + name + "' value='" + request.getParameter(name) + "'/>");
                                }
                            }
                        %>
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/merchant/images/goBack.png">
                        </button>
                    </form>

                </div>
            </div>
            <br><br><br>
            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
            <%
                int records = 0;
                Hashtable actionHistory = (Hashtable) request.getAttribute("actionHistory");
                ActionEntry entry = new ActionEntry();
                Hashtable temphash = null;
                try
                {
                    records=Integer.parseInt((String)actionHistory.get("records"));
                }
                catch(Exception ex)
                { }
                String style = "class=textb";
            %>

            <div class="row reporttable">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=rejectedTransactionDetails_Transaction_Details%></strong></h2>
                        </div>
                        <%
                            // Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");

                            if (hash != null && hash.size() > 0)
                            {
                        %>

                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <%
                                    temphash = (Hashtable) hash.get("1");
                                    String trackingId = (String)temphash.get("id");
                                    if (trackingId == null || trackingId =="" || trackingId =="null")
                                        trackingId = " ";
                                    String amount = (String) temphash.get("amount");
                                    if (amount == null || amount =="" || amount =="null")
                                        amount = "-";
                                    String dateOfTransaction = (String)temphash.get("transactiontime");
                                    if (dateOfTransaction == null || dateOfTransaction =="" || dateOfTransaction =="null")
                                        dateOfTransaction = "-";
                                    String description=(String)temphash.get("description");
                                    if (description == null || description =="" || description =="null")
                                        description = "-";
                                    String orderDesc=(String)temphash.get("orderdescription");
                                    if (orderDesc == null || orderDesc =="" || orderDesc =="null")
                                        orderDesc = "-";
                                    String remark1=(String)temphash.get("remark");
                                    if(temphash.get("remark").toString().contains("<BR>"))
                                        remark1 = ((String)temphash.get("remark")).replaceAll("<BR>","\n");
                                    if (remark1 == null || remark1 =="" || remark1 =="null")
                                        remark1 = "-";
                                    String rejectReason=(String)temphash.get("rejectreason");
                                    if(temphash.get("rejectreason").toString().contains("<BR>"))
                                        rejectReason = ((String)temphash.get("rejectreason")).replaceAll("<BR>","\n");
                                    if (rejectReason == null || rejectReason =="" || rejectReason =="null")
                                        rejectReason = "-";
                                    String httpHeader=(String)temphash.get("httpheader");
                                    if (httpHeader == null || httpHeader =="" || httpHeader =="null")
                                        httpHeader = "-";
                                    String requestHost=(String)temphash.get("requestedhost");
                                    if (requestHost == null || requestHost =="" || requestHost =="null")
                                        requestHost = "-";
                                    String requestIp=(String)temphash.get("requestedip");
                                    if (requestIp == null || requestIp =="" || requestIp =="null")
                                        requestIp = "-";
                                %>
                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_TrackingID%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Tracking ID\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(trackingId) + "</td>");%>
                                </tr>
                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Transaction_Amount%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Transaction Amount\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(amount) +  "</td>");%>
                                </tr>
                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Date_Transaction%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Date Of Transaction\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(dateOfTransaction) +  "</td>");%>
                                </tr>
                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Description%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Description\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(description) +  "</td>");%>
                                </tr>
                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Order_Description%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Order Description\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(orderDesc) +  "</td>");%>
                                </tr>
                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Remark%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Remark\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(remark1) +  "</td>");%>
                                </tr>

                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Rejected_Reason%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Rejected Reason\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(rejectReason) +  "</td>");%>
                                </tr>

                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Http_Header%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Http Header\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(httpHeader) +  "</td>");%>
                                </tr>

                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Request_Host%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Request Host\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(requestHost) +  "</td>");%>
                                </tr>

                                <tr >
                                    <td valign="middle" style="text-align: center;background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=rejectedTransactionDetails_Request_IP%></b></td>
                                    <%out.println("<td valign=\"middle\" data-label=\"Request IP\" style=\"text-align: center; font-weight:600;\">" + ESAPI.encoder().encodeForHTML(requestIp) +  "</td>");%>
                                </tr>
                            </table>
                        </div>
                        <%
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1("Sorry", "No records found for given search criteria. Invalid TrackingID"));
                            }
                        %>
                    </div>
                </div>
            </div>
            <div class="row reporttable">
                <div class="col-sm-6 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=rejectedTransactionDetails_Customer_Details%></strong>

                            </h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div >
                                <div class="table table-responsive">
                                    <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <%
                                            temphash = (Hashtable) hash.get("1");
                                            String CardholderFirstName = (String)temphash.get("firstname");
                                            String CardholderLastName = (String)temphash.get("lastname");
                                            if (CardholderFirstName == null ||  CardholderFirstName =="" || CardholderFirstName =="null" || CardholderLastName == null ||  CardholderLastName =="" || CardholderLastName =="null")
                                            {
                                                CardholderFirstName = "-";
                                                CardholderLastName="-";
                                            }
                                            else
                                            {
                                                CardholderFirstName=CardholderFirstName+" " +CardholderLastName;
                                            }

                                            String firstsix1 = (String)temphash.get("firstsix");
                                            String lastfour1 = (String)temphash.get("lastfour");
                                            if (!functions.isValueNull(firstsix1))
                                                firstsix1 = "";
                                            if (!functions.isValueNull(lastfour1))
                                                lastfour1 = "";
                                            String cardNumber="";
                                            if(firstsix1!="" && lastfour1!="")
                                                 cardNumber = firstsix1+"******"+lastfour1;
                                            else
                                                cardNumber="-";

                                            /*String expiryDate = (String) temphash.get("expirydate");
                                            if (expiryDate == null || expiryDate =="" || expiryDate =="null")
                                                expiryDate = "-";
                                            else
                                                expiryDate = PzEncryptor.decryptExpiryDate(expiryDate);
                                            transactionLogger.debug("expiryDate----"+expiryDate);
                                            transactionLogger.debug("expiryDate1----"+(String) temphash.get("expirydate"));*/
                                            String customerEmail=(String)temphash.get("email");
                                            if (customerEmail == null || customerEmail =="" || customerEmail =="null")
                                                customerEmail = "-";
                                            String country=(String)temphash.get("country");
                                            if (country == null || country =="" || country =="null")
                                                country = "-";
                                            String city=(String)temphash.get("city");
                                            if (city == null || city =="" || city =="null")
                                                city = "-";
                                            String street=(String)temphash.get("street");
                                            if (street == null || street =="" || street =="null")
                                                street = "-";
                                            String zip=(String)temphash.get("zipcode");
                                            if (zip == null || zip =="" || zip =="null")
                                                zip = "-";
                                            String state=(String)temphash.get("state");
                                            if (state == null || state =="" || state =="null")
                                                state = "-";
                                            String phone=(String)temphash.get("phone");
                                            if (phone == null || phone =="" || phone =="null")
                                                phone = "-";
                                            String birthdate=(String)temphash.get("birthdate");
                                            if (birthdate == null || birthdate =="" || birthdate =="null")
                                                birthdate = "-";
                                            String cardholderIp=(String)temphash.get("cardholderip");
                                            if (cardholderIp == null || cardholderIp =="" || cardholderIp =="null")
                                                cardholderIp = "-";
                                            String cardtypeid = (String) temphash.get("cardtypeid");
                                            if (cardtypeid == null || cardtypeid == "" || cardtypeid == "null")
                                                cardtypeid = "-";
                                            String paymenttypeid = (String) temphash.get("paymenttypeid");
                                            if (paymenttypeid == null || paymenttypeid == "" || paymenttypeid == "null")
                                                paymenttypeid = "-";
                                            String currency = (String) temphash.get("currency");
                                            if (currency == null || currency == "" || currency == "null")
                                                currency = "";
                                            String print_email = " ";
                                            if(Roles.contains("superpartner")){
                                                print_email =customerEmail;
                                            }else{

                                                print_email=functions.getEmailMasking(customerEmail);
                                            }
                                        %>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Cardholder_Name%></b></td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(CardholderFirstName) + "</td>");%>
                                        </tr>

                                        <%--<tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Card_Number%></b></td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(cardNumber) +  "</td>");%>
                                        </tr>--%>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>First Six</b></td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(firstsix1) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>Last Four</b></td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(lastfour1) +  "</td>");%>
                                        </tr>

                                     <%--   <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Expiry_Date%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(expiryDate) +  "</td>");%>
                                        </tr>--%>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Card_TypeID%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(cardtypeid) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Payment_TypeID%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(paymenttypeid) +  "</td>");%>
                                        </tr>

                                        <tr>
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Currency%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(currency) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Customer_Email%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + print_email +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Country%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(country) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Cardholder_IP%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(cardholderIp) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>Street</b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(street) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>City</b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(city) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>State</b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(state) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>Zip Code</b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(zip) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>Phone</b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(phone) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>Birth Date</b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(birthdate) +  "</td>");%>
                                        </tr>

                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=rejectedTransactionDetails_Merchant_Details%></strong>

                            </h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div>
                                <div class="table table-responsive">
                                    <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <%
                                            temphash = (Hashtable) hash.get("1");
                                            String memberid = (String)temphash.get("toid");
                                            if (memberid == null || memberid =="" || memberid =="null")
                                                memberid = "-";
                                            String name = (String)temphash.get("login");
                                            if (name == null || name =="" || name =="null")
                                                name = "-";
                                            String contactperson = (String) temphash.get("contact_persons");
                                            if (contactperson == null || contactperson =="" || contactperson =="null")
                                                contactperson = "-";
                                            String memail=(String)temphash.get("contact_emails");
                                            if (memail == null || memail =="" || memail =="null")
                                                memail = "-";
                                            String url=(String)temphash.get("sitename");
                                            if (url == null || url =="" || url =="null")
                                                url = "-";
                                            String telno=(String)temphash.get("telno");
                                            if (telno == null || telno =="" || telno =="null")
                                                telno = "-";
                                            String terminalid=(String)temphash.get("terminalid");
                                            if (terminalid == null || terminalid =="" || terminalid =="null")
                                                terminalid = "-";
                                        %>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_MerchantID%></b></td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(memberid) + "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Name_Merchant%></b></td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(name) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Contact_Person%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(contactperson) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Merchant_Email%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + functions.getEmailMasking(memail) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_SiteURL%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(url) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b><%=rejectedTransactionDetails_Merchant_Telephone%></b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + functions.getPhoneNumMasking(telno) +  "</td>");%>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;"><b>Terminal ID :</b> </td>
                                            <%out.println("<td style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(terminalid) +  "</td>");%>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>