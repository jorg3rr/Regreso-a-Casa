package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.util.TangentBinormalGenerator;

public class Gato {
    private Spatial modelo;
    private final Node rootNode;
    private final AssetManager assetManager;

    // Físicas de salto
    private boolean enSalto = false;
    private boolean saltoDobleDisponible = true;
    private float velocidadSalto = 0;
    private final float FUERZA_SALTO = 7f;
    private final float GRAVEDAD = 20f;

    private RigidBodyControl cuerpoFisico;

    public Gato(AssetManager assetManager, Node rootNode, Vector3f posicionInicial) {
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        crearModelo(posicionInicial);
    }

    private void crearModelo(Vector3f posicionInicial) {
        modelo = assetManager.loadModel("Models/cute-black-cat.obj");
        modelo.setLocalScale(1.2f);

        generarNormales(modelo);
        reemplazarMateriales(modelo);

        modelo.setLocalTranslation(posicionInicial);
        rootNode.attachChild(modelo);

        // Cambiar la forma de colisión por una cápsula
        CapsuleCollisionShape colisionador = new CapsuleCollisionShape(0.5f, 1.0f, 1);
        cuerpoFisico = new RigidBodyControl(colisionador, 1.0f);
        cuerpoFisico.setAngularFactor(0); // Bloquea rotación
        cuerpoFisico.setFriction(1.0f);   // Más fricción
        modelo.addControl(cuerpoFisico);
    }

    private void generarNormales(Spatial spatial) {
        if (spatial instanceof Geometry) {
            Mesh mesh = ((Geometry) spatial).getMesh();
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
        modelo.setLocalTranslation(nuevaPos);
    }

    public void mover(Vector3f direccion) {
        Vector3f velActual = cuerpoFisico.getLinearVelocity();
        Vector3f nuevaVelocidad = new Vector3f(direccion.x * 5f, velActual.y, direccion.z * 5f);
        cuerpoFisico.setLinearVelocity(nuevaVelocidad);
    }

    public void setRotacion(float x, float y, float z) {
        modelo.setLocalRotation(modelo.getLocalRotation().fromAngles(x, y, z));
    }

    public void resetearRotacion() {
        modelo.setLocalRotation(modelo.getLocalRotation().fromAngles(0, 0, 0));
    }

    public Vector3f getPosicion() {
        return modelo.getLocalTranslation().clone();
    }

    public Spatial getModelo() {
        return modelo;
    }

    public void actualizar(float tpf) {
        if (enSalto) {
            velocidadSalto -= GRAVEDAD * tpf;
            float nuevaY = modelo.getLocalTranslation().y + velocidadSalto * tpf;

            if (nuevaY <= 1f) {
                nuevaY = 1f;
                enSalto = false;
                saltoDobleDisponible = true;
                velocidadSalto = 0;
            }

            Vector3f nuevaPos = modelo.getLocalTranslation().clone();
            nuevaPos.y = nuevaY;
            modelo.setLocalTranslation(nuevaPos);
        }
    }

    public void saltar() {
        Vector3f velocidadActual = cuerpoFisico.getLinearVelocity();

        if (!enSalto) {
            cuerpoFisico.setLinearVelocity(new Vector3f(velocidadActual.x, FUERZA_SALTO, velocidadActual.z));
            enSalto = true;
        } else if (saltoDobleDisponible) {
            cuerpoFisico.setLinearVelocity(new Vector3f(velocidadActual.x, FUERZA_SALTO * 0.75f, velocidadActual.z));
            saltoDobleDisponible = false;
        }
    }

    public RigidBodyControl getCuerpoFisico() {
        return cuerpoFisico;
    }
    
}