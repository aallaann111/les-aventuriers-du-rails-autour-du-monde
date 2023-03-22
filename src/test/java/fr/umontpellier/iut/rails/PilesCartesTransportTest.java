package fr.umontpellier.iut.rails;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.umontpellier.iut.rails.data.CarteTransport;
import fr.umontpellier.iut.rails.data.Couleur;
import fr.umontpellier.iut.rails.data.Destination;
import fr.umontpellier.iut.rails.data.TypeCarteTransport;
import fr.umontpellier.iut.rails.data.Ville;

import static org.junit.jupiter.api.Assertions.*;

class PilesCartesTransportTest {

    /*@Test
    public void testPiocher() {
        PilesCartesTransport piles = new PilesCartesTransport();
        int size = piles.size();

        // On pioche une carte
        CarteTransport carte = piles.piocher();
        assertNotNull(carte);

        // Vérification que la pile a bien diminué de taille
        assertNotNull(cartePiochee);
        assertEquals(size - 1, piles.size());
    }


    @Test
    public void testDefausser() {
        PilesCartesTransport piles = new PilesCartesTransport();
        CarteTransport carte = new CarteTransport(TypeCarteTransport.WAGON, Couleur.BLANC, false, false);

        // On ajoute une carte à la pile
        piles.defausser(carte);

        // Vérification que la carte a été ajoutée à la pile de défausse
        assertTrue(piles.getPileDefausse().contains(carte));
    }

    @Test
    public void testEstVide() {
        CarteTransport carte = new CarteTransport(TypeCarteTransport.WAGON, Couleur.BLANC, false, false);
        CarteTransport carte2 = new CarteTransport(TypeCarteTransport.WAGON, Couleur.VERT, true, false);
        CarteTransport carte3 = new CarteTransport(TypeCarteTransport.WAGON, Couleur.ROUGE, false, false);
        List<CarteTransport> = new List<CarteTransport>();
        PilesCartesTransport piles = new PilesCartesTransport();

        // Vérification que la pile n'est pas vide initialement
        assertFalse(piles.estVide());

        // On pioche toutes les cartes
        int size = piles.length();
        for (int i = 0; i < size; i++) {
            piles.piocher();
        }

        // Vérification que la pile est vide après avoir pioché toutes les cartes
        assertTrue(piles.estVide());
        }*/

}