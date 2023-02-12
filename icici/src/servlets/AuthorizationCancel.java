import com.directi.pg.*;
import com.logicboxes.util.Util;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.request.PZCancelRequest;
import com.payment.response.PZCancelResponse;
import com.payment.response.PZResponseStatus;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Jul 6, 2013
 * Time: 5:56:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationCancel extends HttpServlet
{
    private static final int MAX_CAPTURE_TRANSACTIONS = 10;
    private static Logger logger = new Logger(AuthorizationCancel.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter pWriter = response.getWriter();
        response.setContentType("text/plain");
        ServletContext ctx = getServletContext();
        String memberid = request.getParameter("toid");
        String trackingIds = request.getParameter("trackingid");//comma seperated trckingid
        String cancelReasons = request.getParameter("reason"); //comma seperated reasons for cancellation
        String checksum = request.getParameter("checksum");
        String ipAddress = Functions.getIpAddress(request);
        StringBuffer status = new StringBuffer("");
        StringBuffer statusMsg = new StringBuffer("");
        String key = "";
        String checksumAlgorithm = "";
        String icicimerchantid = "";
        String description = "";

        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        if (memberid == null || Functions.convertStringtoInt(memberid, 0) == 0)
        {
            status.append("N");
            statusMsg.append("Invalid toid");
            calCheckSumAndWriteStatus(pWriter, "", "", status, statusMsg, key, checksumAlgorithm);
            return;
        }
        if (!Functions.isValidSQL(trackingIds) || !Functions.isValidSQL(cancelReasons))
        {
            status.append("N");
            statusMsg.append("Invalid data Received");
            calCheckSumAndWriteStatus(pWriter, "", "", status, statusMsg, key, checksumAlgorithm);
            return;
        }

        try
        {
            conn = Database.getConnection();
            String s1 = "select clkey,checksumalgo from members where memberid=?";
            preparedStatement = conn.prepareStatement(s1);
            preparedStatement.setString(1, memberid);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                key = resultSet.getString(1);
                checksumAlgorithm = resultSet.getString(2);
            }
            else
            {
                status.append("N");
                statusMsg.append("Merchant not found");
                calCheckSumAndWriteStatus(pWriter, "", "", status, statusMsg, key, checksumAlgorithm);
                return;
            }
            if (Functions.verifyChecksumV2(memberid, trackingIds, cancelReasons, checksum, key, checksumAlgorithm))
            {
                String trackingIdArray[] = trackingIds.split(",");
                if (trackingIdArray.length > MAX_CAPTURE_TRANSACTIONS)
                {
                    status.append("N");
                    statusMsg.append("Please do not send more than " + MAX_CAPTURE_TRANSACTIONS + " transactions in single cancel request");
                    calCheckSumAndWriteStatus(pWriter, "", "", status, statusMsg, key, checksumAlgorithm);
                    return;
                }
                String cancelReasonArray[] = cancelReasons.split(",");
                if (cancelReasonArray.length != trackingIdArray.length)
                {
                    status.append("N");
                    statusMsg.append("The number of cancel reasons sent should match the number of trackingIds");
                    calCheckSumAndWriteStatus(pWriter, "", "", status, statusMsg, key, checksumAlgorithm);
                    return;
                }

                TransactionEntry transactionEntry = new TransactionEntry();
                StringBuffer statusBuffer = new StringBuffer("");
                StringBuffer statusMessageBuffer = new StringBuffer("");
                for (int i = 0; i < trackingIdArray.length; i++)
                {
                    String stat = "";
                    String statMessage = "";
                    String trackingId = trackingIdArray[i];
                    String cancelReason = cancelReasonArray[i];
                    String authTransaction = "select T.*,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge from transaction_common as T,members as M where toid=? and T.toid=M.memberid and trackingid = ? and status='authsuccessful'  order by trackingid asc";
                    PreparedStatement authTransPreparedStatement = conn.prepareStatement(authTransaction);
                    authTransPreparedStatement.setString(1, memberid);
                    authTransPreparedStatement.setString(2, trackingId);
                    ResultSet rsAuthtransaction = authTransPreparedStatement.executeQuery();
                    if (rsAuthtransaction.next())
                    {
                        String accountId = rsAuthtransaction.getString("accountid");
                        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accountId));
                        PZCancelRequest cancelRequest = new PZCancelRequest();
                        cancelRequest.setAccountId(Integer.valueOf(accountId));
                        cancelRequest.setMemberId(Integer.valueOf(memberid));
                        cancelRequest.setTrackingId(Integer.valueOf(trackingId));
                        cancelRequest.setIpAddress(ipAddress);
                        cancelRequest.setCancelReason(cancelReason);

                        //newly added
                        AuditTrailVO auditTrailVO = new AuditTrailVO();
                        auditTrailVO.setActionExecutorId(memberid);
                        auditTrailVO.setActionExecutorName("Admin Cancel");
                        cancelRequest.setAuditTrailVO(auditTrailVO);

                        PZCancelResponse cancelResponse = paymentProcess.cancel(cancelRequest);
                        PZResponseStatus responseStatus = cancelResponse.getStatus();
                        String cancelDescription = cancelResponse.getResponseDesceiption();

                        if (PZResponseStatus.SUCCESS.equals(responseStatus))
                        {
                            stat = "Y";
                        }
                        else
                        {
                            stat = "N";
                        }
                        statMessage = cancelDescription;
                    }
                    else
                    {
                        stat = "N";
                        statMessage = "Transaction not found";
                    }
                    statusBuffer.append(stat).append(",");
                    statusMessageBuffer.append(statMessage).append(",");
                }
                statusBuffer.deleteCharAt(statusBuffer.length() - 1);
                statusMessageBuffer.deleteCharAt(statusMessageBuffer.length() - 1);
                transactionEntry.closeConnection();
                calCheckSumAndWriteStatus(pWriter, trackingIds, cancelReasons, statusBuffer, statusMessageBuffer, key, checksumAlgorithm);
            }
            else
            {
                ctx.log("checksum Mismatch ");
                status.append("N");
                statusMsg.append("Illegal Access. CheckSum mismatch");
                calCheckSumAndWriteStatus(pWriter, "", "", status, statusMsg, key, checksumAlgorithm);
            }
        }
        catch (Exception ex)
        {
            ctx.log("Error " + Util.getStackTrace(ex));
            status.append("N");
            statusMsg.append("System Error");
            calCheckSumAndWriteStatus(pWriter, "", "", status, statusMsg, key, checksumAlgorithm);
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
    }

    private void calCheckSumAndWriteStatus(PrintWriter pWriter, String description, String amount, StringBuffer status, StringBuffer statusMsg, String key, String checksumAlgo)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumV2(description, amount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status.append("N");
            statusMsg.append(e.getMessage());
        }
        pWriter.write(description + ":" + status + ":" + statusMsg + ":" + amount + ":" + checkSum);
    }
}
