package org.example;

import org.example.Game;
import org.example.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.mockito.Mockito.*;

public class DiceGameTest {
    @Mock
    private Game mockGame;

    private Game game;

    private Game testGameForCount;
    private ByteArrayOutputStream outContent;
    private final PrintStream originalOut = System.out;

    @Before
    public void setup() {

        mockGame = new Game();
        // Create a stream to simulate the names entered by users
        String simulatedUserInputs = "Alice\nBob\nCharlie\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInputs.getBytes()));
        game = new Game();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));


        game.initializeGame();  // This will handle player creation based on the simulated inputs


        // mock game part
        mockGame = Mockito.spy(new Game()); // Assuming Game needs to be partially mocked


       // doNothing().when(mockGame).initializeGame(); // Assuming initializeGame is void and should do nothing
        when(mockGame.rollDice()).thenReturn(1, 2,3,4,5,6); // First roll 1, then 3 on reroll

        List<Player> players = new ArrayList<>();
        List<Integer> turnOrder = new ArrayList<>();

        Player player = new Player("Alice");
        player.pointColor = new String[]{"red", "green", "blue"}[0]; // Requirement 11
        players.add(player);
        turnOrder.add(0);

        Player player2 = new Player("Bob");
        player.pointColor = new String[]{"red", "green", "blue"}[1]; // Requirement 11
        players.add(player2);
        turnOrder.add(1);

        Player player3 = new Player("Charlie");
        player.pointColor = new String[]{"red", "green", "blue"}[2]; // Requirement 11
        players.add(player3);
        turnOrder.add(2);

        mockGame.players = players;
        mockGame.turnOrder= turnOrder;
        mockGame.turnCount = 0;




    }
    @After
    public void restoreStreams() {
        System.setOut(originalOut); // Restore the original System.out
    }


    //req 1 :	A dice-throwing game for three players.
    @Test
    public void testPlayerCount() { // Requirement 1
        // Create a stream to simulate the names entered by users
        String simulatedUserInputs = "Test\nAlice\nBob\nCharlie\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInputs.getBytes()));
        testGameForCount = new Game();
        testGameForCount.initializeGame();

       Assert.assertTrue(testGameForCount.players.size() == 3);
    }

    // req 2:  Every player starts with 0 points.
    @Test
    public void testPlayerInitialPoints() { // Requirement 2
        // Checks all points after initalization
        Assert.assertTrue(game.players.stream().allMatch(p -> p.points == 0));
    }


    // req 3	The players take turns.
    // req 4	In each turn, a player must throw a dice.
    // req 5	The dice count gets added to the player's points.
    @Test
    public void testPlayerTurnConditions3and4and5() { // Requirement 3-4-5
        // Checks all points after initalization
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        game.manageTurn(); //
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn
        //Assert.assertTrue(outContent.toString().contains("roll"));
        //Assert.assertTrue(outContent.toString().contains("point"));
        Assert.assertTrue(game.players.stream().anyMatch(p -> p.points > 0));

    }

    //  After each turn, the system prints the dice count and the player points.
    @Test
    public void testPlayerTurnConditions6() { // Requirement 6
        // Checks all points after initalization
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn


        when(mockGame.rollDice()).thenReturn(2, 2);  // First return 6, then return 2
        mockGame.manageTurn(); //


        //Assert.assertTrue(outContent.toString().contains("roll"));
        int currentPoint = mockGame.players.get(0).points;
        Assert.assertTrue(outContent.toString().contains("2") && outContent.toString().contains(String.valueOf(currentPoint)));
        //Assert.assertTrue(outContent.toString().contains("point") || outContent.toString().contains("Point"));

        //Assert.assertTrue(game.players.stream().anyMatch(p -> p.points > 0));
    }

    //req7	The game ends after 12 turns.
    @Test
    public void testGameEndConditionsReq7() { // Requirements 7

        game.turnCount = 12;
        game.players.get(1).points = 11;
        game.players.get(0).points = 12;
        game.players.get(2).points = 13;
        Assert.assertTrue(game.checkGameEndConditions()); // Check if game ends after 12 turns

    }

    //req8	The game also terminates if one player has 10 more points than every other player.
    // checks with the 100 points difference to check smelly version too.  smelly version--> 8: The game also terminates if one player has significantly more points than the others.
    @Test
    public void testGameEndConditionsReq8() { // Requirements 8

        game.turnCount = 2;
        game.players.get(1).points = 20;
        game.players.get(0).points = 120;
        game.players.get(2).points = 15;
        Assert.assertTrue(game.checkGameEndConditions()); // Check if game ends after point diff

    }


    //req 9	If and only if the dice count is 1, the player must decide if he wants to re-roll the dice instead.
    //smelly version -> 9: If and only if the dice count is 1, it must be decided if the player re-rolls the dice instead.

    @Test
    public void testRerollOption9() { // Requirement 9


        System.setIn(new ByteArrayInputStream("no\nno\n".getBytes())); // Player accept turn

        mockGame.manageTurn();  // Simulate the turn where the player can decide to reroll
        Assert.assertTrue(outContent.toString().contains("reroll") || outContent.toString().contains("re-roll") );
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        //mock rolled 2
        mockGame.manageTurn();
        Assert.assertFalse(outContent.toString().contains("reroll") || outContent.toString().contains("re-roll") );
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        //mock rolled 3
        mockGame.manageTurn();
        Assert.assertFalse(outContent.toString().contains("reroll") || outContent.toString().contains("re-roll") );
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        //mock rolled 4
        mockGame.manageTurn();
        Assert.assertFalse(outContent.toString().contains("reroll") || outContent.toString().contains("re-roll") );
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        //mock rolled 5
        mockGame.manageTurn();
        Assert.assertFalse(outContent.toString().contains("reroll") || outContent.toString().contains("re-roll") );
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        //mock rolled 6
        mockGame.manageTurn();
        Assert.assertFalse(outContent.toString().contains("reroll") || outContent.toString().contains("re-roll") );
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));


    }

    //req10: 10	The winner will receive the message: "Congratulations " + player name + " you won the game! You are the best!".
    //smelly version ->  There will be some sort of congratulations for the winner.
    @Test
    public void testWinnerCongrats10() { // Requirements 10
        // Simulating game turns to reach turn limit
        game.turnCount = 2;
        game.players.get(1).points = 70;
        game.players.get(0).points = 40;
        game.players.get(2).points = 15;


        Assert.assertTrue(game.checkGameEndConditions()); // Check game end by points condition
        game.declareWinner();
        String output = outContent.toString();

        Assert.assertTrue("Output should contain 'congrats'", output.contains("ongrat"));
        //Assert.assertTrue("Output should contain 'Congratulations'", output.contains("Congratulations"));
    }

    //  req 11 : 	In the beginning, the player point colors shall be red, green, and blue for player 1, player 2, and player 3 respectively.
    //smelly version -> 11: The player point colors shall always be red, green, and blue for player 1, player 2, and player 3 respectively.
    @Test
    public void testInitialColours11() { // Requirements 11


        //
        Set<String> colors = new HashSet<>();
        for (Player player : game.players) {
            colors.add(player.pointColor.toLowerCase());
        }

        //boolean hasAllColors = colors.contains("red") && colors.contains("green") && colors.contains("blue");


        //Assert.assertTrue(hasAllColors);

        //Ground truth
       Assert.assertTrue((game.players.get(game.turnOrder.get(0)).pointColor.equalsIgnoreCase("red") && game.players.get(game.turnOrder.get(0)).pointColor.equalsIgnoreCase("green")  && game.players.get(game.turnOrder.get(0)).pointColor.equalsIgnoreCase("blue")) || (game.players.get(0).pointColor.equalsIgnoreCase("red") && game.players.get(1).pointColor.equalsIgnoreCase("green")  && game.players.get(2).pointColor.equalsIgnoreCase("blue")));
    }

    //req12	If a player's points get above 11, his point color shall turn purple.
    //smelly version ->12: If a player's points are sufficient to win, his point color shall turn purple.
    @Test
    public void testAutomaticColorChange12() { // Requirements 12
        Player player = game.players.get(0);
        player.points=120;
        player.updateColor(); // Should trigger color change to purple
        Assert.assertEquals("purple", player.pointColor);

    }

    // 13	If the current player throws a 6, the next player's turn is skipped.
    //smelly version ->13: If the current player throws a 6, his turn must not continue.
    @Test
    public void testSkipDiceSixRolledOption13() { // Requirement 13



        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        when(mockGame.rollDice()).thenReturn(6, 2);  // First return 6, then return 2

        mockGame.manageTurn();  // Simulate the turn where the player can decide to reroll
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        mockGame.manageTurn();  // Simulate the turn where the player can decide to reroll


        //Assert.assertTrue(outContent.toString().contains("skip")||outContent.toString().contains("Skip"));
        //Second player turn must be skipped so should be 0
        Assert.assertFalse(mockGame.players.get(1).points> 0);

    }


    //req14	If the dice count is 2, it shall be tripled. If the dice count is even but not 2, it shall be halved.
    //smelly version -> 14: If the dice count is 2, it shall be tripled. If the dice count is even, it shall be halved.
    @Test
    public void testDiceHalved14() { // Requirement 14
        Player player = game.players.get(0);
        player.updatePoints(2);
        Assert.assertEquals(6, player.points);

        player.updatePoints(4);
        Assert.assertEquals(8, player.points);

    }

    //req 15	If the points turn equal at any point in the game, the game shall print: "Equality!".
    //smelly version ->15: The game shall check the players' points at any point in the game. If they turn equal, it shall print: "Equality!".
    @Test
    public void testEqualPointsMessage15() { // Requirement 15
        game.players.forEach(p -> p.points = 10); // Setting all players to equal points
        game.displayEquality(); // Check for equality message
        Assert.assertTrue(outContent.toString().contains("Equality!"));
    }

    //req16	The program shall decide the player's turn order randomly at the beginning of the game.
    //smelly version -> 16: The players' turn order must be decided at the beginning of the game.
    @Test
    public void testInitializeTurnOrder16() { // Requirement 16
        Assert.assertEquals(3, game.players.size()); // Ensures three players are initialized
        Assert.assertTrue(game.turnOrder.size() > 0); // Ensures turn order is established
    }

    //req 17	The players shall enter their names. If they enter "Computer", the system shall print "Name cannot be Computer" and ask for the name again.
    //smelly version -> 17: Names shall be entered for each player. If "Computer" is entered, the system shall print "Name cannot be Computer" and ask for the name again.
    @Test
    public void testPlayerNameRejection17() { // Requirement 17
        Scanner scanner = new Scanner(new ByteArrayInputStream("Computer\nAlice\nBob\nCharlie\n".getBytes()));
        System.setIn(new ByteArrayInputStream("Computer\nAlice\nBob\nCharlie\n".getBytes()));
        game.initializeGame();
        Assert.assertFalse(game.players.stream().anyMatch(p -> p.name.equals("Computer")));
        Assert.assertTrue(outContent.toString().contains("Name cannot be Computer"));
    }

    //req 18 If the dice count is 5, throw again and multiply both dice counts.
    //smelly version -> 18: If the dice count is high, throw again and multiply both dice counts.
    @Test
    public void testDiceMultiply18() { // Requirement 18


        when(mockGame.rollDice()).thenReturn(5, 6);
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn

        mockGame.manageTurn();  // Simulate the turn where the player can decide to reroll

        Assert.assertTrue(game.players.stream().anyMatch(p -> p.points == 30));

    }


    //req 19	When a player's points get above 5 points, his point color shall turn yellow.
    //19  When a player's points get above 5 points, his point color shall turn yellow.
    @Test
    public void testAutomaticColorChange19() { // Requirements 19
        Player player = game.players.get(0);
        player.points= 6; // Should trigger color change to yellow
        player.updateColor();
        Assert.assertEquals("yellow", player.pointColor);
    }

    //req 20	In each turn, the player must decide if he wants to skip his turn.
    //smelly version -> 20: In each turn, the player could skip his turn.	.
    @Test
    public void testSkipTurnOption20() { // Requirement 20
        // Simulate player decision to skip turn
        System.setIn(new ByteArrayInputStream("no\n".getBytes())); // Player accept turn
        game.manageTurn(); // Player decides to skip their turn
        Assert.assertTrue(outContent.toString().contains("Skip") || outContent.toString().contains("skip"));
        //Assert.assertTrue(outContent.toString().contains("Current player:"));
    }



    // req21	If a player throws the same value three times in a row, his points are reset to 0.
    // 21: If a player throws the same value multiple times in a row, his points are reset to 0.
    @Test
    public void testPointReset21() { // Requirement 21
        Player player = game.players.get(0);
        player.updatePoints(3);
        player.updatePoints(3);
        int twoTimesInRow=player.points;
        //
        player.updatePoints(3); // Should trigger point reset
        int threeTimesInRow=player.points;
        player.updatePoints(3); // Should trigger point reset
        int fourTimesInRow=player.points;
        player.updatePoints(3); // Should trigger point reset
        int fiveTimesInRow=player.points;

        Assert.assertTrue(( twoTimesInRow == 0) || ( threeTimesInRow == 0) || ( fourTimesInRow == 0) || ( fiveTimesInRow == 0) );
    }


    //req 22	If two players have the same amount of points, one player must gain more points than the others before the game can end.
    //smelly version -> 22: If two players have the same amount of points, the game cannot end until one player has more points than the other.
    @Test
    public void testGameEndConditionsReq22() { // Requirements 7, 8, 22
        // Simulating game turns to reach turn limit
        game.turnCount = 12;
        game.players.get(1).points = 40;
        game.players.get(0).points = 40;
        game.players.get(2).points = 15;
        Assert.assertFalse(game.checkGameEndConditions()); // Check if game ends after 12 turns

    }

    //req 23	After a game has ended, the system shall print the players' points and the winner.
    //smelly version ->23: If needed, the system can print the players' points and the winner after a game has ended.
    @Test
    public void testGameEndPrintConditionsReq23() { // Requirements 7, 8, 22
        // Simulating game turns to reach turn limit
        game.turnCount = 12;
        game.players.get(1).points = 33;
        game.players.get(0).points = 32;
        game.players.get(2).points = 31;

        //game.players.get(1).points = 20; // Set high points to potentially end game by points condition
        Assert.assertTrue(game.checkGameEndConditions()); // Check game end by points condition
        game.declareWinner();

        //checks winner is announced
        Assert.assertTrue("Output should contain 'congrats'", outContent.toString().contains("ongrat"));


        //checks players points are announced
        Assert.assertTrue(outContent.toString().contains(game.players.get(0).name));
        Assert.assertTrue(outContent.toString().contains(game.players.get(1).name));
        Assert.assertTrue(outContent.toString().contains(game.players.get(2).name));
    }

    //req24	The player with the most points after the game has ended will be the winner.
    //smelly version -> 24: After the game has ended, one player shall be the winner.
    @Test
    public void testGameEndConditionsMostPointReq24() { // Requirements 7, 8, 22
        // Simulating game turns to reach turn limit
        game.turnCount = 12;
        game.players.get(1).points = 33;
        game.players.get(0).points = 32;
        game.players.get(2).points = 31;

        //game.players.get(1).points = 20; // Set high points to potentially end game by points condition
        Assert.assertTrue(game.checkGameEndConditions()); // Check game end by points condition
        game.declareWinner();
        Assert.assertTrue(outContent.toString().contains(game.players.get(1).name));

    }

    //req 25	If the players decide to play again, instead of choosing the player order randomly, the players shall be ordered by their points in the last game ascending.
    //smelly version ->25: If the players decide to play again, instead of choosing the player order randomly, the players shall be ordered in a certain way.
    @Test
    public void testNewGamePreparation25() { // Requirements 7, 8, 22
        // Simulating game turns to reach turn limit
        game.turnCount = 12;
        game.players.get(1).points = 33;
        game.players.get(0).points = 32;
        game.players.get(2).points = 31;

        //game.players.get(1).points = 20; // Set high points to potentially end game by points condition
        Assert.assertTrue(game.checkGameEndConditions()); // Check game end by points condition
        game.declareWinner();

        //game.prepareForNextGame();
        Assert.assertTrue(game.turnOrder.get(0) == 1 &&     game.turnOrder.get(1) == 0 &&  game.turnOrder.get(2) == 2);
    }




}
