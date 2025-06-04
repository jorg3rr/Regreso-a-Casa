package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;

public class Gato {
    private Spatial modelo;
    private Vector3f posicion;
    private final Node rootNode;
    private final AssetManager assetManager;
    // Físicas de salto
    private boolean enSalto = false;
    private boolean saltoDobleDisponible = true;
    private float velocidadSalto = 0;
    private final float FUERZA_SALTO = 10f;
    private final float GRAVEDAD = 20f;


    public Gato(AssetManager assetManager, Node rootNode, Vector3f posicionInicial) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.posicion = posicionInicial.clone();
        crearModelo();
    }

    private void crearModelo() {
        modelo = assetManager.loadModel("Models/cute-black-cat.obj");
        modelo.setLocalScale(1.2f);

        generarNormales(modelo);
        reemplazarMateriales(modelo);

        modelo.setLocalTranslation(posicion);
        rootNode.attachChild(modelo);
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
            mat.setBoolean("UseMaterialColors", true);
            mat.setColor("Diffuse", new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
            mat.setColor("Specular", ColorRGBA.White.mult(0.5f));
            mat.setFloat("Shininess", 32f);

            if (geom.getName().toLowerCase().contains("eye")) {
                mat.setColor("Diffuse", new ColorRGBA(0.7f, 1f, 0.5f, 1f));
                mat.setColor("Specular", ColorRGBA.White);
                mat.setFloat("Shininess", 128f);
            }

            geom.setMaterial(mat);
        } else if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                reemplazarMateriales(child);
            }
        }
    }

    public void setPosicion(Vector3f nuevaPos) {
        this.posicion.set(nuevaPos);
        modelo.setLocalTranslation(posicion);
    }

    public void mover(Vector3f delta) {
        this.posicion.addLocal(delta);
        modelo.setLocalTranslation(posicion);
    }

    public void setRotacion(float x, float y, float z) {
        modelo.setLocalRotation(modelo.getLocalRotation().fromAngles(x, y, z));
    }

    public void resetearRotacion() {
        modelo.setLocalRotation(modelo.getLocalRotation().fromAngles(0, 0, 0));
    }

    public Vector3f getPosicion() {
        return posicion;
    }

    public Spatial getModelo() {
        return modelo;
    }
    
    public void actualizar(float tpf) {
        if (enSalto) {
            velocidadSalto -= GRAVEDAD * tpf;
            posicion.y += velocidadSalto * tpf;

            if (posicion.y <= 1f) { // Suelo
                posicion.y = 1f;
                enSalto = false;
                saltoDobleDisponible = true;
                velocidadSalto = 0;
            }

            modelo.setLocalTranslation(posicion);
        }
    }
    
    public void saltar() {
        if (!enSalto) {
            velocidadSalto = FUERZA_SALTO;
            enSalto = true;
        } else if (saltoDobleDisponible) {
            velocidadSalto = FUERZA_SALTO * 0.75f;
            saltoDobleDisponible = false;
        }
    }


}   

