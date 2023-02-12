<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp"%>
<%@ include file="qwipitab.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 7/12/14
  Time: 1:23 PM
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

        function DoUpdate()
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

            if (confirm("Do you really want to Update all selected transaction."))
            {
                document.updateOrderVerifyRefundAlertform.submit();
            }

        }

    </script>
    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<%
    Logger logger = new Logger("qwipiVerifyOrderRefundAlertList.jsp");
    session.setAttribute("submit","Verify Order & Refund Alert");

    if(buttonvalue==null)
    {
        buttonvalue=(String)session.getAttribute("submit");
    }

%>
<%
    try
    {

    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<form action="/icici/servlet/QwipiVerifyOrderRefundAlertList?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
    <input type="hidden" value="searchList" name="action">
    <%

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
        str = str + "ctoken=" + ctoken;
        str=str+"&action=searchList";
        if (fdate != null) str = str + "&fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;

        String toid=request.getParameter("toid");
        String trackingid=request.getParameter("trackingid");

        String isVerifyOrder=request.getParameter("verifyorder");
        String isRefundAlert=request.getParameter("refundalert");

        if(isVerifyOrder!=null)str = str + "&verifyorder=" + isVerifyOrder;
        else isVerifyOrder="";
        if(isRefundAlert!=null)str = str + "&refundalert=" + isRefundAlert;
        else isRefundAlert="";

        if(toid!=null)str = str + "&toid=" + toid;
        else toid="";
        if(trackingid!=null)str = str + "&trackingid=" + trackingid;
        else trackingid="";

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
    %>
    <div class="row" style="margin-top: 0px">
        <div class="col-lg-12">
            <div class="panel panel-default"style="margin-top: 0px">
                <div class="panel-heading" >
                    Qwipi Verify Order & Refund Alert
                </div>
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
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="tdate">
                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="tmonth">
                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="tyear">
                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
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
                                    <td width="8%" class="textb" >Tracking ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="500" type="text" name="trackingid" class="txtbox" value="<%=trackingid%>">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Description</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="description" class="txtbox" value="">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Verify Order</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <select name="verifyorder">
                                            <option value=""></option>
                                        <%
                                            if("Y".equals(isVerifyOrder))
                                            {%>
                                            <option value="N">N</option>
                                            <option value="Y" selected>Y</option>
                                            <%}
                                            else if("N".equals(isVerifyOrder))
                                            {%>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="N">N</option>
                                            <option value="Y">Y</option>
                                            <%}
                                        %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Refund Alert</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <select name="refundalert">
                                            <option value=""></option>
                                            <%if("Y".equals(isRefundAlert))
                                                {%>
                                            <option value="N">N</option>
                                            <option value="Y" selected>Y</option>
                                            <%}
                                            else if("N".equals(isRefundAlert))
                                            {%>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="N">N</option>
                                            <option value="Y">Y</option>
                                            <%}
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb"></td>

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
            </div>
        </div>
    </div>
</form>
<div class="reporttable">
    <%
        Functions functions = new Functions();
        if(functions.isValueNull(String.valueOf(request.getAttribute("sSuccessMessage"))))
        {
            String successMessage = request.getAttribute("sSuccessMessage").toString();
            out.println(Functions.NewShowConfirmation("Success Updating", successMessage));
        }
        if(functions.isValueNull(String.valueOf(request.getAttribute("sErrorMessage"))))
        {
            String errorMessage = request.getAttribute("sErrorMessage").toString();
            out.println(Functions.NewShowConfirmation("Failed Updating", errorMessage));
        }

    %>
    <%  String errormsg1 = (String)request.getAttribute("errormessage");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
            out.println(errormsg1);
            out.println("</font></td></tr></table>");
        }
    %>
    <%
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

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
<form name="updateOrderVerifyRefundAlertform" action="QwipiVerifyOrderRefundAlertList?ctoken=<%=ctoken%>" method="post">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <input type="hidden" value="updateOrderVerifyRefundAlert" name="action">
    <table align="center" width="100%" class="table table-striped table-bordered table-hover table-green dataTable" >
         <thead>
            <tr>
                <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td valign="middle" align="center" class="th0">TrackingID</td>
                <td valign="middle" align="center" class="th0">ToId</td>
                <td valign="middle" align="center" class="th0">MID</td>
                <td valign="middle" align="center" class="th0">Description</td>
                <td valign="middle" align="center" class="th0">Status</td>
                <td valign="middle" align="center" class="th0">Time</td>
                <td valign="middle" align="center" class="th0">Payment Order No</td>
                <td valign="middle" align="center" class="th0">Amount</td>
                <td valign="middle" align="center" class="th0">VerifyOrder</td>
                <td valign="middle" align="center" class="th0">RefundAlert</td>
                <td valign="middle" align="center" class="th0">Is Verify Order</td>
                <td valign="middle" align="center" class="th0">Is Refund Alert</td>
         </thead>
        </tr>
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
                out.println("<td "+style+"><input type=\"checkbox\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");

                out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"</td>");

                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("qwipiPaymentOrderNumber"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isVerifyOrder"))+"</td>");
                out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isRefundAlert"))+"</td>");
                out.println("<td "+style+"><select name=\"isVerifyOrder_"+temphash.get("trackingid")+"\"><option value=\"N\" default>N</option><option value=\"Y\">Y</option> </select></td>");
                out.println("<td "+style+"><select name=\"isRefundAlert_"+temphash.get("trackingid")+"\"><option value=\"N\" default>N</option><option value=\"Y\">Y</option> </select></td>");
                out.println("</tr>");
            }
        %>
        <thead>
        <tr>
            <td valign="middle" align="center"  class="th0" colspan="2">
                Total:<%=totalrecords%>
            </td>
            <td valign="middle" align="center"  class="th0" colspan="11">
                <button type="button" value="Update" class="addnewmember" onClick="return DoUpdate();" >
                    Update Selected
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
                <jsp:param name="page" value="QwipiVerifyOrderRefundAlertList"/>
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
%>

<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
    }
    catch(Exception e)
    {
       logger.error("exception in page qwipiVerifyOrderRefundAlertList.jsp::",e);
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