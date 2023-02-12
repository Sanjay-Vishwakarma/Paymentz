<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.Hashtable" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: saurabh
  Date: 2/11/14
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title>Bank Details > GateWay Account Master</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }

%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Gateway Accounts Interface
                <div style="float:right;">
                    <form action="/icici/addPartnerAccountMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add Partner Account Mapping" name="submit" style="width: 250px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Partner Account Mapping
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addNewGatewayAccount.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Gateway Account" name="submit" style="width: 250px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Gateway Account
                        </button>
                    </form>
                </div>
            </div>

            <form action="/icici/servlet/listGatewayAccountDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    String str="ctoken=" + ctoken;
                    str+="&accountid="+request.getParameter("accountid");
                    str+="&merchantid="+request.getParameter("merchantid");
                    str+="&gateway="+request.getParameter("gateway");
                    str+="&ismastercardsupported="+request.getParameter("ismastercardsupported");
                    str+="&currency="+request.getParameter("currency");
                    String gateway=Functions.checkStringNull(request.getParameter("gateway"))==null?"":request.getParameter("gateway");
                    String currency=Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

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
                                    <td width="13%" class="textb" >Gateway Name</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" name="gateway"  class="txtbox" value="<%=gateway%>">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="13%" class="textb" >Gateway Account Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  maxlength="15" type="text" name="accountid"  class="txtbox" >
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Merchant Id</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" name="merchantid" class="txtbox">
                                    </td>
                                <tr><td colspan="12">&nbsp;</td></tr>
                                <tr><td colspan="12">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is MasterCard Supported</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><select name="ismastercardsupported"
                                                                     id="ismastercardsupported">
                                        <option value="" >All</option>
                                        <option value="1">Y</option>
                                        <option value="0">N</option>
                                    </select></td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Currency</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" name="currency" class="txtbox">
                                    </td>
                                <tr><td colspan="12">&nbsp;</td></tr>
                                <tr><td colspan="12">&nbsp;</td></tr>
                                </tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
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

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;


    String errormsg=(String)request.getAttribute("message");
    if(errormsg!=null)
    {
        out.println("<center><font class=\"textb\"><b>"+errormsg+"</b></font></center><br>");
    }

    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
        currentblock="1";

    try
    {
        records=Integer.parseInt((String)hash.get("records"));
        totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
    }
    catch(Exception ex)
    {

    }
    if(hash!=null)
    {
        hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
%>
<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">

    <thead>
        <tr>
            <td valign="middle" class="th0" align="center" >Sr no</td>
            <td valign="middle" align="center" class="th0">Account Id</td>
            <td valign="middle" align="center" class="th0">Merchantid</td>
            <td valign="middle" align="center" class="th0">Gateway Name</td>
            <td valign="middle" align="center" class="th0">Alias Name</td>
            <td valign="middle" align="center" class="th0">Display Name</td>
            <td valign="middle" align="center" class="th0">3D Support</td>
            <td valign="middle" align="center" class="th0">Is MasterCard Supported</td>
            <td valign="middle" align="center" class="th0">Action Executor Name</td>
            <td valign="middle" align="center" class="th0" colspan = "2">Action</td>
            </td>
        </tr>
    </thead>
    <%
        String style="class=td1";
        String ext="light";

        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

            if(pos%2==0)
            {
                style="class=tr1";
                ext="dark";
            }
            else
            {
                style="class=tr0";
                ext="light";
            }

            temphash=(Hashtable)hash.get(id);
            Functions functions=new Functions();
            String actionExecutorName="-";
            if(functions.isValueNull((String)temphash.get("actionExecutorName"))){
                actionExecutorName=(String)temphash.get("actionExecutorName");
            }
             String mastercard="";
            String master_card="";
            if (temphash.get("ismastercardsupported")!= null)
            {
                 master_card= (String)temphash.get("ismastercardsupported");

            }
            if (functions.isValueNull(master_card) && "1".equalsIgnoreCase(master_card))
            {
                mastercard="Y";
            }
           else if(functions.isValueNull(master_card) && "0".equalsIgnoreCase(master_card))
            {
                mastercard="N";
            }
            out.println("<tr>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+srno+ "</td>");
            out.println("<td "+style+" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"<input type=\"hidden\" name=\"accountid\" value=\""+temphash.get("accountid")+"\"></td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("merchantid"))+"<input type=\"hidden\" name=\"merchantidy\" value=\""+temphash.get("merchantid")+"\"></td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("gateway"))+"_"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"<input type=\"hidden\" name=\"pgtypeid\" value=\""+temphash.get("pgtypeid")+"\"></td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("aliasname"))+"<input type=\"hidden\" name=\"aliasname\" value=\""+temphash.get("aliasname")+"\"></td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("displayname"))+"<input type=\"hidden\" name=\"displayname\" value=\""+temphash.get("displayname")+"\"></td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("3dSupportAccount"))+"<input type=\"hidden\" name=\"3dSupportAccount\" value=\""+temphash.get("3dSupportAccount")+"\"></td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML(mastercard)+"<input type=\"hidden\" name=\"ismastercardsupported\" value=\""+mastercard+"\"></td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"<input type=\"hidden\" name=\"actionExecutorName\" value=\""+temphash.get("actionExecutorName")+"\"></td>");
            out.println("<td "+style+" align=\"center\"><form action=\"/icici/servlet/viewGatewayAccountDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"View\"><input type=\"hidden\" name=\"action\" value=\"View\"></form></td>");
            out.println("<td "+style+" align=\"center\"><form action=\"/icici/servlet/viewGatewayAccountDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"Edit\"><input type=\"hidden\" name=\"action\" value=\"modify\"></form></td>");

            //out.println("</form>");
            out.println("</tr>");
        }
    %>
</table>
    <table align=center valign=top>
        <tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="listGatewayAccountDetails"/>
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
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
    }
%>

</div>
</body>
</html>