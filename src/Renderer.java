import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Quaternion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;


/**
 * Based on :http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html with minor modifications
 * JOGL 2.0 Program Template (GLCanvas)
 * This is a "Component" which can be added into a top-level "Container".
 * It also handles the OpenGL events to render graphics.
 */
@SuppressWarnings("serial")
public class Renderer extends GLCanvas implements GLEventListener {
    private GLU glu;  // for the GL Utility

    private FloatBuffer colourLookupTable;
    private Zones zones;

    // Mouse controlled
    float xRot, yRot, zRot;
    float xOffset, yOffset, zOffset;


    boolean projectionModified;
    // User modifiable vars
    int colourVar;
    boolean fill;
    boolean ortho;
    float[] backgroundColour;

    /**
     * Constructor to setup the GUI for this Component
     */
    Renderer(Zones zonesIn) {
        zones = zonesIn;
        this.addGLEventListener(this);
    }

    /**
     * Loads the colour lookup table from "CoolWarmFloat257.csv" into a FloatBuffer
     */
    private void loadColours() {
        // 257 lines * 3 floats/line * 4bytes/float
        ByteBuffer vbb = ByteBuffer.allocateDirect(257 * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());      // use the device hardware's native byte order
        colourLookupTable = vbb.asFloatBuffer(); // create a floating point buffer from the ByteBuffer

        try (BufferedReader br = new BufferedReader(new FileReader("CoolWarmFloat257.csv"))) {
            br.readLine(); // Get rid of the header
            for (int i = 0; i < 257; i++) {
                // Scalar, R, G, B
                String[] line = br.readLine().split(",");

                // We don't care about the scalar so just take the R,G,B values
                colourLookupTable.put(Float.parseFloat(line[1]));
                colourLookupTable.put(Float.parseFloat(line[2]));
                colourLookupTable.put(Float.parseFloat(line[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @SuppressWarnings("static-access")
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        loadColours();

        xRot = 0.0f;
        yRot = 0.0f;
        zRot = 0.0f;
        xOffset = 0.0f;
        yOffset = 0.0f;
        zOffset = 0.0f;

        projectionModified = false;

        // Defaults
        ortho = false;
        colourVar = 0; // 0..variables.size()-1
        fill = false;
        backgroundColour = new float[]{0.0f, 0.0f, 0.1f}; // 0.0f..1.0f
    }

    @SuppressWarnings("static-access")
    @Override
    public void display(GLAutoDrawable drawable) {
        if (projectionModified) {
            this.setProjection(drawable);
            projectionModified = false;
        }

        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(backgroundColour[0], backgroundColour[1], backgroundColour[2], 0.0f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); // Reset The View

        float xCentre = (zones.getMin(0)+zones.getMax(0))/2.0f;
        float yCentre = (zones.getMin(1)+zones.getMax(1))/2.0f;
        float zCentre = (zones.getMin(2)+zones.getMax(2))/2.0f;

        // Centre the object in the display
        gl.glTranslatef(-xCentre, -yCentre, -zCentre);

        // Apply mouse movement
        gl.glTranslatef(xOffset, yOffset, zOffset);

        // Move the 'camera' back a bit
        gl.glTranslatef(0f, 0f, -20.0f);

        // Rotate around the centre of the object
        gl.glTranslatef(xCentre, yCentre, zCentre);
        gl.glRotatef(xRot, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(yRot, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(zRot, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(-xCentre, -yCentre, -zCentre);

        float currMin = zones.getMin(colourVar);
        float currMax = zones.getMax(colourVar);
        // Switch between Fill and Line drawing modes
        if (fill) {
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
        } else {
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
            gl.glColor3f(1.0f, 1.0f, 1.0f);
        }

        // Draw each polygon in each zone
        for (int i = 0; i < zones.getElementsZonesSize(); i++) {
            PointsZone pointsZone = zones.getPointsZone(i);
            ElementsZone elementsZone = zones.getElementsZone(i);
            for (int j = 0; j < elementsZone.getSize(); j++) {
                ArrayList<Integer> elements = elementsZone.getElements(j);
                gl.glBegin(GL2.GL_POLYGON);
                for (int element : elements) {
                    if (fill) {
                        int colourLookupIndex = (int) Math.floor(256*
                                                                 (pointsZone.getVar(element-1, colourVar)-currMin) /
                                                                 (currMax - currMin));
                        colourLookupTable.position(3 * colourLookupIndex);
                        gl.glColor3fv(colourLookupTable);
                    }

                    gl.glVertex3fv(pointsZone.getPoints(element-1));
                }
                gl.glEnd();
            }
        }

        gl.glFlush();
    }

    @SuppressWarnings("static-access")
    @Override
    public void reshape(GLAutoDrawable drawable, int xStart, int yStart, int width, int height) {
        setProjection(drawable);
    }

    private void setProjection(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        final float h = (float) this.getSurfaceWidth() / (float) this.getSurfaceHeight();
        if (ortho) {
            // Orthographic view of at least 1.2 times the size of largest dimension of the object
            float maxSize = 0.6f * Math.max(zones.getMax(0) - zones.getMin(0),
                                            Math.max(zones.getMax(1) - zones.getMin(1),
                                                     zones.getMax(2) - zones.getMin(2)));
            if (h > 1.0f) {
                gl.glOrtho(-maxSize*h, maxSize*h, -maxSize, maxSize, 1.0f, 100.0f);
            } else {
                gl.glOrtho(-maxSize, maxSize, -maxSize/h, maxSize/h, 1.0f, 100.0f);
            }
        } else {
            glu.gluPerspective(45.0f, h, 1.0f, 100.0f);
        }
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}
}