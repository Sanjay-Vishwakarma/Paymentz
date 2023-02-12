<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 4/14/15
  Time: 4:57 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page errorPage="error.jsp"
         import="org.owasp.esapi.ESAPI" %>

<%@ include file="Top.jsp" %>


<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script language="JavaScript" src="/merchant/FusionCharts/FusionCharts.js?ver=1"></script>
    <link rel="stylesheet" type="text/css" href="/merchant/FusionCharts/style.css" >--%>

<%--
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
--%>
    <%--<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>--%>
    <script type="text/javascript">
        function checkValue()
        {
            var hat = document.getElementById("rec_interval").value.trim();
            if(hat!='')
            {
                var hatto = document.getElementById("interval").options.length;
                for(i=0;i<hatto;i++)
                {
                    var intervalList = document.getElementById("interval").options[i].value.trim();
                    if(hat==intervalList)
                    {
                        document.getElementById("interval").options[i].selected=true;
                    }
                }
            }

            var mHat = document.getElementById("rec_frequency").value.trim();
            if(mHat!='')
            {
                var mHatto = document.getElementById("frequency").options.length;
                for(i=0;i<mHatto;i++)
                {
                    var frequencyList = document.getElementById("frequency").options[i].value.trim();
                    if(mHat==frequencyList)
                    {
                        document.getElementById("frequency").options[i].selected=true;
                    }
                }
            }

            var rHat = document.getElementById("rec_rundate").value.trim();
            if(rHat!='')
            {
                var rHatto = document.getElementById("runDate").options.length;
                for(i=0;i<rHatto;i++)
                {
                    var runList = document.getElementById("runDate").options[i].value.trim();
                    if(rHat==runList)
                    {
                        document.getElementById("runDate").options[i].selected=true;
                    }
                }
            }

            var aHat = document.getElementById("act").value.trim();
            if(aHat!='')
            {
                var aHatto = document.getElementById("actDeact").options.length;
                for(i=0;i<aHatto;i++)
                {
                    var actList = document.getElementById("actDeact").options[i].value.trim();
                    if(aHat==actList)
                    {
                        document.getElementById("actDeact").options[i].selected=true;
                    }
                }
            }
        }
    </script>
    <%
        String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String manualRebill_Go_Back = StringUtils.isNotEmpty(rb1.getString("manualRebill_Go_Back"))?rb1.getString("manualRebill_Go_Back"): "Go Back";
        String manualRebill_TransactionDetails = StringUtils.isNotEmpty(rb1.getString("manualRebill_TransactionDetails"))?rb1.getString("manualRebill_TransactionDetails"): "Transaction Details";
        String manualRebill_recurring = StringUtils.isNotEmpty(rb1.getString("manualRebill_recurring"))?rb1.getString("manualRebill_recurring"): "Recurring Billing Id";
        String manualRebill_parent = StringUtils.isNotEmpty(rb1.getString("manualRebill_parent"))?rb1.getString("manualRebill_parent"): "Parent Tracking ID";
        String manualRebill_CardNumber = StringUtils.isNotEmpty(rb1.getString("manualRebill_CardNumber"))?rb1.getString("manualRebill_CardNumber"): "Card Number";
        String manualRebill_holder = StringUtils.isNotEmpty(rb1.getString("manualRebill_holder"))?rb1.getString("manualRebill_holder"): "Card Holder Name";
        String manualRebill_unique = StringUtils.isNotEmpty(rb1.getString("manualRebill_unique"))?rb1.getString("manualRebill_unique"): "Unique Order Id";
        String manualRebill_Amount = StringUtils.isNotEmpty(rb1.getString("manualRebill_Amount"))?rb1.getString("manualRebill_Amount"): "Amount";
        String manualRebill_PayNow = StringUtils.isNotEmpty(rb1.getString("manualRebill_PayNow"))?rb1.getString("manualRebill_PayNow"): "Pay Now";
    %>


</head>
<title> <%=company%> | Recurring Billing Modification</title>

<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/merchant/servlet/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post">

                        <%--<%
                            Enumeration<String> requestName=request.getParameterNames();
                            while(requestName.hasMoreElements())
                            {
                                String name=requestName.nextElement();
                                if("trackingid".equals(name))
                                {
                                    out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                                }
                                else
                                out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                            }
                        %>--%>
                            <input type="hidden" value="<%=request.getParameter("terminalbuffer")%>" name="terminalbuffer">
                        <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/merchant/images/goBack.png">
                        </button>--%>
                            <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=manualRebill_Go_Back%></button>

                    </form>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=manualRebill_TransactionDetails%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form name="form" method = "post" action="/merchant/servlet/ManualRebill?ctoken=<%=ctoken%>" id="form1" name="myformname">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">


                                    <%
                                        if(request.getAttribute("error")!=null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("error") + "</h5>");
                                        }
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=manualRebill_recurring%></label>
                                        <input type="text" name="rbid" size="10" class="form-control" value="<%=functions.isValueNull(request.getParameter("rbid"))?ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("rbid")):""%>" disabled style="border: 1px solid #b2b2b2;font-weight:bold">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=manualRebill_parent%></label>
                                        <input type="text" name="trackingid" size="10" class="form-control" value=<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("trackingid"))%> disabled style="border: 1px solid #b2b2b2;font-weight:bold">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=manualRebill_CardNumber%></label>
                                        <input type="hidden" name="cardnum" value="<%=request.getParameter("firstsix")+"******"+request.getParameter("lastfour")%>" class="form-control">

                                        <input class="form-control" value=<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("firstsix")+"******"+request.getParameter("lastfour"))%> disabled style="border: 1px solid #b2b2b2;font-weight:bold"  type="Text">

                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=manualRebill_holder%></label>
                                        <input type="hidden" name="name" value="<%=request.getParameter("name")%>" class="form-control">
                                        <input class="form-control" title="" type="Text" value=<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("name"))%> disabled style="border: 1px solid #b2b2b2;font-weight:bold"  type="Text">

                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=manualRebill_unique%></label>
                                        <input  type="Text"  name="description" size="15" maxlength="50" class="form-control">

                                    </div>

                                    <%--<tr>
                                        <td style="padding:2px " class="textb">&nbsp;</td>
                                        <td style="padding:2px " class="textb">Email Address</td>
                                        <td style="padding:2px " class="textb">:</td>
                                        <td style="padding:2px ">
                                            <input  type="Text"  name="emailaddr" size="15" maxlength="50" class="txtbox"></td>
                                    </tr>--%>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=manualRebill_Amount%></label>
                                        <input type="text" name="amount" size="10"  class="form-control">

                                    </div>

                                    <%--<tr class="tr0" align="center">
                                        <td align="center" colspan="2"><input type="submit" class="goto" value="Pay Now"></td>
                                    </tr>--%>
                                    <div class="form-group col-md-9 has-feedback">&nbsp;</div>
                                    <div class="form-group col-md-3">
                                        <div class="textb" style="margin-left: 13%">
                                            <button type="submit" name="Submit" class="btn btn-default">
                                                &nbsp;<%=manualRebill_PayNow%>
                                            </button>
                                        </div>
                                    </div>

                                    <input type="hidden" name="rbid" value="<%=request.getParameter("rbid")%>">
                                    <input type="hidden" name="trackingid" value="<%=request.getParameter("trackingid")%>">
                                    <input type="hidden" name="firstsix" value="<%=request.getParameter("firstsix")%>">
                                    <input type="hidden" name="lastfour" value="<%=request.getParameter("lastfour")%>">
                                    <input type="hidden" value="<%=request.getParameter("terminalbuffer")%>" name="terminalbuffer">

                                </form>
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