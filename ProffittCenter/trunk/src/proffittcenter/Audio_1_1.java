/*
 * AudioPlayer.java
 *
 * Created on 31 October 2007, 19:12
 */
package proffittcenter;

import java.io.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

/**
 *
 * @author  Dave Proffitt
 */
public class Audio_1_1 extends javax.swing.JFrame {

    private static Thread thread;
    private static Clip clip;
    private AudioFormat audioFormat;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;
    private boolean stopPlayback = false;
    private static String sep = File.separator;
    private static String fn = "";
    private URL is = null;
    private boolean firstTime = true;
    private static PlayThread audioThread;

    /** Creates new form AudioPlayer */
    public Audio_1_1() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playBtn = new javax.swing.JButton();
        stopBtn = new javax.swing.JButton();
        textField = new javax.swing.JTextField();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setName("audioPlayer"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/AudioPlayer"); // NOI18N
        playBtn.setText(bundle.getString("Audio.playBtn.text")); // NOI18N
        playBtn.addActionListener(formListener);

        stopBtn.setText(bundle.getString("Audio.stopBtn.text")); // NOI18N
        stopBtn.addActionListener(formListener);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textField, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playBtn)
                        .addGap(18, 18, 18)
                        .addComponent(stopBtn)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playBtn)
                    .addComponent(stopBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == playBtn) {
                Audio_1_1.this.playBtnActionPerformed(evt);
            }
            else if (evt.getSource() == stopBtn) {
                Audio_1_1.this.stopBtnActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void playBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBtnActionPerformed
        stopBtn.setEnabled(true);
        playBtn.setEnabled(false);
        play(textField.getText());//Play the file

    }//GEN-LAST:event_playBtnActionPerformed

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed
        stopPlayback = true;
    }//GEN-LAST:event_stopBtnActionPerformed

//    public static synchronized void play1(String fileName) {
//        if (!Main.hardware.isSoundOn() || Main.shop.isSpeech()) {
//            return;
//        }
//        if (fileName.length() == 0) {
//            return;
//        }
//        if (fileName.equalsIgnoreCase("TaDa")) {
//            int xx = 0;
//        }
//        fn = "audio" + "/" + fileName.toUpperCase() + ".WAV";
//        if (thread == null) {
//            thread = new Thread(new Runnable() {
//
//                private Clip clip;
//
//                public void run() {
//                    try {
////                        if (clip != null) {
////                            clip.stop();
////                        }
//                        clip = AudioSystem.getClip();
//                        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Main.class.getResourceAsStream(fn));
//                        fn = "";
//                        clip.open(inputStream);
//                        clip.start();
//                        while (clip.isActive()) {
//                            Thread.sleep(100);
//                        }
//                    } catch (Exception e) {
//                        Main.logger.log(Level.SEVERE, "Audio:", "run: " + e.getMessage());
//                    }
//                }
//            });
//            thread.start();
//        } else {
//        }
//
//    }

    public static void play(String fileName) {
        {
            InputStream bufferedIn;
            AudioInputStream inputStream = null;
            try {
                if (!Main.hardware.isSoundOn() || Main.shop.isSpeech()) {
                    return;
                }
                if (fileName.length() == 0) {
                    return;
                }
                fn = "audio" + "/" + fileName.toUpperCase() + ".WAV";
                if (clip != null) {
                    clip.close();
                }
                clip = AudioSystem.getClip();
                //read audio data from whatever source (file/classloader/etc.)
                InputStream audioSrc = Main.class.getResourceAsStream(fn);
                //add buffer for mark/reset support
                bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);                
                fn = "";
                clip.open(audioStream);
                clip.start();
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Audio_1_1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Audio_1_1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Audio_1_1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                Main.hardware.setSoundOn(false);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Audio_1_1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        @Override
        public void run() {
            try {
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();
                int cnt;
                //Keep looping until the input read method returns -1 for empty 
                //stream or the user clicks the Stop button causing
                // stopPlayback to switch from false to true.
                while ((cnt = audioInputStream.read(tempBuffer,
                        0, tempBuffer.length)) != -1 && stopPlayback == false) {
                    if (cnt > 0) {
                        //Write data to the internal buffer of the data line 
                        //where it will be delivered to the speaker.
                        sourceDataLine.write(tempBuffer, 0, cnt);
                    }//end if
                }//end while
                //Block and wait for internal buffer of the data line to empty.
                sourceDataLine.drain();
                sourceDataLine.close();
                //Prepare to playback another file
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
                stopPlayback = false;
            } catch (Exception e) {
                if (firstTime) {//if no sound card, limit respose to first time
                    firstTime = false;
                    Main.logger.log(Level.SEVERE, "AudioPlayer ", "play: " + e.getMessage());
                }
            }//end catch
        }//end run
    }//end inner class PlayThread
    //===================================//

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Audio_1_1().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton playBtn;
    private javax.swing.JButton stopBtn;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables
}
