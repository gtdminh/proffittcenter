/*
 * Customers.java
 *
 * Created on 14 April 2008, 17:26
 */
package proffittcenter;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author  HP_Owner
 */
public class Customers extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Customers");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("FirstName"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Surname"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Phone"));
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("Address1"));
    @SuppressWarnings("unchecked")
    private boolean b7 = tableHeader.add(bundle.getString("Address2"));
    @SuppressWarnings("unchecked")
    private boolean b8 = tableHeader.add(bundle.getString("Address3"));
    @SuppressWarnings("unchecked")
    private boolean b9 = tableHeader.add(bundle.getString("County"));
    @SuppressWarnings("unchecked")
    private boolean b10 = tableHeader.add(bundle.getString("Limit"));
    private String byCustomer = SQL.byCustomer;
    private long idSelected = 0;
    private String type;
    private long id = -1;
    private String name;
    private String firstName;
    private String surname;
    private Date date;
    private String phone;
    private String address1;
    private String address2;
    private String address3;
    private String county;
    private long limit;
    String code = "";
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.RIGHT, 
        SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.RIGHT, 
        SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT, 
        SwingConstants.RIGHT, SwingConstants.RIGHT};
    private Object[] line = {id, firstName, surname, date, phone, address1, 
        address2, address3, county, limit};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private AbstractTableModel model = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }

        public int getRowCount() {
            return rowData.size();
        }

        public int getColumnCount() {
            return tableHeader.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//ID
                return line[0];
            } else if (columnIndex == 1) {//Name
                return line[1];
            } else if (columnIndex == 2) {
                return line[2];
            } else if (columnIndex == 3) {
                return line[3];
            } else if (columnIndex == 4) {
                return line[4];
            } else if (columnIndex == 5) {
                return line[5];
            } else if (columnIndex == 6) {
                return line[6];
            } else if (columnIndex == 7) {
                return line[7];
            } else if (columnIndex == 8) {
                return line[8];
            } else if (columnIndex == 9) {
                return line[9];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    public String fullName = "";
    private Long customer;
    private PreparedStatement customerSelect;
    private ResultSet rs;
    private int selection;
    private int columnSelected;
    private boolean stop;
    private boolean ownUse;

    /** Creates new form Customers
     * @param parent
     * @param modal  
     */
    public Customers(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(okBtn);
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(1);
        column.setPreferredWidth(400);
        column = jTable1.getColumnModel().getColumn(2);
        column.setPreferredWidth(400);
        column = jTable1.getColumnModel().getColumn(3);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(4);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(5);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(6);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(7);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(8);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(9);
        column.setPreferredWidth(300);
        mhr = new MyHeaderRenderer(align);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setDefaultRenderer(Money.class, mtcr);
        jTable1.setDefaultRenderer(PerCent.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Customers", Main.mainHelpSet);
    }

    public long execute(String type,boolean ownUse) {
        this.ownUse = ownUse;
        stoppedCheckBox.setSelected(true);
        code="";
        idSelected = SaleType.CUSTOMER.code() * 10000l;
        this.type = type;
        selection = -1;
        surname = "";
        if (type.compareTo("ByName") == 0) {
            columnSelected = 2;
        } else if(type.compareTo("ByNumber") == 0) {
            columnSelected = 0;
        } else {//own use customers
            columnSelected = 4;            
        }
        drawGrid();
        Audio.play("Tada");
        JRootPane rp = getRootPane();
        rp.setDefaultButton(okBtn);
        jCustomerCode.setText("");
        jCustomerCode.requestFocus();
        setVisible(true);
        return idSelected;
    }

    private void drawGrid() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement customers = null;
        try {
            if (columnSelected == 0) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " ID");
            } else if (columnSelected == 1) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " Name2");
            } else if (columnSelected == 2) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " Name1");
            } else if (columnSelected == 3) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " WhenCreated");  
            } else if (columnSelected == 4) {//own use customers
                customers = Main.getConnection().prepareStatement(SQL.byOwnUseCustomer + " WhenCreated"); 
            } else if (columnSelected == 5) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " Name2");
            } else if (columnSelected == 6) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " Name2");
            } else if (columnSelected == 7) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " Name2");
            } else if (columnSelected == 8) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " Name2");
            } else if (columnSelected == 9) {
                customers = Main.getConnection().prepareStatement(SQL.byCustomer + " Name2");            } else if (columnSelected == 1) {
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            String c=code+'*';
            customers.setString(1, code+'%');
            stop = stoppedCheckBox.isSelected();
            customers.setBoolean(2, stop);
            rs = customers.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            int i = 1;
            if (rs.first()) {
                do {//customers to display
                    id = rs.getLong("ID");
                    firstName = rs.getString("Name2");
                    surname = rs.getString("Name1");
                    date = rs.getDate("WhenCreated");
                    phone = rs.getString("Phone1");
                    address1 = rs.getString("Address1");
                    address2 = rs.getString("Address2");
                    address3 = rs.getString("Address3");
                    county = rs.getString("County");
                    limit = rs.getLong("Creditlimit");
                    Object[] aLine = {id, firstName, surname, date, phone, address1, address2, address3, county, limit};
                    rowData.add(aLine);
                    i++;
                } while (rs.next());
            }
            rs.close();
            customers.close(); 
            if(i>1){
                model.fireTableDataChanged();
            }           
            if(i==2){
                selection=0;
                jTable1.setRowSelectionInterval(selection, selection);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (SQLException ex) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();
        infoButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        jCustomerCode = new javax.swing.JTextField();
        stoppedCheckBox = new javax.swing.JCheckBox();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Customers"); // NOI18N
        setTitle(bundle.getString("Customers.title_1")); // NOI18N
        setLocationByPlatform(true);
        setModal(true);
        setName("customers"); // NOI18N
        addWindowListener(formListener);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(formListener);
        jTable1.addFocusListener(formListener);
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setToolTipText(bundle.getString("Customers.newButton.toolTipText")); // NOI18N
        jPanel1.setFocusable(false);
        jPanel1.addFocusListener(formListener);

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton.setToolTipText(bundle.getString("Customers.closeButton.toolTipText")); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.addActionListener(formListener);

        okBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okBtn.setToolTipText(bundle.getString("Customers.okBtn.toolTipText")); // NOI18N
        okBtn.setContentAreaFilled(false);
        okBtn.addActionListener(formListener);

        infoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        infoButton.setToolTipText(bundle.getString("Customers.infoButton.toolTipText")); // NOI18N
        infoButton.setBorderPainted(false);
        infoButton.setContentAreaFilled(false);
        infoButton.addActionListener(formListener);

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/plus.png"))); // NOI18N
        newButton.setMnemonic('+');
        newButton.setToolTipText(bundle.getString("Customers.newButton.toolTipText")); // NOI18N
        newButton.setContentAreaFilled(false);
        newButton.addActionListener(formListener);

        jCustomerCode.setToolTipText(bundle.getString("jCustomerCode.tooltip")); // NOI18N
        jCustomerCode.addFocusListener(formListener);
        jCustomerCode.addKeyListener(formListener);

        stoppedCheckBox.setText(bundle.getString("Customers.stoppedCheckBox.text")); // NOI18N
        stoppedCheckBox.addActionListener(formListener);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCustomerCode, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(stoppedCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(newButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {closeButton, infoButton, okBtn});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCustomerCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(stoppedCheckBox))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(closeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, Short.MAX_VALUE)
                        .addComponent(infoButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, Short.MAX_VALUE)
                        .addComponent(newButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(okBtn, 0, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jCustomerCode, newButton});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton, infoButton, okBtn});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.WindowListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == closeButton) {
                Customers.this.closeButtonActionPerformed(evt);
            }
            else if (evt.getSource() == okBtn) {
                Customers.this.okBtnActionPerformed(evt);
            }
            else if (evt.getSource() == infoButton) {
                Customers.this.infoButtonActionPerformed(evt);
            }
            else if (evt.getSource() == newButton) {
                Customers.this.newButtonActionPerformed(evt);
            }
            else if (evt.getSource() == stoppedCheckBox) {
                Customers.this.stoppedCheckBoxActionPerformed(evt);
            }
        }

        public void focusGained(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == jTable1) {
                Customers.this.jTable1FocusGained(evt);
            }
            else if (evt.getSource() == jPanel1) {
                Customers.this.jPanel1FocusGained(evt);
            }
        }

        public void focusLost(java.awt.event.FocusEvent evt) {
            if (evt.getSource() == jCustomerCode) {
                Customers.this.jCustomerCodeFocusLost(evt);
            }
        }

        public void keyPressed(java.awt.event.KeyEvent evt) {
        }

        public void keyReleased(java.awt.event.KeyEvent evt) {
            if (evt.getSource() == jCustomerCode) {
                Customers.this.jCustomerCodeKeyReleased(evt);
            }
        }

        public void keyTyped(java.awt.event.KeyEvent evt) {
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == jTable1) {
                Customers.this.onSelectRow(evt);
            }
        }

        public void mouseEntered(java.awt.event.MouseEvent evt) {
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
        }

        public void mousePressed(java.awt.event.MouseEvent evt) {
        }

        public void mouseReleased(java.awt.event.MouseEvent evt) {
        }

        public void windowActivated(java.awt.event.WindowEvent evt) {
        }

        public void windowClosed(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == Customers.this) {
                Customers.this.formWindowClosed(evt);
            }
        }

        public void windowClosing(java.awt.event.WindowEvent evt) {
        }

        public void windowDeactivated(java.awt.event.WindowEvent evt) {
        }

        public void windowDeiconified(java.awt.event.WindowEvent evt) {
        }

        public void windowIconified(java.awt.event.WindowEvent evt) {
        }

        public void windowOpened(java.awt.event.WindowEvent evt) {
        }
    }// </editor-fold>//GEN-END:initComponents

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    try {
        PreparedStatement customers;
        idSelected = Main.salesScreen.getCustomer();
        customers = Main.getConnection().prepareStatement("SELECT * FROM Customers WHERE ID=?");
        customers.setLong(1, idSelected);
        ResultSet rs = customers.executeQuery();
        if (rs.first()) {
            firstName = rs.getString("Name2");
            surname = rs.getString("Name1");
        } else {
            firstName = "";
            surname = "";
        }
        setVisible(false);
    } catch (SQLException ex) {
        Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_closeButtonActionPerformed

private void onSelectRow(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onSelectRow
    jTable1.invalidate();
    Audio.play("Beep");
    jCustomerCode.requestFocus();
}//GEN-LAST:event_onSelectRow

private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    selection = jTable1.getSelectedRow();
    if (Main.salesScreen.getCustomer() != SaleType.CUSTOMER.code() * 10000l) {
        //a customer already selected
        idSelected = Main.salesScreen.getCustomer();
        try {
            PreparedStatement customers;
            idSelected = Main.salesScreen.getCustomer();
            customers = Main.getConnection().prepareStatement("SELECT * FROM Customers WHERE ID=?");
            customers.setLong(1, idSelected);
            rs = customers.executeQuery();
            if (rs.first()) {
                firstName = rs.getString("Name2");
                surname = rs.getString("Name1");
            } else {
                firstName = "";
                surname = "";
            }
            setVisible(false);
        } catch (SQLException ex) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
        }
        Audio.play("Ring");
        setVisible(false);
        return;
    }
    if (selection == -1) {
        idSelected = SaleType.CUSTOMER.code() * 10000l;
        firstName = "";
        surname = "";
        Audio.play("Ring");
    } else {
        idSelected = (Long)jTable1.getValueAt(selection, 0);
        if (idSelected != Main.salesScreen.getCustomer()) {
            customer = idSelected;
            firstName = (String) jTable1.getValueAt(selection, 1);//used elsewhere
            surname = (String) jTable1.getValueAt(selection, 2);//used elsewhere
        }
        Audio.play("Beep");
    }
    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    setVisible(false);
}//GEN-LAST:event_okBtnActionPerformed

private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
    selection = jTable1.getSelectedRow();
    if (selection == -1) {//no selected row
        return;
    }
    selection = jTable1.convertRowIndexToModel(selection);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    customer = (Long) model.getValueAt(selection, 0);
//    Main.customer.setLocationRelativeTo(this);
    boolean b = Main.customer.execute(customer,false);
    //FormRestore.destroyPosition(this, root);
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    drawGrid();
}//GEN-LAST:event_infoButtonActionPerformed

private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
    if (Main.operator.handleAccounts||Main.operator.isOwner()) {
        int op = Main.operator.getOperator();//needed to clear temporary operator
        boolean b = Main.customer.execute(-1l,ownUse);
        drawGrid();
    }
}//GEN-LAST:event_newButtonActionPerformed

private void jCustomerCodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCustomerCodeFocusLost
        code = jCustomerCode.getText();
        if (code.length()>0&&Character.isLetter(code.charAt(0))) {
            //do alpha search on code
        } else {
            String ss = StringOps.numericOnly(code);
            code="";//otherwise would be used for alpha search
            if (ss.isEmpty()) {
                return;
            }
            if (ss.length() > 11) {
                jCustomerCode.setText("");
                jCustomerCode.requestFocus();
                return;
            }
            long bc = Long.parseLong(ss);
            long k = SaleType.CUSTOMER.code() * 10000l;
            if (bc >= SaleType.CUSTOMER.code() * 10000l) {
                if (isCustomer(bc)) {
                    customer = bc;
                    idSelected = bc;
                    firstName = getFirstName();
                    surname = getSurname();
                    setVisible(false);
                } else {
                    jCustomerCode.setText("");
                    jCustomerCode.requestFocus();
                    Audio.play("Ring");
                }
            } else {
                jCustomerCode.setText("");
                jCustomerCode.requestFocus();
                Audio.play("Ring");
                return;
            }
            jCustomerCode.requestFocus();
            Audio.play("Beep");
        }
}//GEN-LAST:event_jCustomerCodeFocusLost

private void jPanel1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanel1FocusGained
    jCustomerCode.requestFocus();
}//GEN-LAST:event_jPanel1FocusGained

private void jTable1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTable1FocusGained
    jCustomerCode.requestFocus();
}//GEN-LAST:event_jTable1FocusGained

private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    try {
        PreparedStatement customers;
        idSelected = Main.salesScreen.getCustomer();
        customers = Main.getConnection().prepareStatement("SELECT * FROM Customers WHERE ID=?");
        customers.setLong(1, idSelected);
        ResultSet rs = customers.executeQuery();
        if (rs.first()) {
            firstName = rs.getString("Name2");
            surname = rs.getString("Name1");
        } else {
            firstName = "";
            surname = "";
        }
        setVisible(false);
    } catch (SQLException ex) {
        Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_formWindowClosed

private void jCustomerCodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCustomerCodeKeyReleased
    int xx = evt.getKeyChar();
    int xt = evt.getKeyCode();
    if (evt.getKeyCode() == KeyEvent.VK_UP) {
        selection = jTable1.getSelectedRow() - 1;
        if (selection >= 0) {
            jTable1.setRowSelectionInterval(selection, selection);
        }
    } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
        selection = jTable1.getSelectedRow() + 1;
        int i = jTable1.getRowCount() - 1;
        if (selection < jTable1.getRowCount()) {
            jTable1.setRowSelectionInterval(selection, selection);
        }
    } else if (evt.getKeyCode() == KeyEvent.VK_ADD || evt.getKeyCode() == KeyEvent.VK_EQUALS) {
        jCustomerCode.setText("");
        if (Main.settingsTab.isOwnerManager()) {
            int op = Main.operator.getOperator();//needed to clear temporary operator
//            Main.customer.setLocationRelativeTo(this);
            boolean b = Main.customer.execute(-1l,false);
            //FormRestore.destroyPosition(this, root);
            drawGrid();
        }
    } else if (evt.getKeyCode() == KeyEvent.VK_I && evt.isControlDown()) {
        jCustomerCode.setText("");
        selection = jTable1.getSelectedRow();
        if (selection == -1) {//no selected row
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        customer = (Long) model.getValueAt(selection, 0);
//        Main.customer.setLocationRelativeTo(this);
        boolean b = Main.customer.execute(customer,false);
        //FormRestore.destroyPosition(this, root);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        drawGrid();
    }else {        
        code=jCustomerCode.getText();
        if(code.isEmpty()||Character.isLetter(code.charAt(0))){
            drawGrid();
        }else {
            code="";
        }
    }
}//GEN-LAST:event_jCustomerCodeKeyReleased

private void stoppedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stoppedCheckBoxActionPerformed
    drawGrid();
}//GEN-LAST:event_stoppedCheckBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Customers dialog = new Customers(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton infoButton;
    private javax.swing.JTextField jCustomerCode;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton newButton;
    private javax.swing.JButton okBtn;
    private javax.swing.JCheckBox stoppedCheckBox;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    public String getSurname(long aCustomer) {
        try {
            customerSelect = Main.getConnection().prepareStatement(SQL.isCustomer);
            customerSelect.setLong(1, aCustomer);
            rs = customerSelect.executeQuery();
            if (rs.first()) {
                surname = rs.getString("Name1");
                rs.close();
                return surname;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getFirstName(long aCustomer) {
        try {
            customerSelect = Main.getConnection().prepareStatement(SQL.isCustomer);
            customerSelect.setLong(1, aCustomer);
            rs = customerSelect.executeQuery();
            if (rs.first()) {
                firstName = rs.getString("Name2");
                rs.close();
                return firstName;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    boolean isCustomer(long id) {
        try {
            customerSelect = Main.getConnection().prepareStatement(SQL.isCustomer);
            customerSelect.setLong(1, id);
            rs = customerSelect.executeQuery();
            if (rs.first()) {
                firstName = rs.getString("Name2");
                surname = rs.getString("Name1");
                rs.close();
                return true;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Customers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
