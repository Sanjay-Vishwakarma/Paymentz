<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 2/10/15
  Time: 2:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.vo.payoutVOs.PartnerWireVO" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%!
    private static Logger logger=new Logger("actionAgentWireManager.jsp");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();

        });
    </script>
    <title></title>
</head>
<body>
<%
    Connection conn=null;
    try
    {
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        Functions functions=new Functions();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
            String str2 = "";
            String pgtypeid = "";
            String currency= "";
            currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

            pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");

            //String accountid2 = Functions.checkStringNull(request.getParameter("accountid"));
            String memberid = Functions.checkStringNull(request.getParameter("toid"))==null?"":request.getParameter("toid");
            String partnerid = request.getParameter("partnerid");

           // Map terminalMap = GatewayTypeService.getAllTerminalsGroupByMerchant();

            if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
            else
                pgtypeid="";

            conn= Database.getConnection();
            String query="";
            PreparedStatement pstmt=null;
            ResultSet rs=null;
            int year = Calendar.getInstance().get(Calendar.YEAR);
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Partner Wire Manager
                <%--<div style="float: right;">
                    <form action="/icici/addwire.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New wire
                        </button>
                    </form>
                </div>--%>
            </div><br>
            <form action="/icici/servlet/PartnerWireList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%

                    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
                    if (gateway == null)
                    {gateway = "";}
                    PartnerDAO partnerDAO =new PartnerDAO();
                    TerminalManager terminalManager = new TerminalManager();
                    List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
                    String str="";
                    String fdate=null;
                    String tdate=null;
                    String fmonth=null;
                    String tmonth=null;
                    String fyear=null;
                    String tyear=null;
                    String toid=null;
                    String accountid=null;
                    String terminalid=null;
                    try
                    {
                        fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
                        tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
                        fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
                        tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
                        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
                        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
                        toid = ESAPI.validator().getValidInput("toid",request.getParameter("toid"),"Numbers",10,true);
                        accountid = ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",10,true);
                        terminalid = ESAPI.validator().getValidInput("accountid",request.getParameter("tyear"),"Numbers",10,true);
                    }
                    catch(ValidationException e)
                    {
                        logger.error("Date Format Exception while select",e);
                    }
                    Calendar rightNow = Calendar.getInstance();
                    if (fdate == null) fdate = "" + 1;
                    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
                    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
                    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
                    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
                    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
                    str = str + "ctoken=" + ctoken;
                    if (fdate != null) str = str + "&fdate=" + fdate;
                    if (tdate != null) str = str + "&tdate=" + tdate;
                    if (fmonth != null) str = str + "&fmonth=" + fmonth;
                    if (tmonth != null) str = str + "&tmonth=" + tmonth;
                    if (fyear != null) str = str + "&fyear=" + fyear;
                    if (tyear != null) str = str + "&tyear=" + tyear;
                    if (toid != null) str = str + "&toid=" + toid;
                    if (accountid != null) str = str + "&accountid=" + accountid;
                    if (terminalid != null) str = str + "&terminalid=" + terminalid;

                    //int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    //int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                    TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
                    TreeMap<String,TerminalVO> terminalMap = new TreeMap<String, TerminalVO>();
                    //TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
                    //TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
                    TreeMap partneriddetails=partnerDAO.getpartnerDetails();


                    for(TerminalVO terminalVO : terminalList)
                    {
                        String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
                        memberMap.put(memberKey,terminalVO);

                        terminalMap.put(terminalVO.getTerminalId(),terminalVO);
                    }

                %>
                <table  border="0" align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td  class="textb" colspan="12">&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="textb" colspan="2">Gateway</td>
                        <td class="textb" colspan="2">
                            <input name="pgtypeid" id="gateway" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
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
                        <td class="textb" colspan="2">Accountid</td>
                        <td class="textb" colspan="2">
                            <input name="accountid" id="accountid" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                        <%-- <select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
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
                            </select>--%>
                        </td>
                        <td class="textb" colspan="2">MemberId</td>
                        <td class="textb" colspan="2">
                            <input name="toid" id="memberid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                        <%-- <select name="toid" id="memberid" class="txtbox" style="width:200px;">
                                <option data-bank="all"  data-accid="all" value="0" selected>--Select MemberID--</option>
                                <%

                                    for(String sMemberId: memberMap.keySet())
                                    {

                                        TerminalVO t = memberMap.get(sMemberId);
                                        String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                        String accId = t.getAccountId();
                                        String aContactPerson = t.getContactPerson();
                                        String gateway2 = t.getGateway().toUpperCase();
                                        String currency2 = t.getCurrency();
                                        String pgtype = t.getGateway_id();
                                        String value = gateway2+"-"+currency2+"-"+pgtype;
                                        String isSelected = "";
                                        if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid + "-" + accId))
                                        {
                                            isSelected = "selected";
                                        }
                                        else
                                        {
                                            isSelected = "";
                                        }

                                %>
                                <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aContactPerson%></option>
                                <%

                                    }
                                %>
                            </select>--%>
                        </td>
                    </tr>
                    <tr><td colspan="">&nbsp;</td></tr>
                    <tr>

                        <td class="textb" colspan="2">Terminal ID</td>
                        <td class="textb" colspan="2">
                            <input name="terminalid" id="tid" value="<%=Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid")%>" class="txtbox" autocomplete="on">
                        <%--<select name="terminalid" class="txtbox" >
                                <option value="" selected>--ALL--</option>

                                <%
                                    String isSelected = "";
                                    String active = "";

                                    for(String terminalId : terminalMap.keySet())
                                    {
                                        TerminalVO t = terminalMap.get(terminalId);
                                        String paymode = t.getPaymentName();
                                        String cardType = t.getCardType();
                                        if (t.getIsActive().equalsIgnoreCase("Y"))
                                        {
                                            active = "Active";
                                        }
                                        else
                                        {
                                            active = "InActive";
                                        }
                                        if (t.getTerminalId().equalsIgnoreCase(terminalid))
                                        {
                                            isSelected = "selected";
                                        }
                                        else
                                        {
                                            isSelected = "";
                                        }

                                %>

                                <option  value="<%=t.getTerminalId()%>" <%=isSelected%>>
                                    <%=t.getTerminalId()%>-<%=paymode%>-<%=cardType%>-<%=active%></option>
                                <%
                                    }
                                %>
                            </select>--%>
                        </td>

                        <td class="textb" colspan="2">Partner ID</td>
                        <td class="textb" colspan="2">
                            <select name="partnerid" class="txtbox">
                                <option value="" selected>--ALL--</option>

                                <%
                                    String selected = "";
                                    String key = "";
                                    String value = "";
                                    for(Object pid:partneriddetails.keySet())
                                    {
                                        key = String.valueOf(pid);
                                        value = (String) partneriddetails.get(pid);
                                        if (key.equals(partnerid))
                                            selected = "selected";
                                        else
                                            selected = "";
                                %>
                                <option value="<%=key%>" <%=selected%> ><%=value%></option>
                                <%
                                    }
                                %>

                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td class="textb" colspan="12">&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="2" class="textb" >From</td>
                        <td colspan="2" class="textb">
                            <select size="1" name="fdate"  value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                                <%
                                    if (fdate != null)
                                        out.println(Functions.dayoptions(1, 31, fdate));
                                    else
                                        out.println(Functions.printoptions(1, 31));
                                %>
                            </select>
                            <select size="1" name="fmonth"  value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                                <%
                                    if (fmonth != null)
                                        out.println(Functions.newmonthoptions(1, 12, fmonth));
                                    else
                                        out.println(Functions.printoptions(1, 12));
                                %>
                            </select>
                            <select size="1" name="fyear"  value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                                <%
                                    if (fyear != null)
                                        out.println(Functions.yearoptions(2005, year, fyear));
                                    else
                                        out.println(Functions.printoptions(2005, year));
                                %>
                            </select>
                        </td>
                        <td colspan="2" class="textb" >To</td>
                        <td colspan="2" class="textb">
                            <select size="1" name="tdate" >
                                <%
                                    if (tdate != null)
                                        out.println(Functions.dayoptions(1, 31, tdate));
                                    else
                                        out.println(Functions.printoptions(1, 31));
                                %>
                            </select>

                            <select size="1" name="tmonth" >
                                <%
                                    if (tmonth != null)
                                        out.println(Functions.newmonthoptions(1, 12, tmonth));
                                    else
                                        out.println(Functions.printoptions(1, 12));
                                %>
                            </select>

                            <select size="1" name="tyear" >
                                <%
                                    if (tyear != null)
                                        out.println(Functions.yearoptions(2005, year, tyear));
                                    else
                                        out.println(Functions.printoptions(2005, year));
                                %>
                            </select>
                        </td>
                        <td class="textb" colspan="2">Is Paid</td>
                        <td class="textb" colspan="2">
                            <Select name="isrr">
                                <option value="">--ALL--</option>
                                <option value="N">Unpaid </option>
                                <option value="Y"> Paid </option>
                            </Select>
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
                        <td width="8%" class="textb" >&nbsp;</td>
                        <td width="3%" class="textb">&nbsp;</td>
                        <td width="12%" class="textb">
                            <button type="submit" class="buttonform" value="Submit">
                                <i class="fa fa-clock-o"></i>
                                &nbsp;&nbsp;Search
                            </button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable">
    <%
        String errorMessage=(String)request.getAttribute("message");
        if(errorMessage!=null)
        {
            Functions.NewShowConfirmation("Error",errorMessage);
        }

        String action = (String) request.getAttribute("action");
        String conf = " ";
        if (action != null)
        {
            if (action.equalsIgnoreCase("view"))
            {
                conf = "disabled";
            }
            PartnerWireVO partnerWireVO=(PartnerWireVO)request.getAttribute("partnerWireVO");
            if (partnerWireVO != null)
            {
                String style="class=tr1";

    %>
    <form action="/icici/servlet/UpdatePartnerWire?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" name="id" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getSettledId())%>" <%=conf%>>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="true" name="isSubmitted">
        <table border="1" bordercolor="#ffffff" align="center" style="width:80%" class="table table-striped table-bordered table-green dataTable">
            <tr <%=style%>>
                <td class="th0" colspan="2">Partner Wire Manager:</td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">SettleId :</td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="toid" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getSettledId())%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Partner ID :</td>
                <td class="tr1"><input type="text" class="txtbox1" class="txtbox1" size="30" name="partnerid" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getPartnerId())%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Partner Type :</td>
                <td class="tr1"><input type="text" class="txtbox1" class="txtbox1" size="30" name="partnertype" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getPartnerType())%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Member ID:</td>
                <td class="tr1"><input type="text" class="txtbox1" class="txtbox1" size="30" name="toid" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getMemberId())%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Account Id : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getAccountId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Terminal Id: </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="terminalid" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getTerminalId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Pay Mode : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="paymodeid" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getPayModeId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Card Type : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="cardtypeid" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getCardTypeId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Partner Charge Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="partnerchargeamount" value="<%=ESAPI.encoder().encodeForHTML(Functions.round(partnerWireVO.getPartnerChargeAmount(),2))%>" <%=conf%>> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Partner Unpaid Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="partnerunpaidamount" value="<%=ESAPI.encoder().encodeForHTML(Functions.round(partnerWireVO.getPartnerUnpaidAmount(),2))%>" <%=conf%>> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Partner Total Funded Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="partnertotalfundedamount" value="<%=ESAPI.encoder().encodeForHTML(Functions.round(partnerWireVO.getPartnerTotalFundedAmount(),2))%>" <%=conf%>> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Currency : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="currency" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getCurrency())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Status : </td>
                <td class="tr1">
                    <% if(action.equalsIgnoreCase("view")){ %>
                    <input type="text" size="30"  class="txtbox" name="status" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getStatus())%>" <%=conf%>> </td>
                <% }else{  %>
                <select name="status">
                    <% if( partnerWireVO.getStatus().equals("paid")){ %>
                    <option value="paid" selected> PAID</option>
                    <option value="unpaid"> UNPAID</option>
                    <% }else{
                    %>
                    <option value="paid" > PAID</option>
                    <option value="unpaid" selected> UNPAID</option>
                    <% }%>
                </select>
                <%}%>

            </tr>
            <tr <%=style%>>
                <td class="tr1">Settle Date : </td>
                <td class="tr1">
                    <%
                        if(action.equalsIgnoreCase("view"))
                        {
                    %>
                    <%--<input type="text" class="txtbox1" size="30" name="settledate" readonly class="datepicker"  value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getSettleDate())%>" <%=conf%>>--%>
                    <input type="text" class="txtbox1" size="30" name="settledate" readonly class="datepicker"  value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML(partnerWireVO.getSettleDate()))?ESAPI.encoder().encodeForHTML(partnerWireVO.getSettleDate()):""%>"<%=conf%>>
                    <%
                    }
                    else
                    {
                    %>  <input type="text"  readonly class="datepicker" name="settledate">
                    <%
                        }
                    %></td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Wire Creation Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="wirecreationdate" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getWireCreationDate())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement Start Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="settlementstartdate" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getSettlementStartDate())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement End Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="settlementenddate" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getSettlementEndDate())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Decline Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getDeclinedCoverDateUpTo())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Reversal Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getReversedCoverDateUpTo())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Chargeback Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getChargebackCoverDateUpTo())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement Report File Name : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" style="width:90%" name="settlementreportfilename" value="<%=ESAPI.encoder().encodeForHTML(partnerWireVO.getSettlementReportFileName())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1" colspan="2" align="center"><input type="submit" value="Update" class="buttonform" <%=conf%>></td>
            </tr>
        </table>
    </form>
    <%      }
    else
    {
        //no record found
    }
    }
    }
    }

    catch(Exception e)
    {
        logger.error("Sql Exception in actionPartnerWireManager:",e);
        out.println(Functions.NewShowConfirmation("Error","Error while add record in wire manager."));
    }

    finally
    {
        Database.closeConnection(conn);
    }



    %>
</div>
</body>
</html>