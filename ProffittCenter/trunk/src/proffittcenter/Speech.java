/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proffittcenter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.*;
import javax.speech.synthesis.*;

/**
 *
 * @author Dave
 */
public class Speech {


    private Synthesizer synth=null;

    public Speech(){
        
    }

    public static void main(String[] args) {
        
    }

    public void say(String s){
        if(Main.shop.isSpeech()){
            try {
            //constructor
            synth = Central.createSynthesizer(new SynthesizerModeDesc(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Speech.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EngineException ex) {
            Logger.getLogger(Speech.class.getName()).log(Level.SEVERE, null, ex);
        }
            try {
                if(synth==null){
                    return;
                }
                Calendar calendar = new GregorianCalendar();
            String sayTime = " Its "
                    + calendar.get(Calendar.HOUR) + " "
                    + calendar.get(Calendar.MINUTE) + " "
                    + (calendar.get(Calendar.AM_PM) == 0 ? " in the morning" : " in the afternoon");

                synth.allocate();
                synth.resume();

                synth.speakPlainText(s, null);

                synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
                synth.deallocate();

            } catch (Exception e) {
                Main.logger.log(Level.SEVERE, "Speech.say ", "Exception: " + e.getMessage());
            } 
        }
    }
    
    public String asCash(int cash){
        String s = Money.asMoney(cash);//will return £4.36 or 76p as appropriate
        if(s.charAt(0)=='£'){
            //locate decimal point
            int p=s.indexOf('.');
            s=s.substring(1, p)+" pound "+s.substring(p+1,s.length());
        } else {
            int p=s.indexOf('p');
            s=s.substring(0, s.length()-1)+" pence ";
        }
        return s;       
    }

}
