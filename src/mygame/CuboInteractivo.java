package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Node;

public class CuboInteractivo extends Node {

    private final RigidBodyControl rigidBody;
    private float velocidad;
    private float min;
    private float max;
    private boolean avanzar;
    private char ejeMovimiento; // 'x' o 'z'
    private boolean esMovil;
    
    public CuboInteractivo(AssetManager assetManager, Vector3f posicionInicial, float min, float max,
                     float velocidad, boolean avanzarInicial, char ejeMovimiento, ColorRGBA color, boolean esMovil) {

        this.velocidad = velocidad;
        this.min = min;
        this.max = max;
        this.avanzar = avanzarInicial;
        this.ejeMovimiento = ejeMovimiento;
        this.esMovil = esMovil;

        // Crear cubo visual
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry geom = new Geometry("CuboInteractivo", box);

        // Aplica tamaño diferente si es móvil o no
        if (esMovil) {
            geom.setLocalScale(1f); // Tamaño normal
        } else {
            geom.setLocalScale(1.5f); // Tamaño más grande solo para fijos
        }

        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.0f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);


        this.attachChild(geom);
        this.setLocalTranslation(posicionInicial);

        // Colisión física
        rigidBody = new RigidBodyControl(new BoxCollisionShape(new Vector3f(0.5f, 0.5f, 0.5f)), 0);
        rigidBody.setKinematic(true); 
        this.addControl(rigidBody);
    }

    public RigidBodyControl getRigidBody() {
        return rigidBody;
    }

    public void actualizarMovimiento(float tpf) {
        if (!esMovil) return;
        
        Vector3f pos = this.getLocalTranslation();
        float nuevaPos;

        if (ejeMovimiento == 'z') {
            nuevaPos = pos.z + (avanzar ? velocidad : -velocidad) * tpf;
            if (nuevaPos < min) {
                nuevaPos = min;
                avanzar = true;
            } else if (nuevaPos > max) {
                nuevaPos = max;
                avanzar = false;
            }
            this.setLocalTranslation(pos.x, pos.y, nuevaPos);
        } else if (ejeMovimiento == 'x') {
            nuevaPos = pos.x + (avanzar ? velocidad : -velocidad) * tpf;
            if (nuevaPos < min) {
                nuevaPos = min;
                avanzar = true;
            } else if (nuevaPos > max) {
                nuevaPos = max;
                avanzar = false;
            }
            this.setLocalTranslation(nuevaPos, pos.y, pos.z);
        }

        rigidBody.setPhysicsLocation(this.getWorldTranslation()); // actualizar física
    }
    
    public boolean esMovil() {
        return esMovil;
    }

}