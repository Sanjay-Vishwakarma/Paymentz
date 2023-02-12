<%@ page errorPage="error.jsp" import="com.directi.pg.Functions,java.util.Hashtable" %>
<%@ page import="com.directi.pg.Logger" %>

<%@ include file="Top.jsp" %>
<%String company = (String)session.getAttribute("company");%>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

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

    </style>
</head>

<body class="pace-done widescreen fixed-left-void">
<%  Logger logger = new Logger("test1");
    try
    {
        Hashtable hash = (Hashtable) request.getAttribute("podconfirm");
        int hashsize = 0;
        if(hash!=null)
        {
            hashsize=hash.size();
        }
        String error=(String) request.getAttribute("error");
        if (hashsize > 0)
        {
%>
<%--<div class="rowcontainer-fluid">
    <div class="row rowadd" style="margin-top: 70px">
        <div class="form foreground bodypanelfont_color panelbody_color">
            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/color:#34495e;padding-top: 5px;font-size: 18px;align: initial;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Proof Of Delivery Confirmation</h2>
            <p class="hrform"></p>
        </div>--%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Proof Of Delivery Confirmation</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">

                            <table class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <thead style="color: white;">
                                <tr >
                                    <th style="text-align: center; color: white;">Sr No</th>
                                    <th style="text-align: center; color: white;">Tracking Id</th>
                                    <th style="text-align: center; color: white;">Status</th>
                                </tr>
                                </thead>
                                <%
                                    String style = "";
                                    String ext = "light";

                                    for (int pos = 1; pos <= hashsize; pos++)
                                    {
                                        String id = Integer.toString(pos);
                                        style = "class=\"tr" + pos % 2 + "\"";

                                        String value = (String) hash.get(id);
                                        int pos1 = value.indexOf("|");

                                        out.println("<tbody>");
                                        out.println("<tr>");
                                        out.println("<td data-label=\"Sr No\" style=\"text-align: center\">&nbsp;" + id + "</td>");
                                        out.println("<td data-label=\"Tracking Id\" style=\"text-align: center\">&nbsp;" + value.substring(0, pos1) + "</a></td>");
                                        out.println("<td data-label=\"Status\" style=\"text-align: center\">&nbsp;" + value.substring(pos1 + 1) + "</a></td>");
                                        out.println("</tr>");
                                        out.println("</tbody>");
                                    }
                                %>
                            </table>

                            <%
                                    }
                                    else if(!error.equals("") && error!=null)
                                    {
                                        out.println("<div class=\"content-page\">");
                                        out.println("<div class=\"content\">");
                                        out.println("<div class=\"page-heading\">");
                                        out.println("<div class=\"row\">");
                                        out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
                                        out.println("<div class=\"widget\">");
                                        out.println("<div class=\"widget-header transparent\">\n" +
                                                "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;Capture Status</strong></h2>\n" +
                                                "                                <div class=\"additional-btn\">\n" +
                                                "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                                                "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                                                "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                                                "                                </div>\n" +
                                                "                            </div>");
                                        out.println("<div class=\"widget-content padding\">");

                                        out.println(Functions.NewShowConfirmation1("Error",error));
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                    }
                                    else
                                    {
                                        out.println(Functions.NewShowConfirmation1("Sorry", "No Transaction is Captured."));
                                    }
                                }
                                catch(Exception e)
                                {
                                    logger.error("Exception podConfirmation",e);
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