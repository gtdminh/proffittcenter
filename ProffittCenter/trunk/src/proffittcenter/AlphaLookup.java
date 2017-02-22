/*
 * AlphaLookup.java
 *
 * Created on 21 December 2006, 11:32
 */
package proffittcenter;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * AlphaLookup is used to find products by entering a string of 3 or more
 * character marking the initial characters of any word in the product
 * description
 * @author  David Proffitt
 */
public class AlphaLookup extends EscapeDialog {

    Preferences root = Preferences.userNodeForPackage(getClass());
    private static final ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/AlphaLookup");
    Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    boolean b0 = tableHeader.add(bundle.getString("Bar_code"));
    @SuppressWarnings("unchecked")
    boolean b1 = tableHeader.add(bundle.getString("Description"));
    @SuppressWarnings("unchecked")
    boolean b5 = tableHeader.add(bundle.getString("SKU"));
    @SuppressWarnings("unchecked")
    boolean b2 = tableHeader.add(bundle.getString("Price") + " " + Main.shop.poundSymbol);
    @SuppressWarnings("unchecked")
    boolean b3 = tableHeader.add(bundle.getString("Quantity"));
    @SuppressWarnings("unchecked")
    boolean b4 = tableHeader.add("n");
    String theData;
    String s;
    Long barcode;
    String description;
    Integer quantity;
    Integer price;
    Integer n;
    String dataLocal;
    private Integer sku;
    Font plain = new Font("SansSerif", Font.PLAIN, 24);
    MyHeaderRenderer mhr = null;
    MyTableCellRenderer mtcr = null;
    int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT,SwingConstants.CENTER};
    Object[] line = {barcode, description,sku, price, quantity,n};
    Vector<Object[]> rowData = new Vector<Object[]>();
    NumberFormat nf = NumberFormat.getInstance();
    AbstractTableModel model = new AbstractTableModel() {

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
                return line[0];
            } else if (columnIndex == 1) {
                return line[1];
            } else if (columnIndex == 2) {
                return line[2];
            } else if (columnIndex == 3) {//the price
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer)line[3])) / 100);
            } else if (columnIndex == 4) {
                return line[4];
            } else if (columnIndex == 5) {
                if((Integer)line[5]>9){
                    return "";
                } else {
                    return ""+(Integer)line[5];
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
    private String returnData;
    private PreparedStatement alpha;
    private Integer lastSku;
    
    private class AlphaRenderer extends DefaultTableCellRenderer {
        
    }

    /**
     * 
     * @param data contains three or more characters
     * @return String - the barcode of the selected product
     * or data.toUpperCase()
     */

    private boolean isAlpha(String data){
        if (data.length() != 3) {
            return false;
        }
        dataLocal=data;
        theData = data.toUpperCase();
        char c0 = theData.charAt(0);
        char c1 = theData.charAt(1);
        char c2 = theData.charAt(2);
        boolean isBarcode = Character.isDigit(c0)&&Character.isDigit(c1);//&&Character.isDigit(c2);
        boolean isHotkey = Character.isLetter(c0) && Character.isDigit(c1)&&Character.isDigit(c2);
        boolean isProposedHotkey = Character.isLetter(c0) && Character.isLetter(c1) && Character.isDigit(c2) ;
        boolean isTimesN = c0=='.'||c0=='*'&&Character.isDigit(c1);
        boolean isMinus = c0=='-';
        boolean isDiscount = Character.isDigit(c0)&&Character.isDigit(c1)&&(c2=='%'||c1=='/')
                ||Character.isDigit(c0)&&(c1=='%'||c1=='/');
        boolean isPriceOver = c0=='@'||c0=='=';
        return !isProposedHotkey && !isDiscount && !isMinus && !isTimesN 
                && !isBarcode && !isHotkey && !isPriceOver;
    }

    public boolean isBarcode(String data){
        return !isAlpha(data);
    }

    public boolean isFound(String data){
        //true if item selected; get results with returnDataIs()
        if(!isAlpha(data)){
            returnData=data;
            return false;
        }
        barcode = 0L;
        description = "";
        quantity = 0;
        price = 0;
            drawGrid();
        if (rowData.isEmpty()) {
            returnData=StringOps.numericOnly(data);
            return false;
        }
        dataEntry.setText(theData);
        theData="";
        returnData="";
        Audio.play("Tada");
        //FormRestore.createPosition(this, root);
        returnData=StringOps.numericOnly(data);
        setVisible(true);
        //FormRestore.destroyPosition(this, root);
        if(returnData.length()>=7){
            return true;
        } else {
            returnData=StringOps.numericOnly(data);
            return false;
        }
    }

    public String returnDataIs(){
        return returnData;
    }

    private boolean drawGrid() {
        try {
            alpha = Main.getConnection().prepareStatement(SQL.alpha);
            theData=theData.trim();
            String s1=theData +"%";
            String s2="% "+ theData +"%";
            alpha.setString(1, s1);
            alpha.setString(2, s2 );
            int k=SkuType.STOP.value();
            alpha.setShort(3, SkuType.STOP.value());//exclude stopped (6)
            ResultSet rs = alpha.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            n=0;//the line number
            lastSku=0;
            while (rs.next()) {//products to display
                barcode = rs.getLong("ID");
                description = rs.getString("Description");
                price = rs.getInt("Price");
                quantity = rs.getInt("Quantity");
                sku= rs.getInt("Sku");
                if(sku.compareTo(lastSku)==0){
                   description=" " + description; 
                }
                lastSku=sku;
                Object[] line1 = {barcode, description, sku, price, quantity,n};
                rowData.add(line1);
                n++;
            }
            rs.close();
            alpha.close();
            if (rowData.size() > 0) {//highlight first line
                jTable1.changeSelection(0, -1, false, false);
            }
            count.setText(""+n);
            model.fireTableDataChanged();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Creates new form AlphaLookup
     * @param parent
     * @param modal
     */
    public AlphaLookup(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(310);//quantity column
        tc.getColumn(1).setPreferredWidth(800);
        tc.getColumn(2).setPreferredWidth(150);//quantity column
        tc.getColumn(3).setPreferredWidth(150);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        mtcr.setFont(plain);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setDefaultRenderer(Long.class, mtcr);
        jTable1.setDefaultRenderer(Double.class, mtcr);
        JRootPane rp = getRootPane();
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Alphalookup", Main.mainHelpSet);
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataEntry = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        count = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/AlphaLookup"); // NOI18N
        setTitle(bundle.getString("AlphaLookup.title")); // NOI18N
        setForeground(java.awt.Color.white);
        setName("AlphaLookup"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        dataEntry.setBackground(java.awt.Color.lightGray);
        dataEntry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                dataEntryFocusLost(evt);
            }
        });
        dataEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataEntryKeyReleased(evt);
            }
        });

        jScrollPane1.setInheritsPopupMenu(true);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title code", "Description", "SKU", "Price", "Quantity", "n"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1onSelectRow(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(dataEntry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 427, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(count, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dataEntry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(count, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dataEntryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataEntryKeyReleased
        //need to rescan if new letter added
        Character c = evt.getKeyChar();
        if(Character.isLetter(c)||Character.isSpaceChar(c)||c=='/'||c=='\''||c=='.'||c=='('||c==')'){
            dataLocal+=c;
        }
        dataLocal=dataLocal.toUpperCase();
        dataEntry.setText(dataLocal);
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            int sel = jTable1.getSelectedRow();
            if (sel == -1) {
                sel = 0;
            } else {
                sel++;
            }
            if (sel >= 0 && sel <= rowData.size() - 1) {
                //valid selection
                jTable1.changeSelection(sel, 0, false, false);
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            int sel = jTable1.getSelectedRow();
            if (sel == -1) {
                sel = 0;
            } else {
                sel+=10;
            }
            int kk = rowData.size() - 1;
            if (sel >= 0 && sel <= rowData.size() - 1) {
                //valid selection
                jTable1.changeSelection(sel, 0, false, false);
            } else {
                jTable1.changeSelection(kk, 0, false, false);
                sel=0;
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            int sel = jTable1.getSelectedRow();
            if (sel <= 0) {
                sel = 0;
            } else {
                sel--;
            }
            int kk = rowData.size() - 1;
            if (sel >= 0 && sel <= kk) {
                //valid selection
                jTable1.changeSelection(sel, 0, false, false);
            } 
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            int sel = jTable1.getSelectedRow();
            if (sel == -1) {
                sel = 0;
            } else {
                sel-=10;
            }
            if (sel >= 0 && sel <= rowData.size() - 1) {
                //valid selection
                jTable1.changeSelection(sel, 0, false, false);
            } else {
                jTable1.changeSelection(0, 0, false, false);
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int selection = jTable1.getSelectedRow();
            if (selection >= 0 && selection < rowData.size()) {
                //valid selection
                line = rowData.get(selection);
                s = line[0].toString();
                returnData = s;
                setVisible(false);
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            theData = "";
            setVisible(false);
        }else if (Character.isDigit(c)){
           int selection=Integer.parseInt(c.toString());
           if(selection<0||selection>=rowData.size()){
               Audio.play("Ring");
               return;
           }
           line = rowData.get(selection);
            s = line[0].toString();
            returnData = s;
            this.setVisible(true);
            dataEntry.requestFocus();
            setVisible(false);
        } else  {//a letter or a suitable char
//            theData = dataLocal;
            theData = dataEntry.getText();
            drawGrid();
            if (rowData.isEmpty()) {
                dataEntry.setText(theData);
                Audio.play("Ring");
                this.setVisible(false);
                return;
            }
            dataEntry.setText(theData);
//            AudioPlayer.play("Beep");
            //this.setVisible(true);
            dataEntry.requestFocus();
        }
    }//GEN-LAST:event_dataEntryKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selection = jTable1.getSelectedRow();
        if (selection >= 0 && selection < rowData.size()) {
            //valid selection
            line = rowData.get(selection);
            s = line[0].toString();
            returnData = s;
            setVisible(false);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1onSelectRow(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1onSelectRow
        int selection = jTable1.getSelectedRow();
        if (selection >= 0 && selection < rowData.size()) {
            //valid selection
            line = rowData.get(selection);
            s = line[0].toString();
            Main.salesScreen.setDataEntry(s);
            returnData=s;
            setVisible(false);
        }
    }//GEN-LAST:event_jTable1onSelectRow

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        theData = "";
        returnData="";
    }//GEN-LAST:event_formWindowClosing

    private void dataEntryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dataEntryFocusLost
            dataEntry.requestFocus();
    }//GEN-LAST:event_dataEntryFocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new AlphaLookup(new javax.swing.JFrame(),
                        true).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JTextField count;
    javax.swing.JTextField dataEntry;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
