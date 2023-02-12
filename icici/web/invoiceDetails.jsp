<%@ page import="com.directi.pg.Functions,
                 com.invoice.dao.InvoiceEntry,
                 com.invoice.vo.ProductList" %>

<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Oct 11, 2006
  Time: 4:31:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    InvoiceEntry invoiceEntry = new InvoiceEntry();
    SortedMap statushash = invoiceEntry.getSortedMap();

    Hashtable hash = (Hashtable ) request.getAttribute("hiddenvariables");
    List<ProductList> listProducts = (List<ProductList>) hash.get("listOfProducts");
    String invoiceno= (String) hash.get("invoiceno");
    String trackingid=(String) hash.get("trackingid");
    String orderid=(String) hash.get("orderid");
    String orderdesc=(String) hash.get("orderdesc");
    Functions functions = new Functions();



   orderid = Functions.checkStringNull(request.getParameter("orderid"))==null?"":request.getParameter("orderid");
    orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"))==null?"":request.getParameter("orderdesc");

    trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    invoiceno = Functions.checkStringNull(request.getParameter("invoiceno"))==null?"":request.getParameter("invoiceno");
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

    String status = Functions.checkStringNull(request.getParameter("status"));

    Calendar rightNow = Calendar.getInstance();
    String str = "";

    //rightNow.setTime(new Date());
    String currentyear= ""+rightNow.get(rightNow.YEAR);
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

    if (fdate != null) str = str + "fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;
    if (orderid != null) str = str + "&orderid=" + orderid;
    if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();


    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;
%>

<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Invoice Details</title></head>


</head>
<body>
<%@ include file="/index.jsp" %>
<form name="form" method="post" action="Invoice?ctoken=<%=ctoken%>">
<input type="hidden" value="<%=ctoken%>" name="ctoken">

<div class="row">
<div class="col-lg-12">
<div class="panel panel-default" >
<div class="panel-heading" >
     Merchant Invoice
</div>
<input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
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
        <select size="1" name="fdate" class="textBoxes" style="font-size:12px; " >
            <%
                if (fdate != null)
                    out.println(Functions.dayoptions(1, 31, fdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>
        <select size="1" name="fmonth" class="textBoxes" style="color:#151B54;font-size:12px;" >
            <%
                if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select size="1" name="fyear" class="textBoxes" style="color:#151B54;font-size:12px;">
            <%
                if (fyear != null)
                            out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear),fyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >To</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <select size="1" name="tdate" style="color:#151B54;font-size:12px; ">
            <%
                if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>
        <select size="1" name="tmonth" class="textBoxes" style="color:#151B54;font-size:12px;">
            <%
                if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select size="1" name="tyear" class="textBoxes" style="color:#151B54;font-size:12px;width:56px;">
            <%
                if (tyear != null)
                            out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Status</td>
    <td width="3%" class="textb"></td>
    <td width="10%" class="textb">
        <select size="1" name="status"  class="txtbox">
            <option value="">All</option>
            <%
                //Enumeration enu = statushash.keys();

                Set<String> setStatus = statushash.entrySet();
                Iterator i = setStatus.iterator();

                String selected = "";
                String key = "";
                String value = "";

                while (i.hasNext())
                {
                    Map.Entry entryMap =(Map.Entry) i.next();
                    key = (String) entryMap.getKey();
                    value = (String) entryMap.getValue();

                    if (key.equals(status))
                        selected = "selected";
                    else
                        selected = "";

            %>
            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>>
                <%=ESAPI.encoder().encodeForHTML(value)%>
            </option>
            <%
                }
            %>
        </select>
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
        <input type=text name="trackingid" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb">Invoice NO</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="invoiceno"  class="txtbox" maxlength="20" value="<%=ESAPI.encoder().encodeForHTMLAttribute(invoiceno)%>" size="15">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb">Order ID</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="orderid" maxlength="100" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderid)%>">
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
    <td width="8%" class="textb" colspan="2">Order Description</td>
    <td width="12%" class="textb">
        <input type=text name="orderdesc" maxlength="100"  size="15" class="txtbox" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderdesc)%>">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Rows/pages</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="text" maxlength="5"  size="15" class="txtbox" value="<%=pagerecords%>" name="SRecords" size="2" />

    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" ></td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <button type="submit" class="buttonform" name="B1">
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

<div class="reporttable" >
    <table class="table table-striped table-bordered  table-green dataTable">
        <%
            if(hash!=null)
            {
                String style = "class=td0";
                String ext = "light";

        %>

        <tr>
            <td class="textb" colspan="7"><center><b><u>Invoice Details</u></b></center></td>
        </tr>
        <tr >

            <td width="12%" class="th0">Tracking Id </td>
            <td width="12%" class="th1">Invoice No </td>
            <td width="12%" class="th0">Order ID</td>
            <td width="12%" class="th1">Order Description</td>
            <td width="12%" class="th0">Invoice Amount</td>
            <td width="12%" class="th0">Date of Invoice</td>
            <td width="12%" class="th0">Invoice Status</td>

        </tr>

        <%

            invoiceno= (String) hash.get("invoiceno");
            trackingid=(String) hash.get("trackingid");
            if (trackingid == null) trackingid = "-";
            orderid=(String) hash.get("orderid");
            orderdesc=(String) hash.get("orderdesc");
            if (orderdesc == null) orderdesc = "-";
            String amount = (String) hash.get("amount");
            String date = (String) hash.get("date");
            status = (String) hash.get("status");

            out.println("<tr " + style + ">");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(trackingid) + "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(invoiceno) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(orderid) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(orderdesc) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(date) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(status) +  "</td>");

        %>

    </table>
</div>

<div class="reporttable">
    <table class="table table-striped table-bordered  table-green dataTable">
        <tr>
            <td class="textb"  colspan="8"><center><b><u>Customer's Details</u></b></center></td>
        </tr>
        <tr >
            <td width="12%" class="th0">Customers's Name </td>
            <td width="12%" class="th1">Customer's Emailaddress </td>
            <td width="12%" class="th0">Tel No</td>
            <td width="12%" class="th1">City</td>
            <td width="12%" class="th0">Street</td>
            <td width="12%" class="th0">State</td>
            <td width="12%" class="th0">Country</td>
            <td width="12%" class="th0">Pin Code</td>

        </tr>

        <%
            String custname = (String) hash.get("custname");
            if (custname == null) custname = "-";
            String email = (String) hash.get("custemail");
            if (email == null) email = "-";
            String phonecc = (String) hash.get("phonecc");
            if (phonecc == null) phonecc = "-";
            String phone = (String) hash.get("phone");
            if (phone == null) phone = "-";
            String city = (String) hash.get("city");
            if (city == null) city = "-";
            String street = (String) hash.get("address");
            if (street == null) street = "-";
            String zip = (String) hash.get("zipcode");
            if (zip == null) zip = "-";
            String state = (String) hash.get("state");
            if (state == null) state = "-";
            String country = (String) hash.get("country");
            if (country == null) country = "-";

            out.println("<tr " + style + ">");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(custname) + "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(email) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(phone) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(city) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(street) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(state) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(country) +  "</td>");
            out.println("<td align=\"center\"class=\"textb\"" + style + ">" + ESAPI.encoder().encodeForHTML(zip) +  "</td>");

        %>

    </table>
    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry", "No records found. Invalid Invoice No<br><br>"));
        }
    %>
</div>


<%
    if (listProducts.size()>0)
    {
%>

<div class="reporttable">
    <table class="table table-striped table-bordered  table-green dataTable">
        <tr>
            <td class="textb"  colspan="8"><center><b><u>Product List Details</u></b></center></td>
        </tr>
        <tr >
            <td width="12%" class="th0">Product Description </td>
            <td width="12%" class="th1">Product Unit </td>
            <td width="12%" class="th0">Product Amount </td>
            <td width="12%" class="th1">Quantity </td>
            <td width="12%" class="th0">Tax (%) </td>
            <td width="12%" class="th0">Sub Total</td>

        </tr>

        <%

            String taxAmt = "0.00";

            for (ProductList productList : listProducts)
            {

                out.println("<tr>");
                out.println("<td align=\"center\"class=\"textb\">" + ESAPI.encoder().encodeForHTML(productList.getProductDescription()) + "</td>");
                out.println("<td align=\"center\"class=\"textb\">" + ESAPI.encoder().encodeForHTML(productList.getProductUnit()) + "</td>");
                out.println("<td align=\"center\"class=\"textb\">" + ESAPI.encoder().encodeForHTML(productList.getProductAmount()) + "</td>");
                out.println("<td align=\"center\"class=\"textb\">" + ESAPI.encoder().encodeForHTML(productList.getQuantity()) + "</td>");
                out.println("<td align=\"center\"class=\"textb\">" + ESAPI.encoder().encodeForHTML(productList.getTax()) + "</td>");
                out.println("<td align=\"center\"class=\"textb\">" + ESAPI.encoder().encodeForHTML(productList.getProductTotal()) + "</td>");
            }
                if(functions.isValueNull((String) hash.get("taxamount")))
                {
                    taxAmt = (String) hash.get("taxamount");
                }

                out.println("<tr>");
                out.println("<td></td>");
                out.println("<td></td>");
                out.println("<td></td>");
                out.println("<td></td>");
                out.println("<td align=\"center\" class=\"textb\">Tax Amount</td>");
                out.println("<td align=\"center\" class=\"textb\">"+taxAmt+"</td>");

                out.println("</tr>");

                out.println("<tr>");
                out.println("<td></td>");
                out.println("<td></td>");
                out.println("<td></td>");
                out.println("<td></td>");
                out.println("<td align=\"center\" class=\"textb\">Grand Total</td>");
                out.println("<td align=\"center\" class=\"textb\">"+(String) hash.get("amount")+"</td>");

                out.println("</tr>");


        %>

    </table>
    <%
        }

    %>
</div>

</body>
</html>