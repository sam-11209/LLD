package com.system.lld;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


import lombok.Getter;

public class ConcurrentHashMapMyImpl<K, V> {

	private int capacity;
	private final float loadfactor;

	private AtomicInteger size = new AtomicInteger(0);
	private AtomicInteger load = new AtomicInteger(0);

	Node<K, V>[] buckets;

	private final ReentrantReadWriteLock[] readWriteLock;

	private final ReentrantReadWriteLock tableLock = new ReentrantReadWriteLock();

	@SuppressWarnings("unchecked")
	public ConcurrentHashMapMyImpl() {
		capacity = 16;
		loadfactor = 0.75f;
		buckets = new Node[capacity];
		load.set((int) (capacity * loadfactor));
		this.readWriteLock = createLock(capacity);
	}

	@SuppressWarnings("unchecked")
	public ConcurrentHashMapMyImpl(int capcaity, float lf) {
		this.capacity = capcaity;
		this.loadfactor = lf;
		buckets = new Node[capacity];
		load.set((int) (capacity * loadfactor));
		this.readWriteLock = createLock(capacity);
	}

	private ReentrantReadWriteLock[] createLock(int capacity) {
		ReentrantReadWriteLock[] rWeLock = new ReentrantReadWriteLock[capacity];

		for (int i = 0; i < capacity; i++) {
			rWeLock[i] = new ReentrantReadWriteLock();
		}
		return rWeLock;
	}

	public V put(K key, V val) {

		if (key == null || val == null) {
			throw new IllegalArgumentException("Null key val not allowed");
		}
		try {
			tableLock.readLock().lock();
			int hash = getHash(key);
			int index = mapIndex(hash);

			WriteLock writeLock = readWriteLock[index].writeLock();
			writeLock.lock();
			try {
				Node<K, V> curNode = buckets[index];

				// New Node Came
				if (curNode == null) {
					Node<K, V> newNode = new Node<>(hash, key, val, null);
					buckets[index] = newNode;
					size.incrementAndGet();
					checkResizing();
					return val;
				}

				// find existing key and replace
				while (curNode != null) {
					if (Objects.equals(val, curNode.val)) {
						V oldVal = curNode.val;
						curNode.val = val;
						return oldVal;
					}
					curNode = curNode.next;
				}

				// new key insert on head
				Node<K, V> newNode = new Node<>(hash, key, val, buckets[index]);
				buckets[index] = newNode;
				size.incrementAndGet();
				checkResizing();
				return val;

			} finally {
				writeLock.unlock();
			}

		} finally {
			tableLock.readLock().unlock();

		}
	}

	public V get(K key) {

		try {
			tableLock.readLock().lock();
			if (Objects.isNull(key)) {
				throw new IllegalArgumentException("Null key not allowed");
			}
			int hash = getHash(key);
			int index = mapIndex(hash);

			ReadLock readLock = readWriteLock[index].readLock();
			readLock.lock();

			try {
				Node<K, V> curNode = buckets[index];
				while (curNode != null) {
					if (Objects.equals(key, curNode.getKey())) {
						return curNode.getVal();
					}
					curNode = curNode.next;
				}

			} finally {
				readLock.unlock();

			}

		} finally {
			tableLock.readLock().unlock();

		}
		return null;

	}

	public V remove(K key) {
		tableLock.readLock().lock();
		try {
			if (Objects.isNull(key)) {
				throw new IllegalArgumentException("Null key not allowed");
			}
			int hash = getHash(key);
			int index = mapIndex(hash);

			WriteLock writeLock = readWriteLock[index].writeLock();
			writeLock.lock();
			try {
				Node<K, V> curNode = buckets[index];
				Node<K, V> prevNode = null;

				while (curNode != null) {
					if (Objects.equals(curNode.getKey(), key)) {

						if (prevNode == null) {
							buckets[index] = curNode.next;
						} else {
							prevNode.next = curNode.next;
						}
						size.decrementAndGet();
						return curNode.val;
					}
					prevNode = curNode;
					curNode = curNode.next;
				}
			} finally {
				writeLock.unlock();
			}
		} finally {
			tableLock.readLock().unlock();
		}
		return null;
	}

	private void checkResizing() {

		int threshold = load.get();
		int curSize = size.get();
		if (curSize < threshold) {
			return;
		}
		tableLock.writeLock().lock();

		try {
			resize();
		} finally {
			tableLock.writeLock().unlock();
		}
	}

	private void resize() {

		Node<K, V>[] oldbuckets = buckets;
		int newCapacity = capacity * 2;

		Node<K, V>[] newbuckets = new Node[newCapacity];

		for (Node<K, V> head : oldbuckets) {
			Node<K, V> cur = head;
			while (cur != null) {
				Node<K, V> next = cur.next;
				int index = mapIndex(cur.getHash());
				Node<K, V> newbucketNode = newbuckets[index];
				if (newbucketNode == null) {
					newbuckets[index] = cur;

				} else {
					cur.next = newbuckets[index];
					newbuckets[index] = cur;
				}
				cur = next;
			}

		}
		buckets = newbuckets;
		capacity = newCapacity;
		load.set((int) (capacity * loadfactor));
		createLock(capacity);
	}

	private int getHash(K key) {
		return key.hashCode();
	}

	private int mapIndex(int hash) {
		return hash % (buckets.length - 1);

	}
}

@Getter
class Node<K, V> {
	private final int hash;
	private final K key;
	V val;
	Node<K, V> next;

	Node(int hash, K key, V val, Node<K, V> next) {
		this.hash = hash;
		this.key = key;
		this.val = val;
		this.next = next;
	}
}