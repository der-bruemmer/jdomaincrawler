package jdomaincrawler.controller;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jdomaincrawler.crawler.Crawler;
import jdomaincrawler.stripper.Stripper;

;

public class ThreadExecutor {
	private int poolSize = Integer.valueOf(PropertiesFactory.getProperties()
			.getProperty("minPoolSize", "2"));
	private int maxPoolSize = Integer.valueOf(PropertiesFactory.getProperties()
			.getProperty("maxPoolSize", "8"));
	private int timeout = Integer.valueOf(PropertiesFactory.getProperties()
			.getProperty("timeout", "2"));
	private ThreadPoolExecutor threadPool = null;
	private PriorityBlockingQueue<Runnable> queue;

	public ThreadExecutor() {

	}

	public void init() {
		Comparator<Runnable> comp = new Comparator<Runnable>() {

			@Override
			public int compare(final Runnable o1, final Runnable o2) {

				if (o1 instanceof Crawler) {
					if (o2 instanceof Crawler) {
						return (o1.hashCode() > o2.hashCode()) ? 1 : -1;
					} else {
						return 1;
					}
				} else if (o1 instanceof Stripper) {
					if (o2 instanceof Crawler) {
						return -1;
					} else if (o2 instanceof DirExplorer) {
						return 1;
					} else {
						return (o1.hashCode() > o2.hashCode()) ? 1 : -1;
					}
				} else if (o1 instanceof DirExplorer) {
					if (o2 instanceof DirExplorer) {
						return (o1.hashCode() > o2.hashCode()) ? 1 : -1;
					} else {
						return -1;
					}
				}
				return 0;
			}
		};
		queue = new PriorityBlockingQueue<Runnable>(
				Integer.valueOf(PropertiesFactory.getProperties().getProperty(
						"queueLength", "11")), comp);
		System.out.println(poolSize);
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, timeout,
				TimeUnit.SECONDS, queue);
	}

	public void executeTask(final Runnable task){
		threadPool.execute(task);
	}
	
	public void shutdownNow(){
		threadPool.shutdownNow();
	}

	public void shutdown() {
		threadPool.shutdown();
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(final int poolSize) {
		this.poolSize = poolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(final int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}
	
	public int getQueueSize(){
		return threadPool.getQueue().size();
	}
}
