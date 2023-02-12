package com.directi.pg;

import com.manager.TerminalManager;
import com.manager.dao.PayoutDAO;
import com.manager.vo.TerminalVO;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZFileVO;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Pramod on 20/01/2021.
 */
public class CommonBulkPayOut
{
    private static Logger logger = new Logger(CommonBulkPayOut.class.getName());
    private final  static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.bulkmaxcount");
    public Hashtable saveBulkPayOut(String completeFileName, String gateway, AuditTrailVO auditTrailVO,String memberId)
    {
        PZFileVO  pzFileVO      = new PZFileVO();
        PayoutDAO payoutDAO     = new PayoutDAO();
        String resultString     = "";
        HashMap<String,String> errorHashMap         = null;
        Functions functions     = new Functions();
        List<PZPayoutRequest>  pzPayoutRequestList  = null;
        List<PZPayoutRequest>  pzPayoutRequestList2  = new ArrayList<PZPayoutRequest>();

        Hashtable hashtable                         = new Hashtable();


        try{

            pzFileVO.setFilepath(completeFileName);
            logger.error("-----file Name -----"+pzFileVO.getFileName());
            AbstractPaymentProcess paymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(gateway);
            logger.error("-----Before readBulkPayOut()-----");
            Hashtable returnHashtable  = paymentProcess.readBulkPayoutFile(pzFileVO);
            logger.error("-----After readBulkPayOutFile()----"+returnHashtable);

            errorHashMap        = (HashMap<String,String>)returnHashtable.get("errorHashMap");
            pzPayoutRequestList = (List<PZPayoutRequest>)returnHashtable.get("pzPayoutRequestsList");
            //int maxcount= Integer.parseInt(checkBulkpayoutMaxcount(memberId));
            int maxcount        = Integer.parseInt(RB.getString("maxcount"));
            if(errorHashMap != null && !errorHashMap.isEmpty())
            {
                for(String error : errorHashMap.values() ){
                    resultString = resultString + error;
                }
                hashtable.put("ERROR",resultString);
                deleteErrorExcelFile(completeFileName);
                return hashtable;
            }
            if(pzPayoutRequestList !=null && pzPayoutRequestList.size()>maxcount )
            {
                String resultString2      = "Max Bulkpayout Size should not be Greater Than::"+maxcount;
                resultString              = (String) hashtable.getOrDefault("ERROR","");
                if(functions.isValueNull(resultString)){
                    resultString = resultString+"<BR>"+resultString2;
                }else{
                    resultString = resultString2;
                }
                hashtable.put("ERROR", resultString);
                deleteErrorExcelFile(completeFileName);
                return hashtable;
            }

            logger.info("pzPayoutRequestList ----------> " + pzPayoutRequestList);
            if(pzPayoutRequestList != null && (pzPayoutRequestList.size() > 0 && pzPayoutRequestList.size()<=maxcount) )
            {

               try
                {   String invalidErrorStr        = validateBulkUploadData(pzPayoutRequestList,memberId,pzPayoutRequestList2);
                    logger.info("inside if pzPayoutRequestList != null invalidErrorStr ---->"+invalidErrorStr);
                    if(!invalidErrorStr.isEmpty()){
                        logger.info("inside if !invalidErrorStr.isEmpty() condition pzPayoutRequestList2.size() ---->"+pzPayoutRequestList2.size());
                        if(pzPayoutRequestList2 != null && pzPayoutRequestList2.size() > 0){
                           payoutDAO.addBulkPayout(pzPayoutRequestList2, completeFileName, memberId);
                           // resultString=" Successfully Uploaded Total Count: "+pzPayoutRequestList2.size();
                            Double totalAmount   =  pzPayoutRequestList2.stream().mapToDouble(x -> Double.parseDouble(x.getPayoutAmount())).sum();

                            hashtable.put("batchCount",pzPayoutRequestList2.size()+"");
                            hashtable.put("batchAmount",totalAmount+"");
                        }
                        else{
                            resultString = "Successfully Uploaded Total Count: "+pzPayoutRequestList2.size();
                        }
                         hashtable.put("ERROR",invalidErrorStr);
                         hashtable.put("pzPayoutRequestList",pzPayoutRequestList);
                         boolean isFileDeleted = deleteErrorExcelFile(completeFileName);
                         logger.info("Error File Deleted 1 ---------->"+isFileDeleted);

                    }else{
                        logger.error("addBulkPayout Start---->");
                        resultString        = payoutDAO.addBulkPayout(pzPayoutRequestList, completeFileName, memberId);
                        resultString        = "Bulk Payout Uploaded Successfully";
                        //resultString        = "Uploaded Successful Total Count: "+pzPayoutRequestList.size();
                        hashtable.put("SUCCESS",resultString);

                        Double totalAmount  =  pzPayoutRequestList.stream().mapToDouble(x -> Double.parseDouble(x.getPayoutAmount())).sum();

                        hashtable.put("batchCount",pzPayoutRequestList.size()+"");
                        hashtable.put("batchAmount",totalAmount+"");

                        logger.error("addBulkPayout End---->");
                        logger.error("addBulkPayout totalAmount---->>>> "+ totalAmount);
                    }
                }
                catch (PZDBViolationException e)
                {
                    logger.error("---- saveBulkPayOut(Borgun)-----", e);
                    resultString    = "Error While Uploading";
                    hashtable.put("ERROR",resultString);
                    boolean isFileDeleted = deleteErrorExcelFile(completeFileName);
                    logger.info("Error File Deleted 2 ---------->"+isFileDeleted);
                }
            }
        }catch (Exception e){
            logger.error("---- saveBulkPayOut(Borgun)-----", e);
            resultString = "Error While Uploading";
            hashtable.put("ERROR",resultString);
            boolean isFileDeleted = deleteErrorExcelFile(completeFileName);
            logger.info("Error File Deleted 3 ---------->"+isFileDeleted);
        }
        return hashtable;
    }
    public String  validateBulkUploadData(List<PZPayoutRequest>  pzPayoutRequestList,String memberId ,List<PZPayoutRequest>  pzPayoutRequestList2){
        InputValidator inputValidator       = new InputValidator();
        String error                        = "";
        String EOL                          = "<BR>";
        String errorString                  = "";
        List<InputFields> inputFieldsList   = new ArrayList<InputFields>();
        TerminalManager terminalManager     = new TerminalManager();
        Functions functions                 = new Functions();
        StringBuilder orderids              = new StringBuilder();
        List<String> orderCheck             = new ArrayList<>();
        String OrderUniqueness  = "";
        PayoutDAO payoutDAO     = new PayoutDAO();
        boolean isInValid         = false;
        try
        {
            //inputFieldsList.add(InputFields.TERMINALID);
            inputFieldsList.add(InputFields.BANK_ACCOUNT_NAME);
            inputFieldsList.add(InputFields.BANK_ACCOUNT_NUMBER);
            inputFieldsList.add(InputFields.BANK_IFSC);
            inputFieldsList.add(InputFields.TRANSFER_TYPE);
            inputFieldsList.add(InputFields.AMOUNT);

             for(PZPayoutRequest pzPayoutRequest : pzPayoutRequestList){
                 error = "";
                /* String []res        = pzPayoutRequest.getTerminalId().split("\\.");
                 String terminalId   = res[0];
                 pzPayoutRequest.setTerminalId(terminalId);
                 TerminalVO terminalVO       = null;

                 terminalVO = terminalManager.getTerminalByTerminalId(pzPayoutRequest.getTerminalId());
                 if(terminalVO == null){
                     error = "Invalid Terminal ID"+EOL;
                 }else{

                     boolean isValidTerminal = terminalManager.isValidTerminal(memberId,terminalVO.getAccountId(),terminalId,terminalVO.getPaymodeId(),terminalVO.getCardTypeId());
                     if(isValidTerminal){
                         error = "Terminal ID doesn't Exist"+EOL;
                     }
                 }*/
                 if(functions.isValueNull(pzPayoutRequest.getPayoutAmount()) ){
                    try{
                     String payoutAmount =   String.format("%.2f", Double.parseDouble(pzPayoutRequest.getPayoutAmount()));
                        pzPayoutRequest.setPayoutAmount(payoutAmount);
                    }catch (NumberFormatException e){
                        logger.error("NumberFormatException---");
                    }

                 }
                 if(functions.isValueNull(pzPayoutRequest.getOrderId()) ){
                    try{
                     orderids.append("'"+pzPayoutRequest.getOrderId()+"',");
                        if(orderCheck.contains(pzPayoutRequest.getOrderId())){
                            logger.error("in side if  contains checkexcel duplicate---"+error);
                           // errorString = EOL+"Duplicate OrderId"+EOL;
                            error    = "Duplicate OrderId : "+pzPayoutRequest.getOrderId()+ EOL;
                            isInValid =  true;
                        }else{
                            orderCheck.add(pzPayoutRequest.getOrderId());
                        }

                    }catch (Exception e){
                        logger.error("Exception---", e);
                    }
                 }
                 if(functions.isValueNull(pzPayoutRequest.getOrderId()) ){
                    try{
                        OrderUniqueness  =   payoutDAO.checkOrderUniqueness(memberId, pzPayoutRequest.getOrderId());
                        if(functions.isValueNull(OrderUniqueness)){
                            logger.error("inside if OrderUniqueness check in database ---"+pzPayoutRequest.getOrderId()+"::"+OrderUniqueness);
                            error       = error + OrderUniqueness+":"+pzPayoutRequest.getOrderId() + EOL;
                            //errorString = errorString + EOL +OrderUniqueness;
                            isInValid   = true;
                        }

                    }catch (Exception e){
                        logger.error("Exception---", e);
                    }
                 }
                 if(functions.isValueNull(pzPayoutRequest.getOrderId()) ){
                     try{
                         OrderUniqueness  =   payoutDAO.checkOrderUniquenessInUploadList(memberId, pzPayoutRequest.getOrderId());
                         if(functions.isValueNull(OrderUniqueness)){
                             logger.error("inside if OrderUniqueness check in InUploadList database ---"+pzPayoutRequest.getOrderId()+"::"+OrderUniqueness);
                             error          = error + OrderUniqueness + ":" +pzPayoutRequest.getOrderId() + EOL;
                             //errorString    = errorString + EOL +OrderUniqueness;
                             isInValid = true;
                         }

                     }catch (Exception e){
                         logger.error("Exception---", e);
                     }
                 }
                 logger.error("pzPayoutRequest.getPayoutAmount() ---"+ pzPayoutRequest.getPayoutAmount());
                 if(pzPayoutRequest.getPayoutAmount().equalsIgnoreCase("0.0") || pzPayoutRequest.getPayoutAmount().equalsIgnoreCase("0.00")){
                     isInValid  = true;
                     error      = error + "Payout Amount should be grater than 0.0 "+EOL;
                 }

                 ValidationErrorList validationErrorList = new ValidationErrorList();
                 InputValidationsfor(pzPayoutRequest, inputFieldsList, validationErrorList, false);
                 if (!validationErrorList.isEmpty())
                 {
                     for (InputFields inputFields : inputFieldsList)
                     {
                         String errorlist = "";
                         if (validationErrorList.getError(inputFields.toString()) != null)
                         {
                             logger.error("inputFields error >>>> "+validationErrorList.getError(inputFields.toString()).getLogMessage());
                             errorlist = error + validationErrorList.getError(inputFields.toString()).getMessage() + EOL;
                             if(!errorlist.isEmpty()){
                                 pzPayoutRequest.setOrderDescription(errorlist);
                                 isInValid = true;
                               //  errorString =  "Please Check Discription Column for details Error"+EOL;
                             }
                         }
                     }
                     pzPayoutRequest.setStatus("Rejected");

                 }else{
                     logger.error("in side  else validationErrorList ---");
                     if(!error.isEmpty()){
                         logger.error("in side if !error.isEmpty() condition ---"+error);
                         pzPayoutRequest.setOrderDescription(error);
                         pzPayoutRequest.setStatus("Rejected");
                         isInValid = true;
                     }else if(error.isEmpty()){
                         logger.error("in side else  clean record Uploaded condition---");
                          pzPayoutRequest.setOrderDescription(pzPayoutRequest.getOrderId());
                          pzPayoutRequestList2.add(pzPayoutRequest);
                          pzPayoutRequest.setStatus("Uploaded");
                     }

                 }
             }
           /*if(functions.isValueNull(OrderUniqueness)){
                logger.error("inside if OrderUniqueness check in database ---"+OrderUniqueness);
               // error=error+OrderUniqueness+EOL;
                errorString = errorString + EOL +OrderUniqueness;
            }*/
            /*for(String list:orderCheck){
                if(orderCheck.contains(list)){
                    logger.error("inside if Duplicate OrderId condition ---" +list);
                    errorString="Duplicate OrderId Found In UploadFile!. Kindly try to place transaction with unique orderId.";
                }else{
                    logger.error("inside else Duplicate OrderId condition ---" +list);
                }
                break;
            }*/
         /*   if(orderids.length()>0){
                int len     = orderids.length()-1;
                orderids    = orderids.deleteCharAt(len);
                logger.error("inside if uniq Orderid Check CommonBulkPayout::::"+orderids);
                String OrderUniqueness  =   payoutDAO.checkOrderUniquenessStatus(memberId,orderids);
                errorString = errorString + EOL +OrderUniqueness;
            }*/
            if(isInValid){
                errorString = errorString + EOL +  "Please Check Discription Column for details Error"+EOL;
            }else{
                errorString = "";
            }

            logger.error("ouside for duplicate check condition ---" +errorString);
        }
        catch (Exception e)
        {
            logger.error("validateBulkUploadData 2 ------------>",e);
            errorString = "Error while Uploading";
        }

        return errorString;
    }

    public void InputValidationsfor(PZPayoutRequest pZPayoutRequest, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional){

        for (InputFields input : inputList)
        {
            switch (input)
            {
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), pZPayoutRequest.getPayoutAmount(), "Amount", 12, isOptional) )
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount. It accepts Only Numeric value", "Invalid Amount "));
                    break;
                case BANK_ACCOUNT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), pZPayoutRequest.getCustomerBankAccountName(), "SafeString",35,isOptional))
                    {
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Account Name", "Invalid Bank Account Name"));
                    }
                break;
                case BANK_ACCOUNT_NUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(),pZPayoutRequest.getBankAccountNo(),"OnlyNumber",35,isOptional))
                    {
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Account Number", "Invalid Bank Account Number::::"+pZPayoutRequest.getBankAccountNo()));
                    }
                break;
                case BANK_IFSC:
                    if (!ESAPI.validator().isValidInput("bankIfsc",pZPayoutRequest.getBankIfsc(),"alphanum",11,isOptional))
                    {
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Ifsc Code", "Invalid Bank Ifsc Code"+ ":::"+pZPayoutRequest.getBankIfsc()));
                    }
                break;
                case TRANSFER_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(),pZPayoutRequest.getBankTransferType(),"StrictString",5,isOptional))
                    {
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Transfer Type", "Invalid Bank Transfer Type"+ ":::"+pZPayoutRequest.getBankTransferType()));
                    }
                break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), pZPayoutRequest.getOrderId(), "Description", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Order Id/Description", "Invalid Order Id/Description :::" + pZPayoutRequest.getOrderId()));
                    break;
                /*case TERMINALID:
                    if (!ESAPI.validator().isValidInput(input.toString(), pZPayoutRequest.getTerminalId(), "Numbers", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Terminal ID", "Invalid Terminal ID :::" + pZPayoutRequest.getTerminalId()));

                break;*/

            }
        }
    }
    public ArrayList<String> getBulkColumnNameList(){
        ArrayList arrayList = new ArrayList();

        arrayList.add("bankaccountno");
        arrayList.add("customerbankaccountname");
        arrayList.add("bankifsc");
        arrayList.add("payoutamount");
        arrayList.add("banktransfertype");
        arrayList.add("orderid");
        //arrayList.add("terminalid");

        return arrayList;
    }
    public  boolean deleteErrorExcelFile(String filename){
        File deleteFile         = new File(filename) ;
        boolean isFileDeleted   = false;

        if(deleteFile.exists()){
            isFileDeleted = deleteFile.delete() ;
        }
        return isFileDeleted;
    }

      static String checkBulkpayoutMaxcount(String toid )
    {
        logger.debug("check order uniqueness---");
        String bulkpayoutmaxcount = "";
        Connection con = null;
        try
        {

            con = Database.getConnection();
            String transaction_table = "transaction_common";
            String query2 = "select bulkpayoutmaxcount from member_account_mapping where memberid = ?";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            logger.error("checkBulkpayoutMaxcount query---" + pstmt1);

            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                bulkpayoutmaxcount =rs1.getString("bulkpayoutmaxcount");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return bulkpayoutmaxcount;
    }

}