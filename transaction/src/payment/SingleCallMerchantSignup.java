package payment;

import com.directi.pg.*;
import com.manager.PartnerManager;
import com.manager.vo.PartnerDefaultConfigVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.MultipleMemberUtill;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationAccountsException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Sneha on 12/5/2015.
 */
public class SingleCallMerchantSignup extends HttpServlet
{
    Logger logger=new Logger(SingleCallMerchantSignup.class.getName());
    TransactionLogger transactionLogger=new TransactionLogger(SingleCallMerchantSignup.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.debug("Entering in SingleCallMerchantSignup doGet");
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.debug("Entering in SingleCallMerchantSignup doPost");
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.debug("Entering into SingleCallMerchantSignup doProccess::");

        PrintWriter pWriter = response.getWriter();
        TransactionUtility transactionUtility=new TransactionUtility();
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();

        String responseType="";
        String responseLength="";
        HttpSession session = request.getSession();
        String loginName = request.getParameter("loginName");
        String newPassword = request.getParameter("newPassword");
        String conPassword = request.getParameter("conPassword");
        String companyName = request.getParameter("companyName");
        String website = request.getParameter("website");
        String contactName = request.getParameter("contactName");
        String contactEmail = request.getParameter("contactEmail");
        String contactPhone = request.getParameter("contactPhone");
        String country = request.getParameter("country");
        String partnerId = request.getParameter("partnerid");
        String checksum = request.getParameter("checksum");
        String clKey ="";

        Hashtable details = new Hashtable();
        if (!ESAPI.validator().isValidInput("partnerid",partnerId,"Numbers",4,false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "Unauthorised user ", "", "",responseType,responseLength);
            return;
        }
        if (!ESAPI.validator().isValidInput("checksum", checksum, "SafeString", 50, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "Invalid request ", partnerId, "",responseType,responseLength);
            return;
        }
        try
        {
            PartnerDetailsVO partnerDetailsVO = null;
            PartnerManager partnerManager = new PartnerManager();
            partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);

            if(partnerDetailsVO==null)
            {
                transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "Unauthorised user ", partnerId, "",responseType,responseLength);
                return;
            }
            clKey = partnerDetailsVO.getPartnerKey();
            responseType = partnerDetailsVO.getResponseType();
            responseLength = partnerDetailsVO.getResponseLength();

            if(!Checksum.verifyMerchantSignUpChecksum(loginName, website, contactName, partnerId, clKey, checksum))
            {
                transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "Checksum mismatch ", "", "",responseType,responseLength);
                return;
            }
            StringBuffer errorMsg = new StringBuffer();
            if (!ESAPI.validator().isValidInput("loginName",loginName,"UserName",50,false))
            {
                errorMsg.append("Invalid loginName |");
            }
            if (!ESAPI.validator().isValidInput("newPassword",newPassword,"NewPassword",255,false))
            {
                errorMsg.append("Invalid newPassword |");
            }
            if (!ESAPI.validator().isValidInput("conPassword",conPassword,"NewPassword",255,false) || (!newPassword.equals(conPassword)))
            {
                errorMsg.append("Invalid conPassword |");
            }
            if (!ESAPI.validator().isValidInput("companyName",companyName,"SafeString",255,false))
            {
                errorMsg.append("Invalid companyName |");
            }
            if (!ESAPI.validator().isValidInput("website",website,"URL",255,false))
            {
                errorMsg.append("Invalid website |");
            }
            if (!ESAPI.validator().isValidInput("contactName",contactName,"SafeString",255,false))
            {
                errorMsg.append("Invalid contactName |");
            }
            if (!ESAPI.validator().isValidInput("contactEmail",contactEmail,"Email",255,false))
            {
                errorMsg.append("Invalid contactEmail |");
            }
            if (!ESAPI.validator().isValidInput("contactPhone",contactPhone,"Phone",255,false))
            {
                errorMsg.append("Invalid contactPhone |");
            }
            if (!ESAPI.validator().isValidInput("country",country,"SafeString",255,false))
            {
                errorMsg.append("Invalid country |");
            }

            if(errorMsg.length() > 0)
            {
                transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", errorMsg.toString(), partnerId, clKey,responseType,responseLength);
                return;
            }
            Merchants merchants = new Merchants();
            Member member = null;

            Functions functions=new Functions();

            PartnerDefaultConfigVO partnerDefaultConfigVO=partnerManager.getPartnerDefaultConfig(partnerId);
            if(partnerDefaultConfigVO == null)
            {
                logger.debug("partner default configuration not found");
                transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "partner default configuration not found", partnerId, clKey,responseType,responseLength);
                return;
            }
            else
            {
                if(!merchants.isMember(loginName) || !multipleMemberUtill.isUniqueChildMember(loginName))
                {
                    details.put("login",loginName);
                    details.put("passwd",newPassword);
                    details.put("company_name",companyName);
                    details.put("sitename",website);
                    details.put("contact_persons",contactName);
                    details.put("contact_emails",contactEmail);
                    details.put("telno",contactPhone);
                    details.put("country",country);
                    details.put("partnerid",partnerId);
                    details.put("clkey",clKey);
                    details.put("responseType",partnerDetailsVO.getResponseType());
                    details.put("responseLength",partnerDetailsVO.getResponseLength());

                    String toId="";
                    request.setAttribute("role","merchant");
                    member = addMerchant(details,partnerDefaultConfigVO);
                    if(member!=null && functions.isValueNull(String.valueOf(member.memberid)))
                    {
                        toId =String.valueOf(member.memberid);
                    }
                    transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, toId, "Y", "New merchant successfully registered", partnerId, clKey,responseType,responseLength);
                    return;
                }
                else
                {
                    transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "loginName already used please provide unique loginName", partnerId, clKey,responseType,responseLength);
                    return;
                }
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError::",systemError);

            if(systemError.getMessage().contains("Duplicate"))
            {
                transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "loginName already used please provide unique loginName", partnerId, clKey,responseType,responseLength);
                return;
            }
            else
            {
                transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "Internal error while processing your request", partnerId, clKey,responseType,responseLength);
                return;
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException:: ",e);
            transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, null, "N", "Internal error while processing your request", "", "",responseType,responseLength);
            return;
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception:: ",e);
            transactionUtility.calculateCheckSumAndWriteStatusMerchantRegistration(pWriter, "", "N", "Internal error while processing your request", partnerId, clKey,responseType,responseLength);
            return;
        }
    }
    public Member addMerchant(Hashtable details,PartnerDefaultConfigVO partnerDefaultConfigVO) throws SystemError
    {
        Merchants merchants=new Merchants();
        Member mem = null;
        try
        {
            User user = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "merchant");
            mem = merchants.addMerchant_new(user.getAccountId(),details,partnerDefaultConfigVO);
        }
        catch (Exception e)
        {
            transactionLogger.error("Add user throwing Authentication Exception ", e);
            logger.error("Add user throwing Authentication Exception ", e);

            if(e instanceof AuthenticationAccountsException)
            {
                String message=((AuthenticationAccountsException)e).getLogMessage();
                if(message.contains("Duplicate"))
                {
                    throw new SystemError("Error: " + message);
                }
            }
            try
            {
                merchants.DeleteBoth((String) details.get("login"));
            }
            catch(Exception e1)
            {
                transactionLogger.error("Exception while deletion of Details::",e1);
                logger.error("Exception while deletion of Details::",e1);
            }
            throw new SystemError("Error: " + e.getMessage());
        }
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap merchantSignupMail=new HashMap();
        merchantSignupMail.put(MailPlaceHolder.USERNAME,details.get("login"));
        merchantSignupMail.put(MailPlaceHolder.NAME,details.get("contact_persons"));
        merchantSignupMail.put(MailPlaceHolder.TOID,String.valueOf(mem.memberid));
        asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION,merchantSignupMail);
        return mem;
    }
}
