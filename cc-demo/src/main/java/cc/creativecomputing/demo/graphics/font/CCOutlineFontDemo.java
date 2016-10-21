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
package cc.creativecomputing.demo.graphics.font;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCTextAlign;


public class CCOutlineFontDemo extends CCGL2Adapter{

	CCFont<?> font0;
	CCFont<?> font1;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		font0 = CCFontIO.createOutlineFont("Arial",48, 30);
		font1 = CCFontIO.createVectorFont("Arial",48);
		
		g.textAlign(CCTextAlign.CENTER);
		g.clearColor(1f,0,0);
		g.bezierDetail(31);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.color(255);

		g.textFont(font1);
		g.textSize(192);
		g.text("texone",0,0);
		

		g.strokeWeight(2);
		g.color(0);

		g.textFont(font0);
		g.textSize(192);
		g.text("texone",0,0);
	}
	
	public static void main(String[] args) {
		CCOutlineFontDemo demo = new CCOutlineFontDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
