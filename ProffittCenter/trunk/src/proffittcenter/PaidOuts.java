/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PaidOuts.java
 *
 * Created on 28-Apr-2009, 17:20:48
 */
package proffittcenter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class PaidOuts extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/PaidOuts");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Till_ID"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("When"));
    @SuppressWarnings("unchecked")
    private boolean b7 = tableHeader.add(bundle.getString("Time"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Operator"));
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Why"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Amount") + " (" + Main.shop.poundSymbol + ")");
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("InOut"));
    private Long id;
    private Integer tillId;
    private Date when;
    private String operator;
    private String description;
    private Integer amount=0;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private Integer out;
    private String time;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.LEFT, SwingConstants.LEFT, 
        SwingConstants.LEFT,SwingConstants.LEFT,SwingConstants.RIGHT,SwingConstants.LEFT};
    private Object[] line = {id, tillId, when,time, operator, description,new Money(amount),out};
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
            if(rowData.isEmpty())return 0;
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//ID
                return line[0];
            } else if (columnIndex == 1) {//tillID
                return line[1];
            } else if (columnIndex == 2) {//When
                return line[2];
            } else if (columnIndex == 3) {
                return line[3];
            } else if (columnIndex == 4) {
                return line[4];
            } else if (columnIndex == 5) {
                return line[5];
            }else if (columnIndex == 6) {//amount
                return line[6];
            } else if (columnIndex == 7) {
                if((Integer)line[7]>0){//out
                    return bundle.getString("PaidOut");
                }else if((Integer)line[7]<0) {
                    return bundle.getString("PaidIn");
                }else {
                    return bundle.getString("NoSale!");
                }
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private int columnSelected;
    private PreparedStatement sqlOrdered;
    private String sql = "SELECT PaidOuts.ID,PaidOuts.TillID,"
            + "Date(PaidOuts.WhenCreated) AS Date, Time(PaidOuts.WhenCreated) AS Time,"
            +"Operators.Description AS Operator," +
            "PaidOuts.Description,PaidOuts.Amount " +
            "FROM PaidOuts,Operators " +
            "WHERE (PaidOuts.TillID=? OR 0=?) AND PaidOuts.Operator=Operators.ID " +
            "AND Date(PaidOuts.WhenCreated)>=? " + //sd
            "AND Date(PaidOuts.WhenCreated)<=? " + //ed
            "ORDER BY ";
    private String byId="ID DESC,PaidOuts.WhenCreated DESC";
    private String byTillId="TillId,PaidOuts.WhenCreated DESC";
    private String byDate="PaidOuts.WhenCreated,PaidOuts.WhenCreated DESC";
    private String byOpereator="Operator,PaidOuts.WhenCreated DESC";
    private String byDescription="PaidOuts.description,PaidOuts.WhenCreated DESC";
    private String byAmount="SIGN(PaidOuts.Amount),ABS(PaidOuts.Amount),PaidOuts.WhenCreated DESC";
    private String byInOut = "SIGN(PaidOuts.Amount)";

    /** Creates new form PaidOuts */
    public PaidOuts(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(150);
        tc.getColumn(1).setPreferredWidth(150);
        tc.getColumn(2).setPreferredWidth(200);
        tc.getColumn(3).setPreferredWidth(350);
        tc.getColumn(4).setPreferredWidth(250);
        tc.getColumn(5).setPreferredWidth(200);
        tc.getColumn(6).setPreferredWidth(200);
        tc.getColumn(7).setPreferredWidth(200);
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
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Paidouts", Main.mainHelpSet);
    }

    private void drawGrid() {
        Audio.play("Beep");
        try {
            switch (columnSelected) {
                case 0:
                    sqlOrdered = Main.getConnection().prepareStatement(sql + byId );
                    break;
                case 1:
                    sqlOrdered = Main.getConnection().prepareStatement(sql + byTillId);
                    break;
                case 2:
                    sqlOrdered = Main.getConnection().prepareStatement(sql + byDate);
                    break;
                case 3:
                    sqlOrdered = Main.getConnection().prepareStatement(sql + byOpereator);
                    break;
                case 4:
                    sqlOrdered = Main.getConnection().prepareStatement(sql + byDescription);
                    break;
                case 5:
                    sqlOrdered = Main.getConnection().prepareStatement(sql + byAmount);
                    break;
                case 6:
                    sqlOrdered = Main.getConnection().prepareStatement(sql + byInOut);
                    break;
                default:return;
            }
            sqlOrdered.setInt(1, tillId);
            sqlOrdered.setInt(2, tillId);
            Calendar startDate = jDateChooser1.getCalendar();
            Calendar endDate = jDateChooser2.getCalendar();
            java.sql.Date sd = new java.sql.Date(startDate.getTimeInMillis());//start
            sqlOrdered.setDate(3, sd);
            java.sql.Date ed = new java.sql.Date(endDate.getTimeInMillis());//start
            sqlOrdered.setDate(4, ed);
            ResultSet rs = sqlOrdered.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
             while (rs.next()) {//customers to display
                id = rs.getLong("ID");
                tillId=rs.getInt("TillId");
                when = rs.getDate("Date");
                time = rs.getString("Time");
                operator = rs.getString("Operator");
                description = rs.getString("PaidOuts.Description");
                amount = rs.getInt("Amount");
                out = 1;
                if(amount <0 ){
                    amount = - amount;
                    out = -1;
                }else if (amount==0){
                    out=0;
                }
                Object[] aLine = {id,tillId, when,time, operator, description, new Money(amount),out};
                rowData.add(aLine);
            }
                model.fireTableDataChanged();
            rs.close();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "PaidOuts.drawGrid ", "Exception: " + ex.getMessage());
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
        jLabel1 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/PaidOuts"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("PaidOuts"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
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

        jPanel1.setName("jPanel1"); // NOI18N

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesBy"); // NOI18N
        jLabel1.setText(bundle1.getString("SalesBy.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jDateChooser1.setName("jDateChooser1"); // NOI18N

        jDateChooser2.setName("jDateChooser2"); // NOI18N

        jLabel2.setText(bundle1.getString("SalesBy.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okButton.setBorderPainted(false);
        okButton.setContentAreaFilled(false);
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton2.setBorderPainted(false);
        closeButton2.setContentAreaFilled(false);
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
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        drawGrid();
    }//GEN-LAST:event_okButtonActionPerformed

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButton2ActionPerformed

    public void execute(int tillId) {
        this.tillId = tillId;
        Audio.play("Tada");
        Calendar c = Calendar.getInstance();
        jDateChooser2.setCalendar(c);
        c.add(Calendar.MONTH, -1);
        jDateChooser1.setCalendar(c);;
        drawGrid();
        setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                PaidOuts dialog = new PaidOuts(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    
}
