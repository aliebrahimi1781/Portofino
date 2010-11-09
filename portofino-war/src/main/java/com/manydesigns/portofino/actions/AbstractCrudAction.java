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

package com.manydesigns.portofino.actions;

import com.manydesigns.elements.blobs.Blob;
import com.manydesigns.elements.blobs.BlobsManager;
import com.manydesigns.elements.logging.LogUtil;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.logging.Logger;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public abstract class AbstractCrudAction extends PortofinoAction
        implements ServletRequestAware, Preparable, ModelDriven {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    //**************************************************************************
    // Constants
    //**************************************************************************

    public final String DEFAULT_EXPORT_FILENAME_FORMAT = "export-{0}";

    //**************************************************************************
    // ServletRequestAware implementation
    //**************************************************************************
    public HttpServletRequest req;

    public void setServletRequest(HttpServletRequest req) {
        this.req = req;
    }

    //**************************************************************************
    // Preparable/ModelDriven implementation
    //**************************************************************************

    public void prepare() {
        setupMetadata();
    }

    public CrudUnit getModel() {
        return rootCrudUnit;
    }

    //**************************************************************************
    // Configuration parameters and setters (for struts.xml inspections in IntelliJ)
    //**************************************************************************

    public String qualifiedName;
    public String exportFilenameFormat = DEFAULT_EXPORT_FILENAME_FORMAT;

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public void setExportFilenameFormat(String exportFilenameFormat) {
        this.exportFilenameFormat = exportFilenameFormat;
    }

    //**************************************************************************
    // Web parameters
    //**************************************************************************

    public String pk;
    public String[] selection;
    public String searchString;
    public String cancelReturnUrl;
    public String relName;
    public int selectionProviderIndex;
    public String labelSearch;
    public String code;

    //**************************************************************************
    // Use case instance root (tree)
    //**************************************************************************

    public CrudUnit rootCrudUnit;

    //**************************************************************************
    // Presentation/export elements
    //**************************************************************************

    public InputStream inputStream;
    public String errorMessage;
    public String contentType;
    public String fileName;
    public Long contentLength;
    public String chartId;

    //**************************************************************************
    // Logging
    //**************************************************************************

    public static final Logger logger =
            LogUtil.getLogger(AbstractCrudAction.class);

    //**************************************************************************
    // Action default execute method
    //**************************************************************************

    public String execute() {
        if (qualifiedName == null) {
            return redirectToFirst();
        } else {
            return rootCrudUnit.execute();
        }
    }

    public abstract String redirectToFirst();
    public abstract void setupMetadata();

    //**************************************************************************
    // Search
    //**************************************************************************

    public String search() {
        return rootCrudUnit.search();
    }

    //**************************************************************************
    // Return to search
    //**************************************************************************

    public String returnToSearch() {
        return RETURN_TO_SEARCH;
    }

    //**************************************************************************
    // Read
    //**************************************************************************

    public String read() {
        return rootCrudUnit.read();
    }

    //**************************************************************************
    // Blobs
    //**************************************************************************

    public String downloadBlob() throws IOException {
        Blob blob = BlobsManager.getManager().loadBlob(code);
        contentLength = blob.getSize();
        contentType = blob.getContentType();
        inputStream = new FileInputStream(blob.getDataFile());
        fileName = blob.getFilename();
        return EXPORT;
    }

    //**************************************************************************
    // Create/Save
    //**************************************************************************

    public String create() {
        return rootCrudUnit.create();
    }

    public String save() {
        return rootCrudUnit.save();
    }

    //**************************************************************************
    // Edit/Update
    //**************************************************************************

    public String edit() {
        return rootCrudUnit.edit();
    }

    public String update() {
        return rootCrudUnit.update();
    }

    //**************************************************************************
    // Bulk Edit/Update
    //**************************************************************************

    public String bulkEdit() {
        return rootCrudUnit.bulkEdit();
    }

    public String bulkUpdate() {
        return rootCrudUnit.bulkUpdate();
    }

    //**************************************************************************
    // Delete
    //**************************************************************************

    public String delete() {
        return rootCrudUnit.delete();
    }

    public String bulkDelete() {
        return rootCrudUnit.bulkDelete();
    }

    //**************************************************************************
    // Cancel
    //**************************************************************************

    public String cancel() {
        return CANCEL;
    }

    //**************************************************************************
    // Ajax
    //**************************************************************************

    public String jsonSelectFieldOptions() {
        String text = rootCrudUnit.jsonOptions(
                relName, selectionProviderIndex, labelSearch, true);
        inputStream = new StringBufferInputStream(text);
        return JSON_SELECT_FIELD_OPTIONS;
    }

    public String jsonAutocompleteOptions() {
        String text = rootCrudUnit.jsonOptions(
                relName, selectionProviderIndex, labelSearch, false);
        inputStream = new StringBufferInputStream(text);
        return JSON_SELECT_FIELD_OPTIONS;
    }


    //**************************************************************************
    // ExportSearch
    //**************************************************************************
/*
    public String exportSearchExcel() {
        setupMetadata();

        SearchFormBuilder searchFormBuilder =
                new SearchFormBuilder(classAccessor);
        searchForm = searchFormBuilder.build();
        searchForm.readFromRequest(req);

        Criteria criteria = new Criteria(classAccessor);
        searchForm.configureCriteria(criteria);
        objects = context.getObjects(criteria);

        TableFormBuilder tableFormBuilder =
            createTableFormBuilderWithSelectionProviders()
                            .configNRows(objects.size());
        tableForm = tableFormBuilder.configMode(Mode.VIEW)
                .build();
        tableForm.readFromObject(objects);

        writeFileSearchExcel();

        return EXPORT;
    }

    private void writeFileSearchExcel() {
        File fileTemp = createExportTempFile();
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(fileTemp);
            WritableSheet sheet = workbook.createSheet(qualifiedName, 0);

            addHeaderToSheet(sheet);

            int i = 1;
            for ( TableForm.Row row : tableForm.getRows()) {
                exportRows(sheet, i, row);
                i++;
            }

            workbook.write();
        } catch (IOException e) {
            LogUtil.warning(logger, "IOException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        } catch (RowsExceededException e) {
            LogUtil.warning(logger, "RowsExceededException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        } catch (WriteException e) {
            LogUtil.warning(logger, "WriteException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            }
            catch (Exception e) {
                LogUtil.warning(logger, "IOException", e);
                SessionMessages.addErrorMessage(e.getMessage());
            }
        }
        paramExport(fileTemp);
    }

    //**************************************************************************
    // ExportRead
    //**************************************************************************

    public String exportReadExcel() {
        setupMetadata();
        Serializable pkObject = pkHelper.parsePkString(pk);

        SearchFormBuilder searchFormBuilder =
                new SearchFormBuilder(classAccessor);
        searchForm = searchFormBuilder.build();
        configureSearchFormFromString();

        Criteria criteria = new Criteria(classAccessor);
        searchForm.configureCriteria(criteria);
        objects = context.getObjects(criteria);

        object = context.getObjectByPk(qualifiedName, pkObject);

        TableFormBuilder tableFormBuilder =
            createTableFormBuilderWithSelectionProviders()
                            .configMode(Mode.VIEW)
                            .configNRows(objects.size());
        tableForm = tableFormBuilder.build();
        tableForm.readFromObject(object);

        form = createFormBuilderWithSelectionProviders()
                .configMode(Mode.VIEW)
                .build();
        form.readFromObject(object);

        relatedTableFormList = new ArrayList<RelatedTableForm>();
        Table table = model.findTableByQualifiedName(qualifiedName);
        for (ForeignKey relationship : table.getOneToManyRelationships()) {
            setupRelatedTableForm(relationship);
        }

        writeFileReadExcel();

        return EXPORT;
    }


    private void writeFileReadExcel() {
        File fileTemp = createExportTempFile();
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(fileTemp);
            WritableSheet sheet = workbook.createSheet(qualifiedName, 0);

            addHeaderToSheet(sheet);

            int i = 1;
            for (FieldSet fieldset : form) {
                int j = 0;
                for (Field field : fieldset) {
                    addFieldToCell(sheet, i, j, field);
                    j++;
                }
                i++;
            }

            //Aggiungo le relazioni/sheet
           int k = 1;
           WritableCellFormat formatCell = headerExcel();
           for (RelatedTableForm relTabForm : relatedTableFormList) {
                sheet = workbook.createSheet(relTabForm.relationship.
                        getFromTable().getQualifiedName() , k);
                k++;
                int m = 0;
                for (TableForm.Column col : relTabForm.tableForm.getColumns()) {
                    sheet.addCell(new Label(m, 0, col.getLabel(), formatCell));
                    m++;
                }
                i = 1;
                for (TableForm.Row row : relTabForm.tableForm.getRows()) {
                    int j = 0;
                    for (Field field : Arrays.asList(row.getFields())) {
                        addFieldToCell(sheet, i, j, field);
                        j++;
                    }
                    i++;
                }
            }

            workbook.write();
        } catch (IOException e) {
            LogUtil.warning(logger, "IOException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        } catch (RowsExceededException e) {
            LogUtil.warning(logger, "RowsExceededException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        } catch (WriteException e) {
            LogUtil.warning(logger, "WriteException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            }
            catch (Exception e) {
                LogUtil.warning(logger, "IOException", e);
                SessionMessages.addErrorMessage(e.getMessage());
            }
        }

        paramExport(fileTemp);
    }


    private WritableCellFormat headerExcel() {
        WritableFont fontCell = new WritableFont(WritableFont.ARIAL, 12,
             WritableFont.BOLD, true);
        return new WritableCellFormat (fontCell);
    }

    private void exportRows(WritableSheet sheet, int i,
                            TableForm.Row row) throws WriteException {
        int j = 0;
        for (Field field : row.getFields()) {
            addFieldToCell(sheet, i, j, field);

            j++;
        }
    }

    private File createExportTempFile() {
         String exportId = RandomUtil.createRandomCode();
         return RandomUtil.getTempCodeFile(exportFilenameFormat, exportId);
     }


    private void paramExport(File fileTemp) {
        contentType = "application/ms-excel; charset=UTF-8";
        fileName = fileTemp.getName() + ".xls";

        contentLength = fileTemp.length();

        try {
            inputStream = new FileInputStream(fileTemp);
        } catch (IOException e) {
            LogUtil.warning(logger, "IOException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        }
    }

    private void addHeaderToSheet(WritableSheet sheet) throws WriteException {
        WritableCellFormat formatCell = headerExcel();
        int l = 0;
        for (TableForm.Column col : tableForm.getColumns()) {
            sheet.addCell(new Label(l, 0, col.getLabel(), formatCell));
            l++;
        }
    }

    private void addFieldToCell(WritableSheet sheet, int i, int j,
                                Field field) throws WriteException {
        if (field instanceof NumericField) {
            NumericField numField = (NumericField) field;
            if (numField.getDecimalValue() != null) {
                Number number;
                BigDecimal decimalValue = numField.getDecimalValue();
                if (numField.getDecimalFormat() == null) {
                    number = new Number(j, i,
                            decimalValue == null
                                    ? null : decimalValue.doubleValue());
                } else {
                    NumberFormat numberFormat = new NumberFormat(
                            numField.getDecimalFormat().toPattern());
                    WritableCellFormat writeCellNumberFormat =
                            new WritableCellFormat(numberFormat);
                    number = new Number(j, i,
                            decimalValue == null
                                    ? null : decimalValue.doubleValue(),
                            writeCellNumberFormat);
                }
                sheet.addCell(number);
            }
        } else if (field instanceof PasswordField) {
            Label label = new Label(j, i,
                    PasswordField.PASSWORD_PLACEHOLDER);
            sheet.addCell(label);
        } else if (field instanceof DateField) {
            DateField dateField = (DateField) field;
            DateTime dateCell;
            Date date = dateField.getDateValue();
            if (date != null) {
                DateFormat dateFormat = new DateFormat(
                        dateField.getDatePattern());
                WritableCellFormat wDateFormat =
                        new WritableCellFormat(dateFormat);
                dateCell = new DateTime(j, i,
                        dateField.getDateValue() == null
                                ? null : dateField.getDateValue(),
                        wDateFormat);
                sheet.addCell(dateCell);
            }
        } else {
            Label label = new Label(j, i, field.getStringValue());
            sheet.addCell(label);
        }
    }



    //**************************************************************************
    // ExportReadPdf
    //**************************************************************************

    public String exportSearchPdf() throws FOPException,
            IOException, TransformerException {
        setupMetadata();

        SearchFormBuilder searchFormBuilder =
                new SearchFormBuilder(classAccessor);
        searchForm = searchFormBuilder.build();
        searchForm.readFromRequest(req);

        Criteria criteria = new Criteria(classAccessor);
        searchForm.configureCriteria(criteria);
        objects = context.getObjects(criteria);

        TableFormBuilder tableFormBuilder =
            createTableFormBuilderWithSelectionProviders()
                            .configNRows(objects.size());
        tableForm = tableFormBuilder.configMode(Mode.VIEW)
                .build();
        tableForm.readFromObject(objects);

        FopFactory fopFactory = FopFactory.newInstance();

        FileOutputStream out = null;
        File tempPdfFile = createExportTempFile();
        try {
            out = new FileOutputStream(tempPdfFile);

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            ClassLoader cl = getClass().getClassLoader();
            InputStream xsltStream = cl.getResourceAsStream(
                   "templateFOP.xsl");

            // Setup XSLT
            TransformerFactory Factory = TransformerFactory.newInstance();
            Transformer transformer = Factory.newTransformer(new StreamSource(
                    xsltStream));

            // Set the value of a <param> in the stylesheet
            transformer.setParameter("versionParam", "2.0");

            // Setup input for XSLT transformation
            String xml = composeXml();
            Source src = new StreamSource(new StringReader(xml));

            // Resulting SAX events (the generated FO) must be piped through to
            // FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);

            out.flush();
        } catch (Exception e) {
            LogUtil.warning(logger, "IOException", e);
            SessionMessages.addErrorMessage(e.getMessage());
        } finally {
            try {
                if (out != null)
                    out.close();
            }
            catch (Exception e) {
                LogUtil.warning(logger, "IOException", e);
                SessionMessages.addErrorMessage(e.getMessage());
            }
        }

        inputStream = new FileInputStream(tempPdfFile);

        contentType = "application/pdf";

        fileName = tempPdfFile.getName() + ".pdf";

        contentLength = tempPdfFile.length();

        return EXPORT;
    }

    public String composeXml() {
        // TODO: per favore usa XmlBuffer
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<class>");
        sb.append("<table>");
        sb.append(qualifiedName);
        sb.append("</table>");
        for (TableForm.Row row : tableForm.getRows()) {
            for (Field field : row.getFields()) {
                sb.append("<header>");
                sb.append("<nameColumn>");
                sb.append(field.getLabel());
                sb.append("</nameColumn>");
                sb.append("</header>");
            }
        }

        for (TableForm.Row row : tableForm.getRows()) {
            for (Field field : row.getFields()) {
                sb.append("<row>");
                sb.append("<value>");
                sb.append(field.getStringValue());
                sb.append("</value>");
                sb.append("</row>");
            }
        }
        sb.append("</class>");
        return sb.toString();
    }
*/
}