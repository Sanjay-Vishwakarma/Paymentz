<%@ page import="java.util.*" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 9/10/14
  Time: 4:13 PM
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
    <link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
    <script src="/icici/olddatepicker1/jquery-1.9.1.js"></script>
    <script src="/icici/olddatepicker1/jquery-ui.js"></script>
    <link rel="stylesheet" href="/resources/demos/style.css">
    <title></title>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });
    </script>

</head>
<body>
<%  try
{  String memberid=nullToStr(request.getParameter("memberid"));
    Logger logger = new Logger("actionMemberFraudSystem.jsp");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<%--<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Member Fraud System
                <div style="float: right;">
                    <form action="/icici/manageMemberFraudSystem.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" name="submit" class="addnewmember" style="width:300px">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Member Fraud System Mapping
                        </button>
                    </form>
                </div>
            </div>

            <form action="/icici/servlet/ListMemberFraudSystem?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    &lt;%&ndash;<select name="memberid" class="txtbox" style="width: 172px;"><option value="" selected></option>
                                            <%
                                                Connection conn = null;
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                                                    PreparedStatement pstmt = conn.prepareStatement(query);
                                                    ResultSet rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getInt("memberid") + "\">" + rs.getInt("memberid") + " - " + rs.getString("company_name") + "</option>");
                                                    }
                                                }
                                                catch (Exception e) {
                                                    logger.debug("Exception::"+e);
                                                }
                                                finally {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>&ndash;%&gt;
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Fraud System</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select name="fsid" class="txtbox"><option value="" selected></option>
                                            <%
                                                Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                                                Iterator it1 = fraudSystem.entrySet().iterator();
                                                while (it1.hasNext())
                                                {
                                                    Map.Entry pair = (Map.Entry)it1.next();
                                                    out.println("<option value=\""+pair.getKey()+"\">"+pair.getKey()+" - "+pair.getValue()+"</option>");
                                                }

                                            %>
                                        </select>
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
</div>--%>
<%
    String error=(String ) request.getAttribute("errormessage");
    HashMap hash = (HashMap)request.getAttribute("chargedetails");
    String action=(String)request.getAttribute("action");
    if(hash!=null && !hash.isEmpty())
    {
%>

<div class="row" style="margin-inside: 0px">
    <div class="col-lg-12">
        <div class="panel panel-default" style="margin-inside: 0px">
            <div class="panel-heading" >
                Update Member Fraud System Mapping
            </div>
            <form action="/icici/servlet/ActionMemberFraudSystem?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="mappingid" value="<%=hash.get("id")%>">
                <input type="hidden" name="memberid" value="<%=hash.get("memberid")%>">
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
                                        <input type="text" size="30" class="txtbox" name="memberid" value="<%=(String)hash.get("memberid")%>" disabled>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Fraud System Name</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="fsid" class="txtbox"><%--<option value="" selected></option>--%>
                                            <%
                                                Hashtable<String,String> fraudSystem1 = FraudSystemService.getFraudSystem();
                                                Iterator it2 = fraudSystem1.entrySet().iterator();
                                                while (it2.hasNext())
                                                {
                                                    Map.Entry pair = (Map.Entry)it2.next();
                                            %>
                                            <option value="<%=pair.getKey()%>" <%if(pair.getKey().equals(hash.get("fsid"))){%>selected="" <%}%>><%=pair.getKey()+" - "+pair.getValue()%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is Online Fraud Check</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isonlinefraudcheck">
                                            <% if(hash.get("isonlinefraudcheck").equals("Y")){ %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <% }else{%>
                                            <option value="Y" >Y</option>
                                            <option value="N" selected>N</option>
                                            <%}%>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Fraud API User</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isapiuser">
                                            <% if(hash.get("isapiuser").equals("Y")){ %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <% }else{%>
                                            <option value="Y" >Y</option>
                                            <option value="N" selected> N</option>
                                            <%}%>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is Active</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isActive">
                                            <% if(hash.get("isActive").equals("Y")){ %>
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
                                    <td style="padding: 3px" class="textb">Is Test</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isTest">
                                            <% if(hash.get("isTest").equals("Y")){ %>
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
                out.println("<div class=\"reporttable\">");
                if(error !=null)
                {
                    out.println("<font class=\"textb\"><b>");
                    out.println(error);
                    out.println("</b></font>");
                }
                {
                    out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
                }
                out.println("</div>");
            }
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
    }
    catch (Exception e)
    {
        e.printStackTrace();
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