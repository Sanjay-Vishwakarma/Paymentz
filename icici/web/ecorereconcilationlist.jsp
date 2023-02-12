<%@ include file="ecoretab.jsp"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 3/3/14
  Time: 5:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<html>
<head>
    <title></title>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("trackingId");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function DoChargeback()
        {
            var checkboxes = document.getElementsByName("trackingId");
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
    String memberid=nullToStr(request.getParameter("toid"));
    session.setAttribute("submit","Manual Reconcilation");
    Functions functions = new Functions();
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

<form action="/icici/servlet/EcoreReconcilationList?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
    <div class="row" style="margin-top: 0px">
        <div class="col-lg-12">
            <div class="panel panel-default"style="margin-top: 0px">
                <div class="panel-heading" >

                Ecore Reconcilation Transaction List
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
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="tdate" class="textb" >

                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>

                                        <select size="1" name="tmonth" class="textb" >

                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>

                                        <select size="1" name="tyear" class="textb">

                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Tracking ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input maxlength="15" type="text" name="trackingid"  class="txtbox" value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">

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
                                    <td width="8%" class="textb" for="mid" >Member ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                       <%-- <input maxlength="15" type="text" name="toid"  class="txtbox" value="<%=request.getParameter("toid")==null?"":request.getParameter("toid")%>">--%>

                                           <input name="toid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb">Payment Order NO</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input maxlength="100" type="text" name="paymentnumber" class="txtbox" value="<%=request.getParameter("paymentnumber")==null?"":request.getParameter("paymentnumber")%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Description</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">

                                        <input maxlength="15" type="text" name="description" class="txtbox" value="<%=request.getParameter("description")==null?"":request.getParameter("description")%>">

                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" style="padding:10px; " class="textb">
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

        Hashtable hash = (Hashtable)request.getAttribute("transdetails");

        Hashtable temphash=null;

        String currentblock=request.getParameter("currentblock");
        int records=0;
        int totalrecords=0;
        String error=(String ) request.getAttribute("errormessage");
        if(error !=null)
        {
            out.println(error);

        }

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
    <br>
    <div class="scroll">
    <form name="form" action="/icici/servlet/EcoreReconcilationProcess?ctoken=<%=ctoken%>" method="post">
        <table align=center width="80%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
                <tr>
                    <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                    <td valign="middle" align="center" class="th0" >Sr no</td>
                    <td valign="middle" align="center" class="th0">TrackingID</td>
                    <td valign="middle" align="center" class="th0">Fromid</td>
                    <td valign="middle" align="center" class="th0">Toid</td>
                    <td valign="middle" align="center" class="th0">Ecore PaymentOrderNO</td>
                    <td valign="middle" align="center" class="th0">Description</td>
                    <td valign="middle" align="center" class="th0">Amount</td>
                    <td valign="middle" align="center" class="th0">status</td>
                    <td valign="middle" align="center" class="th0">TimeStamp</td>
                    <td valign="middle" align="center" class="th0">Status Treatment</td>
                    <td valign="middle" align="center" class="th0">Refund Amount</td>
                    <td valign="middle" align="center" class="th0">Ecore PaymentOrderNO</td>
                    <td valign="middle" align="center" class="th0">Remark</td>
            </thead>
                </tr>
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
                    out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"trackingId\" value=\""+temphash.get("trackingid")+"\"></td>");
                    out.println("<td "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"<input type=\"hidden\" name=\"\" value=> </td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fromid"))+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid_"+temphash.get("trackingid")+"\" value=\""+temphash.get("toid")+"\" ></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("ecorePaymentOrderNumber"))+"<input type=\"hidden\" name=\"dbpaymentnumber_"+temphash.get("trackingid")+"\" value=\""+temphash.get("ecorePaymentOrderNumber")+"\"></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description_"+temphash.get("trackingid")+"\" value=\""+temphash.get("description")+"\"></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"amount_"+temphash.get("trackingid")+"\" value=\""+temphash.get("amount")+"\" ></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
                    out.println("<td "+style+"><select name=\"status_"+temphash.get("trackingid")+"\">");
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
                    out.println("<td "+style+"><input class=\"txtboxtabel\" type=\"Text\" name=\"refundamount_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" > </td>");
                    out.println("<td "+style+"> <input class=\"txtboxtabel\" type=\"Text\" name=\"ecorePaymentOrderNumber_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" > </td>");
                    out.println("<td "+style+"> <input class=\"txtboxtabel\" type=\"Text\" name=\"remark_"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"\" value=\"\" > </td>");

                    out.println("</tr>");

                }
            %>
            <thead><tr><td class="th0" colspan="14"><center><input type="submit" class="addnewmember" value="PROCESS" onClick="return DoChargeback();"></center></td></tr></thead>
        </table>
        <input type="hidden" name="fdate"  value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
        <input type="hidden" name="fmonth"  value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
        <input type="hidden" name="fyear"  value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
        <input type="hidden" name="tdate"  value="<%=request.getParameter("tdate")==null?"":request.getParameter("tdate")%>" >
        <input type="hidden" name="tmonth"  value="<%=request.getParameter("tmonth")==null?"":request.getParameter("tmonth")%>" >
        <input type="hidden" name="tyear"  value="<%=request.getParameter("tyear")==null?"":request.getParameter("tyear")%>" >
        <input type="hidden" name="trackingid"   value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">
        <input type="hidden" name="toid"   value="<%=request.getParameter("toid")==null?"":request.getParameter("toid")%>">
        <input type="hidden" name="paymentnumber"  value="<%=request.getParameter("paymentnumber")==null?"":request.getParameter("paymentnumber")%>">
        <input type="hidden" name="description"  value="<%=request.getParameter("description")==null?"":request.getParameter("description")%>">

    </form>
    <table align=center valign=top>
        <tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                    <jsp:param name="numrows" value="<%=pagerecords%>"/>
                    <jsp:param name="pageno" value="<%=pageno%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="EcoreReconcilationList"/>
                    <jsp:param name="currentblock" value="<%=currentblock%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
            </td>
        </tr>
    </table>
    </div>
    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry", "No record Found"));
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