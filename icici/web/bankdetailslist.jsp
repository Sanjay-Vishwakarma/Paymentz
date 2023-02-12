<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.enums.ApplicationStatus" %>
<%@ page import="com.vo.applicationManagerVOs.AppFileDetailsVO" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankApplicationMasterVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankTypeVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

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
    ApplicationManager applicationManager = new ApplicationManager();
    Functions functions = new Functions();
%>
<html>
<head>
    <title>Application Manager> Merchant Application PDF</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("pgtypeid");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
    </script>
</head>
<%--using for admin AppManagerStatus--%>
<body>

<div class="row">
    <div class="col-lg-12" style="margin-top: 8%">
        <div class="panel panel-default" style="margin-top: 0%">
            <div class="panel-heading" >
                Merchant Application PDF

            </div>

            <form action="/icici/servlet/BankDetailsList" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">


                <%

                    //Error Disply
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

                    ApplicationManagerVO applicationManagerVOParam = new ApplicationManagerVO();
                    //applicationManagerVOParam.setStatus(ApplicationStatus.VERIFIED.name());

                    List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getApplicationManagerVO(applicationManagerVOParam);%>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Merchant Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
<%--
                                        <input name="memberid" class="txtbox" id="mid">
--%>
                                        <select name="memberid" class="txtbox">
                                            <option value="">Select Merchant ID</option>
                                            <%
                                                for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
                                                {
                                                    if(ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.name().equals(applicationManagerVO.getStatus()))
                                                        out.println("<option value=\"" + applicationManagerVO.getMemberId()+"\" "+((applicationManagerVO.getMemberId().equals(request.getParameter("memberid")))?"selected":"")+">"+applicationManagerVO.getMemberId()+"</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
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

        if(request.getAttribute("bankTypeVOList")!=null)
        {

            List<BankTypeVO> bankTypeVOList = (List<BankTypeVO>) request.getAttribute("bankTypeVOList");

            Map<String,AppFileDetailsVO> fileDetailsVOMap =null;
            Map<String,BankTypeVO> merchantBankMappingMap=null;

            if(request.getAttribute("fileDetailsVOMap")!=null)
                fileDetailsVOMap=(Map<String, AppFileDetailsVO>) request.getAttribute("fileDetailsVOMap");

            if(request.getAttribute("merchantBankMappingMap")!=null)
                merchantBankMappingMap= (Map<String, BankTypeVO>) request.getAttribute("merchantBankMappingMap");
            Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs = (Map<String, List<BankApplicationMasterVO>>) request.getAttribute("bankApplicationMasterVOs");
            if(bankTypeVOList.size()>0)
            {
    %>
    <form name="form" action="/icici/servlet/CreateBankApplication?ctoken=<%=ctoken%>" method="post" id="myformname" target="_blank">
        <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">
        <table align=center width="45%" class="table table-striped table-bordered  table-green dataTable">
            <thead>
            <tr style="border-bottom-color:#34495e" >
                <td valign="middle" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td valign="middle" class="th0">Bank&nbsp;Name</td>

                <%
                    if(bankApplicationMasterVOs!=null)
                    {
                %>
                <td valign="middle" class="th0" style="border-right-color:#34495e" colspan="3">View Application</td>
                <%--<td valign="middle" class="th0" style="border-right-color:#34495e"></td>
                <td valign="middle" class="th0"></td>
--%>
                <%
                    }
                %>
            </tr>
            </thead>
            <%

                for(BankTypeVO bankTypeVO:bankTypeVOList)
                {
                    if(functions.isValueNull(bankTypeVO.getFileName()) && (((merchantBankMappingMap!=null && merchantBankMappingMap.containsKey(bankTypeVO.getBankId())) || (merchantBankMappingMap!=null && !(merchantBankMappingMap.size()>0))) ||(bankApplicationMasterVOs!=null && bankApplicationMasterVOs.containsKey(bankTypeVO.getBankId()))))
                    {
            %>
            <tr>
                <td valign="middle" align="center" class="tr0" <%=(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankTypeVO.getBankId()) )?"rowspan=\"2\"":""%>><input type="checkbox"  name="pgtypeid" value="<%=bankTypeVO.getBankId()%>"></td>
                <td valign="middle" align="center" class="tr0" <%=(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankTypeVO.getBankId()) )?"rowspan=\"2\"":""%>><%=bankTypeVO.getBankName()%></td>
                <%--
                                <td valign="middle" align="center" class="tr0">
                --%>
                <%
                    if((bankApplicationMasterVOs!=null && bankApplicationMasterVOs.containsKey(bankTypeVO.getBankId())))
                    {

                        boolean firstElement=true;
                        List<BankApplicationMasterVO> bankApplicationMasterVOList=bankApplicationMasterVOs.get(bankTypeVO.getBankId());
                        for(BankApplicationMasterVO bankApplicationMasterVO:bankApplicationMasterVOList)
                        {

                %>

                <td style="border-right-color:#ffffff">
                    <table>
                        <tr>
                            <td valign="middle" align="center"  class="tr0">
                                <form action="/icici/servlet/CreateBankApplication?ctoken=<%=ctoken%>" method="post">
                                    <input type="hidden" name="fileName" value="<%=bankApplicationMasterVO.getBankfilename()%>">
                                    <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">
                                    <%--<input type="hidden" name="action" value="<%=bankApplicationMasterVO.getBankapplicationid()%>|View">--%>
                                    <input type="image" src="/icici/images/pdflogo.jpg" width="25%" name="action" value="<%=bankApplicationMasterVO.getBankapplicationid()%>|View">
                                </form>
                            <td valign="middle" align="center"  class="tr0"><%=bankApplicationMasterVO.getTimestamp()%><br><br>
                                <select name="" class="txtbox" disabled="true" style="border: none;background-color:#eee9e9 " >
                                    <%=applicationManager.getBankApplicationStatus(functions.isValueNull(bankApplicationMasterVO.getStatus())?bankApplicationMasterVO.getStatus():"",null)%>
                                </select>
                            </td>
                        </tr>
                    </table>
                </td>
                <%
                        }
                    }

                %>
            </tr>
            <%
                if(fileDetailsVOMap!=null && fileDetailsVOMap.containsKey(bankTypeVO.getBankId()))
                {
            %>
            <tr>
                <td colspan="3">
                    <span><%=fileDetailsVOMap.get(bankTypeVO.getBankId()).isSuccess()?"Success":fileDetailsVOMap.get(bankTypeVO.getBankId()).getReasonOfFailure()%></span>
                </td>
            </tr>
            <%
                }
            %>
            <%
                    }
                }
            %>

        </table>
        <table align=center style="width:20%" class="table table-striped table-bordered dataTable">
            <tr>

                <td valign="middle" align="center" class="tr0"><input type="submit" class="buttonform"  name="action" value="Generate PDF" onclick="myOpenBankGeneratedTemplate('<%=ctoken%>')"></td>
                <td valign="middle" align="center" class="tr0"><input type="submit" class="buttonform"  name="action" value="Verified PDF" onclick="myOpenBankGeneratedTemplate('<%=ctoken%>')"></td>
            </tr>
        </table>
    </form>

    <%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry","No records found"));
            }
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Filter","Please provide proper data to get list of bank"));
        }
    %>


</div>
<script src='/icici/stylenew/AfterAppManager.js'></script>
</body>
</html>