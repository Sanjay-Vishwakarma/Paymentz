<html>

<head>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<style type="text/css">
<!--
font {  font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 9px; font-style: normal; font-weight: bold; color: #333333; text-decoration: none}
.font1 {  font-family: Arial, Helvetica, sans-serif; font-size: 9px; font-style: normal; font-weight: bold; color: #333333; text-decoration: none}
input {  border: 1px #000000 solid}
-->
</style>

<title>Form</title>
</head>

<body>

 <form method="POST" enctype="UTF-8" action="/icici/servlet/PostPay" >
  <table align="center" >
  <tr align="center" ><td colspan="2"><img src="/icici/images/logo.gif" ></td></tr>
  <tr align="center" ><td colspan="2">&nbsp;</td></tr>
  <tr align="center" ><td colspan="2"><img src="/icici/images/verified_logo.gif" ></td></tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <input type="hidden" name="merchantID" value="MPI0003" size="20">
  <input type="hidden" name="whatiuse" value="">
  <input type="hidden" name="deviceCategory" value="0">
<input type="hidden" name="acceptHdr" value="*/*">
<input type="hidden" name="agentHdr" value="Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; YComp 5.0.0.0)">
<input type="hidden" name="ShoppingContext" value="4321">
  <tr><td  class="label" ><font color="#800000">Card number</font></td><td><input type="text" name="pan" size="20" maxlength="19" ></td></tr>
  <tr><td class="label" ><font color="#800000">Expiry date (MMYY)</font></td><td><input type="text" name="expirydate" size="20" maxlength="19" >
   <!-- <select NAME="EXPIRE_MONTH">
	<option VALUE="01" selected>January</option>
	<option VALUE="02">February</option>
	<option VALUE="03">March</option>
	<option VALUE="04">April</option>
	<option VALUE="05">May</option>
	<option VALUE="06">June</option>
	<option VALUE="07">July</option>
	<option VALUE="08">August</option>
	<option VALUE="09">September</option>
	<option VALUE="10">October</option>
	<option VALUE="11">November</option>
	<option VALUE="12">December</option>
	</select>
	<select NAME="EXPIRE_YEAR">
	<option VALUE="2003" SELECTED>2003</option>
	<option VALUE="2004">2004</option>
	<option VALUE="2005">2005</option>
	<option VALUE="2006">2006</option>
	<option VALUE="2007">2007</option>
	<option VALUE="2008">2008</option>
	<option VALUE="2009">2009</option>
	<option VALUE="2010">2010</option>
	<option VALUE="2011">2011</option>
	</select> -->
  </td></tr>
  <tr><td class="label" ><font color="#800000">Display Amount</font></td><td class="label" ><font color="#800000"><%=request.getParameter("displayAmount")%></font></td></tr>
  <tr><td class="label" ><font color="#800000">Order ID</font></td><td class="label" ><font color="#800000"><%=request.getParameter("orderdesc")%></font></td></tr>
  <input type="hidden" name="displayAmount"  value="<%=request.getParameter("displayAmount")%>"></font></td></tr>
  <input type="hidden" name="purchaseAmount" value="<%=request.getParameter("purchaseAmount")%>" >
  <input type="hidden" name="currencyVal"  value="418">
  <input type="hidden" name="exponent"  value="2">
  <input type="hidden" name="orderdesc"  value="<%=request.getParameter("orderdesc")%>">
  <input type="hidden" name="recur_freq"  value="12">
  <input type="hidden" name="recur_end"  value="20011212">
  <input type="hidden" name="installment" value="12">
  <!--<tr><td>MerchantID</td><td><input type="text" name="merchantID" size="20"></td></tr>-->
  <tr><td></td><td><input type="submit"  value="Charge My Card" name="B1">&nbsp;&nbsp;<input type="reset" value="Reset" name="B2"></td></tr>
</form>
</table>
</body>

</html>
