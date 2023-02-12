<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.directi.pg.Merchants" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp" %>
<%@ include file="Top.jsp" %>
<%--<%@include file="whitelistTab.jsp" %>--%>
<%--
  Created by IntelliJ IDEA.
  User: Sanjay
  Date: 1/24/2022
  Time: 1:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Upload Bulk Cards</title>

    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css"
          integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
    <script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
        <script src="/merchant/NewCss/libs/bootstrap/js/umd/popper.js"></script>
          <script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/content.js"></script>

    <script type="text/javascript" language="JavaScript">

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
    </script>
    <script type="text/javascript">
        function getWhitelistingLevel(ctoken)
        {
            console.log("inside")
            var memberId = document.getElementById("memberid1").value;
            console.log("memberId:::", memberId)
            document.FIRCForm.action = "/merchant/servlet/UploadBulkCards?ctoken=" + ctoken + "&action=getdata" + "&toid=" + memberId;
            document.FIRCForm.submit()
            console.log("memberId 11:::", memberId)
        }

        function uploadExcel()
        {
            $('#search').val(' ');
            $('#uploadForm').attr('enctype','multipart/form-data');
            document.getElementById("uploadForm").submit();
        }

        function downLoadExcel()
        {
            $('#search').val('download');
            $('#uploadForm').removeAttr('enctype');
            document.getElementById("uploadForm").submit();
        }
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

        tr:nth-child(odd) {
            background: transparent !important;
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

        .eye-icon {
            float: right;
            z-index: 2;
            margin-right: 5px;
        }

        .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
            border-top: 1px solid #ddd;
        }

        /********************Table Responsive Ends**************************/
    </style>
</head>
<body>
<%

    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String memberid = (String)session.getAttribute("merchantid");
    ResourceBundle rb1 = null;

    String accountId = request.getParameter("accountid") == null ? "" : request.getParameter("accountid");
    Functions functions = new Functions();
    String language_property1 = (String)session.getAttribute("language_property");

    rb1 = LoadProperties.getProperty(language_property1);
    String whitelist_merchantid1 = StringUtils.isNotEmpty(rb1.getString("whitelist_merchantid1"))?rb1.getString("whitelist_merchantid1"): "Merchant ID*";
    String whitelist_account_id = StringUtils.isNotEmpty(rb1.getString("whitelist_account_id"))?rb1.getString("whitelist_account_id"): "Account ID";
    String Upload_ExcelFile = StringUtils.isNotEmpty(rb1.getString("Upload_ExcelFile"))?rb1.getString("Upload_ExcelFile"): "Upload Excel File";
    String Upload = StringUtils.isNotEmpty(rb1.getString("Bulk_Upload"))?rb1.getString("Bulk_Upload"): "Upload";
    String Download_Excel_Format = StringUtils.isNotEmpty(rb1.getString("Download_Excel_Format"))?rb1.getString("Download_Excel_Format"): "Download Excel Format";
    String Upload_Bulk_Cards = StringUtils.isNotEmpty(rb1.getString("Upload_Bulk_Cards"))?rb1.getString("Upload_Bulk_Cards"): "Upload Bulk Cards";


%>
<div class="content-page" style="margin-top: 60px !important;">
    <div class="content" style="padding:0px 20px ;margin: 0px;">
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=Upload_Bulk_Cards%></strong>
                            </h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                            <div class="pull-right">
                                <div class="btn-group">
                                    <form action="/merchant/whitelist.jsp?ctoken=<%=ctoken%>" method="post" name="form">
                                        <button class="btn-xs" type="submit" name="B1"
                                                style="background: transparent;border: 0;">
                                            <img style="height: 35px;" src="/merchant/images/goBack.png">
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                        <div class="widget-content">

                            <div id="horizontal-form">
                                <form name="FIRCForm" action="/merchant/servlet/UploadBulkCards?ctoken=<%=ctoken%>"
                                      id="uploadForm"  method="post">
                                    <input type="hidden" name="whitelistlevel"
                                           value="<%=request.getAttribute("whitelistlevel")%>">
                                    <input type="hidden" name="toid" value="<%=memberid%>">
                                    <input type="hidden" name="merchantid" value="<%=memberid%>" id="merchantid">

                                    <input type="hidden" value="" name="performAction" id="search">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">

                                <%--<input type="hidden" name="accountid" value="<%=accountid%>">--%>
                                    <div align="center">
                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label><%=whitelist_merchantid1%></label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input name="toid" id="memberid1"
                                                           class="form-control"
                                                           value="<%=memberid%>" disabled>
                                                </div>

                                            </div>
                                        </div>

                                       <%-- <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label><%=whitelist_account_id%> </label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input id="accountid" name="accountid" value="<%=accountId%>"
                                                           autocomplete="off"  class="form-control">
                                                </div>
                                            </div>
                                        </div>--%>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label><%=Upload_ExcelFile%></label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input id="File" type="file" name="File" value="Choose File"
                                                           class="form-control" accept="application/vnd.ms-excel">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group col-md-12">
                                            <p style="text-align: center;">
                                                <button type="submit" name="upload" value="uploadExcel" id="upload"
                                                        onclick="uploadExcel()" class="buttonform btn btn-default"><i
                                                        class="fa fa-upload"></i>
                                                   <%=Upload%>
                                                </button>
                                                &nbsp;&nbsp;&nbsp;&nbsp;
                                                <button  id="downloadExcel" type="submit" name="download" value="downloadExcel"
                                                        style="display: inline-block!important;" onclick="downLoadExcel()"
                                                        class="btn btn-default center-block"><i
                                                        class="fa fa-download"></i>
                                                    &nbsp;&nbsp;<%=Download_Excel_Format%>
                                                </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>

                    </div>

                </div>
            </div>
        </div>
        <div class="widget">
            <%

                if (functions.isValueNull(String.valueOf(request.getAttribute("sSuccessMessage"))) || functions.isValueNull(String.valueOf(request.getAttribute("sErrorMessage"))))
                {
                    if (functions.isValueNull(String.valueOf(request.getAttribute("sSuccessMessage"))))
                    {
                        String successMessage = request.getAttribute("sSuccessMessage").toString();
                        out.println(Functions.NewShowConfirmation(successMessage, ""));
                    }
                    if (functions.isValueNull(String.valueOf(request.getAttribute("sErrorMessage"))))
                    {
                        String errorMessage = request.getAttribute("sErrorMessage").toString();
                        out.println(Functions.NewShowConfirmation("Failed Updating", errorMessage));
                    }
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Filter","Please upload the Whitelisted card details File."));
                }

            %>
        </div>
    </div>
</div>
</div>

</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if (str == null)
            return "";
        return str;
    }
%>
