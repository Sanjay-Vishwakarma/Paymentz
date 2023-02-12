<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.enums.ApplicationStatus" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="top.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: kajal
  Date: 2/23/15
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>

<%!
  private Functions functions=new Functions();
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","ListofAppMember");
  PartnerFunctions partnerFunctions = new PartnerFunctions();
%>
<%
  int pageno=functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
  int pagerecords=functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);

  String listofapplicationmemeber_Merchant_Application_Form = rb1.getString("listofapplicationmemeber_Merchant_Application_Form");
  String listofapplicationmemeber_PartnerID = rb1.getString("listofapplicationmemeber_PartnerID");
  String listofapplicationmemeber_PartnerId = rb1.getString("listofapplicationmemeber_PartnerId");
  String listofapplicationmemeber_Select_Partner_ID = rb1.getString("listofapplicationmemeber_Select_Partner_ID");
  String listofapplicationmemeber_MerchantID = rb1.getString("listofapplicationmemeber_MerchantID");
  String listofapplicationmemeber_Select_Merchant_ID = rb1.getString("listofapplicationmemeber_Select_Merchant_ID");
  String listofapplicationmemeber_Search = rb1.getString("listofapplicationmemeber_Search");
  String listofapplicationmemeber_ReportTable = rb1.getString("listofapplicationmemeber_ReportTable");
  String listofapplicationmemeber_MemberID = rb1.getString("listofapplicationmemeber_MemberID");
  String listofapplicationmemeber_Application_ID = rb1.getString("listofapplicationmemeber_Application_ID");
  String listofapplicationmemeber_Status = rb1.getString("listofapplicationmemeber_Status");
  String listofapplicationmemeber_User = rb1.getString("listofapplicationmemeber_User");
  String listofapplicationmemeber_Action = rb1.getString("listofapplicationmemeber_Action");
  String listofapplicationmemeber_View = rb1.getString("listofapplicationmemeber_View");
  String listofapplicationmemeber_ToModify = rb1.getString("listofapplicationmemeber_ToModify");
  String listofapplicationmemeber_Update = rb1.getString("listofapplicationmemeber_Update");
  String listofapplicationmemeber_ShowingPage = rb1.getString("listofapplicationmemeber_ShowingPage");
  String listofapplicationmemeber_Sorry = rb1.getString("listofapplicationmemeber_Sorry");
  String listofapplicationmemeber_No = rb1.getString("listofapplicationmemeber_No");
  String listofapplicationmemeber_Filter = rb1.getString("listofapplicationmemeber_Filter");
  String listofapplicationmemeber_Fill = rb1.getString("listofapplicationmemeber_Fill");
  String listofapplicationmemeber_page_no = rb1.getString("listofapplicationmemeber_page_no");
  String listofapplicationmemeber_total_no_of_records = rb1.getString("listofapplicationmemeber_total_no_of_records");

%>
<html>
<head>
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
  <link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.1/css/all.css">
</head>
<title><%=company%> Application Manager> Merchant Application Form</title>
<body >
<%!
  ApplicationManager applicationManager = new ApplicationManager();
%>
<%
  if (partner.isLoggedInPartner(session))
  {
  String partnerId = (String) session.getAttribute("merchantid");
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    String Style1= "";
    String Style2= "";
    /*String merchantid= "";
    LinkedHashMap memberidDetails=null;
    Functions function = new Functions();
    String partnerid =nullToStr(request.getParameter("partnerid"));
    String partner_id = session.getAttribute("merchantid").toString();
    if(function.isValueNull(partnerid)){
      memberidDetails=partner.getPartnerMembersDetail(partnerid);
    }
    else{
      memberidDetails = partner.getSuperPartnerMembersDetail(partner_id);
    }*/

    String dISABLED_Id="";
    String Config="";

    if(Roles.contains("superpartner")){
      Style1= "style=\"display: none\"";
      Config = "style=\"display: block\"";
    }else{
      dISABLED_Id=String.valueOf(session.getAttribute("merchantid"));
      Style2= "style=\"display: none\"";
    }
  List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getSuperPartnersMerchantApplicationManagerVO(partnerId);
  TreeMap<String,String> partneriddetails =null;
  partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
%>
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
          method: "applicationmanager",
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

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/CreateMerchantApplication?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" value="listofapplicationmember" name="submit" style="background: transparent;border: 0;">
              <img style="height: 50px;width: 230px;" src="/partner/images/createmerchantapplication.png">
            </button>
          </form>
        </div>
      </div>

      <br>
      <br>
      <br>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">

          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=listofapplicationmemeber_Merchant_Application_Form%></strong></h2>
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

                <form action="/partner/net/ListofAppMember?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">


                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">

                  <div class="form-group col-md-4" <%=Style2%>>
                    <label class="col-sm-4 control-label"><%=listofapplicationmemeber_PartnerId%></label>
                    <div class="col-sm-8">
                      <select  class="form-control" name="partnerid" id="partnerid">
                        <option value=""><%=listofapplicationmemeber_Select_Partner_ID%></option>
                        <%
                          String partneid = (String) request.getAttribute("pid");
                          /*for(String pid : partneriddetails.keySet())
                            if(functions.isValueNull(partneid) && partneid.equals(pid)){*/
                        %>
                        <%--<option value="<%=pid%>" ><%=partneriddetails.get(pid)%></option>--%>
                        <%
//                            }
                        %>
                        <%
                          String Selected = "";
                          for(String pid : partneriddetails.keySet())
                          {
                            if(pid.toString().equals(partneid))
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
                    <label class="col-sm-4 control-label"><%=listofapplicationmemeber_PartnerID%></label>
                    <div class="col-sm-8">
                      <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                      <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                    </div>
                  </div>

                 <div class="form-group col-md-4">
                    <label class="col-sm-4 control-label"><%=listofapplicationmemeber_MerchantID%></label>
                    <div class="col-sm-8">
                      <select name="apptoid" class="form-control" id="apptoid">
                        <option value=""><%=listofapplicationmemeber_Select_Merchant_ID%></option>
                        <%
                          String memberId = (String) request.getAttribute("memberId");

                          for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
                          {
                            if(functions.isValueNull(memberId) && memberId.equals(applicationManagerVO.getMemberId().split("_")[0]))
                              out.println("<option value=\""+(applicationManagerVO.getMemberId().split("_"))[0]+"\" selected>"+(applicationManagerVO.getMemberId().split("_"))[0]+"</option>");
                            else
                              out.println("<option value=\""+(applicationManagerVO.getMemberId().split("_"))[0]+"\">"+(applicationManagerVO.getMemberId().split("_"))[0]+"</option>");
                          }
                        %>
                      </select>
                    </div>
                  </div>

                  <%--<div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">&nbsp;&nbsp;<%=listofapplicationmemeber_MerchantID%></label>
                    <div class="col-sm-6">
                      <input name="memberid" id="member" value="<%=merchantid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>--%>


                  <div class="form-group col-md-3 has-feedback">&nbsp;</div>
                  <div class="form-group col-md-3">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=listofapplicationmemeber_Search%></button>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=listofapplicationmemeber_ReportTable%></strong></h2>
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
                    <%--<td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Sr No</b></td>
                    --%><td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listofapplicationmemeber_PartnerID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listofapplicationmemeber_MemberID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listofapplicationmemeber_Application_ID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listofapplicationmemeber_Status%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listofapplicationmemeber_User%></b></td>
                    <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=listofapplicationmemeber_Action%></b></td>
                  </tr>

                  </thead>

                  <%
                    StringBuffer requestParameter = new StringBuffer();
                    Enumeration<String> stringEnumeration = request.getParameterNames();
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
                      if(functions.isValueNull(applicationManagerVO.getStatus()) && !ApplicationStatus.STEP1_SAVED.toString().equals(applicationManagerVO.getStatus()) && !ApplicationStatus.STEP1_SUBMIT.toString().equals(applicationManagerVO.getStatus()))
                      {
                        String Partnerid= partnerFunctions.getPartnerId(applicationManagerVO.getMemberId());
                  %>

                  <tbody>

                  <tr id="maindata">
                    <%--<td valign="middle" data-label="Sr No" align="center"><%=srno%></td>--%>
                    <td valign="middle" data-label="Member ID" align="center"><%=Partnerid%></td>
                    <td valign="middle" data-label="Member ID" align="center"><%=applicationManagerVO.getMemberId()%></td>
                    <td valign="middle" data-label="Application ID" align="center"><%=applicationManagerVO.getApplicationId()%></td>
                    <td valign="middle" data-label="Status" align="center"><%=applicationManagerVO.getStatus()%></td>
                    <td valign="middle" data-label="User" align="center"><%=applicationManagerVO.getUser()%></td>
                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"_View"%>" name="action"><%=listofapplicationmemeber_View%></button>
                      <%
                        out.println(requestParameter);
                      %>
                    </td>
                    <%
                      if(functions.isValueNull(applicationManagerVO.getAppliedToModify()) && "Y".equals(applicationManagerVO.getAppliedToModify()))
                      {
                    %>
                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"_Update"%>" name="action"><%=listofapplicationmemeber_ToModify%></button>
                    </td>
                    <%
                    }
                    else if((functions.isValueNull(applicationManagerVO.getStatus()) && (ApplicationStatus.SAVED.name().equals(applicationManagerVO.getStatus())) || ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus())))
                    {
                    %>
                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"_Edit"%>" name="action"><%=listofapplicationmemeber_Update%></button>
                    </td>
                    <%
                    }
                    else
                    {
                    %>
                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit" class="button btn btn-default" style="background:grey" value="<%=applicationManagerVO.getMemberId()+"_Edit"%>" name="action" disabled><%=listofapplicationmemeber_Update%></button>
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

                    <%--<td valign="middle" data-label="Sr No" align="center"><%=srno%></td>--%>
                    <td valign="middle" data-label="Member ID" align="center"><%=partnerFunctions.getPartnerId(applicationManagerVO.getMemberId())%></td>
                    <td valign="middle" data-label="Member ID" align="center"><%=applicationManagerVO.getMemberId()%></td>
                    <td valign="middle" data-label="Application ID" align="center"><%=applicationManagerVO.getApplicationId()%></td>
                    <td valign="middle" data-label="Status" align="center"><%=applicationManagerVO.getSpeed_status()%></td>
                    <td valign="middle" data-label="User" align="center"><%=applicationManagerVO.getSpeed_user()%></td>
                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"_SPEED_View"%>" name="action"><%=listofapplicationmemeber_View%></button>
                    </td>
                    <%
                      if(functions.isValueNull(applicationManagerVO.getAppliedToModify()) && "Y".equals(applicationManagerVO.getAppliedToModify()))
                      {
                    %>

                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"_SPEED_Update"%>" name="action"><%=listofapplicationmemeber_ToModify%></button>
                    </td>
                    <%
                    }
                    else if((functions.isValueNull(applicationManagerVO.getSpeed_status()) || ApplicationStatus.STEP1_SAVED.name().equals(applicationManagerVO.getSpeed_status())) && !ApplicationStatus.VERIFIED.name().equals(applicationManagerVO.getSpeed_status()))
                    {
                    %>
                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit" class="button btn btn-default" value="<%=applicationManagerVO.getMemberId()+"_SPEED_Edit"%>" name="action"><%=listofapplicationmemeber_Update%></button>
                    </td>
                    <%
                    }
                    else
                    {
                    %>
                    <td valign="middle" data-label="Action" align="center">
                      <button type="submit"  class="button btn btn-default" style="background:grey" value="<%=applicationManagerVO.getMemberId()+"_SPEED_Edit"%>" name="action" disabled><%=listofapplicationmemeber_Update%></button>
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





                  </tbody>

                </table>

              </form>

            </div>
          </div>
        </div>
      </div>

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
      <div id="showingid"><strong><%=listofapplicationmemeber_page_no%> <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=listofapplicationmemeber_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="ListofAppMember"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>


      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(listofapplicationmemeber_Sorry, listofapplicationmemeber_No));
          }
        }

        else
        {
          out.println(Functions.NewShowConfirmation1(listofapplicationmemeber_Filter, listofapplicationmemeber_Fill));
        }

      %>

    </div>
  </div>
</div>
<%
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
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
</body>
</html>