package com.company;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class ConcurAffline extends Affline {
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch latch;
    int threadNum;


    public ConcurAffline(int[][] p, int[][] q, int height, int width, ImageOperator operator,int threadNum) {
        super(p, q, height, width, operator);
        this.latch = new CountDownLatch(this.height*this.width);
        this.threadNum = threadNum;
    }
    @Override
    public void  changeImage() throws InterruptedException {
        this.latch =new CountDownLatch(this.height*this.width);
        for (int i=0; i <this.height;i++){
            for(int j=0;j<this.width;j++){
                int finalI = i;
                int finalJ = j;
                executorService.execute(()->{
                    int[] point = afflinePoint(new int[]{finalI, finalJ});
                    operator.setPixelColor(finalI, finalJ,point[0],point[1]);
                    latch.countDown();
                });
            }
        }


        this.latch.await();

        return;
    }
}
