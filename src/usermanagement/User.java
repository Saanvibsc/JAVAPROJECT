package usermanagement;

public class User {

    private String fname;
    private String lname;
    private String mobile;
    private String email;
    private String location;
    private String username;
    private String password;

    public User(String fname, String lname, String mobile,
                String email, String location,
                String username, String password) {

        this.fname = fname;
        this.lname = lname;
        this.mobile = mobile;
        this.email = email;
        this.location = location;
        this.username = username;
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}