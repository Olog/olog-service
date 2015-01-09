/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.boundry.AttachmentManager;
import edu.msu.nscl.olog.entity.XmlAttachments;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

/**
 *
 * @author berryman
 */
public class XmlAttachmentConverter extends DozerConverter<Long, Collection> implements MapperAware {

    private Mapper mapper;

    public XmlAttachmentConverter() {
        super(Long.class, Collection.class);
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Collection convertTo(Long a, Collection b) {
        
        try {
            return AttachmentManager.findAll(a).getAttachments();
        } catch (OlogException ex) {
            Logger.getLogger(XmlAttachmentConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Long convertFrom(Collection b, Long a) {
        return null;
    }

}
