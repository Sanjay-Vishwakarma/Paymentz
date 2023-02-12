<%@ page import="org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="top.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: mukesh
  Date: 2/03/14
  Time: 11:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private static Logger log=new Logger("viewGatewayAccountDetails.jsp");
%>
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
<%!
     String selected;
    HashMap<String,String> dropdown= new HashMap<String, String>();
%>
<%

    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    dropdown.put("1","Y");
    dropdown.put("0","N");
%>
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default" >
<div class="panel-heading" >
    Bank Accounts Details
    <div style="float: right;">
        <form action="/partner/gatewayAccountInterface.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="button" value="Bank Accounts" name="submit" style="width: 250px; font-size:14px">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Bank Accounts
            </button>
        </form>
    </div>

</div>
<br>
    <form action="/partner/net/editGatewayAccountsDetails?ctoken=<%=ctoken%>" method="POST"> <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <table border="1" align="center" style="width:50%" class="table table-striped table-bordered table-green dataTable">


        <%
            String message=(String)request.getAttribute("message");
            String accountid = (String) request.getAttribute("accountid");
            String action = (String) request.getAttribute("action");

            if(message!=null)
            {
                out.println("<center><font class=\"textb\"><b>"+message+"</b></font></center>");
            }

            Hashtable hash = (Hashtable) request.getAttribute("gatewayaccountdetails");
            String isreadonly =(String) request.getAttribute("isreadonly");
            String table_data =(String) request.getAttribute("table_data");
            String columnName = (String) request.getAttribute("table_column");
            String conf = " ";
            String gateway_table_name = (String)request.getAttribute("gateway_table_name");
            Hashtable innerhash = new Hashtable();
            Hashtable temphash=null;
            int records=0;
            if(isreadonly.equalsIgnoreCase("view"))
            {
                conf = "disabled";
            }
            if (hash != null && hash.size() > 0)
            {
                String style="class=tr0";
                innerhash = (Hashtable) hash.get(1 + "");
        %>

        <input type="hidden" name="columnnames" value=<%=columnName%>>
        <input type="hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("accountid"))%>">
        <input type="hidden" name="merchantid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("merchantid"))%>">
        <input type="hidden" name="pgtypeid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("pgtypeid"))%>">
        <input type="hidden" name="gateway_table_name" value="<%=ESAPI.encoder().encodeForHTML(gateway_table_name)%>">

        <tr <%=style%>>
            <td class="th0" colspan="2">GatewayAccount Details</td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">AccountType Id: </td>
            <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("accountid"))%>" <%=conf%>></td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">MerchantId: </td>
            <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("merchantid"))%>" <%=conf%>></td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Pgtypeid: </td>
            <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("pgtypeid"))%>" <%=conf%>></td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Alias Name: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="aliasname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("aliasname"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Display Name: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="displayname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("displayname"))%>" <%=conf%>> </td>
            <input type="hidden" name="0ld_displayname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("displayname"))%>" <%=conf%>>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Is MasterCardSupported: </td>
            <td class="tr1">

                <select type="text" name="ismastercardsupported" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("ismastercardsupported"))%>" <%=conf%>>
            <%
                for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                {
                    selected="";
                    if(yesNoPair.getKey().equals((String) innerhash.get("ismastercardsupported")))
                    {
                        selected="selected";
                    }
                    out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                }
            %>
             </select>
            </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Short Name: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="shortname" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("shortname"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Site: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="site" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("site"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Path: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="path" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("path"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Username: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("username"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Password: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="passwd" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("passwd"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">ChargeBack Path: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="chargeback_path" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("chargeback_path"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Is Cvv Required: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="isCVVrequired" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isCVVrequired"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Monthly Card Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="monthly_card_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Daily Amount Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="daily_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_amount_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Monthly Amount Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="monthly_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_amount_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Weekly Amount Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="weekly_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_amount_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Daily Card Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="daily_card_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Weekly Card Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="weekly_card_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Min Transaction Amount: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="min_transaction_amount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("min_transaction_amount"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Max Transaction Amount: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="max_transaction_amount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("max_transaction_amount"))%>" <%=conf%>> </td>
        </tr>

        <tr <%=style%>>
            <td class="tr1">Daily Card Amount Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="daily_card_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("daily_card_amount_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Weekly Card Amount Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="weekly_card_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("weekly_card_amount_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Monthly Card Amount Limit: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="monthly_card_amount_limit" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("monthly_card_amount_limit"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Is Test Account: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="istest" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("istest"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Is Active Account: </td>
            <td class="tr1"><input type="text" class="txtbox1" name="isactive" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("isactive"))%>" <%=conf%>> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Partner : </td>
            <td class="tr1">
                <select name="partnerid" class="txtbox" <%=conf%> ></option>
                    <option value=""></option>
                    <%
                        Connection conn = Database.getConnection();
                        String query = "SELECT partnerid,partnerName FROM partners WHERE activation='T' ORDER BY partnerid ASC";
                        try
                        {
                        PreparedStatement pstmt = conn.prepareStatement( query );
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                            selected="";
                            if(rs.getString("partnerid").equalsIgnoreCase((String) innerhash.get("partnerid")))
                            {
                                selected="selected";
                            }
                            out.println("<option value=\""+rs.getInt("partnerid")+"\" "+selected+">"+rs.getInt("partnerid")+" - "+rs.getString("partnerName")+"</option>");
                        }
                    %>
                </select>
            </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Agent : </td>
            <td class="tr1">
                <select name="agentid" class="txtbox" <%=conf%> ></option>
                    <option value=""></option>
                    <%
                        conn = Database.getConnection();
                        query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
                        pstmt = conn.prepareStatement( query );
                        rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                            selected="";
                            if(rs.getString("agentid").equalsIgnoreCase((String) innerhash.get("agentid")))
                            {
                                selected="selected";
                            }
                            out.println("<option value=\""+rs.getInt("agentid")+"\" "+selected+">"+rs.getInt("agentid")+" - "+rs.getString("agentName")+"</option>");
                        }
                        }
                        catch (Exception e)
                        {
                            log.error("Exception while fetching data from agents table::",e);
                        }
                        finally
                        {
                            Database.closeConnection(conn);
                        }
                    %>
                </select>
            </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Gateway Table Name: </td>
            <td class="tr1"><input type="text" class="txtbox1" value="<%=partnerFunctions.isValueNull(ESAPI.encoder().encodeForHTML(gateway_table_name))?ESAPI.encoder().encodeForHTML(gateway_table_name):""%>" <%=conf%>></td>
        </tr>

        <% if(table_data.length() > 0)
        {

        %>
        <tr <%=style%>>
            <td class="th0" colspan="2">GateWay Data Details</td>
        </tr>
        <tr <%=style%>>

                        <%=table_data%>

        </tr>
        <%

           }
           %>
        <%
                out.println("<tr>");
                if(!conf.equalsIgnoreCase("disabled"))
                out.println("<td colspan='2' style='text-align:center'><input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\"><input type=\"hidden\" name=\"action\" value=\""+action+"\"><input type=\"submit\" name=\"modify\" value=\"Save\" "+conf+"></td>");

                out.println("</tr>");
            }
        %>


    </table>
</form>
</div>
</div>
</div>
</body>
</html>