package com.example.user_service.users;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return userService.getUsers(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(@PathVariable Long id)
    {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest)
    {
        var response = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}