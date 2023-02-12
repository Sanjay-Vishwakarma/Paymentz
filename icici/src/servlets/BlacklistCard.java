import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.MerchantDAO;
import com.manager.dao.WhiteListDAO;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.MerchantDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 9/4/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlacklistCard extends HttpServlet
{
    private static Logger log = new Logger(BlacklistCard.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd = req.getRequestDispatcher("/uploadBlacklistCardDetails.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        String role=(String) session.getAttribute("role");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String username=(String)session.getAttribute("username");
        String actionExecutorName=username+"-"+role;
        MerchantDAO merchantDAO=new MerchantDAO();


        FileUploadBean fub = new FileUploadBean();
        fub.setSavePath(ApplicationProperties.getProperty("MPR_FILE_STORE"));

        try
        {
            fub.doUpload(req,null);
        }
        catch(SystemError sys){
            log.error("SystemError:::::",sys);
            req.setAttribute("sErrorMessage","Your file already exists in the System. Please Upload new File.");
            rd.forward(req, res);
        }

        String EOL="<br>";
        String filePath=ApplicationProperties.getProperty("MPR_FILE_STORE")+fub.getFilename();
        try
        {
            if (!ESAPI.validator().isValidInput("reason", fub.getFieldValue("reason"), "Description", 100, false))
            {
                sErrorMessage.append("Invalid Reason should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("remark", fub.getFieldValue("remark"), "Description", 100, false))
            {
                sErrorMessage.append("Invalid Remark  should not be empty" + EOL);
            }
            if(sErrorMessage.length()>0)
            {
                req.setAttribute("sErrorMessage",sErrorMessage);
                FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
                fileHandlingUtil.deleteFile(filePath);
                rd.forward(req, res);
            }
        }
        catch (Exception e){
            req.setAttribute("sErrorMessage", e.getMessage());
            rd.forward(req, res);
        }
        int faildcount      = 0;
        int successcount    = 0;
        FileHandlingUtil fileHandlingUtil   =new FileHandlingUtil();
        try
        {

            HashMap<String ,Object> stringObjectHashMap = readCardDetailsNewForGroup(filePath, fub.getFieldValue("reason"), fub.getFieldValue("remark"));
            List<String> queryBatch = (List<String>) stringObjectHashMap.get("queryBatch");
            faildcount              = (int) stringObjectHashMap.getOrDefault("faildcount", 0);

            if (queryBatch == null || queryBatch.size() == 0 )
            {
                req.setAttribute("sErrorMessage", "No Records found in file / File records can not be more than 1000 records ..!!  ");
                rd.forward(req, res);
            }

            if (queryBatch != null && queryBatch.size() >=1000)
            {
                req.setAttribute("sErrorMessage", "File records can not be more than 1000 records ..!!  ");
                rd.forward(req, res);

            }
            try
            {

                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(fub.getFieldValue("toid"));
                /*if (function.isValueNull(merchantDetailsVO.getMemberId()))
                {*/
                    log.error("Failed Count ----------> " + faildcount);
                    log.error("queryBatch.size()-------> " + queryBatch.size());
                    HashMap<String,Integer> stringHashMap =  uploadCards(queryBatch);

                    int successCount  =   stringHashMap.getOrDefault("succescount",0);
                    int faildCount    =  stringHashMap.getOrDefault("failcount",0);

                    sSuccessMessage.append("Total Updated Records --> "+successCount+"<BR>");
                    sSuccessMessage.append("Total Failed Records(EXISTING RECORDS IN DATABASE FOR "+fub.getFieldValue("reason")+" & "+fub.getFieldValue("remark")+") --> "+(faildcount+faildCount));

                    //sSuccessMessage = processSettlement(fub.getFieldValue("toid"), fub.getFieldValue("accountid"), sSuccessMessage, sErrorMessage, queryBatch);
                    req.setAttribute("sSuccessMessage", sSuccessMessage.toString());
                    rd.forward(req, res);
                /*}
                else
                {
                    StringBuilder response = new StringBuilder("Invalid MemberId");
                    req.setAttribute("sErrorMessage", response.toString());
                    rd.forward(req, res);
                }*/

            }
            catch (Exception e){
                req.setAttribute("sErrorMessage", e.getMessage());
                rd.forward(req, res);
            }
        }
        catch(SystemError sys){
            fileHandlingUtil.deleteFile(filePath);
            log.error("SystemError:::::", sys);
            req.setAttribute("sErrorMessage","Please provide valid file");
            rd.forward(req, res);
        }
    }

    public HashMap<String,Integer> uploadCards(List<String> queryBatch) throws PZDBViolationException
    {
        Connection connection                    = null;
        PreparedStatement preparedStatement      = null;
        int count = 0;
        int succescount = 0;
        int failcount = 0;
        HashMap<String,Integer> stringHashMap = new HashMap<>();

        try
        {
            connection  = Database.getConnection();
            for(String batch:queryBatch){
                if (batch != null && batch.length() > 0 && batch.charAt(batch.length() - 1) == ','){
                    batch = batch.substring(0, batch.length() - 1);
                }
                preparedStatement =connection.prepareStatement(batch.toString());
                log.error("uploadCards preparedStatement:::::"+ preparedStatement);
                count = preparedStatement.executeUpdate();
                log.error("count---> "+count);
                if(count > 0){
                    succescount = succescount + 1;
                    log.error("succescount ---->"+succescount);
                }else{
                    failcount = failcount + 1;
                    log.error("failcount ----> "+failcount);
                }
            }

            stringHashMap.put("succescount",succescount);
            stringHashMap.put("failcount",failcount);
        }
        catch (SystemError systemError){
            log.error("uploadCards SystemError:::::", systemError);
            PZExceptionHandler.raiseDBViolationException("WhiteListDAO.java", "uploadCards()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException sql){
            log.error(" uploadCards SQLException:::::", sql);

            PZExceptionHandler.raiseDBViolationException("WhiteListDao.java", "uploadCards()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.INCORRECT_QUERY, null, sql.getMessage(), sql.getCause());
        }
        finally{
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return stringHashMap ;
    }

    public HashMap<String ,Object> readCardDetailsNewForGroup(String fileName, String reason, String remark) throws SystemError
    {
        List<String> queryBatch = new ArrayList();
        StringBuilder sErrorMessage = new StringBuilder();
        String EOL = "<br>";
        StringBuilder sb = null;
        InputStream inputStream = null;
        Functions functions = new Functions();
        boolean isRecordAvailable = false;
        HashMap<String ,Object> stringObjectHashMap = new HashMap<String ,Object>();


        try
        {
            inputStream             = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs      = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb         = new HSSFWorkbook(fs);
            HSSFSheet sheet         = wb.getSheetAt(0);
            if (inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }




            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT IGNORE INTO blacklist_cards(first_six,last_four,reason,remark)VALUES");

            final int batchSize = 1000;
            int count           = 0;
            int faildcount      = 0;

            while (rows.hasNext())
            {

                HSSFRow row     = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells  = row.cellIterator();
                String firstSix = "";
                String lastFour = "";


                while (cells.hasNext())
                {

                    HSSFCell cell = (HSSFCell) cells.next();
                    if (cell.getCellNum() == 0)
                    {
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            firstSix = getFirstSix(cell.getStringCellValue());
                            lastFour = getLastFour(cell.getStringCellValue());
                            if (functions.isValueNull(firstSix) && (!ESAPI.validator().isValidInput(firstSix,firstSix , "FirstSixcc", 6, true))
                                    || functions.isValueNull(firstSix) && (!ESAPI.validator().isValidInput(lastFour,lastFour , "LastFourcc", 4, true)))
                            {
                                faildcount = faildcount +1;
                            }else{

                                count                   = count + 1;
                                sb                   = new StringBuilder();

                                sb.append("('" + firstSix + "','" + lastFour + "','" + reason + "','" + remark +"' ),");

                            }

                        }
                    }

                }

                if (sb != null && sb.length() > 0)
                {
                    if (count > batchSize)
                    {
                        count       = 0;
                        sb          = null;
                        queryBatch  = null;
                        break;
                    }
                    else{
                        queryBatch.add(stringBuilder.toString() + sb.toString());
                        sb = null;
                    }

                }

            }
            stringObjectHashMap.put("queryBatch",queryBatch);
            stringObjectHashMap.put("faildcount",faildcount);
            log.error("faildcount bbbbbb ---------> ="+faildcount );

        }
        catch (IOException e)
        {
            if (e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            if (num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (SystemError systemError)
        {
            throw new SystemError("Error: invalid file content");
        }

     /*   catch (EncryptionException e)
        {
            log.error("EncryptionException FileHandlingUtil ::::::", e);
        }*/
        catch (Exception e)
        {
            log.error("Exception FileHandlingUtil ::::::", e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return stringObjectHashMap;
    }
   public static String getFirstSix(String ccpan)
    {
        String firstSix =ccpan.substring(0, 6);
        return firstSix;
    }

    public static String getLastFour(String ccpan)
    {
        String lastFour = ccpan.substring((ccpan.length() - 4), ccpan.length());
        return lastFour;
    }

}
