<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.AdminDetailsVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 3/9/15
  Time: 4:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <%--

    --%>
    <title>Admin Management> Admin List</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script language="javascript" type="text/javascript">
        function getAction(submitValue) {
            var msg= 'Are you sure you want to deactivate account?';
            console.log("Inside getAction");
            var button_value= document.getElementById([submitValue]).value;
            if(button_value =="Deactivate" )
            {
                if(confirm(msg))
                {
                    return true;
                }
                else
                {
                    event.preventDefault();
                    event.stopImmediatePropagation();
                }
            }
        }
    </script>
</head>
<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions=new Functions();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str="ctoken=" + ctoken;
        if (request.getParameter("adminid") != null) str = str + "&adminid=" + request.getParameter("adminid");
        if (request.getParameter("username") != null) str = str + "&username=" + request.getParameter("username");

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);


%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Admin List
                <div style="float: right;">
                    <form action="/icici/adminsignup.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Create New Admin" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Admin
                        </button>
                    </form>
                </div>
            </div>
            <%
                String errormsg = (String) request.getAttribute("error");
                if(errormsg!=null)
                {
                    out.println("<br><font class=\"textb\"><b>");
                    errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                    out.println(errormsg);
                    out.println("</b></font>");
                    out.println("</td></tr></table>");
                }
            %>
            <form action="/icici/servlet/AdminDetailsList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:-2%;margin-right: 2.5% ">
                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td colspan="4">
                                        &nbsp;
                                    </td>
                                </tr>
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Admin Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="adminid"  class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Username</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="username" class="txtbox" >

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
                                    <td width="12%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
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

        String fmsg = (String)request.getAttribute("fmsg");
        if (fmsg == null)
        {
            fmsg = "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
            out.println(fmsg);
            out.println("</font></td></tr></table>");
        }
        String msg = (String)request.getAttribute("msg");
        if (msg== null)
        {
            msg= "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
            out.println(msg);
            out.println("</font></td></tr></table>");
        }
    %>
    <br><br>
    <%
        List<AdminDetailsVO> adminDetailsVOList=(List<AdminDetailsVO>)request.getAttribute("adminListVO");
        PaginationVO paginationVO=(PaginationVO)request.getAttribute("paginationVO");
        String message=(String)request.getAttribute("message");
        if(adminDetailsVOList!=null && adminDetailsVOList.size()>0)
        {
    %>
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center"  class="th0">Sr No</td>
            <td valign="middle" align="center"  class="th0">SignUp Time</td>
            <td valign="middle" align="center" class="th0">Admin ID</td>
            <td valign="middle" align="center" class="th0">Admin UserName</td>
            <td valign="middle" align="center" class="th0">Contact Email</td>
            <td valign="middle" align="center" class="th0" colspan="10">Action</td>
        </tr>
        </thead>
        <%
            String style="class=td1";
            String ext="light";
            int srno=0;
            for(AdminDetailsVO adminDetailsVO:adminDetailsVOList)
            {
                ++srno;
                if(srno%2==0)
                {
                    style="class=tr0";
                    ext="dark";
                }
                else
                {
                    style="class=tr1";
                    ext="light";
                }
        %>
        <form action="/icici/servlet/ForgotPasswordAdminList?ctoken=<%=ctoken%>" method="post">
            <tr>
                <td align="center" <%=style%>>
                    <%=srno%>
                </td>
                <td align="center" <%=style%>>
                    <%=adminDetailsVO.getSignUpTime()%>
                    <input type="hidden" name="signuptime" value="<%=adminDetailsVO.getSignUpTime()%>">
                </td>
                <td align="center" <%=style%> >
                    <%=adminDetailsVO.getAdminId()%>
                    <input type="hidden" name="adminid" value="<%=adminDetailsVO.getAdminId()%>">

                </td>
                <td align="center" <%=style%> >
                    <%=adminDetailsVO.getLogin()%>
                    <input type="hidden" name="login" value="<%=adminDetailsVO.getLogin()%>">

                </td>
                <td align="center" <%=style%> >
                        <%=adminDetailsVO.getContactEmails()%>
                    <input type="hidden" name="contact_emails" value=" <%=adminDetailsVO.getContactEmails()%>">


                <td align="center" <%=style%>>&nbsp;<input type="submit" class="gotoauto" value="ForgotPassword"></td> </form>
                <form onsubmit="getAction('submit_deactivated_<%=srno%>')" action="/icici/servlet/UpdateUserAccount?ctoken=<%=ctoken%>" method="post"  id="myforms" name="myforms">

                    <input type="hidden" name="signuptime" value="<%=adminDetailsVO.getSignUpTime()%>">
                    <input type="hidden" name="adminid" value="<%=adminDetailsVO.getAdminId()%>">
                    <input type="hidden" name="login" value="<%=adminDetailsVO.getLogin()%>">
                    <input type="hidden" name="contact_emails" value=" <%=adminDetailsVO.getContactEmails()%>">
                    <%
                        String value="";
                        if ("Y".equalsIgnoreCase(adminDetailsVO.getIsActive()))
                        {
                             value="Deactivate";
                             out.println("<input type=\"hidden\"  name=\"isActive\" value=\"N\">");
                        }
                        else if ("N".equalsIgnoreCase(adminDetailsVO.getIsActive()))
                        {
                            value="Activate";
                            out.println("<input type=\"hidden\"  name=\"isActive\" value=\"Y\">");
                        }
                    %>
                <td align="center" <%=style%>>&nbsp;
                    <input type="submit" class="gotoauto" value="<%=value%>" <%--onclick="return getAction(this)"--%> id="submit_deactivated_<%=srno%>" name="submit_deactivated"></td>
                </form>

            </tr>
        <%
            }
        %>
    </table>

    <table align=center valign=top>
        <tr>
            <td align=center>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                    <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                    <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                    <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
            </td>
        </tr>
    </table>
    <%
        }
        else if(functions.isValueNull(message))
        {
            out.println(Functions.NewShowConfirmation("Result",message));
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry","No Records Founds."));
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