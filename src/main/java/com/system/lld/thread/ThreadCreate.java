package com.system.lld.thread;

public class ThreadCreate {

	public static void main(String[] args) {
		MyThread mt = new MyThread();
		mt.start();
		
		MyRunnable mr = new MyRunnable();
		Thread t1 = new Thread(mr);
		t1.start();
		
		Thread t2 = new Thread(()->{
			System.out.println("thread Running with Runnable Again");
		});
		t2.start();
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