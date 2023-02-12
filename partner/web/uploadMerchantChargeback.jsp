<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ResourceBundle" %>

<%--
  Created by IntelliJ IDEA.
  User: NAMRATA BARI.
  Date: 15/09/2019
  Time: 11:22 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload Chargeback</title>
    <style type="text/css">
        @media (min-width: 768px) {
            .form-horizontal .control-label {
                text-align: left !important;
            }

            table {
                width: 100%;
                max-width: 40%;
                border-collapse: collapse;
                margin-bottom: 20px;
                display: table;
                border-collapse: separate;
                /* border-color: grey; */
            }

            .txtbox {
                color: #001962;
                text-valign: center;
                font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
                font-size: 12px;
                FONT-WEIGHT: normal;
                width: 142px;
                height: 25px;
                -webkit-border-radius: 4px;
                -moz-border-radius: 4px;
                border-radius: 4px;
            }

            .textb {
                color: #001962;
                text-valign: center;
                font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
                font-size: 12px;
                /* font-weight: bold; */
            }

            td {
                padding-top: 10px;
                padding-bottom: 10px;
                padding-left: 10px;
                padding-right: 10px;
                vertical-align: top;
                border-bottom: none;
            }

            button, input, optgroup, select, textarea {
                color: inherit;
                font: inherit;
                margin: 0;
            }
        }
    </style>
    <script type="text/javascript" language="JavaScript">
        function check()
        {
            var retpath = document.FIRCForm.File.value;
            var pos = retpath.indexOf(".");
            var filename = "";
            if (pos != -1)
                filename = retpath.substring(pos + 1);
            else
                filename = retpath;
            if (filename == ('xls') || filename == ('xlsx'))
            {
                return true;
            }
            alert('Please select .xls or .xlsx file instead!');
            return false;
        }
    </script>
</head>
<body>
<%
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String uploadMerchantChargeback_Upload_Chargeback = StringUtils.isNotEmpty(rb1.getString("uploadMerchantChargeback_Upload_Chargeback")) ? rb1.getString("uploadMerchantChargeback_Upload_Chargeback") : "Upload Chargeback";
    String uploadMerchantChargeback_Bank_Name = StringUtils.isNotEmpty(rb1.getString("uploadMerchantChargeback_Bank_Name")) ? rb1.getString("uploadMerchantChargeback_Bank_Name") : "Bank Name";
    String uploadMerchantChargeback_Bank_Chargeback = StringUtils.isNotEmpty(rb1.getString("uploadMerchantChargeback_Bank_Chargeback")) ? rb1.getString("uploadMerchantChargeback_Bank_Chargeback") : "Bank Chargeback File";
    String uploadMerchantChargeback_Upload = StringUtils.isNotEmpty(rb1.getString("uploadMerchantChargeback_Upload")) ? rb1.getString("uploadMerchantChargeback_Upload") : "Upload";
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String partnerid = String.valueOf(session.getAttribute("partnerId"));
    Logger logger = new Logger("uploadMerchantChargeback.jsp");
    String pgtypeId = "";
    if (request.getParameter("pgtypeid") != null && request.getParameter("pgtypeid").length() > 0)
        pgtypeId = request.getParameter("pgtypeid");
    if (partner.isLoggedInPartner(session))
    {
%>


<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->

        <%--Test Level--%>
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnerMerchantChargebackList.jsp?ctoken=<%=ctoken%>" method="post"
                          name="form">
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <br>
            <br>
            <div class="row">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><i class="fa fa-upload"></i>&nbsp;&nbsp;<strong><%=uploadMerchantChargeback_Upload_Chargeback%></strong></h2>

                            <div class="additional-btn">

                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <form name="FIRCForm" action="/partner/net/UploadChargebackFile?ctoken=<%=ctoken%>"
                              method="post" ENCTYPE="multipart/form-data">
                            <%
                                String errorMsg = (String) request.getAttribute("errorMsg");
                                if (errorMsg != null)
                                {
                                    out.println("<center><font class=\"textb\"><b>" + errorMsg + "</b></font></center>");
                                }
                            %>
                            <div class="col-lg-12">
                                <div class="panel panel-default" style="background-color: #f8f8f8;">
                                    <br>
                                    <table>
                                        <tr>
                                            <div class="form-group col-md-12">
                                                <div class="ui-widget">
                                                    <div class="col-sm-offset-3 col-md-2" style="margin-top:6px;">
                                                        <label class="control-label"><%=uploadMerchantChargeback_Bank_Name%></label>
                                                    </div>
                                                    <div class="col-md-3">
                                                        <select id="pgtypeid" class="form-control" name="pgtypeid">
                                                            <option value=""></option>
                                                            <%
                                                                Connection con = null;
                                                                if (errorMsg != null)
                                                                {
                                                                    out.println("<center><font class=\"textb\"><b>" + errorMsg + "</b></font></center>");
                                                                }
                                                                try
                                                                {
                                                                    con = Database.getConnection();
                                                                    StringBuffer qry = new StringBuffer("SELECT  DISTINCT gtype.gateway FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid=?");
                                                                    PreparedStatement pstmt = con.prepareStatement(qry.toString());
                                                                    pstmt.setString(1, partnerid);
                                                                    ResultSet rs = pstmt.executeQuery();
                                                                    while (rs.next())
                                                                    {
                                                                        String selected = "";
                                                                        if (rs.getString("gateway").equals(pgtypeId))
                                                                        {
                                                                            selected = "selected";
                                                                        }
                                                                        out.println("<option value=" + rs.getString("gateway") + " " + selected + ">" + rs.getString("gateway") + "</option>");
                                                                    }
                                                                }
                                                                catch (Exception e)
                                                                {
                                                                    logger.error("Exception::::" + e);
                                                                }
                                                                finally
                                                                {
                                                                    Database.closeConnection(con);
                                                                }
                                                            %>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="form-group col-md-12">
                                                <div class="ui-widget">
                                                    <div class="col-sm-offset-3 col-md-2" style="margin-top:6px;">
                                                        <label class="control-label"><%=uploadMerchantChargeback_Bank_Chargeback%></label>
                                                    </div>
                                                    <div class="col-md-3">
                                                    <input name="File" type="file" value="choose File"
                                                           class="btn-default">
                                                        </div>
                                                </div>
                                            </div>
                                            <div class="form-group col-md-12">
                                                <div class="col-md-7 text-right center-block">
                                                    <button name="mybutton" type="submit" value="Upload" class="buttonform btn btn-default" onclick="return check()"><%=uploadMerchantChargeback_Upload%>
                                                    </button>
                                                </div>
                                            </div>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </form>
                            <%
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
  }
%>
</body>
</html>