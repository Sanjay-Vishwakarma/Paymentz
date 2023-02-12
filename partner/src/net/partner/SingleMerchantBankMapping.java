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
 * Created by admin on 1/27/2016.
 */
public class SingleMerchantBankMapping extends HttpServlet
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

        RequestDispatcher rdSuccess= request.getRequestDispatcher("/merchantmappingbank.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/merchantmappingbank.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            validationErrorList=validateMandatoryParameter(request);
            if(!validationErrorList.isEmpty())
            {
                logger.error("validation error");
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            gatewayTypeVOs=new HashMap<String,BankTypeVO>();
            String pid = request.getParameter("partnerid");
            String memberid=request.getParameter("memberid");
            String partner_id = session.getAttribute("merchantid").toString();
            Connection conn=null;
            String partnerId=null;
            try
            {
                conn = Database.getConnection();
                String query = "SELECT partnerId FROM members WHERE memberid=?";
                PreparedStatement pstmt =conn.prepareStatement(query);
                pstmt.setString(1,memberid);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next())
                {
                    partnerId = rs.getString("partnerId");
                }
            }
            catch(Exception e)
            {
                logger.error("Exception while loading paymodeids", e);
            }
            finally
            {
                Database.closeConnection(conn);
            }
            if(!actionVO.isUpdate())
            {
                gatewayTypeVOListMap = applicationManager.getBankMerchantMappingDetailsByMap(request.getParameter(InputFields.MEMBERID.toString()), null);

                logger.debug("TOID:::"+request.getParameter(InputFields.MEMBERID.toString())+" ");

                if(gatewayTypeVOListMap.containsKey(request.getParameter(InputFields.MEMBERID.toString())))
                {
                    gatewayTypeVOList=gatewayTypeVOListMap.get(request.getParameter(InputFields.MEMBERID.toString()));
                    for(BankTypeVO bankTypeVO : gatewayTypeVOList)
                    {
                        logger.debug("GATEWAY:::"+bankTypeVO.getBankId());
                        gatewayTypeVOs.put(bankTypeVO.getBankId(),bankTypeVO);
                    }
                }
            }
            else
            {
                gatewayTypeVOList = applicationManager.getAllGatewayMappedToPartner(partnerId);
                for(BankTypeVO bankTypeVO : gatewayTypeVOList)
                {
                    if(bankTypeVO.isDefaultApplication())
                        gatewayTypeVOs.put(bankTypeVO.getBankName(), bankTypeVO);
                }
            }

            if(!actionVO.isUpdate())
            {

                if (gatewayTypeVOs.isEmpty())
                {
                    actionVO.setAdd();
                }
                else
                {
                    actionVO.setEdit();
                }
            }
            request.setAttribute("memberBankMap",gatewayTypeVOs);
            request.setAttribute("partnerId",partnerId);
            request.setAttribute("actionVO",actionVO);
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
