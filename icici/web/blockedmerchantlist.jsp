<%@ page import="java.util.*"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.*" %>
<%@ include file="index.jsp"%>
<html>
<head>
<script language="javascript">

function confirmation()
{

	if(confirm("Do u really want to unlock this Merchant account"))
	{
		return true;
	}
	return false;
}
</script>
</head>
<title>Account History> Unblock Merchant Account</title>
<body class="bodybackground">
<br><br><br><br><br><br><br><br>
<div class="qwipitab" style="margin-top: 0px">

<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
<h3 align="center"><p class="textb">Blocked Merchant Account List</p> </h3>


<% Hashtable merchanthash=null;
  if(request.getAttribute("blockedmerchant")!=null)
  {
    merchanthash=(Hashtable)request.getAttribute("blockedmerchant");
  }
    Hashtable temphash=null;
	//int emailhashsize=merchanthash.size();
    String str="";
	int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
	int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
    str = str + "SRecords=" + pagerecords;
     str = str + "&ctoken="+ctoken;
	int records=0;
	int totalrecords=0;

	String currentblock=request.getParameter("currentblock");

	if(currentblock==null)
	currentblock="1";


	try
	{
		records=Integer.parseInt((String)merchanthash.get("records"));
		totalrecords=Integer.parseInt((String)merchanthash.get("totalrecords"));
	}
	catch(Exception ex)
	{
	}

        if(request.getAttribute("msg")!=null)
        {
            out.println(Functions.NewShowConfirmation("Successfull",request.getAttribute("msg").toString()));
            out.println("<br>");
        }

  if(records>0)
  {
%>

<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">Merchant ID</td>
            <td valign="middle" align="center" class="th0" >Login name</td>
            <td valign="middle" align="center" class="th0" >Contect email</td>
            <td valign="middle" align="center" class="th0" >Action</td>
        </tr>
    </thead>
<%
	String style="class=td1";
	String ext="light";

	for(int pos=1;pos<=records;pos++)
	{
		String id=Integer.toString(pos);

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=tr0";
				ext="dark";
			}
			else
			{
				style="class=tr1";
				ext="light";
			}

		temphash=(Hashtable)merchanthash.get(id);



			out.println("<tr>");
			out.println("<td "+style+">&nbsp;"+srno+ "</td>");
			out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"</td>");
            out.println("<td align=\"center\" "+style+" valign=\"middle\" >");
			out.println("<form action=UnBlockedAccount?ctoken"+ctoken+" method=post><input type =hidden  name=login value=\""+ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("login"))+"\" ><input type =hidden  name=ctoken value="+ctoken+"><input type=submit value=\"Unlock\" class=\"goto\" onclick= 'return confirmation();'></form>");
			out.println("</td>");
			out.println("</tr>");
	}
%>
</table>

<br>
<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value="<%=str%>"/>
	<jsp:param name="page" value="BlockedMerchantList"/>
	<jsp:param name="currentblock" value="1"/>
	<jsp:param name="orderby" value=""/>
</jsp:include>
</td>
</tr>
</table>
<%
}
else
{
	out.println(Functions.NewShowConfirmation("Sorry", "Not found Blocked merchant account"));
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
</div>
</body>
</html>