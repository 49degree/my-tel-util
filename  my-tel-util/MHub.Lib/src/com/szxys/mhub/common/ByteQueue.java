/*
 * @(#)ByteQueue.java	1.00 11/04/20
 *
 * Copyright (c) 2011-2013  New Element Inc. 
 * 9/10f, Building 2, Financial Base, No.6 Keyuan Road, 
 * Nanshan District, Shenzhen 518057
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * New Element Medical Equipment Technology Development CO., Ltd 
 * ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with New Element.
 */
package com.szxys.mhub.common;

/**
 * BYTE队列，用于日志模块缓存日志
 * @author xak
 */
class ByteQueue {
	private byte[] queue;
	private int capacity;
	private int size;
	private int head;
	private int tail;

	/**
	 * 默认构造函数
	 * @param cap	容量大小（字节）
	 */
	public ByteQueue(int cap) {
		capacity = (cap > 0) ? cap : 1; 
		queue = new byte[capacity];
		head = 0;
		tail = 0;
		size = 0;
	}

	/**
	 * 获取队列容量
	 * @return 队列容量
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * 获取数据总量
	 * @return 数据总量
	 */
	public synchronized int getSize() {
		return size;
	}	

	/**
	 * 添加数据
	 * @param data 需要添加的数据
	 * @throws InterruptedException
	 */
	public synchronized void add(byte[] data) throws InterruptedException {
		int ptr = 0;
		while (ptr < data.length) {			
			waitWhileFull();
			int space = capacity - size;
			int distToEnd = capacity - head;
			int blockLen = Math.min(space, distToEnd);
			int bytesRemaining = data.length - ptr;
			int copyLen = Math.min(blockLen, bytesRemaining);
			System.arraycopy(data, ptr, queue, head, copyLen);
			head = (head + copyLen) % capacity;
			size += copyLen;
			ptr += copyLen;			
			notifyAll();
		}
	}

	/**
	 * 获取所有数据并从队列移除
	 * @return 所有数据
	 * @throws InterruptedException
	 */
	public synchronized byte[] removeAll() throws InterruptedException {		
		waitWhileEmpty();		
		byte[] list = new byte[size];		
		int distToEnd = capacity - tail;
		int copyLen = Math.min(size, distToEnd);
		System.arraycopy(queue, tail, list, 0, copyLen);
		if (size > copyLen) {
			System.arraycopy(queue, 0, list, copyLen, size - copyLen);
		}
		tail = (tail + size) % capacity;
		size = 0;
		notifyAll();
		return list;
	}		

	private synchronized boolean isEmpty() {
		return (size == 0);
	}

	private synchronized boolean isFull() {
		return (size == capacity);
	}	
	
	private synchronized void waitWhileEmpty() throws InterruptedException {
		while (isEmpty()) {			
			wait();			
		}
	}	

	private synchronized void waitWhileFull() throws InterruptedException {
		while (isFull()) {			
			wait();			
		}
	}
}