import java.util.ArrayList;
import java.util.Arrays;
/**
 * A class representing a game piece, and handling interactions with it.
 * 
 * @author Mckenna Cisler
 * @version 11.23.2015
 */
public class Piece
{
    private int x;
    private int y;
    private boolean isKing = false;
    public boolean isWhite;

    /**
     * Constructor for objects of class Piece
     * Initializes position and color.
     * @param x The x position of this piece.
     * @param y The y position of this piece.
     * @param isWhite Whether this piece is white or black.
     */
    public Piece(int x, int y, boolean isWhite)
    {
        this.x = x;
        this.y = y;
		this.isWhite = isWhite;
    }

    /**
     * @return Returns a two-part array representing the coordinates of this piece's position.
     */
    public int[] getCoordinates()
    {
        int[] coordinates = new int[2];
        coordinates[0] = this.x;
        coordinates[1] = this.y;
        return coordinates;
    }
    
    /**
     * @return Returns a string representation of this given piece
     */
    public String getString()
    {
        String baseSymbol;

        if (isWhite)
            baseSymbol = "W";
        else
            baseSymbol = "B";

        if (isKing)
            baseSymbol += "K";
        else
            baseSymbol += " "; // add a space in the non-king state just to keep consistency

        return baseSymbol;
    }

    /**
     * Switches this piece to a king (TODO: MAY BE UNNECCESARY DUE TO BELOW METHOD!!)
     */
    private void setKing()
    {
        isKing = true;
    }
    
    /**
     * Switches this peice to be a king if it is at the end of the board.
     * Should be called after every move.
     */
    public void checkIfShouldBeKing(Board board)
    {
        // if the piece is white, it's a king if it's at the +y, otherwise if its black this happens at the -y side
        if (isWhite && this.y == board.size - 1 || 
            !isWhite && this.y == 0)
            this.setKing();
    }

    /**
     * Moves this piece's reference of its position (DOES NOT ACTUALLY MOVE ON BOARD)
     * @param x The x coordinate of the move
     * @param y The y coordinate of the move
     */
    public void moveTo(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Generates all physically possible moves of the given piece.
     * (Only actually generates the non-jumping moves - jumps are done recusively in getAllPossibleJumps)
     * @return Returns a list of all the moves (including recusively found jumps), including each individual one involved in every jump.
     * @param board The board to work with - assumed to be flipped to correspond to this piece's color.
     */
    public Move[] getAllPossibleMoves(Board board)
    {
        // create expandable list of all moves
        ArrayList<Move> moves = new ArrayList<Move>();
        
        // change y endpoints based on kingness and color=direction of movement
        int startingY, yIncrement;
        if (isWhite)
        {
            // if it's white, we move from further down the board backwards to possible king position
            startingY = this.y + 1; 
            yIncrement = -2;
        }
        else 
        {
            // if it's black, we move from further up the board forward to possible king position
            startingY = this.y - 1;
            yIncrement = 2;
        }
        
        // use kingess to determine number of rows to check
        int rowsToCheck = 1; // default as non-king
        if (this.isKing)
            rowsToCheck = 2;
        
        // iterate over the four spaces where normal (non-jumping) moves are possible        
        for (int x = this.x - 1; x <= this.x + 1; x += 2)
        {
            // go over the rows (or row) (we iterate the number of times determined by the kingess above)
            int y = startingY - yIncrement; // add this so we can add the normal increment before the boundary checks
            for (int i = 0; i < rowsToCheck; i++) 
            {
                // increment y if we need to (this will have no effect if we only run one iteration)
                y += yIncrement;
                
                // check for going off end of board, in which case just skip this iteration (we may do this twice if at a corner)
                if (board.isOverEdge(x, y))
                    continue;
                
                // add a move here if there's not a piece 
                if (board.getValueAt(x, y) == null)
                {
                    // this is not jump move in any case, and is always the first move
                    moves.add(new Move(this.x, this.y, x, y, null, false)); 
                }
            }
        }
        
        // after we've checked all normal moves, look for and add all possible jumps (recusively as well - I mean ALL jumps)
        Move[] possibleJumps = this.getAllPossibleJumps(board, null);
        if (possibleJumps != null)
            moves.addAll(Arrays.asList(possibleJumps));

        // IF there are some moves, shorten and return ArrayList as a normal array
        if (!moves.isEmpty())
        {
            moves.trimToSize();
            return moves.toArray(new Move[1]); // convert to Move objects
        }
        else 
            return null; // return null otherwise to symbolize no moves
    }
    
    /**
     * Finds all jumping moves originating from this piece.
     * Does this recursivly; for each move a new imaginary piece will be generated,
     * and this function will then be called on that piece to find all possible subsequent moves.
     * @param board The board to work with - assumed to be flipped to correspond to this piece's color.
     * @param precedingMove The moves preceding the call to search for moves off this piece - only used
     * in recursion, should be set to null at first call. (if it's not, it means this piece is imaginary).
     */
    private Move[] getAllPossibleJumps(Board board, Move precedingMove)
    {
        // create expandable list of all moves
        ArrayList<Move> moves = new ArrayList<Move>();
        
        // this is the same as above except we're doing a large cube (4x4)
        // change y endpoints based on kingness and color=direction of movement
        int startingY, yIncrement;
        if (isWhite)
        {
            // if it's white, we move from further down the board backwards to possible king position
            startingY = this.y + 2;
            yIncrement = -4;
        }
        else 
        {
            // if it's black, we move from further up the board forward to possible king position
            startingY = this.y - 2;
            yIncrement = 4;
        }
        
        // use kingess to determine number of rows to check
        int rowsToCheck = 1; // default as non-king
        if (this.isKing)
            rowsToCheck = 2;
        
        // iterate over the four spaces where normal (non-jumping) moves are possible        
        for (int x = this.x - 2; x <= this.x + 2; x += 4)
        {
            // go over the rows (or row) (we iterate the number of times determined by the kingess above)
            int y = startingY - yIncrement; // add this so we can add the normal increment before the boundary checks in the loop
            for (int i = 0; i < rowsToCheck; i++) 
            {
                // increment y if we need to (this will have no effect if we only run one iteration)
                y += yIncrement;
                
                // check for going off end of board, in which case just skip this iteration (we may do this twice if at a corner)
                if (board.isOverEdge(x, y))
                    continue;
                
                // don't try to go backward to our old move start so we don't get in infinite recursion loops
                if (precedingMove != null &&
                    x == precedingMove.getStartingPosition()[0] && 
                    y == precedingMove.getStartingPosition()[1])
                    continue;
                
                // test if there is a different-colored piece between us (at the average of our position) and the starting point 
                // AND that there's no piece in the planned landing space (meaning we can possible jump there)
                Piece betweenPiece = board.getValueAt( (this.x + x)/2 , (this.y + y)/2 );
                if (betweenPiece != null &&
                    betweenPiece.isWhite != this.isWhite &&
                    board.getValueAt(x, y) == null)
                {
                    // in which case, add a move here, and note that it is a jump (we may be following some other jumps)
                    Move jumpingMove = new Move(this.x, this.y, x, y, precedingMove, true); // origin points are absolute origin (ORIGINAL piece)
                    
                    // then add it to our list
                    moves.add(jumpingMove);
                      
                    // after jumping, create an imaginary piece as if it was there to look for more jumps
                    Piece imaginaryPiece = new Piece(x, y, this.isWhite);
                    
					// correspond possible jumps to this piece's kingness
               		if (this.isKing) imaginaryPiece.setKing();
                    
                    // find possible subsequent moves recusivly
                    Move[] subsequentMoves = imaginaryPiece.getAllPossibleJumps(board, jumpingMove);
                    
                    // add these moves to our list if they exist, otherwise just move on to other possibilities
                    if (subsequentMoves != null)
                        moves.addAll(Arrays.asList(subsequentMoves));
                }
            }
        }

        // IF there are some moves, shorten and return ArrayList as a normal array
        if (!moves.isEmpty())
        {
            moves.trimToSize();
            return moves.toArray(new Move[1]); // convert to Move arrays
        }
        else 
            return null; // return null otherwise to symbolize no moves
    }
}
