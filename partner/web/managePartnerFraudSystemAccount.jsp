<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="functions.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: NAMRATA B. BARI
  Date: 21/01/2020
  Time: 12:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp" %>
<html>
<head>
    <title>Add New Fraud System Account</title>
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
    <script>
        $(document).ready(function ()
        {
            var w = $(window).width();

            //alert(w);

            if (w > 990)
            {
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else
            {
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>
</head>
<body>

<%!
    Logger logger = new Logger("test.jsp");
    private ApplicationManager applicationManager = new ApplicationManager();
    private Functions functions = new Functions();
%>
<%
    if (partner.isLoggedInPartner(session))
    {
        logger.debug("Fraud System Account Master");
        session.setAttribute("submit", "Fraud System Account Master");
        String partnerid = Functions.checkStringNull(request.getParameter("partnerid")) == null ? "" : request.getParameter("partnerid");
        String fsAccount = Functions.checkStringNull(request.getParameter("fsAccount")) == null ? "" : request.getParameter("fsAccount");
        String isActive = Functions.checkStringNull(request.getParameter("isActive")) == null ? "" : request.getParameter("isActive");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String managePartnerFraudSystemAccount_Allocate_Fraud = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Allocate_Fraud")) ? rb1.getString("managePartnerFraudSystemAccount_Allocate_Fraud") : "Allocate Fraud  System Account";
        String managePartnerFraudSystemAccount_Partner = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Partner")) ? rb1.getString("managePartnerFraudSystemAccount_Partner") : "Partner*";
        String managePartnerFraudSystemAccount_Fraud_system = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Fraud_system")) ? rb1.getString("managePartnerFraudSystemAccount_Fraud_system") : "Fraud System Account Id*";
        String managePartnerFraudSystemAccount_Select_PartnerId = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Select_PartnerId")) ? rb1.getString("managePartnerFraudSystemAccount_Select_PartnerId") : "Select Partner Id";
        String managePartnerFraudSystemAccount_Select_fraud_system = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Select_fraud_system")) ? rb1.getString("managePartnerFraudSystemAccount_Select_fraud_system") : "Select Fraud System Account Id";
        String managePartnerFraudSystemAccount_isActive = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_isActive")) ? rb1.getString("managePartnerFraudSystemAccount_isActive") : "isActive*";
        String managePartnerFraudSystemAccount_N = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_N")) ? rb1.getString("managePartnerFraudSystemAccount_N") : "N";
        String managePartnerFraudSystemAccount_Y = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Y")) ? rb1.getString("managePartnerFraudSystemAccount_Y") : "Y";
        String managePartnerFraudSystemAccount_Add = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Add")) ? rb1.getString("managePartnerFraudSystemAccount_Add") : "Add";
        String managePartnerFraudSystemAccount_Account_List = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Account_List")) ? rb1.getString("managePartnerFraudSystemAccount_Account_List") : "Account List";
        String managePartnerFraudSystemAccount_Add_new = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Add_new")) ? rb1.getString("managePartnerFraudSystemAccount_Add_new") : "Add new Account";
        String managePartnerFraudSystemAccount_Allocated_Account = StringUtils.isNotEmpty(rb1.getString("managePartnerFraudSystemAccount_Allocated_Account")) ? rb1.getString("managePartnerFraudSystemAccount_Allocated_Account") : "Allocated Account List ";

%>

<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                                style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
                            <%=managePartnerFraudSystemAccount_Account_List%>&nbsp; <i class="fa fa-sign-in" style="font-size: small; color: white;"></i>
                        </button>
                    </form>
                </div>
            </div>

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/manageFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                                style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
                            <%=managePartnerFraudSystemAccount_Add_new%> &nbsp; <i class="fa fa-sign-in" style="font-size: small; color: white;"></i>
                        </button>
                    </form>
                </div>
            </div>
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/managePartnerAllocatedAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                                 style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
                            <%=managePartnerFraudSystemAccount_Allocated_Account%>&nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
                        </button>
                    </form>
                </div>
            </div>

            <br><br>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=managePartnerFraudSystemAccount_Allocate_Fraud%></strong></h2>
                        </div>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>

                        <form action="/partner/net/ManagePartnerFraudSystemAccount?ctoken=<%=ctoken%>" method="post"
                              name="form1" id="form1" class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                <%
                                    java.util.TreeMap<String,String> partneriddetails =null;
                                    partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
                                    String message = (String)request.getAttribute("statusMsg");
                                    String error = (String)request.getAttribute("error");
                                    Functions functions=new Functions();
                                     if(functions.isValueNull(message)){
                                     out.println(Functions.NewShowConfirmation1("Result",message));
                                     }else if(functions.isValueNull(error)){
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                     }
                               %>
                            <div class="widget-content padding">
                                <div class="form-group">
                                    <label class="col-md-4 control-label"><%=managePartnerFraudSystemAccount_Partner%><br>
                                    </label>

                                    <div class="col-md-4 ui-widget">
                                        <select class="form-control" name="partnerid">
                                            <option value="" default><%=managePartnerFraudSystemAccount_Select_PartnerId%></option>
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
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-4 control-label"><%=managePartnerFraudSystemAccount_Fraud_system%><br>
                                    </label>

                                    <div class="col-md-4 ui-widget">
                                        <select class="form-control" name="fsAccount">
                                            <option value="" default><%=managePartnerFraudSystemAccount_Select_fraud_system%></option>
                                            <%
                                                Connection conn = null;
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    ResultSet rs = Database.executeQuery("select fsaccountid,accountname,fsid from fraudsystem_account_mapping ", conn);
                                                    while (rs.next())
                                                    {
                                                        if (rs.getString("fsaccountid").equals(fsAccount))
                                                        {
                                                            out.println("<option value=" + rs.getString("fsaccountid") + " selected>" + rs.getString("fsaccountid") + " - " + rs.getString("accountname") + "(" + FraudSystemService.getFSGateway(rs.getString("fsid")) + ")" + "</option>");
                                                        }
                                                        else
                                                        {
                                                            out.println("<option value=" + rs.getString("fsaccountid") + ">" + rs.getString("fsaccountid") + " - " + rs.getString("accountname") + "(" + FraudSystemService.getFSGateway(rs.getString("fsid")) + ")" + "</option>");
                                                        }
                                                    }

                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception" + e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-4 control-label"><%=managePartnerFraudSystemAccount_isActive%><br>
                                    </label>

                                    <div class="col-md-4 ui-widget">
                                        <select name="isActive" class="form-control">
                                            <%
                                                if (isActive.equals("N"))
                                                {
                                            %>
                                            <option value="N"> <%=managePartnerFraudSystemAccount_N%></option>
                                            <option value="Y"> <%=managePartnerFraudSystemAccount_Y%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="Y"> <%=managePartnerFraudSystemAccount_Y%></option>
                                            <option value="N"> <%=managePartnerFraudSystemAccount_N%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                </div>

                                <div class="form-group col-md-12 has-feedback">
                                    <center>
                                        <label>&nbsp;</label>
                                        <input type="hidden" value="1" name="step">
                                        <button type="submit" class="btn btn-default" name="action"
                                                style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=managePartnerFraudSystemAccount_Add%>
                                        </button>
                                    </center>
                                </div>
                            </div>
                    </div>
                    </form>
                    <div>
                    </div>
                </div>
            </div>

            <%

            %>
        </div>
    </div>
</div>
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