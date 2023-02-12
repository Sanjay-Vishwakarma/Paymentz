package com.manager;

import com.manager.dao.CurrencyExchangeDao;
import com.manager.vo.ExchangeRatesVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.text.ParseException;

/**
 * Created by Jitendra on 2/28/2018.
 */
public class CurrencyExchangeManager
{
    CurrencyExchangeDao currencyExchangeDao=new CurrencyExchangeDao();
    public boolean addExchangeRate(ExchangeRatesVO exchangeRatesVO) throws PZDBViolationException
    {
        return currencyExchangeDao.addExchangeRate(exchangeRatesVO);
    }
    public  ExchangeRatesVO getExchangeDetails(String mappingId) throws PZDBViolationException, ParseException
    {
        return currencyExchangeDao.getExchangeDetails(mappingId);
    }
    public boolean updateExchangeDetails(String mappingId,String exchangeRate)throws PZDBViolationException
    {
        return currencyExchangeDao.updateExchangeDetails(mappingId,exchangeRate);
    }
    public  boolean checkAvailability(ExchangeRatesVO exchangeRatesVO)throws PZDBViolationException
    {
        return currencyExchangeDao.checkAvailability(exchangeRatesVO);
    }
}
