<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.ConsolidatedApplicationVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Pradeep
  Date: 22/07/2015
  Time: 18:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%!
  ApplicationManager applicationManager = new ApplicationManager();
  Functions functions = new Functions();
%>
<%
  session.setAttribute("submit","manageBankApp");
  String memberid="";
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
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);

  String manageBankApp_Manage_Application = rb1.getString("manageBankApp_Manage_Application");
  String manageBankApp_PartnerId = rb1.getString("manageBankApp_PartnerId");
  String manageBankApp_Select_Partner_ID = rb1.getString("manageBankApp_Select_Partner_ID");
  String manageBankApp_PartnerID = rb1.getString("manageBankApp_PartnerID");
  String manageBankApp_MemberID = rb1.getString("manageBankApp_MemberID");
  String manageBankApp_Search = rb1.getString("manageBankApp_Search");
  String manageBankApp_Report_Table = rb1.getString("manageBankApp_Report_Table");
  String manageBankApp_Member_ID = rb1.getString("manageBankApp_Member_ID");
  String manageBankApp_Gateway_Name = rb1.getString("manageBankApp_Gateway_Name");
  String manageBankApp_Consolidated_Application = rb1.getString("manageBankApp_Consolidated_Application");
  String manageBankApp_Application_Status = rb1.getString("manageBankApp_Application_Status");
  String manageBankApp_Action = rb1.getString("manageBankApp_Action");
  String manageBankApp_SendMail = rb1.getString("manageBankApp_SendMail");
  String manageBankApp_FTP_to_Bank = rb1.getString("manageBankApp_FTP_to_Bank");
  String manageBankApp_Delete = rb1.getString("manageBankApp_Delete");
  String manageBankApp_Sorry = rb1.getString("manageBankApp_Sorry");
  String manageBankApp_No_records_found = rb1.getString("manageBankApp_No_records_found");

%>
<script language="javascript">
  function ToggleAll(checkbox)
  {
    flag = checkbox.checked;
    var checkboxes = document.getElementsByName("gateway");
    var total_boxes = checkboxes.length;

    for(i=0; i<total_boxes; i++ )
    {
      checkboxes[i].checked =flag;
    }
  }

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
          method: "ManageApplication",
          term: request.term
        },
        success: function (data)
        {
          $('#memberid').find('option').not(':first').remove();
          $.each(data.aaData, function (i, data)
          {
            var div_data = "<option value=" + data.value + ">" + data.text + "</option>";
            console.log(div_data);
            $(div_data).appendTo('#memberid');
          });
        }
      });
      minLength: 0
    });
  });

  function Delete(ctoken)
  {
    if (confirm("Do you really want to Delete."))
    {
      return true;
    }
    else
      return false;
  }
</script>
<%
  String company= (String)session.getAttribute("partnername");
%>
<html>
<head>
 <%-- <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <title><%=company%> Application Manager> Manage Application</title>
</head>
<body>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=manageBankApp_Manage_Application%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <form action="/partner/net/ManageBankApp?ctoken=<%=ctoken%>" method="post">
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partner_id%>"  id="partnerid">

              <%
                //Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMapOption=applicationManager.getconsolidated_applicationForMemberIdOrPgTypeId(null,null,null);
                String partnerId = (String) session.getAttribute("merchantid");
                Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMapOption=applicationManager.SuperPartner_getconsolidated_memberList(partnerId);
              %>
              <%
                if(request.getAttribute("errorC")!=null)
                {
                  Object o=request.getAttribute("errorC");
                  if(o instanceof ValidationErrorList)
                  {
                    ValidationErrorList error = (ValidationErrorList) request.getAttribute("errorC");
                    for (ValidationException errorList : error.errors())
                    {
                      //out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorList.getMessage() + "</h5>");
                    }
                  }
                  else
                  {
                    String errorMsg=(String)request.getAttribute("errorC");
                    //out.println("<center><font class=\"textb\">" + errorMsg + "</font></center>");
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5>");
                  }

                }
                if (request.getAttribute("message")!=null){
                  String errorMsg=(String)request.getAttribute("message");
                  //out.println("<center><font class=\"textb\">" + errorMsg + "</font></center>");
                  out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5>");
                }

                java.util.TreeMap<String,String> partneriddetails =null;
                partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
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
              %>


              <div class="widget-content padding">
                <div id="horizontal-form">

                  <div class="form-group col-md-4 has-feedback" <%=Style2%>>
                    <label class="col-sm-4 control-label"><%=manageBankApp_PartnerId%></label>
                    <div class="col-sm-8">
                      <select class="form-control" name="partnerid" id="pid">
                        <option value="" default><%=manageBankApp_Select_Partner_ID%></option>
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
                    <label class="col-sm-4 control-label"><%=manageBankApp_PartnerID%></label>
                    <div class="col-sm-8">
                      <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                      <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                    </div>
                  </div>


                  <%--<div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">&nbsp;<%=manageBankApp_MemberID%></label>
                    <div class="col-sm-6">
                      <input name="mid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>
                  &lt;%&ndash; <div class="form-group col-md-4 has-feedback">
                     <label>Gateway Name*</label>
                     <select name="memberid" class="form-control">

                       <option value="">Select GatewayName</option>
                       <%
                         for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMapOption.entrySet())
                         {
                           out.println("<option value=\""+consolidatedApplicationVO.getValue().getGatewayname()+"\" "+(consolidatedApplicationVO.getValue().getGatewayname().equals(request.getParameter("gateway"))?"selected":"")+">"+consolidatedApplicationVO.getValue().getGatewayname()+"</option>");
                           //gatewayOptionTag.append("<option value=\""+consolidatedApplicationVO.getValue().getGatewayname()+"\"> "+consolidatedApplicationVO.getValue().getGatewayname()+"</option>");
                         }
                       %>
                     </select>
                   </div>&ndash;%&gt;--%>

                  <div class="form-group col-md-4 has-feedback" >
                    <label class="col-sm-4 control-label"><%=manageBankApp_MemberID%></label>
                    <div class="col-sm-8">
                      <select name="memberid" class="form-control" id="memberid">

                        <option value="">Select Merchant Id</option>
                        <%
                          StringBuffer gatewayOptionTag= new StringBuffer();
                          String id = "";
                          for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMapOption.entrySet())
                          {
                            if(!functions.isValueNull(id) || !id.equals(consolidatedApplicationVO.getValue().getMemberid()))
                            {
                              id = consolidatedApplicationVO.getValue().getMemberid();
                              out.println("<option value=\""+consolidatedApplicationVO.getValue().getMemberid()+"\" "+(consolidatedApplicationVO.getValue().getMemberid().equals(request.getParameter("memberid"))?"selected":"")+">"+consolidatedApplicationVO.getValue().getMemberid()+"</option>");
                              gatewayOptionTag.append("<option value=\""+consolidatedApplicationVO.getValue().getGatewayname()+"\"> "+consolidatedApplicationVO.getValue().getGatewayname()+"</option>");
                            }
                          }
                        %>
                      </select>
                    </div>
                  </div>
                  <div class="form-group col-md-3">
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-search"></i>&nbsp;&nbsp;<%=manageBankApp_Search%></button>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
      <%--<div class="reporttable">--%>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=manageBankApp_Report_Table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <form action="/partner/net/SendMailToBank?ctoken=<%=ctoken%>" method="post">
                <%
                  if(request.getAttribute("consolidatedApplicationVOMap")!=null)
                  {

                    Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = (Map<String, ConsolidatedApplicationVO>) request.getAttribute("consolidatedApplicationVOMap");

                %>
                <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                    <td style="text-align: center"><%=manageBankApp_PartnerID%></td>
                    <td style="text-align: center"><%=manageBankApp_Member_ID%></td>
                    <td style="text-align: center"><%=manageBankApp_Gateway_Name%></td>
                    <td style="text-align: center"><%=manageBankApp_Consolidated_Application%></td>
                    <td style="text-align: center"><%=manageBankApp_Application_Status%></td>
                    <td style="text-align: center" colspan="3"><%=manageBankApp_Action%></td>
                  </tr>
                  </thead>
                  <%--Start update code--%>
                  <%
                    PartnerFunctions partnerFunctions = new PartnerFunctions();
                    for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMap.entrySet())
                    {

                  %>

                  <tr>
                    <td data-label="Partner ID" style="text-align: center"><%=partnerFunctions.getPartnerId(consolidatedApplicationVO.getValue().getMemberid())%></td>

                    <td data-label="Member ID" style="text-align: center"><%=consolidatedApplicationVO.getValue().getMemberid()%></td>

                    <td data-label="Gateway Name" style="text-align: center"><%=consolidatedApplicationVO.getValue().getGatewayname()%></td>

                    <td data-label="Consolidated Application" style="text-align: center">
                      <%--<input type="submit" class="btn btn-default"  name="action" value="Generate ZIP" onclick="myOpenBankGeneratedTemplate()">&lt;%&ndash;<input type="image" src="/icici/images/Zip.jpg" onclick="myOpenBankGeneratedTemplate()" name="action" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>_View">&ndash;%&gt;--%>
                      <form id="pdfForm" action="/partner/net/SendMailToBank?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name="fileName" value="<%=consolidatedApplicationVO.getValue().getFilename()%>">
                        <input type="hidden" name="memberid" value="<%=consolidatedApplicationVO.getValue().getMemberid()%>">
                        <%--<input type="hidden" name="action" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Download"%>">--%>
                        <input type="image" src="/icici/images/Zip.jpg" name="action" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Download"%>">
                      </form>
                    </td>

                    <td data-label="Application Status" style="text-align: center">

                      <%--<input type="text" class="form-control" value="<%=consolidatedApplicationVO.getValue().getStatus()%>" disabled>--%>

                      <select  name="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>_status"  class="form-control">
                        <%=applicationManager.getConsolidatedAppStatusOptionTag(functions.isValueNull(consolidatedApplicationVO.getValue().getStatus()) ? consolidatedApplicationVO.getValue().getStatus() : "", null)%>
                      </select>
                    </td>

                    <td data-label="Action" style="text-align: center">
                      <button type="submit" class="btn btn-default" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Mail"%>" name="action"><%=manageBankApp_SendMail%></button>
                    </td>
                    <td data-label="Action" style="text-align: center">
                      <button type="submit" class="btn btn-default" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Edit"%>" name="action" disabled><%=manageBankApp_FTP_to_Bank%></button>
                    </td>
                    <td data-label="Action" style="text-align: center">
                      <button type="submit" class="btn btn-default" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Delete"%>" name="action" onclick=" return Delete(this);"><%=manageBankApp_Delete%></button>
                    </td>
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
                  out.println(Functions.NewShowConfirmation1(manageBankApp_Sorry, manageBankApp_No_records_found));
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
      </div>
</body>
</html>