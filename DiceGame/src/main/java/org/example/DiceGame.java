package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// Player class handles player details and game logic concerning players
class Player {
    String name;
    int points = 0;
    String pointColor;
    int[] consecutiveRolls = new int[3];
    int consecutiveIndex = 0;

    Player(String name) {
        this.name = name;
    }

    void updatePoints(int diceValue) {
        // Handling consecutive rolls for resetting points
        consecutiveRolls[consecutiveIndex % 3] = diceValue;
        consecutiveIndex++;
        if (consecutiveIndex >= 3 && consecutiveRolls[0] == consecutiveRolls[1] && consecutiveRolls[1] == consecutiveRolls[2]) {
            points = 0;
            System.out.println(name + " has thrown the same dice value three times in a row. Points reset to 0.");
            updateColor();
            consecutiveIndex = 0; // Reset the consecutive roll tracking
        } else {
            if (diceValue == 2) {
                diceValue *= 3;
            } else if (diceValue % 2 == 0 && diceValue != 2) {
                diceValue /= 2;
            }
            points += diceValue;
            System.out.println(name + " rolls " + diceValue + " and now has " + points + " points.");
            updateColor();
        }
    }

    void updateColor() {
        if (points > 11) {
            pointColor = "purple";
        } else if (points > 5) {
            pointColor = "yellow";
        }
    }

    void resetPoints() {
        points = 0;
    }
}

// Game class handles the game logic
class Game {
    List<Player> players = new ArrayList<>();
    List<Integer> turnOrder = new ArrayList<>();
    int turnCount = 0;
    boolean gameOver = false;

    void initializeGame() {
        Scanner scanner = new Scanner(System.in);
        for (int i = 1; i <= 3; i++) {
            System.out.print("Enter name for player " + i + " (Cannot be 'Computer'): ");
            String name = scanner.nextLine();
            while (name.equalsIgnoreCase("Computer")) {
                System.out.println("Name cannot be Computer");
                name = scanner.nextLine();
            }
            Player player = new Player(name);
            players.add(player);
        }
        Collections.shuffle(players); // Randomizing player order
        for (int i = 0; i < players.size(); i++) {
            turnOrder.add(i);
            // Assigning initial colors
            if (i == 0) players.get(i).pointColor = "red";
            else if (i == 1) players.get(i).pointColor = "green";
            else if (i == 2) players.get(i).pointColor = "blue";
        }
    }

    void manageTurn() {
        Player current = players.get(turnOrder.get(turnCount % players.size()));
        System.out.println(current.name + "'s turn with color " + current.pointColor);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Would you like to skip? ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("yes")) {
            System.out.println(current.name + " skips the turn.");
        } else {
            int diceValue = rollDice();
            if (diceValue == 1) {
                System.out.print("Would you like to re-roll? ");
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("yes")) {
                    diceValue = rollDice();
                }
            }
            if (diceValue == 5) {
                int secondRoll = rollDice();
                diceValue *= secondRoll;
                System.out.println("Rolled a 5, second roll is " + secondRoll + ", total for this turn: " + diceValue);
            }
            current.updatePoints(diceValue);
            // Check if the next player's turn is skipped due to a roll of 6
            if (diceValue == 6) {
                turnCount++; // Skip next player
                System.out.println("Next player's turn is skipped.");
            }
        }
        turnCount++;
        checkGameEndConditions();
    }

    int rollDice() {
        return (int) (Math.random() * 6) + 1;
    }

    boolean checkGameEndConditions() {
        if (turnCount >= 12) {
            gameOver = true;
            System.out.println("Game ended after 12 turns.");
            return true;
        }
        for (Player player : players) {
            if (player.points >= 10) {
                boolean allOtherLower = true;
                for (Player other : players) {
                    if (other != player && player.points <= other.points + 10) {
                        allOtherLower = false;
                        break;
                    }
                }
                if (allOtherLower) {
                    gameOver = true;
                    System.out.println("Game ended because " + player.name + " has 10 more points than every other player.");
                    return true;
                }
            }
        }
        return false;
    }

    void declareWinner() {
        Player winner = Collections.max(players, (p1, p2) -> p1.points - p2.points);
        System.out.println("Congratulations " + winner.name + " you won the game! You are the best!");
        for (Player p : players) {
            System.out.println(p.name + ": " + p.points + " points");
        }
    }

    void displayEquality() {
        int firstPlayerPoints = players.get(0).points;
        boolean isEqual = true;
        for (Player p : players) {
            if (p.points != firstPlayerPoints) {
                isEqual = false;
                break;
            }
        }
        if (isEqual) {
            System.out.println("Equality!");
        }
    }

    void prepareForNextGame() {
        Collections.sort(players, (p1, p2) -> p1.points - p2.points);
        turnOrder.clear();
        for (int i = 0; i < players.size(); i++) {
            turnOrder.add(i);
        }
    }
}

// DiceGame class to run the game
public class DiceGame {
    public static void main(String[] args) {
        Game game = new Game();
        game.initializeGame();
        while (!game.gameOver) {
            game.manageTurn();
        }
        game.declareWinner();
        game.displayEquality();
        // Optionally, prepare for the next game if players decide to continue
        System.out.print("Do you want to play another round? ");
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            game.prepareForNextGame();
            game.gameOver = false;
            while (!game.gameOver) {
                game.manageTurn();
            }
            game.declareWinner();
            game.displayEquality();
        }
    }
}
