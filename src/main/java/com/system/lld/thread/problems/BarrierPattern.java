package com.system.lld.thread.problems;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

//If you have 5 threads so all 4 will stay at a barrier and when 5th will come they all move together.
public class BarrierPattern {

	private int curThread;;
	private final Semaphore mutex = new Semaphore(1);
	private final Semaphore zeroSemaphore = new Semaphore(0);

	private AtomicInteger parties;

	BarrierPattern(int curThread) {
		this.curThread = curThread;
		this.parties = new AtomicInteger(curThread);
	}

	public static void main(String[] args) {

		BarrierPattern bp = new BarrierPattern(5);

		for (int i = 0; i < 5; i++) {
			final int data = i;
			Thread t1 = new Thread(() -> {
				try {
					bp.runTask(data);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			t1.start();
		}
	}

	private void runTask(int data) throws InterruptedException {

		System.out.println("Running Task fpr data:" + data + "thread name" + Thread.currentThread().getName());

		blockAllThread();

		System.out.println("*****data:" + data + "thread name" + Thread.currentThread().getName());
		
		blockAllThread();
		
		System.out.println("Shubham thread name" + Thread.currentThread().getName());

	}

	private void blockAllThread() throws InterruptedException {

		parties.decrementAndGet();

		if (parties.get() == 0) {
			mutex.acquire();

			for (int i = 0; i < curThread - 1; i++) {
				zeroSemaphore.release();
			}
			parties = new AtomicInteger(curThread);
			mutex.release();

		} else {
			zeroSemaphore.acquire();
		}

	}
}
