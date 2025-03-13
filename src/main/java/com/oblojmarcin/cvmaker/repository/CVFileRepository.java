package com.oblojmarcin.cvmaker.repository;

import com.oblojmarcin.cvmaker.entity.CVFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CVFileRepository extends JpaRepository<CVFile, Integer> {
    List<CVFile> findByUserUserId(int userId);
}
