/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SaleEscape.java
 *
 * Created on 28-Oct-2009, 19:56:33
 */

package proffittcenter;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.Cursor;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author HP_Owner
 */
public class Sale_1 extends EscapeDialog {

    Preferences root = Preferences.userNodeForPackage(getClass());
    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Sale");
    Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    boolean b0 = tableHeader.add("ID");
    @SuppressWarnings("unchecked")
    boolean b1 = tableHeader.add(bundle.getString("Sale"));
    @SuppressWarnings("unchecked")
    boolean b2 = tableHeader.add(bundle.getString("Quantity"));
    @SuppressWarnings("unchecked")
    boolean b3 = tableHeader.add(bundle.getString("Product"));
    @SuppressWarnings("unchecked")
    boolean b4 = tableHeader.add(bundle.getString("Charge"));
    @SuppressWarnings("unchecked")
    boolean b5 = tableHeader.add(bundle.getString("Track"));
    private int saleLine;
    private Long sale;
    private int quantity;
    private int price;
    private int wholesalePrice;
    private int packSize;
    private int taxRate;
    private String product;
    private int charge;
    private short waste;
    Integer customer;
    private String track;
    private int profit;
    private int encode;
    private short fixedProfit;
    Object[] line = {saleLine, sale, quantity, product, charge,track };
    Vector<Object[]> rowData = new Vector<Object[]>();
    AbstractTableModel model = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }

        public int getColumnCount() {
            return tableHeader.size();
        }

        public int getRowCount() {
            return rowData.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {
                //SaleLine ID
                return line[0];
            } else if (columnIndex == 1) {
                //Sale ID
                return line[1];
            } else if (columnIndex == 2) {
                //Quantity
                return line[2];
            } else if (columnIndex == 3) {
                //product
                return line[3];
            } else if (columnIndex == 4) {
                //Price
                return (new Money((Integer) line[4])).toString();
            } else if (columnIndex == 5) {
                //Track
                return line[5];
            }else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private int till;
    private int discount;
    private PreparedStatement proffittSQL;
    private Timestamp whenCreated;

    /** Creates new form SaleEscape */
    public Sale_1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(3);
        column.setPreferredWidth(200);
        column = jTable1.getColumnModel().getColumn(5);
        column.setPreferredWidth(150);
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Sale", Main.mainHelpSet);
    }

    private boolean downButtonAction() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement saleps;
        try {
            saleps = Main.getConnection().prepareStatement(SQL.dSale);
            saleps.setLong(1, sale);
            saleps.setInt(2, till); //TillId
            saleps.setInt(3, till); //TillId
            saleps.setInt(4, customer); //Customer
            saleps.setInt(5, customer); //Customer
            ResultSet rs = saleps.executeQuery();
            upButton.setEnabled(true);
            if (rs.first()) {
                //sale to display
                sale = rs.getLong("ID");
                idTextField.setText(sale.toString());
                whenCreatedDate = rs.getDate("whenCreated");
                dateTextField.setText(whenCreatedDate.toString());
                whenCreatedTime = rs.getTime("whenCreated");
                timeTextField.setText(whenCreatedTime.toString());
                fixedProfit=rs.getShort("FixedProfit");
                if (rs.getInt("Operator") == 0) {
                    operator = "Default";
                } else {
                    operator = rs.getString("Operators.Description");
                }
                operatorTextField.setText(operator);
                total = rs.getInt("Total");
                totalTextField.setText((new Money(total)).toString());
                tax = rs.getInt("Tax");
                taxTextField.setText((new Money(tax)).toString());
                cash = rs.getInt("Cash");
                cashTextField.setText((new Money(cash)).toString());
                cheque = rs.getInt("Cheque");
                chequeTextField.setText((new Money(cheque)).toString());
                debit = rs.getInt("Debit");
                debitTextField.setText((new Money(debit)).toString());
                coupon = rs.getInt("Coupon");
                couponTextField.setText((new Money(coupon)).toString());
                String customerName = rs.getString("Name2") + ", " + rs.getString("Name1");
                customerIdTextField.setText(customerName);
                tillId = rs.getInt("TillId");
                tillIdTextField.setText(tillId.toString());
                waste = rs.getShort("Waste");
                wasteText.setText(SaleType.description(waste));
                rs.close();
                saleps.close();
                if(fixedProfit==0){
                    profitButton.setEnabled(false);
                }else{
                    profitButton.setEnabled(true);
                }
                drawGrid();
            } else {
                downButton.setEnabled(false);
                Audio.play("Ring");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sale_1.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
            jTextField1.requestFocus();
            return true;
        }
        setCursor(Cursor.getDefaultCursor());
        Audio.play("Beep");
        jTextField1.requestFocus();
        return false;
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        closeButton = new JButton();
        upButton = new JButton();
        downButton = new JButton();
        printButton = new JButton();
        invoiceButton = new JButton();
        jTextField1 = new JTextField();
        profitButton = new JToggleButton();
        profitTextField = new JTextField();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        idTextField = new JTextField();
        dateTextField = new JTextField();
        timeTextField = new JTextField();
        jLabel4 = new JLabel();
        totalTextField = new JTextField();
        jLabel5 = new JLabel();
        operatorTextField = new JTextField();
        jLabel6 = new JLabel();
        taxTextField = new JTextField();
        jLabel7 = new JLabel();
        cashTextField = new JTextField();
        jLabel8 = new JLabel();
        chequeTextField = new JTextField();
        jLabel10 = new JLabel();
        debitTextField = new JTextField();
        jLabel11 = new JLabel();
        couponTextField = new JTextField();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jLabel12 = new JLabel();
        tillIdTextField = new JTextField();
        jLabel13 = new JLabel();
        customerIdTextField = new JTextField();
        wasteText = new JTextField();
        jLabel14 = new JLabel();
        customerOrderText = new JTextField();
        jLabel15 = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Sale"); // NOI18N
        setTitle(bundle.getString("sale.title")); // NOI18N
        setName("Sale"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        closeButton.setIcon(new ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        closeButton.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                closeButtonKeyReleased(evt);
            }
        });

        upButton.setIcon(new ImageIcon(getClass().getResource("/proffittcenter/resource/forward_nav.gif"))); // NOI18N
        upButton.setContentAreaFilled(false);
        upButton.setName("upButton"); // NOI18N
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setIcon(new ImageIcon(getClass().getResource("/proffittcenter/resource/backward_nav.gif"))); // NOI18N
        downButton.setContentAreaFilled(false);
        downButton.setName("downButton"); // NOI18N
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        printButton.setIcon(new ImageIcon(getClass().getResource("/proffittcenter/resource/Receipt.png"))); // NOI18N
        printButton.setMnemonic('P');
        printButton.setText("null");
        printButton.setToolTipText(bundle.getString("Receipt")); // NOI18N
        printButton.setContentAreaFilled(false);
        printButton.setName("printButton"); // NOI18N
        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        invoiceButton.setIcon(new ImageIcon(getClass().getResource("/proffittcenter/resource/print_edit.gif"))); // NOI18N
        invoiceButton.setToolTipText(bundle.getString("Invoice")); // NOI18N
        invoiceButton.setContentAreaFilled(false);
        invoiceButton.setName("invoiceButton"); // NOI18N
        invoiceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setBorder(null);
        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        profitButton.setText(bundle.getString("Sale.profitButton.text")); // NOI18N
        profitButton.setName("profitButton"); // NOI18N
        profitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                profitButtonActionPerformed(evt);
            }
        });

        profitTextField.setEditable(false);
        profitTextField.setName("profitTextField"); // NOI18N

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(printButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(invoiceButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addGap(111, 111, 111)
                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, 302, Short.MAX_VALUE)
                .addComponent(profitTextField, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(profitButton)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(downButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(upButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(profitButton)
                        .addComponent(profitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(downButton, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(upButton, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(invoiceButton, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(printButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel1.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText(bundle.getString("Sale.jLabel1.text_2")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText(bundle.getString("Sale.jLabel2.text_2")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel3.setText(bundle.getString("Sale.jLabel3.text_2")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        idTextField.setEditable(false);
        idTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        idTextField.setName("idTextField"); // NOI18N

        dateTextField.setEditable(false);
        dateTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        dateTextField.setName("dateTextField"); // NOI18N

        timeTextField.setEditable(false);
        timeTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        timeTextField.setName("timeTextField"); // NOI18N

        jLabel4.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel4.setText(bundle.getString("Sale.jLabel4.text_2")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        totalTextField.setEditable(false);
        totalTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        totalTextField.setName("totalTextField"); // NOI18N

        jLabel5.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel5.setText(bundle.getString("Sale.jLabel5.text_2")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        operatorTextField.setEditable(false);
        operatorTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        operatorTextField.setName("operatorTextField"); // NOI18N

        jLabel6.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel6.setText(bundle.getString("Sale.jLabel6.text_2")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        taxTextField.setEditable(false);
        taxTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        taxTextField.setName("taxTextField"); // NOI18N

        jLabel7.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel7.setText(bundle.getString("Sale.jLabel7.text_2")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        cashTextField.setEditable(false);
        cashTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        cashTextField.setName("cashTextField"); // NOI18N

        jLabel8.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel8.setText(bundle.getString("Sale.jLabel8.text_2")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        chequeTextField.setEditable(false);
        chequeTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        chequeTextField.setName("chequeTextField"); // NOI18N

        jLabel10.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel10.setText(bundle.getString("Sale.jLabel10.text_2")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        debitTextField.setEditable(false);
        debitTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        debitTextField.setName("debitTextField"); // NOI18N

        jLabel11.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel11.setText(bundle.getString("Sale.jLabel11.text_2")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        couponTextField.setEditable(false);
        couponTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        couponTextField.setName("couponTextField"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 313, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel12.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel12.setText(bundle.getString("Sale.jLabel12.text_2")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        tillIdTextField.setEditable(false);
        tillIdTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        tillIdTextField.setName("tillIdTextField"); // NOI18N

        jLabel13.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel13.setText(bundle.getString("Sale.jLabel13.text_2")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        customerIdTextField.setEditable(false);
        customerIdTextField.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        customerIdTextField.setName("customerIdTextField"); // NOI18N

        wasteText.setEditable(false);
        wasteText.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        wasteText.setName("wasteText"); // NOI18N

        jLabel14.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel14.setText(bundle.getString("Sale.jLabel14.text_2")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        customerOrderText.setEditable(false);
        customerOrderText.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        customerOrderText.setName("customerOrderText"); // NOI18N

        jLabel15.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel15.setText(bundle.getString("Sale.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                            .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel8, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel10, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(operatorTextField)
                            .addComponent(idTextField)
                            .addComponent(totalTextField)
                            .addComponent(taxTextField, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                            .addComponent(chequeTextField)
                            .addComponent(cashTextField)
                            .addComponent(debitTextField)
                            .addComponent(couponTextField)
                            .addComponent(dateTextField)
                            .addComponent(timeTextField))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jLabel13, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(tillIdTextField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .addComponent(customerIdTextField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .addComponent(wasteText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .addComponent(customerOrderText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)))
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel12, jLabel13, jLabel14, jLabel15});

        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(customerIdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(customerOrderText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(wasteText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)))
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(tillIdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(idTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(operatorTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(dateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(timeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(totalTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(taxTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cashTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(chequeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(debitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(couponTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void closeButtonKeyReleased(KeyEvent evt) {//GEN-FIRST:event_closeButtonKeyReleased
        
}//GEN-LAST:event_closeButtonKeyReleased

    private void upButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        upButtonAction();
        profitTextField.setText("");
}

    private void upButtonAction() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement saleps;
        try {
            saleps = Main.getConnection().prepareStatement(SQL.uSale);
            saleps.setLong(1, sale);
            saleps.setInt(2, till); //TillId
            saleps.setInt(3, till); //TillId
            saleps.setInt(4, customer); //Customer
            saleps.setInt(5, customer); //Customer
            ResultSet rs = saleps.executeQuery();
            downButton.setEnabled(true);
            if (rs.first()) {
                //sale to display
                sale = rs.getLong("ID");
                idTextField.setText(sale.toString());
                whenCreatedDate = rs.getDate("whenCreated");
                dateTextField.setText(whenCreatedDate.toString());
                whenCreatedTime = rs.getTime("whenCreated");
                timeTextField.setText(whenCreatedTime.toString());
                fixedProfit=rs.getShort("FixedProfit");
                if (rs.getInt("Operator") == 0) {
                    operator = "Default";
                } else {
                    operator = rs.getString("Operators.Description");
                }
                operatorTextField.setText(operator);
                total = rs.getInt("Total");
                totalTextField.setText((new Money(total)).toString());
                tax = rs.getInt("Tax");
                taxTextField.setText((new Money(tax)).toString());
                cash = rs.getInt("Cash");
                cashTextField.setText((new Money(cash)).toString());
                cheque = rs.getInt("Cheque");
                chequeTextField.setText((new Money(cheque)).toString());
                debit = rs.getInt("Debit");
                debitTextField.setText((new Money(debit)).toString());
                coupon = rs.getInt("Coupon");
                couponTextField.setText((new Money(coupon)).toString());
                String customerName = rs.getString("Name2") + ", " + rs.getString("Name1");
                customerIdTextField.setText(customerName);
                tillId = rs.getInt("TillId");
                tillIdTextField.setText(tillId.toString());
                waste = rs.getShort("Waste");
                wasteText.setText(SaleType.description(waste));
                //                customerOrderText.setText(rs.getString("CustomerOrder"));
                rs.close();
                saleps.close();
                drawGrid();
                if(fixedProfit==0){
                    profitButton.setEnabled(false);
                }else{
                    profitButton.setEnabled(true);
                }
            } else {
                upButton.setEnabled(false);
                Audio.play("Ring");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sale_1.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
            jTextField1.requestFocus();
            return ;
        }
        setCursor(Cursor.getDefaultCursor());
        Audio.play("Beep");
        jTextField1.requestFocus();
}//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        downButtonAction();
        profitTextField.setText("");
}//GEN-LAST:event_downButtonActionPerformed

    private void printButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        Main.receiptPrinter.printReceipt(sale,true);
}//GEN-LAST:event_printButtonActionPerformed

    private void invoiceButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Main.invoice.print(sale,discount,true);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_invoiceButtonActionPerformed

    private void jTextField1KeyReleased(KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
            Audio.play("Beep");
            this.setVisible(false);
        }else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_PRINTSCREEN) {
            Main.receiptPrinter.printReceipt(sale,true);
        }else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
            if(upButton.isEnabled()){
                upButtonAction();
            }
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
            if(downButton.isEnabled()){
                downButtonAction();
            }
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void profitButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_profitButtonActionPerformed
        ResultSet rs;
        if(Main.operator.isOwner()){
            try {
                proffittSQL = Main.getConnection().prepareStatement(
                        SQL.proffittSQL);
                proffittSQL.setLong(1, sale);
                rs=proffittSQL.executeQuery();
                profit=0;
                while(rs.next()){
                    quantity=rs.getInt("Quantity");
                    price = rs.getInt("Price");
                    wholesalePrice=rs.getInt("wholesalePrice");
                    packSize=rs.getInt("packSize");
                    taxRate=rs.getInt("taxRate");
                    encode = rs.getInt("Encode");
                    profit+=Main.takings.profitCalculation( quantity, price, 
                            wholesalePrice, packSize, taxRate, encode,waste,fixedProfit);
                }
                if(profit!=0){
                    profitTextField.setText(Money.asMoney(profit));
                } else {
                    profitTextField.setText("");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Sale_1.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }//GEN-LAST:event_profitButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Sale_1 dialog = new Sale_1(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void drawGrid() {
        //fills in the saleLines
        PreparedStatement saleLines;
        try {
            saleLines = Main.getConnection().prepareStatement(
                    SQL.saleLines);
            saleLines.setLong(1, sale);
            ResultSet rs = saleLines.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            jTable1.addNotify();
            while (rs.next()) {
                //lines to display
                saleLine = rs.getInt("ID");
//                sale = rs.getLong("Sale");
                quantity = rs.getInt("Quantity");
                product = rs.getString("Products.Description");
                charge = Line.getCharge(rs);
                track = rs.getString("Track");
                Object[] line1 = {saleLine, sale, quantity, product, charge,track};
                rowData.add(line1);
            }
            rs.close();
            saleLines.close();
            jTable1.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(Sale_1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean execute(long sale,int customer, boolean account,int till,boolean up,boolean down) {
//        printButton.setEnabled(!Main.hardware.isInvoicePrinter());
//        invoiceButton.setEnabled(Main.hardware.isInvoicePrinter());
        profitTextField.setText("");
        profitButton.setEnabled(Main.operator.getIntAuthority()==0);
        this.customer = customer;
        this.sale = sale;
        onAccount = account;
        this.till = till;
        PreparedStatement Sale;
        try {
            Sale = Main.getConnection().prepareStatement(SQL.aSale);
            Sale.setInt(1, Integer.MAX_VALUE);
            Sale.setInt(2, till); //TillId
            Sale.setInt(3, till); //TillId
            Sale.setInt(4, customer); //Customer
            Sale.setInt(5, customer); //Customer
            Sale.setLong(6, sale); //Sale
            Sale.setLong(7, sale); //Sale
            ResultSet rs = Sale.executeQuery();
            if (rs.first()) {
                //sale to display
                this.sale = rs.getLong("Sales.ID");
                idTextField.setText(this.sale + "");
                whenCreatedDate = rs.getDate("Sales.whenCreated");
                dateTextField.setText(whenCreatedDate.toString());
                whenCreatedTime = rs.getTime("Sales.whenCreated");
                whenCreated = rs.getTimestamp("Sales.whenCreated");
                timeTextField.setText(whenCreatedTime.toString());
                fixedProfit=rs.getShort("FixedProfit");
                if(fixedProfit==0){
                    profitButton.setEnabled(false);
                }else{
                    profitButton.setEnabled(true);
                }
                if (rs.getInt("Operator") == 0) {
                    operator = "Default";
                } else {
                    operator = rs.getString("Operators.Description");
                }
                operatorTextField.setText(operator);
                total = rs.getInt("Total");
                String s = (new Money(total)).toString();
                totalTextField.setText((new Money(total)).toString());
                tax = rs.getInt("Tax");
                taxTextField.setText((new Money(tax)).toString());
                cash = rs.getInt("Cash");
                cashTextField.setText((new Money(cash)).toString());
                cheque = rs.getInt("Cheque");
                chequeTextField.setText((new Money(cheque)).toString());
                debit = rs.getInt("Debit");
                debitTextField.setText((new Money(debit)).toString());
                coupon = rs.getInt("Coupon");
                couponTextField.setText((new Money(coupon)).toString());
                String customerName = rs.getString("Name2") + ", "
                        + rs.getString("Name1");
                customerIdTextField.setText(customerName);
                tillId = rs.getInt("TillId");
                tillIdTextField.setText(this.tillId.toString());
                waste = rs.getShort("Waste");
                wasteText.setText(SaleType.description(waste));
                discount = rs.getInt("Discount");
                //customerOrderText.setText(rs.getString("CustomerOrder"));
                downButton.setEnabled(rs.next());
                drawGrid();
            }else{
                profitButton.setEnabled(false);
            }
            rs.close();
            Audio.play("Tada");
            upButton.setEnabled(up);
            downButton.setEnabled(down);
            invoiceButton.setEnabled(Main.hardware.isInvoicePrinter());
//            downButton.requestFocus();
            jTextField1.requestFocus();
            setVisible(true);
            //FormRestore.destroyPosition(this, root);
        } catch (SQLException ex) {
            Logger.getLogger(Sale_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextField cashTextField;
    private JTextField chequeTextField;
    private JButton closeButton;
    private JTextField couponTextField;
    private JTextField customerIdTextField;
    private JTextField customerOrderText;
    private JTextField dateTextField;
    private JTextField debitTextField;
    private JButton downButton;
    private JTextField idTextField;
    private JButton invoiceButton;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel15;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JTable jTable1;
    private JTextField jTextField1;
    private JTextField operatorTextField;
    private JButton printButton;
    private JToggleButton profitButton;
    private JTextField profitTextField;
    private JTextField taxTextField;
    private JTextField tillIdTextField;
    private JTextField timeTextField;
    private JTextField totalTextField;
    private JButton upButton;
    private JTextField wasteText;
    // End of variables declaration//GEN-END:variables
    private boolean onAccount;
    private Integer tax;
    private Integer cheque;
    private Integer total;
    private Integer cash;
    private Integer debit;
    private Integer coupon;
    private Integer ownUse;
    private Integer tillId;
    private String operator;
    private Date whenCreatedDate;
    private Time whenCreatedTime;
}
