<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.AgentDetailsVO" %>
<%@ page import="com.manager.vo.AgentManager" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.payoutVOs.AgentWireVO" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 12/12/14
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
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/agentid-memberid-terminalid.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();
        });
    </script>
    <title>Agent Wire Mananger </title>
</head>
<body>
<%!
    private static Logger logger=new Logger("agentWirelist.jsp");%>
<%
    String str2 = "";
    String memberId = Functions.checkStringNull(request.getParameter("toid")) == null ? "" : request.getParameter("toid");
    String agentId =Functions.checkStringNull(request.getParameter("agentid")) == null ? "" : request.getParameter("agentid");
    String terminalId =Functions.checkStringNull(request.getParameter("terminalid"))==null ? "" : request.getParameter("terminalid");
    String isPaid =Functions.checkStringNull(request.getParameter("isrr"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        try
        {
            AgentManager agentManager=new AgentManager();
            TerminalManager terminalManager=new TerminalManager();
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Agent Wire Manager
            </div><br>
            <form action="/icici/servlet/AgentWireList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
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

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                    int year = Calendar.getInstance().get(Calendar.YEAR);

                %>
                <table  border="0" align="center" width="98%" cellpadding="2" cellspacing="2">
                    <tr>
                        <td class="textb" colspan="6">&nbsp;</td>
                    </tr>
                    <tr>
                        <td  class="textb" >From</td>
                        <td  class="textb">
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
                        <td  class="textb" >To</td>
                        <td  class="textb">
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
                        <td class="textb" >
                            <select name="isrr" class="txtbox">
                                <%
                                    if("N".equals(isPaid)){%>
                                <option value=""></option>
                                <option value="Y">Paid</option>
                                <option value="N" selected>Unpaid</option>
                                <%}
                                else if("Y".equals(isPaid)){%>
                                <option value=""></option>
                                <option value="Y" selected>Paid</option>
                                <option value="N">Unpaid</option>
                                <%}
                                else{%>
                                <option value="" selected></option>
                                <option value="Y">Paid</option>
                                <option value="N">Unpaid</option>
                                <%}
                                %>
                            </Select>
                        </td>
                    </tr>
                    <tr>
                        <td  class="textb" colspan="6">&nbsp;</td>
                    </tr>
                    <tr>
                        <td  class="textb" colspan="6">&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="textb" >Agent ID:</td>
                        <td class="textb" >
                            <input name="agentid" id="aid-act" value="<%=agentId%>" class="txtbox" autocomplete="on">
                            <%--<select name="agentid" class="txtbox" id="bank">
                                <option value="0" selected></option>
                                <%
                                    String isSelected="";
                                    for(AgentDetailsVO agentVO : agentList){
                                        if (agentVO.getAgentId().equalsIgnoreCase(agentId)){
                                            isSelected = "selected";
                                        }
                                        else{
                                            isSelected = "";
                                        }
                                %>
                                <option value="<%=agentVO.getAgentId()%>" <%=isSelected%>><%=agentVO.getAgentId()%>-<%=agentVO.getAgentName()%></option>
                                <%
                                    }
                                %>
                            </select>--%>
                        </td>
                        <td class="textb" >Member Id:</td>
                        <td class="textb" >
                            <input name="toid" id="aid-act-mid" value="<%=memberId%>" class="txtbox" autocomplete="on">
                            <%--<select name="toid" id="accountid" class="txtbox">
                                <option data-bank="all"  value="0"></option>
                                <%
                                    for(MerchantDetailsVO merchantDetailsVO:merchantDetailsVOList)
                                    {
                                        /*//String isSelected1="";
                                        if (merchantDetailsVO.getMemberId().equalsIgnoreCase(memberId)){
                                            isSelected1 = "selected";
                                        }
                                        else{
                                            isSelected1 = "";
                                        }*/
                                %>
                                <option data-bank="<%=merchantDetailsVO.getAgentId()%>"  value="<%=merchantDetailsVO.getMemberId()%>" &lt;%&ndash;<%=isSelected1%>&ndash;%&gt;><%=merchantDetailsVO.getMemberId()+"-"+merchantDetailsVO.getCompany_name()%></option>
                                <%
                                    }
                                %>
                            </select>--%>
                        </td>
                        <td class="textb" >Terminal Id:</td>
                        <td class="textb" >
                            <input name="terminalid" id="aid-mid-tid" value="<%=terminalId%>" class="txtbox" autocomplete="on">
                            <%-- <select name="terminalid" id="memberid" class="txtbox" >
                                 <option data-accid="all" value="0" selected></option>
                                 <%
                                     for(String terminalId1:terminalVOTreeMap.keySet())
                                     {
                                         //String isSelected2="";
                                         TerminalVO terminalVO=terminalVOTreeMap.get(terminalId1);
                                        /* if (terminalVO.getTerminalId().equalsIgnoreCase(terminalId)){
                                             isSelected2 = "selected";
                                         }
                                         else{
                                             isSelected2 = "";
                                         }*/
                                 %>
                                 <option data-accid="<%=terminalVO.getMemberId()%>" value="<%=terminalId1%>" &lt;%&ndash;<%=isSelected2%>&ndash;%&gt;><%=terminalVO.getMemberId()+"-"+terminalId1+"-"+ GatewayAccountService.getPaymentMode(terminalVO.getPaymodeId()) + "-" + GatewayAccountService.getCardType(terminalVO.getCardTypeId())%></option>
                                 <%   }
                                 %>
                             </select>--%>
                        </td>
                    </tr>
                    <tr><td colspan="6">&nbsp;</td></tr>
                    <tr>
                        <td colspan="4">&nbsp;</td>
                        <td colspan="1" class="textb" align="right">
                            <button type="submit" class="buttonform" value="Submit">
                                <i class="fa fa-clock-o"></i>
                                &nbsp;&nbsp;Submit
                            </button>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable">
    <%
        Functions functions =new Functions();
        String action = (String) request.getAttribute("action");
        String conf = " ";
        if (action != null)
        {
            if (action.equalsIgnoreCase("view"))
            {
                conf = "disabled";
            }
            AgentWireVO agentWireVO=(AgentWireVO)request.getAttribute("agentWireVO");
            if (agentWireVO != null)
            {
                String style="class=tr1";

    %>
    <form action="/icici/servlet/UpdateAgentWire?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" name="id" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getSettledId())%>" <%=conf%>>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="true" name="isSubmitted">
        <table border="1" bordercolor="#ffffff" align="center" style="width:80%" class="table table-striped table-bordered table-green dataTable">
            <tr <%=style%>>
                <td class="th0" colspan="2">Agent Wire Manager:</td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">SettleId :</td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="toid" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getSettledId())%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Agent ID :</td>
                <td class="tr1"><input type="text" class="txtbox1" class="txtbox1" size="30" name="agentid" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getAgentId())%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Member ID:</td>
                <td class="tr1"><input type="text" class="txtbox1" class="txtbox1" size="30" name="toid" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getMemberId())%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Account Id : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="accountid" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getAccountId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Terminal Id: </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="terminalid" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getTerminalId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Pay Mode : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="paymodeid" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getPayModeId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Card Type : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="cardtypeid" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getCardTypeId())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Agent Charge Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="agentchargeamount" value="<%=ESAPI.encoder().encodeForHTML(Functions.convert2Decimal(String.valueOf(agentWireVO.getAgentChargeAmount())))%>" <%=conf%>> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Agent Unpaid Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="agentunpaidamount" value="<%=ESAPI.encoder().encodeForHTML(Functions.convert2Decimal(String.valueOf(agentWireVO.getAgentUnpaidAmount())))%>" <%=conf%>> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Agent Total Funded Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="agenttotalfundedamount" value="<%=ESAPI.encoder().encodeForHTML(Functions.convert2Decimal(String.valueOf(agentWireVO.getAgentTotalFundedAmount())))%>" <%=conf%>> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Currency : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="currency" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getCurrency())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Status : </td>
                <td class="tr1">
                    <% if(action.equalsIgnoreCase("view")){ %>
                    <input type="text" size="30"  class="txtbox" name="status" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getStatus())%>" <%=conf%>> </td>
                <% }else{  %>
                <select name="status">
                    <% if( agentWireVO.getStatus().equals("paid")){ %>
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
                    <input type="text" class="txtbox1" size="30" name="settledate" readonly class="datepicker"  value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML(agentWireVO.getSettleDate()))?ESAPI.encoder().encodeForHTML(agentWireVO.getSettleDate()):""%>" <%=conf%>>
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
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="wirecreationdate" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getWireCreationDate())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement Start Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="settlementstartdate" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getSettlementStartDate())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement End Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="settlementenddate" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getSettlementEndDate())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Decline Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getDeclinedCoverDateUpTo())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Reversal Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getReversedCoverDateUpTo())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Chargeback Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getChargebackCoverDateUpTo())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement Report File Name : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" style="width:90%" name="settlementreportfilename" value="<%=ESAPI.encoder().encodeForHTML(agentWireVO.getSettlementReportFileName())%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1" colspan="2" align="center"><input type="submit" value="Update" class="buttonform" <%=conf%>></td>
            </tr>
        </table>
    </form>
    <%}
    else
    {
        out.println(Functions.NewShowConfirmation("Result","No records found"));
    }
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Result","Action not defined"));
    }
    }
    catch(Exception e)
    {
        logger.error("Sql Exception in actionAgentWireManager:",e);
        e.printStackTrace();
        out.println(Functions.NewShowConfirmation("Error","Error while add record in wire manager."));
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
<script language="javascript">
    function getPdfFile(settleid){
        if (confirm("Do you really want to downloads selected file.")){
            document.getElementById("pdfform"+settleid).submit();
        }
    }
    $(function(){
        $('#bank').on('change', function(){
            var val=$(this).val();
            var val2 = $('#accountid').val();
            var sub=$('#memberid');
            var sub2= $('#accountid');
            if(val == '0') {
                sub2.find('option').not(':first').hide();
                $('option', sub2).filter(function(){
                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;
                            if($(this).parent().is('span')) {
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }
                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;
                            $(opt).show();
                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);
                            }
                        }
                    } else {
                        $(this).show(); //all other browsers use standard .show()
                    }
                    //$(this).show();
                });
                if(val2 == '0') {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                if($(this).parent().is('span')) {
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0")
                                {
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {

                                    $(span).replaceWith(opt);
                                }
                            }
                        } else {
                            $(this).show(); //all other browsers use standard .show()
                        }

                        //$(this).show();

                    });
                }
                else {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                if($(this).parent().is('span')) {
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0"){
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }
                        if($(this).attr('data-accid') == val2){
                            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 													{
                                if (this.nodeName.toUpperCase() === 'OPTION') {
                                    var span = $(this).parent();
                                    var opt = this;
                                    $(opt).show();
                                    if($(this).parent().is('span')) {
                                        $(span).replaceWith(opt);
                                    }
                                }
                            } else {
                                $(this).show(); //all other browsers use standard .show()
                            }
                            //$(this).show();
                        }
                    });
                }
            }
            else {
                sub2.find('option').not(':first').hide();
                $('option', sub2).filter(function(){
                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;
                            if($(this).parent().is('span')) {
                                $(span).replaceWith(opt);
                            }
                            if($(this).attr('value') != "0"){
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }
                        }
                    }
                    if($(this).attr('data-bank') == val){

                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 												{

                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {

                                    $(span).replaceWith(opt);
                                }

                            }
                        } else {
                            $(this).show(); //all other browsers use standard .show()
                        }
                        //$(this).show();
                    }
                });
                if(val2 == '0') {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;

                                if($(this).parent().is('span')) {
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0"){
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }
                        if($(this).attr('data-bank') == val){
                            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                                if (this.nodeName.toUpperCase() === 'OPTION') {
                                    var span = $(this).parent();
                                    var opt = this;
                                    $(opt).show();
                                    if($(this).parent().is('span')) {
                                        $(span).replaceWith(opt);
                                    }
                                }
                            } else {
                                $(this).show(); //all other browsers use standard .show()
                            }
                            //$(this).show();
                        }
                    });
                }
                else {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                if($(this).parent().is('span')) {
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0"){
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }
                        if($(this).attr('data-accid') == val2){
                            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                                if (this.nodeName.toUpperCase() === 'OPTION') {
                                    var span = $(this).parent();
                                    var opt = this;
                                    $(opt).show();
                                    if($(this).parent().is('span')) {

                                        $(span).replaceWith(opt);
                                    }
                                }
                            } else {
                                $(this).show(); //all other browsers use standard .show()
                            }
                            //$(this).show();
                        }
                    });
                }
            }
            sub.val(0);
            //sub2.val(0);
        });
    });
    $(function(){

        $('#accountid').on('change', function(){
            var val = $(this).val();
            var sub = $('#memberid');
            var val2 = $('#bank').val();
            if(val == '0') {
                if(val2 == '0'){
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION'){
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')){
                                    //$(opt).show();
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0")
                                {
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }

                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {

                                    $(span).replaceWith(opt);
                                }
                            }
                        } else {
                            $(this).show(); //all other browsers use standard .show()
                        }

                    });


                }
                else {
                    sub.find('option').not(':first').hide();

                    $('option', sub).filter(function(){

                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION')
                            {
                                var span = $(this).parent();
                                var opt = this;
                                //$(opt).show();
                                if($(this).parent().is('span'))
                                {
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0")
                                {
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }
                    });
                }
            }
            else {
                if(val2 == '0') {
                    sub.find('option').not(':first').hide();
                    $('option', sub).filter(function(){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION')
                            {
                                var span = $(this).parent();
                                var opt = this;
                                if($(this).parent().is('span'))
                                {
                                    //$(opt).show();
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0")
                                {
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }
                        if($(this).attr('data-accid') == val){
                            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                                if (this.nodeName.toUpperCase() === 'OPTION') {
                                    var span = $(this).parent();
                                    var opt = this;
                                    $(opt).show();
                                    if($(this).parent().is('span')) {
                                        //$(opt).show();
                                        $(span).replaceWith(opt);
                                    }
                                }
                            } else {
                                $(this).show(); //all other browsers use standard .show()
                            }

                            //$(this).show();
                        }
                    });
                }
                else {
                    sub.find('option').not(':first').hide();

                    $('option', sub).filter(function(){

                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION')
                            {
                                var span = $(this).parent();
                                var opt = this;
                                if($(this).parent().is('span'))
                                {
                                    //$(opt).show();
                                    $(span).replaceWith(opt);
                                }
                                if($(this).attr('value') != "0")
                                {
                                    $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                                }
                            }
                        }


                        if($(this).attr('data-accid') == val){
                            if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                                if (this.nodeName.toUpperCase() === 'OPTION') {
                                    var span = $(this).parent();
                                    var opt = this;
                                    $(opt).show();
                                    if($(this).parent().is('span')) {

                                        $(span).replaceWith(opt);
                                    }
                                }
                            } else {
                                $(this).show(); //all other browsers use standard .show()
                            }

                            //$(this).show();
                        }
                    });
                }

            }
            sub.val(0);
        });
    });


    $(function(){


        var val = $('#bank').val();
        var sub = $('#memberid');
        var sub2 = $('#accountid');
        var val2 = $('#accountid').val();


        if(val == '0') {

            // Populate Currency drop down

            sub2.find('option').not(':first').hide();

            $('option', sub2).filter(function(){

                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;

                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);

                        }
                        if($(this).attr('value') != "0")
                        {
                            $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                        }


                    }

                }


                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;
                        $(opt).show();
                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);
                        }
                    }
                } else {
                    $(this).show(); //all other browsers use standard .show()
                }

                //$(this).show();

            });


            if(val2 == '0') {


                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;

                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);

                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }


                        }

                    }


                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;
                            $(opt).show();
                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);
                            }
                        }
                    } else {
                        $(this).show(); //all other browsers use standard .show()
                    }

                    //$(this).show();

                });
            }
            else {
                sub.find('option').not(':first').hide();


                $('option', sub).filter(function(){


                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;

                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);

                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }


                        }

                    }



                    if($(this).attr('data-accid') == val2){

                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 													{
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {

                                    $(span).replaceWith(opt);
                                }
                            }
                        } else {
                            $(this).show(); //all other browsers use standard .show()
                        }

                        //$(this).show();
                    }


                });
            }
        }
        else {


            sub2.find('option').not(':first').hide();


            $('option', sub2).filter(function(){


                if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                    if (this.nodeName.toUpperCase() === 'OPTION') {
                        var span = $(this).parent();
                        var opt = this;

                        if($(this).parent().is('span')) {

                            $(span).replaceWith(opt);

                        }
                        if($(this).attr('value') != "0")
                        {
                            $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                        }


                    }

                }




                if($(this).attr('data-bank') == val){

                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') 												{

                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;
                            $(opt).show();
                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);
                            }

                        }
                    } else {
                        $(this).show(); //all other browsers use standard .show()
                    }
                    //$(this).show();
                }



            });



            if(val2 == '0') {


                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){


                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;

                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);

                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }


                        }

                    }




                    if($(this).attr('data-bank') == val){

                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {

                                    $(span).replaceWith(opt);
                                }

                            }
                        } else {
                            $(this).show(); //all other browsers use standard .show()
                        }
                        //$(this).show();
                    }



                });
            }
            else {
                sub.find('option').not(':first').hide();

                $('option', sub).filter(function(){


                    if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {

                        if (this.nodeName.toUpperCase() === 'OPTION') {
                            var span = $(this).parent();
                            var opt = this;

                            if($(this).parent().is('span')) {

                                $(span).replaceWith(opt);

                            }
                            if($(this).attr('value') != "0")
                            {
                                $(opt).wrap((navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') ? '<span>' : null);
                            }


                        }

                    }
                    if($(this).attr('data-bank') == val && $(this).attr('data-accid') == val2){
                        if(navigator.appName == 'Microsoft Internet Explorer'|| navigator.appName =='Netscape') {
                            if (this.nodeName.toUpperCase() === 'OPTION') {
                                var span = $(this).parent();
                                var opt = this;
                                $(opt).show();
                                if($(this).parent().is('span')) {

                                    $(span).replaceWith(opt);
                                }
                            }
                        } else {
                            $(this).show(); //all other browsers use standard .show()
                        }

                        //$(this).show();
                    }
                });
            }
        }
    });
</script>
</html>