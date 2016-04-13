package dev;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.Graphics;
import java.awt.Image;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Client - contine metodele principale de generare a clientului, de animatie, 
 * impreuna cu setul specific de stari definitorii
 * */

public class Client implements Runnable {
	private static final int VITEZA_CLIENT = 20;
	private static final int PAS_CLIENT = 1;
	public static final int LATIME_CLIENT = 30;
	public static final int INALTIME_CLIENT = 30;
	private int x = 0;
	private int y = 0;
	private int destinatieX;
	private int destinatieY;
	private int indexCasa; // casa la care vrea clientul sa mearga
	private int timpDeActivare; // timpul la care sa inceapa sa mearga spre casa
	public static int greutateCos;
	private boolean stareMiscare = false;
	
	// Variabile de simulare
	long timpInit;
	long durataMersCasa;
	long timpInitCasa;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private Image imagineClient = new javax.swing.ImageIcon("imagini/client.png").getImage();
	private GUI gui = null;
	
	public Client(int xVal, int yVal) {
		x = xVal;
		y = yVal;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public Client(int xVal, int yVal, int timpDeActivare) {
		this.x = xVal;
		this.y = yVal;
		this.timpDeActivare = timpDeActivare;
		Thread thread = new Thread(this);
		thread.start();
		scheduler.schedule(new Runnable() {
			public void run() { 
				Client.this.startMiscare();
    	    }
		}, timpDeActivare, SECONDS);
	}
	
	public void startMiscare() {
		this.stareMiscare = true;
	}
	
	public void stopMiscare() {
		this.stareMiscare = false;
	}
	
	public boolean getStareMiscare() {
		return stareMiscare;
	}
		
	public void setDestinatie(int destinatieX, int destinatieY) {
		this.destinatieX = destinatieX;
		this.destinatieY = destinatieY;
	}
	
	public void setDestinatieX(int destinatieX) {
		this.destinatieX = destinatieX;
	}
	
	public void setIndexCasa(int index) {
		this.indexCasa = index;
	}
	
	public int getIndexCasa() {
		return indexCasa;
	}
	
	public void timpInitCasa() {
		timpInitCasa = System.currentTimeMillis();
	}
	
	// Metoda este statica fiindca ne dorim ca toti clientii sa aiba o cant. generica
	public static void setGreutateCos(int cantitate) {
		greutateCos = cantitate;
	}
	
	public int getX() {
		return this.x;
	}
	
	public void decrement(int vitezaSimulare) {
		this.timpDeActivare -= vitezaSimulare;
	}
	
	public int getTimpActivare() {
		return timpDeActivare;
	}
	
	public int getY() {
		return this.y;
	}
	
	public boolean atDestinatie() {
		if(destinatieX == x) 
			return true;
		else return false;
	}
	
	private synchronized void mutaClient() {
		/* Vrem sa il miscam in linie dreapta dar trebuie sa stim cum...derivata
		 * de ordinul intai atunci
		 * */
		stareMiscare = true; 
		int deriv = (destinatieX-x != 0) ? (destinatieY-y)/(destinatieX-x) : 0;
		x += PAS_CLIENT;
		if(x < 600)
			y += deriv * PAS_CLIENT;
	}
	
	public void desenareClient(Graphics g) {
		g.drawImage(imagineClient, x, y, gui);
	}
	
	// Micul thread ce misca clientul
	@Override
	public void run() {
		try {
			timpInit = System.currentTimeMillis();
			while(true) {
				if(stareMiscare) {
					mutaClient();
					if(atDestinatie()) {
						// chiar numai odata se poate intampla asta
						timpInitCasa = System.currentTimeMillis();
						stareMiscare = false;
						durataMersCasa = Math.abs(System.currentTimeMillis() - timpInit) / 1000;
						GUI.commentTextArea.append("Clientul a ajuns la casa in "+ durataMersCasa +" secunde\n");
					}
				}
				Thread.sleep(VITEZA_CLIENT);
			}
		} catch(InterruptedException ie) {
			// am putea sa nu facem ceva
		}
	}
	
}
