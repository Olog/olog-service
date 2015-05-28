/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author berryman
 */
public class BitemporalLogs implements List<BitemporalLog>{
    
    List<BitemporalLog> bitemporalLog = new ArrayList<BitemporalLog>();
    Long queryCount = 0L;
    
    public Long getQueryCount() {
        return this.queryCount;
    }
    
    public void setQueryCount(Long queryCount){
        this.queryCount = queryCount;
    }

    @Override
    public int size() {
        return bitemporalLog.size();
    }

    @Override
    public boolean isEmpty() {
        return bitemporalLog.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return bitemporalLog.contains(o);
    }

    @Override
    public Iterator<BitemporalLog> iterator() {
        return bitemporalLog.iterator();
    }

    @Override
    public Object[] toArray() {
        return bitemporalLog.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return bitemporalLog.toArray(a);
    }

    @Override
    public boolean add(BitemporalLog e) {
        return bitemporalLog.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return bitemporalLog.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return bitemporalLog.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends BitemporalLog> c) {
        return bitemporalLog.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends BitemporalLog> c) {
        return bitemporalLog.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return bitemporalLog.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return bitemporalLog.retainAll(c);
    }

    @Override
    public void clear() {
        bitemporalLog.clear();
    }

    @Override
    public BitemporalLog get(int index) {
        return bitemporalLog.get(index);
    }

    @Override
    public BitemporalLog set(int index, BitemporalLog element) {
        return bitemporalLog.set(index, element);
    }

    @Override
    public void add(int index, BitemporalLog element) {
        bitemporalLog.add(index, element);
    }

    @Override
    public BitemporalLog remove(int index) {
        return bitemporalLog.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return bitemporalLog.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return bitemporalLog.lastIndexOf(o);
    }

    @Override
    public ListIterator<BitemporalLog> listIterator() {
        return bitemporalLog.listIterator();
    }

    @Override
    public ListIterator<BitemporalLog> listIterator(int index) {
        return bitemporalLog.listIterator(index);
    }

    @Override
    public List<BitemporalLog> subList(int fromIndex, int toIndex) {
        return bitemporalLog.subList(fromIndex, toIndex);
    }
    
}
