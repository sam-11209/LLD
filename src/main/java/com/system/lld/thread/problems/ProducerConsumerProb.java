
package com.system.lld.thread.problems;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumerProb {

	private final Queue<Integer> q = new LinkedList<>();
	private final int capacity = 4;

	void produce(Integer data) throws InterruptedException {
		synchronized (this) {
			while (q.size() == capacity) {
				System.out.println("Queue is full. Producer waiting.");
				wait();
			}

			q.add(data);
			System.out.println("Produced: " + data);

			notifyAll();
		}
	}

	void consume() throws InterruptedException {
		synchronized (this) {
			while (q.isEmpty()) {
				System.out.println("Queue is empty. Consumer waiting.");
				wait();
			}

			System.out.println("Consumer consumed: " + q.poll());

			notifyAll();
		}
	}

	public static void main(String[] args) {
		ProducerConsumerProb pc = new ProducerConsumerProb();

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
					pc.consume();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});

			con.start();
		}
	}
}
