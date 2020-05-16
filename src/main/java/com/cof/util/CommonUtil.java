package com.cof.util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class CommonUtil {

    private static  CountDownLatch countDownLatch = new CountDownLatch(5);
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

    public static void countDown(){
        countDownLatch.countDown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void cyclicBarrier(){
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
