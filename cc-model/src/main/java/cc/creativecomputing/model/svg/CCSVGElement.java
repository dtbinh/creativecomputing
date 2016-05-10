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
package cc.creativecomputing.model.svg;

import java.awt.Paint;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.util.CCTriangulator;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.CCStrokeCap;
import cc.creativecomputing.model.CCStrokeJoin;


public abstract class CCSVGElement {

	public static enum CCShapeKind{
		LINE, ELLIPSE, RECT, POLYGON, PATH, GROUP, DEF
	}
	
	protected CCShapeKind _myKind;
	
	public static enum CCShapeFamily{
		GROUP, PRIMITIVE, PATH
	}

	/** The shape type, one of GROUP, PRIMITIVE, PATH, or GEOMETRY. */
	protected CCShapeFamily family;
	
	protected CCSVGGroup parent;
	
	protected String _myName;
	
	protected CCMatrix32 matrix;
	
	// set to false if the object is hidden in the layers palette
	protected boolean visible = true;
	
	protected boolean stroke;
	protected CCColor strokeColor;
	protected double strokeWeight; // default is 1
	protected CCStrokeCap strokeCap;
	protected CCStrokeJoin strokeJoin;
	CCSVGGradient strokeGradient;
	Paint strokeGradientPaint;
	String strokeName; // id of another object, gradients only?

	protected boolean fill;
	protected CCColor fillColor;
	CCSVGGradient fillGradient;
	Paint fillGradientPaint;
	String fillName; // id of another object
	
	/** True if this is a closed path. */
	protected boolean close;
	
	double opacity;
	double strokeOpacity;
	double fillOpacity;
	
	public CCSVGElement(CCSVGGroup theParent){
		parent = theParent;
		
		if (parent == null) {
			// set values to their defaults according to the SVG spec
			stroke = false;
			strokeColor = CCColor.BLACK.clone();
			strokeWeight = 1;
//			strokeCap = PConstants.SQUARE; // equivalent to BUTT in svg spec
//			strokeJoin = PConstants.MITER;
			strokeGradient = null;
			strokeGradientPaint = null;
			strokeName = null;

			fill = true;
			fillColor = CCColor.BLACK.clone();
			fillGradient = null;
			fillGradientPaint = null;
			fillName = null;

			// hasTransform = false;
			// transformation = null; //new double[] { 1, 0, 0, 1, 0, 0 };

			strokeOpacity = 1;
			fillOpacity = 1;
			opacity = 1;

		} else {
			stroke = parent.stroke;
			strokeColor = parent.strokeColor.clone();
			strokeWeight = parent.strokeWeight;
			strokeCap = parent.strokeCap;
			strokeJoin = parent.strokeJoin;
			strokeGradient = parent.strokeGradient;
			strokeGradientPaint = parent.strokeGradientPaint;
			strokeName = parent.strokeName;

			fill = parent.fill;
			fillColor = parent.fillColor.clone();
			fillGradient = parent.fillGradient;
			fillGradientPaint = parent.fillGradientPaint;
			fillName = parent.fillName;

			// hasTransform = parent.hasTransform;
			// transformation = parent.transformation;

			opacity = parent.opacity;
		}
	}
	
	public void kind(CCShapeKind theKind) {
		_myKind = theKind;
	}
	
	public CCShapeKind kind(){
		return _myKind;
	}

	public void name(String theName) {
		_myName = theName;
	}

	public String name() {
		return _myName;
	}
	
	public void opacity(double theOpacity){
		opacity = theOpacity;
		strokeColor.a = theOpacity;
		fillColor.a = theOpacity;
	}
	
	public void fillOpacity(double theOpacity){
		opacity = theOpacity;
		fillColor.a = theOpacity;
	}
	
	public void strokeOpacity(double theOpacity){
		opacity = theOpacity;
		strokeColor.a = theOpacity;
	}
	
	public abstract void drawImplementation(CCGraphics g, boolean theFill);
	
	public void draw(CCGraphics g){
		if(matrix != null){
			g.pushMatrix();
			g.applyMatrix(matrix);
		}
		g.pushAttribute();

		if (fill) {
			// System.out.println("filling " + PApplet.hex(fillColor));
			g.color(fillColor);
			drawImplementation(g, true);
		} 
		if (stroke) {
			g.color(strokeColor);
			g.strokeWeight(strokeWeight);
			drawImplementation(g, false);
		} 
		
		g.popAttribute();
		if(matrix != null){
			g.popMatrix();
		}
	}
	
	private CCTriangulator _myTriangulator = null;
	

	protected void draw(CCGraphics g, CCLinearSpline theSpline, boolean theFill) {
		if(theFill){
			if(_myTriangulator == null){
				_myTriangulator = new CCTriangulator();
				_myTriangulator.beginPolygon();
				for(CCVector3 myPoint:theSpline.points()){
					_myTriangulator.vertex(myPoint);
				}
				_myTriangulator.endPolygon();
			}
			g.beginShape(CCDrawMode.TRIANGLES);
			for(CCVector3 myVertex:_myTriangulator.vertices()){
				g.vertex(myVertex);
			}
			g.endShape();
		}else{
			if(theSpline.isClosed())g.beginShape(CCDrawMode.LINE_LOOP);
			else g.beginShape(CCDrawMode.LINE_STRIP);
			for(CCVector3 myPoint:theSpline.points()){
				g.vertex(myPoint);
			}
			g.endShape();
		}
	}
	
	public abstract List<CCLinearSpline> contours();
}