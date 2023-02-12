<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.ArrayList" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: nikita
  Date: 03/09/2015
  Time: 12:52 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
    Logger logger=new Logger("addBankAgentMapping.jsp");
    Functions functions= new Functions();
%>
<html>
<head>
    <title>Add Bank Agent Mapping</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
    ArrayList<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();
    Functions functions=new Functions();
    String agentId=Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
    String pgtypeid=Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Bank Agent Mapping
                <div style="float: right;">
                    <form action="/icici/addGatewayType.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Gateway
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addBankPartnerMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add Bank Partner Mapping" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Partner Mapping
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/gatewayInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Gateway Master
                        </button>
                    </form>
                </div>
    </div>
    <form name="addtype" action="/icici/servlet/AddBankAgentMapping?ctoken=<%=ctoken%>" method="post" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
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
                            <td style="padding: 3px" width="43%" class="textb">Gateway Type *</td>
                            <td style="padding: 3px" width="5%" class="textb">:</td>
                            <td style="padding: 3px" width="50%" class="textb">
                                <input name="pgtypeid" id="pgtypeid" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">


                            <%--<select name="pgtypeid" class="txtbox"></option>
                                    <option value=""></option>
                                    <%
                                        for(GatewayType gatewayType : gatewayTypes)
                                        {
                                            String name1 = gatewayType.getName();
                                    %>
                                    <option value="<%=gatewayType.getPgTypeId()%>"><%=name1%></option>
                                    <%
                                        }
                                    %>
                                </select>--%>

                            </td>

                        </tr>

                        <tr>
                            <td style="padding: 3px" class="textb">&nbsp;</td>
                            <td style="padding: 3px" class="textb">Agent*</td>
                            <td style="padding: 3px" class="textb">:</td>
                            <td style="padding: 3px" class="textb">
                                <input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on">
                               <%-- <select name="agentid" class="txtbox"></option>
                                <option value=""></option>
                                <%
                                    Connection conn=null;
                                    try
                                    {
                                        conn = Database.getConnection();
                                        String query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
                                        PreparedStatement pstmt = conn.prepareStatement(query);
                                        ResultSet rs = pstmt.executeQuery();
                                        while (rs.next())
                                        {
                                            out.println("<option value=\"" + rs.getInt("agentId") + "\">" + rs.getInt("agentId") + " - " + rs.getString("agentName") + "</option>");
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        logger.error("Exception:::"+e);
                                    }
                                    finally
                                    {
                                        Database.closeConnection(conn);
                                    }
                                %>
                            </select>--%>
                            </td>
                        </tr>
                        <tr><td colspan="4">&nbsp;</td></tr>
                        <tr>
                            <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                            <td style="padding: 3px" width="43%" class="textb"></td>
                            <td style="padding: 3px" width="5%" class="textb"></td>
                            <td style="padding: 3px" width="50%" class="textb">
                                <button type="submit" class="buttonform" value="Save"  style="width:200px ">
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
    if(functions.isValueNull((String) request.getAttribute("statusMsg")))
    {
        out.println("<div class=\"reporttable\">");
        out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
        out.println("</div>");
    }
%>
</div>
</body>
</html>