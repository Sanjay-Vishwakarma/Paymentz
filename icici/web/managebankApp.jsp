<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.ConsolidatedApplicationVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Map" %>
<%--
  Created by IntelliJ IDEA.
  User: Pradeep
  Date: 22/07/2015
  Time: 18:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%!
  ApplicationManager applicationManager = new ApplicationManager();
  Functions functions = new Functions();

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
</script>
<html>
<head>
  <title>Application Manager> Manage Application</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>

<div class="row">
  <div class="col-lg-12" style="margin-top: 8%">
    <div class="panel panel-default" style="margin-top: 0%">
      <div class="panel-heading" >
        Manage Application
      </div>

      <form action="/icici/servlet/ManageBankApp" method="post">
        <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">

        <%
          Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMapOption=applicationManager.getconsolidated_applicationForMemberIdOrPgTypeId(null,null,null);
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
                out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
              }
            }
            else
            {
              String errorMsg=(String)request.getAttribute("errorC");
              out.println("<center><font class=\"textb\">" + errorMsg + "</font></center>");
            }

          }
          if (request.getAttribute("message")!=null){
            String errorMsg=(String)request.getAttribute("message");
            //out.println("<center><font class=\"textb\">" + errorMsg + "</font></center>");
            out.println("<center><font class=\"textb\">" + errorMsg + "</font></center>");
           // out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5>");
          }
        %>
        <table  align="center" width="100%" cellpadding="2" cellspacing="2" <%--style="margin-left:2.5%;margin-right: 2.5% "--%>>

          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Merchant Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
<%--
                    <input name="memberid" class="txtbox" id="mid">
--%>


                   <select name="memberid" class="txtbox">

                      <option value="">Select Merchant ID</option>
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

                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Gateway Name</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">

                    <select name="gateway" class="txtbox">

                      <option value="">Select GatewayName</option>
                      <%
                        for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMapOption.entrySet())
                        {
                          out.println("<option value=\""+consolidatedApplicationVO.getValue().getGatewayname()+"\" "+(consolidatedApplicationVO.getValue().getGatewayname().equals(request.getParameter("gateway"))?"selected":"")+">"+consolidatedApplicationVO.getValue().getGatewayname()+"</option>");
                          //gatewayOptionTag.append("<option value=\""+consolidatedApplicationVO.getValue().getGatewayname()+"\"> "+consolidatedApplicationVO.getValue().getGatewayname()+"</option>");
                        }
                      %>
                    </select>

                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
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
  <form action="/icici/servlet/SendMailToBank?ctoken=<%=ctoken%>" method="post">
    <%
      if(request.getAttribute("consolidatedApplicationVOMap")!=null)
      {

        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = (Map<String, ConsolidatedApplicationVO>) request.getAttribute("consolidatedApplicationVOMap");

    %>
    <table align=center width="45%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" class="th0">Member ID</td>
        <td valign="middle" class="th0">Gateway Name</td>
        <td valign="middle" class="th0">Consolidated Application</td>
        <td valign="middle" class="th0">Application Status</td>
        <td width="25%" valign="middle" align="center" class="th0" colspan="3">Action</td>
      </tr>
      </thead>
      <%--Start update code--%>
      <%

        for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVO : consolidatedApplicationVOMap.entrySet())
        {

      %>

      <tr>

        <td valign="middle" align="center" class="tr0"><%=consolidatedApplicationVO.getValue().getMemberid()%></td>

        <td valign="middle" align="center" class="tr0"><%=consolidatedApplicationVO.getValue().getGatewayname()%></td>

        <td valign="middle" align="center"  class="tr0">
          <form action="/icici/servlet/SendMailToBank?ctoken=<%=ctoken%>" method="post">
            <input type="hidden" name="fileName" value="<%=consolidatedApplicationVO.getValue().getFilename()%>">
            <input type="hidden" name="memberid" value="<%=consolidatedApplicationVO.getValue().getMemberid()%>">
            <input type="image" src="/icici/images/Zip.jpg" name="action" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>_Download">
          </form>
        </td>
        <td valign="middle" align="center" class="tr0">
          <select  name="<%=consolidatedApplicationVO.getValue().getConsolidated_id()%>_status"  class="txtbox">
            <%=applicationManager.getConsolidatedAppStatusOptionTag(functions.isValueNull(consolidatedApplicationVO.getValue().getStatus()) ? consolidatedApplicationVO.getValue().getStatus() : "", null)%>
          </select>
        </td>

        <td class="tr0" align="center">
          <button type="submit" class="button" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Mail"%>" name="action">Send Mail</button>
        </td>
        <td class="tr0" align="center">
          <button type="submit" class="button" style="width: 92%" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Edit"%>" name="action">Update Status</button>
        </td>
        <td class="tr0" align="center">
          <button type="submit" class="button" style="width: 75%" value="<%=consolidatedApplicationVO.getValue().getConsolidated_id()+"_Delete"%>" name="action">Delete</button>
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
      out.println(Functions.NewShowConfirmation("Sorry","No records found"));
    }
  %>
</div>
</body>
</html>