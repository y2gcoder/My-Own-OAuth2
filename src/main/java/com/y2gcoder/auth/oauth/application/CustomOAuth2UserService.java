package com.y2gcoder.auth.oauth.application;

import com.y2gcoder.auth.oauth.application.dto.CustomOAuth2UserDetails;
import com.y2gcoder.auth.oauth.application.dto.OAuth2UserInfo;
import com.y2gcoder.auth.oauth.domain.OAuth2Authentication;
import com.y2gcoder.auth.oauth.domain.OAuth2AuthenticationId;
import com.y2gcoder.auth.user.application.UserRepository;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2AuthenticationRepository oAuth2AuthenticationRepository;
    private final UserRepository userRepository; // TODO 바운디드 컨텍스트를 위해 리팩토링 필요


    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfoBy(registrationId,
                oAuth2User.getAttributes());
        validateOAuth2UserInfo(oAuth2UserInfo);

        //OAuth2 인증 정보 있는지 확인
        Optional<OAuth2Authentication> oAuth2AuthenticationOptional
                = oAuth2AuthenticationRepository.findByProviderId(oAuth2UserInfo.getId());

        LocalDateTime now = LocalDateTime.now();

        //OAuth2 인증 정보가 없음(미가입자 || ID/PW 가입자)
        if (oAuth2AuthenticationOptional.isEmpty()) {
            String email = oAuth2UserInfo.getEmail();

            checkAlreadyRegisteredUserBy(email);

            // 신규 가입
            User user = createUser(oAuth2UserInfo);

            createOAuth2Authentication(oAuth2UserInfo, now, user);

            return CustomOAuth2UserDetails.create(user, oAuth2UserInfo.getAttributes());
        }

        //OAuth 인증 정보가 존재함(OAuth2 가입자)
        OAuth2Authentication oAuth2Authentication = oAuth2AuthenticationOptional.get();
        UserId ownerId = oAuth2Authentication.getOwnerId();
        // 유저 정보 조회
        Optional<User> userOptional = userRepository.findById(ownerId);
        // 해당 회원이 존재하지 않음(특수한 경우) - OAuth2Authentication 삭제 후 재생성, User 생성
        if (userOptional.isEmpty()) {
            oAuth2AuthenticationRepository.delete(oAuth2Authentication);

            // 신규 가입
            User user = createUser(oAuth2UserInfo);

            createOAuth2Authentication(oAuth2UserInfo, now, user);
            return CustomOAuth2UserDetails.create(user, oAuth2UserInfo.getAttributes());
        }

        //회원이 존재함
        User user = userOptional.get();
        if (user.isDeleted()) {
            throw new DeletedUserException();
        }

        return CustomOAuth2UserDetails.create(user, oAuth2UserInfo.getAttributes());
    }

    private void validateOAuth2UserInfo(OAuth2UserInfo oAuth2UserInfo) {
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new EmailNotFoundException();
        }
        if (!StringUtils.hasText(oAuth2UserInfo.getId())) {
            throw new ProviderIdNotFoundException();
        }
    }

    private void checkAlreadyRegisteredUserBy(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isDeleted()) { //삭제된 회원
                throw new DeletedUserException();
            }
            //이미 가입한 회원이 존재함.
            throw new AlreadyRegisteredUserException(user.getEmail());
        });
    }

    private User createUser(OAuth2UserInfo oAuth2UserInfo) {
        UserId userId = userRepository.nextUserId();
        return userRepository.save(new User(
                userId,
                oAuth2UserInfo.getEmail(),
                "",
                oAuth2UserInfo.getName(),
                null,
                oAuth2UserInfo.getProfileImageUrl()
        ));
    }

    private void createOAuth2Authentication(OAuth2UserInfo oAuth2UserInfo, LocalDateTime now,
            User user) {
        OAuth2AuthenticationId oAuth2AuthenticationId = oAuth2AuthenticationRepository
                .nextOAuth2AuthenticationId();
        oAuth2AuthenticationRepository.save(new OAuth2Authentication(
                oAuth2AuthenticationId,
                user.getId(),
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getId(),
                now
        ));
    }
}
