package net.partner;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.SettlementManager;
import com.manager.dao.BankDao;
import com.manager.vo.ActionVO;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.SettlementDateVO;
import com.manager.vo.payoutVOs.SettlementCycleVO;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.arenaplus.core.ArenaPlusPaymentProcess;
import com.payment.borgun.core.BorgunPaymentProcess;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZSettlementFile;
import com.payment.response.PZSettlementRecord;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Sandip on 2/13/2018.
 */
public class ProcessSettlement extends HttpServlet
{
    private static Logger logger = new Logger(ProcessSettlement.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        SettlementManager settlementManager = new SettlementManager();
        String action = request.getParameter("action");
        if ("next".equalsIgnoreCase(action))
        {
            //Reading the values from session-bankWireId,accountId,settlementCycleId,settlementCycleDateRangeVO,settlementFileDateRangeVO

            String bankWireId = (String) session.getAttribute("bankWireId");
            String accountId = (String) session.getAttribute("accountid");
            //String partnerId = (String) session.getAttribute("merchantid");
            String settlementCycleId = (String) session.getAttribute("settlementCycleId");
            SettlementDateVO settlementCycleDateRangeVO = (SettlementDateVO) session.getAttribute("settlementDateVO1");
            SettlementDateVO settlementFileDateRangeVO = (SettlementDateVO) session.getAttribute("settlementDateVO2");
            SettlementCycleVO settlementCycleVO = (SettlementCycleVO) session.getAttribute("settlementCycleVO");
            String partnerId=settlementCycleVO.getPartnerId();
            logger.error("PartnerId in ProcessSettlement:::"+partnerId);

            BankDao bankDao = new BankDao();

           /*Getting bank settlement file details*/
            ActionVO actionVO = new ActionVO();
            actionVO.setActionCriteria(bankWireId);
            BankWireManagerVO bankWireManagerVO = bankDao.getSingleBankWireManagerActionSpecific(actionVO);

            String isTransactionFileAvailable = settlementCycleVO.getIsTransactionFileAvailable();

            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorId((String) session.getAttribute("merchantid"));
            //auditTrailVO.setActionExecutorName("Partner");
            String role = "";
            for (String s:user.getRoles())
            {
                role += s;
            }
            auditTrailVO.setActionExecutorName(role+"-"+session.getAttribute("username").toString());

            try
            {
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
                String tableName = Database.getTableNameForSettlement(gatewayType.getGateway());

                List<PZSettlementRecord> transactionsList = null;
                String errorMsg = "";

                AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(null, Integer.parseInt(accountId));
                if ("Y".equalsIgnoreCase(isTransactionFileAvailable))
                {
                    String fullFileName = ApplicationProperties.getProperty("MPR_FILE_STORE") + bankWireManagerVO.getBanksettlement_transaction_file();
                    logger.debug("fullFileName:::::" + fullFileName);

                    PZSettlementFile pzSettlementFile = new PZSettlementFile();
                    pzSettlementFile.setFilepath(fullFileName);
                    pzSettlementFile.setAccountId(Integer.parseInt(accountId));

                    /*Reading settlement file*/
                    transactionsList = process.readSettlementFile(pzSettlementFile);
                }
                else
                {
                    /*read all the transactions from database for given date range*/
                    transactionsList = settlementManager.getSettlementTransactions3(gatewayAccount, settlementFileDateRangeVO.getSettlementStartDate(), settlementFileDateRangeVO.getSettlementEndDate(), tableName);
                }

                if (transactionsList != null && transactionsList.size() > 0)
                {
                    /*settle the transaction into system*/
                    if (process instanceof ArenaPlusPaymentProcess)
                    {
                        ArenaPlusPaymentProcess aProcess = new ArenaPlusPaymentProcess();
                        errorMsg = aProcess.processSettlement(Integer.parseInt(accountId), transactionsList, process.getAdminEmailAddress(), auditTrailVO,tableName);
                    }
                    else if (process instanceof BorgunPaymentProcess)
                    {
                        BorgunPaymentProcess bProcess = new BorgunPaymentProcess();
                        errorMsg = bProcess.processSettlement(Integer.parseInt(accountId), transactionsList, process.getAdminEmailAddress(), auditTrailVO,tableName);
                    }
                    else
                    {
                        CommonPaymentProcess proc = new CommonPaymentProcess();
                        errorMsg = proc.processSettlement(Integer.parseInt(accountId), transactionsList, process.getAdminEmailAddress(), auditTrailVO,tableName);
                    }

                   /*take the appropriate action when settlement not uploaded successfully*/
                    logger.debug("Settlement Uploaded ");
                    logger.debug("settlement upload result:" + errorMsg);

                   /*Set the isSettlementCronExecuted='Y'*/
                    bankDao.updateSettlementCronExecutedFlag(bankWireId);
                    bankDao.updateTheStatus(settlementCycleId, "SettlementUploaded");

                    Hashtable hashTable2 = null;
                    if ("Y".equalsIgnoreCase(isTransactionFileAvailable))
                    {
                        /*Settlement File Date Range and Settlement File Data*/
                        hashTable2 = settlementManager.getGatewayAccountProcessingAmountFromTempTable(settlementFileDateRangeVO.getSettlementStartDate(), settlementFileDateRangeVO.getSettlementEndDate(), settlementCycleId);
                    }
                    else
                    {
                        /*Settlement Cycle Date Range & PZ DB Data*/
                        hashTable2 = settlementManager.getSettlementTransactions1(gatewayAccount, settlementCycleDateRangeVO.getSettlementStartDate(), settlementCycleDateRangeVO.getSettlementEndDate(), tableName, partnerId);
                     /*read the transaction details from temp table*/
                    }
                    //Settlement Cycle Date Range & PZ DB Data
                    Hashtable hashTable1 = settlementManager.getGatewayAccountProcessingAmount(gatewayAccount, settlementCycleDateRangeVO.getSettlementStartDate(), settlementCycleDateRangeVO.getSettlementEndDate(), tableName, partnerId);

                    request.setAttribute("hashTable1", hashTable1);
                    request.setAttribute("hashTable2", hashTable2);

                    request.setAttribute("settlementDateVO1", settlementCycleDateRangeVO);
                    request.setAttribute("settlementDateVO2", settlementFileDateRangeVO);
                }
                else
                {
                    errorMsg = "Transactions not found in settlement file";
                }
                logger.debug("errorMsg:::::" + errorMsg);
                RequestDispatcher rd = request.getRequestDispatcher("/batchingStep2Recon.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;

            }
            catch (Exception dve)
            {
                logger.error("Exception---" +dve);
                /*take an appropriate action when exception occurred*/
            }
        }
        else if ("back".equalsIgnoreCase(action))
        {
            //Reading the values from session-bankWireId,accountId,settlementCycleId,settlementCycleDateRangeVO,settlementFileDateRangeVO
            String bankWireId = (String) session.getAttribute("bankWireId");
            String accountId = (String) session.getAttribute("accountid");
            String settlementCycleId = (String) session.getAttribute("settlementCycleId");

            BankDao bankDao = new BankDao();

           /*Getting bank settlement file details*/
            ActionVO actionVO = new ActionVO();
            actionVO.setActionCriteria(bankWireId);
            BankWireManagerVO bankWireManagerVO = bankDao.getSingleBankWireManagerActionSpecific(actionVO);

            String fullFileName = ApplicationProperties.getProperty("MPR_FILE_STORE") + bankWireManagerVO.getBanksettlement_transaction_file();
            logger.debug("fullFileName:::::" + fullFileName);

            /*actions to be taken*/
            /*step1:remove the transactions form temp_settlement_upload*/
            settlementManager.removeTempTransactions(settlementCycleId);

            /*remove the uploaded file*/
            File file = new File(fullFileName);
            file.delete();

            session.setAttribute("bankWireManagerVO", bankWireManagerVO);
             /*3)remove the bank wire*/
            bankDao.removeTheBankWire(bankWireId);

            RequestDispatcher rd = request.getRequestDispatcher("/addBankWire.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else if ("continue".equalsIgnoreCase(action))
        {
            String pendingSettlementCycleId = request.getParameter("settlementcycleid");
            String partnerId=null;
            try
            {
                SettlementCycleVO settlementCycleVO = settlementManager.getSettlementCycleInfo(pendingSettlementCycleId, "SettlementUploaded");
                partnerId=settlementCycleVO.getPartnerId();
                if (settlementCycleVO == null)
                {
                    /*take an appropriate action*/
                }

                BankDao bankDao = new BankDao();
                BankWireManagerVO bankWireManagerVO = bankDao.getSingleBankWireManagerActionSpecificBasedOnSettlementCycle(pendingSettlementCycleId);
                if (bankWireManagerVO != null)
                {
                   /*need to take action when bank wire not founds*/
                }

                SettlementDateVO settlementCycleDateRangeVO = new SettlementDateVO();
                settlementCycleDateRangeVO.setSettlementStartDate(settlementCycleVO.getStartDate());
                settlementCycleDateRangeVO.setSettlementEndDate(settlementCycleVO.getEndDate());

                /*fetch the transaction details after the settlement upload*/
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(bankWireManagerVO.getAccountId());
                GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
                String tableName = Database.getTableNameForSettlement(gatewayType.getGateway());

                String isTransactionFileAvailable = settlementCycleVO.getIsTransactionFileAvailable();
                SettlementDateVO settlementFileDateRangeVO = null;
                Hashtable hashTable2 = null;
                if ("Y".equalsIgnoreCase(isTransactionFileAvailable))
                {
                    settlementFileDateRangeVO = settlementManager.getSettlementPeriodFromExcel(pendingSettlementCycleId);
                    //Settlement File Date Range and Settlement File Data
                    hashTable2 = settlementManager.getGatewayAccountProcessingAmountFromTempTable(settlementFileDateRangeVO.getSettlementStartDate(), settlementFileDateRangeVO.getSettlementEndDate(), pendingSettlementCycleId);

                }
                else
                {
                    settlementFileDateRangeVO = new SettlementDateVO();
                    settlementFileDateRangeVO.setSettlementStartDate(settlementCycleVO.getStartDate());
                    settlementFileDateRangeVO.setSettlementEndDate(settlementCycleVO.getEndDate());

                     /*Settlement Cycle Date Range & PZ DB Data*/
                    hashTable2 = settlementManager.getSettlementTransactions1(gatewayAccount, settlementCycleDateRangeVO.getSettlementStartDate(), settlementCycleDateRangeVO.getSettlementEndDate(), tableName, partnerId);
                }

                //Settlement Cycle Date Range & PZ DB Data
                Hashtable hashTable1 = settlementManager.getGatewayAccountProcessingAmount(gatewayAccount, settlementCycleDateRangeVO.getSettlementStartDate(), settlementCycleDateRangeVO.getSettlementEndDate(), tableName, partnerId);

                request.setAttribute("hashTable1", hashTable1);
                request.setAttribute("hashTable2", hashTable2);

                request.setAttribute("settlementDateVO1", settlementCycleDateRangeVO);
                request.setAttribute("settlementDateVO2", settlementFileDateRangeVO);

                session.setAttribute("bankWireId", bankWireManagerVO.getBankwiremanagerId());
                session.setAttribute("settlementCycleId", pendingSettlementCycleId);

                RequestDispatcher rd = request.getRequestDispatcher("/batchingStep2Recon.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            catch (Exception e)
            {
                /*take appropriate action*/
                logger.debug("Exception:::::" + e);
            }
        }
    }
}
