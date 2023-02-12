<%@ page import="com.directi.pg.Database,com.directi.pg.Functions" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringKeyword" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringSubKeyword" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringUnit" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.*" %>
<%!
    private static Logger log = new Logger("merchantTerminalThreshold.jsp");
%>
<html>
<head>
    <style>
        .multiselect {
            width: 100px;
        }
        .selectBox {
            position: relative;
        }
        .selectBox select {
            width: 100%;
            font-weight: bold;
        }
        .overSelect {
            position: absolute;
            left: 0; right: 0; top: 0; bottom: 0;
        }
        .checkboxes {
            display: none;
            border: 1px #dadada solid;
        }
        .checkboxes label {
            display: block;
        }
        .checkboxes label:hover {
            background-color: #1e90ff;
        }
    </style>
</head>
<title> Merchants Management > Risk Rule Mapping </title>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<script>
    var expanded = false;
    function showCheckboxes(terminalId,mappingId) {
        var checkboxes = document.getElementById(terminalId+'_checkboxes_'+mappingId);
        if (!expanded) {
            checkboxes.style.display = "block";
            expanded = true;
        } else {
            checkboxes.style.display = "none";
            expanded = false;
        }
    }
</script>
<script>
    var expanded = false;
    function showCheckboxesPSP(terminalId,mappingId) {
        var checkboxes = document.getElementById(terminalId+'_checkboxespsp_'+mappingId);
        if (!expanded) {
            checkboxes.style.display = "block";
            expanded = true;
        } else {
            checkboxes.style.display = "none";
            expanded = false;
        }
    }
</script>
<script>
    var expanded = false;
    function showCheckboxesMerchant(terminalId,mappingId) {
        var checkboxes = document.getElementById(terminalId+'_checkboxesmerchant_'+mappingId);
        if (!expanded) {
            checkboxes.style.display = "block";
            expanded = true;
        } else {
            checkboxes.style.display = "none";
            expanded = false;
        }
    }
</script>
<script>
    var expanded = false;
    function showCheckboxesAgent(terminalId,mappingId) {
        var checkboxes = document.getElementById(terminalId+'_checkboxesagent_'+mappingId);
        if (!expanded) {
            checkboxes.style.display = "block";
            expanded = true;
        } else {
            checkboxes.style.display = "none";
            expanded = false;
        }
    }
</script>
<script type="text/javascript">
    function confirmsubmit2(i)
    {
        var checkboxes = document.getElementsByName("mappingid_" + i);
        var total_boxes = checkboxes.length;
        flag = false;

        for (i = 0; i < total_boxes; i++)
        {
            if (checkboxes[i].checked)
            {
                flag = true;
                break;
            }
        }
        if (!flag)
        {
            alert("select at least one rule");
            return false;
        }
        if (confirm("Do you really want to update all selected rule."))
        {
            document.getElementById("details" + i).submit();
        }
        else
        {
            return false;
        }
    }
    function doChangesForAlertActivation(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_alertActiovationValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_alertActiovationValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_alertActiovation_' + mappingId).value = document.getElementById(terminalId+'_alertActiovationValue_' + mappingId).value;
    }
    function doChangesForSuspensionActivation(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_suspensionActivationValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_suspensionActivationValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_suspensionActivation_' + mappingId).value = document.getElementById(terminalId+'_suspensionActivationValue_' + mappingId).value;
    }
    function doChangesForAlertToAdmin(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAdminValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAdminValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAdmin_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminValue_' + mappingId).value;
    }
    function doChangesForAlertToAdminSales(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAdminSalesValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAdminSalesValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAdminSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminSalesValue_' + mappingId).value;
    }
    function doChangesForAlertToAdminRF(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAdminRFValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAdminRFValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAdminRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminRFValue_' + mappingId).value;
    }
    function doChangesForAlertToAdminCB(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAdminCBValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAdminCBValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAdminCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminCBValue_' + mappingId).value;
    }
    function doChangesForAlertToAdminFraud(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAdminFraudValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAdminFraudValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAdminFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminFraudValue_' + mappingId).value;
    }
    function doChangesForAlertToAdminTech(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAdminTechValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAdminTechValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAdminTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAdminTechValue_' + mappingId).value;
    }
    function doChangesForAlertToPSP(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToPartnerValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToPartnerValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToPartner_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerValue_' + mappingId).value;
    }
    function doChangesForAlertToPartnerSales(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToPartnerSalesValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToPartnerSalesValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToPartnerSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerSalesValue_' + mappingId).value;
    }
    function doChangesForAlertToPartnerRF(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToPartnerRFValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToPartnerRFValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToPartnerRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerRFValue_' + mappingId).value;
    }
    function doChangesForAlertToPartnerCB(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToPartnerCBValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToPartnerCBValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToPartnerCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerCBValue_' + mappingId).value;
    }
    function doChangesForAlertToPartnerFraud(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToPartnerFraudValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToPartnerFraudValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToPartnerFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerFraudValue_' + mappingId).value;
    }
    function doChangesForAlertToPartnerTech(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToPartnerTechValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToPartnerTechValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToPartnerTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToPartnerTechValue_' + mappingId).value;
    }
    function doChangesForAlertToMerchant(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToMerchantValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToMerchantValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToMerchant_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantValue_' + mappingId).value;
    }
    function doChangesForAlertToMerchantSales(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToMerchantSalesValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToMerchantSalesValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToMerchantSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantSalesValue_' + mappingId).value;
    }
    function doChangesForAlertToMerchantRF(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToMerchantRFValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToMerchantRFValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToMerchantRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantRFValue_' + mappingId).value;
    }
    function doChangesForAlertToMerchantCB(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToMerchantCBValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToMerchantCBValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToMerchantCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantCBValue_' + mappingId).value;
    }
    function doChangesForAlertToMerchantFraud(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToMerchantFraudValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToMerchantFraudValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToMerchantFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantFraudValue_' + mappingId).value;
    }
    function doChangesForAlertToMerchantTech(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToMerchantTechValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToMerchantTechValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToMerchantTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToMerchantTechValue_' + mappingId).value;
    }
    function doChangesForAlertToAgent(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAgentValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAgentValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAgent_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentValue_' + mappingId).value;
    }
    function doChangesForAlertToAgentSales(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAgentSalesValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAgentSalesValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAgentSales_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentSalesValue_' + mappingId).value;
    }
    function doChangesForAlertToAgentRF(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAgentRFValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAgentRFValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAgentRF_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentRFValue_' + mappingId).value;
    }
    function doChangesForAlertToAgentCB(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAgentCBValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAgentCBValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAgentCB_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentCBValue_' + mappingId).value;
    }
    function doChangesForAlertToAgentFraud(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAgentFraudValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAgentFraudValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAgentFraud_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentFraudValue_' + mappingId).value;
    }
    function doChangesForAlertToAgentTech(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isAlertToAgentTechValue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isAlertToAgentTechValue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isAlertToAgentTech_' + mappingId).value = document.getElementById(terminalId+'_isAlertToAgentTechValue_' + mappingId).value;
    }
    function ToggleAll(checkbox, terminalid)
    {
        flag = checkbox.checked;
        var checkboxes = document.getElementsByName("mappingid_" + terminalid);
        var total_boxes = checkboxes.length;

        for (i = 0; i < total_boxes; i++)
        {
            checkboxes[i].checked = flag;
        }
    }
    function doChangeFrequency(data,mappingId,terminalId)
    {
        var value=confirm("Do you really want to change selected frequency?")
        if(value)
        {
            document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value=data.value;
        }
        else
        {
            data.value=document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value
        }
    }
    function showCheckboxesFrequency(terminalId,mappingId)
    {
        var checkboxes = document.getElementById(terminalId+'_checkboxesfrequency_'+mappingId);
        if(!expanded)
        {
            checkboxes.style.display = "block";
            expanded = true;
        }
        else
        {
            checkboxes.style.display = "none";
            expanded = false;
        }
    }
    function doChangesForDailyExecution(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isdailyexecution_' + mappingId).value = document.getElementById(terminalId+'_isdailyexecutionvalue_' + mappingId).value;
    }
    function doChangesForWeeklyExecution(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_isweeklyexecutionvalue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_isweeklyexecutionvalue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_isweeklyexecution_' + mappingId).value = document.getElementById(terminalId+'_isweeklyexecutionvalue_' + mappingId).value;
    }
    function doChangesForMonthlyExecution(data, mappingId,terminalId)
    {
        if (data.checked)
        {
            document.getElementById(terminalId+'_ismonthlyexecutionvalue_' + mappingId).value = "Y";
        }
        else
        {
            document.getElementById(terminalId+'_ismonthlyexecutionvalue_' + mappingId).value = "N";
        }
        var value=document.getElementById(terminalId+'_ismonthlyexecution_' + mappingId).value = document.getElementById(terminalId+'_ismonthlyexecutionvalue_' + mappingId).value;
    }
    function selectTerminals(data)
    {
        document.f1.submit();
    }
    function dailyEnableDisableTextBox(data,mappingId,terminalId)
    {
        if(data.checked)
        {
            document.getElementsByName(terminalId+"_alertThreshold_"+mappingId)[0].disabled = false;
            document.getElementsByName(terminalId+'_suspensionThreshold_'+mappingId)[0].disabled = false;
        }
        else
        {
            document.getElementsByName(terminalId+"_alertThreshold_"+mappingId)[0].disabled = true;
            document.getElementsByName(terminalId+'_suspensionThreshold_'+mappingId)[0].disabled = true;
        }
    }
    function weeklyEnableDisableTextBox(data,mappingId,terminalId)
    {
        if(data.checked)
        {
            document.getElementsByName(terminalId+"_weeklyAlertThreshold_"+mappingId)[0].disabled = false;
            document.getElementsByName(terminalId+'_weeklySuspensionThreshold_'+mappingId)[0].disabled = false;
        }
        else
        {
            document.getElementsByName(terminalId+"_weeklyAlertThreshold_"+mappingId)[0].disabled = true;
            document.getElementsByName(terminalId+'_weeklySuspensionThreshold_'+mappingId)[0].disabled = true;
        }
    }
    function monthlyEnableDisableTextBox(data,mappingId,terminalId)
    {
        if(data.checked)
        {
            document.getElementsByName(terminalId+"_monthlyAlertThreshold_"+mappingId)[0].disabled = false;
            document.getElementsByName(terminalId+'_monthlySuspensionThreshold_'+mappingId)[0].disabled = false;
        }
        else
        {
            document.getElementsByName(terminalId+"_monthlyAlertThreshold_"+mappingId)[0].disabled = true;
            document.getElementsByName(terminalId+'_monthlySuspensionThreshold_'+mappingId)[0].disabled = true;
        }
    }

</script>
<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Risk Rule Mapping
                <div style="float: right;">
                    <form action="/icici/manageMonitoringParameter.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Single Risk Rule Mapping
                        </button>
                    </form>
                </div>
            </div>
            <%
                Logger logger=new Logger("riskrulemapping.jsp");
                ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                if (com.directi.pg.Admin.isLoggedIn(session))
                {
                    String memberId = nullToStr(request.getParameter("memberid"));
                    String terminalid = nullToStr(request.getParameter("terminalid"));
            %>
            <form action="/icici/servlet/RiskRuleList?ctoken=<%=ctoken%>" method="get" name="f1"
                  onsubmit="">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <input type="hidden" value="riskrulemapping" name="requestedfilename">
                <br>
                <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>
                            <%
                                String errormsg1 = (String) request.getAttribute("error");
                                if (errormsg1 != null)
                                {
                                    out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                                }
                            %>
                            <%
                                String errormsg = (String) request.getAttribute("cbmessage");
                                if (errormsg == null)
                                    errormsg = "";
                                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                                out.println(errormsg);
                                out.println("</b></font></td></tr></table>");
                            %>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" for="midy">Member Id*</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb">
                                        <input name="memberid" id="midy" value="<%=memberId%>" class="txtbox" autocomplete="on">
                                        <%--<select name="memberid" class="txtbox" style="width: 162px" onchange="selectTerminals(this)">
                                            <option value="" selected></option>
                                            <%
                                                Connection conn=null;
                                                PreparedStatement pstmt = null;
                                                ResultSet rs = null;
                                                try
                                                {
                                                    //conn= Database.getConnection();
                                                    conn= Database.getRDBConnection();
                                                    String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                                                    pstmt = conn.prepareStatement(query);
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        String selection="";
                                                        if(rs.getString("memberid").equals(memberId))
                                                        {
                                                            selection="selected";
                                                        }
                                            %>
                                            <option value="<%=rs.getInt("memberid")%>" <%=selection%>><%=rs.getInt("memberid")+"-"+rs.getString("company_name")%></option>;
                                            <%
                                                    }
                                                }
                                                catch (SystemError se)
                                                {
                                                    logger.error("Exception:::::"+se);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" for="tid">Terminal Id*</td>
                                    <td width="5%" class="textb">
                                        <input name="terminalid" id="tid" value="<%=terminalid%>"  class="txtbox" autocomplete="on">
                                      <%--  <select name="terminalid" class="txtbox" style="width: 162px">
                                            <option value="" selected>All</option>
                                            <%
                                                try
                                                {
                                                    //conn = Database.getConnection();
                                                    conn = Database.getRDBConnection();
                                                    String query = "SELECT terminalid,paymodeid,cardtypeid,memberid FROM member_account_mapping where memberid='" + memberId + "'";
                                                    pstmt = conn.prepareStatement(query);
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getString("terminalid") + "\">" + rs.getString("memberid") + "-" + rs.getString("terminalid") + "-" + GatewayAccountService.getPaymentMode(rs.getString("paymodeid")) + "-" + GatewayAccountService.getCardType(rs.getString("cardtypeid")) + "</option>");
                                                    }
                                                }
                                                catch (SystemError se)
                                                {
                                                    logger.error("Exception:::::" + se);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                    <td width="50%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable" style="margin-bottom: 9px;">
    <%
        Map<String, List<MonitoringParameterMappingVO>> stringListMap = (Map) request.getAttribute("stringListMap");
        TerminalManager terminalManager = new TerminalManager();
        if (stringListMap != null && stringListMap.size() > 0)
        {
            Set set = stringListMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext())
            {
                String terminalIdString = (String) iterator.next();
                String[] terminalIdStringArr=terminalIdString.split(":");
                String terminalId=terminalIdStringArr[0];
                String settings=terminalIdStringArr[1];

                TerminalVO terminalVO = terminalManager.getActiveInActiveTerminalInfo(terminalId);
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
                String currency = gatewayAccount.getCurrency();
                String bank = gatewayAccount.getGateway();
                String cardType = terminalVO.getCardType();
                String toggleVisibility="";
                String toggleSelection="";

                if("default".equals(settings))
                {
                    toggleVisibility="disabled";
                    toggleSelection="checked";
                }
                List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = stringListMap.get(terminalIdString);
    %>
    <form name="update" id="details_<%=terminalId%>" action="/icici/servlet/RiskRuleMapping?ctoken=<%=ctoken%>"
          method=post>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type=hidden value="<%=terminalVO.getAccountId()%>" name="accountid">
        <input type=hidden value="<%=terminalVO.getTerminalId()%>" name="terminalid">
        <input type=hidden value="<%=terminalVO.getMemberId()%>" name="memberid">
        <input type=hidden value="UPDATE" name="action">
        <table border="0" width="100%" class="table table-striped table-bordered table-green dataTable"
               style="margin-bottom: 0px">
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0" colspan='16'><font size=2><%=bank%> - <%=currency%>
                    - <%=cardType%>
                </font></td>
                <td valign="middle" align="center" class="th0"
                    style="border-left-style: hidden"><%=terminalVO.toString()%>
                </td>
            </tr>
            </thead>
            <thead>
            <tr>
                <td class="textb" valign="middle" align="center" rowspan="2"><input type="checkbox"
                                                                                    onClick="ToggleAll(this,<%=terminalId%>);"
                                                                                    name="alltrans" <%=toggleSelection%>></td>
                <td  width="15%" class="textb" valign="middle" align="center" colspan="2"><b>Risk Rule Detail</b></td>
                <td width="2%" class="textb" valign="middle" align="center" rowspan="2"><b>Frequency</b></td>
                <td  width="17%" class="textb" valign="middle" align="center" colspan="2"><b>Alert Setting</b></td>
                <td  width="17%"  class="textb" valign="middle" align="center" colspan="2"><b>Suspension Setting</b></td>
                <td  width="40%" class="textb" valign="middle" align="center" colspan="8"><b>E-mail Notification</b></td>
                <td width="11%" class="textb" valign="middle" align="center" rowspan="2"><b>Alert Message</b></td>
            </tr>
            <tr>
                <td class="textb" valign="middle" align="center" rowspan="2"><b>Risk ID</b></td>
                <td class="textb" valign="middle" align="center" rowspan="2"><b>Risk Name</b></td>

                <td class="textb" valign="middle" align="center" rowspan="3" colspan="2"><b>Threshold<br>(DL,WL,ML)</b></td>

                <td class="textb" valign="middle" align="center" rowspan="3" colspan="2"><b>Threshold<br>(DL,WL,ML)</b></td>

                <td class="textb" valign="middle" align="center" colspan="2"><b>Admin</b></td>
                <td class="textb" valign="middle" align="center" colspan="2"><b>PSP</b></td>
                <td class="textb" valign="middle" align="center" colspan="2"><b>Merchant</b></td>
                <td class="textb" valign="middle" align="center" colspan="2"><b>Agent</b></td>
            </tr>
            </thead>
            <%
                for (MonitoringParameterMappingVO monitoringParameterMappingVO : monitoringParameterMappingVOs)
                {
                    MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
                    String alertActivation = "";
                    String suspensionActivation = "";
                    String isAlertToAdmin = "";
                    String isAlertToAdminSales = "";
                    String isAlertToAdminRF = "";
                    String isAlertToAdminCB = "";
                    String isAlertToAdminFraud = "";
                    String isAlertToAdminTech = "";

                    String isAlertToPSP = "";
                    String isAlertToPSPSales = "";
                    String isAlertToPSPRF = "";
                    String isAlertToPSPCB = "";
                    String isAlertToPSPFraud = "";
                    String isAlertToPSPTech = "";

                    String isAlertToMerchant = "";
                    String isAlertToMerchantSales = "";
                    String isAlertToMerchantRF = "";
                    String isAlertToMerchantCB = "";
                    String isAlertToMerchantFraud = "";
                    String isAlertToMerchantTech = "";

                    String isAlertToAgent = "";
                    String isAlertToAgentSales = "";
                    String isAlertToAgentRF = "";
                    String isAlertToAgentCB = "";
                    String isAlertToAgentFraud = "";
                    String isAlertToAgentTech = "";

                    String monitoringUnit = "";
                    String alertThreshold = "0";
                    String weeklyThreshold = "0";
                    String monthlyThreshold = "0";
                    String suspensionThreshold = "0";
                    String weeklySuspensionThreshold = "0";
                    String monthlySuspensionThreshold = "0";
                    String ruleVisibility="";

                    String isdailyexecution="";
                    String isweeklyexecution="";
                    String ismonthlyexecution="";

                    String dailyThresholdVisibility="disabled";
                    String weeklyThresholdVisibility="disabled";
                    String monthlyThresholdVisibility="disabled";

                    if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
                    {
                        alertThreshold = Functions.round(monitoringParameterMappingVO.getAlertThreshold(), 2);
                        weeklyThreshold = Functions.round(monitoringParameterMappingVO.getWeeklyAlertThreshold(), 2);
                        monthlyThreshold = Functions.round(monitoringParameterMappingVO.getMonthlyAlertThreshold(), 2);

                        suspensionThreshold = Functions.round(monitoringParameterMappingVO.getSuspensionThreshold(), 2);
                        weeklySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getWeeklySuspensionThreshold(), 2);
                        monthlySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getMonthlySuspensionThreshold(), 2);
                        if(MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
                        {
                            monitoringUnit = "%";
                        }
                        else
                        {
                            monitoringUnit = currency;
                        }

                    }
                    else
                    {
                        alertThreshold = Functions.round(monitoringParameterMappingVO.getAlertThreshold(), 0);
                        weeklyThreshold = Functions.round(monitoringParameterMappingVO.getWeeklyAlertThreshold(), 0);
                        monthlyThreshold = Functions.round(monitoringParameterMappingVO.getMonthlyAlertThreshold(), 0);

                        suspensionThreshold = Functions.round(monitoringParameterMappingVO.getSuspensionThreshold(), 0);
                        weeklySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getWeeklySuspensionThreshold(), 0);
                        monthlySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getMonthlySuspensionThreshold(), 0);
                        /*if (Integer.parseInt(alertThreshold) <= 0)
                        {
                            alertThreshold = "";
                        }
                        if (Integer.parseInt(weeklyThreshold) <= 0)
                        {
                            weeklyThreshold = "";
                        }
                        if (Integer.parseInt(monthlyThreshold) <= 0)
                        {
                            monthlyThreshold = "";
                        }
                        if (Integer.parseInt(suspensionThreshold) <= 0)
                        {
                            suspensionThreshold = "";
                        }
                        if (Integer.parseInt(weeklySuspensionThreshold) <= 0)
                        {
                            weeklySuspensionThreshold = "";
                        }
                        if (Integer.parseInt(monthlySuspensionThreshold) <= 0)
                        {
                            monthlySuspensionThreshold = "";
                        }*/
                        if (MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
                        {
                            monitoringUnit = "Day";
                        }
                        else if(MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
                        {
                            monitoringUnit = "Cnt";
                        }
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
                    {
                        alertActivation = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
                    {
                        suspensionActivation = "checked";
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdmin()))
                    {
                        isAlertToAdmin = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminSales()))
                    {
                        isAlertToAdminSales = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminRF()))
                    {
                        isAlertToAdminRF = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminCB()))
                    {
                        isAlertToAdminCB = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminFraud()))
                    {
                        isAlertToAdminFraud = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdminTech()))
                    {
                        isAlertToAdminTech = "checked";
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartner()))
                    {
                        isAlertToPSP = "checked";
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerSales()))
                    {
                        isAlertToPSPSales = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerRF()))
                    {
                        isAlertToPSPRF = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerCB()))
                    {
                        isAlertToPSPCB = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerFraud()))
                    {
                        isAlertToPSPFraud = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerTech()))
                    {
                        isAlertToPSPTech = "checked";
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchant()))
                    {
                        isAlertToMerchant = "checked";
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantSales()))
                    {
                        isAlertToMerchantSales = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantRF()))
                    {
                        isAlertToMerchantRF = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantCB()))
                    {
                        isAlertToMerchantCB = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantFraud()))
                    {
                        isAlertToMerchantFraud = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantTech()))
                    {
                        isAlertToMerchantTech = "checked";
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgent()))
                    {
                        isAlertToAgent = "checked";
                    }

                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentSales()))
                    {
                        isAlertToAgentSales = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentRF()))
                    {
                        isAlertToAgentRF = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentCB()))
                    {
                        isAlertToAgentCB = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentFraud()))
                    {
                        isAlertToAgentFraud = "checked";
                    }
                    if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgentTech()))
                    {
                        isAlertToAgentTech = "checked";
                    }

                    if("disabled".equals(toggleVisibility))
                    {
                        ruleVisibility="checked";
                    }
                    if("Y".equals(monitoringParameterMappingVO.getIsDailyExecution()))
                    {
                        isdailyexecution = "checked";
                        dailyThresholdVisibility="";
                    }
                    if("Y".equals(monitoringParameterMappingVO.getIsWeeklyExecution()))
                    {
                        isweeklyexecution = "checked";
                        weeklyThresholdVisibility="";
                    }
                    if("Y".equals(monitoringParameterMappingVO.getIsMonthlyExecution()))
                    {
                        ismonthlyexecution = "checked";
                        monthlyThresholdVisibility="";
                    }

            %>
            <tr>
                <td align="center" class="textb">
                    <input type="checkbox" name="mappingid_<%=terminalId%>"
                           value="<%=monitoringParameterVO.getMonitoringParameterId()%>" <%=ruleVisibility%>>
                    <input type="hidden" name="<%=terminalId%>_monitoringParameterId_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterVO.getMonitoringParameterId()%>">
                    <input type="hidden"
                           name="<%=terminalId%>_monitoringUnit_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringUnit%>">
                </td>

                <td align="center" class="textb"><button type="submit" class="goto" onclick="window.open('MonitoringRiskRuleDetails?ruleid=<%=monitoringParameterVO.getMonitoringParameterId()%>&ctoken=<%=user.getCSRFToken()%>', 'newwindow', 'width=500, height=350'); return false;"> <%=monitoringParameterVO.getMonitoringParameterId()%></button></td>
                <td align="center" class="textb"><%=monitoringParameterVO.getMonitoringParameterName()%></td>

                <td align="center">
                    <div class="multiselect" style="width:100%" align="center">
                        <div class="selectBox" onclick="showCheckboxesFrequency(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                            <select  class="textb" onchange="doChangeFrequency(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                                <option>Frequency</option>
                            </select>
                            <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxesfrequency_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes" >

                            <label for="<%=terminalId%>_isdailyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isdailyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsDailyExecution()%>"
                                       valign="middle" <%=isdailyexecution%>
                                       onclick="doChangesForDailyExecution(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"
                                       onchange="dailyEnableDisableTextBox(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isdailyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isdailyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsDailyExecution()%>">
                                Daily
                            </label>

                            <label for="<%=terminalId%>_isweeklyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isweeklyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsWeeklyExecution()%>"
                                       valign="middle"<%=isweeklyexecution%>
                                       onclick="doChangesForWeeklyExecution(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"
                                       onchange="weeklyEnableDisableTextBox(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"">

                                <input type="hidden" name="<%=terminalId%>_isweeklyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isweeklyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsWeeklyExecution()%>">
                                Weekly
                            </label>

                            <label for="<%=terminalId%>_ismonthlyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>">

                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_ismonthlyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsMonthlyExecution()%>"
                                       valign="middle" <%=ismonthlyexecution%>
                                       onclick="doChangesForMonthlyExecution(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"
                                       onchange="monthlyEnableDisableTextBox(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"">

                                <input type="hidden" name="<%=terminalId%>_ismonthlyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_ismonthlyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsMonthlyExecution()%>">
                                Monthly
                            </label>
                        </div>
                    </div>
                </td>

                <td align="center">
                    <input type="checkbox"
                           id="<%=terminalId%>_alertActiovationValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getAlertActivation()%>"
                           valign="middle" <%=alertActivation%>
                           onclick="doChangesForAlertActivation(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                    <input type="hidden" name="<%=terminalId%>_alertActiovation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           id="<%=terminalId%>_alertActiovation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getAlertActivation()%>">
                </td>

                <td align="left" class="textb">
                    <input type=text style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_alertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=alertThreshold%>" <%=dailyThresholdVisibility%>>

                    <input type=hidden style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_hiddenAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=alertThreshold%>">

                    <input type=text style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_weeklyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=weeklyThreshold%>" <%=weeklyThresholdVisibility%>>

                    <input type=hidden style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_hiddenWeeklyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=weeklyThreshold%>">

                    <input type=text style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_monthlyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monthlyThreshold%>" <%=monthlyThresholdVisibility%>>

                    <input type=hidden style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_hiddenMonthlyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monthlyThreshold%>">

                    <b><%=monitoringUnit%></b>
                </td>

                <td align="center"><input type="checkbox"
                                          id="<%=terminalId%>_suspensionActivationValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                          value="<%=monitoringParameterMappingVO.getSuspensionActivation()%>"
                                          valign="middle" <%=suspensionActivation%>
                                          onclick="doChangesForSuspensionActivation(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                    <input type="hidden"
                           name="<%=terminalId%>_suspensionActivation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           id="<%=terminalId%>_suspensionActivation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getSuspensionActivation()%>">
                </td>

                <td align="left" class="textb">
                    <input type=text style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_suspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=suspensionThreshold%>" <%=dailyThresholdVisibility%>>

                    <input type=hidden style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_hiddenSuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=suspensionThreshold%>" >

                    <input type=text style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_weeklySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=weeklySuspensionThreshold%>" <%=weeklyThresholdVisibility%>>

                    <input type=hidden style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_hiddenWeeklySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=weeklySuspensionThreshold%>">

                    <input type=text style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_monthlySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monthlySuspensionThreshold%>" <%=monthlyThresholdVisibility%>>

                    <input type=hidden style="width:40px" class="txtboxsmall" size=5
                           name="<%=terminalId%>_hiddenMonthlySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monthlySuspensionThreshold%>">

                    <b><%=monitoringUnit%></b>
                </td>

                <td align="center"><input type="checkbox"
                                          id="<%=terminalId%>_isAlertToAdminValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                          value="<%=monitoringParameterMappingVO.getIsAlertToAdmin()%>"
                                          valign="middle" <%=isAlertToAdmin%>
                                          onclick="doChangesForAlertToAdmin(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                    <input type="hidden" name="<%=terminalId%>_isAlertToAdmin_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           id="<%=terminalId%>_isAlertToAdmin_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getIsAlertToAdmin()%>">
                </td>
                <td align="center">
                    <div class="multiselect" style="width:80%" align="center">
                        <div class="selectBox" onclick="showCheckboxes(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                            <select  class="textb" >
                                <option>Teams</option>
                            </select>
                            <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxes_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes" >

                            <label for="<%=terminalId%>_isAlertToAdminSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAdminSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminSales()%>"
                                       valign="middle" <%=isAlertToAdminSales%>
                                       onclick="doChangesForAlertToAdminSales(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAdminSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAdminSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminSales()%>">
                                Sales
                            </label>

                            <label for="<%=terminalId%>_isAlertToAdminRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAdminRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminRF()%>"
                                       valign="middle" <%=isAlertToAdminRF%>
                                       onclick="doChangesForAlertToAdminRF(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAdminRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAdminRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminRF()%>">
                                RF
                            </label>

                            <label for="<%=terminalId%>_isAlertToAdminCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAdminCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminCB()%>"
                                       valign="middle" <%=isAlertToAdminCB%>
                                       onclick="doChangesForAlertToAdminCB(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAdminCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAdminCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminCB()%>">
                                CB
                            </label>

                            <label for="<%=terminalId%>_isAlertToAdminFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAdminFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminFraud()%>"
                                       valign="middle" <%=isAlertToAdminFraud%>
                                       onclick="doChangesForAlertToAdminFraud(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAdminFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAdminFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminFraud()%>">
                                Fraud
                            </label>

                            <label for="<%=terminalId%>_isAlertToAdminTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAdminTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminTech()%>"
                                       valign="middle" <%=isAlertToAdminTech%>
                                       onclick="doChangesForAlertToAdminTech(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAdminTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAdminTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAdminTech()%>">
                                Tech
                            </label>
                        </div>
                    </div>
                </td>

                <td align="center"><input type="checkbox"
                                          id="<%=terminalId%>_isAlertToPartnerValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                          value="<%=monitoringParameterMappingVO.getIsAlertToPartner()%>"
                                          valign="middle" <%=isAlertToPSP%>
                                          onclick="doChangesForAlertToPSP(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                    <input type="hidden" name="<%=terminalId%>_isAlertToPartner_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           id="<%=terminalId%>_isAlertToPartner_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getIsAlertToPartner()%>">
                </td>
                <td align="center">
                    <div class="multiselect" style="width:80%" align="center">
                        <div class="selectBox" onclick="showCheckboxesPSP(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                            <select  class="textb" >
                                <option>Teams</option>
                            </select>
                            <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxespsp_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes" >

                            <label for="<%=terminalId%>_isAlertToPartnerSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToPartnerSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerSales()%>"
                                       valign="middle" <%=isAlertToPSPSales%>
                                       onclick="doChangesForAlertToPartnerSales(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToPartnerSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToPartnerSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerSales()%>">
                                Sales
                            </label>

                            <label for="<%=terminalId%>_isAlertToPartnerRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToPartnerRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerRF()%>"
                                       valign="middle" <%=isAlertToPSPRF%>
                                       onclick="doChangesForAlertToPartnerRF(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToPartnerRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToPartnerRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerRF()%>">
                                RF
                            </label>

                            <label for="<%=terminalId%>_isAlertToPartnerCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToPartnerCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerCB()%>"
                                       valign="middle" <%=isAlertToPSPCB%>
                                       onclick="doChangesForAlertToPartnerCB(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToPartnerCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToPartnerCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerCB()%>">
                                CB
                            </label>

                            <label for="<%=terminalId%>_isAlertToPartnerFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToPartnerFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerFraud()%>"
                                       valign="middle" <%=isAlertToPSPFraud%>
                                       onclick="doChangesForAlertToPartnerFraud(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToPartnerFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToPartnerFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerFraud()%>">
                                Fraud
                            </label>

                            <label for="<%=terminalId%>_isAlertToPartnerTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToPartnerTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerTech()%>"
                                       valign="middle" <%=isAlertToPSPTech%>
                                       onclick="doChangesForAlertToPartnerTech(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToPartnerTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToPartnerTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToPartnerTech()%>">
                                Tech
                            </label>
                        </div>
                    </div>
                </td>

                <td align="center">
                    <input type="checkbox"
                           id="<%=terminalId%>_isAlertToMerchantValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getIsAlertToMerchant()%>"
                           valign="middle" <%=isAlertToMerchant%>
                           onclick="doChangesForAlertToMerchant(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                    <input type="hidden" name="<%=terminalId%>_isAlertToMerchant_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           id="<%=terminalId%>_isAlertToMerchant_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getIsAlertToMerchant()%>">
                </td>
                <td align="center">
                    <div class="multiselect" style="width:80%" align="center">
                        <div class="selectBox" onclick="showCheckboxesMerchant(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                            <select  class="textb" >
                                <option>Teams</option>
                            </select>
                            <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxesmerchant_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes" >

                            <label for="<%=terminalId%>_isAlertToMerchantSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToMerchantSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantSales()%>"
                                       valign="middle" <%=isAlertToMerchantSales%>
                                       onclick="doChangesForAlertToMerchantSales(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToMerchantSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToMerchantSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantSales()%>">
                                Sales
                            </label>

                            <label for="<%=terminalId%>_isAlertToMerchantRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToMerchantRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantRF()%>"
                                       valign="middle" <%=isAlertToMerchantRF%>
                                       onclick="doChangesForAlertToMerchantRF(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToMerchantRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToMerchantRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantRF()%>">
                                RF
                            </label>

                            <label for="<%=terminalId%>_isAlertToMerchantCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToMerchantCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantCB()%>"
                                       valign="middle" <%=isAlertToMerchantCB%>
                                       onclick="doChangesForAlertToMerchantCB(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToMerchantCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToMerchantCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantCB()%>">
                                CB
                            </label>

                            <label for="<%=terminalId%>_isAlertToMerchantFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToMerchantFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantFraud()%>"
                                       valign="middle" <%=isAlertToMerchantFraud%>
                                       onclick="doChangesForAlertToMerchantFraud(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToMerchantFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToMerchantFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantFraud()%>">
                                Fraud
                            </label>

                            <label for="<%=terminalId%>_isAlertToMerchantTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToMerchantTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantTech()%>"
                                       valign="middle" <%=isAlertToMerchantTech%>
                                       onclick="doChangesForAlertToMerchantTech(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToMerchantTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToMerchantTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToMerchantTech()%>">
                                Tech
                            </label>
                        </div>
                    </div>
                </td>

                <td align="center">
                    <input type="checkbox"
                           id="<%=terminalId%>_isAlertToAgentValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getIsAlertToAgent()%>"
                           valign="middle" <%=isAlertToAgent%>
                           onclick="doChangesForAlertToAgent(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                    <input type="hidden" name="<%=terminalId%>_isAlertToAgent_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           id="<%=terminalId%>_isAlertToAgent_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                           value="<%=monitoringParameterMappingVO.getIsAlertToAgent()%>">
                </td>
                <td align="center">
                    <div class="multiselect" style="width:80%" align="center">
                        <div class="selectBox" onclick="showCheckboxesAgent(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                            <select  class="textb" >
                                <option>Teams</option>
                            </select>
                            <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxesagent_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes" >

                            <label for="<%=terminalId%>_isAlertToAgentSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAgentSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentSales()%>"
                                       valign="middle" <%=isAlertToAgentSales%>
                                       onclick="doChangesForAlertToAgentSales(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAgentSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAgentSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentSales()%>">
                                Sales
                            </label>

                            <label for="<%=terminalId%>_isAlertToAgentRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAgentRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentRF()%>"
                                       valign="middle" <%=isAlertToAgentRF%>
                                       onclick="doChangesForAlertToAgentRF(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAgentRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAgentRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentRF()%>">
                                RF
                            </label>

                            <label for="<%=terminalId%>_isAlertToAgentCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAgentCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentCB()%>"
                                       valign="middle" <%=isAlertToAgentCB%>
                                       onclick="doChangesForAlertToAgentCB(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAgentCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAgentCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentCB()%>">
                                CB
                            </label>

                            <label for="<%=terminalId%>_isAlertToAgentFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAgentFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentFraud()%>"
                                       valign="middle" <%=isAlertToAgentFraud%>
                                       onclick="doChangesForAlertToAgentFraud(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAgentFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAgentFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentFraud()%>">
                                Fraud
                            </label>

                            <label for="<%=terminalId%>_isAlertToAgentTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                                <input type="checkbox" align="left"
                                       id="<%=terminalId%>_isAlertToAgentTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentTech()%>"
                                       valign="middle" <%=isAlertToAgentTech%>
                                       onclick="doChangesForAlertToAgentTech(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                                <input type="hidden" name="<%=terminalId%>_isAlertToAgentTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       id="<%=terminalId%>_isAlertToAgentTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                       value="<%=monitoringParameterMappingVO.getIsAlertToAgentTech()%>">
                                Tech
                            </label>
                        </div>
                    </div>
                </td>

                <td align="center" class="textb">
                    <textarea cols="15" rows="2" name="<%=terminalId%>_alertMessage_<%=monitoringParameterVO.getMonitoringParameterId()%>" ><%=monitoringParameterVO.getDefaultAlertMsg()%></textarea>
                </td>
            </tr>
            <%
                }
            %>
            <tr>
                <td colspan="17" align="center"><input id="submit" type="Submit" value="Save Changes" name="submit"
                                                       class="buttonform"
                                                       onclick="return confirmsubmit2(<%=terminalId%>)"></td>
            </tr>
        </table>
    </form>
    <%
            }
        }
        else if (request.getAttribute("updatemsg") != null)
        {
            out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
            out.println((String) request.getAttribute("updatemsg"));
            out.println("</b></font>");
            out.println("</td></tr></table>");
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
        }
    %>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>

<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>