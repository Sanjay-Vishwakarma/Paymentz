<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.directi.pg.Admin" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ include file="functions.jsp"%>
<%@ include file="rollingReserveManager.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: WAHEED
  Date: 2/11/14
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <%--Datepicker css format--%>
    <link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
    <script src="/icici/olddatepicker1/jquery-1.9.1.js"></script>
    <script src="/icici/olddatepicker1/jquery-ui.js"></script>
    <link rel="stylesheet" href="/resources/demos/style.css">
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();
//            $("#yourinput").datepicker( "setDate" , "7/11/2005" );

        });
    </script>
    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (Admin.isLoggedIn(session))
    {
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");
        Hashtable temphash=null;
        int records=0;
        int totalrecords=0;
        String str="ctoken=" + ctoken;
        String accountid = request.getParameter("accountid");
        if (accountid != null) str = str + "&accountid=" + accountid;
        String fromDate = request.getParameter("fromDate");
        if (fromDate != null) str = str + "&fromDate=" + fromDate;
        String toDate = request.getParameter("toDate");
        if (toDate != null) str = str + "&toDate=" + toDate;
        String time_difference_daylight = request.getParameter("time_difference_daylight");
        if (time_difference_daylight != null) str = str + "&time_difference_daylight=" + time_difference_daylight;
        String mybutton = request.getParameter("mybutton");
        if (mybutton != null) str = str + "&mybutton=" + mybutton;
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        String errormsg=(String)request.getAttribute("message");
        if(errormsg!=null)
        {
            out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");
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
            TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
            accountid = Functions.checkStringNull(request.getParameter("accountid"));

%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Generate Rolling Reserve File
            </div>

            <form name = "FIRCForm" action="/icici/servlet/ExportReserveFile?ctoken=<%=ctoken%>" method="post"  >
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >From</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="0%" class="textb">
                                        <input name="fromDate" type="text"  readonly class="datepicker" value="<%= request.getAttribute("fromDate").toString() %>">
                                    </td>
                                    <td width="5%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >To</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="0%" class="textb">
                                        <input name="toDate" type="text" readonly class="datepicker" value="<%= request.getAttribute("toDate").toString() %>">

                                    </td>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb">Day Light Saving</td>
                                    <td width="30%" class="textb"></td>
                                    <td width="80%" class="textb">
                                        <select size="1" name="time_difference_daylight" class="textBoxes">
                                            <option value="N">No</option>
                                            <option value="Y" <%if("Y".equals(request.getAttribute("isDaylight"))){%> selected <%}%>>Yes</option>
                                        </select>
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
                                    <td width="2%" class="textb" >&nbsp;</td>
                                    <td width="12%" class="textb">Account Id*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb" colspan="5">
                                        <select size="1" name="accountid" class="txtbox" style="width:500px ">
                                            <%
                                                for(Integer sAccid : accountDetails.keySet())
                                                {
                                                    GatewayAccount g = accountDetails.get(sAccid);
                                                    String selected3 = "";

                                                    if (String.valueOf(sAccid).equals(accountid))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";
                                                /*Enumeration enu3 = accountidhash.keys();
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                while (enu3.hasMoreElements())
                                                {
                                                    key3 = (String) enu3.nextElement();
                                                    value3 = (String) accountidhash.get(key3);
                                                    if (key3.equals(accountid))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";*/
                                            %>
                                            <option value="<%=sAccid%>" <%=selected3%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                    <%--<td width="2%" class="textb"></td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb"></td>--%>

                                    <td width="2%" class="textb"></td>
                                    <td width="8%" class="textb" >Time Difference Normal</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb"><input name="time_difference_normal" type="text" value="<%= request.getAttribute("time_difference_normal").toString() %>" disabled=""></td>

                                </tr>
                                <tr>
                                    <td width="2%" class="textb"></td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>


                                    <td width="2%" class="textb"></td>
                                    <td width="8%" class="textb"><button name="mybutton" type="submit" value="View" class="buttonform">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;View
                                    </button></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb"><button name="mybutton" type="submit" value="Generate Excel File" style="width:160px " class="buttonform">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Generate Excel File
                                    </button></td>

                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<br>

<center><h2>Rolling File</h2></center>
<br>
<table align=center width="50%">
    <tr>
        <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Tracking Id </td>
        <td valign="middle" align="center" bgcolor="#008BBA"> Description</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Account Id</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Transaction amount </td>
        <td valign="middle" align="center" bgcolor="#008BBA">Rolling amount</td>
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
                style="class=td2";
                ext="dark";
            }
            else
            {
                style="class=td1";
                ext="light";
            }

            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"<input type=\"hidden\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description\" value=\""+temphash.get("description")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"<input type=\"hidden\" name=\"accountid\" value=\""+temphash.get("accountid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("amount"))+"<input type=\"hidden\" name=\"amount\" value=\""+temphash.get("amount")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("RollingReserveAmountKept"))+"<input type=\"hidden\" name=\"RollingReserveAmountKept\" value=\""+temphash.get("RollingReserveAmountKept")+"\"></td>");
            out.println("</tr>");
        }
    %>
    <tr><td colspan=3 align="left" class=textb>Total Records: <%=totalrecords%></td><td colspan=3 align="right"
                                                                                        class=textb>
        Page No <%=pageno%></td></tr>
</table>
<%
    }
    else
    {
        out.println(ShowConfirmation("Sorry","No records found."));
    }
%>
<table align=center valign=top><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="ExportReserveFile"/>
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
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>