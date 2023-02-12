	<%@ page import="java.util.*,com.directi.pg.*"%>
<%@ include file="functions.jsp"%>	
<html>
<title>FIRC Batch Updation</title>
<head>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />
<script language="javascript">

function confirmbatch()
{
	//alert(podbatch)

	if(confirm("Do u really want to confirm the batch"))
	{
		document.f1.mybutton.disabled=true;
		return true;
		
	}
	return false;
}
</script>
</head>

<body>
<br>
<center>
<h1>FIRC</h1>
</center>
<hr>
<b><i>Please upload the file with the .firc extension. Please check the file for the right format before uploading it. Once selected please confirm the FIRC list.</b></i>
<form name="firc" action="/icici/servlet/FIRC" method=post onSubmit='return confirmbatch()' ENCTYPE="multipart/form-data">

<br><br>
<table align="center" width="40%">
	<tr>
		<td width="50%" align="center"><b>FIRC file:</b></td>
		<td width="50%" align="center"><input name="firc" type="file" value="Select Raw File"></td>
	</tr>
	<tr>
		<td colspan="2" align="center"><hr></td>
	</tr>
	<tr>
		<td colspan="2" align="center"><input name=mybutton type="submit" value="Upload and Confirm batch"></td>
	</tr>
</table>

</form>
</body>
</html>