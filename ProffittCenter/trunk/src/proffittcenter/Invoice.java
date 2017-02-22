/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

/**
 *
 * @author HP_Owner
 */
public class Invoice {

    private int total;
    static String userHome = System.getProperty("user.home");
    private static String deliveryAddress = "";
    private static int discount;
    private JasperPrint jasperPrint;

    public void print(long sale, int discount, boolean isCopy) {
        if (!Main.hardware.isInvoicePrinter()&&!SalesScreenFunctions.invoiceRequired) {
            return;
        }
        String report;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            if (isCopy) {
                params.put("isCopy", true);
            } else {
                params.put("isCopy", false);
            }
            String sales_ID = Long.toString(sale);
            params.put("sales_ID", sales_ID);
            params.put("discount", "" + discount);
            if (Main.shop.isJasperInMyDocuments()) {
                params.put("columnHeader", true);
                File fc = Main.salesScreen.getJasper("Invoice");
                report = fc.getAbsolutePath();
                jasperPrint = JasperFillManager.fillReport(report, params, Main.getConnection());//log4j
//                JasperViewer.viewReport(jasperPrint, true);
            } else {
                params.put("columnHeader", false);
                report= userHome + "/Reports/templates/Invoice.jasper";
                jasperPrint = JasperFillManager.fillReport(report,
                        params, Main.getConnection());
            }
            Main.closeConnection();
            JasperPrintManager.printReport(jasperPrint,false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Exception: " + ex, "Invoice", JOptionPane.ERROR_MESSAGE);
            String connectMsg = "Could not create the invoice report " + ex.getMessage() + " " + ex.getLocalizedMessage();
            Main.logger.log(Level.SEVERE, "Invoice ", "Exception: " + connectMsg);
        }
    }
}

