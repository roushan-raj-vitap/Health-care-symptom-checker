package com.example.symptom.model;

import java.util.function.Function;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
//import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name="user_name")
    private String username;
    
    @ManyToMany(cascade = CascadeType.ALL,fetch =FetchType.EAGER)
    @JoinTable(name="user_role",
    joinColumns=@JoinColumn(name="user_id"),
    inverseJoinColumns = @JoinColumn(name="role_id"))
//    @JsonManagedReference no need as I used user DTO 
    private List<Role> role;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		Function<Role,GrantedAuthority> fun =(r)-> {
			return new SimpleGrantedAuthority("ROLE_"+r.getName());
		};
		return role.stream().map(r->fun.apply(r)).collect(Collectors.toList());
//		return role.stream().map(r->new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
	}
	@Override
	public String getUsername() {
	    return email;
	}

	@Override
	public String getPassword() {
	    return password;
	}

	@Override
	public boolean isAccountNonExpired() {
	    return true; // we can add a field like `accountExpired` if needed
	}

	@Override
	public boolean isAccountNonLocked() {
	    return true; // Add a field like `accountLocked` if we want dynamic control
	}

	@Override
	public boolean isCredentialsNonExpired() {
	    return true; // Add a field like `credentialsExpired` if needed
	}

	@Override
	public boolean isEnabled() {
	    return true; // Add a field like `enabled` if we want to toggle user status
	}
}