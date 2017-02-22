/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OperatorsEscape.java
 *
 * Created on 28-Oct-2009, 19:24:17
 */

package proffittcenter;

import java.awt.Component;
import java.awt.Cursor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author HP_Owner
 */
public class Operators extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Operators");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Description"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Authority"));
    private Long password;
    private String description;
    private Integer authority;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,SwingConstants.LEFT};
    private Object[] line = {password, description, authority};
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
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {
                //id
                java.text.DecimalFormat nft = new java.text.DecimalFormat("0000");
                nft.setDecimalSeparatorAlwaysShown(false);
                return "1000037"+nft.format(line[0]);
            } else if (columnIndex == 1) {
                //description
                return line[1];
            } else if (columnIndex == 2) {
                //Authority
                switch ((Integer) line[2]) {
                    case 0:
                        return bundle.getString("Owner");
                    case 1:
                        return bundle.getString("Manager");
                    case 2:
                        return bundle.getString("Senior_staff");
                    case 3:
                        return bundle.getString("Junior_staff");
                    case 4:
                        return bundle.getString("Trainee");
                    case 5:
                        return bundle.getString("Locked_out");
                    default:
                        return "";
                }
            } else {
                return null;
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private final MyTableCellRenderer mtcr;
    private int columnSelected=0;

    /** Creates new form OperatorsEscape */
    public Operators(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(1);
        column.setPreferredWidth(400);
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Operators", Main.mainHelpSet);
    }

     public void execute() {
        drawGrid();
        Audio.play("Tada");
        newBtn.requestFocus();
        //FormRestore.createPosition(this, root);
        setVisible(true);
        //FormRestore.destroyPosition(this, root);
    }

      private void drawGrid() {
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement operators;
        try {
            String operatorsString = "SELECT * FROM Operators ORDER BY ";
             if (columnSelected == 0) {
                operators = Main.getConnection().prepareStatement(operatorsString + "ID");
            } else if (columnSelected == 1) {
                operators = Main.getConnection().prepareStatement(operatorsString + "Description");
            } else if (columnSelected == 2) {
                operators = Main.getConnection().prepareStatement(operatorsString + "Authority");
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            ResultSet rs = operators.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {
                //operators to display
                password = rs.getLong("ID");
                description = rs.getString("Description");
                authority = rs.getInt("Authority");
                Object[] line1 = {password, description, authority};
                rowData.add(line1);
                model.fireTableDataChanged();
            }
            rs.close();
        } catch (SQLException ex) {
            Audio.play("Ring");
            ex.printStackTrace();
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
      
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        closeButton2 = new JButton();
        detailsButton = new JButton();
        newBtn = new JButton();
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Operators"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("Operators"); // NOI18N

        jPanel1.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jPanel1.setName("jPanel1"); // NOI18N

        closeButton2.setIcon(new ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton2.setBorderPainted(false);
        closeButton2.setContentAreaFilled(false);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });

        detailsButton.setIcon(new ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Info.png"))); // NOI18N
        detailsButton.setBorderPainted(false);
        detailsButton.setContentAreaFilled(false);
        detailsButton.setName("detailsButton"); // NOI18N
        detailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                detailsButtonActionPerformed(evt);
            }
        });

        newBtn.setIcon(new ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/add_obj.gif"))); // NOI18N
        newBtn.setBorderPainted(false);
        newBtn.setContentAreaFilled(false);
        newBtn.setName("newBtn"); // NOI18N
        newBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newBtnActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(500, Short.MAX_VALUE)
                .addComponent(newBtn, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(detailsButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(closeButton2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(closeButton2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(newBtn, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                    .addComponent(detailsButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {closeButton2, detailsButton});

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new DefaultTableModel(
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

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(444, Short.MAX_VALUE)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(67, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButton2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        Audio.play("Beep");
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    private void detailsButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_detailsButtonActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {
            //no selected row
            Audio.play("Ring");
            return;
        }
        selection=jTable1.convertRowIndexToModel(selection);
        String ids = model.getValueAt(selection, 0).toString();
//        Main.operator.setLocationRelativeTo(this);
        Main.operator.execute(ids);
        drawGrid();
}//GEN-LAST:event_detailsButtonActionPerformed

    private void newBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_newBtnActionPerformed
        Main.operator.execute("");
        drawGrid();
}//GEN-LAST:event_newBtnActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Operators dialog = new Operators(new javax.swing.JFrame(), true);
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
    private JButton closeButton2;
    private JButton detailsButton;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JTable jTable1;
    private JButton newBtn;
    // End of variables declaration//GEN-END:variables

    

}
