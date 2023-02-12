package com.fraud;

import com.directi.pg.Logger;
import com.fraud.at.ATRequestVO;
import com.fraud.at.ATResponseVO;
import com.fraud.at.AtUtils;
import com.fraud.dao.FraudDAO;
import com.fraud.fourstop.FourStopFSGateway;
import com.fraud.fourstop.FourStopRequestVO;
import com.fraud.fourstop.FourStopResponseVO;
import com.fraud.fourstop.FourStopUtils;
import com.fraud.utils.ReadFraudServiceRequest;
import com.fraud.validators.FraudServiceInputValidator;
import com.fraud.vo.*;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/28/14
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZFraudProcessor extends AbstractFraudProcessor
{
    static Logger logger = new Logger(PZFraudProcessor.class.getName());
    //private static TransactionLogger transactionLogger = new TransactionLogger(FraudChecker.class.getName());
    AtUtils atUtils=new AtUtils();
    FourStopUtils fourStopUtils=new FourStopUtils();
    FraudUtils fraudUtils=new FraudUtils();
    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
    FraudServiceInputValidator fraudServiceInputValidator =new FraudServiceInputValidator();
    @Override
    public PZFraudResponseVO newTransaction(PZFraudRequestVO pzFraudRequestVO,String fsid)
    {
        FraudTransaction fsTransaction=new FraudTransaction();
        PZFraudResponseVO pzFraudResponseVO=new PZFraudResponseVO();

        String memberId=pzFraudRequestVO.getMemberid();
        String trackingId=pzFraudRequestVO.getTrackingid();
        String memberTransId=pzFraudRequestVO.getDescription();
        String amount=pzFraudRequestVO.getAmount();
        String status=pzFraudRequestVO.getStatus();
        String time=pzFraudRequestVO.getTime();
        String accountId=pzFraudRequestVO.getAccountid();

        //System.out.println(memberId+"---"+trackingId+"---"+memberTransId+"---"+amount+"---"+status+"---"+accountId);

        FraudAccountDetailsVO accountDetailsVO=pzFraudRequestVO.getFraudAccountDetailsVO();
        List errorList=new ArrayList();

        fraudServiceInputValidator.performNewTransactionValidation(errorList,pzFraudRequestVO);
        //System.out.println("error---"+errorList.size());
        //System.out.println("error---"+errorList.toString());
        if(errorList.size()>0)
        {
            pzFraudResponseVO.setResponseCode("1");
            pzFraudResponseVO.setStatus("Failed");
            pzFraudResponseVO.setDescription("Invalid Inputs");
            pzFraudResponseVO.setErrorList(errorList);
            return pzFraudResponseVO;
        }

        //System.out.println("fsid---"+fsid);
        String fsGateway=FraudSystemService.getFSGateway(fsid);
        AbstractFSGateway gatewayInstance=FSGatewayFactory.getFSGatewayInstance(fsGateway);
        //System.out.println("Fs Gateway Name===" + fsGateway);
        logger.debug("Fs Gateway Name==="+fsGateway);

        if(ATFSGateway.FSNAME.equals(fsGateway))
        {
            ATRequestVO atRequestVO=(ATRequestVO)FSRequestVOFactory.getFSRequestVO(fsGateway);

            int fraud_trans_id=0;

            if((fraud_trans_id=fsTransaction.checkForTransExits(memberId,trackingId,memberTransId))!=0)
            {

                logger.debug("Transaction is Already available In Fraud Transaction");
                fsTransaction.updateFraudTransactionEntry(memberId,trackingId,memberTransId);

            }
            else
            {
                logger.debug("Transaction Not Available,Make Fresh Entry In Fraud Transaction");
                //fraud_trans_id=fsTransaction.fraudTransactionEntry(memberId,accountId,trackingId, fsid, memberTransId);
                fraud_trans_id=fsTransaction.fraudTransactionEntry(pzFraudRequestVO,fsid);

            }
            pzFraudRequestVO.setPzfraudtransid(String.valueOf(fraud_trans_id));
            pzFraudRequestVO.setStatus("0");
            logger.debug("Member "+memberId+" Using AcuityTech For Fraud Checking");
            String amount1=amount;
            amount=String.valueOf(Math.round(Double.parseDouble(amount)));
            pzFraudRequestVO.setAmount(amount);
            try
            {
                time=targetFormat.format(targetFormat.parse(time));
                pzFraudRequestVO.setTime(time);
            }
            catch (Exception e)
            {
                logger.error("Error While Converting Date"+e.getMessage());
            }

            ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
            readFraudServiceRequest.setATRequestParameter(pzFraudRequestVO,atRequestVO);
            atRequestVO.setFraudAccountDetailsVO(accountDetailsVO);
            ////todo - 54 - newTransaction(fraud gateway call)
            //Date date54=new Date();
            //transactionLogger.debug("PZFraudProcessor newTransaction(fraud gateway call) start time 54########"+date54.getTime());
            ATResponseVO atResponseVO=(ATResponseVO)gatewayInstance.newTransaction(atRequestVO);
            //transactionLogger.debug("PZFraudProcessor newTransaction(fraud gateway call) end time 54########" + new Date().getTime());
            //transactionLogger.debug("PZFraudProcessor newTransaction(fraud gateway call) diff time 54########"+(new Date().getTime()-date54.getTime()));
            fsTransaction.fraudActionEntryNew(atResponseVO,String.valueOf(fraud_trans_id),trackingId,amount1, memberId);

            //Date date542=new Date();
            pzFraudResponseVO= atUtils.getPZFraudResponseVO(atResponseVO,errorList,atRequestVO.getTrans_id());
            //transactionLogger.debug("PZFraudProcessor getPZFraudResponseVO from ATResponse to pzResponse diff time 542########"+(new Date().getTime()-date542.getTime()));

        }
        else if (FourStopFSGateway.FSNAME.equals(fsGateway))
        {
            FourStopRequestVO fourStopRequestVO=(FourStopRequestVO)FSRequestVOFactory.getFSRequestVO(fsGateway);

            int fraud_trans_id=0;

            if((fraud_trans_id=fsTransaction.checkForTransExits(memberId,trackingId,memberTransId))!=0)
            {

                logger.debug("Transaction is Already available In Fraud Transaction");
                fsTransaction.updateFraudTransactionEntry(memberId,trackingId,memberTransId);

            }
            else
            {
                logger.debug("Transaction Not Available,Make Fresh Entry In Fraud Transaction");
                //fraud_trans_id=fsTransaction.fraudTransactionEntry(memberId,accountId,trackingId, fsid, memberTransId);
                fraud_trans_id=fsTransaction.fraudTransactionEntry(pzFraudRequestVO,fsid);

            }
            pzFraudRequestVO.setPzfraudtransid(String.valueOf(fraud_trans_id));
            pzFraudRequestVO.setStatus("0");
            logger.debug("Member "+memberId+" Using FourStop For Fraud Checking");
            String amount1=amount;
            amount=String.valueOf(Math.round(Double.parseDouble(amount)));
            pzFraudRequestVO.setAmount(amount);
            try
            {
                time=targetFormat.format(targetFormat.parse(time));
                pzFraudRequestVO.setTime(time);
            }
            catch (Exception e)
            {
                logger.error("Error While Converting Date"+e.getMessage());
            }

            ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
            readFraudServiceRequest.setFourStopRequestParameter(pzFraudRequestVO, fourStopRequestVO);
            fourStopRequestVO.setFraudAccountDetailsVO(accountDetailsVO);

            //Date date54=new Date();
            //transactionLogger.debug("PZFraudProcessor newTransaction(fraud gateway call) start time 54########"+date54.getTime());
            FourStopResponseVO fourStopResponseVO=(FourStopResponseVO)gatewayInstance.newTransaction(fourStopRequestVO);
            //transactionLogger.debug("PZFraudProcessor newTransaction(fraud gateway call) end time 54########" + new Date().getTime());
            //transactionLogger.debug("PZFraudProcessor newTransaction(fraud gateway call) diff time 54########"+(new Date().getTime()-date54.getTime()));
            fsTransaction.fraudActionEntryNewForFourStop(fourStopRequestVO,fourStopResponseVO, String.valueOf(fraud_trans_id), trackingId, amount1, memberId);

            //Date date542=new Date();
            pzFraudResponseVO= fourStopUtils.getPZFraudResponseVO(fourStopResponseVO,errorList,fourStopRequestVO.getTrans_id());
            //transactionLogger.debug("PZFraudProcessor getPZFraudResponseVO from ATResponse to pzResponse diff time 542########"+(new Date().getTime()-date542.getTime()));
        }
        else
        {
            pzFraudResponseVO.setResponseCode("1");
            pzFraudResponseVO.setDescription("Please Map "+memberId+" With AcuityTech Fraud System For Fraud Checking");
            errorList.add("Apart From AcuityTech Fraud System We Don't Have Fraud System Implementation.");
            logger.debug("Don't Have Integration Other than AcuityTech Gateway");
            logger.debug("Please Map "+memberId+" With AcuityTech Fraud System for fraud Checking");
        }
        return  pzFraudResponseVO;
    }

    @Override
    public PZFraudResponseVO updateTransaction(PZFraudRequestVO requestVO,String fsid)
    {
        PZFraudResponseVO pzFraudResponseVO=new PZFraudResponseVO();
        FraudTransaction fsTransaction=new FraudTransaction();
        String status=requestVO.getStatus();
        String requestStatus=status;
        List errorList=new ArrayList();
        FraudServiceInputValidator fraudServiceInputValidator =new FraudServiceInputValidator();
        fraudServiceInputValidator.performUpdateTransactionValidation(errorList,requestVO);
        if(errorList.size()>0)
        {
            pzFraudResponseVO.setResponseCode("1");             //Set Error  Response Code
            pzFraudResponseVO.setStatus("Failed");
            pzFraudResponseVO.setDescription("Invalid Inputs"); //General Description On Error
            pzFraudResponseVO.setErrorList(errorList);
            return pzFraudResponseVO;
        }

        String fsGateway=FraudSystemService.fraudSystem.get(fsid);
        if(ATFSGateway.FSNAME.equals(fsGateway))
        {
            logger.debug("We Are Going To Use   "+fsGateway+" Fraud Service Gateway");
            status=fraudUtils.getATStatus(fraudUtils.getPZTransactionStatus(status));
            requestVO.setStatus(status);

            AbstractFSGateway fsGatewayInstance=FSGatewayFactory.getFSGatewayInstance(fsGateway);
            ATRequestVO atRequestVO=(ATRequestVO)FSRequestVOFactory.getFSRequestVO(fsGateway);
            ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
            readFraudServiceRequest.setATRequestParameter(requestVO,atRequestVO);
            ATResponseVO atResponseVO=(ATResponseVO)fsGatewayInstance.updateTransaction(atRequestVO);
            int k = fsTransaction.updateFraudActionEntry(requestStatus, atResponseVO.getStatus(), atResponseVO.getDescription(), atResponseVO.getInternal_trans_id());
            if(k>0)
            {
                errorList.add(" Request Process Successfully");
            }
            pzFraudResponseVO.setFsTransId(requestVO.getFstransid());
            pzFraudResponseVO.setResponseCode("1");
            pzFraudResponseVO.setStatus("Success");
            pzFraudResponseVO.setDescription("Thank you for using online fraud service with us");
            pzFraudResponseVO.setErrorList(errorList);
        }
        else if (FourStopFSGateway.FSNAME.equals(fsGateway))
        {
            logger.debug("We Are Going To Use   "+fsGateway+" Fraud Service Gateway");
            status=fraudUtils.getFourStopStatus(fraudUtils.getPZTransactionStatus(status));
            requestVO.setStatus(status);
            AbstractFSGateway fsGatewayInstance=FSGatewayFactory.getFSGatewayInstance(fsGateway);
            FourStopRequestVO fourStopRequestVO=(FourStopRequestVO)FSRequestVOFactory.getFSRequestVO(fsGateway);
            ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
            readFraudServiceRequest.setFourStopRequestParameter(requestVO, fourStopRequestVO);
            FourStopResponseVO fourStopResponseVO = (FourStopResponseVO) fsGatewayInstance.updateTransaction(fourStopRequestVO);
            int k = fsTransaction.updateFraudActionEntry(requestStatus, fourStopResponseVO.getStatus(), fourStopResponseVO.getDescription(), fourStopResponseVO.getInternal_trans_id());
            if(k>0)
            {
                errorList.add(" Request Process Successfully");
            }
            pzFraudResponseVO.setFsTransId(requestVO.getFstransid());
            pzFraudResponseVO.setResponseCode("1");
            pzFraudResponseVO.setStatus("Success");
            pzFraudResponseVO.setDescription("Thank you for using online fraud service with us");
            pzFraudResponseVO.setErrorList(errorList);
        }
        else
        {
            // Common  Checking flow For Update Transaction.
            pzFraudResponseVO.setResponseCode("1");
            pzFraudResponseVO.setDescription("Common Flow For FraudCheck Not Available.");
            errorList.add("Common Flow For FraudCheck Not Available.");
        }
        return pzFraudResponseVO;
    }

    @Override
    public PZFraudCustRegResponseVO customerRegistration(PZFraudCustRegRequestVO pzFraudCustRegRequestVO,String fsId)
    {
        PZFraudCustRegResponseVO pzFraudCustRegResponseVO =new PZFraudCustRegResponseVO();
        FraudAccountDetailsVO accountDetailsVO= pzFraudCustRegRequestVO.getFraudAccountDetailsVO();
        List errorList=new ArrayList();
        fraudServiceInputValidator.performCustomerRegistrationValidation(errorList, pzFraudCustRegRequestVO);
        if(errorList.size()>0){
            pzFraudCustRegResponseVO.setResponseCode("1");
            pzFraudCustRegResponseVO.setStatus("Failed");
            pzFraudCustRegResponseVO.setDescription("Invalid inputs");
            pzFraudCustRegResponseVO.setErrorList(errorList);
            return pzFraudCustRegResponseVO;
        }

        String fsGateway=FraudSystemService.getFSGateway(fsId);
        AbstractFSGateway gatewayInstance=FSGatewayFactory.getFSGatewayInstance(fsGateway);

        FraudDAO fraudTransactionDAO=new FraudDAO();
        int customerID=fraudTransactionDAO.allocateCustomerId(pzFraudCustRegRequestVO,fsId);
        int custRequestId= Integer.parseInt(pzFraudCustRegRequestVO.getCust_request_id());

        if (customerID==0){
            pzFraudCustRegResponseVO.setResponseCode("1");
            errorList.add("Internal error occured. please try again");
            pzFraudCustRegResponseVO.setErrorList(errorList);
            logger.error("Error in customer registration.");
            return pzFraudCustRegResponseVO;
        }
        if (FourStopFSGateway.FSNAME.equals(fsGateway))
        {
            FourStopRequestVO fourStopRequestVO=new FourStopRequestVO();
            ReadFraudServiceRequest readFraudServiceRequest=new ReadFraudServiceRequest();

            readFraudServiceRequest.setFourStopCustDetailsParameter(pzFraudCustRegRequestVO, fourStopRequestVO, customerID);
            fourStopRequestVO.setFraudAccountDetailsVO(accountDetailsVO);

            FourStopResponseVO fourStopResponseVO=(FourStopResponseVO)gatewayInstance.customerRegistration(fourStopRequestVO);

            boolean result=fraudTransactionDAO.updateCustomerInfo(fourStopResponseVO,customerID);

            pzFraudCustRegResponseVO = fourStopUtils.getPZFraudCustomerResponseVO(fourStopResponseVO,customerID,custRequestId);
        }
        else{
            pzFraudCustRegResponseVO.setResponseCode("1");
            pzFraudCustRegResponseVO.setDescription("Invalid fraud processor/fraud processor not found");
            errorList.add("Invalid fraud processor/fraud processor not found");
            logger.debug("Invalid fraud processor/fraud processor not found");
        }

        return pzFraudCustRegResponseVO;
    }

    @Override
    public PZFraudDocVerifyResponseVO documentIdVerify(PZFraudDocVerifyRequestVO pzFraudDocVerifyRequestVO, String fsId)
    {
        PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO =new PZFraudDocVerifyResponseVO();
        FraudAccountDetailsVO accountDetailsVO=pzFraudDocVerifyRequestVO.getFraudAccountDetailsVO();

        List errorList=new ArrayList();
        fraudServiceInputValidator.performDocVerifyParameterValidation(errorList, pzFraudDocVerifyRequestVO);
        if(errorList.size()>0){
            pzFraudDocVerifyResponseVO.setResponseCode("1");
            pzFraudDocVerifyResponseVO.setStatus("Failed");
            pzFraudDocVerifyResponseVO.setDescription("Invalid Inputs");
            pzFraudDocVerifyResponseVO.setErrorList(errorList);
            return pzFraudDocVerifyResponseVO;
        }

        String fsGateway=FraudSystemService.getFSGateway(fsId);
        AbstractFSGateway gatewayInstance=FSGatewayFactory.getFSGatewayInstance(fsGateway);

        FraudDAO fraudTransactionDAO=new FraudDAO();

        PZCustomerDetailsVO pzCustomerDetailsVO=fraudTransactionDAO.getCustomerRegistrationDetails(pzFraudDocVerifyRequestVO);

        pzFraudDocVerifyRequestVO.setPzCustomerDetailsVO(pzCustomerDetailsVO);

        int customerID=fraudTransactionDAO.insertDocVerifyInfo(pzFraudDocVerifyRequestVO);
        //int customerID= Integer.parseInt(pzFraudDocVerifyRequestVO.getCustomer_registration_id());
        logger.error("customerID :"+customerID);
        pzFraudDocVerifyRequestVO.setCustomer_registration_id(pzCustomerDetailsVO.getCustomer_registration_id());

        if (FourStopFSGateway.FSNAME.equals(fsGateway))
        {
            FourStopRequestVO fourStopRequestVO=new FourStopRequestVO();
            ReadFraudServiceRequest readFraudServiceRequest=new ReadFraudServiceRequest();

            readFraudServiceRequest.setFourStopDocVerifyParameter(pzFraudDocVerifyRequestVO, fourStopRequestVO);
            fourStopRequestVO.setFraudAccountDetailsVO(accountDetailsVO);

            FourStopResponseVO fourStopResponseVO=(FourStopResponseVO)gatewayInstance.documentIdVerify(fourStopRequestVO);
            pzFraudDocVerifyResponseVO = fourStopUtils.getDocumentVerifyResponseVO(fourStopResponseVO);

            boolean result=fraudTransactionDAO.updateDocVerifyInfo(fourStopResponseVO, customerID);

        }
        else
        {
            pzFraudDocVerifyResponseVO.setResponseCode("1");
            pzFraudDocVerifyResponseVO.setDescription("Invalid fraud processor/fraud processor not found");
            errorList.add("Invalid fraud processor/fraud processor not found");
            logger.debug("Invalid fraud processor/fraud processor not found");
        }
        return pzFraudDocVerifyResponseVO;
    }

    public void setSpecificReqParameters(PZFraudRequestVO requestVO,FSGenericRequestVO fsGenericRequestVO)
    {
        //To change body of created methods use File | Settings | File Templates.

    }
    public void setSpecificResParameters(PZFraudResponseVO requestVO,FSGenericResponseVO fsGenericResponseVO)
    {
        //To change body of created methods use File | Settings | File Templates.

    }



}
