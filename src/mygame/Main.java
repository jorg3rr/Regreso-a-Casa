package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.TangentBinormalGenerator;

public class Main extends SimpleApplication {

    private DirectionalLight luzPrincipal;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        MapaIzquierdo mapaIzquierdo = new MapaIzquierdo(assetManager);
        MapaDerecho mapaDerecho = new MapaDerecho(assetManager);
        
        // Fondo gris claro
        viewPort.setBackgroundColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));
        
        // Cargar modelo
        Spatial modelo = assetManager.loadModel("Models/cute-black-cat.obj");

        // Mejorar normales para correcta iluminación
        generarNormales(modelo);

        // Reemplazar material por Lighting.j3md
        reemplazarMateriales(modelo);

        // Transformaciones
        modelo.scale(1f);
        modelo.rotate(0, FastMath.PI, 0);
        modelo.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(modelo);
        rootNode.attachChild(mapaIzquierdo);
        rootNode.attachChild(mapaDerecho);
        
        // Mejorar normales para correcta iluminación
        generarNormales(modelo);

        // Reemplazar material por Lighting.j3md
        reemplazarMateriales(modelo);

        // Configurar iluminación
        configurarIluminacion();

        // Activar sombras reales
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
        dlsr.setLight(luzPrincipal);
        viewPort.addProcessor(dlsr);

        // Configurar la cámara
        cam.setLocation(new Vector3f(0, 1.5f, -5));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    private void configurarIluminacion() {
        // Luz principal
        luzPrincipal = new DirectionalLight();
        luzPrincipal.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());
        luzPrincipal.setColor(ColorRGBA.White.mult(0.7f));
        rootNode.addLight(luzPrincipal);

        // Luz de relleno
        DirectionalLight luzRelleno = new DirectionalLight();
        luzRelleno.setDirection(new Vector3f(1, -1, 1).normalizeLocal());
        luzRelleno.setColor(ColorRGBA.White.mult(0.15f));
        rootNode.addLight(luzRelleno);

        // Luz de contra
        DirectionalLight luzContra = new DirectionalLight();
        luzContra.setDirection(new Vector3f(0, 1, 1).normalizeLocal());
        luzContra.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(luzContra);

        // Luz frontal
        DirectionalLight luzFrontal = new DirectionalLight();
        luzFrontal.setDirection(new Vector3f(0, -0.2f, -1f).normalizeLocal());
        luzFrontal.setColor(ColorRGBA.White.mult(0.25f));
        rootNode.addLight(luzFrontal);

        // Luz ambiental suave
        AmbientLight luzAmbiente = new AmbientLight();
        luzAmbiente.setColor(ColorRGBA.White.mult(0.15f));
        rootNode.addLight(luzAmbiente);
    }

    private void generarNormales(Spatial spatial) {
        if (spatial instanceof Geometry) {
            Geometry geom = (Geometry) spatial;
            Mesh mesh = geom.getMesh();
            TangentBinormalGenerator.generate(mesh, true);
        } else if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                generarNormales(child);
            }
        }
    }

    private void reemplazarMateriales(Spatial spatial) {
        if (spatial instanceof Geometry) {
            Geometry geom = (Geometry) spatial;
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

            // Aplicar material base negro suave para el gato
            mat.setBoolean("UseMaterialColors", true);
            mat.setColor("Diffuse", new ColorRGBA(0.95f, 0.94f, 0.9f, 1f));  // Blanco perla 
            mat.setColor("Specular", ColorRGBA.White);  // Brillo
            mat.setFloat("Shininess", 64f);            // Nivel de brillo (1-128)

            // Si tienes una forma que representa los ojos, cambias su color aquí
            // Cambiar color de los ojos a verde pastel
            if (geom.getName().contains("eye")) {  // Asumimos que los ojos tienen "eye" en su nombre
                mat.setColor("Diffuse", new ColorRGBA(0.7f, 1f, 0.5f, 1f));  // Verde pastel
            }

            geom.setMaterial(mat);
        } else if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                reemplazarMateriales(child);
            }
        }
    }


    @Override
    public void simpleUpdate(float tpf) {
        // Lógica de actualización (opcional)
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Lógica de render personalizado (opcional)
    }
}
