<%@ page import="com.directi.pg.Functions,
                 com.manager.vo.PaginationVO,
                 com.manager.vo.TransactionVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="ecoretab.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/8/12
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

        }

    </script>
    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<div class="row" style="margin-top: 0px">
    <div class="col-lg-12">
        <div class="panel panel-default" style="margin-top: 0px">
            <div class="panel-heading" >
                Ecore Refund
            </div>
            <%!
                Functions functions = new Functions();
            %>
            <%
                session.setAttribute("submit","Refund");

            %>
            <form action="/icici/servlet/EcoreRefundList?ctoken=<%=ctoken%>" method="post" name="forms" >
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
                    if (fdate == null) fdate = "" + 1;
                    if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
                    if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
                    if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
                    if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
                    if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);
                    %>

                <table  align="center" width="80%" cellpadding="2" cellspacing="2" >
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="30%" class="textb" >From</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="25%" class="textb">
                                        <select size="1" name="fdate" class="textb" style="font-size:12px; " value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >>
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="textb" style="color:#151B54;font-size:12px;" >
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" class="textb" style="color:#151B54;font-size:12px;">
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, rightNow.get(Calendar.YEAR), fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" >To</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="48%" class="textb">

                                        <select size="1" name="tdate" style="color:#151B54;font-size:12px;"class="textb">
                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="tmonth" class="textb" style="color:#151B54;font-size:12px;">
                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="tyear" class="textb" style="color:#151B54;font-size:12px;">
                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, rightNow.get(Calendar.YEAR), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>

                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td  class="textb" >To ID</td>
                                    <td  class="textb"></td>
                                    <td  class="textb">
                                        <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td  class="textb">&nbsp;</td>
                                    <td  class="textb" >Tracking ID</td>
                                    <td  class="textb"></td>
                                    <td  class="textb">
                                        <input maxlength="500" type="text" name="trackingid"  value="<%=functions.isValueNull(request.getParameter("trackingid"))?(ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("trackingid"))):""%>" class="txtbox">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="22%" class="textb">

                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%"  align="center">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>
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
        String errormsg1 = (String) request.getAttribute("error");
        if (errormsg1 != null)
        {
            out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
        }
        Functions functions = new Functions();
    /*String error = (String) request.getAttribute("cbmessage");

    if(functions.isValueNull(String.valueOf(request.getAttribute("cbmessage"))))
    {
        String sMessage = request.getAttribute("cbmessage").toString();
        out.println(Functions.NewShowConfirmation("Success/Fail Updating", sMessage));
    }

    if(functions.isValueNull(String.valueOf(request.getAttribute("cbmessage"))))
    {
        String successMessage = request.getAttribute("cbmessage").toString();
        out.println(Functions.NewShowConfirmation("Success Updating", successMessage));
    }
    if(functions.isValueNull(String.valueOf(request.getAttribute("cbmessage"))))
    {
        String errorMessage = request.getAttribute("cbmessage").toString();
        out.println(Functions.NewShowConfirmation("Failed Updating", errorMessage));
    }*/
    %>
    <br>
    <%

        if(request.getAttribute("refundVOList")!=null)
        {

            List<TransactionVO> refundVOList= (List<TransactionVO>) request.getAttribute("refundVOList");
            PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
            paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
            if(refundVOList.size()>0)
            {
    %>
    <form name="reversalform" action="EcoreRefundTransaction?ctoken=<%=ctoken%>" method="post">
        <table align="center" width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td valign="middle" align="center" class="th0">TrackingID</td>
                <td valign="middle" align="center" class="th0">Toid</td>
                <td valign="middle" align="center" class="th0">MID</td>
                <td valign="middle" align="center" class="th0">Description</td>
                <td valign="middle" align="center" class="th0">status</td>
                <%--<td valign="middle" align="center" class="th0">Time</td>--%>
                <td valign="middle" align="center" class="th0">Time</td>
                <td valign="middle" align="center" class="th0">Payment Order No</td>
                <td valign="middle" align="center" class="th0">Amount</td>
                <td valign="middle" align="center" class="th0">Refund Amount</td>
                <td valign="middle" align="center" class="th0">Is Fraud Trans</td>
            </tr>
            </thead>
            <%
                String style="class=td1";
                String ext="light";
                int pos = 0;
                for(TransactionVO transactionVO : refundVOList)
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

                    out.println("<tr>");
                    out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+transactionVO.getTrackingId()+"\"></td>");

                    out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId())+"</td>");
                    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getToid()) + "<input type=\"hidden\" name=\"toid_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getToid() + "\" ></td>");
                    //out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getToid())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getFromid())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getFromid())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getStatus())+"</td>");
                    //out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getTimestamp())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getDtStamp())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getEcorePaymentOrderNumber())+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getAmount())+"</td>");
                    //out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)hash.get("refundamount"))+"</td>");
                    out.println("<td "+style+">&nbsp;<input  type=\"Text\" value="+transactionVO.getAmount()+" readonly name=\"refundamount_"+ESAPI.encoder().encodeForHTML( transactionVO.getTrackingId())+"\" size=\"10\"> </td>");
                    //out.println("<td "+style+">&nbsp;<input id=\"isFraud\" type=\"checkbox\" name=\"isFraud_"+temphash.get("trackingid")+"\" value=\"\"></td>");
                    out.println("<td "+style+">&nbsp;<select name=\"isFraud_"+transactionVO.getTrackingId()+"\"><option value=\"N\" default>N</option><option value=\"Y\">Y</option> </select></td>");


                    out.println("</tr>");
                }
            %>
        </table>
        <br>
        <center><input class="button" style="width:150px" type="button" value="Reverse Selected"  onClick="return DoReverse();" ></center>

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
            out.println(Functions.NewShowConfirmation("Filter","Please provide the data to get Ecore refund List"));
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