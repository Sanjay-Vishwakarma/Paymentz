<%@ page import="com.directi.pg.Functions,
                 com.directi.pg.Logger,
                 com.directi.pg.core.GatewayAccount"%>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%!
    Logger logger=new Logger("fraudTransactionReversalList.jsp");
%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 10/4/14
  Time: 12:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

<html>
<head>

    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("trackingid");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function DoReverse()
        {
            var checkboxes = document.getElementsByName("trackingid");
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
                alert("Select at least one transaction");
                return false;
            }
            if (confirm("Do you really want to reverse all selected transaction."))
            {
                document.reversalform.submit();
            }
            else
            {
                return false;
            }
    }
    </script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>
    <title>Fraud Transaction Details> Reverse Fraud Transaction</title>
</head>
<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Reverse Fraud Transaction
            </div>
            <%
                ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
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

                    String accountid = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
                    String memberid = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");

                    Map terminalMap = GatewayTypeService.getAllTerminalsGroupByMerchant();
                    if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
                    else
                        pgtypeid="";
            %>
            <form action="/icici/servlet/FraudTransactionReverseList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    Hashtable statushash = new Hashtable();
                    String str="";
                    String fdate=null;
                    String tdate=null;
                    String fmonth=null;
                    String tmonth=null;
                    String fyear=null;
                    String tyear=null;
                    String accountId=request.getParameter("accountid");
                    String memberId=request.getParameter("memberid");


                    try
                    {
                        fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
                        tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
                        fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
                        tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
                        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
                        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
                    }
                    catch(ValidationException e)
                    {

                    }
                    Calendar rightNow = Calendar.getInstance();
                    String currentyear = "" + rightNow.get(Calendar.YEAR);

                    if (fdate == null) fdate = "" + 1;
                    if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
                    if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
                    if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
                    if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
                    if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);
                    str = str + "ctoken=" + ctoken;
                    if (fdate != null) str = str + "&fdate=" + fdate;
                    if (tdate != null) str = str + "&tdate=" + tdate;
                    if (fmonth != null) str = str + "&fmonth=" + fmonth;
                    if (tmonth != null) str = str + "&tmonth=" + tmonth;
                    if (fyear != null) str = str + "&fyear=" + fyear;
                    if (tyear != null) str = str + "&tyear=" + tyear;
                    if (memberId != null) str = str + "&memberid=" + memberId;
                    if (accountId != null) str = str + "&accountid=" + accountId;

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                %>
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >Gateway</td>

                                    <%--<td width="17%" class="textb"></td>--%>
                                    <td width="20%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">

                                    </td>
                                    <td width="5%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >Currency</td>
                                    <%--<td width="10%" class="textb"></td>--%>
                                    <td width="200%" class="textb">
                                        <select size="1" id="currency" class="txtbox" name="currency" style="width: 200px">
                                            <option value="--All--" default>--All--</option>
                                            <%
                                                Set<String> currencySet = new HashSet<String>();
                                                for(Object key : terminalMap.keySet())
                                                {
                                                    for(TerminalVO t : (List<TerminalVO>)terminalMap.get(key))
                                                    {
                                                        currencySet.add(t.getCurrency());
                                                    }
                                                }
                                                Iterator currencyIterator = currencySet.iterator();
                                                String sCurrency = "";
                                                while(currencyIterator.hasNext())
                                                {
                                                    sCurrency = (String)currencyIterator.next();
                                                    String isSelected = "";
                                                    if(currency.equalsIgnoreCase(sCurrency))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                            %>
                                            <option value="<%=sCurrency%>" <%=isSelected%>><%=sCurrency%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="70%" class="textb">&nbsp;</td>
                                    <td width="150%" class="textb">Account ID*</td>
                                    <%--<td width="17%" class="textb"></td>--%>
                                    <td width="80%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                    <%--<select size="1" id="sub_groups" name="accountid" class="txtbox" style= "width: 200px;margin-left: 12%" >
                                            <option data-bank="all" data-curr="all" value="0">Select AccountID</option>
                                            <%
                                                Set accSet = terminalMap.keySet();
                                                Iterator accountIterator = accSet.iterator();
                                                while(accountIterator.hasNext())
                                                {
                                                    String accId = (String)accountIterator.next();
                                                    GatewayAccount t = GatewayAccountService.getGatewayAccount(accId);
                                                    String isSelected = "";

                                                    String aGateway = t.getGateway();
                                                    String aCurrency = t.getCurrency();
                                                    String aMid = t.getMerchantId();
                                                    if(accId.equalsIgnoreCase(accountid))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                            %>
                                            <option data-group="<%=aGateway.toUpperCase()%>" data-curr="<%=aCurrency%>" value="<%=accId%>" <%=isSelected%>><%=accId+"-"+"-"+aMid+"-"+aCurrency+"-"+aGateway%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
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

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <%--<td width="3%" class="textb"></td>--%>
                                    <td width="12%" class="textb">
                                        <select size="1" name="fdate" value="" >
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth"  value="" >
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" value="" >
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To</td>
                                    <%--<td width="3%" class="textb"></td>--%>
                                    <td width="12%" class="textb">
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
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="70%" class="textb">&nbsp;</td>
                                    <td width="150%" class="textb">Member ID*</td>
                                    <%-- <td width="30%" class="textb"></td>--%>
                                    <td width="80%" class="textb">
                                        <input name="memberid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    <%--<select name="memberid" id="member_groups" class="txtbox" style="width:200px;margin-left: 12%">
                                            <option value="0" selected>--Select MemberID--</option>
                                            <%
                                                Iterator memberIterator = accountSet.iterator();
                                                while(memberIterator.hasNext())
                                                {
                                                    TerminalVO t = (TerminalVO)memberIterator.next();
                                                    String aGateway = t.getGateway();
                                                    String aCurrency = t.getCurrency();
                                                    String accId = t.getAccountId();
                                                    String aMemberId = t.getMemberId();
                                                    String aContactPerson = t.getContactPerson();
                                                    String aCompanyName= t.getCompany_name();
                                                    String isSelected = "";
                                                    if(aMemberId.equals(memberid))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                            %>
                                            <option data-group="<%=aGateway.toUpperCase()%>" data-curr="<%=aCurrency%>" data-accid="<%=accId%>" value="<%=aMemberId%>" <%=isSelected%>><%=aMemberId+"-"+aCompanyName%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
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
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
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
        String errormsg = (String)request.getAttribute("cbmessage");
        Functions functions=new Functions();
        Hashtable hash = (Hashtable) request.getAttribute("transdetails");
        Hashtable temphash=null;
        int records=0;
        int totalrecords=0;
        String error=(String ) request.getAttribute("errormessage");
        if(error !=null)
        {
            out.println("<table align=\"center\" font class=\"textb\"><tr><td><b>");
            out.println(error);
            out.println("</b></font></td></tr></table>");
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
        if(records>0)
        {
    %>
    <form name="reversalform" action="ReverseFraudTransactions?ctoken=<%=ctoken%>" method="post">
        <table align="center" width="90%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td width="8%" valign="middle" align="center" class="th0">Tracking Id</td>
                <td valign="middle" align="center" class="th0">Fraud Id</td>
                <td valign="middle" align="center" class="th0">Description</td>
                <td valign="middle" align="center" class="th0">Status</td>
                <td valign="middle" align="center" class="th0">Time</td>
                <td valign="middle" align="center" class="th0">Payment Order No</td>
                <td valign="middle" align="center" class="th0">Amount</td>
                <td valign="middle" align="center" class="th0">AccountId</td>
                <td valign="middle" align="center" class="th0">MemberId</td>
                <td valign="middle" align="center" class="th0">Fraud Score</td>
                <td valign="middle" align="center" class="th0">Fraud Alert Score</td>
                <td valign="middle" align="center" class="th0">Auto Reversal Score</td>
                <td valign="middle" align="center" class="th0">Reversal Action Taken</td>
                <td valign="middle" align="center" class="th0">Is Fraudulent Reverse</td>
            </tr>
            </thead>
            <%
                String style="class=td1";
                String ext="light";

                for(int pos=1;pos<=records;pos++)
                {
                    String id=Integer.toString(pos);

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
                    out.println("<td align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                    out.println("<td  align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fstransid"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"<input  type=\"hidden\" value="+ESAPI.encoder().encodeForHTML((String) temphash.get("status"))+" name=\"status_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\"></td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("time"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("paymentOrderNumber"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input  type=\"hidden\" value="+ESAPI.encoder().encodeForHTML((String) temphash.get("amount"))+" name=\"refundamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\"> <input  type=\"hidden\" value="+ESAPI.encoder().encodeForHTML((String) temphash.get("refundamount"))+" name=\"reversedamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\"></td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("accountid"))+"<input  type=\"hidden\" value="+ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+" name=\"accountid_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" size=\"10\"></td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input  type=\"hidden\" value="+ESAPI.encoder().encodeForHTML((String) temphash.get("toid"))+" name=\"memberid_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" size=\"10\"></td>");
                    out.println("<td align=\"center\"  "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("score"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("maxScoreAllowed"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("maxScoreAutoReversal"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isRefund"))+"</td>");
                    out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isFraud"))+"</td>");

                    out.println("</tr>");
                }
            %>
            <thead>
            <tr>
                <td valign="middle" align="center"  class="th0" colspan="15">
                    <button type="button" value="Reverse Selected" class="addnewmember" onClick="return DoReverse();" >
                        Reverse Selected
                    </button>
                </td>
            </tr>
            </thead>
        </table>
    </form>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="FraudTransactionReverseList"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
</div>
    <%
        }
        else if(functions.isValueNull(errormsg)){
            out.println(Functions.NewShowConfirmation("Result",errormsg));
        }
        else{
            out.println(Functions.NewShowConfirmation("Result","No Records Found"));
        }
    %>

<%
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
    }
%>
</body>
</html>