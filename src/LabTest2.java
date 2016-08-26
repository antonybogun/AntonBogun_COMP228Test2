import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LabTest2 extends JFrame{
	JTabbedPane tabbedPane = new JTabbedPane();
	JPanel view = new JPanel();
	JPanel mainPanel;
	JPanel searchAndEdit, 
	searchAndEditHeader, 
	searchAndEditHeader1, 
	searchAndEditHeader2,
	editFields;
	JButton searchB, updateB;
	JTextField searchText, id, department, firstName, lastName, address, city, postalCode, province;

	JTable table, table2;
	Vector <Object>  rowsView, columnsView;
	Vector <Object> rowsSearch, columnsSearch;
	DefaultTableModel tableModel, tableModel2;
	JScrollPane scrollPane, scrollPane2;

	Connection conn;
	Statement st;
	ResultSet rs;

	public LabTest2() {

		rowsView=new Vector<>();
		columnsView= new Vector<>();
		rowsSearch= new Vector<>();
		columnsSearch = new Vector<>();

		tableModel = new DefaultTableModel();
		tableModel2 = new DefaultTableModel();
		table = new JTable(tableModel);
		table2 = new JTable(tableModel2);
		scrollPane= new JScrollPane(table);//ScrollPane
		scrollPane2= new JScrollPane(table2);//ScrollPane

		mainPanel=new JPanel();
		setSize(750,480);

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add("North", new JLabel("Employee List"));
		mainPanel.add("Center",scrollPane);

		searchAndEdit = new JPanel();
		searchAndEdit.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		searchAndEditHeader = new JPanel(new FlowLayout());
		searchText = new JTextField(15);
		searchB = new JButton("Search by Last Name");
		searchAndEditHeader.add(searchText);
		searchAndEditHeader.add(searchB);

		c.fill = GridBagConstraints.BOTH;
		c.weightx=0.1;
		c.gridx = 0;
		c.gridy = 0;
		searchAndEdit.add(searchAndEditHeader,c);

		editFields = new JPanel(new GridBagLayout());
		GridBagConstraints ce = new GridBagConstraints();

		id = new JTextField(15);
		id.setEditable(false);
		department = new JTextField(15);
		firstName = new JTextField(15);
		lastName = new JTextField(15);
		address = new JTextField(15);
		city = new JTextField(15);;
		postalCode = new JTextField(15);
		province = new JTextField(15);
		ce.insets= new Insets (3,3,3,3);
		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 0;
		editFields.add(new JLabel("ID"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 0;
		editFields.add(id , ce);

		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 1;
		editFields.add(new JLabel("Department"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 1;
		editFields.add(department , ce);

		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 2;
		editFields.add(new JLabel("First Name"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 2;
		editFields.add(firstName , ce);

		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 3;
		editFields.add(new JLabel("Last Name"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 3;
		editFields.add(lastName , ce);

		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 4;
		editFields.add(new JLabel("Address"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 4;
		editFields.add(address , ce);

		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 5;
		editFields.add(new JLabel("City"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 5;
		editFields.add(city , ce);

		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 6;
		editFields.add(new JLabel("Postal Code"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 6;
		editFields.add(postalCode , ce);

		ce.fill = GridBagConstraints.HORIZONTAL;
		ce.gridx = 0;
		ce.gridy = 7;
		editFields.add(new JLabel("Province"), ce);

		ce.fill = GridBagConstraints.EAST;
		ce.gridx = 1;
		ce.gridy = 7;
		editFields.add(province , ce);

		c.fill = GridBagConstraints.BOTH;
		c.weightx=0.1;
		c.gridx = 1;
		c.gridy = 0;
		searchAndEdit.add(editFields,c);

		updateB = new JButton("Update Information");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=0.1;
		c.gridx = 2;
		c.gridy = 0;
		searchAndEdit.add(updateB,c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		searchAndEdit.add(scrollPane2,c);

		tabbedPane.addTab("View", null, mainPanel, "Viewing table only");
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);

		tabbedPane.addTab("Search and Edit", null, searchAndEdit, "Searching and Editing table content");
		searchB.addActionListener(new SearchListener());
		updateB.addActionListener(new UpdateListener());
		refreshData();

	}

	private void refreshData()	{
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://localhost/Company?"
					+ "user=root&password=admin";
			conn = DriverManager.getConnection(url);

			st = conn.createStatement();
			rs = st.executeQuery("SELECT * FROM Employees");
			ResultSetMetaData md = rs.getMetaData();
			//create columns headers
			columnsView.clear();
			rowsView.clear();
			for( int i=1;i <= md.getColumnCount();i++){
				columnsView.addElement(md.getColumnName(i));
			}

			int row=0;
			while(rs.next())
			{
				Vector vRow = new Vector(); //to store the current row
				//System.out.println("Row " +row+"\n");
				for( int i=1;i <= md.getColumnCount();i++)
				{

					String columnValue = rs.getString(i);
					vRow.addElement(columnValue);
				}
				row+=1;
				rowsView.addElement(vRow);
			}

			tableModel.setDataVector(rowsView,columnsView);

			//use the same data for the second JTable
			tableModel2.setDataVector(rowsView, columnsView);
			table2.getSelectionModel().addListSelectionListener(new
					ListSelectionListener(){
				public void valueChanged(ListSelectionEvent event) {
					if(table2.getSelectedRow()!=-1){
						id.setText(table2.getValueAt(table2.getSelectedRow(),0).toString());
						department.setText(table2.getValueAt(table2.getSelectedRow(),1).toString());
						firstName.setText(table2.getValueAt(table2.getSelectedRow(),2).toString());
						lastName.setText(table2.getValueAt(table2.getSelectedRow(),3).toString());
						address.setText(table2.getValueAt(table2.getSelectedRow(),4).toString());
						city.setText(table2.getValueAt(table2.getSelectedRow(),5).toString());
						postalCode.setText(table2.getValueAt(table2.getSelectedRow(),6).toString());
						province.setText(table2.getValueAt(table2.getSelectedRow(),7).toString());
					}
				}
			});
			rs.close();
			searchText.setText("");

			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if (conn != null) { 
				try { 
					conn.close(); // close the connection after you're finished with it
				} catch (SQLException ex) {/*nothing here*/} 
				conn = null; 
			}
		}
	}
	class SearchListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			// Select only some of the stuff from the table
			String searchT = searchText.getText();
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String url = "jdbc:mysql://localhost/Company?"
						+ "user=root&password=admin";
				conn = DriverManager.getConnection(url);

				PreparedStatement stq; 
				if (isNullOrEmpty(searchT)) {
					stq = conn.prepareStatement("SELECT * FROM Employees");
				}
				else{
					stq = conn.prepareStatement("SELECT * FROM Employees where LastName = ?");
					stq.setString(1, searchT);
				}	
				rs = stq.executeQuery();
				ResultSetMetaData md = rs.getMetaData();
				//create columns headers
				columnsView.clear();
				rowsView.clear();
				for( int i=1;i <= md.getColumnCount();i++)
				{
					columnsView.addElement(md.getColumnName(i));
				}

				int row=0;
				while(rs.next())
				{
					Vector vRow = new Vector(); //to store the current row
					//System.out.println("Row " +row+"\n");
					for( int i=1;i <= md.getColumnCount();i++)
					{

						String columnValue = rs.getString(i);
						vRow.addElement(columnValue);
					}
					row+=1;
					rowsView.addElement(vRow);
				}
				tableModel2.setDataVector(rowsView, columnsView);
				rs.close();

			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally
			{
				if (conn != null) { 
					try { 
						conn.close(); // close the connection after you're finished with it
					} catch (SQLException ex) {/*nothing here*/} 
					conn = null; 
				}
			}
		}
	}

	class UpdateListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int r = table2.getSelectedRow();
			if (r == -1) {
				JOptionPane.showMessageDialog(null, "No row is selected");
				return;
			}
			String departmentT = department.getText();
			String firstNameT = firstName.getText();
			String lastNameT = lastName.getText();
			String addressT = address.getText();
			String cityT = city.getText();
			String postalCodeT = postalCode.getText();
			String provinceT = province.getText();

			if (isNullOrEmpty(departmentT)) {
				JOptionPane.showMessageDialog(null, "Deparment is required field");
				return;
			}
			if (isNullOrEmpty(firstNameT)) {
				JOptionPane.showMessageDialog(null, "First Name is required field");
				return;
			}
			if (isNullOrEmpty(lastNameT)) {
				JOptionPane.showMessageDialog(null, "Last Name is required field");
				return;
			}
			if (isNullOrEmpty(addressT)) {
				JOptionPane.showMessageDialog(null, "Address is required field");
				return;
			}
			if (isNullOrEmpty(cityT)) {
				JOptionPane.showMessageDialog(null, "City is required field");
				return;
			}
			if (isNullOrEmpty(postalCodeT)) {
				JOptionPane.showMessageDialog(null, "Postal Code is required field");
				return;
			}
			if (isNullOrEmpty(provinceT)) {
				JOptionPane.showMessageDialog(null, "Province is required field");
				return;
			}
			int n = table2.getSelectedRow();
			String idS = table2.getModel().getValueAt(table2.getSelectedRow(), 0).toString();
			Integer idSS = Integer.parseInt(idS);
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String url = "jdbc:mysql://localhost/Company?"
						+ "user=root&password=admin";
				conn = DriverManager.getConnection(url);
				PreparedStatement s = conn.prepareStatement("Update Employees Set Department = ?, FirstName = ?, LastName = ?,"
						+"Address = ?, City = ?, PostalCode = ?, Province =? where EmployeeID = ?");
				s.setString(1, departmentT);
				s.setString(2, firstNameT);
				s.setString(3, lastNameT);
				s.setString(4, addressT);
				s.setString(5, cityT);
				s.setString(6, postalCodeT);
				s.setString(7, provinceT);
				s.setInt(8, idSS);
				s.executeUpdate();
				refreshData();
				table2.setRowSelectionInterval(n,n);

			}catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Failed to update row: " + ex.getMessage());
			}
			finally
			{
				if (conn != null) { 
					try { 
						conn.close(); // close the connection after you're finished with it
					} catch (SQLException ex) {/*nothing here*/} 
					conn = null; 
				}
			}
		}
	}
	public static void main(String[] args) {
		LabTest2 tc=new LabTest2();
		tc.setMinimumSize(new Dimension(1000,300));
		tc.setPreferredSize(new Dimension(1000,300));
		tc.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}
}
