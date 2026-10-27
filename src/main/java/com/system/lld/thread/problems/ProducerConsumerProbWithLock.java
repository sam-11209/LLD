package com.system.lld.thread.problems;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerProbWithLock {

	private final Queue<Integer> q = new LinkedList<>();
	private final int capacity = 4;

	private final ReentrantLock lock = new ReentrantLock();
	private final Condition notFull = lock.newCondition();
	private final Condition notEmpty = lock.newCondition();

	void produce(Integer data) throws InterruptedException {
		lock.lock();
		try {
			while (q.size() == capacity) {
				System.out.println("Queue is full. Producer waiting.");
				notFull.await();
			}

			q.add(data);
			System.out.println("Produced: " + data);

			notEmpty.signalAll();

		} finally {
			lock.unlock();
		}
	}

	void consumer() throws InterruptedException {
		lock.lock();
		try {
			while (q.isEmpty()) {
				System.out.println("Queue is empty. Consumer waiting.");
				notEmpty.await();
			}

			Integer data = q.poll();
			System.out.println("Consumed: " + data);

			notFull.signalAll();

		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) {
		ProducerConsumerProbWithLock pc = new ProducerConsumerProbWithLock();

		for (int i = 0; i < 10; i++) {
			final int data = i;

			Thread prod = new Thread(() -> {
				try {
					pc.produce(data);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});

			prod.start();
		}

		for (int j = 0; j < 10; j++) {
			Thread con = new Thread(() -> {
				try {
					pc.consumer();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});

			con.start();
		}
	}
}
