/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;

/**
 *
 * @author HP_Owner
 */
public class SplashWindow_1
        extends Window {

    /**
    
     *
    
     */
    private static final long serialVersionUID = 1L;
    private Image splashImage;
    private boolean paintCalled = false;
    public SplashWindow_1(Frame owner, Image splashImage) {
        super(owner);
        this.splashImage = splashImage;
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(splashImage, 0);
        try {
            mt.waitForID(0);
        } catch (InterruptedException ie) {
        }
        int imgWidth = splashImage.getWidth(this);
        int imgHeight = splashImage.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenDim.width - imgWidth) / 2,
                (screenDim.height - imgHeight) / 2);
    }

    @Override
    public void update(Graphics g) {
        g.setColor(getForeground());
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(splashImage, 0, 0, this);
        if (!paintCalled) {
            paintCalled = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static Frame splash(Image splashImage) {
        Frame f = new Frame();
        SplashWindow_1 w = new SplashWindow_1(f, splashImage);
        w.toFront();
        w.show();
        if (!EventQueue.isDispatchThread()) {
            synchronized (w) {
                while (!w.paintCalled) {
                    try {
                        w.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        return f;

    }
}
