package slaynash.sgengine.gui.slider;

public class GUISliderEvent {
	
	private float percent;
	
	public GUISliderEvent(float trackPercent) {
		this.percent = trackPercent;
	}
	
	public float getPercent(){
		return percent;
	}

}