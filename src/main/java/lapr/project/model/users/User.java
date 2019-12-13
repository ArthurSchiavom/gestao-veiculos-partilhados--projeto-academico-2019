package lapr.project.model.users;

import java.util.Objects;

/**
 * Class that represents an user
 */
public abstract class User {
    private final String email, password,username;
    private final UserType type;

    public User(String email, String username, String password, UserType type) {
        this.email = email;
        this.password = password;
        this.type = type;
        this.username = username;
    }

    public UserType getType() {
        return type;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.email);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ",username='" + username+'\''+
                ", password='" + password + '\'' +
                ", type=" + type +
                '}';
    }
}
