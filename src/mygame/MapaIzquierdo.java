package mygame;
import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Logger;

public class MapaIzquierdo extends Node {
    
    private static final Logger logger = Logger.getLogger(MapaIzquierdo.class.getName());

    public MapaIzquierdo(AssetManager assetManager) {
        // Cargar el modelo completo del mapa
        Spatial mapaCompleto = assetManager.loadModel("Models/MapaIzquierdo/Mapa_izquierdo.j3o"); // Cambiado a .j3o
        mapaCompleto.setLocalScale(1.0f);
        mapaCompleto.setLocalTranslation(0, 0, 0);
        this.attachChild(mapaCompleto);

        // Activar todas las animaciones en el modelo
        activarTodasAnimaciones(mapaCompleto);
    }

    private void activarTodasAnimaciones(Spatial spatial) {
        // Buscar AnimComposer en el nodo actual
        AnimComposer animComposer = spatial.getControl(AnimComposer.class);
        if (animComposer != null) {
            logger.info("Encontrado AnimComposer en: " + spatial.getName());
            
            // Listar todas las animaciones disponibles
            for (String animName : animComposer.getAnimClipsNames()) {
                logger.info(" - Animación disponible: " + animName);
                try {
                    // Activar cada animación
                    animComposer.setCurrentAction(animName);
                    logger.info("   Activada animación: " + animName);
                } catch (Exception e) {
                    logger.warning("Error al activar animación " + animName + ": " + e.getMessage());
                }
            }
        }

        // Buscar recursivamente en los hijos
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                activarTodasAnimaciones(child);
            }
        }
    }
}
