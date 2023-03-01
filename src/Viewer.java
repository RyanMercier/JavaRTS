import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

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
 
 * Credits: Kelly Charles (2020)
 */
public class Viewer extends JPanel
{
	private long CurrentAnimationTime = 0;

	Model gameworld;

	private int viewWidth;
	private int viewHeight;
	private float zoom; // set default in Model.java
	int tilesX;
	int tilesY;

	private float cameraX;
	private float cameraY;

	tile tiles[][];
	int tileSize;
	Image textureAtlas;
	private Map<String, Image> imageCache = new HashMap<>();

	public Viewer(Model World, int _viewWidth, int _viewHeight)
	{
		this.gameworld = World;
		this.viewWidth = _viewWidth;
		this.viewHeight = _viewHeight;
		this.cameraX = viewWidth / 2;
		this.cameraY = viewHeight / 2;

		tiles = gameworld.GetWorldTiles();
		tileSize = gameworld.getTileSize();

		File AtlasFile = new File("res/tilemap.png");
		try
		{
			textureAtlas = ImageIO.read(AtlasFile);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateview()
	{

		this.repaint();
	}

	public Point globalToScreenCoords(float _x, float _y)
	{

		float offsetX = (_x - cameraX);
		float offsetY = (_y - cameraY);
		return new Point((int)(viewWidth / 2 + offsetX * zoom), (int)(viewHeight / 2 + offsetY * zoom));
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		CurrentAnimationTime++;

		updateCamera();

		int scale = 2;

		drawViewPortBackground(g);

		drawUnits(gameworld.getZombies(), g, scale);
		// drawUnits(gameworld.getEnemies(), g, scale);
	}

	public void updateCamera()
	{
		zoom = gameworld.getZoom();
		cameraX = gameworld.getCameraX();
		cameraY = gameworld.getCameraY();
		tilesX = (int)(viewWidth / (tileSize * zoom));
		tilesY = (int)(viewHeight / (tileSize * zoom));
	}

	private void drawViewPortBackground(Graphics g)
	{
		// draw tile map

		float camOffsetX = cameraX - (cameraX % tileSize);
		float camOffsetY = cameraY - (cameraY % tileSize);

		for (int y = 0; y <= tilesY + 1; y++)
		{
			// only draw tiles in view
			for (int x = 0; x <= tilesX + 1; x++)
			{
				// only draw tiles in map bounds
				if (y < 0 || y >= tiles.length || x < 0 || x >= tiles[0].length) // also still need to stop camera from moving out of bounds
				{
					continue; // skip tiles outside the map
				}

				int tileX = (int)(cameraX / tileSize + x - tilesX / 2 - 1);
				int tileY = (int)(cameraY / tileSize + y - tilesY / 2 - 1);

				if (tileX >= 0 && tileX < tiles[0].length && tileY >= 0 && tileY < tiles.length)
				{
					Point pos = globalToScreenCoords(camOffsetX + (x - (tilesX / 2) - 1) * tileSize, camOffsetY + (y - (tilesY / 2) - 1) * tileSize);
					g.drawImage(textureAtlas, pos.x, pos.y, (int)(pos.x + tileSize * zoom), (int)(pos.y + tileSize * zoom), tiles[tileY][tileX].textureIndex * tileSize, 0, tiles[tileY][tileX].textureIndex * tileSize + tileSize, tileSize, null);
				}
			}
		}
	}

	private void drawUnits(List<Unit> units, Graphics g, int scale)
	{
		for (Unit unit : units)
		{
			drawUnit((int)unit.getGameObject().getCentre().getX(), (int)unit.getGameObject().getCentre().getY(), (int)unit.getGameObject().getWidth(), (int)unit.getGameObject().getHeight(), scale, unit.getFrames(), unit.getGameObject().getTexture(), g);
		}
	}

	private void drawUnit(int x, int y, int width, int height, int scale, int frames, String texture, Graphics g)
	{
		Image myImage = imageCache.get(texture);
		if (myImage == null)
		{
			// Image not in cache, load it from disk
			try
			{
				File textureFile = new File(texture);
				myImage = ImageIO.read(textureFile);
				imageCache.put(texture, myImage);
			} catch (IOException e)
			{
				e.printStackTrace();
				return; // Don't try to draw if image couldn't be loaded
			}
		}

		int currentPositionInAnimation = ((int)((CurrentAnimationTime % (10 * frames)) / 10)) * width; // slows down animation so every 10 frames we get another frame so every 100ms
		Point pos = globalToScreenCoords(x, y);
		g.drawImage(myImage, pos.x, pos.y, Math.round(pos.x + width * scale * zoom), Math.round(pos.y + height * scale * zoom), currentPositionInAnimation, 0, currentPositionInAnimation + width - 1, height, null);
	}

}
