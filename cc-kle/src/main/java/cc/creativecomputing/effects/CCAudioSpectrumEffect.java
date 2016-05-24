package cc.creativecomputing.effects;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioAsset;

public class CCAudioSpectrumEffect extends CCEffect{
	
	private CCAudioAsset _myAudioAsset;
	
	@CCProperty(name = "apply", min = 0, max = 1)
	private Map<String, Double> _cApplies = new LinkedHashMap<>();
	@CCProperty(name = "default", min = 0, max = 1)
	private Map<String, Double> _cDefaults = new LinkedHashMap<>();
	
	private Map<String, CCAudioAsset>_myAssetMap = new HashMap<>();
	
	@CCProperty(name = "clip")
	private CCSelection _mySelection = new CCSelection();
	
	public CCAudioSpectrumEffect(){
	}
	
	public void addAsset(String theName, CCAudioAsset theAsset){
		_myAssetMap.put(theName, theAsset);
		_mySelection.add(theName);
	}
	
	@Override
	public void valueNames(String... theValueNames) {
		super.valueNames(theValueNames);
		
		for(String myValueName:_myValueNames){
			CCLog.info(myValueName);
			_cApplies.put(myValueName, 0d);
			_cDefaults.put(myValueName, 1d);
		}
	}
	
	public void update(final double theDeltaTime){
		_myAudioAsset = _myAssetMap.get(_mySelection.value());
		if(_myAudioAsset == null)return;
		if(_myAudioAsset.value() == null)return;
	}
	
	public double[] applyTo(CCEffectable theElement){
		double myBlend = elementBlend(theElement);
		double[] myResult = new double[_myValueNames.length];
		if(_myAudioAsset == null || _myAudioAsset.value() == null)return myResult;
		for(int i = 0; i < _myValueNames.length;i++){
			double myNameBlend = _cApplies.get(_myValueNames[i]);
			double mySpectrum = _myAudioAsset.spectrum(_cModulations.get(_myValueNames[i]).modulation(theElement));
			myResult[i] = CCMath.blend(_cDefaults.get(_myValueNames[i]), mySpectrum, myNameBlend) * myBlend;
		}
		return myResult;
	}
}