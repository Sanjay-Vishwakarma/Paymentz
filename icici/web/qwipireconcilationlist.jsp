<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ include file="qwipitab.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 4/1/13
  Time: 3:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<%
    //String buttonvalue=request.getParameter("submit");
    String memberid=nullToStr(request.getParameter("toid"));
    if(buttonvalue==null)
    {
        buttonvalue=(String)session.getAttribute("submit");
        System.out.println("buttonvalue"+buttonvalue);
    }
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
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

        if (confirm("Do you really want to Process all selected transaction ?"))
        {
            document.form.submit();
        }
        else
        {
            return false;
        }
    }
    </script>
</head>
<body>
<%
    session.setAttribute("submit","Reconcilation cron");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
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
        str = str + "ctoken=" + ctoken;
        if (fdate != null) str = str + "&fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

%>
<form action="/icici/servlet/QwipiReconcilationList?ctoken=<%=ctoken%>" method="post" name="forms" >
<div class="row" style="margin-top: 0px">
<div class="col-lg-12">
<div class="panel panel-default" style="margin-top: 0px">
<div class="panel-heading">
    Reconciliation Transaction List
</div>
<table  align="center" width="85%" cellpadding="2" cellspacing="2" style="margin-left:55px; ">
<tr>
<td>
    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
    <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>
        <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td width="20%" class="textb" ></td>
            <td width="5%" class="textb"></td>
            <td width="22%" class="textb" align="center">
            </td>

            <td width="10%" class="textb">&nbsp;</td>
            <td width="40%" class="textb" ></td>
            <td width="5%" class="textb"></td>
            <td width="50%" class="textb" align="right">
            </td>
        </tr>
        <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td width="30%" class="textb" >From</td>
            <td width="0%" class="textb"></td>
            <td width="25%" class="textb">
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
                            out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                        else
                            out.println(Functions.printoptions(2005, 2020));
                    %>
                </select>
            </td>

            <td width="2%" class="textb"></td>
            <td width="45%" class="textb" >To</td>
            <td width="4%" class="textb"></td>
            <td width="35%" class="textb">

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
            <td  class="textb" >Member ID</td>
            <td  class="textb"></td>
            <td  class="textb">
                <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
            </td>

            <td  class="textb">&nbsp;</td>
            <td  class="textb" >Tracking ID</td>
            <td  class="textb"></td>
            <td  class="textb">
                <input maxlength="15" type="text" name="trackingid" class="txtbox" size="20" value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">
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
            <td  class="textb">&nbsp;</td>
            <td  class="textb" >PaymentOrderNO</td>
            <td  class="textb"></td>
            <td  class="textb">
                <input maxlength="15" type="text" name="paymentnumber"  class="txtbox" value="<%=request.getParameter("paymentnumber")==null?"":request.getParameter("paymentnumber")%>">
            </td>

            <td  class="textb">&nbsp;</td>
            <td  class="textb" >Description</td>
            <td  class="textb"></td>
            <td  class="textb">
                <input maxlength="15" type="text" name="description" class="txtbox" value="<%=request.getParameter("description")==null?"":request.getParameter("description")%>">
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
            <td width="0%" class="textb"></td>
            <td width="22%" class="textb"></td>

            <td width="2%" class="textb"></td>
            <td width="45%" class="textb" ></td>
            <td width="8%" class="textb"></td>
            <td width="40%" class="textb" align="right">
                <button type="submit" class="buttonform">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                </button>
            </td>

        </tr>
        <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td width="20%" class="textb" ></td>
            <td width="5%" class="textb"></td>
            <td width="22%" class="textb" align="center">
            </td>

            <td width="10%" class="textb">&nbsp;</td>
            <td width="40%" class="textb" ></td>
            <td width="5%" class="textb"></td>
            <td width="50%" class="textb" align="right">
            </td>
        </tr>

    </table>
</tD>
</tr>
</table>
</div>
</div>
</div>

</form>
<div class="reporttable" style="margin-left:210;margin-right: 6px;">
<%  String errormsg1 = (String)request.getAttribute("cbmessage");
    if (errormsg1 == null)
    {
        errormsg1 = "";
    }
    else
    {
        out.println(errormsg1);
    }
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;

    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
        out.println("<b>");
        out.println(error);
        out.println("</b>");
    }

    String currentblock=request.getParameter("currentblock");
    int records=0;
    int totalrecords=0;

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
    if(records>0)
    {
%>

<form name="form" action="/icici/servlet/ReconcilationProcess?ctoken=<%=ctoken%>" method="post">
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
       <thead>
        <tr>
            <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
            <td width=5% valign="middle" align="center" class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">TrackingID</td>
            <td valign="middle" align="center" class="th0">Fromid</td>
            <td valign="middle" align="center" class="th0">Toid</td>
            <td valign="middle" align="center" class="th0">Qwipi PaymentOrderNO</td>
            <td valign="middle" align="center" class="th0">Description</td>
            <td valign="middle" align="center" class="th0">Amount</td>
            <td valign="middle" align="center" class="th0">status</td>
            <td valign="middle" align="center" class="th0">TimeStamp</td>
            <td valign="middle" align="center" class="th0">Status Treatment</td>
            <td valign="middle" align="center" class="th0">Refund Amount</td>
            <td valign="middle" align="center" class="th0">Qwipi PaymentOrderNO</td>
            <td valign="middle" align="center" class="th0">Remark</td>

        </tr>
       </thead>
<%
    String style="class=tr1";
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
            out.println("<td align=center "+style+"><input type=\"checkbox\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
            out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"</td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"</td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid_"+temphash.get("trackingid")+"\" value=\""+temphash.get("toid")+"\" ></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("qwipiPaymentOrderNumber"))+"<input type=\"hidden\" name=\"dbpaymentnumber_"+temphash.get("trackingid")+"\" value=\""+temphash.get("qwipiPaymentOrderNumber")+"\"></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description_"+temphash.get("trackingid")+"\" value=\""+temphash.get("description")+"\"></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"amount_"+temphash.get("trackingid")+"\" value=\""+temphash.get("amount")+"\" ></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
            out.println("<td align=center "+style+"><select class=\"textb\" name=\"status_"+temphash.get("trackingid")+"\">");
            out.println("<option value=\"null\" default>Select Status Treatment</option>");
            if("authstarted".equalsIgnoreCase((String)temphash.get("status")))
            {
                out.println("<option value=failed>failed</option>");
                out.println("<option value=authfailed>authfailed</option>");
                out.println("<option value=capturesuccess>capturesuccess</option>");
            }
            if("markedforreversal".equals(temphash.get("status")))
            {
                out.println("<option value=reversed>reversed</option>");
            }
            out.println("</select></td>");
            out.println("<td align=center "+style+"> <input class=\"txtboxtabel\" type=\"Text\" name=\"refundamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" > </td>");
            out.println("<td align=center "+style+"> <input class=\"txtboxtabel\" type=\"Text\" name=\"qwipipaymentorderno_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" > </td>");
            out.println("<td align=center "+style+"> <input class=\"txtboxtabel\" type=\"Text\" name=\"remark_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" > </td>");
            out.println("</tr>");

        }
        %>
        <thead>
        <tr>
            <td class="th0" colspan="14"><center><input type="submit" value="PROCESS" style="color: #2c3e50;font-weight: bold;" onClick="return DoChargeback();"></center></td></tr>
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
                <jsp:param name="page" value="QwipiReconcilationList"/>
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
        out.println(Functions.NewShowConfirmation("Sorry","No record Found"));
    }
%>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>


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