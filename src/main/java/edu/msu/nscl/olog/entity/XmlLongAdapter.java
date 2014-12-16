/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.entity;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author berryman
 */
public class XmlLongAdapter extends XmlAdapter<String, Long> {

    @Override
    public Long unmarshal(String s) {
        return Long.parseLong(s);
    }

    @Override
    public String marshal(Long number) {
        if (number == null) {
            return "";
        }

        return number.toString();
    }
}
