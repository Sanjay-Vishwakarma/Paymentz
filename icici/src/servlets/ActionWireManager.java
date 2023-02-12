import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.PayoutDAO;
import com.manager.vo.payoutVOs.WireVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: NISHANT
 * Date: 6/4/14
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionWireManager extends HttpServlet
{
    private final static String PAYOUT_REPORT_FILE_PATH = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE_PATH");
    private final static String SETTLEMENT_FILE_PATH = ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
    private final static String PAYOUT_REPORT_FILE = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE");

    private static Logger log = new Logger(ActionWireManager.class.getName());

    public static boolean sendFile(String filepath,String filename,File f,HttpServletResponse response)throws Exception
    {

        int length = 0;
        // Set browser download related headers
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }

        in.close();
        op.flush();
        op.close();

        log.info("Successfully donloaded  file======"+filename);
        return true;

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in WireList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        PayoutDAO payoutDAO = new PayoutDAO();
        ResultSet resultSet=null;
        String errormsg = "";
        String EOL = "<BR>";
        Hashtable data=new Hashtable();
        String settledid=req.getParameter("settledid");  // Make New
        Hashtable payoutDetails = new Hashtable();
        String action=req.getParameter("action");
        String wireId=req.getParameter("mappingid");
        String payoutid=req.getParameter("payoutid");
        String message=req.getParameter("message");

        //System.out.println("action::::"+action+" mappingid:::"+wireId);
        if(!functions.isValueNull(wireId))
        {
            wireId="";
        }
        String terminalId=req.getParameter("terminalId");
        String settlementCycleNo=req.getParameter("settlementcycle_no");

        try
        {
            if (functions.isValueNull(action))
            {
            if (action.equals("PayoutView"))
            {
                Hashtable payOutDetailsUpdate = payoutDAO.getPayOutDetailsUpdate(req.getParameter("payout_id"));
                req.setAttribute("viewpayout",payOutDetailsUpdate);
                req.setAttribute("isreadonly","View");
                req.setAttribute("settledid",req.getParameter("settledid"));
                req.setAttribute("reportid",req.getParameter("reportid"));
                req.setAttribute("payoutid",req.getParameter("payout_id"));
                RequestDispatcher rd = req.getRequestDispatcher("/payoutDetailsUpdate.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else if (action.equals("PayoutUpdate"))
            {
                //System.out.println("inside else if PayoutUpdate");
                Hashtable payOutDetailsUpdate = payoutDAO.getPayOutDetailsUpdate(req.getParameter("payout_id"));
                req.setAttribute("viewpayout",payOutDetailsUpdate);
                req.setAttribute("isreadonly","Update");
                req.setAttribute("settledid",req.getParameter("settledid"));
                req.setAttribute("reportid",req.getParameter("reportid"));
                req.setAttribute("payoutid",req.getParameter("payout_id"));
                RequestDispatcher rd = req.getRequestDispatcher("/payoutDetailsUpdate.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else if(action.equalsIgnoreCase("sendPdfFilePayout"))
            {
                String exportPath =null;// ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
                try
                {
                    conn= Database.getRDBConnection();
                    String qry="SELECT swift_upload FROM merchant_payout_details WHERE payout_id = ? AND settledid=?";
                    preparedStatement=conn.prepareStatement(qry);
                    preparedStatement.setString(1, req.getParameter("payoutId1"));
                    preparedStatement.setString(2, req.getParameter("settledid"));
                    resultSet=null;
                    log.debug("Download Query::::::::::::::" + preparedStatement);
                    resultSet=preparedStatement.executeQuery();
                    resultSet.next();
                    exportPath=resultSet.getString("swift_upload");
                    if(exportPath==null || exportPath.isEmpty())
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                        req.setAttribute("message",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/actionwiremanager.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(req, res);
                    }

                    File f = new File(PAYOUT_REPORT_FILE+exportPath);
                    String filename=f.getName();
                    log.debug("File Path::::::::::::::"+f);
                    if(filename==null || filename.isEmpty())
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                        req.setAttribute("message",errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/actionwiremanager.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(req, res);
                    }

                    sendFile(exportPath,filename,f,res);

                }
                catch (SystemError systemError)
                {
                    log.error("Error while fetching data from merchant payout detail",systemError);
                }
                catch (SQLException e)
                {
                    log.error("SQL error while fetching data from merchant payout details", e);
                }
                catch (Exception e)
                {
                    log.error("Error while fetching data from merchant payout details",e);
                }
            }
            /*else if(action.equals("") || wireId.equals(""))
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>Action Or mapping ID is not provided." + EOL + "</b></font></center>";
                req.setAttribute("message",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/actionwiremanager.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }*/
            else
            {
                if (action.equalsIgnoreCase("view"))
                {
                    data = getRecords(wireId);
                    payoutDetails = getPayOutDetails(wireId,payoutid);
                    req.setAttribute("readonly", "readonly");
                    req.setAttribute("action", action);
                    req.setAttribute("data", data);
                    req.setAttribute("payoutid", payoutid);
                    req.setAttribute("payoutDetails", payoutDetails);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionwiremanager.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if (action.equalsIgnoreCase("update"))
                {
                    data = getRecords(wireId);
                    payoutDetails = getPayOutDetails(wireId,payoutid);
                    req.setAttribute("readonly", "readonly");
                    if(functions.isValueNull(message)){
                        req.setAttribute("message", message);
                    }
                    req.setAttribute("action", action);
                    req.setAttribute("data", data);
                    req.setAttribute("payoutid", payoutid);
                    req.setAttribute("payoutDetails", payoutDetails);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionwiremanager.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                /*else if (action.equals("TWTupdate"))
                {
                    //System.out.println("inside else if PayoutUpdate");
                    Hashtable payOutDetailsUpdate = payoutDAO.getPayOutDetailsUpdatesettleid(req.getParameter("settledid"));
                    req.setAttribute("TWTupdatedetails",payOutDetailsUpdate);
                    req.setAttribute("isreadonly","Update");
                    req.setAttribute("settledid",req.getParameter("settledid"));
                    RequestDispatcher rd = req.getRequestDispatcher("/addTWTPayout.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }*/
                else if (functions.isValueNull(action) && action.equalsIgnoreCase("delete"))
                {
                    String ids = req.getParameter("ids");
                    String[] id=null;
                    List<String> idList = new ArrayList();
                    boolean isMarkForDelete=false;
                    id = ids.split(",");
                    if (functions.isValueNull(ids))
                    {
                        idList = Arrays.asList(id);
                    }
                    List<WireVO> wireVOs=null;
                    for (String Id : idList)
                    {
                        wireVOs = PayoutDAO.getMerchantWirelistBySettledid(Id);


                        for (WireVO wireVO : wireVOs)
                        {
                            String wireid = wireVO.getSettleId();
                            String terminalid = wireVO.getTerminalId();
                            String settlementCycleno = wireVO.getSettlementCycleNo();

                            isMarkForDelete = deletePayoutReport(wireid, terminalid, settlementCycleno);

                        }
                    }
                    if (isMarkForDelete)
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>Record Deletion successful." + EOL + "</b></font></center>";
                    }
                    else
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>Record Deletion Failed." + EOL + "</b></font></center>";
                    }
                    req.setAttribute("message", errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/reverseReport.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if (action.equalsIgnoreCase("conform"))
                {
                    boolean setRR = setRollingReserveWire(wireId);
                    if (setRR)
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>Rolling Reserve Update successful." + EOL + "</b></font></center>";
                    }
                    else
                    {
                        errormsg += "<center><font class=\"text\" face=\"arial\"><b>Rolling Reserve Update failed." + EOL + "</b></font></center>";
                    }
                    req.setAttribute("message", errormsg);
                    RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                else if (action.equalsIgnoreCase("sendExcelFile"))
                {
                    String exportPath = null;// ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
                    try
                    {
                        conn = Database.getRDBConnection();
                        String qry = "SELECT settledtransactionfilepath FROM merchant_wiremanager WHERE settledid=?";
                        preparedStatement = conn.prepareStatement(qry);
                        preparedStatement.setString(1, wireId);
                        ResultSet rs = null;
                        rs = preparedStatement.executeQuery();
                        rs.next();
                        exportPath = rs.getString("settledtransactionfilepath");
                        if (exportPath == null || exportPath.isEmpty())
                        {
                            errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                            req.setAttribute("message", errormsg);
                            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                        }

                        File f = new File(SETTLEMENT_FILE_PATH + exportPath);
                        String filename = f.getName();
                        if (filename == null || filename.isEmpty())
                        {
                            errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                            req.setAttribute("message", errormsg);
                            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                        }

                        sendFile(exportPath, filename, f, res);

                    }
                    catch (SystemError systemError)
                    {
                        log.error("Error while fetching data from wiremanager", systemError);
                    }
                    catch (SQLException e)
                    {
                        log.error("SQL error while fetching data from wiremanager", e);
                    }
                    catch (Exception e)
                    {
                        log.error("Error while fetching data from wiremanager", e);
                    }


                }
                else if (action.equalsIgnoreCase("sendPdfFile"))
                {
                    String exportPath = null;// ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
                    try
                    {
                        conn = Database.getRDBConnection();
                        String qry = "SELECT settlementreportfilepath FROM merchant_wiremanager WHERE settledid=?";
                        preparedStatement = conn.prepareStatement(qry);
                        preparedStatement.setString(1, wireId);
                        resultSet = null;
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        exportPath = resultSet.getString("settlementreportfilepath");
                        if (exportPath == null || exportPath.isEmpty())
                        {
                            errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                            req.setAttribute("message", errormsg);
                            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                        }

                        File f = new File(PAYOUT_REPORT_FILE_PATH + exportPath);
                        String filename = f.getName();
                        if (filename == null || filename.isEmpty())
                        {
                            errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                            req.setAttribute("message", errormsg);
                            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                        }

                        sendFile(exportPath, filename, f, res);

                    }
                    catch (SystemError systemError)
                    {
                        log.error("Error while fetching data from wiremanager", systemError);
                    }
                    catch (SQLException e)
                    {
                        log.error("SQL error while fetching data from wiremanager", e);
                    }
                    catch (Exception e)
                    {
                        log.error("Error while fetching data from wiremanager", e);
                    }
                }
                else if (action.equalsIgnoreCase("sendRollingReserveFile"))
                {
                    String exportPath = null;
                    try
                    {
                        conn = Database.getRDBConnection();
                        String qry = "SELECT rollingReserveFilePath FROM merchant_wiremanager WHERE settledid=?";
                        preparedStatement = conn.prepareStatement(qry);
                        preparedStatement.setString(1, wireId);
                        resultSet = null;
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        exportPath = resultSet.getString("rollingReserveFilePath");
                        if (exportPath == null || exportPath.isEmpty())
                        {
                            errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                            req.setAttribute("message", errormsg);
                            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                        }

                        File f = new File(SETTLEMENT_FILE_PATH + exportPath);
                        String filename = f.getName();
                        if (filename == null || filename.isEmpty())
                        {
                            errormsg += "<center><font class=\"text\" face=\"arial\"><b>File Is Not Available." + EOL + "</b></font></center>";
                            req.setAttribute("message", errormsg);
                            RequestDispatcher rd = req.getRequestDispatcher("/reverseReport.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(req, res);
                        }

                        sendFile(exportPath, filename, f, res);

                    }
                    catch (SystemError systemError)
                    {
                        log.error("Error while fetching data from wiremanager", systemError);
                    }
                    catch (SQLException e)
                    {
                        log.error("SQL error while fetching data from wiremanager", e);
                    }
                    catch (Exception e)
                    {
                        log.error("Error while fetching data from wiremanager", e);
                    }
                }
            }
            }
        }
        catch (Exception e)
        {
            log.error("main exception ::",e);
        }
        finally {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
    }

    public Hashtable getRecords(String mappingid)
    {
        Hashtable wireData=new Hashtable();
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection= Database.getRDBConnection();
            String qry="SELECT settledid,settledate,firstdate,lastdate,amount,balanceamount,netfinalamount,unpaidamount,currency,status,settlementreportfilepath,settledtransactionfilepath,markedfordeletion,TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,payoutCurrency,payoutAmount,payerBankDetails,receiverBankDetails,paymentConfirmation,paymentReceiptDate,remark,conversionRate,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationdate',payoutid,reportid FROM merchant_wiremanager WHERE settledid=?";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,mappingid);
            wireData=Database.getHashFromResultSet(preparedStatement.executeQuery());
        }
        catch (SystemError systemError)
        {
            log.error("Error while fetching data from merchant_wiremanager",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL error while fetching data from merchant_wiremanager", e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return wireData;
    }

    // Create Method For Display PayOutDetails

   public Hashtable getPayOutDetails(String settledid,String payoutid)
   {
       Hashtable payoutDetails=new Hashtable();
       Connection connection=null;
       PreparedStatement preparedStatement=null;
       Functions function = new Functions();
       ResultSet rs=null;
       String qry="";
       try
       {
           connection= Database.getRDBConnection();
           if(function.isValueNull(payoutid)){
               qry = "SELECT payout_id, settledid, payout_date, payout_currency, conversion_rate, payout_amount, beneficiary_bank_details, remitter_bank_details, remarks, swift_message,swift_upload,payment_receipt_date,payment_receipt_confirmation FROM merchant_payout_details WHERE payout_id = "+payoutid;

           }else
           {
                qry = "SELECT payout_id, settledid, payout_date, payout_currency, conversion_rate, payout_amount, beneficiary_bank_details, remitter_bank_details, remarks, swift_message,swift_upload,payment_receipt_date,payment_receipt_confirmation FROM merchant_payout_details WHERE settledid ="+settledid;
           }
           //String countqry="SELECT count(*) as total FROM merchant_payout_details where settledid = ?";
           preparedStatement=connection.prepareStatement(qry);
           payoutDetails=Database.getHashFromResultSet(preparedStatement.executeQuery());
           //System.out.println("Seelect Query Payout Details Method:::::::::"+preparedStatement);

       }
       catch (SystemError systemError)
       {
           log.error("Error while fetching data from merchant_payout_details",systemError);
       }
       catch (SQLException e)
       {
           log.error("SQL error while fetching data from merchant_payout_details", e);
       }
       finally {
           Database.closePreparedStatement(preparedStatement);
           Database.closeConnection(connection);
       }
       return payoutDetails;
    }


    public boolean setMarkForDelete(String mappingid)
    {
        boolean flag=false;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection= Database.getConnection();
            String qry="DELETE member_settlementcycle_details, bank_merchant_settlement_master,merchant_wiremanager\n" +
                    "FROM member_settlementcycle_details JOIN bank_merchant_settlement_master JOIN merchant_wiremanager\n" +
                    "WHERE member_settlementcycle_details.`cycleid`= bank_merchant_settlement_master.`cycleid`\n" +
                    "AND merchant_wiremanager.`settledid`=bank_merchant_settlement_master.`cycleid`\n" +
                    "AND member_settlementcycle_details.cycleid=?";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,mappingid);
            int i=preparedStatement.executeUpdate();
            if(i==1)
            {
                flag=true;
            }
        }
        catch (SystemError systemError)
        {
            log.error("Error while fetching data from merchant_wiremanager",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL error while fetching data from merchant_wiremanager", e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }
    public boolean deletePayoutReport(String mappingid,String terminalId,String settlementCycleNO)
    {
        boolean flag=false;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection= Database.getConnection();
            String qry="DELETE FROM bank_merchant_settlement_master WHERE cycleid=? AND terminalId=? ";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,settlementCycleNO);
            preparedStatement.setString(2,terminalId);
            int i=preparedStatement.executeUpdate();
            log.error("Query:::::"+preparedStatement);
            log.error("i:::::"+i);

            if(i==1)
            {
                String qry1 = "DELETE FROM merchant_wiremanager WHERE settledid=? AND terminalid=? AND settlementcycle_no=?";
                String qry2 = "DELETE FROM member_settlementcycle_details WHERE cycleid=? AND terminalid=?";
                preparedStatement = connection.prepareStatement(qry1);
                preparedStatement.setString(1, mappingid);
                preparedStatement.setString(2, terminalId);
                preparedStatement.setString(3, settlementCycleNO);
                int j = preparedStatement.executeUpdate();
                log.error("Query1:::::" + preparedStatement);
                log.error("j:::::" + j);

                preparedStatement = connection.prepareStatement(qry2);
                preparedStatement.setString(1, settlementCycleNO);
                preparedStatement.setString(2, terminalId);
                int k = preparedStatement.executeUpdate();
                log.error("Query2:::::" + preparedStatement);
                log.error("j:::::" + k);

                flag=true;
            }
            else
            {
                log.error("Something is wrong while fetching details from all the tables.");
                return flag;
            }
        }
        catch (SystemError systemError)
        {
            log.error("Error while fetching data from merchant_wiremanager",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL error while fetching data from merchant_wiremanager", e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }

    public boolean setRollingReserveWire(String mappingid)
    {
        boolean flag=false;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        try
        {
            connection= Database.getConnection();
            String qry="UPDATE merchant_wiremanager SET isrollingreserveincluded='Y' WHERE settledid=?";
            preparedStatement=connection.prepareStatement(qry);
            preparedStatement.setString(1,mappingid);
            int i=preparedStatement.executeUpdate();
            if(i==1)
            {
                flag=true;
            }
        }
        catch (SystemError systemError)
        {
            log.error("Error while fetching data from wiremanager",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL error while fetching data from wiremanager", e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(connection);
        }
        return flag;
    }
}