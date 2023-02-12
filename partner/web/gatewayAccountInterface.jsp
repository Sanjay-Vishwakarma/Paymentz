<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 7/28/14
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
    PartnerFunctions partner1=new PartnerFunctions();
%>
<%!
    Logger logger=new Logger("gatewayAccountInterface.jsp");
%>
<html>
<head>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <title><%=company%>  Bank Accounts</title>
    <style type="text/css">

        /*        #main{background-color: #ffffff}

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

                *//********************Table Responsive Start**************************//*

                @media (max-width: 640px){

                    table {border: 0;}

                    *//*table tr {
                        padding-top: 20px;
                        padding-bottom: 20px;
                        display: block;
                    }*//*

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

                *//********************Table Responsive Ends**************************/

        @media(max-width: 767px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit!important;
            }
        }


    </style>
</head>
<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">

<body>
<%

    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    session.setAttribute("submit","gatewayAccountInterface");
    if (partner.isLoggedInPartner(session))
    {

        String str="";

        String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
        String merchantid = Functions.checkStringNull(request.getParameter("merchantid"))==null?"":request.getParameter("merchantid");
        String partnerId = (String) session.getAttribute("merchantid");

        if (accountid != null) str = str + "&accountid=" + accountid;
        if (merchantid != null) str = str + "&merchantid=" + merchantid;
        String roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String gatewayAccountInterface_Partner_Gateway_Allocation = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Partner_Gateway_Allocation"))?rb1.getString("gatewayAccountInterface_Partner_Gateway_Allocation"): "Partner Gateway Allocation";
        String gatewayAccountInterface_Partner_Account_Allocation = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Partner_Account_Allocation"))?rb1.getString("gatewayAccountInterface_Partner_Account_Allocation"): "Partner Account Allocation";
        String gatewayAccountInterface_Add_New_Bank_Account = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Add_New_Bank_Account"))?rb1.getString("gatewayAccountInterface_Add_New_Bank_Account"): "Add New Bank Account";
        String gatewayAccountInterface_Bank_Accounts = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Bank_Accounts"))?rb1.getString("gatewayAccountInterface_Bank_Accounts"): "Bank Accounts";
        String gatewayAccountInterface_Bank_Account_ID = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Bank_Account_ID"))?rb1.getString("gatewayAccountInterface_Bank_Account_ID"): "Bank Account ID";
        String gatewayAccountInterface_Partner_ID = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Partner_ID"))?rb1.getString("gatewayAccountInterface_Partner_ID"): "Partner ID";
        String gatewayAccountInterface_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Merchant_ID"))?rb1.getString("gatewayAccountInterface_Merchant_ID"): "Merchant ID";
        String gatewayAccountInterface_Search = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Search"))?rb1.getString("gatewayAccountInterface_Search"): "Search";
        String gatewayAccountInterface_Report_Table = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Report_Table"))?rb1.getString("gatewayAccountInterface_Report_Table"): "Report Table";
        String gatewayAccountInterface_Sr_No = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Sr_No"))?rb1.getString("gatewayAccountInterface_Sr_No"): "Sr No";
        String gatewayAccountInterface_Account_Id = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Account_Id"))?rb1.getString("gatewayAccountInterface_Account_Id"): "Account Id";
        String gatewayAccountInterface_Partner_Id = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Partner_Id"))?rb1.getString("gatewayAccountInterface_Partner_Id"): "Partner Id";
        String gatewayAccountInterface_Merchant_Id = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Merchant_Id"))?rb1.getString("gatewayAccountInterface_Merchant_Id"): "Merchant Id";
        String gatewayAccountInterface_Bank_Name = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Bank_Name"))?rb1.getString("gatewayAccountInterface_Bank_Name"): "Bank Name";
        String gatewayAccountInterface_Currency = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Currency"))?rb1.getString("gatewayAccountInterface_Currency"): "Currency";
        String gatewayAccountInterface_Alias_Name = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Alias_Name"))?rb1.getString("gatewayAccountInterface_Alias_Name"): "Alias Name";
        String gatewayAccountInterface_Action = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Action"))?rb1.getString("gatewayAccountInterface_Action"): "Action";
        String gatewayAccountInterface_Showing_Page = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Showing_Page"))?rb1.getString("gatewayAccountInterface_Showing_Page"): "Showing Page";
        String gatewayAccountInterface_of = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_of"))?rb1.getString("gatewayAccountInterface_of"): "of";
        String gatewayAccountInterface_records = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_records"))?rb1.getString("gatewayAccountInterface_records"): "records";
        String gatewayAccountInterface_Sorry = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_Sorry"))?rb1.getString("gatewayAccountInterface_Sorry"): "Sorry";
        String gatewayAccountInterface_No_records_found = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_No_records_found"))?rb1.getString("gatewayAccountInterface_No_records_found"): "No records found.";
        String gatewayAccountInterface_view = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_view"))?rb1.getString("gatewayAccountInterface_view"): "View";
        String gatewayAccountInterface_edit = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_edit"))?rb1.getString("gatewayAccountInterface_edit"): "Edit";
        String gatewayAccountInterface_page_no = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_page_no"))?rb1.getString("gatewayAccountInterface_page_no"): "Page number";
        String gatewayAccountInterface_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("gatewayAccountInterface_total_no_of_records"))?rb1.getString("gatewayAccountInterface_total_no_of_records"): "Total number of records";

        LinkedHashMap<String, String> gatewayTypes =new LinkedHashMap<String, String>();
        Connection conn=null;
        try
        {
            if (request.getParameter("name") != null && request.getParameter("name").length() > 0)
            {
                try
                {
                    conn = Database.getConnection();
                    ResultSet rs = Database.executeQuery("SELECT gtype.name as name FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid  and gtype.gateway IN(select gateway from gateway_type) ORDER BY gtype.name,gtype.currency ASC", conn);
                    while (rs.next())
                    {
                        gatewayTypes.put(rs.getString("name"), rs.getString("name"));
                    }
                }catch(Exception e){
                    logger.error("Exception---" + e);
                }
                finally
                {
                    Database.closeConnection(conn);
                }
            }
            else{
                try
                {
                    conn = Database.getConnection();
                    ResultSet rs = Database.executeQuery("SELECT gtype.name as name FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid  ORDER BY gtype.name,gtype.currency ASC", conn);
                    while (rs.next())
                    {
                        gatewayTypes.put(rs.getString("name"), rs.getString("name"));
                    }
                }catch(Exception e){
                    logger.error("Exception---" + e);
                }
                finally
                {
                    Database.closeConnection(conn);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("SQLException in gatewayAccountInterface...", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

%>
<%--<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

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
<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <%
                if(roles.contains("superpartner"))
                {
            %>
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnerGatewayMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;"><%=gatewayAccountInterface_Partner_Gateway_Allocation%>
                        </button>
                    </form>
                </div>
            </div>

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnerGatewayAccountMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;"><%=gatewayAccountInterface_Partner_Account_Allocation%>
                        </button>
                    </form>
                </div>
            </div>
            <%
                }
            %>

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/manageGatewayAccount.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;"><%=gatewayAccountInterface_Add_New_Bank_Account%>
                        </button>
                    </form>
                </div>
            </div>

            <%--<div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/manageGatewayAccount.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-xs" type="submit" value="gatewayAccountInterface" name="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 45px;width: 200px;" src="/partner/images/addnewbankaccount.png">
                        </button>
                    </form>
                </div>
            </div>
--%>
            <br>
            <br>
            <br>

            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=gatewayAccountInterface_Bank_Accounts%></strong></h2>
                            <div class="additional-btn">
                                <%--<form action="/partner/manageGatewayAccount.jsp?ctoken=<%=ctoken%>" method="POST">
                                    <button type="submit" class="btn btn-default" value="Add New Gateway Account" name="submit">
                                        <i class="fa fa-sign-in"></i>&nbsp;&nbsp;Add New Bank Account
                                    </button>
                                </form>--%>
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>

                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form  name="forms" method="post" action="/partner/net/listGatewayAccountDetails?ctoken=<%=ctoken%>" <%--class="form-horizontal"--%>>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                    <%

                                        String pid = null;
                                        String Config ="";
                                        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                        if(Roles.contains("superpartner")){
                                            pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
                                        }else{
                                            Config = "disabled";
                                            pid = String.valueOf(session.getAttribute("merchantid"));
                                        }
                                        String memberId=Functions.checkStringNull(request.getParameter("merchantid"))==null?"":request.getParameter("merchantid");
                                        String accontId=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
                                        String bankname= Functions.checkStringNull(request.getParameter("name"))==null?"":request.getParameter("name");

                                        if (bankname!=null)str=str + "&name=" + bankname;
                                        else
                                            bankname="";
                                        if(memberId!=null)str = str + "&merchantid=" + memberId;
                                        else
                                            memberId="";
                                        if(memberId!=null)str = str + "&accountid=" + memberId;
                                        else
                                            memberId="";
                                        if(pid!=null)str = str + "&pid=" + pid;
                                        else
                                            pid="";

                                        str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                        int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                                        int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

                                        str = str + "&SRecords=" + pagerecords;
                                        //str = str + "&bank=" + bank;
                                    %>
                                    <%
                                        String msg = (String) request.getAttribute("message");
                                        //String errormsg=(String)request.getAttribute("message");
                                        if(partner1.isValueNull(msg))
                                        {
                                            //out.println("<center><font class=\"textb\"><b>"+msg+"</b></font></center><br>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                                        }

                                        String successMsg = (String) request.getAttribute("success");
                                        //String errormsg=(String)request.getAttribute("message");
                                        if(partner1.isValueNull(successMsg))
                                        {
                                            //out.println("<center><font class=\"textb\"><b>"+msg+"</b></font></center><br>");
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + successMsg + "</h5>");
                                        }
                                    %>



                                    <%--<div class="form-group col-md-5">
                                        <label class="col-sm-4 control-label">Bank Account Id</label>
                                        <div class="col-sm-6">
                                            <input class="form-control" maxlength="22" type="text" name="accountid">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-5">
                                        <label class="col-sm-4 control-label">Merchant Id</label>
                                        <div class="col-sm-6">
                                            <input  class="form-control" maxlength="50" type="text" name="merchantid">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search</button>
                                        </div>
                                    </div>

                                    --%>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=gatewayAccountInterface_Bank_Account_ID%></label>
                                        <input class="form-control" maxlength="22" type="text" name="accountid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accontId)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=gatewayAccountInterface_Partner_ID%></label>
                                        <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                        <input type="hidden" name="pid" value="<%=pid%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=gatewayAccountInterface_Merchant_ID%></label>
                                        <input class="form-control" maxlength="22" type="text" name="merchantid"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=gatewayAccountInterface_Bank_Name%></label>
                                        <select size="1" name="name" class="form-control">
                                            <option value="">All</option>

                                            <%
                                                Set gatewayType        = gatewayTypes.keySet();
                                                Iterator it1        = gatewayType.iterator();
                                                String selected5    = "";
                                                String key5         = "";
                                                String value5       = "";
                                                while(it1.hasNext())
                                                {
                                                    key5    = (String)it1.next();
                                                    value5  = gatewayTypes.get(key5);
                                                    if (key5.equals(bankname))
                                                        selected5   = "selected";
                                                    else
                                                        selected5   = "";

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key5)%>"
                                                    <%=selected5%>><%=ESAPI.encoder().encodeForHTML(value5)%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="color: transparent;"><%=gatewayAccountInterface_Search%></label>
                                        <button type="submit" class="btn btn-default" style="display: inherit;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;<%=gatewayAccountInterface_Search%></button>
                                    </div>

                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>

                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=gatewayAccountInterface_Report_Table%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">

                            <%  StringBuffer requestParameter = new StringBuffer();
                                Enumeration<String> stringEnumeration = request.getParameterNames();
                                while(stringEnumeration.hasMoreElements())
                                {
                                    String name=stringEnumeration.nextElement();
                                    if("SPageno".equals(name) || "SRecords".equals(name))
                                    {

                                    }
                                    else
                                        requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                                }
                                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");


                               // Hashtable hash = (Hashtable)request.getAttribute("transdetails");
                                HashMap hash = (HashMap)request.getAttribute("transdetails");

                                HashMap temphash=null;
                                int records=0;
                                int totalrecords=0;

                                String errormsg=(String)request.getAttribute("message");
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
                                if(hash!=null)
                                {
                                    hash = (HashMap)request.getAttribute("transdetails");
                                }
                                if(records>0)
                                {
                            %>
                            <table class="table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <%--<td valign="middle" class="th0" align="center" >Sr no</td>--%>
                                    <%--<td valign="middle" align="center" class="th0">Account Id</td>--%>
                                    <%--<td valign="middle" align="center" class="th0">Merchant Id</td>--%>
                                    <%--<td valign="middle" align="center" class="th0">Bank Name</td>--%>
                                    <%--<td valign="middle" align="center" class="th0">Currency</td>--%>
                                    <%--<td valign="middle" align="center" class="th0">Alias Name</td>--%>
                                    <%--<td valign="middle" align="center" class="th0" colspan = "2">Action</td>--%>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Sr_No%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Account_Id%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Partner_Id%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Merchant_Id%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Bank_Name%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Currency%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Alias_Name%></b></td>
                                    <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=gatewayAccountInterface_Action%></b></td>
                                </tr>
                                </thead>

                                <%


                                    String style="class=td1";
                                    String ext="light";

                                    for(int pos=1;pos<=records;pos++)
                                    {
                                        //String id=Integer.toString(pos);

                                        String id = Integer.toString(pos);
                                        style = "class=\"tr" + (pos + 1) % 2 + "\"";
                                        int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

                                        /*if(pos%2==0)
                                        {
                                            style="class=tr1";
                                            ext="dark";
                                        }
                                        else
                                        {
                                            style="class=tr0";
                                            ext="light";
                                        }*/

                                        temphash=(HashMap)hash.get(id);
                                        out.println("<tr id=\"maindata\">");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Sr No\" align=\"center\">&nbsp;"+srno+ "</td>");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Account Id\" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"<input type=\"hidden\" name=\"accountid\" value=\""+temphash.get("accountid")+"\"></td>");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Partner Id\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerid"))+"<input type=\"hidden\" name=\"pidy\" value=\""+(String)temphash.get("partnerid")+"\"></td>");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Merchant Id\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("merchantid"))+"<input type=\"hidden\" name=\"merchantidy\" value=\""+temphash.get("merchantid")+"\"></td>");
                                        //out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("pgtypeid"))+"<input type=\"hidden\" name=\"pgtypeid\" value=\""+temphash.get("pgtypeid")+"\"></td>");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Bank Name\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("name"))+"<input type=\"hidden\" name=\"gateway\" value=\""+temphash.get("gateway")+"\"></td>");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Currency\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"<input type=\"hidden\" name=\"currency\" value=\""+temphash.get("currency")+"\"></td>");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Alias Name\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("aliasname"))+"<input type=\"hidden\" name=\"aliasname\" value=\""+temphash.get("aliasname")+"\"></td>");
                                        out.println("<td "+style+" valign=\"middle\" data-label=\"Action\" align=\"center\"><form action=\"/partner/net/ActionGatewayAccounts?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\"" + ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto btn btn-default\" value=\"View\"><input type=\"hidden\" name=\"action\" value=\"View\">");
                                        out.println(requestParameter.toString());
                                        out.print("</form></td>");
                                        out.println("<td " + style + " valign=\"middle\" data-label=\"Action\" align=\"center\"><form action=\"/partner/net/ActionGatewayAccounts?ctoken=" + ctoken + "\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\"" + ESAPI.encoder().encodeForHTML((String) temphash.get("accountid")) + "\"><input type=\"submit\" name=\"submit\" class=\"gotoauto btn btn-default\" value=\"Edit\"><input type=\"hidden\" name=\"action\" value=\"modify\">");
                                        out.println(requestParameter.toString());
                                        out.print("</form></td>");
            /*out.println("<td "+style+" align=\"center\"><form action=\"/partner/net/viewGatewayAccountDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"View\"><input type=\"hidden\" name=\"action\" value=\"View\"></form></td>");
            out.println("<td "+style+" align=\"center\"><form action=\"/partner/net/viewGatewayAccountDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"Edit\"><input type=\"hidden\" name=\"action\" value=\"modify\"></form></td>");*/

                                        //out.println("</form>");
                                        out.println("</tr>");
                                    }
                                %>

                                <%--<tr>
                                    <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;"><b>Member Details</b></td>
                                </tr>

                                <tr>
                                    <td colspan="3" valign="middle" align="left" >Member ID:</td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getMemberId()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" >Terminal ID:</td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getTerminalId()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" >Company Name:</td>
                                    <td colspan="2" valign="middle" align="left" ><%=merchantDetailsVO.getCompany_name()%></td>
                                </tr>
                                <tr>
                                    <td  colspan="3" valign="middle" align="left" >Contact Person:</td>
                                    <td colspan="2" valign="middle" align="left" ><%=merchantDetailsVO.getContact_persons()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" >Payment Mode:</td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getPaymentTypeName()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" >Card Type:</td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getCardType()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" >Currency:</td>
                                    <td colspan="2" valign="middle" align="left" ><%=Currency%></td>
                                </tr>

                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" >Statement Period:</td>
                                    <td colspan=""  valign="middle" align="left" >From</td>
                                    <td colspan="2" valign="middle" align="left" > <%=transactionSummaryVO.getMinOfAllStatusDate()%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" >To</td>
                                    <td colspan="2" valign="middle" align="left" ><%=transactionSummaryVO.getMaxOfAllStatusDate()%></td>
                                </tr>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" >Declined Period:</td>
                                    <td colspan=""  valign="middle" align="left" >From</td>
                                    <td colspan="2" valign="middle" align="left" > <%=function.isValueNull(transactionSummaryVO.getMinAuthFailedDate())?transactionSummaryVO.getMinAuthFailedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" >To</td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(transactionSummaryVO.getMaxAuthFailedDate())?transactionSummaryVO.getMaxAuthFailedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" >Reversal Period:</td>
                                    <td colspan=""  valign="middle" align="left" >From</td>
                                    <td colspan="2" valign="middle" align="left" > <%=function.isValueNull(transactionSummaryVO.getMinReversedDate())?transactionSummaryVO.getMinReversedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" >To</td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(transactionSummaryVO.getMaxReversedDate())?transactionSummaryVO.getMaxReversedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" >ChargeBack Period:</td>
                                    <td colspan=""  valign="middle" align="left" >From</td>
                                    <td colspan="2" valign="middle" align="left" > <%=function.isValueNull(transactionSummaryVO.getMinChargeBackDate())?transactionSummaryVO.getMinChargeBackDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" >To</td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(transactionSummaryVO.getMaxChargeBackDate())?transactionSummaryVO.getMaxChargeBackDate():"N.A"%></td>
                                </tr>
                                <%
                                    if(function.isValueNull(rollingReserveDateVO.getRollingReserveStartDate()) || function.isValueNull(rollingReserveDateVO.getRollingReserveEndDate()))
                                    {
                                %>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" >Rolling Reserve Release:</td>
                                    <td colspan=""  valign="middle" align="left" >Start Date :</td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(rollingReserveDateVO.getRollingReserveStartDate())?rollingReserveDateVO.getRollingReserveStartDate():transactionSummaryVO.getMinOfAllStatusDate()%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" >End Date :</td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(rollingReserveDateVO.getRollingReserveEndDate())?rollingReserveDateVO.getRollingReserveEndDate():transactionSummaryVO.getMaxOfAllStatusDate()%></td>
                                </tr>--%>



                            </table>

                            <% int TotalPageNo;
                                if(totalrecords%pagerecords!=0)
                                {
                                    TotalPageNo=totalrecords/pagerecords+1;
                                }
                                else
                                {
                                    TotalPageNo=totalrecords/pagerecords;
                                }
                            %>
                            <div id="showingid"><strong><%=gatewayAccountInterface_page_no%> <%=pageno%> <%=gatewayAccountInterface_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                            <div id="showingid"><strong><%=gatewayAccountInterface_total_no_of_records%>   <%=totalrecords%> </strong></div>

                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                <jsp:param name="pageno" value="<%=pageno%>"/>
                                <jsp:param name="str" value="<%=str%>"/>
                                <jsp:param name="page" value="listGatewayAccountDetails"/>
                                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>


                            <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(gatewayAccountInterface_Sorry, gatewayAccountInterface_No_records_found));
                                }
                            %>
                            <%
                                }
                                else
                                {
                                    response.sendRedirect("/partner/logout.jsp");
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
</html>