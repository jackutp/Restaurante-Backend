package Entities;

public class DetallePedido {
    int detallePedidoid;
    int pedidoid;
    int platilloid;
    int cantidad;
    String nota;
    public enum estado{
        EMITIDO, DESPACHAR, COMPLETADO, CANCELADO
    }
}
