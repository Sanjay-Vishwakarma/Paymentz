<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="java.util.List" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 5/5/2015
  Time: 4:12 PMblacklistIp.jsp
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="blocktab.jsp" %>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<html>
<head>
    <title>Block Ip Address</title>
<style>
    .btn-default {
        color: #ffffff !important;
        background-color: #34495e !important;
        border-color: #2c3e50;
    }
    .btn{
        font-size: 12px !important;
        font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif ;
    }
</style>
</head>

<%
    session.setAttribute("submit","Block IPs");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String memberid = request.getParameter("memberid") == null ? "" : request.getParameter("memberid");
    String selectIpVersion = request.getParameter("type") == null ? "" : request.getParameter("type");
    String ipAddress = request.getParameter("ipAddress") == null ? "" : request.getParameter("ipAddress");
    String reason = request.getParameter("reason") == null ? "" : request.getParameter("reason");
    String remark = request.getParameter("remark") == null ? "" : request.getParameter("remark");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions = new Functions();
    int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
%>
<body>

<div class="row" style="margin-top: 0px">
    <div class="col-lg-12">
        <div class="panel panel-default" style="margin-top: 0px">
            <div class="panel-heading">
                Blocked IP List
            <div style="float: right">
                <form action="/icici/uploadblacklistip.jsp?ctoken=<%=ctoken%>" method="POST">
                    <button type="submit" value="Upload Card Details" name="submit" class="addnewmember">
                        <i class="fa fa-sign-in"></i>
                        Upload Bulk Blacklist Ip
                    </button>
                </form>
            </div>
            </div>
            <td>&nbsp;&nbsp;</td>
            <%

                String errormsg1 = (String) request.getAttribute("error");
                if (errormsg1 != null)
                {
                    out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                }
                if (request.getAttribute("msg") != null)
                {
                    out.println("<center><font class=\"textb\"><b>" + request.getAttribute("msg") + "<br></b></font></center>");
                }
            %>
            <table border="0" align="center" width="95%" cellpadding="2" cellspacing="2"
                   style="margin-left:2.5%;margin-right: 2.5% ">
                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>
                <form name="BlacklistIp" action="/icici/servlet/BlacklistIp?ctoken=<%=ctoken%>" method="post">

                    <%

                        String str = "ctoken=" + ctoken;

                    %>
                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <input type="hidden" value="" name="upload" id="upload">
                    <table align="center" width="45%" cellpadding="2" cellspacing="2">
                        <tbody>
                        <tr>
                            <td>
                                <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                    <tbody>

                                    <tr style="margin: 35px;">
                                        <td width="5%" class="textb">&nbsp;</td>
                                        <td width="10%" class="textb">Select IP Type*</td>
                                        <td width="5%" class="textb">:</td>
                                        <td width="20%" class="textb" style=" padding: 8px 0px;">
                                            <select class="form-control" style="width: 70%" name="type">
                                                <option value=""<%
                                                    if (selectIpVersion.equals(""))
                                                    {
                                                %> selected <%}%> default>Select IP Version
                                                </option>
                                                <option value="IPv4" <%
                                                    if (selectIpVersion.equals("IPv4"))
                                                    {
                                                %> selected <%}%>>IPv4
                                                </option>
                                                <option value="IPv6" <%
                                                    if (selectIpVersion.equals("IPv6"))
                                                    {
                                                %> selected <%}%>>IPv6
                                                </option>
                                            </select>
                                        </td>
                                    </tr>


                                    <tr>
                                        <td width="5%" class="textb" style="height:10%">&nbsp;</td>
                                        <td width="10%" class="textb">IP Address*</td>
                                        <td width="5%" class="textb">:</td>
                                        <td width="20%" class="textb" style=" padding: 8px 0px;">
                                            <input type="text" name="ipAddress" class="form-control"
                                                   style="width: 70%;height: 30px;"
                                                   value="">
                                        </td>
                                    </tr>

                                    <td>&nbsp;&nbsp;</td>
                                    <tr style="margin: 35px;">
                                        <td width="5%" class="textb">&nbsp;</td>
                                        <td width="10%" class="textb">Member ID*</td>
                                        <td width="5%" class="textb">:</td>
                                        <td>
                                            <input name="memberid" id="mid" value="" class="txtbox"
                                                   style="width: 70%;height: 32px;"
                                                   autocomplete="on">
                                        </td>
                                    </tr>
                                    <td>&nbsp;&nbsp;</td>
                                    <td>&nbsp;&nbsp;</td>
                                    <tr style="margin: 35px;">
                                        <td width="5%" class="textb">&nbsp;</td>
                                        <td width="10%" class="textb">Reason*</td>
                                        <td width="5%" class="textb">:</td>
                                        <td>
                                            <input name="reason"  value="" class="txtbox"
                                                   style="width: 70%;height: 32px;">
                                        </td>
                                    </tr>

                                    <td>&nbsp;&nbsp;</td>
                                    <tr style="margin: 35px;">
                                        <td width="5%" class="textb">&nbsp;</td>
                                        <td width="10%" class="textb">Remark</td>
                                        <td width="5%" class="textb">:</td>
                                        <td>
                                            <input name="remark"  value="" class="txtbox"
                                                   style="width: 70%;height: 32px;">
                                        </td>
                                    </tr>
                                    <td>&nbsp;&nbsp;</td>
                                    <tr>
                                        <div>

                                            <td>&nbsp;&nbsp;</td>
                                            <td>&nbsp;&nbsp;</td>
                                            <td>&nbsp;&nbsp;</td>
                                            <td>

                                                <button type="button"
                                                        style="display: inline-block!important;width:32%;height: 28px ;"
                                                        onclick="memberidonclick()"
                                                        class="btn btn-default center-block">
                                                    <i class="fa fa-ban"></i>
                                                    &nbsp;&nbsp;Block IP
                                                </button>
                                                &nbsp;&nbsp;&nbsp;
                                                <button type="submit" name="search" value="search"
                                                        class="btn btn-default center-block"
                                                        style="display: inline-block!important;width:32%;height: 28px ;">
                                                    <i class="fa fa-clock-o"></i>
                                                    &nbsp;&nbsp;Search
                                                </button>
                                            </td>
                                        </div>
                                    </tr>

                                    </tr>
                                    <tr>
                                        <td>&nbsp;&nbsp;</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </table>
            </form>
            </table>
        </div>
    </div>
</div>
<div class="reporttable">

    <%
        String currentblock = request.getParameter("currentblock");
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;


        if (currentblock == null)
            currentblock = "1";

        if (request.getAttribute("listOfall") != null)
        {
            List<BlacklistVO> iList = (List<BlacklistVO>) request.getAttribute("listOfall");
            PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
            paginationVO.setInputs(paginationVO.getInputs() + "&ctoken=" + ctoken);
            if (iList.size() > 0)
            {
    %>

    <br>

    <form name="BlacklistIpDelete" action="/icici/servlet/BlacklistIp?ctoken=<%=ctoken%>"
          method="post">
        <table id="myTable" class="display table table-striped table-bordered" width="100%"
               style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
            <thead>
            <tr style="background-color:#34495e; color: white;">
                <th valign="middle" align="center" style="text-align: center ;width: 4% "><input type="checkbox"
                                                                                                 onClick="ToggleAll(this);"
                                                                                                 name="alltrans">
                </th>
                <th valign="middle" align="center" style="text-align: center ;width: 8%">Sr No</th>
                <th valign="middle" align="center" style="text-align: center ;width: 25%">Member ID</th>
                <th valign="middle" align="center" style="text-align: center ;width: 40%">Ip Address</th>
                <th valign="middle" align="center" style="text-align: center ;width: 80%">IP Type</th>
                <th valign="middle" align="center" style="text-align: center ;width: 80%">Reason</th>
                <th valign="middle" align="center" style="text-align: center ;width: 80%">Remark</th>
                <th valign="middle" align="center" style="text-align: center ;width: 100%">Timestamp</th>
                <th valign="middle" align="center" style="text-align: center ;width: 100%">Action Executor Id</th>
                <th valign="middle" align="center" style="text-align: center ;width: 120%">Action Executor Name</th>
            </tr>
            </thead>
            <tbody>
                <%
                String style = "class=td1";
                String ext = "light";
                int pos = 1;
                if (pos % 2 == 0)
                {
                    style = "class=tr0";
                    ext = "dark";
                }
                else
                {
                    style = "class=tr1";
                    ext = "light";
                }
                pos = (pagerecords * (pageno - 1)) + 1;
                for (BlacklistVO blacklistVO :iList )
                {
                if(functions.isValueNull(blacklistVO.getActionExecutorId()))
                {
                actionExecutorId=blacklistVO.getActionExecutorId();
                }
                else
                {
                actionExecutorId="-";
                }
                if(functions.isValueNull(blacklistVO.getActionExecutorName()))
                {
                actionExecutorName=blacklistVO.getActionExecutorName();
                }
                else
                {
                actionExecutorName="-";
                }if(functions.isValueNull(blacklistVO.getBlacklistReason()))
                {
                reason=blacklistVO.getBlacklistReason();
                }
                else
                {
                reason="-";
                }if(functions.isValueNull(blacklistVO.getRemark()))
                {
                remark=blacklistVO.getRemark();
                }
                else
                {
                remark="-";
                }
                    out.println("<tr>");
                    out.println("<td  align=center  "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value=\""+blacklistVO.getId()+"\"></td>");
                    out.println("<td data-label='Sr no' align=center " + style + ">" + pos + "</td>");
                     if (functions.isValueNull(blacklistVO.getMemberId()))
                    {
                    out.println("<td data-label='Member ID' align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getMemberId()) + "</td>");
                    }
                    else
                    {
                        out.println("<td data-label='Member ID' align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML("-") + "</td>");
                    }
                    out.println("<td data-label='Ip Address' align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getStartIpv4()) + "</td>");
                    out.println("<td data-label='IP Type' align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getselectIpVersion()) + "</td>");
                    out.println("<td data-label='Reason' align=center " + style + ">&nbsp;"+ reason+ "</td>");
                    out.println("<td data-label='Remark' align=center " + style + ">&nbsp;"+ remark+ "</td>");
                    out.println("<td data-label='Timestamp' align=center " + style + ">&nbsp;"+ blacklistVO.getTimestamp()+ "</td>");
                    out.println("<td data-label='Action Executor Id' align=center " + style + ">&nbsp;" +  actionExecutorId + "</td>");
                    out.println("<td data-label='Action Executor Name' align=center " + style + ">&nbsp;"+ actionExecutorName+ "</td>");
                    out.println("</tr>");
                    pos++;

                }
                %>
            <thead>
            <tr>
                <td class="th0">&nbsp;</td>
                <td class="th0" align="center" class=textb>Page No:- <%=pageno%>
                </td>
                <td class="th0" align="center" class=textb>Total Records: <%=paginationVO.getTotalRecords()%>
                </td>
                <td class="th0">&nbsp;</td>
                <td class="th0">&nbsp;</td>
                <td class="th0">&nbsp;</td>
                <td class="th0">&nbsp;</td>
                <td class="th0">&nbsp;</td>
                <td class="th0">&nbsp;</td>
                <td class="th0">&nbsp;</td>
            </tr>
            </thead>
        </table>
        <table width="100%">
            <thead>

            <tr>
                <td width="15%" align="center">
                    <input type="hidden" name="memberid" value="<%=memberid%>">
                    <input type="hidden" name="type" value="<%=selectIpVersion%>">
                    <input type="hidden" name="ipAddress" value="<%=ipAddress%>">
                    <input type="hidden" name="delete" value="delete"><input type="button" name="Unblock"
                                                                             class="btn btn-default center-block"
                                                                             value="Unblock" onclick="return Delete();">
                </td>
            </tr>
            </thead>
        </table>
    </form>
    &nbsp;&nbsp;
    <table align=center valign=top>
        <tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                    <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                    <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                    <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                    <jsp:param name="page" value="BlacklistIp"/>
                    <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
            </td>
        </tr>
    </table>
</div>
</div>
<%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
        }
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Filter", "No records found."));
    }
%>

</body>
<script language="javascript">
    function memberidonclick()
    {
        var val = document.getElementById("mid").value;
        console.log(val, val != "", val != null, val != undefined)
        if (val != "" && val != null)
        {
            document.getElementById("upload").value = "upload";
            document.BlacklistIp.submit();
        }
        else
        {
            var conf = confirm("If Member ID is blank, the requested IP will be blocked Globally");
            if (conf == true)
            {
                document.getElementById("upload").value = "upload";
                document.BlacklistIp.submit();
            }
        }
    }

    function ToggleAll(checkbox)
    {
        flag = checkbox.checked;
        var checkboxes = document.getElementsByName("id");
        var total_boxes = checkboxes.length;

        for (i = 0; i < total_boxes; i++)
        {
            checkboxes[i].checked = flag;
        }
    }
    function Delete()
    {
        var checkboxes = document.getElementsByName("id");
        console.log("ID" + document.getElementsByName("id"));
        var total_boxes = checkboxes.length;
        flag = false;

        for (i = 0; i < total_boxes; i++)
        {
            if (checkboxes[i].checked)
            {
                flag = true;
                break;
            }
        }
        if (!flag)
        {
            alert("Select at least one record");
            return false;
        }
        if (confirm("Do you really want to Unblock all selected Data."))
        {
            document.BlacklistIpDelete.submit();
        }
    }

</script>

</html>