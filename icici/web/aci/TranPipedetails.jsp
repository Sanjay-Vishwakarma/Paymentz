<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" session="true" %>
<%@ page import="java.text.DecimalFormat" %>

<HTML>
<HEAD>
<TITLE>Colors of Success</TITLE>
</HEAD>

<BODY>

<%@ page import="java.io.*" %>

<%

String sku = request.getParameter("SKU");
String desc = request.getParameter("NAME");
String price = request.getParameter("PRICE");
double pr = Double.parseDouble(price);
int qty = Integer.parseInt(request.getParameter("QTY"));
double totalPrice = qty * pr;
String total = new DecimalFormat("#####.00").format(totalPrice);

%>

<CENTER>

<TABLE border="1">
	<TR>
		<TD>SKU:</TD>
		<TD>Description:</TD>
		<TD>Unit Price:</TD>
		<TD>Qty:</TD>
		<TD>Price:</TD>
	</TR>
	<TR>
		<TD><%=sku%></TD>
		<TD><%=desc%></TD>
		<TD><%=price%></TD>
		<TD><%=qty%></TD>
		<TD><%=total%></TD>
	</TR>
	<TR>
		<TD></TD><TD></TD><TD></TD>
		<TD>Total Price:</TD>
		<TD><%=total%></TD>
	</TR>
</TABLE>

<P>
<FONT size="3"><B>Shipping Details:</B></FONT>
<HR>
<%
File basePath = new File(pageContext.getServletContext().getRealPath("/"));
File usersFile = new File(basePath+"/aci/", "SBM.properties");
//out.println("usersFile----:"+ usersFile);
BufferedInputStream  in = new BufferedInputStream(new FileInputStream(usersFile) );
BufferedReader br = new BufferedReader (new FileReader(usersFile));
String headerCount="", str="", institutionId="";
int index=0;
		while ((str = br.readLine()) != null) {
				if ( str.substring(0,str.indexOf("=")) .equals("gateway.headerCount") ){
						index				=	str.indexOf("=");
						headerCount			=	str.substring(index+1, str.length());
						str					=	br.readLine();
						index				=	str.indexOf("=");
						institutionId		=	str.substring(index+1, str.length());
					}
		}
%>
<FORM name="form1" ACTION="TranPipeCardInfo.jsp" METHOD="POST">
<input type="hidden" name="InstituteID" value="<%=institutionId%>">


<input type="hidden" name="SKU"   value="<%=sku%>">
<input type="hidden" name="NAME"  value="<%=desc%>">
<input type="hidden" name="QTY" value="<%=qty%>">
<input type="hidden" name="PRICE" value="<%=price%>">
<input type="hidden" name="AMOUNT" value="<%=total%>">

<TABLE>
	<TR>
		<TD width="30%">Name:</TD>
		<TD><input size="20" type="text" name="name"></TD>
	</TR>
	<TR>
		<TD width="30%">Address Line 1:</TD>
		<TD><input size="25" type="text" name="addr1"></TD>
	</TR>
	<TR>
		<TD width="30%">Address Line 2:</TD>
		<TD><input size="25" type="text" name="addr2"></TD>
	</TR>
	<TR>
		<TD width="30%">Address Line 3:</TD>
		<TD><input size="25" type="text" name="addr3"></TD>
	</TR>
	<TR>
		<TD width="30%">City:</TD>
		<TD><input size="20" type="text" name="city"></TD>
	</TR>
	<TR>
		<TD width="30%">State:</TD>
		<TD><input size="20" type="text" name="state"></TD>
	</TR>
	<TR>
		<TD width="30%">Country:</TD>
		<TD><input size="25" type="text" name="country"></TD>
	</TR>
	<TR>
		<TD width="30%">Postal Code:</TD>
		<TD><input size="12" type="text" name="postalcd"></TD>
	</TR>
	<TR>
		<TD width="30%"></TD>
		<TD><input type="Submit" value="Buy"></TD>
	</TR>
</TABLE>

</FORM>
</CENTER>

</BODY>
</HTML>
