package shop.petmily.domain.member.controller;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import shop.petmily.domain.member.dto.*;
import shop.petmily.domain.member.entity.Member;
import shop.petmily.domain.member.entity.Petsitter;
import shop.petmily.domain.member.mapper.MemberMapper;
import shop.petmily.domain.member.mapper.PetsitterMapper;
import shop.petmily.domain.member.repository.PetsitterRepository;
import shop.petmily.domain.member.service.MemberService;
import shop.petmily.domain.member.service.PetsitterService;
import shop.petmily.domain.pet.entity.Pet;
import shop.petmily.global.argu.LoginMemberId;
import shop.petmily.global.dto.LoginMemberResponseDto;
import shop.petmily.global.dto.SingleResponseDto;
import shop.petmily.global.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Validated
@Slf4j
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/members";
    private final MemberService memberService;
    private final PetsitterService petsitterService;
    private final MemberMapper memberMapper;
    private final PetsitterMapper petsitterMapper;

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberPostRequestDto requestBody) {
        Member member = memberService.createMember(memberMapper.memberPostDtoToMember(requestBody));
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());
        return ResponseEntity.created(location).body(new SingleResponseDto<>("success create member"));
    }

    @PatchMapping(value = "/{member-id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity patchMember(@PathVariable("member-id") @Positive long memberId,
                                      @LoginMemberId Long loginMemberId,
                                      @RequestPart(required = false) MemberPatchRequestDto requestBody,
                                      @RequestPart(required = false) MultipartFile file) throws IOException {
        Member findMember = memberService.findMember(memberId);
        memberService.verifyAuthority(findMember, loginMemberId);

        requestBody.setMemberId(findMember.getMemberId());
        memberService.updateMember(memberMapper.memberPatchDtoToMember(requestBody), file);

        return new ResponseEntity<>(new SingleResponseDto<>("success modify member"), HttpStatus.OK);
    }

    @PatchMapping("/{member-id}/photo")
    public ResponseEntity photoDeletePet(@PathVariable ("member-id") @Positive long memberId,
                                         @LoginMemberId Long loginMemberId) throws IOException {
        Member findMember = memberService.findMember(memberId);
        memberService.verifyAuthority(findMember, loginMemberId);
        memberService.photoDelete(findMember.getMemberId());
        return new ResponseEntity(new SingleResponseDto<>("success delete photo"), HttpStatus.OK);
    }

    @PatchMapping("/petsitters/{member-id}")
    public ResponseEntity patchPetsitterPossible(@PathVariable("member-id") @Positive long memberId,
                                      @LoginMemberId Long loginMemberId,
                                      @Valid @RequestBody PetsitterPatchRequestDto requestBody) {
        Member findMember = memberService.findMember(memberId);
        memberService.verifyAuthority(findMember, loginMemberId);

        Petsitter findPetsitter = petsitterService.findPetsitter(findMember);
        requestBody.setPetsitterId(findPetsitter.getPetsitterId());
        petsitterService.updatePetsitter(petsitterMapper.petstitterPatchDtoToPetsitter(requestBody));

        return new ResponseEntity<>(new SingleResponseDto<>("success modify member"), HttpStatus.OK);
    }

    @GetMapping("/petsitters")
    public ResponseEntity<PetsitterPossibleResoponseDto> getPetsitterPossible(@LoginMemberId Long loginMemberId) {
        Member findMember = memberService.findMember(loginMemberId);
        Petsitter findPetsitter = petsitterService.findPetsitter(findMember);
        PetsitterPossibleResoponseDto petsitterPossibleResoponseDto = petsitterService.findPossible(findPetsitter);
        return new ResponseEntity<>(petsitterPossibleResoponseDto, HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/my-page")
    public ResponseEntity<MemberGetResponseDto> getMember(@LoginMemberId Long loginMemberId) {
        MemberGetResponseDto memberGetResponseDto = memberService.findProfileMember(loginMemberId);

        return new ResponseEntity<>(memberGetResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") @Positive long memberId, @LoginMemberId Long loginMemberId) {
        Member findMember = memberService.findMember(memberId);
        memberService.verifyAuthority(findMember, loginMemberId);
        memberService.removeMember(findMember.getMemberId());
        return new ResponseEntity<>(new SingleResponseDto<>("success delete member"), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping("/user")
    public ResponseEntity accountUserDetails(@LoginMemberId Long loginMemberId) {
        Member findMember = memberService.findMember(loginMemberId);

        return new ResponseEntity<>(new LoginMemberResponseDto(memberMapper.memberToLoginMemberResponseDto(findMember)), HttpStatus.OK);
    }
}