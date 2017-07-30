import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class OptionsFrame extends JPanel {
    private Renderer canvas;

    OptionsFrame(Renderer canvasIn, Zones zones) {
        canvas = canvasIn;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add elements

        // Colour var drop-down selection
        this.add(new JLabel("Pseudo-Colouring Variable:"));

        JComboBox<String> colourDrop = new JComboBox<>(zones.getVariables().toArray(new String[zones.getVariables().size()]));
        // Fix for height in BoxLayout
        Dimension max = colourDrop.getMaximumSize();
        max.height = getPreferredSize().height;
        colourDrop.setMaximumSize(max);

        colourDrop.addActionListener(e -> canvas.colourVar = ((JComboBox)e.getSource()).getSelectedIndex());

        this.add(colourDrop);
        // End


        // Wireframe / Fill Radio buttons
        this.add(new JLabel("Wire/Fill:"));
        final JRadioButton wireCheck = new JRadioButton("Wireframe", true);
        final JRadioButton fillCheck = new JRadioButton("Fill");

        wireCheck.addItemListener(e -> canvas.fill = (e.getStateChange()!=1));
        fillCheck.addItemListener(e -> canvas.fill = (e.getStateChange()==1));

        ButtonGroup fillGroup = new ButtonGroup();
        fillGroup.add(wireCheck);
        fillGroup.add(fillCheck);

        this.add(wireCheck);
        this.add(fillCheck);
        // End


        // Projection Radio buttons
        this.add(new JLabel("Projection:"));
        final JRadioButton perspectiveCheck = new JRadioButton("Perspective", true);
        final JRadioButton orthoCheck = new JRadioButton("Orthographic");

        perspectiveCheck.addItemListener(e -> {
                                            canvas.ortho = (e.getStateChange()!=1);
                                            canvas.projectionModified = true;
                                        });
        orthoCheck.addItemListener(e -> {
                                        canvas.ortho = (e.getStateChange()==1);
                                        canvas.projectionModified = true;
                                    });

        ButtonGroup orthoGroup = new ButtonGroup();
        orthoGroup.add(perspectiveCheck);
        orthoGroup.add(orthoCheck);

        this.add(perspectiveCheck);
        this.add(orthoCheck);
        // End


        // Background
        this.add(new JLabel("Background Colour:"));

        JButton changeColour = new JButton("Change Color");
        changeColour.addActionListener(e -> {
            Color colour = JColorChooser.showDialog(this, "Choose a color", Color.lightGray);
            if (colour == null) colour = Color.lightGray;
            canvas.backgroundColour = colour.getRGBColorComponents(canvas.backgroundColour);
        });

        this.add(changeColour);
        // End

        // Filler
        this.add(Box.createVerticalGlue());
    }
}
