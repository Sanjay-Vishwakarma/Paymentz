package com.payment.p4.vos.queryBlock.typesBlock;

import com.payment.p4.vos.queryBlock.typesBlock.typeBlock.Type;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by Admin on 28/10/2015.
 */
@XmlRootElement(name = "Types")
@XmlAccessorType(XmlAccessType.FIELD)
public class Types
{
    @XmlElement(name = "Type")
    List<Type> Type;

    public List<Type> getType()
    {
        return Type;
    }

    public void setType(List<Type> type)
    {
        Type = type;
    }
}
