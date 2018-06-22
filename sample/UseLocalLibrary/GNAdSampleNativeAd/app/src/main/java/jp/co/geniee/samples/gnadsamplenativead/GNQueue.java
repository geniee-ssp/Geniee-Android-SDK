package jp.co.geniee.samples.gnadsamplenativead;

import java.util.ArrayList;

public class GNQueue {
	int maxSize;
	ArrayList<Object> queue;
	
	public GNQueue(int maxSize) {
		super();
		this.maxSize = maxSize;
		this.queue = new ArrayList<Object>();
	}
	
	public Object dequeue() {
		Object obj;
		if (queue.size() == 0) return null;
		obj = queue.get(0);
		if (obj != null) {
			queue.remove(0);
		}
		return obj;
	}
	
	public void enqueue(Object obj) {
		if (obj == null) return;
		if (queue.size() >= maxSize) {
			queue.remove(0);
		}
		queue.add(obj);
	}
	
	public int size() {
		return queue.size();
	}

}
