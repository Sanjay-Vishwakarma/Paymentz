<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title>Customer Support> Support Ext Master</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Customer Support Executive Detail's
                <div style="float: right;">
                    <form action="/icici/cseSignup.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Add New Customer Support" name="submit" class="addnewmember" style="width: 250px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Customer Support
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/SearchCSE?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%

                    String str="";

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),10);
                    String Eid=checkStringNull(request.getParameter("Eid"))==null?"":request.getParameter("Eid");
                    String Ename=checkStringNull(request.getParameter("Ename"))==null?"":request.getParameter("Ename");
                    str="Eid="+Eid+"&Ename="+Ename+"&ctoken="+ctoken+"";
                %>

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Executive Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="Eid"  value="" class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Executive Name</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="15" type="text" name="Ename" class="txtbox" value="">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" >
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
    <%
        String errormsg1 = (String) request.getAttribute("error");
        if (errormsg1 != null)
        {
            out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
        }

    %>
<%  Hashtable hash = (Hashtable)request.getAttribute("CSElist");
    /*System.out.println(" data :::"+hash);*/
    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;
    String mes=request.getParameter("MES");
//    String errormsg=(String)request.getAttribute("error");
    if(mes!=null)
    {
       Hashtable error=(Hashtable) request.getAttribute("error");

        if(error!=null && !error.isEmpty() )
        {

            Enumeration enu = error.keys();
            String key = "";
            String value = "";
            while (enu.hasMoreElements())
            {
                key = (String) enu.nextElement();
                value = (String) error.get(key);
                out.println("<center><li class=\"textb\"><b>"+value+"</b></font></li></center>");
            }
        }
    }
    if(request.getAttribute("update")!=null)
    {
        boolean update= Boolean.parseBoolean((request.getAttribute("update").toString()));
        if(update)
            out.println("<center><h4 class=\"textb\">Updated Successfully<h4></center>");
        else
            out.println("<center><h4 class=\"textb\">Not updated successfully <h4></center>");
    }
    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
        currentblock="1";

    try
    {
        records=Integer.parseInt(hash.get("records").toString());
        totalrecords=Integer.parseInt((hash.get("totalrecords").toString()));
    }
    catch(Exception ex)
    {

    }

    if(records>0)
    {
%>
<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr no</td>

            <td valign="middle" align="center" class="th0">Executive Id</td>
            <td valign="middle" align="center" class="th0">Executive Name</td>
            <td valign="middle" align="center" class="th0" colspan = "2">Action</td>

            </td>
        </tr>
    </thead>
    <%
        String style="class=td1";
        String ext="light";
         /*System.out.println("records::"+records);*/
        for(int pos=1;pos<=records;pos++)
        {

            int srno=pos+ ((pageno-1)*pagerecords);

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
            /*System.out.println("position :: "+pos);*/
            temphash=(Hashtable)hash.get(""+pos);
            /*System.out.println(" data 2:::"+temphash);*/
            out.println("<tr>");
            //out.println("<form action=\"/icici/servlet/EcoreInquiry\" method=\"POST\">");
            out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("csId"))+"</td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("csName"))+"</td>");
            //out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerName"))+"<input type=\"submit\" name=\"partnerName\" value=\"VIEW\"></td>");
            out.println("<td align=center "+style+"><form action=\"/icici/servlet/CSEviewupdate?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"csId\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("csId"))+"\"><input type=\"submit\" class=\"gotoauto\" name=\"submit\" value=\"View\"></form>");
            out.println("<td align=center "+style+"><form action=\"/icici/servlet/CSEviewupdate?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"csId\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("csId"))+"\"><input type=\"submit\" class=\"gotoauto\" name=\"submit\" value=\"Edit\"></form>");
            //out.println("<td "+style+"><a href=\"viewPartnerDetails.jsp?p_id="+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerId"))+"\">view</a></td>");
            //out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isinputrequired"))+"<input type=\"hidden\" name=\"isinputrequired\" value=\""+temphash.get("isinputrequired")+"\"></td>");
            //out.println("</form>");
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
            <jsp:param name="page" value="SearchCSE"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>
</tr>
</table>
<% }
else
{
    out.println(Functions.NewShowConfirmation("Sorry","No records found."));
}
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