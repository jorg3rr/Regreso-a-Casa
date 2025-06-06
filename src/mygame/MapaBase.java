package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.bullet.collision.shapes.CollisionShape;

import java.util.logging.Logger;

public abstract class MapaBase extends Node {

    protected final AssetManager assetManager;
    protected static final Logger logger = Logger.getLogger(MapaBase.class.getName());
    
    protected RigidBodyControl rigidBody;
    protected abstract String getRutaModelo(); // Cada hijo especifica su modelo

    public RigidBodyControl getRigidBodyControl() {
        return rigidBody;
    }

    public MapaBase(AssetManager assetManager) {
        this.assetManager = assetManager;
        inicializarMapa();
    }

    private void inicializarMapa() {
        Spatial mapaCompleto = assetManager.loadModel(getRutaModelo());
        mapaCompleto.setLocalScale(1.0f);
        mapaCompleto.setLocalTranslation(0, 0, 0);
        this.attachChild(mapaCompleto);
        //activarTodasAnimaciones(mapaCompleto);
        
        // ACTIVAR COLISIÓN SÓLIDA
        CollisionShape colision = CollisionShapeFactory.createMeshShape(mapaCompleto);
        RigidBodyControl rigidBody = new RigidBodyControl(colision, 0); // masa 0 = estático
        mapaCompleto.addControl(rigidBody);

        // IMPORTANTE: Guardar el RigidBody para agregarlo más tarde al BulletAppState
        this.rigidBody = rigidBody;
        
        inicializar(); // <<< hook para lógica personalizada
    }
    
    protected void inicializar() {
        // Este método será sobreescrito por los hijos (MapaIzquierdo, MapaDerecho)
    }
    
    protected void activarTodasAnimaciones(Spatial spatial) {
        AnimComposer animComposer = spatial.getControl(AnimComposer.class);
        if (animComposer != null) {
            logger.info("Encontrado AnimComposer en: " + spatial.getName());
            for (String animName : animComposer.getAnimClipsNames()) {
                logger.info(" - Animación disponible: " + animName);
                try {
                    animComposer.setCurrentAction(animName);
                    logger.info("   Activada animación: " + animName);
                } catch (Exception e) {
                    logger.warning("Error al activar animación " + animName + ": " + e.getMessage());
                }
            }
        }

        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                activarTodasAnimaciones(child);
            }
        }
    }
    
}