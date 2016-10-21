package cc.creativecomputing.demo.topic.simulation.reactivediffusion;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCGLSwapBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.math.spline.CCSpline;

public class CCReactiveDiffusionDemo extends CCGL2Adapter {
	@CCProperty(name = "Reaction u", min=0.0, max=0.4, digits = 2)
	private double	mReactionU = 0.25;
	@CCProperty(name = "Reaction v", min=0.0, max=0.4, digits = 2)
	private double	mReactionV = 0.04;
	@CCProperty(name = "Reaction k", min=0.0, max=1.0, digits = 3)
	private double	mReactionK = 0.047;
	@CCProperty(name = "Reaction f", min=0.0, max=1.0, digits = 3)
	private double	mReactionF = 0.1;
	
	@CCProperty(name = "spline 0")
	private CCSpline _cSpline0 = new CCBezierSpline();
	@CCProperty(name = "spline 1")
	private CCSpline _cSpline1 = new CCBezierSpline();
	
	private CCGLSwapBuffer _mySwapBuffer;
	
	@CCProperty(name = "reaction diffusion shader")
	private CCGLProgram _myReactiveDiffusionProgram;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySwapBuffer = new CCGLSwapBuffer(g.width(), g.height(), CCTextureTarget.TEXTURE_RECT);
		_mySwapBuffer.clear();
		
		_myReactiveDiffusionProgram = new CCGLProgram(null, CCNIOUtil.classPath(this,"reactive_diffusion2.glsl"));
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	private boolean _myReset = false;
	
	@CCProperty(name = "reset")
	public void reset(){
		_myReset = true;
	}

	@Override
	public void display(CCGraphics g) {
		
		if(_myReset){
			_mySwapBuffer.clear();
		}
		
		for(int i = 0; i < 25;i++){
			g.texture(0, _mySwapBuffer.attachment(0));

			_myReactiveDiffusionProgram.start();
			_myReactiveDiffusionProgram.uniform1i( "tex", 0 );
			_myReactiveDiffusionProgram.uniform1f( "ru", mReactionU );
			_myReactiveDiffusionProgram.uniform1f( "rv", mReactionV );
			_myReactiveDiffusionProgram.uniform1f( "k", mReactionK );
			_myReactiveDiffusionProgram.uniform1f( "f", mReactionF );
			
			_myReactiveDiffusionProgram.uniform2f( "iResolution", g.width(), g.height());
			_myReactiveDiffusionProgram.uniform1i( "reset", _myReset ? 1 : 0);
			
			_mySwapBuffer.draw();
			_myReactiveDiffusionProgram.end();
			
			g.noTexture();
			
			
			_mySwapBuffer.swap();
			
		}
		

		_mySwapBuffer.beginDraw();
		if(mouse().isPressed)g.ellipse(mouse().position.x, g.height() - mouse().position.y, 25);
		_mySwapBuffer.endDraw();
		
		g.clear();
		g.pushMatrix();
		g.translate(-g.width() / 2, -g.height() / 2);
		g.image(_mySwapBuffer.attachment(0), 0,0);
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(double i = 0; i <= 100;i++){
			double myBlend = i / 100;
			CCVector3 myVertex = _cSpline0.interpolate(myBlend);
			if(myVertex == null)break;
			g.vertex(myVertex.x * g.width(), (1 - myVertex.y) * g.height());
		}
		g.endShape();
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(double i = 0; i <= 100;i++){
			double myBlend = i / 100;
			CCVector3 myVertex = _cSpline1.interpolate(myBlend);
			if(myVertex == null)break;
			g.vertex(myVertex.x * g.width(), (1 - myVertex.y) * g.height());
		}
		g.endShape();
		g.popMatrix();
		

		_myReset = false;
	}

	public static void main(String[] args) {

		CCReactiveDiffusionDemo demo = new CCReactiveDiffusionDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}