<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.BankTypeVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Namrata Bari.
  Date: 31/10/2019
  Time: 18:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp" %>

<html>
<head>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <title>Partner Bank Mapping</title>

    <style type="text/css">
        @media (max-width: 991px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit !important;
            }
        }
    </style>

</head>

<body>

<%!
    Logger logger = new Logger("test.jsp");
    private ApplicationManager applicationManager = new ApplicationManager();
    private Functions functions = new Functions();
%>
<%

    session.setAttribute("submit", "merchantmappingbank");
    java.util.TreeMap<String, String> partneriddetails = null;
    ApplicationManager applicationManager = new ApplicationManager();
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String defaultGatewaySetting_Default_Gateway = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Default_Gateway")) ? rb1.getString("defaultGatewaySetting_Default_Gateway") : "Default Gateway Mapping";
    String defaultGatewaySetting_Partner_ID = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Partner_ID")) ? rb1.getString("defaultGatewaySetting_Partner_ID") : "Partner ID";
    String defaultGatewaySetting_Select_PartnerId = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Select_PartnerId")) ? rb1.getString("defaultGatewaySetting_Select_PartnerId") : "Select Partner Id";
    String defaultGatewaySetting_Search = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Search")) ? rb1.getString("defaultGatewaySetting_Search") : "Search";
    String defaultGatewaySetting_Report_Table = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Report_Table")) ? rb1.getString("defaultGatewaySetting_Report_Table") : "Report Table";
    String defaultGatewaySetting_partnerid = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_partnerid")) ? rb1.getString("defaultGatewaySetting_partnerid") : "Partner Id:";
    String defaultGatewaySetting_Selected = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Selected")) ? rb1.getString("defaultGatewaySetting_Selected") : "Selected";
    String defaultGatewaySetting_Gateway_Name = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Gateway_Name")) ? rb1.getString("defaultGatewaySetting_Gateway_Name") : "Gateway Name";
    String defaultGatewaySetting_Update = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Update")) ? rb1.getString("defaultGatewaySetting_Update") : "Update";
    String defaultGatewaySetting_Sorry = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Sorry")) ? rb1.getString("defaultGatewaySetting_Sorry") : "Sorry";
    String defaultGatewaySetting_no = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_no")) ? rb1.getString("defaultGatewaySetting_no") : "No records found";
    String defaultGatewaySetting_Filter = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_Filter")) ? rb1.getString("defaultGatewaySetting_Filter") : "Filter";
    String defaultGatewaySetting_please = StringUtils.isNotEmpty(rb1.getString("defaultGatewaySetting_please")) ? rb1.getString("defaultGatewaySetting_please") : "Please Fill the Data for Partner Bank Mapping Details";
    Map<String, BankTypeVO> partnerMappedBank;
    String partnerid = nullToStr(request.getParameter("partnerid"));
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    String Style1= "";
    String Style2= "";
    //String str="";
    String dISABLED_Id="";
    String partnerId=null;
    partneriddetails = partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));

    if(Roles.contains("superpartner")){

    }else{
        dISABLED_Id=String.valueOf(session.getAttribute("merchantid"));

    }
    /*partnerId= (String) session.getAttribute("merchantid");
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("partnerId")));
    if(Roles.contains("superpartner") || Roles.contains("childsuperpartner"))
    {
        pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
    }
    str=str+"&pid="+pid;*/
%>
<script type="text/javascript">
    $( document ).ready(function() {
        var merchantid = <%=dISABLED_Id%>;
        if(merchantid != "")
        {
            $("#partnerid").val(merchantid);
            document.getElementById("partnerid").disabled = true;

            //document.forms.submit();
        }
    });
</script>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>" method="post" name="form">
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
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-table"></i>&nbsp;&nbsp;<%=defaultGatewaySetting_Default_Gateway%></strong></h2>
                        </div>
                        <div class="additional-btn">
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>
                        <form action="/partner/net/DefaultGatwaySetting?ctoken=<%=ctoken%>" method="post"
                              name="forms" class="form-horizontal">
                            <%
                                // Map<String,MerchantDetailsVO> merchantDetailsVOList=merchantDAO.getALLMerchantForPartner((String)session.getAttribute("merchantid"));
                            %>

                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <%
                                        if (request.getAttribute("error") != null)
                                        {
                                             out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"  + request.getAttribute("error") + "</h5>");

                                        }
                                        if (request.getAttribute("catchError") != null)
                                        {
                                            //out.println("<center><font class=\"form-control\" style=\"text-align: center; margin-bottom: 15px; background-color: #d0f1e4;\">" + request.getAttribute("catchError") + "</font></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                        }
                                        if (request.getAttribute("success") != null)
                                        {
                                            //out.println("<center><font class=\"form-control\" style=\"text-align: center; margin-bottom: 15px; background-color: #d0f1e4;\">" + request.getAttribute("catchError") + "</font></center>");
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + request.getAttribute("success") + "</h5>");
                                        }
                                        if (request.getAttribute("DELETED") != null)
                                        {
                                            //out.println("<center><font class=\"form-control\" style=\"text-align: center; margin-bottom: 15px; background-color: #d0f1e4;\">Deleted Successfully</font></center>");
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;Deleted Successfully</h5>");
                                        }
                                        if (request.getAttribute("update") != null)
                                        {
                                            //out.println("<center><font class=\"form-control\" style=\"text-align: center; margin-bottom: 15px; background-color: #d0f1e4;\">Updated Successfully</font></center>");
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;Updated Successfully</h5>");
                                        }

                                    %>
                                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                                    <div class="form-group col-md-4 has-feedback" <%=Style2%>>
                                        <label class="col-sm-4 control-label"><%=defaultGatewaySetting_Partner_ID%></label>
                                        <div class="col-sm-8">
                                            <select class="form-control" name="partnerid" id="partnerid">
                                                <option value="" default><%=defaultGatewaySetting_Select_PartnerId%></option>
                                                <%
                                                    String Selected = "";
                                                    for (String pid : partneriddetails.keySet())
                                                    {
                                                        if (pid.toString().equals(partnerid))
                                                        {
                                                            Selected = "selected";
                                                        }
                                                        else
                                                        {
                                                            Selected = "";
                                                        }
                                                %>
                                                <option value="<%=pid%>" <%=Selected%>><%=partneriddetails.get(pid)%>
                                                </option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                            <input type="hidden" value="<%=dISABLED_Id.equals("")?"":dISABLED_Id%>" name="partnerid">
                                        </div>
                                    </div>
                                    <%--<div class="form-group col-md-4 has-feedback" <%=Style1%>>
                                        <label class="col-sm-4 control-label">Partner ID</label>
                                        <div class="col-sm-8">
                                            <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                                            <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                                        </div>
                                    </div>--%>

                                    <div class="form-group col-md-3 has-feedback">&nbsp;</div>

                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default" name="action" value="1_View">
                                                <i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=defaultGatewaySetting_Search%>
                                            </button>
                                        </div>
                                    </div>


                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="row reporttable">

            <%
                try
                {
                     partnerMappedBank = (Map<String, BankTypeVO>) request.getAttribute("partnerMappedBank");
                    if(partnerMappedBank != null){
                    List<BankTypeVO> partnerMappedGateway = applicationManager.getAllGatewayMappedToPartner(request.getParameter("partnerid"));
            %>
            <form action="/partner/net/DefaultGatwaySetting?ctoken=<%=ctoken%>" method="post" name="myForm">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                               <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=defaultGatewaySetting_Report_Table%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <br>
                        <center>
                            <table align="center" id="smalltable" border="0"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 50%;">

                                <tr>

                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <center><font class="textb"
                                                      style="color: #ffffff;font-family: Open Sans;font-size: 13px;font-weight: 700;"><%=defaultGatewaySetting_partnerid%>
                                            </font></center>
                                    </td>
                                    <td valign="middle" align="center">
                                        <center><font class="textb"><input name="partnerid" type="text"
                                                                           class="form-control"
                                                                           value="<%=request.getParameter("partnerid")%>"
                                                                           readonly/></font></center>
                                    </td>

                                </tr>

                            </table>

                            <table align=center width="50%" id="smalltable"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 50%;">

                                <thead>
                                <tr>
                                    <td valign="middle" data-label="Selected" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=defaultGatewaySetting_Selected%></b></td>
                                    <td valign="middle" data-label="Gateway Name" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=defaultGatewaySetting_Gateway_Name%></b></td>


                                </tr>
                                </thead>
                                <%
                                    for (BankTypeVO bankTypeVO : partnerMappedGateway)
                                    {
                                %>
                                <tbody>
                                <tr>

                                    <td valign="middle" data-label="Selected" align="center"><input type=checkbox
                                                                                                    class="form-control"
                                                                                                    name='pgTypeId'
                                                                                                    value="<%=bankTypeVO.getBankId()%>" <%=partnerMappedBank != null && partnerMappedBank.containsKey(bankTypeVO.getBankId()) ? "checked" : ""%>/>
                                    </td>

                                    <td valign="middle" data-label="Gateway Name"
                                        align="center"><%--<input type=text maxlength="7" class="form-control" size=10 name='maxscoreautoreversal' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("maxScoreAutoReversal"))%>">--%>
                                        <%=functions.isValueNull(bankTypeVO.getBankName()) ? bankTypeVO.getBankName() : ""%>
                                    </td>

                                </tr>
                                </tbody>
                                <%
                                    }
                                %>


                            </table>


                            <table align="center" id="smalltable" border="0"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 50%;">

                                <tr style="background-color: transparent;">
                                    <td align="center" colspan="2">
                                        <button type="submit" class="btn btn-default" name="action" value="1_Update">
                                            <%=defaultGatewaySetting_Update%>
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </center>
                    </div>
                </div>
            </form>
        </div>


        <br>
            <%
        }
        else if(partnerMappedBank == null)
        {
         out.println("<div class=\"col-md-12\">\n" +
        "    <div class=\"widget\">\n" +
        "      <div class=\"widget-header\">\n" +
        "        <h2><i class=\"fa fa-table\"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>\n" +
        "        <div class=\"additional-btn\">\n" +
        "          <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
        "          <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
        "          <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
        "        </div>\n" +
        "      </div>\n" +
        "      <div class=\"widget-content padding\">");
       out.println(Functions.NewShowConfirmation1(defaultGatewaySetting_Sorry,defaultGatewaySetting_no)); out.println("</div>");
        out.println("</div>\n" +
        "  </div>");
        }
        else
        {
        //out.println(Functions.NewShowConfirmation1("Filter","Please Fill the Data for Merchant Bank Mapping Details"));


        out.println("<div class=\"col-md-12\">\n" +
        "    <div class=\"widget\">\n" +
        "      <div class=\"widget-header\">\n" +
        "        <h2><i class=\"fa fa-table\"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>\n" +
        "        <div class=\"additional-btn\">\n" +
        "          <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
        "          <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
        "          <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
        "        </div>\n" +
        "      </div>\n" +
        "      <div class=\"widget-content padding\">");
        out.println(Functions.NewShowConfirmation1(defaultGatewaySetting_Filter,defaultGatewaySetting_please));
        out.println("</div>");
        out.println("</div>\n" +
        "  </div>");


        }
        }
        catch (Exception e)
        {
        logger.error("Exception---" + e);
        }
      %>
            <%!
        public static String nullToStr(String str)
        {
        if(str == null)
        return "";
        return str;
        }
      %>
</body>
</html>
