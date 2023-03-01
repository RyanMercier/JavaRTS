
public class Vector2D
{
	public float x;
	public float y;

	public Vector2D(float _x, float _y)
	{
		this.x = _x;
		this.y = _y;
	}

	public void Normalize()
	{
		double magnitude = Math.sqrt(x * x + y * y);

		if (magnitude > 0)
		{
			x /= magnitude;
			y /= magnitude;
		}
	}

	public void Scale(float scalar)
	{
		x *= scalar;
		y *= scalar;
	}

	public void Add(Vector2D v)
	{
		x += v.x;
		y += v.y;
	}

}
