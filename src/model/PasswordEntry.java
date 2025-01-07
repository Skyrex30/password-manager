package model;

public class PasswordEntry {
    private String website;
    private String password;

    public PasswordEntry(String website, String password) {
        this.website = website;
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public String getPassword() {
        return password;
    }
}
