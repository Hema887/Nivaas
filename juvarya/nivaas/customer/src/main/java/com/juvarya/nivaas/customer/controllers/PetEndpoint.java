package com.juvarya.nivaas.customer.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.customer.dto.PetDTO;
import com.juvarya.nivaas.customer.model.PetModel;
import com.juvarya.nivaas.customer.model.constants.PetType;
import com.juvarya.nivaas.customer.populator.PetPopulator;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.PetService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/jtpet")
@Slf4j
public class PetEndpoint extends JTBaseEndpoint {

	private static final String USER = "ROLE_USER";

	@Autowired
	private PetPopulator petPopulator;

	@Autowired
	private PetService petService;

	@SuppressWarnings("unchecked")
	@PostMapping("/save")
	public ResponseEntity save(@Valid @RequestBody PetDTO petDto)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering save API with JTPetDTO: {}", petDto);
		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
		if (ObjectUtils.isEmpty(user)) {
			return ResponseEntity.badRequest().body(new MessageResponse("user should login to add pet"));
		}

		PetModel petModel = new PetModel();
		petModel.setBreed(petDto.getBreed());
		petModel.setColour(petDto.getColour());
		petModel.setNickName(petDto.getNickName());
		petModel.setCustomerId(user.getId());
		petModel.setPetType(petType(petDto.getPetType()));

		petModel = petService.addPet(petModel);
		PetDTO petDTO = (PetDTO) getConverterInstance().convert(petModel);
		log.info("Pet saved successfully with id: {}", petDTO.getId());
		return ResponseEntity.ok().body(petDTO);

	}

	@SuppressWarnings("unchecked")
	@GetMapping("/{petid}")
	public ResponseEntity<?> getPetDetails(@PathVariable("petid") Long petid)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering getPetDetails API with petid: {}", petid);
		Optional<PetModel> petModelOptional = petService.findById(petid);

		if (petModelOptional.isPresent()) {
			PetModel petModel = petModelOptional.get();
			PetDTO petDTO = (PetDTO) getConverterInstance().convert(petModel);
			return ResponseEntity.ok().body(petDTO);
		} else {
			log.warn("Pet not found with id: {}", petid);
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity removePet(@Valid @RequestParam Long petId) {
		log.info("Entering removePet API with petId: {}", petId);

		Optional<PetModel> jtPetModel = petService.findById(petId);
		if (null != jtPetModel) {
			petService.remove(petId);
			log.info("Pet details removed successfully with id: {}", petId);
			return ResponseEntity.ok().body(new MessageResponse("pet details removed "));
		}
		log.warn("Pet not found with id: {}", petId);
		return ResponseEntity.badRequest().body(new MessageResponse("Pet Not Found With Given Id"));

	}

	@SuppressWarnings("unchecked")
	@GetMapping("/list")
	public ResponseEntity getAll(@Valid @RequestParam int pageNo, int pageSize, String petType)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering getAll API with pageNo: {}, pageSize: {}, petType: {}", pageNo, pageSize, petType);
		Pageable pageable = (Pageable) PageRequest.of(pageNo, pageSize);
		Page<PetModel> petModel = petService.getAll(pageable);

		Map<String, Object> response = new HashMap<>();

		response.put(NivaasConstants.TOTAL_PAGES, petModel.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);

		if (!CollectionUtils.isEmpty(petModel.getContent())) {
			response.put(NivaasConstants.PROFILES, getConverterInstance().convertAll(petModel.getContent()));
			response.put(NivaasConstants.CURRENT_PAGE, petModel.getNumber());
			response.put(NivaasConstants.TOTAL_ITEMS, petModel.getTotalElements());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@PutMapping("/update")
	public ResponseEntity update(@Valid @RequestBody PetDTO dto)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering update API with JTPetDTO: {}", dto);
		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();

		if (null == dto.getId()) {
			return ResponseEntity.ok().body(new MessageResponse("petId shouldn't be null"));
		}
		Optional<PetModel> petModel = petService.findById(dto.getId());
		if (petModel.isEmpty()) {
			log.warn("Pet not found with id: {}", dto.getId());
			return ResponseEntity.ok().body(new MessageResponse("pet not found"));
		}
		if (user.getId().equals(petModel.get().getCustomerId())) {
			petModel.get().setBreed(dto.getBreed());
			petModel.get().setColour(dto.getColour());
			petModel.get().setNickName(dto.getNickName());
			petModel.get().setPetType(petType(dto.getPetType()));

			PetModel jtpetModel = petService.addPet(petModel.get());
			PetDTO updatedPetDTO = (PetDTO) getConverterInstance().convert(jtpetModel);
			log.info("Pet details updated successfully with id: {}", updatedPetDTO.getId());
			return ResponseEntity.ok().body(updatedPetDTO);
		}
		log.warn("User not allowed to update this petDetails");
		return ResponseEntity.ok().body(new MessageResponse("You are not allowed to update this petDetails"));
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(petPopulator, PetDTO.class.getName());
	}

	public PetType petType(String type) {
		if (type.equals(PetType.DOG.toString())) {
			return PetType.DOG;
		} else if (type.equals(PetType.CAT.toString())) {
			return PetType.CAT;
		} else {
			return PetType.BIRD;

		}
	}
}
