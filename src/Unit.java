import java.awt.Point;

import util.GameObject;
import util.Point3f;
import util.Vector3f;

public class Unit extends Entity
{
	private int health = 100;
	float speed = 2;

	private int tileSize = 32;
	private Vector2D[][] flowField;

	public Unit(int _id, String _filepath, int _width, int _height, Point3f _position, int _tileSize, int _frames)
	{
		id = _id;
		gameObject = new GameObject(_filepath, _width, _height, _position);
		tileSize = _tileSize;
		frames = _frames;

	}

	public void assignFlowField(Vector2D[][] _flowField)
	{
		flowField = _flowField;
	}

	public void clearFlowField(Vector2D[][] _flowField)
	{
		flowField = null;
	}

	public Point currentTile()
	{
		return new Point((int)(gameObject.getCentre().getX() / tileSize), (int)(gameObject.getCentre().getY() / tileSize));
	}

	public void Update()
	{
		if (flowField != null)
		{
			Point tilePos = currentTile();
			gameObject.getCentre().ApplyVector(new Vector3f(flowField[tilePos.y][tilePos.x].x * speed, flowField[tilePos.y][tilePos.x].y * speed, 0));
		}
	}
}
