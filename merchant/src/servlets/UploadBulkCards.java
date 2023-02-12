import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.BlacklistVO;
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
import org.owasp.esapi.errors.EncryptionException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Sanjay on 1/24/2022.
 */
public class UploadBulkCards extends HttpServlet
{
    private static Logger log = new Logger(UploadBulkCards.class.getName());


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
        String role=(String) session.getAttribute("role");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String username=(String)session.getAttribute("username");
        String actionExecutorName=username+"-"+role;
        BlacklistVO blacklistVO = new BlacklistVO();

        RequestDispatcher rd = req.getRequestDispatcher("/uploadBulkCards.jsp?ctoken=" + user.getCSRFToken());
        Merchants merchants = new Merchants();
        if (!merchants.isLoggedIn(session))
        {
            log.error("merchant logout===");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String action = "";
        StringBuilder sErrorMessage     = new StringBuilder();
        StringBuilder sSuccessMessage   = new StringBuilder();
        MerchantDAO merchantDAO         = new MerchantDAO();
        Functions function = new Functions();
        FileUploadBean fub = new FileUploadBean();
        WhiteListManager whiteListManager = new WhiteListManager();


        try
        {
            if(req.getParameter("performAction") != null){
                action = req.getParameter("performAction");
            }
            log.error("performAction------>  "+action);


            if (action.equalsIgnoreCase("download"))
            {
                log.error("inside if condtion of dowmload==");
                String exportPath   = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                String fileName     = "Card_Whitelisting_FORMAT.xls";
                FileOutputStream fileOut = new FileOutputStream(exportPath + "/" + fileName);
                downloadExcelFileFormat2(fileOut);
                sendFile(exportPath + "/" + fileName, fileName, res);
                return;
            }
        }
        catch (Exception e)
        {
            log.error("Exception main try block==="+e.getMessage());
            e.printStackTrace();
        }

            if ("getdata".equals(req.getParameter("action")) && function.isValueNull(req.getParameter("toid")))
            {
                try
                {
                    log.error("inside if try block+++");

                    String whitelistlevel = whiteListManager.getWhitelistingLevel(req.getParameter("toid"));
                    log.error("in try block whitelist ubc--" + whitelistlevel);

                    if (!function.isValueNull(whitelistlevel))
                    {
                        if (function.isValueNull(req.getParameter("toid")))
                        {
                            boolean isPresent = merchantDAO.isMemberExist(req.getParameter("toid"));
                            if (!isPresent)
                            {
                                log.debug("Merchant does not exist in the system");
                                sErrorMessage.append("Merchant does not exist in the system");
                            }
                        }
                    }
                    req.setAttribute("whitelistlevel", whitelistlevel);
                    req.setAttribute("sErrorMessage", sErrorMessage.toString());
                    rd.forward(req, res);
                    return;
                }
                catch (Exception e)
                {
                    log.error("Catch Exception...", e);
                }
            }
            else
            {
                log.error("inside else condition+++++++");

                fub.setSavePath(ApplicationProperties.getProperty("MPR_FILE_STORE"));

                try
                {
                    fub.doUpload(req, null);
                }
                catch (SystemError sys)
                {
                    log.error("SystemError:::::", sys);
                    req.setAttribute("sErrorMessage", "Please provide valid file or file name should not contains space.");
                    rd.forward(req, res);
                    return;
                }

                String EOL                  = "<br>";
                String filePath             = ApplicationProperties.getProperty("MPR_FILE_STORE") + fub.getFilename();
                String whiteListingLevel    = fub.getFieldValue("whitelistlevel");

                log.error("whitelist ===> " + whiteListingLevel);

                if (!ESAPI.validator().isValidInput("toid", fub.getFieldValue("toid"), "Numbers", 50, false))
                {
                    sErrorMessage.append("Invalid MemberId / MemberId should not be empty" + EOL);
                }

                if (function.isValueNull(whiteListingLevel))
                {
                    if (whiteListingLevel.equals("Account") && !ESAPI.validator().isValidInput("accountid", fub.getFieldValue("accountid"), "Numbers", 50, false))
                    {
                        sErrorMessage.append("Invalid AccountId / AccountId should not be empty" + EOL);
                    }
                }

                if (sErrorMessage.length() > 0)
                {
                    req.setAttribute("sErrorMessage", sErrorMessage);
                    rd.forward(req, res);
                    return;
                }
                int faildcount      = 0;
                int successcount    = 0;
                try
                {
                    HashMap<String ,Object> stringObjectHashMap = readCardDetailsNewForGroup(filePath, fub.getFieldValue("accountid"), fub.getFieldValue("toid"), actionExecutorId, actionExecutorName);
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
                        if (function.isValueNull(merchantDetailsVO.getMemberId()))
                        {
                            log.error("Failed Count ----------> " + faildcount);
                            log.error("queryBatch.size()-------> " + queryBatch.size());
                            HashMap<String,Integer> stringHashMap =  uploadCards(queryBatch);

                           int successCount  =   stringHashMap.getOrDefault("succescount",0);
                           int faildCount    =  stringHashMap.getOrDefault("failcount",0);
                            String accountId ="";
                            if(fub != null ){
                                accountId = fub.getFieldValue("accountid");
                            }

                            sSuccessMessage.append("Total Updated Records --> "+successCount+"<BR>");
                            sSuccessMessage.append("Total Failed Records(EXISTING RECORDS IN DATABASE FOR "+fub.getFieldValue("toid"));
                            if(function.isValueNull(accountId)){
                                sSuccessMessage.append(" & "+accountId);
                            }
                            sSuccessMessage.append(") --> "+(faildcount+faildCount));

                            //sSuccessMessage = processSettlement(fub.getFieldValue("toid"), fub.getFieldValue("accountid"), sSuccessMessage, sErrorMessage, queryBatch);
                            req.setAttribute("sSuccessMessage", sSuccessMessage.toString());
                            rd.forward(req, res);
                        }
                        else
                        {
                            StringBuilder response = new StringBuilder("Invalid MemberId");
                            req.setAttribute("sErrorMessage", response.toString());
                            rd.forward(req, res);
                        }
                    }
                    catch (Exception e)
                    {
                        req.setAttribute("sErrorMessage", e.getMessage());
                        rd.forward(req, res);
                    }
                }
                catch (SystemError sys)
                {
                    log.error("SystemError:::::", sys);
                   if(function.isValueNull(sys.getMessage()))
                   {
                       blacklistVO.setError(sys.getMessage()+"<BR/>");
                   }

                    req.setAttribute("sErrorMessage", "Please provide valid file or Valid file content");
                    rd.forward(req, res);
                }

            }

    }


    public HashMap<String ,Object> readCardDetailsNewForGroup(String fileName, String accountId, String memberId, String actionExecutorId, String actionExecutorName ) throws SystemError
    {
        List<String> queryBatch = new ArrayList();
        StringBuilder sErrorMessage = new StringBuilder();
        String EOL = "<br>";
        StringBuilder sb = null;
        InputStream inputStream = null;
        Functions functions = new Functions();
        boolean isRecordAvailable = false;
        HashMap<String ,Object> stringObjectHashMap = new HashMap<String ,Object>();
        if (functions.isValueNull(accountId))
        {
            accountId = accountId;
        }
        else
        {
            accountId = "0";
        }

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
            stringBuilder.append("INSERT IGNORE INTO whitelist_details(firstsix,lastfour,accountid,memberid,isApproved,emailAddr,isTemp,name,ipAddress,expiryDate,actionExecutorId,actionExecutorName)VALUES");

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
                String email    = "";
                String cardHolderName    = "";
                String ipAddress        = "";
                String expiryDate       = "";

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
                                String encryptExpiry    = "";
                                sb                   = new StringBuilder();
                                if (functions.isValueNull(expiryDate))
                                {
                                    encryptExpiry = PzEncryptor.hashExpiryDate(expiryDate, memberId);
                                }
                                sb.append("('" + firstSix + "','" + lastFour + "'," + accountId + "," + memberId + ",'Y','" + email + "','N','" + cardHolderName + "','" + ipAddress + "','" + encryptExpiry + "','"+ actionExecutorId +"','"+ actionExecutorName +"' ),");

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
            log.error("faildcount ---------> ="+faildcount );
//            log.error("queryBatch ---------> ="+queryBatch.size());

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

        catch (EncryptionException e)
        {
            log.error("EncryptionException FileHandlingUtil ::::::", e);
        }
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

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response) throws Exception
    {

        File f          = new File(filepath);
        int length      = 0;

        // Set browser download related headers
        response.setContentType("application/ms-excel");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf         = new byte[1024];
        DataInputStream in  = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }
        log.info("####### File Create Successfully #######");
        in.close();
        op.flush();
        op.close();

        // file must be deleted after transfer...
        // caution: select to download only files which are temporarily created zip files
        // do not call this servlets with any other files which may be required later on.
        File file = new File(filepath);
        file.delete();
        log.info("####### File Deleted Successfully #######");
        return true;

    }

    void downloadExcelFileFormat2(FileOutputStream fileOut){
        HSSFWorkbook workbook       = new HSSFWorkbook();
        HSSFSheet sheet             = workbook.createSheet("bulkUploadExcel");
        HSSFRow rowhead             = sheet.createRow(0);
        try
        {

            rowhead.createCell((short)0).setCellValue("Card Number (Add Cards in format 123456******1234)");

            workbook.write(fileOut);
            fileOut.close();

        }
        catch (Exception e)
        {
            log.debug("UploadBulkCards Exception ---" + e);
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



}
