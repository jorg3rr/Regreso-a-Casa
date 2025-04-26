package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Cambiar el color de fondo a gris claro
        viewPort.setBackgroundColor(new ColorRGBA(0.92f, 0.92f, 0.92f, 1f));

        // Cargar el modelo .obj (debe tener el .mtl en la misma carpeta)
        Spatial modelo = assetManager.loadModel("Models/cute-black-cat.obj");

        // NO se sobrescribe el material para respetar el .mtl

        // Ajustar transformación
        modelo.scale(1f);
        modelo.rotate(0, FastMath.PI, 0); // Si está al revés
        modelo.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(modelo);

        // Configurar iluminación realista y equilibrada
        configurarIluminacion();

        // Configurar la cámara
        cam.setLocation(new Vector3f(0, 1.5f, 5));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    private void configurarIluminacion() {
        // Luz principal
        DirectionalLight luzPrincipal = new DirectionalLight();
        luzPrincipal.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());
        luzPrincipal.setColor(ColorRGBA.White.mult(0.7f));
        rootNode.addLight(luzPrincipal);

        // Luz de relleno
        DirectionalLight luzRelleno = new DirectionalLight();
        luzRelleno.setDirection(new Vector3f(1, -1, 1).normalizeLocal());
        luzRelleno.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(luzRelleno);

        // Luz de contra
        DirectionalLight luzContra = new DirectionalLight();
        luzContra.setDirection(new Vector3f(0, 1, 1).normalizeLocal());
        luzContra.setColor(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(luzContra);

        // Luz frontal para resaltar ojos
        DirectionalLight luzFrontal = new DirectionalLight();
        luzFrontal.setDirection(new Vector3f(0, -0.2f, -1f).normalizeLocal());
        luzFrontal.setColor(ColorRGBA.White.mult(0.6f));
        rootNode.addLight(luzFrontal);

        // Luz ambiental
        AmbientLight luzAmbiente = new AmbientLight();
        luzAmbiente.setColor(ColorRGBA.White.mult(0.4f));
        rootNode.addLight(luzAmbiente);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Lógica de actualización si es necesaria
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Lógica de render personalizado si se necesita
    }
}
