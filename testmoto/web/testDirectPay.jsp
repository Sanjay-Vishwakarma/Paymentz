<script language="javascript">

function debug()
{

alert('sending->'+document.ECom.requestparameter.value);

}

</script>
<%

   String requestparameter = "200805141000002|DOM|IND|INR|100.00|111111|NULL|https://secure.tc.com/testmoto/testDirectPayResponse.jsp|https://secure.tc.com/testmoto/testDirectPayResponse.jsp|TOML|1025-Demo Bank|DD";

%>
<FORM NAME = "ECom" METHOD = "POST" ACTION="https://test.timesofmoney.com/direcpay/secure/dpMerchantParams.jsp" onSubmit="debug();">
<INPUT TYPE = "hidden" NAME = "requestparameter" VALUE ="<%=requestparameter%>">
<INPUT type="submit" value="submit" >
</FORM>