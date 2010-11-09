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

package com.manydesigns.portofino.model.usecases;

import com.manydesigns.portofino.model.ModelObject;
import com.manydesigns.portofino.model.Model;
import com.manydesigns.portofino.xml.XmlAttribute;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class Button implements ModelObject {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    //**************************************************************************
    // Fields
    //**************************************************************************

    protected final UseCase useCase;

    protected String name;
    protected String label;

    //**************************************************************************
    // Constructors
    //**************************************************************************

    public Button(UseCase useCase) {
        this.useCase = useCase;
    }

    public Button(UseCase useCase, String name, String label) {
        this(useCase);
        this.name = name;
        this.label = label;
    }

    //**************************************************************************
    // ModelObject implementation
    //**************************************************************************

    public void reset() {}

    public void init(Model model) {}

    public String getQualifiedName() {
        return String.format("%s*%s", useCase.getQualifiedName(), name);
    }

    //**************************************************************************
    // Getters/setters
    //**************************************************************************

    public UseCase getUseCase() {
        return useCase;
    }

    @XmlAttribute(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(required = true)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}