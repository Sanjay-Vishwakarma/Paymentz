<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.GatewayManager" %>
<%@ page import="java.util.List" %>
<%@ page import="com.manager.vo.gatewayVOs.GatewayAccountVO" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.manager.vo.BankRollingReserveVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Niket
  Date: 9/4/14
  Time: 2:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <%--Datepicker css format--%>
        <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
        <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

        <script type="text/javascript">
            $('#sandbox-container input').datepicker({
            });
        </script>
        <script>
            $(function() {
                $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>
    <title>Admin |Bank Settlement</title>
</head>
<body>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Bank Rolling Reserve Manager
                <div style="float: right;">
                    <form action="/icici/bankRollingReserveMaster.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" class="addnewmember" value="Add" name="submit" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Rolling Reserve
                        </button>
                    </form>
                </div>
            </div>
            <%
                if(request.getParameter("MES")!=null)
                {
                    String mes=request.getParameter("MES");
                    if(mes.equals("ERR"))
                    {
                        ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                        for(Object errorList : error.errors())
                        {
                            ValidationException ve = (ValidationException) errorList;
                            out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                        }
                    }

                }
            %>
            <form action="/icici/servlet/BankRollingReserveList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%!
                    Logger logger = new Logger("test");
                %>
                <%
                    GatewayManager gatewayManager = new GatewayManager();

                    List<GatewayAccountVO> gatewayAccountVOList= gatewayManager.getListOfAllGatewayAccount();

                %>
                <%
                    try
                    {
                        session.setAttribute("submit","Reports");
                        String selected="";

                        String fromdate = null;
                        String todate = null;
                        try
                        {
                            Date date = new Date();
                            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

                            String Date = originalFormat.format(date);

                            fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? "" : request.getParameter("fromdate");
                            todate = Functions.checkStringNull(request.getParameter("todate")) == null ? "" : request.getParameter("todate");
                        }
                        catch (Exception e)
                        {
                            logger.error("JSP page exception ::",e);
                        }
                %>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="90%"  align="center" style="margin-left:1% ">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="9%" class="textb" >From</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input size="6" name="fromdate" style="width:100px" readonly class="datepicker" value="<%=fromdate%>" >

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="9%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input size="6" name="todate" style="width:100px" readonly class="datepicker" value="<%=todate%>">

                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Account Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select name="accountid"><option value="" selected></option>
                                            <%
                                                for(GatewayAccountVO gatewayAccountVO : gatewayAccountVOList)
                                                {
                                                    GatewayAccount gatewayAccount = gatewayAccountVO.getGatewayAccount();
                                                    int accountId  = gatewayAccount.getAccountId();
                                                    String merchantId = gatewayAccount.getMerchantId();
                                                    String displayName = gatewayAccount.getDisplayName();
                                                    out.println("<option value=\""+accountId+"\" >"+accountId+"-"+merchantId+"-"+displayName+"</option>");
                                                }
                                            %>
                                        </select>

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
                                    <td width="12%" class="textb"></td>
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
                                    <td width="12%" class="textb" align="right">
                                        <button type="submit" class="buttonform" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>

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

        List<BankRollingReserveVO> bankRollingReserveVOList = (List<BankRollingReserveVO>) request.getAttribute("BankRollingReserveVOs");
        if(bankRollingReserveVOList !=null)
        {
            if(bankRollingReserveVOList.size()>0)
            {
                PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

                int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
%>
<form action="/icici/servlet/ViewOrEditBankRollingReserve?ctoken=<%=ctoken%>" method="post">
    <center><h4 class="textb"><b>Bank Rolling Reserve Release List</b></h4></center>
    <table align=center width="50%" border="1" class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td width="2%"valign="middle" align="center" class="th0">Sr&nbsp;No</td>
            <td width="8%" valign="middle" align="center" class="th0">Account Id</td>
            <td width="8%" valign="middle" align="center" class="th0">Rolling&nbsp;Release&nbsp;Date</td>
            <td width="8%" valign="middle" align="center" class="th0" colspan="2">Action</td>
        </tr>
        <%
            for(BankRollingReserveVO bankRollingReserveVO : bankRollingReserveVOList)
            {

        %>
        <tr>

            <td class="tr0" align="center"><%=srno%></td>
            <td class="tr0" align="center"><%=bankRollingReserveVO.getAccountId()%></td>
            <td class="tr0" align="center"><%=bankRollingReserveVO.getRollingReserveDateUpTo()%></td>
            <td  class="tr0" align="center">
                <button type="submit" class="button" value="<%=bankRollingReserveVO.getBankRollingReserveId()+"_View"%>" name="action">View</button>
            </td>
            <td class="tr0" align="center">
                <button type="submit" class="button" value="<%=bankRollingReserveVO.getBankRollingReserveId()+"_Edit"%>" name="action">Edit</button>
            </td>
        </tr>

        <%
                srno++;
            }
        %>
    </table>
</form>
    <div >
        <center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </center>
    </div>
<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry","No records found"));
    }
}
else
{
    //action specific code
    BankRollingReserveVO singleBankRollingReserveVO= (BankRollingReserveVO) request.getAttribute("singleBankRollingReserveVO");
    if(request.getParameter("UPDATE")!=null)
    {
        String update=request.getParameter("UPDATE");
        if(update.equals("Success"))
        {
            out.println("<center><font class=\"textb\">Bank rolling reserve updated</font></center><br>");
        }
    }
    if(singleBankRollingReserveVO!=null || ("Add").equals(request.getParameter("submit")))
    {


        String bankRollingReserveId = "";
        String accountId = "";
        String rollingReserveDateUpTo = "";
        String rollingReleaseTime = "";

        ActionVO actionVO = null;
        if (("Add").equals(request.getParameter("submit")))
        {
            actionVO= new ActionVO();
            actionVO.setAdd();
        }
        if (!("Add").equals(request.getParameter("submit")))
        {
            actionVO = (ActionVO) request.getAttribute("actionVO");
            bankRollingReserveId = singleBankRollingReserveVO.getBankRollingReserveId();
            accountId = singleBankRollingReserveVO.getAccountId();
            rollingReserveDateUpTo = singleBankRollingReserveVO.getRollingReserveDateUpTo();
            rollingReleaseTime=singleBankRollingReserveVO.getRollingRelease_time();

            session.setAttribute("singleBankRollingReserveVO",singleBankRollingReserveVO);
        }
%>
<form action="/icici/servlet/UpdateBankRollingReserve?ctoken=<%=ctoken%>" method="post">
    <table align=center width="80%" border="0">
        <tr>
            <td colspan="2"><center><B><%if (actionVO.isView())
            {%>
                View Bank Rolling Reserve Release
                <%
                    }
                    if (actionVO.isEdit())
                    {
                %>
                Edit Bank Rolling Reserve Release
                <%
                    }
                    if(actionVO.isAdd())
                    {
                %>
                Add New Rolling Reserve Release
                <%
                    }
                %>
            </B></center> </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%if (!actionVO.isAdd()){%>
        <tr class="tr0">
            <td valign="middle" align="center" class="textb">Id</td> <td class="td0"><input type="text" class="txtbox" name="bankreceivedid" value="<%=bankRollingReserveId%>"  readonly=""></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%}%>

        <tr class="tr0">
            <td valign="middle" align="center" class="textb">AccountId</td> <td class="textb"><select type="text" name="accountid"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
             <option value="" selected></option>
            <%

                for(GatewayAccountVO gatewayAccountVO : gatewayAccountVOList)
                {
                    selected="";
                    if(gatewayAccountVO.getAccountId().equals(accountId))
                    {
                        selected="selected";
                    }
                    GatewayAccount gatewayAccount = gatewayAccountVO.getGatewayAccount();
                    int accountid  = gatewayAccount.getAccountId();
                    String merchantid = gatewayAccount.getMerchantId();
                    String displayName = gatewayAccount.getDisplayName();
                    out.println("<option value=\""+accountid+"\" "+selected+">"+accountid+"-"+merchantid+"-"+displayName+"</option>");
                }
            %>
        </select>
        </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td valign="middle" align="center" class="textb">Rolling&nbsp;Release&nbsp;Date</td>  <td class="textb"><input type="text" name="rollingreservedateupto" class="datepicker" readonly value="<%=rollingReserveDateUpTo%>" <%if (actionVO.isView()){%> disabled><%}%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%if (actionVO.isEdit())
        {
        %>
        <tr class="tr0">
            <td colspan="2" valign="middle" align="center"  ><button type="submit" class="buttonform" name="action" value="<%=bankRollingReserveId%>_Update">Update</button></td>
        </tr>
        <%
            }
            if (actionVO.isAdd())
            {
        %>
        <tr class="tr0">
            <td colspan="2" valign="middle" align="center"  ><input type="hidden" name="submit" value="Add"><button type="submit" class="buttonform" name="action" value="1_Add">Add</button></td>
        </tr>
        <%
            }
        %>
    </table>
</form>
<%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Filter","fill in the required data for Bank Settlement List"));
            }
        }
    }

    catch (Exception e)
    {
        logger.error("bankRollingReserveMaster.jsp error::",e);
    }

%>
</div>
</body>
</html>