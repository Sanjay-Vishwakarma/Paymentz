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
    <title>Agent Management> Agent Wire Mananger </title>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/agentid-memberid-terminalid.js"></script>

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
           // List<AgentDetailsVO> agentList = agentManager.getAgentDetails();
           // List<MerchantDetailsVO> merchantDetailsVOList=agentManager.getAgentMemberListFromMapping();
            //LinkedHashMap<String,TerminalVO> terminalVOTreeMap=terminalManager.getTerminalMap();
%>
    <div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Agent Wire Manager
            </div><br>
            <form action="/icici/servlet/AgentWireList?ctoken=<%=ctoken%>" method="post" name="forms" >
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
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");

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
                logger.error("Records & TotalRecords is found null",ex);
            }

            if(records>0)
            {
    %>
    <table align=center width="90%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr No</td>
            <td valign="middle" align="center" class="th0">Agent ID</td>
            <td valign="middle" align="center" class="th0">Member ID</td>
            <td valign="middle" align="center" class="th0">Wire Creation On</td>
            <td valign="middle" align="center" class="th0">Settle Start Date</td>
            <td valign="middle" align="center" class="th0">Settle End Date</td>
            <td valign="middle" align="center" class="th0">Agent Funded Amount</td>
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
                String agentId1=ESAPI.encoder().encodeForHTML((String)temphash.get("agentid"));
                String memberId1=ESAPI.encoder().encodeForHTML((String)temphash.get("toid"));
                String wireCreationTime=ESAPI.encoder().encodeForHTML((String)temphash.get("wirecreationtime"));
                String firstDate=ESAPI.encoder().encodeForHTML((String)temphash.get("settlementstartdate"));
                String lastDate=ESAPI.encoder().encodeForHTML((String)temphash.get("settlementenddate"));
                String netFinalAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("agenttotalfundedamount"));
                String unpaidAmount=ESAPI.encoder().encodeForHTML((String)temphash.get("agentunpaidamount"));
                String currency2=ESAPI.encoder().encodeForHTML((String)temphash.get("currency"));
                String status=ESAPI.encoder().encodeForHTML((String)temphash.get("status"));
        %>
        <tr>
            <td align="center" <%=style%>><%=srno%></td>
            <td align="center" <%=style%>><%=agentId1%></td>
            <td align="center" <%=style%>><%=memberId1%></td>
            <td align="center"<%=style%>><%=wireCreationTime%></td>
            <td align="center"<%=style%>><%=firstDate%></td>
            <td align="center"<%=style%>><%=lastDate%></td>
            <td align="center"<%=style%>><%=netFinalAmount%></td>
            <td align="center"<%=style%>><%=unpaidAmount%></td>
            <td align="center"<%=style%>><%=currency2%></td>
            <td align="center"<%=style%>><%=status%></td>
            <td align="center"<%=style%>><form id="pdfform<%=settleId%>" action="/icici/servlet/ActionAgentWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="sendPdfFile">  </form><a href="javascript: getPdfFile(<%=settleId%>)"><img width="20" height="28" border="0" src="/icici/images/pdflogo.jpg"></a></td>
            <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionAgentWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="view"><input type="submit" class="gotoauto"    value="View"></form></td>
            <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionAgentWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="update"><input type="submit" class="gotoauto"  value="Update"></form></td>
            <td <%=style%>>&nbsp;<form action="/icici/servlet/ActionAgentWireManager?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=settleId%>><input type="hidden" name="action" value="delete"><input type="submit" class="gotoauto"  value="Delete"></form></td>
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
                <jsp:param name="page" value="AgentWireList"/>
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
            }

            catch(Exception e)
            {
                logger.error("Exception ::",e);
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
        if (confirm("Do you really want to download selected file.")){
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