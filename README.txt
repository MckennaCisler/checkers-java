PROJECT TITLE: Checkers
PURPOSE OF PROJECT: Build both a 2-player and 1-player (vs AI) checkers game.
VERSION or DATE: 5.30.2016
AUTHORS: Mckenna Cisler
HOW TO RUN THIS PROJECT: Compile GameRunner.java, then execute the main method of the class GameRunner.
CLASS SUMMARY:

The remaining classes can be summarized as follows:
Player (Abstract) - Responsible for outlining shared methods of the HumanPlayer and AIPlayer classes so they can be used interchangably. 

HumanPlayer - Responsible for interacting with a human player in order to determine their move and apply it to the board.
	Note: Proposal was altered here: moves are now identified by displaying possible ones to the user and having them choose
		  from that list.

AIPlayer - Responsible for using artificial intelligence to determine an effective move and apply it to the board.

Board - Stores and allows manipulation of the game board and game pieces. 

Piece - Responsible for storing data associated with a certain piece and determing properties of that piece such as available moves.

Move - Stores data associated with the move of a piece, and methods to determine further properties. 
