/**
 * Stores and handles interaction with the game board.
 * 
 * @author Mckenna Cisler
 * @version 11.23.2015
 */
public class Board
{
    // global vars
    public Piece[][] boardArray;
    public int size;

    /**
     * Responsible for generating a brand new board
     * @param size The size of the board (8 for common checkers)
     * NOTE: currently will probably break with other than 8 as size!
     */
    public Board(int size)
    {
        // new board is just empty
        this.boardArray = new Piece[size][size];
        
        // store the size for further use
        this.size = size;
        
        // setup the starting positions
        setupBoard();
    }
    
    /**
     * Responsible for generating a board based on another board
     */
    public Board(Board board)
    {
        // just transfer stuff
        this.boardArray = board.boardArray;
        this.size = board.size;
    }
    
    /**
     * Fills the board with pieces in their starting positions.
     * Adds WHITE pieces at the top to start (so white should move first)
     */
    public void setupBoard()
    {
        for (int y = 0; y < size; y++)
        {
            for (int x = 0; x < size; x++)
            {
                // add white pieces to the top (in a checkerboard pattern of black spaces - not on white spaces)
                if (y < 3 && isCheckerboardSpace(x, y))
                {
                    this.boardArray[y][x] = new Piece(x, y, true);
                }
                // ... and black pieces to the bottom in the opposite pattern
                else if (y >= size - 3 && isCheckerboardSpace(x, y))
                {
                    this.boardArray[y][x] = new Piece(x, y, false);
                }
            }
        }
    }
    
    /**
     * Using the given move and piece, move the piece on the board and apply it to this board.
     * @param move The Move object to execute on the piece and board.
     * @param piece The Piece object that will be moved.
     */
    public void applyMoveToBoard(Move move, Piece piece)
    {
        // NOTE: at this point, the starting position of the move (move.getStartingPosition) will not neccesarily
        // be equal to the piece's location, because jumping moves have no understanding of the root move
        // and therefore can only think back one jump. WE ARE PRESUMING that the piece given to this function
        // is the one which the move SHOULD be applied to, but due to this issue we can't test this.
        
        int[] moveStartingPos = piece.getCoordinates();
        int[] moveEndingPos = move.getEndingPosition();
        
        // find any pieces we've jumped in the process, and remove them as well
        Piece[] jumpedPieces = move.getJumpedPieces(this);
        if (jumpedPieces != null)
        {
            // loop over all jumped pieces and remove them
            for (int i = 0; i < jumpedPieces.length; i++)
            {
                if (jumpedPieces[i] != null) // apparently this can happen... ?????
                {
                    this.setValueAt(jumpedPieces[i].getCoordinates()[0], jumpedPieces[i].getCoordinates()[1], null);
                }
            }
        }
        
        // and, move this piece (WE PRESUME that it's this piece) from its old spot (both on board and with the piece itself)
        this.setValueAt(moveStartingPos[0], moveStartingPos[1], null);
        piece.moveTo(moveEndingPos[0], moveEndingPos[1]);
        
        // do a favor to the piece and check if it should now be a king (it'll change itself)
        piece.checkIfShouldBeKing(this);
        
        // finally, set the move's destination to the piece we're moving
        this.setValueAt(moveEndingPos[0], moveEndingPos[1], piece);
    }
     
    /**
     * Sets the space at these coordinates to the given Piece object.
     * @param x The x position of the Piece
     * @param y The y position of the Piece
     * @param piece The Piece to put in this space, but can be null to make the space empty
     */
    private void setValueAt(int x, int y, Piece piece)
    {
        this.boardArray[y][x] = piece;
    }
    
    /**
     * Sets the space at this number position to the given Piece object.
     * @param position The number position, zero indexed at top left.
     * @param piece The Piece to put in this space, but can be null to make the space empty
     */
    private void setValueAt(int position, Piece piece)
    {
        int[] coords = getCoordinatesFromPosition(position); // convert position to coordinates and use that
        this.setValueAt(coords[0], coords[1], piece);
    }
    
    /**
     * Get's the Piece object at this location. (doesn't error check)
     * @param x The x position of the Piece
     * @param y The y position of the Piece
     * @return The Piece here. (May be null)
     */
    public Piece getValueAt(int x, int y)
    {
        return this.boardArray[y][x];
    }
    
    /**
     * Get's the Piece object at this location, but using a single number,
     * which progresses from 0 at the top left to the square of the size at the bottom right
     * @param position This number, zero indexed at top left
     * @return The Piece here. (may be null).
     */
    public Piece getValueAt(int position)
    {
        int[] coords = getCoordinatesFromPosition(position); // convert position to coordinates and use that
        return this.getValueAt(coords[0], coords[1]); 
    }
    
    /**
     * Converts a single position value to x and y coordinates.
     * @param position The single position value, zero indexed at top left.
     * @return A two part int array where [0] is the x coordinate and [1] is the y.
     */
    public int[] getCoordinatesFromPosition(int position)
    {
        int[] coords = new int[2];
        
        // get and use x and y by finding low and high frequency categories
        coords[0] = position % this.size; // x is low frequency
        coords[1] = position / this.size; // y is high frequency
        return coords;
    }
    
    /**
     * Converts from x and y coordinates to a single position value,
     * which progresses from 0 at the top left to the square of the size minus one at the bottom right
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The single position value.
     */
    public int getPositionFromCoordinates(int x, int y)
    {
        // sum all row for y, and add low frequency x
        return this.size*y + x;
    }
    
    /**
     * @return Returns true if the given position on the board represents a "BLACK" square on the checkboard.
     * (The checkerboard in this case starts with a "white" space in the upper left hand corner
     * @param x The x location of the space
     * @param y The y location of the space
     */
    public boolean isCheckerboardSpace(int x, int y)
    {
        // this is a checkerboard space if x is even in an even row or x is odd in an odd row
        return x % 2 == y % 2;
    }
    
    /**
     * @return Returns true if the given coordinates are over the edge the board
     * @param x The x coordinate of the position
     * @param y The y coordinate of the position
     */
    public boolean isOverEdge(int x, int y)
    {
        return (x < 0 || x >= this.size ||
                y < 0 || y >= this.size);
    }
    
    /**
     * @return Returns true if the given position is over the edge the board
     * @param position The given 0-indexed position value
     */
    public boolean isOverEdge(int position)
    {
         int[] coords = getCoordinatesFromPosition(position); // convert position to coordinates and use that
        return this.isOverEdge(coords[0], coords[1]); 
    }
    
    /**
     * Flips the board coordinates so that the other pieces are on top, etc.
     * @return Returns a new board flipped (doesn't modify this one)
     * @deprecated // this method doesn't seem to work, and there are easier ways to do this
     */
    public Board getFlippedBoard()
    {
        // copy this Board, as the basis for a new, flipped one
        Board newBoard = new Board(this);

        // switch every piece to the one in the opposite corner
        for (int y = 0; y < newBoard.size; y++)
        {
            for (int x = 0; x < newBoard.size; x++)
            {
                // get piece in opposite corner...
                Piece oldPiece = this.getValueAt(this.size - 1 - x, this.size - 1 - y);
                
                if (oldPiece != null)
                {
                    // ...and transfer color and position to a new generated piece if it exists
                    newBoard.setValueAt(x, y, new Piece(x, y, oldPiece.isWhite));
                }
                else 
                {
                    // otherwise just add an empty space
                    newBoard.setValueAt(x, y, null);
                }
            }
        }
        
        return newBoard;
    }
}
