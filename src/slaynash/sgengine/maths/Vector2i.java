package slaynash.sgengine.maths;

public class Vector2i {
	
	public int x = 0;
	public int y = 0;
	
	public Vector2i() {
		
	}
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2i(Vector2i vec) {
		this.x = vec.x;
		this.y = vec.y;
	}
	
}
