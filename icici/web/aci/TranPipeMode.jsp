<html><HEAD><TITLE>HostedPayment Merchant Website </TITLE>
<FORM name="form1" ACTION="/demo1/TranPipeCardInfo.jsp" METHOD="POST">
<Center>
<%@ page import="java.io.*" %>

<%
File basePath = new File(pageContext.getServletContext().getRealPath("/"));
File usersFile = new File(basePath+"/aci/", "SBM.properties");
//out.println("usersFile----:"+ usersFile);
BufferedInputStream  in = new BufferedInputStream(new FileInputStream(usersFile) );
String str = "", str1="", str2="",instituteName="", instituteDesc="";
BufferedReader br = new BufferedReader (new FileReader(usersFile));
String sku = request.getParameter("SKU");
String desc = request.getParameter("NAME");
String qty = request.getParameter("QTY");
String price = request.getParameter("PRICE");
String total = request.getParameter("AMOUNT");
String name = request.getParameter("name");
String addr1 = request.getParameter("addr1");
String addr2 = request.getParameter("addr2");
String addr3 = request.getParameter("addr3");
String city = request.getParameter("city");	
String state = request.getParameter("state");
String country = request.getParameter("country");
String postalCd = request.getParameter("postalcd");

%>
<input type="hidden" name="SKU"   value="<%=sku%>">
<input type="hidden" name="NAME"  value="<%=desc%>">
<input type="hidden" name="QTY" value="<%=qty%>">
<input type="hidden" name="PRICE" value="<%=price%>">
<input type="hidden" name="AMOUNT" value="<%=total%>">
<input type=hidden name="name"  value="<%=name%>">
<input type=hidden name="addr1"  value="<%=addr1%>">
<input type=hidden name="addr2"  value="<%=addr2%>">
<input type=hidden name="addr3"  value="<%=addr3%>">
<input type=hidden name="city"  value="<%=city%>">
<input type=hidden name="state"  value="<%=state%>">
<input type=hidden name="country"  value="<%=country%>">
<input type=hidden name="postalcd"  value="<%=postalCd%>">

<table  width = 100% border = 0 BgColor = "Brown" >
<tr  height="22">
			<td align="center"  class="head" colspan=4><font  size = 2 color = White face = verdana >Payment Mode :</font></td>
		</tr>
</table>
<TABLE  width=100% border=1 align="Center" cellPadding=1 cellSpacing=1  >
<tr class=boldEleven width = 100%>
<%
			while ((str = br.readLine()) != null) {
				if ( str.substring(0,str.indexOf("=")) .equals("gateway.header") ){
					str1			=	 br.readLine();
					str2			=	 br.readLine();
					instituteName	=	 br.readLine();
					instituteDesc	=	 br.readLine();
			%>
				<td width = 15%></td>
				<tr><td align="Left" ><input type="radio" name="InstituteID" value = <%= str.substring(str.indexOf("=")+1)%> onClick = "javascript:document.form1.submit()" ><b><font size = 2 color = red > <%= instituteName.substring(instituteName.indexOf("=")+1)%></font> 
				 <br><font size = 1 color = black> <%= instituteDesc.substring(instituteDesc.indexOf("=")+1)%> </font></b></td>
				</tr>
				<%				
				}
			 } //closes while
%></tr>
</table> 

</html>
