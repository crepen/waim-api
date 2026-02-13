package com.waim.core.domain.user.service;


import com.waim.core.common.model.error.WAIMException;
import com.waim.core.common.util.crypto.CryptoProvider;
import com.waim.core.domain.user.model.dto.enumable.UserRole;
import com.waim.core.domain.configure.service.ValidationChecker;
import com.waim.core.domain.user.model.dto.enumable.UserState;
import com.waim.core.domain.user.model.entity.UserEntity;
import com.waim.core.domain.user.model.error.UserErrorCode;
import com.waim.core.domain.user.repoisitory.UserRepository;
import com.waim.core.domain.user.model.dto.AddUserDTO;
import com.waim.core.domain.user.repoisitory.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationChecker validationChecker;
    private final CryptoProvider cryptoProvider;


    public Optional<UserEntity> getUserByUid(String uid){
        return userRepository.findByUid(uid);
    }

    @Transactional
    public void addUser(AddUserDTO addUserDTO) {
        addUser(addUserDTO , UserRole.USER.getValue());
    }

    @Transactional
    public void addUser(AddUserDTO addUserDTO, String ...roles) {
        addUser(addUserDTO , Arrays.stream(roles).toList() );
    }


    @Transactional
    public void addUser(AddUserDTO userDTO , List<String> roles ) {
        if (!StringUtils.hasText(userDTO.getUserId())) {
            throw new WAIMException(UserErrorCode.Validation.USER_ID_EMPTY);
        }

        if (!StringUtils.hasText(userDTO.getUserName())) {
            throw new WAIMException(UserErrorCode.Validation.USER_NAME_EMPTY);
        }

        if (!StringUtils.hasText(userDTO.getPassword())) {
            throw new WAIMException(UserErrorCode.Validation.USER_PASSWORD_EMPTY);
        }

        if(!StringUtils.hasText(userDTO.getEmail())){
            throw new WAIMException(UserErrorCode.Validation.USER_EMAIL_EMPTY);
        }
        else if(!validationChecker.getEmailRegex().matcher(userDTO.getEmail()).matches()){
            throw new WAIMException(UserErrorCode.Validation.USER_EMAIL_INVALID);
        }

        String emailEnc = Sha512DigestUtils.shaHex(userDTO.getEmail());
        List<UserEntity> duplicateUsers = this.getDuplicateUserList(userDTO.getUserName() , userDTO.getUserId() , userDTO.getEmail());

        if(!duplicateUsers.isEmpty()){
            if(duplicateUsers.stream().anyMatch(x->x.getUserId().equals(userDTO.getUserId()))){
                throw new WAIMException(UserErrorCode.Validation.USER_ID_DUPLICATED);
            }
            else if(duplicateUsers.stream().anyMatch(x->x.getUserName().equals(userDTO.getUserName()))){
                throw new WAIMException(UserErrorCode.Validation.USER_NAME_DUPLICATED);
            }
            else if(duplicateUsers.stream().anyMatch(x->x.getUserEmailHash().equals(emailEnc))){
                throw new WAIMException(UserErrorCode.Validation.USER_EMAIL_DUPLICATED);
            }
        }

        UserEntity addUserEntity = UserEntity.builder()
                .userId(userDTO.getUserId())
                .userName(userDTO.getUserName())
                .userPassword(passwordEncoder.encode(userDTO.getPassword()))
                .userEmail(cryptoProvider.encrypt(userDTO.getEmail()))
                .userEmailHash(emailEnc)
                .userState(userDTO.getUserState() == null ? UserState.PENDING : userDTO.getUserState() )
                .build();

        if (!roles.isEmpty()) {
            addUserEntity.addRoles(roles.toArray(new String[0]));
        }
        userRepository.save(addUserEntity);
    }

    @Transactional
    public void removeUser(String userUid){
        if(!StringUtils.hasText(userUid)){
            throw new WAIMException(UserErrorCode.Validation.USER_UID_EMPTY);
        }

        Optional<UserEntity> userEntity = userRepository.findByUid(userUid);
        if(userEntity.isPresent()){
            removeUser(userEntity.get());
        }
        else{
            throw new WAIMException(UserErrorCode.Common.USER_NOT_FOUND);
        }
    }

    @Transactional
    public void removeUser(UserEntity userEntity){

        userEntity.removeAllRoles();
        userRepository.delete(userEntity);
    }


    @Transactional
    public void removeUserRole(String userUid , List<String> roles){
        if(!StringUtils.hasText(userUid)){
            throw new WAIMException(UserErrorCode.Validation.USER_UID_EMPTY);
        }

        Optional<UserEntity> userEntity = userRepository.findByUid(userUid);
        if(userEntity.isPresent()){
            removeUserRole(userEntity.get() , roles);
        }
        else{
            throw new WAIMException(UserErrorCode.Common.USER_NOT_FOUND);
        }
    }

    @Transactional
    public void removeUserRole(UserEntity userEntity, List<String> roles){
        userEntity.removeRoles(String.valueOf(roles));
    }








    /**
     * 중복된 사용자명 , 사용자 ID , 사용자 Email 를 가진 Entity 조회
     * @param name 사용자명
     * @param id 사용자 ID
     * @param emailEnc 사용자 Email (Hash 암호화값)
     * @return 중복된 사용자 Entity List
     */
    public List<UserEntity> getDuplicateUserList( String name , String id , String emailEnc){
        return userRepository.findAll(UserSpecification.duplicateMatchUser(name , id , emailEnc));
    }


    /**
     * 로그인 시 입력한 ID or Email 매칭 사용자 검색
     * 
     * @param idOrEmail 사용자 ID or Email
     * @return 매칭 유저
     */
    public Optional<UserEntity> getLoginUserByIdOrEmail(String idOrEmail){
        String encEmailOrId = Sha512DigestUtils.shaHex(idOrEmail);
        return userRepository.findOne(UserSpecification.loginIdOrEmail(idOrEmail , encEmailOrId));
    }


}
