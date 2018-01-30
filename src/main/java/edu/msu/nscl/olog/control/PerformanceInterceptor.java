/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.control;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author berryman
 */
public class PerformanceInterceptor {

//    private static final AtomicLong callCount = new AtomicLong();
//
//    private long getCallCount() {
//        return callCount.get();
//    }
//
//    private void increaseCallCount() {
//        callCount.getAndIncrement();
//    }

    @AroundInvoke
    Object measureTime(InvocationContext ctx) throws Exception {
        Logger audit = Logger.getLogger(ctx.getMethod().getDeclaringClass().getName() + ".audit");
        TimeWatch watch = TimeWatch.start();
        Object obj = null;
        
        try {
//            increaseCallCount();
            obj = ctx.proceed();
            return obj;
        } finally {
//            getCallCount();
            audit.info(ctx.getMethod().getDeclaringClass().getName()+
                    "."+ctx.getMethod().getName()+
                    " | "+watch.time(TimeUnit.MILLISECONDS)+
                    "ms");
        }
    }
}
