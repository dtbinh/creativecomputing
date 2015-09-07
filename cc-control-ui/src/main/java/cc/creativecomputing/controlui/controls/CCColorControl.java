package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCColor;

public class CCColorControl extends CCValueControl<CCColor, CCColorPropertyHandle>{
	
	private class ShowColorChooserAction extends AbstractAction {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -563699696651781605L;
		
		private JColorChooser _myColorChooser;
	    private JDialog _myDialog;

	    ShowColorChooserAction(Component theFrame, JColorChooser theColorChooser) {
	        super("Color Chooser...");
	        _myColorChooser = theColorChooser;
	        

	        // Choose whether dialog is modal or modeless
	        boolean modal = false;

	        // Create the dialog that contains the chooser
	        _myDialog = JColorChooser.createDialog(
	        	theFrame, 
	        	"", 
	        	modal,
	            theColorChooser, 
	            null, 
	            new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent theE) {
						_myColorPanel.setBackground(new Color((float)_myLastColor.r, (float)_myLastColor.g,(float) _myLastColor.b, (float)_myLastColor.a));
						_myColor.set(_myLastColor);
						_myHandle.value(_myColor, true);
					}
				}
	        );
	    }

	    public void actionPerformed(ActionEvent evt) {
	    	try{
		        CCColor myColor = (CCColor)_myHandle.member().value();
		        CCLog.info(myColor);
		        _myColorChooser.setColor(new Color((float)myColor.r, (float)myColor.g, (float)myColor.b,(float) myColor.a));
	        }catch(Exception e){
	        	
	        }
	    	
	    	_myLastColor.set(_myColor);
	        // Show dialog
	        _myDialog.setVisible(true);

	        // Disable the action; to enable the action when the dialog is closed, see
	        // Listening for OK and Cancel Events in a JColorChooser Dialog
	        setEnabled(false);
	    }
	};
	
	private JButton _myButton;
	
	private CCColor _myLastColor = new CCColor();;
	
	private CCColor _myColor;
	
	private JPanel _myColorPanel;
	
	private JColorChooser _myColorChooser;

	static final Dimension SMALL_BUTTON_SIZE = new Dimension(100,15);

	public CCColorControl(CCColorPropertyHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(new CCPropertyListener<CCColor>() {
			
			@Override
			public void onChange(CCColor theValue) {
				_myColor = theValue;
				_myColorPanel.setBackground(new Color((float)_myColor.r, (float)_myColor.g, (float)_myColor.b, (float)_myColor.a));
			}
		});
		
		_myColor = theHandle.value().clone();
 
        //Create the Button.
		_myColorPanel = new JPanel();
		_myColorPanel.setBackground(new Color((float)_myColor.r, (float)_myColor.g, (float)_myColor.b, (float)_myColor.a));

		// Create a color chooser dialog
		_myColorChooser = new JColorChooser(new Color((float)_myColor.r, (float)_myColor.g, (float)_myColor.b, (float)_myColor.a));
		_myColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent theE) {
				Color myColor = _myColorChooser.getColor();
				_myColorPanel.setBackground(myColor);
				_myColor = new CCColor(myColor.getRed(), myColor.getGreen(), myColor.getBlue(), myColor.getAlpha());
				_myHandle.value(_myColor, true);
			}
		});
		
        AbstractColorChooserPanel[] panels=_myColorChooser.getChooserPanels();
        for(AbstractColorChooserPanel p:panels){
            String displayName=p.getDisplayName();
            switch (displayName) {
                case "RGB":
                case "HSL":
                case "CMYK":
                case "Muster":
                	_myColorChooser.removeChooserPanel(p);
                    break;
            }
        }

        _myColorChooser.setPreviewPanel(new JPanel());
		
        _myButton = new JButton("edit color");
        CCUIStyler.styleButton(_myButton);
        
	}
	
	@Override
	public CCColor value() {
		return _myColor;
	}
	
	private JPanel _myPanel;
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		_myPanel = thePanel;
		
		_myButton.addActionListener(new ShowColorChooserAction(_myPanel, _myColorChooser));
		
		thePanel.add(_myLabel,  constraints(0, theY, GridBagConstraints.LINE_END, 5, 5, 1, 5));
		thePanel.add(_myButton, constraints(1, theY, GridBagConstraints.LINE_START, 5, 15, 1, 5));
		thePanel.add(_myColorPanel, constraints(2, theY, GridBagConstraints.LINE_START, 5, 15, 1, 5));
//		thePanel.add(_myColorPanel, myConstraints);
	}
}
