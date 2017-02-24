/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OffersNew.java
 *
 * Created on 13-Mar-2009, 11:09:37
 */
package proffittcenter;

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
public class Offers extends EscapeDialog implements MouseListener {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private Vector tableHeader = new Vector();
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Offers");
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("IX"));
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("Start"));
    @SuppressWarnings("unchecked")
    private boolean b7 = tableHeader.add(bundle.getString("End"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("OfferType"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Product"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Product_description"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("X"));
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Y") + " (" + Main.shop.poundSymbol + bundle.getString("or%"));
    @SuppressWarnings("unchecked")
    private boolean b8 = tableHeader.add(bundle.getString("Limit"));
    private NumberFormat nf = NumberFormat.getInstance();
    private Integer ix;
    private Date start;
    private Date end;
    private Integer kind;
    private Long product;
    private String productDescription;
    private Integer x;
    private Integer y;
    private Integer limit;
    private java.sql.Date endDate;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.RIGHT};
    private Object[] line = {ix, start, end, kind, product, productDescription, x,
        y, limit};
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

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {
                //ix
                return line[0];
            } else if (columnIndex == 1) {
                //start
                return line[1];
            } else if (columnIndex == 2) {
                //end
                return line[2];
            } else if (columnIndex == 3) {
                //kind
                int kind = (Integer) line[3];
                if (kind == Offer.QUANTITY) {
                    return bundle.getString("Quantity");
                } else if (kind == Offer.PACK) {
                    return bundle.getString("Pack");
                } else return "";
            } else if (columnIndex == 4) {
                //product
                return line[4];
            } else if (columnIndex == 5) {
                //productDescription
                return line[5];
            } else if (columnIndex == 6) {
                //x
                return line[6];
            } else if (columnIndex == 7) {
                if((Integer) line[3]!=Offer.QUANTITY){
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2);
                    nf.setMinimumFractionDigits(2);
                    return nf.format((new Double((Integer) line[7])) / 100);
                } else {
                    return ""+(Integer)line[7] + "%";
                }
            } else if (columnIndex == 8) {
                //y
                return line[8];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private int columnSelected = 0;

    /** Creates new form OffersNew */
    public Offers(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(1).setPreferredWidth(100);
        tc.getColumn(2).setPreferredWidth(100);
        tc.getColumn(4).setPreferredWidth(200);
        tc.getColumn(5).setPreferredWidth(600);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setDefaultRenderer(Date.class, mtcr);
        jTable1.setDefaultRenderer(Double.class, mtcr);
        jTable1.setDefaultRenderer(Long.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        header.addMouseListener(this);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Viewoffers", Main.mainHelpSet);
    }

    public void execute() {
        columnSelected=0;
        drawGrid();
        Audio.play("Tada");
        setVisible(true);
    }

    private void drawGrid() {//line={ix, start, end, kind, product, productDescription, x, y, limit};
        PreparedStatement offersStatement;
        String orderType = "Offers.OfferType,Products.Description";
        String orderBarcode = "Offers.Product";
        String orderIx = "Offers.Ix ,Products.Description DESC ";
        String orderStartDate = "Offers.StartDate,Products.Description";
        String orderEndDate = "Offers.EndDate DESC,Products.Description ";
        String offers = "SELECT Offers.IX," +
                "Offers.startDate,Offers.EndDate,Offers.OfferType," +
                "Offers.Product," +
                "Products.Description,Offers.X,Offers.Y," +
                "Offers.LimitValue " +
                "FROM Offers,Products " +
                "WHERE Offers.Product=Products.ID ORDER BY ";
        String orderDescription="Products.Description";
        String orderX="Offers.X,Products.Description";
        String orderY="OffersY,Products.Description";
        String orderLimit="Offers.LimitValue,Products.Description";
        try {
            switch (columnSelected) {
                case 0:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderIx);
                    break;
                case 1:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderStartDate);
                    break;
                case 2:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderEndDate);
                    break;
                case 3:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderType);
                    break;
                case 4:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderBarcode);
                    break;
                case 5:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderDescription);
                    break;
                case 6:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderX);
                    break;
                case 7:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderY);
                case 8:
                    offersStatement = Main.getConnection().prepareStatement(offers + orderLimit);
                    break;
                default:return;
            }
            ResultSet rs = offersStatement.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {//offers to display
                ix = rs.getInt("Offers.ix");
                start = rs.getDate("StartDate");
                end = rs.getDate("EndDate");
                kind = rs.getInt("OfferType");
                product = rs.getLong("Product");
                productDescription = rs.getString("Products.Description");
                x = rs.getInt("X");
                y = rs.getInt("Y");
                limit = rs.getInt("LimitValue");
                Object[] line1 = {ix, start, end, kind, product,
                    productDescription, x, y, limit};
                rowData.add(line1);
                model.fireTableDataChanged();
            }
            rs.close();
        } catch (SQLException ex) {
            Audio.play("Ring");
            ex.printStackTrace();
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

        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        deleteProductButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        changeDateButton = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Offers"); // NOI18N
        setTitle(bundle.getString("Offers.title")); // NOI18N
        setName("Offers"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/add_obj.gif"))); // NOI18N
        addButton.setText(bundle.getString("Offers.addButton.text")); // NOI18N
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setLabel(bundle.getString("Offers.addButton.text")); // NOI18N
        addButton.setName("addButton"); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        deleteProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/delete_obj.gif"))); // NOI18N
        deleteProductButton.setBorderPainted(false);
        deleteProductButton.setContentAreaFilled(false);
        deleteProductButton.setLabel(bundle.getString("deleteProductButton")); // NOI18N
        deleteProductButton.setName("deleteProductButton"); // NOI18N
        deleteProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProductButtonActionPerformed(evt);
            }
        });

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/delete_obj.gif"))); // NOI18N
        deleteButton.setToolTipText(bundle.getString("deleteButton")); // NOI18N
        deleteButton.setContentAreaFilled(false);
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        changeDateButton.setText(bundle.getString("changeDateButton")); // NOI18N
        changeDateButton.setContentAreaFilled(false);
        changeDateButton.setName("changeDateButton"); // NOI18N
        changeDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDateButtonActionPerformed(evt);
            }
        });

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton2.setBorderPainted(false);
        closeButton2.setContentAreaFilled(false);
        closeButton2.setIconTextGap(0);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(changeDateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteProductButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(changeDateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, Short.MAX_VALUE)
                        .addComponent(deleteProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton, changeDateButton, closeButton2, deleteButton, deleteProductButton});

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setToolTipText(bundle.getString("Table_Tooltip")); // NOI18N
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1198, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    private void deleteProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProductButtonActionPerformed
        int[] selections = jTable1.getSelectedRows();
        if (selections.length == 0) {//no selected row
            Audio.play("Ring");
            return;
        }
        for(int i=0;i<selections.length;i++){
            ix = (Integer) model.getValueAt(selections[i], 0);
            product = (Long) model.getValueAt(selections[i], 4);
            //delete
            try {
                PreparedStatement del = Main.getConnection().prepareStatement(
                        "DELETE FROM Offers WHERE IX=? AND Product=?");
                del.setInt(1, ix);//needed because product could be in two offers
                del.setLong(2, product);
                del.executeUpdate();
                Audio.play("Beep");
            } catch (SQLException ex) {
                Audio.play("Ring");
                System.out.println(bundle.getString("SQL_Error:_") + ex.getMessage());
            }
        }
        drawGrid();
}//GEN-LAST:event_deleteProductButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {//no selected row
            Audio.play("Ring");
            return;
        }
        ix = (Integer) model.getValueAt(selection, 0);
        //delete
        try {
            PreparedStatement del = Main.getConnection().prepareStatement(
                    "DELETE FROM Offers WHERE IX=?");
            del.setInt(1, ix);//all offers with same ix
            del.executeUpdate();
            Audio.play("Beep");
        } catch (SQLException ex) {
            Audio.play("Ring");
            System.out.println(bundle.getString("SQL_Error:_") + ex.getMessage());
        }
        drawGrid();
}//GEN-LAST:event_deleteButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {//no selected row
            Audio.play("Ring");
            return;
        }
        ix = (Integer) model.getValueAt(selection, 0);
        Main.offer.execute(ix);
        drawGrid();
    }//GEN-LAST:event_addButtonActionPerformed

    private void changeDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDateButtonActionPerformed
        int[] selections = jTable1.getSelectedRows();
        if (selections.length == 0) {//no selected row
            Audio.play("Ring");
            return;
        }
        //get the new dates
        java.sql.Date newEndDate = Main.selectDate.execute(endDate);
        for(int i=0;i<selections.length;i++){
            ix = (Integer) model.getValueAt(selections[i], 0);
            endDate=(java.sql.Date) model.getValueAt(selections[i], 2);
            String cd = "UPDATE Offers SET EndDate=? WHERE IX=? ";
            try {
                PreparedStatement ucd = Main.getConnection().prepareStatement(cd);
                ucd.setDate(1, newEndDate);
                ucd.setInt(2, ix);//all offers with same ix
                ucd.executeUpdate();
                Audio.play("Beep");
            } catch (SQLException ex) {
                Audio.play("Ring");
                System.out.println(bundle.getString("SQL_Error:_") + ex.getMessage());
            }
        }
        drawGrid();
    }//GEN-LAST:event_changeDateButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Offers dialog = new Offers(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton addButton;
    private javax.swing.JButton changeDateButton;
    private javax.swing.JButton closeButton2;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteProductButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
        TableColumnModel colModel = jTable1.getColumnModel();
        // The index of the column whose header was clicked
        int vColIndex = colModel.getColumnIndexAtX(e.getX());
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
        if (!headerRect.contains(e.getX(), e.getY())) {
            // Mouse was clicked between column heads
            // vColIndex is the column head closest to the click
            // vLeftColIndex is the column head to the left of the click
            int vLeftColIndex = vColIndex;
            if (e.getX() < headerRect.x) {
                vLeftColIndex--;
            }
        }
        columnSelected = vColIndex;
        Audio.play("Beep");
        drawGrid();
    }

    @Override
    public void mousePressed(MouseEvent e) {
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
