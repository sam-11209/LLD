package com.system.lld;

import java.util.Objects;

public class CustomHashMap<K, V> {

	private static final int DEFAULT_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	private static class Node<K, V> {
		final int hash;
		final K key;
		V value;
		Node<K, V> next;

		Node(int hash, K key, V value) {
			this.hash = hash;
			this.key = key;
			this.value = value;
		}
	}

	private Node<K, V>[] buckets;
	private int size;
	private int threshold;
	private final float loadFactor;

	public CustomHashMap() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	public CustomHashMap(int capacity) {
		this(capacity, DEFAULT_LOAD_FACTOR);
	}

	@SuppressWarnings("unchecked")
	public CustomHashMap(int capacity, float loadFactor) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity must be positive");
		}

		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
			throw new IllegalArgumentException("Load factor must be positive");
		}

		int actualCapacity = tableSizeFor(Math.min(capacity, MAXIMUM_CAPACITY));

		this.loadFactor = loadFactor;
		this.buckets = (Node<K, V>[]) new Node[actualCapacity];
		this.threshold = calculateThreshold(actualCapacity);
	}

	private int calculateThreshold(int capacity) {
		return (int) (capacity * loadFactor);
	}

	// Spread higher hash bits into lower bits.
	private int hash(Object key) {
		if (key == null) {
			return 0;
		}

		int h = key.hashCode();
		return h ^ (h >>> 16);
	}

	private int getIndex(int hash) {
		return hash & (buckets.length - 1);
	}

	// Round capacity up to a power of two.
	private int tableSizeFor(int capacity) {
		int n = capacity - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;

		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}

	public void put(K key, V value) {
		int hash = hash(key);
		int index = getIndex(hash);
		Node<K, V> current = buckets[index];

		// Update an existing mapping.
		while (current != null) {
			if (current.hash == hash && Objects.equals(current.key, key)) {
				current.value = value;
				return;
			}
			current = current.next;
		}

		// Insert a new mapping at the bucket's head.
		Node<K, V> node = new Node<>(hash, key, value);
		node.next = buckets[index];
		buckets[index] = node;
		size++;

		if (size > threshold && buckets.length < MAXIMUM_CAPACITY) {
			resize();
		}
	}

	public V get(K key) {
		int hash = hash(key);
		int index = getIndex(hash);
		Node<K, V> current = buckets[index];

		while (current != null) {
			if (current.hash == hash && Objects.equals(current.key, key)) {
				return current.value;
			}
			current = current.next;
		}

		return null;
	}

	public boolean containsKey(K key) {
		int hash = hash(key);
		int index = getIndex(hash);
		Node<K, V> current = buckets[index];

		while (current != null) {
			if (current.hash == hash && Objects.equals(current.key, key)) {
				return true;
			}
			current = current.next;
		}

		return false;
	}

	public V remove(K key) {
		int hash = hash(key);
		int index = getIndex(hash);
		Node<K, V> current = buckets[index];
		Node<K, V> previous = null;

		while (current != null) {
			if (current.hash == hash && Objects.equals(current.key, key)) {
				if (previous == null) {
					buckets[index] = current.next;
				} else {
					previous.next = current.next;
				}

				size--;
				return current.value;
			}

			previous = current;
			current = current.next;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void resize() {

		// Step 1: Store the current array capacity.
		// Example: oldCapacity = 4
		int oldCapacity = buckets.length;

		// Step 2: Double the capacity.
		// Example: newCapacity = 8
		int newCapacity = oldCapacity << 1;

		// Step 3: Stop if capacity exceeds the supported maximum.
		if (newCapacity <= 0 || newCapacity > MAXIMUM_CAPACITY) {
			return;
		}

		// Step 4: Keep a reference to the old bucket array.
		// We need to read all existing nodes from it.
		Node<K, V>[] oldBuckets = buckets;

		// Step 5: Create a new, larger bucket array.
		Node<K, V>[] newBuckets = (Node<K, V>[]) new Node[newCapacity];

		// Step 6: Visit every bucket in the old array.
		for (Node<K, V> head : oldBuckets) {

			// Start at the first node in this bucket's linked list.
			Node<K, V> current = head;

			// Step 7: Visit every node in the linked list.
			while (current != null) {

				// Save the next node before changing current.next.
				Node<K, V> next = current.next;

				// Step 8: Recalculate the bucket index.
				// Reuse the stored hash, but use the NEW capacity.
				int newIndex = current.hash & (newCapacity - 1);

				// Step 9: Link this existing node into its new bucket.
				// Insert at the beginning of the new bucket's list.
				current.next = newBuckets[newIndex];
				newBuckets[newIndex] = current;

				// Step 10: Move to the next node from the OLD list.
				current = next;
			}
		}

		// Step 11: Make the new array the active bucket array.
		buckets = newBuckets;

		// Step 12: Recalculate when the next resize should occur.
		threshold = calculateThreshold(newCapacity);
	}

	public int size() {
		return size;
	}

	public int capacity() {
		return buckets.length;
	}
}
