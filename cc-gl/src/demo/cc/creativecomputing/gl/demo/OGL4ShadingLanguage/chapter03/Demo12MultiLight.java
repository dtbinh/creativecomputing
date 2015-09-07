package cc.creativecomputing.gl.demo.OGL4ShadingLanguage.chapter03;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl.data.GLCombinedBuffer;
import cc.creativecomputing.gl.data.GLMesh;
import cc.creativecomputing.gl.demo.CCTorus;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class Demo12MultiLight extends CCGL4Adapter{
	
	private GLMesh _myMesh;
//	private GLMesh _myPlane;
	private CCMatrix4x4 _myModelMatrix;
	private CCMatrix4x4 _myViewMatrix;
	private CCMatrix4x4 _myProjectionMatrix;
	
	private GLShaderProgram _myShader;
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(GLGraphics theG) {
	    _myModelMatrix = new CCMatrix4x4();
	    _myViewMatrix = CCMatrix4x4.createLookAt(new CCVector3(0.5f,0.75f,0.75f), new CCVector3(0.0f,0.0f,0.0f), new CCVector3(0.0f,1.0f,0.0f));
	    _myProjectionMatrix = new CCMatrix4x4();
	    
		_myMesh = new GLMesh(GLDrawMode.TRIANGLES, new GLCombinedBuffer(new CCTorus(10, 10, 0.2f, 0.6f).data()));
//		_myPlane = new CCMesh(new CCGridXZ(10, 10, true).generateMesh(100, 100));

		_myShader = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this, "shader/multilight_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this, "shader/multilight_frag.glsl"))
		);
		_myShader.use();

		for (int i = 0; i < 5; i++) {
			String name = "lights[" + i + "].Position";
			double x = 2.0f * CCMath.cos((CCMath.TWO_PI / 5) * i);
			double z = 2.0f * CCMath.sin((CCMath.TWO_PI / 5) * i);
			_myShader.uniform4f(name, _myViewMatrix.applyPost(new CCVector4(x, 1.2f, z + 1.0f, 1.0f)));
		}

		_myShader.uniform3f("lights[0].Intensity", 0.0f, 0.8f, 0.8f);
		_myShader.uniform3f("lights[1].Intensity", 0.0f, 0.0f, 0.8f);
		_myShader.uniform3f("lights[2].Intensity", 0.8f, 0.0f, 0.0f);
		_myShader.uniform3f("lights[3].Intensity", 0.0f, 0.8f, 0.0f);
		_myShader.uniform3f("lights[4].Intensity", 0.8f, 0.8f, 0.8f);

		_myShader.uniform3f("Kd", 0.4f, 0.4f, 0.4f);
		_myShader.uniform3f("Ks", 0.9f, 0.9f, 0.9f);
		_myShader.uniform3f("Ka", 0.1f, 0.1f, 0.1f);
		_myShader.uniform1f("Shininess", 180.0f);
	}
	
	@Override
	public void reshape(GLGraphics g) {
		g.viewport(0, 0, g.width(), g.height());
		_myProjectionMatrix = CCMatrix4x4.createPerspective(70.0f,g.aspectRatio(), 0.3f, 100.0f);
	}
	
	double _myAngle = 0;

	
	@Override
	public void update(CCAnimator theAnimator) {
		_myAngle += theAnimator.deltaTime();
	}
	
	private void setMatrices(){
		 CCMatrix4x4 mv = _myModelMatrix.multiply(_myViewMatrix);
		 _myShader.uniformMatrix4f("ModelViewMatrix", mv);
		 _myShader.uniformMatrix3f("NormalMatrix", mv.matrix3());
		 _myShader.uniformMatrix4f("MVP", mv.multiply(_myProjectionMatrix));
	}
	
	@Override
	public void display(GLGraphics g) {
		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);	
		g.depthTest();
	   _myModelMatrix.set(CCMatrix4x4.IDENTITY);
		_myModelMatrix.applyRotationY(_myAngle);
//		_myModelMatrix.applyRotationY(CCMath.radians(90));
		setMatrices();
	    _myMesh.draw();
	    
	    _myModelMatrix.set(CCMatrix4x4.IDENTITY);
	    _myModelMatrix.applyTranslationPost(0,-0.45f,0);
	    setMatrices();
//	    _myPlane.draw();
	}
	
	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo12MultiLight());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		myAppManager.glcontext().size(800, 800);
		myAppManager.start();
	}
}
