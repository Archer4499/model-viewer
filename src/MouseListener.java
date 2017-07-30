import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;


class MouseListener extends MouseInputAdapter {
    private Renderer canvas;
    private Point mousePt;

    MouseListener(Renderer canvasIn) {
        canvas = canvasIn;
    }
    
    @Override
    public void mousePressed(MouseEvent event) {
        mousePt = event.getPoint();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void mouseDragged(MouseEvent event) {
        int b1 = MouseEvent.BUTTON1_DOWN_MASK;
        int b3 = MouseEvent.BUTTON3_DOWN_MASK;
        int ctrl = MouseEvent.CTRL_DOWN_MASK;
        int alt = MouseEvent.ALT_DOWN_MASK;
        int shift = MouseEvent.SHIFT_DOWN_MASK;
        
        int modifiers = event.getModifiersEx();

        float xDiff = 0.1f * (event.getX() - mousePt.x);
        float yDiff = 0.1f * (event.getY() - mousePt.y);
        mousePt = event.getPoint();

        if ((modifiers & b1) == b1) {
            if ((modifiers & b3) == b3) {
                // Left and Right Mouse buttons down
                canvas.zOffset += 0.5f * yDiff;
            } else {
                // Left Mouse button down
                canvas.xOffset += 0.5f * xDiff;
                canvas.yOffset -= 0.5f * yDiff;
            }
        } else if ((modifiers & b3) == b3) {
            // Right Mouse button down
            if ((modifiers & (ctrl|alt|shift)) == ctrl) {
                // Only Control down
                canvas.yRot += xDiff;
            } else if ((modifiers & (ctrl|alt|shift)) == alt) {
                // Only Alt down
                canvas.xRot += yDiff;
            } else if ((modifiers & (ctrl|alt|shift)) == shift) {
                // Only Shift down
                canvas.zRot += xDiff;
            } else if ((modifiers & (ctrl|alt|shift)) == 0) {
                // No modifier key down
                canvas.xRot += yDiff;
                canvas.yRot += xDiff;
            }
        }
    }
}
