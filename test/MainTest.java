import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void testMainExecution() {
        // Bloquer l'apparition de popups éventuelles
        Main.modeTest = true;
        
        // On vérifie que la méthode main s'exécute sans lancer d'exception
        assertDoesNotThrow(() -> {
            Main.main(new String[]{});
        }, "La méthode main ne devrait pas lever d'exception lors de son exécution.");
    }
}
