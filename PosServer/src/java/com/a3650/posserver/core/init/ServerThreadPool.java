package com.a3650.posserver.core.init;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义线程池
 * @author Administrator
 *
 */
public class ServerThreadPool {
	public final static int DEFAULT_POOL_SIZE = 5;//默认池大小
	private int poolSize = 0;
	private int workingNum = 0;
	private int finsishWorkingNum = 0;
	private ServerThread[] threadPool= null;
	// 任务队列，作为一个缓冲,List线程不安全
	private List<Runnable> taskQueue = new LinkedList<Runnable>();

	private ServerThreadPool(int poolSize){
		this.poolSize = poolSize;
		threadPool = new ServerThread[this.poolSize];
		for (int i = 0; i < this.poolSize; i++) {
			threadPool[i] = new ServerThread();
			threadPool[i].start();// 开启线程池中的线程
		}
	}
	
	public static ServerThreadPool getServerThreadPool(){
		return getServerThreadPool(DEFAULT_POOL_SIZE);
	}
	
	public static ServerThreadPool getServerThreadPool(int poolSize){
		return new ServerThreadPool(poolSize);
	}
	
	

	// 执行任务,其实只是把任务加入任务队列，什么时候执行有线程池管理器觉定
	public void execute(Runnable task) {
		synchronized (taskQueue) {
			taskQueue.add(task);
			taskQueue.notify();
		}
	}

	// 批量执行任务,其实只是把任务加入任务队列，什么时候执行有线程池管理器觉定
	public void execute(Runnable[] task) {
		synchronized (taskQueue) {
			for (Runnable t : task)
				taskQueue.add(t);
			taskQueue.notify();
		}
	}

	// 批量执行任务,其实只是把任务加入任务队列，什么时候执行有线程池管理器觉定
	public void execute(List<Runnable> task) {
		synchronized (taskQueue) {
			for (Runnable t : task)
				taskQueue.add(t);
			taskQueue.notify();
		}
	}

	
	// 返回工作线程的个数
	public int getWorkThreadNumber() {
		return workingNum;
	}

	// 返回已完成任务的个数,这里的已完成是只出了任务队列的任务个数，可能该任务并没有实际执行完成
	public int getFinishedTasknumber() {
		return finsishWorkingNum;
	}

	// 返回任务队列的长度，即还没处理的任务个数
	public int getWaitTasknumber() {
		return taskQueue.size();
	}

	// 覆盖toString方法，返回线程池信息：工作线程个数和已完成任务个数
	@Override
	public String toString() {
		return "WorkThread number:" + workingNum + "  finished task number:"
				+ finsishWorkingNum + "  wait task number:" + getWaitTasknumber();
	}

	
	private class ServerThread extends Thread{
		// 该工作线程是否有效，用于结束该工作线程
		private boolean isRunning = true;

		/*
		 * 关键所在啊，如果任务队列不空，则取出任务执行，若任务队列空，则等待
		 */
		@Override
		public void run() {
			Runnable r = null;
			while (isRunning) {// 注意，若线程无效则自然结束run方法，该线程就没用了
				synchronized (taskQueue) {
					while (isRunning && taskQueue.isEmpty()) {// 队列为空
						try {
							taskQueue.wait(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (!taskQueue.isEmpty())
						r = taskQueue.remove(0);// 取出任务
				}
				if (r != null) {
					r.run();// 执行任务
				}
				finsishWorkingNum++;
				r = null;
			}
		}

		// 停止工作，让该线程自然执行完run方法，自然结束
		public void stopWorker() {
			isRunning = false;
		}

	}
}
