package g43197.zebras.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventory of the game.
 *
 * @author Philippe
 */
public class Pieces {

    private final List<Animal> animals;

    /**
     * Creates new list of animals at start of game.
     */
    public Pieces() {
        animals = new ArrayList<>();

        int[] nbAnimals = new int[]{6, 5, 1, 1, 2};
        Species[] species = Species.values();

        for (Color color : Color.values()) {
            for (int s = 0; s < nbAnimals.length; s++) {
                addAnimals(nbAnimals[s], color, species[s]);
            }
        }
    }

    private void addAnimals(int nb, Color color, Species species) {
        for (int i = 0; i < nb; i++) {
            animals.add(new Animal(species, color));
        }
    }

    /**
     * Gets an animal of the inventory. Takes him out of the inventory at the
     * same time.
     *
     * @param color specified color
     * @param species specified specie
     * @return the animal
     */
    public Animal getAnimal(Color color, Species species) {
        Animal animal = new Animal(species, color);
        int pos = animals.indexOf(animal);
        animal = animals.get(pos);
        animals.remove(pos);
        return animal;
    }

    /**
     * Checks if there's any animal left to place.
     *
     * @return true if there an animal left.
     */
    public boolean hasAvailable() {
        return !animals.isEmpty();
    }

    /**
     * Checks if there is still ananimal left of the player with the specified
     * color.
     *
     * @param color specified color
     * @return true if there's at least an animal
     */
    public boolean hasAvailable(Color color) {
        int nbAnimals = 0;
        for (Species species : Species.values()) {
            nbAnimals += getNbAnimals(color, species);
        }
        return nbAnimals > 0;
    }

    /**
     * Return the number of animals left in the inventory with specific color
     * and species.
     *
     * @param color specified color
     * @param species specified specie
     * @return nbAnimals
     */
    public int getNbAnimals(Color color, Species species) {
        return (int) animals.parallelStream().filter(Objects::nonNull).filter((animal)
                -> (animal.getColor() == color && animal.getSpecies() == species))
                .count();
    }

    /**
     * Puts an animal in the reserve.
     *
     * @param animal specified animal
     * @throws GameException if the animal isn't in run state
     */
    public void putAnimal(Animal animal) {
        if (animal.getState() != AnimalState.RUN) {
            throw new GameException("Animal shouldn't"
                    + " be placed back in pieces, not in run state");
        }
        animals.add(animal);
    }
}
