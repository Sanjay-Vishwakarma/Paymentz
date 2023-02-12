<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="index.jsp" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 8/17/2019
  Time: 1:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Payout Details View/Edit </title>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
        $(function ()
        {
            $(".datepicker").datepicker();

        });
    </script>

    <script>
        function goBack() {
            window.history.back();
        }
    </script>

</head>
<%
    Logger logger = new Logger("payoutDetailsUpdate.jsp");
    Functions functions = new Functions();
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Payout Details View/Edit
                <div style="float: right">
                    <form action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" name="form" method="post">
                        <input type="hidden" name="mappingid" value="<%=request.getAttribute("settledid")%>"/>
                        <input type="hidden" name="action" value="update"/>
                        <input type="hidden" name="payoutid" value="<%=request.getAttribute("payoutid")%>"/>

                    <%-- <%
                            Enumeration<String> stringEnumeration = request.getParameterNames();
                            while (stringEnumeration.hasMoreElements())
                            {
                                String name = stringEnumeration.nextElement();
                                if ("settledid".equals(name))
                                {
                                    out.println("<input type='hidden' name='mappingid' value='" + request.getParameter(name) + "'/>");
                                    out.println("<input type='hidden' name='action' value='update'/>");
                                }
                                else if(request.getParameter("mappingid")!= null)
                                {
                                    out.println("<input type='hidden' name='action' value='update'/>");
                                    out.println("<input type='hidden' name='mappingid' value='" + request.getParameter("mappingid") + "'/>");
                                }
                                /*else
                                    out.println("<input type='hidden' name='" + name + "' value='" + request.getParameter(name) + "'/>");*/
                            }
                        %>--%>
                        <button type="submit" name="submit" class="btn btn-default"
                                style="display: inline-block;background-color: #eeeeee;color: #2c3e50; ">
                            <i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp; Go Back
                        </button>
                    </form>

                    <%--<button onclick="goBack()"class="btn btn-default"style="display:inline-block;background-color: #eeeeee;color: #2c3e50; ">
                        <i class="fa fa-arrow-circle-left" aria-hidden="true"></i>Go Back</button>--%>
                </div>
            </div>

            <div>
                <form action="/icici/servlet/PayoutDetailsUpdate?ctoken=<%=ctoken%>" method="post" name="form1"
                      style="text-align: center" ENCTYPE="multipart/form-data">
                    <%
                        String error = (String) request.getAttribute("error");
                        String status = (String) request.getAttribute("message");
                        if(functions.isValueNull(error))
                            out.println("<center> <font class=\"textb\" size=\"28\" style=\"display: inline-block;\" face=\"arial\"> " + error + "</font></center>");
                        if (functions.isValueNull(status))
                            out.println("<center> <font class=\"textb\" size=\"28\" face=\"arial\"> " + status + "</font></center>");

                        String isreadonly = (String) request.getAttribute("isreadonly");
                        String conf = "";
                        if (isreadonly.equals("View"))
                            conf = "disabled";

                        Hashtable getPayOutDetailsUpdate = (Hashtable) request.getAttribute("viewpayout");
                        //System.out.println("get Details :::::::::"+request.getAttribute("viewpayout"));
                        //System.out.println("getPayOutDetailsUpdate :::::::::"+getPayOutDetailsUpdate);
                        Set<String> keys = getPayOutDetailsUpdate.keySet();

                        if (getPayOutDetailsUpdate != null)
                        {
                            Hashtable viewpayoutDetails = (Hashtable) getPayOutDetailsUpdate.get("" + 1);
                            request.setAttribute("payoutid" ,viewpayoutDetails.get("payout_id"));

                            String style = "class=tr1";

                            //System.out.println("Payment Recived Data::::::"+viewpayoutDetails.get("payment_receipt_date"));

                            String settleID = request.getParameter("settledid");
                            String reportid = request.getParameter("reportid");
                    %>


                    <table border="1" bordercolor="#ffffff" align="center" style="width:80%">
                        <input type="hidden" name="PayoutId" value="<%=viewpayoutDetails.get("payout_id")%>"/>
                        <input type="hidden" name="settledid" value="<%=functions.isValueNull(settleID)==true?settleID:request.getParameter("mappingid")%>" ></td>


                        <tr>
                            <td class="th0" colspan="2"></td>
                        </tr>
                        <tr <%=style%>>
                            <td class="tr1">Report Id :</td>
                            <td class="tr1">
                                <input type="text" class="txtbox1" style="padding: 10px" size="30" name="reportid"
                                       value="<%=functions.isValueNull(reportid)==true?reportid:request.getAttribute("reportid")%>" readonly></td>
                        </tr>
                        <tr <%=style%>>
                            <td class="tr1">Payout Date :</td>
                            <td class="tr1">
                                <input type="text" readonly class="datepicker" size="30" name="payoutDate"
                                       value="<%= viewpayoutDetails.get("payout_date")%>" <%=conf%>></td>
                        </tr>
                        <tr <%=style%>>
                            <td class="tr1">Payout Currency :</td>
                            <td class="tr1">
                                <input type="text" class="txtbox1" size="30" name="payoutCurrency"  maxlength="3" onkeypress="return onlyAlphabets(event,this);"
                                       value="<%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("payout_currency"))%>" <%=conf%>>
                            </td>
                        </tr>
                        <tr <%=style%>>
                            <td class="tr1">Conversion Rate:</td>
                            <td class="tr1">
                                <input type="text" class="txtbox1" size="30" name="conversionRate" maxlength="12"
                                       value="<%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("conversion_rate"))%>" <%=conf%>>
                            </td>
                        </tr>

                        <tr <%=style%>>
                            <td class="tr1">Payout Amount :</td>
                            <td class="tr1">
                                <input type="text" class="txtbox1" size="30" name="payoutAmount" maxlength="18"
                                       value="<%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("payout_amount"))%>" <%=conf%>>
                            </td>
                        </tr>
                        <tr <%=style%>>
                            <%
                                String receiptDate ="";
                            if(functions.isValueNull((String) viewpayoutDetails.get("payment_receipt_date")))
                            {
                                //System.out.println("date"+ viewpayoutDetails.get("payment_receipt_date"));
                                if(!("00/00/0000".equals(viewpayoutDetails.get("payment_receipt_date"))))
                                {
                                    //System.out.println("date1:::::"+ viewpayoutDetails.get("payment_receipt_date"));
                                    receiptDate = (String) viewpayoutDetails.get("payment_receipt_date");
                                }
                            }
                            %>
                            <td class="tr1">Payment Receipt Date :</td>
                            <td class="tr1">
                                <input type="text" readonly class="datepicker" size="30" name="paymentReceiptDate"
                                       value="<%= receiptDate%>" <%=conf%>></td>
                        </tr>

                        <%--<tr <%=style%>>
                            <td class="tr1">Payment Receipt Date :</td>
                            <td class="tr1">
                                <input type="text" class="txtbox1" size="30" name="paymentReceiptDate"
                                       value="<%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("payment_receipt_date"))%>" <%=conf%>>
                            </td>
                        </tr>--%>

                        <tr <%=style%>>
                            <td class="tr1">Payment Receipt Confirmation :</td>
                            <td class="tr1">
                                <select name="paymentReceiptConfirmation" id="paymentReceiptConfirmation" class="txtbox1"<%=conf%>>
                                    <option <%=viewpayoutDetails.get("payment_receipt_confirmation").equals("No")?"selected":""%>  value='No'>No </option>
                                    <option <%=viewpayoutDetails.get("payment_receipt_confirmation").equals("Yes")?"selected":""%> value='Yes'>Yes </option>
                                </select>
                                <%--<input type="text" class="txtbox1" size="30" name="paymentReceiptConfirmation"
                                       value="<%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("payment_receipt_confirmation"))%>" <%=conf%>>--%>
                            </td>
                        </tr>

                        <tr <%=style%>>
                            <td class="tr1">Beneficiary Bank Details :</td>
                            <td class="tr1">
                                <textarea class="txtbox1" size="30" maxlength="255"
                                          name="beneficiarybankdetails" <%=conf%>><%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("beneficiary_bank_details"))%>
                                </textarea>
                            </td>
                        </tr>
                        <tr <%=style%>>
                            <td class="tr1">Remitter Bank Details :</td>
                            <td class="tr1">
                                <textarea class="txtbox1" size="30"  maxlength="255"
                                          name="remitterbankdetails" <%=conf%>><%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("remitter_bank_details"))%>
                                </textarea>
                            </td>
                        </tr>
                        <tr <%=style%>>
                            <td class="tr1">Remarks :</td>
                            <td class="tr1">
                                <textarea class="txtbox1" size="30" maxlength="255"
                                          name="remarks" <%=conf%>><%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("remarks"))%>
                                </textarea>
                            </td>
                        </tr>
                        <tr <%=style%>>
                            <td class="tr1">Swift Message :</td>
                            <td class="tr1">
                                <textarea class="txtbox1" size="30" maxlength="255"
                                          name="swiftmessage" <%=conf%>><%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("swift_message"))%>
                                </textarea>
                            </td>
                        </tr>

                        <tr <%=style%>>
                            <td class="tr1">Swift Upload :</td>
                            <td class="tr1">
                                <%
                                    if (conf.equalsIgnoreCase("disabled"))
                                    {
                                        if (functions.isValueNull((String) viewpayoutDetails.get("swift_upload")))
                                        {
                                %>
                                <textarea class="txtbox1" size="30" maxlength="255"
                                          name="swiftupload" <%=conf%>><%=ESAPI.encoder().encodeForHTML((String) viewpayoutDetails.get("swift_upload"))%>
                                </textarea>
                                <%
                                }
                                else
                                {
                                %>
                                <textarea class="txtbox1" size="30" maxlength="255"
                                          name="swiftupload" <%=conf%>><%="-"%>
                                </textarea>
                                <%
                                    }
                                }
                                else
                                {
                                %>
                                <input name="fileName" type="file" value="">
                                <%}%>
                            </td>
                            <%-- <td width="50%" class="textb">
                                 <input name="fileName" type="file" value="">
                             </td>
                             <td width="5%" class="textb"
                                 style="position: relative; right: 225px;width: 65px; ">
                                 <button name="mybutton" type="submit" value="Upload"
                                         onclick="return check1();" class="buttonform">Upload
                                 </button>
                             </td>--%>
                        </tr>

                        <tr <%=style%>>
                            <td class="tr1" colspan="2" style="padding:10px" align="center">
                                <input type="submit" value="Update" name="Update"
                                       class="buttonform" onclick="return check1();"<%=conf%>>
                            </td>
                        </tr>
                    </table>
                    <%}%>
                </form>
            </div>
        </div>
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

<script type="text/javascript" language="JavaScript">
    function check1()
    {
        //alert("inside")
        //alert("INSIDE FUNCTIONS:::::  " + document.forms["form1"]["fileName"].value);
        var retpath = document.forms["form1"]["fileName"].value;
        //alert("INSIDE FUNCTIONS::::  " + retpath);
        if (retpath != ('')){
        var pos = retpath.lastIndexOf(".");
        var filename = "";
        if (pos != -1)
            filename = retpath.substring(pos + 1);
        else
            filename = retpath;

        //alert("INSIDE FUNCTIONS:: " + filename);
        if (filename == ('docx') || filename ==('docs') || filename == ('doc') || filename ==('pdf'))
        {
            return true;
        }
        alert('Please select a .docs OR .pdf file instead!');
        return false;

    }
    else
    return true;

    }

</script>

<script language="Javascript" type="text/javascript">

    function onlyAlphabets(e, t) {
        try {
            if (window.event) {
                var charCode = window.event.keyCode;
            }
            else if (e) {
                var charCode = e.which;
            }
            else { return true; }
            if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123))
                return true;
            else
                return false;
        }
        catch (err) {
            alert(err.Description);
        }
    }

</script>