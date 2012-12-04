package jdomaincrawler.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jdomaincrawler.properties.PropertiesFactory;
;

public class ThreadExecutor {
	private int poolSize = 2;
	private int maxPoolSize = 8;
	private int timeout = 2;
	private ThreadPoolExecutor threadPool = null;
	private LinkedBlockingQueue<Runnable> queue;
	
	public ThreadExecutor(){
		
	}
	
	public void init(){
		queue= new LinkedBlockingQueue<Runnable>();
		threadPool= new ThreadPoolExecutor(poolSize, maxPoolSize, timeout, TimeUnit.SECONDS, queue);
	}
	
	public void executeTask(Runnable task){
		threadPool.execute(task);
	}
	
	public void shutdown(){
		threadPool.shutdown();
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
