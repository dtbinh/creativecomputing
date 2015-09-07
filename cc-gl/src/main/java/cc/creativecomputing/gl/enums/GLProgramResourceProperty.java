package cc.creativecomputing.gl.enums;

import com.jogamp.opengl.GL4;

public enum GLProgramResourceProperty {
	ARRAY_SIZE(GL4.GL_ARRAY_SIZE),
	OFFSET(GL4.GL_OFFSET),
	BLOCK_INDEX(GL4.GL_BLOCK_INDEX),
	ARRAY_STRIDE(GL4.GL_ARRAY_STRIDE),
	MATRIX_STRIDE(GL4.GL_MATRIX_STRIDE),
	IS_ROW_MAJOR(GL4.GL_IS_ROW_MAJOR),
	ATOMIC_COUNTER_BUFFER_INDEX(GL4.GL_ATOMIC_COUNTER_BUFFER_INDEX),
	BUFFER_BINDING(GL4.GL_BUFFER_BINDING),
	BUFFER_DATA_SIZE(GL4.GL_BUFFER_DATA_SIZE),
	NUM_ACTIVE_VARIABLES(GL4.GL_NUM_ACTIVE_VARIABLES),
	ACTIVE_VARIABLES(GL4.GL_ACTIVE_VARIABLES),
	REFERENCED_BY_VERTEX_SHADER(GL4.GL_REFERENCED_BY_VERTEX_SHADER),
	REFERENCED_BY_TESS_CONTROL_SHADER(GL4.GL_REFERENCED_BY_TESS_CONTROL_SHADER),
	REFERENCED_BY_TESS_EVALUATION_SHADER(GL4.GL_REFERENCED_BY_TESS_EVALUATION_SHADER),
	REFERENCED_BY_GEOMETRY_SHADER(GL4.GL_REFERENCED_BY_GEOMETRY_SHADER),
	REFERENCED_BY_FRAGMENT_SHADER(GL4.GL_REFERENCED_BY_FRAGMENT_SHADER),
	REFERENCED_BY_COMPUTE_SHADER(GL4.GL_REFERENCED_BY_COMPUTE_SHADER),
	NUM_COMPATIBLE_SUBROUTINES(GL4.GL_NUM_COMPATIBLE_SUBROUTINES),
	COMPATIBLE_SUBROUTINES(GL4.GL_COMPATIBLE_SUBROUTINES),
	TOP_LEVEL_ARRAY_SIZE(GL4.GL_TOP_LEVEL_ARRAY_SIZE),
	LOCATION(GL4.GL_LOCATION),
	LOCATION_INDEX(GL4.GL_LOCATION_INDEX),
	IS_PER_PATCH(GL4.GL_IS_PER_PATCH);
	
	private int _myGLID;
	
	private GLProgramResourceProperty(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLProgramResourceProperty fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_ARRAY_SIZE:return ARRAY_SIZE;
		case GL4.GL_OFFSET:return OFFSET;
		case GL4.GL_BLOCK_INDEX:return BLOCK_INDEX;
		case GL4.GL_ARRAY_STRIDE:return ARRAY_STRIDE;
		case GL4.GL_MATRIX_STRIDE:return MATRIX_STRIDE;
		case GL4.GL_IS_ROW_MAJOR:return IS_ROW_MAJOR;
		case GL4.GL_ATOMIC_COUNTER_BUFFER_INDEX:return ATOMIC_COUNTER_BUFFER_INDEX;
		case GL4.GL_BUFFER_BINDING:return BUFFER_BINDING;
		case GL4.GL_BUFFER_DATA_SIZE:return BUFFER_DATA_SIZE;
		case GL4.GL_NUM_ACTIVE_VARIABLES:return NUM_ACTIVE_VARIABLES;
		case GL4.GL_ACTIVE_VARIABLES:return ACTIVE_VARIABLES;
		case GL4.GL_REFERENCED_BY_VERTEX_SHADER:return REFERENCED_BY_VERTEX_SHADER;
		case GL4.GL_REFERENCED_BY_TESS_CONTROL_SHADER:return REFERENCED_BY_TESS_CONTROL_SHADER;
		case GL4.GL_REFERENCED_BY_TESS_EVALUATION_SHADER:return REFERENCED_BY_TESS_EVALUATION_SHADER;
		case GL4.GL_REFERENCED_BY_GEOMETRY_SHADER:return REFERENCED_BY_GEOMETRY_SHADER;
		case GL4.GL_REFERENCED_BY_FRAGMENT_SHADER:return REFERENCED_BY_FRAGMENT_SHADER;
		case GL4.GL_REFERENCED_BY_COMPUTE_SHADER:return REFERENCED_BY_COMPUTE_SHADER;
		case GL4.GL_NUM_COMPATIBLE_SUBROUTINES:return NUM_COMPATIBLE_SUBROUTINES;
		case GL4.GL_COMPATIBLE_SUBROUTINES:return COMPATIBLE_SUBROUTINES;
		case GL4.GL_TOP_LEVEL_ARRAY_SIZE:return TOP_LEVEL_ARRAY_SIZE;
		case GL4.GL_LOCATION:return LOCATION;
		case GL4.GL_LOCATION_INDEX:return LOCATION_INDEX;
		case GL4.GL_IS_PER_PATCH:return IS_PER_PATCH;
		}
		return null;
	}
}

