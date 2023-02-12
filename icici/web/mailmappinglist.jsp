<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="servlets.MailUIUtils" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.TreeMap" %>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 5/20/14
  Time: 12:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<html>
<head>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title> Mail Mapping List</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
<%
    MailUIUtils mailUIUtils=new MailUIUtils();
    Hashtable mailEvents= mailUIUtils.getMailEvents();
    Hashtable mailTemplate = mailUIUtils.getMailTemplates();
    String eventId=(String)request.getAttribute("event");
    String templateId=(String)request.getAttribute("template");
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Mail Mapping Details
                <div style="float: right;">
                    <form action="/icici/addmailconfigdetails.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" style="width:250px"  value="Add Event,Template & Entity" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Event,Template & Entity
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addmailmapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add Mail Mapping" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Mail Mapping
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addsmsmapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add SMS Mapping" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add SMS Mapping
                        </button>
                    </form>
                </div>
            </div>
            <br><br>
            <form action="/icici/servlet/MailMappingList?ctoken=<%=ctoken%>" method="post" name="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" >
                <table  align="center" width="90%" cellpadding="2" cellspacing="2" style="margin-left:17;">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" >Mail Event</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="25%" class="textb">
                                        <select size="1" name="event" class="txtbox" style="width:350px">
                                            <option value="all"> All</option>
                                            <%
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                TreeMap treeMap = new TreeMap(mailEvents);
                                                Iterator itr = treeMap.keySet().iterator();
                                                while (itr.hasNext())
                                                {
                                                    key3 = (String) itr.next();
                                                    value3 = treeMap.get(key3).toString();
                                                    if (key3.equals(eventId))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";
                                            %>
                                            <option value="<%=key3%>" <%=selected3%> > <%=value3%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb">Mail Template</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="48%" class="textb">
                                        <select size="1" name="template" style="width:220px">
                                            <option value="all"> All</option>
                                            <%
                                                String selected = "";
                                                String key = "";
                                                String value = "";
                                                TreeMap treeMap1 = new TreeMap(mailTemplate);
                                                Iterator itr1 = treeMap1.keySet().iterator();
                                                while (itr1.hasNext())
                                                {
                                                    key = (String) itr1.next();
                                                    value = treeMap1.get(key).toString();
                                                    if (key.equals(templateId))
                                                        selected = "selected";
                                                    else
                                                        selected = "";
                                            %>
                                            <option value="<%=key%>" <%=selected%> > <%=value%></option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>

                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="22%" class="textb">

                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" ></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%"  align="center">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" ></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb" ></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>
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
    HashMap mappingList=(HashMap) request.getAttribute("mappingdetail");
    String error=(String)request.getAttribute("error");
    if(error!=null)
    {
        out.println("<font class=\"textb\">");
        out.println("<center>"+error+"</center></font>");
    }
    if(mappingList!=null)
    {
%>
<table align=center width="90%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
    <tr>
        <td valign="middle" align="center" class="th0" width="10%">Mapping ID</td>
        <td valign="middle" align="center" class="th0" width="20%">Event Name</td>
        <td valign="middle" align="center" class="th0" width="15%">Mail From Entity</td>
        <td valign="middle" align="center" class="th0" width="15%">Mail TO Entity</td>
        <td valign="middle" align="center" class="th0" width="15%">Template Name</td>
        <td valign="middle" align="center" class="th0" width="15%">Mail Subject</td>
        <td valign="middle" align="center" class="th0" width="15%">Delete&nbsp;Mapping</td>
    </tr>
    <%
        String mappingId = "";
        TreeMap mappingMap = new TreeMap(mappingList);

        Iterator itr2 = mappingMap.keySet().iterator();
        int pos=1;
        String style=null;
        while (itr2.hasNext())
        {
            if(pos%2==0)
            {
                style="class=tr0";

            }
            else
            {
                style="class=tr1";

            }
           /*String style="class=td1";*/
           mappingId = (String) itr2.next();
           HashMap innerhash=(HashMap) mappingMap.get(mappingId);
           out.println("<tr "+style+">");
            out.println("<form action=\"/icici/servlet/DeleteMailMapping?ctoken="+ctoken+"\" method=\"POST\">");
           out.println("<td align= center "+style+" >&nbsp;" +ESAPI.encoder().encodeForHTML((String) innerhash.get("mappingId"))+"</td>");
           out.println("<td align= center "+style+" >&nbsp;" +ESAPI.encoder().encodeForHTML((String) innerhash.get("mailEventId"))+"</td>");
           out.println("<td align= center "+style+" >&nbsp;" +ESAPI.encoder().encodeForHTML((String) innerhash.get("mailFromEntityId"))+"</td>");
           out.println("<td align= center "+style+" >&nbsp;" +ESAPI.encoder().encodeForHTML((String) innerhash.get("mailToEntityId"))+"</td>");
           out.println("<td align= center "+style+" >&nbsp;" +ESAPI.encoder().encodeForHTML((String) innerhash.get("mailTemplateId"))+"</td>");
           out.println("<td align= center "+style+" >&nbsp;" +ESAPI.encoder().encodeForHTML((String) innerhash.get("mailSubject"))+"</td>");
           out.println("<td align= center "+style+" ><input type=\"hidden\" name=\"mappingid\" value=\""+ESAPI.encoder().encodeForHTML((String) innerhash.get("mappingId"))+"\"><input type=\"submit\" class=\"gotopage\" value=\"Delete\"></td>");
            out.println("</form>");
           out.println("</tr>");
            pos++;
        }
        %>
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