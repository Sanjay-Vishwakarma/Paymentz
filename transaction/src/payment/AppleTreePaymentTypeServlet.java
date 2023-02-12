package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TerminalVO;
import com.payment.appletree.AppleTreeCellulantPaymentGateway;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import payment.util.ReadRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Admin on 11/16/2018.
 */
public class AppleTreePaymentTypeServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AppleTreePaymentTypeServlet.class.getName());

    public void doPost(HttpServletRequest req , HttpServletResponse res) throws IOException , ServletException {


        HttpSession session = req.getSession(true);
        String ctoken       = req.getParameter("ctoken");
        session.setAttribute("ctoken", ctoken);
        Enumeration en      = req.getParameterNames();

        while (en.hasMoreElements()){
            String key   = (String) en.nextElement();
            String value = req.getParameter(key);
            transactionLogger.debug("key----"+key+"-----"+value);
        }

        BufferedReader bf   = req.getReader();
        StringBuffer sb     = new StringBuffer();
        Functions functions =  new Functions();

        String str  ="";
        while ((str = bf.readLine())!=null)
        {
            sb.append(str);
        }

        transactionLogger.error("Inside AppleTreePaymentTypeServlet JSON "+sb.toString());
        CommonValidatorVO standardKitValidatorVO    = null;
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        String email        = "";
        String phonecc      = "";
        String phoneno      = "";
        String firstName    = "";
        String lastName     = "";
        MerchantDAO merchantDAO             = new MerchantDAO();


        String remoteAddr   = Functions.getIpAddress(req);
        int serverPort      = req.getServerPort();
        String servletPath  = req.getServletPath();
        String httpProtocol = req.getScheme();
        String userAgent    = req.getHeader("User-Agent");
        String header       = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String hostName     = httpProtocol + "://" + remoteAddr;
        try
        {
            standardKitValidatorVO = ReadRequest.getSTDProcessControllerRequestParametersForSale(req);
            standardKitValidatorVO.setVersion("2");
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);

            transactionLogger.error("Inside AppleTreePaymentTypeServlet ");
            transactionLogger.error("Inside standardKitValidatorVO "+standardKitValidatorVO);

            String paymentMapStr  = req.getParameter("transDetails");
            String terminalMapStr  = req.getParameter("terminalMap");

            terminalMapStr = PzEncryptor.decryptPAN(terminalMapStr);

            transactionLogger.error("Inside standardKitValidatorVO from paymentMap String---"+paymentMapStr);
            transactionLogger.error("Inside standardKitValidatorVO from terminalMapStr String---"+terminalMapStr);

            HashMap<String,List<String>> paymentMap = new HashMap<String,List<String>>();
            List<String> stringList                 = null;
            if(paymentMapStr != null){
                String[] commSeprate    = paymentMapStr.split(",");
                for(String commStr : commSeprate){
                    if(commStr.contains("=")){
                        String[] equalSeprate = commStr.split("=");
                        if(equalSeprate.length > 0){
                            String key  = equalSeprate[0];
                            stringList  = new ArrayList<>();
                            if(equalSeprate[1].length() > 0 && equalSeprate[1].contains("_")){
                             String[] values    =   equalSeprate[1].split("_");
                                for(String commStrs :values){
                                    stringList.add(commStrs);
                                }
                            }else{
                                stringList.add(equalSeprate[1]);
                            }
                            paymentMap.put(key,stringList);
                        }
                    }
                }
            }


            HashMap<String,TerminalVO> terminalMap = new HashMap<>();
            JSONObject jsObject     = new JSONObject(terminalMapStr);
            Iterator keys           = jsObject.keys();
            while (keys.hasNext())
            {
                String key          = (String) keys.next();
                transactionLogger.error("JSON key "+key);
                String jsonStr = jsObject.getString(key);
                JSONObject terminalVOJSON  =  new JSONObject(jsonStr);
                transactionLogger.error("terminalVOJSON = "+terminalVOJSON);


                TerminalVO terminalVO = new TerminalVO();
                if(terminalVOJSON.has("memberId")){
                    terminalVO.setMemberId(terminalVOJSON.getString("memberId"));
                }
                if(terminalVOJSON.has("terminalId")){
                    terminalVO.setTerminalId(terminalVOJSON.getString("terminalId"));
                }
                if(terminalVOJSON.has("accountId")){
                    terminalVO.setAccountId(terminalVOJSON.getString("accountId"));
                }
                if(terminalVOJSON.has("paymodeId")){
                    terminalVO.setPaymodeId(terminalVOJSON.getString("paymodeId"));
                }
                if(terminalVOJSON.has("cardTypeId")){
                    terminalVO.setCardTypeId(terminalVOJSON.getString("cardTypeId"));
                }
                if(terminalVOJSON.has("paymentName")){
                    terminalVO.setPaymentName(terminalVOJSON.getString("paymentName"));
                }
                if(terminalVOJSON.has("isActive")){
                    terminalVO.setIsActive(terminalVOJSON.getString("isActive"));
                }
                if(terminalVOJSON.has("gateway")){
                    terminalVO.setGateway(terminalVOJSON.getString("gateway"));
                }
                if(terminalVOJSON.has("currency")){
                    terminalVO.setCardType(terminalVOJSON.getString("currency"));
                }
                if(terminalVOJSON.has("addressDetails")){
                    terminalVO.setAddressDetails(terminalVOJSON.getString("addressDetails"));
                }
                if(terminalVOJSON.has("addressValidation")){
                    terminalVO.setAddressValidation(terminalVOJSON.getString("addressValidation"));
                }
                if(terminalVOJSON.has("amountLimitCheckAccountLevel")){
                    terminalVO.setAmountLimitCheckAccountLevel(terminalVOJSON.getString("amountLimitCheckAccountLevel"));
                }
                if(terminalVOJSON.has("amountLimitCheckTerminalLevel")){
                    terminalVO.setAmountLimitCheckTerminalLevel(terminalVOJSON.getString("amountLimitCheckTerminalLevel"));
                }

                if(terminalVOJSON.has("cardLimitCheckAccountLevel")){
                    terminalVO.setCardLimitCheckAccountLevel(terminalVOJSON.getString("cardLimitCheckAccountLevel"));
                }
                if(terminalVOJSON.has("cardLimitCheckTerminalLevel")){
                    terminalVO.setCardLimitCheckTerminalLevel(terminalVOJSON.getString("cardLimitCheckTerminalLevel"));
                }
                if(terminalVOJSON.has("cardAmountLimitCheckAccountLevel")){
                    terminalVO.setCardAmountLimitCheckAccountLevel(terminalVOJSON.getString("cardAmountLimitCheckAccountLevel"));
                }
                if(terminalVOJSON.has("cardAmountLimitCheckTerminalLevel")){
                    terminalVO.setCardAmountLimitCheckTerminalLevel(terminalVOJSON.getString("cardAmountLimitCheckTerminalLevel"));
                }
                if(terminalVOJSON.has("minTransactionAmount")){
                    terminalVO.setMin_transaction_amount(Float.parseFloat(terminalVOJSON.getString("minTransactionAmount")));
                }
                if(terminalVOJSON.has("maxTransactionAmount")){
                    terminalVO.setMax_transaction_amount(Float.parseFloat(terminalVOJSON.getString("maxTransactionAmount")) );
                }

                terminalMap.put(key,terminalVO);

            }

            transactionLogger.error("Inside standardKitValidatorVO from terminalMap String---"+terminalMap);
            transactionLogger.error("Inside standardKitValidatorVO from paymentMap String---"+paymentMap);

            String currency     = req.getParameter("currency");
            String country      = req.getParameter("country");
            String accountId    = req.getParameter("accountId");

            if(req.getParameter("firstname") != null){
                firstName = req.getParameter("firstname");
            }
            if(req.getParameter("lastname") != null){
                lastName = req.getParameter("lastname");
            }
            if(req.getParameter("phone-CC") != null){
                phonecc = req.getParameter("phone-CC");
            }
            if(req.getParameter("telno") != null){
                phoneno = req.getParameter("telno");
            }
            if(req.getParameter("emailaddr") != null){
                email = req.getParameter("emailaddr");
            }

            if(functions.isValueNull(email)){
                standardKitValidatorVO.getAddressDetailsVO().setEmail(email);
            }
            if (functions.isValueNull(phonecc))
            {
                standardKitValidatorVO.getAddressDetailsVO().setTelnocc(phonecc);
            }
            if (functions.isValueNull(phoneno))
            {
                standardKitValidatorVO.getAddressDetailsVO().setPhone(phoneno);
            }
            if (functions.isValueNull(firstName))
            {
                standardKitValidatorVO.getAddressDetailsVO().setFirstname(firstName);
            }
            if (functions.isValueNull(lastName))
            {
                standardKitValidatorVO.getAddressDetailsVO().setLastname(lastName);
            }

            standardKitValidatorVO.getAddressDetailsVO().setCountry(country);


            AppleTreeCellulantPaymentGateway appleTree              = new AppleTreeCellulantPaymentGateway(accountId);
            HashMap<String, ArrayList<TerminalVO>> paymentTypeHM    = appleTree.getPaymentType(currency, country);
            transactionLogger.error("paymentMap before >>>>> " + paymentMap);

            Iterator<Map.Entry<String,List<String>>> iterator = paymentMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry entry     =  iterator.next();
                String paymentType  = GatewayAccountService.getPaymentTypes(entry.getKey().toString());
                transactionLogger.error("paymentType " + paymentType);
                if (!paymentTypeHM.containsKey(paymentType)) {
                    iterator.remove();
                }
            }

            String memberId    = standardKitValidatorVO.getMerchantDetailsVO().getMemberId();
            String totype      = standardKitValidatorVO.getTransDetailsVO().getTotype();
            MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
            merchantDetailsVO = merchantDAO.getMemberAndPartnerDetails(memberId, totype);

            standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            standardKitValidatorVO.setMapOfPaymentCardType(paymentMap);
            standardKitValidatorVO.setTerminalMap(terminalMap);

            if (functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()))
            {
                session.setAttribute("merchantLogoName", standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            }
            PartnerDetailsVO partnerDetailsVO   = new PartnerDetailsVO();

            partnerDetailsVO.setPartnerId(merchantDetailsVO.getPartnerId());
            partnerDetailsVO.setPartnertemplate(merchantDetailsVO.getPartnertemplate());
            commonInputValidator.setAllTemplateInformationRelatedToMerchant(merchantDetailsVO, standardKitValidatorVO.getVersion());
            commonInputValidator.setAllTemplateInformationRelatedToPartner(partnerDetailsVO, standardKitValidatorVO.getVersion());
            standardKitValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            commonInputValidator.getPaymentPageTemplateDetails(standardKitValidatorVO, session);

            transactionLogger.error("paymentMap before >>>>> " + paymentMap);

            req.setAttribute("transDetails", standardKitValidatorVO);
            req.setAttribute("ctoken", session.getAttribute("ctoken"));
            req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
            req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
            req.setAttribute("paymentTypeHM", paymentTypeHM);

            RequestDispatcher rd = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException-----",e);
        }
    }

}
