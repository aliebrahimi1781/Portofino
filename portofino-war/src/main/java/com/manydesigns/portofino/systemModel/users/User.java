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
package com.manydesigns.portofino.systemModel.users;

import com.manydesigns.elements.annotations.Label;
import com.manydesigns.elements.annotations.Password;
import org.apache.commons.lang.RandomStringUtils;
import sun.misc.BASE64Encoder;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
* @author Angelo Lupo          - angelo.lupo@manydesigns.com
* @author Giampiero Granatella - giampiero.granatella@manydesigns.com
*/
public class User implements Serializable{
    //Dati user_
    Integer uuid;
    String email;
    String pwd;
    Integer state;


    Date delDate;
    Date modifiedDate;
    Date pwdModDate;
    Date loginDate;
    Date lastLoginDate;
    Date lastFailedLoginDate;
    Date lockoutDate;
    Date createDate;

    Boolean defaultUser;
    Boolean extAuth;
    Boolean pwdEncrypted;
    Boolean pwdReset;
    Boolean lockout;
    Boolean agreedToTerms;
    Boolean active;

    String token;
    String remQuestion;
    String remans;
    String screenName;
    String greeting;
    String comments;
    String firstName;
    String middleName;
    String lastName;
    String jobTitle;
    String loginIp;
    String lastLoginIp;

    Integer failedLoginAttempts;
    Integer bounced;
    Integer graceLoginCount;



    //gruppi di appartenenza
    List<UsersGroups> groups = new ArrayList<UsersGroups>();

    public Integer getUuid() {
        return uuid;
    }

    public void setUuid(Integer uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Password
    @Label(value = "password")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public List<UsersGroups> getGroups() {
        return groups;
    }

    public void setGroups(List<UsersGroups> groups) {
        this.groups = groups;
    }

    public Integer getState() {
        //TODO eliminare lo stato fisso
        state=1;
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getDelDate() {
        return delDate;
    }

    public void setDelDate(Date delDate) {
        this.delDate = delDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Boolean getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(Boolean defaultUser) {
            this.defaultUser = defaultUser;
    }

    public Boolean getExtAuth() {
        return extAuth;
    }

    public void setExtAuth(Boolean extAuth) {
        this.extAuth = extAuth;
    }

    public Boolean getPwdEncrypted() {
        return pwdEncrypted;
    }

    public void setPwdEncrypted(Boolean pwdEncrypted) {
        this.pwdEncrypted = pwdEncrypted;
    }

    public Boolean getPwdReset() {
        return pwdReset;
    }

    public void setPwdReset(Boolean pwdReset) {
        this.pwdReset = pwdReset;
    }

    public Date getPwdModDate() {
        return pwdModDate;
    }

    public void setPwdModDate(Date pwdModDate) {
        this.pwdModDate = pwdModDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRemQuestion() {
        return remQuestion;
    }

    public void setRemQuestion(String remQuestion) {
        this.remQuestion = remQuestion;
    }

    public String getRemans() {
        return remans;
    }

    public void setRemans(String remans) {
        this.remans = remans;
    }

    /*public Integer getGraceLoginCount() {
        return graceLoginCount;
    }

    public void setGraceLoginCount(Integer graceLoginCount) {
        this.graceLoginCount = graceLoginCount;
    }*/

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Date getLastFailedLoginDate() {
        return lastFailedLoginDate;
    }

    public void setLastFailedLoginDate(Date lastFailedLoginDate) {
        this.lastFailedLoginDate = lastFailedLoginDate;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Boolean getLockout() {
        return lockout;
    }

    public void setLockout(Boolean lockout) {
        this.lockout = lockout;
    }

    public Date getLockoutDate() {
        return lockoutDate;
    }

    public void setLockoutDate(Date lockoutDate) {
        this.lockoutDate = lockoutDate;
    }

    public Boolean getAgreedToTerms() {
        return agreedToTerms;
    }

    public void setAgreedToTerms(Boolean agreedToTerms) {
        this.agreedToTerms = agreedToTerms;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getGraceLoginCount() {
        return graceLoginCount;
    }

    public void setGraceLoginCount(Integer graceLoginCount) {
        this.graceLoginCount = graceLoginCount;
    }

    public Integer getBounced() {
        return bounced;
    }

    public void setBounced(Integer bounced) {
        this.bounced = bounced;
    }

    public void setPwdEncrypted(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(pwd.getBytes("UTF-8"));
            byte raw[] = md.digest(); //step 4
            setPwd((new BASE64Encoder()).encode(raw));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void setPwdEncrypted() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(pwd.getBytes("UTF-8"));
            byte raw[] = md.digest(); //step 4
            setPwd((new BASE64Encoder()).encode(raw));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public synchronized  void tokenGenerator() {
        setToken(RandomStringUtils.random(30, true, true));
    }

    public synchronized void passwordGenerator(int len) {
        setPwd(RandomStringUtils.random(len, true, true));
    }
}