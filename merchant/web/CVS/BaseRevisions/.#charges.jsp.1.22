<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="java.util.List" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 11/19/14
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Charges Summary");
    response.setHeader("X-Frame-Options", "ALLOWALL");
    session.setAttribute("X-Frame-Options", "ALLOWALL");

%>
<html>
<head>
    <title><%=company%> Merchant Account Details > Charges Summary</title>
    <title><%=company%> Settings > Merchant Config Details</title>

    <%--    <style type="text/css">

            #main{background-color: #ffffff}

            :target:before {
                content: "";
                display: block;
                height: 50px;
                margin: -50px 0 0;
            }

            .table > thead > tr > th {font-weight: inherit;}

            :target:before {
                content: "";
                display: block;
                height: 90px;
                margin: -50px 0 0;
            }

            footer{border-top:none;margin-top: 0;padding: 0;}

            /********************Table Responsive Start**************************/

            @media (max-width: 640px){

                table {border: 0;}

                /*table tr {
                    padding-top: 20px;
                    padding-bottom: 20px;
                    display: block;
                }*/

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

        </style>--%>
</head>
<%@ include file="Top.jsp" %>
<body class="pace-done widescreen fixed-left">

<%!
    private static Logger logger = new Logger("charges.jsp");
    private Functions function = new Functions();
    private TerminalManager terminalManager = new TerminalManager();
%>
<%
    try
    {
        String uId = "";
        String str = "";
        if(session.getAttribute("role").equals("submerchant"))
        {
            uId = (String) session.getAttribute("userid");
        }
        else
        {
            uId = (String) session.getAttribute("merchantid");
        }
        //List<TerminalVO> terminalVO = terminalManager.getTerminalsByMerchantId(session.getAttribute("merchantid").toString());
        String fromdate = null;
        String todate = null;
        String terminalid="";
        if(request.getAttribute("terminalid")!=null)
        {
            terminalid = request.getAttribute("terminalid").toString();
        }

        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);

        String charges_Charges_Summary1 = !function.isEmptyOrNull(rb1.getString("charges_Charges_Summary1"))?rb1.getString("charges_Charges_Summary1"): "Charges Summary";
        String charges_select = !function.isEmptyOrNull(rb1.getString("charges_select"))?rb1.getString("charges_select"): "Select Terminal ID";
        String charges_no_terminals = !function.isEmptyOrNull(rb1.getString("charges_no_terminals"))?rb1.getString("charges_no_terminals"): "No Terminals Allocated";
        String charges_terminalid1 = !function.isEmptyOrNull(rb1.getString("charges_terminalid1"))?rb1.getString("charges_terminalid1"): "Terminal ID*";
        String charges_Submit1 = !function.isEmptyOrNull(rb1.getString("charges_Submit1"))?rb1.getString("charges_Submit1"): "Submit";
        String charges_Charges_Data = !function.isEmptyOrNull(rb1.getString("charges_Charges_Data"))?rb1.getString("charges_Charges_Data"): "Charges Data";
        String charges_Terminal_ID1 = !function.isEmptyOrNull(rb1.getString("charges_Terminal_ID1"))?rb1.getString("charges_Terminal_ID1"): "Terminal ID :";
        String charges_Charges = !function.isEmptyOrNull(rb1.getString("charges_Charges"))?rb1.getString("charges_Charges"): "Charges";
        String charges_Frequency = !function.isEmptyOrNull(rb1.getString("charges_Frequency"))?rb1.getString("charges_Frequency"): "Frequency";
        String charges_Rate_Fee = !function.isEmptyOrNull(rb1.getString("charges_Rate_Fee"))?rb1.getString("charges_Rate_Fee"): "Rate/Fee";
        String charges_sorry = !function.isEmptyOrNull(rb1.getString("charges_sorry"))?rb1.getString("charges_sorry"): "Sorry";
        String charges_criteria = !function.isEmptyOrNull(rb1.getString("charges_criteria"))?rb1.getString("charges_criteria"): "No records found for given search criteria.";
        String charges_filter = !function.isEmptyOrNull(rb1.getString("charges_filter"))?rb1.getString("charges_filter"): "Filter";
        String charges_select1 = !function.isEmptyOrNull(rb1.getString("charges_select1"))?rb1.getString("charges_select1"): "Please select the Terminal Id for Charge Summary.";
        String charges_showing = !function.isEmptyOrNull(rb1.getString("charges_showing"))?rb1.getString("charges_showing"): "Showing Page";
        String charges_OF = !function.isEmptyOrNull(rb1.getString("charges_OF"))?rb1.getString("charges_OF"): "of";
        String charges_records = !function.isEmptyOrNull(rb1.getString("charges_records"))?rb1.getString("charges_records"): "records";
        String charges_page_no = !function.isEmptyOrNull(rb1.getString("charges_page_no"))?rb1.getString("charges_page_no"): "Page number";
        String charges_total_no_of_records = !function.isEmptyOrNull(rb1.getString("charges_total_no_of_records"))?rb1.getString("charges_total_no_of_records"): "Total number of records";
        String terminal_id = !function.isEmptyOrNull(rb1.getString("Invalid_terminalid"))?rb1.getString("Invalid_terminalid"): "Invalid terminalid.";

%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=charges_Charges_Summary1%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <form  name="form" method="post" action="/merchant/servlet/ChargesList?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" class="form-horizontal">

                            <%--<div class="form foreground bodypanelfont_color panelbody_color">
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp;Charges Summary</h2>
                                <hr class="hrform">
                            </div>--%>
                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            <%
                                String terminalCurrency ="";
                                StringBuffer terminalBuffer = new StringBuffer();
                                if (request.getParameter("MES") != null)
                                {
                                    String mes = request.getParameter("MES");
                                    if (mes.equals("ERR"))
                                    {
                                        if(request.getAttribute("error")!=null)
                                        {
                                            ValidationErrorList error = (ValidationErrorList) request.getAttribute("error");
                                            for (Object errorList : error.errors())
                                            {
                                                ValidationException ve = (ValidationException) errorList;
                                                //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + ve.getMessage() + "</b></li></center>");
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + terminal_id + "</h5>");
                                            }
                                        }
                                        else if(request.getAttribute("catchError")!=null)
                                        {
                                            //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + request.getAttribute("catchError") + "</b></li></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                        }
                                    }
                                }
                            %>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-md-8">
                                        <label class="col-sm-3 control-label"><%=charges_terminalid1%></label>
                                        <div class="col-sm-4">
                                            <select size="1" name="terminalid" class="form-control">
                                                <%
                                                    terminalBuffer.append("(");
                                                    TerminalManager terminalManager=new TerminalManager();
                                                    List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                    if(terminalVOList.size()>0)
                                                    {
                                                %>
                                                <option value=""><%=charges_select%></option>
                                                <%
                                                    for (TerminalVO terminalVO:terminalVOList)
                                                    {
                                                        String str1 = "";
                                                        String active = "";
                                                        if (terminalVO.getIsActive().equalsIgnoreCase("Y"))
                                                        {
                                                            active = "Active";
                                                        }
                                                        else
                                                        {
                                                            active = "InActive";
                                                        }
                                                        if(terminalid.equals(terminalVO.getTerminalId()))
                                                        {
                                                            terminalCurrency = terminalVO.getCurrency();
                                                            str1= "selected";
                                                        }
                                                        if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                                                        {
                                                            terminalBuffer.append(",");
                                                        }
                                                        terminalBuffer.append(terminalVO.getTerminalId());
                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()+"-"+active)%> </option>
                                                <%
                                                    }
                                                    terminalBuffer.append(")");
                                                }
                                                else
                                                {
                                                %>
                                                <option value="NoTerminals"><%=charges_no_terminals%></option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>

                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>

                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=charges_Submit1%></button>
                                        </div>
                                    </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="widget">
                <div class="widget-header">
                    <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=charges_Charges_Data%></strong></h2>
                    <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                    </div>
                </div>
                <div class="widget-content padding">
                    <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                    <input type="hidden" name="currency" value=<%=terminalCurrency%>>
                    <input type="hidden" name="terminalid" value=<%=terminalid%>>

                    <%
                        List<ChargeVO> chargeVOList = (List<ChargeVO>) request.getAttribute("chargeVOList");
                        if(chargeVOList!=null )
                        {
                            if(chargeVOList.size()>0)
                            {
                                PaginationVO paginationVO = (PaginationVO) request.getAttribute("PaginationVO");
                                TerminalVO selectedTerminalVO= (TerminalVO) request.getAttribute("terminalVO");

                                paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

                    %>
                    <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                        <thead>
                        <tr>
                            <td colspan="3" align="center" style="color: #ffffff;"><%=charges_Terminal_ID1%> &nbsp;<b></b> &nbsp;<%=selectedTerminalVO.getTerminalId()%></td>
                        </tr>
                        <tr style="color: white;">
                            <th style="text-align: center"><%=charges_Charges%></th>
                            <th style="text-align: center"><%=charges_Frequency%></th>
                            <th style="text-align: center"><%=charges_Rate_Fee%></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            for(ChargeVO chargeVO : chargeVOList)
                            {
                        %>
                        <tr>
                            <td data-label="Charges" style="text-align: center"><%=chargeVO.getChargename()%></td>
                            <td data-label="Frequency" style="text-align: center"><%=chargeVO.getFrequency()%></td>
                            <td data-label="Rate/Fee" style="text-align: center"><%=chargeVO.getChargevalue()%></td>
                        </tr>

                        <%
                            }
                        %>
                        </tbody>
                        <div>
                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                                <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                                <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                                <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                                <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                                <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>
                        </div>
                        <%
                            int TotalPageNo;
                            if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
                            {
                                TotalPageNo =paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
                            }
                            else
                            {
                                TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
                            }
                        %>
                        <div id="showingid"><strong><%=charges_page_no%> <%=paginationVO.getPageNo()%> <%=charges_OF%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                        <div id="showingid"><strong><%=charges_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>



                        <%
                                    }
                                    else
                                    {
                                        out.println(Functions.NewShowConfirmation1(charges_sorry, charges_criteria));
                                    }
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(charges_filter, charges_select1));
                                }
                            }
                            catch(PZDBViolationException dbe)
                            {
                                logger.error("Db exception::",dbe);
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<div class=\"table-responsive datatable\">");
                                out.println(Functions.NewShowConfirmation1("Sorry", "Kindly check Charge Summary after some time."));
                                out.println("</div>");
                                PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), PZOperations.CHARGE_LIST);
                            }
                            catch(Exception e)
                            {
                                logger.error("Generic exception in transaction summary",e);
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<br>");
                                out.println("<div class=\"table-responsive datatable\">");
                                out.println(Functions.NewShowConfirmation1("Sorry", "Kindly check Charge Summary after some time."));
                                out.println("</div>");
                                PZExceptionHandler.raiseAndHandleGenericViolationException("charges.jsp","doService()",null,"Merchant","Generic exception while getting Charges Summary",null,e.getMessage(),e.getCause(),session.getAttribute("merchantid").toString(),PZOperations.CHARGE_LIST);
                            }
                        %>
                    </table>
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