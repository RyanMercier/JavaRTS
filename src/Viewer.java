import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

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
		CurrentAnimationTime++; // runs animation time step

		updateCamera();

		int scale = 2;

		// Draw background
		drawViewPortBackground(g);

		// Draw Zombies
		gameworld.getZombies().forEach((temp) -> {
			drawUnit((int)temp.getGameObject().getCentre().getX(), (int)temp.getGameObject().getCentre().getY(), (int)temp.getGameObject().getWidth(), (int)temp.getGameObject().getHeight(), scale, temp.getFrames(), temp.getGameObject().getTexture(), g);
		});

		// Draw Enemies
		gameworld.getEnemies().forEach((temp) -> {
			drawUnit((int)temp.getGameObject().getCentre().getX(), (int)temp.getGameObject().getCentre().getY(), (int)temp.getGameObject().getWidth(), (int)temp.getGameObject().getHeight(), scale, temp.getFrames(), temp.getGameObject().getTexture(), g);

		});
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

	private void drawUnit(int x, int y, int width, int height, int scale, int frames, String texture, Graphics g)
	{
		File TextureToLoad = new File(texture); // should work okay on OSX and Linux but check if you have issues
												// depending your eclipse install or if your running this without an IDE
		try
		{
			Image myImage = ImageIO.read(TextureToLoad);
			// The sprite is 32x32 pixel wide and 4 of them are placed together so we need
			// to grab a different one each time
			// remember your training :-) computer science everything starts at 0 so 32
			// pixels gets us to 31
			int currentPositionInAnimation = ((int)((CurrentAnimationTime % (10 * frames)) / 10)) * width; // slows down animation so every 10 frames we get another frame so every 100ms
			Point pos = globalToScreenCoords(x, y);
			g.drawImage(myImage, pos.x, pos.y, Math.round(pos.x + width * scale * zoom), Math.round(pos.y + height * scale * zoom), currentPositionInAnimation, 0, currentPositionInAnimation + width - 1, height, null);

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
