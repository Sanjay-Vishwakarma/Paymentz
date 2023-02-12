<%@ page import="com.directi.pg.Admin,
                 com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp" %>
<html>
<head>
<script language="javascript">

function confirmation()
{

	if(confirm("Do u really want to unblock this IP"))
	{
		return true;
	}
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
                Block IP

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
            <form action="/icici/servlet/BlockIP?ctoken=<%=ctoken%>" method="post" name="F1" >
                <table class="search" align=center border="0" cellpadding="2" cellspacing="0" style="width:80%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
                    <%
                        for(int i=1;i<=5;i++)
                        {
                    %>
                    <tr>
                        <td  align="center">Start IP&nbsp;&nbsp;&nbsp; <input name="startip<%=i%>" class="txtboxsignup" size="22"></td><TD  align="center">End IP&nbsp;&nbsp;&nbsp; <input class="txtboxsignup" name="endip<%=i%>" size="22"></td>
                    </tr>
                    <%
                        }
                    %>
                    <tr>
                        <td class=search colspan=2 align=center>
                            <button type="submit" value="Block" class="buttonform">Block</button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable">

    <%

        Hashtable iphash=(Hashtable)request.getAttribute("blockedip");
        Hashtable temphash=null;
        int iphashsize=iphash.size();

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        int records=0;
        int totalrecords=0;

        String currentblock=ESAPI.encoder().encodeForHTML(request.getParameter("currentblock"));

        if(currentblock==null)
            currentblock="1";

        String str="";
        str = str + "?SRecords=" + pagerecords;
        str = str + "&ctoken=" + ctoken;
        try
        {
            records=Integer.parseInt((String)iphash.get("records"));
            totalrecords=Integer.parseInt((String)iphash.get("totalrecords"));
        }
        catch(Exception ex)
        {
        }

        if(records>0)
        {
    %>
    <center><font class="textb"><b>Total records <%=totalrecords%></b></font></center>
    <table align=center style="width:90%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td width="5%" align="center" class="th0">Sr no</td>
            <td width="10%" align="center" class="th0">Start Ip Address</td>
            <td width="10%" align="center" class="th0">End Ip Address</td>
            <td width="5%" align="center" class="th0" colspan=2>Action</td>
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

                temphash=(Hashtable)iphash.get(id);

                String startipaddr=(String)temphash.get("startipaddress");
                String endipaddr=(String)temphash.get("endipaddress");
                String startipcode=(String)temphash.get("startipcode");
                String endipcode=(String)temphash.get("endipcode");

                out.println("<tr>");
                out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(startipaddr)+"</td>");
                out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(endipaddr)+"</td>");
                out.println("<td align=center "+style+" valign=\"middle\" >");
                out.println("<form action=UnblockIP?ctoken="+ctoken+" method=post><input type =hidden  name=startipcode value=\""+ESAPI.encoder().encodeForHTMLAttribute(startipcode)+"\" ><input type =hidden  name=ctoken value="+ctoken+"><input type =hidden  name=endipcode value=\""+ESAPI.encoder().encodeForHTMLAttribute(endipcode)+"\" ><input type=submit value=\"Unblock\" class=\"gotoauto\" onclick= 'return confirmation();'></form>");
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
                <jsp:param name="page" value="BlockedIPList"/>
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