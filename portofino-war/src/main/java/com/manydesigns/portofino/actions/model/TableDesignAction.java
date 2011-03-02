/*
 * Copyright (C) 2005-2010 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. View the full text of the
 * exception in file OPEN-SOURCE-LICENSE.txt in the directory of this
 * software distribution.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307  USA
 *
 */

package com.manydesigns.portofino.actions.model;

import com.manydesigns.elements.Mode;
import com.manydesigns.elements.annotations.AnnotationsManager;
import com.manydesigns.elements.forms.Form;
import com.manydesigns.elements.forms.FormBuilder;
import com.manydesigns.elements.forms.TableForm;
import com.manydesigns.elements.forms.TableFormBuilder;
import com.manydesigns.elements.messages.SessionMessages;
import com.manydesigns.elements.options.DefaultSelectionProvider;
import com.manydesigns.elements.options.SelectionProvider;
import com.manydesigns.elements.reflection.ClassAccessor;
import com.manydesigns.elements.reflection.PropertiesAccessor;
import com.manydesigns.elements.text.OgnlTextFormat;
import com.manydesigns.portofino.actions.PortofinoAction;
import com.manydesigns.portofino.context.ModelObjectNotFoundError;
import com.manydesigns.portofino.database.Type;
import com.manydesigns.portofino.model.datamodel.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class TableDesignAction extends PortofinoAction
        implements ServletRequestAware{
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    //**************************************************************************
    // Implementazione ServletRequestAware
    //**************************************************************************
    private HttpServletRequest req;
    public void setServletRequest(HttpServletRequest request) {
        req = request;
    }

    //**************************************************************************
    // STEP del wizard
    //**************************************************************************
    private static final int ANNOTATION_STEP = 4;
    private static final int COLUMN_STEP = 2;
    private static final int TABLE_STEP = 1;
    private static final int PRIMARYKEY_STEP = 3;

    //**************************************************************************
    // Web parameters
    //**************************************************************************
    public InputStream inputStream;

    public String qualifiedTableName;
    public String cancelReturnUrl;
    public Table table;
    public final List<String> annotations;
    public final List<String> annotationsImpl;
    public List<AnnModel> colAnnotations;
    public PrimaryKeyModel pkModel;
    public String pk_primaryKeyName;
    public String pk_column;
    public String colAnn_annotationName;

    public List<String> columnNames;

    //Contatori righe TableForm
    public Integer ncol;
    public Integer npkcol;
    public Integer nAnnotations;

    //righe da rimuovere
    public String[] cols_selection;
    public String[] pkCols_selection;
    public String[] colAnnT_selection;

    //testo parziale per autocomplete
    public String term;

    //Step
    public Integer step;


    //**************************************************************************
    // Web parameters setters (for struts.xml inspections in IntelliJ)
    //**************************************************************************
    public void setQualifiedTableName(String qualifiedTableName) {
        this.qualifiedTableName = qualifiedTableName;
    }


    //**************************************************************************
    // Forms
    //**************************************************************************
    public Form tableForm;
    public Form columnForm;
    public Form pkForm;
    public Form pkColumnForm;
    //public Form colAnnotationForm;
    public Form annForm;
    public Form annPropForm;

    public TableForm columnTableForm;
    public TableForm pkColumnTableForm;
    public TableForm colAnnotationTableForm;


    //**************************************************************************
    // Other objects
    //**************************************************************************

    public static final Logger logger =
            LoggerFactory.getLogger(TableDesignAction.class);


    //**************************************************************************
    // WebParameters
    //**************************************************************************

    public String table_databaseName;
    public String table_schemaName;
    public String table_tableName;




    //**************************************************************************
    // Constructor
    //**************************************************************************
    public TableDesignAction() {
        annotations = new ArrayList<String>();
        annotationsImpl = new ArrayList<String>();
        Set<Class> annotationsClasses
                =  AnnotationsManager.getManager().getManagedAnnotationClasses();

        for (Class aClass: annotationsClasses){
            Target target;
            target = (Target) aClass.getAnnotation(Target.class);
            if (null!= target && ArrayUtils.contains(target.value(),
                    ElementType.FIELD)){
                annotations.add(aClass.getName());
                annotationsImpl.add(AnnotationsManager.getManager()
                        .getAnnotationImplementationClass(aClass).getName());
            }
        }
        colAnnotations = new ArrayList<AnnModel>();
        columnNames = new ArrayList<String>();
    }

    //**************************************************************************
    // Action default execute method
    //**************************************************************************

    public String execute() {
        if (qualifiedTableName == null) {
            qualifiedTableName = model.getAllTables().get(0).getQualifiedName();
            return REDIRECT_TO_FIRST;
        }

        Table table = setupTable();

        tableForm = new FormBuilder(Table.class)
                .configFields("databaseName", "schemaName", "tableName")
                .configMode(Mode.VIEW)
                .build();
        tableForm.readFromObject(table);

        columnTableForm = new TableFormBuilder(Column.class)
                .configFields("columnName", "columnType")
                .configNRows(table.getColumns().size())
                .configMode(Mode.VIEW)
                .build();
        columnTableForm.readFromObject(table.getColumns());
        return SUMMARY;
    }

    //**************************************************************************
    // Common methods
    //**************************************************************************

    public Table setupTable() {
        Table table = model.findTableByQualifiedName(qualifiedTableName);
        if (table == null) {
            throw new ModelObjectNotFoundError(qualifiedTableName);
        }
        return table;
    }

    //**************************************************************************
    // Cancel
    //**************************************************************************

    public String cancel() {
        return CANCEL;
    }

    //**************************************************************************
    // Drop
    //**************************************************************************

    public String drop() {
        return "drop";
    }

    //**************************************************************************
    // Add new Column
    //**************************************************************************

    public String create() throws CloneNotSupportedException {
        /*CreateTableStatement cts = new CreateTableStatement("pubLic", "teZt");
        cts.addColumn("a1", new VarcharType());
        CreateTableGenerator generator = new CreateTableGenerator();
        try {
            Database database =
                   CommandLineUtils.createDatabaseObject(getClass().getClassLoader(),
                            "jdbc:postgresql://127.0.0.1:5432/portofino4", "manydesigns", "manydesigns", "org.postgresql.Driver",
                            "public", "liquibase.database.core.PostgresDatabase");
            SortedSet<SqlGenerator> sqlGenerators = new TreeSet<SqlGenerator>();
            sqlGenerators.add(generator);
            Sql[] sqls =  generator.generateSql(cts, database, new SqlGeneratorChain(sqlGenerators) );
            for(Sql sql : sqls){
                System.out.println(sql.toSql());
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/
        setupForms();
        step= TABLE_STEP;
        return CREATE;
    }



    public String addCol() {
        step = COLUMN_STEP;
        setupForms();
        readFromRequest();
        Column col = new Column(table);
        columnForm.readFromRequest(req);
        if(!columnForm.validate()){
            return CREATE;
        }      
       
        columnForm.writeToObject(col);
        List<Column> columns = table.getColumns();
        boolean found = false;
        for (Column currentColumn : columns){
            String name = currentColumn.getColumnName();
            if (name.equals(col.getColumnName())){
                found = true;
            }
        }       
        if (!found){
            columns.add(col);
            columnNames.add(col.getColumnName());
        } else {
            SessionMessages.addInfoMessage("Column exists");
        }
        columnTableForm = new TableFormBuilder(Column.class)
            .configFields("columnName", "columnType", "nullable",
                    "autoincrement", "length", "scale",
                    "searchable", "javaType", "propertyName")
                .configPrefix("cols_")
                .configNRows(table.getColumns().size())
                .configMode(Mode.CREATE_PREVIEW)
                .build();
        ncol++;
        columnTableForm.setSelectable(true);
        columnTableForm.setKeyGenerator(OgnlTextFormat.create("%{columnName}"));
        columnTableForm.readFromObject(table.getColumns());

        return CREATE;
}

    public String remCol() {
        step= COLUMN_STEP;
        setupForms();
        if(!readFromRequest()){
            return CREATE;
        }
        columnNames.clear();
        for(TableForm.Row row : columnTableForm.getRows()) {
            try {
                Column currCol = new Column(table);
                row.writeToObject(currCol);
                if (ArrayUtils.contains(cols_selection, currCol.getColumnName())){
                    table.getColumns().remove(
                            table.findColumnByName(
                                    currCol.getColumnName()));
                } else {
                    columnNames.add(currCol.getColumnName());
                }
            } catch (Throwable e) {
                logger.info(e.getMessage());
            }
        }

        columnTableForm = new TableFormBuilder(Column.class)
            .configFields("columnName", "columnType", "nullable",
                    "autoincrement", "length", "scale",
                    "searchable", "javaType", "propertyName")
                .configPrefix("cols_")
                .configNRows(table.getColumns().size())
                .configMode(Mode.CREATE_PREVIEW)
                .build();
        columnTableForm.setSelectable(true);
        columnTableForm.setKeyGenerator(OgnlTextFormat.create("%{columnName}"));
        columnTableForm.readFromObject(table.getColumns());
        ncol = table.getColumns().size();
        columnTableForm.setSelectable(true);
        columnTableForm.setKeyGenerator(OgnlTextFormat.create("%{columnName}"));
        columnTableForm.readFromObject(table.getColumns());

        return CREATE;
    }


    public String addColAnnotation() {
        step= ANNOTATION_STEP;
        setupForms();
        readFromRequest();
        
        AnnModel annotation = new AnnModel();
        Properties properties = new Properties();

        annForm.writeToObject(annotation);
        annPropForm.writeToObject(properties);
        annotation.properties=properties;

        colAnnotations.add(annotation);

        colAnnotationTableForm = new TableFormBuilder(AnnModel.class)
            .configFields("columnName", "annotationName", "propValues")
                .configPrefix("colAnnT_")
            .configNRows(colAnnotations.size())
            .configMode(Mode.CREATE_PREVIEW)
            .build();
        nAnnotations++;
        colAnnotationTableForm.setSelectable(true);
        colAnnotationTableForm.setKeyGenerator(
                OgnlTextFormat.create("%{columnName+\"_\"+annotationName}"));
        colAnnotationTableForm.readFromObject(colAnnotations);

        return CREATE;
    }

    public String setAnnParameters() throws ClassNotFoundException, NoSuchFieldException {
        step= ANNOTATION_STEP;
        setupForms();
        readFromRequest(); 
        if (colAnn_annotationName==null){
            SessionMessages.addErrorMessage("SELECT A ANNOTATION");
 
        }
        return CREATE;
    }

    public String remColAnnotation() {
        step= ANNOTATION_STEP;
        setupForms();
        if(!readFromRequest()){
            return CREATE;
        }

        for(TableForm.Row row : colAnnotationTableForm.getRows()) {
            try {
                AnnModel annotation = new AnnModel();
                row.writeToObject(annotation);
                if (ArrayUtils.contains(colAnnT_selection,
                        annotation.columnName+"_"+
                        annotation.annotationName)){
                        colAnnotations.remove(annotation);
                }
            } catch (Throwable e) {
                //do nothing: accetto errori quali assenza di pk sulla tabella
                logger.info(e.getMessage());
            }
        }

        colAnnotationTableForm = new TableFormBuilder(AnnModel.class)
            .configFields("columnName", "annotationName", "propValues")
                .configPrefix("colAnnT_")
            .configNRows(colAnnotations.size())
            .configMode(Mode.CREATE_PREVIEW)
            .build();
        colAnnotationTableForm.setSelectable(true);
        colAnnotationTableForm.setKeyGenerator(OgnlTextFormat
                .create("%{columnName+\"_\"+annotationName}"));
        colAnnotationTableForm.readFromObject(colAnnotations);
        nAnnotations=colAnnotations.size();

        return CREATE;
    }

    public String addPkCol() {
        step= PRIMARYKEY_STEP;
        setupForms();
        if(!readFromRequest()){
            return CREATE;
        }

        PrimaryKeyColumnModel colModel = new PrimaryKeyColumnModel();

        pkColumnForm.writeToObject(colModel);
        pkModel.add(colModel);
        npkcol++;

        pkColumnTableForm = new TableFormBuilder(PrimaryKeyColumnModel.class)
                .configFields("column", "genType", "seqName",
                        "tabName", "colName", "colValue").configPrefix("pkCols_")
            .configNRows(pkModel.size())
            .configMode(Mode.CREATE_PREVIEW)
            .build();

        pkColumnTableForm.setSelectable(true);
        pkColumnTableForm.setKeyGenerator(OgnlTextFormat.create("%{column}"));
        pkColumnTableForm.readFromObject(pkModel);
        return CREATE;
    }

    public String remPkCol() {
        step= PRIMARYKEY_STEP;
        setupForms();
        if(!readFromRequest()){
            return CREATE;
        }
        for(TableForm.Row row : pkColumnTableForm.getRows()) {
            try {
                PrimaryKeyColumnModel currCol = new PrimaryKeyColumnModel();
                row.writeToObject(currCol);
                if (ArrayUtils.contains(pkCols_selection, currCol.column)){
                    pkModel.remove(currCol);
                    npkcol--;
                } 
            } catch (Throwable e) {
                // do nothing: accetto errori quali assenza di pk sulla tabella
                // la classe mi serve solo come modello dei dati
                logger.info(e.getMessage());
            }
        }
        pkColumnTableForm = new TableFormBuilder(PrimaryKeyColumnModel.class)
                .configFields("column", "genType", "seqName",
                        "tabName", "colName", "colValue").configPrefix("pkCols_")
            .configNRows(pkModel.size())
            .configMode(Mode.CREATE_PREVIEW)
            .build();

        pkColumnTableForm.setSelectable(true);
        pkColumnTableForm.setKeyGenerator(OgnlTextFormat.create("%{column}"));
        pkColumnTableForm.readFromObject(pkModel);

        return CREATE;
    }

    //**************************************************************************
    // private methods
    //**************************************************************************
    //**************************************************************************
    // Preparazione dei form
    //**************************************************************************
    private void setupForms() {
        if (ncol == null){
            ncol = 0;
        }
        if (npkcol == null){
            npkcol = 0;
        }
        if (nAnnotations == null){
            nAnnotations = 0;
        }
        Mode mode = Mode.CREATE;

        //Available databases
        List<Database> databases = model.getDatabases();
        String [] databaseNames = new String[databases.size()];
        int i = 0;
        for (Database db : databases){
            databaseNames[i++] = db.getQualifiedName();
        }
        //Costruisco form per Table

        FormBuilder formBuilder = new FormBuilder(Table.class)
                .configFields("databaseName", "schemaName", "tableName")
                .configMode(mode);

        SelectionProvider selectionProvider = DefaultSelectionProvider
                .create("databases",databaseNames, databaseNames);
        formBuilder.configSelectionProvider(selectionProvider, "databaseName");
        formBuilder.configPrefix("table_");
        tableForm = formBuilder.build();

        //Costruisco form per Column
        formBuilder = new FormBuilder(Column.class)
                .configFields("columnName", "columnType", "nullable",
                        "autoincrement", "length", "scale",
                        "searchable", "javaType", "propertyName")
                .configMode(mode);
        formBuilder.configPrefix("column_");
        columnForm = formBuilder.build();

        //Costruisco form per Primary Key
        formBuilder = new FormBuilder(PrimaryKey.class)
                .configFields("primaryKeyName")
                .configMode(mode);
        formBuilder.configPrefix("pk_");
        pkForm = formBuilder.build();
        pkColumnForm = new FormBuilder(PrimaryKeyColumnModel.class)
                .configFields("column", "genType", "seqName",
                        "tabName", "colName", "colValue").configPrefix("pk_")
                .configMode(mode).build();

        //Costruisco form per Annotations
        formBuilder = new FormBuilder(AnnModel.class)
                .configFields("columnName", "annotationName").configPrefix("colAnn_")
                .configMode(mode);
        String[] anns = new String[annotations.size()];
        String[] annsImpl = new String[annotationsImpl.size()];
        SelectionProvider selectionProviderAnns =
                DefaultSelectionProvider.create("annotations",
                annotationsImpl.toArray(annsImpl), annotations.toArray(anns));
        formBuilder.configSelectionProvider(selectionProviderAnns, "annotationName");
        annForm = formBuilder.build();

        if (colAnn_annotationName!=null && colAnn_annotationName.length()>0){
            try {
                Class annotationClass =
                        this.getClass().getClassLoader()
                        .loadClass(colAnn_annotationName);
                Properties properties = new Properties();
                Field[] fields = annotationClass.getDeclaredFields();
                for (Field field : fields){
                    if (!Modifier.isStatic(field.getModifiers())){
                        properties.put(field.getName(), "");
                    }
                }
                ClassAccessor propertiesAccessor = new PropertiesAccessor(properties);
                FormBuilder builder = new FormBuilder(propertiesAccessor);
                annPropForm = builder.configMode(Mode.CREATE).build();
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
                SessionMessages.addErrorMessage(e.getMessage());
            }
        }

    }

    //**************************************************************************
    // Inizializzazione dei form a partire dalla request
    //**************************************************************************
    private boolean readFromRequest() {
        if(null==table_databaseName){
            return false;
        }

        tableForm.readFromRequest(req);

        if(!tableForm.validate()){
            return false;
        }
        //Gestione tabella
        Database database  =
            new Database(model.findDatabaseByName(table_databaseName)
                    .getDatabaseName());
        Schema schema = new Schema(database, table_schemaName);
        table = new Table(schema, table_tableName);

        schema.getTables().add(table);
        tableForm.readFromObject(table);

        if(!tableForm.validate()){
            return false;
        }

        //Gestione colonne
        columnTableForm = new TableFormBuilder(Column.class)
            .configFields("columnName", "columnType", "nullable",
                    "autoincrement", "length", "scale",
                    "searchable", "javaType", "propertyName")
            .configPrefix("cols_").configNRows(ncol)
            .configMode(Mode.CREATE_PREVIEW)
            .build();
        columnTableForm.setSelectable(true);
        columnTableForm.setKeyGenerator(OgnlTextFormat.create("%{columnName}"));
        columnTableForm.readFromRequest(req);
        for(TableForm.Row row : columnTableForm.getRows()) {
            try {
                Column currCol = new Column(table);
                row.writeToObject(currCol);
                table.getColumns().add(currCol);
                columnNames.add(currCol.getColumnName());
            } catch (Throwable e) {
                //Do nothing
            }
        }

        //Gestione Chiave primaria
        pkModel = new PrimaryKeyModel();
        pkColumnTableForm = new TableFormBuilder(PrimaryKeyColumnModel.class)
                .configFields("column", "genType", "seqName",
                        "tabName", "colName", "colValue").configPrefix("pkCols_")
            .configNRows(npkcol)
            .configMode(Mode.CREATE_PREVIEW)
            .build();

        pkColumnTableForm.setSelectable(true);
        pkColumnTableForm.setKeyGenerator(OgnlTextFormat.create("%{column}"));
        pkColumnTableForm.readFromRequest(req);
        pkForm.readFromRequest(req);
        pkModel.primaryKeyName = pk_primaryKeyName!=null?
            pk_primaryKeyName:"pk_"+table_tableName;
        pkColumnForm.readFromRequest(req);
        for(TableForm.Row row : pkColumnTableForm.getRows()) {
            try {
                PrimaryKeyColumnModel currCol = new PrimaryKeyColumnModel();
                row.writeToObject(currCol);
                pkModel.add(currCol);
            } catch (Throwable e) {
                //Do nothing
                logger.error(e.getMessage());
            }
        }

        //Gestione annotations
        colAnnotationTableForm = new TableFormBuilder(AnnModel.class)
            .configFields("columnName", "annotationName", "propValues")
                .configPrefix("colAnnT_")
            .configNRows(nAnnotations)
            .configMode(Mode.CREATE_PREVIEW)
            .build();

        colAnnotationTableForm.setSelectable(true);
        colAnnotationTableForm.setKeyGenerator(
                OgnlTextFormat.create("%{columnName+\"_\"+annotationName}"));
        colAnnotationTableForm.readFromRequest(req);
        for(TableForm.Row row : colAnnotationTableForm.getRows()) {
            try {
                AnnModel currAnnotation = new AnnModel();
                row.writeToObject(currAnnotation);
                colAnnotations.add(currAnnotation);
            } catch (Throwable e) {
                logger.error(e.getMessage());
            }
        }
        
        //Proprieta' delle annotation
        annForm.readFromRequest(req);
        annPropForm.readFromRequest(req);

        return true;
    }





    private String createJsonArray (List<String> collection) {
        List<String> resulList = new ArrayList<String>();

        for(String string : collection){

                resulList.add("\""+string+"\"");
        }
        String result = "["+ StringUtils.join(resulList, ",")+"]";
        inputStream = new ByteArrayInputStream(result.getBytes());
        return "json";

    }

    //**************************************************************************
    // Json output per lista Colonne
    //**************************************************************************
    public String jsonColumns() throws Exception {
        return createJsonArray(columnNames);
    }

    //**************************************************************************
    // Json output per i corretti types per una piattaforma
    //**************************************************************************
    public String jsonTypes() throws Exception {
        Type[] types = context.getConnectionProvider(table_databaseName).getTypes();
        List<String> typesString = new ArrayList<String>();

        for(Type currentType : types){
            if(null!=term && !"".equals(term)) {
                if (StringUtils.startsWithIgnoreCase(currentType.getTypeName(),term))
                   typesString.add("\""+currentType.getTypeName()+"\"");
            } else {
                typesString.add("\""+currentType.getTypeName()+"\"");
            }
        }
        String result = "["+ StringUtils.join(typesString, ",")+"]";
        inputStream = new ByteArrayInputStream(result.getBytes());
        return "json";
    }

    //**************************************************************************
    // Json output per i corretti Java types per una piattaforma
    //**************************************************************************
    public String jsonJavaTypes() throws Exception {
        Type[] types = context.getConnectionProvider(table_databaseName).getTypes();
        List<String> javaTypesString = new ArrayList<String>();

        for(Type currentType : types){
            if(StringUtils.equalsIgnoreCase(currentType.getTypeName(),
                    req.getParameter("column_columnType"))){
                String defJavaType;
                try{
                    defJavaType= currentType.getDefaultJavaType().getName();
                } catch (Throwable e){
                    defJavaType="UNSOPPORTED";
                }
                javaTypesString.add("\""+defJavaType+"\"");
            }
        }
        String result = "["+ StringUtils.join(javaTypesString, ",")+"]";
        inputStream = new ByteArrayInputStream(result.getBytes());
        return "json";
    }
    
    //**************************************************************************
    // Json output per vedere se richiesta Precision, Scale, ...
    //**************************************************************************
    public String jsonTypeInfo() throws Exception {
        Type[] types = context.getConnectionProvider(table_databaseName).getTypes();
        List<String> info = new ArrayList<String>();

        for(Type currentType : types){
            if(StringUtils.equalsIgnoreCase(currentType.getTypeName(),
                    req.getParameter("column_columnType"))){

                info.add("\"precision\" : \""+
                        (currentType.isPrecisionRequired()?"true":"false")+"\"");
                info.add("\"scale\" : \""+
                        (currentType.isScaleRequired()?"true":"false")+"\"");
                info.add("\"searchable\" : \""+
                        (currentType.isSearchable()?"true":"false")+"\"");
                info.add("\"autoincrement\" : \""+
                        (currentType.isAutoincrement()?"true":"false")+"\"");
            }
        }
        String result = "{"+ StringUtils.join(info, ",")+"}";
        inputStream = new ByteArrayInputStream(result.getBytes());
        return "json";
    }

    //**************************************************************************
    // Json output per lista Annotations
    //**************************************************************************
    public String jsonAnnotation() throws Exception {
        return createJsonArray(annotations);
    }

}

