package mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Control {
	
	private GuiSolution gs;
	PrintHandler printHandler=new PrintHandler();
	
	Control(GuiSolution guis) {
		this.gs = guis;
	}
	
	ActionListener getNewCancelListener () {
		return new CancelListener();
	}
	ActionListener getNewPrintListener () {
		return new PrintListener();
	}
	
	private class PrintListener implements ActionListener {
    	public void actionPerformed(ActionEvent evt){
    		printHandler.printToConsole(gs.getTextField());
    	}
    }
	
    private class CancelListener implements ActionListener {
    	public void actionPerformed(ActionEvent evt){
    		gs.setTextField("");
    	}
    } 
}
