import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void testMainExecution() {
        // On vérifie que la méthode main s'exécute sans lancer d'exception (comme un NullPointerException)
        // Attention: cela va brièvement ouvrir l'interface graphique lors de l'exécution du test.
        assertDoesNotThrow(() -> {
            Main.main(new String[]{});
        }, "La méthode main ne devrait pas lever d'exception lors de son exécution.");
    }
}
