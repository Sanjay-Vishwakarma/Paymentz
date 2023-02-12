package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.manager.GatewayManager;
import com.manager.vo.ActionVO;
import com.vo.applicationManagerVOs.BankTypeVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by Namrata B. Bari on 31/10/2019.
 */
public class DefaultGatwaySetting extends HttpServlet
{
    private Logger logger = new Logger(SingleMerchantBankMapping.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {

        HttpSession session = Functions.getNewSession(request);

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //Manager Instance
        ApplicationManager applicationManager = new ApplicationManager();
        GatewayManager gatewayManager = new GatewayManager();
        //VO instance
        ActionVO actionVO = new ActionVO();
        Map<String, List<BankTypeVO>> gatewayTypeVOListMap=null;
        List<BankTypeVO> gatewayTypeVOList=null;
        Map<String, BankTypeVO> gatewayTypeVOs=null;

        //Validation Error List
        ValidationErrorList validationErrorList=null;
        String pid = request.getParameter("partnerid");
        List<BankTypeVO> bankTypeVOs =new ArrayList<BankTypeVO>();
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/defaultGatwaySetting.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/defaultGatwaySetting.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        try
        {
             if(pid.equals(null) || pid == "")
            {
                logger.error("validation error");
                request.setAttribute("error","Invalid Partrner Id");
                rdError.forward(request,response);
                return;
            }
            String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));

            if(Roles.contains("superpartner")){

            }else{
                pid = String.valueOf(session.getAttribute("merchantid"));
            }
            System.out.println(pid);
            actionVO.setAllContentAuto(request.getParameter("action"));

            gatewayTypeVOs=new HashMap<String,BankTypeVO>();

            Connection conn=null;

            boolean update=false;

            String pgTypeIds[]=null;

            if (actionVO.isUpdate())
                {
                    pgTypeIds =request.getParameterValues("pgTypeId");

                    if(pgTypeIds!=null && pgTypeIds.length>0)
                    {
                        for (String pgTypeId : pgTypeIds)
                        {

                            logger.error("Inside PgTypeId");
                            BankTypeVO bankTypeVO = new BankTypeVO();
                            bankTypeVO.setBankId(pgTypeId);
                            bankTypeVOs.add(bankTypeVO);
                        }
                        applicationManager.updateDefaultApplicationGatewayForPartnerAndPgTypeId(pid, null, false);
                        for (BankTypeVO bankTypeVO : bankTypeVOs)
                        {
                            update = applicationManager.updateDefaultApplicationGatewayForPartnerAndPgTypeId(pid, bankTypeVO.getBankId(), true);
                        }
                    }
                    request.setAttribute("update",update);
                }
                else  if (actionVO.isView())
                {
                    gatewayTypeVOList = applicationManager.getAllGatewayMappedToPartner(pid);
                    for (BankTypeVO bankTypeVO : gatewayTypeVOList)
                    {
                        if (bankTypeVO.isDefaultApplication())
                            gatewayTypeVOs.put(bankTypeVO.getBankId(), bankTypeVO);
                    }
                    request.setAttribute("partnerMappedBank",gatewayTypeVOs);
                }
           /* if(!actionVO.isUpdate())
            {

                if (gatewayTypeVOs.isEmpty())
                {
                    actionVO.setAdd();
                }
                else
                {
                    actionVO.setEdit();
                }
            }*/

            /*request.setAttribute("actionVO",actionVO);*/
            rdSuccess.forward(request,response);
            return;
        }
        catch (PZDBViolationException e)
        {
            logger.error("Db violation exception while getting plane details", e);
            PZExceptionHandler.handleDBCVEException(e, null, null);
            request.setAttribute("catchError", "Kindly check for the Member Bank Mapping after sometime");
            rdError.forward(request, response);
            return;
        }
    }

    private ValidationErrorList validateMandatoryParameter(HttpServletRequest request)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.SMALL_ACTION);

        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputMandatoryParameter,validationErrorList,false);
        inputMandatoryParameter=new ArrayList<InputFields>();

        if(validationErrorList.isEmpty())
        {
            ActionVO actionVO = new ActionVO();
            actionVO.setAllContentAuto(request.getParameter("action"));

            if(!actionVO.isUpdate())
            {
                inputMandatoryParameter.add(InputFields.MEMBERID);
                inputValidator.InputValidations(request, inputMandatoryParameter, validationErrorList, actionVO.isAdd());
            }
        }
        return validationErrorList;
    }


}
