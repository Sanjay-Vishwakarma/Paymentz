<%@ page import="com.directi.pg.Merchants,
                      org.owasp.esapi.ESAPI,
                      java.util.Hashtable,
                      java.util.Enumeration" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Checksum" %>
<%@ page import="com.payment.Mapping" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="Top.jsp" %>

<%
    String totype=(String) session.getAttribute("company");
    session.setAttribute("submit","Terminal"+request.getParameter("terminalid"));
%>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
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
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <title><%=totype%> Merchant Virtual Terminal </title>

    <script language="javascript">

        function myjunk()
        {
            var hat = this.document.myformname.country.selectedIndex
            var hatto = this.document.myformname.country.options[hat].value

            if(hatto == 'US|001')
            {
                $("#us_states_id").attr("disabled", false);
                document.getElementById("b_state").readOnly = true;
            }
            else
            {
                $("#us_states_id").attr("disabled", true);
                document.getElementById("b_state").readOnly = false;
                document.getElementById("b_state").value = "";
            }

            if (hatto != 'Select one')
            {
                this.document.myformname.countrycode.value = hatto.split("|")[0];
                this.document.myformname.telnocc.value = hatto.split("|")[1];
                this.document.myformname.country.options[0].selected=false
            }
        }
        function myjunk1()
        {
            var hat = this.document.myformname.country.selectedIndex
            var hatto = this.document.myformname.country.options[hat].value

            if(hatto == 'USA|001')
            {
                $("#us_states_id").attr("disabled", false);
                document.getElementById("b_state").readOnly = true;
            }
            else
            {
                $("#us_states_id").attr("disabled", true);
                document.getElementById("b_state").readOnly = false;
                document.getElementById("b_state").value = "";
            }

            if (hatto != 'Select one')
            {
                this.document.myformname.countrycode.value = hatto.split("|")[0];
                this.document.myformname.telnocc.value = hatto.split("|")[1];
                this.document.myformname.country.options[0].selected=false
            }
        }

        function usstate()
        {
            var hat = this.document.myformname.us_states.selectedIndex
            var hatto = this.document.myformname.us_states.options[hat].value

            if (hatto != 'Select one')
            {
                this.document.myformname.b_state.value = hatto;
                this.document.myformname.us_states.options[0].selected=true
            }
        }

        function submitForm(actionurl)
        {
            if(!document.myformname.TC.checked)
            {
                alert("Please accept Terms & Condition");
                return false;
            }
            else
            {
                document.myformname.action=actionurl;
            }
        }
    </script>

</head>

<body class="pace-done widescreen fixed-left-void ">

<%
    Functions functions=new Functions();
    Mapping mapping = new Mapping();
    String memberid = (String)session.getAttribute("merchantid");
    String currency =  (String)session.getAttribute("currency");
  //  String totype=(String) session.getAttribute("company");

    String currency1 = request.getParameter("currency");
    String terminalid = request.getParameter("terminalid");
    String accountid = request.getParameter("accountid");
    String cardtype = request.getParameter("cardtype");

    String paymode = request.getParameter("paymodeid");
    if(paymode==null)
    {
        paymode = (String) request.getAttribute("paymenttype");
    }
    String cardtypename = request.getParameter("cardtypename");
    String paymodename =request.getParameter("paymodename");
    String cCPaymentPage=request.getParameter("ccpage");
    String addressDetails=request.getParameter("addressDetails");
    String addressValidation=request.getParameter("addressValidation");
    String multiCurrencySupport=request.getParameter("multiCurrencySupport");
    String sequenceNum = functions.getAutoGeneratedOrderId(memberid);
    System.out.println("sequenceNum   " +sequenceNum);
    String orderId = memberid + "_" + sequenceNum;
    if(cCPaymentPage==null)
    {
        cCPaymentPage=(String)request.getAttribute("ccpage");
    }
    System.out.println(cCPaymentPage);

    Hashtable memberData = mapping.getKeyAlgoAutoSelectTerminal(memberid);

    String key = (String) memberData.get("key");
    String checksumAlgorithm = (String) memberData.get("checksumAlgorithm");
    String autoSelectTerminal = (String) memberData.get("autoSelectTerminal");

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String virtualSingleCall_transaction_details = rb1.getString("virtualSingleCall_transaction_details");
    String virtualSingleCall_merchantid = rb1.getString("virtualSingleCall_merchantid");
    String virtualSingleCall_terminal_id = rb1.getString("virtualSingleCall_terminal_id");
    String virtualSingleCall_paymode = rb1.getString("virtualSingleCall_paymode");
    String virtualSingleCall_card_type = rb1.getString("virtualSingleCall_card_type");
    String virtualSingleCall_currency = rb1.getString("virtualSingleCall_currency");
    String virtualSingleCall_order_description = rb1.getString("virtualSingleCall_order_description");
    String virtualSingleCall_order_id = rb1.getString("virtualSingleCall_order_id");
    String virtualSingleCall_amount = rb1.getString("virtualSingleCall_amount");
    String virtualSingleCall_read = rb1.getString("virtualSingleCall_read");
    String virtualSingleCall_please_note = rb1.getString("virtualSingleCall_please_note");
    String virtualSingleCall_pay = rb1.getString("virtualSingleCall_pay");
%>


<%--<%
    String action = ESAPI.encoder().encodeForHTML(request.getParameter("action"));
    String error=(String) request.getAttribute("error");
    /*String errormsg = null;*/
    if( error!= null)
    {
        /*out.println("<div class=\"vterror\">");
        out.println("<table align=\"center\" width=\"80%\" ><tr><td><font class=\"textb\" >You have <b
        >NOT FILLED</b> some of required details or some of details filled by you are incorrect.<br> Please fill following Invalid details completely before going for next step.<BR></font>");

        out.println("</td></tr><tr><td algin=\"center\" ><font face=\"arial\" class=\"textb\"  size=\"2\">");
        out.println("<br>");
        out.println(error);
        out.println("</font>");
        out.println("</td></tr></table>");
        out.println("</div>");*/

        out.println("<div class=\"vterror\">");
        out.println("<table align=\"center\" width=\"95%\" ><tr><td class=\"textsterror\">You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect.");
        out.println("</td></tr><tr><td class=\"textsterror\" algin=\"center\" ><font size=\"2\">");
        out.println("<br>");
        out.println(error);
        out.println("</font>");
        out.println("</td></tr></table>");
        out.println("</div>");
    }

%>--%>
<%--<div class="container-fluid ">
    <div class="row rowadd" style="margin-top: 50px">
        &lt;%&ndash;<div class="col-md-12 col-md-offset-2" style="margin-left: 0;width:99.666667%">&ndash;%&gt;
        <div style="/*margin-top: 80px*/;margin-bottom: 12px; /*background: rgba(255, 255, 255, 0.7);*/">
            <div class="form foreground bodypanelfont_color panelbody_color">

                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/color:#34495e;padding-top: 5px;font-size: 18px;align: initial;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Transaction Details</h2>
                <hr class="hrform">
            </div>--%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form action="" name="myformname" method="post" accept-charset="UTF-8">

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=virtualSingleCall_transaction_details%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <%
                                    String action = ESAPI.encoder().encodeForHTML(request.getParameter("action"));
                                    String error=(String) request.getAttribute("error");
    /*String errormsg = null;*/
                                    if( error!= null)
                                    {
        /*out.println("<div class=\"vterror\">");
        out.println("<table align=\"center\" width=\"80%\" ><tr><td><font class=\"textb\" >You have <b
        >NOT FILLED</b> some of required details or some of details filled by you are incorrect.<br> Please fill following Invalid details completely before going for next step.<BR></font>");

        out.println("</td></tr><tr><td algin=\"center\" ><font face=\"arial\" class=\"textb\"  size=\"2\">");
        out.println("<br>");
        out.println(error);
        out.println("</font>");
        out.println("</td></tr></table>");
        out.println("</div>");*/

                                        /*out.println("<div class=\"vterror\">");
                                        out.println("<table align=\"center\" width=\"95%\" ><tr><td class=\"textsterror\">You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect.");
                                        out.println("</td></tr><tr><td class=\"textsterror\" algin=\"center\" ><font size=\"2\">");
                                        out.println("<br>");
                                        out.println(error);
                                        out.println("</font>");
                                        out.println("</td></tr></table>");
                                        out.println("</div>");*/
                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+error+"</b></li></center>");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                    }

                                %>
                                <div id="horizontal-form">

                                    <input type="hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(accountid)%>" class="textb">

                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_merchantid%></label>
                                        <input type="text" name="toid" size="10" class="form-control" value=<%=ESAPI.encoder().encodeForHTML(memberid)%> disabled style="border: 1px solid #b2b2b2;font-weight:bold">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_terminal_id%></label>
                                        <input type="hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(accountid)%>" class="textb">
                                        <input type="hidden" name="cardtypename" value="<%=ESAPI.encoder().encodeForHTML(cardtypename)%>" class="textb">
                                        <input type="hidden" name="paymodename" value="<%=ESAPI.encoder().encodeForHTML(paymodename)%>" class="textb">
                                        <input type="hidden" name="terminalid" value="<%=ESAPI.encoder().encodeForHTML(terminalid)%>" class="textb">
                                        <input type="hidden" name="redirecturl" value="" class="form-control">
                                        <input class="form-control" value=<%=ESAPI.encoder().encodeForHTML(terminalid)%>
                                                disabled style="border: 1px solid #b2b2b2;font-weight:bold"  type="Text">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_paymode%></label>
                                        <input type="hidden" name="paymenttype" value="<%=ESAPI.encoder().encodeForHTML(paymode)%>" class="form-control">
                                        <input class="form-control" value=<%=ESAPI.encoder().encodeForHTML(paymodename)%> disabled style="border: 1px solid #b2b2b2;font-weight:bold"  type="Text">
                                    </div>


                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_card_type%></label>
                                        <input type="hidden" name="cardtype" value="<%=ESAPI.encoder().encodeForHTML(cardtype)%>" class="form-control">
                                        <input class="form-control" title="" type="Text" value=<%=ESAPI.encoder().encodeForHTML(cardtypename)%> disabled style="border: 1px solid #b2b2b2;font-weight:bold">
                                    </div>

                                    <%
                                        //String terminalCurrency = ESAPI.encoder().encodeForHTML(currency1);

                                        String textDisabled = "readonly";

                                        if(functions.isValueNull(multiCurrencySupport) && functions.isValueNull(currency1)){
                                            if(multiCurrencySupport.equalsIgnoreCase("Y") && currency1.equalsIgnoreCase("ALL"))
                                            {

                                                currency1 = "";
                                                textDisabled = "";
                                            }
                                        }

                                    %>

                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_currency%></label>
                                        <%--<input type="hidden" name="currency" value="<%=currency1%>" class="form-control">--%>
                                        <input class="form-control" title="" name="currency" style="border: 1px solid #b2b2b2;font-weight:bold" type="Text" value=<%=ESAPI.encoder().encodeForHTML(currency1)%> <%=textDisabled%> >
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_order_description%></label>
                                        <input  type="Text"  name="orderdescription" size="15" maxlength="80" value="Order from VT" class="form-control">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_order_id%></label>
                                        <input  type="Text" name="description" size="10" maxlength="30" class="form-control" readonly value="<%=ESAPI.encoder().encodeForHTML(orderId)%>">
                                    </div>

                                    <div class="form-group col-md-3">
                                        <label><%=virtualSingleCall_amount%></label>
                                        <input type="text" name="amount" size="10"  class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <%
                    if(functions.isValueNull(cCPaymentPage))
                    {
                %>
                <jsp:include page="<%=cCPaymentPage%>"></jsp:include>
                <%
                    }
                %>
                <input type="hidden" name="ccpage" value=<%=ESAPI.encoder().encodeForHTML(cCPaymentPage)%>>
                <input type="hidden" name="toid" value=<%=ESAPI.encoder().encodeForHTML(memberid)%>>
                <input type="hidden" name="totype" value=<%=ESAPI.encoder().encodeForHTML(totype)%>>
                <input type="hidden" name="bitkey" value=<%=ESAPI.encoder().encodeForHTML(key)%>>
                <input type="hidden" name="currency" value=<%=ESAPI.encoder().encodeForHTML(currency)%>>
                <input type="hidden" name="ctoken" value=<%=ESAPI.encoder().encodeForHTML(ctoken)%>>
                <input type="hidden" name="checksumAlgorithm" value=<%=ESAPI.encoder().encodeForHTML(checksumAlgorithm)%>>
                <input type="hidden" name="autoSelectTerminal" value=<%=ESAPI.encoder().encodeForHTML(autoSelectTerminal)%>>
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTML(addressDetails)%>" name="addressDetails">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTML(addressValidation)%>" name="addressValidation">
                <%--<input type="hidden" value="<%=multiCurrencySupport%>" name="multiCurrencySupport">--%>
                <input type="hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(accountid)%>" class="textb">

                <%--<div class="container-fluid ">
                    <div class="row rowadd" style="margin-top: 10px;">--%>
                <%--<div class="bg-info" style="margin-top: 15px;/*margin-left:29px;margin-right: 14px;*/margin-bottom: 12px;/*background-color: #ffffff*/" align="center">--%>
                <%--<div style="text-align: center">
                    <label>
                        <input type="checkbox" id="terms" value="tc" name="TC" >&nbsp;<b>I have read the Terms and condition and I accept the same.</b><br><br>
                    </label>
                </div>--%>

                <div class="bg-info" style="text-align:center;background-color: #ffffff;color: #000000;border-left: 4px solid #34495e;border: #ffffff">
                    <i class="fa fa-right"></i>&nbsp;&nbsp;
                    <input type="checkbox" id="terms" value="tc" name="TC" >&nbsp<b><%=virtualSingleCall_read%></b>
                </div>

                <div class="bg-info" style="text-align:center;">
                    <i class="fa fa-info-circle"></i>&nbsp;&nbsp;
                   <%=virtualSingleCall_please_note%>
                </div>
                <%--</div>
            </div>--%>

                <div class="form-group col-md-5">

                </div>
                <div class="form-group col-md-5">
                    <button type="submit" name="Submit" class="btn btn-default" onclick="return submitForm('/merchant/servlet/VirtualSingleCall?ctoken=<%=ctoken%>');">
                        <%--<i class="fa fa-"></i>--%>
                       <%=virtualSingleCall_pay%>
                    </button>
                </div>
                <div class="form-group col-md-2">

                </div>
                <input type="hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(accountid)%>" class="textb">

            </form>
        </div>
    </div>
</div>
</body>
</html>