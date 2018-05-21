/* A Class that represents a Creature in the game of life
 * @author          Collin Browse
 */
package assignment2;
import java.util.ArrayList;
import java.util.Random;

/**
 * A Creature has the ability to move around the board. The goal of a creature is to survive each generation.
 * At every time step of the generation the creature has the ability to eat a Strawberry, eat a Mushroom, get eaten by
 * a Monster or to move to an empty space. If the Creature eats a strawberry it gains health and is able to continue
 * to the next time step. If a Creature eats a Mushroom it dies and the Mushroom and the Creature disappear from the
 * board. If the Creature is eaten by a Monster it dies and the Creature disappears from the board. If the creature
 * moves to an empty space it uses up one health. If a Creature's health ever reaches 0 it dies and is removed from
 * the board.
 * <p>
 * A Creature's movement is determined by its chromosome. In the beginning a chromosome is randomly generated. Then
 * after each generation the surviving parents are used to create the chromosomes for the next generation. The top
 * half of all surviving parents are selected based on their health left after the generation. Out of these "fit"
 * parents two are randomly selected. Their chromosomes are then combined based on a random break point. Each
 * Creature made in the next generation will have a chromosome with a combination of the two randomly selected parents
 * split by a randomly selected break point.
 * <p>
 * Because surviving parents are used to create the decisions for creatures of the next generation, the creatures of
 * the next generation will make better decisions than the creatures of the previous generation. This is the
 * genetic algorithm and allows for the creatures to learn and adapt as time goes on. There are many factors that
 * affect the survival rate of creatures: how many creatures are on the board vs how many monsters there are; How much
 * room the creatures have to move around i.e. how large the board is. But most importantly the method for selecting
 * the parents to create the next generations chromosomes. The method that was implemented selects two random parents
 * from the top half of the fittest parents. This method is not overly selective and allows for some bad genes to
 * persist through generations. Other methods for selecting fittest parents could have been used, however it is
 * interesting to see the increase in survival rates even while using parents without the absolute best genes.
 */
public class Creature {

    /**
     * A creature has a health that they start out with by default. It has a row and column position on the board.
     */
	private int health;
	private int r;
	private int c;
	private static ArrayList<Creature> creatures = new ArrayList<>(); //holds all creatures
	private static ArrayList<Creature> deadCreatures = new ArrayList<>(); // holds dead creatures
	private static ArrayList<Creature> parents = new ArrayList<>(); // Holds parent creatures
	private int[] chromosome; // chromosome for a creature

	/**
	 *  Creates the creature with properties from the board class and either a randomly created chromosome if
     *  in the first generation or a chromosome based on parents chromosomes if after the first generation.
	 * @param c             the column the creature is in
	 * @param r             the row the creature is in
	 * @param health        the creature's health. The creature dies when health is 0
	 * @param chromosome    an array that determines the creatures actions
	 */
	public Creature(int c, int r, int health, int[] chromosome) {
	    // Initialize Creature properties
		this.c = c;
		this.r = r;
		this.health = health;
		this.chromosome = chromosome;	
	}

	/**
	 * Tells the creature to the smartest action from the result of chooseAct()
     * <p>
     * Calls moveCreature() to perform the movement
	 */
	public void actCreature() {

		// Find out what move the creature should make
		int go = chooseAct();

		String direction;
        if (go == 1)
			direction = "eatS";
		else if (go == 2)
			direction = "eatM";
		else if (go == 3)
			direction = "North";
		else if (go == 4)
			direction = "West";
		else if (go == 5) 
			direction = "South";
		else if (go == 6)
			direction = "East";
		else {
			Random random = new Random();
			int rand = random.nextInt(4)+1;
			if (rand == 1)
				direction = "North";
			else if (rand == 2)
				direction = "West";
			else if (rand == 3)
				direction = "South";
			else
				direction = "East";
		}
			
		// Move the creature
		if (direction.equals("eatS")) {
			moveCreature(nearestStrawb());
		}
		else if (direction.equals("eatM")) {
			moveCreature(nearestMushroom());
		}
		else {
			moveCreature(direction);
		}

		// Did the creature run out of health?
		if (health <= 0) {
			Creature.getDeadCreatures().add(this); //add to dead pile
			int[][] creatureArray = Board.getCreatureArray();
			creatureArray[c][r] = 0;
			Board.setCreatureArray(creatureArray);
		}
		
	}

	/**
	 * The creature decides what the smartest movement is based on its chromosome. It takes into account the weights
     * of each decision for the smartest movement.
	 * @return              The smartest action for the creature based on its chromosome
     *
	 */
	private int chooseAct() {

		int largestWeight = 0;
		int chromosomeWeights = 0;
		int smartestAction = -1;
		ArrayList<Integer> possibleActions = new ArrayList<>(); // Holds action list for each creature at each time


		if (strawbPresent()) // A strawberry is present
			if(chromosome[0] > 0) // when chromosome[0] > 0 Eat a strawb if it is present
				possibleActions.add(1); // So we note that in our list of actions to take

		if (mushroomPresent()) // A mushroom is present
			if(chromosome[1] > 0)  // when chromosome[1] > 0 Eat a mushroom if it is present
				possibleActions.add(2); // So we note that in our list of actions to take

		if (nearestStrawb().compareTo("") != 0) // A strawberry is nearby
			if (chromosome[2] > 0)  //
				possibleActions.add(3); // Note in our actionList

		if (nearestMushroom().compareTo("") != 0) // A mushroom is nearby
			if (chromosome[3] > 0)
				possibleActions.add(4); // Note that

		if (nearestMonster().compareTo("") != 0) // A monster is nearby
			if (chromosome[4] > 0)
				possibleActions.add(5); // Note

		if (nearestCreature().compareTo("") != 0) // A Creature is nearby
			if (chromosome[5] != 0)
				possibleActions.add(6); //Note

		// Else, nothing is nearby
		// refer to the creatures default action
		if (possibleActions.isEmpty()) {
			// If a default action was not chosen (randomly)
			if (chromosome[6] == 0) {
				Random random = new Random();
				return random.nextInt(5) + 1; // chromosome states that default action is random
			}
			else // If a default action was chosen (randomly)
				return chromosome[6];
		}

		// The creature decides what to do even though there are multiple choices for it
        // Chromosome weights tell the creature what the smartest action is
		else { 
			for (Integer i : possibleActions) {
				if (largestWeight < chromosome[chromosomeWeights+i]) {
					largestWeight = chromosome[chromosomeWeights+i];
					smartestAction = i;
				}
			}

			// Do the smartest action
			if (smartestAction == 1) //Wants to eat a strawberry
				return 1;
			if (smartestAction == 2) //Wants to eat a mushroom
				return 2;
			if (smartestAction == 3) //Respond
				return chooseAction(3, Board.getStrawbArray());
			if (smartestAction == 4)
				return chooseAction(4, Board.getMushroomArray());
			if (smartestAction == 5)
				return chooseAction(5, Board.getMonsterArray());
			if (smartestAction == 6)
				return chooseAction(6, Board.getCreatureArray());

			return -1;
		}
	}

    /**
     *  Chooses the action to do based on what the chromosome says for that item and its weight for that action.
     * @param chromosome    Which object (food/monster) the creature is reacting to
     * @param location      The 2D array for the object (food/monster) that the creature is reacting to
     * @return              Does the creature move towards/away from/random/ignore the item?
     */
	private int chooseAction(int chromosome, int[][] location) {

		int direction = 0;

		// Find out where the item is next to us
		if (r > 0 && location[c][r-1] == 1)
			direction = 3; //North
		else if (c > 0 &&location[c-1][r] == 1)
			direction = 4; // West
		else if (r < Board.getRows()-1 && location[c][r+1] == 1)
			direction = 5; // South
		else if (c < Board.getCols()-1 && location[c+1][r] == 1)
			direction = 6; // East

		// If the chromosome tells us to move toward the item, return that instruction
		if (getChromosome()[chromosome] == 1) {
			return direction;
		}
		// If the chromosome tells us to move away from the item, return that instruction
		else if (getChromosome()[chromosome] == 2) {
			if (direction == 3) 
				return 5; // item is North; move south
			else if (direction == 4) 
				return 6; // item is West, move north
			else if (direction == 5) 
				return 3; // Item is South, move North
			else 	
				return 4; // Item is East, move west
		}

		// Random Movement
		else if (getChromosome()[chromosome] == 3) {
			Random random = new Random();
			return random.nextInt(4) + 3;
		}
		// Ignore item
		return -1;
	}

    /**
     *  Performs the movement of the creature. This method will update the arrays for Strawberries,
     *  Mushrooms, Creatures and Monsters based and the movement that was performed by the creature.
     * @param direction     The direction to move
     */
    private void moveCreature(String direction) {

        int[][] creatureArray = Board.getCreatureArray();
        int[][] monsterArray = Board.getMonsterArray();
        int[][] mushroomArray = Board.getMushroomArray();
        int[][] strawbArray = Board.getStrawbArray();


        if (direction.equals("West") && c > 0) {

            creatureArray[c][r] = 0;

            // There is a strawberry
            if (strawbArray[c-1][r] == 1)  {
                strawbArray[c-1][r] = 0;
                creatureArray[c-1][r] = 1; c--;
                health+=4;
            }
            // There is a mushroom or a monster
            else if (mushroomArray[c-1][r] == 1 || monsterArray[c-1][r] == 1) {
                deadCreatures.add(this); //add to dead pile

                if (mushroomArray[c-1][r] == 1)
                    mushroomArray[c-1][r] = 0;
                health = 0;
            }
            // There is a creature
            else if (creatureArray[c-1][r] == 1) {
                // Do not move
                creatureArray[c][r] = 1;
            }
            // There is nothing
            else {
                creatureArray[c-1][r] = 1; c--;
                health--;
            }
        }


        else if (direction.equals("North") && r > 0) {

            creatureArray[c][r] = 0;

            // There is a strawberry
            if (strawbArray[c][r-1] == 1)  {
                strawbArray[c][r-1] = 0;
                creatureArray[c][r-1] = 1; r--;
                health+=4;
            }
            // There is a mushroom or a monster
            else if (mushroomArray[c][r-1] == 1 || monsterArray[c][r-1] == 1) {
                deadCreatures.add(this); //add to dead pile

                if (mushroomArray[c][r-1] == 1)
                    mushroomArray[c][r-1] = 0;
                health = 0;
            }
            // There is a creature
            else if (creatureArray[c][r-1] == 1) {
                // Do not move
                creatureArray[c][r] = 1;
            }
            // There is nothing
            else  {
                creatureArray[c][r-1] = 1; r--;
                health--;
            }
        }

        else if (direction.equals("East") && c < Board.getCols()-1) {

            creatureArray[c][r] = 0;

            // There is a strawberry
            if (strawbArray[c+1][r] == 1)  {
                strawbArray[c+1][r] = 0;
                creatureArray[c+1][r] = 1; c++;
                health += 4;

            }
            // There is a mushroom or a monster
            else if (mushroomArray[c+1][r] == 1 || monsterArray[c+1][r] == 1) {
                deadCreatures.add(this); //add to dead pile

                if (mushroomArray[c+1][r] == 1 )
                    mushroomArray[c+1][r] = 0;
                health = 0;
            }
            // There is a creature
            else if (creatureArray[c+1][r] == 1) {
                // Do not move
                creatureArray[c][r] = 1;
            }
            // There is nothing
            else {
                creatureArray[c+1][r] = 1; c++;
                health --;

            }
        }

        else if (direction.equals("South") && r < Board.getRows()-1) {
            creatureArray[c][r] = 0;

            // There is a strawberry
            if (strawbArray[c][r+1] == 1)  {
                strawbArray[c][r+1] = 0;
                creatureArray[c][r+1] = 1; r++;
                health += 4;
            }
            // There is a mushroom or monster
            else if (mushroomArray[c][r+1] == 1 || monsterArray[c][r+1] == 1) {
                deadCreatures.add(this); //add to dead pile

                if (mushroomArray[c][r+1] == 1)
                    mushroomArray[c][r+1] = 0;
                health = 0;
            }
            // There is a creature
            else if (creatureArray[c][r+1] == 1) {
                // Do not move
                creatureArray[c][r] = 1;
            }
            // There is nothing
            else {
                creatureArray[c][r+1] = 1; r++;
                health--;
            }
        }

        Board.setCreatureArray(creatureArray);
        Board.setMonsterArray(monsterArray);
        Board.setMushroomArray(mushroomArray);
        Board.setStrawbArray(strawbArray);
    }


    /**
     * Finds out if there is a strawberry next to this creature
     * @return              Is there a strawberry next to this creature
     */
	private boolean strawbPresent() {

		int[][] strawbArray = Board.getStrawbArray();

		if (c != 0 && strawbArray[c-1][r] == 1 )
			return true;
		else if (r != 0 && strawbArray[c][r-1] == 1 )
			return true;
		else if (c != Board.getCols()-1 && strawbArray[c+1][r] == 1)
			return true;
		else
		    return r != Board.getRows() - 1 && strawbArray[c][r + 1] == 1;
	}

    /**
     * Finds out if there is a mushroom next to this creature
     * @return              Is there a mushroom next to this creature
     */
    private boolean mushroomPresent() {

        int[][] mushroomArray = Board.getMushroomArray();

        if (c != 0 && mushroomArray[c-1][r] == 1 )
            return true;
        else if (r != 0 && mushroomArray[c][r-1] == 1 )
            return true;
        else if (c != Board.getCols()-1 && mushroomArray[c+1][r] == 1)
            return true;
        else
            return r != Board.getRows() - 1 && mushroomArray[c][r + 1] == 1;
    }

    /**
     * Finds out which direction the nearest strawberry is from this creature
     * @return              The direction the strawberry is in relation to this creature
     */
	private String nearestStrawb() {

		int[][] strawbArray = Board.getStrawbArray();

		if (c != 0 && strawbArray[c-1][r] == 1 )
			return "West";
		else if (r != 0 && strawbArray[c][r-1] == 1 )
			return "North";
		else if (c != Board.getCols()-1 && strawbArray[c+1][r] == 1)
			return "East";
		else if (r != Board.getRows()-1 && strawbArray[c][r+1] == 1)
			return "South";
		else 
			return "";
	}

    /**
     * Finds out which direction the nearest mushroom is from this creature
     * @return              The direction the mushroom is in relation to this creature
     */
	private String nearestMushroom() {

		int[][] mushroomArray = Board.getMushroomArray();

		if (c != 0 && mushroomArray[c-1][r] == 1 )
			return "West";
		else if (r != 0 && mushroomArray[c][r-1] == 1 )
			return "North";
		else if (c != Board.getCols()-1 && mushroomArray[c+1][r] == 1)
			return "East";
		else if (r != Board.getRows()-1 && mushroomArray[c][r+1] == 1)
			return "South";
		else 
			return "";
	}

    /**
     * Finds out which direction the nearest creature is from this creature
     * @return              The direction the creature is in relation to this creature
     */
    private String nearestCreature() {

		int[][] creatureArray = Board.getCreatureArray();

		if (c != 0 && creatureArray[c-1][r] == 1 )
			return "West";
		else if (r != 0 && creatureArray[c][r-1] == 1 )
			return "North";
		else if (c != Board.getCols()-1 && creatureArray[c+1][r] == 1)
			return "East";
		else if (r != Board.getRows()-1 && creatureArray[c][r+1] == 1)
			return "South";
		else 
			return "";
	}

    /**
     * Finds out which direction the nearest monster is from this creature
     * @return              The direction the monster is in relation to this creature
     */
	private String nearestMonster() {

		int[][] monsterArray = Board.getMonsterArray();

		if (c != 0 && monsterArray[c-1][r] == 1 )
			return "West";
		else if (r != 0 && monsterArray[c][r-1] == 1 )
			return "North";
		else if (c != Board.getCols()-1 && monsterArray[c+1][r] == 1)
			return "East";
		else if (r != Board.getRows()-1 && monsterArray[c][r+1] == 1)
			return "South";
		else 
			return "";
	}


    /**
     * Finds a predetermined (6) number of parents from the pool of fittest parents at the end of the generation. The
     * parents are randomly selected.
     * @param survivors     Every creature from the previous generation with health greater than 0
     * @return              6 (int: numParents) random parents that were in the top half of fitness
     */
	public static ArrayList<Creature> parentSurvivors(ArrayList<Creature> survivors) {

		parents.clear();
		int numParents = 6;
		ArrayList<Creature> fittestParents = new ArrayList<>();
		ArrayList<Creature> selectedParents = new ArrayList<>();

		double avFitness = getAverageFitness(survivors);

        // Add every creature with above average fitness to a list
		for (Creature fit : survivors) {
			if (fit.health >= avFitness)
			    fittestParents.add(fit);
		}

		// Choose randomly out of the top half
		while (selectedParents.size() < numParents && fittestParents.size() > 0) {
			Random random = new Random();
			int rand = random.nextInt(fittestParents.size());
			Creature Random = fittestParents.get(rand); // random creature in list

            selectedParents.add(Random);            // Add the parent to the list
			fittestParents.remove(rand);  			 // Make sure there are no duplicate creatures
		}

		parents = selectedParents;
		return parents;
	}



	//***************************************************************
	//Accessors
	//***************************************************************

    /**
     * Allows access to the current row of this creature
     * @return                  The current row of this creature
     */
	public int getCurrentR() {
		return r;
	}

    /**
     * Allows access to the current column of this creature
     * @return                  The current column of this creature
     */
	public int getCurrentC() {
		return c;
	}

    /**
     * Allows access to the chromosome of this creature
     * @return                  The chromosome of this creature
     */
	public int[] getChromosome() {
		return chromosome;
	}

    /**
     * Allows access to all alive creatures of this generation
     * @return                  All alive creatures
     */
	public static ArrayList<Creature> getCreatures() {
		return creatures;
	}

    /**
     * Allows access to all the dead creatures of this generation
     * @return                  All dead creatures
     */
	public static ArrayList<Creature> getDeadCreatures() {
		return deadCreatures;
	}

    /**
     * Allows access to the average fitness of all alive parents from the generation
     * @return                  The average fitness from the surviving creatures of the generation
     */
	public static double getAverageFitness() {
		return getAverageFitness(Creature.getCreatures());
	}

    /**
     * Helper method for getAverageFitness()
     * Allows access to the average fitness of all alive parents from the generation
     * @param survivors         The survivors of the generation
     * @return                  The average fitness (health remaining)
     */
	private static double getAverageFitness(ArrayList<Creature> survivors) {
		// Get the fittest half of all survivors
		double healthSum = 0;
		double maxHealth = 0;
		double minHealth = 100;
		for (Creature fit : survivors) {
            if (fit.health > maxHealth)
                maxHealth = fit.health;
		    if (fit.health < minHealth)
		        minHealth = fit.health;
		    healthSum += fit.health;
        }

        if (healthSum != 0)
			healthSum = healthSum/survivors.size();
		return healthSum;
	}

}
