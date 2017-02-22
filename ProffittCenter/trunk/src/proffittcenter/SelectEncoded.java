/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SelectEncoded.java
 *
 * Created on 01-Jul-2010, 09:40:57
 */

package proffittcenter;

import java.util.ResourceBundle;

/**
 *
 * @author Dave
 */
public class SelectEncoded extends EscapeDialog {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SelectEncoded");
    private int returnValue;
    
    /** Creates new form SelectEncoded
     * @param parent
     * @param modal */
    public SelectEncoded(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jComboBox1.addItem(bundle.getString("NotEncoded"));
        jComboBox1.addItem(bundle.getString("EncodedByPriceParity"));
        jComboBox1.addItem(bundle.getString("EncodedByWeightParity"));
        jComboBox1.addItem(bundle.getString("EncodedByPriceNoParity"));
        jComboBox1.addItem(bundle.getString("EncodedByWeightNoParity"));
        jComboBox1.addItem(bundle.getString("EncodedByPrice5"));
        jComboBox1.addItem(bundle.getString("EncodedByWeight5"));
        getRootPane().setDefaultButton(okButton);
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SelectEncoded"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setName("selectencoded\n"); // NOI18N

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton.setContentAreaFilled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/OK.png"))); // NOI18N
        okButton.setContentAreaFilled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        returnValue=jComboBox1.getSelectedIndex();
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SelectEncoded dialog = new SelectEncoded(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                int x = dialog.execute();
                System.out.println(x);
                System.exit(0);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    int execute() {
            Audio.play("TaDa");
        returnValue=-1;
        jComboBox1.setSelectedIndex(0);
        jComboBox1.requestFocus();
        setVisible(true);
        return returnValue;
    }

}
