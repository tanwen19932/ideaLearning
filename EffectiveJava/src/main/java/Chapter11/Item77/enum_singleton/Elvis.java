package Chapter11.Item77.enum_singleton;

import java.util.*;

public enum Elvis {
    INSTANCE;
    private String[] favoriteSongs =
        { "Hound Dog", "Heartbreak Hotel" };
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }

    public static void main(String[] args){
      Elvis.INSTANCE.printFavorites();
    }
}