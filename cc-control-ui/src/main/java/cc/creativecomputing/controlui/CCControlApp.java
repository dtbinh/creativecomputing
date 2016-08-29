package cc.creativecomputing.controlui;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;

public class CCControlApp  {
	
	public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
		  public void uncaughtException(Thread t, Throwable e) {
		    handle(e);
		  }

		  public void handle(Throwable throwable) {
		    try {
		      // insert your e-mail code here
		    } catch (Throwable t) {
		    	CCLog.info("bla");
		      t.printStackTrace();
		    }
		  }

		  public static void registerExceptionHandler() {
		    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		    System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		  }
		}

	private Map<String, CCCustomMenu> _myCustomMenues = new HashMap<String, CCCustomMenu>();
	
	private CCControlComponent _myControlComponent;
	
	private CCAnimatorAdapter _myAnimatorListener;

	private JMenuBar _myMenuBar;
	
	private JFrame _myFrame;
	
	@CCProperty(name = "animator")
	private CCAnimator _myAnimator;
	

	public CCControlApp(Object theRootObject, CCAnimator theAnimator) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// System.err.println("Couldn't use system look and feel.");
		// }

		// Create and set up the window.
        _myFrame = new JFrame("Creative Computing Controls");
        _myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		_myControlComponent = new CCControlComponent(_myFrame);

		// Add content to the window.
		_myFrame.add(_myControlComponent);
		
		_myAnimator = theAnimator;
		_myAnimatorListener = new CCAnimatorAdapter() {
			@Override
			public void update(CCAnimator theAnimator) {
				CCControlApp.this.update(theAnimator.deltaTime());
			}
		};
		_myAnimator.listener().add(_myAnimatorListener);
		
		_myMenuBar = new JMenuBar();
		_myMenuBar.add(_myControlComponent.view().fileMenu());
		_myMenuBar.add(_myControlComponent.view().timelineMenu());
		_myFrame.setJMenuBar(_myMenuBar);

		// Display the window.
		_myFrame.pack();
		_myFrame.setVisible(true);
		
		ExceptionHandler.registerExceptionHandler();
	}
	
	public void update(double theDeltaTime){
		_myControlComponent.timeline().update(theDeltaTime);
		_myControlComponent.propertyMap().rootHandle().update(theDeltaTime);
	}
	
	public void setData(Object theData, String thePresetPath){
		_myControlComponent.setData(theData, thePresetPath);
	}
	
	public TimelineContainer timeline(){
		return _myControlComponent.timeline();
	}
	
	public CCControlApp(CCTimelineSynch theSynch) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// System.err.println("Couldn't use system look and feel.");
		// }

		// Create and set up the window.
        _myFrame = new JFrame("Creative Computing Controls");
        _myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		_myControlComponent = new CCControlComponent(_myFrame);

		// Add content to the window.
		_myFrame.add(_myControlComponent);
		
		_myAnimator = theSynch.animator();
		_myAnimatorListener = new CCAnimatorAdapter() {
			@Override
			public void update(CCAnimator theAnimator) {
				if(_myControlComponent.propertyMap().rootHandle() == null){
					return;
				}
				_myControlComponent.propertyMap().rootHandle().update(theAnimator.deltaTime());
			}
		};
		_myAnimator.listener().add(_myAnimatorListener);
		theSynch.timeline(_myControlComponent.timeline());
		
		_myMenuBar = new JMenuBar();
		_myMenuBar.add(_myControlComponent.view().fileMenu());
		_myMenuBar.add(_myControlComponent.view().timelineMenu());
		_myFrame.setJMenuBar(_myMenuBar);

		// Display the window.
		_myFrame.pack();
		_myFrame.setVisible(true);
	}
	
	public CCControlApp(Object theRootObject) {
		this(theRootObject, new CCAnimator());
		_myAnimator.start();
	}
	
	public CCAnimatorAdapter animatorListener(){
		return _myAnimatorListener;
	}

	
	public CCPropertyMap propertyMap(){
		return _myControlComponent.propertyMap();
	}
	
	public JMenuItem addCustomCommand(final String theMenu, final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator) {
		if(!_myCustomMenues.containsKey(theMenu)) {
			CCCustomMenu myCustomMenu = new CCCustomMenu(theMenu);
			_myMenuBar.add(myCustomMenu);
			_myCustomMenues.put(theMenu, myCustomMenu);
		}
		return _myCustomMenues.get(theMenu).addCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator);
	}
	
	public JCheckBoxMenuItem addCustomCheckBoxCommand(final String theMenu, final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator, boolean theDefault) {
		if(!_myCustomMenues.containsKey(theMenu)) {
			CCCustomMenu myCustomMenu = new CCCustomMenu(theMenu);
			_myMenuBar.add(myCustomMenu);
			_myCustomMenues.put(theMenu, myCustomMenu);
		}
		return _myCustomMenues.get(theMenu).addCheckBoxCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator, theDefault);
	}
}
