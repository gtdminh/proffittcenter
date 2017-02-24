/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PackSuppliers.java
 *
 * Created on 15-Sep-2009, 17:22:36
 */

package proffittcenter;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class PackSuppliers extends EscapeDialog implements MouseListener {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/PackSuppliers");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Pack"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Price") + " (" + Main.shop.poundSymbol + ") ");
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Supplier"));
    private int align[] = {SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.LEFT,SwingConstants.RIGHT,SwingConstants.LEFT};
    private Integer id;
    private String pack;
    private Integer price;
    private Date date;
    private String supplier;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private Object[] line = {id, pack, date, price, supplier};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private AbstractTableModel model = new AbstractTableModel() {

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
            if (columnIndex == 0) {//id
                return line[0];
            } else if (columnIndex == 1) {//pack
                return line[1];
            } else if (columnIndex == 2) {//date
                return line[2];
            } else if (columnIndex == 3) {//price
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) line[3])) / 100);
            } else if (columnIndex == 4) {//supplier
                return line[4];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
//            return line[c].getClass();
            return getValueAt(0, c).getClass();
        }
    };
    private int columnSelected=0;
    private String product;


    /** Creates new form PackSuppliers */
    public PackSuppliers(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(100);
        tc.getColumn(1).setPreferredWidth(100);
        tc.getColumn(2).setPreferredWidth(150);
        tc.getColumn(3).setPreferredWidth(100);
        tc.getColumn(4).setPreferredWidth(400);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setDefaultRenderer(Long.class, mtcr);
        jTable1.setDefaultRenderer(Date.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        header.addMouseListener(this);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Packsuppliers", Main.mainHelpSet);
    }

    public void execute() {
        product="0";
        columnSelected=0;
        drawGrid("0");
        Audio.play("Tada");
        setVisible(true);
        return;
    }
    public void execute(String product) {
        columnSelected=0;
        this.product=product;
        drawGrid(product);
        Audio.play("Tada");
        setVisible(true);
        return;
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/PackSuppliers"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("PackSuppliers"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PackSuppliers dialog = new PackSuppliers(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    private void drawGrid(String product) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement packSuppliers;
            String ps = "SELECT *,Suppliers.Description AS Description FROM PackSuppliers " +
                    "JOIN Suppliers ON PackSuppliers.Supplier=Suppliers.ID " +
                    "Join Packs ON PackSuppliers.Pack=Packs.ID " +
                    "WHERE Packs.Product=? OR 0=? " +
                    "ORDER BY ";
            if (columnSelected == 0) {
                packSuppliers = Main.getConnection().prepareStatement(ps + "PackSuppliers.ID");
            } else if (columnSelected == 1) {
                packSuppliers = Main.getConnection().prepareStatement(ps + "PackSuppliers.Pack");
            } else if (columnSelected == 2) {
                packSuppliers = Main.getConnection().prepareStatement(ps + "PackSuppliers.WhenCreated");
            } else if (columnSelected == 3) {
                packSuppliers = Main.getConnection().prepareStatement(ps + "PackSuppliers.Price");
            }else if (columnSelected == 4) {
                packSuppliers = Main.getConnection().prepareStatement(ps + "Suppliers.Description");
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            packSuppliers.setString(1, product);
            packSuppliers.setString(2, product);
            ResultSet rs = packSuppliers.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {//
                id=rs.getInt("ID");
                pack=rs.getString("Pack");
                date=rs.getDate("WhenCreated");
                price=rs.getInt("Price");
                supplier=rs.getString("Description");
                Object[] line1 = {id, pack, date, price, supplier};
                rowData.add(line1);
                model.fireTableDataChanged();
            }
            packSuppliers.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        TableColumnModel colModel = jTable1.getColumnModel();
        // The index of the column whose header was clicked
        int vColIndex = colModel.getColumnIndexAtX(evt.getX());
        //int mColIndex = jTable1.convertColumnIndexToModel(vColIndex);
        // Return if not clicked on any column header
        if (vColIndex == -1) {
            return;
        }
        // Determine if mouse was clicked between column heads
        Rectangle headerRect = jTable1.getTableHeader().getHeaderRect(vColIndex);
        if (vColIndex == 0) {
            headerRect.width -= 3;    // Hard-coded constant
        } else {
            headerRect.grow(-3, 0);   // Hard-coded constant
        }
        if (!headerRect.contains(evt.getX(), evt.getY())) {
            // Mouse was clicked between column heads
            // vColIndex is the column head closest to the click
            // vLeftColIndex is the column head to the left of the click
            int vLeftColIndex = vColIndex;
            if (evt.getX() < headerRect.x) {
                vLeftColIndex--;
            }
        }
        columnSelected = vColIndex;
        drawGrid(product);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        //do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //do nothing
    }

}
