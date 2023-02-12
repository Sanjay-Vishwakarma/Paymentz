<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 9/9/14
  Time: 5:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <link rel="stylesheet" href="/resources/demos/style.css">
    <title></title>
</head>
<body>
<%  String memberid=nullToStr(request.getParameter("memberid"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Logger logger=new Logger("manageMemberFraudSystem.jsp");
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Add New Member Fraud System Mapping
                <div style="float: right;">
                    <form action="/icici/listMemberFraudSystem.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in" ></i>
                            &nbsp;&nbsp;Member Fraud System
                        </button>
                    </form>
                </div>
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
            <form action="/icici/servlet/ManageMemberFraudSystem?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <input type="hidden" value="true" name="isSubmitted">
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
                                    <td style="padding: 3px" width="43%" class="textb">Member Id</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                      <%--  <select name="memberid"><option value="" selected></option>
                                            <%
                                                Connection conn=null;
                                                try
                                                {
                                                    conn= Database.getConnection();
                                                    String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                                                    PreparedStatement pstmt = conn.prepareStatement( query );
                                                    ResultSet rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
                                                    }
                                                }
                                                catch (SystemError se)
                                                {
                                                    logger.error("Exception:::::"+se);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Fraud System</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><select name="fsid"><option value="" selected>Select Fraud System</option>
                                        <%
                                            Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                                            Iterator it1 = fraudSystem.entrySet().iterator();
                                            while (it1.hasNext())
                                            {
                                                Map.Entry pair = (Map.Entry)it1.next();
                                                out.println("<option value=\""+pair.getKey()+"\">"+pair.getKey()+" - "+pair.getValue()+"</option>");
                                            }
                                        %>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Online Fraud Check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><select name="isonlinefraudcheck">
                                        <option value="Y">Y</option>
                                        <option value="N" selected="">N</option>
                                    </select></td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is API Call Supported</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"> <select name="isfraudapiuser">
                                        <option value="Y">Y</option>
                                        <option value="N" selected="">N</option>
                                    </select> </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is Active</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isActive">
                                            <option value="Y">Y</option>
                                            <option value="N" selected="">N</option>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">IsTest</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isTest">
                                            <option value="Y">Y</option>
                                            <option value="N" selected="">N</option>
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
                                            &nbsp;&nbsp;Save
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
        if(request.getAttribute("message")!=null)
        {
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Result", (String) request.getAttribute("message")));
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
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>