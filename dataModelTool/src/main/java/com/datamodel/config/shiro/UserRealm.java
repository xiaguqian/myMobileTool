package com.datamodel.config.shiro;

import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.entity.UserEntity;
import com.datamodel.repository.UserRepository;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

@Component
public class UserRealm extends AuthorizingRealm {

    private final UserRepository userRepository;

    public UserRealm(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();

        try {
            DataSourceContextHolder.switchToPrimary();
            
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UnknownAccountException("用户不存在"));

            if (user.getStatus() != 1) {
                throw new LockedAccountException("账号已被禁用");
            }

            return new SimpleAuthenticationInfo(
                    user,
                    user.getPassword(),
                    ByteSource.Util.bytes(user.getSalt()),
                    getName()
            );
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }
}
