<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="com.manager.vo.payoutVOs.CBReasonsVO" %>
<%@ include file="ecoretab.jsp"%>


<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/24/13
  Time: 2:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
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

        function DoChargeback()
        {
            //if (isNaN(document.reversalform.numrows.value))
            //alert("please enter valid page number");

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

            if (confirm("Do you really want to chargeback all selected transaction ?"))
            {
                document.chargebackform.submit();
            }
        }

    </script>

</head>
<body>
<div class="row" style="margin-top: 0px">
    <div class="col-lg-12">
        <div class="panel panel-default"style="margin-top: 0px">
            <div class="panel-heading" >
                Ecore Chargeback
            </div>
            <%!
                Functions functions = new Functions();
            %>
            <%
                session.setAttribute("submit","Chargeback");
                String str="";
                String fdate=null;
                String tdate=null;
                String fmonth=null;
                String tmonth=null;
                String fyear=null;
                String tyear=null;
                String memberid=nullToStr(request.getParameter("toid"));
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
                int year = Calendar.getInstance().get(Calendar.YEAR);
            %>

            <form action="/icici/servlet/EcoreChargebackList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="fdate" class="textb" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="textb" value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" class="textb" value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, year, fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, year));
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
                                                    out.println(Functions.yearoptions(2005, year, tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, year));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">

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
                                    <td width="11%" class="textb" >Payment Order No</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="50" type="text" name="paymentOrderNo" class="txtbox" value="">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Tracking ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input maxlength="500" type="text" name="trackingid" class="txtbox" value="<%=functions.isValueNull(request.getParameter("trackingid"))?(ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("trackingid"))):""%>">

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">

                                        <button type="submit" class="buttonform">
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

        Functions functions = new Functions();
        if(functions.isValueNull(String.valueOf(request.getAttribute("sSuccessMessage"))))
        {
        String successMessage = (String)request.getAttribute("sSuccessMessage");
        out.println(Functions.NewShowConfirmation("Success Updating", successMessage));
        }
        if(functions.isValueNull(String.valueOf(request.getAttribute("sErrorMessage"))))
        {
        String errorMessage = (String) request.getAttribute("sErrorMessage");
        out.println(Functions.NewShowConfirmation("Failed Updating", errorMessage));
        }
        String error=(String) request.getAttribute("error");
        if(error !=null)
        {
            out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
        }
    %>
    <%

        String errormsg1 = (String)request.getAttribute("cbmessage");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"text\" >");
            out.println(errormsg1);
            out.println("</font></td></tr></table>");
        }
        String currentblock=request.getParameter("currentblock");

        if(currentblock==null)
            currentblock="1";

        if(request.getAttribute("cbVO")!=null)
        {
            List<TransactionVO> cbVOList= (List<TransactionVO>) request.getAttribute("cbVO");
            List<CBReasonsVO> cbReasonVOList= (List<CBReasonsVO>) request.getAttribute("reason");
            PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
            paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
            if(cbVOList.size()>0)
            {
    %>
    <form name="chargebackform" action="/icici/servlet/EcoreChargebackTransaction?ctoken=<%=ctoken%>" method="post">
        <table align=center width="45%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td valign="middle" align="center" class="th0">TrackingID</td>
                <td valign="middle" align="center" class="th0">toid</td>
                <td valign="middle" align="center" class="th0">Description</td>
                <td valign="middle" align="center" class="th0">status</td>
                <td valign="middle" align="center" class="th0">Payment Order No</td>
                <td valign="middle" align="center" class="th0">Amount</td>
                <td valign="middle" align="center" class="th0">CB Amount</td>
                <td valign="middle" align="center" class="th0">CB Reason</td>
                <td valign="middle" align="center" class="th0">CB Remark</td>
                </td>
            </tr>
            </thead>

            <%
                String style="class=td1";
                String ext="light";
                Hashtable inner=null;
                for(TransactionVO transactionVO : cbVOList)
                {
                    int pos = 1;
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

                    out.println("<tr>");
                    out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+transactionVO.getTrackingId()+"\"></td>");
                    out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) transactionVO.getTrackingId())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)transactionVO.getToid())+"<input type=\"hidden\" name=\"toid_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getToid()+"\"></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)transactionVO.getOrderDesc())+"<input type=\"hidden\" name=\"desc_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getOrderDesc()+"\"></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)transactionVO.getStatus())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)transactionVO.getEcorePaymentOrderNumber())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)transactionVO.getAmount())+"<input type=\"hidden\" name=\"captureamount_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getAmount()+"\"></td>");
                    out.println("<td "+style+">&nbsp; <input class=\"txtbox\" type=\"Text\" value=\""+ESAPI.encoder().encodeForHTMLAttribute((String)transactionVO.getAmount())+"\" name=\"chargebackamount_"+ESAPI.encoder().encodeForHTML((String)transactionVO.getTrackingId())+"\" size=\"10\"> </td>");
                    out.println("<td "+style+">&nbsp; <select class=\"txtbox\" name=\"chargebackreason_"+ESAPI.encoder().encodeForHTML((String) transactionVO.getTrackingId())+"\">");
                    out.println("<option value=\"0\" default>Select Reason</option>");
                    for(CBReasonsVO cbReasonsVO : cbReasonVOList)
                    {
                        /*String id1=Integer.toString(i);
                        inner=(Hashtable)rs.get(id1);*/
                        String reason=cbReasonsVO.getCbreason();
                        out.println("<option value=\""+reason+"\">\""+reason+"\"</option>");
                    }
                    out.println("</select></td>");
                    out.println("<td "+style+">&nbsp; <input class=\"txtbox\" type=\"Text\" name=\"chargebackremark_"+ESAPI.encoder().encodeForHTML((String) transactionVO.getTrackingId())+"\" value=\"\" > </td>");
                    out.println("<td ><input  type=\"hidden\" name=\"accountid_"+ESAPI.encoder().encodeForHTML((String) transactionVO.getTrackingId())+"\" value=\""+(String)transactionVO.getAccountId() +"\"></td>");
                    out.println("</tr>");
                }
            %>
            <tr>
                <td valign="middle" align="center"  colspan="11">
                    <input class="button" type="button" value="Chargeback Selected"   onClick="return DoChargeback();" >
                </td>
            </tr>
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
                out.println(Functions.NewShowConfirmation("Sorry","No records found."));
            }
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Filter","Please provide the data to get Ecore Chargeback List"));
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