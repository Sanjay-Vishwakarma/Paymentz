<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ include file="functions.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="whitelistTab.jsp" %>

<%--
Created by IntelliJ IDEA.
User: sanjeet
Date: 11/28/2018
Time: 5:01 PM
To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <style type="text/css">
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
            table thead { display: none;}

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

        tr:nth-child(odd) {background: transparent;}
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
            padding-top: 6px !important;
            padding-bottom: 6px;
            padding-left: 10px;
            padding-right: 10px;
            vertical-align: top;
            border-bottom: none;
        }
        .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}
        /********************Table Responsive Ends**************************/
    </style>
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
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
    <script>
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function Delete()
        {
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    break;
                }
            }
            if(!flag)
            {
                alert("Select at least one record");
                return false;
            }
            if (confirm("Do you really want to delete all selected Data."))
            {
                document.ipwhitelist.submit();
            }
        }
    </script>

</head>
<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions=new Functions();
    String type=request.getParameter("type")==null?"":request.getParameter("type");
    String memberid=request.getParameter("memberid")==null?"":request.getParameter("memberid");
    String ipAddress = request.getParameter("ipAddress")==null?"":request.getParameter("ipAddress");
    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String ipwhitelist_ip = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_ip"))?rb1.getString("ipwhitelist_ip"): "WhiteList IP";
    String ipwhitelist_merchant = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_merchant"))?rb1.getString("ipwhitelist_merchant"): "Merchant ID*";
    String ipwhitelist_ip_type = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_ip_type"))?rb1.getString("ipwhitelist_ip_type"): "IP Type*";
    String ipwhitelist_ip_address = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_ip_address"))?rb1.getString("ipwhitelist_ip_address"): "IP Address*";
    String ipwhitelist_whitelist_ip = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_whitelist_ip"))?rb1.getString("ipwhitelist_whitelist_ip"): "WhiteList IP";
    String ipwhitelist_search = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_search"))?rb1.getString("ipwhitelist_search"): "Search";
    String ipwhitelist_sorry = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_sorry"))?rb1.getString("ipwhitelist_sorry"): "Sorry";
    String ipwhitelist_no_record = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_no_record"))?rb1.getString("ipwhitelist_no_record"): "No records found.";
    String ipwhitelist_Select_IP_Version = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_Select_IP_Version"))?rb1.getString("ipwhitelist_Select_IP_Version"): "Select IP Version";
    String ipwhitelist_Whitelist_IP_List = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_Whitelist_IP_List"))?rb1.getString("ipwhitelist_Whitelist_IP_List"): "Whitelist IP List";
    String ipwhitelist_MerchantID = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_MerchantID"))?rb1.getString("ipwhitelist_MerchantID"): "Merchant ID";
    String ipwhitelist_Ip_Address = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_Ip_Address"))?rb1.getString("ipwhitelist_Ip_Address"): "Ip Address";
    String ipwhitelist_IP_Type = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_IP_Type"))?rb1.getString("ipwhitelist_IP_Type"): "IP Type";
    String ipwhitelist_Delete = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_Delete"))?rb1.getString("ipwhitelist_Delete"): "Delete";
    String ipwhitelist_Showing_Page = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_Showing_Page"))?rb1.getString("ipwhitelist_Showing_Page"): "Showing Page ";
    String ipwhitelist_of = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_of"))?rb1.getString("ipwhitelist_of"): "of";
    String ipwhitelist_records = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_records"))?rb1.getString("ipwhitelist_records"): "records";
    String ipwhitelist_page_no = StringUtils.isNotEmpty(rb1.getString("ipwhitelist_page_no"))?rb1.getString("ipwhitelist_page_no"): "Page number";
    String ipwhitelistEmail_total_no_of_records=StringUtils.isNotEmpty(rb1.getString("ipwhitelistEmail_total_no_of_records"))?rb1.getString("ipwhitelistEmail_total_no_of_records"): "Total number of records";
%>
<div class="content-page">
    <div class="content" style="padding: 0px 20px;margin: 0px;">
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ipwhitelist_ip%></strong>
                            </h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content">
                            <div id="horizontal-form">
                                <form action="/merchant/servlet/IpWhiteList?ctoken=<%=ctoken%>" method="post" name="forms" >
                                    <input type="hidden" value="<%=ctoken%>" id="ctoken">
                                    <input type="hidden" value="<%=(String) session.getAttribute("merchantid")%>" id="merchantid">
                                    <%
                                        String errormsg1 = (String) request.getAttribute("error");
                                        String msg = (String) request.getAttribute("msg");
                                        if (functions.isValueNull(errormsg1))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                        }
                                        if(functions.isValueNull(msg))
                                        {
                                            out.println(Functions.NewShowConfirmation1("Sorry", msg));
                                        }
                                    %>
                                    <div class="form-group col-md-1">
                                    </div>

                                    <div class="form-group col-md-12">
                                        <div class="ui-widget">
                                            <div class="col-sm-offset-1 col-md-4" style="margin-top:10px; text-align: center">
                                                <label><%=ipwhitelist_merchant%></label>
                                            </div>
                                            <div class="col-md-3">
                                                <input type="text" name="merchantid" class="form-control"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12">
                                        <div class="ui-widget">
                                            <div class="col-sm-offset-1 col-md-4" style="margin-top:10px;text-align:center">
                                                <label><%=ipwhitelist_ip_type%></label>
                                            </div>
                                            <div class="col-md-3">
                                                <select class="form-control" name="type"><option value=""<%if(type.equals("")){%> selected <%}%> default><%=ipwhitelist_Select_IP_Version%></option><option value="IPv4" <%if(type.equals("IPv4")){%> selected <%}%>>IPv4</option><option value="IPv6" <%if(type.equals("IPv6")){%> selected <%}%>>IPv6</option></select>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12">
                                        <div class="ui-widget">
                                            <div class="col-sm-offset-1 col-md-4" style="margin-top:10px;text-align:center">
                                                <label><%=ipwhitelist_ip_address%></label>
                                            </div>
                                            <div class="col-md-3">
                                                <input type="text" name="ipAddress"  class="form-control" value="<%=ipAddress%>">
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12">
                                        <p style="text-align: center;">
                                            <button type="submit" name="upload" value="upload"
                                                    style="display: inline-block!important;"
                                                    class="btn btn-default center-block">
                                                <i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=ipwhitelist_whitelist_ip%>
                                            </button>
                                            <button type="submit" class="btn btn-default center-block" style="display: inline-block!important;"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=ipwhitelist_search%></button>
                                        </p>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <%
                        Hashtable hash = (Hashtable)request.getAttribute("recordHash");
                        PaginationVO paginationVO=(PaginationVO)request.getAttribute("paginationVO");
                        String str="";
                        str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                        Hashtable temphash=null;
                        int records=-1;
                        int totalrecords=0;
                        String currentblock=request.getParameter("currentblock");
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

                        if(hash!=null && hash.size()>0)
                        {
                            hash = (Hashtable)request.getAttribute("recordHash");
                        }
                        if(records >0)
                        {
                    %>
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=ipwhitelist_Whitelist_IP_List%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <form name="ipwhitelist" action="/merchant/servlet/IpWhiteList?ctoken=<%=ctoken%>" method="post">
                                <table id="myTable" class="display table table-striped table-bordered" width="100%"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr style="background-color: #7eccad; color: white;">
                                        <th valign="middle" align="center" style="text-align: center"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></th>
                                        <th valign="middle" align="center" style="text-align: center"><%=ipwhitelist_MerchantID%></th>
                                        <th valign="middle" align="center" style="text-align: center"><%=ipwhitelist_Ip_Address%></th>
                                        <th valign="middle" align="center" style="text-align: center"><%=ipwhitelist_IP_Type%></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        String style="class=td1";
                                        String ext="light";
                                        int pos = 1;
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
                                        for( pos=1;pos<=records;pos++)
                                        {
                                            String id=Integer.toString(pos);
                                            if(records>0)
                                            {
                                                temphash=(Hashtable)hash.get(id);
                                                out.println("<tr>");
                                                out.println("<td align=center "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("id"))+"\"></td>");
                                                out.println("<td data-label='Merchant ID' align=center "+style+">"+ESAPI.encoder().encodeForHTML((String) session.getAttribute("merchantid"))+"</td>");
                                                out.println("<td data-label='Ip Address' align=center "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("ipAddress"))+"</td>");
                                                out.println("<td data-label='IP Type' align=center "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("type"))+"</td>");
                                                out.println("</tr>");
                                            }
                                        }
                                    %>
                                    </tbody>
                                </table>
                                <table width="100%">
                                    <thead>
                                    <tr>
                                        <td width="15%" align="center">
                                            <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default center-block" value="<%=ipwhitelist_Delete%>" onclick="return Delete();">
                                        </td>
                                    </tr>
                                    </thead>
                                </table>
                            </form>
                        </div>
                    </div>
                    <%
                        int TotalPageNo;
                        if(totalrecords%pagerecords!=0)
                        {
                            TotalPageNo =totalrecords/pagerecords+1;
                        }
                        else
                        {
                            TotalPageNo=totalrecords/pagerecords;
                        }
                    %>
                    <div id="showingid"><strong><%=ipwhitelist_page_no%> <%=pageno%> <%=ipwhitelist_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                    <div id="showingid"><strong><%=ipwhitelistEmail_total_no_of_records%> <%=totalrecords%></strong></div>
                    <jsp:include page="page.jsp" flush="true">
                        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                        <jsp:param name="numrows" value="<%=pagerecords%>"/>
                        <jsp:param name="pageno" value="<%=pageno%>"/>
                        <jsp:param name="str" value="<%=paginationVO.getInputs()+str%>"/>
                        <jsp:param name="page" value="IpWhiteList"/>
                        <jsp:param name="currentblock" value="<%=currentblock%>"/>
                        <jsp:param name="orderby" value=""/>
                    </jsp:include>
                    <%
                        }
                        else
                        {
                            out.println(Functions.NewShowConfirmation1(ipwhitelist_sorry,ipwhitelist_no_record));
                        }

                    %>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
