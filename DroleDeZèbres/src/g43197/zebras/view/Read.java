package g43197.zebras.view;

import g43197.zebras.model.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Class to read different objects from the keyboard.
 *
 * @author Philippe
 */
public class Read {

    public static final Scanner CLAVIER = new Scanner(System.in);
    public static final String PROP_BASE_NAME 
            = "g43197.zebras.resources.MessagesBundle";
    public static ResourceBundle messages
            = ResourceBundle.getBundle(PROP_BASE_NAME, new Locale("en"));
    public static final String[] LGGS = {"en", "fr"};

    /**
     * Method that ask the language to the player.
     *
     * @return
     */
    public static ResourceBundle askLanguage() {
        String lgg;
        System.out.println(messages.getString("askLgg"));
        System.out.println(messages.getString("possibleLggsAre"));
        System.out.println(messages.getString("possibleLggs"));
        lgg = CLAVIER.next();
        //TODO enum with lggs, attribut short name
        while (!isLgg(lgg)) {
            System.out.println(messages.getString("wrongLgg"));
            System.out.println(messages.getString("possibleLggsAre"));
            System.out.println(messages.getString("possibleLggs"));
            lgg = CLAVIER.next();
        }
        if (!lgg.equals(LGGS[0])) {
            Locale locale = new Locale(lgg);
            messages = ResourceBundle.getBundle(PROP_BASE_NAME, locale);
        }
        return messages;
    }

    private static boolean isLgg(String msg) {
        for (String lgg : LGGS) {
            if (lgg.equals(msg)) {
                return true;
            }
        }
        return false;
    }

//    /*This method does only work for normal file names like file.txt.*/
//    private static String getFileExtension(File file) {
//        String extension = "", fileName = file.getName();
//        int i = fileName.lastIndexOf('.');
//        if (i > 0) {
//            extension = fileName.substring(i + 1);
//        }
//        return extension;
//    }
//
//    private static void displayLggs() {
//        System.out.println(messages.getString("possibleLanguagesAre"));
//        File folder = new File(PATH_VIEW_PACKAGE);
//        File[] files = folder.listFiles();
//        String ext;
//        int nbLggs = 0;
//        String[] lggs = new String[]{};
//        for (File file : files) {
//            if(file.isFile()){
//                ext = getFileExtension(file);
//                if(ext.equals("properties")){
//                    lggs[nbLggs] = ResourceBundle.
//                }
//            }
//        }
//    }
//
    /**
     * Reads an integer on the keyboard. Continues until it has a good answer.
     *
     * @param msg to explain what the user has to put in
     * @return the integer
     */
    public static int readInt(String msg) {
        System.out.println(msg);

        while (!CLAVIER.hasNextInt()) {
            System.out.println(messages.getString("askInt"));
            CLAVIER.next();
        }

        return CLAVIER.nextInt();
    }

    /**
     * Reads an integer between limits. Continues until it has a good answer.
     * Limits can be selected as well.
     *
     * @param msg to explain what the user has to put in
     * @param min the minimum limit
     * @param max the maximum limit
     * @return the good nb inserted by the user
     */
    public static int readIntBetween(String msg, int min, int max) {
        int nb;
        nb = readInt(msg);
        while (nb < min || max < nb) {
            System.out.println(messages.getString("askNbLimits"));
            System.out.println(messages.getString("minIs") + min);
            System.out.println(messages.getString("maxIs") + max);
            nb = readInt(msg);
        }
        return nb;
    }

    public static boolean readBoolean() {
        String reponse = CLAVIER.next().toUpperCase();
        while (!reponse.equals(messages.getString("yes"))
                && !reponse.equals(messages.getString("no"))) {
            System.out.println(messages.getString("askBoolean"));
            reponse = CLAVIER.next().toUpperCase();
        }
        return reponse.equals(messages.getString("yes"));
    }

    /**
     * Reads a specie on the keyboard. Continues until it has a good answer.
     *
     * @return the specie
     */
    public static Species readSpecies() {
        String msg = messages.getString("askSpecie");
        System.out.println(msg);
        boolean error;
        Species species = null;
        String s;

        do {
            try {
                error = false;
                s = CLAVIER.next().toUpperCase();
                if (s.equals("ZEBRE")) {
                    s = "ZEBRA";
                }
                species = Species.valueOf(s);
            } catch (IllegalArgumentException e) {
                error = true;
                System.out.println(messages.getString("wrongSpecie"));
                System.out.print(messages.getString("specieChoice"));
                System.out.print(messages.getString("gazelle") + ", ");
            }
        } while (error);
        return species;
    }

    public static boolean switchCrocoGaz() {
        System.out.println(messages.getString("wannaSwitchCrG"));
        return readBoolean();
    }

    /**
     * Read a position on the bord. Limits of the bord are 5 rows and 6 columns.
     * Asks the position in relation with impala's position. For example, if
     * impala is on column 4, only the row will be asked, and column 4 will be
     * used.
     *
     * @param impala game's impala
     * @return the inserted Coordinate
     */
    public static Coordinates readAnimalPosition(ImpalaJones impala) {
        String msg = messages.getString("askAnlPos");
        System.out.println(msg);

        int col = impala.getColumn(), row = impala.getRow();
        if (col != -1) {
            String rowMsg = messages.getString("askRow");
            row = readIntBetween(rowMsg, 1, Reserve.MAX_ROWS) - 1;
        } else {
            String colMsg = messages.getString("askCol");
            col = readIntBetween(colMsg, 1, Reserve.MAX_COLUMNS) - 1;
        }
        return new Coordinates(row, col);
    }

    public static int readImpalaFirstPosition() {
        String msg = messages.getString("askInitImpPos");
        return Read.readIntBetween(msg, 1, 22) - 1;
    }

    public static int readImpalaDistance() {
        String msg;
        msg = messages.getString("askNbStepsImp");
        return Read.readInt(msg);
    }
}
