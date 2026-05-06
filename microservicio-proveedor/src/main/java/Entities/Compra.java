package Entities;

import java.sql.Date;

public class Compra {
    int compraid;
    int proveedorid;
    int comprobanteCompraid;
    float monto;
    Date fechaCompra;
    public enum estadoCompra{
        RECIBIDO, PENDIENTE, CANCELADO, DEVUELTO;
    }

}
