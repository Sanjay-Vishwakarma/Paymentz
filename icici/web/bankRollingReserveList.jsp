<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 11/25/14
  Time: 4:55 PM
  To change this template use File | Settings | File Templates.
--%>


<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.vo.BankRollingReserveVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TreeMap" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="/icici/styyle.css" >
    <%-- <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>--%>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <title>Bank Description Manager> Bank Rolling Reserve Manager</title>
</head>
<body>
<%!
    private static Logger logger=new Logger("bankRollingReserveList.jsp");

%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str2 = "";
        String pgtypeid = "";
      /*  String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");*/
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        String accountid = Functions.checkStringNull(request.getParameter("accountid"));


        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();



%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Bank Rolling Reserve Manager
                <div style="float: right;">
                    <form  action="/icici/addNewBankRollingReserve.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="addnewmember" style="margin-top:5px;"  value="Add Rolling Reserve">
                    </form>
                </div>
                <div style="float: right;">
                    <form  action="/icici/bankRollingReserveList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="addnewmember" style="margin-top:5px;" value="Rolling Reserve List">
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/BankRollingReserveList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    String str="";
                    String fdate=null;
                    String tdate=null;
                    String fmonth=null;
                    String tmonth=null;
                    String fyear=null;
                    String tyear=null;
                    String accountId=null;
                    try
                    {
                        fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
                        tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
                        fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
                        tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
                        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
                        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
                        accountId = ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",10,true);

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
                    if (accountid != null) str = str + "&accountid=" + accountid;

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                %>
                <center>
                    <table   border="0"  align="center" width="90%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">



                        <%--<tr><td colspan="10">&nbsp;</td></tr>--%>
                        <tr><td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td colspan="4" class="textb" >From</td>
                            <td colspan="4" class="textb">
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
                            <td colspan="4" class="textb" >To</td>
                            <td colspan="4" class="textb">
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

                        </tr>

                        <tr><td class="textb" colspan="12">&nbsp;</td></tr>
                        <tr><td class="textb" colspan="12">&nbsp;</td></tr>

                        <tr>
                            <td class="textb" colspan="2">Gateway</td>
                            <td class="textb" colspan="6">
                                <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox"
                                       autocomplete="on"
                                <%--<select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
                                        <option value="0" default>--All--</option>
                                        <%
                                            for(String gatewayType : gatewayTypeTreeMap.keySet())
                                            {
                                                String isSelected = "";
                                                String value = gatewayType.toUpperCase()+"-"+gatewayTypeTreeMap.get(gatewayType).getPgTypeId();
                                                //String value = g.getGateway().toUpperCase()+"-"+g.getCurrency()+"-"+g.getPgTypeId();
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

                            <td class="textb" colspan="2">Account ID</td>
                            <td class="textb" colspan="6">
                                <input name="accountid" id="accountid1"
                                       value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>"
                                       class="txtbox" autocomplete="on">
                                <%--<select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
                                        <option data-bank="all" value="0">---All---</option>
                                        <%
                                            for(Integer sAccid : accountDetails.keySet())
                                            {
                                                GatewayAccount g = accountDetails.get(sAccid);
                                                String isSelected = "";
                                                String gateway2 = g.getGateway().toUpperCase();
                                                String currency2 = g.getCurrency();
                                                String pgtype = g.getPgTypeId();
                                                //String aGateway = accountMap.get(sAccountID).getGateway().toUpperCase()+"-"+accountMap.get(sAccountID).getCurrency()+"-"+accountMap.get(sAccountID).getGateway_id();

                                                if (String.valueOf(sAccid).equals(accountId))
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

                            <td colspan="4" class="textb">
                                <button type="submit" class="buttonform">
                                    <i class="fa fa-clock-o"></i>
                                    &nbsp;&nbsp;Search
                                </button>
                            </td>

                        </tr>

                    </table>
                </center>
            </form>

        </div>
    </div>
</div>
<div class="reporttable">
    <%
        String errormsg1 = (String)request.getAttribute("message");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\" class=\"textb\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

            out.println(errormsg1);

            out.println("</font></td></tr></table>");
        }
        List<BankRollingReserveVO> bankRollingReserveVOList = (List<BankRollingReserveVO>) request.getAttribute("BankRollingReserveVOs");
        if(bankRollingReserveVOList !=null)
        {
            if(bankRollingReserveVOList.size()>0)
            {
                PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

                int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
    %>

    <center><h4 class="textb"><b>Bank Rolling Reserve Release List</b></h4></center>
    <table align=center width="50%" border="1" class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td width="2%"valign="middle" align="center" class="th0">Sr&nbsp;No</td>
            <td width="8%" valign="middle" align="center" class="th0">Account Id</td>
            <td width="8%" valign="middle" align="center" class="th0">Rolling&nbsp;Release&nbsp;Date</td>
            <td width="8%" valign="middle" align="center" class="th0" colspan="3">Action</td>
        </tr>
        <%
            for(BankRollingReserveVO bankRollingReserveVO : bankRollingReserveVOList)
            {

        %>
        <tr>

            <td class="tr0" align="center"><%=srno%></td>
            <td class="tr0" align="center"><%=bankRollingReserveVO.getAccountId()%></td>
            <td class="tr0" align="center"><%=bankRollingReserveVO.getRollingReserveDateUpTo()%></td>
            <td  class="tr0" align="center">
                <form action="/icici/servlet/ActionRollingReserveManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=bankRollingReserveVO.getBankRollingReserveId()%>><input type="hidden" name="action" value="view"><input class="button" type="submit" value="View"></form>
            </td>
            <td class="tr0" align="center">
                <form action="/icici/servlet/ActionRollingReserveManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=bankRollingReserveVO.getBankRollingReserveId()%>><input type="hidden" name="action" value="modify"><input  class="button" type="submit" value="Modify"></form>
            </td>
            <td class="tr0" align="center">
                <form action="/icici/servlet/ActionRollingReserveManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=bankRollingReserveVO.getAccountId()%>><input type="hidden" name="action" value="history"><input class="button" type="submit" value="History"></form>
            </td>
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
                        out.println(Functions.NewShowConfirmation("Sorry", "No records found"));
                    }
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Sorry", "No records found"));
                }
            }
            else
            {
                response.sendRedirect("/icici/logout.jsp");
                return;
            }
        }
    %>
</div>
</body>
</html>