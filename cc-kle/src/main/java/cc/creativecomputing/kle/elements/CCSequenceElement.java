package cc.creativecomputing.kle.elements;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.lights.CCLightBrightnessSetup;
import cc.creativecomputing.kle.elements.lights.CCLightChannel;
import cc.creativecomputing.kle.elements.lights.CCLightRGBSetup;
import cc.creativecomputing.kle.elements.lights.CCLightSetup;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionSetup;
import cc.creativecomputing.kle.elements.motors.CCMotorBounds;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.kle.elements.motors.CCMotorSetup;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionSetup;

public class CCSequenceElement  {

	private final int _myID;
	
	private final List<CCSequenceChannel> _myChannels = new ArrayList<>();

	private final CCMotorSetup _myMotorSetup;
	private final CCLightSetup _myLightSetup;
	
	private double _myIDBlend;
	private double _myGroupIDBlend;
	private double _myGroupBlend;
	private int _myGroup;

	public double _myXBlend;
	public double _myYBlend;
	
	public CCSequenceElement(
		int theID, 
		
		List<CCMotorChannel> theMotors,
		List<CCLightChannel> theLights,
		
		CCMotorBounds theBounds,
		
		double theElementRadius
	){
		_myID = theID;
		_myChannels.addAll(theMotors);
		
		switch(theMotors.size()){
		case 2:
			if(theMotors.get(0).connectionPosition().equals(theMotors.get(1).connectionPosition())){
				_myMotorSetup = new CC2Motor1ConnectionSetup(theMotors, (CC2Motor1ConnectionBounds)theBounds, theElementRadius);
			}else{
				_myMotorSetup = new CC2Motor2ConnectionSetup(theMotors, (CC2Motor2ConnectionBounds)theBounds, theElementRadius);
			}
			
			break;
		default:
			_myMotorSetup = new CCMotorSetup(theMotors);
		}
		

		_myChannels.addAll(theLights);
		switch(theLights.size()){
		case 1:
			_myLightSetup = new CCLightBrightnessSetup(theLights);
			break;
		case 3:
			_myLightSetup = new CCLightRGBSetup(theLights);
			break;
		default:
			_myLightSetup = new CCLightSetup(theLights);
		}
	}
	
	public double groupIDBlend(){
		return _myGroupIDBlend;
	}
	
	public void groupIDBlend(double theGroupIDBlend){
		_myGroupIDBlend = theGroupIDBlend;
	}
	
	public double groupBlend(){
		return _myGroupBlend;
	}
	
	public void groupBlend(double theGroupBlend){
		_myGroupBlend = theGroupBlend;
	}
	
	public int group(){
		return _myGroup;
	}
	
	public double xBlend(){
		return _myXBlend;
	}
	
	public void xBlend(double theXBlend){
		_myXBlend = theXBlend;
	}
	
	public double yBlend(){
		return _myYBlend;
	}
	
	public void yBlend(double theYBlend){
		_myYBlend = theYBlend;
	}
	
	public int id(){
		return _myID;
	}
	
	public double idBlend(){
		return _myIDBlend;
	}
	
	public void idBlend(double theIDBlend){
		_myIDBlend = theIDBlend;
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
	
	public List<CCSequenceChannel> channels(){
		return _myChannels;
	}
	
	public CCXMLElement toXML(){
		CCXMLElement myResult = new CCXMLElement("element");
		myResult.addAttribute("id", _myID);
		myResult.addChild(_myMotorSetup.toXML());
		myResult.addChild(_myLightSetup.toXML());
		
		return myResult;
	}
}
