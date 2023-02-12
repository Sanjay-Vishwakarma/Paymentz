<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.ConsolidatedApplicationVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.LinkedHashMap" %>
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
  session.setAttribute("submit","consolidatedHistory");
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
  //$('#deletedid').css( 'display','none');
  function deleteconfirm(ctoken)
  {
    if (confirm("Do you really want to Delete this Record ?"))
    {
     // alert("Deleted Successfully!");
      //$('#deletedid').css( 'display','block');
      return true;
    }
    else
      //$('#deletedid').css( 'display','none');
      return false;
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
          method: "AppConsolhistory",
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

</script>
<html>
<head>
  <%
    String company= (String)session.getAttribute("partnername");
  %>
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <title><%=company%> Application Manager> Consolidated Application History</title>
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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Consolidated Application Historys</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <form action="/partner/net/ConsolidatedHistory" method="post">
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partner_id%>" id="partnerid">

              <%
               // Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMapOption=applicationManager.getconsolidated_applicationHistoryForMemberIdOrPgTypeId(null,null,null);
                String partnerId = (String) session.getAttribute("merchantid");
                Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMapOption=applicationManager.getconsolidated_memberList_history_superpartner(partnerId);
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

              <%--<h5 class="bg-info" style="text-align: center;" id="deletedid"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp; Deleted Successfully!</h5>--%>
              <div class="widget-content padding">
                <div id="horizontal-form">

              <div class="form-group col-md-4 has-feedback" <%=Style2%>>
                <label class="col-sm-4 control-label">Partner Id</label>
                <div class="col-sm-8">
                  <select class="form-control" name="partnerid" id="pid">
                    <option value="" default>Select Partner ID</option>
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
                    <label class="col-sm-4 control-label">Partner ID</label>
                    <div class="col-sm-8">
                      <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                      <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                    </div>
                  </div>


                  <div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">&nbsp;&nbsp;Merchant ID</label>
                    <div class="col-sm-6">
                      <input name="apptoid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>


                  <%--<div class="form-group col-md-4 has-feedback">
                    <label>Gateway Name</label>
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
                  </div>--%>

                  <div class="form-group col-md-3">
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-search"></i>&nbsp;&nbsp;Search</button>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <form action="/partner/net/ConsolidatedHistoryAction?ctoken=<%=ctoken%>" method="post">
                <%
                  ConsolidatedApplicationVO consolidatedApplicationVO1=null;
                  if(request.getAttribute("consolidatedApplicationVOMap")!=null)
                  {

                    Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = (Map<String, ConsolidatedApplicationVO>) request.getAttribute("consolidatedApplicationVOMap");

                %>
                <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                    <td style="text-align: center">Partner ID</td>
                    <td style="text-align: center">Member ID</td>
                    <td style="text-align: center">Gateway Name</td>
                    <td style="text-align: center">Consolidated Application</td>
                    <td style="text-align: center">Timestamp</td>
                    <td style="text-align: center">Consolidated Status</td>
                    <td style="text-align: center">Action</td>
                  </tr>
                  </thead>
                  <%--Start update code--%>
                  <%
                    PartnerFunctions partnerFunctions = new PartnerFunctions();
                    for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMap.entrySet())
                    {

                  %>

                  <tr>
                    <td valign="middle" align="center" class="tr0"><%=partnerFunctions.getPartnerId(consolidatedApplicationVO.getValue().getMemberid())%></td>
                    <td valign="middle" align="center" class="tr0"><%=consolidatedApplicationVO.getValue().getMemberid()%></td>
                    <input type="hidden" name="consolidatedId" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>">
                    <input type="hidden" name="memberid" value="<%=consolidatedApplicationVO.getValue().getMemberid()%>">
                    <input type="hidden" name="filename" value="<%=consolidatedApplicationVO.getValue().getFilename()%>">
                    <td valign="middle" align="center" class="tr0"><%=consolidatedApplicationVO.getValue().getGatewayname()%></td>

                    <td valign="middle" align="center"  class="tr0">
                      <form id="pdfForm" action="/partner/net/ConsolidatedHistoryAction?ctoken=<%=ctoken%>" method="post">
                        <input type="hidden" name="fileName" value="<%=consolidatedApplicationVO.getValue().getFilename()%>">
                        <input type="hidden" name="memberid" value="<%=consolidatedApplicationVO.getValue().getMemberid()%>">
                        <%--<input type="hidden" name="action" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>_Download">--%>
                        <input type="image" src="/icici/images/Zip.jpg" name="action" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>_Download">

                        <%--<button class="btn-xs" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>_Download" name="action" type="submit" style="background: transparent;border: 0;">
                          <img style="height: 35px;" src="/icici/images/Zip.jpg">
                        </button>--%>
                      </form>
                    </td>
                    <td valign="middle" align="center" class="tr0"><%=consolidatedApplicationVO.getValue().getTimestamp()%> </td>

                    <td valign="middle" align="center" class="tr0"><%=consolidatedApplicationVO.getValue().getStatus()%> </td>

                    <td class="tr0" align="center">
                      <%if ("INVALIDATED".equals(consolidatedApplicationVO.getValue().getStatus())) {%> <button type="submit" class="button btn btn-default"  value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Delete"%>" name="action" onclick="return deleteconfirm('ctoken')">Delete</button><%}%>
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
                  out.println(Functions.NewShowConfirmation1("Sorry", "No records found"));
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