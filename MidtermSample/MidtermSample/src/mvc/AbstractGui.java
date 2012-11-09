package mvc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

abstract public class AbstractGui extends JDialog {
	
	private static final long serialVersionUID = 1L;
	protected String mainLabelStr = "Enter Data";
	protected final String PRINT_BUTN = "Print";
	protected final String CANCEL_BUTN = "Cancel";	
	protected final String TO_PRINT = "To Print";
	protected JTextField textField;
	protected JButton printButton;
	protected JButton cancelButton;
	JPanel mainPanel;
	JPanel upper, middle, lower;

	abstract public void addPrintListener();
	abstract public void addCancelListener();
	abstract public void prelimInitialize();
	
	public AbstractGui() {
		prelimInitialize();	
		handleWindowClosing();
		initializeWindow();
		defineMainPanel();
		getContentPane().add(mainPanel);			
	}
	protected void handleWindowClosing() {
        addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent w) {
                dispose();
                System.exit(0);
           }
        }); 				
	}		
	protected void initializeWindow() {	
		setSize(300,150);		
	}	
	protected void defineMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		defineUpperPanel();
		defineMiddlePanel();
		defineLowerPanel();
		mainPanel.add(upper,BorderLayout.NORTH);
		mainPanel.add(middle,BorderLayout.CENTER);
		mainPanel.add(lower,BorderLayout.SOUTH);			
	}
	protected void defineUpperPanel(){
		upper = new JPanel();		
		upper.setLayout(new FlowLayout(FlowLayout.CENTER));		
		JLabel mainLabel = new JLabel(mainLabelStr);		
		upper.add(mainLabel);					
	}
	
	protected void defineMiddlePanel(){
		middle = new JPanel();		
		middle.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel label = new JLabel(TO_PRINT);
		textField = new JTextField(10);		
		middle.add(label);
		middle.add(textField);
	}
	
	protected void defineLowerPanel(){		
		printButton = new JButton(PRINT_BUTN);
		addPrintListener();
		cancelButton = new JButton(CANCEL_BUTN);
		addCancelListener();
		JButton [] buttons = {printButton,cancelButton};
		lower = createStandardButtonPanel(buttons, Color.WHITE);
	}
	protected static JPanel createStandardButtonPanel(JButton[] buttons,
													  Color backgroundColor){
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));   
        buttonPanel.setBackground(backgroundColor);
        if(buttons != null && buttons.length>0) {
            for(int i = 0; i < buttons.length; ++i) {
                buttonPanel.add(buttons[i]);    
            }
        }
        return buttonPanel;	        
	} 	
}  

