/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api.service;

import api.Producto;
import api.ProductoDAO;
import api.entity.ProductoEntity;
import conection.ConexionMySQL;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import validation.Validation;

@javax.ejb.Stateless
@Path("api.producto")
public class ProductoFacadeREST extends AbstractFacade<Producto> {

    @PersistenceContext(unitName = "apiMascotasExamenPU")
    private EntityManager em;
    Validation v = new Validation();
    ConexionMySQL con;
    ProductoDAO dao;
    JSONObject exc;
    JSONParser parser;
    JSONObject json;

    public ProductoFacadeREST() throws SQLException {
        super(Producto.class);
        this.em = Persistence.createEntityManagerFactory("apiMascotasExamenPU").createEntityManager();
        init();
    }

    public final void init() throws SQLException {
        con = new ConexionMySQL();
        con.getConnection();
        dao = new ProductoDAO(con);
        exc = new JSONObject();
        parser = new JSONParser();
        json = new JSONObject();
    }

    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Integer id, String obj) {
        try {
            json = (JSONObject) parser.parse(obj);
            String nombre = json.get("nombre").toString();
            String descripcion = json.get("descripcion").toString();
            String url = json.get("url").toString();
            double precio = Double.valueOf(json.get("precio").toString());
            int cant = Integer.valueOf(json.get("cantinventario").toString());
            if (v.evaluarInputNumber(String.valueOf(id))) {
                if (v.evaluarInputNumber(String.valueOf(cant))) {
                    if (v.evaluarInputText(nombre)) {
                        if (v.evaluarInputText(descripcion)) {
                            if (v.evaluarInputUrl(url)) {
                                if (dao.getInventario(id) != null) {
                                    dao.modificar(id, nombre, descripcion, url, precio, cant);
                                    em.getEntityManagerFactory().getCache().evictAll();
                                    return Response.status(Response.Status.ACCEPTED).entity(super.find(id)).build();
                                } else {
                                    return Response.status(Response.Status.NOT_FOUND).build();
                                }
                            } else {
                                exc.put("Información", "Url inválido");
                                exc.put("Formato requerido", "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
                                exc.put("Ejemplo", "htttp://google.com");
                                return Response.status(422).entity(exc.toJSONString()).build();
                            }
                        } else {
                            exc.put("Información", "Descripcion inválida");
                            exc.put("Formato requerido", "[a-zA-Z ]{2,254}");
                            exc.put("Explicacion", "Solo se admite texto de entre 2 a 254 caractéres");
                            return Response.status(422).entity(exc.toJSONString()).build();
                        }
                    } else {
                        exc.put("Información", "Nombre inválido");
                        exc.put("Formato requerido", "[a-zA-Z ]{2,254}");
                        exc.put("Explicacion", "Solo se admite texto de entre 2 a 254 caractéres");
                        return Response.status(422).entity(exc.toJSONString()).build();
                    }
                } else {
                    exc.put("Información", "Cantidad inválida");
                    exc.put("Formato requerido", "^[0-9.-]+$");
                    exc.put("Explicacion", "Solo se admiten números");
                    return Response.status(422).entity(exc.toJSONString()).build();
                }

            } else {
                exc.put("Información", "Id inválido");
                exc.put("Formato requerido", "^[0-9.-]+$");
                exc.put("Explicacion", "Solo se admiten números");
                return Response.status(422).entity(exc.toJSONString()).build();
            }

        } catch (NumberFormatException | ParseException e) {
            exc.put("Información", "Revise las posibles razones:");
            exc.put("Razón 1: ", "Los campos cantinventario, id o el precio contienen caractéres diferentes a los numéricos");
            exc.put("Razón 2: ", "Hay digitos fuera del formato JSON");
            return Response.status(422).entity(exc.toJSONString()).build();
        }
    }

    @PATCH
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response add(@PathParam("id") Integer id, String obj) {
        try {
            json = (JSONObject) parser.parse(obj);
            String operation = json.get("operation").toString();
            int cantidad = Integer.valueOf(json.get("cantidad").toString());
            if (v.evaluarInputNumber(String.valueOf(cantidad))
                    && v.evaluarInputText(operation) && operation.equals("restar")
                    || operation.equals("agregar")
                    || operation.equals("modificar")) {
                ProductoEntity pe = dao.getInventario(id);
                if (dao.getInventario(id) != null) {
                    switch (operation) {
                        case "agregar":
                            dao.modificarInv(id, cantidad + pe.getCantinventario());
                            break;
                        case "modificar":
                            dao.modificarInv(id, cantidad);
                            break;
                        case "restar":
                            dao.modificarInv(id, pe.getCantinventario() - cantidad);
                            break;
                        default:
                            break;
                    }
                    em.getEntityManagerFactory().getCache().evictAll();
                    return Response.status(Response.Status.ACCEPTED).entity(super.find(id)).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }

            } else {
                exc.put("Información", "La palabra: " + "'" + operation + "'" + " no tiene el formato correcto(restar,agregar o modificar)");
                return Response.status(422).entity(exc.toJSONString()).build();
            }
        } catch (NumberFormatException | ParseException e) {
            exc.put("Información", "Revise las posibles razones:");
            exc.put("Razón 1: ", "El campo cantidad posee otros caractéres que no son numéricos");
            exc.put("Razón 2: ", "Hay digitos fuera del formato JSON");
            return Response.status(422).entity(exc.toJSONString()).build();
        }
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response test(String obj) throws SQLException {
        try {
            json = (JSONObject) parser.parse(obj);
            int id = Integer.valueOf(json.get("idproducto").toString());
            String nombre = json.get("nombre").toString();
            String descripcion = json.get("descripcion").toString();
            String url = json.get("url").toString();
            double precio = Double.valueOf(json.get("precio").toString());
            int cant = Integer.valueOf(json.get("cantinventario").toString());
            if (v.evaluarInputNumber(String.valueOf(id))) {
                if (v.evaluarInputNumber(String.valueOf(cant))) {
                    if (v.evaluarInputText(nombre)) {
                        if (v.evaluarInputText(descripcion)) {
                            if (v.evaluarInputUrl(url)) {
                                ProductoEntity pe = new ProductoEntity(id, nombre, descripcion, url, precio, cant);
                                dao.insertar(pe);
                                em.getEntityManagerFactory().getCache().evictAll();
                                return Response.status(Response.Status.ACCEPTED).entity(super.find(id)).build();
                            } else {
                                exc.put("Información", "Url inválido");
                                exc.put("Formato requerido", "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
                                exc.put("Ejemplo", "htttp://google.com");
                                return Response.status(422).entity(exc.toJSONString()).build();
                            }
                        } else {
                            exc.put("Información", "Descripcion inválida");
                            exc.put("Formato requerido", "[a-zA-Z ]{2,254}");
                            exc.put("Explicacion", "Solo se admite texto de entre 2 a 254 caractéres");
                            return Response.status(422).entity(exc.toJSONString()).build();
                        }
                    } else {
                        exc.put("Información", "Nombre inválido");
                        exc.put("Formato requerido", "[a-zA-Z ]{2,254}");
                        exc.put("Explicacion", "Solo se admite texto de entre 2 a 254 caractéres");
                        return Response.status(422).entity(exc.toJSONString()).build();
                    }
                } else {
                    exc.put("Información", "Cantidad inválida");
                    exc.put("Formato requerido", "^[0-9.-]+$");
                    exc.put("Explicacion", "Solo se admiten números");
                    return Response.status(422).entity(exc.toJSONString()).build();
                }

            } else {
                exc.put("Información", "Id inválido");
                exc.put("Formato requerido", "^[0-9.-]+$");
                exc.put("Explicacion", "Solo se admiten números");
                return Response.status(422).entity(exc.toJSONString()).build();
            }

        } catch (NumberFormatException | ParseException | NullPointerException e) {
            exc.put("Información", "Revise las posibles razones:");
            exc.put("Razón 1: ", "Los campos cantinventario, id o el precio contienen caractéres diferentes a los numéricos");
            exc.put("Razón 2: ", "Hay digitos fuera del formato JSON");
            return Response.status(422).entity(exc.toJSONString()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") Integer id) {
        em.getEntityManagerFactory().getCache().evictAll();
        if (super.find(id) == null) {
            return Response.status(404).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(super.find(id)).build();

    }

    @Transactional
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Producto> getAll() throws IOException {
        em.getEntityManagerFactory().getCache().evictAll();
        return super.findAll();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
