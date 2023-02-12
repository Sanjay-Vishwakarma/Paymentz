<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.dao.GatewayAccountDAO" %>
<%@ page import="com.manager.dao.GatewayTypeDAO" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Sagar
  Date: 5/8/14
  Time: 3:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%!
  private static Logger logger = new Logger("merchantTheme.jsp");
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  String partnerid=((String)session.getAttribute("merchantid"));
  PartnerDAO partnerDAO = new PartnerDAO();


  LinkedHashMap<String,Integer> thememap = partnerDAO.listCurrenttheme();


  String tname= request.getParameter("currenttemplatetheme");

  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String merchantTheme_Theme_Configuration = StringUtils.isNotEmpty(rb1.getString("merchantTheme_Theme_Configuration")) ? rb1.getString("merchantTheme_Theme_Configuration") : "Theme Configuration";
  String merchantTheme_PartnerID = StringUtils.isNotEmpty(rb1.getString("merchantTheme_PartnerID")) ? rb1.getString("merchantTheme_PartnerID") : "Partner ID:";
  String merchantTheme_Default_Theme = StringUtils.isNotEmpty(rb1.getString("merchantTheme_Default_Theme")) ? rb1.getString("merchantTheme_Default_Theme") : "Default Theme:";
  String merchantTheme_Default_Selected = StringUtils.isNotEmpty(rb1.getString("merchantTheme_Default_Selected")) ? rb1.getString("merchantTheme_Default_Selected") : "Default No Theme Selected";
  String merchantTheme_Current_Theme = StringUtils.isNotEmpty(rb1.getString("merchantTheme_Current_Theme")) ? rb1.getString("merchantTheme_Current_Theme") : "Current Theme:";
  String merchantTheme_select_Theme = StringUtils.isNotEmpty(rb1.getString("merchantTheme_select_Theme")) ? rb1.getString("merchantTheme_select_Theme") : "--Select Theme--";
  String merchantTheme_Save = StringUtils.isNotEmpty(rb1.getString("merchantTheme_Save")) ? rb1.getString("merchantTheme_Save") : "Save";
  String merchantTheme_Sorry = StringUtils.isNotEmpty(rb1.getString("merchantTheme_Sorry")) ? rb1.getString("merchantTheme_Sorry") : "Sorry";
  String merchantTheme_no = StringUtils.isNotEmpty(rb1.getString("merchantTheme_no")) ? rb1.getString("merchantTheme_no") : "No Records Found";

  //System.out.println("tname----"+tname);
%>
<html>
<head>
  <title><%=company%> Partner> Theme Configuration</title>
  <link href="/agent/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">

  <style type="text/css">
    @media (min-width: 641px) {
      #mytabletr td:first-child {
        width: 50%;
      }
    }

    table.table{
      margin-top: 0 !important;
    }

  </style>


</head>
<body class="bodybackground">

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <%
        MerchantDAO merchant = new MerchantDAO();
        GatewayTypeDAO gatewayTypeDAO = new GatewayTypeDAO();
        GatewayAccountDAO gatewayAccountDAO =new GatewayAccountDAO();
        int partnerProcessingBanks =gatewayTypeDAO.getPartnerProcessingBanks(partnerid);
        int partnerBankAccounts =gatewayAccountDAO.getPartnerBankAccounts(partnerid);
        PartnerDetailsVO partnerDetailsVOs=(PartnerDetailsVO)request.getAttribute("partnerDetailsVOs");
        //System.out.println("getDefaultTheme in jsp...."+partnerDetailsVOs.getDefaultTheme());
        //System.out.println("getCurrentTheme in jsp....."+partnerDetailsVOs.getCurrentTheme());
        if(partnerDetailsVOs!=null)
        {
          //System.out.println("getDefaultTheme in jsp after...."+partnerDetailsVOs.getDefaultTheme());
          //System.out.println("getCurrentTheme in jsp after....."+partnerDetailsVOs.getCurrentTheme());

      %>

      <form action="/partner/net/SetThemeColor?ctoken=<%=ctoken%>" method=post name="myformnameSave" id="myformnameSave">

      <div class="row reporttable">

        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i></strong>&nbsp;&nbsp;<strong><%=company%> <%=merchantTheme_Theme_Configuration%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <div id="horizontal-form">                        <%-- End Radio Button--%>
                <div class="form-group col-md-12 has-feedback">
                  <div class="table-responsive">

                    <%
                      String msg = (String) request.getAttribute("msg");
                      Functions functions = new Functions();
                      if(functions.isValueNull(msg))
                      {
                        //out.println("<center><font class=\"textb\"><b>"+msg+"</b></font></center><br>");
                        //out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                        out.println(Functions.NewShowConfirmation1("Sorry", msg));
                      }
                    %>

                    <%-- <table align="center" width="90%" border="1">--%>
                    <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                      <tr id="mytabletr">
                        <td align="left" style="background-color: #7eccad !important;color: white;"><%=merchantTheme_PartnerID%></td>
                        <td align="center">
                          <%=partnerDetailsVOs.getPartnerId()%>
                        </td>
                      </tr>

                      <tr id="mytabletr">
                        <td align="left" style="background-color: #7eccad !important;color: white;"><%=merchantTheme_Default_Theme%></td>
                        <td align="center">
                          <%
                            String str=partnerDetailsVOs.getDefaultTheme();
                           // Functions functions=new Functions();
                            if(functions.isEmptyOrNull(str))
                            {
                            %>
                              <label class="txtbox"><%=merchantTheme_Default_Selected%></label>
                            <%
                              }
                            else
                            {
                              %>
                           <%=partnerDetailsVOs.getDefaultTheme()%>
                            <%}
                          %>

                        </td>
                      </tr>

                      <tr id="mytabletr">
                        <td align="left" style="background-color: #7eccad !important;color: white;"><%=merchantTheme_Current_Theme%></td>
                        <td align="center">
                       <%--   <select name="currenttemplatetheme" class="txtbox" value="">
                            <option value="">No Theme</option>
                            <option value="orange">Orange Theme</option>
                            <option value="GradientBlue">Gradient Blue Theme</option>
                            <option value="GradientGreen">Gradient Green Theme</option>

                          </select>
--%>
                          <select name="currenttemplatetheme"   class="txtbox" >
                            <option value="" selected><%=merchantTheme_select_Theme%></option>
                            <%
                              String selected = "";
                              String key= "";
                              for (String themename : thememap.keySet())
                              {
                                key = String.valueOf(themename);
                                if (key.equals(partnerDetailsVOs.getCurrentTheme()))
                                selected ="selected";
                                else
                                selected="";
                            %>
                            <option value="<%=key%>" <%=selected%>> <%=key%></option>
                            <%
                              }
                            %>
                          </select>
                        <%--  <input value='<%=partnerDetailsVOs.getCurrentTheme()%>' id="getcountval" type="hidden">
                          <script>

                              var countryval = document.getElementById('getcountval').value;
                              $('[name=currenttemplatetheme] option').filter(function ()
                              {
                                if(countryval =="")
                                {
                                  return($(this).text()=="")
                                }
                                if (countryval == '')
                                {
                                  return ($(this).text() == '');
                                }
                                else if (countryval == 'orange')
                                {
                                  return ($(this).text() == 'orange');
                                }
                                else if (countryval == 'GradientBlue')
                                {
                                  return ($(this).text() == 'GradientBlue');
                                }
                                else if(countryval == 'GradientGreen')
                                {
                                  return ($(this).text() == 'GradientGreen');
                                }
                                else
                                {
                                  return ($(this).text() == 'No Theme');
                                }

                              })
                            .prop('selected', true);
                              </script>--%>
                        </td>
                      </tr>

                    </table>

                  </div>
                </div>
                <div class="form-group col-md-12 has-feedback">
                  <center>
                    <label >&nbsp;</label>
                    <input type="hidden" value="1" name="step">
                    <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=merchantTheme_Save%></button>
                  </center>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      </form>
    </div>


  </div>

  <%
    }
    else
    {
      out.println(Functions.NewShowConfirmation1(merchantTheme_Sorry,merchantTheme_no ));
    }
  %>


</div>

</body>
</html>