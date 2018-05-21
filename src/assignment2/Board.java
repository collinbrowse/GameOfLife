/* A class for the 2D board that the Game of Life Exists on
 * @author          Collin Browse
 */
package assignment2;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Board is where all Creatures and Monsters do their movement which happens in the main method. The board is
 * always square and the height and length can be changed by changing ROWS and COLS. The Board is created through
 * Swing. On the board creatures are represented by yellow squares. Monsters are represented by cyan squares.
 * Strawberries are represented by red circles. Mushrooms are represented by gray circles.
 * <p>
 * This class also has the responsibility of the chromosomes. Chromosomes determine a creature's movement. For instance,
 * if the creature sees a mushroom next to it should it eat or not? In the first generation these decisions in the
 * chromosome are randomly generated. After the subsequent generation the chromosomes are then created by taking
 * a mutation of the chromosomes from the healthiest surviving parents from the previous generation.
 * <p>
 * A chromosome is as follows:
 * <table style = "border-collapse: collapse">
 *     <tr style = "border: 1px solid black">
 *         <th style = "border: 1px solid black">Position</th>
 *         <th style = "border: 1px solid black">Role</th>
 *     </tr>
 *     <tr style = "border: 1px solid black">
 *         <th style = "border: 1px solid black">1</th>
 *         <th style = "border: 1px solid black">action to do when mushroom present</th>
 *     </tr>
 *     <tr style = "border: 1px solid black">
 *       <th style = "border: 1px solid black">2</th>
 *       <th style = "border: 1px solid black">action to do when strawb present</th>
 *     </tr>
 *     <tr style = "border: 1px solid black">
 *        <th style = "border: 1px solid black">3</th>
 *        <th style = "border: 1px solid black">action on nearest mushroom</th>
 *     </tr>
 *     <tr style = "border: 1px solid black">
 *       <th style = "border: 1px solid black">4</th>
 *       <th style = "border: 1px solid black">action on nearest strawb</th>
 *    </tr>
 *    <tr style = "border: 1px solid black">
 *       <th style = "border: 1px solid black">5</th>
 *       <th style = "border: 1px solid black">action on nearest creature/ignore</th>
 *    </tr>
 *    <tr style = "border: 1px solid black">
 *       <th style = "border: 1px solid black">6</th>
 *       <th style = "border: 1px solid black">action on nearest monster</th>
 *   </tr>
 *   <tr style = "border: 1px solid black">
 *      <th style = "border: 1px solid black">7</th>
 *      <th style = "border: 1px solid black">default action</th>
 *   </tr>
 *   <tr style = "border: 1px solid black">
 *      <th style = "border: 1px solid black">8</th>
 *      <th style = "border: 1px solid black">action 1 weight</th>
 *   </tr>
 *   <tr style = "border: 1px solid black">
 *      <th style = "border: 1px solid black">9</th>
 *      <th style = "border: 1px solid black">action 2 weight</th>
 *   </tr>
 *   <tr style = "border: 1px solid black">
 *      <th style = "border: 1px solid black">10</th>
 *      <th style = "border: 1px solid black">action 3 weight</th>
 *   </tr>
 *   <tr style = "border: 1px solid black">
 *      <th style = "border: 1px solid black">11</th>
 *      <th style = "border: 1px solid black">action 4 weight</th>
 *   </tr>
 *   <tr style = "border: 1px solid black">
 *      <th style = "border: 1px solid black">12</th>
 *      <th style = "border: 1px solid black">action 5 weight</th>
 *   </tr>
 *   <tr style = "border: 1px solid black">
 *      <th style = "border: 1px solid black">13</th>
 *      <th style = "border: 1px solid black">action 6 weight</th>
 *   </tr>
 * </table>
 * <p>
 * The board also holds the array for each thing on the board (creatureArray, monsterArray, strawbArray and
 * mushroomArray).
 */
public class Board extends JFrame {

    /**
     * Instance variables control how large the board is, how long a generation is, how many generations there
     * are and how much health a creature starts with. All of these will affect survival rates of the creatures.
     */
	private static int ROWS = 30;
	private static int COLS = 30;
	private static int time = 10;
	private static int generations = 1000;
	private static int currentGen = 1;
	private static int creatureInitialHealth = 16;
	private static int bound = 10;

	private static Color monsterColor = Color.cyan;
	private static Color creatureColor = Color.yellow;
	private static Color strawbColor = Color.red;
	private static Color mushroomColor = Color.gray;

	private static int[][] creatureArray;
	private static int[][] monsterArray;
	private static int[][] strawbArray;
	private static int[][] mushroomArray;

	private static int creaturePop = 0;
	private static int monsterPop = 0;
	private static int strawbPop = 0;
	private static int mushroomPop = 0;

	private static JPanel mainPanel;
	private static JFrame frame;
	private static JFrame survivorFrame;
	private static BoardPanel[][] panel;
	private static Chart2D survivalChart;
	private static ITrace2D trace;

    /**
     * Creates the board in Swing by randomly placing creatures, monsters, mushrooms, strawberries or blank squares
     * down. Creatures are the most likely to be placed with a 30% chance with mushrooms, monsters and strawberries
     * having a 10% chance to be place. If no item is placed in a square it will be blank.
     * @param rows      How many rows the board should have
     * @param cols      How many columns the board should have
     */
	public Board(int rows, int cols) {

		// Instance variable set up
		ROWS = rows;
		COLS = cols;
		creatureArray = new int[ROWS][COLS];
		monsterArray = new int[ROWS][COLS];
		strawbArray = new int[ROWS][COLS];
		mushroomArray = new int[ROWS][COLS];

		// Set up view using Swing 
		panel = new BoardPanel[ROWS][COLS];
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(ROWS, COLS));
		frame = new JFrame("Evolution");
		frame.getContentPane().add(mainPanel);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.white);

		Random random = new Random();

		// Loop for each position on the board and randomly place a creature/monster/strawberry/mushroom
		for (int c = 0; c < COLS; c++) {
			for (int r  = 0; r < ROWS; r++) {

				int rand = random.nextInt(bound);

				if (rand == 1 || rand == 5 || rand == 8){
					panel[c][r] = new BoardPanel(creatureColor); //Creature
					creatureArray[c][r] = 1;
					Creature.getCreatures().add(new Creature(c,r, creatureInitialHealth, newChromosome())); 
					creaturePop++;
				}
				else if (rand == 2){
					panel[c][r] = new BoardPanel(monsterColor); //Monster
					monsterArray[c][r] = 1;
					Monster.getMonsters().add(new Monster(c,r));
					monsterPop++;
				}
				else if (rand == 3){
					panel[c][r] = new BoardPanel(strawbColor);  //Strawberry
					strawbArray[c][r] = 1;
					strawbPop++;
				}
				else if (rand == 4){
					panel[c][r] = new BoardPanel(mushroomColor); // Mushroom
					mushroomArray[c][r] = 1;
					mushroomPop++;
				}
				else {
					panel[c][r] = new BoardPanel(Color.white);
				}
				mainPanel.add(panel[c][r]);
			}
		}

		mainPanel.setPreferredSize(new Dimension(20*COLS, 20*ROWS));
		frame.pack();
		frame.setVisible(true);
	}

    /**
     * Moves creatures around the board for specified generations. First the creatures move one action and all
     * dead creatures get placed in the dead pile. Then monsters do one action. Then the board updates with
     * the movement and results from each.
     * <p>
     * Movements are only shown for the first and last generation. For all generations in between a graph is shown
     * with the survival rate for creatures after each generation. The survival Rate is the number of creatures
     * that survived the generation divided by the number of creatures that were placed to start the generation.
     * @param args          Main method args
     */
	public static void main(String[] args) {


		// Create a chart to view the survival rate of creatures as generations progress:  
		survivorFrame = new JFrame("Survival Rate");
		survivorFrame.setSize(600,500);
		survivalChart = new Chart2D();
		// Create an ITrace: 
		trace = new Trace2DSimple(); 
		trace.setColor(Color.green);
		// Add the trace to the chart.  
		survivalChart.addTrace(trace);    

		// Construct the board
		Board board = new Board(ROWS, COLS); // This is the initial generation

		// Repeat for generations to simulate evolution
		while (currentGen <= generations) {

		    if (currentGen == 1) {

				System.out.println("It was the beginning of time....");
				System.out.println(creaturePop + " creatures have randomly been placed");

				// For aesthetics
				try { Thread.sleep(1000);}
				catch (InterruptedException e) { e.printStackTrace();  }

				for (int t = 0; t < time; t ++) {


                    //Move all creatures based on random chromosome entries
					for (Creature creature : Creature.getCreatures())  {
						creature.actCreature();
					}
					Creature.getCreatures().removeAll(Creature.getDeadCreatures());

                    // Move all monsters
                    for (Monster monster : Monster.getMonsters()) {
                        monster.moveMonster(monster.findMoveDirection());
                    }
                    Creature.getCreatures().removeAll(Creature.getDeadCreatures()); // Make sure not to loop over dead creatures


                    try { Thread.sleep(500);}
					catch (InterruptedException e) { e.printStackTrace();  }
					redrawBoard(); //Show creature movement for the first generation
				}

                // To graph the Survival Rate
				double size = (double) Creature.getCreatures().size();
				double pop = (double) creaturePop;
				trace.addPoint(currentGen, (size/pop)*100);  // Graph the survival rate
				//trace.addPoint(currentGen, Creature.getAverageFitness()); // Graph the average fitness

				System.out.println("The initial survival rate was: " + size/pop*100);
				System.out.println("The initial average fitness was: " + Creature.getAverageFitness());
				System.out.println(size + " Creatures survived the first generation");
			}

			else if (currentGen < generations && currentGen > 1) {

			    newGen();

			    for (int t = 0; t < time; t ++) {

					// Move all creatures based on chromosomes from parents
					for (Creature creature : Creature.getCreatures()) { 
						creature.actCreature();
					}
					Creature.getCreatures().removeAll(Creature.getDeadCreatures());

                    // Move all monsters
                    for (Monster monster : Monster.getMonsters()) {
                        monster.moveMonster(monster.findMoveDirection());
                    }
                    Creature.getCreatures().removeAll(Creature.getDeadCreatures()); // Make sure not to loop over dead creatures
				}

				// Calculate survivor rate for the success graph
				double size = (double) Creature.getCreatures().size();
				double pop = (double) creaturePop;

				trace.addPoint(currentGen, (size/pop)*100); //Graph the survival rate
				//trace.addPoint(currentGen, Creature.getAverageFitness());  //Graph the average fitness
				
				// Now to make the chart visible
				survivorFrame.getContentPane().add(survivalChart);
				survivorFrame.setVisible(true);
			}
			
			else { 

			    // Wait to show creature movement in the last generation
				try { Thread.sleep(3000);}
				catch (InterruptedException e) { e.printStackTrace();  }

				System.out.println("\nIt is the final year: It is year: " + currentGen);
				newGen();
                redrawBoard();
				for (int t = 0; t <= time; t ++) {


					for (Creature creature : Creature.getCreatures()) { 
						creature.actCreature();
					}
					Creature.getCreatures().removeAll(Creature.getDeadCreatures());

					for (Monster monster : Monster.getMonsters()) {
                        monster.moveMonster(monster.findMoveDirection());
                    }
                    Creature.getCreatures().removeAll(Creature.getDeadCreatures()); // Make sure not to loop over dead creatures


                    try { Thread.sleep(500);}
					catch (InterruptedException e) { e.printStackTrace();  }
					redrawBoard();
				}
				
				// Calculate survivor rate for the success graph
				double size = (double) Creature.getCreatures().size();
				double pop = (double) creaturePop;

				trace.addPoint(currentGen, (size/pop)*100); //Graph the survival rate
				//trace.addPoint(currentGen, Creature.getAverageFitness());

				survivorFrame.getContentPane().add(survivalChart);
				survivorFrame.setVisible(true);

				System.out.println("The final average fitness was: " + Creature.getAverageFitness());
                System.out.println("The final survival rate(%) was: " + size/pop*100);
                System.out.println(size + " Creatures survived the last generation");

            }
			currentGen++;
		}
	}

    /**
     * Handles the change from one generation to the next. Survivors from the previous generation are used to create
     * the new chromosomes for the next generation. The board is also wiped and all items on the board are
     * randomly placed.
     *
     */
	private static void newGen() {

		// Woo these creatures survived!
        ArrayList<Creature> survivors = new ArrayList<>(Creature.getCreatures());

		// So that we don't add to the combine lists from different generations
		Creature.getCreatures().clear();
		Monster.getMonsters().clear();
		Creature.getDeadCreatures().clear();
		creaturePop=0;
		monsterPop=0;
		mushroomPop=0;
		strawbPop=0;

		// RESET EVERYTHING
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				creatureArray[c][r] = 0;
				monsterArray[c][r] = 0;
				mushroomArray[c][r] = 0;
				strawbArray[c][r] = 0;
			}
		}

        ArrayList<Creature> parents = Creature.parentSurvivors(survivors);

		// Replace all creatures/monsters/strawberries/mushrooms randomly
		for (int r = 0; r < ROWS; r ++) {
			for (int c = 0; c < COLS; c ++) {
				Random random = new Random();
				int rand = random.nextInt(bound);
				if (rand == 1 || rand == 5 || rand == 8) {
					creatureArray[c][r] = 1;
					int[] nextGenerationChromosome = nextGenerationChromosome(parents);
					Creature.getCreatures().add(new Creature(c,r, creatureInitialHealth, nextGenerationChromosome));
					creaturePop++;
				}
				else if (rand == 2){
					monsterArray[c][r] = 1;
					Monster.getMonsters().add(new Monster(c,r));
					monsterPop++;
				}
				else if (rand == 3){
					strawbArray[c][r] = 1;
					strawbPop++;
				}
				else if (rand == 4){
					mushroomArray[c][r] = 1;
					mushroomPop++;
				}
			}
		}
	}

    /**
     * How a chromosome is created from the surviving parents of the previous generation. Two of the fittest (better
     * than average remaining fitness) parents are randomly selected. Then a random crossover point is selected and
     * the two parent's chromosomes are combined to form the new chromosome for the new creature.
     * @param parents       The creatures that survived the previous generation
     * @return              An unique array (the chromosome) for the creature being created
     */
    private static int[] nextGenerationChromosome(ArrayList<Creature> parents) {

        Random random = new Random();

        //Find a random crossover Point
        int randomCrossoverPoint = random.nextInt(parents.size());

        //Find random Parents
        Creature randomParent1 = parents.get(random.nextInt(parents.size()));
        Creature randomParent2 = parents.get(random.nextInt(parents.size()));

        //Make sure they aren't the same
        while (randomParent1 == randomParent2)
            randomParent2 = parents.get(random.nextInt(parents.size()));


        //Combine the chromosomes
        int[] newGenerationChromosome = new int[randomParent1.getChromosome().length];
        for (int i = 0; i < randomCrossoverPoint; i++)
            newGenerationChromosome[i] = randomParent1.getChromosome()[i];
        for (int i = randomCrossoverPoint; i < randomParent1.getChromosome().length; i ++ )
            newGenerationChromosome[i] = randomParent2.getChromosome()[i];

        return newGenerationChromosome;

    }

    /**
     * Creates chromosomes for the very first generation. All chromosome entries are randomly generated.
     * @return              An unique array (the chromosome) for the creature being created
     */
	private static int[] newChromosome() {

		Random random = new Random();
		double rand = random.nextDouble();
		int[] chromosome = new int[13];

		//Strawberry boolean
		if (rand < .50) 
			chromosome[0] = 1; //Eat it 
		else 
			chromosome[0] = 0; 

		// Mushroom boolean
		rand = random.nextDouble();
		if (rand > .50) 
			chromosome[1] = 1; 	// Eat it 
		else 
			chromosome[1] = 0; 

		// Strawberry
		// Move towards, away, ignore, or at random is a strawberry is near
		rand =  random.nextDouble();

		if (rand < 0.25) 
			chromosome[2] = 1; // Towards
		else if (rand < 0.5 && rand >= 0.25)
			chromosome[2] = 2; // Away
		else if (rand < 0.75 && rand >= 0.5)
			chromosome[2] = 3; // Random
		else 
			chromosome[2] = 0; // Ignore

		// Mushroom:
		// Move Towards, away, ignore, or move at random for a near mushroom
		rand =  random.nextDouble();
		if (rand < 0.25) 
			chromosome[3] = 1; // Towards
		else if (rand < 0.5 && rand >= 0.25)
			chromosome[3] = 2; // Away
		else if (rand < 0.75 && rand >= 0.5)
			chromosome[3] = 3; // Random
		else 
			chromosome[3] = 0; // Ignore

		// Monster
		// Move Towards, away, ignore, or move at random for a near monster
		rand =  random.nextDouble();
		if (rand < 0.25)
			chromosome[4] = 1; // Towards
		else if (rand < 0.5 && rand >= 0.25)
			chromosome[4] = 2; // Away
		else if (rand < 0.75 && rand >= 0.5)
			chromosome[4] = 3; // Move Random
		else 
			chromosome[4] = 0; // Ignore

		// Creature
		// Move Towards, away, ignore, or move at random for a near creature
		rand =  random.nextDouble();
		if (rand < 0.25)
			chromosome[5] = 1; // Move Towards
		else if (rand < 0.5 && rand >= 0.25)
			chromosome[5] = 2; // Move Away
		else if (rand < 0.75 && rand >= 0.5)
			chromosome[5] = 3; // Move Random
		else 
			chromosome[5] = 0; // Ignore

		// Default Action
		// Still is uniformly random
		rand =  random.nextDouble();
		if (rand < 0.2) 
			chromosome[6] = 1; // Move Up
		else if (rand < 0.4 && rand >= 0.2)
			chromosome[6] = 2; // Move Down
		else if (rand < 0.6 && rand >= 0.4)
			chromosome[6] = 3; // Move Right
		else if (rand < 0.8 && rand >= 0.6)
			chromosome[6] = 4; // Move Left
		// Move randomly every time
		else {
			chromosome[6] = 0; // Move Random
		}

		// Randomize the weights to creature actions
		chromosome[7] = random.nextInt(6);
		chromosome[8] = random.nextInt(6);
		chromosome[9] = random.nextInt(6);
		chromosome[10] = random.nextInt(6);
		chromosome[11] = random.nextInt(6);
		chromosome[12] = random.nextInt(6);
		
		return chromosome;
	}

    /**
     * Redraws the board. Called after all creatures and monsters have done one action
     */
	private static void redrawBoard() {

        for (int c = 0; c < COLS; c++) {
		    for (int r = 0; r < ROWS; r++) {
                if (creatureArray[c][r] == 1)
                    panel[c][r].setColor(creatureColor);
				else if (monsterArray[c][r] == 1)
                    panel[c][r].setColor(monsterColor);
				else if (strawbArray[c][r] == 1)
					panel[c][r].setColor(strawbColor);
				else if (mushroomArray[c][r] == 1)
					panel[c][r].setColor(mushroomColor);
				else
					panel[c][r].setColor(Color.white);
		    }
		}

		frame.repaint();
	}


	//**********************************************************
	// Setters
	//**********************************************************

    /**
     *  Updates the creature array
     * @param array         The creature's 2D array that contains all creature locations
     */
	public static void setCreatureArray(int[][] array) {
		creatureArray = array;
	}

    /**
     * Updates the monster array
     * @param array         The monster's 2D array that contains all monster locations
     */
	public static void setMonsterArray(int[][] array) {
		monsterArray = array;
	}

    /**
     * Updates the mushroom array
     * @param array         The mushroom's 2D array that contains all mushroom locations
     */
	public static void setMushroomArray(int[][] array) {
		mushroomArray = array;
	}

    /**
     * Updates the strawberry array
     * @param array         The strawberry's 2D array that contains all strawberry locations
     */
	public static void setStrawbArray(int[][] array) {
		strawbArray = array;
	}


	//**********************************************************
	// Accessors
	//**********************************************************

    /**
     * Allows access to the creature array
     * @return              The creature's 2D array that contains all creature locations
     */
	public static int[][] getCreatureArray() {
		return creatureArray;
	}

    /**
     * UAllows access to the monster array
     * @return              The monster's 2D array that contains all monster locations
     */
	public static int[][] getMonsterArray() {
		return monsterArray;
	}

    /**
     * Allows access to the mushroom array
     * @return              The mushroom's 2D array that contains all mushroom locations
     */
	public static int[][] getMushroomArray() {
		return mushroomArray;
	}

    /**
     * Allows access to the strawberry array
     * @return              The strawberry's 2D array that contains all strawberry locations
     */
    public static int[][] getStrawbArray() {
        return strawbArray;
    }

    /**
     * Allows access to the board's number of rows
     * @return              the number of rows
     */
	public static int getRows() {
		return ROWS;
	}

    /**
     * Allows access to the board's number of columns
     * @return              the number of columns
     */
	public static int getCols() {
		return COLS;
	}
}





