<%@ page import="servlets.MailUIUtils" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.TreeMap" %>
<%@ include file="functions.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 2/23/2017
  Time: 5:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp" %>
<html>
<head>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>SMS Mapping</title>
</head>
<body>
<% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
<%
    MailUIUtils mailUIUtils = new MailUIUtils();
    Hashtable smsEvents = mailUIUtils.getMailEvents();
    Hashtable smsTemplate = mailUIUtils.getSMSTemplates();
    Hashtable smsEntity = mailUIUtils.getMailEntity();
    String eventId = (String) request.getAttribute("event");
    String templateId = (String) request.getAttribute("template");
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                SMS Mapping Details
                <div style="float: right;">
                    <form action="/icici/addmailconfigdetails.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" style="width:250px"
                                value="Add Event,Template & Entity" name="submit">
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
            <%
                String style = "class=tr0";
                String error = (String) request.getAttribute("error");
                if (error != null)
                {
                    out.println("<font class=\"textb\">");
                    out.println("<center>" + error + "</center></font>");
                }
            %>
            <form action="/icici/servlet/SmsMappingList?ctoken=<%=ctoken%>" method="post" name="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table align="center" width="90%" cellpadding="2" cellspacing="2" style="margin-left:17px;">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb">Mail / SMS Event</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="25%" class="textb">
                                        <select size="1" name="event" class="txtbox" style="width:350px ">
                                            <option value="all"> All</option>
                                            <%
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                TreeMap treeMap = new TreeMap(smsEvents);
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
                                            <option value="<%=key3%>" <%=selected3%> ><%=value3%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb">Mail / SMS Template</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="48%" class="textb">
                                        <select size="1" name="template" class="txtbox" style="width:220px">
                                            <option value="all"> All</option>
                                            <%
                                                String selected = "";
                                                String key = "";
                                                String value = "";
                                                TreeMap treeMap1 = new TreeMap(smsTemplate);
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
                                            <option value="<%=key%>" <%=selected%> ><%=value%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb"></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb"></td>
                                    <td width="8%" class="textb"></td>
                                    <td width="40%" class="textb"></td>

                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="22%" class="textb">

                                    </td>

                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" align="center">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb"></td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb"></td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb"></td>
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
    <form action="/icici/servlet/AddSmsMappingDetail?ctoken=<%=ctoken%>" method="POST">
        <input type="hidden" name="ctoken" value="<%=ctoken%>">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
            <tbody>
            <tr>
                <td>
                    <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                        <tbody>
                        <tr>
                            <td class="textb" align="center" colspan="4"><b>Add SMS Mapping</b></td>
                        </tr>
                        <tr>
                            <td colspan="4">&nbsp;</td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb">SMS Event</td>
                            <td width="5%" class="textb">:</td>
                            <td width="50%" class="textb">
                                <select class="txtbox" size="1" name="eventid" style="width: 340px;">
                                    <%
                                        String selected5 = "";
                                        String key5 = "";
                                        String value5 = "";
                                        TreeMap treeMap5 = new TreeMap(smsEvents);
                                        Iterator itr5 = treeMap5.keySet().iterator();
                                        while (itr5.hasNext())
                                        {
                                            key5 = (String) itr5.next();
                                            value5 = treeMap5.get(key5).toString();
                                    %>
                                    <option value="<%=key5%>"><%=value5%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb">SMS From Entity</td>
                            <td class="textb">:</td>
                            <td>
                                <select class="txtbox" size="1" name="fromentityid">
                                    <%
                                        String key7 = "";
                                        String value7 = "";
                                        TreeMap treeMap7 = new TreeMap(smsEntity);
                                        Iterator itr7 = treeMap7.keySet().iterator();
                                        while (itr7.hasNext())
                                        {
                                            key7 = (String) itr7.next();
                                            value7 = treeMap7.get(key7).toString();
                                            if ("Admin".equalsIgnoreCase(value7) || "Partner".equalsIgnoreCase(value7) || "Merchant".equalsIgnoreCase(value7))
                                            {
                                    %>
                                    <option value="<%=key7%>"><%=value7%>
                                    </option>
                                    <% }
                                    }
                                    %>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb">SMS TO Entity</td>
                            <td class="textb">:</td>
                            <td>
                                <select class="txtbox" size="1" name="toentityid">
                                    <%
                                        String key8 = "";
                                        String value8 = "";
                                        TreeMap treeMap8 = new TreeMap(smsEntity);
                                        Iterator itr8 = treeMap8.keySet().iterator();
                                        while (itr8.hasNext())
                                        {
                                            key8 = (String) itr8.next();
                                            value8 = treeMap8.get(key8).toString();
                                    %>
                                    <option value="<%=key8%>"><%=value8%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb">SMS Template</td>
                            <td class="textb">:</td>
                            <td>
                                <select class="txtbox" size="1" name="smstemplateid" style="width: 220px;">
                                    <option> </option>
                                    <%
                                        String selected6 = "";
                                        String key6 = "";
                                        String value6 = "";
                                        TreeMap treeMap6 = new TreeMap(smsTemplate);
                                        Iterator itr6 = treeMap6.keySet().iterator();
                                        while (itr6.hasNext())
                                        {
                                            key6 = (String) itr6.next();
                                            value6 = treeMap6.get(key6).toString();
                                    %>
                                    <option value="<%=key6%>"><%=value6%>
                                    </option>
                                    <%
                                        }
                                    %>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td class="textb">&nbsp;</td>
                            <td class="textb">SMS Subject</td>
                            <td class="textb">:</td>
                            <td>
                                <input type="text" size="40" name="smssubject" value="" class="txtboxsignup">
                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb">
                                <button type="submit" class="buttonform" name="submit" value="Save">
                                    <i class="fa fa-sign-in"></i>
                                    &nbsp;&nbsp;Save
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td width="2%" class="textb">&nbsp;</td>
                            <td width="43%" class="textb"></td>
                            <td width="5%" class="textb"></td>
                            <td width="50%" class="textb"></td>
                        </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            </tbody>
        </table>
    </form>
</div>
</body>
</html>