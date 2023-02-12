<%@ page import="java.util.*,
                 org.owasp.esapi.ESAPI,
                 com.directi.pg.Merchants"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
<script type="text/javascript" language="javascript">

function validateccnum()
{
    var firstfourofccnum = document.F1.firstfourofccnum.value;
    var lastfourofccnum= document.F1.lastfourofccnum.value;
    if(firstfourofccnum.length==0 && lastfourofccnum.length==0 )
        return true;
    if(firstfourofccnum.length<4)
    {

     alert("Enter first four of ccnum");
        return false;
    }

  if( lastfourofccnum.length<4)
    {
        alert("Enter last four of ccnum");
        return false;
    }

}
</script>

</head>
<body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Merchants merchants = new Merchants();
    if (merchants.isLoggedIn(session))
    {
    %>
<h2>Admin Proof Received Interface</h2>
<br><br><br>
<%

 Hashtable hash=(Hashtable)request.getAttribute("transdetails");
 Hashtable temphash=null;
 //int pageno=1;
 //int records=30;
 //out.println(hash);
 int hashsize=hash.size();
  String str="";
 int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
 int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
 str = str + "SRecords=" + pagerecords;
     str = str + "&ctoken="+ctoken;
 int records=0;
 int totalrecords=0;

 String currentblock=ESAPI.encoder().encodeForHTML(request.getParameter("currentblock"));

 if(currentblock==null)
 currentblock="1";


 try
 {
  records=Integer.parseInt((String)hash.get("records"));
  totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
 }
 catch(Exception ex)
 {
 }

if(records>0)
{
%>



<font class="info">Total records <%=totalrecords%></font>
<table align=center>
<tr>
 <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
 <td valign="middle" align="center" bgcolor="#008BBA">ICICI Transid</td>
 <td valign="middle" align="center" bgcolor="#008BBA">S.T(Authid)</td>
 <td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>
 <td valign="middle" align="center" bgcolor="#008BBA">Description</td>
 <td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
 <td valign="middle" align="center" bgcolor="#008BBA">CCNum</td>
 <td valign="middle" align="center" bgcolor="#008BBA" colspan=2>Action</td>
</tr>
<%
 String style="class=td1";
 String ext="light";

 for(int pos=1;pos<=records;pos++)
 {
  String id=Integer.toString(pos);

  int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

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

  temphash=(Hashtable)hash.get(id);

  String tempcurrentstatus=(String)temphash.get("currentstatus");
  String tempprovstatus=(String)temphash.get("provstatus");
  String productname=(String)temphash.get("productkey");
  String path=productname+"/";

   out.println("<tr>");
   out.println("<td "+style+">&nbsp;"+srno+ "</td>");
   out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("icicitransid"))+"</a></td>");
   out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("authid"))+"</td>");
   out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"</td>");
   out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"</td>");
   out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
  out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("ccnum"))+"</td>");
   //out.println("<td "+style+">&nbsp;<input type=text name=" +(String)temphash.get("icicitransid")+"></td>");
   String status=(String)temphash.get("status");

   out.println("<td "+style+">");

   if(status.equals("proofrequired"))
   {
   //out.println("&nbsp;<a href=AdminDoReverseTransaction?icicitransid="+ temphash.get("icicitransid") +" onclick='return confirm(\"Do you really want to reverse this transaction.\")' >Reverse</a>");
   out.println("<form action=AdminDoProofReceived?ctoken="+ctoken+" method=post><input type =hidden  name=icicitransid value="+ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icicitransid")) +"><input type =hidden  name=ctoken value="+ctoken+"><input type=submit value=\"Proof Received\"></form>");
   }
   else
   out.println("&nbsp;");

   out.println("</td>");
   out.println("</tr>");
 }
%>
</table>


<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
 <jsp:param name="numrecords" value="<%=totalrecords%>" />
  <jsp:param name="numrows" value="<%=pagerecords%>"/>
 <jsp:param name="pageno" value="<%=pageno%>"/>
 <jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
 <jsp:param name="page" value="AdminProofrequiredList"/>
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
 out.println(ShowConfirmation("Sorry","No records found."));
}
    temphash=null;
%>
<form action="/icici/servlet/AdminProofrequiredList?ctoken=<%=ctoken%>" method="post" name="F1" onsubmit="return validateccnum()">
<%  String errormsg = (String)request.getAttribute("cbmessage");
        if (errormsg == null)
        {
        errormsg = "";
        }
        else
        {
        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(errormsg);

        out.println("</font></td></tr></table>");
        }
%>
 <table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="60%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
  <tr>
  <td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
  </tr>
  <tr>
   <td class=search width="20%" align="right">Tracking Id</td>
   <td><input name="STrakingid" maxlength="10"  size="10"></td>
  </tr>
  <tr>
   <td class=search width="20%" align="right">CCNum</td>
   <td>
            <input  name="firstfourofccnum" type="Text" maxlength="6" size="5"   >
            **** ****
            <input name="lastfourofccnum" type="Text" maxlength="4"  size="3"  ><br>(Enter First six & Last four digit of Credit Card) </td>
  </tr>
  <!--
  <tr>
        <input type="hidden" name="firstfourofccnum">
            <input type="hidden" name="lastfourofccnum">
   <td class=search width="20%" align="right">Credicard Number</td>
   <td><input name="SCc" size="10"></td>
  </tr>
  -->
  <tr>
   <td class=search colspan=2 align=center>
   <input type="submit" value="Search"></td>
  </tr>
 </table>
</form>
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