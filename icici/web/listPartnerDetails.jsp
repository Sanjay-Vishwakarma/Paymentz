<%@ page import="java.util.Hashtable" %>
<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 2/12/14
  Time: 1:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<center><h2>Charges Interface</h2></center>
<br>
<%--<center><b>Inquiry</b> &nbsp;&nbsp;&nbsp;<a href="/icici/?ctoken=<%=ctoken%>">Reconcilation cron</a>&nbsp;&nbsp;&nbsp;<a href="/icici/?ctoken=<%=ctoken%>">Chargeback Cron</a>&nbsp;&nbsp;&nbsp;<a href="/icici/EcoreSettlementCron.jsp?ctoken=<%=ctoken%>">Settle Cron</a>&nbsp;&nbsp;&nbsp;<a href="/icici/servlet/EcoreRefundList?ctoken=<%=ctoken%>">Refund</a>&nbsp;&nbsp;&nbsp;<a href="/icici/servlet/EcoreDetailList?ctoken=<%=ctoken%>">Details</a>&nbsp;&nbsp;&nbsp;<a href="/icici/ecorechargebacklist.jsp?ctoken=<%=ctoken%>">Chargeback</a>&nbsp;&nbsp;&nbsp;<a href="/icici/ecoreManualSettlement.jsp?ctoken=<%=ctoken%>">Manual Settlement</a><br><br></center>--%>
<center>
    <table border="0">
        <tr>
            <td><form action="/icici/partnerInterface.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Charge Master" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;</td>
            <td><form action="/icici/listGatewayAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Gateway Accounts Charges" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;</td>
            <td><form action="/icici/listMemberAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Member Accounts Charges" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;</td>
        </tr>

    </table>

</center>
<table border="0" align="center" width="50%">
    <tr>
        <td></td>
        <td></td>
        <td></td>
        <td width="30" bgcolor="#2379A5" colspan="1" align="center"><font color="#FFFFFF" size="2" face="Verdana, Arial" ><form action="/icici/manageChargeMaster.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Add New Charge" name="submit" class="buttonadmin"></form></font></td>
    </tr>
</table>

<form action="/icici/servlet/ListPartnerDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <%

        String str="ctoken=" + ctoken;

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);



    %>

    <table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="50%" bordercolorlight="#000000" bordercolordark="#FFFFFF">

        <tr>
            <td width="692" bgcolor="#2379A5" colspan="4" align="center"><font color="#FFFFFF" size="2" face="Verdana, Arial" ><b>Charge Master</b></font></td>
        </tr>
        <tr>
            <%--<td align=center width="40">
                <font color="black"> Charge Id:</font> <input maxlength="15" type="text" name="chargeid"  value="">
            </td>--%>
            <td align=center width="40">
                <font color="black"> Charge Name:</font> <input maxlength="15" type="text" name="chargename"  value="">
            </td>
            <td align=center width="100">
                <font color="black"> Input Required? :</font> <select name="isinputrequired"><option value="" selected></option><option value="Y">Yes</option><option value="N">No</option></select>
            </td>
            <td align=center width="50"><input type="submit" value="Search"></td>

        </tr>

    </table>
    <br/>

</form>
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

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
<table align=center width="50%">
    <tr>
        <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
        <%--<td valign="middle" align="center" bgcolor="#008BBA">Charge Id</td>--%>
        <td valign="middle" align="center" bgcolor="#008BBA">Charge Name</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Input Required?</td>
        </td>
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
            //out.println("<form action=\"/icici/servlet/EcoreInquiry\" method=\"POST\">");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");
            //out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("chargeid"))+"<input type=\"hidden\" name=\"chargeid\" value=\""+temphash.get("chargeid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"<input type=\"hidden\" name=\"chargename\" value=\""+temphash.get("chargename")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isinputrequired"))+"<input type=\"hidden\" name=\"isinputrequired\" value=\""+temphash.get("isinputrequired")+"\"></td>");
            out.println("</form>");
            out.println("</tr>");
        }
    %>
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
            <jsp:param name="page" value="ListChargeMaster"/>
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