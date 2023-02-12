<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.ProfileManagementManager" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.riskRuleVOs.ProfileVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Sagar
  Date: 25/08/2015
  Time: 21:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","riskProfile");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String riskProfile_Risk_Profile = StringUtils.isNotEmpty(rb1.getString("riskProfile_Risk_Profile")) ? rb1.getString("riskProfile_Risk_Profile") : "Risk Profile";
    String riskProfile_ProfileID = StringUtils.isNotEmpty(rb1.getString("riskProfile_ProfileID")) ? rb1.getString("riskProfile_ProfileID") : "Profile ID";
    String riskProfile_All = StringUtils.isNotEmpty(rb1.getString("riskProfile_All")) ? rb1.getString("riskProfile_All") : "All";
    String riskProfile_Search = StringUtils.isNotEmpty(rb1.getString("riskProfile_Search")) ? rb1.getString("riskProfile_Search") : "Search";
    String riskProfile_Report_Table = StringUtils.isNotEmpty(rb1.getString("riskProfile_Report_Table")) ? rb1.getString("riskProfile_Report_Table") : "Report Table";
    String riskProfile_SrNo = StringUtils.isNotEmpty(rb1.getString("riskProfile_SrNo")) ? rb1.getString("riskProfile_SrNo") : "Sr No";
    String riskProfile_Profile_Name = StringUtils.isNotEmpty(rb1.getString("riskProfile_Profile_Name")) ? rb1.getString("riskProfile_Profile_Name") : "Profile Name";
    String riskProfile_Action = StringUtils.isNotEmpty(rb1.getString("riskProfile_Action")) ? rb1.getString("riskProfile_Action") : "Action";
    String riskProfile_Showing_Page = StringUtils.isNotEmpty(rb1.getString("riskProfile_Showing_Page")) ? rb1.getString("riskProfile_Showing_Page") : "Showing Page";
    String riskProfile_of = StringUtils.isNotEmpty(rb1.getString("riskProfile_of")) ? rb1.getString("riskProfile_of") : "of";
    String riskProfile_records = StringUtils.isNotEmpty(rb1.getString("riskProfile_records")) ? rb1.getString("riskProfile_records") : "records";
    String riskProfile_Sorry = StringUtils.isNotEmpty(rb1.getString("riskProfile_Sorry")) ? rb1.getString("riskProfile_Sorry") : "Sorry";
    String riskProfile_no = StringUtils.isNotEmpty(rb1.getString("riskProfile_no")) ? rb1.getString("riskProfile_no") : "No records found";
    String riskProfile_Filter = StringUtils.isNotEmpty(rb1.getString("riskProfile_Filter")) ? rb1.getString("riskProfile_Filter") : "Filter";
    String riskProfile_please = StringUtils.isNotEmpty(rb1.getString("riskProfile_please")) ? rb1.getString("riskProfile_please") : "Please Fill the Data for Risk Profile";
    String riskProfile_View = StringUtils.isNotEmpty(rb1.getString("riskProfile_View")) ? rb1.getString("riskProfile_View") : "View";
    String riskProfile_Edit = StringUtils.isNotEmpty(rb1.getString("riskProfile_Edit")) ? rb1.getString("riskProfile_Edit") : "Edit";
    String riskProfile_Delete = StringUtils.isNotEmpty(rb1.getString("riskProfile_Delete")) ? rb1.getString("riskProfile_Delete") : "Delete";
    String riskProfile_page_no = StringUtils.isNotEmpty(rb1.getString("riskProfile_page_no")) ? rb1.getString("riskProfile_page_no") : "Page number";
    String riskProfile_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("riskProfile_total_no_of_records")) ? rb1.getString("riskProfile_total_no_of_records") : "Total number of records";

%>
<html>
<head>
    <title><%=company%> Rule Management> Risk Profile</title>
    <style type="text/css">
        @media(max-width: 1040px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit!important;
            }
        }

        @media(max-width: 640px) {
            .additional-btn button:nth-of-type(2) {
                margin-top: 10px;
            }
        }

        .textb {
            color: red;
            font-weight: bold;
        }

        @media (max-width: 767px){
            #mainrow{
                margin-top: 100px;
            }
        }

    </style>
</head>
<body>

<%
    session.setAttribute("submit","riskProfile");
%>
<%!Functions functions = new Functions();
%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/net/SingleRiskRuleDetails?ctoken=<%=ctoken%>" method="POST" name="myForm">
                        <button class="btn-xs" type="submit" value="1_Add" name="action" onclick="cancel('<%=ctoken%>')" style="background: transparent;border: 0;">
                            <img style="height: 45px;width: 200px;" src="/partner/images/downloadxml.png">
                        </button>
                        <button class="btn-xs" type="submit" value="1_Add" name="action" onclick="add('<%=ctoken%>')" style="background: transparent;border: 0;">
                            <img style="height: 45px;width: 200px;" src="/partner/images/addnewprofile.png">
                        </button>
                    </form>
                </div>
            </div>


            <div class="row" id="mainrow">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=riskProfile_Risk_Profile%></strong></h2>
                            <div class="additional-btn">
                                <%--<form action="/partner/net/SingleRiskRuleDetails?ctoken=<%=ctoken%>" method="POST" name="myForm">
                                    <button type="submit" class="btn btn-default" style="/*width:250px;*/ font-size:14px" name="action" value="1_Add" onclick="cancel('<%=ctoken%>')">
                                        <i class="fa fa-download"></i>
                                        &nbsp;&nbsp;Download XML
                                    </button>
                                    <button type="submit" class="btn btn-default" style="/*width:250px;*/ font-size:14px" name="action" value="1_Add" onclick="add('<%=ctoken%>')">
                                        <i class="fa fa-sign-in"></i>
                                        &nbsp;&nbsp;Add New Profile
                                    </button>
                                </form>--%>
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>



                        <form action="/partner/net/RiskProfileList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                            <%
                                ProfileManagementManager profileManagementManager=new ProfileManagementManager();
                                List<ProfileVO> riskProfileVOList= profileManagementManager.getListOfRiskProfileVO(session.getAttribute("merchantid").toString(), null, " profileid ASC", null);
                            %>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <%
                                        if(request.getAttribute("error")!=null)
                                        {
                                            ValidationErrorList error = (ValidationErrorList) request.getAttribute("error");
                                            for (ValidationException errorList : error.errors())
                                            {
                                                //out.println("<center><font class=\"form-control\" style=\"background-color: #d0f1e4;\">" + errorList.getMessage() + "</font></center>");
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorList.getMessage() + "</h5>");
                                            }
                                        }
                                        if(request.getAttribute("catchError")!=null)
                                        {
                                            //out.println("<center><font class=\"form-control\" style=\"background-color: #d0f1e4;\">" + request.getAttribute("catchError") + "</font></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                        }
                                        if(request.getAttribute("DELETED")!=null)
                                        {
                                            //out.println("<center><font class=\"form-control\" style=\"background-color: #d0f1e4;\">Deleted Successfully</font></center>");
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;Deleted Successfully</h5>");
                                        }
                                        if(request.getAttribute("update")!=null)
                                        {
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;Updated successfully</h5>");
                                        }
                                        //Created new error message for inserted data
                                        if(request.getAttribute("insert")!=null)
                                        {
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;Risk Profile added successfully</h5>");
                                        }

                                        String msg = (String)request.getAttribute("msg");
                                        if(msg!=null)
                                        {
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+msg+"</h5>");
                                        }

                                    %>

                                    <br>

                                    <div class="form-group col-md-8">
                                        <label class="col-sm-3 control-label"><%=riskProfile_ProfileID%></label>
                                        <div class="col-sm-6">
                                            <select name="profileid" class="form-control">
                                                <option value=""><%=riskProfile_All%></option>
                                                <%
                                                    for(ProfileVO profileVO : riskProfileVOList)
                                                    {
                                                        out.println("<option value=\""+profileVO.getId()+"\""+(profileVO.getId().equals(request.getParameter("profileid"))?"selected":"")+">"+profileVO.getId()+"</option>");
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>


                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=riskProfile_Search%></button>
                                        </div>
                                    </div>



                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>



            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=riskProfile_Report_Table%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">

                            <%
                                if(request.getAttribute("riskProfileVOList")!=null)
                                {
                                    List<ProfileVO> riskProfileVOListInside= (List<ProfileVO>) request.getAttribute("riskProfileVOList");
                                    if(riskProfileVOListInside!=null && !riskProfileVOListInside.isEmpty())
                                    {
                                        PaginationVO paginationVO  = (PaginationVO) request.getAttribute("paginationVO");
                                        paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                            %>

                            <form action="/partner/net/SingleRiskRuleDetails?ctoken=<%=ctoken%>" method="POST">
                                <input type="hidden" name="profileid" value="<%=functions.isValueNull(request.getParameter("profileid"))?request.getParameter("profileid"):""%>"/>

                                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=riskProfile_SrNo%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=riskProfile_ProfileID%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=riskProfile_Profile_Name%></b></td>
                                        <td  valign="middle" align="center" colspan="3" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=riskProfile_Action%></b></td>


                                    </tr>
                                    </thead>

                                    <%
                                        int srNo=1;

                                        for(ProfileVO profileVO:riskProfileVOListInside)
                                        {
                                    %>


                                    <tr>
                                        <td valign="middle" align="center" data-label="Sr No" style="vertical-align: middle;"><%=srNo%></td>
                                        <td valign="middle" align="center" data-label="Profile ID" style="vertical-align: middle;"><%=profileVO.getId()%></td>
                                        <td valign="middle" align="center" data-label="Profile Name" style="vertical-align: middle;"><%=profileVO.getName()%></td>
                                        <td valign="middle" align="center" data-label="Action"><button type="submit" class="btn btn-default"  name="action" value="<%=profileVO.getId()%>_View" ><%=riskProfile_View%></button></td>
                                        <td valign="middle" align="center" data-label="Action"><button type="submit" class="btn btn-default"  name="action" value="<%=profileVO.getId()%>_Edit"><%=riskProfile_Edit%></button></td>
                                        <td valign="middle" align="center" data-label="Action"><button type="submit" class="btn btn-default"  name="action" value="<%=profileVO.getId()%>_Delete" onclick="return Delete(this);"><%=riskProfile_Delete%></button></td>

                                    </tr>
                                    <%
                                            srNo++;
                                        }
                                    %>


                                </table>



                            </form>

                            <%
                                int TotalPageNo;
                                if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
                                {
                                    TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
                                }
                                else
                                {
                                    TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
                                }
                            %>
                            <div id="showingid"><strong><%=riskProfile_page_no%> <%=paginationVO.getPageNo()%>  <%=riskProfile_of%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                            <div id="showingid"><strong><%=riskProfile_total_no_of_records%>      <%=paginationVO.getTotalRecords()%> </strong></div>
                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                                <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                                <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                                <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                                <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                                <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>
                        </div>
                        <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(riskProfile_Sorry,riskProfile_no));
                                }
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1(riskProfile_Filter,riskProfile_please));
                            }
                        %>

                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

<script language="javascript">
    function Delete(ctoken)
    {
        if (confirm("Do you really want to Delete this User."))
        {
            return true;
        }
        else
            return false;
    }
</script>

<script>
    function cancel(ctoken) {
        document.myForm.action="/partner/net/GenerateRiskProfileXML?ctoken="+ctoken;
    }
    function add(ctoken) {
        document.myForm.action="/partner/net/SingleRiskRuleDetails?ctoken="+ctoken;
    }
</script>

</body>
</html>