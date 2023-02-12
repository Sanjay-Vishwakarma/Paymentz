<%@ page import="com.directi.pg.Merchants,
                 org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 java.util.Enumeration" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.directi.pg.Merchants" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="org.eclipse.jdt.internal.compiler.ast.ThisReference" %>
<%@ page import="com.directi.pg.Functions" %>


<%


    Functions functions = new Functions();
    String memberid = (String)session.getAttribute("merchantid");
    //String currency =  (String)request.getAttribute("currency");
    Hashtable hiddenvariables = (Hashtable)request.getAttribute("hiddenvariables");
    String currency = String.valueOf(hiddenvariables.get("currency"));
    Hashtable hash1 = (Hashtable)request.getAttribute("paymodelist");
    String error=(String) request.getAttribute("error");
    String validationError=request.getAttribute("validationError")!=null?(String)request.getAttribute("validationError"):"";
    String address = "";
    String city = "";
    String state = "";
    String country = "";
    String zip = "";
    String phone = "";
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("address"))))
        address = String.valueOf(hiddenvariables.get("address"));
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("city"))))
        city = String.valueOf(hiddenvariables.get("city"));
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("state"))))
        state = String.valueOf(hiddenvariables.get("state"));
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("country"))))
        country = String.valueOf(hiddenvariables.get("country"));
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("zipcode"))))
        zip = String.valueOf(hiddenvariables.get("zipcode"));

%>

<html>
<head>
    <title>Merchant Order Payments Confirmation</title>

</head>
<body bgcolor="#FFFFFF" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<%@ include file="/index.jsp"%>
<br><br>
<div class="row" >
<div class="col-lg-12" style="display: -webkit-box;">
<div class="panel panel-default" >
<div class="panel-heading" >
    Invoice Regeneration Confirmation
</div>

<%
    if(error!=""&&error!=null)
    {

%>
<table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
    <tr>
        <td>
            <table width="95%" border="1" cellspacing="0" cellpadding="0" bordercolor="#E4D6C9" >
                <tr>
                    <td colspan="2">Cannot Regenerate Invoice due to the following Error<br><%=error%></td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<%}
else{

%>

<br>
<form method=post action="RegenerateInvoice?ctoken=<%=ctoken%>">
<table width="70%" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="#E4D6C9" style="margin-left:250px; ">
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%">&nbsp;</td>
    <td colspan="6" width="78%"><font color=red><%=validationError%></font></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>

<tr>
    <td width="21%" class="textb">
        <div align="left">Order ID</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6">&nbsp;
        <input type=text name=orderid readonly="readonly" class="txtbox" value=<%=hiddenvariables.get("orderid")%>></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left" >Order
            Description</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=hiddenvariables.get("orderdesc")%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td class="textb">Order
        Amount</td>
    <td ><strong>:</strong></td>
    <td colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=hiddenvariables.get("amount")%>&nbsp;&nbsp;<%=currency%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            Email ID</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6">&nbsp;
        <input type=text name="custemail" size="100" style="width:200px; " class="txtbox" value=<%=hiddenvariables.get("custemail")%>></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            Address</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=address%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            City</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=city%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            State</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=state%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            Country</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=country%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            Zip Code</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=zip%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            Phone No</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=hiddenvariables.get("phonecc")%> - <%=hiddenvariables.get("phone")%> </td>
</tr>
<%--<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>--%>
<%--<tr>
    <td width="21%" class="textb"><div align="left">Select Pay Mode</div></td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6">&nbsp;
        <select name=paymenttype class="txtbox">
            <%
                Enumeration paymodeenum = hash1.keys();
                String paytypekey = "";
                String paytypevalue = "";
                String isCreditCard ="";
            %>
            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(paytypekey)%>" selected >
                <%=ESAPI.encoder().encodeForHTML(paytypevalue)%>
            </option>
            <%
                while (paymodeenum.hasMoreElements())
                {
                    paytypekey = (String) paymodeenum.nextElement();
                    paytypevalue = (String)hash1.get(paytypekey);

                    if(paytypekey.equals(hiddenvariables.get("paymodeid")))
                    {
                        isCreditCard="selected";
                    }
            %>
            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(paytypekey)%>"  <%=isCreditCard%>>
                <%=ESAPI.encoder().encodeForHTML(paytypevalue)%>
            </option>
            <%
                }
            %>


        </select>
    </td>
</tr>--%>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td width="21%" class="textb">
        <div align="left">Customer
            Redirect URL</div>
    </td>
    <td width="1%" ><strong>:</strong></td>
    <td width="78%" colspan="6" class="txtbox">&nbsp;&nbsp;
        <%=hiddenvariables.get("redirecturl")%></td>
</tr>
<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%"></td>
    <td colspan="6" width="78%"></td>
</tr>
<tr>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
    <td colspan="6" >&nbsp;</td>
</tr>

<tr>
    <td width="21%">&nbsp;</td>
    <td width="1%">&nbsp;</td>
    <td colspan="6" width="78%">
        <%--
                                            <INPUT TYPE="submit" value="Click to Re-Generate Invoice" class="submit">
        --%>
        <button type="submit"  class="buttonform" style="width:202px; ">
            <i class="fa fa-backward"></i>
            &nbsp;&nbsp;Click to Re-Generate Invoice
        </button>
    </td>

</tr>

</table>

                <input type=hidden name=invoiceno value=<%=hiddenvariables.get("invoiceno")%>  >
                <input type="hidden" name="paymenttype" value="">
</form>
<%
    }%>
</div>
</div></div>
</body>
</html>