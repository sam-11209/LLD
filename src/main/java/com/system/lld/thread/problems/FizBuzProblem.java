package com.system.lld.thread.problems;

import java.util.concurrent.Semaphore;

public class FizBuzProblem {
	private final int num;
	private int x = 1;

	// The number thread starts first.
	private final Semaphore numSemaphore = new Semaphore(1);
	private final Semaphore fizSemaphore = new Semaphore(0);
	private final Semaphore buzSemaphore = new Semaphore(0);
	private final Semaphore fizbuzSemaphore = new Semaphore(0);

	private final StringBuilder result = new StringBuilder();

	public FizBuzProblem(int num) {
		this.num = num;
	}

	private void number() throws InterruptedException {
		while (true) {
			numSemaphore.acquire();

			if (x > num) {
				finish();
				return;
			}

			// This thread handles numbers divisible by neither 3 nor 5.
			if (x % 3 != 0 && x % 5 != 0) {
				result.append(x+" ");
				x++;
				signalNextThread();
			}
			// If this is not a number's turn, leave x unchanged.
			// The correct thread will be signaled by signalNextThread().
			else {
				signalNextThread();
			}
		}
	}

	private void fiz() throws InterruptedException {
		while (true) {
			fizSemaphore.acquire();

			if (x > num) {
				finish();
				return;
			}

			result.append("fizz"+ " ");
			x++;
			signalNextThread();
		}
	}

	private void buz() throws InterruptedException {
		while (true) {
			buzSemaphore.acquire();

			if (x > num) {
				finish();
				return;
			}

			result.append("buzz"+" ");
			x++;
			signalNextThread();
		}
	}

	private void fizbuz() throws InterruptedException {
		while (true) {
			fizbuzSemaphore.acquire();

			if (x > num) {
				finish();
				return;
			}

			result.append("fizzbuzz"+" ");
			x++;
			signalNextThread();
		}
	}

	private void signalNextThread() {
		if (x > num) {
			finish();
		} else if (x % 3 == 0 && x % 5 == 0) {
			fizbuzSemaphore.release();
		} else if (x % 3 == 0) {
			fizSemaphore.release();
		} else if (x % 5 == 0) {
			buzSemaphore.release();
		} else {
			numSemaphore.release();
		}
	}

	private void finish() {
		// Wake every thread so it can see that the sequence is complete and exit.
		numSemaphore.release();
		fizSemaphore.release();
		buzSemaphore.release();
		fizbuzSemaphore.release();
	}

	public static void main(String[] args) throws InterruptedException {
		FizBuzProblem printer = new FizBuzProblem(15);

		Thread numThread = new Thread(() -> runSafely(printer::number));
		Thread fizThread = new Thread(() -> runSafely(printer::fiz));
		Thread buzThread = new Thread(() -> runSafely(printer::buz));
		Thread fizbuzThread = new Thread(() -> runSafely(printer::fizbuz));

		numThread.start();
		fizThread.start();
		buzThread.start();
		fizbuzThread.start();

		numThread.join();
		fizThread.join();
		buzThread.join();
		fizbuzThread.join();

		System.out.println(printer.result);
	}

	private static void runSafely(InterruptibleTask task) {
		try {
			task.run();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@FunctionalInterface
	private interface InterruptibleTask {
		void run() throws InterruptedException;
	}
}