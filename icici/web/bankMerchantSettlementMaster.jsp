<%@ page import="com.directi.pg.Admin" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.GatewayManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.manager.vo.BankMerchantSettlementVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.gatewayVOs.GatewayAccountVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 9/10/14
  Time: 3:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<html>
<head>
    <%--Datepicker css format--%>
    <link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <%--<script src="/icici/olddatepicker1/jquery-ui.js"></script>--%>
    <link rel="stylesheet" href="/resources/demos/style.css">
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'dd/mm/yy'});
        });
    </script>


        <script src="/icici/css/jquery-ui.min.js"></script>
        <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

      <%--  <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>--%>
        <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>
    <title> Bank Description Manager> Bank Merchant Settlement Master</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (Admin.isLoggedIn(session))
    {
        String str2 = "";
        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
      //  String accountid = Functions.checkStringNull(request.getParameter("accountid"));
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");

        String gateway = Functions.checkStringNull(request.getParameter("gateway"));
        if (gateway == null)
        {gateway = "";}

        TerminalManager terminalManager = new TerminalManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();

       /* List<String> gatewayCurrency = GatewayTypeService.loadCurrency();
        List<String> gatwayName = GatewayTypeService.loadGateway();
        Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();
*/
        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";
        if(currency!=null)str2 = str2 + "&currency=" + currency;
        else
            currency="";
        if(accountid2!=null)str2 = str2 + "&accountid=" + accountid2;
        else
            accountid2="";

        String accountid = Functions.checkStringNull(request.getParameter("accountid"));
        String memberid = Functions.checkStringNull(request.getParameter("toid")) == null ? "" : request.getParameter("toid");

        TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();


        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }


%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Bank Merchant Settlement Manager
                <%--<div style="float: right;">
                    <form action="/icici/bankMerchantSettlementMaster.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button class="addnewmember" type="submit" value="Add" name="submit" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Merchant Settlement
                        </button>
                    </form>
                </div>--%>
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
            %>
            <form action="/icici/servlet/BankMerchantSettlementList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%!
                    Logger logger = new Logger("test");
                    MerchantDAO merchantDAO = new MerchantDAO();
                %>
                <%
                    GatewayManager gatewayManager = new GatewayManager();


                    try
                    {
                        List<GatewayAccountVO> gatewayAccountVOList= gatewayManager.getListOfAllGatewayAccount();

                        String selected="";
                        HashMap<String,String> dropdown = new HashMap<String, String>();
                        dropdown.put("Y","YES");
                        dropdown.put("N","NO");
                %>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>

                                    <td class="textb">
                                        Gateway&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
                                    </td>
                                    <td>
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox"
                                               autocomplete="on">
                                       <%-- <select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
                                            <option value="0" default>--All--</option>
                                            <%

                                                for(String gatewayType : gatewayTypeTreeMap.keySet())
                                                {
                                                    String isSelected = "";

                                                    if(gatewayType.equalsIgnoreCase(pgtypeid))
                                                    {
                                                        isSelected = "selected";
                                                    }

                                            %>
                                            <option value="<%=gatewayType%>" <%=isSelected%>><%=gatewayType%></option>
                                            <%
                                                }
                                            %>

                                        </select>--%>
                                    </td>

                                    <%--<td width="4%" class="textb">&nbsp;</td>--%>
                                    <td width="8%" class="textb" align="center">Account ID:&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="accountid" id="accountid1"
                                               value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>"
                                               class="txtbox" autocomplete="on">
                                    <%-- <select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
                                            <option data-bank="all" value="0"></option>
                                            <%

                                                for(Integer sAccid : accountDetails.keySet())
                                                {
                                                    GatewayAccount g = accountDetails.get(sAccid);
                                                    String isSelected = "";
                                                    String gateway2 = g.getGateway().toUpperCase();
                                                    String currency2 = g.getCurrency();
                                                    String pgtype = g.getPgTypeId();

                                                    if (String.valueOf(sAccid).equalsIgnoreCase(accountid))
                                                        isSelected = "selected";
                                                    else
                                                        isSelected = "";

                                                    System.out.println("is selected value--"+isSelected);

                                            %>
                                            <option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                                            <%

                                                }
                                            %>
                                        </select>--%>
                                    </td>

                                       <%-- <td width="12%" class="textb">&nbsp;</td>--%>
                                    <td width="4%" class="textb">Member ID:</td>
                                    <td width="12%" class="textb">
                                        <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox"
                                               autocomplete="on">
                                    <%--<select name="toid" id="memberid" class="txtbox" style="width:200px;">
                                            <option data-bank="all"  data-accid="all" value="0" selected>--All--</option>
                                            <%

                                                for(String sMemberId: memberMap.keySet())
                                                {

                                                    TerminalVO t = memberMap.get(sMemberId);
                                                    String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                                    String accId = t.getAccountId();
                                                    String aContactPerson = t.getContactPerson();
                                                    String aCompanyName = t.getCompany_name();
                                                    String gateway2 = t.getGateway().toUpperCase();
                                                    String currency2 = t.getCurrency();
                                                    String pgtype = t.getGateway_id();
                                                    String value = gateway2+"-"+currency2+"-"+pgtype;
                                                    String isSelected = "";
                                                    if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid+"-"+accId))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                                    else
                                                    {
                                                        isSelected = "";
                                                    }

                                            %>
                                            <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aCompanyName%></option>
                                            <%

                                                }
                                            %>
                                        </select>--%>
                                    </td>

                                    </tr>

                              <tr><td>&nbsp;&nbsp;&nbsp;</td></tr>
                                <tr>

                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" ></td>
                                    <td width="3%" class="textb"><button type="submit" class="buttonform" <%--style="margin-left: 100%"--%> >
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Search
                                    </button></td>
                                    <%--<td width="10%" class="textb">--%>

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

        List<BankMerchantSettlementVO> bankMerchantSettlementVOList = (List<BankMerchantSettlementVO>) request.getAttribute("BankMerchantSettlementVOs");
        if(bankMerchantSettlementVOList !=null)
        {
            if(bankMerchantSettlementVOList.size()>0)
            {
                PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

                int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
    %>
    <form action="/icici/servlet/ViewOrEditBankMerchantSettlement?ctoken=<%=ctoken%>" method="post">
        <center><h4 class="textb"><b>Bank Merchant Settlement List</b></h4></center>
        <table align=center width="100%" border="1" class="table table-striped table-bordered table-green dataTable">

            <tr>
                <td width="2%"valign="middle" align="center" class="th0">Sr&nbsp;No</td>
                <td width="15%" valign="middle" align="center" class="th0">MemberId</td>
                <td width="15%" valign="middle" align="center" class="th0">AccountId</td>
                <td width="25%" valign="middle" align="center" class="th0">Bank&nbsp;Received&nbsp;Settlement&nbsp;Id</td>
                <td width="15%" valign="middle" align="center" class="th0">Is&nbsp;Paid</td>
                <td colspan="20%" valign="middle" align="center" class="th0" >Action</td>
            </tr>
            <%
                for(BankMerchantSettlementVO bankMerchantSettlementVO : bankMerchantSettlementVOList)
                {

            %>
            <tr>

                <td class="tr0" align="center"><%=srno%></td>
                <td class="tr0" align="center"><%=bankMerchantSettlementVO.getMemberId()%></td>
                <td class="tr0" align="center"><%=bankMerchantSettlementVO.getAccountId()%></td>
                <td class="tr0" align="center"><%=bankMerchantSettlementVO.getBankReceivedSettlementId()%></td>
                <td class="tr0" align="center"><%=bankMerchantSettlementVO.getPaid()%></td>
                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=bankMerchantSettlementVO.getBankMerchantSettlementId()+"_View"%>" name="action">View</button>
                </td>
                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=bankMerchantSettlementVO.getBankMerchantSettlementId()+"_Edit"%>" name="action">Edit</button>
                </td>
            </tr>

            <%
                    srno++;
                }
            %>
        </table>
    </form>
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
        //action specific code
        BankMerchantSettlementVO singleBankMerchantSettlementVO= (BankMerchantSettlementVO) request.getAttribute("singleBankMerchantSettlementVO");
        if(request.getParameter("UPDATE")!=null)
        {
            String update=request.getParameter("UPDATE");
            if(update.equals("Success"))
            {
                out.println("<center><font class=\"textb\">Bank Merchant settlement updated</font></center>");
            }
        }
        if(singleBankMerchantSettlementVO!=null || ("Add").equals(request.getParameter("submit")))
        {

            String bankMerchantSettlementId = "";
            String bankSettlementReceivedId = "";
            String accountId = "";
            String merchantId = "";
            String isPaid="N";
            ActionVO actionVO = null;
            if (("Add").equals(request.getParameter("submit")))
            {
                actionVO= new ActionVO();
                actionVO.setAdd();
            }
            if (!("Add").equals(request.getParameter("submit")))
            {
                actionVO = (ActionVO) request.getAttribute("actionVO");
                bankMerchantSettlementId= singleBankMerchantSettlementVO.getBankMerchantSettlementId();
                bankSettlementReceivedId = singleBankMerchantSettlementVO.getBankReceivedSettlementId();

                accountId = singleBankMerchantSettlementVO.getAccountId();
                //System.out.println("accountid---->"+accountId);
                merchantId = singleBankMerchantSettlementVO.getMemberId();
                isPaid=singleBankMerchantSettlementVO.getPaid();


                session.setAttribute("singleBankMerchantSettlementVO",singleBankMerchantSettlementVO);
            }

    %>
    <form action="/icici/servlet/UpdateBankMerchantSettlement?ctoken=<%=ctoken%>" method="post">
        <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member2.js"></script>
        <table align=center width="80%">
            <tr>
                <td colspan="2"><center><B><%if (actionVO.isView())
                {%>
                    View Bank Merchant Settlement
                    <%
                        }
                        if (actionVO.isEdit())
                        {
                    %>
                    Edit Bank Merchant Settlement
                    <%
                        }
                        if(actionVO.isAdd())
                        {
                    %>
                    Add New Bank Merchant Settlement
                    <%
                        }
                    %>
                </B></center> </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <%if (!actionVO.isAdd())
                {
            %>
            <tr class="tr0">
                <td valign="middle" align="center">Bank&nbsp;Merchant&nbsp;Settlement&nbsp;Id</td> <td><input type="text" class="txtbox" name="bankmerchantid" value="<%=bankMerchantSettlementId%>"  readonly=""></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center">Account&nbsp;Id</td> <td><input type="text" class="txtbox" name="accountid" value="<%=accountId%>"  readonly=""></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td valign="middle" align="center">Member&nbsp;Id</td> <td><input type="text" class="txtbox" name="toid" value="<%=merchantId%>"  readonly=""></td>
            </tr>
            <%
                }
                else
                {
            %>
            <tr><td>&nbsp;</td></tr>
            <tr>
                 <td valign="middle" align="center" class="textb">Gateway</td> <td class="textb">
                     <select size="1" id="bank1" name="pgtypeid" >
                         <option value="0" default>--All--</option>
                         <%

                             for(String gatewayType : gatewayTypeTreeMap.keySet())
                             {
                                 String isSelected = "";

                                 if(gatewayType.equalsIgnoreCase(pgtypeid))
                                 {
                                     isSelected = "selected";
                                 }

                         %>
                         <option value="<%=gatewayType%>" <%=isSelected%>><%=gatewayType%></option>
                         <%
                             }
                         %>
                     </select>
             </td>
             </tr>

            <tr><td>&nbsp;</td></tr>
            <tr>
                <td valign="middle" align="center" class="textb">Account Id</td> <td class="textb">
                <select size="1" id="accountid1" name="accountid">
                    <option data-bank="all" value="0">---Select AccountID---</option>
                    <%

                        for(Integer sAccid : accountDetails.keySet())
                        {
                            GatewayAccount g = accountDetails.get(sAccid);
                            String isSelected = "";
                            String gateway2 = g.getGateway().toUpperCase();
                            String currency2 = g.getCurrency();
                            String pgtype = g.getPgTypeId();


                            if (String.valueOf(sAccid).equals(accountid))
                                isSelected = "selected";
                            else
                                isSelected = "";

                    %>
                    <option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                    <%

                        }
                    %>
                </select>
            </td>
            </tr>

            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="textb">Member Id</td> <td class="textb">
                    <select name="toid" id="memberid1" %>
                        <option data-bank="all"  data-accid="all" value="0" selected>--Select MemberID--</option>
                        <%

                            for(String sMemberId: memberMap.keySet())
                            {

                                TerminalVO t = memberMap.get(sMemberId);
                                String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                String accId = t.getAccountId();
                                String aContactPerson = t.getContactPerson();
                                String aComapnyName = t.getCompany_name();
                                String gateway2 = t.getGateway().toUpperCase();
                                String currency2 = t.getCurrency();
                                String pgtype = t.getGateway_id();
                                String value = gateway2+"-"+currency2+"-"+pgtype;
                                String isSelected = "";
                                if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid+"-"+accId))
                                {
                                    isSelected = "selected";
                                }
                                else
                                {
                                    isSelected = "";
                                }

                        %>
                        <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aComapnyName%></option>
                        <%

                            }
                        %>
                    </select>
            </td>
            </tr>
            <%
                }
            %>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="textb">Bank&nbsp;SettlementCycle&nbsp;Id</td> <td ><input type="text" class="txtbox" name="bankreceivedid"  value="<%=bankSettlementReceivedId%>" <%if (!actionVO.isAdd()){%> readonly=""><%}%></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr class="tr0">
                <td  valign="middle" align="center" class="textb">isPaid</td> <td class="textb"><select type="text" name="isPaid"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                <%
                    for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                    {
                        selected="";
                        if(yesNoPair.getKey().equals(isPaid))
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
            <%

                if (actionVO.isEdit())
                {
            %>
            <tr class="tr0">
                <td colspan="2" valign="middle" align="center"  ><button type="submit" class="buttonform" name="action" value="<%=bankMerchantSettlementId%>_Update">Update</button></td>
            </tr>
            <%}%>
            <%

                if (actionVO.isAdd())
                {
            %>
            <tr class="tr0">
                <td colspan="2" valign="middle" align="center"  ><input type="hidden" name="submit" value="Add"><button type="submit" class="buttonform" name="action" value="1_Add">Add</button></td>
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
                    out.println(Functions.NewShowConfirmation("Filter","Fill in the required data for bank merchant settlement list"));
                }
            }
        }
        catch (Exception e)
        {
            logger.error(" bankMerchantSettlementMaster jsp error::",e);
        }
        }
    %>
</div>
</body>
</html>