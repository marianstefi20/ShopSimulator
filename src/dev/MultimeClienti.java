package dev;

import java.awt.Graphics;
import java.util.*;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MultimeClienti implements Runnable {
	private Vector<Client> clienti = new Vector<Client>();
	private static int intervalGenerare = 10000;
	private static int intervalActivare = 10000;
	private GUI gui;
	public volatile boolean start = false;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public MultimeClienti(GUI gui) {
		this.gui = gui;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public static void setSpawnTime(int spawnTime) {
		intervalGenerare = spawnTime * 1000;
	}
	
	public static void setActivationTime(int acTime) {
		intervalActivare = acTime;
	}
	
	public void decrement(int vitezaSimulare) {
		for(int i=0;i<clienti.size();i++) {
			clienti.elementAt(i).decrement(vitezaSimulare);
		}
	}
	
	private synchronized void randomCreation() {
		// Adauga la timp random pe pozitie random un nou client
		Random generator = new Random();
		Integer randX = generator.nextInt(GUI.LATIME - GUI.LATIME_CASA - 2*Client.LATIME_CLIENT) + Client.LATIME_CLIENT/2;
		Integer randY = generator.nextInt(GUI.INALTIME - 2*Client.INALTIME_CLIENT) + Client.INALTIME_CLIENT;		
		Integer randActiveTime = generator.nextInt(intervalActivare); // pentru a fi siguri ca asteptam cel putin 0 sec
		// Creare efectiva de client
		clienti.add(new Client(randX, randY, randActiveTime));
	}
	
	public synchronized void setDestinatie() {
		for(int i=0;i<clienti.size();i++) {
			Client temp = clienti.elementAt(i);
			// get casa marcat cu minim clienti
			int bufferNr = gui.caseMarcat.elementAt(0).getNrClienti(); int index = 0;
			for(int k=0;k<3;k++) {
				int nrClienti = gui.caseMarcat.elementAt(k).getNrClienti();
				if(nrClienti <= bufferNr) {
					bufferNr = nrClienti;
					index = k;
				}
			}
			Casa minCasa = gui.caseMarcat.elementAt(index);

			// seteaza pozitia x si y
			int destinatieX = minCasa.getX();
			int destinatieY = minCasa.getY();
			temp.setDestinatie(destinatieX, destinatieY);	
			temp.setIndexCasa(index);
		}
			
	}
	
	private synchronized void vfCozi() {
		for(int k=0;k<3;k++) {
			int nrClienti = gui.caseMarcat.elementAt(k).getNrClienti();
			if(nrClienti == 0) {
				gui.caseMarcat.elementAt(k).casaInchisa();
			} else gui.caseMarcat.elementAt(k).casaDeschisa();
		}
		for(int i=0;i<clienti.size();i++) {
			Client temp = clienti.elementAt(i);
			if(temp.atDestinatie()) {
				// Adauga la Casa noul client
				gui.caseMarcat.elementAt(temp.getIndexCasa()).addClient(temp);
				notifyAll();
			}
		}
	}
	
	public void desenareMultimeClienti(Graphics g) {
		for(int i=0;i<clienti.size();i++) {
			clienti.elementAt(i).desenareClient(g);
		}
	}
	
	@Override
	public void run() {
		try {
			Random generator = new Random();
			while(true) {
				if(start) {
					randomCreation();
					vfCozi();
				}
				Thread.sleep(generator.nextInt(intervalGenerare));
			}
		} catch(InterruptedException ie) {
			// am putea sa nu facem ceva
		}
	}
}
