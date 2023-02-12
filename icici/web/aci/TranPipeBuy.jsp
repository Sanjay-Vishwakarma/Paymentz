<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>Colors of Success - Tran Pipe Testor</TITLE>
</HEAD>
</HTML>
<%@ page language="java" session="true" %>
<%--@ page import="com.aciworldwide.demo.Config" --%>
<%@ page import="com.aciworldwide.commerce.gateway.plugins.e24TranPipe" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.io.*" %>
<%  String payID = request.getParameter("payID");
	String action = request.getParameter("action");
	String amount = request.getParameter("AMOUNT");
	String currcd = request.getParameter("currcd");
	String name1   = request.getParameter("name");
	String addr   = request.getParameter("addr");
	String pan    = request.getParameter("pan");
	String cvv    = request.getParameter("cvv");
	String expmm  = request.getParameter("expmm");
	String expyy  = request.getParameter("expyy");
	String type  = request.getParameter("type");
	String udf5  = request.getParameter("udf5");

	String instituteId	=	request.getParameter("InstituteID");
	//String trackid = String.valueOf(Math.abs(rnd.nextLong()));

	// Start building the Payment message with the generic data elements
	e24TranPipe e24tp = new e24TranPipe();

	File basePath = new File(pageContext.getServletContext().getRealPath("/"));
	File usersFile = new File(basePath+"/aci/", "SBM.properties");
	//out.println("usersFile----:"+ usersFile);

	String str = "",  resourcePath="", aliasName="";

	BufferedReader br = new BufferedReader (new FileReader(usersFile));
		while ((str = br.readLine()) != null) 
		 {
			if ( str.substring(0,str.indexOf("=")) .equals("resource.path") )
				resourcePath	=	str.substring(str.indexOf("=")+1);
			if ( str.substring(0,str.indexOf("=")) .equals("default.terminal.alias") )
				aliasName	=	str.substring(str.indexOf("=")+1);
		 }
	//System.out.println(" resourcePath---"+instituteId);
	String name = request.getParameter("name");
	String addr1 = request.getParameter("addr1");
	String addr2 = request.getParameter("addr2");
	String addr3 = request.getParameter("addr3");
	String city = request.getParameter("city");
	String state = request.getParameter("state");
	String country = request.getParameter("country");
	String postalCd = request.getParameter("postalcd");

    e24tp.setAlias(aliasName);
    e24tp.setResourcePath(resourcePath);
    e24tp.setAction(action);
    e24tp.setAmt(amount);
    e24tp.setTrackId(payID);
    e24tp.setTransId(request.getParameter("transid"));
    e24tp.setCurrencyCode(currcd);

    e24tp.setMember(name);

    e24tp.setCard(pan);

  //  e24tp.setType("CC");

    e24tp.setExpMonth(expmm);
    e24tp.setExpYear(expyy);
    e24tp.setCvv2(cvv);
    e24tp.setAddress("1234 First Street");
    e24tp.setZip("68116");

    e24tp.setUdf1("UDF1");

    e24tp.setDebug(true);


    int result=e24tp.performTransaction();
    if( result != e24tp.SUCCESS)
		{
            out.println("<BR> Error sending TranPipe Request:");
            out.println("<BR> get result "+result);
            out.println("<BR> get debug message "+e24tp.getDebugMsg());
            out.println("<BR> getErrorMsg : "+ e24tp.getErrorMsg());  //GW00150
			out.println("<BR> getErrorText: "+ e24tp.getErrorText()); //!ERROR!-GW00150-Missing required data.
			out.println("<BR> getTrackId: "+ e24tp.getTrackId());
			out.println("<BR> getResult: "+ e24tp.getResult());
			out.println("<BR> getResponseCode : "+ e24tp.getResponseCode());
			//response.sendRedirect( response.encodeRedirectURL("error.jsp?ErrorTx=" + e24tp.getErrorMsg()+"&paymentid="+payID));
   			return;
		}
%>

<HTML>
<BODY>

<TABLE align=center border=1  bordercolor=black><tr><td>

<TABLE align=center border=0  bordercolor=black>
<tr><td>Error MSG</td>
	<td><%=e24tp.getErrorMsg()%></td>
</tr>
<tr><td>Result </td>
	<td><%=result%></td>
</tr>
        <TR>
			<TD colspan="2" align="center">
				<FONT size="4"><B>Transaction Details   </B></FONT>
			</TD>
		</TR>
		<TR>
			<TD colspan="2" align="center">
				<HR>
			</TD>
		</TR>

             <TR>
			<TD>Tracking ID</TD>
			<TD><%= e24tp.getTrackId()%></TD>
		</TR>
        <TR>
			<TD>Transaction Status</TD>
			<TD><b><%= e24tp.getResult()%></b></TD>
		</TR>
       <TR>
			<TD>Transaction Response code</TD>
			<TD><b><%= e24tp.getResponseCode()%></b></TD>
		</TR>
    <TR>
			<TD>Transaction Date</TD>
			<TD><%= e24tp.getDate()%></TD>
		</TR>
		<TR>
			<TD>Transaction Reference ID</TD>
			<TD><%= e24tp.getRef()%></TD>
		</TR>
		<TR>
			<TD><b>Transaction ID</b></TD>
			<TD><%= e24tp.getTransId()%></TD>
		</TR>
		<TR>
			<TD>Transaction Auth Code</TD>
			<TD><%= e24tp.getAuth()%></TD>
		</TR>


		</TR>
		</td></tr></table>
		</table>

<br>
		<TABLE align=center border=1  bordercolor=black><tr><td>

<TABLE align=center border=0  bordercolor=black>

		<TR>
			<TD colspan="2" align="center">
				<FONT size="4"><B>Customer Shipping Details    </B></FONT>
			</TD>
		</TR>
		<TR>
			<TD colspan="2" align="center">
				<HR>
			</TD>
		</TR>
		<TR>
			<TD>Customer Name</TD>
			<TD><%=name1%></TD>
		</TR>
		<TR>
			<TD>Address 1</TD>
			<TD><%=addr1%></TD>
		</TR>
		<TR>
			<TD>Address 2</TD>
			<TD><%=addr2%></TD>
		</TR>
		<TR>
			<TD>Address 3</TD>
			<TD><%=addr3%></TD>
		</TR>
		<TR>
			<TD>City</TD>
			<TD><%=city%></TD>
		</TR>
		<TR>
			<TD>State</TD>
			<TD><%=state%></TD>
		</TR>
		<TR>
			<TD>Country</TD>
			<TD><%=country%></TD>
		</TR>
		<TR>
			<TD>Postal Code</TD>
			<TD><%=postalCd%></TD>
		</TR>
	</TABLE></td></tr></table><td></tr></table>
<br>
		<TABLE align=center><tr></tr> <tr></tr><tr></tr>
		<TR>
		<td><FONT size=2 color="BLUE"><A href="TranPipeIndex.jsp">Click here to enter another TranPipe transaction</A></FONT></td>
		</tr>
	<tr><td>
	<FONT size=2 color="BLUE"><A href="HostedPaymentIndex.jsp">Click here to enter a PaymentPipe transaction</A></FONT>
	</td></tr></table>

</BODY>
</HTML>
<%  return; %>