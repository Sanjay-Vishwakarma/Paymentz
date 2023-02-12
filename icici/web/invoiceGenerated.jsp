<%@page import = "java.util.Hashtable"


        %>
<%
    String currency =  (String)session.getAttribute("currency");

    Hashtable hiddenvariables = (Hashtable)request.getAttribute("hiddenvariables");
%>
<html>
<head>
    <title> Invoice </title>
        <%--<link rel="stylesheet" type="text/css" href="/merchant/style.css"/>--%>

</head>
<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<%@ include file="/index.jsp" %>
<h1 align=center> Invoice Generated</h1>
<table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
    <tr>
        <td>
            <table width="95%" border="1" cellspacing="0" cellpadding="0" bordercolor="#E4D6C9" bgcolor="#CCE0FF">
                <tr>
                    <td colspan="2"></td>
                </tr>
                <tr>
                    <td colspan="2">

                        <table width="80%" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#CCE0FF">
                            <tr>
                                <td width="100%" align=center colspan=3>
                                    <div align="center"><b><font face="Arial, Helvetica, sans-serif" size="2">Invoice No.
                                        </font></b><strong>:</strong><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                        <%=hiddenvariables.get("invoiceno")%></b></font></font></div>
                                    <div align="right"><b><font face="Arial, Helvetica, sans-serif" size="2">Date.
                                    </font></b><strong>:</strong><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("date")%></b></font></font></div></td>
                            </tr>
                            <tr>
                                <td width="21%">&nbsp;</td>
                                <td width="1%">&nbsp;</td>
                                <td colspan="6" width="78%">&nbsp;</td>
                            </tr>
                            <tr>
                                <td width="21%"><b><font face="Arial, Helvetica, sans-serif" size="2">Invoice Sent To
                                    </font></b><strong>:</strong></td>
                                <td width="1%">&nbsp;</td>
                                <td colspan="6" width="78%" align=left><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("custemail")%></b></font></font></td>
                            </tr>
                            <tr>
                                <td width="21%"><b><font face="Arial, Helvetica, sans-serif" size="2">Customer Details
                                </font></b></td>
                                <td width="1%">&nbsp;</td>
                                <td colspan="6" width="78%">&nbsp;</td>
                            </tr>
                            <tr>
                                <td width="21%">&nbsp; &nbsp;

                                </td>
                                <td width="1%" >&nbsp;</td>
                                <td width="78%" colspan="6" align=left><b><font face="Arial, Helvetica, sans-serif" size="2">Customer Name.
                                </font></b><strong>:</strong><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("custname")%></b></font></font></td>
                            </tr>
                            <tr>
                                <td width="21%">&nbsp; &nbsp;

                                </td>
                                <td width="1%" >&nbsp;</td>
                                <td width="78%" colspan="6" align=left><b><font face="Arial, Helvetica, sans-serif" size="2">Customer Phone No.
                                    </font></b><strong>:</strong><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("phonecc")%>-<%=hiddenvariables.get("phone")%></b></font></font></td>
                            </tr>
                            <tr>
                                <td width="21%">&nbsp; &nbsp;

                                </td>
                                <td width="1%" >&nbsp;</td>
                                <td width="78%" colspan="6" align=left>
                                    <%--<table><tr><td>

                                    <b><font face="Arial, Helvetica, sans-serif" size="2">Customer Address.
                                </font></b><strong>:</strong></td><td><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("address")%> ,<BR> <%=hiddenvariables.get("city")%> , <%=hiddenvariables.get("state")%> , <%=hiddenvariables.get("country")%> - <%=hiddenvariables.get("zipcode")%>
                                </b></font></font></td></tr></table>--%></td>
                            </tr>
                            <tr>
                                <td width="21%"><div align="left"><b><font face="Arial, Helvetica, sans-serif" size="2">Invoice Details
                                </font></b></div></td>
                                <td width="1%">&nbsp;</td>
                                <td colspan="6" width="78%">&nbsp;</td>
                            </tr>
                            <tr>
                                <td width="21%">&nbsp; &nbsp;

                                </td>
                                <td width="1%" >&nbsp;</td>
                                <td width="78%" colspan="6" align=left><b><font face="Arial, Helvetica, sans-serif" size="2">Order
                                    ID</font></b><strong>:</strong><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("orderid")%></b></font></font></td>
                            </tr>

                            <tr>
                                <td width="21%">&nbsp; &nbsp;

                                </td>
                                <td width="1%" >&nbsp;</td>
                                <td width="78%" colspan="6" align=left><b><font face="Arial, Helvetica, sans-serif" size="2">Order
                                    Description</font></b><strong>:</strong><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("orderdesc")%></b></font></font></td>
                            </tr>
                            <tr>
                                <td>&nbsp; &nbsp;</td>
                                <td >&nbsp;</td>
                                <td colspan="6"><font size="2"><b><font face="Arial, Helvetica, sans-serif" size="2">Order
                                    Amount</font></b><strong>:</strong><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>
                                    <%=hiddenvariables.get("amount")%>&nbsp;&nbsp;<%=hiddenvariables.get("currency")%></b></font></font></td>
                            </tr>



                        </table>
                </td></tr>
                </table>


 </table>
<br><p align=center><a href="/icici/servlet/Invoice?ctoken=<%=ctoken%>"
                       class="menufont">Click Here to Go To Invoice</a> </p>
</body>
</html>