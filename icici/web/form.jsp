<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Form</title>
</head>

<body>

<!--<form method="POST" enctype="UTF-8" action="https://203.199.32.82/ocsmpi/merchant.asp" > -->
 <form method="POST" action="https://secure.tc.com/icici/servlet/TestServlet" >
  <table align="center" >
  <tr align="center" ><td colspan="2"><img src="/icici/images/verified_logo.gif" ></td></tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr><td>MerchantID</td><td><input type="text" name="merchantID" size="20"></td></tr>
  <tr><td>Card number</td><td><input type="text" name="PAN" size="20" maxlength="19" ></td></tr>
  <tr><td>Expiry date (MMYY)</td><td>
  <input type="text" name="expirydate" size="20" maxlength="19" >
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
  <tr><td>PurchaseAmount (Ex. 1,234.56 )</td><td><input type="text" name="PurchaseAmount" size="20"></td></tr>
  <tr><td>Currency code</td><td><input type="text" name="Currencycode" size="20" maxlength="3"></td></tr>
  <tr><td>DisplayAmount (Ex. $1,234.56 )</td><td><input type="text" name="DisplayAmount" size="20"></td></tr>

  <tr><td>Description</td><td><input type="text" name="orderdesc" size="20"></td></tr>
  <tr><td>SHOPPING CONTEXT ( any description <br> that merchant want to receive back )</td><td><input type="text" name="SHOPPINGCONTEXT" size="20"></td></tr>
  <!--<tr><td>MerchantID</td><td><input type="text" name="merchantID" size="20"></td></tr>-->
  <tr><td></td><td><input type="submit" value="Submit" name="B1"><input type="reset" value="Reset" name="B2"></td></tr>
</form>
</table>
</body>

</html>
