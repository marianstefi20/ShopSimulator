package dev;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class Controller implements ActionListener {
	private GUI gui;
	private Vector<String> simulationData = new Vector<String>();
	
	public Controller(GUI gui) {
		this.gui = gui;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		/**
		 * String-ul command contine actiunile trimise de catre GUI 
		 */
		String command = e.getActionCommand();
		System.out.println("Comanda este: " + command);

		switch(command) {
			case "simulare":
				initSimulare();				
				break;
		}
	}
	
	public void initSimulare() {
		simulationData.add(0, gui.durataSimulare.getText());
		simulationData.add(1, gui.greutateCosuri.getText());
		simulationData.add(2, gui.frecAparitie.getText());
		
		// Setare valori de mai sus in celelalte componente
		MultimeClienti.setSpawnTime(Integer.parseInt(simulationData.elementAt(2)));
		Client.setGreutateCos(Integer.parseInt(simulationData.elementAt(1)));
		GUI.setDurataSimulare(Integer.parseInt(simulationData.elementAt(0)));
		
		// GUI. startSIMULARE
		gui.startActiv = true;
	}
}
