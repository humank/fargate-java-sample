/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;

/**
 * @author Vitaliy Fedoriv
 *
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api/pets")
public class PetRestController {

	@Autowired
	private ClinicService clinicService;

	@RequestMapping(value = "/{petId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Pet> getPet(@PathVariable("petId") int petId){
		Pet pet = this.clinicService.findPetById(petId);
		if(pet == null){
			return new ResponseEntity<Pet>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Pet>(pet, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<Pet>> getPets(){
		Collection<Pet> pets = this.clinicService.findAllPets();
		if(pets.isEmpty()){
			return new ResponseEntity<Collection<Pet>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<Pet>>(pets, HttpStatus.OK);
	}

	@RequestMapping(value = "/pettypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<PetType>> getPetTypes(){
		return new ResponseEntity<Collection<PetType>>(this.clinicService.findPetTypes(), HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Pet> addPet(@RequestBody @Valid Pet pet, BindingResult bindingResult, UriComponentsBuilder ucBuilder){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
		if(bindingResult.hasErrors() || (pet == null)){
			errors.addAllErrors(bindingResult);
			headers.add("errors", errors.toJSON());
			return new ResponseEntity<Pet>(headers, HttpStatus.BAD_REQUEST);
		}
		this.clinicService.savePet(pet);
		headers.setLocation(ucBuilder.path("/api/pets/{id}").buildAndExpand(pet.getId()).toUri());
		return new ResponseEntity<Pet>(pet, headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{petId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Pet> updatePet(@PathVariable("petId") int petId, @RequestBody @Valid Pet pet, BindingResult bindingResult){
		BindingErrorsResponse errors = new BindingErrorsResponse();
		HttpHeaders headers = new HttpHeaders();
		if(bindingResult.hasErrors() || (pet == null)){
			errors.addAllErrors(bindingResult);
			headers.add("errors", errors.toJSON());
			return new ResponseEntity<Pet>(headers, HttpStatus.BAD_REQUEST);
		}
		Pet currentPet = this.clinicService.findPetById(petId);
		if(currentPet == null){
			return new ResponseEntity<Pet>(HttpStatus.NOT_FOUND);
		}
		currentPet.setBirthDate(pet.getBirthDate());
		currentPet.setName(pet.getName());
		currentPet.setType(pet.getType());
		currentPet.setOwner(pet.getOwner());
		this.clinicService.savePet(currentPet);
		return new ResponseEntity<Pet>(currentPet, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{petId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@Transactional
	public ResponseEntity<Void> deletePet(@PathVariable("petId") int petId){
		Pet pet = this.clinicService.findPetById(petId);
		if(pet == null){
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		this.clinicService.deletePet(pet);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}


}
