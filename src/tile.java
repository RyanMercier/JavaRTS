
public class tile
{
	public int x;
	public int y;
	public int textureIndex = 0;

	public tile(int _x, int _y)
	{
		x = _x;
		y = _y;
	}

	public tile(int _x, int _y, int _textureIndex)
	{
		x = _x;
		y = _y;
		textureIndex = _textureIndex;
	}

	public boolean isNotWall()
	{
		return true;
	}
}
