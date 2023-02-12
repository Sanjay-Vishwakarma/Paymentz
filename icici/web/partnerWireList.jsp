<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 2/10/15
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
    <script language="javascript">
        function getPdfFile(settleid)
        {
            if (confirm("Do you really want to download selected file."))
            {
                document.getElementById("pdfform"+settleid).submit();
            }

        }
    </script>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <%-- <script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>--%>
    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>
    <title>Partners Management> Partner Wire Manager</title>
</head>
<body>
<%!
    private static Logger logger=new Logger("partnerWirelist.jsp");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

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
                    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
                    if (gateway == null)
                    {gateway = "";}
                    PartnerDAO partnerDAO =new PartnerDAO();
                    TerminalManager terminalManager = new TerminalManager();
                    Functions functions = new Functions();
                    List<TerminalVO> terminalList = terminalManager.getAllMappedTerminal();

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
                    String ispaid=null;
                    String pgtypeid = "";

                    pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
                    String memberid = Functions.checkStringNull(request.getParameter("toid"))==null?"":request.getParameter("toid");
                    String partnerid = Functions.checkStringNull(request.getParameter("partnerid"))==null?"":request.getParameter("partnerid");
                    String terminalId=Functions.checkStringNull(request.getParameter("terminalid"));
                    ispaid = request.getParameter("isrr");

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
                    if (ispaid != null) str = str + "&isrr=" + ispaid;

                    if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
                    else
                        pgtypeid="";

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                    TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
                    TreeMap<String,TerminalVO> terminalMap = new TreeMap<String, TerminalVO>();
                    TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
                    TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
                    LinkedHashMap partneriddetails=partnerDAO.getpartnerDetail();
                    for(TerminalVO terminalVO : terminalList)
                    {
                        String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
                        memberMap.put(memberKey,terminalVO);

                        terminalMap.put(terminalVO.getTerminalId(),terminalVO);
                    }
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                %>

                <table  border="0" align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td  class="textb" colspan="12">&nbsp;</td>
                    </tr>

                    <tr>
                        <td class="textb" colspan="2">Gateway/Account</td>
                        <td class="textb" colspan="2">
                            <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                        <%--<select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
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
                            <%--   <%
                                   TreeSet<String> gatewaySet = new TreeSet<String>();
                                   TreeSet<String> currencySet = new TreeSet<String>();
                                   Set<TerminalVO> accountSet = new HashSet<TerminalVO>();
                                   TreeSet<String> memberSet = new TreeSet<String>();
                                   TreeSet<Integer> accountidSet = new TreeSet<Integer>();

                                   for(Object key : terminalMap.keySet())
                                   {
                                       for(TerminalVO t : (List<TerminalVO>)terminalMap.get(key))
                                       {
                                           //System.out.println("----"+t.getMemberId()+"-"+t.getGateway());
                                           gatewaySet.add(t.getGateway().toUpperCase());
                                           currencySet.add(t.getCurrency());
                                           accountSet.add(t);
                                           memberSet.add(t.getMemberId());
                                           accountidSet.add(Integer.valueOf(t.getAccountId()));
                                       }
                                   }
                                   Iterator gatewayIterator = gatewaySet.iterator();
                                   String gateway2 = "";
                                   while(gatewayIterator.hasNext())
                                   {
                                       gateway2 = (String)gatewayIterator.next();
                                       String isSelected = "";
                                       if(pgtypeid.equalsIgnoreCase(gateway2))
                                       {
                                           isSelected = "selected";
                                       }

                               %>
                               <option value="<%=gateway2.toUpperCase()%>" <%=isSelected%>><%=gateway2.toUpperCase()%></option>
                               <%

                                   }

                               %>
--%>

                        </td>
                        <td class="textb" colspan="2">AccountID</td>
                        <td class="textb" colspan="2">
                            <input name="accountid" id="accountid1" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
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
                        <td class="textb" colspan="2">Member ID</td>
                        <td class="textb" colspan="2">
                            <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                        <%--  <select name="toid" id="memberid" class="txtbox" style="width:200px;">
                                <option data-bank="all"  data-accid="all" value="0" selected>--Select MemberID--</option>
                                <%

                                    for(String sMemberId: memberMap.keySet())
                                    {

                                        TerminalVO t = memberMap.get(sMemberId);
                                        String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                        String accId = t.getAccountId();
                                        String aCompanyName =t.getCompany_name();
                                        String aContactPerson = "";
                                        if (functions.isValueNull(t.getContactPerson()))
                                        {
                                            aContactPerson = t.getContactPerson();
                                        }
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
                                <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aCompanyName%></option>
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
                            <input name="terminalid" id="tid1" value="<%=Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid")%>" class="txtbox" autocomplete="on">
                        <%--<select name="terminalid" class="txtbox" >
                                <option value="" selected>--ALL--</option>

                                <%
                                    String active = "";
                                    String isSelected="";
                                    String terminalValue = "";
                                    for(TerminalVO terminalVO : terminalList)
                                    {
                                        terminalValue = terminalVO.getTerminalId()+"-"+terminalVO.getCardTypeId()+"-"+terminalVO.getPaymodeId();
                                        if (terminalValue.equalsIgnoreCase(terminalId))
                                            isSelected="selected";

                                        if (terminalVO.getIsActive().equalsIgnoreCase("Y"))
                                            active = "Active";
                                        else
                                            active = "InActive";
                                        String value1 = terminalVO.getGateway().toUpperCase()+"-"+terminalVO.getCurrency()+"-"+terminalVO.getGateway_id();
                                %>
                                <option data-bank="<%=value1%>" data-accid="<%=terminalVO.getMemberId()%>" value="<%=terminalValue%>" <%=isSelected%>>
                                    <%=terminalVO.getTerminalId()%>-<%=terminalVO.getPaymentName()%>-<%=terminalVO.getCardType()%>-<%=terminalVO.getCurrency()%>-<%=active%>
                                </option>
                                <%
                                    }
                                %>
                            </select>--%>
                        </td>
                        <td class="textb" colspan="2">Partner ID</td>
                        <td class="textb" colspan="2">
                            <input name="partnerid" id="pid1" value="<%=partnerid%>" class="txtbox" autocomplete="on">

                            <%--<select name="partnerid" class="txtbox">
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
                                &lt;%&ndash;  <%
                                      query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                                      pstmt = conn.prepareStatement( query );
                                      rs = pstmt.executeQuery();
                                      while (rs.next())
                                      {
                                          out.println("<option value=\""+rs.getInt("partnerId")+"\">"+rs.getInt("partnerId")+" - "+rs.getString("partnerName")+"</option>");
                                      }
                                  %>&ndash;%&gt;
                            </select>--%>
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
                            <select name="isrr" >
                                <%
                                    if("Y".equals(ispaid))
                                    {
                                %>
                                <option value=""></option>
                                <option value="Y" selected>Paid</option>
                                <option value="N">UnPaid</option>
                                <%
                                }
                                else if("N".equals(ispaid))
                                {
                                %>
                                <option value=""></option>
                                <option value="N" selected>Unpaid</option>
                                <option value="Y">Paid</option>
                                <%
                                }
                                else
                                {
                                %>
                              <%-- <option value="" selected></option>--%>
                                <option value="" selected>All</option>
                                <option value="N" >Unpaid</option>
                                <option value="Y">Paid</option>
                                <% }
                                %>
                            </select>
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
                                &nbsp;&nbsp;Submit
                            </button>
                        </td>
                    </tr>
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
            out.println("<table align=\"center\" class=\"textb\" ><tr><td valign=\"middle\"><font class=\"text\" >");

            out.println(errormsg1);

            out.println("</font></td></tr></table>");
        }
    %>

    <%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

        Hashtable temphash=null;
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
                logger.error("Records & TotalRecords is found null", ex);
            }

            if(records>0)
            {
    %>

    <table align=center width="90%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr No</td>
            <td valign="middle" align="center" class="th0">Partner ID</td>
            <td valign="middle" align="center" class="th0">Member ID</td>
            <td valign="middle" align="center" class="th0">Wire Creation On</td>
            <td valign="middle" align="center" class="th0">Settle Start Date</td>
            <td valign="middle" align="center" class="th0">Settle End Date</td>
            <td valign="middle" align="center" class="th0">Partner Funded Amount</td>
            <td valign="middle" align="center" class="th0">Unpaid Amount</td>
            <td valign="middle" align="center" class="th0">Currency</td>
            <td valign="middle" align="center" class="th0">Status</td>
            <td valign="middle" align="center" class="th0">PDF Report</td>
            <td valign="middle" align="center" class="th0" colspan="3">Action</td>
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
                String partnerId=ESAPI.encoder().encodeForHTML((String)temphash.get("partnerid"));
                String memberId=ESAPI.encoder().encodeForHTML((String)temphash.get("toid"));
                String wireCreationTime=ESAPI.encoder().encodeForHTML((String)temphash.get("wirecreationtime"));
                String firstDate=ESAPI.encoder().encodeForHTML((String)temphash.get("settlementstartdate"));
                String lastDate=ESAPI.encoder().encodeForHTML((String)temphash.get("settlementenddate"));
                String netFinalAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("partnertotalfundedamount"));
                String unpaidAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("partnerunpaidamount"));
                String currency2=ESAPI.encoder().encodeForHTML((String)temphash.get("currency"));
                String status=ESAPI.encoder().encodeForHTML((String)temphash.get("status"));

        %>

        <tr>

            <td align="center" <%=style%>><%=srno%></td>
            <td align="center" <%=style%>><%=partnerId%></td>
            <td align="center" <%=style%>><%=memberId%></td>
            <td align="center"<%=style%>><%=wireCreationTime%></td>
            <td align="center"<%=style%>><%=firstDate%></td>
            <td align="center"<%=style%>><%=lastDate%></td>
            <td align="center"<%=style%>><%=netFinalAmount%></td>
            <td align="center"<%=style%>><%=unpaidAmount%></td>
            <td align="center"<%=style%>><%=currency2%></td>
            <td align="center"<%=style%>><%=status%></td>

            <td align="center"<%=style%>><form id="pdfform<%=settleId%>" action="/icici/servlet/ActionPartnerWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="sendPdfFile">  </form><a href="javascript: getPdfFile(<%=settleId%>)"><img width="20" height="28" border="0" src="/icici/images/pdflogo.jpg"></a></td>


            <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionPartnerWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="view"><input type="submit" class="gotoauto"    value="View"></form></td>
            <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionPartnerWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="update"><input type="submit" class="gotoauto"  value="Update"></form></td>
            <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionPartnerWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="delete"><input type="submit" class="gotoauto"  value="Delete"></form></td>

        </tr>

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
                <jsp:param name="page" value="PartnerWireList"/>
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
    %>
</div>
</body>
</html>