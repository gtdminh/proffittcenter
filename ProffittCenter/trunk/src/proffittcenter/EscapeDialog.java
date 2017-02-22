/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author HP_Owner
 */
public class EscapeDialog extends JDialog {

    public EscapeDialog(Frame frame, Boolean b) {
        super((Frame) frame, b);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", new PropertyChangeListener(){
            public void propertyChange(final PropertyChangeEvent e) {
                if (e.getNewValue() instanceof JTextField) {
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run() {
                            JTextField textField = (JTextField) e.getNewValue();
                            textField.selectAll();
                        }
                    });

                }
            }
        });
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addPropertyChangeListener("permanentFocusOwner", new PropertyChangeListener(){
            public void propertyChange(final PropertyChangeEvent e) {
                if (e.getNewValue() instanceof JTextField) {
                    SwingUtilities.invokeLater(new Runnable()
                 {

                        public void run() {
                            JTextField textField = (JTextField) e.getNewValue();
                            textField.selectAll();
                        }
                    });

                }
            }
        });
    }

    
    
    @Override
    protected JRootPane createRootPane() {
        JRootPane rp = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        AbstractAction actionListener = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        InputMap inputMap = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rp.getActionMap().put("ESCAPE", actionListener);

        return rp;
    }
}


