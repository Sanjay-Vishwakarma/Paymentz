package com.payment.pfs.core;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.PfsPaymentGateway;
import com.logicboxes.util.ApplicationProperties;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZFileVO;
import com.payment.request.PZSettlementFile;
import com.payment.request.PZTC40Record;
import com.payment.response.PZSettlementRecord;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 3/30/13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class PfsPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PfsPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PfsPaymentProcess.class.getName());
    private final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.PfsServlet");
    private final static String SERVLET = rb.getString("SERVLET");
    final static String WAITSERVLET3D = rb.getString("3DSERVLET");

    @Override
    public String getRedirectPage(Hashtable parameters) throws SystemError
    {

        StringBuffer responseBuffer = new StringBuffer();
        Hashtable error = new Hashtable();
        Hashtable otherdetails = new Hashtable();
        String template = null;
        String vbv = null;
        String autoredirect = null;
        String key = "";
        String val = "";
        String ctoken = String.valueOf(parameters.get("ctoken"));

        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String sql = "select autoredirect,template,vbv from members where memberid =?";
            PreparedStatement p = connection.prepareStatement(sql);
            p.setString(1, String.valueOf(parameters.get("TOID")));
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                autoredirect = rs.getString("autoredirect");
                template = rs.getString("template");
                vbv = rs.getString("vbv");
            }
        }
        catch (Exception e)
        {
            error.put("Error", "Error occured while inserting data");

        }
        finally
        {
            Database.closeConnection(connection);
        }

        StringBuffer sbuf = new StringBuffer();

        if (error.size() > 0)
        {
            sbuf.append("Following Parameters are Invalid");
            Enumeration enu = error.keys();

            sbuf.append("<center><table border=1>");
            sbuf.append("<tr bgcolor=\"blue\" >");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Field");
            sbuf.append("</font></td>");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Error");
            sbuf.append("</font></td>");
            sbuf.append("</tr>");
            while (enu.hasMoreElements())
            {
                String field = (String) enu.nextElement();
                sbuf.append("<tr>");
                sbuf.append("<td>");
                sbuf.append(field);
                sbuf.append("</td>");
                sbuf.append("<td>");
                sbuf.append((String) error.get(field));
                sbuf.append("</td>");
                sbuf.append("</tr>");
            }
            sbuf.append("</table>");

            otherdetails.put("MESSAGE", sbuf.toString());

            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");

            try
            {
                String toid = String.valueOf(parameters.get("TOID"));
                responseBuffer.append(Template.getError(toid, otherdetails));


            }
            catch (SystemError se)
            {
                log.error("Excpetion in WaitServlet", se);
                transactionLogger.error("Excpetion in WaitServlet", se);

            }
            return responseBuffer.toString();
        }

        parameters.put("autoredirect", autoredirect);
        parameters.put("TEMPLATE", template);
        parameters.put("VBV", vbv);


        responseBuffer.append("<HTML>");
        responseBuffer.append("<HEAD> <script language=\"javascript\">" +
                "function Load(){" +
                "document.form.submit();" +
                "}" +
                " </script>");
        responseBuffer.append("</HEAD>");
        responseBuffer.append("<BODY onload=Load()>");
        responseBuffer.append("<form name=\"form\" action=\""+SERVLET+"?ctoken"+ctoken+"\" method=\"post\" >");
        Enumeration enu = parameters.keys();

        while (enu.hasMoreElements())
        {
            key = (String) enu.nextElement();
            val = parameters.get(key).toString();
            responseBuffer.append("<input type=hidden name=\"" + key.toString() + "\" value=\"" + val.toString() + "\" >");
            //log.debug("<input type=hidden name=\"" + key.toString() + "\" value=\"" + val.toString() + "\"");
        }

        responseBuffer.append("</form>");
        responseBuffer.append("</BODY>");
        responseBuffer.append("</HTML>");

        return responseBuffer.toString();
    }


    private void sendFailMails(String transactionStatus, String transactionMessage,CommResponseVO transRespDetails,String trackingId, Hashtable transactionAttributes,Hashtable attributeHash,String transactionDescription) throws SystemError
    {
        String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        String ids = TimeZone.getTimeZone("GMT+5:30").getID();
        SimpleTimeZone tz = new SimpleTimeZone(+0 * 00 * 60 * 1000, ids);
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar(tz);
        cal.setTime(new java.util.Date());
    }

    public Hashtable process3DConfirmation(String trackingId, String paRes)
    {
        Hashtable responseHash = new Hashtable();
        ComConfirmRequestVO requestVo = new ComConfirmRequestVO();

        requestVo.setTrackingId(trackingId);
        requestVo.setPaRes(paRes);

        String authMessage = "";
        PfsPaymentGateway pg = null;
        CommResponseVO transRespDetails = null;
        String accountId = "";
        String toid = "";
        Connection connection = null;
        String amount = "";
        String isService = "";
        String isSuccessful="N";
        TransactionManager transactionManager = new TransactionManager();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        try
        {
            connection = Database.getConnection();

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            accountId = transactionDetailsVO.getAccountId();
            amount = transactionDetailsVO.getAmount();
            toid = transactionDetailsVO.getToid();

            MerchantDetailsVO merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);
            isService = merchantDetailsVO.getService();
            log.debug("---is Service PFSPaymentProcess---"+isService);

            String sql1 = "select merchantid, username, passwd from gateway_accounts where accountid = ?";
            PreparedStatement p = connection.prepareStatement(sql1);
            p.setString(1, accountId);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                requestVo.setMerchantId(rs.getString("merchantid"));
                requestVo.setUserName(rs.getString("username"));
                requestVo.setPassword(rs.getString("passwd"));
            }

            pg = new PfsPaymentGateway(accountId);
            transRespDetails = (CommResponseVO)pg.processConfirm(requestVo);

            if ((transRespDetails.getStatus().trim()).equals("fail"))
            {
                authMessage = "Failed";
            }
            if ((transRespDetails.getStatus().trim()).equals("success"))
            {
                isSuccessful="Y";
                authMessage = transRespDetails.getDescription();
            }

        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PzTechnical Exception while refunding transaction VIA Pfs",e);
            transactionLogger.error("PzTechnical Exception while refunding transaction VIA Pfs",e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "inquiry");
            responseHash.put("status", "Failed");

        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException while refunding transaction VIA Pfs",e);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA Pfs",e);

            PZExceptionHandler.handleDBCVEException(e, null, "inquiry");
            responseHash.put("status", "Failed");
        }
        catch (SQLException e)
        {
            log.error("SQLException while refunding transaction VIA Pfs",e);
            transactionLogger.error("SQLException while refunding transaction VIA Pfs",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(PfsPaymentProcess.class.getName(),"inquiry",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"inquiry");
            responseHash.put("status", "Failed");

        }
        catch (SystemError systemError)
        {
            log.error("PZDBViolationException while refunding transaction VIA Pfs",systemError);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA Pfs",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(PfsPaymentProcess.class.getName(), "inquiry", null, "common", "Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "inquiry");
            responseHash.put("status", "Failed");

        }

        finally
        {
            Database.closeConnection(connection);
        }
        responseHash.put("trackingId", trackingId);
        responseHash.put("accountId",accountId);
        responseHash.put("merchantId", requestVo.getMerchantId());
        responseHash.put("status", authMessage);
        responseHash.put("displayName", pg.getDisplayName());
        responseHash.put("issuccessful",isSuccessful);
        responseHash.put("finalStatus", transRespDetails.getStatus());
        return responseHash;
    }

    /*public String getSpecificVirtualTerminalJSP()
    {
        return "pfsspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }*/

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D){

        String target = "target=_blank";
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\""+target +">" +
        "<input type=hidden name=\"PaReq\" value=\""+response3D.getPaReq()+"\"/>" +
        "<input type=hidden name=\"TermUrl\" value=\""+WAITSERVLET3D+"?t="+trackingId+"&ctoken="+ctoken+"\"/>" +
        "<input type=hidden name=\"MD\" value=\""+response3D.getMd()+"\"/>" +
        "</form>" +
        "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){

        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                "<input type=hidden name=\"PaReq\" value=\""+response3D.getPaReq()+"\"/>" +
                "<input type=hidden name=\"TermUrl\" value=\""+WAITSERVLET3D+"?t="+trackingId+"&ctoken="+ctoken+"\"/>" +
                "<input type=hidden name=\"MD\" value=\""+response3D.getMd()+"\"/>" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("MD");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PaReq");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue(WAITSERVLET3D+"?t="+directKitResponseVO.getTrackingId()+"&ctoken=1234567890");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);



    }

    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        InputStream inputStream = null;
        List<PZSettlementRecord> vList = new ArrayList<PZSettlementRecord>();

        log.debug("File Path--->>"+fileName.getFilepath());
        log.debug("File Name--->>"+fileName.getFileName());

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


                if(((String)((HSSFCell) row.getCell((short)6)).getStringCellValue()).trim().equals("Sale"))
                {
                    if(((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Successful"))
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                    }
                }
                else if(((String)((HSSFCell) row.getCell((short)6)).getStringCellValue()).equals("Refund"))
                {
                    if(((String)((HSSFCell) row.getCell((short)12)).getStringCellValue()).trim().equals("Refund Successful - Full Refund"))
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL));
                    }
                }
                //loadTransResponse.setSDateTime(((HSSFCell) row.getCell((short)2)).getStringCellValue()+"");
                String sAmount = (float)((HSSFCell) row.getCell((short)7)).getNumericCellValue()+"";
                loadTransResponse.setAmount(sAmount);

                String strMerchantTXNid;
                if(row.getCell((short) 9).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                {
                    strMerchantTXNid = String.valueOf((long)row.getCell((short)9).getNumericCellValue());
                }
                else
                {
                    strMerchantTXNid = row.getCell((short) 9).getStringCellValue();
                }

                if(strMerchantTXNid.endsWith("_R"))
                {
                    strMerchantTXNid = strMerchantTXNid.substring(0,strMerchantTXNid.length() -2);
                }

                loadTransResponse.setMerchantTXNid(strMerchantTXNid);
                loadTransResponse.setPaymentid((long)((HSSFCell) row.getCell((short)0)).getNumericCellValue()+"");

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
        }
        log.debug("PFS settlement file updated successfully");
        return vList;
    }

    /*public List<PZTC40Record> readTC40Records(PZFileVO fileName) throws SystemError
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
                throw new SystemError("TC 40 file not found");
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
                throw new SystemError("TC40 File Error : Invalid File Format");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;
                Iterator cells = row.cellIterator();
                PZTC40Record pztc40Records = new PZTC40Record();

                //load vo

                *//*pztc40Records.setArn(row.getCell((short) 1).getNumericCellValue() + "");
                pztc40Records.setMerchantcode(row.getCell((short) 2).getNumericCellValue() + "");
                pztc40Records.setIsrefund(row.getCell((short) 3).getStringCellValue() + "");
                pztc40Records.setAmount(row.getCell((short) 5).getNumericCellValue() + "");*//*

                vList.add(pztc40Records);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return vList;
    }*/

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
            log.error("FileNotFoundException---",e1);
        }
        catch (IOException e1)
        {
            log.error("IOException---", e1);
        }

        return vList;
    }

    public static void main(String[] args) throws SystemError
    {
        String filepath= "E:\\TC40.xls";
        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFileName(filepath);

        PfsPaymentProcess pfsPaymentProcess=new PfsPaymentProcess();

        List<PZTC40Record> readTC40Records= pfsPaymentProcess.readTC40file(pzFileVO);

        for (PZTC40Record pztc40Records : readTC40Records)
        {
            //System.out.println("filepath-----"+pztc40Records.getArn() +"-"+pztc40Records.getMerchantcode()+"-"+pztc40Records.getIsrefund()+"-"+pztc40Records.getAmount());
        }



    }

    /*public static void main(String[] args)
    {
        //String filePath = "E:\\CB_Payforasia_Jinesh.xls";
        String filePath = "E:\\TC40.xls";
        List<PZSettlementRecord> vTransactions = new ArrayList<PZSettlementRecord>();

        try
        {
            //POIFSFileSystem fs = new POIFSFileSystem(new BufferedInputStream(new FileInputStream(filePath)));
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));
            HSSFSheet sheet = wb.getSheetAt(0);

            //HSSFRow row = sheet.getRow(2);
            //HSSFCell cell = row.getCell((short)0);

            //System.out.println("data---"+cell.getStringCellValue());
            System.out.println("data---"+sheet.getPhysicalNumberOfRows());
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

                String strStatus = null;
                String strRefundStatus = null;
                String strChargebackStatus = null;

                System.out.println(row.getCell((short) 1).getNumericCellValue());
                //strRefundStatus = row.getCell((short) 12).getStringCellValue();
                //strChargebackStatus = row.getCell((short) 14).getStringCellValue();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
}