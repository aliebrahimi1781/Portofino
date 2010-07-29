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

package com.manydesigns.elements.fields;

import com.manydesigns.elements.reflection.PropertyAccessor;
import com.manydesigns.elements.xml.XhtmlBuffer;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class UploadField extends AbstractField
        implements MultipartFormDataField {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    public static final String UPLOAD_KEEP = "_keep";
    public static final String UPLOAD_MODIFY = "_modify";
    public static final String UPLOAD_DELETE = "_delete";

    public static final String OPERATION_SUFFIX = "_operation";

    protected FileUpload fileUpload;

    //--------------------------------------------------------------------------
    // Costruttori
    //--------------------------------------------------------------------------
    public UploadField(PropertyAccessor accessor) {
        this(accessor, null);
    }

    public UploadField(PropertyAccessor accessor, String prefix) {
        super(accessor, prefix);
    }

    //--------------------------------------------------------------------------
    // AbstractField implementation
    //--------------------------------------------------------------------------
    public void valueToXhtml(XhtmlBuffer xb) {
        if (mode.isView(immutable)) {
            valueToXhtmlView(xb);
        } else if (mode.isEdit()) {
            valueToXhtmlEdit(xb);
        } else if (mode.isPreview()) {
            valueToXhtmlView(xb);
        } else if (mode.isHidden()) {
            // do nothing
        } else {
            throw new IllegalStateException("Unknown mode: " + mode);
        }
    }

    private void valueToXhtmlView(XhtmlBuffer xb) {
        xb.openElement("div");
        xb.addAttribute("id", id);
        xb.addAttribute("class", "value");
        if (fileUpload != null
                && fileUpload.getFileName() != null
                && !"".equals(fileUpload.getFileName())) {
            xb.openElement("a");
            xb.addAttribute("href", fileUpload.getDownloadUrl());
            valueToTextOrPreview(xb);
            xb.closeElement("a");
        }
        xb.closeElement("div");
    }

    private void valueToTextOrPreview(XhtmlBuffer xb) {
        if (fileUpload.getPreviewUrl() == null) {
            xb.write(fileUpload.getFileName());
        } else {
            xb.writeImage(fileUpload.getPreviewUrl(),
                    fileUpload.getFileName(),
                    fileUpload.getFileName(), null, null);
        }
    }

    private void valueToXhtmlEdit(XhtmlBuffer xb) {
        String operationInputName = inputName + OPERATION_SUFFIX;

        if (fileUpload != null
                && fileUpload.getFileName() != null
                && !"".equals(fileUpload.getFileName())) {

            String innerId = id + "_inner";

            xb.openElement("div");
            xb.addAttribute("class", "value");
            xb.addAttribute("id", id);

            xb.openElement("div");
            valueToTextOrPreview(xb);
            xb.closeElement("div");

            xb.openElement("div");
            String keepIdStr = id + UPLOAD_KEEP;
            String script = "var inptxt = document.getElementById('"
                    + StringEscapeUtils.escapeJavaScript(innerId) + "');"
                    + "inptxt.disabled=true;inptxt.value='';";
            xb.writeInputRadio(keepIdStr, operationInputName, UPLOAD_KEEP,
                    true, false, script);
            xb.writeLabel(getText("elements.field.upload.keep"), keepIdStr, null);
            xb.closeElement("div");

            xb.openElement("div");
            String modifyIdStr = id + UPLOAD_MODIFY;
            script = "var inptxt = document.getElementById('"
                    + StringEscapeUtils.escapeJavaScript(innerId) + "');"
                    + "inptxt.disabled=false;inptxt.value='';";
            xb.writeInputRadio(modifyIdStr, operationInputName,
                    UPLOAD_MODIFY, false, false, script);
            xb.writeLabel(getText("elements.field.upload.update"), modifyIdStr, null);
            xb.closeElement("div");

            xb.openElement("div");
            String deleteIdStr = id + UPLOAD_DELETE;
            script = "var inptxt = document.getElementById('"
                    + StringEscapeUtils.escapeJavaScript(innerId) + "');"
                    + "inptxt.disabled=true;inptxt.value='';";
            xb.writeInputRadio(deleteIdStr, operationInputName,
                    UPLOAD_DELETE, false, false, script);
            xb.writeLabel(getText("elements.field.upload.delete"), deleteIdStr, null);
            xb.closeElement("div");

            xb.writeInputFile(innerId, inputName, true);

            xb.closeElement("div");
        } else {
            xb.writeInputHidden(operationInputName, UPLOAD_MODIFY);
            // TODO: verificare se primo parametro è inputName o id
            xb.writeInputFile(inputName, inputName, false);
        }
    }

    //--------------------------------------------------------------------------
    // Element implementation
    //--------------------------------------------------------------------------
    public void readFromRequest(HttpServletRequest req) {
        super.readFromRequest(req);

        if (mode.isView(immutable)) {
            return;
        }

        Class reqClass = req.getClass();
        if (!(reqClass.getName().equals(
                "org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper"))) {
            return;
        }

        String operationInputName = inputName + OPERATION_SUFFIX;

        String updateTypeStr = req.getParameter(operationInputName);
        if (updateTypeStr == null) {
            // do nothing
        } else if (updateTypeStr.equals(UPLOAD_KEEP)) {
            // do nothing
        } else if (updateTypeStr.equals(UPLOAD_MODIFY)) {
            // check that a file is actually attached
            try {
                Method getFilesMethod =
                        reqClass.getMethod("getFiles", String.class);
                Method getFileNamesMethod =
                        reqClass.getMethod("getFileNames", String.class);
                Method getFileSystemNamesMethod =
                        reqClass.getMethod("getFileSystemNames", String.class);

                File[] files = (File[])
                        getFilesMethod.invoke(req, inputName);
                String[] fileNames = (String[])
                        getFileNamesMethod.invoke(req, inputName);
                String[] fileSystemNames = (String[])
                        getFileSystemNamesMethod.invoke(req, inputName);

                if (files != null && files.length > 0) {
                    // create a FileUpload from the attachment
                    fileUpload = new FileUpload(
                            files[0],
                            fileNames[0],
                            fileSystemNames[0],
                            null,
                            null
                    );
                }
            } catch (Exception e) {
                throw new Error(e);
            }
        } else if (updateTypeStr.equals(UPLOAD_DELETE)) {
            fileUpload = null;
        }
    }

    public boolean validate() {
        if (mode.isView(immutable) || (mode.isBulk() && !bulkChecked)) {
            return true;
        }

        boolean result = true;
        if (required && (fileUpload == null)) {
            errors.add(getText("elements.error.field.required"));
            result = false;
        }
        return result;
    }

    public void readFromObject(Object obj) {
        super.readFromObject(obj);
        try {
            if (obj == null) {
                fileUpload = null;
            } else {
                fileUpload = (FileUpload) accessor.get(obj);
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public void writeToObject(Object obj) {
        writeToObject(obj, fileUpload);
    }
}
