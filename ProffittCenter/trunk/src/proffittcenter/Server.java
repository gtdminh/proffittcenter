 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Server.java
 *
 * Created on 20-Sep-2010, 21:09:51
 */
package proffittcenter;

import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.help.CSH;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static proffittcenterworkingcopy.DatabaseCreate.connectionQuery;

/**
 *
 * @author Dave
 */
public class Server extends javax.swing.JPanel {

    private char[] input1;
    private char[] input2;
    Preferences root = Preferences.userNodeForPackage(getClass());
    public String password = root.get("Password", "");//1234;
    public String serverName = root.get("ServerName", "localhost");//www.proffittcenter.org;
    public String database = root.get("Database", "till");//till proff2_till;
    public String userName = root.get("UserName", "root");//root proff2_test;
    static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Settings");
    public boolean internetEnabled = root.getBoolean("InternetEnabled", true);
    public String backupUser = root.get("backupUser", "");
    private char[] input3;
    public String backupPassword = root.get("backupPassword", "");
    public boolean backupOnline = root.getBoolean("backupOnline", false);
    private final JFileChooser chooser= new JFileChooser();
    public String dropboxDirectoryAsString= root.get("dropboxDirectory", "");
    public File dropboxDirectory = new File(dropboxDirectoryAsString);    
    JFrame parent = new JFrame();
    private String hostname;

    /** Creates new form Server */
    public Server() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jServerName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jDatabase = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPassword1 = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        jPassword2 = new javax.swing.JPasswordField();
        createDatabaseButton = new javax.swing.JButton();
        createTablesButton = new javax.swing.JButton();
        internetCheckbox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jUserName = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        locateDropbox = new javax.swing.JButton();
        dropboxLocation = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        connectionLabel = new javax.swing.JLabel();
        testConnectionButton = new javax.swing.JButton();
        dropboxButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Settings"); // NOI18N
        setName(bundle.getString("Server.name")); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/CashupReconciliation"); // NOI18N
        jLabel5.setText(bundle1.getString("Settings.jLabel5.text")); // NOI18N
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel5.setName("jLabel5"); // NOI18N

        jServerName.setName("jServerName"); // NOI18N

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(bundle1.getString("Settings.jLabel6.text_1")); // NOI18N
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel6.setName("jLabel6"); // NOI18N

        jDatabase.setName("jDatabase"); // NOI18N

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(bundle1.getString("Settings.jLabel7.text_1")); // NOI18N
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(bundle1.getString("Settings.jLabel8.text_1")); // NOI18N
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel8.setName("jLabel8"); // NOI18N

        jPassword1.setName("jPassword1"); // NOI18N

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText(bundle1.getString("Settings.jLabel9.text_1")); // NOI18N
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel9.setName("jLabel9"); // NOI18N

        jPassword2.setName("jPassword2"); // NOI18N

        createDatabaseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/CreateDatabase.png"))); // NOI18N
        createDatabaseButton.setToolTipText(bundle.getString("CreateDatabase")); // NOI18N
        createDatabaseButton.setContentAreaFilled(false);
        createDatabaseButton.setEnabled(false);
        createDatabaseButton.setName("createDatabaseButton"); // NOI18N
        createDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createDatabaseButtonActionPerformed(evt);
            }
        });

        createTablesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/CreateTables.png"))); // NOI18N
        createTablesButton.setToolTipText(bundle.getString("CreateTables")); // NOI18N
        createTablesButton.setContentAreaFilled(false);
        createTablesButton.setEnabled(false);
        createTablesButton.setIconTextGap(0);
        createTablesButton.setName("createTablesButton"); // NOI18N
        createTablesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createTablesButtonActionPerformed(evt);
            }
        });

        internetCheckbox.setName("internetCheckbox"); // NOI18N

        jLabel1.setText(bundle.getString("jLabel1")); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLabel1KeyPressed(evt);
            }
        });

        jUserName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        jUserName.setName("jUserName"); // NOI18N
        jUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUserNameActionPerformed(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText(bundle.getString("Server.jTextArea1.text")); // NOI18N
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        locateDropbox.setText(bundle.getString("Server.locateDropbox.text")); // NOI18N
        locateDropbox.setName("locateDropbox"); // NOI18N
        locateDropbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locateDropboxActionPerformed(evt);
            }
        });

        dropboxLocation.setText(bundle.getString("Server.dropboxLocation.text")); // NOI18N
        dropboxLocation.setName("dropboxLocation"); // NOI18N

        connectButton.setText(bundle.getString("Server.connectButton.text")); // NOI18N
        connectButton.setName("connectButton"); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        connectionLabel.setText(bundle.getString("Server.connectionLabel.text")); // NOI18N
        connectionLabel.setName("connectionLabel"); // NOI18N

        testConnectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Test.png"))); // NOI18N
        testConnectionButton.setToolTipText(bundle.getString("server.Test")); // NOI18N
        testConnectionButton.setContentAreaFilled(false);
        testConnectionButton.setName("testConnectionButton"); // NOI18N
        testConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testConnectionButtonActionPerformed(evt);
            }
        });

        dropboxButton.setText(bundle.getString("Server.dropboxButton.text")); // NOI18N
        dropboxButton.setName("dropboxButton"); // NOI18N
        dropboxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropboxButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jUserName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addComponent(jPassword1, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addComponent(jPassword2)
                                .addComponent(jServerName))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(internetCheckbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectionLabel))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(dropboxButton, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(locateDropbox, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dropboxLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(testConnectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(createTablesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {createDatabaseButton, createTablesButton, testConnectionButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jServerName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPassword1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPassword2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(internetCheckbox)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(connectButton)
                        .addComponent(connectionLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dropboxButton)
                    .addComponent(locateDropbox)
                    .addComponent(dropboxLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                    .addComponent(createTablesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createDatabaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(testConnectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {createDatabaseButton, createTablesButton, testConnectionButton});

    }// </editor-fold>//GEN-END:initComponents

    private void testConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testConnectionButtonActionPerformed
        //test the connection
        input1 = Main.server.getPassword1();
        input2 = Main.server.getPassword2();
        if (input1.length != input2.length || !Arrays.equals(input1, input2)) {
            Arrays.fill(input1, '0');
            jPassword1.setBackground(Color.RED);
            jPassword2.setBackground(Color.RED);
            jPassword1.requestFocus();
            Audio.play("Ring");
            return;
        }
        jPassword1.setBackground(Color.WHITE);
        jPassword2.setBackground(Color.WHITE);
        password = new String(input1);
        serverName = jServerName.getText();
        database = jDatabase.getText();
        userName = (String) jUserName.getSelectedItem();
        if (!DatabaseCreate.connectionQuery()) {//returns true if till found
//            testConnectionButton.setEnabled(false);
            CSH.setHelpIDString(getRootPane(), "xampp");
            Main.csh.actionPerformed(evt);
            CSH.setHelpIDString(getRootPane(), "firststeps");
            Audio.play("Ring");
            return;
        }
        if (!DatabaseCreate.databaseQuery()) {
            testConnectionButton.setEnabled(false);
            createDatabaseButton.setEnabled(true);
            getRootPane().setDefaultButton(createDatabaseButton);
            Audio.play("Beep");
            return;
        }
        Audio.play("Beep");//need to let know passwords match
//        okBtn.setEnabled(true);
        Main.settingsTab.setOkBtn(true);
        jPassword1.setBackground(Color.WHITE);
        jPassword2.setBackground(Color.WHITE);
        testConnectionButton.setEnabled(false);
}//GEN-LAST:event_testConnectionButtonActionPerformed

    private void createDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createDatabaseButtonActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Main.shop.setShiftFloat(0);
        //create the database
        if (DatabaseCreate.createDatabase()) {
            Audio.play("Beep");
            createDatabaseButton.setText(bundle.getString("Created"));
            createDatabaseButton.setEnabled(false);
            createTablesButton.setEnabled(true);
            getRootPane().setDefaultButton(createTablesButton);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_createDatabaseButtonActionPerformed

    private void createTablesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createTablesButtonActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (DatabaseCreate.createTables()) {
            createTablesButton.setEnabled(false);
            createTablesButton.setText(bundle.getString("Created"));
            Audio.play("Beep");
            Main.settingsTab.setOkBtn(true);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_createTablesButtonActionPerformed

private void dropboxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropboxButtonActionPerformed
        Main.salesScreen.openURL("http://db.tt/UIS1zPah");
}//GEN-LAST:event_dropboxButtonActionPerformed

    private void locateDropboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locateDropboxActionPerformed
        // locate the Dropbox folder
        chooser.setCurrentDirectory(new java.io.File("."));
        String choosertitle = null;
        chooser.setDialogTitle("Select Dropbox folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
         if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
             dropboxDirectory=chooser.getSelectedFile();
             dropboxLocation.setText(getDropboxDirectory().toString());
             dropboxDirectoryAsString=getDropboxDirectory().toString();
          }
        else {
            dropboxDirectory=null;
            dropboxLocation.setText("");
        }
    }//GEN-LAST:event_locateDropboxActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        input1 = Main.server.getPassword1();
        input2 = Main.server.getPassword2();
        if (input1.length != input2.length || !Arrays.equals(input1, input2)) {
            Arrays.fill(input1, '0');
            jPassword1.setBackground(Color.RED);
            jPassword2.setBackground(Color.RED);
            jPassword1.requestFocus();
            Audio.play("Ring");
            return;
        }
        jPassword1.setBackground(Color.WHITE);
        jPassword2.setBackground(Color.WHITE);
        password = new String(input1);
        root.put("Password", password);
        internetEnabled = internetCheckbox.isSelected();
        root.putBoolean("InternetEnabled", internetEnabled);
        dropboxDirectoryAsString=dropboxLocation.getText();
        root.put("dropboxDirectory",dropboxDirectoryAsString);
        serverName=jServerName.getText();
        root.put("ServerName",jServerName.getText());
        database=jDatabase.getText();
        root.put("Database",jDatabase.getText());
        userName=(String) jUserName.getSelectedItem();
        root.put("UserName", (String) jUserName.getSelectedItem());        
        if(connectionQuery()){
            Audio.play("Beep");
            connectionLabel.setText("Connected");
            password = new String(input1);
            root.put("Password", password);
            internetEnabled = internetCheckbox.isSelected();
            root.putBoolean("InternetEnabled", internetEnabled);
            dropboxDirectoryAsString=dropboxLocation.getText();
            root.put("dropboxDirectory",dropboxDirectoryAsString);
            serverName=jServerName.getText();
            root.put("ServerName",jServerName.getText());
            database=jDatabase.getText();
            root.put("Database",jDatabase.getText());
            userName=(String) jUserName.getSelectedItem();
            root.put("UserName", (String) jUserName.getSelectedItem());
        } else {
            connectionLabel.setText("Not onnected");
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    private void jLabel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLabel1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel1KeyPressed

    private void jUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUserNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jUserNameActionPerformed

    public char[] getPassword1() {
        return jPassword1.getPassword();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel connectionLabel;
    private javax.swing.JButton createDatabaseButton;
    private javax.swing.JButton createTablesButton;
    private javax.swing.JButton dropboxButton;
    private javax.swing.JTextField dropboxLocation;
    private javax.swing.JCheckBox internetCheckbox;
    private javax.swing.JTextField jDatabase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPassword1;
    private javax.swing.JPasswordField jPassword2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jServerName;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JComboBox jUserName;
    private javax.swing.JButton locateDropbox;
    private javax.swing.JButton testConnectionButton;
    // End of variables declaration//GEN-END:variables

    char[] getPassword2() {
        return jPassword2.getPassword();
    }

    String getPassword() {
        return password;
    }

    String getServerName() {
        serverName=root.get("ServerName", "localhost");
        return root.get("ServerName", "localhost");
    }

    String getDatabase() {
        return database = root.get("Database", "till");
    }

    String getUserName() {
        return root.get("UserName", "root");
    }

    void execute() {
        testConnectionButton.setEnabled(true);
        createDatabaseButton.setEnabled(false);
        createTablesButton.setEnabled(false);
        serverName = root.get("ServerName", "localhost");
        jServerName.setText(serverName);
        database = root.get("Database", "till");
        jDatabase.setText(database);
        String t = root.get("Password", "");
        jPassword1.setText(root.get("Password", ""));
        jPassword1.setBackground(Color.white);
        jPassword2.setText(root.get("Password", ""));
        jPassword2.setBackground(Color.white);
        String s = StringOps.numericOnly(root.get("TillId", "1"));
        internetEnabled = root.getBoolean("InternetEnabled", false);
        internetCheckbox.setSelected(internetEnabled);
        backupUser = root.get("backupUser", "");
        backupPassword = root.get("backupPassword", "");
        dropboxDirectoryAsString = root.get("dropboxDirectory","");
        dropboxLocation.setText(dropboxDirectoryAsString);
        connectButton.setName("Connect"); 
        jUserName.removeAllItems();
        jUserName.addItem(userName);
    }

    public boolean ok() {
        input1 = Main.server.getPassword1();
        input2 = Main.server.getPassword2();
        if (input1.length != input2.length || !Arrays.equals(input1, input2)) {
            Arrays.fill(input1, '0');
            jPassword1.setBackground(Color.RED);
            jPassword2.setBackground(Color.RED);
            jPassword1.requestFocus();
            Audio.play("Ring");
            return false;
        }
        jPassword1.setBackground(Color.WHITE);
        jPassword2.setBackground(Color.WHITE);
        password = new String(input1);
        root.put("Password", password);
        root.putBoolean("InternetEnabled", internetEnabled);
        dropboxDirectoryAsString=dropboxLocation.getText();
        root.put("dropboxDirectory",dropboxDirectoryAsString);
        serverName=jServerName.getText();
        root.put("ServerName",jServerName.getText());
        database=jDatabase.getText();
        root.put("Database",jDatabase.getText());
        userName=(String) jUserName.getSelectedItem();
        root.put("UserName", (String) jUserName.getSelectedItem());
        if (!DatabaseCreate.connectionQuery()) {
            return false;
        }
        if (!DatabaseCreate.databaseQuery()) {
            return false;
        }
        if (!DatabaseCreate.tablesQuery()) {
            //need to create tables
            if (DatabaseCreate.createTables()) {
                return false;
            }
        }
        root.put("ServerName", jServerName.getText());
        root.put("Database", jDatabase.getText());
        root.put("UserName", (String) jUserName.getSelectedItem());
        root.put("Password", password);
        internetEnabled = internetCheckbox.isSelected();
        root.putBoolean("InternetEnabled", internetEnabled);
        root.put("dropboxDirectory",dropboxDirectoryAsString);
        return true;
    }

    /**
     * @return the dropboxDirectory
     */
    File getDropboxDirectory() {
        return dropboxDirectory;
    }
}
