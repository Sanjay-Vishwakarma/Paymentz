package com.directi.pg;

import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/29/14
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class RollingReserveManager
{
    private static Logger log = new Logger(RollingReserveManager.class.getName());


    public String processRollingReserve(String accountid,String fileName, String fullFileName)
    {
        List<RollingReserveVo> vTransactions = new ArrayList<RollingReserveVo>();
        StringBuffer errString = new StringBuffer();
        errString.append("<br>|  Tracking ID  |   Merchant ID  |                    status                  | " +  "\r\n");
        MailService mailService=new MailService();
        if(!isFileNameValid(fileName))
        {
            log.error("RollingReserveManager : Error : Rolling Reserve Filename is invalid");
            return "Error : Rolling Reserve Filename is invalid";
        }

        //Loading the Transactions from Excel File
        try
        {
            vTransactions = loadTransactions2(fullFileName);
        }catch(SystemError se)
        {
            log.error("RollingReserveManager : Error While Loading the Transactions : "+ se.getMessage());
            return se.getMessage();
        }
        Connection connection=null;
        //Display Loaded Transactions
        String trackingId="";
        String accountId="";
        BigDecimal rrAmount=null;
        try
        {   HashMap rrDataMail=new HashMap();
            connection=Database.getConnection();
            for(RollingReserveVo vRRTransVO : vTransactions)
            {
                trackingId= String.valueOf(vRRTransVO.getTrackingId());
                accountId= String.valueOf(vRRTransVO.getAccountId());
                rrAmount= new BigDecimal(vRRTransVO.getRrAmount().toString());
                LinkedHashMap rrData=new LinkedHashMap();
                rrData.put("Tracking Id",trackingId);
                rrData.put("Account Id",accountId);
                rrData.put("Rolling Reserve Amount",rrAmount.toString());
                rrData.put("Rolling Reserve Status","0");

                if(accountid.equals(String.valueOf(accountId)))
                {

                    String q1="SELECT * FROM bin_details WHERE icicitransid=? AND accountid=? AND isSuccessful='Y' AND isSettled='Y';";
                    PreparedStatement p1=connection.prepareStatement(q1);
                    p1.setString(1, trackingId);
                    p1.setString(2, accountId);
                    ResultSet rs=p1.executeQuery();
                    if(rs.next())
                    {
                        String q2= "UPDATE bin_details SET isRollingReserveKept='N', isRollingReserveReleased='Y', RollingReserveAmountKept=?, RollingReserveAmountReleased=? WHERE icicitransid =? AND accountid=?";
                        p1=connection.prepareStatement(q2);
                        p1.setString(1,"0.00");
                        p1.setBigDecimal(2,rrAmount);
                        p1.setString(3, trackingId);
                        p1.setString(4,accountId);
                        int k=p1.executeUpdate();
                        if(k==1)
                        {

                            rrData.put("Rolling Reserve Status","successful");
                            errString.append("<br>|  "+trackingId+"  |   "+accountId+"  |       Rolling Reserve kept successful       | " +  "\r\n");
                        }
                        else
                        {
                            rrData.put("Rolling Reserve Status","Failed");
                            errString.append("<br>|  "+trackingId+"  |   "+accountId+"  |       Rolling Reserve kept Failed           | " +  "\r\n");
                        }
                    }
                    else
                    {
                        rrData.put("Rolling Reserve Status","Transaction is not marked as successful & settled");
                        errString.append("<br>|  "+trackingId+"  |   "+accountId+"  |Transaction is not marked as successful & settled| " +  "\r\n");
                    }
                }
                else
                {
                    rrData.put("Rolling Reserve Status","Accountid is not valid");
                    errString.append("<br>|  "+trackingId+"  |   "+accountId+"  |     Accountid is not valid      | " +  "\r\n");
                }
             rrDataMail.put(trackingId,rrData);
            }
            Date dt=new Date();
            rrDataMail.put(MailPlaceHolder.MULTIPALTRANSACTION,mailService.getDetailTable(rrDataMail));
            rrDataMail.put(MailPlaceHolder.FROMDATE,dt);
            mailService.sendMail(MailEventEnum.ROLLING_REVERSE_TRANSACTION_DETAILS,rrDataMail);
        }
        catch (SystemError systemError)
        {
            log.error("System Error",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Error", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }

        //
        String vMailSend = "";
        return "Settlement File uploaded successfully. "+ vMailSend + "\r\n" + errString.toString();
    }

    private boolean isFileNameValid(String fileName)
    {
        //Sample Valid Filename: settlementDetailsMERCHANT_201212192328.xls
        boolean isValidFilename = false;

        if(fileName.length()<20)
        {
            return isValidFilename;
        }
        if(!fileName.endsWith(".xls"))
        {
            return isValidFilename;
        }
        isValidFilename = true;
        return isValidFilename;
    }
/*
    public List<RollingReserveVo> loadTransactions(String fileName) throws SystemError
    {
        InputStream inputStream = null;
        List<RollingReserveVo> vList = new ArrayList<RollingReserveVo>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            if (inputStream == null)
            {
                throw new SystemError("Rolling Reserve File is not found");
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
                throw new SystemError("Rolling Reserve Error : Invalid File Format");
            }
            int i=0;
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;

                RollingReserveVo RRDetailsVO=new RollingReserveVo();
                RRDetailsVO.setTrackingId((int)(row.getCell((short) 0).getNumericCellValue()));
                RRDetailsVO.setDescription(row.getCell((short) 1).getStringCellValue());
                RRDetailsVO.setAccountId((int) row.getCell((short) 2).getNumericCellValue());
                RRDetailsVO.setAmount(new BigDecimal(row.getCell((short) 3).getNumericCellValue()));
                RRDetailsVO.setRrAmount(new BigDecimal(row.getCell((short) 4).getNumericCellValue()));
                System.out.println(RRDetailsVO.toString());
                if(RRDetailsVO!= null)
                {

                    vList.add(RRDetailsVO);
                }
            }
        }
        catch (IOException e)
        {   e.printStackTrace();
            throw new SystemError("ERROR: Rolling Reserve File cant be read");
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
                    throw new SystemError("ERROR: Rolling Reserve File cant be closed successfully");
                }

            }
        }
        return (List<RollingReserveVo>)vList;
    }*/

    public List<RollingReserveVo> loadTransactions2(String fileName) throws SystemError
    {
        InputStream inputStream = null;
        List<RollingReserveVo> vList = new ArrayList<RollingReserveVo>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
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
                SettleTransVO vSettleTransVO = new SettleTransVO();
                while (cells.hasNext())
                {
                    RollingReserveVo RRDetailsVO=new RollingReserveVo();
                    HSSFCell cell = (HSSFCell) cells.next();
                    if(cell.getCellNum()==0)
                    {
                        String vPayOrderId = cell.getStringCellValue();
                        if(vPayOrderId==null || vPayOrderId=="")
                        {
                            throw new SystemError("ERROR : PaymentOrderID not Found from the File");
                        }
                        //System.out.println(cell.getStringCellValue());
                        RRDetailsVO.setTrackingId(cell.getStringCellValue());
                        continue;
                    }
                    if(cell.getCellNum()==1)
                    {

                        RRDetailsVO.setDescription(cell.getStringCellValue());
                        continue;
                    }
                    if(cell.getCellNum()==2)
                    {
                        RRDetailsVO.setAccountId(cell.getStringCellValue());

                        continue;
                    }
                    if(cell.getCellNum()==3)
                    {
                        RRDetailsVO.setAmount(cell.getStringCellValue());

                        continue;
                    }
                    if(cell.getCellNum()==4)
                    {
                        RRDetailsVO.setRrAmount(cell.getStringCellValue());

                        continue;
                    }

                    if(RRDetailsVO!= null)
                    {

                        vList.add(RRDetailsVO);
                    }

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
        return (List<RollingReserveVo>)vList;
    }
}
