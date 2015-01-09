/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.boundry.AttachmentManager;
import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.XmlAttachments;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

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
       return source.getRecordInterval().getStart().toDate();
    }

    @Override
    public BitemporalLog convertFrom(Date source, BitemporalLog destination) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
