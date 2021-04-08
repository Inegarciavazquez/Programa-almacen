package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Main extends Application {
    // Colección de elementos gráficos que se van a representar en 3D
    private Group grupo3D;
    private Group estantes;
    private Group grupo_palets;
    private Group grupo_contenedores;
    // Cámara utilizada en la escena 3D
    private PerspectiveCamera camara;
    // Posición del ratón en la ventana
    private double ratonX, ratonY;
    private double ratonXVentanaAntes, ratonYVentanaAntes;
    ComboBox comboProducto;
    Label buscar = new Label("");
    Label click_contenedor = new Label("");
    @Override
    public void start(Stage stage) {// Se ejecuta al comienzo del programa
        Document archivoXml = null;
        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        dbf = DocumentBuilderFactory.newInstance();
        try {
            //se lee toda la información escrita en almacen.xml
            db = dbf.newDocumentBuilder();
            archivoXml = db.parse("almacen.xml");
        } catch (Exception ex) {
            String causa = ex.getMessage();
            System.out.println("Error: " + causa);
            System.exit(0);
        }
        //se extrae la info de almacen.xml y se guarda
        Element nodoRaiz = archivoXml.getDocumentElement();
        NodeList listaEstanteria = nodoRaiz.getElementsByTagName("estanteria");
        int numEstanteria = listaEstanteria.getLength();
        NodeList listaProducto = nodoRaiz.getElementsByTagName("producto");
        int numProducto = listaProducto.getLength();
        NodeList listaTipo = nodoRaiz.getElementsByTagName("tipo");
        int numTipo = listaTipo.getLength();

        ArrayList<String> Producto_idProducto = new ArrayList<String>();
        ArrayList<String> Producto_idTipo = new ArrayList<String>();
        for (int i = 0; i < numProducto; i++) {
            Element producto = (Element) listaProducto.item(i);
            Producto_idProducto.add(producto.getAttribute("idProducto"));
            Producto_idTipo.add(producto.getAttribute("idTipo"));
        }

        ArrayList<String> Tipo_color = new ArrayList<String>();
        ArrayList<String> Tipo_idTipo = new ArrayList<String>();
        for (int i = 0; i < numTipo; i++) {
            Element tipo = (Element) listaTipo.item(i);
            Tipo_color.add(tipo.getAttribute("color"));
            Tipo_idTipo.add(tipo.getAttribute("idTipo"));
        }

        ArrayList<Pallet> Palet = new ArrayList<Pallet>();
        //se recorren las estanterias
        for (int e = 0; e <numEstanteria; e++) {
            Element estanteria = (Element) listaEstanteria.item(e);
            NodeList listaBalda = estanteria.getElementsByTagName("balda");
            int numBalda = listaBalda.getLength();
            //se recorren las baldas
            for (int b =0; b <numBalda; b++) {
                Element balda = (Element) listaBalda.item(b);
                NodeList listaPalet = balda.getElementsByTagName("palet");
                int numPalet = listaPalet.getLength();
                //se recorren los pallets
                for (int p = 0; p <numPalet ; p++) {
                    Element palett = (Element) listaPalet.item(p);
                    boolean encontrado= false;
                    int idp=0, idt=0;
                    //se busca el idtipo correspondiente al palet
                    while (! encontrado){
                        if ((palett.getAttribute("idProducto").compareTo(Producto_idProducto.get(idp)))==0){
                            if((Producto_idTipo.get(idp).compareTo(Tipo_idTipo.get(idt)))==0){
                                encontrado=true;
                            }else idt++;
                        }else idp++;
                    }
                    //se guarda la informacion del palet
                    Pallet pal = new Pallet(estanteria.getAttribute("numero"), balda.getAttribute("altura"), palett.getAttribute("alto"),
                            palett.getAttribute("ancho"), palett.getAttribute("cantidadProducto"), palett.getAttribute("delante"),
                            palett.getAttribute("idPalet"), palett.getAttribute("idProducto"), palett.getAttribute("largo"),
                            palett.getAttribute("posicion"),Producto_idTipo.get(idp),Tipo_color.get(idt));
                    Palet.add(pal);
               }
            }

        }

        //--------------------Subescena 3D-------------------------
        double[] posyEstanterias = {0, 5.3, 8.6, 13.9};
        estantes = new Group();
        for (int e = 0; e < 4; e++) {// se dibujan las estanterias
            for (int a = 0; a < 8; a++) {
                Box estanteria = new Box(36.3, 3.3, 0.1);
                estanteria.setTranslateX(18.15);
                estanteria.setTranslateY(posyEstanterias[e]+1.65);
                estanteria.setTranslateZ((a * 2)+0.05);
                estanteria.setMaterial(new PhongMaterial(Color.GREY));
                estantes.getChildren().add(estanteria);
            }
        }
        grupo_palets=new Group();
        grupo_contenedores=new Group();
        for (int p = 0; p < Palet.size(); p++) {//se dibujan palets y contenedores
            Box dibujo_palet = new Box(1.2, 1.2, 0.2);
            Box dibujo_contenedores = new Box(Palet.get(p).largo, Palet.get(p).ancho, Palet.get(p).alto);
            dibujo_palet.setTranslateX(0.3+0.6+1.5*(Palet.get(p).posicion-1));
            dibujo_contenedores.setTranslateX(0.3+0.6+1.5*(Palet.get(p).posicion-1));
            int numest=Palet.get(p).num_estanteria;
            if (((numest==1 || numest==3) & ! Palet.get(p).delante) || ((numest==2 || numest==4) & Palet.get(p).delante)){//si estan en la izquierda de la estanteria
                dibujo_palet.setTranslateY(0.6+0.3+posyEstanterias[numest-1]);
                dibujo_contenedores.setTranslateY(0.6+0.3+posyEstanterias[numest-1]);
                }
            if (((numest==1 || numest==3) & Palet.get(p).delante) || ((numest==2 || numest==4) & ! Palet.get(p).delante)){//si estan en la derecha de la estanteria
                dibujo_palet.setTranslateY(1.5+0.6+0.3+posyEstanterias[numest-1]);
                dibujo_contenedores.setTranslateY(1.5+0.6+0.3+posyEstanterias[numest-1]);
            }
            dibujo_palet.setTranslateZ(0.1+0.1+2*(Palet.get(p).altura_balda-1));
            dibujo_contenedores.setTranslateZ(0.1+0.2+(Palet.get(p).alto/2)+2*(Palet.get(p).altura_balda-1));
            dibujo_palet.setMaterial(new PhongMaterial(Color.LIGHTGREY));
            dibujo_contenedores.setMaterial(new PhongMaterial(new Color(Palet.get(p).color_r,Palet.get(p).color_g,Palet.get(p).color_b,0.9)));// el ultimo numero es la opacidad 0=transparente 1=opaco
            grupo_palets.getChildren().add(dibujo_palet);
            grupo_contenedores.getChildren().add(dibujo_contenedores);
       }

        grupo3D=new Group();
        grupo3D.getChildren().add(estantes);
        grupo3D.getChildren().add(grupo_palets);
        grupo3D.getChildren().add(grupo_contenedores);
        // Añade el grupo a la escena 3D
        SubScene subEscena3D = new SubScene(grupo3D, 0, 0, true, SceneAntialiasing.BALANCED);
        // Establece un color blanco de fondo
        subEscena3D.setFill(new Color(0.8, 0.8, 0.8, 1));
        // Crea una cámara para realizar la proyección
        camara = new PerspectiveCamera(true);
        // Se van a representar en pantalla todos los objetos situados desde una distancia
        // de 0.1 unidades de la cámara y hasta una distancia de 100000 unidades
        camara.setNearClip(0.1);
        camara.setFarClip(100000.0);
        // A la cámara se le aplican las rotaciones y traslación indicadas por parámetro para
        // situarla en una posición inicial. Primero una traslación de -3000 unidades en el
        // eje Z, luego una rotación de -30º en el eje Y y finalmente una rotación de 160º
        // en el eje X
        Rotate rotacionXCamara = new Rotate(-80, Rotate.X_AXIS);
        Rotate rotacionYCamara = new Rotate(-120, Rotate.Y_AXIS);
        Rotate rotacionZCamara = new Rotate(-15, Rotate.Z_AXIS);
        Translate traslacionCamara = new Translate(0, 4, -80);
        camara.getTransforms().addAll(rotacionXCamara, rotacionZCamara,rotacionYCamara, traslacionCamara);
        // Establece la cámara para la escena 3D
        subEscena3D.setCamera(camara);
        // Se define en la escena una luz puntual situada en (10000, 20000, 30000) con un
        // color gris definido por sus componentes roja = verde = azul = 0.6
        PointLight luzPuntual = new PointLight(new Color(0.8, 0.8, 0.8, 1));
        luzPuntual.setTranslateX(100);
        luzPuntual.setTranslateY(50);
        luzPuntual.setTranslateZ(20);
        grupo3D.getChildren().add(luzPuntual);
        // Añade una luz difusa de color gris formada por rojo = verde = azul = 0.3
        grupo3D.getChildren().add(new AmbientLight(new Color(0.5, 0.5, 0.5, 1)));

        //-------------------------Parte superior de la ventana en 2D-----------------------

        Label etiquetaProductos = new Label("Productos:");
        etiquetaProductos.setPadding(new Insets(5)); // Márgenes de 5 puntos alrededor
        buscar.setPadding(new Insets(5));
        click_contenedor.setPadding(new Insets(5));

        //desplegables tipo de producto
        ArrayList<String> opciones_tipo = new ArrayList<String>(Tipo_idTipo);
        opciones_tipo.add("Todos");
        ComboBox comboTipo = new ComboBox(FXCollections.observableArrayList(opciones_tipo));
        comboTipo.setValue("Todos");
        comboProducto = new ComboBox(FXCollections.observableArrayList("Todos"));
        comboProducto.getItems().addAll(Producto_idProducto);
        comboProducto.setValue("Todos");

        Label etiquetaEstanteria = new Label("Estantería");
        etiquetaEstanteria.setPadding(new Insets(5));

        //checkbox para las estanterias y baldas
        CheckBox checkbaldas = new CheckBox("Baldas");
        checkbaldas.setPadding(new Insets(5));
        checkbaldas.setSelected(true); // Inicialmente el CheckBox está seleccionado
        checkbaldas.setOnAction(evento -> {
            estantes.setVisible(checkbaldas.isSelected());
        });
        CheckBox check_estanteria1 = new CheckBox("1");
        check_estanteria1.setPadding(new Insets(5));
        check_estanteria1.setSelected(true);
        check_estanteria1.setOnAction(evento -> {
          for (int e =0; e<8; e++)
            estantes.getChildren().get(e).setVisible(check_estanteria1.isSelected());
            for (int p = 0; p < Palet.size(); p++) {
                if (Palet.get(p).num_estanteria==1){
                    grupo_palets.getChildren().get(p).setVisible(check_estanteria1.isSelected());
                    grupo_contenedores.getChildren().get(p).setVisible(check_estanteria1.isSelected());
                }
            }

        });
        CheckBox check_estanteria2 = new CheckBox("2");
        check_estanteria2.setPadding(new Insets(5));
        check_estanteria2.setSelected(true);
        check_estanteria2.setOnAction(evento -> {
            for (int e =8; e<16; e++)
                estantes.getChildren().get(e).setVisible(check_estanteria2.isSelected());
            for (int p = 0; p < Palet.size(); p++) {
                if (Palet.get(p).num_estanteria==2){
                    grupo_palets.getChildren().get(p).setVisible(check_estanteria2.isSelected());
                    grupo_contenedores.getChildren().get(p).setVisible(check_estanteria2.isSelected());
                }
            }

        });
        CheckBox check_estanteria3 = new CheckBox("3");
        check_estanteria3.setPadding(new Insets(5));
        check_estanteria3.setSelected(true);
        check_estanteria3.setOnAction(evento -> {
            for (int e =16; e<24; e++)
                estantes.getChildren().get(e).setVisible(check_estanteria3.isSelected());
            for (int p = 0; p < Palet.size(); p++) {
                if (Palet.get(p).num_estanteria==3){
                    grupo_palets.getChildren().get(p).setVisible(check_estanteria3.isSelected());
                    grupo_contenedores.getChildren().get(p).setVisible(check_estanteria3.isSelected());
                }
            }

        });
        CheckBox check_estanteria4 = new CheckBox("4");
        check_estanteria4.setPadding(new Insets(5));
        check_estanteria4.setSelected(true);
        check_estanteria4.setOnAction(evento -> {
            for (int e =24; e<32; e++)
                estantes.getChildren().get(e).setVisible(check_estanteria4.isSelected());
            for (int p = 0; p < Palet.size(); p++) {
                if (Palet.get(p).num_estanteria==4){
                    grupo_palets.getChildren().get(p).setVisible(check_estanteria4.isSelected());
                    grupo_contenedores.getChildren().get(p).setVisible(check_estanteria4.isSelected());
                }
            }

        });
        //en  funcion del tipo seleccionado se dan las opciones correspondientes de producto
        comboTipo.setOnAction(evento -> {
            if( comboTipo.getSelectionModel().getSelectedItem().equals("Todos")){
                comboProducto.getItems().add("Todos");
                comboProducto.getItems().addAll(FXCollections.observableArrayList(Producto_idProducto));
            }else {
                comboProducto.getItems().clear();
                comboProducto.getItems().add("Todos");
                for (int p = 0; p < numProducto; p++) {
                    if (comboTipo.getSelectionModel().getSelectedItem().equals(Producto_idTipo.get(p)))
                        comboProducto.getItems().add(Producto_idProducto.get(p));
                }
            }comboProducto.setValue("Todos");
        });
        Button boton_buscar = new Button("Buscar");
        boton_buscar.setPadding(new Insets(5));
        //cuando se pulsa buscar
        boton_buscar.setOnAction(evento -> {
            int contador=0, litros=0;
            //para cada pallet
            for (int p = 0; p < Palet.size(); p++) {
                //si las condiciones seleccionadas coinciden con las del palet
                if ( ((comboTipo.getSelectionModel().getSelectedItem().equals(Palet.get(p).idTipo))&&(comboProducto.getSelectionModel().getSelectedItem().equals("Todos"))) ||
                        ((comboTipo.getSelectionModel().getSelectedItem().equals(Palet.get(p).idTipo))&&(comboProducto.getSelectionModel().getSelectedItem().equals(Palet.get(p).idProducto))) ||
                        (( comboTipo.getSelectionModel().getSelectedItem().equals("Todos"))&&(comboProducto.getSelectionModel().getSelectedItem().equals("Todos"))) ||
                        (( comboTipo.getSelectionModel().getSelectedItem().equals("Todos"))&&(comboProducto.getSelectionModel().getSelectedItem().equals(Palet.get(p).idProducto))))  {
                    //si el palet se encuentra en una estanteria seleccionada como visible
                    if((Palet.get(p).num_estanteria==1 && check_estanteria1.isSelected())||(Palet.get(p).num_estanteria==2 && check_estanteria2.isSelected())
                    ||(Palet.get(p).num_estanteria==3 && check_estanteria3.isSelected())||(Palet.get(p).num_estanteria==4 && check_estanteria4.isSelected())) {
                        //sehace visible el palet
                        grupo_palets.getChildren().get(p).setVisible(true);
                        grupo_contenedores.getChildren().get(p).setVisible(true);
                        //se actualizan el numero de palets visibles y los litros de capacidad de los mismos
                        contador++;
                       litros= litros + Palet.get(p).cantidadProducto;
                    }
                } else {
                    //se oculta el palet
                    grupo_palets.getChildren().get(p).setVisible(false);
                    grupo_contenedores.getChildren().get(p).setVisible(false);
                }
            }buscar.setText(contador + " palets = " + litros + " L.");
        });

        ToolBar barraSuperior = new ToolBar();
        barraSuperior.getItems().add(etiquetaEstanteria);
        barraSuperior.getItems().add(check_estanteria1);
        barraSuperior.getItems().add(check_estanteria2);
        barraSuperior.getItems().add(check_estanteria3);
        barraSuperior.getItems().add(check_estanteria4);
        barraSuperior.getItems().add(checkbaldas);
        barraSuperior.getItems().add(etiquetaProductos);
        barraSuperior.getItems().add(comboTipo);
        barraSuperior.getItems().add(comboProducto);
        barraSuperior.getItems().add(boton_buscar);
        barraSuperior.getItems().add(buscar);
        barraSuperior.getItems().add(click_contenedor);
        barraSuperior.setOrientation(Orientation.HORIZONTAL);


        // -------------------------- Ventana --------------------------
        BorderPane contenidoVentana = new BorderPane();
        contenidoVentana.setCenter(subEscena3D);
        contenidoVentana.setTop(barraSuperior);
        subEscena3D.heightProperty().bind(contenidoVentana.heightProperty());
        subEscena3D.widthProperty().bind(contenidoVentana.widthProperty());
        Scene escena = new Scene(contenidoVentana);

        //cuando se clicka el raton
        escena.setOnMousePressed((MouseEvent evento) -> {
            //se guardan las coordenadas X e Y donde se pulsó el ratón.
            ratonXVentanaAntes = evento.getSceneX();
            ratonYVentanaAntes = evento.getSceneY();
        });

        //cuando se gira la rueda del raton
        escena.setOnScroll((ScrollEvent evento) -> {
            //Con el método getDeltaY getRotate se recoge el ángulo de rotación en el eje Z de la cámara
            // Con el método getDeltaY se recoge la cantidad de movimiento en la rueda del ratón
            double factor = 0.02;
            camara.setRotationAxis(Rotate.Z_AXIS);
            double roll = camara.getRotate() + evento.getDeltaY() * factor;
            // Modifica la rotación de la cámara en su eje Z
            Rotate rotacion = new Rotate(roll, Rotate.Z_AXIS);
            camara.getTransforms().addAll(rotacion);
        });
        //cuando se pulsa una tecla
        escena.setOnKeyPressed(event -> {
            double desplazamiento = 10;// Cantidad de espacio que se desplaza la cámara
            if (event.isShiftDown()) {//mayusculas pulsadas
                desplazamiento = 60;
            }

            KeyCode tecla = event.getCode(); // Tecla pulsada
            if (tecla == KeyCode.S) {
                //desplazamiento hacia atrás
                Translate traslacion = new Translate(0, 0, -desplazamiento);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.W) { // Desplazamiento hacia delante
                Translate traslacion = new Translate(0, 0, desplazamiento);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.A) { // Desplazamiento hacia la izquierda
                Translate traslacion = new Translate(-desplazamiento, 0, 0);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.D) { // Desplazamiento hacia la derecha
                Translate traslacion = new Translate(desplazamiento, 0, 0);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.E) { // Desplazamiento hacia arriba
                Translate traslacion = new Translate(0, -desplazamiento, 0);
                camara.getTransforms().addAll(traslacion);
            }
            if (tecla == KeyCode.C) { // Desplazamiento hacia abajo
                Translate traslacion = new Translate(0, desplazamiento, 0);
                camara.getTransforms().addAll(traslacion);
            }
        });

        //si se arrastra el ratón con algún botón pulsado
        escena.setOnMouseDragged(evento -> {
            double limitePitch = 90;// Ángulo máximo de cabeceo
            // se obtiene la nueva posición del ratón
            ratonY = evento.getSceneY();
            ratonX = evento.getSceneX();
            // Cantidad de movimiento del ratón desde la anterior posición
            double movimientoRatonY = ratonY - ratonYVentanaAntes;
            double movimientoRatonX = ratonX - ratonXVentanaAntes;

            double factor = 0.1;// Factor por el que se multiplican los ángulos de giro
            if (evento.isShiftDown())
                factor = 0.3;

            if (evento.isPrimaryButtonDown()) {// Se está pulsando el botón izquierdo del ratón

                if (ratonY != ratonYVentanaAntes) {// Si hubo movimiento vertical del ratón
                    camara.setRotationAxis(Rotate.X_AXIS);
                    double pitch = -movimientoRatonY * factor;// Ángulo de cabeceo
                    //se limita el pitch
                    if (pitch > limitePitch)
                        pitch = limitePitch;
                    if (pitch < -limitePitch)
                        pitch = -limitePitch;
                    //se rota la cámara
                    camara.getTransforms().addAll(new Rotate(pitch, Rotate.X_AXIS));

                }
                if (ratonX != ratonXVentanaAntes) { // Hubo movimiento horizontal del ratón
                    camara.setRotationAxis(Rotate.Y_AXIS);
                    double yaw = movimientoRatonX * factor;
                    // Rota la cámara
                    camara.getTransforms().addAll(new Rotate(yaw, Rotate.Y_AXIS));
                }
            }
            // guardo la posición del ratón
            ratonXVentanaAntes = ratonX;
            ratonYVentanaAntes = ratonY;

        });

        //se hace click con el ratón
        escena.setOnMouseClicked((MouseEvent evento) -> {
            // se obtiene una referencia al objeto correspondiente
            Node captura = evento.getPickResult().getIntersectedNode();
            // Si es una referencia a un objeto de la clase Box
            if (captura instanceof Box) {
                Box cajaseleccionada = (Box) captura;
                boolean caja_encontrada=false;
                int pos_caja=0;
                while (! caja_encontrada){
                    //se encuentra el palet seleccionado
                    if (cajaseleccionada == grupo_contenedores.getChildren().get(pos_caja)){
                        click_contenedor.setText("Palet " + Palet.get(pos_caja).idPalet + " = " + Palet.get(pos_caja).cantidadProducto +
                                " L. " + Palet.get(pos_caja).idTipo + " " + Palet.get(pos_caja).idProducto);
                        caja_encontrada=true;
                    }
                    pos_caja++;
                    //se recorrieron todos los palets
                    if (pos_caja==Palet.size()){
                        click_contenedor.setText("");
                        caja_encontrada=true;
                    }

                }
            }else click_contenedor.setText("");
        });

        stage.setScene(escena); // Establece la escena que se muestra en la ventana
        stage.setWidth(1200); // Establece ancho inicial de la ventana
        stage.setHeight(700); // Establece alto inicial de la ventana
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
