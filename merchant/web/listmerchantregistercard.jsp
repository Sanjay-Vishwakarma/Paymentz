   <%@ page errorPage="error.jsp"   import="com.directi.pg.Functions,org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
   <%@ page import="com.directi.pg.*" %>

<%@ include file="Top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Registration History");

    String fromdate = null;
    String todate = null;

    try
    {
        fromdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
        todate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
        /*fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
        tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);*/
    }
    catch(ValidationException e)
    {

    }

    Date date1 = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date1);
    date1.setDate(1);
    String fromDate = originalFormat.format(date1);

    fromdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    todate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");

    /*String fdate=null;
    String tdate=null;
    String fmonth=null;
    String tmonth=null;
    String fyear=null;
    String tyear=null;*/
    String description=null;
    String firstName=null;
    String lastName=null;
    String email=null;

    firstName=Functions.checkStringNull(request.getParameter("firstname"));
    lastName=Functions.checkStringNull(request.getParameter("lastname"));
    email=Functions.checkStringNull(request.getParameter("email"));
    description=Functions.checkStringNull(request.getParameter("description"));

    if(firstName==null){firstName="";}
    if(lastName==null){lastName="";}
    if(email==null){email="";}
    if(description==null){description="";}


    Calendar rightNow = Calendar.getInstance();
    String str = "";
    str= str + "&ctoken="+ctoken;
    if (fromdate == null) fromdate = "" + 1;
    if (todate == null) todate = "" + rightNow.get(rightNow.DATE);
    /*if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/

    String currentyear= ""+rightNow.get(rightNow.YEAR);

    if (fromdate != null) str = str + "&fdate=" + fromdate;
    if (todate != null) str = str + "&tdate=" + todate;
    /*if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;*/
    if (description != null) str = str + "&description=" + description;
    if(firstName!=null)str = str + "&firstname=" + firstName;
    if(lastName!=null)str = str + "&lastname=" + lastName;
    if(email!=null)str = str + "&email=" + email;
    int pageno =1;
    int pagerecords=30;
    try
    {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",request.getParameter("SPageno"),"Numbers",3,true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
    }
    catch(ValidationException e)
    {
        pageno = 1;
        pagerecords = 30;
    }
    str = str + "&SRecords=" + pagerecords;

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String listmerchantregistercard_Card_Registration = rb1.getString("listmerchantregistercard_Card_Registration");
    String listmerchantregistercard_From = rb1.getString("listmerchantregistercard_From");
    String listmerchantregistercard_To = rb1.getString("listmerchantregistercard_To");
    String listmerchantregistercard_First_Name1 = rb1.getString("listmerchantregistercard_First_Name1");
    String listmerchantregistercard_Last_Name1 = rb1.getString("listmerchantregistercard_Last_Name1");
    String listmerchantregistercard_Email_ID1 = rb1.getString("listmerchantregistercard_Email_ID1");
    String listmerchantregistercard_Search = rb1.getString("listmerchantregistercard_Search");
    String listmerchantregistercard_Token_Data = rb1.getString("listmerchantregistercard_Token_Data");
    String listmerchantregistercard_Sr_No = rb1.getString("listmerchantregistercard_Sr_No");
    String listmerchantregistercard_Registration_Time = rb1.getString("listmerchantregistercard_Registration_Time");
    String listmerchantregistercard_Token_ID = rb1.getString("listmerchantregistercard_Token_ID");
    String listmerchantregistercard_Card_Num = rb1.getString("listmerchantregistercard_Card_Num");
    String listmerchantregistercard_Token = rb1.getString("listmerchantregistercard_Token");
    String listmerchantregistercard_First_Name = rb1.getString("listmerchantregistercard_First_Name");
    String listmerchantregistercard_Last_Name = rb1.getString("listmerchantregistercard_Last_Name");
    String listmerchantregistercard_Email_ID = rb1.getString("listmerchantregistercard_Email_ID");
    String listmerchantregistercard_is_Active = rb1.getString("listmerchantregistercard_is_Active");
    String listmerchantregistercard_Generated_By = rb1.getString("listmerchantregistercard_Generated_By");
    String listmerchantregistercard_Showing_Page = rb1.getString("listmerchantregistercard_Showing_Page");
    String listmerchantregistercard_of = rb1.getString("listmerchantregistercard_of");
    String listmerchantregistercard_records = rb1.getString("listmerchantregistercard_records");
    String listmerchantregistercard_Sorry = rb1.getString("listmerchantregistercard_Sorry");
    String listmerchantregistercard_No = rb1.getString("listmerchantregistercard_No");
    String listmerchantregistercard_page_no=rb1.getString("listmerchantregistercard_page_no");
    String listmerchantregistercard_total_no_of_records=rb1.getString("listmerchantregistercard_total_no_of_records");
%>

<%

%>
<html lang="en">
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<%--<script>
        $(document).ready(function(){
            $('#myTable').dataTable();
        });
    </script>--%>
    <title><%=company%> Merchant Token Management > Registration History </title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>

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

                /*tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}*/

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

            /*tr:nth-child(odd) {background: #F9F9F9;}*/

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
<link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
<%--<script>
    $(document).ready(function(){
        $('#myTable').dataTable();
    });
</script>--%>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<body>

<%--<div class="container-fluid " >
    <div class="row rowadd">
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-size:18px ;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;&lt;%&ndash;<%=company%>&ndash;%&gt; Card Registration</h2>
                <hr class="hrform">
            </div>--%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <form name="form" method="post" action="/merchant/servlet/ListMerchantRegisterCard?ctoken=<%=ctoken%>">

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=listmerchantregistercard_Card_Registration%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>



                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <%
                                String error=(String) request.getAttribute("error");
                                if( error== null)
                                {
                                    error="";
                                }
                                else
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                }
                            %>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=listmerchantregistercard_From%></label>
                                        <input type="text" size="16" name="fdate" class="datepicker form-control" value="<%=fromdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=listmerchantregistercard_To%></label>
                                        <input type="text" size="16" name="tdate" class="datepicker form-control" value="<%=todate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <%--<div class="form-group col-md-4 has-feedback">
                                        <label>Description</label>
                                        <input type=text name="description" class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(description)%>">
                                    </div>--%>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=listmerchantregistercard_First_Name1%>&nbsp;</label>

                                        <input type=text name="firstname" class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstName)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=listmerchantregistercard_Last_Name1%>&nbsp;</label>
                                        <input type=text name="lastname" class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastName)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=listmerchantregistercard_Email_ID1%></label>
                                        <input type="text" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(email)%>" name="email">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">

                                    </div>

                                    <div class="form-group col-md-4">

                                    </div>

                                    <div class="form-group col-md-4">
                                        <button type="submit" class="btn btn-default" name="B1" >
                                            <span><i class="fa fa-clock-o"></i></span>
                                            &nbsp;&nbsp;<%=listmerchantregistercard_Search%>
                                        </button>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <%----------------------Report Start-----------------------------------%>

            <div class="row">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=listmerchantregistercard_Token_Data%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">


                            <%
                                if(request.getAttribute("listmerchantregistercard")!=null)
                                {
                                    Hashtable hash = (Hashtable) request.getAttribute("listmerchantregistercard");
                                    Hashtable temphash = null;
                                    int records = 0;
                                    int totalrecords = 0;
                                    int currentblock = 1;
                                    try
                                    {
                                        records = Integer.parseInt((String) hash.get("records"));
                                        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                        currentblock = Integer.parseInt(request.getParameter("currentblock"));
                                    }
                                    catch (Exception ex)
                                    {

                                    }
                                    String style = "class=tr0";
                                    String ext = "light";
                                    String style1 = "class=\"textb\"";

                                    if (records > 0)
                                    {
                            %>


                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead style="color: white;">
                                <tr>
                                    <th style="text-align: center"><%=listmerchantregistercard_Sr_No%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_Registration_Time%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_Token_ID%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_Card_Num%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_Token%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_First_Name%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_Last_Name%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_Email_ID%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_is_Active%></th>
                                    <th style="text-align: center"><%=listmerchantregistercard_Generated_By%></th>
                                </tr>
                                </thead>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <tbody>
                                <%
                                    for (int pos = 1; pos <= records; pos++)
                                    {
                                        String id = Integer.toString(pos);
                                        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                        style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                        temphash = (Hashtable) hash.get(id);

                                        String date =(String)temphash.get("creationtime");
                                        String tokenid =(String) temphash.get("tokenid");
                                        String token=(String)temphash.get("token");
                                        String firstname=(String)temphash.get("cardholder_firstname");
                                        String lastname=(String)temphash.get("cardholder_lastname");
                                        String email1=(String)temphash.get("cardholderemail");
                                        String isActive=(String)temphash.get("isactive");
                                        String useraccname="";
                                        Functions functions = new Functions();
                                        if (functions.isValueNull((String)temphash.get("generatedBy")))
                                        {
                                            useraccname = (String)temphash.get("generatedBy");
                                        }
                                        else
                                        {
                                            useraccname = "-";
                                        }
                                        String cardNum= PzEncryptor.decryptPAN((String) temphash.get("cnum"));
                                        if(functions.isValueNull(cardNum)){
                                            cardNum=Functions.getFirstSix(cardNum)+"******"+Functions.getLastFour(cardNum);
                                        }

                                        out.println("<tr>");
                                        out.println("<td data-label=\"Sr No\" style=\"text-align: center\">&nbsp;" + srno + "</td>");
                                        out.println("<td data-label=\"Registration Time\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(date) + "</td>");
                                        out.println("<td data-label=\"Token ID\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(tokenid) + "</td>");
                                        out.println("<td data-label=\"Token ID\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(cardNum) + "</td>");
                                        out.println("<td data-label=\"Token\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(token) + "</td>");
                                        out.println("<td data-label=\"First Name\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(firstname) + "</td>");
                                        out.println("<td data-label=\"Last Name\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(lastname) + "</td>");
                                        out.println("<td data-label=\"Email ID\" style=\"text-align: center\">&nbsp;" + functions.getEmailMasking(email1) + "</td>");
                                        out.println("<td data-label=\"is Active\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(isActive) + "</td>");
                                        out.println("<td data-label=\"Generated By\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(useraccname) + "</td>");
                                        out.println("</tr>");
                                    }
                                %>
                                </tbody>
                                <%--<thead>
                                <tr>
                                    <td  align="left" class="tablefooter">Total Records : <%=totalrecords%></td>
                                    &lt;%&ndash;<td  align="right" class="th1">Page No : <%=pageno%></td>&ndash;%&gt;
                                    <td class="tablefooter">&nbsp;</td>
                                    <td class="tablefooter">&nbsp;</td>
                                    <td class="tablefooter">&nbsp;</td>
                                    <td class="tablefooter">&nbsp;</td>
                                </tr>
                                </thead>--%>
                            </table>

                        </div>

                        <%
                            currentblock = 1;
                            try
                            {
                                currentblock = Integer.parseInt(request.getParameter("currentblock"));
                            }
                            catch (Exception ex)
                            {
                                currentblock = 1;
                            }

                        %>
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
                    <div id="showingid"><strong><%=listmerchantregistercard_page_no%> <%=pageno%> <%=listmerchantregistercard_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                    <div id="showingid"><strong><%=listmerchantregistercard_total_no_of_records%>   <%=totalrecords%> </strong></div>

                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                            <jsp:param name="numrows" value="<%=pagerecords%>"/>
                            <jsp:param name="pageno" value="<%=pageno%>"/>
                            <jsp:param name="str" value="<%=str%>"/>
                            <jsp:param name="page" value="ListMerchantRegisterCard"/>
                            <jsp:param name="currentblock" value="<%=currentblock%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>
                        <%--<div class="reporttable">

                                <%
                            if(request.getAttribute("listmerchantregistercard")!=null)
                            {

                            Hashtable hash = (Hashtable) request.getAttribute("listmerchantregistercard");
                            Hashtable temphash = null;
                            int records = 0;
                            int totalrecords = 0;
                            int currentblock = 1;
                            try
                            {
                                records = Integer.parseInt((String) hash.get("records"));
                                totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                currentblock = Integer.parseInt(request.getParameter("currentblock"));
                            }
                            catch (Exception ex)
                            {

                            }
                            String style = "class=tr0";
                            String ext = "light";
                            String style1 = "class=\"textb\"";

                            if (records > 0)
                            {
                        %>
                            <table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%">
                                <tr><td>
                                    <table border="0" cellpadding="5" cellspacing="2" width="100%" color="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
                                        <thead>
                                        <tr>
                                            <td width="10%" class="th0">Sr No</td>
                                            <td width="10%" class="th0">Register Time</td>
                                            <td width="10%" class="th1">Token ID</td>
                                            <td width="5%" class="th0"> Token</td>
                                            <td width="10%" class="th0">First Name</td>
                                            <td width="15%" class="th1">Last Name</td>
                                            <td width="15%" class="th1">Email ID</td>
                                            <td width="15%" class="th1">Is Active</td>
                                            <td width="15%" class="th1">GeneratedBy</td>

                                        </tr>
                                        </thead>
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <%
                                            for (int pos = 1; pos <= records; pos++)
                                            {
                                                String id = Integer.toString(pos);
                                                int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                                style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                                temphash = (Hashtable) hash.get(id);

                                                String date =(String)temphash.get("creationtime");
                                                String tokenid =(String) temphash.get("tokenid");
                                                String token=(String)temphash.get("token");
                                                String firstname=(String)temphash.get("cardholder_firstname");
                                                String lastname=(String)temphash.get("cardholder_lastname");
                                                String email1=(String)temphash.get("cardholderemail");
                                                String isActive=(String)temphash.get("isactive");
                                                String useraccname=(String)temphash.get("generatedBy");

                                                out.println("<tr " + style + ">");
                                                out.println("<td align=\"center\"" + style + ">" + srno + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date) + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tokenid) + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(token) + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(firstname) + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(lastname) + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(email1) + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isActive) + "</td>");
                                                out.println("<td align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(useraccname) + "</td>");
                                                out.println("</tr>");
                                            }
                                        %>
                                        <thead>
                                        <tr>
                                            <td  align="left" class="th0">Total Records : <%=totalrecords%></td>
                                            <td  align="right" class="th0">Page No : <%=pageno%></td>
                                            <td class="th0">&nbsp;</td>
                                            <td class="th0">&nbsp;</td>
                                            <td class="th0">&nbsp;</td>
                                            <td class="th0">&nbsp;</td>
                                            <td class="th0">&nbsp;</td>
                                            <td class="th0">&nbsp;</td>
                                            <td class="th0">&nbsp;</td>
                                            <td class="th0">&nbsp;</td>
                                        </tr>
                                        </thead>
                                    </table>
                                </td></tr>
                            </table>

                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                <jsp:param name="pageno" value="<%=pageno%>"/>
                                <jsp:param name="str" value="<%=str%>"/>
                                <jsp:param name="page" value="ListMerchantRegisterCard"/>
                                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>--%>

                        <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(listmerchantregistercard_Sorry, listmerchantregistercard_No));
                                }
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1(listmerchantregistercard_Sorry, listmerchantregistercard_No));
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