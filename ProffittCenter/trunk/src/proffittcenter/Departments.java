
        /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DepartmentsNew.java
 *
 * Created on 10-Mar-2009, 15:15:12
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Departments extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Departments");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Description"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("TaxID"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Tax_Description"));
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("TaxRate"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Margin"));
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("Minimum"));
    @SuppressWarnings("unchecked")
    private boolean b7 = tableHeader.add(bundle.getString("Restrictions"));
    private int department;
    private String departmentDescription;
    private int tax;
    private String taxDescription;
    private int taxRate;
    private int margin;
    private int minimum;
    private String restriction;
    private Integer department1;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.RIGHT, SwingConstants.LEFT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT,SwingConstants.LEFT};
    private Object[] line = {new Integer(department), departmentDescription,new Integer( tax), taxDescription,
        new PerCent(taxRate), new PerCent(margin), new Integer(minimum),restriction};
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
            if (columnIndex == 0) {//department
                return line[0];
            } else if (columnIndex == 1) {//departmentDescription
                String s=(String) line[1];
                return line[1];
            } else if (columnIndex == 2) {//tax
                return line[2];
            } else if (columnIndex == 3) {//tax description
                return line[3];
            } else if (columnIndex == 4) {//tax rate
                return line[4];
            } else if (columnIndex == 5) {
                return line[5] ;
            } else if (columnIndex == 6) {
                return line[6];
            } else if (columnIndex == 7) {
                return line[7];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };

    /** Creates new form Departments */
    public Departments(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(1).setPreferredWidth(400);
        tc.getColumn(3).setPreferredWidth(200);
        tc.getColumn(6).setPreferredWidth(100);
        tc.getColumn(7).setPreferredWidth(100);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Departments", Main.mainHelpSet);
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
        newBtn = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        deleteBtn = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Departments"); // NOI18N
        setTitle(bundle.getString("Departments.title")); // NOI18N
        setName("Departments"); // NOI18N

        jPanel1.setName("Departments"); // NOI18N

        newBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/plus.png"))); // NOI18N
        newBtn.setContentAreaFilled(false);
        newBtn.setName("newBtn"); // NOI18N
        newBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBtnActionPerformed(evt);
            }
        });

        updateBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        updateBtn.setContentAreaFilled(false);
        updateBtn.setName("updateBtn"); // NOI18N
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Department ID", "Description", "Tax ID", "Tax rate", "Margin", "Minimum"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        deleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Minus.png"))); // NOI18N
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setName("deleteBtn"); // NOI18N
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(673, Short.MAX_VALUE)
                .addComponent(newBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(newBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton, updateBtn});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBtnActionPerformed
        Main.department.execute("");
        drawGrid();
    }//GEN-LAST:event_newBtnActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {//no selected row
            return;
        }
        selection=jTable1.convertRowIndexToModel(selection);
        department1 = (Integer) model.getValueAt(selection, 0);
        Main.department.execute(Integer.toString(department1));
        drawGrid();
    }//GEN-LAST:event_updateBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        //Do not delete if any products exist that are linked so unlink first
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {//no selected row
            Audio.play("Ring");
            return;
        }
        department1 = (Integer) model.getValueAt(selection, 0);
        //find sku with this department
        PreparedStatement unlinkSkus;
        try {
            unlinkSkus = Main.getConnection().prepareStatement(
                    "UPDATE Skus SET Department=1 " +
                    "WHERE Department=?");
            unlinkSkus.setString(1, Integer.toString(department1));
            String s="Help";
            if(s==null)s="";
            unlinkSkus.executeUpdate();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "Departments.deleteBtnActionPerformed ", "Exception: " + ex.getMessage());
        }
        PreparedStatement deleteDepartment;
        try {
            deleteDepartment = Main.getConnection().prepareStatement(
                    "DELETE FROM " +
                    "Departments WHERE ID=? AND ID!=1");
            deleteDepartment.setInt(1, department1);
            deleteDepartment.executeUpdate();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "Departments.deleteBtnActionPerformed ", "Exception: " + ex.getMessage());
        }
        drawGrid();
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

    }//GEN-LAST:event_jTable1MouseClicked

    public void execute() {
        drawGrid();
        Audio.play("Tada");
        setVisible(true);
    }

    private void drawGrid() {
        PreparedStatement departments;
        try {
            departments = Main.getConnection().prepareStatement(SQL.departments );
            ResultSet rs = departments.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {//departments to display
                department = rs.getInt("Department");
                departmentDescription = rs.getString("departmentDescription");
                tax = rs.getInt("tax");
                taxDescription = rs.getString("T.Description");
                taxRate = rs.getInt("T.Rate");
                margin = rs.getInt("margin");
                restriction=rs.getString("Restriction");
                minimum=rs.getInt("Minimum");
                Object[] line1 = new Object[]{new Integer(department), departmentDescription,new Integer(tax),
                    taxDescription,new PerCent(taxRate),new PerCent(margin), new Integer(minimum),restriction
                };
                boolean b=rowData.add(line1);
                model.fireTableDataChanged();
            }
            rs.close();
            departments.close();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "Departments.drawGrid ", "Exception: " + ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Departments dialog = new Departments(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton deleteBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton newBtn;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables

    
}
