package com.payment.Ecommpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.response.PZInquiryResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.payment.Ecommpay.EcommpayUtils.calculateSignature;

/**
 * Created by Sagar on 6/13/2020.
 */
public class EcommpayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(EcommpayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "ecommpay";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ecommpay");
    private static Logger log = new Logger(EcommpayPaymentGateway.class.getName());

    public EcommpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("EcommpayPaymentGateway :: Inside processSale ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        RecurringBillingVO recurringBillingVO =commRequestVO.getRecurringBillingVO();
        int mId = Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String saleurl = "";
        String amount = "0";
        int year = Integer.parseInt(cardDetailsVO.getExpYear());
        int month = Integer.parseInt(cardDetailsVO.getExpMonth());
        String cardnum = cardDetailsVO.getCardNum();
        String card_holder = "";
        String customerid = addressDetailsVO.getCustomerid();
        String country = addressDetailsVO.getCountry();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String phone = addressDetailsVO.getPhone();
        String day_of_birth = addressDetailsVO.getBirthdate();
        String first_name = addressDetailsVO.getFirstname();
        String last_name = addressDetailsVO.getLastname();
        String email = addressDetailsVO.getEmail();
        String ip_address = addressDetailsVO.getCardHolderIpAddress();
        String cvv = cardDetailsVO.getcVV();
        String street = addressDetailsVO.getStreet();
        String currency = commTransactionDetailsVO.getCurrency();
        String recurringType = commRequestVO.getRecurringBillingVO().getRecurringType();
        String termUrl = "";
        String notifyUrl = RB.getString("NOTIFY_URL");
        String recurringtype=recurringBillingVO.getRecurringType();
        transactionLogger.error("recurringtype->"+recurringtype);
        if (functions.isValueNull(addressDetailsVO.getBirthdate()))
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
            try
            {
                if (!addressDetailsVO.getBirthdate().contains("-"))
                {
                    day_of_birth = dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));
                }
                else
                {
                    day_of_birth = dateFormat2.format(dateFormat.parse(addressDetailsVO.getBirthdate()));
                }
            }
            catch (ParseException e)
            {
                transactionLogger.error("Parse Exception-->" + trackingID + "--", e);
            }
        }

        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                saleurl = RB.getString("TEST_SALE_URL");
            }
            else
            {
                saleurl = RB.getString("LIVE_SALE_URL");
            }

           /* if (functions.isValueNull(commMerchantVO.getHostUrl()))//for whitelist
            {
                termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
                transactionLogger.error("From HOST_URL----" + termUrl);
            }
            else
            {*/
                termUrl = RB.getString("TERM_URL") + trackingID;
                transactionLogger.error("From RB TERM_URL ----" + termUrl);
            //}

            if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
            else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
            else
                amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());

            TreeMap<String, Object> requestHashMap = new TreeMap<>();
            TreeMap<String, Object> requestHashMaplog = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);

            TreeMap<String, Object> card = new TreeMap<>();

            card.put("pan", cardnum);
            card.put("year", year);
            card.put("month", month);
            card.put("cvv", cvv);
            card.put("card_holder", first_name + " " + last_name);

            TreeMap<String, Object> cardlog = new TreeMap<>();

            cardlog.put("pan", functions.maskingPan(cardnum));
            cardlog.put("year", functions.maskingNumber(String.valueOf(year)));
            cardlog.put("month", functions.maskingNumber(String.valueOf(month)));
            cardlog.put("cvv", functions.maskingNumber(cvv));
            cardlog.put("card_holder", first_name + " " + last_name);

            TreeMap<String, Object> customer = new TreeMap<>();
            if (functions.isValueNull(customerid))
            {
                customer.put("id", customerid);
            }
            if (functions.isValueNull(country))
            {
                customer.put("country", country);
            }
            if (functions.isValueNull(city))
            {
                customer.put("city", city);
            }
            if (functions.isValueNull(day_of_birth))
            {
                customer.put("day_of_birth", day_of_birth);
            }
            if (functions.isValueNull(phone))
            {
                customer.put("phone", phone);
            }
            if (functions.isValueNull(first_name))
            {
                customer.put("first_name", first_name);
            }
            if (functions.isValueNull(last_name))
            {
                customer.put("last_name", last_name);
            }

            if (functions.isValueNull(email))
            {
                customer.put("email", email);
            }
            if (functions.isValueNull(ip_address))
            {
                customer.put("ip_address", ip_address);
            }
            if (functions.isValueNull(street))
            {
                customer.put("street", street);
            }
            if (functions.isValueNull(state))
            {
                customer.put("state", state);
            }

            TreeMap<String, Object> payment = new TreeMap<>();
            if (functions.isValueNull(amount))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }

            TreeMap<String, Object> acs_return_url = new TreeMap<>();
            acs_return_url.put("return_url", termUrl);
            acs_return_url.put("3ds_notification_url", notifyUrl);

            TreeMap<String, Object> return_url = new TreeMap<>();
            return_url.put("success", termUrl + "&status=success");
            return_url.put("decline", termUrl + "&status=decline");

            if("INITIAL".equals(recurringType)||"Manual".equalsIgnoreCase(recurringType))
            {
                TreeMap<String, Object> recurring = new TreeMap<>();
                recurring.put("register",true);
                //  recurring.put("type","R");
                //recurring.put("scheduled_payment_id",trackingID);
                requestHashMap.put("recurring",recurring);
                requestHashMap.put("recurring_register",true);
            }


            requestHashMap.put("general", general);
            requestHashMap.put("card", card);
            requestHashMap.put("customer", customer);
            requestHashMap.put("payment", payment);
            requestHashMap.put("acs_return_url", acs_return_url);
            requestHashMap.put("return_url", return_url);

            requestHashMaplog.put("general", general);
            requestHashMaplog.put("card", cardlog);
            requestHashMaplog.put("customer", customer);
            requestHashMaplog.put("payment", payment);
            requestHashMaplog.put("acs_return_url", acs_return_url);
            requestHashMaplog.put("return_url", return_url);

            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            String requestlog = gson.toJson(requestHashMaplog);
            transactionLogger.error("processSale Request-->" + trackingID + "--" + requestlog);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(saleurl, request);
            transactionLogger.error("processSale Response-->" + trackingID + "--" + response);

            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Date d=new Date();
                    transactionLogger.error("start first sleepThread-->"+trackingID + "--" +d.getTime());
                    Thread.sleep(1500);
                    transactionLogger.error("end first sleepThread-->"+trackingID + "--" +(new Date().getTime()));
                    transactionLogger.error("Diff first sleepThread-->"+trackingID + "--" +(new Date().getTime()-d.getTime()));
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));

                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException--" + trackingID + "--", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--" + trackingID + "--", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException--" + trackingID + "--", e);
        }


        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("EcommpayPaymentGateway :: Inside processRefund ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        int mId = Integer.parseInt(gatewayAccount.getMerchantId());
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String description = commTransactionDetailsVO.getOrderDesc();
        String amount = "0";
        String currency = commTransactionDetailsVO.getCurrency();
        String refundurl = "";
        String notifyUrl = RB.getString("NOTIFY_URL");
        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                refundurl = RB.getString("TEST_REFUND_URL");
            }
            else
            {
                refundurl = RB.getString("LIVE_REFUND_URL");
            }


            if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
            else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
            else
                amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());

            TreeMap<String, Object>requestHashMap = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);


            TreeMap<String, Object> payment = new TreeMap<>();
            if (functions.isValueNull(description))
            {
                payment.put("description", description);
            }
            if (functions.isValueNull(String.valueOf(amount)))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }
            requestHashMap.put("general", general);
            requestHashMap.put("payment", payment);

            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            transactionLogger.error("processRefund Request-->" + trackingID + "--" + request);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(refundurl, request);
            transactionLogger.error("processRefund Response-->" + trackingID + "--" + response);


            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                }
                else
                {
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" + trackingID + "--", e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException--" + trackingID + "--", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--" + trackingID + "--", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException--" + trackingID + "--", e);
        }
        return commResponseVO;
    }
    //Inquiry
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("inside processQuery");
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        Gson gson = new Gson();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        int mId = Integer.parseInt(gatewayAccount.getMerchantId());
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String sleepTime = RB.getString("THREAD_SLEEP_TIME");
        transactionLogger.error("MID DETAILS:::::::: secretkey" + secretkey + " username" + username);


        String statusurl = "";
        String pa_req = "";
        String acs_url = "";
        String md = "";
        String term_url = "";
        String description = "";
        String amount = "0";
        String transactiontype="";
        String transactionid="";
        String id="";
        String method="";
        // String date="";
        String date=commResponseVO.getBankTransactionDate();
        String currency = commTransactionDetailsVO.getCurrency();
        boolean isTest = gatewayAccount.isTest();

        if (isTest)
        {
            statusurl = RB.getString("TEST_STATUS_URL");
            transactionLogger.error("InquiryRequest TESTURL::::::::" + statusurl);

        }
        else
        {
            statusurl = RB.getString("LIVE_STATUS_URL");
            transactionLogger.error("InquiryRequest LIVEURL::::::::" + statusurl);

        }
        if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
            amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
        else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
            amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
        else
            amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());

        try
        {
            TreeMap<String, TreeMap<String, Object>> requestStatusHashMap = new TreeMap<>();
            TreeMap<String, Object> statusGeneral = new TreeMap<>();
            statusGeneral.put("project_id", mId);
            statusGeneral.put("payment_id", trackingID);
            TreeMap<String, Object> payment = new TreeMap<>();
            if (functions.isValueNull(amount))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }

            requestStatusHashMap.put("payment",payment);
            requestStatusHashMap.put("general", statusGeneral);
            String statusSignature = calculateSignature(gson.toJson(requestStatusHashMap), secretkey);
            statusGeneral.put("signature", statusSignature);

            String statusRequest = gson.toJson(requestStatusHashMap);
            transactionLogger.error("processQuery statusRequest-->" + trackingID + "--" + statusRequest);
            String statusResponse = EcommpayUtils.doPostHTTPSURLConnectionClient(statusurl, statusRequest);
            transactionLogger.error("processQuery statusResponse-->" + trackingID + "--" + statusResponse);

            if (functions.isValueNull(statusResponse))
            {
                JSONObject statusResponseJSON = new JSONObject(statusResponse);
                transactionLogger.error("processQuery statusResponseJSON-->" + statusResponseJSON);

                if (statusResponseJSON.has("payment"))
                {
                    String status = statusResponseJSON.getJSONObject("payment").getString("status");
                    if (statusResponseJSON.has("operations"))
                    {

                        Object o = statusResponseJSON.get("operations");
                        if (o instanceof JSONArray)
                        {
                            JSONArray operations = statusResponseJSON.getJSONArray("operations");

                            if (operations.length() > 0)
                            {
                                JSONObject jsonObject = (JSONObject) operations.get(0);
                                JSONObject provider = jsonObject.getJSONObject("provider");
                                if (provider.has("auth_code"))
                                {
                                    commResponseVO.setResponseHashInfo(provider.getString("auth_code"));
                                    commResponseVO.setAuthCode(provider.getString("auth_code"));
                                }
                                if (provider.has("payment_id"))
                                {
                                    commResponseVO.setTransactionId(provider.getString("payment_id"));
                                    transactionid = provider.getString("payment_id");
                                }
                                if (jsonObject.has("eci"))
                                {
                                    commResponseVO.setEci(jsonObject.getString("eci"));
                                }
                                if (jsonObject.has("code"))
                                {
                                    commResponseVO.setErrorCode(jsonObject.getString("code"));
                                }
                                if (jsonObject.has("message"))
                                {
                                    description = jsonObject.getString("message");
                                }
                                if (jsonObject.has("type"))
                                {
                                    transactiontype=jsonObject.getString("type");
                                }
                                if(jsonObject.has("date"))
                                {
                                    date = jsonObject.getString("date");
                                }
                            }
                        }
                    }
                    if (statusResponseJSON.has("acs"))
                    {
                        JSONObject acs = statusResponseJSON.getJSONObject("acs");

                        if (acs.has("pa_req"))
                        {
                            pa_req = acs.getString("pa_req");
                        }
                        if (acs.has("acs_url"))
                        {
                            acs_url = acs.getString("acs_url");
                        }
                        if (acs.has("md"))
                        {
                            md = acs.getString("md");
                        }
                        if (acs.has("term_url"))
                        {
                            term_url = acs.getString("term_url");
                        }
                    }
                    if (statusResponseJSON.has("redirect_data"))
                    {
                        JSONObject redirect_data=statusResponseJSON.getJSONObject("redirect_data");
                        if(redirect_data.has("url"))
                            acs_url=redirect_data.getString("url");
                        if(redirect_data.has("method"))
                            method=redirect_data.getString("method");
                    }
                    if(statusResponseJSON.has("recurring"))
                    {
                        JSONObject recurring=statusResponseJSON.getJSONObject("recurring");
                        if(recurring.has("id"))
                        {
                            id=recurring.getString("id");
                        }
                    }

                    transactionLogger.error("status===============" + status);
                    transactionLogger.error("url===============" + acs_url);
                    if ("processing".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setRemark("Transaction is pending");
                        commResponseVO.setDescription("Transaction is pending");
                        transactionLogger.error(" inside Transaction is pending");
                        if ("pending".equalsIgnoreCase(commResponseVO.getStatus()))
                        {
                            Date d=new Date();
                            transactionLogger.error("sale sleepTime-->"+trackingID + "--" +sleepTime);
                            transactionLogger.error("second first sleepThread-->"+trackingID + "--" +(d.getTime()));
                            Thread.sleep(Long.parseLong(sleepTime));
                            transactionLogger.error("end first sleepThread-->"+trackingID + "--" +(new Date().getTime()));
                            transactionLogger.error("Diff first sleepThread-->"+trackingID + "--" +(new Date().getTime()-d.getTime()));
                            commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                        }

                    }
                    else if ("success".equalsIgnoreCase(status) || "awaiting capture".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if(functions.isValueNull(description))
                        {
                            commResponseVO.setDescription(description);
                            commResponseVO.setRemark(description);
                        }else
                        {
                            commResponseVO.setDescription("Transaction successful");
                            commResponseVO.setRemark("Transaction successful");
                        }
                        commResponseVO.setMerchantId(String.valueOf(mId));
                        commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTransactionType(transactiontype);
                        commResponseVO.setBankTransactionDate(date);
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setResponseHashInfo(id);
                        transactionLogger.error(" inside Transaction successful");

                    }
                    else if ("awaiting 3ds result".equalsIgnoreCase(status) || "awaiting redirect result".equalsIgnoreCase(status))
                    {
                        HashMap<String,String> threeDsDataMap=EcommpayUtils.get3DsData(trackingID);
                        transactionLogger.error("size===============" + threeDsDataMap.size());
                        if(!functions.isValueNull(acs_url))
                            acs_url=threeDsDataMap.get("acs_url");
                        if(!functions.isValueNull(md))
                            md=threeDsDataMap.get("md");
                        if(!functions.isValueNull(pa_req))
                            pa_req=threeDsDataMap.get("pa_req");
                        if(!functions.isValueNull(term_url))
                            term_url=threeDsDataMap.get("term_url");
                        if(!functions.isValueNull(acs_url))
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setRemark("Transaction is pending");
                            commResponseVO.setDescription("Transaction is pending");
                            transactionLogger.error(" inside Transaction is pending");
                            Date d=new Date();
                            transactionLogger.error("sale sleepTime-->"+trackingID + "--" +sleepTime);
                            transactionLogger.error("second first sleepThread-->"+trackingID + "--" +(d.getTime()));
                            Thread.sleep(Long.parseLong(sleepTime));
                            transactionLogger.error("end first sleepThread-->"+trackingID + "--" +(new Date().getTime()));
                            transactionLogger.error("Diff first sleepThread-->"+trackingID + "--" +(new Date().getTime()-d.getTime()));
                            commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                        }
                        commResponseVO.setUrlFor3DRedirect(acs_url);
                        commResponseVO.setMd(md);
                        commResponseVO.setPaReq(pa_req);
                        commResponseVO.setTerURL(term_url);
                        if(functions.isValueNull(method))
                            commResponseVO.setMethod(method);
                        else
                            commResponseVO.setMethod("POST");
                        commResponseVO.setDescription("Transaction is Pending");
                        commResponseVO.setStatus("pending3DConfirmation");
                        transactionLogger.error(" inside pending3DConfirmation Transaction is Pending");

                    }
                    else if ("decline".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription(description);
                        commResponseVO.setRemark(description);
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setTransactionId(transactionid);
                        transactionLogger.error(" inside failed");
                    }
                    else if ("partially refunded".equalsIgnoreCase(status) || "refunded".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription(description);
                        commResponseVO.setRemark(description);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setTransactionId(transactionid);
                        transactionLogger.error(" refund success");


                    }
                    else if ("canceled".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription(description);
                        commResponseVO.setRemark(description);
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setTransactionId(transactionid);
                        transactionLogger.error("canceled success");

                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                        transactionLogger.error(" else Transaction Failed");
                    }
                }
            }
            else
            {
                commResponseVO.setDescription("Transaction Failed");
                commResponseVO.setRemark("Transaction Failed");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("processQuery JSONException e--for--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("processQuery InterruptedException e--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Inside processCommon3DSaleConfirmation-----");
        Comm3DRequestVO comm3DRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        String pares = comm3DRequestVO.getPaRes();
        String MD = comm3DRequestVO.getMd();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        int mId = Integer.parseInt(gatewayAccount.getMerchantId());
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String sale3DUrl = "";
        String sleepTime=RB.getString("THREAD_SLEEP_TIME");

        if (isTest)
        {
            sale3DUrl = RB.getString("TEST_SALE3D_URL");
        }
        else
        {
            sale3DUrl = RB.getString("LIVE_SALE3D_URL");
        }

        try
        {
            TreeMap<String, Object> requestHashMap = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);
            requestHashMap.put("general", general);
            requestHashMap.put("pares", pares);
            requestHashMap.put("md", MD);
            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            transactionLogger.error("processCommon3DSaleConfirmation Request-->" + trackingID + "--" + request);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(sale3DUrl, request);
            transactionLogger.error("processCommon3DSaleConfirmation Response-->" + trackingID + "--" + response);

            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                    if ("pending".equalsIgnoreCase(commResponseVO.getStatus()) || "pending3DConfirmation".equalsIgnoreCase(commResponseVO.getStatus()))
                    {
                        transactionLogger.error("3D sale sleepTime-->"+trackingID + "--" +sleepTime);
                        Thread.sleep(Long.parseLong(sleepTime));
                        commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (PZGenericConstraintViolationException e1)
        {
            transactionLogger.error("processCommon3DSaleConfirmation PZGenericConstraintViolationException e--for--" + trackingID + "--", e1);
        }
        catch (JSONException e1)
        {
            transactionLogger.error("processCommon3DSaleConfirmation JSONException e--for--" + trackingID + "--", e1);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("processCommon3DSaleConfirmation InterruptedException e--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Inside processAuth3DConfirmation-----");
        Comm3DRequestVO comm3DRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        String pares = comm3DRequestVO.getPaRes();
        String MD = comm3DRequestVO.getMd();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        int mId = Integer.parseInt(gatewayAccount.getMerchantId());
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String auth3DUrl = "";
        String sleepTime=RB.getString("THREAD_SLEEP_TIME");

        if (isTest)
        {
            auth3DUrl = RB.getString("TEST_AUTH3D_URL");
        }
        else
        {
            auth3DUrl = RB.getString("LIVE_AUTH3D_URL");
        }
        try
        {
            TreeMap<String, Object> requestHashMap = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);
            requestHashMap.put("general", general);
            requestHashMap.put("pares", pares);
            requestHashMap.put("md", MD);
            transactionLogger.error("Pares--->" + pares);
            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            transactionLogger.error("processCommon3DAuthConfirmation Request-->" + trackingID + "--" + request);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(auth3DUrl, request);
            transactionLogger.error("processCommon3DAuthConfirmation Response-->" + trackingID + "--" + response);

            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                    if ("pending".equalsIgnoreCase(commResponseVO.getStatus()) || "pending3DConfirmation".equalsIgnoreCase(commResponseVO.getStatus()))
                    {
                        transactionLogger.error("3D auth sleepTime-->"+trackingID + "--" +sleepTime);
                        Thread.sleep(Long.parseLong(sleepTime));
                        commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (PZGenericConstraintViolationException e1)
        {
            transactionLogger.error("processCommon3DAuthConfirmation PZGenericConstraintViolationException e--for--" + trackingID + "--", e1);
        }
        catch (JSONException e1)
        {
            transactionLogger.error("processCommon3DAuthConfirmation PZGenericConstraintViolationException e--for--" + trackingID + "--", e1);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("processCommon3DAuthConfirmation InterruptedException e--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("EcommpayPaymentGateway :: Inside processAuth ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        RecurringBillingVO recurringBillingVO =commRequestVO.getRecurringBillingVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        int mId = Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String authurl = "";
        String amount = "0";
        int year = Integer.parseInt(cardDetailsVO.getExpYear());
        int month = Integer.parseInt(cardDetailsVO.getExpMonth());
        String cardnum = cardDetailsVO.getCardNum();
        String customerid = addressDetailsVO.getCustomerid();
        String country = addressDetailsVO.getCountry();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String phone = addressDetailsVO.getPhone();
        String day_of_birth = addressDetailsVO.getBirthdate();
        String first_name = addressDetailsVO.getFirstname();
        String last_name = addressDetailsVO.getLastname();
        String email = addressDetailsVO.getEmail();
        String ip_address = addressDetailsVO.getCardHolderIpAddress();
        String cvv = cardDetailsVO.getcVV();
        String street = addressDetailsVO.getStreet();
        String currency = commTransactionDetailsVO.getCurrency();
        String termUrl = "";
        String notifyUrl = RB.getString("NOTIFY_URL");
      /*  String recurringType=recurringBillingVO.getRecurringType();
        transactionLogger.error("recurringtype->"+recurringType);
      */  if (functions.isValueNull(addressDetailsVO.getBirthdate()))
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        try
        {
            if (!addressDetailsVO.getBirthdate().contains("-"))
            {
                day_of_birth = dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));
            }
            else
            {
                day_of_birth = dateFormat2.format(dateFormat.parse(addressDetailsVO.getBirthdate()));
            }
        }
        catch (ParseException e)
        {
            transactionLogger.error("Parse Exception-->" + trackingID + "--", e);
        }
    }

        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                authurl = RB.getString("TEST_AUTH_URL");
            }
            else
            {
                authurl = RB.getString("LIVE_AUTH_URL");
            }

            if (functions.isValueNull(commMerchantVO.getHostUrl()))//for whitelist
            {
                termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
                transactionLogger.error("From HOST_URL----" + termUrl);
            }
            else
            {
                termUrl = RB.getString("TERM_URL") + trackingID;
                transactionLogger.error("From RB TERM_URL ----" + termUrl);
            }

            if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
            else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
            else
                amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());


            TreeMap<String,Object> requestHashMap = new TreeMap<>();
            TreeMap<String,Object> requestHashMaplog = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);

            TreeMap<String, Object> card = new TreeMap<>();
            card.put("pan", cardnum);
            card.put("year", year);
            card.put("month", month);
            card.put("cvv", cvv);
            card.put("card_holder", first_name + " " + last_name);
            TreeMap<String, Object> cardlog = new TreeMap<>();

            cardlog.put("pan", functions.maskingPan(cardnum));
            cardlog.put("year", functions.maskingNumber(String.valueOf(year)));
            cardlog.put("month", functions.maskingNumber(String.valueOf(month)));
            cardlog.put("cvv", functions.maskingNumber(cvv));
            cardlog.put("card_holder", first_name + " " + last_name);

            TreeMap<String, Object> customer = new TreeMap<>();
            if (functions.isValueNull(customerid))
            {
                customer.put("id", customerid);
            }
            if (functions.isValueNull(country))
            {
                customer.put("country", country);
            }
            if (functions.isValueNull(city))
            {
                customer.put("city", city);
            }
            if (functions.isValueNull(day_of_birth))
            {
                customer.put("day_of_birth", day_of_birth);
            }
            if (functions.isValueNull(phone))
            {
                customer.put("phone", phone);
            }
            if (functions.isValueNull(first_name))
            {
                customer.put("first_name", first_name);
            }
            if (functions.isValueNull(last_name))
            {
                customer.put("last_name", last_name);
            }
            if (functions.isValueNull(email))
            {
                customer.put("email", email);
            }
            if (functions.isValueNull(ip_address))
            {
                customer.put("ip_address", ip_address);
            }
            if (functions.isValueNull(street))
            {
                customer.put("street", street);
            }
            if (functions.isValueNull(state))
            {
                customer.put("state", state);
            }

            TreeMap<String, Object> payment = new TreeMap<>();
            if (functions.isValueNull(amount))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }

            TreeMap<String, Object> acs_return_url = new TreeMap<>();
            acs_return_url.put("return_url", termUrl);
            acs_return_url.put("3ds_notification_url", notifyUrl);

            TreeMap<String, Object> return_url = new TreeMap<>();
            return_url.put("success", termUrl + "&status=success");
            return_url.put("decline", termUrl + "&status=decline");

           /* if("INITIAL".equals(recurringType) || "Manual".equalsIgnoreCase(recurringType))
            {
                TreeMap<String, Object> recurring = new TreeMap<>();
                recurring.put("register",true);
                //  recurring.put("type","R");
                //recurring.put("scheduled_payment_id",trackingID);
                requestHashMap.put("recurring",recurring);
                requestHashMap.put("recurring_register",true);

            }*/

            requestHashMap.put("general", general);
            requestHashMap.put("card", card);
            requestHashMap.put("customer", customer);
            requestHashMap.put("payment", payment);
            requestHashMap.put("acs_return_url", acs_return_url);
            requestHashMap.put("return_url", return_url);

            requestHashMaplog.put("general", general);
            requestHashMaplog.put("card", cardlog);
            requestHashMaplog.put("customer", customer);
            requestHashMaplog.put("payment", payment);
            requestHashMaplog.put("acs_return_url", acs_return_url);
            requestHashMaplog.put("return_url", return_url);

            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            String requestlog = gson.toJson(requestHashMaplog);
            transactionLogger.error("processAuthentication Request-->" + trackingID + "--" + requestlog);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(authurl, request);
            transactionLogger.error("processAuthentication Response-->" + trackingID + "--" + response);

            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));

                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("processAuthentication InterruptedException e--for--" + trackingID + "--", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("processAuthentication PZConstraintViolationException e--for--" + trackingID + "--", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("processAuthentication PZGenericConstraintViolationException e--for--" + trackingID + "--", e);
        }
        return commResponseVO;

    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("EcommpayPaymentGateway :: Inside processCapture");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        int mId = Integer.parseInt(gatewayAccount.getMerchantId());
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String description = commTransactionDetailsVO.getOrderDesc();
        String amount = "0";
        String currency = commTransactionDetailsVO.getCurrency();
        String captureurl = "";
        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                captureurl = RB.getString("TEST_CAPTURE_URL");
            }
            else
            {
                captureurl = RB.getString("LIVE_CAPTURE_URL");
            }
            if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
            else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
            else
                amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());

            TreeMap<String, TreeMap<String, Object>> requestHashMap = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);


            TreeMap<String, Object> payment = new TreeMap<>();

            if (functions.isValueNull(amount))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }
            requestHashMap.put("payment", payment);
            requestHashMap.put("general", general);

            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            transactionLogger.error("payment-->" + payment.toString());
            transactionLogger.error("processCapture Request-->" + trackingID + "--" + request);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(captureurl, request);
            transactionLogger.error("processCapture Response-->" + trackingID + "--" + response);


            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                }
                else
                {
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" + trackingID + "--", e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException--" + trackingID + "--", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--" + trackingID + "--", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("EcommpayPaymentGateway :: Inside processVoid ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        int mId = Integer.parseInt(gatewayAccount.getMerchantId());
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String description = commTransactionDetailsVO.getOrderDesc();
        String amount = "0";
        String currency = commTransactionDetailsVO.getCurrency();
        String cancelurl = "";

        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                cancelurl = RB.getString("TEST_CANCEL_URL");
            }
            else
            {
                cancelurl = RB.getString("LIVE_CANCEL_URL");
            }


            if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
            else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
            else
                amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());

            TreeMap<String, TreeMap<String, Object>> requestHashMap = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);


            TreeMap<String, Object> payment = new TreeMap<>();
            if (functions.isValueNull(String.valueOf(amount)))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }
            requestHashMap.put("general", general);
            requestHashMap.put("payment", payment);

            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            transactionLogger.error("processVoid Request-->" + trackingID + "--" + request);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(cancelurl, request);
            transactionLogger.error("processVoid Response-->" + trackingID + "--" + response);


            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                }
                else
                {
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" + trackingID + "--", e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException--" + trackingID + "--", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--" + trackingID + "--", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("EcommpayPaymentGateway :: Inside processPayout ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        int mId = Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String amount = "0";
        String cardnum = cardDetailsVO.getCardNum();
        String customerid = addressDetailsVO.getCustomerid();
        String country = addressDetailsVO.getCountry();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String phone = addressDetailsVO.getPhone();
        String first_name = addressDetailsVO.getFirstname();
        String last_name = addressDetailsVO.getLastname();
        String email = addressDetailsVO.getEmail();
        String ip_address = addressDetailsVO.getIp();
        String cvv = cardDetailsVO.getcVV();
        String payouturl = "";
        String card_holder="";
        int year = Integer.parseInt(cardDetailsVO.getExpYear());
        int month = Integer.parseInt(cardDetailsVO.getExpMonth());
        String street = addressDetailsVO.getStreet();
        String currency = commTransactionDetailsVO.getCurrency();

        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                payouturl = RB.getString("TEST_PAYOUT_URL");
            }
            else
            {
                payouturl = RB.getString("LIVE_PAYOUT_URL");
            }


            if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
            else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
            else
                amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());


            TreeMap<String, TreeMap<String, Object>> requestHashMap = new TreeMap<>();
            TreeMap<String, TreeMap<String, Object>> requestHashMaplog = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);

            TreeMap<String, Object> card = new TreeMap<>();

            card.put("pan", cardnum);
            card.put("year", year);
            card.put("month", month);
            card.put("cvv", cvv);
            card.put("card_holder", first_name + " " + last_name);
            TreeMap<String, Object> cardlog = new TreeMap<>();

            cardlog.put("pan", functions.maskingPan(cardnum));
            cardlog.put("year", functions.maskingNumber(String.valueOf(year)));
            cardlog.put("month", functions.maskingNumber(String.valueOf(month)));
            cardlog.put("cvv", functions.maskingNumber(cvv));
            cardlog.put("card_holder", first_name + " " + last_name);

            TreeMap<String, Object> customer = new TreeMap<>();
            if (functions.isValueNull(customerid))
            {
                customer.put("id", customerid);
            }
            if (functions.isValueNull(country))
            {
                customer.put("country", country);
            }
            if (functions.isValueNull(city))
            {
                customer.put("city", city);
            }
            if (functions.isValueNull(phone))
            {
                customer.put("phone", phone);
            }
            if (functions.isValueNull(first_name))
            {
                customer.put("first_name", first_name);
            }
            if (functions.isValueNull(last_name))
            {
                customer.put("last_name", last_name);
            }
            if (functions.isValueNull(email))
            {
                customer.put("email", email);
            }
            if (functions.isValueNull(ip_address))
            {
                customer.put("ip_address", ip_address);
            }
            if (functions.isValueNull(street))
            {
                customer.put("street", street);
            }
            if (functions.isValueNull(state))
            {
                customer.put("state", state);
            }

            TreeMap<String, Object> payment = new TreeMap<>();
            if (functions.isValueNull(amount))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }
            requestHashMap.put("general", general);
            requestHashMap.put("payment", payment);
            requestHashMap.put("card", card);
            requestHashMap.put("customer", customer);

            requestHashMaplog.put("general", general);
            requestHashMaplog.put("payment", payment);
            requestHashMaplog.put("card", cardlog);
            requestHashMaplog.put("customer", customer);


            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            String requestlog = gson.toJson(requestHashMaplog);
            transactionLogger.error("processPayout Request-->" + trackingID + "--" + requestlog);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(payouturl, request);
            transactionLogger.error("processPayout Response-->" + trackingID + "--" + response);
            if (functions.isValueNull(response))//if not null
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                }
                else
                {
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" + trackingID + "--", e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException--" + trackingID + "--", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--" + trackingID + "--", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("EcommpayPaymentGateway :: Inside processRecurring ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EcommpayUtils ecommpayUtils = new EcommpayUtils();
        int mId = Integer.parseInt(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String recurringurl = "";
        String amount = "0";
        //  int year = Integer.parseInt(cardDetailsVO.getExpYear());
        // int month = Integer.parseInt(cardDetailsVO.getExpMonth());
        String cardnum = cardDetailsVO.getCardNum();
        String card_holder = "";
        String customerid = addressDetailsVO.getCustomerid();
        String country = addressDetailsVO.getCountry();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String phone = addressDetailsVO.getPhone();
        String first_name = addressDetailsVO.getFirstname();
        String last_name = addressDetailsVO.getLastname();
        String email = addressDetailsVO.getEmail();
        String ip_address = addressDetailsVO.getIp();
        String cardholderIp=addressDetailsVO.getCardHolderIpAddress();
        String cvv = cardDetailsVO.getcVV();
        String street = addressDetailsVO.getStreet();
        String currency = commTransactionDetailsVO.getCurrency();
        String termUrl = "";
        String notifyUrl = RB.getString("NOTIFY_URL");
        String previousTrackingId= commTransactionDetailsVO.getPreviousTransactionId();//orderId //PreviousTransactionId
        String id=EcommpayUtils.getResponseHashInfoQuery(previousTrackingId);
        transactionLogger.error("RecurringId--> "+id);
        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                recurringurl = RB.getString("TEST_RECURRING_URL");
            }
            else
            {
                recurringurl = RB.getString("LIVE_RECURRING_URL");
            }


            if ("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
            else if ("KWD".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
                amount = EcommpayUtils.getKWDSupportedAmount(commTransactionDetailsVO.getAmount());
            else
                amount = EcommpayUtils.getCentAmount(commTransactionDetailsVO.getAmount());


            TreeMap<String, Object> requestHashMap = new TreeMap<>();

            TreeMap<String, Object> general = new TreeMap<>();
            general.put("project_id", mId);
            general.put("payment_id", trackingID);

            TreeMap<String, Object> customer = new TreeMap<>();
            if (functions.isValueNull(customerid))
            {
                customer.put("id", customerid);
            }

            if (functions.isValueNull(country))
            {
                customer.put("country", country);
            }
            if (functions.isValueNull(city))
            {
                customer.put("city", city);
            }
            if (functions.isValueNull(phone))
            {
                customer.put("phone", phone);
            }
            if (functions.isValueNull(first_name))
            {
                customer.put("first_name", first_name);
            }
            if (functions.isValueNull(last_name))
            {
                customer.put("last_name", last_name);
            }
            if (functions.isValueNull(email))
            {
                customer.put("email", email);
            }
            if (functions.isValueNull(cardholderIp))
            {
                customer.put("ip_address", cardholderIp);
            }
            else
            {
                customer.put("ip_address",ip_address);
            }
            if (functions.isValueNull(street))
            {
                customer.put("street", street);
            }
            if (functions.isValueNull(state))
            {
                customer.put("state", state);
            }

            TreeMap<String, Object> payment = new TreeMap<>();
            if (functions.isValueNull(amount))
            {
                payment.put("amount", Integer.parseInt(amount));
            }
            if (functions.isValueNull(currency))
            {
                payment.put("currency", currency);
            }

            TreeMap<String, Object> recurring = new TreeMap<>();
            recurring.put("id",Integer.parseInt(id));

            requestHashMap.put("general", general);
            requestHashMap.put("customer", customer);
            requestHashMap.put("payment", payment);
            requestHashMap.put("recurring", recurring);

            Gson gson = new Gson();
            String signature = calculateSignature(gson.toJson(requestHashMap), secretkey);
            transactionLogger.error("signature-->" + signature);
            general.put("signature", signature);
            requestHashMap.put("general", general);
            String request = gson.toJson(requestHashMap);
            transactionLogger.error("processRecurring Request-->" + trackingID + "--" + request);
            String response = ecommpayUtils.doPostHTTPSURLConnectionClient(recurringurl, request);
            transactionLogger.error("processRecurring Response-->" + trackingID + "--" + response);


            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if ("SUCCESS".equalsIgnoreCase(responseJSON.getString("status")))
                {
                    Thread.sleep(1500);
                    commResponseVO = (Comm3DResponseVO) processQuery(trackingID, requestVO);
                }
                else
                {
                    if (responseJSON.has("message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("message"));
                        commResponseVO.setRemark(responseJSON.getString("message"));
                    }
                    else
                    {
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark("Transaction Failed");
                    }
                    if (responseJSON.has("code"))
                        commResponseVO.setErrorCode(responseJSON.getString("code"));
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--" + trackingID + "--", e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException--" + trackingID + "--", e);
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException--" + trackingID + "--", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--" + trackingID + "--", e);
        }
        return commResponseVO;
    }
}