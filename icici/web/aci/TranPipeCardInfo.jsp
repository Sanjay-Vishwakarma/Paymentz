<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<TITLE>Colors of Success - TranPipe Test Page</TITLE>
</HEAD>
<BODY>
<DIV align="center">
  <font size=10>Plug-in Test Tool</font>
</DIV>
<IMG src="images/bar.gif" width="100%" height="10" border="0">
<%
String instituteId = request.getParameter("InstituteID");
String amt = request.getParameter("AMOUNT");
String name = request.getParameter("name");
String addr1 = request.getParameter("addr1");
String addr2 = request.getParameter("addr2");
String addr3 = request.getParameter("addr3");
String city = request.getParameter("city");
String state = request.getParameter("state");
String country = request.getParameter("country");
String postalCd = request.getParameter("postalcd");
%>
<CENTER>
<P>
<FONT size="5"><B>Transaction Details</B></FONT>
<FORM name="form1" ACTION="TranPipeBuy.jsp" METHOD="POST">
<TABLE align=center border=1  bordercolor=black>
<tr><td>
<TABLE align=center border=0  bordercolor=black>
	<TR>
		<TD width="30%">Action Type:</TD>
		<TD><select type="text" name="action" >
					<option value="1">1 - Purchase   </option> 
			<option value="2"> 2 - Credit   </option>
            <option value="4"> 4 - Authorisation </option>
            <option value="5"> 5 - Capture</option>
        </select>
		</TD>
    </TR>
    <TR>
		<TD >Card Number:</TD>
		<TD><input size="19" type="text" name="pan"></TD>
	</TR>
	<TR>
		<TD >CVV:</TD>
		<TD><input size="3" type="text" name="cvv" maxlength=4></TD>
	</TR>
	<TR>
		<TD >ExpMonth:</TD>
		<TD><select type="text" name="expmm" >
			<option value="1">1</option> 
			<option value="2">2</option> 
			<option value="3">3</option> 
			<option value="4">4</option> 
			<option value="5">5</option> 
			<option value="6">6</option> 
			<option value="7">7</option> 
			<option value="8">8</option> 
			<option value="9">9</option> 
			<option value="10">10</option> 
			<option value="11">11</option> 
			<option value="12" selected>12</option> 
			</select>
		ExpYear:
		<select type="text" name="expyy" >
			<option value="2004">2004</option> 
			<option value="2005">2005</option> 
			<option value="2006">2006</option> 
			<option value="2007">2007</option> 
			<option value="2008" >2008</option> 
			<option value="2006">2009</option> 
			<option value="2007">2010</option>
			<option value="2006">2011</option> 
			<option value="2007" selected>2012</option>
			</select>
		</TD></tr>
		<TR>
		<TD >Cardholder's Name:</TD>
		<TD><input size="25" type="text" name="name"></TD>
		</TR>
        <TR>
		<TD >Transid:</TD>
		<TD><input size="25" type="text" name="transid"></TD>
		</TR>
        <TR>
			<TD width="30%">Payment ID:</TD>
			<TD><input size="40" type="text" name="payID"></TD>
		</TR>
</TABLE>

</td></tr></table>

<TABLE align=center border=0  bordercolor=black>

<TR>
		<TD ></TD>
		<TD><input type="Submit" value="Buy"></TD>
	</TR>

</table>

<input type=hidden name=InstituteID value="<%= instituteId %>" >
<input type=hidden name=type value="CC" >
<input type=hidden name=udf5 value="PaymentID" >
<input type=hidden name=currcd value="840" >
<input type=hidden name=AMOUNT value="<%= amt %>" >
<input type=hidden name="name"  value="<%=name%>">
<input type=hidden name="addr1"  value="<%=addr1%>">
<input type=hidden name="addr2"  value="<%=addr2%>">
<input type=hidden name="addr3"  value="<%=addr3%>">
<input type=hidden name="city"  value="<%=city%>">
<input type=hidden name="state"  value="<%=state%>">
<input type=hidden name="country"  value="<%=country%>">
<input type=hidden name="postalcd"  value="<%=postalCd%>">
</FORM>
</P>
</CENTER>
<CENTER>

</CENTER>
</BODY>
</HTML>
