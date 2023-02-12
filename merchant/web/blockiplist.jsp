<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="blocktab.jsp" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
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

            /* tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}*/
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
            background: transparent;
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
            padding-top: 6px !important;
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
    </style>
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
    <%--<script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>--%>
    <script>

        $(document).ready(function(){

            var w = $(window).width();

            //alert(w);

            if(w > 990){
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else{
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>
</head>
<body>
    <%

  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Functions functions=new Functions();

    String selectIpVersion=request.getParameter("selectIpVersion")==null?"":request.getParameter("selectIpVersion");
    String AllIp=request.getParameter("IPAddress")==null?"":request.getParameter("IPAddress");

     ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String blockiplist_blocked_ip = rb1.getString("blockiplist_blocked_ip");
    String blockiplist_merchantid1 = rb1.getString("blockiplist_merchantid1");
    String blockiplist_select_ip_type = rb1.getString("blockiplist_select_ip_type");
    String blockiplist_select_ip_version = rb1.getString("blockiplist_select_ip_version");
    String blockiplist_IPv4 = rb1.getString("blockiplist_IPv4");
    String blockiplist_IPv6 = rb1.getString("blockiplist_IPv6");
    String blockiplist_ip_address = rb1.getString("blockiplist_ip_address");
    String blockiplist_search = rb1.getString("blockiplist_search");
    String blockiplist_blocked_ip_list = rb1.getString("blockiplist_blocked_ip_list");
    String blockiplist_sr_no = rb1.getString("blockiplist_sr_no");
    String blockiplist_ip = rb1.getString("blockiplist_ip");
    String blockiplist_memberid = rb1.getString("blockiplist_memberid");
    String blockiplist_selected_ip = rb1.getString("blockiplist_selected_ip");
    String blockiplist_sorry = rb1.getString("blockiplist_sorry");
    String blockiplist_no_records = rb1.getString("blockiplist_no_records");
    String blockiplist_page_no=rb1.getString("blockiplist_page_no");
    String blockiplist_total_no_of_records=rb1.getString("blockiplist_total_no_of_records");
%>
<body>
<div class="content-page">
    <div class="content" style="padding:0px 20px ;margin: 0px;">
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=blockiplist_blocked_ip%></strong>
                            </h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%

                            String errormsg1 = (String) request.getAttribute("error");
                            String msg = (String) request.getAttribute("msg");
                            if (functions.isValueNull(errormsg1))
                            {
                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                            }
                            if (functions.isValueNull(msg))
                            {
                                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                            }

                        %>
                        <div class="widget-content">
                            <div id="horizontal-form">
                                <form action="/merchant/servlet/BlockIp?ctoken=<%=ctoken%>" method="post"
                                      name="forms">
                                    <%
                                        int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                                        int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                                    %>
                                    <div align="center">
                                        <input type="hidden" value="<%=ctoken%>" id="ctoken">
                                        <input type="hidden" value="<%=(String) session.getAttribute("merchantid")%>"
                                               id="merchantid">

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label><%=blockiplist_merchantid1%></label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" name="merchantid" class="form-control"
                                                           value="<%=(String) session.getAttribute("merchantid")%>"
                                                           disabled>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3"
                                                     style="margin-top:10px;">
                                                    <label><%=blockiplist_select_ip_type%></label>
                                                </div>
                                                <div class="col-md-3">
                                                    <select id="selectIpVersion"
                                                            name="selectIpVersion" class="form-control"
                                                    <%--style="width:342px; height:30px "--%>>
                                                        <%
                                                            if ("".equals(selectIpVersion))
                                                            {
                                                        %>
                                                        <option value="" selected><%=blockiplist_select_ip_version%></option>
                                                        <option value="IPv4"><%=blockiplist_IPv4%></option>
                                                        <option value="IPv6"><%=blockiplist_IPv6%></option>
                                                        <%
                                                        }
                                                        else if ("IPv4".equals(selectIpVersion))
                                                        {
                                                        %>
                                                        <option value=""><%=blockiplist_select_ip_version%></option>
                                                        <option value="IPv4" selected><%=blockiplist_IPv4%></option>
                                                        <option value="IPv6"><%=blockiplist_IPv6%></option>

                                                        <%
                                                        }
                                                        else if ("IPv6".equals(selectIpVersion))
                                                        {
                                                        %>
                                                        <option value=""><%=blockiplist_select_ip_version%></option>
                                                        <option value="IPv4"><%=blockiplist_IPv4%></option>
                                                        <option value="IPv6" selected><%=blockiplist_IPv6%></option>
                                                        <%
                                                            }
                                                        %>
                                                        %>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>

                                        <div
                                                class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3"
                                                     style="margin-top:10px;">
                                                    <label><%=blockiplist_ip_address%></label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type=text name="IPAddress" class="form-control"
                                                           id="IPAddress"
                                                           maxlength="39"
                                                           size="10" class="txtbox"
                                                    <%--style="width:342px ;height:30px "--%>
                                                           value="<%=AllIp%>">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group col-md-12">
                                            <div class="col-sm-offset-4 col-md-4" style="margin-top:10px; ">
                                                <button type="submit" class="btn btn-default"><i
                                                        class="fa fa-search"></i>
                                                    &nbsp;&nbsp;<%=blockiplist_search%>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="content-page">
    <div class="content" style="padding:0px 20px ;margin: 0px;">
        <div class="page-heading">
            <div class="row">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=blockiplist_blocked_ip_list%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content" style="overflow-y: auto;">

                            <div class="widget-content padding">

                                <%
                                    String currentblock = request.getParameter("currentblock");
                                    int records = 0;
                                    int totalrecords = 0;
                                    if (currentblock == null)
                                        currentblock = "1";
                                    try
                                    {
                                        records = (int) request.getAttribute("records");
                                        totalrecords = (int) request.getAttribute("totalrecords");
                                    }
                                    catch (Exception ex)
                                    {
                                    }

                                    if (request.getAttribute("listOfIp") != null)
                                    {
                                        List<BlacklistVO> iList = (List<BlacklistVO>) request.getAttribute("listOfIp");
                                        PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                                        paginationVO.setInputs(paginationVO.getInputs() + "&ctoken=" + ctoken);
                                        if (iList.size() > 0)
                                        {
                                %>
                                <br>
                                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr style="color: white;">
                                        <th valign="middle" align="center" style="text-align: center"><%=blockiplist_sr_no%></th>
                                        <th valign="middle" align="center" style="text-align: center"><%=blockiplist_ip%></th>
                                        <th valign="middle" align="center" style="text-align: center"><%=blockiplist_memberid%></th>
                                        <th valign="middle" align="center" style="text-align: center"><%=blockiplist_selected_ip%>
                                        </th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    <%
                                        String style = "class=td1";
                                        String ext = "light";
                                        int pos = 1;
                                        if (pos % 2 == 0)
                                        {
                                            style = "class=tr0";
                                            ext = "dark";
                                        }
                                        else
                                        {
                                            style = "class=tr1";
                                            ext = "light";
                                        }

                                        pos = (pagerecords * (pageno - 1)) + 1;
                                        for (BlacklistVO blacklistVO : iList)
                                        {
                                            out.println("<tr>");
                                            out.println("<td data-label='Sr no' align=center " + style + ">" + pos + "</td>");

                                            out.println("<td data-label='IP' align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getStartIpv4()) + "</td>");
                                            if (functions.isValueNull(blacklistVO.getMemberId()))
                                            {
                                                out.println("<td data-label='Member ID' align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getMemberId()) + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td data-label='Member ID' align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML("-") + "</td>");
                                            }
                                            out.println("<td data-label='Selected IP Versions''; align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getselectIpVersion()) + "</td>");
                                            out.println("</tr>");
                                            pos++;

                                        }

                                    %>
                                    </tbody>
                                </table>
                            </div>
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
                        <div id="showingid" style="text-align:left "><strong><%=blockiplist_page_no%> <%=paginationVO.getPageNo()%> of <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                        <div id="showingid" style="text-align:left "><strong><%=blockiplist_total_no_of_records%>   <%=paginationVO.getTotalRecords()%></strong></div>

                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                            <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                            <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                            <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                            <jsp:param name="page" value="BlockIp"/>
                            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>
                        <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(blockiplist_sorry, blockiplist_no_records));
                                }
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1(blockiplist_sorry, blockiplist_no_records));
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
