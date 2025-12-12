package com.uppcl.ewallet.pnb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uppcl.ewallet.pnb.model.Agency;

public interface AgencyRepository extends JpaRepository<Agency, Long> {

//	List<Agency> findAll();
}
