package payment;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.fraud.dao.FraudDAO;
import com.fraud.vo.PZFraudDocVerifyResponseVO;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by SurajT. on 2/20/2018.
 */
public class FourStopBackEndNotification extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(FourStopBackEndNotification.class.getName());
    private static Logger logger = new Logger(FourStopBackEndNotification.class.getName());
    Functions functions=new Functions();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO = new PZFraudDocVerifyResponseVO();
        FraudDAO fraudDAO = new FraudDAO();
        transactionLogger.error("--Entering into FourstopBackEndNotification--");

        StringBuilder jsonResponse = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            jsonResponse.append(str);
        }
        transactionLogger.error("----fourstop response parameters-----:" + jsonResponse);

        if (jsonResponse != null)
        {
            try
            {
                JSONObject json = null;
                json = new JSONObject(jsonResponse.toString());
                pzFraudDocVerifyResponseVO.setJsonObject(json);

                Iterator i = json.keys();
                while (i.hasNext())
                {
                    String key = (String) i.next();
                    logger.error("Response  ====> key:  " + key + "   Value:" + json.get(key));
                    transactionLogger.error("Response  ====> key:  " + key + "   Value:" + json.get(key));
                    Map readJsonData = new Gson().fromJson(String.valueOf(json), Map.class);
                    if ("score".equals(key))
                    {
                        logger.error("inside json : " + json.get(key));
                        pzFraudDocVerifyResponseVO.setScore((Double) readJsonData.get("score"));
                    }
                    else if ("reference_id".equals(key))
                    {
                        pzFraudDocVerifyResponseVO.setReference_id((String) readJsonData.get("reference_id"));
                    }
                    else if ("score_complete".equals(key))
                    {
                        pzFraudDocVerifyResponseVO.setScore_complete((Double) readJsonData.get("score_complete"));
                    }
                    else if ("kyc_source".equals(key))
                    {
                        pzFraudDocVerifyResponseVO.setKyc_source((String) readJsonData.get("kyc_source"));
                    }
                }

                if (json.has("data"))
                {
                    JSONObject json1 = json.getJSONObject("data");

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision1_Decision"))))
                        pzFraudDocVerifyResponseVO.setFiledecision1_decision((String) json1.get("FileDecision1_Decision"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision1_Classtype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision1_classtype((String) json1.get("FileDecision1_Classtype"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision1_Doctype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision1_doctype((String) json1.get("FileDecision1_Doctype"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision2_Decision"))))
                        pzFraudDocVerifyResponseVO.setFiledecision2_decision((String) json1.get("FileDecision2_Decision"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision2_Classtype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision2_classtype((String) json1.get("FileDecision2_Classtype"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision2_Doctype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision2_doctype((String) json1.get("FileDecision2_Doctype"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision3_Decision"))))
                        pzFraudDocVerifyResponseVO.setFiledecision3_decision((String) json1.get("FileDecision3_Decision"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision3_Classtype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision3_classtype((String) json1.get("FileDecision3_Classtype"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision3_Doctype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision3_doctype((String) json1.get("FileDecision3_Doctype"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision4_Decision"))))
                        pzFraudDocVerifyResponseVO.setFiledecision4_decision((String) json1.get("FileDecision4_Decision"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision4_Classtype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision4_classtype((String) json1.get("FileDecision4_Classtype"));

                    if (functions.isValueNull(String.valueOf(json1.get("FileDecision4_Doctype"))))
                        pzFraudDocVerifyResponseVO.setFiledecision4_doctype((String) json1.get("FileDecision4_Doctype"));


                }
                logger.error("getting notification url..");
                String url=fraudDAO.getNotificationUrl(pzFraudDocVerifyResponseVO);
                logger.error("notification Url : "+url);
                pzFraudDocVerifyResponseVO.setNotificationUrl(url);

                logger.error("calling update verify method");
                boolean updateVerifyDetails = fraudDAO.updateDocVerifyScore(pzFraudDocVerifyResponseVO);
                logger.error("Document verify status :" + updateVerifyDetails);

                //Sending Beckend Notification to 123sign
                AsyncFraudNotificationService asyncNotificationService = AsyncFraudNotificationService.getInstance();
                asyncNotificationService.sendNotification(pzFraudDocVerifyResponseVO,response);
                transactionLogger.debug("After sending notification---");

            }
            catch (JSONException je)
            {
                transactionLogger.error("Notification Exception :"+je);
                transactionLogger.error("Notification Exception 1:"+je.getCause());

            }

        }
    }
}
