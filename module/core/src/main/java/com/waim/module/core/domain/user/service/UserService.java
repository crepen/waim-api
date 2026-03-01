package com.waim.module.core.domain.user.service;

import com.waim.module.core.domain.auth.model.error.AuthForbiddenException;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.model.error.*;
import com.waim.module.core.domain.user.repository.UserRepository;
import com.waim.module.data.domain.user.AddUserProp;
import com.waim.module.data.domain.user.FindUserProp;
import com.waim.module.data.domain.user.RemoveUserProp;
import com.waim.module.data.domain.user.UserStatus;
import com.waim.module.util.crypto.CryptoProvider;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CryptoProvider cryptoProvider;
    private final PasswordEncoder passwordEncoder;


    // region Runtime method

    // endregion



    // region Base method

    @Transactional
    public List<UserEntity> getUser(
        String userUid
    ){
        Specification<UserEntity> spec = (root , query , cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("uid") , userUid)
            );

            return cb.and(predicates);
        };

        return userRepository.findAll(spec);
    }

    // endregion




    @Transactional
    public Optional<UserEntity> findUser(String uid){
        return userRepository.findOne(((root, query, cb) -> cb.equal(root.get("uid"), uid)));
    }


    @Transactional
    public Optional<UserEntity> findActiveUser(String uid){

        if(!StringUtils.hasText(uid)){
            return Optional.empty();
        }

        return userRepository.findOne(
                (root, query, cb) -> {
                    return cb.and(
                            cb.equal(root.get("userStatus") , UserStatus.ACTIVE),
                            cb.equal(root.get("uid") , uid)
                    );
                }
        );
    }

    @Transactional
    public Optional<UserEntity> findActiveUserByIdOrEmail(String idOrEmail){

        if(!StringUtils.hasText(idOrEmail)){
            return Optional.empty();
        }

        return userRepository.findOne(
                (root, query, cb) -> {

                    String emailHash = cryptoProvider.staticHash(idOrEmail);

                    return cb.and(
                            cb.equal(root.get("userStatus") , UserStatus.ACTIVE),
                            cb.or(
                                    cb.equal(root.get("userId") , idOrEmail),
                                    cb.equal(root.get("userEmailHash") , emailHash)
                            )
                    );
                }
        );
    }

    @Transactional
    public void addUser(AddUserProp addUserProp){
        if(!StringUtils.hasText(addUserProp.getId())){
            // Empty ID
            throw new UserEmptyIdException();
        }

        if(!StringUtils.hasText(addUserProp.getName())){
            // Empty Name
            throw new UserEmptyNameException();
        }

        if(!StringUtils.hasText(addUserProp.getPassword())){
            // Empty Password
            throw new UserEmptyPasswordException();
        }

        if(!StringUtils.hasText(addUserProp.getEmail())){
            // Empty Email
            throw new UserEmptyEmailException();
        }

        String emailHash = cryptoProvider.staticHash(addUserProp.getEmail());

        List<UserEntity> duplicateList = getDuplicateUserList(
                addUserProp.getId(),
                addUserProp.getName(),
                emailHash
        );

        if(!duplicateList.isEmpty()){
            if(duplicateList.stream().anyMatch(x->x.getUserId().equals(addUserProp.getId()))){
                // Duplicate Id
                throw new UserDuplicateIdException();
            }

            if(duplicateList.stream().anyMatch(x->x.getUserName().equals(addUserProp.getName()))){
                // Duplicate Name
                throw new UserDuplicateNameException();
            }

            if(duplicateList.stream().anyMatch(x->x.getUserEmailHash().equals(emailHash))){
                // Duplicate Email
                throw new UserDuplicateEmailException();
            }
        }



        UserEntity addUserEntity = UserEntity.builder()
                .userId(addUserProp.getId())
                .userName(addUserProp.getName())
                .userPassword(passwordEncoder.encode(addUserProp.getPassword()))
                .userEmail(addUserProp.getEmail())
                .userEmailHash(emailHash)
                .userStatus(addUserProp.getStatus())
                .userRole(addUserProp.getRole())
                .build();

        userRepository.save(addUserEntity);
    }


    @Transactional
    public void removeUser(RemoveUserProp removeProp){

        if(!StringUtils.hasText(removeProp.getUserUid())){
            throw new UserEmptyUidException();
        }

        if(!removeProp.getUserUid().equals(removeProp.getActionUserUid()) && !removeProp.isAdmin()){
            throw new AuthForbiddenException();
        }

        Specification<UserEntity> spec = (root , query , cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(
                    cb.equal(root.get("uid") , removeProp.getUserUid())
            );

            return cb.and(predicates);
        };

        Optional<UserEntity> findUser = userRepository.findOne(spec);

        if(findUser.isEmpty()){
            throw new UserNotFoundException();
        }

        if(findUser.get().getUserStatus() == UserStatus.DELETE){
            throw new UserAlreadyDeleteException();
        }

        findUser.get().setUserStatus(UserStatus.DELETE);

        userRepository.save(findUser.get());
    }

    @Transactional
    public void updateUserPassword(String userUid){

        if(!StringUtils.hasText(userUid)){
            throw new UserEmptyUidException();
        }

        Optional<UserEntity> user = userRepository.findByUid(userUid);
    }

    @Transactional
    public void updateUserPassword(UserEntity userEntity , String password){

        if(userEntity == null || !StringUtils.hasText(userEntity.getUid())){
            throw new UserEmptyUidException();
        }

        userEntity.setUserPassword(passwordEncoder.encode(password));
        userRepository.save(userEntity);
    }

    private List<UserEntity> getDuplicateUserList (String id , String name , String emailHash) {
        Specification<UserEntity> spec = (
                (root, query, cb) -> {
                    query.distinct(true);

                    List<Predicate> predicateList = new ArrayList<>();

                    predicateList.add(
                            cb.equal(root.get("userId"), id)
                    );

                    // Check Duplicate UserName
                    predicateList.add(
                            cb.equal(root.get("userName"), name)
                    );

                    // Check Duplicate UserEmailHash
                    predicateList.add(
                            cb.equal(root.get("userEmailHash"), emailHash)
                    );

                    return cb.or(predicateList);
                }
        );

        return userRepository.findAll(spec);
    }
}
