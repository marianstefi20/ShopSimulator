package dev;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

public class MyPanel extends JPanel {	
	private GUI gui;
	
	public MyPanel(GUI gui) {
		this.gui = gui;
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
        g.fillRect(0,60, GUI.LATIME, GUI.INALTIME);
        
        // Desenam o linie sub care tot ce se intampla e grav - in esenta ai pierdut
        g.setColor(Color.black);
        g.drawLine(GUI.LATIME - GUI.LATIME_CASA, 0, GUI.LATIME-GUI.LATIME_CASA, 140);
        g.drawLine(GUI.LATIME - GUI.LATIME_CASA, 160, GUI.LATIME-GUI.LATIME_CASA, 300);
        g.drawLine(GUI.LATIME - GUI.LATIME_CASA, 320, GUI.LATIME-GUI.LATIME_CASA, 460);
        g.drawLine(GUI.LATIME - GUI.LATIME_CASA, 480, GUI.LATIME-GUI.LATIME_CASA, 600);
	
        /*
         * Urmeaza un tip generic de desenare - in esenta se rezuma la a gasi componenta
         * si la a desena pe un offsreen
         * */
        gui.clienti.desenareMultimeClienti(g);
        
        gui.caseMarcat.elementAt(0).deseneazaCasa(g);
        gui.caseMarcat.elementAt(1).deseneazaCasa(g);
        gui.caseMarcat.elementAt(2).deseneazaCasa(g);
	}
}
