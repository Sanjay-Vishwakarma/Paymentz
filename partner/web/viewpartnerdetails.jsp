<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.dao.GatewayAccountDAO" %>
<%@ page import="com.manager.dao.GatewayTypeDAO" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 5/8/14
  Time: 3:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%!
    private static Logger logger = new Logger("viewmerchantdetails.jsp");
%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    String partnerid=((String)session.getAttribute("merchantid"));
    Functions functions = new Functions();
%>
<html>
<head>
    <title><%=company%> Partner> Profile</title>
    <link href="/agent/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

    <style type="text/css">
        @media (min-width: 641px) {
            #mytabletr td:first-child {
                width: 50%;
            }
        }

        table.table{
            margin-top: 0 !important;
        }

    </style>


</head>
<body class="bodybackground">

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <%
                ResourceBundle rb1 = null;
                String language_property1 = (String) session.getAttribute("language_property");
                rb1 = LoadProperties.getProperty(language_property1);
                String viewpartnerdetails_Profile_Info = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Profile_Info")) ? rb1.getString("viewpartnerdetails_Profile_Info") : "Profile Info";
                String viewpartnerdetails_PartnerID = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_PartnerID")) ? rb1.getString("viewpartnerdetails_PartnerID") : "Partner ID:";
                String viewpartnerdetails_Partner_Name = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Partner_Name")) ? rb1.getString("viewpartnerdetails_Partner_Name") : "Partner Name:";
                String viewpartnerdetails_Contact_Person = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Contact_Person")) ? rb1.getString("viewpartnerdetails_Contact_Person") : "Contact Person:";
                String viewpartnerdetails_Country = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Country")) ? rb1.getString("viewpartnerdetails_Country") : "Country:";
                String viewpartnerdetails_Validate_Email = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Validate_Email")) ? rb1.getString("viewpartnerdetails_Validate_Email") : "Is Validate Email:";
                String viewpartnerdetails_Is_IpWhiteListed = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Is_IpWhiteListed")) ? rb1.getString("viewpartnerdetails_Is_IpWhiteListed") : "Is IpWhiteListed:";
                String viewpartnerdetails_Configured_Emails = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Configured_Emails")) ? rb1.getString("viewpartnerdetails_Configured_Emails") : "Configured Emails";
                String viewpartnerdetails_Type_MailID = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Type_MailID")) ? rb1.getString("viewpartnerdetails_Type_MailID") : "Type Of Mail ID";
                String viewpartnerdetails_Contact_Person_Name = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Contact_Person_Name")) ? rb1.getString("viewpartnerdetails_Contact_Person_Name") : "Contact Person Name";
                String viewpartnerdetails_Contact_Person_MailID = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Contact_Person_MailID")) ? rb1.getString("viewpartnerdetails_Contact_Person_MailID") : "Contact Person Mail ID";
                String viewpartnerdetails_Active_Merchants = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Active_Merchants")) ? rb1.getString("viewpartnerdetails_Active_Merchants") : "Active Merchants";
                String viewpartnerdetails_MerchantID = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_MerchantID")) ? rb1.getString("viewpartnerdetails_MerchantID") : "Merchant ID";
                String viewpartnerdetails_Contact_Person1 = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Contact_Person1")) ? rb1.getString("viewpartnerdetails_Contact_Person1") : "Contact Person";
                String viewpartnerdetails_Contact_MailID = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Contact_MailID")) ? rb1.getString("viewpartnerdetails_Contact_MailID") : "Contact Mail ID";
                String viewpartnerdetails_Company_Name = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Company_Name")) ? rb1.getString("viewpartnerdetails_Company_Name") : "Company Name";
                String viewpartnerdetails_Configuration = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Configuration")) ? rb1.getString("viewpartnerdetails_Configuration") : "Configuration";
                String viewpartnerdetails_Total_Banks = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Total_Banks")) ? rb1.getString("viewpartnerdetails_Total_Banks") : "Total Banks:";
                String viewpartnerdetails_Active_Bank_Accounts = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Active_Bank_Accounts")) ? rb1.getString("viewpartnerdetails_Active_Bank_Accounts") : "Active Bank Accounts:";
                String viewpartnerdetails_ActiveMerchants = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_ActiveMerchants")) ? rb1.getString("viewpartnerdetails_ActiveMerchants") : "Active Merchants:";
                String viewpartnerdetails_Active_Terminals = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Active_Terminals")) ? rb1.getString("viewpartnerdetails_Active_Terminals") : "Active Terminals:";
                String viewpartnerdetails_Sorry = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Sorry")) ? rb1.getString("viewpartnerdetails_Sorry") : "Sorry";
                String viewpartnerdetails_no = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_no")) ? rb1.getString("viewpartnerdetails_no") : "No Records Found";
                String viewpartnerdetails_Support_MailID = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Support_MailID")) ? rb1.getString("viewpartnerdetails_Support_MailID") : "Support Mail ID:";
                String viewpartnerdetails_Admin_MailID = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Admin_MailID")) ? rb1.getString("viewpartnerdetails_Admin_MailID") : "Admin Mail ID:";
                String viewpartnerdetails_Main_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Main_Contact")) ? rb1.getString("viewpartnerdetails_Main_Contact") : "Main Contact :";
                String viewpartnerdetails_Sales_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Sales_Contact")) ? rb1.getString("viewpartnerdetails_Sales_Contact") : "Sales Contact:";
                String viewpartnerdetails_Billing_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Billing_Contact")) ? rb1.getString("viewpartnerdetails_Billing_Contact") : "Billing Contact:";
                String viewpartnerdetails_Notify_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Notify_Contact")) ? rb1.getString("viewpartnerdetails_Notify_Contact") : "Notify Contact:";
                String viewpartnerdetails_Fraud_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Fraud_Contact")) ? rb1.getString("viewpartnerdetails_Fraud_Contact") : "Fraud Contact:";
                String viewpartnerdetails_Chargeback_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Chargeback_Contact")) ? rb1.getString("viewpartnerdetails_Chargeback_Contact") : "Chargeback Contact:";
                String viewpartnerdetails_Refund_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Refund_Contact")) ? rb1.getString("viewpartnerdetails_Refund_Contact") : "Refund Contact:";
                String viewpartnerdetails_Techical_Contact = StringUtils.isNotEmpty(rb1.getString("viewpartnerdetails_Techical_Contact")) ? rb1.getString("viewpartnerdetails_Techical_Contact") : "Technical Contact:";
                MerchantDAO merchant = new MerchantDAO();
                GatewayTypeDAO gatewayTypeDAO = new GatewayTypeDAO();
                GatewayAccountDAO gatewayAccountDAO =new GatewayAccountDAO();
                int partnerProcessingBanks =gatewayTypeDAO.getPartnerProcessingBanks(partnerid);
                int partnerBankAccounts =gatewayAccountDAO.getPartnerBankAccounts(partnerid);
                PartnerDetailsVO partnerDetailsVOs=(PartnerDetailsVO)request.getAttribute("partnerDetailsVOs");
                if(partnerDetailsVOs!=null)
                {

            %>


            <div class="row reporttable">

                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<strong><%=company%> <%=viewpartnerdetails_Profile_Info%></strong></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <div id="horizontal-form">                        <%-- End Radio Button--%>
                                <div class="form-group col-md-12 has-feedback">
                                    <div class="table-responsive">

                                        <%-- <table align="center" width="90%" border="1">--%>
                                        <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_PartnerID%></td>
                                                <td align="center">
                                                    <%=partnerDetailsVOs.getPartnerId()%>
                                                </td>
                                            </tr>


                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Partner_Name%></td>

                                                <td align="center">
                                                    <%=partnerDetailsVOs.getPartnerName()%>
                                                </td>
                                            </tr>


                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Contact_Person%></td>

                                                <td align="center">
                                                    <%=partnerDetailsVOs.getContactPerson()%>
                                                </td>
                                            </tr>


                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Country%></td>
                                                <td align="center">
                                                    <%=partnerDetailsVOs.getCountry()%>
                                                </td>
                                            </tr>


                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Validate_Email%></td>

                                                <td align="center">
                                                    <%=partnerDetailsVOs.getValidateEmail()%>
                                                </td>
                                            </tr>


                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Is_IpWhiteListed%></td>

                                                <td align="center">
                                                    <%=partnerDetailsVOs.getIpWhitelisted()%>
                                                </td>
                                            </tr>


                                        </table>

                                    </div>
                                </div>
                                <%--<div class="form-group col-md-12 has-feedback">
                                  <center>
                                    <label >&nbsp;</label>
                                    <input type="hidden" value="1" name="step">
                                    <button type="submit" class="btn btn-default" id="submit" disabled="disabled" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
                                  </center>
                                </div>--%>
                            </div>
                        </div>
                    </div>
                </div>
            </div>


            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=company%> <%=viewpartnerdetails_Configured_Emails%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">

                            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=viewpartnerdetails_Type_MailID%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=viewpartnerdetails_Contact_Person_Name%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=viewpartnerdetails_Contact_Person_MailID%></b></td>

                                </tr>
                                </thead>

                                <tr>
                                    <td valign="middle" align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Support_MailID%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name">-</td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getSupportMailId()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle"  align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Admin_MailID%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name">-</td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getAdminMailId()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle" align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Main_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getContactPerson())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getContact_emails()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle"  align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Sales_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getSalesContactName())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getSalesMailId()))%></td>
                                </tr>
                                <tr >

                                    <td valign="middle" align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Billing_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getBillingContactName())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getBillingMailId()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle"  align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Notify_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getNotifyContactName())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getNotifyEmailId()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle"  align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Fraud_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getFraudContactName())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getFraudemailid()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle"  align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Chargeback_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getChargebackContactName())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getChargebackMailId()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle"  align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Refund_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getRefundContactName())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getRefundMailId()))%></td>
                                </tr>
                                <tr >
                                    <td valign="middle"  align="center" data-label="Type Of MailId"><%=viewpartnerdetails_Techical_Contact%> </td>
                                    <td valign="middle" align="center" data-label="Contact Person Name"><%=Functions.checkValue(partnerDetailsVOs.getTechContactName())%></td>
                                    <td valign="middle" align="center" data-label="Contact Person Mail ID"><%=functions.getEmailMasking(Functions.checkValue(partnerDetailsVOs.getTechMailId()))%></td>
                                </tr>
                            </table>


                        </div>
                    </div>
                </div>
            </div>


            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=company%> <%=viewpartnerdetails_Active_Merchants%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">

                            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=viewpartnerdetails_MerchantID%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=viewpartnerdetails_Contact_Person1%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=viewpartnerdetails_Contact_MailID%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=viewpartnerdetails_Company_Name%></b></td>

                                </tr>
                                </thead>

                                <%
                                    String style="class=td1";
                                    String ext="light";

                                    Set<MerchantDetailsVO> merchantDetailsVOs =   merchant.getPartnerMembers(partnerid);
                                    for(MerchantDetailsVO merchantDetailsVO:merchantDetailsVOs)
                                    {
                                        int pos=1;
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
                                        out.println("<tr>");
                                        out.println("<td valign=\"middle\" data-label=\"Merchant ID\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(merchantDetailsVO.getMemberId())+"</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Contact Person\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(merchantDetailsVO.getContact_persons())+"</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Contact Mail ID\" align=\"center\""+style+">&nbsp;"+functions.getEmailMasking(merchantDetailsVO.getContact_emails())+"</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Company Name\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(merchantDetailsVO.getCompany_name())+"</td>");
                                        out.println("</tr>");
                                        pos ++;
                                    }
                                %>

                            </table>


                        </div>
                    </div>
                </div>
            </div>


            <div class="row reporttable">

                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><i class="fa fa-th-large"></i>&nbsp;&nbsp;<strong><%=company%> <%=viewpartnerdetails_Configuration%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <div id="horizontal-form">                        <%-- End Radio Button--%>
                                <div class="form-group col-md-12 has-feedback">
                                    <div class="table-responsive">

                                        <%-- <table align="center" width="90%" border="1">--%>
                                        <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Total_Banks%></td>
                                                <td align="center">
                                                    <%=partnerProcessingBanks%>
                                                </td>
                                            </tr>


                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Active_Bank_Accounts%></td>

                                                <td align="center">
                                                    <%=partnerBankAccounts%>
                                                </td>
                                            </tr>


                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_ActiveMerchants%></td>

                                                <td align="center">
                                                    <%=merchantDetailsVOs.size()%>
                                                </td>
                                            </tr>

                                            <%
                                                List<TerminalVO> terminalVOs=new TerminalManager().getActiveTerminalsByPartnerId(partnerid);
                                            %>

                                            <tr id="mytabletr">
                                                <td align="left" style="background-color: #7eccad !important;color: white;"><%=viewpartnerdetails_Active_Terminals%></td>
                                                <td align="center">
                                                    <%=terminalVOs.size()%>
                                                </td>
                                            </tr>

                                        </table>

                                    </div>
                                </div>
                                <%--<div class="form-group col-md-12 has-feedback">
                                  <center>
                                    <label >&nbsp;</label>
                                    <input type="hidden" value="1" name="step">
                                    <button type="submit" class="btn btn-default" id="submit" disabled="disabled" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
                                  </center>
                                </div>--%>
                            </div>
                        </div>
                    </div>
                </div>
            </div>




        </div>

    </div>

    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation1(viewpartnerdetails_Sorry, viewpartnerdetails_no));
        }
    %>


</div>

</body>
</html>