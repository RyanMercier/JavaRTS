import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import util.GameObject;
import util.Point3f;
import util.Vector3f;

/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 */
public class Model
{

	private Controller controller = Controller.getInstance();
	private String worldFilePath = "res/world.txt";
	private int worldWidth = 100;
	private int worldHeight = 100;
	private int viewWidth;
	private int viewHeight;
	private int[][] tiles = new int[worldHeight][worldWidth];
	private int tileSize = 32;

	private final float minZoom = 0.5f;
	private float zoom = 2f;
	private GameObject camera = new GameObject();
	private float cameraSpeed = 1f;

	private Point mousePos1;
	private Point mousePos2;

	private Ship player;
	private CopyOnWriteArrayList<GameObject> EnemiesList = new CopyOnWriteArrayList<GameObject>();
	private CopyOnWriteArrayList<GameObject> BulletList = new CopyOnWriteArrayList<GameObject>();

	private int Score = 0;

	public Model(int frameWidth, int frameHeight)
	{
		// setup game world
		LoadWorld(worldFilePath);

		camera.setCentre(new Point3f(500, 500, 0));
		viewWidth = frameWidth;
		viewHeight = frameHeight;

		// Player
		player = new Ship("res/pirateship.png", 16, 16, new Point3f(500, 500, 0), 1);
		// Enemies starting with four

		EnemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 50 + 400), 0, 0)));
		EnemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 50 + 500), 0, 0)));
		EnemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 100 + 500), 0, 0)));
		EnemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 100 + 400), 0, 0)));

	}

	public float getZoom()
	{
		return zoom;
	}

	public void changeZoom(float amount)
	{
		zoom += amount * 0.02f;
		if (zoom < minZoom)
		{
			zoom = minZoom;
		}

		float zoomedSize = tileSize * zoom;
		int leftBound = (int)(camera.getCentre().getX() / tileSize - viewWidth / zoomedSize / 2);
		int rightBound = (int)(leftBound + viewWidth / zoomedSize);
		int upperBound = (int)(camera.getCentre().getY() / tileSize - viewHeight / zoomedSize / 2);
		int lowerBound = (int)(upperBound + viewHeight / zoomedSize);

		if (leftBound < 0)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX() + viewWidth / zoomedSize / 2, camera.getCentre().getY(), 0));
		}

		if (rightBound >= tiles[0].length)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX() - viewWidth / zoomedSize / 2, camera.getCentre().getY(), 0));
		}

		if (upperBound < 0)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX(), camera.getCentre().getY() + viewHeight / zoomedSize / 2, 0));
		}

		if (lowerBound >= tiles.length)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX(), camera.getCentre().getY() - viewHeight / zoomedSize / 2, 0));
		}
	}

	public float getCameraX()
	{
		return camera.getCentre().getX();
	}

	public float getCameraY()
	{
		return camera.getCentre().getY();
	}

	public Point globalToScreenCoords(float _x, float _y)
	{
		return new Point((int)(viewWidth / 2 + (_x - getCameraX()) * zoom), (int)(viewHeight / 2 + (_y - getCameraY()) * zoom));
	}

	public Point screenToGlobalCoords(Point p)
	{
		return new Point((int)((p.getX() - viewWidth / 2) / zoom + getCameraX()), (int)((p.getY() - viewHeight / 2) / zoom + getCameraY()));
	}

	private void LoadWorld(String fileName)
	{
		// this loads the map from a text file and puts it into a 2d array containing
		// the texture atlas index of the tile
		File worldFile = new File(fileName);
		try
		{
			Scanner scanner = new Scanner(worldFile);

			int row = 0;
			while (scanner.hasNextLine())
			{
				char[] worldRow = scanner.nextLine().toCharArray();
				for (int i = 0; i < worldRow.length; i++)
				{
					tiles[row][i] = (int)(worldRow[i] - '0');
					System.out.print(tiles[row][i]);
				}

				row++;
				System.out.println();
			}

			scanner.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// This is the heart of the game , where the model takes in all the inputs
	// ,decides the outcomes and then changes the model accordingly.
	public void logic()
	{
		// user interaction first
		userLogic();
		// Player Logic first
		// playerLogic();
		// Enemy Logic next
		enemyLogic();
		// Bullets move next
		bulletLogic();
		// interactions between objects
		gameLogic();
		// window input and logic
		windowLogic();

	}

	private void windowLogic()
	{
		changeZoom(Controller.getInstance().getNotches());

		float zoomedSize = tileSize * zoom;
		int leftBound = (int)(camera.getCentre().getX() / tileSize - viewWidth / zoomedSize / 2);
		int rightBound = (int)(leftBound + viewWidth / zoomedSize);
		int upperBound = (int)(camera.getCentre().getY() / tileSize - viewHeight / zoomedSize / 2);
		int lowerBound = (int)(upperBound + viewHeight / zoomedSize);

		// handle camera movement;
		if (Controller.getInstance().isKeyAPressed())
		{
			if (leftBound > 0)
			{
				camera.getCentre().ApplyVector(new Vector3f(-cameraSpeed * (1 + 1 / zoom / 2), 0, 0));
			}
		}

		if (Controller.getInstance().isKeyDPressed())
		{
			if (rightBound < tiles[0].length - 1)
			{
				camera.getCentre().ApplyVector(new Vector3f(cameraSpeed * (1 + 1 / zoom / 2), 0, 0));
			}
		}

		if (Controller.getInstance().isKeyWPressed())
		{
			if (upperBound > 0)
			{
				camera.getCentre().ApplyVector(new Vector3f(0, cameraSpeed * (1 + 1 / zoom / 2), 0));
			}
		}

		if (Controller.getInstance().isKeySPressed())
		{
			if (lowerBound < tiles.length - 1)
			{
				camera.getCentre().ApplyVector(new Vector3f(0, -cameraSpeed * (1 + 1 / zoom / 2), 0));
			}
		}
	}

	private void userLogic()
	{
		// handle right mouse drag select
		boolean rightMousePressed = controller.isRightMousePressed();
		boolean rightMouseReleased = controller.isRightMouseReleased();

		if (rightMousePressed)
		{
			mousePos1 = controller.getMousePosition();
		}

		else if (rightMouseReleased)
		{
			mousePos2 = controller.getMousePosition();
			controller.setRightMouseReleased(false);

			Point worldPos1 = screenToGlobalCoords(mousePos1);
			Point worldPos2 = screenToGlobalCoords(mousePos2);

			tiles[(int)(worldPos1.getY() / tileSize)][(int)(worldPos1.getX() / tileSize)] = 0;
			tiles[(int)(worldPos2.getY() / tileSize)][(int)(worldPos2.getX() / tileSize)] = 0;
		}
	}

	private void gameLogic()
	{

		// this is a way to increment across the array list data structure

		// see if they hit anything
		// reading wise too
		for (GameObject temp : EnemiesList)
		{
			for (GameObject Bullet : BulletList)
			{
				if (Math.abs(temp.getCentre().getX() - Bullet.getCentre().getX()) < temp.getWidth() && Math.abs(temp.getCentre().getY() - Bullet.getCentre().getY()) < temp.getHeight())
				{
					EnemiesList.remove(temp);
					BulletList.remove(Bullet);
					Score++;
				}
			}
		}

	}

	private void enemyLogic()
	{
		// TODO Auto-generated method stub
		for (GameObject temp : EnemiesList)
		{
			// Move enemies

			temp.getCentre().ApplyVector(new Vector3f(0, -1, 0));

			// see if they get to the top of the screen ( remember 0 is the top
			if (temp.getCentre().getY() == 900.0f) // current boundary need to pass value to model
			{
				EnemiesList.remove(temp);

				// enemies win so score decreased
				Score--;
			}
		}

		if (EnemiesList.size() < 2)
		{
			while (EnemiesList.size() < 6)
			{
				EnemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 1000), 0, 0)));
			}
		}
	}

	private void bulletLogic()
	{
		// TODO Auto-generated method stub
		// move bullets

		for (GameObject temp : BulletList)
		{
			// check to move them

			temp.getCentre().ApplyVector(new Vector3f(0, 1, 0));
			// see if they hit anything

			// see if they get to the top of the screen ( remember 0 is the top
			if (temp.getCentre().getY() == 0)
			{
				BulletList.remove(temp);
			}
		}

	}

	private void CreateBullet()
	{
		BulletList.add(new GameObject("res/Bullet.png", 32, 64, new Point3f(player.getGameObject().getCentre().getX(), player.getGameObject().getCentre().getY(), 0.0f)));

	}

	public int[][] GetWorldTiles()
	{
		return tiles;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public Ship getPlayer()
	{
		return player;
	}

	public CopyOnWriteArrayList<GameObject> getEnemies()
	{
		return EnemiesList;
	}

	public CopyOnWriteArrayList<GameObject> getBullets()
	{
		return BulletList;
	}

	public int getScore()
	{
		return Score;
	}

}
