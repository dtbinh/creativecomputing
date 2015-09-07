package cc.creativecomputing.gl4;

import cc.creativecomputing.image.CCPixelType;

import com.jogamp.opengl.GL4;

public enum GLPixelDataType {
	UNSIGNED_BYTE(GL4.GL_UNSIGNED_BYTE),
	BYTE(GL4.GL_BYTE),
	UNSIGNED_SHORT(GL4.GL_UNSIGNED_SHORT),
	SHORT(GL4.GL_SHORT),
	UNSIGNED_INT(GL4.GL_UNSIGNED_INT),
	INT(GL4.GL_INT),
	HALF_FLOAT(GL4.GL_HALF_FLOAT),
	FLOAT(GL4.GL_FLOAT),
	UNSIGNED_BYTE_3_3_2(GL4.GL_UNSIGNED_BYTE_3_3_2),
	UNSIGNED_BYTE_2_3_3_REV(GL4.GL_UNSIGNED_BYTE_2_3_3_REV),
	UNSIGNED_SHORT_5_6_5(GL4.GL_UNSIGNED_SHORT_5_6_5),
	UNSIGNED_SHORT_5_6_5_REV(GL4.GL_UNSIGNED_SHORT_5_6_5_REV),
	UNSIGNED_SHORT_4_4_4_4(GL4.GL_UNSIGNED_SHORT_4_4_4_4),
	UNSIGNED_SHORT_4_4_4_4_REV(GL4.GL_UNSIGNED_SHORT_4_4_4_4_REV),
	UNSIGNED_SHORT_5_5_5_1(GL4.GL_UNSIGNED_SHORT_5_5_5_1),
	UNSIGNED_SHORT_1_5_5_5_REV(GL4.GL_UNSIGNED_SHORT_1_5_5_5_REV),
	UNSIGNED_INT_8_8_8_8(GL4.GL_UNSIGNED_INT_8_8_8_8),
	UNSIGNED_INT_8_8_8_8_REV(GL4.GL_UNSIGNED_INT_8_8_8_8_REV),
	UNSIGNED_INT_10_10_10_2(GL4.GL_UNSIGNED_INT_10_10_10_2),
	UNSIGNED_INT_2_10_10_10_REV(GL4.GL_UNSIGNED_INT_2_10_10_10_REV),
	UNSIGNED_INT_24_8(GL4.GL_UNSIGNED_INT_24_8),
	UNSIGNED_INT_10F_11F_11F_REV(GL4.GL_UNSIGNED_INT_10F_11F_11F_REV),
	UNSIGNED_INT_5_9_9_9_REV(GL4.GL_UNSIGNED_INT_5_9_9_9_REV),
	FLOAT_32_UNSIGNED_INT_24_8_REV(GL4.GL_FLOAT_32_UNSIGNED_INT_24_8_REV);
	
	private int _myGLID;
	
	private GLPixelDataType(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLPixelDataType fromCC(CCPixelType theType){
		switch(theType){
		case UNSIGNED_BYTE:return UNSIGNED_BYTE;
		case BYTE:return BYTE;
		case UNSIGNED_SHORT:return UNSIGNED_SHORT;
		case SHORT:return SHORT;
		case UNSIGNED_INT:return UNSIGNED_INT;
		case INT:return INT;
		case HALF_FLOAT:return HALF_FLOAT;
		case FLOAT:return FLOAT;
		case UNSIGNED_BYTE_3_3_2:return UNSIGNED_BYTE_3_3_2;
		case UNSIGNED_BYTE_2_3_3_REV:return UNSIGNED_BYTE_2_3_3_REV;
		case UNSIGNED_SHORT_5_6_5:return UNSIGNED_SHORT_5_6_5;
		case UNSIGNED_SHORT_5_6_5_REV:return UNSIGNED_SHORT_5_6_5_REV;
		case UNSIGNED_SHORT_4_4_4_4:return UNSIGNED_SHORT_4_4_4_4;
		case UNSIGNED_SHORT_4_4_4_4_REV:return UNSIGNED_SHORT_4_4_4_4_REV;
		case UNSIGNED_SHORT_5_5_5_1:return UNSIGNED_SHORT_5_5_5_1;
		case UNSIGNED_SHORT_1_5_5_5_REV:return UNSIGNED_SHORT_1_5_5_5_REV;
		case UNSIGNED_INT_8_8_8_8:return UNSIGNED_INT_8_8_8_8;
		case UNSIGNED_INT_8_8_8_8_REV:return UNSIGNED_INT_8_8_8_8_REV;
		case UNSIGNED_INT_10_10_10_2:return UNSIGNED_INT_10_10_10_2;
		case UNSIGNED_INT_2_10_10_10_REV:return UNSIGNED_INT_2_10_10_10_REV;
		case UNSIGNED_INT_24_8:return UNSIGNED_INT_24_8;
		case UNSIGNED_INT_10F_11F_11F_REV:return UNSIGNED_INT_10F_11F_11F_REV;
		case UNSIGNED_INT_5_9_9_9_REV:return UNSIGNED_INT_5_9_9_9_REV;
		case FLOAT_32_UNSIGNED_INT_24_8_REV:return FLOAT_32_UNSIGNED_INT_24_8_REV;
		}
		return null;
	}
	
	public static GLPixelDataType fromGLID(int theGLID){
		switch(theGLID){
		case GL4.GL_UNSIGNED_BYTE:return UNSIGNED_BYTE;
		case GL4.GL_BYTE:return BYTE;
		case GL4.GL_UNSIGNED_SHORT:return UNSIGNED_SHORT;
		case GL4.GL_SHORT:return SHORT;
		case GL4.GL_UNSIGNED_INT:return UNSIGNED_INT;
		case GL4.GL_INT:return INT;
		case GL4.GL_HALF_FLOAT:return HALF_FLOAT;
		case GL4.GL_FLOAT:return FLOAT;
		case GL4.GL_UNSIGNED_BYTE_3_3_2:return UNSIGNED_BYTE_3_3_2;
		case GL4.GL_UNSIGNED_BYTE_2_3_3_REV:return UNSIGNED_BYTE_2_3_3_REV;
		case GL4.GL_UNSIGNED_SHORT_5_6_5:return UNSIGNED_SHORT_5_6_5;
		case GL4.GL_UNSIGNED_SHORT_5_6_5_REV:return UNSIGNED_SHORT_5_6_5_REV;
		case GL4.GL_UNSIGNED_SHORT_4_4_4_4:return UNSIGNED_SHORT_4_4_4_4;
		case GL4.GL_UNSIGNED_SHORT_4_4_4_4_REV:return UNSIGNED_SHORT_4_4_4_4_REV;
		case GL4.GL_UNSIGNED_SHORT_5_5_5_1:return UNSIGNED_SHORT_5_5_5_1;
		case GL4.GL_UNSIGNED_SHORT_1_5_5_5_REV:return UNSIGNED_SHORT_1_5_5_5_REV;
		case GL4.GL_UNSIGNED_INT_8_8_8_8:return UNSIGNED_INT_8_8_8_8;
		case GL4.GL_UNSIGNED_INT_8_8_8_8_REV:return UNSIGNED_INT_8_8_8_8_REV;
		case GL4.GL_UNSIGNED_INT_10_10_10_2:return UNSIGNED_INT_10_10_10_2;
		case GL4.GL_UNSIGNED_INT_2_10_10_10_REV:return UNSIGNED_INT_2_10_10_10_REV;
		case GL4.GL_UNSIGNED_INT_24_8:return UNSIGNED_INT_24_8;
		case GL4.GL_UNSIGNED_INT_10F_11F_11F_REV:return UNSIGNED_INT_10F_11F_11F_REV;
		case GL4.GL_UNSIGNED_INT_5_9_9_9_REV:return UNSIGNED_INT_5_9_9_9_REV;
		case GL4.GL_FLOAT_32_UNSIGNED_INT_24_8_REV:return FLOAT_32_UNSIGNED_INT_24_8_REV;
		}
		return null;
	}
}

