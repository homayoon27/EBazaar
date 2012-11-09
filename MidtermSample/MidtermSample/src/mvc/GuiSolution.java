package mvc;

import mvc.AbstractGui;

public class GuiSolution extends AbstractGui {
	
	Control control;
	
	public GuiSolution() {
		super();
	}
	public void addPrintListener() {
		printButton.addActionListener(control.getNewPrintListener());
	}
	public void addCancelListener() {
		cancelButton.addActionListener(control.getNewCancelListener());
	}
	
	public void prelimInitialize() {
		control = new Control(this);
	}
	
	public String getTextField(){
		return textField.getText(); 
	}
	public void setTextField(String str) {
		textField.setText(str);
	}
	
	public static void main(String[] args) {
		GuiSolution g = new GuiSolution();
		g.setVisible(true);		
	}    
	private static final long serialVersionUID = 1L;

}
