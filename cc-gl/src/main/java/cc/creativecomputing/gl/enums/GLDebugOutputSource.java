package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLDebugOutputSource {
	DEBUG_SOURCE_API(GL4.GL_DEBUG_SOURCE_API),
	DEBUG_SOURCE_WINDOW_SYSTEM(GL4.GL_DEBUG_SOURCE_WINDOW_SYSTEM),
	DEBUG_SOURCE_SHADER_COMPILER(GL4.GL_DEBUG_SOURCE_SHADER_COMPILER),
	DEBUG_SOURCE_THIRD_PARTY(GL4.GL_DEBUG_SOURCE_THIRD_PARTY),
	DEBUG_SOURCE_APPLICATION(GL4.GL_DEBUG_SOURCE_APPLICATION),
	DEBUG_SOURCE_OTHER(GL4.GL_DEBUG_SOURCE_OTHER),
	DONT_CARE(GL4.GL_DONT_CARE);
	
	private int _myGLID;
	
	private GLDebugOutputSource(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLDebugOutputSource fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_DEBUG_SOURCE_API:return DEBUG_SOURCE_API;
		case GL4.GL_DEBUG_SOURCE_WINDOW_SYSTEM:return DEBUG_SOURCE_WINDOW_SYSTEM;
		case GL4.GL_DEBUG_SOURCE_SHADER_COMPILER:return DEBUG_SOURCE_SHADER_COMPILER;
		case GL4.GL_DEBUG_SOURCE_THIRD_PARTY:return DEBUG_SOURCE_THIRD_PARTY;
		case GL4.GL_DEBUG_SOURCE_APPLICATION:return DEBUG_SOURCE_APPLICATION;
		case GL4.GL_DEBUG_SOURCE_OTHER:return DEBUG_SOURCE_OTHER;
		case GL4.GL_DONT_CARE:return DONT_CARE;
		}
		return null;
	}
}

