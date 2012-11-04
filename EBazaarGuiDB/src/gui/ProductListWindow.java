package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


/**
 * Class Description: This window displays all known products belonging
 * to the selected catalog group. The catalog group is passed into
 * the constructor as  a parameter. In the final version of the
 * application, the products displayed should be all products in
 * the database having category group equal to this parameter. 
 */
public class ProductListWindow extends javax.swing.JWindow implements ParentWindow {

	//constants
	private final String AVAIL_BOOKS = "List of Available Books";
 	private final String AVAIL_CLOTHING = "List of Available Clothing";
    private final float [] COL_WIDTH_PROPORTIONS =	{1.0f};
 	
 	
 	/** Parent is used to return to main screen */
	private Window parent;

	
	/////////////constants
	
	//should be set to 'false' if data for table is obtained from a database
	//or some external file
	private final boolean USE_DEFAULT_DATA = false;//true;
	private final String SELECT = "Select";
	private final String BACK = "Back";
	
	
	//table configuration
	private Properties headers; 
	private String[] header;
	
	private final int TABLE_WIDTH = Math.round(0.75f*GuiControl.SCREEN_WIDTH);
    private final int DEFAULT_TABLE_HEIGHT = Math.round(0.75f*GuiControl.SCREEN_HEIGHT);

	//JPanels
	private JPanel mainPanel;
	private JPanel upperSubpanel;
	private JPanel lowerSubpanel;
	private JPanel labelPanel;
	
	//other widgets
	private String title;
	private String mainLabelText;
	private JLabel mainLabel;
	private JScrollPane tablePane;
	private JTable table;
	
	private String catalogType;
    private CustomTableModel model;
 
 	private void setTitleAndTableLabel(){
 		title = "Available "+catalogType;
 		mainLabelText = title;
 	}
 	/**
 	 * 
 	 * @param type - the category group (either books or clothes)
 	 */
	public ProductListWindow(String type) {
		this.catalogType = type;
			
		initializeWindow();
		initializeTableHeaderTable();
		defineMainPanel();
		getContentPane().add(mainPanel);
		
	}
	
	//this method iniitializes the table of headers for the table columns
	private void initializeTableHeaderTable() {
		headers = new Properties(); 
		headers.setProperty(DefaultData.BOOKS,this.AVAIL_BOOKS);
		headers.setProperty(DefaultData.CLOTHES,this.AVAIL_CLOTHING);		
		
	}

	private void initializeWindow() {
		setTitleAndTableLabel();
		setSize(GuiControl.SCREEN_WIDTH,GuiControl.SCREEN_HEIGHT);		
		GuiControl.centerFrameOnDesktop(this);
		
	}
	private void defineMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(GuiControl.FILLER_COLOR);
		mainPanel.setBorder(new WindowBorder(GuiControl.WINDOW_BORDER));
		defineUpperPanel();
		defineLowerPanel();
		mainPanel.add(upperSubpanel,BorderLayout.NORTH);
		mainPanel.add(lowerSubpanel,BorderLayout.SOUTH);
			
	}
	private void defineUpperPanel() {
		upperSubpanel = new JPanel();
		upperSubpanel.setLayout(new BorderLayout());
		upperSubpanel.setBackground(GuiControl.FILLER_COLOR);
		
		//create and add label
		createMainLabel();
		upperSubpanel.add(labelPanel,BorderLayout.NORTH);
		
		//create and add table
		createTableAndTablePane();
		JPanel tablePanePanel = GuiControl.createStandardTablePanePanel(table,tablePane);
	
		upperSubpanel.add(tablePanePanel,BorderLayout.CENTER);		
	}

	private void createMainLabel() {
		JLabel mainLabel = new JLabel(mainLabelText);
		Font f = GuiControl.makeVeryLargeFont(mainLabel.getFont());
		f = GuiControl.makeBoldFont(f);
		mainLabel.setFont(f);
		labelPanel = new JPanel();
		labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		labelPanel.setBackground(GuiControl.FILLER_COLOR);
		labelPanel.add(mainLabel);		
	}	
	
	private void createTableAndTablePane() {
		updateModel();
		table = new JTable(model);
		tablePane = new JScrollPane();
		tablePane.setPreferredSize(new Dimension(TABLE_WIDTH, DEFAULT_TABLE_HEIGHT));
		tablePane.getViewport().add(table);
		updateTable();		
	}
	
	public void updateModel(List<String[]> list){
		if(model == null) {
	        model = new CustomTableModel();    	    
		}
		
		String colHeader = headers.getProperty(catalogType);
		header = new String[]{colHeader};
		model.setTableValues(list);	
		//if(table != null) updateTable();
	}
	
	/**
	 * If default data is being used, this method obtains it
	 * and then passes it to updateModel(List). If real data is
	 * being used in the GuiDB exercise, read data from DB here,
	 * load into a list, and pass to updateModel(List). If
	 * controllers have been implemented, then the List of data
	 * should be passed to updateModel(List) from the appropriate
	 * controller class.
	 */
	private void updateModel() {
		List<String[]> theData = new ArrayList<String[]>();
        if(USE_DEFAULT_DATA) {
			DefaultData dd = DefaultData.getInstance();
			theData = dd.getCatalogWindowData(catalogType);
        }
		// homayoon @Oct.29
        else {
        	System.out.println("using db");
        	Connection con = homayoon.DBLocator.GetProductsDbConnection();
    		try {
    			Statement stmt = con.createStatement();
    			String SQLSt = "SELECT catalogid FROM CatalogType"
    							+ " WHERE catalogname=\'";
    			SQLSt += this.catalogType + "\'";
    			System.out.println(SQLSt);
    			ResultSet rs = stmt.executeQuery(SQLSt);
    			if (rs.next()){
    				String id = rs.getString("catalogid");
    				
    				stmt = con.createStatement();
    				SQLSt = "SELECT * FROM Product WHERE catalogid=" + id;
    				rs = stmt.executeQuery(SQLSt);
    				while (rs.next()) {
        				String name = rs.getString("productname");
        				id = rs.getString("productid");
        				System.out.println("id: "+ id + " name: "+name);
        				theData.add(new String[]{name});
    				}
    			} else {
            		String errMsg = "Unknown Catalog \'" + this.catalogType + "\'";
            		JOptionPane.showMessageDialog(ProductListWindow.this,         									          
            									          errMsg,
            									          "Error", 
            									          JOptionPane.ERROR_MESSAGE);
    			}
    			
    			stmt.close();
    			con.close();
    		}
    		catch(SQLException s){
    			s.printStackTrace();
    		}

        }
        // nooyamoh
        
		updateModel(theData);
 	}		
	
    private void updateTable() {
		GuiControl.createCustomColumns(table, 
                TABLE_WIDTH,
                COL_WIDTH_PROPORTIONS,
                header);
		       
        table.setModel(model);
        table.updateUI();
        repaint();
        
    }	

	private void defineLowerPanel() {
		 
		//select button
		JButton selectButton = new JButton(SELECT);
		selectButton.addActionListener(new SelectButtonListener());
		
		
		//back button
		JButton backButton = new JButton(BACK);
		backButton.addActionListener(new BackButtonListener());
		
		
		//create lower panel
		JButton [] buttons = {selectButton,backButton};
		lowerSubpanel = GuiControl.createStandardButtonPanel(buttons);
		
		
	}
	public void setParentWindow(Window parentWindow) {
		parent = parentWindow;
	}
	
	public Window getParentWindow() {
		return parent;
	}
		
	class SelectButtonListener implements ActionListener {
		//this method does the work when you are using default data
		HashMap<String,String[]> readProductDetailsData(String type) {
			
			DefaultData productData = DefaultData.getInstance();
			return productData.getProductDetailsData();
		}
		
		//This method does the work when you are
		//reading the database in the GuiDB exercise
		List<String> readProductDetailsList(String type){
			//implement -- read database to find data  
			List<String> data = new ArrayList<String>();
			
        	System.out.println("using db");
        	Connection con = homayoon.DBLocator.GetProductsDbConnection();
        	
    		try {
    			Statement stmt = con.createStatement();
    			String SQLSt = "SELECT productname,priceperunit,totalquantity,description"
    							+" FROM Product WHERE productname=\'";
    			SQLSt += type + "\'";
    			System.out.println(SQLSt);
    			ResultSet rs = stmt.executeQuery(SQLSt);
    			if (rs.next()){
    				data.add(rs.getString("productname"));
    				data.add(rs.getString("priceperunit"));
    				data.add(rs.getString("totalquantity"));
    				data.add(rs.getString("description"));
    			} else {
            		String errMsg = "Unknown Product \'" + type + "\'";
            		JOptionPane.showMessageDialog(ProductListWindow.this,         									          
            									          errMsg,
            									          "Error", 
            									          JOptionPane.ERROR_MESSAGE);
    			}
    			
    			stmt.close();
    			con.close();
    		}
    		catch(SQLException s){
    			s.printStackTrace();
    		}
			 
    		return data;			
		}
        public void actionPerformed(ActionEvent evt) {
        	
        	int selectedRow = table.getSelectedRow();
        	
        	if(selectedRow >= 0) {
        		String type = (String)table.getValueAt(selectedRow,0);
        		System.out.println(type);
        		
        		setVisible(false);
        		String[] productParams = null;
        		if(USE_DEFAULT_DATA){
        			HashMap<String,String[]> productTable = readProductDetailsData(type);
        			productParams = productTable.get(type);
        		}
        		else {
        			List<String> list = readProductDetailsList(type);
        			productParams = new String[list.size()];
        			for(int i = 0; i < productParams.length; ++i) {
        				productParams[i] = list.get(i);
        			} 
        		}
        		//System.out.println("Is product params an instance of Object[]? "+(productParams instanceof Object[]));
        		ProductDetailsWindow pd =  new ProductDetailsWindow(productParams);
        		
        		setVisible(false);
        		pd.setParentWindow(ProductListWindow.this);
        		pd.setVisible(true);
        		
        		
        	}
        	//value of selectedRow is -1, which means no row was selected
        	else {
        		String errMsg = "Please select a row.";
        		JOptionPane.showMessageDialog(ProductListWindow.this,         									          
        									          errMsg,
        									          "Error", 
        									          JOptionPane.ERROR_MESSAGE);

        	}
        	
        }
	}
	
	class BackButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			
			parent.setVisible(true);
			setVisible(false);
		    			
		}
	}		
	
	public static void main(String args[]) {
	
		(new ProductListWindow("Clothes")).setVisible(true);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3834024779601818169L;

}