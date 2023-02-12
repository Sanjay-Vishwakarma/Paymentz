package net.partner;
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayType;
import com.manager.GatewayManager;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahima on 1/4/2020.
 */
public class AddGatewayType extends HttpServlet
{
    Logger logger=new Logger(AddGatewayType.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public  void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        logger.debug("Entering in  addGatewayType");
        Functions functions=new Functions();
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String EOL = "<BR>";
        String result = "";
        String gateway = "";
        String currency = "";
        String name = "";
        String address = "";
        String gatewaytable = "";
        String pspcode = "";
        String key = "";
        String wsservice = "";
        String wspassword = "";
        int chargepercentage = 0;
        int taxpercentage = 0;
        int withdrawalcharge = 0;
        int reversalcharge = 0;
        int chargebackcharge= 0;
        int chargesaccount= 0;
        int taxaccount = 0;
        int highriskamount = 0;

        String timedifferencenormal = "";
        String timedifferencedaylight = "";
        String partnerId="";
        String agentId="";
        String bankIp = "";
        String bank_emailid="";
        String isCvvOptional="";
        try
        {

            ValidationErrorList validationErrorList=validateMandatoryParameter(request);
            validateOptionalParameter(request,validationErrorList);
            if(!validationErrorList.isEmpty())
            {
                logger.debug("ENTER VALID DATA ");
                request.setAttribute("errormsg",validationErrorList);
                RequestDispatcher rd = request.getRequestDispatcher("/addGatewayType.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            gateway = request.getParameter("gateway");
            currency = request.getParameter("currency");
            name = request.getParameter("name");

            chargepercentage = Integer.parseInt(request.getParameter("chargepercentage"));
            withdrawalcharge = Integer.parseInt(request.getParameter("withdrawalcharge"));
            taxpercentage = Integer.parseInt(request.getParameter("taxpercentage"));
            reversalcharge = Integer.parseInt(request.getParameter("reversalcharge"));
            chargebackcharge = Integer.parseInt(request.getParameter("chargebackcharge"));
            chargesaccount = Integer.parseInt(request.getParameter("chargesaccount"));
            taxaccount = Integer.parseInt(request.getParameter("taxaccount"));
            highriskamount = Integer.parseInt(request.getParameter("highriskamount"));
            timedifferencenormal = request.getParameter("timedifferencenormal");
            timedifferencedaylight = request.getParameter("timedifferencedaylight");
            partnerId=request.getParameter("partnerid");
            agentId=request.getParameter("agentid");
            bankIp = request.getParameter("bankip");
            pspcode = request.getParameter("pspcode");
            key = request.getParameter("key");
            wsservice = request.getParameter("wsservice");
            wspassword = request.getParameter("wspassword");
            bank_emailid =request.getParameter("bank_emailid");
            isCvvOptional=request.getParameter("isCvvOptional");

            if(functions.isValueNull(request.getParameter("address")))
                address = request.getParameter("address");

            if(functions.isValueNull(request.getParameter("gatewaytablename")))
                gatewaytable = request.getParameter("gatewaytablename");

            GatewayType gatewayType=new GatewayType();

            gatewayType.setGateway(gateway);
            gatewayType.setCurrency(currency);
            gatewayType.setName(name);
            gatewayType.setTableName(gatewaytable);
            gatewayType.setChargePercentage(chargepercentage);
            gatewayType.setWithdrawalCharge(withdrawalcharge);
            gatewayType.setTaxPercentage(taxpercentage);
            gatewayType.setReversalCharge(reversalcharge);
            gatewayType.setChargebackCharge(chargebackcharge);
            gatewayType.setChargesAccount(chargesaccount);
            gatewayType.setTaxAccount(taxaccount);
            gatewayType.setHighRiskAmount(highriskamount);
            gatewayType.setAddress(address);
            gatewayType.setTime_difference_normal(timedifferencenormal);
            gatewayType.setTime_difference_daylight(timedifferencedaylight);
            gatewayType.setAgentId("1");
            gatewayType.setPartnerId(partnerId);
            gatewayType.setBank_ipaddress(bankIp);
            gatewayType.setPspCode(pspcode);
            gatewayType.setKey(key);
            gatewayType.setWsService(wsservice);
            gatewayType.setWsPassword(wspassword);
            gatewayType.setBank_emailid(bank_emailid);
            gatewayType.setIsCvvOptional(isCvvOptional);
            GatewayManager gatewayManager=new GatewayManager();
            result=gatewayManager.addNewGatewayType(gatewayType);

        }
        catch (Exception e)
        {
            logger.debug("Exception : Add gateway type : " + e);
            result = e.getMessage();
        }

        request.setAttribute("success",result);
        RequestDispatcher rd = request.getRequestDispatcher("/addGatewayType.jsp?errormsg=" + result + "&ctoken="+user.getCSRFToken());
        rd.forward(request, response);
        return;
    }

    private ValidationErrorList validateMandatoryParameter(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.CURRENCY);
        inputFieldsListMandatory.add(InputFields.NAME_SMALL);

        inputFieldsListMandatory.add(InputFields.PARTNERID);
       // inputFieldsListMandatory.add(InputFields.AGENTID);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }

    private void validateOptionalParameter(HttpServletRequest request,ValidationErrorList validationErrorList)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.GATEWAY_TABLENAME);
        inputFieldsListOptional.add(InputFields.ADDRESS_SMALL);
        inputFieldsListOptional.add(InputFields.CHARGEPERCENTAGE);
        inputFieldsListOptional.add(InputFields.WITHDRAWCHARGE);
        inputFieldsListOptional.add(InputFields.TAXPER);
        inputFieldsListOptional.add(InputFields.REVERSECHARGE);
        inputFieldsListOptional.add(InputFields.CHARGEBACKCHARGE);
        inputFieldsListOptional.add(InputFields.CHARGESACCOUNT);
        inputFieldsListOptional.add(InputFields.TAXACCOUNT);
        inputFieldsListOptional.add(InputFields.HIGHRISKAMOUNT);
        inputFieldsListOptional.add(InputFields.TIMEDIFFDAYLIGHT);
        inputFieldsListOptional.add(InputFields.TIMEDIFFNORMAL);
        inputFieldsListOptional.add(InputFields.BANKIP);
        inputFieldsListOptional.add(InputFields.Bank_EmailID);
        inputValidator.InputValidations(request,inputFieldsListOptional,validationErrorList,true);
    }
}
