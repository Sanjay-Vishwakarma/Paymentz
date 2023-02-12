<html>
<head>
<title>Confirmation</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style type="text/css">
<!--
font {  font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 9px; font-style: normal; font-weight: bold; color: #333333; text-decoration: none}
.font1 {  font-family: Arial, Helvetica, sans-serif; font-size: 9px; font-style: normal; font-weight: bold; color: #333333; text-decoration: none}
-->
</style>
<script language="JavaScript">
<!--
function MM_goToURL() { //v3.0
  var i, args=MM_goToURL.arguments; document.MM_returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}
//-->
</script>
</head>

<body bgcolor="#FFFFFF" text="#000000">
<table width="518" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr> 
    <td width="64" height="60"></td>
    <td width="33"></td>
    <td width="9"></td>
    <td valign="top" colspan="2"><img src="/icici/images/logo.gif" width="318" height="60"></td>
    <td width="37"></td>
    <td width="57"></td>
  </tr>
  <tr>
    <td height="28"></td>
    <td></td>
    <td></td>
    <td width="309"></td>
    <td width="9"></td>
    <td></td>
    <td></td>
  </tr>
  <tr> 
    <td height="145"></td>
    <td></td>
    <td valign="top" bgcolor="#CCCCCC" colspan="2"> 
      <div align="center"> 
        <p><font><br>
        <%
           // String status=(String )request.getAttribute("STATUS");
            String status=request.getParameter("status");
            if(status != null && status.equalsIgnoreCase("Y"))
            {
                out.println("Your transaction was successful.");
            }
			else
			{
                out.println("Your transaction was failed.");
			}
		  %>
		  </font></p>
        <p class="font1">Purchase ID : <FONT><%=request.getParameter("orderdesc")%></FONT></p>
        <p class="font1">Please quote the above mentioned purchase ID<br>
          in your corresspondence related to this transaction.</p>
        <p class="font1" onClick="MM_goToURL('parent','test.html');return document.MM_returnValue"><a href="#">back 
          to products listing.</a></p>
      </div>
    </td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr> 
    <td height="77"></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <tr> 
    <td height="33"></td>
    <td colspan="5" valign="top" class="font1"> 
      <div align="center">prices are subject to change and not indicative of current 
        value. <br>
        all trademarks are copyright of their respective owners.<br>
        this is a demonstration process for the Payment Gateway system.</div>
    </td>
    <td></td>
  </tr>
</table>
</body>
</html>
