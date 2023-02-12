<%@ page import="java.util.*,
                 com.directi.pg.Admin"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />
</head>
<body>
<br><br><br>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();


    if (Admin.isLoggedIn(session))
    {
    %>

<%
	Hashtable hash=(Hashtable)request.getAttribute("podcomfirm");

	int hashsize=hash.size();
	//out.println(hash.toString());
    String errormsg = (String)request.getAttribute("cbmessage");
        if (errormsg != null && !errormsg.equals("null"))
        {
        
        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(errormsg);

        out.println("</font></td></tr></table>");
        }
if(hashsize>0)
{
%>
<center><font class=info>Capture Result</font></center>
<table align=center>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA" width=10 nowrap>Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA" width=70 nowrap>ICICI Transid</td>
	<td valign="middle" align="center" bgcolor="#008BBA" width=150 nowrap>Status</td>
</tr>
<%
	String style="class=td1";
	String ext="light";
		
	for(int pos=1;pos<=hashsize;pos++) 
	{
		String id=Integer.toString(pos);
		
				
			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}	

			String value=(String)hash.get(id);
			int pos1=value.indexOf("|");

			out.println("<tr>");
			out.println("<td "+style+">&nbsp;"+id+ "</td>");
			out.println("<td "+style+">&nbsp;" + value.substring(0,pos1) +"</a></td>");
			out.println("<td "+style+">&nbsp;" + value.substring(pos1+1) +"</a></td>");
			out.println("</tr>");
	}
%>
</table>

<%
}	
else
{
	out.println(ShowConfirmation("Sorry","No Transaction is Captured."));
}
%>

<%
    }
    else
{
    response.sendRedirect("/icici/logout.jsp");
    return;
}
%>

</body>
</html>