<%@ page contentType="text/html;charset=ISO-8859-1" language="java"
         pageEncoding="ISO-8859-1"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"
%><%@taglib prefix="mde" uri="/manydesigns-elements"
%><stripes:layout-render name="/skins/${skin}/modal-page.jsp">
    <stripes:layout-component name="customScripts">
        <script src="<stripes:url value="/ace-0.2.0/ace.js" />" type="text/javascript" charset="utf-8"></script>
        <script src="<stripes:url value="/ace-0.2.0/theme-twilight.js" />" type="text/javascript" charset="utf-8"></script>
        <script src="<stripes:url value="/ace-0.2.0/mode-groovy.js" />" type="text/javascript" charset="utf-8"></script>
        <script type="text/javascript">
            window.onload = function() {
                var editor = ace.edit("scriptEditor");
            };
        </script>
    </stripes:layout-component>
    <jsp:useBean id="actionBean" scope="request" type="com.manydesigns.portofino.actions.CrudAction"/>
    <stripes:layout-component name="contentHeader">
        <stripes:submit name="updateConfiguration" value="Update configuration" class="contentButton"/>
        <stripes:submit name="cancel" value="Cancel" class="contentButton"/>
        <div class="breadcrumbs">
            <div class="inner">
                <mde:write name="breadcrumbs"/>
            </div>
        </div>
    </stripes:layout-component>
    <stripes:layout-component name="portletHeader">
        <%@include file="../portlet-common-configuration.jsp" %>
    </stripes:layout-component>
    <stripes:layout-component name="portletBody">
        <mde:write name="actionBean" property="crudConfigurationForm"/>
        <!-- Properties -->
        <fieldset id="crudPropertiesFieldset" class="mde-form-fieldset" style="padding-top: 1em; margin-top: 1em;">
            <legend>Properties</legend>
            <c:if test="${not empty actionBean.propertiesTableForm}">
                <mde:write name="actionBean" property="propertiesTableForm"/>
            </c:if>
            <c:if test="${empty actionBean.propertiesTableForm}">
                You must select a table first.
            </c:if>
        </fieldset>
        <c:if test="${not empty actionBean.propertiesTableForm}">
            <script type="text/javascript">
                var inputs = $("#crudPropertiesFieldset tr").find("td:first input[type=checkbox]");
                inputs.each(function(i, obj) {
                    obj = $(obj);
                    var rowInputs = obj.parent().siblings().find("input");
                    function toggleRow() {
                        if(!obj.is(':checked')) {
                            rowInputs.attr('disabled', 'disabled');
                        } else {
                            rowInputs.removeAttr('disabled');
                        }
                    }
                    obj.click(toggleRow);
                    toggleRow();
                });
            </script>
        </c:if>
        <!-- End properties -->

        <fieldset id="crudSelectionProvidersFieldset" class="mde-form-fieldset"
                  style="padding-top: 1em; margin-top: 1em;">
            <legend>Selection Providers</legend>
            <mde:write name="actionBean" property="selectionProvidersForm"/>
        </fieldset>

        <fieldset id="scriptFieldset" class="mde-form-fieldset"
                  style="position: relative; padding: 0; margin-top: 1em; min-height: 20em;">
            <legend>Script</legend>
            <pre id="scriptEditor" name="script"
                              style="min-height: 20em; width: 100%;">
                <c:out value="${actionBean.script}" />
            </pre>
        </fieldset>

        <input type="hidden" name="cancelReturnUrl" value="<c:out value="${actionBean.cancelReturnUrl}"/>"/>
    </stripes:layout-component>
    <stripes:layout-component name="portletFooter"/>
    <stripes:layout-component name="contentFooter">
        <stripes:submit name="updateConfiguration" value="Update configuration" class="contentButton"/>
        <stripes:submit name="cancel" value="Cancel" class="contentButton"/>
    </stripes:layout-component>
</stripes:layout-render>