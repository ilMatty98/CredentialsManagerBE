package com.credentialsmanager.controller;

import com.credentialsmanager.dto.ChangeEmailDto;
import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.validator.ChangePasswordValidator;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.credentialsmanager.constants.UrlConstants.*;

@RestController
@Tag(name = "Authentication")
@RequestMapping(BASE_PATH)
public interface AuthenticationController {

    @PostMapping(SIGN_UP)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SignUp successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    ResponseEntity<Object> signUp(@Parameter(description = "Dto to create a new user")
                                  @Valid @RequestBody SignUpDto signUpDto);

    @PostMapping(LOG_IN)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LogIn successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    LogInDto.Response logIn(@Parameter(description = "Dto to log in")
                            @Valid @RequestBody LogInDto.Request requestLogInDto);

    @GetMapping(CHECK_EMAIL)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checking on email done"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    boolean checkEmail(@RequestHeader("checkEmail") String email);

    @PatchMapping(CONFIRM_EMAIL)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    ResponseEntity<Object> confirmEmail(@PathVariable String email, @PathVariable String code);

    @PostMapping(CHANGE_PASSWORD)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    ResponseEntity<Object> changePassword(@Parameter(description = "Dto to change password")
                                          @Validated(ChangePasswordValidator.class) @RequestBody SignUpDto signUpDto,
                                          HttpServletRequest request);

    @PostMapping(CHANGE_EMAIL)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    ResponseEntity<Object> changeEmail(@Parameter(description = "Dto to change email")
                                       @Valid @RequestBody ChangeEmailDto changeEmailDto,
                                       HttpServletRequest request);

    @PostMapping(CHANGE_LANGUAGE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    ResponseEntity<Object> changeLanguage(@Parameter(description = "String to change language")
                                          @RequestBody String language,
                                          HttpServletRequest request);

    @PostMapping(CHANGE_HINT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hint successfully changed"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    ResponseEntity<Object> changeHint(@Parameter(description = "String to change hint")
                                      @RequestBody String hint,
                                      HttpServletRequest request);
}
