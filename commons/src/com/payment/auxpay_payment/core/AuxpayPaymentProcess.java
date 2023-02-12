package com.payment.auxpay_payment.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionEntry;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
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
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jinesh on 11/7/2015.
 */
public class AuxpayPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(AuxpayPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(AuxpayPaymentProcess.class.getName());

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D){
        String target = "target=_blank";
        transactionLogger.error("3d page displayed....."+response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\""+target+">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){
        transactionLogger.error("3d page displayed....."+response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

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
               Iterator cells = row.cellIterator();
               PZSettlementRecord loadTransResponse=new PZSettlementRecord();
               while (cells.hasNext())
               {
                   HSSFCell cell = (HSSFCell) cells.next();
                   if(cell.getCellNum()==0)
                   {
                       //Date of transaction at bank side.
                   }
                   else if(cell.getCellNum()==1)
                   {
                       //time of transaction at bank side
                   }
                   else if(cell.getCellNum()==2)
                   {
                       //Merchant name
                   }
                   else if(cell.getCellNum()==3)
                   {
                       //trackingid
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
                           throw new SystemError("ERROR : Tracking not Found from the File");
                       }
                       else
                       {
                           loadTransResponse.setMerchantTXNid(trackingId.trim());
                       }
                   }
                   else if(cell.getCellNum()==4)
                   {
                       //card first six & last four data.
                   }
                   else if(cell.getCellNum()==5)
                   {
                       //cardtype
                   }
                   else if(cell.getCellNum()==6)
                   {
                       //cardholder name
                   }
                   else if(cell.getCellNum()==7)
                   {
                       //bank transaction id
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
                   else if(cell.getCellNum()==8)
                   {
                       //external ref
                   }
                   else if(cell.getCellNum()==9)
                   {
                       //customer id
                   }
                   else if(cell.getCellNum()==10)
                   {
                       //transaction amount
                       String amount="";
                       if(cell.getCellType()==cell.CELL_TYPE_NUMERIC)
                       {
                           amount=String.valueOf(cell.getNumericCellValue());
                       }
                       else
                       {
                           amount=cell.getStringCellValue();
                       }
                       if(amount ==null)
                       {
                           throw new SystemError("ERROR : Transaction amount not Found from the File");
                       }
                       else
                       {
                           loadTransResponse.setAmount(amount.toString().trim());
                       }
                   }
                   else if(cell.getCellNum()==11)
                   {
                       //transaction currency
                   }
                   else if(cell.getCellNum()==12)
                   {
                       //transaction status
                       String transactionStatus=cell.getStringCellValue();
                       if(!(transactionStatus==null || transactionStatus.trim().equals("")))
                       {
                           if("DEPOSIT".equals(transactionStatus.trim()))
                           {
                               loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                           }
                           else if("FAILED".equals(transactionStatus.trim()))
                           {
                               loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.AUTH_FAILED));
                           }
                       }
                   }
                   else if(cell.getCellNum()==13)
                   {
                       //transaction decline reason
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
    public static void main(String args[])
    {
        String filePath="D:\\auxpaysettlementfile.xlsx";
        //String filePath1="D:\\settlementDetailsMERCHANT_20131232345_10772_03July2014.xls";
        String filePath1="D:\\auxpay_2450_settlementfile_1st_9th_dec_2015_6.xls";

        PZSettlementFile PZSettlementFile = new PZSettlementFile();

        List<PZSettlementRecord> vTransactions = new ArrayList<PZSettlementRecord>();

        String str="";
        String val="";
        Connection con = null;
        TransactionEntry transactionEntry = null;

        //AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(null, Integer.parseInt(accountid));
        PZSettlementFile.setFilepath(filePath1);
        PZSettlementFile.setAccountId(Integer.parseInt("431"));

        AuxpayPaymentProcess process=new AuxpayPaymentProcess();

        try
        {
            List<PZSettlementRecord> pzSettlementRecords=process.readSettlementFile(PZSettlementFile);
            for(PZSettlementRecord settlementRecord:pzSettlementRecords)
            {
              /*  System.out.println("=========================");

                System.out.println("traclingid==="+settlementRecord.getMerchantTXNid());
                System.out.println("amount==="+settlementRecord.getAmount());
                System.out.println("payment id==="+settlementRecord.getPaymentid());
                System.out.println("transaction status==="+settlementRecord.getStatusDetail());

                System.out.println("=========================");*/
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ",e);
        }

    }
    public static String round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_DOWN);
        return bd.toString();
    }

}
