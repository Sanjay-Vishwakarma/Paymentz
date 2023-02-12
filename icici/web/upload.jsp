<%@ page import="com.vo.applicationManagerVOs.AppUploadLabelVO" %>
<%@ page import="com.vo.applicationManagerVOs.AppFileDetailsVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.vo.applicationManagerVOs.FileDetailsListVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Sagar
  Date: 4/26/13
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!private Functions functions = new Functions();%>
<%


    int srno=1;

    ApplicationManagerVO applicationManagerVO=null;
    ValidationErrorList validationErrorList=null;
    Map<String,FileDetailsListVO> fileDetailsVOHashMap=null;
    Map<String,AppUploadLabelVO> uploadLabelVOList=null;
    if(session.getAttribute("applicationManagerVO")!=null)
    {
        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
    }

    if(applicationManagerVO.getFileDetailsVOs()!=null)
    {
        fileDetailsVOHashMap=applicationManagerVO.getFileDetailsVOs();
    }
    if(applicationManagerVO.getUploadLabelVOs()!=null)
    {
        uploadLabelVOList=applicationManagerVO.getUploadLabelVOs();
    }
    if(fileDetailsVOHashMap==null)
    {
        fileDetailsVOHashMap=new HashMap<String, FileDetailsListVO>();
    }
    if(applicationManagerVO.getUploadLabelVOs()==null)
    {
        uploadLabelVOList=new HashMap<String, AppUploadLabelVO>();
    }
    if(session.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");
    }
    else if(request.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");
    }
    else
    {
        validationErrorList= new ValidationErrorList();
    }
%>
<script type="text/javascript" src="/icici/stylenew/filestyle1.js"></script>
<script type="text/javascript">
    $(":file").filestyle({buttonText: "Find file"});
</script>

<script>

    function check() {
        var temp = document.myformname.querySelectorAll('input[type=file]');
        var  retpath = "";
        if(temp.length>0) {
            for(var i = 0; i < temp.length; i++ ){
                if(document.myformname.querySelectorAll('input[type=file]')[i].value) {
                    retpath = document.myformname.querySelectorAll('input[type=file]')[i].value;
                    break;
                }
            }
        }

        var pos = retpath.lastIndexOf(".");
        var filename="";
        if (pos != -1)
            filename = retpath.substring(pos + 1);
        else
            filename = retpath;
        var regex = new RegExp("^[^<>=]*$");
        if (!regex.test(retpath)) {
            return false;
        }
        else{
            if (filename=='pdf' || filename=='xlsx' || filename=='png'  || filename=='jpeg' || filename=='jpg'|| filename=='PDF' || filename=='XLSX' || filename=='PNG'  || filename=='JPEG' || filename=='JPG' ) {
                return true;
            }
            else{

                return false;
            }

        }
        console.log("filename = ",filename)

    }

    function myUpload1(ctoken) {
        if(check()) {
            console.log("inside if_____")
            /* document.getElementById("myformname").enctype = "multipart/form-data";
             document.myformname.action = "/partner/net/UploadServlet?ctoken=" + ctoken + "&alternate_name=" + alternate_name + "&cust_reg_Id=" + cust_reg_Id;
             document.myformname.submit();*/
            document.getElementById("myformname").enctype = "multipart/form-data";
            document.myformname.action="/icici/servlet/UploadServlet?ctoken="+ctoken;
            document.myformname.submit();
        }
        else
        {
            alert('Please select a valid file (Supported Formats: Excel, PDF, JPG & PNG. ) instead!');
        }
    }

</script>


<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Upload Document
            </div>
            <br>
            <table style="width: 95%">
                <tr>
                    <td>
                        <table align="center" style="width:95%;margin-left: 4%" border="1" class="table table-striped table-bordered table-green dataTable">
                            <tr >
                                <td colspan="5"  class="textb" class="tr0">
                                    <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%>"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please Upload KYC"%></h2></center>

                                    <center><h4 class="txtboxinfo bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;">Supported Formats: Excel, PDF, JPG & PNG. <br> The file size cannot exceed 2 MB.</h4></center>
                                </td>
                            </tr>
                            <tr>
                                <th>Sr No.</th>
                                <th>Document Name</th>
                                <th>File Name</th>
                                <th>Action</th>
                                <th>Status</th>
                            </tr>
                            <%
                                for(Map.Entry<String,AppUploadLabelVO>  uploadLabelVO:uploadLabelVOList.entrySet())
                                {
                                    String errorMsg="";
                                    if (validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null){
                                        errorMsg=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName()).getLogMessage();
                                    }

                                    if (functions.isValueNull(errorMsg)){
                                        if (errorMsg.contains("pdf, xlsx, png, jpg")){
                                            errorMsg="File is not a .[pdf, xlsx, png, jpg] extension";
                                        }
                                    }

                                    String name="";
                                    String data_buttonText="Upload File";
                                    String classname="";
                                    if(fileDetailsVOHashMap.containsKey(uploadLabelVO.getValue().getAlternateName()) /*&& fileDetailsVOHashMap.get(uploadLabelVO.getValue().getAlternateName()).isSuccess()*/)
                                    {
                                        name="|Replace";
                                        data_buttonText="Replace File";
                                        classname="txtboxconfirm";
                                    }
                                    StringBuffer acceptString=new StringBuffer();
                                    for(String accept: uploadLabelVO.getValue().getSupportedFileType().split(","))
                                    {
                                        if(accept.equalsIgnoreCase("PDF"))
                                        {
                                            acceptString.append("application/pdf,");
                                        }
                                        else if(accept.equalsIgnoreCase("XLSX"))
                                        {
                                            acceptString.append("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,");
                                        }
                                        else if(accept.equalsIgnoreCase("PNG"))
                                        {
                                            acceptString.append("image/png,");
                                        }
                                        else if(accept.equalsIgnoreCase("JPG"))
                                        {
                                            acceptString.append("image/jpeg,");
                                        }
                                    }

                            %>
                            <script type="">
                                $(document).ready(function()
                                {
                                    $('.file-input-wrapper input[type=file]').change(function ()
                                    {

                                        var fileName;
                                        fileName = $(this).val();

                                        // Remove any previous file names
                                        $(this).parent().next('.file-input-name').remove();
                                        if (!!$(this).prop('files') && $(this).prop('files').length > 1)
                                        {
                                            fileName = $(this)[0].files.length + ' files';
                                            fileName = fileName.replace(/[<>=]/g, '');
                                            //$(this).parent().after('<span class="file-input-name">'+$(this)[0].files.length+' files</span>');
                                        }
                                        else
                                        {
                                            // var fakepath = 'C:\\fakepath\\';
                                            // fileName = $(this).val().replace('C:\\fakepath\\','');
                                            fileName = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
                                            fileName = fileName.replace(/[<>=]/g, '');
                                        }

                                        $(this).parent().after('<span class="file-input-name">'+fileName+'</span>');
                                        //$('#file_name_id<%=uploadLabelVO.getValue().getLabelId()%>').append('<span class="file-input-name">' + fileName + '</span>');
                                    });
                                });
                            </script>


                            <tr>
                                <td><%=srno%></td>

                                <%
                                    User user =  (User)session.getAttribute("ESAPIUserSessionKey");
                                    String ctoken= null;
                                    if(user!=null)
                                    {
                                        ctoken = user.getCSRFToken();

                                    }
                                %>
                                <td><%=uploadLabelVO.getValue().getLabelName()%></td>
                                <td><input name="<%=uploadLabelVO.getValue().getAlternateName()+"|"+uploadLabelVO.getValue().getLabelId()%>" type="file" class="filestyle" accept="<%=acceptString.toString()%>" data-buttonText="Browse"></td>
                                <td><button type="button" id="newbtn" class="btn btn-default" style="margin-left: 15px;margin-top: 8px;" name="upload" onclick="myUpload1('<%=ctoken%>')">
                                    <i class="fa fa-upload" class="iconbuttonform"></i>
                                    &nbsp;&nbsp;Upload
                                </button></td>
                                <td><span class="<%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?"txtboxerror":classname%>"style="color: red;"><%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?errorMsg:functions.isValueNull(classname)?"Uploaded":""%></span></td>

                            </tr>
                            <%
                                FileDetailsListVO fileDetailsListVO= fileDetailsVOHashMap.get(uploadLabelVO.getValue().getAlternateName());
                                if(fileDetailsListVO!=null)
                                {
                                    List<AppFileDetailsVO> files = fileDetailsListVO.getFiledetailsvo();
                                    if(files!=null && files.size()>0){
                                        for(int i=0;i<files.size();i++){
                                            AppFileDetailsVO fileDetail = files.get(i);
                                            name += fileDetail.getFilename();
                            %>
                            <tr>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>

                                <td><p><%=fileDetail.getFilename()%></p></td>
                                <td><input name="<%=uploadLabelVO.getValue().getAlternateName()+"|"+uploadLabelVO.getValue().getLabelId()+name+"|"+fileDetail.getMappingId()%>" type="file" style="padding-left:0;" class="filestyle" accept="<%=acceptString.toString()%>" data-buttonText="<%=data_buttonText%>" <%--style="display:table"--%>></td>
                                <td>&nbsp;</td>
                            </tr>
                            <%
                                        }
                                    }
                                }
                            %>

                            <%

                                    srno++;
                                }

                            %>
                            </td>
                            <%--<tr >
                                <td class="textb" class="tr0">
                                    <%=srno%>
                                </td>

                                <td style="padding:5px" class="tr0">
                                    <%=uploadLabelVO.getValue().getLabelName()%>
                                </td>

                                <td style="padding:5px" class="tr0">
                                    <input name="<%=uploadLabelVO.getValue().getAlternateName()+"|"+uploadLabelVO.getValue().getLabelId()+name%>" type="file" class="filestyle" accept="<%=acceptString.toString()%>" data-buttonText="<%=data_buttonText%>" style="display:table">

                                    <span class="<%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?"txtboxerror":classname%>"style="margin-left: 18%;padding-bottom: 1%;padding-top: 1%;padding-right: 1%;padding-left: 1%;font-size: 11px"><%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?validationErrorList.getError(uploadLabelVO.getValue().getAlternateName()).getLogMessage():functions.isValueNull(classname)?"Upload":""%></span>
                                </td>

                            </tr>

                            <%f

                                    srno++;
                                }

                            %>--%>
                        </table>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>