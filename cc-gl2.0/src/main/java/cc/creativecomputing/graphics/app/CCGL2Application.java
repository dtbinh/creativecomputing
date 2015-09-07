package cc.creativecomputing.graphics.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCTimelineSynch;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.graphics.CCGraphics;

public class CCGL2Application {
	
	@CCProperty(name = "animator")
	private CCAnimator _myAnimator;
	@CCProperty(name = "gl context")
	private CCGL2Context _myGLContext;
	@CCProperty(name = "synch")
	private CCTimelineSynch _mySynch = new CCTimelineSynch();
	
	@CCProperty(name = "app")
	private CCGLAdapter<CCGraphics, CCGL2Context> _myAdapter;

	private CCControlApp _myControlApp;

	public CCGL2Application(CCGLAdapter<CCGraphics, CCGL2Context> theGLAdapter) {
		_myAdapter = theGLAdapter;
		_myAnimator = new CCAnimator();
		_myAnimator.framerate = 60;
		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		_myAnimator.addListener(theGLAdapter);
		
		_myGLContext = new CCGL2Context(_myAnimator);
		theGLAdapter.glContext(_myGLContext);

		_myGLContext.addListener(new CCGLAdapter<CCGraphics, CCGL2Context>() {
			@Override
			public void init(CCGraphics theG) {
				_myAdapter.init(theG, _myAnimator);
				_myControlApp = new CCControlApp(CCGL2Application.this, _mySynch);
				_mySynch.animator().start();
				theGLAdapter.controlApp(_myControlApp);
			}
		});
		_myGLContext.addListener(
			new CCGLAdapter<CCGraphics, CCGL2Context>() {
				@Override
				public void display(CCGraphics theG) {
					theG.beginDraw();
				}
			}
		);
		_myGLContext.addListener(theGLAdapter);
		_myGLContext.addListener(
			new CCGLAdapter<CCGraphics, CCGL2Context>() {
				@Override

				public void display(CCGraphics theG) {
					theG.endDraw();
				}
			}
		);
		_myGLContext.addListener(
			new CCGLAdapter<CCGraphics, CCGL2Context>() {
				@Override
				public void dispose(CCGraphics theG) {
					_myAnimator.stop();
				}
			}
		);
	}
	
	public CCGL2Application(){
		this(null);
	}
	
	public CCAnimator animator(){
		return _myAnimator;
	}
	
	public CCGL2Context glcontext(){
		return _myGLContext;
	}
	
	
	public void start(){
		_myGLContext.start();
		_myAnimator.start();
	}
}