package com.guanri.fsk.conversion;

import java.util.LinkedList;

public class SourceQueue {
	private LinkedList<byte[]> sourceList = new LinkedList<byte[]>();
	public synchronized void put(byte[] o) {
		sourceList.addLast(o); // 尾部添加元素
	}
	public synchronized byte[] get() {
		return sourceList.removeFirst();// 从前面删除
	}

	public synchronized boolean empty() {
		return sourceList.isEmpty();
	}
	
	public synchronized int size() {
		return sourceList.size();
	}
}
