package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

public class JuegoBasico extends SimpleApplication {

    private Gato gato;

    public static void main(String[] args) {
        JuegoBasico app = new JuegoBasico();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        gato = new Gato(assetManager, rootNode, new Vector3f(0, 1, 0));
        configurarEntradas();
        flyCam.setEnabled(false); // usamos cámara personalizada
    }

    @Override
    public void simpleUpdate(float tpf) {
        gato.actualizar(tpf); // actualiza físicas del gato (salto)
        actualizarCamara();   // cámara sigue al gato
    }

    private void configurarEntradas() {
        inputManager.addMapping("MoverIzquierda", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("MoverDerecha", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("MoverArriba", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("MoverAbajo", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Saltar", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Resetear", new KeyTrigger(KeyInput.KEY_R));

        inputManager.addListener(accionListener,
            "MoverIzquierda", "MoverDerecha",
            "MoverArriba", "MoverAbajo",
            "Saltar", "Resetear"
        );
    }

    private final ActionListener accionListener = new ActionListener() {
        @Override
        public void onAction(String nombre, boolean isPressed, float tpf) {
            if (!isPressed) return;

            float paso = 1f;

            switch (nombre) {
                case "MoverIzquierda":
                    gato.mover(new Vector3f(-paso, 0, 0));
                    break;
                case "MoverDerecha":
                    gato.mover(new Vector3f(paso, 0, 0));
                    break;
                case "MoverArriba":
                    gato.mover(new Vector3f(0, 0, -paso));
                    break;
                case "MoverAbajo":
                    gato.mover(new Vector3f(0, 0, paso));
                    break;
                case "Saltar":
                    gato.saltar();
                    break;
                case "Resetear":
                    gato.setPosicion(new Vector3f(0, 1, 0));
                    gato.resetearRotacion();
                    break;
            }
        }
    };

    private void actualizarCamara() {
        Vector3f posicionGato = gato.getPosicion();
        Vector3f posicionCamara = posicionGato.add(0, 5, 10); // altura y distancia trasera
        cam.setLocation(posicionCamara);
        cam.lookAt(posicionGato, Vector3f.UNIT_Y);
    }
}
