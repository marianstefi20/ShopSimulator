package dev;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.AncestorListener;

/**
* Clasa principala a jocului - In aceasta clasa se integreaza celelalte
* clase si se creeaza fereastra de joc
*/
public class GUI extends JFrame implements Runnable, ActionListener {
	// Constante globale
	private static final long serialVersionUID = 1L;
	public static final int LATIME = 800;
	public static final int INALTIME = 600;
	public static final int LATIME_CASA = 200;
	
	public MyPanel drawingPanel = new MyPanel(this);
	public JFrame mainFrame;
	
	// Viteza cu care se face refresh - i.e. cat doarme thread-ul princ.
	private static int simulationTime;
	private int vitezaSimulare = 50;
	public boolean startActiv = false;
	private boolean pauza = false; // pp ca nu e pauza by default
	
	// Setul de date transmise simulatorului
	public JTextField durataSimulare = new JTextField(5);
	public JTextField frecAparitie = new JTextField(5);
	public JTextField frecActivare = new JTextField(5);
	public JTextField greutateCosuri = new JTextField(5);
	
	// Metoda ce inchide thread-ul principal al jocului SpaceInvaders
	public volatile boolean running = true;
	private Vector<String> simulationData = new Vector<String>();
	
	// Instantierea celorlalte clase
	public MultimeClienti clienti;
	public Simulator simulator;
	public Vector<Casa> caseMarcat = new Vector<Casa>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public GUI(String titluSimulare) {
		super(titluSimulare);		
		setSize(LATIME, INALTIME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		clienti = new MultimeClienti(this);
		caseMarcat.add(new Casa(GUI.LATIME - GUI.LATIME_CASA, 140));
		caseMarcat.add(new Casa(GUI.LATIME - GUI.LATIME_CASA, 300));
		caseMarcat.add(new Casa(GUI.LATIME - GUI.LATIME_CASA, 460));

		// Metoda aceasta trebuie sa fie muncita putin
		startSimulare();
		this.prepareGUI();	
	}
	
	public void actionPerformed(ActionEvent e) {
		/**
		 * String-ul command contine actiunile trimise de catre GUI 
		 */
		String command = e.getActionCommand();
		System.out.println("Comanda este: " + command);

		switch(command) {
			case "simulare":
				clienti.start = true;
				initSimulare();				
				break;
		}
	}
	
	public void initSimulare() {
		/**
		 * 1. Durata de simulare
		 * 2. frec. de aparitie
		 * 3. frec. de activare
		 * 4. Greutatea medie a cosurilor
		 * */
		simulationData.add(0, durataSimulare.getText());
		simulationData.add(1, frecAparitie.getText());
		simulationData.add(2, frecActivare.getText());
		simulationData.add(3, greutateCosuri.getText());
		
		// Setare valori de mai sus in celelalte componente
		if(simulationData.elementAt(0).isEmpty()) System.out.println("Timpul total al simularii nu este setat!");
		else GUI.setDurataSimulare(Integer.parseInt(simulationData.elementAt(0)));
		
		if(simulationData.elementAt(1).isEmpty()) System.out.println("Frec. de aparitie nu este setata");
		else MultimeClienti.setSpawnTime(Integer.parseInt(simulationData.elementAt(1)));
		
		if(simulationData.elementAt(2).isEmpty()) System.out.println("Frec. de activare nu este setata!");
		else MultimeClienti.setActivationTime(Integer.parseInt(simulationData.elementAt(2)));
		
		if(simulationData.elementAt(3).isEmpty()) System.out.println("Greutatea cosurilor nu este setata!");
		else Client.setGreutateCos(Integer.parseInt(simulationData.elementAt(3)));
		
		scheduler.schedule(new Runnable() {
			public void run() { 
				startActiv = false;
    	    }
		}, Integer.parseInt(simulationData.elementAt(0)), SECONDS);
		
		// Seteaza flag-ul care permite redesenarea in thread-ul principal
		startActiv = true;
		
	}
	
	public void prepareGUI() {	
		JPanel panel1 = new JPanel(false);
		panel1.setSize(new Dimension(LATIME, 60));
		panel1.setBackground(Color.white);
		panel1.setLayout(new FlowLayout());

		JLabel dS = new JLabel("Durata Sim.: ");
		panel1.add(dS);
		panel1.add(durataSimulare);
		
		JLabel frecAparitieClient = new JLabel("Frec. aparitie: ");
		panel1.add(frecAparitieClient);
		panel1.add(frecAparitie);
		
		JLabel timpActivare = new JLabel("Timp activare: ");
		panel1.add(timpActivare);
		panel1.add(frecActivare);
		
		JLabel greutateMedieCosuri = new JLabel("Greutate cosuri: ");
		panel1.add(greutateMedieCosuri);
		panel1.add(greutateCosuri);
		
		JButton ok = new JButton("OK");
		ok.setActionCommand("simulare");
		ok.addActionListener(this);
		panel1.add(ok);
		
		getContentPane().add(panel1);
		
		// adaugam panoul de desenare la acest JFrame
		getContentPane().add(drawingPanel);
		setVisible(true);
	}
	
	public static void setDurataSimulare(int durata) {
		simulationTime = durata;
	}
	
	/**
	* Cand scoti mouse-ul vrem sa pauzam toata simularea
	*/
	public void pauzaSimulare(boolean state) {
		pauza = state;
	}


	/**
	* Metoda care incepe executia jocului <=> este vorba de un nou proces
	* => vom folosi iarasi un Thread
	*/
	public void startSimulare() {
		Thread thread = new Thread(this);
		thread.start();
	}
    
	/**
	* Metoda run() obligatorie datorita implementarii interfetei Runnable
	* Ea ruleaza un Thread principal cu care se muta clientii si se redeseneaza toate elementele
	*/
    @Override
	public void run() {
		int count = 0;
		while(running) {
			try {
				Thread.sleep(vitezaSimulare);
			} catch(InterruptedException ie) {
				// deocamdata nu prea facem ceva
			}
			if(!pauza) {
				
			}
			if(startActiv) {
				repaint(); // Metoda actualizeaza elementele ecranului
				// verifica care clienti au acum timpul sub 0 si ii pune in miscare daca da
				clienti.setDestinatie();
				count++;
			}
		}
	}

    /**
     * Incepe initializarea programului
     */
    public static void main(String []args) {
    	GUI init = new GUI("Simulare clienti");
    }

}