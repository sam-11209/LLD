package com.system.lld.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadCreate {

	public static void main(String[] args) {
		MyThread mt = new MyThread();
		mt.start();

		MyRunnable mr = new MyRunnable();
		Thread t1 = new Thread(mr);
		t1.start();

		Thread t2 = new Thread(() -> {
			System.out.println("thread Running with Runnable Again");
		});
		t2.start();

		ExecutorService ex = Executors.newFixedThreadPool(2);
		ex.submit(new MyCallable());
		ex.shutdown();
		
	}
}

class MyThread extends Thread {

	@Override
	public void run() {

		System.out.println("thread Running");
	}
}

class MyRunnable implements Runnable {

	@Override
	public void run() {
		System.out.println("thread Running with Runnable");

	}

}

class MyCallable implements Callable<String> {

	@Override
	public String call() throws Exception {
		System.out.println("hfdh");
		return null;
	}

}