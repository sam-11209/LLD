package com.system.lld.thread.semaphore;

import java.util.concurrent.Semaphore;

public class SemaphoreEample {

	private static final Semaphore semaphore = new Semaphore(3);
	
	
	public static void main(String[] args) {
		for(int i=0; i<15; i++) {
			Thread t1 = new Thread(()->{
				try {
					dotasks();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			t1.start();
		}
	}
	
	

	public static void dotasks() throws InterruptedException {
		
		semaphore.acquire();
		try {

			System.out.println("Processing done via thread:" + Thread.currentThread().getName());
			Thread.sleep(1000);
		} finally {
			semaphore.release();
			System.out.println("Lock released via thread:" + Thread.currentThread().getName());
		}
	}
}
