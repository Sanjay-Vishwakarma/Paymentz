package com.payment;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
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
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 3/12/14
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaygatewayPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PaygatewayPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaygatewayPaymentProcess.class.getName());
/*
    public static void main(String[] args) throws SystemError
    {
        PaygatewayPaymentProcess p = new PaygatewayPaymentProcess();
        PZSettlementFile PZSettlementFile = new PZSettlementFile();
        PZSettlementFile.setFilepath("C:\\excelReader\\PayGateway130391091900820312_testSettlementFile_7.xls");
        p.readSettlementFile(PZSettlementFile);
        //String error = ecoreSettlementCron.processSettlement("PayGateway130391091900820312_testSettlementFile_7.xls","C:\\excelReader\\PayGateway130391091900820312_testSettlementFile_7.xls");
        //System.out.println(error);
    }*/

    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        log.debug("entering in Paygateway");
        transactionLogger.debug("entering in Paygateway");
        InputStream inputStream = null;
        List<PZSettlementRecord> vList = new ArrayList<PZSettlementRecord>();

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
                String strStatus = null;
                try
                {
                    strStatus = row.getCell((short)3).getStringCellValue();
                }catch (NumberFormatException nfe)
                {
                    strStatus = String.valueOf(row.getCell((short)3).getNumericCellValue());
                }

                strStatus = strStatus.trim();

                if(strStatus.equals("0.0"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                }
                else if(strStatus.equals("5.0") ||  strStatus.equals("T0005"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                }
                else if(strStatus.equals("R0000"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                }
                else if(strStatus.startsWith("R"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL));
                }

                //loadTransResponse.setSDateTime(((HSSFCell) row.getCell((short)2)).getStringCellValue()+"");
                loadTransResponse.setAmount((float)((HSSFCell) row.getCell((short)29)).getNumericCellValue()+"");
                loadTransResponse.setMerchantTXNid((int)((HSSFCell) row.getCell((short)2)).getNumericCellValue()+"");
                loadTransResponse.setPaymentid(((HSSFCell) row.getCell((short)1)).getStringCellValue()+"");

                if(loadTransResponse!= null)
                {
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
        }
        return vList;
    }

    @Override
    public String getSpecificCreditPageTemplate()
    {
        return "paygatewaycreditpage.template";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Hashtable specificValidationAPI(Hashtable<String, String> parameter)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.BIRTHDATE, parameter.get("birthdate"));

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
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.BIRTHDATE, request.getParameter("birthdate"));

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

        hashTable.put(InputFields.BIRTHDATE, parameter.get("birthdate"));

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
        return "paygatewayspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
