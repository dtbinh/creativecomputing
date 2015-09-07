package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLHintTarget {
	LINE_SMOOTH_HINT(GL4.GL_LINE_SMOOTH_HINT),
	POLYGON_SMOOTH_HINT(GL4.GL_POLYGON_SMOOTH_HINT),
	TEXTURE_COMPRESSION_HINT(GL4.GL_TEXTURE_COMPRESSION_HINT),
	FRAGMENT_SHADER_DERIVATIVE_HINT(GL4.GL_FRAGMENT_SHADER_DERIVATIVE_HINT);
	
	private int _myGLID;
	
	private GLHintTarget(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLHintTarget fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_LINE_SMOOTH_HINT:return LINE_SMOOTH_HINT;
		case GL4.GL_POLYGON_SMOOTH_HINT:return POLYGON_SMOOTH_HINT;
		case GL4.GL_TEXTURE_COMPRESSION_HINT:return TEXTURE_COMPRESSION_HINT;
		case GL4.GL_FRAGMENT_SHADER_DERIVATIVE_HINT:return FRAGMENT_SHADER_DERIVATIVE_HINT;
		}
		return null;
	}
}

