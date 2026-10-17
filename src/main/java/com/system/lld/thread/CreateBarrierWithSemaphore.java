package com.system.lld.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class CreateBarrierWithSemaphore {

	private final int parties;
	private int count;
	private final Semaphore zeroSemaphore = new Semaphore(0);
	private final Semaphore binarySemphore = new Semaphore(1);

	CreateBarrierWithSemaphore(int parties) {
		this.parties = parties;
		this.count = parties;
	}

	private void waitAllThreads() throws InterruptedException {

		binarySemphore.acquire();
		count = count - 1;

		if (count == 0) {
			zeroSemaphore.release(parties - 1);
			count = parties;
			binarySemphore.release();

		} else {
			binarySemphore.release();
			zeroSemaphore.acquire();
		}
	}

	public static void main(String[] args) {

		int threadCount = 5;
		CreateBarrierWithSemaphore bs = new CreateBarrierWithSemaphore(threadCount);
		ExecutorService ex = Executors.newFixedThreadPool(threadCount);

		for (int i = 0; i < threadCount; i++) {
			ex.submit(() -> {

				System.out.println("1My thread is Running Thread:" + Thread.currentThread().getName());
				System.out.println("2My thread is Running Thread:" + Thread.currentThread().getName());
				System.out.println("3My thread is Running Thread:" + Thread.currentThread().getName());

				// All threads need to stop here
				try {
					bs.waitAllThreads();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("1Start After waiting Thread:" + Thread.currentThread().getName());
				System.out.println("2Start After waiting Thread:" + Thread.currentThread().getName());
				System.out.println("3Start After waiting Thread:" + Thread.currentThread().getName());

			});
		}
		ex.shutdown();
	}
}
