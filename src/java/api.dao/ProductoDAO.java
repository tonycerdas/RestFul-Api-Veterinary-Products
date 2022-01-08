/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import api.entity.ProductoEntity;
import conection.ConexionMySQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private ConexionMySQL c;

    public ProductoDAO(ConexionMySQL conn) throws SQLException {
        c = conn;
    }
    final String INSERT = "INSERT INTO producto(idproducto,nombre,descripcion,url,precio,cantinventario) values(?,?,?,?,?,?)";
    final String UPDATE = "UPDATE producto SET nombre = ?,descripcion = ?,url = ?,precio = ?,cantinventario = ? WHERE idproducto = ?";
    final String SELECTINV = "SELECT * FROM PRODUCTO WHERE IDPRODUCTO = ?";
    final String UPDATEINV = "UPDATE producto SET cantinventario = ? WHERE idproducto = ?";
    final String GETALL = "SELECT idproducto,nombre,descripcion,url,precio,cantinventario FROM producto  order by idproducto";

    public List<ProductoEntity> getAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<ProductoEntity> listaProductos = new ArrayList<>();
        try {
            st = c.getConnection().prepareStatement(GETALL);
            rs = st.executeQuery();
            while (rs.next()) {
                listaProductos.add(convertir(rs));
            }
        } catch (SQLException e) {
            System.out.println("Excepcion 1: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Excepcion 2:" + e.getMessage());
                }
            }
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.out.println("Excepcion 3:" + e.getMessage());
                }
            }
        }
        return listaProductos;
    }
    public boolean insertar(ProductoEntity am) {
        PreparedStatement st = null;
        boolean value = false;
        try {
            st = c.getConnection().prepareStatement(INSERT);
            st.setInt(1, am.getIdproducto());
            st.setString(2, am.getNombre());
            st.setString(3, am.getDescripcion());
            st.setString(4, am.getUrl());
            st.setDouble(5, am.getPrecio());
            st.setInt(6, am.getCantinventario());
            st.executeUpdate();
            value = true;
        } catch (SQLException e) {
            System.out.println("Excepcion 1: " + e.getMessage());
            value = false;
        } finally {
            if (st != null) {
                try {
                    st.close();

                } catch (SQLException e) {
                    System.out.println("Excepcion 2: " + e.getMessage());
                }
            }
        }
        return value;
    }

    public boolean modificar(Integer idproducto, String nombre, String descripcion, String url, Double precio, Integer cantinventario) {
        PreparedStatement st = null;
        boolean value = false;
        try {
            if (this.getInventario(idproducto) != null) {
                st = c.getConnection().prepareStatement(UPDATE);
                st.setString(1, nombre);
                st.setString(2, descripcion);
                st.setString(3, url);
                st.setDouble(4, precio);
                st.setInt(5, cantinventario);
                st.setInt(6, idproducto);
                st.executeUpdate();
                c.getConnection().commit();
                value = true;
            }
            else{
                value = false;
            }
        } catch (SQLException e) {
            System.out.println("Excepcion 1: " + e.getMessage());
            value = false;
            System.out.println("here false");
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.out.println("Excepcion 2: " + e.getMessage());
                }
            }
        }
        return value;
    }

    public void modificarInv(Integer idproducto, Integer cantinventario) {
        PreparedStatement st = null;
        try {
            st = c.getConnection().prepareStatement(UPDATEINV);
            st.setInt(1, cantinventario);
            st.setInt(2, idproducto);
            st.executeUpdate();
            c.getConnection().commit();
        } catch (SQLException e) {
            System.out.println("Excepcion 1: " + e.getMessage());
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    System.out.println("Excepcion 2: " + e.getMessage());
                }
            }
        }
    }

    public ProductoEntity getInventario(int idproducto) {
        PreparedStatement st = null;
        ResultSet rs = null;
        ProductoEntity nm = null;
        try {
            st = c.getConnection().prepareStatement(SELECTINV);
            st.setInt(1, idproducto);
            rs = st.executeQuery();
            if (rs.next()) {
                System.out.println("got them");
                nm = convertir(rs);
            } else {
                System.out.println("Registro no encontrado");
            }
        } catch (SQLException e) {
            System.out.println("Excepcion get: " + e.getMessage());
        }
        return nm;
    }
//

    private ProductoEntity convertir(ResultSet rs) throws SQLException {
        System.out.println("entró al convertidor");
        int idproducto = Integer.valueOf(rs.getString("idproducto"));
        String nombre = rs.getString("nombre");
        String descripcion = rs.getString("descripcion");
        String url = rs.getString("url");
        double precio = Double.valueOf(rs.getString("precio"));
        int cant = Integer.valueOf(rs.getString("cantinventario"));

        ProductoEntity am = new ProductoEntity(idproducto, nombre, descripcion, url, precio, cant);
        return am;
    }

}
