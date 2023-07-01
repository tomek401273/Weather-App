package com.tgrajkowski.service.repository;

import com.tgrajkowski.model.entity.RequestParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestParametersRepository extends JpaRepository<RequestParameters, Long> {
}
