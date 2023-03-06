import java.awt.Point;

import util.GameObject;
import util.Point3f;
import util.Vector3f;

public class Unit extends Entity
{
	private Type type;
	private int health = 100;
	float speed = 1f;
	private int currentFrame = 0;
	private int reloadFrames = 70;

	private int tileSize = 32;
	private Vector2D[][] flowField;
	private Vector2D movement = new Vector2D(getRandomFloat(-1, 1) * speed, getRandomFloat(-1, 1) * speed);

	Vector3f variance = new Vector3f(0, 0, 0);
	public int collisionRadius = 28;
	public int shotRadius = 50;
	public int pushForce = 2;

	private float steps = 0;

	public Unit(int _id, Type _type, String _filepath, int _width, int _height, Point3f _position, int _tileSize, int _frames)
	{
		id = _id;
		type = _type;
		gameObject = new GameObject(_filepath, _width, _height, _position);
		tileSize = _tileSize;
		frames = _frames;

	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type _type)
	{
		type = _type;

		if (type == type.HUMAN)
		{
			gameObject.setTexture(Model.humanTexture);
		}

		if (type == type.FARMER)
		{
			gameObject.setTexture(Model.farmerTexture);
		}

		if (type == type.ZOMBIE)
		{
			gameObject.setTexture(Model.zombieTexture);
		}
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

	private float getRandomFloat(float min, float max)
	{
		return (float)((Math.random() * (max - min)) + min);
	}

	public void Update()
	{
		if (type == Type.FARMER)
		{
			currentFrame++;
		}

		if (flowField != null)
		{
			Point tilePos = currentTile();
			if (tilePos.x >= 0 && tilePos.x < flowField[0].length && tilePos.y >= 0 && tilePos.y < flowField.length)
			{
				movement = new Vector2D(flowField[tilePos.y][tilePos.x].x * speed, flowField[tilePos.y][tilePos.x].y * speed);
				Move();
			}
		}

		// mill around
		else
		{
			if (steps > getRandomFloat(0.5f, 1.0f) * 100)
			{
				movement = new Vector2D(getRandomFloat(-1, 1) * speed, getRandomFloat(-1, 1) * speed);
				steps = 0;
			}
			Move();
		}
	}

	public void Move()
	{
		if (steps > 100)
		{
			variance = new Vector3f(getRandomFloat(-1, 1), getRandomFloat(-1, 1), 0);
			steps = 0;
		}

		// should add better obstacle avoidance
		Vector3f tentativeMove = new Vector3f(movement.x, movement.y, 0).PlusVector(variance).Normal();
		gameObject.getCentre().ApplyVector(tentativeMove);

		steps++;
	}

	public boolean Shoot()
	{
		if (type == Type.FARMER && currentFrame > reloadFrames)
		{

			currentFrame = 0;
			return true;
		}

		return false;
	}

	public Vector2D getMovement()
	{
		return movement;
	}
}

enum Type
{
	ZOMBIE, HUMAN, FARMER
};
