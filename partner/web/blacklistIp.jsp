<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="java.util.List" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%--
  Created by IntelliJ IDEA.
  User: Sanjay Yadav
  Date: 13/10/2018
  Time: 4:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="blocktab.jsp" %>

<html>
<head>
    <title>Block Ip Address</title>
    <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <style type="text/css">
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>

</head>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String partnerid = session.getAttribute("partnerId").toString();
    String memberid = request.getParameter("memberid") == null ? "" : request.getParameter("memberid");
    String pid = request.getParameter("pid") == null ? "" : request.getParameter("pid");
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
        pid = String.valueOf(session.getAttribute("merchantid"));
        Config = "disabled";
    }
    String selectIpVersion = request.getParameter("type") == null ? "" : request.getParameter("type");
    String ipAddress = request.getParameter("ipAddress") == null ? "" : request.getParameter("ipAddress");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions = new Functions();
    PartnerFunctions partnerFunctions = new PartnerFunctions();
    int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
    String role=(String)session.getAttribute("role");
    String username=(String)session.getAttribute("username");
    String actionExecutorId=(String)session.getAttribute("merchantid");
    String actionExecutorName=role+"-"+username;

%>
<div class="content-page">
    <div class="content" style="padding:0px 20px ;margin: 0px;">
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Block IP</strong>
                            </h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content">
                            <div id="horizontal-form">
                                <form name="BlacklistIp" action="/partner/net/BlacklistIp?ctoken=<%=ctoken%>" method="post" name="forms">
                                    <div align="center">
                                        <input type="hidden" value="<%=ctoken%>" id="ctoken">
                                        <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                            <%
                      String errormsg = (String) request.getAttribute("error");
                      String msg = (String) request.getAttribute("msg");
                      if (partnerFunctions.isValueNull(errormsg))
                      {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                      }
                      if(functions.isValueNull(msg))
                      {
                      out.println(Functions.NewShowConfirmation1("Sorry", msg));
                      }
                      %>
                                        <div class="form-group col-md-1">
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Partner ID</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" name="pid" id="pid" class="form-control"
                                                           value="<%=pid%>" <%=Config%>>
                                                    <input value="<%=pid%>" name="pid" type="hidden">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Merchant ID*</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" name="memberid" id="member" class="form-control"
                                                           value="<%=memberid%>">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>IP Type</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <select class="form-control" name="type">
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
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>IP Address</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" name="ipAddress" class="form-control"
                                                           value="<%=ipAddress%>">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <p style="text-align: center;">
                                                <button type="submit" name="upload" value="upload"
                                                        style="display: inline-block!important;"
                                                        class="btn btn-default center-block">
                                                    <i class="fa fa-ban"></i>
                                                    &nbsp;&nbsp;Block IP
                                                </button>
                                                <button type="submit" class="btn btn-default center-block"
                                                        style="display: inline-block!important;">
                                                    <i class="fa fa-clock-o"></i>
                                                    &nbsp;&nbsp;Search
                                                </button>
                                            </p>
                                        </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Block IP List</strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-y: auto;">
                        <br>
                        <%

                            String currentblock = request.getParameter("currentblock");

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

                        <form name="BlacklistIpDelete" action="/partner/net/BlacklistIp?ctoken=<%=ctoken%>"
                              method="post">
                            <table id="myTable" class="display table table-striped table-bordered" width="100%"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr style="background-color: #7eccad; color: white;">
                                    <th valign="middle" align="center" style="text-align: center ;width: 2% "><input type="checkbox"
                                                                                                                     onClick="ToggleAll(this);"
                                                                                                                     name="alltrans">
                                    </th>
                                    <th valign="middle" align="center" style="text-align: center ;width: 15%">Sr No</th>
                                    <th valign="middle" align="center" style="text-align: center ;width: 25%">Member ID</th>
                                    <th valign="middle" align="center" style="text-align: center ;width: 40%">Ip Address</th>
                                    <th valign="middle" align="center" style="text-align: center ;width: 80%">IP Type</th>
                                    <th valign="middle" align="center" style="text-align: center ;width: 80%">Action Executor Id</th>
                                    <th valign="middle" align="center" style="text-align: center ;width: 80%">Action Executor Name</th>
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
                    out.println("<td data-label='Action Executor Id' align=center " + style + ">&nbsp;" + actionExecutorId + "</td>");
                    out.println("<td data-label='Action Executor Name' align=center " + style + ">&nbsp;" + actionExecutorName + "</td>");
                    out.println("</tr>");
                    pos++;

                }
                %>
                            </table>
                            <table width="100%">
                                <thead>
                                <tr>
                                    <td width="15%" align="center">
                                        <input type="hidden" name="memberid" value="<%=memberid%>">
                                        <input type="hidden" name="type" value="<%=selectIpVersion%>">
                                        <input type="hidden" name="ipAddress" value="<%=ipAddress%>">
                                        <input type="hidden" name="delete" value="delete"><input type="button"
                                                                                                 name="Unblock"
                                                                                                 class="btn btn-default center-block"
                                                                                                 value="Delete"
                                                                                                 onclick="return Delete();">

                                    </td>
                                </tr>
                                </thead>
                            </table>
                        </form>
                    </div>
                </div>
                <% int TotalPageNo;
                    if(paginationVO.getTotalRecords()%pagerecords!=0)
                    {
                        TotalPageNo=paginationVO.getTotalRecords()/pagerecords+1;
                    }
                    else
                    {
                        TotalPageNo=paginationVO.getTotalRecords()/pagerecords;
                    }
                %>
                <div id="showingid"><strong>Page number  <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                <div id="showingid"><strong>Total number of records  <%=paginationVO.getTotalRecords()%> </strong></div>


                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                    <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                    <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                    <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                    <jsp:param name="page" value="BlacklistIp"/>
                    <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
                <%
                        }
                        else
                        {
                            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                        }
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                    }
                %>
            </div>
        </div>
    </div>
</div>
</div>
</body>
<script>
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
    function Delete()
    {
        var checkboxes = document.getElementsByName("id");
        console.log("ID"+document.getElementsByName("id"));
        var total_boxes = checkboxes.length;
        flag = false;

        for(i=0; i<total_boxes; i++ )
        {
            if(checkboxes[i].checked)
            {
                flag= true;
                break;
            }
        }
        if(!flag)
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
