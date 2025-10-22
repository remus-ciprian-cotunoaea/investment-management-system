// src/main/java/com/investment/users/dto/UserRequestDto.java
package com.investment.users.dto;

import com.investment.users.utils.Constants;
import com.investment.users.utils.enums.UserStatusEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO used to create or update user information in API requests.
 *
 * <p>This request DTO contains the minimal set of fields accepted from clients.
 * Validation annotations are applied to protect the service from invalid input.
 * Mapping from this DTO to the domain entity is performed by service or mapper
 * implementations.</p>
 *
 * <ul>
 *   <li>name: required, max length 100</li>
 *   <li>email: required, must be a valid email address</li>
 *   <li>status: optional, represents the user's state</li>
 * </ul>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank
    @Size(max = Constants.HUNDRED)
    private String name;

    @NotBlank
    @Email
    private String email;

    private UserStatusEnum status;
}