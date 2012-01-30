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

package com.manydesigns.portofino.pageactions.timesheet.model;

import org.joda.time.DateMidnight;

/**
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
public class Person {
    public static final String copyright =
            "Copyright (c) 2005-2011, ManyDesigns srl";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    final String id;
    String longName;
    DateMidnight firstDay;
    DateMidnight lastDay;
    boolean me;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public Person(String id) {
        this.id = id;
    }

    public Person(String id, String longName, DateMidnight firstDay, DateMidnight lastDay, boolean me) {
        this.id = id;
        this.longName = longName;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.me = me;
    }

    //--------------------------------------------------------------------------
    // Getters/setters
    //--------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public DateMidnight getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(DateMidnight firstDay) {
        this.firstDay = firstDay;
    }

    public DateMidnight getLastDay() {
        return lastDay;
    }

    public void setLastDay(DateMidnight lastDay) {
        this.lastDay = lastDay;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }
}
