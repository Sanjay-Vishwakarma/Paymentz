<%@ page import="com.manager.AdminModuleManager" %>
<%@ page import="com.manager.enums.AdminModuleEnum" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.Set" %>
<%@ include file="ietest.jsp" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type='text/javascript' src='/icici/css/menu_jquery.js'></script>
    <link rel="stylesheet" type="text/css" href="/icici/stylenew/styles123.css">
    <script src='/icici/css/umd/popper.js'></script>
    <script src='/icici/css/bootstrap.min.js'></script>
    <link href="/icici/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">
</head>

<style type="text/css">

    .ui-menu{
        position: relative;
        top: -197px;
        left: 460px;
        width: 200px!important;
        background-color: #ffffff;
        max-height: 200px;
        overflow: auto;
        padding: 3px 6px;

        color: #001962!important;
        font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif!important;
        font-size: 12px!important;
        font-weight: normal;
        border-radius: 4px;

    }

    .ui-menu-item:hover{
        background-color: dodgerblue;
        color: #ffffff;
    }


    .ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {
        border: none !important;
        background: dodgerblue !important;
        font-weight: normal !important;
        color: #ffffff!important;
    }

    .ui-menu .ui-menu-item a{
        display: inline!important;
    }

    .ui-widget-content a:focus{
        color: #ffffff!important;
    }

    .ui-widget-content a:hover{
        color: #ffffff!important;
    }

    .ui-widget-content a:active{
        color: #ffffff!important;
    }

    .ui-menu .ui-menu-item a:focus{
        color: #ffffff!important;
    }

    .ui-menu .ui-menu-item a:hover{
        color: #ffffff!important;
    }

    .ui-menu .ui-menu-item a:active{
        color: #ffffff!important;
    }

    .ui-helper-hidden-accessible{
        display: none;
    }


</style>

<body>
<%
    User user       =  (User)session.getAttribute("ESAPIUserSessionKey");
    String ctoken   = null;
    if(user != null)
    {
        ctoken = user.getCSRFToken();
    }
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        AdminModuleManager adminModuleManager   = new AdminModuleManager();
        String adminId                          = (String)session.getAttribute("merchantid");
        Set<String> moduleSet                   = adminModuleManager.getAdminAccessModuleSet(adminId);
%>
<div class="toppanel">
    <table class="toppaneltable"  >
        <tr>
            <td width="5%" bgcolor="#ffffff">
                <img src="/icici/images/paylogo.png">
            </td>
            <td width="90%" >
            </td>
            <td width="13%"  >
            </td>
            <td width="10%" >
                <div class="btn-group">
                    <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" >
                        <img src="/merchant/images/user-icon.png" >
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" role="menu">
                        <div style="padding-left:7px; ">
                            <table width="100" height="20" border="0" cellspacing="5" cellpadding="2" align="center">
                                <tr>
                                    <td width="30" align="center" style="background:#2c3e50 ">
                                        <button name="b" type="submit" value="default Skin" onclick="changeBG(0)" style="background-color:#2c3e50;width:30px;height:20px;border: 0px"  title="default Skin">
                                        </button>
                                    </td>
                                    <td>
                                        &nbsp;
                                    </td>
                                    <td width="30" align="center" style="background:#797979 ">
                                        <button type="submit" name="radiobutton" onclick="changeBG(1)" style="background-color:#797979;width:30px;height:20px;border: 0px"  value="Blue Skin" title="Blue Skin">
                                        </button>
                                    </td>
                                    <td>
                                        &nbsp;
                                    </td>
                                    <td width="30" align="center" style="background:#98bac8">
                                        <button name="radiobutton" type="submit" onclick="changeBG(3)" style="background-color:#98bac8;width:30px;height:20px;border:0px"  value="Grey Skin" title="Light Grey Skin">
                                        </button>
                                    </td>
                                    <td>
                                        &nbsp;
                                    </td>
                                    <td width="30" align="center" style="background:#808080 ">
                                        <button name="radiobutton" type="submit" onclick="changeBG(2)" style="background-color:#808080;width:30px;height:20px;border:0px"  value="Grey Skin" title="Grey Skin">
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <li class="divider"></li>
                        <li><form action="/icici/logout.jsp?ctoken=<%=ctoken%>" method="POST" class="menufont" style="background-color:#ffffff; ">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            <button type="submit" name="submit" class="logout"  style="width: 120px" >
                                <i class="fa fa-power-off"></i>
                                &nbsp;&nbsp;Logout
                            </button>
                        </form>
                        </li>
                    </ul>
                </div>
            </td>
        </tr>
    </table>
</div>
<div id='cssmenu' style="margin-top: 60px;">
    <ul>
        <%
            if(moduleSet.contains(AdminModuleEnum.MERCHANT_MANAGEMENT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-users"></i>&nbsp;&nbsp;Merchants Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/servlet/MerchantDetails?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"  name="submit" class="buttoncssmenusub">
                            Merchant Master
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/memberpreference.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                            Merchant's Configuration
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/invoice.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px " >
                            Merchant Invoice
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/wirelist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Merchant Wire Manager
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/listMerchantAgent.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub"style="margin-top: -10px" >
                            Merchant Agent Mapping
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/memberChildList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"  name="submit" class="buttoncssmenusub"style="margin-top: -10px">
                            Merchant's User Management
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/merchantintimationmail.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Merchant Intimation Mail
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/monitoringParameterMaster.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Risk Rule Master
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/riskrulemapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Risk Rule Mapping
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/monitoringRuleLog.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Monitoring Rule Change Log
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/riskRuleGraph.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Risk Rule Graph
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.BANK_DETAILS.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-dollar"></i>&nbsp;&nbsp;&nbsp;&nbsp;<%--Bank Details--%>Gateway Account Details<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px" >
                <li>
                    <form action="/icici/qwipichecklist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit">
                            QWIPI
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/ecorechecklist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                            ECORE
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/gatewayAccountInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                            GateWay Account Master
                        </button>
                    </form>
                </li>
                <li class='has-sub'>
                    <button type="submit" class="buttoncssmenusub" style="margin-top:-10px " >
                        Bank Description Manager<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                    </button>
                    <ul style="height:12px ">
                        <li>
                            <form action="/icici/bankRollingReserveList.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub"name="submit" style="margin-top:-10px " >
                                    Bank RollingReserveMaster
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/bankMerchantSettlementMaster.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Bank MerchantSettlementMaster
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/bankWireManager.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Bank WireManager
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/isocommwiremanagerlist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    ISO Commission Report
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/addsettlementcycle.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Settlement Cycle Master
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li class='has-sub'>
                    <button type="submit" class="buttoncssmenusub" >
                        Common Integration<span class="fa fa-caret-right" style="float:right;margin-right:10px"></span>
                    </button>
                    <ul style="height:12px ;margin-left: 2px;">
                        <li>
                            <form action="/icici/commonrefundlist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub"name="submit" >
                                    Common Refund
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/commonchargeback.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub"name="submit" style="margin-top:-10px " >
                                    Common Chargeback
                                </button>
                            </form>
                        </li>
                        <li class='has-sub'>
                            <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                                Common Fraud<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                            </button>
                            <ul style="height:12px;margin-left: 3px; ">
                                <li>
                                    <form action="/icici/markFraudTransaction.jsp?ctoken=<%=ctoken%>" method="POST">
                                        <button type="submit" name="submit" class="buttoncssmenusub" style="margin-top:-10px " >
                                            Fraud Upload
                                        </button>
                                    </form>
                                </li>
                                <li>
                                    <form action="/icici/fraudNotification.jsp?ctoken=<%=ctoken%>" method="POST">
                                        <button type="submit" name="submit" class="buttoncssmenusub" style="margin-top:-10px " >
                                            Fraud Intimation & Action
                                        </button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <form action="/icici/commoninquirylist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="submit">
                                    Common Inquiry
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/commonpayout.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Common Payouts
                                </button>
                            </form>
                        </li>
                        <%--<li>
                            <form action="/icici/commonactionhistory.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Common Action History
                                </button>
                            </form>
                        </li>--%>
                        <li>
                            <form action="/icici/commonsettlecron.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Common Settlement Cron
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/commonreconlist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Common Manual Reconcilation
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/commonManualSettlement.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                                    Common Manual Settlement
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li>
                    <form action="/icici/commonChargebackUplaod.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit">
                            Bulk Chargeback Upload
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/bulkRefundUpload.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit"  style="margin-top:-10px">
                            Upload Refund Chargeback
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/tc40Upload.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                            Bulk TC40 Upload
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.SETTINGS.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-wrench"></i>&nbsp;&nbsp;&nbsp;Settings<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/partnerTheme.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" >
                            Partner Theme
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/chngpassword.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Change Password
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/listCrons.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Cron List
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/servlet/MemberMappingDetails?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Commercials & Limits
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/payoutLimitAmount.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Payout Amount Limits
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/compliance.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px " >
                            Compliance
                        </button>
                    </form>
                </li>
                <li class='has-sub'>
                    <button type="submit" class="buttoncssmenusub" style="margin-top:-10px">
                        Whitelist Module<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                    </button>
                    <ul style="height:12px">
                        <li>
                            <form action="/icici/whitelistdetails.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px " >
                                    White list Module
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/ipwhitelistconfig.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px " >
                                    IP Whitelist Configuration
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/uploadwhitelistemaildetails.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px " >
                                    Whitelist Email
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/whitelistBin.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px " >
                                    Whitelist Bin
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/whitelistBinCountry.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px " >
                                    Whitelist Bin Country
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
               <%-- <li class='has-sub'>
                    <button type="submit" class="buttoncssmenusub" >
                        2. Blacklist Module<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                    </button>
                </li>--%>
                <li>
                    <form action="/icici/statusSynchronization.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px height: 12px;" >
                            Status Synchronization
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/gatewayInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub"name="submit" style="margin-top:-10px " >
                            GateWay Master
                        </button>
                    </form>
                </li>
                <li class='has-sub'>
                    <button type="submit" class="buttoncssmenusub" style="margin-top:-10px">
                        Account History<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                    </button>
                    <ul style="height:12px ">
                        <li>
                            <form action="/icici/mailmappinglist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" >
                                    Mail Configuration
                                </button>
                            </form>
                        </li>
                        <li class='has-sub'>
                            <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                                Rolling Reserve Manager<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                            </button>
                            <ul style="height:12px ">
                                <li>
                                    <form action="/icici/generateRollingReserveFile.jsp?ctoken=<%=ctoken%>" method="POST">
                                        <button type="submit" value="Generate Rolling Reserve Release File" name="submit" class="buttoncssmenusub" style="margin-top:-10px " >
                                            Generate Rolling Reserve Release
                                        </button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <form action="/icici/servlet/BlockedMerchantList?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="buttoncssmenusub">
                                    UnBlock Merchant Account
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/servlet/BlockedUserList?ctoken=<%=ctoken%>" method="POST" style="margin-top:-10px ">
                                <button type="submit" class="buttoncssmenusub">
                                    UnBlock Account
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li>
                    <button type="submit"class="buttoncssmenusub" <%--style="margin-top:-10px "--%>>
                        Charges Interface<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                    </button>
                    <ul style="height:12px " >
                        <li>
                            <form action="/icici/listChargeMaster.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                                    Charge Master
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/listGatewayAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="buttoncssmenusub" value="Gateway Accounts Charges" name="submit"style="margin-top:-10px " >
                                    Gateway Accounts Charges
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/listMemberAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" value="Member Accounts Charges" name="submit" style="margin-top:-10px ">
                                    Member Accounts Charges
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/listMerchantRandomCharges.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                                    Merchant Random Charges
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/listPartnerCommission.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                                    Partner Commission
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/listAgentCommission.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                                    Agent Commission
                                </button>
                            </form>
                        </li>

                        <li>
                            <form action="/icici/wlpartnercommmappinglist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                                    White Label Partner Commission
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li>
                    <form action="/icici/listExchangeRates.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" name="submit" style="margin-top:-10px height: 12px;" >
                            Exchange Rates
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.REVERSE_REPORT.name()))
            {
        %>
        <li >
            <form action="/icici/reverseReport.jsp?ctoken=<%=ctoken%>" method="POST" style="margin: 0">
                <button type="submit" class="buttoncssmenu">
                    <i class="fa fa-wrench"></i>&nbsp;&nbsp;&nbsp;Reverse Report
                </button>
            </form>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.ACTIVITY_TRACKER.name()))
            {
        %>
        <li >
            <form action="/icici/ActivityTracker.jsp?ctoken=<%=ctoken%>" method="POST" style="margin: 0">
                <button type="submit" class="buttoncssmenu">
                    <i class="fa fa-wrench"></i>&nbsp;&nbsp;&nbsp;Activity Tracker
                </button>
            </form>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.PARTNER_MANAGEMENT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-user"></i>&nbsp;&nbsp;&nbsp;Partners Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/partnerInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" >
                            Partner Master
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/partnerWireList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub"style="margin-top: -10px" >
                            Partner Wire Manager
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/partnerpreference.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top: -10px">
                            Partner Default Configuration
                        </button>
                    </form>
                </li>

                <li>
                    <form action="/icici/partnerChildList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top: -10px">
                            Partner's User Management
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.AGENT_MANAGEMENT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-user"></i>&nbsp;&nbsp;&nbsp;Agents Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/agentInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" >
                            Agent Master
                        </button>
                    </form>
                </li>

                <li>
                    <form action="/icici/agentWireList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub"style="margin-top: -10px" >
                            Agent Wire Manager
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.CUSTOMER_SUPPORT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-headphones"></i>&nbsp;&nbsp;&nbsp;Customer Support<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/cseManager.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" >
                            Support Ext Master
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/shippingdetails.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Shipping Details
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.TRANSACTION_MANAGEMENT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-rupee"></i>&nbsp;&nbsp;&nbsp;Transaction Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/adminTransactionDetails.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub">
                            Transaction Detail
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/transactions.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Transaction List
                        </button>
                    </form>
                </li>

                <li>
                    <form action="/icici/bdPayoutBatch.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                            BD Payout Batch
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/blockedCardList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" name="submit" style="margin-top:-10px ">
                            Block Transactions
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/rejectedTransactionsList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Rejected Transactions List
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/exportSFTPTransaction.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Export SFTP Transaction Detail
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/fraudAlertTransaction.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Fraud Alert Transactions
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/payoutTransactions.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Payout Transaction List
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/servlet/TransactionsLogs?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px ">
                            Transactions Log
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.APPLICATION_MANAGER.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-table"></i>&nbsp;&nbsp;&nbsp;Application Manager<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/bankMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Bank Mapping
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/contractualPartner.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Contractual Partner
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/listofapplicationmember.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Merchant Application Form
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/bankdetailslist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Merchant Application PDF
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/consolidatedapplication.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Merchant Consolidated Application
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/managebankApp.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Manage Application
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/consolidatedHistory.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Consolidated Application History
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.REPORTS.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu" >
                <i class="fa fa-table"></i>&nbsp;&nbsp;&nbsp;Reports<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/merchanttransmaillist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" >
                            Member Transaction Report
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/partnerTransactionReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub"  style="margin-top:-10px ">
                            Partner Transaction Report
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/bankTransactionReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Bank Transaction Report
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/businessDashboard.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Business Dashboard
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/listMerchantPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px " >
                            Merchant Payout Report
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/bankPartnerPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub"style="margin-top: -10px" >
                            Bank Partner Payout Report
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/partnerWirePayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub"style="margin-top: -10px" >
                            Merchant Partner Payout Report
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/agentPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub"style="margin-top: -10px" >
                            Merchant Agent Payout Report
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/wlPartnerInvoiceList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top: -10px">
                            White Label Partner Invoice
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.FRAUD_DETAILS.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-glass"></i>&nbsp;&nbsp;&nbsp;Fraud Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li class='has-sub'>
                    <button type="submit" class="buttoncssmenusub">
                        Fraud System Master Details<span class="fa fa-caret-right" style="float:right;margin-right:10px"></span>
                    </button>

                    <ul style="height:12px ">
                        <li>
                            <form action="/icici/listFraudSystem.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"  name="submit" class="buttoncssmenusub" >
                                    Fraud System Master
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/listMemberFraudSystem.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" name="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                                    Member Fraud System
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li class='has-sub'>
                    <button type="submit"class="buttoncssmenusub">
                        Fraud Transaction Details<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                    </button>
                    <ul style="height:12px ">
                        <li>
                            <form action="/icici/fraudTransactionList.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" name="submit"  class="buttoncssmenusub">
                                    Fraud Transaction List
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/fraudTransactionReverseList.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"  name="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                                    Reverse Fraud Transaction
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li class="has-sub">
                    <button type="submit"class="buttoncssmenusub">
                        Fraud Rules<span class="fa fa-caret-right" style="float:right;margin-right:10px "></span>
                    </button>
                    <ul style="height:12px ">
                        <li>
                            <form action="/icici/fraudRuleList.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="fsRuleMaster" >
                                    Fraud Rule Master
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/fraudSystemAccountRuleList.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="fsRuleMaster" style="margin-top:-10px ">
                                    Manage Account Rules
                                </button>
                            </form>
                        </li>
                        <li>
                            <form action="/icici/fraudSystemSubAccountRuleList.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"class="buttoncssmenusub" name="fsRuleMaster" style="margin-top:-10px ">
                                    Manage SubAccount Rules
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
                <li>
                    <form action="/icici/accountFraudRuleList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"  name="submit" class="buttoncssmenusub" >
                            Accounts Rule Setting
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" name="fsAccMapping" style="margin-top:-10px " >
                            FraudSystem Account Master
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/fraudSystemMerchantSubAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" name="merchantFsSubaccMapping" style="margin-top:-10px ">
                            Manage Merchant Fraud Account
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/fraudRuleChangeIntimationList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" name="fraudrulechangeintimation" style="margin-top:-10px ">
                            Fraud Rule Change Intimation
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
        %>
        <%
            if(moduleSet.contains(AdminModuleEnum.MODULE_MANAGEMENT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-briefcase"></i>&nbsp;&nbsp;&nbsp;Module Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/adminModuleList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="" >
                            Admin Module Master
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/adminModuleMappingList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px" >
                            Admin Module Mapping
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.ADMIN_MANAGEMENT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu">
                <i class="fa fa-user"></i>&nbsp;&nbsp;&nbsp;Admin Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <%--<li>
                    <form action="/icici/adminsignup.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="" >
                            Admin Master
                        </button>
                    </form>
                </li>--%>
                <li>
                    <form action="/icici/adminlist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit"class="buttoncssmenusub" style="margin-top:-10px " >
                            Admin List
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
            if(moduleSet.contains(AdminModuleEnum.INFLIGHT_RULE_MANAGEMENT.name()))
            {
        %>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu" >
                <i class="fa fa-table"></i>&nbsp;&nbsp;&nbsp;IFE Rule Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px ">
                <li>
                    <form action="/icici/riskRuleManagement.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" >
                            Risk Rule
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/businessRuleManagement.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="buttoncssmenusub" style="margin-top:-10px ">
                            Business Rule
                        </button>
                    </form>
                </li>
            </ul>
        </li>
        <%
            }
        %>
        <%
            if(moduleSet.contains(AdminModuleEnum.TOKEN_MANAGEMENT.name()))
            {
        %>
        <li>
        <li class='has-sub'>
            <button type="submit" class="buttoncssmenu" >
                <i class="fa fa-table"></i>&nbsp;&nbsp;&nbsp;Token Management<span class="fa fa-caret-right" style="float:right;"></span>
            </button>
            <ul style="height:12px">
                <li>
                    <form action="/icici/listOfMerchantRegistration.jsp?ctoken=<%=ctoken%>" method="POST" >
                        <button type="submit" value="Registration History" class="buttoncssmenusub" style="margin-top:-10px ">
                            Registration History
                        </button>
                    </form>
                </li>
                <li>
                    <form action="/icici/listmerchantregistercard.jsp?ctoken=<%=ctoken%>" method="POST" >
                        <button type="submit" value="Registration History" class="buttoncssmenusub" style="margin-top:-10px ">
                            Card Registration
                        </button>
                    </form>
                </li>
            </ul>
            <%
                }
            %>
        </li>
        <%
            if(moduleSet.contains(AdminModuleEnum.RECURRING_MODULE.name()))
            {
        %>
        <li >
            <form action="/icici/recurringModule.jsp?ctoken=<%=ctoken%>" method="POST" style="margin: 0">
                <button type="submit" class="buttoncssmenu">
                    <i class="fa fa-wrench"></i>&nbsp;&nbsp;&nbsp;Recurring Module
                </button>
            </form>
        </li>
        <%
            }
            }
        %>
    </ul>
</div>
</body>
</html>