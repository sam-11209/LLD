package com.system.lld.thread;

public class WaitVsSleep {

	public static void main(String[] args) {

		SleepTest st = new SleepTest();

		Thread t1 = new Thread(() -> {
			st.mysleepTest();
		});

		Thread t2 = new Thread(() -> {
			st.mysleepTest();
		});

		t1.start();
		t2.start();
		
		Thread t3 = new Thread(() -> {
			st.notifyOne();
			try {
				String name = Thread.currentThread().getName();
				System.out.println("Sleeping Thread:" + name);
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			st.notifyOne();
		});
		t3.start();
	}
}

class SleepTest {

	synchronized void mysleepTest() {

		String name = Thread.currentThread().getName();

		System.out.println("I am processing" + name);

		try {
			wait();
			System.out.println("Working after notify" + name);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	synchronized void notifyOne() {
		String name = Thread.currentThread().getName();

		System.out.println("Calling Notify for Thread  :" + name);

		notify();
	}

}