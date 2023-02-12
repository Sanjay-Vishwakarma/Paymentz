package com.payment.ReitumuBank.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZFileVO;
import com.payment.request.PZSettlementFile;
import com.payment.request.PZTC40Record;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/16/15
 * Time: 8:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReitumuBankMerchantPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(ReitumuBankMerchantPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ReitumuBankMerchantPaymentProcess.class.getName());

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
        return "reitumuspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        log.debug("inside reitumu payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D){
        //System.out.println("3d page displayed....."+response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\""+target+">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){
        //System.out.println("3d page displayed....."+response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }
    @Override
    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
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
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                // Skip the header rows.
                if (cntRowToLeaveFromTop==5)
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
                Iterator cells = row.cellIterator();

                PZSettlementRecord loadTransResponse=new PZSettlementRecord();
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if(cell.getCellNum()==0)
                    {
                        //Terminal Id
                    }
                    else if(cell.getCellNum()==1)
                    {
                        //Settelement Date
                    }
                    else if(cell.getCellNum()==2)
                    {
                        //Processed date
                    }
                    else if(cell.getCellNum()==3)
                    {
                        //Ecomm Id (Paymentid)
                        String paymentId=null;
                        if(cell.getCellType() == cell.CELL_TYPE_NUMERIC)
                        {
                            paymentId=String.valueOf(cell.getNumericCellValue());
                        }
                        else
                        {
                            paymentId= cell.getStringCellValue();
                        }
                        if(paymentId !=null || paymentId.trim() != "")
                        {
                            loadTransResponse.setPaymentid(cell.getStringCellValue().trim());
                        }
                    }
                    else if(cell.getCellNum()==4)
                    {
                        //Processing ID
                    }
                    else if(cell.getCellNum()==5)
                    {
                        //Transaction type
                        String transactionStatus = cell.getStringCellValue();
                        if (!(transactionStatus == null || transactionStatus.trim().equals("")))
                        {
                            if ("Transaction".equals(transactionStatus.trim()))
                            {
                                loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                            }
                            else if ("Refund".equals(transactionStatus.trim()))
                            {
                                loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                            }
                        }
                    }
                    else if (cell.getCellNum() == 6)
                    {
                        //amount
                        Double amount = Double.valueOf(cell.getNumericCellValue());
                        if (amount == null)
                        {
                            throw new SystemError("ERROR : Transaction amount not Found from the File");
                        }
                        else
                        {
                            loadTransResponse.setAmount(amount.toString().trim());
                        }
                    }
                    else if(cell.getCellNum()==7)
                    {
                        //Currency
                    }
                    else if(cell.getCellNum()==8)
                    {
                        //card type
                    }
                    else if(cell.getCellNum()==9)
                    {
                        //Merchant Transaction Id

                       String trackingId=null;
                        if(cell.getCellType() == cell.CELL_TYPE_NUMERIC)
                        {
                            trackingId=String.valueOf(cell.getNumericCellValue());
                        }
                        else
                        {
                            trackingId= cell.getStringCellValue();
                        }
                        if(trackingId==null || trackingId.trim()=="")
                        {
                            throw new SystemError("ERROR : tracking Id not found from the file");
                        }
                        else
                        {
                            loadTransResponse.setMerchantTXNid(trackingId.trim());
                        }
                    }
                    else if(cell.getCellNum()==10)
                    {
                        //Arn
                    }
                }
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
        return (List<PZSettlementRecord>)vList;
    }
    public static String round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_DOWN);
        return bd.toString();
    }

    public List<PZTC40Record> readTC40file(PZFileVO fileName) throws SystemError
    {
        /*log.debug("Entering in PayForAsia readSettlementFile");
        transactionLogger.debug("Entering in PayForAsia readSettlementFile");*/
        InputStream inputStream = null;
        List<PZTC40Record> vList = new ArrayList<PZTC40Record>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName.getFileName()));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            log.debug("Number of rows---"+sheet.getPhysicalNumberOfRows());

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
                if (cntRowToLeaveFromTop ==3)
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
                Iterator cells = row.cellIterator();
                PZTC40Record pztc40Record = new PZTC40Record();

                //System.out.println(row.getCell((short) 2).getStringCellValue());

                pztc40Record.setOrderno(row.getCell((short) 1).getStringCellValue() + "");

                vList.add(pztc40Record);

            }

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

    /*public static void main(String args[])
    {
        String filePath1 = "D:\\custom_report_1454415087_reitumubank02.xls";

        PZSettlementFile PZSettlementFile = new PZSettlementFile();
        PZSettlementFile.setFilepath(filePath1);
        PZSettlementFile.setAccountId(Integer.parseInt("434"));

        ReitumuBankMerchantPaymentProcess process = new ReitumuBankMerchantPaymentProcess();
        try
        {
            List<PZSettlementRecord> pzSettlementRecords = process.readSettlementFile(PZSettlementFile);
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

    public static void main(String[] args) throws SystemError
    {
        //System.out.println("Inside in---");

        String filepath = "E:\\Tc40Reitumu.xls";

        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFileName(filepath);

        ReitumuBankMerchantPaymentProcess payforasiaPaymentProcess = new ReitumuBankMerchantPaymentProcess();

        List<PZTC40Record> pzChargebackRecords = payforasiaPaymentProcess.readTC40file(pzFileVO);

        //System.out.println("chargebackFile----"+pzChargebackRecords.size());

        for(PZTC40Record chargebackRecord : pzChargebackRecords)
        {
            //System.out.println("paymentid--"+chargebackRecord.getOrderno());
        }

    }
}
