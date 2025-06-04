package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class Main extends SimpleApplication {

    private DirectionalLight luzPrincipal;
    private Gato gato;
    
    private Vector3f offsetCamara = new Vector3f(0, 4, 10);
    private float suavizadoCamara = 5f;


    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Fondo gris claro
        viewPort.setBackgroundColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));

        // Mapa
        MapaIzquierdo mapaIzquierdo = new MapaIzquierdo(assetManager);
        MapaDerecho mapaDerecho = new MapaDerecho(assetManager);
        rootNode.attachChild(mapaIzquierdo);
        rootNode.attachChild(mapaDerecho);

        // Gato
        Vector3f posicionInicial = new Vector3f(0, 1, 0);
        gato = new Gato(assetManager, rootNode, posicionInicial);

        // Iluminación y sombras
        configurarIluminacion();

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
        dlsr.setLight(luzPrincipal);
        viewPort.addProcessor(dlsr);

        // Cámara
        cam.setLocation(new Vector3f(0, 4f, 10));
        cam.lookAt(posicionInicial, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);

        // Controles
        configurarEntradas();
    }

    private void configurarEntradas() {
        inputManager.addMapping("Izquierda", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Derecha", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Arriba", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Abajo", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Saltar", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_R));
        
        inputManager.addListener(accionListener, "Izquierda", "Derecha", "Arriba", "Abajo", "Saltar", "Reset");
    }

    private final ActionListener accionListener = new ActionListener() {
        @Override
        public void onAction(String nombre, boolean isPressed, float tpf) {
            if (!isPressed) return;

            float paso = 0.5f;

            switch (nombre) {
                case "Izquierda":
                    gato.mover(new Vector3f(-paso, 0, 0));
                    break;
                case "Derecha":
                    gato.mover(new Vector3f(paso, 0, 0));
                    break;
                case "Arriba":
                    gato.mover(new Vector3f(0, 0, -paso));
                    break;
                case "Abajo":
                    gato.mover(new Vector3f(0, 0, paso));
                    break;
                case "Saltar":
                    gato.saltar();
                    break;
                case "Reset":
                    gato.setPosicion(new Vector3f(0, 1, 0));
                    gato.resetearRotacion();
                    break;
            }
        }
    };
    private void actualizarCamara(float tpf) {
        Vector3f objetivo = gato.getPosicion().add(offsetCamara);
        Vector3f posicionActual = cam.getLocation();
        Vector3f nuevaPosicion = posicionActual.interpolateLocal(objetivo, tpf * suavizadoCamara);
        cam.setLocation(nuevaPosicion);
        cam.lookAt(gato.getPosicion(), Vector3f.UNIT_Y);
    }

    private void configurarIluminacion() {
        luzPrincipal = new DirectionalLight();
        luzPrincipal.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());
        luzPrincipal.setColor(ColorRGBA.White.mult(0.7f));
        rootNode.addLight(luzPrincipal);

        DirectionalLight luzRelleno = new DirectionalLight();
        luzRelleno.setDirection(new Vector3f(1, -1, 1).normalizeLocal());
        luzRelleno.setColor(ColorRGBA.White.mult(0.15f));
        rootNode.addLight(luzRelleno);

        DirectionalLight luzContra = new DirectionalLight();
        luzContra.setDirection(new Vector3f(0, 1, 1).normalizeLocal());
        luzContra.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(luzContra);

        DirectionalLight luzFrontal = new DirectionalLight();
        luzFrontal.setDirection(new Vector3f(0, -0.2f, -1f).normalizeLocal());
        luzFrontal.setColor(ColorRGBA.White.mult(0.25f));
        rootNode.addLight(luzFrontal);

        AmbientLight luzAmbiente = new AmbientLight();
        luzAmbiente.setColor(ColorRGBA.White.mult(0.15f));
        rootNode.addLight(luzAmbiente);
    }

    @Override
    public void simpleUpdate(float tpf) {
        gato.actualizar(tpf);
        actualizarCamara(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Render personalizado opcional
    }
}
