package cc.creativecomputing.kle.elements;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.lights.CCLightBrightnessSetup;
import cc.creativecomputing.kle.elements.lights.CCLightChannel;
import cc.creativecomputing.kle.elements.lights.CCLightRGBSetup;
import cc.creativecomputing.kle.elements.lights.CCLightRGBWSetup;
import cc.creativecomputing.kle.elements.lights.CCLightSetup;
import cc.creativecomputing.kle.elements.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC1Motor1ConnectionSetup;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionSetup;
import cc.creativecomputing.kle.elements.motors.CCMotorCalculations;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.kle.elements.motors.CCMotorSetup;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionCalculations;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionSetup;

public class CCSequenceElement extends CCEffectable{
	
	private final List<CCSequenceChannel> _myChannels = new ArrayList<>();

	private final CCMotorSetup _myMotorSetup;
	private final CCLightSetup _myLightSetup;
	
	private CCMatrix4x4 _myMatrix;
	
	public CCSequenceElement(
		int theID, 
		
		List<CCMotorChannel> theMotors,
		List<CCLightChannel> theLights,
		
		CCMotorCalculations<?> theBounds,
		
		CCVector3 theCentroid,
		
		CCMatrix4x4 theTransform,
		
		double theElementRadius
	){
		super(theID);
		_myMotorSetup = setMotors(theMotors, theBounds, theCentroid, theElementRadius);
		_myLightSetup = setLights(theLights);
		_myMatrix = theTransform;
	}
	
	public CCSequenceElement(int theID, List<CCLightChannel> theLights){
		this(theID, null, theLights, null, null, new CCMatrix4x4(), 0);
	}
	
	public CCSequenceElement(int theID, CCLightSetup theLightSetup){
		super(theID);
		_myMotorSetup = null;
		_myLightSetup = theLightSetup;
	}
	
	public CCSequenceElement(int theID, CCMotorSetup theMotorSetup){
		super(theID);
		_myMotorSetup = theMotorSetup;
		_myLightSetup = null;
	}
	
	public CCSequenceElement(int theID){
		super(theID);
		_myMotorSetup = null;
		_myLightSetup = null;
	}
	
	public CCMatrix4x4 matrix(){
		return _myMatrix;
	}
	
	private CCMotorSetup setMotors(List<CCMotorChannel> theMotors, CCMotorCalculations<?> theBounds, CCVector3 theCentroid, double theElementRadius){
		if(theMotors == null)return new CCMotorSetup(theMotors, theCentroid);
		
		_myChannels.addAll(theMotors);
		
		switch(theMotors.size()){
		case 2:
			if(theMotors.get(0).connectionPosition().equals(theMotors.get(1).connectionPosition())){
				return new CC2Motor1ConnectionSetup(theMotors, (CC2Motor1ConnectionBounds)theBounds, theElementRadius);
			}else{
				return new CC2Motor2ConnectionSetup(this, theMotors, (CC2Motor2ConnectionCalculations)theBounds, theCentroid,  theElementRadius);
			}
		case 1:
			return new CC1Motor1ConnectionSetup(theMotors, (CC1Motor1ConnectionBounds)theBounds, theCentroid);
		default:;
			return new CCMotorSetup(theMotors, theCentroid);
		}
	}
	
	private CCLightSetup setLights(List<CCLightChannel> theLights){
		if(theLights == null)return new CCLightSetup(theLights);
			
		_myChannels.addAll(theLights);
		
		switch(theLights.size()){
		case 1:
			return new CCLightBrightnessSetup(theLights);
		case 3:
			return new CCLightRGBSetup(theLights);
		case 4:
			return new CCLightRGBWSetup(theLights);
		default:
			return new CCLightSetup(theLights);
		}
	}
	
	public CCMotorSetup motorSetup(){
		return _myMotorSetup;
	}
	
	public CCLightSetup lightSetup(){
		return _myLightSetup;
	}
	
	public CCChannelSetup<?> setup(CCKleChannelType theChannelType){
		switch(theChannelType){
		case LIGHTS:
			return _myLightSetup;
		case MOTORS:
			return _myMotorSetup;
		}
		return _myMotorSetup;
	}
	
	public void update(double theDeltaTime){
		if(_myMotorSetup != null)_myMotorSetup.update(theDeltaTime);
	}
	
	public List<CCSequenceChannel> channels(){
		return _myChannels;
	}
	
	public CCXMLElement toXML(){
		CCXMLElement myResult = new CCXMLElement("element");
		myResult.addAttribute("id", _myID);
		myResult.addChild(_myMotorSetup.toXML());
		myResult.addChild(_myLightSetup.toXML());
		CCXMLElement myMatrixXML = myResult.createChild("matrix");
		myMatrixXML.addAttribute("m00", _myMatrix.m00);
		myMatrixXML.addAttribute("m01", _myMatrix.m01);
		myMatrixXML.addAttribute("m02", _myMatrix.m02);
		myMatrixXML.addAttribute("m03", _myMatrix.m03);

		myMatrixXML.addAttribute("m10", _myMatrix.m10);
		myMatrixXML.addAttribute("m11", _myMatrix.m11);
		myMatrixXML.addAttribute("m12", _myMatrix.m12);
		myMatrixXML.addAttribute("m13", _myMatrix.m13);

		myMatrixXML.addAttribute("m20", _myMatrix.m20);
		myMatrixXML.addAttribute("m21", _myMatrix.m21);
		myMatrixXML.addAttribute("m22", _myMatrix.m22);
		myMatrixXML.addAttribute("m23", _myMatrix.m23);

		myMatrixXML.addAttribute("m30", _myMatrix.m30);
		myMatrixXML.addAttribute("m31", _myMatrix.m31);
		myMatrixXML.addAttribute("m32", _myMatrix.m32);
		myMatrixXML.addAttribute("m33", _myMatrix.m33);
		
		return myResult;
	}
}
