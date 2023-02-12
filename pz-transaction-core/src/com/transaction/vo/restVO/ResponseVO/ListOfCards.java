package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 8/20/2016.
 */
@XmlRootElement(name = "cards")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOfCards
{
    private List<Card> card = new ArrayList<Card>();

    public void addListOfCards(Card card)
    {
        this.card.add(card);
    }
}
