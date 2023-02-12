<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="top.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 2/11/14
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>
</head>
<body>
<%
    PartnerFunctions partnerFunctions=new PartnerFunctions();
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Partner Profile Details
            </div>

            <form action="/partner/net/PartnerDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    String partnerId=request.getParameter("partnerId");
                    String partnerName=request.getParameter("partnerName");

                    String str="ctoken=" + ctoken;

                    if(partnerId!=null)str = str + "&partnerId=" + partnerId;
                    if(partnerId!=null)str = str + "&partnerName=" + partnerName;

                    int pageno=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                %>

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Partner Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="partnerId"  value="" class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Partner Name</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="30" type="text" name="partnerName"  value="" class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                    </td>

                                </tr>


                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>

                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

            </form>

        </div>
    </div>
</div>
<div class="reporttable">
    <%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

        Hashtable temphash=null;
        int records=0;
        int totalrecords=0;

        String errormsg=(String)request.getAttribute("message");

        if(errormsg!=null)
        {
            out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
        }

        String currentblock=request.getParameter("currentblock");

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
        if(hash!=null)
        {
            hash = (Hashtable)request.getAttribute("transdetails");
        }
        if(records>0)
        {
    %>
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">Partner Id</td>
            <td valign="middle" align="center" class="th0">Partner Name</td>
            <td valign="middle" align="center" class="th0">contact_persons</td>
            <td valign="middle" align="center" class="th0">contact_emails</td>
            <td valign="middle" align="center" class="th0">Address</td>
            <td valign="middle" align="center" class="th0">Country</td>
        <tr>
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

                temphash=(Hashtable)hash.get(id);
                out.println("<tr>");

                out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("partnerId"))+"<input type=\"hidden\" name=\"partnerId\" value=\""+temphash.get("partnerId")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerName"))+"<input type=\"hidden\" name=\"partnerName\" value=\""+temphash.get("partnerName")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"<input type=\"hidden\" name=\"contact_persons\" value=\""+temphash.get("contact_persons")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"contact_emails\" value=\""+temphash.get("contact_emails")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("address"))+"<input type=\"hidden\" name=\"address\" value=\""+temphash.get("address")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("country"))+"<input type=\"hidden\" name=\"country\" value=\""+temphash.get("country")+"\"></td>");

                out.println("</tr>");
            }
        %>
    </table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="ListPartnerDetails"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
        }


    %>

</div>
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