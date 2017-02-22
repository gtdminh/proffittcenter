package proffittcenter;

import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JRootPane;
import javax.swing.JSpinner;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SelectTill.java
 *
 * Created on 18-Dec-2008, 17:36:38
 */
/**
 *
 * @author HP_Owner
 */
public class SelectTill_1 extends EscapeDialog {

    Preferences root = Preferences.userNodeForPackage(getClass());
    private boolean ok;
        JFormattedTextField ftf;

    /** Creates new form SelectTill */
    public SelectTill_1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        JRootPane rp = getRootPane();
        rp.setDefaultButton(okBtn);
        ftf = getTextField(jSpinner1);
        JComponent editor = jSpinner1.getEditor();
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void ftfkeyReleased(java.awt.event.KeyEvent evt) {
                ftfKeyReleased(evt);
            }

            private void ftfKeyReleased(KeyEvent evt) {
                okBtn.requestFocus();
            }
        });
    }

    int execute() {
        ok=false;
        Audio.play("Tada");
        jSpinner1.setValue(Main.shop.getTillId());
        ftf.requestFocus();
        ftf.setText(ftf.getText());
        ftf.selectAll();
        //FormRestore.createPosition(this, root);
        setVisible(true);
        //FormRestore.destroyPosition(this, root);
        if (ok) {
            return (Integer) jSpinner1.getValue();
        } else {
            return -1;
        }
    }

    private JFormattedTextField getTextField(JSpinner jSpinner1) {
        JComponent editor = jSpinner1.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            return ((JSpinner.DefaultEditor) editor).getTextField();
        } else {
            System.err.println("Unexpected editor type: " + jSpinner1.getEditor().getClass() + " isn't a descendant of DefaultEditor");
            return null;
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

        jSpinner1 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        okBtn = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SelectTill"); // NOI18N
        setTitle(bundle.getString("Select Till")); // NOI18N
        setName("SelectTill"); // NOI18N

        jSpinner1.setName("jSpinner1"); // NOI18N

        jLabel1.setText(bundle.getString("SelectTill.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        okBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/OK.png"))); // NOI18N
        okBtn.setContentAreaFilled(false);
        okBtn.setName("okBtn"); // NOI18N
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        ok = true;
        this.setVisible(false);
}//GEN-LAST:event_okBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                SelectTill_1 dialog = new SelectTill_1(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JButton okBtn;
    // End of variables declaration//GEN-END:variables
}
