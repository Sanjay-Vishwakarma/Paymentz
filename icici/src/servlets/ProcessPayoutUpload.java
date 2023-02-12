package servlets;

import com.directi.pg.*;
import com.manager.PaymentManager;
import com.manager.TerminalManager;
import com.manager.TransactionManager;
import com.manager.dao.PartnerDAO;
import com.manager.dao.TransactionDAO;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionVO;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.common.core.CommResponseVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Created by Admin on 8/31/2020.
 */
public class ProcessPayoutUpload
{
    private static Logger log = new Logger(ProcessPayoutUpload.class.getName());

    public StringBuilder uploadPayout(String fullFileName, StringBuilder sSuccessMessage, StringBuilder sErrorMessage ,String actionExecutorId , String actionExecutorName, String terminalid , String memberid )
    {
        try
        {
            Functions functions = new Functions();
            List<TransactionVO> vTransactions = null;
            FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
            TransactionDAO transactionDAO = new TransactionDAO();
            StringBuffer stringBuffer=new StringBuffer();
            PaymentManager paymentManager = new PaymentManager();
            TerminalVO terminalVO=null;
            TerminalManager terminalManager=new TerminalManager();
            CommResponseVO responseVO =null;
            AuditTrailVO auditTrailVO = null;
            PartnerDAO partner = new PartnerDAO();

            String paymentId="";
            String fullname="";
            String bankaccount="";
            String ifsccode="";
            String amount="";
            int actionEntry=0;
            int trackingId;
            int i = 0;
            int record = 0;

            vTransactions = fileHandlingUtil.readPayoutUpload(fullFileName);
            fileHandlingUtil.deleteFile(fullFileName);
            for(TransactionVO transactionVO1 : vTransactions)
            {
                i=i+1;
                if (functions.isValueNull(transactionVO1.getPaymentId()))
                    paymentId = transactionVO1.getPaymentId();

                stringBuffer.append(paymentId);
                if(vTransactions.size()>i){
                    stringBuffer.append(",");
                }
            }
            TransactionManager transactionManager = new TransactionManager();
            String getTrackingID = "";
            for (TransactionVO transactionVO : vTransactions)
            {
                if (transactionVO != null)
                {
                     paymentId="";
                     fullname="";
                     bankaccount="";
                     ifsccode="";
                     amount="";
                     trackingId=0;
                    actionEntry=0;
                    responseVO = new CommResponseVO();
                    auditTrailVO = new AuditTrailVO();

                    if (functions.isValueNull(transactionVO.getPaymentId()))
                        paymentId = transactionVO.getPaymentId();
                    if (functions.isValueNull(transactionVO.getFullname()))
                        fullname = transactionVO.getFullname();
                    if (functions.isValueNull(transactionVO.getBankaccount()))
                        bankaccount = transactionVO.getBankaccount();
                    if (functions.isValueNull(transactionVO.getIFSCCode()))
                        ifsccode = transactionVO.getIFSCCode();
                    if (functions.isValueNull(transactionVO.getAmount()))
                        amount = transactionVO.getAmount();

                    String checkpaymentid = checkEntryPresent(paymentId);
                    if(checkpaymentid.equals("false"))
                    {
                        if (functions.isValueNull(memberid) && functions.isValueNull(terminalid))
                        {
                            terminalVO = terminalManager.getTerminalMerchantsInFo(memberid, terminalid);

                            if (terminalVO != null)
                            {
                                String status = ActionEntry.STATUS_PAYOUT_STARTED;
                                responseVO.setStatus(status);
                                auditTrailVO.setActionExecutorId(actionExecutorId);
                                auditTrailVO.setActionExecutorName(actionExecutorName);
                                responseVO.setFullname(fullname);
                                responseVO.setBankaccount(bankaccount);
                                responseVO.setIfsc(ifsccode);
                                responseVO.setCurrency(terminalVO.getCurrency());

                                trackingId = paymentManager.insertTransactionCommonForPayoutSuccess(memberid, partner.getPartnerName(terminalVO.getPartnerId()), terminalVO.getGwMid(), terminalVO.getGateway(), "", "", amount, "",
                                        terminalVO.getAccountId(), Integer.parseInt(terminalVO.getPaymodeId()), Integer.parseInt(terminalVO.getCardTypeId()), terminalVO.getCurrency(), "", "", ActionEntry.STATUS_PAYOUT_SUCCESSFUL, terminalid, "", "", "",
                                        "", "", "", "", "", "", "", "", "", "", "0.00", "", "", "", ActionEntry.ACTION_PAYOUT_SUCCESSFUL, terminalVO.getCardType(), paymentId);

                                if (trackingId > 0)
                                {
                                    AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(trackingId, Integer.parseInt(terminalVO.getAccountId()));

                                    actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_PAYOUT_STARTED, ActionEntry.STATUS_PAYOUT_STARTED, responseVO, null, auditTrailVO, "");
                                    paymentManager.addBinDetailsEntry(String.valueOf(trackingId), terminalVO.getAccountId(), "", "", "", "", "", "", "");

                                    responseVO.setTransactionId(paymentId);
                                    responseVO.setStatus(ActionEntry.STATUS_PAYOUT_SUCCESSFUL);
                                    responseVO.setTransactionType("payout");


                                    if (actionEntry > 0)
                                    {
                                        actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, responseVO, null, auditTrailVO, "Payout Created Successfully");
                                        record = record + 1;
                                    }
                                }
                            }
                            else
                            {
                                sErrorMessage.append("<b>  Invalid Member Terminal Mapping </b><BR>");
                                break;
                            }
                        }
                        else
                        {
                            sErrorMessage.append("<b>  Please provide required data </b><BR>");
                            break;
                        }
                    }
                }
                else
                    break;
            }
            if(record > 0)
            {
                sSuccessMessage.append("<tr class=\"tdstyle texthead\"><td style=\"text-align: center; width:300px\">  Status </td><td style=\"text-align: center; width:300px\"> Updated Record Count </td></tr>");
                sSuccessMessage.append("<tr class='report'><td style=\"text-align: center; width:300px\"> " + ActionEntry.STATUS_PAYOUT_SUCCESSFUL + " </td><td style=\"text-align: center;width:300px\">" + record + "</td></tr>");
            }else if(record == 0){
                sErrorMessage.append("<b> " + record +" Record Updated </b>");
            }
        }
        catch (Exception e)
        {
            log.error("processpayoutupload---------->",e);
            log.error("File---->"+fullFileName);
            FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
            fileHandlingUtil.deleteFile(fullFileName);
            sErrorMessage.append("<b>Invalid File Content.</b>");
        }
        StringBuilder Message = new StringBuilder();
        Message.append(sSuccessMessage.toString());
        Message.append("<BR/>");
        Message.append(sErrorMessage.toString());
        return Message;
    }

    public String checkEntryPresent(String paymetnId){
        String isPresent = "false";
        Connection con=null;
        Functions function = new Functions();

        try
        {
            if(function.isValueNull(paymetnId)){
            con = Database.getConnection();
            String query = "select paymentid from transaction_common where paymentid = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, paymetnId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                isPresent = "true";
            }
        }
        else{
                isPresent="false";
            }
        }catch(Exception e){
            log.error(e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isPresent;
    }
}