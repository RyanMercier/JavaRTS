import java.awt.Point;

import util.GameObject;
import util.Point3f;
import util.Vector3f;

public class Unit extends Entity
{
	private int health = 100;
	float speed = 1.5f;

	private int tileSize = 32;
	private Vector2D[][] flowField;
	private Vector2D movement = new Vector2D(0, 0);

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
			if (tilePos.x >= 0 && tilePos.x < flowField[0].length && tilePos.y >= 0 && tilePos.y < flowField.length)
			{
				movement = new Vector2D(flowField[tilePos.y][tilePos.x].x * speed, flowField[tilePos.y][tilePos.x].y * speed);
				Move();
			}
		}
	}

	public void Move()
	{
		gameObject.getCentre().ApplyVector(new Vector3f(movement.x, movement.y, 0));
	}

	public Vector2D getMovement()
	{
		return movement;
	}
}
