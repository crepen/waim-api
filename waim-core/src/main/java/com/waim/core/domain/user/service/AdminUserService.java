package com.waim.core.domain.user.service;

import com.waim.core.domain.user.model.UserRole;
import com.waim.core.domain.user.model.UserState;
import com.waim.core.domain.user.model.dto.AddUserDTO;
import com.waim.core.domain.user.repoisitory.UserRepository;
import com.waim.core.domain.user.repoisitory.UserSpecification;
import com.waim.core.domain.user.model.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public boolean isExistAdminRoleUser (){
        return userRepository.exists(UserSpecification.hasRole(UserRole.ADMIN));
    }


    @Transactional
    public void addAdminUserAccount(){



        AddUserDTO addUserDTO = AddUserDTO.builder()
                .userName("Administrator")
                .userId("root")
                .password("qwer1234")
                .email("admin@admin.com")
                .userState(UserState.ACTIVE)
                .build();

        userService.addUser(addUserDTO , UserRole.ADMIN.getValue());
    }

    public List<UserEntity> getAdminUserAccount(){
        return userRepository.findAll(UserSpecification.hasRole(UserRole.ADMIN));
    }
}
