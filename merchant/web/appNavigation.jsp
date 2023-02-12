<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.vo.applicationManagerVOs.NavigationVO" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/15
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    session.setAttribute("submit" ,"PopulateApplication");
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<script src='/merchant/stylenew01/BeforeAppManager.js'></script>
<%!
    private Logger logger = new Logger("appNavigation.jsp");
    private Functions mainFunctions = new Functions();
%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","");
    response.setHeader("X-Frame-Options", "ALLOWALL");
    session.setAttribute("X-Frame-Options", "ALLOWALL");

%>
<%
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String appNavigation_Prev = rb1.getString("appNavigation_Prev");
    String appNavigation_Upload = rb1.getString("appNavigation_Upload");
    String appNavigation_Save = rb1.getString("appNavigation_Save");
    String appNavigation_Cancel = rb1.getString("appNavigation_Cancel");
    String appNavigation_Submit = rb1.getString("appNavigation_Submit");
    String appNavigation_Next = rb1.getString("appNavigation_Next");
    String appNavigation_Sorry_msg = rb1.getString("appNavigation_Sorry_msg");
    String appNavigation_Navigation_msg = rb1.getString("appNavigation_Navigation_msg");

    String disablePreviousPage="disabled";
    String disableNextPage="disabled";
    Set<Integer> contextInvalid= new TreeSet<Integer>();

    NavigationVO navigationVO=null;
    if(session.getAttribute("navigationVO")!=null)
    {
        navigationVO= (NavigationVO) session.getAttribute("navigationVO");
    }
    else
    {
        out.println("<br><br><br>");
        out.println(Functions.NewShowConfirmation1("appNavigation_Sorry_msg","appNavigation_Navigation_msg"));
    }


    int previousPageNO=0;
    int currentPageNO=1;
    int nextPageNO =2;
    if("ERR".equals(request.getParameter("MES")))
    {
        logger.debug("inside ERR");
        if(request.getAttribute("validationErrorList")!=null)
        {
            currentPageNO=0;
            logger.debug("inside validationErrorList");


            ValidationErrorList validationErrorList= (ValidationErrorList) request.getAttribute("validationErrorList");
            for(ValidationException validationException : validationErrorList.errors())
            {
                logger.debug(" inside loop PageNo::"+validationException.getContext()+" validation Meassage:::"+validationException.getLocalizedMessage());
                contextInvalid.add(Integer.valueOf(validationException.getContext()));
                if(currentPageNO==0 || currentPageNO>Integer.valueOf(validationException.getContext()))
                {

                    logger.debug("INSIDE IF");
                    currentPageNO=Integer.valueOf(validationException.getContext());
                    logger.debug("currentPage no::::::"+currentPageNO);
                }

            }

            if(mainFunctions.isValueNull(request.getParameter("previous")) )
            {
                if(contextInvalid.contains(Integer.valueOf(request.getParameter("previous"))+1))
                {
                    currentPageNO=Integer.valueOf(request.getParameter("previous"))+1;
                }
                else
                {
                    currentPageNO=Integer.valueOf(request.getParameter("previous"));
                }
            }
            else if(mainFunctions.isValueNull(request.getParameter("next")) )
            {
                if(contextInvalid.contains(Integer.valueOf(request.getParameter("next"))-1))
                {
                    currentPageNO=Integer.valueOf(request.getParameter("next"))-1;
                }
                else
                {
                    currentPageNO=Integer.valueOf(request.getParameter("next"));
                }
            }


        }
    }
    else if(mainFunctions.isValueNull(request.getParameter("previous")))
    {
        currentPageNO=Integer.valueOf(request.getParameter("previous"));

    }
    else if(mainFunctions.isValueNull(request.getParameter("next")))
    {
        currentPageNO= Integer.valueOf(request.getParameter("next"));

    }
    else if(mainFunctions.isValueNull(request.getParameter("save")))
    {
        currentPageNO= Integer.valueOf(request.getParameter("save"));
    }
    else if(mainFunctions.isValueNull(request.getParameter("appsubmit")))
    {
        currentPageNO= Integer.valueOf(request.getParameter("appsubmit"));
    }
    else if(mainFunctions.isValueNull(request.getParameter("apptab")))
    {
        currentPageNO=Integer.valueOf(request.getParameter("apptab"));
    }
    else if(mainFunctions.isValueNull(request.getParameter("upload")))
    {
        currentPageNO=Integer.valueOf(request.getParameter("upload"));
    }
    else if(mainFunctions.isValueNull((String)session.getAttribute("pageno"))){
        currentPageNO=Integer.valueOf((String)session.getAttribute("pageno"));
    }

    previousPageNO=currentPageNO-1;
    nextPageNO=currentPageNO+1;
    if(navigationVO.getStepAndPageName().containsKey(previousPageNO))
    {
        disablePreviousPage="";
    }

    if(navigationVO.getStepAndPageName().containsKey(nextPageNO))
    {
        disableNextPage="";
    }


    String showPage=navigationVO.getStepAndPageName().get(currentPageNO);
%>
<%--<div id="page-content-wrapper">
    <div class="container-fluid xyz">--%>

<html>


<head>
    <title><%=company%> Merchant Application</title>
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
    <script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
    <%--<link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>--%>
    <%--<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <style type="text/css">
        .skins-nav {
            opacity: 1;
            -webkit-transition: right .3s;
            -moz-transition: right .3s;
            -ms-transition: right .3s;
            -o-transition: right .3s;
            transition: right .3s;
            position: fixed;
            right: 0px;
            top: 141px;
            font-size: 13px;
            z-index: 9999;
        }

        .skins-nav p{
            border-left: 1px solid #bbbbbb;
            border-top: 1px solid #bbbbbb;
            border-bottom: 1px solid #bbbbbb;
            margin: 0 0 1px;
            box-shadow: 0px 0px 14px;
        }

        .appstepstab {
            background-color: #ffffff;
            color: #000000;
            border: 0px;
            height: 35px;
            transition: 0.5s;
        }

        .appstepstab:hover{
            background-color: #ABB7B7;
            color: #ffffff;
            transform: scale(1.2);
        }

        .appstepstab:active{
            background-color: #7eccad;
            color: #ffffff;
        }

        /*        .btn-toolbar>.btn, .btn-toolbar>.btn-group, .btn-toolbar>.input-group {
                    margin-left: 5px!important;
                }*/

        #newbtn{
            margin-bottom: 5px;
            margin-left: inherit;
        }

        .btn-toolbar {
            margin-left: 20px!important;
        }

    </style>
    <script>


        //        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
        function getpageno(currentpage)
        {
            document.getElementById("pageNoInJSP").setAttribute('value',currentpage);
        }

    </script>
</head>

<body>
<%@ include file="Top.jsp" %>
<%
    try{
%>
<%--<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
    });
</script>--%>
<form action="/merchant/servlet/Navigation?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" method="post" name="myformname" id="myformname">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <input type="hidden" value="<%=currentPageNO%>" name="currentPage">
    <input type="hidden" value="" name="pageNoInJSP" id="pageNoInJSP">

<%--Strt test tab--%>
    <div class="skins-nav" >
        <%for(Map.Entry<Integer,String> stepAndPageName:navigationVO.getStepAndPageName().entrySet()){%>
        <p  class="skin second_nav">
            <button type="submit" class="appstepstab" name="apptab" onclick="getpageno(<%=stepAndPageName.getKey()%>)" id="<%=currentPageNO==stepAndPageName.getKey()?"activeid":""%>" style="<%=currentPageNO==stepAndPageName.getKey()?"color:#ffffff":""%>"  value="<%=stepAndPageName.getKey()%>">Step-<%=stepAndPageName.getKey()%> </button>
        </p>
        <%}%>
    </div>

    <%--End test tab--%>
    <jsp:include page="<%=showPage%>">
        <jsp:param name="currentPageNO" value="<%=currentPageNO%>"/>
    </jsp:include>
    <%-- <div class="container-fluid ">&lt;%&ndash; style="width: 97%"&ndash;%&gt;
         <div class="row" style="background-color: #ffffff">&lt;%&ndash;margin-left: 226px;&ndash;%&gt;
             <div class="col-md-8 col-md-offset-2" style="margin-top:5px;">

                 <div class="form-group col-md-3 has-feedback">
                     <button type="submit" style="float:right;"  class="btn btn-default" name="previous" value="<%=previousPageNO%>" <%=disablePreviousPage%>>
                         <i class="fa fa-angle-double-left" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Prev
                     </button>
                 </div>
             <%if(navigationVO.getStepAndPageName().get(currentPageNO).equals("upload.jsp"))
             {
             %>
              <div class="form-group col-md-3 has-feedback">
                 <button type="submit"  style="float:right;"class="btn btn-default" name="upload" value="<%=currentPageNO%>" onclick="myUpload('<%=ctoken%>')">
                     <i class="fa fa-clock-o" class="iconbuttonform"></i>
                     &nbsp;&nbsp;Upload
                 </button>
             </div>
             <%
                 }
                 else
                 {
             %>
             <div class="form-group col-md-3 has-feedback">
                 <button type="submit" style="float:right;" class="btn btn-default" name="save" value="<%=currentPageNO%>">
                     <i class="fa fa-clock-o" class="iconbuttonform"></i>
                     &nbsp;&nbsp;Save
                 </button>
             </div>
             <%
                 }
             %>
             <div class="form-group col-md-3 has-feedback">
                 <button type="submit" style="float:left;"  class="btn btn-default" name="cancel">
                     <i class="fa fa-clock-o" class="iconbuttonform"></i>
                     &nbsp;&nbsp;Cancel
                 </button>
             </div>
             <div class="form-group col-md-3 has-feedback">
                 <button type="submit" style="float:left;"  class="btn btn-default" name="next" value="<%=nextPageNO%>" <%=disableNextPage%>>

                     Next&nbsp;&nbsp;<i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                 </button>
             </div>



             <%if(mainFunctions.isValueNull(disableNextPage))
             {
             %>
             &lt;%&ndash;</div>
         </div>
     </div>
     <div class="container-fluid ">
         <div class="row" style="margin-left: 226px;margin-bottom: 12px;background-color: #ffffff">
             <div class="col-md-8 col-md-offset-2">&ndash;%&gt;

                 <div class="form-group col-md-12 has-feedback">
                     <center><button type="submit"  class="btn btn-default" name="appsubmit"  value="<%=currentPageNO%>">
                         <i class="fa fa-clock-o" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Submit
                     </button></center>
                 </div>
             <%}%>
                 </div>



             <div class = "btn-toolbar" role = "toolbar">

                 <div class = "btn-group">
                     &lt;%&ndash;<button type = "button" id="newbtn" class = "btn btn-default">Button 1</button>&ndash;%&gt;
                     <button type="submit" style="float:right;" id="newbtn" class="btn btn-default" name="previous" value="<%=previousPageNO%>" <%=disablePreviousPage%>>
                         <i class="fa fa-angle-double-left" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Prev
                     </button>
                         <%if(navigationVO.getStepAndPageName().get(currentPageNO).equals("upload.jsp"))
                         {
                         %>

                     &lt;%&ndash;<button type = "button" id="newbtn" class = "btn btn-default">Button 2</button>&ndash;%&gt;
                         <button type="submit"  style="float:right;" id="newbtn" class="btn btn-default" name="upload" value="<%=currentPageNO%>" onclick="myUpload('<%=ctoken%>')">
                             <i class="fa fa-clock-o" class="iconbuttonform"></i>
                             &nbsp;&nbsp;Upload
                         </button>
                         <%
                         }
                         else
                         {
                         %>

                     &lt;%&ndash;<button type = "button" id="newbtn" class = "btn btn-default">Button 3</button>&ndash;%&gt;
                         <button type="submit" style="float:right;" id="newbtn" class="btn btn-default" name="save" value="<%=currentPageNO%>">
                             <i class="fa fa-clock-o" class="iconbuttonform"></i>
                             &nbsp;&nbsp;Save
                         </button>
                         <%
                             }
                         %>

                     &lt;%&ndash;<button type = "button" id="newbtn" class = "btn btn-default">Button 4</button>&ndash;%&gt;
                         <button type="submit" style="float:left;" id="newbtn" class="btn btn-default" name="cancel">
                             <i class="fa fa-clock-o" class="iconbuttonform"></i>
                             &nbsp;&nbsp;Cancel
                         </button>

                         <button type="submit" style="float:left;" id="newbtn" class="btn btn-default" name="next" value="<%=nextPageNO%>" <%=disableNextPage%>>

                             Next&nbsp;&nbsp;<i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                         </button>



                 </div>

                 <%if(mainFunctions.isValueNull(disableNextPage))
                 {
                 %>


                 <div class="form-group col-md-12 has-feedback">
                     <center><button type="submit"  class="btn btn-default" name="appsubmit"  value="<%=currentPageNO%>">
                         <i class="fa fa-clock-o" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Submit
                     </button></center>
                 </div>
                 <%}%>

                 &lt;%&ndash; <div class = "btn-group">
                      <button type = "button" class = "btn btn-default">Button 7</button>
                      <button type = "button" class = "btn btn-default">Button 8</button>
                      <button type = "button" class = "btn btn-default">Button 9</button>
                  </div>&ndash;%&gt;

             </div>


         </div>
     </div>--%>


    <div class="content-page" >
        <div class="content" style="margin-top:0">
            <!-- Page Heading Start -->
            <div class="page-heading">

                <div class="row">

                    <div class="col-sm-12 portlets ui-sortable">

                        <div class="widget">

                            <div class="widget-content padding">

                                <div id="horizontal-form">

                                    <div class="col-md-4"></div>

                                    <%--<div class = "btn-toolbar" role = "toolbar">

                                        <div class = "btn-group">
                                            <button type = "button" id="newbtn" class = "btn btn-default">Button 1</button>
                                            <button type = "button" id="newbtn" class = "btn btn-default">Button 2</button>

                                            <button type = "button" id="newbtn" class = "btn btn-default">Button 3</button>
                                            <button type = "button" id="newbtn" class = "btn btn-default">Button 4</button>
                                        </div>

                                        &lt;%&ndash; <div class = "btn-group">
                                             <button type = "button" class = "btn btn-default">Button 7</button>
                                             <button type = "button" class = "btn btn-default">Button 8</button>
                                             <button type = "button" class = "btn btn-default">Button 9</button>
                                         </div>&ndash;%&gt;

                                    </div>--%>


                                    <div class = "btn-toolbar" role = "toolbar">

                                        <div class = "btn-group" style="display: block;">
                                            <%--<button type = "button" id="newbtn" class = "btn btn-default">Button 1</button>--%>
                                            <button type="submit" id="newbtn" class="btn btn-default" name="previous" value="<%=previousPageNO%>" <%=disablePreviousPage%> onclick="getpageno(<%=previousPageNO%>)";>
                                                <i class="fa fa-angle-double-left" class="iconbuttonform"></i>
                                                &nbsp;&nbsp;<%=appNavigation_Prev%> </button>
                                            <%if(navigationVO.getStepAndPageName().get(currentPageNO).equals("upload.jsp"))
                                            {
                                            %>

                                            <%--<button type = "button" id="newbtn" class = "btn btn-default">Button 2</button>--%>
                                            <button type="submit" id="newbtn" class="btn btn-default" name="upload" value="<%=currentPageNO%>" onclick="myUpload('<%=ctoken%>','<%=copyiframe%>')" onclick="getpageno(<%=currentPageNO%>)">
                                                <i class="fa fa-upload" class="iconbuttonform"></i>
                                                &nbsp;&nbsp;<%=appNavigation_Upload%>
                                            </button>
                                            <%
                                            }
                                            else
                                            {
                                            %>

                                            <%--<button type = "button" id="newbtn" class = "btn btn-default">Button 3</button>--%>
                                            <button type="submit" id="newbtn" class="btn btn-default" name="save" value="<%=currentPageNO%>" onclick="getpageno(<%=currentPageNO%>)">
                                                <i class="fa fa-floppy-o" class="iconbuttonform"></i>
                                                &nbsp;&nbsp;<%=appNavigation_Save%>
                                            </button>
                                            <%
                                                }
                                            %>

                                            <%--<button type = "button" id="newbtn" class = "btn btn-default">Button 4</button>--%>
                                            <button type="submit" id="newbtn" class="btn btn-default" name="cancel" onclick="getpageno(<%=currentPageNO%>)">
                                                <i class="fa fa-times" class="iconbuttonform"></i>
                                                &nbsp;&nbsp;<%=appNavigation_Cancel%>
                                            </button> &nbsp;&nbsp;

                                            <%if(mainFunctions.isValueNull(disableNextPage))
                                            {
                                            %>

                                            <button type="submit" id="newbtn" class="btn btn-default" name="appsubmit"  value="<%=currentPageNO%>" onclick="getpageno(<%=currentPageNO%>)">
                                                <i class="fa fa-clock-o" class="iconbuttonform"></i>
                                                &nbsp;&nbsp;<%=appNavigation_Submit%>
                                            </button>

                                            <%}%>

                                            <button type="submit" id="newbtn" class="btn btn-default" name="next" value="<%=nextPageNO%>" <%=disableNextPage%> onclick="getpageno(<%=nextPageNO%>)">

                                                <%=appNavigation_Next%>&nbsp;&nbsp;<i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                                            </button>



                                        </div>

                                        <%--<%if(mainFunctions.isValueNull(disableNextPage))
                                        {
                                        %>


                                        <div class="form-group col-md-12 has-feedback" style="margin-bottom: 0;">
                                            <center><button type="submit"  class="btn btn-default" name="appsubmit"  value="<%=currentPageNO%>">
                                                <i class="fa fa-clock-o" class="iconbuttonform"></i>
                                                &nbsp;&nbsp;Submit
                                            </button></center>
                                        </div>
                                        <%}%>--%>

                                        <%-- <div class = "btn-group">
                                             <button type = "button" class = "btn btn-default">Button 7</button>
                                             <button type = "button" class = "btn btn-default">Button 8</button>
                                             <button type = "button" class = "btn btn-default">Button 9</button>
                                         </div>--%>

                                    </div>

                                </div>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>

    </div>


</form>
<script src='/merchant/stylenew01/AfterAppManager.js'></script>

<script>
    $("form#myformname :input[type='text'],[type='password'],[type='email']").each(function(){
        $(this).attr({
            'pattern': '^[^<>=]*$'
        });

        $(this).attr("oninvalid", "InvalidMsg(this,'Details');");
        $(this).attr("oninput", "InvalidMsg(this,'Details');");
    });

    function InvalidMsg(textbox, details) {
        var name = textbox.name;

        if(textbox.validity.patternMismatch){

            $('input[name=' + name + ']').after("<i class='form-control-feedback glyphicon glyphicon-remove erroricon ' ></i>");
            textbox.setCustomValidity("");
        }
        else {
            $('input[name='+name+']').nextAll().remove("i");
            textbox.setCustomValidity('');
        }
        return true;
    }
    document.addEventListener('invalid', (function(){
        return function(e){

            e.preventDefault();

        };
    })(), true);

</script>

</body>
</html>
<%--    </div>
</div>--%>
<%
    }catch(Exception e)
    {
        logger.error("exception in application::",e);
    }
%>
