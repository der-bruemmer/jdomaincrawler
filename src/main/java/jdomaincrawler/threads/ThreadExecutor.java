package jdomaincrawler.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jdomaincrawler.properties.PropertiesFactory;
;

public class ThreadExecutor {
	private int poolSize = Integer.valueOf(PropertiesFactory.getProperties().getProperty("pool.size","2"));
	private int maxPoolSize = Integer.valueOf(PropertiesFactory.getProperties().getProperty("pool.maxSize","8"));
	private int timeout = Integer.valueOf(PropertiesFactory.getProperties().getProperty("pool.timeout", "10"));
	private ThreadPoolExecutor threadPool = null;
	private LinkedBlockingQueue<Runnable> queue;
	
	public ThreadExecutor(){
		queue= new LinkedBlockingQueue<Runnable>();
		threadPool= new ThreadPoolExecutor(poolSize, maxPoolSize, timeout, TimeUnit.SECONDS, queue);
	}
	
	public void executeTask(Runnable task){
		threadPool.execute(task);
	}
	
	public void shutdown(){
		threadPool.shutdown();
	}
}
