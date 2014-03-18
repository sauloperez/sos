package com.redch;

public class Sample {

	private String id;
	private String sensorId;
	private float[] coord;
	private Object value;
	private String resultTime;
	private Action action;

	public Sample() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public float[] getCoord() {
		return coord;
	}

	public void setCoord(float[] coord) {
		this.coord = coord;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object object) {
		this.value = object;
	}

	public String getResultTime() {
		return resultTime;
	}

	public void setResultTime(String resultTime) {
		this.resultTime = resultTime;
	}

	public String getAction() {
		return action.getValue();
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "Sample [id = "+ id + ", lat = " + coord[0] + ", lng = " + coord[1] + ", value = " + value + "]";
	}
}
