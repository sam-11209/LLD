package com.system.lld.thread.problems;

import java.util.concurrent.Semaphore;

public class ReadWriteLockImpl {

	int readCount = 0;
	private final Semaphore readLock = new Semaphore(1);
	private final Semaphore writeLock = new Semaphore(1);

	public void lockRead() throws InterruptedException {
		// Why need to acquire read Lock ?
		// When writer is writing at that time no reader can read this is our condition
		// but you can see reader can come here and acquire this lock but at that time
		// read count is 0 hence he try to acquire writer lock but he will not get it so
		// he can not release reader lock so other reader will not read also
		readLock.acquire();
		readCount++;

		if (readCount == 1) {
			writeLock.acquire();
		}
		readLock.release();
	}
	public void unlockRead() throws InterruptedException {

		readLock.acquire();
		readCount--;

		if (readCount == 0) {
			writeLock.release();
		}
		readLock.release();
	}

	public void lockWrite() throws InterruptedException {
		writeLock.acquire();
	}

	public void unlockWrite() throws InterruptedException {
		writeLock.release();
	}

	public static void main(String[] args) {

		ReadWriteLockImpl rw = new ReadWriteLockImpl();
		Runnable Reader1 = () -> {
			try {
				rw.lockRead();
				System.out.println("Rading start by reader1");
				Thread.sleep(1000);
				System.out.println("Rading Done by reader1");
				rw.unlockRead();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		Runnable Reader2 = () -> {
			try {
				rw.lockRead();
				System.out.println("Rading start by Reader2");
				Thread.sleep(1000);
				System.out.println("Rading Done by Reader2");
				rw.unlockRead();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		Runnable Writer1 = () -> {
			try {
				rw.lockWrite();
				System.out.println("Writing start by Writer1");
				Thread.sleep(1000);
				System.out.println("Writing Done by Writer1");
				rw.unlockWrite();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		Thread t1 = new Thread(Reader1);
		Thread t2 = new Thread(Reader2);
		Thread t3 = new Thread(Writer1);

		t1.start();
		t2.start();
		t3.start();

	}
}
