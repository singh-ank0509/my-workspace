package com.uppcl.ewallet.pnb.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uppcl.ewallet.pnb.model.PnbTransaction;

@Repository
public interface PnbTransactionRepository extends JpaRepository<PnbTransaction, Long> {

	Optional<PnbTransaction> findByTransactionId(String transactionId);

	Optional<PnbTransaction> findByAgencyAccountNumberAndUppclAccountNumberAndTransactionDate(
			String agencyAccountNumber, String uppclAccountNumber, String date);
}
