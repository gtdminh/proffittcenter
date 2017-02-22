 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import gnu.io.*;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author HP_Owner
 */
public class Serial {

    private final String name;
    private String port;
    public static final String RECEIPT = "Receipt";
    public static final String POLE = "Pole";
    private static boolean same = false;
    private OutputStream fileStream = null;
    private BufferedWriter br = null;
    private gnu.io.SerialPort serialPort = null;
    public static final Serial RECEIPTPORT = new Serial(RECEIPT);
    public static final Serial POLEPORT = new Serial(POLE);

    private Serial(String name) {
        this.name = name;
        if (name.equalsIgnoreCase("Receipt")) {
            try {
                port = Main.hardware.getReceiptPort();
                if (!port.isEmpty()) {
                    try {
                        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
                        String s = this.getClass().getName();
                        gnu.io.CommPort commPort;
                        commPort = portIdentifier.open(this.getClass().getName(), 10000);
                        if (commPort instanceof gnu.io.SerialPort) {
                            serialPort = (gnu.io.SerialPort) commPort;
                            serialPort.setSerialPortParams(Main.hardware.getBaudRrate(), gnu.io.SerialPort.DATABITS_8, gnu.io.SerialPort.STOPBITS_1, gnu.io.SerialPort.PARITY_NONE);
                        }
                    } catch (PortInUseException ex) {
                        Main.logger.log(Level.SEVERE, "Serial:", "PortInUseException: " + ex.getMessage());
                        Main.hardware.setReceiptPort("");
                        JOptionPane.showMessageDialog(null, "PortInUseException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                    try {
                        fileStream = serialPort.getOutputStream();
                    } catch (IOException ex) {
                        Main.logger.log(Level.SEVERE, "Serial:", "IOException(5): " + ex.getMessage());
                        JOptionPane.showMessageDialog(null, "IOException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (UnsupportedCommOperationException ex) {
                Main.logger.log(Level.SEVERE, "Serial:", "UnsupportedCommOperationException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "UnsupportedCommOperationException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
            } catch (NoSuchPortException ex) {
                Main.logger.log(Level.SEVERE, "Serial:", "NoSuchPortException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "NoSuchPortException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
                Main.hardware.setReceiptPort("");
                System.exit(0);
            }
        } else if (name.equalsIgnoreCase("Pole")) {
            port = Main.hardware.getPolePort();
            if (!port.isEmpty()) {
                if (port.equalsIgnoreCase(RECEIPTPORT.port)) {
                    try {
                        fileStream = RECEIPTPORT.serialPort.getOutputStream();
                    } catch (IOException ex) {
                        Logger.getLogger(Serial.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    same = true;
                } else {
                    try {
                        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
                        gnu.io.CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
                        if (commPort instanceof gnu.io.SerialPort) {
                            serialPort = (gnu.io.SerialPort) commPort;
                            serialPort.setSerialPortParams(Main.hardware.getBaudRrate(), gnu.io.SerialPort.DATABITS_8, gnu.io.SerialPort.STOPBITS_1, gnu.io.SerialPort.PARITY_NONE);
                            try {
                                this.fileStream = serialPort.getOutputStream();
                            } catch (IOException ex) {
                                Main.logger.log(Level.SEVERE, "Serial:", "IOException(6): " + ex.getMessage());
                                JOptionPane.showMessageDialog(null, "IOException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (UnsupportedCommOperationException ex) {
                        Main.logger.log(Level.SEVERE, "Serial:", "UnsupportedCommOperationException: " + ex.getMessage());
                        JOptionPane.showMessageDialog(null, "UnsupportedCommOperationException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
                    } catch (PortInUseException ex) {
                        Main.logger.log(Level.SEVERE, "Serial:", "PortInUseException: " + ex.getMessage());
                        Main.hardware.setPolePort("");
                        JOptionPane.showMessageDialog(null, "PortInUseException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    } catch (NoSuchPortException ex) {
                        Main.logger.log(Level.SEVERE, "Serial:", "NoSuchPortException: " + ex.getMessage());
                        JOptionPane.showMessageDialog(null, "NoSuchPortException:" + ex, "Serial", JOptionPane.ERROR_MESSAGE);
                        Main.hardware.setPolePort("");
                        System.exit(0);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public void write(byte[] bs) {//need if fileStream is null here
        if (fileStream != null && bs != null) {
            try {
                if (bs.length == 0) {
                    return;//do not print nothing
                }
                //code set conversion here
                fileStream.write(bs);
            } catch (IOException e) {
                Main.logger.log(Level.SEVERE, "Serial:", "IOException(1): " + e.getMessage());
                try {
                    getFileStream().close();
                } catch (IOException ex) {
                    Main.logger.log(Level.SEVERE, "Serial:", "IOException(2): " + ex.getMessage());
                }
            }
        }
    }

    public void closePort() {
        try {
            String s = getPort();
            if (serialPort != null) {
                serialPort.close();
            }
            if (fileStream != null) {
                fileStream.close();
                fileStream = null;
            }
        } catch (IOException ex) {
            Main.logger.log(Level.SEVERE, "Serial:", "IOException(3): " + ex.getMessage());
        }
        same = false;
    }

    public void openPort(String name) {
        try {
            if (name.equalsIgnoreCase("ReceiptPort")) {
                setPort(Main.hardware.getReceiptPort());
                if (!port.isEmpty()) {
                    if (getPort().equalsIgnoreCase(POLEPORT.getPort())) {
                        if (POLEPORT.fileStream == null) {
                            RECEIPTPORT.fileStream = new FileOutputStream(getPort());
                        } else {
                            RECEIPTPORT.fileStream = POLEPORT.fileStream;
                        }
                        same = true;
                    } else {

                        RECEIPTPORT.fileStream = new FileOutputStream(getPort());

                    }
                }
            } else if (name.equalsIgnoreCase("PolePort")) {
                setPort(Main.hardware.getPolePort());
                if (!port.isEmpty()) {
                    if (getPort().equalsIgnoreCase(RECEIPTPORT.getPort())) {
                        POLEPORT.fileStream = RECEIPTPORT.fileStream;
                        same = true;
                    } else {
                        POLEPORT.fileStream = new FileOutputStream(getPort());
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Main.logger.log(Level.SEVERE, "Serial:", "FileNotFoundException(4): " + ex.getMessage());
        }
    }

    /**
     * @return the fileStream
     */
    public OutputStream getFileStream() {
        if (fileStream == null) {
            if (same) {
                return RECEIPTPORT.fileStream;
            }
        }
        return fileStream;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @return the same
     */
    public static boolean isSame() {
        return same;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        if(port==null){
            this.port="";
        }else{
            this.port = port;
        }
    }
}
