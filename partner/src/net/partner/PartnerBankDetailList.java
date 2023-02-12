package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.manager.GatewayManager;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.BankApplicationMasterVO;
import com.vo.applicationManagerVOs.BankTypeVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerBankDetailList extends HttpServlet
{
    private static Logger logger= new Logger(PartnerBankDetailList.class.getName());
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions =new Functions();
        //Gateway Manager instance
        GatewayManager gatewayManager = new GatewayManager();
        ApplicationManager applicationManager = new ApplicationManager();
        //List og GatewayType
        List<BankTypeVO> bankTypeVOList=null;
        Map<String, List<BankApplicationMasterVO>> bankApplicationMasterVOs=null;
        Map<String,List<BankTypeVO>> merchantBankMappingMap=null;
        List<BankTypeVO> bankTypeVOs=null;
        Map<String,BankTypeVO> bankTypeVOMap=new HashMap<String, BankTypeVO>();
        ApplicationManagerVO applicationManagerVO=null;

        BankApplicationMasterVO bankApplicationMasterVO = null;
        BankApplicationMasterVO oldBankApplicationMasterVO=null;
        String orderBy=null;
        try
        {
            ValidationErrorList validationErrorList=validateOptionalParameter(request);

            RequestDispatcher rdError = request.getRequestDispatcher("/partnerbankdetailslist.jsp?MES=ERR&ctoken="+user.getCSRFToken());
            RequestDispatcher rdSuccess = request.getRequestDispatcher("/partnerbankdetailslist.jsp?MES=Success&ctoken="+user.getCSRFToken());

            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            if(request.getAttribute("error")!=null)
            {
                request.setAttribute("error",request.getAttribute("error"));
                rdError.forward(request,response);
                return;
            }

            bankTypeVOList = applicationManager.getListOfAllBankTypeWithAvailableTemplateName();

            bankApplicationMasterVO=new BankApplicationMasterVO();
            bankApplicationMasterVO.setMember_id(request.getParameter("memberid"));
            orderBy="dtstamp desc";
            bankApplicationMasterVOs = applicationManager.getBankApplicationMasterVOForGatewayId(bankApplicationMasterVO,orderBy,null);
            merchantBankMappingMap = applicationManager.getBankMerchantMappingDetailsByMap(bankApplicationMasterVO.getMember_id(),null);

            if(merchantBankMappingMap!=null && merchantBankMappingMap.containsKey(bankApplicationMasterVO.getMember_id()))
            {
                bankTypeVOs = merchantBankMappingMap.get(bankApplicationMasterVO.getMember_id());
                for(BankTypeVO bankTypeVO : bankTypeVOs)
                {
                    bankTypeVOMap.put(bankTypeVO.getBankId(),bankTypeVO);
                }
            }

            request.setAttribute("merchantBankMappingMap",bankTypeVOMap);
            request.setAttribute("bankTypeVOList",bankTypeVOList);
            request.setAttribute("bankApplicationMasterVOs",bankApplicationMasterVOs);

            rdSuccess.forward(request,response);


        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }

    }

    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }
}
