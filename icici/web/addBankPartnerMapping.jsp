<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.Functions"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("addBankPartnerMapping.jsp");
  Functions functions= new Functions();
%>
<html>
<head><title>Add Bank Partner Mapping</title>
  <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
  </head>
<body>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Add Bank Partner Mapping
        <div style="float: right;">
        <form action="/icici/gatewayInterface.jsp?ctoken=<%=ctoken%>" method="POST">
          <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
            <i class="fa fa-sign-in"></i>
            &nbsp;&nbsp;Gateway Master
          </button>
        </form>
      </div>
        <div style="float: right;">
          <form action="/icici/addGatewayType.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Gateway
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/addBankAgentMapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add Bank Agent Mapping" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add Bank Agent Mapping
            </button>
          </form>
        </div>
      </div>
      <%
        String daily_card_limit="1";
        String weekly_card_limit="3";
        String monthly_card_limit="5";
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (!com.directi.pg.Admin.isLoggedIn(session))
        {
          response.sendRedirect("/icici/logout.jsp");
          return;
        }
        ArrayList<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();
      %>
      <form name="addtype" action="/icici/servlet/AddBankPartnerMapping?ctoken=<%=ctoken%>" method="post" name="form1">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr>
                  <td colspan="4"></td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">PgType/Bank Name *</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <%
                    TreeMap <String, String> gatewayTreemap = new TreeMap<>();
                      for(GatewayType gatewayType : gatewayTypes)
                      {
                        gatewayTreemap.put(gatewayType.getName().substring(0,1).toUpperCase() + gatewayType.getName().substring(1) ,gatewayType.getPgTypeId());
                      }
                    %>
                    <select name="pgtypeid" class="txtbox">
                      <option value=""></option>
                      <%
                        for(Object key : gatewayTreemap.keySet())
                        {
                          String name1 = String.valueOf(key);
                          if(name1 != null)
                          {
                      %>
                      <option value="<%=gatewayTreemap.get(key)%>"><%=key%></option>
                         <%
                          }

                        }

                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Partner *</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px" class="textb">
                    <select name="partnerid" class="txtbox">
                      <option value=""></option>
                      <%
                        Connection conn=null;
                        try
                        {
                          conn=Database.getConnection();
                          String  query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                          PreparedStatement pstmt = conn.prepareStatement( query );
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("partnerId")+"\" >"+rs.getInt("partnerId")+" - "+rs.getString("partnerName")+"</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception::::::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Daily Card Limit</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px" class="textb">
                    <input class="txtbox" type="Text" value="<%=daily_card_limit%>" name="daily_card_limit" id="daily_card_limit" size="30">
                  </td>
                </tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Weekly Card Limit</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px" class="textb">
                    <input class="txtbox" type="Text"  value="<%=weekly_card_limit%>" name="weekly_card_limit" id="weekly_card_limit" size="30">
                  </td>
                </tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Monthly Card Limit</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px" class="textb">
                    <input class="txtbox" type="Text" value="<%=monthly_card_limit%>" name="monthly_card_limit" id="monthly_card_limit" size="30">
                  </td>
                </tr>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform" value="Save" style="width:200px ">
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
  if(functions.isValueNull((String)request.getAttribute("statusMsg")))
  {
    out.println("<div class=\"reporttable\">");
    out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
    out.println("</div>");
  }
%>
</body>
</html>