<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountVO" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.RuleMasterVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","Fraud Rule Configuration");
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerMerchantFraudRule_Fraud_Rule_Settings = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Fraud_Rule_Settings")) ? rb1.getString("partnerMerchantFraudRule_Fraud_Rule_Settings") : "Fraud Rule Settings";
    String partnerMerchantFraudRule_Partner_ID = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Partner_ID")) ? rb1.getString("partnerMerchantFraudRule_Partner_ID") : "Partner ID";
    String partnerMerchantFraudRule_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Merchant_ID")) ? rb1.getString("partnerMerchantFraudRule_Merchant_ID") : "Merchant ID*";
    String partnerMerchantFraudRule_Search = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Search")) ? rb1.getString("partnerMerchantFraudRule_Search") : "Search";
    String partnerMerchantFraudRule_Internal_Rule_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Internal_Rule_Configuration")) ? rb1.getString("partnerMerchantFraudRule_Internal_Rule_Configuration") : "Internal Rule Configuration";
    String partnerMerchantFraudRule_RuleID = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_RuleID")) ? rb1.getString("partnerMerchantFraudRule_RuleID") : "Rule ID";
    String partnerMerchantFraudRule_RuleName = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_RuleName")) ? rb1.getString("partnerMerchantFraudRule_RuleName") : "Rule Name";
    String partnerMerchantFraudRule_Rule_Description = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Rule_Description")) ? rb1.getString("partnerMerchantFraudRule_Rule_Description") : "Rule Description";
    String partnerMerchantFraudRule_Score = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Score")) ? rb1.getString("partnerMerchantFraudRule_Score") : "Score";
    String partnerMerchantFraudRule_Limit = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Limit")) ? rb1.getString("partnerMerchantFraudRule_Limit") : "Limit";
    String partnerMerchantFraudRule_Status = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Status")) ? rb1.getString("partnerMerchantFraudRule_Status") : "Status";
    String partnerMerchantFraudRule_UpdateSelected = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_UpdateSelected")) ? rb1.getString("partnerMerchantFraudRule_UpdateSelected") : "Update Selected";
    String partnerMerchantFraudRule_Online_Common_Rule_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Online_Common_Rule_Configuration")) ? rb1.getString("partnerMerchantFraudRule_Online_Common_Rule_Configuration") : "Online Common Rule Configuration";
    String partnerMerchantFraudRule_Online_Specific_Rule_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Online_Specific_Rule_Configuration")) ? rb1.getString("partnerMerchantFraudRule_Online_Specific_Rule_Configuration") : "Online Specific Rule Configuration";
    String partnerMerchantFraudRule_Report_Table = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Report_Table")) ? rb1.getString("partnerMerchantFraudRule_Report_Table") : "Report Table";
    String partnerMerchantFraudRule_Result = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_Result")) ? rb1.getString("partnerMerchantFraudRule_Result") : "Result";
    String partnerMerchantFraudRule_No = StringUtils.isNotEmpty(rb1.getString("partnerMerchantFraudRule_No")) ? rb1.getString("partnerMerchantFraudRule_No") : "No Records Found.";


%>
<%!
    private static Logger log=new Logger("partnerMerchantFraudRule.jsp");
%>
<html>
<head>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("ruleid");
            var total_boxes = checkboxes.length;
            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function doChanges(data,ruleid)
        {
            if(data.checked){
                document.getElementById('cbk_'+ruleid).value="Enable";
            }
            else{
                document.getElementById('cbk_'+ruleid).value="Disable";
            }
            document.getElementById('status_'+ruleid).value=document.getElementById('cbk_'+ruleid).value;
        }
        function DoUpdate()
        {
            var checkboxes = document.getElementsByName("ruleid");
            var total_boxes = checkboxes.length;
            flag = false;
            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    break;
                }
            }
            if(!flag)
            {
                alert("select at least one rule");
                return false;
            }
            if (confirm("Do you really want to update all selected rule."))
            {
                document.updateSubAccountLevelFraudConfform.submit();
            }
        }
        function DoUpdate1()
        {
            var checkboxes = document.getElementsByName("ruleid1");
            var total_boxes = checkboxes.length;
            flag = false;
            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    break;
                }
            }
            if(!flag)
            {
                alert("select at least one rule");
                return false;
            }
            if (confirm("Do you really want to update all selected rule."))
            {
                document.update.submit();
            }
        }
        function doChanges1(data,ruleid1)
        {
            if(data.checked){
                document.getElementById('cbk_'+ruleid1).value="Enable";
            }
            else{
                document.getElementById('cbk_'+ruleid1).value="Disable";
            }
            document.getElementById('status_'+ruleid1).value=document.getElementById('cbk_'+ruleid1).value;
        }
        function ToggleAll1(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("ruleid1");
            var total_boxes = checkboxes.length;
            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function ToggleAll2(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("ruleid2");
            var total_boxes = checkboxes.length;
            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
    </script>
    <style type="text/css">
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <style type="text/css">
        input[type=radio], input[type=checkbox]{
            transform: scale(2);
            -ms-transform: scale(2);
            -webkit-transform: scale(2);
            padding: 10px;
        }
        table#paginateid td{
            vertical-align: initial!important;
        }
        @media (max-width: 640px) {
            table thead{
                display: table-row-group;
            }
            table thead tr th:first-child {
                display: block!important;
            }
            table thead tr th:nth-of-type(2){
                display: none;
            }
            table thead tr th:nth-of-type(3){
                display: none;
            }
            table thead tr th:nth-of-type(4){
                display: none;
            }
            table thead tr th:nth-of-type(5){
                display: none;
            }
        }
    </style>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script type="">
        $(document).ready(function() {
            //alert("hi checkbox");
            $('div.icheckbox_square-aero').remove();
            $('ins.iCheck-helper').remove();
        });
    </script>
</head>
<title><%=company%> Fraud Management> Merchant Fraud Rules</title>
<body>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerMerchantFraudRule_Fraud_Rule_Settings%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%
                            ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                            if (partner.isLoggedInPartner(session))
                            {
                                String memberid=nullToStr(request.getParameter("memberid"));
                                String pid=nullToStr(request.getParameter("pid"));
                                String partnerId=(String)session.getAttribute("merchantid");
                                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                String Config =" ";
                                if(Roles.contains("superpartner")){
                                     pid=nullToStr(request.getParameter("pid"));
                                }else{
                                    Config = "disabled";
                                    pid = String.valueOf(session.getAttribute("merchantid"));
                                }
                                String errormsg= String.valueOf(request.getAttribute("errormsg"));
                                Functions functions = new Functions();
                                if (functions.isValueNull(errormsg)){
                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                            }
                        %>
                        <form action="/partner/net/PartnerMerchantFraudRuleList?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="" class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
                            <%--<input type="hidden" value="<%=(String)session.getAttribute("merchantid")%>" name="partnerId" id="partnerId">--%>
                            <br>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=partnerMerchantFraudRule_Partner_ID%></label>
                                        <div class="col-sm-8">
                                        <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                        <input type="hidden" name="pid" value="<%=pid%>">
                                    </div>
                                    </div>
                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=partnerMerchantFraudRule_Merchant_ID%></label>
                                        <div class="col-sm-8">
                                            <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=partnerMerchantFraudRule_Search%></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <%
                String updateMsg = (String)request.getAttribute("updateMsg");
                String statusMsg=(String)request.getAttribute("statusMsg");
                 errormsg= String.valueOf(request.getAttribute("errormsg"));

                MerchantFraudAccountVO fraudAccountVO=(MerchantFraudAccountVO)request.getAttribute("fraudAccountVO");
                FraudSystemSubAccountVO subAccountVO=(FraudSystemSubAccountVO)request.getAttribute("subAccountVO");
                List<RuleMasterVO> accountLevelRuleMapping=(List<RuleMasterVO>)request.getAttribute("accountLevelRuleMapping");
                List<RuleMasterVO> subAccountLevelRuleMapping=(List<RuleMasterVO>)request.getAttribute("subAccountLevelRuleMapping");
                List<RuleMasterVO> internalLevelRuleMapping=(List<RuleMasterVO>)request.getAttribute("internalLevelRuleMapping");

                if(fraudAccountVO!=null && subAccountVO!=null &&  accountLevelRuleMapping!=null && subAccountLevelRuleMapping!=null && internalLevelRuleMapping!=null)
                {
                    if(subAccountLevelRuleMapping.size() > 0 || internalLevelRuleMapping.size() > 0)
                    {

            %>

            <div class="row">
                <%
                    if (internalLevelRuleMapping.size() > 0 && internalLevelRuleMapping!=null)
                    {
                %>
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerMerchantFraudRule_Internal_Rule_Configuration%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">

                            <%
                                if (errormsg.length()>0){
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                                }
                                if (statusMsg.length()>0){
                                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-circle\"></i>&nbsp;&nbsp;" + statusMsg + "</h5>");
                                }
                            %>

                            <form name="update" action="/partner/net/PartnerMerchantFraudRuleList?action=update&ctoken=<%=ctoken%>" method="post" accept-charset="UTF-8">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
                                <input type="hidden" value="<%=fraudAccountVO.getMemberId()%>" name="memberid">
                                <input type="hidden" name="fsaccountid" value="<%=subAccountVO.getFraudSystemAccountId()%>">
                                <input type="hidden" name="fssubaccountid" value="<%=subAccountVO.getFraudSystemSubAccountId()%>">

                                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                    <script type="">
                                        $(document).ready(function()
                                        {
                                            $(".thcheckbox1").append("<input type=\"checkbox\" onClick=\"ToggleAll1(this);\" name=\"alltrans\">")
                                        });
                                    </script>

                                    <thead >
                                    <tr style="background-color: #7eccad !important;color: white;">
                                        <th style="text-align: center;" class="thcheckbox1"></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_RuleID%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_RuleName%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_Rule_Description%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_Score%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_Limit%></th>
                                        <th style="text-align: center;" ><%=partnerMerchantFraudRule_Status%></th>
                                    </tr>
                                    </thead>
                                    <tbody class="checkclassfirst1">
                                    <%
                                        for(RuleMasterVO ruleMasterVO:internalLevelRuleMapping)
                                        {
                                            String ext="";
                                            String maxLength="2";
                                            if("Enable".equals(ruleMasterVO.getDefaultStatus()))
                                            {
                                                ext="checked";
                                            }

                                    %>
                                    <script type="">
                                        $(function()
                                        {
                                            $(".checkclassfirst1").append("<tr>" +
                                            "<td style=\"text-align: center; vertical-align: middle;\" > <input type=\"checkbox\" class=\"CheckBoxClass1\" name=\"ruleid1\" value=\"<%=ruleMasterVO.getRuleId()%>\"></td>" +
                                            "<td  data-label=\"Rule ID\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleId()%></td>" +
                                            "<td  data-label=\"Rule Name\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleName()%></td>" +
                                            "<td  data-label=\"Rule Name\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleDescription()%></td>" +
                                            "<td data-label=\"Score\" style=\"text-align: center;\" ><input type=\"text\"  style=\"text-align: center\" maxlength=\"2\" class=\"form-control\" style=\"width:75px\" valign=\"middle\" name=\"score_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultScore()%>\" class=\"txtbox\"></td>" +
                                            "<td data-label=\"Limit\" style=\"text-align: center;\" ><input type=\"text\"  style=\"text-align: center\" maxlength=\"30\" class=\"form-control\" style=\"width:75px\" valign=\"middle\" name=\"value_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultValue()==null?"":ruleMasterVO.getDefaultValue()%>\" class=\"txtbox\"></td>" +
                                            "<td data-label=\"Status\" style=\"text-align: center;\"><input type=\"hidden\" name=\"name_<%=ruleMasterVO.getRuleId()%>\" id=\"name_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getRuleName()%>\"><input type=\"hidden\" name=\"status_<%=ruleMasterVO.getRuleId()%>\" id=\"status_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultStatus()%>\"><input type=\"checkbox\" id=\"cbk_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultStatus()%>\" valign=\"middle\" name=\"status\" <%=ext%> onclick=\"doChanges1(this,<%=ruleMasterVO.getRuleId()%>)\"> </td>" +
                                            "</tr>");
                                        });
                                    </script>
                                    <%
                                        }
                                    %>
                                    </tbody>
                                </table>

                                <div align="center">
                                    <button type="button" name="action" value="update" class="btn btn-default" onClick="return DoUpdate1();" >
                                        <%=partnerMerchantFraudRule_UpdateSelected%>
                                    </button>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </div>

            <form action="" name="adminform" method="post" accept-charset="UTF-8">
                <%
                    if (accountLevelRuleMapping.size() > 0 && accountLevelRuleMapping!=null)
                    {

                %>
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerMerchantFraudRule_Online_Common_Rule_Configuration%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <script type="">
                                        $(document).ready(function()
                                        {
                                            $(".thcheckboxnew").append("<input type=\"checkbox\" onClick=\"ToggleAll2(this);\" name=\"alltrans\" >")
                                        });
                                    </script>
                                    <thead >
                                    <tr style="background-color: #7eccad !important;color: white;">
                                        <th style="text-align: center;" class="thcheckboxnew"></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_RuleID%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_RuleName%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_Score%></th>
                                        <th style="text-align: center;" ><%=partnerMerchantFraudRule_Status%></th>
                                    </tr>
                                    </thead>
                                    <tbody class="checkclassfirst">
                                    <%
                                        for(RuleMasterVO ruleMasterVO:accountLevelRuleMapping)
                                        {
                                            String ext="";
                                            if("Enable".equals(ruleMasterVO.getRuleDescription()))
                                            {
                                                ext="checked";
                                            }
                                    %>
                                    <script type="">
                                        $(function()
                                        {
                                            $(".checkclassfirst").append("<tr>" +
                                            "<td style=\"text-align: center; vertical-align: middle;\" > <input type=\"checkbox\" class=\"CheckBoxClass1\" name=\"ruleid2\" value=\"<%=ruleMasterVO.getRuleId()%>\"></td>" +
                                            "<td  data-label=\"Rule ID\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleId()%></td>" +
                                            "<td  data-label=\"Rule Name\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleName()%></td>" +
                                            "<td data-label=\"Score\" style=\"text-align: center; vertical-align: middle;\" ><input type=\"text\"  style=\"text-align: center\" maxlength=\"2\" class=\"form-control\" style=\"width:75px\" valign=\"middle\" name=\"score_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultScore()%>\"></td>" +
                                            "<td data-label=\"Status\" style=\"text-align: center; vertical-align: middle;\"><input type=\"checkbox\" valign=\"middle\" name=\"status\" <%=ext%>> </td>" +
                                            "</tr>");
                                        });
                                    </script>
                                    <%
                                        }
                                    %>
                                    </tbody>
                                </table>

                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>
            </form>
            <%
                if(subAccountVO != null)
                {
                    PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                    paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                    int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;

                    if (subAccountLevelRuleMapping.size() > 0 && subAccountLevelRuleMapping!=null)
                    {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerMerchantFraudRule_Online_Specific_Rule_Configuration%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <form name="updateSubAccountLevelFraudConfform" action="/partner/net/ManagePartnerMerchantFraudRule?ctoken=<%=ctoken%>" method="post">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
                                <input type="hidden" value="<%=fraudAccountVO.getMemberId()%>" name="memberid">
                                <input type="hidden" name="fsaccountid" value="<%=subAccountVO.getFraudSystemAccountId()%>">
                                <input type="hidden" name="fssubaccountid" value="<%=subAccountVO.getFraudSystemSubAccountId()%>">

                                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                    <script type="">
                                        $(document).ready(function()
                                        {
                                            $(".thcheckbox").append("<input type=\"checkbox\" onClick=\"ToggleAll(this);\" name=\"alltrans\">")
                                        });
                                    </script>

                                    <thead >
                                    <tr style="background-color: #7eccad !important;color: white;">
                                        <th style="text-align: center;" class="thcheckbox"><%--<input type="checkbox" onClick="ToggleAll(this);" name="alltrans">--%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_RuleID%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_RuleID%></th>
                                        <th style="text-align: center;" ><%=partnerMerchantFraudRule_Score%></th>
                                        <th style="text-align: center;"><%=partnerMerchantFraudRule_Status%></th>
                                    </tr>
                                    </thead>
                                    <tbody class="checkclass">

                                    <%
                                        for(RuleMasterVO ruleMasterVO:subAccountLevelRuleMapping)
                                        {
                                            String ext="";
                                            if("Enable".equals(ruleMasterVO.getRuleDescription()))
                                            {
                                                ext="checked";
                                            }
                                    %>

                                    <script type="">
                                        $(function()
                                        {
                                            $(".checkclass").append("<tr>" +
                                            "<input type=\"hidden\" value=\"<%=srno%>\" name=\"srno\">" +
                                            "<td style=\"text-align: center; vertical-align: middle;\" > <input  type=\"checkbox\" class=\"CheckBoxClass\" name=\"ruleid\" value=\"<%=ruleMasterVO.getRuleId()%>\"></td>" +
                                            "<td  data-label=\"Rule ID\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleId()%></td>" +
                                            "<td  data-label=\"Rule Name\" style=\"text-align: center; vertical-align: middle;\" ><%=ruleMasterVO.getRuleName()%></td>" +
                                            "<td data-label=\"Score\" style=\"text-align: center; vertical-align: middle;\" ><input type=\"text\"  style=\"text-align: center; vertical-align: middle;\" maxlength=\"2\" class=\"form-control\" style=\"width:75px\" valign=\"middle\" name=\"score_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getDefaultScore()%>\" class=\"txtbox\"></td>" +
                                            "<td data-label=\"Status\" style=\"text-align: center; vertical-align: middle;\"><input type=\"hidden\" name=\"status_<%=ruleMasterVO.getRuleId()%>\" id=\"status_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getRuleDescription()%>\"><input type=\"checkbox\" id=\"cbk_<%=ruleMasterVO.getRuleId()%>\" value=\"<%=ruleMasterVO.getRuleDescription()%>\" valign=\"middle\" <%=ext%> onclick=\"doChanges(this,<%=ruleMasterVO.getRuleId()%>)\"> </td>" +
                                            "</tr>");
                                        });
                                    </script>
                                    <%
                                            srno++;
                                        }
                                    %>

                                    </tbody>
                                </table>
                                <div id="showingid"><strong>Showing Page <%=paginationVO.getPageNo()%> of <%=paginationVO.getTotalRecords()%> records</strong></div>
                                <div>
                                    <jsp:include page="page.jsp" flush="true">
                                        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                                        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                                        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                                        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                                        <jsp:param name="page" value="PartnerMerchantFraudRuleList"/>
                                        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                                        <jsp:param name="orderby" value=""/>
                                    </jsp:include>
                                </div>
                                <br><br>
                                <div align="center">
                                    <button type="button" value="Update" class="btn btn-default" onClick="return DoUpdate();" >
                                        <%=partnerMerchantFraudRule_UpdateSelected%>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>
        </div>
    </div>
</div>
<br>
<%
                }
                else
                {
                    out.println("<div class=\"row reporttable\">");
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
                    out.println(Functions.NewShowConfirmation1("Result", "Merchant Not Mapped With Fraud Account."));
                    out.println("</div>");
                    out.println("</div>\n" +
                            "  </div>\n" +
                            "</div>");
                }
            }
            else
            {
                out.println("<div class=\"row reporttable\">");
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
                out.println(Functions.NewShowConfirmation1("Result", "Merchant Not Mapped With Fraud Account."));
                out.println("</div>");
                out.println("</div>\n" +
                        "  </div>\n" +
                        "</div>");

            }
        }
        else if(updateMsg != null)
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
            out.println(Functions.NewShowConfirmation1("Result",updateMsg));
            out.println("</div>");
            out.println("</div>\n" +
                    "  </div>\n" +
                    "</div>");

        }
        else if(statusMsg!=null)
        {
            out.println("<div class=\"row reporttable\">");
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
            out.println(Functions.NewShowConfirmation1("Result",statusMsg));
            out.println("</div>");
            out.println("</div>\n" +
                    "  </div>\n" +
                    "</div>");

        }
        else
        {
            out.println("<div class=\"col-md-12\">\n" +
                    "    <div class=\"widget\">\n" +
                    "      <div class=\"widget-header\">\n" +
                    "        <h2><i class=\"fa fa-table\"></i>&nbsp;&nbsp;<strong>"+partnerMerchantFraudRule_Report_Table+"</strong></h2>\n" +
                    "        <div class=\"additional-btn\">\n" +
                    "          <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                    "          <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                    "          <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                    "        </div>\n" +
                    "      </div>\n" +
                    "      <div class=\"widget-content padding\">");
            out.println(Functions.NewShowConfirmation1(partnerMerchantFraudRule_Result,partnerMerchantFraudRule_No));
            out.println("</div>");
            out.println("</div>\n" +
                    "  </div>\n" +
                    "</div>");
        }
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
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