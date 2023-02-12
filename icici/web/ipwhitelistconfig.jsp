<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.payment.IPEntry" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 3/31/14
  Time: 1:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%!
    private static Logger logger= new Logger("ipwhitelistconfig.jsp");
%>
<head>
    <title>1.Whitelist Module> IP Whitelist Configuration</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<script>
    function handleChange1()
    {
        document.forms.merchantid.value="";
        document.forms.partnerid.value="";
        document.forms.agentid.value="";
    }
</script>
<%
    IPEntry ipEntry = new IPEntry();
    String memberid=nullToStr(request.getParameter("merchantid"));
    String partnerid=nullToStr(request.getParameter("partnerid"));
    String agentid=nullToStr(request.getParameter("agentid"));
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    LinkedHashMap memberidDetails=ipEntry.selectMemberidForDropDown(memberid);
    LinkedHashMap partnerdetails=ipEntry.selectPartnerIdForDropDown(partnerid);
    LinkedHashMap agentdetails=ipEntry.selectAgentIDForDropDown(agentid);
    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<form action="/icici/servlet/IpWhitelistConfig?ctoken=<%=ctoken%>" method="post" name="forms" >
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    IP Whitelisting Configuration
                </div>
                <%
                    String errormsg = (String) request.getAttribute("error");
                    if(errormsg!=null)
                    {
                        out.println("<br><font face=\"arial\" color=\"red\" size=\"2\">");
                        errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                        out.println(errormsg);
                        out.println("</font>");
                        out.println("</td></tr></table>");
                    }
                %>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.4%;margin-right: 2.4% ">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <%
                        String role="Admin";
                        String username=(String)session.getAttribute("username");
                        String actionExecutorId=(String)session.getAttribute("merchantid");
                        String actionExecutorName=role+"-"+username;
                        String str="ctoken=" + ctoken;
                        String IPv4 = "", IPv6 = "";
                        String mgId= "";
                        String pgId ="";
                        String agId ="";
                        if("mId".equals(request.getAttribute("group1")))
                        {
                            mgId = "checked";
                            str=str+"&merchantid="+memberid;

                        }
                        if("pId".equals(request.getAttribute("group1")))
                        {
                            pgId = "checked";
                            str=str+"&partnerid="+partnerid;
                        }
                        if("aId".equals(request.getAttribute("group1")))
                        {
                            agId = "checked";
                            str=str+"&agentid="+agentid;
                        }
                    %>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb"></td>
                                    <td width="10%" class="textb" for="mid" ><input type="radio" name="group1"  onchange="handleChange1();" value="mId" <%=mgId%>>&nbsp;&nbsp;Merchant Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="merchantid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on" onclick="handleChange1()">
                                    </td>
                                    <td width="4%" class="textb"></td>
                                    <td width="8%" class="textb"><input type="radio" name="group1" onchange="handleChange1();" value="pId" <%=pgId%>>&nbsp;&nbsp;Partner Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="partnerid" id="allpid" value="<%=partnerid%>" class="txtbox" autocomplete="on" onclick="handleChange1()">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"><input type="radio" name="group1" onchange="handleChange1();" value="aId"  <%=agId%>>&nbsp;&nbsp;Agent Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="agentid" id="aid" value="<%=agentid%>" class="txtbox" autocomplete="on" onclick="handleChange1()">
                                    </td>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" value="Search" class="buttonform" id="searchBtn">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

            </div>
        </div>
    </div>
</form>
<div class="reporttable">
    <%
        Hashtable hash = (Hashtable)request.getAttribute("recordHash");
        Hashtable temphash=null;
        int records=-1;
        int totalrecords=0;
        String currentblock=request.getParameter("currentblock");
        try
        {
            if(hash.equals(null) || hash== null)
            {
                out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
            }
        }
        catch (Exception e)
        {
            logger.error("Nullpointer exception",e);
        }
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
        String ipAddress = "";

        errormsg=(String)request.getAttribute("message");
        if(errormsg!=null)
        {
            out.println("<center><font class=\"text\" face=\"arial\">"+errormsg+"</font></center>");
        }
        if(hash!=null && hash.size()>0)
        {
            hash = (Hashtable)request.getAttribute("recordHash");
        }
        String style="class=tr1";
        String ext="light";
        String loginName = "";
        String userName="";

        if(request.getAttribute("merchantid")!=null)
        {
            loginName =  (String) memberidDetails.get(request.getAttribute("merchantid"));
            userName = "Merchant";
        }

        if(request.getAttribute("partnerid")!=null)
        {
            loginName =  (String) partnerdetails.get(request.getAttribute("partnerid"));
            userName = "Partner";
        }

        if(request.getAttribute("agentid")!=null)
        {
            loginName =  (String) agentdetails.get(request.getAttribute("agentid"));
            userName = "Agent";
        }
        if(records > -1)
        {
    %>
    <table  bordercolor="#ffffff" width="30%" align="center" class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td valign="middle" align="center" class="th0"><%=userName%> Start Ip</td>
            <td valign="middle" align="center" class="th0">IP Type</td>
            <td valign="middle" align="center" class="th0">Action Executor Id</td>
            <td valign="middle" align="center" class="th0">Action Executor Name</td>
            <td valign="middle" align="center" class="th0" colspan="1">Operation</td>
        </tr>
        <%
            String memberId=request.getParameter("merchantid")==null?"":request.getParameter("merchantid");
            String partnerId=request.getParameter("partnerid")==null?"":request.getParameter("partnerid");
            String agentId=request.getParameter("agentid") ==null?"":request.getParameter("agentid");
            Functions functions=new Functions();


            for(int pos=1;pos<=records;pos++)
            {


                String id=Integer.toString(pos);
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
                if(records>0)
                {
                    temphash=(Hashtable)hash.get(id);
                    if(functions.isValueNull((String)temphash.get("actionExecutorId")))
                    {
                        actionExecutorId=(String)temphash.get("actionExecutorId");
                    }
                    else
                    {
                        actionExecutorId="-";
                    }
                    if(functions.isValueNull((String)temphash.get("actionExecutorName")))
                    {
                        actionExecutorName=(String)temphash.get("actionExecutorName");
                    }
                    else
                    {
                        actionExecutorName="-";
                    }
                    out.println("<tr>");
                    out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("ipAddress"))+"</td>");
                    out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("type"))+"</td>");
                    out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
                    out.println("<td align=center "+style+">"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
                    out.println("<td align=center "+style+"><form action=\"/icici/servlet/IpWhitelistConfig?ctoken="+ctoken+"\" method=\"POST\">" +
                            "<input type=\"hidden\" name=\"merchantid\" value=\""+memberId+"\">"+
                            "<input type=\"hidden\" name=\"partnerid\" value=\""+partnerId+"\">"+
                            "<input type=\"hidden\" name=\"agentid\" value=\""+agentId+"\">"+
                            "<input type=\"hidden\" name=\"ipAddress\" value=\""+temphash.get("ipAddress")+"\">"+
                            "<input type=\"hidden\" name=\"action\" value=\"delete\">"+
                            "<input type=\"submit\" class=\"buttonform\" name=\"submit\" value=\"Delete\"></form></td>");
                    out.println("</tr>");
                }
            }
            String id="";
            if(functions.isValueNull(memberId))
            {
                id=memberId;
            }
            if(functions.isValueNull(partnerId))
            {
                id=partnerId;
            }
            if(functions.isValueNull(agentId))
            {
                id=agentId;
            }
            out.println("<tr>");
            out.println("<td align=center "+style+"><form action=\"/icici/servlet/IpWhitelistConfig?ctoken="+ctoken+"\" method=\"POST\">"+
                    "<input type=\"hidden\" name=\"merchantid\" value=\""+memberId+"\">"+
                    "<input type=\"hidden\" name=\"partnerid\" value=\""+partnerId+"\">"+
                    "<input type=\"hidden\" name=\"agentid\" value=\""+agentId+"\">"+
                    "<input type=\"text\"class=\"txtbox \"  size=\"15\" name=\"ipAddress\"></td>");
            out.println("<td align=center "+style+">&nbsp;<select class=\"txtboxtabel\" name=\"type_"+id+"\"><option value=\"IPv4\" default>IPv4</option><option value=\"IPv6\">IPv6</option> </select></td>");
            out.println("<td align=center "+style+"><input type=\"hidden\" name=\"action\" value=\"add\"><input type=\"submit\" class=\"buttonform\" name=\"submit\" value=\"Add\"></form></td>");
            out.println("</tr>");
        %>
    </table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="IpWhitelistConfig"/>
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
                out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>