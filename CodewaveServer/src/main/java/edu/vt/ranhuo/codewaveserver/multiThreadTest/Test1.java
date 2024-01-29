package edu.vt.ranhuo.codewaveserver.multiThreadTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test1 {

    public static void main(String[] args) throws InterruptedException {
        final int threadNum = 100000;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        final Num n = new Num();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < threadNum; i++) {
            executorService.submit(() -> {
               n.add();
               countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        System.out.println(n.get());
    }
}
class Num{
    int count = 0;
    public void add(){
        count++;
    }
    public int get(){
        return count;
    }
}
