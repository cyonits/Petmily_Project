package shop.petmily.domain.pet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.petmily.domain.pet.dto.PetPatchDto;
import shop.petmily.domain.pet.dto.PetPostDto;
import shop.petmily.domain.pet.entity.Pet;
import shop.petmily.domain.pet.mapper.PetMapper;
import shop.petmily.domain.pet.service.PetService;
import shop.petmily.global.argu.LoginMemberId;

import javax.validation.constraints.Positive;
import java.io.IOException;

@RequestMapping("/pets")
@RestController
public class PetController {
    private final PetMapper mapper;
    private final PetService service;

    public PetController(PetMapper mapper, PetService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity postPet(@RequestPart PetPostDto petPostDto,
                                  @RequestPart(required = false) MultipartFile file,
                                  @LoginMemberId Long memberId) throws IOException {
        petPostDto.setMemberId(memberId);
        Pet pet = service.createPet(mapper.PetPostDtoToPet(petPostDto), file);
        return new ResponseEntity(mapper.PetToPetResponseDto(pet), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{pet_id}",
                  consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity patchPet(@PathVariable ("pet_id") @Positive long petId,
                                   @RequestPart(required = false) PetPatchDto petPatchDto,
                                   @RequestPart(required = false) MultipartFile file,
                                   @LoginMemberId Long memberId) throws IOException {
        petPatchDto = (petPatchDto == null) ? new PetPatchDto() : petPatchDto;
        petPatchDto.setMemberId(memberId);
        petPatchDto.setPetId(petId);

        Pet pet = service.updatePet(mapper.PetPatchDtoToPet(petPatchDto), file);
        return new ResponseEntity(mapper.PetToPetResponseDto(pet), HttpStatus.OK);
    }

    @PatchMapping("/{pet_id}/photo")
    public ResponseEntity photoDeletePet(@PathVariable ("pet_id") @Positive long petId,
                                         @LoginMemberId Long memberId) throws IOException {
        Pet pet = service.photoDelete(petId, memberId);
        return new ResponseEntity(mapper.PetToPetResponseDto(pet), HttpStatus.OK);
    }

    @GetMapping("/{pet_id}")
    public ResponseEntity findPet(@PathVariable ("pet_id") @Positive long petId){
        Pet pet = service.findPet(petId);
        return new ResponseEntity(mapper.PetToPetResponseDto(pet), HttpStatus.OK);
    }


    @DeleteMapping("/{pet_id}")
    public HttpStatus deletePet(@PathVariable ("pet_id") @Positive long petId,
                                @LoginMemberId Long memberId){
        service.deletePet(petId, memberId);
        return HttpStatus.NO_CONTENT;
    }
}
