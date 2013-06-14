<%@ page import="com.manydesigns.portofino.dispatcher.Dispatch"
%><%@ page import="com.manydesigns.portofino.dispatcher.DispatcherUtil"
%><%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes-dynattr.tld"
%><%@ taglib prefix="mde" uri="/manydesigns-elements"
%><stripes:layout-definition><%--
--%><!doctype html>
    <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    <head>
        <jsp:include page="head.jsp"/>
        <stripes:layout-component name="customScripts"/>
        <jsp:useBean id="actionBean" scope="request" type="net.sourceforge.stripes.action.ActionBean" />
        <%
            Dispatch dispatch = DispatcherUtil.getDispatch(request, actionBean);
            pageContext.setAttribute("dispatch", dispatch);
        %>
        <title><c:out value="${dispatch.lastPageInstance.page.description}"/></title>
    </head>
    <body class="yui-skin-sam">
    <div id="doc3" class="yui-t2">
        <div id="bd">
            <div id="yui-main">
                <div id="content" class="yui-b" style="margin-left: 0;">
                    <c:if test="${empty formActionUrl}">
                        <c:set var="formActionUrl" value="${dispatch.originalPath}" />
                    </c:if>
                    <stripes:form action="${formActionUrl}" method="post" enctype="multipart/form-data">
                        <div class="row-fluid">
                            <stripes:layout-component name="contentHeader">
                                Portlet page header
                            </stripes:layout-component>
                        </div>
                        <div class="row-fluid">
                            <div class="portletWrapper">
                                <div class="portlet">
                                    <mde:sessionMessages/>
                                    <div class="portletHeader">
                                        <stripes:layout-component name="portletHeader">
                                            <div>
                                                <div class="portletTitle">
                                                    <h1>
                                                    <stripes:layout-component name="portletTitle">
                                                        portletTitle
                                                    </stripes:layout-component>
                                                    </h1>
                                                </div>
                                                <div class="pull-right">
                                                    <stripes:layout-component name="portletHeaderButtons" />
                                                </div>
                                            </div>
                                            <div class="portletHeaderSeparator"></div>
                                        </stripes:layout-component>
                                    </div>
                                    <div class="portletBody">
                                        <stripes:layout-component name="portletBody">
                                            Portlet body
                                        </stripes:layout-component>
                                    </div>
                                    <div class="portletFooter">
                                        <stripes:layout-component name="portletFooter">
                                        </stripes:layout-component>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row-fluid">
                            <stripes:layout-component name="contentFooter">
                                Portlet page footer
                            </stripes:layout-component>
                        </div>
                    </stripes:form>
                </div>
            </div>
        </div>
    </div>
    </body>
    </html>
</stripes:layout-definition>