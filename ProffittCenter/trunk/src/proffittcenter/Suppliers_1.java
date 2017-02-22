/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SuppliersNew.java
 *
 * Created on 18-Mar-2009, 21:51:52
 */
package proffittcenter;

import java.awt.Cursor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Suppliers_1 extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Suppliers");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Description"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Address"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Post_Code"));
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Phone_number"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Weeks_Credit"));
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("DaysToNextDelivery"));
    private Integer id = 0;
    private String description = "";
    private String address = "";
    private String phoneNumber = "";
    private String postCode = "";
    private Short weeksCredit = 0;
    private short daysToNextDelivery=0;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.LEFT,
        SwingConstants.RIGHT,SwingConstants.RIGHT};
    private Object[] line = {id, description, address, phoneNumber, postCode, weeksCredit, daysToNextDelivery};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private AbstractTableModel model = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }

        @Override
        public int getColumnCount() {
            return tableHeader.size();
        }

        @Override
        public int getRowCount() {
            return rowData.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if(rowData.isEmpty())return 0;
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//description
                return line[0];
            } else if (columnIndex == 1) {//rate
                return line[1];
            } else if (columnIndex == 2) {//rate
                return (String) line[2];
            } else if (columnIndex == 3) {
                //address
                return line[3];
            } else if (columnIndex == 4) {
                //postCode
                return line[4];
            } else if (columnIndex == 5) {
                //weeksCredit
                return line[5];
            } else if (columnIndex == 6) {
                //days to next delivery
                return line[6];
            }else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private int columnSelected=0;
    private PreparedStatement ps;
    private ResultSet rs;
    private int department;

    /** Creates new form SuppliersNew */
    public Suppliers_1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(1).setPreferredWidth(600);
        tc.getColumn(2).setPreferredWidth(1200);
        tc.getColumn(3).setPreferredWidth(400);
        tc.getColumn(4).setPreferredWidth(400);
        tc.getColumn(5).setPreferredWidth(500);
        tc.getColumn(6).setPreferredWidth(600);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Suppliers", Main.mainHelpSet);
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
        jPanel2 = new javax.swing.JPanel();
        newBtn = new javax.swing.JButton();
        modifyBtn = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Suppliers"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("Suppliers"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPanel1KeyReleased(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N

        newBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/plus.png"))); // NOI18N
        newBtn.setContentAreaFilled(false);
        newBtn.setName("newBtn"); // NOI18N
        newBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBtnActionPerformed(evt);
            }
        });
        newBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                newBtnKeyReleased(evt);
            }
        });

        modifyBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Info.png"))); // NOI18N
        modifyBtn.setContentAreaFilled(false);
        modifyBtn.setName("modifyBtn"); // NOI18N
        modifyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyBtnActionPerformed(evt);
            }
        });
        modifyBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                modifyBtnKeyReleased(evt);
            }
        });

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton2.setContentAreaFilled(false);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });
        closeButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                closeButton2KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(726, Short.MAX_VALUE)
                .addComponent(newBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(modifyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(newBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modifyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton2, modifyBtn, newBtn});

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBtnActionPerformed
        Main.supplier.execute("");
        drawGrid();
}//GEN-LAST:event_newBtnActionPerformed

    private void newBtnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newBtnKeyReleased
}//GEN-LAST:event_newBtnKeyReleased

    private void modifyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyBtnActionPerformed
        int selection =jTable1.getSelectionModel().getLeadSelectionIndex(); //jTable1.getSelectedRow();
        if (selection < 0) {
            //no selected row
            Audio.play("Ring");
            return;
        }
        selection=jTable1.convertRowIndexToModel(selection);
        String ids = model.getValueAt(selection, 0).toString();
//        Main.supplier.setLocationRelativeTo(this);
        Main.supplier.execute(ids);
        drawGrid();
}//GEN-LAST:event_modifyBtnActionPerformed

    private void modifyBtnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_modifyBtnKeyReleased
}//GEN-LAST:event_modifyBtnKeyReleased

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    private void closeButton2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_closeButton2KeyReleased
}//GEN-LAST:event_closeButton2KeyReleased

    private void jPanel1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyReleased
}//GEN-LAST:event_jPanel1KeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        modifyBtn.setEnabled(true);
    }//GEN-LAST:event_jTable1MouseClicked

    public void execute() {
        columnSelected=1;
        drawGrid();
        Audio.play("Tada");
//        //FormRestore.createPosition(this, root);
        modifyBtn.setEnabled(false);
        setVisible(true);
//        //FormRestore.destroyPosition(this, root);
    }

    private void drawGrid() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement suppliers;
        try {
            String s = "SELECT * FROM Suppliers ORDER BY " ;
            if (columnSelected == 0) {
                suppliers = Main.getConnection().prepareStatement(s + "ID");
            } else if (columnSelected == 1) {
                suppliers = Main.getConnection().prepareStatement(s + "Description");
            } else if (columnSelected == 2) {
                suppliers = Main.getConnection().prepareStatement(s + "Address");
            } else if (columnSelected == 3) {
                suppliers = Main.getConnection().prepareStatement(s + "PostCode");
            } else if (columnSelected == 4) {
                suppliers = Main.getConnection().prepareStatement(s + "PhoneNumber");
            } else if (columnSelected == 5) {
                suppliers = Main.getConnection().prepareStatement(s + "WeeksCredit");
            } else if (columnSelected == 6) {
                suppliers = Main.getConnection().prepareStatement(s + "DaysToNextDelivery");
            }else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            ResultSet rs = suppliers.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {
                //suppliers to display
                id = rs.getInt("ID");
                description = rs.getString("Description");
                address = rs.getString("Address");
                postCode = rs.getString("PostCode");
                phoneNumber = rs.getString("PhoneNumber");
                weeksCredit = rs.getShort("WeeksCredit");
                daysToNextDelivery = rs.getShort("DaysToNextDelivery");
                Object[] lineA = {id, description, address, postCode, phoneNumber, weeksCredit,daysToNextDelivery};
                rowData.add(lineA);
                model.fireTableDataChanged();
            }
            rs.close();
        } catch (SQLException ex) {
            Audio.play("Ring");
            ex.printStackTrace();
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Suppliers_1 dialog = new Suppliers_1(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton closeButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton modifyBtn;
    private javax.swing.JButton newBtn;
    // End of variables declaration//GEN-END:variables

    
}
