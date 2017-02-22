/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JFileChooser;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

/**
 *
 * @author HP_Owner
 */
public class BarcodePrinter {
static JasperPrint jasperPrint;
static Map<String, Object> parameters = new HashMap<String, Object>();
    private static Connection con;
    //hardware driver for a barcode printer
    public static boolean print(Long barcode, String description) {
        JRPrintServiceExporter exporter = null;
        exporter = new JRPrintServiceExporter();
        try {
            String userHome = System.getProperty("user.home");
            parameters.put("Barcode", barcode.toString());
            parameters.put("Description", description);
            con = Main.getConnection();
            parameters.put("REPORT_CONNECTION", con);
            PrinterJob job = PrinterJob.getPrinterJob();
            PrintService[] services;
            int selectedService = Main.hardware.getBarcodePrinterIndex();
            if (Main.shop.isJasperInMyDocuments()) {
                File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
                File fc = new File(Main.salesScreen.getDefaultDirectory() + "ProffittCenterReports/CustomerBarcode.jasper");
                jasperPrint = JasperFillManager.fillReport(fc.getAbsolutePath(), parameters, Main.getConnection());
                JasperPrintManager.printReport(jasperPrint, true);
            } else if (selectedService != -1) {
                services = PrintServiceLookup.lookupPrintServices(null, null);
                job.setPrintService(services[selectedService]);
                PrintServiceAttributeSet printRequestAttributeSet = new HashPrintServiceAttributeSet();
                if(Main.hardware.getBarcodePrinter().isEmpty()){
                    return false;
                }
                printRequestAttributeSet.add(new PrinterName(Main.hardware.getBarcodePrinter(), null));
                jasperPrint = JasperFillManager.fillReport(Main.salesScreen.getDefaultDirectory() +  "/Reports/templates/Barcode.jasper",
                        parameters, Main.getConnection());
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);//, services[selectedService]);
                exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, services[selectedService]);
                exporter.exportReport();
            }
        } catch (PrinterException ex) {
            Logger.getLogger(BarcodePrinter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (JRException ex) {
            Logger.getLogger(BarcodePrinter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    static void printCodes() {
        //print codes on the default printer using Barcodes.jasper in my Documents
        if (Main.shop.isJasperInMyDocuments()) {
            try {
                File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
                File fc = new File(Main.salesScreen.getDefaultDirectory() + "ProffittCenterReports/Barcodes.jasper");
                con = Main.getConnection();
                Long barcode=0L;
            parameters.put("Barcode", barcode.toString());
            String s=fc.getAbsolutePath();
                jasperPrint = JasperFillManager.fillReport(fc.getAbsolutePath(), parameters, con);
                //                    JasperViewer.viewReport(jasperPrint);
                JasperPrintManager.printReport(jasperPrint, true);
            } catch (JRException ex) {
                Logger.getLogger(BarcodePrinter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private BarcodePrinter() {
    }
}

