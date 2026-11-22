package com.cardsystem.smartdoor.service;

import com.cardsystem.smartdoor.dto.Dtos.UserDto;
import com.cardsystem.smartdoor.dto.Dtos.UserRequest;
import com.cardsystem.smartdoor.model.User;
import com.cardsystem.smartdoor.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return users.findAll().stream().map(UserDto::from).toList();
    }

    @Transactional(readOnly = true)
    public UserDto findById(int id) {
        return UserDto.from(get(id));
    }

    public UserDto create(UserRequest req) {
        User u = new User();
        apply(u, req);
        return UserDto.from(users.save(u));
    }

    public UserDto update(int id, UserRequest req) {
        User u = get(id);
        apply(u, req);
        return UserDto.from(u);
    }

    /** Users are deactivated, not deleted, so the audit trail stays intact. */
    public void deactivate(int id) {
        get(id).setActive(false);
    }

    User get(int id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    private static void apply(User u, UserRequest req) {
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setRole(req.role());
    }
}
