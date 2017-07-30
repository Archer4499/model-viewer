import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Based on :http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html with minor modifications
 * JOGL 2.0 Program Template (GLCanvas)
 * This is the top-level "Container", which allocates and add GLCanvas ("Component")
 * and animator.
 */
@SuppressWarnings("serial")
public class Assignment2 extends JFrame {
    // Define constants for the top-level container
    private static String TITLE = "Assignment 2 - Derek King";  // window's title
    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 480; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second

    /**
     * Constructor to setup the top-level container and animator
     */
    private Assignment2(String filename) {
        // Load zones
        Zones zones = new Zones(filename);

        // Create the OpenGL rendering canvas
        GLCanvas canvas = new Renderer(zones);

        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // Create a animator that drives canvas' display() at the specified FPS.
        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

        // Create the top-level container frame
        this.getContentPane().add(canvas, BorderLayout.CENTER);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Use a dedicate thread to run the stop() to ensure that the
                // animator stops before program exits.
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }
                }.start();
            }
        });

        // Create and register a listener for dragging
        MouseListener mouse = new MouseListener((Renderer)canvas);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseListener(mouse);

        // Create Options pane
        JPanel optionsFrame = new OptionsFrame((Renderer)canvas, zones);
        this.getContentPane().add(optionsFrame, BorderLayout.LINE_END);

        this.setTitle(TITLE);
        this.pack();

        this.setVisible(true);
        animator.start(); // start the animation loop
    }

    /**
     * The entry main() method
     */
    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        // run the constructor
        if (args.length != 1) {
            System.out.println("Give filename as first argument");
            System.exit(0);
        } else {
            SwingUtilities.invokeLater(() -> new Assignment2(args[0]));
        }
    }
}