<%@ page import="java.util.Hashtable,
                 com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
                 <%@ include file="functions.jsp"%>
                 <%@ include file="index.jsp"%>
 <%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jun 13, 2012
  Time: 7:34:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
  <script type="text/javascript">

        function goBack()
          { var a= document.getElementById('ctoken').value;
            document.location.href = "/icici/admin/index.jsp?ctoken="+a;

          }


           function toggle()
           {
                var A= document.getElementById("trackingid");
                var B=document.getElementById("merchantid");
                if(A.checked==true)
                {
                    B.checked=false;
                }

           }
          </script>

  </head>
  <body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
  <h1 align="center">Exceptional Transaction Search Interface</h1>


  <form action="/icici/servlet/ExceptionalTransaction?ctoken=<%=ctoken%>" method="post" name="forms" >

    <%

    String str="";

	int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
	int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);

        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        String val="";


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
        String currentyear= ""+rightNow.get(rightNow.YEAR);

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        /*if (fdate != null) str = str + "fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;*/
           String searchstatus = request.getParameter("searchstatus");
    String searchId = request.getParameter("SearchId");

    /*str = str + "&searchstatus="+searchstatus;
    str = str + "&SearchId="+searchId;
    str = str + "&SRecords=" + pagerecords;*/
  %>

	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="40%" bordercolorlight="#000000" bordercolordark="#FFFFFF">

        <tr>
		<td width="692" bgcolor="#2379A5" colspan="3" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Search Status</b></font></td>
		</tr>
        <tr>



			<td class=search  align="center">
            <input type="radio" name="searchstatus" value="authfailed"  >Authorisation Failed Transaction</td>

            <td class=search  align="center">
            <input type="radio" name="searchstatus" value="authstarted"   >Authorisation Started Transaction</td>

            <td class=search  align="center">
            <input type="radio" name="searchstatus" value="reversal" > Reversal Request Sent Transaction</td>
		</tr>

        <tr>
		<td width="692" bgcolor="#2379A5" colspan="2" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Search Type</b></font></td>
        <td  bgcolor="#2379A5"  align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Search Id</b></font></td>
		</tr>

		<tr>


			<td class=search  align="center" >
            <input type="checkbox" name="searchType" value="Tracking ID" id="trackingid"  onclick="toggle();"  >Tracking Id</td>


            <td class=search  align="center">
            <input type="checkbox" name="searchType" value="Merchant ID" id="merchantid" onclick="toggle();"  >Merchant ID</td>

            <td colspan=3 align=center>
            <input name="SearchId" maxlength="100" value="<%=request.getParameter("SearchId")==null?"":request.getParameter("SearchId")%>"></td>

		</tr>
        <tr>
        <td width="692" bgcolor="#2379A5" colspan="2" align="center"><font color="#FFFFFF" size="1" face="Verdana, Arial" ><b>Search by Date</b></font></td>
        </tr>
        <tr>
            <%--<td valign="middle" align="right"    class="label">--%>
    <td valign="middle" align="left"     class="label">From:
        <select size="1" name="fdate" class="textBoxes" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
            <%
                if (fdate != null)
                    out.println(Functions.dayoptions(1, 31, fdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>
        <select size="1" name="fmonth" class="textBoxes" value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
            <%
                if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select size="1" name="fyear" class="textBoxes" value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
            <%
                if (fyear != null)
                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>
        <%--<td valign="middle" align="right"  class="label"></td>--%>
    <td valign="middle" align="left" class="label">To:
        <select size="1" name="tdate" class="textBoxes">

            <%
                if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>

        <select size="1" name="tmonth" class="textBoxes">

            <%
                if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>

        <select size="1" name="tyear" class="textBoxes">

            <%
                if (tyear != null)
                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>

    </td>
       <td class=search colspan=3 align=center>
			<input type="submit" value="Search">
            <input type="button" value="Back" onclick="goBack()" />
			</td>


        </tr>

	</table>
</form>

<%  //Exceptional Transaction table Start
    String errormsg = (String) request.getAttribute("error");
    if(errormsg!=null)
    {
            out.println("<table align=\"center\"  >");
            out.println("<tr><td algin=\"center\" ><font face=\"arial\" color=\"red\"  size=\"2\">");
            errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
            out.println(errormsg);
            out.println("</font>");
            out.println("</td></tr></table>");
    }
    else
    {
        errormsg = "";
    }
   if(request.getAttribute("exceptionaltransaction")!=null)
   {
	Hashtable hash=(Hashtable)request.getAttribute("exceptionaltransaction");
	Hashtable temphash=null;
	//int pageno=1;
	int records=30;
	//out.println(hash);
	int hashsize=hash.size();



    int totalrecords = 0;
    int currentblock = 1;

    try
    {
        records = Integer.parseInt((String) hash.get("records"));
        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        currentblock = Integer.parseInt(request.getParameter("currentblock"));
    }
    catch (Exception ex)
    {

    }

     if (fdate != null) str = str + "?fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;
           searchstatus = request.getParameter("searchstatus");
    searchId = request.getParameter("SearchId");
    str = str + "&ctoken="+ctoken;
    str = str + "&searchstatus="+searchstatus;
    str = str + "&SearchId="+searchId;
    str = str + "&SRecords=" + pagerecords;

%>
<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
<input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
<input id="ctoken" type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center  >
<tr ><td valign="middle" colspan="10"  align="center"   bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" >Exceptional Transaction</font></td></tr>
<tr>

    <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">TrackingId</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Toid</td>
    <td valign="middle" align="center" bgcolor="#008BBA">AccountId</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Status</td>


            <%
                if(request.getAttribute("searchstatus")!=null && request.getAttribute("searchstatus").equals("reversal"))
            {
            %>

                <td valign="middle" align="center" bgcolor="#008BBA">Refund responce code</td>
                <td valign="middle" align="center" bgcolor="#008BBA">Refund responce description</td>
            <%
            }
            else
            {
            %>

               <td valign="middle" align="center" bgcolor="#008BBA">Auth responce code</td>
                <td valign="middle" align="center" bgcolor="#008BBA">Auth responce description</td>
            <%

            }

             %>

	<td valign="middle" align="center" bgcolor="#008BBA">Transaction Date</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Last update Date</td>

</tr>
<%
	String style="class=td11";
	String ext="light";
    String hiddenField = "";

	for(int pos=1;pos<=records;pos++)
	{
		String id=Integer.toString(pos);
        hiddenField = "";

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td22";
				ext="dark";
			}
			else
			{
				style="class=td11";
				ext="light";
			}

		    temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
			//out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"chargeback\" value=\""+temphash.get("icicitransid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;" +temphash.get("icicitransid")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("toid")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("accountid")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("amount")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("status")+"</td>");


        if(request.getAttribute("searchstatus")!=null && request.getAttribute("searchstatus").equals("reversal"))
        {

            out.println("<td "+style+">&nbsp;"+(String)temphash.get("refundqsiresponsecode")+"</td>");

            out.println("<td "+style+">&nbsp;"+(String)temphash.get("refundqsiresponsedesc")+"</td>");

        }
        else
        {
           out.println("<td "+style+">&nbsp;"+(String)temphash.get("authqsiresponsecode")+"</td>");

            out.println("<td "+style+">&nbsp;"+(String)temphash.get("authqsiresponsedesc")+"</td>");

        }

			out.println("<td "+style+">&nbsp;"+(String)temphash.get("Transaction Date")+"</td>");
            out.println("<td "+style+">&nbsp;"+(String)temphash.get("timestamp")+"</td>");
            out.println("</tr>");

	}
%>
<%--<tr><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><input class="button" type="button" value="Proccess Selected ChargeBack "  onClick="return DoChargeback();" ></td></tr>--%>
<tr><td colspan=3 valign="middle"   align="center"   bgcolor="#008BBA"> <font  color="#ffffff" >Total Records : <%=totalrecords%></font></td>
</table>
<br>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true">
    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
    <jsp:param name="numrows" value="<%=pagerecords%>"/>
    <jsp:param name="pageno" value="<%=pageno%>"/>
    <jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
    <jsp:param name="page" value="ExceptionalTransaction"/>
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
	out.println(ShowConfirmation("Sorry","No records found."));
}
%>
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