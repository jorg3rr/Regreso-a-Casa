package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class MapaIzquierdo extends Node {

    public MapaIzquierdo(AssetManager assetManager) {
        // Cargar el modelo OBJ
        Spatial mapa = assetManager.loadModel("Models/MapaIzquierdo/Mapa_izquierdo.obj");

        // Escalado o rotación si es necesario
        mapa.setLocalScale(1); // Ajusta si se ve muy grande o pequeño
        mapa.setLocalTranslation(0, 0, 0); // Posición en la escena

        // Adjuntar al nodo
        this.attachChild(mapa);
    }
}
