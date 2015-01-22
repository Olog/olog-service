/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.XmlLog;
import edu.msu.nscl.olog.entity.bitemporal.Bitemporal;
import edu.msu.nscl.olog.entity.bitemporal.TimeUtils;
import java.util.Date;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 *
 * @author berryman
 */
public class IntervalConverter extends DozerConverter<BitemporalLog, XmlLog> {
    
    public IntervalConverter() {
        super(BitemporalLog.class, XmlLog.class);
    }

    @Override
    public XmlLog convertTo(BitemporalLog source, XmlLog destination) {
       String param = this.getParameter();
       switch(param){
           case "validitystart":
               destination.setEventStart(source.getValidityInterval().getStart().toDate());
               return destination;
           case "validityend":
               destination.setEventEnd(source.getValidityInterval().getEnd().toDate());
               return destination;
           default:
               throw new UnsupportedOperationException("Not supported yet.");
       }
       
       
    }

    @Override
    public BitemporalLog convertFrom(XmlLog source, BitemporalLog destination) {
        throw new UnsupportedOperationException("Not supported yet.");
//        For some reason, this doesn't work in Dozer
//        So, it's currently marked as one way 
//        Interval interval;
//        if(source.getEventStart()!=null && source.getEventEnd()!=null){
//            interval = new Interval(source.getEventStart().getTime(),source.getEventEnd().getTime());
//        }
//        else if(source.getEventStart()!=null && source.getEventEnd()==null){
//            interval = TimeUtils.from(new DateTime(source.getEventStart()));
//        }else {
//            interval = TimeUtils.fromNow();
//        }
//        return destination.copyWith(interval);
    }

}
