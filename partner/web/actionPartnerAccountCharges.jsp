<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit", "partnerMerchantCharges");
    //String partnerId =(String) session.getAttribute("merchantid");

    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    Functions functions = new Functions();
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String actionPartnerAccountCharges_Update_Merchant = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Update_Merchant")) ? rb1.getString("actionPartnerAccountCharges_Update_Merchant") : "Update Merchant Charges";
    String actionPartnerAccountCharges_MemberId = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_MemberId")) ? rb1.getString("actionPartnerAccountCharges_MemberId") : "Member Id :";
    String actionPartnerAccountCharges_TerminalId = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_TerminalId")) ? rb1.getString("actionPartnerAccountCharges_TerminalId") : "Terminal Id :";
    String actionPartnerAccountCharges_Charge_Name = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Charge_Name")) ? rb1.getString("actionPartnerAccountCharges_Charge_Name") : "Charge Name :";
    String actionPartnerAccountCharges_Dynamic_Input = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Dynamic_Input")) ? rb1.getString("actionPartnerAccountCharges_Dynamic_Input") : "Dynamic Input :";
    String actionPartnerAccountCharges_Start_Date = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Start_Date")) ? rb1.getString("actionPartnerAccountCharges_Start_Date") : "Start Date* :";
    String actionPartnerAccountCharges_End_Date = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_End_Date")) ? rb1.getString("actionPartnerAccountCharges_End_Date") : "End Date* :";
    String actionPartnerAccountCharges_Merchant_Rate = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Merchant_Rate")) ? rb1.getString("actionPartnerAccountCharges_Merchant_Rate") : "Merchant Rate* :";
    String actionPartnerAccountCharges_agent = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_agent")) ? rb1.getString("actionPartnerAccountCharges_agent") : "Agent Commission* :";
    String actionPartnerAccountCharges_partner = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_partner")) ? rb1.getString("actionPartnerAccountCharges_partner") : "Partner Commission*:";
    String actionPartnerAccountCharges_Sequence_Number = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Sequence_Number")) ? rb1.getString("actionPartnerAccountCharges_Sequence_Number") : "Sequence Number :";
    String actionPartnerAccountCharges_Button = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Button")) ? rb1.getString("actionPartnerAccountCharges_Button") : "Button";
    String actionPartnerAccountCharges_Update = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Update")) ? rb1.getString("actionPartnerAccountCharges_Update") : "Update";
    String actionPartnerAccountCharges_SrNo = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_SrNo")) ? rb1.getString("actionPartnerAccountCharges_SrNo") : "Sr. No";
    String actionPartnerAccountCharges_MappingId = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_MappingId")) ? rb1.getString("actionPartnerAccountCharges_MappingId") : "Mapping Id";
    String actionPartnerAccountCharges_MerchantId = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_MerchantId")) ? rb1.getString("actionPartnerAccountCharges_MerchantId") : "Merchant Id";
    String actionPartnerAccountCharges_AccountId = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_AccountId")) ? rb1.getString("actionPartnerAccountCharges_AccountId") : "Account Id";
    String actionPartnerAccountCharges_TerminalId1 = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_TerminalId1")) ? rb1.getString("actionPartnerAccountCharges_TerminalId1") : "Terminal Id";
    String actionPartnerAccountCharges_Partner_Charge = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_Partner_Charge")) ? rb1.getString("actionPartnerAccountCharges_Partner_Charge") : "Partner Charge";
    String actionPartnerAccountCharges_AgentCharge = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_AgentCharge")) ? rb1.getString("actionPartnerAccountCharges_AgentCharge") : "Agent Charge";
    String actionPartnerAccountCharges_MemberCharge = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_MemberCharge")) ? rb1.getString("actionPartnerAccountCharges_MemberCharge") : "Member Charge";
    String actionPartnerAccountCharges_StartDate = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_StartDate")) ? rb1.getString("actionPartnerAccountCharges_StartDate") : "Start Date";
    String actionPartnerAccountCharges_EndDate = StringUtils.isNotEmpty(rb1.getString("actionPartnerAccountCharges_EndDate")) ? rb1.getString("actionPartnerAccountCharges_EndDate") : "End Date";

%>
<head>
    <title><%=company%> | Merchant Charges</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script type="text/javascript" language="JavaScript" src="/partner/javascript/memberid_terminal.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd", endDate: '+10y'});
        });
    </script>
    <style type="text/css">
        #main {
            background-color: #ffffff
        }

        :target:before {
            content: "";
            display: block;
            height: 50px;
            margin: -50px 0 0;
        }

        .table > thead > tr > th {
            font-weight: inherit;
        }

        :target:before {
            content: "";
            display: block;
            height: 90px;
            margin: -50px 0 0;
        }

        footer {
            border-top: none;
            margin-top: 0;
            padding: 0;
        }

        /********************Table Responsive Start**************************/
        @media (max-width: 640px) {
            table {
                border: 0;
            }

            table thead {
                display: none;
            }

            tr:nth-child(odd), tr:nth-child(even) {
                background: #ffffff;
            }

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

        tr:nth-child(odd) {
            background: #F9F9F9;
        }

        tr {
            display: table-row;
            vertical-align: inherit;
            border-color: inherit;
        }

        th {
            padding-right: 1em;
            text-align: left;
            font-weight: bold;
        }

        td, th {
            display: table-cell;
            vertical-align: inherit;
        }

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

        .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
            border-top: 1px solid #ddd;
        }

        /********************Table Responsive Ends**************************/
        @media (min-width: 768px) {
            .form-horizontal .control-label {
                text-align: left !important;
            }
        }
    </style>
</head>
<body>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/net/PartnerMerchantCharges?ctoken=<%=ctoken%>" method="post" name="form">

                        <%
                            Enumeration<String> stringEnumeration=request.getParameterNames();
                            while(stringEnumeration.hasMoreElements())
                            {
                                String name=stringEnumeration.nextElement();
                                out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                            }
                        %>
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br><br><br>

            <div class="col-md-12 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=actionPartnerAccountCharges_Update_Merchant%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <br>

                    <div class="widget-content padding">
                        <div id="horizontal-form">
                            <%
                                String errormessage = (String) request.getAttribute("errormessage");
                                String success = (String) request.getAttribute("successmessage");

                                if (functions.isValueNull(errormessage))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormessage + "</h5>");
                                }
                                if (functions.isValueNull(success))
                                {
                                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"></i>&nbsp;&nbsp;" + success + "</h5>");
                                }
                            %>

                            <%
                                HashMap hash = (HashMap) request.getAttribute("chargedetails");
                                String action = (String) request.getAttribute("action");
                                if (hash != null)
                                {
                                    if (action.equalsIgnoreCase("history"))
                                    {
                            %>
                            <div class="widget-content padding" style="overflow-y: auto;">
                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_SrNo%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_MappingId%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_MerchantId%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_AccountId%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_TerminalId1%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_Partner_Charge%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_AgentCharge%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_MemberCharge%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_StartDate%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=actionPartnerAccountCharges_EndDate%></b></td>
                                    </tr>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    </thead>
                                    <%
                                        Object mappingId;
                                        TreeMap mappingMap = new TreeMap(hash);
                                        Iterator itr2 = mappingMap.keySet().iterator();
                                        int i = 1;
                                        while (itr2.hasNext())
                                        {
                                            String style = "class=td1";
                                            mappingId = itr2.next();
                                            HashMap innerHash = (HashMap) mappingMap.get(mappingId);
                                            out.println("<tr>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + i + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("mappingid")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("memberid")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("accountid")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("terminalid")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("partnerCommision")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("agentCommision")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("merchantChargeValue")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("effectiveStartDate")) + "</td>");
                                            out.println("<td " + style + " style='text-align: center;'>&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerHash.get("effectiveEndDate")) + "</td>");
                                            out.println("</tr>");
                                            i = i + 1;
                                        }
                                    %>
                                </table>
                            </div>
                            <%
                            }
                            else
                            {
                            %>
                            <div class="widget-content padding" style="overflow-y: auto;">
                                <form action="/partner/net/UpdatePartnerAccountsCharges?ctoken=<%=ctoken%>"
                                      method="post" name="forms" class="form-horizontal">
                                    <input type="hidden" name="mappingid" value="<%=(String)hash.get("mappingid")%>">

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_MemberId%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" name="username" class="form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("memberid"))%>"
                                                   disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_TerminalId%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" name="username" class="form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("terminalid"))%>"
                                                   disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_Charge_Name%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" name="username" class="form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("chargename"))%>"
                                                   disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_Dynamic_Input%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" name="username" class="form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("isinput_required"))%>"
                                                   disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_Start_Date%></label>

                                        <div class="col-md-4">
                                            <%
                                                if ("false".equals((String) hash.get("version")))
                                                {%>
                                            <input type="text" size="16" name="startdate"
                                                   class="datepicker form-control"
                                                   value="<%=commonFunctionUtil.convertTimestampToDatepicker((String) hash.get("effectiveStartDate"))%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <input type="text" size="16" name="startdate"
                                                   class="datepicker form-control"
                                                   value="<%=commonFunctionUtil.convertTimestampToDatepicker(getPreviousDate((String) hash.get("lastupdateDate")))%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            <%
                                                }
                                            %>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_End_Date%></label>

                                        <div class="col-md-4">
                                            <%
                                                if ("false".equals((String) hash.get("version")))
                                                {%>
                                            <input type="text" size="16" name="enddate" class="datepicker form-control"
                                                   value="<%=commonFunctionUtil.convertTimestampToDatepicker((String) hash.get("lastupdateDate"))%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <input type="text" size="16" class="datepicker form-control" name="enddate"
                                                   value="" readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            <%
                                                }
                                            %>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_Merchant_Rate%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" class="form-control" name="memberchargeval"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("chargevalue"))%>">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_agent%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" class="form-control" name="agentchargeval"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("agentchargevalue"))%>">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_partner%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" class="form-control" name="partnerchargeval"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("partnerchargevalue"))%>">
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-2"></div>
                                        <label class="col-md-4 control-label"><%=actionPartnerAccountCharges_Sequence_Number%></label>

                                        <div class="col-md-4">
                                            <input type="text" size="30" name="username" class="form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("sequencenum"))%>"
                                                   disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-3"></div>
                                        <label class="col-md-3 control-label" style="visibility: hidden;"><%=actionPartnerAccountCharges_Button%></label>

                                        <div class="col-md-4">
                                            <input type="hidden" value="1" name="step">
                                            <button type="submit" class="btn btn-default" id="submit" name="action"
                                                    value="update" style="display: -webkit-box;"><i
                                                    class="fa fa-sign-in"></i>&nbsp;&nbsp;<%=actionPartnerAccountCharges_Update%>
                                            </button>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>
                                    <input type="hidden" size="30" name="pstartdate"
                                           value="<%=(String) hash.get("effectiveStartDate")%>">
                                    <input type="hidden" size="30" name="penddate"
                                           value="<%=(String) hash.get("lastupdateDate")%>">
                                    <input type="hidden" size="30" name="version"
                                           value="<%=(String) hash.get("version")%>">
                                </form>
                            </div>
                            <%
                                        }
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
        </div>
    </div>
</div>
</body>
<%!
    public String getPreviousDate(String source)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = null;
        try
        {
            date2 = sdf.parse(source);
        }
        catch (ParseException e)
        {
            //logger.error("Parse Exception while  getting  PreviousDate",e);
        }
        Date dateBefore = new Date(date2.getTime() + 1 * 24 * 3600 * 1000);
        String sDate2 = sdf.format(dateBefore);
        return sDate2;
    }
%>


