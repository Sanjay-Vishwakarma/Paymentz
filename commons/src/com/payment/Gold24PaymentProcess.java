package com.payment;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZSettlementFile;
import com.payment.response.PZSettlementRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 3/14/14
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Gold24PaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(Gold24PaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(Gold24PaymentProcess.class.getName());

    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        log.debug("entering in Gold24Payment");
        transactionLogger.debug("entering in Gold24Payment");
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
                if(((String)((HSSFCell) row.getCell((short)11)).getStringCellValue()).trim().equals("Sale"))
                {
                    if(((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Transaction successful") || ((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Marked as success"))
                    {

                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));

                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                    }
                }
                else if(((String)((HSSFCell) row.getCell((short)11)).getStringCellValue()).equals("Refund"))
                {
                    if(((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Transaction successful") || ((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Marked as refunded"))
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL));
                    }
                }
                //loadTransResponse.setSDateTime(((HSSFCell) row.getCell((short)2)).getStringCellValue()+"");
                String sAmount = (float)((HSSFCell) row.getCell((short)9)).getNumericCellValue()+"";
                loadTransResponse.setAmount(new BigDecimal(sAmount).divide(new BigDecimal(100.00)).floatValue()+ "");
                loadTransResponse.setMerchantTXNid((long)((HSSFCell) row.getCell((short)8)).getNumericCellValue()+"");
                loadTransResponse.setPaymentid((long)((HSSFCell) row.getCell((short)0)).getNumericCellValue()+"");

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
}
