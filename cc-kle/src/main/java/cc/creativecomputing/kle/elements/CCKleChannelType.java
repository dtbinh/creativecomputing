package cc.creativecomputing.kle.elements;

public enum CCKleChannelType {
	
	LIGHTS("lights"), MOTORS("motors");
	
	private String _myID;

	private CCKleChannelType(String theID){
		_myID = theID;
	}
	
	public String id(){
		return _myID;
	}
}
