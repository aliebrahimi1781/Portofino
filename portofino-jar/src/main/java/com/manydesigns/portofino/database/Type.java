/*
 * Copyright (C) 2005-2011 ManyDesigns srl.  All rights reserved.
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

package com.manydesigns.portofino.database;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
* @author Alessio Stalla       - alessio.stalla@manydesigns.com
*/
public class Type {
    public static final String copyright =
            "Copyright (c) 2005-2011, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final String typeName;
    protected final int jdbcType;
    protected final boolean autoincrement;
    protected final int maximumPrecision;
    protected final String literalPrefix;
    protected final String literalSuffix;
    protected final boolean nullable;
    protected final boolean caseSensitive;
    protected final boolean searchable;
    protected final short minimumScale;
    protected final short maximumScale;
    protected final Boolean precisionRequired;
    protected final Boolean scaleRequired;


    //**************************************************************************
    // Constructors
    //**************************************************************************

    public Type(String typeName, int jdbcType, int maximumPrecision,
                String literalPrefix, String literalSuffix,
                boolean nullable, boolean caseSensitive, boolean searchable,
                boolean autoincrement, short minimumScale, short maximumScale) {
        this.typeName = typeName;
        this.jdbcType = jdbcType;
        this.maximumPrecision = maximumPrecision;
        this.literalPrefix = literalPrefix;
        this.literalSuffix = literalSuffix;
        this.nullable = nullable;
        this.caseSensitive = caseSensitive;
        this.searchable = searchable;
        this.autoincrement = autoincrement;
        this.minimumScale = minimumScale;
        this.maximumScale = maximumScale;

        this.precisionRequired = null;
        this.scaleRequired = null;

    }

    public Type(String typeName, int jdbcType, int maximumPrecision,
                String literalPrefix, String literalSuffix,
                boolean nullable, boolean caseSensitive, boolean searchable,
                boolean autoincrement, short minimumScale, short maximumScale,
                boolean precisionRequired, boolean scaleRequired) {
        this.typeName = typeName;
        this.jdbcType = jdbcType;
        this.maximumPrecision = maximumPrecision;
        this.literalPrefix = literalPrefix;
        this.literalSuffix = literalSuffix;
        this.nullable = nullable;
        this.caseSensitive = caseSensitive;
        this.searchable = searchable;
        this.autoincrement = autoincrement;
        this.minimumScale = minimumScale;
        this.maximumScale = maximumScale;

        this.precisionRequired = precisionRequired;
        this.scaleRequired = scaleRequired;

    }

    //**************************************************************************
    // Getters/setter
    //**************************************************************************

    public String getTypeName() {
        return typeName;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public Class getDefaultJavaType() {
        return getDefaultJavaType(jdbcType);
    }

    public static Class getDefaultJavaType(int jdbcType) {
        switch (jdbcType) {
            case Types.BIGINT:
                return Long.class;
            case Types.BIT:
                return Boolean.class;
            case Types.BOOLEAN:
                return Boolean.class;
            case Types.CHAR:
                return String.class;
            case Types.VARCHAR:
                return String.class;
            case Types.DATE:
                return java.sql.Date.class;
            case Types.TIME:
                return java.sql.Time.class;
            case Types.TIMESTAMP:
                return Date.class;
            case Types.DECIMAL:
                return BigDecimal.class;
            case Types.NUMERIC:
                return BigDecimal.class;
            case Types.DOUBLE:
                return Double.class;
            case Types.REAL:
                return Double.class;
            case Types.FLOAT:
                return Float.class;
            case Types.INTEGER:
                return Integer.class;
            case Types.SMALLINT:
                return Short.class;
            case Types.TINYINT:
                return Byte.class;
            case Types.BINARY:
                return byte[].class;
            case Types.BLOB:
                 return java.sql.Blob.class;
            case Types.CLOB:
                return java.sql.Clob.class;
            case Types.LONGVARBINARY:
                return byte[].class;
            case Types.LONGVARCHAR:
                return java.lang.String.class;
            case Types.VARBINARY:
                return byte[].class;
            case Types.ARRAY:
                return java.sql.Array.class;
            case Types.DATALINK:
                return java.net.URL.class;
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
                return Object.class;
            case Types.NULL:
            case Types.OTHER:
            case Types.REF:
                return java.sql.Ref.class;
            case Types.STRUCT:
                return java.sql.Struct.class;
            default:
                throw new Error("Unsupported type: " + jdbcType);
        }
    }


    public boolean isAutoincrement() {
        return autoincrement;
    }

    public int getMaximumPrecision() {
        return maximumPrecision;
    }

    public String getLiteralPrefix() {
        return literalPrefix;
    }

    public String getLiteralSuffix() {
        return literalSuffix;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public short getMinimumScale() {
        return minimumScale;
    }

    public short getMaximumScale() {
        return maximumScale;
    }

    @Override
    public String toString() {
        return "Type{" +
                "typeName='" + typeName + '\'' +
                ", jdbcType=" + jdbcType +
                ", autoincrement=" + autoincrement +
                ", maximumPrecision=" + maximumPrecision +
                ", literalPrefix='" + literalPrefix + '\'' +
                ", literalSuffix='" + literalSuffix + '\'' +
                ", nullable=" + nullable +
                ", caseSensitive=" + caseSensitive +
                ", searchable=" + searchable +
                ", minimumScale=" + minimumScale +
                ", maximumScale=" + maximumScale +
                '}';
    }

    public boolean isPrecisionRequired() {
        if (precisionRequired!= null){
            return precisionRequired;
        }
        switch (jdbcType) {
            case Types.BIGINT:
                return false;
            case Types.BIT:
                return false;
            case Types.BOOLEAN:
                return false;
            case Types.CHAR:
                return true;
            case Types.VARCHAR:
                return true;
            case Types.DATE:
                return false;
            case Types.TIME:
                return false;
            case Types.TIMESTAMP:
                return false;
            case Types.DECIMAL:
                return true;
            case Types.NUMERIC:
                return true;
            case Types.DOUBLE:
                return false;
            case Types.REAL:
                return false;
            case Types.FLOAT:
                return false;
            case Types.INTEGER:
                return false;
            case Types.SMALLINT:
                return false;
            case Types.TINYINT:
                return false;
            case Types.BINARY:
                return false;
            case Types.BLOB:
                 return false;
            case Types.CLOB:
                return false;
            case Types.LONGVARBINARY:
                return false;
            case Types.LONGVARCHAR:
                return false;
            case Types.VARBINARY:
                return false;
            case Types.ARRAY:
                return false;
            case Types.DATALINK:
                return false;
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
                return false;
            case Types.NULL:
            case Types.OTHER:
            case Types.REF:
                return false;
            case Types.STRUCT:
                return false;
            default:
                return false;
        }

    }

    public boolean isScaleRequired() {
        if (scaleRequired!= null){
            return scaleRequired;
        }
        switch (jdbcType) {
            case Types.BIGINT:
                return false;
            case Types.BIT:
                return false;
            case Types.BOOLEAN:
                return false;
            case Types.CHAR:
                return false;
            case Types.VARCHAR:
                return false;
            case Types.DATE:
                return false;
            case Types.TIME:
                return false;
            case Types.TIMESTAMP:
                return false;
            case Types.DECIMAL:
                return true;
            case Types.NUMERIC:
                return true;
            case Types.DOUBLE:
                return false;
            case Types.REAL:
                return false;
            case Types.FLOAT:
                return false;
            case Types.INTEGER:
                return false;
            case Types.SMALLINT:
                return false;
            case Types.TINYINT:
                return false;
            case Types.BINARY:
                return false;
            case Types.BLOB:
                 return false;
            case Types.CLOB:
                return false;
            case Types.LONGVARBINARY:
                return false;
            case Types.LONGVARCHAR:
                return false;
            case Types.VARBINARY:
                return false;
            case Types.ARRAY:
                return false;
            case Types.DATALINK:
                return false;
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
                return false;
            case Types.NULL:
            case Types.OTHER:
            case Types.REF:
                return false;
            case Types.STRUCT:
                return false;
            default:
                return false;
        }
    }
}
