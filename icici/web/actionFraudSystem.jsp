<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 9/12/14
  Time: 3:38 PM
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
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

            <%--<div class="panel-heading" >
                Fraud System Master
                <div style="float: right;">
                    <form action="/icici/addNewFraudSystem.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Add New Fraud System" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Fraud System
                        </button>
                    </form>
                </div>
            </div>--%>

    <%--        <form action="/icici/servlet/ListFraudSystem?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    String str="ctoken=" + ctoken;
                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                %>

               &lt;%&ndash; <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">

                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Fraud System ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  maxlength="15" type="text" name="fsid"  value=""  class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Fraud System Name</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  maxlength="15" type="text" name="fsname"  value="" class="txtbox">
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
&ndash;%&gt;            </form>
--%>
<div class="row" style="margin-inside: 0px">
    <div class="col-lg-12">
        <div class="panel panel-default" style="margin-inside: 0px">
            <div class="panel-heading" >
                Update Fraud System Details
            </div>
            <%
                List errorList=(List)request.getAttribute("errorList");
                if(errorList!=null)
                {
                    out.println("<table align=\"center\" font class=\"textb\"><tr><td><b>");
                    out.println(errorList);
                    out.println("</b></font></td></tr></table>");
                }
            %>
            <%
                String error=(String ) request.getAttribute("errormessage");
                if(error !=null)
                {
                    out.println("<b>");
                    out.println(error);
                    out.println("</b>");
                }
                HashMap hash = (HashMap)request.getAttribute("chargedetails");
                String action=(String)request.getAttribute("action");
                if(hash!=null)
                {
                    String style="class=tr0";
            %>

            <form action="/icici/servlet/ActionFraudSystem?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="mappingid" value="<%=hash.get("fsid")%>">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">

                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">Fraud System Id</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <input type="text" size="30" name="fsid" class="txtbox" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("fsid"))%>" disabled="true">
                                    </td>

                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Fraud System Name</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input type="text" class="txtbox" size="30" name="fsname" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("fsname"))%>" disabled="true"></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Contact Person</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input type="text" size="30" class="txtbox" name="contactperson" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("contactperson"))%>"></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Contact Email</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input type="text" size="30" class="txtbox" name="contactemail" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("contactemail"))%>"> </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Website Url</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <input type="text" size="30" name="weburl" class="txtbox" value="<%=ESAPI.encoder().encodeForHTML((String)hash.get("weburl"))%>">
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Offline Fraud Check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="offline">
                                            <% if(hash.get("offline").equals("Y")){
                                            %>
                                            <option value="Y" selected> Y</option>
                                            <option value="N"> N</option>
                                            <%}else{%>
                                            <option value="Y" > Y</option>
                                            <option value="N" selected> N</option>
                                            <%}%>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Online Fraud Check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="online">
                                            <% if(hash.get("online").equals("Y")){ %>
                                            <option value="Y" selected> Y</option>
                                            <option value="N"> N</option>
                                            <% }else{%>
                                            <option value="Y" > Y</option>
                                            <option value="N" selected> N</option>
                                            <%}%>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is API Call Supported</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="apicall">
                                            <% if(hash.get("api").equals("Y")){ %>
                                            <option value="Y" selected> Y</option>
                                            <option value="N"> N</option>
                                            <% }else{%>
                                            <option value="Y" > Y</option>
                                            <option value="N" selected> N</option>
                                            <%}%>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb"></td>
                                    <td style="padding: 3px" width="5%" class="textb"></td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <button type="submit" class="buttonform">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Update
                                        </button>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>


<%

        }
        else
        {
            out.println("<div class=\"reportable\">");
            out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
            out.println("</div>");
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>

