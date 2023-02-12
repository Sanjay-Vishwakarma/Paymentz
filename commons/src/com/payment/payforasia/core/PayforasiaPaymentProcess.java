package com.payment.payforasia.core;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZFileVO;
import com.payment.request.PZSettlementFile;
import com.payment.request.PZTC40Record;
import com.payment.response.PZChargebackRecord;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 7/19/14
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayforasiaPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PayforasiaPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayforasiaPaymentProcess.class.getName());

    /*public static void main(String[] args) throws SystemError
    {
        String filepath = "C:\\Users\\ThinkPadT410\\Desktop\\Chargeback_Demo\\CB_Payforasia_Jinesh.xls";


        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFileName(filepath);

        PayforasiaPaymentProcess payforasiaPaymentProcess = new PayforasiaPaymentProcess();
        System.out.println("orderno-----"+pzFileVO.getFileName());
        List<PZTC40Record> pztc40Record = payforasiaPaymentProcess.readTC40file(pzFileVO);
        for(PZTC40Record pztc40Record1 : pztc40Record)
        {
            System.out.println("orderno-----"+pztc40Record1.getOrderno());
        }

        System.out.println("size-----"+pztc40Record.size());

    }
*/
    public static void main(String[] args) throws SystemError
    {
        System.out.println("Inside in---");

        //String filepath = "C:\\Users\\ThinkPadT410\\Downloads\\CB_Payforasia-20.xls";
        //String filepath = "D:\\Chargebacktransactioninformation_1Septo30Nov16_PFA_01.xls";
        String filepath="C:\\Users\\admin\\Downloads\\Payforasia_19 to 25 oct 16_2404_Main_1 3 20_59000_delete18.xls";

        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFilepath(filepath);

        PayforasiaPaymentProcess payforasiaPaymentProcess = new PayforasiaPaymentProcess();

        List<PZChargebackRecord> pzChargebackRecords = payforasiaPaymentProcess.readChargebackFile(pzFileVO);

        System.out.println("chargebackFile----" + pzChargebackRecords.size());
        int sr=1;

        for (PZChargebackRecord chargebackRecord : pzChargebackRecords)
        {

            System.out.println("sr.no:" + sr + "paymentid--" + chargebackRecord.getPaymentid() + "-" + chargebackRecord.getTrackingid() + "-" + chargebackRecord.getAmount() + "-" + chargebackRecord.getChargebackAmount() + "-" + chargebackRecord.getRrn() + "-" + chargebackRecord.getChargebackReason());
            //System.out.println("paymentid--"+chargebackRecord.getPaymentid());
            sr=sr+1;
        }

    }

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
        return "payforasiaspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<PZSettlementRecord> readSettlementFileOld(PZSettlementFile fileName) throws SystemError
    {
        log.debug("Entering in PayForAsia readSettlementFile");
        transactionLogger.debug("Entering in PayForAsia readSettlementFile");
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
                if (cntRowToLeaveFromTop==2)
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
                String strRefundStatus = null;
                String strChargebackStatus = null;

                try
                {
/*                    if(row==null)
                    {
                        continue;
                    }
                    if(row.getCell((short)8) == null || row.getCell((short)8).equals(""))
                    {
                        continue;
                    }
                    if(((String)((HSSFCell) row.getCell((short)8)).getStringCellValue())==null || ((String)((HSSFCell) row.getCell((short)8)).getStringCellValue()).equals(""))
                    {
                        continue;
                    }*/
                    strStatus = row.getCell((short)8).getStringCellValue();
                    strRefundStatus = row.getCell((short)9).getStringCellValue();
                    strChargebackStatus = row.getCell((short)11).getStringCellValue();

                }catch (NumberFormatException nfe)
                {
                    throw new SystemError("Settlement Cron Error : while reading the status value");
                }

                strStatus = strStatus.trim();
                strRefundStatus = strRefundStatus.trim();
                strChargebackStatus = strChargebackStatus.trim();

                if(strStatus.equalsIgnoreCase("Success") && strRefundStatus.equalsIgnoreCase("No Refund") && strChargebackStatus.equalsIgnoreCase("No Protest"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                    log.debug("in success block ");
                    transactionLogger.debug("in success block ");
                }
                else if(!strStatus.equalsIgnoreCase("Success"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                    log.debug("in capture failed block");
                    transactionLogger.debug("in capture failed block");
                }
                else if(strStatus.equalsIgnoreCase("Success") && strRefundStatus.equalsIgnoreCase("Refunded") && strChargebackStatus.equalsIgnoreCase("No Protest"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                    log.debug("in reversed block");
                    transactionLogger.debug("in reversed block");
                }

                //loadTransResponse.setSDateTime(((HSSFCell) row.getCell((short)2)).getStringCellValue()+"");
                loadTransResponse.setAmount((float) row.getCell((short) 7).getNumericCellValue() + "");
                loadTransResponse.setMerchantTXNid(row.getCell((short) 4).getStringCellValue() + "");
                loadTransResponse.setPaymentid(row.getCell((short) 3).getStringCellValue() + "");
                log.debug("trackingid=" + loadTransResponse.getMerchantTXNid());
                transactionLogger.debug("trackingid=" + loadTransResponse.getMerchantTXNid());
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

    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        InputStream inputStream = null;
        PZSettlementRecord loadTransResponse = null;
        List<PZSettlementRecord> vList = new ArrayList();
        Functions functions=new Functions();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            int cntRowToLeaveFromTop = 0;
            int cntTotalRecords = 0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                // Skip the header rows.
                if (cntRowToLeaveFromTop == 2)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Settlement Cron Error : Invalid File Format");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;
                Iterator cells = row.cellIterator();
                loadTransResponse = new PZSettlementRecord();

                String paymentId ="";
                String strMerchantOrderId ="";

                String strTxnAmount = "";
                String strSuccessfulTxnAmount ="";
                String strRefundedTxnAmount = "";
                String strChargebackTxnAmount = "";

                String strStatus ="";
                String strRefundStatus ="";
                String strChargebackStatus ="";


                try
                {
                    if(!isCellEmpty(row.getCell((short) 3))){
                        try
                        {
                            paymentId=row.getCell((short) 3).getStringCellValue() + "";
                        }
                        catch (NumberFormatException nfe)
                        {
                            paymentId=row.getCell((short) 3).getNumericCellValue() + "";
                        }
                    }
                    if(!isCellEmpty(row.getCell((short) 4))){
                        try
                        {
                            strMerchantOrderId=row.getCell((short) 4).getStringCellValue() + "";
                        }
                        catch (NumberFormatException nfe)
                        {
                            strMerchantOrderId=row.getCell((short) 4).getNumericCellValue() + "";
                        }
                    }

                    //Amount
                    if(!isCellEmpty(row.getCell((short) 9))){
                        strTxnAmount = row.getCell((short) 9).getNumericCellValue() + "";
                    }

                    //Successful Amount
                    if (!isCellEmpty(row.getCell((short) 9)))
                    {
                        strSuccessfulTxnAmount=row.getCell((short) 9).getNumericCellValue() + "";
                    }

                    //Refunded Amount
                    if (!isCellEmpty(row.getCell((short) 15)))
                    {
                        strRefundedTxnAmount = row.getCell((short) 15).getNumericCellValue() + "";
                    }

                    //Chargeback Amount
                    if (!isCellEmpty(row.getCell((short) 17)))
                    {
                        strChargebackTxnAmount = row.getCell((short) 17).getNumericCellValue() + "";
                    }

                    if(!isCellEmpty(row.getCell((short) 13))){
                        strStatus = row.getCell((short) 13).getStringCellValue();
                    }
                    if(!isCellEmpty(row.getCell((short) 14))){
                        strRefundStatus = row.getCell((short) 14).getStringCellValue();
                    }
                    if(!isCellEmpty(row.getCell((short) 16))){
                        strChargebackStatus = row.getCell((short) 16).getStringCellValue();
                    }
                }
                catch(NumberFormatException nfe){
                    throw new SystemError("Settlement Cron Error : while reading the status value");
                }

                paymentId =paymentId.trim();
                strMerchantOrderId =strMerchantOrderId.trim();
                strTxnAmount = strTxnAmount.trim();

                strSuccessfulTxnAmount =strSuccessfulTxnAmount.trim();
                strRefundedTxnAmount = strRefundedTxnAmount.trim();
                strChargebackTxnAmount = strChargebackTxnAmount.trim();

                strStatus = strStatus.trim();
                strRefundStatus = strRefundStatus.trim();
                strChargebackStatus = strChargebackStatus.trim();

                if(!functions.isValueNull(paymentId) || !functions.isValueNull(strMerchantOrderId) || !functions.isValueNull(strSuccessfulTxnAmount) || !functions.isValueNull(strStatus) || !functions.isValueNull(strRefundStatus) || !functions.isValueNull(strChargebackStatus)){
                    transactionLogger.error("Settlement Cron Error :Improper values at Record No. "+cntTotalRecords+", Please edit the file and upload again");
                    throw new SystemError("Settlement Cron Error :Improper values at Records No. "+cntTotalRecords+",Please edit the file and upload again");
                }

                if (strStatus.equalsIgnoreCase("Successful") && strRefundStatus.equalsIgnoreCase("Non Refunded") && strChargebackStatus.equalsIgnoreCase("Non Chargeback"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.SETTLED.toString());

                }
                else if (strStatus.equalsIgnoreCase("Failed"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.AUTH_FAILED.toString());

                }
                else if (strStatus.equalsIgnoreCase("Successful") && strRefundStatus.equalsIgnoreCase("Refunded") && strChargebackStatus.equalsIgnoreCase("Non Chargeback"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.REVERSED.toString());

                }
                else if (strStatus.equalsIgnoreCase("Successful") && strRefundStatus.equalsIgnoreCase("Non Refunded") && strChargebackStatus.equalsIgnoreCase("Chargeback"))
                {
                    loadTransResponse.setStatusDetail(PZTransactionStatus.CHARGEBACK.toString());
                }
                try
                {
                    loadTransResponse.setMerchantTXNid(row.getCell((short) 4).getStringCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransResponse.setMerchantTXNid(row.getCell((short) 4).getNumericCellValue() + "");
                }
                try
                {
                    loadTransResponse.setPaymentid(row.getCell((short) 3).getStringCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransResponse.setPaymentid(row.getCell((short) 3).getNumericCellValue() + "");
                }

                loadTransResponse.setAmount((float) row.getCell((short) 9).getNumericCellValue() + "");
                loadTransResponse.setTransactionTime(targetFormat.format((row.getCell((short) 5).getDateCellValue())));
                loadTransResponse.setAmount(strTxnAmount);
                loadTransResponse.setSuccessfulAmount(strSuccessfulTxnAmount);
                loadTransResponse.setRefundAmount(strRefundedTxnAmount);
                loadTransResponse.setChargebackAmount(strChargebackTxnAmount);

                if (loadTransResponse != null)
                {
                    vList.add(loadTransResponse);
                }
            }
        }
        catch (IOException e)
        {
            throw new SystemError("ERROR: Settlement File cant be read");
        }
        /*catch (ParseException e)
        {
            throw new SystemError("ERROR: Settlement File cant be read");
        }*/
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

    public List<PZChargebackRecord> readChargebackFile(PZFileVO fileName) throws SystemError
    {
        List<PZChargebackRecord> chargebackRecordList = new ArrayList();
        try
        {
            //Create a new Workbook Instance
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);
            //Iterate through each rows one by one
            Iterator rows = sheet.rowIterator();

            int cntRowToLeaveFromTop = 0;
            int cntTotalRecords = 0;
            boolean isFileProcceding = false;
            //Skip header rows
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if(cntRowToLeaveFromTop == 2)
                {
                    isFileProcceding = true;
                    break;
                }
            }

            if(!isFileProcceding)
            {
                throw new SystemError("Invalid File Format");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;

                //For each row, iterate through all the columns
                PZChargebackRecord loadTransResponse = new PZChargebackRecord();
                try
                {
                    loadTransResponse.setPaymentid(row.getCell((short) 1).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransResponse.setPaymentid(row.getCell((short) 1).getStringCellValue() + "");
                }

                try
                {
                    loadTransResponse.setTrackingid(row.getCell((short) 2).getNumericCellValue() + "");
                }
                catch (NumberFormatException e)
                {
                    loadTransResponse.setTrackingid(row.getCell((short) 2).getStringCellValue() + "");
                }

                try
                {
                    loadTransResponse.setAmount((row.getCell((short) 4).getNumericCellValue() + "").trim());
                }
                catch (NumberFormatException e)
                {
                    loadTransResponse.setAmount((row.getCell((short) 4).getStringCellValue() + "").trim());
                }
                try
                {
                    loadTransResponse.setChargebackAmount(row.getCell((short) 9).getNumericCellValue() + "");
                }
                catch (NumberFormatException e)
                {
                    loadTransResponse.setChargebackAmount(row.getCell((short) 9).getStringCellValue() + "");
                }
                try
                {
                    //String no= String.valueOf(row.getCell((short) 12).getNumericCellValue());
                    if ((row.getCell((short) 12)!=null))
                    {
                        loadTransResponse.setRrn(row.getCell((short) 12).getNumericCellValue() + "");
                    }
                    else {
                        loadTransResponse.setRrn("empty");
                    }

                }
                catch (NumberFormatException e)
                {
                    loadTransResponse.setRrn(row.getCell((short) 12).getStringCellValue() + "");
                }
                try
                {
                    if ((row.getCell((short) 13)!=null))
                    {
                        loadTransResponse.setRrn(row.getCell((short) 13).getNumericCellValue() + "");
                    }
                    else {
                        loadTransResponse.setRrn("empty");
                    }

                   // loadTransResponse.setChargebackReason(row.getCell((short) 9).getNumericCellValue() + "");
                }
                catch (NumberFormatException e)
                {
                    loadTransResponse.setChargebackReason(row.getCell((short) 13).getStringCellValue() + "");
                }
               // loadTransResponse.setChargebackReason(row.getCell((short) 22).getStringCellValue() + "");
                chargebackRecordList.add(loadTransResponse);
            }
        }
        catch (FileNotFoundException e)
        {
            log.error("FileNotFoundException:::::" + e);
            //e.printStackTrace();
        }
        catch (IOException ie)
        {
            //ie.printStackTrace();
            log.error("IOException:::::" + ie);
        }
        return chargebackRecordList;
    }
    public List<PZTC40Record> readTC40file(PZFileVO fileName) throws SystemError
    {
        InputStream inputStream = null;
        List<PZTC40Record> vList = new ArrayList<PZTC40Record>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName.getFileName()));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            if (inputStream == null)
            {
                throw new SystemError("TC40 File is not found");
            }
            // get rows of sheet
            Iterator rows = sheet.rowIterator();

            int cntRowToLeaveFromTop = 0;
            int cntTotalRecords = 0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();

                cntRowToLeaveFromTop++;
                // Skip the header rows.
                if (cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Invalid File Format");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;

                if(null!=row.getCell((short) 3).getStringCellValue() && !("").equals(row.getCell((short) 3).getStringCellValue()))
                {
                    PZTC40Record pztc40Record = new PZTC40Record();
                    pztc40Record.setOrderno(row.getCell((short) 3).getStringCellValue() + "");

                    vList.add(pztc40Record);
                }
            }
        }
        catch (NullPointerException b)
        {
            log.error("NullPointerException---",b);
        }
        catch (FileNotFoundException e1)
        {
            log.error("FileNotFoundException---",e1);
        }
        catch (IOException ie)
        {
            log.error("IOException---", ie);
        }

        return vList;
    }
    public boolean isCellEmpty(final HSSFCell cell) {
        if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
            return true;
        }

        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
            return true;
        }
        return false;
    }

}

    /*public static void main(String args[])
    {

        String filePath = "D:\\auxpaysettlementfile.xlsx";
        //String filePath1="D:\\settlementDetailsMERCHANT_20131232345_10772_03July2014.xls";
        String filePath1 = "D:\\payforasia settlement\\Payforasia_9Apr2016.xls";
        PZSettlementFile PZSettlementFile = new PZSettlementFile();

        List<PZSettlementRecord> vTransactions = new ArrayList<PZSettlementRecord>();

        String str = "";
        String val = "";
        Connection con = null;
        TransactionEntry transactionEntry = null;

        //AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(null, Integer.parseInt(accountid));
        PZSettlementFile.setFilepath(filePath1);
        PZSettlementFile.setAccountId(Integer.parseInt("431"));

        PayforasiaPaymentProcess payforasiaPaymentProcess = new PayforasiaPaymentProcess();

        try
        {
            List<PZSettlementRecord> pzSettlementRecords = payforasiaPaymentProcess.readSettlementFile(PZSettlementFile);
            for (PZSettlementRecord settlementRecord : pzSettlementRecords)
            {
                System.out.println("=========================");

                System.out.println("traclingid===" + settlementRecord.getMerchantTXNid());
                System.out.println("amount===" + settlementRecord.getAmount());
                System.out.println("payment id===" + settlementRecord.getPaymentid());
                System.out.println("transaction status===" + settlementRecord.getStatusDetail());

                System.out.println("=========================");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/



    /*public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        Connection con=null;
        log.debug("Entering in PayForAsia readSettlementFile");
        transactionLogger.debug("Entering in PayForAsia readSettlementFile");
        InputStream inputStream = null;
        List<PZSettlementRecord> vList = new ArrayList<PZSettlementRecord>();

        try
        {
            transactionLogger.debug("Entering in try block");
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
                if (cntRowToLeaveFromTop==2)
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
                if(((String)((HSSFCell) row.getCell((short)11)).getStringCellValue()).trim().equals("Successful"))
                {
                    if(((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Non Refunded"))
                    {
                        if(((String)((HSSFCell) row.getCell((short)14)).getStringCellValue()).trim().equals("Non Chargeback"))
                        {
                            loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                        }
                        else
                        {
                            loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                        }
                    }

                }
                else if(((String)((HSSFCell) row.getCell((short)11)).getStringCellValue()).equals("Successful"))
                {
                    if(((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Refunded"))
                    {
                        if (((String) ((HSSFCell) row.getCell((short) 14)).getStringCellValue()).trim().equals("Non Chargeback"))
                        {
                            loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                        }
                        else
                        {
                            loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL));
                        }
                    }
                }
                loadTransResponse.setAmount((float)((HSSFCell) row.getCell((short)9)).getNumericCellValue()+"");
                loadTransResponse.setMerchantTXNid(((HSSFCell) row.getCell((short) 4)).getStringCellValue() + "");
                loadTransResponse.setPaymentid(((HSSFCell) row.getCell((short) 3)).getStringCellValue() + "");
                log.debug("trackingid=" + loadTransResponse.getMerchantTXNid());
                transactionLogger.debug("trackingid=" + loadTransResponse.getMerchantTXNid());
                if(loadTransResponse!= null)
                {
                    vList.add(loadTransResponse);
                    transactionLogger.debug("Transaction List===");
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
    }*/

