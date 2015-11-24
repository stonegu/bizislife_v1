package com.bizislife.core.Schedule.Example;

public class ThreadTaskExample implements QuartzTask{

	@Override
	public void work(Object o) {
		
		
		Runnable run1 = new Runnable() {
			@Override
			public void run() {
				int i = 5;
				while (i>0) {
					i--;
					System.out.println("Hello world from "+Thread.currentThread().getName());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		Runnable run2 = new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					System.out.println("Goodbye from "+Thread.currentThread().getName());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		Thread t1 = new Thread(run1, "thread #1");
		Thread t2 = new Thread(run1, "thread #2");
		
		t1.start();
		t2.start();
		
	}

}
