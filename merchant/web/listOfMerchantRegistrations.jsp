<%@ page errorPage="error.jsp"   import="com.directi.pg.Functions,org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.TokenDetailsVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>

<%@ include file="Top.jsp" %>
 <%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Registration History");

    String fromdate = null;
    String todate = null;

    Date date1 = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date1);
    date1.setDate(1);
    String fromDate = originalFormat.format(date1);

    fromdate = Functions.checkStringNull((String)request.getAttribute("fdate")) == null ? fromDate : (String)request.getAttribute("fdate");
    todate = Functions.checkStringNull((String)request.getAttribute("tdate")) == null ? Date : (String)request.getAttribute("tdate");

    PaginationVO paginationVO = new PaginationVO();
    String firstName="";
    String lastName="";
    String email="";

    firstName = Functions.checkStringNull(request.getParameter("firstname"));
    lastName=Functions.checkStringNull(request.getParameter("lastname"));
    email=Functions.checkStringNull(request.getParameter("email"));

    if(firstName==null){firstName="";}
    if(lastName==null){lastName="";}
    if(email==null){email="";}

    Calendar rightNow = Calendar.getInstance();
    String str = "";
    str= str + "&ctoken="+ctoken;
    if (fromdate == null) fromdate = "" + 1;
    if (todate == null) todate = "" + rightNow.get(rightNow.DATE);


    if (fromdate != null) str = str + "&fdate=" + fromdate;
    if (todate != null) str = str + "&tdate=" + todate;
    if(firstName!=null)str = str + "&firstname=" + firstName;
    if(lastName!=null)str = str + "&lastname=" + lastName;
    if(email!=null)str = str + "&email=" + email;
    str = str + "&SRecords=" + paginationVO.getRecordsPerPage();

    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

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
    <title><%=company%>  Register Card List</title>
    <script language="javascript">
        /*function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }*/

        function DoDelete()
        {
            if (confirm("Do you really want to delete this token."))
            {
                return true;
            }
            else
            {
               return false;
            }
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

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <form name="form" method="post" action="/merchant/servlet/ListOfMerchantRegistrations?ctoken=<%=ctoken%>">

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp; Registration History</strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>



                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>From</label>
                                        <input type="text" size="16" name="fdate" class="datepicker form-control" value="<%=fromdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>To</label>
                                        <input type="text" size="16" name="tdate" class="datepicker form-control" value="<%=todate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>First&nbsp;Name</label>

                                        <input type=text name="firstname" class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstName)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Last&nbsp;Name</label>
                                        <input type=text name="lastname" class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastName)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Email ID</label>
                                        <input type="text" name="email" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(email)%>">
                                    </div>
                                   <%-- <div class="form-group col-md-4 has-feedback">--%>
                                    </div>

                                    <div class="form-group col-md-4">
                                    </div>

                                    <br>
                                    <div class="form-group col-md-4">
                                        <button type="submit" class="btn btn-default" name="B1" >
                                            <span><i class="fa fa-clock-o"></i></span>
                                           &nbsp;&nbsp;Search
                                        </button>
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
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Token Data</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content" style="overflow-y: auto;">

                            <%
                                String error = (String) request.getAttribute("error");
                                String msg = (String) request.getAttribute("msg");
                                if(functions.isValueNull(error))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                    return;
                                }
                                else
                                if(functions.isValueNull(msg))
                                {
                                    out.println(Functions.NewShowConfirmation1("Message", msg));
                                    return;
                                }

                                List<TokenDetailsVO> tokenDetailsVOs = (List<TokenDetailsVO>) request.getAttribute("listOfMerchantRegistrations");

                                StringBuffer requestParameter= new StringBuffer();
                                Enumeration<String> requestName=request.getParameterNames();
                                while(requestName.hasMoreElements())
                                {
                                    String name=requestName.nextElement();
                                    if("SPageno".equals(name) || "SRecords".equals(name))
                                    {

                                    }
                                    else
                                        requestParameter.append("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                }
                                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                                if(tokenDetailsVOs != null && !((List<TokenDetailsVO>) request.getAttribute("listOfMerchantRegistrations")).isEmpty())
                                {
                                    if(tokenDetailsVOs.size() > 0)
                                    {
                                        //System.out.println("inside>0---->"+tokenDetailsVOs.size());
                                        paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                                        paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                                        int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
                            %>


                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead style="color: white;">
                                <tr>
                                    <th style="text-align: center">Sr No</th>
                                    <<th style="text-align: center">Registration Time</th>
                                    <th style="text-align: center">Token</th>
                                    <th style="text-align: center">First Name</th>
                                    <th style="text-align: center">Last Name</th>
                                    <th style="text-align: center">Email ID</th>
                                    <th style="text-align: center">is Active</th>
                                    <th style="text-align: center">Generated By</th>
                                    <th style="text-align: center">Type</th>
                                    <th style="text-align: center">Action</th>
                                </tr>
                                </thead>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <tbody>
                                <%
                                    for (TokenDetailsVO tokenDetailsVO : tokenDetailsVOs)
                                    {
                                        String fname = tokenDetailsVO.getAddressDetailsVO().getFirstname();
                                        String lname= tokenDetailsVO.getAddressDetailsVO().getLastname();
                                        String mail= tokenDetailsVO.getAddressDetailsVO().getEmail();
                                        String tokenType = "";


                                        if(!functions.isValueNull(fname)) {fname = " - "; }
                                        if(!functions.isValueNull(lname)) {lname = " - "; }
                                        if(!functions.isValueNull(mail)) {mail = " - "; }
                                        out.println("<tr>");
                                        out.println("<td data-label=\"Sr No\" style=\"text-align: center\">&nbsp;" + srno + "</td>");
                                        out.println("<td data-label=\"Registration Time\" style=\"text-align: center\">&nbsp;" + tokenDetailsVO.getCreationOn() + "</td>");
                                        out.println("<td data-label=\"Token\" style=\"text-align: center\">&nbsp;" + tokenDetailsVO.getRegistrationToken() + "</td>");
                                        out.println("<td data-label=\"First Name\" style=\"text-align: center\">&nbsp;" +fname+ "</td>");
                                        out.println("<td data-label=\"Last Name\" style=\"text-align: center\">&nbsp;" + lname + "</td>");
                                        out.println("<td data-label=\"Email ID\" style=\"text-align: center\">&nbsp;" + mail + "</td>");
                                        out.println("<td data-label=\"is Active\" style=\"text-align: center\">&nbsp;" + tokenDetailsVO.getIsActive() + "</td>");
                                        out.println("<td data-label=\"Generated By\" style=\"text-align: center\">&nbsp;" + tokenDetailsVO.getGeneratedBy()+ "</td>");
                                        if(functions.isValueNull(tokenDetailsVO.getBankAccountId())) {
                                            tokenType = "Account Token";
                                        }
                                        else {
                                            tokenType = "Card Token";
                                        }
                                        out.println("<td data-label=\"Type\"  style=\"text-align: center\">&nbsp;" +tokenType+ "</td>");
                                        out.println("<td data-label=\"Action\" style=\"text-align: center\">&nbsp;" +
                                                "<form action=\"/merchant/servlet/DeleteRegistration?ctoken="+ctoken+"\" method=\"POST\">" +
                                                "<input type=\"hidden\" name=\"tokenid\" value=" +tokenDetailsVO.getRegistrationToken()+ ">" +
                                                "<input type=\"hidden\" name=\"fdate\" value=" +fromdate+ ">" +
                                                "<input type=\"hidden\" name=\"tdate\" value="+todate+">"+
                                                "<input type=\"hidden\" name=\"firstname\" value="+firstName+">"+
                                                "<input type=\"hidden\" name=\"lastname\" value="+lastName+">"+
                                                "<input type=\"hidden\" name=\"email\" value="+email+">"+
                                                "<input type=\"submit\" name=\"DeleteRegistration\" value=\"Delete\" class=\"btn btn-default\" width=\"100\" onClick=\"return DoDelete()\" ></form></td>");
                                        out.println("</tr>");
                                        srno++;
                                    }
                                %>
                                </tbody>
                            </table>
                        </div>

                        <%
                            if(paginationVO.getRecordsPerPage() > paginationVO.getTotalRecords())
                            {
                                paginationVO.setRecordsPerPage(paginationVO.getTotalRecords());
                            }
                        %>
                        <div id="showingid"><strong>Showing <%=paginationVO.getRecordsPerPage()%> of <%=paginationVO.getTotalRecords()%> records</strong></div>

                        <jsp:include page="page.jsp" flush="true">
                            <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                            <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                            <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                            <jsp:param name="page" value="ListOfMerchantRegistrations"/>
                            <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>


                        <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry", "No Records Found for given search criteria."));
                                }
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1("Sorry", "No Records Found for given search criteria."));
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