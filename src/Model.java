import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
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
	private tile[][] tiles = new tile[worldHeight][worldWidth];
	private int tileSize = 32;

	private final float minZoom = 0.5f;
	private float zoom = 0.5f;
	private GameObject camera = new GameObject();
	private float cameraSpeed = 1f;

	private Point mousePos1;
	private Point mousePos2;

	private int idCount = 0;
	private CopyOnWriteArrayList<Unit> zombiesList = new CopyOnWriteArrayList<Unit>();
	private CopyOnWriteArrayList<Unit> enemiesList = new CopyOnWriteArrayList<Unit>();
	ArrayList<Unit> selected;

	private int Score = 0;

	public Model(int frameWidth, int frameHeight)
	{
		// setup game world
		LoadWorld(worldFilePath);

		camera.setCentre(new Point3f(500, 500, 0));
		viewWidth = frameWidth;
		viewHeight = frameHeight;

		//
		for (int i = 0; i < 1000; i++)
		{
			zombiesList.add(new Unit(idCount++, "res/pirateship.png", 16, 16, new Point3f(getRandomInt(100, 2500), getRandomInt(100, 2500), 0), tileSize, 1));
		}

		// Enemies starting with four
//		enemiesList.add(new Unit(idCount++, "res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 50 + 400), 0, 0), tileSize, 1));
//		enemiesList.add(new Unit(idCount++, "res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 50 + 500), 0, 0), tileSize, 1));
//		enemiesList.add(new Unit(idCount++, "res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 100 + 500), 0, 0), tileSize, 1));
//		enemiesList.add(new Unit(idCount++, "res/UFO.png", 50, 50, new Point3f(((float)Math.random() * 100 + 400), 0, 0), tileSize, 1));

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
		float leftBound = camera.getCentre().getX() / tileSize - viewWidth / zoomedSize / 2;
		float rightBound = leftBound + viewWidth / zoomedSize;
		float upperBound = camera.getCentre().getY() / tileSize - viewHeight / zoomedSize / 2;
		float lowerBound = upperBound + viewHeight / zoomedSize;

		if (leftBound < 0)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX() + viewWidth / zoomedSize / 2, camera.getCentre().getY(), 0));
		}

		if (rightBound > tiles[0].length)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX() - viewWidth / zoomedSize / 2, camera.getCentre().getY(), 0));
		}

		if (upperBound < 0)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX(), camera.getCentre().getY() + viewHeight / zoomedSize / 2, 0));
		}

		if (lowerBound > tiles.length)
		{
			camera.setCentre(new Point3f(camera.getCentre().getX(), camera.getCentre().getY() - viewHeight / zoomedSize / 2, 0));
		}
	}

	public int getRandomInt(int min, int max)
	{
		return (int)((Math.random() * (max - min)) + min);
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

	public ArrayList<Unit> getUnitsInArea(Point p1, Point p2)
	{
		ArrayList<Unit> result = new ArrayList<Unit>();

		int x1 = Math.min(p1.x, p2.x);
		int x2 = Math.max(p1.x, p2.x);
		int y1 = Math.min(p1.y, p2.y);
		int y2 = Math.max(p1.y, p2.y);

		for (Unit zombie : zombiesList)
		{
			float zombieX = zombie.getGameObject().getCentre().getX();
			float zombieY = zombie.getGameObject().getCentre().getY();

			if (zombieX >= x1 && zombieX <= x2 && zombieY >= y1 && zombieY <= y2)
			{
				result.add(zombie);
			}
		}

		return result;
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
					tiles[row][i] = new tile(row, i, (int)(worldRow[i] - '0'));
					// System.out.print(tiles[row][i].textureIndex);
				}

				row++;
				// System.out.println();
			}

			scanner.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		Vector2D[][] flow = generateFlowField(tiles[2][2]);

		for (int y = 0; y < flow.length; y++)
		{
			for (int x = 0; x < flow[0].length; x++)
			{
				System.out.print(flow[y][x].x + ", " + flow[y][x].y + " | ");
			}

			System.out.println();
		}

	}

	// This is the heart of the game , where the model takes in all the inputs
	// ,decides the outcomes and then changes the model accordingly.
	public void logic()
	{
		// user interaction first
		userLogic();
		// run game
		gameLogic();
		// window input and logic
		windowLogic();

	}

	private void windowLogic()
	{
		changeZoom(Controller.getInstance().getNotches());

		float zoomedSize = tileSize * zoom;
		float leftBound = camera.getCentre().getX() / tileSize - viewWidth / zoomedSize / 2;
		float rightBound = leftBound + viewWidth / zoomedSize;
		float upperBound = camera.getCentre().getY() / tileSize - viewHeight / zoomedSize / 2;
		float lowerBound = upperBound + viewHeight / zoomedSize;

		// handle camera movement;
		if (Controller.getInstance().isKeyAPressed())
		{
			if ((int)leftBound > 0)
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
			if ((int)upperBound > 0)
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

			selected = getUnitsInArea(worldPos1, worldPos2);

			Vector2D[][] flow = generateFlowField(tiles[2][2]);

			for (Unit zombie : selected)
			{
				System.out.println(zombie.getId());
				zombie.assignFlowField(flow);
			}
		}
	}

	private void gameLogic()
	{
		for (Unit zombie : zombiesList)
		{
			zombie.Update();
		}
	}

	public tile[][] GetWorldTiles()
	{
		return tiles;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public CopyOnWriteArrayList<Unit> getZombies()
	{
		return zombiesList;
	}

	public CopyOnWriteArrayList<Unit> getEnemies()
	{
		return enemiesList;
	}

	public int getScore()
	{
		return Score;
	}

	public int[][] generateHeatMap(tile goalTile)
	{
		// use Djikstra's Algorithm

		int[][] distances = new int[tiles.length][tiles[0].length];

		for (int i = 0; i < distances.length; i++)
		{
			Arrays.fill(distances[i], Integer.MAX_VALUE);
		}

		distances[goalTile.y][goalTile.x] = 0;

		Queue<tile> openList = new LinkedList<>();
		openList.add(goalTile);

		while (!openList.isEmpty())
		{
			tile selected = openList.remove();
			int selectedDistance = distances[selected.y][selected.x];

			ArrayList<tile> neighbors = new ArrayList<>();

			// Check if neighbors within array bounds
			for (int i = selected.y - 1; i <= selected.y + 1; i++)
			{
				for (int j = selected.x - 1; j <= selected.x + 1; j++)
				{
					// Skip current tile
					if (i == selected.y && j == selected.x)
					{
						continue;
					}

					// Check if tile is within bounds
					if (i >= 0 && i < tiles.length && j >= 0 && j < tiles[0].length)
					{
						// Tile is within bounds
						// Add to neighbor list
						neighbors.add(tiles[i][j]);
					}
				}
			}

			for (tile neighbor : neighbors)
			{
				int neighborDistance = distances[neighbor.y][neighbor.x];
				if (neighbor.isNotWall() && selectedDistance + 1 < neighborDistance)
				{
					distances[neighbor.y][neighbor.x] = selectedDistance + 1;
					openList.add(neighbor);
				}
			}
		}

		return distances;
	}

	public Vector2D[][] generateFlowField(tile goalTile)
	{
		int[][] heatMap = generateHeatMap(goalTile);

		// compute vector field
		Vector2D[][] vectorField = new Vector2D[heatMap.length][heatMap[0].length];

		// iterate over heatMap and let the vector at a position be the sum of all
		// f(neighborVector) with f being 1/x
		for (int y = 0; y < vectorField.length; y++)
		{

			for (int x = 0; x < vectorField[0].length; x++)
			{
				Vector2D flow = new Vector2D(0, 0);

				for (int i = y - 1; i <= y + 1; i++)
				{
					for (int j = x - 1; j <= x + 1; j++)
					{
						// Skip current tile
						if (i == y && j == x)
						{
							continue;
						}

						// Check if neighbor tile is within bounds and heatMap value is not zero (to
						// avoid divide by zero)
						if (i >= 0 && i < vectorField.length && j >= 0 && j < vectorField[0].length && heatMap[i][j] != 0)
						{
							// check if neighbor tile is not a wall
							if (tiles[i][j].isNotWall())
							{
								// if it isn't then we get the normalized cardinal direction vector and let its
								// magnitude be f(heatMapDistance) with f being 1 / x
								// we then add that to our resultant vector
								Vector2D neighbor = new Vector2D(j - x, i - y);
								neighbor.Normalize();
								neighbor.Scale(1.0f / heatMap[i][j]);
								flow.Add(neighbor);
							} else
							{
								// if it's a wall tile, add a vector pointing away from the wall to avoid it
								Vector2D wallAvoidance = new Vector2D(x - j, y - i);
								wallAvoidance.Normalize();
								wallAvoidance.Scale(2.0f); // increase the magnitude for stronger avoidance
								flow.Add(wallAvoidance);
							}
						}
					}
				}

				flow.Normalize();
				flow.y = -flow.y; // idk bruh it works
				vectorField[y][x] = flow;
			}
		}

		// Smoothing
		Vector2D[][] smoothedVectorField = new Vector2D[vectorField.length][vectorField[0].length];
		for (int y = 0; y < vectorField.length; y++)
		{

			for (int x = 0; x < vectorField[0].length; x++)
			{
				Vector2D flow = new Vector2D(0, 0);

				for (int i = y - 1; i <= y + 1; i++)
				{
					for (int j = x - 1; j <= x + 1; j++)
					{
						// Check if neighbor tile is within bounds and heatMap value is not zero (to
						// avoid divide by zero)
						if (i >= 0 && i < vectorField.length && j >= 0 && j < vectorField[0].length && heatMap[i][j] != 0)
						{
							flow.Add(vectorField[i][j]);
						}
					}
				}

				flow.Normalize();
				smoothedVectorField[y][x] = flow;
			}
		}

		return smoothedVectorField;
	}
}
