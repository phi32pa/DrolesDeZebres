package g43197.zebras.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that links the methods and the main class. Makes the game rules be
 * followed.
 *
 * @author Philippe
 */
public class Game implements Model {

    private final List<Player> players;
    private final Reserve reserve;
    private final ImpalaJones impala;
    private final Pieces pieces;
    private GameStatus status;
    private Color currentColor;
    private Player wonInauguration;

    /**
     * Constructor of game. Adds a new player of every possible color.
     * Initialize game status to init.
     */
    public Game() {
        players = new ArrayList<>();
        for (Color color : Color.values()) {
            Player p1 = new Player(color);
            players.add(p1);
        }
        this.reserve = new Reserve();
        this.impala = new ImpalaJones();
        this.pieces = new Pieces();
        this.status = GameStatus.INIT;
        this.currentColor = players.get(0).getColor();
        this.wonInauguration = null;
    }

    /**
     * Start a match and reset attributes.
     */
    @Override
    public void start() {
        Animal a;
        Coordinates pos;
        for (int row = 0; row < Reserve.MAX_ROWS; row++) {
            for (int col = 0; col < Reserve.MAX_COLUMNS; col++) {
                pos = new Coordinates(row, col);
                a = reserve.getAnimal(pos);
                reserve.remove(row, col);
                if (a != null) {
                    pieces.putAnimal(a);
                }
            }
        }
        impala.init(0);
        this.status = GameStatus.INIT;
    }

    /**
     * Set Impala Jones first position. Changes status to Animal.
     *
     * @param position of Impala Jones at the beginning of game
     * @throws GameException if game's status isn't GameStatus.INIT.
     */
    @Override
    public void setImpalaJonesFirstPosition(int position) {
        if (status != GameStatus.INIT) {
            throw new GameException(
                    "Ce n'est pas le moment de placer Impala!");
        }

        this.impala.init(position);
        this.status = GameStatus.ANIMAL;
    }

    /**
     * Put an animal in the Board. Put an animal of the given species for the
     * current player. Changes status to impala.
     *
     * @param position position on the board
     * @param species species of an animal
     * @throws GameException if
     * <ul>
     * <li>game's status isn't Status.ANIMAL</li>
     * <li>or Impala Jones isn't on the the same row or column</li>
     * <li>or that position is not free</li>
     * <li>or the current player doesn't have that tile of that species to play
     * anymore</li>
     * </ul>
     */
    @Override
    public void putAnimal(Coordinates position, Species species) {
        if (status != GameStatus.ANIMAL) {
            throw new GameException(
                    "Ce n'est pas le moment de placer un animal!");
        }
        if (getNb(species) == 0) {
            throw new GameException(
                    "Vous n'avez plus d'animal de cette espèce!");
        }
        if (!impala.valid(position)) {
            throw new GameException(
                    "Cette position n'est pas sur la même rangée/colone "
                    + "que Impala!");
        }
        if (!reserve.isFree(position)) {
            throw new GameException(
                    "Cette position n'est pas libre!");
        }
        Animal animal = pieces.getAnimal(currentColor, species);
        reserve.put(animal, position.getRow(), position.getColumn());

        consequencePutAnimal(animal, position);
        inaugurationSector(position);
        this.status = GameStatus.IMPALA;
    }

    private void consequencePutAnimal(Animal animal, Coordinates position) {
        List<Coordinates> adjacents = reserve.getAdjacents(position);
        Animal other;
        for (Coordinates adjacent : adjacents) {
            other = reserve.getAnimal(adjacent);
            animal.action(other);
            if (other != null) {
                runningAnimals(adjacent, other);
            }
        }
    }

    private void runningAnimals(Coordinates adjacent, Animal other) {
        if (other.getState() == AnimalState.RUN) {
            reserve.remove(adjacent.getRow(), adjacent.getColumn());
            pieces.putAnimal(other);
            other.setState(AnimalState.REST);
        }
    }

    /*The position in param is the position where an animal has just been put.*/
    private void inaugurationSector(Coordinates position) {
        if (wonInauguration == null) {
            if (reserve.getSector(position).isFull()) {
                wonInauguration = getCurrentPlayer();
            }
        }
    }

    private Player getCurrentPlayer() {
        for (Player player : players) {
            if (player.getColor() == currentColor) {
                return player;
            }
        }
        return null;
    }

    /**
     * Move Impala Jones some steps forward. Changes status to animal and
     * changes player to nextPlayer if the next player still has pieces to put
     * in.
     *
     * @param distance number of step
     * @throws GameException if
     * <ul>
     * <li>game's status isn't Status.IMPALA</li>
     * <li>or distance is negative</li>
     * <li>or ImpalaJones will arrive on a full row or column</li>
     * <li>or the distance is too large</li>
     * </ul>
     */
    @Override
    public void moveImpalaJones(int distance) {
        if (status != GameStatus.IMPALA) {
            throw new GameException(
                    "Ce n'est pas le moment de bouger Impala!");
        }
        if (distance <= 0) {
            throw new GameException("Distance négative ou nulle!");
        }
        if (!impala.checkMove(reserve, distance)) {
            throw new GameException(
                    "Impala va arriver sur une rangée/colone pleine!");
        }
        if (impala.findFirst(reserve) <= 3) {
            if (distance > 3) {
                throw new GameException("Distance trop grande! "
                        + "Elle doit être au maximum égale à 3!");
            }
        } else if (distance != impala.findFirst(reserve)) {
            throw new GameException("La distance est mauvaise! Elle devrait "
                    + "être égale à la première position disponible!");

        }

        impala.move(distance);
        this.status = GameStatus.ANIMAL;
        nextPlayer();
    }

    private void nextPlayer() {
        int i = 0;
        //get indice of current player
        while (players.get(i).getColor() != currentColor) {
            i++;
        }
        Color nextColor;
        if ((++i) < players.size()) {
            nextColor = players.get(i).getColor();
        } else {
            nextColor = players.get(0).getColor();
        }
        currentColor = nextColor;
    }

    /**
     * Return true if the game is over.
     *
     * @return true if the game is over
     */
    @Override
    public boolean isOver() {
        return !pieces.hasAvailable();
    }

    /**
     * Return the state of the game.
     *
     * @return the state of the game
     */
    @Override
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Return the current player color.
     *
     * @return the current player color
     */
    @Override
    public Color getCurrentColor() {
        return currentColor;
    }

    /**
     * Return the list of all player.
     *
     * @return the list of all player
     */
    @Override
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Return the reserve.
     *
     * @return the reserve.
     */
    @Override
    public Reserve getReserve() {
        return reserve;
    }

    /**
     * Return the amount of animals of the specified species that the curent
     * player can put in the reserve.
     *
     * @param species of the animal searched
     * @return the amount of animals of the specified species for the current
     * player, left in the stock.
     */
    @Override
    public int getNb(Species species) {
        return pieces.getNbAnimals(currentColor, species);
    }

    /**
     * Return Impala Jones.
     *
     * @return Impala Jones
     */
    @Override
    public ImpalaJones getImpalaJones() {
        return impala;
    }

    /**
     * Get the score of the player of the given color.
     *
     * @param color the color of the player
     * @return the score of the player of the given color.
     */
    @Override
    public int getScore(Color color) {
        int score = reserve.getScore(color);
        if (wonInauguration != null && wonInauguration.getColor() == color) {
            score += 5;
        }
        return score;
    }
}
