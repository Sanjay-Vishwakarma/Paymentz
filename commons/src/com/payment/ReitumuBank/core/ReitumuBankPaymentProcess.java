package com.payment.ReitumuBank.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
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
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/10/15
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReitumuBankPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(ReitumuBankPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ReitumuBankPaymentProcess.class.getName());

    /*@Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "reitumuCreditPage.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }*/

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D){
        log.debug("3d page displayed....."+response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\" "+target+">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){
        log.debug("3d page displayed....."+response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        log.debug("inside reitumu payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

    public List<PZTC40Record> readTC40file(PZFileVO fileName) throws SystemError
    {
        log.debug("Entering in Reitumu readTC40file");
        transactionLogger.debug("Entering in Reitumu readTC40file");

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

                if(null!=row.getCell((short) 1).getStringCellValue() && !("").equals(row.getCell((short) 1).getStringCellValue()))
                {
                    PZTC40Record pztc40Record = new PZTC40Record();
                    pztc40Record.setOrderno(row.getCell((short) 1).getStringCellValue() + "");

                    vList.add(pztc40Record);
                }
            }
            log.debug("Total count size---"+cntTotalRecords);
        }
        catch (NullPointerException b)
        {
            log.error("NullPointerException--->",b);
        }
        catch (FileNotFoundException e1)
        {
            log.error("FileNotFoundException--->",e1);
        }
        catch (IOException ie)
        {
            log.error("IOException--->", ie);
        }

        return vList;
    }

    public static void main(String[] args) throws SystemError
    {
        String filepath = "D:\\Tc40Reitumu1.xls";


        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFileName(filepath);

        ReitumuBankPaymentProcess payforasiaPaymentProcess = new ReitumuBankPaymentProcess();
        //System.out.println("orderno-----"+pzFileVO.getFileName());
        List<PZTC40Record> pztc40Record = payforasiaPaymentProcess.readTC40file(pzFileVO);
        for(PZTC40Record pztc40Record1 : pztc40Record)
        {
            transactionLogger.debug("orderno-----"+pztc40Record1.getOrderno());
        }

        transactionLogger.debug("orderno-----"+pztc40Record.size());

    }
}
