<%@ page import="com.directi.pg.Merchants,
                 org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 java.util.Enumeration" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ include file="ietest.jsp" %>

<%

    String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
    User user =  (User)session.getAttribute("ESAPIUserSessionKey");
    Hashtable hiddenvariables= new Hashtable();
    hiddenvariables = (Hashtable) request.getAttribute("hiddenvariables");

    String ctoken= null;
     if(user!=null)
     {
         ctoken = user.getCSRFToken();

     }
    Merchants merchants = new Merchants();
    if (!merchants.isLoggedIn(session))
    {


%>

<html>
<head>
    <title>Virtual Merchant  Logout</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
<style>
    /********************Table Responsive Start**************************/

    .table#myTable > thead > tr > th {font-weight: inherit;}

    @media (max-width: 640px){

        table#myTable {border: 0;}

        /*table tr {
            padding-top: 20px;
            padding-bottom: 20px;
            display: block;
        }*/

        table#myTable thead { display: none;}

        #myTable tr:nth-child(odd), #myTable tr:nth-child(even) {background: #ffffff;}

        table#myTable td {
            display: block;
            border-bottom: none;
            padding-left: 0;
            padding-right: 0;
        }

        table#myTable td:before {
            content: attr(data-label);
            float: left;
            width: 100%;
            font-weight: bold;
        }

        #myTable tr:nth-child(odd) {background: #cacaca!important;}

    }

    table#myTable {
        width: 100%;
        max-width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;
        display: table;
        border-collapse: separate;
        /*border-color: grey;*/
    }

    #myTable thead {
        display: table-header-group;
        vertical-align: middle;
        border-color: inherit;

    }
    /*#myTable tr:nth-child(odd) {background: #F9F9F9;}*/

    #myTable tr {
        display: table-row;
        vertical-align: inherit;
        border-color: inherit;
    }

    #myTable th {padding-right: 1em;text-align: left;font-weight: bold;}

    #myTable td, #myTable th {display: table-cell;vertical-align: inherit;}

    #myTable tbody {
        display: table-row-group;
        vertical-align: middle;
        border-color: inherit;
    }

    #myTable td {
        padding-top: 6px;
        padding-bottom: 6px;
        padding-left: 10px;
        padding-right: 10px;
        vertical-align: top;
        border-bottom: none;
    }

    .table#myTable>thead>tr>th, .table#myTable>tbody>tr>th, .table#myTable>tfoot>tr>th, .table#myTable>thead>tr>td, .table#myTable>tbody>tr>td, .table#myTable>tfoot>tr>td{border-top: 1px solid #ddd;}

    /********************Table Responsive Ends**************************/
</style>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<form action="index.jsp" method=post>
    <br><br>

    <br>
    <table class=search border="0" cellpadding="0" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;Merchant Virtual Terminal</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Your session has expired </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="virtualLogin.jsp" class="link">here</a> to go to the Virtual login
                page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>
</form>
</body>
</html>
<%
        return;
    }



        String memberid = (String)session.getAttribute("merchantid");
        String currency =  (String)hiddenvariables.get("currency");
        String autoSelectTerminal = (String)hiddenvariables.get("autoSelectTerminal");


%>

<html>
<head>
<title>Merchant Order Payments Confirmation</title>

</head>
<body bgcolor="#FFFFFF" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<br><br>
<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">

            <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">

        </td>
    </tr>
</table>
<br><br>
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
                <td width="21%">&nbsp;</td>
                <td width="1%">&nbsp;</td>
                <td colspan="6" width="78%">&nbsp;</td>
              </tr>
              <tr>
                <td width="21%">&nbsp;</td>
                <td width="1%">&nbsp;</td>
                <td colspan="6" width="78%">&nbsp;</td>
              </tr>
              <tr>
                <td width="21%">
                  <div align="left"><b><font face="Arial, Helvetica, sans-serif" size="2">Order
                    ID</font></b></div>
                </td>
                <td width="1%" ><strong>:</strong></td>
                <td width="78%" colspan="6"><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("oid")%></b></font></font></td>
              </tr>
              <tr>
                <td >&nbsp;</td>
                <td >&nbsp;</td>
                <td colspan="6" >&nbsp;</td>
              </tr>
              <tr>
                <td><b><font face="Arial, Helvetica, sans-serif" size="2">Order
                  Amount</font></b></td>
                <td ><strong>:</strong></td>
                <td colspan="6"><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("total")%>&nbsp;&nbsp;<%=currency%></b></font></font></td>
              </tr>
              <%
                  if(autoSelectTerminal.equals("Y"))
                  {
              %>
              <tr>
                <td><b><font face="Arial, Helvetica, sans-serif" size="2">Pay Mode</font></b></td>
                <td ><strong>:</strong></td>
                <td colspan="6"><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("paymode")%>&nbsp;&nbsp;</b></font></font></td>
              </tr>

              <tr>
                <td><b><font face="Arial, Helvetica, sans-serif" size="2">Card Type</font></b></td>
                <td><strong>:</strong></td>
                <td colspan="6"><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                                    <%=hiddenvariables.get("cardtype")%>&nbsp;&nbsp;</b></font></font></td>
              </tr>
              <%
                  }
                  else
                  {
              %>
                <tr>
                    <td><b><font face="Arial, Helvetica, sans-serif" size="2">Terminal Type</font></b></td>
                    <td><strong>:</strong></td>
                    <td colspan="6"><font size="2"><font color="#CC0000" face="Arial, Helvetica, sans-serif"><b>&nbsp;&nbsp;
                        <%=hiddenvariables.get("terminalid")%>&nbsp;&nbsp;</b></font></font></td>
                </tr>

                <%
                    }
                %>

              <tr>
                <td>&nbsp;</td>
               <td>&nbsp;</td>
                <td colspan="6">&nbsp;</td>
              </tr>
              <tr>
                <td width="21%">&nbsp;</td>
                <td width="1%">&nbsp;</td>
                <td colspan="6" width="78%">
                  <%--<form name="form" method="post" action="/icici/servlet/PayProcessController">--%>
                <form name="form" method="post" action="/transaction/PayProcessController">
                    <input type=hidden name=toid value="<%=hiddenvariables.get("mid")%>">
                    <input type=hidden name=totype value="<%=hiddenvariables.get("totype")%>">
                    <input type=hidden name=amount value="<%=hiddenvariables.get("total")%>">
                    <input type=hidden name=description value="<%=hiddenvariables.get("oid")%>">
                    <input type=hidden name=orderdescription value="Order placed from VT ">
                    <input type=hidden name=redirecturl value="<%=hiddenvariables.get("redirecturl")%>">
                    <input type=hidden name=checksum value="<%=hiddenvariables.get("checksum")%>">
                    <input type="hidden" name="TMPL_COUNTRY" value="<%=hiddenvariables.get("countrycode")%>">
                    <input type="hidden" name="TMPL_AMOUNT" value="1">
                    <input type="hidden" name="TMPL_CURRENCY" value="<%=currency%>">
                    <input type="hidden" name="TMPL_street" value="<%=hiddenvariables.get("b_address")%>">
                    <input type="hidden" name="TMPL_state" value="<%=hiddenvariables.get("b_state")%>">
                    <input type="hidden" name="TMPL_city" value="<%=hiddenvariables.get("b_city")%>">
                    <input type="hidden" name="TMPL_zip" value="<%=hiddenvariables.get("b_zipcode")%>">
                    <input type="hidden" name="TMPL_telno" value="<%=hiddenvariables.get("phone2")%>">
                    <input type="hidden" name="TMPL_telnocc" value="<%=hiddenvariables.get("phone1")%>">
                    <input type="hidden" name="TMPL_emailaddr" value="<%=hiddenvariables.get("email")%>">
                    <input type="hidden" name="paymenttype" value="<%=hiddenvariables.get("paymenttype")%>">
                    <input type="hidden" name="cardtype" value="<%=hiddenvariables.get("cardtypeid")%>">
                    <input type="hidden" name="terminalid" value="<%=hiddenvariables.get("terminalid")%>">
                    <input type="hidden" name="ipaddr" value="<%=hiddenvariables.get("ipaddr")%>">
                    <input type="hidden" name="logo" value="<%=session.getAttribute("logo")%>">

                    <INPUT TYPE="submit" value="Pay Now">
                  </form>
                </td>
              </tr>
            </table>
            </td>
          </tr>
        </table>
</td>
  </tr>
</table>
 </body>
</html>