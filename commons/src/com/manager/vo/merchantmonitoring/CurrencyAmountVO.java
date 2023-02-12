package com.manager.vo.merchantmonitoring;

import java.util.HashMap;
import java.util.List;

/**
 * Created by supriya on 3/11/2016.
 */
public class CurrencyAmountVO
{
    HashMap<String,CardTypeAmountVO> cardTypeAmountVOList;

    public HashMap<String, CardTypeAmountVO> getCardTypeAmountVOList()
    {
        return cardTypeAmountVOList;
    }

    public void setCardTypeAmountVOList(HashMap<String, CardTypeAmountVO> cardTypeAmountVOList)
    {
        this.cardTypeAmountVOList = cardTypeAmountVOList;
    }
}
