<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.vo.applicationManagerVOs.NavigationVO" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.manager.vo.ActionVO" %>

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
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","");
    String readonly="";//this is for the icici module


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

    if(session.getAttribute("actionVO")!=null)
    {
        actionVO= (ActionVO) session.getAttribute("actionVO");
    }
%>

<html>
<head>
    <title></title>



</head>

<body class="bodybackground">
<%@include file="index.jsp"%>
<%!
    private Functions functions=new Functions();
%>
<%
    try{
%>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
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
</script>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                List Of Application Member

            </div>

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
            <form action="/icici/servlet/ListofAppMember?ctoken=<%=ctoken%>" method="post" name="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">

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
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <%--<select name="apptoid" class="txtbox">
                                            <option value="">Select Application</option>
                                            <%

                                                String memberId = (String) request.getAttribute("memberId");

                                                for(ApplicationManagerVO applicationManagerVO : applicationManagerVOList)
                                                {
                                                    //out.println("<option value=\""+applicationManagerVO.getMemberId()+"\" "+((applicationManagerVO.getMemberId().equals(request.getParameter("apptoid")))?"selected":"")+">"+applicationManagerVO.getApplicationId()+"_"+applicationManagerVO.getMemberId()+"</option>");
                                                    if(functions.isValueNull(memberId) && memberId.equals(applicationManagerVO.getMemberId().split("_")[0]))
                                                    {
                                                        out.println("<option value=\""+(applicationManagerVO.getMemberId().split("_"))[0]+"\" selected>"+(applicationManagerVO.getMemberId().split("_"))[0]+"</option>");
                                                    }
                                                    else
                                                        out.println("<option value=\""+(applicationManagerVO.getMemberId().split("_"))[0]+"\">"+(applicationManagerVO.getMemberId().split("_"))[0]+"</option>");
                                                }
                                            %>
                                        </select>--%>
                                        <input type="hidden" name="apptoid" value="<%=selectedMemberID.toString()%>" class="textb">
                                        <input type="hidden" name="selectedID" value="<%=selectedMemberID.toString()%>" class="textb">
                                        <input class="textb" title="" value=<%=ESAPI.encoder().encodeForHTMLAttribute(selectedMemberID)%> disabled type="Text">


                                    </td>
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

<script src='/icici/stylenew/BeforeAppManager.js'></script>
<%-- End adding for listof Application Member page--%>
<form name="myformname" id="myformname" action="/icici/servlet/Navigation?ctoken=<%=ctoken%>"   method="post">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <input type="hidden" value="<%=currentPageNO%>" name="currentPage">
    <%--Strt test tab--%>
    <div class="skins-nav" >
        <%for(Map.Entry<Integer,String> stepAndPageName:navigationVO.getStepAndPageName().entrySet()){%>
        <p  class="skin second_nav">
            <button type="submit" class="appstepstab" name="apptab" style="<%=currentPageNO==stepAndPageName.getKey()?"background-color:#34495e;color:#ffffff":""%>" value="<%=stepAndPageName.getKey()%>">Step-<%=stepAndPageName.getKey()%></button>
        </p>
        <%}%>
    </div>

    <%--End test tab--%>
    <jsp:include page="<%=showPage%>">
        <jsp:param name="currentPageNO" value="<%=currentPageNO%>"/>
    </jsp:include>
    <%--<div class="reporttable">
        <table  width="100%" border="0">
            <tr>
                <td>
                    <button type="submit" style="float:right;"  class="buttonform" name="previous" value="<%=previousPageNO%>" <%=disablePreviousPage%>>
                        <i class="fa fa-angle-double-left" class="iconbuttonform"></i>
                        &nbsp;&nbsp;Prev
                    </button>
                </td>
                <%if(navigationVO.getStepAndPageName().get(currentPageNO).equals("upload.jsp"))
                {
                %>
                <td style="padding-right: 10px;">
                    <button type="submit"  style="float:right;"class="buttonform" name="upload" value="<%=currentPageNO%>" onclick="myUpload('<%=ctoken%>')">
                        <i class="fa fa-clock-o" class="iconbuttonform"></i>
                        &nbsp;&nbsp;Upload
                    </button>
                </td>
                <%
                }
                else
                {
                %>
                <td style="padding-right: 10px;">
                    <button type="submit" style="float:right;" class="buttonform" name="save" value="<%=currentPageNO%>">
                        <i class="fa fa-clock-o" class="iconbuttonform"></i>
                        &nbsp;&nbsp;Save
                    </button>
                </td>
                <%
                    }
                %>
                <td>
                    <button type="submit" style="float:left;"  class="buttonform" name="cancel">
                        <i class="fa fa-clock-o" class="iconbuttonform"></i>
                        &nbsp;&nbsp;Cancel
                    </button>
                </td>
                <td>
                    <button type="submit" style="float:left;"  class="buttonform" name="next" value="<%=nextPageNO%>" <%=disableNextPage%>>

                        Next&nbsp;&nbsp;<i class="fa fa-angle-double-right" class="iconbuttonform"></i>
                    </button>
                </td>
            </tr>

            <tr>
                <td>&nbsp;</td>
            </tr>
        </table>
        <%if(mainFunctions.isValueNull(disableNextPage))
        {
        %>
        <table align="center">
            <tr>
                <td align="center">
                    <button type="submit"  class="buttonform" name="appsubmit"  value="<%=currentPageNO%>">
                        <i class="fa fa-clock-o" class="iconbuttonform"></i>
                        &nbsp;&nbsp;Submit
                    </button>
                </td>
            </tr>

        </table>
        <%}%>
    </div>--%>

    <div class="container-fluid ">
        <div class="row" style="margin-left: 226px;background-color: #ffffff; margin-top: 15px;">
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
            </div></div></div>

</form>
<%--<%
    }
%>--%>
<script src='/icici/stylenew/AfterAppManager.js'></script>


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
