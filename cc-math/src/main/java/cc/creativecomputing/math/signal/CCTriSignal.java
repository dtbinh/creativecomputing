/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.math.signal;

import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCTriSignal extends CCSignal{
	
	private double triValue(double theInput){
		if(theInput < 0){
			theInput = -theInput + 0.5f;
		}
		theInput = (theInput + 0.25f) * 2 % 2;
		if(theInput < 1){
			return theInput;
		}
		return 2 - theInput;
	}

	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return new double[]{CCMath.average(triValue(theX),triValue(theY))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY) {
		return new double[]{CCMath.average(triValue(theX),triValue(theY))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double)
	 */
	@Override
	public double[] signalImpl(double theX) {
		return new double[]{triValue(theX)};
	}

}