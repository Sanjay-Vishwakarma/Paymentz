<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.manager.enums.Charge_unit" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.vo.WLPartnerCommissionVO" %>
<%@ page import="com.manager.vo.payoutVOs.WLPartnerCommissionDetailsVO" %>
<%@ page import="com.manager.vo.payoutVOs.WLPartnerCommissionReportVO" %>
<%@ page import="com.manager.vo.payoutVOs.WLPartnerPerBankCommissionReportVO" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.manager.vo.payoutVOs.ServiceTaxChargeVO" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 5/9/2016
  Time: 3:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%!
    Logger logger = new Logger("whitelabelinvoice.jsp");
    Functions functions = new Functions();
%>
<html>
<head>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: 'yy-mm-dd'});
        });
        function getDynamicCharges(ctoken)
        {
            var partnerId = document.getElementById("partnerid").value;
            document.f1.action = "/icici/servlet/WLPartnerInvoiceDynamicCharges?ctoken=" + ctoken + "&partnerid=" + partnerId;
            document.f1.submit();
        }
        function confirmsubmitreg()
        {
            var r = window.confirm("Are You Sure To Generate wire?");
            if (r == true)
            {
                document.getElementById("generatewire").submit();
            }
            else
            {
                return false;
            }
        }
    </script>
    <title> Add White Label Invoice</title>
</head>
<body>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        List<WLPartnerCommissionVO> wlPartnerCommissionVOs = (List) request.getAttribute("wlPartnerCommissionVOList");
        Set<String> processingCurrSet = (Set) request.getAttribute("processingCurrSet");
        String reportingCurrency = (String) request.getAttribute("reportingCurrency");

        String pgtypeId = Functions.checkStringNull(request.getParameter("pgtypeid")) == null ? "" : request.getParameter("pgtypeid");
        String partnerId = Functions.checkStringNull(request.getParameter("partnerid")) == null ? "" : request.getParameter("partnerid");

        PartnerManager partnerManager = new PartnerManager();
        List<PartnerDetailsVO> partnerList = partnerManager.getAllWhitelabelPartners();


%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                White Label Invoice Manager
                <div style="float: right;">
                    <form action="/icici/wlPartnerInvoiceList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Invoice List
                        </button>
                    </form>
                </div>
            </div>
            <form name="f1" action="/icici/servlet/WhitelableInvoice?ctoken=<%=ctoken%>" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table align="center" width="65%">
                    <tbody>
                    <tr>
                        <td>
                            <table width="75%" align="center">
                                <tbody>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                <tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">Partner Name*</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <select id="partnerid" name="partnerid" class="txtbox"
                                                onchange="getDynamicCharges('<%=ctoken%>')">
                                            <option value="">Select Partner Name</option>
                                            <%
                                                for (PartnerDetailsVO partnerDetailsVO : partnerList)
                                                {
                                                    String isSelected = "";
                                                    String partnerID = partnerDetailsVO.getPartnerId() + "-" + partnerDetailsVO.getCompanyName();
                                                    if (partnerDetailsVO.getPartnerId().equalsIgnoreCase(partnerId))
                                                        isSelected = "selected";
                                                    else
                                                        isSelected = "";

                                            %>
                                            <option value="<%=partnerDetailsVO.getPartnerId()%>" <%=isSelected%>><%=partnerID%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                <tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">Start Date*</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td colspan="2" class="textb"><input type="text" readonly class="datepicker"
                                                                         name="firstdate" style="width: 48%;"></td>
                                    <td colspan="2" align="left" class="textb"><input maxlength="10"
                                                                                      type="text"
                                                                                      class="txtbox"
                                                                                      name="settledstarttime"
                                                                                      value="00:00:00"
                                                                                      placeholder="(HH:MM:SS)">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                <tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">End Date*</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td colspan="2" class="textb"><input type="text" readonly class="datepicker"
                                                                         name="lastdate" style="width: 48%;"></td>
                                    <td colspan="2" align="left" class="textb" width="43%"><input
                                            maxlength="10" type="text" class="txtbox" name="settledendtime"
                                            value="23:59:59"
                                            placeholder="(HH:MM:SS)"></td>
                                </tr>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                    <%
                                      if(wlPartnerCommissionVOs!=null)
                                      {
                                      StringBuffer dynamicCommissionIds=new StringBuffer();
                                      for(WLPartnerCommissionVO commissionVO:wlPartnerCommissionVOs)
                                      {
                                        String countOrAmountString="";
                                        if("FlatRate".equalsIgnoreCase(commissionVO.getChargeMasterVO().getValueType()))
                                        {
                                          countOrAmountString="Counter";
                                        }
                                        else
                                        {
                                          countOrAmountString=" Amount";
                                        }
                                        if(dynamicCommissionIds.length()>0)
                                        {
                                          dynamicCommissionIds.append(",");
                                        }
                                        dynamicCommissionIds.append(commissionVO.getChargeId());
                                        %>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><%=commissionVO.getChargeMasterVO().getChargeName()%> <%=countOrAmountString%>
                                        *
                                    </td>
                                    <td class="textb">:</td>
                                    <td colspan="4" class="textb">
                                        <input maxlength="10" type="text" class="txtbox"
                                               name="dynamicchargevalue_<%=commissionVO.getChargeId()%>" value=""
                                               style="width:33%;">
                                        <input maxlength="10" type="hidden" class="txtbox"
                                               name="dynamicchargename_<%=commissionVO.getChargeId()%>"
                                               value="<%=commissionVO.getChargeMasterVO().getChargeName()%>"
                                               style="width: 220px">
                                        <input maxlength="10" type="hidden" class="txtbox"
                                               name="dynamicchargetype_<%=commissionVO.getChargeId()%>"
                                               value="<%=commissionVO.getChargeMasterVO().getValueType()%>"
                                               style="width: 220px"></td>
                                </tr>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                    <%
                                    }          %>
                                <input type="hidden" name="dynamiccommissionids"
                                       value="<%=dynamicCommissionIds.toString()%>">
                                    <%           }
                                    %>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                    <%
                                      if(processingCurrSet!=null)
                                      {

                                      %>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Reporting Currency</td>
                                    <td class="textb">:</td>
                                    <td colspan="4" class="textb"><%=reportingCurrency%>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                    <%

                                      StringBuffer processingCurrency=new StringBuffer();
                                      for(String currency:processingCurrSet)
                                      {
                                        if(processingCurrency.length()>0)
                                        {
                                          processingCurrency.append(",");
                                        }
                                        processingCurrency.append(currency);
                                        %>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><%=currency%>&nbsp; Conversion Rate
                                        *
                                    </td>
                                    <td class="textb">:</td>
                                    <td colspan="4" class="textb">
                                        <input maxlength="10" type="text" class="txtbox"
                                               name="<%=currency%>_conversion_rate" value=""
                                               style="width:33%;">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                    <%
                                    }          %>
                                <input type="hidden" name="processingcurrency"
                                       value="<%=processingCurrency.toString()%>">
                                    <%
            }
          %>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">Random Charge Details:</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td colspan="2" class="txtbox">
                                        <input class="txtbox" type="text" name="randomchargename" style="width: 48%;"
                                               placeholder="Name"></td>
                                    <td colspan="2" align="left" class="txtbox" width="43%">
                                        <input maxlength="10" type="text" class="txtbox" name="randomchargevalue"
                                               value="" placeholder="Amount">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="7">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="7" style="padding: 3px" width="50%" class="textb" align="center">
                                        <button type="submit" class="buttonform" value="Add" style="width:150px ">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Preview
                                        </button>
                                    </td>
                                </tr>
                            </table>
                    </tbody>
                </table>
                </tbody>
            </form>
        </div>
    </div>
</div>
<%
    WLPartnerCommissionReportVO wlPartnerCommissionReportVO = (WLPartnerCommissionReportVO) session.getAttribute("wlPartnerCommissionReportVO");
    if (wlPartnerCommissionReportVO != null)
    {
        session.removeAttribute("wlPartnerCommissionReportVO");
        session.setAttribute("wlPartnerCommissionReportVOForWireGeneration", wlPartnerCommissionReportVO);

        HashMap<String, WLPartnerCommissionDetailsVO> otherCommissionFeesDetailsVOHashMap = wlPartnerCommissionReportVO.getOtherFeeHashMap();
        HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal = wlPartnerCommissionReportVO.getServiceTaxChargeVOHashMapFinal();
        PartnerDetailsVO partnerDetailsVO = wlPartnerCommissionReportVO.getPartnerDetailsVO();

        double totalCommissionAmount = 0.00;
        double totalPreviousAmount = wlPartnerCommissionReportVO.getPreviousBalanceAmount();
        double transactionFee = wlPartnerCommissionReportVO.getTotalTransactionFee();
        double netFinalCommissionAmount = wlPartnerCommissionReportVO.getNetFinalFeeAmount();
        double otherTotalCommissionAmount = wlPartnerCommissionReportVO.getOtherFeeAmount();

        if (wlPartnerCommissionReportVO != null)
        {

            String customerName = "";
            String contactName = "";
            String address = "";
            String telephoneNumber = "";
            String gstin = "";
            String lutno = "";

            if (functions.isValueNull(wlPartnerCommissionReportVO.getCustomer()))
            {
                customerName = wlPartnerCommissionReportVO.getCustomer();
            }
            if (functions.isValueNull(partnerDetailsVO.getContactPerson()))
            {
                contactName = partnerDetailsVO.getContactPerson();
            }
            if (functions.isValueNull(partnerDetailsVO.getAddress()))
            {
                address = partnerDetailsVO.getAddress();
            }
            if (functions.isValueNull(partnerDetailsVO.getTelno()))
            {
                telephoneNumber = partnerDetailsVO.getTelno();
            }


            List<WLPartnerPerBankCommissionReportVO> wlPartnerPerBankCommissionReportVOs = wlPartnerCommissionReportVO.getWlPartnerPerBankCommissionReportVOList();
%>
<div class="reporttable">
    <table align=center width="73%" height="100%" border="2px" style="border-color:#ffffff ">
        <tr>
            <td colspan="4" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Invoice Details</b>
            </td>
            <td colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Customer Details</b>
            </td>
        </tr>
        <tr>
            <td colspan="4" style="padding-left: 1.5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">
                Invoice Number:-&nbsp;<%=wlPartnerCommissionReportVO.getInvoiceNumber()%>
            </td>
            <td colspan="5" style="padding-left: 1.5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">
                Customer Name:-&nbsp;<%=customerName%>
            </td>
        </tr>
        <tr>
            <td colspan="4" style="padding-left: 1.5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">
                Invoice Date:-&nbsp;<%=wlPartnerCommissionReportVO.getInvoiceDate()%>
            </td>
            <td colspan="5" style="padding-left: 1.5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">
                Customer Type:-&nbsp;PSP<%--(Compania incubadora de Soluciones Moviles S.A.C.)--%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left:1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                PSP Name:-&nbsp;<%=wlPartnerCommissionReportVO.getPspName()%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                Contact Name:-&nbsp;<%=contactName%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                Reporting
                Period:-&nbsp;<%=wlPartnerCommissionReportVO.getStartPeriod() + " to " + wlPartnerCommissionReportVO.getEndPeriod()%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                Address:-&nbsp;<%=address%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                Report Currency:-&nbsp;<%=wlPartnerCommissionReportVO.getCurrency()%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                Telephone No:-&nbsp;<%=telephoneNumber%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                GSTIN:-&nbsp;<%="27AAECP5135G1ZH"%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                &nbsp;<%=""%>
            </td>
        </tr>
        </tr>
        <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
            LUT NO:-&nbsp;<%="AD270820003263J"%>
        </td>
        <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
            &nbsp;<%=""%>
        </td>
        </tr>

    <%
        int totalCommissionValueTest = 0;
        for (WLPartnerPerBankCommissionReportVO wlPartnerPerBankCommissionReportVO : wlPartnerPerBankCommissionReportVOs)
        {
            totalCommissionAmount = totalCommissionAmount + wlPartnerPerBankCommissionReportVO.getTransactionFeeAmount();
            if(totalCommissionAmount >0)
            {
                totalCommissionValueTest = 1;
                break;
            }
        }
    %>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "9":"8"%>" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>
                Transactions Fees</b></td>
        </tr>
        <tr>
            <td style="width:4%" valign="middle" align="center" class="texthead" colspan="2"><b>Fee Name</b></td>
            <td style="width:2%" valign="middle" align="center" class="texthead"><b>Processor</b></td>
            <td style="width:2%" valign="middle" align="center" class="texthead"><b>Currency</b></td>
            <td style="width:2%" valign="middle" align="center" class="texthead"><b>Rate/Fee</b></td>
            <td style="width:2%" valign="middle" align="center" class="texthead"><b>Counter</b></td>
            <%
            if(totalCommissionValueTest == 1)
            {
            %>
            <td style="width:2%" valign="middle" align="center" class="texthead" ><b>Amount</b></td>
            <%
                }
            %>
            <td style="width:2%" valign="middle" align="center" class="texthead"><b>Total</b></td>
            <td style="width:2%" valign="middle" align="center" class="texthead"><b>Line Total</b></td>
        </tr>
        <%
            for (WLPartnerPerBankCommissionReportVO wlPartnerPerBankCommissionReportVO : wlPartnerPerBankCommissionReportVOs)
            {
                String bankName = wlPartnerPerBankCommissionReportVO.getGatewayType().getGateway();
                String processingCurrency = wlPartnerPerBankCommissionReportVO.getProcessingCurrency();
                totalCommissionAmount = totalCommissionAmount + wlPartnerPerBankCommissionReportVO.getTransactionFeeAmount();

                HashMap<String, WLPartnerCommissionDetailsVO> commissionDetailsVOHashMap = wlPartnerPerBankCommissionReportVO.getTransactionFeeHashMap();
                Set set = commissionDetailsVOHashMap.keySet();
                Iterator iterator = set.iterator();
                while (iterator.hasNext())
                {
                    String commissionName = (String) iterator.next();
                    WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = commissionDetailsVOHashMap.get(commissionName);
                    String commissionRateValue = "";
                    if (Charge_unit.Percentage.toString().equals(wlPartnerCommissionDetailsVO.getValueType()))
                    {
                        commissionRateValue = Double.valueOf(wlPartnerCommissionDetailsVO.getChargeValue()) + "%";
                    }
                    else
                    {
                        commissionRateValue = String.valueOf(Double.valueOf(wlPartnerCommissionDetailsVO.getChargeValue()));
                    }
        %>
        <tr>
            <td colspan="2" style="padding-left:1.5%" class="textb" valign="middle" align="left" bgcolor="#f5f5f5"
                    ><%=wlPartnerCommissionDetailsVO.getChargeName()%>
            </td>
            <td class="textb" valign="middle" align="center" bgcolor="#f5f5f5"
                    ><%=bankName%>
            </td>
            <td class="textb" valign="middle" align="center" bgcolor="#f5f5f5"
                    ><%=processingCurrency%>
            </td>
            <td style="padding-left: 4.0%" class="textb" valign="middle" align="left" bgcolor="#f5f5f5"
                    ><%=commissionRateValue%>
            </td>
            <td style="padding-right: 3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=wlPartnerCommissionDetailsVO.getCount()%>
            </td>
            <%
                if(totalCommissionValueTest == 1)
                {
            %>
            <td style="padding-right: 3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5">
                <%=Functions.round(wlPartnerCommissionDetailsVO.getAmount(), 2)%>
            </td>
            <% } %>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2)%>
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2)%>
            </td>
        </tr>
        <%
            }
        %>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" valign="middle" align="right" bgcolor="#f5f5f5" class="textb"><b><%--Total:---%></b></td>
            <td style="padding-right:3.5%" valign="middle" align="right" class="texthead"
                style="background-color:#2c3e50; "><font
                    color="white"><b><%=Functions.round(wlPartnerPerBankCommissionReportVO.getTransactionFeeAmount(), 2)%>
            </b></font></td>
        </tr>
        <%
            }
        %>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "9":"8"%>" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Other Fees</b></td>
        </tr>
        <%
            Set set1 = otherCommissionFeesDetailsVOHashMap.keySet();
            Iterator iterator2 = set1.iterator();
            while (iterator2.hasNext())
            {
                String commissionName = (String) iterator2.next();
                WLPartnerCommissionDetailsVO wlPartnerCommissionDetailsVO = otherCommissionFeesDetailsVOHashMap.get(commissionName);
        %>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "4":"3"%>" style="padding-left: 1.5%" class="textb" valign="middle" align="left" bgcolor="#f5f5f5"
                    ><%=wlPartnerCommissionDetailsVO.getChargeName()%>
            </td>
            <td style="padding-right: 3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(new Double(wlPartnerCommissionDetailsVO.getChargeValue()), 2)%>
            </td>
            <td style="padding-right: 3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=wlPartnerCommissionDetailsVO.getCount()%>
            </td>
            <td class="textb" valign="middle" align="center" bgcolor="#f5f5f5">
                <%=/*Functions.round(wlPartnerCommissionDetailsVO.getAmount(), 2)*/"-"%>
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2)%>
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(wlPartnerCommissionDetailsVO.getTotal(), 2)%>
            </td>
        </tr>
        <%
            }
        %>
        <%
            if (functions.isValueNull(wlPartnerCommissionReportVO.getRandomChargeName()))
            {%>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" style="padding-left: 1.5%" class="textb" valign="middle" align="left"
                bgcolor="#f5f5f5"><%=wlPartnerCommissionReportVO.getRandomChargeName()%>
            </td>
            <%--<td style="padding-right: 3.5%" class="textb" valign="middle" align="center" bgcolor="#f5f5f5"></td>
            <td style="padding-right: 3.5%" class="textb" valign="middle" align="center" bgcolor="#f5f5f5"></td>
            <td class="textb" valign="middle" align="center" bgcolor="#f5f5f5"></td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="center" bgcolor="#f5f5f5"></td>--%>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right"
                bgcolor="#f5f5f5"><%=Functions.round(wlPartnerCommissionReportVO.getRandomChargeValue(), 2)%>
            </td>
        </tr>
        <%
            }
        %>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" valign="middle" align="right" bgcolor="#f5f5f5" class="textb"><b><%--Total:---%></b></td>
            <td style="padding-right:3.5%" valign="middle" align="right" class="texthead"
                style="background-color:#2c3e50; "><font
                    color="white"><b><%=Functions.round(otherTotalCommissionAmount, 2)%>
            </b></font></td>
        </tr>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "6":"5"%>" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Gross Summary</b></td>
            <td valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Fee Total</b></td>
            <td valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Currency Multiplier</b></td>
            <td valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Line Total</b></td>
        </tr>
        <%
            for (WLPartnerPerBankCommissionReportVO wlPartnerPerBankCommissionReportVO : wlPartnerCommissionReportVO.getWlPartnerPerBankCommissionReportVOList())
            {
        %>
        <tr>
            <td style="padding-left: 1.5%" class="textb" colspan="<%=totalCommissionValueTest==1 ? "6":"5"%>" valign="middle" align="left" bgcolor="#f5f5f5">
                <%=wlPartnerPerBankCommissionReportVO.getGatewayType().getName()%> Transactions
                Fees&nbsp;[<%=wlPartnerPerBankCommissionReportVO.getProcessingCurrency()%>]
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right"
                bgcolor="#f5f5f5"><%=Functions.round(wlPartnerPerBankCommissionReportVO.getTransactionFeeAmount(), 2)%>
            </td>
            <td class="textb" valign="middle" align="center" bgcolor="#f5f5f5">
                <%
                    String stringAmount = String.valueOf(wlPartnerPerBankCommissionReportVO.getConversionRate());
                    int length = stringAmount.length();
                    if (length > 5)
                    {%>
                <%=wlPartnerPerBankCommissionReportVO.getConversionRate()%>
                <%
                }
                else
                {
                %>
                <%=Functions.round(wlPartnerPerBankCommissionReportVO.getConversionRate(), 2)%>
                <%
                    }
                %>
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(wlPartnerPerBankCommissionReportVO.getConvertedAmount(), 2)%>
            </td>
        </tr>
        <%
            }
        %>
        <tr>
            <td style="padding-left: 1.5%" class="textb" colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" valign="middle" align="left"
                bgcolor="#f5f5f5">Transaction
                Fee&nbsp;[<%=wlPartnerCommissionReportVO.getCurrency()%>]
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(transactionFee, 2)%>
            </td>
        </tr>

        <tr>
            <td style="padding-left: 1.5%" class="textb" colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" valign="middle" align="left"
                bgcolor="#f5f5f5">Other
                Fee&nbsp;[<%=wlPartnerCommissionReportVO.getCurrency()%>]
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(otherTotalCommissionAmount, 2)%>
            </td>
        </tr>
        <%
            Set serviceSet1 = serviceTaxChargeVOHashMapFinal.keySet();
            Iterator serviceIterator2 = serviceSet1.iterator();
            while (serviceIterator2.hasNext())
            {
                String commissionName = (String) serviceIterator2.next();
                ServiceTaxChargeVO serviceTaxChargeVO = serviceTaxChargeVOHashMapFinal.get(commissionName);
        %>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "4":"3"%>" style="padding-left: 1.5%" class="textb" valign="middle" align="left" bgcolor="#f5f5f5"
                    ><%=serviceTaxChargeVO.getChargeName()%>
            </td>
            <td style="padding-right: 3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(new Double(serviceTaxChargeVO.getChargeValue()), 2)%>
            </td>
            <td style="padding-right: 3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%="-"%>
            </td>
            <td class="textb" valign="middle" align="center" bgcolor="#f5f5f5">
                <%=Functions.round(serviceTaxChargeVO.getAmount(), 2)%>
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(serviceTaxChargeVO.getTotal(), 2)%>
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right" bgcolor="#f5f5f5"
                    ><%=Functions.round(serviceTaxChargeVO.getTotal(), 2)%>
            </td>
        </tr>
        <%
            }
        %>
        <tr>
            <td style="padding-left: 1.5%" class="textb" colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" valign="middle" align="left" bgcolor="#f5f5f5">
                Previous Balance Amount
            </td>
            <td style="padding-right:3.5%" class="textb" valign="middle" align="right"
                bgcolor="#f5f5f5"><%=Functions.round(totalPreviousAmount, 2)%>
            </td>
        </tr>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" valign="middle" align="right" class="texthead" bgcolor="#008BBA"><b>Net Final Amt:</b></td>
            <td style="padding-right:3.5%" align="right" valign="middle" class="texthead"
                style="background-color:#2c3e50;"><font
                    color="white"><b><%=Functions.round(netFinalCommissionAmount, 2)%>
            </b></font></td>
        </tr>
        <tr>
            <td style="padding-left: 1.5%" class="textb" colspan="<%=totalCommissionValueTest==1 ? "9":"8"%>" valign="middle" align="center"
                bgcolor="#34495e"><label style="color:white;">Bank Details</label>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                Beneficiary’s Bank Name:&nbsp;<%=""%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                &nbsp;<%="Bank of Baroda"%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                Beneficiary’s Bank Branch with Address :&nbsp;<%=""%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                &nbsp;<%="Crawford Market Branch, Dada Manzil,67/69 Mohammad Ali Road, Mumbai-400003"%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                Beneficiary:&nbsp;<%=""%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                &nbsp;<%="Payment Gateway Solutions Private Limited"%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                Account No. of Beneficiary:&nbsp;<%=""%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                &nbsp;<%="03920200001625"%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                IFSC Code:&nbsp;<%=""%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                &nbsp;<%="BARB0CRAWFO (5th character is zero)"%>
            </td>
        </tr>
        <tr>
            <td class="textb" style="padding-left: 1.5%" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">
                SWIFT Code: &nbsp;<%=""%>
            </td>
            <td class="textb" style="padding-left: 1.5%" colspan="5" valign="middle" align="left" bgcolor="#f5f5f5">
                &nbsp;<%="BARBINBBCRM"%>
            </td>
        </tr>
        <tr>
            <td colspan="<%=totalCommissionValueTest==1 ? "8":"7"%>" valign="middle" align="right" bgcolor="#f5f5f5" class="textb"></td>
            <td align="right" valign="middle" align="right" bgcolor="#f5f5f5" class="textb">
                <form name="generatewire" id="generatewire" method="post"
                      action="/icici/servlet/GenerateWireServlet?ctoken=<%=ctoken%>">
                    <input type="button" class="buttonform" value="Generate Wire" onclick="confirmsubmitreg()">
                </form>
            </td>
        </tr>
    </table>
    <br>
    <br>
    <br>
    <br>
</div>
<%
            }
            else if (functions.isValueNull((String) request.getAttribute("statusMsg")))
            {
                out.println("<div class=\"reporttable\">");
                out.println(Functions.NewShowConfirmation("Result", (String) request.getAttribute("statusMsg")));
                out.println("</div>");
            }
            else
            {
                out.println("<div class=\"reporttable\">");
                out.println(Functions.NewShowConfirmation("Result", "No Record Found"));
                out.println("</div>");
            }
        }
        else if (functions.isValueNull((String) request.getAttribute("statusMsg")))
        {
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Result", (String) request.getAttribute("statusMsg")));
            out.println("</div>");
        }
        else
        {
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Result", "No Record Found"));
            out.println("</div>");
        }

    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }


%>
</body>
</html>
