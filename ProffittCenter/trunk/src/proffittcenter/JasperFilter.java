/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proffittcenter;

import java.io.File;
/**
 *
 * @author Dave Proffitt
 */
public class JasperFilter extends javax.swing.filechooser.FileFilter{

public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(".jasper")||filename.endsWith(".jrxml")||file.isDirectory();
        }
    @Override
        public String getDescription() {
            return "*.jasper,*.jrxml";
        }
}


