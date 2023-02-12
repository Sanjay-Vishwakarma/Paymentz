<html><head><title>Payment Gateway</title>
<SCRIPT ID=clientEventHandlersVBS LANGUAGE=vbscript></SCRIPT>
<script language=javascript>
function fn()
{
    if(document.frm.transfer.value != 'y'){
        document.frm.BGCOLOR.value = window.opener.document.mainForm.BGCOLOR.value
        document.frm.TEXTCOLOR.value = window.opener.document.mainForm.TEXTCOLOR.value
        document.frm.FONTFACE.value = window.opener.document.mainForm.FONTFACE.value
        document.frm.HIGHLIGHT_TEXT_COLOR.value = window.opener.document.mainForm.HIGHLIGHT_TEXT_COLOR.value       
        document.frm.transfer.value='y'
        document.frm.submit()
    }
}
</script>
</head>
<%  final String BGCOLOR=request.getParameter("BGCOLOR");
    final String TEXTCOLOR=request.getParameter("TEXTCOLOR");
    final String FONTFACE=request.getParameter("FONTFACE");
    final String HIGHLIGHT_TEXT_COLOR=request.getParameter("HIGHLIGHT_TEXT_COLOR");
    
    String transfer = (request.getParameter("transfer"));
    if(transfer==null) transfer="";
%>
<body bgcolor="<%=BGCOLOR%>" text="<%=TEXTCOLOR%>" topmargin="10" marginwidth="10" marginheight="10" <%=(transfer.equals("y")?"":"onLoad='fn()'")%>>
<table border="0" cellspacing="0" cellpadding="0" bgcolor="#cccccc" align="center">
  <tr>
    <td>
      <table width="100%" border="1" bordercolor="#000000" cellspacing="0" cellpadding="0" bgcolor="<%=BGCOLOR%>">
        <tr> 
          <td align="middle"><b class="contenttitle"><font color="<%=HIGHLIGHT_TEXT_COLOR%>" face="<%=FONTFACE%>" size="2">What is a Card
        Verification Number?</b></font>
           </td>
        </tr>
        <tr> 
          <td> 
            <p align="center"><img src="/newtemplates/images/visa.gif" width="163" height="121" 
           ><br>
              <br>
              <b><font face="<%=FONTFACE%>" size="2">Visa/MasterCard Requirements<br>
              Card Verification Number</font></b></p>
            <table border="0" cellspacing="0" cellpadding="4">
              <tr>
                <td><font face="<%=FONTFACE%>" size="2">The 3-digit, non-embossed number printed on the signature 
                  panel on the <b>back</b> of the card immediately following the 
                  credit card account number. This number is required as an additional 
                  security precaution.<br>
                  &nbsp; </font></td>
              </tr>
            </table>
		</td>
              </tr>
            </table>
      </td>
        </tr>
      </table>
<form name="frm" method="post">
<input type=hidden name="BGCOLOR" value=''>
<input type=hidden name="TEXTCOLOR" value=''>
<input type=hidden name="FONTFACE" value=''>
<input type=hidden name="HIGHLIGHT_TEXT_COLOR" value=''>
<input type=hidden name="transfer" value=''>
</form>      
</body></html>