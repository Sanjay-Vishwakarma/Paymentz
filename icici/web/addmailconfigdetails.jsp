<%@ page import="servlets.MailUIUtils" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.TreeMap" %>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: NISHANT
  Date: 5/21/14
  Time: 12:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<html>
<head>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
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
            <br>
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
                                        <select size="1" name="event" class="txtbox" style="width:350px ">
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
                                        <select size="1" name="template" class="txtbox" style="width:220px" >
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
    String style="class=tr0";
%>
<form action="/icici/servlet/UpdateMailDetails?ctoken=<%=ctoken%>" method="POST" > <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <table align="center" width="65%" cellpadding="2" cellspacing="2">
        <tbody>
        <tr>
            <td>
                <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                    <tbody>
                    <tr><td class="textb" align="center" colspan="4"><h5><b>Add Event,Entity and Template Details</b></h5></td>
                    </tr>
                    <tr><td colspan=4>&nbsp;</td></tr>
                    <tr>
                        <td colspan="4">
                           <%
                               String error=(String)request.getAttribute("error");
                               if(error!=null)
                               {
                                   out.println("<center><b class=textb>"+error+"</b></center>");
                               }
                           %>

                        </td>
                    </tr>
                    <tr><td colspan=4>&nbsp;</td></tr>
                    <tr>
                        <td width="2%" class="textb">&nbsp;</td>
                        <td width="43%" class="textb">Mail / SMS Entity</td>
                        <td width="5%" class="textb">:</td>
                        <td width="50%" class="textb">
                            <input type="text" size="30" name="entity" class="txtbox" value="" >
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
                        <td class="textb">Mail / SMS Event</td>
                        <td class="textb">:</td>
                        <td>
                            <input type="text" size="50" name="event" class="txtbox" value="" >
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
                        <td class="textb">Mail TemplateName (case sensitive)  </td>
                        <td class="textb">:</td>
                        <td>
                            <input type="text" size="30"class="txtbox" name="template" value="" >
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
                        <td class="textb">SMS TemplateName (case sensitive)</td>
                        <td class="textb">:</td>
                        <td>
                            <input type="text" size="30" class="txtbox" name="smstemplate" value="">
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