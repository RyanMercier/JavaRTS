
public class Tile
{
	public int x;
	public int y;
	public int textureIndex = 0;

	public Tile(int _x, int _y)
	{
		x = _x;
		y = _y;
	}

	public Tile(int _x, int _y, int _textureIndex)
	{
		x = _x;
		y = _y;
		textureIndex = _textureIndex;
	}

	public boolean isNotWall()
	{
		if (textureIndex == 0)
		{
			return true;
		}

		return false;
	}
}
