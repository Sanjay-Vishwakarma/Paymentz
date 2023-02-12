<%--
<%@ page import="com.manager.vo.fileRelatedVOs.UploadLabelVO" %>
<%@ page import="com.manager.vo.FileDetailsVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="ApplicationManagerVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.enums.UploadFileType" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
&lt;%&ndash;<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>&ndash;%&gt;
&lt;%&ndash;
  Created by IntelliJ IDEA.
  User: Sagar
  Date: 4/26/13
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
&ndash;%&gt;

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!private Functions functions = new Functions();
    private static Logger logger = new Logger("upload.jsp");
%>


<%

    int srno=1;

    ApplicationManagerVO applicationManagerVO=null;
    ValidationErrorList validationErrorList=null;
    HashMap<String,List<FileDetailsVO>> fileDetailsVOHashMap=null;
    Map<String,UploadLabelVO> uploadLabelVOList=null;
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
        fileDetailsVOHashMap=new HashMap<String, List<FileDetailsVO>>();
    }
    if(applicationManagerVO.getUploadLabelVOs()==null)
    {
        uploadLabelVOList=new HashMap<String, UploadLabelVO>();
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

<script type="text/javascript">
    (function($){var nextId=0;var Filestyle=function(element,options){this.options=options;this.$elementFilestyle=[];this.$element=$(element)};Filestyle.prototype={clear:function(){this.$element.val("");this.$elementFilestyle.find(":text").val("");this.$elementFilestyle.find(".badge").remove()},destroy:function(){this.$element.removeAttr("style").removeData("filestyle");this.$elementFilestyle.remove()},disabled:function(value){if(value===true){if(!this.options.disabled){this.$element.attr("disabled","true");this.$elementFilestyle.find("label").attr("disabled","true");this.options.disabled=true}}else{if(value===false){if(this.options.disabled){this.$element.removeAttr("disabled");this.$elementFilestyle.find("label").removeAttr("disabled");this.options.disabled=false}}else{return this.options.disabled}}},buttonBefore:function(value){if(value===true){if(!this.options.buttonBefore){this.options.buttonBefore=true;if(this.options.input){this.$elementFilestyle.remove();this.constructor();this.pushNameFiles()}}}else{if(value===false){if(this.options.buttonBefore){this.options.buttonBefore=false;if(this.options.input){this.$elementFilestyle.remove();this.constructor();this.pushNameFiles()}}}else{return this.options.buttonBefore}}},icon:function(value){if(value===true){if(!this.options.icon){this.options.icon=true;this.$elementFilestyle.find("label").prepend(this.htmlIcon())}}else{if(value===false){if(this.options.icon){this.options.icon=false;this.$elementFilestyle.find(".icon-span-filestyle").remove()}}else{return this.options.icon}}},input:function(value){if(value===true){if(!this.options.input){this.options.input=true;if(this.options.buttonBefore){this.$elementFilestyle.append(this.htmlInput())}else{this.$elementFilestyle.prepend(this.htmlInput())}this.$elementFilestyle.find(".badge").remove();this.pushNameFiles();this.$elementFilestyle.find(".group-span-filestyle").addClass("input-group-btn")}}else{if(value===false){if(this.options.input){this.options.input=false;this.$elementFilestyle.find(":text").remove();var files=this.pushNameFiles();if(files.length>0&&this.options.badge){this.$elementFilestyle.find("label").append(' <span class="badge">'+files.length+"</span>")}this.$elementFilestyle.find(".group-span-filestyle").removeClass("input-group-btn")}}else{return this.options.input}}},size:function(value){if(value!==undefined){var btn=this.$elementFilestyle.find("label"),input=this.$elementFilestyle.find("input");btn.removeClass("btn-lg btn-sm");input.removeClass("input-lg input-sm");if(value!="nr"){btn.addClass("btn-"+value);input.addClass("input-"+value)}}else{return this.options.size}},placeholder:function(value){if(value!==undefined){this.options.placeholder=value;this.$elementFilestyle.find("input").attr("placeholder",value)}else{return this.options.placeholder}},buttonText:function(value){if(value!==undefined){this.options.buttonText=value;this.$elementFilestyle.find("label .buttonText").html(this.options.buttonText)}else{return this.options.buttonText}},buttonName:function(value){if(value!==undefined){this.options.buttonName=value;this.$elementFilestyle.find("label").attr({"class":"btn "+this.options.buttonName})}else{return this.options.buttonName}},iconName:function(value){if(value!==undefined){this.$elementFilestyle.find(".icon-span-filestyle").attr({"class":"icon-span-filestyle "+this.options.iconName})}else{return this.options.iconName}},htmlIcon:function(){if(this.options.icon){return'<span class="icon-span-filestyle '+this.options.iconName+'"></span> '}else{return""}},htmlInput:function(){if(this.options.input){return'<input type="text" class="form-control '+(this.options.size=="nr"?"":"input-"+this.options.size)+'" placeholder="'+this.options.placeholder+'" disabled> '}else{return""}},pushNameFiles:function(){var content="",files=[];if(this.$element[0].files===undefined){files[0]={name:this.$element[0]&&this.$element[0].value}}else{files=this.$element[0].files}for(var i=0;i<files.length;i++){content+=files[i].name.split("\\").pop()+", "}if(content!==""){this.$elementFilestyle.find(":text").val(content.replace(/\, $/g,""))}else{this.$elementFilestyle.find(":text").val("")}return files},constructor:function(){var _self=this,html="",id=_self.$element.attr("id"),files=[],btn="",$label;if(id===""||!id){id="filestyle-"+nextId;_self.$element.attr({id:id});nextId++}btn='<span class="group-span-filestyle '+(_self.options.input?"input-group-btn":"")+'"><label for="'+id+'" class="btn '+_self.options.buttonName+" "+(_self.options.size=="nr"?"":"btn-"+_self.options.size)+'" '+(_self.options.disabled?'disabled="true"':"")+">"+_self.htmlIcon()+'<span class="buttonText">'+_self.options.buttonText+"</span></label></span>";html=_self.options.buttonBefore?btn+_self.htmlInput():_self.htmlInput()+btn;_self.$elementFilestyle=$('<div class="bootstrap-filestyle input-group">'+html+"</div>");_self.$elementFilestyle.find(".group-span-filestyle").attr("tabindex","0").keypress(function(e){if(e.keyCode===13||e.charCode===32){_self.$elementFilestyle.find("label").click();return false}});_self.$element.css({position:"absolute",clip:"rect(0px 0px 0px 0px)"}).attr("tabindex","-1").after(_self.$elementFilestyle);if(_self.options.disabled){_self.$element.attr("disabled","true")}_self.$element.change(function(){var files=_self.pushNameFiles();if(_self.options.input==false&&_self.options.badge){if(_self.$elementFilestyle.find(".badge").length==0){_self.$elementFilestyle.find("label").append(' <span class="badge">'+files.length+"</span>")}else{if(files.length==0){_self.$elementFilestyle.find(".badge").remove()}else{_self.$elementFilestyle.find(".badge").html(files.length)}}}else{_self.$elementFilestyle.find(".badge").remove()}});if(window.navigator.userAgent.search(/firefox/i)>-1){_self.$elementFilestyle.find("label").click(function(){_self.$element.click();return false})}}};var old=$.fn.filestyle;$.fn.filestyle=function(option,value){var get="",element=this.each(function(){if($(this).attr("type")==="file"){var $this=$(this),data=$this.data("filestyle"),options=$.extend({},$.fn.filestyle.defaults,option,typeof option==="object"&&option);if(!data){$this.data("filestyle",(data=new Filestyle(this,options)));data.constructor()}if(typeof option==="string"){get=data[option](value)}}});if(typeof get!==undefined){return get}else{return element}};$.fn.filestyle.defaults={buttonText:"Choose file",iconName:"glyphicon glyphicon-folder-open",buttonName:"btn-default",size:"nr",input:true,badge:true,icon:true,buttonBefore:false,disabled:false,placeholder:""};$.fn.filestyle.noConflict=function(){$.fn.filestyle=old;return this};$(function(){$(".filestyle").each(function(){var $this=$(this),options={input:$this.attr("data-input")==="false"?false:true,icon:$this.attr("data-icon")==="false"?false:true,buttonBefore:$this.attr("data-buttonBefore")==="true"?true:false,disabled:$this.attr("data-disabled")==="true"?true:false,size:$this.attr("data-size"),buttonText:$this.attr("data-buttonText"),buttonName:$this.attr("data-buttonName"),iconName:$this.attr("data-iconName"),badge:$this.attr("data-badge")==="false"?false:true,placeholder:$this.attr("data-placeholder")};$this.filestyle(options)})})})(window.jQuery);
</script>
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
        float: left;
        margin-top: 15px;
        margin-bottom: 15px;
    }

    .glyphicon{display: none;}

    #mainname{text-align: left;}

    @media (max-width: 640px){

        #mainname{text-align: center;}
    }


    .input-group .form-control:first-child{display: none;}

</style>
<div class="content-page" id="uploadid">
    <div class="content">
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
                                    Thank you for choosing us for processing your transactions, we would be happy to be your Payment Processor.
                                    <br> Short description for each button:<br>

                                    <div class="col-md-12" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-floppy-o"></i>
                                            &nbsp;&nbsp;Save
                                        </div> - All the filled information will be saved, even if you log out, and will be waiting to be submitted when you fill all mandatory fields.
                                    </div>

                                    <div class="col-md-12" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Submit
                                        </div> - All the mandatory fields are filled and you want to submit form and to start process transactions.
                                    </div>

                                    <div class="col-md-6" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-upload"></i>
                                            &nbsp;&nbsp;Upload
                                        </div> - Upload the selected documents.
                                    </div>

                                    <div class="col-md-6" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-times"></i>
                                            &nbsp;&nbsp;Cancel
                                        </div> - Redirected to the beginning(Step 1).
                                    </div>

                                    <div class="col-md-6" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-angle-double-left"></i>
                                            &nbsp;&nbsp;Previous
                                        </div> - Go one step back.
                                    </div>

                                    <div class="col-md-6" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-angle-double-right"></i>
                                            &nbsp;&nbsp;Next
                                        </div> - Go one step forward.
                                    </div>
                                    <br>
                                    If you need more details, please do not hesitate to contact us, we would be more than happy to assist you. Our contact number is:<br> +44 20 3129 5664

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>


            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Upload</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">                        &lt;%&ndash; End Radio Button&ndash;%&gt;
                                <div class="form-group col-md-12 has-feedback">

                                    <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%>bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please Upload KYC"%></h2></center>

                                    &lt;%&ndash; <table align="center" width="90%" border="1">&ndash;%&gt;
                                    <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;border: 1px solid #ddd;">

                                        <%
                                            for(Map.Entry<String,UploadLabelVO>  uploadLabelVO:uploadLabelVOList.entrySet())
                                            {
                                                String name="";
                                                String data_buttonText="Upload File";
                                                String classname="";
                                                //ToDo Add logger
                                                logger.debug("Alternate Name uploadjsp....."+uploadLabelVO.getValue().getAlternateName());
                                                if(fileDetailsVOHashMap !=null && fileDetailsVOHashMap.get(uploadLabelVO.getValue().getAlternateName())!=null)
                                                {
                                                    //logger.debug("Alternate Name inside if uploadjsp....." + fileDetailsVOHashMap.get(uploadLabelVO.getValue().getAlternateName()).isSuccess());
                                                }
                                                if(fileDetailsVOHashMap.containsKey(uploadLabelVO.getValue().getAlternateName()) /*&& fileDetailsVOHashMap.get(uploadLabelVO.getValue().getAlternateName()).isSuccess()*/)
                                                {
                                                    logger.debug("inside if........"+uploadLabelVO.getValue().getAlternateName());
                                                    //logger.debug("inside if........"+fileDetailsVOHashMap.get(uploadLabelVO.getValue().getAlternateName()).isSuccess());
                                                    name="|Replace";
                                                    data_buttonText="Replace File";
                                                    classname="txtboxconfirm";

                                                    logger.debug("inside if name........"+name);
                                                    logger.debug("inside if data_buttonText........"+data_buttonText);
                                                    logger.debug("inside if classname........"+classname);

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

                                                //ToDO Print acceptString
                                                logger.debug("accept String...."+acceptString);


                                        %>

                                        <tr>
                                            <td align="left" style="vertical-align: middle;text-align: center;font-size: 15px;"><%=srno%></td>
                                            <td align="left" id="mainname" style="vertical-align: middle;font-size: 15px;"><%=uploadLabelVO.getValue().getLabelName()%></td>
                                            <td align="center">
                                                <input name="<%=uploadLabelVO.getValue().getAlternateName()+"|"+uploadLabelVO.getValue().getLabelId()+name%>" type="file" class="filestyle" accept="<%=acceptString.toString()%>" data-buttonText="<%=data_buttonText%>" &lt;%&ndash;style="display:table"&ndash;%&gt;>
                                            </td>
                                            <td align="center">
                                                <span class="<%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?"txtboxerror":classname%>" style="color: red;/*margin-left: 18%;padding-bottom: 1%;padding-top: 1%;padding-right: 1%;padding-left: 1%;font-size: 11px*/"><%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?validationErrorList.getError(uploadLabelVO.getValue().getAlternateName()).getLogMessage():functions.isValueNull(classname)?"Uploaded":""%></span>
                                            </td>

                                        </tr>
                                        <%

                                                srno++;
                                            }

                                        %>


                                    </table>

                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
--%>

<%@ page import="com.vo.applicationManagerVOs.AppUploadLabelVO" %>
<%@ page import="com.vo.applicationManagerVOs.AppFileDetailsVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.enums.UploadFileType" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.vo.applicationManagerVOs.FileDetailsListVO" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<%--<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>--%>
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
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String upload_Note = StringUtils.isNotEmpty(rb1.getString("upload_Note"))?rb1.getString("upload_Note"): "Note";
    String upload_please = StringUtils.isNotEmpty(rb1.getString("upload_please"))?rb1.getString("upload_please"): "Please Upload KYC";
    String upload_supported = StringUtils.isNotEmpty(rb1.getString("upload_supported"))?rb1.getString("upload_supported"): "Supported Formats: Excel, PDF, JPG & PNG.";
    String upload_exceed = StringUtils.isNotEmpty(rb1.getString("upload_exceed"))?rb1.getString("upload_exceed"): "The file size cannot exceed 2 MB.";
    String upload_Upload = StringUtils.isNotEmpty(rb1.getString("upload_Upload"))?rb1.getString("upload_Upload"): "Upload";
    String upload_sr = StringUtils.isNotEmpty(rb1.getString("upload_sr"))?rb1.getString("upload_sr"): "Sr No.";
    String upload_document = StringUtils.isNotEmpty(rb1.getString("upload_document"))?rb1.getString("upload_document"): "Document Name";
    String upload_file_name = StringUtils.isNotEmpty(rb1.getString("upload_file_name"))?rb1.getString("upload_file_name"): "File Name";
    String upload_Action = StringUtils.isNotEmpty(rb1.getString("upload_Action"))?rb1.getString("upload_Action"): "Action";
    String upload_status = StringUtils.isNotEmpty(rb1.getString("upload_status"))?rb1.getString("upload_status"): "Status";
    String upload_upload = StringUtils.isNotEmpty(rb1.getString("upload_upload"))?rb1.getString("upload_upload"): "Upload";
    String upload_save = StringUtils.isNotEmpty(rb1.getString("upload_save"))?rb1.getString("upload_save"): "Save";
    String upload_filled = StringUtils.isNotEmpty(rb1.getString("upload_filled"))?rb1.getString("upload_filled"): "- All the filled information will be saved, even if you log out, and will be waiting to be submitted when you fill all mandatory fields.";
    String upload_submit = StringUtils.isNotEmpty(rb1.getString("upload_submit"))?rb1.getString("upload_submit"): "Submit";
    String upload_mandatory = StringUtils.isNotEmpty(rb1.getString("upload_mandatory"))?rb1.getString("upload_mandatory"): "- All the mandatory fields are filled and you want to submit form and to start process transactions.";
    String upload_selected = StringUtils.isNotEmpty(rb1.getString("upload_selected"))?rb1.getString("upload_selected"): "- Upload the selected documents.";
    String upload_cancel = StringUtils.isNotEmpty(rb1.getString("upload_cancel"))?rb1.getString("upload_cancel"): "Cancel";
    String upload_redirected = StringUtils.isNotEmpty(rb1.getString("upload_redirected"))?rb1.getString("upload_redirected"): "- Redirected to the beginning(Step 1).";
    String upload_Previous = StringUtils.isNotEmpty(rb1.getString("upload_Previous"))?rb1.getString("upload_Previous"): "Previous";
    String upload_stepback = StringUtils.isNotEmpty(rb1.getString("upload_stepback"))?rb1.getString("upload_stepback"): "- Go one step back.";
    String upload_next = StringUtils.isNotEmpty(rb1.getString("upload_next"))?rb1.getString("upload_next"): "Next";
    String upload_forward = StringUtils.isNotEmpty(rb1.getString("upload_forward"))?rb1.getString("upload_forward"): "- Go one step forward.";
    int srno=1;
    String copyiframeUpload=(String)session.getAttribute("fileName");
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
        fileDetailsVOHashMap = applicationManagerVO.getFileDetailsVOs();
    }
    if(applicationManagerVO.getUploadLabelVOs()!=null)
    {
        uploadLabelVOList=applicationManagerVO.getUploadLabelVOs();
    }
    if(fileDetailsVOHashMap==null)
    {
        fileDetailsVOHashMap = new HashMap<String, FileDetailsListVO>();
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
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<script type="text/javascript">
    $(":file").filestyle({buttonText: "Find file"});

    $(":file").filestyle();

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

    function myUploadNew1(ctoken,alternateName,iframefiled) {
        if(check()) {
            console.log("inside if_____")
            /* document.getElementById("myformname").enctype = "multipart/form-data";
             document.myformname.action = "/partner/net/UploadServlet?ctoken=" + ctoken + "&alternate_name=" + alternate_name + "&cust_reg_Id=" + cust_reg_Id;
             document.myformname.submit();*/
            document.getElementById("myformname").enctype = "multipart/form-data";
            document.myformname.action="/merchant/servlet/UploadServlet?ctoken="+ctoken+"&alternate_name="+alternateName+"&pageNoInJSP=6&copyiframe="+iframefiled;
            document.myformname.submit();
        }
        else
        {
            alert('Please select a valid file (Supported Formats: Excel, PDF, JPG & PNG. ) instead!');
        }
    }

</script>

<script type="text/javascript">
    (function($){var nextId=0;var Filestyle=function(element,options){this.options=options;this.$elementFilestyle=[];this.$element=$(element)};Filestyle.prototype={clear:function(){this.$element.val("");this.$elementFilestyle.find(":text").val("");this.$elementFilestyle.find(".badge").remove()},destroy:function(){this.$element.removeAttr("style").removeData("filestyle");this.$elementFilestyle.remove()},disabled:function(value){if(value===true){if(!this.options.disabled){this.$element.attr("disabled","true");this.$elementFilestyle.find("label").attr("disabled","true");this.options.disabled=true}}else{if(value===false){if(this.options.disabled){this.$element.removeAttr("disabled");this.$elementFilestyle.find("label").removeAttr("disabled");this.options.disabled=false}}else{return this.options.disabled}}},buttonBefore:function(value){if(value===true){if(!this.options.buttonBefore){this.options.buttonBefore=true;if(this.options.input){this.$elementFilestyle.remove();this.constructor();this.pushNameFiles()}}}else{if(value===false){if(this.options.buttonBefore){this.options.buttonBefore=false;if(this.options.input){this.$elementFilestyle.remove();this.constructor();this.pushNameFiles()}}}else{return this.options.buttonBefore}}},icon:function(value){if(value===true){if(!this.options.icon){this.options.icon=true;this.$elementFilestyle.find("label").prepend(this.htmlIcon())}}else{if(value===false){if(this.options.icon){this.options.icon=false;this.$elementFilestyle.find(".icon-span-filestyle").remove()}}else{return this.options.icon}}},input:function(value){if(value===true){if(!this.options.input){this.options.input=true;if(this.options.buttonBefore){this.$elementFilestyle.append(this.htmlInput())}else{this.$elementFilestyle.prepend(this.htmlInput())}this.$elementFilestyle.find(".badge").remove();this.pushNameFiles();this.$elementFilestyle.find(".group-span-filestyle").addClass("input-group-btn")}}else{if(value===false){if(this.options.input){this.options.input=false;this.$elementFilestyle.find(":text").remove();var files=this.pushNameFiles();if(files.length>0&&this.options.badge){this.$elementFilestyle.find("label").append(' <span class="badge">'+files.length+"</span>")}this.$elementFilestyle.find(".group-span-filestyle").removeClass("input-group-btn")}}else{return this.options.input}}},size:function(value){if(value!==undefined){var btn=this.$elementFilestyle.find("label"),input=this.$elementFilestyle.find("input");btn.removeClass("btn-lg btn-sm");input.removeClass("input-lg input-sm");if(value!="nr"){btn.addClass("btn-"+value);input.addClass("input-"+value)}}else{return this.options.size}},placeholder:function(value){if(value!==undefined){this.options.placeholder=value;this.$elementFilestyle.find("input").attr("placeholder",value)}else{return this.options.placeholder}},buttonText:function(value){if(value!==undefined){this.options.buttonText=value;this.$elementFilestyle.find("label .buttonText").html(this.options.buttonText)}else{return this.options.buttonText}},buttonName:function(value){if(value!==undefined){this.options.buttonName=value;this.$elementFilestyle.find("label").attr({"class":"btn "+this.options.buttonName})}else{return this.options.buttonName}},iconName:function(value){if(value!==undefined){this.$elementFilestyle.find(".icon-span-filestyle").attr({"class":"icon-span-filestyle "+this.options.iconName})}else{return this.options.iconName}},htmlIcon:function(){if(this.options.icon){return'<span class="icon-span-filestyle '+this.options.iconName+'"></span> '}else{return""}},htmlInput:function(){if(this.options.input){return'<input type="text" class="form-control '+(this.options.size=="nr"?"":"input-"+this.options.size)+'" placeholder="'+this.options.placeholder+'" disabled> '}else{return""}},pushNameFiles:function(){var content="",files=[];if(this.$element[0].files===undefined){files[0]={name:this.$element[0]&&this.$element[0].value}}else{files=this.$element[0].files}for(var i=0;i<files.length;i++){content+=files[i].name.split("\\").pop()+", "}if(content!==""){this.$elementFilestyle.find(":text").val(content.replace(/\, $/g,""))}else{this.$elementFilestyle.find(":text").val("")}return files},constructor:function(){var _self=this,html="",id=_self.$element.attr("id"),files=[],btn="",$label;if(id===""||!id){id="filestyle-"+nextId;_self.$element.attr({id:id});nextId++}btn='<span class="group-span-filestyle '+(_self.options.input?"input-group-btn":"")+'"><label for="'+id+'" class="btn '+_self.options.buttonName+" "+(_self.options.size=="nr"?"":"btn-"+_self.options.size)+'" '+(_self.options.disabled?'disabled="true"':"")+">"+_self.htmlIcon()+'<span class="buttonText">'+_self.options.buttonText+"</span></label></span>";html=_self.options.buttonBefore?btn+_self.htmlInput():_self.htmlInput()+btn;_self.$elementFilestyle=$('<div class="bootstrap-filestyle input-group">'+html+"</div>");_self.$elementFilestyle.find(".group-span-filestyle").attr("tabindex","0").keypress(function(e){if(e.keyCode===13||e.charCode===32){_self.$elementFilestyle.find("label").click();return false}});_self.$element.css({position:"absolute",clip:"rect(0px 0px 0px 0px)"}).attr("tabindex","-1").after(_self.$elementFilestyle);if(_self.options.disabled){_self.$element.attr("disabled","true")}_self.$element.change(function(){var files=_self.pushNameFiles();if(_self.options.input==false&&_self.options.badge){if(_self.$elementFilestyle.find(".badge").length==0){_self.$elementFilestyle.find("label").append(' <span class="badge">'+files.length+"</span>")}else{if(files.length==0){_self.$elementFilestyle.find(".badge").remove()}else{_self.$elementFilestyle.find(".badge").html(files.length)}}}else{_self.$elementFilestyle.find(".badge").remove()}});if(window.navigator.userAgent.search(/firefox/i)>-1){_self.$elementFilestyle.find("label").click(function(){_self.$element.click();return false})}}};var old=$.fn.filestyle;$.fn.filestyle=function(option,value){var get="",element=this.each(function(){if($(this).attr("type")==="file"){var $this=$(this),data=$this.data("filestyle"),options=$.extend({},$.fn.filestyle.defaults,option,typeof option==="object"&&option);if(!data){$this.data("filestyle",(data=new Filestyle(this,options)));data.constructor()}if(typeof option==="string"){get=data[option](value)}}});if(typeof get!==undefined){return get}else{return element}};$.fn.filestyle.defaults={buttonText:"Choose file",iconName:"glyphicon glyphicon-folder-open",buttonName:"btn-default",size:"nr",input:true,badge:true,icon:true,buttonBefore:false,disabled:false,placeholder:""};$.fn.filestyle.noConflict=function(){$.fn.filestyle=old;return this};$(function(){$(".filestyle").each(function(){var $this=$(this),options={input:$this.attr("data-input")==="false"?false:true,icon:$this.attr("data-icon")==="false"?false:true,buttonBefore:$this.attr("data-buttonBefore")==="true"?true:false,disabled:$this.attr("data-disabled")==="true"?true:false,size:$this.attr("data-size"),buttonText:$this.attr("data-buttonText"),buttonName:$this.attr("data-buttonName"),iconName:$this.attr("data-iconName"),badge:$this.attr("data-badge")==="false"?false:true,placeholder:$this.attr("data-placeholder")};$this.filestyle(options)})})})(window.jQuery);
</script>
<%--<script text="text/javascript">
    $(function() {

        // We can attach the `fileselect` event to all file inputs on the page
        $(document).on('change', ':file', function() {
            var input = $(this),
                    numFiles = input.get(0).files ? input.get(0).files.length : 1,
                    label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
            input.trigger('fileselect', [numFiles, label]);
        });

        // We can watch for our custom `fileselect` event like this
        $(document).ready( function() {
            $(':file').on('fileselect', function(event, numFiles, label) {

                var input = $(this).parents('.input-group').find(':text'),
                        log = numFiles > 1 ? numFiles + ' files selected' : label;

                if( input.length ) {
                    input.val(log);
                } else {
                    if( log ) alert(log);
                }

            });
        });

    });
</script>--%>

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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=upload_Note%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">

                            <div id="horizontal-form">

                                <div class="bg-info "style="text-align: center;font-size:16px;/*color: #31708f;*/">
                                    <center><h2 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;background-color:#f3f3f3!important;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():upload_please%></h2></center>

                                    <center><h4 class="txtboxinfo bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;background-color:#f3f3f3!important;"><%=upload_supported%> <br> <%=upload_exceed%></h4></center>


                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=upload_Upload%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div>                        <%-- End Radio Button--%>
                                <div class="form-group col-md-12 has-feedback">
                                    <%-- <table align="center" width="90%" border="1">--%>
                                    <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;border: 1px solid #ddd;">
                                        <%
                                            User user =  (User)session.getAttribute("ESAPIUserSessionKey");
                                            String ctoken= null;
                                            if(user!=null)
                                            {
                                                ctoken = user.getCSRFToken();

                                            }
                                        %>
                                        <%--<tr>
                                          <td align="left" style="vertical-align: middle;text-align: center;font-size: 15px;"><%=srno%></td>
                                          <td align="left" id="mainname" style="vertical-align: middle;font-size: 15px;"><%=uploadLabelVO.getValue().getLabelName()%></td>
                                          <td align="center">
                                              <input name="<%=uploadLabelVO.getValue().getAlternateName()+"|"+uploadLabelVO.getValue().getLabelId()%>" type="file" class="filestyle" accept="<%=acceptString.toString()%>" data-buttonText="Upload File" &lt;%&ndash;style="display:table"&ndash;%&gt;>
                                          </td>
                                            <td align="right">


                                            <%
                                                    List<FileDetailsVO> files = fileDetailsVOHashMap.get(uploadLabelVO.getValue().getAlternateName());
                                                    if(files.size()>0){
                                                    for(int i=0;i<files.size();i++){
                                                        FileDetailsVO fileDetail = files.get(i);
                                                        name += fileDetail.getFilename();
                                                       %>
                                                        <td align="right">
                                            file Name:<%=fileDetail.getFilename()%>
                                            <input name="<%=uploadLabelVO.getValue().getAlternateName()+"|"+uploadLabelVO.getValue().getLabelId()+name%>" type="file" class="filestyle" accept="<%=acceptString.toString()%>" data-buttonText="<%=data_buttonText%>" &lt;%&ndash;style="display:table"&ndash;%&gt;>

                                                        </td>
                                                <%
                                                        }
                                                    }
                                                    %>

                                          <td align="center">
                                            <span class="<%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?"txtboxerror":classname%>"style="color: red;/*margin-left: 18%;padding-bottom: 1%;padding-top: 1%;padding-right: 1%;padding-left: 1%;font-size: 11px*/"><%=validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())!=null?validationErrorList.getError(uploadLabelVO.getValue().getAlternateName()).getLogMessage():functions.isValueNull(classname)?"Uploaded":""%></span>
                                          </td>

                                        </tr>--%>

                                        <%--<thead>

                                        </thead>
                                        <tbody>

                                        </tbody>--%>

                                        <tr>
                                            <th><%=upload_sr%></th>
                                            <th style="width: 25%;"><%=upload_document%></th>
                                            <th style="width: 25%;"><%=upload_file_name%></th>
                                            <th style="width: 25%;"><%=upload_Action%></th>
                                            <th style="width: 15%;"><%=upload_status%></th>
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
                                                    data_buttonText="Replace";
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
                                                $('.file-input-wrapper input[type=file]').change(function (e)
                                                {
                                                    e.stopImmediatePropagation();
                                                    e.preventDefault();
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

                                                    $(this).parent().after('<span class="file-input-name"><p style="word-break:break-all;width:60%;">'+fileName+'</p></span>');
                                                    //$('#file_name_id<%=uploadLabelVO.getValue().getLabelId()%>').append('<span class="file-input-name">' + fileName + '</span>');
                                                });
                                            });
                                        </script>

                                        <tr>

                                            <td><%=srno%></td>
                                            <td><%=uploadLabelVO.getValue().getLabelName()%></td>
                                            <%--<td id="file_name_id<%=uploadLabelVO.getValue().getLabelId()%>"></td>--%>
                                            <td><input name="<%=uploadLabelVO.getValue().getAlternateName()+"|"+uploadLabelVO.getValue().getLabelId()%>" type="file" class="filestyle" accept="<%=acceptString.toString()%>" data-buttonText="Browse"></td>
                                            <td><button type="button" id="newbtn" class="btn btn-default" style="margin-left: 15px;margin-top: 8px;" name="upload" onclick="myUploadNew1('<%=ctoken%>','<%=uploadLabelVO.getValue().getAlternateName()%>','<%=copyiframeUpload%>');">
                                                <i class="iconbuttonform"></i>
                                                <%=upload_upload%>
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
                                        </tr>

                                    </table>

                                </div>
                                <%--<div class="form-group col-md-12 has-feedback">
                                    <center>
                                        <label >&nbsp;</label>
                                        <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
                                    </center>
                                </div>--%>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=upload_Note%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">

                            <div id="horizontal-form">

                                <div class="bg-info "style="text-align: center;font-size:16px;/*color: #31708f;*/">

                                    <div class="col-md-12" id="topborder" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-floppy-o"></i>
                                            &nbsp;&nbsp;<%=upload_save%>
                                        </div> <%=upload_filled%>
                                    </div>

                                    <div class="col-md-12" id="topborder" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;<%=upload_submit%>
                                        </div> <%=upload_mandatory%>
                                    </div>

                                    <div class="col-md-6" id="topborder" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-upload"></i>
                                            &nbsp;&nbsp;<%=upload_upload%>
                                        </div> <%=upload_selected%>
                                    </div>

                                    <div class="col-md-6" id="topborder" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-times"></i>
                                            &nbsp;&nbsp;<%=upload_cancel%>
                                        </div>     <%=upload_redirected%>
                                    </div>

                                    <div class="col-md-6" id="topborder" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-angle-double-left"></i>
                                            &nbsp;&nbsp;<%=upload_Previous%>
                                        </div> <%=upload_stepback%>
                                    </div>

                                    <div class="col-md-6" id="topborder" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;text-align: initial;">
                                        <div class="btn btn-default" style="width:100px;">
                                            <i class="fa fa-angle-double-right"></i>
                                            &nbsp;&nbsp;<%=upload_next%>
                                        </div> <%=upload_forward%>
                                    </div>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                    <br>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

