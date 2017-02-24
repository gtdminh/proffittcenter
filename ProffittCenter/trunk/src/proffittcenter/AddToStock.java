/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dave
 */
public class AddToStock extends EscapeDialog {

    private int quantity;
    private int sku = 0;
    private PreparedStatement productExists;
    private ResultSet rs;
    private String updateSku;
    private PreparedStatement ps;

    /**
     * Creates new form AddToStock
     */
    public AddToStock(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Addtostock", Main.mainHelpSet);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        barcodeText = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        quantitySpinner = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add to stock");
        setName("addToStock"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/AddToStock"); // NOI18N
        jLabel1.setText(bundle.getString("jLabel1")); // NOI18N

        barcodeText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                barcodeTextFocusLost(evt);
            }
        });
        barcodeText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                barcodeTextKeyReleased(evt);
            }
        });

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okButton.setToolTipText(bundle.getString("okButton")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okButton.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                okButtonFocusGained(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText(bundle.getString("jLabel3")); // NOI18N

        quantitySpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000000, 1));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Use only for adding free stock");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(barcodeText)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 249, Short.MAX_VALUE))))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(barcodeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (!ok()) {
            barcodeText.requestFocus();
        } else {
            Audio.play("Beep");
            barcodeText.requestFocus();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void okButtonFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_okButtonFocusGained
    }//GEN-LAST:event_okButtonFocusGained

    private void barcodeTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barcodeTextKeyReleased
        String data = barcodeText.getText();
        if (data.length() == 3) {
            if (Main.alphaLookup.isBarcode(data)) {
                return;
            }
            if (Main.alphaLookup.isFound(data)) {
                barcodeText.setText(Main.alphaLookup.returnDataIs());
            } else {
                barcodeText.setText("");
            }
        }
        getRootPane().setDefaultButton(okButton);
        barcodeText.requestFocus();
    }//GEN-LAST:event_barcodeTextKeyReleased

    private void barcodeTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barcodeTextFocusLost
        try {
            // check product exists
            String productExistsString = "SELECT Sku FROM Products WHERE ID=?";
            Connection connection = Main.getConnection();
            productExists = connection.prepareStatement(productExistsString);
            String barcode = barcodeText.getText();
            productExists.setString(1, barcode);
            rs = productExists.executeQuery();
            if (rs.first()) {
                //product exists
                sku = rs.getInt("Sku");
                quantitySpinner.requestFocus();
                Audio.play("Vibes");
            } else {
                sku = 0;
                barcodeText.setText("");
                Audio.play("Ring");
                barcodeText.requestFocus();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(AddToStock.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (productExists != null) {
                try {
                    rs.close();
                    barcodeText.setText("");
                    Audio.play("Vibes");
//                    productExists.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AddToStock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_barcodeTextFocusLost

    private boolean ok() {
        if (sku != 0) {
            try {
                //need to increase stock level
                quantity = (Integer) quantitySpinner.getValue();
                barcodeText.setText("");
                quantitySpinner.setValue(1);
                updateSku = "UPDATE Skus SET "
                        + " Quantity=Quantity+? "
                        + " WHERE ID=?";
                ps = Main.getConnection().prepareStatement(updateSku);
                ps.setInt(2, sku);
                ps.setInt(1, quantity);
                ps.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(AddToStock.class.getName()).log(Level.SEVERE, null, ex);
            }            
            return false;
        }
        return false;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AddToStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddToStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddToStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddToStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddToStock dialog = new AddToStock(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField barcodeText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton okButton;
    private javax.swing.JSpinner quantitySpinner;
    // End of variables declaration//GEN-END:variables

    void execute() {
        Audio.play("Tada");
        barcodeText.setText("");
        barcodeText.requestFocus();
        quantitySpinner.setValue(1);
        sku = 0;
        setVisible(true);
    }
}
