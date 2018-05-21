/* A class for the graphical board in the Game of Life
 * @author          Collin Browse
 */
package assignment2;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * This class is for Swing purposes. It creates the squares contained within the board.
 * <p>Creatures are yellow rectangles<br />
 * <p>Monsters are cyan rectangles<br />
 * <p>Strawberries are red ovals<br />
 * <p>Mushrooms are gray ovals
 */
public class BoardPanel extends JPanel {
	
	Color color;

    /**
     * Creates a new square on the board with specified color
     * @param color         The color for the panel
     */
	public BoardPanel(Color color) {
		this.color = color;
	}

    /**
     * Sets the color of this square on the board
     * @param color         The color for the panel
     */
	public void setColor(Color color) {
		this.color = color;
	}

    /**
     * Creates the shapes for each item on the board
     * @param page          The Swing Graphics page
     */
	public void paintComponent(Graphics page) {
		
		super.paintComponent(page);
		page.setColor(color);

		if (color.equals(Color.yellow))     // Creatures are yellow rectangles
			page.fillRect(0, 0, 19, 19);
		if (color.equals(Color.cyan))       // Monsters are cyan rectangles
			page.fillRect(0, 0, 19, 19);
		if (color.equals(Color.red))        // Strawberries are red ovals
			page.fillOval(0, 0, 14, 14);	
		if (color.equals(Color.gray))       // Mushrooms are gray ovals
			page.fillOval(0, 0, 10, 10);

	}
}
