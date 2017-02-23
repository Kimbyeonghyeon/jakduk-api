package com.jakduk.api.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.jakduk.api.common.vo.SocialProfile;
import com.jakduk.api.configuration.authentication.user.CommonPrincipal;
import com.jakduk.api.configuration.authentication.user.JakdukUserDetail;
import com.jakduk.api.configuration.authentication.user.SocialUserDetail;
import com.jakduk.core.common.CommonRole;
import com.jakduk.core.common.CoreConst;
import com.jakduk.core.model.db.User;
import com.jakduk.core.model.embedded.CommonWriter;
import com.jakduk.core.model.etc.AuthUserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pyohwan
 *         16. 7. 31 오후 9:04
 */

@Component
public class UserUtils {

    @Value("${api.server.url}")
    private String apiServerUrl;

    private final String DAUM_PROFILE_API_URL = "https://apis.daum.net/user/v1/show.json";
    private final String FACEBOOK_PROFILE_API_URL = "https://graph.facebook.com/v2.7/me?fields=id,name,email&format=json";

    public SocialProfile getDaumProfile(String accessToken) {

        JsonNode jsonNode = fetchProfile(DAUM_PROFILE_API_URL, accessToken);

        JsonNode resultJson = jsonNode.get("result");
        SocialProfile profile = new SocialProfile();
        profile.setId(resultJson.get("userid").asText());
        profile.setNickname(resultJson.get("nickname").asText());

        return profile;
    }

    public SocialProfile getFacebookProfile(String accessToken) {

        JsonNode jsonNode = fetchProfile(FACEBOOK_PROFILE_API_URL, accessToken);

        SocialProfile profile = new SocialProfile();
        profile.setId(jsonNode.get("id").asText());
        profile.setNickname(jsonNode.get("name").asText());
        profile.setEmail(jsonNode.get("email").asText());

        return profile;
    }

    /**
     * 손님인지 검사.
     */
    public static Boolean isAnonymousUser() {
        Boolean result = false;

        if (! SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
            result = true;

        if (! result) {
            Collection<? extends GrantedAuthority> authorises = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

            for (GrantedAuthority authority : authorises) {
                if ("ROLE_ANONYMOUS".equals(authority.getAuthority())) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 로그인 중인 회원이 관리자인지 검사.
     * @return 관리자 이면 true
     */
    public static Boolean isAdmin() {
        Boolean result = false;

        if (! SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
            return false;

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ROOT")) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * 로그인 중인 회원이 USER 권한인지 검사.
     * @return 회원이면 true
     */
    public static Boolean isUser() {
        Boolean result = false;

        if (! SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
            return false;

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            if (authority.equals("ROLE_USER_01") || authority.equals("ROLE_USER_02") || authority.equals("ROLE_USER_03")) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * 로그인 중인 회원이 이메일 기반인지 검사.
     * @return 이메일 기반이면 true, 아니면 false
     */
    public static Boolean isJakdukUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof JakdukUserDetail;
    }

    /**
     * 로그인 중인 회원이 소셜 기반인지 검사.
     * @return 이메일 기반이면 true, 아니면 false
     */
    public static Boolean isSocialUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SocialUserDetail;
    }

    /**
     * 로그인 중인 회원 정보를 가져온다.
     * @return 회원 객체
     */
    public static AuthUserProfile getAuthUserProfile() {
        AuthUserProfile authUserProfile = null;

        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SocialUserDetail) {
                SocialUserDetail userDetail = (SocialUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

                List<String> roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                authUserProfile = AuthUserProfile.builder()
                        .id(userDetail.getId())
                        .email(userDetail.getUserId())
                        .username(userDetail.getUsername())
                        .providerId(userDetail.getProviderId())
                        .imageUrl(userDetail.getImageUrl())
                        .roles(roles)
                        .build();

            } else if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof JakdukUserDetail) {
                JakdukUserDetail userDetail = (JakdukUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

                List<String> roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                authUserProfile = AuthUserProfile.builder()
                        .id(userDetail.getId())
                        .email(userDetail.getUsername())
                        .username(userDetail.getNickname())
                        .providerId(userDetail.getProviderId())
                        .imageUrl(userDetail.getImageUrl())
                        .roles(roles)
                        .build();
            }
        }

        return authUserProfile;
    }

    /**
     * 로그인 중인 회원의 정보를 가져온다.
     * @return 로그인 회원 객체.
     */
    public static CommonPrincipal getCommonPrincipal() {
        CommonPrincipal commonPrincipal = null;

        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SocialUserDetail) {
                SocialUserDetail userDetail = (SocialUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                commonPrincipal = CommonPrincipal.builder()
                        .id(userDetail.getId())
                        .email(userDetail.getUserId())
                        .username(userDetail.getUsername())
                        .providerId(userDetail.getProviderId())
                        .build();
            } else if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof JakdukUserDetail) {
                JakdukUserDetail principal = (JakdukUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                commonPrincipal = CommonPrincipal.builder()
                        .id(principal.getId())
                        .email(principal.getUsername())
                        .username(principal.getNickname())
                        .providerId(principal.getProviderId())
                        .build();
            }
        }

        return commonPrincipal;
    }

    /**
     * CommonWriter를 가져온다.
     */
    public static CommonWriter getCommonWriter() {
        CommonPrincipal commonPrincipal = getCommonPrincipal();

        if (! ObjectUtils.isEmpty(commonPrincipal)) {
            return new CommonWriter(commonPrincipal.getId(), commonPrincipal.getUsername(), commonPrincipal.getProviderId());
        } else {
            return null;
        }
    }

    /**
     * 이메일 기반 회원의 로그인 처리
     */
    public JakdukUserDetail signInJakdukUser(String id, String email, String password, String username,
                                             CoreConst.ACCOUNT_TYPE providerId, String imageUrl, List<Integer> roles) {

        JakdukUserDetail jakdukUserDetail = new JakdukUserDetail(email, id, password, username, providerId, imageUrl,
                true, true, true, true, getAuthorities(roles));

        Authentication authentication = new UsernamePasswordAuthenticationToken(jakdukUserDetail, password, jakdukUserDetail.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jakdukUserDetail;
    }

    /**
     * SNS 기반 회원의 로그인 처리
     */
    public void signInSocialUser(String id, String email, String username, CoreConst.ACCOUNT_TYPE providerId, String providerUserId,
                                 String imageUrl, List<Integer> roles) {

        SocialUserDetail userDetail = new SocialUserDetail(id, email, username, providerId, providerUserId, imageUrl,
                true, true, true, true, getAuthorities(roles));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 회원 프로필 이미지 URL을 생성한다.
     *
     * @param id UserImage의 ID
     */
    public String generateUserImageUrl(String id) {

        if (StringUtils.isEmpty(id))
            return null;

        return String.format("%s/user/image/%s", apiServerUrl, id);
    }

    public static Collection<? extends GrantedAuthority> getAuthorities(List<Integer> roles) {
        return getGrantedAuthorities(getRoles(roles));
    }

    /**
     * accessToken에 해당하는 프로필 정보를 가져온다.
     * @param url 요청할 URL
     * @param accessToken accessToken
     */
    private JsonNode fetchProfile(String url, String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        return responseEntity.getBody();
    }

    private static List<String> getRoles(List<Integer> roles) {
        List<String> newRoles = new ArrayList<>();

        if (roles != null) {
            for (Integer roleNumber : roles) {
                String roleName = CommonRole.getRoleName(roleNumber);
                if (!roleName.isEmpty()) {
                    newRoles.add(roleName);
                }
            }
        }

        return newRoles;
    }

    private static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }

}

