<%@ page import="com.directi.pg.CommonSettlement" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 12/13/13
  Time: 3:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.fileupload.FileUploadBean" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ include file="index.jsp" %>
<html>
<head>
    <title></title>
</head>
<br><br><br><br><br><br><br><br>
<div class="qwipitab" style="margin-top: 0px">
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

            out.println(Functions.NewShowConfirmation("ERROR",sys.getMessage()));
            return;
        }

        String name=fub.getFilename();
        String val="";

        if(fub.getFieldValue("accountid")!=null && !fub.getFieldValue("accountid").equals(""))
        {

            String accountid=fub.getFieldValue("accountid");
            if(ApplicationProperties.getProperty("MPR_FILE_STORE")+name!=null)
            {
                try
                {
                    String filepath=ApplicationProperties.getProperty("MPR_FILE_STORE")+name;
                    CommonSettlement commonSettlementn=new CommonSettlement();
                    val= commonSettlementn.processSettlement(name,filepath,accountid);
                    if(val!=null)
                    {
                      out.println(Functions.NewShowConfirmation("Result",val));
                    }
                }
                catch (Exception e)
                {
                    out.println(Functions.NewShowConfirmation("Error",e.getMessage()));
                }
            }
        }
        else
        {
            out.println(Functions.NewShowConfirmation("ERROR", "Kindly select account Id."));
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }

    %>
    </div>
<body>

</body>
</html>
<%!
    private boolean isFileNameValid(String fileName)
    {
        boolean isValidFilename = false;

        if(fileName.length()<42)
        {
            return isValidFilename;
        }
        if(!fileName.endsWith(".xls"))
        {
            return isValidFilename;
        }
        isValidFilename = true;
        return isValidFilename;
    }
%>