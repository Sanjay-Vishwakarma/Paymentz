<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.TreeMap" %>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 08/27/2018
  Time: 2:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    TreeMap<String,String> statushash = new TreeMap<>();

    statushash.put("failed", "Failed");
    statushash.put("authfailed", "Auth Failed");
    statushash.put("authstarted", "Auth Started");
    statushash.put("authstarted_3D", "Auth Started 3D");
    statushash.put("chargeback", "Chargeback");
    statushash.put("chargebackreversed", "Chargeback Reversed");
    statushash.put("cancelstarted", "Cancel Inititated");
    statushash.put("capturestarted", "Capture Started");
    statushash.put("capturesuccess", "Capture Success");
    statushash.put("payoutcancelsuccessful","Payout Cancel Started");
    statushash.put("payoutstarted", "Payout Started");
    statushash.put("markedforreversal", "Reversal Request Sent");
    /*statushash.put("authstarted", "Auth Started");
    statushash.put("capturestarted", "Capture Started");
    statushash.put("markedforreversal", "Reversal Request Sent");
    statushash.put("capturesuccess", "Capture Success");
    statushash.put("partialrefund", "Partial Refund");
    statushash.put("authsuccessful", "Auth Successful");*/
%>
<html>
<head>
    <title>Common Integration> Common Manual Reconcilation</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
    <script>
        $(function () {

            $('#File').change(function () {
                document.getElementById("Upload").disabled = false;
                var  retpath = document.FIRCForm.File.value;
                var pos = retpath.indexOf(".");
                var filename="";
                if (pos != -1)
                    filename = retpath.substring(pos + 1);
                else
                    filename = retpath;
                if (filename==('xls'))
                {
                    var files = $('#File').get(0).files;
                    if (files.length > 0)
                    {
                        var file = files[0];

                        var fileReader = new FileReader();
                        fileReader.onloadend = function (e)
                        {
                            var arr = (new Uint8Array(e.target.result)).subarray(0, 4);
                            var header = '';
                            for (var i = 0; i < arr.length; i++)
                            {
                                header += arr[i].toString(16);
                            }

                            if(header != "d0cf11e0"){
                                alert('Please select a .xls file instead!');
                                document.getElementById("Upload").disabled = true;
                            }

                        };
                        fileReader.readAsArrayBuffer(file);
                    }
                }
                else{
                    alert('Please select a .xls file instead!');
                    document.getElementById("Upload").disabled = true;
                }
            });

        });
    </script>

    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("trackingid");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }

        /*function checkRefundAmount(trackingid)
        {
            var fieldname = "refundamount_"+trackingid;
            if (isNaN(document.getElementById(fieldname).value) || (document.getElementById(fieldname).value) <=0)
            {
                alert("Please Enter a Valid Refund Amount greater than 0 for tracking id "+trackingid);
                return false;
            }
            return true;
        }*/

        function checkCaptureAmount(trackingid)
        {
            var fieldname = "captureamount_"+trackingid;
            if (isNaN(document.getElementById(fieldname).value) || (document.getElementById(fieldname).value) <= 0)
            {
                alert("Please Enter a Valid Capture Amount greater than 0 for tracking id "+trackingid);
                return false;
            }
            return true;
        }

        function DoChargeback()
        {
            var checkboxes = document.getElementsByName("trackingid");
            var total_boxes = checkboxes.length;
            var valid = false;
            var activeStatus = document.getElementById("menu").value;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {   flag = true;

                    /*if(activeStatus == "reversed")
                    {
                        if (!checkRefundAmount(checkboxes[i].value))
                        {
                            valid = false;
                            return false;
                        }
                        else
                        {
                            valid = true;
                        }
                    }*/
                    if(activeStatus == "capturesuccess"){
                        if (!checkCaptureAmount(checkboxes[i].value))
                        {
                            valid = false;
                            return false;
                        }
                        else
                        {
                            valid = true;
                        }
                    }
                    else{
                        valid = true;
                    }
                }
            }

            if(!flag)
            {
                alert("Select at least one transaction");
                return false;
            }

            if(!valid){
                return false;
            }

            if (confirm("Do you really want to Process all selected transaction ?"))
            {
                document.form.submit();
            }
            else
            {
                return false;
            }
        }
        function checkOption(obj,trackingid)
        {
            console.log("obj.value---",obj.value)
            if(obj.value === "reversed" )
            {
                document.getElementById("refundamount_"+trackingid).disabled = false;
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
                document.getElementById("isfraud_"+trackingid).disabled = false;
                //document.getElementById("captureamount_"+trackingid).disabled = true;

            }
            if(obj.value === "capturesuccess" )
            {
                document.getElementById("captureamount_"+trackingid).disabled = false;
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
                //document.getElementById("isfraud_"+trackingid).disabled = false;
                document.getElementById("refundamount_"+trackingid).disabled = true;

            }
            /*if(obj.value === "capturesuccess" )
            {
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
                document.getElementById("isfraud_"+trackingid).disabled = false;

            }*/

            if(obj.value === "partialrefund" )
            {
                document.getElementById("refundamount_"+trackingid).disabled = false;
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
                //document.getElementById("isfraud_"+trackingid).disabled = false;

            }

            else if(obj.value === "capturesuccess")
            {
                document.getElementById("captureamount_"+trackingid).disabled = false;
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
               // document.getElementById("isfraud_"+trackingid).disabled = false;
            }
            else if(obj.value === "chargebackreversed")
            {
                document.getElementById("chargebackamount_"+trackingid).disabled = false;
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
                // document.getElementById("isfraud_"+trackingid).disabled = false;
            }
            else if(obj.value === "casefiling")
            {
                document.getElementById("chargebackamount_"+trackingid).disabled = true;
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
                // document.getElementById("isfraud_"+trackingid).disabled = false;
            }

            else if(obj.value === "capturefailed" )
            {
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;

            }
            else if(obj.value === "settled" )
            {
                document.getElementById("remark_"+trackingid).disabled = false;

            }

            else if(obj.value === "authcancelled" )
            {
                document.getElementById("remark_"+trackingid).disabled = false;

            }
            else if(obj.value === "payoutfailed" )
            {
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;

            }
            else if(obj.value === "payoutsuccessful" )
            {
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
                document.getElementById("payoutamount_"+trackingid).disabled = false;

            }

            else if(obj.value === "payoutcancelfailed" )
            {
                document.getElementById("remark_"+trackingid).disabled = false;
                document.getElementById("paymentid_"+trackingid).disabled = false;
            }

            else
            {
                if(obj.value === "reversed" )
                {
                    document.getElementById("refundamount_"+trackingid).disabled = false;
                    //document.getElementById("captureamount_"+trackingid).disabled = false;
                    document.getElementById("remark_"+trackingid).disabled = false;
                    document.getElementById("paymentid_"+trackingid).disabled = false;
                    document.getElementById("isfraud_"+trackingid).disabled = false;

                }
                else if(obj.value === "capturesuccess")
                {
                    document.getElementById("captureamount_"+trackingid).disabled = false;
                    document.getElementById("remark_"+trackingid).disabled = false;
                    document.getElementById("paymentid_"+trackingid).disabled = false;
                   // document.getElementById("isfraud_"+trackingid).disabled = false;
                }
                else if(obj.value === "authsuccessful" || obj.value === "failed")
                {
                    document.getElementById("captureamount_"+trackingid).disabled = true;
                    document.getElementById("remark_"+trackingid).disabled = false;
                    document.getElementById("paymentid_"+trackingid).disabled = false;
                    //document.getElementById("isfraud_"+trackingid).disabled = false;

                }
                else if(obj.value === "authstarted" || obj.value==="authfailed")
                {

                    document.getElementById("paymentid_"+trackingid).disabled = false;
                    document.getElementById("remark_"+trackingid).disabled = false;
                }
                else if(obj.value === "markedforreversal")
                {

                    document.getElementById("remark_"+trackingid).disabled = false;
                    document.getElementById("paymentid_"+trackingid).disabled = false;
                    document.getElementById("refundamount_"+trackingid).disabled = false;
                    //document.getElementById("captureamount_"+trackingid).disabled = false;
                   // document.getElementById("captureamount_"+trackingid").readOnly = true;

                }
                else
                {
                    document.getElementById("refundamount_"+trackingid).disabled = true;
                    document.getElementById("remark_"+trackingid).disabled = true;
                    document.getElementById("paymentid_"+trackingid).disabled = true;
                    document.getElementById("isfraud_"+trackingid).disabled = true;
                }
            }
        }

    </script>

</head>
<body>
<%
    String memberid=nullToStr(request.getParameter("toid"));
    String accountID=nullToStr(request.getParameter("accountid"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str="";
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        String toid=null;
        String accountid=null;
        String trackingid=null;
        String discription=null;
        String paymentId=null;
        String status;
        try
        {
            fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
            toid =  ESAPI.validator().getValidInput("toid",request.getParameter("toid"),"Numbers",15,true);
            accountid =  ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",15,true);
            trackingid = ESAPI.validator().getValidInput("trackingid",request.getParameter("trackingid"),"Numbers",15,true);
            paymentId =  ESAPI.validator().getValidInput("paymentnumber",request.getParameter("paymentnumber"),"SafeString",100,true);
            discription = ESAPI.validator().getValidInput("description",request.getParameter("description"),"SafeString",50,true);
            status =  ESAPI.validator().getValidInput("status",request.getParameter("status"),"SafeString",50,true);
        }
        catch(ValidationException e)
        {

        }
        Calendar rightNow = Calendar.getInstance();
        String currentyear= "" + rightNow.get(rightNow.YEAR);
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
        status = Functions.checkStringNull(request.getParameter("status"));

        str = str + "ctoken=" + ctoken;
        if (fdate != null) str = str + "&fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;
        if (status != null) str = str + "&status=" + status;
        if (toid != null) str = str + "&toid=" + toid;
        if (accountid != null) str = str + "&accountid=" + accountid;
        if (trackingid != null) str = str + "&trackingid=" + trackingid;
        if (paymentId != null) str = str + "&paymentnumber=" + paymentId;
        if (discription != null) str = str + "&description=" + discription;
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);

%>
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Reconciliation Transaction List
                </div>
                <form name = "FIRCForm" action="/icici/servlet/UpdateReconciliationTxn?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
                    <table style="margin-top: 0.5%;margin-left: 48%;">
                        <tr>
                            <td class="textb" colspan="2" align="left"><b>Upload Transaction List File</b></td>
                            <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                            <td align="center"><input name="File" id="File" type="file" value="choose File"
                                                      style="width: 280px" accept="application/vnd.ms-excel"></td>
                            <td class="textb">
                                <button type="submit" id="Upload" class="buttonform" style="height: 24px;">
                                    <i class="fa fa-clock-o"></i>
                                    &nbsp;&nbsp;Upload
                                </button>
                            </td>
                        </tr>
                    </table>
                </form>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken" id="ctoken">

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <form action="/icici/servlet/CommonReconList?ctoken=<%=ctoken%>" method="post" name="forms" >
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="fdate" class="textb" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="textb" value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>">
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" class="textb" value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>">
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

                                        <select size="1" name="tyear" class="textb" style="width:54px;">

                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2013));
                                            %>
                                        </select>
                                    </td>

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Account ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountID%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Merchant ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">

                                    <%--</td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Account ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountID%>" class="txtbox" autocomplete="on">
                                    </td>--%>
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
                                    <td width="8%" class="textb" >Tracking ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" name="trackingid" class="txtbox"  value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">Payment ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input maxlength="100" type="text" name="paymentnumber" style="width:300px;" class="txtbox"  value="<%=request.getParameter("paymentnumber")==null?"":request.getParameter("paymentnumber")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Order ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="description" class="txtbox" value="<%=request.getParameter("description")==null?"":request.getParameter("description")%>">
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
                                    <td width="8%" class="textb" >Status</td>
                                    <td width="3%" class="textb" ></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="status" class="txtbox"   >
                                            <option value="">All</option>
                                            <%
                                                Set statusSet = statushash.keySet();
                                                Iterator iterator=statusSet.iterator();
                                                String selected = "";
                                                String key = "";
                                                String value = "";

                                                while (iterator.hasNext())
                                                {
                                                    key = (String)iterator.next();
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
                                    <td width="8%" class="textb" >&nbsp;</td>
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
                            </form>

                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>


<div class="reporttable">
    <%
        String error1= (String)request.getAttribute("error");
        if(error1 != null)
        {
            out.println("<center><font class=\"textb\"><b>"+error1+"</b></font></center>");
        }

        String errormsg1 = (String)request.getAttribute("cbmessage");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println(errormsg1);
        }

        /*log.debug("sErrorMessage:::::"+sErrorMessage.toString());
        log.debug("sSuccessMessage:::::"+sSuccessMessage.toString());
        */
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");

        Hashtable temphash=null;

        String error=(String ) request.getAttribute("errormessage");
        if(error !=null)
        {
            out.println("<b>");
            out.println(error);
            out.println("</b>");
        }

        String currentblock=request.getParameter("currentblock");
        int records=0;
        int totalrecords=0;

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
        if(records>0)
        {
    %>
    <div class="scroll">
        <form  action="/icici/servlet/CommonReconciliationProcess?ctoken=<%=ctoken%>" method="post" name="form">
            <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
                <thead>

                <tr>
                    <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                    <td valign="middle" width="2%" align="center" class="th0">Sr&nbsp;no</td>
                    <td valign="middle" align="center" class="th0">Tracking&nbsp;ID</td>
                    <td valign="middle" align="center" class="th0">From&nbsp;id</td>
                    <td valign="middle" align="center" class="th0">Merchant&nbsp;ID</td>
                    <td valign="middle" align="center" class="th0">Payment&nbsp;ID</td>
                    <td valign="middle" align="center" class="th0">Order&nbsp;ID</td>
                    <td valign="middle" align="center" class="th0">Amount</td>
                    <td valign="middle" align="center" class="th0">Chargebacked Amount</td>
                    <td  class="th0">status</td>
                    <td valign="middle" align="center" class="th0">TimeStamp</td>
                    <td valign="middle" align="center" class="th0">Status&nbsp;Treatment</td>
                    <td valign="middle" align="center" class="th0">Capture&nbsp;Amount</td>
                    <td valign="middle" align="center" class="th0">Refund&nbsp;Amount</td>
                    <td valign="middle" align="center" class="th0">CB Reversal&nbsp;Amount</td>
                    <td valign="middle" align="center" class="th0">Payout&nbsp;Amount</td>
                    <td valign="middle" align="center" class="th0">PaymentOrderNO</td>
                    <td valign="middle" align="center" class="th0">Remark</td>
                    <td valign="middle" align="center" class="th0">Is Fraud Trans</td>
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
                        out.println("<tr>");
                        out.println("<td align=center "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                        out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                        out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"</td>");

                        if(functions.isValueNull((String)temphash.get("fromid")))
                        {
                            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"</td>");
                        }
                        else
                        {
                            out.println("<td align=center "+style+">&nbsp;"+"-"+"</td>");
                        }

                        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid_"+temphash.get("trackingid")+"\" value=\""+temphash.get("toid")+"\" ></td>");
                        if (functions.isValueNull((String)temphash.get("paymentid")))
                        {
                            out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("paymentid")) + "<input type=\"hidden\" name=\"dbpaymentnumber_" + temphash.get("trackingid") + "\" value=\"" + temphash.get("paymentid") + "\"></td>");
                        }
                        else
                        {
                            out.println("<td align=center " + style + ">&nbsp;" + "-" + "<input type=\"hidden\" name=\"dbpaymentnumber_" + temphash.get("trackingid") + "\" value=\"" + temphash.get("paymentid") + "\"></td>");
                        }
                        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description_"+temphash.get("trackingid")+"\" value=\""+temphash.get("description")+"\"></td>");
                        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"amount_"+temphash.get("trackingid")+"\" value=\""+temphash.get("amount")+"\" ></td>");
                        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargebackamount"))+"<input type=\"hidden\" name=\"chargebackamount_"+temphash.get("trackingid")+"\" value=\""+temphash.get("chargebackamount")+"\" ></td>");
                        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"<input type=\"hidden\" name=\"accountid_"+temphash.get("trackingid")+"\" value=\""+temphash.get("accountid")+"\" >"+"<input type=\"hidden\" name=\"fromtype_"+temphash.get("trackingid")+"\" value=\""+temphash.get("fromtype")+"\"></td>");
                        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("timestamp"))+"</td>");
                        out.println("<td align=center"+style+"><select class=\"txtboxtabel\" name=\"status_"+temphash.get("trackingid")+"\" id=\"menu\" onChange=\"checkOption(this,"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+")\">");
                        out.println("<option value=\"null\"default >Select Status Treatment</option>");
                        String isActive=" ";
                        String refundamount=" ";
                        String captureAmount=" ";
                        String payoutAmount="";
                        String chargebackAmount="";

                        if("authstarted".equalsIgnoreCase((String)temphash.get("status")))
                        {
                            isActive="disabled";
                            captureAmount= (String) temphash.get("captureamount");
                            out.println("<option value=authfailed>authfailed</option>");
                            out.println("<option value=authsuccessful>authsuccessful</option>");
                            out.println("<option value=capturesuccess>capturesuccess</option>");
                            out.println("<option value=failed>failed</option>");

                        }
                        else if( "capturestarted".equalsIgnoreCase((String)temphash.get("status")))
                        {
                           /* out.println("<option value=authfailed>authfailed</option>");*/
                            out.println("<option value=capturefailed>capturefailed</option>");
                            out.println("<option value=capturesuccess>capturesuccess</option>");
                            captureAmount= (String) temphash.get("captureamount");
                            /*out.println("<option value=failed>failed</option>");*/
                        }

                        else if( "authstarted_3D".equalsIgnoreCase((String)temphash.get("status")))
                        {
                            captureAmount= (String) temphash.get("captureamount");
                            out.println("<option value=authfailed>authfailed</option>");
                            out.println("<option value=authsuccessful>authsuccessful</option>");
                            out.println("<option value=capturesuccess>capturesuccess</option>");
                            out.println("<option value=failed>failed</option>");
                        }
                        else if( "chargeback".equalsIgnoreCase((String)temphash.get("status")))
                        {
                            out.println("<option value=chargebackreversed>chargebackreversed</option>");
                            out.println("<option value=casefiling>casefiling</option>");
                            /*out.println("<option value=settled>settled</option>");*/
                            chargebackAmount= (String) temphash.get("chargebackamount");
                            captureAmount= (String) temphash.get("captureamount");

                        }
                        else if( "chargebackreversed".equalsIgnoreCase((String)temphash.get("status")))
                        {
                            out.println("<option value=chargebackreversed>chargebackreversed</option>");
                            /*out.println("<option value=capturesuccess>capturesuccess</option>");*/
                            /*out.println("<option value=settled>settled</option>");*/
                            chargebackAmount= (String) temphash.get("chargebackamount");
                            captureAmount= (String) temphash.get("captureamount");
                        }

                        else if( "cancelstarted".equalsIgnoreCase((String)temphash.get("status")))
                        {

                            out.println("<option value=authcancelled>Authorisation cancelled</option>");
                            out.println("<option value=authsuccessful>Auth Successful</option>");
                        }

                        else if( "payoutstarted".equalsIgnoreCase((String)temphash.get("status")))
                        {
                            payoutAmount= (String) temphash.get("payoutamount");
                            captureAmount= "0.00";
                            out.println("<option value=payoutfailed>Payout Failed</option>");
                            out.println("<option value=payoutsuccessful>Payout Successful</option>");
                        }
                        else if( "payoutcancelsuccessful".equalsIgnoreCase((String)temphash.get("status")))
                        {

                            out.println("<option value=payoutcancelfailed>Payout Cancel Failed</option>");

                        }
                        else if( "capturesuccess".equalsIgnoreCase((String) temphash.get("status")))
                        {
                            out.println("<option value=markedforreversal>Reversal Request Sent</option>");
                            /*out.println("<option value=reversed>reversed</option>");*/
                            refundamount= (String) temphash.get("refundamount");
                            captureAmount= (String) temphash.get("captureamount");

                        }
                        else if( "authfailed".equalsIgnoreCase((String) temphash.get("status")))
                        {
                            out.println("<option value=authsuccessful>authsuccessful</option>");
                            out.println("<option value=capturesuccess>capturesuccess</option>");
                            captureAmount= (String) temphash.get("captureamount");

                        }
                        else if( "partialrefund".equalsIgnoreCase((String) temphash.get("status")))
                        {
                            out.println("<option value=reversed>reversed</option>");
                            refundamount= (String) temphash.get("refundamount");
                        }

                        else if( "authsuccessful".equalsIgnoreCase((String) temphash.get("status")))
                        {
                            out.println("<option value=authfailed>authfailed</option>");
                            out.println("<option value=capturesuccess>capturesuccess</option>");
                            out.println("<option value=failed>failed</option>");

                        }
                        else if("markedforreversal".equals(temphash.get("status")))
                        {

                            refundamount= (String) temphash.get("refundamount");
                            captureAmount= (String) temphash.get("captureamount");
                            out.println("<option value=capturesuccess>capturesuccess</option>");
                            out.println("<option value=reversed>reversed</option>");
                        }
                        else if( "failed".equalsIgnoreCase((String) temphash.get("status")))
                        {
                            out.println("<option value=authsuccessful>authsuccessful</option>");
                            out.println("<option value=capturesuccess>capturesuccess</option>");
                            captureAmount= (String) temphash.get("captureamount");
                        }
                        out.println("</select></td>");

                        out.println("<td "+style+">&nbsp; <input class=\"txtboxtabel\" type=\"Text\" id=\"captureamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" name=\"captureamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\""+captureAmount+"\" id=\"menu\" onChange=\"checkOption(this,\""+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" disabled> </td>");
                        out.println("<input type=\"hidden\" name=\"captureamount_" + temphash.get("trackingid") + "\" value=\"" + captureAmount + "\">");
                        out.println("<td "+style+"> <input class=\"txtboxtabel\" type=\"Text\" id=\"refundamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" name=\"refundamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\""+refundamount+"\" id=\"menu\" onChange=\"checkOption(this,\""+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" disabled> </td>");
                        out.println("<td "+style+">&nbsp; <input class=\"txtboxtabel\" type=\"Text\" id=\"chargebackamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" name=\"chargebackAmount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\""+chargebackAmount+"\" id=\"menu\" onChange=\"checkOption(this,\""+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" disabled> </td>");
                        out.println("<td "+style+"> <input class=\"txtboxtabel\" type=\"Text\" id=\"payoutamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" name=\"payoutamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\""+payoutAmount+"\" id=\"menu\" onChange=\"checkOption(this,\""+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" disabled> </td>");
                        out.println("<td "+style+">&nbsp; <input class=\"txtboxtabel\" type=\"Text\" id=\"paymentid_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" name=\"paymentorderno_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" id=\"menu\" onChange=\"checkOption(this,\""+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\") disabled > </td>");
                        out.println("<td "+style+"> <input class=\"txtboxtabel\" type=\"Text\" id=\"remark_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" name=\"remark_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" disabled > </td>");
                        out.println("<td "+style+"> <select name=\"isFraud_" + temphash.get("trackingid")+"\" "+isActive+" id=\"isfraud_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" onChange=\"checkOption(this,\""+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\") disabled ><option value=\"N\" default id=\"menu\"+>N</option><option value=\"Y\">Y</option> </select></td>");
                        out.println("<td style=\"display:none\"><input type=\"hidden\" name=\"prestatus_"+temphash.get("trackingid")+"\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"\" ></td>");
                        out.println("</tr>");
                %>
                <input type="hidden" name="notificationUrl_<%=temphash.get("trackingid")%>" value="<%=temphash.get("notificationUrl")%>">
                <%
                %>
                <input type="hidden" name="reversedAmount_<%=temphash.get("trackingid")%>" value="<%=temphash.get("refundamount")%>">
                <%
                %>
                <input type="hidden" name="currentStatus_<%=temphash.get("trackingid")%>" value="<%=temphash.get("status")%>">
                <%
                %>
                <input type="hidden" name="currentRemark_<%=temphash.get("trackingid")%>" value="<%=temphash.get("remark")%>">
                <%

                    }
                %>

                <thead>
                <tr>
                    <td class="th0" colspan="19">
                        <center><button type="submit" class="addnewmember" value="PROCESS" onclick="return DoChargeback();">DO Process</button></center>
                    </td>
                </tr>
                </thead>
            </table>

        </form>
        <table align=center valign=top><tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                    <jsp:param name="numrows" value="<%=pagerecords%>"/>
                    <jsp:param name="pageno" value="<%=pageno%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="CommonReconList"/>
                    <jsp:param name="currentblock" value="<%=currentblock%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
            </td>
        </tr>
        </table>
    </div>

    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry", "No record Found"));
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