/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Dave
 */
 public class ReportRunner implements Runnable {
    private String fileLocation;
    private JasperPrint jasperPrint;
    private String jasperFile;
    private Map<String, Object> parameters;
    private Connection connection;
    public static boolean done=false;

    public void run() {
        execute();
        done=true;
    }
    
    public void setup(String jasperFile, Map<String, Object> parameters, Connection connection){
        this.jasperFile = jasperFile;
        this.parameters = parameters;
        this.connection =connection;
        Main.salesScreen.greyReportsMenu(false);
    }
    
    public void setup(String jasperFile, Map<String, Object> parameters){
        this.jasperFile = jasperFile;
        this.parameters = parameters;
        this.connection =(Connection) parameters.get("Connection");
        if(this.connection==null){
            this.connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", this.connection);
        }
        Main.salesScreen.greyReportsMenu(false);
    }
    
    private void execute(){
        File fc=Main.salesScreen.getJasper(jasperFile);
        
            try {
                fileLocation = fc.getAbsolutePath();
                jasperPrint = JasperFillManager.fillReport(fileLocation, parameters, connection);
                JasperViewer.viewReport(jasperPrint, false);
                Main.salesScreen.greyReportsMenu(true);
            } catch (JRException ex) {
                Logger.getLogger(ReportRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
}
