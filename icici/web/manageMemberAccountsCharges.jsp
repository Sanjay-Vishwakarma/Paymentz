<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 11/8/12
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.manager.ChargeManager" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.enums.Charge_category" %>
<%@ page import="com.manager.enums.Charge_keyword" %>
<%@ page import="com.manager.enums.Charge_subKeyword" %>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function handleTeminalChange()
        {
            var els = document.querySelectorAll('.hideTerminal');
            if (document.getElementById('copyCharge').checked)
            {
                document.getElementById("chargeId").disabled = true;
                document.getElementById("tid3").disabled = true;
                document.getElementById("memberid1").disabled = true;
                els.forEach(function (el)
                {
                    el.classList.remove("hide");
                });
            }
            else
            {
                document.getElementById("chargeId").disabled = false;
                document.getElementById("tid3").disabled = false;
                document.getElementById("memberid1").disabled = false;
                els.forEach(function (el)
                {
                    el.classList.add("hide");
                });
            }
        }
        function checked()
        {
            if (document.getElementById('copyCharge').checked)
            {
                if (!document.getElementById("fromMemberId").value)
                {
                    alert("Please provide the From Member Id")
                    return false;
                }
                if (!document.getElementById("fromTerminal").value)
                {
                    alert("Please provide the From terminal Id")
                    return false;
                }
                /*if (!document.getElementById("tid5").value)
                 {
                 alert("Please provide the To terminal Id")
                 return false;
                 }*/

                if (!document.getElementById("toMemberId").value)
                {
                    alert("Please provide the To Member Id")
                    return false;
                }
            }
            else
            {
                if (!document.getElementById("tid3").value)
                {
                    alert("Please provide the terminal Id")
                    return false;
                }
                var checked = 0;
                var chks = document.getElementsByName("chargeId");
                for (var i = 0; i < chks.length; i++)
                {
                    if (chks[i].checked)
                    {
                        checked++;
                    }
                }

                if (checked <= 0)
                {
                    alert("Please select Charge Details");
                    return false;
                }
            }
        }
        function deleteRow(rowId, id)
        {
            var element = document.getElementById(rowId);
            element.parentNode.removeChild(element);
            document.getElementById("deleteId").value += id + ",";
        }

        $('#sandbox-container input').datepicker({});

        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: 'yy-mm-dd', endDate: '+10y'});
        });

        var expanded = false;
        function showCheckboxes(selectId, checkboxDivId)
        {
            if (!document.getElementById(selectId).disabled)
            {
                var checkboxes = document.getElementById(checkboxDivId);
                if (!expanded)
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
        }
        function showCheckboxes1(selectId, checkboxDivId)
        {
            if (!document.getElementById(selectId).disabled)
            {
                var checkboxes = document.getElementById(checkboxDivId);
                if (!expanded)
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
        }
    </script>
    <script type="text/javascript">
        function getTerminalDetails(ctoken)
        {
            console.log("inside")
            var memberId = document.getElementById("toMemberId").value;
            console.log("MemberId:::",memberId)
            document.f1.action = "/icici/servlet/ManageMemberAccountCharges?ctoken=" + ctoken + "&action=getinfo";
            document.getElementById("submit_button").click();
        }
    </script>
    <title> Add Merchant Charges</title>
    <style type="text/css">

        .multiselect {
            width: 100%;

        }

        .selectBox {
            position: relative;
        }

        .selectBox select {
            width: 60%;
            font-weight: normal;
            height: 60%;

        }

        .overSelect {
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
        }

        .checkboxes-option {
            display: none;
            border: 1px #dadada solid;

            position: absolute;
            width: 30%;
            background-color: #ffffff;
            z-index: 1;
            height: 100px;
            overflow-x: auto;
        }

        .checkboxes-option label {
            display: block;
        }

        .checkboxes-option label:hover {
            background-color: #1e90ff;
        }

        input[type="checkbox"] {
            width: 18px; /*Desired width*/
            height: 18px; /*Desired height*/
        }
    </style>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        TerminalManager terminalManager = new TerminalManager();
        Functions functions = new Functions();

        String terminalid = Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
        String memberid = nullToStr(request.getParameter("memberid"));
        String fromterminal = nullToStr(request.getParameter("fromTerminal"));

        String fromMember = nullToStr(request.getParameter("fromMember"));
        String toMember = nullToStr(request.getParameter("toMember"));

        StringBuilder sb = new StringBuilder();
        String EOL="<BR>";

        if (functions.isValueNull(request.getParameter("memberid")))
        {
            if (!ESAPI.validator().isValidInput("memberid", request.getParameter("memberid"), "Numbers", 20, false) || "0".equals(request.getParameter("memberid")))
            {

                sb.append("Invalid Member Id." +EOL);
            }
        }
        if (functions.isValueNull(request.getParameter("terminalid")))
        {
            if (!ESAPI.validator().isValidInput("terminalid", request.getParameter("terminalid"), "Numbers", 5, false) || "0".equals(request.getParameter("terminalid")))
            {
                sb.append("Invalid Terminal Id.");
            }
            memberpreference.jsp        }

        List<ChargeVO> chargeNameList = PayoutManager.loadchargename();
        String role="Admin";
        String username=(String)session.getAttribute("username");
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Create New Member Account Charge
                <div style="float: right;">
                    <form action="/icici/listMemberAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Member Accounts Charges" name="submit"  class="addnewmember" style="width: 250px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Member Accounts Charges
                        </button>
                    </form>
                </div>
            </div>
            <%
                String errormsg2 = (String) request.getAttribute("sberror");
                String status = (String) request.getAttribute("message");
                String chargeids1 = (String) request.getAttribute("chargeids1");
                String terminalIds = (String) request.getAttribute("terminalIds");
                if (functions.isValueNull(errormsg2))
                {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg2 + "</h5>");
                }
                if (functions.isValueNull(status))
                {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + status + "</h5>");
                }
                if (sb.length()>0){
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + sb.toString() + "</h5>");
                }
            %>
            <form id="myForm" action="/icici/manageMemberAccountsCharges.jsp?ctoken=<%=ctoken%>" method="post" name="f1" onsubmit="return checked()" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <input type="hidden" value="<%=(String)session.getAttribute("merchantid")%>" name="partnerId">

                <table border="0" align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                <tr>
                                    <td style="padding: 2px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 2px" width="30%" class="textb"><b>Member Id*</b></td>
                                    <td style="padding: 2px" width="5%" class="textb">:</td>
                                    <td style="padding: 2px" width="50%">
                                        <input name="memberid" id="memberid1" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" class="form-control" autocomplete="on" required/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                <tr>
                                    <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px"  width="30%" class="textb"><b>Terminal Id*</b></td>
                                    <td style="padding: 2px" width="5%" class="textb">:</td>
                                    <td style="padding: 2px" width="50%">
                                        <input name="terminalid" id="tid3" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>" class="form-control" autocomplete="on" <%--required--%>/>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="30%" class="textb"><b>Charge Name *</b></td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="80%">
                                        <div select size="1" class="selectBox" onclick="showCheckboxes('chargeId','checkboxes')" name="chargename">
                                            <select class="form-control" id="chargeId" name="chargeid">
                                                <option value="" default>Select Charge Name</option>
                                            </select>
                                            <div class="overSelect"></div>
                                        </div>
                                        <div id="checkboxes" class="checkboxes-option" style="resize: both;overflow: auto; z-index: 9999">
                                            <div id="chkbox" class="checkboxes">
                                                <%
                                                    for(ChargeVO chargeVO : chargeNameList)
                                                    {
                                                %>
                                                <input type="checkbox" name="chargeId" value="<%=chargeVO.getChargeid()%>-<%=chargeVO.getChargename()%>-<%=chargeVO.getValuetype()%>"><%=chargeVO.getChargename()+ "-"+ chargeVO.getKeyword()+ "-" + chargeVO.getValuetype()%><br>
                                                <%
                                                    }
                                                %>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px"  width="30%" class="textb">
                                        <%
                                            String check="";
                                            if(functions.isValueNull(request.getParameter("copyCharge"))){
                                                check="checked";
                                            }
                                        %>
                                        <input type="checkbox" name="copyCharge" id="copyCharge" value="copyCharge" <%=check%> onchange="handleTeminalChange()"><b> Copy Terminal Charge</b>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr class="hideTerminal hide">
                                    <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px"  width="30%" class="textb"><b>From Member*</b></td>
                                    <td style="padding: 2px" width="5%" class="textb">:</td>
                                    <td style="padding: 2px" width="50%">
                                        <input name="fromMember" id="fromMemberId" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromMember)%>" class="form-control" autocomplete="on" />
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr class="hideTerminal hide">
                                    <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px"  width="30%" class="textb"><b>From Terminal*</b></td>
                                    <td style="padding: 2px" width="5%" class="textb">:</td>
                                    <td style="padding: 2px" width="50%">
                                        <input name="fromTerminal" id="fromTerminal" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromterminal)%>" class="form-control" autocomplete="on" />
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr class="hideTerminal hide">
                                    <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px"  width="30%" class="textb"><b>To Member*</b></td>
                                    <td style="padding: 2px" width="5%" class="textb">:</td>
                                    <td style="padding: 2px" width="50%">
                                        <input name="toMember" id="toMemberId" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toMember)%>" onblur="return getTerminalDetails('<%=ctoken%>')" class="form-control" autocomplete="on" />
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr class="hideTerminal hide">
                                    <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px"  width="30%" class="textb"><b>To Terminal*</b></td>
                                    <td style="padding: 2px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="80%">
                                        <div select size="1" class="selectBox" onclick="showCheckboxes1('tid5','checkboxes1')" name="toTerminal1">
                                            <select class="form-control" id="tid5" name="toTerminal2">
                                                <option value="" default>Select Terminal Id</option>
                                            </select>
                                            <div class="overSelect"></div>
                                        </div>
                                        <div id="checkboxes1" class="checkboxes-option" style="resize: both;overflow: auto; z-index: 9999">
                                            <div id="chkbox1" class="checkboxes">
                                                <%
                                                    List<TerminalVO> terminalList = terminalManager.getTerminalsByMerchantId(toMember);
                                                    for(TerminalVO terminalVO : terminalList)
                                                    {
                                                %>
                                                <input type="checkbox" name="toTerminal" value="<%=terminalVO.getTerminalId()%>"><%=terminalVO.getTerminalId()%>-<%=terminalVO.getPaymentName()%>-<%=terminalVO.getCardType()%><br>
                                                <%
                                                    }
                                                %>
                                            </div>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                <tr>
                                    <center>
                                        <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                        <td style="padding: 3px" width="43%" class="textb"></td>
                                        <td style="padding: 3px" width="5%" class="textb"></td>
                                        <td style="padding: 3px" width="50%" class="textb">
                                            <button type="submit" class="btn btn-default" id="submit_button" value="OK" <%--onclick="checked()"--%>>
                                                <i class="fa fa-sign-in"></i>
                                                &nbsp;&nbsp;OK
                                            </button>
                                        </td></center>
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
<script>
    (function () {
       handleTeminalChange();
    }());
</script>
<%
    try{
    if (sb.length()<=0)
    {
        String chargeIds[]=null;
        String terminalIds1[]=null;
        if(functions.isValueNull(chargeids1))
        {
            chargeIds=chargeids1.split("\\|");
        }
        else
        {
            chargeIds=request.getParameterValues("chargeId");
        }
        if(functions.isValueNull(terminalIds))
        {
            terminalIds1=terminalIds.split("\\|");
        }
        else
        {
            terminalIds1=request.getParameterValues("toTerminal");
        }
        StringBuffer terminalBuffer=new StringBuffer();
        int m=0;
        if(terminalIds1!=null){
            for(String terminal:terminalIds1){
                terminalBuffer.append(terminal);
                if(m!=terminalIds1.length)
                {
                    terminalBuffer.append("|");
                }
                m=m+1;
            }
        }

        String memberId=request.getParameter("memberid");
        String terminalId=request.getParameter("terminalid");
        String fromTerminal=request.getParameter("fromTerminal");
        String ToMember=request.getParameter("toMember");
        String FromMember=request.getParameter("fromMember");
        String copy=request.getParameter("copyCharge");
        if(!functions.isValueNull(memberId) && functions.isValueNull(FromMember)){
            memberId=FromMember;
        }
        List<ChargeVO> chargeVOList=null;
        ChargeManager chargeManager=new ChargeManager();
        if(chargeIds==null && functions.isValueNull(fromTerminal) && terminalIds1.length>0 && functions.isValueNull(ToMember) && functions.isValueNull(fromMember)){
            chargeVOList=chargeManager.getListOfTerminalCharges(memberId, fromTerminal);
        }

        if (functions.isValueNull(fromTerminal))
        {
            if (!ESAPI.validator().isValidInput("terminalid", fromTerminal, "Numbers", 5, false) || "0".equals(fromTerminal))
            {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid From Terminal Id." + "</h5>");
                return;
            }
        }
        if (functions.isValueNull(fromTerminal))
        {
            if (chargeVOList.size()==0)
            {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "No Charges Found on From Terminal." + "</h5>");
                return;
            }
        }
        if(chargeIds!=null && functions.isValueNull(terminalId))
        {
            TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
            if (terminalVO == null)
            {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
                return;
            }
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
            String accountId = terminalVO.getAccountId();
            String payModeId = terminalVO.getPaymodeId();
            String cardTypeId = terminalVO.getCardTypeId();

            boolean terminalVO1 = terminalManager.isValidTerminal(memberId, accountId, terminalId, payModeId, cardTypeId);
            if (terminalVO1)
            {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
                return;
            }
        }
        if (functions.isValueNull(memberId) && (functions.isValueNull(terminalId) || functions.isValueNull(terminalBuffer.toString())))
        {
            StringBuffer stringBuffer = new StringBuffer();
            if (chargeIds!=null || chargeVOList.size()>0)
            {
%>
<div class="reporttable">
    <form name="AddNewCharges" action="/icici/servlet/ManageMemberAccountCharges?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" value="<%=memberId%>" name="memberid" id="memberid">
        <input type="hidden" value="<%=toMember%>" name="toMember" id="memberid">
        <input type="hidden" value="<%=terminalId%>" name="terminalid" id="terminalid">
        <input type="hidden" value="<%=fromTerminal%>" name="fromTerminal" id="fromTerminal">
        <%--<input type="hidden" value="<%=terminalBuffer.toString()%>" name="toTerminal" id="toTerminal">--%>
        <input type="hidden" value="<%=copy%>" name="copyCharge">
        <table align="center" width="80%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
                <td align="center" class="th0">Charge Name</td>
                <td align="center" class="th0">Dynamic Value*</td>
                <td align="center" class="th0">Start Date*</td>
                <td align="center" class="th0">End Date*</td>
                <td align="center" class="th0">Member Value*</td>
                <td align="center" class="th0">Agent Value*</td>
                <td align="center" class="th0">Partner Value*</td>
                <td align="center" class="th0">Sequence Number</td>
                <td align="center" class="th0">Action</td>
            </tr>
            </thead>
            <%
                if(chargeIds!=null)
                {
                    int i=0;
                    TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
                    if (terminalVO == null)
                    {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
                    }
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
                    String accountId= terminalVO.getAccountId();
                    String payModeId= terminalVO.getPaymodeId();
                    String cardTypeId=terminalVO.getCardTypeId();

                    boolean terminalVO1 = terminalManager.isValidTerminal(memberId,accountId,terminalId,payModeId,cardTypeId);
                    if(terminalVO1)
                    {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
                    }else {
                        for(String chargeId:chargeIds)
                        {
                            String cid=chargeId.split("-")[0];
                            String cname=chargeId.split("-")[1];
                            String cvaluetype=chargeId.split("-")[2];

                            stringBuffer.append(chargeId);
                            if(i!=chargeIds.length)
                            {
                                stringBuffer.append("|");
                            }
                            i=i+1;

                            String merchantRate="";
                            String agentCommission="";
                            String partnerCommission= "";
                            String isinputrequired="";
                            String sequenceNumber="";
                            String fromDate="";
                            String toDate="";
                            String negativebalance="";

                            //String currency = gatewayAccount.getCurrency();

                            String chargeUnit="";

                            if("Percentage".equals(cvaluetype)){
                                chargeUnit = "%";
                            }
                            else if ("FlatRate".equals(cvaluetype)){
                                chargeUnit = "";
                            }


                            if(functions.isValueNull(request.getParameter("merchantRate_"+cid)))
                                merchantRate=request.getParameter("merchantRate_"+cid);
                            if(functions.isValueNull(request.getParameter("agentCommission_"+cid)))
                                agentCommission=request.getParameter("agentCommission_"+cid);
                            if(functions.isValueNull(request.getParameter("partnerCommission_"+cid)))
                                partnerCommission = request.getParameter("partnerCommission_"+cid);
                            if(functions.isValueNull(request.getParameter("isinputrequired_"+cid)))
                                isinputrequired = request.getParameter("isinputrequired_"+cid);
                            if(functions.isValueNull(request.getParameter("sequenceNumber_"+ cid)))
                                sequenceNumber = request.getParameter("sequenceNumber_"+ cid);
                            if(functions.isValueNull(request.getParameter("fromdate_"+cid)))
                                fromDate = request.getParameter("fromdate_"+cid);
                            if(functions.isValueNull(request.getParameter("todate_"+cid)))
                                toDate = request.getParameter("todate_"+cid);
                            if(functions.isValueNull(request.getParameter("negativebalance_"+cid)))
                                negativebalance = request.getParameter("negativebalance_"+cid);
            %>
            <tbody>
            <tr id="hideRow_<%=cid%>">
                <td><%=cname%>
                    <input type="hidden" name="chargeId_<%=cid%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cid)%>">
                    <%
                        boolean Negativebalanceshow =chargeManager.Negativebalanceshow(cid);
                        if(Negativebalanceshow){
                    %>
                    <ul style="margin-bottom: -11px"><label style="font-size: 14px"><Strong>*Negative Balance Settings*</Strong></label></ul>
                    <select name="negativebalance_<%=cid%>" class="form-control">
                        <%
                            if ("Y".equalsIgnoreCase(negativebalance))
                            {
                        %>
                        <option value="N">No Settlement Charges</option>
                        <option value="Y" selected>Positive Settlement Charges</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value="N">No Settlement Charges</option>
                        <option value="Y">Positive Settlement Charges</option>
                        <%
                            }
                        %>
                    </select>
                    <%
                    }
                    %>
                </td>
                <td> <select name="isinputrequired_<%=cid%>" class="form-control">
                    <%
                        if ("Y".equalsIgnoreCase(isinputrequired))
                        {
                    %>
                    <option value="N">N</option>
                    <option value="Y" selected>Y</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <%
                        }
                    %>
                </select>
                </td>
                <td><input type="text" size="10" readonly="readonly" name="fromdate_<%=cid%>" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromDate)%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
                <td><input type="text" size="10" readonly="readonly" class="datepicker form-control" name="todate_<%=cid%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toDate)%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
                <td><div class="input-group"><input class="form-control"  maxlength="20" type="text" name="merchantRate_<%=cid%>"  value="<%=merchantRate%>" size="5" placeholder="0.00">
                    <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
                </td>
                <td><div class="input-group"><input class="form-control"  maxlength="20" type="text" name="agentCommission_<%=cid%>"  value="<%=agentCommission%>" size="5" placeholder="0.00">
                    <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
                </td>
                <td><div class="input-group"><input class="form-control" maxlength="20" type="text" name="partnerCommission_<%=cid%>"  value="<%=partnerCommission%>" size="5" placeholder="0.00">
                    <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
                </td>
                <td>
                    <select size="1" class="form-control" name="sequenceNumber_<%=cid%>">
                        <option value=""></option>
                        <%
                            for (int j = 1; j <= 50; j++)
                            {
                                String isSelected = "";
                                if (String.valueOf(j).equals(sequenceNumber))
                                {
                                    isSelected = "selected";
                                }
                        %>
                        <option value="<%=j%>" <%=isSelected%>><%=j%>
                                <%
                    }
                  %>
                    </select>
                </td>
                <td><button type="button" class="btn btn-default" name="delete" value="delete" style="display: -webkit-box;" onclick="deleteRow('hideRow_<%=cid%>','<%=cid%>')">
                    <i class="fas fa-trash-alt"></i>&nbsp;&nbsp;Delete</button></td>
            </tr>
            </tbody>
            <%
                    }
                }
            }
            else if(chargeVOList!=null && chargeVOList.size()>0)
            {
                int i=0;
                for(ChargeVO chargeVO:chargeVOList)
                {
                    /*String cid=chargeId.split("-")[0];
                    String cname=chargeId.split("-")[1];
                    String cvaluetype=chargeId.split("-")[2];*/

                    stringBuffer.append(chargeVO.getChargeid()+"-"+chargeVO.getChargename()+"-"+chargeVO.getValuetype());
                    if(i!=chargeVOList.size())
                    {
                        stringBuffer.append("|");
                    }
                    i=i+1;

                    String merchantRate="";
                    String agentCommission="";
                    String partnerCommission= "";
                    String isinputrequired="";
                    String sequenceNumber="";
                    String fromDate="";
                    String toDate="";
                    String[] fromdate=null;
                    String[] todate=null;
                    CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();

                    //String currency = gatewayAccount.getCurrency();

                    String chargeUnit="";

                    if("Percentage".equals(chargeVO.getValuetype())){
                        chargeUnit = "%";
                    }
                    else if ("FlatRate".equals(chargeVO.getValuetype())){
                        chargeUnit = "";
                    }


                    if(functions.isValueNull(chargeVO.getChargevalue()))
                        merchantRate=chargeVO.getChargevalue();
                    if(functions.isValueNull(chargeVO.getAgentChargeValue()))
                        agentCommission=chargeVO.getAgentChargeValue();
                    if(functions.isValueNull(chargeVO.getPartnerChargeValue()))
                        partnerCommission = chargeVO.getPartnerChargeValue();
                    if(functions.isValueNull(chargeVO.getIsInputRequired()))
                        isinputrequired = chargeVO.getIsInputRequired();
                    if(functions.isValueNull(chargeVO.getSequencenum()))
                        sequenceNumber = chargeVO.getSequencenum();
                    if(functions.isValueNull(chargeVO.getStartdate())){
                        fromDate = chargeVO.getStartdate();
                        fromdate= commonFunctionUtil.convertTimestampToDateTimePicker(fromDate);
                    }

                    if(functions.isValueNull(chargeVO.getEnddate())){
                        toDate = chargeVO.getEnddate();
                        todate= commonFunctionUtil.convertTimestampToDateTimePicker(toDate);
                    }
            %>
            <tbody>
            <tr id="hideRow_<%=chargeVO.getChargeid()%>">
                <td><%=chargeVO.getChargename()%>
                    <input type="hidden" name="chargeId_<%=chargeVO.getChargeid()%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeVO.getChargeid())%>">
                    <%
                        boolean Negativebalanceshow =chargeManager.Negativebalanceshow(chargeVO.getChargeid());
                        if(Negativebalanceshow){
                    %>
                    <ul style="margin-bottom: -11px">
                    <label style="font-size: 14px"><Strong>*Negative Balance Settings*</Strong></label></ul>
                    <select name="negativebalance_<%=chargeVO.getChargeid()%>" class="form-control">
                        <%
                            if ("Y".equalsIgnoreCase(chargeVO.getNegativebalance()))
                            {
                        %>
                        <option value="N">No Settlement Charges</option>
                        <option value="Y" selected>Positive Settlement Charges</option>
                        <%
                        }
                        else
                        {
                        %>
                        <option value="N">No Settlement Charges</option>
                        <option value="Y">Positive Settlement Charges</option>
                        <%
                            }
                        %>
                    </select>
                    <%
                        }
                    %>
                </td>
                <td> <select name="isinputrequired_<%=chargeVO.getChargeid()%>" class="form-control">
                    <%
                        if ("Y".equalsIgnoreCase(isinputrequired))
                        {
                    %>
                    <option value="N">N</option>
                    <option value="Y" selected>Y</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <%
                        }
                    %>
                </select>
                </td>
                <td><input type="text" size="10" readonly="readonly" name="fromdate_<%=chargeVO.getChargeid()%>" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate[0])%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
                <td><input type="text" size="10" readonly="readonly" class="datepicker form-control" name="todate_<%=chargeVO.getChargeid()%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate[0])%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
                <td><div class="input-group"><input class="form-control"  maxlength="20" type="text" name="merchantRate_<%=chargeVO.getChargeid()%>"  value="<%=merchantRate%>" size="5" placeholder="0.00">
                    <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
                </td>
                <td><div class="input-group"><input class="form-control"  maxlength="20" type="text" name="agentCommission_<%=chargeVO.getChargeid()%>"  value="<%=agentCommission%>" size="5" placeholder="0.00">
                    <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
                </td>
                <td><div class="input-group"><input class="form-control" maxlength="20" type="text" name="partnerCommission_<%=chargeVO.getChargeid()%>"  value="<%=partnerCommission%>" size="5" placeholder="0.00">
                    <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
                </td>
                <td>
                    <select size="1" class="form-control" name="sequenceNumber_<%=chargeVO.getChargeid()%>">
                        <option value=""></option>
                        <%
                            for (int j = 1; j <= 50; j++)
                            {
                                String isSelected = "";
                                if (String.valueOf(j).equals(sequenceNumber))
                                {
                                    isSelected = "selected";
                                }
                        %>
                        <option value="<%=j%>" <%=isSelected%>><%=j%>
                                <%
                    }
                  %>
                    </select>
                </td>
                <td><button type="button" class="btn btn-default" name="delete" value="delete" style="display: -webkit-box;" onclick="deleteRow('hideRow_<%=chargeVO.getChargeid()%>','<%=chargeVO.getChargeid()%>')">
                    <i class="fas fa-trash-alt"></i>&nbsp;&nbsp;Delete</button></td>
            </tr>
            </tbody>
            <%
                        }
                    }
                }
                else {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "No charges Found On From Terminal" + "</h5>");
                }
            %>
            <input type="hidden" name="deletedId" id="deleteId" value="">
        </table>
        <div class="form-group col-md-12 has-feedback">
            <center>
                <label >&nbsp;</label>
                <input type="hidden" value="1" name="step">
                <input type="hidden" name="chargeids" value="<%=stringBuffer.toString()%>">
                <input type="hidden" name="toTerminal" value="<%=terminalBuffer.toString()%>">
                <button type="submit" class="btn btn-default" name="save" value="Save" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
            </center>
        </div>
    </form>
</div>
<%
            }
        }
        }catch (Exception e){
                e.printStackTrace();
            }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }

%>
<script>
    console.log("outside function---",document.getElementById('copyCharge'));
    (function () {
        console.log("inside function---",document.getElementById('copyCharge'));
        var els = document.querySelectorAll('.hideTerminal');
        if(document.getElementById('copyCharge').checked) {
            document.getElementById("chargeId").disabled = true;
            document.getElementById("tid3").disabled = true;
            els.forEach(function(el) {
                el.classList.remove("hide");
            });
        }else{
            document.getElementById("chargeId").disabled = false;
            document.getElementById("tid3").disabled = false;
            els.forEach(function(el) {
                el.classList.add("hide");
            });
        }
    }());
    $(document).mouseup(function(e)
    {
        var container = $("#checkboxes");
        if(!$(e.target).closest('#chargeId').length > 0) {
            if (!container.is(e.target) && container.has(e.target).length === 0) {
                container.hide();
            }
        }
        else{
            container.show();
        }
    });

    $(document).mouseup(function(e)
    {
        var container = $("#checkboxesTerm");
        if(!$(e.target).closest('#terId').length > 0) {
            if (!container.is(e.target) && container.has(e.target).length === 0) {
                container.hide();
            }
        }
        else{
            container.show();
        }
    });
</script>
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