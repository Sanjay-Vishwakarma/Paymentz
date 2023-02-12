package com.payment.p4.vos.queryBlock.scopeBlock;

import javax.xml.bind.annotation.*;

/**
 * Created by Admin on 28/10/2015.
 */
@XmlRootElement(name = "Scope")
@XmlAccessorType(XmlAccessType.FIELD)
public class Scope
{
    @XmlAttribute(name = "entity")
    String entity;

    @XmlElement(name = "EntityID")
    String EntityID;

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getEntityID()
    {
        return EntityID;
    }

    public void setEntityID(String entityID)
    {
        EntityID = entityID;
    }
}
