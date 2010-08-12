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

package com.manydesigns.elements.annotations.impl;

import com.manydesigns.elements.annotations.Status;

import java.lang.annotation.Annotation;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
@SuppressWarnings({"ClassExplicitlyAnnotation"})
public class StatusImpl implements Status {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    private String[] red;
    private String[] amber;
    private String[] green;

    public StatusImpl(String[] red, String[] amber, String[] green) {
        this.red = red;
        this.amber = amber;
        this.green = green;
    }

    public String[] red() {
        return red;
    }

    public String[] amber() {
        return amber;
    }

    public String[] green() {
        return green;
    }

    public Class<? extends Annotation> annotationType() {
        return Status.class;
    }
}