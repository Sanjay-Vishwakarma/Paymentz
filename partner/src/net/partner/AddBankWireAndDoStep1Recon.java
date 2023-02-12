package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.fileupload.SettlementFileUpload;
import com.logicboxes.util.ApplicationProperties;
import com.manager.SettlementManager;
import com.manager.dao.BankDao;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.SettlementDateVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.payoutVOs.SettlementCycleVO;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.request.PZSettlementFile;
import com.payment.response.PZSettlementRecord;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Sandip on 2/13/2018.
 */
public class AddBankWireAndDoStep1Recon extends HttpServlet
{
    private static Logger logger = new Logger(AddBankWireAndDoStep1Recon.class.getName());

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

        String pathToStore = ApplicationProperties.getProperty("MPR_FILE_STORE");
        String pathToLog = ApplicationProperties.getProperty("LOG_STORE");

        SettlementManager settlementManager = new SettlementManager();
        PZSettlementFile PZSettlementFile = new PZSettlementFile();
        CommonFunctionUtil commonFunctionUtil1 = new CommonFunctionUtil();

        BankDao bankDao = new BankDao();
        Functions functions = new Functions();

        String errorMsg = "";
        String fullFileName = "";
        String action = "";

        try
        {

            SettlementFileUpload fileUpload = new SettlementFileUpload();
            fileUpload.setSavePath(pathToStore);
            fileUpload.setLogpath(pathToLog);

            HashMap<String, String> hashMap = fileUpload.getInputFields(request, null);
            String fileName = fileUpload.getFilename();

            if (hashMap.containsKey("action"))
            {
                action = hashMap.get("action");
            }
            else
            {
                action = request.getParameter("action");
            }

            logger.debug("action:" + action);
            logger.debug("fileUpload.getInputFields.hashMap:::::" + hashMap);


            fullFileName = ApplicationProperties.getProperty("MPR_FILE_STORE") + fileName;
            if ("next".equalsIgnoreCase(action))
            {
                /*Reading all the input fields*/
                String accountId = "";
                String bankStartDate = "";
                String bankEndDate = "";
                String serverStartDate = "";
                String serverEndDate = "";
                String netFinalAmount = null;
                String unPaidAmount = null;
                String isPaid = "";
                String rrDate = "";
                String ssDate = null;
                String isSettlementFileIsThere = "";
                String partnerId = "";

                if (functions.isValueNull(hashMap.get("acid")))
                {
                    accountId = hashMap.get("acid");
                }
                if (functions.isValueNull(hashMap.get("bsdate")))
                {
                    bankStartDate =commonFunctionUtil1.convertDatepickerFromTimestampWithSlash(hashMap.get("bsdate"));
                }
                if (functions.isValueNull(hashMap.get("bedate")))
                {
                    //bankEndDate = hashMap.get("bedate");
                    bankEndDate = commonFunctionUtil1.convertDatepickerFromTimestampWithSlash(hashMap.get("bedate"));
                }
                if (functions.isValueNull(hashMap.get("ssdate")))
                {
                    //serverStartDate = hashMap.get("ssdate");
                    serverStartDate = commonFunctionUtil1.convertDatepickerFromTimestampWithSlash(hashMap.get("ssdate"));
                }
                if (functions.isValueNull(hashMap.get("sedate")))
                {
                    //serverEndDate = hashMap.get("sedate");
                    serverEndDate = commonFunctionUtil1.convertDatepickerFromTimestampWithSlash(hashMap.get("sedate"));
                }
                if (functions.isValueNull(hashMap.get("nfamt")))
                {
                    netFinalAmount = hashMap.get("nfamt");
                }
                if (functions.isValueNull(hashMap.get("unamt")))
                {
                    unPaidAmount = hashMap.get("unamt");
                }
                if (functions.isValueNull(hashMap.get("ispaid")))
                {
                    isPaid = hashMap.get("ispaid");
                }
                if (functions.isValueNull(hashMap.get("rrdate")))
                {
                    rrDate = hashMap.get("rrdate");
                }
                if (functions.isValueNull(hashMap.get("sdate")))
                {
                    ssDate = hashMap.get("sdate");
                }
                if (functions.isValueNull(hashMap.get("issft")))
                {
                    isSettlementFileIsThere = hashMap.get("issft");
                }
                if (functions.isValueNull(hashMap.get("partnerId")))
                {
                    partnerId = hashMap.get("partnerId");
                }
                logger.error("PartnerId in AddBankWireAndDoStep1Recon:::::"+partnerId);

               // String partnerId = (String) session.getAttribute("merchantid");
                String settlementCycleId = (String) session.getAttribute("settlementCycleId");

                StringBuffer validationMsg = new StringBuffer();
                String EOL = "<BR>";

                /*input validation step*/
                if (!ESAPI.validator().isValidInput("ReceivedAmount", netFinalAmount, "AmountStr", 9, true))
                {
                    validationMsg.append("Invalid Received Amount" + EOL);
                }
                if (!ESAPI.validator().isValidInput("UnPaidAmount", unPaidAmount, "AmountStr", 9, true))
                {
                    validationMsg.append("Invalid UnPaidAmount" + EOL);
                }
                if (!ESAPI.validator().isValidInput("RR Release Date", rrDate, "fromDate", 20, false))
                {
                    validationMsg.append("Invalid RR Release Date" + EOL);
                }
                if (validationMsg.length() > 0)
                {
                    request.setAttribute("sberror", validationMsg.toString());
                    /*when dates are not valid then return on bank wire page so user can change the dates and re submit*/
                    //String settlementCycleId = (String) session.getAttribute("settlementCycleId");
                    /*step1:remove the transactions form temp_settlement_upload*/
                    settlementManager.removeTempTransactions(settlementCycleId);

                    /*remove the uploaded file*/
                    File file = new File(fullFileName);
                    file.delete();

                    /*return with error message*/
                    //errorMsg = "Internal error while processing your request";
                    RequestDispatcher rd = request.getRequestDispatcher("/addBankWire.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;

                }

                /*add the date and & time picker to select both date and time*/
                CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
                if (functions.isValueNull(rrDate))
                {
                    rrDate = commonFunctionUtil.convertDatepickerToTimestamp(rrDate, "23:59:59");
                }
                if (functions.isValueNull(ssDate))
                {
                    ssDate = commonFunctionUtil.convertDatepickerToTimestamp(ssDate, "23:59:59");
                }

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
                String tableName = Database.getTableNameForSettlement(gatewayType.getGateway());

                /*read the transaction details from temp table*/
                TransactionSummaryVO fileTransactionSummaryVO = null;

                Hashtable hashTable2 = null;
                SettlementDateVO settlementFileDateRangeVO = null;

                /*create the settlement cycle date object*/
                SettlementDateVO settlementCycleDateRangeVO = new SettlementDateVO();
                settlementCycleDateRangeVO.setSettlementStartDate(serverStartDate);
                settlementCycleDateRangeVO.setSettlementEndDate(serverEndDate);

                if ("Y".equalsIgnoreCase(isSettlementFileIsThere))
                {
                    /*create the object of payment process to read the settlement file*/
                    AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(null, Integer.parseInt(accountId));
                    PZSettlementFile.setFilepath(fullFileName);
                    PZSettlementFile.setAccountId(Integer.parseInt(accountId));

                   /*Reading settlement file*/
                    List<PZSettlementRecord> vTransactions = process.readSettlementFile(PZSettlementFile);
                    if (vTransactions == null || vTransactions.size() <= 0)
                    {
                     /*take an action when there are no transactions founds in excel file*/
                        String result = "Invalid settlement file/transactions are not found in file";

                    /*remove the uploaded file*/
                        File file = new File(fullFileName);
                        file.delete();

                    /*return with error message*/
                        request.setAttribute("sberror", result);
                        RequestDispatcher rd = request.getRequestDispatcher("/addBankWire.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }

                    //insert data into temp table
                    for (PZSettlementRecord settlementRecord : vTransactions)
                    {
                        boolean result = settlementManager.insertDataIntoTemp(settlementRecord, settlementCycleId);
                        if (result)
                        {
                            logger.debug(settlementRecord.getPaymentid() + ":added successfully");
                        }
                        else
                        {
                            logger.debug(settlementRecord.getPaymentid() + ":added failed");
                        }
                    }
                    settlementFileDateRangeVO = settlementManager.getSettlementPeriodFromExcel(settlementCycleId);

                     /*Settlement File Date Range and Settlement File Data*/
                    hashTable2 = settlementManager.getGatewayAccountProcessingAmountFromTempTable(settlementFileDateRangeVO.getSettlementStartDate(), settlementFileDateRangeVO.getSettlementEndDate(), settlementCycleId);
                    fileTransactionSummaryVO = settlementManager.getGatewayAccountProcessingAmountFromTempTable(settlementCycleId);
                }
                else
                {
                    settlementFileDateRangeVO = new SettlementDateVO();
                    settlementFileDateRangeVO.setSettlementStartDate(serverStartDate);
                    settlementFileDateRangeVO.setSettlementEndDate(serverEndDate);

                    /*Settlement Cycle Date Range & PZ DB Data*/
                    hashTable2 = settlementManager.getSettlementTransactions1(gatewayAccount, settlementCycleDateRangeVO.getSettlementStartDate(), settlementCycleDateRangeVO.getSettlementEndDate(), tableName, partnerId);

                     /*read the transaction details from temp table*/
                    fileTransactionSummaryVO = settlementManager.getSettlementTransactions2(gatewayAccount, settlementCycleDateRangeVO.getSettlementStartDate(), settlementCycleDateRangeVO.getSettlementEndDate(), tableName);
                }


                /*Settlement Cycle Date Range & PZ DB Data*/
                Hashtable hashTable1 = settlementManager.getGatewayAccountProcessingAmount(gatewayAccount, settlementCycleDateRangeVO.getSettlementStartDate(), settlementCycleDateRangeVO.getSettlementEndDate(), tableName, partnerId);

                /*preparing bank wire manager vo for insertion*/
                BankWireManagerVO bankWireManagerVO = new BankWireManagerVO();
                bankWireManagerVO.setPgtypeId(gatewayAccount.getPgTypeId());
                bankWireManagerVO.setMid(gatewayAccount.getMerchantId());
                bankWireManagerVO.setCurrency(gatewayAccount.getCurrency());
                bankWireManagerVO.setAccountId(accountId);
                bankWireManagerVO.setBank_start_date(bankStartDate);
                bankWireManagerVO.setBank_end_date(bankEndDate);
                bankWireManagerVO.setServer_start_date(serverStartDate);
                bankWireManagerVO.setServer_end_date(serverEndDate);

                double totalSaleAmount = fileTransactionSummaryVO.getSettledAmount() + fileTransactionSummaryVO.getReversedAmount() + fileTransactionSummaryVO.getChargebackAmount();
                double totalProcessingAmount = totalSaleAmount + fileTransactionSummaryVO.getAuthfailedAmount();
                double grossAmount = 0.00;
                if (functions.isValueNull(netFinalAmount))
                {
                    grossAmount = Double.valueOf(netFinalAmount);//totalSaleAmount-Double.valueOf(netFinalAmount);
                }

                //this will be dynamic after excel file reading
                bankWireManagerVO.setProcessing_amount(String.valueOf(totalProcessingAmount));
                bankWireManagerVO.setGrossAmount(String.valueOf(grossAmount));
                bankWireManagerVO.setNetfinal_amount(netFinalAmount);
                bankWireManagerVO.setUnpaid_amount(unPaidAmount);

                bankWireManagerVO.setIsrollingreservereleasewire("Y");
                bankWireManagerVO.setSettleddate(ssDate);
                bankWireManagerVO.setRollingreservereleasedateupto(rrDate);
                bankWireManagerVO.setDeclinedcoveredupto(serverEndDate);
                bankWireManagerVO.setChargebackcoveredupto(serverEndDate);
                bankWireManagerVO.setReversedCoveredUpto(serverEndDate);

                //this will be dynamic after excel file reading
                bankWireManagerVO.setBanksettlement_report_file("NoFile_" + settlementCycleId + ".pdf");
                if ("N".equalsIgnoreCase(isSettlementFileIsThere))
                {
                    bankWireManagerVO.setBanksettlement_transaction_file("NoFile_" + settlementCycleId + ".xls");
                }
                else
                {
                    bankWireManagerVO.setBanksettlement_transaction_file(fileName);
                }

                bankWireManagerVO.setSettlementCronExceuted("N");
                bankWireManagerVO.setPayoutCronExcuted("N");
                bankWireManagerVO.setIsPartnerCommCronExecuted("N");
                bankWireManagerVO.setIsAgentCommCronExecuted("N");

                bankWireManagerVO.setIspaid(isPaid);
                bankWireManagerVO.setSettlementCycleId(settlementCycleId);

                int bankWireId = bankDao.insertNewBankWireManagerNew(bankWireManagerVO);

                if (bankWireId > 0)
                {
                    bankDao.updateTheStatusBankWire(settlementCycleId, String.valueOf(bankWireId), "BankWireGenerated");

                    request.setAttribute("hashTable1", hashTable1);
                    request.setAttribute("hashTable2", hashTable2);

                    request.setAttribute("settlementDateVO1", settlementCycleDateRangeVO);
                    request.setAttribute("settlementDateVO2", settlementFileDateRangeVO);

                    request.setAttribute("accountid", accountId);

                    session.setAttribute("bankWireId", String.valueOf(bankWireId));

                    RequestDispatcher rd = request.getRequestDispatcher("/batchingStep1Recon.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            else if ("back".equals(action))
            {
                /*
                  remove the settlement cycle info,bankwire info from session as well as database
                */
                CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

                String settlementCycleId = (String) session.getAttribute("settlementCycleId");
                if (functions.isValueNull(settlementCycleId))
                {
                    settlementManager.removeSettlementCycle(settlementCycleId);
                }

                SettlementCycleVO settlementCycleVO = (SettlementCycleVO) session.getAttribute("settlementCycleVO");
                if (settlementCycleVO != null)
                {
                    settlementCycleVO.setStartDate(commonFunctionUtil.convertTimestampToDatepicker(settlementCycleVO.getStartDate()));
                    settlementCycleVO.setEndDate(commonFunctionUtil.convertTimestampToDatepicker(settlementCycleVO.getEndDate()));
                    session.setAttribute("settlementCycleVO", settlementCycleVO);
                }

                session.removeAttribute("settlementCycleId");
                RequestDispatcher rd = request.getRequestDispatcher("/addSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else if ("continue".equalsIgnoreCase(action))
            {
                String pendingSettlementCycleId = request.getParameter("settlementcycleid");
                RequestDispatcher rd = null;
                try
                {
                    SettlementCycleVO settlementCycleVO = settlementManager.getSettlementCycleInfo(pendingSettlementCycleId, "BankWireGenerated");
                    if (settlementCycleVO == null)
                    {
                        request.setAttribute("message", "Settlement cycle not found,please contact to  support team");
                        rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                    String partnerId=settlementCycleVO.getPartnerId();
                    logger.error("PartnerId in AddBankWireAndDoStep1Recon::::"+partnerId);

                    BankWireManagerVO bankWireManagerVO = bankDao.getSingleBankWireManagerActionSpecificBasedOnSettlementCycle(pendingSettlementCycleId);
                    if (bankWireManagerVO == null)
                    {
                        /*need to take action when bank wire not founds*/
                        request.setAttribute("message", "Bank wire not found,please contact to  support team");
                        rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }

                    /*fetch the transaction details after the settlement upload*/
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(bankWireManagerVO.getAccountId());
                    GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
                    String tableName = Database.getTableNameForSettlement(gatewayType.getGateway());

                    SettlementDateVO settlementCycleDateRangeVO = new SettlementDateVO();
                    settlementCycleDateRangeVO.setSettlementStartDate(settlementCycleVO.getStartDate());
                    settlementCycleDateRangeVO.setSettlementEndDate(settlementCycleVO.getEndDate());

                    String isSettlementFileIsThere = settlementCycleVO.getIsTransactionFileAvailable();
                    SettlementDateVO settlementFileDateRangeVO = null;
                    Hashtable hashTable2 = null;

                    if ("Y".equalsIgnoreCase(isSettlementFileIsThere))
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
                    session.setAttribute("settlementCycleVO",settlementCycleVO);
                    request.setAttribute("accountid", bankWireManagerVO.getAccountId());

                    rd = request.getRequestDispatcher("/batchingStep1Recon.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                catch (Exception e)
                {
                    logger.error("Exception:::::" + e);
                    request.setAttribute("message", "Internal error while processing your request,please contact to  support team");
                    rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            else
            {
                logger.error("Invalid Action");
                request.setAttribute("message", "Invalid action selection,please contact to  support team");
                RequestDispatcher rd = request.getRequestDispatcher("/listSettlementCycle.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        catch (SystemError systemError)
        {

            logger.error("systemError---"+systemError);
           /*when dates are not valid then return on bank wire page so user can change the dates and re submit*/
            String settlementCycleId = (String) session.getAttribute("settlementCycleId");
           /*step1:remove the transactions form temp_settlement_upload*/
            settlementManager.removeTempTransactions(settlementCycleId);

            /*remove the uploaded file*/
            File file = new File(fullFileName);
            file.delete();

            /*return with error message*/
            errorMsg = "Internal error while processing your request";

            request.setAttribute("sberror", errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addBankWire.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;

        }
        catch (Exception e)
        {
           logger.error("Exception---"+e);
          /*when dates are not valid then return on bank wire page so user can change the dates and re submit*/
            String settlementCycleId = (String) session.getAttribute("settlementCycleId");
          /*step1:remove the transactions form temp_settlement_upload*/
            settlementManager.removeTempTransactions(settlementCycleId);

          /*remove the uploaded file*/
            File file = new File(fullFileName);
            file.delete();

            errorMsg = "Internal error while processing your request";
            /*return with error message*/
            request.setAttribute("sberror", errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addBankWire.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
    }

    public String compare(String bankStartDate, String bankEndDate, String fileStartDate, String fileEndDate) throws ParseException
    {
        String result = "";
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date bankSDate = targetFormat.parse(bankStartDate);
        Date bankEDate = targetFormat.parse(bankEndDate);

        Date fileSDate = targetFormat.parse(fileStartDate);
        Date fileEDate = targetFormat.parse(fileEndDate);

        int dateStartCompareResult = bankSDate.compareTo(fileSDate);
        int dateEndCompareResult = bankEDate.compareTo(fileEDate);

        if (dateStartCompareResult != 0 || dateEndCompareResult != 0)
        {
            result = "Invalid file for for settlement cycle";
        }
        return result;
    }
}
