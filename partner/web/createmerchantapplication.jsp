<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: kajal
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
    //String memberid="";
    LinkedHashMap memberidDetails=null;
    Functions function = new Functions();
    String partnerid =nullToStr(request.getParameter("partnerid"));
    String partner_id = session.getAttribute("merchantid").toString();
    if(function.isValueNull(partnerid)){
        memberidDetails=partner.getPartnerMembersDetail(partnerid);
    }
    else{
        memberidDetails = partner.getSuperPartnerMembersDetail(partner_id);
    }
  int pageno=functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
  int pagerecords=functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String createmerchantapplication_List_Application = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_List_Application")) ? rb1.getString("createmerchantapplication_List_Application") : "List Of Application Member";
    String createmerchantapplication_PartnerID = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_PartnerID")) ? rb1.getString("createmerchantapplication_PartnerID") : "Partner ID";
    String createmerchantapplication_select_partnerid = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_select_partnerid")) ? rb1.getString("createmerchantapplication_select_partnerid") : "Select Partner ID";
    String createmerchantapplication_MemberID = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_MemberID")) ? rb1.getString("createmerchantapplication_MemberID") : "Member ID";
    String createmerchantapplication_select_MemberID = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_select_MemberID")) ? rb1.getString("createmerchantapplication_select_MemberID") : "Select Member ID";
    String createmerchantapplication_Search = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_Search")) ? rb1.getString("createmerchantapplication_Search") : "Search";
    String createmerchantapplication_Report = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_Report")) ? rb1.getString("createmerchantapplication_Report") : "Report Table";
    String createmerchantapplication_SrNo = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_SrNo")) ? rb1.getString("createmerchantapplication_SrNo") : "Sr No";
    String createmerchantapplication_MAF = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_MAF")) ? rb1.getString("createmerchantapplication_MAF") : "MAF";
    String createmerchantapplication_Speed = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_Speed")) ? rb1.getString("createmerchantapplication_Speed") : "Speed";
    String createmerchantapplication_Create = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_Create")) ? rb1.getString("createmerchantapplication_Create") : "Create";
    String createmerchantapplication_Showing_Page = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_Showing_Page")) ? rb1.getString("createmerchantapplication_Showing_Page") : "Showing Page";
    String createmerchantapplication_of = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_of")) ? rb1.getString("createmerchantapplication_of") : "of";
    String createmerchantapplication_records = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_records")) ? rb1.getString("createmerchantapplication_records") : "records";
    String createmerchantapplication_Sorry = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_Sorry")) ? rb1.getString("createmerchantapplication_Sorry") : "Sorry";
    String createmerchantapplication_no = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_no")) ? rb1.getString("createmerchantapplication_no") : "No records found";
    String createmerchantapplication_Filter = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_Filter")) ? rb1.getString("createmerchantapplication_Filter") : "Filter";
    String createmerchantapplication_fill = StringUtils.isNotEmpty(rb1.getString("createmerchantapplication_fill")) ? rb1.getString("createmerchantapplication_fill") : "Fill the required data for Application Member List";
%>
<html>
<head>
  <%--  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <title></title>
</head>
<script type="text/javascript">

    $(function ()
    {
        $('#partnerid').on('change', function (request, response)
        {
            $.ajax({
                url: "/partner/net/GetDetails",
                dataType: "json",
                data: {
                    partnerid: $('#partnerid').val(),
                    ctoken: $('#ctoken').val(),
                    method: "ApplicationManager",
                    term: request.term
                },
                success: function (data)
                {
                    $('#apptoid').find('option').not(':first').remove();
                    $.each(data.aaData, function (i, data)
                    {
                        var div_data = "<option value=" + data.value + ">" + data.text + "</option>";
                        console.log(div_data);
                        $(div_data).appendTo('#apptoid');
                    });
                }
            });
            minLength: 0
        });
    });
</script>
<%--javascript for update button unabled and disabled purpose--%>



<%--using for partner AppManagerStatus--%>
<body onClick="action();">
<script src='/partner/stylenew/BeforeAppManager.js'></script>


<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">

          <div class="pull-right">
            <div class="btn-group">
              <form action="/partner/net/ListofAppMember" method="post" name="forms" >

                <%
                  Enumeration<String> stringEnumerations=request.getParameterNames();
                  while(stringEnumerations.hasMoreElements())
                  {
                    String name=stringEnumerations.nextElement();
                    if("action".equals(name))
                    {
                      out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)+"'/>");
                    }
                    else
                      out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                  }
                %>
                <button class="addnewmember btn-xs" value="listofapplicationmember" name="submit" type="submit" name="B1" style="background: transparent;border: 0;">
                  <img style="height: 35px;" src="/partner/images/goBack.png">
                </button>

              </form>
            </div>
          </div>
          <br><br><br>
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=createmerchantapplication_List_Application%></strong></h2>
              <div class="additional-btn">
                <%--<form action="/partner/net/CreateMerchantApplication?ctoken=<%=ctoken%>" method="POST">
                    <button type="submit" class="btn btn-default" value="listofapplicationmember" name="submit" style="/*width: 250px; */font-size:14px;">
                        <i class="fa fa-sign-in"></i>
                        &nbsp;&nbsp;Create Merchant Application
                    </button>
                </form>--%>
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">

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

                <form action="/partner/net/CreateMerchantApplication?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partner_id%>"  id="partnerid">
                  <%!ApplicationManager applicationManager = new ApplicationManager();%>
                  <%List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getSuperPartnersNewMerchantApplicationManagerVO((String) session.getAttribute("merchantid"));
                    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                    String Style1= "";
                    String Style2= "";
                    String dISABLED_Id="";
                    if(Roles.contains("superpartner")){
                    Style1= "style=\"display: none\"";
                    }else{
                    dISABLED_Id=String.valueOf(session.getAttribute("merchantid"));
                    Style2= "style=\"display: none\"";
                    }
                      TreeMap<String,String> partneriddetails =null;
                      partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
                  %>

                    <div class="form-group col-md-4 has-feedback" <%=Style2%>>
                        <label class="col-sm-4 control-label"><%=createmerchantapplication_PartnerID%></label>
                        <div class="col-sm-8">
                            <select class="form-control" name="partnerid" id="pid">
                                <option value="" default><%=createmerchantapplication_select_partnerid%></option>
                                <%
                                    String Selected = "";
                                    for(String pid : partneriddetails.keySet())
                                    {
                                        if(pid.toString().equals(partnerid))
                                        {
                                            Selected="selected";
                                        }
                                        else
                                        {
                                            Selected="";
                                        }
                                %>
                                <option value="<%=pid%>" <%=Selected%>><%=partneriddetails.get(pid)%></option>
                                <%
                                    }
                                %>
                            </select>
                        </div>
                    </div>


                    <div class="form-group col-md-4 has-feedback" <%=Style1%>>
                        <label class="col-sm-4 control-label"><%=createmerchantapplication_PartnerID%></label>
                        <div class="col-sm-8">
                            <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                            <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                        </div>
                    </div>


                  <div class="form-group col-md-4">
                    <label class="col-sm-4 control-label"><%=createmerchantapplication_MemberID%></label>
                    <div class="col-sm-8">
                      <select name="apptoid" class="form-control" id="apptoid">
                        <option value=""><%=createmerchantapplication_select_MemberID%></option>
                        <%
                            String memberid = (String) request.getAttribute("apptoid");
                          for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
                          {
                              System.out.println(memberid);
                              if(functions.isValueNull(memberid) && memberid.equals((applicationManagerVO.getMemberId().split("_"))[0]))
                              {
                                 %>
                          <option value="<%=(applicationManagerVO.getMemberId().split("_"))[0]%>" selected> <%=(applicationManagerVO.getMemberId().split("_"))[0]%></option>
                          <%
                              }else{
                             %>
                          <option value="<%=(applicationManagerVO.getMemberId().split("_"))[0]%>"> <%=(applicationManagerVO.getMemberId().split("_"))[0]%></option>
                              <%}
                          }
                        %>
                      </select>
                    </div>
                  </div>

                   <%-- <div class="form-group col-md-8">
                        <label class="col-sm-3 control-label">&nbsp;&nbsp;<%=createmerchantapplication_MemberID%></label>
                        <div class="col-sm-6">
                            <input name="apptoid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                        </div>
                    </div>--%>


                  <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                  <div class="form-group col-md-3">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=createmerchantapplication_Search%></button>
                    </div>
                  </div>


                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <br>


      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=createmerchantapplication_Report%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
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

              <form action="/partner/net/PopulateApplication?ctoken=<%=ctoken%>" method="post">
                <input type="hidden" value="<%=pageno%>" name="SPageno">
                <input type="hidden" value="<%=pagerecords%>" name="SRecords">

                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=createmerchantapplication_SrNo%></b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=createmerchantapplication_PartnerID%></b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=createmerchantapplication_MemberID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=createmerchantapplication_MAF%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=createmerchantapplication_Speed%></b></td>
                  </tr>

                  </thead>

                  <%
                    StringBuffer requestParameter = new StringBuffer();
                    Enumeration<String> stringEnumeration = request.getParameterNames();
                    PartnerFunctions partnerFunctions = new PartnerFunctions();
                    while(stringEnumeration.hasMoreElements())
                    {
                      String name=stringEnumeration.nextElement();
                      if("SPageno".equals(name) || "SRecords".equals(name))
                      {

                      }
                      else
                        requestParameter.append("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                    }
                    requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                    requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                    for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList1)
                    {
                  %>

                  <tbody>

                  <tr id="maindata">
                    <td valign="middle" data-label="Sr No" align="center"><%=srno%></td>
                    <td valign="middle" data-label="Sr No" align="center"><%=partnerFunctions.getPartnerId((applicationManagerVO.getMemberId().split("_"))[0])%></td>
                    <td valign="middle" data-label="Member ID" align="center"><%=(applicationManagerVO.getMemberId().split("_"))[0]%></td>
                    <%
                      if(applicationManagerVO.getMemberId().contains("_MAF") || applicationManagerVO.getMemberId().contains("_SPEED"))
                      {
//                        System.out.println("inside MAF OR SPEED--");
                    %>
                    <%
                      //          if(applicationManagerVO.getMemberId().contains("_MAF") && !applicationManagerVO.getMaf_Status().equals(ApplicationStatus.SAVED) && !applicationManagerVO.getMaf_Status().equals(ApplicationStatus.SUBMIT))
                      if(applicationManagerVO.getMemberId().contains("_MAF") && !functions.isValueNull(applicationManagerVO.getMaf_Status()))
                      {
//                        System.out.println("IF Inside MAF");
                    %>

                    <td valign="middle" data-label="MAF" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"MAF_status"%>" name="action"><%=createmerchantapplication_Create%></button>
                      <%
                        out.println(requestParameter);
                      %>
                    </td>
                    <%
                    }
                    else
                    {
//                      System.out.println("ELSE Inside MAF");
                    %>


                    <td valign="middle" data-label="MAF" align="center">
                      <button type="submit" class="button btn btn-default" style="background:grey" value="<%=applicationManagerVO.getMemberId()+"MAF_status"%>" name="action" disabled><%=createmerchantapplication_Create%></button>
                      <%
                        out.println(requestParameter);
                      %>
                    </td>

                    <%
                      }
                    %>
                    <%
                      //          if(applicationManagerVO.getMemberId().contains("_SPEED") && !applicationManagerVO.getSpeed_status().equals(ApplicationStatus.STEP1_SAVED) && !applicationManagerVO.getSpeed_status().equals(ApplicationStatus.STEP1_SUBMIT))
                      if(applicationManagerVO.getMemberId().contains("_SPEED") && !functions.isValueNull(applicationManagerVO.getSpeed_status()))
                      {
//                        System.out.println("IF Inside SPEED");
                    %>


                    <td valign="middle" data-label="Speed" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"SPEED_status"%>" name="action"><%=createmerchantapplication_Create%></button>
                      <%
                        out.println(requestParameter);
                      %>
                    </td>
                    <%
                    }
                    else
                    {
                    %>


                    <td valign="middle" data-label="Speed" align="center">
                      <button type="submit" class="button btn btn-default" style="background:grey" value="<%=applicationManagerVO.getMemberId()+"SPEED_status"%>" name="action" disabled><%=createmerchantapplication_Create%></button>
                      <%
                        out.println(requestParameter);
                      %>
                    </td>
                    <%
                      }
                    }
                    else
                    {
                    %>


                    <td valign="middle" data-label="MAF" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"MAF_status"%>" name="action"><%=createmerchantapplication_Create%></button>
                      <%
                        out.println(requestParameter);
                      %>
                    </td>

                    <td valign="middle" data-label="Speed" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"SPEED_status"%>" name="action"><%=createmerchantapplication_Create%></button>
                      <%
                        out.println(requestParameter);
                      %>
                    </td>
                    <%
                      }
                    %>
                    <%
                        srno++;

                      }

                    %>

                  </tr>





                  </tbody>

                </table>

              </form>

              <%--<table align=center valign=top style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                      <td  align="left" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Total Records : <%=totalrecords%></td>
                      <td  align="right" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Page No : <%=pageno%></td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                      <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  </tr>
                  </thead>

                  <tbody>
                  <tr>
                      <td align=center>
                          <jsp:include page="page.jsp" flush="true">
                              <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                              <jsp:param name="numrows" value="<%=pagerecords%>"/>
                              <jsp:param name="pageno" value="<%=pageno%>"/>
                              <jsp:param name="str" value="<%=str%>"/>
                              <jsp:param name="page" value="MemberDetailList"/>
                              <jsp:param name="currentblock" value="<%=currentblock%>"/>
                              <jsp:param name="orderby" value=""/>
                          </jsp:include>
                      </td>
                  </tr>
                  </tbody>
              </table>--%>



            </div>
          </div>
        </div>
      </div>

      <div id="showingid"><strong><%=createmerchantapplication_Showing_Page%> <%=paginationVO.getPageNo()%> <%=createmerchantapplication_of%> <%=paginationVO.getTotalRecords()%> <%=createmerchantapplication_records%></strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="CreateMerchantApplication"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>


      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(createmerchantapplication_Sorry,createmerchantapplication_no));
          }
        }

        else
        {
          out.println(Functions.NewShowConfirmation1(createmerchantapplication_Filter,createmerchantapplication_fill));
        }

      %>
        <%!
            public static String nullToStr(String str)
            {
                if(str == null)
                    return "";
                return str;
            }
        %>

    </div>
  </div>
</div>

</body>
</html>