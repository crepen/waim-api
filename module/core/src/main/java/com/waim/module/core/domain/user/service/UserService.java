package com.waim.module.core.domain.user.service;

import com.waim.module.core.domain.auth.model.error.AuthForbiddenException;
import com.waim.module.core.domain.user.model.entity.UserAttributeEntity;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.model.error.*;
import com.waim.module.core.domain.user.repository.UserRepository;
import com.waim.module.core.system.config.service.SystemConfigService;
import com.waim.module.data.domain.user.*;
import com.waim.module.data.domain.user.prop.AddUserProp;
import com.waim.module.data.domain.user.prop.RemoveUserProp;
import com.waim.module.data.domain.user.prop.UpdateUserProp;
import com.waim.module.data.system.config.SystemConfigKey;
import com.waim.module.util.crypto.CryptoProvider;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private static final String LOWERCASE_CHARSET = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBER_CHARSET = "0123456789";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final int TEMP_PASSWORD_MAX_GENERATE_ATTEMPT = 100;
    private static final String DEFAULT_USER_SIGNUP_ENABLE = "yes";
    private static final String DEFAULT_USER_SIGNUP_REQUIRE_ADMIN_APPROVAL = "yes";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIREMENT = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE = "yes";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL = "no";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_NUMBER = "no";
    private static final int DEFAULT_USER_SIGNUP_PASSWORD_MIN_LENGTH = 8;
    private static final int DEFAULT_USER_SIGNUP_PASSWORD_MAX_LENGTH = 64;

    private final UserRepository userRepository;
    private final SystemConfigService systemConfigService;
    private final CryptoProvider cryptoProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserAttributeService userAttributeService;
    private final Random random = new java.security.SecureRandom();


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

    @Transactional
    public Page<UserEntity> searchUsers(String keyword, String status, Pageable pageable) {
        Specification<UserEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.notEqual(root.get("userStatus"), UserStatus.DELETE));

            if (StringUtils.hasText(keyword)) {
                String normalizedKeyword = keyword.trim();
                String likeKeyword = "%" + normalizedKeyword + "%";
                String emailHash = cryptoProvider.staticHash(normalizedKeyword);

                predicates.add(
                        cb.or(
                                cb.like(root.get("userId"), likeKeyword),
                                cb.equal(root.get("userEmailHash"), emailHash)
                        )
                );
            }

            if (StringUtils.hasText(status)) {
                try {
                    UserStatus matchStatus = UserStatus.valueOf(status.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("userStatus"), matchStatus));
                }
                catch (IllegalArgumentException ignored) {
                    // Ignore invalid status filter value
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable);
    }

    // endregion




    @Transactional
    public Optional<UserEntity> findUser(String uid){
        return userRepository.findOne((root, query, cb) -> cb.and(
                cb.equal(root.get("uid"), uid),
                cb.notEqual(root.get("userStatus"), UserStatus.DELETE)
        ));
    }

    @Transactional
    public Optional<UserEntity> findUserById(String userId) {
        if (!StringUtils.hasText(userId)) {
            return Optional.empty();
        }

        return userRepository.findOne((root, query, cb) -> cb.and(
                cb.equal(root.get("userId"), userId),
                cb.notEqual(root.get("userStatus"), UserStatus.DELETE)
        ));
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

        validatePasswordByPolicy(addUserProp.getPassword());

        if(!StringUtils.hasText(addUserProp.getEmail())){
            // Empty Email
            throw new UserEmptyEmailException();
        }

        if (!isValidEmail(addUserProp.getEmail())) {
            throw new UserInvalidEmailException();
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



        UserRole targetRole = addUserProp.getRole() == null ? UserRole.GENERAL : addUserProp.getRole();

        if (targetRole == UserRole.GENERAL && !isSignupEnabled()) {
            throw new UserSignupDisabledException();
        }

        UserStatus targetStatus = addUserProp.getStatus();

        if (targetRole == UserRole.GENERAL) {
            targetStatus = isSignupRequireAdminApproval() ? UserStatus.INACTIVE : UserStatus.ACTIVE;
        }

        if (targetStatus == null) {
            targetStatus = UserStatus.ACTIVE;
        }

        UserEntity addUserEntity = UserEntity.builder()
                .userId(addUserProp.getId())
                .userName(addUserProp.getName())
                .userPassword(passwordEncoder.encode(addUserProp.getPassword()))
                .userEmail(addUserProp.getEmail())
                .userEmailHash(emailHash)
                .userStatus(targetStatus)
                .userRole(targetRole)
                .build();

        userRepository.save(addUserEntity);
    }


    @Transactional
    public void updateUser(UpdateUserProp updateProp){
        if(!StringUtils.hasText(updateProp.getUserUid())){
            throw new UserEmptyUidException();
        }

        Optional<UserEntity> matchUser = userRepository.findByUid(updateProp.getUserUid());

        if(matchUser.isEmpty()){
            throw new UserNotFoundException();
        }

        UserEntity userEntity = matchUser.get();

        // Update Password
        if(StringUtils.hasText(updateProp.getPassword())){
            validatePasswordByPolicy(updateProp.getPassword());
            userEntity.setUserPassword(passwordEncoder.encode(updateProp.getPassword()));
        }

        // Update email
        if(StringUtils.hasText(updateProp.getEmail())){
            if (!isValidEmail(updateProp.getEmail())) {
                throw new UserInvalidEmailException();
            }

            userEntity.setUserEmail(updateProp.getEmail());
            userEntity.setUserEmailHash(cryptoProvider.staticHash(updateProp.getEmail()));
        }

        // Update name
        if(StringUtils.hasText(updateProp.getName())){
            userEntity.setUserName(updateProp.getName());
        }

        // Update role
        if(StringUtils.hasText(updateProp.getRole())){

            UserRole userRole;

            try{
                userRole = Enum.valueOf(UserRole.class, updateProp.getRole().toUpperCase());
            }
            catch (Exception ex) {
                throw new UserUnsupportedRoleException();
            }


            userEntity.setUserRole(userRole);
        }

        // Update user config
        if(updateProp.getConfig() != null && !updateProp.getConfig().isEmpty()){

            List<String> matchProjectList = userAttributeService.getProtectedKeys().stream().filter(
                    x-> !updateProp.getConfig().keySet().stream().filter(
                            y -> y.toUpperCase().equals(x)
                    ).toList().isEmpty()
            ).toList();

            if(!matchProjectList.isEmpty()){
                throw new UserProtectedAttributeException(matchProjectList.getFirst().toUpperCase());
            }

                List<UserAttributeEntity> userAttrList = userAttributeService.getConfigs(
                    userEntity.getUid(),
                    updateProp.getConfig().keySet().stream().map(String::toUpperCase).toList()
                );

            for(String key : updateProp.getConfig().keySet()){
                UserAttributeEntity matchAttr = userAttrList.stream().filter(x->x.getAttrKey().equals(key.toUpperCase()))
                        .findFirst().orElse(
                                UserAttributeEntity.builder()
                                        .attrKey(key.toUpperCase())
                                        .build()
                        );

                matchAttr.setAttrValue(updateProp.getConfig().get(key));

                userEntity.addAttribute(matchAttr);
            }
        }

        userRepository.save(userEntity);
    }

    @Transactional
    public void approveUser(String userUid) {
        UserEntity userEntity = getMutableUser(userUid);
        userEntity.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(userEntity);
    }

    @Transactional
    public void blockUser(String userUid) {
        UserEntity userEntity = getMutableUser(userUid);
        userEntity.setUserStatus(UserStatus.BLOCK);
        userRepository.save(userEntity);
    }

    @Transactional
    public void softDeleteUser(String userUid) {
        UserEntity userEntity = getMutableUser(userUid);

        if (userEntity.getUserStatus() == UserStatus.DELETE) {
            throw new UserAlreadyDeleteException();
        }

        userEntity.setUserStatus(UserStatus.DELETE);
        userRepository.save(userEntity);
    }

    private UserEntity getMutableUser(String userUid) {
        if (!StringUtils.hasText(userUid)) {
            throw new UserEmptyUidException();
        }

        Optional<UserEntity> matchUser = userRepository.findByUid(userUid);

        if (matchUser.isEmpty() || matchUser.get().getUserStatus() == UserStatus.DELETE) {
            throw new UserNotFoundException();
        }

        return matchUser.get();
    }

    private boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email.trim()).matches();
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

    @Transactional
    public String resetPasswordByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new UserEmptyEmailException();
        }

        String emailHash = cryptoProvider.staticHash(email.trim());

        Optional<UserEntity> userOpt = userRepository.findOne(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("userEmailHash"), emailHash),
                        cb.notEqual(root.get("userStatus"), UserStatus.DELETE)
                )
        );

        if (userOpt.isEmpty()) {
            throw new UserNotFoundException();
        }

        String temporaryPassword = generateTemporaryPasswordByPolicy();
        updateUserPassword(userOpt.get(), temporaryPassword);
        return temporaryPassword;
    }

    @Transactional
    public String generateTemporaryPasswordByPolicy() {
        int minLength = getIntConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_MIN_LENGTH.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_MIN_LENGTH
        );

        int maxLength = getIntConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_MAX_LENGTH.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_MAX_LENGTH
        );

        if (maxLength < minLength) {
            maxLength = minLength;
        }

        int targetLength = minLength;

        boolean requireUppercase = getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE
        );

        boolean requireNumber = getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_NUMBER.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_NUMBER
        );

        boolean requireSymbol = getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL
        );

        String allowedSymbols = systemConfigService.getConfig(
                        SystemConfigKey.USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS.name()
                )
                .map(config -> config.getConfigValue())
                .filter(StringUtils::hasText)
                .orElse(DEFAULT_USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS);

        for (int attempt = 0; attempt < TEMP_PASSWORD_MAX_GENERATE_ATTEMPT; attempt++) {
            List<Character> passwordChars = new ArrayList<>();
            passwordChars.add(randomChar(LOWERCASE_CHARSET));

            String candidatePool = LOWERCASE_CHARSET + UPPERCASE_CHARSET + NUMBER_CHARSET;

            if (requireUppercase) {
                passwordChars.add(randomChar(UPPERCASE_CHARSET));
            }

            if (requireNumber) {
                passwordChars.add(randomChar(NUMBER_CHARSET));
            }

            if (requireSymbol && StringUtils.hasText(allowedSymbols)) {
                passwordChars.add(randomChar(allowedSymbols));
                candidatePool += allowedSymbols;
            }

            if (!StringUtils.hasText(candidatePool)) {
                candidatePool = LOWERCASE_CHARSET + UPPERCASE_CHARSET + NUMBER_CHARSET;
            }

            while (passwordChars.size() < targetLength) {
                passwordChars.add(randomChar(candidatePool));
            }

            Collections.shuffle(passwordChars, random);

            StringBuilder passwordBuilder = new StringBuilder(passwordChars.size());
            passwordChars.forEach(passwordBuilder::append);
            String password = passwordBuilder.toString();

            try {
                validatePasswordByPolicy(password);
                return password;
            }
            catch (UserInvalidPasswordPolicyException ignored) {
                // Retry until a valid temporary password is generated.
            }
        }

        throw new UserInvalidPasswordPolicyException();
    }

    private char randomChar(String source) {
        return source.charAt(random.nextInt(source.length()));
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

    private boolean isSignupEnabled() {
        return getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_ENABLED.name(),
                DEFAULT_USER_SIGNUP_ENABLE
        );
    }

    private boolean isSignupRequireAdminApproval() {
        return getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_REQUIRE_ADMIN_APPROVAL.name(),
                DEFAULT_USER_SIGNUP_REQUIRE_ADMIN_APPROVAL
        );
    }

    private boolean getBooleanConfigValue(String key, String defaultValue) {
        String rawValue = systemConfigService.getConfig(key)
                .map(config -> config.getConfigValue())
                .orElse(defaultValue);

        if (!StringUtils.hasText(rawValue)) {
            rawValue = defaultValue;
        }

        return "yes".equalsIgnoreCase(rawValue)
                || "true".equalsIgnoreCase(rawValue)
                || "y".equalsIgnoreCase(rawValue)
                || "1".equals(rawValue);
    }

    private void validatePasswordByPolicy(String password) {
        boolean hasStructuredPolicy = systemConfigService.getConfig(SystemConfigKey.USER_SIGNUP_PASSWORD_MIN_LENGTH.name()).isPresent()
            || systemConfigService.getConfig(SystemConfigKey.USER_SIGNUP_PASSWORD_MAX_LENGTH.name()).isPresent()
            || systemConfigService.getConfig(SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE.name()).isPresent()
            || systemConfigService.getConfig(SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL.name()).isPresent()
            || systemConfigService.getConfig(SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_NUMBER.name()).isPresent();

        boolean requireUppercase = getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE
        );

        boolean requireSymbol = getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL
        );

        boolean requireNumber = getBooleanConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_NUMBER.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_NUMBER
        );

        int minLength = getIntConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_MIN_LENGTH.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_MIN_LENGTH
        );

        int maxLength = getIntConfigValue(
                SystemConfigKey.USER_SIGNUP_PASSWORD_MAX_LENGTH.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_MAX_LENGTH
        );

        if (maxLength < minLength) {
            maxLength = minLength;
        }

        String allowedSymbols = systemConfigService.getConfig(
                        SystemConfigKey.USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS.name()
                )
                .map(config -> config.getConfigValue())
                .filter(StringUtils::hasText)
                .orElse(DEFAULT_USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS);

        boolean isValid = password.length() >= minLength && password.length() <= maxLength;

        if (isValid && requireUppercase) {
            isValid = password.chars().anyMatch(Character::isUpperCase);
        }

        if (isValid && requireNumber) {
            isValid = password.chars().anyMatch(Character::isDigit);
        }

        if (isValid && requireSymbol) {
            isValid = password.chars()
                    .mapToObj(ch -> (char) ch)
                    .anyMatch(ch -> allowedSymbols.indexOf(ch) >= 0);
        }

        // Backward compatibility for legacy regex-only policy values.
        if (!hasStructuredPolicy) {
            String passwordRegex = systemConfigService.getConfig(
                            SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIREMENT.name()
                    )
                    .map(config -> config.getConfigValue())
                    .filter(StringUtils::hasText)
                    .orElse(DEFAULT_USER_SIGNUP_PASSWORD_REQUIREMENT);

            isValid = Pattern.compile(passwordRegex).matcher(password).matches();
        }

        if (!isValid) {
            throw new UserInvalidPasswordPolicyException();
        }
    }

    private int getIntConfigValue(String key, int defaultValue) {
        String value = systemConfigService.getConfig(key)
                .map(config -> config.getConfigValue())
                .orElse(String.valueOf(defaultValue));

        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        }
        catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }
}
