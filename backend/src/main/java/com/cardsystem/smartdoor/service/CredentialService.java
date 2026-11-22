package com.cardsystem.smartdoor.service;

import com.cardsystem.smartdoor.dto.Dtos.CredentialDto;
import com.cardsystem.smartdoor.dto.Dtos.CredentialRequest;
import com.cardsystem.smartdoor.model.Credential;
import com.cardsystem.smartdoor.repository.CredentialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Fingerprints and cards are enrolled on the lock itself (templates never leave it);
 * this service only records which slot index belongs to which user.
 */
@Service
@Transactional
public class CredentialService {

    private final CredentialRepository credentials;
    private final UserService userService;

    public CredentialService(CredentialRepository credentials, UserService userService) {
        this.credentials = credentials;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<CredentialDto> findByUser(int userId) {
        return credentials.findByUserId(userId).stream().map(CredentialDto::from).toList();
    }

    public CredentialDto create(CredentialRequest req) {
        Credential c = new Credential();
        c.setUser(userService.get(req.userId()));
        c.setType(req.type());
        c.setIndex(req.index());
        c.setCardUid(req.cardUid());
        return CredentialDto.from(credentials.save(c));
    }

    public void delete(int id) {
        if (!credentials.existsById(id)) {
            throw new NotFoundException("Credential " + id + " not found");
        }
        credentials.deleteById(id);
    }
}
