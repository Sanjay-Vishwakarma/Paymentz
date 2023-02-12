<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.enums.ApplicationStatus" %>
<%@ page import="com.manager.vo.PaginationVO" %>

<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>

<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2/23/15
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%!
    private Functions functions=new Functions();
%>
<%
    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
%>
<html>
<head>
    <title>Application Manager> Merchant Application Form</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>

<%--javascript for update button unabled and disabled purpose--%>



<%--using for admin AppManagerStatus--%>
<body onClick="action();" >
<script src='/icici/stylenew/BeforeAppManager.js'></script>
<div class="row">
    <div class="col-lg-12" style="margin-top: 8%">
        <div class="panel panel-default" style="margin-top: 0%">
            <div class="panel-heading" >
                List Of Application Member

            </div>
            <%
                if(request.getParameter("MES")!=null)
                {
                    String mes=request.getParameter("MES");
                    if(mes.equals("ERR"))
                    {
                        ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                        for(ValidationException errorList : error.errors())
                        {
                            out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
                        }
                    }

                }
            %>
            <form action="/icici/servlet/ListofAppMember?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">

                <%!
                    ApplicationManager applicationManager = new ApplicationManager();
                %>
                <%List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getApplicationManagerVO(null);%>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">

                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">

<%--
                                        <input name="apptoid" class="txtbox" id="mid">
--%>
                                        <select name="apptoid" class="txtbox">
                                            <option value="">Select Member Id</option>
                                            <%
                                                try
                                                {

                                                    String memberId = (String) request.getAttribute("memberId");

                                                    for (ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
                                                    {
                                                        //out.println("<option value=\""+applicationManagerVO.getMemberId()+"\" "+((applicationManagerVO.getMemberId().equals(request.getParameter("apptoid")))?"selected":"")+">"+applicationManagerVO.getApplicationId()+"_"+applicationManagerVO.getMemberId()+"</option>");
                                                        if (functions.isValueNull(memberId) && memberId.equals(applicationManagerVO.getMemberId().split("_")[0]))
                                                        {
                                                            out.println("<option value=\"" + (applicationManagerVO.getMemberId().split("_"))[0] + "\" selected>" + (applicationManagerVO.getMemberId().split("_"))[0] + "</option>");
                                                        }
                                                        else if(functions.isValueNull(applicationManagerVO.getMemberId()))
                                                        {
                                                            out.println("<option value=\"" + (applicationManagerVO.getMemberId().split("_"))[0] + "\">" + (applicationManagerVO.getMemberId().split("_"))[0] + "</option>");
                                                        }
                                                    }
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            %>
                                        </select>

                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" ></td>
                                    <td width="3%" class="textb"><button type="submit" class="buttonform" >
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Search
                                    </button></td>
                                    <td width="10%" class="textb">

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

    <%
        //blank the Session Value after Submit
        session.setAttribute("applicationManagerVO",null);
        session.setAttribute("navigationVO",null);
        session.setAttribute("apptoid",null);
        session.setAttribute("validationErrorList",null);

        List<ApplicationManagerVO> applicationManagerVOList1 = (List<ApplicationManagerVO>) request.getAttribute("applicationManagerVOs");
        if(applicationManagerVOList1 !=null)
        {


            if(applicationManagerVOList1.size()>0)
            {
                PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

                int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
    %>
    <form action="/icici/servlet/PopulateApplication?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" value="<%=pageno%>" name="SPageno">
        <input type="hidden" value="<%=pagerecords%>" name="SRecords">
        <center><h4 class="textb"><b>List Of Application Member</b></h4></center>
        <table align=center width="100%" border="1" class="table table-striped table-bordered table-green dataTable">
            <tr>
                <td width="2%"valign="middle" align="center" class="th0">Sr&nbsp;No</td>
                <td width="15%" valign="middle" align="center" class="th0">Member Id</td>
                <td width="15%" valign="middle" align="center" class="th0">Application ID</td>
                <td width="25%" valign="middle" align="center" class="th0">Status</td>
                <td width="25%" valign="middle" align="center" class="th0">User</td>
                <td colspan="20%" valign="middle" align="center" class="th0" >Action</td>
            </tr>
            <%
                for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList1)
                {
                  if(functions.isValueNull(applicationManagerVO.getStatus()) && !ApplicationStatus.STEP1_SAVED.toString().equals(applicationManagerVO.getStatus()) && !ApplicationStatus.STEP1_SUBMIT.toString().equals(applicationManagerVO.getStatus()))
                  {
            %>
            <tr>

                <td class="tr0" align="center"><%=srno%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getMemberId()%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getApplicationId()%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getStatus()%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getUser()%></td>
                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=applicationManagerVO.getMemberId()+"_View"%>" name="action">View</button>
                </td>
                <%
                    if(functions.isValueNull(applicationManagerVO.getAppliedToModify()) && "Y".equals(applicationManagerVO.getAppliedToModify()))
                    {
                %>

                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=applicationManagerVO.getMemberId()+"_Update"%>" name="action">To Modify</button>
                </td>
                <%
                }
                else if((functions.isValueNull(applicationManagerVO.getStatus()) && (ApplicationStatus.SAVED.name().equals(applicationManagerVO.getStatus())) || ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus())))
                {
                %>
                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=applicationManagerVO.getMemberId()+"_Edit"%>" name="action">Update</button>
                </td>
                <%
                    }
                    else
                    {
                %>
                <td class="tr0" align="center">
                    <button type="submit" class="button" style="background:grey" value="<%=applicationManagerVO.getMemberId()+"_Edit"%>" name="action" disabled>Update</button>
                </td>
                <%
                    }
                %>
                <%
                    srno++;
                    }
                %>
            </tr>
            <%
                if(functions.isValueNull(applicationManagerVO.getSpeed_status()))
                {
            %>
            <tr>

                <td class="tr0" align="center"><%=srno%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getMemberId()%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getApplicationId()%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getSpeed_status()%></td>
                <td class="tr0" align="center"><%=applicationManagerVO.getSpeed_user()%></td>
                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=applicationManagerVO.getMemberId()+"_SPEED_View"%>" name="action">View</button>
                </td>
                <%
                    if(functions.isValueNull(applicationManagerVO.getAppliedToModify()) && "Y".equals(applicationManagerVO.getAppliedToModify()))
                    {
                %>

                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=applicationManagerVO.getMemberId()+"_SPEED_Update"%>" name="action">To Modify</button>
                </td>
                <%
                }
                else if((functions.isValueNull(applicationManagerVO.getSpeed_status()) || ApplicationStatus.STEP1_SAVED.name().equals(applicationManagerVO.getSpeed_status())) && !ApplicationStatus.VERIFIED.name().equals(applicationManagerVO.getSpeed_status()))
                {
                %>
                <td class="tr0" align="center">
                    <button type="submit" class="button" value="<%=applicationManagerVO.getMemberId()+"_SPEED_Edit"%>" name="action">Update</button>
                    <input type="hidden" name="update" value="update">
                </td>
                <%
                    }
                    else
                    {
                %>
                <td class="tr0" align="center">
                    <button type="submit"  class="button" style="background:grey" value="<%=applicationManagerVO.getMemberId()+"_SPEED_Edit"%>" name="action" disabled>Update</button>
                </td>
                <%
                    }
                %>
            </tr>

            <%
                    srno++;
                    }
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
            out.println(Functions.NewShowConfirmation("Filter","Fill the required data for Application Member List"));
        }

    %>

</div>
</body>
</html>