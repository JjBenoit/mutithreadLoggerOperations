package jjben;


import jjben.actor.ProducerThreadLog;
import jjben.share.AsynchronousLogger;

public class Launcher {
	
	
	private final AsynchronousLogger queue;
	private final Thread [] threadsProducer;
	
	public static void main(String[] args) throws InterruptedException {
		
		Launcher launcher = new Launcher(10,1,10);
		launcher.start();
	}
	
	
	
	public Launcher(int nbThreadProducer, int nbThreadConsummer,int aggragationPeriodictyInSeconds) {
		
		super();
		queue = new AsynchronousLogger(nbThreadConsummer,aggragationPeriodictyInSeconds);
		threadsProducer = new Thread [nbThreadProducer];
		
		
	}

	public void start() throws InterruptedException {
		
		queue.startLogger();
		
		for (int i = 0; i < threadsProducer.length; i++) {
			 threadsProducer[i] = new Thread(new ProducerThreadLog(queue), "ProducerThreadLog "+i);
			 threadsProducer[i].start();

		 }
		
	}

	
	
	

	
}
