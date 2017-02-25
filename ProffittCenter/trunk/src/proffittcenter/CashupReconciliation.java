/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CashupReconcilliation.java
 *
 * Created on 27-Sep-2009, 15:23:52
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class CashupReconciliation extends EscapeDialog {

    Preferences root = Preferences.userNodeForPackage(getClass());
    private static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation");
    private String findCashUp = "SELECT * FROM CashUps,Operators WHERE Cashups.ID=? AND Cashups.Operator=Operators.ID ORDER BY WhenCreated DESC ";
    private String findDebits = "SELECT * FROM Sales WHERE TillID=? AND WhenCreated>=? AND WhenCreated<=? AND Debit<>0";
    private String findCheques = "SELECT * FROM Sales WHERE TillID=? AND WhenCreated>=? AND WhenCreated<=? AND Cheque<>0";
    private String findCoupons = "SELECT * FROM Sales WHERE TillID=? AND WhenCreated>=? AND WhenCreated<=? AND Coupon<>0";
    private String findPaidOuts = "SELECT * FROM PaidOuts WHERE TillID=? AND WhenCreated>=? AND WhenCreated<=?";
    private String findReceived = "SELECT Sales.*,Customers.* FROM Sales LEFT JOIN Customers ON Sales.Customer=Customers.ID WHERE TillID=? AND Sales.WhenCreated>=? AND Sales.WhenCreated<=? AND Waste="
            + SaleType.RECEIVEDONACCOUNT.valueString();
    private String save = "UPDATE cashups SET Notes200=?,Notes100=?,Notes50=?,Notes20=?,"
            + "Notes10=?,Notes5=?,Bags=?,"
            + "Loose200=?,Loose100=?,Loose50=?,Loose25=?,Loose20=?,Loose10=?,"
            + "Loose5=?,Loose2=?,Loose1=?,Error=?,DebitsError=?,ChequeError=?,CouponsError=?,ReceivedError=?,PaidOutError=?,Reconciled=? WHERE ID=?";
    private String lastCashUp = "SELECT * FROM Cashups WHERE TillID=? AND ID < ? ORDER BY WhenCreated DESC";
    private int tillID;
    private Integer amount;
    private String reason;
    private Vector order;
    private Object linePaidOuts[] = {amount, reason};
    private Vector<Object[]> rowDataDebits = new Vector<Object[]>();
    private Vector<Object[]> rowDataCheques = new Vector<Object[]>();
    private Vector<Object[]> rowDataCoupons = new Vector<Object[]>();
    private Vector<Object[]> rowDataPaidOuts = new Vector<Object[]>();
    private Vector<Object[]> rowDataReceived = new Vector<Object[]>();
    private int align[] = {SwingConstants.RIGHT};
    private int alignWithReason[] = {SwingConstants.LEFT, SwingConstants.RIGHT};
    //debit
    private int calculatedDebitTotal;
    private Integer debit;
    private Object debitLine[] = {debit};
    private int cashInDrawer;
    private Vector debitsHeader = new Vector();
    private MyHeaderRenderer debitHeaderRenderer = null;
    private MyTableCellRenderer debitTableCellRenderer = null;
    @SuppressWarnings("unchecked")
    private boolean b0d = debitsHeader.add(bundle.getString("Debits"));
    private AbstractTableModel modelDebits = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) debitsHeader.get(col);
        }

        public int getColumnCount() {
            return debitsHeader.size();
        }

        public int getRowCount() {
            return rowDataDebits.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            debitLine = (Object[]) rowDataDebits.get(rowIndex);
            if (columnIndex == 0) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
//                return debitLine[0].toString();
                return nf.format((new Double((Integer) debitLine[0]) / 100));
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    //cheque
    private int calculatedChequeTotal;
    private Integer cheque;
    private Object chequeLine[] = {cheque};
    private Vector chequesHeader = new Vector();
    private MyHeaderRenderer chequeHeaderRenderer = null;
    private MyTableCellRenderer chequeTableCellRenderer = null;
    @SuppressWarnings("unchecked")
    private boolean b0c = chequesHeader.add(bundle.getString("Cheques"));
    private AbstractTableModel modelCheques = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) chequesHeader.get(col);
        }

        public int getColumnCount() {
            return chequesHeader.size();
        }

        public int getRowCount() {
            return rowDataCheques.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            chequeLine = (Object[]) rowDataCheques.get(rowIndex);
            if (columnIndex == 0) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) chequeLine[0]) / 100));
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    //coupon
    private int calculatedCouponTotal;
    private Integer coupon;
    private Object couponLine[] = {coupon};
    private Vector couponsHeader = new Vector();
    private MyHeaderRenderer couponHeaderRenderer = null;
    private MyTableCellRenderer couponTableCellRenderer = null;
    @SuppressWarnings("unchecked")
    private boolean b0p = couponsHeader.add(bundle.getString("Coupons"));
    private AbstractTableModel modelCoupons = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) couponsHeader.get(col);
        }

        public int getColumnCount() {
            return couponsHeader.size();
        }

        public int getRowCount() {
            return rowDataCoupons.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            couponLine = (Object[]) rowDataCoupons.get(rowIndex);
            if (columnIndex == 0) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) couponLine[0]) / 100));
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    //received
    private int calculatedReceivedTotal;
    private int calculatedReceivedTotalCash;
    private Integer received;
    private String reasonReceived;
    private Object receivedLine[] = {reasonReceived, received};
    private Vector receivedHeader = new Vector();
    private MyHeaderRenderer receivedHeaderRenderer = null;
    private MyTableCellRenderer receivedTableCellRenderer = null;
    @SuppressWarnings("unchecked")
    private boolean b0po = receivedHeader.add("");
    @SuppressWarnings("unchecked")
    private boolean b0po1 = receivedHeader.add(bundle.getString("Received"));
    private AbstractTableModel modelReceived = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) receivedHeader.get(col);
        }

        @Override
        public int getColumnCount() {
            return receivedHeader.size();
        }

        public int getRowCount() {
            return rowDataReceived.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            receivedLine = (Object[]) rowDataReceived.get(rowIndex);
            if (columnIndex == 0) {
                return receivedLine[0];
            } else if (columnIndex == 1) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) receivedLine[1]) / 100));
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    //received
    private int calculatedPaidOutTotal;
    private Integer paidOut;
    String reasonPaidOut;
    private Object paidOutLine[] = {reasonPaidOut, received};
    private Vector paidOutHeader = new Vector();
    private MyHeaderRenderer paidOutHeaderRenderer = null;
    private MyTableCellRenderer paidOutTableCellRenderer = null;
    @SuppressWarnings("unchecked")
    private boolean b0pp = paidOutHeader.add("");
    @SuppressWarnings("unchecked")
    private boolean b0pp1 = paidOutHeader.add(bundle.getString("PaidOuts"));
    private AbstractTableModel modelPaidOuts = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) paidOutHeader.get(col);
        }

        @Override
        public int getColumnCount() {
            return paidOutHeader.size();
        }

        public int getRowCount() {
            return rowDataPaidOuts.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            paidOutLine = (Object[]) rowDataPaidOuts.get(rowIndex);
            if (columnIndex == 0) {
                return paidOutLine[0];
            } else if (columnIndex == 1) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) paidOutLine[1]) / 100));
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private int selectedCashupID;
    private int operator;
    private int takings;
    private int fixedFloat;
    private int error;
    private int yesterdaysFloat;
    private int tomorrowsFloat;
    private int lastCashupID;
    private int todaysFloat;
    private int cashup;
    private int debitError;
    private int chequeError;
    private int couponError;
    private int receivedError;
    private int paidOutError;
    private int charged;

    /** Creates new form CashupReconcilliation
     * @param parent
     * @param modal 
     */
    public CashupReconciliation(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jDebits.setModel(modelDebits);
        TableColumnModel debitTableColumn = jDebits.getTableHeader().getColumnModel();
        debitHeaderRenderer = new MyHeaderRenderer(align);
        for (int i = 0; i < debitTableColumn.getColumnCount(); i++) {
            debitTableColumn.getColumn(i).setHeaderRenderer(debitHeaderRenderer);
        }
        debitTableCellRenderer = new MyTableCellRenderer(align);
        jDebits.setDefaultRenderer(String.class, debitTableCellRenderer);

        jCheques.setModel(modelCheques);
        TableColumnModel chequeTableColumn = jCheques.getTableHeader().getColumnModel();
        chequeHeaderRenderer = new MyHeaderRenderer(align);
        for (int i = 0; i < chequeTableColumn.getColumnCount(); i++) {
            chequeTableColumn.getColumn(i).setHeaderRenderer(chequeHeaderRenderer);
        }
        chequeTableCellRenderer = new MyTableCellRenderer(align);
        jCheques.setDefaultRenderer(String.class, chequeTableCellRenderer);

        jCoupons.setModel(modelCoupons);
        TableColumnModel couponTableColumnModel = jCoupons.getTableHeader().getColumnModel();
        couponHeaderRenderer = new MyHeaderRenderer(align);
        for (int i = 0; i < couponTableColumnModel.getColumnCount(); i++) {
            couponTableColumnModel.getColumn(i).setHeaderRenderer(couponHeaderRenderer);
        }
        couponTableCellRenderer = new MyTableCellRenderer(align);
        jCoupons.setDefaultRenderer(String.class, couponTableCellRenderer);

        jPaidOuts.setModel(modelPaidOuts);
        TableColumnModel paidOutTableColumnModel = jPaidOuts.getTableHeader().getColumnModel();
        paidOutHeaderRenderer = new MyHeaderRenderer(alignWithReason);
        for (int i = 0; i < paidOutTableColumnModel.getColumnCount(); i++) {
            paidOutTableColumnModel.getColumn(i).setHeaderRenderer(paidOutHeaderRenderer);
        }
        paidOutTableCellRenderer = new MyTableCellRenderer(alignWithReason);
        jPaidOuts.setDefaultRenderer(String.class, paidOutTableCellRenderer);

        jReceived.setModel(modelReceived);
        TableColumnModel receivedTableColumnModel = jReceived.getTableHeader().getColumnModel();
        receivedHeaderRenderer = new MyHeaderRenderer(alignWithReason);
        for (int i = 0; i < paidOutTableColumnModel.getColumnCount(); i++) {
            receivedTableColumnModel.getColumn(i).setHeaderRenderer(receivedHeaderRenderer);
        }
        receivedTableCellRenderer = new MyTableCellRenderer(alignWithReason);
        jReceived.setDefaultRenderer(String.class, receivedTableCellRenderer);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Cashupreconcilliation", Main.mainHelpSet);
    }

    public void calculateCashInDrawer() {
        // Have entered all money, do calculations and enter new float values
        cashInDrawer = (Integer) spinNotes200.getValue() * 20000;
        cashInDrawer += (Integer) spinNotes100.getValue() * 10000;
        cashInDrawer += (Integer) spinNotes50.getValue() * 5000;
        cashInDrawer += (Integer) spinNotes20.getValue() * 2000;
        cashInDrawer += (Integer) spinNotes10.getValue() * 1000;
        cashInDrawer += (Integer) spinNotes5.getValue() * 500;
        cashInDrawer += (Integer) spinBags1.getValue() * 100;
        cashInDrawer += (Integer) spinLoose200.getValue() * 200;
        cashInDrawer += (Integer) spinLoose100.getValue() * 100;
        cashInDrawer += (Integer) spinLoose50.getValue() * 50;
        cashInDrawer += (Integer) spinLoose25.getValue() * 25;
        cashInDrawer += (Integer) spinLoose20.getValue() * 20;
        cashInDrawer += (Integer) spinLoose10.getValue() * 10;
        cashInDrawer += (Integer) spinLoose5.getValue() * 5;
        cashInDrawer += (Integer) spinLoose2.getValue() * 2;
        cashInDrawer += (Integer) spinLoose1.getValue() * 1;
        jCashInDrawer.setText(new Money(cashInDrawer).toString());
    }

    public boolean execute(int selectedCashupID) {
        int rounding = Main.shop.getRounding();
        spinLoose1.setVisible(rounding <= 1);
        spinLoose2.setVisible(rounding <= 2);
        spinBags1.setVisible(rounding <= 2);
        spinLoose5.setVisible(rounding <= 5);
        spinLoose10.setVisible(rounding <= 10);
        clearMoneyValues();
        inError.setVisible(false);
        saveButton.setEnabled(false);
        jCalculate.setEnabled(true);
        cashup = selectedCashupID;
        this.selectedCashupID = selectedCashupID;
        jID.setText("" + selectedCashupID);
        calculatedDebitTotal = 0;
        calculatedChequeTotal = 0;
        calculatedCouponTotal = 0;
        calculatedPaidOutTotal = 0;
        calculatedReceivedTotal = 0;
        calculatedReceivedTotalCash = 0;
        try {
            PreparedStatement selectionStatement = Main.getConnection().prepareStatement(findCashUp);
            selectionStatement.setInt(1, selectedCashupID);
            ResultSet rs = selectionStatement.executeQuery();
            if (rs.next()) {
                jStart.setText(rs.getString("WhenStarted"));
                jEnd.setText(rs.getString("WhenCreated"));
                jTillID.setText(rs.getString("TillID"));
                tillID = rs.getInt("TillID");
                operator = rs.getInt("Operator");
                jOperator.setText(rs.getString("Description"));
                takings = rs.getInt("Takings");
                jTakings.setText(new Money(takings).toString());
                charged = rs.getInt("Charged");
                jCharged.setText(new Money(charged).toString());
                jAgency.setText(new Money(rs.getInt("Agency")).toString());
                fixedFloat = rs.getInt("FixedFloat");
            } else {
                rs.close();
                return false;
            }
            yesterdaysFloat = fixedFloat;
            jYesterday.setText(new Money(yesterdaysFloat).toString());
            rs.close();
            selectionStatement.close();
            //list the debits
            selectionStatement = Main.getConnection().prepareStatement(findDebits);
            selectionStatement.setInt(1, tillID);
            selectionStatement.setString(2, jStart.getText());
            selectionStatement.setString(3, jEnd.getText());
            rs = selectionStatement.executeQuery();
            rowDataDebits.clear();
            while (rs.next()) {
                Object[] localDebitLine = {(Integer) rs.getInt("Debit")};
                rowDataDebits.add(localDebitLine);
                calculatedDebitTotal += rs.getInt("Debit");
            }
            selectionStatement.close();
            jDebitsTotal.setText(new Money(calculatedDebitTotal).toString());
            //list the cheques
            selectionStatement = Main.getConnection().prepareStatement(findCheques);
            selectionStatement.setInt(1, tillID);
            selectionStatement.setString(2, jStart.getText());
            selectionStatement.setString(3, jEnd.getText());
            rs = selectionStatement.executeQuery();
            rowDataCheques.clear();
            while (rs.next()) {
                Object[] localChequeLine = {(Integer) rs.getInt("Cheque")};
                rowDataCheques.add(localChequeLine);
                calculatedChequeTotal += rs.getInt("Cheque");
            }
            selectionStatement.close();
            jChequesTotal.setText(new Money(calculatedChequeTotal).toString());
            //list the coupons
            selectionStatement = Main.getConnection().prepareStatement(findCoupons);
            selectionStatement.setInt(1, tillID);
            selectionStatement.setString(2, jStart.getText());
            selectionStatement.setString(3, jEnd.getText());
            rs = selectionStatement.executeQuery();
            rowDataCoupons.clear();
            while (rs.next()) {
                Object[] localCouponLine = {(Integer) rs.getInt("Coupon")};
                rowDataCoupons.add(localCouponLine);
                calculatedCouponTotal += rs.getInt("Coupon");
            }
            selectionStatement.close();
            jCouponsTotal.setText(new Money(calculatedCouponTotal).toString());
            //list the paidOuts
            selectionStatement = Main.getConnection().prepareStatement(findPaidOuts);
            selectionStatement.setInt(1, tillID);
            selectionStatement.setString(2, jStart.getText());
            selectionStatement.setString(3, jEnd.getText());
            rs = selectionStatement.executeQuery();
            rowDataPaidOuts.clear();
            while (rs.next()) {
                Object[] localPaidOutLine = {
                    (String) rs.getString("Description"),
                    (Integer) rs.getInt("Amount")};
                rowDataPaidOuts.add(localPaidOutLine);
                calculatedPaidOutTotal += rs.getInt("Amount");
            }
            selectionStatement.close();
            jPaidOutTotal.setText(new Money(calculatedPaidOutTotal).toString());
            jDebitError.setText((Money.asMoney(0)));
            jChequeError.setText((Money.asMoney(0)));
            jCouponError.setText((Money.asMoney(0)));
            jPaidOutError.setText((Money.asMoney(0)));
            jReceivedError.setText((Money.asMoney(0)));
            selectionStatement.close();
            //list the received
            selectionStatement = Main.getConnection().prepareStatement(findReceived);
            selectionStatement.setInt(1, tillID);
            selectionStatement.setString(2, jStart.getText());
            selectionStatement.setString(3, jEnd.getText());
            rs = selectionStatement.executeQuery();
            rowDataReceived.clear();
            while (rs.next()) {
                String type = "";
                if(rs.getInt("Debit")!=0){
                    type += " (D)";
                }
                if(rs.getInt("Cheque")!=0){
                    type += " (C)";
                }
                Object[] localReceivedLine = {
                    (String) (rs.getString("Name2") + " " + rs.getString("Name1")+type),
                    (Integer) rs.getInt("Total")};
                rowDataReceived.add(localReceivedLine);
                calculatedReceivedTotalCash += rs.getInt("Total")
                            -rs.getInt("Debit")-rs.getInt("Coupon")-rs.getInt("Cheque");
                calculatedReceivedTotal += rs.getInt("Total");
            }
            selectionStatement.close();
            jReceivedTotal.setText(new Money(calculatedReceivedTotal).toString());
            Audio.play("TaDa");
            setVisible(true);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Deliveries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        spinNotes50 = new javax.swing.JSpinner();
        spinNotes20 = new javax.swing.JSpinner();
        spinNotes10 = new javax.swing.JSpinner();
        spinNotes5 = new javax.swing.JSpinner();
        spinBags1 = new javax.swing.JSpinner();
        spinLoose200 = new javax.swing.JSpinner();
        spinLoose100 = new javax.swing.JSpinner();
        spinLoose50 = new javax.swing.JSpinner();
        spinLoose25 = new javax.swing.JSpinner();
        spinLoose20 = new javax.swing.JSpinner();
        spinLoose10 = new javax.swing.JSpinner();
        spinLoose5 = new javax.swing.JSpinner();
        spinLoose2 = new javax.swing.JSpinner();
        spinLoose1 = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        spinNotes100 = new javax.swing.JSpinner();
        jLabel23 = new javax.swing.JLabel();
        spinNotes200 = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        inError = new javax.swing.JCheckBox();
        jLabel42 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jID = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jTakings = new javax.swing.JTextField();
        jYesterday = new javax.swing.JTextField();
        jError = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jAgency = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        jLabel47 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jTillID = new javax.swing.JTextField();
        jCashInDrawer = new javax.swing.JTextField();
        jCharged = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jStart = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jEnd = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jCalculate = new javax.swing.JButton();
        jOperator = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jDebitError = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jCoupons = new javax.swing.JTable();
        jReceivedTotal = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jCheques = new javax.swing.JTable();
        jChequeError = new javax.swing.JTextField();
        jCouponError = new javax.swing.JTextField();
        jPaidOutTotal = new javax.swing.JTextField();
        jDebitsTotal = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jReceived = new javax.swing.JTable();
        jDebitsScrollPane = new javax.swing.JScrollPane();
        jDebits = new javax.swing.JTable();
        jPaidOutError = new javax.swing.JTextField();
        jReceivedError = new javax.swing.JTextField();
        jChequesTotal = new javax.swing.JTextField();
        jCouponsTotal = new javax.swing.JTextField();
        totalLabel = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPaidOuts = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation"); // NOI18N
        setTitle(bundle.getString("CashupReconciliation.title")); // NOI18N
        setName("cashupReconcilliation"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPanel1FocusLost(evt);
            }
        });

        spinNotes50.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinNotes50.setName("spinNotes50"); // NOI18N

        spinNotes20.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinNotes20.setName("spinNotes20"); // NOI18N

        spinNotes10.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinNotes10.setName("spinNotes10"); // NOI18N

        spinNotes5.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinNotes5.setName("spinNotes5"); // NOI18N

        spinBags1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinBags1.setName("spinBags1"); // NOI18N

        spinLoose200.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose200.setName("spinLoose200"); // NOI18N

        spinLoose100.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose100.setName("spinLoose100"); // NOI18N

        spinLoose50.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose50.setName("spinLoose50"); // NOI18N

        spinLoose25.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose25.setName("spinLoose25"); // NOI18N

        spinLoose20.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose20.setName("spinLoose20"); // NOI18N

        spinLoose10.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose10.setName("spinLoose10"); // NOI18N

        spinLoose5.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose5.setName("spinLoose5"); // NOI18N

        spinLoose2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose2.setName("spinLoose2"); // NOI18N

        spinLoose1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinLoose1.setName("spinLoose1"); // NOI18N
        spinLoose1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spinLoose1FocusLost(evt);
            }
        });

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText(bundle.getString("CashupReconciliation.jLabel17.text")+Main.shop.getPennySymbol()+" x"); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(bundle.getString("CashupReconciliation.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText(bundle.getString("CashupReconciliation.jLabel16.text")+Main.shop.getPennySymbol()+" x"); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText(bundle.getString("CashupReconciliation.jLabel20.text")+Main.shop.getPennySymbol()+" x"); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText(bundle.getString("CashupReconciliation.jLabel18.text")+Main.shop.getPennySymbol()+" x"); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText(bundle.getString("CashupReconciliation.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText(bundle.getString("CashupReconciliation.jLabel15.text")+Main.shop.getPennySymbol()+" x"); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText(bundle.getString("CashupReconciliation.jLabel14.text")+Main.shop.getPennySymbol()+" x"); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText(bundle.getString("CashupReconciliation.jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText(bundle.getString("CashupReconciliation.jLabel19.text")+Main.shop.getPennySymbol()+" x"); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        spinNotes100.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinNotes100.setName("spinNotes100"); // NOI18N

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText(Main.shop.poundSymbol+java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation").getString("CashupReconciliation.jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        spinNotes200.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinNotes200.setName("spinNotes200"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinNotes200, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinNotes50, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinNotes20, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinNotes5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinNotes10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinNotes100, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLoose200, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLoose5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLoose10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLoose20, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLoose25, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLoose50, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLoose100, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(spinLoose2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinLoose1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinBags1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {spinBags1, spinLoose1, spinLoose10, spinLoose100, spinLoose2, spinLoose20, spinLoose200, spinLoose25, spinLoose5, spinLoose50, spinNotes10, spinNotes100, spinNotes20, spinNotes200, spinNotes5, spinNotes50});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinNotes200, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinNotes100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinNotes50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spinNotes20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinNotes10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(spinNotes5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(spinBags1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(spinLoose200, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(spinLoose100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(spinLoose50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(spinLoose25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(spinLoose20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(spinLoose10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(spinLoose5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(spinLoose2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(spinLoose1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setName("jPanel2"); // NOI18N

        inError.setText(bundle.getString("CashupReconciliation.inError.text")); // NOI18N
        inError.setName("inError"); // NOI18N

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText(bundle.getString("CashupReconciliation.jLabel42.text")); // NOI18N
        jLabel42.setName("jLabel42"); // NOI18N

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText(bundle.getString("CashupReconciliation.jLabel38.text")); // NOI18N
        jLabel38.setName("jLabel38"); // NOI18N

        jID.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jID.setFocusable(false);
        jID.setName("jID"); // NOI18N

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText(bundle.getString("CashupReconciliation.jLabel39.text")); // NOI18N
        jLabel39.setName("jLabel39"); // NOI18N

        jTakings.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTakings.setFocusable(false);
        jTakings.setName("jTakings"); // NOI18N

        jYesterday.setEditable(false);
        jYesterday.setName("jYesterday"); // NOI18N

        jError.setName("jError"); // NOI18N

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText(bundle.getString("CashupReconciliation.jLabel44.text")); // NOI18N
        jLabel44.setName("jLabel44"); // NOI18N

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText(bundle.getString("CashupReconciliation.jLabel43.text")); // NOI18N
        jLabel43.setName("jLabel43"); // NOI18N

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText(bundle.getString("CashupReconciliation.jLabel40.text")); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N

        jAgency.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jAgency.setFocusable(false);
        jAgency.setName("jAgency"); // NOI18N

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Save.png"))); // NOI18N
        saveButton.setToolTipText(bundle.getString("Save")); // NOI18N
        saveButton.setContentAreaFilled(false);
        saveButton.setName("saveButton"); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText(bundle.getString("CashupReconciliation.jLabel47.text")); // NOI18N
        jLabel47.setName("jLabel47"); // NOI18N

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText(bundle.getString("CashupReconciliation.jLabel41.text")); // NOI18N
        jLabel41.setName("jLabel41"); // NOI18N

        jTillID.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTillID.setFocusable(false);
        jTillID.setName("jTillID"); // NOI18N

        jCashInDrawer.setName("jCashInDrawer"); // NOI18N

        jCharged.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jCharged.setFocusable(false);
        jCharged.setName("jCharged"); // NOI18N

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText(bundle.getString("CashupReconciliation.jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N

        jStart.setEditable(false);
        jStart.setFocusable(false);
        jStart.setName("jStart"); // NOI18N

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText(bundle.getString("CashupReconciliation.jLabel37.text")); // NOI18N
        jLabel37.setName("jLabel37"); // NOI18N

        jEnd.setEditable(false);
        jEnd.setFocusable(false);
        jEnd.setName("jEnd"); // NOI18N

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText(bundle.getString("CashupReconciliation.jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jCalculate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Calculate.png"))); // NOI18N
        jCalculate.setToolTipText(bundle.getString("Calculate")); // NOI18N
        jCalculate.setContentAreaFilled(false);
        jCalculate.setName("jCalculate"); // NOI18N
        jCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCalculateActionPerformed(evt);
            }
        });

        jOperator.setText(bundle.getString("CashupReconciliation.jOperator.text")); // NOI18N
        jOperator.setName("jOperator"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addComponent(jCalculate, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85))
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jEnd)
                            .addComponent(jStart, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(inError, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jID, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jTillID, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jTakings, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel44)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCharged, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jAgency, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(jCashInDrawer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jError, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jYesterday, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jOperator, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jAgency, jCashInDrawer, jCharged, jID, jTakings, jTillID});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCalculate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel38)
                                .addComponent(jID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel39)
                                    .addComponent(jTillID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel40))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel36)
                                    .addComponent(jStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel37)
                                    .addComponent(jEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTakings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41)
                            .addComponent(inError)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCharged, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel42))
                            .addComponent(jOperator, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jAgency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jYesterday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel47)
                                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel43))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCashInDrawer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel44)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setName("jPanel3"); // NOI18N

        jDebitError.setEditable(false);
        jDebitError.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jDebitError.setText(bundle.getString("CashupReconciliation.jDebitError.text")); // NOI18N
        jDebitError.setName("jDebitError"); // NOI18N
        jDebitError.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jDebitErrorKeyReleased(evt);
            }
        });

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jCoupons.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jCoupons.setFocusable(false);
        jCoupons.setName("jCoupons"); // NOI18N
        jScrollPane3.setViewportView(jCoupons);

        jReceivedTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jReceivedTotal.setName("jReceivedTotal"); // NOI18N
        jReceivedTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jReceivedTotalKeyReleased(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jCheques.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jCheques.setFocusable(false);
        jCheques.setName("jCheques"); // NOI18N
        jScrollPane1.setViewportView(jCheques);

        jChequeError.setEditable(false);
        jChequeError.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jChequeError.setText(bundle.getString("CashupReconciliation.jChequeError.text")); // NOI18N
        jChequeError.setName("jChequeError"); // NOI18N
        jChequeError.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jChequeErrorKeyReleased(evt);
            }
        });

        jCouponError.setEditable(false);
        jCouponError.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jCouponError.setText(bundle.getString("CashupReconciliation.jCouponError.text")); // NOI18N
        jCouponError.setName("jCouponError"); // NOI18N
        jCouponError.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jCouponErrorKeyReleased(evt);
            }
        });

        jPaidOutTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPaidOutTotal.setName("jPaidOutTotal"); // NOI18N
        jPaidOutTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPaidOutTotalKeyReleased(evt);
            }
        });

        jDebitsTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jDebitsTotal.setName("jDebitsTotal"); // NOI18N
        jDebitsTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jDebitsTotalKeyReleased(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jReceived.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " ", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jReceived.setFocusable(false);
        jReceived.setName("jReceived"); // NOI18N
        jScrollPane4.setViewportView(jReceived);

        jDebitsScrollPane.setName("debits"); // NOI18N

        jDebits.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jDebits.setFocusable(false);
        jDebits.setName("jDebits"); // NOI18N
        jDebitsScrollPane.setViewportView(jDebits);

        jPaidOutError.setEditable(false);
        jPaidOutError.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPaidOutError.setText(bundle.getString("CashupReconciliation.jPaidOutError.text")); // NOI18N
        jPaidOutError.setName("jPaidOutError"); // NOI18N
        jPaidOutError.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPaidOutErrorKeyReleased(evt);
            }
        });

        jReceivedError.setEditable(false);
        jReceivedError.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jReceivedError.setText(bundle.getString("CashupReconciliation.jReceivedError.text")); // NOI18N
        jReceivedError.setName("jReceivedError"); // NOI18N
        jReceivedError.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jReceivedErrorKeyReleased(evt);
            }
        });

        jChequesTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jChequesTotal.setName("jChequesTotal"); // NOI18N
        jChequesTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jChequesTotalKeyReleased(evt);
            }
        });

        jCouponsTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jCouponsTotal.setName("jCouponsTotal"); // NOI18N
        jCouponsTotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jCouponsTotalKeyReleased(evt);
            }
        });

        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLabel.setText(bundle.getString("Totals")); // NOI18N
        totalLabel.setName("totalLabel"); // NOI18N

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText(bundle.getString("CashupReconciliation.jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jDebitError, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jDebitsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jDebitsTotal)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jChequeError, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                    .addComponent(jChequesTotal)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCouponError, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(jCouponsTotal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jReceivedTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jReceivedError, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addGap(93, 93, 93)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPaidOutError, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                    .addComponent(jPaidOutTotal))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane1)
                    .addComponent(jDebitsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                    .addComponent(jChequesTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDebitsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                    .addComponent(jCouponsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jReceivedTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(totalLabel))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jReceivedError)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCouponError, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel21))
                            .addComponent(jDebitError)
                            .addComponent(jChequeError)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jPaidOutTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPaidOutError, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jChequeError, jChequesTotal, jCouponError, jCouponsTotal, jDebitError, jDebitsTotal, jPaidOutError, jPaidOutTotal, jReceivedError, jReceivedTotal});

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jPaidOuts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jPaidOuts.setFocusable(false);
        jPaidOuts.setName("jPaidOuts"); // NOI18N
        jScrollPane2.setViewportView(jPaidOuts);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(131, 131, 131))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(50, 50, 50))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void spinLoose1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spinLoose1FocusLost
        calculateCashInDrawer();
    }//GEN-LAST:event_spinLoose1FocusLost

    private void jPanel1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPanel1FocusLost
    }//GEN-LAST:event_jPanel1FocusLost

    private void jCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCalculateActionPerformed
        calculateCashInDrawer();
        yesterdaysFloat = Integer.parseInt(StringOps.numericOnly(jYesterday.getText()));
        error = cashInDrawer //positive if too much cash
                - yesterdaysFloat
                - takings
                + Integer.parseInt(StringOps.numericOnly(jDebitsTotal.getText()))
                + Integer.parseInt(StringOps.numericOnly(jChequesTotal.getText()))
                + Integer.parseInt(StringOps.numericOnly(jCouponsTotal.getText()))
                - Integer.parseInt(StringOps.numericOnly(jReceivedTotal.getText()))
                + charged
                + Integer.parseInt(StringOps.numericOnly(jPaidOutTotal.getText()));
        jError.setText(new Money(error).toString());
        saveButton.setEnabled(true);
        debitError = -calculatedDebitTotal + Integer.parseInt(StringOps.numericOnly(jDebitsTotal.getText()));
        Money.asMoney(jDebitError);
        chequeError = -calculatedChequeTotal + Integer.parseInt(StringOps.numericOnly(jChequesTotal.getText()));
        couponError = -calculatedCouponTotal + Integer.parseInt(StringOps.numericOnly(jCouponsTotal.getText()));
        receivedError = -calculatedReceivedTotal + Integer.parseInt(StringOps.numericOnly(jReceivedTotal.getText()));
        paidOutError = -calculatedPaidOutTotal + Integer.parseInt(StringOps.numericOnly(jPaidOutTotal.getText()));
        jDebitError.setText(Money.asMoney(debitError));
        jCouponError.setText(Money.asMoney(couponError));
        jChequeError.setText(Money.asMoney(chequeError));
        jReceivedError.setText(Money.asMoney(receivedError));
        jPaidOutError.setText(Money.asMoney(paidOutError));
    }//GEN-LAST:event_jCalculateActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveData();
        setVisible(false);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void jDebitsTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDebitsTotalKeyReleased
        Money.asMoney(jDebitsTotal);
    }//GEN-LAST:event_jDebitsTotalKeyReleased

    private void jChequesTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jChequesTotalKeyReleased
        Money.asMoney(jChequesTotal);
    }//GEN-LAST:event_jChequesTotalKeyReleased

    private void jCouponsTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCouponsTotalKeyReleased
        Money.asMoney(jCouponsTotal);
    }//GEN-LAST:event_jCouponsTotalKeyReleased

    private void jReceivedTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jReceivedTotalKeyReleased
        Money.asMoney(jReceivedTotal);
    }//GEN-LAST:event_jReceivedTotalKeyReleased

    private void jPaidOutTotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPaidOutTotalKeyReleased
        Money.asMoney(jPaidOutTotal);
    }//GEN-LAST:event_jPaidOutTotalKeyReleased

    private void jDebitErrorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDebitErrorKeyReleased
        Money.asMoney(jDebitError);
    }//GEN-LAST:event_jDebitErrorKeyReleased

    private void jChequeErrorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jChequeErrorKeyReleased
        Money.asMoney(jChequeError);
    }//GEN-LAST:event_jChequeErrorKeyReleased

    private void jCouponErrorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCouponErrorKeyReleased
        Money.asMoney(jCouponError);
    }//GEN-LAST:event_jCouponErrorKeyReleased

    private void jReceivedErrorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jReceivedErrorKeyReleased
        Money.asMoney(jReceivedError);
    }//GEN-LAST:event_jReceivedErrorKeyReleased

    private void jPaidOutErrorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPaidOutErrorKeyReleased
        Money.asMoney(jPaidOutError);
    }//GEN-LAST:event_jPaidOutErrorKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                CashupReconciliation dialog = new CashupReconciliation(new javax.swing.JFrame(), true);
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox inError;
    private javax.swing.JTextField jAgency;
    private javax.swing.JButton jCalculate;
    private javax.swing.JTextField jCashInDrawer;
    private javax.swing.JTextField jCharged;
    private javax.swing.JTextField jChequeError;
    private javax.swing.JTable jCheques;
    private javax.swing.JTextField jChequesTotal;
    private javax.swing.JTextField jCouponError;
    private javax.swing.JTable jCoupons;
    private javax.swing.JTextField jCouponsTotal;
    private javax.swing.JTextField jDebitError;
    private javax.swing.JTable jDebits;
    private javax.swing.JScrollPane jDebitsScrollPane;
    private javax.swing.JTextField jDebitsTotal;
    private javax.swing.JTextField jEnd;
    private javax.swing.JTextField jError;
    private javax.swing.JTextField jID;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jOperator;
    private javax.swing.JTextField jPaidOutError;
    private javax.swing.JTextField jPaidOutTotal;
    private javax.swing.JTable jPaidOuts;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTable jReceived;
    private javax.swing.JTextField jReceivedError;
    private javax.swing.JTextField jReceivedTotal;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jStart;
    private javax.swing.JTextField jTakings;
    private javax.swing.JTextField jTillID;
    private javax.swing.JTextField jYesterday;
    private javax.swing.JButton saveButton;
    private javax.swing.JSpinner spinBags1;
    private javax.swing.JSpinner spinLoose1;
    private javax.swing.JSpinner spinLoose10;
    private javax.swing.JSpinner spinLoose100;
    private javax.swing.JSpinner spinLoose2;
    private javax.swing.JSpinner spinLoose20;
    private javax.swing.JSpinner spinLoose200;
    private javax.swing.JSpinner spinLoose25;
    private javax.swing.JSpinner spinLoose5;
    private javax.swing.JSpinner spinLoose50;
    private javax.swing.JSpinner spinNotes10;
    private javax.swing.JSpinner spinNotes100;
    private javax.swing.JSpinner spinNotes20;
    private javax.swing.JSpinner spinNotes200;
    private javax.swing.JSpinner spinNotes5;
    private javax.swing.JSpinner spinNotes50;
    private javax.swing.JLabel totalLabel;
    // End of variables declaration//GEN-END:variables

    private void saveData() {
        try {
            PreparedStatement saveStatement = Main.getConnection().prepareStatement(save);
            saveStatement.setInt(1, (Integer) spinNotes200.getValue());
            saveStatement.setInt(2, (Integer) spinNotes100.getValue());
            saveStatement.setInt(3, (Integer) spinNotes50.getValue());
            saveStatement.setInt(4, (Integer) spinNotes20.getValue());
            saveStatement.setInt(5, (Integer) spinNotes10.getValue());
            saveStatement.setInt(6, (Integer) spinNotes5.getValue());
            saveStatement.setInt(7, (Integer) spinBags1.getValue());
            saveStatement.setInt(8, (Integer) spinLoose200.getValue());
            saveStatement.setInt(9, (Integer) spinLoose100.getValue());
            saveStatement.setInt(10, (Integer) spinLoose50.getValue());
            saveStatement.setInt(11, (Integer) spinLoose25.getValue());
            saveStatement.setInt(12, (Integer) spinLoose20.getValue());
            saveStatement.setInt(13, (Integer) spinLoose10.getValue());
            saveStatement.setInt(14, (Integer) spinLoose5.getValue());
            saveStatement.setInt(15, (Integer) spinLoose2.getValue());
            saveStatement.setInt(16, (Integer) spinLoose1.getValue());
            saveStatement.setInt(17, error);//cash error
            debitError = -calculatedDebitTotal + Integer.parseInt(StringOps.numericOnly(jDebitsTotal.getText()));
            saveStatement.setInt(18, debitError);
            chequeError = -calculatedChequeTotal + Integer.parseInt(StringOps.numericOnly(jChequesTotal.getText()));
            saveStatement.setInt(19, chequeError);
            couponError = -calculatedCouponTotal + Integer.parseInt(StringOps.numericOnly(jCouponsTotal.getText()));
            saveStatement.setInt(20, couponError);
            receivedError = -calculatedReceivedTotal + Integer.parseInt(StringOps.numericOnly(jReceivedTotal.getText()));
            saveStatement.setInt(21, receivedError);
            paidOutError = -calculatedPaidOutTotal + Integer.parseInt(StringOps.numericOnly(jPaidOutTotal.getText()));
            saveStatement.setInt(22, paidOutError);
            saveStatement.setInt(23, 1);//reconciled
            saveStatement.setInt(24, cashup);
            saveStatement.executeUpdate();
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Deliveries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean execute(int cashup, boolean view) {
        if (view) {//just for viewing
            clearMoneyValues();
            inError.setVisible(true);
            jCalculate.setEnabled(false);
            saveButton.setEnabled(false);
//            totalLabel.setText(bundle.getString("Errors"));
            try {
                PreparedStatement selectionStatement = Main.getConnection().prepareStatement(findCashUp);
                selectionStatement.setInt(1, cashup);
                jID.setText("" + cashup);
                ResultSet rs = selectionStatement.executeQuery();
                if (rs.next()) {
                    jStart.setText(rs.getString("WhenStarted"));
                    jEnd.setText(rs.getString("WhenCreated"));
                    jTillID.setText(rs.getString("TillID"));
                    tillID = rs.getInt("TillID");
                    operator = rs.getInt("Operator");
                    jOperator.setText(rs.getString("Description"));
                    takings = rs.getInt("Takings");
                    jTakings.setText(new Money(takings).toString());
                    charged = rs.getInt("Charged");
                    jCharged.setText(new Money(charged).toString());
                    jAgency.setText(new Money(rs.getInt("Agency")).toString());
                    fixedFloat = rs.getInt("FixedFloat");
                    jYesterday.setText(new Money(fixedFloat).toString());
                    //the cash count
                    spinNotes200.setValue(rs.getInt("Notes200"));
                    spinNotes100.setValue(rs.getInt("Notes100"));
                    spinNotes50.setValue(rs.getInt("Notes50"));
                    spinNotes20.setValue(rs.getInt("Notes20"));
                    spinNotes10.setValue(rs.getInt("Notes10"));
                    spinNotes5.setValue(rs.getInt("Notes5"));
                    spinBags1.setValue(rs.getInt("Bags"));
                    spinLoose200.setValue(rs.getInt("Loose200"));
                    spinLoose100.setValue(rs.getInt("Loose100"));
                    spinLoose50.setValue(rs.getInt("Loose50"));
                    spinLoose20.setValue(rs.getInt("Loose20"));
                    spinLoose10.setValue(rs.getInt("Loose10"));
                    spinLoose5.setValue(rs.getInt("Loose5"));
                    spinLoose2.setValue(rs.getInt("Loose2"));
                    spinLoose1.setValue(rs.getInt("Loose1"));
                    jDebitsTotal.setText((new Money(rs.getInt("DebitsError"))).toString());
                    jChequesTotal.setText((new Money(rs.getInt("ChequeError"))).toString());
                    jCouponsTotal.setText((new Money(rs.getInt("CouponsError"))).toString());
                    jPaidOutTotal.setText((new Money(rs.getInt("PaidOutError"))).toString());
                    jReceivedTotal.setText((new Money(rs.getInt("ReceivedError"))).toString());
                    jError.setText((new Money(rs.getInt("Error"))).toString());
                } else {
                    rs.close();
                    return false;
                }
                yesterdaysFloat = fixedFloat;
                jYesterday.setText(new Money(yesterdaysFloat).toString());
                calculateCashInDrawer();
                jCashInDrawer.setText(new Money(cashInDrawer).toString());
                rs.close();
                selectionStatement.close();
                //list the debits
                selectionStatement = Main.getConnection().prepareStatement(findDebits);
                selectionStatement.setInt(1, tillID);
                selectionStatement.setString(2, jStart.getText());
                selectionStatement.setString(3, jEnd.getText());
                rs = selectionStatement.executeQuery();
                rowDataDebits.clear();
                calculatedDebitTotal = 0;
                while (rs.next()) {
                    Object[] localDebitLine = {(Integer) rs.getInt("Debit")};
                    rowDataDebits.add(localDebitLine);
                    calculatedDebitTotal += rs.getInt("Debit");
                }
                selectionStatement.close();
                jDebitsTotal.setText(new Money(calculatedDebitTotal).toString());
                //list the cheques
                selectionStatement = Main.getConnection().prepareStatement(findCheques);
                selectionStatement.setInt(1, tillID);
                selectionStatement.setString(2, jStart.getText());
                selectionStatement.setString(3, jEnd.getText());
                rs = selectionStatement.executeQuery();
                rowDataCheques.clear();
                calculatedChequeTotal = 0;
                while (rs.next()) {
                    Object[] localChequeLine = {(Integer) rs.getInt("Cheque")};
                    rowDataCheques.add(localChequeLine);
                    calculatedChequeTotal += rs.getInt("Cheque");
                }
                selectionStatement.close();
                jChequesTotal.setText(new Money(calculatedChequeTotal).toString());
                //list the coupons
                selectionStatement = Main.getConnection().prepareStatement(findCoupons);
                selectionStatement.setInt(1, tillID);
                selectionStatement.setString(2, jStart.getText());
                selectionStatement.setString(3, jEnd.getText());
                rs = selectionStatement.executeQuery();
                rowDataCoupons.clear();
                calculatedCouponTotal = 0;
                while (rs.next()) {
                    Object[] localCouponLine = {(Integer) rs.getInt("Coupon")};
                    rowDataCoupons.add(localCouponLine);
                    calculatedCouponTotal += rs.getInt("Coupon");
                }
                selectionStatement.close();
                jCouponsTotal.setText(new Money(calculatedCouponTotal).toString());
                //list the paidOuts
                selectionStatement = Main.getConnection().prepareStatement(findPaidOuts);
                selectionStatement.setInt(1, tillID);
                selectionStatement.setString(2, jStart.getText());
                selectionStatement.setString(3, jEnd.getText());
                rs = selectionStatement.executeQuery();
                rowDataPaidOuts.clear();
                calculatedPaidOutTotal = 0;
                while (rs.next()) {
                    Object[] localPaidOutLine = {
                        (String) rs.getString("Description"),
                        (Integer) rs.getInt("Amount")};
                    rowDataPaidOuts.add(localPaidOutLine);
                    calculatedPaidOutTotal += rs.getInt("Amount");
                }
                selectionStatement.close();
                jPaidOutTotal.setText(new Money(calculatedPaidOutTotal).toString());
                //list the received
                selectionStatement = Main.getConnection().prepareStatement(findReceived);
                selectionStatement.setInt(1, tillID);
                selectionStatement.setString(2, jStart.getText());
                selectionStatement.setString(3, jEnd.getText());
                rs = selectionStatement.executeQuery();
                rowDataReceived.clear();
                calculatedReceivedTotal = 0;
                while (rs.next()) {
                    Object[] localPaidOutLine = {
                        (String) (rs.getString("Name2") + " " + rs.getString("Name1")),
                        (Integer) rs.getInt("Total")-rs.getInt("Debit")-rs.getInt("Coupon")-rs.getInt("Cheque")};
                    rowDataReceived.add(localPaidOutLine);
                    calculatedReceivedTotal += rs.getInt("Total")
                            -rs.getInt("Debit")-rs.getInt("Coupon")-rs.getInt("Cheque");
                }
                selectionStatement.close();
                jReceivedTotal.setText(new Money(calculatedReceivedTotal).toString());
                jDebitError.setText((Money.asMoney(debitError)));
                jChequeError.setText((Money.asMoney(chequeError)));
                jCouponError.setText((Money.asMoney(couponError)));
                jPaidOutError.setText((Money.asMoney(paidOutError)));
                jReceivedError.setText((Money.asMoney(receivedError)));
                Audio.play("TaDa");
                setVisible(true);
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(Deliveries.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        }
        return false;
    }

    private void clearMoneyValues() {
        spinNotes200.setValue(0);
        spinNotes100.setValue(0);
        spinNotes50.setValue(0);
        spinNotes20.setValue(0);
        spinNotes10.setValue(0);
        spinNotes5.setValue(0);
        spinBags1.setValue(0);
        spinLoose200.setValue(0);
        spinLoose100.setValue(0);
        spinLoose50.setValue(0);
        spinLoose25.setValue(0);
        spinLoose20.setValue(0);
        spinLoose10.setValue(0);
        spinLoose5.setValue(0);
        spinLoose2.setValue(0);
        spinLoose1.setValue(0);
        spinNotes200.requestFocus();
    }

}
