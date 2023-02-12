<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.Merchants" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.payment.request.PZPayoutRequest" %>
<%@ include file="Top.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String company = (String)session.getAttribute("company");
    session.setAttribute("submit","Payout");
%>
<html lang="en">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
<script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
<title><%=company%> Merchant Bulk Payout</title>
<script>
    $(function () {

        $('#File').change(function () {
            document.getElementById("upload").disabled = false;
            $("#search").val('');
            document.getElementById("uploadForm").setAttribute("enctype", "multipart/form-data");
            var  retpath    = document.FIRCForm.File.value;
            var pos         = retpath.indexOf(".");
            var filename="";
            if (pos != -1)
                filename = retpath.substring(pos + 1);
            else
                filename = retpath;
            if (filename==('xls'))
            {
                var files = $('#File').get(0).files;
                if (files.length > 0)
                {
                    var file = files[0];

                    var fileReader          = new FileReader();
                    fileReader.onloadend    = function (e)
                    {
                        var arr = (new Uint8Array(e.target.result)).subarray(0, 4);
                        var header = '';
                        for (var i = 0; i < arr.length; i++)
                        {
                            header += arr[i].toString(16);
                        }

                        if(header != "d0cf11e0"){
                            alert('Please select a .xls file instead!');
                            document.getElementById("upload").disabled = true;
                        }
                        console.log("filename "+filename);
                        document.getElementById("filename").value = filename;

                    };
                    fileReader.readAsArrayBuffer(file);
                }
            }
            else{
                alert('Please select a .xls file instead!');
                document.getElementById("upload").disabled = true;
            }
        });

    });

    function ToggleAll(checkbox)
    {
        flag = checkbox.checked;
        var checkboxes = document.getElementsByName("id");
        var total_boxes = checkboxes.length;

        for (i = 0; i < total_boxes; i++)
        {
            checkboxes[i].checked = flag;
        }
        function MakeSelect2()
        {
            $('select').select2();
            $('.dataTables_filter').each(function ()
            {
                $(this).find('label input[type=text]').attr('placeholder', 'Search');
            });
        }

        $(document).ready(function ()
        {
            // Load Datatables and run plugin on tables
            LoadDataTablesScripts(AllTables);
            // Add Drag-n-Drop feature

            WinMove();
        });
    }

    function uploadExcel()
    {
         $('#search').val(' ');
         $('#uploadForm').attr('enctype','multipart/form-data');
        document.getElementById("uploadForm").submit();
    }
    function searchForm()
    {
         $('#search').val('search');
         $('#uploadForm').removeAttr('enctype');
    }
    function downLoadExcel()
    {
         $('#search').val('download');
         $('#uploadForm').removeAttr('enctype');
    }
</script>
<style type="text/css">
    th {
        position: -webkit-sticky;
        position: sticky;
        top: 0;
        z-index: 2;
    }
</style>

</head>
<script>
</script>
<body class="pace-done widescreen fixed-left-void" onload="searchForm()">
<%--<%
    User user = (User) session.getAttribute("ESAPIUserSessionKey");
    Merchants merchants = new Merchants();
    if (merchants.isLoggedIn(session))
    {
%>--%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;BulkPayout Excel Upload</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="pull-right">
                            <div class="btn-group">
                                <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" method="POST">
                                    <button type="hidden" name="backSubmit" value="Bulk Payout Upload" class="btn btn-default" style="display: -webkit-box; margin-right: 15px;"><i class="fa fa-arrow-left" aria-hidden="true"></i>&nbsp;&nbsp;Back</button>
                                </form>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form" align="center">
                                <form name ="FIRCForm" id="uploadForm" method="post" action="/merchant/servlet/BulkPayOutUpload?ctoken=<%=ctoken%>" <%--enctype="multipart/form-data"--%>>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <input type="hidden" value="" name="perfromAction" id="search">
                                    <div align="center">

                                           <%
                                               String errorMsg  = (String)request.getAttribute("ERROR");

                                               String message   = (String)request.getAttribute("Message");
                                               String tableResult   = (String)request.getAttribute("tableResult");
                                               String search    = (String)request.getAttribute("search");
                                               String statusString = "";

                                                if(functions.isValueNull(errorMsg))
                                                {
                                                    statusString ="Failed";
                                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5>");
                                                }

                                                if(functions.isValueNull(message))
                                                {
                                                    statusString = "UPLOADED";
                                                    out.println("<div class=\"bg-info\" style=\"text-align:center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+ message +"</div>");
                                                }
                                                if(functions.isValueNull(search))
                                                {
                                                    statusString = "UPLOADED";
                                                }
                                           %>
                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Payout File:</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input class="form-control" name="File" id="File" type="file" value="choose File"
                                                               style="width: 370px" accept="application/vnd.ms-excel"></td>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <p style="text-align: center;">
                                                <button onclick="uploadExcel()" id="upload" type="submit" name="upload" value="uploadExcel"
                                                        style="display: inline-block!important;"
                                                        class="btn btn-default center-block"><i
                                                        class="fa fa-upload"></i>
                                                    &nbsp;&nbsp;Upload
                                                </button>
                                                &nbsp;&nbsp;&nbsp;&nbsp;
                                                <button onclick="searchForm()" id="serachData" type="submit" name="serach" value="serachData"
                                                        style="display: inline-block!important;"
                                                        class="btn btn-default center-block"><i
                                                        class="fa fa-clock-o"></i>
                                                    &nbsp;&nbsp;Search
                                                </button>
                                                &nbsp;&nbsp;&nbsp;&nbsp;
                                                <button onclick="downLoadExcel()" id="downloadExcel" type="submit" name="download" value="downloadExcel"
                                                        style="display: inline-block!important;"
                                                        class="btn btn-default center-block"><i
                                                        class="fa fa-download"></i>
                                                    &nbsp;&nbsp;Download Excel Format
                                                </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                List<PZPayoutRequest> pzPayoutRequestList       = null;
                if(request.getAttribute("pZPayoutRequestList") != null)
                {
                pzPayoutRequestList = (List<PZPayoutRequest>) request.getAttribute("pZPayoutRequestList");
                if(pzPayoutRequestList != null && pzPayoutRequestList.size() >0)
                {

            %>
            <form name="f1" method="post" action="/merchant/servlet/UploadBulkPayOut">
            <input type="hidden" value="<%=ctoken%>" name="ctoken">

            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>PayOut List <%--<font id="info_checkbox">(To view transaction summary data, please select appropriate checbox.)</font>--%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" <%--style="overflow-y: auto;"--%>>
                            <%if("UPLOADED".equals(statusString)){%>
                            <div align="right">
                                <button name="action" type="submit" class="btn btn-default"
                                        value="Payout">
                                    <span><i class="fa fa-clock-o"></i></span>
                                    &nbsp;&nbsp;Payout
                                </button>
                                <button name="action" type="submit" class="btn btn-default"
                                        value="Delete">
                                    <span><i class="fa fa-clock-o"></i></span>
                                    &nbsp;&nbsp;Cancel
                                </button>
                            </div>
                            <%}%>
                            <div style="width:100%; height:50%; overflow:auto;">
                            <table id="myTable" class="display table table-striped table-bordered"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;margin-top: 6px;">
                                <thead style="color: white;">
                                    <tr>
                                        <%if("UPLOADED".equals(statusString)){%>
                                            <th align="center" style="text-align: center"><input type="checkbox" onclick="ToggleAll(this);" name="alltrans"></th>
                                        <%}%>
                                        <%--<th style="text-align: center">Terminal Id</th>--%>
                                        <th style="text-align: center">Bank name</th>
                                        <th style="text-align: center">Account No</th>
                                        <th style="text-align: center">IFSC</th>
                                        <th style="text-align: center">Amount</th>
                                        <th style="text-align: center">Transfer Type</th>
                                        <th style="text-align: center">Discription</th>
                                        <th style="text-align: center">Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                     <%
                                        for( PZPayoutRequest pzPayoutRequest : pzPayoutRequestList){
                                            out.println("<tr>");
                                                if("UPLOADED".equals(statusString))
                                                {
                                                    out.println("<td align=\"center\"><input type=\"checkbox\" name=\"id\" value=\"" + pzPayoutRequest.getOrderId() + "\"></td>");
                                                }
                                                /*out.println("<td style=\"text-align: center\" data-label=\"terminalId\" align=\"center\">&nbsp;" + pzPayoutRequest.getTerminalId() + "</td>");*/
                                                out.println("<td style=\"text-align: center\" data-label=\"customerBankAccountName\" align=\"center\">&nbsp;" + pzPayoutRequest.getCustomerBankAccountName() + "</td>");
                                                out.println("<td style=\"text-align: center\" data-label=\"bankAccountNo\" align=\"center\">&nbsp;" + pzPayoutRequest.getBankAccountNo() + "</td>");
                                                out.println("<td style=\"text-align: center\" data-label=\"bankIfsc\" align=\"center\">&nbsp;" + pzPayoutRequest.getBankIfsc()+ "</td>");
                                                out.println("<td style=\"text-align: center\" data-label=\"bankTransferType\" align=\"center\">&nbsp;" + pzPayoutRequest.getPayoutAmount() + "</td>");
                                                out.println("<td style=\"text-align: center\" data-label=\"bankTransferType\" align=\"center\">&nbsp;" + pzPayoutRequest.getBankTransferType() + "</td>");
                                                out.println("<td style=\"text-align: center\" data-label=\"orderDescription\" align=\"center\">&nbsp;" + pzPayoutRequest.getOrderDescription() + "</td>");
                                                out.println("<td style=\"text-align: center\" data-label=\"status\" align=\"center\">&nbsp;"+pzPayoutRequest.getStatus()+"</td>");

                                            out.println("</tr>");
                                        }
                                    %>
                                </tbody>
                            </table>
                            </div>

                        </div>

                    </div>
                </div>
            </div>
            </form>
            <%
                }
            }
            %>
            <%if(functions.isValueNull(tableResult)){%>
            <div class="form-group col-md-12">
                <%out.println(tableResult);%>
            </div>
            <%}%>
            <div class="form-group col-md-12" style="margin-top: 10px">
                <div class="bg-info" ><i class="fa fa-info-circle"></i>&nbsp;&nbsp;
                    <p><strong>Note</strong> If a high number of transactions are selected, you might get a Session-out while waiting for a response. However, you need not to worry, as we will Payout the selected transactions from our end.</p>
                </div>
            </div>
        </div>
    </div>
</div>
<%--<%
    }
    else{
        response.sendRedirect("/merchant/Logout.jsp");
    }
%>--%>
</body>
</html>