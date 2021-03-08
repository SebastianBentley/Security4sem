package dtos;

import entities.User;


public class UserDTO {
    
    private String name;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.name = user.getUserName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
