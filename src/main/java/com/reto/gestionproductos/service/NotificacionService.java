package com.reto.gestionproductos.service;

import com.reto.gestionproductos.model.Producto;

/**
 * Servicio de notificaciones (por ejemplo, correos electrónicos, SMS o webhooks).
 * Se simula con Mockito (@Mock) para evitar enviar alertas reales durante las pruebas unitarias.
 */
public interface NotificacionService {

    void notificarRegistroProducto(Producto producto);

    void enviarAlertaStockBajo(String codigoProducto, int stockRestante);
}
