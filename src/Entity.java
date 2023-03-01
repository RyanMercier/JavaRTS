import util.GameObject;

public class Entity
{
	protected GameObject gameObject;
	protected int frames = 1;
	protected int id = 0;

	public GameObject getGameObject()
	{
		return gameObject;
	}

	public int getFrames()
	{
		return frames;
	}

	public int getId()
	{
		return id;
	}

	public void Update()
	{

	}

}
