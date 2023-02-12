<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.statussync.StatusSynchronization" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 6/2/14
  Time: 4:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({

        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();
//            $("#yourinput").datepicker( "setDate" , "7/11/2005" );

        });
    </script>
    <title>Settings> Status Synchronization</title>
</head>
<body>

<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<form action="/icici/statusSynchronization.jsp?ctoken=<%=ctoken%>" method="post" name="forms" >
<input type="hidden" value="<%=ctoken%>" name="ctoken">

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Status Synchronization
            </div>

            <table  align="center" width="85%" cellpadding="2" cellspacing="2" style="margin-left:55px; ">
                <tr>
                    <td>
                        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                        <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                            <tr>
                                <td colspan="4">&nbsp;</td>
                            </tr>
                            <tr>
                                <input type="hidden" name="isSubmitted" value="true">
                                <td width="2%" class="textb">&nbsp;</td>
                                <td width="30%" class="textb" >From Date</td>
                                <td width="0%" class="textb"></td>
                                <td width="25%" class="textb">
                                    <input type="text" readonly class="datepicker" name="fromdate"   value="<%=request.getParameter("fromdate")==null?"":request.getParameter("fromdate")%>">
                                </td>

                                <td width="2%" class="textb"></td>
                                <td width="45%" class="textb" >To Date</td>
                                <td width="4%" class="textb"></td>
                                <td width="35%" class="textb">
                                    <input type="text" readonly class="datepicker" name="todate"  value="<%=request.getParameter("todate")==null?"":request.getParameter("todate")%>">

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
                                <td width="0%" class="textb"></td>
                                <td width="22%" class="textb"></td>

                                <td width="2%" class="textb"></td>
                                <td width="45%" class="textb" ></td>
                                <td width="8%" class="textb"></td>
                                <td width="40%" class="textb" align="right">
                                    <button type="submit" class="buttonform" >
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Search
                                    </button>
                                </td>

                            </tr>
                            <tr>
                                <td width="2%" class="textb">&nbsp;</td>
                                <td width="20%" class="textb" ></td>
                                <td width="5%" class="textb"></td>
                                <td width="22%" class="textb" align="center">
                                </td>

                                <td width="10%" class="textb">&nbsp;</td>
                                <td width="40%" class="textb" ></td>
                                <td width="5%" class="textb"></td>
                                <td width="50%" class="textb" align="right">
                                </td>
                            </tr>

                        </table>
                    </tD>
                </tr>
            </table>
        </div>
    </div>
</div>
<div class="reporttable">
    <%


    String isSubmitted =  request.getParameter("isSubmitted");

    if("true".equals(isSubmitted))
    {
        if("".equals(request.getParameter("fromdate")) || "".equals(request.getParameter("todate")))
        {
            out.println("<center><font class=\"textb\">From date OR To date should not be empty.</font></center>");
            out.println(Functions.NewShowConfirmation("Sorry","No records found."));
        }
        else
        {

            String fromDate= request.getParameter("fromdate");
            String toDate= request.getParameter("todate");
            Functions functions =new Functions();
            StatusSynchronization statusSynchronization=new StatusSynchronization();
            String resMsg=statusSynchronization.allTransactionSynchronizationCron(fromDate,toDate);
            if (functions.isValueNull(resMsg))
            {
                out.println(resMsg);

            }
            else
            {
            out.println(Functions.NewShowConfirmation("Sorry","No records found."));
            }
        }
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));

    }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>



</body>
</html>