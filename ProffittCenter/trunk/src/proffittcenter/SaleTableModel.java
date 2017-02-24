package proffittcenter;

import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author David Proffitt
 */
public class SaleTableModel extends AbstractTableModel {

    private static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/SaleTableModel");
    private String[] columnNames = {bundle.getString("Qty"),
        " "+bundle.getString("Description"),
        bundle.getString("Price") + " (" + Main.shop.poundSymbol+")",
        bundle.getString("Charge") + " (" + Main.shop.poundSymbol +")"};
    private LineList lines;
    NumberFormat nf = NumberFormat.getInstance();
    private int encode;
    private int p;
    private int qty;
    private int price;

    /** Creates a new instance of SaleTableModel
     * @param sale the current LineList
     */
    public SaleTableModel(LineList sale) {
        lines = sale;
    }

    public void add(Line line) {
        lines.add(line);
        fireTableDataChanged();
    }

    public void clear() {
        lines.clear();
        fireTableDataChanged();
    }

    public void deleteSelection() {
        lines.deleteSelectedLine();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if (lines == null) {
            return 0;//added to avoid problems
        }
        return lines.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        Line line = lines.getValueAt(row);
        encode= line.getEncode();
        qty = line.getQuantity();
        if (line == null) {
            return null;
        }
        if (column == 0) {//quantity
            return line.getQuantity();
        } else if (column == 1) {//product description
            return line.getDescription();
        } else if (column == 2) {//original price
            if(line.getOriginalPrice()==line.getRetailPrice()){
                return nf.format((new Double(line.getOriginalPrice())) / 100);
            } else{
                return "*"+nf.format((new Double(line.getOriginalPrice())) / 100);
            }
        } else {                //charge column
            if(encode==NewProduct.NOTENCODE){
                p = line.getRetailPrice() * qty;
                return nf.format(((new Double(p))) / 100);
            } if(encode==NewProduct.ENCODEBYPRICEPARITY 
                    || encode==NewProduct.ENCODEBYPRICENOPARITY
                    || encode==NewProduct.ENCODEBYPRICE5){
                 p = line.getRetailPrice() * qty;
                return nf.format(((new Double(p))) / 100);
            } else {
                qty = line.getQuantity();
                price = line.getRetailPrice();
                p = (price * qty);
                if(p>0){
                    p=(p+500)/1000;
                }else {
                    p=(p-500)/1000;
                }
                return nf.format(((new Double(p))) / 100);
            }
        }
    }

    @Override
    public void setValueAt(Object ob, int row, int column) {
        Line line = lines.getValueAt(row);
        int t = lines.getTotal();
        int p = line.getRetailPrice();
        int q = line.getQuantity();
        if (column == 0) {
//            if(line.getEncode()==NewProduct.ENCODEDBYWEIGHT){
//                double d = (Integer)ob;//convert
//                d /= 1000.0;
//                NumberFormat formatter = new DecimalFormat("0.000");
//            } else {
                line.setQuantity((Integer) ob);
//            }
        } else if (column == 2) {
            line.setRetailPrice((Integer) ob);
        }
       fireTableRowsUpdated(row, row);
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public int getItemsSold() {
        int itemsSold=0;
        for (int i=0;i<getRowCount();i++){
            itemsSold+=(Integer)getValueAt(i,0);
        }
        return itemsSold;
    }

    @Override
    public String getColumnName(int column) {

        return columnNames[column];
    }
}
