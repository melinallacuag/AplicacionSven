package com.anggastudio.sample.WebApiSVEN.Controllers;
import com.anggastudio.sample.WebApiSVEN.Models.Anular;
import com.anggastudio.sample.WebApiSVEN.Models.CDia;
import com.anggastudio.sample.WebApiSVEN.Models.CTurno;
import com.anggastudio.sample.WebApiSVEN.Models.Company;
import com.anggastudio.sample.WebApiSVEN.Models.Correlativo;
import com.anggastudio.sample.WebApiSVEN.Models.Descuentos;
import com.anggastudio.sample.WebApiSVEN.Models.DetalleVenta;
import com.anggastudio.sample.WebApiSVEN.Models.LClientes;
import com.anggastudio.sample.WebApiSVEN.Models.Lados;
import com.anggastudio.sample.WebApiSVEN.Models.ListaComprobante;
import com.anggastudio.sample.WebApiSVEN.Models.Mangueras;
import com.anggastudio.sample.WebApiSVEN.Models.Optran;
import com.anggastudio.sample.WebApiSVEN.Models.Placa;
import com.anggastudio.sample.WebApiSVEN.Models.Reimpresion;
import com.anggastudio.sample.WebApiSVEN.Models.ReporteTarjetas;
import com.anggastudio.sample.WebApiSVEN.Models.Setting;
import com.anggastudio.sample.WebApiSVEN.Models.SettingTask;
import com.anggastudio.sample.WebApiSVEN.Models.Terminal;
import com.anggastudio.sample.WebApiSVEN.Models.TipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.Users;
import com.anggastudio.sample.WebApiSVEN.Models.VContometro;
import com.anggastudio.sample.WebApiSVEN.Models.VProducto;
import com.anggastudio.sample.WebApiSVEN.Models.VTipoPago;
import com.anggastudio.sample.WebApiSVEN.Models.VentaCA;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    /**
     * User
     */
    @GET("api/users/listado")
    Call<List<Users>> getUsers();

    @GET("api/users/listado/{id}")
    Call<List<Users>> findUsers(@Path("id") String id);


    /**
     * Company
     */
    @GET("api/company/listado")
    Call<List<Company>> getCompany();

    @GET("api/company/listado/{id}")
    Call<List<Company>> findCompany(@Path("id") Integer id);


    /**
     * Terminal
     */
    @GET("api/terminal/listado")
    Call<List<Terminal>> getTerminal();

    @GET("api/terminal/listado/{id}")
    Call<List<Terminal>> findTerminal(@Path("id") String id);


    /**
     * Lados
     */
    @GET("api/lados/listado")
    Call<List<Lados>> getLados();

    @GET("api/lados/listado/{id}")
    Call<List<Lados>> findLados(@Path("id") String id);


    /**
     * Manguera
     */
    @GET("api/picos/listado")
    Call<List<Mangueras>> getMangueras();

    @GET("api/picos/listado2/{id}")
    Call<List<Mangueras>> findManguerasByTerminal(@Path("id") String id);

    @POST("api/picos/guardar")
    Call<Mangueras> postMangueras(@Body Mangueras mangueras);


    /**
     * Cliente RUC - DNI
     */
    @GET("api/cliente/listado")
    Call<List<LClientes>> getCliente();

    @GET("api/cliente/listado/{id}")
    Call<List<LClientes>> findCliente(@Path("id") String id);

    @GET("api/cliente/listado/DNI")
    Call<List<LClientes>> getClienteDNI();

    @GET("api/cliente/listado/DNI/{id}")
    Call<List<LClientes>> findClienteDNI(@Path("id") String id);

    @GET("api/cliente/listado/RUC")
    Call<List<LClientes>> getClienteRUC();

    @GET("api/cliente/listado/RUC/{id}")
    Call<List<LClientes>> findClienteRUC(@Path("id") String id);


    /**
     * Detalle de la Venta
     */
    @GET("api/detalleventa/listado/{id}")
    Call<List<DetalleVenta>> findDetalleVenta(@Path("id") String id);


    /**
     * VentaCA
     */
    @POST("api/ventaca/guardar")
    Call<VentaCA> postVentaCA(@Body VentaCA ventaCA);


    /**
     * Optran
     */
    @GET("api/optran/listado/{id}")
    Call<List<Optran>> findOptran(@Path("id") String id);


    /**
     * Tipo de Pago - Spinner Tarjeta
     */
    @GET("api/card/listado")
    Call<List<TipoPago>> getTipoPago();

    @GET("api/card/listado/{id}")
    Call<List<TipoPago>> findTipoPago(@Path("id") Integer id);

    /**
     * Placa
     */
    @GET("api/placa/listado")
    Call<List<Placa>> getPlaca();

    @GET("api/placa/listado/{id}")
    Call<List<Placa>> findPlaca(@Path("id") String id);


    /**
     * Configuración
     */
    @GET("api/setting/listado")
    Call<List<Setting>> getSetting();

    @GET("api/setting/listado/{id}")
    Call<List<Setting>> findSetting(@Path("id") Integer id);


    /**
     * Ajustes
     */
    @GET("api/settingTask/listado")
    Call<List<SettingTask>> getSettingTask();

    @GET("api/settingTask/listado/{id}")
    Call<List<SettingTask>> findSettingTask(@Path("id") String id);


    /**
     * Correlativo
     */
    @GET("api/correlativo/listado")
    Call<List<Correlativo>> getCorrelativo();

    @GET("api/correlativo/listado/{id}/{tipo}")
    Call<List<Correlativo>> findCorrelativo(@Path("id") String id, @Path("tipo") String tipo);


    /**
     * Venta por Contometro
     */

    @GET("api/rcontom/listado")
    Call<List<VContometro>> getVContometro();

    @GET("api/rcontom/listado/{id}")
    Call<List<VContometro>> findVContometro(@Path("id") String id);


    /**
     * Venta por Producto
     */

    @GET("api/rproducto/listado")
    Call<List<VProducto>> getVProducto();

    @GET("api/rproducto/listado/{id}/{turno}")
    Call<List<VProducto>> findVProducto(@Path("id") String id,@Path("turno") Integer turno);


    /**
     * Venta por Pago
     */

    @GET("api/rpago/listado")
    Call<List<VTipoPago>> getVTipoPago();

    @GET("api/rpago/listado/{id}/{turno}")
    Call<List<VTipoPago>> findVTipoPago(@Path("id") String id,@Path("turno") Integer turno);

    /**
     * Incio de Día
     */
    @POST("api/cdia/iniciar/{id}")
    Call<CDia> postCDia(@Path("id") String id);


    /**
     * Cambio de Turno
     */
    @POST("api/cturno/cerrar/{id}")
    Call<CTurno> postCTurno(@Path("id") String id);


    /**
     * Descuentos
     */
    @GET("api/descuentos/listado/{id}")
    Call<List<Descuentos>> findDescuentos(@Path("id") String id);


    /**
     * Consultar Venta
     */
    @GET("api/consultarventa/listado/{id}")
    Call<List<ListaComprobante>> findConsultarVenta(@Path("id") String id);


    /**
     * Anular
     */
    @POST("api/anular/anular/{tipodoc}/{seriedoc}/{nrodoc}/{anuladoid}")
    Call<Anular> postAnular(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc, @Path("anuladoid") String anuladoid);


    /**
     * Reimprimir
     */
    @GET("api/reimpresion/listado/{tipodoc}/{seriedoc}/{nrodoc}")
    Call<List<Reimpresion>> findReimpresion(@Path("tipodoc") String tipodoc, @Path("seriedoc") String seriedoc, @Path("nrodoc") String nrodoc);


    /**
     * Reporte por Tarjeta
     */

    @GET("api/rtarjeta/listado/{id}/{turno}")
    Call<List<ReporteTarjetas>> findRTarjetas(@Path("id") String id, @Path("turno") Integer turno);

}
