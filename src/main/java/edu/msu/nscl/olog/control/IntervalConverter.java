/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.entity.BitemporalLog;
import java.util.Date;
import org.dozer.DozerConverter;

/**
 *
 * @author berryman
 */
public class IntervalConverter extends DozerConverter<BitemporalLog, Date> {

    public IntervalConverter() {
        super(BitemporalLog.class, Date.class);
    }

    @Override
    public Date convertTo(BitemporalLog source, Date destination) {
       String param = this.getParameter();
       switch(param){
           case "validitystart":
               return source.getRecordInterval().getStart().toDate();
           case "validityend":
               return source.getRecordInterval().getEnd().toDate();
           default:
               throw new UnsupportedOperationException("Not supported yet.");
       }
       
       
    }

    @Override
    public BitemporalLog convertFrom(Date source, BitemporalLog destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
