package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

public class MapaDerecho extends MapaBase {

    public MapaDerecho(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getRutaModelo() {
        return "Models/MapaDerecho/Mapa_derecho.j3o";
    }

    @Override
    protected void inicializar() {
        this.setLocalTranslation(10, 0, 0); // Moverlo a la derecha
        this.setLocalScale(1.0f);
        
        // Cargar modelo animado
        Spatial modeloAnimado = assetManager.loadModel("Models/MapaDerecho/Animacion_derecha.j3o");
        modeloAnimado.setLocalTranslation(-10, 0, 0); // posición relativa en el mapa
        modeloAnimado.setLocalScale(1.0f);

        // Activar animación si la tiene
        activarTodasAnimaciones(modeloAnimado);

        // Adjuntar al mapa
        this.attachChild(modeloAnimado);
        
        // Agregar enemigos o portales propios de este lado
        // agregarEnemigos();
        // agregarTrampasElectricas();
    }
    
}
