package accessdb;

import com.healthmarketscience.jackcess.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
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
            IndexCursor cursor = CursorBuilder.createCursor(departamento.getPrimaryKeyIndex());//A los cursores hay que indicarles un índice para mostrar los datos en un orden, es como un ORDER BY
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
            
            System.out.println(id.getName()+"\t"+nombre.getName()+"\t"+puesto.getName()+"\t"+salario.getName()+"\t\t"+iddep.getName());
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
     * @param id El ID del departamento que se va a actualizar
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
    
    /**
     * Actualiza un empleado existente
     * @param id El id del empleado que se va a actualizar
     * @param nombre El nuevo nombre del empleado
     * @param puesto El nuevo puesto del empleado
     * @param salario El nuevo salario del empleado
     * @param iddep El nuevo ID del departamento al que pertenece
     */
    private void actualizarEmpleado(int id, String nombre, String puesto, BigDecimal salario, int iddep){
        try {
            Table empleado = db.getTable("Empleado");
            Row row = CursorBuilder.findRowByPrimaryKey(empleado, id);
            if(row!=null){
                row.put("Nombre", nombre);
                row.put("Puesto", puesto);
                row.put("Salario", salario);
                row.put("IdDep", iddep);
                empleado.updateRow(row);
                System.out.println("Empleado modificado correctamente");
            }else{
                System.out.println("No se ha encontrado un empleado con ese ID");
            }
        } catch (Exception e) {
            System.out.println("Error modificando el empleado");
            e.printStackTrace();
        }
    }
    
    /**
     * Elimina un departamento
     * @param id El ID del departamento que se va a eliminar
     */
    private void eliminarDepartamento(int id){
        try {
            Table departamento = db.getTable("Departamento");
            Row row = CursorBuilder.findRowByPrimaryKey(departamento, id);
            if(row!=null){
                departamento.deleteRow(row);
                System.out.println("Departamento eliminado correctamente");
            }else{
                System.out.println("No se ha encontrado un departamento con ese ID");
            }
        } catch (IOException e) {
            System.out.println("Error eliminando el departamento");
            e.printStackTrace();
        }
    }
    
    /**
     * Elimina un empleado
     * @param id El ID del empleado que se va a eliminar
     */
    private void eliminarEmpleado(int id){
        try {
            Table empleado = db.getTable("Empleado");
            Row row = CursorBuilder.findRowByPrimaryKey(empleado, id);
            if(row!=null){
                empleado.deleteRow(row);
                System.out.println("Empleado eliminado correctamente");
            }else{
                System.out.println("No se ha encontrado un empleado con ese ID");
            }
        } catch (IOException e) {
            System.out.println("Error eliminando el empleado");
            e.printStackTrace();
        }
    }
    
    /**
     * Lista a los empleados junto con el departamento al que pertenecen
     */
    private void listarEmpleadoDepartamento(){
        try {
            Table departamento = db.getTable("Departamento");
            Table empleado = db.getTable("Empleado");
            
            System.out.println("Empleado\tDepartamento");
            System.out.println("------------------------------");
            IndexCursor cursor = CursorBuilder.createCursor(empleado.getPrimaryKeyIndex());
            Cursor cursor2 = CursorBuilder.createCursor(departamento);
            for(Row row:cursor){
                Short iddep = (Short) row.get("IdDep");
                cursor2.findFirstRow(Collections.singletonMap("ID", iddep));
                String nombredep = (String) cursor2.getCurrentRowValue(departamento.getColumn("Nombre"));
                System.out.println(String.format("%s\t\t"+nombredep, row.get("Nombre")));
            }
        } catch (IOException e) {
            System.out.println("Error leyendo las dos tablas");
            e.printStackTrace();
        }
    }
    
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
            System.out.println("3. Eliminar un departamento existente");
            System.out.println("4. Visualizar contenido de la tabla 'Departamento'");
            System.out.println("5. Introducir un nuevo empleado");
            System.out.println("6. Modificar un empleado existente");
            System.out.println("7. Eliminar un empleado existente");
            System.out.println("8. Visualizar contenido de la tabla 'Empleado'");
            System.out.println("9. Visualizar empleados y su departamento");
            System.out.println("10. Salir");
            System.out.print("Introduzca una opción: ");
            opcion = Integer.parseInt(teclado.nextLine());
            
            switch(opcion){
                case 1:
                    int iddep;
                    String nombredep;
                    
                    System.out.print("Introduzca ID del departamento: ");
                    iddep = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca nombre del departamento: ");
                    nombredep = teclado.nextLine();
                    
                    access.insertarDepartamento(iddep, nombredep);
                    break;
                case 2:
                    int i;
                    String nombrenuevo;
                    
                    System.out.print("Introduzca ID del departamento a modificar: ");
                    i = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca el nuevo nombre del departamento:");
                    nombrenuevo = teclado.nextLine();
                    
                    access.actualizarDepartamento(i, nombrenuevo);
                    break;
                case 3:
                    int iddepb;
                    
                    System.out.print("Introduzca ID del departamento a eliminar: ");
                    iddepb = Integer.parseInt(teclado.nextLine());
                    
                    access.eliminarDepartamento(iddepb);
                    break;
                case 4:
                    access.listarDepartamentos();
                    break;
                case 5:
                    int idemp,iddep2;
                    String nombreemp, puestoemp;
                    BigDecimal salarioemp;
                    
                    System.out.print("Introduzca ID del empleado: ");
                    idemp = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca nombre del empleado: ");
                    nombreemp = teclado.nextLine();
                    System.out.print("Introduzca puesto del empleado: ");
                    puestoemp = teclado.nextLine();
                    System.out.print("Introduzca salario del empleado: ");
                    salarioemp = BigDecimal.valueOf(Double.parseDouble(teclado.nextLine()));
                    System.out.print("Introduzca ID del departamento al que pertenece: ");
                    iddep2 = Integer.parseInt(teclado.nextLine());
                    
                    access.insertaEmpleado(idemp, nombreemp, puestoemp, salarioemp, iddep2);
                    break;
                case 6:
                    int idemp2,iddep3;
                    String nombreemp2, puestoemp2;
                    BigDecimal salarioemp2;
                    
                    System.out.print("Introduzca del ID del empleado a modificar: ");
                    idemp2 = Integer.parseInt(teclado.nextLine());
                    System.out.print("Introduzca nuevo nombre del empleado: ");
                    nombreemp2 = teclado.nextLine();
                    System.out.print("Introduzca nuevo puesto del empleado: ");
                    puestoemp2 = teclado.nextLine();
                    System.out.print("Introduzca nuevo salario del empleado: ");
                    salarioemp2 = BigDecimal.valueOf(Double.parseDouble(teclado.nextLine()));
                    System.out.print("Introduzca nuevo ID del departamento al que pertenece: ");
                    iddep3 = Integer.parseInt(teclado.nextLine());
                    
                    access.actualizarEmpleado(idemp2, nombreemp2, puestoemp2, salarioemp2, iddep3);
                    break;
                case 7:
                    int idempb;
                    
                    System.out.print("Introduzca ID del empleado a eliminar: ");
                    idempb = Integer.parseInt(teclado.nextLine());
                    
                    access.eliminarEmpleado(idempb);
                    break;
                case 8:
                    access.listarEmpleados();
                    break;
                case 9:
                    access.listarEmpleadoDepartamento();
                    break;
                default:
                    access.cerrarConexion();
                    break;
            }
        } while (opcion<10);
    }
}