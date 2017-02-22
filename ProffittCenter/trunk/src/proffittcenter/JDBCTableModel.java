/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.table.*;
import java.sql.*;
import java.util.*;
import javax.swing.JTable;

/** an immutable table model built from getting
metadata about a table in a jdbc database
 */
public final class JDBCTableModel extends AbstractTableModel {

    Object[][] contents;
    String[] columnNames;
    Class[] columnClasses;
    private String s;
    private ArrayList colWidths = new ArrayList();
    JTable table;
    private int noOfColumns;
    private String t;
    private String[] colDescriptions;
    private static final int MONEY = 500;
    private HashSet editable;
    private boolean isPerCent;
    private boolean isMoney;
    private static final int PERCENT = 501;
    private Component comp;

    public JDBCTableModel(
        PreparedStatement ps,
        ResourceBundle bundle,
        JTable jTable1,
        HashSet editable)
        throws SQLException {
        super();
        table = jTable1;
        getTableContents(ps, bundle, jTable1, editable);
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.editable = editable;
    }

    public void setColumnWidths() {
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn column = columnModel.getColumn(0);
        int maxColWidthSum = 0;
        for (int col = 0; col < noOfColumns; col++) {
            int colWidth = 0;//max col width in pixels
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer rend = table.getCellRenderer(row, col);
                Object value = table.getValueAt(row, col);
                String q = "";
                if (getColumnClass(col) == Money.class) {
                    q = (String) value.toString();
                    comp = rend.getTableCellRendererComponent(table, q + "   ", false, false, row, col);
                } else if (getColumnClass(col) == PerCent.class) {
                    q = (String) value.toString();
                    comp = rend.getTableCellRendererComponent(table, q + "   ", false, false, row, col);
//                } else if (getColumnClass(col) == java.sql.Timestamp.class){
//                    q = (String) value;
//                    comp = rend.getTableCellRendererComponent(table, q + "   ", false, false, row, col);
                } else {
                    comp = rend.getTableCellRendererComponent(table, value, false, false, row, col);
                }
                int entryWidth = comp.getPreferredSize().width+10;
                colWidth = Math.max(entryWidth, colWidth);
            }
            column = columnModel.getColumn(col);
            TableCellRenderer headerRenderer = column.getHeaderRenderer();
            if (headerRenderer == null) {
                headerRenderer = table.getTableHeader().getDefaultRenderer();
            }
            Object headerValue = column.getHeaderValue();
            Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0, col);
            int xx = headerComp.getPreferredSize().width;
            colWidth = Math.max(headerComp.getPreferredSize().width, colWidth);
            if (col + 1 == noOfColumns) {
                int jjj = table.getWidth();
                if(jjj-maxColWidthSum>colWidth){
                    colWidth = table.getWidth() - maxColWidthSum - 3;
                } else {
                    Dimension xy=new Dimension();
                    xy.setSize(jjj-maxColWidthSum+colWidth,table.getHeight());
                    table.setMaximumSize(xy);
                }
            }
            maxColWidthSum += colWidth;
            if (col == 0) {
                column.setPreferredWidth(colWidth + 10);
            } else {
                column.setPreferredWidth(colWidth);
            }
        }
    }

    /**
     * 
     * @param ps a preparedStatement that delivers named fields
     * use SELECT field1 AS MyField, etc
     * @param bundle points to property file that translates MyField etc.
     * @param jTable1
     * @param editable true for editable fields
     * @throws SQLException
     */
    protected void getTableContents(
            PreparedStatement ps,
            ResourceBundle bundle,
            JTable jTable1,
            HashSet editable)
            throws SQLException {
        table = jTable1;
        this.editable = editable;
        // Execute the query
        ResultSet results = ps.executeQuery();
        // Get the metadata
        ResultSetMetaData meta = results.getMetaData();
        colWidths.clear();
        noOfColumns = meta.getColumnCount();
        ArrayList colNamesList = new ArrayList();
        ArrayList colDescriptionList = new ArrayList();
        ArrayList colClassesList = new ArrayList();
        for (int j = 1; j <= noOfColumns; j++) {
            s = meta.getColumnLabel(j);
            if (bundle != null) {
                t = bundle.getString(s);
                isMoney = t.contains("£");
                isPerCent = t.contains("%");
                t = t.replace("£", Main.shop.poundSymbol);
            } else {
                t = s;
                isMoney = false;
                isPerCent = false;
            }
            colNamesList.add(s);
            colDescriptionList.add(t);
            int width = Math.max(meta.getColumnDisplaySize(j), t.length());
            int dbType = meta.getColumnType(j);
            if (isMoney) {
                dbType = MONEY;
            } else if (isPerCent) {//not both together
                dbType = PERCENT;
            }
            switch (dbType) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                    colClassesList.add(Integer.class);
                    width = Math.max(width,100);
                    break;
                case Types.FLOAT:
                    colClassesList.add(Float.class);
                    width = Math.max(width,150);
                    break;
                case Types.DOUBLE:
                case Types.REAL:
                    colClassesList.add(Double.class);
                    width = Math.max(width,150);
                    break;
                case Types.TIMESTAMP:
                    colClassesList.add(String.class);
                    width = Math.max(width,520);
                    break;
                case Types.DATE:
                    colClassesList.add(java.sql.Date.class);
                    width = Math.max(width,120);
                    break;
                case Types.TIME:
                    colClassesList.add(java.sql.Time.class);
                    break;
                case Types.BIGINT:
                    colClassesList.add(Long.class);
                    width = Math.max(width,120);
                    break;
                case MONEY:
                    colClassesList.add(Money.class);
                    width = Math.max(width,50);
                    break;
                case PERCENT:
                    colClassesList.add(PerCent.class);
                    width = Math.max(width,50);
                    break;
                case Types.BIT: //detects TINYINT(1)
                    colClassesList.add(Boolean.class);
                    width = Math.max(width,40);
                    break;
                default:
                    colClassesList.add(String.class);
                    width = Math.max(width,200);
                    break;
            }
            colWidths.add(width);
        }
        columnNames = new String[colNamesList.size()];
        colNamesList.toArray(columnNames);
        colDescriptions = new String[colDescriptionList.size()];
        colDescriptionList.toArray(colDescriptions);
        columnClasses = new Class[colClassesList.size()];
        colClassesList.toArray(columnClasses);
        // get all data from table and put into
        // contents array
        results.close();
        results = ps.executeQuery();
        ArrayList rowList = new ArrayList();
        while (results.next()) {
            ArrayList cellList = new ArrayList();
            for (int i = 0; i < columnClasses.length; i++) {
                Object cellValue = null;
                if (columnClasses[i] == String.class) {
                    cellValue = results.getString(columnNames[i]);
                } else if (columnClasses[i] == Integer.class) {
                    cellValue = new Integer(
                            results.getInt(columnNames[i]));
                } else if (columnClasses[i] == Float.class) {
                    cellValue = new Float(
                            results.getInt(columnNames[i]));
                } else if (columnClasses[i] == Double.class) {
                    cellValue = new Double(
                            results.getDouble(columnNames[i]));
                } else if (columnClasses[i] == java.sql.Timestamp.class) {
                    cellValue = results.getString(columnNames[i]);
                } else if (columnClasses[i] == java.sql.Date.class) {
                    cellValue = results.getDate(columnNames[i]);
                }    else if (columnClasses[i] == java.sql.Time.class) {
                    cellValue = results.getTime(columnNames[i]);
                } else if (columnClasses[i] == Long.class) {
                    cellValue = results.getLong(columnNames[i]);
                } else if (columnClasses[i] == Money.class) {
                    cellValue = new Money(results.getInt(columnNames[i]));
                } else if (columnClasses[i] == PerCent.class) {
                    cellValue=new PerCent(results.getInt(columnNames[i]));
                } else if (columnClasses[i] == Boolean.class) {
                    cellValue = results.getBoolean(columnNames[i]);
                } else {
                    System.out.println("Can't assign "
                            + columnNames[i]);
                }
                cellList.add(cellValue);
            }// for
            Object[] cells = cellList.toArray();
            rowList.add(cells);

        } // while
        // finally create contents two-dim array
        contents = new Object[rowList.size()][];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = (Object[]) rowList.get(i);
        }
        // close stuff
        results.close();
        table.setModel(this);
        this.setColumnWidths();
        table.setAutoCreateRowSorter(true);
    }

    public void removeRow(int row){
        this.removeRow(row);
    }

    // AbstractTableModel methods
    public int getRowCount() {
        return contents.length;
    }

    public int getColumnCount() {
        if (contents.length == 0) {
            return columnClasses.length;
        } else {
            return contents[0].length;
        }
    }

    public Object getValueAt(int row, int column) {
         return contents[row][column];
    }

    // overrides methods for which AbstractTableModel
    // has trivial implementations
    @Override
    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    @Override
    public String getColumnName(int col) {
        return colDescriptions[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        Object value=null;
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return editable.contains(col);

    }

    public void setValueAt(Object value, int row, int col) {
        contents[row][col] = value;
        fireTableCellUpdated(row, col);
    }
    
}
