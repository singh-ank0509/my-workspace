package com.uppcl.ewallet.pnb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "agency_details", uniqueConstraints = { @UniqueConstraint(columnNames = {"agency_id", "agency_van"}) })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agency {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Column(name = "agency_id", unique = true, nullable = false)
	private String agencyId;
    
    @Column(name = "agency_van", unique = true, nullable = false)
	private String agencyVan;
    
    @Column(name = "agency_account_number", length = 100)
	private String agencyAccountNumber;
    
    @Column(name = "uppcl_account_number", length = 100)
	private String uppclAccountNumber;
}
