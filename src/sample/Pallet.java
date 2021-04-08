package sample;

public class Pallet {
    public int num_estanteria, altura_balda,  cantidadProducto, idPalet,  posicion;
    public double alto, ancho, largo,color_r, color_g,color_b;
    public String idProducto, idTipo;
    public boolean delante;
    public Pallet(String numero,String altura,String altoo,String anch,String cantidad,
                   String delant, String idpal,String idprod,String larg,String pos,String tipo, String color){
        num_estanteria = Integer.parseInt(numero);
        altura_balda = Integer.parseInt(altura);
        alto = Double.parseDouble(altoo)/1000;
        ancho = Double.parseDouble(anch)/1000;
        cantidadProducto = Integer.parseInt(cantidad);
        delante = Boolean.parseBoolean(delant);
        idPalet = Integer.parseInt(idpal);
        idProducto = idprod;
        largo = Double.parseDouble(larg)/1000;
        posicion = Integer.parseInt(pos);
        idTipo= tipo;
        String[] partes_color = color.split(",");
        color_r=Double.parseDouble(partes_color[0]);
        color_g=Double.parseDouble(partes_color[1]);
        color_b=Double.parseDouble(partes_color[2]);
    }
void visualiza(){
        System.out.println(String.valueOf(posicion));
}
}
