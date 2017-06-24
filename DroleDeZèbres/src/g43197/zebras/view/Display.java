package g43197.zebras.view;

import g43197.zebras.model.*;
import static g43197.zebras.view.Read.messages;

/**
 * Class with all methods used for the display. toString methods with basic
 * display aren't included and shouldn't be used anymore.
 *
 * @author Philippe
 */
public class Display {

    private static final String SEPARATOR = "\n";
    private static final String COLOR_DEFAULT = "\033[0m";
    private static final String BKG_DEFAULT = "\033[049m";
    private static final String BOLD_ON = "\033[2m";
    private static final String BOLD_OFF = "\033[22m";

    /**
     * Changes the color of a string. Only color of this enum are accepted.
     *
     * @param msg specified string as msg
     * @param color specified color
     * @return the message in the specified Color.
     */
    public static String toColor(String msg, Color color) {
        switch (color) {
            case RED:
                return "\033[31m" + msg + COLOR_DEFAULT;
            case GREEN:
                return "\033[32m" + msg + COLOR_DEFAULT;
            default:
                return msg;
        }
    }

    /**
     * Changes the message to a bold message.
     *
     * @param msg
     * @return
     */
    public static String toBold(String msg) {
        return BOLD_ON + msg + BOLD_OFF;
    }

    /**
     * Display the intro and a link to the rules if he wants it.
     */
    public static void displayIntro() {
        System.out.println(messages.getString("welcome"));
        System.out.println(messages.getString("rules@"));
        System.out.println(messages.getString("rulesLink"));
    }

    /**
     * Display who's turn it is in the game.
     *
     * @param currentColor of the player in game
     */
    public static void displayCurrentPlayer(Color currentColor) {
        String msg = messages.getString("playersTurn");
        switch (currentColor) {
            case RED:
                msg += messages.getString("red");
                break;
            case GREEN:
                msg += messages.getString("green");
                break;
            default:
                throw new GameException("Whaaaat?!");
        }
        System.out.println(msg);
    }

    /**
     * Display the stock of the current player and the bord.
     *
     * @param game current game
     */
    public static void displayGame(Game game) {
        displayStock(game);
        displayBord(game.getReserve(), game.getImpalaJones());
    }

    /**
     * Nice display of the stock of the current player.
     *
     * @param game current game
     */
    public static void displayStock(Game game) {
        String stock = "";
        stock += "   " + messages.getString("stock") + SEPARATOR;
        stock += "   ";
        for (int i = 0; i < messages.getString("stock").length(); i++) {
            stock += "-";
        }
        stock += SEPARATOR;

        int nbAnimals;
        for (Species species : Species.values()) {
            nbAnimals = game.getNb(species);
            if (nbAnimals <= 1) {
                stock += messages.getString(species.name().toLowerCase());
            } else {
                stock += messages.getString((species.name() + "s").toLowerCase());
            }
            stock += SEPARATOR;
        }

        System.out.println(stock);
    }

    /**
     * Nice display of the bord.
     *
     * @param impala game's impala
     * @param reserve game's reserve
     */
    public static void displayBord(Reserve reserve, ImpalaJones impala) {
        String[] bord = makeBord(reserve, impala);
        String msg = "";
        for (String line : bord) {
            msg += line + SEPARATOR;
        }
        System.out.println(msg);
    }

    private static String[] makeBord(Reserve reserve, ImpalaJones impala) {
        String[] bord = new String[3 + Reserve.MAX_ROWS * 2 - 1 + 2];
        displayUp(bord, impala);
        displayLines(bord, impala, reserve);
        displayDown(bord, impala);
        return bord;
    }

    private static void displayUp(String[] bord, ImpalaJones impala) {
        bord[0] = "      ";
        for (int i = 1; i <= Reserve.MAX_COLUMNS; i++) {
            bord[0] += i + " ";
        }
        bord[1] = displaySidesUD(impala, "up");
        bord[2] = "      ";
        for (int i = 1; i <= Reserve.MAX_COLUMNS; i++) {
            bord[2] += "==";
        }
    }

    private static void displayDown(String[] bord, ImpalaJones impala) {
        bord[bord.length - 2] = "      ";
        for (int i = 1; i <= Reserve.MAX_COLUMNS; i++) {
            bord[bord.length - 2] += "==";
        }
        bord[bord.length - 1] = displaySidesUD(impala, "down");
    }

    private static String displaySidesUD(ImpalaJones impala, String side) {
        String msg = "      ";
        boolean doUp = impala.isUp() && side.equals("up");
        boolean doDown = impala.isDown() && side.equals("down");
        if (doUp || doDown) {
            for (int i = 0; i < Reserve.MAX_COLUMNS; i++) {
                if (i == impala.getColumn()) {
                    msg += "I ";
                } else {
                    msg += ". ";
                }
            }
        } else {
            for (int i = 0; i < Reserve.MAX_COLUMNS; i++) {
                msg += ". ";
            }
        }
        return msg;
    }

    private static void displayLines(String[] bord, ImpalaJones impala, Reserve reserve) {
        int row;
        for (int i = 3; i < bord.length - 2; i++) {
            row = (i - 3) / 2;
            if (i % 2 == 1) {
                normalLine(bord, i, row, impala, reserve);
            } else {
                betweenLines(bord, i, row, reserve);
            }
        }
    }

    private static String riverToString() {
        return "\033[46m" + " " + BKG_DEFAULT;
    }

    private static void normalLine(String[] bord, int i, int row, ImpalaJones impala, Reserve reserve) {
        Coordinates pos;
        //left side
        bord[i] = "";
        bord[i] += row + 1 + " ";
        bord[i] += displaySidesLR(impala, i, "left");
        bord[i] += "| ";
        //center animals
        for (int j = 0; j < Reserve.MAX_COLUMNS; j++) {
            pos = new Coordinates(row, j);
            bord[i] += tileToString(reserve, pos);
        }
        //right side
        bord[i] += "| ";
        bord[i] += displaySidesLR(impala, row, "right");
    }

    private static String displaySidesLR(ImpalaJones impala, int row, String side) {
        String msg = ". ";
        if (row == impala.getRow()) {
            boolean doLeft = impala.isLeft() && side.equals("left");
            boolean doRight = impala.isRight() && side.equals("right");
            if (doLeft || doRight) {
                msg = "I ";
            }
        }
        return msg;
    }

    private static String tileToString(Reserve reserve, Coordinates position) {
        String msg = ".";
        Animal animal = reserve.getAnimal(position);
        if (animal != null) {
            String letter = animal.getSpecies().name().charAt(0) + "";
            if (animal.getState() == AnimalState.HIDDEN) {
                msg = toColor("X", animal.getColor());
            } else {
                msg = toColor(letter, animal.getColor());
                msg = toBold(msg);
            }
        }

        Coordinates nextPos = new Coordinates(position.getRow(), position.getColumn() + 1);
        if (nextPos.getColumn() < Reserve.MAX_COLUMNS && !sameSector(reserve, position, nextPos)) {
            msg += riverToString();
        } else {
            msg += " ";
        }
        return msg;
    }

    private static void betweenLines(String[] bord, int i, int row, Reserve reserve) {
        Coordinates pos = new Coordinates(row, 0);
        Coordinates underPos = new Coordinates(row + 1, 0), rightPos, underRightPos;
        bord[i] = "    |";
        //left side first column
        if (!sameSector(reserve, pos, underPos)) {
            bord[i] += riverToString();
        } else {
            bord[i] += " ";
        }

        for (int col = 0; col < Reserve.MAX_COLUMNS; col++) {
            //col and right tile of col
            pos = new Coordinates(row, col);
            underPos = new Coordinates(row + 1, col);
            rightPos = new Coordinates(row, col + 1);
            underRightPos = new Coordinates(row + 1, col + 1);
            if (!sameSector(reserve, pos, underPos)) {
                bord[i] += riverToString();
                bord[i] += riverToString();
            } else if (rightPos.getColumn() < Reserve.MAX_COLUMNS
                    && (!sameSector(reserve, pos, rightPos) || !sameSector(reserve, pos, underRightPos))) {
                bord[i] += " " + riverToString();
            } else {
                bord[i] += "  ";
            }
        }
        bord[i] += "|";
    }

    private static boolean sameSector(Reserve reserve, Coordinates pos, Coordinates other) {
        return reserve.getSector(pos) == reserve.getSector(other);
    }

    /**
     * Displays the score of the current player of the game.
     *
     * @param game
     */
    public static void displayScore(Game game) {
        String msg = messages.getString("actualScoreIs");
        msg += game.getScore(game.getCurrentColor());
        System.out.println(msg);
    }

    public static void displayWinner(Game game) {
        int redScore, greenScore;
        String msg = messages.getString("gameOver");
        msg += SEPARATOR + SEPARATOR;
        msg += messages.getString("redScore");
        redScore = game.getScore(Color.RED);
        msg += redScore;
        msg += messages.getString("greenScore");
        greenScore = game.getScore(Color.GREEN);
        msg += greenScore;
        msg += SEPARATOR + SEPARATOR + SEPARATOR;
        if (redScore == greenScore) {
            msg += messages.getString("tieGame");
        } else {
            msg += messages.getString("");
            if (redScore > greenScore) {
                msg += messages.getString("red");
            } else {
                msg += messages.getString("green");
            }
            msg += SEPARATOR;
            msg += messages.getString("congrats");
        }
        msg += SEPARATOR;
        System.out.println(msg);
    }
}
