<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--/**
* Created with IntelliJ IDEA.
* User: sanjeet
* Date: 8/3/15
* Time: 7:24 PM
* To change this template use File | Settings | File Templates.
*/--%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","Reports");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title><%=company%> | Partner Merchant Wire Report Updation</title>

    <style type="text/css">
        .table > thead > tr > th {font-weight: inherit;}
        /********************Table Responsive Start**************************/
        @media (max-width: 640px){
            table {border: 0;}
            table thead { display: none;}
            tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}
            table td {
                display: block;
                border-bottom: none;
                padding-left: 0;
                padding-right: 0;
            }
            table td:before {
                content: attr(data-label);
                float: left;
                width: 100%;
                font-weight: bold;
            }
        }
        table {
            width: 100%;
            max-width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            display: table;
            border-collapse: separate;
            border-color: grey;
        }
        thead {
            display: table-header-group;
            vertical-align: middle;
            border-color: inherit;
        }
        tr:nth-child(odd) {background: #F9F9F9;}
        tr {
            display: table-row;
            vertical-align: inherit;
            border-color: inherit;
        }
        th {padding-right: 1em;text-align: left;font-weight: bold;}
        td, th {display: table-cell;vertical-align: inherit;}
        tbody {
            display: table-row-group;
            vertical-align: middle;
            border-color: inherit;
        }
        td {
            padding-top: 6px;
            padding-bottom: 6px;
            padding-left: 10px;
            padding-right: 10px;
            vertical-align: top;
            border-bottom: none;
        }
        .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}
        /********************Table Responsive Ends**************************/
        @media(max-width: 991px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit!important;
            }
        }
        @media (min-width: 768px){
            .form-horizontal .control-label {
                text-align: left!important;
            }
        }

    </style>
    <style type="text/css">
        .field-icon
        {
            float:  right;
            margin-top: -25px;
            position: relative;
            z-index: 2;
        }


    </style>
    <script language="javascript">
        function myjunk()
        {
            var hat = this.document.form2.country.selectedIndex;
            var hatto = this.document.form2.country.options[hat].value;
            var countrycd = this.document.form2.phonecc.value = hatto.split("|")[1];
            var telnumb = this.document.form2.telno.value;
            if (hatto != 'Select one') {

                this.document.form2.countrycode.value = hatto.split("|")[0];
                this.document.form2.phonecc.value = hatto.split("|")[1];
                this.document.form2.country.options[0].selected=false;
            }
        }
    </script>

    <script src="/merchant/javascript/hidde.js"></script>
</head>
<body class="pace-done widescreen fixed-left-void" onload="bodyonload()">
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        Map<String,String> ynMap=new HashMap<String, String>();
        ynMap.put("N","No");
        ynMap.put("Y","Yes");
        String fromdate="";
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String listMerchantWireEdit_Report_Table = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Report_Table")) ? rb1.getString("listMerchantWireEdit_Report_Table") : "Report Table";
        String listMerchantWireEdit_Merchant_Id = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Merchant_Id")) ? rb1.getString("listMerchantWireEdit_Merchant_Id") : "Merchant Id*";
        String listMerchantWireEdit_Terminal_ID = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Terminal_ID")) ? rb1.getString("listMerchantWireEdit_Terminal_ID") : "Terminal ID";
        String listMerchantWireEdit_First_Date = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_First_Date")) ? rb1.getString("listMerchantWireEdit_First_Date") : "First Date";
        String listMerchantWireEdit_Last_Date = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Last_Date")) ? rb1.getString("listMerchantWireEdit_Last_Date") : "Last Date";
        String listMerchantWireEdit_Settled = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Settled")) ? rb1.getString("listMerchantWireEdit_Settled") : "Settled ID";
        String listMerchantWireEdit_Cycle_ID = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Cycle_ID")) ? rb1.getString("listMerchantWireEdit_Cycle_ID") : "Cycle ID";
        String listMerchantWireEdit_net = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_net")) ? rb1.getString("listMerchantWireEdit_net") : "Net Final Amount";
        String listMerchantWireEdit_unpaid = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_unpaid")) ? rb1.getString("listMerchantWireEdit_unpaid") : "Unpaid Amount";
        String listMerchantWireEdit_Currency = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Currency")) ? rb1.getString("listMerchantWireEdit_Currency") : "Currency";
        String listMerchantWireEdit_Status = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Status")) ? rb1.getString("listMerchantWireEdit_Status") : "Status";
        String listMerchantWireEdit_Paid = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_Paid")) ? rb1.getString("listMerchantWireEdit_Paid") : "Paid";
        String listMerchantWireEdit_UnPaid = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_UnPaid")) ? rb1.getString("listMerchantWireEdit_UnPaid") : "UnPaid";
        String listMerchantWireEdit_save = StringUtils.isNotEmpty(rb1.getString("listMerchantWireEdit_save")) ? rb1.getString("listMerchantWireEdit_save") : "Save";
%>
<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/net/MerchantWireReports?ctoken=<%=ctoken%>" method="post" name="form">
                        <%
                            Enumeration<String> stringEnumeration=request.getParameterNames();
                            while(stringEnumeration.hasMoreElements())
                            {
                                String name=stringEnumeration.nextElement();
                                if ("fromdate".equals(name))
                                {
                                    out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                }
                                if ("todate".equals(name))
                                {
                                    out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                }
                            }
                        %>
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br><br><br>
            <div class="row reporttable">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=listMerchantWireEdit_Report_Table%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <form action="/partner/net/EditMerchantWire?ctoken=<%=ctoken%>" method="post" name="form2" class="form-horizontal">
                            <input type="hidden" name="ctoken" value="<%=ctoken%>">
                            <input type="hidden" name="fromdate" value="<%=fromdate%>">

                                <%
                                String status = (String) request.getAttribute("status");
                                if (status!=null)
                                {
                                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + status + "</h5>");
                                }
                                String action = (String) request.getAttribute("action");
                                HashMap hash = (HashMap) request.getAttribute("memberDetail");
                                String isreadonly = "";
                                if (partnerFunctions.isValueNull((String)request.getAttribute("isreadonly")))
                                    isreadonly =(String) request.getAttribute("isreadonly");

                                String conf = " ";
                                String update=" ";

                                HashMap innerhash = new HashMap();

                                int records=0;
                                if(isreadonly.equalsIgnoreCase("view"))
                                {
                                    conf = "disabled";
                                }
                                else
                                {
                                    update="update";
                                    action="modify";
                                }
                                if (hash.size() > 0)
                                {
                                    String style="class=tr0";
                                    innerhash = (HashMap) hash.get(1 + "");

                                    String terminalid = "";
                                    String memberId = "";
                                    String firstdate="";
                                    String lastdate="";
                                    String settledid="";
                                    String settlementcycle_no="";
                                    String netfinalamount = "";
                                    String unpaidamount="";
                                    String currency = "";

                                    Functions functions = new Functions();

                                    if (functions.isValueNull((String)innerhash.get("toid"))) memberId = (String) innerhash.get("toid");
                                    if (functions.isValueNull((String)innerhash.get("terminalid"))) terminalid = (String) innerhash.get("terminalid");
                                    if (functions.isValueNull((String)innerhash.get("firstdate"))) firstdate = (String) innerhash.get("firstdate");
                                    if (functions.isValueNull((String)innerhash.get("lastdate"))) lastdate = (String) innerhash.get("lastdate");
                                    if (functions.isValueNull((String)innerhash.get("settledid"))) settledid = (String) innerhash.get("settledid");
                                    if (functions.isValueNull((String)innerhash.get("settlementcycle_no"))) settlementcycle_no = (String) innerhash.get("settlementcycle_no");
                                    if (functions.isValueNull((String)innerhash.get("netfinalamount"))) netfinalamount = (String) innerhash.get("netfinalamount");
                                    if (functions.isValueNull((String)innerhash.get("unpaidamount"))) unpaidamount = (String) innerhash.get("unpaidamount");
                                    if (functions.isValueNull((String)innerhash.get("currency"))) currency = (String) innerhash.get("currency");
                                    if (functions.isValueNull((String)innerhash.get("status"))) status = (String) innerhash.get("status");
                                    %>
                            <input type="hidden" size="30" name="update" value="<%=update%>">
                            <input type="hidden" size="30" name="action" value="<%=action%>">
                            <div class="widget-content padding">
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_Merchant_Id%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>"readonly>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_Terminal_ID%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="terminalid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>" readonly>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_First_Date%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="firstdate" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstdate)%>" readonly>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_Last_Date%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="lastdate" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastdate)%>" readonly>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_Settled%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="settledid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(settledid)%>" readonly>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_Cycle_ID%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="cycleId" value="<%=ESAPI.encoder().encodeForHTMLAttribute(settlementcycle_no)%>" readonly>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_net%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="netfinalamount" value="<%=ESAPI.encoder().encodeForHTMLAttribute(netfinalamount)%>"disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_unpaid%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="unpaidamount" value="<%=ESAPI.encoder().encodeForHTMLAttribute(unpaidamount)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_Currency%></label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="currency" value="<%=ESAPI.encoder().encodeForHTMLAttribute(currency)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=listMerchantWireEdit_Status%></label>
                                    <div class="col-md-4">
                                        <select name="status" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>"  <%--onchange="myjunk();"--%> <%--<%=conf%> --%>>
                                            <%--<option value="">Select status</option>--%>
                                            <%
                                                if (functions.isValueNull(status) && status.equals("paid"))
                                                {
                                                %>
                                                    <option value="paid" selected><%=listMerchantWireEdit_Paid%></option>
                                                    <option value="unpaid"><%=listMerchantWireEdit_UnPaid%></option>
                                                    <option value="carryforward">Carryforward</option>
                                                    <option value="partialPaid">Partial Paid</option>
                                                <%
                                                }else if (functions.isValueNull(status) && status.equals("unpaid"))
                                                {
                                                %>
                                                <option value="paid" ><%=listMerchantWireEdit_Paid%></option>
                                                <option value="unpaid" selected><%=listMerchantWireEdit_UnPaid%></option>
                                                <option value="carryforward">Carryforward</option>
                                                <option value="partialPaid">Partial Paid</option>
                                                    <%
                                                }else if (functions.isValueNull(status) && status.equals("carryforward"))
                                                {
                                                %>
                                                <option value="paid" ><%=listMerchantWireEdit_Paid%></option>
                                                <option value="unpaid"><%=listMerchantWireEdit_UnPaid%></option>
                                                <option value="carryforward" selected>Carryforward</option>
                                                <option value="partialPaid">Partial Paid</option>
                                                    <%
                                                }
                                                else {
                                                %>
                                                    <option value="paid"><%=listMerchantWireEdit_Paid%></option>
                                                    <option value="unpaid" ><%=listMerchantWireEdit_UnPaid%></option>
                                                    <option value="carryforward">Carryforward</option>
                                                    <option value="partialPaid" selected>Partial Paid</option>
                                                <%
                                                }
                                            %>
                                    </div>
                                    <div class="col-md-6"></div>

                                    <div class="form-group col-md-12 has-feedback">
                                        <center>
                                            <%
                                                out.println("<input type=\"hidden\" name=\"memberid\" value=\"" + innerhash.get("memberid") + "\"><input type=\"submit\" style=\"margin-top: 20px\" class=\"gotoauto btn btn-default\" name=\"action1\" value="+listMerchantWireEdit_save+">");
                                            %>
                                        </center>
                                    </div>
                                </div>
                            </div>
                         </div>
                    </form>
                </div>
            </div>
            <%
                    }
                    else if (!partnerFunctions.isValueNull((String)request.getAttribute("status")))
                    {
                        out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                    }
                }
                else
                {
                    response.sendRedirect("/partner/Logout.jsp");
                    return;
                }
            %>
        </div>
    </div>
</div>
</div>
</body>
</html>