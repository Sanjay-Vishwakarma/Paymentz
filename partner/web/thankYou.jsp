<%--
  Created by IntelliJ IDEA.
  User: SurajT.
  Date: 8/13/2018
  Time: 1:08 PM
  To change this template use File | Settings | File Templates.
--%>


<%@ page import="com.vo.applicationManagerVOs.AppUploadLabelVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.vo.applicationManagerVOs.FileDetailsListVO" %>
<%@ include file="top.jsp"%>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!private Functions functions = new Functions();%>
<%
  ApplicationManagerVO applicationManagerVO=null;
  if(session.getAttribute("applicationManagerVO")!=null)
  {
    applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
  }
%>

<style type="text/css">
  .input-group .form-control:first-child, .input-group-addon:first-child, .input-group-btn:first-child>.btn, .input-group-btn:first-child>.btn-group>.btn, .input-group-btn:first-child>.dropdown-toggle, .input-group-btn:last-child>.btn:not(:last-child):not(.dropdown-toggle), .input-group-btn:last-child>.btn-group:not(:last-child)>.btn{
    top: 0!important;
    left: 0!important;
  }

  .file-input-wrapper{
    color: transparent;
    cursor: default;
    margin-top: -20px;
  }

  .file-input-wrapper:hover{
    color: transparent;
    cursor: default;
  }

  .file-input-name{
    /* float: left;*/
    margin-top: 15px;
    margin-bottom: 15px;
    display: inherit;
  }

  .glyphicon{display: none;}

  #mainname{text-align: left;}

  @media (max-width: 640px){

    #mainname{text-align: center;}
  }


  .input-group .form-control:first-child{display: none;}

  .table#myTable>thead>tr>th, .table#myTable>tbody>tr>th, .table#myTable>tfoot>tr>th, .table#myTable>thead>tr>td {
    color: #ffffff!important;
  }

</style>



<div class="content-page" id="uploadid">
  <div class="content" id="uploadcontent">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Note</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">

              <div id="horizontal-form">

                <div class="bg-info "style="text-align: center;font-size:16px;/*color: #31708f;*/">
                  <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;background-color:#f3f3f3!important;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please Upload KYC"%></h2></center>

                  <center>
                    <h4 class="txtboxinfo bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;background-color:#f3f3f3!important;">Click here to view application

                      <form action="/partner/net/ListofAppMember?apptoid=<%=applicationManagerVO.getMemberId()%>&ctoken=<%=ctoken%>" method="post">

                        <button class='btn btn-default' style="margin-left: 15px;margin-top: 8px;" title="Login">View</button>

                      </form>
                    </h4>

                  </center>

                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</div>


