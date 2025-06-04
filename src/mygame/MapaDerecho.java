package mygame;

import com.jme3.asset.AssetManager;

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

        // Agregar enemigos o portales propios de este lado
        // agregarEnemigos();
        // agregarTrampasElectricas();
    }
}
