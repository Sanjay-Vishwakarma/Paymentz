<%@ page import="com.directi.pg.Functions,
                 com.directi.pg.Logger"%>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 9/9/14
  Time: 3:39 PM
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
    <title>Fraud Management> Fraud System Master Details> Member Fraud System</title>
</head>
<body>
<%!
    private static Logger logger=new Logger("listMemberFraudSystem.jsp");
%>
<%   String memberid=nullToStr(request.getParameter("memberid"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<div class="row">
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
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    String str="ctoken=" + ctoken;
                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                %>

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
                                        <%--<select name="memberid" class="txtbox" style="width: 172px;"><option value="" selected></option>
                                            <%
                                                Connection conn=null;
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
                                                catch(SQLException e)
                                                {
                                                    logger.error("SQLException occurred");
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }

                                            %>
                                        </select>--%>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Fraud System</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select name="fsid" class="txtbox"><option value="" selected>Select Fraud System</option>
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
</div>
<div class="reporttable">
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
        out.println(error);

    }
    Hashtable temphash=null;

    if (hash != null && hash.size() > 0)
    {

        int records=0;
        int totalrecords=0;
        String currentblock=request.getParameter("currentblock");

        if(currentblock==null)
            currentblock="1";

        try
        {
            records=Functions.convertStringtoInt((String)hash.get("records"),15);
            totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
        }
        catch(Exception ex)
        {
            logger.error("Records & TotalRecords is found null",ex);
        }

        if(records>0)
        {
%>
<table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable" >
    <tr>
        <td valign="middle" align="center" class="th0">Sr No</td>
        <td valign="middle" align="center" class="th0">Member Id</td>
        <td valign="middle" align="center" class="th0">Fraud System Name</td>
        <td valign="middle" align="center" class="th0">Is Online Fraud Check</td>
        <td valign="middle" align="center" class="th0">Is Fraud API User</td>
        <td valign="middle" align="center" class="th0">Is Active</td>
        <td valign="middle" align="center" class="th0">Is Test</td>
        <td valign="middle" align="center" class="th0" colspan="2">Action</td>
    </tr>
    <%
        String style="class=td1";
        String ext="light";

        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

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
            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            out.println("<td align=\"center\" "+style+">"+srno+ "</td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"<input type=\"hidden\" name=\"memberid\" value=\""+temphash.get("memberid")+"\"></td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML(FraudSystemService.fraudSystem.get(temphash.get("fsid")))+"<input type=\"hidden\" name=\"fsid\" value=\""+temphash.get("fsid")+"\"></td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("isonlinefraudcheck"))+"<input type=\"hidden\" name=\"isonlinefraudcheck\" value=\""+temphash.get("isonlinefraudcheck")+"\"></td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("isapiuser"))+"<input type=\"hidden\" name=\"isapiuser\" value=\""+temphash.get("isapiuser")+"\"></td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("isActive"))+"<input type=\"hidden\" name=\"isactive\" value=\""+temphash.get("isActive")+"\"></td>");
            out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("isTest"))+"<input type=\"hidden\" name=\"istest\" value=\""+temphash.get("isTest")+"\"></td>");
            out.println("<td align=\"center\" "+style+"><form action=\"/icici/servlet/ActionMemberFraudSystem?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\""+temphash.get("id")+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"gotoauto\" value=\"Modify\"></form></td>");
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
            <jsp:param name="page" value="ListMemberFraudSystem"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>
</tr>
</table>
<%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
        }
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
    }
%>
</div>
<%
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