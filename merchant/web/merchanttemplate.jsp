<%@ page language="java"
         import="com.directi.pg.Template,com.logicboxes.util.ApplicationProperties,java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="Top.jsp" %>
<%@ include file="ietest.jsp" %>

<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Transaction Pages");

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String merchanttemplate_checkout_page = rb1.getString("merchanttemplate_checkout_page");
    String merchanttemplate_Phone = rb1.getString("merchanttemplate_Phone");
    String merchanttemplate_1 = rb1.getString("merchanttemplate_1");
    String merchanttemplate_Phone2 = rb1.getString("merchanttemplate_Phone2");
    String merchanttemplate_01 = rb1.getString("merchanttemplate_01");
    String merchanttemplate_Email = rb1.getString("merchanttemplate_Email");
    String merchanttemplate_02 = rb1.getString("merchanttemplate_02");
    String merchanttemplate_Enable_Templating = rb1.getString("merchanttemplate_Enable_Templating");
    String merchanttemplate_03 = rb1.getString("merchanttemplate_03");
    String merchanttemplate_Auto_Redirect = rb1.getString("merchanttemplate_Auto_Redirect");
    String merchanttemplate_04 = rb1.getString("merchanttemplate_04");
    String merchanttemplate_template1 = rb1.getString("merchanttemplate_template1");
    String merchanttemplate_click_on = rb1.getString("merchanttemplate_click_on");
    String merchanttemplate_Save = rb1.getString("merchanttemplate_Save");
%>

<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
    <script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/html5.js'></script>--%>

    <title><%=company%> Merchant Settings > Customise Template</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function button(btn)
        {

            return doSubmit();
        }
        /*function showPreview(ctoken)
         {   var a= document.getElementById('ctoken').value;

         document.form1.action = "/merchant/servlet/PreviewServlet?ctoken="+ctoken;
         newwindow = window.open('/merchant/blank.jsp?ctoken='+ctoken, 'preview', 'left=10,top=10,width=600,height=520,resizable=no,scrollbars=yes');
         // document.form1.submit();
         return true;
         }*/
        function showHelp(title,ctoken)
        {
            newwindow = window.open('/merchant/help.jsp?ctoken='+ctoken+'&helptitle=' + title, '', 'left=10,top=10,width=500,height=370,resizable=no,scrollbars=yes');
            return false;
        }
        function doSubmit()

        {
            var a= document.getElementById('ctoken').value;

            document.form1.action = "/merchant/servlet/UpdateMerchantTemplate?ctoken="+a;
            document.form1.target = "_self";
            return true;
        }

    </script>
    <style type="text/css">
        .field-icon
        {
            float:  right;
            margin-top: -25px;
            position: relative;
            z-index: 2;
        }


    </style>

    <script src="/merchant/javascript/hidde.js"></script>
</head>
<body class="pace-done widescreen fixed-left-void" onload="bodyonload()">

<%
    //Hashtable details = (Hashtable)request.getAttribute("details");
    // Hashtable images = (Hashtable)request.getAttribute("images");
    if (request.getParameter("MES") != null)
    {
        String mes = request.getParameter("MES");
        if (mes.equals("F"))
        {
%>

<% }
}
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchanttemplate_checkout_page%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <form action="" method="post" name="form1" target="preview">
                                    <input id="ctoken" type="hidden" value="<%=ctoken%>" name="ctoken">

                                    <%
                                        //Hashtable details = (Hashtable)request.getAttribute("details");
                                        // Hashtable images = (Hashtable)request.getAttribute("images");
                                        if (request.getAttribute("error") != null)
                                        {
                                            String mes = request.getAttribute("error").toString();
                                            //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+mes+"</b></li></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + mes + "</h5>");
                                        }

                                    %>
                                    <% Enumeration templateEnum = Template.defaulthash.keys();

                                        Hashtable memberTemplateHash = (Hashtable) request.getAttribute("details");

                                        while (templateEnum.hasMoreElements())
                                        {
                                            String name = (String) templateEnum.nextElement();
                                            String value = (String) memberTemplateHash.get(name);
                                            if (value == null)
                                                value = "";
                                            String label = (String) Template.defaultlabelhash.get(name);

                                            if (name.equalsIgnoreCase("HEADER"))
                                            {
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=label%>&nbsp;&nbsp;<a href="" onclick="return showHelp('<%=name%>','<%=ctoken%>');"><b>?</b></a></label>
                                        <textarea name="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="form-control" cols="50" rows="10"><%=ESAPI.encoder().encodeForHTML(value)%></textarea>
                                    </div>
                                    <% }
                                    else
                                    {    if(!name.equals("BACKGROUND"))
                                    {
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=label%>&nbsp;&nbsp;<a href="" onclick="return showHelp('<%= name%>','<%=ctoken%>');"><b>?</b></a></label>
                                        <input  class="form-control" name="<%=name%>" type="Text" value="<%=value%>" size="30" >
                                    </div>
                                    <%
                                            }
                                        }
                                    %>

                                    <% }
                                        String imagelist = "";
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchanttemplate_Phone%>&nbsp;&nbsp;<a href="" onclick="return showHelp('PHONE1','<%=ctoken%>');"><b><%=merchanttemplate_1%></b></a></label>
                                        <% if(memberTemplateHash.get("PHONE1")!=null) {

                                        %>
                                        <%-- <input  class="form-control" name="PHONE1" type=text length="30" placeholder="+1-541-754-3010" value="<%=memberTemplateHash.get("PHONE1")%>">--%>
                                        <input class="form-control" type="hidden" id="phone1_hidden" name="PHONE1" length="30" placeholder="+1-541-754-3010" value="<%=memberTemplateHash.get("PHONE1")%>" >
                                        <input class="form-control hidedivphone" type="Text" id="phone1"  length="30" placeholder="+1-541-754-3010" value="<%=memberTemplateHash.get("PHONE1")%>" onchange="setvalue('phone1')" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('phone1')"></span>

                                        <% }
                                        else{%>

                                        <input class="form-control" type="hidden" id="phone1_hidden" name="PHONE1" length="30" placeholder="+1-541-754-3010" value="" >
                                        <input class="form-control hidedivphone" type="Text" id="phone1"  length="30" placeholder="+1-541-754-3010" value="" onchange="setvalue('phone1')" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('phone1')"></span>

                                        <% }%>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchanttemplate_Phone2%>&nbsp;&nbsp;<a href="" onclick="return showHelp('PHONE2','<%=ctoken%>');"><b><%=merchanttemplate_01%></b></a></label>
                                        <% if(memberTemplateHash.get("PHONE2")!=null) {

                                        %>
                                        <%--<input  class="form-control" name="PHONE2" type=text length="30" placeholder="+1-541-754-3010" value="<%=memberTemplateHash.get("PHONE2")%>">--%>
                                        <input class="form-control" type="hidden" id="phone2_hidden" name="PHONE2" length="30" placeholder="+1-541-754-3010" value="<%=memberTemplateHash.get("PHONE2")%>" >
                                        <input class="form-control hidedivphone" type="Text" id="phone2"  length="30" placeholder="+1-541-754-3010" value="<%=memberTemplateHash.get("PHONE2")%>" onchange="setvalue('phone2')" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('phone2')"></span>

                                        <% }
                                        else{%>
                                        <%--  <input  class="form-control" name="PHONE2" type=text length="30" placeholder="+1-541-754-3010">--%>
                                        <input class="form-control" type="hidden" id="phone2_hidden" name="PHONE2" length="30" placeholder="+1-541-754-3010" value="" >
                                        <input class="form-control hidedivphone" type="Text" id="phone2"  length="30" placeholder="+1-541-754-3010" value="" onchange="setvalue('phone2')" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('phone2')"></span>
                                        <% }%>
                                    </div>



                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchanttemplate_Email%>&nbsp;&nbsp;<a href="" onclick="return showHelp('EMAILS','<%=ctoken%>');"><b><%=merchanttemplate_02%></b></a></label>
                                        <% if(memberTemplateHash.get("EMAILS")!=null) {

                                        %>
                                        <input type="hidden" name="EMAILS" id="email_hidden"  class="form-control" cols="40" rows="5" placeholder="abc@xyz.com" value="<%=(memberTemplateHash.get("EMAILS"))%>">
                                        <input type="Text"  id="email"  class="form-control hidedivemail" cols="40" rows="5"  placeholder="abc@xyz.com" value="<%=(memberTemplateHash.get("EMAILS"))%>" onchange="setvalue('email')" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('email')"></span>
                                        .
                                        <% }
                                        else{%>

                                        <input type="hidden" name="EMAILS" id="email_hidden"  class="form-control" cols="40" rows="5" placeholder="abc@xyz.com" value="">
                                        <input type="Text"  id="email"  class="form-control hidedivemail" maxlength="50"  placeholder="abc@xyz.com" value="" onchange="setvalue('email')"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('email')"></span>
                                        <% }%>
                                    </div>

                                    <%
                                        String selected = null;
                                        if (((String) memberTemplateHash.get("template")).equalsIgnoreCase("Y"))
                                            selected = "checked";
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchanttemplate_Enable_Templating%>&nbsp;&nbsp;<a href="" onclick="return showHelp('TEMPLATE','<%=ctoken%>');"><b><%=merchanttemplate_03%></b></a></label>
                                        <input  name="template" type="checkbox" value="Y" <%=selected%> disabled="disabled">
                                    </div>
                                    <%
                                        selected = null;
                                        if (((String) memberTemplateHash.get("autoredirect")).equalsIgnoreCase("Y"))
                                            selected = "checked";
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchanttemplate_Auto_Redirect%>&nbsp;&nbsp;<a href="" onclick="return showHelp('AUTOREDIRECT','<%=ctoken%>');"><b><%=merchanttemplate_04%></b></a></label>
                                        <input  name="autoredirect" type="checkbox" value="Y"  disabled="disabled" <%=selected%> >
                                    </div>

                                    <div class="form-group col-md-12" style="color: #fff;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;font-size: 14px;line-height: 1.42857143;">
                                        <label class="bg-info" style="font-family:Open Sans;font-size: 13px;font-weight: 600;  width: inherit;"><i class="fa fa-info-circle"></i>&nbsp;&nbsp;
                                            <%=merchanttemplate_template1%>
                                            <br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <%=merchanttemplate_click_on%>
                                        </label>
                                    </div>

                                    <%--<div class="form-group col-md-4 has-feedback">
                                                <label  ><br><b>Note:</b><br>The template that you prepare here will be reflected on your site only if you have selected Enable Templating option before saving it.<br><br> Click on "?" (question mark) sign located opposite to each field to get hint about that field.</label>
                                            </div>--%>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">


                                    <div class="form-group col-md-12 has-feedback">
                                        <center>
                                            <%--<button type="submit" name="preview" class="btnblue" style="background: rgb(126, 204, 173);" onclick="doSubmit();">
                                                <span><i class="fa fa-save"></i></span>
                                                &nbsp;&nbsp;Save
                                            </button>--%>
                                            <button type="submit" name="preview" class="btn btn-default" style="display: -webkit-box;" onclick="doSubmit();"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=merchanttemplate_Save%></button>
                                        </center>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>

</html>
