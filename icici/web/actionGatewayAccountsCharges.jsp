<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="servlets.ChargesUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.ChargeManager" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 2/12/15
  Time: 3:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd',endDate:'+10y'});
        });
    </script>
</head>
<body>
<%!
    private static Logger logger=new Logger("actiongatewayaccountcharges.jsp");
    private static CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    Functions functions=new Functions();
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
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"));

        String chargeId=request.getParameter("commissionname");

        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";
        if(accountid2!=null)str2 = str2 + "&accountid=" + accountid2;
        else
            accountid2="";
        if(currency!=null)str2 = str2 + "&currency=" + currency;
        else
            currency="";


        ChargeManager chargeManager=new ChargeManager();
        List<ChargeVO> chargeList=chargeManager.getListOfCharges();

        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();


%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Gateway Accounts Charges
                <div style="float: right;">
                    <form action="/icici/manageGatewayAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Add New Gateway Account Charge Mapping" name="submit" class="addnewmember" style="width:330px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Gateway Account Charge Mapping
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <form action="/icici/servlet/ListGatewayAccountsCharges?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    String accountid=request.getParameter("accountid");
                    String paymode=request.getParameter("paymode");
                    String cardtype=request.getParameter("cardtype");
                    String chargename=request.getParameter("chargename");
                    String chargevalue=request.getParameter("chargevalue");
                    String chargetype=request.getParameter("chargetype");

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                    String str="ctoken=" + ctoken;
                    if (accountid != null) str = str + "&accountid=" + accountid;
                    if (paymode != null) str = str + "&paymode=" + paymode;
                    if (cardtype != null) str = str + "&cardtype=" + cardtype;
                    if (chargename != null) str = str + "&chargename=" + chargename;
                    if (chargevalue != null) str = str + "&chargevalue=" + chargevalue;
                    if (chargetype != null) str = str + "&chargetype=" + chargetype;

                %>

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">

                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Gateway</td>
                                    <td width="25%" class="textb">
                                        <select size="1" id="bank" class="txtbox" name="pgtypeid" style="margin-left: 3%" >
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
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Accounts</td>
                                    <td width="25%" class="textb">
                                        <select size="1" id="accountid" name="accountid"  class="txtbox">
                                                <option data-bank="all"  value="0">Select AccountID</option>
                                            <%
                                                for(Integer sAccid : accountDetails.keySet())
                                                {
                                                    GatewayAccount g = accountDetails.get(sAccid);
                                                    String isSelected = "";
                                                    String gateway2 = g.getGateway().toUpperCase();
                                                    String currency2 = g.getCurrency();
                                                    String pgtype = g.getPgTypeId();
                                                    if (String.valueOf(sAccid).equals(accountid2))
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

                                <tr><td>&nbsp;&nbsp;</td></tr>

                                <tr>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Charge Name</td>
                                    <td width="12%" class="textb">
                                        <select class="txtbox" name="chargename" style="margin-left: 3%"><option value="" selected></option>
                                            <%
                                                for(ChargeVO chargeVO:chargeList)
                                                {
                                                    String isSelected="";
                                                    if(chargeVO.getChargeid().equals(chargeId))
                                                        isSelected="selected";
                                                    else
                                                        isSelected="";
                                            %>
                                            <option value="<%=chargeVO.getChargeid()%>" <%=isSelected%>><%=chargeVO.getChargeid()+"-"+chargeVO.getChargename()%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" value="Search" style="margin-left: 100%">
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
        String error = (String) request.getAttribute("errormessage");
        if (error != null)
        {
            out.println("<center><b class=textb>");
            out.println(error);
            out.println("</b><center>");

        }
        HashMap hash = (HashMap) request.getAttribute("chargedetails");
        String action = (String) request.getAttribute("action");
        if (hash != null)
        {
            if (action.equalsIgnoreCase("history"))
            {
    %>

    <table align=center class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td valign="middle" align="center" class="th0" >Sr. No</td>
            <td valign="middle" align="center" class="th0" >Mapping ID</td>
            <td valign="middle" align="center" class="th0" >Account ID</td>
            <td valign="middle" align="center" class="th0" >Charge Value</td>
            <td valign="middle" align="center" class="th0" >Start Date</td>
            <td valign="middle" align="center" class="th0" >End Date</td>
        </tr>
        <%
            Object mappingId;
            TreeMap mappingMap = new TreeMap(hash);
            Iterator itr2 = mappingMap.keySet().iterator();
            int i = 1;

            while (itr2.hasNext())
            {
                String style = "class=td1";
                mappingId = itr2.next();
                HashMap innerhash = (HashMap) mappingMap.get(mappingId);

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate=(String)innerhash.get("effectiveEndDate");
                String startDate=(String)innerhash.get("effectiveStartDate");

                if(innerhash!=null)
                {
                    if (functions.isValueNull((String)innerhash.get("effectiveEndDate")))
                    {
                        endDate=simpleDateFormat.format(simpleDateFormat.parse(endDate));
                    }
                    if (functions.isValueNull((String)innerhash.get("effectiveStartDate")))
                    {
                        startDate = simpleDateFormat.format(simpleDateFormat.parse(startDate));
                    }
                }
                out.println("<tr>");
                out.println("<td " + style + " >&nbsp;" + i + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("mappingid")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("accountid")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("gatewayChargeValue")) + "</td>");
                out.println("<td " + style + " >&nbsp;" +ESAPI.encoder().encodeForHTML(startDate) + "</td>");
                out.println("<td " + style + " >&nbsp;" +ESAPI.encoder().encodeForHTML(endDate) + "</td>");
                out.println("</tr>");
                i = i + 1;
            }

        %>
    </table>

    <%
    }
    else
    {
        String style = "class=tr0";
        String accountId = (String) hash.get("accountid");
        String gatewayName = ChargesUtils.getGatewayName(accountId);
    %> <form action="/icici/servlet/UpdateGatewayAccountsCharges?ctoken=<%=ctoken%>" method="post" name="forms" >
    <table align=center class="table table-striped table-bordered table-green dataTable" style="width:50% ">
        <tr <%=style%>>
            <td class="th0" colspan="2"><b>Update Charges</b></td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Account Id : </td>
            <td class="tr1"><input type="text" class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("accountid"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Gateway Name : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(gatewayName)%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Input Required : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("isinputrequired"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Charge Value : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="chargevalue" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("chargevalue"))%>"> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Start Date : </td>
            <td class="tr1"><input type="text"  size="30" readonly class="datepicker" name="effectivestartdate" value="<%=commonFunctionUtil.convertTimestampToDatepicker(getPreviousDate((String) hash.get("lastupdateDate")))%>"> <input type="hidden" name="mappingid" value="<%=(String)hash.get("mappingid")%>"> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">End Date : </td>
            <td class="tr1"><input type="text"  size="30" readonly class="datepicker" name="effectiveenddate" value=""> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Seq No : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="sequencenum" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("sequencenum"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="td1"></td>
            <td class="td1"><input type="submit" align="center" class="button" value="Update"></td>
        </tr>
    </table>
    <input type="hidden" size="30" name="mappingid" value="<%=(String) hash.get("mappingid")%>">
    <input type="hidden" size="30" name="pstartdate" value="<%=(String) hash.get("effectiveStartDate")%>">
    <input type="hidden" size="30" name="penddate" value="<%=(String) hash.get("lastupdateDate")%>">
</form>

    <%
                }
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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

<%!
    public String getPreviousDate(String source)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = null;
        try
        {

            date2 = sdf.parse(source);


        }
        catch (ParseException e)
        {
            logger.error("Parse Exception while  getting  PreviousDate",e);

        }
        Date dateBefore = new Date(date2.getTime() + 1 * 24 * 3600 * 1000 );
        String sDate2=sdf.format(dateBefore);
        return sDate2;
    }

%>
