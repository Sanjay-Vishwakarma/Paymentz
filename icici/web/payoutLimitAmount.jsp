<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.directi.pg.Merchants" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%!
    private static Logger log   = new Logger("payoutLimitAmount.jsp");
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<script>
    /*function validateInput(){

        var error           = "";
        var memberid        =  document.getElementById("mid").value;
        var terminalId      =  document.getElementById("terminalid").value;

        console.log("memberid ",+memberid);
        console.log("terminalId ",+terminalId);

        if(memberid == ""){
            error += "Please Enter Member Id \n";
        }

        if(terminalId == ""){
            error += "Please Enter Teminal Id \n";
        }

        console.log("validateInput "+error);
        if (error != "")
        {
            alert(error);
            return false;
        }
        else
        {
            document.getElementById("copyTerminal").submit();
        }
    }*/
</script>
<script type="text/javascript">

//    function ToggleAll(checkbox)
//    {
//        flag= checkbox.checked;
//        var checkboxes= document.getElementsByName("id");
//        var total_boxes= checkboxes.length;
//
//        for(i=0; i<total_boxes; i++)
//        {
//            checkboxes[i].checked= flag;
//        }
//    }
//
//    function UpdateAllSelectedRecords()
//    {
//        var checkboxes= document.getElementsByName("id");
//        var checked=[];
//
//        var total_boxes= checkboxes.length;
//        flag= false;
//        for(i=0; i<total_boxes; i++)
//        {
//            if(checkboxes[i].checked)
//            {
//                flag= true;
//                checked.push(checkboxes[i].value);
//                checked.join(',');
//            }
//        }
//        document.getElementById("ids").value= checked;
//        if(!flag)
//        {
//            alert("Select at least one record");
//            return false;
//        }
//        if(confirm("Do you really want to Update all selected Data."))
//        {
//            document.updateAll.submit();
//        }
//    }
//
//        for(i=0; i<total_boxes; i++)
//        {
//            checkboxes[i].checked= flag;
//        }
//    }

function editInput(id)
{
    var disabled2 = document.getElementById([id]).disabled;
    if (disabled2)
    {
        document.getElementById([id]).disabled = false;
    }
    else
    {
        document.getElementById([id]).disabled = true;
    }
}
// to send user edited value to hidden field, getting userEdited value from field as id and pass that id inside hiddenid.val( as value ).
    function copyValue(id,hiddenid){
        var pay = $('#'+id).val();
        var payhidden = $('#'+hiddenid).val();

        $('#'+hiddenid).val($('#'+id).val());
    }

    function copyValue2(id,hiddenid){
        $('#'+hiddenid).val($('#'+id).val());
    }
</script>
<html>
<head>
</head>
<title> Settings> Payout Amount Limit </title>
<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Payout Amount Limit
            </div>
            <%
                ctoken                  = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                Merchants merchants     = new Merchants();
                if (merchants.isLoggedIn(session))
                {

                    String memberId                     = Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
                    String currentPayoutAmount          = "";
                    String addedPayoutAmount            = "";
                    String accountid                    = "";
                    String payoutAmountLimitId          = "";
                    Hashtable<String,String> hashtable  = null;
                    boolean showUpdateButton            = true;
                    String pgtypeid                     = "";
                    String gatewayname                  = Functions.checkStringNull(request.getParameter("gatewayname")) == null ? "" : request.getParameter("gatewayname");

                    if(request.getAttribute("requesHashtable") != null){
                        hashtable           = (Hashtable<String,String>)request.getAttribute("requesHashtable");
                        pgtypeid            = (String)request.getAttribute("pgtypeid");
                        if(!hashtable.isEmpty()){
                            accountid             = (String) hashtable.get("accountid");
                            addedPayoutAmount     = (String) hashtable.get("addedPayoutAmount");
                            currentPayoutAmount   = (String) hashtable.get("currentPayoutAmount");

                        }
                    }
            %>
            <form id="copyTerminal" action="/icici/servlet/PayoutAmountLimit?ctoken=<%=ctoken%>" name="copyTerminal" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <input type="hidden" value="<%=payoutAmountLimitId%>" name="payoutAmountLimitId" id="payoutAmountLimitId">
                <br>
                <table align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <%
                                String errormsg1 = (String) request.getAttribute("error");
                                if (errormsg1 != null)
                                {
                                    out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                                }
                                String errormsg = (String) request.getAttribute("cbmessage");
                                if (errormsg == null)
                                    errormsg = "";
                                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                                out.println(errormsg);
                                out.println("</b></font></td></tr></table>");
                            %>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                </tr>
                                <%--<tr>--%>
                                    <%--<td width="43%" class="textb">Gateway Name</td>--%>
                                    <%--<td width="5%" class="textb">:</td>--%>
                                    <%--<td>--%>
                                        <input type="hidden" name="gatewayname" id="gatewayname" value="" class="txtbox">
                                    <%--</td>--%>
                                <%--</tr>--%>
                                <%--<tr><td>&nbsp;&nbsp;</td></tr>--%>
                                <tr>
                                    <td width="43%" class="textb">Gateway*</td>
                                    <td width="5%" class="textb">:</td>
                                    <td>
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on" onkeyup="copyValue2('gateway1','gatewayname')">
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr>
                                    <td width="43%" class="textb">Account Id*</td>
                                    <td width="5%" class="textb">:</td>
                                    <td>
                                         <%--<input name="accountId" id="accid" value="<%=accountid%>"  class="txtbox" autocomplete="on">--%>
                                         <input name="accountId"  id="accountid1" value="<%=accountid%>"  class="txtbox" autocomplete="on">
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr>
                                    <td class="textb">Current Payout Amount</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <input name="currentPayoutAmount" id="currentPayoutAmount" value="<%=currentPayoutAmount%>" class="txtbox" autocomplete="on">
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr>
                                    <td class="textb">Added Payout Amount</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <input name="addedPayoutAmount" id="addedPayoutAmount" value="<%=addedPayoutAmount%>" class="txtbox" autocomplete="on">
                                    </td>
                                </tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr><td>&nbsp;&nbsp;</td></tr>
                                <tr>
                                    <td>&nbsp;&nbsp;</td>
                                    <td>&nbsp;&nbsp;</td>
                                    <td>&nbsp;&nbsp;</td>
                                    <td style="display: inline-flex">
                                        <button type="submit" name="action" value="search"
                                                class="btn btn-default center-block"
                                                onclick="validateInput()"
                                                style="display: inline-block!important;min-width:30%;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                        &nbsp;&nbsp;
                                        <%if(showUpdateButton){%>
                                        <button type="submit" name="action" value="update"
                                                style="display: inline-block!important;min-width:30%;"
                                                class="btn btn-default center-block">
                                            <i class="fa fa-ban"></i>
                                            &nbsp;&nbsp;Update
                                        </button>
                                        &nbsp;&nbsp;
                                        <button type="submit" name="action" value="save"
                                                style="display: inline-block!important;min-width:30%;"
                                                class="btn btn-default center-block">
                                            <i class="fa fa-ban"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                        <%}%>
                                        &nbsp;&nbsp;
                                        <button type="submit" name="action" value="showPayoutDetails"
                                                style="display: inline-block!important;min-width:50%"
                                                class="btn btn-default center-block">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Show Payout Details
                                        </button>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
<%
    try
    {
        Hashtable hash = (Hashtable) request.getAttribute("CurrentPayoutDetails");
        if (hash != null && hash.size() > 0)
        {
%>
<div id="form" class="reporttable">
    <div class="panel-heading">Payout Amount Details</div>
    <div style="width:100%; overflow: auto">
        <%
            int pageno          = 1;
            int pagerecords     = 1;
            int records         = 0;
            int totalrecords    = 0;
            String currentblock = "1";

            if (request.getParameter("currentblock") != null)
                currentblock = request.getParameter("currentblock");

            records      = Integer.parseInt((String) hash.get("records"));
            totalrecords = Integer.parseInt((String) hash.get("totalrecords"));

            pageno     = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
            pagerecords= convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

            String str="ctoken=" + ctoken;
            str = str + "&action=showPayoutDetails";
            str = str + "&gatewayname=" +gatewayname;

            if (records > 0)
            {
        %>
        <table border="1" cellpadding="5" cellspacing="0" width="750" align="center" class="table table-striped table-bordered table-hover table-green dataTable" style="width: 100%;overflow: auto;">
            <thead>
            <tr>
                <%--<td width="4%" class="th0">--%>
                    <%--<b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></b>--%>
                <%--</td>--%>
                <td class="th0"> Gateway</td>
                <td class="th0"> Account Id</td>
                <td class="th0"> Alias Name</td>
                <td class="th0"> Current Payout Amount</td>
                <td class="th0"> Added Payout Amount</td>
                <td class="th0"> Action</td>
            </tr>
            </thead>
            <%
                Hashtable innerHash = null;
                Functions functions = new Functions();
                String style        = "class=td0";

                    for (int pos = 1; pos <= hash.size(); pos++)
                    {
                        style = "class=\"tr" + pos % 2 + "\"";
                        String id = Integer.toString(pos);
                        innerHash = (Hashtable) hash.get(id);

                        if (innerHash != null && innerHash.size() > 0)
                        {
                            String aliasName = String.valueOf(innerHash.get("aliasname"));
                            String accId = String.valueOf(innerHash.get("accountid"));
                            String pgtypeidGateway = String.valueOf(innerHash.get("gateway"));
                            String currPayAmount = String.valueOf(innerHash.get("currentPayoutAmount"));
                            String addPayAmount = String.valueOf(innerHash.get("addedPayoutAmount"));

                            if (functions.isEmptyOrNull(aliasName))
                                aliasName = "-";
                            if (functions.isEmptyOrNull(accId))
                                accId = "-";
                            if (functions.isEmptyOrNull(pgtypeidGateway))
                                pgtypeidGateway = "-";
                            if (functions.isEmptyOrNull(currPayAmount))
                                currPayAmount = "-";
                            if (functions.isEmptyOrNull(addPayAmount))
                                addPayAmount = "-";

            %>
            <tr <%=style%>>
                <%--<td align=center <%=style%>>&nbsp;--%>
                    <%--<input type="checkbox" name="id" value="<%=ESAPI.encoder().encodeForHTML(accId+"~"/*+request.getParameter("addedPayoutAmount")*/)%>">--%>
                <%--</td>--%>
                <td align=center> <%=ESAPI.encoder().encodeForHTML(pgtypeidGateway)%></td>
                <td align=center> <%=ESAPI.encoder().encodeForHTML(accId)%> </td>
                <td align=center> <%=ESAPI.encoder().encodeForHTMLAttribute(aliasName)%> </td>
                <td align=center> <input type="text" name="currentPayoutAmount_<%=pos%>" id="currPayAmount_<%=pos%>" disabled value=<%=ESAPI.encoder().encodeForHTML(currPayAmount)%>></td>
                <td align=center> <input type="text" name="addedPayoutAmount_<%=pos%>" id="addPayAmount_<%=pos%>" onkeyup="copyValue('addPayAmount_<%=pos%>','addPayAmountHidden_<%=pos%>')" disabled value=<%=ESAPI.encoder().encodeForHTML(addPayAmount)%>></td>
                <td align=center>
                    <form action="/icici/servlet/PayoutAmountLimit?ctoken=<%=ctoken%>" method="post">
                        <button type="button" class="goto" name="edit" id="edit" value="edit" onclick="editInput('addPayAmount_<%=pos%>')"> Edit </button>
                        &nbsp;
                        <%--<input type="hidden" name="ctoken" value="<%=ctoken%>"/>--%>
                        <%--<input type="hidden" name="hideaction" value="showPayoutDetails"/>--%>
                        <input type="hidden" name="accountId" id="accountIdHidden" value="<%=accId%>" class="txtbox">
                        <input type="hidden" name="addedPayoutAmount" id="addPayAmountHidden_<%=pos%>" value=<%=ESAPI.encoder().encodeForHTML(addPayAmount)%>>
                        <button type="submit" id="updateSingleRecord" class="goto" name="action" value="updateSingleRecord"> Update </button>
                    </form>
                </td>
            </tr>
            <%
                        }
                    }
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
                }
            %>
        </table>
        <%--<form action="/icici/servlet/PayoutAmountLimit?ctoken=<%=ctoken%>" name="updateAll" method="post">--%>
            <%--<input type="hidden" id="ids" name="ids" value="">--%>
            <%--<input type="hidden" name="hideaction" value="showPayoutDetails"/>--%>
            <%--<button type="submit" name="action" value="updateSelectedRecords" class="btn btn-default center-block" onclick=" return UpdateAllSelectedRecords()">--%>
                <%--<i class="fa fa-clock-o"></i> &nbsp;&nbsp; Update All--%>
            <%--</button>--%>
        <%--</form>--%>
        <td class="th0" align="center" class=textb>Page No:- <%=pageno%></td>
        <td class="th0"align="center" class=textb>Total Records: <%=totalrecords%></td>
    </div>
    <table align=center valign=top>
        <tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                    <jsp:param name="numrows" value="<%=pagerecords%>"/>
                    <jsp:param name="pageno" value="<%=pageno%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="PayoutAmountLimit"/>
                    <jsp:param name="currentblock" value="<%=currentblock%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
            </td>
    </table>
</div>
<%
            }
        }
        catch (Exception e)
        {
            log.error("Exception in Paayout : " + e);
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>

</body>
</html>
