package com.cof;

import com.cof.util.CommonUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    public static void main(String[] args) throws InterruptedException {
//        byte[] src = new byte[]{1,2,3,4,5};
//        byte[] target = new byte[10];
//        System.arraycopy(src, 2, target, 0, 3);
//        System.out.println(Arrays.toString(target));
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(2000);
            int andAdd = atomicInteger.getAndAdd(1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CommonUtil.cyclicBarrier();
                    System.out.println(andAdd);
                }
            }).start();
        }
    }
}
