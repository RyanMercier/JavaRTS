import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

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

//Singleton pattern
public class Controller implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{

	private static boolean KeyAPressed = false;
	private static boolean KeySPressed = false;
	private static boolean KeyDPressed = false;
	private static boolean KeyWPressed = false;
	private static boolean KeySpacePressed = false;

	private static int notches;
	private static boolean leftMouseClicked = false;
	private static boolean rightMousePressed = false;
	private static boolean rightMouseReleased = false;
	private static Point mousePosition = new Point(0, 0);
	private static Point dragPosition = new Point(0, 0);

	private static final Controller instance = new Controller();

	public Controller()
	{
		// action = new MouseAction();
	}

	public static Controller getInstance()
	{
		return instance;
	}

	@Override
	// Key pressed , will keep triggering
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyChar())
		{
		case 'a':
			setKeyAPressed(true);
			break;
		case 's':
			setKeySPressed(true);
			break;
		case 'w':
			setKeyWPressed(true);
			break;
		case 'd':
			setKeyDPressed(true);
			break;
		case ' ':
			setKeySpacePressed(true);
			break;
		default:
			// System.out.println("Controller test: Unknown key pressed");
			break;
		}

		// You can implement to keep moving while pressing the key here .

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyChar())
		{
		case 'a':
			setKeyAPressed(false);
			break;
		case 's':
			setKeySPressed(false);
			break;
		case 'w':
			setKeyWPressed(false);
			break;
		case 'd':
			setKeyDPressed(false);
			break;
		case ' ':
			setKeySpacePressed(false);
			break;
		default:
			// System.out.println("Controller test: Unknown key pressed");
			break;
		}
		// upper case

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		dragPosition = e.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			leftMouseClicked = true;
			mousePosition = e.getPoint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
		{
			rightMousePressed = true;
			mousePosition = e.getPoint();
			dragPosition = mousePosition;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
		{
			rightMousePressed = false;
			rightMouseReleased = true;
			mousePosition = e.getPoint();
			dragPosition = null;
		}

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		notches = e.getWheelRotation();
	}

	public boolean isKeyAPressed()
	{
		return KeyAPressed;
	}

	public void setKeyAPressed(boolean keyAPressed)
	{
		KeyAPressed = keyAPressed;
	}

	public boolean isKeySPressed()
	{
		return KeySPressed;
	}

	public void setKeySPressed(boolean keySPressed)
	{
		KeySPressed = keySPressed;
	}

	public boolean isKeyDPressed()
	{
		return KeyDPressed;
	}

	public void setKeyDPressed(boolean keyDPressed)
	{
		KeyDPressed = keyDPressed;
	}

	public boolean isKeyWPressed()
	{
		return KeyWPressed;
	}

	public void setKeyWPressed(boolean keyWPressed)
	{
		KeyWPressed = keyWPressed;
	}

	public boolean isKeySpacePressed()
	{
		return KeySpacePressed;
	}

	public void setKeySpacePressed(boolean keySpacePressed)
	{
		KeySpacePressed = keySpacePressed;
	}

	public int getNotches()
	{
		int temp = notches;
		notches = 0;
		return temp;
	}

	public Point getMousePosition()
	{
		return mousePosition;
	}

	public Point getDragPosition()
	{
		return dragPosition;
	}

	public boolean isRightMousePressed()
	{
		return rightMousePressed;
	}

	public boolean isRightMouseReleased()
	{
		return rightMouseReleased;
	}

	public void setRightMouseReleased(boolean released)
	{
		rightMouseReleased = released;
	}

	public boolean isLeftMouseClicked()
	{
		return leftMouseClicked;
	}

	public void setLeftMouseReleased(boolean released)
	{
		leftMouseClicked = released;
	}
}
