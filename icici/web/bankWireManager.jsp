<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.manager.vo.BankWireManagerVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 10/9/14
  Time: 5:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<html>
<head>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <%--Datepicker css format--%>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script src="/icici/css/am_multipleselection/bootstrap-multiselect.js"></script>
    <link href="/icici/css/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet"/>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });
    </script>
    <style type="text/css">
        #checkboxes {
            display: none;
            border: 1px #dadada solid;
            position: absolute;
            width: 100%;
            background-color: #ffffff;
            z-index: 1;
            height: 130px;
            overflow-x: auto;
        }

        #checkboxes label {
            display: block;
        }

        #checkboxes label:hover {
            background-color: #1e90ff;
        }

        input[type="checkbox"] {
            width: 18px; /*Desired width*/
            height: 18px; /*Desired height*/
        }

        .icheckbox_square-aero {
            margin: 3px 5px;
        }

        /********************************************************************************/

        .multiselect-container > li {
            padding: 0;
            margin-left: 31px;
        }

        .open > #multiselect-id.dropdown-menu {
            display: block;
        }

        .multiselect-container > li > a > label {
            margin: 0;
            height: 24px;
            padding-left: 1px;
        !important;
            text-align: left;
        }

        span.multiselect-native-select {
            position: relative;
        }

        @supports (-ms-ime-align:auto) {
            span.multiselect-native-select {
                position: static !important;
            }
        }

        select[multiple], select[size] {
            height: auto;
            border-color: rgb(169, 169, 169);
        }

        .widget .btn-group {
            z-index: 1;
        }

        .btn-group, .btn-group-vertical {
            position: relative;
            vertical-align: middle;
            border-radius: 4px;
        }

        #mainbtn-id.btn-default {
            color: #333;
            background-color: #fff;
            border-color: #333;
            padding: 6px;
        }

        .btn-group > .btn:first-child {
            margin-left: 0;
        }

        .btn-group > .btn:first-child {
            margin-left: 0;
        }

        .btn-group > .btn, .btn-group-vertical > .btn {
            position: relative;
            float: left;
        }

        .multiselect-container {
            position: absolute;
            list-style-type: none;
            margin: 0;
            padding: 0;
            height: 225px;
            overflow-y: scroll;
        }

        #multiselect-id.dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            z-index: 500;
            display: none;
            float: left;
            min-width: 160px;
            font-size: 14px;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
            list-style: none;
            background-color: #fff;
            border: 1px solid #ccc;
            border: 1px solid rgba(0, 0, 0, 0.15);
            border-radius: 4px;
            -webkit-box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
            background-clip: padding-box;
        }

        #mainbtn-id .multiselect-selected-text {
            font-size: 12px;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
        }

        #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open > .dropdown-toggle#mainbtn-id.btn-default {
            color: #333;
            /*color: #fff;*/
            background-color: white !important;
            border-color: #ddd !important;
            text-align: left;
            width: 187px;
        !important;
        }

        .multiselect-container .active > a > label {
            color: #fff;
        !important;
        }

        .btn .caret {
            position: absolute;
            display: inline-block;
            width: 0px;
            height: 1px;
            margin-left: 2px;
            vertical-align: middle;
            border-top: 7px solid;
            border-right: 4px solid transparent;
            border-left: 4px solid transparent;
            float: right;
            margin-top: 5px;
            box-sizing: inherit;
            top: 13px;
            right: 5px;
            margin-top: -2px;
        }

        .fa-chevron-down {
            position: absolute;
            right: 0px;
            top: 0px;
            margin-top: -2px;
            vertical-align: middle;
            float: right;
            font-size: 9px;
        }

        #mainbtn-id {
            overflow: hidden;
            display: block;
        }

        .cardLimitCheck, .cardAmountLimitCheck, .amountLimitCheck {
            color: #001962;
            text-valign: center;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
            font-size: 12px;
            FONT-WEIGHT: normal;
            height: 25px;
            -webkit-border-radius: 4px;
            -moz-border-radius: 4px;
            border-radius: 4px;
        }

        /********************************************************************************/
    </style>
    <script type="text/javascript">
        function getAccountDetails(ctoken)
        {
            console.log("inside")
            var accountId = document.getElementById("hiddenaccid").value;
            console.log("accountId===="+accountId)
            document.f1.action = "/icici/servlet/UpdateBankWireManager?ctoken=" + ctoken + "&action=getinfo";
            document.getElementById("submit_button").click();

        }
        function getBankDetails(ctoken)
        {
            console.log("inside")
            var accountId = document.getElementById("parent_bankwireId").value;
            document.f1.action = "/icici/servlet/UpdateBankWireManager?ctoken=" + ctoken + "&action=getinfo";
            document.getElementById("submit_button").click();
        }


        $(function ()
        {
            $(document).ready(function ()
            {
                $(".caret").addClass('icon2');
                $('.multiselect-selected-text').addClass("filter-option pull-left");
                firefox = navigator.userAgent.search("Firefox");
                if (firefox > -1)
                {
                    $('.icon2').removeClass("caret");
                    $('.icon2').addClass("fa fa-chevron-down");
                    $('.icon2').css({
                        "height": "30px",
                        "width": "17px",
                        "text-align": "center",
                        "background-color": "#E6E2E2",
                        "padding-top": "6px",
                        "margin-top": "0px",
                        "border": "1px solid #C7BFBF"
                    });
                    $('.dropdown-toggle').css({"padding": "0px", "vertical-align": "middle", "height": "25px"});
                    $('.tr0 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
                    $('.tr1 .dropdown-toggle .icon2').css({"background-color": "white", "border": "0px"});
                    $('.multiselect-selected-text').css({
                        "padding-top": "4px",
                        "padding-bottom": "10px",
                        "padding-left": "10px",
                        "vertical-align": "middle"
                    });
                }
            });
        });

    </script>

    <script>
        $(function ()
        {
            var ctoken = $("#ctoken").val();
            $('#accountidList').multiselect({
                buttonText: function (options, select)
                {
                    var labels = [];
                    if (options.length === 0)
                    {
                        console.log(" in if");
                        labels.pop();
                        document.getElementById('accid').value = labels;
                        return 'Select Account Id';

                    }

                    else
                    {
                        console.log(" in else");
                        options.each(function ()
                        {
                            labels.push($(this).val());
                        });
                        console.log("Label::::" + labels);
                        document.getElementById('accid').value = labels;
                        return labels.join(', ') + '';
                    }

                },
                includeSelectAllOption: true
            });
        });
    </script>

    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid2.js"></script>
    <title>Bank Description Manager> Bank Wire Manager</title>
</head>

<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Bank Wire Manager
                <div style="float: right;">
                    <form action="/icici/bankWireManager.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" class="addnewmember" value="Add" name="submit" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Wire Manager
                        </button>
                    </form>
                </div>
            </div>
            <%
                if(request.getParameter("MES")!=null)
                {
                    String mes=request.getParameter("MES");
                    if(mes.equals("ERR"))
                    {
                        ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                        for(Object errorList : error.errors())
                        {
                            ValidationException ve = (ValidationException) errorList;
                            out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                        }
                    }
                }
                if (request.getAttribute("error")!=null)
                {
                    out.println("<center><font class=\"textb\">" +request.getAttribute("error") + "</font></center>");
                }
            %>
            <form action="/icici/servlet/BankWireManagerList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%!
                    private static Logger logger = new Logger("test");
                    MerchantDAO merchantDAO = new MerchantDAO();
                %>
                <%

                    String pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
                    String accountid1 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
                    String parent_bankwireId = Functions.checkStringNull(request.getParameter("parent_bankwireId"))==null?"":request.getParameter("parent_bankwireId");
                    String bankwireid= Functions.checkStringNull(request.getParameter("bankwiremangerid"))==null?"":request.getParameter("bankwiremangerid");
                    String disable ="";

                    TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
                    TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();

                    String selected="";
                    HashMap<String,String> dropdown = new HashMap<String, String>();
                    dropdown.put("N","NO");
                    dropdown.put("Y","YES");
                %>
                <%
                    try
                    {
                        session.setAttribute("submit","Reports");
                        String fromdate = null;
                        String todate = null;
                        try
                        {
                            Date date = new Date();
                            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String Date = originalFormat.format(date);
                            fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ?"" : request.getParameter("fromdate");
                            todate = Functions.checkStringNull(request.getParameter("todate")) == null ? "" : request.getParameter("todate");
                        }
                        catch (Exception e)
                        {
                            logger.error("JSP page exception ::",e);
                        }
                %>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:1%;">

                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input size="6" name="fromdate" readonly class="datepicker" style="width: 142px;height: 25px;" value="<%=fromdate%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input size="6" name="todate" style="width: 142px;height: 25px;" readonly class="datepicker" value="<%=todate%>">
                                    </td>

                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Gateway</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox"
                                               autocomplete="on">
                                    </td>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Account Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountid1%>" class="txtbox"
                                               autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Parent Bankwire Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb" >
                                        <input name="parent_bankwireId" id="parent_bankwireId1" value="<%=parent_bankwireId%>" class="txtbox"
                                               autocomplete="on" >
                                    </td>

                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb"> Bankwire Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb" >
                                        <input name="bankwiremangerid" id="bankwireid1" value="<%=bankwireid%>" class="txtbox"
                                               autocomplete="on">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" id="button1">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable">
    <%
        List<BankWireManagerVO> bankWireManagerVOList = (List<BankWireManagerVO>) request.getAttribute("BankWireManagerVOList");
        if(bankWireManagerVOList !=null)
        {
            if(bankWireManagerVOList.size()>0)
            {
                PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

                int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
    %>
    <center><h4 class="textb"><b>Bank Wire Manager List</b></h4></center>
    <table align=center width="80%" border="0" class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td width="4%"valign="middle" align="center" class="th0">Sr&nbsp;No</td>
            <td width="8%"valign="middle" align="center" class="th0">Bank Wire ID</td>
            <td width="8%"valign="middle" align="center" class="th0">Parent BankWireID</td>
            <td width="8%" valign="middle" align="center" class="th0">Settled&nbsp;Date</td>
            <td width="8%" valign="middle" align="center" class="th0">PgtypeId</td>
            <td width="8%" valign="middle" align="center" class="th0">AccountId</td>
            <td width="12%" valign="middle" align="center" class="th0">Bank&nbsp;Start&nbsp;Date</td>
            <td width="12%" valign="middle" align="center" class="th0">Bank&nbsp;End&nbsp;Date</td>
            <td width="12%" valign="middle" align="center" class="th0">Server&nbsp;Start&nbsp;Date</td>
            <td width="12%" valign="middle" align="center" class="th0">Server&nbsp;End&nbsp;Date</td>
            <td colspan="4" valign="middle" align="center" class="th0" >Action</td>
        </tr>
        <%
            String parentwireid= "";
            for(BankWireManagerVO bankWireManagerVO : bankWireManagerVOList)
            {
                Functions functions1=new Functions();
                if (functions1.isValueNull(bankWireManagerVO.getParent_bankwireid()))
                {
                    parentwireid= (String)bankWireManagerVO.getParent_bankwireid();
                }
                else
                {
                    parentwireid= "-";
                }
        %>
        <tr>
            <td align="center" class="tr0"><%=srno%></td>
            <td align="center" class="tr0"><%=bankWireManagerVO.getBankwiremanagerId()%></td>
            <td align="center" class="tr0"><%=parentwireid%></td>
            <td align="center" class="tr0"><%=functions1.isValueNull(bankWireManagerVO.getSettleddate())? bankWireManagerVO.getSettleddate():""%></td>
            <td align="center" class="tr0"><%=bankWireManagerVO.getPgtypeId()%></td>
            <td align="center" class="tr0"><%=bankWireManagerVO.getAccountId()%></td>
            <td align="center" class="tr0"><%=bankWireManagerVO.getBank_start_date()%></td>
            <td align="center" class="tr0"><%=bankWireManagerVO.getBank_end_date()%></td>
            <td align="center" class="tr0"><%=bankWireManagerVO.getServer_start_date()%></td>
            <td align="center" class="tr0"><%=bankWireManagerVO.getServer_end_date()%></td>
            <td align="center" class="tr0"><form action="/icici/servlet/ViewOrEditBankWireManager?ctoken=<%=ctoken%>" method="post"><button type="submit" class="button" value="<%=bankWireManagerVO.getBankwiremanagerId()+"_View"%>" name="action">View</button><input type="hidden" value="<%=bankWireManagerVO.getAccountId()%>" name="accountid"></form></td>
            <td align="center" class="tr0"><form action="/icici/servlet/ViewOrEditBankWireManager?ctoken=<%=ctoken%>" method="post"><button type="submit" class="button" value="<%=bankWireManagerVO.getBankwiremanagerId()+"_Edit"%>" name="action">Edit</button><input type="hidden" value="<%=bankWireManagerVO.getAccountId()%>" name="accountid"><input type="hidden" value="<%=bankWireManagerVO.getParent_bankwireid()%>" name="parent_bankwireId"></form></td>
            <td align="center" class="tr0"><form action="/icici/manageMerchantRandomCharge.jsp?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="bankwireid" value="<%=bankWireManagerVO.getBankwiremanagerId()%>"><button type="submit" class="button" value="" name="action">Add RDM</button></form></td>
            <td align="center" class="tr0"><form action="/icici/servlet/ListMerchantRandomCharges?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="bankwireid" value="<%=bankWireManagerVO.getBankwiremanagerId()%>"><button type="submit" class="button" value="" name="action">View RDM</button></form></td>
        </tr>
        <%
                srno++;
            }
        %>
    </table>
    <div >
        <center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </center>
    </div>
    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry","No records found"));
        }
    }
    else
    {
        BankWireManagerVO singleBankWireManagerVO= (BankWireManagerVO) request.getAttribute("singleBankWireManagerVO");
        BankWireManagerVO bankWireManagerVO=(BankWireManagerVO) request.getAttribute("bankWireManagerVO");
        Functions functions=new Functions();
        if(request.getParameter("UPDATE")!=null)
        {

            System.out.println("bankWireManagerVO-------------------"+bankWireManagerVO.getServer_start_date());
            String update=request.getParameter("UPDATE");
            if(update.equals("Success"))
            {
                out.println("<center><font class=\"textb\">Bank wire manager");
                if( ((ActionVO) request.getAttribute("actionVO")).isAdd())
                {
                    out.println("Added");
                    if (functions.isValueNull(String.valueOf(request.getAttribute("result")))){
                        out.println(request.getAttribute("result"));
                    }
                }
                else
                {
                    out.println("Updated");
                }

                out.println("</font></center>");
            }
        }

        if(singleBankWireManagerVO!=null || ("Add").equals(request.getParameter("submit")))
        {
            String bankwiremanagerId = "";
            String settleddate = "";
            String pgtypeId = "";
            String accountId = "";
            String mid = "";
            String bank_start_date = "";
            String bank_end_date = "";
            String server_start_date = "";
            String server_end_date = "";
            String processing_amount = "";
            String grossAmount = "";
            String netfinal_amount = "";
            String unpaid_amount = "";
            String currency = "";
            String isrollingreservereleasewire = "";
            String rollingreservereleasedateupto = "";
            String declinedcoveredupto = "";
            String chargebackcoveredupto = "";
            String reversedCoveredUpto = "";
            String banksettlement_report_file = "";
            String banksettlement_transaction_file = "";
            String isSettlementCronExceuted = "";
            String isPayoutCronExcuted = "";
            String ispaid = "";
            String bank_start_time = "";
            String bank_end_time = "";
            String server_start_time = "";
            String server_end_time = "";
            String settled_time = "";
            String rollingreservecovered_time = "";
            String declinecovered_time = "";
            String reversecovered_time = "";
            String chargebackcovered_time = "";
            String ispartnerpommcronexecuted = "";
            String isagentcommcronexecuted = "";

            String declinedcoveredStartdate = "";
            String chargebackcoveredStartdate = "";
            String reversedCoveredUptoStartdate = "";
            String rollingReleaseStartdate="";

            String declinecovered_time_start = "";
            String reversecovered_time_start = "";
            String chargebackcovered_time_start = "";
            String rollingRelease_time_start="";

            String declinecovered_time_end = "";
            String reversecovered_time_end= "";
            String chargebackcovered_time_end = "";
            String rollingRelease_time_end="";
            bank_start_date = Functions.checkStringNull(request.getParameter("expected_startDate")) == null ? "" : request.getParameter("expected_startDate");
            bank_start_time = Functions.checkStringNull(request.getParameter("expected_startTime")) == null ? "" : request.getParameter("expected_startTime");
            bank_end_date = Functions.checkStringNull(request.getParameter("expected_endDate")) == null ? "" : request.getParameter("expected_endDate");
            bank_end_time = Functions.checkStringNull(request.getParameter("expected_endTime")) == null ? "" : request.getParameter("expected_endTime");
            processing_amount = Functions.checkStringNull(request.getParameter("processingamount")) == null ? "" : request.getParameter("processingamount");
            grossAmount = Functions.checkStringNull(request.getParameter("grossamount")) == null ? "" : request.getParameter("grossamount");
            netfinal_amount = Functions.checkStringNull(request.getParameter("netfinalamout")) == null ? "" : request.getParameter("netfinalamout");
            unpaid_amount = Functions.checkStringNull(request.getParameter("unpaidamount")) == null ? "" : request.getParameter("unpaidamount");
            isrollingreservereleasewire = Functions.checkStringNull(request.getParameter("isrollingreservereleasewire")) == null ? "" : request.getParameter("isrollingreservereleasewire");
            rollingReleaseStartdate = Functions.checkStringNull(request.getParameter("rollingreserveStartdate")) == null ? "" : request.getParameter("rollingreserveStartdate");
            rollingRelease_time_start = Functions.checkStringNull(request.getParameter("rollingReleaseTimeStart")) == null ? "" : request.getParameter("rollingReleaseTimeStart");
            rollingreservereleasedateupto = Functions.checkStringNull(request.getParameter("rollingreservedateupto")) == null ? "" : request.getParameter("rollingreservedateupto");
            rollingreservecovered_time = Functions.checkStringNull(request.getParameter("rollingRelease_Time")) == null ? "" : request.getParameter("rollingRelease_Time");
            declinedcoveredStartdate = Functions.checkStringNull(request.getParameter("declinedcoveredStartdate")) == null ? "" : request.getParameter("declinedcoveredStartdate");
            declinecovered_time_start = Functions.checkStringNull(request.getParameter("declinedcoveredtimeStart")) == null ? "" : request.getParameter("declinedcoveredtimeStart");
            declinedcoveredupto = Functions.checkStringNull(request.getParameter("declinedcoveredupto")) == null ? "" : request.getParameter("declinedcoveredupto");
            declinecovered_time = Functions.checkStringNull(request.getParameter("declinedcoveredtime")) == null ? "" : request.getParameter("declinedcoveredtime");
            chargebackcoveredStartdate = Functions.checkStringNull(request.getParameter("chargebackcovereduptoStartdate")) == null ? "" : request.getParameter("chargebackcovereduptoStartdate");
            chargebackcovered_time_start = Functions.checkStringNull(request.getParameter("chargebackcoveredtimeStart")) == null ? "" : request.getParameter("chargebackcoveredtimeStart");
            chargebackcoveredupto = Functions.checkStringNull(request.getParameter("chargebackcoveredupto")) == null ? "" : request.getParameter("chargebackcoveredupto");
            chargebackcovered_time = Functions.checkStringNull(request.getParameter("chargebackcoveredtime")) == null ? "" : request.getParameter("chargebackcoveredtime");
            reversedCoveredUptoStartdate = Functions.checkStringNull(request.getParameter("reversedcovereduptoStartdate")) == null ? "" : request.getParameter("reversedcovereduptoStartdate");
            reversecovered_time_start = Functions.checkStringNull(request.getParameter("reversedcoveredtimeStart")) == null ? "" : request.getParameter("reversedcoveredtimeStart");
            reversedCoveredUpto = Functions.checkStringNull(request.getParameter("reversedcoveredupto")) == null ? "" : request.getParameter("reversedcoveredupto");
            reversecovered_time = Functions.checkStringNull(request.getParameter("reversedcoveredtime")) == null ? "" : request.getParameter("reversedcoveredtime");
            banksettlement_report_file = Functions.checkStringNull(request.getParameter("settlement_report_file")) == null ? "" : request.getParameter("settlement_report_file");
            banksettlement_transaction_file = Functions.checkStringNull(request.getParameter("settlement_transaction_file")) == null ? "" : request.getParameter("settlement_transaction_file");
            /*String disable1 ="";
            String style1 ="";
            if(functions.isValueNull(parent_bankwireId))
            {
                disable1 ="readonly";
                style1 = "pointer-events:none; cursor:auto; opacity:0.6";
            }*/

            if(bankWireManagerVO!=null)
            {
                if (functions.isValueNull(parent_bankwireId))
                {
                    if(functions.isValueNull(bankWireManagerVO.getParent_bankwireid()) && parent_bankwireId.equals(bankWireManagerVO.getParent_bankwireid()))
                    {
                        String style = "style = 'pointer-events:none; cursor:auto;'";
                        disable ="readonly "+style;
                    }
                    if(functions.isValueNull(bankWireManagerVO.getBankwiremanagerId()) && parent_bankwireId.equals(bankWireManagerVO.getBankwiremanagerId()))
                    {
                        String style = "style = 'pointer-events:none; cursor:auto;'";
                        disable ="readonly "+style;
                    }
                    if(functions.isValueNull(bankWireManagerVO.getProcessing_amount()))
                        processing_amount= bankWireManagerVO.getProcessing_amount();
                    if (functions.isValueNull(bankWireManagerVO.getGrossAmount()))
                        grossAmount= bankWireManagerVO.getGrossAmount();
                    if (functions.isValueNull(bankWireManagerVO.getNetfinal_amount()))
                        netfinal_amount= bankWireManagerVO.getNetfinal_amount();
                    if(functions.isValueNull(bankWireManagerVO.getUnpaid_amount()))
                        unpaid_amount= bankWireManagerVO.getUnpaid_amount();
                    if (functions.isValueNull(bankWireManagerVO.getIsrollingreservereleasewire()))
                        isrollingreservereleasewire= bankWireManagerVO.getIsrollingreservereleasewire();
                    if (functions.isValueNull(bankWireManagerVO.getIspaid()))
                        ispaid= bankWireManagerVO.getIspaid();

                    if (functions.isValueNull(bankWireManagerVO.getBank_end_date()))
                        bank_end_date = bankWireManagerVO.getBank_end_date();
                    if (functions.isValueNull(bankWireManagerVO.getServer_end_date()))
                        server_end_date = bankWireManagerVO.getServer_end_date();
                    if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredupto()))
                        declinedcoveredupto = bankWireManagerVO.getDeclinedcoveredupto();
                    if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredupto()))
                        chargebackcoveredupto = bankWireManagerVO.getChargebackcoveredupto();
                    if (functions.isValueNull(bankWireManagerVO.getReversedCoveredUpto()))
                        reversedCoveredUpto = bankWireManagerVO.getReversedCoveredUpto();
                    if (functions.isValueNull(bankWireManagerVO.getRollingreservereleasedateupto()))
                        rollingreservereleasedateupto = bankWireManagerVO.getRollingreservereleasedateupto();

                    if (functions.isValueNull(bankWireManagerVO.getBank_start_date()))
                        bank_start_date = bankWireManagerVO.getBank_start_date();
                    if (functions.isValueNull(bankWireManagerVO.getServer_start_date()))
                        server_start_date = bankWireManagerVO.getServer_start_date();
                    if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredStartdate()))
                        declinedcoveredStartdate = bankWireManagerVO.getDeclinedcoveredStartdate();
                    if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredStartdate()))
                        chargebackcoveredStartdate = bankWireManagerVO.getChargebackcoveredStartdate();
                    if (functions.isValueNull(bankWireManagerVO.getReversedCoveredStartdate()))
                        reversedCoveredUptoStartdate = bankWireManagerVO.getReversedCoveredStartdate();
                    if (functions.isValueNull(bankWireManagerVO.getRollingreservereleaseStartdate()))
                        rollingReleaseStartdate = bankWireManagerVO.getRollingreservereleaseStartdate();

                    if (functions.isValueNull(bankWireManagerVO.getBank_start_timestamp()))
                        bank_start_time = bankWireManagerVO.getBank_start_timestamp();
                    if (functions.isValueNull(bankWireManagerVO.getServer_start_timestamp()))
                        server_start_time = bankWireManagerVO.getServer_start_timestamp();
                    if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredtimeStarttime()))
                        declinecovered_time_start = bankWireManagerVO.getDeclinedcoveredtimeStarttime();
                    if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredtimeStarttime()))
                        chargebackcovered_time_start = bankWireManagerVO.getChargebackcoveredtimeStarttime();
                    if (functions.isValueNull(bankWireManagerVO.getReversedcoveredtimeStarttime()))
                        reversecovered_time_start = bankWireManagerVO.getReversedcoveredtimeStarttime();
                    if (functions.isValueNull(bankWireManagerVO.getRollingreservereleaseStarttime()))
                        rollingRelease_time_start = bankWireManagerVO.getRollingreservereleaseStarttime();

                    if (functions.isValueNull(bankWireManagerVO.getBank_end_timestamp()))
                        bank_end_time = bankWireManagerVO.getBank_end_timestamp();
                    if (functions.isValueNull(bankWireManagerVO.getServer_end_timestamp()))
                        server_end_time = bankWireManagerVO.getServer_end_timestamp();
                    if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredtime()))
                        declinecovered_time = bankWireManagerVO.getDeclinedcoveredtime();
                    if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredtime()))
                        chargebackcovered_time = bankWireManagerVO.getChargebackcoveredtime();
                    if (functions.isValueNull(bankWireManagerVO.getReversedcoveredtime()))
                        reversecovered_time = bankWireManagerVO.getReversedcoveredtime();
                    if (functions.isValueNull(bankWireManagerVO.getRollingreservetime()))
                        rollingreservecovered_time = bankWireManagerVO.getRollingreservetime();


                }
                else
                {

                    if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredStartdate()))
                        declinedcoveredStartdate = bankWireManagerVO.getDeclinedcoveredStartdate();
                    if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredStartdate()))
                        chargebackcoveredStartdate = bankWireManagerVO.getChargebackcoveredStartdate();
                    if (functions.isValueNull(bankWireManagerVO.getReversedCoveredStartdate()))
                        reversedCoveredUptoStartdate = bankWireManagerVO.getReversedCoveredStartdate();
                    if (functions.isValueNull(bankWireManagerVO.getRollingreservereleaseStartdate()))
                        rollingReleaseStartdate = bankWireManagerVO.getRollingreservereleaseStartdate();

                    if (functions.isValueNull(bankWireManagerVO.getDeclinedcoveredtimeStarttime()))
                        declinecovered_time_start = bankWireManagerVO.getDeclinedcoveredtimeStarttime();
                    if (functions.isValueNull(bankWireManagerVO.getChargebackcoveredtimeStarttime()))
                        chargebackcovered_time_start = bankWireManagerVO.getChargebackcoveredtimeStarttime();
                    if (functions.isValueNull(bankWireManagerVO.getReversedcoveredtimeStarttime()))
                        reversecovered_time_start = bankWireManagerVO.getReversedcoveredtimeStarttime();
                    if (functions.isValueNull(bankWireManagerVO.getRollingreservereleaseStarttime()))
                        rollingRelease_time_start = bankWireManagerVO.getRollingreservereleaseStarttime();

                }
            }
            request.setAttribute("bankWireManagerVO", bankWireManagerVO);
            session.setAttribute("bankWireManagerVO", bankWireManagerVO);

            ActionVO actionVO = null;
            if (("Add").equals(request.getParameter("submit")))
            {
                actionVO = new ActionVO();
                actionVO.setAdd();
            }
            if (!("Add").equals(request.getParameter("submit")))
            {
                actionVO = (ActionVO) request.getAttribute("actionVO");
                bankwiremanagerId = singleBankWireManagerVO.getBankwiremanagerId();
                if (functions.isValueNull(singleBankWireManagerVO.getParent_bankwireid()))
                    parent_bankwireId= singleBankWireManagerVO.getParent_bankwireid();
                else
                    parent_bankwireId= "";
                settleddate = functions.isValueNull(singleBankWireManagerVO.getSettleddate()) ? singleBankWireManagerVO.getSettleddate():"";
                pgtypeId = singleBankWireManagerVO.getPgtypeId();
                accountId = singleBankWireManagerVO.getAccountId();
                mid = singleBankWireManagerVO.getMid();
                bank_start_date = singleBankWireManagerVO.getBank_start_date();
                bank_end_date = singleBankWireManagerVO.getBank_end_date();
                server_start_date = singleBankWireManagerVO.getServer_start_date();
                server_end_date = singleBankWireManagerVO.getServer_end_date();
                processing_amount = singleBankWireManagerVO.getProcessing_amount();
                grossAmount = singleBankWireManagerVO.getGrossAmount();
                netfinal_amount = singleBankWireManagerVO.getNetfinal_amount();
                unpaid_amount = singleBankWireManagerVO.getUnpaid_amount();
                currency = singleBankWireManagerVO.getCurrency();
                isrollingreservereleasewire = singleBankWireManagerVO.getIsrollingreservereleasewire();
                rollingreservereleasedateupto = singleBankWireManagerVO.getRollingreservereleasedateupto();
                declinedcoveredupto = singleBankWireManagerVO.getDeclinedcoveredupto();
                chargebackcoveredupto = singleBankWireManagerVO.getChargebackcoveredupto();
                reversedCoveredUpto = singleBankWireManagerVO.getReversedCoveredUpto();
                banksettlement_report_file = singleBankWireManagerVO.getBanksettlement_report_file();
                banksettlement_transaction_file = singleBankWireManagerVO.getBanksettlement_transaction_file();
                isSettlementCronExceuted = singleBankWireManagerVO.getSettlementCronExceuted();
                isPayoutCronExcuted = singleBankWireManagerVO.getPayoutCronExcuted();
                ispaid = singleBankWireManagerVO.getIspaid();
                bank_start_time = singleBankWireManagerVO.getBank_start_timestamp();
                bank_end_time = singleBankWireManagerVO.getBank_end_timestamp();
                server_start_time = singleBankWireManagerVO.getServer_start_timestamp();
                server_end_time = singleBankWireManagerVO.getServer_end_timestamp();
                settled_time = singleBankWireManagerVO.getSettled_timestamp();
                rollingreservecovered_time = singleBankWireManagerVO.getRollingreservetime();
                declinecovered_time = singleBankWireManagerVO.getDeclinedcoveredtime();
                reversecovered_time = singleBankWireManagerVO.getReversedcoveredtime();
                chargebackcovered_time = singleBankWireManagerVO.getChargebackcoveredtime();
                ispartnerpommcronexecuted = singleBankWireManagerVO.getIsPartnerCommCronExecuted();
                isagentcommcronexecuted = singleBankWireManagerVO.getIsAgentCommCronExecuted();

                if(functions.isValueNull(singleBankWireManagerVO.getBank_start_date())){
                    bank_start_date = singleBankWireManagerVO.getBank_start_date();
                }
                else{
                    bank_start_date="";
                }
                if(functions.isValueNull(singleBankWireManagerVO.getBank_end_date())){
                    bank_end_date = singleBankWireManagerVO.getBank_end_date();
                }
                else{
                    bank_end_date="";
                }
                if(functions.isValueNull(singleBankWireManagerVO.getDeclinedcoveredStartdate())){
                    declinedcoveredStartdate = singleBankWireManagerVO.getDeclinedcoveredStartdate();
                }
                else{
                    declinedcoveredStartdate="";
                }

                if(functions.isValueNull(singleBankWireManagerVO.getReversedCoveredStartdate())){
                    reversedCoveredUptoStartdate = singleBankWireManagerVO.getReversedCoveredStartdate();
                }
                else{
                    reversedCoveredUptoStartdate="";
                }

                if(functions.isValueNull(singleBankWireManagerVO.getChargebackcoveredStartdate())){
                    chargebackcoveredStartdate = singleBankWireManagerVO.getChargebackcoveredStartdate();
                }
                else{
                    chargebackcoveredStartdate="";
                }

                if(functions.isValueNull(singleBankWireManagerVO.getRollingreservereleaseStartdate())){
                    rollingReleaseStartdate=singleBankWireManagerVO.getRollingreservereleaseStartdate();
                }
                else{
                    rollingReleaseStartdate="";
                }

                if(functions.isValueNull(singleBankWireManagerVO.getBank_start_timestamp())){
                    bank_start_time=singleBankWireManagerVO.getBank_start_timestamp();
                }
                else{
                    bank_start_time="";
                }if(functions.isValueNull(singleBankWireManagerVO.getBank_end_timestamp())){
                bank_end_time=singleBankWireManagerVO.getBank_end_timestamp();
            }
            else{
                bank_end_time="";
            }if(functions.isValueNull(singleBankWireManagerVO.getDeclinedcoveredtimeStarttime())){
                declinecovered_time_start=singleBankWireManagerVO.getDeclinedcoveredtimeStarttime();
            }
            else{
                declinecovered_time_start="";
            }
                if(functions.isValueNull(singleBankWireManagerVO.getChargebackcoveredtimeStarttime())){
                    chargebackcovered_time_start=singleBankWireManagerVO.getChargebackcoveredtimeStarttime();
                }
                else{
                    chargebackcovered_time_start="";
                }
                if(functions.isValueNull(singleBankWireManagerVO.getReversedcoveredtimeStarttime())){
                    reversecovered_time_start=singleBankWireManagerVO.getReversedcoveredtimeStarttime();
                }
                else{
                    reversecovered_time_start="";
                }
                if(functions.isValueNull(singleBankWireManagerVO.getRollingreservereleaseStarttime())){
                    rollingRelease_time_start=singleBankWireManagerVO.getRollingreservereleaseStarttime();
                }
                else{
                    rollingRelease_time_start="";
                }

                session.setAttribute("singleBankWireManagerVO", singleBankWireManagerVO);
                request.setAttribute("singleBankWireManagerVO", singleBankWireManagerVO);
            }

    %>
    <form action="/icici/servlet/UpdateBankWireManager?ctoken=<%=ctoken%>" method="post" name="f1">
        <input type="hidden" name="declinedcoveredStartdate" value="<%=declinedcoveredStartdate%>">
        <input type="hidden" name="declinedcoveredtimeStart" value="<%=declinecovered_time_start%>">
        <input type="hidden" name="chargebackcovereduptoStartdate" value="<%=chargebackcoveredStartdate%>">
        <input type="hidden" name="chargebackcoveredtimeStart" value="<%=chargebackcovered_time_start%>">
        <input type="hidden" name="reversedcovereduptoStartdate" value="<%=reversedCoveredUptoStartdate%>">
        <input type="hidden" name="reversedcoveredtimeStart" value="<%=reversecovered_time_start%>">

        <table align=center width="80%" border="0">
            <tr>
                <td colspan="3"><center><B>
                    <%
                        if (actionVO.isView())
                        {
                    %>
                    View Bank Wire Manager
                    <%
                        }
                        if (actionVO.isEdit())
                        {
                            if (functions.isValueNull(parent_bankwireId)){
                            String style = "style = 'pointer-events:none; cursor:auto;'";
                            disable ="readonly "+style;}
                    %>
                    Edit Bank Wire Manager
                    <%
                        }
                        if(actionVO.isAdd())
                        {
                    %>
                    Add new Bank Wire
                    <%
                        }
                    %>
                </B></center> </td>
            </tr>
            <%
                if(!actionVO.isAdd())
                {
            %>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <input type="hidden" name="bankwiremangerid" value="<%=bankwiremanagerId%>">
                <td valign="middle" align="center" class="td0">Bank&nbsp;Wire&nbsp;Id</td> <td class="textb"><input type="text" name="bankwiremangerid1" value="<%=bankwiremanagerId%>"  disabled="">
            </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Settled&nbsp;Date</td> <td class="textb"><input type="text"class="datepicker"  <%=disable%> readonly  name="settlementdate" value="<%=settleddate%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%></td>
                <td class="textb">Time:<input type="text" <%=disable%> class="bootstrap-datetimepicker-widget" name="settlementtime" value="<%=settled_time%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <input type="hidden" name="accountid" value="<%=accountId%>">
                <td valign="middle" align="center" class="td0">Account&nbsp;Id</td>
                <td class="textb"><input type="text" value="<%=accountId%>" disabled=""></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <input type="hidden" name="parent_bankwireId" value="<%=parent_bankwireId%>">
                <td valign="middle" align="center" class="td0">ParentBankWireId</td>
                <td class="textb"><input type="text" value="<%=parent_bankwireId%>" disabled=""></td>
            </tr>
            <%
            }
            else if(actionVO.isAdd())
            {
            %>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0" >
                <td valign="middle" align="center">Gateway</td>
                <td class="textb" colspan="2">
                    <input name="pgtypeid" id="pgtypeid" value="<%=pgtypeid%>" class="txtbox" autocomplete="on" onblur="return getAccountDetails('<%=ctoken%>')">
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0" >
                <td valign="middle" align="center">AccountId</td>
                <td class="textb" colspan="2">
                    <%--<input name="accountid" id="accid" value="<%=accountid1%>" class="txtbox" autocomplete="on" onblur="return getAccountDetails('<%=ctoken%>')">--%>

                    <select id="accountidList" size="1" class="multiselect txtboxsmall" multiple="multiple" style="width:186px"  <%--onBlur="return getAccountDetails('<%=ctoken%>')"--%>>
                        <%

                            String[] pgTypeId = null;
                            if (functions.isValueNull(pgtypeid)){
                                pgTypeId = pgtypeid.split("-");
                            }
                            Connection conn = null;
                            PreparedStatement pstmt = null;
                            ResultSet rs = null;
                            try
                            {
                                conn = Database.getConnection();
                                StringBuilder  query = new StringBuilder("SELECT DISTINCT gapm.accountid,ga.merchantid,ga.pgtypeid,gt.currency FROM gateway_account_partner_mapping AS gapm, gateway_accounts AS ga, gateway_type AS gt WHERE gapm.accountid=ga.accountid AND ga.pgtypeid = gt.pgtypeid");
                                if (functions.isValueNull(pgtypeid)){
                                    query.append(" and ga.pgtypeid ="+pgTypeId[2]);
                                }
                                query.append(" ORDER  BY gapm.accountid ASC");
                                pstmt = conn.prepareStatement( query.toString() );
                                rs = pstmt.executeQuery();
                                while (rs.next())
                                {
                                    out.println("<option value=\""+rs.getString("gapm.accountid")+"\">"+rs.getString("gapm.accountid")+" - "+rs.getString("ga.merchantid")+" - "+rs.getString("gt.currency")+"</option>");
                                }
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception"+e);
                            }
                            finally
                            {
                                Database.closeConnection(conn);
                            }
                        %>
                    </select>
                    <input type="hidden" id="accid" name="accountid" value="<%=accountid1%>">
                    <input type="hidden" id="hiddenaccid"  value="<%=accountid1%>">
                        <button type="button" class="btn" onclick="return getAccountDetails('<%=ctoken%>')"->
                            <i class="fa fa-arrow-circle-right fa-lg"></i></button>

                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0" >
                <td valign="middle" align="center">Parent BankwireID</td>
                <td class="textb" colspan="2">
                    <input name="parent_bankwireId" id="parent_bankwireId" value="<%=parent_bankwireId%>" class="txtbox" autocomplete="on" onblur="return getBankDetails('<%=ctoken%>')">
                </td>
            </tr>
            <%
                }
            %>

            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Bank&nbsp;Start&nbsp;Date</td>
                <td width="">
                    <input type="text" class="datepicker" readonly name="expected_startDate" <%=disable%> value="<%=bank_start_date%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                </td>
                <td valign="middle">Time:
                    <input type="text" class="bootstrap-datetimepicker-widget" name="expected_startTime" <%=disable%> value="<%=bank_start_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Bank&nbsp;End&nbsp;Date</td>
                <td class="textb"><input type="text" class="datepicker" readonly name="expected_endDate" <%=disable%> value="<%=bank_end_date%>"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%></td>
                <td>Time:
                    <input type="text" placeholder="HH:MM:SS" name="expected_endTime" <%=disable%> value="<%=bank_end_time%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <%
                if(!actionVO.isAdd())
                {
            %>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">Server&nbsp;Start&nbsp;Date</td>
                <td class="textb">
                    <input type="text" class="datepicker" readonly name="actual_startDate" <%=disable%> value="<%=server_start_date%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                </td>
                <td align="left">Time:
                    <input type="text" placeholder="HH:MM:SS"  name="actual_startTime" <%=disable%> value="<%=server_start_time%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">Server&nbsp;End&nbsp;Date</td>
                <td class="textb"><input type="text" class="datepicker" readonly name="actual_endDate" <%=disable%> value="<%=server_end_date%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%></td>
                <td>Time:<input type="text"  name="actual_endTime" <%=disable%> value="<%=server_end_time%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <%
                }
            %>
            <tr class="tr0" >
                <td  valign="middle" align="center" class="td0">Processing&nbsp;Amount</td>
                <td class="textb"colspan="3">
                    <input type="text" name="processingamount"  value="<%=processing_amount%>" <%if (actionVO.isView()){%> disabled="true"><%}%>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">Gross&nbsp;Amount</td>
                <td class="textb" colspan="3">
                    <input type="text" name="grossamount"  value="<%=grossAmount%>" <%if (actionVO.isView()){%> disabled="true"><%}%>
                </td>
            </tr>
            <tr><td>&nbsp;</td>
            </tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Net&nbsp;Final&nbsp;Amount</td>
                <td class="textb" colspan="3">
                    <input type="text" name="netfinalamout"  value="<%=netfinal_amount%>" <%if (actionVO.isView()){%> disabled><%}%>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Unpaid&nbsp;Amount</td>
                <td class="textb" colspan="3">
                    <input type="text" name="unpaidamount" value="<%=unpaid_amount%>" <%if (actionVO.isView()){%> disabled><%}%></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">isrollingreservereleasewire</td>
                <td class="textb" colspan="3">
                    <select type="text" name="isrollingreservereleasewire"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                        <%
                            for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                            {
                                selected="";
                                if(yesNoPair.getKey().equals(isrollingreservereleasewire))
                                {
                                    selected="selected";
                                }
                                out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                            }
                        %>
                    </select>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Rolling&nbsp;Reserve&nbsp;Start Date</td>
                <td class="textb"><input type="text" class="datepicker" <%=disable%>  name="rollingreserveStartdate" value="<%=rollingReleaseStartdate%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
                <td>Time:
                    <input type="text"  placeholder="HH:MM:SS" <%=disable%> class="" name="rollingReleaseTimeStart"  value="<%=rollingRelease_time_start%>" <%if(actionVO.isView()){%> disabled<%}%>></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Rolling&nbsp;Reserve&nbsp;DateUpto</td>
                <td class="textb"><input type="text" class="datepicker" <%=disable%>  name="rollingreservedateupto" value="<%=rollingreservereleasedateupto%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
                <td>Time:
                    <input type="text"  placeholder="HH:MM:SS" <%=disable%> class="" name="rollingRelease_Time" value="<%=rollingreservecovered_time%>" <%if (actionVO.isView()){%> disabled<%}%>> </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Declined&nbsp;Covered&nbsp;Start Date</td> <td class="textb"><input type="text" class="datepicker" readonly style="pointer-events: none;cursor: auto;" name="declinedcoveredStartdate" value="<%=declinedcoveredStartdate%>" <%if (actionVO.isView()){%> disabled<%}%> ></td>
                <td>Time:
                    <input type="text" placeholder="HH:MM:SS" <%=disable%> class="" readonly style="pointer-events: none;cursor: auto;" name="declinedcoveredtimeStart" value="<%=declinecovered_time_start%>" <%if (actionVO.isView()){%> disabled<%}%> ></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Declined&nbsp;Covered&nbsp;DateUpto</td> <td class="textb"><input type="text" class="datepicker" <%=disable%> readonly name="declinedcoveredupto" value="<%=declinedcoveredupto%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
                <td>Time:
                    <input type="text"  placeholder="HH:MM:SS" <%=disable%>  class="" name="declinedcoveredtime" value="<%=declinecovered_time%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Chargeback&nbsp;Covered&nbsp;Start Date</td> <td class="textb"><input type="text" <%=disable%> class="datepicker" readonly style="pointer-events: none;cursor: auto;" name="chargebackcovereduptoStartdate" value="<%=chargebackcoveredStartdate%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
                <td>Time:
                    <input type="text" placeholder="HH:MM:SS" class="" <%=disable%> readonly style="pointer-events: none;cursor: auto;" name="chargebackcoveredtimeStart" value="<%=chargebackcovered_time_start%>" <%if (actionVO.isView()){%> disabled<%}%> ></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Chargeback&nbsp;Covered&nbsp;DateUpto</td> <td class="textb"><input type="text"  <%=disable%> class="datepicker" readonly name="chargebackcoveredupto" value="<%=chargebackcoveredupto%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
                <td>Time:
                    <input type="text" placeholder="HH:MM:SS" class="" <%=disable%> name="chargebackcoveredtime" value="<%=chargebackcovered_time%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Reversed&nbsp;Covered&nbsp;Start Date</td> <td class="textb"><input type="text" <%=disable%> class="datepicker" readonly style="pointer-events: none;cursor: auto;" name="reversedcovereduptoStartdate" value="<%=reversedCoveredUptoStartdate%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
                <td>Time:
                    <input type="text" placeholder="HH:MM:SS" class="" readonly style="pointer-events: none;cursor: auto;" <%=disable%> name="reversedcoveredtimeStart" value="<%=reversecovered_time_start%>" <%if (actionVO.isView()){%> disabled<%}%> ></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">Reversed&nbsp;Covered&nbsp;DateUpto</td> <td class="textb"><input type="text"  <%=disable%> class="datepicker"  name="reversedcoveredupto" value="<%=reversedCoveredUpto%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
                <td>Time:
                    <input type="text" placeholder="HH:MM:SS" class="" <%=disable%>  name="reversedcoveredtime" value="<%=reversecovered_time%>" <%if (actionVO.isView()){%> disabled<%}%>></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">Bank&nbsp;Report&nbsp;File&nbsp;(.xls,.pdf)</td>
                <td class="textb" colspan="3">
                    <input type="text" name="settlement_report_file"  value="<%=banksettlement_report_file%>" <%if (actionVO.isView()){%>disabled="true"><%}%>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">Bank&nbsp;Transaction&nbsp;File&nbsp;(.xls,.pdf)</td>
                <td class="textb" colspan="3">
                    <input type="text" name="settlement_transaction_file"  value="<%=banksettlement_transaction_file%>" <%if (actionVO.isView()){%> disabled="true"><%}%></td>
            </tr>

            <%
                if(!actionVO.isAdd())
                {
            %>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">isSettlementCronExecuted</td>
                <td class="textb">
                    <select type="text" name="issettlementcron"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                        <%
                            for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                            {
                                selected="";
                                if(yesNoPair.getKey().equals(isSettlementCronExceuted))
                                {
                                    selected="selected";
                                }
                                out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                            }
                        %>
                    </select>
                </td>
            </tr>

            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">isPayoutCronExecuted</td> <td class="textb"><select type="text" name="ispayoutcron"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                <%
                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                    {
                        selected="";
                        if(yesNoPair.getKey().equals(isPayoutCronExcuted))
                        {
                            selected="selected";
                        }
                        out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                    }
                %>
            </select>
            </td>
            </tr>

            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">isPartnerCommCronExecuted</td><td class="textb">
                <select type="text" name="ispartnercommcronexecuted" <%if (actionVO.isView()){%> disabled><%}else{out.print(">");}%>>
                        <%
            for(Map.Entry<String ,String> yesNoPair : dropdown.entrySet())
            {
                selected="";
                if(yesNoPair.getKey().equals(ispartnerpommcronexecuted))
                {
                    selected="selected";
                }
                out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
            }
            %>
            </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
            </tr>
            <tr class="tr0">
                <td valign="middle" align="center" class="td0">isAgentCommCronExecuted</td>
                <td class="textb">
                    <select type="text" name="isagentcommcronexecuted" <%if (actionVO.isView()){%> disabled><%}else{out.print(">");}%>>
                            <%
            for(Map.Entry<String ,String> yesNoPair : dropdown.entrySet())
            {
                selected="";
                if(yesNoPair.getKey().equals(isagentcommcronexecuted))
                {
                    selected="selected";

                }
                out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
            }
            %>
                </td>
            </tr>
            <%
                }
            %>

            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">isPaid</td>
                <td colspan="3">
                    <select type="text" name="isPaid"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                        <%
                            for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                            {
                                selected="";
                                if(yesNoPair.getKey().equals(ispaid))
                                {
                                    selected="selected";
                                }
                                out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                            }
                        %>
                    </select>
                </td>
            </tr>

            <%
                if(actionVO.isAdd())
                {
            %>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="td0">Day Light Saving</td>
                <td colspan="3">
                    <select type="text" name="isdaylight">
                        <%
                            for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                            {
                                selected="";
                                if(yesNoPair.getKey().equals("N"))
                                {
                                    selected="selected";
                                }
                                out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                            }
                        %>
                    </select>
                </td>
            </tr>

            <%
                }
                if(actionVO.isEdit())
                {
            %>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td colspan="3" valign="middle" align="center"  ><button type="submit" class="button" name="action" value="<%=singleBankWireManagerVO.getBankwiremanagerId()%>_Update">Update</button></td>
            </tr>

            <%
                }
            %>
            <%
                if(actionVO.isAdd())
                {
            %>
            <tr class="tr0">
                <td colspan="3" valign="middle" align="center"  ><input type="hidden" name="submit" value="Add"><button type="submit" class="button" name="action" id="submit_button" value="1_Add">Add</button></td>
            </tr>
            <%
                }
            %>
        </table>
    </form>
    <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Filter","Fill in the required data for Bank WireManager List"));
                }
            }
        }
        catch (Exception e)
        {
            logger.error(" bankWireManager.jsp error::",e);
        }
    %>
</div>
<script type="text/javascript">

    var value = [];
    var details = '<%=accountid1%>';
    value = details.split(",");
    for (var i in value)
    {
        $("#accountidList option[value='" + value[i] + "']").prop('selected', true);
    }
</script>
</body>
</html>