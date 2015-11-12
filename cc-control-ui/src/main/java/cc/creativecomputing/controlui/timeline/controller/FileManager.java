package cc.creativecomputing.controlui.timeline.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.control.timeline.AbstractTrack;
import cc.creativecomputing.control.timeline.GroupTrack;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.controlui.timeline.controller.track.GroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;


public class FileManager {
	
	public static interface FileManagerListener{
		public void onLoad(Path thePath);

		public void onSave(Path thePath);

		public void onNew(Path thePath);
		
	}
	
	private class DataSerializer{
	
		private static final String TIMELINE_ELEMENT = "timeline";
		private static final String TIMELINES_ELEMENT = "timelines";
		
		private static final String TRANSPORT_ELEMENT = "Transport";
		private static final String PLAYBACK_SPEED_ATTRIBUTE = "speed";
		private static final String LOOP_START_ATTRIBUTE = "loop_start";
		private static final String LOOP_END_ATTRIBUTE = "loop_end";
		private static final String LOOP_ACTIVE_ATTRIBUTE = "loop_active";
		
		private static final String LOWER_BOUND_ATTRIBUTE = "lower_bound";
		private static final String UPPER_BOUND_ATTRIBUTE = "upper_bound";
		
		public DataSerializer() {
		}
		
		////////////////////////////////////
		//
		// LOADING
		//
		////////////////////////////////////
		private void loadTransport(final CCDataObject theTransportData, final TransportController theTransportController) {
			
			if (theTransportData.containsKey(PLAYBACK_SPEED_ATTRIBUTE)) {
				theTransportController.speed(
					theTransportData.getDouble(PLAYBACK_SPEED_ATTRIBUTE)
				);
			}
			
			if (theTransportData.containsKey(LOOP_START_ATTRIBUTE) && theTransportData.containsKey(LOOP_END_ATTRIBUTE)) {
				theTransportController.loop(
					theTransportData.getDouble(LOOP_START_ATTRIBUTE),
					theTransportData.getDouble(LOOP_END_ATTRIBUTE)
				);
			}
			
			if (theTransportData.containsKey(LOOP_ACTIVE_ATTRIBUTE)) {
				theTransportController.doLoop(theTransportData.getBoolean(LOOP_ACTIVE_ATTRIBUTE));
			}
			
			CCDataObject myTrackDataData = theTransportData.getObject(TrackData.TRACKDATA_ELEMENT);
			if(myTrackDataData != null) {
				TrackData myTrackData = new TrackData(null);
				myTrackData.data(myTrackDataData);
				theTransportController.trackData(myTrackData);
			}
		}
		
		private void loadGroupTrack(CCDataObject theData, TimelineController theTimeline){
			if(!theData.containsKey("path"))return;
			Path myPath = Paths.get(theData.getString("path"));
			GroupTrackController myController = theTimeline.createGroupController(myPath);
			myController.groupTrack().data(theData);
			
			Object myData = theData.get(GroupTrack.GROUP_TRACKS);
			if(myData instanceof CCDataArray){
				for(Object myObject:(CCDataArray)myData){
					loadTrack((CCDataObject)myObject, theTimeline);
				}
			}else{
				loadTrack((CCDataObject)myData, theTimeline);
			}
			
		}
		
		private void loadDataTrack(CCDataObject theData, TimelineController theTimeline){
			if(!theData.containsKey("path"))return;
			Path myPath = Paths.get(theData.getString("path"));
			TrackController myController = theTimeline.createController(myPath);
			myController.track().data(theData);
		}
		
		private void loadTrack(CCDataObject theData, TimelineController theTimeline){
			CCLog.info(theData);
			
			if(theData.containsKey("tracks")){
				loadGroupTrack(theData, theTimeline);
			}else{
				loadDataTrack(theData, theTimeline);
			}
		}
		
		private void loadTracks(CCDataObject theTimelineData, TimelineController theTimeline){
			CCDataObject myTimelineData = theTimelineData.getObject(TIMELINE_ELEMENT);
			loadTrack(myTimelineData, theTimeline);
		}
		
		private void loadTimeline(CCDataObject myTimelineData, TimelineController theTimelineController){
			CCDataObject myTransportData = myTimelineData.getObject(TRANSPORT_ELEMENT);
			loadTransport(myTransportData, theTimelineController.transportController());
				
				
			if (myTimelineData.containsKey(LOWER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setLowerBound(myTimelineData.getDouble(LOWER_BOUND_ATTRIBUTE));
			}
			
			if (myTimelineData.containsKey(UPPER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setUpperBound(myTimelineData.getDouble(UPPER_BOUND_ATTRIBUTE));
			}
			theTimelineController.resetClipTracks();
			if(myTimelineData.containsKey(TIMELINE_ELEMENT)){
				loadTracks(myTimelineData, theTimelineController);
			}
		}
		
		public void loadTimeline(Path thePath, TimelineController theTimelineController) {
			CCDataObject myTimelineData = CCDataIO.createDataObject(thePath);
			if(myTimelineData == null)throw new RuntimeException("the given timelinedocument:" + thePath +" does not exist");
			loadTimeline(myTimelineData, theTimelineController);
		}
		
		public void loadProject(Path thePath){
			CCDataObject myProjectData = CCDataIO.createDataObject(thePath);
			CCDataObject myTimelinesObject = myProjectData.getObject(TIMELINES_ELEMENT);
			
			List<String> myKeys = new ArrayList<>(myTimelinesObject.keySet());
			Collections.sort(myKeys);
			for(String myTimeline:myKeys){
				TimelineController myController = _myTimelineContainer.addTimeline(myTimeline);
				loadTimeline(myTimelinesObject.getObject(myTimeline), myController);
			}
			
		}
		
		public List<AbstractTrack> insertTracks(Path thePath, TimelineController theTimelineController) {
			try {
				CCDataObject myTimelineData = CCDataIO.createDataObject(thePath);
				
				CCDataObject myTransportData = myTimelineData.getObject(TRANSPORT_ELEMENT);
				loadTransport(myTransportData, theTimelineController.transportController());
				
				CCDataObject myMarkerDataData = myTransportData.getObject(TrackData.TRACKDATA_ELEMENT);
				TrackData myMarkerData = new TrackData(null);
				if(myMarkerDataData != null){
					myMarkerData.data(myMarkerDataData);
				}
				
//				List<AbstractTrack> myTracks = loadTracks(myTimelineData);
//
//				_myTimelineController.insertTracks(myTracks, myMarkerData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		////////////////////////////////////
		//
		// SAVING
		//
		////////////////////////////////////
		
		private CCDataObject createTransportData(TransportController theTransportController, double theStart, double theEnd) {
			CCDataObject myTransportData = new CCDataObject();
			myTransportData.put(PLAYBACK_SPEED_ATTRIBUTE, theTransportController.speed());
			myTransportData.put(LOOP_START_ATTRIBUTE, theTransportController.loopStart());
			myTransportData.put(LOOP_END_ATTRIBUTE, theTransportController.loopEnd());
			myTransportData.put(LOOP_ACTIVE_ATTRIBUTE, theTransportController.doLoop());
			
			CCDataObject myMarkerData = theTransportController.trackData().data(theStart, theEnd);
			myTransportData.put("marker",myMarkerData);
			return myTransportData;
		}
		
		private CCDataObject createTimelineData(TimelineController theTimelineController, boolean theSaveSelection) {
			CCDataObject myTimelineData = new CCDataObject();
			myTimelineData.put(LOWER_BOUND_ATTRIBUTE, theTimelineController.zoomController().lowerBound());
			myTimelineData.put(UPPER_BOUND_ATTRIBUTE, theTimelineController.zoomController().upperBound());
			
			double myStart = 0;
			double myEnd = theTimelineController.maximumTime();
			
			if(theSaveSelection){
				myStart = theTimelineController.transportController().loopStart();
				myEnd = theTimelineController.transportController().loopEnd();
			}
			
			myTimelineData.put(TRANSPORT_ELEMENT, createTransportData(theTimelineController.transportController(), myStart, myEnd));
			
			GroupTrackController myRootController = theTimelineController.rootController();
			CCLog.info("myRootController:" + myRootController);
			if(myRootController == null)return myTimelineData;
			
			myTimelineData.put(TIMELINE_ELEMENT, myRootController.groupTrack().data(myStart, myEnd));
			
			return myTimelineData;
		}
		
		public void saveTimeline(Path thePath, TimelineController theTimelineController) {
			CCDataObject myTimelineData = createTimelineData(theTimelineController, false);
			CCDataIO.saveDataObject(myTimelineData, thePath);
		}
		
		public void saveProject(Path thePath){
			CCDataObject myProjecteData = new CCDataObject();
			CCDataObject myTimelines = myProjecteData.createObject(TIMELINES_ELEMENT);
			for(String myKey:_myTimelineContainer.timelineKeys()){
				myTimelines.put(myKey, createTimelineData(_myTimelineContainer.timeline(myKey), false));
			}
			CCDataIO.saveDataObject(myProjecteData, thePath);
		}
		
		public void saveTimelineSelection(Path thePath, TimelineController theTimelineController) {
			CCDataObject myTimelineData = createTimelineData(theTimelineController, true);
			CCDataIO.saveDataObject(myTimelineData, thePath);
		}
	}
	
	private DataSerializer _mySerializer;
	
	private TimelineContainer _myTimelineContainer;
	
	private CCListenerManager<FileManagerListener> _myEvents = CCListenerManager.create(FileManagerListener.class);
	
	private String _myExtension = "json";
	private String _myDescription = "Data Path";
	
	public FileManager(TimelineContainer theTimelineContainer) {
		_mySerializer = new DataSerializer();
		_myTimelineContainer = theTimelineContainer;
	}
	
	public void extension(String theExtension, String theDescription) {
		_myExtension = theExtension;
		_myDescription = theDescription;
	}
	
	public String extension() {
		return _myExtension;
	}
	
	public String description() {
		return _myDescription;
	}
	
	public CCListenerManager<FileManagerListener> events() {
		return _myEvents;
	}
	
	public void replaceCurrentTimeline(Path thePath) {
		TimelineController myController = _myTimelineContainer.activeTimeline();
		myController.removeAll();
		_mySerializer.loadTimeline(thePath, myController);
		_myEvents.proxy().onLoad(thePath);
		myController.render();
		UndoHistory.instance().clear();
	}
	
	public void addToCurrentTimeline(Path thePath) {
		_mySerializer.loadTimeline(thePath, _myTimelineContainer.activeTimeline());
		_myEvents.proxy().onLoad(thePath);
		_myTimelineContainer.activeTimeline().render();
		UndoHistory.instance().clear();
	}
	
	public void insertAtTimeToCurrentTimeline(Path thePath) {
		_mySerializer.insertTracks(thePath, _myTimelineContainer.activeTimeline());
		UndoHistory.instance().clear();
	}
	
	public void resetTimeline() {
		_myTimelineContainer.activeTimeline().resetTracks();
		UndoHistory.instance().clear();
		_myEvents.proxy().onNew(Paths.get("New Path"));
	}
	
	private Path _myCurrentPath;
	
	public void exportCurrentTimeline(Path thePath) {
		_myCurrentPath = thePath;
		_mySerializer.saveTimeline(thePath, _myTimelineContainer.activeTimeline());
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(thePath);
	}
	
	public void save(){
		if(_myCurrentPath == null)return;
		exportCurrentTimeline(_myCurrentPath);
	}
	
	public void exportCurrentTimelineSelection(Path thePath){
		_mySerializer.saveTimelineSelection(thePath, _myTimelineContainer.activeTimeline());
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(thePath);
	}
	
	public void saveProject(Path thePath) {
		_mySerializer.saveProject(thePath);
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(thePath);
	}
	
	public void loadProject(Path thePath) {
		_myTimelineContainer.reset();
		_mySerializer.loadProject(thePath);
		UndoHistory.instance().clear();
		_myEvents.proxy().onLoad(thePath);
	}

	public void newProject(){
		_myTimelineContainer.reset();
	}
}
