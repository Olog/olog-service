/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.entity;

import edu.msu.nscl.olog.entity.bitemporal.BitemporalWrapper_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author berryman
 */
@StaticMetamodel(BitemporalLog.class)
public abstract class BitemporalLog_ extends BitemporalWrapper_<Log>{
    public static volatile SingularAttribute<BitemporalLog, java.lang.Long> id;
    public static volatile SingularAttribute<BitemporalLog, edu.msu.nscl.olog.entity.Log> log;
}
