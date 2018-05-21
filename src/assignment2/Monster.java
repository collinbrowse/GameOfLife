/* A class to represent a monster in the Game of Life
 * @author          Collin Browse
 */
package assignment2;
import java.util.ArrayList;
import java.util.Random;

/**
 * A Monster has a row and column coordinate on the board. A Monster has the ability to move around the board.
 * If a monster is ever next to a creature it will eat the creature. Otherwise the monster will move around the
 * board as long as nothing is in its way. If something besides a creature (other monster, mushroom or
 * strawberry) is in its way it will not move. A Monster does not have a health trait and can move for
 * forever without food in this simulation.
 */
public class Monster {

    /**
     * Cues for directions the monsters will move.
     */
	private static final int NORTH = 1;
	private static final int WEST = 2;
	private static final int SOUTH = 3;
	private static final int EAST = 4;
	private int r;
	private int c;
	private static ArrayList<Monster> monsters = new ArrayList<Monster>();

	/**
	 * Creates a Monster object with a row and column
	 * @param c         The column of the monster
	 * @param r         The row of the monster
	 */
	public Monster(int c, int r) {
		this.r = r;
		this.c = c;
	}

    /**
     * Moves the monster to eat a creature or if nothing else is there
     *
     * @param direction Which direction the monster should move
     */
    public void moveMonster(String direction) {

        int[][] creatureArray = Board.getCreatureArray();
        int[][] monsterArray = Board.getMonsterArray();
        int[][] strawbArray = Board.getStrawbArray();
        int[][] mushroomArray = Board.getMushroomArray();

        if (direction.equals("North") && r > 0) {

            monsterArray[c][r] = 0;

            // There is a creature
            if (creatureArray[c][r-1] == 1) {
                creatureArray[c][r-1] = 0; // Creature dies
                monsterArray[c][r-1] = 1; r--; // Monster moves there
                killOffCreature();
            }
            // There is a strawberry, mushroom or monster
            else if (strawbArray[c][r-1] == 1 || mushroomArray[c][r-1] == 1 || monsterArray[c][r-1] == 1) {
                // Don't move the monster
                monsterArray[c][r] = 1;
            }
            // There is nothing
            else {
                monsterArray[c][r-1] = 1; r--;
            }
        }

        else if (direction.equals("West") && c > 0) {

            monsterArray[c][r] = 0;

            // There is a creature
            if (creatureArray[c-1][r] == 1) {
                creatureArray[c-1][r] = 0;
                monsterArray[c-1][r] = 1; c--;
                killOffCreature();
            }
            // There is a strawberry, mushroom or monster
            else if (strawbArray[c-1][r] == 1 || mushroomArray[c-1][r] == 1 || monsterArray[c-1][r] == 1) {
                // Don't move the monster
                monsterArray[c][r] = 1;
            }
            // There is nothing
            else {
                monsterArray[c-1][r] = 1; c--;
            }
        }

        else if (direction.equals("South") && r < Board.getRows()-1) {

            monsterArray[c][r] = 0;

            // There is a creature
            if (creatureArray[c][r+1] == 1) {
                creatureArray[c][r+1] = 0;
                monsterArray[c][r+1] = 1; r++;
                killOffCreature();
            }
            // There is a strawberry, mushroom or monster
            else if (strawbArray[c][r+1] == 1 || mushroomArray[c][r+1] == 1 || monsterArray[c][r+1] == 1) {
                // Don't move the monster
                monsterArray[c][r] = 1;
            }
            // Nothing is there
            else {
                monsterArray[c][r+1] = 1; r++;
            }
        }

        else if (direction.equals("East") && c < Board.getCols()-1) {

            monsterArray[c][r] = 0;

            // There is a creature
            if (creatureArray[c+1][r] == 1) {
                creatureArray[c+1][r] = 0;
                monsterArray[c+1][r] = 1; c++;
                killOffCreature();
            }
            // There is a strawberry mushroom or monster
            else if (strawbArray[c+1][r] == 1 || mushroomArray[c+1][r] == 1 || monsterArray[c+1][r] == 1) {
                // Don't move the monster
                monsterArray[c][r] = 1;
            }
            // Nothing is there
            else {
                monsterArray[c+1][r] = 1; c++;
            }
        }

        Board.setStrawbArray(strawbArray);
        Board.setMushroomArray(mushroomArray);
        Board.setMonsterArray(monsterArray);
        Board.setCreatureArray(creatureArray);
    }


    /**
     * Finds which direction the monster should move. If there is a creature next to the monster the monster will
     * move that direction (and subsequently eat the creature)
     * @return          Which direction the monster should move
     */
	public String findMoveDirection () {
		// Check if there is a creature to eat
		// If yes return that direction
		for (Creature creature : Creature.getCreatures()) {
			if (creatureNear(creature) != "") {
				return creatureNear(creature);
			}
		}

		// If no monster is present, move at random
		Random random = new Random();
		int rand = random.nextInt(4)+1;
		if (rand == NORTH)
			return "North";
		else if (rand == WEST)
			return "West";
		else if (rand == SOUTH)
			return "South";
		else 
			return "East";
		
 	}


    /**
     * Finds the direction that the creature is in relation to this monster
     * @param creature  A creature object to see if this Monster is close
     * @return          The direction that the creature is in relation
     *                  to the Monster
     */
	private String creatureNear(Creature creature) {

           if (creature.getCurrentR() == this.r && creature.getCurrentC() == this.c+1)
        	   return "East";
           else if (creature.getCurrentR() == this.r && creature.getCurrentC() == this.c-1)
        	   return "West";
           else if (creature.getCurrentR() == this.r-1 && creature.getCurrentC() == this.c)
        	   return "North";
           else if (creature.getCurrentR() == this.r+1 && creature.getCurrentC() == this.c)
        	   return "South";
           else {
        	   return "";
           }
        }

    /**
     * Adds a creature to the dead pile
     */
    private void killOffCreature() {
        for (Creature creature : Creature.getCreatures()) {
            if (c == creature.getCurrentC() && r == creature.getCurrentR()) {
                Creature.getDeadCreatures().add(creature); //Add to the dead pile
            }
        }

    }
	
	//***************************************************************
	//Accessors
	//***************************************************************

    /**
     *  Allows access to all of the monsters of the generation
     * @return          The ArrayList containing all Monsters
     */
	public static ArrayList<Monster> getMonsters() {
		return monsters;
	}

	
}
