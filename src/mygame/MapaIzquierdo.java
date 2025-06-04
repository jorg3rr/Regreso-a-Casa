package mygame;

import com.jme3.asset.AssetManager;

public class MapaIzquierdo extends MapaBase {

    public MapaIzquierdo(AssetManager assetManager) {
        super(assetManager);
    }

    @Override
    protected String getRutaModelo() {
        return "Models/MapaIzquierdo/Mapa_izquierdo.j3o";
    }

    @Override
    protected void inicializar() {
        this.setLocalTranslation(10, 0, 0); // Moverlo a la izquierda
        this.setLocalScale(1.0f);

        // Aquí puedes agregar trampas, enemigos, lógicas especiales:
        // agregarTrampas();
        // agregarZonaDeCuracion();
        // activarTriggerPuerta();
    }
}
