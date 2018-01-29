package accessdb;

import com.healthmarketscience.jackcess.*;
import java.io.File;
import java.io.IOException;

public class AccessDB {
    private Database db;

    public AccessDB() {
        try {
            File dbFile = new File("accessdb.accdb");
            if(dbFile.exists()){
                System.out.println("Conectando a la base de datos...");
                db = DatabaseBuilder.open(dbFile);
            }else{
                System.out.println("Creando base de datos...");
                db = DatabaseBuilder.create(Database.FileFormat.V2010, dbFile);
                System.out.println("Base de datos creada");
            }
        } catch (IOException e) {
            System.out.println("Error abriendo o creando la base de datos");
            e.printStackTrace();
        }
    }
    
    private void crearTablaDepartamento(){
        
    }
    
    private void cerrarConexion(){
        try {
            System.out.println("Cerrando conexión");
            db.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        AccessDB access = new AccessDB();
        access.cerrarConexion();
    }
}