package jp.co.solxyz.jsn.springbootadvincedexam.security;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.user.UserAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ユーザアカウントサービス
 */
@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;

    /**
     *  ユーザ名を指定してユーザ情報を取得する
     * @param username ユーザ名
     * @return ユーザ情報
     * @throws UsernameNotFoundException ユーザが見つからない場合
     */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserDetails(userAccount);
    }
}
