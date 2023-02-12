<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.vo.AgentCommissionVO" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="java.util.*" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 10/10/2015
  Time: 1:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private static Logger logger=new Logger("actionFraudSystemSubAccountRule.jsp");
%>
<html>
<head>
    <title>Settings> Charges Interface> Agent Commission</title>
    <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
    <script type="text/javascript" src='/partner/css/new/html5.js'></script>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>


    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
    </script>
    <script language="javascript">
        function Delete()
        {
            var checkboxes = document.getElementsByName("id");
            var checked=[];

            var total_boxes = checkboxes.length;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    checked.push(checkboxes[i].value);
                    checked.join(',');
                }
            }
            document.getElementById("ids").value=checked;
            if(!flag)
            {
                alert("Select at least one record");
                return false;
            }
            if (confirm("Do you really want to Delete all selected Data."))
            {
                document.delete.submit();
            }
        }
    </script>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if(com.directi.pg.Admin.isLoggedIn(session))
    {
        String agentId=nullToStr(request.getParameter("agentid"));
        String memberid=nullToStr(request.getParameter("memberid"));
        String terminalid=nullToStr(request.getParameter("terminalid"));
        String chargeName = request.getParameter("chargeid") == null ? "" : request.getParameter("chargeid");

        String str="ctoken=" + ctoken;
        if(memberid!=null){str=str+"&memberid="+memberid;}
        if(agentId!=null){str=str+"&agentid="+agentId;}
        if (chargeName != null) {str = str + "&chargename=" + chargeName;}
        if (terminalid != null) {str = str + "&terminalid=" + terminalid;}

        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        List<AgentCommissionVO> chargeNameList = PayoutManager.loadchargenameAgent();


%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Agent Commission
                <div style="float:right;">
                    <form action="/icici/manageAgentCommission.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Agent Commission" name="submit" style="width: 250px">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Agent Commission
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/ListAgentCommission?ctoken=<%=ctoken%>" method="post" name="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                %>
                <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right:2.5%">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td colspan="4"&nbsp;></td>
                                </tr>
                                <br>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Agent Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on">
                                        <%--<select name="agentid" class="txtbox"><option value="" selected></option>
                                            <%
                                                Connection conn=null;
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT agentId,agentName FROM agents WHERE activation='T' ORDER BY agentId ASC";
                                                    PreparedStatement pstm = conn.prepareStatement(query);
                                                    ResultSet rs = pstm.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getInt("agentId") + "\">" + rs.getInt("agentId") + " - " + rs.getString("agentName") + "</option>");
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception"+e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" for="midy">Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="memberid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                       <%-- <select name="memberid" class="txtbox"><option value="" selected></option>
                                            <%
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                                                    PreparedStatement pstm = conn.prepareStatement(query);
                                                    ResultSet rs = pstm.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception"+e);
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
                                    <td colspan="6">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" for="tid">Terminal Id:</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="terminalid" id="tid3" value="<%=terminalid%>" class="txtbox" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Commission Name:</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" class="txtbox" name="chargeid">
                                            <option value="" selected>Select Commission Name</option>
                                            <%
                                                for (AgentCommissionVO commissionVO : chargeNameList)
                                                {
                                                    String isSelected = "";
                                                    if (commissionVO.getChargeId().equalsIgnoreCase(chargeName))
                                                        isSelected = "selected";
                                                    else
                                                        isSelected = "";
                                            %>
                                            <option value="<%=commissionVO.getChargeId()%>" <%=isSelected%>><%=commissionVO.getChargeName()%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="6">&nbsp;</td>
                                </tr>
                                </tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
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
    <%  String errormsg1 = (String)request.getAttribute("message");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
            out.println(errormsg1);
            out.println("</font></td></tr></table>");
        }
    %>

    <%
        Functions functions=new Functions();
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");
        Hashtable temphash=null;
        int records=0;
        int totalrecords=0;

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
    <div id="containrecord"></div>
    <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td width="4%" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
            <td width="5%"  class="th0">Agent ID</td>
            <td width="5%"  class="th0">Member ID</td>
            <td width="5%" class="th1">Terminal ID</td>
            <td width="15%" class="th0">Commission Name</td>
            <td width="4%" class="th1">Commission Value</td>
            <td width="4%" class="th1">Start Date</td>
            <td width="4%" class="th1">End Date</td>
            <td width="4%" class="th1">Sequence Number</td>
            <td width="4%" class="th1" colspan="2">Action</td>
            <td width="4%" class="th1">Action Executor Id</td>
            <td width="4%" class="th1">Action Executor Name</td>
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
                style="class=tr0";
                ext="dark";
            }
            else
            {
                style="class=tr1";
                ext="light";
            }

            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
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
            out.println("<td align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value=\"" +ESAPI.encoder().encodeForHTML((String)temphash.get("id"))+ "\"></td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("agentid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("commission_value"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+targetFormat.format(targetFormat.parse((String)temphash.get("startdate")))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+targetFormat.format(targetFormat.parse((String)temphash.get("enddate")))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("sequence_no"))+"</td>");
            out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionAgentCommission?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"commissionid\" value=\"" + temphash.get("id") + "\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"goto\" value=\"Modify\"></form></td>");
            out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionAgentCommission?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"id\" value=\"" + temphash.get("id") + "\"><input type=\"hidden\" name=\"action\" value=\"history\"><input type=\"submit\" class=\"goto\" value=\"History\"></form></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
        }
    %>
        <form name="delete" action="/icici/servlet/ListAgentCommission?ctoken=<%=ctoken%>" method="post">
            <table width="100%">
                <thead>
                <tr>
                    <td width="15%" align="center">
                        <input type="hidden" name="delete" value="delete">
                        <input id="ids" type="hidden" name="ids" value="">
                        <input type="button" name="delete" class="btn btn-default center-block" value="Delete" onclick="return Delete();">
                    </td>
                </tr>
                </thead>
            </table>
        </form>
        <table align=center valign=top><tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                    <jsp:param name="numrows" value="<%=pagerecords%>"/>
                    <jsp:param name="pageno" value="<%=pageno%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="ListAgentCommission"/>
                    <jsp:param name="currentblock" value="<%=currentblock%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
            </td>
        </tr>
        </table>
            <%
      }
      else if(functions.isValueNull((String)request.getAttribute("statusMsg")))
      {
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
      }
      else if (functions.isValueNull((String)request.getAttribute("success")))
      {
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("success")));
      }
      else
      {
         out.println(Functions.NewShowConfirmation("Result", "No Records Found."));
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
