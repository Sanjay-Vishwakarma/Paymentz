package com.payment.dvg_payment;

import com.directi.pg.*;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZSettlementFile;
import com.payment.response.PZSettlementRecord;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/11/14
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DVGPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(DVGPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DVGPaymentProcess.class.getName());

    public Hashtable specificValidationAPI(Hashtable<String, String> parameter)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, parameter.get("cardholderipaddress"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    @Override
    public String specificValidationAPI(HttpServletRequest request)
    {
        log.debug("inside specific validation");
        transactionLogger.debug("inside specific validation");
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, request.getParameter("cardholderipaddress"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    public Hashtable specificValidation(Map<String, String> parameter)
    {

        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, parameter.get("cardholderipaddress"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    @Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "dvgspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        Connection con = null;
        InputStream inputStream = null;
        List<PZSettlementRecord> vList = new ArrayList<PZSettlementRecord>();

        log.debug("File Path--->>"+fileName.getFilepath());

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName.getFilepath()));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            if (inputStream == null)
            {
                throw new SystemError("Settlement File is not found");
            }

            // get rows of sheet
            Iterator rows = sheet.rowIterator();

            int cntRowToLeaveFromTop=0;
            int cntTotalRecords=0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                // Skip the header rows.
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Settlement Cron Error : Invalid File Format");
            }

            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;
                Iterator cells = row.cellIterator();
                PZSettlementRecord loadTransResponse=new PZSettlementRecord();


                if(((String)((HSSFCell) row.getCell((short)1)).getStringCellValue()).trim().equals("SALE"))
                {
                    if(((String)((HSSFCell) row.getCell((short)3)).getStringCellValue()).trim().equals("APPROVED"))
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                    }
                }
                else if(((String)((HSSFCell) row.getCell((short)1)).getStringCellValue()).equals("REFUND"))
                {
                    if(((String)((HSSFCell) row.getCell((short)3)).getStringCellValue()).trim().equals("APPROVED"))
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL));
                    }
                }
                StringBuffer sAmount = new StringBuffer((float)((HSSFCell) row.getCell((short)5)).getNumericCellValue()+"");
                loadTransResponse.setAmount(sAmount.toString());

                StringBuffer strMerchantTXNid = new StringBuffer();
                if(row.getCell((short)16).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                {
                    strMerchantTXNid.append(String.valueOf((long) ((HSSFCell) row.getCell((short) 16)).getNumericCellValue()));
                }
                else {
                    strMerchantTXNid.append(row.getCell((short)16).getStringCellValue());
                }
                loadTransResponse.setMerchantTXNid(strMerchantTXNid.toString());

                /*con = Database.getConnection();
                StringBuffer qry = new StringBuffer();
                qry.append("SELECT paymentid,trackingid FROM transaction_common WHERE trackingid=?");
                PreparedStatement pstmt= con.prepareStatement(qry.toString());
                pstmt.setString(1,strMerchantTXNid.toString());
                ResultSet rs = pstmt.executeQuery();
                if(rs.next())
                {
                    loadTransResponse.setPaymentid(rs.getString("paymentid"));
                }*/

                if(loadTransResponse!= null)
                {
                    log.debug("bankid======="+loadTransResponse.getPaymentid()+" trackigid==="+loadTransResponse.getMerchantTXNid());
                    vList.add(loadTransResponse);
                }
            }
        }
        catch (IOException e)
        {
            throw new SystemError("ERROR: Settlement File cant be read");
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
                    throw new SystemError("ERROR: Settlement File cant be closed successfully");
                }
            }
            if(con != null)
            {
                try { con.close(); }
                catch (SQLException e)
                {
                    throw new SystemError("ERROR: Connection not closed successfully");
                }

            }
        }
        return vList;
    }

   /* public static void main(String ar [] )
    {
        String val = null;
        String acc = "416";
        PZSettlementFile file = new PZSettlementFile();
        //file.setFilepath("/home//Desktop/MPRFiles/Divitia_Settlement Cron_dbBAckup_6thAugust2015.xls");
        file.setFilepath("/home//Desktop/MPRFiles/DVG_localfile_7Aug15.xls");
        //file.setFileName("Divitia_Settlement Cron_dbBAckup_6thAugust2015.xls");
        file.setFileName("DVG_localfile_7Aug15.xls");
        System.out.println(file.getFilepath());

        CommonSettlement commonSettlement = new CommonSettlement();
        val = commonSettlement.processSettlement(file.getFileName(), file.getFilepath(), acc);
        System.out.println("VAL-------------->"+val);

    }*/
}
