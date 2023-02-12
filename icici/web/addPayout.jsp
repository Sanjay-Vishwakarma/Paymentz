<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.manager.enums.Currency" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: sanjeet
  Date: 11/26/13
  Time: 2:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.fileupload.FileUploadBean" %>
<%@ page import="java.util.Enumeration" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<html>
<head>
    <title>Add Payout Details</title>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script language="javascript">
    </script>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        var count = 0;
        $(function ()
        {
            $(".datepicker").datepicker();

        });

        function showDetails()
        {
            count = document.getElementById("receiverBankDetailsCount").value;
            console.log("count---->" + count)
            var receiverBankDetails = document.getElementById("receiverBankDetails").value;
            var disabled = document.getElementById("receiverBankDetails").getAttributeNode("disabled") != null ? true : false;
            if (receiverBankDetails.includes(","))
            {
                var s = receiverBankDetails.split(",");
                for (var i = 0; i < s.length; i++)
                {
                    if (i != 0)
                    {
                        var element = document.createElement("input");
                        element.setAttribute("type", "text");
                        element.setAttribute("name", "receiverBankDetails_" + i);
                        element.setAttribute("class", "txtbox1");
                        element.setAttribute("style", "margin-right:5px;margin-top:5px;");
                        element.setAttribute("size", "30");
                        element.setAttribute("value", s[i]);
                        element.disabled = disabled;
                        var spanvar = document.getElementById("beneficiaryText");
                        spanvar.appendChild(element);
                    }
                    else
                    {
                        document.getElementById("receiverBankDetails").value = s[i];
                    }
                }
            }
        }
    </script>
</head>
<%
    Logger logger = new Logger("addPayout.jsp");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Add Payout Details
                <div style="float: right">

                    <form action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" name="form" method="post">
                        <%
                            Enumeration<String> stringEnumeration = request.getParameterNames();
                            while (stringEnumeration.hasMoreElements())
                            {
                                String name = stringEnumeration.nextElement();
                                if ("settledid".equals(name))
                                {
                                    out.println("<input type='hidden' name='mappingid' value='" + request.getParameter(name) + "'/>");
                                }
                                else
                                    out.println("<input type='hidden' name='" + name + "' value='" + request.getParameter(name) + "'/>");
                            }
                        %>
                        <button type="submit" name="submit" class="btn btn-default"
                                style="display: inline-block;background-color: #eeeeee;color: #2c3e50; ">
                            <i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp; Go Back
                        </button>
                    </form>
                </div>
            </div>

            <%
                Functions functions = new Functions();
                String action = (String) request.getAttribute("action");
                String conf = " ";

                String settleID = request.getParameter("settledid");
                String cycleid = request.getParameter("cycleid");
                String payoutDate = "";
                String payoutCurrency = "";
                String conversionRate = "";
                String payoutAmount = "";
                String beneficiaryBankDetails = "";
                String remitterBankDetails = "";
                String remarks = "";
                String swiftMessage = "";
                String paymentReceiptDate = "";
                String paymentConfirmation = "";

                FileUploadBean fub = new FileUploadBean();
                String userName = null;


                payoutDate = functions.isValueNull((String) request.getAttribute("payoutDate"))? (String) request.getAttribute("payoutDate") :"";
                paymentReceiptDate = functions.isValueNull((String) request.getAttribute("paymentReceiptDate"))? (String) request.getAttribute("paymentReceiptDate") :"";
                payoutCurrency = functions.isValueNull((String) request.getAttribute("payoutCurrency"))? (String) request.getAttribute("payoutCurrency") :"";
                conversionRate = functions.isValueNull((String) request.getAttribute("ConversionRate"))? (String) request.getAttribute("ConversionRate") :"";
                payoutAmount = functions.isValueNull((String) request.getAttribute("payoutAmount"))? (String) request.getAttribute("payoutAmount") :"";
                beneficiaryBankDetails = functions.isValueNull((String) request.getAttribute("beneficiaryBankDetails"))? (String) request.getAttribute("beneficiaryBankDetails") :"";
                remitterBankDetails = functions.isValueNull((String) request.getAttribute("remitterBankDetails"))? (String) request.getAttribute("remitterBankDetails") :"";
                remarks = functions.isValueNull((String) request.getAttribute("remarks"))? (String) request.getAttribute("remarks") :"";
                swiftMessage = functions.isValueNull((String) request.getAttribute("swiftMessage"))? (String) request.getAttribute("swiftMessage") :"";

            %>
            <table align="center" width="70%" cellpadding="2" cellspacing="2">
                <form action="/icici/servlet/AddPayout?ctoken=<%=ctoken%>" method="post" name="form1"
                      ENCTYPE="multipart/form-data">
                    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
                    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false">
                    <input type="hidden"  name="settledid" value="<%=settleID%>">


                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>

                                <%
                                    String error = (String) request.getAttribute("error");
                                    String status = (String) request.getAttribute("message");
                                    if(functions.isValueNull(error))
                                        out.println("<center> <font class=\"textb\" face=\"arial\"> " + error + "</font></center>");
                                    if (functions.isValueNull(status))
                                        out.println("<center> <font class=\"textb\" face=\"arial\"> " + status + "</font></center>");
                                       /* try
                                        {
                                            fub.doUpload1(request, userName);
                                        }
                                        catch (SystemError sys)
                                        {
                                            out.println(Functions.NewShowConfirmation("message", sys.getMessage()));
                                            return;
                                        }*/
                                %>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb">Report ID</span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%">
                                        <input type="text" class="txtbox1" size="30" readonly name="reportid"
                                               value="<%=functions.isValueNull(cycleid)==true?cycleid:request.getParameter("reportid")%>" <%=conf%>>
                                    </td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb">Payout Date*</span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%"><input type="text" size="30" readonly class="datepicker"
                                                           name="payoutDate"
                                                           value="<%=ESAPI.encoder().encodeForHTML(payoutDate)%>" <%=conf%>>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb">Payout Currency*</span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%"><input type="text" class="txtbox1" size="30" maxlength="3" name="payoutCurrency"onkeypress="return onlyAlphabets(event,this);"
                                                           value="<%=ESAPI.encoder().encodeForHTML(payoutCurrency)%>" <%=conf%>>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb">Conversion Rate*</span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%"><input type="text" class="txtbox1" size="30" maxlength="12" name="conversionRate"
                                                           value="<%=ESAPI.encoder().encodeForHTML(conversionRate)%>" <%=conf%>>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Payout Amount*</td>
                                    <td class="textb">:</td>
                                    <td width="50%"><input type="text" class="txtbox1" size="30" maxlength="18" name="payoutAmount"
                                                           value="<%=ESAPI.encoder().encodeForHTML(payoutAmount)%>" <%=conf%>>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span
                                            class="textb"> Payment Receipt Date </span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%"><input type="text" size="30" readonly class="datepicker"
                                                           name="paymentReceiptDate"
                                                           value="<%=ESAPI.encoder().encodeForHTML(paymentReceiptDate)%>" <%=conf%>>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="txtbox1">&nbsp;</td>
                                    <td width="43%" class="txtbox1"><span class="txtbox1">Payment Receipt Confirmation</span><br></td>
                                    <td width="5%" class="txtbox1">:</td>
                                    <td class="txtbox1">
                                    <select name="paymentReceiptConfirmation" id="paymentReceiptConfirmation" class="txtbox1">
                                                <option value='No'>No</option>
                                                <option value='Yes'>Yes</option>
                                            </select>
                                </tr>



                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span
                                            class="textb"> Beneficiary Bank Details </span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%">
                                        <textarea class="txtbox" maxlength="255"
                                                  placeholder="Beneficiary Bank Details"
                                                  name="beneficiarybankdetails" size="35" ><%=beneficiaryBankDetails%></textarea>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb"> Remitter Bank Details </span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%">
                                        <textarea class="txtbox" maxlength="255"
                                                  placeholder="Remitter Bank Details"
                                                  name="remitterbankdetails" size="35"><%=remitterBankDetails%></textarea>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb">Remarks</span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%">
                                        <textarea class="txtbox" maxlength="255"
                                                  placeholder="Remarks"
                                                  name="remarks" size="35"><%=remarks%></textarea>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb"> Swift Message </span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%">
                                        <textarea class="txtbox" maxlength="255"
                                                  placeholder="Swift Message"
                                                  name="swiftmessage" size="35"><%=swiftMessage%></textarea>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb"> Swift Upload </span><br>
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb">
                                        <input name="fileName" type="file" value="">
                                    </td>
                                    <%--<td width="5%" class="textb"
                                        style="position: relative; right: 225px;width: 65px; ">
                                        <button name="mybutton" type="submit" value="Upload"
                                                onclick="return check1();" class="buttonform">Upload
                                        </button>--%>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"></td>
                                    <td class="textb"></td>
                                    <td>
                                        <button id="submit" type="Submit" value="submit" name="submit"
                                                class="buttonform" onclick="return check1();">
                                            Add Payout
                                        </button>
                                    </td>
                                </tr>
                               <%-- <%}%>--%>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </form>
            </table>
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
        if (retpath != (''))
        {
            //alert("INSIDE FUNCTIONS::::  " + retpath);
            var pos = retpath.lastIndexOf(".");
            var filename = "";
            if (pos != -1)
                filename = retpath.substring(pos + 1);
            else
                filename = retpath;

            //alert("INSIDE FUNCTIONS:: " + filename);
            if (filename == ('docx') ||  filename == ('docs') || filename == ('doc')|| filename == ('pdf'))
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