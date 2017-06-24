package g43197.zebras;

import g43197.zebras.model.*;
import g43197.zebras.view.*;

/**
 * Class to play the game and do basic tests with model methods.
 *
 * @author Philippe
 */
public class Zebras {

    public static void play() {

        Game game = new Game();
        Read.askLanguage();
        
        
        startGame(game);

        while (!game.isOver()) {
            Display.displayCurrentPlayer(game.getCurrentColor());
            Display.displayScore(game);
            Display.displayGame(game);

            if (game.getStatus() == GameStatus.ANIMAL) {
                putAnimal(game);
            } else if (game.getStatus() == GameStatus.IMPALA) {
                moveImpala(game);
            }

            System.out.println("");
        }
        Display.displayWinner(game);
    }

    /*
    * Creates a new game, displays introduction text and rules if needed
    * and initialize Impala's position
     */
    private static void startGame(Game game) {
        Display.displayIntro();
        int initImp = Read.readImpalaFirstPosition();
        try {
            game.setImpalaJonesFirstPosition(initImp);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }

    /*
    * Puts an animal in if good status and can play.
     */
    private static void putAnimal(Game game) {
        boolean error;
        Species species;
        Coordinates pos;
        do {
            try {
                error = false;
                species = Read.readSpecies();

                pos = Read.readAnimalPosition(game.getImpalaJones());

                game.putAnimal(pos, species);
            } catch (GameException e) {
                System.out.println(e.getMessage());
                error = true;
            }
        } while (error);
    }

    /*
    * Moves Impala if good status.
     */
    private static void moveImpala(Game game) {
        boolean error;
        int distance;
        do {
            try {
                error = false;
                distance = Read.readImpalaDistance();
                game.moveImpalaJones(distance);
            } catch (GameException e) {
                System.out.println(e.getMessage());
                error = true;
            }
        } while (error);
    }

    public static void main(String[] args) {
        play();
    }
}
