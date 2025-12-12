package com.uppcl.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppcl.model.ReportRequest;

public interface ReportRequestRepository extends JpaRepository<ReportRequest, Integer> {

	@Query(value="select * from recon.report_request\n"
			+ "where request_status = 'PENDING'\n"
			+ "order by id asc limit 1", nativeQuery=true)
	Optional<ReportRequest> findByRequestStatus();
}
