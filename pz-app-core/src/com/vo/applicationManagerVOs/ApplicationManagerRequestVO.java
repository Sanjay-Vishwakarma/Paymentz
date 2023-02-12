package com.vo.applicationManagerVOs;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 08-09-2017.
 */
@XmlRootElement(name="MerchantApplicationForm")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationManagerRequestVO
{
    @FormParam("MerchantApplicationForm.applicationId")
    String applicationId;

    @FormParam("MerchantApplicationForm.memberId")
    String memberId;

    @FormParam("MerchantApplicationForm.status")
    String status;

    @FormParam("MerchantApplicationForm.maf_Status")
    String maf_Status;

    @FormParam("MerchantApplicationForm.kyc_Status")
    String kyc_Status;

    @FormParam("MerchantApplicationForm.user")
    String user;

    @FormParam("MerchantApplicationForm.speed_user")
    String speed_user;

    @FormParam("MerchantApplicationForm.standby_user")
    String standby_user;

    @InjectParam("CompanyProfileVO")
    CompanyProfileRequestVO companyProfileRequestVO;

    @InjectParam("OwnershipProfileVO")
    OwnershipProfileRequestVO ownershipProfileRequestVO;

    @InjectParam("BusinessProfileVO")
    BusinessProfileRequestVO businessProfileVO;

    @InjectParam("BankProfileVO")
    BankProfileRequestVO bankProfileVO;

    @InjectParam("CardholderProfileVO")
    CardholderProfileRequestVO cardholderProfileVO;

    @InjectParam("ExtradetailsprofileVO")
    ExtraDetailsProfileRequestVO extradetailsprofileVO;

    //upload details
   /* HashMap<String,List<AppFileDetailsVO>> fileDetailsVOs=null;
    Map<String,AppUploadLabelVO> uploadLabelVOs=null;
    Map<String,List<AppFileDetailsVO>> submittedFileDetailsVO=null;
*/
    @FormParam("MerchantApplicationForm.isApplicationSaved")
    String isApplicationSaved;

    @FormParam("MerchantApplicationForm.appliedToModify")
    String appliedToModify;

    @FormParam("MerchantApplicationForm.notificationMessage")
    String notificationMessage;

    @FormParam("MerchantApplicationForm.messageColorClass")
    String messageColorClass;

    @FormParam("MerchantApplicationForm.speed_status")
    String speed_status;

    @FormParam("MerchantApplicationForm.speed_id")
    String speed_id;

    @FormParam("MerchantApplicationForm.createMAF")
    String createMAF;

    @FormParam("MerchantApplicationForm.createSPEED")
    String createSPEED;


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

    public CompanyProfileRequestVO getCompanyProfileRequestVO()
    {
        return companyProfileRequestVO;
    }

    public void setCompanyProfileRequestVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        this.companyProfileRequestVO = companyProfileRequestVO;
    }

    public OwnershipProfileRequestVO getOwnershipProfileRequestVO()
    {
        return ownershipProfileRequestVO;
    }

    public void setOwnershipProfileVO(OwnershipProfileRequestVO ownershipProfileVO)
    {
        this.ownershipProfileRequestVO = ownershipProfileVO;
    }

    public BusinessProfileRequestVO getBusinessProfileRequestVO()
    {
        return businessProfileVO;
    }

    public void setBusinessProfileVO(BusinessProfileRequestVO businessProfileVO)
    {
        this.businessProfileVO = businessProfileVO;
    }

    public BankProfileRequestVO getBankProfileVO()
    {
        return bankProfileVO;
    }

    public void setBankProfileVO(BankProfileRequestVO bankProfileVO)
    {
        this.bankProfileVO = bankProfileVO;
    }

    public CardholderProfileRequestVO getCardholderProfileVO()
    {
        return cardholderProfileVO;
    }

    public void setCardholderProfileVO(CardholderProfileRequestVO cardholderProfileVO)
    {
        this.cardholderProfileVO = cardholderProfileVO;
    }

    public ExtraDetailsProfileRequestVO getExtradetailsprofileVO()
    {
        return extradetailsprofileVO;
    }

    public void setExtradetailsprofileVO(ExtraDetailsProfileRequestVO extradetailsprofileVO)
    {
        this.extradetailsprofileVO = extradetailsprofileVO;
    }

   /* public HashMap<String, List<AppFileDetailsVO>> getFileDetailsVOs()
    {
        return fileDetailsVOs;
    }

    public void setFileDetailsVOs(HashMap<String, List<AppFileDetailsVO>> fileDetailsVOs)
    {
        this.fileDetailsVOs = fileDetailsVOs;
    }

    public Map<String, AppUploadLabelVO> getUploadLabelVOs()
    {
        return uploadLabelVOs;
    }

    public void setUploadLabelVOs(Map<String, AppUploadLabelVO> uploadLabelVOs)
    {
        this.uploadLabelVOs = uploadLabelVOs;
    }

    public Map<String, List<AppFileDetailsVO>> getSubmittedFileDetailsVO()
    {
        return submittedFileDetailsVO;
    }

    public void setSubmittedFileDetailsVO(Map<String, List<AppFileDetailsVO>> submittedFileDetailsVO)
    {
        this.submittedFileDetailsVO = submittedFileDetailsVO;
    }*/

    public String getIsApplicationSaved()
    {
        return isApplicationSaved;
    }

    public void setIsApplicationSaved(String isApplicationSaved)
    {
        this.isApplicationSaved = isApplicationSaved;
    }

    public String getAppliedToModify()
    {
        return appliedToModify;
    }

    public void setAppliedToModify(String appliedToModify)
    {
        this.appliedToModify = appliedToModify;
    }

    public String getNotificationMessage()
    {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage)
    {
        this.notificationMessage = notificationMessage;
    }

    public String getMessageColorClass()
    {
        return messageColorClass;
    }

    public void setMessageColorClass(String messageColorClass)
    {
        this.messageColorClass = messageColorClass;
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

    public String getCreateMAF()
    {
        return createMAF;
    }

    public void setCreateMAF(String createMAF)
    {
        this.createMAF = createMAF;
    }

    public String getCreateSPEED()
    {
        return createSPEED;
    }

    public void setCreateSPEED(String createSPEED)
    {
        this.createSPEED = createSPEED;
    }


}
