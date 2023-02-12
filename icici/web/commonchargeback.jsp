<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="com.manager.vo.payoutVOs.CBReasonsVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 4/9/13
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Common Integration> Common Chargeback </title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
    <script language="javascript">
        $(function() {
            $( ".datepicker" ).datepicker({format: 'yyyy-mm-dd'});
        });

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
        function DoChargeback()
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
            if (confirm("Do you really want to chargeback all selected transaction."))
            {
                document.chargebackform.submit();
            }
        }
        function doUpdateRetrivalRequest()
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

            if (confirm("Do you really want to update all selected transaction."))
            {
                document.chargebackform.submit();
            }
        }
    </script>
</head>
<%
    Functions functions = new Functions();
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String accountid = request.getParameter("accountid")==null?"":request.getParameter("accountid");
    String memberid = request.getParameter("toid")==null?"":request.getParameter("toid");
    String pgtypeid = request.getParameter("pgtypeid")==null?"":request.getParameter("pgtypeid");
    String fromType = request.getParameter("fromtype")==null?"":request.getParameter("fromtype");
    String currency = request.getParameter("currency")==null?"":request.getParameter("currency");
    String toid = Functions.checkStringNull(request.getParameter("toid"))==null?"":request.getParameter("toid");
    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str="";
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;

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
        String currentyear= "" + rightNow.get(rightNow.YEAR);
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        if (pgtypeid == null) pgtypeid = "";
        if (accountid == null) accountid = "";
        if (toid == null) toid = "";
        if (currency == null) currency = "";

        if (pgtypeid !=null) str = str + "&pgtypeid=" + pgtypeid;
        if (accountid !=null) str = str + "&accountid=" + accountid;
        if (toid != null) str = str + "&toid=" + toid;
        if (currency != null) str = str + "&currency=" + currency;
%>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<form action="/icici/servlet/CommonChargebackList?ctoken=<%=ctoken%>" method="post" name="forms">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Common Chargeback
                </div>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                <%
                    String errorMsg=(String) request.getAttribute("errorMsg");
                    if(errorMsg !=null)
                    {
                        out.println("<center><font class=\"textb\"><b>"+errorMsg+"</b></font></center>");
                    }
                %>
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5%">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="fdate" class="textb">
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="textb">
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" class="textb">
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2013));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="tdate" class="textb">
                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>

                                        <select size="1" name="tmonth" class="textb">
                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>

                                        <select size="1" name="tyear" class="textb" style="width:54px;">
                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2013));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Payment/Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="paymentid" maxlength="50" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("paymentid")==null?"":request.getParameter("paymentid"))%>">
                                    </td>
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
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Gateway</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Account Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
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
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Tracking ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="500" type="text" name="trackingid"  class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("trackingid")==null?"":request.getParameter("trackingid"))%>">
                                    </td>

                                    <td colspan="6" class="textb" align="center">
                                        <button type="submit" class="buttonform" name="B1" style="margin-left: 104%;">
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
            </div>
        </div>
    </div>
</form>
<div class="reporttable">
    <%
        String errormsg1 = (String)request.getAttribute("cbmessage");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<center><table align=\"center\" class=\"textb\" >" );
            out.println(errormsg1);
            out.println("</table></center>");
        }

        String error=(String) request.getAttribute("error");
        if(error !=null)
        {
            out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
        }
        String currentblock=request.getParameter("currentblock");

        if(currentblock==null)
            currentblock="1";

        if(currentblock==null)
            currentblock="1";
        if(request.getAttribute("cbVO")!=null)
        {
            List<TransactionVO> cbVOList= (List<TransactionVO>) request.getAttribute("cbVO");
            List<CBReasonsVO> reasonVOList= (List<CBReasonsVO>) request.getAttribute("reason");
            PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
            paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
            if(cbVOList.size()>0)
            {
    %>
    <form name="chargebackform" action="CommonDoChargeback?ctoken=<%=ctoken%>" method="post" style="width: 100%;overflow: auto;">
        <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
                <td style="display:none"></td>
                <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td valign="middle" align="center" class="th0">Sr No.</td>
                <td valign="middle" align="center" class="th0">TrackingID</td>
                <td valign="middle" align="center" class="th0">AccountID</td>
                <td valign="middle" align="center" class="th0">Toid</td>
                <td valign="middle" align="center" class="th0">Description</td>
                <td width="10%" valign="middle" align="center" class="th0">Capture&nbsp;Amount</td>
                <td width="10%" valign="middle" align="center" class="th0">Refund&nbsp;Amount</td>
                <td width="10%" valign="middle" align="center" class="th0">Chargeback&nbsp;Amount</td>
                <td width="10%" valign="middle" align="center" class="th0">isRetrivalRequest</td>
                <td valign="middle" align="center" class="th0">PaymentID</td>
                <td valign="middle" align="center" class="th0">status</td>
                <td valign="middle" align="center" class="th0">Time</td>
                <td valign="middle" align="center" class="th0">CB&nbsp;Amount</td>
                <td valign="middle" align="center" class="th0">CB&nbsp;Reason</td>
                <td valign="middle" align="center" class="th0">CB&nbsp;Date</td>
                <td valign="middle" align="center" class="th0">RetrivalRequest</td>
                <td valign="middle" align="center" class="th0">Blacklist Details</td>
                <td valign="middle" align="center" class="th0">Refund Details</td>
            </tr>
            </thead>
            <%
                String style="class=td1";
                String ext="light";
                Hashtable inner=null;
                int sno=1;
                for(TransactionVO transactionVO : cbVOList)
                {
                    int pos=1;
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
                    int srno=sno+ ((pageno-1)*pagerecords);
                    out.println("<tr>");
                    out.println("<td style=\"display:none\"><input type=\"hidden\" name=\"notificationURL_"+transactionVO.getTrackingId()+"\"  value=\""+transactionVO.getNotificationUrl()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+transactionVO.getTrackingId()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;" +srno+"</td>");
                    out.println("<td align=center " + style + ">&nbsp;" +ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId())+"</td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getAccountId())+"<input type=\"hidden\" name=\"accountid_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getAccountId()+"\"></td>");
                    out.println("<td align=center "+style + ">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getToid())+"<input type=\"hidden\" name=\"toid_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getToid()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getOrderDesc())+"<input type=\"hidden\" name=\"desc_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getOrderDesc()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getCapAmount())+"<input type=\"hidden\" name=\"captureamount_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getCapAmount()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getRefundAmount())+"</td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getChargebackAmount())+"</td>");
                    out.println("<td align=center "+style+">&nbsp;"+ ESAPI.encoder().encodeForHTML(transactionVO.getRetrivalRequest())+"</td>");
                    if (functions.isValueNull(transactionVO.getPaymentId()))
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getPaymentId()) + "</td>");
                    }
                    else
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                    }
                    out.println("<td align=center " + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getStatus())+"</td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getTimestamp())+"</td>");
                    out.println("<td align=center "+style+"> <input type=\"text\" class=\"txtboxtabel\" name=\"cbamount_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getAmount()+"\"></td>");
                    out.println("<td align=center "+style+"> <select class=\"txtbox\" name=\"chargebackreason_"+ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId())+"\">");
                    out.println("<option value=\"\"  default>Select Reason</option>");
                    for(CBReasonsVO transactionVO1 : reasonVOList)
                    {
                        String reason=transactionVO1.getCbreason();
                        String code=transactionVO1.getCbReasonId();
                        String type=transactionVO1.getType();
                        out.println("<option value=\"" + reason + "\">" +type+" - "+code+" "+reason+ "</option>");
                    }
                    out.println("</select></td>");
                    out.println("<td align=center "+style+"> <input type=\"text\" class=\"datepicker\" readonly  name=\"cbdate_"+transactionVO.getTrackingId()+"\" value=\"\" style=\"cursor: auto;background-color: #ffffff;opacity: 1;width: auto;\"></td>");
                    out.println("<td align=center "+style+"> <select class=\"txtboxtabel\" name=\"isRetrivalRequest_"+transactionVO.getTrackingId()+"\"><option value=\"N\" default>N</option><option value=\"Y\">Y</option> </select></td>");
                    out.println("<td align=center "+style+"> <select class=\"txtboxtabel\" name=\"isBlacklistDetails_"+transactionVO.getTrackingId()+"\"><option value=\"N\" default>N</option><option value=\"Current\">Current</option><option value=\"All\">All</option> </select></td>");
                    out.println("<td align=center "+style+"> <select class=\"txtboxtabel\" name=\"isRefundDetails_"+transactionVO.getTrackingId()+"\"><option value=\"N\" default>N</option><option value=\"Y\">Y</option> </select></td>");
                    out.println("</tr>");
                    sno++;
                }
            %>
            <thead>
            <tr>
                <td class="th0" colspan="19">
                    <input type="button" name="submitbutton" class="addnewmember" value="Chargeback Selected" onclick="return DoChargeback();">
                    <input type="submit" name="submitbutton" class="addnewmember" value="RetrivalRequest Selected" onclick="return doUpdateRetrivalRequest();">
                </td>
            </tr>
            </thead>
        </table>
    </form>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
    <%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
            }
        }
            else
            {
                Functions functions1=new Functions();
                if (functions.isValueNull(request.getParameter("trackingid"))){

                }else{
                    out.println(Functions.NewShowConfirmation("Sorry","No records found."));
                }
            }
       /* else
        {
            out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
        }*/
    %>
</div>
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