<%@ page import="org.owasp.esapi.ESAPI,
                 java.util.Hashtable,
                 com.directi.pg.Merchants,
                 com.directi.pg.core.GatewayTypeService,
                 com.directi.pg.Functions"%>
<%@ page import="java.util.*"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: WAHEED
  Date: Jun 8, 2012
  Time: 8:27:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

</head>
<body>
<% //ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Agent Master
                <div style="float: right;">
                    <form action="/icici/agentsignup.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Add New Agent" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Agent
                        </button>
                    </form>
                </div>
            </div>
            <br><br>
            <form action="/icici/servlet/ListAgentDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    String agentid=(String)request.getParameter("agentid");
                    String str="ctoken=" + ctoken;
                    if (agentid != null) str = str + "&agentid=" + agentid;
                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);



                %>
                <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="30%" class="textb" >Agent Id</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="25%" class="textb">
                                        <input  maxlength="15" type="text" name="agentid"  value=""  class="txtbox">
                                    </td>

                                    <td width="2%" class="textb"></td>
                                    <td width="45%" class="textb">Agent Name</td>
                                    <td width="10%" class="textb"></td>
                                    <td width="48%" class="textb">
                                        <input  maxlength="15" type="text" name="agentname"  value="" class="txtbox">

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
                                    <td width="10%" class="textb"></td>
                                    <td width="50%"  align="center">
                                        <button type="submit" class="buttonform" >
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
    String errormsg = (String) request.getAttribute("error");
    if(errormsg!=null)
    {
        out.println("<table align=\"center\"  >");
        out.println("<tr><td algin=\"center\" ><font face=\"arial\" color=\"red\"  size=\"2\">");
        out.println(errormsg);
        out.println("</font>");
        out.println("</td></tr></table>");
    }
    else
    {
        errormsg="";
    }
    Hashtable status_data=(Hashtable)request.getAttribute("status_report");

    String style="class=\"tr0\"";
    int records = 0;
    int totalrecords = 0;

    Hashtable temphash=new Hashtable();
    try
    {
        records = Integer.parseInt((String) status_data.get("records"));
        totalrecords = Integer.parseInt((String) status_data.get("totalrecords"));
    }
    catch (Exception ex)
    {
    }
    if (records > 0 && status_data!=null)
    {
%>

<table align=center width="50%">
    <tr>
        <td>
            <div align="center" class="textb"><h5>Merchant Transaction Report</h5></div> </td>

    </tr>
    <tr>
        <td width="50%"><div align="center">


            <table border="1" cellpadding="3" cellspacing="3" width="30%"  align="center" class="table table-striped table-bordered table-hover table-green dataTable">
               <thead>
                    <tr <%=style%>>

                        <td  class="th0">Status</td>
                        <td  class="th0">Transaction</td>
                        <td  class="th0">Total Amount</td>
                    </tr>
               </thead>
                <%  int tempcount=0;
                    String tempamt=null;
                    for (int pos = 1; pos < records; pos++)
                    {
                        String id = Integer.toString(pos);

                        temphash = (Hashtable) status_data.get(id);

                        String status=(String)temphash.get("status");
                        String count=(String)temphash.get("count");
                        String amount=(String)temphash.get("amount");

                %>
                <tr <%=style%>>
                <td class="tr0" align="center"><%=status%> </td>
                <td class="tr1" align="center" ><%=count%> </td>
                <td class="tr0" align="center"><%=amount%> </td>
            </tr>
                <%      //tempcount = tempcount+Integer.parseInt(count);

                }
                    out.println("<tr "+style+">");
                    out.println("<td  class=\"textb\" colspan=\"3\" align=\"center\">TOTAL Transaction:<font color=\"red\">"+totalrecords+"</font> </td>");
                    out.println("</tr >");
                    out.println("<tr "+style+">");
                    out.println("<td  class=\"textb\" colspan=\"3\" align=\"center\">GRAND Total Amount:<font color=\"red\">"+status_data.get("grandtotal")+"</font> </td>");
                    out.println("</tr >");

                %>
            </table>

        </div>
</table>
<br>
<%     }
else
{
    out.println(Functions.NewShowConfirmation("Sorry", "Status records is not found."));
}
}
else
{
    response.sendRedirect("/icici/logout.jsp");
    return;
}
%>
</div>
</body>
</html>
