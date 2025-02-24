package com.lambdaschool.todos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

// User is considered the parent entity

@Entity
@Table(name = "users")
public class User extends Auditable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userid;

    @Column(nullable = false,
            unique = true)
    private String username;

    @Column(nullable = false)
    //this line is so that no one can see the password
    //so it NEVER prints it out in json response
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false,
            unique = true)
    @Email //this annotations validates the email
    private String primaryemail;

    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<UserRoles> userroles = new ArrayList<>();

    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @JsonIgnoreProperties("user")
    private List<Todo> todos = new ArrayList<>();

    public User()
    {
    }

    public User(String username,
                String password,
                String primaryemail,
                List<UserRoles> userRoles)
    {
        setUsername(username);
        setPassword(password);
        this.primaryemail = primaryemail;
        for (UserRoles ur : userRoles)
        {
            ur.setUser(this);
        }
        this.userroles = userRoles;
    }

    public long getUserid()
    {
        return userid;
    }

    public void setUserid(long userid)
    {
        this.userid = userid;
    }

    public String getUsername()
    {
        if (username == null) // this is possible when updating a user
        {
            return null;
        } else
        {
            return username.toLowerCase();
        }
    }

    public void setUsername(String username)
    {
        this.username = username.toLowerCase();
    }

    public String getPrimaryemail()
    {
        return primaryemail;
    }

    public void setPrimaryemail(String primaryemail)
    {
        this.primaryemail = primaryemail;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void setPasswordNoEncrypt(String password)
    {
        this.password = password;
    }

    public List<UserRoles> getUserroles()
    {
        return userroles;
    }

    public void setUserroles(List<UserRoles> userroles)
    {
        this.userroles = userroles;
    }

    @JsonIgnore
    public List<SimpleGrantedAuthority> getAuthority()
    {
        List<SimpleGrantedAuthority> rtnList = new ArrayList<>();

        for (UserRoles r : this.userroles)
        {
            String myRole = "ROLE_" + r.getRole()
                                       .getName()
                                       .toUpperCase();
            rtnList.add(new SimpleGrantedAuthority(myRole));
        }

        return rtnList;
    }

    @Override
    public String toString()
    {
        return "User{" + "userid=" + userid + ", username='" + username + '\'' + ", password='" + password + '\'' + ", primaryEmail='" + primaryemail + '\'' + ", userroles=" + userroles + '}';
    }
}
