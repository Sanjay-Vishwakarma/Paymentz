<%--
  Created by IntelliJ IDEA.
  User: WAHEED
  Date: 6/3/14
  Time: 6:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="index.jsp" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>--%>
    <link rel="stylesheet" href="/resources/demos/style.css">
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
    <title>Action History> Rolling Reserve Manager> Generate Rolling Reserve Release</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");
        Hashtable temphash=null;
        int records=0;
        int totalrecords=0;
        String str="ctoken=" + ctoken;
        String accountid = request.getParameter("accountid");
        if (accountid != null) str = str + "&accountid=" + accountid;
        String fromDate = request.getParameter("fromDate");
        if (fromDate != null) str = str + "&fromDate=" + fromDate;
        String toDate = request.getParameter("toDate");
        if (toDate != null) str = str + "&toDate=" + toDate;
        String time_difference_daylight = request.getParameter("time_difference_daylight");
        if (time_difference_daylight != null) str = str + "&time_difference_daylight=" + time_difference_daylight;
        String mybutton = request.getParameter("mybutton");
        if (mybutton != null) str = str + "&mybutton=" + mybutton;
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        String errormsg=(String)request.getAttribute("message");
        if(errormsg!=null)
        {
            out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");
        }
        String currentblock=request.getParameter("currentblock");
        if(currentblock==null)
            currentblock="1";

        try
        {
            records=Integer.parseInt((String)hash.get("records"));
            totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
        }
        catch(Exception ex)
        {

        }
        if(hash!=null)
        {
            hash = (Hashtable)request.getAttribute("transdetails");
        }

        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("gateway"))==null?"":request.getParameter("gateway");
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");;

        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();

        if (pgtypeid!=null)str = str + "&gateway=" + pgtypeid;
        else
            pgtypeid="";
        if(currency!=null)str = str + "&currency=" + currency;
        else
            currency="";
        if(accountid2!=null)str = str + "&accountid=" + accountid2;
        else
            accountid2="";

%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Generate Rolling Reserve File
            </div>

            <form name = "FIRCForm" action="/icici/servlet/ExportReserveFile?ctoken=<%=ctoken%>" method="post"  >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table  align="center" width="97%" cellpadding="2" cellspacing="2" style="margin-left:0.5%;margin-right: 2.5% ">
                    <%
                        String error=(String ) request.getAttribute("error");
                        if(error !=null)
                        {
                            out.println("<BR>");
                            out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" ><b>");
                            out.println(error);
                            out.println("</b></font></td></tr></table>");
                            out.println("<BR>");
                        }
                    %>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >From</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="0%" class="textb">
                                        <input name="fromDate" type="text" style="width: 143px;" readonly class="datepicker" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("fromDate"))==null?"":ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("fromDate"))%>">
                                    </td>
                                    <td width="5%" class="textb">&nbsp;</td>
                                    <td width="10%" align="right" class="textb" >To</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="0%" class="textb">
                                        <input name="toDate" type="text" style="width: 143px;" readonly class="datepicker" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("toDate"))==null?"":ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("toDate"))%>">
                                    </td>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="15%" class="textb">Day Light Saving</td>
                                    <td width="10%" class="textb"></td>
                                    <td width="80%" class="textb">
                                        <select size="1" name="time_difference_daylight" class="txtbox" style="width: 70px">
                                            <option value="N">No</option>
                                            <option value="Y">Yes</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb" >&nbsp;</td>
                                    <td width="14%" class="textb">Gateway*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="gateway" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    <%-- <select size="1" id="bank" class="txtbox" name="gateway" style="margin-left: 3%" >
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

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="15%" class="textb">AccountID</td>
                                    <td width="80%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountid2%>" class="txtbox" autocomplete="on">
                                    <%-- <select size="1" id="accountid" name="accountid" class="txtbox">
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
                                        </select>--%>
                                    </td>

                                    <td width="2%" class="textb"></td>
                                    <td width="8%" class="textb"><button name="mybutton" type="submit" value="View" class="buttonform">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;View
                                    </button></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb"><button name="mybutton" type="submit" value="Generate Excel File" style="width:160px " class="buttonform">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Generate Excel File
                                    </button></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb"></td>
                                    <td width="8%" class="textb" ><%if(request.getAttribute("time_difference_normal")!=null){%>Time Difference Normal<%}%></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb"><%if(request.getAttribute("time_difference_normal")!=null){%><input name="time_difference_normal" type="text" value="<%= request.getAttribute("time_difference_normal").toString() %>" disabled=""><%}%></td>
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
        if(records>0)
        {
    %>

    <center><p class="textb">Rolling File</p></center>
    <br>
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">Tracking Id </td>
            <td valign="middle" align="center" class="th0"> Description</td>
            <td valign="middle" align="center" class="th0">Account Id</td>
            <td valign="middle" align="center" class="th0">Transaction amount </td>
            <td valign="middle" align="center" class="th0">Rolling amount</td>
        </tr>
        </thead>
        <%
            String style="class=td1";
            String ext="light";

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
                out.println("<tr>");
                out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"<input type=\"hidden\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description\" value=\""+temphash.get("description")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"<input type=\"hidden\" name=\"accountid\" value=\""+temphash.get("accountid")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("amount"))+"<input type=\"hidden\" name=\"amount\" value=\""+temphash.get("amount")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("RollingReserveAmountKept"))+"<input type=\"hidden\" name=\"RollingReserveAmountKept\" value=\""+temphash.get("RollingReserveAmountKept")+"\"></td>");
                out.println("</tr>");
            }
        %>
        <thead>
        <tr>
            <td  align="center" class=th0>Total Records: <%=totalrecords%></td>
            <td  align="center" class=th0>Page No <%=pageno%></td>
            <td class=th0></td>
            <td class=th0></td>
            <td class=th0></td>
            <td class=th0></td>
        </tr>
        </thead>

    </table>
    <table align=center valign=top ><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="ExportReserveFile"/>
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
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
    %>
</div>
</body>
</html>