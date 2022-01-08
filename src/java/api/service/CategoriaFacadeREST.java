/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api.service;

import api.Categoria;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@javax.ejb.Stateless
@Path("api.categoria")
public class CategoriaFacadeREST extends AbstractFacade<Categoria> {

    @PersistenceContext(unitName = "apiMascotasExamenPU")
    private EntityManager em;

    public CategoriaFacadeREST() throws SQLException {
        super(Categoria.class);
        this.em = Persistence.createEntityManagerFactory("apiMascotasExamenPU").createEntityManager();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response find(@PathParam("id") Integer id) throws IOException, IndexOutOfBoundsException {
            em.getEntityManagerFactory().getCache().evictAll();
        if (super.find(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(super.find(id)).build();

    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Categoria> getAll() throws IOException {
            em.getEntityManagerFactory().getCache().evictAll();
        return super.findAll();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
