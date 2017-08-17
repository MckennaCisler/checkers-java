import java.util.Scanner;
/**
 * Resposible for communicating with the human player and serving as an interface with the main game engine.
 * 
 * @author Mckenna Cisler 
 * @version 12.7.2015
 */
public class HumanPlayer extends Player
{
    // global variables
    Scanner input = new Scanner(System.in);    
    boolean isWhite;
    
    /**
     * Constructor for the HumanPlayer
     * @param color The color (side) of this player, to be used to identify the user
     */
    public HumanPlayer(boolean isWhite)
    {
		this.isWhite = isWhite;
    }
    
    /**
     * Gets a move, by asking the human player what move they want to do.
     * @param board The board to apply the move to (assumed to be oriented so that this player is on the top)
     * @return Returns the board, modified according to the player's move
     */
    public Board getMove(Board board)
    {        
        // display board to help user (without possible moves)
        displayBoard(board, null);
        
        // keep asking until they select a piece with a valid move
        Move[] possibleMoves;
        while (true)
        {
            // ask user for a piece
            Piece pieceMoving = getPieceFromUser(board);
                        
            // check for quit
            if (pieceMoving == null)
                return board;
            
            // find all possible moves the player could do
            possibleMoves = pieceMoving.getAllPossibleMoves(board);
                       
            // check that there are some, and if so continue to ask for move
            if (possibleMoves == null)
                System.out.println("That piece has no possible moves! Please choose another:");
            else
            {
                // show the user possible moves and ask for one (user will enter a number)
                displayBoard(board, possibleMoves);
                Move move = getMoveFromUser(possibleMoves);
                
                // apply move to board and return it if the user entered a valid one
                // OTHERWISE, the user requested a retry, so loop again
                if (move != null)
                {
                    board.applyMoveToBoard(move, pieceMoving);
                    return board;
                }
            }
        } 
    }
    
    /**
     * Responsible for displaying the game board to the user (optionally with possible moves)
     * @param board The board to be displayed
     * @param possibleMoves An optional Array of possible moves to display while printing the board.
     * The board will display as normal if this is null.
     */
    private void displayBoard(Board board, Move[] possibleMoves)
    {
        // clear the screen for board display
        GameRunner.clearScreen();
        
        // include a hidden top row for coordinates
        for (int y = -1; y < board.size; y++)
        {   
            // include a hidden left column for coordinates
            for (int x = -1; x < board.size; x++)
            {
                // add an exception for the top row (print letter coordinates)
                if (y == -1) 
                {
                    if (x != -1) // skip hidden column
                        // print a letter, starting with capital a, for each x value
                        System.out.print("-" + (char)(x + 65) + "- ");
                    else
                        System.out.print("     "); // still fill the place we skipped
                }
                // add an exception for the left column (print number coordinates)
                else if (x == -1)
                {
                    if (y != -1) // skip hidden row
                        // print a number, starting with one, for each y value
                        System.out.print("-" + (y + 1) + "- ");
                }
                else
                {
                    // get piece here (possibly null)
                    Piece thisPiece = board.getValueAt(x, y);
                    
                    // if there are any, loop over the possible moves and see if any end at this space
                    if (possibleMoves != null)
                    {
                        // use to determine whether to continue and skip printing other things
                        boolean moveFound = false;
                        
                        for (int i = 0; i < possibleMoves.length; i++)
                        {
                            int[] move = possibleMoves[i].getEndingPosition();
                            if (move[0] == x && move[1] == y)
                            {
                                // if one here, put the list index (one-indexed) here as a char
                                System.out.print("| " + Integer.toString(i+1) + " ");
                                moveFound = true;
                            }
                        }
                        
                        // if a move is found here, skip our other possible printings
                        if (moveFound)
                            continue;
                    }
                 
                    // if the piece at this location exists, print it with a bar for cosmetics
                    if (thisPiece != null)
                        System.out.print("| " + thisPiece.getString()); // the last space is added even in single-chars to allow for king display
                    // print out dots (black places) at checkerboard spaces
                    else if (board.isCheckerboardSpace(x, y))
                        System.out.print("| . ");
                    else
                        System.out.print("|   ");
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Asks the user for a piece on the board (for them to move),
     * and ensures it is an actual piece of the correct color
     * @param board The board to check against
     * @return The Piece object to be returned (will be an actual piece)
     */
    private Piece getPieceFromUser(Board board)
    {
        // keep trying again until we get a valid peice chosen
        while (true)
        {       
            String raw;
            
            System.out.println(getColor() + ", please select a piece by its coordinates (i.e. A3):");
            try
            {    
                raw = input.nextLine().toLowerCase();
                
                // allow user to exit
                if (raw.equalsIgnoreCase("exit"))
                {
                    GameRunner.endGameNow();
                    return null;
                }
                // ensure a valid coordinate input
                else if (raw.length() < 2)
                    throw new Exception();
                    
                // Presume that the user entered the letter coordinate first, but flip them if it's the other way around
                char letterChar = raw.charAt(0);
                char numberChar = raw.charAt(1);
                if (letterChar < 97) // the letter is actually a number...
                {
                    letterChar = numberChar;
                    numberChar = raw.charAt(0);
                }   
                                
                // get coordinates by shifting the corresponding character to its numeric value (0-indexed)
                int x = letterChar - 97;
                int y = numberChar - 48 - 1;
                               
                // ensure there's no out-of-bounds entries 
                if (board.isOverEdge(x, y))
                    throw new Exception();              
                
                // now get the actual piece there
                Piece userPiece = board.getValueAt(x, y);
                
                // and see if it is valid (isn't null and is this player's color)
                if (userPiece == null)
                    System.out.println("There is no piece there!\n");
                else if (userPiece.isWhite != this.isWhite)
                    System.out.println("That's not your piece!\n");
                else
                    return userPiece;  
            }
            catch (Exception e) // catch incorrect parse or our throw exception
            {
               System.out.println("Please enter a coordinate on the board in the form '[letter][number]'.");
               continue;
            }
        }
    }
    
    /**
     * Asks the user for a number representing a move of a particular piece,
     * checking that it is an available move. (The user should be shown all moves beforehand)
     * @param possibleMoves The list of possible moves the user can request
     * @return The Move object representing the chosen move (may be null if the user chooses to get a new piece)
     */
    private Move getMoveFromUser(Move[] possibleMoves)
    {
        int moveNum;
        
        // keep trying again until we get a valid move chosen
        while (true)
        {       
            System.out.println(getColor() + ", please select a move the its number (enter 0 to go back):");
            try 
            {
                moveNum = input.nextInt();
                input.nextLine(); // compensate for java's annoying issue
                
                // allow user to quit back to another piece by entering 0
                if (moveNum == 0)
                {
                    return null;
                }
                // ensure they enter a move that we printed
                else if (moveNum > possibleMoves.length)
                    throw new Exception();                    
                                                                
                // return the move the user entered (switch to 0-indexed), once we get a valid entry
                return possibleMoves[moveNum - 1];
            }
            catch (Exception e) // catch incorrect parse or our throw exception
            {
               System.out.println("Please enter one of the numbers on the board or 0 to exit.");
               input.nextLine(); // compensate for java's annoying issue
            }
        }
    }
    
    /**
     * @return Returns a titlecase string representing this player's color
     */
    private String getColor()
    {
        return isWhite ? "White" : "Black";
    }
}
