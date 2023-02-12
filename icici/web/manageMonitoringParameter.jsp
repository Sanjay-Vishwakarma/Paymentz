<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.Connection" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 11/5/16
  Time: 9:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script language="javascript">
        function doChanges(data,ruleid)
        {
            if(data.checked)
            {
                document.getElementById('cbk_'+ruleid).value="Enable";
            }
            else
            {
                document.getElementById('cbk_'+ruleid).value="Disable";
            }
            document.getElementById('status_'+ruleid).value=document.getElementById('cbk_'+ruleid).value;
        }
        function doChangesForAlertActivation(data, mappingId)
        {
            if(data.checked)
            {
                document.getElementById('alertActiovationValue_' + mappingId).value = "Y";
            }
            else
            {
                document.getElementById('alertActiovationValue_' + mappingId).value = "N";
            }
            document.getElementById('alertActiovation_' + mappingId).value = document.getElementById('alertActiovationValue_' + mappingId).value;
        }
        function dailyEnableDisableTextBox(data)
        {
            if(data.checked)
            {
                document.getElementsByName("alertThreshold")[0].disabled = false;
                document.getElementsByName("suspensionThreshold")[0].disabled = false;
            }
            else
            {
                document.getElementsByName("alertThreshold")[0].disabled = true;
                document.getElementsByName("suspensionThreshold")[0].disabled = true;
            }
        }
        function weeklyEnableDisableTextBox(data)
        {
            if(data.checked)
            {
                document.getElementsByName("weeklyAlertThreshold")[0].disabled = false;
                document.getElementsByName("weeklySuspensionThreshold")[0].disabled = false;
            }
            else
            {
                document.getElementsByName("weeklyAlertThreshold")[0].disabled = true;
                document.getElementsByName("weeklySuspensionThreshold")[0].disabled = true;
            }
        }
        function monthlyEnableDisableTextBox(data)
        {
            if (data.checked)
            {
                document.getElementsByName("monthlyAlertThreshold")[0].disabled = false;
                document.getElementsByName("monthlySuspensionThreshold")[0].disabled = false;
            }
            else
            {
                document.getElementsByName("monthlyAlertThreshold")[0].disabled = true;
                document.getElementsByName("monthlySuspensionThreshold")[0].disabled = true;
            }
        }
    </script>
    <script>
        var expanded = false;
        function showCheckboxesAdmin()
        {
            var checkboxes = document.getElementById('checkboxesadmin');
            if (!expanded) {
                checkboxes.style.display = "block";
                expanded = true;
            } else {
                checkboxes.style.display = "none";
                expanded = false;
            }
        }
        function showCheckboxesMerchant()
        {
            var checkboxes = document.getElementById('checkboxesmerchant');
            if (!expanded) {
                checkboxes.style.display = "block";
                expanded = true;
            } else {
                checkboxes.style.display = "none";
                expanded = false;
            }
        }
        function showCheckboxesPartner()
        {
            var checkboxes = document.getElementById('checkboxespartner');
            if (!expanded) {
                checkboxes.style.display = "block";
                expanded = true;
            } else {
                checkboxes.style.display = "none";
                expanded = false;
            }
        }
        function showCheckboxesAgent()
        {
            var checkboxes = document.getElementById('checkboxesagent');
            if (!expanded) {
                checkboxes.style.display = "block";
                expanded = true;
            } else {
                checkboxes.style.display = "none";
                expanded = false;
            }
        }
        function showCheckboxesFrequency()
        {
            var checkboxes = document.getElementById("checkboxsfrequency");
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
    </script>
    <script type="application/javascript">
        function selectTerminals()
        {
            document.getElementById('forms').submit();
        }
    </script>
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
<body>
<%!
    Logger logger=new Logger("manageChargeMaster");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String memberId = nullToStr(request.getParameter("memberId"));
        String terminalId = nullToStr(request.getParameter("terminalId"));
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Risk Rule Mapping
                <div style="float: right;">
                    <form action="/icici/riskrulemapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp; Go Back
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <form action="/icici/servlet/MonitoringParameterMapping?ctoken=<%=ctoken%>" method="post" name="forms" id="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <input type="hidden" value="true" name="isSubmitted">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="5">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Member Id*</td>
                                    <td class="textb">:</td>
                                    <td colspan="2">
                                        <input name="memberId" id="midy" value="<%=memberId%>" style="width: 162px" class="txtbox" autocomplete="on">
                                    <%-- <select name="memberId" id="memberId" class="txtbox" style="width: 162px" onChange="selectTerminals()">
                                            <option value="" selected></option>
                                            <%
                                                Connection conn=null;
                                                String query=null;
                                                PreparedStatement pstmt=null;
                                                ResultSet rs=null;
                                                try
                                                {
                                                    //conn= Database.getConnection();
                                                    conn= Database.getRDBConnection();
                                                    query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                                                    pstmt = conn.prepareStatement( query );
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
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Terminal Id*</td>
                                    <td class="textb">:</td>
                                    <td colspan="2">
                                        <input name="terminalId" id="tid" value="<%=terminalId%>" style="width: 162px" class="txtbox" autocomplete="on">
                                        <%--<select name="terminalId" class="txtbox" style="width: 162px">
                                            <option value="" selected></option>
                                            <%
                                                try
                                                {
                                                    //conn=Database.getConnection();
                                                    conn=Database.getRDBConnection();
                                                    query = "SELECT terminalid,paymodeid,cardtypeid,memberid FROM member_account_mapping WHERE memberid='"+memberId+"'";
                                                    pstmt = conn.prepareStatement( query );
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next()){
                                                        out.println("<option value=\""+rs.getString("terminalid")+"\">"+rs.getString("memberid")+"-"+rs.getString("terminalid")+"-"+GatewayAccountService.getPaymentMode(rs.getString("paymodeid"))+"-"+GatewayAccountService.getCardType(rs.getString("cardtypeid"))+"</option>");

                                                    }
                                                }
                                                catch (SystemError se)
                                                {
                                                    logger.debug("SystemError::::"+se);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Risk Rule Name*</td>
                                    <td class="textb">:</td>
                                    <td colspan="2">
                                        <select name="monitoringParameterId" class="txtbox" style="width: 162px">
                                            <option value="" selected></option>
                                            <%
                                                Connection conn=null;
                                                String query=null;
                                                PreparedStatement pstmt=null;
                                                ResultSet rs=null;
                                                try
                                                {
                                                    //conn=Database.getConnection();
                                                    conn=Database.getRDBConnection();
                                                    query = "select * from  monitoring_parameter_master";
                                                    pstmt = conn.prepareStatement( query );
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\""+rs.getString("monitoing_para_id")+"\">"+rs.getString("monitoing_para_id")+"- "+rs.getString("monitoing_para_name")+"</option>");
                                                    }
                                                }
                                                catch(Exception e)
                                                {
                                                    logger.debug("SystemError::::"+e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Monitoring Frequency*</td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" colspan="2" >
                                        <div class="multiselect" style="width:162px" align="center">
                                            <div class="selectBox" onclick="showCheckboxesFrequency()">
                                                <select class="txtbox">
                                                    <option>Frequency</option>
                                                </select>
                                                <div class="overSelect"></div>
                                            </div>
                                            <div id="checkboxsfrequency" align="left" class="checkboxes" style="width: 162px">
                                                <label for="isdailyexecution">
                                                    <input type="checkbox" value="Y" name="isdailyexecution" id="isdailyexecution" onchange="dailyEnableDisableTextBox(this)">Daily
                                                </label>
                                                <label for="isweeklyexecution">
                                                    <input type="checkbox" value="Y" name="isweeklyexecution" id="isweeklyexecution" onchange="weeklyEnableDisableTextBox(this)">Weekly
                                                </label>
                                                <label for="ismonthlyexecution">
                                                    <input type="checkbox" value="Y" name="ismonthlyexecution" id="ismonthlyexecution" onchange="monthlyEnableDisableTextBox(this)">Monthly
                                                </label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Daily Alert Threshold[Contains whole number Or whole number
                                        with decimal points]
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb" colspan="2">
                                        <input maxlength="50" type="text" name="alertThreshold" class="txtbox" value="" disabled="disabled" style="width: 162px">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Daily Suspension Threshold[Contains whole number Or whole
                                        number with decimal points]
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb" colspan="2">
                                        <input maxlength="50" type="text" name="suspensionThreshold" class="txtbox" value="" disabled="disabled" style="width: 162px">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Weekly Alert Threshold[Contains whole number Or whole number
                                        with decimal points]
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb" colspan="2">
                                        <input maxlength="50" type="text" name="weeklyAlertThreshold" class="txtbox" value="" disabled="disabled" style="width: 162px">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Weekly Suspension Threshold[Contains whole number Or whole number
                                        with decimal points]
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb" colspan="2">
                                        <input maxlength="50" type="text" name="weeklySuspensionThreshold" class="txtbox" value="" disabled="disabled" style="width: 162px">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Monthly Alert Threshold[Contains whole number Or whole number
                                        with decimal points]
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb" colspan="2">
                                        <input maxlength="50" type="text" name="monthlyAlertThreshold" class="txtbox" value="" disabled="disabled" style="width: 162px">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Monthly Suspension Threshold[Contains whole number Or whole number
                                        with decimal points]
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb" colspan="2">
                                        <input maxlength="50" type="text" name="monthlySuspensionThreshold" class="txtbox" value=""disabled="disabled" style="width: 162px">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Alert Message*</td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb" colspan="2">
                                        <textarea cols="21" rows="2" name="alertMessage"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Is Alert To Admin</td>
                                    <td width="5%" class="textb">:</td>
                                    <td class="" >
                                        <input type="checkbox" name="isAlertToAdmin" value="Y" style="margin-top:8px" >
                                    </td>
                                    <td>
                                        <div class="multiselect" style="width:31% margin-left: -8%;" align="center">
                                            <div class="selectBox" onclick="showCheckboxesAdmin()" >
                                                <select  class="txtbox" >
                                                    <option>Teams</option>
                                                </select>
                                                <div class="overSelect"></div>
                                            </div>
                                            <div id="checkboxesadmin" align="left" class="checkboxes" >
                                                <label for="isAlertToAdminSales">
                                                    <input type="checkbox" value="Y" name="isAlertToAdminSales" id="isAlertToAdminSales"/>Sales
                                                </label>
                                                <label for="isAlertToAdminRF">
                                                    <input type="checkbox" value="Y" name="isAlertToAdminRF" id="isAlertToAdminRF"/>RF
                                                </label>
                                                <label for="isAlertToAdminCB">
                                                    <input type="checkbox" value="Y" name="isAlertToAdminCB" id="isAlertToAdminCB"/>CB
                                                </label>
                                                <label for="isAlertToAdminFraud">
                                                    <input type="checkbox" value="Y" name="isAlertToAdminFraud" id="isAlertToAdminFraud"/>Fraud
                                                </label>
                                                <label for="isAlertToAdminTech">
                                                    <input type="checkbox" value="Y" name="isAlertToAdminTech" id="isAlertToAdminTech"/>Tech
                                                </label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Is Alert To Merchant</td>
                                    <td width="5%" class="textb">:</td>
                                    <td class="">
                                        <input type="checkbox" name="isAlertToMerchant" value="Y" style="margin-top:8px">
                                    </td>
                                    <td>
                                        <div class="multiselect"  style="width:31% margin-left: -8%;" align="center">
                                            <div class="selectBox" onclick="showCheckboxesMerchant()">
                                                <select  class="txtbox" >
                                                    <option>Teams</option>
                                                </select>
                                                <div class="overSelect"></div>
                                            </div>
                                            <div id="checkboxesmerchant" align="left" class="checkboxes" >
                                                <label for="isAlertToMerchantSales">
                                                    <input type="checkbox" value="Y" name="isAlertToMerchantSales" id="isAlertToMerchantSales"/>Sales
                                                </label>
                                                <label for="isAlertToMerchantRF">
                                                    <input type="checkbox" value="Y" name="isAlertToMerchantRF" id="isAlertToMerchantRF"/>RF
                                                </label>
                                                <label for="isAlertToMerchantCB">
                                                    <input type="checkbox" value="Y" name="isAlertToMerchantCB" id="isAlertToMerchantCB"/>CB
                                                </label>
                                                <label for="isAlertToMerchantFraud">
                                                    <input type="checkbox" value="Y" name="isAlertToMerchantFraud" id="isAlertToMerchantFraud"/>Fraud
                                                </label>
                                                <label for="isAlertToMerchantTech">
                                                    <input type="checkbox" value="Y" name="isAlertToMerchantTech" id="isAlertToMerchantTech"/>Tech
                                                </label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Is Alert To Partner</td>
                                    <td width="5%" class="textb">:</td>
                                    <td  class="">
                                        <input type="checkbox" name="isAlertToPartner" value="Y" style="margin-top:8px">
                                    </td>
                                    <td>
                                        <div class="multiselect"  style="width:31% margin-left: -8%;" align="center">
                                            <div class="selectBox" onclick="showCheckboxesPartner()">
                                                <select  class="txtbox" >
                                                    <option>Teams</option>
                                                </select>
                                                <div class="overSelect"></div>
                                            </div>
                                            <div id="checkboxespartner" align="left" class="checkboxes" >
                                                <label for="isAlertToPartnerSales">
                                                    <input type="checkbox" value="Y" name="isAlertToPartnerSales" id="isAlertToPartnerSales"/>Sales
                                                </label>
                                                <label for="isAlertToPartnerRF">
                                                    <input type="checkbox" value="Y" name="isAlertToPartnerRF" id="isAlertToPartnerRF"/>RF
                                                </label>
                                                <label for="isAlertToPartnerCB">
                                                    <input type="checkbox" value="Y" name="isAlertToPartnerCB" id="isAlertToPartnerCB"/>CB
                                                </label>
                                                <label for="isAlertToPartnerFraud">
                                                    <input type="checkbox" value="Y" name="isAlertToPartnerFraud" id="isAlertToPartnerFraud"/>Fraud
                                                </label>
                                                <label for="isAlertToPartnerTech">
                                                    <input type="checkbox" value="Y" name="isAlertToPartnerTech" id="isAlertToPartnerTech"/>Tech
                                                </label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Is Alert To Agent</td>
                                    <td width="5%" class="textb">:</td>
                                    <td  class="" >
                                        <input type="checkbox" name="isAlertToAgent" value="Y" style="margin-top:8px">
                                    </td>
                                    <td>
                                        <div class="multiselect" style="width:31% margin-left: -8%;" align="center">
                                            <div class="selectBox" onclick="showCheckboxesAgent()">
                                                <select  class="txtbox" >
                                                    <option>Teams</option>
                                                </select>
                                                <div class="overSelect"></div>
                                            </div>
                                            <div id="checkboxesagent" align="left" class="checkboxes" >
                                                <label for="isAlertToAgentSales">
                                                    <input type="checkbox" value="Y" name="isAlertToAgentSales" id="isAlertToAgentSales"/>Sales
                                                </label>
                                                <label for="isAlertToAgentRF">
                                                    <input type="checkbox" value="Y" name="isAlertToAgentRF" id="isAlertToAgentRF"/>RF
                                                </label>
                                                <label for="isAlertToAgentCB">
                                                    <input type="checkbox" value="Y" name="isAlertToAgentCB" id="isAlertToAgentCB"/>CB
                                                </label>
                                                <label for="isAlertToAgentFraud">
                                                    <input type="checkbox" value="Y" name="isAlertToAgentFraud" id="isAlertToAgentFraud"/>Fraud
                                                </label>
                                                <label for="isAlertToAgentTech">
                                                    <input type="checkbox" value="Y" name="isAlertToAgentTech" id="isAlertToAgentTech"/>Tech
                                                </label>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <td width="4%" class="textb">&nbsp;</td>
                                <td width="8%" class="textb">Alert Activation</td>
                                <td width="5%" class="textb">:</td>
                                <td valign="middle" class="tr0" width="6%" colspan="2">
                                    <input type="checkbox" name="alertActivation" value="Y" valign="middle">
                                </td>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <td width="4%" class="textb">&nbsp;</td>
                                <td width="8%" class="textb">Suspension Activation</td>
                                <td width="5%" class="textb">:</td>
                                <td valign="middle" class="tr0" width="6%" colspan="2">
                                    <input type="checkbox" name="suspensionActivation" value="Y" valign="middle">
                                </td>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2">
                                        <button type="submit" class="buttonform" value="Save">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb" colspan="2"></td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
<%
    if (request.getAttribute("message") != null)
    {
%>
<div class="reporttable">
    <%
                out.println(Functions.NewShowConfirmation("Result", (String) request.getAttribute("message")));
            }
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
    %>
</div>
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