package com.payment.common.core;

import com.directi.pg.core.valueObjects.GenericCardDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/4/13
 * Time: 1:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommCardDetailsVO extends GenericCardDetailsVO
{
    private String cardHolderFirstName;
    private String cardHolderSurname;
    private String name_on_card;

    public String getName_on_card() {return name_on_card;}
    public void setName_on_card(String name_on_card) {this.name_on_card = name_on_card;}

    public String getCardHolderFirstName()
    {
        return cardHolderFirstName;
    }

    public void setCardHolderFirstName(String cardHolderFirstName)
    {
        this.cardHolderFirstName = cardHolderFirstName;
    }

    public String getCardHolderSurname()
    {
        return cardHolderSurname;
    }

    public void setCardHolderSurname(String cardHolderSurname)
    {
        this.cardHolderSurname = cardHolderSurname;
    }


}
