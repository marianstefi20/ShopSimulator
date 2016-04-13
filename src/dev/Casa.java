package dev;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class Casa implements Runnable {
	public static final int LATIME_CASA = 35;
	public static final int INALTIME_CASA = 30;
	private static final int VITEZA_PROCESARE = 100; // o cant. generica de procesat pe sec
	public boolean stare = false; 
	private int x = 0;
	private int y = 0;
	//Queue<Client> coadaClienti = new LinkedList<Client>(Collections.synchronizedList(new LinkedList<Client>()));
	private Vector<Client> coadaClienti = new Vector<Client>();
	public Casa(int xVal, int yVal) {
		this.x = xVal;
		this.y = yVal;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void casaDeschisa() {
		this.stare = true;
	}
	
	public void casaInchisa() {
		this.stare = false;
	}
	
	//public Queue<Client> getCoadaClienti() {
		//return coadaClienti;
	//}
	
	public synchronized void addClient(Client c) {
		coadaClienti.addElement(c);
		notifyAll();
	}
	
	public synchronized void removeClient() throws InterruptedException {
		while(coadaClienti.size() == 0)
			wait();
		//Client c = coadaClienti.poll();
		Client c = coadaClienti.elementAt(0);
		coadaClienti.removeElementAt(0);
		c.startMiscare();
		System.out.println("Client a fost deservit de catre casa!\n");
		notifyAll();
		//return coadaClienti.poll();
	}
	
	public synchronized int getNrClienti() {
		notifyAll();
		return coadaClienti.size();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void deseneazaCasa(Graphics g) {
		if(stare) 
			g.setColor(Color.green);
		else 
			g.setColor(Color.red);
		g.drawLine(x, y, x, y+20);
		g.drawLine(x+1, y, x+1, y+20);
	}

	@Override
	public void run() {
		try {
			while(true) {
				removeClient();
				Thread.sleep((long)(Client.greutateCos * 1000 * (Math.random()%0.2+0.4)));
			}
		} catch(InterruptedException e) {
			System.out.println("Intrerupere\n");
			System.out.println(e.toString());
		}			
		
	}
}
