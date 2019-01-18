package lab1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;

public class SnakeModel extends GameModel {
    public enum Directions {
        EAST(1, 0),
        WEST(-1, 0),
        NORTH(0, -1),
        SOUTH(0, 1),
        NONE(0, 0);

        private final int xDelta;
        private final int yDelta;

        Directions(final int xDelta, final int yDelta) {
            this.xDelta = xDelta;
            this.yDelta = yDelta;
        }

        public int getXDelta() {
            return this.xDelta;
        }

        public int getYDelta() {
            return this.yDelta;
        }
    }
    
    int nbrOfTicks = 0;
    /** Graphical representation of a head. */
    private static final GameTile HEAD_TILE = new RectangularTile(Color.red);
    
    /** Graphical representation of piece of the body. */
    private static final GameTile BODY_TILE = new RectangularTile(Color.GREEN);
    
    /** Graphical representation of the food. */
    private static final GameTile FOOD_TILE = new RoundTile(Color.RED, Color.BLUE, 2.0, 0.5);
    
    /** Graphical representation of a blank tile. */
    private static final GameTile BLANK_TILE = new GameTile();
    
    /** A Queue containing the position of all the body pieces. */
    private final ArrayDeque<Position> body = new ArrayDeque<Position>();
    
    /** The position of the head. */
    private Position headPosition;
    
    /** The position of the food. */
    private Position foodPosition;
    
    /** The direction of the snake. */
    private Directions direction = Directions.NORTH;

    /** The total foods collected */
    private int score;
    
    
    private Dimension size = getGameboardSize(); 
    
    /** A method to add one food tile to the board. */
    private void addFood() {
        this.foodPosition = new Position((int) (Math.random() * size.width),
                                        (int) (Math.random() * size.height));
        if(isPositionEmpty(foodPosition)) {
            setGameboardState(this.foodPosition, FOOD_TILE);
        }
        else
        	// A recursive call of the method to make sure a blank tile is found
        	addFood();
  
	}

    /**
	 * Create a new model for the snake game.
	 */
    public SnakeModel() {      
        // Make the gameboard all blank.
        for (int i = 0; i < size.width; i++) {
            for (int j = 0; j < size.height; j++) {
                setGameboardState(i, j, BLANK_TILE);
            }
        }
        
        // Insert the head in the middle of the gameboard.
        this.headPosition = new Position(size.width / 2, size.height / 2);
        setGameboardState(this.headPosition, HEAD_TILE);
        
        // Placing the first food tile, the rest will be placed from gameUpdate
        addFood();
        
        // Giving the snake a single body tile to start with. Otherwise its not a snake right?
        body.add(new Position(size.width / 2+1, size.height / 2));
        setGameboardState(body.getLast(), BODY_TILE);
       
    }
    
    /**
	 * Update the direction of the snake
	 * according to the user's keypress.
	 */
    private void updateDirection(final int key) {
        switch (key) {
        case KeyEvent.VK_LEFT:
            if (this.direction != Directions.EAST) this.direction = Directions.WEST;
            break;    
        case KeyEvent.VK_UP:
            if (this.direction != Directions.SOUTH) this.direction = Directions.NORTH;
            break;
        case KeyEvent.VK_RIGHT:
            if (this.direction != Directions.WEST) this.direction = Directions.EAST;
            break;
        case KeyEvent.VK_DOWN:
            if (this.direction != Directions.NORTH) this.direction = Directions.SOUTH;
            break;
        default:
            // Don't change direction if another key is pressed
            break;
        }
    }
    
    /** Checking if a certain position is empty. */
    private boolean isPositionEmpty(final Position pos) {
        return (getGameboardState(pos) == BLANK_TILE);
    }
    
    /**
     * Get next position of the head.
     */
    private Position getNextHeadPos() {
        return new Position(
                this.headPosition.getX() + this.direction.getXDelta(),
                this.headPosition.getY() + this.direction.getYDelta());
    }
    
    /**
	 * This method is called repeatedly so that the
	 * game can update its state.
	 * 
	 * @param lastKey
	 *            The most recent keystroke.
	 */
    @Override
    public void gameUpdate(final int lastKey) throws GameOverException {
        updateDirection(lastKey);
        
        // Throwing exception if snake goes outside the board
        if (isOutOfBounds(getNextHeadPos())) {
            System.out.println(nbrOfTicks);
            throw new GameOverException(this.score);
        }
        
        // Add a body piece if you eat food, else move one step forward.
        if (getGameboardState(getNextHeadPos()) == FOOD_TILE) {
        	body.addFirst(this.headPosition);
        	setGameboardState(this.headPosition, BODY_TILE);
        	score++;
        	
            // Ending game if snake reaches max length
            if (score == size.width * size.height - 2){
                System.out.println(nbrOfTicks);
                setGameboardState(getNextHeadPos(), HEAD_TILE);
                setGameboardState(this.headPosition, BODY_TILE);
                this.score = score + 2;
                throw new GameOverException(this.score);
            }
            addFood();
        }
        
        else {
        	body.addFirst(this.headPosition);
            setGameboardState(body.getFirst(), BODY_TILE);
            setGameboardState(body.getLast(), BLANK_TILE);
        	body.removeLast();
        }
        
        // Moving the head's position.
        this.headPosition = getNextHeadPos();
        
        // Check if head collide with body.
        if (getGameboardState(this.headPosition) == BODY_TILE) {
            System.out.println(nbrOfTicks);
            throw new GameOverException(this.score);
        }
        
        // Draw head at new position.
        setGameboardState(this.headPosition, HEAD_TILE);
        
        nbrOfTicks++;
    }


	/**
     * 
     * @param pos The position to test.
     * @return <code>false</code> if the position is outside the playing field, <code>true</code> otherwise.
     */
    private boolean isOutOfBounds(Position pos) {
        return pos.getX() < 0 || pos.getX() >= getGameboardSize().width
                || pos.getY() < 0 || pos.getY() >= getGameboardSize().height;
    }  
}
