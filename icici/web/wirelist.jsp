<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TreeMap" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 6/2/14
  Time: 3:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <script language="javascript">
        function getExcelFile(settleid)
        {
            if (confirm("Do you really want to downloads selected file."))
            {
                document.getElementById("viewsettletransfile"+settleid).submit();
            }

        }
        function getPdfFile(settleid)
        {
            if (confirm("Do you really want to downloads selected file."))
            {
                document.getElementById("pdfform"+settleid).submit();
            }
        }
        /*function Delete(el)
        {
            var e = $(el);
            if (confirm("Do you really want to delete the file."))
            {
                e.parent('form').submit();
            }
        }*/
    </script>
    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>--%>
    <%-- <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>

    <title>Merchant Management > Merchant Wire Manager</title>
</head>
<body>
<%!
    private static Logger logger=new Logger("wirelist.jsp");
%>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Merchant Wire Manager
                <div style="float: right;">
                    <form action="/icici/addWireForAllTerminal.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New wire For ALL Terminal
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addwire.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New wire
                        </button>
                    </form>
                </div>
            </div><br>
            <form action="/icici/servlet/WireList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    String str="";
                    String fdate=null;
                    String tdate=null;
                    String fmonth=null;
                    String tmonth=null;
                    String fyear=null;
                    String tyear=null;
                    String toid=null;
                    String ispaid=null;
                    String gateway=null;
                    String accountid=null;
                    String terminalid=null;
                    String cycleid=null;
                    String parentcycleid=null;

                    String pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
                    accountid = Functions.checkStringNull(request.getParameter("accountid"));
                    String memberid = nullToStr(Functions.checkStringNull(request.getParameter("toid")));

                    //Get List of TerminalID
                    PayoutManager payoutManager = new PayoutManager();
                    List<TerminalVO> sTerminal = payoutManager.getTerminalForMerchantWireList();

                    TerminalManager terminalManager = new TerminalManager();
                    List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
                    TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
                    TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
                    TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();

                    for(TerminalVO terminalVO : terminalList)
                    {
                        String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
                        memberMap.put(memberKey,terminalVO);
                    }

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
                        terminalid = ESAPI.validator().getValidInput("terminalid",request.getParameter("terminalid"),"Numbers",10,true);
                        cycleid = ESAPI.validator().getValidInput("cycleid",request.getParameter("cycleid"),"Numbers",10,true);
                        parentcycleid = ESAPI.validator().getValidInput("parentcycleid",request.getParameter("parentcycleid"),"Numbers",10,true);
                        ispaid = request.getParameter("isrr");
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
                    if (cycleid != null) str = str + "&cycleid=" + cycleid;
                    if (parentcycleid != null) str = str + "&parentcycleid=" + parentcycleid;
                    if (ispaid != null) str = str + "&isrr=" + ispaid;

                    if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
                    else
                        pgtypeid="";

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                    str = str + "&SRecords=" + pagerecords;
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                %>

                <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
                    <tr><td colspan="12">&nbsp;</td></tr>
                    <tr>
                        <td  class="textb" >From</td>
                        <td class="textb" colspan="2">
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
                        <td class="textb" >To</td>
                        <td class="textb">
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
                        <td class="textb" >Is Paid</td>
                        <td class="textb">
                            <select name="isrr" >
                                <%
                                    if("paid".equals(ispaid))
                                    {
                                %>
                                <option value="">--ALL--</option>
                                <option value="paid" selected>Paid</option>
                                <option value="unpaid">UnPaid</option>
                                <option value="carryforward"> CarryForward</option>
                                <option value="partialPaid"> PartialPaid</option>
                                <%
                                }
                                else if("unpaid".equals(ispaid))
                                {
                                %>
                                <option value="">--ALL--</option>
                                <option value="unpaid" selected>UnPaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward"> CarryForward</option>
                                <option value="partialPaid"> PartialPaid</option>
                                <%
                                }
                                else if("carryforward".equals(ispaid))
                                {
                                %>
                                <option value="">--ALL--</option>
                                <option value="unpaid">UnPaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward" selected> CarryForward</option>
                                <option value="partialPaid"> PartialPaid</option>
                                <%
                                }
                                else if("partialPaid".equals(ispaid))
                                {
                                %>
                                <option value="">--ALL--</option>
                                <option value="unpaid">UnPaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward"> CarryForward</option>
                                <option value="partialPaid" selected> PartialPaid</option>
                                <%
                                }
                                else
                                {
                                %>
                                <option value="" selected>--ALL--</option>
                                <option value="unpaid" >UnPaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward"> CarryForward</option>
                                <option value="partialPaid"> PartialPaid</option>
                                <% }%>
                            </select>
                        </td>

                        <td  class="textb" for="gateway" colspan="2">Gateway</td>
                        <td  class="textb">
                            <input name="pgtypeid" id="pgtypeid" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                        </td>

                    </tr>

                    <tr><td colspan="12">&nbsp;</td></tr>
                    <tr><td colspan="12">&nbsp;</td></tr>

                    <tr>


                        <td colspan="1" class="textb">AccountID</td>
                        <td colspan="2" class="textb" >
                            <input name="accountid" id="accid"
                                   value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>"
                                   class="txtbox" autocomplete="on">
                        </td>

                        <td colspan="1" class="textb">Member ID</td>
                        <td class="textb" >
                            <input name="toid" id="memid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                        </td>

                        <td colspan="1" class="textb">Terminal ID</td>
                        <td colspan="2" class="textb">
                            <input name="terminalid" id="tid2"
                                   value="<%=Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid")%>"
                                   class="txtbox" autocomplete="on">
                        </td>

                        <td colspan="1" class="textb">Report ID</td>
                        <td colspan="2" class="textb">
                            <input name="reportid"
                                   value="<%=Functions.checkStringNull(request.getParameter("reportid"))==null?"":request.getParameter("reportid")%>"
                                   class="txtbox">
                        </td>
                    <tr>
                    <tr><td colspan="12">&nbsp;</td></tr>
                    <tr><td colspan="12">&nbsp;</td></tr>
                    </tr>

                        <td colspan="1" class="textb">Cycle ID</td>
                        <td colspan="2" class="textb">
                            <input name="cycleid" id="bankwireid"
                                   value="<%=Functions.checkStringNull(cycleid)==null?"":cycleid%>"
                                   class="txtbox" autocomplete="on">
                        </td>

                        <td colspan="1" class="textb">Parent Cycle ID</td>
                        <td colspan="2" class="textb">
                            <input name="parentcycleid" id="parent_bankwireId"
                                   value="<%=Functions.checkStringNull(parentcycleid)==null?"":parentcycleid%>"
                                   class="txtbox" autocomplete="on">
                        </td>
                    <td class="textb">
                        <button type="submit" class="buttonform">
                            <i class="fa fa-clock-o"></i>
                            &nbsp;&nbsp;Search
                        </button>
                    </td>
                    </tr>
                    <tr><td colspan="12">&nbsp;</td></tr>

                    <tr>
                        <td colspan="5"></td>

                    </tr>
                    <tr><td colspan="12">&nbsp;</td></tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable">
    <%  String errormsg1 = (String)request.getAttribute("message");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
            out.println(errormsg1);
            out.println("</font></td></tr></table>");
        }
    %>
    <%
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");
        Hashtable temphash=null;
        str = str + "&ctoken=" + ctoken;

        if (hash != null && hash.size() > 0)
        {
            int records=0;
            int totalrecords=0;
            int currentblock=1;
            try
            {
                records=Functions.convertStringtoInt((String)hash.get("records"),15);
                totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
                currentblock = Functions.convertStringtoInt((request.getParameter("currentblock")),1);
            }
            catch(Exception ex)
            {
                logger.error("Records & TotalRecords is found null",ex);
            }

            if(records>0)
            {
    %>
    <table align=center width="90%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr No</td>
            <td valign="middle" align="center" class="th0">Member ID</td>
            <td valign="middle" align="center" class="th0">Terminal ID</td>
            <td valign="middle" align="center" class="th0">Cycle ID</td>
            <td valign="middle" align="center" class="th0">Parent Cycle ID</td>
            <td valign="middle" align="center" class="th0">Wire Creation On</td>
            <td valign="middle" align="center" class="th0">First Date</td>
            <td valign="middle" align="center" class="th0">Last Date</td>
            <td valign="middle" align="center" class="th0">Net Final Amount</td>
            <td valign="middle" align="center" class="th0">Unpaid Amount</td>
            <td valign="middle" align="center" class="th0">Currency</td>
            <td valign="middle" align="center" class="th0">Status</td>
            <td valign="middle" align="center" class="th0">Report File</td>
            <td valign="middle" align="center" class="th0">Transaction File</td>
            <td valign="middle" align="center" class="th0" colspan="4">Action</td>
            </td>
        </tr>
        </thead>
        <%
            String style="class=td1";
            String ext="light";
            Hashtable inner=null;
            for(int pos=1;pos<=records;pos++)
            {
                String id=Integer.toString(pos);
                int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

                if(pos%2==0)
                {
                    style="class=tr0";
                    ext="dark";
                }
                else
                {
                    style="class=tr1";
                    ext="light";
                }
                temphash=(Hashtable)hash.get(id);

                String settleId=ESAPI.encoder().encodeForHTML((String)temphash.get("settledid"));
                String memberId=ESAPI.encoder().encodeForHTML((String)temphash.get("toid"));
                String terminalId=ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"));
                String wireCreationDate=ESAPI.encoder().encodeForHTML((String)temphash.get("wirecreationdate"));
                String firstDate=ESAPI.encoder().encodeForHTML((String)temphash.get("firstdate"));
                String lastDate=ESAPI.encoder().encodeForHTML((String)temphash.get("lastdate"));
                String netFinalAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("netfinalamount"));
                String unpaidAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("unpaidamount"));
                String currency2=ESAPI.encoder().encodeForHTML((String)temphash.get("currency"));
                String status=ESAPI.encoder().encodeForHTML((String)temphash.get("STATUS"));
                String settlementcycle_no=ESAPI.encoder().encodeForHTML((String)temphash.get("settlementcycle_no"));
                String parentsettlementcycle_no=ESAPI.encoder().encodeForHTML((String)temphash.get("parentsettlementcycle_no"));
                String payoutid=ESAPI.encoder().encodeForHTML((String)temphash.get("payoutid"));

                if ("paid".equalsIgnoreCase(status))
                    status = status.substring(0,1).toUpperCase()+status.substring(1);
                else if ("unpaid".equalsIgnoreCase(status))
                    status = status.substring(0,1).toUpperCase()+status.substring(1,2)+status.substring(2,3).toUpperCase()+status.substring(3);
                else if ("carryforward".equalsIgnoreCase(status))
                    status = status.substring(0,1).toUpperCase()+status.substring(1,5)+status.substring(5,6).toUpperCase()+status.substring(6);
                else if ("partialPaid".equalsIgnoreCase(status))
                    status = status.substring(0,1).toUpperCase()+status.substring(1,7)+status.substring(7, 8).toUpperCase() +status.substring(8);
        %>
        <tr>
            <td align="center" <%=style%>><%=srno%></td>
            <td align="center" <%=style%>><%=memberId%></td>
            <td align="center"<%=style%>><%=terminalId%></td>
            <td align="center"<%=style%>><%=settlementcycle_no!=null?settlementcycle_no:"-"%></td>
            <td align="center"<%=style%>><%=parentsettlementcycle_no!=null?parentsettlementcycle_no:"-"%></td>
            <td align="center"<%=style%>><%=wireCreationDate%></td>
            <td align="center"<%=style%>><%=firstDate%></td>
            <td align="center"<%=style%>><%=lastDate%></td>
            <td align="center"<%=style%>><%=netFinalAmount%></td>
            <td align="center"<%=style%>><%=unpaidAmount%></td>
            <td align="center"<%=style%>><%=currency2%></td>
            <td align="center"<%=style%>><%=status%></td>
            <td align="center"<%=style%>><form id="pdfform<%=settleId%>" action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="sendPdfFile">  </form><a href="javascript: getPdfFile(<%=settleId%>)"><img width="20" height="28" border="0" src="/icici/images/pdflogo.jpg"></a></td>
            <td align="center"<%=style%>><form id="viewsettletransfile<%=settleId%>" action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="sendExcelFile"></form><a href="javascript: getExcelFile(<%=settleId%>)"><img border="0" src="/icici/images/excel.jpg"></a></td>
            <td align="center" <%=style%>>&nbsp;<form action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="payoutid" value=<%=payoutid%>><input type="hidden" name="action" value="view"><input type="submit" class="gotoauto" value="View"></form></td>
            <td align="center" <%=style%>>&nbsp;<form action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="payoutid" value=<%=payoutid%>><input type="hidden" name="action" value="update"><input type="submit" class="gotoauto" value="Update"></form></td>
            <%--<td align="center" <%=style%>>&nbsp;<form action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" method="post" name="DeleteDetails" id="DeleteDetails"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="delete"><input type="button" class="gotoauto" onclick="return Delete(this);" value="Delete"></form></td>--%>
            <%--<td align="center" <%=style%>>&nbsp;<form action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="conform"><input type="submit" class="gotoauto" value="Confirm"></form></td>&ndash;%&gt;--%>
            <td align="center" <%=style%>>&nbsp;<form action="/icici/servlet/ListMerchantRandomCharges?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="memberid" value=<%=memberId%>><input type="hidden" name="terminalid" value=<%=terminalId%>><input type="hidden" name="bankwireid" value=<%=settlementcycle_no%>><input type="submit" class="gotoauto" value="Random Charges"></form></td>
<%--
            <td align="center" <%=style%>>&nbsp;<form action="/icici/servlet/ActionWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="settledid" value="<%=settleId%>"/><input type="hidden" name="cycleid" value="<%=settlementcycle_no%>"/><input type="hidden" name="action" value="TWTupdate"/><input type="submit" class="gotoauto" value="Add TWT Payout"></form></td>
--%>

        <%--<%
              Functions function = new Functions();
                String Disabled="";
            if(function.isValueNull(payoutid))
            {
                Disabled = "disabled";
            }
            %>
            <td align="center" <%=style%>>&nbsp;<form action="/icici/addPayout.jsp?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="settledid" value="<%=settleId%>"/><input type="hidden" name="cycleid" value="<%=settlementcycle_no%>"/><input type="hidden" name="action" value="update"/><input type="submit" class="gotoauto" value="Add Manual Payout Details" <%=Disabled%>></form></td>
   --%>     </tr>
        <%
            }
        %>
    </table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="WireList"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
    <%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry","No records found."));
            }
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Filter","Please provide the Details for WireList."));
        }
    %>
    <%
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