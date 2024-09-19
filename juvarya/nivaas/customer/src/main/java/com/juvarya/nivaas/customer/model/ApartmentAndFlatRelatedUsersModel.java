package com.juvarya.nivaas.customer.model;

import com.juvarya.nivaas.customer.model.constants.RelatedType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "APARTMENT_FLAT_RELATED_USERS", indexes = { @Index(name = "idx_related_users_id", columnList = "id", unique = true) })
@Getter
@Setter
public class ApartmentAndFlatRelatedUsersModel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "ONBOARD_REQUEST_ID")
    private Long onboardingRequestId;

    @Column(name = "USER_ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "RELATED_TYPE")
    @NotNull
    private RelatedType relatedType;

    @Column(name = "RELATED_USER_APPROVED")
    private boolean relatedUserApproved;
}
