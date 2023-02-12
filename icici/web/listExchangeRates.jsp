<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="com.manager.enums.PZTransactionCurrency" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Jitendra
  Date: 27/02/18
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Functions functions = new Functions();
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Settings> Exchange Rate</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Currency Exchange Master
        <div style="float: right;">
          <form action="/icici/addNewExchangeRate.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Charge" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Mapping
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/ListExchangeRates?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="ctoken=" + ctoken;
          str=str+"&isinputrequired="+request.getParameter("isinputrequired");
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >From Currency</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="fromcurrency" class="txtbox">
                      <option value="" selected>&nbsp;&nbsp;&nbsp;&nbsp;Select Currency</option>
                      <%
                        for (PZTransactionCurrency transactionCurrency: PZTransactionCurrency.values()){
                          out.println("<option value=\""+transactionCurrency.name()+"\">"+transactionCurrency.name()+"</option>");
                        }
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >To Currency</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="tocurrency" class="txtbox">
                      <option value="" selected>&nbsp;&nbsp;&nbsp;&nbsp;Select Currency</option>
                      <%
                        for (PZTransactionCurrency transactionCurrency: PZTransactionCurrency.values()){
                          out.println("<option value=\""+transactionCurrency.name()+"\">"+transactionCurrency.name()+"</option>");
                        }
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" >
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
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    String message=(String)request.getAttribute("msg");
    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;
    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
      out.println(error);

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
  %>

  <input type="hidden" name="ctoken" value="<%=ctoken%>">
  <table align=center style="width:100%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0">Sr No</td>
      <td valign="middle" align="center" class="th0">From Currency</td>
      <td valign="middle" align="center" class="th0">To Currency</td>
      <td valign="middle" align="center" class="th0">Exchange Rate</td>
      <td valign="middle" align="center" class="th0">Creation Time</td>
      <td valign="middle" align="center" class="th0">Last Updated Time</td>
      <td valign="middle" align="center" class="th0">Action</td>
    </tr>
    </thead>
    <%
      SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String style="class=td1";
      String ext="light";

      for(int pos=1;pos<=records;pos++)
      {
        String id=Integer.toString(pos);
        int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

        if(pos%2==0)
        {
          style="class=tr0";
        }
        else
        {
          style="class=tr1";
        }

        temphash=(Hashtable)hash.get(id);
        temphash.get(id);
        String date= (String) temphash.get("dtstamp");
        if(functions.isValueNull(date))
        {
          date=targetFormat.format(targetFormat.parse(date));
        }

        String date2= (String) temphash.get("timestamp");
        if (functions.isValueNull(date2))
        {
          date2=targetFormat.format(targetFormat.parse(date2));
        }

        out.println("<tr>");
        out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("from_currency"))+"</td>");
        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("to_currency"))+"</td>");
        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("exchange_rate"))+"</td>");
        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)date)+"</td>");
        out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)date2)+"</td>");
        out.println("<td class=\"tr0\" align=\"center\"><form action=\"/icici/servlet/UpdateCurrencyExchange?ctoken="+ ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"id\" value="+temphash.get("id")+"><input type=\"submit\" class=\"button\" value=\"modify\" name=\"action\"></button></form></td>");
        out.println("</tr>");
      }
    %>
  </table>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="ListExchangeRates"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
    }
    else if(functions.isValueNull(message))
    {
      out.println(Functions.NewShowConfirmation("Result",message));
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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