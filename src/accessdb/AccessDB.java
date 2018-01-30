package accessdb;

import com.healthmarketscience.jackcess.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AccessDB {
    private Database db;

    /**
     * Constructor de la clase, se crea la base de datos si no existe,
     * si existe simplemente la abre
     */
    public AccessDB() {
        try {
            File dbFile = new File("accessdb.accdb");
            if(dbFile.exists()){
                System.out.println("Abriendo la base de datos...");
                db = DatabaseBuilder.open(dbFile);
            }else{
                System.out.println("Creando base de datos...");
                db = DatabaseBuilder.create(Database.FileFormat.V2010, dbFile);//La creación de una base de datos lleva implícita la conexión
                System.out.println("Base de datos creada");
                crearTablaDepartamento();
            }
        } catch (IOException e) {
            System.out.println("Error abriendo o creando la base de datos");
            e.printStackTrace();
        }
    }
    
    /**
     * Crea la tabla "Departamento" con dos columnas, "ID" y "Nombre"
     */
    private void crearTablaDepartamento(){
        try {
            System.out.println("Creando tabla Departamento...");
            Table departamento = new TableBuilder("Departamento")
                    .addColumn(new ColumnBuilder("ID",DataType.INT))
                    .addColumn(new ColumnBuilder("Nombre",DataType.TEXT))
                    .addIndex(new IndexBuilder(IndexBuilder.PRIMARY_KEY_NAME)//Hay que utilizar índices para definir claves primarias
                        .addColumns("ID").setPrimaryKey())
                    .addIndex(new IndexBuilder("NombreIndex")//También se utilizan para agilizar las consultas
                        .addColumns("Nombre")).toTable(db);
            System.out.println("Tabla Departamento creada");
        } catch (IOException ex){
            System.out.println("Error accediendo a la base de datos");
            ex.printStackTrace();
        }
    }
    
    /**
     * Inserta un nuevo departamento en la tabla "Departamento"
     * @param id Define el ID del departamento
     * @param nombre Define el nombre del departamento
     */
    private void insertarDepartamento(int id, String nombre){
        try {
            Table departamento = db.getTable("Departamento");
            departamento.addRow(id, nombre);
            System.out.println("Departamento insertado");
        } catch (IOException e) {
            System.out.println("Error en la creación del departamento");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra el contenido de la tabla "Departamento"
     */
    private void listarDepartamentos(){
        try {
            Table departamento = db.getTable("Departamento");
            Column id = departamento.getColumn("ID");
            Column nombre = departamento.getColumn("Nombre");
            
            System.out.println(id.getName()+"\t"+nombre.getName());
            System.out.println("-----------------------------");
            IndexCursor cursor = CursorBuilder.createCursor(departamento.getPrimaryKeyIndex());
            for(Row row:cursor){
                System.out.println(String.format("%d\t'%s'", row.get("ID"), row.get("Nombre")));
            }
        } catch (Exception e) {
        }
    }
    
    /**
     * Reinicia la base de datos
     */
//    private void reiniciarBD(){
//        cerrarConexion();
//        File dbFile = new File(db.getFile().getName());
//        if(dbFile.exists()){
//            if(dbFile.delete())
//                System.out.println("Base de datos borrada");
//        }
//    }
    
    /**
     * Cierra la conexión de la base de datos.
     */
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
        Scanner teclado = new Scanner(System.in);
        int opcion;
        
        do {            
            System.out.println("\n\n1. Introduzca un nuevo departamento");
            System.out.println("2. Visualizar contenido de la tabla departamento");
            System.out.println("3. Reiniciar base de datos");
            System.out.println("4. Salir");
            System.out.print("Introduzca una opción:");
            opcion = Integer.parseInt(teclado.nextLine());
            
            switch(opcion){
                case 1:
                    int id;
                    String nombre;
                    
                    System.out.print("Introduzca ID del departamento:");
                    id = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca nombre del departamento:");
                    nombre = teclado.nextLine();
                    
                    access.insertarDepartamento(id, nombre);
                    break;
                case 2:
                    access.listarDepartamentos();
                    break;
                case 3:
                    //access.reiniciarBD();
                    break;
                default:
                    System.out.println("Adios");
                    break;
            }
        } while (opcion<4);
        access.cerrarConexion();
    }
}