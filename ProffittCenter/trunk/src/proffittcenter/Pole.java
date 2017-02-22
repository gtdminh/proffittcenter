/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

//import com.sun.speech.freetts.VoiceManager;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.speech.AudioException;
//import javax.speech.Central;
//import javax.speech.EngineException;
//import javax.speech.EngineStateError;
//import javax.speech.synthesis.*;


/**
 *
 * @author HP_Owner
 */
public class Pole {

    private final byte[] POLE = {0x1b, '=', 0x02};//select
    private final byte[] SELFTEST = {0x1f, 0x40};
    private byte[] countrySelect = {0x1b, 'R', Main.hardware.getCode()};
    private final byte[] RESET = {0x0c};//Initialize printer
    private String port;
    private OutputStream fileStream;
    String country;


    public Pole() {
//        try {
            Serial rp = Serial.POLEPORT;
            port = rp.getPort();
            fileStream = null;
            if (!port.isEmpty()) {
                fileStream = Serial.POLEPORT.getFileStream();
                if (fileStream == null) {
                    Serial.POLEPORT.openPort("PolePort");
                    fileStream = Serial.POLEPORT.getFileStream();
                }
                if (fileStream != null) {
                    Serial.POLEPORT.write(POLE);
                    Serial.POLEPORT.write(RESET);
                }
            }
            
        }

    public void execute(String top, String bottom) {
        port = Main.hardware.getPolePort();
        if (!port.isEmpty()) {
            selectPole();
            Serial.POLEPORT.write(Main.hardware.getCodeTable());
            Serial.POLEPORT.write(Main.hardware.getFontSet());
            if(top==null||bottom==null){
                return;
            }
            showLine(StringOps.fixLengthUntrimmed(top, 20)
                    + StringOps.fixLengthUntrimmed(bottom, 20));
        }
    }


    @SuppressWarnings("static-access")
    private void showLine(String s) {
        s = replacePoundAndEuro(s);
        if (!port.isEmpty()) {
            try {
                if (s.length() == 0) {
                    return;
                }
                selectPole();
                Serial.POLEPORT.write(RESET);
                Serial.POLEPORT.write(Main.hardware.getCodeTable());
                Serial.POLEPORT.write(Main.hardware.getFontSet());
                String s1 = s;
                //need to convert from String to byte[]
                byte[] line = new byte[s1.length()];
                String charset = Main.hardware.charset;
                if (charset.isEmpty()) {
                    line = s1.getBytes();
                } else {
                    line = s1.getBytes("ISO-8859-1");//"UTF-8""ISO-8859-1"
                }
                Serial.POLEPORT.write(line);
            } catch (UnsupportedEncodingException ex) {
                Main.logger.log(Level.SEVERE, "Pole.showLine ", "UnsupportedEncodingException: " + ex.getMessage());
            }
        }
    }

    private void selectPole() {
        if(Serial.isSame()){
            Serial.POLEPORT.write(POLE);
        }
    }

    private String replacePoundAndEuro(String s) {
        char e = '€', ee = 0xd5, p = '£', pp = '#';//ee = 0xd5
        s = s.replace(p, pp);
        s = s.replace(e, ee);
        return s;
    }

    void selfTest() {
         port = Main.hardware.getPolePort();
        if (!port.isEmpty()){
            Serial.POLEPORT.write(SELFTEST);
        }
    }
}
