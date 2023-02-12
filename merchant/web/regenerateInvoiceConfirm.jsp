<%--
  Created by IntelliJ IDEA.
  User: Nikhil Poojari
  Date: 02-Jun-17
  Time: 12:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.Merchants,
                 org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 java.util.Enumeration" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.directi.pg.Merchants" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="org.eclipse.jdt.internal.compiler.ast.ThisReference" %>
<%@ include file="ietest.jsp" %>

<%
    String memberid = (String)session.getAttribute("merchantid");
    String currency =  (String)session.getAttribute("currency");
    Hashtable hiddenvariables = (Hashtable)request.getAttribute("hiddenvariables");
    Hashtable hash1 = (Hashtable)request.getAttribute("paymodelist");
    String error=(String) request.getAttribute("error");
    String validationError=request.getAttribute("validationError")!=null?(String)request.getAttribute("validationError"):"";
    String orderid = (String) request.getAttribute("orderid");
    String address = "";
    String city = "";
    String state = "";
    String country = "";
    String zip = "";
    String phone = "";
    String langForInvoice = "";

    if (functions.isValueNull((String) hiddenvariables.get("address")))
    {
        address = (String) hiddenvariables.get("address");
    }
    else
    {
        address = "";
    }

    if (functions.isValueNull((String) hiddenvariables.get("city")))
    {
        city = (String) hiddenvariables.get("city");
    }
    else
    {
        city = "";
    }

    if (functions.isValueNull((String) hiddenvariables.get("state")))
    {
        state = (String) hiddenvariables.get("state");
    }
    else
    {
        state = "";
    }

    if (functions.isValueNull((String) hiddenvariables.get("country")))
    {
        country = (String) hiddenvariables.get("country");
    }
    else
    {
        country = "";
    }

    if (functions.isValueNull((String) hiddenvariables.get("zipcode")))
    {
        zip = (String) hiddenvariables.get("zipcode");
    }
    else
    {
        zip = "";
    }

    if (functions.isValueNull((String) hiddenvariables.get("phonecc")))
    {
        phone = hiddenvariables.get("phonecc") + "-" + hiddenvariables.get("phone");
    }
    else
    {
        phone = "";
    }

    if (functions.isValueNull((String) hiddenvariables.get("langForInvoice")))
    {
        langForInvoice = hiddenvariables.get("langForInvoice") + "-" + hiddenvariables.get("langForInvoice");
    }
    else
    {
        langForInvoice = "EN";
    }
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title>Merchant Order Payments Confirmation</title>

    <style type="text/css">
        @media (min-width: 768px) {
            .form-horizontal .control-label {
                text-align: left !important;
            }

            #pcontent{
                padding-top: 9px;
            }
        }

        @media (min-width: 992px){
            .form-horizontal .control-label {
                padding-left: 0;
            }
        }

        @media (max-width: 479px){
            .redirecturl_class{
                word-break:break-all;
            }
        }

        #pcontent{
            /*height: 34px;
            padding: 6px 12px;*/
            font-size: 13px;
        }
    </style>

</head>
<%@ include file="Top.jsp"%>
<body>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">


            <form name="form" method="post" action="/merchant/servlet/Invoice?ctoken=<%=ctoken%>">
                <div class="pull-right">
                    <div class="btn-group">

                        <%

                            Enumeration<String> aName=request.getParameterNames();
                            while(aName.hasMoreElements())
                            {
                                String name=aName.nextElement();
                                String value = request.getParameter(name);
                                if(value==null || value.equals("null"))
                                {
                                    value = "";
                                }
                        %>

                        <input type=hidden name=<%=name%> value=<%=value%>>
                        <%
                            }

                        %>
                        <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/merchant/images/goBack.png">
                        </button>--%>
                        <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;Go Back</button>
                    </div>
                </div>
            </form>
            <br><br><br>


            <div class="row">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Invoice Regeneration Confirmation</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <%
                            if(error!="" && error!=null && !error.equals(null))
                            {

                        %>
                        <h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;Cannot Regenerate Invoice due to the following Error<br><%=error%></h5>
                        <%
                        }
                        else
                        {
                        %>
                        <br>


                        <form action="RegenerateInvoice?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">
                            <div class="widget-content padding">


                                <font color=red><%=validationError%></font><br>


                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Order ID :</label>
                                    <div class="col-md-4">
                                        <input type=text name=orderid class="form-control" disabled value=<%=orderid%>>
                                        <input type="hidden" name=orderid value=<%=orderid%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Order Description :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=hiddenvariables.get("orderdesc")%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Order Amount :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=hiddenvariables.get("amount")%>&nbsp;&nbsp;<%=currency%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Language For Invoice :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=hiddenvariables.get("langForInvoice")%>&nbsp;&nbsp;</p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer Email ID :</label>
                                    <div class="col-md-4">
                                        <input type=text name="custemail" size="100" class="form-control" value=<%=hiddenvariables.get("custemail")%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer Address :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=address%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer City :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=city%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer State :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=state%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer Country :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=country%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer Zip Code :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=zip%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer Phone No :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=phone%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Pay Mode :</label>
                                    <div class="col-md-4">
                                        <select name=paymenttype class="form-control">
                                            <%--<option value="" selected>Select Pay Mode</option>--%>
                                            <%
                                                Enumeration paymodeenum = hash1.keys();
                                                String paytypekey = "";
                                                String paytypevalue = "";
                                                String isCreditCard ="";
                                            %>
                                            <%--<option value="<%=ESAPI.encoder().encodeForHTMLAttribute(paytypekey)%>" selected>
                                                <%=ESAPI.encoder().encodeForHTML(paytypevalue)%>
                                            </option>--%>
                                            <%
                                                while (paymodeenum.hasMoreElements())
                                                {
                                                    paytypekey = (String) paymodeenum.nextElement();
                                                    paytypevalue = (String)hash1.get(paytypekey);

                                                    if(paytypekey.equals(hiddenvariables.get("paymodeid")))
                                                    {
                                                        isCreditCard="selected";
                                                    }
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(paytypekey)%>"  <%=isCreditCard%>>
                                                <%=ESAPI.encoder().encodeForHTML(paytypevalue)%>
                                            </option>
                                            <%
                                                }
                                            %>


                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Invoice will Expire after :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent"><%=hiddenvariables.get("expirationPeriod")%> Hours</p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label">Customer Redirect URL :</label>
                                    <div class="col-md-4">
                                        <p id="pcontent" class="redirecturl_class"><%=hiddenvariables.get("redirecturl")%></p>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>


                                <div class="form-group">
                                    <div class="col-md-3"></div>
                                    <label class="col-md-2 control-label" style="visibility:hidden;">Button</label>
                                    <div class="col-md-4">
                                        <button type="submit" name="submit" class="btn btn-default" id="submit" <%--style="background: rgb(126, 204, 173);"--%>>
                                            <i class="fa fa-repeat"></i>
                                            &nbsp;Re-generate
                                        </button>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <input type=hidden name="invoiceno" value=<%=hiddenvariables.get("invoiceno")%> >

                            </div>


                        </form>
                        <%
                            }
                        %>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>
