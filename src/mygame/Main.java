package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
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
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
    
    private DirectionalLight luzPrincipal;
    private Gato gato;
    private BulletAppState bulletAppState;
    private float suavizadoCamara = 10f;
    private Vector3f offsetCamara = new Vector3f(0, 10, 20);
    private CuboInteractivo cubo1, cubo2, cubo3, cubo4, cubo5, cuboFijo1, cuboFijo2, cuboFijo3, cuboFijo4, cuboFijo5;
    private float tiempoInvulnerable = 0f;
    private final float duracionInvulnerabilidad = 1.5f;
    private AudioNode sonidoVidaPerdida;
    private AudioNode musicaFondo;


    // Variables Gameplay
    private int vidas = 7;
    private BitmapText textoVidas;
    private BitmapText textoEstadoJuego;
    private final Vector3f posicionInicial = new Vector3f(-25.6f, 4, -6);
    private final Vector3f posicionVictoria = new Vector3f(3.4f, 0.8f, -17);
    private boolean juegoTerminado = false;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Regreso a Casa");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Configurar fondo azul cielo
        viewPort.setBackgroundColor(new ColorRGBA(0.53f, 0.81f, 0.92f, 1f));
        
        // Inicializar sistema de físicas
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Cargar mapas
        MapaIzquierdo mapaIzq = new MapaIzquierdo(assetManager);
        MapaDerecho mapaDer = new MapaDerecho(assetManager);
        bulletAppState.getPhysicsSpace().add(mapaIzq.getRigidBodyControl());
        bulletAppState.getPhysicsSpace().add(mapaDer.getRigidBodyControl());
        rootNode.attachChild(mapaIzq);
        rootNode.attachChild(mapaDer);

        // Inicializar gato
        gato = new Gato(assetManager, rootNode, posicionInicial);
        bulletAppState.getPhysicsSpace().add(gato.getCuerpoFisico());

        // Configuración de iluminación
        configurarIluminacion();

        // Configuración de sombras
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
        dlsr.setLight(luzPrincipal);
        viewPort.addProcessor(dlsr);

        // Configuración de cámara
        cam.setFrustumPerspective(60f, (float) cam.getWidth() / cam.getHeight(), 1f, 1000f);
        cam.setLocation(new Vector3f(0, 10, 20));
        cam.lookAt(posicionInicial, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);

        // Configurar controles
        configurarEntradas();
        
        cargarMusicaFondo();
        cargarSonidos();
        
        // Crear cubos móviles
        cubo1 = crearCubo(new Vector3f(-4.2f, 1f, 3.4f), -1.6f, 3.4f, 2.7f, false, 'z', ColorRGBA.Red);
        cubo2 = crearCubo(new Vector3f(-1.3f, 1f, -0.9f), -0.9f, 4.4f, 3.12f, true, 'z', ColorRGBA.Orange);
        cubo3 = crearCubo(new Vector3f(4.3f, 1.2f, 0f), 4.3f, 9.8f, 2.935f, true, 'x', ColorRGBA.Yellow);
        cubo4 = crearCubo(new Vector3f(9.2f, -1f, 2f), 9.1f, 13.3f, 2.1f, true, 'x', ColorRGBA.Green);
        cubo5 = crearCubo(new Vector3f(12.7f, -1.7f, -6f), -6.3f, 0.9f, 3.5f, false, 'z', ColorRGBA.Blue);

        // Crear cubos fijos
        cuboFijo1 = crearCuboFijo(new Vector3f(-13f, 2f, 3.5f), ColorRGBA.Gray);
        cuboFijo2 = crearCuboFijo(new Vector3f(-13f, 5f, -7f), ColorRGBA.Gray);
        cuboFijo3 = crearCuboFijo(new Vector3f(9.9f, -1, -4.9f), ColorRGBA.Gray);
        cuboFijo4 = crearCuboFijo(new Vector3f(7.3f, -1f, -12f), ColorRGBA.Gray);
        cuboFijo5 = crearCuboFijo(new Vector3f(2.8f, 0f, -13f), ColorRGBA.Gray);
        
        // Agregar a la escena
        rootNode.attachChild(cuboFijo1);
        rootNode.attachChild(cuboFijo2);
        rootNode.attachChild(cuboFijo3);
        rootNode.attachChild(cuboFijo4);
        rootNode.attachChild(cuboFijo5);

        // Agregar al mundo físico si usas BulletAppState
        bulletAppState.getPhysicsSpace().add(cuboFijo1.getRigidBody());
        bulletAppState.getPhysicsSpace().add(cuboFijo2.getRigidBody());
        bulletAppState.getPhysicsSpace().add(cuboFijo3.getRigidBody());
        bulletAppState.getPhysicsSpace().add(cuboFijo4.getRigidBody());
        bulletAppState.getPhysicsSpace().add(cuboFijo5.getRigidBody());

        initTextoHUD();
    }

    private CuboInteractivo crearCubo(Vector3f posicion, float min, float max, float velocidad, 
                                    boolean inicioEnMin, char eje, ColorRGBA color) {
        CuboInteractivo cubo = new CuboInteractivo(assetManager, posicion, min, max, 
                                                 velocidad, inicioEnMin, eje, color, true);
        rootNode.attachChild(cubo);
        bulletAppState.getPhysicsSpace().add(cubo.getRigidBody());
        return cubo;
    }

    private CuboInteractivo crearCuboFijo(Vector3f posicion, ColorRGBA color) {
        CuboInteractivo cubo = new CuboInteractivo(assetManager, posicion, 0, 0, 0, false, 'x', color, false);
        rootNode.attachChild(cubo);
        bulletAppState.getPhysicsSpace().add(cubo.getRigidBody());
        return cubo;
    }

    private void initTextoHUD() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        textoVidas = new BitmapText(guiFont, false);
        textoVidas.setSize(guiFont.getCharSet().getRenderedSize() * 2); // Texto más grande
        textoVidas.setColor(ColorRGBA.Black);
        textoVidas.setText("Vidas: " + vidas);
        textoVidas.setLocalTranslation(20, settings.getHeight() - 20, 0); // Mejor posición
        guiNode.attachChild(textoVidas);

        textoEstadoJuego = new BitmapText(guiFont, false);
        textoEstadoJuego.setSize(40);
        textoEstadoJuego.setLocalTranslation(settings.getWidth() / 2 - 150, settings.getHeight() / 2, 0);
        guiNode.attachChild(textoEstadoJuego);
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
            if (juegoTerminado || !isPressed) return;

            float paso = 0.5f;
            Vector3f movimiento = Vector3f.ZERO;

            switch (nombre) {
                case "Izquierda":
                    movimiento = new Vector3f(-paso, 0, 0);
                    break;
                case "Derecha":
                    movimiento = new Vector3f(paso, 0, 0);
                    break;
                case "Arriba":
                    movimiento = new Vector3f(0, 0, -paso);
                    break;
                case "Abajo":
                    movimiento = new Vector3f(0, 0, paso);
                    break;
                case "Saltar":
                    gato.saltar();
                    return;
                case "Reset":
                    reiniciarAplicacion();
                    return;
            }
            
            if (tiempoInvulnerable <= 0) {
                gato.mover(movimiento);
            }
        }
    };

    private void reiniciarPosicion() {
        gato.setPosicion(posicionInicial);
        gato.resetearRotacion();
    }

    private void actualizarCamara(float tpf) {
        Vector3f objetivo = gato.getPosicion().add(offsetCamara);
        Vector3f posicionActual = cam.getLocation();
        Vector3f nuevaPosicion = posicionActual.interpolateLocal(objetivo, tpf * suavizadoCamara);
        cam.setLocation(nuevaPosicion);
        cam.lookAt(gato.getPosicion(), Vector3f.UNIT_Y);
    }

    private void configurarIluminacion() {
        // Configuración mejorada de iluminación
        luzPrincipal = new DirectionalLight();
        luzPrincipal.setDirection(new Vector3f(-0.5f, -1, -0.5f).normalizeLocal());
        luzPrincipal.setColor(ColorRGBA.White.mult(0.8f));
        rootNode.addLight(luzPrincipal);

        AmbientLight luzAmbiente = new AmbientLight();
        luzAmbiente.setColor(new ColorRGBA(0.4f, 0.4f, 0.4f, 1f));
        rootNode.addLight(luzAmbiente);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (juegoTerminado) return;

        // Actualizar objetos
        gato.actualizar(tpf);
        actualizarCamara(tpf);

        // Actualizar cubos móviles
        cubo1.actualizarMovimiento(tpf);
        cubo2.actualizarMovimiento(tpf);
        cubo3.actualizarMovimiento(tpf);
        cubo4.actualizarMovimiento(tpf);
        cubo5.actualizarMovimiento(tpf);

        // Manejar invulnerabilidad
        if (tiempoInvulnerable > 0) {
            tiempoInvulnerable -= tpf;
        }

        // Verificar condiciones de juego
        verificarCaida(tpf);
        verificarColisiones();
        verificarVictoria();
        
        setDisplayStatView(false);
        setDisplayFps(false);
    }

    private void verificarCaida(float tpf) {
        if (gato.getPosicion().y < -5 && tiempoInvulnerable <= 0) {
            perderVida();
        }
    }

    private void verificarColisiones() {
        if (tiempoInvulnerable > 0) return;

        if (colisionConCubo(cubo1) || colisionConCubo(cubo2) || colisionConCubo(cubo3) ||
            colisionConCubo(cubo4) || colisionConCubo(cubo5) ||
            colisionConCubo(cuboFijo1) || colisionConCubo(cuboFijo2) ||
            colisionConCubo(cuboFijo3) || colisionConCubo(cuboFijo4) ||
            colisionConCubo(cuboFijo5)) {
            perderVida();
        }
    }


    private void verificarVictoria() {
        if (gato.getPosicion().distance(posicionVictoria) < 1.5f) {
            textoEstadoJuego.setText("¡HAS GANADO!");
            textoEstadoJuego.setColor(ColorRGBA.Green);
            juegoTerminado = true;
        }
    }

    private boolean colisionConCubo(CuboInteractivo cubo) {
        if (cubo == null) return false;
        float distancia = gato.getPosicion().distance(cubo.getWorldTranslation());
        return distancia < 1.5f; // Radio de colisión aumentado para mejor detección
    }

    private void perderVida() {
        vidas--;
        textoVidas.setText("Vidas: " + vidas);
        tiempoInvulnerable = duracionInvulnerabilidad;
        reiniciarPosicion();
        
        if (sonidoVidaPerdida != null) {
            sonidoVidaPerdida.playInstance();
        }
        
        if (vidas <= 0) {
            textoEstadoJuego.setText("GAME OVER");
            textoEstadoJuego.setColor(ColorRGBA.Red);
            juegoTerminado = true;
        }
        
    }
    
    private void cargarSonidos() {
        sonidoVidaPerdida = new AudioNode(assetManager, "Sounds/vida_perdida.ogg", AudioData.DataType.Buffer);
        sonidoVidaPerdida.setPositional(false);
        sonidoVidaPerdida.setLooping(false);
        sonidoVidaPerdida.setVolume(2);
        rootNode.attachChild(sonidoVidaPerdida);
    }

    private void cargarMusicaFondo() {
        musicaFondo = new AudioNode(assetManager, "Sounds/musica_fondo.ogg", AudioData.DataType.Stream);
        musicaFondo.setLooping(true);
        musicaFondo.setPositional(false);
        musicaFondo.setVolume(0.5f);
        rootNode.attachChild(musicaFondo);
        musicaFondo.play();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Renderizado adicional si es necesario
    }
    
    private void reiniciarAplicacion() {
        stop(); // Detiene la aplicación actual

        // Crea una nueva instancia y arráncala
        Main nuevaApp = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Regreso a Casa");
        nuevaApp.setSettings(settings);
        nuevaApp.start();
    }

}