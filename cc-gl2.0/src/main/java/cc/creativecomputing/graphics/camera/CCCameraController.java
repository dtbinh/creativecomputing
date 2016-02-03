/*
   The PeasyCam Processing library, which provides an easy-peasy
   camera for 3D sketching.
  
   Copyright 2008 Jonathan Feinberg

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package cc.creativecomputing.graphics.camera;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLListener;
import cc.creativecomputing.gl.app.events.CCKeyAdapter;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.gl.app.events.CCMouseAdapter;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.gl.app.events.CCMouseEvent.CCMouseButton;
import cc.creativecomputing.gl.app.events.CCMouseWheelEvent;
import cc.creativecomputing.gl.app.events.CCMouseWheelListener;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCVector3;

/**
 * 
 * @author Jonathan Feinberg
 */
public class CCCameraController {
	
	private static class InterpolationManager {
		private AbstractInterp currentInterpolator = null;

		protected synchronized void startInterpolation(final AbstractInterp interpolation) {
			cancelInterpolation();
			currentInterpolator = interpolation;
			currentInterpolator.start();
		}

		protected synchronized void cancelInterpolation() {
			if (currentInterpolator != null) {
				currentInterpolator.cancel();
				currentInterpolator = null;
			}
		}

	}
	
	private static interface CCCameraMouseDragHandler {
		public void handleDrag(final double theMoveX, final double theMoveY, double theMouseX, double theMouseY);
	}
	
	private abstract class CCDampedAction extends CCAnimatorAdapter{
		private double _myVelocity;
		private final double _myDamping;

		public CCDampedAction() {
			this(0.16f);
		}

		public CCDampedAction(final double theFriction) {
			_myVelocity = 0;
			_myDamping = 1.0f - theFriction;
			_myApp.animator().listener().add(this);
		}

		public void impulse(final double theImpulse) {
			_myVelocity += theImpulse;
		}

		public void update(CCAnimator theAnimator) {
			if (_myVelocity == 0) {
				return;
			}
			behave(_myVelocity);
//			feed();
			_myVelocity *= _myDamping;
			if (CCMath.abs(_myVelocity) < .001f) {
				_myVelocity = 0;
			}
		}

		public void stop() {
			_myVelocity = 0;
		}

		abstract protected void behave(final double theVelocity);
	}
	
	public static class CCCameraState  {
		final CCQuaternion _myRotation;
		final CCVector3 _myCenter;
		final double _myDistance;

		public CCCameraState(final CCQuaternion theRotation, final CCVector3 theCenter, final double theDistance) {
			_myRotation = theRotation;
			_myCenter = theCenter;
			_myDistance = theDistance;
		}

	}
	
	private static final CCVector3 LOOK = CCVector3.UNIT_Z;
	private static final CCVector3 UP = CCVector3.UNIT_Y;

	private static enum Constraint {
		YAW, PITCH, ROLL, SUPPRESS_ROLL
	}

	private final CCGraphics g;
	private final CCGL2Adapter _myApp;

	private final double _myStartDistance;
	private final CCVector3 _myStartCenter;

	private boolean resetOnDoubleClick = true;
	private double minimumDistance = 1;
	private double maximumDistance = Float.MAX_VALUE;

	private final CCDampedAction _myRotateXAction;
	private final CCDampedAction _myRotateYAction;
	private final CCDampedAction _myRotateZAction;
	
	private final CCDampedAction _myDampedZoom;
	private final CCDampedAction _myDampedPanX;
	private final CCDampedAction _myDampedPanY;

	@CCProperty(name = "distance", min = 1, max = 20000, readBack = true)
	private double _myDistance;
	@CCProperty(name = "center", min = -10000, max = 10000, readBack = true)
	private CCVector3 _myCenter;
	@CCProperty(name = "rotation", min = -1, max = 1, readBack = true)
	private CCQuaternion _myRotation;

	private Constraint _myDragConstraint = null;
	private Constraint permaConstraint = null;

	private final InterpolationManager rotationInterps = new InterpolationManager();
	private final InterpolationManager centerInterps = new InterpolationManager();
	private final InterpolationManager distanceInterps = new InterpolationManager();

	private final CCCameraMouseDragHandler panHandler = new CCCameraMouseDragHandler() {
		public void handleDrag(final double theMoveX, final double theMoveY, double theMouseX, double theMouseY) {
			_myDampedPanX.impulse(theMoveX / 8f);
			_myDampedPanY.impulse(theMoveY / 8f);
		}
	};
	
	private CCCameraMouseDragHandler centerDragHandler = panHandler;

	private final CCCameraMouseDragHandler rotateHandler = new CCCameraMouseDragHandler() {
		public void handleDrag(final double theMoveX, final double theMoveY, double theMouseX, double theMouseY) {
			mouseRotate(theMoveX, theMoveY, theMouseX, theMouseY);
		}
	};
	private CCCameraMouseDragHandler leftDragHandler = rotateHandler;

	private final CCCameraMouseDragHandler zoomHandler = new CCCameraMouseDragHandler() {
		public void handleDrag(final double theMoveX, final double theMoveY, double theMouseX, double theMouseY) {
			_myDampedZoom.impulse(theMoveY / 10f);
		}
	};
	private CCCameraMouseDragHandler rightDraghandler = zoomHandler;

	private final CCMouseWheelListener _myWheelHandler = new CCMouseWheelListener() {

		@Override
		public void mouseWheelMoved(CCMouseWheelEvent theThe) {
			_myDampedZoom.impulse(_myWheelScale * theThe.rotation());
		}
	};
	
	private double _myWheelScale = 1f;

	private final CCCameraMouseListener _myMouseListener = new CCCameraMouseListener();
	private final CCCameraKeyListener _myKeyListener = new CCCameraKeyListener();
	private boolean _myIsActive = false;
	
	private CCCamera _myCamera;

	public CCCameraController(final CCGL2Adapter theApp, final CCGraphics theG, final double theDistance) {
		this(theApp, theG, 0, 0, 0, theDistance);
	}

	public CCCameraController(
		final CCGL2Adapter theApp, 
		final CCGraphics theG,  
		final double theLookAtX, final double theLookAtY, final double theLookAtZ, 
		final double theDistance
	) {
		_myApp = theApp;
		
		_myApp.animator().listener().add(new CCAnimatorAdapter() {
			@Override
			public void update(CCAnimator theAnimator) {
				feed();
			}
		});
		
		_myApp.glContext().listener().add(new CCGLListener<CCGraphics>() {

			@Override
			public void reshape(CCGraphics theContext) {
				_myCamera = new CCCamera(theContext);
				_myCamera.viewport(new CCViewport(0, 0, theContext.width(), theContext.height()));
			}

			@Override
			public void init(CCGraphics theContext) {}

			@Override
			public void dispose(CCGraphics theContext) {}

			@Override
			public void display(CCGraphics theContext) {}
		});
		g  = theG;
		_myStartCenter = new CCVector3(theLookAtX, theLookAtY, theLookAtZ);
		_myCenter = new CCVector3(theLookAtX, theLookAtY, theLookAtZ);
		_myStartDistance = _myDistance = theDistance;
		_myRotation = new CCQuaternion();
		
		_myCamera = new CCCamera(theG);

		feed();

		_myRotateXAction = new CCDampedAction() {
			@Override
			protected void behave(final double theVelocity) {
				_myRotation.multiplyLocal(CCQuaternion.createFromAngleAxis(theVelocity, CCVector3.UNIT_X ));
			}
		};

		_myRotateYAction = new CCDampedAction() {
			@Override
			protected void behave(final double theVelocity) {
				_myRotation.multiplyLocal(CCQuaternion.createFromAngleAxis(theVelocity, CCVector3.UNIT_Y));
			}
		};

		_myRotateZAction = new CCDampedAction() {
			@Override
			protected void behave(final double theVelocity) {
				_myRotation.multiplyLocal(CCQuaternion.createFromAngleAxis(theVelocity, CCVector3.UNIT_Z));
			}
		};

		_myDampedZoom = new CCDampedAction() {
			@Override
			protected void behave(final double theVelocity) {
				mouseZoom(theVelocity);
			}
		};

		_myDampedPanX = new CCDampedAction() {
			@Override
			protected void behave(final double theVelocity) {
				mousePan(theVelocity, 0);
			}
		};

		_myDampedPanY = new CCDampedAction() {
			@Override
			protected void behave(final double theVelocity) {
				mousePan(0, theVelocity);
			}
		};

		setActive(true);
	}
	
	public CCCamera camera(){
		return _myCamera;
	}

	/**
	 * <_myApp>
	 * Turn on or off default mouse-handling behavior:
	 * 
	 * <_myApp>
	 * <table>
	 * <tr>
	 * <td><b>left-drag</b></td>
	 * <td>rotate camera around look-at point</td>
	 * <tr>
	 * <tr>
	 * <td><b>center-drag</b></td>
	 * <td>pan camera (change look-at point)</td>
	 * <tr>
	 * <tr>
	 * <td><b>right-drag</b></td>
	 * <td>zoom</td>
	 * <tr>
	 * <tr>
	 * <td><b>wheel</b></td>
	 * <td>zoom</td>
	 * <tr>
	 * </table>
	 * 
	 * @param isMouseControlled
	 */
	public void setActive(final boolean theIsActive) {
		if (theIsActive == _myIsActive) {
			return;
		}
		_myIsActive = theIsActive;
		if (_myIsActive) {
			_myApp.mouseListener().add(_myMouseListener);
			_myApp.mouseWheelListener().add(_myWheelHandler);
			_myApp.mouseMotionListener().add(_myMouseListener);
			_myApp.keyListener().add(_myKeyListener);
		} else {
			_myApp.mouseListener().remove(_myMouseListener);
			_myApp.mouseWheelListener().remove(_myWheelHandler);
			_myApp.mouseMotionListener().remove(_myMouseListener);
			_myApp.keyListener().remove(_myKeyListener);
		}
	}

	public boolean isActive() {
		return _myIsActive;
	}

	public double wheelScale() {
		return _myWheelScale;
	}

	public void wheelScale(final double theWheelScale) {
		_myWheelScale = theWheelScale;
	}
	
	protected class CCCameraMouseListener extends CCMouseAdapter{
		
		private double _myPMouseX;
		private double _myPMouseY;
		
		@Override
		public void mouseReleased(CCMouseEvent theEvent) {
			_myDragConstraint = null;
		}
		
		@Override
		public void mousePressed(CCMouseEvent theEvent) {
			_myPMouseX = theEvent.x();
			_myPMouseY = theEvent.y();
		}
		
		@Override
		public void mouseClicked(CCMouseEvent theEvent) {
			if (resetOnDoubleClick && 2 == (int)theEvent.clickCount()) {
				reset();
			}
		}
		
		@Override
		public void mouseDragged(CCMouseEvent theMouseEvent) {
			final double theMoveX = theMouseEvent.x() - _myPMouseX;
			final double theMoveY = _myPMouseY - theMouseEvent.y();
			_myPMouseX = theMouseEvent.x();
			_myPMouseY = theMouseEvent.y();

			if (theMouseEvent.isShiftDown()) {
				if (_myDragConstraint == null && Math.abs(theMoveX - theMoveY) > 1) {
					_myDragConstraint = Math.abs(theMoveX) > Math.abs(theMoveY) ? Constraint.YAW : Constraint.PITCH;
				}
			} else if (permaConstraint != null) {
				_myDragConstraint = permaConstraint;
			} else {
				_myDragConstraint = null;
			}

			final CCMouseButton b = theMouseEvent.button();
			if (centerDragHandler != null && (b == CCMouseButton.CENTER || (b == CCMouseButton.LEFT && theMouseEvent.isMetaDown()))) {
				centerDragHandler.handleDrag(theMoveX, theMoveY, theMouseEvent.x(), theMouseEvent.y());
			} else if (leftDragHandler != null && b == CCMouseButton.LEFT) {
				leftDragHandler.handleDrag(theMoveX, theMoveY, theMouseEvent.x(), theMouseEvent.y());
			} else if (rightDraghandler != null && b == CCMouseButton.RIGHT) {
				rightDraghandler.handleDrag(theMoveX, theMoveY, theMouseEvent.x(), theMouseEvent.y());
			}
		}
	}
	
	protected class CCCameraKeyListener extends CCKeyAdapter{
		@Override
		public void keyReleased(CCKeyEvent theKeyEvent) {
			if (theKeyEvent.isShiftDown())
				_myDragConstraint = null;
		}
	}

	private void mouseZoom(final double delta) {
		safeSetDistance(_myDistance + delta * (double)Math.log1p(_myDistance));
	}

	private void mousePan(final double dxMouse, final double dyMouse) {
		final double panScale = CCMath.sqrt(_myDistance * .005f);
		pan(
			_myDragConstraint == Constraint.PITCH ? 0 : -dxMouse * panScale,
			_myDragConstraint == Constraint.YAW ? 0 : -dyMouse * panScale
		);
	}

	private void mouseRotate(final double theMoveX, final double theMoveY, double mouseX, double mouseY) {
		final CCVector3 u = LOOK.multiply(100 + .6f * _myStartDistance).negate();

		final int xSign = theMoveX > 0 ? -1 : 1;
		final int ySign = theMoveY < 0 ? -1 : 1;

		final double eccentricity = CCMath.abs((g.height() / 2f) - mouseY) / (g.height() / 2f);
		final double rho = CCMath.abs((g.width() / 2f) - mouseX) / (g.width() / 2f);

		if (_myDragConstraint == null || _myDragConstraint == Constraint.YAW || _myDragConstraint == Constraint.SUPPRESS_ROLL) {
			final double adx = Math.abs(theMoveX) * (1 - eccentricity);
			final CCVector3 vx = u.add(new CCVector3(adx, 0, 0));
			_myRotateYAction.impulse(CCVector3.angle(u, vx) * xSign * 0.1);
		}
		if (_myDragConstraint == null || _myDragConstraint == Constraint.PITCH || _myDragConstraint == Constraint.SUPPRESS_ROLL) {
			final double ady = Math.abs(theMoveY) * (1 - rho);
			final CCVector3 vy = u.add(new CCVector3(0, ady, 0));
			_myRotateXAction.impulse(CCVector3.angle(u, vy) * ySign * 0.1);
		}
		if (_myDragConstraint == null || _myDragConstraint == Constraint.ROLL) {
			{
				final double adz = Math.abs(theMoveY) * rho;
				final CCVector3 vz = u.add(new CCVector3(0, adz, 0));
				_myRotateZAction.impulse(CCVector3.angle(u, vz) * -ySign * (mouseX < g.width() / 2 ? -1 : 1) * 0.1);
			}
			{
				final double adz = Math.abs(theMoveX) * eccentricity;
				final CCVector3 vz = u.add(new CCVector3(0, adz, 0));
				_myRotateZAction.impulse(CCVector3.angle(u, vz) * xSign * (mouseY > g.height() / 2 ? -1 : 1) * 0.1);
			}
		}
	}

	public double distance() {
		return _myDistance;
	}

	public void distance(final double theNewDistance) {
		distance(theNewDistance, 0.3f);
	}

	public void distance(final double theNewDistance, final double theAnimationTime) {
		distanceInterps.startInterpolation(new DistanceInterp(theNewDistance, theAnimationTime));
	}

	public double[] getLookAt() {
		return new double[] { _myCenter.x, _myCenter.y, _myCenter.z };
	}

	public void lookAt(final double x, final double y, final double z) {
		centerInterps.startInterpolation(new CenterInterp(new CCVector3(x, y, z), 0.3f));
	}

	public void lookAt(final double x, final double y, final double z,
			final double distance) {
		lookAt(x, y, z);
		distance(distance);
	}

	public void lookAt(final double x, final double y, final double z,
			final long animationTimeMillis) {
		lookAt(x, y, z, _myDistance, animationTimeMillis);
	}

	public void lookAt(
		final double x, final double y, final double z,
		final double distance, final long animationTimeMillis
	) {
		setState(new CCCameraState(_myRotation, new CCVector3(x, y, z), distance), animationTimeMillis);
	}

	private void safeSetDistance(final double distance) {
		this._myDistance = Math.min(maximumDistance, Math.max(minimumDistance, distance));
//		feed();
	}

	public void feed() {
		apply(_myCenter, _myRotation, _myDistance);
	}

	public void apply(final CCVector3 center, final CCQuaternion rotation, final double distance) {
		final CCVector3 pos = rotation.apply(LOOK).multiply(distance).add(center);
		final CCVector3 rup = rotation.apply(UP);
		
		_myCamera.position(pos.x, pos.y, pos.z);
		_myCamera.target(center.x, center.y, center.z);
		_myCamera.up(rup.x, rup.y, rup.z);
	}

	/**
	 * Where is the PeasyCam in world space?
	 * 
	 * @return double[]{x,y,z}
	 */
	public double[] getPosition() {
		final CCVector3 pos = _myRotation.apply(LOOK).multiply(_myDistance).add(_myCenter);
		return new double[] { pos.x, pos.y, pos.z };
	}

	public void reset() {
		reset(0.3f);
	}

	public void reset(final double theAnimationTime) {
		setState(new CCCameraState(new CCQuaternion(), _myStartCenter, _myStartDistance), theAnimationTime);
	}

	public void pan(final double theMoveX, final double theMoveY) {
		_myCenter.addLocal(_myRotation.apply(new CCVector3(theMoveX, theMoveY, 0)));
//		feed();
	}

	public void rotateX(final double angle) {
		_myRotation.multiplyLocal(CCQuaternion.createFromAngleAxis(angle, CCVector3.UNIT_X));
	}

	public void rotateY(final double angle) {
		_myRotation.multiplyLocal(CCQuaternion.createFromAngleAxis(angle, CCVector3.UNIT_Y));
	}

	public void rotateZ(final double angle) {
		_myRotation.multiplyLocal(CCQuaternion.createFromAngleAxis(angle, CCVector3.UNIT_Z));
	}

	public CCCameraState getState() {
		return new CCCameraState(_myRotation, _myCenter, _myDistance);
	}

	/**
	 * Permit arbitrary rotation. (Default mode.)
	 */
	public void setFreeRotationMode() {
		permaConstraint = null;
	}

	/**
	 * Only permit yaw.
	 */
	public void setYawRotationMode() {
		permaConstraint = Constraint.YAW;
	}

	/**
	 * Only permit pitch.
	 */
	public void setPitchRotationMode() {
		permaConstraint = Constraint.PITCH;
	}

	/**
	 * Only permit roll.
	 */
	public void setRollRotationMode() {
		permaConstraint = Constraint.ROLL;
	}

	/**
	 * Only suppress roll.
	 */
	public void setSuppressRollRotationMode() {
		permaConstraint = Constraint.SUPPRESS_ROLL;
	}

	public void setMinimumDistance(final double minimumDistance) {
		this.minimumDistance = minimumDistance;
		safeSetDistance(_myDistance);
	}

	public void setMaximumDistance(final double maximumDistance) {
		this.maximumDistance = maximumDistance;
		safeSetDistance(_myDistance);
	}

	public void setResetOnDoubleClick(final boolean resetOnDoubleClick) {
		this.resetOnDoubleClick = resetOnDoubleClick;
	}

	public void setState(final CCCameraState state) {
		setState(state, 0.3f);
	}

	public void setState(final CCCameraState state, final double animationTimeMillis) {
		if (animationTimeMillis > 0) {
			rotationInterps.startInterpolation(new RotationInterp(state._myRotation, animationTimeMillis));
			centerInterps.startInterpolation(new CenterInterp(state._myCenter, animationTimeMillis));
			distanceInterps.startInterpolation(new DistanceInterp(state._myDistance, animationTimeMillis));
		} else {
			_myRotation = state._myRotation;
			_myCenter.set(state._myCenter);
			_myDistance = state._myDistance;
		}
//		feed();
	}


	abstract public class AbstractInterp extends CCAnimatorAdapter{
		double _myTime;
		final double _myDuration;

		protected AbstractInterp(final double theDuration) {
			_myDuration = theDuration;
		}
		


		protected double smooth(final double a, final double b, final double t) {
			final double smooth = (t * t * (3 - 2 * t));
			return (b * smooth) + (a * (1 - smooth));

		}

		protected CCVector3 smooth(final CCVector3 a, final CCVector3 b, final double t) {
			return new CCVector3(
				smooth(a.x, b.x, t), 
				smooth(a.y, b.y, t), 
				smooth(a.z, b.z, t)
			);
		}

		void start() {
			_myTime = 0;
			_myApp.animator().listener().add(this);
		}

		void cancel() {
			_myApp.animator().listener().remove(this);
		}

		public void update(CCAnimator theAnimator) {
			_myTime += theAnimator.deltaTime();
			final double t = _myTime / _myDuration;
			if (t > .99) {
				cancel();
				setEndState();
			} else {
				interp(t);
			}
//			feed();
		}

		protected abstract void interp(double t);

		protected abstract void setEndState();
	}

	class DistanceInterp extends AbstractInterp {
		private final double _myStartDistance = _myDistance;
		private final double _myEndDistance;

		public DistanceInterp(final double endDistance, final double theDuration) {
			super(theDuration);
			_myEndDistance = Math.min(maximumDistance, Math.max(minimumDistance, endDistance));
		}

		@Override
		protected void interp(final double t) {
			_myDistance = smooth(_myStartDistance, _myEndDistance, t);
		}

		@Override
		protected void setEndState() {
			_myDistance = _myEndDistance;
		}
	}

	class CenterInterp extends AbstractInterp {
		private final CCVector3 startCenter;
		private final CCVector3 endCenter;

		public CenterInterp(final CCVector3 endCenter, final double theDuration) {
			super(theDuration);
			startCenter = new CCVector3(_myCenter);
			this.endCenter = endCenter;
		}

		@Override
		protected void interp(final double t) {
			_myCenter.set(smooth(startCenter, endCenter, t));
		}

		@Override
		protected void setEndState() {
			_myCenter.set(endCenter);
		}
	}

	class RotationInterp extends AbstractInterp {
		final CCQuaternion _myStartRotation;
		final CCQuaternion _myEndRotation;

		public RotationInterp(final CCQuaternion endRotation, final double theTime) {
			super(theTime);
			_myStartRotation = new CCQuaternion(_myRotation);
			_myEndRotation = endRotation;
		}
		
		// Thanks to Michael Kaufmann <mail@michael-kaufmann.ch> for improvements to this function.
		private CCQuaternion slerp(final CCQuaternion a, final CCQuaternion b, final double t) {
			final double a0 = a.w, a1 = a.x, a2 = a.y, a3 = a.z;
			double b0 = b.w, b1 = b.x, b2 = b.y, b3 = b.z;

			double cosTheta = a0 * b0 + a1 * b1 + a2 * b2 + a3 * b3;
			if (cosTheta < 0) {
				b0 = -b0;
				b1 = -b1;
				b2 = -b2;
				b3 = -b3;
				cosTheta = -cosTheta;
			}

			final double theta = CCMath.acos(cosTheta);
			final double sinTheta = CCMath.sqrt(1.0f - cosTheta * cosTheta);

			double w1, w2;
			if (sinTheta > 0.001f) {
				w1 = CCMath.sin((1.0f - t) * theta) / sinTheta;
				w2 = CCMath.sin(t * theta) / sinTheta;
			} else {
				w1 = 1.0f - t;
				w2 = t;
			}
			return new CCQuaternion(w1 * a1 + w2 * b1, w1 * a2 + w2 * b2, w1
					* a3 + w2 * b3, w1 * a0 + w2 * b0).normalizeLocal();
		}

		@Override
		void start() {
			_myRotateXAction.stop();
			_myRotateYAction.stop();
			_myRotateZAction.stop();
			super.start();
		}

		@Override
		protected void interp(final double t) {
			_myRotation.set(slerp(_myStartRotation, _myEndRotation, t));
		}

		@Override
		protected void setEndState() {
			_myRotation.set(_myEndRotation);
		}
	}
}
