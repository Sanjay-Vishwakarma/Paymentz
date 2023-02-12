<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: kajal
  Date: 2/23/15
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%
    Logger logger = new Logger("appManagerAction.jsp");
    logger.debug("Entering into AppManagerAction.jsp file------");
    List<ApplicationManagerVO> applicationManagerVOList1 = (List<ApplicationManagerVO>) request.getAttribute("applicationManagerVOs");
    ApplicationManagerVO applicationManagerVO = new ApplicationManagerVO();
    User  user =  (User)session.getAttribute("ESAPIUserSessionKey");


%>
<%--<div class="container-fluid ">
<div class="row" style="margin-top: 100px;background-color: #ffffff">
    <div class="col-md-8 col-md-offset-2" style="margin-top: 4%;margin-bottom: 1%;">
  &lt;%&ndash;<div class="col-lg-12" style="margin-top: 8%">&ndash;%&gt;
    &lt;%&ndash;<div class="panel panel-default" style="margin-top: 0%">&ndash;%&gt;

      <form action="/partner/listofapplicationmember.jsp" method="post" name="forms" >
        <input type="hidden" value="<%=user.getCSRFToken()%>" name="ctoken">

          <div class="form-group col-md-8" style="margin-left: -24%; ">
              <label class="col-sm-3 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Member Id</label>
          <div class="form-group col-sm-4 " style="margin-top: -2%">
              &lt;%&ndash;<input type="text"  class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">&ndash;%&gt;
              <%
                  String selectedMemberID = "";
                  if(request.getParameter("apptoid")!=null && !request.getParameter("apptoid").equals(""))
                  {
                      selectedMemberID = request.getParameter("apptoid");
                      request.setAttribute("selectedID",selectedMemberID);
                  }
                  else if(request.getParameter("selectedID")!=null && !request.getParameter("selectedID").equals(""))
                  {
                      selectedMemberID =request.getParameter("selectedID");
                  }
                  else if(request.getAttribute("selectedID")!=null && !request.getAttribute("selectedID").equals(""))
                  {
                      selectedMemberID = (String) request.getAttribute("selectedID");
                  }
              %>
              <input type="hidden" name="selectedID" value="<%=selectedMemberID%>" class="textb">
              <input type="hidden" name="apptoid" value="<%=selectedMemberID%>" class="textb">
              <input class="form-control" title="" type="text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(selectedMemberID)%>" disabled style="border: 1px solid #b2b2b2;font-weight:bold;"  type="Text">

          </div>
              </div>
          <div class="form-group col-md-2 has-feedback">
              </div>

          <div class="form-group col-md-2 has-feedback" style="margin-left: 24%">
              <button type="submit" value="Go Back" name="submit" class="buttonform" name="B1" style="">
                  <i class="fa fa-sign-in"></i>
                  &nbsp;Go Back
              </button>
          </div>
      </form>

    </div>
  </div>
</div>--%>


<div class="content-page" style="margin-bottom: 0;">
    <div class="content"<%-- style="margin-top: 0;"--%>>
        <!-- Page Heading Start -->
        <div class="page-heading" style="margin-bottom: 0;">

            <div class="row">



                <div class="col-sm-12 portlets ui-sortable">

                    <div class="pull-right">
                        <div class="btn-group">
                            <%--<form action="/partner/net/CreateMerchantApplication" method="post" name="forms" >--%>
                            <form action="/partner/net/ListofAppMember" method="post" name="forms" >

                                <%
                                    Enumeration<String> stringEnumeration=request.getParameterNames();
                                    while(stringEnumeration.hasMoreElements())
                                    {
                                        String name=stringEnumeration.nextElement();
                                        if("action".equals(name))
                                        {
                                            if("apptoid".equals(name))
                                            {
                                                out.println("<input type='hidden' name='" + name + "' value='" + request.getParameterValues(name) + "'/>");
                                            }
                                        }
                                        else
                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                    }
                                %>

                                <button class="buttonform btn-xs" value="listofapplicationmember" name="submit" type="submit" name="B1" style="background: transparent;border: 0;">
                                    <img style="height: 35px;" src="/partner/images/goBack.png">
                                </button>

                            </form>
                        </div>
                    </div>
                    <br><br><br>

                    <div class="widget" style="margin-bottom: 0;">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;List Of Application Member</strong></h2>
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

                                <form action="/partner/listofapplicationmember.jsp" method="post" name="forms" class="form-horizontal">
                                    <input type="hidden" value="<%=user.getCSRFToken()%>" name="ctoken">


                                    <div class="form-group col-md-12">
                                        <label class="col-sm-4 control-label">Member ID</label>
                                        <div class="col-sm-4">
                                            <%--<input type="hidden" name="selectedID" value="<%=selectedMemberID%>" class="textb">--%>
                                            <input type="hidden" name="apptoid" value="<%=selectedMemberID%>" class="textb">
                                            <input class="form-control" title="" type="text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(selectedMemberID)%>" disabled style="border: 1px solid #b2b2b2;font-weight:bold;"  type="Text">
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
                                                <button class="buttonform btn-xs" value="Go Back" name="submit" type="submit" name="B1" style="background: transparent;border: 0;">
                                                    <img style="height: 35px;" src="/partner/images/goBack.png">
                                                </button>

                                            </form>
                                        </div>
                                    </div>--%>


                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

