import java.util.ArrayList;
import java.util.Arrays;
/**
 * Represents a single move of a piece.
 * 
 * @author Mckenna Cisler
 * @version 12.1.2015
 */
public class Move
{
    int x1, y1, x2, y2;
    Move precedingMove;
    boolean isJump;
    
    /**
     * Constructor for objects of class Move - initializes starting and final position.
     * @param x1 Starting x position.
     * @param y1 Starting y position.
     * @param x2 Ending x position.
     * @param y2 Ending y position.
     * @param precedingMove The move preceding this one (can be null if move is first)
     */
    public Move(int x1, int y1, int x2, int y2, Move precedingMove, boolean isJump)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.precedingMove = precedingMove;
        this.isJump = isJump;
    }

    /**
     * @return Returns a two-part array representing the coordinates of this move's starting position.
     */
    public int[] getStartingPosition()
    {
        int[] position = new int[2];
        position[0] = x1;
        position[1] = y1;
        return position;
    }
    
    /**
     * @return Returns a two-part array representing the coordinates of this move's ending position.
     */
    public int[] getEndingPosition()
    {
        int[] position = new int[2];
        position[0] = x2;
        position[1] = y2;
        return position;
    }
    
    /**
     * Finds the pieces jumped in this move.
     * (Get's inbetween jumps using recursion)
     * @return Returns an array of pieces that were jumped.
     * @param board The board to look for the pieces on.
     */
    public Piece[] getJumpedPieces(Board board)
    {
        // if this move wasn't a jump, it didn't jump a piece!
        if (isJump)
        {
            // create expandable list of all pieces
            ArrayList<Piece> pieces = new ArrayList<Piece>();
            
            // the piece this move is jumping should be between the start and end of this move
            // (the average of those two positions)
            int pieceX = (x1 + x2)/2;
            int pieceY = (y1 + y2)/2;
            
            // add this most recent jump...
            pieces.add(board.getValueAt(pieceX, pieceY));
            
            // ...but also go back to get the inbetween ones (if we're not the first move)
            if (precedingMove != null)
            {
                pieces.addAll(Arrays.asList(precedingMove.getJumpedPieces(board))); 
                // something is wrong (a preceding move isn't a jump) if this returns null, so let the error be thrown
            }
            
            // shorten and return
            pieces.trimToSize();
            return pieces.toArray(new Piece[1]); // convert to Piece array 
        }
        else
            return null;
    }
}
