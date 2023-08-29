package shop.petmily.domain.petsitter.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PetsitterPostRequestDto {
    @NotBlank
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-zA-Z]).{8,}", message = "영어와 숫자를 최소 1개 포함하여 8자 이상이어야합니다.")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,}$", message = "2자 이상부터 가능하며 한글만 가능합니다.")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z가-헿0-9]{4,}$", message = "4자 이상부터 가능하며 특수 문자가 없어야 합니다.")
    private String displayName;

    @NotBlank
    @Pattern(regexp = "^010\\d{4}\\d{4}$", message = "'010'으로 시작해야 하며 '-'를 제외한 총 11자리 숫자여야 합니다.")
    private String phone;

    private String address;

    private String possiblePetType;

    private String possibleDay;

    private String possibleTimeStart;

    private String possibleTimeEnd;

    private String possibleLocation;
}
