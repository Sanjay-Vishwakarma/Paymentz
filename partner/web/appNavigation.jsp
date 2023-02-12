<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.NavigationVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.TreeSet" %>

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/15
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="ietest.jsp" %>



<%!
    private Logger logger = new Logger("appNavigation.jsp");
    private Functions mainFunctions = new Functions();

%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","listofapplicationmember");
    String readonly="";//this is for the icici module
   ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String appNavigation_List_Application_Member = StringUtils.isNotEmpty(rb1.getString("appNavigation_List_Application_Member")) ? rb1.getString("appNavigation_List_Application_Member") : "List Of Application Member";
    String appNavigation_List_MemberID = StringUtils.isNotEmpty(rb1.getString("appNavigation_List_MemberID")) ? rb1.getString("appNavigation_List_MemberID") : "Member ID";
    String appNavigation_Prev = StringUtils.isNotEmpty(rb1.getString("appNavigation_Prev")) ? rb1.getString("appNavigation_Prev") : "Prev";
    String appNavigation_Upload = StringUtils.isNotEmpty(rb1.getString("appNavigation_Upload")) ? rb1.getString("appNavigation_Upload") : "Upload";
    String appNavigation_Save = StringUtils.isNotEmpty(rb1.getString("appNavigation_Save")) ? rb1.getString("appNavigation_Save") : "Save";
    String appNavigation_List_Cancel = StringUtils.isNotEmpty(rb1.getString("appNavigation_List_Cancel")) ? rb1.getString("appNavigation_List_Cancel") : "Cancel";
    String appNavigation_Next = StringUtils.isNotEmpty(rb1.getString("appNavigation_Next")) ? rb1.getString("appNavigation_Next") : "Next";

    ActionVO actionVO=null;
    if(session.getAttribute("actionVO")!=null)
    {
        actionVO= (ActionVO) session.getAttribute("actionVO");
    }
%>
<%

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
        out.println(Functions.NewShowConfirmation("Sorry","NavigationVo is not in Session"));
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
    if(showPage!=null && !showPage.equals("") && showPage.equals("businessprofile.jsp"))
    {
        showPage = "appbusinessprofile.jsp";
    }

    if(session.getAttribute("actionVO")!=null)
    {
        actionVO= (ActionVO) session.getAttribute("actionVO");
    }
%>

<html>
<head>
    <title></title>


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
            background-color: #7eccad!important;
            color: #ffffff!important;
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

        #companycontent, #ownershipcontent, #businesscontent, #bankappcontent, #cardcontent, #uploadcontent{
            margin-top: 0;
        }

    </style>


</head>

<body class="bodybackground">
<%@ include file="top.jsp"%>
<%
    try{
%>
<%--<link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>
<script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
    });
</script>
<%--<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            &lt;%&ndash;<div class="panel-heading" >
              List Of Application Member

            </div>&ndash;%&gt;
            <%
                String selectedMemberID = "";
                if(request.getParameter("apptoid")!=null && !request.getParameter("apptoid").equals(""))
                {
                    selectedMemberID = request.getParameter("apptoid");
                    //request.setAttribute("selectedID",selectedMemberID);
                }
                else if(request.getParameter("selectedID")!=null && !request.getParameter("selectedID").equals(""))
                {
                    selectedMemberID =request.getParameter("selectedID");
                    //request.setAttribute("selectedID",selectedMemberID);
                }
                else if(request.getAttribute("selectedID")!=null && !request.getAttribute("selectedID").equals(""))
                {
                    selectedMemberID = (String) request.getAttribute("selectedID");
                }
                else if(session.getAttribute("applicationManagerVO")!=null)
                {
                    ApplicationManagerVO appManVO = (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
                    selectedMemberID = appManVO.getMemberId();
                }
            %>
            <form action="/partner/net/ListofAppMember" method="post" name="forms">

                <%!
                    ApplicationManager applicationManager = new ApplicationManager();
                %>
                <%List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getApplicationManagerVO(null);%>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member Id</td>
                                    <td width="12%" class="textb">

                                        <input type="hidden" name="apptoid" value="<%=selectedMemberID%>" class="textb">
                                        <input type="hidden" name="selectedID" value="<%=selectedMemberID%>" class="textb">
                                        <input class="txtbox" title="" type="Text" value=<%=ESAPI.encoder().encodeForHTMLAttribute(selectedMemberID)%> disabled style="border: 1px solid #b2b2b2;font-weight:bold"  type="Text"></td>

                                    </td>
                                    <td width="50%" class="textb">&nbsp;</td>
                                    <td>
                                        <form action="/partner/net/ListofAppMember" method="post" name="forms" >

                                            <%
                                                Enumeration<String> stringEnumeration=request.getParameterNames();
                                                while(stringEnumeration.hasMoreElements())
                                                {
                                                    String name=stringEnumeration.n4extElement();
                                                    if("action".equals(name))
                                                    {
                                                        out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)+"'/>");
                                                    }
                                                    else
                                                        out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                                }
                                            %>
                                            <button type="submit" value="Go Back" name="submit" class="addnewmember" name="B1" style="width: 120px;">
                                                <i class="fa fa-sign-in"></i>
                                                &nbsp;Go Back
                                            </button>
                                        </form>
                                    </td>
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
</div>--%>


<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading" style="margin-bottom: 0;">

            <div class="row">



                <div class="col-sm-12 portlets ui-sortable">

                    <div class="pull-right">
                        <div class="btn-group">
                            <form action="/partner/net/ListofAppMember" method="post" name="forms" >

                                <%
                                    Enumeration<String> stringEnumeration=request.getParameterNames();
                                    while(stringEnumeration.hasMoreElements())
                                    {
                                        String name=stringEnumeration.nextElement();
                                        if("action".equals(name))
                                        {
                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)+"'/>");
                                        }
                                        else
                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                    }
                                %>
                                <button class="addnewmember btn-xs" value="listofapplicationmember" name="submit" type="submit" name="B1" style="background: transparent;border: 0;">
                                    <img style="height: 35px;" src="/partner/images/goBack.png">
                                </button>

                            </form>
                        </div>
                    </div>
                    <br><br><br>


                    <div class="widget" style="margin-bottom: 0;">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=appNavigation_List_Application_Member%></strong></h2>
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

                                <%--<%
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
                                %>--%>


                                <%
                                    String selectedMemberID = "";
                                    if(request.getParameter("apptoid")!=null && !request.getParameter("apptoid").equals(""))
                                    {
                                        selectedMemberID = request.getParameter("apptoid");
                                        //request.setAttribute("selectedID",selectedMemberID);
                                    }
                                    else if(request.getParameter("selectedID")!=null && !request.getParameter("selectedID").equals(""))
                                    {
                                        selectedMemberID =request.getParameter("selectedID");
                                        //request.setAttribute("selectedID",selectedMemberID);
                                    }
                                    else if(request.getAttribute("selectedID")!=null && !request.getAttribute("selectedID").equals(""))
                                    {
                                        selectedMemberID = (String) request.getAttribute("selectedID");
                                    }
                                    else if(session.getAttribute("applicationManagerVO")!=null)
                                    {
                                        ApplicationManagerVO appManVO = (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
                                        selectedMemberID = appManVO.getMemberId();
                                    }
                                %>

                                <form action="/partner/net/ListofAppMember" method="post" name="forms" class="form-horizontal">


                                    <%!
                                        ApplicationManager applicationManager = new ApplicationManager();
                                    %>
                                    <%List<ApplicationManagerVO> applicationManagerVOList=applicationManager.getApplicationManagerVO(null);%>
                                    <div class="form-group col-md-12">
                                        <label class="col-sm-4 control-label"><%=appNavigation_List_MemberID%></label>
                                        <div class="col-sm-4">
                                            <input type="hidden" name="apptoid" value="<%=selectedMemberID%>" class="textb">
                                            <input type="hidden" name="selectedID" value="<%=selectedMemberID%>" class="textb">
                                            <input class="form-control" title="" type="Text" value=<%=ESAPI.encoder().encodeForHTMLAttribute(selectedMemberID)%> disabled style="border: 1px solid #b2b2b2;font-weight:bold"  type="Text">
                                        </div>
                                    </div>


                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                                    <%--<div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            &lt;%&ndash;<button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;Search</button>&ndash;%&gt;

                                            <form action="/partner/net/ListofAppMember" method="post" name="forms" >

                                                <%
                                                    Enumeration<String> stringEnumeration=request.getParameterNames();
                                                    while(stringEnumeration.hasMoreElements())
                                                    {
                                                        String name=stringEnumeration.nextElement();
                                                        if("action".equals(name))
                                                        {
                                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)+"'/>");
                                                        }
                                                        else
                                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                                    }
                                                %>
                                                &lt;%&ndash;<button type="submit" value="Go Back" name="submit" class="addnewmember btn btn-default" name="B1" style="width: 120px;">
                                                    <i class="fa fa-sign-in"></i>
                                                    &nbsp;Go Back
                                                </button>
        &ndash;%&gt;
                                                <button class="addnewmember btn-xs" value="Go Back" name="submit" type="submit" name="B1" style="background: transparent;border: 0;">
                                                    <img style="height: 35px;" src="/partner/images/goBack.png">
                                                </button>

                                            </form>
                                        </div>
                                    </div>
--%>

                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>


<script src='/partner/stylenew/BeforeAppManager.js'></script>
<%-- End adding for listof Application Member page--%>
<form name="myformname" id="myformname" action="/partner/net/Navigation?ctoken=<%=ctoken%>"   method="post">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <input type="hidden" value="<%=currentPageNO%>" name="currentPage">


    <input type="hidden" value="<%=selectedMemberID%>" name="selectedID">
    <%--Strt test tab--%>
    <div class="skins-nav" >
        <%for(Map.Entry<Integer,String> stepAndPageName:navigationVO.getStepAndPageName().entrySet()){%>
        <p  class="skin second_nav">
            <button type="submit" class="appstepstab" name="apptab" style="<%=currentPageNO==stepAndPageName.getKey()?"background-color:#7eccad;color:#ffffff":""%>" value="<%=stepAndPageName.getKey()%>">Step-<%=stepAndPageName.getKey()%></button>
        </p>
        <%}%>
    </div>

    <%--End test tab--%>
    <jsp:include page="<%=showPage%>">
        <jsp:param name="currentPageNO" value="<%=currentPageNO%>"/>
    </jsp:include>

    <%-- <div class="container-fluid ">
         <div class="row" style="margin-left: 226px;background-color: #ffffff">
             <div class="col-md-8 col-md-offset-2" style="margin-top:11px;">

                 <div class="form-group col-md-3 has-feedback">
                     <button type="submit" style="float:right;"  class="buttonform" name="previous" value="<%=previousPageNO%>" <%=disablePreviousPage%>>
                         <i class="fa fa-angle-double-left" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Prev
                     </button>
                 </div>
                 <%if(navigationVO.getStepAndPageName().get(currentPageNO).equals("upload.jsp"))
                 {
                 %>
                 <div class="form-group col-md-3 has-feedback">
                     <button type="submit"  style="float:right;"class="buttonform" name="upload" value="<%=currentPageNO%>" onclick="myUpload('<%=ctoken%>')">
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
                     <button type="submit" style="float:right;" class="buttonform" name="save" value="<%=currentPageNO%>">
                         <i class="fa fa-clock-o" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Save
                     </button>
                 </div>
                 <%
                     }
                 %>
                 <div class="form-group col-md-3 has-feedback">
                     <button type="submit" style="float:left;"  class="buttonform" name="cancel">
                         <i class="fa fa-clock-o" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Cancel
                     </button>
                 </div>
                 <div class="form-group col-md-3 has-feedback">
                     <button type="submit" style="float:left;"  class="buttonform" name="next" value="<%=nextPageNO%>" <%=disableNextPage%>>

                         Next&nbsp;&nbsp;<i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                     </button>
                 </div>

                 <%if(mainFunctions.isValueNull(disableNextPage))
                 {
                 %>
             </div>
         </div>
     </div>
     <div class="container-fluid ">
         <div class="row" style="margin-left: 226px;margin-bottom: 12px;background-color: #ffffff">
             <div class="col-md-8 col-md-offset-2">

                 <div class="form-group col-md-12 has-feedback">
                     <center><button type="submit"  class="buttonform" name="appsubmit"  value="<%=currentPageNO%>">
                         <i class="fa fa-clock-o" class="iconbuttonform"></i>
                         &nbsp;&nbsp;Submit
                     </button></center>
                 </div>
                 <%}%>
             </div></div></div>--%>
<div class="content-page" <%--style="margin-left: 0;"--%>>
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
                                        <button type="submit" id="newbtn" class="btn btn-default" name="previous" value="<%=previousPageNO%>" <%=disablePreviousPage%>>
                                            <i class="fa fa-angle-double-left" class="iconbuttonform"></i>
                                            &nbsp;&nbsp;<%=appNavigation_Prev%>
                                        </button>
                                        <%if(navigationVO.getStepAndPageName().get(currentPageNO).equals("upload.jsp"))
                                        {
                                        %>

                                        <%--<button type = "button" id="newbtn" class = "btn btn-default">Button 2</button>--%>
                                        <button type="submit" id="newbtn" class="btn btn-default" name="upload" value="<%=currentPageNO%>" onclick="myUpload('<%=ctoken%>')">
                                            <i class="fa fa-upload" class="iconbuttonform"></i>
                                            &nbsp;&nbsp;<%=appNavigation_Upload%>
                                        </button>
                                        <%
                                        }
                                        else
                                        {
                                        %>

                                        <%--<button type = "button" id="newbtn" class = "btn btn-default">Button 3</button>--%>
                                        <button type="submit" id="newbtn" class="btn btn-default" name="save" value="<%=currentPageNO%>">
                                            <i class="fa fa-floppy-o" class="iconbuttonform"></i>
                                            &nbsp;&nbsp;<%=appNavigation_Save%>
                                        </button>
                                        <%
                                            }
                                        %>

                                        <%--<button type = "button" id="newbtn" class = "btn btn-default">Button 4</button>--%>
                                        <button type="submit" id="newbtn" class="btn btn-default" name="cancel">
                                            <i class="fa fa-times" class="iconbuttonform"></i>
                                            &nbsp;&nbsp;<%=appNavigation_List_Cancel%>
                                        </button>

                                        <%if(mainFunctions.isValueNull(disableNextPage))
                                        {
                                        %>

                                        <button type="submit" id="newbtn" class="btn btn-default" name="appsubmit" value="<%=currentPageNO%>">

                                            <i class="fa fa-clock-o" class="iconbuttonform"></i>&nbsp;Submit
                                        </button>

                                        <%}%>

                                        <button type="submit" id="newbtn" class="btn btn-default" name="next" value="<%=nextPageNO%>" <%=disableNextPage%>>

                                            <%=appNavigation_Next%>&nbsp;&nbsp;<i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                                        </button>

                                    </div>

                                    <%--<%if(mainFunctions.isValueNull(disableNextPage))
                                    {
                                    %>


                                    <div class="form-group col-md-12 has-feedback" style="margin-bottom: 0;padding-left: 10px;">
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

<script src='/partner/stylenew/AfterAppManager.js'></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">

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
<%
    }catch(Exception e)
    {
        logger.error("exception in application::",e);
    }
%>
