/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dave
 */
public class ResultsModel extends AbstractTableModel {
    Vector tableHeader = new Vector();
    
       ResourceBundle bundle1 = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/ResultsModel");
    @Override
        public String getColumnName(int col) {
            return "";
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            if(Main.salesScreen==null){
                return 0;
            }
            int i = Main.salesScreen.resultsData.size();
            return Main.salesScreen.resultsData.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == 0) {//customer
                if (columnIndex == 0) {
                    long c = Main.salesScreenFunctions.getCustomer();
                    return (Main.salesScreenFunctions.getCustomer() == SaleType.CUSTOMER.code() * 10000) ? "" : Main.salesScreenFunctions.getFirstName();
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getCustomer() == SaleType.CUSTOMER.code() * 10000) ? "" : Main.salesScreenFunctions.getSurname();
                } else {
                    return null;
                }
            } else if (rowIndex == 1) {//total
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getTotal() == 0) ? "" : bundle1.getString("Total");
                } else if (columnIndex == 1) {
                    
                    return (Main.salesScreenFunctions.getTotal() == 0) ? "" : Main.salesScreenFunctions.getTotal();
                } else {
                    return null;
                }
            } else if (rowIndex == 2) {//cash
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getCash() == 0) ? "" : bundle1.getString("Cash");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getCash() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getCash());
                } else {
                    return null;
                }
            } else if (rowIndex == 3) {//debit
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getDebit() == 0 && !Main.hardware.touch) ? "" : bundle1.getString("Debit");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getDebit() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getDebit());
                } else {
                    return null;
                }
            } else if (rowIndex == 4) {//coupon
                int c = Main.salesScreenFunctions.getCoupon();
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getCoupon() == 0 && !Main.hardware.touch) ? "" : bundle1.getString("Coupon");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getCoupon() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getCoupon());
                } else {
                    return null;
                }
            } else if (rowIndex == 5) {//cheque
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getCheque() == 0 && !Main.hardware.touch) ? "" : bundle1.getString("Cheque");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getCheque() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getCheque());
                } else {
                    return null;
                }
            } else if (rowIndex == 6) {//savings
                //savings = aSale.getSavings();
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getSavings() == 0) ? "" : bundle1.getString("Savings");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getSavings() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getSavings());
                } else {
                    return null;
                }
            } else if (rowIndex == 7) {//tax
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getTax1() == 0) ? "" : bundle1.getString("Tax");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getTax1() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getTax1());
                } else {
                    return null;
                }
            } else if (rowIndex == 8) {//tax2
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getTax2() == 0) ? "" : bundle1.getString("Tax2");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getTax2() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getTax2());
                } else {
                    return null;
                }
            } else if (rowIndex == 9) {//toPay swapped with cheque
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getToPay() <= 0) ? "" : bundle1.getString("To_pay");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getToPay() <= 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getToPay());
                } else {
                    return null;
                }
            } else if (rowIndex == 10) {//discount
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getDiscountTotal() == 0) ? "" : bundle1.getString("CustomerDiscount") + ": ("
                            + Main.salesScreen.getDiscount() + "%)";
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getDiscountTotal() == 0) ? "" : StringOps.asPounds(Main.salesScreenFunctions.getDiscountTotal());
                } else {
                    return null;
                }
            } else if (rowIndex == 11) {//status
                if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getStatus().length() == 0) ? "" : bundle1.getString("Status");
                } else if (columnIndex == 1) {
                    return (Main.salesScreenFunctions.getStatus().length() == 0) ? "" : Main.salesScreenFunctions.getStatus();
                } else {
                    return null;
                }
            } else if (rowIndex == 12) {//change
                if (Main.salesScreenFunctions.getStatus().isEmpty() || Main.salesScreenFunctions.getStatus().equalsIgnoreCase(bundle1.getString("Balance"))) {
                    return "";
                } else if (columnIndex == 0) {
                    return (Main.salesScreenFunctions.getChange() == 0) ? "<html>" + bundle1.getString("Change") + " </html>" : "<html>" + bundle1.getString("Change") + "</html>";
                } else if (columnIndex == 1) {
                    if (Main.salesScreenFunctions.getStatus().isEmpty()) {
                        return "";
                    } else if (Main.salesScreenFunctions.getChange() == 0) {//this did not work without font size
                        return "<html><bgcolor=\"lime\"><font size=+1>" + bundle1.getString("No_change");
                    } else {
                        return "<html><bgcolor=\"lime\"><font size=+1>" + StringOps.asPounds(Main.salesScreenFunctions.getChange()) + "</html>";
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

}
