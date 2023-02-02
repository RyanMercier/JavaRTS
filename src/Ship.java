import util.GameObject;
import util.Point3f;

public class Ship extends Entity
{
	private int health = 100;
	
	public Ship(String _filepath, int _width, int _height, Point3f _position, int _frames)
	{
		gameObject = new GameObject(_filepath, _width, _height, _position);
		frames = _frames;
	}
}
