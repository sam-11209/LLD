package com.system.lld;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CustomConcurrentHashMap<K, V> {

	private static final int INITIAL_CAPACITY = 16;
	private static final float LOAD_FACTOR = 0.75f;
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	// Current bucket array.
	private Node<K, V>[] buckets;

	// One lock per bucket: locks.length == buckets.length.
	private ReentrantReadWriteLock[] locks;

	// Protects the entire table during normal operations and resizing.
	private final ReentrantReadWriteLock tableLock = new ReentrantReadWriteLock();

	private volatile int threshold;

	// Atomic counter for concurrent insertions and removals.
	private final AtomicInteger size = new AtomicInteger(0);

	private static class Node<K, V> {
		final K key;
		V value;
		Node<K, V> next;

		Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	@SuppressWarnings("unchecked")
	public CustomConcurrentHashMap() {
		buckets = (Node<K, V>[]) new Node[INITIAL_CAPACITY];

		locks = createLocks(INITIAL_CAPACITY);

		threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
	}

	private ReentrantReadWriteLock[] createLocks(int capacity) {
		ReentrantReadWriteLock[] newLocks = new ReentrantReadWriteLock[capacity];

		for (int i = 0; i < capacity; i++) {
			newLocks[i] = new ReentrantReadWriteLock();
		}

		return newLocks;
	}

	private int hash(Object key) {
		int h = key.hashCode();
		return h ^ (h >>> 16);
	}

	private int getIndex(Object key, int capacity) {
		return hash(key) & (capacity - 1);
	}

	// Inserts a new entry or updates an existing entry.
	// Returns the previous value, or null for a new entry.
	public V put(K key, V value) {
		Objects.requireNonNull(key, "Null keys are not supported");
		Objects.requireNonNull(value, "Null values are not supported");

		V oldValue = null;

		tableLock.readLock().lock();
		try {
			int index = getIndex(key, buckets.length);

			ReentrantReadWriteLock.WriteLock bucketWriteLock = locks[index].writeLock();

			bucketWriteLock.lock();
			try {
				Node<K, V> current = buckets[index];

				while (current != null) {
					if (current.key.equals(key)) {
						oldValue = current.value;
						current.value = value;
						return oldValue;
					}

					current = current.next;
				}

				Node<K, V> newNode = new Node<>(key, value);
				newNode.next = buckets[index];
				buckets[index] = newNode;

				size.incrementAndGet();

			} finally {
				bucketWriteLock.unlock();
			}

		} finally {
			tableLock.readLock().unlock();
		}

		// Must run after releasing the table read lock.
		resizeIfNeeded();

		return oldValue;
	}

	// Multiple threads can read the same bucket concurrently.
	public V get(K key) {
		Objects.requireNonNull(key, "Null keys are not supported");

		tableLock.readLock().lock();
		try {
			int index = getIndex(key, buckets.length);

			ReentrantReadWriteLock.ReadLock bucketReadLock = locks[index].readLock();

			bucketReadLock.lock();
			try {
				Node<K, V> current = buckets[index];

				while (current != null) {
					if (current.key.equals(key)) {
						return current.value;
					}

					current = current.next;
				}

				return null;

			} finally {
				bucketReadLock.unlock();
			}

		} finally {
			tableLock.readLock().unlock();
		}
	}

	// Removes an entry and returns its value.
	public V remove(K key) {
		Objects.requireNonNull(key, "Null keys are not supported");

		tableLock.readLock().lock();
		try {
			int index = getIndex(key, buckets.length);

			ReentrantReadWriteLock.WriteLock bucketWriteLock = locks[index].writeLock();

			bucketWriteLock.lock();
			try {
				Node<K, V> current = buckets[index];
				Node<K, V> previous = null;

				while (current != null) {
					if (current.key.equals(key)) {

						if (previous == null) {
							buckets[index] = current.next;
						} else {
							previous.next = current.next;
						}

						size.decrementAndGet();
						return current.value;
					}

					previous = current;
					current = current.next;
				}

				return null;

			} finally {
				bucketWriteLock.unlock();
			}

		} finally {
			tableLock.readLock().unlock();
		}
	}

	public int size() {
		return size.get();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	private void resizeIfNeeded() {
		if (size.get() <= threshold) {
			return;
		}

		// Do not hold the table read lock when requesting this write lock.
		tableLock.writeLock().lock();
		try {
			// Another thread may already have resized the table.
			if (size.get() > threshold) {
				resize();
			}
		} finally {
			tableLock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private void resize() {
		int oldCapacity = buckets.length;

		if (oldCapacity >= MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		int newCapacity = oldCapacity * 2;

		Node<K, V>[] oldBuckets = buckets;

		Node<K, V>[] newBuckets = (Node<K, V>[]) new Node[newCapacity];

		// One new lock for every new bucket.
		ReentrantReadWriteLock[] newLocks = createLocks(newCapacity);

		// Transfer all existing nodes.
		for (Node<K, V> head : oldBuckets) {
			Node<K, V> current = head;

			while (current != null) {
				// Save the next node before changing current.next.
				Node<K, V> next = current.next;

				int newIndex = getIndex(current.key, newCapacity);

				// Insert this existing node into the new bucket.
				current.next = newBuckets[newIndex];
				newBuckets[newIndex] = current;

				current = next;
			}
		}

		// Publish the new table and its matching lock array.
		// No normal map operation can run while we hold the
		// table write lock.
		buckets = newBuckets;
		locks = newLocks;
		threshold = (int) (newCapacity * LOAD_FACTOR);
	}
}
