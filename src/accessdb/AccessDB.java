package accessdb;

import com.healthmarketscience.jackcess.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
                crearTablaEmpleado();
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
                    .addIndex(new IndexBuilder("NombreDIndex")//También se utilizan para agilizar las consultas
                        .addColumns("Nombre")).toTable(db);
            System.out.println("Tabla Departamento creada");
        } catch (IOException ex){
            System.out.println("Error creando la tabla Departamento");
            ex.printStackTrace();
        }
    }
    
    /**
     * Crea la tabla "Empleado" con los campos "ID", "Nombre", "Puesto", "Salario" e "IdDep"
     */
    private void crearTablaEmpleado(){
        try {
            System.out.println("Creando tabla Empleado...");
            Table empleado = new TableBuilder("Empleado")
                    .addColumn(new ColumnBuilder("ID",DataType.INT))
                    .addColumn(new ColumnBuilder("Nombre",DataType.TEXT))
                    .addColumn(new ColumnBuilder("Puesto",DataType.TEXT))
                    .addColumn(new ColumnBuilder("Salario",DataType.MONEY))
                    .addColumn(new ColumnBuilder("IdDep",DataType.INT))
                    .addIndex(new IndexBuilder(IndexBuilder.PRIMARY_KEY_NAME)
                        .addColumns("ID").setPrimaryKey())
                    .addIndex(new IndexBuilder("NombreEIndex")
                        .addColumns("Nombre"))
                    .addIndex(new IndexBuilder("PuestoIndex")
                        .addColumns("Puesto"))
                    .addIndex(new IndexBuilder("SalarioIndex")
                        .addColumns("Salario"))
                    .addIndex(new IndexBuilder("IdDepIndex")
                        .addColumns("IdDep")).toTable(db);
            
            Table departamento = db.getTable("Departamento");
            Relationship rel = new RelationshipBuilder(departamento, empleado)//Esta es la manera de relacionar dos tablas
                    .addColumns(departamento.getColumn("ID"), empleado.getColumn("IdDep"))//Se indica que campos de cada tabla se van a relacionar
                    .setReferentialIntegrity()//Con este método se establece la conexión entre los dos campos, es necesario ponerlo si queremos actualizar y borrar en cascada
                    .setCascadeUpdates()//Se indica que se actualice en cascada
                    .setCascadeDeletes()//Se indica que se borre en cascada
                    .toRelationship(db);
        } catch (IOException e) {
            System.out.println("Error creando la tabla Empleados");
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
     * Inserta un nuevo empleado en la tabla "Empleado"
     * @param id Define el ID del empleado
     * @param nombre Define el nombre del empleado
     * @param puesto Define el puesto del empleado
     * @param salario Define el salario del empleado
     * @param iddep Define el id del departamento al que pertenece
     */
    private void insertaEmpleado(int id, String nombre, String puesto, BigDecimal salario, int iddep){
        try {
            Table empleado = db.getTable("Empleado");
            empleado.addRow(id, nombre, puesto, salario, iddep);
            System.out.println("Empleado insertado");
        } catch (IOException e) {
            System.out.println("Error en la creación del empleado");
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
                System.out.println(String.format("%d\t%s", row.get("ID"), row.get("Nombre")));
            }
        } catch (IOException e) {
            System.out.println("Error leyendo la tabla 'Departamento'");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra el contenido de la tabla "Empleado"
     */
    private void listarEmpleados(){
        try {
            Table empleado = db.getTable("Empleado");
            Column id = empleado.getColumn("ID");
            Column nombre = empleado.getColumn("Nombre");
            Column puesto = empleado.getColumn("Puesto");
            Column salario = empleado.getColumn("Salario");
            Column iddep = empleado.getColumn("IdDep");
            
            System.out.println(id.getName()+"\t"+nombre.getName()+"\t"+puesto.getName()+"\t"+salario.getName()+"\t"+iddep.getName());
            System.out.println("-----------------------------------------------------------");
            IndexCursor cursor = CursorBuilder.createCursor(empleado.getPrimaryKeyIndex());
            for(Row row:cursor){
                System.out.println(String.format("%d\t%s\t%s\t%f\t%d", row.get("ID"), row.get("Nombre"), row.get("Puesto"), row.get("Salario"), row.get("IdDep")));
            }
        } catch (IOException e) {
            System.out.println("Error leyendo la tabla 'Empleado'");
            e.printStackTrace();
        }
    }
    
    /**
     * Actualiza un departamento existente
     * @param id El departamento que se va a actualizar
     * @param nombre El nuevo nombre del departamento
     */
    private void actualizarDepartamento(int id, String nombre){
        try {
            Table departamento = db.getTable("Departamento");
            Row row = CursorBuilder.findRowByPrimaryKey(departamento, id);
            if(row!=null){
                row.put("Nombre", nombre);
                departamento.updateRow(row);
                System.out.println("Departamento modificado correctamente");
            }else{
                System.out.println("No se ha encontrado un departamento con ese ID");
            }
        } catch (IOException e) {
            System.out.println("Error modificando el departamento");
            e.printStackTrace();
        }
    }
    
    private void actualizarEmpleado(int id, String nombre, String puesto, BigDecimal salario, int iddep){
        try {
            
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
            System.out.println("\n\n1. Introducir un nuevo departamento");
            System.out.println("2. Modificar un departamento existente");
            System.out.println("3. Visualizar contenido de la tabla 'Departamento'");
            System.out.println("4. Introducir un nuevo empleado");
            System.out.println("5. Visualizar contenido de la tabla 'Empleado'");
            System.out.println("6. Salir");
            System.out.print("Introduzca una opción:");
            opcion = Integer.parseInt(teclado.nextLine());
            
            switch(opcion){
                case 1:
                    int iddep;
                    String nombredep;
                    
                    System.out.print("Introduzca ID del departamento:");
                    iddep = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca nombre del departamento:");
                    nombredep = teclado.nextLine();
                    
                    access.insertarDepartamento(iddep, nombredep);
                    break;
                case 2:
                    int i;
                    String nombrenuevo;
                    
                    System.out.print("Introduzca ID del departamento a modificar:");
                    i = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca el nuevo nombre del departamento:");
                    nombrenuevo = teclado.nextLine();
                    
                    access.actualizarDepartamento(i, nombrenuevo);
                    break;
                case 3:
                    access.listarDepartamentos();
                    break;
                case 4:
                    int idemp,iddep2;
                    String nombreemp, puestoemp;
                    BigDecimal salarioemp;
                    
                    System.out.print("Introduzca ID del empleado:");
                    idemp = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca nombre del empleado:");
                    nombreemp = teclado.nextLine();
                    System.out.print("Introduzca puesto del empleado:");
                    puestoemp = teclado.nextLine();
                    System.out.print("Introduzca salario del empleado:");
                    salarioemp = BigDecimal.valueOf(Double.parseDouble(teclado.nextLine()));
                    System.out.print("Introduzca ID del departamento al que pertenece:");
                    iddep2 = Integer.parseInt(teclado.nextLine());
                    
                    access.insertaEmpleado(idemp, nombreemp, puestoemp, salarioemp, iddep2);
                    break;
                case 5:
                    access.listarEmpleados();
                    break;
                default:
                    System.out.println("Adios");
                    break;
            }
        } while (opcion<6);
        access.cerrarConexion();
    }
}