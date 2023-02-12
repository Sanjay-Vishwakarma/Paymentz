<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.memeberConfigVOS.MerchantConfigCombinationVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.memeberConfigVOS.MemberAccountMappingVO" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.enums.Charge_unit" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Oct 11, 2006
  Time: 4:31:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%!
    private static Logger logger = new Logger("viewmerchantdetails.jsp");
%>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> | Merchant Details</title>

    <link href="/agent/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/agent/datepicker/datepicker/bootstrap-datepicker.js"></script>
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

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

</head>

<body class="pace-done widescreen fixed-left-void">
<%--<form name="form" method="post" action="Transactions?ctoken=<%=ctoken%>">--%>

<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions = new Functions();

    try
    {
        Hashtable memberidDetails=agent.getAgentMemberDetailList((String) session.getAttribute("merchantid"));
        String memberid=(request.getParameter("memberid"));
        TerminalVO terminalVO = (TerminalVO)request.getAttribute("terminalvo");
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        String currency = "";

        try
        {
            fdate = ESAPI.validator().getValidInput("fdate",(String)request.getAttribute("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",(String)request.getAttribute("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",(String)request.getAttribute("fmonth"),"Months",2,true);
            tmonth = ESAPI.validator().getValidInput("tmonth",(String)request.getAttribute("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",(String)request.getAttribute("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",(String)request.getAttribute("tyear"),"Years",4,true);
        }
        catch(ValidationException e)
        {
            logger.error("ValidationException::",e);
            out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
            out.println(Functions.NewShowConfirmation1("Sorry","Internal error while accessing data."));
            out.println("</div>");
        }
        Calendar rightNow = Calendar.getInstance();
        String currentyear= ""+rightNow.get(rightNow.YEAR);

        rightNow.setTime(new Date());
        if(fdate==null)fdate=""+1;
        if(tdate==null)tdate=""+rightNow.get(rightNow.DATE);
        if(fmonth==null)fmonth=""+rightNow.get(rightNow.MONTH);
        if(tmonth==null)tmonth=""+rightNow.get(rightNow.MONTH);
        if(fyear==null)fyear=""+rightNow.get(rightNow.YEAR);
        if(tyear==null)tyear=""+rightNow.get(rightNow.YEAR);
%>


<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/agent/net/MerchantDetails?ctoken=<%=ctoken%>" method="POST">
                        <%
                            Enumeration<String> requestName=request.getParameterNames();
                            while(requestName.hasMoreElements())
                            {
                                String name=requestName.nextElement();
                                if("invoiceno".equals(name))
                                {
                                    String[] values=request.getParameterValues(name);

                                    out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                                }
                                else
                                    out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                            }
                        %>
                        <button class="btn-xs" type="submit" name="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/agent/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br><br><br>

            <%
                if(request.getAttribute("merchantConfigCombinationVO")!=null)
                {
                    MerchantConfigCombinationVO merchantConfigCombinationVO= (MerchantConfigCombinationVO) request.getAttribute("merchantConfigCombinationVO");
                    MerchantDetailsVO merchantDetailsVO = merchantConfigCombinationVO .getMerchantDetailsVO();
                    MemberAccountMappingVO memberAccountMappingVO= merchantConfigCombinationVO.getMemberAccountMappingVO();
                    List<ChargeVO> chargeVOList = merchantConfigCombinationVO.getChargeVOs();
            %>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Profile</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding"  style="overflow-y: auto;">
                            <%--<div class="table-responsive">--%>
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >

                                <%--<div class="form foreground bodypanelfont_color panelbody_color">
                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Action History</h2>
                                    <hr class="hrform" style="width: 95%;margin-left: 10px">
                                </div>--%>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th style="text-align: center">Merchant ID</th>
                                    <th style="text-align: center">Terminal ID</th>
                                    <th style="text-align: center">Company Name</th>
                                    <th style="text-align: center">Person Name</th>
                                    <th style="text-align: center">Email Address</th>
                                    <th style="text-align: center">Site URL</th>
                                    <th style="text-align: center">Phone Number</th>
                                    <th style="text-align: center">Country</th>
                                    <th style="text-align: center">Currency</th>

                                </tr>
                                </thead>
                                <tbody>

                                <tr>
                                    <td data-label="Merchant ID" style="text-align: center"><%=merchantDetailsVO.getMemberId()%></td>
                                    <td data-label="Terminal ID" style="text-align: center"><%=memberAccountMappingVO.getTerminalid()%></td>
                                    <td data-label="Company Name" style="text-align: center"><%=merchantDetailsVO.getCompany_name()%></td>
                                    <td data-label="Person Name" style="text-align: center"><%=merchantDetailsVO.getContact_persons()%></td>
                                    <td data-label="Email Address" style="text-align: center"><%=functions.getEmailMasking(merchantDetailsVO.getContact_emails())%></td>
                                    <td data-label="Site URL" style="text-align: center"><%=merchantDetailsVO.getSiteName()%></td>
                                    <td data-label="Phone Number" style="text-align: center"><%=functions.getPhoneNumMasking(merchantDetailsVO.getTelNo())%></td>
                                    <td data-label="Country" style="text-align: center"><%=merchantDetailsVO.getCountry()%></td>
                                    <td data-label="Currency" style="text-align: center"><%=terminalVO.getCurrency()%></td>
                                </tr>

                                </tbody>
                            </table>
                            <%--</div>--%>
                        </div>
                    </div>
                </div>
            </div>


            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Charges</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding" style="overflow-y: auto;">
                            <%--<div class="table-responsive">--%>
                            <%
                                if(chargeVOList.size()>0)
                                {
                                    out.println("<table id=\"myTable\" class=\"table table-striped table-bordered table-hover table-green dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
                                    for(ChargeVO chargeVO : chargeVOList)
                                    {
                            %>
                            <%--<table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                <thead>

                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th style="text-align: center">Tracking Id </th>
                                    <th style="text-align: center">Auth Code </th>
                                    <th style="text-align: center">Transaction Amount </th>
                                    <th style="text-align: center">Date of Transaction</th>
                                    <th style="text-align: center">Transaction Status</th>
                                    <th style="text-align: center">Status Detail</th>
                                </tr>
                                </thead>--%>
                            <tbody>
                            <tr>
                                <td data-label="" style="text-align: center"><%=chargeVO.getChargename()%></td>
                                <td data-label="" style="text-align: center"><%=(Charge_unit.Percentage.name().equals(chargeVO.getValuetype()))?chargeVO.getChargevalue()+"%":chargeVO.getChargevalue()%></td>
                            </tr>
                            </tbody>

                            <%
                                    }
                                    out.println("</table>");
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry", "No Charges Found"));
                                }
                            %>
                            <%--</table>--%>
                            <%--</div>--%>
                        </div>

                    </div>
                </div>
            </div>


            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Limits</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <%--<div class="form foreground bodypanelfont_color panelbody_color">
                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Customer's Details</h2>
                                    <hr class="hrform" style="width: 95%;margin-left: 10px">
                                </div>--%>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th style="text-align: center">Daily Amount Limit</th>
                                    <th style="text-align: center">Monthly Amount Limit</th>
                                    <th style="text-align: center">High Risk Amount Limit</th>
                                    <th style="text-align: center">Daily Card Limit</th>
                                    <th style="text-align: center">Weekly Card Limit</th>
                                    <th style="text-align: center">Monthly Card Limit</th>
                                    <th style="text-align: center">Daily Card Amount Limit</th>
                                    <th style="text-align: center">Weekly Card Amount Limit</th>
                                    <th style="text-align: center">Monthly Card Amount Limit</th>
                                    <th style="text-align: center">Daily Refund Amount Limit</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td data-label="Daily Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getDaily_amount_limit()%></td>
                                    <td data-label="Monthly Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getMonthly_amount_limit()%></td>
                                    <td data-label="High Risk Amount Limit" style="text-align: center"><%=merchantDetailsVO.getAptPrompt()%></td>
                                    <td data-label="Daily Card Limit" style="text-align: center"><%=memberAccountMappingVO.getDaily_card_limit()%></td>
                                    <td data-label="Weekly Card Limit" style="text-align: center"><%=memberAccountMappingVO.getWeekly_card_limit()%></td>
                                    <td data-label="Monthly Card Limit" style="text-align: center"><%=memberAccountMappingVO.getMonthly_card_limit()%></td>
                                    <td data-label="Daily Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getDaily_card_amount_limit()%></td>
                                    <td data-label="Weekly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getWeekly_card_amount_limit()%></td>
                                    <td data-label="Monthly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getMonthly_card_amount_limit()%></td>
                                    <td data-label="Daily Refund Amount Limit" style="text-align: center"><%=merchantDetailsVO.getRefunddailylimit()%></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>


            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Config</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <%--<div class="form foreground bodypanelfont_color panelbody_color">
                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Customer's Details</h2>
                                    <hr class="hrform" style="width: 95%;margin-left: 10px">
                                </div>--%>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th style="text-align: center">Pharma Processing</th>
                                    <th style="text-align: center">Merchant Activation</th>
                                    <th style="text-align: center">Direct Capture Mode</th>
                                    <th style="text-align: center">Exclusive whitelisted Card Processing</th>
                                    <th style="text-align: center">Exclusive whitelisted IP Address</th>
                                    <th style="text-align: center">Refund Allowed</th>
                                    <th style="text-align: center">Terminal Activation</th>
                                    <th style="text-align: center">Test Terminal</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td data-label="Pharma Processing" style="text-align: center"><%=merchantDetailsVO.getPharma()%></td>
                                    <td data-label="Merchant Activation" style="text-align: center"><%=merchantDetailsVO.getActivation()%></td>
                                    <td data-label="Direct Capture Mode" style="text-align: center"><%=merchantDetailsVO.getService()%></td>
                                    <td data-label="Exclusive whitelisted Card Processing" style="text-align: center"><%=merchantDetailsVO.getIswhitelisted()%></td>
                                    <td data-label="Exclusive whitelisted IP Address" style="text-align: center"><%=merchantDetailsVO.getIpWhiteListed()%></td>
                                    <td data-label="Refund Allowed" style="text-align: center"><%=merchantDetailsVO.getIsrefund()%></td>
                                    <td data-label="Terminal Activation" style="text-align: center"><%=memberAccountMappingVO.getActive()%></td>
                                    <td data-label="Test Terminal" style="text-align: center"><%=memberAccountMappingVO.getTest()%></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Sorry","No Records Found"));
                }
            %>


        </div>
    </div>
</div>

<%
    }
    catch(Exception e)
    {
        logger.error("Exception in Merchant Config Details ",e);
        out.println("<br>");
        out.println("<br>");
        out.println("<br>");
        out.println("<div class=\"reporttable\" style=\"margin-top:100px\">");
        out.println(Functions.NewShowConfirmation1("Sorry","Internal error while accessing data."));
        out.println("</div>");
        PZExceptionHandler.raiseAndHandleGenericViolationException("accountSummary.jsp","doService()",null,"Agent","Exception while getting Member Config details",null,e.getMessage(),e.getCause(),session.getAttribute("merchantid").toString(),PZOperations.MEMBER_CONFIG);
    }
%>
</body>
</html>