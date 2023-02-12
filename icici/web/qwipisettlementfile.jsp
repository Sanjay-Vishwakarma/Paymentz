<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.QWIPISettlementCron" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.fileupload.FileUploadBean" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ include file="qwipitab.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/10/13
  Time: 9:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title> QWIPI Settlement Uploading</title>
</head>
<div class="reporttable">
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String pathtoraw = null;
        String pathtolog =null;
        pathtoraw = ApplicationProperties.getProperty("MPR_FILE_STORE");
        pathtolog = ApplicationProperties.getProperty("LOG_STORE");
        FileUploadBean fub = new FileUploadBean();
        fub.setSavePath(pathtoraw);
        fub.setLogpath(pathtolog);
        try
        {
            fub.doUpload(request,null);

        }
        catch(SystemError sys)
        {
            //sys.printStackTrace();
            out.println(Functions.NewShowConfirmation("ERROR", sys.getMessage()));
            return;
        }

        String File=fub.getFilename();
        String str="";
        String merchantId=fub.getFieldValue("merchantId");
        String startdate=fub.getFieldValue("startdate");
        String enddate=fub.getFieldValue("enddate");
        String filepath=ApplicationProperties.getProperty("MPR_FILE_STORE")+File;
        /*if(merchantId.equals(""))
        {
            Functions.ShowMessage("ERROR", "Enter valid MerchantId");
            return;
        }
        else
        {
            merchantId=request.getParameter("merchantId");
            System.out.println("mervhantid=================="+merchantId);
            Connection con= Database.getConnection();
            String query="select * from gateway_accounts where merchantid=? ";
            PreparedStatement pst= con.prepareStatement(query);
            pst.setString(1,merchantId);
            ResultSet rs=pst.executeQuery();
            if(!rs.next())
            {
                out.println(Functions.ShowMessage("ERROR", "Merchant Id is not exist in system"));
                return;
            }
        }*/
        /*if(startdate.equals(""))
        {

            out.println(Functions.ShowMessage("ERROR", "Enter valid Start Date"));
            return;
        }
        else
        {
            startdate= request.getParameter("startdate");
        }
        if(enddate.equals(""))
        {
            out.println(Functions.ShowMessage("ERROR", "Enter valid End Date"));
            return;
        }
        else
        {
            enddate= request.getParameter("enddate");
        }*/
        if(File.equals(""))
        {

            out.println(Functions.NewShowConfirmation("ERROR","Enter valid Filename"));
            //return;
        }
        else
        {
            QWIPISettlementCron process=new QWIPISettlementCron();
            try
            {
                str= process.processSettlement(File,filepath);
                out.println(Functions.NewShowConfirmation("",str));
                return;
            }
            catch (Exception e)
            {
                out.println(Functions.NewShowConfirmation("",e.getMessage()));
                return;
            }

            /*if(str.contains("Settlement File uploaded successfully"))
            {
                System.out.println("------4-------");
                out.println(Functions.ShowMessage("Successful",str));
            }
            else
            {
                System.out.println("------5-------");
                out.println(Functions.ShowMessage("ERROR","Exception occur while process Settlement File"));
            }*/

        }

    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</div>
</body>
</html>