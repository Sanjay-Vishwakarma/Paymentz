<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
<script language="javascript">

function confirmation()
{

	if(confirm("Do u really want to unblock this email"))
	{
		return true;
	}
	return false;
}
</script>
</head>
<body>

<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Block Email
            </div>
            <%

                    String errormsg = (String)request.getAttribute("cbmessage");
                    if (errormsg == null)
                    errormsg = "";
                    out.println("<br>");
                    out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\"><b>");

                    out.println(errormsg);

                    out.println("</b></font></td></tr></table>");
                    out.println("<br>");

              %>
            <form action="/icici/servlet/BlockEmail?ctoken=<%=ctoken%>" method="post" name="F1" >
                <table class="search" align=center border="0" cellpadding="2" cellspacing="0" style="width:80%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
                    <%
                        for(int i=1;i<=8;i++)
                        {
                    %>
                    <tr>
                        <td align="center"><%=i%>&nbsp;&nbsp;&nbsp;<input maxlength="50"  class="txtboxsignup" name="email<%=i++%>" size="22"></td><TD align="center"><%=i%>&nbsp;&nbsp;&nbsp;<input maxlength="50" class="txtboxsignup" name="email<%=i%>" size="22"></td>
                    </tr>
                    <%
                        }
                    %>
                    <tr>
                        <td class=search colspan=2 align=center>
                            <button type="submit" value="Block" class="buttonform">Block</button></td>
                    </tr>
                </table>
            </form>
            <%
                }
                else
                {
                    session.invalidate();
                }
            %>

</div> </div></div>
<div class="reporttable">
    <%

        Hashtable emailhash=(Hashtable)request.getAttribute("blockedemail");
        Hashtable temphash=null;
        int emailhashsize=emailhash.size();

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        int records=0;
        int totalrecords=0;

        String currentblock=request.getParameter("currentblock");

        if(currentblock==null)
            currentblock="1";

        String str="";
        str = str + "?SRecords=" + pagerecords;
        str = str + "&ctoken=" + ctoken;
        try
        {
            records=Integer.parseInt((String)emailhash.get("records"));
            totalrecords=Integer.parseInt((String)emailhash.get("totalrecords"));
        }
        catch(Exception ex)
        {
        }

        if(records>0)
        {
    %>
    <center><font class="textb"><b>Total records <%=totalrecords%></b></font></center>
    <br>
    <table align=center style="width:90%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
            <tr>
                <td  width="10%" align="center" class="th0">Sr no</td>
                <td  width="5%" align="center" class="th0">EmailAddress</td>
                <td  width="10%" align="center" class="th0" colspan=2>Action</td>
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

                temphash=(Hashtable)emailhash.get(id);

                String emailaddr=(String)temphash.get("emailaddr");

                out.println("<tr>");
                out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(emailaddr)+"</td>");
                out.println("<td align=center "+style+" valign=\"middle\" >");
                out.println("<form action=UnblockEmail?ctoken="+ctoken+" method=post><input type =hidden  name=emailaddr value=\""+ESAPI.encoder().encodeForHTMLAttribute(emailaddr)+"\" ><input type =hidden  name=ctoken value="+ctoken+"><input type=submit value=\"Unblock\" class=\"gotoauto\" onclick= 'return confirmation();'></form>");
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
                <jsp:param name="page" value="BlockedEmailList"/>
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
            out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
        }
    %>
</div>
</body>
</html>