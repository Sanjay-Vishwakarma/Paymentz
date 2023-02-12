package servlets;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.TransactionManager;
import com.manager.dao.TransactionDAO;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.TransactionVO;
import java.util.List;

/**
 * Created by Namrata Bari on 6/8/2020.
 */
public class ProcessUploadReconTxn
{
    private static Logger log = new Logger(ProcessUploadReconTxn.class.getName());

    public StringBuilder uploadReconTxn(String fullFileName, StringBuilder sSuccessMessage, StringBuilder sErrorMessage ,String actionExecutorId , String actionExecutorName )
    {
        try
        {
            Functions functions = new Functions();
            List<TransactionVO> vTransactions = null;
            FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
            TransactionDAO transactionDAO = new TransactionDAO();
            StringBuffer stringBuffer=new StringBuffer();
            String trackingId = "";
            String paymentId = "";
            String status = "";
            String remark = "";
            int i = 0;
            int record = 0;

            vTransactions = fileHandlingUtil.readReconTxn(fullFileName);
            fileHandlingUtil.deleteFile(fullFileName);
            for(TransactionVO transactionVO1 : vTransactions)
            {
                i=i+1;
                if (functions.isValueNull(transactionVO1.getTrackingId()))
                    trackingId = transactionVO1.getTrackingId();

                stringBuffer.append(trackingId);
                if(vTransactions.size()>i){
                    stringBuffer.append(",");
                }
            }
            TransactionManager transactionManager = new TransactionManager();
            boolean isAuthstarted = false;
            for (TransactionVO transactionVO : vTransactions)
            {
                if (transactionVO != null)
                {
                    trackingId ="";
                    paymentId="";
                    status="";
                    remark="";
                    if (functions.isValueNull(transactionVO.getTrackingId()))
                        trackingId = transactionVO.getTrackingId();
                    if (functions.isValueNull(transactionVO.getPaymentId()))
                        paymentId = transactionVO.getPaymentId();
                    if (functions.isValueNull(transactionVO.getStatus()))
                        status = transactionVO.getStatus();
                    if (functions.isValueNull(transactionVO.getRemark()))
                        remark = transactionVO.getRemark();

                    if (functions.isValueNull(trackingId) && functions.isValueNull(status))
                    {
                        //isAuthstarted = transactionDAO.isAuthstarted(trackingId);
                    }
                    if (isAuthstarted == true && status.equalsIgnoreCase("authfailed"))
                    {

                        boolean isUpdated = transactionDAO.getUpdateStatus(trackingId, paymentId, remark, status);
                        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                        String amount = transactionDetailsVO.getAmount();
                        if (isUpdated == true)
                        {
                            i = i + 1;
                            stringBuffer.append(trackingId);
                            if (vTransactions.size() > i)
                            {
                                stringBuffer.append(",");
                            }
                            try
                            {

                                boolean inserted = transactionDAO.isTransactionDetailInserted(trackingId, status, amount, actionExecutorId, actionExecutorName, remark);

                                if(inserted == true)
                                {
                                    record = record + 1;
                                }
                            }
                            catch (Exception e)
                            {
                                sErrorMessage=new StringBuilder();
                                sErrorMessage.append("<b>Invalid File Content.</b>");
                            }
                        }
                    }
                }
                else
                    break;
            }
            if(record > 0)
            {
                sSuccessMessage.append("<tr class=\"tdstyle texthead\"><td>  Status </td><td> Updated Record Count </td></tr>");
                sSuccessMessage.append("<tr class='report'><td style=\"text-align: center;\">" + status + "  </td><td style=\"text-align: center;\">" + record + "</td></tr>");
            }else if(record == 0){
                sErrorMessage.append("<b> " + record +" Record Updated </b>");
            }

        }
        catch (Exception e)
        {
            sErrorMessage = new StringBuilder("File Not Found");
        }
        StringBuilder Message = new StringBuilder();
        Message.append(sSuccessMessage.toString());
        Message.append("<BR/>");
        Message.append(sErrorMessage.toString());
        return Message;
    }
}