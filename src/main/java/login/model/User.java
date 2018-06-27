package login.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Document(collection = "users")
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String username;
	private String password;
	private Boolean enabled;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
	    this.password = password;
    }

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
	    this.username = username;
    }

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
	    this.enabled = enabled;
    }
}