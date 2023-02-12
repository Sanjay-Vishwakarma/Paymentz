package com.vo.applicationManagerVOs;

import com.directi.pg.Functions;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.vo.requestVOs.Authentication;
import com.vo.requestVOs.AuthenticationVO;
import com.vo.requestVOs.Merchant;
import com.vo.requestVOs.MerchantVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/20/15
 * Time: 8:03 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="MerchantApplicationForm")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationManagerVO
{

    @XmlElement(name="applicationId")
    String applicationId;

    @XmlElement(name="memberId")
    String memberId;


    @XmlElement(name="status")
    String status;

    @XmlElement(name="maf_Status")
    String maf_Status;

    @XmlElement(name="kyc_Status")
    String kyc_Status;

    @XmlElement(name="user")
    String user;

    @XmlElement(name="speed_user")
    String speed_user;

    @XmlElement(name="standby_user")
    String standby_user;

    @XmlElement(name="CompanyProfileVO")
    CompanyProfileVO companyProfileVO;

    @XmlElement(name="OwnershipProfileVO")
    OwnershipProfileVO ownershipProfileVO;

    @XmlElement(name="BusinessProfileVO")
    BusinessProfileVO businessProfileVO;

    @XmlElement(name="BankProfileVO")
    BankProfileVO bankProfileVO;

    @XmlElement(name="CardholderProfileVO")
    CardholderProfileVO  cardholderProfileVO;

    @XmlElement(name="ExtradetailsprofileVO")
    ExtraDetailsProfileVO extradetailsprofileVO;

    @XmlElement(name="MerchantVO")
    MerchantVO merchantVO;

    @XmlElement(name="Merchant")
    Merchant merchant;



    @XmlElement(name="AuthenticationVO")
    AuthenticationVO authenticationVO;

    @XmlElement(name="Authentication")
    Authentication authentication;

    @XmlElement(name="ErrorCodeListVO")
    private ErrorCodeListVO errorCodeListVO ;

    //upload details
 /*  HashMap<String,List<AppFileDetailsVO>> fileDetailsVOs=null;
    Map<String,AppUploadLabelVO> uploadLabelVOs=null;
    Map<String,List<AppFileDetailsVO>> submittedFileDetailsVO=null;*/

   Map<String,FileDetailsListVO> fileDetailsVOs=null;
    Map<String,AppUploadLabelVO> uploadLabelVOs=null;
    Map<String,FileDetailsListVO> submittedFileDetailsVO=null;

    @XmlElement(name="isApplicationSaved")
    String isApplicationSaved;

    @XmlElement(name="appliedToModify")
    String appliedToModify;

    @XmlElement(name="notificationMessage")
    String notificationMessage;

    @XmlElement(name="messageColorClass")
    String messageColorClass;

    @XmlElement(name="speed_status")
    String speed_status;

    @XmlElement(name="speed_id")
    String speed_id;

    @XmlElement(name="createMAF")
    String createMAF;

    @XmlElement(name="createSPEED")
    String createSPEED;

    @XmlElement(name="partnerid")
    String partnerid;

    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId(String applicationId)
    {
        this.applicationId = applicationId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public CompanyProfileVO getCompanyProfileVO()
    {
        return companyProfileVO;
    }

    public void setCompanyProfileVO(CompanyProfileVO companyProfileVO)
    {
        this.companyProfileVO = companyProfileVO;
    }

    public OwnershipProfileVO getOwnershipProfileVO()
    {
        return ownershipProfileVO;
    }

    public void setOwnershipProfileVO(OwnershipProfileVO ownershipProfileVO)
    {
        this.ownershipProfileVO = ownershipProfileVO;
    }

    public BusinessProfileVO getBusinessProfileVO()
    {
        return businessProfileVO;
    }

    public void setBusinessProfileVO(BusinessProfileVO businessProfileVO)
    {
        this.businessProfileVO = businessProfileVO;
    }

    public BankProfileVO getBankProfileVO()
    {
        return bankProfileVO;
    }

    public void setBankProfileVO(BankProfileVO bankProfileVO)
    {
        this.bankProfileVO = bankProfileVO;
    }

    public CardholderProfileVO getCardholderProfileVO()
    {
        return cardholderProfileVO;
    }

    public void setCardholderProfileVO(CardholderProfileVO cardholderProfileVO)
    {
        this.cardholderProfileVO = cardholderProfileVO;
    }

    public ExtraDetailsProfileVO getExtradetailsprofileVO()
    {
        return extradetailsprofileVO;
    }

    public void setExtradetailsprofileVO(ExtraDetailsProfileVO extradetailsprofileVO)
    {
        this.extradetailsprofileVO = extradetailsprofileVO;
    }


    public String getApplicationSaved()
    {
        return isApplicationSaved;
    }

    public void setApplicationSaved(String applicationSaved)
    {
        isApplicationSaved = applicationSaved;
    }

    public String getAppliedToModify()
    {
        return appliedToModify;
    }

    public void setAppliedToModify(String appliedToModify)
    {
        this.appliedToModify = appliedToModify;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMaf_Status()
    {
        return maf_Status;
    }

    public void setMaf_Status(String maf_Status)
    {
        this.maf_Status = maf_Status;
    }

    public String getKyc_Status()
    {
        return kyc_Status;
    }

    public void setKyc_Status(String kyc_Status)
    {
        this.kyc_Status = kyc_Status;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getSpeed_user()
    {
        return speed_user;
    }

    public void setSpeed_user(String speed_user)
    {
        this.speed_user = speed_user;
    }

    public String getStandby_user()
    {
        return standby_user;
    }

    public void setStandby_user(String standby_user)
    {
        this.standby_user = standby_user;
    }

    public String getNotificationMessage()
    {
        return notificationMessage;
    }

    public void setNotificationMessage(boolean success,String successor,String defaultMessage)
    {
        Functions functions = new Functions();
        if(success)
        {
            if(functions.isValueNull(successor) && functions.isValueNull(defaultMessage))
            {
                this.notificationMessage = successor+" "+ defaultMessage;
            }
            else if(!functions.isValueNull(successor) && functions.isValueNull(defaultMessage))
            {
                this.notificationMessage = defaultMessage;
            }
            else if(functions.isValueNull(successor) && !functions.isValueNull(defaultMessage))
            {
                this.notificationMessage = successor+ " Saved Successfully.";
            }
            else
            {
                this.notificationMessage = "Saved Successfully.";
            }
            messageColorClass="txtboxconfirm";
        }
        else
        {
            this.notificationMessage = defaultMessage;
            messageColorClass="txtboxerror";
        }
    }

    public String getSpeed_status()
    {
        return speed_status;
    }

    public void setSpeed_status(String speed_status)
    {
        this.speed_status = speed_status;
    }

    public String getSpeed_id()
    {
        return speed_id;
    }

    public void setSpeed_id(String speed_id)
    {
        this.speed_id = speed_id;
    }

    public void setNotificationMessage(String message)
    {
        this.notificationMessage=message;
    }

    public String getMessageColorClass()
    {
        return this.messageColorClass;
    }
    public void setMessageColorClass(String className)
    {
       this.messageColorClass=className;
    }

    public String getCreateMAF() {return createMAF;}
    public void setCreateMAF(String createMAF)
    {
        this.createMAF = createMAF;
    }

    public String getCreateSPEED() {return createSPEED;}
    public void setCreateSPEED(String createSPEED)
    {
        this.createSPEED = createSPEED;
    }


    public Map<String, FileDetailsListVO> getFileDetailsVOs()
    {
        return fileDetailsVOs;
    }

    public void setFileDetailsVOs(Map<String, FileDetailsListVO> fileDetailsVOs)
    {
        this.fileDetailsVOs = fileDetailsVOs;
    }

    public Map<String, FileDetailsListVO> getSubmittedFileDetailsVO()
    {
        return submittedFileDetailsVO;
    }

    public void setSubmittedFileDetailsVO(Map<String, FileDetailsListVO> submittedFileDetailsVO)
    {
        this.submittedFileDetailsVO = submittedFileDetailsVO;
    }

    public Map<String, AppUploadLabelVO> getUploadLabelVOs()
    {
        return uploadLabelVOs;
    }

    public void setUploadLabelVOs(Map<String, AppUploadLabelVO> uploadLabelVOs)
    {
        this.uploadLabelVOs = uploadLabelVOs;
    }


    public MerchantVO getMerchantVO()
    {
        return merchantVO;
    }

    public void setMerchantVO(MerchantVO merchantVO)
    {
        this.merchantVO = merchantVO;
    }

    public AuthenticationVO getAuthenticationVO()
    {
        return authenticationVO;
    }

    public void setAuthenticationVO(AuthenticationVO authenticationVO)
    {
        this.authenticationVO = authenticationVO;
    }


    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    public void setErrorCodeListVO(ErrorCodeListVO errorCodeListVO)
    {
        this.errorCodeListVO = errorCodeListVO;
    }


    public Merchant getMerchant()
    {
        return merchant;
    }

    public void setMerchant(Merchant merchant)
    {
        this.merchant = merchant;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public String getIsApplicationSaved()
    {
        return isApplicationSaved;
    }

    public void setIsApplicationSaved(String isApplicationSaved)
    {
        this.isApplicationSaved = isApplicationSaved;
    }

    public String getPartnerid()
    {
        return partnerid;
    }

    public void setPartnerid(String partnerid)
    {
        this.partnerid = partnerid;
    }
}

