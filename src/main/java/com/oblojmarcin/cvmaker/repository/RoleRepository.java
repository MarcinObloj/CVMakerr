package com.oblojmarcin.cvmaker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.oblojmarcin.cvmaker.entity.Roles;


@Repository
public interface RoleRepository extends JpaRepository<Roles,Integer> {
}
