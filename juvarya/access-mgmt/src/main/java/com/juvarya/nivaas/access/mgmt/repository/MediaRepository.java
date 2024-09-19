package com.juvarya.nivaas.access.mgmt.repository;

import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<MediaModel, Long> {

	MediaModel findByCustomerIdAndMediaType(Long userId, String mediaType);

}
