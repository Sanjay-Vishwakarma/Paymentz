<%@ page import="com.directi.pg.Admin, com.directi.pg.Functions"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<html>
<head>
<script language="javascript">
function confirmation()
{
	if(confirm("Do u really want to unblock this Country"))
		return true;
	return false;
}
</script>
</head>
<body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (Admin.isLoggedIn(session))
    {
    %>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Blocked Country

            </div>
            <%

                String errormsg = (String)request.getAttribute("cbmessage");
                if (errormsg == null)
                    errormsg = "";

                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\"><b>");

                out.println(errormsg);

                out.println("</b></font></td></tr></table>");
                out.println("<br>");


            %>
            <form action="/icici/servlet/BlockCountry?ctoken=<%=ctoken%>" method="post" name="F1" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table class="search" align=center border="0" cellpadding="2" cellspacing="0" style="width:80%">
                    <%  for(int i=1;i<=5;i++)
                    {%>
                    <tr>
                        <td align="center">Country&nbsp;&nbsp;&nbsp;<input maxlength="50"  class="txtboxsignup" name="country<%=i%>" size="22"></td>
                    </tr>
                    <%  }%>
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td class=search colspan=2 align=center>
                            <button type="submit" value="Block" class="buttonform">Block</button></td>
                    </tr>
                </table>
            </form>

        </div>
    </div>
</div>
<div class="reporttable">

    <%

        Hashtable countryHash=(Hashtable)request.getAttribute("blockedcountry");
        Hashtable temphash=null;

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        int records=0,totalrecords=0;

        String currentblock=ESAPI.encoder().encodeForHTML(request.getParameter("currentblock"));

        if(currentblock==null)
            currentblock="1";
        String str="";
        str = str + "?SRecords=" + pagerecords;
        str = str + "&ctoken=" + ctoken;
        try{
            records=Integer.parseInt((String)countryHash.get("records"));
            totalrecords=Integer.parseInt((String)countryHash.get("totalrecords"));
        }catch(Exception ex){}

        if(records>0){
    %>
    <center><font class="textb">Total records <%=totalrecords%></font></center>
    <table align=center style="width:90%" class="table table-striped table-bordered table-hover table-green dataTable" >
        <tr>
            <td  width="10%" align="center" class="th0">Sr. no.</td>
            <td  width="10%" align="center" class="th0">Country</td>
            <td  width="10%" align="center" class="th0" colspan=2>Action</td>
        </tr>
        <%
            String style="class=td1";
            String ext="light";

            for(int pos=1;pos<=records;pos++)
            {
                String id=Integer.toString(pos);

                int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

                if(pos%2==0){
                    style="class=td2";
                    ext="dark";
                }else{
                    style="class=td1";
                    ext="light";
                }

                temphash=(Hashtable)countryHash.get(id);

                String country=(String)temphash.get("country");
                String countryname=(String)temphash.get("countryname");

                out.println("<tr>");
                out.println("<td "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(countryname)+"</td>");
                out.println("<td "+style+" valign=\"middle\" >");
                out.println("<form action=UnblockCountry?ctoken="+ctoken+" method=post><input type =hidden  name=\"country\" value=\""+ESAPI.encoder().encodeForHTMLAttribute(country)+"\" ><input type =hidden  name=\"country\" value="+ctoken+"><input type=submit value=\"Unblock\" onclick= 'return confirmation();'></form>");
                out.println("</td>");
                out.println("</tr>");
            }
        %>
    </table>
    </form>
    <br>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true" >
                <jsp:param name="numrecords" value="<%=totalrecords%>" />
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="BlockedCountryList"/>
                <jsp:param name="currentblock" value="1"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
    <%
        }else{
            out.println(Functions.NewShowConfirmation("Sorry","No records found."));
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