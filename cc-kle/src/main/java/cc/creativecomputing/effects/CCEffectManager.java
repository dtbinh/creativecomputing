package cc.creativecomputing.effects;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.filter.CCFilter;

public class CCEffectManager<Type extends CCEffectable> extends LinkedHashMap<String, CCEffect> implements CCAnimatorListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	@CCProperty(name = "animation blender")
	protected final CCEffectBlender _myAnimationBlender;
	
	protected final CCEffectables<Type> _myEffectables;
	
	private final String[] _myValueNames;
	
	@CCProperty(name = "scales", min = 0, max = 3, defaultValue = 1)
	private final Map<String, Double> _cScales = new LinkedHashMap<>();
	
	@CCProperty(name = "filters")
	private final Map<String, CCFilter> _cFilters = new LinkedHashMap<>();
	
	@CCProperty(name = "amount animations")
	private final Map<String, CCEffect> _cAmountAnimation = new LinkedHashMap<>();
	
	@CCProperty(name = "normalize")
	private boolean _cNormalize = false;
	@CCProperty(name = "bypass amount")
	private boolean _cBypassAmount = false;
	@CCProperty(name = "end scale")
	private double _scale = 1;
	@CCProperty(name = "end add")
	private double _add = 0;
	
	
	
	
	public CCEffectManager(CCEffectables<Type> theEffectables, String...theValueNames){
		_myEffectables = theEffectables;
		_myAnimationBlender = new CCEffectBlender(theEffectables);
		_myValueNames = theValueNames;
		_cScales.put("global", 1.0);
		for(String myValueName:_myValueNames){
			_cScales.put(myValueName + " scale", 1.0);
		}
		
		for(String myIdSource:theEffectables.idSources()){
			theEffectables.addRelativeSources(myIdSource);
		}
		for(CCEffectable myEffectable:theEffectables){
			for(String myIdSource:theEffectables.idSources()){
				myEffectable.addRelativeSource(myIdSource, myEffectable.idSource(myIdSource) / (double)theEffectables.idMax(myIdSource));
			}
		}
		
	}
	
	@Override
	public CCEffect put(String theKey, CCEffect theEffect) {
		theEffect.addGroupBlends(_myEffectables.groups());
		theEffect.valueNames(_myEffectables, _myValueNames);
		return super.put(theKey, theEffect);
	}
	
	public CCEffect amountAnimation(String theKey, CCEffect theEffect) {
		theEffect.addGroupBlends(_myEffectables.groups());
		theEffect.valueNames(_myEffectables, "amount");
		return _cAmountAnimation.put(theKey, theEffect);
	}
	
	public void addFilter(String theKey, CCFilter theFilter){
		theFilter.channels(_myValueNames.length * _myEffectables.size());
		_cFilters.put(theKey, theFilter);
	}
	
	public void apply(Type theEffectable, double[]theValues){
		theEffectable.apply(theValues);
	}

	@Override
	public void update(CCAnimator theAnimator){
		double myBlendSumA = 0;
		double myBlendSumB = 0;
		
		for(CCEffect myEffect:values()){
			myEffect.update(theAnimator.deltaTime());
			myBlendSumA += myEffect.blend() * (1 - myEffect.channelBlend());
			myBlendSumB += myEffect.blend() * myEffect.channelBlend();
		}
//		double myCenter = 0;
		int index = 0;
		for(Type myEffectable:_myEffectables){
			myEffectable.parameters(_myValueNames);
			double[] myValueA = new double[_myValueNames.length];
			double[] myValueB = new double[_myValueNames.length];
			for(CCEffect myEffect:values()){
				double[] myValues = myEffect.applyTo(myEffectable);
				
				for(int i = 0; i < myValues.length;i++){
					double myValue = myValues[i];
					if(Double.isNaN(myValue))continue;
					
					myValueA[i] += myValue * (1 - myEffect.channelBlend());
					myValueB[i] += myValue * (myEffect.channelBlend());
				}
			}
			
			if(_cNormalize){
				for(int i = 0; i < myValueA.length;i++){
					myValueA[i] = myBlendSumA == 0 ? 1 : myValueA[i] / myBlendSumA;
					myValueB[i] = myBlendSumB == 0 ? 1 : myValueB[i] / myBlendSumB;
				}
			}
			double myAmountValue = _cAmountAnimation.size() == 0 || _cBypassAmount ? 1 : 0;
			if(!_cBypassAmount){
				for(CCEffect myAnimation:_cAmountAnimation.values()){
					double[] myValues = myAnimation.applyTo(myEffectable);
					
					double myValue = myValues[0];
					if(Double.isNaN(myValue))continue;
						
					myAmountValue+= myValue;
				}
			}
			
			double[] myValues = _myAnimationBlender.blend(myEffectable, myValueA, myValueB);
//			myElement.motorSetup().rotateZ(CCMath.sign(myTranslation.x) * CCMath.pow(CCMath.abs(myTranslation.x), _cRotationPow) * _cRotationAngle);
			double myGlobalScale = _cScales.get("global");
			for(int i = 0; i < myValues.length;i++){
				String myValueName = _myValueNames[i];
				
				myValues[i] = myValues[i] * _cScales.get(myValueName + " scale") * myAmountValue * _scale * myGlobalScale + _add;
				for(CCFilter myFilter:_cFilters.values()){
					myValues[i] = myFilter.process(index, myValues[i], theAnimator.deltaTime());
				}
				index++;
			}
			
			apply(myEffectable, myValues);
		}
	}
	
	@Override
	public void start(CCAnimator theAnimator) {}
	
	@Override
	public void stop(CCAnimator theAnimator) {}
}
