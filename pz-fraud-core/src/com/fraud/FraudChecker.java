package com.fraud;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.fraud.manager.InternalFraudProcessor;
import com.fraud.utils.ReadFraudServiceRequest;
import com.fraud.vo.*;
import com.manager.FraudRuleManager;
import com.manager.GatewayManager;
import com.manager.dao.FraudTransactionDAO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
import com.manager.vo.fraudruleconfVOs.RuleMasterVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 12/23/14
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudChecker
{
    private static Logger logger = new Logger(FraudChecker.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(FraudChecker.class.getName());

    //private static TransactionLogger transactionLogger = new TransactionLogger(FraudChecker.class.getName());
    FraudTransactionDAO fraudTransactionDAO=new FraudTransactionDAO();
    Functions functions=new Functions();
    public void checkFraudBasedOnMerchantFlag(CommonValidatorVO commonValidatorVO, FraudServiceConfigurationVO fraudSystemConfigurationVO)
    {
        boolean isFraud=false;
        String fraudScore="";
        GatewayManager gatewayManager=new GatewayManager();
        GatewayAccount gatewayAccount=gatewayManager.getGatewayAccountForAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getGatewayAccount();

        ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
        PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(commonValidatorVO,gatewayAccount);

        //Get merchant fraud account details
        MerchantDAO merchantDAO=new MerchantDAO();
        FraudAccountDetailsVO accountDetailsVO=merchantDAO.getFraudAccountDetails(fraudSystemConfigurationVO.getMemberId());
        if(accountDetailsVO!=null)
        {
            requestVO.setFraudAccountDetailsVO(accountDetailsVO);
            PZFraudProcessor pzFraudProcessor=new PZFraudProcessor();
            PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.newTransaction(requestVO,fraudSystemConfigurationVO.getFraudSystemId());

            if(pzFraudResponseVO.getErrorList().size()>0)
            {
                //MANAGE EXCEPTION AND PROPOGATE TRANSACTION ACCORDINGLY
            }
            else
            {
                double autoReversalScore=commonValidatorVO.getMerchantDetailsVO().getFraudAutoReversalScore();
                double fsScore=Double.valueOf(pzFraudResponseVO.getScore());
                fraudScore= Functions.round(pzFraudResponseVO.getScore(),2);
                logger.debug("MemberId==="+commonValidatorVO.getMerchantDetailsVO().getMemberId());
                logger.debug("Member Auto Reversal Score===="+autoReversalScore);
                logger.debug("Fraud System Returned Score===="+fsScore);
                isFraud=calculateFraudRisk(autoReversalScore,fsScore);
                if(isFraud)
                {
                    fraudTransactionDAO.updateStatusFlagForOnlineCheck("Y","N/A","N/A",requestVO.getTrackingid());
                }
            }
            commonValidatorVO.setFraud(isFraud);
            commonValidatorVO.setFraudScore(fraudScore);
        }
        else
        {
            logger.debug("Merchant is not mapped with fraudaccount");
            commonValidatorVO.setFraudScore(fraudScore);
        }

    }
    public void checkFraudBasedOnMerchantFlagNew(CommonValidatorVO commonValidatorVO,FraudAccountDetailsVO accountDetailsVO)
    {
        boolean isFraud=false;
        String fraudScore="";
        GatewayManager gatewayManager=new GatewayManager();
        GatewayAccount gatewayAccount=gatewayManager.getGatewayAccountForAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getGatewayAccount();

        ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();

        PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(commonValidatorVO,gatewayAccount);

        requestVO.setFraudAccountDetailsVO(accountDetailsVO);
        PZFraudProcessor pzFraudProcessor=new PZFraudProcessor();

        PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.newTransaction(requestVO,accountDetailsVO.getFraudSystemId());
        logger.error("null check:::" +pzFraudResponseVO.getErrorList());

            if (pzFraudResponseVO.getErrorList() != null && pzFraudResponseVO.getErrorList().size() > 0)
            {
                //MANAGE EXCEPTION AND PROPOGATE TRANSACTION ACCORDINGLY
            }
            else
            {
                double autoReversalScore = commonValidatorVO.getMerchantDetailsVO().getFraudAutoReversalScore();
                double fsScore = 0.00;
                if(pzFraudResponseVO.getScore() != null)
                {
                    Double.valueOf(pzFraudResponseVO.getScore());
                    fraudScore = Functions.round(pzFraudResponseVO.getScore(), 2);
                }
                logger.debug("MemberId===" + commonValidatorVO.getMerchantDetailsVO().getMemberId());
                logger.debug("Member Auto Reversal Score====" + autoReversalScore);
                logger.debug("Fraud System Returned Score====" + fsScore);
                isFraud = calculateFraudRisk(autoReversalScore, fsScore);
                if (isFraud)
                {
                    fraudTransactionDAO.updateStatusFlagForOnlineCheck("Y", "N/A", "N/A", requestVO.getTrackingid());
                }
            }
        commonValidatorVO.setFraud(isFraud);
        commonValidatorVO.setFraudScore(fraudScore);
    }

    public void checkFraudBasedOnMerchantFlagNew(CommonValidatorVO commonValidatorVO,FraudAccountDetailsVO accountDetailsVO,GatewayAccount gatewayAccount)
    {
        boolean isFraud=false;
        String fraudScore="";
        String rulesTriggered="";
        //GatewayManager gatewayManager=new GatewayManager();
        //GatewayAccount gatewayAccount=gatewayManager.getGatewayAccountForAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getGatewayAccount();

        ReadFraudServiceRequest readFraudServiceRequest=new ReadFraudServiceRequest();

        PZFraudRequestVO requestVO=readFraudServiceRequest.getPZFraudRequestVO(commonValidatorVO,gatewayAccount);

        requestVO.setFraudAccountDetailsVO(accountDetailsVO);
        PZFraudProcessor pzFraudProcessor=new PZFraudProcessor();

        //System.out.println("from gateway account-----"+gatewayAccount.getGateway());
        PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.newTransaction(requestVO,accountDetailsVO.getFraudSystemId());


        if(pzFraudResponseVO.getErrorList().size()>0)
        {
            //MANAGE EXCEPTION AND PROPOGATE TRANSACTION ACCORDINGLY
        }
        else
        {
            double autoReversalScore=commonValidatorVO.getMerchantDetailsVO().getFraudAutoReversalScore();
            double fsScore=Double.valueOf(pzFraudResponseVO.getScore());
            fraudScore= Functions.round(pzFraudResponseVO.getScore(),2);
            rulesTriggered=pzFraudResponseVO.getRulesTriggered();
            logger.debug("MemberId==="+commonValidatorVO.getMerchantDetailsVO().getMemberId());
            logger.debug("Member Auto Reversal Score===="+autoReversalScore);
            logger.debug("Fraud System Returned Score===="+fsScore);
            isFraud=calculateFraudRisk(autoReversalScore,fsScore);
            if(isFraud)
            {
                fraudTransactionDAO.updateStatusFlagForOnlineCheck("Y","N/A","N/A",requestVO.getTrackingid());
            }
        }
        commonValidatorVO.setFraud(isFraud);
        commonValidatorVO.setFraudScore(fraudScore);
        commonValidatorVO.setRuleTriggered(rulesTriggered);

    }
    public PZFraudCustRegResponseVO verifyCustomer(PZFraudCustRegRequestVO pzFraudCustRegRequestVO)
    {
        FraudTransaction fraudTransaction=new FraudTransaction();
        PZFraudCustRegResponseVO pzFraudCustRegResponseVO=new PZFraudCustRegResponseVO();
        String fsAccountId=fraudTransaction.getPartnerFsDetails(pzFraudCustRegRequestVO.getPartnerId());
        logger.error("fsAccountId:::::" + fsAccountId);
        FraudSystemAccountVO fraudSystemAccountVO= new FraudSystemAccountVO();
        if (functions.isValueNull(fsAccountId))
        {
            fraudSystemAccountVO = FraudSystemAccountService.getFraudSystemAccount(fsAccountId);
        }
        logger.error("fraud system id:::::" + fraudSystemAccountVO.getFraudSystemId());
        logger.error("fraud system account name:::::" + fraudSystemAccountVO.getAccountName());
        if ((functions.isValueNull(fsAccountId)) && (functions.isValueNull(fraudSystemAccountVO.getFraudSystemId())))
        {
            FraudAccountDetailsVO accountDetailsVO = new FraudAccountDetailsVO();
            accountDetailsVO.setFraudSystemMerchantId(fraudSystemAccountVO.getAccountName());
            accountDetailsVO.setPassword(fraudSystemAccountVO.getPassword());
            pzFraudCustRegRequestVO.setFraudAccountDetailsVO(accountDetailsVO);

            PZFraudProcessor pzFraudProcessor = new PZFraudProcessor();
            pzFraudCustRegResponseVO = pzFraudProcessor.customerRegistration(pzFraudCustRegRequestVO, fraudSystemAccountVO.getFraudSystemId());
            return pzFraudCustRegResponseVO;
        }
        else {
            Result result=new Result();
            result.setResultCode("-1");
            result.setStatus("failed");
            result.setDescription("Fraud System account not found");

            pzFraudCustRegResponseVO.setResult(result);
            return pzFraudCustRegResponseVO;
        }
    }
    public PZFraudDocVerifyResponseVO documentIdVerify(PZFraudDocVerifyRequestVO pzFraudDocVerifyRequestVO)
    {
        FraudTransaction fraudTransaction=new FraudTransaction();
        PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO=new PZFraudDocVerifyResponseVO();
        String fsAccountId=fraudTransaction.getPartnerFsDetails(pzFraudDocVerifyRequestVO.getPartnerId());
        FraudSystemAccountVO fraudSystemAccountVO= FraudSystemAccountService.getFraudSystemAccount(fsAccountId);

        if (!(functions.isValueNull(fsAccountId)) || !(functions.isValueNull(fraudSystemAccountVO.getFraudSystemAccountId()))){

            Result result=new Result();
            result.setResultCode("-1");
            result.setStatus("failed");
            result.setDescription("Fraud System account not found");

            pzFraudDocVerifyResponseVO.setResult(result);
            return pzFraudDocVerifyResponseVO;
        }
        logger.debug("fraud system account:::::" + fraudSystemAccountVO.getFraudSystemAccountId());
        logger.debug("fraud system account name:::::" + fraudSystemAccountVO.getAccountName());

        FraudAccountDetailsVO accountDetailsVO=new FraudAccountDetailsVO();
        accountDetailsVO.setFraudSystemMerchantId(fraudSystemAccountVO.getAccountName());
        accountDetailsVO.setPassword(fraudSystemAccountVO.getPassword());
        pzFraudDocVerifyRequestVO.setFraudAccountDetailsVO(accountDetailsVO);

        PZFraudProcessor pzFraudProcessor=new PZFraudProcessor();
        pzFraudDocVerifyResponseVO =pzFraudProcessor.documentIdVerify(pzFraudDocVerifyRequestVO,fraudSystemAccountVO.getFraudSystemId());

        return pzFraudDocVerifyResponseVO;
    }

    public PZFraudResponseVO newTransaction(PZFraudRequestVO pzFraudRequestVO)
    {
        FraudTransaction fraudTransaction = new FraudTransaction();
        PZFraudResponseVO pzFraudResponseVO = new PZFraudResponseVO();
        String fsAccountId = fraudTransaction.getPartnerFsDetails(pzFraudRequestVO.getPartnerid());
        logger.error("partnerid:::" + pzFraudRequestVO.getPartnerid());
        logger.error("fsAccountId:::::" + fsAccountId);
        FraudSystemAccountVO fraudSystemAccountVO = new FraudSystemAccountVO();
        if (functions.isValueNull(fsAccountId))
        {
            fraudSystemAccountVO = FraudSystemAccountService.getFraudSystemAccount(fsAccountId);
        }

        logger.error("fraud system id:::::" + fraudSystemAccountVO.getFraudSystemId());
        logger.error("fraud system account name:::::" + fraudSystemAccountVO.getAccountName());

        if ((functions.isValueNull(fsAccountId)) && (functions.isValueNull(fraudSystemAccountVO.getFraudSystemId())))
        {
            FraudAccountDetailsVO accountDetailsVO = new FraudAccountDetailsVO();
            accountDetailsVO.setFraudSystemMerchantId(fraudSystemAccountVO.getAccountName());
            accountDetailsVO.setPassword(fraudSystemAccountVO.getPassword());
            pzFraudRequestVO.setFraudAccountDetailsVO(accountDetailsVO);

            PZFraudProcessor pzFraudProcessor = new PZFraudProcessor();
            pzFraudResponseVO = pzFraudProcessor.newTransaction(pzFraudRequestVO, fraudSystemAccountVO.getFraudSystemId());
            return pzFraudResponseVO;
        }
        else
        {
            Result result = new Result();
            result.setResultCode("-1");
            result.setStatus("failed");
            result.setDescription("Fraud System account not found");

            pzFraudResponseVO.setResult(result);
            return pzFraudResponseVO;
        }
    }
    public boolean calculateFraudRisk(double autoReversalScore,double fsScore)
    {
        if(fsScore>=autoReversalScore)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void checkInternalFraudRules(CommonValidatorVO commonValidatorVO)throws PZDBViolationException
    {
        transactionLogger.debug("Inside FraudChecker checkInternalFraudRules---");
        //boolean isFraud=false;
        String fraudScore="";

        FraudRuleManager fraudRuleManager = new FraudRuleManager();
        List<RuleMasterVO> internalFraudVOList = fraudRuleManager.getInternalSubLevelRiskRuleList(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        transactionLogger.debug("Inside FraudChecker Rule Size---"+internalFraudVOList.size());
        if(internalFraudVOList.size()==0)
            internalFraudVOList = fraudRuleManager.getInternalAccountLevelRiskRuleList(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        GatewayManager gatewayManager = new GatewayManager();
        GatewayAccount gatewayAccount = gatewayManager.getGatewayAccountForAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getGatewayAccount();

        ReadFraudServiceRequest readFraudServiceRequest = new ReadFraudServiceRequest();
        PZFraudRequestVO requestVO = readFraudServiceRequest.getPZFraudRequestVO(commonValidatorVO,gatewayAccount);

        InternalFraudProcessor internalFraudChecker = new InternalFraudProcessor();
        PZFraudResponseVO pzFraudResponseVO = internalFraudChecker.internalFraudCheck(requestVO,internalFraudVOList);

        commonValidatorVO.setErrorCodeListVO(pzFraudResponseVO.getErrorCodeListVO());
        commonValidatorVO.setFraud(pzFraudResponseVO.isFraud());
        commonValidatorVO.setFraudScore(fraudScore);
    }
}
