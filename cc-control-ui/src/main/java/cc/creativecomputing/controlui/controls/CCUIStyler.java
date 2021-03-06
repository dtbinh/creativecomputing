package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.JTextComponent;

import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;

public class CCUIStyler {

	
	protected static final Dimension SMALL_BUTTON_SIZE = new Dimension(100,13);
	
	public static JColorChooser createColorChooser(Color theColor){
		JColorChooser myResult = new JColorChooser(theColor);
		AbstractColorChooserPanel[] panels=myResult.getChooserPanels();
        for(AbstractColorChooserPanel p:panels){
            String displayName=p.getDisplayName();
            switch (displayName) {
                case "RGB":
                case "HSL":
                case "CMYK":
                case "Muster":
                	myResult.removeChooserPanel(p);
                    break;
            }
        }
        myResult.setPreviewPanel(new JPanel());
        return myResult;
	}
	
	public static void styleButton(AbstractButton theButton, int theWidth, int theHeight){
		theButton.setMargin(new Insets(2, 0, 0, 0));
		theButton.setFont(SwingGuiConstants.ARIAL_9);
		theButton.setPreferredSize(new Dimension(theWidth, theHeight));
	}
	
	public static void styleButton(AbstractButton theButton){
		styleButton(theButton, SMALL_BUTTON_SIZE.width, SMALL_BUTTON_SIZE.height);
	}
	
	public static void styleCombo(JComboBox<?> theCombo){
		theCombo.setFont(SwingGuiConstants.ARIAL_9);
		theCombo.setPreferredSize(new Dimension(113,13));
		theCombo.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		final JTextComponent tcA = (JTextComponent) theCombo.getEditor().getEditorComponent();
		tcA.setMargin(new Insets(0, 0, 0, 0));
		tcA.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
	}
	
	public static void styleLabel(JLabel theLabel){
		theLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		theLabel.setFont(SwingGuiConstants.ARIAL_BOLD_10);
		theLabel.setPreferredSize(new Dimension(100,15));
		theLabel.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	public static void styleTextField(JTextField theValueField, int theWidth) {
		theValueField.setBackground(Color.WHITE);
		theValueField.setForeground(Color.BLACK);
		theValueField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		theValueField.setHorizontalAlignment(JTextField.LEFT);
		theValueField.setFont(SwingGuiConstants.ARIAL_9);
		theValueField.setPreferredSize(new Dimension(theWidth,12));
	}
	
	public static void styleTransportComponent(JComponent theComponent, int theWidth, int theHeight){
		theComponent.setPreferredSize(new Dimension(theWidth, theHeight));
		theComponent.setFont(SwingGuiConstants.ARIAL_11);
		theComponent.setBackground(new Color(0.9f, 0.9f, 0.9f));
		theComponent.setForeground(Color.BLACK);
	}
	
	public static void addComponent(
		Container cont, GridBagLayout gbl, Component c,
		int x, int y, int width, int height, double weightx, double weighty
	) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}
	
	public static void styleSplitPane(JSplitPane thePane) {
		thePane.setUI(new BasicSplitPaneUI() {
			@SuppressWarnings("serial")
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					public void setBorder(Border b) {
					}

					@Override
					public void paint(Graphics g) {
						g.setColor(Color.GRAY);
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});
		thePane.setDividerSize(3);
		thePane.setBorder(null);
	}
}
