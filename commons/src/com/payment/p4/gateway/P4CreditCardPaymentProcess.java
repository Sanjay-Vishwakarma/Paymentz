package com.payment.p4.gateway;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZFileVO;
import com.payment.request.PZTC40Record;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jinesh on 7/14/2016.
 */
public class P4CreditCardPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(P4CreditCardPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4CreditCardPaymentProcess.class.getName());

    public List<PZTC40Record> readTC40file(PZFileVO fileName) throws SystemError
    {
        log.debug("Entering in P4CreditCardPaymentProcess");
        transactionLogger.debug("Entering in P4CreditCardPaymentProcess");
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
                Iterator cells = row.cellIterator();
                PZTC40Record pztc40Record = new PZTC40Record();

                //System.out.println(row.getCell((short) 2).getStringCellValue());

                pztc40Record.setOrderno(row.getCell((short) 0).getStringCellValue() + "");

                vList.add(pztc40Record);

            }

        }
        catch (FileNotFoundException e1)
        {
            log.error("FileNotFoundException---", e1);
        }
        catch (IOException e1)
        {
            log.error("IOException---", e1);
        }

        return vList;
    }
}
